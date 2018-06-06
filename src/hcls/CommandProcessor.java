// Copyright 2018 Daiki Yoshida
//
// This file is part of HyperCommandLineShell.
//
// HyperCommandLineShell is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// HyperCommandLineShell is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with HyperCommandLineShell. If not, see <http://www.gnu.org/licenses/>.

package hcls;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CommandProcessor {
	public static void commandProcess(String[] cmdarray, CurrentWorkingDirectory cwd) throws CommandLineException {
		try {
			switch(cmdarray[0]) {
			case "mkfile":
				command_mkfile(cwd.getAbsolutePath(new File(cmdarray[1])));
				break;

			case "rmfile":
				command_rmfile(cwd.getAbsolutePath(new File(cmdarray[1])));
				break;

			case "cpfile":
				command_cpfile(cwd.getAbsolutePath(new File(cmdarray[1])), cwd.getAbsolutePath(new File(cmdarray[2])));
				break;

			case "mkdir":
				command_mkdir(cwd.getAbsolutePath(new File(cmdarray[1])));
				break;

			case "rmdir":
				command_rmdir(cwd.getAbsolutePath(new File(cmdarray[1])));
				break;

			case "cpdir":
				command_cpdir(cwd.getAbsolutePath(new File(cmdarray[1])), cwd.getAbsolutePath(new File(cmdarray[2])));
				break;

			case "rename":
				command_rename(cwd.getAbsolutePath(new File(cmdarray[1])), cwd.getAbsolutePath(new File(cmdarray[2])));
				break;

			case "tview":
				command_tview(cwd.getAbsolutePath(new File(cmdarray[1])));
				break;

			case "bview":
				command_bview(cwd.getAbsolutePath(new File(cmdarray[1])));
				break;

			case "chcwd":
				command_chcwd(cwd, cwd.getAbsolutePath(new File(cmdarray[1])));
				break;

			case "pcwd":
				command_pcwd(cwd);
				break;

			case "exec":
				if(cmdarray.length == 1) {
					throw new CommandLineException("few args");
				}

				// softwareCmdArray = {cmdarray[1], ..., cmdarray[cmdarray.length - 1]}
				String[] softwareCmdArray = new String[cmdarray.length - 1];
				for(int i = 0; i < softwareCmdArray.length; ++i) {
					softwareCmdArray[i] = cmdarray[i + 1];
				}

				command_exec(cwd, softwareCmdArray);
				break;

			case "path":
				switch(cmdarray[1]) {
				case "add":
					command_path_add(new File(cmdarray[2]));
					break;

				case "del":
					command_path_del(Integer.parseInt(cmdarray[2]));
					break;

				case "clear":
					command_path_clear();
					break;

				case "list":
					command_path_list();
					break;

				default:
					throw new CommandLineException("unknown option of path command");
				}
				break;

			case "now":
				command_now();
				break;

			case "exit":
				System.exit(0);
				break;

			default:
				throw new CommandLineException("unknown command");
			}
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new CommandLineException("few args");
		} catch(NumberFormatException e) {
			throw new CommandLineException("invalid path setting number");
		}
	}

	private static void command_mkfile(File file) throws CommandLineException {
		try {
			if(!file.createNewFile()) {
				throw new CommandLineException("failed make a file");
			}
		} catch(IOException e) {
			throw new CommandLineException("I/O error");
		} catch(SecurityException e) {
			throw new CommandLineException("access denied");
		}
	}

	private static void command_rmfile(File file) throws CommandLineException {
		try {
			if(!file.delete()) {
				throw new CommandLineException("failed remove a file");
			}
		} catch(SecurityException e) {
			throw new CommandLineException("access denied");
		}
	}

	private static void command_cpfile(File source, File dest) throws CommandLineException {
		try {
			Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES, LinkOption.NOFOLLOW_LINKS);
		} catch(DirectoryNotEmptyException e) {
			throw new CommandLineException("it is a directory");
		} catch(IOException e) {
			throw new CommandLineException("I/O error");
		} catch(SecurityException e) {
			throw new CommandLineException("access denied");
		} catch(InvalidPathException e) {
			throw new CommandLineException("invalid filename");
		}
	}

	private static void command_mkdir(File dir) throws CommandLineException {
		try {
			if(!dir.mkdir()) {
				throw new CommandLineException("failed make a directory");
			}
		} catch(SecurityException e) {
			throw new CommandLineException("access denied");
		}
	}

	private static void command_rmdir(File dir) throws CommandLineException {
		if(!removeDir(dir)) {
			throw new CommandLineException("failed remove a directory");
		}
	}

	// helper of command_rmdir method
	private static boolean removeDir(File file) throws CommandLineException {
		try {
			if(!file.exists()) {
				return false;
			}

			if(file.isDirectory()) {
				for(File fileInTheDir: file.listFiles()) {
					if(!removeDir(fileInTheDir)) {
						return false;
					}
				}
				return file.delete();
			} else {
				return file.delete();
			}
		} catch(SecurityException e) {
			throw new CommandLineException("access denied");
		}
	}

	private static void command_cpdir(File source, File dest) throws CommandLineException {
		if(!copyDir(source, dest)) {
			throw new CommandLineException("failed copy a directory");
		}
	}

	// helper of command_cpdir method
	private static boolean copyDir(File source, File dest) throws CommandLineException {
		try {
			if(!source.exists()) {
				throw new CommandLineException("source do not exists");
			}

			if(!source.isDirectory()) {
				throw new CommandLineException("source is not directory");
			}

			if(dest.exists()) {
				removeDir(dest);
			}

			if(!dest.mkdir()) {
				return false;
			}

			for(File fileInSourceDir: source.listFiles()) {
				if(fileInSourceDir.isFile()) {
					Files.copy(fileInSourceDir.toPath(), new File(dest.toString() + '/' + fileInSourceDir.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES, LinkOption.NOFOLLOW_LINKS);
				} else {
					return copyDir(fileInSourceDir, new File(dest.toString() + '/' + fileInSourceDir.getName()));
				}
			}
		} catch(SecurityException e) {
			throw new CommandLineException("access denied");
		} catch(IOException e) {
			throw new CommandLineException("I/O error");
		}

		return true;
	}

	private static void command_rename(File source, File dest) throws CommandLineException {
		try {
			if(!source.renameTo(dest)) {
				throw new CommandLineException("failed rename a file");
			}
		} catch(SecurityException e) {
			throw new CommandLineException("access denied");
		}
	}

	private static void command_tview(File file) throws CommandLineException {
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
			String line;
			for(int i = 1; (line = reader.readLine()) != null; ++i) {
				System.out.println(i + ":\t" + line);
			}
		} catch(FileNotFoundException e) {
			throw new CommandLineException("file not found");
		} catch(IOException e) {
			throw new CommandLineException("I/O error");
		}
	}

	private static void command_bview(File file) throws CommandLineException {
		final int fileSizeMax = 1024 * 50; // 50kB
		try(BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file))) {
			byte[] bytes = new byte[fileSizeMax];
			int bytesNumber = stream.read(bytes);
			if(bytesNumber == -1) {
				return;
			}

			System.out.println("\t+0 +1 +2 +3 +4 +5 +6 +7 +8 +9 +A +B +C +D +E +F 0123456789ABCDEF");

			for(int i = 0; i < bytesNumber; i += 0x10) {
				System.out.print(Integer.toHexString(i).toUpperCase() + ":\t");

				int j;
				for(j = 0; j <= 0xf && i + j < bytesNumber; ++j) {
					int n = bytes[i + j];
					String s = (n < 0x10 ? "0" : "") + Integer.toHexString(n).toUpperCase();
					System.out.print(s + ' ');
				}

				if(j < 0x10) {
					for(int k = 1; k <= 0x10 - j; ++k) {
						for(int l = 1; l <= 3; ++l) {
							System.out.print(' ');
						}
					}
				}

				for(int k = 0; k < 0xf; ++k) {
					int c = bytes[i + k];
					System.out.print(Character.isISOControl(c) ? '.' : (char)c);
				}

				System.out.println();
			}
		} catch(FileNotFoundException e) {
			throw new CommandLineException("file not found");
		} catch(IOException e) {
			throw new CommandLineException("I/O error");
		}
	}

	private static void command_chcwd(CurrentWorkingDirectory cwd, File dir) {
		cwd.set(dir);
	}

	private static void command_pcwd(CurrentWorkingDirectory cwd) {
		System.out.println(cwd.toString());
	}

	private static void command_exec(CurrentWorkingDirectory cwd, String[] cmdarray) throws CommandLineException {
		try {
			cmdarray[0] = PathProcessor.pathProcess(new File(cmdarray[0]), cwd).toString();
			if(cmdarray[0] == null) {
				throw new CommandLineException("it do not exists");
			}

			ProcessBuilder pb = new ProcessBuilder(cmdarray);
			pb.directory(cwd.get());
			pb.inheritIO();

			pb.start().waitFor();
		} catch(SecurityException e) {
			throw new CommandLineException("access denied");
		} catch(IOException e) {
			throw new CommandLineException("I/O error");
		} catch(InterruptedException e) {}
	}

	private static void command_path_add(File dir) throws CommandLineException {
		try {
			PathProcessor.add(dir);
		} catch(IOException e) {
			throw new CommandLineException("I/O error");
		}
	}

	private static void command_path_del(int n) throws CommandLineException {
		try {
			PathProcessor.del(n);
		} catch(FileNotFoundException e) {
			throw new CommandLineException("path settings not found");
		} catch(IOException e) {
			throw new CommandLineException("I/O error");
		} catch(IndexOutOfBoundsException e) {
			throw new CommandLineException("invalid setting number");
		}
	}

	private static void command_path_clear() throws CommandLineException {
		try {
			PathProcessor.clear();
		} catch(FileNotFoundException e) {
			// Nothing
		} catch(IOException e) {
			throw new CommandLineException("I/O error");
		}
	}

	private static void command_path_list() {
		int i = 1;
		for(File path: PathProcessor.getPaths()) {
			System.out.println((i++) + ":\t" + path.toString());
		}
	}

	private static void command_now() {
		System.out.println(new Date().toString());
	}

	public static String[] splitCommandLine(String command) {
		return command.split(" ");
	}
}

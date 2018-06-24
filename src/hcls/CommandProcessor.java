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

			case "list":
				command_list(cwd.getAbsolutePath(new File(cmdarray[1])));
				break;

			case "version":
				command_version();
				break;

			case "script":
				try {
					script(cwd.getAbsolutePath(new File(cmdarray[1])), cwd);
				} catch(FileNotFoundException e) {
					throw new CommandLineException("file not found");
				} catch(IOException e) {
					throw new CommandLineException("I/O error");
				}
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
					if(!copyDir(fileInSourceDir, new File(dest.toString() + '/' + fileInSourceDir.getName()))) {
						return false;
					}
				}
			}

			return true;
		} catch(SecurityException e) {
			throw new CommandLineException("access denied");
		} catch(IOException e) {
			throw new CommandLineException("I/O error");
		}
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
		try(BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file))) {
			List<Integer> bytes = new ArrayList<Integer>();
			int b;
			while((b = stream.read()) != -1) {
				bytes.add(b);
			}

			if(bytes.isEmpty()) {
				return;
			}

			System.out.println("\t+0 +1 +2 +3 +4 +5 +6 +7 +8 +9 +A +B +C +D +E +F 0123456789ABCDEF");

			for(int i = 0; i < bytes.size(); i += 0x10) {
				System.out.print(Integer.toHexString(i).toUpperCase());
				System.out.print(":\t");

				int j = 0;
				for(; j <= 0xf && i + j < bytes.size(); ++j) {
					int b2 = bytes.get(i + j);

					if(Integer.compareUnsigned(b2, 0x10) < 0) {
						System.out.print('0');
					}

					System.out.print(Integer.toHexString(b2).toUpperCase());
					System.out.print(' ');
				}

				if(j < 0xf) {
					for(int k = 1; 0x10 - j >= k; ++k) {
						System.out.print("   "); // 3 spaces
					}
				}

				for(int k = 0; k <= 0xf && i + k < bytes.size(); ++k) {
					int c = bytes.get(i + k);
					System.out.print((Character.isISOControl(c)) ? '.' : (char)c);
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
			File file = PathProcessor.pathProcess(new File(cmdarray[0]), cwd);
			if(file == null) {
				throw new CommandLineException("it do not exists");
			}

			cmdarray[0] = file.toString();

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

	private static void command_path_list() throws CommandLineException {
		try {
			int i = 1;
			for(File path: PathProcessor.getPaths()) {
				System.out.println((i++) + ":\t" + path.toString());
			}
		} catch(FileNotFoundException e) {
			// Nothing
		} catch(IOException e) {
			throw new CommandLineException("I/O error");
		}
	}

	private static void command_now() {
		System.out.println(new Date().toString());
	}

	private static void command_list(File dir) throws CommandLineException {
		try {
			for(File fileInTheDir: dir.listFiles()) {
				System.out.println((fileInTheDir.isFile() ? "file" : "dir") + ":\t" + fileInTheDir.getName());
			}
		} catch(SecurityException e) {
			throw new CommandLineException("access denied");
		} catch(NullPointerException e) {
			throw new CommandLineException("it is not a directory");
		}
	}

	private static void command_version() {
		System.out.println(ShellDatas.version);
	}

	public static String[] splitCommandLine(String command) throws CommandLineException {
		final int commandLength = command.length();
		boolean quoted = false;
		String temp = "";
		List<String> tokens = new ArrayList<String>();
		boolean escaped = false;

		for(int i = 0; i < commandLength; ++i) {
			char c = command.charAt(i);
			switch(c) {
			case ' ':
				if(escaped) {
					throw new CommandLineException("invalid character alignment");
				}

				if(quoted) {
					temp += ' ';
				} else {
					tokens.add(temp);
					temp = "";
				}
				break;

			case '\'':
				if(escaped) {
					temp += '\'';
					escaped = false;
				} else if(quoted) {
					temp.replaceAll("\\\\\\\\", "\\");
					temp.replaceAll("\\\\'", "'");

					tokens.add(temp);
					temp = "";
					quoted = false;
				} else {
					quoted = true;
				}
				break;

			case '\\':
				if(quoted) {
					if(escaped) {
						temp += '\\';
						escaped = false;
					} else {
						escaped = true;
					}
				} else {
					temp += '\\';
				}
				break;

			default:
				if(escaped) {
					throw new CommandLineException("invalid character alignment");
				}
				temp += c;
			}
		}

		tokens.add(temp);
		return tokens.toArray(new String[tokens.size()]);
	}

	public static void script(File file, CurrentWorkingDirectory cwd) throws FileNotFoundException, IOException {
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
			String line;
			while((line = reader.readLine()) != null) {
				try {
					List<String> tokens = Arrays.asList(splitCommandLine(line));
					int i = tokens.indexOf("#");
					if(i != -1) {
						tokens = tokens.subList(0, i);
					}

					if(tokens.isEmpty()) {
						continue;
					}

					commandProcess(tokens.toArray(new String[tokens.size()]), cwd);
				} catch(CommandLineException e) {
					System.err.println("Error: " + e.getMessage());
				}
			}
		}
	}
}

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

import java.io.*;
import java.nio.file.*;
package hcls;

public class CommandProcessor {
	public static void CommandProcess(String[] cmdarray, CurrentWorkingDirectory cwd) throws CommandLineException {
		try {
			switch(cmdarray[0]) {
			case "mkfile":
				command_mkfile(new File(cmdarray[1]), cwd);
				break;

			case "rmfile":
				command_rmfile(new File(cmdarray[1]), cwd);
				break;

			case "cpfile":
				command_cpfile(new File(cmdarray[1]), new File(cmdarray[2]), cwd);
				break;

			case "mkdir":
				command_mkdir(new File(cmdarray[1]), cwd);
				break;

			case "rmdir":
				command_rmdir(new File(cmdarray[1]), cwd);
				break;

			case "cpdir":
				command_cpdir(new File(cmdarray[1]), new File(cmdarray[2]), cwd);
				break;

			case "rename":
				command_rename(new File(cmdarray[1]), new File(cmdarray[2]), cwd);
				break;

			case "tview":
				command_tview(new File(cmdarray[1]), cwd);
				break;

			case "bview":
				command_bview(new File(cmdarray[1]), cwd);
				break;

			case "chcwd":
				command_chcwd(cwd, new File(cmdarray[1]));
				break;

			case "pcwd":
				command_pcwd(cwd);
				break;

			case "app":
				if(cmdarray.length == 1) {
					throw new CommandLineException("few args");
				}

				// appCmdArray = {cmdarray[1], ..., cmdarray[cmdarray.length - 1]}
				String[] appCmdArray = new String[cmdarray.length - 1];
				for(int i = 0; i < appCmdArray.length; ++i) {
					appCmdArray[i] = cmdarray[i + 1];
				}

				command_app(cwd, appCmdArray);
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

	private static void command_mkfile(File file, CurrentWorkingDirectory cwd) throws CommandLineException {
		try {
			if(!cwd.getAbsolutePath(file).createNewFile()) {
				throw new CommandLineException("failed make a file");
			}
		} catch(IOException e) {
			throw new CommandLineException("I/O error");
		} catch(SecurityException e) {
			throw new CommandLineException("access denied");
		}
	}

	private static void command_rmfile(File file, CurrentWorkingDirectory cwd) throws CommandLineException {
		try {
			if(!cwd.getAbsolutePath(file).delete()) {
				throw new CommandLineException("failed remove a file");
			}
		} catch(SecutiryException e) {
			throw new CommandLineException("access denied");
		}
	}

	private static void command_cpfile(File source, File dest, CurrentWorkingDirectory cwd) throws CommandLineException {
		try {
			Files.copy(cwd.getAbsolutePath(source).toPath(), cwd.getAbsolutePath(dest).toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.NOFOLLOW_LINKS);
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

	private static void command_mkdir(File dir, CurrentWorkingDirectory cwd) throws CommandLineException {
		try {
			if(!cwd.getAbsolutePath(dir).mkdir()) {
				throw new CommandLineException("failed make a directory");
			}
		} catch(SecutiryException e) {
			throw new CommandLineException("access denied");
		}
	}

	private static void command_rmdir(File dir, CurrentWorkingDirectory cwd) throws CommandLineException {
		if(!RemoveDir(cwd.getAbsolutePath(dir))) {
			throw new CommandLineException("failed remove a directory");
		}
	}:

	// helper of command_rmdir method
	private static boolean RemoveDir(File file) throws CommandLineException {
		if(!file.exists()) {
			return false;
		}

		try {
			if(file.isDirectory()) {
				for(File FileInTheDir: file.listFiles()) {
					if(!RemoveDir(FileInTheDir)) {
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

	private static void command_cpdir(File source, File dest, CurrentWorkingDirectory cwd) throws CommandLineException {
		if(!CopyDir(cwd.getAbsolutePath(source), cwd.getAbsolutePath(dest))) {
			throw new CommandLineException("failed copy a directory");
		}
	}

	// helper of command_cpdir method
	private static boolean CopyDir(File source, File dest) throws CommandLineException {
		try {
			if(!source.exists()) {
				return false;
			}

			if(!dest.exists()) {
				if(!dest.mkdir()) {
					return false;
				}
			}

			if(source.isFile()) {
				throw new CommandLineException("source is a file");
			}

			if(dest.isFile()) {
				throw new CommandLineException("destination is a file");
			}

			for(File FileInTheDir: source.listFiles()) {
				if(FileInTheDir.isFile()) {
					try {
						Files.copy(FileInTheDir.toPath(), new File(dest.toString() + '/' + FileInTheDir.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.NOFOLLOW_LINKS);
					} catch(IOException e) {
						throw new CommandLineException("I/O error");
					} catch(DirectoryNotEmptyException e) {}
				} else {
					return CopyDir(FileInTheDir, new File(dest.toString() + '/' + FileInTheDir.getName()));
				}
			}

			return true;
		} catch(SecurityException e) {
			throw new CommandLineException("access denied");
		}
	}

	private static void command_rename(File source, File dest, CurrentWorkingDirectory cwd) throws CommandLineException {
		try {
			if(!cwd.getAbsolutePath(source).renameTo(cwd.getAbsolutePath(dest))) {
				throw new CommandLineException("failed rename a file");
			}
		} catch(SecurityException e) {
			throw new CommandLineException("access denied");
		}
	}
}

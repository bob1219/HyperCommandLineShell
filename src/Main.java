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
import hcls.*;

class Main {
	public static void main(String[] args) {
		try {
			if(args.length == 0) {
				commandLine();
			} else {
				try {
					CommandProcessor.script(new File(args[0]), new CurrentWorkingDirectory());
				} catch(FileNotFoundException e) {
					throw new FatalException("file not found");
				} catch(IOException e) {
					throw new FatalException("I/O error");
				}
			}
		} catch(FatalException e) {
			System.err.println("Error: " + e.getMessage());
			System.exit(2);
		} catch(Exception e) {
			System.err.println("THIS IS MY BUG: An unexcepted exception occured.");
			System.err.println("Stack Trace:");
			e.printStackTrace();
			System.err.println();

			System.err.println("If you are developer of this shell, you should fix the bug.");
			System.err.println("If not, you should other shells.");
			System.exit(3);
		} catch(Error e) {
			System.err.println("THIS IS NOT MY BUG: An unexcepted exception occured.");
			System.err.println("Stack Trace:");
			e.printStackTrace();
			System.err.println();

			System.err.println("If you are developer of this shell, you should investigate the cause of the exception.");
			System.err.println("If not, you should ask any Java programmers for help.");
			System.exit(4);
		} catch(Throwable e) {
			System.err.println("CAUSE UNKNOWN: An unexcepted exception occured.");
			System.err.println("Stack Trace:");
			e.printStackTrace();
			System.err.println();

			System.err.println("If you are developer of this shell, you should investigate the cause of the exception.");
			System.err.println("If not, you should ask any Java programmers for help.");
			System.exit(5);
		}
	}

	private static void commandLine() {
		System.out.println("HyperCommandLineShell Version " + ShellDatas.version);
		System.out.println("Copyright 2018 Daiki Yoshida");
		System.out.println();
		System.out.println("This program comes with ABSOLUTELY NO WARRANTY.");
		System.out.println("This is free software, and you are welcome to redistribute it under certain conditions.");
		System.out.println();

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		CurrentWorkingDirectory cwd = new CurrentWorkingDirectory();

		while(true) {
			System.out.print('>');

			String command;
			try {
				command = reader.readLine();
			} catch(IOException e) {
				throw new FatalException("I/O error");
			}

			if(command == null || command.equals("")) {
				continue;
			}

			try {
				CommandProcessor.commandProcess(CommandProcessor.splitCommandLine(command), cwd);
			} catch(CommandLineException e) {
				System.err.println("Error: " + e.getMessage());
			}

			System.out.println();
		}
	}
}

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
			commandLine();
		} catch(FatalException e) {
			System.err.println("Error: " + e.getMessage());
			System.exit(1);
		} catch(Exception e) {
			System.err.println("An unexcepted exception occured.");
			System.err.println("Stack trace:");
			e.printStackTrace();
			System.err.println();

			System.err.println("If you are developer of HyperCommandLineShell, you should bugfix it.");
			System.err.println("If else, USE OTHER VERY VERY GREAT COMMAND-LINE-SHELL!");

			System.exit(2);
		}
	}

	private static void commandLine() {
		System.out.println("HyperCommandLineShell");
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

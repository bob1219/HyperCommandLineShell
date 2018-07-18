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
import java.util.*;
import java.io.*;

class PathProcessor {
	private static List<File> paths = null;
	private static String pathsFile = "./../data/PATH";

	public static File pathProcess(File file, CurrentWorkingDirectory cwd) throws IOException {
		List<String> executableExtensions = getExecutableExtensions();

		// Is target absolute path?
		if(file.isAbsolute()) {
			if(file.exists()) { // Does target exist?
				return file;
			} else {
				for(String executableExtension: executableExtensions) {
					File checkFile = new File(file.toString() + executableExtension);
					if(checkFile.exists()) {
						return checkFile;
					}
				}
			}
		} else {
			File checkFile = new File(cwd.toString() + '/' + file.toString());
			if(checkFile.exists()) { // Is target in current working directory?
				return checkFile;
			}

			for(String executableExtension: executableExtensions) {
				File checkFile2 = new File(checkFile.toString() + executableExtension);
				if(checkFile2.exists()) {
					return checkFile2;
				}
			}

			try {
				read();
			} catch(FileNotFoundException e) {
				return null;
			}

			// Check path directories
			for(File path: paths) {
				File checkFile1 = new File(path.toString() + '/' + file.toString());
				if(checkFile1.exists()) {
					return checkFile1;
				}

				for(String executableExtension: executableExtensions) {
					File checkFile2 = new File(checkFile1.toString() + executableExtension);
					if(checkFile2.exists()) {
						return checkFile2;
					}
				}
			}
		}

		return null;
	}

	private static List<String> getExecutableExtensions() throws IOException {
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("./../data/EXECUTABLE_EXTENSIONS"))))) {
			List<String> executableExtensions = new ArrayList<String>();
			String line;
			while((line = reader.readLine()) != null) {
				executableExtensions.add(line);
			}

			return executableExtensions;
		} catch(FileNotFoundException e) {
			return new ArrayList<String>();
		}
	}

	public static void add(File dir) throws IOException {
		try {
			read();
		} catch(FileNotFoundException e) {}

		File canonicalDir = dir.getCanonicalPath();
		if(!paths.contains(canonicalDir)) {
			paths.add(canonicalDir);
			write();
		}
	}

	public static void del(int n) throws IOException {
		read();
		paths.remove(--n);
		write();
	}

	public static void clear() throws IOException {
		paths.clear();
		write();
	}

	public static List<File> getPaths() throws FileNotFoundException, IOException {
		read();
		return paths;
	}

	private static void read() throws FileNotFoundException, IOException {
		if(paths == null) {
			paths = new ArrayList<File>();
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(pathsFile)))) {
				String line;
				while((line = reader.readLine()) != null) {
					paths.add(new File(line));
				}
			}
		}
	}

	private static void write() throws IOException {
		try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathsFile)))) {
			for(File path: paths) {
				writer.write(path.toString());
				writer.newLine();
			}
		}
	}
}

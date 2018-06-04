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

import java.util.*;
package hcls;

private class PathProcessor {
	private List<File> paths;

	public static File pathProcess(File file, CurrentWorkingDirectory cwd) throws IOException {
		if(file.isAbsolute()) {
			return file;
		} else {
			if(new File(cwd.toString() + '/' + file.toString()).exists()) {
				return new File(cwd.toString() + '/' + file.toString());
			}

			read();
			for(File path: paths) {
				File checkFile = new File(path.toString() + '/' + file.toString());
				if(checkFile.exists()) {
					return checkFile;
				}
			}
		}

		return null;
	}

	public static void add(File dir) throws IOException {
		try {
			read();
		} catch(FileNotFoundException e) {}

		paths.add(dir);
		write();
	}

	public static void del(int n) throws IOException {
		read();
		paths.remove(n);
		write();
	}

	public static void clear() throws IOException {
		read();
		paths.clear();
		write();
	}

	public static List<File> getPaths() { return paths; }

	private static void read() throws IOException {
		paths = new ArrayList<File>();
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("./../datas/PATH")))) {
			String line;
			while((line = reader.readLine()) != null) {
				paths.add(new File(line));
			}
		}
	}

	private static void write() throws IOException {
		try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./../datas/PATH")))) {
			for(File path: paths) {
				writer.write(path.toString());
				writer.newLine();
			}
		}
	}
}

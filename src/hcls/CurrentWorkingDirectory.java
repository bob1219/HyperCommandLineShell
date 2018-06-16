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

public class CurrentWorkingDirectory {
	private File cwd;

	public CurrentWorkingDirectory() { cwd = new File(".").getAbsoluteFile(); }
	public CurrentWorkingDirectory(File dir) { cwd = dir.getAbsoluteFile(); }

	public File getAbsolutePath(File file) {
		return file.isAbsolute() ? file : new File(cwd.toString() + '/' + file.toString());
	}

	public File get() { return cwd; }
	public void set(File dir) { cwd = dir.getAbsoluteFile(); }

	@Override
	public boolean equals(Object obj) {
		return obj == this || (obj.getClass() == getClass() && ((CurrentWorkingDirectory)obj).cwd.equals(cwd));
	}

	@Override
	public int hashCode() { return cwd.hashCode(); }

	@Override
	public String toString() { return cwd.toString(); }
}

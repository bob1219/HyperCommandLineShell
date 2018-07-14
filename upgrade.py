import os.path
import shutil
import sys

try:
	old = input("Old shell's directory: ")
	old_path_file = old + '/data/PATH'
	if os.path.exists(old_path_file):
		shutil.copyfile(old_path_file, './data/PATH')
except SameFileError:
	pass
except OSError:
	error('failed copy path file')

try:
	print()

	print('Executable Extensions:')
	executable_extensions = sys.stdin.readlines()
	if len(executable_extensions) != 0:
		with open('./data/EXECUTABLE_EXTENSIONS', 'w') as executable_extensions_file:
			executable_extensions_file.writelines(executable_extensions)
except IOError:
	error('cannot open executable-extensions file')

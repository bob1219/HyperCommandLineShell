import sys

def error(message):
	print('Error: ' + message)

try:
	print('Paths:')
	paths = sys.stdin.readlines()
	if len(paths) != 0:
		with open('./data/PATH', 'w') as path_file:
			path_file.writelines(paths)
except IOError:
	error('cannot open path file')

try:
	print()

	print('Executable Extensions:')
	executable_extensions = sys.stdin.readlines()
	if len(executable_extensions) != 0:
		with open('./data/EXECUTABLE_EXTENSIONS', 'w') as executable_extensions_file:
			executable_extensions_file.writelines(executable_extensions)
except IOError:
	error('cannot open executable-extensions file')

HyperCommandLineShell
=====================
HyperCommandLineShell is a command-line-shell project.

Development Environment
-----------------------
* Operating System  
Microsoft Windows 10

* Programming Language  
Java9

* JDK  
JDK 9.0.1

Usage
-----
`java -jar hcls.jar`

Commands
--------
* mkfile
	* Description  
	Make a file

	* Usage  
	`mkfile [filename]`

* rmfile
	* Description  
	Remove a file

	* Usage  
	`rmfile [filename]`

* cpfile
	* Description  
	Copy a file

	* Usage  
	`cpfile [source-filename] [destination-filename]`

* mkdir
	* Description  
	Make a directory

	* Usage  
	`mkdir [directory-name]`

* rmdir
	* Description  
	Remove a directory

	* Usage  
	`rmdir [directory-name]`

* cpdir
	* Description  
	Copy a directory

	* Usage  
	`cpdir [source-directory-name] [destination-directory-name]`

* rename
	* Description  
	Rename a file or a directory

	* Usage  
	`rename [source-name] [destination-name]`

* tview
	* Description  
	View content (text format) of a file

	* Usage  
	`tview [filename]`

* bview
	* Description  
	View content (binary format) of a file

	* Usage  
	`bview [filename]`

* chcwd
	* Description  
	Change current-working-directory

	* Usage  
	`chcwd [destination-directory-name]`

* pcwd
	* Description  
	Print current-working-directory

	* Usage  
	`pcwd`

* exec
	* Description  
	Execute a software

	* Usage  
	`exec [software-name] <arguments>`

* path
	* Description  
	Addition, Delete, Print, or Clear path settings

	* Usage  
	`path [option] <option-arguments>`

	* Options
		* add
			* Description  
			Addition a path setting

			* Arguments  
				1. [setting]

		* del
			* Description  
			Delete a path setting

			* Arguments  
				1. [setting-number]

		* clear
			* Description  
			Clear path settings

			* Arguments  
				(nothing arguments)

		* list
			* Description  
			Print list of path settings

			* Arguments  
				(nothing arguments)

* now
	* Description  
	Print current date and time

	* Usage  
	`now`

* exit
	* Description  
	Exit shell

	* Usage  
	`exit`

History
-------
* 1.0.0 (June 18th, 2018)
	* First Version

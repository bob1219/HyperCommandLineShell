use strict;
use warnings;

sub error {
	my ($message) = @_;
	die "Error: " . $message . "\n";
}

print "Paths:\n";
my @paths = <STDIN>;
unless(@paths == 0) {
	open(PATHS_FILE, ">", "./data/PATH") or &error("cannot open path file");
	foreach (@paths) {
		print PATHS_FILE $_;
	}
}

print "\n";

print "Executable Extensions:\n";
my @executable_extensions = <STDIN>;
unless(@executable_extensions == 0) {
	open(EXECUTABLE_EXTENSIONS_FILE, ">", "./data/EXECUTABLE_EXTENSIONS") or &error("cannot open executable-extensions file");
	foreach (@executable_extensions) {
		print EXECUTABLE_EXTENSIONS_FILE $_;
	}
}

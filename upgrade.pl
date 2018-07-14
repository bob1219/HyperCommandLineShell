use strict;
use warnings;
use File::Copy;

sub error {
	my ($message) = @_;
	print "Error: " . $message . "\n";
}

print "Old shell's directory: ";
my $old = <STDIN>;
chop $old;

my $old_path_setting = "$old/data/PATH";
if(-e $old_path_setting) {
	copy($old_path_setting, "./data/PATH") or &error("failed copy path file");
}

print "Executable Extensions:\n";
my @executable_extensions = <STDIN>;
unless(@executable_extensions == 0) {
	open(EXECUTABLE_EXTENSIONS_FILE, ">", "./data/EXECUTABLE_EXTENSIONS") or &error("cannot open executable-extensions file");
	foreach (@executable_extensions) {
		print EXECUTABLE_EXTENSIONS_FILE $_;
	}
}

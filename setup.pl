use strict;
use warnings;
use File::Copy;

sub menu {
	my (@selects) = @_;

	for(my $i = 1; $i <= 55; ++$i) {
		print "*";
	}
	print "\n";

	my $n = 1;
	foreach (@selects) {
		print "$n. $_\n";
		++$n;
	}

	for(my $i = 1; $i <= 55; ++$i) {
		print "*";
	}
	print "\n";

	while(1) {
		print ">";
		my $n = <STDIN>;
		chomp $n;
		$n += 0;

		if($n > 0 and $n <= @selects) {
			return $n;
		}
	}
}

sub failed {
	my ($message) = @_;
	die "Error: $message\n";
}

sub setup_new {
	print "Paths:\n";
	my @paths = <STDIN>;

	print "Executable Extensions:\n";
	my @executable_extensions = <STDIN>;

	open(PATHS_FILE, "> data/PATH") or &failed("failed open path file");
	foreach (@paths) {
		print PATHS_FILE $_;
	}

	open(EXECUTABLE_EXTENSIONS_FILE, "> data/EXECUTABLE_EXTENSIONS") or &failed("failed open executable-extensions file");
	foreach (@executable_extensions) {
		print EXECUTABLE_EXTENSIONS_FILE $_;
	}
}

sub setup_old {
	print "Other HyperCommandLineShell's version:\n";
	my $selects = &menu(("1.0.0 - 2.1.0"), ("2.2.0 - 2.3.1"));

	print "Other HyperCommandLineShell's directory: ";
	my $src_dir = <STDIN>;
	chomp $src_dir;

	if($selects == 1) {
		copy("$src_dir/data/PATH", "./data/PATH") or &failed("failed copy path file");

		print "Executable Extensions:\n";
		my @executable_extensions = <STDIN>;

		open(EXECUTABLE_EXTENSIONS_FILE, "> data/EXECUTABLE_EXTENSIONS") or &failed("failed open executable-extensions file");
		foreach (@executable_extensions) {
			print EXECUTABLE_EXTENSIONS_FILE $_;
		}
	} else {
		copy("$src_dir/data/PATH", "./data/PATH") or &failed("failed copy path file");
		copy("$src_dir/data/EXECUTABLE_EXTENSIONS", "./data/EXECUTABLE_EXTENSIONS") or &failed("failed copy executable-extensions file");
	}
}

print "HyperCommandLineShell Setup:" . "\n";
my $select = &menu(("Setup with new settings", "Setup with other HyperCommandLineShell's settings"));

if($select == 1) {
	&setup_new;
} elsif($select == 2) {
	&setup_old;
}

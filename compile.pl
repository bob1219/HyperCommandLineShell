use strict;
use warnings;
use File::Basename;
use File::Path;

sub failed {
	my ($line) = @_;
	die "error: line $line\n";
}

chdir("src") or &failed(__LINE__);
system("javac Main.java") and &failed(__LINE__);
chdir("..") or &failed(__LINE__);
if(-e "bin/hcls.jar") {
	unlink("bin/hcls.jar") or &failed(__LINE__);
}
rename("src/Main.class", "bin/Main.class") or &failed(__LINE__);
unless(-e "bin/hcls") {
	mkdir("bin/hcls") or &failed(__LINE__);
}
foreach (glob("src/hcls/*.class")) {
	$_ = basename($_);
	rename("src/hcls/$_", "bin/hcls/$_") or &failed(__LINE__);
}
system("jar cvfm bin/hcls.jar manifest.mf -C bin .") and &failed(__LINE__);
rmtree("bin/hcls") or &failed(__LINE__);
unlink("bin/Main.class") or &failed(__LINE__);

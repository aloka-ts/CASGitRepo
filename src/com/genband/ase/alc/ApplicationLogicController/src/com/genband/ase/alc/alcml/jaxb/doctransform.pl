#!/usr/bin/perl -I. 
use Getopt::Long;
use File::Find;
use POSIX qw(strftime);
Getopt::Long::Configure("auto_abbrev");
GetOptions("source:s", "transform:s", "appbuilddir:s", "output:s");

$opt_source || die;
$opt_transform || die;
$opt_appbuilddir || die;
$opt_output || die;

# build translet
my $compile=`java org.apache.xalan.xsltc.cmdline.Compile -d $opt_appbuilddir $opt_transform`;

my ($transformClassName) = ($opt_transform =~ /([a-z,A-Z,0-9]+)\.xsl/s);
                       

# transform                           
my $transform=`java -classpath $opt_appbuilddir  org.apache.xalan.xsltc.cmdline.Transform -u $opt_source $transformClassName`;

open(OUTPUT, ">$opt_output") || die "Can't open $opt_output";
print OUTPUT $transform;
close OUTPUT;

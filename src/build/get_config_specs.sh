#!/bin/ksh 
. ant.properties

echo "BUILD INFORMATION"
echo
echo "######## $@ ########"
echo
uname -a
$JAVA_HOME/bin/java -version
id
hostname
date
echo
echo "----CONFIG SPECS----"
echo
cleartool catcs

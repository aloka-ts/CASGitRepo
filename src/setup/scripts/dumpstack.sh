#!/bin/ksh

## Helper script for attaching the JVM to jdb before
#  exit. This script can be called from inside the JVM
#  and this will dump the debug information into a file.
#  The port chosen here is 8000. If you need to change
#  the port where jdb attaches itself to the JVM, change
#  it in this script and also in the ase_no_ems script
#  Nasir   BayPackets Inc. 07/26/04
##

#### INSTALL_ROOT should be replaced with actual InstallRoot at the time of install
export INSTALLROOT=INSTALL_ROOT
cd $INSTALLROOT

PATH=$PATH:/usr/bin
export PATH

ASE_HOME=$INSTALLROOT/ASESubsystem

HOST_ADDR=`id | sed "s/uid=//g" | cut -d"(" -f2 | sed "s/).*//g"`
date_stamp=`date | awk '{ printf("%s_%s_%s", $2, $3, $4); }'`
FILENAME="ASE_StackDump_${HOST_ADDR}_${date_stamp}.log"

#exec 1>$ASE_HOME/../LOGS/$FILENAME 2>&1 
exec 1>/LOGS/CAS/$FILENAME 2>&1 

echo "------------------START-$$------------------" 

DEBUG_PORT=8000

echo "Debugger call invoked at :"`date`
echo "Port number is :" $DEBUG_PORT
echo
echo

jdb -attach $DEBUG_PORT << _EOF_

suspend 

where all

resume

exit

_EOF_

echo "------------------END--------------------"

echo "Debug completed at :" `date`

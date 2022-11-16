#!/bin/ksh
### ====================================================================== ###
##                                                                          ##
##  JBoss Startup Script                                                    ##
##                                                                          ##
### ====================================================================== ###

HOST_ADDR=`id | sed "s/uid=//g" | cut -d"(" -f2 | sed "s/).*//g"`
date_stamp=`date | awk '{ printf("%s_%s_%s", $2, $3, $4); }'`
FILE_NAME="MAPS_${HOST_ADDR}_${date_stamp}.rexec_out"

exec 1>/LOGS/MAPS/${FILE_NAME} 2>&1

if [ ! -d $JBOSS_HOME ]
then

echo "####################################"
echo "Could not find JBoss Home Directory."
exit "####################################"

fi

echo "##### Attempting to start JBoss Server #####"
echo ""
echo "Note : Kindly Check latest logs in /LOGS/MAPS for the status"
echo ""

#trapping Ctrl-C and Terminal close signals

trap "echo got signal..." HUP INT 
. $JBOSS_HOME/bin/run.sh -c maps -b $WWW_BIND_IP -DLOG_LOCATION="$LOG_LOCATION" -DLOG_FILENAME="$LOG_FILENAME"

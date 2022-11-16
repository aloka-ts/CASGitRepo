#!/bin/ksh

#####################################################
#                                                   #
# Script to stop JBoss running server               #
#                                                   #
#####################################################


JBOSS_PID=`/usr/ucb/ps -auxww | grep -i jboss | grep -v grep | awk '{print $2}'`
PID=${JBOSS_PID:-NotAvailable}


if [ $PID != "NotAvailable" ]
then
   
   echo ""
   echo "Killing Jboss process."
   echo "Process ID of currently running JBoss  :: $PID"
   kill -9 $PID
   echo ""

else
   echo ""
   echo "No JBoss process is running."
   echo ""
fi

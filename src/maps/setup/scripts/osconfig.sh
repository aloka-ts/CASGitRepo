#!/bin/ksh

#Going to create Logs directory

LOG_DIR=LOG_DIRECTORY

if [ -d $LOG_DIR ] 
then
rm -rf $LOG_DIR
fi

mkdir -p $LOG_DIR 
chmod 777 $LOG_DIR 

echo "SUCCESS"

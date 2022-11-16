#!/usr/bin/ksh -p

# This script need to be run once a day with retention period as the argument.
# Script will cleanup all the directory older than retention period. Script
# also compress the files older than two days.

export PATH=$PATH:/usr/bin:/bin

typeset -i RetentionPeriod

LogDir=$1
RetentionPeriod=$2
User=$3
format=$4
TMP_FILE_NAME=/tmp/logCleaner_"$$".lst

if (( $# != 4))
then
   echo "Invalid number of arguments."
   exit 1
fi

if [ ! -d $LogDir ]
then
   echo "$LogDir is not a directory"
   exit 1
fi

if (( $RetentionPeriod <= 0 ))
then
   echo "Retention period should be more than two days."
   exit 1
fi

find $LogDir -name "$format" -type d -user $User -mtime +$RetentionPeriod -exec rm -Rf {} \;
find $LogDir -name "$format" -user $User -mtime +$RetentionPeriod -exec rm -Rf {} \;
find $LogDir \( ! -name "*gz" \) -user $User -type f -mtime +1 > $TMP_FILE_NAME
cat $TMP_FILE_NAME | grep "$format" | while read fl
do
	gzip $fl
done
rm $TMP_FILE_NAME

find $LogDir -name "$format" -user $User -type f -size 1000000c -exec gzip {} \;

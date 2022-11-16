#!/bin/ksh

####################################################################
# Functions used for the getting the file currently in use
####################################################################
function getCurrentlyUsedFile {
	cd $LOG_DIR

	# Pick the last one (based on the timestamp in the filename)
	FILE_NAME_LIST=`ls -r $FILE_PATTERN | tr "/\n" " "`
	CURRENT_FILE=`echo $FILE_NAME_LIST | cut -d" " -f1`

	cd $ORIG_DIR
}

####################################################################
# Functions used for the getting the files to be deleted
####################################################################
function getFilesToBeDeleted {
	# Get the user Id for which the files are to be removed
	CURRENT_CMD=`whence $0`
	USER_ID=`ls -al $CURRENT_CMD | awk '{print $3}'`

	cd $LOG_DIR

	# Get the filenames qualifying the HK criterion (other than the one in use)
	FILE_NAMES_TBR=`find . \( -name "$FILE_PATTERN" -a ! -name $CURRENT_FILE -a -user $USER_ID -a -mtime +$RETENTION_PERIOD_IN_DAYS \)`

	cd $ORIG_DIR
}

######################################################################
# Function used for getting the dated sub-directories to be deleted.
######################################################################
function getDirsToBeDeleted {
        # Get the user Id for which the files are to be removed
        CURRENT_CMD=`whence $0`
        USER_ID=`ls -al $CURRENT_CMD | awk '{print $3}'`

        cd $LOG_DIR

        # Get the dirnames qualifying the HK criterion 
        DIR_NAMES_TBR=`find . \( -type "d" -a -user $USER_ID -a -mtime +$RETENTION_PERIOD_IN_DAYS \)`
        cd $ORIG_DIR
}

LOG_DIR=$1
FILE_PATTERN=$2
RETENTION_PERIOD_IN_DAYS=$3
LOGFILE=/bp_logs/Housekeeping/cleanupSASLog.log
ORIG_DIR=`pwd`

echo "Cleanup started on "  `date` >> $LOGFILE
echo "Parameters used -" >> $LOGFILE
echo "$LOG_DIR $FILE_PATTERN $RETENTION_PERIOD_IN_DAYS" >> $LOGFILE

getCurrentlyUsedFile
echo "Current file in use is : $CURRENT_FILE" >> $LOGFILE

getFilesToBeDeleted
echo "Log files to be deteled are : $FILE_NAMES_TBR" >> $LOGFILE

for FILE_NAME in $FILE_NAMES_TBR
do
	echo "Removing file $LOG_DIR/$FILE_NAME" >> $LOGFILE
	rm -rf $LOG_DIR/$FILE_NAME
done

getDirsToBeDeleted
echo "Log directories to be deteled are : $DIR_NAMES_TBR" >> $LOGFILE

for DIR_NAME in $DIR_NAMES_TBR
do
        echo "Removing directory $LOG_DIR/$DIR_NAME and its contents" >> $LOGFILE
        rm -rf $LOG_DIR/$DIR_NAME
done

echo "Cleanup for HK completed" >> $LOGFILE

#EOF

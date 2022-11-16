#!/bin/ksh

if [[ $# -ne 3 ]] ; then
	echo "Usage: ./rollbackDbPatch.sh <DB User> <DB Passwd> <DB SID>"
	exit 1
fi

LOG_FILE=/tmp/utility_dbPatch_rollback.lst

$ORACLE_HOME/bin/sqlplus $1/$2@$3 << _EOF_
set linesize 1500 trimspool on
spool $LOG_FILE


DROP VIEW CDR_DATA_VW;
ALTER TABLE CDR_ATTR_TBL DROP CONSTRAINT CDR_ATTR_TBL_UK ;
DROP TABLE CDR_ATTR_TBL;

ALTER TABLE CDR_DATA_TBL DROP COLUMN ACPC;

CREATE VIEW CDR_DATA_VW AS (
SELECT 
SWITCH_CODE || ',' || STATUS_IND_ATTMPTD_CALL || ',' || STATUS_IND_FAILED_CALL || ',' || STATUS_IND_INTRMDT_CALL || ',' || 
STATUS_IND_NON_CHRGD_CALL || ',' || CHARGE_TABLE_TYPE || ',' || CALLING_ID_MA || ',' || CALLED_ID || ',' || 
START_TIME  || ',' || ANSWER_TIME || ',' || DISCONNECT_TIME || ',' || TERM_INTERWORKING_IND || ',' || 
ORIG_INTERWORKING_IND || ',' || TERM_ISUP1_LINK_IND || ',' || ORIG_ISUP1_LINK_IND || ',' || TERM_ISDN_ACCESS_IND || ',' || 
ORIG_ISDN_ACCESS_IND || ',' || CHARGE_INDICATOR || ',' || CALLING_PARTY_CATEGORY || ',' || 
CHARGE_ID_OPTION || ',' || RECEIVED_INFORMATION_OPTION || ',' || SERVICE_INDICATOR || ',' || ATTEMPTED_OPTION || ',' ||
CALLING_ID_OPTION || ',' || SIGNALING_NW_USAGE_OPTION || ',' || VPN_OPTION || ',' || ORIG_TERM_CA_OPTION || ',' ||
FW_DIR_CIT_INFORMATION || ',' || BW_DIR_CIT_INFORMATION || ',' || UAN_OPTION AS CDR,  
FILENAME, IS_SFTPED, CREATED_ON, SENT_FILENAME  
FROM CDR_DATA_TBL);

ALTER TRIGGER INSERT_SENDFILENAME ENABLE;


SHOW ERROR;

_EOF_

if [[ $? -ne 0 ]] ; then
	echo "Failed to connect to $1/$2@$3 database"
	exit 1;
fi

NO_ERROR=`cat $LOG_FILE | grep "No errors." | wc -l`
if [[ $NO_ERROR -eq 0 ]]
then
    echo "Failed to apply DB Patch on $1/$2@$3 database !!!"
    exit 1
fi

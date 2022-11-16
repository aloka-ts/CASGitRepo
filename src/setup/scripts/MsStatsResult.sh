#!/bin/ksh

##########Function block##################

function getNumberRecords
{
	>$TEMP_FILE
	>$OUTPUT_FILE
	########## $1- number, $2- type, $3- order by
	$ORACLEHOME/bin/sqlplus -s -L $DBUSER/$DBPASSWORD@$DBSID << _EOF_

	set linesize 15000 trimspool on
	set head off
	set feedback off
        set echo off
	col data format A200
 	spool $TEMP_FILE

	SELECT 'ANNOUNCEMENT_ID                          ANNOUNCEMENT_TYPE         ATTEMPTS          RETRIES         ANNOUNCEMENT_DURATION' data from dual;
        SELECT '--------------                           -----------------         --------          -------         ----------------------' data from dual;

	 SELECT RPAD(SUBSTR(announcement_id,1,40),40,' ')||'     '||RPAD(SUBSTR(announcement_type,1,15),15,' ')||'        '||
                        RPAD(SUBSTR(TO_CHAR( attempts),1,10),10,' ')||'         '||
                        RPAD(SUBSTR(TO_CHAR( retries),1,10),10,' ')||'        '||
                        TO_CHAR(announcement_duration) data
		 FROM media_statistics_info
		 WHERE (ROWNUM <= $1 )
		 AND announcement_type = '$2'
		 ORDER BY $3 DESC;

	spool off

_EOF_
	
}


function getTimeRecords
{
        >$TEMP_FILE
	>$OUTPUT_FILE
        ########## $1- from time, $2- to time
        $ORACLEHOME/bin/sqlplus -s -L $DBUSER/$DBPASSWORD@$DBSID << _EOF_

        set linesize 15000 trimspool on
        set head off
        set feedback off
        set echo off
	col data format A200
        spool $TEMP_FILE

	SELECT 'ANNOUNCEMENT_ID                          ANNOUNCEMENT_TYPE         ATTEMPTS          RETRIES         ANNOUNCEMENT_DURATION' data from dual;
        SELECT '--------------                           -----------------         --------          -------         ----------------------' data from dual;

        SELECT RPAD(SUBSTR(announcement_id,1,40),40,' ')||'     '||RPAD(SUBSTR(announcement_type,1,15),15,' ')||'        '||
                        RPAD(SUBSTR(TO_CHAR( attempts),1,10),10,' ')||'         '||
                        RPAD(SUBSTR(TO_CHAR( retries),1,10),10,' ')||'        '||
                        TO_CHAR(announcement_duration) data
                 FROM media_statistics_info
                 WHERE to_date('$1','YYYYMMDDHH24MISS') <= to_date(last_updated_time,'YYYYMMDDHH24MISS')
                 AND to_date('$2','YYYYMMDDHH24MISS') >= to_date(last_updated_time,'YYYYMMDDHH24MISS')
                 ORDER BY announcement_id ASC;

        spool off

_EOF_

}

function getDurationRecords
{
        >$TEMP_FILE
        >$OUTPUT_FILE
        ########## $1- type
        $ORACLEHOME/bin/sqlplus -s -L $DBUSER/$DBPASSWORD@$DBSID << _EOF_

        set linesize 15000 trimspool on
        set head off
        set feedback off
        set echo off
	column announcement_type format a20
	column total_duration format a100
        spool $TEMP_FILE

        SELECT 'ANNOUNCEMENT_TYPE' announcement_type, 'TOTAL_DURATION' total_duration from dual;
        SELECT '-----------------' announcement_type, '--------------' total_duration from dual;

        SELECT announcement_type, TO_CHAR(SUM(announcement_duration))
                 FROM media_statistics_info
		 GROUP BY announcement_type;

        spool off

_EOF_

}

##########Function block ends##################

if [[ $# -ne 4 ]] ; then
        echo "USage:: ./MsStatsResult.sh <ORACLE_HOME> <DB USER> <DB PASSWORD> <DB SID>"
        exit 1
fi

ORACLEHOME=$1
DBUSER=$2
DBPASSWORD=$3
DBSID=$4

OUTPUT_FILE=/LOGS/CAS/MsStatsOutput
TEMP_FILE=/LOGS/CAS/MsStatsTemp.lst

echo "Please select the type of report to generate : "
echo "1 (Time of Day based report)"
echo "2 (Guidance with maximum duration)"
echo "3 (Prompts with maximum retries)"
echo "4 (Prompts with maximum attempts)" 
echo "5 (Total duration report)"
read REPORT

if [[ $REPORT -eq 1 || $REPORT -eq 2 || $REPORT -eq 3 || $REPORT -eq 4 || $REPORT -eq 5 ]];then
	case $REPORT in
		1) echo "Please enter from time (YYYYMMDDHH24MI format)  : "
			read FROMTIME
			
			if [[ x$FROMTIME == "x" ]]; then
				echo "\033[1mNo input for from time!! Please run the script again...\033[0m"
			exit 1
			fi

			echo "Please enter to time (YYYYMMDDHH24MI format)  : "
			read TOTIME
			
			if [[ y$TOTIME == "y" ]]; then
				echo "\033[1mNo input for to time !! Please run the script again...\033[0m"
				exit 1

			fi;;
		
		2|3|4) echo "Enter number of records to retrieve : "
			read RECORDS

			if [[ x$RECORDS == "x" ]]; then
				echo "\033[1mNo input for number of records !! Please run the script again...\033[0m"	
				exit 1
			fi;;

		5) echo " ";;


		*) echo "\033[1mInvalid option selected !! Please run the script again...\033[0m"
                   exit 1
	esac

else
	echo "\033[1mInvalid Report selected... Please run script again!!\033[0m" 
	exit 1
fi
	
if [[ $REPORT -eq 1 ]];then

	getTimeRecords $FROMTIME $TOTIME	
	cat $TEMP_FILE >> $OUTPUT_FILE
	rm -rf $TEMP_FILE

elif [[ $REPORT -eq 2 ]];then

	getNumberRecords $RECORDS "Guidance" "announcement_duration"
	cat $TEMP_FILE >> $OUTPUT_FILE
	rm -rf $TEMP_FILE

elif [[ $REPORT -eq 3 ]];then

	getNumberRecords $RECORDS "Prompt" "retries"	
	cat $TEMP_FILE >> $OUTPUT_FILE
	rm -rf $TEMP_FILE

elif [[ $REPORT -eq 4 ]];then

	getNumberRecords $RECORDS "Prompt" "attempts"
	cat $TEMP_FILE >> $OUTPUT_FILE
	rm -rf $TEMP_FILE

elif [[ $REPORT -eq 5 ]];then

        getDurationRecords
        cat $TEMP_FILE >> $OUTPUT_FILE
        rm -rf $TEMP_FILE

fi

echo " "
echo "\033[1mThe generated report file is : $OUTPUT_FILE \033[0m"

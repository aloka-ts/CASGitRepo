#!/bin/ksh

cd
. profile.test.emsSAS

CURRDIR=`pwd`

DB_USER=`cat $INSTALLROOT/sol28g/conf/ConfigFile.dat | grep DB_User_Id | awk -F':' '{print $2}'`
DB_PASSWD=`cat $INSTALLROOT/sol28g/conf/ConfigFile.dat | grep DB_Password | awk -F':' '{print $2}'`
DB_PRI_HOST=`cat $INSTALLROOT/sol28g/conf/ConfigFile.dat | grep DB_Connect_String | awk -F':' '{print $2}'`

DB_SEC_HOST=`cat $INSTALLROOT/sol28g/conf/ConfigFile.dat | grep DB_Secondary_Connect_String | awk -F':' '{print $2}'`

DB_PRI_STR=$DB_USER/$DB_PASSWD@$DB_PRI_HOST
DB_SEC_STR=$DB_USER/$DB_PASSWD@$DB_SEC_HOST

SUBSYS_ID=$1

echo "DB USER $DB_USER"
echo "DB Password $DB_PASSWD"
echo "Primary DB SID $DB_PRI_HOST"
echo "Secondary DB SID $DB_SEC_HOST"
echo
echo "Primary DB conection string : $DB_PRI_STR"
echo "Secondary DB conection string : $DB_SEC_STR"

echo "SubSystemID : $SUBSYS_ID "

update_db() {

$ORACLE_HOME/bin/sqlplus $DB_STR <<  _EOF_ 

spool SipServlet5.5B04_P04_rollBack.log

SET SERVEROUTPUT ON

DECLARE
flag Number := 0;
BEGIN
    BEGIN
        select 1 into flag from rsi_measurement_cntr where id='SAS.Total Number of ACK Timedout' and SUBSYSID='$SUBSYS_ID';
        IF (flag = 1) THEN
            delete from rsi_measurement_cntr where id in ('SAS.Total Number of ACK Timedout','SAS.Total Number of PRACK Timedout','SAS.Total Number of Serialization Failures','SAS.Total Number of Deserialization Failures') and SUBSYSID='$SUBSYS_ID';

			
        END IF;
	    EXCEPTION
        WHEN NO_DATA_FOUND THEN
	        flag :=0; 
    END;
	commit;
END;
/
_EOF_

}

DB_STR="$DB_PRI_STR"
update_db

if [ -n "$DB_SEC_HOST" ]
then
    DB_STR="$DB_SEC_STR"
    update_db
fi

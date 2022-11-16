#!/bin/ksh

##########################################################################################
#   Objective       : SAS upgradation propcess from SAS5.1.1 to SAS 5.5
#   Propgram        : Gets the subsystem ID from the DB 
#   Author          : Prashant Kumar
#   Last Modified   : 10.03.2007
##########################################################################################


. profile.test.emsSAS

DB_STR=$1
SUB_ID=$2

$ORACLE_HOME/bin/sqlplus $DB_STR << _EOF_


select *  from rsisubsystem where subsystem_id = '$SUB_ID' and name like 'ASESUBSYSTEM%';

_EOF_

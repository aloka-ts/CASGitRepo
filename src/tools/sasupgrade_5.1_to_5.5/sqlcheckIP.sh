#!/bin/ksh

##########################################################################################
#   Objective       : SAS upgradation propcess from SAS5.1.1 to SAS 5.5
#   script			: Returns the FIP for SAS setup from DB.
#   Author          : Prashant Kumar
#   Last Modified   : 10.03.2007
##########################################################################################


. profile.test.emsSAS

DB_STR=$1
SUB_ID=$2

ret=`$ORACLE_HOME/bin/sqlplus -s $DB_STR << _EOF_
select new_param_val from rsicomponent_config where component_id = '$SUB_ID' and param_oid = '30.1.11' ;
_EOF_`

echo $ret
fip=`echo $ret |  sed 's/[- ][- ]*/ /g' | cut -f2 -d' ' `
echo "%"$fip

#!/bin/ksh

##########################################################################################
#   Objective       : SAS upgradation propcess from SAS5.1.1 to SAS 5.5
#   scripts 		: returns the peer SAS IP
#   Author          : Prashant Kumar
#   Last Modified   : 10.03.2007
##########################################################################################


. profile.test.emsSAS

DB_STR=$1
SUBSYS_ID=$2

ret=`$ORACLE_HOME/bin/sqlplus -s $DB_STR << _EOF12_
select new_param_val from rsicomponent_config where component_id = '$SUBSYS_ID'and param_oid = '30.1.31';
_EOF12_`

peer_ip=`echo $ret |  sed 's/[- ][- ]*/ /g' | cut -f2 -d' ' `

echo "% "$peer_ip

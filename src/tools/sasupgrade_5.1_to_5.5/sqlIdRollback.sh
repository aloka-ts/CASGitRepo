#!/bin/ksh

##########################################################################################
#   Objective       : SAS upgradation propcess from SAS5.1.1 to SAS 5.5
#   scripts 		: returns the peer SAS IP
#   Author          : Prashant Kumar
#   Last Modified   : 10.03.2007
##########################################################################################


#select new_param_val from rsicomponent_config where component_id = '$SUBSYS_ID'and param_oid = '30.1.31';
#echo "% "$peer_ip

. profile.test.emsSAS

DB_STR=$1
SUBSYS_ID=$2

ret=`$ORACLE_HOME/bin/sqlplus -s $DB_STR << _EOF12_
select fu_id from rsisubsystem where subsystem_id = '$SUBSYS_ID';
_EOF12_`

FUNC_ID=`echo $ret |  sed 's/[- ][- ]*/ /g' | cut -f2 -d' ' `
echo "AA func id =" $FUNC_ID

ret=`$ORACLE_HOME/bin/sqlplus -s $DB_STR << _EOF12_
select fug_id from rsifunctionalunit where fu_id = '$FUNC_ID';
_EOF12_`

FUNC_GRP_ID=`echo $ret |  sed 's/[- ][- ]*/ /g' | cut -f2 -d' ' `
echo "AA func grp id =" $FUNC_GRP_ID

ret=`$ORACLE_HOME/bin/sqlplus -s $DB_STR << _EOF12_
select system_id from rsifunctionalunitgroup where fug_id = '$FUNC_GRP_ID';
_EOF12_`

SYS_ID=`echo $ret |  sed 's/[- ][- ]*/ /g' | cut -f2 -d' ' `
echo "AA system id = " $SYS_ID

ret=`$ORACLE_HOME/bin/sqlplus -s $DB_STR << _EOF12_
select cluster_id from rsisystem where system_id = '$SYS_ID';
_EOF12_`

CLUS_ID=`echo $ret |  sed 's/[- ][- ]*/ /g' | cut -f2 -d' ' `
echo "AA cluster id = " $CLUS_ID

ret=`$ORACLE_HOME/bin/sqlplus -s $DB_STR << _EOF12_
select domain_id from rsicluster where cluster_id = '$CLUS_ID';
_EOF12_`

DOM_ID=`echo $ret |  sed 's/[- ][- ]*/ /g' | cut -f2 -d' ' `
echo "AA domain id = " $DOM_ID

echo "Got following values"
echo "functional id = " $FUNC_ID
echo "functional grp id = " $FUNC_GRP_ID
echo "system id = " $SYS_ID
echo "cluster id = " $CLUS_ID
echo "domain id = " $DOM_ID

$ORACLE_HOME/bin/sqlplus -s $DB_STR << _EOF_

update rsicomponent_config set param_val = '5.1.1' , new_param_val = '5.1.1' 
where component_id ='$SUBSYS_ID' and param_oid = '1.11.1' and subcomponent_id = '0';

update rsicomponent_config set param_val = '5.1.1' , new_param_val = '5.1.1' 
where component_id ='$FUNC_ID' and param_oid = '1.11.1' and subcomponent_id = '0';

update rsicomponent_config set param_val = '5.1.1' , new_param_val = '5.1.1' 
where component_id ='$FUNC_GRP_ID' and param_oid = '1.11.1' and subcomponent_id = '0';

update rsicomponent_config set param_val = '5.1.1' , new_param_val = '5.1.1' 
where component_id ='$SYS_ID' and param_oid = '1.11.1' and subcomponent_id = '0';

commit;

_EOF_

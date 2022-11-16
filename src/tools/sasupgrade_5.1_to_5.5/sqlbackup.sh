#!/bin/ksh 

##########################################################################################
#   Objective       : SAS upgradation propcess from SAS5.1.1 to SAS 5.5
#   script			: rollbacks the DB changes done for SAS upgradation
#   Author          : Prashant Kumar
#   Last Modified   : 10.03.2007
##########################################################################################


. profile.test.emsSAS

DB_STRING=$1
INSTALL_ROOT=$2
SUBSYS=$3

echo "oracle home :"
echo $ORACLE_HOME 
echo "db connection string : "
echo $DB_STRING 
echo "install root :"
echo $INSTALL_ROOT 
echo "subsystem id :"
echo $SUBSYS 

$ORACLE_HOME/bin/sqlplus $DB_STRING <<  _EOF_

SET SERVEROUTPUT ON

delete from rsicomponent_config
where component_id = '$SUBSYS';

insert into rsicomponent_config ( select * from rsicomponent_config_$SUBSYS );
drop table rsicomponent_config_$SUBSYS;

DECLARE
flag Number := 0;

BEGIN

update rsisubsystem set execution_info = '$INSTALL_ROOT/ASESubsystem/sol28g/scripts/ase'
where subsystem_id ='$SUBSYS';

delete from rsi_measurement_set where id='SAS MeasurementSet Priority Calls' and SUBSYSID='$SUBSYS';

BEGIN

SELECT 1 INTO flag FROM rsi_user_measurement_set WHERE ID='SAS MeasurementSet Priority Calls' AND VERSION=1;
IF (flag = 1) THEN
delete from rsi_user_measurement_set where id ='SAS MeasurementSet Priority Calls' and VERSION=1 ;
END IF;
EXCEPTION
	WHEN NO_DATA_FOUND THEN
    flag :=0; 
END;

delete from rsi_measurement_cntr where id in ('SAS.Priority INVITEs IN','SAS.Priority INVITEs OUT','SAS.Priority Unsuccessful Sessions','SAS.Priority SIP Messages','SAS.Priority INVITEs Queued But Dropped','SAS.Priority INVITEs Dropped Due To M/C Cngn','SAS.Priority INVITEs Exempted Frm Cngn Ctrl','SAS.Priority INVITEs Exempted Frm N/W Mgmt Ctrl') and SUBSYSID='$SUBSYS';


commit;

END;
/
_EOF_

#!/bin/ksh 

##########################################################################################
#   Objective       : SAS upgradation propcess from SAS5.1.1 to SAS 5.5
#   scripts 		: upgrades and insert the SAS OIDS in DB
#   Author          : Prashant Kumar
#   Last Modified   : 10.03.2007
##########################################################################################


. profile.test.emsSAS

INSTALL_ROOT=$1
DB_STRING=$2
SUBSYS_ID=$3

echo "oracle home is : $ORACLE_HOME" 
echo "data base string is :" $DB_STRING 
echo "subsystem id is : " $SUBSYS_ID 

$ORACLE_HOME/bin/sqlplus $DB_STRING <<  _EOF_ 

spool sas_dbupgrade.log

SET SERVEROUTPUT ON

DECLARE
flag Number := 0;
query VARCHAR2(500);


BEGIN

query:='create table rsicomponent_config_$SUBSYS_ID as (select * from rsicomponent_config where component_id=$SUBSYS_ID)';
execute immediate query;

update rsicomponent_config set param_val = '/LOGS/SAS/' , new_param_val = '/LOGS/SAS/' 
where component_id ='$SUBSYS_ID' and param_oid = '1.1.2' and subcomponent_id = '0';

update rsicomponent_config set param_val = 'SAS.log' ,  new_param_val = 'SAS.log' 
where component_id ='$SUBSYS_ID' and param_oid = '1.1.7' and subcomponent_id = '0';

update rsicomponent_config set param_val = '/LOGS/SAS/sipDebug.log'  , new_param_val = '/LOGS/SAS/sipDebug.log' 
where component_id ='$SUBSYS_ID' and param_oid = '30.1.10' and subcomponent_id = '0';

update rsicomponent_config set param_val = '5.5' , new_param_val = '5.5' 
where component_id ='$SUBSYS_ID' and param_oid = '1.11.1' and subcomponent_id = '0';

update rsicomponent_config set param_val = '1' , new_param_val = '1' 
where component_id ='$SUBSYS_ID' and param_oid = '30.1.21' and subcomponent_id = '0';

update rsicomponent_config set param_val = '5' , new_param_val = '5' 
where component_id ='$SUBSYS_ID' and param_oid = '30.1.16' and subcomponent_id = '0';

update rsicomponent_config set param_val = '60' , new_param_val = '60' 
where component_id ='$SUBSYS_ID' and param_oid = '30.1.17' and subcomponent_id = '0';

update rsisubsystem set execution_info = '$INSTALL_ROOT/ASESubsystem/scripts/ase'
where subsystem_id ='$SUBSYS_ID';

insert into rsicomponent_config values ( '$SUBSYS_ID' , '30.1.36' , 'NULL' , 'NULL' , '0' );
insert into rsicomponent_config values ( '$SUBSYS_ID' , '30.1.37' , 'NULL' , 'NULL' , '0' );
insert into rsicomponent_config values ( '$SUBSYS_ID' , '30.1.38' , 'NULL' , 'NULL' , '0' );
insert into rsicomponent_config values ( '$SUBSYS_ID' , '30.1.39' , 'NULL' , 'NULL' , '0' );
insert into rsicomponent_config values ( '$SUBSYS_ID' , '30.1.40' , 'NULL' , 'NULL' , '0' );
insert into rsicomponent_config values ( '$SUBSYS_ID' , '30.1.41' , 'NULL' , 'NULL' , '0' );
insert into rsicomponent_config values ( '$SUBSYS_ID' , '30.1.42' , 'NULL' , 'NULL' , '0' );
insert into rsicomponent_config values ( '$SUBSYS_ID' , '30.1.43' , 'NULL' , 'NULL' , '0' );
insert into rsicomponent_config values ( '$SUBSYS_ID' , '30.1.44' , 'NULL' , 'NULL' , '0' );
insert into rsicomponent_config values ( '$SUBSYS_ID' , '30.1.45' , 'NULL' , 'NULL' , '0' );
insert into rsicomponent_config values ( '$SUBSYS_ID' , '30.1.58' , 'NULL' , 'NULL' , '0' );
insert into rsicomponent_config values ( '$SUBSYS_ID' , '30.1.59' , 'NULL' , 'NULL' , '0' );
insert into rsicomponent_config values ( '$SUBSYS_ID' , '30.1.66' , '60' , '60' , '0' );

insert into rsicomponent_config values ( '$SUBSYS_ID' , '30.4.1' , '0' , '0' , '0' );
insert into rsicomponent_config values ( '$SUBSYS_ID' , '30.4.2' , '47' , '47' , '0' );
insert into rsicomponent_config values ( '$SUBSYS_ID' , '30.4.3' , '8081' , '8081' , '0' );
insert into rsicomponent_config values ( '$SUBSYS_ID' , '30.4.4' , '2000' , '2000' , '0' );
insert into rsicomponent_config values ( '$SUBSYS_ID' , '30.4.5' , '75' , '75' , '0' );
insert into rsicomponent_config values ( '$SUBSYS_ID' , '30.4.6' , '60000' , '60000' , '0' );
insert into rsicomponent_config values ( '$SUBSYS_ID' , '30.4.7' , '60000' , '60000' , '0' );
insert into rsicomponent_config values ( '$SUBSYS_ID' , '30.4.8' , '75' , '75' , '0' );

commit;

insert into rsi_measurement_set (ID,VERSION,PRIORITY,ACCUMULATIONINTERVAL,ENTITYTYPE,MEASTYPE,ENABLE,RESETFLAG ,SUBSYSID)
values ('SAS MeasurementSet Priority Calls',1,0,1800,'OSS',0,'true','true','$SUBSYS_ID');

	BEGIN
		SELECT 1 INTO flag FROM rsi_user_measurement_set WHERE ID='SAS MeasurementSet Priority Calls' AND VERSION=1;
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
        insert into rsi_user_measurement_set (ID,VERSION,ACCUMULATIONINTERVAL,NUMOFCNTRS,CREATIONTIME,STATUS)
        VALUES ('SAS MeasurementSet Priority Calls',1,1800,8,(sysdate - to_date('19700101000000', 'YYYYMMDDHH24MISS') ) * 86400,
0);
        COMMIT;
	END;


insert into rsi_measurement_cntr (ID,REFTYPE,TYPE,MEASMODE,SCANINTERVAL,OID,ENABLE,SUBSYSID)
values ('SAS.Priority INVITEs IN','name','incOnly','event',60,'30.10.253','true','$SUBSYS_ID');

insert into rsi_measurement_cntr (ID,REFTYPE,TYPE,MEASMODE,SCANINTERVAL,OID,ENABLE,SUBSYSID) 
values ('SAS.Priority INVITEs OUT','name','incOnly','event',60,'30.10.254','true','$SUBSYS_ID');

insert into rsi_measurement_cntr (ID,REFTYPE,TYPE,MEASMODE,SCANINTERVAL,OID,ENABLE,SUBSYSID)
values ('SAS.Priority INVITEs Queued But Dropped','name','incOnly','event',60,'30.10.255','true','$SUBSYS_ID');

insert into rsi_measurement_cntr (ID,REFTYPE,TYPE,MEASMODE,SCANINTERVAL,OID,ENABLE,SUBSYSID)
values ('SAS.Priority Unsuccessful Sessions','name','incOnly','event',60,'30.10.256','true','$SUBSYS_ID');

insert into rsi_measurement_cntr (ID,REFTYPE,TYPE,MEASMODE,SCANINTERVAL,OID,ENABLE,SUBSYSID)
values ('SAS.Priority INVITEs Exempted Frm Cngn Ctrl','name','incOnly','event',60,'30.10.257','true','$SUBSYS_ID');

insert into rsi_measurement_cntr (ID,REFTYPE,TYPE,MEASMODE,SCANINTERVAL,OID,ENABLE,SUBSYSID)
values ('SAS.Priority SIP Messages','name','incOnly','event',60,'30.10.258','true','$SUBSYS_ID');

insert into rsi_measurement_cntr (ID,REFTYPE,TYPE,MEASMODE,SCANINTERVAL,OID,ENABLE,SUBSYSID)
values ('SAS.Priority INVITEs Dropped Due To M/C Cngn','name','incOnly','event',60,'30.10.259','true','$SUBSYS_ID');

insert into rsi_measurement_cntr (ID,REFTYPE,TYPE,MEASMODE,SCANINTERVAL,OID,ENABLE,SUBSYSID)
values ('SAS.Priority INVITEs Exempted Frm N/W Mgmt Ctrl','name','incOnly','event',60,'30.10.260','true','$SUBSYS_ID');


insert into rsi_mset_cntr_map (MEASSETID,MEASSETVERSION,MEASCNTRID,SUBSYSID,CONSOLIDATEDMEASSETID,CONSOLIDATEDMEASVERSION) 
values ('SAS MeasurementSet Priority Calls',1,'SAS.Priority INVITEs IN','$SUBSYS_ID','SAS MeasurementSet Priority Calls',1 );

insert into rsi_mset_cntr_map (MEASSETID,MEASSETVERSION,MEASCNTRID,SUBSYSID,CONSOLIDATEDMEASSETID,CONSOLIDATEDMEASVERSION) 
values ('SAS MeasurementSet Priority Calls',1,'SAS.Priority INVITEs OUT','$SUBSYS_ID','SAS MeasurementSet Priority Calls',1 );

insert into rsi_mset_cntr_map (MEASSETID,MEASSETVERSION,MEASCNTRID,SUBSYSID,CONSOLIDATEDMEASSETID,CONSOLIDATEDMEASVERSION)
values ('SAS MeasurementSet Priority Calls',1,'SAS.Priority INVITEs Queued But Dropped','$SUBSYS_ID','SAS MeasurementSet Priority Calls',1 );

insert into rsi_mset_cntr_map (MEASSETID,MEASSETVERSION,MEASCNTRID,SUBSYSID,CONSOLIDATEDMEASSETID,CONSOLIDATEDMEASVERSION)
values ('SAS MeasurementSet Priority Calls',1,'SAS.Priority Unsuccessful Sessions','$SUBSYS_ID', 'SAS MeasurementSet Priority Calls',1 );

insert into rsi_mset_cntr_map (MEASSETID,MEASSETVERSION,MEASCNTRID,SUBSYSID,CONSOLIDATEDMEASSETID,CONSOLIDATEDMEASVERSION)
values ('SAS MeasurementSet Priority Calls',1,'SAS.Priority INVITEs Dropped Due To M/C Cngn','$SUBSYS_ID','SAS MeasurementSet Priority Calls',1 );

insert into rsi_mset_cntr_map (MEASSETID,MEASSETVERSION,MEASCNTRID,SUBSYSID,CONSOLIDATEDMEASSETID,CONSOLIDATEDMEASVERSION)
values ('SAS MeasurementSet Priority Calls',1,'SAS.Priority SIP Messages','$SUBSYS_ID','SAS MeasurementSet Priority Calls',1 );

insert into rsi_mset_cntr_map (MEASSETID,MEASSETVERSION,MEASCNTRID,SUBSYSID,CONSOLIDATEDMEASSETID,CONSOLIDATEDMEASVERSION)
values ('SAS MeasurementSet Priority Calls',1,'SAS.Priority INVITEs Exempted Frm Cngn Ctrl','$SUBSYS_ID','SAS MeasurementSet Priority Calls',1 );

insert into rsi_mset_cntr_map (MEASSETID,MEASSETVERSION,MEASCNTRID,SUBSYSID,CONSOLIDATEDMEASSETID,CONSOLIDATEDMEASVERSION)
values ('SAS MeasurementSet Priority Calls',1,'SAS.Priority INVITEs Exempted Frm N/W Mgmt Ctrl','$SUBSYS_ID','SAS MeasurementSet Priority Calls',1);


commit;
END;
/
_EOF_

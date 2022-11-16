#!/bin/ksh

DB_STRING=$1
SUBSYS_ID=$2
SIG_IP=$3
PEER_MGMT_IP=$4

. profile.test.emsSAS

$ORACLE_HOME/bin/sqlplus $DB_STRING <<  _EOF_

spool asiehi.log

SET SERVEROUTPUT ON

DECLARE
flag Number := 0;

BEGIN

update rsicomponent_config set param_val = '$SIG_IP' , new_param_val = '$SIG_IP'
where component_id ='$SUBSYS_ID' and param_oid = '30.1.24' and subcomponent_id = '0';
	
update rsicomponent_config set param_val = '$PEER_MGMT_IP' , new_param_val = '$PEER_MGMT_IP'
where component_id ='$SUBSYS_ID' and param_oid = '30.1.13' and subcomponent_id = '0';
	
BEGIN
SELECT 1 INTO flag FROM rsicomponent_config WHERE param_oid ='30.4.555' ;
EXCEPTION
	WHEN NO_DATA_FOUND THEN

	insert into rsioid_config values ( '30.4.555' , '30.4' , '16555' , '16555' , '2','1', '0' , '65555' , '0', 'NRC', '0' );
	insert into rsicomponent_config values ( '$SUBSYS_ID' , '30.4.555' , '$SIG_IP' , '$SIG_IP' , '0' );

	update rsicomponent_config set param_val = '$SIG_IP' , new_param_val = '$SIG_IP'
	where component_id ='$SUBSYS_ID' and param_oid = '30.1.31' and subcomponent_id = '0';
	COMMIT;
	return;
END;

update rsicomponent_config set param_val = (select new_param_val from rsicomponent_config where param_oid = '30.4.555') , new_param_val = (select new_param_val from rsicomponent_config where param_oid = '30.4.555')
where component_id ='$SUBSYS_ID' and param_oid = '30.1.31' and subcomponent_id = '0';

update rsicomponent_config set param_val = '$SIG_IP' , new_param_val = '$SIG_IP'
where component_id =(select component_id from rsicomponent_config where param_oid = '30.4.555') and param_oid = '30.1.31';

BEGIN
SELECT 1 INTO flag FROM rsicomponent_config WHERE param_oid ='30.4.555' ;
IF (flag = 1) THEN
delete from rsicomponent_config where param_oid = '30.4.555';
delete from rsioid_config where param_oid = '30.4.555';
END IF;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
    flag :=0; 
END;


commit;
END;
/

_EOF_

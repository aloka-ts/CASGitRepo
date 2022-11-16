#!/bin/ksh

DB_STRING=$1

. profile.test.emsSAS


$ORACLE_HOME/bin/sqlplus $DB_STRING <<  _EOF_

spool rollbcak.log

SET SERVEROUTPUT ON

DECLARE
flag Number := 0;

BEGIN
SELECT 1 INTO flag FROM rsicomponent_config WHERE param_oid ='30.4.555' ;
IF (flag = 1) THEN
delete from rsicomponent_config where param_oid = '30.4.555';
delete from rsioid_config where param_oid = '30.4.555';
END IF;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
    flag :=0; 

commit;
END;
/

_EOF_

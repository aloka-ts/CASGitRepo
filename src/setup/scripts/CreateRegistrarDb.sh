#!/bin/ksh

echo Enter the ORACLE_HOME path

read homeoracle
#export ORACLE_HOME=/user/oracle/OracleClient817/8.1.7/
ORACLE_HOME=$homeoracle
export ORACLE_HOME

echo Enter the ORACLE_BASE path
read baseoracle
#export ORACLE_BASE=$ORACLE_HOME
ORACLE_BASE=$baseoracle
export ORACLE_BASE

LD_LIBRARY_PATH=${homeoracle}/lib

echo Enter username
read username

echo Enter password
read password

echo DBSID
read dbsid

#echo Enter [username] [password] [DBSID] 
 
#$ORACLE_HOME/bin/sqlplus $1/$2@$3  << _EOF_

${homeoracle}/bin/sqlplus $username/$password@$dbsid << _EOF_


@../schema/registrar.schema

_EOF_

echo "Loading complete...."


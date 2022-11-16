#!/bin/ksh

export LD_LIBRARY_PATH=${SAS_ORACLE_HOME}/lib:${SAS_ORACLE_HOME}/rdbms/lib:/usr/ucblib
export PATH=${SAS_ORACLE_HOME}/bin:$PATH

if [[ $MY_HOST == $MACHINE1 ]]
then
	$INSTALL_ROOT/ASESubsystem/schema/registrar.schema $SAS_DB_CONN_STRING
fi

echo "SUCCESS"

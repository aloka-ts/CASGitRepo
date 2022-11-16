#!/bin/ksh

export CUR_DIR=`pwd`

echo
echo "IN Gateway Compilation Script"
echo "All rights Reserverd"
echo "(C)GENBAND Inc."
echo

echo "Setting environment to build INGw ..."

. /vob/Sipservlet/src/build/INGwScript.shrc

$RELROOT/build/INGwScript.sh

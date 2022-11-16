#!/bin/ksh

echo "In Build.sh"

if [ x${INSTALLROOT} = x ]
then
    echo "Please define INSTALLROOT ..."
    exit 1
fi

. /vob/Sipservlet/src/maps/build/build.shrc

$ANT_HOME/bin/ant $@ -DINSTALLROOT=${INSTALLROOT}

echo "Out of Build.sh"

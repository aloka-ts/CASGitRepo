#!/bin/ksh -p

export PATH=/bin:/usr/bin:$PATH

export PLTFRM=`uname`

if [ $# != 4 ]
then
   echo " Invalid Number of arguments "
   echo "Usage :  <LOGDIR> <format> <retention Period> <nsp user> "
   exit 1
fi

if test $PLTFRM = "Linux"
then
   export PLTFRM_DIR=redhat80g
   export CurrPath=`dirname $0`
else
   export PLTFRM_DIR=sol28g
   FullPath=`whence $0`
   export CurrPath=`dirname $FullPath`
fi

cd $CurrPath

export INSTALLROOT="$CurrPath/../../.."

export SUBSYS_DIR=SUBSYS_INGW

#$INSTALLROOT/Common/$PLTFRM_DIR/scripts/logCleaner.sh /LOGS/$SUBSYS_INGW $3 root
# here $1- LOGROOT, $3- retention Period $2- format, CCM $4 - nsp user
$INSTALLROOT/$SUBSYS_INGW/$PLTFRM_DIR/scripts/logCleaner.sh $1 $3 $4 $2
$INSTALLROOT/$SUBSYS_INGW/$PLTFRM_DIR/scripts/logCleaner.sh $1 $3 $4 "ingw*"

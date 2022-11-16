#!/bin/ksh

# here the profile of rsi will be moved to the user home
MY_PATH=`dirname $0`
cd $MY_PATH

PLTFRM=`uname`
export PLTFRM
echo "platform is : $PLTFRM"
if test $PLTFRM = "Linux"
then
PLTFRM_DIR=redhat80g
else
PLTFRM_DIR=sol28g
fi

SUBSYS_DIR=$SUBSYS_INGW

cp ../scripts/profile.ingw ~/

echo "SUCCESS"

#!/bin/ksh -ep

if [[ ${FIRST_ERR_BREAK:-"Not set"} == "Not set" ]]
then
set +e
fi

export CUR_DIR=$PWD

echo " "
echo " "
echo "Building INGw ..."
echo " "
echo " "

set -x

#
# Pre-build 
#
export CUR_DIR=$PWD

mkdir -p $INSTALLROOT/BuildEnv

/usr/atria/bin/cleartool pwv > $INSTALLROOT/BuildEnv/INGw_view_tag
/usr/atria/bin/cleartool catcs > $INSTALLROOT/BuildEnv/INGw_config_specs
env > $INSTALLROOT/BuildEnv/env_INGw
alias > $INSTALLROOT/BuildEnv/alias_INGw


#
# Building INGw
#
export BUILDROOT=${RELROOT}/INGw/src
cd ${BUILDROOT}
imake $IMAKE_OPTIONS CXX=$CCCOMPILER headers

#Making messages as separate lib dependent only on CCMUtil and Util.
#This will aid in testing messages with stubs.

imake $IMAKE_OPTIONS CXX=$CCCOMPILER headers

mkdir -p $INSTALLROOT/sol28g/include/INGwTcapMessage
mkdir -p $INSTALLROOT/sol28g/lib

#This has been commented temporarily - Start
cd ${BUILDROOT}/INGwTcapMessage
make

\rm -f $INSTALLROOT/sol28g/include/INGwTcapMessage/*.hpp
cp ${BUILDROOT}/INGwTcapMessage/INGwTcapMessage/*.hpp $INSTALLROOT/sol28g/include/INGwTcapMessage
cp ${BUILDROOT}/INGwTcapMessage/lib* $INSTALLROOT/sol28g/lib

\rm ${BUILDROOT}/INGwTcapMessage/*.o
#This has been commented temporarily - End

cd ${BUILDROOT}
for COMPONENT in src
do
#   cd ${BUILDROOT}/$COMPONENT
   imake $IMAKE_OPTIONS CXX=$CCCOMPILER
done

# Build ingwProbe
cd ${BUILDROOT}/INGwTelnetScript
make clean
make

cp ${BUILDROOT}/INGwTelnetScript/INGwProbe $INSTALLROOT/sol28g/bin

set +x 

echo " "
echo " "
echo "Building INGw done"
echo " "
echo " "

cd $CUR_DIR

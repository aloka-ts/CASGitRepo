#!/bin/ksh

#
# Function Definition
#
function return_def_ans {
 def_ans=`egrep "^$1" $2 | awk '{print $2}' 2>/dev/null`
 ans=${def_ans:-$3}
 echo $ECHO_PREFIX $ans
}

CURDIR=$PWD
export PATH=/usr/bin:/usr/ccs/bin:$PATH

if [ $# -lt 3 ]
then
	echo "Usage: `basename $0` <BUILDROOT> <RELEASE_NUMBER> <DROP_NUMBER>"
	echo "Example: `basename $0` /home/INGw_BASE 7.5.0 1"
exit 1
fi


#
# Get the Build Directory, Version number for INGw
#
INSTALLROOT=$1
VERSION_NUMBER=$2

TMP_ENV=/tmp/envfile.${EXT}
BUILD_ENV=${INSTALLROOT}/BuildEnv/env_INGw

if [ ! -e $BUILD_ENV ]
then
    read THIRD_PARTY_DIR?"Please enter Thirdparty Directory: "
else
    sed 's/=/ /' $BUILD_ENV > ${TMP_ENV}

    THIRD_PARTY_DIR=`return_def_ans THIRDPARTY $TMP_ENV NONE`
    XERCES_HOME=`return_def_ans XERCES_HOME $TMP_ENV NONE`
    rm -f ${TMP_ENV}
fi

PLTFRM_DIR=sol28g
RELROOT=/vob/Sipservlet/src/INGw
BUILD=SOL
export PKG_DATE=`date +%d%m%y`

# Create package directory
PACKAGE_NAME=INGw${VERSION_NUMBER}
BASEROOT=${INSTALLROOT}/${PACKAGE_NAME}
PACKAGEROOT=${BASEROOT}/${PACKAGE_NAME}

rm -Rf $BASEROOT
mkdir -p ${PACKAGEROOT}
export PACKAGEROOT 
export BASEROOT

#
# copy INGw build.xml
#
cp ${RELROOT}/setup/build.xml ${BASEROOT}
chmod +w ${BASEROOT}/build.xml

# replace the ingw version in build file
sed 's/INGW_VERSION/'$VERSION_NUMBER'/g' ${BASEROOT}/build.xml > ${BASEROOT}/build.xml.1
cp ${BASEROOT}/build.xml.1 ${BASEROOT}/build.xml
rm -f ${BASEROOT}/build.xml.*

#
# Copy INGw template
#

# subsys
cp -f $RELROOT/setup/subsys_tmpl/* ${BASEROOT}
chmod +w ${BASEROOT}/INGw_template.xml

# replace the ingw version in templete file
sed 's/INGW_VERSION/'$VERSION_NUMBER'/g' ${BASEROOT}/INGw_template.xml > ${BASEROOT}/INGw_template.xml.1
sed 's/PLTFRM_DIR/'$PLTFRM_DIR'/g' ${BASEROOT}/INGw_template.xml.1 > ${BASEROOT}/INGw_template.xml.2
cp ${BASEROOT}/INGw_template.xml.2 ${BASEROOT}/INGw_template.xml
rm -f ${BASEROOT}/INGw_template.xml.*

# conf
mkdir -p ${PACKAGEROOT}/${PLTFRM_DIR}/conf_tmpl
mkdir -p ${PACKAGEROOT}/${PLTFRM_DIR}/conf

cp ${RELROOT}/setup/conf_tmpl/* ${PACKAGEROOT}/${PLTFRM_DIR}/conf_tmpl
chmod 777 ${PACKAGEROOT}/${PLTFRM_DIR}/conf_tmpl/*

cp ${RELROOT}/src/INGwStackManager/INGwStackManager/INGwSm_CCM1.xml ${PACKAGEROOT}/${PLTFRM_DIR}/conf/
cp ${RELROOT}/setup/conf_tmpl/ss7_sig_conf.sh ${PACKAGEROOT}/${PLTFRM_DIR}/conf/
cp ${RELROOT}/setup/conf_tmpl/ccpu_ssi_mem.conf ${PACKAGEROOT}/${PLTFRM_DIR}/conf/
chmod 777 ${PACKAGEROOT}/${PLTFRM_DIR}/conf/*

# scripts
mkdir -p ${PACKAGEROOT}/${PLTFRM_DIR}/scripts_tmpl
mkdir -p ${PACKAGEROOT}/${PLTFRM_DIR}/scripts

cp ${RELROOT}/setup/scripts_tmpl/* ${PACKAGEROOT}/${PLTFRM_DIR}/scripts_tmpl
chmod 777 ${PACKAGEROOT}/${PLTFRM_DIR}/scripts_tmpl/*

cp -r ${RELROOT}/src/INGwTelnetScript/INGwProbeScripts ${PACKAGEROOT}/${PLTFRM_DIR}/scripts/
chmod 777 ${PACKAGEROOT}/${PLTFRM_DIR}/scripts/INGwProbeScripts/*

# config scripts
mkdir -p ${PACKAGEROOT}/${PLTFRM_DIR}/configscripts
cp ${RELROOT}/setup/config_scripts/* ${PACKAGEROOT}/${PLTFRM_DIR}/configscripts/
chmod +x ${PACKAGEROOT}/${PLTFRM_DIR}/configscripts/*


#
# Copy Build Information
#
cp -r ${INSTALLROOT}/BuildEnv ${PACKAGEROOT}/${PLTFRM_DIR}/

#
# Copy binaries
#
mkdir -p ${PACKAGEROOT}/${PLTFRM_DIR}/bin
cp ${INSTALLROOT}/${PLTFRM_DIR}/bin/INGw* ${PACKAGEROOT}/${PLTFRM_DIR}/bin
chmod 777 ${PACKAGEROOT}/${PLTFRM_DIR}/bin/INGw*

#
# Copy libraries
#
mkdir -p ${PACKAGEROOT}/${PLTFRM_DIR}/lib
cp ${INSTALLROOT}/${PLTFRM_DIR}/lib/libINGw* ${PACKAGEROOT}/${PLTFRM_DIR}/lib
cp ${INSTALLROOT}/${PLTFRM_DIR}/lib/libTcapMessage.so ${PACKAGEROOT}/${PLTFRM_DIR}/lib
cp /home/mriganka/ccpu_20Oct_Base_Build_Mriganka/ccpu_base_27Jun/lib/libstk_ftha_witrc_widbg.so  ${PACKAGEROOT}/${PLTFRM_DIR}/lib
cp /home/mriganka/ccpu_20Oct_Base_Build_Mriganka/ccpu_base_27Jun/lib/libstk_ftha.so  ${PACKAGEROOT}/${PLTFRM_DIR}/lib

#Create Common libs directory, Xerces libs
COMMON_LIB_DIR=${PACKAGEROOT}/${PLTFRM_DIR}/CommonLib
mkdir -p ${COMMON_LIB_DIR}/EmsLib

cp ${INSTALLROOT}/${PLTFRM_DIR}/lib/libEmsAgent.so ${COMMON_LIB_DIR}/EmsLib/
cp ${INSTALLROOT}/${PLTFRM_DIR}/lib/libUtil.so ${COMMON_LIB_DIR}/EmsLib/
cp ${INSTALLROOT}/${PLTFRM_DIR}/lib/libEmsCommon.so ${COMMON_LIB_DIR}/EmsLib/
cp ${INSTALLROOT}/${PLTFRM_DIR}/lib/libEmsIdl.so ${COMMON_LIB_DIR}/EmsLib/

#copy TAO 
mkdir -p ${COMMON_LIB_DIR}/TaoLib/

export ACE_ROOT="${INSTALLROOT}/${PLTFRM_DIR}/lib/thirdParty/TAO/ACE/ACE_wrappers/ace/"
echo "ACE_ROOT: ${ACE_ROOT}"

#cp ${THIRD_PARTY_DIR}/TAO/ACE/ACE_wrappers/ace/*.so ${COMMON_LIB_DIR}/TaoLib
#cp ${THIRD_PARTY_DIR}/TAO/ACE/ACE_wrappers/ace/libACE*.so.* ${COMMON_LIB_DIR}/TaoLib

cp ${ACE_ROOT}/libACE*.so* ${COMMON_LIB_DIR}/TaoLib
cp ${ACE_ROOT}/libTAO*.so* ${COMMON_LIB_DIR}/TaoLib

# copy other lib
mkdir -p ${COMMON_LIB_DIR}/OtherLib

cp ${XERCES_HOME}/lib/lib*28  ${COMMON_LIB_DIR}/OtherLib

# 
# Create Tar ball
#
cd ${BASEROOT}
tar cf ingw.tar ${PACKAGE_NAME}

\rm -fR ${PACKAGE_NAME}

mkdir ${PACKAGE_NAME}

mv ingw.tar INGw_template.xml ${PACKAGE_NAME}

cp ${RELROOT}/setup/config_scripts/config-setup.xml* ${PACKAGE_NAME}/

cd ..
#tar cf ${PACKAGE_NAME}.tar ${BASEROOT}
tar cf ${BUILD}_${PACKAGE_NAME}_${PKG_DATE}.tar ${PACKAGE_NAME}
gzip ${BUILD}_${PACKAGE_NAME}_${PKG_DATE}.tar

\rm -fR ${BASEROOT}

echo "\033[1mINGw packaged successfully. \033[0m"
echo "\033[1mTarball [${BUILD}_${PACKAGE_NAME}_${PKG_DATE}.tar.gz] available at $PWD.\033[0m"


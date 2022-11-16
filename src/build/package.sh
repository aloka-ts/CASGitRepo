#!/bin/ksh +x

# Usage       - /bin/ksh $0 [INSTALLROOT] [SAS_VERSION] [CAS_THIRDPARTY_ROOT]
#
# File name   - package.sh
#
# Description - Utility to create the patch of CAS
# 				[THIRDPARTY_ROOT] - Contains the Thirdparty required by CAS Server
#
# Author	  - Ankit Singhal

. ./ant.properties


bold=`tput bold`
offbold=`tput rmso`

if [ $# -ne 3 ]
then
    echo "${bold}Usage: package.sh [CAS-INSTALLROOT] [CAS_VERSION] [CAS_THIRDPARTY_ROOT] ${offbold}"
    exit 1
fi


INSTALLROOT=$1
VERSION_NUMBER=$2
THIRDPARTY=$3

if [ ! -d "$INSTALLROOT" ]
then
    echo "${bold} SAS INSTALLROOT not found... ${offbold} "
    exit
fi

if [ ! -d "$THIRDPARTY" ]
then
    echo "${bold} [CAS_THIRDPARTY_ROOT] not found... ${offbold} "
    exit
fi

ASE_VERSION=${VERSION_NUMBER}
PACKAGE_NAME=SipServlet${VERSION_NUMBER}
PACKAGE_ROOT=${INSTALLROOT}/${PACKAGE_NAME}
ASE_ROOT=${PACKAGE_ROOT}/ASE${ASE_VERSION}

# Creation of the packaging directory structure
mkdir -p ${ASE_ROOT}
mkdir -p ${PACKAGE_ROOT}/CDR1
mkdir -p ${PACKAGE_ROOT}/CDR2
chmod +w ${ASE_ROOT}
# Adding utilities dir
mkdir -p ${PACKAGE_ROOT}/utility

# Changing the scripts permissions to 777
chmod 777 ${INSTALLROOT}/setup/ase/*
chmod 777 ${INSTALLROOT}/conf/*
chmod 755 ${INSTALLROOT}/scripts/*

# Changing the utility scripts permissions 
chmod 755 -R ${INSTALLROOT}/utility/*

mkdir -p $INSTALLROOT/jndiprovider/fileserver

echo "Copying third party files to install root directory..."
mkdir -p $INSTALLROOT/Common/thirdParty/TAO/JacOrb/JacORB
cp -r $THIRDPARTY/TAO/JacOrb/Jacorb1_4_1/bin $INSTALLROOT/Common/thirdParty/TAO/JacOrb/JacORB/.
cp -r $THIRDPARTY/TAO/JacOrb/Jacorb1_4_1/lib $INSTALLROOT/Common/thirdParty/TAO/JacOrb/JacORB/.

mkdir -p $INSTALLROOT/Common/thirdParty/jakarta-tomcat
cp -r $THIRDPARTY/webserver/apache-tomcat-7.0.30/bin $INSTALLROOT/Common/thirdParty/jakarta-tomcat
cp -r $THIRDPARTY/webserver/apache-tomcat-7.0.30/lib $INSTALLROOT/Common/thirdParty/jakarta-tomcat
cp -r $THIRDPARTY/webserver/apache-tomcat-7.0.30/conf $INSTALLROOT/Common/thirdParty/jakarta-tomcat
#cp -r $THIRDPARTY/webserver/apache-tomcat-7.0.30/logs $INSTALLROOT/Common/thirdParty/jakarta-tomcat
#cp -r $THIRDPARTY/webserver/apache-tomcat-7.0.30/temp $INSTALLROOT/Common/thirdParty/jakarta-tomcat
cp -r $THIRDPARTY/webserver/apache-tomcat-7.0.30/webapps $INSTALLROOT/Common/thirdParty/jakarta-tomcat
#cp -r $THIRDPARTY/webserver/apache-tomcat-7.0.30/work $INSTALLROOT/Common/thirdParty/jakarta-tomcat
###cp -r $THIRDPARTY/webserver/jakarta-tomcat-5.0.28/* $INSTALLROOT/Common/thirdParty/jakarta-tomcat Commented for NSEP Tomcat version change

mkdir -p $INSTALLROOT/Common/thirdParty/apache-activemq-5.3.0
cp -r $THIRDPARTY/apache/apache-activemq-5.3.0/bin $INSTALLROOT/Common/thirdParty/apache-activemq-5.3.0
cp -r $THIRDPARTY/apache/apache-activemq-5.3.0/conf $INSTALLROOT/Common/thirdParty/apache-activemq-5.3.0
cp -r $THIRDPARTY/apache/apache-activemq-5.3.0/lib $INSTALLROOT/Common/thirdParty/apache-activemq-5.3.0
cp -r $THIRDPARTY/apache/apache-activemq-5.3.0/README.txt $INSTALLROOT/Common/thirdParty/apache-activemq-5.3.0

## Copy the XML files into the relevant locations.
echo "Copying the template files....."
cp ${INSTALLROOT}/setup/build.xml ${PACKAGE_ROOT}/.
cp ${INSTALLROOT}/setup/upgrade.xml ${PACKAGE_ROOT}/.
cp ${INSTALLROOT}/setup/ase/ASE_template.xml ${ASE_ROOT}/.
cp ${INSTALLROOT}/setup/ase/ASE_template_emsl.xml ${ASE_ROOT}/.
cp ${INSTALLROOT}/setup/ase/ASE_upgrade_template.xml ${ASE_ROOT}/.

## Replacement of the KEYWORDS in the XML files
for file in ${PACKAGE_ROOT}/build.xml ${PACKAGE_ROOT}/upgrade.xml ${ASE_ROOT}/ASE_template.xml ${ASE_ROOT}/ASE_template_emsl.xml ${ASE_ROOT}/ASE_upgrade_template.xml ${INSTALLROOT}/scripts/ase_no_ems
do
	echo "Replacing the keyword(s) in file :" $file
	sed 's/ASE_VERSION/'${ASE_VERSION}'/g' $file > $file.tmp
	mv $file.tmp $file

	sed 's/PREV_VERSION/'${UPGRADABLE_VERSIONS}'/g' $file > $file.tmp
	mv $file.tmp $file
	chmod 755 $file
done;

# Copy the non ems installer files to the package.
cp ${INSTALLROOT}/setup/ase/nems_installer/* ${PACKAGE_ROOT}

# Change the permissions on the all the installer scripts to 755
cd ${PACKAGE_ROOT}
chmod 755 *

# Prepairing the exclude list for the ASE tar file
cd ${INSTALLROOT}
echo ${PACKAGE_NAME} > exclude.lst
echo exclude.lst >> exclude.lst
echo ide >> exclude.lst
# adding utility to remove list
echo utility >> exclude.lst
echo bpjars/mph-data.jar >> exclude.lst

# Making the ASE tar
tar cvfX ${ASE_ROOT}/ase.tar exclude.lst *
rm ${INSTALLROOT}/exclude.lst
cd ${ASE_ROOT}
tar xvf ${ASE_ROOT}/ase.tar
cd ..
tar cvf ${ASE_ROOT}/ase.tar ASE${ASE_VERSION}
cd ${INSTALLROOT}

# Making the utility tar
cd ${INSTALLROOT}/utility
tar cvf ${PACKAGE_ROOT}/utility/utility.tar *
cd ${INSTALLROOT}

## remove ase.tar ##
rm -rf ${ASE_ROOT}/ase.tar

# Remove extra files
#rm -rf ${ASE_ROOT}/camelAppClasses
#rm -rf ${ASE_ROOT}/camelAppBuild
#rm -rf ${ASE_ROOT}/bpjars
#rm -rf ${ASE_ROOT}/Common
#rm -rf ${ASE_ROOT}/conf
#rm -rf ${ASE_ROOT}/scripts
#rm -rf ${ASE_ROOT}/dsjars
#rm -rf ${ASE_ROOT}/httpjars
#rm -rf ${ASE_ROOT}/lib
#rm -rf ${ASE_ROOT}/otherjars
#rm -rf ${ASE_ROOT}/ra
#rm -rf ${ASE_ROOT}/schema
#rm -rf ${ASE_ROOT}/setup
#rm -rf ${ASE_ROOT}/soapserver
#rm -rf ${ASE_ROOT}/sysapps
#rm -rf ${ASE_ROOT}/test-apps
#rm -rf ${ASE_ROOT}/tools
#rm -rf ${ASE_ROOT}/AlcClasses
#rm -rf ${ASE_ROOT}/dsclasses
#rm -rf ${ASE_ROOT}/alcjars
#rm -rf ${ASE_ROOT}/doc
#rm -rf ${ASE_ROOT}/TcapClasses
#rm -rf ${ASE_ROOT}/jndiprovider
#rm -rf ${ASE_ROOT}/xsd
#rm -rf ${ASE_ROOT}/sbb
#rm -rf ${ASE_ROOT}/msml
#rm -rf ${ASE_ROOT}/jmxClient

# Commenting this as this creates problem in installing SAS with
# EMS Unified installer. But due to this change SAS non-ems installer
# will not work and one need to move ase.tar one directory up and 
# extract it
## Making the package zip 
#if [[ $3 != "C" ]]
#then
#  echo "exploding ase.tar"
#  cd ${PACKAGE_ROOT}
#  mv -f ${ASE_ROOT}/ase.tar .
#  rm -rf ASE${ASE_VERSION} #remove the junk version
#  tar xf ase.tar
#  rm -f ase.tar #dont need it
#  cd ${INSTALLROOT}
#  zip -rq ${PACKAGE_NAME}.zip ${PACKAGE_NAME}
#  rm -rf ${PACKAGE_NAME}
#fi

# Making the package tar
if [[ $3 != "C" ]]
then
tar cvf ${PACKAGE_NAME}.tar ${PACKAGE_NAME}
rm -rf ${PACKAGE_NAME}
gzip ${PACKAGE_NAME}.tar
fi

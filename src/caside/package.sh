#*******************************************************************************
#   Copyright (c) 2014 Agnity, Inc. All rights reserved.
#   
#   This is proprietary source code of Agnity, Inc. 
#   
#   Agnity, Inc. retains all intellectual property rights associated 
#   with this source code. Use is subject to license terms.
#   
#   This source code contains trade secrets owned by Agnity, Inc.
#   Confidentiality of this computer program must be maintained at 
#   all times, unless explicitly authorized by Agnity, Inc.
#*******************************************************************************
#!/bin/ksh

. ../build/ant.properties

INSTALLROOT=$1
VERSION_NUMBER=$2

PACKAGE_ROOT=$INSTALLROOT/ide/plugins
PACKAGE_NAME=CAS_IDE_PLUGIN_${VERSION_NUMBER}

#mapping version number with plugin bundle number
if (! test -f ${INSTALLROOT}/ide/META-INF/MANIFEST.MF )
then
	echo "Could not find ${INSTALLROOT}/ide/META-INF/MANIFEST.MF file of the ide"
	exit 1;
fi
if (! test -w ${INSTALLROOT}/ide/META-INF/MANIFEST.MF )
then
	echo "${INSTALLROOT}/ide/META-INF/MANIFEST.MF file is write protected!"
	exit 2;
fi

sed "s/Bundle-Version.*/Bundle-Version\: ${VERSION_NUMBER}/" ${INSTALLROOT}/ide/META-INF/MANIFEST.MF > /tmp/tmp_$$

if ( test $? -ne 0 )
then
	echo "Got an error while working on MANIFEST.MF"
	exit 3;
fi
cp /tmp/tmp_$$ ${INSTALLROOT}/ide/META-INF/MANIFEST.MF
rm -f /tmp/tmp_$$

#PLUGIN_NAME=com.baypackets.sas.ide_1.0.0
PLUGIN_NAME=com.baypackets.sas.ide_${VERSION_NUMBER}
PLUGIN_DIR=${PACKAGE_ROOT}/${PLUGIN_NAME}

### Cleanup the previous packaging contents....
rm -rf ${PLUGIN_DIR}
rm -rf ${INSTALLROOT}/${PACKAGE_NAME}.zip
#rm -rf ${INSTALLROOT}/${PACKAGE_NAME}.tar.gz

### Create the directories if not there
mkdir -p ${PACKAGE_ROOT}
mkdir -p ${PLUGIN_DIR}

## Copy the CAS plug-in files to the PACKAGE DIR....
cp -r ${INSTALLROOT}/ide/bin ${PLUGIN_DIR}
cp -r ${INSTALLROOT}/ide/conf ${PLUGIN_DIR}
cp -r ${INSTALLROOT}/ide/icons ${PLUGIN_DIR}
cp -r ${INSTALLROOT}/ide/library ${PLUGIN_DIR}
cp -r ${INSTALLROOT}/ide/resources ${PLUGIN_DIR}
cp -r ${INSTALLROOT}/ide/META-INF ${PLUGIN_DIR}
cp -r ${INSTALLROOT}/ide/build.properties ${PLUGIN_DIR}
cp -r ${INSTALLROOT}/ide/toc.xml ${PLUGIN_DIR}
cp -r ${INSTALLROOT}/ide/plugin.xml ${PLUGIN_DIR}
### Unzip the Help files....

cd ${PLUGIN_DIR}/resources/help

cd servlets
unzip -o servlet-2.4.zip

cd ../sbb/
unzip -o bpsbb.zip

cd ../sipservlets/
unzip -o sipservlet.zip

cd ../sasmanagement
unzip -o sasmanagement.zip

cd ../servicemanagement
unzip -o servicemanagement.zip

cd ../sampleapps
unzip -o sampleapps.zip

cd ../gettingstarted
unzip -o gettingstarted.zip


cd ${INSTALLROOT}/ide

### Package the Plug-in TAR File.
zip -rq ${INSTALLROOT}/${PACKAGE_NAME}.zip plugins
rm -rf plugins 

cd ${INSTALLROOT} 
echo "CAS IDE Packaging Complete. Unzip and Untar it under in eclipse home directory for installing the plug-ins. "

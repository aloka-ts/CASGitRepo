#!/bin/ksh +x

if [ $# -lt 2 ]
then
  echo "Usage: `basename $0` <BUILDROOT> <RELEASE_NUMBER>"
  echo "Example: `basename $0` /home/SAS_BASE 6.0.0.6"
  exit 1
fi

INSTALLROOT=$1
VERSION_NUMBER=$2

MAPS_VERSION=${VERSION_NUMBER}
PACKAGE_NAME=MAPS${VERSION_NUMBER}

#Remove previous packaging  files
rm -f ${INSTALLROOT}/${PACKAGE_NAME}.zip

#Changing scripts permissions
chmod 755 ${INSTALLROOT}/MmAppProvServer/scripts/*

#Making PACKAGE_NAME.zip 
cd ${INSTALLROOT}/MmAppProvServer

echo "The current directory is `pwd` "

zip -rq ${PACKAGE_NAME}.zip *
mv -f ${PACKAGE_NAME}.zip ${INSTALLROOT} 

cd ${INSTALLROOT}


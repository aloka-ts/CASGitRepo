#!/bin/ksh +x

#. ./ant.properties

INSTALLROOT=$1
VERSION_NUMBER=$2

#JACORB_VERSION="JacORB-2.2.4"
JACORB_VERSION="Jacorb1_4_1"
MAPS_VERSION=${VERSION_NUMBER}
PACKAGE_NAME=MAPS${VERSION_NUMBER}
PACKAGE_ROOT=${INSTALLROOT}/${PACKAGE_NAME}
MAPS_ROOT=${PACKAGE_ROOT}/MmAppProvServer${MAPS_VERSION}

# Creation of the packaging directory structure
mkdir -p ${MAPS_ROOT}
chmod +w ${MAPS_ROOT}


# Changing the scripts permissions to 777
cp ${INSTALLROOT}/install/build.xml ${PACKAGE_ROOT}/.
cp ${INSTALLROOT}/install/maps/MAPS_template.xml ${MAPS_ROOT}/.
cp ${INSTALLROOT}/install/setup-config/* ${PACKAGE_ROOT}/.


#update version in scripts
cd ${INSTALLROOT}/MmAppProvServer/conf
	sed 's/JACVERSION/'${JACORB_VERSION}'/g' setup_template > setup_template_1
	mv -f setup_template_1 setup_template
cd -

for file in ${PACKAGE_ROOT}/build.xml ${MAPS_ROOT}/MAPS_template.xml ${INSTALLROOT}/MmAppProvServer/scripts/mapsUtils.sh ${INSTALLROOT}/MmAppProvServer/scripts/configMAPSData.sh 
do
	echo "Replacing the keyword(s) in file :" $file
	sed 's/mapsVersion/'${MAPS_VERSION}'/g' $file > $file.tmp
	mv $file.tmp $file

done;

#Changing scripts permissions
chmod 777 ${INSTALLROOT}/MmAppProvServer/conf/*
chmod 755 ${INSTALLROOT}/MmAppProvServer/scripts/*
chmod 755 ${INSTALLROOT}/MmAppProvServer/bin/*
chmod 755 ${INSTALLROOT}/MmAppProvServer/thirdParty/Jacorb1_4_1/bin/*

#Updating MAPS folder to MAPS Version

mv -f ${INSTALLROOT}/MmAppProvServer ${INSTALLROOT}/MmAppProvServer${MAPS_VERSION}

# Making MAPS tar

cd ${INSTALLROOT}
tar cvf ${MAPS_ROOT}/maps.tar MmAppProvServer${MAPS_VERSION}

# Making the package tar if not for Combined maps and sas
if [[ $3 != "C" ]]
then
  tar cf ${PACKAGE_NAME}.tar ${PACKAGE_NAME}
  rm -rf ${PACKAGE_NAME}
  gzip ${PACKAGE_NAME}.tar
fi

#reseting it...
mv -f ${INSTALLROOT}/MmAppProvServer${MAPS_VERSION} ${INSTALLROOT}/MmAppProvServer


#!/bin/ksh

. ../build/ant.properties
echo "Note: Specify 3rd argument as Studio type studio-win or studio-lnx"
INSTALLROOT=$1
VERSION_NUMBER=$2
PACKAGE_TYPE=$3

if [ $3 == "" ]; then
  echo "Please Specify Studio type studio-win or studio-lnx"
  exit 0;
fi


PACKAGE_ROOT=$INSTALLROOT/maps-ide/plugins
PACKAGE_NAME=MAPS_IDE_PLUGIN_${VERSION_NUMBER}

#mapping version number with plugin bundle number
if (! test -f ${INSTALLROOT}/maps-ide/META-INF/MANIFEST.MF )
then
        echo "Could not find ${INSTALLROOT}/maps-ide/META-INF/MANIFEST.MF file of the ide"
        exit 1;
fi
if (! test -w ${INSTALLROOT}/maps-ide/META-INF/MANIFEST.MF )
then
        echo "${INSTALLROOT}/maps-ide/META-INF/MANIFEST.MF file is write protected!"
        exit 2;
fi

sed "s/Bundle-Version.*/Bundle-Version\: ${VERSION_NUMBER}/" ${INSTALLROOT}/maps-ide/META-INF/MANIFEST.MF > /tmp/tmp_$$

if ( test $? -ne 0 )
then
        echo "Got an error while working on MANIFEST.MF"
        exit 3;
fi
cp /tmp/tmp_$$ ${INSTALLROOT}/maps-ide/META-INF/MANIFEST.MF
rm -f /tmp/tmp_$$


PLUGIN_NAME=com.genband.m5.maps_${VERSION_NUMBER}
PLUGIN_DIR=${PACKAGE_ROOT}/${PLUGIN_NAME}

### Cleanup the previous packaging contents....
rm -rf ${PLUGIN_DIR}

if [ $3 == "studio-win" ]; then
rm -rf ${INSTALLROOT}/${PACKAGE_NAME}.zip
fi

if [ $3 == "studio-lnx" ]; then
rm -rf ${INSTALLROOT}/${PACKAGE_NAME}.zip
fi


### Create the directories if not there
mkdir -p ${PACKAGE_ROOT}
mkdir -p ${PLUGIN_DIR}

## Copy the SAS plug-in files to the PACKAGE DIR....
cp -r ${INSTALLROOT}/maps-ide/bin ${PLUGIN_DIR}
cp -r ${INSTALLROOT}/maps-ide/icons ${PLUGIN_DIR}
cp -r ${INSTALLROOT}/maps-ide/library ${PLUGIN_DIR}
cp -r ${INSTALLROOT}/maps-ide/resources ${PLUGIN_DIR}
cp -r ${INSTALLROOT}/maps-ide/META-INF ${PLUGIN_DIR}
cp -r ${INSTALLROOT}/maps-ide/build.properties ${PLUGIN_DIR}
cp -r ${INSTALLROOT}/maps-ide/plugin.xml ${PLUGIN_DIR}
#### Unzip the JBoss IDE files into the package root....
cd ${INSTALLROOT}/maps-ide

echo "Going to package for Studio"
 echo "The Value of Studio Type" 
 echo $3 
if [ $3 == "studio-win" ]; then
   echo "Going to package for Studio-win" 
   zip -rq ${INSTALLROOT}/${PACKAGE_NAME}.zip plugins
   rm -rf plugins
fi

if [ $3 == "studio-lnx" ]; then 
    echo "Going to package for Studio-lnx"  
    zip -rq ${INSTALLROOT}/${PACKAGE_NAME}.zip plugins
    rm -rf plugins
fi 

cd ${INSTALLROOT} 
echo "M5 Maps IDE Packaging Complete. Unzip and Untar it under in eclipse home directory for installing the plug-ins. "

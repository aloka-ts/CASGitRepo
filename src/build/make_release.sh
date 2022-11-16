#!/bin/bash

vprint()
{
   # Print or not depending on global "$verbosity"
   # Change the verbosity with a single variable.
   # Arg. 1 is the level for this message.
   level=$1; shift
   if [[ $level -le $verbosity ]]; then
      echo $*
   fi
}

die()
{
   # Print an error message and exit with given status
   # call as: die status "message" ["message" ...]
   exitstat=$1; shift
   for i in "$@"; do
      echo "$i"
   done
   exit $exitstat
}

verbosity=2

if [ $# -lt 3 ]
then
  echo "Usage: `basename $0` <BUILDROOT> <RELEASE_NUMBER> <DROP_NUMBER> [<PKG_FLAG>]"
  echo "Example: `basename $0` /home/SAS_BASE 6.0.0 6 1"
  exit 1
fi

PKG_FLAG=1

export BLDROOT=$1
VERSION=$2 #Release Number
DROP=$3 #Drop number
PKG_FLAG=$4

export COMMON_BUILD=/user/integ/SOL_COM6.0.10.2_080408/SOL_COM6.0.10.2_070408.tar.gz
export PKG_DATE=`/bin/date +%d%m%y`
export REL_PACKAGE="M5_SAS${VERSION}.${DROP}"
export REL_STUDIO_PACKAGE="M5_SAS_STUDIO${VERSION}.${DROP}"

mkdir -p $BLDROOT

#### Build SAS Container #####
echo ""
echo "#### Build SAS Container #####"
echo ""

cd /vob/Sipservlet/src/build
./build.sh -DINSTALLROOT=$BLDROOT/SAS | tee sas_build.logs
[ $? -eq 0 ] || die 1 "SAS container build returned $?" "SAS Build Failed. Exiting ..."

./build.sh -DINSTALLROOT=$BLDROOT/SAS ra-sh | tee sas_shbuild.logs
./build.sh -DINSTALLROOT=$BLDROOT/SAS ra-rf | tee sas_rfbuild.logs
./build.sh -DINSTALLROOT=$BLDROOT/SAS ra-ro | tee sas_robuild.logs
./build.sh -DINSTALLROOT=$BLDROOT/SAS ra-smpp | tee sas_smppbuild.logs

if [ $PKG_FLAG == 1 ]
then
	./package.sh $BLDROOT/SAS ${VERSION} C | tee sas_package.logs
	[ $? -eq 0 ] || die 1 "SAS container packaging returned $?" "SAS Build Failed. Exiting ..."
fi

#### Build MAPS #####
echo ""
echo "#### Build MAPS #####"
echo ""

cd /vob/Sipservlet/src/maps/build
export INSTALLROOT=$BLDROOT/MAPS
./build.sh | tee maps_server_build.logs
[ $? -eq 0 ] || die 1 "MAPS server build returned $?" "MAPS Build Failed. Exiting ..."

if [ $PKG_FLAG == 1 ]
then
	./package.sh $BLDROOT/MAPS ${VERSION} C | tee maps_server_package.logs
	[ $? -eq 0 ] || die 1 "MAPS server packaging returned $?" "MAPS Build Failed. Exiting ..."
fi


if [ $PKG_FLAG == 1 ]
then
	#### Packaging MAPS and SAS #####
	echo ""
	echo "#### #####"
	echo ""

	PACKAGE_NAME=SipServlet${VERSION}
	PACKAGE_ROOT=${BLDROOT}/SAS/${PACKAGE_NAME}
	ASE_ROOT=${PACKAGE_ROOT}/ASE${VERSION}

	cd ${BLDROOT}/MAPS
	MAPS_PACKAGE_NAME=MAPS${VERSION}
	MAPS_ELEM=${MAPS_PACKAGE_NAME}/MmAppProvServer${VERSION}
	echo "Moving ${MAPS_ELEM} ${PACKAGE_ROOT} current dir: `pwd`"
	mv ${MAPS_ELEM} ${PACKAGE_ROOT}

	cd $BLDROOT/SAS
	echo "To tar SAS release ball ${PACKAGE_NAME}.tar ${PACKAGE_NAME} current dir: `pwd`"
	tar cvf ${PACKAGE_NAME}.tar ${PACKAGE_NAME}
	rm -rf ${PACKAGE_NAME}
	gzip ${PACKAGE_NAME}.tar
fi

#### Build SAS Container Evaluation Version & SAS STUDIO #####
echo ""
echo "#### Build SAS Container Evaluation Version & SAS STUDIO #####"
echo ""

cd /vob/Sipservlet/src/build
./build.sh -DINSTALLROOT=$BLDROOT/SAS_STUDIO -DEVAL=true | tee saseval_builds.logs
[ $? -eq 0 ] || die 1 "SAS Studio build returned $?" "SAS Studio Build Failed. Exiting ..."

./build.sh -DINSTALLROOT=$BLDROOT/SAS_STUDIO ide | tee saseval_studio.logs
[ $? -eq 0 ] || die 1 "SAS plugin build returned $?" "SAS Studio Build Failed. Exiting ..."

cd /vob/Sipservlet/src/ide
./package.sh $BLDROOT/SAS_STUDIO ${VERSION}
[ $? -eq 0 ] || die 1 "SAS plugin packaging returned $?" "SAS Studio Build Failed. Exiting ..."

cd /vob/Sipservlet/src/build
./package.sh $BLDROOT/SAS_STUDIO ${VERSION}E | tee saseval_package.logs
[ $? -eq 0 ] || die 1 "SAS Studio packaging returned $?" "SAS Studio Build Failed. Exiting ..."

#### Build MAPS Studio Server #####
echo ""
echo "#### Build MAPS Studio Server #####"
echo ""

cd /vob/Sipservlet/src/maps/build
export INSTALLROOT=$BLDROOT/MAPS_STUDIO
./build.sh -DSTUDIO=yes | tee maps_studio_build.logs
[ $? -eq 0 ] || die 1 "MAPS Studio build returned $?" "MAPS Studio Build Failed. Exiting ..."

./package_studio.sh $BLDROOT/MAPS_STUDIO ${VERSION}.${DROP} | tee maps_studio_package.logs
[ $? -eq 0 ] || die 1 "MAPS Studio packaging returned $?" "MAPS Studio Build Failed. Exiting ..."


#### Build MAPS Studio Plug-ins #####
echo ""
echo "#### Build MAPS Studio Plug-ins #####"
echo ""

cd /vob/Sipservlet/src/maps/build
export INSTALLROOT=$BLDROOT/MAPS_STUDIO
./build.sh -DSTUDIO=yes maps-ide | tee maps_plugins_build.logs
[ $? -eq 0 ] || die 1 "MAPS plugin build returned $?" "MAPS Studio Build Failed. Exiting ..."

cd /vob/Sipservlet/src/maps/ide/build
./package.sh $BLDROOT/MAPS_STUDIO ${VERSION}.${DROP} studio-win | tee maps_plugins_package.logs
[ $? -eq 0 ] || die 1 "MAPS plugin build returned $?" "MAPS Studio Build Failed. Exiting ..."


#### Build INGw #####
echo ""
echo "#### Build INGw #####"
echo ""

mkdir -p $BLDROOT/INGW
cd $BLDROOT/INGW
gunzip -c $COMMON_BUILD | tar xf -
cd /vob/Sipservlet/src/build
./build.sh INGw -DINSTALLROOT=$BLDROOT/INGW | tee ingw_build.logs
[ $? -eq 0 ] || die 1 "INGW build returned $?" "INGW Build Failed. Exiting ..."

if [ $PKG_FLAG == 1 ]
then
	./packageINGw.sh $BLDROOT/INGW ${VERSION}.${DROP} | tee ingw_package.logs
	[ $? -eq 0 ] || die 1 "INGW packaging returned $?" "INGW Build Failed. Exiting ..."

fi

#### End Build INGw #####

#### Package Studio #####

cd $BLDROOT

zip -qr ${REL_STUDIO_PACKAGE}_${PKG_DATE}.zip MAPS_STUDIO/MAPS${VERSION}.${DROP}.zip MAPS_STUDIO/MAPS_IDE_PLUGIN_${VERSION}.${DROP}.zip SAS_STUDIO/SipServlet${VERSION}E.zip SAS_STUDIO/SAS_IDE_PLUGIN_${VERSION}.zip
    
echo ""
echo "Tarball \033[1m[$BLDROOTS/${REL_STUDIO_PACKAGE}_${PKG_DATE}.zip]\033[0m is available for release."

#### End Package Studio #####

if [ $PKG_FLAG == 1 ]
then
	echo ""
	echo "#### Create Package #####"
	echo ""

	cd $BLDROOT
	tar cf ${REL_PACKAGE}_${PKG_DATE}.tar SAS/*.tar.gz INGW/*.tar.gz
	gzip ${REL_PACKAGE}_${PKG_DATE}.tar
        
	echo ""
	echo "Tarball \033[1m[$BLDROOT/${REL_PACKAGE}_${PKG_DATE}.tar.gz]\033[0m is available for release."

fi

echo "Execution of make_release.sh completed @ " `/bin/date`

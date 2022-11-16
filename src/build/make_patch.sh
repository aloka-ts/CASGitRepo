#!/bin/ksh

if [ $# -lt 2 ]
then
  echo "Usage: `basename $0` BUILDROOT PATCH_NUMBER"
  echo "Example: `basename $0` /home/SAS_BASE 1"
  exit 1
fi

export BLDROOT=$1
export VERSION=$2
CURRDIR=`pwd`
export PKG_DATE=`/bin/date +%d%m%y`
export REL_PACKAGE="M5_SAS"$VERSION"_"$PKG_DATE
echo $REL_PACKAGE

# remove already existing package, if any
# and create new one.
if test -s $BLDROOT/$REL_PACKAGE
then 
	echo ""
	\rm -fR $BLDROOT/$REL_PACKAGE
fi

mkdir -p $BLDROOT/$REL_PACKAGE/SAS
mkdir -p $BLDROOT/$REL_PACKAGE/MAPS
mkdir -p $BLDROOT/$REL_PACKAGE/INGW

## Copy ApplySasPatch.sh and RollbackSasPatch.sh
cp /vob/Sipservlet/src/build/ApplySasPatch.sh $BLDROOT/$REL_PACKAGE/
cp /vob/Sipservlet/src/build/RollbackSasPatch.sh $BLDROOT/$REL_PACKAGE/

chmod -fR 755 $BLDROOT/$REL_PACKAGE/*.sh

## Update Version 
sed 's/REPLACE_VERSION/'$VERSION'/g' $BLDROOT/$REL_PACKAGE/ApplySasPatch.sh > $BLDROOT/$REL_PACKAGE/ApplySasPatch.sh.1
sed 's/REPLACE_VERSION/'$VERSION'/g' $BLDROOT/$REL_PACKAGE/RollbackSasPatch.sh > $BLDROOT/$REL_PACKAGE/RollbackSasPatch.sh.1

mv -f $BLDROOT/$REL_PACKAGE/ApplySasPatch.sh.1 $BLDROOT/$REL_PACKAGE/ApplySasPatch.sh
mv -f $BLDROOT/$REL_PACKAGE/RollbackSasPatch.sh.1 $BLDROOT/$REL_PACKAGE/RollbackSasPatch.sh

chmod -fR 755 $BLDROOT/$REL_PACKAGE/*.sh

## Copy SAS related Info.
cp -f /vob/Sipservlet/src/tools/patch_mop/applyPatch.sh $BLDROOT/$REL_PACKAGE/SAS
cp -f /vob/Sipservlet/src/tools/patch_mop/rollbackPatch.sh $BLDROOT/$REL_PACKAGE/SAS
chmod -fR 755 $BLDROOT/$REL_PACKAGE/SAS/*.sh

sed 's/REPLACE_VERSION/'$VERSION'/g' $BLDROOT/$REL_PACKAGE/SAS/applyPatch.sh > $BLDROOT/$REL_PACKAGE/SAS/applyPatch.sh.1
sed 's/REPLACE_VERSION/'$VERSION'/g' $BLDROOT/$REL_PACKAGE/SAS/rollbackPatch.sh > $BLDROOT/$REL_PACKAGE/SAS/rollbackPatch.sh.1

mv -f $BLDROOT/$REL_PACKAGE/SAS/applyPatch.sh.1 $BLDROOT/$REL_PACKAGE/SAS/applyPatch.sh
mv -f $BLDROOT/$REL_PACKAGE/SAS/rollbackPatch.sh.1 $BLDROOT/$REL_PACKAGE/SAS/rollbackPatch.sh
chmod -fR 755 $BLDROOT/$REL_PACKAGE/SAS/*.sh

# make tar of SAS patch
cd $BLDROOT/SAS
tar cf sas_patch.tar bpjars otherjars alcjars ra sysapps/registrar.sar dsjars/dsua.jar conf/server-ocm.xml conf/diagnostics.properties conf/rfClient.cfg conf/roClient.cfg conf/shClient.cfg conf/smpp-config.xml doc
if [ $? -ne 0 ]
then
    echo "Problem in packaging SAS patch"
	cd $BLDROOT/$REL_PACKAGE/SAS
    if test -s sas_patch.tar
    then
        rm sas_patch.tar
		cd $CURRDIR
    fi
    exit
fi
mv sas_patch.tar $BLDROOT/$REL_PACKAGE/SAS
cd $CURRDIR


## Copy MAPS related Info

CURRDIR=`pwd`

echo "MAPS - Calling createPatch.sh $BLDROOT/MAPS $VERSION"
cd /vob/Sipservlet/src/maps/build
./createPatch.sh $BLDROOT/MAPS $VERSION
if [ $? -ne 0 ]; then
  echo "Patch creation for MAPS is not successful"
  cd $CURRDIR
  exit 1
fi

echo "MAPS - Adding maps_patch.tar ..."

cp -f $BLDROOT/MAPS/maps_P${VERSION}.tar $BLDROOT/$REL_PACKAGE/MAPS
cp -f $BLDROOT/MAPS/applyPatch.sh $BLDROOT/$REL_PACKAGE/MAPS
cp -f $BLDROOT/MAPS/rollbackPatch.sh $BLDROOT/$REL_PACKAGE/MAPS
cp -f $BLDROOT/MAPS/patchFiles.dat $BLDROOT/$REL_PACKAGE/MAPS
chmod -f 750 $BLDROOT/$REL_PACKAGE/MAPS/*.sh

echo "MAPS - Done with maps patch processing ..."

cd $CURRDIR

## Copy INGw related Info
mkdir -p $BLDROOT/$REL_PACKAGE/INGW/sol28g/lib 
mkdir -p $BLDROOT/$REL_PACKAGE/INGW/sol28g/bin 
mkdir -p $BLDROOT/$REL_PACKAGE/INGW/sol28g/CommonLib/EmsLib

cp -r $BLDROOT/INGW/sol28g/lib/libINGw* $BLDROOT/$REL_PACKAGE/INGW/sol28g/lib/
cp -r $BLDROOT/INGW/sol28g/lib/libTcap* $BLDROOT/$REL_PACKAGE/INGW/sol28g/lib/
cp -r $BLDROOT/INGW/sol28g/bin/INGw $BLDROOT/$REL_PACKAGE/INGW/sol28g/bin/INGw
cp -r $BLDROOT/INGW/sol28g/lib/libEmsAgent.so $BLDROOT/$REL_PACKAGE/INGW/sol28g/CommonLib/EmsLib
cp -r $BLDROOT/INGW/sol28g/lib/libEmsCommon.so $BLDROOT/$REL_PACKAGE/INGW/sol28g/CommonLib/EmsLib
cp -r $BLDROOT/INGW/sol28g/lib/libEmsIdl.so $BLDROOT/$REL_PACKAGE/INGW/sol28g/CommonLib/EmsLib
cp -r $BLDROOT/INGW/sol28g/lib/libUtil.so $BLDROOT/$REL_PACKAGE/INGW/sol28g/CommonLib/EmsLib

# copy applyPatch.sh and rollback.sh scripts
cp -f /vob/Sipservlet/src/INGw/setup/Patch_Scripts/applyPatch.sh $BLDROOT/$REL_PACKAGE/INGW
cp -f /vob/Sipservlet/src/INGw/setup/Patch_Scripts/rollbackPatch.sh $BLDROOT/$REL_PACKAGE/INGW

chmod -fR 755 $BLDROOT/$REL_PACKAGE/INGW/*.sh

## Update Version 
sed 's/REPLACE_VERSION/'$VERSION'/g' $BLDROOT/$REL_PACKAGE/INGW/applyPatch.sh > $BLDROOT/$REL_PACKAGE/INGW/applyPatch.sh.1
sed 's/REPLACE_VERSION/'$VERSION'/g' $BLDROOT/$REL_PACKAGE/INGW/rollbackPatch.sh > $BLDROOT/$REL_PACKAGE/INGW/rollbackPatch.sh.1

mv $BLDROOT/$REL_PACKAGE/INGW/applyPatch.sh.1 $BLDROOT/$REL_PACKAGE/INGW/applyPatch.sh
mv $BLDROOT/$REL_PACKAGE/INGW/rollbackPatch.sh.1 $BLDROOT/$REL_PACKAGE/INGW/rollbackPatch.sh

chmod -fR 755 $BLDROOT/$REL_PACKAGE/INGW/*.sh

# No need to copy OtherLibs, SS7StackLib and TAO libs

# make tar of libs
cd $BLDROOT/$REL_PACKAGE/INGW
tar cf INGw$VERSION.tar sol28g
\rm -fR sol28g
cd $BLDROOT
## INGw patch creation completed

# Create complete tarball
tar cf ${REL_PACKAGE}.tar $REL_PACKAGE
gzip ${REL_PACKAGE}.tar

echo "Execution of make_patch.sh completed @ " `/bin/date`
echo ""
echo "Tarball \033[1m[$BLDROOT/${REL_PACKAGE}.tar.gz]\033[0m is available for release."

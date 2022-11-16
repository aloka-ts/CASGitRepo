#r/bin/ksh

bold=`tput bold`
offbold=`tput rmso`

if [ $# -ne 2 ]
then
    echo "${bold}Usage: createPatch.sh <MAPS-INSTALLROOT> <VERSION> ${offbold}"
    exit 1
fi

maps_build=$1
version=$2

PATCH_NAME=maps_P${version}

if [ ! -d "$maps_build" ]
then
    echo "${bold} MAPS INSTALLROOT not found... ${offbold} "
    exit
fi

SCR_DIR=`pwd`

cp -f ../setup/scripts/applyPatch.sh $maps_build
cp -f ../setup/scripts/rollbackPatch.sh $maps_build
cp -rf patchFiles.dat $maps_build

## Update the PATCHNAME in the scripts 
sed "s/PATCHNAME=maps_P/PATCHNAME=${PATCH_NAME}/g" $maps_build/applyPatch.sh > $maps_build/applyPatch.sh.new
mv -f $maps_build/applyPatch.sh.new $maps_build/applyPatch.sh

sed "s/PATCHNAME=maps_P/PATCHNAME=${PATCH_NAME}/g" $maps_build/rollbackPatch.sh > $maps_build/rollbackPatch.sh.new
mv -f $maps_build/rollbackPatch.sh.new $maps_build/rollbackPatch.sh


chmod u+x "$maps_build"/*

#echo $SCR_DIR

cd "$maps_build"

#myFile="$SCR_DIR/patchFiles.dat"

myFile="patchFiles.dat"
#----- Now the read
        myData=`cat $myFile`

#    echo `pwd` 

tar cf ${PATCH_NAME}.tar  $myData  

if [ $? -ne 0 ] 
then 
	echo "Problem in packaging Patch"
	if test -s  ${PATCH_NAME}.tar
	then 
		rm   ${PATCH_NAME}.tar
	fi
	cd $SCR_DIR
	exit
else
        echo "${bold} package successfully created at $1 ${offbold} "
fi

#!/bin/ksh

bold=`tput bold`
offbold=`tput rmso`

PATCHNAME=maps_P
echo "################################################################################"
echo "############## ${bold}Rolling Back Patch ${PATCHNAME}${offbold} ##################"
echo "################################################################################"
echo ""
echo ""

CURRDIR=`pwd`
PKG_DATE=`/bin/date +%d%m%y`


if [[ ! -s $INSTALLROOT/backup/${PATCHNAME}_backup.tar ]]
then
        echo " ${bold}Rollback failed: Backup file not found!${offbold} "
        exit
fi


myFile=$CURRDIR/MAPS/patchFiles.dat
#----- Now the read
        myData=`cat $myFile`

echo $myData

tar xf $INSTALLROOT/backup/${PATCHNAME}_backup.tar

if [ $? -ne 0 ]
then
        echo " ${bold} Rollback failed ${offbold} "
        exit
fi

mv -f $INSTALLROOT/backup/${PATCHNAME}_backup.tar "$INSTALLROOT/backup/${PATCHNAME}_backup.tar_$PKG_DATE.$$"

cd $CURRDIR

echo "${bold}ROLLBACK APPLIED SUCCESSFULLY${offbold}"


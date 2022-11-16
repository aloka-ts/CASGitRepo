#!/bin/ksh

bold=`tput bold`
offbold=`tput rmso`

PATCHNAME=maps_P

echo "################################################################################"
echo "################## ${bold}Applying Patch ${PATCHNAME}${offbold} ##################"
echo "################################################################################"
echo ""
echo ""

CURRDIR=`pwd`
cd $INSTALLROOT

if [ ! -d MmAppProvServer ]
then
        echo "${bold} INSTALLATION is not proper as MmAppProvServer donot exits ... Exiting${offbold}"
        exit;
fi

if [ -f backup/${PATCHNAME}_backup.tar ]
then
       echo "${bold} Patch already applied. If you want to apply patch again ${offbold}"
       echo "${bold}  first run rollback script and then try to apply patch again ${offbold}"
       exit
fi

myFile=$CURRDIR/MAPS/patchFiles.dat
#----- Now the read
        myData=`cat $myFile`

echo `pwd`

if [ ! -d backup ]
then   
        mkdir backup 
fi

tar cf ${PATCHNAME}_backup.tar $myData

mv ${PATCHNAME}_backup.tar backup/


if [ $? -ne 0 ]
then
        echo "${bold}ERROR in taking backup:${offbold}"
        if test -s backup/${PATCHNAME}_backup.tar
        then
                rm -f backup/${PATCHNAME}_backup.tar
        fi
        cd $CURRDIR
        exit
fi

tar xf  $CURRDIR/MAPS/${PATCHNAME}.tar

if [ $? -ne 0 ]
then
        echo "${bold}Problem in Applying Patch; Rolling Back...${offbold}"
        $CURRDIR/MAPS/rollbackPatch.sh
        exit
fi

cd $CURRDIR

echo "${bold}PATCH APPLIED SUCCESSFULLY${offbold}"


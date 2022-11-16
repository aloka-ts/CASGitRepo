#!/bin/ksh

bold=`tput bold`
offbold=`tput rmso`
#export VERSION=REPLACE_VERSION
export VERSION=7.5.11.53
PATCHNAME=CDRUTILITY$VERSION

echo "##################################################################################"
echo "############## ${bold}Rolling Back Patch ${PATCHNAME}${offbold} ##################"
echo "##################################################################################"
echo ""
echo ""

CURRDIR=`pwd`
                                                                                                                                  
# Run SAS Profile
. $HOME/profile.sas.*

cd $INSTALLROOT

if [[ ! -s ${PATCHNAME}_backup.tar ]]
then
	echo " ${bold}Rollback failed: Backup file not found!${offbold} "
	exit
fi

tar xf ${PATCHNAME}_backup.tar


if [ $? -ne 0 ]
then 
	echo " ${bold} Rollback failed ${offbold} "
	exit
fi

rm -f ${PATCHNAME}_backup.tar

echo "${bold}ROLLBACK APPLIED SUCCESSFULLY${offbold}"


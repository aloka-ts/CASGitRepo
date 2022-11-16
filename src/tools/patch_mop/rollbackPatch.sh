#!/bin/ksh

bold=`tput bold`
offbold=`tput rmso`
#export VERSION=REPLACE_VERSION
export VERSION=10.1.0.36.11
PATCHNAME=CAS$VERSION
typeset -r VERSION_FILE_NAME="cas_version.cfg"
typeset -r BACKUP_VERSION_FILE_NAME="backup_cas_version.cfg"

echo "##################################################################################"
echo "############## ${bold}Rolling Back Patch ${PATCHNAME}${offbold} ##################"
echo "##################################################################################"
echo ""
echo ""

CURRDIR=`pwd`
                                                                                                                                  
# Run CAS Profile
. $HOME/profile.cas.*

cd $INSTALLROOT/ASESubsystem

if [[ ! -s ${PATCHNAME}_backup.tar ]]
then
	echo " ${bold}Rollback failed: Backup file not found!${offbold} "
	exit
fi

#rm -rf conf/ase.properties
rm -rf conf/telnetsshra.properties
rm -rf conf/registrar.properties
rm -rf conf/pac.properties
rm -rf conf/ApprouterConfig.xml
#rm -rf scripts/ase_no_ems
rm -rf scripts/UpdateVersion.sh
#rm -rf bpjars/
rm -rf alcjars/
rm -rf httpjars/
rm -rf sbb/
rm -rf ra/
rm -rf sysapps/cim 
rm -rf sysapps/cim.war
rm -rf sysapps/pac
rm -rf sysapps/pac.war 
rm -rf sysapps/registrar
rm -rf sysapps/registrar.sar
rm -rf lib/redhat80g
rm -rf apps/archives/CIM*
rm -rf apps/archives/RegistrarServlet*
rm -rf apps/archives/PAC*
rm -rf ASE_template.xml
rm -rf ASE_template_emsl.xml
rm -rf setup/ase/ASE_template.xml
rm -rf setup/ase/ASE_template_emsl.xml 
rm -rf dsjars/dsua.jar
rm -rf scripts/encryptor
rm -rf otherjars/asm-5.0.3.jar
rm -rf otherjars/commons-pool2-2.4.2.jar
rm -rf otherjars/jersey*
rm -rf otherjars/jgroups*
rm -rf Common/thirdParty/jakarta-tomcat/lib/tomcat-coyote.jar  
tar xf ${PATCHNAME}_backup.tar ${BACKUP_VERSION_FILE_NAME}

mv ${BACKUP_VERSION_FILE_NAME} ${VERSION_FILE_NAME} 

#echo " Enter the appropriate option for version update (1/2) - "
#echo "  1 EMS Based Installation"
#echo "  2 Non-EMS or wEMS Based Installation"

#echo " Your Option is :"
#read ans

#if [ $ans = '1' ]
#	then
#		echo "Updating component version on EMS..."
#		cd $INSTALLROOT/ASESubsystem/scripts
#		chmod +x UpdateVersion.sh
#		PREV_VERSION=`cat ase_no_ems|grep 'COMPONENT_VERSION='|cut -d '=' -f2`
#		./UpdateVersion.sh $PREV_VERSION ASESUBSYSTEM
#fi

#if [ $? -ne 0 ]
#	then 
#		echo " ${bold} Rollback failed ${offbold} "
#	exit
#fi

cd $INSTALLROOT/ASESubsystem
rm -f ${PATCHNAME}_backup.tar

echo "$(date) : $VERSION patch rollback successfully." >> $INSTALLROOT/ASESubsystem/patch_history.log
echo "-----------------------------------" >> $INSTALLROOT/ASESubsystem/patch_history.log

echo "${bold}ROLLBACK APPLIED SUCCESSFULLY${offbold}"


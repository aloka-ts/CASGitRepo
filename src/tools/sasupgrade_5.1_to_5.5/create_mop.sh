#/bin/ksh

bold=`tput bold`
offbold=`tput rmso`

if [ $# -ne 3 ]
then 
	echo "${bold}usage: create_mop.sh <patch destination> <SAS build location> <system monitor patch location>${offbold}"
	exit 1
fi

patch_dest=$1
sas_build=$2
sysmon_patch=$3
mop_location=/vob/Sipservlet/src/tools/sasupgrade_5.1_to_5.5/

if [ ! -d $patch_dest ]
then
	echo "${bold}patch Destination not found... ${offbold}"
	exit
fi
 
if [ ! -f $sas_build ] 
then
	echo "${bold}Sas Build not found... ${offbold}"
	exit
fi

if [ ! -f $sysmon_patch ]
then
	echo "${bold}system monitor patch not found..... "
	exit
fi

echo "${bold}CREATING SAS UPGRADE MOP."

cd $patch_dest
mkdir -p SASUPGRADE_MOP
cd SASUPGRADE_MOP

echo copying scripts to mop destination directory

cp  $mop_location/TestRexec $mop_location/UpgradeProperties.class $mop_location/applyMop.sh $mop_location/rollbackMop.sh $mop_location/root_scr.txt $mop_location/signalIP.sh $mop_location/createLink.sh $mop_location/sql* .

#cp $mop_location/TestRexec $mop_location/UpgradeProperties.class $mop_location/applyMop.sh $mop_location/rollbackMop.sh $mop_location/root_scr.txt $mop_location/signalIP.sh $mop_location/sql.sh $mop_location/sqlPeerIP.sh $mop_location/sqlbackup.sh $mop_location/sqlcheck.sh $mop_location/sqlcheckIP.sh .
#cp $mop_location/* .

chmod 777 applyMop.sh
cp  $sas_build .

echo copying system monitor patch to mop destination directory
mkdir -p systemmonitor
cp  $sysmon_patch systemmonitor/
cp TestRexec systemmonitor/

tar cf SystemMonitor.tar systemmonitor
if [ $? -ne 0 ]
then
	echo "Problem in making SystemMonitor.tar"
	exit
fi	
gzip -f SystemMonitor.tar
if [ $? -ne 0 ]
then
	echo "Problem in ziping SystemMonitor.tar"
	exit
fi	
rm -rf systemmonitor

echo making SasUpgradeMop.tar
cd $patch_dest
tar cf SasUpgradeMop.tar SASUPGRADE_MOP
if [ $? -ne 0 ]
then
	echo "Problem in making SasUpgradeMop.tar"
	exit
fi	

echo "making SasUpgradeMop.tar.gz."
gzip -f SasUpgradeMop.tar
if [ $? -ne 0 ]
then
	echo "Problem in making SasUpgradeMop.tar"
	exit
fi	

rm -rf SASUPGRADE_MOP
echo "UPRADE MOP CREATED SUCCESSFULLY IN THE DIR : $patch_dest ${offbold}"


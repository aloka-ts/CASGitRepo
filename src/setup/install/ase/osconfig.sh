#!/bin/ksh

PLATFORM=`uname`
if [ ${PLATFORM} == "SunOS" ]
then 
	echo "Creating expect link"
	rm /usr/bin/expect
	ln -s /usr/local/bin/expect /usr/bin/expect
fi


echo "ase: osconfig: creating the /etc/fileRegistry directory"
mkdir -p /etc/fileRegistry
mkdir -p /LOGS/CAS
chmod 777 /LOGS/CAS
if [[ $? == 0 ]]
then
   echo "INSTALL_ROOT" >> /etc/fileRegistry/sas.filelist
   echo "/etc/inittab" >> /etc/fileRegistry/sas.filelist
   echo "/var/Baypackets/$INSTALL_SUFFIX/SystemMonitor" >> /etc/fileRegistry/sas.filelist
   echo "/opt/Baypackets/$INSTALL_SUFFIX" >> /etc/fileRegistry/sas.filelist
   if [[ $? != 0 ]]
   then
     echo "ase: osconfig.sh : could not create /etc/fileRegistry/sas.filelist "
   fi
else
  echo "ase: osconfig.sh : could not create /etc/fileRegistry"
fi
echo "SUCCESS"

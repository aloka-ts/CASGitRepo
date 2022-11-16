#!/bin/ksh

# changes to make an entry for INGw in /etc/fileRegistry/ingw.filelist
# so that backup of the NSP directory mentioned in this file can be done.

SUBSYS_DIR=$SUBSYS_INGW

echo "INGw: osconfig: creating the /etc/fileRegistry directory"
mkdir -p /etc/fileRegistry
if [[ $? == 0 ]]
then
   echo "$INSTALL_ROOT" > /etc/fileRegistry/ingw.filelist
   echo "$HOME_DIR/profile.ingw" >> /etc/fileRegistry/ingw.filelist
   echo "/etc/inittab" >> /etc/fileRegistry/ingw.filelist
   echo "/var/Baypackets" >> /etc/fileRegistry/ingw.filelist
   echo "/opt/Baypackets" >> /etc/fileRegistry/ingw.filelist
   echo "/LOGS/$SUBSYS_DIR" >> /etc/fileRegistry/ingw.filelist
   echo "/etc/hosts" >> /etc/fileRegistry/ingw.filelist
   if [[ $? != 0 ]]
   then
     echo "INGw: osconfig.sh : could not create /etc/fileRegistry/ingw.filelist "
   fi
else
  echo "INGw: osconfig.sh : could not create /etc/fileRegistry"
fi

mkdir -p /LOGS/$SUBSYS_DIR
chmod 777 /LOGS/$SUBSYS_DIR

echo "SUCCESS"

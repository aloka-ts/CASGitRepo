#!/bin/ksh

# Purpose : Script for replacing variable constants in XMLs for JBoss with their corresponding values
# Author  : Arpana
# Date    : Feb 2008

# Variable Declaration

MAPS_VERSION="mapsVersion"
MAPS_SUBSYS_DIR="MmAppProvServer$MAPS_VERSION"
MAPS_PLTFRM=`uname`

if test $MAPS_PLTFRM = "Linux"
then
	ECHO_PREFIX=' -e '
else
	ECHO_PREFIX=''
fi

CONF_LOC=$INSTALL_ROOT/$MAPS_SUBSYS_DIR/conf/
SCRIPT_LOC=$INSTALL_ROOT/$MAPS_SUBSYS_DIR/scripts/

echo "#### Inside ConfigMAPSData.sh ####"

echo "INSTALLROOT is :: $INSTALL_ROOT "
echo "configuration location :: $CONF_LOC"
echo "scripts location :: $SCRIPT_LOC"

##############################
# Gathering information needed 
##############################

echo "Gathering information needed to modify xml files..."

I_ROOT=`echo $ECHO_PREFIX  $INSTALL_ROOT | sed "s/\//\\\\\\\\\\//g"`
LOG_LOC=`echo $ECHO_PREFIX  $LOG_LOCATION | sed "s/\//\\\\\\\\\\//g"`
TMP_LOC=`echo $ECHO_PREFIX  $TEMP_LOCATION | sed "s/\//\\\\\\\\\\//g"`

echo "Gathering information to modify files is finished..."

##############################
# Replacing into files in MAPS_INSTALL/MmAppProvServer/conf directory
##############################

# Creating temp location to modify files.So that conf preserves the template.

mkdir $INSTALL_ROOT/$MAPS_SUBSYS_DIR/tmpConf
cp -f $INSTALL_ROOT/$MAPS_SUBSYS_DIR/conf/* $INSTALL_ROOT/$MAPS_SUBSYS_DIR/tmpConf/
cd $INSTALL_ROOT/$MAPS_SUBSYS_DIR/tmpConf

echo "Modifying files in $INSTALL_ROOT/MmAppProvServer/tmpConf "

# maps changes
sed 's/INSTALL_ROOT/'$I_ROOT'/g' maps_template > maps_template_1
sed 's/LOG_DIRECTORY/'$LOG_LOC'/g' maps_template_1 > maps_template_2
mv -f maps_template_2 maps

# setup.sh changes
sed 's/INSTALL_ROOT/'$I_ROOT'/g' setup_template > setup_template_1
sed 's/TEMP_LOC/'$TMP_LOC'/g' setup_template_1 > setup_template_2
mv -f setup_template_2 setup.sh

# fthandler changes
sed 's/PRIMARY_MAPS_IP/'$MACHINE1_IP'/g' fthandler.conf > fthandler.conf_1
sed 's/SEC_MAPS_IP/'$MACHINE2_IP'/g' fthandler.conf_1 > fthandler.conf_2
# We need to get a macro for floating ip...
#sed 's/FLOATING_IP/'$SAS_SIG_FIP'/g' fthandler.conf_2 > fthandler.conf_3
sed 's/FLOATING_IP/'$WS_PORT'/g' fthandler.conf_2 > fthandler.conf_3
sed 's/REF_IP/'$REF_NW_IP'/g' fthandler.conf_3 > fthandler.conf_4
sed 's/INSTALL_ROOT/'$I_ROOT'/g' fthandler.conf_4 > fthandler.conf_5
mv -f fthandler.conf_5 fthandler.conf


#osconfig_template changes
sed 's/INSTALL_ROOT/'$I_ROOT'/g' osconfig_template > osconfig_template_1
sed 's/LOG_LOCATION/'$LOG_LOC'/g' osconfig_template_1 > osconfig_template_2
mv -f osconfig_template_2 ftconfig.sh

chmod 755 setup.sh maps ftconfig.sh

cd -

cd $SCRIPT_LOC

sed 's/INSTALL_ROOT/'$I_ROOT'/g' fthandler.sh > fthandler.sh_1 
mv -f fthandler.sh_1 fthandler.sh


sed 's/LOG_DIRECTORY/'$LOG_LOC'/g' osconfig.sh > osconfig.sh_1 
mv -f osconfig.sh_1 osconfig.sh

chmod 755 fthandler.sh osconfig.sh

cd -

# *********************************
# Moving modified files into necesary folders in JBOSSHOME
# *********************************

echo "Moving modified files into respective places..."

cp -f $INSTALL_ROOT/$MAPS_SUBSYS_DIR/tmpConf/maps $SCRIPT_LOC
cp -f $INSTALL_ROOT/$MAPS_SUBSYS_DIR/tmpConf/setup.sh $SCRIPT_LOC
cp -f $INSTALL_ROOT/$MAPS_SUBSYS_DIR/tmpConf/fthandler.conf $CONF_LOC
cp -f $INSTALL_ROOT/$MAPS_SUBSYS_DIR/tmpConf/ftconfig.sh $SCRIPT_LOC

echo "Files have been moved into their respective places.."

rm -rf $INSTALL_ROOT/$MAPS_SUBSYS_DIR/tmpConf

# fthandler related script changes

filename="fthandler"

cd $INSTALL_ROOT/$MAPS_SUBSYS_DIR/conf
if [[ ! -f ../conf/${filename}.lcfg ]]
then
     echo "D LOGFILE fthandler.log" >> ../conf/${filename}.lcfg
     #echo "D LOG""DIR /LOGS/MAPS" >> ../conf/${filename}.lcfg
     echo "D LOGDIR $LOG_LOCATION" >> ../conf/${filename}.lcfg
     echo "D LOGSIZE 1000000" >> ../conf/${filename}.lcfg
     echo "D LOGLEVEL ERR" >> ../conf/${filename}.lcfg
fi
cd -


if [[ ! -f $INSTALL_ROOT/$MAPS_SUBSYS_DIR/conf/MAPSPidFile ]]
then

 touch $INSTALL_ROOT/$MAPS_SUBSYS_DIR/conf/MAPSPidFile
 echo "165767234" > $INSTALL_ROOT/$MAPS_SUBSYS_DIR/conf/MAPSPidFile 

fi

echo "#### Exiting from ConfigMAPSData.sh ####"

echo "SUCCESS"

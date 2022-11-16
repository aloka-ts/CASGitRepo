#!/usr/bin/ksh
 
 
#LOCKFILE=/home/maps/MAPS_HA//SPSI/$PLTFRM_DIR/conf/webServerProcessId
#LOCKFILE2=/home/maps/MAPS_HA//SPSI/$PLTFRM_DIR/conf/pingSPSIProcessId
 
# Here u will source setup.sh
. INSTALL_ROOT/MmAppProvServer/scripts/setup.sh
 
# This will have to be modified to kill any running maps process
# Better to kiil any residual stuff before launching the ftthandler
# Kill any a.out instance running
 
exec INSTALL_ROOT/MmAppProvServer/bin/fthandler -cfgFile INSTALL_ROOT/MmAppProvServer/conf/fthandler.conf -logCfg INSTALL_ROOT/MmAppProvServer/conf/fthandler.lcfg


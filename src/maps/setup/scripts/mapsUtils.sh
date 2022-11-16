#!/bin/ksh

# Purpose : Script for updating profile.maps.username and performing miscellaneous functions.
# Author  : Arpana
# Date    : Feb 2008
# Changes to be done here : Update MAPS_SUBSYS_DIR and jboss version here. 

#Performing PATH setting for whoami
PLTFRM=`uname`
if test $PLTFRM = "Linux"
then
PATH=/usr/bin:$PATH
else
PATH=/usr/ucb:$PATH
fi

USER_NAME=`whoami`
MAPS_PLTFRM=`uname`
MAPS_VERSION="mapsVersion"
MAPS_SUBSYS_DIR="MmAppProvServer$MAPS_VERSION"

echo "##### Inside mapsUtils.sh #####"
echo "INSTALLROOT : $INSTALL_ROOT"

echo "Installation Platform is : $MAPS_PLTFRM "
if test $MAPS_PLTFRM = "Linux"
then
	ECHO_PREFIX=' -e '
else
	ECHO_PREFIX=''
fi
export ECHO_PREFIX


#Jboss link here.
cd $INSTALL_ROOT/$MAPS_SUBSYS_DIR/
ln -s $INSTALL_ROOT/$MAPS_SUBSYS_DIR/jboss-4.2.0.GA jboss
cd -
echo "Jboss link created."

# Updating profile file
chmod 755 $INSTALL_ROOT/$MAPS_SUBSYS_DIR/scripts/*
chmod 664 $INSTALL_ROOT/$MAPS_SUBSYS_DIR/conf/*

# Creating temporary location
mkdir -p $TEMP_LOCATION 
chmod -R 755 $TEMP_LOCATION

# Creating profile

cd

echo "###local file created at `date`

PLTFRM=\`uname\`
export PLTFRM
echo \"platform is \$PLTFRM\"
unset ENV

export INSTALLROOT=$INSTALL_ROOT
export LOG_LOCATION=$LOG_LOCATION

# Set umask to control permissions
umask 0022

# Setting java home
if test \$PLTFRM = \"Linux\"
then
  JAVA_HOME=/usr/java
  JAVA_DEBUG_LIB=/usr/java/lib/i386
else
  JAVA_HOME=/usr/java
  JAVA_DEBUG_LIB=/usr/java/lib/sparc
fi

export JAVA_HOME
export JAVA_DEBUG_LIB

# Setting Expect path
if test \$PLTFRM = \"Linux\"
then
	EXPECTBIN=/usr/bin
	EXPECTLIB=/usr/lib
else
	EXPECTBIN=/usr/local/bin
	EXPECTLIB=/usr/local/lib
fi

export EXPECTBIN
export EXPECTLIB

export PLTFRM_DIR
export PATH

# Setting Library path
if test \$PLTFRM = \"Linux\"
then
  LD_LIBRARY_PATH=\$JAVA_DEBUG_LIB:/usr/local/lib:/usr/lib:\$EXPECTLIB:/lib:/usr/ucblib:\$LD_LIBRARY_PATH
else
  LD_LIBRARY_PATH=\$JAVA_DEBUG_LIB:/usr/local/lib:/usr/lib:\$EXPECTLIB:/lib:/usr/ucblib:\$LD_LIBRARY_PATH
fi

export LD_LIBRARY_PATH

#Setting Path
if test \$PLTFRM = \"Linux\"
then
   PATH=/bin:/sbin:/usr/bin:/etc:\$JAVA_HOME/bin:/usr/sbin:.:\$EXPECTBIN:\$PATH
else
   PATH=/usr/bin:/usr/ucb:/etc:\$JAVA_HOME/bin:/bin:/sbin:/usr/sbin:.:\$EXPECTBIN:\$PATH
fi

export PATH

# Aliases
EDITOR=vi
export EDITOR

export PATH=\$PATH:\$INSTALLROOT/\$MAPS_SUBSYS_DIR/scripts

export PATH

PS1=\"< MAPS ==> \`hostname\` ! >\"
export PS1
alias scr=\"cd \$INSTALLROOT/$MAPS_SUBSYS_DIR/scripts\"
alias conf=\"cd \$INSTALLROOT/$MAPS_SUBSYS_DIR/conf\"
alias log=\"cd \$LOG_LOCATION\"
alias jboss=\"cd \$INSTALLROOT/$MAPS_SUBSYS_DIR/jboss\"
alias gbjars=\"cd \$INSTALLROOT/$MAPS_SUBSYS_DIR/gbjars\"

# JBoss Home

JBOSS_HOME=\$INSTALLROOT/$MAPS_SUBSYS_DIR/jboss
export JBOSS_HOME " > profile.maps.${USER_NAME}
chmod -R 777 profile.maps.${USER_NAME}
cd -

#End of profile

echo "##### Exiting mapsUtils.sh #####"

echo "SUCCESS"

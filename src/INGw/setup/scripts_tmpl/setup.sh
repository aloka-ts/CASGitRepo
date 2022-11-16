#!/usr/bin/ksh
PLTFRM=`uname`

export PLTFRM
echo "platform is : $PLTFRM"
if test $PLTFRM = "Linux"
then
  export PLTFRM_DIR=redhat80g
  export PLATFORM_DIR=redhat80g
else
  export PLTFRM_DIR=sol28g
  export PLATFORM_DIR=sol28g
fi


KERNEL_RELEASE=`uname -r`
export KERNEL_RELEASE
echo "Kernel is : $KERNEL_RELEASE"

export INSTALLROOT=INSTALL_ROOT

export SUBSYS_DIR=SUBSYS_INGW

# Common Libs
export COMMON_LIB_DIR=$INSTALLROOT/$SUBSYS_DIR/PLATFORM_DIR/CommonLib
export EMS_LIBS=$COMMON_LIB_DIR/EmsLib
export SS7_STACK_LIBS=$COMMON_LIB_DIR/Ss7StackLib
export TAO_LIBS=$COMMON_LIB_DIR/TaoLib
export OTHER_LIBS=$COMMON_LIB_DIR/OtherLib

# Path
export PATH=/bin:/usr/bin:/usr/ucb:/usr/local/bin/:/usr/sbin:$PATH

# LD_LIBRARY_PATH
export LD_LIBRARY_PATH=/opt/SUNWspro/lib:/usr/local/lib:/usr/openwin/lib:/usr/lib:$INSTALLROOT/$SUBSYS_DIR/PLATFORM_DIR/lib:$EMS_LIBS:$SS7_STACK_LIBS:$TAO_LIBS:$OTHER_LIBS:/opt/SUNWspro/WS6U1/lib:/usr/lib/lwp:$LD_LIBRARY_PATH

# IP and port 
export IP_ADDRESS_NS=$MY_HOST_IP
export PORT_NUMBER_NS=20500


# Log directory
export LOG_OUTPUT_DIR=/LOGS/$SUBSYS_DIR

export DISPLAY=DISP_MACHINE:0.0
if test $PLTFRM = "SunOS"
then
  coreadm -p core.%f.%p $$
fi

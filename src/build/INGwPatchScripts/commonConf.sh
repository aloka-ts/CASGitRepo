#!/usr/bin/ksh

export PROD_ID="IN Gateway, SAS"
export PROD_VER="7.5.4.17"
export PATCH_NUM="INGw7.5.4.17"

PLTFRM=`uname`
export PLTFRM
if test $PLTFRM = "Linux"
then
  PLTFRM_DIR=redhat80g
  BUILD=LNX
  LIB_EXT=lnx
else
  PLTFRM_DIR=sol28g
  BUILD=SOL
  LIB_EXT=sol
fi

export PLTFRM_DIR
export BUILD

export PKG_NAME=${BUILD}_${PATCH_NUM}
export PKG_NAME_EXT=`echo ${PKG_NAME}.tar.gz`
export REL_NAME=`echo ${PKG_NAME} | cut -b9-`

export curdir=`pwd`

echo ""
echo "PATCH_NUM    : [${PATCH_NUM}]"
#echo "PLTFRM       : [${PLTFRM}]"
#echo "PLTFRM_DIR   : [${PLTFRM_DIR}]"
#echo "BUILD        : [${BUILD}]"
#echo "PKG_NAME     : [${PKG_NAME}]"
#echo "PKG_NAME_EXT : [${PKG_NAME_EXT}]"
#echo "REL_NAME     : [${REL_NAME}]"
#echo "curdir       : [${curdir}]"
echo ""

#!/usr/bin/ksh

removeTemporaryFiles()
{
  echo "Removing build temp files...\c"
  \rm -rf INGw7.5.4.0
  \rm -f applyIngwPatch.sh
  \rm -f rollbackIngwPatch.sh
  \rm -f verifyPatchApp.sh
  \rm -f verifyPatchRollback.sh
  \rm -f commonConf.sh
  if [ -f applyIngwMop.sh ]
  then
   \rm -f applyIngwMop.sh
  fi
  \rm -f updateParamInDb.sh
  \rm -f rollBackParamInDb.sh
  \rm -f ${PKG_NAME}.tar.gz
  \rm -f patchInfo_${PATCH_NUM}
  \rm -f ingw.tar
}

export BUILDROOT=`pwd`
export PKG_DATE=`date +%d%m%y`
export VOB_SRC_DIR="/vob/Sipservlet/src"
export PATCH_SCRIPTS_DIR="${VOB_SRC_DIR}/build/INGwPatchScripts"
export SS7_STACK_DIR="/vob/thirdParty/stacks/ss7Stack/ag_ccpu"
export TAO_LIB_DIR="${PLTFRM_DIR}/lib/thirdParty/TAO/ACE/ACE_wrappers/ace"

echo "\n\nWant to create patch with package date [${PKG_DATE}]? If No then enter n/N : \c"
read use_current_date

if [ "${use_current_date}" == "n" -o "${use_current_date}" == "N" ]
then
echo "Enter package date in the format \"DDMMYY\" : \c"
read PKG_DATE
else
echo "Continuing with package date [${PKG_DATE}]..."
fi

\cp ${PATCH_SCRIPTS_DIR}/commonConf.sh ./
chmod 777 commonConf.sh
. ./commonConf.sh

#if test -f $BUILDROOT/createIngwPatch.log
#then
#  \mv $BUILDROOT/createIngwPatch.log $BUILDROOT/createIngwPatch_`date +%d%h_%H_%M_%S`.log
#  echo "Moving file \"$BUILDROOT/createIngwPatch.log\" to file \"$BUILDROOT/createIngwPatch_`date +%d%h_%H_%M_%S`.log\""
#fi
#
#script $BUILDROOT/createIngwPatch.log

echo " "
echo "Make sure your current directory is one level above $PLTFRM_DIR (of the BUILD)"
echo "Make sure the correct view is set before running this script" 
echo " "
echo "Press any key to continue...\c"
read a


echo "Want to package INGW [Y\N]    : \c"
read PKG_INGW


if [ "${PKG_INGW}" == "Y" -o "${PKG_INGW}" == "y" ]
then
  ## Now package INGW
  echo "\nPackaging INGW\n" 
  
  mkdir -p INGw7.5.4.0/${PLTFRM_DIR}/bin
  mkdir -p INGw7.5.4.0/${PLTFRM_DIR}/lib
  mkdir -p INGw7.5.4.0/${PLTFRM_DIR}/conf
  mkdir -p INGw7.5.4.0/${PLTFRM_DIR}/scripts
  mkdir -p INGw7.5.4.0/${PLTFRM_DIR}/CommonLib/EmsLib
  #mkdir -p INGw7.5.4.0/${PLTFRM_DIR}/CommonLib/TaoLib

  
  
  \cp  -rp ./BuildEnv INGw7.5.4.0/${PLTFRM_DIR}/.
  \cp  -p ${PLTFRM_DIR}/bin/INGw* INGw7.5.4.0/${PLTFRM_DIR}/bin/.
  \cp  -p ${SS7_STACK_DIR}/lib/libstk_ftha.so INGw7.5.4.0/${PLTFRM_DIR}/lib/.
  \cp  -p ${SS7_STACK_DIR}/lib/libstk_ftha_witrc_widbg.so INGw7.5.4.0/${PLTFRM_DIR}/lib/.
  \cp  -p ${PLTFRM_DIR}/lib/libINGwCommonTypes.so INGw7.5.4.0/${PLTFRM_DIR}/lib/.
  \cp  -p ${PLTFRM_DIR}/lib/libINGwInfraUtil.so INGw7.5.4.0/${PLTFRM_DIR}/lib/.
  \cp  -p ${PLTFRM_DIR}/lib/libINGwInfraMsrMgr.so INGw7.5.4.0/${PLTFRM_DIR}/lib/.
  \cp  -p ${PLTFRM_DIR}/lib/libINGwInfraStreamManager.so INGw7.5.4.0/${PLTFRM_DIR}/lib/.
  \cp  -p ${PLTFRM_DIR}/lib/libINGwInfraParamRepository.so INGw7.5.4.0/${PLTFRM_DIR}/lib/.
  \cp  -p ${PLTFRM_DIR}/lib/libINGwInfraResourceMonitor.so INGw7.5.4.0/${PLTFRM_DIR}/lib/.
  \cp  -p ${PLTFRM_DIR}/lib/libINGwInfraTelnetIface.so INGw7.5.4.0/${PLTFRM_DIR}/lib/.
  \cp  -p ${PLTFRM_DIR}/lib/libINGwInfraStatMgr.so INGw7.5.4.0/${PLTFRM_DIR}/lib/.
  \cp  -p ${PLTFRM_DIR}/lib/libINGwInfraManager.so INGw7.5.4.0/${PLTFRM_DIR}/lib/.
  \cp  -p ${PLTFRM_DIR}/lib/libINGwLoadDistributor.so INGw7.5.4.0/${PLTFRM_DIR}/lib/.
  \cp  -p ${PLTFRM_DIR}/lib/libINGwSipProvider.so INGw7.5.4.0/${PLTFRM_DIR}/lib/.
  \cp  -p ${PLTFRM_DIR}/lib/libINGwSipMsgHandler.so INGw7.5.4.0/${PLTFRM_DIR}/lib/.
  \cp  -p ${PLTFRM_DIR}/lib/libINGwFtTalk.so INGw7.5.4.0/${PLTFRM_DIR}/lib/.
  \cp  -p ${PLTFRM_DIR}/lib/libINGwFtPacket.so INGw7.5.4.0/${PLTFRM_DIR}/lib/.
  \cp  -p ${PLTFRM_DIR}/lib/libINGwFtMessenger.so INGw7.5.4.0/${PLTFRM_DIR}/lib/.
  \cp  -p ${PLTFRM_DIR}/lib/libINGwTcapProvider.so INGw7.5.4.0/${PLTFRM_DIR}/lib/.
  \cp  -p ${PLTFRM_DIR}/lib/libTcapMessage.so INGw7.5.4.0/${PLTFRM_DIR}/lib/.
  \cp  -p ${PLTFRM_DIR}/lib/libINGwStackManager.so INGw7.5.4.0/${PLTFRM_DIR}/lib/.
  \cp  -p ${PLTFRM_DIR}/lib/libINGwIwf.so INGw7.5.4.0/${PLTFRM_DIR}/lib/.

  \cp  -p ${PLTFRM_DIR}/lib/libEmsAgent.so INGw7.5.4.0/${PLTFRM_DIR}/CommonLib/EmsLib/.
  \cp  -p ${PLTFRM_DIR}/lib/libEmsCommon.so INGw7.5.4.0/${PLTFRM_DIR}/CommonLib/EmsLib/.
  \cp  -p ${PLTFRM_DIR}/lib/libEmsIdl.so INGw7.5.4.0/${PLTFRM_DIR}/CommonLib/EmsLib/.
  \cp  -p ${PLTFRM_DIR}/lib/libUtil.so INGw7.5.4.0/${PLTFRM_DIR}/CommonLib/EmsLib/.

  #\cp  -p ${TAO_LIB_DIR}/lib* INGw7.5.4.0/${PLTFRM_DIR}/CommonLib/TaoLib/.

  #\cp  -p ${VOB_SRC_DIR}/INGw/setup/conf_tmpl/ss7_sig_conf.sh INGw7.5.4.0/${PLTFRM_DIR}/conf/.
  \cp  -p ${VOB_SRC_DIR}/INGw/setup/conf_tmpl/ccpu_ssi_mem.conf INGw7.5.4.0/${PLTFRM_DIR}/conf/.
  \cp  -p  ${VOB_SRC_DIR}/INGw/src/INGwStackManager/INGwStackManager/INGwSm_CCM1.xml INGw7.5.4.0/${PLTFRM_DIR}/conf/.
  \cp  -p  ${VOB_SRC_DIR}/INGw/src/INGwStackManager/INGwStackManager/INGwSm_CCM2.xml INGw7.5.4.0/${PLTFRM_DIR}/conf/.

  \cp  -p ${VOB_SRC_DIR}/INGw/setup/scripts_tmpl/DiagScr.sh INGw7.5.4.0/${PLTFRM_DIR}/scripts/.
  \cp  -r ${VOB_SRC_DIR}/INGw/src/INGwTelnetScript/INGwProbeScripts INGw7.5.4.0/${PLTFRM_DIR}/scripts/.
  chmod 755 INGw7.5.4.0/${PLTFRM_DIR}/conf/*
  chmod 755 INGw7.5.4.0/${PLTFRM_DIR}/scripts/*
  chmod 777 INGw7.5.4.0/${PLTFRM_DIR}/scripts/INGwProbeScripts/*	


  CDIR1=`pwd`
  # Getting the checksum of existing INGW libraries, binary and other file [
  cd INGw7.5.4.0/${PLTFRM_DIR}
  echo "Calculating CheckSum"

  CKSUM_FILE_LIST="lib/libstk_ftha_witrc_widbg.so lib/libstk_ftha.so lib/libINGwCommonTypes.so  lib/libINGwInfraUtil.so lib/libINGwInfraMsrMgr.so lib/libINGwInfraStreamManager.so lib/libINGwInfraParamRepository.so lib/libINGwInfraResourceMonitor.so lib/libINGwInfraTelnetIface.so lib/libINGwInfraStatMgr.so lib/libINGwInfraManager.so lib/libINGwLoadDistributor.so lib/libINGwSipProvider.so lib/libINGwSipMsgHandler.so lib/libINGwFtTalk.so lib/libINGwFtPacket.so lib/libINGwFtMessenger.so lib/libINGwTcapProvider.so lib/libTcapMessage.so lib/libINGwStackManager.so lib/libINGwIwf.so bin/INGw CommonLib/EmsLib/libEmsAgent.so CommonLib/EmsLib/libEmsCommon.so CommonLib/EmsLib/libEmsIdl.so CommonLib/EmsLib/libUtil.so scripts/DiagScr.sh conf/INGwSm_CCM1.xml"

  if [ -f conf/INGwSm_CCM2.xml ]
  then
    CKSUM_FILE_LIST="${CKSUM_FILE_LIST} conf/INGwSm_CCM2.xml"
  fi

  for i in `echo ${CKSUM_FILE_LIST}`
  do
    cksum $i
  done > ${CDIR1}/patchInfo_${PATCH_NUM}

  # ] - Getting the checksum of existing INGW libraries, binary and other file
  cd ${CDIR1}

  tar -cvf ingw.tar INGw7.5.4.0

  tar -cvf  ${PKG_NAME}.tar ingw.tar

fi

gzip ${PKG_NAME}.tar

\cp ${PATCH_SCRIPTS_DIR}/applyIngwPatch.sh ./
\cp ${PATCH_SCRIPTS_DIR}/rollbackIngwPatch.sh ./
\cp ${PATCH_SCRIPTS_DIR}/verifyPatchApp.sh ./
\cp ${PATCH_SCRIPTS_DIR}/verifyPatchRollback.sh ./
chmod 777 applyIngwPatch.sh
chmod 777 rollbackIngwPatch.sh
chmod 777 verifyPatchApp.sh
chmod 777 verifyPatchRollback.sh
tar -cvf ${PKG_NAME}_${PKG_DATE}.tar commonConf.sh applyIngwPatch.sh rollbackIngwPatch.sh verifyPatchApp.sh verifyPatchRollback.sh ${PKG_NAME}.tar.gz

if [ -s "patchInfo_${PATCH_NUM}" ]
then
  tar -uvf ${PKG_NAME}_${PKG_DATE}.tar patchInfo_${PATCH_NUM}
fi

\cp ${PATCH_SCRIPTS_DIR}/updateParamInDb.sh ./
\cp ${PATCH_SCRIPTS_DIR}/rollBackParamInDb.sh ./
chmod 777 updateParamInDb.sh rollBackParamInDb.sh

tar -uvf ${PKG_NAME}_${PKG_DATE}.tar updateParamInDb.sh rollBackParamInDb.sh

echo "\nPackaging MOP application script for Patch [${PATCH_NUM}]..."
\cp ${PATCH_SCRIPTS_DIR}/applyIngwMop.sh ./
chmod 777 applyIngwMop.sh
tar -uvf ${PKG_NAME}_${PKG_DATE}.tar applyIngwMop.sh

gzip ${PKG_NAME}_${PKG_DATE}.tar
echo "\nCompleted preparing the INGW tar file \n${PKG_NAME}_${PKG_DATE}.tar.gz\nDONE."
removeTemporaryFiles

echo "DONE"

#if test -f script $BUILDROOT/createIngwPatch.log
#then
#  #Exit the script to file command
#  exit
#fi

#!/bin/bash

. ./commonConf.sh

bg_red=$'\e[0;41m'
normal=$'\e[0;00m'
bg_green=$'\e[0;42m'
bg_blue=$'\e[0;44m'

if test -s ~/profile.ingw
then
  . ~/profile.ingw
else
  echo "${bg_blue}FILE \"profile.ingw\" NOT FOUND. EXITING...${normal}"
  exit
fi

bg_red=$'\e[0;41m'
normal=$'\e[0;00m'
bg_green=$'\e[0;42m'
bg_blue=$'\e[0;44m'

cd $INSTALLROOT/INGw/${PLTFRM_DIR}

CKSUM_FILE_LIST="lib/libstk_ftha_witrc_widbg.so lib/libstk_ftha.so lib/libINGwCommonTypes.so lib/libINGwInfraUtil.so lib/libINGwInfraMsrMgr.so lib/libINGwInfraStreamManager.so lib/libINGwInfraParamRepository.so lib/libINGwInfraResourceMonitor.so lib/libINGwInfraTelnetIface.so lib/libINGwInfraStatMgr.so lib/libINGwInfraManager.so lib/libINGwLoadDistributor.so lib/libINGwSipProvider.so lib/libINGwSipMsgHandler.so lib/libINGwFtTalk.so lib/libINGwFtPacket.so lib/libINGwFtMessenger.so lib/libINGwTcapProvider.so lib/libTcapMessage.so lib/libINGwStackManager.so lib/libINGwIwf.so bin/INGw CommonLib/EmsLib/libEmsAgent.so CommonLib/EmsLib/libEmsCommon.so CommonLib/EmsLib/libEmsIdl.so CommonLib/EmsLib/libUtil.so scripts/DiagScr.sh conf/INGwSm_CCM1.xml"

if [ -f conf/INGwSm_CCM2.xml ]
then
  CKSUM_FILE_LIST="${CKSUM_FILE_LIST} conf/INGwSm_CCM2.xml"
fi

for i in `echo ${CKSUM_FILE_LIST}`
do
  cksum $i
done > ${curdir}/patchInfoAfterApp


cd ${curdir}

if [ ! -s "patchInfo_${PATCH_NUM}" ]
then
  echo "${bg_blue}FILE [patchInfo_${PATCH_NUM}] NOT FOUND${normal}"
  exit
elif [ ! -s "patchInfoAfterApp" ]
then
  echo "${bg_blue}FILE [patchInfoAfterApp] NOT FOUND${normal}"
  exit
else
  diff patchInfo_${PATCH_NUM} patchInfoAfterApp
fi
diff_result=$?
if [ ${diff_result} -eq 0 ]
then
  echo "${bg_green}PATCH[${PATCH_NUM}] APPLICATION IS SUCCESSFULL${normal}"
  exit
else
  echo "${bg_red}PATCH[${PATCH_NUM}] APPLICATION FAILED${normal}"
  exit
fi



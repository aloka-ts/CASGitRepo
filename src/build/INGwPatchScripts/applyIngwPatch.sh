#!/usr/bin/ksh

. ./commonConf.sh

bg_red=$'\e[0;41m'
normal=$'\e[0;00m'
bg_green=$'\e[0;42m'
bg_blue=$'\e[0;44m'

getCompSubsysId()
{
  if test $PLTFRM = "Linux"
    then
    myid=`whoami`
  else
    myid=`id | awk -F'(' '{ print $2 }' | awk -F')' '{ print $1 }'`
  fi
  UPPER_USERID=`echo $myid|tr '[a-z]' '[A-Z]'`
  UPPER_HOST=`hostname|tr '[a-z]' '[A-Z]'`
  HOST_ID=`sqlplus -silent ${CONN_STR} << %%
  set head off;
  select SUBSYSTEM_ID from rsisubsystem where NAME like '${1}_${UPPER_HOST}_${UPPER_USERID}';
%%`
  HOST_ID="`echo ${HOST_ID}`"
  echo "${1}_${UPPER_HOST}_${UPPER_USERID} subsystemId: [${HOST_ID}]"
}

updatePatchHistory()
{
    echo ""
    # Update patch History File
    echo "------------`date`-------" >> $INSTALLROOT/.INGW_Patch.history
    echo "INGW Patch - ${PKG_NAME} applied" >> $INSTALLROOT/.INGW_Patch.history
    echo "-------------------------------------------------------" >> $INSTALLROOT/.INGW_Patch.history

#-----------------------------------------------------------
#sqlplus $CONN_STR << END >> $logfile
#	
#--set termout off
#set serveroutput on;
#
#DECLARE
#ingwversion VARCHAR2(20);
#prevRec NUMBER(10);
#prevRec_Product_Detail NUMBER(10);
#prevRec_Install_Detail NUMBER(10);
#
#patch_ver VARCHAR2(10):= 'PatchVer';
#patchname VARCHAR2(10):= 'PatchNam';
#
#v_system_type NUMBER(10);
#v_cluster_id NUMBER(10);
#v_system_id NUMBER(10);
#name     VARCHAR2(100);
#host_id  NUMBER(10);
#v_time NUMBER(10);
#install_root VARCHAR2(500);
#expiry_date NUMBER(10);
#license_key VARCHAR2(100);
#
#CURSOR myCur IS
#SELECT subsystem_id FROM RSISUBSYSTEM where TYPE=97;
#BEGIN
#
#FOR rec IN myCur LOOP
#
#
#SELECT VERSION into ingwversion FROM RSI_INV_PRODUCT_DETAIL WHERE ACCEPTANCE_DATE = (SELECT MIN(ACCEPTANCE_DATE) FROM RSI_INV_PRODUCT_DETAIL WHERE PRODUCT_ID = 'IN Gateway, SAS');
#
#SELECT count(*) into prevRec FROM RSI_INV_INST_MODULE_DETAIL WHERE VERSION=patch_ver and SUBSYS_ID=rec.subsystem_id;
#SELECT count(*) into prevRec_Product_Detail FROM RSI_INV_PRODUCT_DETAIL WHERE VERSION=patch_ver and PRODUCT_ID='IN Gateway, SAS';
#SELECT count(*) into prevRec_Install_Detail FROM RSI_INV_INSTALLATION_DETAIL WHERE VERSION=patch_ver and PRODUCT_ID='IN Gateway, SAS';
#
#DBMS_OUTPUT.put_line('prevRec' || prevRec || 'prevRec_Product_Detail' || prevRec_Product_Detail || 'prevRec_Install_Detail' || prevRec_Install_Detail);
#
#IF prevRec = 0 AND prevRec_Product_detail = 0 AND prevRec_Install_Detail = 0
#THEN
#  
#  SELECT system_id INTO v_system_id FROM RSIFUNCTIONALUNITGROUP WHERE fug_id = ( SELECT fug_id FROM RSIFUNCTIONALUNIT WHERE fu_id = (SELECT fu_id FROM RSISUBSYSTEM WHERE subsystem_id=rec.subsystem_id));
#
#   SELECT cluster_id INTO v_cluster_id FROM RSISYSTEM WHERE system_id = v_system_id;
#   SELECT type INTO v_system_type FROM RSISYSTEM WHERE system_id = v_system_id;
#   SELECT host_id INTO host_id FROM RSISUBSYSTEM WHERE subsystem_id = rec.subsystem_id;  
#   SELECT install_root INTO install_root FROM RSI_INV_INSTALLATION_DETAIL WHERE INSTALLTIME = (SELECT MIN(INSTALLTIME) FROM RSI_INV_INSTALLATION_DETAIL WHERE product_id = 'IN Gateway, SAS');
#   SELECT expiry_date INTO expiry_date FROM RSI_INV_PRODUCT_DETAIL WHERE ACCEPTANCE_DATE = (SELECT MIN(ACCEPTANCE_DATE) FROM RSI_INV_PRODUCT_DETAIL WHERE product_id = 'IN Gateway, SAS');
#   SELECT license_key INTO license_key FROM RSI_INV_PRODUCT_DETAIL WHERE ACCEPTANCE_DATE = (SELECT MIN(ACCEPTANCE_DATE) FROM RSI_INV_PRODUCT_DETAIL WHERE product_id = 'IN Gateway, SAS');
#   SELECT ((SYSDATE - TO_DATE('19700101000000', 'YYYYMMDDHH24MISS') ) * 86400) - 19800 INTO v_time FROM DUAL;
# 
#  INSERT INTO RSI_INV_PRODUCT_DETAIL VALUES('IN Gateway, SAS',patch_ver,v_system_type,patchname,0,v_time,180,expiry_date,license_key,'GENBAND, Inc.',2,'GENBAND IN Gateway','none',0,null);
#  INSERT INTO RSI_INV_INSTALLATION_DETAIL VALUES('IN Gateway, SAS',patch_ver,v_system_type,v_cluster_id,v_system_id,v_time,install_root);
#  INSERT INTO RSI_INV_INST_MODULE_DETAIL VALUES('IN Gateway, SAS',patch_ver,v_system_type,v_cluster_id,v_system_id,rec.subsystem_id,v_time,patchname,host_id,2,patch_ver,0);
#
#  DBMS_OUTPUT.put_line('Tables are updated Successfully !.');
#ELSE
# DBMS_OUTPUT.put_line('Tables already have patch information. No need to update tables!.');
#END IF;
#
#END LOOP;
#END;
#/
#
#commit;
#show errors
#
#END
#----------------------------------------------------------
}



if test -s $PKG_NAME_EXT
then
  echo "Going to apply patch ${PKG_NAME_EXT} on this machine ..."
else
  echo "Patch Package ${PKG_NAME_EXT} does not exist in this directory. Exiting ..."
  exit 1
fi

PATCH_DIR=`pwd`

if test -s ~/profile.ingw
then
  echo ""
  echo "Upgrading INGw ..."
  echo ""
  echo ""
  echo "Setting environment variables....."
  . ~/profile.ingw

  # Move to $INSTALLROOT
  cd $INSTALLROOT

  mkdir -p $INSTALLROOT/BACKUP/${PATCH_NUM}
  echo ""
  echo "Collecting ORACLE Database Information...."

  echo "EMS DB USER CONNECT STRING (user/passwd@<SID or ServiceName>): \c"
  read CONN_STR
  if [ -z "${CONN_STR}" ]
  then
     echo "EMS DB CONNECT STRING NOT PASSED. Exiting..."
     exit 0
  fi


  #if test -f $INSTALLROOT/BACKUP/${PATCH_NUM}/applyIngwPatch.log
  #then
  #  mv $INSTALLROOT/BACKUP/${PATCH_NUM}/applyIngwPatch.log $INSTALLROOT/BACKUP/${PATCH_NUM}/applyIngwPatch_`date +%d%h_%H_%M_%S`.log
  #  echo "Moving file \"$INSTALLROOT/BACKUP/${PATCH_NUM}/applyIngwPatch.log\" to file \"$INSTALLROOT/BACKUP/${PATCH_NUM}/applyIngwPatch_`date +%d%h_%H_%M_%S`.log\""
  #fi
  #script $INSTALLROOT/BACKUP/${PATCH_NUM}/applyIngwPatch.log
  #echo "PATCH NUMBER : ${PATCH_NUM}"
  

  if test -s $INSTALLROOT/BACKUP/${PATCH_NUM}/`echo INGW${REL_NAME}`_backup.tar
  then
    echo $INSTALLROOT/BACKUP/${PATCH_NUM}/`echo INGW${REL_NAME}`_backup.tar already exists..Skipping Backup!!
    #echo "Do you want to continue to reapply this patch (Y|N)?"
    #read ans3
    #if [ $ans3 =  'n' -o $ans3 == 'N' ]
    #then
    #  echo "Exiting.."
    #  exit
    #fi
  else
    tar -cvf `echo INGW${REL_NAME}`_backup.tar \
    INGw/$PLTFRM_DIR/BuildEnv          \
    INGw/$PLTFRM_DIR/lib	             \
    INGw/$PLTFRM_DIR/bin/INGw          \
    INGw/$PLTFRM_DIR/scripts	         \
    INGw/$PLTFRM_DIR/conf              \
    INGw/$PLTFRM_DIR/CommonLib/EmsLib
    #INGw/$PLTFRM_DIR/CommonLib/TaoLib

    chmod 444 `echo INGW${REL_NAME}`_backup.tar 
    mv `echo INGW${REL_NAME}`_backup.tar $INSTALLROOT/BACKUP/${PATCH_NUM}

    echo ""
    echo "Backup [`echo INGW${REL_NAME}`_backup.tar] placed under $INSTALLROOT/BACKUP/${PATCH_NUM}"
    echo ""
  fi


  if [ -f $INSTALLROOT/BACKUP/${PATCH_NUM}/ParamUpdate.log ]
  then
   echo "DB Config Paramter change log in file \"$INSTALLROOT/BACKUP/${PATCH_NUM}/ParamUpdate.log\"" 
  else
    echo ""
    #echo "No DB Config Parameters has been changed in this patch"
  fi

  echo ""
  echo "Please make sure that the INGW are in stopped state on this machine."
  echo "Press enter to continue... "
  read dummy

  #Install the patch
  if test -s $curdir/${PKG_NAME_EXT}
  then
    echo ""
    echo "Installing the patch package..."
    if [ "$curdir/" == "$INSTALLROOT" ]
    then
      echo "Applying patch from current location [${INSTALLROOT}]...\c"
    else
      \cp -f $curdir/${PKG_NAME_EXT} ./
    fi

    gzip -cd ${PKG_NAME_EXT} | tar xvf -
    #tar xvf ${PKG_NAME_EXT}


    CDIR1=`pwd`
    # Getting the checksum of existing INGw libraries, binary and other file [
    cd INGw7.5.4.0/${PLTFRM_DIR}
    echo "Calculating CheckSum of existing Patch Level libraries, binary and other file"

    CKSUM_FILE_LIST="lib/libstk_ftha_witrc_widbg.so lib/libstk_ftha.so lib/libINGwCommonTypes.so  lib/libINGwInfraUtil.so lib/libINGwInfraMsrMgr.so lib/libINGwInfraStreamManager.so lib/libINGwInfraParamRepository.so lib/libINGwInfraResourceMonitor.so lib/libINGwInfraTelnetIface.so lib/libINGwInfraStatMgr.so lib/libINGwInfraManager.so lib/libINGwLoadDistributor.so lib/libINGwSipProvider.so lib/libINGwSipMsgHandler.so lib/libINGwFtTalk.so lib/libINGwFtPacket.so lib/libINGwFtMessenger.so lib/libINGwTcapProvider.so lib/libTcapMessage.so lib/libINGwStackManager.so lib/libINGwIwf.so bin/INGw CommonLib/EmsLib/libEmsAgent.so CommonLib/EmsLib/libEmsCommon.so CommonLib/EmsLib/libEmsIdl.so CommonLib/EmsLib/libUtil.so conf/ss7_sig_conf.sh conf/INGwSm_CCM1.xml"

    if [ -f conf/INGwSm_CCM2.xml ]
    then
      CKSUM_FILE_LIST="${CKSUM_FILE_LIST} conf/INGwSm_CCM2.xml"
    fi

    for i in `echo ${CKSUM_FILE_LIST}`
    do
      cksum $i
    done > ${curdir}/ptchInfoBefore_${PATCH_NUM}

    # ] - Getting the checksum of existing INGw libraries, binary and other file

    cd ${CDIR1}

    if [ -f ingw.tar ]
    then
      tar xvf ingw.tar 2>&1 | tee ${PATCH_DIR}/patchApplyLog_${PATCH_NUM}
      if [ $? -ne 0 ]
      then
        echo "${bg_red}ERROR extracting ingw.tar from patch tar ball${normal}"
        exit 1
      fi
    fi

    echo "INGw Upgraded Successfully"


    updatePatchHistory

    \rm -f ${PKG_NAME_EXT}
    \rm -f ingw.tar

    #if test -f script $INSTALLROOT/BACKUP/${PATCH_NUM}/applyINGWPatch.log
    #then
    #  #Exit the script to file command
    #  exit
    #fi

  else
    echo ""
    echo "Patch tar file: ${PKG_NAME_EXT} does not exist in $curdir"
    exit
  fi
else
  echo ""
  echo "File profile.ingw does not exist"
  echo "Cannot upgrade INGW"
  echo ""
  exit 1
fi


echo""
echo "Patch Applied SUCCESSFULLY."
echo "Start INGW component on this machine"

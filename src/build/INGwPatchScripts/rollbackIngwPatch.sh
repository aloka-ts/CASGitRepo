#!/usr/bin/ksh

. ./commonConf.sh

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

rollbackPatchHistory()
{
    echo ""
    # Remove entry from PatchHistory file
    echo "-----------`date`---------" >> $INSTALLROOT/.INGW_Patch.history
    echo "INGW Patch - ${PKG_NAME} rollbacked" >> $INSTALLROOT/.INGW_Patch.history
    echo "------------------------------------------" >> $INSTALLROOT/.INGW_Patch.history

getCompSubsysId INGW
SubsystemIdINGW=${HOST_ID}
if [ ! -z "$(echo ${SubsystemIdINGW} | sed -e 's/[0-9]//g')" ]
  then
  echo "\033[1mINVALID INGW SUBSYSTEMID[${SubsystemIdINGW}]. Exiting...\033[0m"
    exit 0
fi
sqlplus -silent ${CONN_STR} << %%
  set head off;
delete from RSI_INV_INST_MODULE_DETAIL where product_id='${PROD_ID}' and version='${PROD_VER}' and name='${PATCH_NUM}' and mod_version='${REL_NAME}' and subsys_id=$SubsystemIdINGW;
commit;
%%
}


PKG_NAME_EXT=`echo ${PKG_NAME}`_backup.tar
REL_NAME_EXT=`echo ${PKG_NAME_EXT} | cut -b9-`
#echo "REL_NAME=" $REL_NAME_EXT
echo ""
echo "Collecting ORACLE Database Information...."

echo "EMS DB USER CONNECT STRING (user/passwd@<SID or ServiceName>): \c"
read CONN_STR
if [ -z "${CONN_STR}" ]
then
  echo "EMS DB CONNECT STRING NOT PASSED. Exiting..."
  exit 0
fi

echo ""
echo "Please make sure that INGW component is in stopped state on this machine."
echo "Please make sure patches more recent than this are already rollbacked."
echo "Press enter to continue... "
read dummy


if test -s ~/profile.ingw
then
  echo ""
  echo "Setting environment variables....."
  . ~/profile.ingw

  cd $INSTALLROOT/BACKUP/${PATCH_NUM}
  
  #if test -f $INSTALLROOT/BACKUP/${PATCH_NUM}/rollbackIngwPatch.log
  #then
  #  mv $INSTALLROOT/BACKUP/${PATCH_NUM}/rollbackIngwPatch.log $INSTALLROOT/BACKUP/${PATCH_NUM}/rollbackIngwPatch_`date +%d%h_%H_%M_%S`.log
  #  echo "Moving file \"$INSTALLROOT\/BACKUP\/${PATCH_NUM}\/rollbackIngwPatch.log\" to file \"$INSTALLROOT\/BACKUP\/${PATCH_NUM}\/rollbackIngwPatch_`date +%d%h_%H_%M_%S`.log\""
  #fi
  #script $INSTALLROOT/BACKUP/${PATCH_NUM}/rollbackIngwPatch.log
  #echo "PATCH NUMBER : ${PATCH_NUM}"

  #Rollback the patch
  if test -s INGW${REL_NAME_EXT}
  then
    echo ""
    echo "Installing the backup package [INGW${REL_NAME_EXT}] for INGW..."
    cp INGW${REL_NAME_EXT} $INSTALLROOT
    cd $INSTALLROOT
    tar xvf INGW${REL_NAME_EXT} > /dev/null
    echo "INGW rollback done SUCCESSFULLY"

    rollbackPatchHistory

    \rm -f INGW${REL_NAME_EXT}
    \rm -f $INSTALLROOT/BACKUP/${PATCH_NUM}/INGW${REL_NAME_EXT}
  else
    echo ""
    echo "Backup tar files: INGW${REL_NAME_EXT} does not exist in $INSTALLROOT/BACKUP/${PATCH_NUM}"

    #if test -f script $INSTALLROOT/BACKUP/${PATCH_NUM}/rollbackIngwPatch.log
    #then
    #  #Exit the script to file command
    #  exit
    #fi
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
echo "Patch ROLLBACK done SUCCESSFULLY."
echo "Start INGW components on this machine"

#if test -f script $INSTALLROOT/BACKUP/${PATCH_NUM}/RollbackIngwPatch.log
#then
#  #Exit the script to file command
#  exit
#fi

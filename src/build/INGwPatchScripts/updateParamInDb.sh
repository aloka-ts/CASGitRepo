#!/bin/ksh



getCompSubsysId()
{
  PLTFRM=`uname`
  export PLTFRM
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


isDigit()
{
  if [ -z "${1}" ]
  then
    echo "Empty param"
    return 0
  fi

  if [ -z $(echo ${1} | sed -e 's/[0-9]//g') ]
  then
    return 1
  else
    return 0
  fi
}

checkValidity()
{
#  echo "checkValidity Param : [${1}]"
  if [ -z "${1}" ]
  then
    echo "Empty param"
    return 0
  fi

  count=`echo ${1} | wc -c`
  if [ ${count} -gt 4 ]
  then
    echo "Invalid value"
    return 0
  else
    isDigit ${1}
    retVal=$?
    if [ ${retVal} -ne 1 ]
    then
      echo "Non-Numeric value"
      return 0
    else
      return 1
    fi
  fi
}


testIp()
{
  if [ -z "${1}" ]
  then
    echo "Empty IP"
    return 0
  fi

  var1=`echo ${1} | cut -s -d'.' -f1`
  checkValidity ${var1}
  ret1=$?

  var2=`echo ${1} | cut -s -d'.' -f2`
  checkValidity ${var2}
  ret2=$?

  var3=`echo ${1} | cut -s -d'.' -f3`
  checkValidity ${var3}
  ret3=$?

  var4=`echo ${1} | cut -s -d'.' -f4`
  checkValidity ${var4}
  ret4=$?

  if [ ${ret1} -ne 1 -o ${ret2} -ne 1 -o ${ret3} -ne 1 -o ${ret4} -ne 1 ]
  then
    echo "INVALID IP [${1}]"
    return 0
  else
#    echo "VALID IP [${1}]"
    return 1
  fi

}

PATCH_NUM="INGw7.5.4.17"

if test -s ~/profile.ingw
then
  . ~/profile.ingw
  mkdir -p $INSTALLROOT/BACKUP/${PATCH_NUM}
  #if test -f $INSTALLROOT/BACKUP/${PATCH_NUM}/DBParamUpdate.log
  #then
  #  mv $INSTALLROOT/BACKUP/${PATCH_NUM}/DBParamUpdate.log $INSTALLROOT/BACKUP/${PATCH_NUM}/DBParamUpdate_`date +%d%h_%H_%M_%S`.log
  #  echo "Moving file \"$INSTALLROOT/BACKUP/${PATCH_NUM}/DBParamUpdate.log\" to file \"$INSTALLROOT/BACKUP/${PATCH_NUM}/DBParamUpdate_`date +%d%h_%H_%M_%S`.log\""
  #fi
  #script $INSTALLROOT/BACKUP/${PATCH_NUM}/DBParamUpdate.log
  #echo "PATCH NUMBER : ${PATCH_NUM}"
else
  echo "file ~/profile.ingw not found. Not logging Param Changes"
fi

echo ""
echo "Collecting ORACLE Database Information...."

echo "EMS DB USER CONNECT STRING (user/passwd@<SID or ServiceName>): \c"
read CONN_STR
if [ -z "${CONN_STR}" ]
then
  echo "EMS DB CONNECT STRING NOT PASSED. Exiting..."
  exit 0
fi

echo "INGW DB USER CONNECT STRING (user/passwd@<SID or ServiceName>): \c"
read CONN_STR_INGW
if [ -z "${CONN_STR_INGW}" ]
then
  echo "INGW DB CONNECT STRING NOT PASSED. Exiting..."
  exit 0
fi

getCompSubsysId INGW
SubsystemId=${HOST_ID}
if [ ! -z "$(echo ${SubsystemId} | sed -e 's/[0-9]//g')" ]
  then
    echo "\033[1mINVALID INGW SUBSYSTEMID[${SubsystemId}]. Exiting...\033[0m"
    exit 0
fi


sqlplus -silent ${CONN_STR} << %%
  set head off;
  !echo ""
  !echo "CURRENT VALUES"

  -- Patch NSP6.0.2
  -- CCM Normal CPU Utilization to 75 from 50
  select * from rsicomponent_config where param_oid='8.1.18' and component_id=${SubsystemId};
  --  Number of CCM Worker Threads from 4 to 7
  select * from rsicomponent_config where param_oid='8.1.22' and component_id=${SubsystemId};

%%

choice=1
while [ $choice -eq 1 ]  #[
do
  echo "Enter the private IP(as Self tcap IP) of the machine : \c"
  read McPvtIp

  testIp $McPvtIp

  if [ $? -ne 1 ] #[
  then
     echo "You have Entered an Invaid IP"
     echo "Press 1 if you wish to continue"
     read val
     choice=val
  else
     choice=0
  fi #]

done #]

sqlplus -silent ${CONN_STR} << %%
  set head off;

  -- Patch NSP6.0.2
  -- Change the default CCM Normal CPU Utilization to 75 from 50
  update rsicomponent_config set param_val='75', new_param_val='75' where param_oid='8.1.18' and component_id=${SubsystemId};
  --  Increased Number of CCM Worker Threads from 4 to 7
  update rsicomponent_config set param_val='7', new_param_val='7' where param_oid='8.1.22' and component_id=${SubsystemId};

  commit;

  !echo ""
  !echo "NEW VALUES"
  -- Patch NSP6.0.2
  -- CCM Normal CPU Utilization to 75 from 50
  select * from rsicomponent_config where param_oid='8.1.18' and component_id=${SubsystemId};
  --  Increased Number of CCM Worker Threads from 4 to 7
  select * from rsicomponent_config where param_oid='8.1.22' and component_id=${SubsystemId};
  

%%

echo "Changed Max Normal CPU Utilization(8.1.18) to 75"
echo "Changed CCM Max Worker Threads(8.1.22) to 7"
echo "Active Call MaxHWM(8.1.16), HWM(8.1.14), MaxLWM(8.1.17), LWM(8.1.15) MaxInviteInProgress(8.2.11) changed successfully for SUBSYSTEM[${SubsystemId}]"


#if test -f $INSTALLROOT/BACKUP/${PATCH_NUM}/DBParamUpdate.log
#then
#  #Exit the script to file command
#  exit
#fi

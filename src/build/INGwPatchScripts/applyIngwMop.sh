#!/bin/bash

bg_red=$'\e[0;41m'
normal=$'\e[0;00m'
bg_green=$'\e[0;42m'
bg_yellow=$'\e[0;43m'
bg_blue=$'\e[0;44m'

insertIngwParameter()
{
  FILE_TO_ADD_PROP=${1}
  
  if [ ! -s $FILE_TO_ADD_PROP ] # [
  then
    echo "${bg_red}File ${FILE_TO_ADD_PROP} not found. Returning${normal}"
    return
  fi

  FOUND_STR_1=''
  FOUND_STR_2=''

  FOUND_STR_1=`grep "${2}" ${FILE_TO_ADD_PROP}`
  return_val_1=$?
  if [ $return_val_1 -eq 0 ]
  then
    FOUND_STR_2=`echo $FOUND_STR_1|grep -v "[#].*${2}"`
    return_val_2=$?
  fi

  if [ $return_val_1 -eq 1 ]
  then
    echo "\"${2}\""
    echo -e "${2}" >> ${FILE_TO_ADD_PROP}_Insert
  else

   if [ $return_val_2 -eq  0 ]
   then
    echo "${bg_yellow}Not applying \"${2}\", since it is already present.${normal}"
   else
     if [ $return_val_2 -eq  1 ]
     then
       echo -e "${bg_yellowblue}FOUND_STR [\n${FOUND_STR_1}\n]\nNot applying \"${2}\", since it is present but commented.${normal}"
       #echo -e "FOUND_STR [${FOUND_STR_1}].\n\"${2}\" present but commented."
       #echo -e "Applying \"${2}\", since it is commented."
       #echo -e "${2}" >> ${FILE_TO_ADD_PROP}_Insert
     else
       echo -e "Ret vals [$return_val_1] [$return_val_2]"
     fi
   fi

  fi
}

isDigit()
{
  if [ -z "${1}" ]
  then
#    echo "Empty param"
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
#    echo "Empty param"
    return 0
  fi

  count=`echo ${1} | wc -c`
  if [ ${count} -gt 4 ]
  then
#    echo "Invalid value"
    return 0
  else
    isDigit ${1}
    retVal=$?
    if [ ${retVal} -ne 1 ]
    then
#      echo "Non-Numeric value"
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
#    echo "Empty IP"
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
#    echo "INVALID IP [${1}]"
    return 0
  else
#    echo "VALID IP [${1}]"
    return 1
  fi

}

addRefIP()
{
  if [ -f $INSTALLROOT/HConfigFile.dat ] #[
  then

    cd $INSTALLROOT

    choice=1

  echo "Want to change the Reference IP of NSP (Signaling RefIP) : \c"
  read changeRefIp

  if [ "${changeRefIp}" != "Y" -a "${changeRefIp}" != "y" ]
  then
    echo "Not changing the RefIp"
    return
  else
    echo "changing the RefIp"
  fi

  while [ $choice -eq 1 ]  #[
  do

    echo "Enter the signaling reference IP"
    read sigRefIP

    testIp $sigRefIP

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

     sed -e s/^Ref_Network_IP:.*$/Ref_Network_IP:$sigRefIP/g HConfigFile.dat > Hcfg

     if [ -s "./Hcfg" ]
     then
       cp Hcfg HConfigFile.dat
     fi

     rm -f Hcfg

     cd -

  fi #]
}

addPropertyToINGwScript()
{
  INGW_SCRIPTS_DIR=$INSTALLROOT/$SUBSYS_DIR/${PLTFRM_DIR}/scripts
  cd ${INGW_SCRIPTS_DIR}
  
  for i in INGw
  do
    FILE_TO_ADD_PROP=${INGW_SCRIPTS_DIR}/${i}
  
    if [ -f $FILE_TO_ADD_PROP ] # [
    then
      exec_line_Num=`grep -n "\/${PLTFRM_DIR}\/bin\/INGw " ${FILE_TO_ADD_PROP} |grep -v "[#].*exec"|cut -d: -f1`
      echo "Line Number : [${exec_line_Num}]"
      if [ -z ${exec_line_Num} ] # [
      then
        echo -e "${bg_red}\"/${PLTFRM_DIR}/bin/INGw \" not found in file ${FILE_TO_ADD_PROP}${normal}"
        echo -e "Failed to apply MOP for INGw.\n"
      # ]
      else # [
        if [ -z $(echo ${exec_line_Num} | sed -e 's/[0-9]//g') ] # check whether line number obtained is num # [
        then
          echo "\"/${PLTFRM_DIR}/bin/INGw \" found in file ${FILE_TO_ADD_PROP} at line Number : [${exec_line_Num}]"
          line_num_prev_exec=`expr $exec_line_Num - 1`
      
          sed -n '1,'$line_num_prev_exec'p' ${FILE_TO_ADD_PROP} > ${FILE_TO_ADD_PROP}_Head
          sed -n ''$exec_line_Num',$p' ${FILE_TO_ADD_PROP} > ${FILE_TO_ADD_PROP}_Tail
      
          echo -e "\nFollowing are the MOP steps for INGW\n"
          >${FILE_TO_ADD_PROP}_Insert
          echo "### MOPs for Patch $PATCH_NUM ###"
          # Applied in Patch INGw7.5.4.?
          insertIngwParameter "${FILE_TO_ADD_PROP}" "${1}"
  
  
          echo -e "\n"
      
          if [ -s ${FILE_TO_ADD_PROP}_Head -a -s ${FILE_TO_ADD_PROP}_Tail ]
          then
            cat ${FILE_TO_ADD_PROP}_Head ${FILE_TO_ADD_PROP}_Insert ${FILE_TO_ADD_PROP}_Tail > ${FILE_TO_ADD_PROP}_New
            chmod +x ${FILE_TO_ADD_PROP}_New
            #\cp ${FILE_TO_ADD_PROP} ${FILE_TO_ADD_PROP}_Old
            \mv ${FILE_TO_ADD_PROP}_New ${FILE_TO_ADD_PROP}
  
  
            echo -e "Successfully applied the MOP for file ${FILE_TO_ADD_PROP}\n"
          else
            echo "${bg_red}Intermediate file of zero size. Check for multiple occurances of \"/${PLTFRM_DIR}/bin/INGw \" in file ${INGW_SCRIPTS_DIR}/${FILE_TO_ADD_PROP}${normal}"
            echo -e "Failed to apply MOP for INGw***\n"
          fi
      
          \rm ${FILE_TO_ADD_PROP}_Head ${FILE_TO_ADD_PROP}_Insert ${FILE_TO_ADD_PROP}_Tail
        # ]
        else # [
          echo "${bg_red}Invalid value [${exec_line_Num}] for exec line number"
          echo "\"/${PLTFRM_DIR}/bin/INGw \" not found in file ${INGW_SCRIPTS_DIR}/${FILE_TO_ADD_PROP}${normal}"
          echo -e "Failed to apply MOP for INGw***\n"
        fi # ]
      fi # ]
    # ]
    else # [
      echo -e "${bg_red}File ${FILE_TO_ADD_PROP} doesn't exists. Failed to apply MOP for INGw.${normal}\n"
    fi # ]
  done
}


addPropertyAtEndofFile()
{
  FILE_TO_ADD_PROP=${1}
  
  if [ -s $FILE_TO_ADD_PROP ] # [
  then
    >${FILE_TO_ADD_PROP}_Insert
    insertIngwParameter "${1}" "${2}"
    
    cat ${FILE_TO_ADD_PROP} ${FILE_TO_ADD_PROP}_Insert > ${FILE_TO_ADD_PROP}_New
    chmod +x ${FILE_TO_ADD_PROP}_New
    #\cp ${FILE_TO_ADD_PROP} ${FILE_TO_ADD_PROP}_Old
    \mv ${FILE_TO_ADD_PROP}_New ${FILE_TO_ADD_PROP}
    echo -e "${bg_green}Successfully applied the MOP for file ${FILE_TO_ADD_PROP}${normal}\n"
    
    \rm ${FILE_TO_ADD_PROP}_Insert
  # ]
  else # [
    echo -e "${bg_red}File ${FILE_TO_ADD_PROP} doesn't exists. Failed to apply MOP for INGw.${normal}\n"
  fi # ]
}


# Procedure to modify INGwSm_CCM1.xml file
updateInIngwSmCcm1Xml()
{
 # Replace low and High Dialogue ID 7.5.4.4
 # To avoid coredump in zt
 # START - [
 echo "### Updating property into INGwSm_CCM1.xml for Patch 7.5.4.4 ###"

 chmod 755 $INSTALLROOT/INGw/sol28g/conf/INGwSm_CCM1.xml

 cp $INSTALLROOT/INGw/sol28g/conf/INGwSm_CCM1.xml /tmp/INGwSm_CCM1.xml

 sed 's/16777300/1100000/g' /tmp/INGwSm_CCM1.xml > /tmp/INGwSm_CCM1.xml.1
 mv /tmp/INGwSm_CCM1.xml.1 /tmp/INGwSm_CCM1.xml

 sed 's/16827350/1150050/g' /tmp/INGwSm_CCM1.xml > /tmp/INGwSm_CCM1.xml.1
 mv /tmp/INGwSm_CCM1.xml.1 /tmp/INGwSm_CCM1.xml

 sed 's/16777350/1100050/g' /tmp/INGwSm_CCM1.xml > /tmp/INGwSm_CCM1.xml.1
 mv /tmp/INGwSm_CCM1.xml.1 /tmp/INGwSm_CCM1.xml

 mv $INSTALLROOT/INGw/sol28g/conf/INGwSm_CCM1.xml $INSTALLROOT/INGw/sol28g/conf/INGwSm_CCM1.xml.old

 mv /tmp/INGwSm_CCM1.xml $INSTALLROOT/INGw/sol28g/conf/INGwSm_CCM1.xml
 # ] - END

 # Change SG heart beat timer values 7.5.4.5
 # START - [
 echo "### Updating property into INGwSm_CCM1.xml for Patch 7.5.4.5 ###"
 cp $INSTALLROOT/INGw/sol28g/conf/INGwSm_CCM1.xml /tmp/INGwSm_CCM1.xml

 sed 's/"txHeartBeatTimerCfg" value="400"/"txHeartBeatTimerCfg" value="20"/g' /tmp/INGwSm_CCM1.xml > /tmp/INGwSm_CCM1.xml.1
 mv /tmp/INGwSm_CCM1.xml.1 /tmp/INGwSm_CCM1.xml

 sed 's/"rxHeartBeatTimerCfg" value="400"/"rxHeartBeatTimerCfg" value="40"/g' /tmp/INGwSm_CCM1.xml > /tmp/INGwSm_CCM1.xml.1
 mv /tmp/INGwSm_CCM1.xml.1 /tmp/INGwSm_CCM1.xml

 mv $INSTALLROOT/INGw/sol28g/conf/INGwSm_CCM1.xml $INSTALLROOT/INGw/sol28g/conf/INGwSm_CCM1.xml.old

 mv /tmp/INGwSm_CCM1.xml $INSTALLROOT/INGw/sol28g/conf/INGwSm_CCM1.xml
 # ] - END

 # To make this cumulative patch compatible on top of base release(7.5.4.0) 7.5.4.9
 # START - [
 echo "### Updating property into INGwSm_CCM1.xml for Patch 7.5.4.9 ###"
 cp $INSTALLROOT/INGw/sol28g/conf/INGwSm_CCM1.xml /tmp/INGwSm_CCM1.xml

 sed 's/name="nmbDlgs" value="20050"/name="nmbDlgs" value="50050"/g' /tmp/INGwSm_CCM1.xml > /tmp/INGwSm_CCM1.xml.1
 mv /tmp/INGwSm_CCM1.xml.1 /tmp/INGwSm_CCM1.xml

 sed 's/name="nmbInvs" value="20050"/name="nmbInvs" value="50050"/g' /tmp/INGwSm_CCM1.xml > /tmp/INGwSm_CCM1.xml.1
 mv /tmp/INGwSm_CCM1.xml.1 /tmp/INGwSm_CCM1.xml

 sed 's/name="nmbBins" value="120000"/name="nmbBins" value="258250"/g' /tmp/INGwSm_CCM1.xml > /tmp/INGwSm_CCM1.xml.1
 mv /tmp/INGwSm_CCM1.xml.1 /tmp/INGwSm_CCM1.xml

 sed 's/name="hiDlgId" value="16797300"/name="hiDlgId" value="1150050"/g' /tmp/INGwSm_CCM1.xml > /tmp/INGwSm_CCM1.xml.1
 mv /tmp/INGwSm_CCM1.xml.1 /tmp/INGwSm_CCM1.xml

 sed 's/name="loDlgId" value="16777351"/name="loDlgId" value="1100050"/g' /tmp/INGwSm_CCM1.xml > /tmp/INGwSm_CCM1.xml.1
 mv /tmp/INGwSm_CCM1.xml.1 /tmp/INGwSm_CCM1.xml

 sed 's/name="hiDlgId" value="16797351"/name="hiDlgId" value="1150050"/g' /tmp/INGwSm_CCM1.xml > /tmp/INGwSm_CCM1.xml.1
 mv /tmp/INGwSm_CCM1.xml.1 /tmp/INGwSm_CCM1.xml

 sed 's/name="nmbDlgs" value="20000"/name="nmbDlgs" value="50000"/g' /tmp/INGwSm_CCM1.xml > /tmp/INGwSm_CCM1.xml.1
 mv /tmp/INGwSm_CCM1.xml.1 /tmp/INGwSm_CCM1.xml

 sed 's/name="nmbInvs" value="20000"/name="nmbInvs" value="50000"/g' /tmp/INGwSm_CCM1.xml > /tmp/INGwSm_CCM1.xml.1
 mv /tmp/INGwSm_CCM1.xml.1 /tmp/INGwSm_CCM1.xml

 sed 's/name="nmbBins" value="108000"/name="nmbBins" value="258000"/g' /tmp/INGwSm_CCM1.xml > /tmp/INGwSm_CCM1.xml.1
 mv /tmp/INGwSm_CCM1.xml.1 /tmp/INGwSm_CCM1.xml

 mv $INSTALLROOT/INGw/sol28g/conf/INGwSm_CCM1.xml $INSTALLROOT/INGw/sol28g/conf/INGwSm_CCM1.xml.old

 mv /tmp/INGwSm_CCM1.xml $INSTALLROOT/INGw/sol28g/conf/INGwSm_CCM1.xml
 # ] - END

 
}


# Procedure to modify ss7_sig_conf.sh file
updateInSs7SigConf()
{
  echo ""
 # Patch 7.5.4.7 START - [
 echo "### Updating property into ss7_sig_conf.sh for Patch 7.5.4.7 ###"

 chmod 755 $INSTALLROOT/INGw/sol28g/conf/ss7_sig_conf.sh

 cp $INSTALLROOT/INGw/sol28g/conf/ss7_sig_conf.sh /tmp/ss7_sig_conf.sh

 # Update MTP2 timer values as per NTT standard
 sed 's/MTP2_TMR_T1=4500/MTP2_TMR_T1=1500/g' /tmp/ss7_sig_conf.sh > /tmp/ss7_sig_conf.sh.1
 mv /tmp/ss7_sig_conf.sh.1 /tmp/ss7_sig_conf.sh

 sed 's/MTP2_TMR_T2=2500/MTP2_TMR_T2=500/g' /tmp/ss7_sig_conf.sh > /tmp/ss7_sig_conf.sh.1
 mv /tmp/ss7_sig_conf.sh.1 /tmp/ss7_sig_conf.sh

 sed 's/MTP2_TMR_T3=150/MTP2_TMR_T3=300/g' /tmp/ss7_sig_conf.sh > /tmp/ss7_sig_conf.sh.1
 mv /tmp/ss7_sig_conf.sh.1 /tmp/ss7_sig_conf.sh

 sed 's/MTP2_TMR_T5=50/MTP2_TMR_T5=20/g' /tmp/ss7_sig_conf.sh > /tmp/ss7_sig_conf.sh.1
 mv /tmp/ss7_sig_conf.sh.1 /tmp/ss7_sig_conf.sh

 sed 's/MTP2_TMR_T6=300/MTP2_TMR_T6=500/g' /tmp/ss7_sig_conf.sh > /tmp/ss7_sig_conf.sh.1
 mv /tmp/ss7_sig_conf.sh.1 /tmp/ss7_sig_conf.sh

 sed 's/MTP2_TMR_T7=200/MTP2_TMR_T7=200/g' /tmp/ss7_sig_conf.sh > /tmp/ss7_sig_conf.sh.1
 mv /tmp/ss7_sig_conf.sh.1 /tmp/ss7_sig_conf.sh

 sed 's/MTP2_PROVEMRGCY=50/MTP2_PROVEMRGCY=300/g' /tmp/ss7_sig_conf.sh > /tmp/ss7_sig_conf.sh.1
 mv /tmp/ss7_sig_conf.sh.1 /tmp/ss7_sig_conf.sh

 sed 's/MTP2_PROVNORMAL=500/MTP2_PROVNORMAL=300/g' /tmp/ss7_sig_conf.sh > /tmp/ss7_sig_conf.sh.1
 mv /tmp/ss7_sig_conf.sh.1 /tmp/ss7_sig_conf.sh

 sed 's/MTP2_SUERM_THRESH=64/MTP2_SUERM_THRESH=5/g' /tmp/ss7_sig_conf.sh > /tmp/ss7_sig_conf.sh.1
 mv /tmp/ss7_sig_conf.sh.1 /tmp/ss7_sig_conf.sh

 sed 's/MTP2_SUERM_ERR_RATE=256/MTP2_SUERM_ERR_RATE=285/g' /tmp/ss7_sig_conf.sh > /tmp/ss7_sig_conf.sh.1
 mv /tmp/ss7_sig_conf.sh.1 /tmp/ss7_sig_conf.sh

 sed 's/MTP2_SDTIE=2/MTP2_SDTIE=1/g' /tmp/ss7_sig_conf.sh > /tmp/ss7_sig_conf.sh.1
 mv /tmp/ss7_sig_conf.sh.1 /tmp/ss7_sig_conf.sh

 sed 's/MTP2_SDTIN=10/MTP2_SDTIN=1/g' /tmp/ss7_sig_conf.sh > /tmp/ss7_sig_conf.sh.1
 mv /tmp/ss7_sig_conf.sh.1 /tmp/ss7_sig_conf.sh

 sed 's/MTP2_TMR_TF=50/MTP2_TMR_TF=2/g' /tmp/ss7_sig_conf.sh > /tmp/ss7_sig_conf.sh.1
 mv /tmp/ss7_sig_conf.sh.1 /tmp/ss7_sig_conf.sh

 sed 's/MTP2_TMR_TO=20/MTP2_TMR_TO=2/g' /tmp/ss7_sig_conf.sh > /tmp/ss7_sig_conf.sh.1
 mv /tmp/ss7_sig_conf.sh.1 /tmp/ss7_sig_conf.sh

 sed 's/MTP2_TMR_TA=10/MTP2_TMR_TA=2/g' /tmp/ss7_sig_conf.sh > /tmp/ss7_sig_conf.sh.1
 mv /tmp/ss7_sig_conf.sh.1 /tmp/ss7_sig_conf.sh

 sed 's/MTP2_TMR_TS=10/MTP2_TMR_TS=2/g' /tmp/ss7_sig_conf.sh > /tmp/ss7_sig_conf.sh.1
 mv /tmp/ss7_sig_conf.sh.1 /tmp/ss7_sig_conf.sh

 # Update property to enable TCAP Message replication to peer INC
 sed 's/INGW_DISABLE_MSG_FT=1/INGW_DISABLE_MSG_FT=0/g' /tmp/ss7_sig_conf.sh > /tmp/ss7_sig_conf.sh.1
 mv /tmp/ss7_sig_conf.sh.1 /tmp/ss7_sig_conf.sh

 mv $INSTALLROOT/INGw/sol28g/conf/ss7_sig_conf.sh $INSTALLROOT/INGw/sol28g/conf/ss7_sig_conf.sh.old

 mv /tmp/ss7_sig_conf.sh $INSTALLROOT/INGw/sol28g/conf/ss7_sig_conf.sh
 # ] Patch 7.5.4.7 - END

 # Patch 7.5.4.9 START - [
 echo "### Updating property into ss7_sig_conf.sh for Patch 7.5.4.7 ###"

 chmod 755 $INSTALLROOT/INGw/sol28g/conf/ss7_sig_conf.sh

 cp $INSTALLROOT/INGw/sol28g/conf/ss7_sig_conf.sh /tmp/ss7_sig_conf.sh

 # Update MTP2 timer values as per NTT standard
 sed 's/SM_TRANSPORT_TYPE=0/SM_TRANSPORT_TYPE=2/g' /tmp/ss7_sig_conf.sh > /tmp/ss7_sig_conf.sh.1
 mv /tmp/ss7_sig_conf.sh.1 /tmp/ss7_sig_conf.sh

 mv $INSTALLROOT/INGw/sol28g/conf/ss7_sig_conf.sh $INSTALLROOT/INGw/sol28g/conf/ss7_sig_conf.sh.old

 mv /tmp/ss7_sig_conf.sh $INSTALLROOT/INGw/sol28g/conf/ss7_sig_conf.sh
 # ] Patch 7.5.4.9 - END

 # Patch 7.5.4.12 START - [
 echo "### Updating property into ss7_sig_conf.sh for Patch 7.5.4.12 ###"

 chmod 755 $INSTALLROOT/INGw/sol28g/conf/ss7_sig_conf.sh

 cp $INSTALLROOT/INGw/sol28g/conf/ss7_sig_conf.sh /tmp/ss7_sig_conf.sh

 sed 's/MTP2_TMR_TF=2$/MTP2_TMR_TF=24/g' /tmp/ss7_sig_conf.sh > /tmp/ss7_sig_conf.sh.1
 mv /tmp/ss7_sig_conf.sh.1 /tmp/ss7_sig_conf.sh

 sed 's/MTP2_TMR_TO=2$/MTP2_TMR_TO=24/g' /tmp/ss7_sig_conf.sh > /tmp/ss7_sig_conf.sh.1
 mv /tmp/ss7_sig_conf.sh.1 /tmp/ss7_sig_conf.sh

 sed 's/MTP2_TMR_TA=2$/MTP2_TMR_TA=24/g' /tmp/ss7_sig_conf.sh > /tmp/ss7_sig_conf.sh.1
 mv /tmp/ss7_sig_conf.sh.1 /tmp/ss7_sig_conf.sh

 sed 's/MTP2_TMR_TS=2$/MTP2_TMR_TS=24/g' /tmp/ss7_sig_conf.sh > /tmp/ss7_sig_conf.sh.1
 mv /tmp/ss7_sig_conf.sh.1 /tmp/ss7_sig_conf.sh
 
 sed 's/export SM_XML_FILE/#export SM_XML_FILE/g' /tmp/ss7_sig_conf.sh > /tmp/ss7_sig_conf.sh.1
 mv /tmp/ss7_sig_conf.sh.1 /tmp/ss7_sig_conf.sh

 mv $INSTALLROOT/INGw/sol28g/conf/ss7_sig_conf.sh $INSTALLROOT/INGw/sol28g/conf/ss7_sig_conf.sh.old

 mv /tmp/ss7_sig_conf.sh $INSTALLROOT/INGw/sol28g/conf/ss7_sig_conf.sh
 # ] Patch 7.5.4.12 - END

 # Patch 7.5.4.14 START - [
 echo "### Updating property into ss7_sig_conf.sh for Patch 7.5.4.14 ###"

 chmod 755 $INSTALLROOT/INGw/sol28g/conf/ss7_sig_conf.sh

 cp $INSTALLROOT/INGw/sol28g/conf/ss7_sig_conf.sh /tmp/ss7_sig_conf.sh

 sed 's/export MAX_STREAM_SIZE/#export MAX_STREAM_SIZE/g' /tmp/ss7_sig_conf.sh > /tmp/ss7_sig_conf.sh.1
 mv /tmp/ss7_sig_conf.sh.1 /tmp/ss7_sig_conf.sh

 mv $INSTALLROOT/INGw/sol28g/conf/ss7_sig_conf.sh $INSTALLROOT/INGw/sol28g/conf/ss7_sig_conf.sh.old

 mv /tmp/ss7_sig_conf.sh $INSTALLROOT/INGw/sol28g/conf/ss7_sig_conf.sh
 # ] Patch 7.5.4.14 - END

}




. ./commonConf.sh

MOP_FOR_INGW=1

if test -s ~/profile.ingw # [
then
  echo "Setting environment variables..."
  . ~/profile.ingw

  if [ ${MOP_FOR_INGW} -eq 1 ] # [
  then

    echo -e "\nFollowing are the MOP steps for INGW\n"

    # Should be called only if any modification
    # needed in INGwSm_CCM1.xml else comment it. 
    echo "### Updating configuration file INGwSm_CCM1.xml ###"
    updateInIngwSmCcm1Xml

    # update existing property in configuration file ss7_sig_conf.sh
    #echo -e "\n### Updating configuration file ss7_sig_conf.sh ###"
    updateInSs7SigConf

    # Add new property in INGw launch script INGw
    echo -e "\n### Add new property in INGw launch script INGw ###"
    # Applied in Patch INGw7.5.4.12 - Start [
    addPropertyToINGwScript "#To control TCP SOCKET SND RCV BUFF SIZE"
    addPropertyToINGwScript "export INGW_TCP_SND_RCV=262144"
    # ] INGw7.5.4.12 - End 


    # Applied in Patch INGw7.5.4.6 - Start [
    # Add new property in configuration file ss7_sig_conf.sh
    echo "### Adding property into ss7_sig_conf.sh for Patch $PATCH_NUM ###"
    addPropertyAtEndofFile "$INSTALLROOT/INGw/${PLTFRM_DIR}/conf/ss7_sig_conf.sh" "#DEF_DBG_MASK values: 131071 -> all layers, bits representation for layers (right to left):"
    addPropertyAtEndofFile "$INSTALLROOT/INGw/${PLTFRM_DIR}/conf/ss7_sig_conf.sh" "#TCAP,PSF_TCAP,SCCP,PSF_SCCP,M3UA,PSF_M3UA,LDF_M3UA,SCTP,TUCL,MTP3,"
    addPropertyAtEndofFile "$INSTALLROOT/INGw/${PLTFRM_DIR}/conf/ss7_sig_conf.sh" "#PSF_MTP3,LDF_MTP3,MTP2,RELAY,SG,MR,SH"
    addPropertyAtEndofFile "$INSTALLROOT/INGw/${PLTFRM_DIR}/conf/ss7_sig_conf.sh" "#RESET:0, ALL:131071, SH+MR+SG+RELAY: 122880"
    addPropertyAtEndofFile "$INSTALLROOT/INGw/${PLTFRM_DIR}/conf/ss7_sig_conf.sh" "#export DEF_DBG_MASK=131071"
    # ] INGw7.5.4.6 - End 

    # Applied in Patch INGw7.5.4.7 - Start [
    addPropertyAtEndofFile "$INSTALLROOT/INGw/${PLTFRM_DIR}/conf/ss7_sig_conf.sh" "#U-ABORT User Information octet stream NTT standard"
    addPropertyAtEndofFile "$INSTALLROOT/INGw/${PLTFRM_DIR}/conf/ss7_sig_conf.sh" "export NTT_UABORT_USR_INFO=\"280f06080283386603020600a0030a0100\""

    #addPropertyAtEndofFile "$INSTALLROOT/INGw/${PLTFRM_DIR}/conf/ss7_sig_conf.sh" "#Time since no SIP message has been received from SAS,"
    #addPropertyAtEndofFile "$INSTALLROOT/INGw/${PLTFRM_DIR}/conf/ss7_sig_conf.sh" "#used for connectivity failure detection, because ideally"
    #addPropertyAtEndofFile "$INSTALLROOT/INGw/${PLTFRM_DIR}/conf/ss7_sig_conf.sh" "#NOTIFY or OPTIONS should keep on flowing if both INC and SAS are up."
    #addPropertyAtEndofFile "$INSTALLROOT/INGw/${PLTFRM_DIR}/conf/ss7_sig_conf.sh" "export INGW_NW_ALIVE_TIME=30"
    # ] INGw7.5.4.7 - End 

    # Applied in Patch INGw7.5.4.9 - Start [

    addPropertyAtEndofFile "$INSTALLROOT/INGw/${PLTFRM_DIR}/conf/ss7_sig_conf.sh" "# Unit is 100s of millis secs."
    addPropertyAtEndofFile "$INSTALLROOT/INGw/${PLTFRM_DIR}/conf/ss7_sig_conf.sh" "export INVOKE_TIMER=0"

    addPropertyAtEndofFile "$INSTALLROOT/INGw/${PLTFRM_DIR}/conf/ss7_sig_conf.sh" "# MAX SIZE for log file rollover"
    addPropertyAtEndofFile "$INSTALLROOT/INGw/${PLTFRM_DIR}/conf/ss7_sig_conf.sh" "export MAX_STREAM_SIZE=50000000"

    addPropertyAtEndofFile "$INSTALLROOT/INGw/${PLTFRM_DIR}/conf/ss7_sig_conf.sh" "#Time since no SIP message has been received from SAS,"
    addPropertyAtEndofFile "$INSTALLROOT/INGw/${PLTFRM_DIR}/conf/ss7_sig_conf.sh" "#used for connectivity failure detection, because ideally"
    addPropertyAtEndofFile "$INSTALLROOT/INGw/${PLTFRM_DIR}/conf/ss7_sig_conf.sh" "#NOTIFY or OPTIONS should keep on flowing if both INC and SAS are up."
    addPropertyAtEndofFile "$INSTALLROOT/INGw/${PLTFRM_DIR}/conf/ss7_sig_conf.sh" "export INGW_NW_ALIVE_TIME=30"
    # ] INGw7.5.4.9 - End 

    # Applied in Patch INGw7.5.4.12 - Start [
    addPropertyAtEndofFile "$INSTALLROOT/INGw/${PLTFRM_DIR}/conf/ss7_sig_conf.sh" "#define this to parameter buffer string of RC"
    addPropertyAtEndofFile "$INSTALLROOT/INGw/${PLTFRM_DIR}/conf/ss7_sig_conf.sh" "export NTT_RC_PARAM_BUF=\"040283A9\""
    
    addPropertyAtEndofFile "$INSTALLROOT/INGw/${PLTFRM_DIR}/conf/ss7_sig_conf.sh" "#define this to alter abort cause (default 0x01)"
    addPropertyAtEndofFile "$INSTALLROOT/INGw/${PLTFRM_DIR}/conf/ss7_sig_conf.sh" "export NTT_ABRT_CAUSE=1"
    
    addPropertyAtEndofFile "$INSTALLROOT/INGw/${PLTFRM_DIR}/conf/ss7_sig_conf.sh" "#define as 1 to generate all ABORT"
    addPropertyAtEndofFile "$INSTALLROOT/INGw/${PLTFRM_DIR}/conf/ss7_sig_conf.sh" "#define as 2 to generate ABORT/END in error scenarios bases on transaction state"
    addPropertyAtEndofFile "$INSTALLROOT/INGw/${PLTFRM_DIR}/conf/ss7_sig_conf.sh" "export NTT_DLG_CLOSE_OPT=2"
    # ] INGw7.5.4.12 - End 

  fi # ]
# ]

else # [
  echo -e "${bg_red}\nFile profile.ingw does not exist${normal}"
  echo -e "${bg_red}Cannot apply NSP MOP${normal}\n"
fi # ]



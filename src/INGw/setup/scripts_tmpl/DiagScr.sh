#!/bin/bash

export LOG_PATH="/LOGS/INGw"
export LOG_FILE="${LOG_PATH}/DiagLog.log"

log()
{
  mac=""
  dt=""
  dem=""
  out_str=""
  no_out=0
  file=""
  mod="[1m$MODULE[0m::"
  tid=""

  while getopts mtdnf:cs:i:a: choice
  do
   case $choice in
    m) mac="`hostname`:"
  	;;
    t) dt=`date +%c`
  	;;
    d) dem="\n++++++++++++++++++++++++++++++++++++++++++++++++++"
  	;;
    s) out_str="`echo "${OPTARG}"`"
  	;;
    n) no_out=1
  	;;
    f) file=${OPTARG}
  	;;
    c) mod=""
  	;;
    i) tid=${OPTARG}
  	;;
    a) act=${OPTARG}
  	;;
    \?) out_str="[1m[31mINVALID NUM OF AGR PASSED TO log FUNC[0m"
  	;;
   esac
  done

  if [ ${no_out} -eq 1 ]
  then 
    if [ ! -z "${file}" ]
    then
      echo -e "${mac}${dt}PID[${$}] PPID[${PPID}] TID[${tid}] ACT[${act}]::${mod}${out_str}${dem}" >> ${file}
    else
      echo -e "${mac}${dt}${mod}PID[${$}] PPID[${PPID}] TID[${tid}] ACT[${act}]::${out_str}${dem}" >> ${LOG_FILE}
    fi
  else
    if [ ! -z "${file}" ]
    then
      echo -e "${mac}${dt}PID[${$}] PPID[${PPID}] TID[${tid}] ACT[${act}]::${mod}${out_str}${dem}" | tee -a ${file}
    else
      echo -e "${mac}${dt}${mod}PID[${$}] PPID[${PPID}] TID[${tid}] ACT[${act}]::${out_str}${dem}" | tee -a ${LOG_FILE}
    fi
  fi
}


chekForOtherInstanceRunning()
{
  grep_result=`ps -eaf | grep -v grep | grep "/bin/bash ${1}"`
  echo -e "PID[$$] PPID[${PPID}] THREAD_ID[${4}] ACTION[${3}] Complete grep_result [\n${grep_result}]" >> ${LOG_FILE}
  grep_result=`ps -eaf | grep -v grep | grep "/bin/bash ${1} ${2} ${3}"`
  echo -e "PID[$$] PPID[${PPID}] THREAD_ID[${4}] ACTION[${3}] grep_result [\n${grep_result}]" >> ${LOG_FILE}
  scr_count=`echo "${grep_result}" | wc -l`
  #scr_count=`ps -eaf | grep -v grep | grep "/bin/bash ${0} ${1} ${2} ${3}" | wc -l`
  echo "PID[$$] PPID[${PPID}] THREAD_ID[${4}] [/bin/bash ${1} ${2} ${3}] SCRIPTS COUNT ["${scr_count}"]" >> ${LOG_FILE}

  if [ -z $(echo ${scr_count} | sed -e 's/[0-9]//g') ]
  then
    echo "PID[$$] PPID[${PPID}] THREAD_ID[${4}] NUMB OF SCRIPTS ["${scr_count}"] IS VALID NUMBER" >> ${LOG_FILE}
  else
    echo "PID[$$] PPID[${PPID}] THREAD_ID[${4}] NUMB OF SCRIPTS ["${scr_count}"] IS INVALID NUMBER" >> ${LOG_FILE}
  fi

  #The first process would be the temporary bash shell
  if [ "${scr_count}" -gt 2 ]
  then
    echo "PID[$$] PPID[${PPID}] THREAD_ID[${4}] SCRIPT ALREADY RUNNING. Quiting ..." >> ${LOG_FILE}
    exit 0
  fi
}

activeCallScenario()
{
  echo "PID[$$] PPID[${PPID}] THREAD_ID[${1}] activeCallScenario" >> ${LOG_FILE}

  #Check if snoop is already running
  SNOOP_UDP_SCTP_FOUND=0
  if [ -s /tmp/snid_a ]
  then
    echo "/tmp/snid_a File[\n`cat /tmp/snid_a`\n]" >> ${LOG_FILE}
    for i in `cat /tmp/snid_a`
    do
      snoop_ps_grep=`/usr/ucb/ps -auxww|grep -v grep | grep ${i}`
      if [ ! -z "${snoop_ps_grep}" ]
      then
        SNOOP_UDP_SCTP_FOUND=1
        echo "PID[$$] PPID[${PPID}] THREAD_ID[${1}] activeCallScenario SNOOP SCTP-UDP already running" >> ${LOG_FILE}
      fi
    done
  else
    echo "PID[$$] PPID[${PPID}] THREAD_ID[${1}] activeCallScenario NO PROCESS FOUND snid_a" >> ${LOG_FILE}
  fi
  
  if [ ${SNOOP_UDP_SCTP_FOUND} -eq 0 ]
  then
    nohup snoop -d ${INTERFACE_NAME} -o /LOGS/INGw/snoop_udp_${$}.cap ip proto 17 &
    echo "${!}" >> /tmp/snid_a
    SNOOP_PIDS_AC="${SNOOP_PIDS_AC} ${!}"
    nohup snoop -d ${INTERFACE_NAME} -o /LOGS/INGw/snoop_sctp_${$}.cap ip proto 132 &
    echo "${!}" >> /tmp/snid_a
    SNOOP_PIDS_AC="${SNOOP_PIDS_AC} ${!}"
    echo "PID[$$] PPID[${PPID}] THREAD_ID[${1}] activeCallScenario`/usr/ucb/ps -auxww|grep -i snoop | grep -v grep`" >> ${LOG_FILE}
  fi

  echo "`date`" >> ${LOG_FILE}
  echo "`/sbin/ifconfig -a`" >> ${LOG_FILE}
  echo "`/bin/prstat 1 5`" >> ${LOG_FILE}
  echo "`/bin/vmstat 1 5`" >> ${LOG_FILE}
}


heartBeatScenario()
{
  echo "PID[$$] PPID[${PPID}] THREAD_ID[${1}] heartBeatScenario" >> ${LOG_FILE}

  #Check if snoop is already running
  SNOOP_UDP_FOUND=0
  if [ -s /tmp/snid_h ]
  then
    echo "/tmp/snid_h File[\n`cat /tmp/snid_h`\n]" >> ${LOG_FILE}
    for i in `cat /tmp/snid_h`
    do
      snoop_ps_grep=`/usr/ucb/ps -auxww|grep -v grep | grep ${i}`
      if [ ! -z "${snoop_ps_grep}" ]
      then
        SNOOP_UDP_FOUND=1
        echo "PID[$$] PPID[${PPID}] THREAD_ID[${1}] heartBeatScenario SNOOP UDP already running" >> ${LOG_FILE}
      fi
    done
  else
    echo "PID[$$] PPID[${PPID}] THREAD_ID[${1}] heartBeatScenario NO PROCESS FOUND snid_h" >> ${LOG_FILE}
  fi
  
  if [ ${SNOOP_UDP_FOUND} -eq 0 ]
  then
    nohup snoop -d ${INTERFACE_NAME} -o /LOGS/INGw/snoop_udp_${$}.cap ip proto 17 &
    echo "${!}" >> /tmp/snid_h
    SNOOP_PIDS_HB="${SNOOP_PIDS_HB} ${!}"
    echo "PID[$$] PPID[${PPID}] THREAD_ID[${1}] heartBeatScenario`/usr/ucb/ps -auxww|grep -i snoop | grep -v grep`" >> ${LOG_FILE}
  fi
  
  echo "`date`" >> ${LOG_FILE}
  echo "`/sbin/ifconfig -a`" >> ${LOG_FILE}
  echo "`/bin/prstat 1 5`" >> ${LOG_FILE}
  echo "`/bin/vmstat 1 5`" >> ${LOG_FILE}
}

invalidScenario()
{
  echo "PID[$$] PPID[${PPID}] THREAD_ID[${1}] invalidScenario" >> ${LOG_FILE}

  echo "`date`" >> ${LOG_FILE}
  echo "`/sbin/ifconfig -a`" >> ${LOG_FILE}
  echo "`/bin/prstat 1 5`" >> ${LOG_FILE}
  echo "`/bin/vmstat 1 5`" >> ${LOG_FILE}
}

killActiveCallScenario()
{
  echo "PID[$$] PPID[${PPID}] THREAD_ID[${1}] killActiveCallScenario" >> ${LOG_FILE}
  echo "`/usr/ucb/ps -auxww|grep -i snoop | grep -v grep`" >> ${LOG_FILE}

  if [ -s /tmp/snid_a ]
  then
    echo "/tmp/snid_a File[\n`cat /tmp/snid_a`\n]" >> ${LOG_FILE}
    for i in `cat /tmp/snid_a`
    do
      echo -e "KILLING...\n`/usr/ucb/ps -auxww|grep ${i} | grep -v grep`" >> ${LOG_FILE}
      kill -9 $i
    done
    >/tmp/snid_a
  else
    echo "PID[$$] PPID[${PPID}] THREAD_ID[${1}] killActiveCallScenario NO PROCESS FOUND snid_a" >> ${LOG_FILE}
  fi
  
}

killHeartBeatScenario()
{
  echo "PID[$$] PPID[${PPID}] THREAD_ID[${1}] killHeartBeatScenario" >> ${LOG_FILE}
  echo "`/usr/ucb/ps -auxww|grep -i snoop | grep -v grep`" >> ${LOG_FILE}
  if [ -s /tmp/snid_h ]
  then
    echo "/tmp/snid_h File[\n`cat /tmp/snid_h`\n]" >> ${LOG_FILE}
    for i in `cat /tmp/snid_h`
    do
      echo -e "KILLING...\n`/usr/ucb/ps -auxww|grep ${i} | grep -v grep`" >> ${LOG_FILE}
      kill -9 $i
    done
    >/tmp/snid_h
  else
    echo "PID[$$] PPID[${PPID}] THREAD_ID[${1}] killHeartBeatScenario NO PROCESS FOUND snid_h" >> ${LOG_FILE}
  fi
}

killInvalidScenario()
{
  echo "PID[$$] PPID[${PPID}] THREAD_ID[${1}] killInvalidScenario" >> ${LOG_FILE}
}

killAllScripts()
{
  echo -e "PID[$$] PPID[${PPID}] THREAD_ID[${1}] killAllScripts \n[`ps -eaf|grep "/bin/bash ./DiagScr.sh" | grep -v grep`]" >> ${LOG_FILE}
  pids1=`ps -eaf|grep "/bin/bash ./DiagScr.sh" | grep -v grep|grep -v $$ | awk '{print $2}'`
  if [ ! -z "$pids1" ]
  then
    echo -e "[1mKill the PIDS[$pids1] (Y/N):[0m\\c"
    read ans
    if [ "$ans" = 'Y' -o "$ans" = 'y' ]
    then
      kill -9 ${pids1}
      pids1=""
      sleep 1
    fi
  fi
}




############################ MAIN #####################################

echo "#####################################" >> ${LOG_FILE}
echo "PID[$$] PPID[${PPID}] THREAD_ID[${3}] START `date`" >> ${LOG_FILE}

if [ $# -lt 2 ]
then
  echo "USAGE: ${0} $*" >> ${LOG_FILE}
  echo "USAGE: ${0} -s|-k|-kall 1|2|3 [ThreadId] [FileName] [LineNum] [Reason]" | tee -a ${LOG_FILE}
  echo -e "PID[$$] PPID[${PPID}] THREAD_ID[${3}] END `date`\n+++++++++++++++++++++++++++++++++++++" >> ${LOG_FILE}
  exit 0
fi

echo "PID[$$] PPID[${PPID}] THREAD_ID[${3}] SCENARIO[${2}] ACTION[${1}] FILE_NAME[${4}] LINE[${5}] REASON[${6}]" >> ${LOG_FILE}

chekForOtherInstanceRunning "${0}" "${1}" "${2}" "${3}"

PLTFRM=`uname`
if [ "${PLTFRM}" = "SunOS" ]
then
  HOST_NAME=`hostname`
  INTERFACE_NAME=`grep ${HOST_NAME} /etc/hostname.* | cut -d':' -f1 |cut -d'.' -f2`
  echo "PID[$$] PPID[${PPID}] THREAD_ID[${3}] INTERFACE_NAME: ${INTERFACE_NAME}" >> ${LOG_FILE}
fi

if [ "${1}" = "-s" ]
then # [
  if [ "${2}" -eq "1" ]
  then # [
    echo "PID[$$] PPID[${PPID}] THREAD_ID[${3}] START ACTIVE CALL SCENARIO" >> ${LOG_FILE}
    activeCallScenario "${3}"
  # ]
  elif [ "${2}" -eq  "2" ]
  then # [
    echo "PID[$$] PPID[${PPID}] THREAD_ID[${3}] START HEART BEAT SCENARIO" >> ${LOG_FILE}
    heartBeatScenario "${3}"
  # ]
  else # [
    echo "PID[$$] PPID[${PPID}] THREAD_ID[${3}] START INVALID SCENARIO [${1}]" >> ${LOG_FILE}
    invalidScenario "${3}"
  # ]
  fi
#]
elif [ "${1}" = "-k" ]
then # [
  if [ "${2}" -eq "1" ]
  then # [
    echo "PID[$$] PPID[${PPID}] THREAD_ID[${3}] END ACTIVE CALL SCENARIO" >> ${LOG_FILE}
    killActiveCallScenario "${3}"
  # ]
  elif [ "${2}" -eq  "2" ]
  then # [
    echo "PID[$$] PPID[${PPID}] THREAD_ID[${3}] END HEART BEAT SCENARIO" >> ${LOG_FILE}
    killHeartBeatScenario "${3}"
  # ]
  else # [
    echo "PID[$$] PPID[${PPID}] THREAD_ID[${3}] END INVALID SCENARIO [${1}]" >> ${LOG_FILE}
    killInvalidScenario "${3}"
  # ]
  fi
#]
elif [ "${1}" = "-kall" ]
then # [
  killAllScripts "${3}"
#]
else #[
  echo "PID[$$] PPID[${PPID}] THREAD_ID[${3}] INVALID ACTION [${1}]" >> ${LOG_FILE}
#]
fi

echo -e "PID[$$] PPID[${PPID}] THREAD_ID[${3}] END `date`\n+++++++++++++++++++++++++++++++++++++" >> ${LOG_FILE}


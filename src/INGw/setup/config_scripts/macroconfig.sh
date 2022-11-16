#!/bin/ksh

# Run the setup.sh script

PWD11=`pwd`
MY_PATH=`dirname $0`
cd $MY_PATH

SUBSYS_DIR=$SUBSYS_INGW

#. $INSTALL_ROOT/$SUBSYS_DIR/$PLATFORM_DIR/scripts/setup.sh
#. $INSTALL_ROOT/$SUBSYS_DIR/$PLATFORM_DIR/scripts/setup.sh
. ../scripts_tmpl/setup.sh

UNAME=`whoami`

if [ $PLATFORM_DIR == "sol28g" ]
then
   ECHO_PREFIX=''
else
   ECHO_PREFIX=' -e '
fi

cd $PWD11

HOST_NAME=`hostname`

IP_ADDR=`cat /etc/hosts | egrep -v  "^#" | egrep $HOST_NAME | awk 'NR == 1 {print }' | awk '{print $1}'`

HOST_ADDR=`echo $ECHO_PREFIX  $IP_ADDR | cut -d. -f4`
HOST_ADDR=${HOST_ADDR:-100}

LOGGING_DIR=$LOG_OUTPUT_DIR
LOG_DIR=`echo $ECHO_PREFIX  $LOGGING_DIR | sed "s/\//\\\\\\\\\\//g"`

echo "LOG $LOG_DIR"


NONE_ANS=NONE
if [ ${NONE_ANS}${INGW_FIP} == ${NONE_ANS} ]
then
   INGW_FIP=$MY_HOST_IP
fi

I_ROOT=`echo $ECHO_PREFIX  $INSTALL_ROOT | sed "s/\//\\\\\\\\\\//g"`

echo "INS_ROOT $I_ROOT"

MY_PATH=`dirname $0`
cd $MY_PATH

# cp all the files from the template paths

cp ../scripts_tmpl/* ../scripts
chmod 777 ../scripts/*

cp ../conf_tmpl/* ../conf
chmod 777 ../conf/*

#Configuring the scripts and conf files

for name in ../scripts/INGw ../scripts/INGwHkp.sh ../scripts/setup.sh ../scripts/logCleaner.sh ../scripts/profile.ingw ../conf/hssenv.sh ../conf/ingw_primary.lcfg ../conf/up.xml 
do
  n_cnt=0
  sed 's/INSTALL_ROOT/'$I_ROOT'/g' $name > p.${n_cnt}
  s_cnt=$n_cnt
  n_cnt=`expr $s_cnt + 1`

  sed 's/PLATFORM_DIR/'$PLATFORM_DIR'/g' p.${s_cnt} > p.${n_cnt}
  s_cnt=$n_cnt
  n_cnt=`expr $s_cnt + 1`

  sed 's/DISP_MACHINE/'$MY_HOST_IP'/g' p.${s_cnt} > p.${n_cnt}
  s_cnt=$n_cnt
  n_cnt=`expr $s_cnt + 1`

  sed 's/LOG_DIR/'$LOG_DIR'/g' p.${s_cnt} > p.${n_cnt}
  s_cnt=$n_cnt
  n_cnt=`expr $s_cnt + 1`

  sed 's/INGW_FIP/'$INGW_FIP'/g' p.${s_cnt} > p.${n_cnt}
  s_cnt=$n_cnt
  n_cnt=`expr $s_cnt + 1`

  sed 's/SUBSYS_INGW/'$SUBSYS_INGW'/g' p.${s_cnt} > p.${n_cnt}
  s_cnt=$n_cnt
  n_cnt=`expr $s_cnt + 1`

  mv p.${s_cnt} $name
  chmod 777 $name
  rm p.*
done

echo "SUCCESS"

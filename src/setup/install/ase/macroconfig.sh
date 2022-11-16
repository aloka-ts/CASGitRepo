#!/bin/ksh

# Script for populating the scripts & configuration files present in scripts & conf directory
# with macro values.


# --------------------------- Functions --------------------------------------------------------

# Functions used for populating the VERIFY file which will be used by the upgrade eninge verification
# script to verify successful inclusion of files present in the VERIFY file.
# Function used to insert a directory entry into the Verify file
function put_dir_entry
{
    # get the count of entries in the directory
    VERIFY_CURR_DIR_COUNT=`ls "../$VERIFY_CURR_DIR" |wc -w |sed 's/[ ]//g' `
    echo "DIR:$VERIFY_CURR_DIR:$VERIFY_CURR_DIR_COUNT" 1>> $MACRO_VERIFY_FILE

}

# Function used to put information (name & size) about all the files in a particular directory).
function put_fileentry_for_currdir
{
    # get the direcotry contents for files
     for i in `ls -l "../$VERIFY_CURR_DIR" |grep "^-" | sed 's/[ ][ ]*/ /g' |cut -f9 -d' ' `
     do
        VERIFY_CURR_FILE=$i
        VERIFY_CURR_FILE_SIZE=`ls -ltr "../$VERIFY_CURR_DIR/$i" | sed 's/[ ][ ]*/ /g' | cut -f5 -d' ' `
        echo "FILE:$VERIFY_CURR_FILE:$VERIFY_CURR_FILE_SIZE" 1>> $MACRO_VERIFY_FILE
     done

}

# --------------------------- End of functions -------------------------------------------------

# ------------ Create the MAcro Config verify file ---------------------------------------------
# This file will be used by the verification script corresponding to this script to check if the
# execution of this script is really successful or not.

USER_NAME=`whoami`
MY_PATH=`dirname $0`
SCRIPTS_PATH=$MY_PATH/../../scripts
CONF_PATH=$MY_PATH/../../conf
TEST_APPS_PATH=$MY_PATH/../../test-apps

USER_NAME=`/usr/bin/id |cut -f2 -d'(' |cut -f1 -d')'`
MACRO_VERIFY_FILE=/tmp/$USER_NAME/ASEmacroconfig.tmp
rm -f $MACRO_VERIFY_FILE
touch $MACRO_VERIFY_FILE
echo "The verify file used by $0 is : $MACRO_VERIFY_FILE "

# replace all "/" with "\\/". This is required so that the at the time
# of replacing the directory path values in all scripts, sed should not get
# confused by the "/" symbol. Double backslsh used so that both shell & sed
# need one "\" each as escape character to be used in front of "/".
I_ROOT=`echo $ECHO_PREFIX  $INSTALL_ROOT | sed "s/\//\\\\\\\\\\//g"`
ORACLE_HOME=`echo $ECHO_PREFIX  $ORACLEHOME | sed "s/\//\\\\\\\\\\//g"`


# ----------- Processing of the script files ------------------------
for i in $MY_PATH/osconfig.sh $SCRIPTS_PATH/ase $SCRIPTS_PATH/ase_no_ems $SCRIPTS_PATH/dumpstack.sh $SCRIPTS_PATH/cleanupSASLog.sh $SCRIPTS_PATH/profile.cas
do
	echo "macroconfig: Processing the file :  $i "
	n_cnt=0
	sed 's/INSTALL_ROOT/'$I_ROOT'/g' $i > $i.tmp.$n_cnt

	echo "ORACLEHOME is : $ORACLE_HOME"
	s_cnt=$n_cnt
	n_cnt=`expr $n_cnt + 1`
	sed 's/ORACLEHOME/'$ORACLE_HOME'/g' $i.tmp.$s_cnt > $i.tmp.$n_cnt

	mv $i.tmp.$n_cnt $i
	\rm -f $i.tmp.*
	chmod 777 $i
done

# populate the verify file with the details of the current directory
VERIFY_CURR_DIR="scripts"
put_dir_entry
put_fileentry_for_currdir
# end of putting details of current directory into the verify file

PEER_IP=""
if [[ $MY_ROLE == "Active" ]] then
	PEER_IP=$MACHINE1_IP
else
	PEER_IP=$MACHINE2_IP
fi

DB_TYPE="NON_DATA_GUARD"
if [ $DB_FT_REQD == "0" ]
then
   DB2_SID=_NOT_USED_
   DB2_HOST_IP=$DB1_HOST_IP
fi
if [ $DB_FT_REQD == "2" ]
then
   DB_TYPE="DATA_GUARD"
fi

# ----------- Processing of the conf files ------------------------
for i in $CONF_PATH/ase.properties $CONF_PATH/measurement-file-config.xml $CONF_PATH/dblib.properties
do
	echo "macroconfig: Processing the file :  $i "
	n_cnt=0
	sed 's/INSTALL_ROOT/'$I_ROOT'/g' $i > p.$n_cnt

	echo "The Subsys version is : $SUBSYS_VERSION"
	s_cnt=$n_cnt
	n_cnt=`expr $n_cnt + 1`
	sed 's/SUBSYS_VERSION/'$SUBSYS_VERSION'/g' p.$s_cnt > p.$n_cnt

	echo "The SM Port is : $SM_GENERIC_PORT"
	s_cnt=$n_cnt
	n_cnt=`expr $n_cnt + 1`
	sed 's/SM_GENERIC_PORT/'$SM_GENERIC_PORT'/g' p.$s_cnt > p.$n_cnt

	echo "ACTIVE_DATA_GUARD Value is : $ACTIVE_DATA_GUARD_SUPPORT"
	s_cnt=$n_cnt
	n_cnt=`expr $n_cnt + 1`
	sed 's/ACTIVE_DATA_GUARD_SUPPORT/'$ACTIVE_DATA_GUARD_SUPPORT'/g' p.$s_cnt > p.$n_cnt

	echo "DB2_SID Value is : $DB2_SID"
	s_cnt=$n_cnt
	n_cnt=`expr $n_cnt + 1`
	sed 's/DB2_SID/'$DB2_SID'/g' p.$s_cnt > p.$n_cnt

	echo "DB2_HOST_IP Value is : $DB2_HOST_IP"
	s_cnt=$n_cnt
	n_cnt=`expr $n_cnt + 1`
	sed 's/DB2_HOST_IP/'$DB2_HOST_IP'/g' p.$s_cnt > p.$n_cnt

	echo "DB_TYPE Value is : $DB_TYPE"
	s_cnt=$n_cnt
	n_cnt=`expr $n_cnt + 1`
	sed 's/DB_TYPE/'$DB_TYPE'/g' p.$s_cnt > p.$n_cnt

	mv p.$n_cnt $i
	\rm -f p.*

done

#Updating datasources.xml file

D_USER=`echo $SAS_DB_CONN_STRING | awk -F\/ '{print $1}'`
echo "DB User : $D_USER"

D_PASS_SID=`echo $SAS_DB_CONN_STRING | awk -F\/ '{print $2}'`
D_PASS=`echo $D_PASS_SID | awk -F@ '{print $1}'`
echo "DB Password : $D_PASS"

D_SID=`echo $D_PASS_SID | awk -F@ '{print $2}'`
echo "DB SID : $D_SID"

DATA_SOURCE_FILE=$CONF_PATH/datasources.xml

echo "macroconfig: Processing the file :  $DATA_SOURCE_FILE"
n_cnt=0
sed 's/DB_USER/'$D_USER'/g' $DATA_SOURCE_FILE > p.$n_cnt

s_cnt=$n_cnt
n_cnt=`expr $n_cnt + 1`
sed 's/DB_PASSWORD/'$D_PASS'/g' p.$s_cnt > p.$n_cnt

s_cnt=$n_cnt
n_cnt=`expr $n_cnt + 1`
sed 's/DB_SID/'$D_SID'/g' p.$s_cnt > p.$n_cnt

s_cnt=$n_cnt
n_cnt=`expr $n_cnt + 1`
sed 's/DB_IP/'$SAS_DB_IP'/g' p.$s_cnt > p.$n_cnt

mv p.$n_cnt $DATA_SOURCE_FILE
\rm -f p.*

# Copy the profile.cas to the $HOME dir
cp $SCRIPTS_PATH/profile.cas ~/profile.cas.${USER_NAME}

# populate the verify file with the details of the current directory
VERIFY_CURR_DIR="conf"
put_dir_entry
put_fileentry_for_currdir
# end of putting details of current directory into the verify file.


# ----------- Processing of the test-app files ------------------------
for i in $TEST_APPS_PATH/*/WEB-INF/sip.xml
do
	echo "macroconfig: Processing the file :  $i "
	n_cnt=0
	sed 's/INSTALL_ROOT/'$I_ROOT'/g' $i > $i.tmp.$n_cnt
	mv $i.tmp.$n_cnt $i
	\rm -f $i.tmp.*
	chmod 777 $i
done


echo "SUCCESS"

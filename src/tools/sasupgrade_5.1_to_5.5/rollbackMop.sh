#!/bin/ksh

##########################################################################################
#   Objective       : SAS rollback propcess from SAS5.1.1 to SAS 5.5
#   scripts 		: SAS rollback process from SAS 5.5 to SAS 5.1.1
#   Author          : Prashant Kumar
#   Last Modified   : 10.03.2007
##########################################################################################


# Added to support multiple OS types
#    PLATFORM=`uname`
#    if [ ${PLATFORM} == "Linux" ]
#    then
#        ORACLE_HOME=/usr/local/OraClnt9.2.0.4
#    else
#        ORACLE_HOME=/usr/local/OracleClient8173
#    fi
#export ORACLE_HOME
#export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$ORACLE_HOME/lib:$ORACLE_HOME/rdbms/lib:

export JAVA_HOME=/usr/java
export PATH=$JAVA_HOME/bin.$PATH
export PATH=/bin:/usr/bin:/sbin:/usr/sbin:.:$PATH

bold=`tput bold`
offbold=`tput rmso`

export MOP_DIR=`pwd`


##############################################################################
####################### functions starts here  ###############################
##############################################################################


###################################################################
## function to create a log file where logs of upgradation 
## will be written
## function to create a log file where logs of upgradation 
## will be written. This function also preserves logs of earlier
## upgradation, if any.
###################################################################

create_log_file () {

USER_NAME=`/usr/bin/id |cut -f2 -d'(' |cut -f1 -d')'`
mkdir -p /tmp/$USER_NAME/

FILE=/tmp/$USER_NAME/sas_rollback.log
DIR=/tmp/$USER_NAME

if [ ! -f $FILE ];then
    #echo "file does not exist "
    touch $FILE

else
    count=`ls $DIR |grep rollback | wc -l |sed 's/[ ]//g' `

    while [ $count -gt 1 ]
    do
        less_count=`expr $count - 1`
        mv $FILE.$less_count $FILE.$count
        count=`expr $count - 1`
    done
    mv $FILE $FILE.$count
    touch $FILE
fi
LOG_FILE=$FILE

}


prompt_for_sid() {

echo "please provide $1 db connection string (user/passwd@dbsid)"
read DB_STRING
check_string
return_value=$DB_STRING

}


check_string() {
echo "$DB_STRING" >> $LOG_FILE
SID=`echo $DB_STRING | awk -F@ '{print $2}'`
USERANDPASS=`echo $DB_STRING | awk -F@ '{print $1}'`

USER=`echo $USERANDPASS | awk -F/ '{print $1}'`
PASS=`echo $USERANDPASS | awk -F/ '{print $2}'`

if [ "$SID" ]
then
echo "@ found" >> $LOG_FILE
else
echo "@ not found"
echo "not in correct format.."
STR="again"
prompt_for_sid $STR

#prompt_for_sid
#exit
fi

if [ "$PASS" ]
then
echo "/ found " >> $LOG_FILE
else
echo "/ not found"
echo "not in correct format.."
STR="again"
prompt_for_sid $STR

fi
}

get_user_passwd() {
echo "${bold}Please provide SAS installation user password for this machine ${offbold} "
read user_pass
USER_PWD=$user_pass

}

check_rexec() {
## BPInd 18325
#echo "inside check_rexec() " >> $LOG_FILE

if [ $? == 0 ]
then
    echo "rexec was successful" >> $LOG_FILE
else
    echo "${bold}SAS installation user password was not correct....."
    echo "would you like to continue...... (Y) "
    read iput
    if [ $iput = 'Y' -o $iput = 'y' ]
    then
		get_user_passwd
        is_sas_id $1
    else
        #echo "Applying rollback on the setup..... ${offbold} "
        #rollbackMop.sh $INSTALLROOT
		echo "Exitting from rollback" >> $LOG_FILE
		echo "Exitting from rollback.....${offbold}" 
        exit
    fi
fi
echo "leaving check_rexec() " >> $LOG_FILE
}

get_ems_host() {
echo "inside get_ems_host()" >> $LOG_FILE
ems_ip=`cat $INSTALLROOT/HConfigFile.dat | grep Primary_BMgr_IP | awk -F':' '{print $2}'`
echo " EMS IP IS : $ems_ip " >> $LOG_FILE
}

isdigit ()    # Tests whether *entire string* is numerical.
{             # In other words, tests for integer variable.
	SUCCESS=0
	FAILURE=1
	[ $# -eq 1 ] || return $FAILURE

	case $1 in
		*[!0-9]*|"") return $FAILURE;;
			*) return $SUCCESS;;
	esac
}

digit_check ()  # Front-end to isdigit ().
{
	if isdigit "$@"
	then
		echo "\"$*\" contains only digits [0 - 9]." >> $LOG_FILE
		digit_val=0
	else
		echo "\"$*\" has at least one non-digit character." >> $LOG_FILE
		digit_val=1
	fi
}


is_sas_id() {
> sqlCheck.log
echo "checking for system id" >> $LOG_FILE
#get_ems_host
#get_user_passwd
TestRexec -host $ems_ip -userId $USER_NAME  -passwd $USER_PWD -cmdfile sqlcheck.sh $primary_db $1 >> sqlCheck.log
check_rexec $1
#check_db_error $primary_db
}

get_subsystem_id() {

get_ems_host
get_user_passwd

USER_NAME=`/usr/bin/id |cut -f2 -d'(' |cut -f1 -d')'`

cd /var/Baypackets/SAS/SystemMonitor/$USER_NAME/conf/subsystems/

for i in `ls -l "." |grep "^-" | sed 's/[ ][ ]*/ /g' |cut -f9 -d' ' `
do
cd $MOP_DIR

digit_check $i
is_digit=$digit_val
if [ $is_digit != "0" ]
then
	echo "subsytem id is not numeric" $i >> $LOG_FILE
	continue;
fi


is_sas_id $i

result=`grep 'no rows selected' sqlCheck.log`
if [ ! "$result" ]
then
SUBSYSTEM_ID=$i
fi
done
return_id=$SUBSYSTEM_ID
}

get_sid() {
sid=`echo $1 | awk -F@ '{print $2}'`
}

rollback_db() {
get_subsystem_id
SUBSYS_ID=$return_id
for db_name in $primary_db $mirror_db
do
 if [ -z $db_name ]
  then
 continue
 else
	get_sid $db_name
	dbsid=$sid
	if [ -f dbupdate.$dbsid ] 
	then
		echo "Rollbacking signalling IP in DB " >> $LOG_FILE
		TestRexec -host $ems_ip -userId $USER_NAME -passwd $USER_PWD -cmdfile sqlSignalIPRollback.sh $db_name >> $LOG_FILE

		echo "Rollbcaking version in DB. " >> $LOG_FILE
		TestRexec -host $ems_ip -userId $USER_NAME -passwd $USER_PWD -cmdfile sqlIdRollback.sh $db_name $SUBSYS_ID >> $LOG_FILE

		echo "Rollbacking various OIDs value in DB " >> $LOG_FILE
		TestRexec -host $ems_ip -userId $USER_NAME -passwd $USER_PWD -cmdfile sqlbackup.sh $db_name $INSTALLROOT $SUBSYS_ID >> $LOG_FILE
		check_db_error $db_name
	fi
fi
done

}

check_db_error() { 

result=`grep SP2- $LOG_FILE`
if [ "$result" ]
then 
echo "unable to logon to db for db string $1. db string not correct"
get_sid $1
dbsid=$sid
touch dbupdate.$dbsid
exit
fi

result=`grep ORA $LOG_FILE`
if [ "$result" ]
then 
	resu=`grep ORA-01403 $LOG_FILE`
	if [ ! "$resu" ]
	then
		echo "Error in db rollback for db string $1."
		get_sid $1
		dbsid=$sid
		touch dbupdate.$dbsid
		exit
	fi
fi
echo "db rollback was successful for $1" 
rm dbupdate.$dbsid
}

check_root_pass() {
./TestRexec -host 0 -userId root -passwd $root_pwd -cmd echo "testing root password" >> $LOG_FILE

if [ $? == 0 ]
then
	echo "Root password is correct" >> $LOG_FILE
else
	echo "${bold}root password was not correct....."
	echo "would you like to continue...... (Y/N) "
	read iput
	if [ $iput = 'Y' -o $iput = 'y' ]
	then
		prompt_root
	else
		echo "Exitting from rollback" >> $LOG_FILE
		echo "Exitting from rollback.....${offbold}"
		exit
	fi
fi
}

prompt_root(){
echo "${bold}Please provide root password for this machine ${offbold} "
read root_pwd
check_root_pass
}

rollback_sysmon() {
	prompt_root

	cd systemmonitor
	echo "rollbacking system monitor " >> $LOG_FILE
	./sysmonrollback_jumbo.sh $root_pwd  >> $LOG_FILE
	#if [ $? != 0 ]
	#then
	#	echo "Problem in system monitor rollbak"
	#fi
	cd $MOP_DIR
	#rm IPAD sqlCheck.log sqlCheckIP.log tmp_sig
}

##############################################################################
####################### functions ends here  #################################
##############################################################################

echo "${bold}"
echo "#######################################################"
echo "###                                                 ###"
echo "###               SAS rollback Applier              ###"
echo "###                                                 ###"
echo "#######################################################"
echo "${offbold}"

if [ ! "$1" ]
then
	echo "enter install root"
	read INSTALLROOT
else
	INSTALLROOT=$1
fi

if [ -d $INSTALLROOT ] ; then 
	echo "install root exists"
else 
	echo "install root does not exist"
	exit
fi

create_log_file

USER_NAME=`/usr/bin/id |cut -f2 -d'(' |cut -f1 -d')'`


if [ -d $INSTALLROOT/../SAS_5.1/ASE5.1.1 ] ; then

    echo "SAS5.1. data was moved proceeding with the rollback" >> $LOG_FILE
else 
	echo "jars were not replaced"
	if [ -d $INSTALLROOT/../SAS_5.5 ] ; then
		rm -rf $INSTALLROOT/../SAS_5.5
	fi

	echo "SAS5.1. data was not moved. checking for db changes" >> $LOG_FILE 
	if [ -f dbupdate.* ] 
	then 
		echo "rollbacking db changes"

		STR="Primary"
		prompt_for_sid $STR
		primary_db=$return_value

		echo "Whether this setup is DB FT <Y/N>"
		read iput
		if [ $iput = 'Y' -o $iput = 'y' ]
		then
    		#echo Enter the Secondary DB SID :
    		STR="Secondary"
    		prompt_for_sid $STR
    		mirror_db=$return_value
		fi
		rollback_db
		if [ -f dbupdate.* ] 
		then 
			for i in `ls | grep dbupdate | cut -d'.' -f2`
			do
				echo "DB roll back was not successful for $i"
			done
				echo "Roll back again with correct DB string"
				echo "Exiting"
			exit
		fi

		#rm -rf $INSTALLROOT/../SAS_5.5
		rm mop_success
		rollback_sysmon
		echo "ROLLBACK APPLIED SUCCESSFULLY...."
		exit
	else
		echo "no db rollback required" >> $LOG_FILE
		echo "no db rollback required"
		if [ -f mop_success ]
		then
			rollback_sysmon
			rm mop_success
		fi
		echo "ROLLBACK APPLIED SUCCESSFULLY...."
		exit
	fi
fi

if [ -d $INSTALLROOT/../SAS_5.5 ] ; then
	rm -rf $INSTALLROOT/../SAS_5.5
fi

mkdir -p $INSTALLROOT/../SAS_5.5
mv $INSTALLROOT/* $INSTALLROOT/../SAS_5.5
mv $INSTALLROOT/../SAS_5.1/* $INSTALLROOT/
rm -rf $INSTALLROOT/../SAS_5.1
rm -rf $INSTALLROOT/../SAS_5.5


if [ -f dbupdate.* ] 
then
	STR="Primary"
	prompt_for_sid $STR
	primary_db=$return_value

	echo "Whether this setup is DB FT <Y/N>"
	read iput
	if [ $iput = 'Y' -o $iput = 'y' ]
	then
   		 #echo Enter the Secondary DB SID :
    	STR="Secondary"
    	prompt_for_sid $STR
    	mirror_db=$return_value
	fi
	rollback_db

	if [ -f dbupdate.* ] 
	then 
		for i in `ls | grep dbupdate | cut -d'.' -f2`
		do
			echo "DB roll back was not successful for $i"
		done
		echo "Roll back again with correct DB string"
		echo "Exiting"
		exit	
	fi
else
	echo "DB login wasn't successful during upgradation"
	#exit
fi

### BPInd18325
if test -f $INSTALLROOT/profile.sas.$USER_NAME
then
echo "moving profile file" >> $LOG_FILE
mv $INSTALLROOT/profile.sas.$USER_NAME ~/
fi

#####


if [ -f mop_success ]
then
	rollback_sysmon
	rm mop_success
fi

echo "ROLLBACK APPLIED SUCCESSFULLY"

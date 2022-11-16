#!/bin/ksh

##########################################################################################
#	Objective		: SAS upgradation propcess from SAS5.1.1 to SAS 5.5
#	Script			: upgrades the installed SAS 5.1.1 to SAS 5.5				 
#	Author 			: Prashant Kumar
#	Last Modified	: 10.03.2007
##########################################################################################


# Added to support multiple OS types
#    PLATFORM=`uname`
#    if [ ${PLATFORM} == "Linux" ]
#    then
#		ORACLEHOME=/usr/local/OraClnt9.2.0.4
#    else
#		ORACLEHOME=/usr/local/OracleClient8173/
#    fi
#export ORACLE_HOME=$ORACLEHOME
#export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$ORACLE_HOME/lib:$ORACLE_HOME/rdbms/lib:

export JAVA_HOME=/usr/java
export PATH=$JAVA_HOME/bin:/usr/ucb:.$PATH
export PATH=/bin:/usr/bin:/sbin:/usr/sbin:.:$PATH

bold=`tput bold`
offbold=`tput rmso`


if [ -f mop_success ] 
then 
echo "${bold}MOP already applied successfully."
echo "Exiting...........${offbold}\n"
exit
fi

chmod 777 *
export MOP_DIR=`pwd`


##############################################################################
####################### functions starts here  ###############################
##############################################################################

##############################################################################
## function to create a log file where logs of upgradation 
## will be written
## function to create a log file where logs of upgradation 
## will be written. This function also preserves logs of earlier
## upgradation, if any.
##############################################################################

create_log_file () {

USER_NAME=`/usr/bin/id |cut -f2 -d'(' |cut -f1 -d')'`
mkdir -p /tmp/$USER_NAME/

FILE=/tmp/$USER_NAME/sas_upgrade.log
DIR=/tmp/$USER_NAME

if [ ! -f $FILE ];then
    #echo "file does not exist "
    touch $FILE

else
    count=`ls $DIR |grep upgrade | wc -l |sed 's/[ ]//g' `
	
	while [ $count -gt 1 ]
    do
        less_count=`expr $count - 1`
	#	echo " LESS COUNT IS :: $less_count"
		### PK 
		# touch $FILE.$count
        mv $FILE.$less_count $FILE.$count
        count=`expr $count - 1`
    done
	#touch $FILE.$count
    mv $FILE $FILE.$count
    touch $FILE
fi
LOG_FILE=$FILE

}

manage_properties() {
echo "old property file $1 " >> $LOG_FILE
echo " new prop file $2" >> $LOG_FILE

cat $1 | grep appName | sed 's/appName/name/' > $2
cat $1 | grep version >> $2
cat $1 | grep priority >> $2
#cat $1 | grep archive | sed 's/sol28g//' >> $2

NAME=`cat $1 | grep appName | awk -F= '{print $2}'`
VER=`cat $1 | grep version | awk -F= '{print $2}'`
US=_      
UO=_1    
OLDSAR=$NAME$US$VER.sar
NEWSAR=$NAME$US$VER$UO.sar
             
tmp=`cat $1 | grep archive | sed 's/sol28g\///'`
final=`echo $tmp | sed 's/'$OLDSAR'/'$NEWSAR/''`
echo $final >> $2
    
CTX=`cat $1 | grep contextPath | awk -F= '{print $1}'`
echo $CTX= >> $2

CTX=`cat $1 | grep contextPath | awk -F= '{print $1}'`
echo $CTX= >> $2


SID=`cat $1 | grep state | awk -F= '{print $2}'`


if [ $SID = '1' ]
then
NAME=2
fi

if [ $SID = '2' ]
then
NAME=4
fi

if [ $SID = '3' ]
then
NAME=5
fi

if [ $SID = '4' ]
then
NAME=1
fi

if [ $SID = '5' ]
then
NAME=5
fi

if [ $SID = '6' ] 
then      
echo $SID
NAME=6
fi       
        
cat $1 | grep state | sed 's/'$SID'/'$NAME'/' >> $2
cat $1 | grep upgraded >> $2
cat $1 | grep deployedBy >> $2
}


copy_sar() {
   
for i in `ls -l $1 |grep sar | grep "^-" | sed 's/[ ][ ]*/ /g' |cut -f9 -d' ' `
do
    SID=`echo $i | sed 's/.sar/_1.sar/'`
    cp $1/$i $2/$SID
done
}

copy_prop() {

for i in `ls -l $1 |grep properties | grep "^-" | sed 's/[ ][ ]*/ /g' |cut -f9 -d' ' `
do
    SID=`echo $i | sed 's/.properties/_1.properties/'`
    touch $2/$SID
    manage_properties $1/$i $2/$SID
done
}


prompt_for_sid() {

echo "${bold}please provide $1 db service string (user/passwd@dbsid) ${offbold}"
read DB_STRING
check_string
return_value=$DB_STRING

}

check_string() {

SID=`echo $DB_STRING | awk -F@ '{print $2}'`
USERANDPASS=`echo $DB_STRING | awk -F@ '{print $1}'`

USER=`echo $USERANDPASS | awk -F/ '{print $1}'`
PASS=`echo $USERANDPASS | awk -F/ '{print $2}'`

if [ "$SID" ]
then
echo "@ found" >> $LOG_FILE
else
echo "@ not found"
echo "${bold}not in correct format....${offbold}"
STR="again"
prompt_for_sid $STR
fi

if [ "$PASS" ]
then
echo "/ found " >> $LOG_FILE
else
echo "/ not found"
echo "${bold}not in correct format....${offbold}"
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
## BPInd18325
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
        echo "Applying rollback on the setup..... ${offbold} "
        rollbackMop.sh $INSTALLROOT
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
#./sqlcheck.sh $primary_db $1 >> $LOG_FILE
#get_ems_host
#get_user_passwd
echo "propcessing to rexec " >> $LOG_FILE
TestRexec -host $ems_ip -userId $USER_NAME  -passwd $USER_PWD -cmdfile sqlcheck.sh $primary_db $1 >> sqlCheck.log
check_rexec $1
check_logs $primary_db
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
echo "SAS subsystem id for this machine is " >> $LOG_FILE
echo $return_id >> $LOG_FILE
}

prompt_root(){
echo "${bold}Please provide root password for this machine ${offbold} "
read ROOT_PWD
create_log_dir
own_log_dir
}


create_log_dir() {

./TestRexec -host 0 -userId root -passwd $ROOT_PWD -cmd mkdir -p /LOGS/SAS >> $LOG_FILE
if [ $? == 0 ]
then
	echo "Successfully created /LOGS/SAS/ directory" >> $LOG_FILE
else
	echo "${bold}root password was not correct....."
	echo "would you like to continue...... (Y) "
	read iput
	if [ $iput = 'Y' -o $iput = 'y' ]
	#read ip
	#if [ $ip = 'Y' -o $ip = 'y' ]
	then
		prompt_root
	else
		echo "Applying rollback on the setup..... ${offbold} "
		rollbackMop.sh $INSTALLROOT
		exit
	fi
fi
}

own_log_dir() {

./TestRexec -host 0 -userId root -passwd $ROOT_PWD -cmd chown $USER_NAME /LOGS/SAS >> $LOG_FILE
if [ $? == 0 ]
then
echo "Successfully owned /LOGS/SAS/ directory" >> $LOG_FILE
else
echo "${bold}root password was not correct....."
echo "would you like to continue...... (Y)"
read ip
if [ $ip = 'Y' -o $ip = 'y' ]
then
prompt_root
else
echo "Applying rollback on the setup..... ${offbold} "
rollbackMop.sh $INSTALLROOT
exit
fi
fi
}


ask_db_sid() {

STR="Primary"
prompt_for_sid $STR
primary_db=$return_value

#mirror_db=
echo "${bold}Whether this setup is DB FT <Y/N>"
read iput
if [ $iput = 'Y' -o $iput = 'y' ]
then
    #echo Enter the Secondary DB SID :
    STR="Secondary"
    prompt_for_sid $STR
    mirror_db=$return_value
	check_for_equality
fi
}

get_peer_ip() {
echo "inside get_peer_ip() " >> $LOG_FILE

> tmp_sig
TestRexec -host $ems_ip -userId $USER_NAME  -passwd $USER_PWD -cmdfile sqlPeerIP.sh $primary_db $SUBSYS_ID >> tmp_sig

peer_ip=`cat tmp_sig | grep % |  cut -f2 -d" "`
#peer_ip=`cat tmp_sig | cut -f2 -d"%"`

echo "PEER IP IS " $peer_ip >> $LOG_FILE
echo "tmp_sig for peer  is " >> $LOG_FILE
cat tmp_sig >> $LOG_FILE

echo "leaving get_peer_ip() " >> $LOG_FILE

}


prompt_for_signalIP() {
	echo "${bold}Enter local signalling IP for this machine${offbold}"
	read LOCAL_SIG_IP
	check_signalIP
}

check_signalIP() {
	if [ -z $LOCAL_SIG_IP ] 
	then 
		echo "Local signalling IP value can't be null"
		prompt_for_signalIP
	else
		echo "Signalling IP is  $LOCAL_SIG_IP " >> $LOG_FILE
	fi	
}


get_signaling_ip() {

echo "inside get signalling IP " >> $LOG_FILE

prompt_for_signalIP

#./signalIP.sh $primary_db $SUBSYS_ID $prop_file $old_prop_file $ems_ip $USER_NAME $USER_PWD >> tmp_sig
#sig_ip=`cat tmp_sig | grep % |  cut -f2 -d" "`
#fip=`cat tmp_sig | grep FIP |  cut -f2 -d":"`
#export fip
#echo "fip receiverd is :::  $fip"
#sig_ip=`cat tmp_sig | cut -f2 -d"%"`

#echo "tmp_sig for local is " >> $LOG_FILE
#cat tmp_sig >> $LOG_FILE

#echo "" >> $prop_file
#echo "#### Subsystem (SELF) SIP SIGNALLING IP" >> $prop_file
#echo "sas.signal.ip=$sig_ip" >> $prop_file

#get_peer_ip

#> tmp_sig

#TestRexec -host $peer_ip -userId $USER_NAME -passwd $USER_PWD -cmdfile signalIP.sh $primary_db $SUBSYS_ID $prop_file $old_prop_file $ems_ip $USER_NAME $USER_PWD $fip >> tmp_sig

#peer_sig_ip=`cat tmp_sig | grep % |  cut -f2 -d" "`
#peer_sig_ip=`cat tmp_sig | cut -f2 -d"%"`

#echo "tmp_sig for remote  is " >> $LOG_FILE
#cat tmp_sig >> $LOG_FILE

#echo "" >> $prop_file
#echo "#### Subsystem (PEER) SIP SIGNALLING IP" >> $prop_file
#echo "peer.signal.ip=$peer_sig_ip" >> $prop_file

echo "Leaving get signalling IP " >> $LOG_FILE
}

check_for_equality() {
	if [ $primary_db = $mirror_db ]
	then
		echo "both the DB connection strings are same....error "
		ask_db_sid
	fi
}

update_db() { 

echo "Inside update_db()" >> $LOG_FILE
for db_name in $primary_db $mirror_db
do
 if [ -z $db_name ]
  then
 continue
 else
	echo "Upgrading various OIDs in DB " >> $LOG_FILE
	TestRexec -host $ems_ip -userId $USER_NAME -passwd $USER_PWD -cmdfile ./sql.sh  $INSTALL_ROOT $db_name $SUBSYS_ID >> $LOG_FILE
	#check_db_error $db_name

 	echo "upgrading signalling IP in DB" >> $LOG_FILE
 	TestRexec -host $ems_ip -userId $USER_NAME -passwd $USER_PWD -cmdfile ./sqlSignalIP.sh $db_name $SUBSYS_ID $LOCAL_SIG_IP $peer_ip >> $LOG_FILE
	#check_db_error $db_name

	echo "Upgrading vesion in DB " >> $LOG_FILE
	TestRexec -host $ems_ip -userId $USER_NAME -passwd $USER_PWD -cmdfile ./sqlId.sh $db_name $SUBSYS_ID >> $LOG_FILE
	check_db_error $db_name

fi
done

}

check_logs() {
result=`grep SP2- $LOG_FILE`
if [ "$result" ]
then 
echo "${bold}Unable to logon to db with id $1."
echo "applying rollback on the setup to revert back the changes.  ${offbold}"
./rollbackMop.sh $INSTALLROOT
echo "${bold}Exiting................${offbold}"
exit
fi

result=`grep  ORA $LOG_FILE`
if [ "$result" ]
then 
	resu=`grep ORA-01403 $LOG_FILE`
	if [ ! "$resu" ]
	then	
		echo "${bold}Error in db upgradation for id $1."
		sid=`echo $1 | awk -F@ '{print $2}'`
#PK
		touch dbupdate.$sid
		echo "applying rollback on the setup to revert back the changes/ ${offbold}"
		./rollbackMop.sh $INSTALLROOT
		echo "${bold}Exiting................${offbold}"
		exit
	fi
fi
}

check_db_error() {
check_logs $1
echo "${bold}db upgradation successful for $1 ${offbold}"
sid=`echo $1 | awk -F@ '{print $2}'`
touch dbupdate.$sid
}

create_link() {
./TestRexec -host 0 -userId root -passwd $ROOT_PWD -cmdfile createLink.sh >> $LOG_FILE
if [ $? == 0 ]
then
    echo "Successfully created sysmon and HKP link" >> $LOG_FILE
else
	echo "problem in creating sysmon and HKP link" >> $LOG_FILE
fi

}

##############################################################################
####################### functions ends here  #################################
##############################################################################

echo "${bold}"
echo "#######################################################"
echo "###                                                 ###"
echo "###               SAS upgradation Applier           ###"
echo "###                                                 ###"
echo "#######################################################"
echo "${offbold}"

echo "${bold}please enter install root ${offbold}"
read INSTALLROOT

if [ -d $INSTALLROOT ] 
then 
echo "${bold}install root $INSTALLROOT exists ${offbold}"
else 
echo "${bold}install root $INSTALLROOT does not exist ${offbold}"
exit
fi

export INSTALL_ROOT=$INSTALLROOT

USER_NAME=`/usr/bin/id |cut -f2 -d'(' |cut -f1 -d')'`


#echo "${bold}please enter SAS build path (including SAS build name ) ${offbold} "
#read SASBUILD

#if [ -f $SASBUILD ]
#then 
#	result=`echo $SASBUILD | grep SOL | grep .tar.gz`
#	if [ ! "$result" ]
#	then
#		echo "${bold}Build location path does not contain SAS build "
#		echo "Exiting .........${offbold}"
#		exit
#	fi
#	echo "${bold}SAS Build location is correct "
#else
#echo "${bold}SAS Build location does not exist"
#echo "Exiting............ ${offbold}"
#exit
#fi

#export SAS_BUILD=$SASBUILD

ASE_HOME=$INSTALLROOT/ASESubsystem
export ASE_HOME

create_log_file	


echo "${bold}Extracting Build package.......... ${offbold}"
sasbuild=`ls | grep SOL | grep gz`
gunzip -c $sasbuild | tar xf -

#if [ -f *.gz ]
#then
##echo "unzip build"
#echo "unzip build" >> $LOG_FILE
#gunzip -f *.gz
#fi

##echo "untar build"
##echo "untar build" >> $LOG_FILE
#tar xf *.tar

##gunzip -f SOL*
##tar xf SOL*

if [ -d $INSTALLROOT/../SAS_5.1/ASE5.1.1 ] ; then

	echo "data already backed up so not doing any backup" >> $LOG_FILE

else 
           
       if [ -d $INSTALLROOT/../SAS_5.1  ] ; then
		echo "backup directory exists" >> $LOG_FILE
       else
		echo "creating backup directory" >> $LOG_FILE
		mkdir $INSTALLROOT/../SAS_5.1
       fi
echo "${bold}moving existing sas data to backup location ${offbold}"
mv  $INSTALLROOT/* $INSTALLROOT/../SAS_5.1 
fi

if [ -d $INSTALLROOT/ASE5.5 ]
then 
rm -rf $INSTALLROOT/ASE5.5
fi

echo "${bold}moving new SAS jars to install root ${offbold}"
mv SipServlet5.5/ASE5.5 $INSTALLROOT


#create SAS link 
echo "creating link" >> $LOG_FILE
ln -s $INSTALLROOT/ASE5.5 $INSTALLROOT/ASESubsystem

chmod +x $INSTALLROOT/ASESubsystem/Common/thirdParty/TAO/JacOrb/JacORB/bin/jaco

if [ ! -f $INSTALLROOT/../SAS_5.1/HConfigFile.dat ]
then
	echo "${bold}" $INSTALLROOT"/../SAS_5.1/HConfigFile.dat is not present ${offbold}"
	./rollbackMop.sh $INSTALLROOT
	echo "${bold}Exiting...........${offbold} "
	exit
fi

cp $INSTALLROOT/../SAS_5.1/HConfigFile.dat	$INSTALLROOT/ 
if test -f $INSTALLROOT/HConfigFile.dat
then
sed "s/12100/12000/" $INSTALLROOT/HConfigFile.dat > tmpHConfigFile.dat
mv tmpHConfigFile.dat $INSTALLROOT/HConfigFile.dat
fi

### copying applications from old version to new version
echo "copying app" >> $LOG_FILE
mkdir -p $INSTALLROOT/ASESubsystem/apps/archives
mkdir -p $INSTALLROOT/ASESubsystem/apps/db

copy_sar $INSTALLROOT/../SAS_5.1/ASE5.1.1/sol28g/apps/archives $INSTALLROOT/ASESubsystem/apps/archives
copy_prop $INSTALLROOT/../SAS_5.1/ASE5.1.1/sol28g/apps/db $INSTALLROOT/ASESubsystem/apps/db 

######### java program to copy properties from old to new SAS ase.properties file.

if [ ! -f $INSTALLROOT/../SAS_5.1/ASE5.1.1/sol28g/conf/ase.properties ]
then
echo "${bold}ase.properties for older SAS not found ${offbold}"
./rollbackMop.sh $INSTALLROOT
exit
fi


if [ ! -f $INSTALLROOT/ASESubsystem/conf/ase.properties ]
then
echo "${bold}ase.properties for new SAS not found ${offbold} "
./rollbackMop.sh $INSTALLROOT
exit
fi


echo "runing java program " >> $LOG_FILE
echo "<=============== JAVA PROPGRAM OUTPUT ==============>"  >> $LOG_FILE
java UpgradeProperties $INSTALLROOT/../SAS_5.1/ASE5.1.1/sol28g/conf/ase.properties $INSTALLROOT/ASESubsystem/conf/ase.properties property.out >> $LOG_FILE

result=`grep Exception $LOG_FILE`
if [ "$result" ]
then 
echo "${bold}Exception in java prgram."
echo "Applying roll back on the setup. ${offbold} "
./rollbackMop.sh $INSTALLROOT
echo "${bold}Exiting............. ${offbold}"
exit
fi
 
result=`grep Error $LOG_FILE`
if [ "$result" ]
then 
echo "${bold}Error in java prgram."
echo "Applying roll back on the setup. ${offbold}"
./rollbackMop.sh $INSTALLROOT
echo "${bold}Exiting............. ${offbold}"
exit
else
echo "java program executed successfully" >> $LOG_FILE
fi

echo "moving property file" >> $LOG_FILE
mv property.out $ASE_HOME/conf/ase.properties


#### creating /LOGS/SAS/ directory
USER_NAME=`/usr/bin/id |cut -f2 -d'(' |cut -f1 -d')'`
prompt_root


### BPInd18325
echo "copying profile " >> $LOG_FILE
cp ~/profile.sas.$USER_NAME $INSTALLROOT/../SAS_5.1

#### BPInd18325 ends

echo "=========== REPLACING INSTALL_ROOT IN SCRIPT ==========" >> $LOG_FILE

if [ ! -d /tmp/$USER_NAME ] 
then
mkdir /tmp/$USER_NAME
fi
chmod +x $INSTALLROOT/ASE5.5/setup/ase/macroconfig.sh
cd $INSTALLROOT/ASE5.5/setup
$INSTALLROOT/ASE5.5/setup/ase/macroconfig.sh >> $LOG_FILE
cd $MOP_DIR

ask_db_sid

get_subsystem_id
SUBSYS_ID=$return_id

echo "upgradation for multi lan setup"

export old_prop_file=$INSTALLROOT/ASE5.1.1/sol28g/conf/ase.properties
export prop_file=$INSTALLROOT/ASESubsystem/conf/ase.properties

get_signaling_ip
get_peer_ip

echo "updating db oids value  "
echo " =============== UPDATE DB OUTPUT ===================  " >> $LOG_FILE

update_db 

##############################################
###### SystemMonitor upgradation starts  #####

echo "upgrading system monitor"

gunzip -c SystemMonitor.tar.gz | tar xf -

cd systemmonitor
#if [ -f SystemMonitorPatch_jumbo.tar.gz ]
#then
#gunzip -f SystemMonitorPatch_jumbo.tar.gz
#fi
#tar xf SystemMonitorPatch_jumbo.tar

gunzip -c SystemMonitorPatch_jumbo.tar.gz | tar xf -

chmod +x *
#./sysmonupgrade_jumbo.sh $ROOT_PWD /opt/Baypackets/SAS/SystemMonitor/ >> $LOG_FILE
./sysmonupgrade_jumbo.sh $ROOT_PWD  >> $LOG_FILE
cd $MOP_DIR

###### SystemMonitor upgradation ends   ######
##############################################

##############################################
###### house keeping upgradation starts  #####

#echo "upgrading house keeping "
#gunzip -c HouseKeeping.tar.gz | tar xf -
#cd housekeeping
#gunzip -c HouseKeepingUpgrade.tar.gz | tar xf -

#./houseKeeping_upgrade.sh $ROOT_PWD

#if [ $? != 0 ]
#then
#	echo "Problem in House Keepig upgradation."
#	echo "Applying rollback only for House Keeping"
#	./houseKeeping_rollback.sh $ROOT_PWD 
#	if [ $?!= 0 ]
#	then
#		echo "Problem in House Keepig rollback."
#	fi
#echo "Again apply upgradation only for House Keeping "
#echo "Run ./housekeeping/houseKeeping_upgrade.sh"
#exit
#fi

###### house keeping upgradation ends   ######
##############################################

######## creating sysmon & HKP links #########

create_link

touch mop_success

echo "${bold}MOP APPLIED SUCCESSFULLY${offbold}"

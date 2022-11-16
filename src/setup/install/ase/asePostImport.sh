#!/bin/ksh

###Author : Siddharth Angrish,  SipServlet Team, 3/10/05 "

# MACHINE1 should be the machine on which you are running this script
#export MACHINE1="rentbplinux125"
#export MACHINE2="rentbplinux143"
#export MACHINE3="rentbplinux120"

#export MACHINE1_IP="192.168.12.15"
#export MACHINE2_IP="192.168.12.23"
#export MACHINE3_IP="192.168.12.14"

#export CLUSTER_NAME="SAS"
#UNIX_USER="snp1"
#BMGR_DBUSER=snp2
#BMGR_DBPASSWD=snp2
#BMGR_DB1SID=bplin03
#BMGR_DB2SID=bpsun406

# MY_HOST should be the same as MACHINE1: The machine on which you are running this script
#MY_HOST="rentbplinux125"
#NO_OF_MACHINES=3

#DB_FT_REQD=Y

export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$ORACLE_HOME/lib

## This script has to be executed only once per installation
if [[ $MY_HOST == $MACHINE1 ]]
then
  echo "GOT into THE IF"

  ems_db=$BMGR_DBUSER/$BMGR_DBPASSWD@$BMGR_DB1SID

  DB_COUNT=1
  if [[ $DB_FT_REQD == "Y" ]]
  then
    DB_COUNT=2
  fi

  echo "FT Type, that is, no of DBs : $DB_COUNT"	

  ## This while loop ensures that script is made, run and
  ## deleted twice if there is DB_FT
  current_db_no=0
  while [[ $current_db_no -lt $DB_COUNT ]]
  do
    current_db_no=$((current_db_no+1))
    if [[ $current_db_no == "2" ]]
    then
      ems_db=$BMGR_DBUSER/$BMGR_DBPASSWD@$BMGR_DB2SID
    fi

    prefix="MACHINE"
    env2="1"

    ### removing this file if it already exists
    rm -f /tmp/temp.sh
    echo '#!/bin/ksh' > /tmp/temp.sh
    echo '\n' >> /tmp/temp.sh

    TOTALAPPARENT=$(($NO_OF_MACHINES+1))
    echo "TOTALAPPARENT=="$TOTALAPPARENT
    count=1
    while [[ $count -lt $TOTALAPPARENT ]]
    do
      #temp="\$$prefix$count"
      temp="$UNIX_USER"_"\$$prefix$count"
      DATA="TEMPVALUE=$temp"
      echo  $DATA >> /tmp/temp.sh

      if [[ $count != "1" ]]
      then
	DATA="FINALVALUE=\$FINALVALUE\",\"\$TEMPVALUE"
      else
	DATA="FINALVALUE=\$TEMPVALUE"
      fi	

      echo  $DATA >> /tmp/temp.sh

      ### Loop to fill peer ipaddresses
      scount=1
      POSTFIX="_IP"
      while [[ $scount -lt $TOTALAPPARENT ]]
      do
	if [[ $scount == "1" ]]
	then
          DATA="PEERFINAL_$count=\"\""
          echo  $DATA >> /tmp/temp.sh
	fi

	if [[ $scount == $count ]]
	then
          # Do nothing A
          scount=$((scount+1))
	else
	  echo "adding... $scount "
	  temp2="\$$prefix$scount$POSTFIX"
	  DATA="PEERTEMP=$temp2"
	  echo  $DATA >> /tmp/temp.sh

	  DATA="PEERFINAL_$count=\$PEERFINAL_$count\$PEERTEMP\",\""
	  echo  $DATA >> /tmp/temp.sh
	  scount=$((scount+1))
	fi
      done

      DATA="PEERFINAL_$count=\`echo \$PEERFINAL_$count | sed 's/,\$//' \`"
      echo  $DATA >> /tmp/temp.sh
      DATA=" echo PEERFINAL_$count==\$PEERFINAL_$count"
      echo  $DATA >> /tmp/temp.sh

      count=$((count+1))
    done

    DATA="echo FINALVALUE==\$FINALVALUE"
    echo  $DATA >> /tmp/temp.sh

    DATA="\n"
    echo  $DATA >> /tmp/temp.sh

    DATA="\$ORACLE_HOME/bin/sqlplus -silent $ems_db << EOC"
    echo  $DATA >> /tmp/temp.sh
    DATA="UPDATE RSICOMPONENT_CONFIG a SET a.new_param_val='\$FINALVALUE'  WHERE a.param_oid='30.1.28' AND a.component_id IN ( SELECT  b.subsystem_id FROM   RSISUBSYSTEM b , RSIFUNCTIONALUNIT c , RSIFUNCTIONALUNITGROUP d , RSISYSTEM e , RSICLUSTER f WHERE b.fu_id=c.fu_id AND c.fug_id=d.fug_id AND d.system_id=e.system_id AND e.cluster_id=f.cluster_id AND f.name=UPPER('$CLUSTER_NAME'));"
    echo  $DATA >> /tmp/temp.sh

    DATA="commit;"
    echo  $DATA >> /tmp/temp.sh

    DATA="quit;"
    echo  $DATA >> /tmp/temp.sh

    DATA="EOC"
    echo  $DATA >> /tmp/temp.sh

    count=1
    while [[ $count -lt $TOTALAPPARENT ]]
    do
      DATA="\n"
      echo  $DATA >> /tmp/temp.sh

      temp="\$$prefix$count"
      DATA="TEMPVALUE=$temp"
      echo  $DATA >> /tmp/temp.sh

      DATA="echo TEMPVALUE==\$TEMPVALUE"
      echo  $DATA >> /tmp/temp.sh

	DATA="SUB_NAME=\${TEMPVALUE}_$CLUSTER_NAME"
      	echo  $DATA>> /tmp/temp.sh
      DATA="\$ORACLE_HOME/bin/sqlplus -silent $ems_db << EOC"
      echo  $DATA >> /tmp/temp.sh
      DATA="UPDATE RSICOMPONENT_CONFIG y set y.new_param_val='\$PEERFINAL_$count'  WHERE y.param_oid='30.1.31' AND y.component_id IN ( SELECT z.subsystem_id FROM RSISUBSYSTEM z WHERE z.name LIKE UPPER('%\$TEMPVALUE%') AND subsystem_id IN (  SELECT  b.subsystem_id FROM   RSISUBSYSTEM b , RSIFUNCTIONALUNIT c , RSIFUNCTIONALUNITGROUP d , RSISYSTEM e , RSICLUSTER f WHERE b.fu_id=c.fu_id AND c.fug_id=d.fug_id AND d.system_id=e.system_id AND e.cluster_id=f.cluster_id AND f.name=UPPER('$CLUSTER_NAME')));"
      echo  $DATA >> /tmp/temp.sh

      DATA="commit;"
      echo  $DATA >> /tmp/temp.sh

     DATA="INSERT into RSICOMPONENT_CONFIG y (NEW_PARAM_VAL,PARAM_VAL,PARAM_OID,COMPONENT_ID, SUBCOMPONENT_ID) values  ('\$MACHINE$count$POSTFIX','\$MACHINE$count$POSTFIX' , '30.1.24', ( SELECT z.subsystem_id FROM RSISUBSYSTEM z WHERE z.name LIKE UPPER('%ASE%\$SUB_NAME')),0);"
      echo  $DATA >> /tmp/temp.sh

      DATA="commit;"
      echo  $DATA >> /tmp/temp.sh

      DATA="quit;"
      echo  $DATA >> /tmp/temp.sh

      DATA="EOC"
      echo  $DATA >> /tmp/temp.sh

      ##increment counter
      count=$((count+1))
    done

    chmod +x /tmp/temp.sh
    /tmp/temp.sh

    MESSAGE="echo \"FINISHED\""
    echo $MESSAGE >> /tmp/temp.sh

    ##It finshes the while loop which generates,executes and deletes
    ##temp.sh file on each DB
  done

  echo  "SUCCESS success "
  ### This if condition ends
else
  echo  "   *** FAILED *** MY_HOST is different than MACHINE1. MACHINE1 should be the same as the machine on which you are running this script. Fix this and execute this script again."
fi

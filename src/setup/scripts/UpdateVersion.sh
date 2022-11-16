#!/usr/bin/ksh

. ~/profile.cas.*

#path to be exported for both Solaris and Linux Commands
export PATH=/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin:$PATH

if [ -z "$1" ];
then
echo "Please enter Patch Version as the first argument"
echo "EXITTING!!!"
return
fi

if [ -z "$2" ];
then
echo "Please enter INGW/ASESUBSYSTEM/SPSI as the second argument"
echo "EXITTING!!!"
return
fi

if [ $2 = "ASESUBSYSTEM" ]; then
Type=89
elif [ $2 = "INGW" ]; then
Type=97
elif [ $2 = "SPSI" ]; then
Type=32
else
echo "Only INGW/ASESUBSYSTEM/SPSI are allowed as the second argument"
echo "EXITTING!!!"
return
fi

ProductId=$ProductId
echo $ProductId
echo $Type
typeset -u host=`uname -n`
typeset -u user=`whoami`
temp=$2
new=${temp}_${host}%_${user}
echo $new
patch_ver=$1

dbftStatus=$DBFT_STATUS
db_user=$DB_USER
db_passwd=$DB_PASSWD
primary_db=$CONN_STRING
mirror_db=$MIRROR_DB

#### Database Checking For DataGuard #####
if [ $dbftStatus -eq 2 ]
then
echo "dbftStatus is dataguard."
command="select open_mode from v\$database;"
TEST=`sqlplus -silent $db_user/$db_passwd@$primary_db << EOF
set head off;
$command
EOF`

count=`echo $TEST | grep "WRITE"|wc -l`

if [ $count -ne 1 ]
then
TEST1=`sqlplus -silent $db_user/$db_passwd@$mirror_db << EOF
set head off;
$command
EOF`

count=`echo $TEST1 | grep "WRITE"|wc -l`

if [ $count -ne 1 ]
then
echo "Error !!! Unable to connect to any of the Databases"
echo "For DataGuard, one Database should be UP and available"
echo "So Version cannot be updated"
echo "EXITTING!!!"
#exit 1
return
else
primary_db=`echo $mirror_db`
mirror_db=
fi

else
mirror_db=
fi

#### Database Checking For Non DataGuard DBFT #####
elif [ $dbftStatus -eq 1 ]
then

echo "dbft status is Non DataGuard DBFT."
TEST=`sqlplus -silent $db_user/$db_passwd@$primary_db << EOF
set head off;
select * from RSIBAYMGR_CONFIG;
EOF`

count=`echo $TEST | grep "ORACLE"|wc -l`

if [ $count -ne 0 ]
then
echo "Unable to connect to database $primary_db"
echo "Both the databases must be UP and available for DBFT"
echo "So Version cannot be updated"
echo "EXITTING!!!!"
#exit 1
return

else
TEST1=`sqlplus -silent $db_user/$db_passwd@$mirror_db << EOF
set head off;
select * from RSIBAYMGR_CONFIG;
EOF`

count=`echo $TEST1 | grep "ORACLE"|wc -l`

if [ $count -ne 0 ]
then
echo "Unable to connect to database $mirror_db"
echo "Both the databases must be UP and available for DBFT"
echo "So Version cannot be updated"
echo "EXITTING!!!!"
#exit 1
return
fi

fi

#### Database Checking For Non DBFT #####
elif [ $dbftStatus -eq 0 ]
then

echo "dbft status is Non DBFT."
TEST=`sqlplus -silent $db_user/$db_passwd@$primary_db << EOF
set head off;
select * from RSIBAYMGR_CONFIG;
EOF`

count=`echo $TEST | grep "ORACLE"|wc -l`

if [ $count -ne 0 ]
then
echo "Unable to connect to database $primary_db"
echo "Database must be UP and available for Non DBFT"
echo "So Version cannot be updated"
echo "EXITTING!!!!"
#exit 1
return
fi

#### Database Checking For Oracle RAC #####
elif [ $dbftStatus -eq 3 ]
then
echo "dbft status is RAC."
TEST=`sqlplus -silent $db_user/$db_passwd@$primary_db << EOF
set head off;
select * from RSIBAYMGR_CONFIG;
EOF`

count=`echo $TEST | grep "ORACLE"|wc -l`

if [ $count -ne 0 ]
then
echo "Unable to connect to database $primary_db"
echo "Database must be UP and available for Oracle RAC"
echo "So Version cannot be updated"
echo "EXITTING!!!!"
return
fi

fi

echo "Database [$primary_db] Status OK to continue running the script"
echo " "


echo " Enter the appropriate option (1/2) - "
echo "	1 Update Product Version $ProductId$patch_ver"
echo "	2 Exit"

echo " Your Option is :"
read ans


if [ $ans = '1' ]
then

unset LD_LIBRARY_PATH
export LD_LIBRARY_PATH=${ORACLE_HOME}/lib:${ORACLE_HOME}/rdbms/lib:/usr/ucblib
export PATH=${ORACLE_HOME}/bin:$PATH

echo " "

export logfile="/tmp/.viewcreatelog.`date +%j%H%M%S`"
echo " "



echo "primary dbname : $primary_db"
echo  "secondary db name : $mirror_db"


echo "Going to Update Version. Sure? (Y/N)"
read patchAns

if [ $patchAns = 'y' ] || [ $patchAns = 'Y' ]
then
for db_name in $primary_db $mirror_db
do
 	if [ -z $db_name ]
  	then
 		continue
 	else
sqlplus $db_user/$db_passwd@$db_name << END >> $logfile
--set termout off
set serveroutput on;

	DECLARE
	emsversion VARCHAR2(50);
	check_MAIN VARCHAR2(50);
	--ProductId VARCHAR2(50);
	name VARCHAR2(100);
	prevRec NUMBER(10);
	prevRec_Product_Detail NUMBER(10);
	prevRec_Install_Detail NUMBER(10);
	
	
	CURSOR myCur IS
	SELECT subsystem_id, name FROM RSISUBSYSTEM where name like '$new' AND TYPE=$Type;
	
	BEGIN
		FOR rec IN myCur LOOP
			DBMS_OUTPUT.PUT_LINE(rec.subsystem_id);
			IF ('$ProductId' LIKE 'DR_%')OR ('$ProductId' NOT LIKE 'DR_%' )THEN
			
				SELECT VERSION into emsversion FROM RSI_INV_PRODUCT_DETAIL WHERE ACCEPTANCE_DATE = (SELECT MIN(ACCEPTANCE_DATE) FROM RSI_INV_PRODUCT_DETAIL WHERE PRODUCT_ID = '$ProductId') AND PRODUCT_ID = '$ProductId';
				
				SELECT count(*) into check_MAIN FROM RSI_INV_PRODUCT_DETAIL WHERE ACCEPTANCE_DATE = (SELECT MIN(ACCEPTANCE_DATE) FROM RSI_INV_PRODUCT_DETAIL WHERE PRODUCT_ID = '$ProductId') AND PRODUCT_ID = '$ProductId';
				IF (check_MAIN >= 0) THEN	
					BEGIN
						SELECT count(*) into prevRec FROM RSI_INV_INST_MODULE_DETAIL WHERE VERSION='$patch_ver' and PRODUCT_ID='$ProductId' and SUBSYS_ID=rec.subsystem_id;
					EXCEPTION
						WHEN NO_DATA_FOUND THEN
							prevRec:=0;
					END;
				
					BEGIN
						SELECT count(*) into prevRec_Product_Detail FROM RSI_INV_PRODUCT_DETAIL WHERE VERSION='$patch_ver' and PRODUCT_ID='$ProductId';
					EXCEPTION
				    	WHEN NO_DATA_FOUND THEN
							prevRec_Product_Detail:=0;
					END;
				
					BEGIN
						SELECT count(*) into prevRec_Install_Detail FROM RSI_INV_INSTALLATION_DETAIL WHERE VERSION='$patch_ver' and PRODUCT_ID='$ProductId';
					EXCEPTION
				    	WHEN NO_DATA_FOUND THEN
							prevRec_Install_Detail:=0;
					END;
				
					DBMS_OUTPUT.put_line('prevRec' || prevRec || 'prevRec_Product_Detail' || prevRec_Product_Detail || 'prevRec_Install_Detail' || prevRec_Install_Detail);
				
					IF (prevRec = 0) AND (prevRec_Product_detail = 0) AND (prevRec_Install_Detail = 0) THEN	
						TOPOLOGY_PACKAGE.INSERTMODULEDETAILHISTORY('$ProductId','$emsversion','$ProductId$patch_ver','$patch_ver',rec.subsystem_id);
						DELETE RSI_INV_PRODUCT_DETAIL where ACCEPTANCE_DATE=(SELECT max(ACCEPTANCE_DATE) from RSI_INV_PRODUCT_DETAIL WHERE PRODUCT_ID = '$ProductId' AND ACCEPTANCE_DATE NOT IN (SELECT MAX(ACCEPTANCE_DATE) FROM RSI_INV_PRODUCT_DETAIL WHERE PRODUCT_ID = '$ProductId')) and PRODUCT_ID = '$ProductId'; 
						DBMS_OUTPUT.put_line('Version tables are updated with patch info Successfully');
					ELSE
						DBMS_OUTPUT.put_line('Version tables already have patch information. No need to update tables!.');
					END IF;--END IF PRODUCT FOUND
				ELSE
					DBMS_OUTPUT.put_line('NO ACTIVE PRODUCT FOUND!.');
				END IF;--END IF CHECK_MAIN>0
			END IF;--END IF PRODUCT_ID LIKE..
		END LOOP;
	END;
	/
	
	commit;
	show errors

END
# echo "Tables are updated Successfully !."
fi

done
else
echo "Exitting.."
return
fi

TAB_UPDATED=`grep -i "Version tables are updated with patch info Successfully" $logfile | wc -l`
export TAB_UPDATED

	if [ $TAB_UPDATED = "0" ]
        then
		echo "Version tables already have patch version information. No need to update tables!."
	else
                echo "Version tables are updated with $ProductId patch info Successfully!."
        fi

NUM_ERRS=`grep -i error $logfile | grep -v -i "no errors"| wc -l`
export NUM_ERRS


	if [ $NUM_ERRS = "0" ]
        then
  		echo "No Errors Encountered..."
    	else
            echo "ERRORS ENCOUNTERED IN Updating Version in $db_name."
            export errfile=/tmp/errlog_$db_name
            cp $logfile $errfile
            echo "Please see $errfile for details."
            echo ""
            echo -e "\033[1mEXITING\033[0m"
            #rm -f $logfile
            return 1
    	fi



#rm -f $logfile
else
echo "Exiting without Updating Version!!"
#fi
fi

if [ $ans = '2' ]
then
	echo "Exittting without Updating Version!!"
fi

return 0;

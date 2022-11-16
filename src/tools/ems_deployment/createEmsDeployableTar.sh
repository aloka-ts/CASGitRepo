#!/bin/sh

OS=`uname -a | cut -d " " -f1`

OSName=""
if test "$OS" = "SunOS"
then 
	OSName="Solaris"
elif test "$OS" = "Linux"
then 
	OSName="Linux"
fi

debug()
{
	msg=$1
	#echo "At stage $1"
}

# argumenets 
# $1 = 1 for string validation 
#	   = 2 for file existence 
# $2 = string input incase $1=1
#		 = file name incase $1=2
validateInput()
{
	# 1 - validating string input
	# 2 - validating file existence
	if test $1 = "1"
	then 
		if test -z "$2"
		then
			echo "Please enter valid $3. Exiting"
			exit
		fi
	elif test $1 = "2"
	then
		if  [ ! -f "$2" ]
		then
			echo "File [$2] does not exists. Exiting"
			exit
		fi
	fi
}

replaceData()
{
        mkdir -p /tmp/sas/
        sar_name=$1
        sar_path=$2

        cp $sar_path/$sar_name /tmp/sas/

        curr_dir=`pwd`
        cd /tmp/sas/

        jar xf $sar_name
        cd WEB-INF

        productID=`cat sas.xml  | grep "<name>" | awk -F">" '{print $2}' | awk -F"<" '{print $1}'`

        appVer=`cat sas.xml  | grep "<version>" | awk -F">" '{print $2}' | awk -F"<" '{print $1}'`

        cd $curr_dir
}

productID=""
appVer=""

echo "Enter application name (e.g., Application1.sar): "
read sarName
validateInput "1" "$sarName" "application name"
debug "sarname Validation Completed"

echo "Enter location of application: [`pwd`] "
read location

if test -z "$location" 
then 
location=`pwd`
fi

validateInput "2" "$location/$sarName" 
debug "File existence at location $location/$sarName validated"

replaceData "$sarName" "$location/"

appName=`echo $sarName | cut -d "." -f1`
# check if appname is not null
mkdir -p /tmp/$appName/service
cp TLDD.xml /tmp/$appName/service
cp TIDD.xml /tmp/$appName/
cp $location/$sarName /tmp/$appName/service

debug "Directory /tmp/$appName created"

#echo "Enter application version: " 
#read appVer
#validateInput "1" "$appVer" "application version"
#debug "Application version is validated"

n_cnt=0
sed 's/APPNAME/'$appName'/g' /tmp/$appName/TIDD.xml  > /tmp/$appName/p.${n_cnt}
s_cnt=$n_cnt

n_cnt=`expr $s_cnt + 1`
sed 's/APP_VERSION/'$appVer'/g' /tmp/$appName/p.${s_cnt}  > /tmp/$appName/p.${n_cnt}
s_cnt=$n_cnt

n_cnt=`expr $s_cnt + 1`
sed 's/DEPLOYED_ON/'$OSName'/g' /tmp/$appName/p.${s_cnt}  > /tmp/$appName/p.${n_cnt}
s_cnt=$n_cnt

n_cnt=`expr $s_cnt + 1`
sed 's/PRODUCT-ID/'$productID'/g' /tmp/$appName/p.${s_cnt}  > /tmp/$appName/p.${n_cnt}
mv  /tmp/$appName/p.${n_cnt}  /tmp/$appName/TIDD.xml
s_cnt=$n_cnt

debug "TIDD changes done"

rm /tmp/$appName/p.*
rm -rf /tmp/sas

debug "temporary files remvoed"

sed 's/SAR_NAME/'$sarName'/g' /tmp/$appName/service/TLDD.xml  > /tmp/$appName/service/p
mv  /tmp/$appName/service/p  /tmp/$appName/service/TLDD.xml
debug "TLDD changes done"

#create service tar ball

cd /tmp/$appName/
tar cf service.tar service
rm -fR service/

cd ..
tar cf $appName.tar $appName
rm -fR $appName/

debug "tar ball created"

echo "The package $appName.tar is available in /tmp folder. Use this to deploy from EMS"

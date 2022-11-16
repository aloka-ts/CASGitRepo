#/bin/ksh

##########################################################################################
#   Objective       : SAS upgradation propcess from SAS5.1.1 to SAS 5.5
#   script			: return the sas signalling ip on the SAS machine for multi LAN SAS.
#   Author          : Prashant Kumar
#   Last Modified   : 10.03.2007
##########################################################################################


export PATH=/bin:/usr/bin:/sbin:/usr/sbin:.:$PATH


DB_STR=$1
SUB_ID=$2

if [ -f $3 ]
then 
prop_file=$3
else
prop_file=$4
fi

ems_ip=$5
user_name=$6
user_pwd=$7
fip=$8

echo "IP IS $ems_ip"
echo "user name $user_name"
echo "user pass $user_pwd"
echo "fip is $fip"

self_ip=`cat $prop_file | grep 30.1.24 | cut -f2 -d'='`
echo "self ip is " $self_ip

> IPAD
ifconfig -a | grep inet |cut -f2 -d' ' >> IPAD

for i in `cat IPAD`
do
echo $i

if [ $i == "127.0.0.1" ]
then
	echo "local host ip address $i"
	continue
fi

if [ $i == $self_ip ]
then 
	echo "it is self ip $i "
	continue
fi

> sqlCheckIP.log

if [ ! "$fip" ]
then
echo "getting fip from db "
TestRexec -host $ems_ip -userId $user_name -passwd $user_pwd -cmdfile sqlcheckIP.sh $DB_STR $SUB_ID >> sqlCheckIP.log
fip=`cat sqlCheckIP.log | grep % | cut -f2 -d'%'`
echo "FIP :$fip"
fi

if [ $i == $fip ] 
then 
	echo "it is fip :$i"
	continue
fi
	echo "It is signalling ip "
	echo "%" $i
done  


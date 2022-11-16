#!/bin/sh

#echo $1
#file_size=`stat -c %s $1`
#echo $file_size
prvs_file="null"
i=1
while true
do
#file=`find /LOGS/SAS -type f -name "*.rexec_out" | tail -1`
file=`ls -rt /LOGS/SAS/*rexec_out* | tail -1`
#echo $file | cut -f4 -d/
#file_name=`echo $file | cut -f4 -d/`
#echo $file_name
if [ "$prvs_file" != "$file" ]
then
i=1
prvs_file=$file
fi
#echo "this is echoa $file"
if [ -f "$file" ]
then
file_size=`stat -c %s $file`
 if [ "$file_size" -gt "26214400" ]
  then
   file_name=`echo $file | cut -f4 -d/`
   i=`find /LOGS/SAS -type f -name $file_name"*" | wc -l`
   i=`echo $i | cut -f2 -d" "`
   tmp=$file"."$i
   echo $tmp
   cp $file $tmp
   #i=`expr $i + 1`
   >$file
 fi
 sleep 3600
 #echo $i
fi
done
#echo $i

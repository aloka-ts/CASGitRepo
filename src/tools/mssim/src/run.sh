#!/bin/bash

CLASSPATH=
export CLASSPATH

for filename in ./lib/*.jar mssim.jar
do
	CLASSPATH=$CLASSPATH:$filename
done

java com.genband.mssim.MsSimulator $@

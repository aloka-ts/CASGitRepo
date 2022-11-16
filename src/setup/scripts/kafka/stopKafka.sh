#!/bin/ksh

export JAVA_HOME=/usr/java
export PATH=$PATH:$JAVA_HOME/bin

export KAFKA_PATH=/AGNITY/agnity/kafka/kafka_2.12-2.3.1
$KAFKA_PATH/bin/kafka-server-stop.sh  $KAFKA_PATH/config/server.properties
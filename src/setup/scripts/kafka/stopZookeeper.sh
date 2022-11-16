#!/bin/ksh
export JAVA_HOME=/usr/java
export PATH=$PATH:$JAVA_HOME/bin
export ZOOKEEPER_PATH=/AGNITY/agnity/kafka/apache-zookeeper-3.6.1
$ZOOKEEPER_PATH/bin/zkServer.sh stop $ZOOKEEPER_PATH/conf/zoo.cfg
#!/bin/ksh

export CLASSPATH=$CLASSPATH
. ../build/ant.properties

export JAVA_HOME
$THIRDPARTY/ANT/apache-ant-1.5.3/bin/ant $@


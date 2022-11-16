#!/bin/ksh

###############################################################################
# Set the various environment variables used
export JAVA_HOME=/usr/java
export JRE_HOME=${JAVA_HOME}/jre
AT_HOME=${1}

###Set the env variables....
export PATH=${PATH}:${JAVA_HOME}/bin:${JRE_HOME}/bin:/usr/bin

###pwd
####echo ${AT_HOME}
cd ${AT_HOME}
java -jar atClient.jar application.properties

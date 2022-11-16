#!/bin/ksh

###############################################################################
# Set the various environment variables used
export JAVA_HOME=/usr/java
export JRE_HOME=${JAVA_HOME}/jre
CDR_UTILITY_HOME_PULL=${1}

###Set the env variables....
export PATH=${PATH}:${JAVA_HOME}/bin:${JRE_HOME}/bin:/usr/bin

###pwd
####echo ${CDR_UTILITY_HOME_PULL}
cd ${CDR_UTILITY_HOME_PULL}
java -jar cdrDbPull.jar

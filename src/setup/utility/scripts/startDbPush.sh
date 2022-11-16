#!/bin/ksh

###############################################################################

# Set the various environment variables used
export JAVA_HOME=/usr/java
export JRE_HOME=${JAVA_HOME}/jre
CDR_UTILITY_HOME_PUSH=${1}

#Run CAS Profile
. $HOME/profile.cas.*

###Set the env variables....
export PATH=${PATH}:${JAVA_HOME}/bin:${JRE_HOME}/bin:/usr/bin

###pwd
####echo ${CDR_UTILITY_HOME_PUSH}
cd ${CDR_UTILITY_HOME_PUSH}
java -jar cdrDbPush.jar

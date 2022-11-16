#!/bin/ksh
 
export JAVA_HOME=/usr/java
 
exec $INSTALLROOT/ASESubsystem/Common/thirdParty/apache-activemq-5.3.0/bin/run_activemq.sh /LOGS/SAS/ $INSTALLROOT/ASESubsystem/Common/thirdParty/apache-activemq-5.3.0/bin $INSTALLROOT/ASESubsystem/conf/jms_broker.xml

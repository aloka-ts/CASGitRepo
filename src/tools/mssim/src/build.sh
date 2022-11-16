#!/bin/sh

JAVA_HOME=/vob/thirdParty/java/j2sdk1.4.2_11
export JAVA_HOME

THIRDPARTY=/vob/thirdParty
export THIRDPARTY

ANT_HOME=$THIRDPARTY/ANT/apache-ant-1.5.3
export ANT_HOME

PATH=$JAVA_HOME/bin:$ANT_HOME/bin:/usr/bin:.
export PATH

$ANT_HOME/bin/ant mssim $@

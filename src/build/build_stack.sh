#!/bin/ksh

. ./ant.properties

export JAR_FILE_NAME="dsua.jar"
export SOURCE_DIR=$DS_HOME
export CLASS_DIR="$1/dsclasses"
export DSJARS_DIR="$1/dsjars"

mkdir -p $CLASS_DIR
mkdir -p $DSJARS_DIR

# Following is an excerpt from DS file "setupcp.sh" : [
# Set the value of UAHOME to absolute path of the UA root directory.

UAHOME=$DS_HOME
export UAHOME

THIS_CLASSPATH=.:\
$UAHOME/classes:\
$UAHOME/lib/concurrent.jar:\
$UAHOME/lib/dns.jar:\
$UAHOME/lib/dslicense.jar:\
$UAHOME/lib/flexlm.jar:\
$UAHOME/lib/jcert.jar:\
$UAHOME/lib/jndi.jar:\
$UAHOME/lib/jnet.jar:\
$UAHOME/lib/jsse.jar:\
$UAHOME/lib/log4j.jar:\
$UAHOME/lib/mime.jar:\
$UAHOME/lib/providerutil.jar:\
$UAHOME/lib/stat_util.jar:\
$UAHOME/lib/xmlParserAPIs.jar:\
$UAHOME/lib/xercesImpl.jar:\
$UAHOME/lib/trove.jar:\
$UAHOME/lib/Acme.jar

export THIS_CLASSPATH

# : ]

export JAVAC_FLAGS="-classpath $THIS_CLASSPATH -O -sourcepath $SOURCE_DIR"

export TMP_FILE_NAME="/tmp/`whoami`.$$"

find $SOURCE_DIR/com -type d | grep -v "DsApps" | grep -v "jain" | grep -v "TokenSip\/test" > $TMP_FILE_NAME

# This is one additional step to generate the LL State table class
echo "Generating LL State table class DsSipStateMachineDefinitions..."
export CUR_DIR=`pwd`
cd $SOURCE_DIR/com/dynamicsoft/DsLibs/DsSipLlApi
perl DsSipStateMachineDefinitions.pl
ls -lrt DsSipStateMachineDefinitions.java
cd $CUR_DIR
echo "...done."

cat $TMP_FILE_NAME | while read dir
do
	ls $dir | grep ".java" > /dev/null
	if [[ $? == "0" ]]
	then 
		echo "Compiling $dir..."
		$JAVA_HOME/bin/javac -d $CLASS_DIR $JAVAC_FLAGS $dir/*.java
#		echo "...done."
	fi
done

# Make a Jar of newly compiled classes
cd $CLASS_DIR
$JAVA_HOME/bin/jar -cf $JAR_FILE_NAME com
mv $JAR_FILE_NAME $DSJARS_DIR
cd -

# Cleanup steps..
echo "Cleaning up.."
rm $SOURCE_DIR/com/dynamicsoft/DsLibs/DsSipLlApi/DsSipStateMachineDefinitions.java
rm $TMP_FILE_NAME
rm -rf $CLASS_DIR

echo "Build completed with Jar file : $DSJARS_DIR/$JAR_FILE_NAME"


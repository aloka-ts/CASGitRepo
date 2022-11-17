#!/bin/ksh

# Usage       - /bin/ksh $0 [INSTALLROOT] [JAVA_HOME] [ANT_HOME] [CAS_THIRDPARTY_ROOT] [CAS_STACK_ROOT] [BAYPROCESSOR_ROOT] [EMS_COMMON_ROOT] [<Target>]
#
# File name   - build.sh
#
# Description - Utility to compile the CAS code
# 				[THIRDPARTY_ROOT] - Contains the Thirdparty required by CAS Server
#
# Author	  - Ankit Singhal

#Setup the environment
PATH=$PATH:/bin:/sbin:/usr/bin

. ./ant.properties

PARENT_BASE=${PWD%/*}
echo "------------------------------------------------------------------------------------"
echo "Setting Base Directory: $PARENT_BASE"
echo "------------------------------------------------------------------------------------"
echo ""

#Check whether the specified parameters are correct
if [ $# -lt 1 ]
then
  echo "Usage: $0 [INSTALLROOT] [<Target>]"
  echo
  exit 1
fi

# IF INSTALLROOT passed as argument then set the same
if [ $# -gt 0 ]; then
	export INSTALLROOT=$1
fi

#export FEE_BUILD_ROOT=/AGNITY/aconyx/GitRepo/FEE_BUILD/
#export MPH_BUILD_ROOT=$( cd ../../../MPHGitRepo/build/libs/;echo $PWD)

#export FEE_BUILD_ROOT=${FEE_BUILD_ROOT}
#export MPH_BUILD_ROOT=${MPH_BUILD_ROOT}
export CAS_THIRDPARTY_ROOT=$( cd ../../../thirdPartyCASGitRepo7.5/;echo $PWD)
export CAS_STACK_ROOT=$( cd ../../../CASStackGitRepo7.5/ ;echo $PWD)
export BAYPROCESSOR_ROOT=$( cd ../../../BayProcessorGitRepo;echo $PWD)
export EMS_COMMON_ROOT=$( cd ../../../CommonEMSGitRepo;echo $PWD)
export COMP_MON_ROOT=$( cd ../../../ComponentMonitor;echo $PWD)
PLATFORM=`uname`
if [ ${PLATFORM} == "Linux" ]
then
	echo "Using Linux Envt."
	 #export JAVA_HOME=${CAS_THIRDPARTY_ROOT}/jdk1.6.0_24/
         #export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.65-3.b17.el7.x86_64/
         export JAVA_HOME=${CAS_THIRDPARTY_ROOT}/jdk8u202-b08/ 
         #export JAVA_HOME=${CAS_THIRDPARTY_ROOT}/jdk1.7.0_51/	
         export ANT_HOME=${CAS_THIRDPARTY_ROOT}/apache-ant-1.7.1/
else
	echo "Using Solaris Envt."
	export JAVA_HOME=$( cd ../../../thirdPartyCommonGitRepo/Solaris/jdk1.6.0_21/;echo $PWD)
	export ANT_HOME=$( cd ../../../thirdPartyCommonGitRepo/Solaris/apache-ant-1.7.0/;echo $PWD)
fi


export DS_HOME=${CAS_STACK_ROOT}/stacks/SIPStack/DySIPUAJava_6.4/src/dsua
export CLASSPATH=
export CAP_HOME=${PARENT_BASE}/tcap/cap
#Unable to find this with the current config specs
export BN_HOME=${CAS_THIRDPARTY_ROOT}/BinaryNotes1.5.2

echo "-----------------------------------------------"
echo "Using INSTALLROOT="${INSTALLROOT}
echo "Using JAVA_HOME="${JAVA_HOME}
echo "Using ANT_HOME="${ANT_HOME}
echo "Using DS_HOME="${DS_HOME}
echo "Using CAP_HOME="${CAP_HOME}
echo "Using BN_HOME="${BN_HOME}
echo "Using CAS_THIRDPARTY_ROOT="${CAS_THIRDPARTY_ROOT}
echo "Using CAS_STACK_ROOT="${CAS_STACK_ROOT}
echo "Using BAYPROCESSOR_ROOT="${BAYPROCESSOR_ROOT}
echo "Using EMS_COMMON_ROOT="${EMS_COMMON_ROOT}
echo "Using COMP_MON_ROOT="${COMP_MON_ROOT}
echo "-----------------------------------------------"
echo ""

$ANT_HOME/bin/ant -version;echo ""

#Shifting the attribute list by 7, so that now the list contains only the ANT-Targets
shift 1

if [ $# -ge 1 ]
then
	echo "Ant Targets to Execute: " $@
	echo
 else
	echo "Running default Target"
	echo
fi

#Execute the build
$ANT_HOME/bin/ant sonar -Dsonar.login=135468c9df77494b369eb4c0a26cda02c76248fd -DJAVA_HOME=${JAVA_HOME} -DINSTALLROOT=${INSTALLROOT} -DPARENT_BASE=${PARENT_BASE} -DDS_HOME=${DS_HOME} -DCAP_HOME=${CAP_HOME} -DBN_HOME=${BN_HOME} -DTHIRDPARTY=${CAS_THIRDPARTY_ROOT} -DCAS_STACK_ROOT=${CAS_STACK_ROOT} -DBAYPROCESSOR_ROOT=${BAYPROCESSOR_ROOT}  -DCOMP_MON_ROOT=${COMP_MON_ROOT} -DFEE_BUILD_ROOT=${FEE_BUILD_ROOT} -DMPH_BUILD_ROOT=${MPH_BUILD_ROOT} -DEMS_COMMON_ROOT=${EMS_COMMON_ROOT} $@
ant sonar -v

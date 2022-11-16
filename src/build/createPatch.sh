#!/bin/ksh

# Usage       - /bin/ksh $0 [INSTALLROOT] [SAS_VERSION] [CAS_THIRDPARTY_ROOT]
#
# File name   - createPatch.sh
#
# Description - Utility to create the patch of CAS
# 				[THIRDPARTY_ROOT] - Contains the Thirdparty required by CAS Server
#
# Author	  - Amit Baxi
. ./ant.properties

bold=`tput bold`
offbold=`tput rmso`

if [ $# -ne 2 ]
then
    echo "${bold}Usage: createPatch.sh [CAS-INSTALLROOT] [CAS_VERSION] ${offbold}"
    exit 1
fi

echo "${bold} Creating patch ${offbold} "
cas_build=$1
export version=$2
DATE=`date +%d%m%y`
PATCH_NAME=CAS$version

if [ ! -d "$cas_build" ]
then
    echo "${bold} CAS INSTALLROOT not found... ${offbold} "
    exit
fi


SCR_DIR=`pwd`

cp -f ../tools/patch_mop/applyPatch.sh $cas_build
cp -f ../tools/patch_mop/rollbackPatch.sh $cas_build
cp -rf ../tools/utility_scripts/scripts.tgz $cas_build
cp -rf cas_version_current.cfg $cas_build
chmod u+x "$cas_build"/*

cd "$cas_build"

rm bpjars/mph-data.jar
rm conf/diameter_ro.yml
rm conf/diameter_sh.yml
#rm conf/SmppServerAppRules.yml

#tar cf cas_patch.tar scripts/UpdateVersion.sh scripts/ase_no_ems_jrockit scripts/encryptor/ sysapps/registrar sysapps/registrar.sar sysapps/cim sysapps/cim.war sysapps/pac sysapps/pac.war setup/ase/ASE_template_emsl.xml setup/ase/ASE_template.xml bpjars/ httpjars/ ra/ sbb/ lib/redhat80g dsjars/dsua.jar otherjars/snmp4j-2.3.4.jar otherjars/icmp4j-1018.jar otherjars/asm-5.0.3.jar otherjars/kryo-3.0.3.jar otherjars/kryo-serializers-0.37.jar otherjars/minlog-1.3.0.jar otherjars/objenesis-2.1.jar otherjars/reflectasm-1.10.1.jar otherjars/java-uuid-generator-3.1.3.jar otherjars/commons-lang3-3.4.jar otherjars/commons-collections4-4.1.jar otherjars/guava-19.0.jar otherjars/jersey-client-1.18.1.jar otherjars/jersey-core-1.18.1.jar otherjars/jersey-json-1.18.1.jar otherjars/jersey-server-1.18.1.jar otherjars/jersey-servlet-1.18.1.jar otherjars/commons-pool2-2.4.2.jar otherjars/postgresql-9.4.1212.jre7.jar otherjars/jgroups-3.5.0.Final.jar otherjars/commons-compress-1.12.jar otherjars/org.apache.commons.httpclient.jar msml/input-values.xml Common/


tar cf cas_patch.tar scripts/UpdateVersion.sh scripts/ase_no_ems_jrockit scripts/encryptor/ scripts/ase_no_ems_mjds sysapps/registrar sysapps/registrar.sar sysapps/cim sysapps/cim.war sysapps/pac sysapps/pac.war setup/ase/ASE_template_emsl.xml setup/ase/ASE_template.xml bpjars/ httpjars/ ra/ sbb/ lib/redhat80g dsjars/dsua.jar otherjars  Common/ scripts.tgz mjds/mjds12.0.7-2/


if [ $? -ne 0 ] 
then 
	echo "Problem in packaging"
	if test -s cas_patch.tar
	then 
		rm cas_patch.tar
	fi
	cd $SCR_DIR
	exit
fi

tar cf ${PATCH_NAME}_${DATE}.tar cas_patch.tar applyPatch.sh rollbackPatch.sh cas_version_current.cfg 2>/dev/null

if [ $? -ne 0 ]
then
	echo " ${bold} Patch  packaging Faiiled. Provide correct InstallRoot ${offbold} "
	cd $SCR_DIR
	exit
fi


gzip -f ${PATCH_NAME}_${DATE}.tar

if [ $? -ne 0 ]
then
    echo "Problem in packaging"
	if test -s cas_patch.tar
	then 
		rm cas_patch.tar
	fi
	if test -s ${PATCH_NAME}.tar
	then
		rm ${PATCH_NAME}.tar
	fi
	cd $SCR_DIR
	exit
else 
	echo "${bold} package successfully created at $1 ${offbold} "
	rm -rf applyPatch.sh rollbackPatch.sh cas_patch.tar
fi


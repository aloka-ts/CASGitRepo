#!/bin/ksh

bold=`tput bold`
offbold=`tput rmso`
#export VERSION=REPLACE_VERSION
export VERSION=10.1.0.36.11
PATCHNAME=CAS$VERSION
typeset -r VERSION_FILE_NAME="cas_version.cfg"
typeset -r BACKUP_VERSION_FILE_NAME="backup_cas_version.cfg"



#Reading the latest cas_version.dat to determine the current patch level and other properties
export VERSION_BACKUP=`grep SERVICE_VER ${VERSION_FILE_NAME} |cut -d= -f2`


function putCASJarDetailsInBackupVersionFile {

 echo "SERVICE_VER="${VERSION_BACKUP}>${BACKUP_VERSION_FILE_NAME}
 cp cas_version_current.cfg cas_version.cfg
 rm cas_version_current.cfg
}


modifyRaProperty(){
	if test -s ./telnetsshra.properties
        then
                        grep "telnetssh.ls.quque.loging.period" ./conf/telnetsshra.properties >>/dev/null
                                if [ $? -ne 0 ]
                                        then
                                        	propHeader="#Defined in seconds after which LS quque will be logged into log file"
                                       		newProp="telnetssh.ls.quque.loging.period=120"
                                         	echo "" >> ./conf/telnetsshra.properties
                                         	echo "$propHeader" >> ./conf/telnetsshra.properties
                                         	echo "$newProp" >> ./conf/telnetsshra.properties

                                fi
                        grep "telnetssh.local.environment" ./conf/telnetsshra.properties >>/dev/null
                                if [ $? -ne 0 ]
                                        then
                                        	propHeader="#Flag to indicate telnet ssh ra running in local environment (Default false)"
                                       		newProp="telnetssh.local.environment=false"
                                         	echo "" >> ./conf/telnetsshra.properties
                                         	echo "$propHeader" >> ./conf/telnetsshra.properties
                                         	echo "$newProp" >> ./conf/telnetsshra.properties

                                fi       
	fi
}
	
modifySysAppsProperty(){

if test -s ./conf/registrar.properties
        then
                        grep "registrar.add.empty.passociated.uri.header" ./conf/registrar.properties >>/dev/null
                                if [ $? -ne 0 ]
                                        then
                                        	propHeader="### Flag for addition of empty P-Associated-URI header (Default false)"
                                       		newProp="registrar.add.empty.passociated.uri.header=true"
                                         	echo "" >> ./conf/registrar.properties
                                         	echo "$propHeader" >> ./conf/registrar.properties
                                         	echo "$newProp" >> ./conf/registrar.properties

                                fi
                                
                        grep "registrar.allow.pac.rest.subscription" ./conf/registrar.properties >>/dev/null
                                if [ $? -ne 0 ]
                                        then
                                        	propHeader="### Flag to allow registrar to notify PAC through rest to subscribe for an unsubscribed user (Default true)"
                                       		newProp="registrar.allow.pac.rest.subscription=true"
                                         	echo "" >> ./conf/registrar.properties
                                         	echo "$propHeader" >> ./conf/registrar.properties
                                         	echo "$newProp" >> ./conf/registrar.properties

                                fi
                                
                        grep "registrar.rest.version" ./conf/registrar.properties >>/dev/null
                                if [ $? -ne 0 ]
                                        then
                                        	propHeader="### REST version"
                                       		newProp="registrar.rest.version=v1"
                                         	echo "" >> ./conf/registrar.properties
                                         	echo "$propHeader" >> ./conf/registrar.properties
                                         	echo "$newProp" >> ./conf/registrar.properties

                                fi
                                
                        grep "registrar.ipaddress" ./conf/registrar.properties >>/dev/null
                                if [ $? -ne 0 ]
                                        then
                                        	propHeader="### Enter the value of CAS FIP and the value of HTTP_PORT used in CAS"
                                       		newProp="registrar.ipaddress=CAS_FIP:HTTP_PORT"
                                         	echo "" >> ./conf/registrar.properties
                                         	echo "$propHeader" >> ./conf/registrar.properties
                                         	echo "$newProp" >> ./conf/registrar.properties

                                fi         
					   
fi

if test -s ./conf/pac.properties
		then
						grep "pac.subscribe.for.only.active.users" ./conf/pac.properties >>/dev/null
                                if [ $? -ne 0 ]
                                        then
                                        	propHeader="### Subscription is maintained for only those users whose presence status is available (true/false, default=true)"
                                       		newProp="pac.subscribe.for.only.active.users=true"
                                         	echo "" >> ./conf/pac.properties
                                         	echo "$propHeader" >> ./conf/pac.properties
                                         	echo "$newProp" >> ./conf/pac.properties

                                fi
                                
                        
fi
						grep "pac.sip.subscription.expires.adjust.interval" ./conf/pac.properties >>/dev/null
                                if [ $? -ne 0 ]
                                        then
                                        	propHeader="### Interval(in seconds) for adjusting the Timer running for Subscribe Request. Its Value should be between 30 and 90 seconds.If not, then default value of 30 will be set"
                                       		newProp="pac.sip.subscription.expires.adjust.interval=30"
                                         	echo "" >> ./conf/pac.properties
                                         	echo "$propHeader" >> ./conf/pac.properties
                                         	echo "$newProp" >> ./conf/pac.properties

                                fi
}


modifyAseNoEMSJrockit(){
if test -s ./scripts/ase_no_ems_jrockit
        then
                        echo "Updating INSTALLROOT in ase_no_ems_jrockit..."
                        sed "s#^export INSTALLROOT=.*#export INSTALLROOT=${INSTALLROOT}#" ./scripts/ase_no_ems_jrockit > ./scripts/ase_no_ems_jrockit.tmp.1
                        mv ./scripts/ase_no_ems_jrockit.tmp.1 ./scripts/ase_no_ems_jrockit
                        rm -rf ./scripts/ase_no_ems_jrockit.tmp.*
                        chmod +x ./scripts/ase_no_ems_jrockit

                        echo "Updating ORACLE_HOME in ase_no_ems_jrockit...${ORACLE_HOME}"
                        sed "s#^export ORACLE_HOME=.*#export ORACLE_HOME=${ORACLE_HOME}#" ./scripts/ase_no_ems_jrockit > ./scripts/ase_no_ems_jrockit.tmp.1
                        mv ./scripts/ase_no_ems_jrockit.tmp.1 ./scripts/ase_no_ems_jrockit
                        rm -rf ./scripts/ase_no_ems_jrockit.tmp.*
                        chmod +x ./scripts/ase_no_ems_jrockit

                        echo "Updating Component Version in ase_no_ems_jrockit..."
                        sed "s/^COMPONENT_VERSION=.*/COMPONENT_VERSION=${VERSION}/" ./scripts/ase_no_ems_jrockit > ./scripts/ase_no_ems_jrockit.tmp.1
                        mv ./scripts/ase_no_ems_jrockit.tmp.1 ./scripts/ase_no_ems_jrockit
                        rm -rf ./scripts/ase_no_ems_jrockit.tmp.*
                        chmod +x ./scripts/ase_no_ems_jrockit
                        
                        grep "ENABLE_TCP_PING" ./scripts/ase_no_ems_jrockit >>/dev/null
						if [ $? -ne 0 ]
							then
							sed -i '/^export LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${ASE_HOME}/a\### Set the value of Tcp ping flag. True will enable TCP Ping and false will enable icmp ping \nENABLE_TCP_PING="true" ' ./scripts/ase_no_ems_jrockit
							sed 's|^export ASE_OPTS=.*|export ASE_OPTS="-Dbpdblib.config=${ASE_HOME}/conf/dblib.properties -Djava.security.auth.login.config=${ASE_HOME}/conf/jaas.config -Dase.home=${ASE_HOME} -Dhttp.container.home=${HTTP_CONTAINER_HOME} -DIsEmsManaged=$EMS_MANAGED -DIswEMSManaged=$WEMS_MANAGED -DREXEC_LOG_FILE_NAME=${LOG_FILE_NAME} -DIsTcpPingEnabled=${ENABLE_TCP_PING}"|' ./scripts/ase_no_ems_jrockit > ./scripts/ase_no_ems_jrockit.tmp.1
							mv ./scripts/ase_no_ems_jrockit.tmp.1 ./scripts/ase_no_ems_jrockit
						fi
						
						grep "TRY_NEW_BM_CONN_ON_PING_FAIL" ./scripts/ase_no_ems_jrockit >>/dev/null
						if [ $? -ne 0 ]
							then
							sed -i '/^export ENABLE_TCP_PING=/a\\n### Setting this property as true results in CAS trying for new BayManager Connection if CAS is unable is report its status to BayManager. \nTRY_NEW_BM_CONN_ON_PING_FAIL="false" \n### Setting this property as true restarts the CAS if CAS is unbale to report its status even after obtaining new BayManager Connection. \nEXIT_ON_BM_PING_FAIL="false"' ./scripts/ase_no_ems_jrockit
							sed 's|^export ASE_OPTS=.*|export ASE_OPTS="-Dbpdblib.config=${ASE_HOME}/conf/dblib.properties -Djava.security.auth.login.config=${ASE_HOME}/conf/jaas.config -Dase.home=${ASE_HOME} -Dhttp.container.home=${HTTP_CONTAINER_HOME} -DIsEmsManaged=$EMS_MANAGED -DIswEMSManaged=$WEMS_MANAGED -DREXEC_LOG_FILE_NAME=${LOG_FILE_NAME} -DIsTcpPingEnabled=${ENABLE_TCP_PING} -DTryNewBmConnOnPingFail=${TRY_NEW_BM_CONN_ON_PING_FAIL} -DExitOnBmPingFail=${EXIT_ON_BM_PING_FAIL}"|' ./scripts/ase_no_ems_jrockit > ./scripts/ase_no_ems_jrockit.tmp.1
							mv ./scripts/ase_no_ems_jrockit.tmp.1 ./scripts/ase_no_ems_jrockit
						fi
						
						rm -rf ./scripts/ase_no_ems_jrockit.tmp.*
						chmod +x ./scripts/ase_no_ems_jrockit
fi

}

modifyAppRouterConfig(){

	if test -s ./conf/ApprouterConfig.xml
        then
    grep "allow-uri-without-user" ./conf/ApprouterConfig.xml >>/dev/null
				if [ $? -ne 0 ]
					then
					sed -i '/^<approuter-config>/a\        <allow-uri-without-user>false</allow-uri-without-user>' ./conf/ApprouterConfig.xml
				fi
	fi
	
}


echo "##################################################################################"
echo "################## ${bold}Applying Patch ${PATCHNAME}${offbold} ##################"
echo "##################################################################################"
echo ""
echo ""

CURRDIR=`pwd`

# Run CAS Profile
. $HOME/profile.cas.*

if [[ ! -d $CURRDIR ]]
then
	echo "${bold}You are not in $INSTALLROOT/ASESubsystem dir... Exiting${offbold}"
	exit;
fi

cd $INSTALLROOT/ASESubsystem

if [ -f ${PATCHNAME}_backup.tar ]
then
       echo "${bold} Patch already applied. If you want to apply patch again ${offbold}"
       echo "${bold}  first run rollback script and then try to apply patch again ${offbold}"
       exit
fi

if test -s resources
then
tar -cf ${PATCHNAME}_backup.tar cas_patch.tar scripts/UpdateVersion.sh scripts/ase_no_ems conf/ase.yml conf/cas-startup.yml conf/telnetsshra.properties conf/registrar.properties conf/pac.properties conf/ApprouterConfig.xml sysapps/registrar.sar sysapps/registrar sysapps/pac sysapps/pac.war sysapps/cim sysapps/cim.war ASE_template.xml ASE_template_emsl.xml setup/ase/ASE_template.xml setup/ase/ASE_template_emsl.xml bpjars/ httpjars/ ra/ sbb/ lib/redhat80g resources dsjars/dsua.jar Common/thirdParty/jakarta-tomcat/lib/tomcat-coyote.jar ${BACKUP_VERSION_FILE_NAME}
else
tar -cf ${PATCHNAME}_backup.tar cas_patch.tar scripts/UpdateVersion.sh scripts/ase_no_ems  conf/ase.yml conf/cas-startup.yml conf/telnetsshra.properties conf/registrar.properties conf/pac.properties conf/ApprouterConfig.xml sysapps/registrar.sar sysapps/registrar sysapps/pac sysapps/pac.war sysapps/cim sysapps/cim.war ASE_template.xml ASE_template_emsl.xml setup/ase/ASE_template.xml setup/ase/ASE_template_emsl.xml bpjars/ httpjars/ ra/ sbb/ lib/redhat80g dsjars/dsua.jar Common/thirdParty/jakarta-tomcat/lib/tomcat-coyote.jar ${BACKUP_VERSION_FILE_NAME}

fi

if test -s scripts/encryptor
then
tar -rf ${PATCHNAME}_backup.tar scripts/encryptor
fi

if test -s otherjars/commons-pool-1.3.jar
then
tar -rf ${PATCHNAME}_backup.tar otherjars/commons-pool-1.3.jar
else
tar -rf ${PATCHNAME}_backup.tar otherjars/commons-pool2-2.4.2.jar
fi

if test -s otherjars/asm-3.1.jar
then
tar -rf ${PATCHNAME}_backup.tar otherjars/asm-3.1.jar
else
tar -rf ${PATCHNAME}_backup.tar otherjars/asm-5.0.3.jar
fi

if test -s otherjars/jersey-client-1.12.jar
then
tar -rf ${PATCHNAME}_backup.tar otherjars/jersey-client-1.12.jar otherjars/jersey-core-1.12.jar otherjars/jersey-json-1.12.jar otherjars/jersey-server-1.12.jar otherjars/jersey-servlet-1.12.jar
else
tar -rf ${PATCHNAME}_backup.tar otherjars/jersey-client-1.18.1.jar otherjars/jersey-core-1.18.1.jar otherjars/jersey-json-1.18.1.jar otherjars/jersey-server-1.18.1.jar otherjars/jersey-servlet-1.18.1.jar
fi

#if test -s otherjars/jgroups-2.6.15.GA.jar
#then
#tar -rf ${PATCHNAME}_backup.tar otherjars/jgroups-2.6.15.GA.jar
#else
#tar -rf ${PATCHNAME}_backup.tar otherjars/jgroups-3.5.0.Final.jar
#fi

rm -rf otherjars/commons-compress-1.16.1.jar
if test -s otherjars/commons-compress-1.12.jar
then
tar -rf ${PATCHNAME}_backup.tar otherjars/commons-compress-1.12.jar
fi


if ! test -s appjars
then
mkdir appjars
fi




if [ $? -ne 0 ]
then
	echo "${bold}ERROR in taking backup:${offbold}"
	if test -s ${PATCHNAME}_backup.tar
	then
		rm ${PATCHNAME}_backup.tar
	fi
	exit
fi

if test -s conf/diameter_sh.yml
then
cp -rf conf/diameter_sh.yml conf/diameter_sh.yml_bkp
fi


if test -s conf/diameter_sh.yml
then
cp -rf conf/diameter_sh.yml conf/diameter_sh.yml_bkp
fi

rm -rf scripts/UpdateVersion.sh
rm -rf httpjars/
#rm -rf bpjars/
rm -rf alcjars/
rm -rf sbb/
rm -rf ra/
rm -rf sysapps/cim 
rm -rf sysapps/cim.war 
rm -rf sysapps/registrar
rm -rf sysapps/registrar.sar
rm -rf sysapps/pac
rm -rf sysapps/pac.war
rm -rf lib/redhat80g
rm -rf setup/ase/ASE_template.xml 
rm -rf setup/ase/ASE_template_emsl.xml
rm -rf ASE_template.xml
rm -rf ASE_template_emsl.xml
rm -rf dsjars/dsua.jar
rm -rf otherjars/asm-3.1.jar
rm -rf otherjars/commons-pool-1.3.jar
rm -rf otherjars/jersey*
rm -rf otherjars/jgroups*
rm ${BACKUP_VERSION_FILE_NAME}

tar xf $CURRDIR/cas_patch.tar 
cp setup/ase/ASE_template.xml .
cp setup/ase/ASE_template_emsl.xml .
chmod +x ./scripts/encryptor/*

if [ $? -ne 0 ]
then
	echo "${bold}Problem in Applying Patch; Rolling Back...${offbold}"
	tar xf ${PATCHNAME}_backup.tar
	if [ $? -ne 0 ]
	then
		echo "${bold}Rollback failed...${offbold}"
		exit
	fi

	rm -rf ${PATCHNAME}_backup.tar
	exit
fi


echo "updating scripts"
cp scripts.tgz ~/
cd ~/
tar -xvzf scripts.tgz
cd $INSTALLROOT/ASESubsystem
rm -rf scripts.tgz

#modifyRaProperty
#modifyAseNoEMS
#modifyAseNoEMSJrockit
#modifyAseProperty
#modifySysAppsProperty
#modifyAppRouterConfig

rm -rf apps/archives/CIM*
rm -rf apps/archives/PAC*
rm -rf apps/archives/RegistrarServlet*

#echo " Enter the appropriate option for version update (1/2) - "
#echo "  1 EMS Based Installation"
#echo "  2 Non-EMS/ wEMS Based Installation"

#echo " Your Option is :"
#read ans

#if [ $ans = '1' ]
#	then
#		cd $INSTALLROOT/ASESubsystem/scripts
#		chmod +x UpdateVersion.sh
#		./UpdateVersion.sh $VERSION ASESUBSYSTEM
#fi

cd $CURRDIR

echo "$(date) : $VERSION patch applied successfully." >> $INSTALLROOT/ASESubsystem/patch_history.log
echo "-----------------------------------" >> $INSTALLROOT/ASESubsystem/patch_history.log

echo "${bold}PATCH APPLIED SUCCESSFULLY${offbold}"

#!/bin/ksh

bold=`tput bold`
offbold=`tput rmso`
#export VERSION=REPLACE_VERSION
export VERSION=7.5.11.53
PATCHNAME=CDRUTILITY$VERSION

#  modify dbPUllProperties file
modifydbPullProperty(){

       # adding new property to CDRDbPullConfig.property file
        decoration="###################################################"
        mainpropHeader="###Filename prefix###"
        propHeader="## This property is used for generating CDR file name and used as file prefix ##"
        newProp="cdrpull.cdr.file.prefix=MNSAS1"
        if test -s $PULL_CONFIG_FILE
        then
                grep "cdrpull.cdr.file.prefix" $PULL_CONFIG_FILE >>/dev/null
                if [ $? -ne 0 ]
                then
                        echo "" >> $PULL_CONFIG_FILE
                        echo "$decoration" >> $PULL_CONFIG_FILE
                        echo "$mainpropHeader" >> $PULL_CONFIG_FILE
                        echo "$propHeader" >> $PULL_CONFIG_FILE
                        echo "$decoration" >> $PULL_CONFIG_FILE
                        echo "$newProp" >> $PULL_CONFIG_FILE

                fi
        fi

       # adding new property to CDRDbPullConfig.property file
        decoration="###################################################"
        mainpropHeader="###File size###"
        propHeader="##Max CDRs in file; count; Value less than 0 disables the feature##"
        newProp="cdrpull.cdr.file.maxsize=5000"
        if test -s $PULL_CONFIG_FILE
        then
                grep "cdrpull.cdr.file.maxsize" $PULL_CONFIG_FILE >>/dev/null
                if [ $? -ne 0 ]
                then
                        echo "" >> $PULL_CONFIG_FILE
                        echo "$decoration" >> $PULL_CONFIG_FILE
                        echo "$mainpropHeader" >> $PULL_CONFIG_FILE
                        echo "$propHeader" >> $PULL_CONFIG_FILE
                        echo "$decoration" >> $PULL_CONFIG_FILE
                        echo "$newProp" >> $PULL_CONFIG_FILE

                fi
        fi

       # adding new property to CDRDbPullConfig.property file
        decoration="###################################################"
        mainpropHeader="###File ROLLOVER interval###"
        propHeader="##Rollover period in seconds;  Value less than 0 disables the feature##"
        newProp="cdrpull.cdr.file.maxTime=240"
        if test -s $PULL_CONFIG_FILE
        then
                grep "cdrpull.cdr.file.maxTime" $PULL_CONFIG_FILE >>/dev/null
                if [ $? -ne 0 ]
                then
                        echo "" >> $PULL_CONFIG_FILE
                        echo "$decoration" >> $PULL_CONFIG_FILE
                        echo "$mainpropHeader" >> $PULL_CONFIG_FILE
                        echo "$propHeader" >> $PULL_CONFIG_FILE
                        echo "$decoration" >> $PULL_CONFIG_FILE
                        echo "$newProp" >> $PULL_CONFIG_FILE

                fi
        fi

       # adding new property to CDRDbPullConfig.property file
        decoration="###################################################"
        mainpropHeader="###SFTP filename ctr identifier for instance used to share or isolate ctrs between instances ###"
        propHeader="## It is recommened that f prefix is same for active and stanby identifier should also be same ##"
        newProp="cdrpull.cdr.file.ctr.identifier=MNSAS1"
        if test -s $PULL_CONFIG_FILE
        then
                grep "cdrpull.cdr.file.ctr.identifier" $PULL_CONFIG_FILE >>/dev/null
                if [ $? -ne 0 ]
                then
                        echo "" >> $PULL_CONFIG_FILE
                        echo "$decoration" >> $PULL_CONFIG_FILE
                        echo "$mainpropHeader" >> $PULL_CONFIG_FILE
                        echo "$propHeader" >> $PULL_CONFIG_FILE
                        echo "$decoration" >> $PULL_CONFIG_FILE
                        echo "$newProp" >> $PULL_CONFIG_FILE

                fi
        fi

       # adding new property to CDRDbPullConfig.property file
        decoration="###################################################"
        mainpropHeader="###Factor decide percentage of CDRs to be ignored while closing the file. These CDRs will be written in next file###"
        propHeader="##done to ensure time seq of CDRS##"
        newProp="cdrpull.cdr.size.adjustmnet.factor=5"
        if test -s $PULL_CONFIG_FILE
        then
                grep "cdrpull.cdr.size.adjustmnet.factor" $PULL_CONFIG_FILE >>/dev/null
                if [ $? -ne 0 ]
                then
                        echo "" >> $PULL_CONFIG_FILE
                        echo "$decoration" >> $PULL_CONFIG_FILE
                        echo "$mainpropHeader" >> $PULL_CONFIG_FILE
                        echo "$propHeader" >> $PULL_CONFIG_FILE
                        echo "$decoration" >> $PULL_CONFIG_FILE
                        echo "$newProp" >> $PULL_CONFIG_FILE

                fi
        fi

       # adding new property to CDRDbPullConfig.property file
        decoration="###################################################"
        mainpropHeader="###Minimun number of CDrs for adjustment to be applied###"
        propHeader="## valid number greater than zero ##"
        newProp="cdrpull.cdr.size.adjustmnet.mincdr=100"
        if test -s $PULL_CONFIG_FILE
        then
                grep "cdrpull.cdr.size.adjustmnet.mincdr" $PULL_CONFIG_FILE >>/dev/null
                if [ $? -ne 0 ]
                then
                        echo "" >> $PULL_CONFIG_FILE
                        echo "$decoration" >> $PULL_CONFIG_FILE
                        echo "$mainpropHeader" >> $PULL_CONFIG_FILE
                        echo "$propHeader" >> $PULL_CONFIG_FILE
                        echo "$decoration" >> $PULL_CONFIG_FILE
                        echo "$newProp" >> $PULL_CONFIG_FILE

                fi
        fi

	### Changing an existing property
	#if test -s ./conf/ase.properties
	#then
		#sed 's/\"nonheap.memory.lower.threshold=25600000\"/\"nonheap.memory.lower.threshold=67108864\"/g' ./conf/ase.properties> ./conf/ase.properties.tmp.1
		#mv ./conf/ase.properties.tmp.1 ./conf/ase.properties
		#rm -rf ./conf/ase.properties.tmp.*
	#fi

}


 #modify dbPUshProperties file
modifydbPushProperty(){

       # adding new property to CDRDbPushConfig.property file
        decoration="###################################################"
        mainpropHeader="###Filename prefix###"
        propHeader="## This property is used for generating CDR file name and used as file prefix ##"
        newProp="cdrpull.cdr.file.prefix=MNSAS1"
        if test -s $PUSH_CONFIG_FILE
        then
                grep "cdrpull.cdr.file.prefix" $PUSH_CONFIG_FILE >>/dev/null
                if [ $? -ne 0 ]
                then
                        echo "" >> $PUSH_CONFIG_FILE
                        echo "$decoration" >> $PUSH_CONFIG_FILE
                        echo "$mainpropHeader" >> $PUSH_CONFIG_FILE
                        echo "$propHeader" >> $PUSH_CONFIG_FILE
                        echo "$decoration" >> $PUSH_CONFIG_FILE
                        echo "$newProp" >> $PUSH_CONFIG_FILE

                fi
        fi

        ### Changing an existing property
        #if test -s ./conf/ase.properties
        #then
                #sed 's/\"nonheap.memory.lower.threshold=25600000\"/\"nonheap.memory.lower.threshold=67108864\"/g' ./conf/ase.properties> ./conf/ase.properties.tmp.1
                #sed 's/\"nonheap.memory.threshold=25600000\"/\"nonheap.memory.threshold=67108864\"/g' ./conf/ase.properties> ./conf/ase.properties.tmp.1
                #mv ./conf/ase.properties.tmp.1 ./conf/ase.properties
                #rm -rf ./conf/ase.properties.tmp.*
        #fi

}

echo "##################################################################################"
echo "################## ${bold}Applying Patch ${PATCHNAME}${offbold} ##################"
echo "##################################################################################"
echo ""
echo ""

CURRDIR=`pwd`

# Run SAS Profile
. $HOME/profile.sas.*

cd $INSTALLROOT/ASESubsystem/utility/cdrSftpUtility/dbScripts

if [[ ! -s $INSTALLROOT/cdrSftpUtility ]]
then
       echo "UTILITY NOT FOUND, EXITING"
       exit
fi

if [ -f $INSTALLROOT/${PATCHNAME}_backup.tar ]
then
       echo "${bold} Patch already applied. If you want to apply patch again ${offbold}"
       echo "${bold}  first run rollback script and then try to apply patch again ${offbold}"
       exit
fi

tar -cf $INSTALLROOT/${PATCHNAME}_backup.tar $INSTALLROOT/cdrSftpUtility

PULL_CONFIG_FILE=$INSTALLROOT/cdrSftpUtility/dbPull/CdrPullConfig.properties

#echo "UPDATING DB PULL PROPS"
#modifydbPullProperty
#cp -r ../dbPull/CdrPullDBSchema.properties $INSTALLROOT/cdrSftpUtility/dbPull/

echo "UPDATING DB PULL JAR"
cp -r ../dbPull/cdrDbPull.jar $INSTALLROOT/cdrSftpUtility/dbPull/

PUSH_CONFIG_FILE=$INSTALLROOT/cdrSftpUtility/dbPush/CdrPushConfig.properties
#echo "UPDATING DB PUSH PROPS"
#modifydbPushProperty
#cp -r ../dbPush/dbPush.ctrl $INSTALLROOT/cdrSftpUtility/dbPush/

echo "UPDATING DB PUSH JAR"
cp -r ../dbPush/cdrDbPush.jar $INSTALLROOT/cdrSftpUtility/dbPush/

cd $CURRDIR
echo "${bold}PATCH APPLIED SUCCESSFULLY${offbold}"

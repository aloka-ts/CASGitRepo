//*********************************************************************
//   GENBAND, Inc. Confidential and Proprietary
//
// This work contains valuable confidential and proprietary
// information.
// Disclosure, use or reproduction without the written authorization of
// GENBAND, Inc. is prohibited.  This unpublished work by GENBAND, Inc.
// is protected by the laws of the United States and other countries.
// If publication of the work should occur the following notice shall
// apply:
//
// "Copyright 2007 GENBAND, Inc.  All rights reserved."
//*********************************************************************
//********************************************************************
//
//     File:     INGwIfrMgrAlarmMgr.C
//
//     Desc:
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     pankaj Bathwal  26/11/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraManager");

#include <sys/time.h>
#include <stdlib.h>
#include <strings.h>

using namespace std;

#include "Util/imErrorCodes.h"
#include "INGwInfraParamRepository/INGwIfrPrConfigMgr.h"
#include "INGwInfraParamRepository/INGwIfrPrParamRepository.h"
#include "INGwInfraManager/INGwIfrMgrAlarmMgr.h"
#include "INGwInfraUtil/INGwIfrUtlLock.h"

#define ALARM_GAPP_OID       "ALARM_GAPP_OID"

const char* INGwIfrMgrAlarmMgr::mpcOIDsOfInterest[] =
{
     ALARM_GAPP_OID
};

INGwIfrMgrAlarmMgr* INGwIfrMgrAlarmMgr::mpSelf = NULL;

INGwIfrMgrAlarmMgr& INGwIfrMgrAlarmMgr::getInstance(void)
{
    if(NULL == mpSelf) mpSelf = new INGwIfrMgrAlarmMgr();
    return *mpSelf;
}

INGwIfrMgrAlarmMgr::INGwIfrMgrAlarmMgr()
{
    mbIsInitialized = false;
    pthread_mutex_init(&mLock, NULL);
}

INGwIfrMgrAlarmMgr::~INGwIfrMgrAlarmMgr()
{
    INGwIfrPrConfigMgr::getInstance().unregisterForConfig(mpcOIDsOfInterest, 
																sizeof(mpcOIDsOfInterest)/sizeof(char*), this);
    pthread_mutex_destroy(&mLock);
}

int INGwIfrMgrAlarmMgr::initialize(const char* apcInitInfo)
{
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "In initialize()");

    int retVal = 0;

    const char* pcInitInfo = apcInitInfo;
    string value;
    if(NULL == pcInitInfo) {
        INGwIfrPrParamRepository::getInstance().getValue(ALARM_GAPP_OID, value);
        if(true != value.empty()) {
            pcInitInfo = value.c_str();
        }
    }

    if(NULL != pcInitInfo) {
        list<string>* pConfigList = parseInitInfo(pcInitInfo);
        if(NULL != pConfigList) {
            list<string>::iterator it = pConfigList->begin();
            for( ; it != pConfigList->end(); it++) {
                logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
																	"Configuring Alarm Mgr for [%s]", (*it).c_str());

                retVal = configure(ALARM_GAPP_OID, (*it).c_str(), CONFIG_OP_TYPE_ADD);
                if(0 != retVal) {
                    logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, 
																		 "Alarm Mgr configuration [%s] failed", (*it).c_str());
                }
            }
        }
        else {
            logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, 
														 "Alarm Mgr configuration [%s] failed", pcInitInfo);
        }
        delete pConfigList;
    }
    else {
        logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
												 "Alarm Mgr init info is being passed NULL");
    }
    mbIsInitialized = true;

    INGwIfrPrConfigMgr::getInstance().registerForConfig(mpcOIDsOfInterest, 
										 sizeof(mpcOIDsOfInterest)/sizeof(char*), this);

    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "Out initialize()");
    return 0;
}

int INGwIfrMgrAlarmMgr::configure(const char* apcOID, const char* apcValue, ConfigOpType aeOpType)
{
    if(NULL == apcOID) {
        logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "Received NULL OID for configuration");
        return -1;
    }

    if(NULL == apcValue) {
        logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
												 "Received NULL value for configuration of OID [%s]", apcOID);
        return -1;
    }
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
										 "Received configure for [%s, %s, %s]", 
											apcOID, apcValue, INGwIfrUtlConfigurable::getString(aeOpType));

    INGwIfrMgrAlarmId alarmId = 0;
    unsigned long gappDuration = 0;
    int retVal = parseConfig(apcValue, alarmId, gappDuration);
    if(0 != retVal) {
        logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, 
												 "Received invalid value for configuration [%s, %s]", 
													apcOID, apcValue, INGwIfrUtlConfigurable::getString(aeOpType));
    }
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
										 "Alarm info after parsing is [%ul, %ul]", alarmId, gappDuration);

    pthread_mutex_lock(&mLock);
    INGwIfrMgrAlarmInfoEntry* pEntry = NULL;
    switch (aeOpType) {
        case CONFIG_OP_TYPE_ADD : {
            INGwIfrMgrAlarmInfoEntryMap::iterator it = mAlarmInfoMap.find(alarmId);
            if(it != mAlarmInfoMap.end()) {
                logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, 
																 "Alarm info for Id [%d] already exists. Updating the info.", alarmId);
                pEntry = it->second;
            }
            else {
                logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
																 "Adding the alarm info for Id [%d]", alarmId);
                pEntry = new INGwIfrMgrAlarmInfoEntry();
                mAlarmInfoMap[alarmId] = pEntry;
            }
            pEntry->mGappingDuration = gappDuration;
            pEntry = NULL;
            break;
        }
        case CONFIG_OP_TYPE_REMOVE : {
            INGwIfrMgrAlarmInfoEntryMap::iterator it = mAlarmInfoMap.find(alarmId);
            if(it != mAlarmInfoMap.end()) {
                logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
																 "Removing the alarm info for Id [%d]", alarmId);
                pEntry = it->second;
            }
            else {
                logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, 
																 "Alarm info for Id [%d] is not avl", alarmId);
            }
            break;
        }
        case CONFIG_OP_TYPE_REPLACE : {
            INGwIfrMgrAlarmInfoEntryMap::iterator it = mAlarmInfoMap.find(alarmId);
            if(it != mAlarmInfoMap.end()) {
                logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
																 "Replacing the alarm info for Id [%d]", alarmId);
                pEntry = it->second;
            }
            else {
                logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, 
																 "Alarm info for Id [%d] is not avl, Adding the info.", alarmId);
                pEntry = new INGwIfrMgrAlarmInfoEntry();
                mAlarmInfoMap[alarmId] = pEntry;
            }
            pEntry->mGappingDuration = gappDuration;
            pEntry = NULL;
            break;
        }
        default : {
            break;
        }
    }
    pthread_mutex_unlock(&mLock);

    if((CONFIG_OP_TYPE_REMOVE == aeOpType) && (NULL != pEntry)) {
        logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, 
												 "Deleting the alarm gapping entry for Id [%d]", alarmId);
        for(int i = 0; i < pEntry->mAlarmInfoCurrCount; i++) {
            INGwIfrMgrAlarmInfo* pInfo = &(pEntry->mAlarmInfo[i]);
            logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, 
														 "Deleted Alarm info is [%d, %s, %d]", alarmId, 
                pInfo->mEntityId, pInfo->mAlarmCountSinceLastAlarmGen);
        }
        delete pEntry;
    }
    return 0;
}

int INGwIfrMgrAlarmMgr::logAlarm(GapPolicy policy, const char* apcFile, int aiLineNo,
                            int aiAlarmId, const char* apcEntityId, 
                            int aiTroubleId, const char* apcFormat, ...)
{
   if(false == mbIsInitialized) 
   {
      logger.logINGwMsg(false, WARNING_FLAG, 0, "Alarm Mgr not initialised");
      return -1;
   }

   va_list va_msg;
   va_start(va_msg, apcFormat);

   if(policy == NON_GAPPED)
   {
      logger.logAlarmArg(aiLineNo, aiAlarmId, aiTroubleId, apcFormat, va_msg);
      va_end(va_msg);
      return 0;
   }

   if(apcEntityId == NULL)
   {
      apcEntityId = "";
   }

   int retVal = 0;
   INGwIfrUtlGuard(&mLock);

   INGwIfrMgrAlarmInfoEntryMap::iterator it = mAlarmInfoMap.find(aiAlarmId);

   if(it == mAlarmInfoMap.end())
   {
      if(policy == GAPPED)
      {
         mAlarmInfoMap[aiAlarmId] = new INGwIfrMgrAlarmInfoEntry();

         it = mAlarmInfoMap.find(aiAlarmId);
      }
   }

   if(it != mAlarmInfoMap.end()) 
   {
      struct timeval currTime;
      gettimeofday(&currTime, NULL);
      unsigned long currTimeInSec = currTime.tv_sec;

      INGwIfrMgrAlarmInfoEntry* pEntry = it->second;
      INGwIfrMgrAlarmInfo* pInfo = NULL;

      for(int idx = 0; idx < pEntry->mAlarmInfoCurrCount; idx++) 
      {
         pInfo = &pEntry->mAlarmInfo[idx];
         int result = strcmp(apcEntityId, pInfo->mEntityId);

         if(0 == result) 
         {
            logger.logINGwMsg(false, TRACE_FLAG, 0, 
                            "Match found id [%ul, %s] at index [%d]", 
                            aiAlarmId, pInfo->mEntityId, idx);
            break;
         }
         else 
         {
            pInfo = NULL;
         }
      }

      if(NULL == pInfo) 
      {
         if(pEntry->mAlarmInfoCurrCount == INIT_ALARM_INFO_ARRAY_SIZE)
         {
            logger.logINGwMsg(false, ERROR_FLAG, 0, 
                            "Max entities [%d] already reached for Alarm [%d]", 
                            INIT_ALARM_INFO_ARRAY_SIZE, aiAlarmId);
            pInfo = &pEntry->mAlarmInfo[0];
         }
         else
         {
            pInfo = &pEntry->mAlarmInfo[pEntry->mAlarmInfoCurrCount++];
            strcpy(pInfo->mEntityId, apcEntityId);
         }
      }

      if(currTimeInSec - pInfo->mTimeStampForLastAlarmSent > 
         pEntry->mGappingDuration) 
      {
         pInfo->mTimeStampForLastAlarmSent = currTimeInSec;
         logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
                         "Raise the subsequent alarm for [%ul, %s] "
                         "gapped [%d] Alarms", 
                         aiAlarmId, pInfo->mEntityId, 
                         pInfo->mAlarmCountSinceLastAlarmGen + 1);

         logger.logAlarmArg(aiLineNo, aiAlarmId, aiTroubleId, 
                            apcFormat, va_msg);
         pInfo->mAlarmCountSinceLastAlarmGen = 0;
      }
      else 
      {
         pInfo->mAlarmCountSinceLastAlarmGen++;
         logger.logINGwMsg(false, TRACE_FLAG, 0, 
                         "Log the alarm [%ul, %s]", aiAlarmId, 
                         pInfo->mEntityId);
         logger.logINGwMsg(false, WARNING_FLAG, aiAlarmId, apcFormat, va_msg);
      }
   }
   else 
   {
      logger.logINGwMsg(false, TRACE_FLAG, 0, "Alarm Id [%ul] not to be gapped", 
                      aiAlarmId);
      logger.logAlarmArg(aiLineNo, aiAlarmId, aiTroubleId, apcFormat, va_msg);
   }

   return 0;
}

list<string>*
INGwIfrMgrAlarmMgr::parseInitInfo(const char* apcInfo)
{
    if(NULL == apcInfo) {
        return NULL;
    }

    char separator = ',';
    list<string>* retList = new list<string>();

    string csvl(apcInfo);

    unsigned long startPos = 0;
    long pos = 0;
    string id;
    while (-1 != pos){
        pos = csvl.find(separator, startPos);
        if(-1 == pos) {
            if(0 == startPos) {
                id = csvl;
            }
            else {
                id = csvl.substr(startPos, csvl.size() - startPos);
            }
        }
        else {
            id = csvl.substr(startPos, pos - startPos);
        }
        retList->push_back(id);
        startPos = pos + 1;
    }

    return retList;
}

int
INGwIfrMgrAlarmMgr::parseConfig(const char* apcInfo, INGwIfrMgrAlarmId& aAlarmId, 
    unsigned long& aDuration)
{
    if(NULL == apcInfo) {
        return -1;
    }

    char separator = '-';
    int retVal = 0;
    string csvl(apcInfo);

    long pos = csvl.find(separator, 0);
    if(-1 != pos) {
        string id = csvl.substr(0, pos);
        aAlarmId = static_cast<INGwIfrMgrAlarmId>(strtol(id.c_str(), NULL, 10));
        string value = csvl.substr(pos + 1, csvl.size() - pos);
        aDuration = static_cast<INGwIfrMgrAlarmId>(strtol(value.c_str(), NULL, 10));
    }
    else {
        logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, 
												 "Invalid config format [%s] != <AlarmId>-<Duration>", apcInfo);
        retVal = -1;
    }

    return retVal;
}

int INGwIfrMgrAlarmMgr::test(void)
{
    const char* ALARM_GAPP_INIT_INFO = "1121-20,1020-90";
    INGwIfrMgrAlarmMgr& alarmMgr = INGwIfrMgrAlarmMgr::getInstance();
    alarmMgr.initialize(ALARM_GAPP_INIT_INFO);
    alarmMgr.logAlarm(CONFIGURATION, __FILE__, __LINE__, 1000, "CHECK",  1223, "Hello world %d", 1);
    alarmMgr.logAlarm(CONFIGURATION, __FILE__, __LINE__, 1121, "TEST",   1223, "Hello world %d", 2);
    alarmMgr.logAlarm(CONFIGURATION, __FILE__, __LINE__, 1121, "RETEST", 1223, "Hello world %d", 3);
    alarmMgr.logAlarm(CONFIGURATION, __FILE__, __LINE__, 1121, "TEST",   1223, "Hello world %d", 4);
    alarmMgr.logAlarm(CONFIGURATION, __FILE__, __LINE__, 1121, "RETEST", 1223, "Hello world %d", 5);
    sleep(30);
    alarmMgr.logAlarm(CONFIGURATION, __FILE__, __LINE__, 1121, "RETEST", 1223, "Hello world %d", 6);
    alarmMgr.configure(ALARM_GAPP_OID, "1121-0", INGwIfrUtlConfigurable::CONFIG_OP_TYPE_REMOVE);
    return 0;
}

// EOF INGwIfrMgrAlarmMgr.C

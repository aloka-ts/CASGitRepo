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
//     File:     INGwIfrPrConfigMgr.C
//
//     Desc:
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     pankaj Bathwal  26/11/07     Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraParamRep");

#include <Util/LogMgr.h>

using namespace std;

#include "Util/imOid.h"

#include "INGwInfraUtil/INGwIfrUtlConfigurable.h"

#include "INGwInfraParamRepository/INGwIfrPrParamRepository.h"
#include "INGwInfraParamRepository/INGwIfrPrConfigMgr.h"

INGwIfrPrConfigMgr* INGwIfrPrConfigMgr::mpSelf = NULL;

INGwIfrPrConfigMgr& INGwIfrPrConfigMgr::getInstance(void)
{
     if(NULL == mpSelf) { mpSelf = new INGwIfrPrConfigMgr(); }
     return *mpSelf;
}

INGwIfrPrConfigMgr::INGwIfrPrConfigMgr(void) : mParamRep(INGwIfrPrParamRepository::getInstance())
{
}

INGwIfrPrConfigMgr::~INGwIfrPrConfigMgr()
{
}

int INGwIfrPrConfigMgr::registerForConfig(const char** apcOIDList, int aiCount, INGwIfrUtlConfigurable* apObj)
{
    int retVal = 0;
    int i = 0;
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "Registering consumer [%x] for [%d] OIDs" , apObj, aiCount);
    while(i < aiCount) {
        logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "Registering %x consumer for OID [%s]", apObj, apcOIDList[i]);
        mOIDRegMap.insert(OIDRegMap::value_type(apcOIDList[i], apObj));
        i++;
    }
    return retVal;
}

int INGwIfrPrConfigMgr::unregisterForConfig(const char** apcOIDList, int aiCount, INGwIfrUtlConfigurable* apObj)
{
    int retVal = 0;
    int i = 0;
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "Un-registering consumer [%x] for [%d] OIDs" , apObj, aiCount);
    while(i < aiCount) {
        pair<OIDRegMap::iterator, OIDRegMap::iterator> result = mOIDRegMap.equal_range(apcOIDList[i]);
        OIDRegMap::iterator it = result.first;
        bool isFound = false;
        while(it != result.second) {
            if(apObj == (*it).second) {
                isFound = true;
                break;
            }        
            it++;
        }

        if(true == isFound) {
            logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "Un-registering %x consumer for OID [%s]", apObj, apcOIDList[i]);
            mOIDRegMap.erase(it);
        }
        else {
            logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, "Unable to Un-registering %x consumer for OID [%s]", apObj, apcOIDList[i]);
        }

        i++;
    }
    return retVal;
}

int INGwIfrPrConfigMgr::configure(const char* apcOID, const char* apcValue, ConfigOpType aeOpType)
{
     int retVal = 0;

     pair<OIDRegMap::iterator, OIDRegMap::iterator> result = mOIDRegMap.equal_range(apcOID);
     OIDRegMap::iterator it = result.first;
     if(it == result.second) {
         logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, "No consumer registered for configuration [%s, %s, %s]", apcOID, apcValue, INGwIfrUtlConfigurable::getString(aeOpType));
         return retVal;
     }

     while(it != result.second)
     {
         INGwIfrUtlConfigurable* pConsumer = (*it).second;
         if(NULL != pConsumer) {
              retVal = pConsumer->configure(apcOID, apcValue, aeOpType);
              logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "Result of configure() on %x consumer for configuration [%s, %s, %s] is %d", pConsumer, apcOID, apcValue, INGwIfrUtlConfigurable::getString(aeOpType), retVal);
         }
         else {
              logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "Consumer ptr is NULL for configuration [%s, %s, %s]", apcOID, apcValue, INGwIfrUtlConfigurable::getString(aeOpType));
         }
         it++;
     }
          
     return retVal;
}

int INGwIfrPrConfigMgr::oidChanged(const char* apcOID, const char* apcValue, ConfigOpType aeOpType, long alSubsystemId)
{
     int retVal = 0;

     pair<OIDRegMap::iterator, OIDRegMap::iterator> result = mOIDRegMap.equal_range(apcOID);
     OIDRegMap::iterator it = result.first;
     if(it == result.second) {
         logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, "No consumer registered for configuration [%s, %s, %s, %d]", apcOID, apcValue, INGwIfrUtlConfigurable::getString(aeOpType), alSubsystemId);
         return retVal;
     }

     while(it != result.second)
     {
         INGwIfrUtlConfigurable* pConsumer = (*it).second;
         if(NULL != pConsumer) {
              retVal = pConsumer->oidChanged(apcOID, apcValue, aeOpType, alSubsystemId);
              logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "Result of oidChanged() on %x consumer for configuration [%s, %s, %s, %d] is %d", pConsumer, apcOID, apcValue, INGwIfrUtlConfigurable::getString(aeOpType), alSubsystemId, retVal);
         }
         else {
              logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "Consumer ptr is NULL for configuration [%s, %s, %s, %d]", apcOID, apcValue, INGwIfrUtlConfigurable::getString(aeOpType), alSubsystemId);
         }
         it++;
     }
         
     return retVal;
}

// EOF INGwIfrPrConfigMgr.C

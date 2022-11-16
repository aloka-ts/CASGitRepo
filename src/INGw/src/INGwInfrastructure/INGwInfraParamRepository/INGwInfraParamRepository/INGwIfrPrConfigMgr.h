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
//     File:     INGwIfrPrConfigMgr.h
//
//     Desc:      
//               
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     pankaj Bathwal  26/11/07     Initial Creation
//********************************************************************

#ifndef _INGW_IFR_PR_CONFIG_MGR_H_
#define _INGW_IFR_PR_CONFIG_MGR_H_

#include <pthread.h>

#include <string>
#include <map>

#include "INGwInfraUtil/INGwIfrUtlConfigurable.h"

class INGwIfrPrParamRepository;

class INGwIfrPrConfigMgr :  public virtual INGwIfrUtlConfigurable
{
    public :

        static INGwIfrPrConfigMgr& getInstance(void);

        virtual ~INGwIfrPrConfigMgr();
        int registerForConfig(const char** apcOIDList, int aiCount, INGwIfrUtlConfigurable* apObj);
        int unregisterForConfig(const char** apcOIDList, int aiCount, INGwIfrUtlConfigurable* apObj);

        virtual int configure(const char* apcOID, const char* apcValue, ConfigOpType aeOpType);
        virtual int oidChanged(const char* apcOID, const char* apcValue, ConfigOpType aeOpType, long alSubsystemId);

    protected :
    
        typedef std::multimap<std::string, INGwIfrUtlConfigurable*> OIDRegMap;

        INGwIfrPrConfigMgr(void);

        static INGwIfrPrConfigMgr*   mpSelf;
        INGwIfrPrParamRepository&    mParamRep;

        pthread_mutex_t       mRegInfoLock;
        OIDRegMap             mOIDRegMap;

    private:

        INGwIfrPrConfigMgr& operator= (const INGwIfrPrConfigMgr& arSelf);
        INGwIfrPrConfigMgr(const INGwIfrPrConfigMgr& arSelf);

};

#endif //_INGW_IFR_PR_CONFIG_MGR_H_

// EOF INGwIfrPrConfigMgr.h

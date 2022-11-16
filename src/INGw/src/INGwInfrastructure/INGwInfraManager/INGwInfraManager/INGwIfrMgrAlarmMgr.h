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
//     File:     INGwIfrMgrAlarmMgr.h
//
//     Desc:
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     pankaj Bathwal  26/11/07     Initial Creation
//********************************************************************

#ifndef _INGW_IFR_MGR_ALARM_MGR_H_
#define _INGW_IFR_MGR_ALARM_MGR_H_

#include <unistd.h>
#include <pthread.h>

#include <string>
#include <list>
#include <map>

#include "INGwInfraUtil/INGwIfrUtlConfigurable.h"
#include "INGwInfraManager/INGwIfrMgrAlarmInfoEntry.h"

class INGwIfrMgrAlarmMgr : public virtual INGwIfrUtlConfigurable
{
   public:

      enum GapPolicy
      {
         GAPPED,
         NON_GAPPED,
         CONFIGURATION
      };

   public:

      typedef unsigned long INGwIfrMgrAlarmId;

      static INGwIfrMgrAlarmMgr& getInstance();

      INGwIfrMgrAlarmMgr();
      virtual ~INGwIfrMgrAlarmMgr();

      int initialize(const char* apcInitInfo = NULL);
      int configure(const char* apcOID, const char* apcValue, 
                    ConfigOpType aeOpType);

      int logAlarm(GapPolicy policy, const char* apcFile, int aiLineNo, 
                   int aiAlarmId, const char* apcEntityId, int aiTroubleId, 
                   const char* apcFormat, ...);

      int test(void);

   protected:

      std::list<std::string>* parseInitInfo(const char* apcInfo);
      int parseConfig(const char* apcInfo, INGwIfrMgrAlarmId& aAlarmId, 
            unsigned long& aDuration);

      typedef std::map<INGwIfrMgrAlarmId, INGwIfrMgrAlarmInfoEntry*> INGwIfrMgrAlarmInfoEntryMap;

      bool                          mbIsInitialized;
      pthread_mutex_t               mLock;
      INGwIfrMgrAlarmInfoEntryMap   mAlarmInfoMap;

      static const char*            mpcOIDsOfInterest[];
      static INGwIfrMgrAlarmMgr*    mpSelf;

   private:

      INGwIfrMgrAlarmMgr(const INGwIfrMgrAlarmMgr& p_Self);
      INGwIfrMgrAlarmMgr& operator=(const INGwIfrMgrAlarmMgr& p_Self);

};

#endif // _INGW_IFR_MGR_ALARM_MGR_H_

// EOF INGwIfrMgrAlarmMgr.h

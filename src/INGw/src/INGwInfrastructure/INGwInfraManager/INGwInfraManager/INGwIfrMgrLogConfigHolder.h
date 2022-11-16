//////////////////////////////////////////////////////////////////////////
//
//        Copyright (c) 2001, Bay Packets Inc.
//        All rights reserved.
//
//        FILE_NAME: BpLogConfigHolder.h
//
//////////////////////////////////////////////////////////////////////////

#ifndef INCLUDE_BpLogConfigHolder
#define INCLUDE_BpLogConfigHolder

#include <sys/types.h>

#include <string>
#include <set>

#include "ccm/BpCCMIncludes.h"
#include "ccm/BpConfigurable.h"

#define SELECTIVE_LOG_SELECTION     "selective-log-selection"
#define SELECTIVE_LOG_CONFIG_FILE   "selective-log-config-file"

#define NUMBER_OF_MATCHING_CRITERION 7
#define MAX_NUMBER_OF_CRITERIA_COUNT 50

#define CALL_ID_OID                 "call-id"
#define ORIGINATING_ADDRESS_OID     "originating-address"
#define DIALED_ADDRESS_OID          "dialed-address"
#define TARGET_ADDRESS_OID          "target-address"
#define REDIRECTING_ADDRESS_OID     "redirecting-address"
#define IBGW_ADDRESS_OID            "ibgw-address"
#define OBGW_ADDRESS_OID            "obgw-address"


class BpLogConfigHolder : public virtual BpConfigurable
{
   public:

      typedef enum 
      {
         CALL_ID = 0,
         ORIGINATING_ADDRESS,
         DIALED_ADDRESS,
         TARGET_ADDRESS,
         REDIRECTING_ADDRESS,
         IBGW_ADDRESS,
         OBGW_ADDRESS
      } BpCriteriaType;

      static const char* getString(BpCriteriaType aeType);

      static BpLogConfigHolder& getInstance(void);
      int initialize(void);

      int getSelectiveLogLevel(void) const;
      bool match(BpLogConfigHolder::BpCriteriaType aeType, 
                 const char* apcValue);

      int configure(const char* apcOID, const char* apcValue, 
                    BpConfigurable::ConfigOpType aeOpType);
      void dumpConfig(const char* apcFileName);

      typedef std::set<std::string> StrSet;
      typedef std::set<std::string>::iterator StrSetIt;

   protected :

      class BpMatchCriteria
      {
         public:

            int            miCriteriaEntryCount;
            BpCriteriaType meCriteriaType;
            mutable pthread_rwlock_t  mRWLock;
            StrSet         mCriteriaEntryArray;

            BpMatchCriteria()
            {
               pthread_rwlock_init(&mRWLock, NULL);
               miCriteriaEntryCount = 0;
            }

            ~BpMatchCriteria()
            {
               pthread_rwlock_destroy(&mRWLock);
            }
      }; 

      BpLogConfigHolder();
      ~BpLogConfigHolder();
      int loadFile(const char* apcFileName);

      bool               miEnableSelection;
      static const char* mpBpCriteriaTypeDesc[];

      BpMatchCriteria mpMatchCriteriaArray[NUMBER_OF_MATCHING_CRITERION];

      static BpLogConfigHolder* mpSelf;

   private :

      static const char * mpcOidsOfInterest[];

      BpLogConfigHolder(const BpLogConfigHolder& aSelf);
      BpLogConfigHolder& operator=(const BpLogConfigHolder& aSelf);
};

#endif 

// EOF BpLogConfigHolder.h

#include <CCMUtil/BpLogger.h>
BPLOG("ccm");
//////////////////////////////////////////////////////////////////////////
//
//        Copyright (c) 2001, Bay Packets Inc.
//        All rights reserved.
//
//        FILE_NAME: BpLogConfigHolder.C
//
//////////////////////////////////////////////////////////////////////////

using namespace std;

#include <ccm/BpParamRepository.h>
#include <ccm/BpLogConfigHolder.h>
#include <ccm/BpConfigMgr.h>
#include <vector>
#include <algorithm>
#include <functional>
#include <iostream>
#include <sstream>
#include <fstream>
#ifdef LINUX
#include <bits/stream_iterator.h>
#endif // LINUX

char COMMENT_MARKER = '#';

const char* BpLogConfigHolder::mpBpCriteriaTypeDesc[] = 
{
   "CALL_ID",
   "ORIGINATING_ADDRESS",
   "DIALED_ADDRESS",
   "TARGET_ADDRESS",
   "REDIRECTING_ADDRESS",
   "IBGW_ADDRESS",
   "OBGW_ADDRESS",
   NULL
};

const char * BpLogConfigHolder::mpcOidsOfInterest[] = 
{
   SELECTIVE_LOG_CONFIG_FILE,
   SELECTIVE_LOG_SELECTION
};

BpLogConfigHolder* BpLogConfigHolder::mpSelf = 0;

const char* BpLogConfigHolder::getString(BpCriteriaType aeType)
{
   if((CALL_ID <= aeType) && (OBGW_ADDRESS >= aeType)) 
   {
      return mpBpCriteriaTypeDesc[aeType];
   }

   return "INVALID_VALUE";
}

BpLogConfigHolder& BpLogConfigHolder::getInstance(void) 
{ 
   if(0 == mpSelf) 
   {
      mpSelf  = new BpLogConfigHolder();
   }

   return *mpSelf; 
}

BpLogConfigHolder::BpLogConfigHolder() :
     miEnableSelection(false)
{ 
   for(int idx = 0; idx < NUMBER_OF_MATCHING_CRITERION; idx++)
   {
      mpMatchCriteriaArray[idx].meCriteriaType = (BpCriteriaType) idx;
   }
}

BpLogConfigHolder::~BpLogConfigHolder()
{ 
   BpConfigMgr::getInstance().unregisterForConfig(mpcOidsOfInterest, 
                         sizeof(mpcOidsOfInterest)/ sizeof(const char *), this);
}

int
BpLogConfigHolder::initialize(void)
{
   LogBpTrace(false, 0, "IN BpLogConfigHolder::initialize");

   int retValue = 0;

   try 
   {
      string filename = 
           BpParamRepository::getInstance().getValue(SELECTIVE_LOG_CONFIG_FILE);

      loadFile(filename.c_str());
   }
   catch(...) 
   {
      logger.logMsg(WARNING_FLAG, imERR_CONFIGURATION, 
                    "No configuartion file specified for selective logging");
   }

   logger.logBpMsg(false, ALWAYS_FLAG, 0, "BpLogConfigHolder initialized.");

   BpConfigMgr::getInstance().registerForConfig(mpcOidsOfInterest, 
                         sizeof(mpcOidsOfInterest)/ sizeof(const char *), this);

   LogBpTrace(false, 0, "OUT BpLogConfigHolder::initialize");
   return retValue;
}

bool BpLogConfigHolder::match(BpCriteriaType aeType, const char* apcValue) 
{
   if(NULL == apcValue) 
   {
      logger.logMsg(ERROR_FLAG, 0, "match(): NULL value passed. [%s]", 
                    getString(aeType));
      return false;
   }

   if(false == miEnableSelection) 
   {
      return false;
   }

   BpMatchCriteria* pMatchCriteria = NULL;

   switch (aeType) 
   {
      case CALL_ID :
      case ORIGINATING_ADDRESS :
      case DIALED_ADDRESS :
      case TARGET_ADDRESS :
      case REDIRECTING_ADDRESS :
      case IBGW_ADDRESS :
      case OBGW_ADDRESS : 
      {
         if(0 != mpMatchCriteriaArray[aeType].miCriteriaEntryCount) 
         {
            pMatchCriteria = mpMatchCriteriaArray + aeType;
         }
         else 
         {
            return false;
         }
      }
      break;

      default : 
      {
         logger.logMsg(ERROR_FLAG, 0, "Invalid type [%s] for value [%s]", 
                       getString(aeType), apcValue);
         return false;
      }
   }

   pthread_rwlock_rdlock(&(pMatchCriteria->mRWLock));

   if(pMatchCriteria->mCriteriaEntryArray.find(apcValue) != 
      pMatchCriteria->mCriteriaEntryArray.end())
   {
      pthread_rwlock_unlock(&(pMatchCriteria->mRWLock));
      return true;
   }

   pthread_rwlock_unlock(&(pMatchCriteria->mRWLock));
   return false;
}

int BpLogConfigHolder::configure(const char* apcOID, const char* apcValue,
                                 BpConfigurable::ConfigOpType aeOpType)
{
   LogBpTrace(false, 0, "IN BpLogConfigHolder::configure");

   if((NULL == apcOID) || (NULL == apcValue)) 
   {
      logger.logMsg(ERROR_FLAG, imERR_CONFIGURATION, 
                    "Received NULL arguments for config change of "
                    "BpLogConfigHolder");
      LogBpTrace(false, 0, "OUT BpLogConfigHolder::configure");
      return -1;
   }

   logger.logMsg(ALWAYS_FLAG, 0, "Received config change for OID [%s] with "
                                 "value [%s] and OpType [%s]", apcOID, apcValue,
                 BpConfigurable::getString(aeOpType));

   if(0 == strcmp(apcOID, SELECTIVE_LOG_SELECTION)) 
   {
      logger.logMsg(ALWAYS_FLAG, 0, "Updating log selection to [%s]", 
                    apcValue);

      if(strcasecmp(apcValue, "true") == 0)
      {
         miEnableSelection = true;
      }
      else
      {
         miEnableSelection = false;
      }

      LogBpTrace(false, 0, "OUT BpLogConfigHolder::configure");
      return 0;
   }

   if(0 == strcmp(apcOID, SELECTIVE_LOG_CONFIG_FILE)) 
   {
      if(aeOpType != CONFIG_OP_TYPE_ADD)
      {
         for(int idx = 0; idx < NUMBER_OF_MATCHING_CRITERION; idx++)
         {
            BpMatchCriteria *pMatchCriteria = mpMatchCriteriaArray + idx;

            pthread_rwlock_wrlock(&(pMatchCriteria->mRWLock));

            pMatchCriteria->mCriteriaEntryArray.clear();
            pMatchCriteria->miCriteriaEntryCount = 
                                     pMatchCriteria->mCriteriaEntryArray.size();

            pthread_rwlock_unlock(&(pMatchCriteria->mRWLock));
         }
      }

      int retValue = loadFile(apcValue);
      LogBpTrace(false, 0, "OUT BpLogConfigHolder::configure");
      return retValue;
   }

   BpMatchCriteria* pMatchCriteria = NULL;

   if(0 == strcmp(apcOID, ORIGINATING_ADDRESS_OID)) 
   {
      pMatchCriteria = &(mpMatchCriteriaArray[ORIGINATING_ADDRESS]);
   }
   else if(0 == strcmp(apcOID, DIALED_ADDRESS_OID)) 
   {
      pMatchCriteria = &(mpMatchCriteriaArray[DIALED_ADDRESS]);
   }
   else if(0 == strcmp(apcOID, TARGET_ADDRESS_OID)) 
   {
      pMatchCriteria = &(mpMatchCriteriaArray[TARGET_ADDRESS]);
   }
   else if(0 == strcmp(apcOID, REDIRECTING_ADDRESS_OID)) 
   {
      pMatchCriteria = &(mpMatchCriteriaArray[REDIRECTING_ADDRESS]);
   }
   else if(0 == strcmp(apcOID, IBGW_ADDRESS_OID)) 
   {
      pMatchCriteria = &(mpMatchCriteriaArray[IBGW_ADDRESS]);
   }
   else if(0 == strcmp(apcOID, OBGW_ADDRESS_OID)) 
   {
      pMatchCriteria = &(mpMatchCriteriaArray[OBGW_ADDRESS]);
   }
   else if(0 == strcmp(apcOID, CALL_ID_OID)) 
   {
      pMatchCriteria = &(mpMatchCriteriaArray[CALL_ID]);
   }
   else 
   {
      logger.logMsg(ERROR_FLAG, imERR_CONFIGURATION, 
                    "Invalid config change for OID [%s] with value [%s]", 
                    apcOID, apcValue);
      LogBpTrace(false, 0, "OUT BpLogConfigHolder::configure");
      return -1;
   }

   if(BpConfigurable::CONFIG_OP_TYPE_ADD == aeOpType) 
   {
      pthread_rwlock_wrlock(&(pMatchCriteria->mRWLock));

      if(MAX_NUMBER_OF_CRITERIA_COUNT > pMatchCriteria->miCriteriaEntryCount) 
      {
         pMatchCriteria->mCriteriaEntryArray.insert(apcValue);
         pMatchCriteria->miCriteriaEntryCount = 
                                     pMatchCriteria->mCriteriaEntryArray.size();

         logger.logMsg(ALWAYS_FLAG, 0, "Added new entry [%s] for type [%s] "
                                       "count [%d]", apcValue, 
                       getString(pMatchCriteria->meCriteriaType), 
                       pMatchCriteria->miCriteriaEntryCount);
      }
      else 
      {
         logger.logMsg(ALWAYS_FLAG, 0, "Max Count already reached. skipping "
                                       "value [%s]. [%s]", apcValue, 
                       getString(pMatchCriteria->meCriteriaType));
      }

      pthread_rwlock_unlock(&(pMatchCriteria->mRWLock));

      LogBpTrace(false, 0, "OUT BpLogConfigHolder::configure");
      return 0;
   }

   if(BpConfigurable::CONFIG_OP_TYPE_REMOVE == aeOpType) 
   {
      pthread_rwlock_wrlock(&(pMatchCriteria->mRWLock));

      pMatchCriteria->mCriteriaEntryArray.erase(apcValue);
      pMatchCriteria->miCriteriaEntryCount = 
                                     pMatchCriteria->mCriteriaEntryArray.size();

      logger.logMsg(ALWAYS_FLAG, 0, "Removed entry [%s] for type [%s] "
                                    "count [%d]", apcValue, 
                    getString(pMatchCriteria->meCriteriaType), 
                    pMatchCriteria->miCriteriaEntryCount);

      pthread_rwlock_unlock(&(pMatchCriteria->mRWLock));

      LogBpTrace(false, 0, "OUT BpLogConfigHolder::configure");
      return 0;
   }

   if(BpConfigurable::CONFIG_OP_TYPE_REPLACE == aeOpType) 
   {
      pthread_rwlock_wrlock(&(pMatchCriteria->mRWLock));

      pMatchCriteria->mCriteriaEntryArray.clear();
      pMatchCriteria->mCriteriaEntryArray.insert(apcValue);
      pMatchCriteria->miCriteriaEntryCount = 
                                     pMatchCriteria->mCriteriaEntryArray.size();

      logger.logMsg(ALWAYS_FLAG, 0, "Replaced entry [%s] for type [%s] "
                                    "count [%d]", apcValue, 
                    getString(pMatchCriteria->meCriteriaType), 
                    pMatchCriteria->miCriteriaEntryCount);
      pthread_rwlock_unlock(&(pMatchCriteria->mRWLock));

      LogBpTrace(false, 0, "OUT BpLogConfigHolder::configure");
      return 0;
   }

   logger.logMsg(ERROR_FLAG, imERR_CONFIGURATION, "Unknown operation type [%s]",
                 BpConfigurable::getString(aeOpType));

   return -1;
}

int BpLogConfigHolder::loadFile(const char* apcFileName)
{
   typedef vector<string> StrVector;
   typedef StrVector::iterator StrVectorIt;

   LogBpTrace(false, 0, "IN BpLogConfigHolder::loadFile")

   logger.logMsg(VERBOSE_FLAG, 0, "Loading selective-log info from [%s]", 
                 apcFileName);

   int retValue = -1;

   ifstream inFile(apcFileName);

   if(!inFile)
   {
      logger.logMsg(ERROR_FLAG, imERR_CONFIGURATION, 
                    "Unable to open file [%s] for loading selective-log data", 
                    apcFileName);
      LogBpTrace(false, 0, "OUT BpLogConfigHolder::loadFile")
      return retValue;
   }

   StrVector input;
   copy(istream_iterator<string>(inFile), 
        istream_iterator<string>(), 
        back_inserter(input));

   int lineCount = 1;
   for(StrVectorIt it = input.begin(); it != input.end(); it++, lineCount++) 
   {
      string line = (*it);

      if(COMMENT_MARKER == line[0]) 
      {
         continue;
      }

      unsigned long pos = line.find("$");
      if(pos == -1)
      {
         logger.logBpMsg(false, ALWAYS_FLAG, 0,
                         "Problem at line [%d] file [%s] [%s]", lineCount, 
                         apcFileName, line.c_str());
         LogBpTrace(false, 0, "OUT BpLogConfigHolder::loadFile")
         return -1;
      }

      string id = line.substr(0, pos);
      string id_rhs = line.substr(pos + 1);

      logger.logBpMsg(false, ALWAYS_FLAG, 0, "line [%s] id [%s] id_rsh [%s]",
                      line.c_str(), id.c_str(), id_rhs.c_str());

      pos = id_rhs.find("$");
      if(pos == -1)
      {
         logger.logBpMsg(false, ALWAYS_FLAG, 0, 
                         "Problem at line [%d] file [%s] [%s] ", lineCount, 
                         apcFileName, id_rhs.c_str());
         LogBpTrace(false, 0, "OUT BpLogConfigHolder::loadFile")
         return -1;
      }

      string value = id_rhs.substr(0, pos);
      string opStr = id_rhs.substr(pos + 1);
      ConfigOpType opType = (ConfigOpType) atoi(opStr.c_str());

      int result = configure(id.c_str(), value.c_str(), opType);

      if(result == -1)
      {
         logger.logMsg(ALWAYS_FLAG, 0, "Error loading line [%d] file [%s] [%s]",
                       lineCount, apcFileName, line.c_str());
         continue;
      }
   }

   logger.logMsg(VERBOSE_FLAG, 0, "Successfully processed file [%s]", 
                 apcFileName);

   LogBpTrace(false, 0, "OUT BpLogConfigHolder::loadFile")
   return 0;
}

void
BpLogConfigHolder::dumpConfig(const char* apcFileName)
{
   stringstream output;
   BpMatchCriteria* pMatchCriteria = NULL;

   output << "#################################"
             "#################################" << endl;

   output << "Selective Logging feature status : " << miEnableSelection << endl;

   for(int idx = 0; idx < NUMBER_OF_MATCHING_CRITERION; idx++) 
   {
      pMatchCriteria = &(mpMatchCriteriaArray[idx]);

      StrSet& strList = pMatchCriteria->mCriteriaEntryArray;
      pthread_rwlock_rdlock(&(pMatchCriteria->mRWLock));
      output << "Selective Log config data for type : " 
             << getString((BpCriteriaType) idx) 
             << endl;
      copy(strList.begin(), strList.end(), 
           ostream_iterator<string>(output, " ")); output << endl;
      pthread_rwlock_unlock(&(pMatchCriteria->mRWLock));
   }

   output << "#################################"
             "#################################" << endl;

   if(NULL != apcFileName) 
   {
      ofstream outFile(apcFileName);
      if(!outFile)
      {
         logger.logMsg(ERROR_FLAG, imERR_CONFIGURATION, 
                       "Unable to open file [%s], dumping on stdout", 
                       apcFileName);
         cout << output.str();
      }
      else 
      {
         outFile << output.str();
      }
   }
   else 
   {
      cout << output.str();
   }
}

// EOF BpLogConfigHolder.C

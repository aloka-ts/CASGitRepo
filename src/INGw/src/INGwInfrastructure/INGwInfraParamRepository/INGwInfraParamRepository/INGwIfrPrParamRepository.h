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
//     File:     INGwIfrPrParamRepository.h
//
//     Desc:     This class represents repository for all the configuration parameters for INGw
//               Provided function to get and set configuration data
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     pankaj Bathwal  26/11/07     Initial Creation
//********************************************************************

#ifndef _INGW_IFR_PR_PARAM_REPOSITORY_H_
#define _INGW_IFR_PR_PARAM_REPOSITORY_H_

#include <pthread.h>
#include <stdlib.h>

#include <map>
#include <list>
#include <string>
#include <vector>

// Keys for the parameters
#define ingwDEBUG_PORT                      "ingwDEBUG_PORT"
#define ingwDEBUG_WAIT_FOR_DEBUGGER_IN_MSEC "ingwDEBUG_WAIT_FOR_DEBUGGER_IN_MSEC"
#define ingwDEBUG_CHECK_ALLOC_SIZE_IN_BYTES "ingwDEBUG_CHECK_ALLOC_SIZE_IN_BYTES"
#define ingwDEBUG_DIR_PATH_FOR_PERF_DAT     "ingwDEBUG_DIR_PATH_FOR_PERF_DAT"
#define ingwDEBUG_USE_MEM_POOL              "ingwDEBUG_USE_MEM_POOL"
#define ingwWORKER_Q_SIZE_LIMIT             "ingwWORKER_Q_SIZE_LIMIT"
#define ingwIVR_REPLICATION                 "ingwIVR_REPLICATION"
#define sipSIPIVR_RECFILE_COUNTER_FILE      "sipSIPIVR_RECFILE_COUNTER_FILE"

#define ingwSTANDBY_NUM                     "CCM_STANDBY_NUM"
#define ingwSHUTDOWN_INTVL_SECS             "CCM_SHUTDOWN_INTVL"

#define ingwPEER_REPLICATION_DELAY_MSEC     "CCM_PEER_REPLICATION_DELAY_MSEC"
#define ingwPEER_REPLICATION_BURST_PER_SEC  "CCM_PEER_REPLICATION_BURST_PER_SEC"

#define ingwPEER_CONNECTED "PEER_CONNECTED_DUMMY_OID"

#include <INGwInfraParamRepository/INGwIfrPrIncludes.h>
#include <INGwInfraParamRepository/INGwIfrPrParamOid.h>
#include <INGwInfraUtil/INGwIfrUtlLock.h>
#include <INGwInfraUtil/INGwIfrUtlReject.h>


/** This class is a repository for all the configuration and performance
 *  parameters for INGW.
 */
class INGwIfrPrParamRepository
{
    public:

        /** This method gets the ref to the singleton INGwIfrPrParamRepository
         */
        static INGwIfrPrParamRepository&
        getInstance(void);

        /** This method converts the CSVL to a list of long
         */
        static std::list<long>*
        convertCSVLToLongList(const char* apcCSVL);

        static std::list<std::string>*
        convertCSVLToStrList(const char* apcCSVL);

        /** This method initializes the parameter repository
         */
        void
        initialize(int aiArgc, const char** acArgv)
        throw (INGwIfrUtlReject);

        /** This method gets the self Id for the subsystem
         */
        inline long getSelfId(void) const;

        /** This method gets the peer Id for the subsystem
         */
        inline long getPeerId(void) const;

        /** This method gets the stringified self Id for the subsystem
         */
        inline const std::string& getSelfIdStr(void) const;

        /** This method gets the stringified IP address (fixed) for the subsystem
         */
        inline const std::string& getSelfIPAddr(void) const;

        /** This method gets the stringified peer Id for the subsystem
         */
        inline const std::string& getPeerIdStr(void) const;

        /** This method gets the stringified IP address (fixed) for the subsystem
         */
        inline const std::string& getPeerIPAddr(void) const;

        /** This method gets the INGW agent port number
         */
        inline long getAgentPort(void) const;

        /** This method gets the value for the INGW debug level for
         *  message exchanges
         */
        inline int getMsgDebugLevel(void) const;
        void  setMsgDebugLevel(int aiMsgDebugLevel);

        inline int getPeerStatus(char *triggerPoint = NULL);
        inline int getPeerStartTime();
        inline void setPeerStatus(int status, int startTime, 
                                  char *triggerPoint = NULL);

	      inline int getHBTimeoutInMsec()
	      {
		      return mHBTimeout;
	      }

	      inline int getHBTimeoutCount()
	      {
		      return mHBTimeoutCount;
	      }

        /** This method gets the value for the parameter OID
         */
        std::string
        getValue(const std::string& aOID) const
        throw (INGwIfrUtlReject);

        int
        getValue(const std::string& aOID, std::string& arValue) const;

        /** This method sets the value for the parameter OID
         */
        void
        setValue(const std::string& aOID, const std::string& aValue);

        std::string getSubsysName(int subsystemID);

        std::string toLog();

        CCMOperationMode getOperationMode();
        void setOperationMode(CCMOperationMode operationMode);

        int getProcessingState();
        void setProcessingState(int aistate);

        void setPeerIP(const char *val);

				std::vector<int> getINGwList();

				std::list<std::string>* getSasFipList();

				bool isHostPresentInSasList(std::string p_HostIP);

    protected :

        int                  _processingState;
        CCMOperationMode _operationMode;

        typedef std::list<std::string> StrList;
        typedef StrList::iterator StrListIt;
        typedef StrList::const_iterator StrListCIt;
        typedef std::map<std::string, std::string> StrStrMap;
        typedef StrStrMap::iterator StrStrMapIt;
        typedef StrStrMap::const_iterator StrStrMapCIt;

        typedef std::map<int, std::string> SubsysNameMap;
        typedef SubsysNameMap::iterator SubsysNameMapIt;
        typedef SubsysNameMap::const_iterator SubsysNameMapCIt;

        std::string getIPAddr(long alSubsystemId) throw (INGwIfrUtlReject);
        void getEnVar(const char* apcParam, const char* apcDefValue, char* apcOID = NULL);
        void loadFile(const std::string& aFileName) throw (INGwIfrUtlReject);
				void loadDefaultParamValues();

        INGwIfrPrParamRepository();
        ~INGwIfrPrParamRepository();

        long                      mlSelfId;
        long                      mlPeerId;
        std::string               mSelfIdStr;
        std::string               mSelfIPAddr;
        std::string               mPeerIdStr;
        std::string               mPeerIPAddr;

        long                      mlAgentPort;
        int                       miMsgDebugLevel;

        int                       miSoleConnTimerVal;

        mutable pthread_rwlock_t  mRWLock;

        StrStrMap                 mConfigMap;
        SubsysNameMap             mNameMap;

        static INGwIfrPrParamRepository* mpSelf;

        int                       mPeerStatus;
        int                       mPeerStartTime;
				std::vector<int>					mINGwIdList;
				std::list<std::string>*		mSasFipList;

				int       mHBTimeout;
				int       mHBTimeoutCount;

   private:

      int  eventStoreHeldInMSec;
      bool eventStoreSleeFTReplay;

   public:

      int  getEventStoreHeldInMSec()
      {
         return eventStoreHeldInMSec;
      }

      bool isEventStoreSleeFTReplay()
      {
         return eventStoreSleeFTReplay;
      }

    private :

        /** Assignment operator (Not implemented)
         */
        INGwIfrPrParamRepository(const INGwIfrPrParamRepository& aSelf);

        /** Copy constructor (Not implemented)
         */
        INGwIfrPrParamRepository&
        operator=(const INGwIfrPrParamRepository& aSelf);

};

#define sipIVR_REC_FILE_PATH         "sipIVR_REC_FILE_PATH"
#define sipIVR_ANN_SET_PATH          "sipIVR_ANN_SET_PATH"

// These are environment variables and are installation specific.
#define FROMINFO_ADDR                "FROMINFO_ADDR"
#define FROMINFO_PORT                "FROMINFO_PORT"
#define CONTACTINFO_ADDR             "CONTACTINFO_ADDR"
#define CONTACTINFO_PORT             "CONTACTINFO_PORT"
#define SIPURL_TRANSLATION_MODE      "SIPURL_TRANSLATION_MODE"

#define sipISSDPPARSED                  "sipISSDPPARSED"

#define sipHOLD_SIP_MSG                 "sipHOLD_SIP_MSG"
#define sipPSX_ALARM_INTERVAL_IN_SECS      "sipPSX_ALARM_INTERVAL_IN_SECS"
#define sipANCHOR_CONNECTED_EVENT       "sipANCHOR_CONNECTED_EVENT"
#define sipCHECK_ST_REINVITE            "sipCHECK_ST_REINVITE"
#define sipCONVEDIA_ENABLED             "sipCONVEDIA_ENABLED"
#define sipVXML_REQ_URI_FORMAT          "sipVXML_REQ_URI_FORMAT"
#define sipVXML_URL_SLASH_REPLACEMENT   "sipVXML_URL_SLASH_REPLACEMENT"
#define sipTIMER_T1_IN_MSECS            "sipTIMER_T1_IN_MSECS"
#define sipTIMER_T2_IN_MSECS            "sipTIMER_T2_IN_MSECS"
#define sipMAX_RETRAN                   "sipMAX_RETRAN"
#define sipMAX_INV_RETRAN               "sipMAX_INV_RETRAN"

#define MAX_STATPARAM_COUNT          "MAX_STATPARAM_COUNT"
#define STAT_DEFERREDTHREAD_DURATION "STAT_DEFERREDTHREAD_DURATION"
#define STAT_CONFIG_FILE             "STAT_CONFIG_FILE"
#define STAT_LEVEL                   "STAT_LEVEL"

#define CCM_MAX_COMPOSITE_MSG_SIZE   "CCM_MAX_COMPOSITE_MSG_SIZE"

#define SIP_OOD_FAILURE              "SIP_OOD_FAILURE"

//PANKAJ To be removed later
//Defined for compilation



#include "INGwInfraParamRepository/INGwIfrPrParamRepository.icc"

#endif // _INGW_IFR_PR_PARAM_REPOSITORY_H_

// EOF INGwIfrPrParamRepository.h

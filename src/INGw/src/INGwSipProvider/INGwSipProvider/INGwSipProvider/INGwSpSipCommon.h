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
//     File:     INGwSpSipCommon.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef INGW_SP_SIP_COMMON_H_
#define INGW_SP_SIP_COMMON_H_

#include <strings.h>
#include <string>
#include <iostream.h>
#include <sstream>
#include <map>
#include <list>
#include <pthread.h>
#include <Util/Logger.h>

#include <INGwSipProvider/INGwSpSipIncludes.h>

#define MAX_SIPBUF_SIZE 4096
#define MAX_SIPCID_SIZE 150

#define FAIL_SLEE_ACK 1
#define SUCCESS_SLEE_ACK 0

#define SIP_PROVIDER "sip"

#define OP_FAIL   -1
#define OP_SUCCESS 0

#define FROM_SERVICE 1
#define FROM_SELF    2

#define OP_FAIL_EVENT    -1
#define OP_SUCCESS_EVENT  0 

#define DEFAULT_LEAK_TIME 25
#define DEFAULT_LEAK_FAILURE_COUNT 8
#define DEFAULT_PSX_ALARM_INTERVAL 120

typedef enum INGwSipHandlerType
{
  UNKNOWN_HANDLER   ,
  INVITE_HANDLER    ,
  REINVITE_HANDLER  ,
  PRACK_HANDLER     ,
  BYE_HANDLER       ,
  INFO_HANDLER      ,
  CANCEL_HANDLER    ,
  REFER_HANDLER     ,
  NOTIFY_HANDLER		,
  REGISTER_HANDLER  ,
  UPDATE_HANDLER
};

typedef enum
{
  LASTOP_UNKNOWN,
  LASTOP_CONNECT,
  LASTOP_DIALOUT,
  LASTOP_HOLD,
  LASTOP_RESYNCH,
  LASTOP_PASSTHROUGH_REINVITE,
  LASTOP_MSERV_INFO,
  LASTOP_PASSTHROUGH_INFO,
  LASTOP_SESSION_REFRESH_REINVITE
} LastOperation;

typedef enum
{
  FAILCAUSE_NONE   ,
  FAILCAUSE_TIMEOUT,
  FAILCAUSE_CANCEL ,
  FAILCAUSE_ERRRESP
} FailureCause;

#define MAX_EP_NUMBER_LENGTH 31
#define MAX_EP_HOST_LENGTH   31
#define MAX_HEADER_LEN 1023
#define SIZE_OF_IPADDR 15

struct INGWSipEPTimer
{
	INGWSipEPTimer(){
		m_TimerId = 0;
		m_ThreadId = 0;
  }

  INGWSipEPTimer(const INGWSipEPTimer& p_FromObj) {
		m_TimerId = p_FromObj.m_TimerId;
		m_ThreadId = p_FromObj.m_ThreadId;
  }

  INGWSipEPTimer& operator = (const INGWSipEPTimer& p_FromObj) {
		m_TimerId = p_FromObj.m_TimerId;
		m_ThreadId = p_FromObj.m_ThreadId;
		return *this;
  }

	unsigned int m_TimerId;
	int m_ThreadId;
};

struct INGwSipEPInfo
{
	INGwSipEPInfo(){ 
		bzero(mEpNumber, (MAX_EP_NUMBER_LENGTH + 1) );
		bzero(mEpHost, (MAX_EP_HOST_LENGTH + 1) );
		bzero(mContactHdr, (MAX_HEADER_LEN + 1) );
		port = 0; 
		clientport = 0; 
		mContactAddSpec = NULL;
		mEPTimer = NULL;
	}

	char mEpNumber[MAX_EP_NUMBER_LENGTH + 1];
	char mEpHost  [MAX_EP_HOST_LENGTH   + 1];
	char mContactHdr[MAX_HEADER_LEN + 1];
	unsigned short port; // Remote listener port
	unsigned short clientport; // current active port
	unsigned short initalClientPort; // initial local port of remote party

	std::string msLocalCallID;
	INGWSipEPTimer* mEPTimer;
	SipAddrSpec* mContactAddSpec;
	SipAddrSpec* mFromAddSpec;

	// Overloaded '=' operator 
	INGwSipEPInfo& operator = (const INGwSipEPInfo& fromObj) {
		strncpy(mEpNumber, fromObj.mEpNumber, (MAX_EP_NUMBER_LENGTH ) );
		strncpy(mEpHost, fromObj.mEpHost, (MAX_EP_HOST_LENGTH ) );
		strncpy(mContactHdr, fromObj.mContactHdr, (MAX_HEADER_LEN ) );
    msLocalCallID = fromObj.msLocalCallID;
		port = fromObj.port;
		clientport = fromObj.clientport;
		initalClientPort = fromObj.initalClientPort;
		mEPTimer = fromObj.mEPTimer;
		mContactAddSpec = fromObj.mContactAddSpec;
		mFromAddSpec = fromObj.mFromAddSpec;

		return *this;
	}

	// Copy Constructor
	INGwSipEPInfo( const INGwSipEPInfo& fromObj) : port(fromObj.port) {
		strncpy(mEpNumber, fromObj.mEpNumber, (MAX_EP_NUMBER_LENGTH ) );
		strncpy(mEpHost, fromObj.mEpHost, (MAX_EP_HOST_LENGTH ) );
		strncpy(mContactHdr, fromObj.mContactHdr, (MAX_HEADER_LEN ) );
    msLocalCallID = fromObj.msLocalCallID;
		mEPTimer = fromObj.mEPTimer;
		clientport = fromObj.clientport;
		initalClientPort = fromObj.initalClientPort;
		mContactAddSpec = fromObj.mContactAddSpec;
		mFromAddSpec = fromObj.mFromAddSpec;
	}
};

struct INGwSipHBTimerContext
{
	INGwSipHBTimerContext(){ 
		bzero(mEpHost, (MAX_EP_HOST_LENGTH + 1) );
		mTimeoutCount = 0; 
	}

	char mEpHost  [MAX_EP_HOST_LENGTH   + 1];
	int mTimeoutCount;

	// Overloaded '=' operator 
	INGwSipHBTimerContext& operator = (const INGwSipHBTimerContext& fromObj) {
		strncpy(mEpHost, fromObj.mEpHost, (MAX_EP_HOST_LENGTH ) );
		mTimeoutCount = fromObj.mTimeoutCount;
		return *this;
	}
	// Copy Constructor
	INGwSipHBTimerContext( const INGwSipHBTimerContext& fromObj) {
		strncpy(mEpHost, fromObj.mEpHost, (MAX_EP_HOST_LENGTH ) );
		mTimeoutCount = fromObj.mTimeoutCount;
	}
};

static const char* BP_CPC_TAG         = "cpc";
static const char* BP_OLI_TAG         = "oli";
static const char* BP_OLI_TAG_1       = "isup-oli";
// BPUsa07879 : [
static const char* BP_NP_TAG		      = "np";
// : ]
// BPUsa07888 : [
static const char* strAPPLICATION_SDP = "application/sdp";
static const char* strAPPLICATION_ISUP = "application/isup"; 
static const char* strAPPLICATION_GTD = "application/gtd"; 
static const char* strMULTIPART_MIXED = "multipart/mixed";
static const char* strBOUNDARY = "boundary";
// : ]
static const char* BP_LRN_TAG         = "rn";
static const char* BP_CIC_TAG         = "cic";
static const char* BP_NPDI_TAG        = "npdi";
static const char* BP_BGT_PRIVATE_VAL = "private";
static const char* BP_BGT_PUBLIC_VAL  = "public";
static const char* PRIVATE_BGT        = "0";
static const char* UNKNOWN_BGT        = "2";
static const char* PUBLIC_BGT         = "1";
static const char* BP_BGID_TAG        = "bgid";
static const char* BP_BGT_TAG         = "bgt";
static const char* PHONE_CONTEXT_TAG  = "phone-context";

static char* BP_REFER           = (char*)"REFER";
static char* BP_NOTIFY          = (char*)"NOTIFY";

#define OLI_CPC_PAYPHONE_TAG          "payphone"
#define OLI_CPC_PAYPHONE              27
#define OLI_CPC_OTHER                 0

#define IS_RESPONSE true

#define REMOTE_RETRANS_PURGE_INTERVAL 60000

typedef enum
{
  FTSCENE_CALL_ESTABLISHMENT
} FtSerializationScenario;

#define MARK_BLOCK(x, y) LogTrace(0, x);

typedef enum OfferAnswerState_t {
	OA_NONE,
	OA_OFFER,
	OA_ANSWER
} ;

typedef enum
{
  INGW_METHOD_TYPE_NONE         ,
  INGW_SIP_METHOD_TYPE_INVITE   ,
  INGW_SIP_METHOD_TYPE_REINVITE ,
  INGW_SIP_METHOD_TYPE_ACK      ,
  INGW_SIP_METHOD_TYPE_CANCEL   ,
  INGW_SIP_METHOD_TYPE_BYE      ,
  INGW_SIP_METHOD_TYPE_OPTIONS  ,
  INGW_SIP_METHOD_TYPE_NOTIFY   ,
  INGW_SIP_METHOD_TYPE_REFER    ,
  INGW_SIP_METHOD_TYPE_INFO     ,
  INGW_SIP_METHOD_TYPE_PRACK    ,
  INGW_SIP_METHOD_TYPE_UNKNOWN  ,
  INGW_METHOD_TYPE_PARALLELTRANS,
  INGW_SIP_METHOD_TYPE_REGISTER ,
  INGW_SIP_METHOD_TYPE_UPDATE 
} INGwSipMethodType;

static const char *glbMethodName[] = 
{
  "NONE", 
  "INVITE", 
  "INVITE", 
  "ACK", 
  "CANCEL", 
  "BYE", 
  "OPTIONS", 
  "NOTIFY", 
  "REFER", 
  "INFO", 
  "PRACK", 
  "UNKNOWN", 
  "NONE",
  "REGISTER",
  "UPDATE"
};

typedef std::list<INGwSipEPInfo*> t_EPInfoList;
typedef t_EPInfoList::iterator t_EPInfoListItr;

class SipEPInfoMap
{
  public :

    typedef std::map<std::string, INGwSipEPInfo*> t_EPInfoMap;
    typedef t_EPInfoMap::iterator t_EPInfoMapItr;

    SipEPInfoMap()
    {
      pthread_rwlock_init(&m_EPInfoMapLock, 0);
    }
    ~SipEPInfoMap()
    {
      pthread_rwlock_destroy(&m_EPInfoMapLock);
    }

    int addEP(const INGwSipEPInfo& p_SipEPInfo)
    {
      int ret = 0;
      std::string lKey = std::string(p_SipEPInfo.mEpHost);
      pthread_rwlock_wrlock(&m_EPInfoMapLock);

      t_EPInfoMapItr iter = m_EPInfoMap.find(lKey);
      if(iter != m_EPInfoMap.end())
      {
        ret = -1;
      }
      else
      {
        INGwSipEPInfo* sipEP = new INGwSipEPInfo();
        *sipEP = p_SipEPInfo;

        m_EPInfoMap[lKey] = sipEP;
        ret = 0;
      }

      pthread_rwlock_unlock(&m_EPInfoMapLock);

      return ret;
    }

    int getEP(std::string& p_KeyHost, INGwSipEPInfo& p_SipEPInfo)
    {
      int ret = 0;
      pthread_rwlock_rdlock(&m_EPInfoMapLock);

      t_EPInfoMapItr iter = m_EPInfoMap.find(p_KeyHost);
      if(iter != m_EPInfoMap.end())
      {
        p_SipEPInfo = *(m_EPInfoMap[p_KeyHost]);
        ret = 0;
      }
      else
      {
        ret = -1;
      }

      pthread_rwlock_unlock(&m_EPInfoMapLock);

      return ret;
    }

    int updateEP(std::string& p_KeyHost, unsigned short p_ClienPort)
    {
      int ret = 0;
      pthread_rwlock_wrlock(&m_EPInfoMapLock);

      t_EPInfoMapItr iter = m_EPInfoMap.find(p_KeyHost);
      if(iter != m_EPInfoMap.end())
      {
        (m_EPInfoMap[p_KeyHost])->clientport = p_ClienPort;
        ret = 0;
      }
      else
      {
        ret = -1;
      }

      pthread_rwlock_unlock(&m_EPInfoMapLock);

      return ret;
    }

    int removeEP(std::string& p_KeyHost, std::string& p_CallId)
    {
      int ret = 0;
      pthread_rwlock_wrlock(&m_EPInfoMapLock);

      t_EPInfoMapItr iter = m_EPInfoMap.find(p_KeyHost);
      if(iter != m_EPInfoMap.end())
      {
				INGwSipEPInfo* lEpInfo = iter->second;
				if(lEpInfo->msLocalCallID == p_CallId)
				{
          m_EPInfoMap.erase(iter);
          ret = 0;
				}
				else
				{
          ret = -1;
				}
      }
      else
      {
        ret = -1;
      }

      pthread_rwlock_unlock(&m_EPInfoMapLock);

      return ret;
    }

		std::string toLog()
		{
			pthread_rwlock_wrlock(&m_EPInfoMapLock);
			std::ostringstream strStream;
			int lCount = m_EPInfoMap.size();
			if(lCount == 0)
			{
        strStream << "There are no SAS end point registered" << std::endl;
				pthread_rwlock_unlock(&m_EPInfoMapLock);
				return strStream.str();
			}
			strStream << "There are " << lCount << " SAS end point registered" << std::endl;
			int lNum = 1;
			t_EPInfoMapItr iter = m_EPInfoMap.begin();

			for(; iter !=  m_EPInfoMap.end(); iter++)
			{
				INGwSipEPInfo* lEpInfo = iter->second;
				strStream << "----SAS End Point " << lNum++ << " ---"<<std::endl;
				strStream	<< " IP : " << lEpInfo->mEpHost << " Port : " 
									<< lEpInfo->port << " Call Id Recvd : "
									<< lEpInfo->msLocalCallID << std::endl;
			}
			pthread_rwlock_unlock(&m_EPInfoMapLock);
			return strStream.str();

		}


    int getEPList(t_EPInfoList& p_EPInfoList)
    {
      int ret = 0;
      pthread_rwlock_rdlock(&m_EPInfoMapLock);

			t_EPInfoMapItr iter = m_EPInfoMap.begin();

			for(; iter !=  m_EPInfoMap.end(); iter++)
			{
				INGwSipEPInfo* lEpInfo = iter->second;
        INGwSipEPInfo* lRetEpInfo = new INGwSipEPInfo(*lEpInfo);
        p_EPInfoList.push_back(lRetEpInfo);
        ++ret;
      }

      pthread_rwlock_unlock(&m_EPInfoMapLock);

      return ret;
    }

  private :
    pthread_rwlock_t m_EPInfoMapLock;
    t_EPInfoMap m_EPInfoMap;

};


#endif //INGW_SP_SIP_COMMON_H_

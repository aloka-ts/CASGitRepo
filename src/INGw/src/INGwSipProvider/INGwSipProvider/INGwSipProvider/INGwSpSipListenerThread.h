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
//     File:     INGwSpSipListenerThread.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef INGW_SP_SIP_LISTENER_THREAD_H_
#define INGW_SP_SIP_LISTENER_THREAD_H_

#define MAX_HOST_LEN 20
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#include <INGwInfraUtil/INGwIfrUtlThread.h>
#include <INGwInfraUtil/INGwIfrUtlStrStr.h>
#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpSipContext.h>
#include <string>
#include <time.h>

typedef struct timeval timeStamp;
using namespace std;
enum SipTranportType
{
  SipTranportType_UDP,
  SipTranportType_TCP
};

class INGwSpSipListenerThread : public INGwIfrUtlThread
{
   public:

      INGwSpSipListenerThread();
      ~INGwSpSipListenerThread();

      bool initialize(int aPort, char *aHost);
      void proxyRun(void);

      void setStackDebugLevel(bool aDebug);

      void postNetworkMsg(INGwSipTranspInfo *transportinfo,
                          char* aBuffRecvd, int aLen);

      static bool analyzeCallId(const char* callId, unsigned long& hash);
      static bool getModifyTimeForEp(string pSasIp, timeStamp& pTstamp);
      void setModifyTimeForEp(string pSasIp, timeStamp pTstamp);
      int getThreadIdIdx(char* apcCallId);

#ifdef CAPTURE_TS
      static void logTsArray();
#endif

   private:

      bool _getCallIdFromBuffer(const char *msg, int len, char *callID);
      bool _getDlgIdFromBuffer(const char *msg, int len, char *callID);
      bool mReplayFlg;
      static pthread_rwlock_t         mEpTsMapRWLock;
      static map<string , timeStamp > mEpTsMap;
      INGwIfrUtlStrStr _callIdStrStr;
      INGwIfrUtlStrStr _callIdStrStrShort;
      INGwIfrUtlStrStr _dialogueIdStr;
      int extractDlgId(char * pBuffRecvd);

      int mPort;
      char mHost[MAX_HOST_LEN];
      struct sockaddr_in mCliaddr;
      int mSockfd;
      bool mbStackDebug;
      SipTranportType mTranportType;
      bool  mSupOptLog;
}; // end of class INGwSpSipListenerThread

#endif //INGW_SP_SIP_LISTENER_THREAD_H_

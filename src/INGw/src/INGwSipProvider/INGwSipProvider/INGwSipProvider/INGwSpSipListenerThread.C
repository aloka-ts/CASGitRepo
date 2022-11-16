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
//     File:     INGwSpSipListenerThread.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipProvider");

#include <stdio.h>
#include <errno.h>
#include <INGwSipProvider/INGwSpSipIncludes.h>
#include <INGwSipProvider/INGwSpSipListenerThread.h>
#include <INGwSipProvider/INGwSpSipProvider.h>
#include <INGwSipProvider/INGwSpSipContext.h>
#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpSipStackIntfLayer.h>

#include <INGwInfraManager/INGwIfrMgrWorkUnit.h>
#include <INGwInfraManager/INGwIfrMgrWorkerThread.h>
#include <INGwInfraManager/INGwIfrMgrThreadMgr.h>

#include <INGwInfraStreamManager/INGwIfrSmAppStreamer.h>

#include <INGwInfraMsrMgr/MsrMgr.h>
#include <INGwInfraStatMgr/INGwIfrSmStatMgr.h>
#include <INGwSipProvider/INGwSpTcpConnMgr.h>

#include <INGwSipProvider/INGwSpSipConnection.h>

pthread_rwlock_t INGwSpSipListenerThread :: mEpTsMapRWLock;

// Yogesh: instead of using string vs TS map use
// elf hash function and create a map of int vs TS
map<string,timeStamp > INGwSpSipListenerThread :: mEpTsMap;

extern BpGenUtil::INGwIfrSmAppStreamer *sipMsgStream;

timeStamp g_NwInbTs;

extern bool gGetReplayFlg();
extern bool gIsSubSeqMsg(int pDlgId);
bool gReplayFlag;

const int dSocketBufSize = 262144;

#ifdef CAPTURE_TS
static char gcTsBuf[50000][50];
static int gsiMsgCnt = 0;
bool gEnaTsCap = false; 
#endif

INGwSpSipListenerThread::INGwSpSipListenerThread() :
_callIdStrStr("Call-ID:", false),
_callIdStrStrShort("\ni:", false),
_dialogueIdStr("Did:",false)
{
   mPort = -1;
   mHost[0] = 0;
   mSockfd = -1;
   pthread_rwlock_init(&mEpTsMapRWLock, 0);
   mEpTsMap.clear();
}

INGwSpSipListenerThread::~INGwSpSipListenerThread()
{
  pthread_rwlock_destroy(&mEpTsMapRWLock);
}

////////////////////////////////////////////////////////////////////////////////
// Method: initialize
// Description: This method initializes the class, by creating a UDP socket and
//              binding it to the specified host and port. 
//
// IN  - aPort: port number to listen at
// IN  - aHost: host ip address to listen at
//
// Return Value - bool: true if socket creation and binding happen successfully,
//                      false otherwise.
////////////////////////////////////////////////////////////////////////////////

bool INGwSpSipListenerThread::initialize(int aPort, char *aHost)
{
  if(0 == aHost ) {
    logger.logINGwMsg(false, ERROR_FLAG, 1, 
           "Null host passed to INGwSpSipListenerThread::initialize");
    return false;
  }
  if(aPort < 1024) {
    logger.logINGwMsg(false, ERROR_FLAG, 1, 
           "Illegal port <%d> INGwSpSipListenerThread::initialize", aPort);
    return false;
  }

  mSupOptLog = false;
  const char *lSupOptLog = getenv("SUPPRESS_OPTIONS_LOGGING");
  if (lSupOptLog != NULL)
  {
      mSupOptLog = true;
  }

  logger.logINGwMsg(false, ALWAYS_FLAG, 0, "initialize(): " 
	"SUPPRESS_OPTIONS_LOGGING <%d>", mSupOptLog);

  mPort = aPort;
  strcpy(mHost, aHost);

  mReplayFlg = true;

  mTranportType = SipTranportType_TCP;

  // TRANSPORT to be used
  const char *lTransTypeStr = getenv ("SIP_TRANSPORT_TYPE");

  if (lTransTypeStr != NULL)
  {
    if (strncmp (lTransTypeStr, "UDP", 3) == 0)
      mTranportType = SipTranportType_UDP;
  }

  logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
									 "INGwSpSipListenerThread::initialize : "
									 "SIP Transport type : <%s>", 
									 ( mTranportType == SipTranportType_TCP ? "TCP" : "UDP" ));

  if(mTranportType == SipTranportType_UDP )
  {
  
    unsigned int bindAddr;

    logger.logINGwMsg(false, VERBOSE_FLAG, 0, 
      "starting network listener thread, host:<%s>, port:<%d>\n", aHost, aPort);

    if(inet_pton(AF_INET, (const char *)mHost, (void *)&bindAddr) != 1)
    {
      int tmperror = errno;
      logger.logINGwMsg(false, ERROR_FLAG, 0,  
                "converting address <%s>: <%s>\n", mHost, strerror(tmperror));
      return false;
    }

    struct sockaddr_in servaddr;
    mSockfd = socket(AF_INET, SOCK_DGRAM, 0);
    if(mSockfd < 0)
    {
      int tmperror = errno;
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
                       "ERROR Creating socket: <%s>\n", strerror(tmperror));
      return false;
    }
    bzero(&servaddr.sin_family, sizeof(servaddr)); 
    servaddr.sin_family = AF_INET;
    // Anurag : porting
    //servaddr.sin_addr.s_addr = htonl(bindAddr);
    servaddr.sin_addr.s_addr = INADDR_ANY;
    servaddr.sin_port = htons(mPort);
    if(bind(mSockfd, (sockaddr *)&servaddr, sizeof(servaddr)) < 0)
    {
      int tmperror = errno;
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
                        "ERROR binding to <%s:%d>:<%s>\n",
                        mHost, mPort, strerror(tmperror));
      return false;
    }

    if(setsockopt(mSockfd, SOL_SOCKET, SO_RCVBUF, 
                  &dSocketBufSize, sizeof(dSocketBufSize)) != 0)
    {
      perror("Failed to increase UDP max buffer size\n");
      exit(0);
    }
  }
  else
  // TCP
  {
    int lMaxClientConn = 64;

    // TRANSPORT to be used
    const char *lConnMaxStr = getenv ("MAX_SIP_TCP_CONN_COUNT");

    if (lConnMaxStr != NULL)
    {
      lMaxClientConn = atol(lConnMaxStr);
    }
   
    // Initialize TCP conn mgr
    INGwSpTcpConnMgr::instance().initialize(mHost, mPort, 
                                            lMaxClientConn, this);

    if(INGwSpTcpConnMgr::instance().bindListener() != 0)
    {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
                        "ERROR binding to <%s:%d>",
                        mHost, mPort);
      return false;
    }

  }

  logger.logINGwMsg(false, VERBOSE_FLAG, 0, 
                   "Initialization of network listener successful");

#ifdef CAPTURE_TS
  for(int i=0; i< 50000; ++i)
  {
    for(int j=0; j< 50; ++j)
    {
      gcTsBuf[i][j] = 0;
    }
  }

  char * lpcCaptureTs = getenv("INGW_CAPTURE_TS");

  if(NULL != lpcCaptureTs)
  {
    if(1 == atoi(lpcCaptureTs)) 
    {
      gEnaTsCap = true;
    }
    logger.logINGwMsg(false,ALWAYS_FLAG,0,"INGW_CAPTURE_TS <%d>",gEnaTsCap);
  }
  else 
  {
    logger.logINGwMsg(false,ALWAYS_FLAG,0,"INGW_CAPTURE_TS Not Defined, "
                      "default <%d>",gEnaTsCap);
  }
  
#endif

  return true;
} // end of initialize

////////////////////////////////////////////////////////////////////////////////
// Method: proxyRun
// Description: This method spawns a new thread, and in that thread continuously
//              listens for sip messages.  When a SIP message is received, a
//              work unit is created, and a transport context is created from
//              the peer host's address.  Then the context is placed in the work
//              unit, and the work unit is posted to the thread manager.
////////////////////////////////////////////////////////////////////////////////
void INGwSpSipListenerThread::proxyRun()
{
   if(mTranportType == SipTranportType_UDP )
   {
      if(mSockfd < 0)
      {
         return;
      }

      char *mesg = NULL;

      char* l_callId = 0;

      logger.logINGwMsg(false, ALWAYS_FLAG, 0, "IN SIP Listener: Entering while loop\n");

#ifdef BIND_THREADS_TO_CPU

      int cpuid = 3;
      start(cpuid);
      bindToCPU();

#endif

      fd_set readfs;
      FD_ZERO(&readfs);
      FD_SET(mSockfd, &readfs);
      int dMaxFd = mSockfd + 1;

      while(mRunStatus)
      {
         if (select(dMaxFd, &readfs, NULL, NULL, NULL) < 0)
         {
            if(errno == EINTR)
            {
               continue;
            }
            else
            {
               printf("!!!!!!!!!!!! Select failed !!!!!!!!!!!!!!!!\n");
               exit(1);
            }
         }

         if(!FD_ISSET(mSockfd, &readfs))
         {
            continue;
         }

         mesg = INGwSpSipStackIntfLayer::instance().getBuffer();
         socklen_t addrsize = sizeof(mCliaddr);
         int n = recvfrom(mSockfd, mesg, MAX_SIPBUF_SIZE, 0,
                       (sockaddr *)&mCliaddr, &addrsize);
         if(n < 0)
         {
            int tmperror = errno;
            logger.logINGwMsg(false, ERROR_FLAG, 0, "ERROR reading from socket: <%s>\n", 
                       strerror(tmperror));
            INGwSpSipStackIntfLayer::instance().reuseBuffer((char*)mesg);
                                                     
            continue;
         }

         int lCurVal = 0;
   			 INGwIfrSmStatMgr::instance().increment(
	   									INGwSpSipProvider::miStatParamId_SipMsgRecvd, lCurVal, 1);

         mesg[n] = '\0';


         // Make a transport info structure and fill in the peer host.

         int cliaddr = mCliaddr.sin_addr.s_addr;
         INGwSipTranspInfo *transportinfo = new INGwSipTranspInfo;
         const char *retval = inet_ntop(AF_INET, (void *)&cliaddr,
                                     transportinfo->pTranspAddr, 
                                     INET_ADDRSTRLEN);
         transportinfo->mPort = ntohl(mCliaddr.sin_port);

         if(!retval)
         {
            logger.logINGwMsg(false, ERROR_FLAG, 0, "ERROR in inet_ntop : \n");
            delete transportinfo;
            INGwSpSipStackIntfLayer::instance().reuseBuffer(mesg);

            continue;
         }

         transportinfo->mTranspType = Sdf_en_protoUdp;

         postNetworkMsg(transportinfo, mesg, n);

      } 
   }
   else
   // TCP Transport
   {
     INGwSpTcpConnMgr::instance().start();
     // Now connect self
     INGwSpTcpConnMgr::instance().getSelfSocketId();
   }
}
//use "tries" or do elf hashing
void INGwSpSipListenerThread::setModifyTimeForEp(string pSasIp, 
                                                timeStamp pCurrTimeStamp){
   logger.logINGwMsg(false,TRACE_FLAG,0,"In setModifyTimeForEp %s",
   pSasIp.c_str());
   pthread_rwlock_wrlock(&mEpTsMapRWLock);
   map<string , timeStamp >:: iterator it = mEpTsMap.find(pSasIp.c_str());
   if(it != mEpTsMap.end()){
     logger.logINGwMsg(false,TRACE_FLAG,0,"+HB+ Found <%s> with TS %ld "
     "New TS %ld", pSasIp.c_str(),(it->second).tv_sec,pCurrTimeStamp.tv_sec);
     (it->second).tv_usec =  pCurrTimeStamp.tv_usec;
     (it->second).tv_sec =  pCurrTimeStamp.tv_sec;
   }else{
     logger.logINGwMsg(false,TRACE_FLAG,0,"+HB+ Creating Entry %s TS<%ld>", 
     pSasIp.c_str(),pCurrTimeStamp.tv_sec);
     mEpTsMap[pSasIp.c_str()] = pCurrTimeStamp;
   }

   pthread_rwlock_unlock(&mEpTsMapRWLock);
   logger.logINGwMsg(false,TRACE_FLAG,0,"Out setModifyTimeForEp");
}

bool INGwSpSipListenerThread::getModifyTimeForEp(string pSasIp, 
                                                 timeStamp &ptimeStamp){

   logger.logINGwMsg(false,TRACE_FLAG,0,"In getModifyTimeForEp %s",
   pSasIp.c_str());
   pthread_rwlock_rdlock(&mEpTsMapRWLock);
   map<string,timeStamp>::iterator it;
   logger.logINGwMsg(false,TRACE_FLAG,0,"+HB+--- printing TS MAP---");

   for(it = mEpTsMap.begin(); it != mEpTsMap.end(); it++){
     logger.logINGwMsg(false,TRACE_FLAG,0,"+HB+<%s, %ld> ",
     it->first.c_str(), it->second.tv_sec);
   }
   logger.logINGwMsg(false,TRACE_FLAG,0,"+HB+--- printing TS MAP---");

   it = mEpTsMap.find(pSasIp.c_str());
   
   if(it == mEpTsMap.end()){
     logger.logINGwMsg(false,TRACE_FLAG,0,"Out getModifyTimeForEp" 
     "+HB+ Serious Error");
      pthread_rwlock_unlock(&mEpTsMapRWLock);
      return false;
   }
   ptimeStamp.tv_sec = it->second.tv_sec;
   ptimeStamp.tv_usec = it->second.tv_usec;

   pthread_rwlock_unlock(&mEpTsMapRWLock);
   logger.logINGwMsg(false,TRACE_FLAG,0,"Out getModifyTimeForEp");
}

void INGwSpSipListenerThread::postNetworkMsg(INGwSipTranspInfo *transportinfo,
                                             char* aBuffRecvd, int aLen)
{
   logger.logINGwMsg(false, TRACE_FLAG, 0, 
   "IN INGwSpSipListenerThread::postNetworkMsg");
   logger.logINGwMsg(false, TRACE_FLAG, 0,"+HB+ Ip Rx  %s",transportinfo->pTranspAddr);
   //timeStamp g_NwInbTs;
   struct timezone tz;
   gettimeofday(&g_NwInbTs, &tz);
   string lEpIp(transportinfo->pTranspAddr, INET_ADDRSTRLEN);

   logger.logINGwMsg(false, TRACE_FLAG, 0,
   "postNetworkMsg +HB+ Incoming Trffc TS <%d> sec %s", g_NwInbTs.tv_sec,lEpIp.c_str());


   setModifyTimeForEp(lEpIp, g_NwInbTs); 

#ifdef CAPTURE_TS
   // Time stamp - Start
   struct timeval tim;
	 char buf[26];
	 gettimeofday( &tim, NULL);
	 time_t tt;

   #ifdef LINUX
	 char *t = ctime_r(&tim.tv_sec, buf);
   #else
   char *t = ctime_r(&tim.tv_sec, buf, sizeof(buf));
   #endif

	 int len = strlen(buf);
	 buf[len-6] = '\0';

   static short lsiBuflen;

   if(gEnaTsCap)
   {
     gsiMsgCnt = ((++gsiMsgCnt) % 50000);     
     memcpy(gcTsBuf[gsiMsgCnt],buf,len-6);


     // Time stamp - End


     lsiBuflen = 0;


     gcTsBuf[gsiMsgCnt][len-6] = '#';
     gcTsBuf[gsiMsgCnt][len-5] = aBuffRecvd[0];
     gcTsBuf[gsiMsgCnt][len-4] = '#';

     lsiBuflen = len - 4;
   }
#endif

   if(sipMsgStream->isLoggable() )
   {
      bool lbIsSuppressOptLog = false;
      bool lIsOptionMsg = false;

      if(mSupOptLog)
      {
         lbIsSuppressOptLog = true;

         if(strncasecmp("OPTIONS", aBuffRecvd, 7) == 0)
         {
            lIsOptionMsg = true;
         }
      }

#ifndef CAPTURE_TS
      // Time stamp - Start
      struct timeval tim;
	    char buf[26];
	    gettimeofday( &tim, NULL);
	    time_t tt;

      #ifdef LINUX
	    char *t = ctime_r(&tim.tv_sec, buf);
      #else
      char *t = ctime_r(&tim.tv_sec, buf, sizeof(buf));
      #endif

	    int len = strlen(buf);
	    buf[len-6] = '\0';
      // Time stamp - End
#endif



      if(lIsOptionMsg && lbIsSuppressOptLog)
			{
         sipMsgStream->log("%s:%d %d Received RAWMSG <%s:%d> :\n%s\n", 
                            buf, tim.tv_usec, (int)pthread_self(),
                            transportinfo->pTranspAddr, transportinfo->mPort, 
														"OPTIONS HEARTBEAT REQUEST");

         logger.logINGwMsg(false, TRACE_FLAG, 0, 
                         "SipListener::proxyRun: RECEIVED RAWMSG <%s:%d> :\n%s", 
                         transportinfo->pTranspAddr, transportinfo->mPort, 
												 "OPTIONS HEARTBEAT REQUEST");
			}
			else
			{
         sipMsgStream->log("%s:%d %d Received RAWMSG <%s:%d> :\n%s\n", 
                            buf, tim.tv_usec, (int)pthread_self(),
                            transportinfo->pTranspAddr, transportinfo->mPort, aBuffRecvd);

         logger.logINGwMsg(false, TRACE_FLAG, 0, 
                         "SipListener::proxyRun: RECEIVED RAWMSG <%s:%d> :\n%s", 
                         transportinfo->pTranspAddr, transportinfo->mPort, aBuffRecvd);
			}
   }

   int dlgId = 0;
   int liThreadIndex = -1;
   char* l_callId = new char[MAX_SIPCID_SIZE];
   //N means NOTIFY
   if (aBuffRecvd[0] == 'N') 
   {
     dlgId = extractDlgId(aBuffRecvd);
#ifdef CAPTURE_TS
     if(gEnaTsCap){
       lsiBuflen += sprintf(gcTsBuf[gsiMsgCnt] + lsiBuflen, "#%d#",dlgId);
     }
#endif
     logger.logINGwMsg(false,TRACE_FLAG,0,"NOB did<%d>",dlgId);
   }

   if(0 == dlgId) {
     if(_getCallIdFromBuffer(aBuffRecvd, aLen, l_callId) == false) 
     {
        logger.logINGwMsg(false, ERROR_FLAG, 0, "ERROR extracting Call-ID from msg: \n");

        INGwSpSipStackIntfLayer::instance().reuseBuffer(aBuffRecvd);
        delete [] l_callId;

        logger.logINGwMsg(false, TRACE_FLAG, 0, 
                         "OUT INGwSpSipListenerThread::postNetworkMsg");
        return;
     }

#ifdef CAPTURE_TS
     if(gEnaTsCap)
     {
       int liCallIdLen = strlen(l_callId);
       liCallIdLen = (liCallIdLen > (50 - lsiBuflen - 1))?(50 - lsiBuflen - 1):
                                                          liCallIdLen;
       lsiBuflen++; 
       memcpy(gcTsBuf[gsiMsgCnt] + lsiBuflen, l_callId, liCallIdLen);

       gcTsBuf[gsiMsgCnt][lsiBuflen+liCallIdLen] = '\0';
     }
#endif
    
     liThreadIndex = getThreadIdIdx(l_callId); 
   } else {
     sprintf(l_callId,"%d",dlgId);
   }
  
#ifdef CAPTURE_TS
   if(gEnaTsCap)
   {
     gcTsBuf[gsiMsgCnt][49] = '\0';
   }
#endif
    

   // Make a work unit and post it to worker thread.
   INGwIfrMgrWorkUnit *workunit    = new INGwIfrMgrWorkUnit;
   workunit->meWorkType    = INGwIfrMgrWorkUnit::SIP_CALL_MSG;
   workunit->mpContextData = (void *)transportinfo;
   workunit->mpMsg         = (void *)aBuffRecvd;
   workunit->mulMsgSize    = aLen;

   workunit->mpcCallId     = l_callId; //While deleting Workunit callid is 
                                       //deleted.
   workunit->mpWorkerClbk  = &(INGwSpSipStackIntfLayer::instance());
   bool isSubSeqMsg = false;
   
   //isSubSeqMsg should stay false if method is not a notify
   if((aBuffRecvd[0] == 'N') && mReplayFlg) {
     mReplayFlg = gGetReplayFlg();
     isSubSeqMsg = gIsSubSeqMsg(dlgId);
   }
  
   //not generating hash if 200 Ok Response is received
   if(-1 == liThreadIndex) {
     workunit->getHash();
   }

   logger.logINGwMsg(false, VERBOSE_FLAG, 0, 
                     "SIPListener: +THREAD+ callid <%s>,"
                     " hash <%ul> ThreadIdx <%d>", 
                      workunit->mpcCallId, workunit->mlHashId, liThreadIndex);

   if(isSubSeqMsg && (!INGwIfrMgrThreadMgr::getInstance().
      postMsgForTakeOverCalls(workunit, dlgId)))
   {
      //Post mesg is failed.
      delete transportinfo;
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
      "SIPListener: POST msg failed callid <%s>, hash <%ul>", 
       workunit->mpcCallId, workunit->mlHashId);

      delete [] workunit->mpcCallId; workunit->mpcCallId = 0;
      delete workunit; workunit = 0;

      INGwSpSipStackIntfLayer::instance().reuseBuffer((char*)aBuffRecvd);
   } else {
      if(isSubSeqMsg) {
        logger.logINGwMsg(false,ALWAYS_FLAG,0,
        "replay sip postMsgForTakeOverCalls <%d>",dlgId);
      }
   }

   if(!isSubSeqMsg && !INGwIfrMgrThreadMgr::getInstance().postMsg(workunit, liThreadIndex))
   {
      //Post mesg is failed.
      delete transportinfo;
      logger.logINGwMsg(false, ERROR_FLAG, 0,
      "SIPListener: POST msg failed callid <%s>, hash <%ul>", 
       workunit->mpcCallId, workunit->mlHashId);

      delete [] workunit->mpcCallId; workunit->mpcCallId = 0;
      delete workunit; workunit = 0;

      INGwSpSipStackIntfLayer::instance().reuseBuffer((char*)aBuffRecvd);
   }
   logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT INGwSpSipListenerThread::postNetworkMsg");
} 

////////////////////////////////////////////////////////////////////////////////
// Method: analyzeCallId
// Description: 
//
//
////////////////////////////////////////////////////////////////////////////////

bool INGwSpSipListenerThread::analyzeCallId(const char* callId, unsigned long& hash)
{
   if(strncmp(callId, "BP_", 3) != 0) 
   {
      //Orig leg callid.
      return true;
   }

   char *endPtr = NULL;

   hash = strtol(callId + 3, &endPtr, 10);

   if(*endPtr == '_')
   {
      return false;
   }

   return true;
}

bool INGwSpSipListenerThread::_getCallIdFromBuffer(const char *msg, int msgLen, 
                                               char *output)
{
   const char *res = _callIdStrStr.findPatternIn(msg, msgLen);

   if(res == NULL)
   {
      res = _callIdStrStrShort.findPatternIn(msg, msgLen);

      if(res == NULL)
      {
         logger.logMsg(ERROR_FLAG, 0, "Unable to find callID in [%s][%d]", 
                       msg, msgLen);
         return false;
      }

      res += 3;
   }
   else
   {
      res += 8;
   }

   while(*res == ' ' || *res == '\t')
   {
      res++;
   }

   while((*res != '\r') && (*res != '\n'))
   {
      *output++ = *res++;
   }

   *output = '\0';
   output--;

   while(*output == ' ')
   {
      *output-- = '\0';
   }

   return true;
}

int INGwSpSipListenerThread::extractDlgId(char * pBuffRecvd)
{
  int j = 0;
  bool dlgIdFound = false;
  int dlgId = 0;
  
  // Tcap DlgId is sent by SAS in the format "seq-dlg=001-16777350"
  // in NOTIFY request line. Here in the sample value "001" is 
  // TcSeq number and "16777350" is tcap dialogueId

  j = 6; // To skip the string "NOTIFY" in the buffer
  do {
    if ((pBuffRecvd[j] == 's')     && (pBuffRecvd[j + 1] == 'e') && 
        (pBuffRecvd[j + 2] == 'q') && (pBuffRecvd[j + 3] == '-') && 
        (pBuffRecvd[j + 4] == 'd') && (pBuffRecvd[j + 5] == 'l') && 
        (pBuffRecvd[j + 6] == 'g'))
    {
      dlgIdFound = true;
      break;
    }
  
    j++;
  
  } while ((pBuffRecvd[j] != '\r') and (pBuffRecvd[j + 1] != '\n'));
  
  
  if (dlgIdFound)
  {
    j += 12; // To skip the string "seq-dlg=???-"
    int i = 0;
    char lcDlgId[32];

    //Termination character of seq-dlg is either ';' or ' '
    while ((pBuffRecvd[j] != ';') && (pBuffRecvd[j] != ' ')) {
      lcDlgId[i++] = pBuffRecvd[j]; j++; 
    }
    lcDlgId[i] = '\0';
    
    dlgId = atoi(lcDlgId);
    logger.logINGwMsg(false, TRACE_FLAG, 0, "extractDlgId():DlgId<%d>", dlgId);
  }
  else {
    logger.logINGwMsg(false, ERROR_FLAG, 0, 
           "extractDlgId():Parameter seq-dlg not found in NOTIFY req line:\n%s",
           pBuffRecvd);
  }
  
  return dlgId;
} 

//delete this method
bool INGwSpSipListenerThread::_getDlgIdFromBuffer(const char *msg, int msgLen, char *output)
{
   const char *res = _dialogueIdStr.findPatternIn(msg, msgLen);

   if(res == NULL)
   {
         logger.logMsg(ERROR_FLAG, 0, "Unable to find dlgId in \n[%s] "
                                      "\n len [%d]", msg, msgLen);
         return false;
   }
   else
   {
      res += 12;
   }

   while(*res == ' ' || *res == '\t')
   {
      res++;
   }

   while((*res != '\r') && (*res != '\n'))
   {
      *output++ = *res++;
   }

   *output = '\0';
   output--;

   while(*output == ' ')
   {
      *output-- = '\0';
   }

   return true;
}

int 
INGwSpSipListenerThread::getThreadIdIdx(char* apcCallId){
  const char* matchStr = "GB-";
  char idx[4];int i=0,j=3;
  int retVal = -1; 
 
  if('G' == apcCallId[0]) {
   //following check is not required
#ifdef CHECK_PATTERN  
    j=0;  
    while((callId[j++] == matchStr[i++]));

    if(*matchStr[i] != 0) return 0;
#endif     

    while(apcCallId[j] != '-'){
      idx[i++] = apcCallId[j++];
    }
    retVal = atoi(idx);
  }
  return retVal; 
}

#ifdef CAPTURE_TS
void INGwSpSipListenerThread::logTsArray()
{
  
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"In  logTsArray <%d>",gEnaTsCap);

  char lBuf[10100];
  int lBufLen = 0;

  memset(lBuf, 0,sizeof(lBuf));
 
  if(gEnaTsCap)
  {
    logger.logINGwMsg(false,ALWAYS_FLAG,0,
                                       "-----------TS CAPTURE-------------");
    int liCntr = gsiMsgCnt;
    
    for(int i=0; i< 50000; ++i)
    {
      liCntr = ((++liCntr) % 50000);
      if('\0' == gcTsBuf[liCntr][0])
        continue;

      lBufLen += sprintf(lBuf + lBufLen,"\n%s",gcTsBuf[liCntr]); 

      if(lBufLen >= 10000)
      {
        logger.logINGwMsg(false,ALWAYS_FLAG,0," %s",lBuf);
        lBufLen = 0;
        lBuf[0] = 0;
      }
    }

    logger.logINGwMsg(false,ALWAYS_FLAG,0," %s",lBuf);
  }
  else
  {
    logger.logINGwMsg(false,ALWAYS_FLAG,0,"logTsArray()TS Capture not enabled");
  }

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"Out logTsArray");
}
#endif

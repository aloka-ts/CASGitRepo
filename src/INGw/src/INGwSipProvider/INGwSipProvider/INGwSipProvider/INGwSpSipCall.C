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
//     File:     INGwSpSipCall.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipProvider");

using namespace std;
#include <sstream>


#include <INGwSipProvider/INGwSpSipCall.h>
#include <INGwSipProvider/INGwSpSipCallFactory.h>

#include <INGwFtPacket/INGwFtPktDeleteCallMsg.h>
#include <INGwFtPacket/INGwFtPktCallDataMsg.h>

#include <INGwInfraManager/INGwIfrMgrThreadMgr.h>
#include <INGwInfraManager/INGwIfrMgrManager.h>
#include <INGwInfraManager/INGwIfrMgrTimerContext.h>
#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>
#include <INGwInfraManager/INGwIfrMgrWorkUnit.h>
#include <INGwSipProvider/INGwSpSipConnection.h>

#include <INGwSipProvider/INGwSpSipCallMap.h>
#include <INGwSipProvider/INGwSpSipProvider.h>
#include <INGwSipProvider/INGwSpSipCallController.h>


#include <Util/SchedulerMsg.h>
#include <INGwInfraMsrMgr/MsrMgr.h>

extern bool gbCcmOnlyHa;

int    INGwSpSipCall::miStCount = 0;

pthread_mutex_t INGwSpSipCall::mStMutex;
std::string INGwSpSipCall::mSeparator("+");

//using namespace RSI_NSP_CCM;

//VersionHolder * INGwSpSipCall::_verHolder = NULL;

int INGwSpSipCall::sendMsgToPeerINGw(char* apcMsgData, short asLength)
{
   LogTrace(0, "IN INGwSpSipCall::sendMsgToPeerINGw");

   INGwIfrPrParamRepository &params = INGwIfrPrParamRepository::getInstance();
   INGwFtPktCallDataMsg replicationMsg;
   replicationMsg.initialize(apcMsgData, asLength, mCallId.c_str(), 
                             params.getSelfId(), params.getPeerId());

   dispatchMsgToPeerINGw(&replicationMsg);

   LogTrace(0, "OUT INGwSpSipCall::sendMsgToPeerINGw");
   return 0;
}

void INGwSpSipCall::dispatchMsgToPeerINGw(INGwFtPktMsg * apMsg)
{
   LogTrace(0, "IN INGwSpSipCall::dispatchMsgToPeerINGw");

   INGwIfrMgrManager::getInstance().sendMsgToINGW(apMsg);

   LogTrace(0, "IN INGwSpSipCall::dispatchMsgToPeerINGw");
}

int INGwSpSipCall::handleWorkerClbk(INGwIfrMgrWorkUnit* apWork)
{
   LogTrace(0, "IN handleWorkerClbk");

   int retValue = 0;
   if(NULL == apWork) 
   {
      logger.logMsg(ERROR_FLAG, imERR_RESOURCE_FAILED, 
                    "NULL pointer passed in INGwSpSipCall::handleWorkerClbk()");
      LogTrace(0, "OUT handleWorkerClbk");
      return -1;
   }

   logger.logMsg(VERBOSE_FLAG, 0, "WorkUnitType:<%d>", apWork->meWorkType);

   switch(apWork->meWorkType)
   {
      case INGwIfrMgrWorkUnit::CCM_CALL_MSG:
      {
         //This is the internal CCM message raised from GC thread.
         retValue = processCCMWorkUnit(apWork);

         //Peer INGW message wont come here.
      }
      break;

      default:
      {
         logger.logINGwMsg(mLogFlag, ERROR_FLAG, imERR_RESOURCE_FAILED, 
                       "Unhandled workunit type [%d]", apWork->meWorkType);
         retValue = -1;
      }
   };

   LogINGwTrace(mLogFlag, 0, "OUT handleWorkerClbk");
   return retValue;
}

int
INGwSpSipCall::processCCMWorkUnit(INGwIfrMgrWorkUnit* apWork)
{
    LogINGwTrace(mLogFlag, 0, "IN processCCMWorkUnit");

    int retValue = 0;
    LogINGwTrace(mLogFlag, 0, "OUT processCCMWorkUnit");
    return retValue;
}

void
INGwSpSipCall::release(int aiCode, short asRetryCount)
{
   LogINGwTrace(mLogFlag, 0, "IN release");
   if(mbIsCallReleased)
   {
      logger.logINGwMsg(mLogFlag, WARNING_FLAG, 0, "Multiple release attempt [%s]",
                      mCallId.c_str());
      LogINGwTrace(mLogFlag, 0, "OUT release");
      return;
   }

   logger.logINGwMsg(mLogFlag, VERBOSE_FLAG, 0, "release: Cause [%d], retry [%d]", 
                    aiCode, asRetryCount);

    mbIsCallReleased = true;
    getRef();

    INGwSpSipConnection* pConn = getFirstConnection();
    short curConnId = -1;
    while(NULL != pConn) 
    {
       INGwIfrUtlRefCount_var connHolder(pConn);

       curConnId = pConn->getSelfConnectionId();
       logger.logINGwMsg(mLogFlag, VERBOSE_FLAG, 0, 
                       "release: Current connid [%d]", (int)curConnId);
       pConn->disconnect(aiCode);

       pConn = getNextConnection(curConnId);
    }

    operType = REP_DELETE;

    startSerialize();
    releaseRef();
    LogINGwTrace(mLogFlag, 0, "OUT release");
}

void 
INGwSpSipCall::getExtCallId(const string& arIntCallId, string& arExtCallId)
{
    LogINGwTrace(false, 0, "IN getIntCallId");

    short pos = arIntCallId.find(mSeparator);
    if((0 < pos) && (arIntCallId.size() > pos)) {
        arExtCallId = arIntCallId.substr(pos + 1, arIntCallId.size() - pos - 1);
    }
    else {
        arExtCallId = arIntCallId;
    }

    logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, 
										"INGwSpSipCall::getExtCallId() : Ext Call Id %s, Int call Id %s", 
										arExtCallId.c_str(), arIntCallId.c_str());
    LogINGwTrace(false, 0, "OUT getIntCallId");
    return ;
}

int
INGwSpSipCall::getCount(void) { return miStCount; }

void INGwSpSipCall::setCounters()
{
   pthread_mutex_lock(&mStMutex);
   miStCount++;
   pthread_mutex_unlock(&mStMutex);
}

int
INGwSpSipCall::initStaticCount(void)
{
    return pthread_mutex_init(&mStMutex, NULL);
}

void 
INGwSpSipCall::setCallId(const string& arconCallId)
{

   mCallId = arconCallId;
   mulCallIdHash = INGwSpSipCallMap::hash(mCallId.c_str());
   INGwIfrUtlRefCount::mId = mCallId;

   setSerializationType(INGwIfrUtlSerializable::FULL_SER_REQD);
   timeval tp;
   gettimeofday(&tp, NULL);
   mChangeData.mlObjCreationTimeStamp = tp.tv_sec;
}

INGwSpSipCall::INGwSpSipCall()
{
   INGwSpSipCall::initObject(true);
}

INGwSpSipCall::INGwSpSipCall(const string& arconCallId)
{
    INGwSpSipCall::initObject(true);
    setCallId(arconCallId);
}

void INGwSpSipCall::initObject(bool consFlag)
{
   if(!consFlag)
   {
      //During Reuse.

      logger.logINGwMsg(mLogFlag, VERBOSE_FLAG, 0, "Call [%s] is being reused", 
                      mCallId.c_str());

      INGwIfrUtlRefCount::initObject(consFlag);
      INGwIfrMgrWorkerClbkIntf::initObject(consFlag);
      INGwIfrUtlSerializable::initObject(consFlag);

      pthread_mutex_lock(&mStMutex);
      miStCount--;
      pthread_mutex_unlock(&mStMutex);
   }
   else
   {
      //During general constructor.
      memset(&mChangeData, 0, sizeof(ChangeableCallData));

      int errCode = pthread_mutexattr_init(&mCallMutexAttr);
  
      if(0 == errCode)
      {
        errCode = pthread_mutexattr_settype(&mCallMutexAttr,
                                            PTHREAD_MUTEX_RECURSIVE);
      }

      if(0 == errCode)
      {
        errCode = pthread_mutex_init(&mCallMutex, &mCallMutexAttr);
      }

      if(0 != errCode)
      {
        logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, "pthread_mutex_init() failed [%d][%s]", 
                      errCode, strerror(errCode));
      }
   }

   msgList.clear();
   mConnList.clear();

   mLogFlag = false;
   mCallId = "";
   mulCallIdHash = 0;
   mbIsCallReleased = false;
   m_peerStartTime = 0;

   setMajorState(INGwSpSipCall::CALL_IDLE);

   mChangeData.meMinorState = CALL_MIN_IDLE;
   mChangeData.msConnCounter = 0;
   mChangeData.miUniqId = 0;
   mChangeData.mlObjCreationTimeStamp = 0;
}

INGwSpSipCall::~INGwSpSipCall() 
{ 

   pthread_mutex_destroy(&mCallMutex);
   pthread_mutexattr_destroy(&mCallMutexAttr);
}

INGwSpSipConnection*
INGwSpSipCall::getConnection(short asConnId)
{ 
  
  INGwSpSipConnection* pConn = NULL;
    ConnList::iterator it = mConnList.begin();
    for( ; it != mConnList.end(); it++) {
        if(asConnId == (*it)->getSelfConnectionId()) {
            pConn = *it;
            pConn->getRef();
            break ;
        }
    }
    return pConn; 
}

INGwSpSipConnection* INGwSpSipCall::getFirstConnection(void)
{
   INGwSpSipConnection* pConn = NULL;

   if(mConnList.size() > 0) 
   {
      pConn = mConnList.front();
      pConn->getRef();
   }

   return pConn;
}

INGwSpSipConnection* 
INGwSpSipCall::getNextConnection(short asConnId)
{
    INGwSpSipConnection* pConn = NULL;
    ConnList::iterator it = mConnList.begin();
    for( ; it != mConnList.end(); it++) {
        if(asConnId < (*it)->getSelfConnectionId()) {
            pConn = *it;
            pConn->getRef();
            break ;
        }
    }
    return pConn;
}

INGwSpSipConnection * INGwSpSipCall::getLastConnection()
{
   INGwSpSipConnection *pConn = NULL;

   if(mConnList.size() > 0)
   {
      pConn = mConnList.back();
      pConn->getRef();
   }

   return pConn;
}

short
INGwSpSipCall::addConnection(INGwSpSipConnection* apConn, short asConnId)
{
   LogINGwTrace(mLogFlag, 0, "IN addConnection");

   if(NULL == apConn) 
   {
      logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, "NULL Conn for callId [%s]", 
                      mCallId.c_str());
        return -1;
   }

   if(asConnId == -1)
   {
      asConnId = mChangeData.msConnCounter++;
   }

   mConnList.push_back(apConn);
   apConn->setSelfConnectionId(asConnId);
   apConn->getRef();
   apConn->mLogFlag = mLogFlag;

   LogINGwTrace(mLogFlag, 0, "OUT addConnection");
   return asConnId;
}

short INGwSpSipCall::removeConnection(short asConnId, bool backupObj)
{
   LogINGwTrace(mLogFlag, 0, "IN removeConnection");
   logger.logINGwMsg(mLogFlag, VERBOSE_FLAG, 0, "Removing ConnId [%d] [%s]", 
                   asConnId, mCallId.c_str());

   INGwSpSipConnection* pConn = NULL;
   ConnList::iterator it = mConnList.begin();

   for( ; it != mConnList.end(); ) 
   {
      if(asConnId == (*it)->getSelfConnectionId()) 
      {
         pConn = *it;
         mConnList.erase(it);
         break;
      }
			it++;
	 }

 if(NULL == pConn)  
   {
      logger.logINGwMsg(mLogFlag, VERBOSE_FLAG, 0, "Conn Id [%d] not avl", 
                      asConnId);
   }
   else 
   {
      INGwSpSipConnection *pPeerConn = getConnection(pConn->getPeerConnectionId());
      INGwIfrUtlRefCount_var peer_con_var(pPeerConn);

      if(pPeerConn) 
      {
         // Reset the peer connection's peer connection id
         // only if it is equal to the present connection.

         if(asConnId == pPeerConn->getPeerConnectionId())
         {
            pPeerConn->setPeerConnectionId(-1);
         }
      }
       
      pConn->releaseRef();
   }

   short remConnCount = mConnList.size();

   if(0 == remConnCount) 
   {
      logger.logINGwMsg(mLogFlag, VERBOSE_FLAG, 0, "Removing call[%s] from "
                                                 "CallMap", mCallId.c_str());
			INGwSpSipCallController* callCtlr =
							 INGwSpSipProvider::getInstance().getCallController();
      callCtlr->removeCall(mCallId);
   }

   LogINGwTrace(mLogFlag, 0, "OUT removeConnection");
   return remConnCount;
}

short INGwSpSipCall::removeConnectionOnly(short asConnId, bool backupObj)
{
   LogINGwTrace(mLogFlag, 0, "IN removeConnectionOnly");
   logger.logINGwMsg(mLogFlag, VERBOSE_FLAG, 0, "Removing ConnId [%d] [%s]",
                   asConnId, mCallId.c_str());

   INGwSpSipConnection* pConn = NULL;
   ConnList::iterator it = mConnList.begin();

   for( ; it != mConnList.end(); )
   {
      if(asConnId == (*it)->getSelfConnectionId())
      {
         pConn = *it;
         mConnList.erase(it++);
         break;
      }
      else
      {
         it++;
      }
   }

   if(NULL == pConn)
   {
      logger.logINGwMsg(mLogFlag, VERBOSE_FLAG, 0, "Conn Id [%d] not avl",
                      asConnId);
   }
   else
   {
      INGwSpSipConnection *pPeerConn = getConnection(pConn->getPeerConnectionId());
      INGwIfrUtlRefCount_var peer_con_var(pPeerConn);

      if(pPeerConn)
      {
         // Reset the peer connection's peer connection id
         // only if it is equal to the present connection.

         if(asConnId == pPeerConn->getPeerConnectionId())
         {
            pPeerConn->setPeerConnectionId(-1);
         }
      }

      pConn->releaseRef();
   }

   short remConnCount = mConnList.size();

   if(0 == remConnCount)
   {
      logger.logINGwMsg(mLogFlag, VERBOSE_FLAG, 0, "Removing call[%s] from "
                                                 "CallMap", mCallId.c_str());
			INGwSpSipCallController* callCtlr =
							 INGwSpSipProvider::getInstance().getCallController();
      callCtlr->removeCall(mCallId);
   }

   LogINGwTrace(mLogFlag, 0, "OUT removeConnection");
   return remConnCount;
}

void
INGwSpSipCall::removeAllConnections(void)
{
    LogINGwTrace(mLogFlag, 0, "IN removeAllConnections");

    ConnList::iterator it = mConnList.begin();
    for( ; it != mConnList.end(); ) {
        (*it)->releaseRef();
        mConnList.erase(it++);
    }
    LogINGwTrace(mLogFlag, 0, "OUT removeAllConnections");
}

string
INGwSpSipCall::toLog(void) const
{
    ostringstream strStream;

    strStream << endl << endl << "CALL_ID : " << mCallId;
    strStream << INGwIfrUtlRefCount::toLog();
    strStream << " , CON_COUNT : " << mConnList.size() << endl;

    strStream << "----------START---------------------" << endl;

    for(SigMsgListCIt it = msgList.begin(); it != msgList.end(); it++)
    {
       strStream << (*it);
       strStream << "------------------------------------" << endl;
    }

    strStream << "----------END-----------------------" << endl;


    ConnList::const_iterator it = mConnList.begin();
    for( ; it != mConnList.end(); it++) {
            strStream << (*it)->toLog() << endl;
    }

    return strStream.str();
}


int INGwSpSipCall::getUniqId(void) { return mChangeData.miUniqId++; }

void INGwSpSipCall::lock(void) const { pthread_mutex_lock(&mCallMutex); }

void INGwSpSipCall::unlock(void) const { pthread_mutex_unlock(&mCallMutex); }

INGwSpSipCall::MajorState INGwSpSipCall::getMajorState(void) { return mChangeData.meMajorState; }

INGwSpSipCall::MinorState INGwSpSipCall::getMinorState(void) { return mChangeData.meMinorState; }

const string & INGwSpSipCall::getCallId(void) const { return mCallId; }

unsigned long INGwSpSipCall::getHashedCallId(void) const { return mulCallIdHash; }

void INGwSpSipCall::setMajorState(INGwSpSipCall::MajorState aeMajorState) {
  if(aeMajorState == INGwSpSipCall::CALL_CONNECTED && 
		 mChangeData.meMajorState != INGwSpSipCall::CALL_CONNECTED) 
  {
  }
  else if(aeMajorState == INGwSpSipCall::CALL_IDLE && 
					mChangeData.meMajorState == INGwSpSipCall::CALL_CONNECTED)
  {
  }
  mChangeData.meMajorState = aeMajorState; 
}

void
INGwSpSipCall::setMinorState(INGwSpSipCall::MinorState aeMinorState) {

  logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, "Minor Call State Changed from:<%s> to:<%s>", 
     min_state[mChangeData.meMinorState], min_state[aeMinorState]);

  mChangeData.meMinorState = aeMinorState; 
}

bool INGwSpSipCall::startSerialize(INGwIfrUtlSerializable::ReplicationOperationType apRepOpr, 
                                    INGwFtPktMsg* apMsg)
{
  LogINGwTrace(mLogFlag, 0, "IN startSerialize");

   if(gbCcmOnlyHa)
   {
      return true;
   }

   operType = apRepOpr;

  INGwIfrPrParamRepository &paramRepository = INGwIfrPrParamRepository::getInstance();
  if(paramRepository.getPeerStatus())
  {
     if(m_peerStartTime != paramRepository.getPeerStartTime())
     {
        m_peerStartTime = paramRepository.getPeerStartTime();
     }

     //operType = apRepOpr;

     // Allocate memory and call serialize.

		 int dataSize = SIZE_OF_BPMSG + BASE_CCM_CALLDATA_MSG_SIZE + 
			 mCallId.size() + SIZE_OF_SHORT;

		 int emptyMsgDataSize = dataSize;

     unsigned char* serbuf = new unsigned char[1024000];
   
     if((serialize(serbuf, dataSize, dataSize, 51200, 
                   true) == false) || dataSize >= 51200 )
     {
        if(dataSize >= 51200)
        {
           logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0,
                            "Serialization Data Size <%d> greater than MAX 51200", 
														dataSize);
        }   

        logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, 
													"Serialization failed. Data Size <%d>", 
													dataSize);
        logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, 
                         "Sending Delete Call to peer for %s .", 
												 mCallId.c_str());

        dataSize = 0;
/*

        {
           //Sending Delete call to cleanup the call in the peer.

           INGwFtPktDeleteCallMsg deleteCallMsg;

           deleteCallMsg.initialize(mCallId.c_str(), 
                                    paramRepository.getSelfId(),
                                    paramRepository.getPeerId());

           dispatchMsgToPeerINGw(&deleteCallMsg);
        }
*/

     }// end of if serialize fail
     else
     {
        logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, 
													"Serialization success. Data Size <%d>", 
													dataSize);
/*
        if(operType == REP_CREATE)
        {
           operType = REP_DELETE;
        }
     
        else if(operType == REP_DELETE)
        {
           meSerType = SER_NOT_REQD;
        }

        if(meSerType == FULL_SER_REQD)
        {
           meSerType = INC_SER_REQD;
        }
*/

     }// end of else of if serialize fail

		 if(dataSize > emptyMsgDataSize) {

       logger.logINGwMsg(mLogFlag, VERBOSE_FLAG, 0, 
			  								"INGwSpSipCall::startSerialize: Serialized "
                        "size is <%d>", dataSize);

			 sendMsgToPeerINGw((char *)serbuf, (short)dataSize);

		 } else {
			 logger.logINGwMsg(mLogFlag, WARNING_FLAG, 0, 
					              "No call data serialized. ");

			 delete []serbuf;
			 serbuf = NULL;
			 dataSize = 0;
		 }
  }


  LogINGwTrace(mLogFlag, 0, "OUT startSerialize");
  return true;
}

void INGwSpSipCall::setVersionDetail()
{
#if 0
   _verHolder = VersionMgr::getInstance().getVersionHolder(
                            VersionMgr::VER_CCM_SER, VersionMgr::CCM_SUBSYSTEM);
#endif
}

bool INGwSpSipCall::serialize(unsigned char *apcData, int aiOffset, int &aiNewOffset, 
                       int aiMaxSize, bool abForceFullSerialization)
{
#if 0
   if(_verHolder->getSendVersion() != 1)
   {
      return false;
   }

#endif

   LogTrace(0, "IN serialize");

   aiNewOffset = aiOffset;

   logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, "INGwSpSipCall::serialize : "
                     "Serializing current offset is <%d> .", aiNewOffset);

   if(operType == INGwIfrUtlSerializable::REP_DELETE)
   {
      if(aiMaxSize <= (aiNewOffset + 1)) 
      {
         logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, 
													"Buffer size [%d] not enough.Req size is %d",
													aiMaxSize, aiNewOffset + 1);
         LogTrace(0, "OUT serialize");
         return false;
      }

      apcData[aiNewOffset] = (unsigned char)operType;
      aiNewOffset++;

      LogTrace(0, "OUT serialize");
      return true;
   }

   if(aiMaxSize <= (aiNewOffset + 2))  //char + char
   {
      logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, 
													"Buffer size [%d] not enough.Req size is %d",
													aiMaxSize, aiNewOffset + 1);
      LogTrace(0, "OUT serialize");
      return false;
   }

   if(abForceFullSerialization)
   {
      apcData[aiNewOffset] = (unsigned char)INGwIfrUtlSerializable::REP_CREATE;
      aiNewOffset++;
      apcData[aiNewOffset] = (unsigned char)INGwIfrUtlSerializable::FULL_SER_REQD;
      aiNewOffset++;
   }
   else
   {
      apcData[aiNewOffset] = (unsigned char)operType;
      aiNewOffset++;
      apcData[aiNewOffset] = (unsigned char)meSerType;
      aiNewOffset++;
   }

   if(aiMaxSize <= (aiNewOffset + sizeof(ChangeableCallData)))
   {
      logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, "Buffer size not enough.");
      LogTrace(0, "OUT serialize");
      return false;
   }

   logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, "INGwSpSipCall::serialize : "
                     "Serializing ChangeableCallData offset is <%d> .", aiNewOffset);

   aiNewOffset += INGwIfrUtlSerializable::serializeStruct(&mChangeData, 
                   sizeof(ChangeableCallData), (char *)(apcData + aiNewOffset));

   //Serialize the conn obj
   
   logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, "INGwSpSipCall::serialize : "
                     "Serializing connection offset is <%d> .", aiNewOffset);

   INGwSpSipConnection* lConn = getFirstConnection();
   int lConnId = -1;

   while(lConn)
   {
      int lPreviousOffset = aiNewOffset;
      INGwIfrUtlRefCount_var connHolder(lConn);
      lConnId = lConn->getSelfConnectionId();
/*
      if(lConn->serializationReadyFlag == COMP_SERI_NOT_READY)
      {
         logger.logINGwMsg(mLogFlag, WARNING_FLAG, 0, 
                          "Conn [%d] not ready for serialization.skipping.", lConnId);

         lConn = getNextConnection(lConnId);
         continue;
      }
*/
      //Serailize conn id
      aiNewOffset += INGwIfrUtlSerializable::serializeShort((short)lConnId, 
                                                            (char*)(apcData + aiNewOffset));

      logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, "INGwSpSipCall::serialize : "
                        "Serializing connection <%d> .Offset is <%d>", 
												lConnId, aiNewOffset);
      if(lConn->serialize(apcData, aiNewOffset, aiNewOffset, aiMaxSize,
                          abForceFullSerialization) == false)
      {
         aiNewOffset = lPreviousOffset;

         logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, "INGwSpSipCall::serialize : "
                           "Serialization of connection <%d> FAILED.", lConnId);
         LogTrace(0, "OUT serialize");
         return false;
      }

      logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, "INGwSpSipCall::serialize : "
                        "Serialization done for connection <%d> . Offset is <%d>", 
												lConnId, aiNewOffset);
      lConn = getNextConnection(lConnId);
     
   }

   logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, "INGwSpSipCall::serialize : "
                     "Serialization done for call. Offset is <%d>", 
										aiNewOffset);
   LogTrace(0, "OUT serialize");


	 return true;
}

bool INGwSpSipCall::deserialize(const unsigned char* apcData, int aiOffset, 
                         int& aiNewOffset, int aiMaxSize)
{
#if 0
   if(_verHolder->getRecvVersion() != 1)
   {
      return false;
   }

#endif
   bool ret = true;

   LogTrace(0, "IN deserialize");

   logger.logINGwMsg(mLogFlag, VERBOSE_FLAG, 0, 
                  "INGwSpSipCall::deserialize: Current offset is <%d> ",
                  aiOffset);

   if(aiMaxSize <= (aiOffset + 2))
   {
      logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, "Invalid buffersize . "
												"Required [%d] and max is [%d]",
												(aiOffset + 2), aiMaxSize);
      LogTrace(0, "OUT deserialize");
      return false;
   }

   aiOffset += 1;  //Nothing to do with oper type.

   SerializationType type = (SerializationType)(apcData[aiOffset]);
   aiOffset++;

   switch(type)
   {
      case SER_NOT_REQD:
      {
         return true;
      }
      case INC_SER_REQD:
      case FULL_SER_REQD:
      {
         if(aiMaxSize <= (aiOffset + sizeof(ChangeableCallData)))
         {
            logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, "Invalid buffersize . "
					      							"Required [%d] and max is [%d]",
							      					(aiOffset + sizeof(ChangeableCallData)), aiMaxSize);
            LogTrace(0, "OUT deserialize");
            return false;
         }

         logger.logINGwMsg(mLogFlag, VERBOSE_FLAG, 0, 
                        "INGwSpSipCall::deserialize: "
												"deserializing ChangeableCallData Current offset is <%d> ",
                        aiOffset);
         aiOffset += INGwIfrUtlSerializable::deserializeStruct(
                                  (char *)(apcData + aiOffset), &mChangeData,
                                  sizeof(ChangeableCallData));

      }
   }

   while(aiOffset < aiMaxSize)
   {
      short lConnId = -1;
      
      logger.logINGwMsg(mLogFlag, VERBOSE_FLAG, 0, 
                        "INGwSpSipCall::deserialize: "
												"deserializing ConnId Current offset is <%d> ",
                        aiOffset);

      aiOffset += INGwIfrUtlSerializable::deserializeShort((char *)(apcData + aiOffset),
                                                           lConnId);
      ReplicationOperationType repType = 
                                 (ReplicationOperationType)apcData[aiNewOffset];
      bool bReplicationOutOfSynchProblem = false;
      
      INGwSpSipConnection * lConn = NULL;
      switch(repType)
      {
         case REP_DELETE :
         {
            logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, 
                             "Replication type REP_DELETE for conn <%d> .",
                              (int)lConnId);

            lConn = getConnection(lConnId);
            INGwIfrUtlRefCount_var connHolder(lConn);

            if(lConn == NULL)
            {
               logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, 
                               "Replication type REP_DELETE for an non "
                               "existing connection. Conn Id <%d>", (int)lConnId);
               LogTrace(0, "OUT deserialize");
               return false;
            }
            else
            {
               lConn->cleanup();
            } // else lConn is NULL
         } // case REP_DELETE
         break;

         case REP_CREATE :
         {
            logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, 
                             "Replication type REP_CREATE for conn <%d> .",
                              (int)lConnId);

            lConn = getConnection(lConnId);
            INGwIfrUtlRefCount_var connHolder(lConn);

            if(lConn != NULL)
            {
               logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, 
                               "Replication type REP_CREATE for an existing "
                               "connection. Conn Id <%d>", (int)lConnId);
               bReplicationOutOfSynchProblem = true;
            }
            else
            {
              lConn = INGwSpSipProvider::getInstance().getNewConnection(*this);
              if (lConn == NULL) {
                 logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, 
                                 "Failed to get new conn from provider.");
                 bReplicationOutOfSynchProblem = true;
                 break;
              }
              INGwIfrUtlRefCount_var connHolder2(lConn);

              addConnection(lConn, lConnId);

							aiNewOffset = aiOffset;

              if(lConn->deserialize(apcData, aiNewOffset, aiNewOffset, 
                    aiMaxSize) == false)
              {
                 logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, 
                                "Connection deserialize failed.");
                 lConn->cleanup();
                 return false;
              }

							aiOffset = aiNewOffset;

              logger.logINGwMsg(mLogFlag, VERBOSE_FLAG, 0, 
                                "INGwSpSipCall::deserialize: "
										        		"deserialized Conn Current offset is <%d> ",
                                aiOffset);

            } // else lConn is Null
         } // case REP_CREATE
         break;

         case REP_REPLICATE :
         {
            logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, 
                             "Replication type REP_REPLICATE for conn <%d> .",
                              (int)lConnId);

            lConn = getConnection(lConnId);
            INGwIfrUtlRefCount_var connHolder(lConn);

            if(lConn == NULL)
            {
               logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, 
                               "Replication type REP_REPLICATE for an non-existing "
                               "connection. Conn Id <%d>", (int)lConnId);
               bReplicationOutOfSynchProblem = true;
            }
            else
            {

							aiNewOffset = aiOffset;
              if(lConn->deserialize(apcData, aiNewOffset, aiNewOffset, 
                    aiMaxSize) == false)
              {
                 logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, 
                                "Connection deserialize failed.");
                 lConn->cleanup();
                 return false;
              }
							aiOffset = aiNewOffset;
            } // else lConn is Null
         } // case REP_REPLICATE
         break;
      }
       if (bReplicationOutOfSynchProblem) {
         // Some serious problem in replication of this call. 
         // If this connection had a problem in either REP-CREATE/REP_REPLICATE, 
         // we cannot get the data for next connection objects correctly.
         // Moreover, we also cannot afford to leave the call non-updated.
         // Need to make sure that we clean up this call from Standby as this 
         // call's half-cooked state might be dangerous when it becomes active 
         // and tries to salvage the call.
         // For the greater good : 

         logger.logINGwMsg(false, ERROR_FLAG, 0, "Replication for this call seems "
                           "out-of-sync. Shall cleanup the call [ %s ]", mCallId.c_str() );

        cleanup();
        LogINGwTrace(false, 0, "OUT deserialize");
        return false;
      } else {
         logger.logINGwMsg(mLogFlag, VERBOSE_FLAG, 0, 
                  "INGwSpSipCall::deserialize: new offset after deserialization "
                  "of connid <%d> is <%d>", (int)lConnId, aiOffset);
      }

   }

   aiNewOffset = aiOffset;
   logger.logINGwMsg(mLogFlag, VERBOSE_FLAG, 0, 
                     "INGwSpSipCall::deserialize: "
										 "deserialized Call.  offset is <%d> ",
                     aiNewOffset);

   LogTrace(0, "OUT deserialize");
	 return true;
}

void INGwSpSipCall::cleanup(void)
{
   LogINGwTrace(mLogFlag, 0, "IN cleanup");

   INGwSpSipConnection* pConn = getFirstConnection();
   short curConnId = -1;
   while(NULL != pConn) 
   {
      curConnId = pConn->getSelfConnectionId();
      pConn->cleanup();
      pConn->releaseRef();
      pConn = getNextConnection(curConnId);
   }

	INGwSpSipCallController* callCtlr =
						INGwSpSipProvider::getInstance().getCallController();
  callCtlr->removeCall(mCallId);

  LogINGwTrace(mLogFlag, 0, "OUT cleanup");
}

bool INGwSpSipCall::isCallReleased(void) const
{
  return mbIsCallReleased;
}


void INGwSpSipCall::terminateCall()
{
}

bool INGwSpSipCall::isValidConnection(int connID)
{
   ConnList::iterator it = mConnList.begin();
   for( ; it != mConnList.end(); it++) 
   {
      if(connID == (*it)->getSelfConnectionId()) 
      {
         return true;
      }
   }

   return false;
}

void INGwSpSipCall::releaseRef(void)
{
#ifdef USE_LOCK_FOR_REF_COUNT
   pthread_mutex_lock(&mRefMutex);
#endif

   msRefCount--;

   logger.logINGwMsg(mLogFlag, VERBOSE_FLAG, 0, 
                   "releaseRef Call: Id [%x][%s], Ref count <%d>", 
                   (INGwIfrUtlRefCount *)this, mId.c_str(), msRefCount);

   if(1 == msRefCount) 
   {
#ifdef USE_LOCK_FOR_REF_COUNT
     pthread_mutex_unlock(&mRefMutex);
#endif
      INGwSpSipCallFactory::getInstance().reuseObject(this);
      return;
   }
   else if(0 >= msRefCount) 
   {

#ifdef USE_LOCK_FOR_REF_COUNT
      pthread_mutex_unlock(&mRefMutex);
#endif

      delete this;
      return;
   }

#ifdef USE_LOCK_FOR_REF_COUNT
   pthread_mutex_unlock(&mRefMutex);
#endif

   return;
}

// EOF INGwSpSipCall.C

/*------------------------------------------------------------------------------
         File: INGwFtTkCommunicator.C
  Application: INGwFtTalk Communicator
    Component: INGwFtTalk
   Programmer: S.Suriya prakash
      Written: 21-Jun-2003
  Description: BayPackets App message transfer protocol
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#include <Util/Logger.h>
LOG("INGwFtTalk");

#include <INGwFtTalk/INGwFtTkCommunicator.h>
#include <INGwFtTalk/INGwFtTkChannel.h>
#include <INGwFtTalk/INGwFtTkChannelManager.h>
#include <INGwFtTalk/INGwFtTkConnHandlerInf.h>
#include <INGwFtTalk/INGwFtTkFaultHandlerInf.h>
#include <INGwFtTalk/INGwFtTkMessage.h>
#include <INGwFtTalk/INGwFtTkMsgHandlerInf.h>
#include <INGwFtTalk/INGwFtTkSubCompMgr.h>
#include <INGwFtTalk/INGwFtTkFaultManager.h>
#include <INGwFtTalk/INGwFtTkConnHandle.h>

#include <Agent/BayAgentImpl.h>
#include <Util/imOid.h>
#include <Util/imAlarmCodes.h>

#include <sys/types.h>
#include <sys/socket.h>
#include <sys/uio.h>
#include <netinet/in.h>
#include <netinet/tcp.h>
#include <net/if.h>
#include <arpa/inet.h>
#include <inttypes.h>
#include <netdb.h>
#include <strings.h>
#include <pthread.h>
#include <errno.h>
#include <unistd.h>
#include <stdlib.h>
#include <limits.h>

#ifdef LINUX 
//MAX_IOVEC = 10 in linux
#define BTK_MAX_WRITE_ARY 10
#else
#define BTK_MAX_WRITE_ARY IOV_MAX
#endif

bool getNetMaskForIp(const char* apcIP, unsigned int& auirNumericNetMask)
{
   char    *buf, *ptr;
   int     len , lastlen;
   bool    retVal;
   int     sockfd;

   len = lastlen =0;
   retVal = false;

   // First fetch all network config parameters. Extract network name
   // for the expected IP and then fetch subnet mask for corresponding
   // Network name.
   if(0 > (sockfd = socket (AF_INET, SOCK_DGRAM, 0))) 
   {
      logger.logMsg(ERROR_FLAG,0, "Could not get subnetMask for Ip[%s], "
                                  "socket creation failed", apcIP);
      return retVal;
   }

   struct ifconf ifc;

   len = 100*sizeof(struct ifreq);
   for( ; ; ) 
   {
      buf = new char[len];
      memset(buf, '\0', len);

      ifc.ifc_len = len;
      ifc.ifc_buf = buf;

      if(0 < ioctl(sockfd, SIOCGIFCONF, &ifc)) 
      {
         logger.logMsg(ERROR_FLAG,0, "Could not get subnetMask for Ip[%s], "
                                     "ioctl failed ", apcIP);
         delete [] buf;
         return retVal;
      }
      else 
      {
         if(ifc.ifc_len == lastlen)
         {
            break;
         }

         lastlen = ifc.ifc_len;
      }

      len += 10 * sizeof(struct ifreq);
      delete [] buf;
   }

   struct ifreq *ifr;

   for(ptr = buf; ptr < buf + ifc.ifc_len; ) 
   {
      ifr = (struct ifreq *)ptr;

      switch(ifr->ifr_addr.sa_family)
      {
         case AF_INET6:
         {
            len = sizeof(struct sockaddr_in6);
         }
         break;

         case AF_INET:
         {
            len = sizeof(struct sockaddr);
         }
         break;
      }

      ptr += sizeof(ifr->ifr_name) + len;

      sockaddr_in* fetchedIpAddr = (sockaddr_in*)&ifr->ifr_ifru.ifru_addr;
      char *fetchIp = inet_ntoa(fetchedIpAddr->sin_addr);

      if(0 != strcmp(apcIP, fetchIp))
      {
         continue;
      }

      struct ifreq Req;

      // copy network name
      bzero(Req.ifr_name, 16);
      bcopy(ifr->ifr_name, Req.ifr_name, strlen(ifr->ifr_name));

      // fetch subnet mask.
      if(0 == ioctl(sockfd, SIOCGIFNETMASK, &Req)) 
      {
         sockaddr_in* adr = (sockaddr_in*)&Req.ifr_ifru.ifru_addr;

         const char *subMask = inet_ntoa(adr->sin_addr);

         auirNumericNetMask = inet_addr(subMask);
         break;
      }
      else 
      {
         logger.logMsg(ERROR_FLAG,0, "Could not get subnetMask for Ip[%s], "
                                     "ioctl2 failed ", apcIP);
         delete [] buf;
         return retVal;
      }
   }

   if(retVal == true) 
   {
      logger.logMsg(VERBOSE_FLAG, 0, "getNetMask Ip[%s] numMask[%u]", 
                    apcIP, auirNumericNetMask);
   }

   delete [] buf;
   return retVal= true;
}

INGwFtTkCommunicator::INGwFtTkCommunicator(INGwFtTkVersionHandlerInf *versionInf) : 
INGwFtTkSingleton<INGwFtTkCommunicator>()
{
   _subCompMgr = new INGwFtTkSubCompMgr();
   _channelMgr = new INGwFtTkChannelManager();
   _faultMgr = new INGwFtTkFaultManager();
   _selfAbout = new INGwFtTkControlMsg();
   _versionInf = versionInf;
   _sysID = BayAgentImpl::getInstance().getSubsystemID();

   _selfAbout->peerID = _sysID;
   _selfAbout->action = 0;
   _selfAbout->instanceID.startTime = time(NULL);

   _faultCallback = NULL;

   _versionSet.insert(1);

   if(_versionInf == NULL)
   {
      logger.logMsg(ALWAYS_FLAG, 0, "Version handling interface is NULL. "
                                    "All App version negotiation will fail.");
   }

   logger.logMsg(ALWAYS_FLAG, 0, "INGwFtTalk communicator Instanciated. [%d]",
                 _selfAbout->instanceID.startTime);

   if(_initialize() == -1)
   {
      logger.logMsg(ALWAYS_FLAG, 0, "Initialization failed.");
      printf("INGwFtTalk initialization failed.\n");
      exit(1);
   }

   logger.logMsg(ALWAYS_FLAG, 0, "Successfully started INGwFtTalk communicator.");
   logger.logMsg(ALWAYS_FLAG, 0, "Listen [%x-%d] InstanceID [%d] SelfID [%d]",
                 _selfAbout->listenDetail.IP, _selfAbout->listenDetail.port,
                 _selfAbout->instanceID.startTime, _selfAbout->peerID);
}

INGwFtTkCommunicator::~INGwFtTkCommunicator()
{
   delete _channelMgr;
   delete _subCompMgr;
   delete _faultMgr;
   delete _selfAbout;
}

INGwFtTkConnHandle * INGwFtTkCommunicator::openConnection(ObjectId peerId)
{
   if(peerId.getComponentId() == _sysID)
   {
      logger.logMsg(ERROR_FLAG, 0, "Attempt for self connection.");
      return NULL;
   }

   if(peerId.getSubComponentId() == 0)
   {
      logger.logMsg(ERROR_FLAG, 0, "SubComponent ID shouldnt be zero.");
      return NULL;
   }

   int retryCount = 0;

   while(retryCount < 5)
   {
      INGwFtTkChannelManager::INGwFtTkCreationStatus retStatus;

      INGwFtTkChannel *currChannel = _channelMgr->openChannel(peerId, 0, 0, 
                                                         retStatus);

      if(currChannel == NULL)
      {
         logger.logMsg(ERROR_FLAG, 0, "Unable to get the channel.");
         return NULL;
      }

      INGwFtTkRefObjHolder holder(currChannel);

      if(retStatus == INGwFtTkChannelManager::ALREADY_EXIST)
      {
         INGwFtTkConnHandle *handle = new INGwFtTkConnHandle(peerId);

         if(_channelMgr->setHandler(peerId, handle) == 0)
         {
            logger.logMsg(ALWAYS_FLAG, 0, "Successfully registered channel for "
                                          "[%d]", peerId.getComponentId());
            return handle;
         }
   
         delete handle;

         logger.logMsg(ERROR_FLAG, 0, "Unable to set the handler for peer [%d]",
                       peerId.getComponentId());
         return NULL;
      }

      INGwFtTkEndPointInfo peerDetail;

      if(_getINGwFtTkDetail(peerId.getComponentId(), &peerDetail) == -1)
      {
         logger.logMsg(ERROR_FLAG, 0, "Unable to get proper peer detail from "
                                      "BMgr.");
         currChannel->disconnect();
         _channelMgr->closeChannel(peerId.getComponentId());
         return NULL;
      }

      INGwFtTkChannel::INGwFtTkEstablishStatus estRetStatus = INGwFtTkChannel::ESTABLISHED;
      currChannel->setEndPointInfo(peerDetail);

      if(currChannel->establishConnection(*_selfAbout, estRetStatus) == 0)
      {
         if(_faultMgr->checkEstablishSuccessResponse(currChannel->clone()) 
            == -1)
         {
            logger.logMsg(ERROR_FLAG, 0, "Fault Manager rejects the "
                                         "connection.");
            currChannel->disconnect();
            _channelMgr->closeChannel(peerId.getComponentId());
            return NULL;
         }

         INGwFtTkConnHandle *handle = new INGwFtTkConnHandle(peerId);

         if(_channelMgr->setHandler(peerId, handle) == 0)
         {
            logger.logMsg(ALWAYS_FLAG, 0, "Successfully registered channel for "
                                          "[%d]", peerId.getComponentId());
            _startReader(peerId.getComponentId());
            return handle;
         }

         delete handle;

         logger.logMsg(ERROR_FLAG, 0, "Unable to set the handler for peer [%d]",
                       peerId.getComponentId());
      }

      if(_faultMgr->checkEstablishFailedResponse(currChannel->clone()) == -1)
      {
         logger.logMsg(ERROR_FLAG, 0, "Fault Manager rejects the connection.");
         currChannel->disconnect();
         _channelMgr->closeChannel(peerId.getComponentId());
         return NULL;
      }

      currChannel->disconnect();
      _channelMgr->closeChannel(peerId.getComponentId());

      if(estRetStatus == INGwFtTkChannel::SIMULTANEOUS_OPEN)
      {
         retryCount++;

         logger.logMsg(ALWAYS_FLAG, 0, "Simultaneous open detected. peer [%d]",
                       peerId.getComponentId());

         if(_sysID > peerId.getComponentId())
         {
            sleep(2);
         }
         else
         {
            sleep(4);
         }
      }
      else
      {
         break;
      }
   }

   logger.logMsg(ERROR_FLAG, 0, "Unable to establish connection with peer "
                                "[%d]", peerId.getComponentId());
   return NULL;
}

void INGwFtTkCommunicator::closeConnection(INGwFtTkConnHandle *handle)
{
   if(handle == NULL)
   {
      logger.logMsg(ERROR_FLAG, 0, "NULL handle in closeConnection.");
      return;
   }

   logger.logMsg(ALWAYS_FLAG, 0, "App Close Connection for [%d]",
                 handle->getPeerID().getComponentId());

   INGwFtTkChannel *currChannel = _channelMgr->getChannel(handle);
   INGwFtTkRefObjHolder holder(currChannel);

   if((currChannel == NULL) || (!currChannel->isChannelValid()))
   {
      if(_channelMgr->closeChannel(handle) == -1)
      {
         logger.logMsg(ERROR_FLAG, 0, 
                       "Error closing channel with given handle.");
      }

      return;
   }

   if(_channelMgr->getChannelCount(handle->getPeerID().getComponentId()) == 1)
   {
      currChannel->setAppClose(true);

      INGwFtTkEndPointInfo peerInfo = currChannel->getEndPointInfo();

      _sendAction(handle->getPeerID().getComponentId(), 
                  INGwFtTkControlMsg::BTK_CLOSE_CONNECTION, &peerInfo);
   }

   _channelMgr->closeChannel(handle);

   logger.logMsg(ALWAYS_FLAG, 0, "Channel close successful.");
}

int INGwFtTkCommunicator::sendMessageMultiBufMsg(INGwFtTkConnHandle *handle, 
                                            unsigned char opCode,
                                            unsigned short dataStructureId,
                                            INGwFtTkMessageBuf *message,
                                            unsigned int no_of_message, 
                                            int version)
{
   INGwFtTkChannel *currChannel = _channelMgr->getChannel(handle);

   if(currChannel == NULL)
   {
      logger.logMsg(ERROR_FLAG, 0, "Unable to get the channel.");
      return -1;
   }

   INGwFtTkRefObjHolder holder(currChannel);

   if(no_of_message > (BTK_MAX_WRITE_ARY - 1))
   {
      logger.logMsg(ERROR_FLAG, 0, "Too many msg buffer. count [%d] max [%d]",
                    no_of_message, BTK_MAX_WRITE_ARY - 1);
      return -1;
   }

   int msgLen = 0;
   struct iovec iov[BTK_MAX_WRITE_ARY];

   for(int idx = 1, off = 0; idx <= no_of_message; idx++, off++)
   {
      iov[idx].iov_base = (caddr_t) message[off].msg;
      iov[idx].iov_len  = message[off].len;
      msgLen += message[off].len;
   }

   INGwFtTkMesgHeader header;

   switch(currChannel->getVersion())
   {
      case 1:
      {
         header.v1Msg.version = htonl(version);
         header.v1Msg.instanceID = htons(handle->getPeerID().getComponentId());
         header.v1Msg.subComponentID = 
                                 htons(handle->getPeerID().getSubComponentId());
         header.v1Msg.opCode          = htons(opCode);
         header.v1Msg.dataStructureID = htons(dataStructureId);
         header.v1Msg.msgLen          = htonl(msgLen);
         iov[0].iov_base = (caddr_t) &header.v1Msg;
         iov[0].iov_len  = sizeof(INGwFtTkMesgHeader_v1);
      }
      break;

      default:
      {
         logger.logMsg(ERROR_FLAG, 0, "Unsupported version negotiated. [%d]",
                       currChannel->getVersion());
         currChannel->disconnect();
         _channelMgr->closeChannel(handle->getPeerID().getComponentId());
         return -1;
      }
   }

   INGwFtTkChannel::INGwFtTkWriteErrorState errorState;

   if(currChannel->sendAry(iov, no_of_message + 1, errorState) == 0)
   {
      logger.logMsg(TRACE_FLAG, 0, "Successfully sent [%d] message buf.", 
                    no_of_message);

      INGwFtTkMsgHandlerInf *msgInf = currChannel->getMessageHandler();

      if(msgInf)
      {
         msgInf->multiMessageDelivered(handle, opCode, dataStructureId, message,
                                       no_of_message);
      }

      return 0;
   }


   logger.logMsg(ERROR_FLAG, 0, "Failed sending [%d] message buf.",
                 no_of_message);

   currChannel->disconnect();
   _channelMgr->closeChannel(handle->getPeerID().getComponentId());

   if(errorState == INGwFtTkChannel::WR_TIMEDOUT)
   {
      _faultMgr->faultIdentified(currChannel->clone());
   }

   return -1;
}


int INGwFtTkCommunicator::sendMessageMultiMsg(INGwFtTkConnHandle *handle, 
                                         unsigned char opCode,
                                         unsigned short dataStructureId,
                                         INGwFtTkMessageBuf *message,
                                         unsigned int no_of_message, 
                                         int msgVersion)
{
   logger.logMsg (TRACE_FLAG, 0,
     "Entering INGwFtTkCommunicator::sendMessageMultiMsg");

   INGwFtTkChannel *currChannel = _channelMgr->getChannel(handle);

   if(currChannel == NULL)
   {
      logger.logMsg(ERROR_FLAG, 0, "Unable to get the channel.");
      return -1;
   }

   INGwFtTkRefObjHolder holder(currChannel);

   const int max_messages = BTK_MAX_WRITE_ARY >> 1;

   INGwFtTkMesgHeader msgHeader[max_messages];
   struct iovec iov[BTK_MAX_WRITE_ARY];

   int version = currChannel->getVersion();

   for(int idx = 0; idx < no_of_message; idx += max_messages)
   {
      int idx_1 = idx, mcnt = 0, vcnt = 0;

      for(;idx_1 < no_of_message && mcnt < max_messages; 
          idx_1++, mcnt++, vcnt++, vcnt++)
      {
         switch(version)
         {
            case 1:
            {
               msgHeader[mcnt].v1Msg.version = htonl(msgVersion);
               msgHeader[mcnt].v1Msg.instanceID =
                                    htons(handle->getPeerID().getComponentId());
               msgHeader[mcnt].v1Msg.subComponentID  =
                                 htons(handle->getPeerID().getSubComponentId());
               msgHeader[mcnt].v1Msg.opCode          = htons(opCode);
               msgHeader[mcnt].v1Msg.dataStructureID = htons(dataStructureId);
               msgHeader[mcnt].v1Msg.msgLen = htonl(message[idx_1].len);

               iov[vcnt].iov_base = (caddr_t) (&(msgHeader[mcnt].v1Msg));
               iov[vcnt].iov_len  = sizeof(INGwFtTkMesgHeader_v1);
            }
            break;

            default:
            {
               logger.logMsg(ERROR_FLAG, 0, "Unsupported version negotiated. "
                                            "[%d]", currChannel->getVersion());
               currChannel->disconnect();
               _channelMgr->closeChannel(handle->getPeerID().getComponentId());
               return -1;
            }
         }

         iov[vcnt + 1].iov_base = (caddr_t) message[idx_1].msg;
         iov[vcnt + 1].iov_len  = message[idx_1].len;

         logger.logMsg (VERBOSE_FLAG, 0,
           "Populatnig iovec: TotalMsg <%d> index <%d, %d> length <%d>",
           no_of_message, idx_1, idx, message[idx_1].len);
      }

      INGwFtTkChannel::INGwFtTkWriteErrorState errorState;

      if(currChannel->sendAry(iov, vcnt, errorState) == 0)
      {
         logger.logMsg(TRACE_FLAG, 0, "Successfully sent [%d] message bulk",
                       mcnt);
         continue;
      }

      logger.logMsg(ERROR_FLAG, 0, "Error sending [%d] message bulk",
                    mcnt);
   
      currChannel->disconnect();
      _channelMgr->closeChannel(handle->getPeerID().getComponentId());

      if(errorState == INGwFtTkChannel::WR_TIMEDOUT)
      {
         _faultMgr->faultIdentified(currChannel->clone());
      }

      return -1;
   }

   INGwFtTkMsgHandlerInf *msgInf = currChannel->getMessageHandler();
   if(msgInf)
   {
      for(int idx = 0; idx < no_of_message; idx++)
      {
         msgInf->multiMessageDelivered(handle, opCode, dataStructureId, 
                                       message + idx, 1);
      }
   }

   logger.logMsg (TRACE_FLAG, 0,
     "Leaving INGwFtTkCommunicator::sendMessageMultiMsg");
   return 0;
}

INGwFtTkReceiverQueue * INGwFtTkCommunicator::registerMessageReceiver(ObjectId selfID)
{
   if(selfID.getSubComponentId() == 0)
   {
      logger.logMsg(ERROR_FLAG, 0, "Subcomponent ID cant be zero.");
      return NULL;
   }

   return _subCompMgr->registerMessageReceiver(selfID.getSubComponentId());
}

void INGwFtTkCommunicator::deregisterMessageReceiver(INGwFtTkReceiverQueue *inQueue)
{
   _subCompMgr->unregisterMessageReceiver(inQueue);
   return;
}

void INGwFtTkCommunicator::registerFaultCallback(INGwFtTkFaultHandlerInf *inCallBk)
{
   _faultCallback = inCallBk;
}

void INGwFtTkCommunicator::deregisterFaultCallback()
{
   _faultCallback = NULL;
}

int INGwFtTkCommunicator::registerIncomingWatcher(unsigned int peerID, 
                                             INGwFtTkConnHandlerInf *inf)
{
   return _channelMgr->registerIncomingWatcher(peerID, inf);
}

int INGwFtTkCommunicator::deregisterIncomingWatcher(unsigned int peerID)
{
   return _channelMgr->deregisterIncomingWatcher(peerID);
}

int INGwFtTkCommunicator::sendAction(unsigned int peerID, unsigned int actionID)
{
   if(actionID < BTK_MIN_APP_ACTION)
   {
      logger.logMsg(ERROR_FLAG, 0, "Action [%d] is reserved. Min action [%d]",
                    actionID, BTK_MIN_APP_ACTION);
      return -1;
   }

   INGwFtTkEndPointInfo peerDetail;

   if(_getINGwFtTkDetail(peerID, &peerDetail) == -1)
   {
      logger.logMsg(ERROR_FLAG, 0, "Unable to get proper peer detail from "
                                   "BMgr for [%d]", peerID);
      return -1;
   }

   _faultMgr->sentAction(peerID);

   return _sendAction(peerID, actionID, &peerDetail);
}

int INGwFtTkCommunicator::_sendAction(unsigned int peerID, unsigned int actionID,
                                 INGwFtTkEndPointInfo *peerDetail)
{
   INGwFtTkChannel *currChannel = new INGwFtTkChannel(peerID, 0, 0);

   if(currChannel == NULL)
   {
      logger.logMsg(ERROR_FLAG, 0, "Unable to create channel.");
      return -1;
   }

   INGwFtTkRefObjHolder holder(currChannel);

   currChannel->setEndPointInfo(*peerDetail);

   INGwFtTkChannel::INGwFtTkActionSentState actRetStatus;

   if(currChannel->sendAction(*_selfAbout, actionID, actRetStatus) == 0)
   {
      logger.logMsg(ALWAYS_FLAG, 0, "Action [%d] successfully sent to peer "
                                    "[%d]", actionID, peerID);
      return 0;
   }

   logger.logMsg(ALWAYS_FLAG, 0, "Action [%d] to peer [%d] failed.",
                 actionID, peerID);
   return -1;
}

int INGwFtTkCommunicator::negotiateVersion(const ObjectId &peer, 
                                      const INGwFtTkVersionSet &supportedVersion)
{
   int peerID = peer.getComponentId();
   INGwFtTkEndPointInfo peerDetail;

   if(_getINGwFtTkDetail(peerID, &peerDetail) == -1)
   {
      logger.logMsg(ERROR_FLAG, 0, "Unable to get proper peer detail from "
                                   "BMgr for [%d]", peerID);
      return 0;
   }

   INGwFtTkChannel *currChannel = new INGwFtTkChannel(peerID, 0, 0);

   if(currChannel == NULL)
   {
      logger.logMsg(ERROR_FLAG, 0, "Unable to create channel.");
      return 0;
   }

   INGwFtTkRefObjHolder holder(currChannel);

   currChannel->setEndPointInfo(peerDetail);

   int ret = currChannel->negotiateVersion(*_selfAbout, 
                                           peer.getSubComponentId(), 
                                           supportedVersion);
   if(ret == 0)
   {
      logger.logMsg(ALWAYS_FLAG, 0, "No compatible versions. [%d-%d]", peerID, 
                    peer.getSubComponentId());
   }
   else
   {
      logger.logMsg(ALWAYS_FLAG, 0, "Version [%d] negotiated successfully.",
                    ret);
   }

   return ret;
}

int INGwFtTkCommunicator::_getINGwFtTkDetail(unsigned int peerID, 
                                   INGwFtTkEndPointInfo *peerDetail)
{
   try
   {
      RSIEmsTypes::ConfigurationDetail_var ipDetail = 
               BayAgentImpl::getInstance().getConfigParam(peerID, cmNP_IP_ADDR);
      RSIEmsTypes::ConfigurationDetail_var portDetail = 
          BayAgentImpl::getInstance().getConfigParam(peerID, cmNP_UDP_PORT_NUM);

      peerDetail->IP   = ntohl(inet_addr(ipDetail->paramValue.in()));
      peerDetail->port = atoi(portDetail->paramValue.in());

      if((peerDetail->IP == -1) || (peerDetail->port == 0))
      {
         logger.logMsg(ERROR_FLAG, 0, "Wrong detail IP [%s] port [%s]", 
                       ipDetail->paramValue.in(), portDetail->paramValue.in());
         return -1;
      }

      return 0;
   }
   catch(...)
   {
      logger.logMsg(ERROR_FLAG, 0, "Exception while getting peer detail.");
   }

   return -1;
}

extern "C" void * INGwFtTkCommunicator::_listenerThreadStart(void *)
{
   setSignal();
   pthread_detach(pthread_self());

   logger.logMsg(ALWAYS_FLAG, 0, "Listener thread is active.");

   INGwFtTkCommunicator::getInstance()._listen();

   logger.logMsg(ALWAYS_FLAG, 0, "Listener quits");
   printf("Listener quits. Quitting..\n");
   raise(9);
   return NULL;
}

int INGwFtTkCommunicator::_initialize()
{
   INGwFtTkEndPointInfo myInfo;

   if(_getINGwFtTkDetail(_sysID, &myInfo) == -1)
   {
      logger.logMsg(ERROR_FLAG, 0, "Unable to retrieve detail from "
                                   "BayManager.");
      return -1;
   }

   int listenSock = socket(AF_INET, SOCK_STREAM, 0);

   if(listenSock == -1)
   {
      logger.logMsg(ERROR_FLAG, 0, "Unable to create socket. [%s]", 
                    strerror(errno));
      return -1;
   }

   sockaddr_in mydetail;
   memset(&mydetail, 0, sizeof(sockaddr_in));

   mydetail.sin_family = AF_INET;
   mydetail.sin_addr.s_addr = htonl(myInfo.IP);
   mydetail.sin_port = 0;

   if(bind(listenSock, (struct sockaddr *)&mydetail, sizeof(sockaddr_in)) == -1)
   {
      logger.logMsg(ERROR_FLAG, 0, "Unable to bind to IP. [%s]", 
                    strerror(errno));
      return -1;
   }

   memset(&mydetail, 0, sizeof(sockaddr_in));
   socklen_t len = sizeof(sockaddr_in);

   if(getsockname(listenSock, (struct sockaddr *)&mydetail, &len) == -1)
   {
      logger.logMsg(ERROR_FLAG, 0, "Unable to retrieve socket info. [%s]",
                    strerror(errno));
      return -1;
   }

   if(listen(listenSock, 5) == -1)
   {
      logger.logMsg(ERROR_FLAG, 0, "listen failed. [%s]", strerror(errno));
      return -1;
   }

   myInfo.port = ntohs(mydetail.sin_port);
   _selfAbout->listenDetail = myInfo;

   _listenChannel = new INGwFtTkChannel(_sysID, listenSock, 0);
   _listenChannel->setInstance(_selfAbout->instanceID);
   _listenChannel->setEndPointInfo(myInfo);

   pthread_t listenerThread;
   pthread_create(&listenerThread, NULL, _listenerThreadStart, NULL);

   char portStr[20];
   sprintf(portStr, "%d", ntohs(mydetail.sin_port));

   ImMediateTypes::NameValueType nvType;

   nvType.oid = (const char *)cmNP_UDP_PORT_NUM;
   nvType.value <<= (const char *)portStr;

   try
   {
      BayAgentImpl::getInstance().modifyCfgParam(
                                 ImMediateTypes::OperationType_Modify, &nvType);
   }
   catch(...)
   {
      logger.logMsg(ERROR_FLAG, 0, "Error reporting portdetail to BayManager.");
      return -1;
   }

   struct in_addr tempAddr;
   tempAddr.s_addr = htonl(myInfo.IP);

   char ipAddr[40];
   strcpy(ipAddr, inet_ntoa(tempAddr));

   unsigned int subnetMask = 0;
   if(true == getNetMaskForIp(ipAddr, subnetMask))
   {
      logger.logMsg(ALWAYS_FLAG, 0, "Notifying lan [%x] subnet [%x]",
                    htonl(myInfo.IP), subnetMask);
      try 
      {
        BayAgentImpl::getInstance().notifyLan(htonl(myInfo.IP), subnetMask, 
                                              RSIEmsTypes::ConnectionState_UP);
      } 
      catch(...) 
      {
        logger.logMsg(ERROR_FLAG, 0, "Error notifying Lan to BayManager.");
        return -1;
      }
   }
   else
   {
      logger.logMsg(ERROR_FLAG, 0, "Error getting subnet for [%s]", ipAddr);
   }

   logger.logMsg(ALWAYS_FLAG, 0, "Successfully initialized INGwFtTalk "
                                 "Communicator.");
   return 0;
}

extern "C" void * INGwFtTkCommunicator::_readerThreadStart(void *data)
{
   setSignal();
   pthread_detach(pthread_self());

   int peerID = *(int *)data;
   delete (int *)data;

   logger.logMsg(ALWAYS_FLAG, 0, "Reader for [%d] is active", peerID);
   // Raising an alarm from INGwFtTalk would not let us differentiate the 
	 //		Alarm behaviour for Slee and CCM as Peer subsystems : [
	 //	logger.logAlarm(__LINE__, BT_PEER_CONN_UP, peerID, "Link to peer [%d] up", peerID);

   INGwFtTkCommunicator::getInstance()._read(peerID);

   //logger.logAlarm(__LINE__, BT_PEER_CONN_DOWN, peerID, "Link to peer [%d] down", peerID);
	 //	: ]

   logger.logMsg(ALWAYS_FLAG, 0, "Reader for [%d] quits.", peerID);
   return NULL;
}

void INGwFtTkCommunicator::_startReader(unsigned int peerID)
{
   int *data = new int;
   *data = peerID;

   pthread_t readerThread;
   pthread_create(&readerThread, NULL, _readerThreadStart, data);
}

void INGwFtTkCommunicator::_listen()
{
   INGwFtTkControlMsg response = *_selfAbout;
   INGwFtTkChannel::INGwFtTkWriteErrorState wrResult;

   while(true)
   {
      int acceptedSock = -1;
      int acceptedVer = 0;

      INGwFtTkChannel::INGwFtTkListenErrorState retStatus;

      INGwFtTkControlMsg msg = _listenChannel->listen(acceptedSock, acceptedVer,
                                                 retStatus);

      if(retStatus == INGwFtTkChannel::LIS_SOCKET_ERROR)
      {
         logger.logMsg(ERROR_FLAG, 0, "Listen returns error.");
         break;
      }

      logger.logMsg(ALWAYS_FLAG, 0, "Received control msg. sock [%d] peer [%d] "
                                    "instance [%d] action [%d] version [%d]", 
                    acceptedSock, msg.peerID, msg.instanceID.startTime, 
                    msg.action, acceptedVer);

      if(msg.peerID == _selfAbout->peerID)
      {
         logger.logMsg(ERROR_FLAG, 0, "Control msg from self.");
         response.result = INGwFtTkControlMsg::BTK_CONN_REJECT;
         _listenChannel->sendControlMsg(response, 5, wrResult, acceptedSock, 
                                        acceptedVer);
         close(acceptedSock);
         continue;
      }

      if(msg.action < 25)
      {
         if(msg.action == INGwFtTkControlMsg::BTK_OPEN_CONNECTION)
         {
            if(_faultMgr->checkAcceptedConn(msg) == -1)
            {
               logger.logMsg(ALWAYS_FLAG, 0, "FaultMgr rejects the conn.");
               response.result = INGwFtTkControlMsg::BTK_RECONN_REJECT;
               _listenChannel->sendControlMsg(response, 5, wrResult, 
                                              acceptedSock, acceptedVer);
               close(acceptedSock);
               continue;
            }

            ObjectId peerObj(msg.peerID, 0); 
            INGwFtTkChannelManager::INGwFtTkCreationStatus creationStatus;
            INGwFtTkChannel *currChannel = _channelMgr->openChannel(peerObj, 
                                                               acceptedSock, 
                                                               acceptedVer,
                                                               creationStatus);
            if(currChannel == NULL)
            {
               logger.logMsg(ALWAYS_FLAG, 0, "Unable to open channel.");
               response.result = INGwFtTkControlMsg::BTK_CONN_REJECT;
               _listenChannel->sendControlMsg(response, 5, wrResult, 
                                              acceptedSock, acceptedVer);
               close(acceptedSock);
               continue;
            }

            INGwFtTkRefObjHolder holder(currChannel);
            if(creationStatus == INGwFtTkChannelManager::ALREADY_EXIST)
            {
               logger.logMsg(ALWAYS_FLAG, 0, "Channel pre-exists.");
               response.result = INGwFtTkControlMsg::BTK_SIMULTANEOUS_CONN;
               _listenChannel->sendControlMsg(response, 5, wrResult, 
                                              acceptedSock, acceptedVer);
               close(acceptedSock);

               if(_faultMgr->isReaderAvailable(msg.peerID))
               {
                  logger.logMsg(ERROR_FLAG, 0, 
                                "Open connection request received from the "
                                "subsystem whose channel is still open.");
                  logger.logMsg(ALWAYS_FLAG, 0, "Quitting deliberatly");
                  printf("BTK Open conn request for an active channel.\n");
                  raise(9);
                  sleep(5);
               }

               continue;
            }

            if(_faultMgr->isReaderAvailable(msg.peerID))
            {
               logger.logMsg(ALWAYS_FLAG, 0, "Channel partially active.");

               sleep(2);

               if(_faultMgr->isReaderAvailable(msg.peerID))
               {
                  response.result = INGwFtTkControlMsg::BTK_CONN_REJECT;
                  _listenChannel->sendControlMsg(response, 5, wrResult, 
                                                 acceptedSock, acceptedVer);
                  close(acceptedSock);

                  logger.logMsg(ERROR_FLAG, 0, 
                                "Open connection request received from the "
                                "subsystem whose channel is still closed but "
                                "reader is active.");
                  logger.logMsg(ALWAYS_FLAG, 0, "Quitting deliberatly");
                  printf("BTK Open conn request for an active channel.\n");
                  raise(9);
                  sleep(5);
               }
            }

            currChannel->setInstance(msg.instanceID);
            currChannel->setEndPointInfo(msg.listenDetail);

            response.result = INGwFtTkControlMsg::BTK_CONN_ACCEPTED;
            _listenChannel->sendControlMsg(response, 5, wrResult, 
                                           acceptedSock, acceptedVer);
            logger.logMsg(ALWAYS_FLAG, 0, "Channel accepted.");

            _channelMgr->channelAccepted(msg.peerID);

            _startReader(msg.peerID);
            continue;
         }
         else if(msg.action == INGwFtTkControlMsg::BTK_FAULT_MGR_QUERY)
         {
            _faultMgr->checkAcceptedConn(msg);

            response.result = INGwFtTkControlMsg::BTK_QUERY_SUCCESS;
            _listenChannel->sendControlMsg(response, 5, wrResult, 
                                           acceptedSock, acceptedVer);
            close(acceptedSock);
            logger.logMsg(ALWAYS_FLAG, 0, "FaultMgr query replied.");
            continue;
         }
         else if(msg.action == INGwFtTkControlMsg::BTK_VERSION_NEGOTIATION)
         {
            if(_versionInf == NULL)
            {
               response.result = 0;
               _listenChannel->sendControlMsg(response, 5, wrResult, 
                                              acceptedSock, acceptedVer);
               close(acceptedSock);
               logger.logMsg(ALWAYS_FLAG, 0, "No Version handler to handle "
                                             "request.");
               continue;
            }

            _handleVersionQuery(msg.peerID, acceptedSock, acceptedVer);
            continue;
         }
         else if(msg.action == INGwFtTkControlMsg::BTK_CLOSE_CONNECTION)
         {
            logger.logMsg(ALWAYS_FLAG, 0, "Close Connection for [%d] Recevied", 
                          msg.peerID);

            INGwFtTkChannel *currChannel = _channelMgr->getChannel(msg.peerID);
            INGwFtTkRefObjHolder holder(currChannel);

            if(currChannel)
            {
               currChannel->setAppClose(true);
            }

            response.result = INGwFtTkControlMsg::BTK_ACTION_ACCEPTED;
            _listenChannel->sendControlMsg(response, 5, wrResult, 
                                           acceptedSock, acceptedVer);
            close(acceptedSock);

            if(currChannel)
            {
               currChannel->disconnect();
            }
         }
         else
         {
            logger.logMsg(ERROR_FLAG, 0, "Unknown action [%d] Recevied", 
                          msg.action);
            response.result = INGwFtTkControlMsg::BTK_CONN_REJECT;
            _listenChannel->sendControlMsg(response, 5, wrResult, 
                                           acceptedSock, acceptedVer);
            close(acceptedSock);
            continue;
         }
      }
      else if(msg.action >= 50 && msg.action < 500)
      {
         _faultMgr->receivedAction(msg.peerID);
         response.result = INGwFtTkControlMsg::BTK_ACTION_ACCEPTED;
         _listenChannel->sendControlMsg(response, 5, wrResult, 
                                        acceptedSock, acceptedVer);
         close(acceptedSock);
         _notifyAction(msg.peerID, msg.action);
         continue;
      }
      else
      {
         logger.logMsg(ERROR_FLAG, 0, "Unknown action [%d]", msg.action);
         response.result = INGwFtTkControlMsg::BTK_CONN_REJECT;
         _listenChannel->sendControlMsg(response, 5, wrResult, 
                                        acceptedSock, acceptedVer);
         close(acceptedSock);
         continue;
      }
   }

   return;
}

void INGwFtTkCommunicator::_read(unsigned int peerID)
{
   INGwFtTkChannel *currChannel = _channelMgr->getChannel(peerID);

   if(currChannel == NULL)
   {
      logger.logMsg(ERROR_FLAG, 0, "No channel for peer [%d] found.", peerID);
      return;
   }

   INGwFtTkRefObjHolder holder(currChannel);

   _faultMgr->readerStarted(currChannel->clone());
   try 
   {
     BayAgentImpl::getInstance().notifyLink(peerID, 
                                            RSIEmsTypes::ConnectionState_UP);
   }
   catch(...)
   {
     logger.logMsg(ERROR_FLAG, 0, "Error notifying link UP to BayManager for "
                   "peer <%d>.", peerID);
     return;
   }

   INGwFtTkChannel::INGwFtTkReadErrorState retStatus;
   currChannel->readMsg(_subCompMgr, retStatus);

   currChannel->disconnect();
   _channelMgr->closeChannel(peerID);

   _faultMgr->readerStopped(currChannel->clone());

   try 
   {
     if(currChannel->isAppClose())
     {
        BayAgentImpl::getInstance().deleteLink(peerID);
     }
     else
     {
       BayAgentImpl::getInstance().notifyLink(
                                     peerID,RSIEmsTypes::ConnectionState_DOWN);
     }
   }
   catch(...)
   {
     logger.logMsg(ERROR_FLAG, 0, "Error notifying link DOWN to BayManager "
                   "for peer <%d>.", peerID);
     return;
   }

   logger.logMsg(ALWAYS_FLAG, 0, "Reader for [%d] returns.", peerID);
   return;
}

void INGwFtTkCommunicator::setSignal()
{
   sigignore(SIGCHLD);
   sigignore(SIGPIPE);
   sigignore(SIGALRM);
   //sigignore(SIGINT);
}

extern "C" void * INGwFtTkCommunicator::_actionNotifier(void *actionDetail)
{
   setSignal();
   pthread_detach(pthread_self());

   unsigned int *data = (unsigned int *)actionDetail;

   unsigned int peerID = data[0];
   unsigned int actionID = data[1];
   delete []data;

   if(INGwFtTkCommunicator::getInstance()._faultCallback)
   {
      logger.logMsg(ALWAYS_FLAG, 0, "Notifying action [%d] from [%d].",
                    actionID, peerID);
      INGwFtTkCommunicator::getInstance()._faultCallback->doAction(peerID, actionID);
      logger.logMsg(ALWAYS_FLAG, 0, "Action [%d] from [%d] notified.",
                    actionID, peerID);
   }
   else
   {
      logger.logMsg(ERROR_FLAG, 0, "No callback registered to propagate "
                                   "action [%d] from [%d]", actionID, peerID);
   }

   return NULL;
}

void INGwFtTkCommunicator::_notifyAction(unsigned int peerID, unsigned int actionID)
{
   if(_faultCallback == NULL)
   {
      logger.logMsg(ALWAYS_FLAG, 0, "No Fault callback inf registered.");
      logger.logMsg(ALWAYS_FLAG, 0, "Action [%d] from peer [%d] discarded.",
                    actionID, peerID);
      logger.logMsg(ALWAYS_FLAG, 0, "Quitting deliberately.");
      printf("INGwFtTk: No callbacks regitered for actions. Quitting.\n");
      raise(9);
      sleep(5);
      return;
   }

   unsigned int *data = new unsigned int[2];
   data[0] = peerID;
   data[1] = actionID;

   pthread_t notifier;
   pthread_create(&notifier, NULL, _actionNotifier, data);
}

const INGwFtTkVersionSet & INGwFtTkCommunicator::getVersionSet()
{
   return _versionSet;
}

extern "C" void * INGwFtTkCommunicator::_versionHandlerThreadStart(void *sockDetail)
{
   setSignal();
   pthread_detach(pthread_self());

   int *data = (int *)sockDetail;

   int peerID = data[0];
   int acceptedSock = data[1];
   int acceptedVer = data[2];
   delete []data;

   INGwFtTkControlMsg response = *(INGwFtTkCommunicator::getInstance()._selfAbout);
   INGwFtTkChannel::INGwFtTkWriteErrorState wrResult;

   int subComponentID = 0;
   INGwFtTkVersionSet peerVersions;

   INGwFtTkChannel *listenChannel = INGwFtTkCommunicator::getInstance()._listenChannel;
   INGwFtTkVersionHandlerInf *versionInf = 
                                     INGwFtTkCommunicator::getInstance()._versionInf;

   INGwFtTkChannel::INGwFtTkReadErrorState rdResult;
   if(listenChannel->readVersionInfo(subComponentID, peerVersions, 5, rdResult,
                                     acceptedSock, acceptedVer) == -1)
   {
      response.result = 0;
      listenChannel->sendControlMsg(response, 5, wrResult, 
                                    acceptedSock, acceptedVer);
      close(acceptedSock);
      logger.logMsg(ALWAYS_FLAG, 0, "Error reading peer version details.");
      return NULL;
   }

   if(versionInf == NULL)
   {
      response.result = 0;
      listenChannel->sendControlMsg(response, 5, wrResult, 
                                    acceptedSock, acceptedVer);
      close(acceptedSock);
      logger.logMsg(ALWAYS_FLAG, 0, "No Version handler to handle request.");
      return NULL;
   }

   std::string peerVerStr = peerVersions.toString();

   ObjectId id(peerID, subComponentID);

   response.result = versionInf->negotiateVersion(id, peerVersions);

   if(!peerVersions.contains(response.result))
   {
      logger.logMsg(ERROR_FLAG, 0, "Negotiated version [%d] for subComp [%d] "
                                   "not in peerVersionSet. [%s]", 
                    response.result, subComponentID, peerVerStr.c_str());
      response.result = 0;
   }

   if(response.result == 0)
   {
      logger.logMsg(ERROR_FLAG, 0, "Negotiated failed for subComp [%d] "
                                   "peerVersionSet. [%s]", 
                    subComponentID, peerVerStr.c_str());
   }
   else
   {
      logger.logMsg(ALWAYS_FLAG, 0, "Negotiated version [%d] for subComp [%d] "
                                    "successfully. peerVersionSet [%s]", 
                    response.result, subComponentID, peerVerStr.c_str());
   }

   listenChannel->sendControlMsg(response, 5, wrResult, 
                                 acceptedSock, acceptedVer);
   close(acceptedSock);
   return NULL;
}

void INGwFtTkCommunicator::_handleVersionQuery(int peerID, int acceptedSock, 
                                          int acceptedVer)
{
   int *data = new int[3];
   data[0] = peerID;
   data[1] = acceptedSock;
   data[2] = acceptedVer;

   pthread_t versionHandler;
   pthread_create(&versionHandler, NULL, _versionHandlerThreadStart, data);
}

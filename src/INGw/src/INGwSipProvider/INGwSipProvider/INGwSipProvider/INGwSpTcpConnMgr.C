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
//     File:     INGwSpTcpConnMgr.C
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
#include <stdlib.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <errno.h>
#include <unistd.h>
#include <fcntl.h>
#include <INGwSipProvider/INGwSpTcpConnMgr.h>
#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpSipContext.h>
#include <INGwSipProvider/INGwSpSipStackIntfLayer.h>
#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>

/**************************************************************************
* Function     : signalHandler
*
* Description  :
*
* Parameters   :
*   Name             Type             Description
*-------------------------------------------------------------------------
*
* Return Value :
*   Type              Value         Description
*-------------------------------------------------------------------------
*
**************************************************************************/
extern "C" void
INGwSpTcpConnMgr::signalHandler(int    p_SigId)
{
	return;
}


/**************************************************************************
* Function     : instance
*
* Description  :
*
* Parameters   :
*   Name             Type             Description
*-------------------------------------------------------------------------
*
* Return Value :
*   Type              Value         Description
*-------------------------------------------------------------------------
*
**************************************************************************/
INGwSpTcpConnMgr&
INGwSpTcpConnMgr::instance()
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnMgr::instance");
	static INGwSpTcpConnMgr	feConnMgr;
  LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::instance");
	return feConnMgr;
}

/**************************************************************************
* Function     : listenerThdMain
*
* Description  :
*
* Parameters   :
*   Name             Type             Description
*-------------------------------------------------------------------------
*
* Return Value :
*   Type              Value         Description
*-------------------------------------------------------------------------
*
**************************************************************************/
extern "C" void*
INGwSpTcpConnMgr::listenerThdMain(void	*p_Context)
{
	INGwSpTcpConnMgr::instance().startListening();
	return 0;
}


/**************************************************************************
* Function     : msgProcessingThd
*
* Description  :
*
* Parameters   :
*   Name             Type             Description
*-------------------------------------------------------------------------
*
* Return Value :
*   Type              Value         Description
*-------------------------------------------------------------------------
*
**************************************************************************/
extern "C" void*
INGwSpTcpConnMgr::msgProcessingThd(void	*p_Context)
{

	ProcessingThreadDetails* processingDetails = (ProcessingThreadDetails*)p_Context;
	processingDetails->startProcessing();
	return 0;
}

/**************************************************************************
* Function     : start
*
* Description  :
*
* Parameters   :
*   Name             Type             Description
*-------------------------------------------------------------------------
*
* Return Value :
*   Type              Value         Description
*-------------------------------------------------------------------------
*
**************************************************************************/
int
INGwSpTcpConnMgr::start()
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnMgr::start");

	if ((mStartFlag == 0) && (mIsInitialized))
	{

		pthread_create(&mThreadId, 0, INGwSpTcpConnMgr::listenerThdMain, 0);
		pthread_detach(mThreadId);

		LogINGwVerbose(false, 0, "INGwSpTcpConnMgr::start : listener created....");

		mStartFlag	= 1;

    LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::start");
		return CONN_MGR_SUCCESS;
	}
  LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::start");
	return CONN_MGR_FAILURE;
}

/**************************************************************************
* Function     : startListening
*
* Description  :
*
* Parameters   :
*   Name             Type             Description
*-------------------------------------------------------------------------
*
* Return Value :
*   Type              Value         Description
*-------------------------------------------------------------------------
*
**************************************************************************/
int
INGwSpTcpConnMgr::startListening()
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnMgr::startListening");

	updateFdSet();
  logger.logINGwMsg(false, TRACE_FLAG, 0,
	                 "Listener Fd:%d, MaxSockNum:%d", mListenerSock, 
	                  mMaxSockNum);

	while(mShutDownFlag == false)
	{

		if (select((mMaxSockNum + 1), &mSockFdSet, 0, 0, 0) < 0)
		{
      logger.logINGwMsg(false, ERROR_FLAG, 0,
			                  "SELECT ERROR, errno:%d, errTxt:%s",
												errno,strerror(errno));
			updateFdSet();
			continue;
		}

		if (mListenerSock != -1)
		{
			if (FD_ISSET(mListenerSock, &mSockFdSet))
			{
				LogINGwVerbose(false, 0, 
                      "INGwSpTcpConnMgr::startListening : "
                      "Rcvd Connect Request");
				processListenReq();
				updateFdSet();
				continue;
			}
		}

		LogINGwVerbose(false, 0, "INGwSpTcpConnMgr::startListening : "
									"Rcvd DATA Request");
		recvData();
		updateFdSet();
	}

  LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::startListening");
	return CONN_MGR_SUCCESS;
}

/**************************************************************************
* Function     : bindListener
*
* Description  :
*
* Parameters   :
*   Name             Type             Description
*-------------------------------------------------------------------------
*
* Return Value :
*   Type              Value         Description
*-------------------------------------------------------------------------
*
**************************************************************************/
int
INGwSpTcpConnMgr::bindListener()
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnMgr::bindListener");

	if ((mListenerSock = socket(AF_INET, SOCK_STREAM, 0)) < 0)
	{
    logger.logINGwMsg(false, ERROR_FLAG, 0,
		                  "SOCKET CREATION ERROR, errno:%d, errTxt:%s", 
											errno, strerror(errno));

    LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::bindListener");
		return CONN_MGR_FAILURE;
	}
	else
	{
		setSockNonBlocking(mListenerSock);
	}

  int flag = 1;

  if (setsockopt(mListenerSock, SOL_SOCKET, SO_REUSEADDR, &flag,
														  sizeof(flag)) < 0)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0,
		                  "SETSOCKOPT-SO_REUSEADDR ERROR, errno:%d, errTxt:%s", 
											errno, strerror(errno));
  }

#ifdef _DISABLE_LINGER_
	struct linger	lng;
	bzero(&lng, sizeof(lng));

  if (setsockopt(mListenerSock, SOL_SOCKET, SO_LINGER, &lng,
														  sizeof(lng)) < 0)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0,
		                  "SETSOCKOPT-SO_LINGER ERROR, errno:%d, errTxt:%s", 
											errno, strerror(errno));
  }
#endif //  _DISABLE_LINGER_

  mSelfSockAddr.sin_family         = AF_INET;
  mSelfSockAddr.sin_addr.s_addr    = inet_addr(mSelfIpAddr);
  mSelfSockAddr.sin_port           = htons(mSelfPort);

  if (bind(mListenerSock, (struct sockaddr *)&mSelfSockAddr, sizeof(mSelfSockAddr)) < 0)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0,
		                  "ADDR BIND ERROR, errno:%d, errTxt:%s",
											errno, strerror(errno));

    LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::bindListener");
		return CONN_MGR_FAILURE;
  }

  logger.logINGwMsg(false, VERBOSE_FLAG, 0,
	                  "Listener Binded, BindIP:%s, BindPort:%d", 
										inet_ntoa(mSelfSockAddr.sin_addr), ntohs(mSelfSockAddr.sin_port));

  if (listen(mListenerSock, 5) < 0)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0,
		                  "LISTEN ERROR, errno:%d, errTxt:%s", 
											errno, strerror(errno));

    LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::bindListener");
		return CONN_MGR_FAILURE;
  }

  LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::bindListener");
	return CONN_MGR_SUCCESS;
}

/**************************************************************************
* Function     : stop
*
* Description  :
*
* Parameters   :
*   Name             Type             Description
*-------------------------------------------------------------------------
*
* Return Value :
*   Type              Value         Description
*-------------------------------------------------------------------------
*
**************************************************************************/
int
INGwSpTcpConnMgr::stop()
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnMgr::stop");
	
  mShutDownFlag = true;

  LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::stop");
	return CONN_MGR_SUCCESS;
}

/**************************************************************************
* Function     : initialize
*
* Description  :
*
* Parameters   :
*   Name             Type             Description
*-------------------------------------------------------------------------
*
* Return Value :
*   Type              Value         Description
*-------------------------------------------------------------------------
*
**************************************************************************/
int
INGwSpTcpConnMgr::initialize(const	char* 	p_IpAddr, 
					                   const	int 	  p_IpPort,
					                   const	int   	p_MaxConn,
                             INGwSpSipListenerThread* p_ListenerThread)
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnMgr::initialize");

	if (mIsInitialized == 0)
	{
		strcpy(mSelfIpAddr, p_IpAddr);
		mSelfPort = p_IpPort;


		mMaxConn	   = p_MaxConn;
		mClientSockList   = new SockInfo[mMaxConn];
    mSipListenerThread = p_ListenerThread;


		mIsInitialized = 1;

    INGwSpTcpConnection::initINGwNWparam();

    LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::initialize");
		return CONN_MGR_SUCCESS;
	}
	else
	{
    LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::initialize");
		return CONN_MGR_FAILURE;
	}
}
/**************************************************************************
* Function     : getChannelIndex
*
* Description  :
*
* Parameters   :
*   Name             Type             Description
*-------------------------------------------------------------------------
*
* Return Value :
*   Type              Value         Description
*-------------------------------------------------------------------------
*
**************************************************************************/
int
INGwSpTcpConnMgr::getChannelIndex(const char*	p_ClientIp)
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnMgr::getChannelIndex");

	for (int i = 0; i < mMaxConn; ++i)
	{
		if (mClientSockList[i].sockFd != -1)
    {
			if (strcmp(mClientSockList[i].clientId, p_ClientIp) == 0)
      {

        LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::getChannelIndex");
				return i;
      }
    }
	}

  LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::getChannelIndex");
	return CONN_MGR_FAILURE;
}

/**************************************************************************
* Function     : processListenReq
*
* Description  :
*
* Parameters   :
*   Name             Type             Description
*-------------------------------------------------------------------------
*
* Return Value :
*   Type              Value         Description
*-------------------------------------------------------------------------
*
**************************************************************************/
int
INGwSpTcpConnMgr::processListenReq()
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnMgr::processListenReq");

	int 	msgCatg = -1;
	int		dataLen = 0;
	char*	recvBuff= 0;

	m_PeerAddrLen	= sizeof(mPeerSockAddr);
	bzero(&mPeerSockAddr, m_PeerAddrLen);

	int newSock 
			= accept(mListenerSock,(struct sockaddr*)&mPeerSockAddr,&m_PeerAddrLen);

	if (newSock < 0)
	{
    logger.logINGwMsg(false, ERROR_FLAG, 0,
		                 "ACCEPT ERROR, errno:%d", errno);

    LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::processListenReq");
		return CONN_MGR_FAILURE;
	}
	else
	{
		setSockNonBlocking(newSock);
	}

  char* peerIpAdd = inet_ntoa(mPeerSockAddr.sin_addr);

  logger.logINGwMsg(false, ALWAYS_FLAG, 0,
	                  "Accepted new connection, "
										"NewSock:%d, PeerIp:%s, PeerPort:%d", 
	                   newSock, peerIpAdd, mPeerSockAddr.sin_port); 

  // To update SockInfo* array using peer ip address
  updateSockInfoList(peerIpAdd, mPeerSockAddr.sin_port, 
										 SOCK_TYPE_ACCEPTED_CONN, newSock);

	// Put the connection in map
	INGwSpTcpConnection* tcpConn = new INGwSpTcpConnection();

	INGwIfrUtlRefCount_var ltcpConnVar(tcpConn);

	tcpConn->initialize(peerIpAdd, mPeerSockAddr.sin_port);

	tcpConn->setSocketFd(newSock);

	pthread_rwlock_rdlock(&mRequestListRWLock);
	mSipTcpConnMap[tcpConn->getHash()] = tcpConn;
	tcpConn->getRef();
	pthread_rwlock_unlock(&mRequestListRWLock);

  LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::processListenReq");
	return CONN_MGR_SUCCESS;
}

/**************************************************************************
* Function     : updateFdSet
*
* Description  :
*
* Parameters   :
*   Name             Type             Description
*-------------------------------------------------------------------------
*
* Return Value :
*   Type              Value         Description
*-------------------------------------------------------------------------
*
**************************************************************************/
int
INGwSpTcpConnMgr::updateFdSet()
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnMgr::updateFdSet");

	pthread_rwlock_rdlock(&mAcceptListRWLock);

	mMaxSockNum	= -1;

	FD_ZERO(&mSockFdSet);

	if (mListenerSock != -1)
		FD_SET(mListenerSock, &mSockFdSet);

  mMaxSockNum = mListenerSock;

	for (int i = 0; i < mMaxConn; ++i)
	{
		if (mClientSockList[i].sockFd != -1)
		{
			FD_SET(mClientSockList[i].sockFd, &mSockFdSet);

			if (mClientSockList[i].sockFd > mMaxSockNum)
				mMaxSockNum = mClientSockList[i].sockFd;
		}
	}
	pthread_rwlock_unlock(&mAcceptListRWLock);

  logger.logINGwMsg(false, VERBOSE_FLAG, 0,
	                  "mMaxSockNum:%d, mListenerSock:%d", 
										mMaxSockNum, mListenerSock);

  LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::updateFdSet");
	return CONN_MGR_SUCCESS;
}

/**************************************************************************
* Function     : updateSockInfoList
*
* Description  :
*
* Parameters   :
*   Name             Type             Description
*-------------------------------------------------------------------------
*
* Return Value :
*   Type              Value         Description
*-------------------------------------------------------------------------
*
**************************************************************************/
int
INGwSpTcpConnMgr::updateSockInfoList(const char*	p_ClientIp,
                                     int          p_ClientPort,
																		 int          p_SockType,
						                         const int 	  p_SockFd)
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnMgr::updateSockInfoList");

	pthread_rwlock_wrlock(&mAcceptListRWLock);

	for (int i = 0; i < mMaxConn; ++i)
	{
		if (mClientSockList[i].sockFd == -1)
		{
			mClientSockList[i].sockFd = p_SockFd;
      mClientSockList[i].sockPort = p_ClientPort;
			mClientSockList[i].sockType = p_SockType;
      mClientSockList[i].hash = 
                 INGwSpTcpConnection::getConnHash(p_ClientIp, p_ClientPort);

			strncpy(mClientSockList[i].clientId, p_ClientIp, 63);

      logger.logINGwMsg(false, VERBOSE_FLAG, 0,
			                  "Adding Client conn , Client IP :%s, SockFd:%d",
												p_ClientIp, p_SockFd);

			break;
		}
	}


	ProcessingThreadDetails* lProcsssingThdDetails = new ProcessingThreadDetails();
	lProcsssingThdDetails->mSockFd = p_SockFd;
	lProcsssingThdDetails->mPeerIp = p_ClientIp;
  lProcsssingThdDetails->mPeerPort = p_ClientPort;
	QueueManager* lRecvdMsgQueue = new QueueManager(false, CONN_MGR_MsgQueue_LWM, 
																									CONN_MGR_MsgQueue_HWM);
  std::string lQuName =  lProcsssingThdDetails->mPeerIp + " - Sip Msg Processing Q :";
	lRecvdMsgQueue->setName(lQuName.c_str());
	lProcsssingThdDetails->mRecvdMsgQueue = lRecvdMsgQueue;

	mProcessingThreadMap[lProcsssingThdDetails->mSockFd] = lProcsssingThdDetails;
	pthread_create(&lProcsssingThdDetails->mThreadId, 0, 
                 INGwSpTcpConnMgr::msgProcessingThd, 
                 (void*)lProcsssingThdDetails);

	pthread_detach(lProcsssingThdDetails->mThreadId);

	pthread_rwlock_unlock(&mAcceptListRWLock);

  LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::updateSockInfoList");
	return CONN_MGR_SUCCESS;
}

/**************************************************************************
* Function     : recvData
*
* Description  :
*
* Parameters   :
*   Name             Type             Description
*-------------------------------------------------------------------------
*
* Return Value :
*   Type              Value         Description
*-------------------------------------------------------------------------
*
**************************************************************************/
int
INGwSpTcpConnMgr::recvData()
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnMgr::recvData");

	pthread_rwlock_rdlock(&mAcceptListRWLock);

  int lClientIndex = -1;

	for (int i = 0; i < mMaxConn; ++i)
	{
		if (mClientSockList[i].sockFd == -1) 
			continue;
			
		if (FD_ISSET(mClientSockList[i].sockFd, &mSockFdSet))
		{
      char* mesg = INGwSpSipStackIntfLayer::instance().getBuffer();

      int dataLen = readSocketBuffer(mClientSockList[i].sockFd, mesg, MAX_SIPBUF_SIZE);
			if (dataLen < 0)
			{
        //ERROR CASE
        logger.logINGwMsg(false, ERROR_FLAG, 0,
													"Read Error from client socket. Closing Client <%s> FD <%d>",
													mClientSockList[i].clientId, mClientSockList[i].sockFd);

        // unlock here so we can take write lock in removeClientConn
	      pthread_rwlock_unlock(&mAcceptListRWLock);

        removeClientConn(i);

				// Lock again for next iteration
	      pthread_rwlock_rdlock(&mAcceptListRWLock);

        INGwSpSipStackIntfLayer::instance().reuseBuffer((char*)mesg);
				continue;
			}
      else if (dataLen == 0)
      {
        // Blocking case
        INGwSpSipStackIntfLayer::instance().reuseBuffer((char*)mesg);
        continue;
      }
      else
      {
        // Real data recvd
        // Enqueue the data in right queue
        t_ProThdMapIter iter = 
            mProcessingThreadMap.find(mClientSockList[i].sockFd);

				ProcessingThreadDetails* lProcessingThd = NULL;

        if(iter != mProcessingThreadMap.end())
	      {
					lProcessingThd = mProcessingThreadMap[mClientSockList[i].sockFd];
        }
        if(lProcessingThd == NULL)
				{
          logger.logINGwMsg(false, ERROR_FLAG, 0,
				  									"Error, No Msg ProcessingThread for <%s> FD <%d> "
														"Not processing recv sip msg. ",
				  									mClientSockList[i].clientId, mClientSockList[i].sockFd);
          INGwSpSipStackIntfLayer::instance().reuseBuffer((char*)mesg);

				} // end of if lProcesseingThd is null
				else
				{
					// Post mgs
					if(lProcessingThd->mRecvdMsgQueue != NULL)
					{
					  RecvdBufferData* lRecvdData = new RecvdBufferData();
					  lRecvdData->mRecvBuffer = mesg;
					  lRecvdData->mBufferLen = dataLen;
					  QueueData lData;
					  lData.data = static_cast<void*>(lRecvdData);
					  lProcessingThd->mRecvdMsgQueue->eventEnqueue(&lData);
            logger.logINGwMsg(false, TRACE_FLAG, 0,
				    									"Enqueue in ProcessingThread for <%s> FD <%d> "
                              "Msg is fo length <%d> ",
				  									  mClientSockList[i].clientId, mClientSockList[i].sockFd,
															dataLen);

					} // end of id lProcessingThd->mRecvdMsgQueue is null
					else
					{
            logger.logINGwMsg(false, ERROR_FLAG, 0,
				    									"Serious Error, No Q Manager for ProcessingThread of <%s> FD <%d> "
					  									"Not processing recv sip msg. ",
				    									mClientSockList[i].clientId, mClientSockList[i].sockFd);
            INGwSpSipStackIntfLayer::instance().reuseBuffer((char*)mesg);
					}
						
				} // end of else of if lProcessingThd is null
        
      }// end of else of if data recv is null

		} // end of if FD_ISSET
	} // end of for maxcon

	pthread_rwlock_unlock(&mAcceptListRWLock);

  LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::recvData");
	return CONN_MGR_SUCCESS;
}

/**************************************************************************
* Function     : sendSockData
*
* Description  :
*
* Parameters   :
*   Name             Type             Description
*-------------------------------------------------------------------------
*
* Return Value :
*   Type              Value         Description
*-------------------------------------------------------------------------
*
**************************************************************************/
int
INGwSpTcpConnMgr::sendSockData(const char*		p_Data,
					   	                 const int		  p_DataLen,
					                     const int  		p_SockIndex)
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnMgr::sendSockData");

	// Fill iovec structure
	struct iovec    iovData[1];

	iovData[0].iov_base     = (caddr_t)(p_Data);
	iovData[0].iov_len      = p_DataLen;

	if (writev(mClientSockList[p_SockIndex].sockFd, (const iovec*)&iovData, 1) < 0)
	{
    logger.logINGwMsg(false, ERROR_FLAG, 0,
		                  "SEND ERROR. Send to network failed with "
											"errno:%d, errTxt:%s, Closing sockFd:%d of client <%s>",
                       errno, strerror(errno), 
											 mClientSockList[p_SockIndex].sockFd, 
                       mClientSockList[p_SockIndex].clientId);

		closeClientConn(p_SockIndex);

    LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::sendSockData");
		return CONN_MGR_FAILURE;
	}

  LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::sendSockData");
	return CONN_MGR_SUCCESS;
}

/**************************************************************************
* Function     : closeClientConn
*
* Description  :
*
* Parameters   :
*   Name             Type             Description
*-------------------------------------------------------------------------
*
* Return Value :
*   Type              Value         Description
*-------------------------------------------------------------------------
*
**************************************************************************/
int
INGwSpTcpConnMgr::closeClientConn(const int  	p_SockIndex)
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnMgr::closeClientConn");

  logger.logINGwMsg(false, VERBOSE_FLAG, 0,
	                 "Closing socket, sockFd:%d of client <%s>", 
									  mClientSockList[p_SockIndex].sockFd, 
									  mClientSockList[p_SockIndex].clientId);

	if (close(mClientSockList[p_SockIndex].sockFd) < 0)
	{
    logger.logINGwMsg(false, ERROR_FLAG, 0,
										 "SOCKET CLOSE ERROR sockFd:%d of client <%s>",
									    mClientSockList[p_SockIndex].sockFd, 
									    mClientSockList[p_SockIndex].clientId);
	}

	mClientSockList[p_SockIndex].sockFd 	= -1;
	mClientSockList[p_SockIndex].sockType 	= -1;
	mClientSockList[p_SockIndex].sockPort 	= -1;
	mClientSockList[p_SockIndex].hash = -1;
	bzero(mClientSockList[p_SockIndex].clientId, 64);

  LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::closeClientConn");
	return CONN_MGR_SUCCESS;
}


/**************************************************************************
* Function     : setSockNonBlocking
*
* Description  :
*
* Parameters   :
*   Name             Type             Description
*-------------------------------------------------------------------------
*
* Return Value :
*   Type              Value         Description
*-------------------------------------------------------------------------
*
**************************************************************************/
int
INGwSpTcpConnMgr::setSockNonBlocking(const int		p_SockFd)
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnMgr::setSockNonBlocking");

	int		tmpVal = 0;

	tmpVal	= fcntl(p_SockFd, F_GETFL, 0);

	fcntl(p_SockFd, F_SETFL, (tmpVal | O_NONBLOCK));

  LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::setSockNonBlocking");
	return CONN_MGR_SUCCESS;
}

/**************************************************************************
* Function     : readSocketBuffer
*
* Description  :
*
* Parameters   :
*   Name             Type             Description
*-------------------------------------------------------------------------
*
* Return Value :
*   Type              Value         Description
*-------------------------------------------------------------------------
*
**************************************************************************/
int
INGwSpTcpConnMgr::readSocketBuffer(const int	p_SockFd,
							                    char*		p_Buffer,
							                    const int	p_ReadLen)
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnMgr::readSocketBuffer");

	int	  bytesRead = 0;

	bytesRead	=  recv(p_SockFd, p_Buffer, p_ReadLen, 0);

	if (bytesRead <= 0)
	{
    switch(errno)
    {
      case EINTR:
      {
        logger.logMsg(WARNING_FLAG, 0, "Recv interrupted with error [%s] code [%d]",
                             strerror(errno), errno);
      }
      break;

      case EWOULDBLOCK:
      {
        logger.logMsg(TRACE_FLAG, 0, "Recv blocking with error [%s] code [%d].", 
                             strerror(errno), errno);
      }
      break;
      default:
      {
        
        logger.logMsg(ERROR_FLAG, 0, "Recv failed with with error [%s] code [%d]. "
                                     "Bytes Read is [%d]",
                                     strerror(errno), errno, bytesRead);

        LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::readSocketBuffer");
				return -1;
      }
    } // end of switch

	}

	p_Buffer[bytesRead]	= '\0';

  LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::readSocketBuffer");
	return bytesRead;
}

/**************************************************************************
* Function     : dumpClientDetails
*
* Description  :
*
* Parameters   :
*   Name             Type             Description
*-------------------------------------------------------------------------
*
* Return Value :
*   Type              Value         Description
*-------------------------------------------------------------------------
*
**************************************************************************/

int
INGwSpTcpConnMgr::dumpClientDetails(char*		p_DumpFile, int p_Len)
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnMgr::dumpClientDetails");
  return 0;
  LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::dumpClientDetails");
}

/**************************************************************************
* Function     : getSocketFd
*
* Description  :
*
* Parameters   :
*   Name             Type             Description
*-------------------------------------------------------------------------
*
* Return Value :
*   Type              Value         Description
*-------------------------------------------------------------------------
*
**************************************************************************/
int
INGwSpTcpConnMgr::getSocketFd(const char*	p_ClientIp)
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnMgr::getSocketFd");

	for (int i = 0; i < mMaxConn; ++i)
	{
		if (mClientSockList[i].sockFd != -1)
    {
			if (strcmp(mClientSockList[i].clientId, p_ClientIp) == 0)
      {
        LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::getSocketFd");
				return mClientSockList[i].sockFd;
      }
    }
	}

  LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::getSocketFd");
	return CONN_MGR_FAILURE;
}

int
INGwSpTcpConnMgr::removeClientConn(const char*   p_ClientIp)
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnMgr::removeClientConn");
  int lIndex = getChannelIndex(p_ClientIp);
  if(lIndex != -1)
  {
    removeClientConn(lIndex);
  }
  LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::removeClientConn");
	return CONN_MGR_SUCCESS;
}
// Connect to self if not connected and return its socket id
int
INGwSpTcpConnMgr::getSelfSocketId()
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnMgr::getSelfSocketId");

  int lSockFd = -1;
	if(mSelfConn == NULL)
	{
    mSelfConn = new INGwSpTcpConnection();
		std::string lSelfIp = 
				 INGwIfrPrParamRepository::getInstance().getValue(ingwFLOATING_IP_ADDR);
		int lSelfPort = atoi(
				(INGwIfrPrParamRepository::getInstance().getValue(ingwSIP_STACK_LISTENER_PORT)).c_str());

		mSelfConn->initialize(lSelfIp.c_str(), lSelfPort);
		lSockFd = mSelfConn->connect();
		if(lSockFd == -1)
		{
      logger.logINGwMsg(false, ERROR_FLAG, 0,
	                   	"Serious Error Could not connect to self Connection. Exiting.... ");
      exit(0);											
		}
	}
	else
	{
		if(false ==  mSelfConn->isSockConnected(mSelfConn->getSocketFd() ))
		{
		  lSockFd = mSelfConn->connect();
		  if(lSockFd == -1)
		  {
        logger.logINGwMsg(false, ERROR_FLAG, 0,
	                     	"Serious Error Could not connect to self Connection. Exiting.... ");
        exit(0);											
		  }
		}
		lSockFd = mSelfConn->getSocketFd();
	}

  LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::getSelfSocketId");
	return lSockFd;
}

/**************************************************************************
* Function     : removeClientConn
*
* Description  :
*
* Parameters   :
*   Name             Type             Description
*-------------------------------------------------------------------------
*
* Return Value :
*   Type              Value         Description
*-------------------------------------------------------------------------
*
**************************************************************************/

// will be invoked by sending threads

int
INGwSpTcpConnMgr::removeClientConn(unsigned long p_ConnHash)
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnMgr::removeClientConn");

	pthread_rwlock_wrlock(&mAcceptListRWLock);

	for (int i = 0; i < mMaxConn; ++i)
	{
		if (mClientSockList[i].sockFd != -1)
    {
			if (p_ConnHash == mClientSockList[i].hash)
      {
        int lSockId = mClientSockList[i].sockFd;

        // Close TCP conn if exist
				int retValue = closeClientConn(i);

        // Stop queue and Remove thread from the map
        t_ProThdMapIter iter = 
            mProcessingThreadMap.find(lSockId);

        if(iter != mProcessingThreadMap.end())
	      {
	        ProcessingThreadDetails* lProcessingThd = 
										mProcessingThreadMap[lSockId];
          lProcessingThd->stop();
          mProcessingThreadMap.erase(iter);
          delete lProcessingThd;
        }

				break;
      } // end of if client found
    } // end of if sockFd is not -1
	} // end of for maxConn

  LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::removeClientConn");

	pthread_rwlock_unlock(&mAcceptListRWLock);

	return CONN_MGR_SUCCESS;
}

//will be invoked by receiving thread

int
INGwSpTcpConnMgr::removeClientConn(int    p_ClientIndex)
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnMgr::removeClientConn By Index");

	pthread_rwlock_wrlock(&mAcceptListRWLock);
	pthread_rwlock_wrlock(&mRequestListRWLock);

	int lSockType = -1;
	unsigned long lConnHash = -1;

	if (mClientSockList[p_ClientIndex].sockFd != -1)
  {
    int lSockId = mClientSockList[p_ClientIndex].sockFd;
		lSockType = mClientSockList[p_ClientIndex].sockType;
		lConnHash =  mClientSockList[p_ClientIndex].hash;


    // Close TCP conn if exist
		int retValue = closeClientConn(p_ClientIndex);

    // Stop queue and Remove thread from the map
    t_ProThdMapIter iter = 
            mProcessingThreadMap.find(lSockId);

    if(iter != mProcessingThreadMap.end())
	  {
	    ProcessingThreadDetails* lProcessingThd = 
										mProcessingThreadMap[lSockId];
      lProcessingThd->stop();
      mProcessingThreadMap.erase(iter);
      delete lProcessingThd;
    } 
	} 

	pthread_rwlock_unlock(&mAcceptListRWLock);

	//Remove entry from connection map

  t_SipTcpConnMapItr iterConn = 
            mSipTcpConnMap.find(lConnHash);

  if(iterConn != mSipTcpConnMap.end())
	{
	  INGwSpTcpConnection* lTcpConn = (INGwSpTcpConnection*)(iterConn->second);
		lTcpConn->releaseRef();
    mSipTcpConnMap.erase(iterConn);
  } 

	pthread_rwlock_unlock(&mRequestListRWLock);

  LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::removeClientConn");
	return CONN_MGR_SUCCESS;
}

/**
* Frames a single SIP message from the queued array of recvd buffer.
*
*/
void 
INGwSpTcpConnMgr::ProcessingThreadDetails::frameSingleSipMsg() 
{
  LogINGwTrace(false, 0, "IN ProcessingThreadDetails::frameSingleSipMsg");
  // set the mark at the first char, 
  // since we want to keep the entire msg buffered until we find the end
  // also, should pos always be 0 at this point?
  int markpos = 0;
  int pos = 0;

  int contentLength = -1;

  char ch;
  int chi;     // integer representation of the byte

  bool getContentLength = false;
  bool done = false;
  bool trimmedLeadingWS = false;

  // handled is Msg Parser // used to trim leading white space from the beginning of messages
  // handled is Msg Parser boolean isLeadingWhiteSpace = true;

  // We have to do a few things to frame this message:
  // Look for "\nContent-Length:" or "\nl:" 
	//(don't forget about LWS before the ':') (case insensitve)
  // At the same time look for "\n\n" or "\n\r\n" - empty header to separate body
  // If content length exists, continue to look for the empty header
  //  then just read the body and we have a framed message.
  // else we found the empty header but no content length - read until EOF and 
	//we have a framed message
  while (!done  )
  {
    ch = readByte();
    if (!trimmedLeadingWS && ch <= ' ')
    {
      markpos = pos;
      continue;
    }
    trimmedLeadingWS = true;

    // handled is Msg Parser if (isLeadingWhiteSpace)
    // handled is Msg Parser {
    // handled is Msg Parser if (ch <= ' ')
    // handled is Msg Parser {
    // handled is Msg Parser // the char after this WS is now the start of the msg
    // handled is Msg Parser markpos = pos;

    // handled is Msg Parser // and keep stripping WS until we see the first non-WS char
    // handled is Msg Parser continue;
    // handled is Msg Parser }
    // handled is Msg Parser isLeadingWhiteSpace = false;
    // handled is Msg Parser }

    // EOL start of all our tests
    if (ch == '\n')
    {
			// - will check the logic behing exception block
			/*
      try
      {
        ch = readByte();
      }
      catch (EOFException e)
      {
        // It might be that this mb.msg is just missing the final empty header
        // We will try to parse it anyway
        // No need to throw EOFExcetion here, it will be thrown on first read next time

        // hack - fix the message before passing to next parser - jsm
        byte copiedBytes[] = new byte[pos - markpos + 2];
        copiedBytes[copiedBytes.length - 2] = (byte)'\r';
        copiedBytes[copiedBytes.length - 1] = (byte)'\n';
        System.arraycopy(buf, markpos, copiedBytes, 0, pos - markpos);

        return copiedBytes;
      }
			*/

			ch = readByte();

      // short form content length if followed by white space or a ':'
      if (ch == 'l' || ch == 'L')
      {
        ch = readByte();
        if (ch == ':')
        {
          getContentLength = true;
        }
        else if (ch == ' ' || ch == '\t' || ch == '\r') // end of header name
        {
          getContentLength = true;
          ch = readByte();
          while (ch != ':')
          {
            try
            {
              ch = readByte();
            }
            catch (...)
            {
              done = true;
              break;
            }
          }
        }
        else if (ch == '\n') // end of header name
        {
          getContentLength = true;
          // white space must follow or the mb.msg is illegal
          // but, don't worry about that now, it will be caught later
          // just asumme white space and find the ':'
          ch = readByte();
          while (ch != ':')
          {
            try
            {
              ch = readByte();
            }
            catch (...)
            {
              done = true;
              break;
            }
          }
        }
      }

      else if (ch == 'C' || ch == 'c')
      {
        ch = readByte();
        if (ch == 'o' || ch == 'O')
        {
          ch = readByte();
          if (ch == 'n' || ch == 'N')
          {
            ch = readByte();
            if (ch == 't' || ch == 'T')
            {
              ch = readByte();
              if (ch == 'e' || ch == 'E')
              {
                ch = readByte();
                if (ch == 'n' || ch == 'N')
                {
                  ch = readByte();
                  if (ch == 't' || ch == 'T')
                  {
                    ch = readByte();
                    if (ch == '-')
                     {
                       ch = readByte();
                       if (ch == 'L' || ch == 'l')
                       {
                         ch = readByte();
                         if (ch == 'e' || ch == 'E')
                         {
                           ch = readByte();
                           if (ch == 'n' || ch == 'N')
                           {
                             ch = readByte();
                             if (ch == 'g' || ch == 'G')
                             {
                               ch = readByte();
                               if (ch == 't' || ch == 'T')
                               {
                                 ch = readByte();
                                 if (ch == 'h' || ch == 'H')
                                 {
                                   // found Content-Length - now find white space or ':'
                                   ch = readByte();
                                   if (ch == ':')
                                   {
                                     getContentLength = true;
                                   }
                                   else if (ch == ' ' || ch == '\t' || ch == '\r') // end of header name
                                   {
                                     getContentLength = true;
                                     ch = readByte();
                                     while (ch != ':')
                                     {
                                       try
                                       {
                                         ch = readByte();
                                       }
                                       catch (...)
                                       {
                                         done = true;
                                         break;
                                       }
                                     }
                                   }
                                   else if (ch == '\n') // end of header name
                                   {
                                     getContentLength = true;
                                     // white space must follow or the mb.msg is illegal
                                     // but, don't worry about that now, it will be caught later
                                     // just asumme white space and find the ':'
                                     ch = readByte();
                                     while (ch != ':')
                                     {
                                       try
                                       {
                                         ch = readByte();
                                       }
                                       catch (...)
                                       {
                                         done = true;
                                       }
                                     }
                                   }
                                 } // h
                               } // t
                             } // g
                           } // n
                         } // e
                       } // L
                     } // -
                   } // t
                 } // n
               } // e
             } // t
           } // n
         } // o
      } // C


      // second EOL
      else if (ch == '\r')
      {
        ch = readByte();
        if (ch == '\n')
        {
          break;    // found empty header
        }
      }
      else if (ch == '\n')
      {
        break;    // found empty header
      }

      // found the name part and ':' for content length - now get the value
      if (getContentLength)
      {
        getContentLength = false;

        std::string lClStr;

        ch = readByte();

        while (ch == ' ' || ch == '\r' || ch == '\n') // remove leading ws
        {
          ch = readByte();
        }
        lClStr.clear();
        while (ch >= '0' && ch <= '9') // get digits
        {
					lClStr += ch;
          ch = readByte();
        }
        contentLength = atol(lClStr.c_str());

        // Here we go, another strange border case.    Let's say that the content length is the
        // last header in the list and there is no CRLF, just LF.    In this case we have just read
        // the LF that clues us into the fact that the last empty header is next.
        // We may also need to EOF handling from above here as well. - jsm
        if (ch == '\n') // already read the EOL for content length - need to see if empty header follows
        {
          ch = readByte();
          if (ch == '\n')
          {
            break;    // found empty header
          }
          else if (ch == '\r')
          {
            ch = readByte();
            if (ch == '\n')
            {
              break;    // found empty header
            }
          }
        }
      }
    } // if ch = \n
  } // while !done

  logger.logINGwMsg(false, TRACE_FLAG, 0,
	                 "End is found. Getting Content Length is  %d " , 
	                  contentLength);
  // need to make sure that the markpos does not go to -1 during this search
  if (contentLength == 0) // no body - nothing to do
  {
  }
	// To see what this case means
  else if (contentLength == -1) // no content length - read until EOF
  {
	/*
    // should change to block IO - jsm
    try
    {
      while (true)
      {
        pos = count;
        // just keep reading bytes until this fails
        readByte();
      }
    }
    catch (EOFException e)
    {
      // must read to EOF if there is no content length
      // this is the one place where we continue after getting EOF
    }
	*/
  }
  else // positive content length - read that data
  {
    logger.logINGwMsg(false, TRACE_FLAG, 0,
	                 "Content Length is  %d " , 
	                  contentLength);
		// read up to content length
		int lClCount = 0;
    while ((lClCount < contentLength) )
    {
			++lClCount;
			readByte();
		}
  }
  // Now message is complete send to stack parser
	postCurrentMsgToStack();

  LogINGwTrace(false, 0, "OUT ProcessingThreadDetails::frameSingleSipMsg");
}

void
INGwSpTcpConnMgr::ProcessingThreadDetails::startProcessing()
{
  LogINGwTrace(false, 0, "IN ProcessingThreadDetails::startProcessing");

  logger.logINGwMsg(false, TRACE_FLAG, 0,
	                 	"Processing thread started for socket <%d> for IP <%s> ",
										mSockFd, mPeerIp.c_str());
	while( (! mIsStopped) || (mRecvdMsgQueue->queueSize() != 0) )
	{
		try
		{
		  frameSingleSipMsg();
		}
		catch(...)
		{
      logger.logINGwMsg(false, TRACE_FLAG, 0,
	                 	    "Exception caught from frameSingleSipMsg. "
												"May be Thread is stopped");
		}
	}
  // We are stopped now
  // Do Cleanup
  if(mCurrentSendBufferData.mRecvBuffer != NULL) 
  {
    INGwSpSipStackIntfLayer::instance().reuseBuffer(
                         (char*)mCurrentSendBufferData.mRecvBuffer);
  }

  if(mCurrentRecvdBufferData != NULL && 
     mCurrentRecvdBufferData->mRecvBuffer !=NULL )
  {
    INGwSpSipStackIntfLayer::instance().reuseBuffer(mCurrentRecvdBufferData->mRecvBuffer);
    delete mCurrentRecvdBufferData;
  }
  mIsStopOver = true;
  LogINGwTrace(false, 0, "OUT ProcessingThreadDetails::startProcessing");
}

char
INGwSpTcpConnMgr::ProcessingThreadDetails::readByte()
{
	// If there is no data get it from queue,
	// its a producer consumer queue and call will block if there is no data

	while(mCurrentRecvdBufferData == NULL || 
		 mCurrentRecvdBufferData->mRecvBuffer == NULL || 
		 mCurrentRecvdBufferData->mBufferLen == mLengthProccessed)
  {
	  if( (mIsStopped) && (mRecvdMsgQueue->queueSize() == 0) )
    {
			throw -1;
    }
	  QueueData lQueueData;
		int lTimeout = 2000;
		if(mIsStopped)
		{
			lTimeout = 100;
		}
		int lCount = mRecvdMsgQueue->eventDequeueBlk(&lQueueData, 1, lTimeout, true);
		if(lCount == 1)
		{
			mCurrentRecvdBufferData = static_cast<RecvdBufferData*>(lQueueData.data);
			// nothing has been processed from this data so length has to be 0
			mLengthProccessed == 0;
			break;
		}
		else
		{
		/*
      logger.logINGwMsg(false, TRACE_FLAG, 0,
		  		              "dequeue timeout of client with socket <%d> for IP <%s> "
                        " Will again try to dequeue", 
		  									mSockFd, mPeerIp.c_str());
    */
		}
	}
	// Now we have some data to process

  char data = mCurrentRecvdBufferData->mRecvBuffer[mLengthProccessed++];

	// Complete length is exhausted so prepare to get next buffer from queue
  if( mCurrentRecvdBufferData->mBufferLen == mLengthProccessed )
  {
    INGwSpSipStackIntfLayer::instance().reuseBuffer(mCurrentRecvdBufferData->mRecvBuffer);
    mCurrentRecvdBufferData->mRecvBuffer = NULL;
		delete mCurrentRecvdBufferData;
    mCurrentRecvdBufferData = NULL;
		mLengthProccessed = 0;
  } 

  // This is start of construction of new sip msg to be sent to stack parsing
	if(mCurrentSendBufferData.mRecvBuffer == NULL)
	{
    char* mesg = INGwSpSipStackIntfLayer::instance().getBuffer();
		mCurrentSendBufferData.mRecvBuffer = mesg;
		mCurrentSendBufferData.mBufferLen = 0;
	}

	// We assume sip msg is not bigger than MAX_SIPBUF_SIZE
	// If it is we are not copying rest of it current buffer
	if (mCurrentSendBufferData.mBufferLen < ( MAX_SIPBUF_SIZE -1 ))
	{
	  mCurrentSendBufferData.mRecvBuffer[mCurrentSendBufferData.mBufferLen++] = data;
  }
	return data;
}

void
INGwSpTcpConnMgr::ProcessingThreadDetails::postCurrentMsgToStack()
{
  LogINGwTrace(false, 0, "IN ProcessingThreadDetails::postCurrentMsgToStack");

/*
	//No need to extract again.
  struct sockaddr_in cli_addr;
  int addrsize = sizeof(cli_addr);

	int getpeernameRetval = getpeername(mSockFd,
							(struct sockaddr *)&cli_addr,&addrsize);

  if (getpeernameRetval < 0)
	{
    logger.logINGwMsg(false, ERROR_FLAG, 0,
				              "Failed to get client's info from socket <%d> for IP <%s> "
											"Not processing the message",
											mSockFd, mPeerIp.c_str());

    INGwSpSipStackIntfLayer::instance().reuseBuffer((char*)mCurrentSendBufferData.mRecvBuffer);
	  mCurrentSendBufferData.mRecvBuffer = NULL;
	  mCurrentSendBufferData.mBufferLen = 0;
		return;
	}


  int cliaddr = cli_addr.sin_addr.s_addr;

  const char *retval = inet_ntop(AF_INET, (void *)&cliaddr,
                                 transportinfo->pTranspAddr,
                                 INET_ADDRSTRLEN);
  if(!retval)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0,
				              "Failed to get client's name from socket <%d> for IP <%s> "
											"Not processing the message",
											mSockFd, mPeerIp.c_str());

    delete transportinfo;
    INGwSpSipStackIntfLayer::instance().reuseBuffer((char*)mCurrentSendBufferData.mRecvBuffer);

	  mCurrentSendBufferData.mRecvBuffer = NULL;
	  mCurrentSendBufferData.mBufferLen = 0;
    return;
  }

*/

  INGwSipTranspInfo *transportinfo = new INGwSipTranspInfo;

  transportinfo->mPort = mPeerPort;
	strncpy(transportinfo->pTranspAddr, mPeerIp.c_str(), INET_ADDRSTRLEN);

  transportinfo->mTranspType = Sdf_en_protoTcp;
  transportinfo->mSockfd = mSockFd;

	mCurrentSendBufferData.mRecvBuffer[mCurrentSendBufferData.mBufferLen] = '\0';
	char* mesg = mCurrentSendBufferData.mRecvBuffer;
  int dataLen = mCurrentSendBufferData.mBufferLen;

  INGwSpTcpConnMgr::instance().getSipListenerThread()->postNetworkMsg(
																						transportinfo, mesg, dataLen);
	mCurrentSendBufferData.mRecvBuffer = NULL;
	mCurrentSendBufferData.mBufferLen = 0;
  LogINGwTrace(false, 0, "IN ProcessingThreadDetails::postCurrentMsgToStack");
}

void
INGwSpTcpConnMgr::ProcessingThreadDetails::stop()
{
  LogINGwTrace(false, 0, "IN ProcessingThreadDetails::stop");
  mIsStopped = true;

  while(!mIsStopOver)
  {
    sleep(1);
  }

  if(mRecvdMsgQueue != NULL)
  {
    mRecvdMsgQueue->stopQueue();
    delete mRecvdMsgQueue;
  }
  LogINGwTrace(false, 0, "OUT ProcessingThreadDetails::stop");
}

bool    
INGwSpTcpConnMgr::isSockConnected(int p_SockFd)
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnMgr::isSockConnected");
	struct sockaddr_in  cli_addr;

	int lClilen = 0;
	lClilen = sizeof(cli_addr);

	if (p_SockFd == -1)
	{
    LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::isSockConnected");
		return false;
	}	

	if(getpeername(p_SockFd, (struct sockaddr *)&cli_addr, &lClilen) == -1)
	{
    LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::isSockConnected");
		return false;
	}	

  LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::isSockConnected");
	return true;
}

int     
INGwSpTcpConnMgr::addRemoteConnection(const  char*   p_RemoteIp,
                                      int            p_RemotePort)
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnMgr::addRemoteConnection");

	int retVal = -1;

	if(p_RemotePort != -1 && p_RemoteIp != NULL)
	{
		bool lIsPresent = isConnectionPresent(p_RemoteIp, p_RemotePort);

		if(lIsPresent)
		{
      logger.logINGwMsg(false, TRACE_FLAG, 0,
		                    "Connection already present in map. "
												" for <%s> : <%d> . Not making new connection",
										    p_RemoteIp, p_RemotePort); 
      LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::addRemoteConnection");
	    return CONN_MGR_SUCCESS;
		}
	} // end of if p_RemotePort is -1 && p_RemoteIp is NULL
	else
	{
    logger.logINGwMsg(false, ERROR_FLAG, 0,
		                 "Error Invalid input. Returning error");
    LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::addRemoteConnection");
		return CONN_MGR_FAILURE;
	}
  
	// Connection is not prsent.
	// Make a new connection

	INGwSpTcpConnection* tcpConn = new INGwSpTcpConnection();

	INGwIfrUtlRefCount_var ltcpConnVar(tcpConn);

	tcpConn->initialize(p_RemoteIp, p_RemotePort);

	int lSocketId = tcpConn->connect();

	if(lSocketId == -1 )
	{
		// connect failed . return error

    logger.logINGwMsg(false, ERROR_FLAG, 0,
		                  "Connect failed "
											" for <%s> : <%d> . return error",
									    p_RemoteIp, p_RemotePort); 
    LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::addRemoteConnection ");
		return CONN_MGR_FAILURE;
	}
	else
	{
		// Other thread could have inserted the entry in map
		bool lIsPresent = isConnectionPresent(p_RemoteIp, p_RemotePort);
		if(! lIsPresent)
		{
	    pthread_rwlock_wrlock(&mRequestListRWLock);
    	mSipTcpConnMap[tcpConn->getHash()] = tcpConn;
    	tcpConn->getRef();
	    pthread_rwlock_unlock(&mRequestListRWLock);
			updateSockInfoList(p_RemoteIp, p_RemotePort, 
													 SOCK_TYPE_REQUESTED_CONN, lSocketId);

      logger.logINGwMsg(false, TRACE_FLAG, 0,
		                    "Connection established  "
			  								" with <%s> : <%d> . ",
			  						    p_RemoteIp, p_RemotePort); 
		}
	} //end of else of if lSocketId == -1

  LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::addRemoteConnection");
	return CONN_MGR_SUCCESS;
}

bool
INGwSpTcpConnMgr::isConnectionPresent(const  char*   p_RemoteIp,
                                      int            p_RemotePort)
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnMgr::isConnectionPresent");

	unsigned long lConnHash = 
		   INGwSpTcpConnection::getConnHash(p_RemoteIp, p_RemotePort);

	pthread_rwlock_rdlock(&mRequestListRWLock);

  t_SipTcpConnMapItr iterConn = 
            mSipTcpConnMap.find(lConnHash);

  bool lFound = false;

  if(iterConn != mSipTcpConnMap.end())
	{
		lFound = true;
  } 
	else
	{
		lFound = false;
	}
	pthread_rwlock_unlock(&mRequestListRWLock);

  LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::isConnectionPresent");
	return lFound;
}


/**************************************************************************
* Function     : sendMsgToNetwork
*
* Description  :
*
* Parameters   :
*   Name             Type             Description
*-------------------------------------------------------------------------
*
* Return Value :
*   Type              Value         Description
*-------------------------------------------------------------------------
*
**************************************************************************/
int
INGwSpTcpConnMgr::sendMsgToNetwork(const char*	p_ClientIp,
                                   int          p_ClientPort,
					                         const char*	p_Msg,
					                         const int	p_MsgLen)
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnMgr::sendMsgToNetwork");

	int channelIdx	= -1;
	int retVal = CONN_MGR_FAILURE;

	unsigned long lConnHash = 
		   INGwSpTcpConnection::getConnHash(p_ClientIp, p_ClientPort);

	INGwSpTcpConnection* lTcpConn = NULL;
	pthread_rwlock_rdlock(&mRequestListRWLock);

  t_SipTcpConnMapItr iterConn = 
            mSipTcpConnMap.find(lConnHash);

  if(iterConn != mSipTcpConnMap.end())
	{
	  lTcpConn = (INGwSpTcpConnection*)(iterConn->second);
		lTcpConn->getRef();
  } 
	else
	{
    logger.logINGwMsg(false, WARNING_FLAG, 0,
	                   "Connection not found with clientIp : "
	  								"<%s>.  port <%d> Not sending Msg to Network", 
	  								p_ClientIp, p_ClientPort);
	  pthread_rwlock_unlock(&mRequestListRWLock);

    LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::sendMsgToNetwork");
	  return retVal;
	}

	INGwIfrUtlRefCount_var ltcpConnVar(lTcpConn);

	pthread_rwlock_unlock(&mRequestListRWLock);

	//Connection  found send data on connection

  logger.logINGwMsg(false, VERBOSE_FLAG, 0,
	                 "Sending msg to Network, msgLen:%d, clientIp:%s, port:%d", 
									 p_MsgLen, p_ClientIp, p_ClientPort);

  retVal = lTcpConn->sendMsg(p_Msg, p_MsgLen);

	if(retVal == -1)
	{
	  //send failed. Clean entry from map.

	  pthread_rwlock_wrlock(&mRequestListRWLock);

		//Again find in the map as other thread may have
		// removed the entry from map

    t_SipTcpConnMapItr iter = 
            mSipTcpConnMap.find(lConnHash);

    if(iter != mSipTcpConnMap.end())
	  {
			 mSipTcpConnMap.erase(iter);
			lTcpConn->releaseRef();
    } 

	  pthread_rwlock_unlock(&mRequestListRWLock);

		removeClientConn(lConnHash);
	} // end of if send failed


  LogINGwTrace(false, 0, "OUT INGwSpTcpConnMgr::sendMsgToNetwork");
	return retVal;
}


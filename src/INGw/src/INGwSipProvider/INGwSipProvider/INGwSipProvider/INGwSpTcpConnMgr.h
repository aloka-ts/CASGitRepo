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
//     File:     INGwSpTcpConnMgr.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef INGW_SP_TCP_CONN_MGR_H_
#define INGW_SP_TCP_CONN_MGR_H_

#include <sys/socket.h>
#include <strings.h>
#include <string.h>
#include <pthread.h>
#include <signal.h>
#include <map>
#include <list>
#include <sys/resource.h>
#include <Util/QueueMgr.h>

#include <INGwSipProvider/INGwSpSipIncludes.h>
#include <INGwSipProvider/INGwSpSipListenerThread.h>
#include <INGwSipProvider/INGwSpTcpConnection.h>

#define CONN_MGR_MsgQueue_LWM 64
#define CONN_MGR_MsgQueue_HWM 256

#define CONN_MGR_SUCCESS 0
#define CONN_MGR_FAILURE -1

const int SOCK_TYPE_ACCEPTED_CONN = 1;
const int SOCK_TYPE_REQUESTED_CONN = 2;

class INGwSpTcpConnMgr 
{
	public:
		static INGwSpTcpConnMgr&	instance();

		static void         signalHandler(int   p_SigId);

		int				start();
		int 			stop();
		int			  bindListener();


		int 		initialize(const char* 	p_IpAddr, 
							         const int 	p_IpPort,
					   		       const int 	p_MaxConn,
                       INGwSpSipListenerThread* p_ListenerThread);

    // This function will add new fd in INGwFdList
		// and will remove old fd if present
    int     addINGwFd(int            p_INGwSockFd,
											const  char*   p_ClientIp,
											int            p_OldFd = -1);

    // check in map if remote connection is present or not
    bool     isConnectionPresent(const  char*   p_RemoteIp,
                                 int            p_RemotePort);

    // establish conection with remote IP and Port and put in map
    int     addRemoteConnection(const  char*   p_RemoteIp,
                                int            p_RemotePort);

		int		 	sendMsgToNetwork(const  char*   p_RemoteIp,
                             int            p_RemotePort,
								             const  char*   p_Msg,
								             const  int	    p_MsgLen);

		int			dumpClientDetails(char*   p_Buffer, int p_Len);

		int			getSocketFd(const char* p_ClientIp);

    // Connect to self if not connected and return its socket id
		int			getSelfSocketId();

    int     removeClientConn(const char*   p_ClientIp);

    inline INGwSpSipListenerThread* getSipListenerThread()
    {
      return mSipListenerThread;
    }

	private:

		// Structure Classes
		class SockInfo
		{
			public:
				int		sockFd;
				int		sockType;
				int		sockPort;
				unsigned long hash;
				char	clientId[64];

				SockInfo():sockFd(-1),sockType(-1), sockPort(-1), hash(-1)
				{ 
					bzero(clientId, 64);
				}
		};

    struct RecvdBufferData
    {
			char* mRecvBuffer;
			int   mBufferLen;
    };

		static void* listenerThdMain(void	*p_Context);
		static void* msgProcessingThd(void	*p_Context);

		int			startListening();

		int			getChannelIndex(const char* p_ClientIp);

		int			processListenReq();

		int			updateFdSet();

		int			updateSockInfoList(const char*  p_ClientIp,
															 int          p_ClientPort,
															 int          p_SockType,
								               const int 	  p_SockFd);

		int			recvData();

		int			sendSockData(const char*	p_Data,
								         const int	p_DataLen,
							 	         const int	p_SockIndex);

		int			closeClientConn(const int  p_SockIndex);

		int			setSockNonBlocking(const int	p_SockFd);

		bool    isSockConnected(int p_SockFd);

		int			readSocketBuffer(const int	p_SockFd,
									           char*		r_Buffer,
									           const int  p_ReadLen);

    // It will check for Client Fd in the list, 
		// If not found it will try to connect a new connection,
		// if able connected will insert the entry in list and return FD
		// if failed will try untill SAS is registered with INGW

		int     getSendClientFd(const char* p_ClientIp, 
														int         p_ClientPort);

    // will return connected Fd
		int     connectToEndPoint(const char* p_ServerIp, 
														 int         p_ServerPort);

		//will be invoked by receiving thread
		int			removeClientConn(int    p_ClientIndex);

		// will be invoked by sending threads
		int			removeClientConn(unsigned long  p_ConnHash);

		struct sockaddr_in		mSelfSockAddr;
		int						        mSelfPort;
		struct sockaddr_in		mPeerSockAddr;
		socklen_t				      m_PeerAddrLen;
		char					        mSelfIpAddr[64];	

		int					          mMaxConn;
		int					          mTotalConn;
		SockInfo*			        mClientSockList;

		//Tcp Connection to self. 
		//Its fd will be used to set transport info fd
		INGwSpTcpConnection*  mSelfConn;

		std::list<SockInfo*>  mINGwSockList;
		fd_set				        mSockFdSet;
		int					          mMaxSockNum;
		int					          mListenerSock;

		int					          mShutDownFlag;
		int					          mIsInitialized;
		int					          mStartFlag;
		pthread_t			        mThreadId;
		pthread_rwlock_t      mAcceptListRWLock;
		pthread_rwlock_t      mRequestListRWLock;

		struct ProcessingThreadDetails
		{
			pthread_t mThreadId;
      // Queue that will hold the buffer received from peer connection
			QueueManager* mRecvdMsgQueue;
			bool mIsStopped;
			bool mIsStopOver;
			std::string mPeerIp;
			int mPeerPort;
			// This structure will hold the active buffer that is processed by parsing thread.
			RecvdBufferData* mCurrentRecvdBufferData;
			// length of active buffer proccessed by parsing thread.
			int mLengthProccessed;

			// This structure will hold the active buffer that would be sent by parsing thread.
			RecvdBufferData mCurrentSendBufferData;
     

			int mSockFd;

			ProcessingThreadDetails(): mRecvdMsgQueue(0),
															mCurrentRecvdBufferData(0),
															mLengthProccessed(0),
															mIsStopped(false),
															mSockFd(0),
                              mIsStopOver(false),
															mPeerIp(""),
                              mPeerPort(-1)
      {
        mCurrentSendBufferData.mRecvBuffer = NULL;
        mCurrentSendBufferData.mBufferLen = 0;
      }
      ~ProcessingThreadDetails(){};
      void startProcessing();

      //This function will read the next byte from current recvd buffer from queue
      //If current read count is equal to size of current recvd buffer, it will
      //extract next buffer from its queue.
      // Addtional task of this function is copy the current read byte to another buffer.
      char readByte();

      // This function framed single messgae from buffer stored in queue
      void frameSingleSipMsg();

      // Send message to listener thread for further processing
      void postCurrentMsgToStack();

      void stop();
		};

		typedef std::map<unsigned long, INGwSpTcpConnection*> t_SipTcpConnMap;
    typedef t_SipTcpConnMap::iterator t_SipTcpConnMapItr;

		t_SipTcpConnMap mSipTcpConnMap;

		typedef std::map<int, ProcessingThreadDetails*> ProcessingThreadMap;
    typedef ProcessingThreadMap::iterator t_ProThdMapIter;


    ProcessingThreadMap mProcessingThreadMap;

    INGwSpSipListenerThread* mSipListenerThread;

		// ctor
		INGwSpTcpConnMgr():mShutDownFlag(0),
					mSelfPort(0),
					mMaxConn(0),
					mClientSockList(0),
					m_PeerAddrLen(0),
					mMaxSockNum(-1),
					mIsInitialized(0),
					mStartFlag(0),
					mThreadId(0),
          mSelfConn(0)
		{ 
			bzero(&mSelfSockAddr, sizeof(mSelfSockAddr));
			bzero(&mPeerSockAddr, sizeof(mPeerSockAddr));
			m_PeerAddrLen = sizeof(mPeerSockAddr);
			bzero(mSelfIpAddr, 64);

			pthread_rwlock_init(&mAcceptListRWLock, 0);
			pthread_rwlock_init(&mRequestListRWLock, 0);
			FD_ZERO(&mSockFdSet);
		}

		// copy-ctor
		INGwSpTcpConnMgr(const INGwSpTcpConnMgr& p_INGwSpTcpConnMgr) 
		{ }

		// assignment op
		const INGwSpTcpConnMgr& 
		operator=(const INGwSpTcpConnMgr& p_INGwSpTcpConnMgr) 
		{ return *this;}

		// dtor
		~INGwSpTcpConnMgr()
		{ }
};

#endif 



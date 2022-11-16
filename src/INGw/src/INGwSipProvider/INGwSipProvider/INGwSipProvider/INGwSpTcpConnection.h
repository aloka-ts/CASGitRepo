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
//     File:     INGwSpTcpConnection.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef INGW_SP_TCP_CONNECTION_H_
#define INGW_SP_TCP_CONNECTION_H_

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
#include <INGwInfraUtil/INGwIfrUtlRefCount.h>

class INGwSpTcpConnection : public virtual INGwIfrUtlRefCount
{
	public:


    static unsigned long getConnHash(const char*  p_RemoteIpAddr,
                                     const int    p_RemotePort,
																		 const char*  p_LocalIpAddr = NULL,
																		 const int    p_LocalPort = -1);


		int 		initialize(const char* 	p_RemoteIpAddr, 
							         const int 	  p_RemotePort,
											 const char*  p_LocalIpAddr = NULL,
											 const int    p_LocalPort = -1);

		int		 	sendMsg(const  char*   p_Msg,
								    const  int	   p_MsgLen);

    int     connect();

		bool    isSockConnected(int p_SockId);

    inline void    setSocketFd(int p_SockId)
		{
			m_SockId = p_SockId;
		}

    inline int     getSocketFd()
		{
			return m_SockId;
		}

    int     close();

    inline unsigned long getHash()
		{
			return m_ConnHash;
		}

		INGwSpTcpConnection();

		// dtor
		~INGwSpTcpConnection()
		{ }

    static std::string m_INGwIp;
    static int m_INGwPort;


    static void initINGwNWparam();

	private:

    std::string m_SelfIp;
	  int m_SelfPort;

	  std::string m_RemoteIp;
	  int m_RemotePort;

	  int m_SockId;

    unsigned long m_ConnHash;
		pthread_rwlock_t      m_SendRWLock;


		// copy-ctor
		INGwSpTcpConnection(const INGwSpTcpConnection& p_INGwSpTcpConnection) 
		{ }

		// assignment op
		const INGwSpTcpConnection& 
		operator=(const INGwSpTcpConnection& p_INGwSpTcpConnection) 
		{ return *this;}

};

#endif 



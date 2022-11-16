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
//     File:     INGwSpTcpConnection.C
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

#include <sys/types.h>
#include <sys/uio.h>
#include <netinet/in.h>
#include <netinet/tcp.h>
#include <net/if.h>
#include <inttypes.h>
#include <limits.h>

#include <INGwSipProvider/INGwSpTcpConnection.h>
#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpSipContext.h>
#include <INGwSipProvider/INGwSpSipStackIntfLayer.h>
#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>
#include <INGwInfraUtil/INGwIfrUtlHashMap.h>

int INGwSpTcpConnection::m_INGwPort = 5060;
std::string INGwSpTcpConnection::m_INGwIp;

void INGwSpTcpConnection::initINGwNWparam()
{
		m_INGwIp = INGwIfrPrParamRepository::getInstance().getValue(ingwFLOATING_IP_ADDR);
		m_INGwPort = 
       atoi((INGwIfrPrParamRepository::getInstance().getValue(ingwSIP_STACK_LISTENER_PORT)).c_str());
}


INGwSpTcpConnection::INGwSpTcpConnection()
{
}

int
INGwSpTcpConnection::initialize(const char*  p_RemoteIpAddr,
					                      const int    p_RemotePort,
																const char*  p_LocalIpAddr,
																const int    p_LocalPort)
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnection::initialize");

	m_RemoteIp = p_RemoteIpAddr;
	m_RemotePort = p_RemotePort;

	if(p_LocalIpAddr != NULL)
	{
		m_SelfIp = p_LocalIpAddr;
	}
	else
	{
		//m_SelfIp = INGwIfrPrParamRepository::getInstance().getValue(ingwFLOATING_IP_ADDR);
    m_SelfIp = INGwSpTcpConnection::m_INGwIp;
	}

	if(p_LocalPort != -1)
	{
		m_SelfPort = p_LocalPort;
	}
	else
	{
		//m_SelfPort = atoi((INGwIfrPrParamRepository::getInstance().getValue(ingwSIP_STACK_LISTENER_PORT)).c_str());
    m_SelfPort = INGwSpTcpConnection::m_INGwPort;
	}

  pthread_rwlock_init(&m_SendRWLock, 0);

	m_ConnHash = getConnHash(m_RemoteIp.c_str(), m_RemotePort, m_SelfIp.c_str(), m_SelfPort);

  LogINGwTrace(false, 0, "OUT INGwSpTcpConnection::initialize");
	return 0;
}

int
INGwSpTcpConnection::close()
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnection::close");

	if (::close(m_SockId) < 0)
	{
    logger.logINGwMsg(false, ERROR_FLAG, 0,
										 "SOCKET CLOSE ERROR sockFd:%d of <%s> : <%d>",
									    m_SockId, m_RemoteIp.c_str(), m_RemotePort);
	}

  LogINGwTrace(false, 0, "OUT INGwSpTcpConnection::closeClientConn");
	return 0;;
}

int
INGwSpTcpConnection::sendMsg(const char*		p_Msg,
					   	               const int		  p_MsgLen)
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnection::sendMsg");
  pthread_rwlock_wrlock(&m_SendRWLock);

	// Fill iovec structure
	struct iovec    iovData[1];
  fd_set write_fds;
  int lTimeOut = 10;

	iovData[0].iov_base     = (caddr_t)(p_Msg);
	iovData[0].iov_len      = p_MsgLen;

/*
	if (writev(m_SockId, (const iovec*)&iovData, 1) < 0)
	{
    logger.logINGwMsg(false, ERROR_FLAG, 0,
		                  "SEND ERROR. Send to network failed with "
											"errno:%d, errTxt:%s, sockFd:%d for client <%s>",
                       errno, strerror(errno), 
											 m_SockId, m_RemoteIp.c_str());

    pthread_rwlock_unlock(&m_SendRWLock);
    LogINGwTrace(false, 0, "OUT INGwSpTcpConnection::sendMsg");
		return -1;
	}
*/

  int lLengthToSend = p_MsgLen;
  int retVal = 0;

  while(lLengthToSend)
  {
    errno = 0;

	  retVal = writev(m_SockId, (const iovec*)&iovData, 1);

    if(retVal < 0)
    {
      if(errno == EINTR)
      {
        logger.logINGwMsg(false, TRACE_FLAG, 0,
		                      "Writev interrupted with "
					    						"errno:%d, errTxt:%s, sockFd:%d for client <%s>."
                          " Will try again to send",
                           errno, strerror(errno), 
								    			 m_SockId, m_RemoteIp.c_str());
        continue;
      }

      if(errno == EAGAIN)
      {
        time_t wait_start = time(NULL);

        while(true)
        {
          FD_ZERO(&write_fds);  FD_SET(m_SockId, &write_fds);

          const char* lWriteTimeout = getenv("TCP_WRITE_TIMEOUT");
          if(lWriteTimeout != NULL)
          {
            lTimeOut = atol(lWriteTimeout);
          }

          struct timeval time_out;
          time_out.tv_sec = lTimeOut;
          time_out.tv_usec = 0;

          retVal = select(m_SockId + 1, NULL, &write_fds, NULL, &time_out);

          if((retVal == -1) && (errno == EINTR))
          {
            logger.logINGwMsg(false, TRACE_FLAG, 0,
		                          "Select  interrupted for wrting with "
					        						"errno:%d, errTxt:%s, sockFd:%d for client <%s>."
                              " Will try again to send",
                               errno, strerror(errno), 
								    	    		 m_SockId, m_RemoteIp.c_str());
            continue;
          }
          break;
        }

        if(retVal == 0)
        {
          time_t wait_end = time(NULL);
          logger.logINGwMsg(false, ERROR_FLAG, 0,
		                        "Write failed due to timedout."
					      						"errno:%d, errTxt:%s, sockFd:%d for client <%s>.",
                             errno, strerror(errno), 
					  	    	    		 m_SockId, m_RemoteIp.c_str());
          retVal = -1;
          break;
        }

        if(retVal == -1)
        {
          logger.logINGwMsg(false, ERROR_FLAG, 0,
		                        "Select failed for write with "
					      						"errno:%d, errTxt:%s, sockFd:%d for client <%s>.",
                             errno, strerror(errno), 
					  	    	    		 m_SockId, m_RemoteIp.c_str());
          retVal = -1;
          break;
        }

        continue;
      }// end of if errno is EAGAIN
      else
      {
        logger.logINGwMsg(false, ERROR_FLAG, 0,
		                      "Write failed with "
				      						"errno:%d, errTxt:%s, sockFd:%d for client <%s>.",
                           errno, strerror(errno), 
				  	    	    		 m_SockId, m_RemoteIp.c_str());
        retVal = -1;
        break;
      }
    } //end of if retVal less than 0

    lLengthToSend -= retVal;

    if(lLengthToSend == 0)
    {
      retVal = 0;
      break;
    }

    if(retVal < iovData[0].iov_len)
    {
      iovData[0].iov_len -= retVal;
      iovData[0].iov_base = ((char *)iovData[0].iov_base) + retVal;
      retVal = 0;
    }

  }// end of while lLengthToSend

  pthread_rwlock_unlock(&m_SendRWLock);
  LogINGwTrace(false, 0, "OUT INGwSpTcpConnection::sendMsg");
	return 0;
}

bool    
INGwSpTcpConnection::isSockConnected(int p_SockFd)
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnection::isSockConnected");
	struct sockaddr_in  cli_addr;

	int lClilen = 0;
	lClilen = sizeof(cli_addr);

	if (p_SockFd == -1)
	{
    LogINGwTrace(false, 0, "OUT INGwSpTcpConnection::isSockConnected");
		return false;
	}	

	if(getpeername(p_SockFd, (struct sockaddr *)&cli_addr, &lClilen) == -1)
	{
    LogINGwTrace(false, 0, "OUT INGwSpTcpConnection::isSockConnected");
		return false;
	}	

  LogINGwTrace(false, 0, "OUT INGwSpTcpConnection::isSockConnected");
	return true;
}

int     
INGwSpTcpConnection::connect()
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnection::connect");

	int lSocketId = socket(AF_INET, SOCK_STREAM, 0);
  int flags = 0;
	if ( lSocketId == -1)
	{
		logger.logINGwMsg(false, ERROR_FLAG, 0, "Socket creation failed. [%s]", 
									    strerror(errno));
    return -1;
	}

  if((flags = fcntl(lSocketId, F_GETFL, 0)) < 0)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "Error getting socket status. [%s]",
                    strerror(errno));
    return -1;
  }
  flags |= O_NONBLOCK;

  if(fcntl(lSocketId, F_SETFL, flags) < 0)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "Error setting nonBlocking. [%s]",
                  strerror(errno));
    return -1;
  }

  int flag = 1;
  if(setsockopt(lSocketId, IPPROTO_TCP, TCP_NODELAY, &flag, sizeof(int)) < 0)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "Unable to disable Nagle algorithm. [%s]",
                   strerror(errno));
    return -1;
  }

  int timeout = 10; //10 Secs.

  struct sockaddr_in srvAddr;
  memset(&srvAddr, 0, sizeof(srvAddr));
  srvAddr.sin_family = AF_INET;
  srvAddr.sin_port = htons((short)m_RemotePort);
  unsigned int lRemoteIp  = inet_addr(m_RemoteIp.c_str());
  srvAddr.sin_addr.s_addr = htonl(lRemoteIp);

	m_SockId = lSocketId;

  errno = 0;
  int retVal = 0;
  fd_set read_fds;   
  fd_set write_fds;

  if(::connect(lSocketId, (struct sockaddr *)&srvAddr, sizeof(srvAddr) ) < 0)
  {
    if(errno != EINPROGRESS)
    {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "Unable to connect to server <%s>:<%d> due to. [%s]",
                        m_RemoteIp.c_str(), m_RemotePort, strerror(errno));
      close();
      return -1;
    }

    while(true)
    {
       errno = 0;

       FD_ZERO(&read_fds);   FD_SET(lSocketId, &read_fds);
       FD_ZERO(&write_fds);  FD_SET(lSocketId, &write_fds);

       int lTimeout = 10;
       const char* lConnTimeout = getenv("TCP_CONNECT_TIMEOUT");
       if(lConnTimeout != NULL)
       {
         lTimeout = atol(lConnTimeout);
       }

       struct timeval time_out;
       time_out.tv_sec = lTimeout;
       time_out.tv_usec = 0;

       retVal = select(lSocketId + 1, &read_fds, &write_fds, NULL, &time_out);

       if((retVal == -1) && (errno == EINTR))
       {
         logger.logMsg(TRACE_FLAG, 0, "Select interrupted. [%s]. Will try again.",
                       strerror(errno));
         continue;
       }

       break;
    }// end of while true for select

    if(retVal == 0)
    {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
                        "Unable to connect to server <%s>:<%d> Timeout due to. [%s]",
                        m_RemoteIp.c_str(), m_RemotePort, strerror(errno));
      close();
      return -1;
    }

    if(retVal == -1)
    {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
                        "Unable to connect to server <%s>:<%d>. Select Failed due to. [%s]",
                        m_RemoteIp.c_str(), m_RemotePort, strerror(errno));
      close();
      return -1;
    }

    if(FD_ISSET(lSocketId, &read_fds) || FD_ISSET(lSocketId, &write_fds))
    {
      int err;
      socklen_t errlen;

      errlen = sizeof(err);

      if(getsockopt(lSocketId, SOL_SOCKET, SO_ERROR, &err, &errlen) < 0)
      {
        logger.logINGwMsg(false, ERROR_FLAG, 0, 
                          "Unable to connect to server <%s>:<%d> due to. [%s]",
                          m_RemoteIp.c_str(), m_RemotePort, strerror(errno));
        close();
        return -1;
      }

      if(err != 0)
      {
        logger.logINGwMsg(false, ERROR_FLAG, 0, 
                          "Unable to connect to server <%s>:<%d> due to. [%s]",
                          m_RemoteIp.c_str(), m_RemotePort, strerror(err));
        close();
        return -1;
      }

    }// end od if FD_SET
  }// end of if connect

  // Connect is successfull

  LogINGwTrace(false, 0, "OUT INGwSpTcpConnection::connect");
  return lSocketId;
}

unsigned long 
INGwSpTcpConnection::getConnHash(const char*  p_RemoteIpAddr,
																 const int    p_RemotePort,
																 const char*  p_LocalIpAddr,
																 const int    p_LocalPort)
{
  LogINGwTrace(false, 0, "IN INGwSpTcpConnection::getConnHash");

	std::string l_SelfIp;
	int l_SelfPort;

	if(p_LocalIpAddr != NULL)
	{
		l_SelfIp = p_LocalIpAddr;
	}
	else
	{
    //std::string l_FIPOidStr = ingwFLOATING_IP_ADDR;
		//l_SelfIp = INGwIfrPrParamRepository::getInstance().getValue(l_FIPOidStr);
    l_SelfIp = INGwSpTcpConnection::m_INGwIp;
	}

	if(p_LocalPort != -1)
	{
		l_SelfPort = p_LocalPort;
	}
	else
	{
    //std::string l_StackPortOid = ingwSIP_STACK_LISTENER_PORT;
		//l_SelfPort = atoi((INGwIfrPrParamRepository::getInstance().getValue(l_StackPortOid)).c_str());
    l_SelfPort = INGwSpTcpConnection::m_INGwPort;
	}
	char l_ConnAddr[128]; 
	memset(&l_ConnAddr, 0, sizeof(l_ConnAddr));

	sprintf(l_ConnAddr, "%s%d%s%d", p_RemoteIpAddr, p_RemotePort, l_SelfIp.c_str(), l_SelfPort);


	unsigned long retVal = -1;

#ifdef USE_STD_ELF_HASH
	retVal = elf_hash(l_ConnAddr); 
#else
	retVal = elf_hash_func(l_ConnAddr);
#endif

  logger.logINGwMsg(false, TRACE_FLAG, 0, "Hash for <%s> is <%u>",
                    l_ConnAddr, retVal);
  LogINGwTrace(false, 0, "IN INGwSpTcpConnection::getConnHash");

  return retVal;
}

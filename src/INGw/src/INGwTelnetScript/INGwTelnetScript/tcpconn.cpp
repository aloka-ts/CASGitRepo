//============================================================================
// Name        : tcpconn.cpp
// Author      : sanjay kumar
// Version     :
// Copyright   : 
// Description : Class to hold the TCP connection object with the server
//============================================================================

#include <iostream>
#include "tcpconn.h"
#include <netdb.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <strings.h>
#include <unistd.h>

using namespace std;
using namespace AGUtil;

namespace AG
{ 
  const char* UNKNOWN_HOST= "Unknown Host" ;
  CLogger *log 				    = CLogger::getLogger();

  CTcpConn* CTcpConn::mInstance = NULL;
  bool CTcpConn::mIsCreated     = false;
  
  CTcpConn* CTcpConn::getRemoteConn()
	{
    if ( ! mIsCreated )
		{
      if ( mInstance == NULL ){
        mInstance = new CTcpConn();
        mIsCreated = true;
      } 
    }
    
    return mInstance;
  }

  CTcpConn::CTcpConn(){
    mIsInit 		 = false;  //Connection not initiliazed yet
    mSockFd 		 = -1; 
    mRemoteServer= UNKNOWN_HOST;
    mRemotePort  = -1;
    mLocalPort   = -1;
  }

  /**
  *  Desc: opens the socket and connects it with the server
  *        if it fails to do so, it throws CInitException 
  *  Arguments:   1. remote server name as string
  *               2. remote port number as int 
  *  Exception:
  *    if failed to initialize or connect to remote server c'tor 
	*    raises CInitException
  */
	RetStatus 
	CTcpConn::initialize(string aServer,int aPort) throw (CInitException) {
    if( mIsInit ) 
			return eSuccess; //avoid duplicate initialization

    mRemoteServer = aServer;
    mRemotePort  = aPort; 

    //cout<<"Initializing with rmeote server "<<mRemoteServer<<":"<<mRemotePort
		//<<endl;

    if( mRemoteServer.compare( UNKNOWN_HOST ) == 0  || mRemotePort == -1 )
		{
      throw CInitException ( "Remote Server or Remote Port are not specified");
    }

		//cout<<"Creating Socket"<<endl;
    mSockFd = socket( AF_INET, SOCK_STREAM, 0 );
    if( mSockFd < 0 )
		{
      cerr<<"Socket syscall failed"<<endl;
      throw CInitException ( "Socket API Failed" );
    }

    /* bind local socket to any port number */
    struct sockaddr_in local_addr;
    local_addr.sin_family 		 = AF_INET;
    local_addr.sin_addr.s_addr = htonl(INADDR_ANY);
    local_addr.sin_port 			= htons(0);
    memset(local_addr.sin_zero, 0, 8);

    int rc = bind(mSockFd, (struct sockaddr *) &local_addr, sizeof(local_addr));

    if (rc < 0) 
    {
        perror("bind API failed: ");
        exit(4);
    }

    struct sockaddr_in remoteAddr;

    struct hostent *hostrecord = gethostbyname ( mRemoteServer.c_str() );
    if( hostrecord == NULL )
		{
      herror( "gethostbyname syscall failed" );
      throw CInitException ( "gethostbyname API Failed" );
    }

    bzero( (char*) &remoteAddr, sizeof( remoteAddr ) ); 
    bcopy((char *)hostrecord->h_addr, (char *)&remoteAddr.sin_addr.s_addr, 
									hostrecord->h_length);

    remoteAddr.sin_family = AF_INET;
    remoteAddr.sin_port   = htons( mRemotePort );
     
		//cout<<"Socket created with fd "<<mSockFd<<endl;
		//cout<<"Connecting "<<mRemoteServer<<":"<<mRemotePort<<"...."<<endl;

    if(connect(mSockFd,(struct sockaddr *) &remoteAddr,sizeof(remoteAddr)) < 0 )
		{
      //Do a little cleanup
      //we opened the socket, and failed to connect, let's close the socket fd
      close(mSockFd);
      mSockFd = -1;
      cout<<"Connect to remote server failed"<<endl;
      throw CInitException ( "Connect API Failed" );
    }else{
      log->logMsg(eTrace,"Connection Successful");
    }

    //declare the connection available
    mIsInit = true;
		//cout<<"Connected with "<<mRemoteServer<<endl;
    return eSuccess;
  }

  int CTcpConn::sendCmd(string &aCmd)
	{
    if ( !mIsInit ) 
			return -1; // connection not initialized 

    char *buf = new char[aCmd.length() + 2 ];
    sprintf(buf,"%s\r\n", aCmd.c_str());

    ssize_t bytesSent =  send(mSockFd,buf, aCmd.length()+2, 0);

		//cout<<bytesSent<<"  sent"<<endl;
		//cout<<"Sent ["<<aCmd<<"]on ["<<mSockFd<<"] size="<<bytesSent<<endl;

    delete[] buf;
    return bytesSent;
  }

  int CTcpConn::readRsp(char aRsp[])
	{
		//cout<<"Inside readRsp"<<endl;
    if ( !mIsInit ) 
			return -1; // connection not initialized 

    ssize_t bytesRcvd =  read(mSockFd,(void *)aRsp , MAX_RSP_SIZE);
		//cout<<"obtained "<<bytesRcvd<<endl;
    aRsp[bytesRcvd]='\0';
    return bytesRcvd;
  }
} //end of AG namespace

#if STUB
using namespace AG;
  main(){
    try{
      CTcpConn conn("10.32.8.153", 23);
    }catch(...){
      cout<<"Connection failure"<<endl;
    }
  }
#endif


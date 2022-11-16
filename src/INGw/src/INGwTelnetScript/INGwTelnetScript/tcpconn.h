//============================================================================
//============================================================================
// Name        : tcpconn.h
// Author      : sanjay kumar
// Version     :
// Copyright   : 
// Description : Class to hold the TCP connection object with the server
//============================================================================

#ifndef __AG_TCP_CONN__
#define __AG_TCP_CONN__


#include "logger.h"
#include "errcodes.h"
//#include "request.h"
//#include "response.h"
#include "exceptions.h"

using namespace std;

namespace AG{ //AG for Agnity
  const int MAX_RSP_SIZE=2500;

  class CTcpConn{
    public:

     RetStatus initialize(string aServer,  int aPort) throw (CInitException);

     static CTcpConn *getRemoteConn();
     bool isConnected() { return mIsInit;}

    public: 
      int sendCmd(string &cmd);
      int readRsp(char aRsp[]);

      int getSockFd() { return mSockFd; }

    protected: 
      CTcpConn();
      
      static CTcpConn *mInstance;

      string mRemoteServer;
      int mRemotePort;
      int mLocalPort;
      int mSockFd;
      static bool mIsCreated;
      bool mIsInit; //Check this flag if the connection is usable or not

      ~CTcpConn(){
        //cleanup
        if( mSockFd != -1 )
          close(mSockFd);
      }
  };
} //end of agnity client

#endif
// Name        : tcpconn.h
// Author      : sanjay kumar
// Version     :
// Copyright   : 
// Description : Class to hold the TCP connection object with the server
//============================================================================

#ifndef __AG_TCP_CONN__
#define __AG_TCP_CONN__


#include "logger.h"
#include "errcodes.h"
//#include "request.h"
//#include "response.h"
#include "exceptions.h"

using namespace std;

namespace AG{ //AG for Agnity
  const int MAX_RSP_SIZE=1020;

  class CTcpConn{
    public:

     RetStatus initialize(string aServer,  int aPort) throw (CInitException);

     static CTcpConn *getRemoteConn();
     bool isConnected() { return mIsInit;}

    public: 
      int sendCmd(string &cmd);
      int readRsp(string &aRsp);

      int getSockFd() { return mSockFd; }

    protected: 
      CTcpConn();
      
      static CTcpConn *mInstance;

      string mRemoteServer;
      int mRemotePort;
      int mLocalPort;
      int mSockFd;
      static bool mIsCreated;
      bool mIsInit; //Check this flag if the connection is usable or not

      ~CTcpConn(){
        //cleanup
        if( mSockFd != -1 )
          close(mSockFd);
      }
  };
} //end of agnity client

#endif

//============================================================================
// Name        : cmdhandler.h
// Author      : Sanjay Kumar
// Version     :
// Copyright   : 
// Description : This is the default command handler for a command
//============================================================================

#ifndef __AG_CMD_HANDLER__
#define __AG_CMD_HANDLER__

#include "tcpconn.h"
#include "exceptions.h"

using namespace std;

namespace AG{ //AG for Agnity

  class CCmdHandler{
    public:
      CCmdHandler() { }
      //CCmdHandler(string aServer, unsigned long aPort );

      virtual void setTcpConn(string aServer, unsigned long aPort); 
      virtual void action(); // throw CActionPerformException
      virtual void performRemoteAction(); // throw CActionPerformException
      virtual void performLocalAction(); // throw CActionPerformException

      ~CCmdHandler() {}

    protected: 
      string mServer;
      unsigned long mPort;
  };
} //end of agnity namespace

#endif

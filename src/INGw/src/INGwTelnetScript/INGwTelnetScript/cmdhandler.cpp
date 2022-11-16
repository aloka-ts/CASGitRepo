//============================================================================
// Name        : cmdhandler.h
// Author      : Sanjay Kumar
// Version     :
// Copyright   : 
// Description : This is the default command handler for a command
//============================================================================

#include "cmdhandler.h"

using namespace std;

namespace AG{ //AG for Agnity

/*
   CCmdHandler::CCmdHandler(string aServer, unsigned long aPort) {
     try {
       mRemoteServer = new CTcpConn ( aServer, aPort );
     }catch ( CInitException &ex ){
       cerr<<"CCmdHandler Failed: "<<ex<<endl;
     }
   }
*/


   void CCmdHandler::setTcpConn(string aServer, unsigned long aPort) {
     mServer = aServer;
     mPort = aPort;
/*
     //set Tcp Connection only if its not set
     if ( ! mRemoteServer ){
       try {
         mRemoteServer = new CTcpConn ( aServer, aPort );
       }catch ( CInitException &ex ){
         cerr<<"CCmdHandler Failed: "<<ex<<endl;
       }
     }
*/
   }

   void CCmdHandler::action(){
     cout<<"Inside the default action method"<<endl;
   }

   void CCmdHandler::performLocalAction(){
     cout<<"Inside the default perform Local Action method"<<endl;
   }
   void CCmdHandler::performRemoteAction(){
     cout<<"Inside the default perform Remote Action method"<<endl;
   }

//   ~CCmdHandler::CCmdHandler(){
 //    cout<<"D;tor called for ccmd handler"<<endl;
  // }
} //end of agnity namespace


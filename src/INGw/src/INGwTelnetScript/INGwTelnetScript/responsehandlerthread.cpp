//============================================================================
// Name        : responsehandlerthread.cpp
// Author      : Sanjay Kumar
// Version     :
// Copyright   : 
// Description : thread that waits for response from the server, blocks on read
//============================================================================

#include "responsehandlerthread.h"
#include "inputhandlerthread.h"
#include "tcpconn.h"
#include <string.h>
#include "util.h"
#include <vector>
#include <pthread.h>
#include "logmanager.h"
#include "configmanager.h"
#include "cmd.h"
#include "cmdvalidator.h"


using namespace std;
using namespace AGUtil;

  extern pthread_mutex_t gIOMutex;
  extern pthread_cond_t gIOCond;
  extern bool isResponseAvailable;
namespace AG {
  const char REMOTE_SERVER_PROMPT[] = "INGW > ";
  const char REMOTE_HELP_LIST_TEXT[] = "CommandList:";


  void CRespHandlerThread::execute(){
    char rsp[MAX_RSP_SIZE];

    //flush out the prompt
    if( CTcpConn::getRemoteConn()->isConnected() ) 
         CTcpConn::getRemoteConn()->readRsp(rsp);
#if PROMPT
    while( true )
#else
		int count = 1;
		while(count)
#endif
		{
      try 
			{
        if( CTcpConn::getRemoteConn()->isConnected() ) 
				{
          CTcpConn::getRemoteConn()->readRsp(rsp);

#ifndef PROMPT
				  count=0;
#endif

          string displayStr (rsp);

          //In case remote server rejects command it throws 
          //back help text which we'll intercept and wouldn't show
          size_t pos = displayStr.find( REMOTE_HELP_LIST_TEXT );
          if( pos != string::npos )
					{
            //Invalid command, lets show the available commands
            try 
						{ 
              CCmd help(string("help"));
              CCmdValidator::getCmdValidator()->
														 getValidatedCommand(help).action(); 
            }
						catch(...)
						{
              cerr<<"INGW internal error while dispatching command"<<endl;
            }
            cout<<getPrompt();
            continue;
          }

          //We don't want to show the remote server prompt
          pos = displayStr.find( REMOTE_SERVER_PROMPT );

          //if there is somekind of response from the remote server,
          //its our responsibility to show the prompt from here
          //i.e. remote prompts are simulated from here

          if( pos != string::npos )
					{
            string outStr = displayStr.substr(0,pos);
            //Sometimes remote server prompt comes as a separate response
            //display something, if we have some text after removing prompt
            if( outStr.length() > 0 ) {
							cout <<"Response"<<endl;
              cout<<outStr;
							cout <<"Response - done"<<endl;
              if ( CConfigManager::getConfigManager()->isLogOptionOn() )
                CLogManager::getLogManager()->writeLog( outStr );

#if PROMPT
              cout<<getPrompt();
#endif
            }
          }
          else
					{
            cout<<displayStr;
            if ( CConfigManager::getConfigManager()->isLogOptionOn() )
              CLogManager::getLogManager()->writeLog( displayStr );
#if PROMPT
            cout<<getPrompt();
#endif
          }

        }else{
          cout<<"INGW Probe is not connected to remote server"<<endl;
          cout<<"exiting..."<<endl;
          exit(2);
        }
      }catch(...){
        cerr<<"Unexpected error in read operation"<<endl;
        exit(2);
      }
      memset( rsp, 0, MAX_RSP_SIZE);
isResponseAvailable = false;
//pthread_mutex_unlock( &gIOMutex );
    }
  }
}

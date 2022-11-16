//============================================================================
// Name        : inputhandlerthread.h
// Author      : Sanjay Kumar
// Version     :
// Copyright   : 
// Description : Input Handler Thread. 
//                 a. Displays the prompt
//                 b. Does all the input handling and display of response
//============================================================================

#include <iostream>
#include "cmd.h"
#include "inputhandlerthread.h"
#include "cmdvalidator.h"
#include "configmanager.h"
#include "logmanager.h"
#include "util.h"

using namespace std;
using namespace AG;
using namespace AGUtil;

extern pthread_mutex_t gIOMutex; // = PTHREAD_MUTEX_INITIALIZER;
extern pthread_cond_t gIOCond ;// = PTHREAD_COND_INITIALIZER;
extern bool isResponseAvailable ;// = PTHREAD_COND_INITIALIZER;

namespace AG {


  CInputHandlerThread::CInputHandlerThread(): mPrompt("INGW-PROBE>"){
    //cout<<"Inside CInputHandlerThread"<<endl;
  }

  void CInputHandlerThread::execute()
	{
    string userInput;

#if PROMPT
    cout<<getPrompt();
    while(true) 
#else
	int count=1;
		while(count)
#endif
		{
      readLine(userInput);
#ifndef PROMPT
	count=0;
#endif
      //wait for next command if user just enters
      if( userInput.empty() )
			{
				if( CConfigManager::getConfigManager()->isLogOptionOn() ) 
				{
					CLogManager::getLogManager()->
					writeLog( CUtil::getCurrentTime().append(getPrompt()) ) ;
       	}
       	cout<<getPrompt();
       	continue;
      }
      try
			{ 
#ifndef PROMPT
				if(CTcpConn::getRemoteConn()->isConnected())
				{
					CTcpConn::getRemoteConn()->sendCmd(userInput);
				}
#else
        CCmd inputCmd(userInput);
        if( CCmdValidator::getCmdValidator()->validate(inputCmd) )
				{
          // Once the command is validated, we need to perform. 
          // lets send the command to the telnet server
            try 
						{
              if( CConfigManager::getConfigManager()->isLogOptionOn() ) 
							{
                CLogManager::getLogManager()->writeLog( CUtil::getCurrentTime().append(getPrompt()).append( (string) inputCmd ) );
              }

              //Valid commands have the remote command to be executed
              //Lets now provide the arguments to it for dispatching
              CCmdValidator::getCmdValidator()->
										 getValidatedCommand(inputCmd).
										 setActualArgumentMap( inputCmd.getActualArgumentMap() );

              CCmdValidator::getCmdValidator()->
														 getValidatedCommand(inputCmd).action();

              //If the command is local or there is no response expected from 
							// server then its our responsibility to show the prompt
              //i.e. local prompts are simulated from here

              if ( CCmdValidator::getCmdValidator()->getValidatedCommand
								(inputCmd).isLocalCommand() || //if command is local show prompt
					      ! CCmdValidator::getCmdValidator()->
								getValidatedCommand(inputCmd).getRespExpectedState() ) 
																		//if no response expected show prompt
																		//cout<<"no response expected"<<endl;
                    cout<<getPrompt();
               
            }catch(...){
              cerr<<"INGW internal error while dispatching command"<<endl;
            }
        }else
				{ 
          cout<<"\tInvalid Command"<<endl;

          if( CConfigManager::getConfigManager()->isLogOptionOn() ) 
					{
            CLogManager::getLogManager()->
						writeLog( CUtil::getCurrentTime().append(getPrompt()).
																							append( inputCmd ) );
          }

          //If the command (without arguments ) is correct, 
					// lets show the correct format
          if(CCmdValidator::getCmdValidator()->getCommandMap().
						 find(inputCmd.getCommand()) != CCmdValidator::getCmdValidator()->
						 getCommandMap().end() )
					{
            cout<<"usage: "<< 
						CCmdValidator::getCmdValidator()->
						getValidatedCommand(inputCmd).getCorrectUsage()<<endl;

            if( CConfigManager::getConfigManager()->isLogOptionOn() ) 
						{
              CLogManager::getLogManager()->
							writeLog( CCmdValidator::getCmdValidator()->
							getValidatedCommand(inputCmd).getCorrectUsage() ) ;
            }
          }

          //Invalid command, lets show the available commands
          try 
					{ 
            CCmd help(string("help"));
            CCmdValidator::getCmdValidator()->
													 getValidatedCommand(help).action(); 
          }catch(...)
					{
              cerr<<"INGW internal error while dispatching command"<<endl;
          }
          cout<<getPrompt();
        }
#endif
      }catch(CInvalidCommandFormat &ex){
        cout<<ex<<endl;
      }
    }
  }

  void CInputHandlerThread::readLine(string &aLine){
#if PROMPT
    getline(cin,aLine);
    if ( cin.eof() ) {
      cout<<"Exiting..."<<endl;
      exit ( 10 );
    }
#else
	// read command from CConfigManager
	aLine = (CConfigManager::getConfigManager()->getConfigMap())[COMMAND_INPUT];
	cout << "Executing command: [" << aLine << "]" << endl;
#endif
  }
}


#if STUB
using namespace AGUtil;

main(){
   try{
    CCmdValidator::getCmdValidator()->setValidCommand(CCmd(string("exit"))); 
    CCmdValidator::getCmdValidator()->setValidCommand(CCmd(string("getLinkSets"))); 
    CCmdValidator::getCmdValidator()->setValidCommand(CCmd(string("getLinkDetail linkid"))); 
  }catch ( CInvalidCommandFormat &ex ){
    cerr<<"Error : "<<endl<<ex<<endl;
  }

  CInputHandlerThread t1;

  t1.setPrompt("SANJAY-TEST-INGW > ");
  t1.run();


  pthread_join(t1.getThread(),NULL);
}
#endif

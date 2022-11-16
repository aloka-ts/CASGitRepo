//============================================================================
// Name        : ingwprobemain.cpp
// Author      : Sanjay Kumar
// Version     :
// Copyright   : 
// Description : Main program for ingwprobe 
//              
//============================================================================


#include "cmd.h"
#include "cmdvalidator.h"
#include "inputhandlerthread.h"
#include "responsehandlerthread.h"
#include "logmanager.h"
#include "exceptions.h"
#include "configmanager.h"
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/stat.h>
#include <signal.h>
#include <sys/types.h>
#include <fcntl.h>
#include <termios.h>


using namespace AG;
using namespace AGUtil;

pthread_cond_t gIOCond;
pthread_mutex_t gIOMutex ;
bool isResponseAvailable = false;

void initCommands(){
   try{

// GET COMMANDS
    CCmdValidator *cmdvalidator = CCmdValidator::getCmdValidator();
    cmdvalidator->setValidCommand(CCmd("exit","", false, true));  //no response,local command
    cmdvalidator->setValidCommand(CCmd("help","", false, true));  //no response, local command
    cmdvalidator->setValidCommand(CCmd("getRegisteredSAS", "get registeredSAS")); 
    cmdvalidator->setValidCommand(CCmd("getDetailCount", "get detail-count")); 
    cmdvalidator->setValidCommand(CCmd("getLoadDistributorInfo", "get loadDistributorInfo")); 
    cmdvalidator->setValidCommand(CCmd("getAllParamValues", "get all-param-val")); 
    cmdvalidator->setValidCommand(CCmd("getProviderStats", "get provider-stats")); 
    cmdvalidator->setValidCommand(CCmd("getSipDebugLevel", "get sip-debug-level")); 
    cmdvalidator->setValidCommand(CCmd("getRoleDetail", "get role-detail")); 
    cmdvalidator->setValidCommand(CCmd("getSipMsgStats", "get sip-msg-stats")); 
    cmdvalidator->setValidCommand(CCmd("getTcapMsgStats", "get tcap-msg-stats")); 

//SET COMMANDS
    //set codec-debug-level <<0|1|2|3>> command
    CCmd setCodecCmd("setCodecDebugLevel 1", "set codec-debug-level");
    //Specify the argument types for the command
    map<int,CmdArgType> setMap;
    setMap.insert(pair<int,CmdArgType>(1,eInteger));
    setCodecCmd.setArgTypeMap(setMap);

    //Specify the arguemnt range for the command
    map<int,string> rangeMap;
    rangeMap.insert(pair<int,string>(1, "<<0|1|2|3>>"));

    setCodecCmd.setArgValueRangeMap( rangeMap );

    cmdvalidator->setValidCommand(setCodecCmd);

    //set sip-debug-level <<0 | 1 >>
    CCmd setSipDebugLevel("setSipDebugLevel 1","set sip-debug-level", false); //no response expected
    //specify the argument types for the command
    map<int,CmdArgType> sdArgTypeMap;
    sdArgTypeMap.insert( pair<int,CmdArgType>(1,eInteger));
    setSipDebugLevel.setArgTypeMap( sdArgTypeMap );

    //Specify the argument value range
    map<int,string> sdValueRangeMap;
    sdValueRangeMap.insert( pair<int,string> (1,"<<0|1>>"));
    
    setSipDebugLevel.setArgValueRangeMap( sdValueRangeMap );
   
    cmdvalidator->setValidCommand( setSipDebugLevel );


    //set pdu-debug-level <<0 | 1 >>
    CCmd setPduDL( "setPduDebugLevel 1" ,"set pdu-debug-level", false);
    setPduDL.setArgTypeMap( sdArgTypeMap ); //has similar argument type as that of sipdebug command
    setPduDL.setArgValueRangeMap( sdValueRangeMap ); //has similar value range
    cmdvalidator->setValidCommand( setPduDL );

    //set tcap-client-dbg-level <<0 | 1 | 2 | 3>>
    CCmd setTcapDL("setTcapClientDebugLevel 1","set tcap-client-dbg-level" , false);
    setTcapDL.setArgTypeMap( sdArgTypeMap );
    setTcapDL.setArgValueRangeMap( rangeMap );
    cmdvalidator->setValidCommand( setTcapDL );


    //testCall <<1|2|3....>>
    CCmd testCallCmd("testCall 1","testCall",false);
    //set argument type map
    testCallCmd.setArgTypeMap( sdArgTypeMap );
    //set value range
    map<int,string> testCallValurRange;
    testCallValurRange.insert( pair<int,string> (1,"<<0|1|2|3...>>"));
    
    testCallCmd.setArgValueRangeMap( testCallValurRange );
    cmdvalidator->setValidCommand( testCallCmd );

  }catch ( CInvalidCommandFormat &ex ){
    cerr<<"Error : "<<endl<<ex<<endl;
  }/*catch ( ... ){
    cerr<<"Unexpected Error"<<endl;
  }*/
}

extern "C"
static void sigHandler (int sig, siginfo_t *siginfo, void *context)
{
  cout<<"Sending PID: "<< (long)siginfo->si_pid<<"  UID: "<<(long)siginfo->si_uid<<endl;
}

void setSigHandler(){
  struct sigaction act;
  memset (&act, '\0', sizeof(act));
  /* Use the sa_sigaction field because the handles has two additional parameters */
  act.sa_sigaction = &sigHandler;
 
  /* The SA_SIGINFO flag tells sigaction() to use the sa_sigaction field, not sa_handler. */
  act.sa_flags = SA_SIGINFO;
 
  if (sigaction(SIGINT, &act, NULL) < 0) {
    perror ("sigaction");
    exit( 4 ) ; 
  }
}

void setNonCanonicalMode(){
  struct termios oldtio;

  tcgetattr(0,&oldtio); /* save current settings */
  /* set input mode (non-canonical, no echo,...) */
  oldtio.c_cflag &= ~ICANON | ~NOFLSH;

//tcflush(fd, TCIFLUSH);
  tcsetattr(0,TCSANOW,&oldtio);
}

main(int argc, char* argv[]){
  initCommands();
   
  //pthread_cond_init( &gIOCond, NULL );
  //pthread_mutex_init( &gIOMutex, NULL );

  //setSigHandler();
  setNonCanonicalMode();
 
  CConfigManager::getConfigManager()->initialize(argc,argv);

  if( CConfigManager::getConfigManager()->isLogOptionOn() )
	{
    CLogManager::getLogManager()->initialize
			( CConfigManager::getConfigManager()->
			getConfigMap()[OUTPUT_FILE_OPTION] );
  }

  try 
	{ 
    //cout<<"Connecting "<<CConfigManager::getConfigManager()->
		//			getConfigMap()[REMOTE_SERVER_OPTION]<<"...";

    CTcpConn::getRemoteConn()->initialize
		(CConfigManager::getConfigManager()->getConfigMap()[REMOTE_SERVER_OPTION], 
		 CConfigManager::getConfigManager()->getTelnetPort());

    cout<<"done"<<endl<<endl;
  }catch( CInitException &ex )
	{
    cerr<<ex<<endl;
  }

  CInputHandlerThread thInputHdlr;
  thInputHdlr.setPrompt("INGW-PROBE > ");
  thInputHdlr.run();

  CRespHandlerThread thRspHdlr;
  thRspHdlr.setPrompt("INGW-PROBE > ");
  thRspHdlr.run();

  pthread_join(thInputHdlr.getThread(),NULL);
  pthread_join(thRspHdlr.getThread(),NULL);
}

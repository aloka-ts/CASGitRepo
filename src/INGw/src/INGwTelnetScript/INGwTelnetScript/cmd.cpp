//============================================================================
// Name        : cmd.cpp
// Author      : Sanjay Kumar
// Version     :
// Copyright   : 
// Description : Encapsulates a command, each CCmd object would represent a user command
//============================================================================

#include "cmd.h"
#include "cmdvalidator.h"
#include "logger.h"
#include "errcodes.h"
#include "util.h"
#include "tcpconn.h"
#include "logmanager.h"
#include "configmanager.h"

using namespace std;

namespace AG{ //AG for Agnity

  //CTcpConn gTcpConn("10.32.10.11",5656);

  CCmd::CCmd( string aCmd, string aRmtCmd, bool isRespExpected, bool isLocal) throw (CInvalidCommandFormat) {

    this->mIsRspExp = isRespExpected;
    this->mIsLocalCmd = isLocal;
    this->mRemoteCmd = aRmtCmd;

 //if ( mIsRspExp ) cout<<"inside default c'tor no response expected" <<endl;
 //if ( mIsLocalCmd ) cout<<"command "<<aCmd<<" is a local command " <<endl;

    if( mIsRspExp && mIsLocalCmd ) { //can't expect a response from a local command, throw exception
      throw CInvalidCommandFormat( string("Can't expect a response from a local command, invalid configuration for the command ").append(aCmd) );
    }

    
    setCommand(aCmd, aRmtCmd);
  }


  ostream& operator<< (const CCmd& aCmdObj, ostream &cout ){
    cout<<aCmdObj.getCommand();
    for( map<int, string>::const_iterator it = aCmdObj.mArgValueRange.begin();
         it != aCmdObj.mArgValueRange.end();
         ++it ){
    //for( int i = 1 ; i <= aCmdObj.getNumArgs(); ++i ){
      cout<<" " << it->second;
    }
    return cout;
  }

  string CCmd::getCorrectUsage(){
    string usageStr = mTheCmd;
    for( map<int, string>::const_iterator it = mArgValueRange.begin();
         it != mArgValueRange.end();
         ++it ){
      usageStr.append(" ").append( it->second );
    }
    return usageStr;
  }

  ostream& operator<< (ostream &cout, const CCmd& aCmdObj){
    return operator<<(aCmdObj,cout);
  }

  bool CCmd::operator==( const CCmd &aOtherCmd ){

    //self object check
    if ( &aOtherCmd == this ) return true;
  
    bool isCmdValid = true;

    // If the command names are same, lets check the 
    // num args and type of arguments

    if( aOtherCmd.getCommand().compare(this->getCommand() ) == 0 ) {
      if( aOtherCmd.getNumArgs() == this->getNumArgs() ) {
        for( int i=1; i <= mNumArgs; ++i ){
          if ( aOtherCmd.getArgTypeMap()[i] !=  this->getArgTypeMap()[i] ){
            isCmdValid = false;
            break;
          }     
        }
      }
      else{
        isCmdValid = false;
      }
    }else{
        isCmdValid = false;
    }

    return isCmdValid;
  }
 
  /*
  * Comments:
  * Its a copy c'tor
  * Make sure that you process any new attribute you add to the class
  */

  CCmd::CCmd(const CCmd& aObj){
    //cout<<"CCmd Copy c'tor called"<<endl;
    //avoid self copy 	
    if ( this != &aObj ){
      this->mTheCmd = aObj.mTheCmd;
      this->mNumArgs = aObj.mNumArgs;
      this->mIsRspExp = aObj.mIsRspExp;
      this->mIsLocalCmd = aObj.mIsLocalCmd;
      this->mCmdId = aObj.mCmdId;
      this->mRemoteCmd  = aObj.mRemoteCmd ;
      this->mActualArgMap     = aObj.mActualArgMap;  
      this->mArgValueRange    = aObj.mArgValueRange; 

      map<int, CmdArgType>::const_iterator itr;
      for( itr = aObj.mArgTypeMap.begin(); itr != aObj.mArgTypeMap.end(); ++itr){
        this->mArgTypeMap.insert( pair<int,CmdArgType>( itr->first, itr->second) );
      }
    } 
  }

  /**
  * comments:
  * This method is used to set-up the command. It parses the command passed to it
  * and set's up its mArgTypeMap and  mActualArgMap. These maps are used to validate 
  * the command. 
  * i.e. if a command like "setPduDebugLevel 1" is sent, then we set the command as
  * mTheCmd = setPduDebugLevel
  * mArgTypeMap[1] = eInteger ( Key=>1 (first argument, its not zero-indexed), Value=> eInteger )
  * mActualArgMap[1] = 1      ( Key=>1 (first argument, its not zero-indexed), Value=> 1 )
  *
  * Note: all the arguments in the map are co-related through argument position
  */
  
  void CCmd::setCommand( string aCmd , string aRmtCmd) throw (CInvalidCommandFormat) {
    if ( aCmd.empty() ) {
      mTheCmd = "";
      mNumArgs = 0;
      cerr<< "Invalid command, the alias should be non-empty" ;
      throw CInvalidCommandFormat( "Invalid command, the alias should be non-empty" );
    }else {

      vector<string> cmdTokens = AGUtil::CUtil::tokenize( aCmd, string(" ") );

      //First token would always be the command to be executed
      if ( cmdTokens.size() > 0 ) {
        mTheCmd = cmdTokens[0];
      }

      mRemoteCmd = aRmtCmd;

      mNumArgs = cmdTokens.size() - 1; //first token is always the command

      for( int idx = 1;  idx < cmdTokens.size() ; ++idx ){

        if ( AGUtil::CUtil::isInteger( cmdTokens[idx] ) ){
          mArgTypeMap.insert(std::pair<int,CmdArgType> ( idx, eInteger ) );
          mActualArgMap.insert(std::pair<int,string> ( idx, cmdTokens[idx] ) );
        }else{
          //cout<<cmdTokens[idx]<< " is a string "<<endl;
          mArgTypeMap.insert(std::pair<int, CmdArgType> ( idx, eString ) );
        }
      }
    }
  }


  void CCmd::setArgTypeMap( map<int, CmdArgType> aMap){
    mArgTypeMap = aMap;
  }

  /**
  * Comments:
  * The caller need not be aware of the type of the command
  * This method know, which action to perform based on the its type (remote/local)
  */

  void CCmd::action(){
    if ( mIsLocalCmd ) {
      performLocalAction();
    }else{
      performRemoteAction();
    }
  }

  /**
  * Comments: 
  * If the command is of local type, we perform the local operation here.
  * This method is aware of all the local commands. :-| 
  * e.g. Command see's that i'm QUIT command and calls the exit function
  */

  void CCmd::performLocalAction(){
    //If i'm quit command, i'll exit the progrma here
    if ( mTheCmd.compare(QUIT_PROBE_COMMAND) == 0  ) {
      exit ( 0 ); //I'm quitting this program. Do some housekeeping if needed 
    }
    //If i'm help command, i'll show the available commands here
    else if ( mTheCmd.compare(HELP_COMMAND) == 0 ){
      bool isLogOptionOn = CConfigManager::getConfigManager()->isLogOptionOn();
      if( isLogOptionOn ){
        CLogManager::getLogManager()->writeLog( "Command List: " );
      }
      cout<<"Command List: "<<endl;
      for(map<string,CCmd>::const_iterator itr = CCmdValidator::getCmdValidator()->getCommandMap().begin(); 
      itr != CCmdValidator::getCmdValidator()->getCommandMap().end(); ++itr) {
         cout<<"\t"<<itr->first<<endl;
         if( isLogOptionOn ){
           CLogManager::getLogManager()->writeLog( string("\t").append(itr->first) );
         }
      }
    }

  }
 
  /** 
  * Comments:
  * This method is over-ridden from CCmdHandler class. We override this method here so that
  * if the command needs to be sent to the remote server, we send it here after doing some 
  * processing on the command string.
  */

  void CCmd::performRemoteAction(){
    //cout<<"ACTUAL ARGUMENT MAP SIZE ------->"<<mActualArgMap.size()<<endl;
    string arguments="";
    for( map<int,string>::const_iterator it = mActualArgMap.begin(); it != mActualArgMap.end(); ++it )
      arguments.append( it->second );
    
    //cout<<"ACTUAL ARGUMENT MAP SIZE ------->"<<arguments<<endl;

    string sendStr = mRemoteCmd;
    //cout<<"Sending......"<<sendStr.append(" ").append(arguments)<<endl;

    CTcpConn::getRemoteConn()->sendCmd(sendStr.append(" ").append(arguments));
  }
  
  /**
  * Comments:
  * String Conversion Operator:
  * converts the current command as per the actual arguments provided
  */

  CCmd::operator string() {
    string arguments="";
    for( map<int,string>::const_iterator it = mActualArgMap.begin(); it != mActualArgMap.end(); ++it )
      arguments.append( it->second );

    string actualCommand = mTheCmd;
    return actualCommand.append(" ").append(arguments);
  }

  void CCmd::setTcpConn(string aServer, unsigned long aPort ){
    CCmdHandler::setTcpConn(aServer,aPort);
  }


} //end of agnity namespace


/*
using namespace AG;

main(){
  try{
    CCmd cmd1("bc1        x");
    CCmd cmd2("bc1 x");
    if ( cmd1 == cmd2 ) {
      cout<<"Command " <<cmd1<<" and Command "<<cmd2<<" are same"<<endl;
    }else{
      cout<<"Command " <<cmd1<<" and Command "<<cmd2<<" are NOT same"<<endl;
    }
  }catch(CInvalidCommandFormat &ex){
    cout<<ex;
  }


}
*/

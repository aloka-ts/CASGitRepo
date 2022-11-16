//============================================================================
// Name        : cmd.h
// Author      : Sanjay Kumar
// Version     :
// Copyright   : 
// Description : Encapsulates a command, each CCmd object would represent a user command
//============================================================================

#ifndef __AG_CMD__
#define __AG_CMD__


#include <map>
#include "exceptions.h"
#include "cmdhandler.h"

using namespace std;

namespace AG{ //AG for Agnity

  // Assuming only two types of arguments
  enum CmdArgType{
    eString = 0,
    eInteger
  };

  const char CmdArgDesc[][16] = {"string","integer"};
  const char QUIT_PROBE_COMMAND[]= "exit";
  const char HELP_COMMAND[]= "help";

  class CCmd : public CCmdHandler {
    public:
      CCmd() : mTheCmd(""), mRemoteCmd(""), mNumArgs(0), mIsRspExp(false), mIsLocalCmd( false) {}
      CCmd(string aCmd,string aRmtCmd = "", bool isRespExpected=true, bool isLocalCmd = false) throw (CInvalidCommandFormat);
      CCmd(const CCmd &aObj) ;

      //override
      void action();
      void performLocalAction();
      void performRemoteAction();
      void setTcpConn(string, unsigned long);

      bool isLocalCommand(){ return mIsLocalCmd; }

      void setCommand(string aAlias, string aRmtCmd) throw (CInvalidCommandFormat);

      string getRemoteCommand(){ return mRemoteCmd; }
      void setRemoteCommand(string aRemoteCmd){ mRemoteCmd = aRemoteCmd; }

      map<int, string> getActualArgumentMap(){ return mActualArgMap; }
      void setActualArgumentMap(map<int, string> aMap){ mActualArgMap.clear(); mActualArgMap=aMap; }

      map<int, string> getArgValueRangeMap(){ return mArgValueRange; }
      void setArgValueRangeMap(map<int, string> aMap){ mArgValueRange = aMap; }


      int getNumArgs() const{ return mNumArgs; }
      string getCommand() const { return mTheCmd; }
      map<int,CmdArgType> getArgTypeMap() const { return mArgTypeMap; }
      void setArgTypeMap(map<int,CmdArgType>);

      bool getRespExpectedState() { return mIsRspExp; }
      void setRespExpectedState( bool aRspState) {mIsRspExp = aRspState; }

      int getCmdId( ) {  return mCmdId; }
      void setCmdId(int aid ) {  mCmdId = aid; }



      bool operator== (const CCmd &cmd);
      operator string();
      
      string getCorrectUsage();
      
      friend ostream& operator<< (const CCmd &cmd, ostream &cout);
      friend ostream& operator<< (ostream &cout, const CCmd &cmd);


    protected: 
      int mNumArgs;

      // Assuming the sequence of arguments is fixed
      // we use the following map to match the type 
      // of the arguments based on their position in the cmd
      // e.g.  xyz a b 
      //   a: first argument 
      //   b: second 
      //   validate a against first index in the map (key starts at 1)
      //   validate b against second index in the map 
      // Don't want to hard code the size of cmds, so, taking a map
      // map[ 1 ] = eString
      // map[ 2 ] = eInteger

      map<int, CmdArgType>  mArgTypeMap;  
      map<int, string>  mActualArgMap;   //argument pos, value of argument
      map<int, string>  mArgValueRange;  //argument pos, range of the value

      //The command 
      string mTheCmd;

      //is response expected
      bool mIsRspExp;

      //is local command or need to execute on remote telnet server
      bool mIsLocalCmd;

     int mCmdId;
   
     //remote command
     string mRemoteCmd;

  };
} //end of agnity namespace

#endif

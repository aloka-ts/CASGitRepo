//============================================================================
// Name        : cmdhelper.h
// Author      : Sanjay Kumar
// Version     :
// Copyright   : 
// Description : Helper class to maintain all the valid commands
//============================================================================

#ifndef __AG_CMD__
#define __AG_CMD__


#include <map>
#include "exceptions.h"

using namespace std;

namespace AG{ //AG for Agnity

  // Assuming only two types of arguments
  enum CmdArgType{
    eString = 0,
    eInteger
  };

  const char CmdArgDesc[][16] = {"string","integer"};

  class CCmd{
    public:
      CCmd() : mTheCmd(""), mNumArgs(0), isRspExp(false) {} ;
      CCmd(string aCmd) throw (CInvalidCommandFormat);
      CCmd(string aCmd, bool isRespExpected=true) throw (CInvalidCommandFormat);
      CCmd(const CCmd &aObj) ;

      void setCommand(string aCmd) throw (CInvalidCommandFormat);

      int getNumArgs() const{ return mNumArgs; }
      string getCommand() const { return mTheCmd; }
      map<int,CmdArgType> getArgTypeMap() const { return mArgTypeMap; }

      bool getRespExpectedState() { return isRspExp; }
      void setRespExpectedState( bool aRspState) {isRspExp = aRspState; }

      bool operator== (const CCmd &cmd);

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

      //The command 
      string mTheCmd;

      //is response expected
      bool isRspExp;
      
  };
} //end of agnity namespace

#endif

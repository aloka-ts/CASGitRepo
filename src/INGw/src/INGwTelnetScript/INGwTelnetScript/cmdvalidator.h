//============================================================================lidatedCommand.
// Name        : cmdvalidator.h
// Author      : Sanjay Kumar
// Version     :
// Copyright   : 
// Description : Singleton Class That Holds All the Valid commands of the system
//               throws exception if the command sent to it is invalid
//============================================================================

#ifndef __AG_CMD_VALIDATOR__
#define __AG_CMD_VALIDATOR__

#include "exceptions.h"
#include "cmd.h"
#include "cmdhandler.h"
#include <map>
#include <string>

using namespace std;

namespace AG{ //AG for Agnity
  class CCmdValidator{
    public:
      static CCmdValidator* getCmdValidator();
      bool validate(CCmd& );
      void setValidCommand(CCmd);
      CCmd& getValidatedCommand(CCmd &aCmd) throw (CInvalidCommand);
      const map<string, CCmd>&  getCommandMap()  { return mValidCmdMap; }

    protected: 
      CCmdValidator() { }
      std::map<std::string, CCmd>  mValidCmdMap; // map to hold the valid commands
 
      static bool mIsInit;

      static CCmdValidator* mInstance;
  };
} //end of agnity client

#endif

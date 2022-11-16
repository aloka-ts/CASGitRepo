//============================================================================
// Name        : cmdvalidator.cpp
// Author      : Sanjay Kumar
// Version     :
// Copyright   : 
// Description : Singleton Class That Holds All the Valid commands of the system
//               throws exception if the command sent to it is invalid
//============================================================================

#include <iostream>
#include "cmdvalidator.h"
#include "exceptions.h"
#include "cmd.h"
#include <utility>
#include <string>

using namespace std;

namespace AG{ //AG for Agnity

  bool CCmdValidator::mIsInit = false;
  CCmdValidator* CCmdValidator::mInstance = NULL;

  CCmdValidator* CCmdValidator::getCmdValidator(){
    if ( ! mIsInit ){
      if ( mInstance == NULL ){
        mInstance = new CCmdValidator();
        mIsInit = true;
      } 
    }
    
    return mInstance;
  }

  void CCmdValidator::setValidCommand(CCmd aCmd){
 //cout<<" command to be inserted = "<<aCmd<<endl;
    string command = aCmd.getCommand();
    this->mValidCmdMap.insert( std::pair<std::string, AG::CCmd >(command,aCmd) ) ;
    //mValidCmdMap[ (const string)aCmd.getCommand() ] =  aCmd;
 //cout<<" size of the map = "<<mValidCmdMap.size()<<endl;
  }

  bool CCmdValidator::validate(CCmd &aCmd){
    bool isValid = false;
    //check if the cmd is available in the mValidCmdMap
    map<string, CCmd>::iterator it = mValidCmdMap.find(aCmd.getCommand());
    if( it == mValidCmdMap.end() )  {
      isValid = false;
      //cout<<"command "<<aCmd<<" not found in the map"<<endl;
    }
    else if( aCmd == it->second ) { //this checks the arguments and their types as well
      isValid = true;
    }
    return isValid;
  }
  
  CCmd& CCmdValidator::getValidatedCommand(CCmd &aCmd) throw (CInvalidCommand) {
     map<string, CCmd>::iterator it = mValidCmdMap.find(aCmd.getCommand());
     if( it == mValidCmdMap.end() )  {
       throw CInvalidCommand( string("Invalid Command: ").append( aCmd ) );
     }

     return it->second;
   }
} //end of agnity client


/*
using namespace AG;

main(){
  try{
    CCmdValidator::getCmdValidator()->setValidCommand(CCmd(string("exit"))); 
    CCmdValidator::getCmdValidator()->setValidCommand(CCmd(string("getLinkSets"))); 
    CCmdValidator::getCmdValidator()->setValidCommand(CCmd(string("getLinkDetail linkid"))); 
  }catch ( CInvalidCommandFormat &ex ){
    cerr<<"Error : "<<endl<<ex<<endl;
  }

  CCmd command ("exit");

  if ( CCmdValidator::getCmdValidator()->validate(command) )
    cout<<command<<" is a valid command "<<endl;
  else
    cout<<command<<" is an invalid command "<<endl;
  
}

*/

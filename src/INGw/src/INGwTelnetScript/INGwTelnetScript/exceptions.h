//============================================================================
// Name        : exceptions.h
// Author      : Sanjay Kumar
// Version     :
// Copyright   : 
// Description : General Exceptions Raised By the System
//============================================================================

#ifndef __AG_EXCEPTIONS__
#define __AG_EXCEPTIONS__

#include <map>
#include <ucontext.h>


using namespace std;
namespace AG{
  class CException{
    public:
      CException(): mError(-1), mDesc("General Error"), mEnableBT(true) {} 
      //CException(const CException &aExRef);
      //CException& operator= (CException &aExRef);
  
      friend std::ostream& operator<< ( std::ostream &cout,CException &aExRef);
      friend std::ostream& operator<< ( CException &aExRef, std::ostream &cout);
  
      int getErrorCode(){ return mError; }
      std::string getDesc(){ return mDesc; }
     
    protected:
      std::string mDesc;
      int mError;
      bool mEnableBT;
  };
  
  
  class  CInitException: public CException{
    public:
      CInitException(){}
      CInitException(std::string aMsg){
        mDesc = string(aMsg);
      }
      //CInitException(std::map<std::string,std::string> &aConfData);
      //std::map<std::string, std::string> & getConfParams() { return mConfMap; }
  
    private:
      std::map <std::string, std::string> mConfMap;
  };

  class CInvalidCommandFormat: public CException{
    public:
      CInvalidCommandFormat() {}
      CInvalidCommandFormat(std::string aMsg) {
        mDesc = string(aMsg);
      }
  };

  class CInvalidCommand: public CException{
    public:
      CInvalidCommand(){ mDesc = "Invalid Command" ; }
      CInvalidCommand(std::string aMsg) {
        mDesc = string(aMsg);
      }
  };

/*
  std::ostream& operator<<( std::ostream &aCout,CException &aExRef){
    aCout<<aExRef.getDesc()<<endl;
    printstack( 2 );
    return aCout;
  }

  std::ostream& operator<<( CException &aExRef, std::ostream &aCout){
    aCout<<aExRef.getDesc()<<endl;
    printstack( 2 );
    return aCout;
  }
*/
}
 
#endif

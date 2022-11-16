//============================================================================
// Name        : logmanager.h
// Author      : Sanjay Kumar
// Version     :
// Copyright   : 
// Description : manages the log output 
//============================================================================

#ifndef __AG_LOG_MGR__
#define __AG_LOG_MGR__

#include <iostream>
#include <fstream>
#include <map>
#include <string>

using namespace std;

namespace AG {


class CLogManager{
  public:
    static CLogManager* getLogManager();
    void initialize(string aLogFile);

    void writeLog(string msg) { 
      mLogStream<<msg<<endl; 
    }

  private:

    CLogManager() {}

    bool mIsInit;
    static bool mIsCreated;
    ofstream mLogStream;

    static CLogManager* mInstance;
};

}
#endif

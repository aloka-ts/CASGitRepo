//============================================================================
// Name        : logger.h
// Author      : Sanjay Kumar
// Version     :
// Copyright   : 
// Description : A Simple Logger for single threaded application. 
//               This logger library doesn't take care of logger rollover
//============================================================================

#ifndef __AG_UTIL_LOGGER__
#define __AG_UTIL_LOGGER__

#include <fstream>

using namespace std;

namespace AGUtil {

enum LogLevel{
  eVerbose = 0, //should always start with zero as its desc LogDesc is dependent on it
  eTrace,
  eError,
  eInfo
};

const char DEF_FILE_NAME[] = "/tmp/ingwprobe";
const int MAX_FILE_SIZE = 256;

const char LogDesc[][4] = {"VER","TRC","ERR","INF"};


class CLogger{
  public:
    static CLogger* getLogger();
    static CLogger* getLogger(string aLogFile);
    void logMsg(LogLevel, const char *aFmt, ... );
  private:
    CLogger();
    CLogger(string aLogFile);
    static CLogger *mInstance;
    static bool mIsInit;
   
    string mLogFile; //log file with absolute path
    ofstream mFs;
    
};

}
#endif

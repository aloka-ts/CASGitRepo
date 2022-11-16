//============================================================================
// Name        : logger.h
// Author      : Sanjay Kumar
// Version     :
// Copyright   : 
// Description : various utility functions 
//============================================================================
#include "logger.h"
#include "stdarg.h"
#include "time.h"
#include <sys/types.h>

using namespace std;

namespace AGUtil {

  CLogger*  CLogger::mInstance = NULL;
  bool CLogger::mIsInit = false; 

  CLogger* CLogger::getLogger(){
    if( ! mIsInit ) {
      if( mInstance == NULL ){
        pid_t pid = getpid();
        char dynFileName[MAX_FILE_SIZE];
        sprintf(dynFileName, "%s_%d.log",DEF_FILE_NAME,(int)pid);

        mInstance = new CLogger(dynFileName);
        mIsInit = true;
      }
    }
    return mInstance;
  }
  
  CLogger* CLogger::getLogger( string aLogFile ) { //absolute path of the log file
    if( ! mIsInit ) {
      if( mInstance == NULL ){
        mInstance = new CLogger(aLogFile);
        mIsInit = true;
      }
    }
    return mInstance;
  }

  CLogger::CLogger(string aFileName): mFs(aFileName.c_str()){

   mLogFile = string(aFileName);

   if( ! mFs.is_open() ){
     cerr<<"Failed to open ["<<mLogFile<<"]"<<endl; 
   }
  }

  void CLogger::logMsg(LogLevel aLogLevel, const char* aFmt,...){
    va_list arguments;
    va_start(arguments,aFmt);

    char buf[1020];// = new char[sizeof(aLogMsg)+1];
    //memset( buf, 0, sizeof(aLogMsg)+1 ); 
    vsprintf (buf, aFmt,  arguments);

    va_end(arguments);
    struct tm* now ;
    time_t currTime;
    time(&currTime);
    now = localtime(&currTime);
    char timeBuf[32];
    sprintf(timeBuf,"%d-%d-%d %d:%d:%d", now->tm_mday, now->tm_mon, now->tm_year, now->tm_hour, now->tm_min, now->tm_sec);
    mFs<<timeBuf<<" "<<LogDesc[aLogLevel]<<" "<< buf<<endl;
    //delete[] buf;
  }
}


//============================================================================
// Name        : logger.h
// Author      : Sanjay Kumar
// Version     :
// Copyright   : 
// Description : A Simple Logger for single threaded application. 
//               This logger library doesn't take care of logger rollover
//============================================================================

#ifndef __AG_INPUT_THREAD__
#define __AG_INPUT_THREAD__

#include "thread.h"
#include <string>

namespace AG {

class CInputHandlerThread: public CThread{
  public: 
    CInputHandlerThread();
    void readLine(string &aLine);
    string getPrompt(){ return mPrompt; }
    void setPrompt(string aSetPrompt){ mPrompt = aSetPrompt; }
    
  protected:
    void execute();
    string mPrompt;
};

}
#endif

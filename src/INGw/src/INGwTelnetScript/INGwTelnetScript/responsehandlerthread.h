//============================================================================
// Name        : responsehandler.h
// Author      : Sanjay Kumar
// Version     :
// Copyright   : 
// Description : thread that waits for response from the server, blocks on read
//============================================================================

#ifndef __AG_RSP_HANDLER_THREAD__
#define __AG_RSP_HANDLER_THREAD__

#include "thread.h"
#include <string>


namespace AG {

class CRespHandlerThread: public CThread{
  public: 
    CRespHandlerThread(){}
    void setPrompt(string aPrompt) { mPrompt = aPrompt ; }
    string getPrompt() { return mPrompt ; }
    
  protected:
    //Override this method to get started
    void execute();
    string mPrompt;
};

}
#endif

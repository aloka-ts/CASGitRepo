//============================================================================
// Name        : logger.h
// Author      : Sanjay Kumar
// Version     :
// Copyright   : 
// Description : A Simple Logger for single threaded application. 
//               This logger library doesn't take care of logger rollover
//============================================================================

#ifndef __AG_THREAD__
#define __AG_THREAD__

#include <thread.h>

using namespace std;

namespace AG {

class CThread{
  public:
    CThread():mAttr(NULL) {}
    virtual void run();
    pthread_t getThread(){ return mThread; }
  protected:
    pthread_attr_t *mAttr;
    pthread_t mThread;

    static void* mRunnerFunc(void *attr);

    /* Derived classes should over-ride this method 
    *  for their thread execution
    */
    virtual void execute() = 0;
};

}
#endif

//============================================================================
// Name        : thread.h
// Author      : Sanjay Kumar
// Version     :
// Copyright   : 
// Description : A Simple Logger for single threaded application. 
//               This logger library doesn't take care of logger rollover
//============================================================================
#include <iostream>
#include <pthread.h>
#include "thread.h"

using namespace std;

namespace AG {

  void CThread::run(){
    pthread_create ( &mThread, mAttr, mRunnerFunc,(void *)this); 
  }

  extern "C" 
  void *CThread::mRunnerFunc(void *aObjRef){
    ((CThread *)aObjRef)->execute();
    return NULL;
  }

/*
  void CThread::execute(){
    while ( true )
      cout<<"Hello"<<endl;
  }
*/
}

/*
using namespace AGUtil;

main(){
  CThread t1;
  t1.run();
  pthread_join(t1.getThread(),NULL);
}
*/

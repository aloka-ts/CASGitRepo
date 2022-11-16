//============================================================================
// Name        : logmanager.h
// Author      : Sanjay Kumar
// Version     :
// Copyright   : 
// Description : manages the log output 
//============================================================================
#include "logmanager.h"

using namespace std;

namespace AG {
bool CLogManager::mIsCreated = false;
CLogManager * CLogManager::mInstance = NULL;

CLogManager* CLogManager::getLogManager(){
  if( ! mIsCreated ){
    if( !mInstance ){
      mInstance = new CLogManager();
      mIsCreated = true;
    }
  }
  return mInstance;
}

void CLogManager::initialize(string mFileName){

  if( mIsInit ) return;

  mLogStream.open( mFileName.c_str() );

  if( ! mLogStream.is_open() ){
    cout<<"Failed to open "<<mFileName<<endl;
    exit( 6 );
  }

  mIsInit = true;
}
}

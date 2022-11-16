#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraMsrMgr");
/************************************************************************
     Name:     Measurement Value manager - implementation

     Type:     C implementation file

     Desc:     This file provides access to Value Manager 

     File:     MsrValueMgr.C

     Sid:      MsrValueMgr.C 0  -  11/14/03

     Prg:      gs

************************************************************************/


#include <INGwInfraMsrMgr/MsrIncludes.h>
#include <INGwInfraMsrMgr/MsrValueMgr.h>
#include <INGwInfraMsrMgr/MsrPool.C>

static const int DEFAULT_MSR_POOL_SIZE = 10000;

using namespace std;

MsrValueMgr *MsrValueMgr::mpSelf = 0;

//C'tor
MsrValueMgr::MsrValueMgr ():
mInsBackupPool (0),
mAccBackupPool (0)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrValueMgr::MsrValueMgr");

  pthread_rwlock_init (&mInsPoolListLock, 0);
  pthread_rwlock_init (&mAccPoolListLock, 0);

  createPool (DEFAULT_MSR_POOL_SIZE, INSTANTANEOUS);
  createPool (DEFAULT_MSR_POOL_SIZE, ACCUMULATED);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrValueMgr::MsrValueMgr");
}

//D'tor
MsrValueMgr::~MsrValueMgr ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrValueMgr::~MsrValueMgr");

  pthread_rwlock_wrlock (&mInsPoolListLock);

  delete mInsPoolList;
  pthread_rwlock_unlock (&mInsPoolListLock);

  pthread_rwlock_destroy (&mInsPoolListLock);

  pthread_rwlock_wrlock (&mAccPoolListLock);

  delete mAccPoolList;
  pthread_rwlock_unlock (&mAccPoolListLock);

  pthread_rwlock_destroy (&mAccPoolListLock);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrValueMgr::~MsrValueMgr");
}

MsrValueMgr *
MsrValueMgr::getInstance ()
{
  if (mpSelf == 0)
    mpSelf = new MsrValueMgr;

  return mpSelf;
}

//create a new pool
int 
MsrValueMgr::createPool (int aiPoolSize, int aiPoolType)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrValueMgr::createPool");

  if (aiPoolType == INSTANTANEOUS)
  {
    pthread_rwlock_wrlock (&mInsPoolListLock);
    MsrPool<MsrInstantValue> *lpPool = 
                          new MsrPool<MsrInstantValue> (aiPoolType);

    if (lpPool == 0)
    {
      pthread_rwlock_unlock (&mInsPoolListLock);
      logger.logMsg (ERROR_FLAG, 0,
        "Unable to create Instantaneous Pool");
      return MSR_FAIL;
    }

    if (lpPool->initialize (aiPoolType, aiPoolSize) != MSR_SUCCESS)
    {
      pthread_rwlock_unlock (&mInsPoolListLock);
      delete lpPool;
      logger.logMsg (ERROR_FLAG, 0,
        "Unable to initialize Instantaneous Pool");
      return MSR_FAIL;
    }
 
    mInsPoolList = lpPool;

    pthread_rwlock_unlock (&mInsPoolListLock);

  }
  else if (aiPoolType == ACCUMULATED)
  {
    pthread_rwlock_wrlock (&mAccPoolListLock);
    MsrPool<MsrAccValue> *lpPool =
                          new MsrPool<MsrAccValue> (1);

    if (lpPool == 0)
    {
      pthread_rwlock_unlock (&mAccPoolListLock);

      logger.logMsg (ERROR_FLAG, 0,
        "Unable to create accumulated pool");

      return MSR_FAIL;
    }

    if (lpPool->initialize (aiPoolType, aiPoolSize) != MSR_SUCCESS)
    {
      pthread_rwlock_unlock (&mAccPoolListLock);
      delete lpPool;

      logger.logMsg (ERROR_FLAG, 0,
        "Unable to initialize Accumulated Pool");
      return MSR_FAIL;
    }

    mAccPoolList = lpPool;

    pthread_rwlock_unlock (&mAccPoolListLock);

  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrValueMgr::createPool");

  return MSR_SUCCESS;
}

//backup an existing pool
int 
MsrValueMgr::backup (int aiPoolType, MsrPool<MsrInstantValue> *(&arPoolList))
{
  //logger.logMsg (TRACE_FLAG, 0,
  //  "Entering MsrValueMgr::backup");

  int liRetVal = MSR_SUCCESS;

  pthread_rwlock_wrlock (&mInsPoolListLock);

  if (mInsBackupPool == 0)
  {
    mInsBackupPool = new MsrPool<MsrInstantValue> (aiPoolType);
    if (mInsBackupPool &&
      mInsBackupPool->initialize (INSTANTANEOUS, DEFAULT_MSR_POOL_SIZE)
        == MSR_SUCCESS)
    {
      liRetVal = MSR_SUCCESS;
    }
    else 
    {
      liRetVal = MSR_FAIL;
    }
  }

  if (mInsPoolList && liRetVal != MSR_FAIL)
    *mInsBackupPool = *mInsPoolList;
  else 
  {
    delete mInsBackupPool;
    mInsBackupPool = 0;

    logger.logMsg (ERROR_FLAG, 0,
      "MsrValueMgr::backup : Unable to backup Instant Value Pool");
  }

  arPoolList = mInsBackupPool;

  pthread_rwlock_unlock (&mInsPoolListLock);

  //logger.logMsg (TRACE_FLAG, 0,
  //  "Leaving MsrValueMgr::backup");

  return liRetVal;
}

int 
MsrValueMgr::backup (int aiPoolType, MsrPool<MsrAccValue> *(&arPoolList))
{
  //logger.logMsg (TRACE_FLAG, 0,
  //  "Entering MsrValueMgr::backup");

  int liRetVal = MSR_SUCCESS;

  pthread_rwlock_wrlock (&mAccPoolListLock);

  if (mAccBackupPool == 0)
  {
    mAccBackupPool = new MsrPool<MsrAccValue> (aiPoolType);
    if (mAccBackupPool &&
      mAccBackupPool->initialize (ACCUMULATED, DEFAULT_MSR_POOL_SIZE)
        == MSR_SUCCESS)
    {
      liRetVal = MSR_SUCCESS;
    }
    else
    {
      liRetVal = MSR_FAIL;
    }
  }

  if (mAccPoolList && liRetVal  != MSR_FAIL)
    *mAccBackupPool = *mAccPoolList;
  else
  {
    delete mAccBackupPool;
    mAccBackupPool = arPoolList;
  }

  arPoolList = mAccBackupPool;
  
  pthread_rwlock_unlock (&mAccPoolListLock);

  //logger.logMsg (TRACE_FLAG, 0,
  //  "Leaving MsrValueMgr::backup");

  return liRetVal;
} 


//creating of a new index
int 
MsrValueMgr::createValue (int aiPoolType)
{
  //logger.logMsg (TRACE_FLAG, 0,
  //  "Entering MsrValueMgr::createValue");

  int liIndex = -1;

  if (aiPoolType == ACCUMULATED)
  {
    pthread_rwlock_wrlock (&mAccPoolListLock);

    if (mAccPoolList == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "The Accumulated pools have not been initialized");
      pthread_rwlock_unlock (&mAccPoolListLock);
      return liIndex;
    }

    liIndex = mAccPoolList->createValue();

    pthread_rwlock_unlock (&mAccPoolListLock);
  }
  else if (aiPoolType == INSTANTANEOUS)
  {
    pthread_rwlock_wrlock (&mInsPoolListLock);

    if (mInsPoolList == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "The Instantaneous pools have not been initialized");
      pthread_rwlock_unlock (&mInsPoolListLock);
      return liIndex;
    }

    liIndex = mInsPoolList->createValue();

    pthread_rwlock_unlock (&mInsPoolListLock);
  }

  //logger.logMsg (TRACE_FLAG, 0,
  //  "Leaving MsrValueMgr::createValue<%d>", liIndex);

  return liIndex;
}

//setting the value node
int 
MsrValueMgr::increment (int aiPoolType, int aiValueIndex, 
                        unsigned long aiFactor)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrValueMgr::increment");

  if (aiPoolType == ACCUMULATED)
  {
    pthread_rwlock_rdlock (&mAccPoolListLock);

    if (mAccPoolList->increment (aiValueIndex, aiFactor) 
             != MSR_SUCCESS)
    {
      pthread_rwlock_unlock (&mAccPoolListLock);
      return MSR_FAIL;
    }

    pthread_rwlock_unlock (&mAccPoolListLock);
  }
  else if (aiPoolType == INSTANTANEOUS)
  {
    pthread_rwlock_rdlock (&mInsPoolListLock);

    if (mInsPoolList->increment (aiValueIndex, aiFactor) 
             != MSR_SUCCESS)
    {
      pthread_rwlock_unlock (&mInsPoolListLock);
      return MSR_FAIL;
    }

    pthread_rwlock_unlock (&mInsPoolListLock);
  }
  else
    return MSR_FAIL;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrValueMgr::increment");

  return MSR_SUCCESS;
}

int 
MsrValueMgr::decrement (int aiPoolType, int aiValueIndex, 
                        unsigned long aiFactor)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrValueMgr::decrement");

  if (aiPoolType == ACCUMULATED)
  {
    pthread_rwlock_rdlock (&mAccPoolListLock);

    if (mAccPoolList->decrement (aiValueIndex, aiFactor) 
             != MSR_SUCCESS)
    {
      pthread_rwlock_unlock (&mAccPoolListLock);
      return MSR_FAIL;
    }

    pthread_rwlock_unlock (&mAccPoolListLock);
  }
  else if (aiPoolType == INSTANTANEOUS)
  {
    pthread_rwlock_rdlock (&mInsPoolListLock);

    if (mInsPoolList->decrement (aiValueIndex, aiFactor)
             != MSR_SUCCESS)
    {
      pthread_rwlock_unlock (&mInsPoolListLock);
      return MSR_FAIL;
    }

    pthread_rwlock_unlock (&mInsPoolListLock);
  }
  else
    return MSR_FAIL;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrValueMgr::decrement");

  return MSR_SUCCESS;
}

int 
MsrValueMgr::setValue (int aiPoolType, int aiValueIndex, 
                       unsigned long aulValue)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrValueMgr::setValue");

  if (aiPoolType == ACCUMULATED)
  {
    pthread_rwlock_rdlock (&mAccPoolListLock);

    if (mAccPoolList->setValue (aiValueIndex, aulValue) 
             != MSR_SUCCESS)
    {
      pthread_rwlock_unlock (&mAccPoolListLock);
      return MSR_FAIL;
    }

    pthread_rwlock_unlock (&mAccPoolListLock);
  }
  else if (aiPoolType == INSTANTANEOUS)
  {
    pthread_rwlock_rdlock (&mInsPoolListLock);

    if (mInsPoolList->setValue (aiValueIndex, aulValue)
             != MSR_SUCCESS)
    {
      pthread_rwlock_unlock (&mInsPoolListLock);
      return MSR_FAIL;
    }

    pthread_rwlock_unlock (&mInsPoolListLock);
  }
  else
    return MSR_FAIL;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrValueMgr::setValue");

  return MSR_SUCCESS;
}

int 
MsrValueMgr::setStatus (int aiPoolType, int aiValueIndex, 
                        int aiStatus)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrValueMgr::setStatus");

  if (aiPoolType == ACCUMULATED)
  {
    pthread_rwlock_rdlock (&mAccPoolListLock);

    if (mAccPoolList->setStatus (aiValueIndex, aiStatus) 
             != MSR_SUCCESS)
    {
      pthread_rwlock_unlock (&mAccPoolListLock);
      return MSR_FAIL;
    }

    pthread_rwlock_unlock (&mAccPoolListLock);
  }
  else if (aiPoolType == INSTANTANEOUS)
  {
    pthread_rwlock_rdlock (&mInsPoolListLock);

    if (mInsPoolList->setStatus (aiValueIndex, aiStatus)
             != MSR_SUCCESS)
    {
      pthread_rwlock_unlock (&mInsPoolListLock);
      return MSR_FAIL;
    }

    pthread_rwlock_unlock (&mInsPoolListLock);
  }
  else
    return MSR_FAIL;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrValueMgr::setStatus");

  return MSR_SUCCESS;
}


//retrieving the info from Value node
int
MsrValueMgr::getValue (int aiPoolType, 
                       int aiValueIndex, 
                       unsigned long &aulValue)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrValueMgr::getValue");

  int liRetVal = MSR_FAIL;

  if (aiPoolType == ACCUMULATED)
  {
    pthread_rwlock_rdlock (&mAccPoolListLock);

    liRetVal = mAccPoolList->getValue (aiValueIndex, aulValue);

    pthread_rwlock_unlock (&mAccPoolListLock);
  }
  else if (aiPoolType == INSTANTANEOUS)
  {
    pthread_rwlock_rdlock (&mInsPoolListLock);

    liRetVal = mInsPoolList-> getValue (aiValueIndex, aulValue);

    pthread_rwlock_unlock (&mInsPoolListLock);
  }
  else
    return MSR_FAIL;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrValueMgr::getValue");

  return liRetVal;
}

int
MsrValueMgr::getBackupValue (int aiPoolType, 
                             int aiValueIndex, 
                             unsigned long &aulValue)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrValueMgr::getBackupValue");

  int liRetVal = MSR_FAIL;
    
  if (aiPoolType == ACCUMULATED)
  {
    pthread_rwlock_rdlock (&mAccPoolListLock);
  
    liRetVal = mAccPoolList-> getBackupValue (aiValueIndex, aulValue);

    pthread_rwlock_unlock (&mAccPoolListLock);
  }                       
  else if (aiPoolType == INSTANTANEOUS) 
  {
    pthread_rwlock_rdlock (&mInsPoolListLock);

    liRetVal = mInsPoolList-> getBackupValue (aiValueIndex, aulValue);

    pthread_rwlock_unlock (&mInsPoolListLock);
  }
  else
    return MSR_FAIL;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrValueMgr::getBackupValue");

  return liRetVal;
}

int 
MsrValueMgr::getMaxValue (int aiPoolType,  
                          int aiValueIndex,
                          unsigned long &aulValue)
{
  int liRetVal = MSR_FAIL;
    
  if (aiPoolType == ACCUMULATED)
  {
    pthread_rwlock_rdlock (&mAccPoolListLock);
  
    liRetVal = mAccPoolList-> getMaxValue (aiValueIndex, aulValue);

    pthread_rwlock_unlock (&mAccPoolListLock);
  }                       
  else if (aiPoolType == INSTANTANEOUS) 
  {
    pthread_rwlock_rdlock (&mInsPoolListLock);

    liRetVal = mInsPoolList-> getMaxValue (aiValueIndex, aulValue);


    pthread_rwlock_unlock (&mInsPoolListLock);
  }
  else
    return MSR_FAIL;

  return liRetVal;
}

int
MsrValueMgr::getBackupMaxValue (int aiPoolType,
                          int aiValueIndex,
                          unsigned long &aulValue)
{
  int liRetVal = MSR_FAIL;
    
  if (aiPoolType == ACCUMULATED)
  {
    pthread_rwlock_rdlock (&mAccPoolListLock);
  
    liRetVal = mAccPoolList-> getBackupMaxValue (aiValueIndex, aulValue);

    pthread_rwlock_unlock (&mAccPoolListLock);
  }                       
  else if (aiPoolType == INSTANTANEOUS) 
  {
    pthread_rwlock_rdlock (&mInsPoolListLock);

    liRetVal = mInsPoolList-> getBackupMaxValue (aiValueIndex, aulValue);


    pthread_rwlock_unlock (&mInsPoolListLock);
  }
  else
    return MSR_FAIL;

  return liRetVal;
}

int 
MsrValueMgr::getMinValue (int aiPoolType,
                          int aiValueIndex,
                          unsigned long &aulValue)
{
  int liRetVal = MSR_FAIL;
    
  if (aiPoolType == ACCUMULATED)
  {
    pthread_rwlock_rdlock (&mAccPoolListLock);
  
    liRetVal = mAccPoolList-> getMinValue (aiValueIndex, aulValue);

    pthread_rwlock_unlock (&mAccPoolListLock);
  }                       
  else if (aiPoolType == INSTANTANEOUS) 
  {
    pthread_rwlock_rdlock (&mInsPoolListLock);

    liRetVal = mInsPoolList-> getMinValue (aiValueIndex, aulValue);


    pthread_rwlock_unlock (&mInsPoolListLock);
  }
  else
    return MSR_FAIL;

  return liRetVal;
}

int 
MsrValueMgr::getBackupMinValue (int aiPoolType,
                          int aiValueIndex,
                          unsigned long &aulValue)
{
  int liRetVal = MSR_FAIL;
    
  if (aiPoolType == ACCUMULATED)
  {
    pthread_rwlock_rdlock (&mAccPoolListLock);
  
    liRetVal = mAccPoolList-> getBackupMinValue (aiValueIndex, aulValue);

    pthread_rwlock_unlock (&mAccPoolListLock);
  }                       
  else if (aiPoolType == INSTANTANEOUS) 
  {
    pthread_rwlock_rdlock (&mInsPoolListLock);

    liRetVal = mInsPoolList-> getBackupMinValue (aiValueIndex, aulValue);


    pthread_rwlock_unlock (&mInsPoolListLock);
  }
  else
    return MSR_FAIL;

  return liRetVal;
}

int
MsrValueMgr::getNumOfInvokes (int aiPoolType,
                          int aiValueIndex,
                          unsigned long &aulValue)
{
  int liRetVal = MSR_FAIL;
    
  if (aiPoolType == ACCUMULATED)
  {
    pthread_rwlock_rdlock (&mAccPoolListLock);
  
    liRetVal = mAccPoolList-> getNumOfInvokes (aiValueIndex, aulValue);

    pthread_rwlock_unlock (&mAccPoolListLock);
  }                       
  else if (aiPoolType == INSTANTANEOUS) 
  {
    pthread_rwlock_rdlock (&mInsPoolListLock);

    liRetVal = mInsPoolList-> getNumOfInvokes (aiValueIndex, aulValue);


    pthread_rwlock_unlock (&mInsPoolListLock);
  }
  else
    return MSR_FAIL;

  return liRetVal;
}

int
MsrValueMgr::getBackupNumOfInvokes (int aiPoolType,
                          int aiValueIndex,
                          unsigned long &aulValue)
{
  int liRetVal = MSR_FAIL;
    
  if (aiPoolType == ACCUMULATED)
  {
    pthread_rwlock_rdlock (&mAccPoolListLock);
  

    liRetVal = mAccPoolList-> getBackupNumOfInvokes (aiValueIndex, aulValue);

    pthread_rwlock_unlock (&mAccPoolListLock);
  }                       
  else if (aiPoolType == INSTANTANEOUS) 
  {
    pthread_rwlock_rdlock (&mInsPoolListLock);

    liRetVal = mInsPoolList-> getBackupNumOfInvokes (aiValueIndex, aulValue);


    pthread_rwlock_unlock (&mInsPoolListLock);
  }
  else
    return MSR_FAIL;

  return liRetVal;
}

int 
MsrValueMgr::getStatus (int aiPoolType,
                          int aiValueIndex,
                          int &aiStatus)
{   
  int liRetVal = MSR_FAIL;
    
  if (aiPoolType == ACCUMULATED)
  {
    pthread_rwlock_rdlock (&mAccPoolListLock);
  
    liRetVal = mAccPoolList-> getStatus (aiValueIndex, aiStatus);

    pthread_rwlock_unlock (&mAccPoolListLock);
  }                       
  else if (aiPoolType == INSTANTANEOUS) 
  {
    pthread_rwlock_rdlock (&mInsPoolListLock);

    liRetVal = mInsPoolList-> getStatus (aiValueIndex, aiStatus);


    pthread_rwlock_unlock (&mInsPoolListLock);
  }
  else
    return MSR_FAIL;

  return liRetVal;
}

int 
MsrValueMgr::getBackupStatus (int aiPoolType,
                          int aiValueIndex,
                          int &aiStatus)
{  
  int liRetVal = MSR_FAIL;
    
  if (aiPoolType == ACCUMULATED)
  {
    pthread_rwlock_rdlock (&mAccPoolListLock);
  
    liRetVal = mAccPoolList-> getBackupStatus (aiValueIndex, aiStatus);

    pthread_rwlock_unlock (&mAccPoolListLock);
  }                       
  else if (aiPoolType == INSTANTANEOUS) 
  {
    pthread_rwlock_rdlock (&mInsPoolListLock);

    liRetVal = mInsPoolList-> getBackupStatus (aiValueIndex, aiStatus);


    pthread_rwlock_unlock (&mInsPoolListLock);
  }
  else
    return MSR_FAIL;

  return liRetVal;
}


/************************************************************************
     Name:     Measurement Pool - includes

     Type:     C include file

     Desc:     This file provides access to Measurement Pool Node

     File:     MsrPool.h

     Sid:      MsrPool.h 0  -  11/14/03

     Prg:      gs

************************************************************************/

#ifndef _MSR_POOL_H_
#define _MSR_POOL_H_

#include <INGwInfraMsrMgr/MsrValue.h>
#include <pthread.h>

const int MAX_POOL_LOCK = 997;

class MsrValueMgr;

template <class _tValue>
class MsrPool
{
  public:
    //c'tor
    MsrPool (int aiPoolId);

    //d'tor
    ~MsrPool ();

    int initialize (int aiValueType, int aiMaxPoolSize);

    //keep reconfiguration in case we might need in future
    int reconfigure ();

    //creating of a new index
    int createValue ();
    int duplicateValue (int aiIndex, _tValue *(&apValue));

    //setting the value node
    int increment (int aiIndex, unsigned long aiFactor);
    int decrement (int aiIndex, unsigned long aiFactor);
    int setValue (int aiIndex, unsigned long aulValue);
    int setStatus (int aiIndex, int aiStatus);

    //retrieving the info from Value node
    int getValue (int aiIndex, unsigned long &aulValue);
    int getMaxValue (int aiIndex, unsigned long &aulValue);
    int getMinValue (int aiIndex, unsigned long &aulValue);
    int getNumOfInvokes (int aiIndex, unsigned long &aulValue);
    int getStatus (int aiIndex, int &aiStatus);

    //retrieving the info from Value node by worker thread
    int getBackupValue (int aiIndex, unsigned long &aulValue);
    int getBackupMaxValue (int aiIndex, unsigned long &aulValue);
    int getBackupMinValue (int aiIndex, unsigned long &aulValue);
    int getBackupNumOfInvokes (int aiIndex, unsigned long &aulValue);
    int getBackupStatus (int aiIndex, int &aiStatus);

    MsrPool<_tValue>& operator=(MsrPool<_tValue> &apSrc);

  protected:
    int miPoolId;

    //can be accumulated or instantaneous
    int miTypeOfValue;

    int miMaxPoolSize;
    int miCurrPoolSize;

    //actual pool can be a vector or an array
    _tValue *mpValuePool;

    //one lock will control every Nth (MAX_POOL_LOCK) value in the pool
    pthread_mutex_t marPoolLock [MAX_POOL_LOCK];
};



//c'tor
template <class _tValue>
MsrPool <_tValue>::MsrPool (int aiPoolId):
miPoolId (aiPoolId),
mpValuePool (0),
miCurrPoolSize (0)
{
  for (int liCount = 0; liCount < MAX_POOL_LOCK; liCount++)
    pthread_mutex_init (&marPoolLock [liCount], 0);
}

//d'tor
template <class _tValue>
MsrPool <_tValue>::~MsrPool ()
{
  miCurrPoolSize = 0;
  delete [] mpValuePool;
  mpValuePool = 0;

  for (int liCount = 0; liCount < MAX_POOL_LOCK; liCount++)  
    pthread_mutex_destroy (&marPoolLock [liCount]);

}

template <class _tValue>
int
MsrPool <_tValue>:: initialize (int aiValueType,
                                int aiMaxPoolSize)
{
  miTypeOfValue = aiValueType;
  miMaxPoolSize = aiMaxPoolSize;

  if (miMaxPoolSize > 0 && mpValuePool == 0)
    mpValuePool = new _tValue [ miMaxPoolSize];

  if (mpValuePool == 0)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "MsrPool:: initialize : allocation failed for type <%d> and size<%d>",
     aiValueType, aiMaxPoolSize);
    return MSR_FAIL;
  }

  return MSR_SUCCESS;
}

//keep reconfiguration in case we might need in future
template <class _tValue>
int
MsrPool <_tValue>:: reconfigure ()
{
  return MSR_FAIL;
}

//creating of a new index
template <class _tValue>
int
MsrPool <_tValue>::createValue ()
{
  if (miCurrPoolSize > miMaxPoolSize)
    return -1;

  return miCurrPoolSize++;
}

template <class _tValue>
int
MsrPool <_tValue>::duplicateValue (int aiIndex, _tValue *(&apValue))
{
  if (aiIndex < 0 || aiIndex >= miCurrPoolSize)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "MsrPool::duplicateValue : Type <%d> index <%d>, CurrPoolSize <%d>",
      miTypeOfValue, aiIndex, miCurrPoolSize);
    return MSR_FAIL;
  }

  return mpValuePool [aiIndex].duplicate (apValue);
}


//setting the value node
template <class _tValue>
int
MsrPool <_tValue>:: increment (int aiIndex, unsigned long aiFactor)
{
  if (aiIndex < 0 || aiIndex >= miCurrPoolSize)
  { 
    logger.logMsg (ERROR_FLAG, 0,
      "MsrPool::increment : index <%d>, CurrPoolSize <%d>",
      aiIndex, miCurrPoolSize);
    return MSR_FAIL;
  }

  pthread_mutex_lock (& marPoolLock [aiIndex % MAX_POOL_LOCK]);
  int liRetVal = mpValuePool [aiIndex].increment (aiFactor);
  pthread_mutex_unlock (& marPoolLock [aiIndex % MAX_POOL_LOCK]);

  return liRetVal;
}

template <class _tValue>
int
MsrPool <_tValue>::decrement (int aiIndex, unsigned long aiFactor)
{
  if (aiIndex < 0 || aiIndex >= miCurrPoolSize)
  { 
    logger.logMsg (ERROR_FLAG, 0,
      "MsrPool::decrement : index <%d>, CurrPoolSize <%d>",
      aiIndex, miCurrPoolSize);
    return MSR_FAIL;
  }

  pthread_mutex_lock (& marPoolLock [aiIndex % MAX_POOL_LOCK]);
  int liRetVal = mpValuePool [aiIndex].decrement (aiFactor);
  pthread_mutex_unlock (& marPoolLock [aiIndex % MAX_POOL_LOCK]);

  return liRetVal;
}

template <class _tValue>
int
MsrPool <_tValue>::setValue (int aiIndex, unsigned long aulValue)
{
  if (aiIndex < 0 || aiIndex >= miCurrPoolSize)
  { 
    logger.logMsg (ERROR_FLAG, 0,
      "MsrPool::setValue : index <%d>, CurrPoolSize <%d>",
      aiIndex, miCurrPoolSize);
    return MSR_FAIL;
  }

  pthread_mutex_lock (& marPoolLock [aiIndex % MAX_POOL_LOCK]);
  int liRetVal = mpValuePool [aiIndex].setValue (aulValue);
  pthread_mutex_unlock (& marPoolLock [aiIndex % MAX_POOL_LOCK]);

  return liRetVal;
}

template <class _tValue>
int
MsrPool <_tValue>::setStatus (int aiIndex, int aiStatus)
{
  if (aiIndex < 0 || aiIndex >= miCurrPoolSize)
  { 
    logger.logMsg (ERROR_FLAG, 0,
      "MsrPool::setStatus : index <%d>, CurrPoolSize <%d>",
      aiIndex, miCurrPoolSize);
    return MSR_FAIL;
  }

  pthread_mutex_lock (& marPoolLock [aiIndex % MAX_POOL_LOCK]);
  int liRetVal = mpValuePool [aiIndex].setStatus (aiStatus);
  pthread_mutex_unlock (& marPoolLock [aiIndex % MAX_POOL_LOCK]);

  return liRetVal;
}

//retrieving the info from Value node
template <class _tValue>
int
MsrPool <_tValue>::getValue (int aiIndex, unsigned long &aulValue)
{
  if (aiIndex < 0 || aiIndex >= miCurrPoolSize)
  { 
    logger.logMsg (ERROR_FLAG, 0,
      "MsrPool::getValue : index <%d>, CurrPoolSize <%d>",
      aiIndex, miCurrPoolSize);
    return MSR_FAIL;
  }

  pthread_mutex_lock (& marPoolLock [aiIndex % MAX_POOL_LOCK]);
  aulValue = mpValuePool [aiIndex].getValue ();
  pthread_mutex_unlock (& marPoolLock [aiIndex % MAX_POOL_LOCK]);

  return MSR_SUCCESS;
}

template <class _tValue>
int
MsrPool <_tValue>::getMaxValue (int aiIndex, unsigned long &aulValue)
{
  if (aiIndex < 0 || aiIndex >= miCurrPoolSize)
  { 
    logger.logMsg (ERROR_FLAG, 0,
      "MsrPool::getMaxValue : index <%d>, CurrPoolSize <%d>",
      aiIndex, miCurrPoolSize);
    return MSR_FAIL;
  }
 
  pthread_mutex_lock (& marPoolLock [aiIndex % MAX_POOL_LOCK]);
  aulValue = mpValuePool [aiIndex].getMaxValue ();
  pthread_mutex_unlock (& marPoolLock [aiIndex % MAX_POOL_LOCK]);

  return MSR_SUCCESS;
}

template <class _tValue>
int
MsrPool <_tValue>::getMinValue (int aiIndex, unsigned long &aulValue)
{
  if (aiIndex < 0 || aiIndex >= miCurrPoolSize)
  { 
    logger.logMsg (ERROR_FLAG, 0,
      "MsrPool::getMinValue : index <%d>, CurrPoolSize <%d>",
      aiIndex, miCurrPoolSize);
    return MSR_FAIL;
  }
 
  pthread_mutex_lock (& marPoolLock [aiIndex % MAX_POOL_LOCK]);
  aulValue = mpValuePool [aiIndex].getMinValue ();
  pthread_mutex_unlock (& marPoolLock [aiIndex % MAX_POOL_LOCK]);

  return MSR_SUCCESS;
}

template <class _tValue>
int
MsrPool <_tValue>::getNumOfInvokes (int aiIndex, unsigned long &aulValue)
{
  if (aiIndex < 0 || aiIndex >= miCurrPoolSize)
  { 
    logger.logMsg (ERROR_FLAG, 0,
      "MsrPool::getNumOfInvokes : index <%d>, CurrPoolSize <%d>",
      aiIndex, miCurrPoolSize);
    return MSR_FAIL;
  }
 
  pthread_mutex_lock (& marPoolLock [aiIndex % MAX_POOL_LOCK]);
  aulValue = mpValuePool [aiIndex].getNumInvokes ();
  pthread_mutex_unlock (& marPoolLock [aiIndex % MAX_POOL_LOCK]);

  return MSR_SUCCESS;
}

template <class _tValue>
int
MsrPool <_tValue>::getStatus (int aiIndex, int &aiStatus)
{
  if (aiIndex < 0 || aiIndex >= miCurrPoolSize)
  { 
    logger.logMsg (ERROR_FLAG, 0,
      "MsrPool::getStatus : index <%d>, CurrPoolSize <%d>",
      aiIndex, miCurrPoolSize);
    return MSR_FAIL;
  }

  pthread_mutex_lock (& marPoolLock [aiIndex % MAX_POOL_LOCK]);
  aiStatus = mpValuePool [aiIndex].getStatus ();
  pthread_mutex_unlock (& marPoolLock [aiIndex % MAX_POOL_LOCK]);

  return MSR_SUCCESS;
}

//retrieving the info from Value node by worker thread
template <class _tValue>
int
MsrPool <_tValue>::getBackupValue (int aiIndex, unsigned long &aulValue)
{
  if (aiIndex < 0 || aiIndex >= miCurrPoolSize)
  { 
    logger.logMsg (ERROR_FLAG, 0,
      "MsrPool::getBackupValue : index <%d>, CurrPoolSize <%d>",
      aiIndex, miCurrPoolSize);
    return MSR_FAIL;
  }

  aulValue =  mpValuePool [aiIndex].getValue ();

  return MSR_SUCCESS;
}

template <class _tValue>
int
MsrPool <_tValue>::getBackupMaxValue (int aiIndex, unsigned long &aulValue)
{
  if (aiIndex < 0 || aiIndex >= miCurrPoolSize)
  { 
    logger.logMsg (ERROR_FLAG, 0,
      "MsrPool::getBackupMaxValue : index <%d>, CurrPoolSize <%d>",
      aiIndex, miCurrPoolSize);
    return MSR_FAIL;
  }

  aulValue = mpValuePool [aiIndex].getMaxValue ();
  return MSR_SUCCESS;
}

template <class _tValue>
int
MsrPool <_tValue>::getBackupMinValue (int aiIndex, unsigned long &aulValue)
{
  if (aiIndex < 0 || aiIndex >= miCurrPoolSize)
  { 
    logger.logMsg (ERROR_FLAG, 0,
      "MsrPool::getBackupMinValue : index <%d>, CurrPoolSize <%d>",
      aiIndex, miCurrPoolSize);
    return MSR_FAIL;
  }

  aulValue = mpValuePool [aiIndex].getMinValue ();

  return MSR_SUCCESS;
}

template <class _tValue>
int
MsrPool <_tValue>::getBackupNumOfInvokes (int aiIndex, unsigned long &aulValue)
{
  if (aiIndex < 0 || aiIndex >= miCurrPoolSize)
  { 
    logger.logMsg (ERROR_FLAG, 0,
      "MsrPool::getBackupNumOfInvokes : index <%d>, CurrPoolSize <%d>",
      aiIndex, miCurrPoolSize);
    return MSR_FAIL;
  }

  aulValue = mpValuePool [aiIndex].getNumInvokes ();

  return MSR_SUCCESS;
}

template <class _tValue>
int
MsrPool <_tValue>::getBackupStatus (int aiIndex, int &aiStatus)
{
  if (aiIndex < 0 || aiIndex >= miCurrPoolSize)
  { 
    logger.logMsg (ERROR_FLAG, 0,
      "MsrPool::getBackupStatus : index <%d>, CurrPoolSize <%d>",
      aiIndex, miCurrPoolSize);
    return MSR_FAIL;
  }

  aiStatus = mpValuePool [aiIndex].getStatus ();

  return MSR_SUCCESS;
}

template <class _tValue>
MsrPool<_tValue>& 
MsrPool <_tValue>::operator=(MsrPool<_tValue> &apSrc)
{
  return *this;
}

#endif  /* _MSR_POOL_H_ */

/************************************************************************
     Name:     Measurement Pool - includes

     Type:     C include file

     Desc:     This file provides access to Measurement Pool Node

     File:     MsrPool.h

     Sid:      MsrPool.h 0  -  11/14/03

     Prg:      gs

************************************************************************/

#include <INGwInfraMsrMgr/MsrPool.h>


MsrPool<MsrInstantValue>&
MsrPool<MsrInstantValue>::operator=(MsrPool<MsrInstantValue> &apSrc)
{
  if (this != &apSrc)
  {
    this->miTypeOfValue = apSrc.miTypeOfValue;

    if (this->mpValuePool && apSrc.mpValuePool)
    {
      this->miCurrPoolSize = apSrc.miCurrPoolSize;

      for (int liCount = 0; liCount < apSrc.miCurrPoolSize; liCount++)
      {
        this->mpValuePool [liCount] = apSrc.mpValuePool [liCount];
      }
    }
    else
      logger.logMsg (ERROR_FLAG, 0,
        "MsrPool<MsrInstantValue>::operator= : Value Pool is NULL");
  }

  return *this;
}

MsrPool<MsrAccValue>&
MsrPool<MsrAccValue>::operator=(MsrPool<MsrAccValue> &apSrc)
{
  if (this != &apSrc)
  {
    this->miTypeOfValue = apSrc.miTypeOfValue;
    this->miCurrPoolSize = apSrc.miCurrPoolSize;

    if (this->mpValuePool && apSrc.mpValuePool)
    {
      for (int liCount = 0; liCount < this->miCurrPoolSize; liCount++)
      {
        this->mpValuePool [liCount].reset();
      }

      MsrAccValue *lpLocalPool = this->mpValuePool;

      if (lpLocalPool)
      {
        this->mpValuePool = apSrc.mpValuePool;
        apSrc.mpValuePool = lpLocalPool;
      }
    }
    else
      logger.logMsg (ERROR_FLAG, 0,
        "MsrPool<MsrAccValue>::operator= : Value Pool is NULL");
  }

  return *this;
}


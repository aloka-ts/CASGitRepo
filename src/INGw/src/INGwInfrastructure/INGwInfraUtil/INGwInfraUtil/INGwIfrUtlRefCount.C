//*********************************************************************
//   GENBAND, Inc. Confidential and Proprietary
//
// This work contains valuable confidential and proprietary
// information.
// Disclosure, use or reproduction without the written authorization of
// GENBAND, Inc. is prohibited.  This unpublished work by GENBAND, Inc.
// is protected by the laws of the United States and other countries.
// If publication of the work should occur the following notice shall
// apply:
//
// "Copyright 2007 GENBAND, Inc.  All rights reserved."
//*********************************************************************
//********************************************************************
//
//     File:     INGwIfrUtlRefCount.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraUtil");

#include <errno.h>
#include <INGwInfraUtil/INGwIfrUtlRefCount.h>

using namespace std;


INGwIfrUtlRefCount::INGwIfrUtlRefCount(void) :
    msRefCount(1)
{
   INGwIfrUtlRefCount::initObject(true);
}

void INGwIfrUtlRefCount::initObject(bool consFlag)
{
   if(consFlag)
   {
#ifdef USE_LOCK_FOR_REF_COUNT

      int errCode = pthread_mutex_init(&mRefMutex, NULL);

      if(0 != errCode)
      {
         logger.logMsg(ERROR_FLAG, 0, "pthread_mutex_init() failed. [%d][%s]", 
                       errCode, strerror(errno));
      }

#endif
   }

   mId.erase();
   msRefCount = 1;
}

INGwIfrUtlRefCount::~INGwIfrUtlRefCount()
{
#ifdef USE_LOCK_FOR_REF_COUNT
   pthread_mutex_destroy(&mRefMutex);
#endif
}

void INGwIfrUtlRefCount::getRef(void)
{
#ifdef USE_LOCK_FOR_REF_COUNT
    pthread_mutex_lock(&mRefMutex);
#endif
    msRefCount++;

    logger.logINGwMsg(false, VERBOSE_FLAG, 0, "getRef:Id [%x][%s], RefCount [%d]",
                    this, mId.c_str(), msRefCount);

#ifdef USE_LOCK_FOR_REF_COUNT
    pthread_mutex_unlock(&mRefMutex);
#endif
}

void
INGwIfrUtlRefCount::resetRef(void)
{
#ifdef USE_LOCK_FOR_REF_COUNT
    pthread_mutex_lock(&mRefMutex);
#endif
    msRefCount = 1;

    logger.logINGwMsg(false, VERBOSE_FLAG, 0, "reset: Id [%s], RefCount [%d]", 
                    mId.c_str(), msRefCount);

#ifdef USE_LOCK_FOR_REF_COUNT
    pthread_mutex_unlock(&mRefMutex);
#endif
}

void INGwIfrUtlRefCount::releaseRef(void)
{
#ifdef USE_LOCK_FOR_REF_COUNT
   pthread_mutex_lock(&mRefMutex);
#endif

   msRefCount--;

   logger.logINGwMsg(false, VERBOSE_FLAG, 0, "releaseRef: Id [%x][%s], "
                                           "RefCount [%d]",
                   this, mId.c_str(), msRefCount);

    if(0 >= msRefCount) 
    {
#ifdef USE_LOCK_FOR_REF_COUNT
       pthread_mutex_unlock(&mRefMutex);
#endif
        delete this;
        return;
    }

#ifdef USE_LOCK_FOR_REF_COUNT
    pthread_mutex_unlock(&mRefMutex);
#endif
}

string INGwIfrUtlRefCount::toLog(void) const
{
   ostringstream strStream;
   strStream << " , REF_COUNT : " << msRefCount;
   return strStream.str();
}

INGwIfrUtlRefCount_var::INGwIfrUtlRefCount_var(INGwIfrUtlRefCount* apPtr) 
{ 
   mpPtr = apPtr; 
}

INGwIfrUtlRefCount_var::~INGwIfrUtlRefCount_var() 
{ 
   if(NULL != mpPtr) 
   {
      mpPtr->releaseRef(); 
   }
}

// EOF INGwIfrUtlRefCount.C

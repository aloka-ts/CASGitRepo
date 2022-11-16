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
//     File:     INGwIfrUtlLock.C
//
//     Desc:     <Description of file>
//
//     Author     			Date     			Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwIfrUtlLock");
#include <INGwInfraUtil/INGwIfrUtlLock.h>
using namespace std;

INGwIfrUtlLock::INGwIfrUtlLock(const pthread_mutex_t *mutex)
{
   _mutex = const_cast<pthread_mutex_t *>(mutex);
   pthread_mutex_lock(_mutex);
}

INGwIfrUtlLock::~INGwIfrUtlLock()
{
   pthread_mutex_unlock(_mutex);
}

INGwIfrUtlWLock::INGwIfrUtlWLock(const pthread_rwlock_t *mutex)
{
   _mutex = const_cast<pthread_rwlock_t *>(mutex);
    logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft lock INGwIfrUtlWLock %X",_mutex);
    pthread_rwlock_wrlock(_mutex);
}

INGwIfrUtlWLock::~INGwIfrUtlWLock()
{
   logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft unlock INGwIfrUtlWLock %X",_mutex);
   pthread_rwlock_unlock(_mutex);
}

INGwIfrUtlRLock::INGwIfrUtlRLock(const pthread_rwlock_t *mutex)
{
   _mutex = const_cast<pthread_rwlock_t *>(mutex);
    logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft lock INGwIfrUtlRLock %X",_mutex);
   pthread_rwlock_rdlock(_mutex);
}

INGwIfrUtlRLock::~INGwIfrUtlRLock()
{
    logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft unlock INGwIfrUtlRLock %X",_mutex);
   pthread_rwlock_unlock(_mutex);
}


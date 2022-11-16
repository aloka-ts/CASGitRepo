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
//     File:     INGwIfrMgrAgentClbkImpl.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev arya    07/12/07     Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
#include <pthread.h>

#include <INGwInfraStatMgr/INGwIfrSmCommon.h>
#include <INGwInfraStatMgr/INGwIfrSmStatMgr.h>
#include <INGwInfraStatMgr/INGwIfrSmDeferredThread.h>

#include <string>
using namespace std;

BPLOG("StatMgr");

#ifdef STAT_STUBBED
extern "C"
{
  void* startThread(void* apArg)
  {
    ((INGwIfrSmDeferredThread *)apArg)->proxyRun();
    return NULL;
  }
}

void INGwIfrSmDeferredThread::start()
{
  pthread_t tmp;
  pthread_create(&tmp, NULL, startThread, (void *)this);
}

void INGwIfrSmDeferredThread::waitInMiliSec(unsigned long aulWaitTime)
{
  usleep(aulWaitTime * 1000);
}
#endif

INGwIfrSmDeferredThread::INGwIfrSmDeferredThread()
{
  miInterval = 0;
}

INGwIfrSmDeferredThread::~INGwIfrSmDeferredThread()
{
}

bool INGwIfrSmDeferredThread::initialize(int aInterval)
{
  miInterval = aInterval;
  return true;
}

void INGwIfrSmDeferredThread::proxyRun()
{
  logger.logINGwMsg(false, TRACE_FLAG, 0, "proxyRun IN");
  while(1)
  {
    logger.logINGwMsg(false, TRACE_FLAG, 0, "proxyRun: Waiting for <%d> millisecs", miInterval);
    waitInMiliSec(miInterval);

    string outString = "";
    int status = INGwIfrSmStatMgr::instance().startDeferredProcessing(outString);
    if(status < 0)
    {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "Error performing deferred processing.");
    }
  }
}

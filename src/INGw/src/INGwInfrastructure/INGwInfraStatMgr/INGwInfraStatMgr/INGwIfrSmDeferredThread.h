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
#ifndef INGW_IFR_SM_DEFERREDTHREAD_H_
#define INGW_IFR_SM_DEFERREDTHREAD_H_

#include <INGwInfraUtil/INGwIfrUtlThread.h>

class INGwIfrSmDeferredThread
   : public INGwIfrUtlThread
{
  public:
    INGwIfrSmDeferredThread();
    ~INGwIfrSmDeferredThread();

    bool initialize(int aInterval);

    void proxyRun(void);

#ifdef STAT_STUBBED
    void start();
    void waitInMiliSec(unsigned long aulWaitTime);
#endif

  private:
    int miInterval;
};

#endif

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
//     File:     INGwIfrResMonResourceMonitor.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#ifndef _INGW_IFR_RESOURCE_MONITOR_H_
#define _INGW_IFR_RESOURCE_MONITOR_H_

#include <pthread.h>
#include <unistd.h>
#ifdef sun4Sol28
#include <kstat.h>
#include <sys/processor.h>
#endif
#include <stdio.h>
#include <errno.h>
#include <sys/sysinfo.h>
#include <sys/unistd.h>
#include <sys/types.h>

#include <INGwInfraUtil/INGwIfrUtlConfigurable.h>
#include <INGwInfraResourceMonitor/INGwIfrResMonUsageData.h>

class BpCallController;

class INGwIfrResMonResourceMonitor: public virtual INGwIfrUtlConfigurable
{
	public:

		struct BpPsrInfo {
			int miStatus;
 			int miPsrId;
      double mdCPUP;
      int miIdle;
      int miUser;
      int miKernel;
      int miWait;
#ifdef sun4Sol28
      kstat_t* mpCPU_kstat;
      cpu_stat_t mCPU_stat;
#endif 
		};

		INGwIfrResMonResourceMonitor();
		
		virtual ~INGwIfrResMonResourceMonitor();
		
		int 
		initialize();

    int 
		configure(const char* apcOID, const char* apcValue, ConfigOpType aeOpType);

		int
		startResMon();

		void
		shutdown();

	protected:

		int 
		getCPUUsage(void);

		void 
		performRM(void);
		
		int 
		initPsrInfo(void);

		int            miPsrCount;
    BpPsrInfo*     mpInfo;
#ifdef sun4Sol28
    kstat_ctl_t*   mpCPU_kstat_ctl;
#endif

		// The new values set by config() are buffered and taken in the
		// the next cycle of the call gapping thread
		bool           mbUseNewConfig;
		double         mCPU_N_new;
		double         mCPU_X_new;
		double         mCPUGrad_Y_new;
		short          mWaitPeriod;

		// The actual values used by the call gapping thread
		double         mCPU_N;
		double         mCPU_X;
		double         mCPUGrad_Y;

		double         mCPUCurr;
		double         mCPUPrev;
		double         mCPUGrad;

		unsigned int   mTimerId;
		bool					 m_isRunning;

		static const char* mpcOIDsOfInterest[];

	private:

		INGwIfrResMonResourceMonitor(const INGwIfrResMonResourceMonitor& p_Self);
		INGwIfrResMonResourceMonitor& operator=(const INGwIfrResMonResourceMonitor& 
															     p_Self);

};

#endif

// EOF INGwIfrResMonResourceMonitor.h

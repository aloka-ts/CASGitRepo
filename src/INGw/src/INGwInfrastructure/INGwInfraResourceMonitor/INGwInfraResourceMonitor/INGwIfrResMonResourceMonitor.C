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
//     File:     INGwIfrResMonResourceMonitor.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraResourceMonitor");

using namespace std;

#include <Util/imAlarmCodes.h>

#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>
#include <INGwInfraParamRepository/INGwIfrPrConfigMgr.h>
#include <INGwInfraResourceMonitor/INGwIfrResMonResourceMonitor.h>
#include <INGwInfraManager/INGwIfrMgrAlarmMgr.h>

const int ingwMAX_NORM_CPU_UTIL__DEF           =   70;
const int ingwCPU_UTIL_DELTA__DEF              =    5;
const int ingwRESOURCE_USG_MONITORING_DUR__DEF =   30;
const int ingwGRAD_FOR_HIGH_CPU_UTIL__DEF      =    2;

//const char* ingwGRAD_FOR_HIGH_CPU_UTIL = "GRAD_FOR_HIGH_CPU_UTIL";

extern "C"
void* launchMonitoringThread(void *arg)
{
	INGwIfrResMonResourceMonitor *pThis = 
											static_cast<INGwIfrResMonResourceMonitor*>(arg);
	pThis->startResMon();
	return 0;
}

const char* INGwIfrResMonResourceMonitor::mpcOIDsOfInterest[] =
{
     ingwMAX_NORM_CPU_UTIL,
     ingwCPU_UTIL_DELTA,
     ingwGRAD_FOR_HIGH_CPU_UTIL,
     ingwRESOURCE_USG_MONITORING_DUR
};

INGwIfrResMonResourceMonitor::INGwIfrResMonResourceMonitor() :
    miPsrCount(0), mTimerId(0), m_isRunning(true)
{
    LogINGwTrace(false, 0, 
			"IN INGwIfrResMonResourceMonitor::INGwIfrResMonResourceMonitor()");

    mCPU_N     = ingwMAX_NORM_CPU_UTIL__DEF;
    mCPU_X     = ingwCPU_UTIL_DELTA__DEF;
    mCPUGrad_Y = ingwGRAD_FOR_HIGH_CPU_UTIL__DEF;

    mCPUCurr = 0;
    mCPUPrev = 1;
    mCPUGrad = 0;

#ifdef sun4Sol28
		mpCPU_kstat_ctl = NULL;
#endif

    LogINGwTrace(false, 0, 
			"OUT INGwIfrResMonResourceMonitor::INGwIfrResMonResourceMonitor()");
}

INGwIfrResMonResourceMonitor::~INGwIfrResMonResourceMonitor()
{
	LogINGwTrace(false, 0, 
	"IN INGwIfrResMonResourceMonitor::~INGwIfrResMonResourceMonitor()");

	INGwIfrPrConfigMgr::getInstance().unregisterForConfig(mpcOIDsOfInterest, 
										sizeof(mpcOIDsOfInterest)/sizeof(char*), this);
	LogINGwTrace(false, 0, 
	"OUT INGwIfrResMonResourceMonitor::~INGwIfrResMonResourceMonitor()");
}

int
INGwIfrResMonResourceMonitor::initialize()
{
	LogINGwTrace(false, 0, 
	"IN INGwIfrResMonResourceMonitor::initialize()");

	mCPU_N_new = mCPU_N;
  mCPU_X_new = mCPU_X;
  mCPUGrad_Y_new = mCPUGrad_Y;
  mbUseNewConfig = false;

  int retVal = 0;

  mWaitPeriod = ingwRESOURCE_USG_MONITORING_DUR__DEF;
  INGwIfrPrParamRepository& paramRep = INGwIfrPrParamRepository::getInstance();

   try {
      string strMaxNCPUUtil = paramRep.getValue(ingwMAX_NORM_CPU_UTIL);
      configure(ingwMAX_NORM_CPU_UTIL, strMaxNCPUUtil.c_str(), 
                CONFIG_OP_TYPE_REPLACE);
   }
   catch(...) {

      logger.logINGwMsg(false, WARNING_FLAG, 0, 
                      "Unable to get ResMonitor OID [%s]", 
                      ingwMAX_NORM_CPU_UTIL);
   }

   try {
      string strDeltaNCPUUtil = paramRep.getValue(ingwCPU_UTIL_DELTA);
      configure(ingwCPU_UTIL_DELTA, strDeltaNCPUUtil.c_str(), 
                CONFIG_OP_TYPE_REPLACE);
   }
   catch(...) {
      logger.logINGwMsg(false, WARNING_FLAG, 0, 
                      "Unable to get ResMonitor OID [%s]", ingwCPU_UTIL_DELTA);
   }

   try {
      string strGradCPUUtil = paramRep.getValue(ingwGRAD_FOR_HIGH_CPU_UTIL);
      configure(ingwGRAD_FOR_HIGH_CPU_UTIL, strGradCPUUtil.c_str(), 
                CONFIG_OP_TYPE_REPLACE);
   }
   catch(...) {
      logger.logINGwMsg(false, WARNING_FLAG, 0, 
                      "Unable to get ResMonitor OID [%s]", 
                      ingwGRAD_FOR_HIGH_CPU_UTIL);
   }

   try {
      string strWaitTime = paramRep.getValue(ingwRESOURCE_USG_MONITORING_DUR);
      configure(ingwRESOURCE_USG_MONITORING_DUR, strWaitTime.c_str(), 
                CONFIG_OP_TYPE_REPLACE);
   }
   catch(...) {
      logger.logINGwMsg(false, WARNING_FLAG, 0, 
                      "Unable to get ResMonitor OID [%s]", 
                      ingwRESOURCE_USG_MONITORING_DUR);
   }

   INGwIfrPrConfigMgr::getInstance().registerForConfig(mpcOIDsOfInterest, 
								sizeof(mpcOIDsOfInterest)/sizeof(char*), this);

#ifdef sun4Sol28
   initPsrInfo();
   mpCPU_kstat_ctl = kstat_open();

   if(NULL == mpCPU_kstat_ctl) {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
                      "kstat_open() failed, retry after [%d] sec", mWaitPeriod);
      retVal = -1;
      return retVal;
   }

   for (int i = 0; i < miPsrCount; i++) {

      mpInfo[i].mpCPU_kstat = kstat_lookup(mpCPU_kstat_ctl, (char *)"cpu_stat", 
                                           mpInfo[i].miPsrId, NULL);

      if (NULL != mpInfo[i].mpCPU_kstat) {
         kstat_read(mpCPU_kstat_ctl, mpInfo[i].mpCPU_kstat, 
                    (void *)(&(mpInfo[i].mCPU_stat)) );
      }
      else {
         logger.logINGwMsg(false, ERROR_FLAG, 0, 
                         "kstat_lookup() failed for CPU Id %d", 
                         mpInfo[i].miPsrId);
      }
   }
#endif

   getCPUUsage();

   mCPUPrev = mCPUCurr;
   mCPU_N = mCPU_N_new;
   mCPU_X = mCPU_X_new;
   mCPUGrad_Y = mCPUGrad_Y_new;
   mbUseNewConfig = false;

	// create monitoring thread
	pthread_t monThrdId;
	if (0 != pthread_create(&monThrdId, NULL, launchMonitoringThread, this)) {
			logger.logINGwMsg(false, ERROR_FLAG, 0, 
							"OUT INGwIfrResMonResourceMonitor, pthread_create Failed");
	}

   return retVal;
}

#ifdef sun4Sol28

int 
INGwIfrResMonResourceMonitor::initPsrInfo(void)
{
   int n = sysconf(_SC_NPROCESSORS_CONF);

   logger.logINGwMsg(false, ALWAYS_FLAG, 0, "Call Gapping CPU count [%d]", n);

   miPsrCount = n;
   mpInfo = new BpPsrInfo[n];
   memset(mpInfo, 0, sizeof(BpPsrInfo) * n);

   int status;

   for (processorid_t i = 0; n > 0; i++) {

      int status = p_online(i, P_STATUS);

      if (status == -1 && errno == EINVAL) {
         continue;
      }
      else {
         logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
                         "Call Gapping CPU Id [%d]", i);
         mpInfo[n-1].miPsrId = i;
         mpInfo[n-1].miStatus = 0;
         n--;
      }
   }

   return 0;
}
#endif

#ifdef linux
int
INGwIfrResMonResourceMonitor::getCPUUsage(void)
{
	FILE    *statFp;
  char    line[256];

	static  int     prevUser    = -1, prevNice, prevSystem, prevIdle;
	static  int     lastCpuUtil = 0;

	int             currUser, currNice, currSystem, currIdle;

	// Open /proc/stat file.
	if ((statFp = fopen("/proc/stat", "r")) == NULL) {
		return false;
	}

	while (fgets(line, 80, statFp) != NULL) {

		if (!strncmp(line, "cpu ", 4)) {

			/*
      ** Read the number of jiffies spent in user, nice, system
      ** and idle mode among all proc on all CPUs.
      */
			sscanf(line + 5, "%u %u %u %u", &currUser, &currNice, &currSystem, 
											&currIdle);
			break;
		}
	}

	fclose(statFp);

	int  dtUser, dtSystem, dtNice, dtIdle, dtTotal, cpuUtil = 0;

	if (prevUser != -1) {

		dtUser   = currUser   - prevUser;
		dtSystem = currSystem - prevSystem;
		dtNice   = currNice   - prevNice;
		dtIdle   = currIdle   - prevIdle;

		dtTotal = dtUser + dtSystem + dtNice + dtIdle;

    // take care of integer wrap around, skip taking new reading
    // return the last value.
    if (dtTotal > 0 && dtUser >= 0 && dtSystem >= 0 && dtNice >= 0 && 
			  dtIdle >= 0) {

			cpuUtil = 100 - (dtIdle*100)/dtTotal;
		}
		else {
			cpuUtil = lastCpuUtil;
		}
	}

	lastCpuUtil = cpuUtil;
	prevUser    = currUser;
	prevNice    = currNice;
	prevSystem  = currSystem;
	prevIdle    = currIdle;

	mCPUCurr = cpuUtil;
	return 0;
}
#else

int 
INGwIfrResMonResourceMonitor::getCPUUsage(void)
{
   int retValue = 0;

   mCPUCurr = 0;

		for (int i = 0; i < miPsrCount; i++) {

			if (NULL == mpInfo[i].mpCPU_kstat) {
         mpInfo[i].mpCPU_kstat = 
                     kstat_lookup(mpCPU_kstat_ctl, (char *)"cpu_stat", 
                                  mpInfo[i].miPsrId, NULL);

         if (NULL == mpInfo[i].mpCPU_kstat) {
            logger.logINGwMsg(false, ERROR_FLAG, 0, 
                            "kstat_lookup() failed for CPU Id [%d]", 
                            mpInfo[i].miPsrId);
            retValue = -1;
            continue;
         }
      }

      kstat_read(mpCPU_kstat_ctl, mpInfo[i].mpCPU_kstat, 
                 (void *)(&(mpInfo[i].mCPU_stat)) );

      int idle   = mpInfo[i].mCPU_stat.cpu_sysinfo.cpu[CPU_IDLE];
      int user   = mpInfo[i].mCPU_stat.cpu_sysinfo.cpu[CPU_USER];
      int kernel = mpInfo[i].mCPU_stat.cpu_sysinfo.cpu[CPU_KERNEL];
      int wait   = mpInfo[i].mCPU_stat.cpu_sysinfo.cpu[CPU_WAIT];

      int totaldiff = idle   - mpInfo[i].miIdle;
      totaldiff    += user   - mpInfo[i].miUser;
      totaldiff    += kernel - mpInfo[i].miKernel;
      totaldiff    += wait   - mpInfo[i].miWait;

      mpInfo[i].mdCPUP = (double)((idle - mpInfo[i].miIdle))*100.0/totaldiff;
      mpInfo[i].mdCPUP = 100 - mpInfo[i].mdCPUP;

      logger.logMsg(TRACE_FLAG, 0, 
                    "Processor[%d] CPU[%.2f] User[%d] Kernel[%d] Idle[%d] "
                    "wait[%d]", mpInfo[i].miPsrId, mpInfo[i].mdCPUP, user, 
										kernel, idle, wait);

      mCPUCurr += mpInfo[i].mdCPUP;

      mpInfo[i].miIdle   = idle;
      mpInfo[i].miUser   = user;
      mpInfo[i].miKernel = kernel;
      mpInfo[i].miWait   = wait;
   }

   mCPUCurr = mCPUCurr/miPsrCount;

   logger.logMsg(ALWAYS_FLAG, 0, "System CPU [%.2f]", mCPUCurr);

   return retValue;
}
#endif

int 
INGwIfrResMonResourceMonitor::startResMon()
{
	LogINGwTrace(false, 0, "IN handleWorkerClbk");

	while (m_isRunning) {

		sleep (mWaitPeriod);
		performRM();
	}

	LogINGwTrace(false, 0, "OUT handleWorkerClbk");
	return 0;
}

void
INGwIfrResMonResourceMonitor::shutdown()
{
	m_isRunning = false;
}

void 
INGwIfrResMonResourceMonitor::performRM(void)
{
   getCPUUsage();

   if (mCPUCurr < 0) {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "ERROR Curr CPU is [%.2f]", 
                      mCPUCurr);
      return;
   }

   mCPUGrad = ((mCPUCurr - mCPUPrev) * 100)/mCPUPrev;

   logger.logINGwMsg(false, TRACE_FLAG, 0, "CPU Stat: Prev[%.2f] Curr[%.2f], "
					"Inc[%.2f%%] Setup: Max[%.2f] Grad[%.2f%%] NormalBand[%.2f]", 
					 mCPUPrev, mCPUCurr, mCPUGrad, mCPU_N, mCPUGrad_Y, mCPU_X);

   mCPUPrev = mCPUCurr;

   BpResUsageData data;
   data.setMaxNormalCPUUtil(mCPU_N);
   data.setCurrentCPUUtil(mCPUCurr);

   if (mCPUCurr > mCPU_N) {
		INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::CONFIGURATION, 
          __FILE__, __LINE__, CCM_RESOURCE_USG_ABOVE_NORMAL, "CCM",
          INGwIfrPrParamRepository::getInstance().getSelfId(), 
          "Current CPU util [%.2f] exceeds max normal [%.2f]",
          mCPUCurr, mCPU_N);

			printf("Rajeev...CPU ABOVE NORMAL Current CPU util [%.2f] exceeds max normal [%.2f]\n",
			mCPUCurr, mCPU_N);
			fflush(stdout);

      data.setCPUUtilLevel(BpResUsageData::ABOVE_NORMAL_BAND);

      if (-mCPUGrad_Y > mCPUGrad) {
         data.setProjLevelChange(true);
      }
   }
   else {
      if (mCPU_N - mCPUCurr <= mCPU_X) {
         data.setCPUUtilLevel(BpResUsageData::WITHIN_NORMAL_BAND);

         if ( ((100 + mCPUGrad) * mCPUCurr/100) > mCPU_N) {
            data.setProjLevelChange(true);
         }
      }
      else {
         data.setCPUUtilLevel(BpResUsageData::BELOW_NORMAL_BAND);
      }
   }

   //mpCallCtrl->processResUsageData(data);

   if (true == mbUseNewConfig) {
      mbUseNewConfig = false;
      mCPU_N         = mCPU_N_new;
      mCPU_X         = mCPU_X_new;
      mCPUGrad_Y     = mCPUGrad_Y_new;
   }
}

int 
INGwIfrResMonResourceMonitor::configure(const char* apcOID, 
															const char* apcValue, ConfigOpType aeOpType)
{
    logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"Received config for Call Gapping [%s, %s]", apcOID, apcValue);

    int retVal = 0;
    if (0 == strcmp(apcOID, ingwMAX_NORM_CPU_UTIL)) {
        double value = static_cast<double>(strtol(apcValue, NULL, 10));
        if(value > 0) {
             mCPU_N_new = value;
             mbUseNewConfig = true;
        }
    }
    else if(0 == strcmp(apcOID, ingwCPU_UTIL_DELTA)) {
        double value = static_cast<double>(strtol(apcValue, NULL, 10));
        if(value > 0) {
             mCPU_X_new = value;
             mbUseNewConfig = true;
        }
    }
    else if(0 == strcmp(apcOID, ingwGRAD_FOR_HIGH_CPU_UTIL)) {
        double value = static_cast<double>(strtol(apcValue, NULL, 10));
        if(value > 0) {
             mCPUGrad_Y_new = value;
             mbUseNewConfig = true;
        }
    }
    else if(0 == strcmp(apcOID, ingwRESOURCE_USG_MONITORING_DUR)) {
        short value = static_cast<short>(strtol(apcValue, NULL, 10));
        if(value > 0) {
             mWaitPeriod = value;
        }
    }

    return retVal;
}

// EOF INGwIfrResMonResourceMonitor.C

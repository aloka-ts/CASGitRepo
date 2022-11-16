#include "cpu.h"

struct BpPsrInfo {
    int miStatus;
    int miPsrId;
    double mdCPUP;
    int miIdle;
    int miUser;
    int miKernel;
    int miWait;
#ifdef sol28g
    kstat_t* mpCPU_kstat;
    cpu_stat_t mCPU_stat;
#endif
} ;

#define MAX_CPU_NUM			128	

int                miPsrCount;
struct BpPsrInfo   mpInfo[MAX_CPU_NUM];

#ifdef sol28g
kstat_ctl_t*       mpCPU_kstat_ctl;
#endif


int initialize(void)
{
   int retVal = 0;

#ifdef sol28g
   int i = 0;
   initPsrInfo();
   mpCPU_kstat_ctl = kstat_open();

   if(NULL == mpCPU_kstat_ctl)
   {
      printf("kstat_open() failed\n");
	  fflush(stdout);
      retVal = -1;
      return retVal;
   }

   for( ; i < miPsrCount; i++)
   {
      mpInfo[i].mpCPU_kstat = kstat_lookup(mpCPU_kstat_ctl, (char *)"cpu_stat",
                                           mpInfo[i].miPsrId, NULL);
      if(NULL != mpInfo[i].mpCPU_kstat)
      {
         kstat_read(mpCPU_kstat_ctl, mpInfo[i].mpCPU_kstat,
                    (void *)(&(mpInfo[i].mCPU_stat)) );
      }
      else
      {
         printf("kstat_lookup() failed for CPU Id [%d]\n", mpInfo[i].miPsrId);
		 fflush(stdout);
      }
   }
#endif

   return retVal;
}
      
int initPsrInfo(void)
{  
#ifdef sol28g
   processorid_t i = 0;
   int status;                          
   //int n = sysconf(_SC_NPROCESSORS_CONF);
   int n = sysconf(_SC_NPROCESSORS_ONLN);
   printf("CPU count on machine is [%d]\n", n);
   fflush(stdout);
                      
   miPsrCount = n;    
   memset(mpInfo, 0, sizeof(BpPsrInfo) * n);
   
   for ( ; n > 0; i++)
   {
      int status = p_online(i, P_STATUS);
      if(status == -1) // && errno == EINVAL)
      {
		 printf("CPU not found for Id [%d] status[%d] errno[%d]\n", i, status, errno);
		 fflush(stdout);
         continue;
      }
      else 
      {               
		 printf("CPU found for Id [%d] status[%d]\n", i, status);
		 fflush(stdout);
         mpInfo[n-1].miPsrId = i;
         mpInfo[n-1].miStatus = 0;
         n--;
      }
   }  

#endif

   return 0;
}


float getCPUUsage(void)
{
   float retValue = 0;
   double mCPUCurr = 0;

#ifdef sol28g
   int i = 0;
   int idle = 0;
   int user = 0;
   int kernel = 0;
   int wait = 0;
   int totaldiff = 0;

   for( ; i < miPsrCount; i++)
   {
      if(NULL == mpInfo[i].mpCPU_kstat)
      {
         mpInfo[i].mpCPU_kstat =
                     kstat_lookup(mpCPU_kstat_ctl, (char *)"cpu_stat",
                                  mpInfo[i].miPsrId, NULL);
         if(NULL == mpInfo[i].mpCPU_kstat)
         {
            printf("kstat_lookup() failed for CPU Id [%d]\n", mpInfo[i].miPsrId);
			fflush(stdout);
            retValue = -1;
            continue;
         }
      }

      kstat_read(mpCPU_kstat_ctl, mpInfo[i].mpCPU_kstat,
                 (void *)(&(mpInfo[i].mCPU_stat)) );

      idle   = mpInfo[i].mCPU_stat.cpu_sysinfo.cpu[CPU_IDLE];
      user   = mpInfo[i].mCPU_stat.cpu_sysinfo.cpu[CPU_USER];
      kernel = mpInfo[i].mCPU_stat.cpu_sysinfo.cpu[CPU_KERNEL];
      wait   = mpInfo[i].mCPU_stat.cpu_sysinfo.cpu[CPU_WAIT];

      totaldiff = idle   - mpInfo[i].miIdle;
      totaldiff    += user   - mpInfo[i].miUser;
      totaldiff    += kernel - mpInfo[i].miKernel;
      totaldiff    += wait   - mpInfo[i].miWait;

      mpInfo[i].mdCPUP = (double)((idle - mpInfo[i].miIdle))*100.0/totaldiff;
      mpInfo[i].mdCPUP = 100 - mpInfo[i].mdCPUP;

      /*
      printf("Processor[%d] CPU[%.2f] User[%d] Kernel[%d] Idle[%d]\n",
                 mpInfo[i].miPsrId, mpInfo[i].mdCPUP, user, kernel, idle);
      */

      mCPUCurr += mpInfo[i].mdCPUP;

      mpInfo[i].miIdle   = idle;
      mpInfo[i].miUser   = user;
      mpInfo[i].miKernel = kernel;
      mpInfo[i].miWait   = wait;
   }

   mCPUCurr = mCPUCurr/miPsrCount;
   retValue = mCPUCurr;

#elif redhat80g
   FILE    *statFp;
   char    line[256];

   static  int  prevUser    = -1, prevNice, prevSystem, prevIdle;
   static  int  lastCpuUtil = 0;

   int          currUser, currNice, currSystem, currIdle;

   // Open /proc/stat file.
   if ((statFp = fopen("/proc/stat", "r")) == NULL)
       return false;

   while (fgets(line, 80, statFp) != NULL)
   {
       if (!strncmp(line, "cpu ", 4))
       {
           /*
           ** Read the number of jiffies spent in user, nice, system
           ** and idle mode among all proc on all CPUs.
           */
           sscanf(line + 5, "%u %u %u %u", &currUser, &currNice, &currSystem, &currIdle);
           break;
       }
   }
   fclose(statFp);
   int  dtUser, dtSystem, dtNice, dtIdle, dtTotal, cpuUtil = 0;

   if (prevUser != -1)
   {
       dtUser   = currUser   - prevUser;
       dtSystem = currSystem - prevSystem;
       dtNice   = currNice   - prevNice;
       dtIdle   = currIdle   - prevIdle;

       dtTotal = dtUser + dtSystem + dtNice + dtIdle;

       // take care of integer wrap around, skip taking new reading
       // return the last value.
       if (dtTotal > 0 && dtUser >= 0 && dtSystem >= 0 && dtNice >= 0 && dtIdle >= 0)
           cpuUtil = 100 - (dtIdle*100)/dtTotal;
       else
           cpuUtil = lastCpuUtil;
   }

   lastCpuUtil = cpuUtil;
   prevUser    = currUser;
   prevNice    = currNice;
   prevSystem  = currSystem;
   prevIdle    = currIdle;

   mCPUCurr = cpuUtil;
   retValue = mCPUCurr;
#endif

   /* printf("System CPU [%.2f]\n", retValue); */
   return retValue;
}

/* EOF */


#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwMain");

#include <typeinfo>

#include <unistd.h>
#include <pthread.h>

#include <Agent/BayAgentImpl.h>

#include <INGwFtTalk/INGwFtTkInterface.h>

#include <INGwInfraUtil/INGwIfrUtlLock.h>

#include <INGwInfraManager/INGwIfrMgrManager.h>
#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>


using namespace std;

#ifdef LINUX_UNNECESSARY

typedef struct 
{
   int    argc;
   char **argv;
}ProgArgs;

int newMain(int, char**);

extern "C" void * newMainStart(void *data)
{
   ProgArgs *input = (ProgArgs *)data;
   int ret = newMain(input->argc, input->argv);
   delete input;

   logger.logMsg(ALWAYS_FLAG, 0, "Prog quitting with status [%d]", ret);
   printf("Normal quit of Main with status [%d]\n", ret);
   exit(ret);
   return NULL;
}

#endif

int main(int argc, char** argv) 
{

   //INCTBD - Temporary for attaching dbx, to be removed
   char *lpcEnv = getenv ("INGW_STARTUP_SLEEP");
   if(NULL != lpcEnv)
   {
     int liSleepTime = atoi(lpcEnv);
     sleep(liSleepTime);
   }
#ifdef LINUX_UNNECESSARY
   
   pthread_t newMainThread;
   ProgArgs *newMainInput = new ProgArgs;
   newMainInput->argc = argc;
   newMainInput->argv = argv;
   pthread_create(&newMainThread, NULL, newMainStart, newMainInput);

   waitpid(0, 0, __WCLONE);

   logger.logMsg(ALWAYS_FLAG, 0, "ThreadMgr quits. Quitting explicitly.");

   if(getenv("CCM_CORE_EXPLICIT") != NULL)
   {
      char ***ptr = (char ***)0x34;

      if(ptr[23][23][23] == 45)
      {
         ***ptr = 34;
      }
   }

   kill(0, SIGKILL);
   return 1;
}

int newMain(int argc, char **argv)
{
#endif
   // PANKAJ Version Mgr
   //new RSI_NSP_CCM::VersionMgr();

   const char *lDelay = getenv("CCM_DBX_WAIT");

   if(NULL != lDelay) 
   {
      int lSleepTime = atoi(lDelay);
      logger.logMsg(ERROR_FLAG, imERR_NONE, "Sleeping for <%d> seconds",
                    lSleepTime);
      sleep(lSleepTime);
   }
    
   try 
   {
      BayAgentImpl* agent = BayAgentImpl::instance(argc, argv);
      LogAlways(0, "Agent Created.");

      INGwIfrMgrManager& ingwMgr = INGwIfrMgrManager::getInstance();
      ingwMgr.initialize(argc, (const char**)argv);
      LogAlways(0, "INGwIfrMgrManager Initialized.");

      {
         //Lock is required to avoid negotiation before the versions are loaded.
         //INGwIfrUtlGuard(RSI_NSP_CCM::VersionMgr::getInstance().getLock());

         long lINGwId = INGwIfrPrParamRepository::getInstance().getSelfId();
         ObjectId selfId(lINGwId, 0);
         INGwFtTkInterface* pBayTalk = INGwFtTkInterface::instance(&ingwMgr);

         LogAlways(0, "BayTalk Initialized sucessfully.");

         ingwMgr.registerIfaceWithAgent();
         LogAlways(0, "Components registeration with Agent Done.");

         agent->getAndSetAllCfgParams();
         LogAlways(0, "Components configured.");

         agent->startupComponents();
      }

      if(agent->getState() != RSIEmsTypes::ComponentState_Loaded)
      {
         LogAlways(0, "Components startup failed. Might be configuration "
                      "issue.");
         LogAlways(0, "Quitting deliberately.");
         return 1;
      }

      LogAlways(0, "Components loaded successfully.");

      agent->changeComponentState(RSIEmsTypes::ComponentState_Running);
      LogAlways(0, "Changed Components state successfully.");
      agent->reportStateToBayManager();
      LogAlways(0, "Reported state to BayManager successfully.");

      if(agent->getState() != RSIEmsTypes::ComponentState_Running)
      {
         LogAlways(0, "Components failed to go Running state.");
         LogAlways(0, "Quitting deliberately.");
         return 1;
      }
      ingwMgr.connectPeerSubsystems();
      LogAlways(0, "Connected peer subsystem successfully.");

      agent->startProcessingCORBARequests();

      LogAlways(0, "Normal shutdown.");
   }
	 catch(const CORBA::Exception& eCorba)
   {
   	logger.logMsg(ALWAYS_FLAG, 0, 
	"CORBA:EXCEPTION Id [%s] and info [%s]",
	eCorba._rep_id(), eCorba._info().c_str());
   }

   catch(...) 
   {
      LogAlways(0, "Quitting on exception.");
   }

   LogAlways(0, "The End.");
   printf("The End.\n");
   return 0;
}

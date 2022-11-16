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
//     File:     INGwIfrTlIfTelnetIntf.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwTelnetIface");
#define CCPU_STUB
using namespace std;
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <iomanip>

#include <strstream>
#include <fstream>
#include <errno.h>

#include "INGwStackManager/INGwSmAlmHdlr.h"
#include <INGwTcapProvider/INGwSil.h>
#include <INGwInfraTelnetIface/INGwIfrTlIfTelnetIntf.h>
#include <INGwIwf/INGwIwfProvider.h>
#include <INGwIwf/INGwIwfIface.h>
#include <INGwSipProvider/INGwSpSipProvider.h>
#include <INGwSipProvider/INGwSpSipCallController.h>
#include <INGwInfraManager/INGwIfrMgrThreadMgr.h>
#include <INGwInfraManager/INGwIfrMgrRoleMgr.h>
#include <INGwInfraManager/INGwIfrMgrManager.h>
#include <INGwInfraManager/INGwIfrMgrThreadMgr.h>
#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>
#include <INGwTcapProvider/INGwTcapProvider.h>
#include <INGwStackManager/INGwSmBlkConfig.h>

#include <INGwTcapProvider/INGwSilRx.h>
#include <INGwTcapProvider/INGwTcapFtHandler.h>

#include <INGwInfraUtil/INGwIfrUtlConfigurable.h>
#include <Util/StatCollector.h>
#include <INGwInfraMsrMgr/MsrMgr.h>
#include <INGwInfraStatMgr/INGwIfrSmStatMgr.h>
#include <INGwCommonTypes/INCTags.h>
#include <INGwStackManager/INGwSmWrapper.h>
#include "asdli.h"
#include "adax_trill.h"
#include "lsd.x"
#include "sdt.x"

// For debugging - Start [
bool g_enableDiagLogs = false;
// ] End - For debugging

int stack_log_mask;
int tcapLoDlgId;
int tcapHiDlgId;
extern int g_GetMsgCountThrs;
extern int g_PutMsgCountThrs;
extern int g_EnableMBuffStore;
#ifdef PK_UNPK_DEBUG_FLAG
extern void initPkUnpkStore();
#endif

int setOutputFile(const char *finalname);
int runCall(string& p_Count, string& rate, string& ip);
#ifdef CCPU_STUB
#define IDP_MSG     1
#define P_ABT_MSG   2
#define O_ANS_MSG   3
#define O_DISC_MSG  4

  int testCall(int p_msgNum, int dlgId = 111);

  int runLoad(int p_numberOfCalls);
  int doSs7SigtranConfig();
#endif

// To be removed
bool configLoopBack = false;

/**
* Description : 
*
* @param <arInputStr> -
* @param <apcOutput> -
* @param <size     > - 
* @param <newFD> - 
*
* @return <bool> - 
*
*/
struct  debugLevelInfo{
 char* mCmd;
 char* mLayer;
 U32 mDbgMask;
 INGwTcapProvider *mTcapProvider; 
};

struct  statsInfo{
 long int mStatMask;
 INGwTcapProvider *mTcapProvider; 
};

pthread_t dummyConfThr;
pthread_t setDbgLevelThr;
pthread_t setstatMask;
static bool isConfDone = false;
void * doConfiguration(void *apArg){
  INGwTcapProvider * pTcapProvider = (INGwTcapProvider*)apArg;
  pTcapProvider->getSmWrapperPtr()->confSs7SigtranData();
  return 0;
}

int validateLayer(char* apcLayer){
  int retVal = 0; 
  if(NULL != apcLayer){
    if(0==strcasecmp("TCAP",apcLayer)
       || 0== strcasecmp("SCCP",apcLayer)
       || 0== strcasecmp("MTP2",apcLayer)
       || 0== strcasecmp("M3UA",apcLayer)
       || 0== strcasecmp("MTP3",apcLayer)
       || 0== strcasecmp("SCTP",apcLayer)
       || 0== strcasecmp("TUCl",apcLayer)
       || 0== strcasecmp("PSF_M3UA",apcLayer)
       || 0== strcasecmp("PSF_MTP3",apcLayer)
       || 0== strcasecmp("PSF_TCAP",apcLayer)
       || 0== strcasecmp("PSF_SCCP",apcLayer)
       || 0== strcasecmp("LDF_M3UA",apcLayer)
       || 0== strcasecmp("LDF_MTP3",apcLayer)
       || 0== strcasecmp("SG",apcLayer)
       || 0== strcasecmp("SH",apcLayer)
       || 0== strcasecmp("MR",apcLayer)
       || 0== strcasecmp("RELAY",apcLayer)
       || 0== strcasecmp("ALL",apcLayer)
      ){
      
      retVal = 1;
    }
  }
 return retVal;
}
extern "C"
void * sendReqToStack(void *apArg){
  debugLevelInfo * lInfo = (debugLevelInfo *)apArg; 
  INGwTcapProvider * pTcapProvider = lInfo->mTcapProvider;
  pTcapProvider->getSmWrapperPtr()->enableDisableDebug(lInfo->mCmd, lInfo->mLayer, lInfo->mDbgMask);
  if(lInfo != NULL){
    /*if(lInfo->mCmd != NULL){
      delete lInfo->mCmd;
      lInfo->mCmd = NULL;
    }
    if(lInfo->mLayer != NULL){
      delete lInfo->mLayer;
      lInfo->mLayer = NULL;
    }*/
    delete lInfo;
    lInfo = NULL;
  }
  return 0;
}

extern "C"
void * sendTrcReqToStack(void *apArg){
  debugLevelInfo * lInfo = (debugLevelInfo *)apArg; 
  INGwTcapProvider * pTcapProvider = lInfo->mTcapProvider;
  pTcapProvider->getSmWrapperPtr()->enableDisableTrace(lInfo->mCmd, lInfo->mLayer, lInfo->mDbgMask);
  return 0;
}

extern "C"
void * sendReqToStackManager(void *apArg){
  statsInfo * lInfo = (statsInfo *)apArg; 
  INGwTcapProvider * pTcapProvider = lInfo->mTcapProvider;
  pTcapProvider->getSmWrapperPtr()->setStatsMask(lInfo->mStatMask);
  return 0;
}

int
startConfiguration() {
   LogINGwTrace(false, 0, "startConfiguration()");
   if(0 != pthread_create(&dummyConfThr, NULL, doConfiguration,
                           &(INGwTcapProvider::getInstance()) )) {
      logger.logMsg(ERROR_FLAG, 0, 
                    "Thread to execute dummy doConfiguration func created");
      return 1;
   }
   LogINGwTrace(false, 0, "startConfiguration()");
   return 0;
}

int
setStatMask(long int statMask) {
   LogINGwTrace(false, 0, "setStatMask()");
   statsInfo *lInfo = new statsInfo;
   memset(lInfo,0,sizeof(statsInfo));

   lInfo->mStatMask = statMask;
   lInfo->mTcapProvider = &(INGwTcapProvider::getInstance());

   if(0 != pthread_create(&setstatMask, NULL, sendReqToStackManager,lInfo)) {
      logger.logMsg(ERROR_FLAG, 0, 
                    "Created thread to send set statistics mask stack manager");
      return 1;
   }
   LogINGwTrace(false, 0, "setStatMask()");
   return 0;
}

int
setDebugLevel(char* apcCmd, char* apcLayer, U32 lldbgMask) {
   LogINGwTrace(false, 0, "setDebugLevel()");
   debugLevelInfo *lInfo = new debugLevelInfo;
   memset(lInfo,0,sizeof(debugLevelInfo));

   lInfo->mCmd    = apcCmd;
   lInfo->mLayer  = apcLayer;
   lInfo->mDbgMask= lldbgMask;
   lInfo->mTcapProvider = &(INGwTcapProvider::getInstance());

   if(0 != pthread_create(&setDbgLevelThr, NULL, sendReqToStack,lInfo)) {
      logger.logMsg(ERROR_FLAG, 0, 
                    "Created thread to send set debug level req to stack");
      return 1;
   }
   LogINGwTrace(false, 0, "setDebugLevel()");
   return 0;
}

int
setTraceLevel(char* apcCmd, char* apcLayer, U32 lldbgMask) {
   LogINGwTrace(false, 0, "setTraceLevel()");
   debugLevelInfo *lInfo = new debugLevelInfo;
   memset(lInfo,0,sizeof(debugLevelInfo));

   lInfo->mCmd    = apcCmd;
   lInfo->mLayer  = apcLayer;
   lInfo->mDbgMask= lldbgMask;
   lInfo->mTcapProvider = &(INGwTcapProvider::getInstance());

   if(0 != pthread_create(&setDbgLevelThr, NULL, sendTrcReqToStack,lInfo)) {
      logger.logMsg(ERROR_FLAG, 0, 
                    "Created thread to send set debug level req to stack");
      return 1;
   }
   LogINGwTrace(false, 0, "setTraceLevel()");
   return 0;
}


bool 
cliFunc(const string& arInputStr, char** apcOutput, int& size, int newFD)
{
   logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, 
	 "Received <%s> for parsing", arInputStr.c_str());

   int  maxRetMsgSize = 400 + 1;
   bool isParamValid = true;
   bool isWriteInFile = false;
	 string lFileName;
   bool retResult = false;

   *apcOutput = NULL;
   char* command = new char[(arInputStr.size() + 1)];
   strcpy(command, arInputStr.c_str());

   char separator[] = " \t\n\r";
   char* brkt;
   char* paramName = NULL;
   char* word = strtok_r(command, separator, &brkt);

   if(NULL != word) 
   {
      paramName = strtok_r(NULL, separator, &brkt);
   }

   if((NULL == word) || (NULL == paramName)) 
   {
      isParamValid = false;
   }
   else if(0 == strcasecmp("set", word)) 
   {
      char* argv1 = strtok_r(NULL, separator, &brkt);
      if(NULL == argv1) 
      {
         isParamValid = false;
      }
	    else if (0 == strcasecmp("Ss7SigCfg", paramName))
	    {
        if(NULL == argv1) 
        {
           isParamValid = false;
        }
         
        if(isParamValid && (0 == strcasecmp("@99",argv1))) { 
          ostrstream output;
          int lCurrRole = INGwTcapProvider::getInstance().myRole();
          
          if(!isConfDone && lCurrRole == TCAP_PRIMARY)
          {
            isConfDone = true;
            output << endl<<"Configuration being done as per value of env var SM_TRANSPORT_TYPE"
                   << endl<<"... SS7: 0| Sigtran 1| Coexistance 2 "<<endl;
 
            char* lpcTransportType = getenv("SM_TRANSPORT_TYPE");
            if(NULL != lpcTransportType) {
              int liTransportType =  atoi(lpcTransportType);
              if(0 == liTransportType || 1 == liTransportType || 2 == liTransportType)        {
                startConfiguration();
              }
              else{
	   	         logger.logMsg(ERROR_FLAG, 0, "INVALID SM_TRANSPORT_TYPE <%d> "
                  "valid:0,1,2", liTransportType);
              }
            }
            else{
	   	         logger.logMsg(ERROR_FLAG, 0, "Configure SM_TRANSPORT_TYPE") ;
            }
             *apcOutput = BP_NEW_CHAR(output.pcount() + 1);
             (*apcOutput)[output.pcount()] = '\0';

             strncpy(*apcOutput, output.str(), output.pcount());

             delete []output.str();
          }
          else {
             if(lCurrRole)
             output << endl<<"Configuration request already sent"<<endl;
             else
             output << endl<<"Configuration request is served by the primary node only!!!"<<endl;
            *apcOutput = BP_NEW_CHAR(output.pcount() + 1);
            (*apcOutput)[output.pcount()] = '\0';

            strncpy(*apcOutput, output.str(), output.pcount());

            delete []output.str();
          }
        }
        else{
	   	    logger.logMsg(ERROR_FLAG, 0, "+VER+ Not serving the Conf Req") ;
        }
	    }
      else if(0 == strcasecmp("stk-logmask-inc", paramName)) 
      {

         logger.logMsg(TRACE_FLAG, 0, "mask layer:%s",argv1) ;
         char* argv2 = strtok_r(NULL, separator, &brkt); 

         if (strcasecmp("MEM", argv1) == 0) {
           if (argv2 != NULL) {
             g_GetMsgCountThrs = atoi(argv2);
             g_PutMsgCountThrs = atoi(argv2);
			       logger.logMsg(ALWAYS_FLAG, 0, 
                    "Setting g_GetMsgCountThrs and g_PutMsgCountThrs to <%d>",
                    g_GetMsgCountThrs);
           }
           else {
             g_GetMsgCountThrs = -1;
             g_PutMsgCountThrs = -1;
			       logger.logMsg(ALWAYS_FLAG, 0, 
                    "Setting g_GetMsgCountThrs and g_PutMsgCountThrs to -1");
           }
         }

         ostrstream output;

         INGwTcapProvider::getInstance().getSmWrapperPtr()->setStkLogMask(argv1); 

         output << endl <<"ccpu logmask set <" 
                  << stack_log_mask <<">"
                  << endl;


         *apcOutput = BP_NEW_CHAR(output.pcount() + 1);
         (*apcOutput)[output.pcount()] = '\0';

         strncpy(*apcOutput, output.str(), output.pcount());

         delete []output.str();
      }

      else if (0 == strcasecmp("stats-mask", paramName))
	    {
        ostrstream output;
        long lStatMask = atoi(argv1);
       
        int lCurrRole = INGwTcapProvider::getInstance().myRole();
            
        if(lCurrRole == TCAP_PRIMARY)
        {
          logger.logINGwMsg(false,TRACE_FLAG,0,"[stats-mask] Mask <%lu> ",lStatMask); 
          setStatMask(lStatMask);
        }
        else{
          output<<endl<<"Can execute this command only on primary node!!"<<endl;
        }
        *apcOutput = BP_NEW_CHAR(output.pcount() + 1);
        (*apcOutput)[output.pcount()] = '\0';

        strncpy(*apcOutput, output.str(), output.pcount());

        delete []output.str();
	    }

      else if (0 == strcasecmp("abort-tc-dlg", paramName))
      {
        //set  set-debug-level enable/disable sccp   debugmask
        //word paramName       argv1          argv2   argv3
         ostrstream output;
         char* argv2 = strtok_r(NULL, separator, &brkt); 
         char* argv3 = strtok_r(NULL, separator, &brkt);
         long int liDlgId = 0;
      
        
         if(argv1 && argv2)
         {
             liDlgId = atoi(argv1);
             if(!((tcapLoDlgId  <= liDlgId) && (tcapHiDlgId >liDlgId))) 
             {
               output<<endl<<"Invalid dlgid :"<<liDlgId<<endl; 
               return false;
             }
             int lCurrRole = INGwTcapProvider::getInstance().myRole();
             
             if(lCurrRole == TCAP_PRIMARY)
             {
               short lsSpId = atoi(argv2);
               short lsSuId = -1;
               unsigned char lcSsn;
               logger.logINGwMsg(false,ALWAYS_FLAG,0,
               "abort-tc-dlg dlgId<%d> spId <%d>", liDlgId, lsSpId);
               if((lsSpId >= 0) && (lsSpId <8)) 
               {
                 INGwTcapProvider::getInstance().
                                       getSuIdForSpId (lsSuId, lsSpId,lcSsn);
                 TcapMessage ltcMsgObj;
                 if(-1 != lsSuId) 
                 {
                   ltcMsgObj.closeDialogue(0x01, liDlgId, 
                                     lsSuId, lsSpId, false,
                                     true, false, true);
                 }
                 else
                 {
                   logger.logINGwMsg(false,ALWAYS_FLAG,0,"");
                   output<<endl<<"Invalid suId:"<<lsSuId<<endl;
                   return false;
                 }
               }
               else{
                   output<<endl<<"Invalid suId:"<<lsSpId<<endl;

               }
             }
             else{
               output<<endl<<"Can execute this command only on primary node!!"<<endl;
             }
              *apcOutput = BP_NEW_CHAR(output.pcount() + 1);
              (*apcOutput)[output.pcount()] = '\0';
      
              strncpy(*apcOutput, output.str(), output.pcount());
      
              delete []output.str();
         } 
         else {
            output << endl<<"Invalid Input!!!"<<endl;
           *apcOutput = BP_NEW_CHAR(output.pcount() + 1);
           (*apcOutput)[output.pcount()] = '\0';
      
           strncpy(*apcOutput, output.str(), output.pcount());
      
           delete []output.str();
         }
      }
      else if (0 == strcasecmp("stack-debug-level", paramName))
	    {
       //set  set-debug-level enable/disable sccp   debugmask
       //word paramName       argv1          argv2   argv3
        ostrstream output;
        long lDbgMask = 1;
        char* argv2 = strtok_r(NULL, separator, &brkt); 
        char* argv3 = strtok_r(NULL, separator, &brkt);

       
        if(argv1 && (0==strcasecmp("ENABLE",argv1) || 0==strcasecmp("DISABLE",argv1)) 
            && validateLayer(argv2)) 
        {
            if((0==strcasecmp("ENABLE",argv1)) || (0==strcasecmp("DISABLE",argv1))){ 
              if(NULL==argv3)
              return false;
              else
              lDbgMask = strtol(argv3, NULL, 10);
            }

            int lCurrRole = INGwTcapProvider::getInstance().myRole();
            
            if(lCurrRole == TCAP_PRIMARY)
            {
              char* largv1 = new char[strlen(argv1)+1];
              strcpy(largv1,argv1);

              char* largv2 =  new char[strlen(argv2)+1];
              strcpy(largv2,argv2);

              logger.logMsg(TRACE_FLAG,0,"[stack-debug-level]command <%s> layer <%s> dbgMask <%d>",largv1,largv2,lDbgMask); 
              if(lDbgMask >= 0 && lDbgMask < 8) {
                setDebugLevel(largv1,largv2,lDbgMask);
              }
              else{
	   	         logger.logMsg(ERROR_FLAG, 0, "INVALID debug level <%d>",
                 lDbgMask);
                output<<endl<<"Invalid debug level"<<endl; 
              }
            }

            else{
              output<<endl<<"Can execute this command only on primary node!!"<<endl;
            }
             *apcOutput = BP_NEW_CHAR(output.pcount() + 1);
             (*apcOutput)[output.pcount()] = '\0';

             strncpy(*apcOutput, output.str(), output.pcount());

             delete []output.str();
        } 
        else {
           output << endl<<"Invalid Input!!!"<<endl;
          *apcOutput = BP_NEW_CHAR(output.pcount() + 1);
          (*apcOutput)[output.pcount()] = '\0';

          strncpy(*apcOutput, output.str(), output.pcount());

          delete []output.str();
        }
	    }
      else if (0 == strcasecmp("stack-trace-level", paramName))
	    {
       //set  set-trace-level enable/disable sccp   trcMask
       //word paramName       argv1          argv2   argv3
        ostrstream output;
        bool lnkIdFound=false;
        long lTrcMask = 1;
        char lnkName[20] = {'\0',};
        char* argv2 = strtok_r(NULL, separator, &brkt); 
        char* argv3 = strtok_r(NULL, separator, &brkt);

       
        if(argv1 && (0==strcasecmp("ENABLE",argv1) || 0==strcasecmp("DISABLE",argv1)) 
            && validateLayer(argv2)) 
        {
            if((0==strcasecmp("M3UA",argv2)) || (0==strcasecmp("SCCP",argv2)) ) { 
              if(NULL==argv3)
              return false;
              else
              lTrcMask = strtol(argv3, NULL, 10);
            }
            else if(0==strcasecmp("MTP3",argv2)) {
              if(NULL==argv3)
                return false;
              else{
                strcpy(lnkName, argv3);
                LnkSeq* lnkList = INGwSmBlkConfig::getInstance().getLinkList();
                LnkSeq::iterator it;
                for( it=lnkList->begin(); it != lnkList->end(); ++it)
                {
                  char linkName[20] ;
                  strcpy(linkName, (const char *)((*it).lnkName));
                  if(strcmp(linkName, lnkName)) {
                    lTrcMask = (*it).mtp3LsapId;
                  }
                }
                if( it == lnkList->end())
                {
                  output<<endl<<"Invalid Link name"<<endl;
                  lnkIdFound = false;
                }
              }
            }

            int lCurrRole = INGwTcapProvider::getInstance().myRole();
            
            if(lCurrRole == TCAP_PRIMARY)
            {
              char* largv1 = new char[strlen(argv1)+1];
              strcpy(largv1,argv1);

              char* largv2 =  new char[strlen(argv2)+1];
              strcpy(largv2,argv2);

              logger.logMsg(TRACE_FLAG,0,"[stack-trace-level]command <%s> layer <%s> trcMask <%d>",largv1,largv2,lTrcMask); 

              if(0==strcasecmp("M3UA",argv2)){
                if( lTrcMask>0 && lTrcMask < 8) {
                  setTraceLevel(largv1,largv2,lTrcMask);
                }
                else{
	   	          logger.logMsg(ERROR_FLAG, 0, "INVALID trace level <%d>",
                  lTrcMask);
                  output<<endl<<"Invalid trace level"<<endl; 
                }
              }
              else if(0==strcasecmp("SCCP",argv2)){
                if( lTrcMask>0 && lTrcMask < 3) {
                  setTraceLevel(largv1,largv2,lTrcMask);
                }
                else{
	   	          logger.logMsg(ERROR_FLAG, 0, "INVALID trace level <%d>",
                  lTrcMask);
                  output<<endl<<"Invalid trace level"<<endl; 
                }
              }
              else if(0==strcasecmp("MTP3",argv2)){
                if(lnkIdFound == false){
	   	            logger.logMsg(ERROR_FLAG, 0, "INVALID Link Name <%d>", argv3);
                  output<<endl<<"Invalid link name"<<endl; 
                }
                else
                  setTraceLevel(largv1,largv2,lTrcMask);
              }
              else
                setTraceLevel(largv1,largv2,lTrcMask);
            }
            else{
              output<<endl<<"Can execute this command only on primary node!!"<<endl;
            }
             *apcOutput = BP_NEW_CHAR(output.pcount() + 1);
             (*apcOutput)[output.pcount()] = '\0';

             strncpy(*apcOutput, output.str(), output.pcount());

             delete []output.str();
        } 
        else {
           output << endl<<"Invalid Input!!!"<<endl;
          *apcOutput = BP_NEW_CHAR(output.pcount() + 1);
          (*apcOutput)[output.pcount()] = '\0';

          strncpy(*apcOutput, output.str(), output.pcount());

          delete []output.str();
        }
	    }
      else if(0 == strcasecmp("sip-debug-level", paramName)) 
      {
         long val = strtol(argv1, NULL, 10);
				 INGwIfrPrParamRepository::getInstance().setMsgDebugLevel((int)val);

				 INGwSpSipProvider::getInstance().configure(
																			      ingwSIP_STACK_DEBUG_LEVEL,
																			      argv1, 
																			      INGwIfrUtlConfigurable::CONFIG_OP_TYPE_REPLACE);

      }
      else if(0 == strcasecmp("codec-debug-level", paramName)) 
      {
         long val = strtol(argv1, NULL, 10);
         ostrstream output;
         if(val >= 0 && val < 4) {
           INGwTcapMsgLogger::getInstance().setLoggingLevel((int)val);
           output << endl <<"Tcap codec debug level <" 
                  << INGwTcapMsgLogger::getInstance().getLoggingLevel() <<">"
                  << endl;
         }
         else {
           output << endl <<"Invalid Argument"
                  << endl;
         }
         *apcOutput = BP_NEW_CHAR(output.pcount() + 1);
         (*apcOutput)[output.pcount()] = '\0';

         strncpy(*apcOutput, output.str(), output.pcount());

         delete []output.str();
      }
//
      else if(0 == strcasecmp("failure-point-mask", paramName)) 
      {
         long val = strtol(argv1, NULL, 10);
         ostrstream output;
         if(val >= 0) {
           INGwTcapProvider::getInstance().setFpMask((int)val);
           output << endl <<"Failure Point Mask <" 
                  << INGwTcapProvider::getInstance().getFpMask() <<">"
                  << endl;
         }
         else {
           output << endl <<"Invalid Argument"
                  << endl;
         }
         *apcOutput = BP_NEW_CHAR(output.pcount() + 1);
         (*apcOutput)[output.pcount()] = '\0';

         strncpy(*apcOutput, output.str(), output.pcount());

         delete []output.str();
      }
    
//
      else if(0 == strcasecmp("pdu-debug-level", paramName)) 
      {
         long val = strtol(argv1, NULL, 10);
				 INGwTcapProvider::getInstance().configure(
																			      ingwPDU_LOG_LEVEL,
																			      argv1, 
																			      INGwIfrUtlConfigurable::CONFIG_OP_TYPE_REPLACE);
      }
      else if(0 == strcasecmp("tcap-client-dbg-level", paramName)) 
      {
				 INGwTcapProvider::getInstance().configure(
																						"tcapClientDbgLvl",
																			      argv1, 
																			      INGwIfrUtlConfigurable::CONFIG_OP_TYPE_REPLACE);
      }
      else if(0 == strcasecmp("loopback", paramName)) 
      {
				 long val = 0;
				 if(argv1 != NULL)
         	val = strtol(argv1, NULL, 10);

				if(val>0)
					configLoopBack = true;
				else 
					configLoopBack = false;
      }
#ifdef PK_UNPK_DEBUG_FLAG
      else if(0 == strcasecmp("mBuffStore", paramName)) 
      {
         long val = 0;
         if(argv1 != NULL)
           initPkUnpkStore();
           g_EnableMBuffStore = strtol(argv1, NULL, 10);

        logger.logINGwMsg(false, ALWAYS_FLAG,0,"set mBuffStore <%d>",
                          g_EnableMBuffStore); 
      }
#endif
      else if(0 == strcasecmp("enableDiagLogs", paramName)) 
      {
         long val = 0;
         if(argv1 != NULL) {
           val = strtol(argv1, NULL, 10);
           if(val) {
             g_enableDiagLogs = true;
           }
         }
         
        logger.logINGwMsg(false, ALWAYS_FLAG,0,"set enableDiag <%d>",
                          g_enableDiagLogs); 
      }
      
	  }
   else if(0 == strcasecmp("get", word)) 
   {
			char* argv1 = strtok_r(NULL, separator, &brkt);
      if(NULL != argv1) 
      {
         isWriteInFile = true;
				 lFileName = string(argv1);
      }
      if(0 == strcasecmp("sip-debug-level", paramName)) 
      {
         *apcOutput = new char[(maxRetMsgSize + 1)];
         (*apcOutput)[maxRetMsgSize] = '\0';

         snprintf(*apcOutput, maxRetMsgSize, "Msg Debug level is [%d]\n", 
                  INGwIfrPrParamRepository::getInstance().getMsgDebugLevel());
      }
//get
      else if(0 == strcasecmp("INGw-SG-Role", paramName)) 
      {
         int ingwRole = INGwTcapProvider::getInstance().myRole();
         std::string ingwRoleStr =
              (ingwRole == 0x01)?"INGw_ACTIVE":(ingwRole == 0x00)?"INGw_STANDBY":"INVALID";
         std::string sgRoleStr = INGwTcapProvider::getInstance().getAinSilTxRef().
                                         getSelfSgRole().c_str();

         *apcOutput = new char[(maxRetMsgSize + 1)];
         (*apcOutput)[maxRetMsgSize] = '\0';

         snprintf(*apcOutput, maxRetMsgSize, "INGw Role [%s] and SG Role [%s]\n", 
             ingwRoleStr.c_str(), sgRoleStr.c_str());
      }

//get
      else if(0 == strcasecmp("codec-debug-level", paramName)) 
      {
         *apcOutput = new char[(maxRetMsgSize + 1)];
         (*apcOutput)[maxRetMsgSize] = '\0';

         snprintf(*apcOutput, maxRetMsgSize, "codec-debug-level is [%d]\n", 
                   INGwTcapMsgLogger::getInstance().getLoggingLevel());
      }
      else if(0 == strcasecmp("bitArrStatus", paramName)) 
      {
         TcapMessage::getAllActiveDlgs();
      }

      else if(0 == strcasecmp("stats-mask", paramName)) 
      {
         ostrstream output;
         INGwTcapProvider::getInstance().getSmWrapperPtr()->getStatsMask(output);

         *apcOutput = new char[(output.pcount() + 1)];
         (*apcOutput)[output.pcount()] = '\0';

         strncpy(*apcOutput, output.str(), output.pcount());

         delete []output.str();
      }

//get
      else if(0 == strcasecmp("tcapSessionMap", paramName)) 
      {
        INGwIfrMgrWorkUnit* lWorkUnit = new INGwIfrMgrWorkUnit;
        lWorkUnit->meWorkType = INGwIfrMgrWorkUnit::DUMP_TCAP_SESSION_MAP;
        lWorkUnit->mpcCallId  = NULL;
        lWorkUnit->mpWorkerClbk = INGwTcapProvider::getInstance().
                                                           getTcapFtHandler();
        lWorkUnit->mpMsg = NULL;
        lWorkUnit->mpContextData = NULL;
        INGwIfrMgrThreadMgr::getInstance().postMsgForHK(lWorkUnit);
      }
      else if(0 == strcasecmp("failure-point-mask", paramName)) 
      {
         *apcOutput = new char[(maxRetMsgSize + 1)];
         (*apcOutput)[maxRetMsgSize] = '\0';

         snprintf(*apcOutput, maxRetMsgSize, "failure point mask is [%d]\n", 
                  INGwTcapProvider::getInstance().getFpMask());
      }
//
      else if(0 == strcasecmp("detail-count", paramName)) 
      {
         ostrstream output;

         output << "Statistics \n";
				 output << StatCollector::getInstance().toLog(1);

         *apcOutput = new char[(output.pcount() + 1)];
         (*apcOutput)[output.pcount()] = '\0';

         strncpy(*apcOutput, output.str(), output.pcount());

         delete []output.str();
      }
      else if(0 == strcasecmp("registeredSAS", paramName)) 
      {
				 std::string lLogStr = 
						INGwSpSipProvider::getInstance().getCallController()->toLogEpMap();


         *apcOutput = new char[(lLogStr.length() + 1)];
         (*apcOutput)[lLogStr.length()] = '\0';

         strncpy(*apcOutput, lLogStr.c_str(), lLogStr.length());
      }
			else if(0 == strcasecmp("loadDistributorInfo", paramName))
			{
				 std::string lLogStr = INGwTcapProvider::getInstance().getLoadDistDebugInfo();

				 if (lLogStr.empty()) {
         		*apcOutput = new char[6];
         		(*apcOutput)[5] = '\0';
         		strncpy(*apcOutput, "EMPTY", 5);
				 }
				 else {
					*apcOutput = new char[(lLogStr.length() + 1)];
					(*apcOutput)[lLogStr.length()] = '\0';

					strncpy(*apcOutput, lLogStr.c_str(), lLogStr.length());
				 }
			}
      else if(0 == strcasecmp("all-param-val", paramName)) 
      {
				 std::string lLogStr = 
						INGwIfrPrParamRepository::getInstance().toLog();

         *apcOutput = new char[(lLogStr.length() + 1)];
         (*apcOutput)[lLogStr.length()] = '\0';

         strncpy(*apcOutput, lLogStr.c_str(), lLogStr.length());
      }
      else if(0 == strcasecmp("sip-msg-stats", paramName)) 
      {
				 std::string lLogStr = 
						INGwSpSipProvider::getInstance().toLogSipStats();

         char lpcTime[64];
	       memset(lpcTime, 0, sizeof(lpcTime));
         lpcTime[0] = '1';
         g_getCurrentTime(lpcTime);
         logger.logINGwMsg(false,TRACE_FLAG,0,"sip-msg-stats TS <%s>",lpcTime); 
         
         int liLen = 0;
         *apcOutput = new char[(lLogStr.length() + 64 + 1)];
         (*apcOutput)[lLogStr.length()+64] = '\0';
         liLen += sprintf((*apcOutput),"%s\n%s",lpcTime, lLogStr.c_str());
         logger.logINGwMsg(false,TRACE_FLAG,0,
					"+rem+ lpcTimeLen:%d, LogStr:%d, sip-msg-stats [%s]", strlen(lpcTime), lLogStr.length(), lLogStr.c_str());

         //strncpy(((*apcOutput)+ liLen), lLogStr.c_str(), lLogStr.length());
      }
      else if(0 == strcasecmp("full-stats", paramName)) 
      {
        logger.logINGwMsg(false,TRACE_FLAG,0,"[full-stats]command "); 
        isWriteInFile = false; //StatMgr will write into the file
        INGwIfrSmStatMgr& lrStatMgr = INGwIfrSmStatMgr::instance();

        if(lFileName.empty())
          lFileName="Stats.txt"; 

        lrStatMgr.startStatsDisplayProcessing(lFileName);

      }
      else if(0 == strcasecmp("tcap-msg-stats", paramName)) 
      {
        ostrstream output;
        INGwTcapProvider::getInstance().getStatistics(output, 1);

         *apcOutput = new char[(output.pcount() + 1)];
         (*apcOutput)[output.pcount()] = '\0';

         strncpy(*apcOutput, output.str(), output.pcount());

         delete []output.str();
      }
      else if(0 == strcasecmp("ft-msg-stats", paramName)) 
			{
				 std::string lLogStr = 
						INGwIfrMgrManager::getInstance().toLogFtStats();

         *apcOutput = new char[(lLogStr.length() + 1)];
         (*apcOutput)[lLogStr.length()] = '\0';

         strncpy(*apcOutput, lLogStr.c_str(), lLogStr.length());
			}
      else if(0 == strcasecmp("role-detail", paramName)) 
      {

         std::string sgRoleStr = INGwTcapProvider::getInstance().getAinSilTxRef().
                                  getSelfSgRole().c_str();

         ostrstream output;

         output << "Components \n";
				 output << " Self Id : " << INGwIfrPrParamRepository::getInstance().getSelfIdStr() <<endl;
				 output << " Peer Id : " << INGwIfrPrParamRepository::getInstance().getPeerIdStr() <<endl;
         output << "---- Role Details ---- \n";
				 output << INGwIfrMgrRoleMgr::getInstance().toLog() << endl;
				 output << " SG Role : [ " << sgRoleStr.c_str() << " ]" << endl;

         *apcOutput = new char[(output.pcount() + 1)];
         (*apcOutput)[output.pcount()] = '\0';

         strncpy(*apcOutput, output.str(), output.pcount());

         delete []output.str();

      }
      else if(0 == strcasecmp("provider-stats", paramName)) 
      {
         ostrstream output;
				 INGwIfrMgrManager::getInstance().getStatistics(output, 1,false);

         *apcOutput = new char[(output.pcount() + 1)];
         (*apcOutput)[output.pcount()] = '\0';

         strncpy(*apcOutput, output.str(), output.pcount());

         delete []output.str();
      }
      else if(0 == strcasecmp("stack-config", paramName)) 
      {
        INGwSmBlkConfig::getInstance().log();
      }
      else if(0 == strcasecmp("stk-logmask-inc", paramName)) 
      {
        ostrstream output;
        INGwTcapProvider::getInstance().getSmWrapperPtr()->getStkLogMask(output);

         *apcOutput = new char[(output.pcount() + 1)];
         (*apcOutput)[output.pcount()] = '\0';

         strncpy(*apcOutput, output.str(), output.pcount());

         delete []output.str();
      }
      else if(0 == strcasecmp("stack-debug-level", paramName)) 
      {
        ostrstream output;
        INGwTcapProvider::getInstance().getSmWrapperPtr()->getStackDbgMask(output);

         *apcOutput = new char[(output.pcount() + 1)];
         (*apcOutput)[output.pcount()] = '\0';

         strncpy(*apcOutput, output.str(), output.pcount());

         delete []output.str();
      }

#ifdef PK_UNPK_DEBUG_FLAG
      else if(0 == strcasecmp("mBuffStoreDump", paramName)) 
      {
        isWriteInFile = false;
        int nmbDlgsPerFile = 0;
        if (argv1) {
          nmbDlgsPerFile = atoi(argv1);
        }
        logger.logINGwMsg(false, ALWAYS_FLAG,0,
                          "get mBuffStoreDump nmbDlgsPerFile<%d>", nmbDlgsPerFile);
        INGwSilTx::instance().printMBufStore(nmbDlgsPerFile);

        ostrstream output;
        output << "mBuffStore dumped at path : /tmp/DlgDumps" << endl;
        *apcOutput = new char[(output.pcount() + 1)];
         (*apcOutput)[output.pcount()] = '\0';

         strncpy(*apcOutput, output.str(), output.pcount());

         delete []output.str();
      }
      else if(0 == strcasecmp("mBuffStore", paramName)) 
      {
        ostrstream output;
        output << "mBuffStore : " << g_EnableMBuffStore << endl;
        logger.logINGwMsg(false, ALWAYS_FLAG,0,
                          "get mBuffStore <%d>", g_EnableMBuffStore);
        *apcOutput = new char[(output.pcount() + 1)];
         (*apcOutput)[output.pcount()] = '\0';

         strncpy(*apcOutput, output.str(), output.pcount());

         delete []output.str();
      }
#endif
      else if(0 == strcasecmp("enableDiagLogs", paramName)) 
      {
        ostrstream output;
        output << "enableDiag : " << g_enableDiagLogs << endl;

         *apcOutput = new char[(output.pcount() + 1)];
         (*apcOutput)[output.pcount()] = '\0';

         strncpy(*apcOutput, output.str(), output.pcount());

         delete []output.str();
        
      }
			else {
				isParamValid = false;
			}
   }
	 else if (0 == strcasecmp("runcall", word))
	 {
		 if(0 == strcasecmp("count", paramName))
		 {
			 char* argv1 = strtok_r(NULL, separator, &brkt);
			 string count = "1";
			 string rate = "1";
       string ip = "192.168.8.71";

			 if(NULL == argv1)
			 {
				 isParamValid = false;
			 }
			 else
			 {
				 count = argv1;
			 }
			 char* argv2 = strtok_r(NULL, separator, &brkt);
       if(NULL == argv2)
       {
         isParamValid = false;
       }
       else
       {
         rate = argv2;
       }

       char* argv3 = strtok_r(NULL, separator, &brkt);
       if(NULL == argv3)
       {
         isParamValid = false;
       }
       else
       {
         ip = argv3;
       }

       runCall(count, rate, ip);
		 }
	 }
	 else if (0 == strcasecmp("doAudit", word))
	 {
#ifdef INC_DLG_AUDIT
     INGwTcapProvider::getInstance().auditTcapDlg();
#endif

	 }
#ifdef CCPU_STUB
	 else if (0 == strcasecmp("testCall", word))
	 {
     int msgNum = -1;
     if (isParamValid) {
       msgNum = atoi(paramName);
       testCall(msgNum);
     }
	 }
   else if(0 == strcasecmp("clear-ft-msg-store",word) &&
          (0 == strcasecmp("@99",paramName)))
   {
      INGwIfrMgrWorkUnit* lWorkUnit = new INGwIfrMgrWorkUnit;
      lWorkUnit->meWorkType = INGwIfrMgrWorkUnit::CLEAR_TCAP_SESSION_MAP;
      lWorkUnit->mpcCallId  = NULL;
      lWorkUnit->mpWorkerClbk = INGwTcapProvider::getInstance().
                                                         getTcapFtHandler();
      lWorkUnit->mpMsg = NULL;
      lWorkUnit->mpContextData = NULL;
      INGwIfrMgrThreadMgr::getInstance().postMsgForHK(lWorkUnit); 
   }

	 else if(0 == strcasecmp("bind", word)) 
	 {
      char* argv1 = strtok_r(NULL, separator, &brkt);
			if(paramName == NULL || argv1 == NULL) 
			{
				isParamValid = false;
			}
			else {
				U32 pc = atoi(paramName);
				U8 ssn = atoi(argv1);
				INGwTcapProvider::getInstance().registerWithStack(pc, ssn, TCAP_RE_REGISTER);
			}
	 }
	 else if (0 == strcasecmp("runLoad", word))
	 {
     int numOfCall = -1;
     if (isParamValid) {
       numOfCall = atoi(paramName);
       runLoad(numOfCall);
     }
	 }
#endif

#ifdef INC_ASP_SNDDAUD
	 else if (0 == strcasecmp("sendDaud", word))
	 {
     U32 psId = 0;
     if (isParamValid) {
       psId = (U32)atoi(paramName);
       INGwTcapProvider::getInstance().getSmWrapperPtr()->
                  getDistInst()->getAlmHdlr()->sendDaud(psId);
     }
	 }
#endif

   else
   {
      isParamValid = false;
   }

 	if((word == NULL) || (false == isParamValid) ||
      (strcasecmp("help", word) == 0))
   {
      ostrstream output;

      output << "CommandList:" << endl
             << "\t help" << endl
             << "\t get registeredSAS      [Optional Output File ]"<< endl
             << "\t get loadDistributorInfo [Optional Output File ]"<< endl
             << "\t get detail-count       [Optional Output File ]"<< endl
             << "\t\t <Queue Details>"<< endl
             << "\t get sip-debug-level    [Optional Output File ]"<< endl
             << "\t get all-param-val      [Optional Output File ]"<< endl
             << "\t get provider-stats     [Optional Output File ]"<< endl
             << "\t get stack-config       " << endl
             << "\t get role-detail        [Optional Output File ]"<< endl
             << "\t get sip-msg-stats      [Optional Output File ]"<< endl
             << "\t get tcap-msg-stats     [Optional Output File ]"<< endl
             << "\t get ft-msg-stats     	 [Optional Output File ]"<< endl
             << "\t get stk-logmask-inc    "<< endl
             << "\t get stack-debug-level    "<< endl
             << "\t get full-stats         <Output File >         "<< endl
#ifdef PK_UNPK_DEBUG_FLAG
             << "\t get mBuffStoreDump         [NmbDlgsPerFile]       "<< endl
             << "\t get mBuffStore                            "<< endl
#endif
             << "\t get enableDiagLogs       " << endl
#ifdef CCPU_STUB
             << "\t get codec-debug-level                         "<< endl
             << "\t get bitArrStatus                              "<< endl
             << "\t get stats-mask                                "<< endl
             //<< "\t get failure-point-mask                        "<< endl

             //<< "\t testCall                <<1|2|3|...>>         "<< endl
             //<< "\t set Ss7SigCfg                                 "<< endl
             << "\t set stk-logmask-inc    <MSG_FLOW|RY|SG|SH|MR|MT|SS|SM|CM|\n\t\t\t\t"
                    " TCAP|SCCP|PSF_TCAP|PSF_SCCP|MTP3|LDF_MTP3|\n\t\t\t\t"
                    " PSF_MTP3|MTP2|M3UA|LDF_M3UA|PSF_M3UA|SCTP|\n\t\t\t\t"
                    " TUCL|MEM [Count]|RESET>    "<< endl
             << "\t set stats-mask        <BitMask>                                "<< endl
             //<< "\t set stack-debug-level <ENABLE/DISABLE> <LAYER> <1(SSI),\n\t\t\t\t"
             //       "2(SSI,LM),3(SSI,LM,UI),4(SSI,LM,UI,LI),\n\t\t\t\t"
             //       "5(SSI,LM,UI,LI,PI),6(SSI,LM,UI,LI,PI,PLI),\n\t\t\t\t"
             //       "7(SSI,LM,UI,LI,PI,PLI,LYR)>"<< endl
             << "\t set stack-debug-level <ENABLE/DISABLE> <TCAP|PSF_TCAP|SCCP|\n\t\t\t\t"
                    "PSF_SCCP|M3UA|PSF_M3UA|LDF_M3UA|MTP3|PSF_MTP3|\n\t\t\t\t"
                    "LDF_M3UA|MTP2|RELAY|SG|MR|SH|ALL> <0-7>    "<< endl
             //<< "\t set stack-trace-level <ENABLE/DISABLE> <LAYER> <M3UA->1(LIT_TRC_SSNM),\n\t\t\t\t"
             //       "2(1+LIT_TRC_ASPSM),3(2+LIT_TRC_ASPTM),4(3+LIT_TRC_M3UA_XFER),\n\t\t\t\t"
             //       "5(4+LIT_TRC_MGMT),6(5+LIT_TRC_RKM),7(LIT_TRC_ALL) --  \n\t\t\t\t"
             //       "SCCP->1(MTP3), 2(M3UA) --  \n\t\t\t\t"
             //       "MTP3-><Link Name>>"<< endl
             << "\t set codec-debug-level   <<0|1|2|3>>           "<< endl
             //<< "\t set failure-point-mask  <<1|2|3|4>>           "<< endl
             //<< "\t runLoad                 <<Number Of Calls>> "<< endl
             //<< "\t bind  <<PointCode  SSN>>                    "<< endl
             //<< "\t get tcapSessionMap                            "<< endl
             //<< "\t clear-ft-msg-store      <<0 | 1>>             "<< endl
#endif
             << "\t set sip-debug-level <<0 | 1 >>" << endl
             //<< "\t set pdu-debug-level <<0 | 1 >>" << endl
#ifdef PK_UNPK_DEBUG_FLAG
             << "\t set mBuffStore <<0 | 1 | 2>>" << endl
#endif
             << "\t set enableDiagLogs <<0 | 1>>" << endl
#ifdef INC_ASP_SNDDAUD
             << "\t sendDaud <<PsId>>" << endl
#endif
             << "\t doAudit                      " << endl;

      *apcOutput = BP_NEW_CHAR(output.pcount() + 1);
      (*apcOutput)[output.pcount()] = '\0';

      strncpy(*apcOutput, output.str(), output.pcount());

      delete []output.str();
   }
	 else if (isWriteInFile == true )
	 {
     ostrstream output;


		 char* data = *apcOutput;
		 fstream file(lFileName.c_str(), ios::out);
		 if(true != file.is_open())
		 {
			 logger.logMsg(ERROR_FLAG, 0, "Error opening file [%s]",
			               lFileName.c_str());
       output << " Error opening file : " << lFileName << endl;
	   }
		 else
		 {
			 file << data;
			 file.flush();
			 file.close();

       output << " Output written to " << lFileName << endl;
		 }
		 delete [] data;
		 *apcOutput = NULL;

     *apcOutput = BP_NEW_CHAR(output.pcount() + 1);
     (*apcOutput)[output.pcount()] = '\0';

     strncpy(*apcOutput, output.str(), output.pcount());

     delete []output.str();
	 }

   if(NULL != *apcOutput) 
   {
      size = strlen(*apcOutput);
   }
   else 
   {
      size = 0;
   }

   delete [] command;

   return retResult;
}

//S16 gttHexAddrToBcd(LngAddrs *inpBuf,ShrtAddrs *bcdBuf)
//{
//   U8 c;
//   U8 i;
//   U8 *src;
//   U8 *dst;
//   S16 d;
// 
//   src = inpBuf->strg;
//   dst = bcdBuf->strg;
//
//   /* sanity check */
//   if (inpBuf->length > LNGADRLEN)
//      RETVALUE(RFAILED);
//   for (i = inpBuf->length; i; i--)
//   {
//      d = 0;
//      if (!cmIsANumber(&d, (c = *src++), (S16) BASE16))
//         RETVALUE(RFAILED);
//      *dst = (U8) d;    /* The first digit */
//      i--;
//      if (!i)
//         break;
//      if (!cmIsANumber(&d, c = *src++, (S16) BASE16))
//         RETVALUE(RFAILED);
//      *dst++ |= (U8) (d << 4);    /* The second digit */
//   }
//   bcdBuf->length = ( (inpBuf->length % 2) ?
//      (U8)((U8)(inpBuf->length + 1)/2) : (U8)(inpBuf->length/2));
//   RETVALUE(ROK);
//} /* gttHexAddrToBcd */

void myStringToByteArray(std::string str, unsigned char* array, int& size)
{
  
  //int length = str.length();
  //// make sure the input string has an even digit numbers
  //
  //if(length%2 == 1)
  //{
  //	str = "0" + str;
  //	length++;
  //}
  //
  //// allocate memory for the output array
  //(*array) = new unsigned char[length/2];
  //size = length/2;
	std::stringstream sstr(str);
	for(int i=0; i < size; i++)
	{
		char ch1, ch2;
		sstr >> ch1 >> ch2;
		int dig1, dig2;
		if(isdigit(ch1)) dig1 = ch1 - '0';
		else if(ch1>='A' && ch1<='F') dig1 = ch1 - 'A' + 10;
		else if(ch1>='a' && ch1<='f') dig1 = ch1 - 'a' + 10;
		if(isdigit(ch2)) dig2 = ch2 - '0';
		else if(ch2>='A' && ch2<='F') dig2 = ch2 - 'A' + 10;
		else if(ch2>='a' && ch2<='f') dig2 = ch2 - 'a' + 10;
		(array)[i] = dig1*16 + dig2;
	}
}

/**
* Constructor
*/
INGwIfrTlIfTelnetIntf::INGwIfrTlIfTelnetIntf() : INGwIfrTlIfTCPServer(NULL, true, false)
{ 
   LogINGwTrace(false, 0, "IN INGwIfrTlIfTelnetIntf()");
   LogINGwTrace(false, 0, "OUT INGwIfrTlIfTelnetIntf()");
}

/**
* Destructor
*/
INGwIfrTlIfTelnetIntf::~INGwIfrTlIfTelnetIntf()
{ 
   LogINGwTrace(false, 0, "IN ~INGwIfrTlIfTelnetIntf()");
   LogINGwTrace(false, 0, "OUT ~INGwIfrTlIfTelnetIntf()");
}

/**
* Description : 
*
* @param <apcSelfAddr> -
* @param <aiPort     > -
* @param <apFunc     > - 
* @param <abStartListener> - 
* @param <aiBacklog> -
*
* @return <bool> - 
*
*/
bool 
INGwIfrTlIfTelnetIntf::initialize(const char* apcSelfAddr, int aiPort, 
                                 bool abStartListener, int aiBacklog) 
{ 
   LogINGwTrace(false, 0, "IN initialize");

   bool ret = INGwIfrTlIfTCPServer::initialize(apcSelfAddr, aiPort, cliFunc, 
                                    abStartListener, aiBacklog);
   LogINGwTrace(false, 0, "OUT initialize");

   return ret;
}

/**
* Description : 
*
* @param <finalname> -
*
* @return <int> - 
*
*/
int 
setOutputFile(const char *finalname)
{
   logger.logMsg(ALWAYS_FLAG, 0, "Changing the outputfile to [%s]", finalname);

   int fd = -1;

   if((fd = open(finalname, O_WRONLY | O_CREAT | O_TRUNC, 0644)) == -1)
   {
      logger.logMsg(ALWAYS_FLAG, 0, "Error opening outputfile [%s] [%s]",
                    finalname, strerror(errno));
      return 1;
   }

   if(dup2(fd, 1) == -1)
   {
      logger.logMsg(ALWAYS_FLAG, 0, "Error changing outputstr [%s] [%s]",
                    							   finalname, strerror(errno));
      close(fd);
      return 1;
   }

   if(dup2(fd, 2) == -1)
   {
      logger.logMsg(ALWAYS_FLAG, 0, "Error changing errorstr [%s] [%s]",
                    finalname, strerror(errno));
      close(fd);
      return 1;
   }

   logger.logMsg(ALWAYS_FLAG, 0, "Successfully modified out & err to [%s]",
                 finalname, strerror(errno));
   close(fd);
   return 0;
}

int runCall(string& p_Count, string& rate, string& ip)
{
  logger.logMsg(TRACE_FLAG, 0, "runCall call count is [%s] for [%s]",
                p_Count.c_str(), ip.c_str());

  int lCount = atoi(p_Count.c_str());

  for(int i = 0; i < lCount; i++)
  {
    g_TransitObj*  p_transitObj = new g_TransitObj;
    p_transitObj->m_sasIp = ip;
  	INGwIfrMgrWorkUnit* lWorkUnit =  new INGwIfrMgrWorkUnit;
  	lWorkUnit->meWorkType = INGwIfrMgrWorkUnit::TEMP_SEND_NOTIFY;
  	lWorkUnit->mpWorkerClbk = INGwSpSipProvider::getInstance().getCallController();
    lWorkUnit->mpContextData = (void*)p_transitObj;
	  INGwIfrMgrThreadMgr::getInstance().postMsg(lWorkUnit);

    if((i % 200 ) == 0)
    {
      sleep(1);
    }
  }
  return 0;
}
int createSccpAddr(SpAddr* apSpAddr,
                   bool  pres,
                   bool  ssfpres,
                   bool  niInd,
                   U8    rtgInd,
                   bool  ssnInd,
                   bool  pcInd,
                   Dpc   pc,
                   U8    ssn,
                   Swtch sw,
                   GlbTi gt,
                   U8 spHdrOpt = 0x1)
{
  if(NULL == apSpAddr)
  {
    return 1;
  }
    
    apSpAddr->pres    = pres;
    apSpAddr->ssfPres = ssfpres;
    apSpAddr->ssf     = 0;
    apSpAddr->niInd   = niInd;// 0
    apSpAddr->rtgInd  = rtgInd;
    apSpAddr->pcInd   = pcInd;
    apSpAddr->ssnInd  = ssnInd;
    apSpAddr->sw      = sw;
//#ifdef CMSS7_NO_SP_PC
    apSpAddr->spHdrOpt= spHdrOpt; 
    apSpAddr->ssn     = ssn;
//#endif /*CMSS7_NO_SP_PC*/
    if(INC_RTE_SSN == rtgInd)
    {
      apSpAddr->pc      = pc;
    }
    else if(INC_RTE_GT == rtgInd)
    {
      memcpy(&(apSpAddr->gt),&gt,sizeof(GlbTi));
    }
    else
    { 
      return 1; 
    }
  return 0; 
}

int createDlgEv(StDlgEv* apStDlgEv,    /*StDlgEv*/
                bool     pres,         /*pres*/
                U8       pStDlgType,   /*StDlgType*/
                StStr    papConName,   /*apConName*/
                bool     presPres,     /*resPres*/
                U8       presult,      /*result*/
                U8       presSrc,      /*resSrc*/
                U8       presReason,   /*resReason*/
                U8       pabrtSrc      /*abrtSrc*/
               )
{
  if(NULL != apStDlgEv)
  {
    apStDlgEv->pres      = pres;
    apStDlgEv->stDlgType = pStDlgType;
    apStDlgEv->apConName = papConName;
    memcpy(&(apStDlgEv->apConName),&papConName,sizeof(StStr));
    apStDlgEv->resPres   = presPres; 
    apStDlgEv->result    = presult;
    apStDlgEv->resSrc    = presSrc;
    apStDlgEv->resReason = presReason;
    apStDlgEv->abrtSrc   = pabrtSrc;
    return 0;
  }
  return 1;
}
int createcompEv(StComps*  compEv,
                 U8        pStCompType,
                 StOctet   pStInvokeId,
                 U16       pStInvokeTimer,
                 U8        pOpClass,
                 StOctet   pStLinkedId,
                 StStr     pStOpCode,
                 U8        pStOpCodeFlg,
                 StStr     pStErrorCode,
                 U8        pStErrorCodeFlg,
                 StStr     pStProbCode,
                 U8        pStProbCodeFlg, 
                 bool      pCancelFlg,
                 bool      pStLastCmp)                 
{
  if(NULL != compEv)
  {
    compEv->stCompType      = pStCompType;
    memcpy(&(compEv->stInvokeId),&pStInvokeId,sizeof(StInvokeId));
    compEv->stInvokeTimer   = pStInvokeTimer;
    compEv->opClass         = pOpClass;
    memcpy(&(compEv->stLinkedId),&pStLinkedId,sizeof(StInvokeId));
    compEv->stOpCode        = pStOpCode;
    memcpy(&(compEv->stOpCode),&pStOpCode,sizeof(StStr));
    compEv->stOpCodeFlg     = pStOpCodeFlg;
    compEv->stErrorCode     = pStErrorCode;
    memcpy(&(compEv->stErrorCode),&pStErrorCode,sizeof(StStr));
    compEv->stErrorCodeFlg  = pStErrorCodeFlg; 
    compEv->stProbCode      = pStProbCode;
    memcpy(&(compEv->stProbCode),&pStProbCode,sizeof(StStr));
    compEv->stProbCodeFlg   = pStProbCodeFlg;
    compEv->cancelFlg       = pCancelFlg;
    compEv->stLastCmp       = pStLastCmp;
    return 0;
  } 
  return 1;
}
                
int testCall(int p_msgNum, int dlgId)
{
  logger.logMsg(TRACE_FLAG, 0, "testCall MsgNum[%d]",
                p_msgNum);
  
  Pst            *pst;           /* post structure */
  SuId            suId;
  U8              msgType;
  StDlgId         suDlgId;
  StDlgId         spDlgId; 
  SpAddr         *dstAddr = NULL;
  SpAddr         *srcAddr = NULL;
  Bool            compsPres;
  StOctet        pAbtCause;
  StQosSet       qosSet;
  Dpc             opc;
  StDlgEv        *dlgEv;
  Buffer         *uiBuf;
  U8              cause; 

  int liMsgNum = p_msgNum;
  
  if (liMsgNum == 1) {
   /*Begin Indication*/ 
    logger.logMsg(TRACE_FLAG, 0,"Creating Begin Indication");
    pst =  new Pst;
    memset(pst,0,sizeof(Pst));
    suId = 0;
    msgType = INC_BEGIN;
    suDlgId = dlgId;
    spDlgId = dlgId;
    dstAddr = (SpAddr*)malloc(sizeof(SpAddr));
    srcAddr = (SpAddr*)malloc(sizeof(SpAddr));
    GlbTi gt; 
    createSccpAddr(dstAddr,
                   true,
                   false,
                   false,
                   INC_RTE_SSN,
                   true,
                   true,
                   121,
                   146,
                   SW_JAPAN,
                   gt);

   createSccpAddr(srcAddr,
                   true,
                   false,
                   false,
                   INC_RTE_SSN,
                   true,
                   true,
                   121,
                   146,
                   SW_JAPAN,
                   gt); 

   compsPres = true;               
   memset(&pAbtCause,0,sizeof(StOctet)); 
   memset(&qosSet,0,sizeof(StQosSet));   
   opc       = 102;
   //dlgEv = new StDlgEv;
   dlgEv = (StDlgEv*)malloc(sizeof(StDlgEv)); 

   memset(dlgEv,0,sizeof(StDlgEv));
   StStr appConName;
   appConName.len = 5;
   for(int i=0;i<appConName.len;i++)
   {
     appConName.string[i] = i;
   }

   createDlgEv ( dlgEv,       /*StDlgEv*/
                false,        /*pres*/
                INC_BEGIN,    /*StDlgType*/
                appConName,   /*apConName*/
                false,        /*resPres*/
                0x00,         /*result*/
                0x00,         /*resSrc*/
                0x00,         /*resReason*/
                0x00          /*abrtSrc*/
               );
   uiBuf = NULL; 
   StDataParam    *dataParam = NULL;
   Dpc dpc = 0;
   S16 retVal = TuLiStuDatInd(pst,
                  1,
                  msgType,
                  suDlgId,
                  spDlgId, 
                  dstAddr,
                  srcAddr,
                  compsPres,
                  &pAbtCause,
                  &qosSet,
                  opc,
                  dpc,
                  dlgEv,
                  dataParam,
                  uiBuf);
   // component indication
   Buffer     *cpBuf;
   StComps    *compEv;
   S16 cCnt;
#ifdef SS_HISTOGRAM_SUPPORT
   if (SGetMsgNew (DFLT_REGION, DFLT_POOL, &cpBuf, __FILE__, __LINE__) != ROK)
#else
   if (SGetMsg (DFLT_REGION, DFLT_POOL, &cpBuf) != ROK)
#endif
   {
       printf("Cannot Allocate Message Buffer \n");
       return -1;
   }

   //INCTBD remove this
#ifdef SS_HISTOGRAM_SUPPORT
   if (SGetMsg (DFLT_REGION, DFLT_POOL, &cpBuf, __FILE__, __LINE__) != ROK)
#else
   if (SGetMsg (DFLT_REGION, DFLT_POOL, &cpBuf) != ROK)
#endif
   {
       printf("Cannot Allocate Message Buffer \n");
       return -1;
   } 
 
   char* byteBuff = getenv("INGW_IDP_PARAM_BUFFER");

   if(NULL != byteBuff){
     unsigned char* idp;
     string byteBuffStr(byteBuff);
     int size;
  	int length = byteBuffStr.length();
  	// make sure the input string has an even digit numbers
  	
  	if(length%2 == 1)
  	{
  		byteBuffStr = "0" + byteBuffStr;
  		length++;
  	}
  
  	// allocate memory for the output array
  	idp = new unsigned char[length/2];
  	size = length/2;
     myStringToByteArray(byteBuffStr, idp, size);
     SAddPstMsgMult((Data *)(idp), size, cpBuf);
     delete [] idp;
   }
   else{
     unsigned char idp[] = 

     {0x30, 0x25, 0x80, 0x01, 0x65, 0x82, 0x07, 0x03,
      0x10, 0x01, 0x00, 0x00, 0x00, 0x50, 0x83, 0x07, 0x04, 0x13, 
      0x19, 0x12, 0x00, 0x01, 0x01, 0x85, 0x01, 0x0a, 0x88, 0x01,
      0x00, 0xbb, 0x05, 0x80, 0x03, 0x80, 0x90, 0xa3, 0x9c, 0x01,
      0x02}; 
      SAddPstMsgMult((Data *)(idp), 39, cpBuf);
      
     }

   StComps* lcompEv = (StComps*)malloc(sizeof(StComps));
   memset(lcompEv,0,sizeof(StComps));
   StOctet     lStInvokeId;
   lStInvokeId.pres  = true;
   lStInvokeId.octet = 0x01;
   U16 lStInvokeTimer= 10;
   U8  opClass      = 0x01;
   StOctet  lStLinkedId;
   lStLinkedId.pres = false;
   U8 lStOpCodeFlg = 0x01;

   StStr lStOpCode;
   lStOpCode.len = 1;
   lStOpCode.string[0]= 0x00;

   U8 lStErrorCodeFlg = STU_LOCAL;

   StStr lStErrorCode;
   memset(&(lStErrorCode),0,sizeof(StStr)); 
   StStr  lStProbCode;
   U8 lStProbCodeFlg = 0x00;
   memset(&(lStProbCode),0,sizeof(StStr)); 
   bool lCancelFlg = false;
   bool lStLastCmp = true;
   createcompEv(lcompEv,
                STU_INVOKE,
                lStInvokeId,
                lStInvokeTimer,
                opClass,
                lStLinkedId,
                lStOpCode,
                lStOpCodeFlg,
                lStErrorCode,
                lStErrorCodeFlg,
                lStProbCode,
                lStProbCodeFlg, 
                lCancelFlg,
                lStLastCmp);
   Status status;        

   printf("In INGwIfrTlIfTelnetIntf Param cpBuf:-\n");
   SPrntMsg(cpBuf,0,0); 
      
   S16  retValCmp = TuLiStuCmpInd(pst,            /* post structure */
                     1,
                     suDlgId,
                     spDlgId,
                     lcompEv,
                     opc,
                     status,  /*status*/
                     cpBuf);
                    
   delete pst;
   pst     =  0;
  
   
  }
  else if (liMsgNum == 2) {
   /*Abort Indication*/ 
    pst =  new Pst;
    memset(&pst,0,sizeof(Pst));
    suId = 0;
    suDlgId = dlgId;
    spDlgId = dlgId;
    cause = INC_ABORT_RESOURCE;
    compsPres = false;               
    pAbtCause.pres  = true;
    pAbtCause.octet = INC_ABORT_RESOURCE;
    memset(&qosSet,0,sizeof(StQosSet)); 
    opc       = 121;
    dlgEv = (StDlgEv*)malloc(sizeof(StDlgEv));
    StStr appConName;
    appConName.len = 5;
    for(int i=0;i<appConName.len;i++)
    {
      appConName.string[i] = i;
    }

   createDlgEv (dlgEv,       /*StDlgEv*/
                false,         /*pres*/
                INC_P_ABORT,    /*StDlgType*/
                appConName,   /*apConName*/
                false,        /*resPres*/
                0x00,         /*result*/
                0x00,         /*resSrc*/
                0x00,         /*resReason*/
                0x00          /*abrtSrc*/
      );
   
   StDataParam    *dataParam = NULL;
   uiBuf = NULL;
   SccpAddr *d = (SccpAddr*)(sizeof(SccpAddr));
   SccpAddr *s = (SccpAddr*)(sizeof(SccpAddr));
   memset(&s,0,sizeof(SccpAddr));
   memset(&d,0,sizeof(SccpAddr));

   Dpc dpc = 0;
   msgType = INC_P_ABORT;
   Dpc dcp =0;
   S16 retVal = TuLiStuDatInd(pst,
                  1,
                  INC_P_ABORT,
                  suDlgId,
                  spDlgId, 
                  d,
                  s,
                  compsPres,
                  &pAbtCause,
                  &qosSet,
                  opc,
                  dpc,
                  dlgEv,
                  dataParam,
                  uiBuf);
  }if (liMsgNum == 3) {
   /* O-ANS message*/ 
    logger.logMsg(TRACE_FLAG, 0,"Creating O-ANS Indication");
    pst =  new Pst;
    memset(pst,0,sizeof(Pst));
    suId = 0;
    msgType = INC_CONTINUE;
    suDlgId = dlgId;
    spDlgId = dlgId;
    dstAddr = (SpAddr*)malloc(sizeof(SpAddr));
    srcAddr = (SpAddr*)malloc(sizeof(SpAddr));
    memset(dstAddr,0,sizeof(SpAddr));
    memset(srcAddr,0,sizeof(SpAddr));
 
   compsPres = true;               
   memset(&pAbtCause,0,sizeof(StOctet)); 
   memset(&qosSet,0,sizeof(StQosSet));   
   opc       = 102;
   dlgEv = (StDlgEv*)malloc(sizeof(StDlgEv));
   memset(dlgEv,0,sizeof(StDlgEv));
   StStr appConName;
   appConName.len = 5;
   for(int i=0;i<appConName.len;i++)
   {
     appConName.string[i] = i;
   }

   createDlgEv (dlgEv,       /*StDlgEv*/
                false,         /*pres*/
                INC_CONTINUE,    /*StDlgType*/
                appConName,   /*apConName*/
                false,        /*resPres*/
                0x00,         /*result*/
                0x00,         /*resSrc*/
                0x00,         /*resReason*/
                0x00          /*abrtSrc*/
               );
   uiBuf = NULL;
   Dpc dpc = 0; 
   StDataParam    *dataParam = NULL;
   S16 retVal = TuLiStuDatInd(pst,
                  1,
                  msgType,
                  suDlgId,
                  spDlgId, 
                  dstAddr,
                  srcAddr,
                  compsPres,
                  &pAbtCause,
                  &qosSet,
                  opc,
                  dpc,
                  dlgEv,
                  dataParam,
                  uiBuf);
   // component indication
   Buffer     *cpBuf;
   StComps    *compEv;
   S16 cCnt;

#ifdef SS_HISTOGRAM_SUPPORT
   if (SGetMsg (DFLT_REGION, DFLT_POOL, &cpBuf, __FILE__, __LINE__) != ROK)
#else
   if (SGetMsg (DFLT_REGION, DFLT_POOL, &cpBuf) != ROK)
#endif
   {
       printf("Cannot Allocate Message Buffer \n");
       return -1;
   } 
 
   char* byteBuff = getenv("INGW_O_ANS_PARAM_BUFFER");

   if(NULL != byteBuff){
     unsigned char* oAns;
     string byteBuffStr(byteBuff);
     int size;

   	int length = byteBuffStr.length();
   	// make sure the input string has an even digit numbers
   	
   	if(length%2 == 1)
   	{
   		byteBuffStr = "0" + byteBuffStr;
   		length++;
   	}
   
   	// allocate memory for the output array
   	oAns = new unsigned char[length/2];
   	size = length/2;

     myStringToByteArray(byteBuffStr, oAns, size);
     SAddPstMsgMult((Data *)(oAns), size, cpBuf);
     delete [] (oAns);
   }
   else{
      unsigned char oAns[] = 
                 {0x30,0x0D,0x80,0x01,0x07,0xA3,0x03,0x81,0x01,
                  0x02,0xA4,0x03,0x80,0x01,0x00};
     SAddPstMsgMult((Data *)(oAns), 15, cpBuf);
   }
   //delete [] (*array); after TuLiStuCmpInd


   StComps* lcompEv = (StComps*)malloc(sizeof(StComps));
   memset(lcompEv,0,sizeof(StComps));
   StOctet     lStInvokeId;
   lStInvokeId.pres  = true;
   lStInvokeId.octet = 0x02;
   U16 lStInvokeTimer= 10;
   U8  opClass      = 0x01;
   StOctet  lStLinkedId;
   lStLinkedId.pres = false;
   U8 lStOpCodeFlg = 0x01;

   StStr lStOpCode;
   lStOpCode.len = 1;
   lStOpCode.string[0]= 0x18;

   U8 lStErrorCodeFlg = STU_LOCAL;

   StStr lStErrorCode;
   memset(&(lStErrorCode),0,sizeof(StStr)); 
   StStr  lStProbCode;
   U8 lStProbCodeFlg = 0x00;
   memset(&(lStProbCode),0,sizeof(StStr)); 
   bool lCancelFlg = false;
   bool lStLastCmp = true;
   createcompEv(lcompEv,
                STU_INVOKE,
                lStInvokeId,
                lStInvokeTimer,
                opClass,
                lStLinkedId,
                lStOpCode,
                lStOpCodeFlg,
                lStErrorCode,
                lStErrorCodeFlg,
                lStProbCode,
                lStProbCodeFlg, 
                lCancelFlg,
                lStLastCmp);
   Status status;        

   printf("In INGwIfrTlIfTelnetIntf Param cpBuf:-\n");
   SPrntMsg(cpBuf,0,0); 
      
   S16  retValCmp = TuLiStuCmpInd(pst,            /* post structure */
                     1,
                     suDlgId,
                     spDlgId,
                     lcompEv,
                     opc,
                     status,  /*status*/
                     cpBuf);
                    
   delete pst;
   pst     =  0;
  
   
  }

  else if (liMsgNum == 4){
   /* O-DISC msg*/
    logger.logMsg(TRACE_FLAG, 0,"Creating O-DISC Indication");
    pst =  new Pst;
    memset(pst,0,sizeof(Pst));
    suId = 0;
    msgType = INC_CONTINUE;
    suDlgId = dlgId;
    spDlgId = dlgId;
    dstAddr = (SpAddr*)malloc(sizeof(SpAddr));
    srcAddr = (SpAddr*)malloc(sizeof(SpAddr));
    memset(dstAddr,0,sizeof(SpAddr));
    memset(srcAddr,0,sizeof(SpAddr));
 
   compsPres = true;               
   memset(&pAbtCause,0,sizeof(StOctet)); 
   memset(&qosSet,0,sizeof(StQosSet));   
   opc       = 102;
   dlgEv = (StDlgEv*)malloc(sizeof(StDlgEv));
   memset(dlgEv,0,sizeof(StDlgEv));
   StStr appConName;
   appConName.len = 5;
   for(int i=0;i<appConName.len;i++)
   {
     appConName.string[i] = i;
   }

   createDlgEv (dlgEv,       /*StDlgEv*/
                false,         /*pres*/
                INC_CONTINUE, /*StDlgType*/
                appConName,   /*apConName*/
                false,        /*resPres*/
                0x00,         /*result*/
                0x00,         /*resSrc*/
                0x00,         /*resReason*/
                0x00          /*abrtSrc*/
               );
   uiBuf = NULL;
   Dpc dpc = 0; 
   StDataParam    *dataParam = NULL;
   S16 retVal = TuLiStuDatInd(pst,
                  1,
                  msgType,
                  suDlgId,
                  spDlgId, 
                  dstAddr,
                  srcAddr,
                  compsPres,
                  &pAbtCause,
                  &qosSet,
                  opc,
                  dpc,
                  dlgEv,
                  dataParam,
                  uiBuf);
   // component indication
   Buffer     *cpBuf;
   StComps    *compEv;
   S16 cCnt;

#ifdef SS_HISTOGRAM_SUPPORT
   if (SGetMsg (DFLT_REGION, DFLT_POOL, &cpBuf, __FILE__, __LINE__) != ROK)
#else
   if (SGetMsg (DFLT_REGION, DFLT_POOL, &cpBuf) != ROK)
#endif
   {
       printf("Cannot Allocate Message Buffer \n");
       return -1;
   } 
 


   char* byteBuff = getenv("INGW_O_DISC_PARAM_BUFFER");

   if(NULL != byteBuff){
     unsigned char* oDisc;
     string byteBuffStr(byteBuff);
     int size;
    	int length = byteBuffStr.length();
    	// make sure the input string has an even digit numbers
    	if(length%2 == 1)
    	{
    		byteBuffStr = "0" + byteBuffStr;
    		length++;
    	}
    
    	// allocate memory for the output array
    	oDisc = new unsigned char[length/2];
    	size = length/2;

     myStringToByteArray(byteBuffStr, oDisc, size);
     SAddPstMsgMult((Data *)(oDisc), size, cpBuf);
     delete [] (oDisc);
   }
   else{
        unsigned char oDisc[] =
                 {0x30,0x08,0x80,0x01,0x09,
                  0xA3,0x03,0x81,0x01, 0x02}; 
     SAddPstMsgMult((Data *)(oDisc), 10, cpBuf);
   }
   //delete [] (*array); after TuLiStuCmpInd


   StComps* lcompEv = (StComps*)malloc(sizeof(StComps));
   memset(lcompEv,0,sizeof(StComps));
   StOctet     lStInvokeId;
   lStInvokeId.pres  = true;
   lStInvokeId.octet = 0x03;
   U16 lStInvokeTimer= 10;
   U8  opClass      = 0x01;
   StOctet  lStLinkedId;
   lStLinkedId.pres = false;
   U8 lStOpCodeFlg = 0x01;

   StStr lStOpCode;
   lStOpCode.len = 1;
   lStOpCode.string[0]= 0x18;

   U8 lStErrorCodeFlg = STU_LOCAL;

   StStr lStErrorCode;
   memset(&(lStErrorCode),0,sizeof(StStr)); 
   StStr  lStProbCode;
   U8 lStProbCodeFlg = 0x00;
   memset(&(lStProbCode),0,sizeof(StStr)); 
   bool lCancelFlg = false;
   bool lStLastCmp = true;
   createcompEv(lcompEv,
                STU_INVOKE,
                lStInvokeId,
                lStInvokeTimer,
                opClass,
                lStLinkedId,
                lStOpCode,
                lStOpCodeFlg,
                lStErrorCode,
                lStErrorCodeFlg,
                lStProbCode,
                lStProbCodeFlg, 
                lCancelFlg,
                lStLastCmp);
   Status status;        

   printf("In INGwIfrTlIfTelnetIntf Param cpBuf:-\n");
   SPrntMsg(cpBuf,0,0); 
      
   S16  retValCmp = TuLiStuCmpInd(pst,            /* post structure */
                     1,
                     suDlgId,
                     spDlgId,
                     lcompEv,
                     opc,
                     status,  /*status*/
                     cpBuf);
                    
   delete pst;
   pst     =  0;
  
   
  } 
  else if (liMsgNum == 5){
    // Sending Initial DP to network
		// Create component First 
   	// component request
  static int liDlgid = 1;
  static int liInvokeId = 1;
	INGwTcapWorkUnitMsg apMsg; 
	apMsg.m_tcapMsg = new TcapMessage();
    memset(&(apMsg.m_tcapMsg->dlgR),0,sizeof(TcapDlg));

    apMsg.eventType = EVTSTUDATREQ;
    apMsg.m_appInstId.appId = 0;// spId
    apMsg.compPres = true;// component present.
    apMsg.m_dlgId  = ++liDlgid + 100;

    apMsg.m_tcapMsg->dlgR.sudlgId =apMsg.m_dlgId;
		apMsg.m_tcapMsg->dlgR.spdlgId = 0;
		apMsg.m_tcapMsg->appid.appId  = 0;
  
    TcapComp* comp = new TcapComp();
    memset(comp,0,sizeof(TcapComp));
		comp->compType       = INC_INVOKE;
		comp->invIdItu.pres  = true;
		comp->invIdItu.octet = liInvokeId++;
    comp->linkedId.pres  = false; 
    comp->opClass        = 0x01; 
    comp->opTag          = 0x01; 
    
    comp->opCode.len        = 0x01; 
    comp->opCode.string[0]  = 0x00;

		comp->errCode.len    = 0; // error code len
		comp->probCode.len   = 0; // prob code len
    comp->lastComp = true;
    comp->invokeTimer = 0;
    char* lpcInvTimer= getenv("INGW_INVOKE_TIMER");
    if(NULL != lpcInvTimer) {
      comp->invokeTimer = atoi(lpcInvTimer);  
    } 
    comp->cancelFlg = false;

    //INGW_IDP_TO_NW shall be initialized with the byte buffer required to be
    //sent to N/w in component parameter
    char* byteBuff = getenv("INGW_IDP_TO_NW");
    if(NULL != byteBuff){
      unsigned char* idp;
      string byteBuffStr(byteBuff);
      int size;
   	  int length = byteBuffStr.length();
   	  // make sure the input string has an even digit numbers
   	  
   	  if(length%2 == 1)
   	  {
   	  	byteBuffStr = "0" + byteBuffStr;
   	  	length++;
   	  }
   
   	  // allocate memory for the output array
   	  idp = new unsigned char[length/2];
   	  size = length/2;
      myStringToByteArray(byteBuffStr, idp, size);
      comp->param.len      = size;
      comp->param.string   = idp;
    }
    else{
     unsigned char idp[] = 

     {0x30, 0x25, 0x80, 0x01, 0x65, 0x82, 0x07, 0x03,
      0x10, 0x01, 0x00, 0x00, 0x00, 0x50, 0x83, 0x07, 0x04, 0x13, 
      0x19, 0x12, 0x00, 0x01, 0x01, 0x85, 0x01, 0x0a, 0x88, 0x01,
      0x00, 0xbb, 0x05, 0x80, 0x03, 0x80, 0x90, 0xa3, 0x9c, 0x01,
      0x02}; 
      comp->param.len      = 39;
      comp->param.string = new unsigned char[39]; 
      memcpy(comp->param.string,idp,39);
      logger.logMsg(ALWAYS_FLAG, 0,"Value of INGW_IDP_TO_NW found as NULL"); 
    }


    vector<TcapComp*> *lCompVector = new  vector<TcapComp*>;
    lCompVector->push_back(comp);
    apMsg.m_tcapMsg->setCompVector(lCompVector);

		apMsg.m_tcapMsg->dlgR.pres = true;
		apMsg.m_tcapMsg->dlgR.dlgType = INC_BEGIN; 
		apMsg.m_tcapMsg->msg_type = INC_BEGIN; 

    dstAddr = (SpAddr*)malloc(sizeof(SpAddr));
    srcAddr = (SpAddr*)malloc(sizeof(SpAddr));

    GlbTi gtd;
    GlbTi gts;

    /*NTT-Q713 Appendix 12-18*/
    gtd.format       =  GTFRMT_2; //GT contains Translation type only
    //gtd.gt.f2.tType  = 0xE8; //Ttype
    gtd.gt.f2.tType  = 0xE9; //Ttype
    gtd.addr.length  = 2;
    gtd.addr.strg[0] = 0x21;
    gtd.addr.strg[1] = 0x43;
    //gttHexAddrToBcd(&destHexGtDigits, &gtd.addr);

    gts.format =  GTFRMT_2; //GT contains Translation type only
    gts.gt.f2.tType  = 0xE9; //Ttype
    gts.addr.length  = 2;
    gts.addr.strg[0] = 0x21; //It should be in BCD format
    gts.addr.strg[1] = 0x43;

    //gttHexAddrToBcd(&srcHexGtDigits, &gts.addr);
 
    createSccpAddr(dstAddr,
                   true,
                   true,
                   false,
                   INC_RTE_GT,
                   true,
                   false,
                   //121,
                   122,
                   146,
                   SW_JAPAN,// sp_bdy2.c:7732 
                   gtd);
    
    createSccpAddr(srcAddr,
                  true,
                  true,
                  false,
                  INC_RTE_GT,
                  true,
                  false,
                  //120,
                  123,
                  146,
                  SW_JAPAN,//sp_bdy2.c:7732 
                  gts);
    apMsg.m_tcapMsg->dlgR.dstAddr = dstAddr;
    apMsg.m_tcapMsg->dlgR.srcAddr = srcAddr;
		INGwSilTx::instance().sendTcapReq(&apMsg);
  }
  else if (liMsgNum == 6){
    //POC to replicate TC-BEGIN also
    unsigned char rawBytesFromAdaxDevice[] = {
      0x3f ,0x03 ,0x79 ,0x00 ,0x78 ,0x00 ,0x0e ,0x09,
      0x80 ,0x03 ,0x0e ,0x15 ,0x0b ,0x0a ,0x92 ,0xe8, 
      0x02 ,0x31 ,0x00 ,0x80 ,0x80 ,0x00 ,0x21 ,0x43,
      0x07 ,0x0a ,0x92 ,0xe9 ,0x02 ,0x90 ,0x00 ,0x79,
      0x9d ,0x62 ,0x81 ,0x9a ,0x48 ,0x04 ,0x80 ,0x38,
      0x00 ,0x00 ,0x6b ,0x21 ,0x28 ,0x1f ,0x06 ,0x07,
      0x00 ,0x11 ,0x86 ,0x05 ,0x01 ,0x01 ,0x01 ,0xa0,
      0x14 ,0x60 ,0x12 ,0x80 ,0x02 ,0x07 ,0x80 ,0xa1,
      0x0c ,0x06 ,0x0a ,0x02 ,0x83 ,0x38 ,0x66 ,0x03,
      0x02 ,0x02 ,0x01 ,0x01 ,0x04 ,0x6c ,0x6f ,0xa1,
      0x6d ,0x02 ,0x01 ,0x01 ,0x02 ,0x01 ,0x00 ,0x30,
      0x65 ,0x80 ,0x01 ,0x2c ,0x82 ,0x06 ,0x06 ,0x10,
      0x02 ,0x34 ,0x55 ,0x00 ,0x83 ,0x07 ,0x03 ,0x13,
      0x21 ,0x43 ,0x65 ,0x87 ,0x00 ,0x85 ,0x01 ,0x0a,
      0xab ,0x06 ,0x80 ,0x01 ,0x00 ,0x81 ,0x01 ,0x02,
      0x8e ,0x01 ,0x00 ,0xaf ,0x35 ,0x30 ,0x33 ,0x02,
      0x01 ,0xfe ,0xa1 ,0x2e ,0x30 ,0x2c ,0x81 ,0x04,
      0x81 ,0x25 ,0x00 ,0x00 ,0x82 ,0x07 ,0x83 ,0x14,
      0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x83 ,0x0f ,0x02,
      0xfb ,0x05 ,0xfe ,0x03 ,0x00 ,0x90 ,0x30 ,0xf8,
      0x05 ,0xfe ,0x03 ,0x00 ,0x22 ,0x53 ,0x84 ,0x04,
      0xfd ,0x01 ,0xfc ,0x08 ,0x85 ,0x04 ,0x81 ,0x25,
      0x00 ,0x00 ,0x9a ,0x02 ,0x20 ,0x01 ,0xbb ,0x03,
      0x81 ,0x01 ,0x00 ,0x9c ,0x01 ,0x03};

    size_t portMapNdx = 0;
    Buffer *pl3Buffer;
    DlSap_t *pSap =  GetDlSap(portMapNdx);
    if (NULL == (pSap = GetDlSap(portMapNdx))) {
      logger.logINGwMsg(false,ERROR_FLAG,0,"[testCall 6]"
      " Unbound SAP Link %lu",(unsigned long)portMapNdx);
    }
   
    logger.logINGwMsg(false,ERROR_FLAG,0,"[testCall 6] Link No. %d",pSap->linkNo);
#ifdef SS_HISTOGRAM_SUPPORT
    if (SGetMsg(BP_AIN_SM_REGION, pSap->snPst.pool, &pl3Buffer, __FILE__, __LINE__) != ROK)
#else
    if (SGetMsg(BP_AIN_SM_REGION, pSap->snPst.pool, &pl3Buffer) != ROK)
#endif
    {
      logger.logINGwMsg(false, ERROR_FLAG, 0,
        "[testCall 6] SGetMsg failed");
      return -1;
    }

    if (SAddPstMsgMult((Data*)rawBytesFromAdaxDevice,190, pl3Buffer) != ROK) {
      logger.logINGwMsg(false, ERROR_FLAG, 0,
        "[testCall 6] SAddPstMsgMult failed");

      return -1;
    }
    if (!MTP3_MUTEX_LOCK()) {
         SdUiSdtDatInd(&pSap->snPst, pSap->suId, pl3Buffer);
           MTP3_MUTEX_UNLOCK();
    }
    else
    logger.logINGwMsg(false, ERROR_FLAG, 0,
                "[testCall 6] failed to acquire MTP3 LOCK\n");

  }   
  return 0;
}

int runLoad(int p_numberOfCalls)
{
  int retVal = 0;
  int msgType[] = {IDP_MSG, O_ANS_MSG, O_DISC_MSG};
  int msgSeqLen = 3;
  for(int dlgId = 0; dlgId < p_numberOfCalls; dlgId++)
  {
     for(int j =0;j< msgSeqLen;j++)
     { 
       if(-1 == testCall(msgType[j], dlgId))
       {
         retVal = -1;
         break;
       } 
       sleep(1);
     }
  }
  return retVal;
}

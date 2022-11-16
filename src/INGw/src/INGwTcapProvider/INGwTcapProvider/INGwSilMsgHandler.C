//#include <CCMUtil/BpLogger.h>
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwTcapProvider");


#include "INGwSilMsgHandler.h"
#include "INGwTcapProvider.h"
#include "INGwInfraManager/INGwIfrMgrWorkUnit.h"
#include "INGwIwf/INGwIwfBaseProvider.h"


using namespace std;


INGwSilMsgHandler *INGwSilMsgHandler::mpSelf = NULL;

INGwSilMsgHandler::INGwSilMsgHandler(INGwIwfBaseProvider    &arProvider)
   :mrProvider(arProvider)
{
}

INGwSilMsgHandler::~INGwSilMsgHandler()
{
}

void
INGwSilMsgHandler::init()
{
}
   
INGwSilMsgHandler*
INGwSilMsgHandler::getInstance()
{
   logger.logINGwMsg(false, TRACE_FLAG, 0,
                 "IN INGwSilMsgHandler::getInstance()");
   
   if(NULL == mpSelf) {
      logger.logINGwMsg(false, VERBOSE_FLAG, 0,
                    "Creating INGwSilMsgHandler object");
      INGwSilMsgHandler::mpSelf = new INGwSilMsgHandler(INGwTcapProvider::getInstance());
   }

   logger.logINGwMsg(false, TRACE_FLAG, 0,
                 "OUT INGwSilMsgHandler::getInstance()");
   return INGwSilMsgHandler::mpSelf;
}

int
INGwSilMsgHandler::handleWorkerClbk(INGwIfrMgrWorkUnit* apWork)
{
   LogINGwTrace(false, 0, "IN INGwSilMsgHandler::handleWorkerClbk");

   INGwSilMsg *lINGwSilMsg = static_cast<INGwSilMsg *> (apWork->mpMsg);
   
   handleAinSilMsg(*lINGwSilMsg);

   // Delete AinsilMsg
   // Work unit is deleted by CCM
   // just set mpMsg to NULL
   delete lINGwSilMsg;
   apWork->mpMsg = NULL;
   
   LogINGwTrace(false, 0, "OUT INGwSilMsgHandler::handleWorkerClbk");

   return AIN_SUCCESS;
}



int
INGwSilMsgHandler::handleAinSilMsg(INGwSilMsg &arINGwSilMsg)
{
   logger.logINGwMsg(false, TRACE_FLAG, 0,
                 "IN INGwSilMsgHandler::handleAinSilMsg()");

   switch(arINGwSilMsg.evtType) {
   default:
      {
         logger.logINGwMsg(false, ERROR_FLAG, 0,
                       "Call Id <%d>. Unknown event type [%d] received. Returning Error.",
                         arINGwSilMsg.spDlgId,
                         arINGwSilMsg.evtType);
         logger.logINGwMsg(false, TRACE_FLAG, 0,
                         "OUT INGwSilMsgHandler::handleAinSilMsg()");
         return AIN_ERROR;
      }
      
   } // end switch(arINGwSilMsg.evtType)   
   
   logger.logINGwMsg(false, TRACE_FLAG, 0,
                 "OUT INGwSilMsgHandler::handleAinSilMsg()");

   return AIN_SUCCESS;
}


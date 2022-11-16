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
//     File:     INGwSpMsgBaseHandler.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipMsgHandler");


#include <INGwSipMsgHandler/INGwSpMsgBaseHandler.h>
#include <INGwSipProvider/INGwSpSipConnection.h>
#include <INGwSipProvider/INGwSpSipUtil.h>

#include <INGwSipProvider/INGwSpSipCommon.h>

#include <INGwSipProvider/INGwSpSipStackIntfLayer.h>

using namespace std;

const int TERMINATE_BYE     = 0x01; 
const int TERMINATE_CANCEL  = 0x10;

#define MAX_METHODNAME_LENGTH 50

INGwSpMsgBaseHandler::~INGwSpMsgBaseHandler()
{
}

bool INGwSpMsgBaseHandler::terminateServerTransaction(INGwSpSipConnection *aSipConnection, 
                                              INGwSipMethodType aMethodType,
                                              Sdf_st_overlapTransInfo *pOverlapTransInfo)
{
   LogINGwTrace(false, 0, "IN terminateServerTransaction");

   logger.logINGwMsg(aSipConnection->mLogFlag, TRACE_FLAG, 0, 
                   "Terminating server trans [%s]", 
                   aSipConnection->getLocalCallId().c_str());


   // Form a 487 response and send on the server transaction.

   Sdf_ty_retVal status = Sdf_co_fail;
   Sdf_st_callObject  *hssCallObj = aSipConnection->getHssCallObject();

   if(!hssCallObj)
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "NULL call object in connection");
      LogINGwTrace(false, 0, "OUT terminateServerTransaction");
      return false;
   }

   const char *methodTypeStr = NULL;

   switch(aMethodType)
   {
      case INGW_SIP_METHOD_TYPE_PRACK    : 
      case INGW_SIP_METHOD_TYPE_UPDATE    : 
      case INGW_SIP_METHOD_TYPE_CANCEL   : 
      case INGW_SIP_METHOD_TYPE_BYE      : 
      case INGW_SIP_METHOD_TYPE_OPTIONS  : 
      case INGW_SIP_METHOD_TYPE_NOTIFY   : 
      case INGW_SIP_METHOD_TYPE_REFER    : 
      case INGW_SIP_METHOD_TYPE_INFO     : 
      {
         methodTypeStr = glbMethodName[aMethodType];
      }
      break;

      default:
      {
         logger.logINGwMsg(false, ERROR_FLAG, 0, "Unrecognized method type [%d]", 
                         aMethodType);
         LogINGwTrace(false, 0, "OUT terminateServerTransaction");
         return false;
      }
   } 

   Sdf_st_error sdferror;

	 //BPInd19817
   status = sdf_ivk_uaFormResponse(487, methodTypeStr, hssCallObj, 
                                   pOverlapTransInfo,
                                   Sdf_co_false, &sdferror);

   if(status != Sdf_co_success)
   {
      INGwSpSipUtil::checkError(status, sdferror);
      LogINGwTrace(false, 0, "OUT terminateServerTransaction");
      return false;
   }

   if((pOverlapTransInfo != NULL) && (aMethodType == INGW_SIP_METHOD_TYPE_INFO))
   {
      status = INGwSpSipUtil::sendCallToPeer(hssCallObj,
                                         pOverlapTransInfo->pSipMsg,
                                         aMethodType, &sdferror, aSipConnection);
   }
   else {
      status = INGwSpSipUtil::sendCallToPeer(hssCallObj,
                                         hssCallObj->pUasTransaction->pSipMsg,
                                         aMethodType, &sdferror, aSipConnection);

   }

   if(status != Sdf_co_success)
   {
      INGwSpSipUtil::checkError(status, sdferror);
      LogINGwTrace(false, 0, "OUT terminateServerTransaction");
      return false;
   }

   logger.logINGwMsg(false, TRACE_FLAG, 0, "Sent 487 error response for method "
                                            "[%s]", methodTypeStr);

   LogINGwTrace(false, 0, "OUT terminateServerTransaction");
   return true;
} 

bool INGwSpMsgBaseHandler::terminateTransaction(INGwSpSipConnection *aSipConnection, 
                                        int aErrcode, TerminateType aTermType)
{
   LogINGwTrace(false, 0, "IN terminateTransaction");

   INGwSipConnMinorState connminorstate = aSipConnection->getMinorState();

   logger.logINGwMsg(false, TRACE_FLAG, 0, "TerminateTransaction: connection [%s]"
                                         ", connection MinorState [%d]", 
                   aSipConnection->getLocalCallId().c_str(), connminorstate);

   Sdf_ty_retVal status = Sdf_co_fail;
   Sdf_st_callObject  *hssCallObj = aSipConnection->getHssCallObject();
   Sdf_st_error        sdferror;

   if(!hssCallObj)
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "NULL call object in connection");
      LogINGwTrace(false, 0, "OUT terminateTransaction");
      return false;
   }

   if(connminorstate == CONN_MINSTATE_MID_2_MSGRX)
   {
      const char *methodType = NULL;
      INGwSipMethodType aMethodType = aSipConnection->getLastMethod();

      switch(aMethodType)
      {
         case INGW_SIP_METHOD_TYPE_PRACK    : 
         case INGW_SIP_METHOD_TYPE_UPDATE    : 
         case INGW_SIP_METHOD_TYPE_CANCEL   : 
         case INGW_SIP_METHOD_TYPE_BYE      : 
         case INGW_SIP_METHOD_TYPE_REFER    : 
         case INGW_SIP_METHOD_TYPE_OPTIONS  : 
         case INGW_SIP_METHOD_TYPE_NOTIFY   : 
         case INGW_SIP_METHOD_TYPE_INFO     : 
         {
            methodType = glbMethodName[aMethodType];
         }
         break;

         default:
         {
            logger.logINGwMsg(false, ERROR_FLAG, 0, 
                            "Unrecognized method type [%d]", aMethodType);
            return Sdf_co_fail;
         }
      }

      switch(aMethodType)
      {
         case INGW_SIP_METHOD_TYPE_CANCEL   :
         case INGW_SIP_METHOD_TYPE_BYE      :
         {
         }
         break;

         case INGW_SIP_METHOD_TYPE_REFER    :
         case INGW_SIP_METHOD_TYPE_PRACK    :
         case INGW_SIP_METHOD_TYPE_UPDATE    :
         case INGW_SIP_METHOD_TYPE_OPTIONS  :
         case INGW_SIP_METHOD_TYPE_NOTIFY   :
         case INGW_SIP_METHOD_TYPE_INFO     :
         {
            Sdf_st_overlapTransInfo* pOverlapTransInfo =
                                 INGwSpSipUtil::getLastOverlapTransInfo(hssCallObj);

            if(!pOverlapTransInfo)
            {
               logger.logINGwMsg(false, ERROR_FLAG, 0, 
                               "Error getting overlap transinfo");
               status = Sdf_co_fail;
               break;
            }

            status = sdf_ivk_uaFormResponse(aErrcode, methodType, hssCallObj,
                                            pOverlapTransInfo, Sdf_co_false, 
                                            &sdferror);

            status = INGwSpSipUtil::sendCallToPeer(hssCallObj, 
                                               pOverlapTransInfo->pSipMsg, 
                                               aMethodType, &sdferror, 
                                               aSipConnection);

            logger.logINGwMsg(false, TRACE_FLAG, 0, "Sent [%d] error response for"
                                                  "method [%s]", 
                            aErrcode, methodType);
         }
         break;

         default:
         {
            logger.logINGwMsg(false, ERROR_FLAG, 0, 
                            "Unrecognized method type [%d]", aMethodType);
            status = Sdf_co_fail;
         }
      } 
   }
   else if(connminorstate == CONN_MINSTATE_MID_2_MSGTX)
   {
      // We have not received a response.  But that does not prevent us from
      // terminating the call, which is the reason for doing this
      // terminateTransaction anyway.
      status = Sdf_co_success;
   }
   else
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "Called in wrong idle state [%d]", 
                      connminorstate);
      status = Sdf_co_fail;
   }

   if(status == Sdf_co_success)
   {
      return true;
   }

   INGwSpSipUtil::checkError(status, sdferror);
   return false;
} 

void INGwSpMsgBaseHandler::reset(void) 
{
}

void INGwSpMsgBaseHandler::receiveRedirectionInfo(INGwSpSipConnection *aSipConnection, 
                                          int aContactCount, 
                                          INGwSipEPInfo* aContactList)
{
   logger.logINGwMsg(false, ERROR_FLAG, 0, "receiveRedirectionInfo: This method "
                                         "should never be called in the base "
                                         "class");

   if(aContactCount)
   {
      delete[] aContactList;
   }
} 

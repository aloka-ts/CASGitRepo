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
//     File:     INGwSpMsgOptionsHandler.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   08/12/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipMsgHandler");

#include <INGwSipMsgHandler/INGwSpMsgOptionsHandler.h>

#include <INGwSipProvider/INGwSpSipConnection.h>
#include <INGwSipProvider/INGwSpSipCall.h>
#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpData.h>
#include <INGwSipProvider/INGwSpSipUtil.h>
#include <INGwSipProvider/INGwSpSipProvider.h>
#include <INGwSipProvider/INGwSpSipCallController.h>
#include <INGwInfraStatMgr/INGwIfrSmStatMgr.h>

int imERR_NONE = 0;

using namespace std;

INGwSpMsgOptionsHandler::INGwSpMsgOptionsHandler()
{
   miPort = 0;
}

INGwSpMsgOptionsHandler::~INGwSpMsgOptionsHandler()
{
}

Sdf_ty_retVal INGwSpMsgOptionsHandler::stackCallbackRequest(
                    INGwSpSipConnection         *aSipConnection   ,
                    INGwSipMethodType             aMethodType      ,
                    Sdf_st_callObject      **ppCallObj        ,
                    Sdf_st_eventContext     *pEventContext    ,
                    Sdf_st_error            *pErr             ,
                    Sdf_st_overlapTransInfo *pOverlapTransInfo)
{

  LogINGwTrace(aSipConnection->getCall().mLogFlag, 0, "IN stackCallbackRequest");

  int                  connMajorState = (int)aSipConnection->getMajorState();
  INGwSipConnMinorState  connMinorState = aSipConnection->getMinorState();
  Sdf_st_error         sdferror;

  logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
         "INGwSpMsgOptionsHandler::stackCallbackRequest: connmajorstate <%d>, connminorstate <%d> ", 
          connMajorState, connMinorState );


	// INGW Handling
   int lCurVal = 0;

   logger.logINGwMsg(false, TRACE_FLAG,0,"miStatParamId_NumOptRecvd <%d>",
                     INGwSpSipProvider::miStatParamId_NumOptRecvd);

   INGwIfrSmStatMgr::instance().increment(
							INGwSpSipProvider::miStatParamId_NumOptRecvd, lCurVal, 1);

  if(pOverlapTransInfo == NULL )
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "NULL Overlap Transaction Info");
    LogINGwTrace(aSipConnection->getCall().mLogFlag, 0, "OUT stackCallbackRequest");
    return Sdf_co_success;
  }
  //PR 50787
  INGwSipMethodType lLastMethod = aSipConnection->getLastMethod();

  if(lLastMethod == INGW_SIP_METHOD_TYPE_INFO)
  {
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
         "INGwSpMsgOptionsHandler::stackCallbackRequest: "
         "Recvd Options while Info tranx is in progress ", 
          connMajorState, connMinorState );
  }

  aSipConnection->setLastMethod(INGW_SIP_METHOD_TYPE_OPTIONS);

  // Process via header to get GW port
  INGwSpSipUtil::processIncomingViaHeader(aSipConnection, *ppCallObj);

	INGwSpSipCallController* callCtlr =
	        INGwSpSipProvider::getInstance().getCallController();
	// Ask call controller to handle it
	callCtlr->processHBMsg(aSipConnection);

  if(lLastMethod == INGW_SIP_METHOD_TYPE_INFO)
  {
    aSipConnection->setLastMethod(INGW_SIP_METHOD_TYPE_INFO);
  }

  LogINGwTrace(aSipConnection->getCall().mLogFlag, 0, "OUT stackCallbackRequest");
  return Sdf_co_success;
}

Sdf_ty_retVal INGwSpMsgOptionsHandler::stackCallbackResponse
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   int                      aRespCode        ,
                   Sdf_st_callObject      **ppCallObj        ,
                   Sdf_st_eventContext     *pEventContext    ,
                   Sdf_st_error            *pErr             ,
                   Sdf_st_overlapTransInfo *pOverlapTransInfo)
{
	//We will only send OPTION and will not recv them
	//so there won't be any response received

  return Sdf_co_success;
} // end of stackCallbackResponse

Sdf_ty_retVal INGwSpMsgOptionsHandler::stackCallbackAck
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   Sdf_st_callObject      **ppCallObj        ,
                   Sdf_st_eventContext     *pEventContext    ,
                   Sdf_st_error            *pErr             )
{
  return Sdf_co_fail;
}

int INGwSpMsgOptionsHandler::mSendRequest
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   INGwSpData                *aGwData          )
{
	//We will only send OPTION and will not recv them
  return -1;
} // end of mSendRequest

int INGwSpMsgOptionsHandler::mSendResponse
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   INGwSpData                *aGwData          ,
                   int                      aCode)
{
  LogINGwTrace(aSipConnection->getCall().mLogFlag, 0, "IN mSendResponse");

  INGwSipConnMinorState connMinorState = aSipConnection->getMinorState();
  int                  connMajorState = (int)aSipConnection->getMajorState();
  INGwSipMethodType    methodType     = aSipConnection->getLastMethod();
  Sdf_st_callObject*   hssCallObj     = aSipConnection->getHssCallObject();
  Sdf_ty_retVal        status         = Sdf_co_fail;
  Sdf_st_error         sdferror;

  logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE,
                    "INGwSpMsgOptionsHandler::mSendResponse: "
                    "connminstate <%d>, connmajorstate <%d>, methodtype <%d>, ",
                    connMinorState, connMajorState, aMethodType);


  // Form a response
  Sdf_st_overlapTransInfo *pOverlapTransInfo =
              INGwSpSipUtil::getLastOverlapTransInfo(hssCallObj);
  status = sdf_ivk_uaFormResponse(aCode, "OPTIONS" , hssCallObj,
                                  pOverlapTransInfo, Sdf_co_false, &sdferror);


  if(status != Sdf_co_success)
  {
    INGwSpSipUtil::checkError(status, sdferror);
    LogINGwTrace(false, 0, "OUT stackCallbackRequest");
    return Sdf_co_success;
  }

  status = INGwSpSipUtil::sendCallToPeer(hssCallObj,
                                     pOverlapTransInfo->pSipMsg,
                                     INGW_SIP_METHOD_TYPE_OPTIONS,
                                     &sdferror,
                                     aSipConnection);

  if(status != Sdf_co_success)
  {
    INGwSpSipUtil::checkError(status, sdferror);
    LogINGwTrace(false, 0, "OUT stackCallbackRequest");
    return Sdf_co_success;
  }

  aSipConnection->setLastMethod(INGW_METHOD_TYPE_NONE);

  LogINGwTrace(aSipConnection->getCall().mLogFlag, 0, "Sent 200 response for method OPTIONS");
  LogINGwTrace(aSipConnection->getCall().mLogFlag, 0, "OUT mSendResponse");

  if(status == Sdf_co_success) return 0;
  else                         return -1;
}

int INGwSpMsgOptionsHandler::mSendAck
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   INGwSpData                *aGwData          )
{
  LogINGwTrace(aSipConnection->getCall().mLogFlag, 0, "IN mSendAck");
  LogINGwTrace(aSipConnection->getCall().mLogFlag, 0, "OUT mSendAck");
  return -1;
}

void INGwSpMsgOptionsHandler::indicateTimeout
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   INGwSipTimerType::TimerType aType           ,
                   unsigned int              aTimerid        )
{
  LogINGwTrace(aSipConnection->getCall().mLogFlag, 0, "IN indicateTimeout");
  // This is equivalent to a Request-Timeout(408) error.
  Sdf_ty_retVal      status     = Sdf_co_fail;
  Sdf_st_callObject *hssCallObj = aSipConnection->getHssCallObject();
  Sdf_st_error       sdferror;

  status = stackCallbackResponse(aSipConnection,
                                 INGW_SIP_METHOD_TYPE_OPTIONS,
                                 408,
                                 &hssCallObj,
                                 NULL,
                                 &sdferror,
                                 NULL);

  if(status != Sdf_co_success)
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
											"INGwSpMsgOptionsHandler::indicateTimeout: "
											"Error calling response callback on OPTIONS handler");
  else
  {
    // Reset the client transaction.
    status = sdf_ivk_uaClearRegularTransaction(hssCallObj, "OPTIONS", 
                                INGwSpSipUtil::getLastOverlapTransInfo(hssCallObj));
    if(status != Sdf_co_success)
    {
      INGwSpSipUtil::checkError(status, sdferror);
      logger.logINGwMsg(aSipConnection->getCall().mLogFlag, 
												ERROR_FLAG, imERR_NONE, 
												"INGwSpMsgOptionsHandler::indicateTimeout: "
												"error clearing the OPTIONS transaction");
    }
    else
      logger.logINGwMsg(aSipConnection->getCall().mLogFlag, 
												TRACE_FLAG, imERR_NONE, 
												"INGwSpMsgOptionsHandler::mSendRequest: "
												"successfully cleared the OPTIONS transaction");
  }
  LogINGwTrace(aSipConnection->getCall().mLogFlag, 0, "OUT indicateTimeout");
} // end of indicateTimeout

void INGwSpMsgOptionsHandler::setHBParams(const string &aHost, int aPort)
{
  mstrHost = aHost;
  miPort   = aPort;
} // end of setHBParams

std::string INGwSpMsgOptionsHandler::toLog()const
{
   std::string ret = "\nOptionHandler\nHost:";

   ret += mstrHost;
   char local[500];

   sprintf(local, " Port:[%d]\n", miPort);

   ret += local;

   return ret;
}

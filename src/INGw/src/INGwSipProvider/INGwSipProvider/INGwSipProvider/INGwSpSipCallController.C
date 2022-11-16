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
//     File:     INGwSpSipCallController.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipProvider");
#include <time.h>
#include <pthread.h>
#include <map>
#include <string>
#include <Util/imAlarmCodes.h>
#include <INGwInfraManager/INGwIfrMgrAlarmMgr.h>

#include <INGwSipProvider/INGwSpSipCallController.h>

#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpSipConnection.h>
#include <INGwSipProvider/INGwSpDataFactory.h>
#include <INGwSipProvider/INGwSpSipProvider.h>

#include <INGwInfraUtil/INGwIfrUtlGlbInclude.h>
#include <INGwInfraManager/INGwIfrMgrThreadMgr.h>
#include <INGwInfraManager/INGwIfrMgrWorkUnit.h>
#include <INGwInfraManager/INGwIfrMgrWorkerThread.h>
#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>
#include <INGwFtPacket/INGwFtPktDeleteCallMsg.h>
#include <INGwFtPacket/INGwFtPktCallDataMsg.h>
#include <INGwSipProvider/INGwSpSipListenerThread.h>
#include <INGwIwf/INGwIwfProvider.h>
#include <INGwSipProvider/INGwSpSipStackIntfLayer.h>

#include <Util/SchedulerMsg.h>
#include <stdlib.h>
//INCTBD:Yogesh
#define INGW_MSG_BODYTYPE_TCAP  "application/tcap"
extern timeStamp g_NwInbTs;

extern void myStringToByteArray(std::string str, unsigned char* array,
                                int& size);

// For debugging - Start [
extern bool hbIncDiagTriggered;
extern bool g_enableDiagLogs;

extern int g_IncDiag(char * action, char * aFile, int aLine,
                     char * reason, int scenario);
// ] End - For debugging
		
//It will make copy of end point info and save in its map
//Retrun 0 if successful else return -1
int 
INGwSpSipCallController::addEndPoint(const INGwSipEPInfo& p_SipEPInfo)
{
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::addEndPoint");
  int ret = m_SipEPInfoMap.addEP(p_SipEPInfo);

  if(ret == -1)
  {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                      "addEndPoint: "
                      "Adding SAS entry to map failed. Entry exist for : "
                      "SAS IP [%s] and CallId [%s]",
                       p_SipEPInfo.mEpHost, p_SipEPInfo.msLocalCallID.c_str());
  }
  else
  {
    logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, 
                      "addEndPoint: "
                      "Added SAS entry to map . Entry Details are : "
                      "SAS IP [%s] SAS PORT [%d] and CallId [%s]",
                       p_SipEPInfo.mEpHost, p_SipEPInfo.port, 
                       p_SipEPInfo.msLocalCallID.c_str());
  }

  LogINGwTrace(false, 0, "OUT INGwSpSipCallController::addEndPoint");
  return ret;
}


//This function will remove the entry from the map based on the p_EPHost.
//Retrun 0 if successful(entry found and removed) else return -1
int 
INGwSpSipCallController::removeEndPoint(std::string& p_EPHost, std::string& p_CallId)
{
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::removeEndPoint");
  int ret = m_SipEPInfoMap.removeEP(p_EPHost, p_CallId);

  if(ret == -1)
  {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                      "removeEndPoint: "
                      "Removing SAS entry from map failed. Entry does not exist for : "
                      "SAS  [%s]", p_EPHost.c_str());
  }
  else
  {
    logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, 
                      "removeEndPoint: "
                      "Removed SAS entry from map . Entry Details are : "
                      "SAS  [%s]", p_EPHost.c_str());
  }

  LogINGwTrace(false, 0, "OUT INGwSpSipCallController::removeEndPoint");
  return ret;
}


//This function will copy the end point info in the in/out parameter
//Retrun 0 if successful(entry found and copied) else return -1
int 
INGwSpSipCallController::getEndPoint(std::string& p_EPHost, 
                                     INGwSipEPInfo& p_SipEPInfo)
{
  LogINGwTrace(false, 0, "IN getEndPoint");
  int ret = m_SipEPInfoMap.getEP(p_EPHost, p_SipEPInfo);

  
  if(ret == -1)
  {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                      "getEndPoint: "
                      "Getting SAS entry from map failed. Entry does not exist for : "
                      "CallId [%s]",
                       p_EPHost.c_str());
   
    logger.logINGwMsg(false,ERROR_FLAG,0,"Yogesh registered SAS's ",
                      m_SipEPInfoMap.toLog().c_str());
  }
  else
  {
    logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, 
                      "getEndPoint: "
                      "Got SAS entry from map . Entry Details are : "
                      "SAS IP [%s] SAS PORT [%d] and CallId [%s]",
                       p_SipEPInfo.mEpHost, p_SipEPInfo.port, 
                       p_SipEPInfo.msLocalCallID.c_str());
  }

  LogINGwTrace(false, 0, "OUT getEndPoint");
  return ret;
}

//This function will update remote client port of EP info
//Retrun 0 if successful(entry found and updated) else return -1
int 
INGwSpSipCallController::updateEndPoint(std::string& p_EPHost, unsigned short p_Clientport)
{
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::updateEndPoint");
  int ret = m_SipEPInfoMap.updateEP(p_EPHost, p_Clientport);
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::updateEndPoint");
  return ret;
}

//This function will get the list of all registered End Point
//Retrun number of entry in the list
// List will have have EP object allocated, caller needs to free the memory
int 
INGwSpSipCallController::getEndPointList(t_EPInfoList& p_EPInfoList)
{
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::updateEndPoint");
  int ret = m_SipEPInfoMap.getEPList(p_EPInfoList);
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::updateEndPoint");
  return ret;
}

//
//When INVITE is recv
//
int 
INGwSpSipCallController::processSasServerRegistration(INGwSpSipConnection* p_Connection)
{
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::processSasServerRegistration");
  if(p_Connection == NULL)
    return -1;

  logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, 
                    "INGwSpSipCallController::processSasServerRegistration: "
                    "Got SAS registration request . Entry Details are : "
                    "SAS IP [%s] SAS PORT [%d] ",
                    p_Connection->getGwIPAddress(), p_Connection->getGwPort());

  int ret = -1;
  INGwSpData* pcSsnData = NULL;

  int resCode = 500;
  g_TransitObj transitObj;
	transitObj.m_causeCode = G_FAILURE;

	//CHeck if IP addr if for registered SAS
  std::string lSasIp = p_Connection->getGwIPAddress();
  bool lIsRegistered = 
			INGwIfrPrParamRepository::getInstance().isHostPresentInSasList(lSasIp);

  if(lIsRegistered)
	{
		// Find if entry already present in EPMap
		INGwSipEPInfo lSipEPInfo;
	  int lEntryPresent = getEndPoint(lSasIp, lSipEPInfo);

    if(lEntryPresent != -1)
		{
			// Entry present, Multiple registration request from same Host
			transitObj.m_causeCode = G_SUCCESS;

      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                        "INGwSpSipCallController::processSasServerRegistration: "
                        "Multiple registration request recvd from same host "
                        "IP [%s] PORT [%d]. CLEANING previous SAS Registration INFO . ",
                        p_Connection->getGwIPAddress(), p_Connection->getGwPort());

      ret = cleanSasEpRegistration(lSipEPInfo);
		}

    INGwSpSipIface::t_INGwSipIfaceRequestType requestType = 
	    												 INGwSpSipIface::SAS_REQUEST_SERVER_REGISTER_INIT;

    m_SipIface->processSasMgmtRequest(requestType, transitObj);

    if (transitObj.m_causeCode != G_SUCCESS)
    {
      // Service not available at this time
      resCode = 503;
      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                        "INGwSpSipCallController::processSasServerRegistration: "
                        "Receive Failure response for PC-SSN list for "
                        "SAS IP [%s] SAS PORT [%d] registration",
                        p_Connection->getGwIPAddress(), p_Connection->getGwPort());
    }
    else
    {
      // Extract SDP
      // Make a sipdata body and call the required method on info handler.
      if(transitObj.m_buf && transitObj.m_bufLen)
      {
        pcSsnData = INGwSpDataFactory::getInstance().getNewObject();
        pcSsnData->setBody((const char *)transitObj.m_buf, transitObj.m_bufLen);
        pcSsnData->setBodyLength(transitObj.m_bufLen);
        pcSsnData->setBodyType(INGW_MSG_BODYTYPE_TCAP);
        resCode = 200;
        logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                          "INGwSpSipCallController::processSasServerRegistration: "
                          "Receive PC-SSN BODY for "
                          "SAS IP [%s] SAS PORT [%d] registration : %s ",
                          p_Connection->getGwIPAddress(), p_Connection->getGwPort(),
					  							(const char *)transitObj.m_buf);
        //Yogesh 
        //deallocating the char buffer here as there is no need to keep the sipData body along with 
        //reserved pool of INGwSpBody.Is it required in fast_ operations?
        delete [] transitObj.m_buf;
        transitObj.m_buf = 0;
      }
    }
  }
	else
	{
		resCode = 403;
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                      "INGwSpSipCallController::processSasServerRegistration: "
                      "Request recvd from unregisteded host"
                      "IP [%s] PORT [%d] ",
                      p_Connection->getGwIPAddress(), p_Connection->getGwPort());
	}

  logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, 
                    "INGwSpSipCallController::processSasServerRegistration: "
                    "Sending Response [%d] for Request from : "
                    "SAS IP [%s] SAS PORT [%d] ",
                    resCode, p_Connection->getGwIPAddress(), p_Connection->getGwPort());

  ret = p_Connection->mSendResponse(INGW_SIP_METHOD_TYPE_INVITE,
                                    pcSsnData, resCode);
  // Reclaim the sipdata
  if(pcSsnData)
    INGwSpDataFactory::getInstance().reuseObject(pcSsnData); 

  LogINGwTrace(false, 0, "OUT INGwSpSipCallController::processSasServerRegistration");
  return ret;
}

//
//When BYE is recv
//
int 
INGwSpSipCallController::processSasServerDeregistration(INGwSpSipConnection* p_Connection)
{
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::processSasServerDeregistration");
  if(p_Connection == NULL)
    return -1;

  logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, 
                    "INGwSpSipCallController::processSasServerDeregistration: "
                    "Got SAS deregistration request . Entry Details are : "
                    "SAS IP [%s] SAS PORT [%d] ",
                    p_Connection->getGwIPAddress(), p_Connection->getGwPort());

  int ret = -1;

  int resCode = 500;
  g_TransitObj transitObj;
	transitObj.m_causeCode = G_FAILURE;
	transitObj.m_sasIp = std::string(p_Connection->getGwIPAddress());


	// CALL IWF
  INGwSpSipIface::t_INGwSipIfaceRequestType requestType = 
													 INGwSpSipIface::SAS_REQUEST_SERVER_DEREGISTER;

  m_SipIface->processSasMgmtRequest(requestType, transitObj);

  if (transitObj.m_causeCode != G_SUCCESS)
  {
    // IWF can send error for deregistration request
		// we will continue with sip call cleanup
		//Send response code 200
    resCode = 200;
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                      "INGwSpSipCallController::processSasServerDeregistration "
                      "Receive Failure response from IWF but continuing "
                      "SAS IP [%s] SAS PORT [%d] deregistration",
                      p_Connection->getGwIPAddress(), p_Connection->getGwPort());
  }
  else
  {
    resCode = 200;
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                      "INGwSpSipCallController::processSasServerDeregistration "
                      "Successfully deregistered SAS Server with IWF "
                      "SAS IP [%s] SAS PORT [%d] ",
                      p_Connection->getGwIPAddress(), p_Connection->getGwPort());
  }

  logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, 
                    "INGwSpSipCallController::processSasServerDeregistration "
                    "Sending Response [%d] for deregister Request from : "
                    "SAS IP [%s] SAS PORT [%d] ",
                    resCode, p_Connection->getGwIPAddress(), p_Connection->getGwPort());

  INGwSpData* pcSsnData = NULL;
  ret = p_Connection->mSendResponse(INGW_SIP_METHOD_TYPE_BYE,
                                    pcSsnData, resCode);
  LogINGwTrace(false, 0, "OUT INGwSpSipCallController::processSasServerRegistration");
  return ret;
}

//
//When INFO is recv
//
int 
INGwSpSipCallController::processSasAppMgmtRequest(INGwSpSipConnection* p_Connection, 
														                  const void* p_Sdp, const int p_SdpLen)
{
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::processSasAppMgmtRequest");
  if(p_Connection == NULL)
    return -1;

  logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, 
                    "INGwSpSipCallController::processSasAppMgmtRequest: "
                    "Got SAS registration MGMT request . Entry Details are : "
                    "SAS IP [%s] SAS PORT [%d] ",
                    p_Connection->getGwIPAddress(), p_Connection->getGwPort());

  int ret = -1;

  int resCode = 500;
  g_TransitObj transitObj;
	transitObj.m_causeCode = G_FAILURE;
  //yogesh changed
	//transitObj.m_buf = new char[p_SdpLen + 1];
	transitObj.m_buf = new unsigned char[p_SdpLen];
	transitObj.m_bufLen = p_SdpLen;
	memcpy((void*)transitObj.m_buf, (void*)p_Sdp, transitObj.m_bufLen);
	//transitObj.m_buf[transitObj.m_bufLen] = '\0';
  transitObj.m_sasIp = std::string(p_Connection->getGwIPAddress());

	//Call IWF
  INGwSpSipIface::t_INGwSipIfaceRequestType requestType = 
													 INGwSpSipIface::SAS_REQUEST_APP_MGMT;

  m_SipIface->processSasMgmtRequest(requestType, transitObj);

  bool lIsSendInfo = false;

	switch(transitObj.m_causeCode)
	{
		case G_INVALID_OPC_SSN :
		  {
				// SSN not registered with EMS
				resCode = 404;
		  }
		  break;
		case G_REG_INPROGRESS :
		case G_DEREG_INPROGRESS :
		  {
				//IWF will call another API for sending INFO
				// Just Send 200 OK now
				resCode = 200;
		  }
		  break;
		case G_ALREADY_REG :
		case G_ALREADY_DEREG :
		  {
				//PC-SSN already registered/deregirtered
				//Send 200 OK and INFO now
        if(transitObj.m_buf && transitObj.m_bufLen)
        {
          resCode = 200;
					lIsSendInfo = true;
		    }
      }
		  break;
		case G_BAD_REQUEST :
		  {
				// Bad request
				resCode = 400;
		  }
		  break;
		default :
				// Server Error
				resCode = 500;
	}

  logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, 
                    "INGwSpSipCallController::processSasAppMgmtRequest: "
                    "Sending Response [%d] for app MGMT Request from : "
                    "SAS IP [%s] SAS PORT [%d] ",
                    resCode, p_Connection->getGwIPAddress(), p_Connection->getGwPort());

  ret = p_Connection->mSendResponse(INGW_SIP_METHOD_TYPE_INFO,
                                    NULL, resCode);

  if(ret != -1)
	{
		if(lIsSendInfo)
		{
			ret = handleSasAppMgmtResponse(transitObj);
		}
		else
		{
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                        "INGwSpSipCallController::processSasAppMgmtRequest: "
                        "Request proccessing in progress"
												" Not send INFO now for "
                        "SAS IP [%s] SAS PORT [%d]",
                        p_Connection->getGwIPAddress(), p_Connection->getGwPort());
		}
  }
	else
	{
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                      "INGwSpSipCallController::processSasAppMgmtRequest: "
                      "ERROR Sending Response [%d] for app MGMT Request from : "
                      "SAS IP [%s] SAS PORT [%d] ",
                      resCode, p_Connection->getGwIPAddress(), p_Connection->getGwPort());
	}

	delete [] transitObj.m_buf;
  transitObj.m_buf = NULL;
  LogINGwTrace(false, 0, "OUT INGwSpSipCallController::processSasAppMgmtRequest");
  return ret;
}


//
//When NOTIFY is recv
//
int 
INGwSpSipCallController::processOutboundMsg(INGwSpSipConnection* p_Connection,
													                  const void* p_Sdp, const int p_SdpLen, int p_seqNum)
{
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::processOutboundMsg"); 
  if(p_Connection == NULL) {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
           "processOutboundMsg(): Connection NULL");
    return -1;
  }

  int ret = -1;
  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "processOutboundMsg(): "
         "SAS App Req. Sending Host Details: "
         "IP[%s] PORT[%d] p_SdpLen<%d> seqNum<%d>",
         p_Connection->getGwIPAddress(), p_Connection->getGwPort(),
         p_SdpLen, p_seqNum);

  int resCode = 500;

	// Find if entry already present in EPMap
	std::string lSasIp = p_Connection->getGwIPAddress();
	INGwSipEPInfo lSipEPInfo;
	int lEntryPresent = getEndPoint(lSasIp, lSipEPInfo);

	if((lEntryPresent == -1) || (p_SdpLen == 0))
	{
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "processOutboundMsg(): "
           "p_SdpLen<%d> or App Req from Non-registered server: "
           "IP[%s] PORT[%d]. Sending failure response.",
           p_SdpLen, p_Connection->getGwIPAddress(), p_Connection->getGwPort());
    resCode = 481;
	}
	else
	{
    int llengthRxContent = p_SdpLen;
    g_TransitObj transitObj;
  	transitObj.m_causeCode = G_FAILURE;
    //Yogesh changed \r\n check
    if(p_SdpLen >= 2){
      unsigned char* lbuff = (unsigned char*)p_Sdp;
      if((lbuff[p_SdpLen-2] == 0x0D) && (lbuff[p_SdpLen-1] == 0x0A)){
        llengthRxContent = p_SdpLen - 2;
      }
    } 
  	transitObj.m_buf = new unsigned char[p_SdpLen];
 
  	transitObj.m_bufLen = llengthRxContent;
  	memcpy((void*)transitObj.m_buf, p_Sdp, transitObj.m_bufLen);
  	//transitObj.m_buf[transitObj.m_bufLen] = '\0';
    transitObj.m_sasIp = std::string(p_Connection->getGwIPAddress());

	  //Call IWF
    transitObj.m_seqNum = p_seqNum;
    m_SipIface->processOutboundMsg(transitObj);

    logger.logINGwMsg(false,VERBOSE_FLAG,0,"TC-Seq <%d> retransmission m_causeCode <%d>",
      transitObj.m_seqNum, transitObj.m_causeCode);
  	switch(transitObj.m_causeCode)
  	{
  		case G_BAD_REQUEST :
  		{
  			// Bad request
  			resCode = 400;
  		}
  		break;

  		case G_SUCCESS :
		  {
				// Success
				resCode = 200;
		  }
		  break;
  
      case 499 :
      {
        logger.logINGwMsg(false,ERROR_FLAG,0,"retransmission detected");
        resCode = 499;
      }
      break;
		  default :
				// Server Error
				resCode = 500;
  	}

    p_Connection->setGwPort(lSipEPInfo.port);
    if(NULL != transitObj.m_buf){
    delete [] transitObj.m_buf;
    }
	}

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                    "INGwSpSipCallController::processOutboundMsg: "
                    "Sending Response [%d] for app MGMT Request from : "
                    "SAS IP [%s] SAS PORT [%d] ",
                    resCode, p_Connection->getGwIPAddress(), p_Connection->getGwPort());


  ret = p_Connection->mSendResponse(INGW_SIP_METHOD_TYPE_NOTIFY,
                                    NULL, resCode);

  LogINGwTrace(false, 0, "OUT INGwSpSipCallController::processOutboundMsg");
  return ret;
}

//
//When notify needed to be sent
//
int 
INGwSpSipCallController::handleInboundMsg(g_TransitObj  &p_transitObj)
{
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::handleInboundMsg");
	int ret = -1;
#ifndef QUEUE_CHANGES
	INGwIfrMgrWorkUnit* lWorkUnit = NULL;
#endif

#ifdef INGW_TRACE_CALL_THREAD
  logger.logINGwMsg(false,VERBOSE_FLAG,0,"handleInboundMsg +THREAD+ <%d>",
                    p_transitObj.m_stackDlgId);
#endif

  if(p_transitObj.m_buf && p_transitObj.m_bufLen)
  {
	  INGwSipEPInfo lSipEPInfo;
    if( -1 == getEndPoint(p_transitObj.m_sasIp, lSipEPInfo))
	  {
      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
			  								"INGwSpSipCallController::handleInboundMsg"
			  								"Entry for SAS [%s] not found in SAS MAP "
												"May be server is not registered anymore. Returning failure.",
			  								p_transitObj.m_sasIp.c_str());

      p_transitObj.m_causeCode = G_FAILURE;
	  }
	  else
	  {
			INGwSpSipProvider& l_bpSipProvider = INGwSpSipProvider::getInstance();
			char lCallId[MAX_CALLID_LEN];

#ifndef QUEUE_CHANGES
  		lWorkUnit = new INGwIfrMgrWorkUnit;
#endif
			l_bpSipProvider.generateCallid(lCallId, p_transitObj.m_threadIdx);

      logger.logINGwMsg(false,VERBOSE_FLAG,0," ThreadIdx <%d>",
                        p_transitObj.m_threadIdx);  

			char* l_callId = new char[strlen(lCallId) + 1];
      strcpy(l_callId,lCallId);

#ifndef QUEUE_CHANGES
  		lWorkUnit->meWorkType = INGwIfrMgrWorkUnit::SIP_SEND_NOTIFY_REQ;
  		lWorkUnit->mpcCallId = l_callId;
  		lWorkUnit->mpWorkerClbk = this;

      //comment the line below
  		lWorkUnit->getHash();
#endif
	  	g_TransitObj* lContextData = new g_TransitObj;
	  	lContextData->m_bufLen = p_transitObj.m_bufLen;
      lContextData->m_buf = p_transitObj.m_buf;
    
      lContextData->m_buf = p_transitObj.m_buf;
	  	lContextData->m_sasIp = std::string(p_transitObj.m_sasIp);

			//Storing Tcap information for handling failure
			lContextData->m_stackDlgId = p_transitObj.m_stackDlgId;
			lContextData->m_seqNum = p_transitObj.m_seqNum;
			lContextData->m_appId = p_transitObj.m_appId;
			lContextData->m_instId = p_transitObj.m_instId;
      lContextData->m_suId = p_transitObj.m_suId;
      lContextData->m_spId = p_transitObj.m_spId; 
      lContextData->m_billingNo = p_transitObj.m_billingNo; 
			lContextData->m_isDialogueComplete = p_transitObj.m_isDialogueComplete;

#ifndef QUEUE_CHANGES
	  	lWorkUnit->mpContextData = (void*)lContextData;
#endif
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                        "INGwSpSipCallController::handleInboundMsg: "
                        "Receive Response BODY for "
                        "SAS IP [%s] m_buf address[%x]",
                        p_transitObj.m_sasIp.c_str(), 
									    	p_transitObj.m_buf);

            
      logger.logINGwMsg(false,VERBOSE_FLAG,0,
      "+rem+ before postMsg +conn+ <%x>",
      INGwSpSipProvider::getInstance().getThreadSpecificSipData().conn);

#ifndef QUEUE_CHANGES
			ret = INGwIfrMgrThreadMgr::getInstance().postMsg(lWorkUnit);
#else
//#define LOOPBACK_1

#ifdef LOOPBACK_1

  unsigned char lpcResp[] =
  { 
    0x01, 0x06, 0x0D, 0x02, 0x00, 0x00, 0x00, 0x00, 
    0x1D, 0x19, 0x02, 0x23, 0x03, 0x22, 0x01, 0x1E, 0x01, 0x1F, 0x01, 
    0x01, 0x20, 0x02, 0x21, 0x0B, 0x30, 0x09, 0x8f, 0x07, 0x03, 0x10,
    0x12, 0x84, 0x44, 0x21, 0x21
  };

  memcpy(lpcResp + 4,(void*)&(p_transitObj.m_stackDlgId),sizeof(int));

  g_TransitObj transitObj;
  transitObj.m_causeCode = G_FAILURE;

  transitObj.m_bufLen = 35;

  transitObj.m_buf = new unsigned char[transitObj.m_bufLen];
  memcpy((void*)transitObj.m_buf, lpcResp, transitObj.m_bufLen);

  transitObj.m_sasIp  = "10.32.25.4";
  transitObj.m_seqNum = 2;

  int lioffset = 2;

  if (lContextData->m_buf[1] & 80){
    lioffset += (lContextData->m_buf[1] & 0x7F);
  } 

  //responding begin by end
  if(lContextData->m_buf[lioffset] == 0x0B){
    m_SipIface->processOutboundMsg(transitObj);
  }

  delete [] transitObj.m_buf;
  return  G_SUCCESS; 
#endif


			ret = sendNotifyRequest(*lContextData, l_callId);
      delete [] l_callId;

      logger.logINGwMsg(false,VERBOSE_FLAG,0,
        "+rem+ after sendNotifyRequest +conn+ <%x>",
        INGwSpSipProvider::getInstance().getThreadSpecificSipData().conn);

			delete lContextData;
#endif

      p_transitObj.m_causeCode = G_SUCCESS;
			
    }
  }
	else
	{
    p_transitObj.m_causeCode = G_FAILURE;
    ret = G_FAILURE;
	}

  ret = (0 == ret)?G_SUCCESS:G_FAILURE;
  logger.logINGwMsg(false,TRACE_FLAG,0,"+rem+ handleInboundMsg retVal<%d>",ret);

  LogINGwTrace(false, 0, "OUT INGwSpSipCallController::handleInboundMsg");
	return ret;

}

//
//When Replay notify is needed to be sent
//
int 
INGwSpSipCallController::handleReplayInboundMsg(g_TransitObj  &p_transitObj)
{
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::handleReplayInboundMsg");
	int ret = -1;
	INGwIfrMgrWorkUnit* lWorkUnit = NULL;

#ifdef INGW_TRACE_CALL_THREAD
  logger.logINGwMsg(false,VERBOSE_FLAG,0,"handleReplayInboundMsg +THREAD+ <%d>",
                    p_transitObj.m_stackDlgId);
#endif

  if(p_transitObj.m_buf && p_transitObj.m_bufLen)
  {
	  INGwSipEPInfo lSipEPInfo;
    if( -1 == getEndPoint(p_transitObj.m_sasIp, lSipEPInfo))
	  {
      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
			  								"INGwSpSipCallController::handleReplayInboundMsg"
			  								"Entry for SAS [%s] not found in SAS MAP "
												"May be server is not registered anymore. Returning failure.",
			  								p_transitObj.m_sasIp.c_str());

      p_transitObj.m_causeCode = G_FAILURE;
      ret = G_FAILURE;
	  }
	  else
	  {
			INGwSpSipProvider& l_bpSipProvider = INGwSpSipProvider::getInstance();
#ifndef QUEUE_CHANGES
  		lWorkUnit = new INGwIfrMgrWorkUnit;
#endif

			char lCallId[MAX_CALLID_LEN];

      //int lReplayThreadIdx =  INGwIfrMgrThreadMgr::getInstance().
      //                        getReplayThreadIdx(p_transitObj.m_stackDlgId);
 
      int lReplayThreadIdx = 
      INGwIfrMgrThreadMgr::getInstance().getThreadSpecificData().getThreadIdx();
      
			l_bpSipProvider.generateCallid(lCallId, lReplayThreadIdx);
      
			char* l_callId = new char[strlen(lCallId) + 1];
  		strcpy(l_callId,  lCallId);

#ifndef QUEUE_CHANGES
  		lWorkUnit->meWorkType = INGwIfrMgrWorkUnit::SIP_SEND_NOTIFY_REQ;
  		lWorkUnit->mpcCallId = l_callId;
  		lWorkUnit->mpWorkerClbk = this;
	  	lWorkUnit->mpContextData = (void*)(&p_transitObj);
#endif    
	  	p_transitObj.m_sasIp = std::string(p_transitObj.m_sasIp);


      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                        "INGwSpSipCallController::handleReplayInboundMsg: "
                        "Receive Response BODY for "
                        "SAS IP [%s] m_buf address[%x]",
                        p_transitObj.m_sasIp.c_str(), 
									    	p_transitObj.m_buf);


#ifndef QUEUE_CHANGES
			ret = INGwIfrMgrThreadMgr::getInstance().postMsg(lWorkUnit);
#else
			ret = sendNotifyRequest(p_transitObj, l_callId);
      delete [] l_callId;

      logger.logINGwMsg(false,VERBOSE_FLAG,0,
        "+rem+ after sendNotifyRequest +conn+ <%x>",
        INGwSpSipProvider::getInstance().getThreadSpecificSipData().conn);

			delete (&p_transitObj);
#endif
#if 0
      p_transitObj.m_causeCode = G_SUCCESS;
#endif
     
    }
  }
	else
	{
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,"handleReplayInboundMsg"
                      " Invalid Buffer len<%d> addr<%x>",p_transitObj.m_bufLen,
                       p_transitObj.m_buf);

    p_transitObj.m_causeCode = G_FAILURE;
	}

  ret = (0 == ret)?G_SUCCESS:G_FAILURE;
  LogINGwTrace(false, 0, "OUT INGwSpSipCallController::handleReplayInboundMsg");
	return ret;
}

int
INGwSpSipCallController::sendNotifyRequest(g_TransitObj  &p_transitObj, char* p_CallId)
{
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::sendNotifyRequest");
  int ret = 0;
#ifdef INGW_TRACE_CALL_THREAD
  logger.logINGwMsg(false,VERBOSE_FLAG,0,"sendNotifyRequest +THREAD+ <%d>",
                    p_transitObj.m_stackDlgId);
#endif
	INGwSipEPInfo lSipEPInfo;
  if( -1 == getEndPoint(p_transitObj.m_sasIp, lSipEPInfo))
	{
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
											"INGwSpSipCallController::sendNotifyRequest"
											"Entry for SAS [%s] not found in SAS MAP",
											p_transitObj.m_sasIp.c_str());

    p_transitObj.m_causeCode = G_FAILURE;
	  return -1;
	}

	// Create a call and a connection object, and set up the links between the
	// call, connection,

  std::string lCallIdStr = p_CallId;

#ifdef INGW_EXTRA_LOGS
  logger.logINGwMsg(false,VERBOSE_FLAG,0,"+rem+ In sendNotifyRequest 1 +conn+ <%x>",
                    INGwSpSipProvider::getInstance().getThreadSpecificSipData().conn);
#endif

	INGwSpSipCall* tmpSipCall = dynamic_cast<INGwSpSipCall*>(
				 INGwSpSipProvider::getInstance().getNewCall(lCallIdStr, 
																										 true));
  INGwIfrUtlRefCount_var call_var(tmpSipCall);

#ifdef INGW_EXTRA_LOGS
  logger.logINGwMsg(false,VERBOSE_FLAG,0,"+rem+ In sendNotifyRequest 2 +conn+ <%x>",
                    INGwSpSipProvider::getInstance().getThreadSpecificSipData().conn);
#endif

	INGwSpSipConnection *tmpSipConnection = dynamic_cast<INGwSpSipConnection*>(
					INGwSpSipProvider::getInstance().getNewConnection(*tmpSipCall));
  tmpSipConnection->setLocalCallId(p_CallId);
  INGwIfrUtlRefCount_var con_var(tmpSipConnection);

#ifdef INGW_EXTRA_LOGS
  logger.logINGwMsg(false,VERBOSE_FLAG,0,"+rem+ In sendNotifyRequest 3 +conn+ <%x>",
                    INGwSpSipProvider::getInstance().getThreadSpecificSipData().conn);
#endif

	tmpSipCall->addConnection((INGwSpSipConnection *)tmpSipConnection);
	//Now Call has Conn Ref and Conn has Call Ref.

	tmpSipConnection->setGwIPAddress(lSipEPInfo.mEpHost, 
																	 strlen(lSipEPInfo.mEpHost));

	tmpSipConnection->setGwPort((int)lSipEPInfo.port);

	tmpSipConnection->setActiveEPPort((int)lSipEPInfo.clientport);

	tmpSipConnection->setContactAddSpec(lSipEPInfo.mContactAddSpec);
	tmpSipConnection->setFromAddSpec(lSipEPInfo.mFromAddSpec);

	// Set Tcap tranx info
	INGwSpSipConnTcapTransInfo  lTcapTransInfo;

	lTcapTransInfo.m_stackDlgId = p_transitObj.m_stackDlgId;
	lTcapTransInfo.m_suId       = p_transitObj.m_suId;
	lTcapTransInfo.m_spId       = p_transitObj.m_spId;
	lTcapTransInfo.m_isDialogueComplete = p_transitObj.m_isDialogueComplete;
  lTcapTransInfo.m_seqNum     = p_transitObj.m_seqNum;

  logger.logINGwMsg(false, TRACE_FLAG,imERR_NONE,"+cleanDlg+ +rem+ storing m_suId <%d>"
    " m_spId <%d>",lTcapTransInfo.m_suId, lTcapTransInfo.m_spId); 

  tmpSipConnection->setTcapTransInfo(lTcapTransInfo);


#ifdef INGW_EXTRA_LOGS
  logger.logINGwMsg(false,VERBOSE_FLAG,0,"+rem+ before initInboundMsgConn 2 +conn+ <%x>",
                    INGwSpSipProvider::getInstance().getThreadSpecificSipData().conn);
#endif

	INGwSpSipProvider::getInstance().initInboundMsgConn(tmpSipConnection);


#ifdef INGW_EXTRA_LOGS
  logger.logINGwMsg(false,VERBOSE_FLAG,0,"+rem+ after initInboundMsgConn 2 +conn+ <%x>",
                    INGwSpSipProvider::getInstance().getThreadSpecificSipData().conn);
#endif

  // Extract SDP
  // Make a sipdata body and call the required method on info handler.
  if(p_transitObj.m_buf && p_transitObj.m_bufLen)
  {
		INGwSpData* pcSsnData = NULL;
    pcSsnData = INGwSpDataFactory::getInstance().getNewObject();
    pcSsnData->setBody((const char *)p_transitObj.m_buf, p_transitObj.m_bufLen);
    pcSsnData->setBodyLength(p_transitObj.m_bufLen);
    pcSsnData->setBodyType(INGW_MSG_BODYTYPE_TCAP);
    pcSsnData->setDialogueId(p_transitObj.m_stackDlgId);
    pcSsnData->setSeqNum(p_transitObj.m_seqNum);
    pcSsnData->setBillingId(p_transitObj.m_billingNo);

    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                      "m_billingNo %d",p_transitObj.m_billingNo);

    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                      "INGwSpSipCallController::sendNotifyRequest: "
                      "Sending NOTIFY BODY for "
                      "SAS IP [%s] Call Id [%s] Tcap Body : %s ",
                      p_transitObj.m_sasIp.c_str(), p_CallId,
									    (const char *)p_transitObj.m_buf);
    ret = 0;
    ret = tmpSipConnection->mSendRequest(INGW_SIP_METHOD_TYPE_NOTIFY, pcSsnData, NULL);
    
    //safe to delete
		delete [] p_transitObj.m_buf;
		p_transitObj.m_buf = NULL;
		p_transitObj.m_bufLen = 0;

    // Reclaim the sipdata
    //safe to reuse
    if(pcSsnData)
      INGwSpDataFactory::getInstance().reuseObject(pcSsnData); 
	} // end of if [p_transitObj.m_buf && p_transitObj.m_bufLen]

#ifdef INGW_EXTRA_LOGS
  logger.logINGwMsg(false,VERBOSE_FLAG,0,"+rem+ Notify sent+conn+ <%x>",
                    INGwSpSipProvider::getInstance().getThreadSpecificSipData().conn);
#endif

  LogINGwTrace(false, 0, "OUT INGwSpSipCallController::sendNotifyRequest");
  return ret;
}

//
//When INFO needed to be sent
//
int 
INGwSpSipCallController::handleSasAppMgmtResponse(g_TransitObj  &p_transitObj)
{
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::handleSasAppMgmtResponse");
	int ret = -1;
	INGwIfrMgrWorkUnit* lWorkUnit = NULL;

  if(p_transitObj.m_buf && p_transitObj.m_bufLen)
  {
	  INGwSipEPInfo lSipEPInfo;
    if( -1 == getEndPoint(p_transitObj.m_sasIp, lSipEPInfo))
	  {
      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
			  								"INGwSpSipCallController::handleSasAppMgmtResponse"
			  								"Entry for SAS [%s] not found in SAS MAP",
			  								p_transitObj.m_sasIp.c_str());

      p_transitObj.m_causeCode = G_FAILURE;
	  }
	  else
	  {
			
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE,
			  								"INGwSpSipCallController::handleSasAppMgmtResponse"
                        " In else after returning from getEndPoint 1"); 
      char* l_callId = new char[lSipEPInfo.msLocalCallID.length() + 1];
  		strcpy(l_callId,  lSipEPInfo.msLocalCallID.c_str());
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE,
			  								"INGwSpSipCallController::handleSasAppMgmtResponse"
                        "lSipEPInfo.msLocalCallID.c_str() = %s",
                         lSipEPInfo.msLocalCallID.c_str());
  		lWorkUnit = new INGwIfrMgrWorkUnit;
  		lWorkUnit->meWorkType = INGwIfrMgrWorkUnit::SIP_SEND_INFO_REQ;
  		lWorkUnit->mpcCallId = l_callId;
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE,
			  								"INGwSpSipCallController::handleSasAppMgmtResponse"
                        " In else after returning from getEndPoint 2");  
  		lWorkUnit->mpWorkerClbk = this;
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE,
			  								"INGwSpSipCallController::handleSasAppMgmtResponse"
                        " In else after returning from getEndPoint 3");  
  		lWorkUnit->getHash();
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE,
			  								"INGwSpSipCallController::handleSasAppMgmtResponse"
                        " In else after returning from getEndPoint 4"); 

	  	g_TransitObj* lContextData = new g_TransitObj;
	  	lContextData->m_bufLen = p_transitObj.m_bufLen;
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE,
			  								"INGwSpSipCallController::handleSasAppMgmtResponse"
                        " In else after returning from getEndPoint 5");  
      

      lContextData->m_buf = new unsigned char [lContextData->m_bufLen];
      memcpy((void*)lContextData->m_buf, (void*)p_transitObj.m_buf, 
                                          lContextData->m_bufLen);
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE,
			  								"INGwSpSipCallController::handleSasAppMgmtResponse"
                        " In else after returning from getEndPoinT 6"); 
      //lContextData->m_buf[lContextData->m_bufLen] = '\0';

	  	//lContextData->m_buf = p_transitObj.m_buf;
	  	lContextData->m_sasIp = std::string(p_transitObj.m_sasIp);

	  	lWorkUnit->mpContextData = (void*)lContextData;
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE,
			  								"INGwSpSipCallController::handleSasAppMgmtResponse"
                        " In else after returning from getEndPoint 7");  
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                        "INGwSpSipCallController::handleSasAppMgmtResponse: "
                        "Receive Response BODY for "
                        "SAS IP [%s]",
                        p_transitObj.m_sasIp.c_str());

      //yogesh delete the buffer allocated from TcapMessage 
      delete [] p_transitObj.m_buf; 
      p_transitObj.m_buf = 0;
      p_transitObj.m_bufLen = 0;
      
			ret = INGwIfrMgrThreadMgr::getInstance().postMsg(lWorkUnit);
      p_transitObj.m_causeCode = G_SUCCESS;
      
    }
  }
	else
	{
    p_transitObj.m_causeCode = G_FAILURE;
	}

  LogINGwTrace(false, 0, "OUT INGwSpSipCallController::handleSasAppMgmtResponse");
	return ret;
}

int
INGwSpSipCallController::sendInfoRequest(g_TransitObj  &p_transitObj)
{
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::sendInfoRequest");
  int ret = 0;

	INGwSipEPInfo lSipEPInfo;
  if( -1 == getEndPoint(p_transitObj.m_sasIp, lSipEPInfo))
	{
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
											"INGwSpSipCallController::sendInfoRequest"
											"Entry for SAS [%s] not found in SAS MAP",
											p_transitObj.m_sasIp.c_str());

    p_transitObj.m_causeCode = G_FAILURE;
	}
	else
	{
		INGwSpSipCall* lSipCall = getCall(lSipEPInfo.msLocalCallID);

		if(lSipCall == NULL)
		{
			ret = -1;
			logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
				  							"INGwSpSipCallController::sendInfoRequest"
				  							"Entry for SIP call [%s] not found in Call MAP",
				  							p_transitObj.m_sasIp.c_str());
		}
		else
		{
			INGwIfrUtlRefCount_var callRef(lSipCall);

			//Now we have inly one connection in Call with ID 0
			int connId = 0;
			INGwSpSipConnection* lSipConn = lSipCall->getConnection(connId);
			if(lSipConn == NULL)
			{
				ret = -1;
			  logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
			    								"INGwSpSipCallController::sendInfoRequest"
			    								"SIP connection for SAS [%s] not found in Call ",
			    								p_transitObj.m_sasIp.c_str());
			}
			else
			{
				INGwIfrUtlRefCount_var connRef(lSipConn);

        // Extract SDP
        // Make a sipdata body and call the required method on info handler.
        if(p_transitObj.m_buf && p_transitObj.m_bufLen)
        {
					INGwSpData* pcSsnData = NULL;
          pcSsnData = INGwSpDataFactory::getInstance().getNewObject();
          pcSsnData->setBody((const char *)p_transitObj.m_buf, p_transitObj.m_bufLen);
          pcSsnData->setBodyLength(p_transitObj.m_bufLen);
          pcSsnData->setBodyType(INGW_MSG_BODYTYPE_TCAP);

          logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                            "INGwSpSipCallController::sendInfoRequest: "
                            "Sending INFO Response BODY for "
                            "SAS IP [%s] SAS PORT [%d] mgmt request : %s ",
                            lSipConn->getGwIPAddress(), lSipConn->getGwPort(),
									    			(const char *)p_transitObj.m_buf);
          ret = 0;
          ret = lSipConn->mSendRequest(INGW_SIP_METHOD_TYPE_INFO, pcSsnData, NULL);

					//If return value is RETRY, again Enqueue the work unit
          if(ret == INVALID_STATE_RETRY)
          {
	          INGwIfrMgrWorkUnit* lWorkUnit = NULL;
			      char* l_callId = new char[lSipEPInfo.msLocalCallID.length() + 1];
        		strcpy(l_callId,  lSipEPInfo.msLocalCallID.c_str());

  	      	lWorkUnit = new INGwIfrMgrWorkUnit;
        		lWorkUnit->meWorkType = INGwIfrMgrWorkUnit::SIP_SEND_INFO_REQ;
  	      	lWorkUnit->mpcCallId = l_callId;
  	      	lWorkUnit->mpWorkerClbk = this;
        		lWorkUnit->getHash();

	        	g_TransitObj* lContextData = new g_TransitObj;
	  	      lContextData->m_bufLen = p_transitObj.m_bufLen;
            //Yogesh changed
            lContextData->m_buf = new unsigned char[lContextData->m_bufLen];
            memcpy((void*)lContextData->m_buf, (void*)p_transitObj.m_buf, 
                                          lContextData->m_bufLen);
	  	      
            lContextData->m_sasIp = std::string(p_transitObj.m_sasIp);

      	  	lWorkUnit->mpContextData = (void*)lContextData;

            logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                              "INGwSpSipCallController::sendInfoRequest: "
                              "Send Request returns INVALID_STATE_RETRY . "
															"Reposting the WU to try again");

      			ret = INGwIfrMgrThreadMgr::getInstance().postMsg(lWorkUnit);

          }

					delete [] p_transitObj.m_buf;
					p_transitObj.m_buf = NULL;
					p_transitObj.m_bufLen = 0;

          // Reclaim the sipdata
          if(pcSsnData)
            INGwSpDataFactory::getInstance().reuseObject(pcSsnData); 

		    } // end of if [p_transitObj.m_buf && p_transitObj.m_bufLen]

			} // end of else of if [lSipConn == NULL]

		} // end of else of if [lSipCall == NULL]

	} // end of else of if [getEPInfo == -1]


	if(ret == -1)
	{
		p_transitObj.m_causeCode = G_FAILURE;
	}
	else
	{
		p_transitObj.m_causeCode = G_SUCCESS;
	}

  LogINGwTrace(false, 0, "OUT INGwSpSipCallController::sendInfoRequest");
  return ret;
}


INGwSpSipCallController::INGwSpSipCallController(INGwSpSipIface* p_SipIface)
{
	m_SipIface = p_SipIface;
  //INCTBD - Temporary logging for debugging

  getNwAliveTime(); 
  logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
         "INGwSpSipCallController::INGwSpSipCallController(INGwSpSipIface*): m_SipIface<%x> mNwAliveTime<%d>", m_SipIface,mNwAliveTime);
}

INGwSpSipCallController::INGwSpSipCallController(const INGwSpSipCallController& selfObj)
{
	m_SipIface = selfObj.m_SipIface;
  //INCTBD - Temporary logging for debugging
  mNwAliveTime = selfObj.mNwAliveTime;
  logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
         "INGwSpSipCallController::INGwSpSipCallController(const INGwSpSipCallController&): m_SipIface<%x> mNwAliveTime<%d>", m_SipIface,mNwAliveTime);
}

INGwSpSipCallController & 
INGwSpSipCallController::operator = (const INGwSpSipCallController& selfObj)
{
	m_SipIface = selfObj.m_SipIface;
  //INCTBD - Temporary logging for debugging
  logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
         "INGwSpSipCallController::operator =(): m_SipIface<%x>", m_SipIface);
	return *this;
}
	
//Function will take a ref and save in its map
//Retrun 0 if successful else return -1
int 
INGwSpSipCallController::addCall(std::string& p_CallIdStr, INGwSpSipCall* p_SipCall)
{
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::addCall");
  int ret = m_SipCallMap.addCall(p_CallIdStr, p_SipCall);

  if(ret == -1)
  {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                      "INGwSpSipCallController::addCall: "
                      "Adding CALL entry to map failed. Entry exist for : "
                      "and CallId [%s]", p_CallIdStr.c_str());
  }
  else
  {
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                      "INGwSpSipCallController::addCall: "
                      "Added Call entry to map "
                      "for CallId [%s]", p_CallIdStr.c_str());
  }

  LogINGwTrace(false, 0, "OUT INGwSpSipCallController::addCall");
  return ret;
}


//Function will remove the entry from the map and will remove a ref
//Retrun 0 if successful(entry found and removed) else return -1
int 
INGwSpSipCallController::removeCall(std::string& p_CallIdStr)
{
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::removeCall");
  int ret = m_SipCallMap.removeCall(p_CallIdStr);

  if(ret == -1)
  {
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                      "INGwSpSipCallController::removeCall: "
                      "Removing Call entry from map failed. Entry does not exist for : "
                      "CallId [%s]", p_CallIdStr.c_str());
  }
  else
  {
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                      "INGwSpSipCallController::removeCall: "
                      "Removed Call entry from map "
                      "for CallId [%s]", p_CallIdStr.c_str());
  }

  LogINGwTrace(false, 0, "OUT INGwSpSipCallController::removeCall");
  return ret;
}


//Function will take a ref and return call ptr if found
//Retrun 0 if successful(entry found and copied) else return -1
INGwSpSipCall* 
INGwSpSipCallController::getCall(std::string& p_CallIdStr)
{
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::getCall");
  INGwSpSipCall* retCall = m_SipCallMap.getCall(p_CallIdStr);

  if(retCall == NULL)
  {
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                      "INGwSpSipCallController::getCall: "
                      "Getting Call entry from map failed. Entry does not exist for : "
                      "CallId [%s]",
                       p_CallIdStr.c_str());
  }
  else
  {
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                      "INGwSpSipCallController::getCall: "
                      "Got Call entry from map . Entry does exist for : "
                      "CallId [%s]",
                       p_CallIdStr.c_str());
  }

  LogINGwTrace(false, 0, "OUT INGwSpSipCallController::getCall");
  return retCall;
}

int
INGwSpSipCallController::handleWorkerClbk(INGwIfrMgrWorkUnit* apWork) 
{
  LogINGwTrace(false, 0, "IN handleWorkerClbk");

  int l_retVal = OP_FAIL;

  if(0 == apWork) {
    logger.logINGwMsg(false, ERROR_FLAG, 0,
                  " INGwIfrMgrWorkUnit* apWork == NULL \n");
    LogINGwTrace(false, 0, "OUT handleWorkerClbk");
    return OP_FAIL;
  }

  switch(apWork->meWorkType)
  {
    case INGwIfrMgrWorkUnit::SIP_SEND_INFO_REQ:
    {
			g_TransitObj* l_transitObj = (g_TransitObj*)apWork->mpContextData;
			l_retVal = sendInfoRequest(*l_transitObj);
			delete l_transitObj;
      break;
    }

    case INGwIfrMgrWorkUnit::SIP_SEND_NOTIFY_REQ:
    {
			g_TransitObj* l_transitObj = (g_TransitObj*)apWork->mpContextData;
			char * lCallId = apWork->mpcCallId;

#ifdef INGW_LOOPBACK_SAS

      g_TransitObj transitObj;
	    transitObj.m_causeCode = G_SUCCESS;

      loopbackDlgInfo[l_transitObj->m_stackDlgId - m_lowDlgId]->msgCnt++;

      bool msgToBeSent = false;

      if (loopbackDlgInfo[l_transitObj->m_stackDlgId - m_lowDlgId]->msgCnt == 1) {
        getMsgBuf("LOOPBACK_MSG_BUFF_1", transitObj);
        msgToBeSent = true;
      }
      else if (loopbackDlgInfo[l_transitObj->m_stackDlgId - m_lowDlgId]->msgCnt == 3) {
        getMsgBuf("LOOPBACK_MSG_BUFF_2", transitObj);
        msgToBeSent = true;
      }

      logger.logINGwMsg(false, VERBOSE_FLAG, 0, "DlgId<%d> MsgCnt<%d>, %s",
             l_transitObj->m_stackDlgId,
             loopbackDlgInfo[l_transitObj->m_stackDlgId - m_lowDlgId]->msgCnt,
             (msgToBeSent)?"Not sending any message":"Sending Message");


      // Replace the dialogue id at apppropriate offset in the
      // hard coded buffer read from environment
      int lLenOffset = 0;
      if(0x00 != (transitObj.m_buf[1] & 0x80)){
        lLenOffset = (transitObj.m_buf[1]) - 128;
      }
      memcpy((transitObj.m_buf + 4 + lLenOffset), &(l_transitObj->m_stackDlgId), 4);

      transitObj.m_sasIp = INGwIfrPrParamRepository::getInstance().
                           getValue(std::string("32.1.10"));
      
      // Check the dialogue primitive type, if it is ABORT or END then 
      // reset the count for reuse
      if ((transitObj.m_buf[2 + lLenOffset] == 17) || 
          (transitObj.m_buf[2 + lLenOffset] == 13)) 
      {
        loopbackDlgInfo[l_transitObj->m_stackDlgId - m_lowDlgId]->msgCnt = 0;
         logger.logINGwMsg(false, VERBOSE_FLAG, 0, "Resetting MsgCnt to 0");
      }


      delete [] l_transitObj->m_buf;
		  l_transitObj->m_buf = NULL;
		  l_transitObj->m_bufLen = 0;
      delete l_transitObj;

      logger.logINGwMsg(false, TRACE_FLAG, 0, "handleWorkerClbk(): SAS IP Addr [%s]",
                        transitObj.m_sasIp.c_str());

      //processOutboundMsg(aSipConnection, infoData->getBody(), 
      //                   infoData->getBodyLength(),mSeqNum);

      //transitObj.m_seqNum = p_seqNum;
      m_SipIface->processOutboundMsg(transitObj);

#else
      l_retVal = sendNotifyRequest(*l_transitObj, lCallId);
      delete l_transitObj;
#endif


#ifdef INGW_EXTRA_LOGS
      logger.logINGwMsg(false,VERBOSE_FLAG,0,"+rem+ after sendNotifyRequest +conn+ <%x>",
                        INGwSpSipProvider::getInstance().getThreadSpecificSipData().conn);
#endif

      break;
    }

    case INGwIfrMgrWorkUnit::TEMP_SEND_NOTIFY:
		{
      INGwIwfBaseIface *lIface = INGwIwfProvider::getInstance().getInterface();
		  INGwIwfIface* l_IwfIface = static_cast<INGwIwfIface*>(lIface);
			g_TransitObj* l_transitObj = (g_TransitObj*)apWork->mpContextData;
			l_IwfIface->processInboundMsg(*l_transitObj);
      l_retVal = OP_SUCCESS;
      delete l_transitObj;
      break;
		}

    case INGwIfrMgrWorkUnit::TIMER_MSG:
    {
      logger.logINGwMsg(false, TRACE_FLAG, 0, 
												"INGwSpSipCallController::handleWorkerClbk: "
												"Timerid <%u> fired, workunit <%p>", 
												apWork->muiTimerId, apWork);
      // Get the context and call the handleTimeout method.
      INGwSipHBTimerContext *timercontext = (INGwSipHBTimerContext *)(apWork->mpContextData);

      if(!timercontext)
      {
        logger.logINGwMsg(false, ERROR_FLAG, 0, 
													"INGwSpSipCallController::handleWorkerClbk: "
													"Found NULL timer context in WorkUnit");
        l_retVal = OP_FAIL;
      }
      else
      {
        // If the timer had already been stopped, its status will be false.
        // Check this and delete the timer if required.
        SchedulerMsg* sm = dynamic_cast<SchedulerMsg*>(apWork);
        if(!sm)
        {
          logger.logINGwMsg(false, ERROR_FLAG, 0, 
														"INGwSpSipCallController::handleWorkerClbk: "
														"Error casting workunit into scheduler msg");
          l_retVal = OP_FAIL;
        }
        else if(sm->getStatus())
        {
          logger.logINGwMsg(false, TRACE_FLAG, 0, 
														"INGwSpSipCallController::handleWorkerClbk: "
														"Calling handleHBTimeOut");

          handleHBTimeOut(timercontext);
          delete timercontext;
          
          l_retVal = OP_SUCCESS;
        }
        else
        {
          logger.logINGwMsg(false, TRACE_FLAG, 0, 
														"INGwSpSipCallController::handleWorkerClbk: "
														"Not calling handleTimeout, since timer status is false");
          delete timercontext;
          l_retVal = OP_SUCCESS;
        }
      }

      break;
    }

		case INGwIfrMgrWorkUnit::PEER_INGW_CALL_MSG :
		{
			l_retVal = handlePeerINGWCallMsg(apWork);
      break;
		}

    case INGwIfrMgrWorkUnit::SIP_CLEAN_CALL:
    {
	    INGwSipEPInfo* lEPInfo = (INGwSipEPInfo*)apWork->mpContextData;

			l_retVal = cleanSipCall(*lEPInfo);
			delete lEPInfo;
      break;
    }


    case INGwIfrMgrWorkUnit::INIT_RETRANS_ARRAY:
    {
      INGwSpSipStackIntfLayer::instance().initRetransCntArray();   
    }

    default:
      logger.logINGwMsg(false, ERROR_FLAG, 0,
                        "INGwSpSipCallController::handleWorkerClbk: "
                        "Unrecognized work type <%d>",
                        apWork->meWorkType);
      l_retVal = OP_FAIL;
      break;
  } // End of switch

  apWork->mpContextData = NULL;

  LogINGwTrace(false, 0, "OUT handleWorkerClbk");
  return l_retVal;
}

//
//When INFO request failed
//408 will used to indicate timeout
//
int 
INGwSpSipCallController::handleSasAppMgmtMsgFailure(int p_ResponseCode, std::string& p_CallId)
{
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::handleSasAppMgmtMsgFailure");
  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                    "INGwSpSipCallController::handleSasAppMgmtMsgFailure: "
                    "Got failure response <%d> for <%s> ",
                    p_ResponseCode, p_CallId.c_str());

  //HANDLING TO BE PLANNED
  LogINGwTrace(false, 0, "OUT INGwSpSipCallController::handleSasAppMgmtMsgFailure");
  return 0;
}

//
//When notify request failed
//408 will used to indicate timeout
//
int
INGwSpSipCallController::handleInboundMsgFailure(int p_ResponseCode, INGwSpSipConnection* p_Connection)
{
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::handleInboundMsgFailure");

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                    "handleInboundMsgFailure: "
                    "Got failure response <%d> from <%s> ",
                    p_ResponseCode, p_Connection->getGwIPAddress());

  //report the failure to IWF who will clean the dialog

  INGwSpSipConnTcapTransInfo lTcapTransInfo;
	p_Connection->getTcapTransInfo(lTcapTransInfo);

  logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                    "handleInboundMsgFailure: "
                    "Got failure resp<%d> "
										"for DlgId<%d>, spId<%d>, suId<%d>, IsDialogCompete<%s>",
                    p_ResponseCode, lTcapTransInfo.m_stackDlgId, lTcapTransInfo.m_spId,
										lTcapTransInfo.m_suId, 
										(lTcapTransInfo.m_isDialogueComplete)? "TRUE" : "FALSE");
 
  if(! lTcapTransInfo.m_isDialogueComplete)
	{
		t_transitObject lTransitObject;
		lTransitObject.m_stackDlgId = lTcapTransInfo.m_stackDlgId;
		lTransitObject.m_appId = lTcapTransInfo.m_appId;
		lTransitObject.m_instId = lTcapTransInfo.m_instId;
		lTransitObject.m_isDialogueComplete = lTcapTransInfo.m_isDialogueComplete;
		lTransitObject.m_sasIp = p_Connection->getGwIPAddress(); 
    lTransitObject.m_suId = lTcapTransInfo.m_suId;
    lTransitObject.m_spId = lTcapTransInfo.m_spId;
		m_SipIface->processSasErrResp(lTransitObject);
	}

  LogINGwTrace(false, 0, "OUT handleInboundMsgFailure");
  return 0;
}

//
//When OPTIONS is recv
//
int 
INGwSpSipCallController::processHBMsg(INGwSpSipConnection* p_Connection)
{
  LogINGwTrace(false, 0, "IN processHBMsg");
  if(p_Connection == NULL)
    return -1;

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                    "processHBMsg: "
                    "Got SAS HP request . Entry Details are : "
                    "SAS IP [%s] SAS PORT [%d] ",
                    p_Connection->getGwIPAddress(), p_Connection->getGwPort());

  int ret = -1;

  std::string lSasIpStr = std::string(p_Connection->getGwIPAddress());
	INGwSipEPInfo lSipEPInfo;
	int resCode = 200;

	//CHeck if IP addr if for registered SAS
  bool isEPRegistered = 
			INGwIfrPrParamRepository::getInstance().isHostPresentInSasList(lSasIpStr);

  if(isEPRegistered == false)
	{
		resCode = 503;
	}
  else if( -1 == getEndPoint(lSasIpStr, lSipEPInfo))
	{
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
											"processHBMsg"
											"Entry for SAS [%s] not found in SAS MAP",
											lSasIpStr.c_str());
    resCode = 481;

	}
	else if (lSipEPInfo.mEPTimer->m_TimerId == 0)
	{
		// Timer is not yet started only start it

		resCode = 200;
		//Start timer
		startHBTimer(lSipEPInfo, 0);
	  INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED,
	  																					 __FILE__, __LINE__, 
	  																					 INGW_SAS_HEARTBEAT_RESUME,
	  																					 "HB between SAS and INGw Alarm",
	  																					 0, " with SAS IP %s ", 
	  																					 lSipEPInfo.mEpHost);
  }
	else
	{
		// Timer is already running. Restart it.

		resCode = 200;
		//Stop Timer
		stopHBTimer(lSipEPInfo);
		//Start timer
		startHBTimer(lSipEPInfo, 0);
	}

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                    "processHBMsg: "
                    "Sending Response [%d] for HB Request from : "
                    "SAS IP [%s] SAS PORT [%d] ",
                    resCode, p_Connection->getGwIPAddress(), p_Connection->getGwPort());

  ret = p_Connection->mSendResponse(INGW_SIP_METHOD_TYPE_OPTIONS,
                                    NULL, resCode);

  // For debugging - Start [
  std::string lLogStr = INGwSpSipProvider::getInstance().
                           getCallController()->toLogEpMap();
  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE,
         "processHBMsg()::REGISTERED SAS <%s>", (char *)lLogStr.c_str());

  if (g_enableDiagLogs && hbIncDiagTriggered) {
    hbIncDiagTriggered = false;
    g_IncDiag((char *)"-k", (char *)__FILE__, __LINE__,
              (char *)"INC-SAS HEARTBEAT DEBUG", 2);
  }
  else{
    logger.logINGwMsg(false,TRACE_FLAG,0,"diaglogs-<%d> <%d>",g_enableDiagLogs,
                       hbIncDiagTriggered);
  }
  // ] End - For debugging
  

  LogINGwTrace(false, 0, "OUT processHBMsg");
  return ret;
}

int
INGwSpSipCallController::startHBTimer(INGwSipEPInfo& p_EPInfo, int p_TimeoutCount)
{
  LogINGwTrace(false, 0, "IN startHBTimer");

  INGwSipHBTimerContext* lHBTimerContext = new INGwSipHBTimerContext;
  strcpy(lHBTimerContext->mEpHost, p_EPInfo.mEpHost);
  lHBTimerContext->mTimeoutCount = p_TimeoutCount;
  int lTimeout = INGwIfrPrParamRepository::getInstance().getHBTimeoutInMsec();

  int ret = INGwIfrMgrThreadMgr::getInstance().startTimer(lTimeout, 
                                                          (void *)lHBTimerContext,
                                                          this, 
                                                          p_EPInfo.mEPTimer->m_TimerId);  
  if(ret < 0)
  {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
										 "startHBTimer: "
										 "Could not start HB timer for <%s>", p_EPInfo.mEpHost);
    p_EPInfo.mEPTimer->m_TimerId = 0;
		delete lHBTimerContext;
  }
  else
  {
    p_EPInfo.mEPTimer->m_ThreadId = 
         INGwIfrMgrThreadMgr::getInstance().getCurrentThread().getThreadId();

    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
										 "startHBTimer: "
										 "Started HB timer for <%s> TimerId <%u> ThreadID <%d> time <%d> msec", 
										 p_EPInfo.mEpHost, p_EPInfo.mEPTimer->m_TimerId,
										 p_EPInfo.mEPTimer->m_ThreadId, lTimeout);
  }

  LogINGwTrace(false, 0, "OUT startHBTimer");
	return ret;
}

int
INGwSpSipCallController::stopHBTimer(INGwSipEPInfo& p_EPInfo)
{
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::stopHBTimer");

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
									 "INGwSpSipCallController::stopHBTimer: "
									 "Stoping HB timer for <%s> TimerId <%u> ThreadID <%d> ", 
									 p_EPInfo.mEpHost, p_EPInfo.mEPTimer->m_TimerId,
									 p_EPInfo.mEPTimer->m_ThreadId);

  int ret = INGwIfrMgrThreadMgr::getInstance().stopTimer(p_EPInfo.mEPTimer->m_TimerId,
																												 p_EPInfo.mEPTimer->m_ThreadId);  
  if(ret < 0)
  {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
										 "INGwSpSipCallController::stopHBTimer: "
										 "Could not Stop HB timer for <%s>", p_EPInfo.mEpHost);
    p_EPInfo.mEPTimer->m_TimerId = 0;
  }
  else
  {
		p_EPInfo.mEPTimer->m_TimerId = 0;
		p_EPInfo.mEPTimer->m_ThreadId = 0;
  }

  LogINGwTrace(false, 0, "OUT INGwSpSipCallController::stopHBTimer");
	return ret;
}

int
INGwSpSipCallController::handleHBTimeOut(INGwSipHBTimerContext* p_HBTimerContext)
{
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::handleHBTimeOut");
  int ret = -1;
  std::string lHostStr = std::string(p_HBTimerContext->mEpHost);
  int lTimeoutCount = p_HBTimerContext->mTimeoutCount + 1;
  int lMaxCount = INGwIfrPrParamRepository::getInstance().getHBTimeoutCount();


	INGwSipEPInfo lSipEPInfo;
  if( -1 == getEndPoint(lHostStr, lSipEPInfo))
	{
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
											"INGwSpSipCallController::handleHBTimeOut"
											"Entry for SAS [%s] not found in SAS MAP. "
                      "Not Handling the timeout message",
											lHostStr.c_str());
    ret = -1;
	}
  else if(lTimeoutCount < lMaxCount )
  {
    //Yogesh fix
    //if alive time has not expired for any ep reset mTimeoutCount to zero
    ret = startHBTimer(lSipEPInfo, lTimeoutCount);
    logger.logINGwMsg(false,TRACE_FLAG,0,"+HB+ timeoutCnt<%d> Max<%d>",
    lTimeoutCount,lMaxCount);
  }
  else
  {
    struct timeval  lTimeStamp;
    struct timeval  lEptimeStamp;

    struct timezone tz;
    gettimeofday(&lTimeStamp, &tz);
    INGwSpSipListenerThread::getModifyTimeForEp(lHostStr,lEptimeStamp);

    if((lTimeStamp.tv_sec - lEptimeStamp.tv_sec) >= mNwAliveTime){

      // For debugging - Start [
      if (g_enableDiagLogs && !hbIncDiagTriggered) {
        std::string lLogStr = INGwSpSipProvider::getInstance().
                                 getCallController()->toLogEpMap();
        logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE,
               "handleHBTimeOut()::REGISTERED SAS", (char *)lLogStr.c_str());
        if (lLogStr.length()) {
          hbIncDiagTriggered = true;
          g_IncDiag((char *)"-s", (char *)__FILE__, __LINE__,
                    (char *)"INC-SAS HEARTBEAT DEBUG", 2);
        }
        else{
          logger.logINGwMsg(false,ALWAYS_FLAG,0,"diaglogs <%d>",
                                                         lLogStr.length());
        }
      }
      else{
          logger.logINGwMsg(false,ALWAYS_FLAG,0,"diaglogs <%d> <%d>",
                            g_enableDiagLogs, hbIncDiagTriggered);
      }
      // ] End - For debugging

      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
	    									"+HB+ handleHBTimeOut"
	    									"HB Timeout for SAS [%s] . "
                        " Current timeout count is %d and max count is %d",
	    									lHostStr.c_str(), lTimeoutCount, lMaxCount);
		  lSipEPInfo.mEPTimer->m_TimerId = 0;
		  lSipEPInfo.mEPTimer->m_ThreadId = 0;

      logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE,"+HB+ time diff %d "
        "Ep Time In Map <%d> Out Side Map <%d>",
        (lTimeStamp.tv_sec - lEptimeStamp.tv_sec), lEptimeStamp.tv_sec, 
        g_NwInbTs.tv_sec);

      ret = handleSasHBFailure(lSipEPInfo);

      int lCurVal = 0;
      INGwIfrSmStatMgr::instance().increment(
							INGwSpSipProvider::miStatParamId_NumHBFailures, lCurVal, 1);

      
#ifdef CAPTURE_TS
      INGwSpSipListenerThread::logTsArray();
#endif

    }
    else{
      //setting timeout counts to zero
      //turn into verbose later
      logger.logINGwMsg(false,ALWAYS_FLAG,0,"+HB+ resetting timeout count"
      "Time diff <%d> mNwAliveTime<%d>",
       abs(lEptimeStamp.tv_sec - lTimeStamp.tv_sec),mNwAliveTime);
      ret = startHBTimer(lSipEPInfo, 0);
    }
  }
  LogINGwTrace(false, 0, "OUT INGwSpSipCallController::handleHBTimeOut");
  return ret;
}

int
INGwSpSipCallController::cleanSipCall(INGwSipEPInfo& p_SipEPInfo)
{
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::cleanSipCall");
  int ret = 0;

	INGwSipEPInfo lSipEPInfo = p_SipEPInfo;
  if( lSipEPInfo.mEpHost == NULL)
	{
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
											"INGwSpSipCallController::cleanSipCall"
											"Entry for SAS EP Host is NULL ");

	}
	else
	{
		INGwSpSipCall* lSipCall = getCall(lSipEPInfo.msLocalCallID);

		if(lSipCall == NULL)
		{
			ret = -1;
			logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
				  							"INGwSpSipCallController::cleanSipCall"
				  							"Entry for SIP call for [%s] not found in Call MAP",
				  							lSipEPInfo.mEpHost);
		}
		else
		{
			INGwIfrUtlRefCount_var callRef(lSipCall);

			//Now we have inly one connection in Call with ID 0
			int connId = 0;
			INGwSpSipConnection* lSipConn = lSipCall->getConnection(connId);
			if(lSipConn == NULL)
			{
				ret = -1;
			  logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
			    								"INGwSpSipCallController::cleanSipCall"
			    								"SIP connection for SAS [%s] not found in Call ",
			    								lSipEPInfo.mEpHost);
			}
			else
			{
				bool lSendBye = false;
				if(getenv("SEND_BYE_ON_HB_FAILURE"))
					lSendBye = true;

        if(lSendBye)
				{
				  INGwIfrUtlRefCount_var connRef(lSipConn);
          ret = 0;
				  //Send Bye
			    logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE,
			      								"cleanSipCall"
			      								"EndPoint Failed. Sending Bye to SAS [%s] ",
			      								lSipEPInfo.mEpHost);

          ret = lSipConn->mSendRequest(INGW_SIP_METHOD_TYPE_BYE, NULL, NULL);
				}
				else
				{
					// PR - 51484
					INGwIfrUtlRefCount_var connRef(lSipConn);
			    logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE,
			      								"cleanSipCall "
			      								"EndPoint Failed. Cleaning up call without BYE for SAS [%s] ",
			      								lSipEPInfo.mEpHost);

					lSipCall->startSerialize(INGwIfrUtlSerializable::REP_DELETE);
					lSipCall->cleanup();
				}

			} // end of else of if [lSipConn == NULL]

		} // end of else of if [lSipCall == NULL]

	} // end of else of if [getEPInfo == NULL]

  LogINGwTrace(false, 0, "OUT INGwSpSipCallController::cleanSipCall");
  return ret;
}

int
INGwSpSipCallController::cleanSasEpRegistration(INGwSipEPInfo& p_SipEPInfo)
{
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::cleanSasEpRegistration");
  int ret = 0;

	INGwSipEPInfo lSipEPInfo = p_SipEPInfo;
  if( lSipEPInfo.mEpHost == NULL)
	{
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
											"INGwSpSipCallController::cleanSasEpRegistration"
											"Entry for SAS EP Host is NULL ");

	}
	else
	{
    // We need to remove EP and inform IWF even if can't clean sip call
		//Remove SAS from Map
		std::string lHostStr = lSipEPInfo.mEpHost;
		ret = removeEndPoint(lHostStr, lSipEPInfo.msLocalCallID);

    // Call IWF only if we are successfull in removing EndPoint,
		//If we means there EP is already removed and not info is present in IWF

		if(ret != -1)
		{
		  //Inform Sip Interface

      int resCode = 500;
      g_TransitObj transitObj;
	    transitObj.m_causeCode = G_FAILURE;
	    transitObj.m_sasIp = lHostStr;

	    // CALL IWF
      INGwSpSipIface::t_INGwSipIfaceRequestType requestType = 
													 INGwSpSipIface::SAS_REQUEST_SERVER_DEREGISTER;

      m_SipIface->processSasMgmtRequest(requestType, transitObj);

			// Remove the sip call object
			// Two different thread can enter at the same time for cleanup of same call
			// like HB failure thread and re-register thread
			// so cleanup is being done as work so that proceesing can be synchronized.

	    INGwIfrMgrWorkUnit* lWorkUnit = NULL;
			char* l_callId = new char[lSipEPInfo.msLocalCallID.length() + 1];
      strcpy(l_callId,  lSipEPInfo.msLocalCallID.c_str());

  	  lWorkUnit = new INGwIfrMgrWorkUnit;
      lWorkUnit->meWorkType = INGwIfrMgrWorkUnit::SIP_CLEAN_CALL;
  	  lWorkUnit->mpcCallId = l_callId;
  	  lWorkUnit->mpWorkerClbk = this;
      lWorkUnit->getHash();

	    INGwSipEPInfo* lContextData = new INGwSipEPInfo(lSipEPInfo);

      lWorkUnit->mpContextData = (void*)lContextData;

      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                        "INGwSpSipCallController::cleanSasEpRegistration: "
												"Posting the WU to clean call");

      ret = INGwIfrMgrThreadMgr::getInstance().postMsg(lWorkUnit);
		}

	} // end of else of if [getEPInfo == NULL]

  LogINGwTrace(false, 0, "OUT INGwSpSipCallController::cleanSasEpRegistration");
  return ret;
}


int
INGwSpSipCallController::handleSasHBFailure(INGwSipEPInfo& p_SipEPInfo)
{
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::handleSasHBFailure");
  int ret = 0;

  ret = cleanSasEpRegistration(p_SipEPInfo);

	INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED,
																						 __FILE__, __LINE__, 
																						 INGW_SAS_HEARTBEAT_FAILURE,
																						 "HB between SAS and INGw Alarm",
																						 0, " with SAS IP %s ", 
																						 p_SipEPInfo.mEpHost);

  LogINGwTrace(false, 0, "OUT INGwSpSipCallController::handleSasHBFailure");
  return ret;
}

int
INGwSpSipCallController::handlePeerINGWCallMsg(INGwIfrMgrWorkUnit* apWork) 
{
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::handlePeerINGWCallMsg");
  int retValue = 0;

  const char *callId = apWork->mpcCallId;
	std::string lCallIdStr = callId;

  INGwFtPktMsg *msg = (INGwFtPktMsg *)apWork->mpMsg;

  switch(msg->getMsgType())
  {
    case MSG_FT_DELETE_CALL:
    {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
												"MSG_FT_DELETE_CALL recvd for call id [%s]",
                        callId);

      INGwSpSipCall* pCall = getCall(lCallIdStr);
      INGwIfrUtlRefCount_var call(pCall);
      
      if(pCall == NULL)
      {
        LogINGwTrace(false, 0, "OUT handlePeerINGWCallMsg");
        delete msg;
        return 0;
      }
   
      INGwFtPktDeleteCallMsg *deleteCallMsg = static_cast<INGwFtPktDeleteCallMsg *>(msg);
      delete msg;
      LogINGwTrace(false, 0, "OUT handlePeerINGWCallMsg");
      return 0;

    }// end of case MSG_FT_DELETE_CALL
    break;

    case MSG_CALL_BACKUP:
    {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
												"MSG_CALL_BACKUP recvd for call id [%s]",
                        callId);
			// Handling is after switch block
    }
    break;

    default:
    {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "Unexpected message [%s]",
                         msg->toLog().c_str());
      LogINGwTrace(false, 0, "OUT handlePeerINGWCallMsg");
      return -1;
    }
  }

  logger.logINGwMsg(false, VERBOSE_FLAG, 0, 
                  "Processing the PEER_INGW_CALL_MSG for call Id %s", 
                  apWork->mpcCallId);

  INGwFtPktCallDataMsg *callDataMsg = (INGwFtPktCallDataMsg *)msg;

  char* pcMsg = const_cast<char*>(callDataMsg->getCCMBuffer());
   
	if( (pcMsg == NULL) || (0 == callDataMsg->getCCMDataLen() ) )
	{
		logger.logINGwMsg(false, ERROR_FLAG, imERR_RESOURCE_FAILED, 
				            "Ignoring empty message received while processing "
				            "PEER_INGW_CALL_MSG for call Id %s", 
				            apWork->mpcCallId);
		delete callDataMsg;
    LogINGwTrace(false, 0, "OUT handlePeerINGWCallMsg");
		return -1;
	}

  // Execption handling only to safe-guard against unknown exceptions
  // thrown by the provider which can cause core dumps or the provider
  // is not available
  try 
  {
    logger.logINGwMsg(false, TRACE_FLAG, 0, 
                      "Invoking handleBkupInfo() on sip provider type while "
                      "PEER_INGW_CALL_MSG for call Id %s", 
                      apWork->mpcCallId);

    INGwSpSipProvider::getInstance().handleBkupInfo(callId, 
																										pcMsg, 
																										callDataMsg->getCCMDataLen());
  }
  catch(...) 
  {
      logger.logINGwMsg(false, ERROR_FLAG, -1, 
                      "Unknown exception from sip provider "
											"for PEER_INGW_CALL_MSG handling for call Id %s", 
                      apWork->mpcCallId);
  }

  delete callDataMsg;
  LogINGwTrace(false, 0, "OUT INGwSpSipCallController::handlePeerINGWCallMsg");
  return retValue;
}

std::string 
INGwSpSipCallController::toLogEpMap()
{
	return m_SipEPInfoMap.toLog();
}

// Replicate the call object for registered Endpoint
void
INGwSpSipCallController::replicateEpToPeer()
{
  LogINGwTrace(false, 0, "IN INGwSpSipCallController::replicateEpToPeer");

  t_EPInfoList lEPList;

	int lEpCount = getEndPointList(lEPList);
  logger.logINGwMsg(false, TRACE_FLAG, 0, 
                    "INGwSpSipCallController::replicateEpToPeer "
                    "No of registered EP to be replicated to peer is <%d>", 
                    lEpCount);


	while(! lEPList.empty())
	{
    t_EPInfoListItr lEpIter = lEPList.begin();

		INGwSipEPInfo* lEpInfo = (INGwSipEPInfo*)(*lEpIter);

    INGwSpSipCall* lSipCall = getCall(lEpInfo->msLocalCallID);

    if(lSipCall == NULL)
    {
      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
                        "INGwSpSipCallController::replicateEpToPeer"
                        "Entry for SIP call for [%s] not found in Call MAP",
                        lEpInfo->mEpHost);
    }
    else
    {
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE,
                        "INGwSpSipCallController::replicateEpToPeer"
                        "Replicating SIP call for [%s] ",
                        lEpInfo->mEpHost);
			
      INGwIfrUtlRefCount_var callRef(lSipCall);
      lSipCall->startSerialize();
    }

		lEPList.erase(lEpIter);
		delete lEpInfo;
	}

  LogINGwTrace(false, 0, "OUT INGwSpSipCallController::replicateEpToPeer");
}

void
INGwSpSipCallController::getNwAliveTime(long pNwAlivetime)
{
  LogINGwTrace(false, 0, "In getNwAliveTime");
  if(-1 == pNwAlivetime){
    mNwAliveTime = 8;
    char* lpcAliveTime = getenv("INGW_NW_ALIVE_TIME");
    if(NULL != lpcAliveTime){
      mNwAliveTime =  atoi(lpcAliveTime);
    }
  }else{
    mNwAliveTime = pNwAlivetime;
  }  
  logger.logINGwMsg(false, ALWAYS_FLAG,0, "OUT getNwAliveTime <%d>",
  mNwAliveTime);
}

#ifdef INGW_LOOPBACK_SAS
void INGwSpSipCallController::initLoopbackDlgInfo(int p_loDlg, int p_hiDlg)
{
  m_lowDlgId  = p_loDlg;
  m_highDlgId = p_hiDlg;

  int lTotalDlgIds = (p_hiDlg - p_loDlg) + 1;
  logger.logINGwMsg(false, VERBOSE_FLAG, 0,
         "initLoopbackDlgInfo():lowDlgId<%d>, highDlgId<%d> TotDlgIds<%d>",
         p_loDlg, p_hiDlg, lTotalDlgIds);
  loopbackDlgInfo = new LbDlgInfoStruct* [lTotalDlgIds];
  memset(loopbackDlgInfo, 0, (sizeof(LbDlgInfoStruct *)*lTotalDlgIds));
}

//LbDlgInfoStruct** 
//INGwSpSipCallController::getLoopbackDlgInfo()
//{
//  return loopbackDlgInfo;
//}

void INGwSpSipCallController::getMsgBuf(char * envVar, g_TransitObj & transitObj)
{
  char* byteBuff = getenv(envVar);
  
  if(NULL != byteBuff){
    string byteBuffStr(byteBuff);
    int length = byteBuffStr.length();
    // make sure the input string has an even digit numbers
    if(length%2 == 1) {
    	byteBuffStr = "0" + byteBuffStr;
    	length++;
    }
  
    // allocate memory for the output array
    transitObj.m_buf = new unsigned char[length/2 + 2];
    transitObj.m_bufLen = length/2;
  
    myStringToByteArray(byteBuffStr, transitObj.m_buf, transitObj.m_bufLen);
  }
  else {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "EnvVar<%s> not defined", envVar);
  }
}


#endif

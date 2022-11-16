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
//     File:     INGwSpSipIface.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal     07/12/07     Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipProvider");

#include <INGwInfraUtil/INGwIfrUtlGlbInclude.h>
#include <INGwSipProvider/INGwSpSipIface.h>
#include <INGwSipProvider/INGwSpSipProvider.h>
#include <INGwIwf/INGwIwfProvider.h>

#include <string>
using namespace std;

INGwSpSipIface::INGwSpSipIface():INGwIwfBaseIface(INGwIwfBaseIface::SIP)
{
	INGwIwfBaseIface *lIface = INGwIwfProvider::getInstance().getInterface();
	m_IwfIface = static_cast<INGwIwfIface*>(lIface);
}

INGwSpSipIface::~INGwSpSipIface()
{
}

/*
* This method will be called by SIP provider after recveiving 
* INVITE, BYE or INFO message. g_TransitObj will 
* have XML body along with orignating SAS address
*/

int
INGwSpSipIface::processSasMgmtRequest(t_INGwSipIfaceRequestType p_RequestType, 
																			g_TransitObj  &p_transitObj)
{
	 LogINGwTrace(false, 0,"IN INGwSpSipIface::processSasMgmtRequest()");

	 logger.logINGwMsg(false, ALWAYS_FLAG, 0, "Request Type [%d] received.", 
										 int(p_RequestType));
	 int retValue = -1;

	 switch(p_RequestType)
	 {
			case SAS_REQUEST_SERVER_REGISTER_INIT:
					retValue =  handleSasServerRegistrationInit(p_transitObj);
					break;
			case SAS_REQUEST_SERVER_REGISTER_COMPLETE:
					retValue =  handleSasServerRegistrationComplete(p_transitObj);
					break;
			case SAS_REQUEST_SERVER_DEREGISTER:
					retValue =  handleSasServerDeregistration(p_transitObj);
					break;
			case SAS_REQUEST_APP_MGMT:
					retValue =  handleSasAppMgmt(p_transitObj);
					break;
      default :
					retValue =  -1;
				 break;
	 }

	 LogINGwTrace(false, 0,"OUT INGwSpSipIface::processSasMgmtRequest()");
	 return retValue;
}

/*
* This method will be called by SIP provider after recveiving 
* Notify message. g_TransitObj will 
* have XML body along with orignating SAS address
*/

int
INGwSpSipIface::processOutboundMsg(g_TransitObj  &p_transitObj)
{
	 LogINGwTrace(false, 0,"IN INGwSpSipIface::processOutboundMsg()");

	 int ret =  m_IwfIface->processOutboundMsg(p_transitObj);

	 LogINGwTrace(false, 0,"OUT INGwSpSipIface::processOutboundMsg()");
	 return ret;
}

/*
* This method will be called by SIP provider after recveiving 
* failure response for Notify message. g_TransitObj wil
* have Tcap related tranx information
*/

void
INGwSpSipIface::processSasErrResp(g_TransitObj  &p_transitObj)
{
	 LogINGwTrace(false, 0,"IN INGwSpSipIface::processSasErrResp()");

	 m_IwfIface->processSasErrResp(p_transitObj);

	 LogINGwTrace(false, 0,"OUT INGwSpSipIface::processSasErrResp()");
	 return ;
}

/*
* This method will be called by IWF provider after consolidating 
* Dialogue and Components received for any call. g_TransitObj will 
* have encoded XML along with destination SAS address
*/

int
INGwSpSipIface::processInboundMsg(g_TransitObj  &p_transitObj)
{
	 LogINGwTrace(false, 0,"IN INGwSpSipIface::processInboundMsg()");

	 INGwSpSipCallController* callCtlr = 
														INGwSpSipProvider::getInstance().getCallController();

	 int ret = callCtlr->handleInboundMsg(p_transitObj);

	 LogINGwTrace(false, 0,"OUT INGwSpSipIface::processInboundMsg()");
	 return ret;
}

int
INGwSpSipIface::processReplayInboundMsg(g_TransitObj  &p_transitObj)
{
	 LogINGwTrace(false, 0,"IN INGwSpSipIface::processInboundMsg()");

	 INGwSpSipCallController* callCtlr = 
														INGwSpSipProvider::getInstance().getCallController();

	 int ret = callCtlr->handleReplayInboundMsg(p_transitObj);

	 LogINGwTrace(false, 0,"OUT INGwSpSipIface::processInboundMsg()");
	 return ret;
}

/*
* This method will be called by IWF provider for transmitting
* management message IN_SERVICE, OUT_OF_SERVICE, ALLOWED, PROHIBITED. 
* g_TransitObj will have encoded XML along with destination SAS address
*/

int
INGwSpSipIface::sendSasAppResp(g_TransitObj  &p_transitObj)
{
	 LogINGwTrace(false, 0,"IN INGwSpSipIface::processManagmentMsg()");

	 INGwSpSipCallController* callCtlr = 
														INGwSpSipProvider::getInstance().getCallController();

	 int ret = callCtlr->handleSasAppMgmtResponse(p_transitObj);

	 LogINGwTrace(false, 0,"OUT INGwSpSipIface::processManagmentMsg()");
	 return ret;
}

int
INGwSpSipIface::handleSasServerRegistrationInit(g_TransitObj  &p_transitObj)
{
	 LogINGwTrace(false, 0,"IN INGwSpSipIface::handleSasServerRegistrationInit()");

	 m_IwfIface->getOpcSsnList(p_transitObj);

	 LogINGwTrace(false, 0,"OUT INGwSpSipIface::handleSasServerRegistrationInit()");
	 return 0;
}

int
INGwSpSipIface::handleSasServerRegistrationComplete(g_TransitObj  &p_transitObj)
{
	 LogINGwTrace(false, 0,"IN INGwSpSipIface::handleSasServerRegistrationComplete()");

	 LogINGwTrace(false, 0,"OUT INGwSpSipIface::handleSasServerRegistrationComplete()");
	 return 0;
}

int
INGwSpSipIface::handleSasServerDeregistration(g_TransitObj  &p_transitObj)
{
	 LogINGwTrace(false, 0,"IN INGwSpSipIface::handleSasServerDeregistration()");

	 int ret = m_IwfIface->deregisterSas(p_transitObj);

	 LogINGwTrace(false, 0,"OUT INGwSpSipIface::handleSasServerDeregistration()");
	 return ret;
}

int
INGwSpSipIface::handleSasAppMgmt(g_TransitObj  &p_transitObj)
{
	 LogINGwTrace(false, 0,"IN INGwSpSipIface::handleSasAppMgmt()");

	 int ret = m_IwfIface->processSasInfo(p_transitObj);

	 LogINGwTrace(false, 0,"OUT INGwSpSipIface::handleSasAppMgmt()");
	 return ret;
}

int
INGwSpSipIface::handleSasAppRegistration(g_TransitObj  &p_transitObj)
{
	 return 0;
}

int
INGwSpSipIface::handleSasAppDeregistration(g_TransitObj  &p_transitObj)
{
	 return 0;
}

#ifdef INGW_LOOPBACK_SAS
int
INGwSpSipIface::initLoopbackDlgInfo(int p_loDlg, int p_hiDlg)
{
   logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwSpSipIface::initLoopbackDlgInfo()");
   INGwSpSipCallController* callCtlr = INGwSpSipProvider::getInstance().getCallController();

	 callCtlr->initLoopbackDlgInfo(p_loDlg, p_hiDlg);
   return 0;
}
#endif




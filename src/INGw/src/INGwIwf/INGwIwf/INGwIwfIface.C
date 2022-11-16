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
//     File:     INGwIwfIface.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h> 
BPLOG("INGwIwf");

#include <INGwIwf/INGwIwfIface.h>
#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>
#include <INGwInfraParamRepository/INGwIfrPrConfigMgr.h>

/**
* Constructor
*/
INGwIwfIface::INGwIwfIface():INGwIwfBaseIface(INGwIwfBaseIface::IWF)
{
}

/**
* Destructor
*/
INGwIwfIface::~INGwIwfIface()
{
}

void
INGwIwfIface::setTcapIface(INGwIwfBaseIface *p_iface)
{
	m_tcapIface = static_cast<INGwTcapIface*>(p_iface);
}

void
INGwIwfIface::setSipIface(INGwIwfBaseIface *p_iface)
{
	m_sipIface = static_cast<INGwSpSipIface*>(p_iface);
  //INCTBD - Temporary logging for debugging
  logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
         "INGwIwfIface::setSipIface(): m_sipIface<%x>", m_sipIface);
}

/**
* Description : This method will be called on receiving INFO message with SSN 
*						    and  OPC. SIP provider will maintain map of SIP called and SAS 
*							  Info (FIP, Port, Session ID, Contact etc) and will pass the call
*							  ID to IWF. IWF will further pass this information to Load 
*								distributor to update its mapping info. 
*
* @param <p_selfPointCode> - OPC
* @param <p_selfSsn> 	     - SSN associated with OPC
* @param <p_sasCallId> 	   - Call ID received in SIP INVITE
*
* @return <bool> 					 - true | false
*
*/
/*
int
INGwIwfIface::processSasInfo(g_TransitObj &p_transitObj)
{
	return (m_tcapIface->processSasInfo(p_transitObj));
}
*/


int
INGwIwfIface::processSasInfo(g_TransitObj &p_transitObj)
{
  return(m_tcapIface->processSasInfo(p_transitObj));
}
/**
* Description : This method will be called on receiving INVITE message to get 
*								list of registered OPC-SSN List. IWF will further call 
*								getOpcSsnList(0 from  load distributor. SIP provider should 
*								then form XML and send it in 200 OK.
*
* @return <List<OpcSsnList>> - List of OPC/SSn
*/
void
INGwIwfIface::getOpcSsnList(g_TransitObj &p_transitObj)
{
	return (m_tcapIface->getOpcSsnList(p_transitObj));
}

/**
* Description: This method will be invoked from INGwSpSipNotifyHandler
*              to process 200 Ok received for a particular notify message
*               
*/
int
INGwIwfIface::processSeqNumAck(U8 p_direction, int p_dialogueId, int p_seqNum, bool pIsLast)
{
  logger.logINGwMsg(false, TRACE_FLAG, 0,"In processSeqNumAck p_direction<%d>"
    " p_dialogueId <%d> p_seqNum <%d> isLast<%d>",p_direction,p_dialogueId,p_seqNum,pIsLast); 
  return(m_tcapIface->processSeqNumAck(p_direction, p_dialogueId, p_seqNum,pIsLast));
  logger.logINGwMsg(false, TRACE_FLAG, 0,"Out processSeqNumAck"); 
}

/**
* Description : This method will be called on receiving NOTIFY message which 
*								shall be used for sending TCAP message. SIP provider will 
*								first decode XML parser and form an object and pass it IWF to 
*								process it.
*
* @param <p_callInfoInfoObj> - 
* @param <p_causeCode> 	     - Return CauseCode
*
* @return <bool>  - true | false
*/
int
INGwIwfIface::processOutboundMsg(g_TransitObj &p_transitObj)
{
	return (m_tcapIface->processOutboundCall(p_transitObj));
}

int
INGwIwfIface::deregisterSas(g_TransitObj &p_transitObj)
{
	return (m_tcapIface->deregisterSas(p_transitObj));
}

/**
* Description : This method will be called on receiving 4xx response for
*								NOTIFY message sent to SAS.
*
* @param <p_callInfoInfoObj> - 
* @param <p_causeCode> 	     - Return CauseCode
*
* @return <bool>  - true | false
*/
void
INGwIwfIface::processSasErrResp(g_TransitObj &p_transitObj)
{
	return (m_tcapIface->processSasErrResp(p_transitObj));
}


/**
* Description : This method will be called by TCAP provider after consolidating 
* 							Dialogue and Components received for any new call. 
*								Note: Dialogue if first received followed by component from 
*								stack.
*
* @param <p_callInfoObj> - OPC
*
* @return <bool> 					 - true | false
*/
int
INGwIwfIface::processInboundMsg(g_TransitObj  &p_transitObj)
{
  logger.logINGwMsg(false, TRACE_FLAG, 0, 
         "INGwIwfIface::setSipIface(): m_sipIface<%x>", m_sipIface);
	return (m_sipIface->processInboundMsg (p_transitObj));
}


int
INGwIwfIface::processReplayInboundMsg(g_TransitObj  &p_transitObj)
{
  logger.logINGwMsg(false, TRACE_FLAG, 0, 
         "+rem+ INGwIwfIface::setSipIface(): m_sipIface<%x>", m_sipIface);
	return (m_sipIface->processReplayInboundMsg (p_transitObj));
}

int
INGwIwfIface::sendSasAppResp(g_TransitObj &p_transitObj)
{
	return (m_sipIface->sendSasAppResp(p_transitObj));
}

#ifdef INGW_LOOPBACK_SAS
int
INGwIwfIface::initLoopbackDlgInfo(int p_loDlg, int p_hiDlg)
{
  logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwIwfIface::initLoopbackDlgInfo()");
	m_sipIface->initLoopbackDlgInfo(p_loDlg, p_hiDlg);
  return 0;
}
#endif


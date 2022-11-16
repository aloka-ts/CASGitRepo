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
//     File:     INGwIfrMgrAgentClbkImpl.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal   07/12/07     Initial Creation
//********************************************************************
#ifndef _INGW_IFR_MGR_AGENT_CLBK_IMPL_H_
#define _INGW_IFR_MGR_AGENT_CLBK_IMPL_H_

#include <unistd.h>
#include <Agent/BayAgentImpl.h>

/*
 * Configuration change Agent Callback function
 */

int
configIfForINGw(void* apContext, const CORBA::Any& arData, const char* apcOID,
	              const ImMediateTypes::OperationTypeValue aeOpValue);

/*
 * Startup function for INGW as Agent Callback function
 */

int
startupIfForINGw(void* apContext);

/*
 * State change  Agent Callback function
 */

int
changeStateIfForINGw(void* apContext,
		                 const ImMediateTypes::SubsystemStateTypeValue aValue);

/*
 * OID change  Agent Callback function
 */

int 
oidChangedIfForINGw(void *apContext, const CORBA::Any& arData, 
						        const char* apcOID, long  alSubsysId, 
						        ImMediateTypes::SubsysTypeCode subsysType, 
						        const ImMediateTypes::OperationTypeValue aeOpType);

/*
 * Performance : Agent Callback function
 */

ImMediateTypes::NVTypeSeq*
performanceIfForINGw(void* apContext);

/*
 * Reconfiguration of MsrMgr : Agent Callback function
 */

void
reconfigMsrMgrForINGw (const char *apcXml);

/*
 * Get new sub-sys addition notification
 */

void
newSubsysAddedIf(void *context, const RSIEmsTypes::ComponentInfo &info);

/*
kept commented as these will not be used by any other method. 
void copyNodeStatus(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyAddUserPart(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyDelUserPart(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyAddRule(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyDelRule(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyAddLocalSsn(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyDelLocalSsn(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyLocalSsnStatus(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyAddEndPoint(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyDelEndPoint(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyDisableEndPoint(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyEnableEndPoint(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyEnableSsn(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyDisableSsn(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyAddRemoteSsn(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyDelRemoteSsn(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyAddPs(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyDelPs(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyPspStatus(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyAddPsp(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyDelPsp(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyPssStatus(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyDsiableUserPart(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyEnableUserPart(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyM3uaAssocUp(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyM3uaAssocDown(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyM3uaAspUp(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyM3uaAspDown(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyM3uaAspAct(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
void copyM3uaAspInAct(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq);
*/
bool
ss7ConfigurationClbk(void* context, const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq,
										RSIEmsTypes::Ss7SigtranStackRespList_out respList);

#endif //_INGW_IFR_MGR_AGENT_CLBK_IMPL_H_


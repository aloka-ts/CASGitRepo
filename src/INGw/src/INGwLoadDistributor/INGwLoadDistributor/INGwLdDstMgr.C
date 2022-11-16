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
//     File:     INGwLdDstMgr.C
//
//     Desc:     This file provides implementation to header 
//						   
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwLoadDistributor");

#include <INGwLoadDistributor/INGwLdDstMgr.h>
#include <Util/imOid.h>

#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>

#include <INGwInfraUtil/INGwIfrUtlGlbInclude.h>
#include <INGwInfraUtil/INGwIfrUtlGlbFunc.h>
#include <unistd.h>
/**
* Constructor
*/
INGwLdDstMgr::INGwLdDstMgr()
{
	LogINGwTrace(false, 0, "IN INGwLdDstMgr::Constructor()");

  m_dstInfoMap = new INGwLdDstMap;
  //INCTBD temporary log
	logger.logINGwMsg(false, ALWAYS_FLAG, 0,
                         "m_dstInfoMap address is  0x%x",m_dstInfoMap); 
	LogINGwTrace(false, 0, "OUT INGwLdDstMgr::Constructor()");
}

/**
* Destructor
*/
INGwLdDstMgr::~INGwLdDstMgr()
{
	LogINGwTrace(false, 0,"IN INGwLdDstMgr::Destructor()");

	if ( NULL != m_dstInfoMap ) {
		delete m_dstInfoMap;
	}

	LogINGwTrace(false, 0,"OUT INGwLdDstMgr::Destructor()");
}

/**
* Description : 
*
* @return <int> - 
*
*/
int
INGwLdDstMgr::initialize()
{
	LogINGwTrace(false, 0,"IN INGwLdDstMgr::initialize()");

	int retVal = G_SUCCESS;

	// fetch OPC-SSN List from param repository and store locally.
	string opcSsnList;
	INGwIfrPrParamRepository::getInstance().getValue(ingwSS7_APP_INFO, 
																									 opcSsnList); 

	if (true == opcSsnList.empty()) {
		LogINGwTrace(false, 0,"IN INGwLdDstMgr::initialize(), Failed");
		return  retVal;
	}

#if BOTH_OPC_SSN
	retVal = extractOpcSsn(opcSsnList);
#else
	retVal = extractSsn(opcSsnList);
#endif

	if (G_FAILURE == retVal) {
		LogINGwTrace(false, 0,"OUT INGwLdDstMgr::initialize(), Failed");
	}

	// Fetch Criteria Type
	string patternType;
	INGwIfrPrParamRepository::getInstance().getValue(ingwLOAD_DIST_PATTERN, 
																									 patternType); 
		
	if (true == patternType.empty()) {
		LogINGwTrace(false, 0,"IN INGwLdDstMgr::initialize(), Failed");
		return  retVal;
	}

	// Initialize Call Map
	m_dstInfoMap->initialize(m_opcSsnConfigList, patternType);

	LogINGwTrace(false, 0,"OUT INGwLdDstMgr::initialize()");
	return retVal ;
}

/**
* Description : This method will be called to register OPC and SSN for which 
*								TCAP Provider has registered with Stack.
*
* @param <p_selfPointCode> - 
* @param <p_selfSsn> - 
*
* @return <int> - 
*
*/
void
INGwLdDstMgr::setOpcSsnRegisteredWithStack(U32 p_selfPointCode, U8 p_selfSsn)
{
	LogINGwTrace(false, 0,"IN INGwLdDstMgr::setOpcSsnRegisteredWithStack()");

#if BOTH_OPC_SSN
	m_dstInfoMap->markDstAvailable(p_selfPointCode, p_selfSsn);
#else
	U32 lPc =0;
	m_dstInfoMap->markDstAvailable(lPc, p_selfSsn);
#endif

	LogINGwTrace(false, 0,"OUT INGwLdDstMgr::setOpcSsnRegisteredWithStack()");
}

/**
* Description : This method will be called to de-register OPC and SSN for which 
*							  TCAP Provider has de-registered with stack. This will be used 
*							  incase  of stack failure to handle SSN
*
* @param <p_selfPointCode> - 
* @param <p_selfSsn> - 
*
* @return <int> - 
*
*/
void
INGwLdDstMgr::unsetOpcSsnRegisteredWithStack(U32 p_selfPointCode, U8 p_selfSsn)
{
	LogINGwTrace(false, 0,"IN INGwLdDstMgr::unsetOpcSsnRegisteredWithStack()");

#if BOTH_OPC_SSN
	m_dstInfoMap->markDstUnavailable(p_selfPointCode, p_selfSsn);
#else
	U32 lPc =0;
	m_dstInfoMap->markDstUnavailable(lPc, p_selfSsn);
#endif

	LogINGwTrace(false, 0,"OUT INGwLdDstMgr::unsetOpcSsnRegisteredWithStack()");
}

/**
* Description : This method will provide number of SAS Application registered 
*								for given OPC and SSN.
*
* @param <p_state> - 
*
* @return <int> - 
*
*/
int
INGwLdDstMgr::getNumOfSasAppReg(U32 p_selfPointCode, U8 p_selfSsn)
{
	LogINGwTrace(false, 0,"IN INGwLdDstMgr::getNumOfSasAppReg()");

#if BOTH_OPC_SSN
	int retVal = m_dstInfoMap->getNumOfSasAppReg(p_selfPointCode, p_selfSsn);
#else
	U32 lPc =0;
	int retVal = m_dstInfoMap->getNumOfSasAppReg(lPc, p_selfSsn);
#endif

	LogINGwTrace(false, 0,"OUT INGwLdDstMgr::getNumOfSasAppReg()");
	return retVal;
}

/**
* Description : This will be called to get list of configured OPC-SSN list
*
* @param <p_state> - 
*
* @return <int> - 
*
*/
INGwLdPcSsnList
INGwLdDstMgr::getOpcSsnList()
{
	LogINGwTrace(false, 0,"IN INGwLdDstMgr::getOpcSsnList()");
	LogINGwTrace(false, 0,"OUT INGwLdDstMgr::getOpcSsnList()");
	return m_opcSsnConfigList;
}

/**
* Description : This method is called by IWF to check whether INGw has already 
*								registered with ss7 stack with OPC and SSN passed as argument. 
*								This method will be called on reception of SIP NOTIFY
*
* @param <p_state> - 
*
* @return <int> - 
*
*/
bool 
INGwLdDstMgr::isOpcSsnRegisteredWithStack (U32 p_selfPointCode, U8 p_selfSsn)
{
	LogINGwTrace(false, 0,"IN INGwLdDstMgr::isOpcSsnRegisteredWithStack()");

	bool retVal = true;
#if BOTH_OPC_SSN
	retVal = m_dstInfoMap->isSsnAvailable(p_selfPointCode, p_selfSsn);
#else
	U32 lPc =0;
	retVal = m_dstInfoMap->isSsnAvailable(lPc, p_selfSsn);
#endif

	LogINGwTrace(false, 0,"OUT INGwLdDstMgr::isOpcSsnRegisteredWithStack()");
	return retVal;
}

/**
* Description : This method will register SAS application for specific SSN and 
*							  OPC received in INFO message.
*
* @param <p_state> - 
*
* @return <int> - 
*
*/
int
INGwLdDstMgr::registerSASApp(U32 &p_selfPointCode, U8 &p_selfSsn, 
														 string &p_sasCallId)
{
 	LogINGwTrace(false, 0,"IN INGwLdDstMgr::registerSASApp()");

	int retVal = G_SUCCESS;

#if BOTH_OPC_SSN
	retVal = m_dstInfoMap->addDstInfo(p_selfPointCode, p_selfSsn, 
																		p_sasCallId);
#else
	U32 lPc =0;
	retVal = m_dstInfoMap->addDstInfo(lPc, p_selfSsn, p_sasCallId);
#endif

	LogINGwTrace(false, 0,"OUT INGwLdDstMgr::registerSASApp()");
	return retVal;
}

/**
* Description : This method will remove the entry from load distributor table 
*								if any SAS application asks for de-registration. 
*
* @param <p_state> - 
*
* @return <int> - 
*
*/
int
INGwLdDstMgr::deRegisterSASApp(U32 &p_selfPointCode, U8 &p_selfSsn, 
															 string &p_sasCallId)
{
	LogINGwTrace(false, 0,"IN INGwLdDstMgr::deRegisterSASApp()");

	int retVal = G_SUCCESS;
#if BOTH_OPC_SSN
	retVal = m_dstInfoMap->delDstInfo(p_selfPointCode, p_selfSsn, 
																		p_sasCallId);
#else
	U32 lPc =0;
	retVal = m_dstInfoMap->delDstInfo(lPc, p_selfSsn, p_sasCallId);
#endif

	LogINGwTrace(false, 0,"OUT INGwLdDstMgr::deRegisterSASApp()");
	return retVal;
}

/**
* Description : This method is called when IWF needs to find out destination 
*							  to route call to SAS for Inbound TCAP calls.. This method will 
*								decide route based on incoming SSS and OPC and number of SAS 
*								application registered with INGw
*
* @param <p_state> - 
*
* @return <int> - 
*
*/
string
INGwLdDstMgr::getDestSasInfo(U32 &p_pc, U8 &p_ssn)
{
	LogINGwTrace(false, 0,"IN INGwLdDstMgr::getDestSASAddr()");
	LogINGwTrace(false, 0,"OUT INGwLdDstMgr::getDestSASAddr()");
#if BOTH_OPC_SSN
	return (m_dstInfoMap->getDestSasInfo(p_pc, p_ssn));
#else
	U32 lPc =0;
	return (m_dstInfoMap->getDestSasInfo(lPc, p_ssn));
#endif
}

int
INGwLdDstMgr::removeSasApp(U32 &p_pc, U8 &p_ssn, 
													 string &p_sasCallId)
{
	LogINGwTrace(false, 0,"IN INGwLdDstMgr::removeSasApp()");
	LogINGwTrace(false, 0,"OUT INGwLdDstMgr::removeSasApp()");
#if BOTH_OPC_SSN
	return (m_dstInfoMap->removeSasInfo(p_pc, p_ssn, p_sasCallId));
#else
	U32 lPc =0;
	return (m_dstInfoMap->removeSasInfo(lPc, p_ssn, p_sasCallId));
#endif
}

vector<string>
INGwLdDstMgr::getAllRegSasApp(U32 &p_pc, U8 &p_ssn)
{
	LogINGwTrace(false, 0,"IN INGwLdDstMgr::getAllRegSasApp()");
	LogINGwTrace(false, 0,"OUT INGwLdDstMgr::getAllRegSasApp()");
#if BOTH_OPC_SSN
	return (m_dstInfoMap->getAllRegSasApp(p_pc, p_ssn));
#else
	U32 lPc =0;
	return (m_dstInfoMap->getAllRegSasApp(lPc, p_ssn));
#endif
}

/**
* Description : This method will be called to replicate the internal structure 
*								to peer INGw. It will serialize internal data structure.
*
* @param <p_state> - 
*
* @return <int> - 
*
*/
bool
INGwLdDstMgr::serialize (char*&  p_loadDistBuf, int &p_loadDistBufLen)
{
	LogINGwTrace(false, 0,"IN INGwLdDstMgr::serialize()");

	bool retVal = true;

	LogINGwTrace(false, 0,"OUT INGwLdDstMgr::serialize()");
	return retVal;
}

/**
* Description : This method will de-serialize the serialized buffer received 
*							  from peer INGw.
*
* @param <p_state> - 
*
* @return <int> - 
*
*/
bool
INGwLdDstMgr::deserialize(char* p_loadDistBuf, int p_loadDistBufLen)
{
 	LogINGwTrace(false, 0,"IN INGwLdDstMgr::deserialize()");

	bool retVal = true;

	LogINGwTrace(false, 0,"OUT INGwLdDstMgr::deserialize()");
	return retVal;
}

/**
* Description : This method will be called through telnet interface to dump 
*								all content of load distributor.
*
* @param <p_state> - 
*
* @return <int> - 
*
*/
string
INGwLdDstMgr::debugLoadDistInfo()
{
 	LogINGwTrace(false, 0,"IN INGwLdDstMgr::debugLoadDistInfo()");

	string toLogBuf = m_dstInfoMap->debugLog();

	LogINGwTrace(false, 0,"OUT INGwLdDstMgr::debugLoadDistInfo()");

	return toLogBuf;
}


/**
* Description : This method will be called through telnet interface to dump 
*								all content of load distributor.
*
* @param <p_state> - 
*
* @return <int> - 
*
*/
int
INGwLdDstMgr::extractOpcSsn(string &p_oidVal)
{
 	LogINGwTrace(false, 0,"IN INGwLdDstMgr::extractOpcSsn()");

	int retVal = G_SUCCESS;

	vector<string> opcSsnList;

	g_tokenizeValue(p_oidVal, G_PIPE_DELIM, opcSsnList);

	for (int i=0; i< opcSsnList.size(); ++i) {

		vector<string> fieldList;
		g_tokenizeValue(opcSsnList[i], G_COMMA_DELIM, fieldList);

		// Rajeev - temporary
    for (int i=0; i < fieldList.size(); ++i) {
			logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
			"Rajeev - fieldList[%d]=%s", i, fieldList[i].c_str());
		}

		if (true != fieldList.empty()) {
			INGwLdPcSsn pcSsnInfo;

			// Proto Type at index 3
			// We will get PC in zone-cluster-member format through EMS
			int pcolType =0;
      if(fieldList[3] == "JAPAN") {
        pcolType = 7;
				logger.logINGwMsg(false, ALWAYS_FLAG, 0, "Rajeev - Protocol Type is JAPAN");
      }
			else if (fieldList[3] == "ITU") {
				pcolType = 1;
			}
			else if (fieldList[3] == "ANSI") {
				pcolType = 0;
			}
      else{
        //default
        pcolType = 7;
      }

			logger.logINGwMsg(false, ALWAYS_FLAG, 0, "Rajeev - Protocol Type is %d-%s", 
							pcolType, fieldList[3].c_str());

			pcSsnInfo.m_pc  = g_convertPcToDec((char*)(fieldList[g_opcIndex].c_str()),
																         &pcSsnInfo.m_pcDetail[0], pcolType);

			pcSsnInfo.m_ssn = atoi(fieldList[g_ssnIndex].c_str());
			pcSsnInfo.m_protoType = pcolType;
			m_opcSsnConfigList.push_back(pcSsnInfo); 

			logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
			"[extractOpcSsn] opc[%d][%d-%d-%d] Ssn[%d]", pcSsnInfo.m_pc, 
			pcSsnInfo.m_pcDetail[0], pcSsnInfo.m_pcDetail[1], pcSsnInfo.m_pcDetail[2],
			pcSsnInfo.m_ssn);
		}

		fieldList.clear();
	}

	retVal = validateOpcSsn();

	LogINGwTrace(false, 0,"OUT INGwLdDstMgr::extractOpcSsn()");
	return retVal;
}

/**
* Description : This method will be called through telnet interface to dump 
*								all content of load distributor.
*
* @param <p_state> - 
*
* @return <int> - 
*
*/
int
INGwLdDstMgr::extractSsn(string &p_oidVal)
{
 	LogINGwTrace(false, 0,"IN INGwLdDstMgr::extractSsn()");

	int retVal = G_SUCCESS;

	vector<string> opcSsnList;
	map<int, int> ssnList;
	map<int, int>::iterator it;

	g_tokenizeValue(p_oidVal, G_PIPE_DELIM, opcSsnList);

	for (int i=0; i< opcSsnList.size(); ++i) {

		vector<string> fieldList;
		g_tokenizeValue(opcSsnList[i], G_COMMA_DELIM, fieldList);

		if (true != fieldList.empty()) {
			INGwLdPcSsn pcSsnInfo;

			// This list shall be based on SSN Only. 
			// Point code shall be marked as 0
			int pcolType =0;
      if(fieldList[3] == "JAPAN") {
        pcolType = 7;
				logger.logINGwMsg(false, ALWAYS_FLAG, 0, "INGwLdDstMgr::Protocol Type is JAPAN");
      }
			else if (fieldList[3] == "ITU") {
				pcolType = 1;
			}
			else if (fieldList[3] == "ANSI") {
				pcolType = 0;
			}
      else{
        //default
        pcolType = 7;
      }

			pcSsnInfo.m_pc  = 0;

			pcSsnInfo.m_ssn = atoi(fieldList[g_ssnIndex].c_str());
			pcSsnInfo.m_protoType = pcolType;

			if((it = ssnList.find(pcSsnInfo.m_ssn)) == ssnList.end())
				m_opcSsnConfigList.push_back(pcSsnInfo); 
			else
				continue;

			logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
			"[extractSsn] Ssn[%d]", pcSsnInfo.m_ssn);
		}

		fieldList.clear();
	}

	retVal = validateOpcSsn();

	LogINGwTrace(false, 0,"OUT INGwLdDstMgr::extractSsn()");
	return retVal;
}

int
INGwLdDstMgr::validateOpcSsn() 
{
	int retVal = G_SUCCESS;
  
	logger.logINGwMsg(false, ALWAYS_FLAG, 0,
  "[validateOpcSsn] m_opcSsnConfigList size = %d",m_opcSsnConfigList.size());
  
	for (int i=0; i < m_opcSsnConfigList.size(); ++i) {
        logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
        "m_opcSsnConfigList[%d].m_ssn= [%d] pc-detail[0]=[%d]"
        "pc-detail[1]=[%d] pc-detail[2]=[%d]",
        i,m_opcSsnConfigList[i].m_ssn,m_opcSsnConfigList[i].m_pcDetail[0],
        m_opcSsnConfigList[i].m_pcDetail[1],m_opcSsnConfigList[i].m_pcDetail[2]);
  }
	for (int i=0; i < m_opcSsnConfigList.size(); ++i) {
		if (G_SSN_MIN_LIMIT > m_opcSsnConfigList[i].m_ssn ||
				G_SSN_MAX_LIMIT < m_opcSsnConfigList[i].m_ssn ){
        logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
        "[validateOpcSsn] Failure Invalid SSN");
				retVal = G_FAILURE;
				break;
		}

#if BOTH_OPC_SSN
		if (((m_opcSsnConfigList[i].m_protoType == 1) && 
				((G_ITU_ZONE_MIN_LIMIT > m_opcSsnConfigList[i].m_pcDetail[0]    || 
				 G_ITU_ZONE_MAX_LIMIT <  m_opcSsnConfigList[i].m_pcDetail[0])    ||
			  (G_ITU_NETWORK_MIN_LIMIT > m_opcSsnConfigList[i].m_pcDetail[1] ||
				 G_ITU_NETWORK_MAX_LIMIT <  m_opcSsnConfigList[i].m_pcDetail[1]) ||
			  (G_ITU_SP_MIN_LIMIT > m_opcSsnConfigList[i].m_pcDetail[2] ||
				 G_ITU_SP_MAX_LIMIT <  m_opcSsnConfigList[i].m_pcDetail[2]))) ||

		   ((m_opcSsnConfigList[i].m_protoType == 7) && 
				((G_JAPAN_ZONE_MIN_LIMIT > m_opcSsnConfigList[i].m_pcDetail[0]    || 
				 G_JAPAN_ZONE_MAX_LIMIT <  m_opcSsnConfigList[i].m_pcDetail[0])    ||
			  (G_JAPAN_NETWORK_MIN_LIMIT > m_opcSsnConfigList[i].m_pcDetail[1] ||
				 G_JAPAN_NETWORK_MAX_LIMIT <  m_opcSsnConfigList[i].m_pcDetail[1]) ||
			  (G_JAPAN_SP_MIN_LIMIT > m_opcSsnConfigList[i].m_pcDetail[2] ||
				 G_JAPAN_SP_MAX_LIMIT <  m_opcSsnConfigList[i].m_pcDetail[2]))) ||

		   ((m_opcSsnConfigList[i].m_protoType == 0) && 
				((G_ANSI_ZONE_MIN_LIMIT > m_opcSsnConfigList[i].m_pcDetail[0]    || 
				 G_ANSI_ZONE_MAX_LIMIT <  m_opcSsnConfigList[i].m_pcDetail[0])    ||
			  (G_ANSI_NETWORK_MIN_LIMIT > m_opcSsnConfigList[i].m_pcDetail[1] ||
				 G_ANSI_NETWORK_MAX_LIMIT <  m_opcSsnConfigList[i].m_pcDetail[1]) ||
			  (G_ANSI_SP_MIN_LIMIT > m_opcSsnConfigList[i].m_pcDetail[2] ||
				 G_ANSI_SP_MAX_LIMIT <  m_opcSsnConfigList[i].m_pcDetail[2]))))
 		{
        logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
        "[validateOpcSsn] Failure Invalid PC (%s)", 
				(m_opcSsnConfigList[i].m_protoType == 1)?"ITU":
				(m_opcSsnConfigList[i].m_protoType == 0)?"ANSI":
				(m_opcSsnConfigList[i].m_protoType == 7)?"JAPAN":"UNKNOWN");
				retVal = G_FAILURE;
				break;
		}
#endif
	}

	return retVal;
}

bool
INGwLdDstMgr::isSasIpRegistered(U32 &p_pc, U8 &p_ssn, const string &p_sasIp)
{
	LogINGwTrace(false, 0,"IN INGwLdDstMgr::isSasIpRegistered()");
	LogINGwTrace(false, 0,"OUT INGwLdDstMgr::isSasIpRegistered()");

#if BOTH_OPC_SSN
	return (m_dstInfoMap->isSasIpRegistered(p_pc, p_ssn, p_sasIp));
#else
	U32 lPc=0;
	return (m_dstInfoMap->isSasIpRegistered(lPc, p_ssn, p_sasIp));
#endif
}

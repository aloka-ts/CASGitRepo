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
//     File:     INGwIwfBaseInterface.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwLoadDistributor");

#include <INGwLoadDistributor/INGwLdDstMap.h>

INGwLdDstMap::INGwLdDstMap():m_mapBucket(NULL)
{
}

INGwLdDstMap::~INGwLdDstMap()
{
  LogINGwTrace(false, 0, "IN INGwLdDstMap::Destructor()");

	if (NULL != m_mapBucket) {
		delete [] m_mapBucket;
	}

	LogINGwTrace(false, 0, "OUT INGwLdDstMap::Destructor()");
}

int
INGwLdDstMap::initialize(INGwLdPcSsnList &p_opcSsnList, string &p_patternType)
{
  LogINGwTrace(false, 0, "IN INGwLdDstMap::initialize()");

	int retVal = G_SUCCESS;
	INGwLdDstInfo *info = NULL;

	m_mapBucket = new INGwLdDstBucket[G_MAX_SSN_SIZE];

	// Now based on SSN and OPC initialize Data structure.
	for (int i=0; i < p_opcSsnList.size(); ++i) {

		info = new INGwLdDstInfo();

		info->addOpcSsnInfo(p_opcSsnList[i].m_pc, p_opcSsnList[i].m_ssn,
											  p_opcSsnList[i].m_pcDetail, p_patternType);

		bool found = false;

		for (int j=0; j < m_regSsn.size(); ++j) {
			if (m_regSsn[j] ==  p_opcSsnList[i].m_ssn) {
				found = true;
				break;
			}
		}

		if (false == found) {
			m_regSsn.push_back(p_opcSsnList[i].m_ssn);
		}

		m_mapBucket[p_opcSsnList[i].m_ssn].m_dstInfo.push_back(info);
	}

	LogINGwTrace(false, 0, "OUT INGwLdDstMap::initialize()");
	return retVal;
}

string
INGwLdDstMap::debugLog()
{
	LogINGwTrace(false, 0, "IN INGwLdDstMap::debugLog()");

	string toLogBuf;

	for (int i=0; i < m_regSsn.size(); ++i) {

		U8 lSsn = m_regSsn[i];

		for (int j=0; j < m_mapBucket[lSsn].m_dstInfo.size(); ++j) {
			toLogBuf += m_mapBucket[lSsn].m_dstInfo[j]->debugInfo();
		}
	}

	LogINGwTrace(false, 0, "OUT INGwLdDstMap::debugLog()");
	return toLogBuf;
}

int
INGwLdDstMap::addDstInfo(U32 &p_pc, U8 &p_ssn, string &p_sasCallId)
{
	LogINGwTrace(false, 0, "IN INGwLdDstMap::addDstInfo()");

	int retVal = G_FAILURE;

	if (true == p_sasCallId.empty()) {
		return (retVal);
	}

	logger.logINGwMsg(false, VERBOSE_FLAG, 0,
	"[addDstInfo] Rxed Inputs pc[%d] ssn[%d] sasAddr[%s]",
	p_pc, p_ssn, p_sasCallId.c_str());

	for (int idx=0; idx < m_mapBucket[p_ssn].m_dstInfo.size(); ++idx) {
		if ((*(m_mapBucket[p_ssn].m_dstInfo[idx])) == p_pc) {

			retVal = m_mapBucket[p_ssn].m_dstInfo[idx]->addSasId(p_sasCallId);

			logger.logINGwMsg(false, VERBOSE_FLAG, 0,
			"[addDstInfo] Added Inputs pc[%d] ssn[%d] sasAddr[%s]\n",
			p_pc, p_ssn, p_sasCallId.c_str());

			break;
 		}
	}

	LogINGwTrace(false, 0, "OUT INGwLdDstMap::addDstInfo()");
	return retVal;
}

int
INGwLdDstMap::delDstInfo(U32 &p_pc, U8 &p_ssn, string &p_sasCallId)
{
	LogINGwTrace(false, 0, "IN INGwLdDstMap::delDstInfo()");

	int retVal = G_FAILURE;
	bool found = false;

	if (true == p_sasCallId.empty()) {
		return retVal;
	}

	for (int idx=0; idx < m_mapBucket[p_ssn].m_dstInfo.size(); ++idx) {
		if (*(m_mapBucket[p_ssn].m_dstInfo[idx]) == p_pc) {

			retVal = m_mapBucket[p_ssn].m_dstInfo[idx]->delSasId(p_sasCallId);
			found = true;
			break;
 		}
	}

	if (false == found) {
		logger.logINGwMsg(false, ERROR_FLAG, 0,
		"[delDstInfo] Not deleting, entry not found for pc[%d] ssn[%d] sas[%s]",
		p_pc, p_ssn, p_sasCallId.c_str());
	}

	LogINGwTrace(false, 0, "OUT INGwLdDstMap::delDstInfo()");
	return retVal;
}

int 
INGwLdDstMap::removeSasInfo(U32 &p_pc, U8 &p_ssn, string &p_sasCallId)
{
	LogINGwTrace(false, 0, "IN INGwLdDstMap::removeSasInfo()");

	int retVal = G_FAILURE;

	if (true == p_sasCallId.empty()) {
		return retVal;
	}

	bool found = false;
	for (int i=0;((i < m_regSsn.size()) && (found == false)); ++i) {

		U8 lSsn = m_regSsn[i];

		for (int j=0; j < m_mapBucket[lSsn].m_dstInfo.size(); ++j) {
			found = m_mapBucket[lSsn].m_dstInfo[j]->belongTo(p_sasCallId);

			if (true == found) {
				m_mapBucket[lSsn].m_dstInfo[j]->getOpcSsn(p_pc, p_ssn);
				break;
			}
		} // end of inter loop
	} // end of outer for

	if (true == found) {
		retVal = delDstInfo(p_pc, p_ssn, p_sasCallId);
	}
	else {
		logger.logINGwMsg(false, ERROR_FLAG, 0,
		"[removeSasInfo] Not deleting, entry not found for pc[%d] ssn[%d] sas[%s]",
		p_pc, p_ssn, p_sasCallId.c_str());
	}

	LogINGwTrace(false, 0, "OUT INGwLdDstMap::removeSasInfo()");
	return retVal;
}

int
INGwLdDstMap::markDstUnavailable(U32 &p_pc, U8 &p_ssn)
{
	LogINGwTrace(false, 0, "IN INGwLdDstMap::markDstUnavailable()");

	int retVal = G_FAILURE;

	for (int idx=0; idx < m_mapBucket[p_ssn].m_dstInfo.size(); ++idx) {
		if (*(m_mapBucket[p_ssn].m_dstInfo[idx]) == p_pc) {
			m_mapBucket[p_ssn].m_dstInfo[idx]->markSsnUnavailable();
			retVal = G_SUCCESS;
			break;
 		}
	}

	LogINGwTrace(false, 0, "OUT INGwLdDstMap::markDstUnavailable()");
	return retVal;
}

int
INGwLdDstMap::markDstAvailable(U32 &p_pc, U8 &p_ssn)
{
	LogINGwTrace(false, 0, "IN INGwLdDstMap::markDstAvailable()");

	int retVal = G_FAILURE;

	for (int idx=0; idx < m_mapBucket[p_ssn].m_dstInfo.size(); ++idx) {
		if (*(m_mapBucket[p_ssn].m_dstInfo[idx]) == p_pc) {
			m_mapBucket[p_ssn].m_dstInfo[idx]->markSsnAvailable();
			retVal = G_SUCCESS;
			break;
 		}
	}

	LogINGwTrace(false, 0, "OUT INGwLdDstMap::markDstAvailable()");
	return retVal;
}

bool
INGwLdDstMap::isSsnAvailable(U32 &p_pc, U8 &p_ssn) 
{
	LogINGwTrace(false, 0, "IN INGwLdDstMap::isSsnAvailable()");

	bool retVal = false;

	for (int idx=0; idx < m_mapBucket[p_ssn].m_dstInfo.size(); ++idx) {
		if (*(m_mapBucket[p_ssn].m_dstInfo[idx]) == p_pc) {
			retVal = m_mapBucket[p_ssn].m_dstInfo[idx]->isSsnAvailable();
			break;
 		}
	}

	LogINGwTrace(false, 0, "OUT INGwLdDstMap::isSsnAvailable()");
	return retVal;
}

string
INGwLdDstMap::getDestSasInfo(U32 &p_pc, U8 &p_ssn)
{
	LogINGwTrace(false, 0, "IN INGwLdDstMap::getDestSasInfo()");

	string retVal;

	for (int idx=0; idx < m_mapBucket[p_ssn].m_dstInfo.size(); ++idx) {
		if (*(m_mapBucket[p_ssn].m_dstInfo[idx]) == p_pc) {
			return (m_mapBucket[p_ssn].m_dstInfo[idx]->getDestSasInfo());
 		}
	}
	LogINGwTrace(false, 0, "OUT INGwLdDstMap::getDestSasInfo()");
	return retVal;
}

vector<string>
INGwLdDstMap::getAllRegSasApp(U32 &p_pc, U8 &p_ssn)
{
	LogINGwTrace(false, 0, "IN INGwLdDstMap::getAllRegSasApp()");

	vector<string> retVal;

	logger.logINGwMsg(false, VERBOSE_FLAG, 0,
	"[getAllRegSasApp] Rxed pc[%d] ssn[%d]", p_pc, p_ssn);

	for (int idx=0; idx < m_mapBucket[p_ssn].m_dstInfo.size(); ++idx) {
		if (*(m_mapBucket[p_ssn].m_dstInfo[idx]) == p_pc) {
			
			logger.logINGwMsg(false, VERBOSE_FLAG, 0,
			"[getAllRegSasApp] Found pc[%d] ssn[%d]", p_pc, p_ssn);
			return (m_mapBucket[p_ssn].m_dstInfo[idx]->getAllRegSasApp());
 		}
	}
	LogINGwTrace(false, 0, "OUT INGwLdDstMap::AllRegSasApp()");
	return retVal;
}

int
INGwLdDstMap::getNumOfSasAppReg(U32 &p_pc, U8 &p_ssn)
{
	LogINGwTrace(false, 0, "IN INGwLdDstMap::getNumOfSasAppReg()");

	int retVal = 0;

	for (int idx=0; idx < m_mapBucket[p_ssn].m_dstInfo.size(); ++idx) {
		if (*(m_mapBucket[p_ssn].m_dstInfo[idx]) == p_pc) {
			retVal = m_mapBucket[p_ssn].m_dstInfo[idx]->getNumOfSasAppReg();
			break;
 		}
	}

	LogINGwTrace(false, 0, "OUT INGwLdDstMap::getNumOfSasAppReg()");
	return retVal;
}

bool
INGwLdDstMap::isSasIpRegistered(U32 &p_pc, U8 &p_ssn, const string &p_sasIp)
{
	LogINGwTrace(false, 0, "IN INGwLdDstMap::isSasIpRegistered()");
	bool retVal = false;

	for (int idx=0; idx < m_mapBucket[p_ssn].m_dstInfo.size(); ++idx) {
		if (*(m_mapBucket[p_ssn].m_dstInfo[idx]) == p_pc) {
				retVal = m_mapBucket[p_ssn].m_dstInfo[idx]->isSasIpRegistered(p_sasIp);
				break;
		}
	}

	LogINGwTrace(false, 0, "OUT INGwLdDstMap::isSasIpRegistered()");
	return retVal;
}

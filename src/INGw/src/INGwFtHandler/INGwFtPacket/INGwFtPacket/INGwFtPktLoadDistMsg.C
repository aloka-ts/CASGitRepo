//////////////////////////////////////////////////////////////////////////
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
//     File:    INGwFtPktLoadDistMsg.C
//
//     Desc:     
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya    31/01/08     Initial Creation
//********************************************************************
#include <INGwFtPacket/INGwFtPktLoadDistMsg.h>


INGwFtPktLoadDistMsg::INGwFtPktLoadDistMsg():
			m_LoadDistMsgType(0)
{
    mMsgData.msMsgType = MSG_LOAD_DIST_MSG;
}

INGwFtPktLoadDistMsg::~INGwFtPktLoadDistMsg()
{
}

void 
INGwFtPktLoadDistMsg::initialize(short p_loadDistType, short p_srcid, short p_destid)
{
	mMsgData.msSender   = p_srcid;
	mMsgData.msReceiver = p_destid;
	m_LoadDistMsgType	  = p_loadDistType;
}

int 
INGwFtPktLoadDistMsg::depacketize(const char* apcData, int asSize, int version)
{
	if (1 != version) {
		return 0;
	}

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	memcpy(&m_LoadDistMsgType, apcData + offset, SIZE_OF_SHORT);
	m_LoadDistMsgType = ntohs(m_LoadDistMsgType);
	offset += SIZE_OF_SHORT;

	// total OPC/SSN
	short totalOpcCount = 0;
	memcpy(&totalOpcCount, apcData + offset, SIZE_OF_SHORT);
	totalOpcCount = ntohs(totalOpcCount);
	offset += SIZE_OF_SHORT;

	for (int i=0; i  < totalOpcCount; ++i) {
		U32 pc = 0;
		U8 ssn = 0;
		t_loadDistInfo info;

		// opc
		memcpy(&pc, apcData + offset, sizeof(U32));
		info.m_pc = ntohs(pc);
		offset += sizeof(U32);

		// ssn
		memcpy(&ssn, apcData + offset, sizeof(U8)); 
		info.m_ssn = ntohs(ssn);
		offset += sizeof(U8);

		short numOfSasAddr = 0;

		// count of sas
		memcpy(&numOfSasAddr, apcData + offset, SIZE_OF_SHORT);
		numOfSasAddr = ntohs(numOfSasAddr);
		offset += SIZE_OF_SHORT;

		for (int j=0; j < numOfSasAddr; ++j) {

			// size of sas address
			short value = 0;
			memcpy(&value, apcData + offset, SIZE_OF_SHORT);
			value = ntohs(value);
			offset += SIZE_OF_SHORT;

			// sas address
			strncpy(info.m_sasIp[info.m_sasIpCnt], apcData + offset, value);
			info.m_sasIpCnt++;
			offset += value;

		}

		m_info.push_back(info);
	}

   return offset;
}

int 
INGwFtPktLoadDistMsg::packetize(char** apcData, int version)
{
	if (1 != version) {
		return 0;
	}

	*apcData = NULL;

	// Assuming this data won't be more that 1k
	// This is done to avoid second pass
	char lBuf[1000];
	int lBufLen = 0;
	short value =0;

	// register, de-register
	value = htons(m_LoadDistMsgType);
	memcpy(lBuf + lBufLen, (void*)&value, SIZE_OF_SHORT);
	lBufLen += SIZE_OF_SHORT;

	// total OPC/SSN
	value = htons(m_info.size());
	memcpy(lBuf + lBufLen, (void*)&value, SIZE_OF_SHORT);
	lBufLen += SIZE_OF_SHORT;

	for (int i=0; i  < m_info.size(); ++i) {
		U32 pc  = htons(m_info[i].m_pc);
		U8  ssn = htons(m_info[i].m_ssn);

		// opc
		memcpy(lBuf + lBufLen, (void*)&pc, sizeof(U32)); 
		lBufLen += sizeof(U32);

		// ssn
		memcpy(lBuf + lBufLen, (void*)&ssn, sizeof(U8)); 
		lBufLen += sizeof(U8);

		value = htons(m_info[i].m_sasIpCnt);

		// count of sas
		memcpy(lBuf + lBufLen, (void*)&value, SIZE_OF_SHORT);
		lBufLen += SIZE_OF_SHORT;

		for (int j=0; j < m_info[i].m_sasIpCnt; ++j) {

			// size of sas address
			value = htons(strlen(m_info[i].m_sasIp[j]));

			memcpy(lBuf + lBufLen, (void*)&value, SIZE_OF_SHORT);
			lBufLen += SIZE_OF_SHORT;

			// sas address
			memcpy(lBuf + lBufLen, (void*)m_info[i].m_sasIp[j],
								strlen(m_info[i].m_sasIp[j]));
			lBufLen +=  strlen(m_info[i].m_sasIp[j]);
		}
	}

   int offset = 
      INGwFtPktMsg::createPacket(lBufLen , apcData, version);

   char *pkt = *apcData;

   memcpy(pkt + offset, (void*)lBuf, lBufLen);
   offset += lBufLen;

   return offset;
}


std::string 
INGwFtPktLoadDistMsg::toLog(void) const
{
	std::ostringstream    oStr;
	std::string lRoleStr = (m_LoadDistMsgType == REGISTER_SAS_APP)? 
					 				      " - REGISTER_SAS_APP" :
									 (m_LoadDistMsgType == DEREGISTER_SAS_APP) ?
									 " - DEREGISTER_SAS_APP" : " - DELETE_SAS_APP";

	oStr << INGwFtPktMsg::toLog();
	oStr << " , Load Distributor : "  << m_LoadDistMsgType << lRoleStr ;

	for (int i=0; i  < m_info.size(); ++i) {

		oStr << "OPC: " <<  m_info[i].m_pc << ", SSN: " ;
		oStr << (int)m_info[i].m_ssn << ", " ;

		for (int j=0; j < m_info[i].m_sasIpCnt; ++j) {
			oStr << "Sas Addr: " << m_info[i].m_sasIp[j] << " "; 
		}

		oStr << " ]";
	}

	return oStr.str();
}

short 
INGwFtPktLoadDistMsg::getLoadDistMsgType()
{
	return m_LoadDistMsgType;
}

int
INGwFtPktLoadDistMsg::appendSasDestAddrToList(vector<string> &p_sasIP, 
																					U32 &p_pc, U8 &p_ssn)
{
	t_loadDistInfo info;

	info.m_pc = p_pc;
	info.m_ssn = p_ssn;

	for (int i=0; i < p_sasIP.size(); ++i ) {
		strncpy(info.m_sasIp[i], p_sasIP[i].c_str(), p_sasIP[i].size());
		info.m_sasIpCnt++;
	}

	m_info.push_back(info);

	return m_info.size();
}

int
INGwFtPktLoadDistMsg::appendSasDestAddrToList(string &p_sasIP, 
																					U32 &p_pc, U8 &p_ssn)
{
	t_loadDistInfo info;

	info.m_pc = p_pc;
	info.m_ssn = p_ssn;

	strncpy(info.m_sasIp[info.m_sasIpCnt], p_sasIP.c_str(), p_sasIP.size());
	info.m_sasIpCnt++;

	m_info.push_back(info);

	return m_info.size();
}

vector<t_loadDistInfo>
INGwFtPktLoadDistMsg::getLoadDistInfo()
{
	return m_info;
}


void
INGwFtPktLoadDistMsg::getLoadDistInfo(vector<t_loadDistInfo> &p_info)
{
	p_info = m_info;
}

int
INGwFtPktLoadDistMsg::getLdDistMsgCnt()
{
	int retVal= m_info.size();
	return retVal;
}

void
INGwFtPktLoadDistMsg::getPcSsnAtIndex(U32 &p_pc, U8 &p_ssn, int &p_sasIpCnt, int &p_indx)
{
	if (0 == m_info.size() || p_indx > m_info.size()) {
		return;
	}

	p_pc  = m_info[p_indx].m_pc;
	p_ssn = m_info[p_indx].m_ssn;
	p_sasIpCnt = m_info[p_indx].m_sasIpCnt;
}

string
INGwFtPktLoadDistMsg::getIpFromInfoAtIndex(int &p_infoIndx, int &p_ipIndx)
{
	string retVal = "";

	if (0 == m_info.size() || p_infoIndx > m_info.size()) {
		return retVal;
	}

	if (p_ipIndx > m_info[p_infoIndx].m_sasIpCnt ) {
		return retVal;
	}

	retVal =  m_info[p_infoIndx].m_sasIp[p_ipIndx];
	return retVal;
}

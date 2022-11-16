#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktAddNw.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktAddNw::INGwFtPktAddNw()
{
   mMsgData.msMsgType   = MSG_ADD_NW;
	 m_isAddNwSet       	= false;
	 memset(&m_addNw, 0, sizeof(AddNetwork));
}

void
INGwFtPktAddNw::initialize(short senderid, short receiverid, AddNetwork &addNw)
{
	mMsgData.msSender    = senderid;
	mMsgData.msReceiver  = receiverid;
	m_isAddNwSet = true;
	memcpy(&m_addNw, &addNw, sizeof(AddNetwork));
}

INGwFtPktAddNw::~INGwFtPktAddNw()
{
}

int 
INGwFtPktAddNw::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktAddNw::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddNw::depacketize buffSize:%d", asSize);

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U16 lSdata =0;
	U32 lLdata =0;

	// U16 nwId
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addNw.nwId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// U16 variant
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addNw.variant = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// Bool spcBroadcastOn
	memcpy((void*)&m_addNw.spcBroadcastOn, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U32 defaultPc
	memcpy((void*)&lLdata, apcData + offset, SIZE_OF_LONG);
	m_addNw.defaultPc = ntohl(lLdata);
	offset += SIZE_OF_LONG;

	// U8  nmbSpcs
	memcpy((void*)&m_addNw.nmbSpcs, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U32 selfPc
	for(int i =0; i < m_addNw.nmbSpcs; ++i)
	{
		memcpy((void*)&lLdata, apcData + offset, SIZE_OF_LONG);
		m_addNw.selfPc[i] = ntohl(lLdata);
		offset += SIZE_OF_LONG;
	}

	// U8 niInd
	memcpy((void*)&m_addNw.niInd, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 subService
	memcpy((void*)&m_addNw.subService, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// Number for nwkApp
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	U16 sizeOfNwkApp = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// U32 nwkApp
	for(int i=0; i < sizeOfNwkApp; ++i)
	{
		memcpy((void*)&lLdata, apcData + offset, SIZE_OF_LONG);
		m_addNw.nwkApp[i] = ntohl(lLdata);
		offset += SIZE_OF_LONG;
	}

	// U8  ssf
	memcpy((void*)&m_addNw.ssf, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 dpcLen
	memcpy((void*)&m_addNw.dpcLen, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 slsLen
	memcpy((void*)&m_addNw.slsLen, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// S16 suSwtch
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addNw.suSwtch=ntohs(lSdata);
 	offset += SIZE_OF_SHORT;

	// S16 su2Swtch
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addNw.su2Swtch=ntohs(lSdata);
 	offset += SIZE_OF_SHORT;

	// S16 protoType
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addNw.protoType=ntohs(lSdata);
 	offset += SIZE_OF_SHORT;

	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktAddNw::depacketize");
	return (offset);
}

int INGwFtPktAddNw::packetize(char** apcData, int version)
{
	// calculate size of buffer to be created
	int actualSize = sizeof(m_addNw);

	int size2 =0;
	U16 ii =0;
	if(m_addNw.nwkApp[0] != 0)
	{
		for(ii=0; ii < LIT_MAX_PSP; ++ii)
		{
			if(m_addNw.nwkApp[ii] == 0)
				break;
		}

		size2 = (LIT_MAX_PSP-ii)*sizeof(U32);
	}
	else
	{
		size2 = LIT_MAX_PSP*sizeof(U32);
	}

	// Self PC to be encoded
	size2 += (MAXSPC-m_addNw.nmbSpcs)*sizeof(U32);

	// actual size to be allocated. 
	// Size of Short refer to number of nwkApp
	int sizeAllocate = actualSize - size2 + SIZE_OF_SHORT;

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddNw::packetize ActualSize:%d sizeDeducted:%d sizeAllocat+base:%d",
			actualSize, size2, (sizeAllocate+SIZE_OF_BPMSG));

	int offset = INGwFtPktMsg::createPacket(sizeAllocate, apcData, version);

	char *pkt = *apcData;

	if(!m_isAddNwSet)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwFtPktAddNw::packetize AddNw has not been set");
	}

	U16 lSdata =0;
	U32 lLdata =0;

	// U16 nwId
	lSdata = htons(m_addNw.nwId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// U16 variant
	lSdata = htons(m_addNw.variant);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// Bool spcBroadcastOn
	memcpy(pkt + offset, (void*)&m_addNw.spcBroadcastOn, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U32 defaultPc
	lLdata = htonl(m_addNw.defaultPc);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
	offset += SIZE_OF_LONG;

	// U8  nmbSpcs
	memcpy(pkt + offset, (void*)&m_addNw.nmbSpcs, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U32 selfPc
	for(int i =0; i < m_addNw.nmbSpcs; ++i)
	{
		lLdata = htonl(m_addNw.selfPc[i]);
		memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
		offset += SIZE_OF_LONG;
	}

	// U8 niInd
	memcpy(pkt + offset, (void*)&m_addNw.niInd, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 subService
	memcpy(pkt + offset, (void*)&m_addNw.subService, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// Number for nwkApp
	lSdata = htons(ii);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// U32 nwkApp
	for(int i=0; i < LIT_MAX_PSP; ++i)
	{
		if(m_addNw.nwkApp[i] == 0)
			break;

		lLdata = htonl(m_addNw.nwkApp[i]);
		memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
		offset += SIZE_OF_LONG;
	}

	// U8  ssf
	memcpy(pkt + offset, (void*)&m_addNw.ssf, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 dpcLen
	memcpy(pkt + offset, (void*)&m_addNw.dpcLen, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 slsLen
	memcpy(pkt + offset, (void*)&m_addNw.slsLen, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// S16 suSwtch
	lSdata = htons(m_addNw.suSwtch);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
 	offset += SIZE_OF_SHORT;

	// S16 su2Swtch
	lSdata = htons(m_addNw.su2Swtch);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
 	offset += SIZE_OF_SHORT;

	// S16 protoType
	lSdata = htons(m_addNw.protoType);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
 	offset += SIZE_OF_SHORT;

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddNw::packetize, offset:%d actualLength:%d", offset, 
																					(sizeAllocate+SIZE_OF_BPMSG)); 

	return (offset);
}

void
INGwFtPktAddNw::getNwData(AddNetwork &addNw)
{
	memcpy(&addNw, &m_addNw, sizeof(AddNetwork));
}

void
INGwFtPktAddNw::setNwData(AddNetwork &addNw)
{
	m_isAddNwSet = true;
	memcpy(&m_addNw, &addNw, sizeof(AddNetwork));
}

std::string INGwFtPktAddNw::toLog(void) const
{
   char buf[4000];
   int len =0;

	 memset(buf, 0, sizeof(buf));

   len += sprintf((buf + len), "%s", INGwFtPktMsg::toLog().c_str());
   len += sprintf((buf + len), 
					"Add Network Request, NwId:%d, Variant:%d, defaultPc:%d, "
					"nmbSpcs:%d, niInd:%d, subService:%d, slsLen:%d, ssf:%d, "
					"dpcLen:[%d], suSwtch:%d, su2Swtch:%d, protoType:%d", m_addNw.nwId, 
					m_addNw.variant, m_addNw.defaultPc, m_addNw.nmbSpcs,
       		m_addNw.niInd, m_addNw.subService, m_addNw.slsLen, m_addNw.ssf,
					m_addNw.dpcLen, m_addNw.suSwtch, m_addNw.su2Swtch, m_addNw.protoType);

					for(int i=0; i < m_addNw.nmbSpcs; ++i)
					{
						len += sprintf((buf + len), ", SelfPc[%d|%d]", i, 
															m_addNw.selfPc[i]);
					}

					for(int i=0; i < LIT_MAX_PSP; ++i)
					{
						len += sprintf((buf + len), ", NwApp[%d]:%d", i,
													m_addNw.nwkApp[i]);
					}

   return buf;
}


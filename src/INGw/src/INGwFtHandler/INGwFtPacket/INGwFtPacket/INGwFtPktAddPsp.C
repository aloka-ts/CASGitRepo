#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktAddPsp.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktAddPsp::INGwFtPktAddPsp()
{
   mMsgData.msMsgType   = MSG_ADD_PSP;
	 m_isAddPspSet       	= false;
	 memset(&m_addPsp, 0, sizeof(AddPsp));
}

void
INGwFtPktAddPsp::initialize(short senderid, short receiverid, AddPsp& addPsp)
{
  mMsgData.msSender    = senderid;
  mMsgData.msReceiver  = receiverid;
	m_isAddPspSet = true;
	memcpy(&m_addPsp, &addPsp, sizeof(AddPsp));
}

INGwFtPktAddPsp::~INGwFtPktAddPsp()
{
}

int 
INGwFtPktAddPsp::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktAddPsp::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddPsp::depacketize buffSize:%d", asSize);

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U16 lSdata =0;
	U32 lLdata =0;

	// U32 pspId
	memcpy((void*)&lLdata, apcData + offset, SIZE_OF_LONG);
	m_addPsp.pspId = htonl(lLdata); 
	offset += SIZE_OF_LONG;

	// U8 pspType
	memcpy((void*)&m_addPsp.pspType, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 ipspMode
	memcpy((void*)&m_addPsp.ipspMode, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 nmbAddr
	memcpy((void*)&m_addPsp.nmbAddr, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// IPAddr addr
	for(int i=0; i < m_addPsp.nmbAddr; ++i)
	{
		memcpy((void*)&m_addPsp.addr[i].type, apcData + offset, SIZE_OF_CHAR);
		offset += SIZE_OF_CHAR;

		if(m_addPsp.addr[i].type == CM_TPTADDR_IPV4)
		{
			// U32 ipv4NetAddr
			memcpy((void*)&lLdata, apcData + offset, SIZE_OF_LONG);
			m_addPsp.addr[i].u.ipv4NetAddr = htonl(lLdata); 
			offset += SIZE_OF_LONG;
		}
		else { // IPV6
			memcpy((void*)&m_addPsp.addr[i].u.ipv6NetAddr,  apcData + offset,
										sizeof(CmIpAddr6));
			offset += sizeof(CmIpAddr6);
		}
	}

	// U16 dstPort
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addPsp.dstPort = htons(lSdata); 
	offset += SIZE_OF_SHORT;

	// U8 nwkId
	memcpy((void*)&m_addPsp.nwkId, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// Bool includeRC
	memcpy((void*)&m_addPsp.includeRC, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// Bool cfgForAllLps
	memcpy((void*)&m_addPsp.cfgForAllLps, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 currentPspState
	memcpy((void*)&m_addPsp.currentPspState, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktAddPsp::depacketize");
	return (offset);
}

int INGwFtPktAddPsp::packetize(char** apcData, int version)
{
	// calculate size of buffer to be created
	int size = sizeof(AddPsp) -
						 (SCT_MAX_NET_ADDRS*sizeof(IPAddr));

	int size2 =0;
	for(int i=0; i < m_addPsp.nmbAddr; ++i)
	{
		if(m_addPsp.addr[i].type == CM_TPTADDR_IPV4)
			size2 += 5;
		else
			size2 += 17;
	}

	// actual size
	size += size2;

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddPsp::packetize sizeAllocated:%d size2:%d", size, size2);

	int offset = INGwFtPktMsg::createPacket(size, apcData, version);

	char *pkt = *apcData;

	if(!m_isAddPspSet)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwFtPktAddPsp::packetize AddPsp has not been set");
	}

	U16 lSdata =0;
	U32 lLdata =0;

	// U32 pspId
	lLdata = htonl(m_addPsp.pspId);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
	offset += SIZE_OF_LONG;

	// U8 pspType
	memcpy(pkt + offset, (void*)&m_addPsp.pspType, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 ipspMode
	memcpy(pkt + offset, (void*)&m_addPsp.ipspMode, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 nmbAddr
	memcpy(pkt + offset, (void*)&m_addPsp.nmbAddr, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// IPAddr addr
	for(int i=0; i < m_addPsp.nmbAddr; ++i)
	{
		memcpy(pkt + offset, (void*)&m_addPsp.addr[i].type, SIZE_OF_CHAR);
		offset += SIZE_OF_CHAR;

		if(m_addPsp.addr[i].type == CM_TPTADDR_IPV4)
		{
			// U32 ipv4NetAddr
			lLdata = htonl(m_addPsp.addr[i].u.ipv4NetAddr);
			memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
			offset += SIZE_OF_LONG;
		}
		else { // IPV6
			memcpy(pkt + offset, (void*)&m_addPsp.addr[i].u.ipv6NetAddr, 
										sizeof(CmIpAddr6));
			offset += sizeof(CmIpAddr6);
		}
	}

	// U16 dstPort
	lSdata = htons(m_addPsp.dstPort);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// U8 nwkId
	memcpy(pkt + offset, (void*)&m_addPsp.nwkId, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// Bool includeRC
	memcpy(pkt + offset, (void*)&m_addPsp.includeRC, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// Bool cfgForAllLps
	memcpy(pkt + offset, (void*)&m_addPsp.cfgForAllLps, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 currentPspState
	memcpy(pkt + offset, (void*)&m_addPsp.currentPspState, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddPsp::packetize, offset:%d actualLength:%d", offset, size); 

	return (offset);
}

void
INGwFtPktAddPsp::getPspData(AddPsp &addPsp)
{
	memcpy(&addPsp, &m_addPsp, sizeof(AddPsp));
}

void
INGwFtPktAddPsp::setPspData(AddPsp &addPsp)
{
	m_isAddPspSet = true;
	memcpy(&m_addPsp, &addPsp, sizeof(AddPsp));
}

std::string INGwFtPktAddPsp::toLog(void) const
{
   char buf[4000];
   int len =0;

	 memset(buf, 0, sizeof(buf));

   len += sprintf((buf + len), "%s", INGwFtPktMsg::toLog().c_str());
   len += sprintf((buf + len), 
					"AddPsp: pspId:%d, pspType:%d, ipspMode:%d, nmbAddr:%d, dstPort:%d"
					", nwkId:%d, includeRC:%d, cfgForAllLps:%d, currentPspState:%d",
					m_addPsp.pspId, m_addPsp.pspType, m_addPsp.ipspMode, 
					m_addPsp.nmbAddr, m_addPsp.dstPort, m_addPsp.nwkId,
					m_addPsp.includeRC, m_addPsp.cfgForAllLps, m_addPsp.currentPspState);

		for(int i=0; i < m_addPsp.nmbAddr; ++i)
		{
			if(m_addPsp.addr[i].type == CM_TPTADDR_IPV4)
			{
				len += sprintf((buf + len), " Addr[%d]:type:%d,%d",i,
								m_addPsp.addr[i].type, m_addPsp.addr[i].u.ipv4NetAddr);
			}
			else {
				len += sprintf((buf + len), " Addr[%d]:type:%d,",i,
								m_addPsp.addr[i].type);

				for(int l=0; l < sizeof(CmIpAddr6); ++l)
				{
					len += sprintf((buf + len), "%d-", 
						m_addPsp.addr[i].u.ipv6NetAddr[l]);
				}
			}
		}

   return buf;
}


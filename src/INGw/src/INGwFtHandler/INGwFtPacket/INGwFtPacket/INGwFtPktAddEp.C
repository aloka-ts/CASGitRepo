#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktAddEp.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktAddEp::INGwFtPktAddEp()
{
   mMsgData.msMsgType   = MSG_ADD_ENDPOINT;
	 m_isAddEpSet       	= false;
	 memset(&m_addEp, 0, sizeof(AddEndPoint));
}

void
INGwFtPktAddEp::initialize(short senderid, short receiverid, AddEndPoint& addEp)
{
  mMsgData.msSender    = senderid;
  mMsgData.msReceiver  = receiverid;
	m_isAddEpSet = true;
	memcpy(&m_addEp, &addEp, sizeof(AddEndPoint));
}

INGwFtPktAddEp::~INGwFtPktAddEp()
{
}

int 
INGwFtPktAddEp::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktAddEp::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddEp::depacketize buffSize:%d", asSize);

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U16 lSdata =0;
	U32 lLdata =0;

	// U8 endPointid
	memcpy((void*)&m_addEp.endPointid, apcData+offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U16           srcPort
	memcpy((void*)&lSdata, apcData+offset, SIZE_OF_SHORT);
	m_addEp.srcPort = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// U8 nmbAddrs
	memcpy((void*)&m_addEp.nmbAddrs, apcData+offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// CMNetAddr     nAddr
	for(int i=0; i < m_addEp.nmbAddrs; ++i)
	{
		memcpy((void*)&m_addEp.nAddr[i].type, apcData+offset, SIZE_OF_CHAR);
		offset += SIZE_OF_CHAR;

		if(m_addEp.nAddr[i].type == CM_TPTADDR_IPV4)
		{
			// U32 ipv4NetAddr
			memcpy((void*)&lLdata, apcData+offset, SIZE_OF_LONG);
			m_addEp.nAddr[i].u.ipv4NetAddr = ntohl(lLdata); 
			offset += SIZE_OF_LONG;
		}
		else { // IPV6
			memcpy((void*)&m_addEp.nAddr[i].u.ipv6NetAddr, apcData+offset,
										sizeof(CmIpAddr6));
			offset += sizeof(CmIpAddr6);
		}
	}

	// U16 sctpProcId
	memcpy((void*)&lSdata, apcData+offset, SIZE_OF_SHORT);
	m_addEp.sctpProcId = ntohs(lSdata); 
	offset += SIZE_OF_SHORT;

	// S16 sctpLsapId
	memcpy((void*)&lSdata, apcData+offset, SIZE_OF_SHORT);
	m_addEp.sctpLsapId = ntohs(lSdata); 
	offset += SIZE_OF_SHORT;

	// S16 sctpUsapId
	memcpy((void*)&lSdata, apcData+offset, SIZE_OF_SHORT);
	m_addEp.sctpUsapId = ntohs(lSdata); 
	offset += SIZE_OF_SHORT;

	// S16 m3uaLsapId
	memcpy((void*)&lSdata, apcData+offset, SIZE_OF_SHORT);
	m_addEp.m3uaLsapId = ntohs(lSdata); 
	offset += SIZE_OF_SHORT;

	// S16 tuclUsapId
	memcpy((void*)&lSdata, apcData+offset, SIZE_OF_SHORT);
	m_addEp.tuclUsapId = ntohs(lSdata); 
	offset += SIZE_OF_SHORT;

	// U8 currentEpState
	memcpy((void*)&m_addEp.currentEpState, apcData+offset,SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktAddEp::depacketize");
	return (offset);
}

int INGwFtPktAddEp::packetize(char** apcData, int version)
{
	// calculate size of buffer to be created
	int size = sizeof(AddEndPoint) -
						 (SCT_MAX_NET_ADDRS*sizeof(CMNetAddr));

	int size2 =0;
	for(int i=0; i < m_addEp.nmbAddrs; ++i)
	{
		if(m_addEp.nAddr[i].type == CM_TPTADDR_IPV4)
			size2 += 5;
		else
			size2 += 17;
	}

	// actual size
	size += size2;

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddEp::packetize sizeAllocated:%d size2:%d", size, size2);

	int offset = INGwFtPktMsg::createPacket(size, apcData, version);

	char *pkt = *apcData;

	if(!m_isAddEpSet)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwFtPktAddEp::packetize AddEp has not been set");
	}

	U16 lSdata =0;
	U32 lLdata =0;

	// U8 endPointid
	memcpy(pkt + offset, (void*)&m_addEp.endPointid, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U16           srcPort
	lSdata = htons(m_addEp.srcPort);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// U8 nmbAddrs
	memcpy(pkt + offset, (void*)&m_addEp.nmbAddrs, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// CMNetAddr     nAddr
	for(int i=0; i < m_addEp.nmbAddrs; ++i)
	{
		memcpy(pkt + offset, (void*)&m_addEp.nAddr[i].type, SIZE_OF_CHAR);
		offset += SIZE_OF_CHAR;

		if(m_addEp.nAddr[i].type == CM_TPTADDR_IPV4)
		{
			// U32 ipv4NetAddr
			lLdata = htonl(m_addEp.nAddr[i].u.ipv4NetAddr);
			memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
			offset += SIZE_OF_LONG;
		}
		else { // IPV6
			memcpy(pkt + offset, (void*)&m_addEp.nAddr[i].u.ipv6NetAddr, 
										sizeof(CmIpAddr6));
			offset += sizeof(CmIpAddr6);
		}
	}

	// U16 sctpProcId
	lSdata = htons(m_addEp.sctpProcId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// S16 sctpLsapId
	lSdata = htons(m_addEp.sctpLsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// S16 sctpUsapId
	lSdata = htons(m_addEp.sctpUsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// S16 m3uaLsapId
	lSdata = htons(m_addEp.m3uaLsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// S16 tuclUsapId
	lSdata = htons(m_addEp.tuclUsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// U8 currentEpState
	memcpy(pkt + offset, (void*)&m_addEp.currentEpState, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddEp::packetize, offset:%d actualLength:%d", offset, size); 

	return (offset);
}

void
INGwFtPktAddEp::getEpData(AddEndPoint &addEp)
{
	memcpy(&addEp, &m_addEp, sizeof(AddEndPoint));
}

void
INGwFtPktAddEp::setEpData(AddEndPoint &addEp)
{
	m_isAddEpSet = true;
	memcpy(&m_addEp, &addEp, sizeof(AddEndPoint));
}

std::string INGwFtPktAddEp::toLog(void) const
{
   char buf[4000];
   int len =0;

	 memset(buf, 0, sizeof(buf));

   len += sprintf((buf + len), "%s", INGwFtPktMsg::toLog().c_str());
   len += sprintf((buf + len), 
					"AddEp: endPointid:%d, srcPort:%d, nmbAddrs:%d, sctpProcId:%d"
					", sctpLsapId:%d, sctpUsapId:%d, m3uaLsapId:%d, tuclUsapId:%d"
					", currentEpState:%d", m_addEp.endPointid, m_addEp.srcPort, 
					m_addEp.nmbAddrs, m_addEp.sctpProcId, m_addEp.sctpLsapId, 
					m_addEp.sctpUsapId, m_addEp.m3uaLsapId, m_addEp.tuclUsapId, 
					m_addEp.currentEpState);

		for(int i=0; i < m_addEp.nmbAddrs; ++i)
		{
			if(m_addEp.nAddr[i].type == CM_TPTADDR_IPV4)
			{
				len += sprintf((buf + len), " Addr[%d]:type:%d,%d",i,
								m_addEp.nAddr[i].type, m_addEp.nAddr[i].u.ipv4NetAddr);
			}
			else {
				len += sprintf((buf + len), " Addr[%d]:type:%d,",i,
								m_addEp.nAddr[i].type);

				for(int l=0; l < sizeof(CmIpAddr6); ++l)
				{
					len += sprintf((buf + len), "%d-", 
						m_addEp.nAddr[i].u.ipv6NetAddr[l]);
				}
			}
		}

   return buf;
}


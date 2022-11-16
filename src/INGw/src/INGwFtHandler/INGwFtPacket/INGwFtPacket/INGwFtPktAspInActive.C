#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktAspInActive.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktAspInActive::INGwFtPktAspInActive()
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktAspInActive::INGwFtPktAspInActive()");
   mMsgData.msMsgType   = MSG_ASP_INACTV;
	 m_isAspInActvSet       	= false;
	 memset(&m_aspInActv, 0, sizeof(M3uaAspInAct));
}

void
INGwFtPktAspInActive::initialize(short senderid, short receiverid, M3uaAspInAct& psp)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktAspInActive::initialize()");
  mMsgData.msSender    = senderid;
  mMsgData.msReceiver  = receiverid;
	m_isAspInActvSet = true;
	memcpy(&m_aspInActv, &psp, sizeof(M3uaAspInAct));
}

INGwFtPktAspInActive::~INGwFtPktAspInActive()
{
}

int 
INGwFtPktAspInActive::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktAspInActive::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAspInActive::depacketize buffSize:%d", asSize);

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U16 lSdata =0;
	U32 lLdata =0;

	// U32 psId
	memcpy((void*)&lLdata, apcData + offset, SIZE_OF_LONG);
	m_aspInActv.psId = htonl(lLdata); 
	offset += SIZE_OF_LONG;

	// U32 pspId
  lLdata =0;
	memcpy((void*)&lLdata, apcData + offset, SIZE_OF_LONG);
	m_aspInActv.pspId = htonl(lLdata); 
	offset += SIZE_OF_LONG;

	// U16 endPointId
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_aspInActv.endPointId = htons(lSdata); 
	offset += SIZE_OF_SHORT;

	// S16 m3uaLsapId
  lSdata =0;
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_aspInActv.m3uaLsapId = htons(lSdata); 
	offset += SIZE_OF_SHORT;

	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktAspInActive::depacketize "
  "psId:%d, pspId:%d, m3uaLsapId:%d, endpointId:%d",m_aspInActv.psId,
   m_aspInActv.pspId ,m_aspInActv.m3uaLsapId, m_aspInActv.endPointId);
	return (offset);
}

int INGwFtPktAspInActive::packetize(char** apcData, int version)
{
	// calculate size of buffer to be created
	int size = sizeof(M3uaAspInAct) /*- (no of ps)*/;

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAspInActive::packetize sizeAllocated:%d, pspId:%d, m3uaLsapId:%d"
  "endPointId:%d, psId:%d",
    size,m_aspInActv.pspId,m_aspInActv.m3uaLsapId,m_aspInActv.endPointId
   ,m_aspInActv.psId);

	int offset = INGwFtPktMsg::createPacket(size, apcData, version);

	char *pkt = *apcData;

	if(!m_isAspInActvSet)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwFtPktAspInActive::packetize AddAspInActv has not been set");
	}

	U16 lSdata =0;
	U32 lLdata =0;

	// U32 psId
	lLdata = htonl(m_aspInActv.psId);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
	offset += SIZE_OF_LONG;

	// U32 pspId
  lLdata = 0;
	lLdata = htonl(m_aspInActv.pspId);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
	offset += SIZE_OF_LONG;

	// U16 endPointId
	lSdata = htons(m_aspInActv.endPointId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// S16 m3uaLsapId
  lSdata =0;
	lSdata = htons(m_aspInActv.m3uaLsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAspInActive::packetize, offset:%d actualLength:%d", offset, size); 

	return (offset);
}

void
INGwFtPktAspInActive::getPspData(M3uaAspInAct &aspAct)
{
	memcpy(&aspAct, &m_aspInActv, sizeof(M3uaAspInAct));
	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAspInActive::getPspData() psId:%d, pspId:%d, m3uaLsapId:%d", 
  aspAct.psId, aspAct.pspId, aspAct.m3uaLsapId);
}

void
INGwFtPktAspInActive::setPspData(M3uaAspInAct &aspAct)
{
	m_isAspInActvSet = true;
	memcpy(&m_aspInActv, &aspAct, sizeof(M3uaAspInAct));
}
#if 0
std::string INGwFtPktAspActive::toLog(void) const
{
   char buf[4000];
   int len =0;

	 memset(buf, 0, sizeof(buf));

   len += sprintf((buf + len), "%s", INGwFtPktMsg::toLog().c_str());
   len += sprintf((buf + len), 
					"AspActv: psId:%d, pspId:%d,endPointId:%d, m3uaLSapId:%d"
					m_aspActv.psId, m_aspActv.pspId,  
					m_aspActv.endPointId, m_aspActv.m3uaLsapId);

		for(int i=0; i < m_aspActv.nmbAddr; ++i)
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
#endif


#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktAspActive.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktAspActive::INGwFtPktAspActive()
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktAspActive::INGwFtPktAspActive()");
   mMsgData.msMsgType   = MSG_ASP_ACTV;
	 m_isAspActvSet       	= false;
	 memset(&m_aspActv, 0, sizeof(M3uaAspAct));
}

void
INGwFtPktAspActive::initialize(short senderid, short receiverid, M3uaAspAct& psp)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktAspActive::initialize()");
  mMsgData.msSender    = senderid;
  mMsgData.msReceiver  = receiverid;
	m_isAspActvSet = true;
	memcpy(&m_aspActv, &psp, sizeof(M3uaAspAct));
}

INGwFtPktAspActive::~INGwFtPktAspActive()
{
}

int 
INGwFtPktAspActive::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktAspActive::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAspActive::depacketize buffSize:%d", asSize);

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U16 lSdata =0;
	U32 lLdata =0;

	// U32 psId
	memcpy((void*)&lLdata, apcData + offset, SIZE_OF_LONG);
	m_aspActv.psId = htonl(lLdata); 
	offset += SIZE_OF_LONG;

	// U32 pspId
  lLdata =0;
	memcpy((void*)&lLdata, apcData + offset, SIZE_OF_LONG);
	m_aspActv.pspId = htonl(lLdata); 
	offset += SIZE_OF_LONG;

	// U16 endPointId
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_aspActv.endPointId = htons(lSdata); 
	offset += SIZE_OF_SHORT;

	// S16 m3uaLsapId
  lSdata =0;
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_aspActv.m3uaLsapId = htons(lSdata); 
	offset += SIZE_OF_SHORT;

	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktAspActive::depacketize "
  "psId:%d, pspId:%d, m3uaLsapId:%d, endpointId:%d",m_aspActv.psId,
   m_aspActv.pspId ,m_aspActv.m3uaLsapId, m_aspActv.endPointId);
	return (offset);
}

int INGwFtPktAspActive::packetize(char** apcData, int version)
{
	// calculate size of buffer to be created
	int size = sizeof(M3uaAspAct);

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddPsp::packetize sizeAllocated:%d, pspId:%d, m3uaLsapId:%d"
  "endPointId:%d, psId:%d",
    size,m_aspActv.pspId,m_aspActv.m3uaLsapId,m_aspActv.endPointId
   ,m_aspActv.psId);

	int offset = INGwFtPktMsg::createPacket(size, apcData, version);

	char *pkt = *apcData;

	if(!m_isAspActvSet)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwFtPktAspActive::packetize AddAspActv has not been set");
	}

	U16 lSdata =0;
	U32 lLdata =0;

	// U32 psId
	lLdata = htonl(m_aspActv.psId);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
	offset += SIZE_OF_LONG;

	// U32 pspId
  lLdata = 0;
	lLdata = htonl(m_aspActv.pspId);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
	offset += SIZE_OF_LONG;

	// U16 endPointId
	lSdata = htons(m_aspActv.endPointId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// S16 m3uaLsapId
  lSdata =0;
	lSdata = htons(m_aspActv.m3uaLsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAspActive::packetize, offset:%d actualLength:%d", offset, size); 

	return (offset);
}

void
INGwFtPktAspActive::getPspData(M3uaAspAct &aspAct)
{
	memcpy(&aspAct, &m_aspActv, sizeof(M3uaAspAct));
	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAspActive::getPspData() psId:%d, pspId:%d, m3uaLsapId:%d", 
  aspAct.psId, aspAct.pspId, aspAct.m3uaLsapId);
}

void
INGwFtPktAspActive::setPspData(M3uaAspAct &aspAct)
{
	m_isAspActvSet = true;
	memcpy(&m_aspActv, &aspAct, sizeof(M3uaAspAct));
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


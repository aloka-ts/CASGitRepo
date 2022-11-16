#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktDelRoute.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktDelRoute::INGwFtPktDelRoute()
{
   mMsgData.msMsgType   = MSG_DEL_ROUTE;
	 m_isDelRouteSet       = false;
	 memset(&m_delRoute, 0, sizeof(DelRoute));
}

void
INGwFtPktDelRoute::initialize(short senderid, short receiverid, 
															DelRoute& delRoute)
{
  mMsgData.msSender    = senderid;
  mMsgData.msReceiver  = receiverid;
	m_isDelRouteSet = true;
	memcpy(&m_delRoute, &delRoute, sizeof(DelRoute));
}

INGwFtPktDelRoute::~INGwFtPktDelRoute()
{
}

int 
INGwFtPktDelRoute::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktDelRoute::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelRoute::depacketize sizeofBuf:%d", asSize);

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U16 lSdata =0;
	U32 lLdata =0;

	// U32 dpc
	memcpy(&lLdata, apcData + offset, SIZE_OF_LONG);
	m_delRoute.dpc = ntohs(lLdata);
	offset += SIZE_OF_LONG;

	// U8 upSwtch
	memcpy((void*)&m_delRoute.upSwtch, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// S16 nSapId
	memcpy((void*)&lSdata, apcData + offset,  SIZE_OF_SHORT);
	m_delRoute.nSapId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktDelRoute::depacketize");
	return (offset);
}

int INGwFtPktDelRoute::packetize(char** apcData, int version)
{
	int size = sizeof(DelRoute);

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelRoute::packetize sizeAllocated:%d", size);

	int offset = INGwFtPktMsg::createPacket(size, apcData, version);

	char *pkt = *apcData;

	if(!m_isDelRouteSet)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwFtPktDelRoute::packetize DelRoute has not been set");
	}

	U16 lSdata =0;
	U32 lLdata =0;

	// U32 dpc
	lLdata = htonl(m_delRoute.dpc);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
	offset += SIZE_OF_LONG;

	// U8 upSwtch
	memcpy(pkt + offset, (void*)&m_delRoute.upSwtch, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// S16 nSapId
	lSdata = htons(m_delRoute.nSapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelRoute::packetize, offset:%d actualLength:%d", 
				offset, (sizeof(m_delRoute) + SIZE_OF_BPMSG));

	return (offset);
}

void
INGwFtPktDelRoute::getRouteData(DelRoute &delRoute)
{
	memcpy(&delRoute, &m_delRoute, sizeof(DelRoute));
}

void
INGwFtPktDelRoute::setRouteData(DelRoute &delRoute)
{
	m_isDelRouteSet = true;
	memcpy(&m_delRoute, &delRoute, sizeof(DelRoute));
}

std::string INGwFtPktDelRoute::toLog(void) const
{
   char buf[1000];
   int len =0;

	 memset(buf, 0, sizeof(buf));

   len += sprintf((buf + len), "%s", INGwFtPktMsg::toLog().c_str());
   len += sprintf((buf + len), 
			"Del Route Request: dpc:%d, upSwtch:%d, nSapId:%d,",
			m_delRoute.dpc, m_delRoute.upSwtch, m_delRoute.nSapId);

   return buf;
}


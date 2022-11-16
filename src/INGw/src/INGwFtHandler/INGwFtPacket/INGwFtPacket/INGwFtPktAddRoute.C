#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktAddRoute.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktAddRoute::INGwFtPktAddRoute()
{
   mMsgData.msMsgType   = MSG_ADD_ROUTE;
	 m_isAddRouteSet       = false;
	 memset(&m_addRoute, 0, sizeof(AddRoute));
}

void
INGwFtPktAddRoute::initialize(short senderid, short receiverid, 
															AddRoute& addRoute)
{
  mMsgData.msSender    = senderid;
  mMsgData.msReceiver  = receiverid;
	m_isAddRouteSet = true;
	memcpy(&m_addRoute, &addRoute, sizeof(AddRoute));
}

INGwFtPktAddRoute::~INGwFtPktAddRoute()
{
}

int 
INGwFtPktAddRoute::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktAddRoute::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddRoute::depacketize buffSize:%d, expected:%d", asSize,
		(sizeof(m_addRoute)+SIZE_OF_BPMSG));

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U16 lSdata =0;
	U32 lLdata =0;

	// U32 dpc
	memcpy(&lLdata, apcData + offset, SIZE_OF_LONG);
	m_addRoute.dpc = ntohs(lLdata);
	offset += SIZE_OF_LONG;

	// U8 swtchType
	memcpy((void*)&m_addRoute.swtchType, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 spType
	memcpy((void*)&m_addRoute.spType, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 upSwtch
	memcpy((void*)&m_addRoute.upSwtch, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U16 cmbLnkSetId
	memcpy(&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addRoute.cmbLnkSetId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// U8 dir
	memcpy((void*)&m_addRoute.dir, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U32 rteToAdjSp
	memcpy((void*)&lLdata, apcData + offset, SIZE_OF_LONG);
	m_addRoute.rteToAdjSp = ntohl(lLdata);
	offset += SIZE_OF_LONG;

	// U8 ssf
	memcpy((void*)&m_addRoute.ssf, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// S16 swtch
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addRoute.swtch = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// U8 status
	memcpy((void*)&m_addRoute.status, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U16 nmbBpc
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addRoute.nmbBpc = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// bpcList
	for(int i=0; i < m_addRoute.nmbBpc; ++i)
	{
		// U32 bpc;
		memcpy((void*)&lLdata, apcData + offset, SIZE_OF_LONG);
		m_addRoute.bpcList[i].bpc = ntohl(lLdata);
 		offset += SIZE_OF_LONG;

		// U8 prior
		memcpy((void*)&m_addRoute.bpcList[i].prior,apcData + offset, SIZE_OF_CHAR);
		offset += SIZE_OF_CHAR;
	}

	// U8 nmbSsns
	memcpy((void*)&m_addRoute.nmbSsns, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// ssnList
	for(int i=0; i < m_addRoute.nmbSsns; ++i)
	{
		//U8 ssn
		memcpy((void*)&m_addRoute.ssnList[i].ssn, apcData + offset, SIZE_OF_CHAR);
		offset += SIZE_OF_CHAR;

		//U8 status
		memcpy((void*)&m_addRoute.ssnList[i].status,apcData + offset, SIZE_OF_CHAR);
		offset += SIZE_OF_CHAR;

#if (SS7_ANS96 || SS7_BELL05)
   	// U8 replicatedMode
		memcpy((void*)&m_addRoute.ssnList[i].replicatedMode, apcData + offset, 
																										SIZE_OF_CHAR);
		offset += SIZE_OF_CHAR;
#endif /* (SS7_ANS96 || SS7_BELL05) */

   	// U16 nmbBpc
		memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
		m_addRoute.ssnList[i].nmbBpc = ntohs(lSdata);
		offset += SIZE_OF_SHORT;

  	// SpBpcCfg bpcList[MAXNUMBPC]
		for(int j=0; j < m_addRoute.ssnList[i].nmbBpc; ++j)
		{
			// Dpc bpc
			memcpy((void*)&lLdata, apcData + offset, SIZE_OF_LONG);
			m_addRoute.ssnList[i].bpcList[j].bpc = ntohl(lLdata);
			offset += SIZE_OF_LONG;

			// U8  prior
			memcpy((void*)&m_addRoute.ssnList[i].bpcList[j].prior, apcData + offset,
											SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;
		}

   	// U16 nmbConPc
		memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
		m_addRoute.ssnList[i].nmbConPc = ntohs(lSdata);
		offset += SIZE_OF_SHORT;

   	// Dpc conPc[MAXCONPC]
		for(int k=0; k < m_addRoute.ssnList[i].nmbConPc; ++k)
		{
			memcpy((void*)&lLdata, apcData + offset, SIZE_OF_LONG);
			m_addRoute.ssnList[i].conPc[k] = ntohl(lLdata);
			offset += SIZE_OF_LONG;
		}
	}

#if (SS7_ANS96 || SS7_BELL05)
	// U8 replicatedMode
	memcpy((void*)&m_addRoute.replicatedMode, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;
#endif /* (SS7_ANS96 || SS7_BELL05) */

	// U32 preferredOpc
	memcpy((void*)&lLdata, apcData + offset, SIZE_OF_LONG);
	m_addRoute.preferredOpc = ntohl(lLdata);
	offset += SIZE_OF_LONG;

	// S16 nSapId
	memcpy((void*)&lSdata, apcData + offset,  SIZE_OF_SHORT);
	m_addRoute.nSapId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// U8 currentDpcState
	memcpy((void*)&m_addRoute.currentDpcState, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

#if LSPV3_1
	// S16 defaultRoutenSapId
	memcpy((void*)&lSdata, apcData + offset,  SIZE_OF_SHORT);
	m_addRoute.defaultRoutenSapId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// Bool secRteCfg
	memcpy((void*)&m_addRoute.secRteCfg, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// S16 nSapId2
	memcpy((void*)&lSdata, apcData + offset,  SIZE_OF_SHORT);
	m_addRoute.nSapId2 = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// Bool nSap1RteStatus
	memcpy((void*)&m_addRoute.nSap1RteStatus, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// Bool nSap2RteStatus
	memcpy((void*)&m_addRoute.nSap2RteStatus, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;
#endif

	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktAddRoute::depacketize");
	return (offset);
}

int INGwFtPktAddRoute::packetize(char** apcData, int version)
{
	// calculate size of buffer to be created
	// depends on nmbBpc and nmbSsns
	int size = sizeof(m_addRoute) - 
									 ((MAXNUMBPC - m_addRoute.nmbBpc)*sizeof(Sp_Bpc_Cfg)) -
									 ((MAXNUMSSN - m_addRoute.nmbSsns)*sizeof(SPSsnCfg));

	int size2 =0;
	if(m_addRoute.nmbSsns != 0)
	{
		for(int i=0; i < m_addRoute.nmbSsns; ++i)
		{
			if(m_addRoute.ssnList[i].nmbBpc != 0)
			{
				size2 += (MAXNUMBPC-m_addRoute.ssnList[i].nmbBpc)*sizeof(SpBpcCfg);
			}
			else 
			{
				size2 += sizeof(SpBpcCfg)*MAXNUMBPC;
			}

			if(m_addRoute.ssnList[i].nmbConPc !=0)
			{
				size2 += (MAXCONPC-m_addRoute.ssnList[i].nmbConPc)*sizeof(Dpc);
			}
			else
			{
				size2 += sizeof(Dpc)*MAXCONPC;
			}
		}
	}

	size = size - size2;

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddRoute::packetize sizeAllocated:%d, nmbBpc:%d, nmbSsns:%d "
	"size2:%d", size, m_addRoute.nmbBpc, m_addRoute.nmbSsns, size2);

	int offset = INGwFtPktMsg::createPacket(size, apcData, version);

	char *pkt = *apcData;

	if(!m_isAddRouteSet)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwFtPktAddRoute::packetize AddRoute has not been set");
	}

	U16 lSdata =0;
	U32 lLdata =0;

	// U32 dpc
	lLdata = htonl(m_addRoute.dpc);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
	offset += SIZE_OF_LONG;

	// U8 swtchType
	memcpy(pkt + offset, (void*)&m_addRoute.swtchType, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 spType
	memcpy(pkt + offset, (void*)&m_addRoute.spType, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 upSwtch
	memcpy(pkt + offset, (void*)&m_addRoute.upSwtch, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U16 cmbLnkSetId
	lSdata = htons(m_addRoute.cmbLnkSetId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// U8 dir
	memcpy(pkt + offset, (void*)&m_addRoute.dir, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U32 rteToAdjSp
	lLdata = htonl(m_addRoute.rteToAdjSp);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
	offset += SIZE_OF_LONG;

	// U8 ssf
	memcpy(pkt + offset, (void*)&m_addRoute.ssf, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// S16 swtch
	lSdata = htons(m_addRoute.swtch);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// U8 status
	memcpy(pkt + offset, (void*)&m_addRoute.status, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U16 nmbBpc
	lSdata = htons(m_addRoute.nmbBpc);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// bpcList
	for(int i=0; i < m_addRoute.nmbBpc; ++i)
	{
		// U32 bpc;
		lLdata = htonl(m_addRoute.bpcList[i].bpc);
		memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
 		offset += SIZE_OF_LONG;

		// U8 prior
		memcpy(pkt + offset, (void*)&m_addRoute.bpcList[i].prior, SIZE_OF_CHAR);
		offset += SIZE_OF_CHAR;
	}

	// U8 nmbSsns
	memcpy(pkt + offset, (void*)&m_addRoute.nmbSsns, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// ssnList
	for(int i=0; i < m_addRoute.nmbSsns; ++i)
	{
		//U8 ssn
		memcpy(pkt + offset, (void*)&m_addRoute.ssnList[i].ssn, SIZE_OF_CHAR);
		offset += SIZE_OF_CHAR;

		//U8 status
		memcpy(pkt + offset, (void*)&m_addRoute.ssnList[i].status, SIZE_OF_CHAR);
		offset += SIZE_OF_CHAR;

#if (SS7_ANS96 || SS7_BELL05)
   	// U8 replicatedMode
		memcpy(pkt + offset, (void*)&m_addRoute.ssnList[i].replicatedMode, 
											SIZE_OF_CHAR);
		offset += SIZE_OF_CHAR;
#endif /* (SS7_ANS96 || SS7_BELL05) */

   	// U16 nmbBpc
		lSdata = htons(m_addRoute.ssnList[i].nmbBpc);
		memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
		offset += SIZE_OF_SHORT;

  	// SpBpcCfg bpcList[MAXNUMBPC]
		for(int j=0; j < m_addRoute.ssnList[i].nmbBpc; ++j)
		{
			// Dpc bpc
			lLdata = htonl(m_addRoute.ssnList[i].bpcList[j].bpc);
			memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
			offset += SIZE_OF_LONG;

			// U8  prior
			memcpy(pkt + offset, (void*)&m_addRoute.ssnList[i].bpcList[j].prior, 
											SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;
		}

   	// U16 nmbConPc
		lSdata = htons(m_addRoute.ssnList[i].nmbConPc);
		memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
		offset += SIZE_OF_SHORT;

   	// Dpc conPc[MAXCONPC]
		for(int k=0; k < m_addRoute.ssnList[i].nmbConPc; ++k)
		{
			lLdata = htonl(m_addRoute.ssnList[i].conPc[k]);
			memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
			offset += SIZE_OF_LONG;
		}
	}

#if (SS7_ANS96 || SS7_BELL05)
	// U8 replicatedMode
	memcpy(pkt + offset, (void*)&m_addRoute.replicatedMode, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;
#endif /* (SS7_ANS96 || SS7_BELL05) */

	// U32 preferredOpc
	lLdata = htonl(m_addRoute.preferredOpc);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
	offset += SIZE_OF_LONG;

	// S16 nSapId
	lSdata = htons(m_addRoute.nSapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// U8 currentDpcState
	memcpy(pkt + offset, (void*)&m_addRoute.currentDpcState, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

#if LSPV3_1
	// S16 defaultRoutenSapId
	lSdata = htons(m_addRoute.defaultRoutenSapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// Bool secRteCfg
	memcpy(pkt + offset, (void*)&m_addRoute.secRteCfg, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// S16 nSapId2
	lSdata = htons(m_addRoute.nSapId2);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// Bool nSap1RteStatus
	memcpy(pkt + offset, (void*)&m_addRoute.nSap1RteStatus, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// Bool nSap2RteStatus
	memcpy(pkt + offset, (void*)&m_addRoute.nSap2RteStatus, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

#endif

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddRoute::packetize, offset:%d actualLength:%d", 
				offset, (sizeof(m_addRoute) + SIZE_OF_BPMSG));

	return (offset);
}

void
INGwFtPktAddRoute::getRouteData(AddRoute &addRoute)
{
	memcpy(&addRoute, &m_addRoute, sizeof(AddRoute));
}

void
INGwFtPktAddRoute::setRouteData(AddRoute &addRoute)
{
	m_isAddRouteSet = true;
	memcpy(&m_addRoute, &addRoute, sizeof(AddRoute));
}

std::string INGwFtPktAddRoute::toLog(void) const
{
   char buf[4000];
   int len =0;

	 memset(buf, 0, sizeof(buf));

   len += sprintf((buf + len), "%s", INGwFtPktMsg::toLog().c_str());
   len += sprintf((buf + len), 
			"Add Route Request: dpc:%d, swtchType:%d, spType:%d, upSwtch:%d, "
			"	cmbLnkSetId:%d, dir:%d, rteToAdjSp:%d, ssf:%d, swtch:%d, status:%d, "
			" nmbBpc:%d, nmbSsns:%d, preferredOpc:%d, nSapId:%d,"
			" currentDpcState:%d" , m_addRoute.dpc, m_addRoute.swtchType, 
			m_addRoute.spType, m_addRoute.upSwtch, m_addRoute.cmbLnkSetId, 
			m_addRoute.dir, m_addRoute.rteToAdjSp, m_addRoute.ssf, m_addRoute.swtch,
			m_addRoute.status, m_addRoute.nmbBpc, m_addRoute.nmbSsns,
			m_addRoute.preferredOpc, m_addRoute.nSapId,
			m_addRoute.currentDpcState);
#if (SS7_ANS96 || SS7_BELL05)
   len += sprintf((buf + len), " replicateMode:%d", m_addRoute.replicatedMode);
#endif /* (SS7_ANS96 || SS7_BELL05) */

		for(int i=0; i < m_addRoute.nmbBpc; ++i) 
		{
			len += sprintf(buf+len, " bpcList[%d]:{bpc:%d, prior:%d}, ",
								i, m_addRoute.bpcList[i].bpc, m_addRoute.bpcList[i].prior);

		}

		for(int j=0; j < m_addRoute.nmbSsns; ++j) 
		{
			len += sprintf(buf+len, " nmbSsns[%d]:<ssn:%d, nmbBpc:%d, nmbConPc:%d",
				j, m_addRoute.ssnList[j].ssn, m_addRoute.ssnList[j].nmbBpc,
								m_addRoute.ssnList[j].nmbConPc);

			for(int k=0; k < m_addRoute.ssnList[j].nmbBpc;++k)
			{
				len += sprintf(buf+len, " bpcList[%d]:<bpc:%d, Prior:%d>, ",
								k, m_addRoute.ssnList[j].bpcList[k].bpc,
								m_addRoute.ssnList[j].bpcList[k].prior);
			}

			for(int l=0; l < m_addRoute.ssnList[j].nmbConPc;++l) 
			{
				len += sprintf(buf+len, "  conPc[%d]:%d, ",
								l, m_addRoute.ssnList[j].conPc[l]);
			}
		}

#if LSPV3_1
	// S16 defaultRoutenSapId
	len += sprintf(buf+len, "secRteCfg:%d \n", m_addRoute.secRteCfg);

	len += sprintf(buf+len, "defaultRoutenSapId:%d \n", 
																		m_addRoute.defaultRoutenSapId);

	len += sprintf(buf+len, "nSap1RteStatus:%d \n", 
																		m_addRoute.nSap1RteStatus);

	len += sprintf(buf+len, "nSapId2:%d \nnSap2RteStatus: %d", 
										m_addRoute.nSapId2, m_addRoute.nSap2RteStatus);
#endif
   return buf;
}


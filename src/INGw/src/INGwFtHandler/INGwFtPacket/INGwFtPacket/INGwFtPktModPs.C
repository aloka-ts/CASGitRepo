#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktModPs.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktModPs::INGwFtPktModPs()
{
   mMsgData.msMsgType   = MSG_MOD_PS;
	 m_isAddPsSet       	= false;
	 memset(&m_addPs, 0, sizeof(AddPs));
}

void
INGwFtPktModPs::initialize(short senderid, short receiverid, AddPs& addPs)
{
  mMsgData.msSender    = senderid;
  mMsgData.msReceiver  = receiverid;
	m_isAddPsSet = true;
	memcpy(&m_addPs, &addPs, sizeof(AddPs));
}

INGwFtPktModPs::~INGwFtPktModPs()
{
}

int 
INGwFtPktModPs::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktModPs::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktModPs::depacketize buffSize:%d", asSize);

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U16 lSdata =0;
	U32 lLdata =0;

  // U32   psId
	memcpy((void*)&lLdata, apcData+offset, SIZE_OF_LONG);
	m_addPs.psId = ntohl(lLdata);
	offset += SIZE_OF_LONG;

  // U32   routCtx
	memcpy((void*)&lLdata, apcData+offset, SIZE_OF_LONG);
	m_addPs.routCtx = ntohl(lLdata);
	offset += SIZE_OF_LONG;

	//   U8    nwkId
	memcpy((void*)&m_addPs.nwkId, apcData+offset,  SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	//   U8    mode
	memcpy((void*)&m_addPs.mode, apcData+offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	//   U8    loadShareMode
	memcpy((void*)&m_addPs.loadShareMode,apcData+offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	//   U16   nmbActPspReqd
	memcpy((void*)&lSdata,apcData+offset, SIZE_OF_SHORT);
	m_addPs.nmbActPspReqd = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	//   U16   nmbPsp
	memcpy((void*)&lSdata,apcData+offset, SIZE_OF_SHORT);
	m_addPs.nmbPsp = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	//   U16   psp[LIT_MAX_PSP]
	for(int i=0; i < m_addPs.nmbPsp; ++i)
	{
		memcpy((void*)&lSdata,apcData+offset, SIZE_OF_SHORT);
		m_addPs.psp[i] = ntohs(lSdata);
		offset += SIZE_OF_SHORT;
	}
	//   Bool  lFlag
	memcpy((void*)&m_addPs.lFlag, apcData+offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	//   ITPspEp  pspEpLst[LIT_MAX_PSP]
	for(int i=0; i < m_addPs.nmbPsp; ++i) 
	{
		// U32       nmbEp
		memcpy((void*)&lLdata,apcData+offset, SIZE_OF_LONG);
		m_addPs.pspEpLst[i].nmbEp = ntohl(lLdata);
		offset += SIZE_OF_LONG;

		for(int j=0; j < m_addPs.pspEpLst[i].nmbEp; ++j)
		{
			memcpy((void*)&lLdata,apcData+offset, SIZE_OF_LONG);
			m_addPs.pspEpLst[i].endpIds[j] = ntohl(lLdata);
			offset += SIZE_OF_LONG;
		}
	}

	//   U8    rtType
	memcpy((void*)&m_addPs.rtType,apcData+offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	//   U32   dpcMask
	memcpy((void*)&lLdata,apcData+offset, SIZE_OF_LONG);
	m_addPs.dpcMask = ntohl(lLdata);
	offset += SIZE_OF_LONG;

	//   U32   dpc
	memcpy((void*)&lLdata,apcData+offset, SIZE_OF_LONG);
	m_addPs.dpc = ntohl(lLdata);
	offset += SIZE_OF_LONG;

	//   U32   opcMask
	memcpy((void*)&lLdata,apcData+offset, SIZE_OF_LONG);
	m_addPs.opcMask = ntohl(lLdata);
	offset += SIZE_OF_LONG;

	//   U32   opc
	memcpy((void*)&lLdata,apcData+offset, SIZE_OF_LONG);
	m_addPs.opc = ntohl(lLdata);
	offset += SIZE_OF_LONG;

	//   U8    slsMask
	memcpy((void*)&m_addPs.slsMask,apcData+offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	//   U8    sls
	memcpy((void*)&m_addPs.sls,apcData+offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	//   U8    sioMask
	memcpy((void*)&m_addPs.sioMask,apcData+offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	//   U8    sio
	memcpy((void*)&m_addPs.sio,apcData+offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	//   U8    currentPsState
	memcpy((void*)&m_addPs.currentPsState,apcData+offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktModPs::depacketize");
	return (offset);
}

int INGwFtPktModPs::packetize(char** apcData, int version)
{
	// calculate size of buffer to be created
	int size = sizeof(AddPs) -
						(LIT_MAX_PSP-m_addPs.nmbPsp)*sizeof(U16) -
						(LIT_MAX_PSP-m_addPs.nmbPsp)*sizeof(ITPspEp);

	int size2 =0;
	if(m_addPs.nmbPsp != 0)
	{
		for(int i=0; i < m_addPs.nmbPsp; ++i)
		{
			size2 += (LIT_MAX_SEP-m_addPs.pspEpLst[i].nmbEp)*sizeof(U32);
		}
	}

	// Actual Size
	size -= size2;

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktModPs::packetize sizeAllocated:%d, size2:%d", size, size2);

	int offset = INGwFtPktMsg::createPacket(size, apcData, version);

	char *pkt = *apcData;

	if(!m_isAddPsSet)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwFtPktModPs::packetize ModPs has not been set");
	}

	U16 lSdata =0;
	U32 lLdata =0;

  // U32   psId
	lLdata = htonl(m_addPs.psId);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
	offset += SIZE_OF_LONG;

  // U32   routCtx
	lLdata = htonl(m_addPs.routCtx);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
	offset += SIZE_OF_LONG;

	//   U8    nwkId
	memcpy(pkt + offset, (void*)&m_addPs.nwkId, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	//   U8    mode
	memcpy(pkt + offset, (void*)&m_addPs.mode, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	//   U8    loadShareMode
	memcpy(pkt + offset, (void*)&m_addPs.loadShareMode, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	//   U16   nmbActPspReqd
	lSdata = htons(m_addPs.nmbActPspReqd);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	//   U16   nmbPsp
	lSdata = htons(m_addPs.nmbPsp);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	//   U16   psp[LIT_MAX_PSP]
	for(int i=0; i < m_addPs.nmbPsp; ++i)
	{
		lSdata = htons(m_addPs.psp[i]);
		memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
		offset += SIZE_OF_SHORT;
	}
	//   Bool  lFlag
	memcpy(pkt + offset, (void*)&m_addPs.lFlag, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	//   ITPspEp  pspEpLst[LIT_MAX_PSP]
	for(int i=0; i < m_addPs.nmbPsp; ++i) 
	{
		// U32       nmbEp
		lLdata = htonl(m_addPs.pspEpLst[i].nmbEp);
		memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
		offset += SIZE_OF_LONG;

		for(int j=0; j < m_addPs.pspEpLst[i].nmbEp; ++j)
		{
			lLdata = htonl(m_addPs.pspEpLst[i].endpIds[j]);
			memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
			offset += SIZE_OF_LONG;
		}
	}

	//   U8    rtType
	memcpy(pkt + offset, (void*)&m_addPs.rtType, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	//   U32   dpcMask
	lLdata = htonl(m_addPs.dpcMask);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
	offset += SIZE_OF_LONG;

	//   U32   dpc
	lLdata = htonl(m_addPs.dpc);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
	offset += SIZE_OF_LONG;

	//   U32   opcMask
	lLdata = htonl(m_addPs.opcMask);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
	offset += SIZE_OF_LONG;

	//   U32   opc
	lLdata = htonl(m_addPs.opc);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
	offset += SIZE_OF_LONG;

	//   U8    slsMask
	memcpy(pkt + offset, (void*)&m_addPs.slsMask, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	//   U8    sls
	memcpy(pkt + offset, (void*)&m_addPs.sls, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	//   U8    sioMask
	memcpy(pkt + offset, (void*)&m_addPs.sioMask, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	//   U8    sio
	memcpy(pkt + offset, (void*)&m_addPs.sio, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	//   U8    currentPsState
	memcpy(pkt + offset, (void*)&m_addPs.currentPsState, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktModPs::packetize, offset:%d actualLength:%d", offset, size); 

	return (offset);
}

void
INGwFtPktModPs::getPsData(AddPs &addPs)
{
	memcpy(&addPs, &m_addPs, sizeof(AddPs));
}

void
INGwFtPktModPs::setPsData(AddPs &addPs)
{
	m_isAddPsSet = true;
	memcpy(&m_addPs, &addPs, sizeof(AddPs));
}

std::string INGwFtPktModPs::toLog(void) const
{
   char buf[4000];
   int len =0;

	 memset(buf, 0, sizeof(buf));

   len += sprintf((buf + len), "%s", INGwFtPktMsg::toLog().c_str());
   len += sprintf((buf + len), 
					"Modify Ps: psId:%d, routCtx:%d, nwkId:%d, mode:%d, loadShareMode:%d"
					", nmbActPspReqd:%d, nmbPsp:%d, lFlag:%d, rtType:%d, dpcMask:%d"
					", dpc:%d, opcMask:%d, opc:%d, slsMask:%d, sls:%d, sioMask:%d, "
					"sio:%d, currentPsState:%d",
					m_addPs.psId, m_addPs.routCtx, m_addPs.nwkId, m_addPs.mode,
					m_addPs.loadShareMode, m_addPs.nmbActPspReqd, m_addPs.nmbPsp, 
					m_addPs.lFlag, m_addPs.rtType, m_addPs.dpcMask, m_addPs.dpc,
					m_addPs.opcMask, m_addPs.opc, m_addPs.slsMask, m_addPs.sls,
					m_addPs.sioMask, m_addPs.sio, m_addPs.currentPsState);


					for(int i=0; i < m_addPs.nmbPsp; ++i)
					{
						len += sprintf((buf + len), ", Psp[%d]:%d pspEpLst[%d]:%d",i,
									m_addPs.psp[i], m_addPs.pspEpLst[i].nmbEp);

						for(int j=0; j < m_addPs.pspEpLst[i].nmbEp; ++j)
						{
							len += sprintf((buf + len), ", endpIds[%d]:%d, ", j,
									m_addPs.pspEpLst[i].endpIds[j]);
						}
					}

   return buf;
}


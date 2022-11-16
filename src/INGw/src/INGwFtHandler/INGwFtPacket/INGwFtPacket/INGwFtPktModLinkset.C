#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktModLinkset.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktModLinkset::INGwFtPktModLinkset()
{
   mMsgData.msMsgType   = MSG_MOD_LINKSET;
	 m_isAddLinksetSet    = false;
	 memset(&m_addLinkset, 0, sizeof(AddLinkSet));
}

void
INGwFtPktModLinkset::initialize(short senderid, short receiverid,
																AddLinkSet& addLinkset)
{
  mMsgData.msSender    = senderid;
	mMsgData.msReceiver  = receiverid;
	m_isAddLinksetSet = true;
	memcpy(&m_addLinkset, &addLinkset, sizeof(AddLinkSet));
}

INGwFtPktModLinkset::~INGwFtPktModLinkset()
{
}

int 
INGwFtPktModLinkset::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktModLinkset::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktModLinkset::depacketize buffSize:%d, expected:%d", asSize,
		(sizeof(m_addLinkset)+SIZE_OF_BPMSG));

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U16 lSdata =0;
	U32 lLdata =0;

	// U16 lnkSetId
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addLinkset.lnkSetId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// U8 lnkSetType
	memcpy((void*)&m_addLinkset.lnkSetType, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U32 adjDpc
	memcpy((void*)&lLdata, apcData + offset, SIZE_OF_LONG);
	m_addLinkset.adjDpc = ntohl(lLdata);
	offset += SIZE_OF_LONG;

	// U16 nmbActLnkReqd
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addLinkset.nmbActLnkReqd=ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// U16 nmbCmbLnkSet
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addLinkset.nmbCmbLnkSet=ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// cmbLnkSet
	for(int i=0; i < m_addLinkset.nmbCmbLnkSet; ++i)
	{
		// U16 cmbLnkSetId
		memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
		m_addLinkset.cmbLnkSet[i].cmbLnkSetId=ntohs(lSdata);
		offset += SIZE_OF_SHORT;

		// U8 lnkSetPrior
		memcpy((void*)&m_addLinkset.cmbLnkSet[i].lnkSetPrior, 
													apcData + offset, SIZE_OF_CHAR);
		offset += SIZE_OF_CHAR;

		// U16 nmbPrefLinks
		memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
		m_addLinkset.cmbLnkSet[i].nmbPrefLinks=ntohs(lSdata);
		offset += SIZE_OF_SHORT;

		// U16 prefLnkId[LSN_MAX_PREFFERED_LINKS]
		for(int j=0; j < m_addLinkset.cmbLnkSet[i].nmbPrefLinks; ++j)
		{
			memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
			m_addLinkset.cmbLnkSet[i].prefLnkId[j]=ntohs(lSdata);
			offset += SIZE_OF_SHORT;
		}
	}

	logger.logMsg(VERBOSE_FLAG, 0,
	"INGwFtPktModLinkset::depacketize rxBufSize:%d, offset:%d", asSize, offset);

	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktModLinkset::depacketize");
	return (offset);
}

int INGwFtPktModLinkset::packetize(char** apcData, int version)
{
	// calculate size of buffer to be created
	// depends on nmbBpc and nmbSsns
	int size = sizeof(AddLinkSet) - 
					 ((LSN_MAXCMBLNK - m_addLinkset.nmbCmbLnkSet)*sizeof(SnCmbineLnkSet));

	int size2 =0;
	if(m_addLinkset.nmbCmbLnkSet != 0)
	{
		for(int i=0; i < m_addLinkset.nmbCmbLnkSet; ++i)
		{
			if(m_addLinkset.cmbLnkSet[i].nmbPrefLinks !=0)
			{
				size2 += 
				(LSN_MAX_PREFFERED_LINKS-m_addLinkset.cmbLnkSet[i].nmbPrefLinks)*
																										sizeof(U16);
			}
			else 
			{
				size2 += sizeof(U16)*LSN_MAX_PREFFERED_LINKS;
			}
		}
	}

	size = size - size2;

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktModLinkset::packetize sizeAllocated:%d, size2:%d", size, size2);

	int offset = INGwFtPktMsg::createPacket(size, apcData, version);

	char *pkt = *apcData;

	if(!m_isAddLinksetSet)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwFtPktModLinkset::packetize ModLinkset has not been set");
	}

	U16 lSdata =0;
	U32 lLdata =0;

	// U16 lnkSetId
	lSdata = htonl(m_addLinkset.lnkSetId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// U8 lnkSetType
	memcpy(pkt + offset, (void*)&m_addLinkset.lnkSetType, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U32 adjDpc
	lLdata = htonl(m_addLinkset.adjDpc);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
	offset += SIZE_OF_LONG;

	// U16 nmbActLnkReqd
	lSdata = htonl(m_addLinkset.nmbActLnkReqd);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// U16 nmbCmbLnkSet
	lSdata = htons(m_addLinkset.nmbCmbLnkSet);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// cmbLnkSet
	for(int i=0; i < m_addLinkset.nmbCmbLnkSet; ++i)
	{
		// U16 cmbLnkSetId
		lSdata = htons(m_addLinkset.cmbLnkSet[i].cmbLnkSetId);
		memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
		offset += SIZE_OF_SHORT;

		// U8 lnkSetPrior
		memcpy(pkt + offset, (void*)&m_addLinkset.cmbLnkSet[i].lnkSetPrior, 
																										SIZE_OF_CHAR);
		offset += SIZE_OF_CHAR;

		// U16 nmbPrefLinks
		lSdata = htons(m_addLinkset.cmbLnkSet[i].nmbPrefLinks);
		memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
		offset += SIZE_OF_SHORT;

		// U16 prefLnkId[LSN_MAX_PREFFERED_LINKS]
		for(int j=0; j < m_addLinkset.cmbLnkSet[i].nmbPrefLinks; ++j)
		{
			lSdata = htons(m_addLinkset.cmbLnkSet[i].prefLnkId[j]);
			memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
			offset += SIZE_OF_SHORT;
		}
	}

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktModLinkset::packetize, offset:%d actualLength:%d", offset, size);

	return (offset);
}

void
INGwFtPktModLinkset::getLinksetData(AddLinkSet &addLinkset)
{
	memcpy(&addLinkset, &m_addLinkset, sizeof(AddLinkSet));
}

void
INGwFtPktModLinkset::setLinksetData(AddLinkSet &addLinkset)
{
	m_isAddLinksetSet = true;
	memcpy(&m_addLinkset, &addLinkset, sizeof(AddLinkSet));
}

std::string INGwFtPktModLinkset::toLog(void) const
{
   char buf[4000];
   int len =0;

	 memset(buf, 0, sizeof(buf));

   len += sprintf((buf + len), "%s", INGwFtPktMsg::toLog().c_str());
   len += sprintf((buf + len), "Modify Linkset: lnkSetId:%d,"
          "lnksetType: %d, adjDpc: %d, nmbAckLnkReqd: %d, nmbCmbLnkSet: %d",
				 	m_addLinkset.lnkSetId, m_addLinkset.lnkSetType,
          m_addLinkset.adjDpc, m_addLinkset.nmbActLnkReqd, 
					m_addLinkset.nmbCmbLnkSet);
		
		for(int i=0; i < m_addLinkset.nmbCmbLnkSet; ++i)
		{
   		len += sprintf((buf + len), "cmbLnkSet[%d]: cmbLnkSetId: %d,"
				"lnkSetPrior: %d, nmbPrefLinks: %d", i, 
				m_addLinkset.cmbLnkSet[i].cmbLnkSetId,
	 			m_addLinkset.cmbLnkSet[i].lnkSetPrior,
				m_addLinkset.cmbLnkSet[i].nmbPrefLinks);

			for(int j=0; j < m_addLinkset.cmbLnkSet[i].nmbPrefLinks; ++j)
			{
				len += sprintf((buf + len), "prefLnkId[%d]:%d, ",
							j, m_addLinkset.cmbLnkSet[i].prefLnkId[j]);
			}
		}

   return buf;
}


#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktAddGtAddrMap.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktAddGtAddrMap::INGwFtPktAddGtAddrMap()
{
   mMsgData.msMsgType   = MSG_ADD_ADDRMAP;
	 m_isAddGtAddrMapSet       = false;
	 memset(&m_addGtAddrMap, 0, sizeof(AddAddrMapCfg));
}

void
INGwFtPktAddGtAddrMap::initialize(short senderid, short receiverid, 
															AddAddrMapCfg& addAddrMap)
{
  mMsgData.msSender    = senderid;
  mMsgData.msReceiver  = receiverid;
	m_isAddGtAddrMapSet = true;
	memcpy(&m_addGtAddrMap, &addAddrMap, sizeof(AddAddrMapCfg));
}

INGwFtPktAddGtAddrMap::~INGwFtPktAddGtAddrMap()
{
}

int 
INGwFtPktAddGtAddrMap::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktAddGtAddrMap::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddGtAddrMap::depacketize buffSize:%d, expected:%d", asSize,
		(sizeof(m_addGtAddrMap)+SIZE_OF_BPMSG));

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U16 lSdata =0;
  U32 lLdata =0;

  // U16 nwkId
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addGtAddrMap.nwkId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

  // S16 sw 
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addGtAddrMap.sw = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

  // U8 format
	memcpy((void*)&m_addGtAddrMap.format, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // Bool oddEven
	memcpy((void*)&m_addGtAddrMap.oddEven, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 natAddr
	memcpy((void*)&m_addGtAddrMap.natAddr, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 tType
	memcpy((void*)&m_addGtAddrMap.tType, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 numPlan
	memcpy((void*)&m_addGtAddrMap.numPlan, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 encSch
	memcpy((void*)&m_addGtAddrMap.encSch, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // actn U8 type
  memcpy((void*)&m_addGtAddrMap.actn.type, apcData + offset, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // actn U8 nmbActns
  memcpy((void*)&m_addGtAddrMap.actn.nmbActns, apcData + offset, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // actn U8 startDigit
  memcpy((void*)&m_addGtAddrMap.actn.startDigit, apcData + offset,SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // actn U8 endDigit
  memcpy((void*)&m_addGtAddrMap.actn.endDigit, apcData + offset, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // U8 gtDigLen
  memcpy((void*)&m_addGtAddrMap.gtDigLen, apcData + offset, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // U8 gtDigits[]
  memcpy((void*)&m_addGtAddrMap.gtDigits, apcData + offset, 
                                                  m_addGtAddrMap.gtDigLen);
  offset += m_addGtAddrMap.gtDigLen;

  // Bool replGt
  memcpy((void*)&m_addGtAddrMap.replGt, apcData + offset, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // U8 mode
  memcpy((void*)&m_addGtAddrMap.mode, apcData + offset, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // U16 outNwId
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addGtAddrMap.nwkId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

  // U8 numEntity
  memcpy((void*)&m_addGtAddrMap.numEntity, apcData + offset, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // OutAddr outAddr[]
  for(int i=0; i < m_addGtAddrMap.numEntity; ++i)
  {
    // U8 spHdrOpt
    memcpy((void*)&m_addGtAddrMap.outAddr[i].spHdrOpt, apcData + offset, 
                                                    SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // S16 swtch
    memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
    m_addGtAddrMap.outAddr[i].swtch = ntohs(lSdata);
    offset += SIZE_OF_SHORT;

    // U8 ssf
    memcpy((void*)&m_addGtAddrMap.outAddr[i].ssf, apcData + offset,
                                                            SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // Bool niInd
    memcpy((void*)&m_addGtAddrMap.outAddr[i].niInd, apcData + offset, 
                                                           SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 rtgInd
    memcpy((void*)&m_addGtAddrMap.outAddr[i].rtgInd, apcData + offset,
                                                           SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // Bool ssnInd
    memcpy((void*)&m_addGtAddrMap.outAddr[i].ssnInd, apcData + offset,
                                                           SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    //  Bool pcInd
    memcpy((void*)&m_addGtAddrMap.outAddr[i].pcInd, apcData + offset, 
                                                          SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 ssn
    memcpy((void*)&m_addGtAddrMap.outAddr[i].ssn, apcData + offset, 
                                                          SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U32 pc
    memcpy((void*)&lLdata, apcData + offset, SIZE_OF_LONG);
    m_addGtAddrMap.outAddr[i].pc = ntohl(lLdata); 
    offset += SIZE_OF_LONG;

    // U8 format
    memcpy((void*)&m_addGtAddrMap.outAddr[i].format, apcData + offset,
                                                          SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // Bool oddEven
    memcpy((void*)&m_addGtAddrMap.outAddr[i].oddEven, apcData + offset,
                                                          SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 tType
    memcpy((void*)&m_addGtAddrMap.outAddr[i].tType, apcData + offset, 
                                                          SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 natAddr
    memcpy((void*)&m_addGtAddrMap.outAddr[i].natAddr, apcData + offset,
                                                          SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 numPlan
    memcpy((void*)&m_addGtAddrMap.outAddr[i].numPlan, apcData + offset,
                                                         SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 encSch
    memcpy((void*)&m_addGtAddrMap.outAddr[i].encSch, apcData + offset, 
                                                         SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 gtDigLen
    memcpy((void*)&m_addGtAddrMap.outAddr[i].gtDigLen, apcData + offset,
                                                        SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 gtDigits[SHRTADRLEN]
    memcpy((void*)&m_addGtAddrMap.outAddr[i].gtDigits, apcData + offset, 
                                          m_addGtAddrMap.outAddr[i].gtDigLen);
    offset += m_addGtAddrMap.outAddr[i].gtDigLen;
  }

	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktAddGtAddrMap::depacketize");
	return (offset);
}

int INGwFtPktAddGtAddrMap::packetize(char** apcData, int version)
{
	// calculate size of buffer to be created
	// depends on nmbBpc and nmbSsns
	int size = sizeof(AddAddrMapCfg) - 
             (MAX_GT_DIGITS - m_addGtAddrMap.gtDigLen) -
          ((MAXENTITIES - m_addGtAddrMap.numEntity)*sizeof(OutAddr));

  int size2 =0;
  for(int i=0;i < m_addGtAddrMap.numEntity; ++i)
  {
    size2 += (SHRTADRLEN - m_addGtAddrMap.outAddr[i].gtDigLen);
  }

  size -= size2;

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddGtAddrMap::packetize sizeAllocated:%d, size2:%d", size, size2); 

	int offset = INGwFtPktMsg::createPacket(size, apcData, version);

	char *pkt = *apcData;

	if(!m_isAddGtAddrMapSet)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwFtPktAddGtAddrMap::packetize AddGtAddrMap has not been set");
	}

	U16 lSdata =0;
  U32 lLdata =0;

  // U16 nwkId
	lSdata = htons(m_addGtAddrMap.nwkId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

  // S16 sw 
	lSdata = htons(m_addGtAddrMap.sw);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

  // U8 format
	memcpy(pkt + offset, (void*)&m_addGtAddrMap.format, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // Bool oddEven
	memcpy(pkt + offset, (void*)&m_addGtAddrMap.oddEven, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 natAddr
	memcpy(pkt + offset, (void*)&m_addGtAddrMap.natAddr, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 tType
	memcpy(pkt + offset, (void*)&m_addGtAddrMap.tType, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 numPlan
	memcpy(pkt + offset, (void*)&m_addGtAddrMap.numPlan, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 encSch
	memcpy(pkt + offset, (void*)&m_addGtAddrMap.encSch, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // actn U8 type
  memcpy(pkt + offset, (void*)&m_addGtAddrMap.actn.type, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // actn U8 nmbActns
  memcpy(pkt + offset, (void*)&m_addGtAddrMap.actn.nmbActns, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // actn U8 startDigit
  memcpy(pkt + offset, (void*)&m_addGtAddrMap.actn.startDigit, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // actn U8 endDigit
  memcpy(pkt + offset, (void*)&m_addGtAddrMap.actn.endDigit, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // U8 gtDigLen
  memcpy(pkt + offset, (void*)&m_addGtAddrMap.gtDigLen, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // U8 gtDigits[]
  memcpy(pkt + offset, (void*)&m_addGtAddrMap.gtDigits, 
                                                  m_addGtAddrMap.gtDigLen);
  offset += m_addGtAddrMap.gtDigLen;

  // Bool replGt
  memcpy(pkt + offset, (void*)&m_addGtAddrMap.replGt, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // U8 mode
  memcpy(pkt + offset, (void*)&m_addGtAddrMap.mode, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // U16 outNwId
	lSdata = htons(m_addGtAddrMap.outNwId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

  // U8 numEntity
  memcpy(pkt + offset, (void*)&m_addGtAddrMap.numEntity, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // OutAddr outAddr[]
  for(int i=0; i < m_addGtAddrMap.numEntity; ++i)
  {
    // U8 spHdrOpt
    memcpy(pkt + offset, (void*)&m_addGtAddrMap.outAddr[i].spHdrOpt, 
                                                    SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // S16 swtch
    lSdata = htons(m_addGtAddrMap.outAddr[i].swtch);
    memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
    offset += SIZE_OF_SHORT;

    // U8 ssf
    memcpy(pkt + offset, (void*)&m_addGtAddrMap.outAddr[i].ssf, SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // Bool niInd
    memcpy(pkt + offset, (void*)&m_addGtAddrMap.outAddr[i].niInd, SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 rtgInd
    memcpy(pkt + offset, (void*)&m_addGtAddrMap.outAddr[i].rtgInd,SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // Bool ssnInd
    memcpy(pkt + offset, (void*)&m_addGtAddrMap.outAddr[i].ssnInd,SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    //  Bool pcInd
    memcpy(pkt + offset, (void*)&m_addGtAddrMap.outAddr[i].pcInd, SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 ssn
    memcpy(pkt + offset, (void*)&m_addGtAddrMap.outAddr[i].ssn, SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U32 pc
    lLdata = htonl(m_addGtAddrMap.outAddr[i].pc);
    memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
    offset += SIZE_OF_LONG;

    // U8 format
    memcpy(pkt + offset, (void*)&m_addGtAddrMap.outAddr[i].format,SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // Bool oddEven
    memcpy(pkt+ offset, (void*)&m_addGtAddrMap.outAddr[i].oddEven,SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 tType
    memcpy(pkt + offset, (void*)&m_addGtAddrMap.outAddr[i].tType, SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 natAddr
    memcpy(pkt+ offset, (void*)&m_addGtAddrMap.outAddr[i].natAddr,SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 numPlan
    memcpy(pkt+ offset, (void*)&m_addGtAddrMap.outAddr[i].numPlan,SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 encSch
    memcpy(pkt+offset, (void*)&m_addGtAddrMap.outAddr[i].encSch, SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 gtDigLen
    memcpy(pkt+offset, (void*)&m_addGtAddrMap.outAddr[i].gtDigLen,SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 gtDigits[SHRTADRLEN]
    memcpy(pkt + offset, (void*)&m_addGtAddrMap.outAddr[i].gtDigits, 
                                          m_addGtAddrMap.outAddr[i].gtDigLen);
    offset += m_addGtAddrMap.outAddr[i].gtDigLen;
  }

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddGtAddrMap::packetize, offset:%d actualLength:%d",offset, 
                                                (size+ SIZE_OF_BPMSG));

	return (offset);
}

void
INGwFtPktAddGtAddrMap::getGtAddrMapData(AddAddrMapCfg &addGtAddrMap)
{
	memcpy(&addGtAddrMap, &m_addGtAddrMap, sizeof(AddAddrMapCfg));
}

void
INGwFtPktAddGtAddrMap::setGtAddrMapData(AddAddrMapCfg &addGtAddrMap)
{
	m_isAddGtAddrMapSet = true;
	memcpy(&m_addGtAddrMap, &addGtAddrMap, sizeof(AddAddrMapCfg));
}

std::string INGwFtPktAddGtAddrMap::toLog(void) const
{
   char buf[4000];
   int len =0;

	 memset(buf, 0, sizeof(buf));

   len += sprintf((buf + len), "%s", INGwFtPktMsg::toLog().c_str());
   len += sprintf((buf + len), 
        "Add GtAddrMap Request: nwId:%d, sw:%d, format:%d, replGt:%d,"
        "oddEven:%d, natAddr:%d, tType:%d, numPlan:%d, encSch:%d "
        "nmbActns:%d Action[type:%d, startDig:%d, endDig:%d],"
        "gtDigits:%s, gtDigLen:%d", 
        m_addGtAddrMap.nwkId, m_addGtAddrMap.sw, m_addGtAddrMap.format, 
        m_addGtAddrMap.replGt, m_addGtAddrMap.oddEven, m_addGtAddrMap.natAddr,
        m_addGtAddrMap.tType, m_addGtAddrMap.numPlan, m_addGtAddrMap.encSch, 
        m_addGtAddrMap.actn.nmbActns, m_addGtAddrMap.actn.type, 
        m_addGtAddrMap.actn.startDigit, m_addGtAddrMap.actn.endDigit,
        m_addGtAddrMap.gtDigits, m_addGtAddrMap.gtDigLen);

      for(int i=0; i < m_addGtAddrMap.numEntity; ++i)
      {
        len += sprintf((buf + len),
                   "OutAddress [(%d):spHdrOpt:%d, swtch:%d, ssf:%d,"
          "niInd:%d, rtgInd:%d, ssnInd:%d, pcInd:%d, ssn:%d"
          ", pc:%d, format:%d, oddEven:%d, tType:%d, "
          "natAddr:%d, numPlan:%d, encSch:%d, gtDigLen:%d,"
          "gtDigits:%s", i,
            m_addGtAddrMap.outAddr[i].spHdrOpt, m_addGtAddrMap.outAddr[i].swtch,
            m_addGtAddrMap.outAddr[i].ssf, m_addGtAddrMap.outAddr[i].niInd,
            m_addGtAddrMap.outAddr[i].rtgInd, m_addGtAddrMap.outAddr[i].ssnInd,
            m_addGtAddrMap.outAddr[i].pcInd, m_addGtAddrMap.outAddr[i].ssn,
            m_addGtAddrMap.outAddr[i].pc, m_addGtAddrMap.outAddr[i].format,
            m_addGtAddrMap.outAddr[i].oddEven, m_addGtAddrMap.outAddr[i].tType,
            m_addGtAddrMap.outAddr[i].natAddr,m_addGtAddrMap.outAddr[i].numPlan,
            m_addGtAddrMap.outAddr[i].encSch,m_addGtAddrMap.outAddr[i].gtDigLen,
            m_addGtAddrMap.outAddr[i].gtDigits);

      }

   return buf;
}


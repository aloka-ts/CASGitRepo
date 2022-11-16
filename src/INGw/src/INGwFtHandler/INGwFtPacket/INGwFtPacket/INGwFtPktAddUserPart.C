#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktAddUserPart.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktAddUserPart::INGwFtPktAddUserPart()
{
   mMsgData.msMsgType   = MSG_ADD_USER_PART;
	 m_isAddUserPartSet       	= false;
	 memset(&m_addUserPart, 0, sizeof(AddUserPart));
}

void
INGwFtPktAddUserPart::initialize(short senderid, short receiverid, 
																 AddUserPart& addUserPart)
{
  mMsgData.msSender    = senderid;
  mMsgData.msReceiver  = receiverid;
	m_isAddUserPartSet = true;
	memcpy(&m_addUserPart, &addUserPart, sizeof(AddUserPart));
}

INGwFtPktAddUserPart::~INGwFtPktAddUserPart()
{
}

int 
INGwFtPktAddUserPart::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktAddUserPart::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddUserPart::depacketize buffSize:%d", asSize);

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U16 lSdata =0;

	// U8 userPartType
	memcpy((void*)&m_addUserPart.userPartType, apcData+offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 ssf
	memcpy((void*)&m_addUserPart.ssf, apcData+offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 lnkType
	memcpy((void*)&m_addUserPart.lnkType, apcData+offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 upSwtch
	memcpy((void*)&m_addUserPart.upSwtch, apcData+offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 suType
	memcpy((void*)&m_addUserPart.suType, apcData+offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// S16 mtp3UsapId
	memcpy((void*)&lSdata, apcData+offset, SIZE_OF_SHORT);
	m_addUserPart.mtp3UsapId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// U16 nwId
	memcpy((void*)&lSdata, apcData+offset, SIZE_OF_SHORT);
	m_addUserPart.nwId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// S16 m3uaUsapId
	memcpy((void*)&lSdata, apcData+offset, SIZE_OF_SHORT);
	m_addUserPart.m3uaUsapId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// S16 sccpLsapId
	memcpy((void*)&lSdata, apcData+offset, SIZE_OF_SHORT);
	m_addUserPart.sccpLsapId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// U8 currentUserState
	memcpy((void*)&m_addUserPart.currentUserState, apcData+offset, SIZE_OF_CHAR);
 	offset += SIZE_OF_CHAR;

	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktAddUserPart::depacketize");
	return (offset);
}

int INGwFtPktAddUserPart::packetize(char** apcData, int version)
{
	// calculate size of buffer to be created
	int size = sizeof(m_addUserPart);

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddUserPart::packetize sizeAllocated:%d", size);

	int offset = INGwFtPktMsg::createPacket(size, apcData, version);

	char *pkt = *apcData;

	if(!m_isAddUserPartSet)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwFtPktAddUserPart::packetize AddUserPart has not been set");
	}

	U16 lSdata =0;

	// U8 userPartType
	memcpy(pkt + offset, (void*)&m_addUserPart.userPartType, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 ssf
	memcpy(pkt + offset, (void*)&m_addUserPart.ssf, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 lnkType
	memcpy(pkt + offset, (void*)&m_addUserPart.lnkType, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 upSwtch
	memcpy(pkt + offset, (void*)&m_addUserPart.upSwtch, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 suType
	memcpy(pkt + offset, (void*)&m_addUserPart.suType, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// S16 mtp3UsapId
	lSdata = htons(m_addUserPart.mtp3UsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// U16 nwId
	lSdata = htons(m_addUserPart.nwId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// S16 m3uaUsapId
	lSdata = htons(m_addUserPart.m3uaUsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// S16 sccpLsapId
	lSdata = htons(m_addUserPart.sccpLsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// U8 currentUserState
	memcpy(pkt + offset, (void*)&m_addUserPart.currentUserState, SIZE_OF_CHAR);
 	offset += SIZE_OF_CHAR;

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddUserPart::packetize, offset:%d actualLength:%d", offset, size); 

	return (offset);
}

void
INGwFtPktAddUserPart::getUserPartData(AddUserPart &addUserPart)
{
	memcpy(&addUserPart, &m_addUserPart, sizeof(AddUserPart));
}

void
INGwFtPktAddUserPart::setUserPartData(AddUserPart &addUserPart)
{
	m_isAddUserPartSet = true;
	memcpy(&m_addUserPart, &addUserPart, sizeof(AddUserPart));
}

std::string INGwFtPktAddUserPart::toLog(void) const
{
   char buf[4000];
   int len =0;

	 memset(buf, 0, sizeof(buf));

   len += sprintf((buf + len), "%s", INGwFtPktMsg::toLog().c_str());
   len += sprintf((buf + len), 
     "Add UserPart userPartType:%d, ssf:%d, lnkType:%d, "
      " upSwtch:%d, mtp3UsapId:%d, nwId:%d, suType:%d, m3uaUsapId:%d, "
      "sccpLsapId:%d, currentUserState: %d", m_addUserPart.userPartType,
      m_addUserPart.ssf, m_addUserPart.lnkType,
      m_addUserPart.upSwtch, m_addUserPart.mtp3UsapId,
      m_addUserPart.nwId, m_addUserPart.suType,
      m_addUserPart.m3uaUsapId, m_addUserPart.sccpLsapId,
      m_addUserPart.currentUserState);

   return buf;
}


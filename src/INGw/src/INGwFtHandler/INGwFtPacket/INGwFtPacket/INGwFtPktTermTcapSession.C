//********************************************************************
//
//     File:    INGwFtPktTermTcapSession.C
//
//     Desc:     
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Yogesh Tripathi 23/02/12       Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktTermTcapSession.h>

INGwFtPktTermTcapSession::INGwFtPktTermTcapSession(void)
{
    mMsgData.msMsgType = MSG_CLOSE_TCAP_SESSION;
    isReplicated = true;
}

INGwFtPktTermTcapSession::~INGwFtPktTermTcapSession()
{
}

void
INGwFtPktTermTcapSession::initialize(U32 p_dialogueId, 
                                    short srcid, short destid, 
                                    bool p_isReplicated)
{
   mMsgData.msSender     = srcid;
   mMsgData.msReceiver   = destid;

   m_dialogueId = p_dialogueId;
   isReplicated = p_isReplicated;
}

int
INGwFtPktTermTcapSession::depacketize(const char* apcData, int asSize, int version)
{
   if(version != 1)
   {
      return 0;
   }

   int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

   memcpy(&m_dialogueId, apcData + offset, SIZE_OF_INT);
   m_dialogueId = ntohs(m_dialogueId);
   offset += SIZE_OF_INT;

   return (offset);
}

int
INGwFtPktTermTcapSession::packetize(char** apcData, int version)
{
   if(version != 1)
   {
      return 0;
   }

   *apcData = NULL;

   int offset = 
      INGwFtPktMsg::createPacket(SIZE_OF_INT, apcData, version);

   char *pkt = *apcData;

   int lDialogueId = htons(m_dialogueId);
   memcpy(pkt + offset, (void*)&lDialogueId, SIZE_OF_INT);
   offset += SIZE_OF_INT;

   return offset;
}

std::string
INGwFtPktTermTcapSession::toLog(void) const
{
    std::ostringstream    oStr;
		std::string lRoleStr = "INGW_TERMINATE_TCAP_SESSION";

    oStr << INGwFtPktMsg::toLog();
    oStr << "DialogueId: "  << m_dialogueId << endl << lRoleStr.c_str();
    return oStr.str();
}

int
INGwFtPktTermTcapSession::getSeqNum()
{
	 return m_seqNum;
}

U32 
INGwFtPktTermTcapSession::getDialogueId()
{
  return m_dialogueId;
}

U8
INGwFtPktTermTcapSession::getOrigin()
{
  return m_origin;
}

bool
INGwFtPktTermTcapSession::receivedFromPeer() {
  return isReplicated;
}


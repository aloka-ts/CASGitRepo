//********************************************************************
//
//     File:    INGwFtPktTcapCallSeqAck.C
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

#include <INGwFtPacket/INGwFtPktTcapCallSeqAck.h>

INGwFtPktTcapCallSeqAck::INGwFtPktTcapCallSeqAck(void)
{
    mMsgData.msMsgType = MSG_TCAP_CALL_SEQ_ACK;
		m_origin = 0;
    
    //this is to make sby component realize this component is replicated
    m_isReplicated = true;
}

INGwFtPktTcapCallSeqAck::~INGwFtPktTcapCallSeqAck()
{
}

void
INGwFtPktTcapCallSeqAck::initialize(U8 p_origin, U32 p_dialogueId, 
                                    int p_seqNum, short srcid, short destid,
                                    bool p_isReplicated)
{
   mMsgData.msSender   = srcid;
   mMsgData.msReceiver = destid;

	 m_origin            = p_origin;
   m_dialogueId        = p_dialogueId;
   m_seqNum            = p_seqNum;
   m_isReplicated      = p_isReplicated;
}

int
INGwFtPktTcapCallSeqAck::depacketize(const char* apcData, int asSize, int version)
{
   if(version != 1)
   {
      return 0;
   }

   int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

   memcpy(&m_origin, apcData + offset, SIZE_OF_CHAR);
   m_origin = ntohs(m_origin);
   offset += SIZE_OF_CHAR;

   memcpy(&m_seqNum, apcData + offset, SIZE_OF_INT);
   m_seqNum = ntohs(m_seqNum);
   offset += SIZE_OF_INT;


   memcpy(&m_dialogueId, apcData + offset, SIZE_OF_LONG);
   m_dialogueId = ntohs(m_dialogueId);
   offset += SIZE_OF_LONG;

   return (offset);
}

int
INGwFtPktTcapCallSeqAck::packetize(char** apcData, int version)
{
   if(version != 1)
   {
      return 0;
   }

   *apcData = NULL;
   int length = SIZE_OF_SHORT + SIZE_OF_CHAR + SIZE_OF_INT+ SIZE_OF_LONG;

   int offset = 
      INGwFtPktMsg::createPacket(length, apcData, version);

   char *pkt = *apcData;

   U8 ldata = htons(m_origin);
   memcpy(pkt + offset, (void*)&ldata, SIZE_OF_CHAR);
   offset += SIZE_OF_CHAR;

   int lSeqNum = htons(m_seqNum);
   memcpy(pkt + offset, (void*)&lSeqNum, SIZE_OF_INT);
   offset += SIZE_OF_INT;

   int lDialogueId = htons(m_dialogueId);
   memcpy(pkt + offset, (void*)&lDialogueId, SIZE_OF_LONG);
   offset += SIZE_OF_LONG;

   return offset;
}

std::string
INGwFtPktTcapCallSeqAck::toLog(void) const
{
    std::ostringstream    oStr;
		std::string lRoleStr = (INGW_CLEAN_INBOUND_MESSAGE == m_origin) ? 
													 " - INGW_CLEAN_INBOUND_MESSAGE" :
													 (INGW_CLEAN_OUTBOUND_MESSAGE == m_origin) ?
													 " - INGW_CLEAN_OUTBOUND_MESSAGE" :
													 " - UNKNOWN_ORIGIN";

    oStr << INGwFtPktMsg::toLog();
    oStr << "[ DialogueId: "  << m_dialogueId << " SeqNum: " << m_seqNum<< "] "
         << endl << lRoleStr.c_str();
    return oStr.str();
}

int
INGwFtPktTcapCallSeqAck::getSeqNum()
{
	 return m_seqNum;
}

U32 
INGwFtPktTcapCallSeqAck::getDialogueId()
{
  return m_dialogueId;
}

U8
INGwFtPktTcapCallSeqAck::getOrigin()
{
  return m_origin;
}

bool
INGwFtPktTcapCallSeqAck::isReplicated() {
  return m_isReplicated;
}

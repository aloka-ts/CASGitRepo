//////////////////////////////////////////////////////////////////////////
//*********************************************************************
//   GENBAND, Inc. Confidential and Proprietary
//
// This work contains valuable confidential and proprietary
// information.
// Disclosure, use or reproduction without the written authorization of
// GENBAND, Inc. is prohibited.  This unpublished work by GENBAND, Inc.
// is protected by the laws of the United States and other countries.
// If publication of the work should occur the following notice shall
// apply:
//
// "Copyright 2007 GENBAND, Inc.  All rights reserved."
//*********************************************************************
//********************************************************************
//
//     File:    INGwFtPktRoleMsg.C
//
//     Desc:
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal   23/11/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktRoleMsg.h>

INGwFtPktRoleMsg::INGwFtPktRoleMsg(void)
{
    mMsgData.msMsgType = MSG_FT_ROLE_NEGOTIATION;
		m_RoleMsgType = 0;
}

INGwFtPktRoleMsg::~INGwFtPktRoleMsg()
{
}

void
INGwFtPktRoleMsg::initialize(short p_RoleMsgType,
                             short srcid, short destid)
{
   mMsgData.msSender     = srcid;
   mMsgData.msReceiver   = destid;

	 m_RoleMsgType = p_RoleMsgType;
}

int
INGwFtPktRoleMsg::depacketize(const char* apcData, int asSize, int version)
{
   if(version != 1)
   {
      return 0;
   }

   int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

   // Extract the role msg type
   //

   memcpy(&m_RoleMsgType, apcData + offset, SIZE_OF_SHORT);
   m_RoleMsgType   = ntohs(m_RoleMsgType);
   offset += SIZE_OF_SHORT;

   return (offset);
}

int
INGwFtPktRoleMsg::packetize(char** apcData, int version)
{
   if(version != 1)
   {
      return 0;
   }

   *apcData = NULL;

   int offset = 
      INGwFtPktMsg::createPacket(SIZE_OF_SHORT , apcData, version);

   char *pkt = *apcData;

   short ldata = htons(m_RoleMsgType);
   memcpy(pkt + offset, (void*)&ldata, SIZE_OF_SHORT);
   offset += SIZE_OF_SHORT;

   return offset;
}

std::string
INGwFtPktRoleMsg::toLog(void) const
{
    std::ostringstream    oStr;
		std::string lRoleStr = (m_RoleMsgType == ACTIVE_ROLE_MSG_TYPE) ? 
													 " - ACTIVE_ROLE_MSG_TYPE" :
													 (m_RoleMsgType == ACTIVE_PENDING_ROLE_MSG_TYPE) ?
													 " - ACTIVE_PENDING_ROLE_MSG_TYPE" :
													 " - ACTIVE_ROLE_ACK_MSG_TPYE";

    oStr << INGwFtPktMsg::toLog();
    oStr << " , RoleMsgType : "  << m_RoleMsgType << lRoleStr << "] ";
    return oStr.str();
}

short 
INGwFtPktRoleMsg::getRoleMsgType()
{
	 return m_RoleMsgType;
}


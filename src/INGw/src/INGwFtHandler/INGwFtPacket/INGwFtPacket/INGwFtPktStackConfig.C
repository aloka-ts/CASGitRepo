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
//     File:    INGwFtPktStackConfig.C
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

#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktStackConfig::INGwFtPktStackConfig(void)
{
    mMsgData.msMsgType = MSG_STACK_CONFIG;
		m_StackConfigType = 0;
}

INGwFtPktStackConfig::~INGwFtPktStackConfig()
{
}

void
INGwFtPktStackConfig::initialize(short p_StackConfigType,
                             short srcid, short destid)
{
   mMsgData.msSender     = srcid;
   mMsgData.msReceiver   = destid;

	 m_StackConfigType = p_StackConfigType;
}

int
INGwFtPktStackConfig::depacketize(const char* apcData, int asSize, int version)
{
   if(version != 1)
   {
      return 0;
   }

   int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

   // Extract the role msg type
   //

   memcpy(&m_StackConfigType, apcData + offset, SIZE_OF_SHORT);
   m_StackConfigType   = ntohs(m_StackConfigType);
   offset += SIZE_OF_SHORT;

   return (offset);
}

int
INGwFtPktStackConfig::packetize(char** apcData, int version)
{
   if(version != 1)
   {
      return 0;
   }

   *apcData = NULL;

   int offset = 
      INGwFtPktMsg::createPacket(SIZE_OF_SHORT , apcData, version);

   char *pkt = *apcData;

   short ldata = htons(m_StackConfigType);
   memcpy(pkt + offset, (void*)&ldata, SIZE_OF_SHORT);
   offset += SIZE_OF_SHORT;

   return offset;
}

std::string
INGwFtPktStackConfig::toLog(void) const
{
    std::ostringstream    oStr;
		std::string lRoleStr = (m_StackConfigType == STACK_CONFIG_INITIATED) ? 
													 " - STACK_CONFIG_INITIATED" :
													 (m_StackConfigType == STACK_CONFIG_END) ?
													 " - STACK_CONFIG_END" :
													 " - UNKNOWN";

    oStr << INGwFtPktMsg::toLog();
    oStr << " , StackConfigType : "  << m_StackConfigType << lRoleStr << "] ";
    return oStr.str();
}

short 
INGwFtPktStackConfig::getStackConfigType()
{
	 return m_StackConfigType;
}


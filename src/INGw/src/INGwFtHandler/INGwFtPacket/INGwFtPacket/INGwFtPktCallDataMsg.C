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
//     File:     INGwFtPktCallDataMsg.C
//
//     Desc:     Base class for replication msg
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal   23/11/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktCallDataMsg.h>

INGwFtPktCallDataMsg::INGwFtPktCallDataMsg(void):
    msCCMdataOffset(0),
    msCCMdataLen(0),
    mInternalBuf(NULL),
    buflen(0)
{
    mMsgData.msMsgType = MSG_CALL_BACKUP;
}

INGwFtPktCallDataMsg::~INGwFtPktCallDataMsg()
{
   if(mInternalBuf != NULL)
   {
      delete []mInternalBuf;
   }
}

void
INGwFtPktCallDataMsg::initialize(char *msg, short length, const char *callId, 
                          short srcid, short destid)
{
   mMsgData.msSender     = srcid;
   mMsgData.msReceiver   = destid;

   mCallId         = callId;
   msCCMdataOffset = SIZE_OF_BPMSG + BASE_CCM_CALLDATA_MSG_SIZE + 
                     SIZE_OF_SHORT + mCallId.size();
   msCCMdataLen    = length - msCCMdataOffset; 

   mInternalBuf = msg;
   buflen = length;
}

const std::string&
INGwFtPktCallDataMsg::getCallId(void) const
{
    return mCallId;
}

short
INGwFtPktCallDataMsg::getCCMdataOffset() const
{
    return msCCMdataOffset;
}

short
INGwFtPktCallDataMsg::getCCMDataLen() const
{
    return msCCMdataLen;
}

const char * INGwFtPktCallDataMsg::getCCMBuffer() const
{
   return (mInternalBuf + msCCMdataOffset);
}

int
INGwFtPktCallDataMsg::depacketize(const char* apcData, int asSize, int version)
{
   if(version != 1)
   {
      return 0;
   }

   mInternalBuf = (char *) apcData;
   buflen = asSize;

   int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

   // Extract the call Id
   //
   offset = getVLString(apcData, offset, mCallId);

   memcpy(&msCCMdataOffset, apcData + offset, SIZE_OF_SHORT);
   msCCMdataOffset   = ntohs(msCCMdataOffset);
   offset += SIZE_OF_SHORT;

   memcpy(&msCCMdataLen, apcData + offset, SIZE_OF_SHORT);
   msCCMdataLen   = ntohs(msCCMdataLen);
   offset += SIZE_OF_SHORT;

   return (offset);
}

int
INGwFtPktCallDataMsg::packetize(char** apcData, int version)
{
   if(version != 1)
   {
      return 0;
   }

   *apcData = mInternalBuf;
   mInternalBuf = NULL;

   int offset = 
      INGwFtPktMsg::createPacket(BASE_CCM_CALLDATA_MSG_SIZE + mCallId.size() + 
                          SIZE_OF_SHORT + msCCMdataLen, apcData, version);

   char *pkt = *apcData;

   short ldata = htons(mCallId.size());
   memcpy(pkt + offset, (void*)&ldata, SIZE_OF_SHORT);
   offset += SIZE_OF_SHORT;

   memcpy(pkt + offset,(void*)mCallId.c_str(), mCallId.size());
   offset += mCallId.size();

   ldata = htons(msCCMdataOffset);
   memcpy(pkt + offset, (void*)&ldata, SIZE_OF_SHORT);
   offset += SIZE_OF_SHORT;

   ldata = htons(msCCMdataLen);
   memcpy(pkt + offset, (void*)&ldata, SIZE_OF_SHORT);
   offset += SIZE_OF_SHORT;

   return buflen;
}

std::string

INGwFtPktCallDataMsg::toLog(void) const
{
    std::ostringstream    oStr;
    oStr << INGwFtPktMsg::toLog();
    oStr << " , CALL_ID : "  << mCallId;
    oStr << " , CCM_OFFSET : " << msCCMdataOffset;
    oStr << " , CCM_LEN : " << msCCMdataLen;
    
    return oStr.str();
}


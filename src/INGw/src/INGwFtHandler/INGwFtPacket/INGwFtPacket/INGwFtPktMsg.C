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
//     File:     INGwFtPktMsg.C
//
//     Desc:     
//               
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************


#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktMsg.h>

INGwFtPktMsg::INGwFtPktMsg(void)
{ 
    // By default the logging is switched ON
    mMsgData.miMsgStatus = (mMsgData.miMsgStatus | BPMSG_MSK_LOGGING_STATUS);

    sprintf(mcSendRecvTimeStamp, "XXX");
}

int
INGwFtPktMsg::depacketize(const char* apcData, int asSize, int version)
{
   if(version != 1)
   {
      return 0;
   }
    int offset = 0;

    memcpy(&mMsgData.msMsgType, apcData + offset, SIZE_OF_SHORT);
    mMsgData.msMsgType = ntohs(mMsgData.msMsgType);
    offset = offset + SIZE_OF_SHORT;
#ifdef DEBUG_MSG_PARSING
    logger.logINGwMsg(false, TRACE_FLAG, 0, "Msg depak [MSG_TYPE - %d, Offset - %d]", mMsgData.msMsgType, offset);
#endif

    memcpy(&mMsgData.msSender, apcData + offset, SIZE_OF_SHORT);
    mMsgData.msSender = ntohs(mMsgData.msSender);
    offset = offset + SIZE_OF_SHORT;
#ifdef DEBUG_MSG_PARSING
    logger.logINGwMsg(false, TRACE_FLAG, 0, "Msg depak [SENDER - %d, Offset - %d]", mMsgData.msSender, offset);
#endif

    memcpy(&mMsgData.msReceiver, apcData + offset, SIZE_OF_SHORT);
    mMsgData.msReceiver = ntohs(mMsgData.msReceiver);
    offset = offset + SIZE_OF_SHORT;
#ifdef DEBUG_MSG_PARSING
    logger.logINGwMsg(false, TRACE_FLAG, 0, "Msg depak [RECEIVER - %d, Offset - %d]", mMsgData.msReceiver, offset);
#endif

    memcpy(&mMsgData.msMajorSeqNum, apcData + offset, SIZE_OF_SHORT);
    mMsgData.msMajorSeqNum = ntohs(mMsgData.msMajorSeqNum);
    offset = offset + SIZE_OF_SHORT;
#ifdef DEBUG_MSG_PARSING
    logger.logINGwMsg(false, TRACE_FLAG, 0, "Msg depak [MAJOR - %d, Offset - %d]", mMsgData.msMajorSeqNum, offset);
#endif

    memcpy(&mMsgData.msMinorSeqNum, apcData + offset, SIZE_OF_SHORT);
    mMsgData.msMinorSeqNum = ntohs(mMsgData.msMinorSeqNum);
    offset = offset + SIZE_OF_SHORT;
#ifdef DEBUG_MSG_PARSING
    logger.logINGwMsg(false, TRACE_FLAG, 0, "Msg depak [MINOR - %d, Offset - %d]", mMsgData.msMinorSeqNum, offset);
#endif

    memcpy(&mMsgData.mlMsgGenTimeStamp, apcData + offset, SIZE_OF_LONG);
    mMsgData.mlMsgGenTimeStamp = ntohl(mMsgData.mlMsgGenTimeStamp); 
    offset = offset + SIZE_OF_LONG;
#ifdef DEBUG_MSG_PARSING
    logger.logINGwMsg(false, TRACE_FLAG, 0, "Msg depak [GEN_TS - %ld, Offset - %d]", mMsgData.mlMsgGenTimeStamp, offset);
#endif

    memcpy(&mMsgData.miMsgStatus, apcData + offset, SIZE_OF_INT);
    mMsgData.miMsgStatus    = ntohl(mMsgData.miMsgStatus);
    offset = offset + SIZE_OF_INT;
#ifdef DEBUG_MSG_PARSING
    logger.logINGwMsg(false, TRACE_FLAG, 0, "Msg depak [MSG_STATUS - %d, Offset - %d]", mMsgData.miMsgStatus, offset);
#endif

    // NOTE - The return value would be the same as SIZE_OF_BPMSG
    return offset;
}

void 
INGwFtPktMsg::markMsgGenTime(void)
{
    struct timeval currTime;
    gettimeofday(&currTime, NULL);
    mMsgData.mlMsgGenTimeStamp = currTime.tv_sec;
}

void
INGwFtPktMsg::markMsgSendRecvTime(void)
{
    struct timeval currTime;
    struct tm localTime;

    gettimeofday(&currTime, NULL);
    localtime_r(&currTime.tv_sec, &localTime);

    int timeChars = strftime(mcSendRecvTimeStamp, 64, "%H:%M:%S", &localTime);
    sprintf(mcSendRecvTimeStamp + timeChars, ".%03d", currTime.tv_usec/1000);
}

void
INGwFtPktMsg::setValue(short asId, const char* apcValue)
{
    switch(asId) {
        case MSG_FIELD_MSG_TYPE :
            mMsgData.msMsgType = static_cast<short>(strtol(apcValue, 0, 10));
            break;
        case MSG_FIELD_SENDER :
            mMsgData.msSender = strtol(apcValue, 0, 10);
            break;
        case MSG_FIELD_RECEIVER :
            mMsgData.msReceiver = strtol(apcValue, 0, 10);
            break;
        case MSG_FIELD_MAJOR_SEQ_NUM :
            mMsgData.msMajorSeqNum = static_cast<short>(strtol(apcValue, 0, 10));
            break;
        case MSG_FIELD_MINOR_SEQ_NUM :
            mMsgData.msMinorSeqNum = static_cast<short>(strtol(apcValue, 0, 10));
            break;
        default :
            break;
    }
}

std::string
INGwFtPktMsg::toLog(void) const
{
    std::ostringstream strStream;
    strStream << " MSG : " << mMsgData.msMsgType;
    strStream << " , SRID : " << mMsgData.msSender << "-" << mMsgData.msReceiver;
    if((mMsgData.msMsgType != MSG_FILTER_EVENT     ) &&
       (mMsgData.msMsgType != MSG_LOAD_FACTOR      ) &&
       (mMsgData.msMsgType != MSG_BLK_FOR_NEW_CALLS)) {
        strStream << " , SEQ : " << mMsgData.msMajorSeqNum << "-" << mMsgData.msMinorSeqNum;
    }
    strStream << " , MS : " << mMsgData.miMsgStatus;
    strStream << " , GEN_TS : " << mMsgData.mlMsgGenTimeStamp;
    strStream << " , SR_TS : " << mcSendRecvTimeStamp;
    return strStream.str();
}

short
INGwFtPktMsg::getFLString(const char* apcData,
    short asOffset, short asSize, std::string& arString)
{
    short offset = asOffset;
    arString.assign(apcData + asOffset, asSize);
    return asOffset + asSize;
}

short
INGwFtPktMsg::getVLString(const char* apcData, short asOffset, std::string& arString)
{
   short sVarLen = 0;
   short offset = asOffset;
   memcpy(&sVarLen, apcData + offset, SIZE_OF_SHORT);
   sVarLen = ntohs(sVarLen);
   offset += SIZE_OF_SHORT;

   arString.assign(apcData + offset, sVarLen);
   offset += sVarLen;

   return offset;
}

int
INGwFtPktMsg::createPacket(int asSize, char** apcData, int version)
{
   if(version != 1)
   {
      return 0;
   }

   if(*apcData == NULL)
   {
      *apcData = NEW_CHAR_ARY(SIZE_OF_BPMSG + asSize);
      memset(*apcData, 0, SIZE_OF_BPMSG + asSize);
   }

   int offset = 0;

   mMsgData.msMsgType = htons(mMsgData.msMsgType);
   memcpy(*apcData + offset, (void*)&mMsgData.msMsgType, SIZE_OF_SHORT);
   offset = offset + SIZE_OF_SHORT;

   mMsgData.msMsgType = ntohs(mMsgData.msMsgType);

   mMsgData.msSender = htons(mMsgData.msSender);
   memcpy(*apcData + offset, (void*)&mMsgData.msSender, SIZE_OF_SHORT);
   offset = offset + SIZE_OF_SHORT;
   mMsgData.msSender = ntohs(mMsgData.msSender);

   mMsgData.msReceiver = htons(mMsgData.msReceiver);
   memcpy(*apcData + offset, (void*)&mMsgData.msReceiver, SIZE_OF_SHORT);
   offset = offset + SIZE_OF_SHORT;
   mMsgData.msReceiver = ntohs(mMsgData.msReceiver);

   mMsgData.msMajorSeqNum = htons(mMsgData.msMajorSeqNum);
   memcpy(*apcData + offset, (void*)&mMsgData.msMajorSeqNum, SIZE_OF_SHORT);
   offset = offset + SIZE_OF_SHORT;
   mMsgData.msMajorSeqNum = ntohs(mMsgData.msMajorSeqNum);

   mMsgData.msMinorSeqNum = htons(mMsgData.msMinorSeqNum);
   memcpy(*apcData + offset, (void*)&mMsgData.msMinorSeqNum, SIZE_OF_SHORT);
   offset = offset + SIZE_OF_SHORT;
   mMsgData.msMinorSeqNum = ntohs(mMsgData.msMinorSeqNum);

   mMsgData.mlMsgGenTimeStamp = htonl(mMsgData.mlMsgGenTimeStamp);
   memcpy(*apcData + offset, (void*)&mMsgData.mlMsgGenTimeStamp, SIZE_OF_LONG);
   offset = offset + SIZE_OF_LONG;
   mMsgData.mlMsgGenTimeStamp = ntohl(mMsgData.mlMsgGenTimeStamp);

   mMsgData.miMsgStatus = htonl(mMsgData.miMsgStatus);
   memcpy(*apcData + offset, (void*)&mMsgData.miMsgStatus,   SIZE_OF_INT);
   offset = offset + SIZE_OF_INT;
   mMsgData.miMsgStatus = ntohl(mMsgData.miMsgStatus);

   // NOTE - The return value would be the same as SIZE_OF_BPMSG
   return offset;
}

int
INGwFtPktMsg::bcreatePacket(int asSize, char** apcData, int version)
{
   if(version != 1)
   {
      return 0;
   }

   if(*apcData == NULL)
   {
      *apcData = NEW_CHAR_ARY(SIZE_OF_BPMSG + asSize);
      memset(*apcData, 0, SIZE_OF_BPMSG + asSize);
   }

    int offset = 0;

    mMsgData.msMsgType = htons(mMsgData.msMsgType);
    memcpy(*apcData + offset, (void*)&mMsgData.msMsgType, SIZE_OF_SHORT);
    offset = offset + SIZE_OF_SHORT;
    mMsgData.msMsgType = ntohs(mMsgData.msMsgType);

    mMsgData.msSender = htons(mMsgData.msSender);
    memcpy(*apcData + offset, (void*)&mMsgData.msSender, SIZE_OF_SHORT);
    offset = offset + SIZE_OF_SHORT;
    mMsgData.msSender = ntohs(mMsgData.msSender);

    mMsgData.msReceiver = htons(mMsgData.msReceiver);
    memcpy(*apcData + offset, (void*)&mMsgData.msReceiver, SIZE_OF_SHORT);
    offset = offset + SIZE_OF_SHORT;
    mMsgData.msReceiver = ntohs(mMsgData.msReceiver);

    mMsgData.msMajorSeqNum = htons(mMsgData.msMajorSeqNum);
    memcpy(*apcData + offset, (void*)&mMsgData.msMajorSeqNum, SIZE_OF_SHORT);
    offset = offset + SIZE_OF_SHORT;
    mMsgData.msMajorSeqNum = ntohs(mMsgData.msMajorSeqNum);

    mMsgData.msMinorSeqNum = htons(mMsgData.msMinorSeqNum);
    memcpy(*apcData + offset, (void*)&mMsgData.msMinorSeqNum, SIZE_OF_SHORT);
    offset = offset + SIZE_OF_SHORT;
    mMsgData.msMinorSeqNum = ntohs(mMsgData.msMinorSeqNum);

    mMsgData.mlMsgGenTimeStamp = htonl(mMsgData.mlMsgGenTimeStamp);
    memcpy(*apcData + offset, (void*)&mMsgData.mlMsgGenTimeStamp, SIZE_OF_LONG);
    offset = offset + SIZE_OF_LONG;
    mMsgData.mlMsgGenTimeStamp = ntohl(mMsgData.mlMsgGenTimeStamp);

    mMsgData.miMsgStatus = htonl(mMsgData.miMsgStatus);
    memcpy(*apcData + offset, (void*)&mMsgData.miMsgStatus,   SIZE_OF_INT);
    offset = offset + SIZE_OF_INT;
    mMsgData.miMsgStatus = ntohl(mMsgData.miMsgStatus);

    // NOTE - The return value would be the same as SIZE_OF_BPMSG
    return offset;
}

// EOF INGwFtPktMsg.C

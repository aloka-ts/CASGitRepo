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
//     File:     INGwFtPktCallDataMsg.h
//
//     Desc:     Base class for replication msg
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal   23/11/07     Initial Creation
//********************************************************************

#ifndef INGW_FT_PKT_CALL_DATA_MSG_H_
#define INGW_FT_PKT_CALL_DATA_MSG_H_

#include <INGwFtPacket/INGwFtPktMsg.h>

class INGwFtPktCallDataMsg : public INGwFtPktMsg
{
   public:

      INGwFtPktCallDataMsg(void);

      virtual ~INGwFtPktCallDataMsg();

      const std::string& getCallId(void) const;

      short getCCMdataOffset() const;

      short getCCMDataLen() const;

      const char * INGwFtPktCallDataMsg::getCCMBuffer() const;

      int depacketize(const char* apcData, int asSize, int version);

      int packetize(char** apcData, int version);

      void initialize(char *msg, short length, const char *callID, 
                      short srcid, short destid);

      std::string toLog(void) const;

   protected:

      short    msCCMdataOffset;
      short    msCCMdataLen;
      std::string   mCallId;

   private:

      char *mInternalBuf;
      int  buflen;

   private:

      INGwFtPktCallDataMsg& operator= (const INGwFtPktCallDataMsg& arSelf);
      INGwFtPktCallDataMsg(const INGwFtPktCallDataMsg& arSelf);
};

#endif


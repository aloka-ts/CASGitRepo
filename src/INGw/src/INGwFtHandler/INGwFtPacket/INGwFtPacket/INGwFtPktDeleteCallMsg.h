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
//     File:     INGwFtPktDeleteCallMsg.h
//
//     Desc:     
//              
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal  23/11/07     Initial Creation
//********************************************************************

#ifndef INGW_FT_PKT_DELETE_CALL_MSG_H_
#define INGW_FT_PKT_DELETE_CALL_MSG_H_

#include <stdlib.h>

#include <INGwFtPacket/INGwFtPktMsg.h>

class INGwFtPktDeleteCallMsg : public INGwFtPktMsg
{
   public:

      INGwFtPktDeleteCallMsg(void);
      virtual ~INGwFtPktDeleteCallMsg();

      const std::string& getCallId(void);

      int depacketize(const char* deleteCallData, int asSize, int version);
      int packetize(char** apcData, int version);

      void initialize(const char* callid, short srcid,short  destid);

      std::string toLog(void) const;

   protected:

      std::string mCallId;

   private:

      INGwFtPktDeleteCallMsg& operator= (const INGwFtPktDeleteCallMsg& arSelf);
      INGwFtPktDeleteCallMsg(const INGwFtPktDeleteCallMsg& arSelf);
};

#endif // INGW_FT_PKT_DELETE_CALL_MSG_H_


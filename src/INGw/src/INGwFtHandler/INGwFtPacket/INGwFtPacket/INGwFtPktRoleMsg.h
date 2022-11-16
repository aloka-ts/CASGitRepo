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
//     File:    INGwFtPktRoleMsg.h
//
//     Desc:     
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal   23/11/07     Initial Creation
//********************************************************************

#ifndef INGW_FT_PKT_ROLE_MSG_H_
#define INGW_FT_PKT_ROLE_MSG_H_

#include <INGwFtPacket/INGwFtPktMsg.h>

const short ACTIVE_ROLE_MSG_TYPE = 1;
const short ACTIVE_PENDING_ROLE_MSG_TYPE = 2;
const short ACTIVE_ROLE_ACK_MSG_TYPE = 3;

class INGwFtPktRoleMsg : public INGwFtPktMsg
{
   public:

      INGwFtPktRoleMsg(void);

      virtual ~INGwFtPktRoleMsg();

      int depacketize(const char* apcData, int asSize, int version);

      int packetize(char** apcData, int version);

      void initialize(short p_RoleMsgType,
                      short srcid, short destid);

      std::string toLog(void) const;

			short getRoleMsgType();

   protected:
			short m_RoleMsgType;


   private:

      INGwFtPktRoleMsg& operator= (const INGwFtPktRoleMsg& arSelf);
      INGwFtPktRoleMsg(const INGwFtPktRoleMsg& arSelf);
};

#endif // INGW_FT_PKT_ROLE_MSG_H_


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
//     File:    INGwFtPktStackConfig.h
//
//     Desc:     
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal   23/11/07     Initial Creation
//********************************************************************

#ifndef INGW_FT_PKT_STACK_CONFIG_H_
#define INGW_FT_PKT_STACK_CONFIG_H_

#include <INGwFtPacket/INGwFtPktMsg.h>

const short STACK_CONFIG_INITIATED = 1;
const short STACK_CONFIG_END = 2;

class INGwFtPktStackConfig : public INGwFtPktMsg
{
   public:

      INGwFtPktStackConfig(void);

      virtual ~INGwFtPktStackConfig();

      int depacketize(const char* apcData, int asSize, int version);

      int packetize(char** apcData, int version);

      void initialize(short p_StackConfigType,
                      short srcid, short destid);

      std::string toLog(void) const;

			short getStackConfigType();

   protected:
			short m_StackConfigType;


   private:

      INGwFtPktStackConfig& operator= (const INGwFtPktStackConfig& arSelf);
      INGwFtPktStackConfig(const INGwFtPktStackConfig& arSelf);
};

#endif // INGW_FT_PKT_ROLE_MSG_H_


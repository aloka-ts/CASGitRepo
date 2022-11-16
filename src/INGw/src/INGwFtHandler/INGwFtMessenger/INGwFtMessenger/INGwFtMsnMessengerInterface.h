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
//     File:     INGwFtMsnMessengerInterface.h
//
//     Desc:
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     pankaj Bathwal  26/11/07     Initial Creation
//********************************************************************

#ifndef INGW_FT_MSN_MESSENGER_INF_H_
#define INGW_FT_MSN_MESSENGER_INF_H_

class INGwFtPktMsg;

class INGwFtMsnMessengerInterface
{
   public:

      virtual int addINGW(int p_INGwId) = 0;
      virtual int removeINGW(int p_INGwId) = 0;

      virtual void sendMsgToINGW(INGwFtPktMsg *p_Msg) = 0;

      virtual void sendAction(unsigned int peerID, unsigned int actionID) = 0;
};

#endif

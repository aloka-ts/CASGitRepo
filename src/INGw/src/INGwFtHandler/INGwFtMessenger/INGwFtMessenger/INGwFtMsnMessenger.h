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
//     File:     INGwFtMsnMessenger.h
//
//     Desc:
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     pankaj Bathwal  26/11/07     Initial Creation
//********************************************************************

#ifndef INGW_FT_MSN_MESSENGER_H__
#define INGW_FT_MSN_MESSENGER_H__

#include <INGwFtMessenger/INGwFtMsnMessengerInterface.h>

//subcomp ID.

#define BT_CCM_CCM_MESSENGER  30
#define BT_SLEE_CCM_MESSENGER 10

#define BT_SLEE_EVT_SUBCOMP 10
#define BT_SLEE_ACK_SUBCOMP 20

class INGwFtPktMsg;
class INGwIfrMgrFtIface;
class INGwFtMsnCommMgr;
class INGwFtMsnFaultMgr;

class INGwFtMsnMessenger : public INGwFtMsnMessengerInterface
{
   private:

      INGwFtMsnCommMgr   *_ingwFtCommManager;
      INGwFtMsnFaultMgr  *_ingwFaultManager;

      INGwIfrMgrFtIface  *_ingwFTInterface;

   public:

      INGwFtMsnMessenger(INGwIfrMgrFtIface *);
      ~INGwFtMsnMessenger();

      int addINGW(int p_INGwId);
      int removeINGW(int p_INGwId);

      void sendMsgToINGW(INGwFtPktMsg *msg);

      void shutdown();

      void sendAction(unsigned int actionId, unsigned int subsysID);
};

#endif

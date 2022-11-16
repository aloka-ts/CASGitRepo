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
//     File:     INGwFtMsnPktReceiver.h
//
//     Desc:
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     pankaj Bathwal  26/11/07     Initial Creation
//********************************************************************

#ifndef INGW_FT_MSN_PKT_RECEIVER_H_
#define INGW_FT_MSN_PKT_RECEIVER_H_

#include <INGwFtMessenger/INGwFtMsnMessengerThread.h>

#include <Util/ObjectId.h>

#if 0
#include <VersionMgr/VersionMgr.h>
#endif

class INGwFtMsnCommMgr;
class INGwIfrMgrFtIface;

class INGwFtMsnPktReceiver: public virtual INGwFtMsnMessengerThread
{
   private:

      INGwFtTkReceiverQueue *_recvQueue;
  		INGwFtMsnCommMgr  *_ingwCommMgr;
      INGwIfrMgrFtIface *_ingwFtInf;

#if 0
      RSI_NSP_CCM::VersionHolder *_verHolder;
#endif
   public:

      INGwFtMsnPktReceiver(INGwFtMsnCommMgr *ingwCommMgr, INGwIfrMgrFtIface *ingwFtInf);
      virtual ~INGwFtMsnPktReceiver();

   protected:

      virtual void execute(void);

   private:
      
      void _processRecvMsg(char *msg, int len, ObjectId &senderID, int version);
};

#endif

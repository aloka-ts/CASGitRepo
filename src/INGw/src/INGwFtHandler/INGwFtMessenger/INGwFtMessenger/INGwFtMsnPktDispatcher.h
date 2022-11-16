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
//     File:     INGwFtMsnPktDispatcher.h
//
//     Desc:
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     pankaj Bathwal  26/11/07     Initial Creation
//********************************************************************

#ifndef INGW_FT_MSN_PKT_DISPATCHER_H_
#define INGW_FT_MSN_PKT_DISPATCHER_H_

#include <INGwFtTalk/INGwFtTkInterface.h>
#include <INGwFtMessenger/INGwFtMsnMessengerThread.h>

class INGwFtMsnCommMgr;
class INGwIfrMgrFtIface;
class INGwFtPktMsg;

class INGwFtMsnPktDispatcher : public INGwFtMsnMessengerThread, 
                              public INGwFtTkConnHandlerInf
{
   private:

      int              _peerId;
      INGwFtMsnCommMgr  *_ingwCommMgr;
      INGwIfrMgrFtIface *_ingwFtInf;
      INGwFtTkConnHandle    *_connHandle;
      bool             _errorFlag;

      int              _sendVersion;

   public:

      INGwFtMsnPktDispatcher(INGwFtMsnCommMgr *ingwCommMgr, INGwIfrMgrFtIface *ingwFtInf,
                            int aPeerId);
      ~INGwFtMsnPktDispatcher();

      void initConnection();

   protected:

      virtual void execute(void);

   private:

      static void cleanupUnprocessedMsg(QueueData data);

   public:

      void sendMsg(INGwFtPktMsg *msg);

   public:

      //Connection status handler.
      virtual void connectionUpdate(unsigned int peerID,
                                    INGwFtTkConnState::LinkState connState);
};

#endif

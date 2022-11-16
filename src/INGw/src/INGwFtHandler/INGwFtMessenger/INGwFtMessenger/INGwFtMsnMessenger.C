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
//     File:     INGwFtMsnMessenger.C
//
//     Desc:
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     pankaj Bathwal  26/11/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtMessenger");

#include <INGwFtMessenger/INGwFtMsnMessenger.h>
#include <INGwFtMessenger/INGwFtMsnCommMgr.h>
#include <INGwFtMessenger/INGwFtMsnFaultMgr.h>
#include <INGwInfraStreamManager/INGwIfrSmAppStreamer.h>

BpGenUtil::INGwIfrSmAppStreamer *ingwMsgStream = NULL;

INGwFtMsnMessenger::INGwFtMsnMessenger(INGwIfrMgrFtIface *p_ingwFtIface)
{
   _ingwFTInterface = p_ingwFtIface;

   const char *lpcDebugFile = getenv ("INGW_FT_DEBUG_FILE");

   if (lpcDebugFile)
   {
     ingwMsgStream  = new BpGenUtil::INGwIfrSmAppStreamer("INGW_INGW_Messages", lpcDebugFile);
   }
   else
   {
     ingwMsgStream  = new BpGenUtil::INGwIfrSmAppStreamer("INGW_INGW_Messages", "ingwDebug.out");
   }

   _ingwFaultManager = new INGwFtMsnFaultMgr(p_ingwFtIface);
   _ingwFtCommManager  = new INGwFtMsnCommMgr(this, p_ingwFtIface);
}

INGwFtMsnMessenger::~INGwFtMsnMessenger()
{
   delete _ingwFtCommManager;
   delete ingwMsgStream;
}

int INGwFtMsnMessenger::addINGW(int p_INGwId)
{
   return _ingwFtCommManager->add(p_INGwId);
}

int INGwFtMsnMessenger::removeINGW(int p_INGwId)
{
   return _ingwFtCommManager->remove(p_INGwId);
}

void INGwFtMsnMessenger::sendMsgToINGW(INGwFtPktMsg *p_Msg)
{
   if(p_Msg == NULL)
   {
      return;
   }

   _ingwFtCommManager->sendMsg(p_Msg);
   return;
}

void INGwFtMsnMessenger::shutdown()
{
   _ingwFtCommManager->shutdown();
}

void INGwFtMsnMessenger::sendAction(unsigned int p_PeerID, unsigned int p_ActionID)
{
   INGwFtTkInterface::instance()->sendAction(p_PeerID, p_ActionID);
}


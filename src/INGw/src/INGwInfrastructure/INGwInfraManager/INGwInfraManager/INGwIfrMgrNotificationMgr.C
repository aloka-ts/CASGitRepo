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
//     File:     INGwIfrMgrNotificationMgr.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal   27/11/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraManager");



#include <INGwInfraManager/INGwIfrMgrNotificationMgr.h>
//#include <ccm/BpCCM.h>
//#include <ccm/BpCCMThreadMgr.h>
//#include <ccm/BpWorkUnit.h>
//#include <ccm/BpCallController.h>

namespace RSI_NSP_CCM
{
void INGwIfrMgrNotificationMgr::notify(Notification notification, ReasonCode reason)
{
//PANKAJ 
// to do
/*
   logger.logMsg(ALWAYS_FLAG, 0, "Received notification [%d] reason [%d]",
                 notification, reason);
   BpWorkUnit *wrkUnit = new BpWorkUnit();

   wrkUnit->meWorkType = BpWorkUnit::COMPONENT_NOTIFICATION;
   wrkUnit->mlHashId = 0;
   wrkUnit->mpcCallId = NULL;
   wrkUnit->mpMsg = NULL;
   wrkUnit->muiTimerId = (int)notification;
   wrkUnit->mpContextData = (void *)reason;
   wrkUnit->mpWorkerClbk = BpCCM::getInstance().getCallController();

   BpCCMThreadMgr::getInstance().postMsgForHK(wrkUnit);
*/
}
};

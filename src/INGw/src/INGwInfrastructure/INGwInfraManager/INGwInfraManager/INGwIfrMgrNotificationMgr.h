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



#ifndef _INGW_IFR_MGR_NOTIFICATION_MGR_H_
#define _INGW_IFR_MGR_NOTIFICATION_MGR_H_

#include <INGwInfraUtil/INGwIfrUtlSingleton.h>
namespace RSI_NSP_CCM
{

class INGwIfrMgrNotificationMgr : public INGwIfrUtlSingleton<INGwIfrMgrNotificationMgr>
{
   public:

      enum Notification
      {
         SLEE_AVAILABLE = 1,
         SLEE_NOT_AVAILABLE,
         BILLING_AVAILABLE,
         BILLING_NOT_AVAILABLE
      };

      enum ReasonCode
      {
         NO_REASON = 0,
         PROCESS_DOWN,
         REASON_DB_DOWN,
         SERV_INST_THRESHOLD,
         NO_CDR_DIR_AVAILABLE
      };

   public:

      void notify(Notification, ReasonCode);
};

};

#endif

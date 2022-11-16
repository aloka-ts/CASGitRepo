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
//     File:     INGwIfrMgrAlarmInfoEntry.h
//
//     Desc:
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     pankaj Bathwal  26/11/07     Initial Creation
//********************************************************************

#ifndef _INGW_IFR_MGR_ALARM_INFO_ENTRY_H_
#define _INGW_IFR_MGR_ALARM_INFO_ENTRY_H_

#include <INGwInfraManager/INGwIfrMgrAlarmInfo.h>

#define INIT_ALARM_INFO_ARRAY_SIZE 5

class INGwIfrMgrAlarmInfoEntry
{
   public :

      INGwIfrMgrAlarmInfoEntry() 
      { 
         mGappingDuration = 30;
         mAlarmInfoCurrCount = 0;
      }

      unsigned long           mGappingDuration;

      int                     mAlarmInfoCurrCount;
      INGwIfrMgrAlarmInfo     mAlarmInfo[INIT_ALARM_INFO_ARRAY_SIZE];
};

#endif //_INGW_IFR_MGR_ALARM_INFO_ENTRY_H_

// EOF BpAlarmInfoEntry.h

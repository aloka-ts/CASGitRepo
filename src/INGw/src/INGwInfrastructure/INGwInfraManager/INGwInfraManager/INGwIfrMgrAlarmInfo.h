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
//     File:     INGwIfrMgrAlarmInfo.h
//
//     Desc:
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     pankaj Bathwal  26/11/07     Initial Creation
//********************************************************************

#ifndef _INGW_IFR_MGR_ALARM_INFO_H_
#define _INGW_IFR_MGR_ALARM_INFO_H_

#define SIZE_OF_ENTITY_ID 100
#define SIZE_OF_ALARM_MSG 100

class INGwIfrMgrAlarmInfo
{
    public :

        typedef unsigned long INGwIfrMgrAlarmSeverity;

        INGwIfrMgrAlarmInfo() { 
            memset(mEntityId, 0, SIZE_OF_ENTITY_ID + 1);
            mAlarmCountSinceLastAlarmGen = 0;
            mTimeStampForLastAlarmSent = 0;
        }

        char            mEntityId[SIZE_OF_ENTITY_ID + 1];
        unsigned long   mAlarmCountSinceLastAlarmGen;
        unsigned long   mTimeStampForLastAlarmSent;
};

#endif // _INGW_IFR_MGR_ALARM_INFO_H_

// EOF INGwIfrMgrAlarmInfo.h

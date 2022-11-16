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
//     File:     INGwIfrMgrAgentClbkImpl.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya   07/12/07     Initial Creation
//********************************************************************
#ifndef INGW_INFRA_SM_COMMON_H_
#define INGW_INFRA_SM_COMMON_H_

// When the threshold of an instantaneously processable parameter is
// exceeded, the updating function is returned an indication to this
// effect.  This enum indicates the exact nature of the excess, and
// can be used by the calling function or module to take any additional
// action.
typedef enum
{
  NoThresholdExceeded,
  UpperThresholdExceeded,
  LowerThresholdExceeded
} ThresholdIndication;

// This enum specifies the different types of thresholds possible.
typedef enum
{
  Thresh_Absolute,
  Thresh_TimeDiff,
  Thresh_Percent,
  Thresh_TimeDiff_Percent
} ThresholdType;

// This enum specifies the action to be taken if the threshold exceeds.
typedef enum
{
  ThreshAct_Log,
  ThreshAct_Alarm
} ThresholdAction;

#endif

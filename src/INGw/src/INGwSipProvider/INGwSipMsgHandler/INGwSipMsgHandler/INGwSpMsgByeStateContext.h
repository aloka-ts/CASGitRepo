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
//     File:     INGwSpMsgByeStateContext.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef INGW_SP_MSG_BYE_STATE_CONTEXT_H_
#define INGW_SP_MSG_BYE_STATE_CONTEXT_H_

#include <string>

#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpSipContext.h>

struct INGwSpMsgByeStateContext
{
  INGwSpMsgByeStateContext();
  ~INGwSpMsgByeStateContext();
  void reset(void);

  std::string toLog()const;

  bool mbByeReceived; // Set to true when a BYE is received.
  bool mbByeCompleted; 
  bool isFailedByePending;
  bool isByeAlso;
  std::string mAccountCode;
  INGwSipEPInfo* byeAlsoContactInfo;
}; // End of INGwSpMsgByeStateContext

#endif //INGW_SP_MSG_BYE_STATE_CONTEXT_H_

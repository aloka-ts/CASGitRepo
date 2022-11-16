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
//     File:     INGwSpMsgByeStateContext.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipMsgHandler");

#include <INGwSipMsgHandler/INGwSpMsgByeStateContext.h>
#include <INGwSipProvider/INGwSpSipCommon.h>

using namespace std;


INGwSpMsgByeStateContext::INGwSpMsgByeStateContext()
{
   byeAlsoContactInfo = NULL;
   reset();
}

INGwSpMsgByeStateContext::~INGwSpMsgByeStateContext()
{
   reset();
}

void INGwSpMsgByeStateContext::reset(void)
{
   mbByeReceived      = false;
   mbByeCompleted     = false;
   isFailedByePending = false;
   isByeAlso          = false;
   mAccountCode.clear();

   if(byeAlsoContactInfo != NULL)
   {
      delete byeAlsoContactInfo;
   }

   byeAlsoContactInfo = NULL;
}

std::string INGwSpMsgByeStateContext::toLog() const
{
   std::string ret = "Bye Context\n";

   ret += "Account code:";
   ret += mAccountCode;
   ret += "\n";
   char local[500];
   sprintf(local, "ByeReceived:[%d] ByeCompleted:[%d] FailedByePending:[%d]\n"
                  "ByeAlso:[%d]\n", 
           mbByeReceived, mbByeCompleted, isFailedByePending, isByeAlso);

   ret += local;
   return ret;
}

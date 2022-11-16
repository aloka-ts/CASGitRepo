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
//     File:     INGwIfrUtlMachPing.h
//
//     Desc:     Utility used to ping the machine availability.
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#ifndef __BpMach_PING_H__
#define __BpMach_PING_H__

#include <INGwInfraUtil/INGwIfrUtlSingleton.h>

namespace RSI_NSP_CCM
{

class INGwIfrUtlMachPing : public INGwIfrUtlSingleton<INGwIfrUtlMachPing>
{
   public:

      enum INGwIfrUtlMachStatus
      {
         AVAILABLE = 1,
         NOT_AVAILABLE
      };

   private:

      void _setOptions(int sockID);

   public:

      INGwIfrUtlMachStatus getStatus(unsigned int IP, int timeout);
};

};

#endif

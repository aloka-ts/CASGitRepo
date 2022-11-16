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
//     File:     INGwSpSipCallTable.h
//
//     Desc:
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef BPSIP_CALLTABLE_H
#define BPSIP_CALLTABLE_H

//#include <map>
#include <INGwSipProvider/INGwSpHash.h>

#define BP_SIP_NO_OF_HASH_BUCKETS_HSSCALLMAP  200000
#define BP_SIP_MAX_HSS_CALLOBJECTS            200000


#include <INGwSipProvider/INGwSpSipIncludes.h>

typedef Sdf_st_hash SipCallTable;

class INGwSpSipCallTable 
{
   public:

      INGwSpSipCallTable();

      ~INGwSpSipCallTable();

      int getCount() 
      {
         return count;
      }

      bool get(const char *apStr, Sdf_st_callObject** appCallObject);
      bool put(const char *apStr, int aiLen, Sdf_st_callObject* apCallObject);
      bool remove(const char *apStr, Sdf_st_callObject** appCallObject);

   private:

      int count;
      SipCallTable* mpCallTable;
}; 

#endif

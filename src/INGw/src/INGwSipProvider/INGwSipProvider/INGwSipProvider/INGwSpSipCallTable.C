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
//     File:     INGwSpSipCallTable.C
//
//     Desc:
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipProvider");

#include <INGwSipProvider/INGwSpSipCallTable.h>
#include <INGwSipProvider/INGwSpSipCommon.h>

using namespace std;

extern "C" 
{
   SIP_U8bit callTableKeyCompare(void *pKey1, void *pKey2) 
   {
      return Sdf_mc_strcmp((char*)pKey2, (char*)pKey1);
   }

   void callTableFreeElement(void *pElement) 
   {
   }

   void callTableFreeKey(void *pTimerKey) 
   {
      LogINGwTrace(false, 0, "IN callTableTimerKeyFree");

      SipError error;

      fast_memfree(0, (char*) pTimerKey, &error);

      LogINGwTrace(false, 0, "OUT callTableTimerKeyFree");
   }
}

INGwSpSipCallTable::INGwSpSipCallTable() 
{
   LogINGwTrace(false, 0, "IN INGwSpSipCallTable");

   count = 0;

   Sdf_st_error error;

   mpCallTable = (Sdf_st_hash *) sdf_memget (0, sizeof(Sdf_st_hash), &error);

   Sdf_ty_retVal l_retVal = bp_ivk_uaHashInit(mpCallTable, bp_ivk_uaElfHash, 
                                              callTableKeyCompare, 
                                              callTableFreeElement, 
                                              callTableFreeKey, 
                                           BP_SIP_NO_OF_HASH_BUCKETS_HSSCALLMAP,
                                              BP_SIP_MAX_HSS_CALLOBJECTS, 
                                              &error);

   if(Sdf_co_fail == l_retVal) 
   {
      LogINGwError(false, 0, "bp_ivk_uaHashInit FAILED");  
   }

   LogINGwTrace(false, 0, "OUT INGwSpSipCallTable");
}

INGwSpSipCallTable::~INGwSpSipCallTable() 
{
}

bool INGwSpSipCallTable::get(const char *apStr, Sdf_st_callObject** appCallObject) 
{
   logger.logINGwMsg(false, TRACE_FLAG, 0, "IN get [%s]", apStr);

   *appCallObject = (Sdf_st_callObject*)
   bp_ivk_uaHashFetch(mpCallTable, (void*)apStr); 

   if(0 != *appCallObject) 
   {
      LogINGwTrace(false, 0, "OUT get");
      return true;
   }

   LogINGwTrace(false, 0, "OUT get");
   return false;
}

bool INGwSpSipCallTable::put(const char *apStr, int aiLen, 
                         Sdf_st_callObject* apCallObject) 
{
   logger.logINGwMsg(false, TRACE_FLAG, 0, "IN put [%s]", apStr);

   SipError error;

   char* l_key = (char*) fast_memget(0, aiLen + 1, &error);
   strcpy(l_key, apStr);

   if(bp_ivk_uaHashAdd(mpCallTable, (void*)apCallObject, (void*)l_key) == 
      Sdf_co_success) 
   {
      ++count;
      LogINGwTrace(false, 0, "OUT put");
      return true;
   }

   fast_memfree(0, l_key, &error);
   LogINGwTrace(false, 0, "OUT put");
   return false; 
}

bool INGwSpSipCallTable::remove(const char *apStr, 
                            Sdf_st_callObject** appCallObject) 
{
   logger.logINGwMsg(false, TRACE_FLAG, 0, "IN remove [%s]", apStr);

   if(bp_ivk_uaHashRemove(mpCallTable, (void*)apStr, (void**)appCallObject) == 
      Sdf_co_success) 
   {
      --count;
      LogINGwTrace(false, 0, "OUT remove");
      return true;
   }

   LogINGwTrace(false, 0, "OUT remove");
   return false;
}

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
//     File:     INGwSpHash..C
//
//     Desc:
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipProvider");

#ifdef linux
#include <libelf.h>
#else
#include <libelf.h>
#endif

#include <INGwSipProvider/INGwSpHash.h>

using namespace std;

Sdf_ty_retVal bp_ivk_uaHashInit(Sdf_st_hash *pHash, Sdf_ty_hashFunc fpHashFunc, 
                                Sdf_ty_hashKeyCompareFunc fpCompareFunc, 
                                Sdf_ty_hashElementFreeFunc fpElemFreeFunc, 
                                Sdf_ty_hashKeyFreeFunc fpKeyFreeFunc, 
                                Sdf_ty_u32bit numBuckets, 
                                Sdf_ty_u32bit maxElements, Sdf_st_error *pErr)
{
   LogINGwTrace(false, 0, "IN bp_ivk_uaHashInit");

   Sdf_ty_u32bit i=0;
   Sdf_st_error dError;

   /* Initialize structure variables */
   pHash->fpHashFunc = fpHashFunc;
   pHash->fpCompareFunc = fpCompareFunc;
   pHash->numberOfBuckets = numBuckets;
   pHash->numberOfElements = 0;
   pHash->maxNumberOfElements = maxElements;
   pHash->fpElementFreeFunc = fpElemFreeFunc;
   pHash->fpKeyFreeFunc = fpKeyFreeFunc;
   
   /* Allocate space for buckets */
   pHash->ppHashChains = (Sdf_st_hashElement **) \
   sdf_memget(0, sizeof(Sdf_st_hashElement*)*numBuckets,\
         &dError);
   if (pHash->ppHashChains == Sdf_co_null)
   {
      pErr->errCode = Sdf_en_noMemoryError;

        LogINGwTrace(false, 0, "OUT bp_ivk_uaHashInit");
      return Sdf_co_fail;
   }

   /* Initialize the buckets. */
   for(i=0; i<numBuckets; i++)
   {
      pHash->ppHashChains[i] = Sdf_co_null;
   }

    LogINGwTrace(false, 0, "OUT bp_ivk_uaHashInit");
   return Sdf_co_success;
}


/******************************************************************************
 ** FUNCTION:       bp_ivk_uaHashAdd
 ** 
 ** DESCRIPTION:    This is the function to add an entry
 **               into the hash table.
 **
 ******************************************************************************/
Sdf_ty_retVal 
bp_ivk_uaHashAdd
   (Sdf_st_hash *pHash, Sdf_ty_pvoid pElement, Sdf_ty_pvoid pKey)
{
   Sdf_ty_u32bit hashKey=0;
   Sdf_ty_u32bit bucket=0;
   Sdf_st_hashElement* pNewElement = Sdf_co_null;
   Sdf_st_hashElement* pIterator = Sdf_co_null;
   Sdf_st_error dError;

    LogINGwTrace(false, 0, "IN bp_ivk_uaHashAdd");

   /* Check if the max number of Elements is getting exceeded */
   if (pHash->numberOfElements == (pHash->maxNumberOfElements)) {
        LogINGwTrace(false, 0, "OUT bp_ivk_uaHashAdd");
      return Sdf_co_fail;
    }

   /* Compute hash for the element */
   hashKey = pHash->fpHashFunc(pKey);

   /* Locate the bucket */
   bucket = hashKey % pHash->numberOfBuckets;
   
   /* Ensure that the key is not already present */
   pIterator = pHash->ppHashChains[bucket];
   while(pIterator != Sdf_co_null)
   {
      if(pHash->fpCompareFunc(pIterator->pKey, pKey) == 0)
      {
         /* The key already exists. Return failure here */
            LogINGwTrace(false, 0, "OUT bp_ivk_uaHashAdd");
         return Sdf_co_fail;
      }
      pIterator = pIterator->pNext;
   }

   /* Allocate and initialize element holder */
   pNewElement = (Sdf_st_hashElement *)\
   sdf_memget(0, sizeof(Sdf_st_hashElement), &dError);
   
   if (pNewElement == Sdf_co_null)
   {
        LogINGwTrace(false, 0, "OUT bp_ivk_uaHashAdd");
      return Sdf_co_fail;
   }

   pNewElement->pElement = pElement;
   pNewElement->pKey = pKey;
   pNewElement->dRemove = Sdf_co_false;
   //HSS_INITREF(pNewElement->dRefCount);
    pNewElement->dRefCount.ref = 1;

   /* Push element into the bucket */
   pNewElement->pNext = pHash->ppHashChains[bucket];
   pHash->ppHashChains[bucket] = pNewElement;
   pHash->numberOfElements++;

    LogINGwTrace(false, 0, "OUT bp_ivk_uaHashAdd");
   return Sdf_co_success;
}


/******************************************************************************
 ** FUNCTION:       bp_ivk_uaHashFetch
 ** 
 ** DESCRIPTION:    This is the function to fetch an entry
 **               from the hash table.
 **
 ******************************************************************************/
Sdf_ty_pvoid 
bp_ivk_uaHashFetch
   (Sdf_st_hash *pHash, Sdf_ty_pvoid pKey)
{
    LogINGwTrace(false, 0, "IN bp_ivk_uaHashFetch");

   Sdf_ty_u32bit hashKey=0;
   Sdf_ty_u32bit bucket=0;
   Sdf_st_hashElement* pIterator = Sdf_co_null;

   /* Compute hash for the element */
   hashKey = pHash->fpHashFunc(pKey);
   
   /* Locate the bucket */
   bucket = hashKey % pHash->numberOfBuckets;
   
   /* Go through chain */
   pIterator = pHash->ppHashChains[bucket];
   
   while(pIterator != Sdf_co_null)
   {
      if(pHash->fpCompareFunc(pIterator->pKey, pKey) == 0)
      {
         break;
      }
      pIterator = pIterator->pNext;
   }
   
    LogINGwTrace(false, 0, "OUT bp_ivk_uaHashFetch");

   if (pIterator == Sdf_co_null)
       return Sdf_co_null;
   else
      return pIterator->pElement;
}


/******************************************************************************
 ** FUNCTION:       bp_ivk_uaHashRemove
 ** 
 ** DESCRIPTION:    This is the function to remove an entry
 **               from the hash table.
 **
 **               Note:
 **               -----
 **               This function should not be invoked if memory does not 
 **               have to be freed at time of invocation. 
 **               bp_ivk_uaHashRelease( ) should be called instead.
 **
 ******************************************************************************/
Sdf_ty_retVal bp_ivk_uaHashRemove1
   (Sdf_st_hash *pHash, Sdf_ty_pvoid pKey, void** pObject)
{

    LogINGwTrace(false, 0, "IN bp_ivk_uaHashRemove");
   
    *pObject = 0;

   Sdf_st_hashElement *pTempElement = Sdf_co_null;
   Sdf_ty_u32bit hashKey=0;
   Sdf_ty_u32bit bucket=0;
   Sdf_st_hashElement **ppIterator = Sdf_co_null;
   Sdf_st_error dError;

   /* Compute hash for the element */
   hashKey = pHash->fpHashFunc(pKey);

   /* Locate the bucket */
   bucket = hashKey % pHash->numberOfBuckets;

   /* Go through chain */
   ppIterator = &(pHash->ppHashChains[bucket]);

   while(*ppIterator != Sdf_co_null)
   {
      if(pHash->fpCompareFunc((*ppIterator)->pKey, pKey) == 0)
         break;
      ppIterator = &((*ppIterator)->pNext);
   }
   if(*ppIterator == Sdf_co_null)
   {
      return Sdf_co_fail;
   }

   /*    check if this hash entry is in use.
      If so just set the remove flag and return */

   //HSS_LOCKREF((*ppIterator)->dRefCount);

   HSS_DECREF((*ppIterator)->dRefCount);
   if (HSS_CHECKREF((*ppIterator)->dRefCount))
   {
      pTempElement = *ppIterator;
        *pObject = (Sdf_st_callObject*) pTempElement->pElement;
      *ppIterator = (*ppIterator)->pNext;
      if (pHash->fpElementFreeFunc != Sdf_co_null)
         pHash->fpElementFreeFunc(pTempElement->pElement);
      if (pHash->fpKeyFreeFunc != Sdf_co_null)
         pHash->fpKeyFreeFunc(pTempElement->pKey);

      //HSS_UNLOCKREF(pTempElement->dRefCount);
      //HSS_DELETEREF(pTempElement->dRefCount);

      sdf_memfree(0,(Sdf_ty_pvoid *)&pTempElement,&dError);
      pHash->numberOfElements--;
   }
   else
   {
      //ERROR - object nor deleted
      LogINGwError(false, 0, "bp_ivk_uaHashRemove, object not removed");
      LogINGwTrace(false, 0, "OUT bp_ivk_uaHashRemove");
      return Sdf_co_fail;
   }

    LogINGwTrace(false, 0, "OUT bp_ivk_uaHashRemove");
    return Sdf_co_success;
}


/******************************************************************************
 ** FUNCTION:       bp_ivk_uaElfHash
 **
 ** DESCRIPTION:    ELF hash function 
 **             Algorithm from Dr. Dobb's Journal
 **
 **
 ******************************************************************************/
Sdf_ty_u32bit bp_ivk_uaElfHash
(Sdf_ty_pvoid pName)
{
    LogINGwTrace(false, 0, "IN bp_ivk_uaElfHash");

   Sdf_ty_u32bit h = 0;
#ifdef LINUX
   const char *name = Sdf_co_null;
#else
   const char *name = Sdf_co_null;
#endif

   if (pName == Sdf_co_null) {
      return (Sdf_ty_u32bit)0;
    }
   
#ifdef LINUX
   name = static_cast<const char *>(pName);
#else
   name = static_cast<const char *>(pName);
#endif

    h = elf_hash(name);

    logger.logINGwMsg(false, TRACE_FLAG, 0,
												 "bp_ivk_uaElfHash : KEY <%s> Hash <%d>",
													name, h);
    /***************************
   while ( *name ) 
   { 
      h = ( h << 4 ) + *name++; 
      if ( (g = h & 0xF0000000) ) 
         h ^= g >> 24; 
      h &= ~g; 
   }
    ****************************/

    LogINGwTrace(false, 0, "OUT bp_ivk_uaElfHash");
   return h; 
}

Sdf_ty_u32bit 
bp_ivk_uaElfHashTmr
(Sdf_ty_pvoid pName)
{
    LogINGwTrace(false, 0, "IN bp_ivk_uaElfHashTmr");

    Sdf_ty_u32bit h = 0;
#ifdef LINUX
    const char *name = Sdf_co_null;
#else
    const char *name = Sdf_co_null;
#endif

    if (pName == Sdf_co_null) {
        return (Sdf_ty_u32bit)0;
    }
  
    SipTimerKey* l_timerKey = (SipTimerKey*) pName; 
#ifdef LINUX
    name = reinterpret_cast<const char *>(l_timerKey->dCallid);
#else
    name = reinterpret_cast<const char *>(l_timerKey->dCallid);
#endif

    h = elf_hash(name);
    logger.logINGwMsg(false, TRACE_FLAG, 0,
												 "bp_ivk_uaElfHashTmr : KEY <%s> Hash <%d>",
													name, h);
    /***************************
    while ( *name )
    {
        h = ( h << 4 ) + *name++;
        if ( (g = h & 0xF0000000) )
            h ^= g >> 24;
        h &= ~g;
    }
    ****************************/

    LogINGwTrace(false, 0, "OUT bp_ivk_uaElfHashTmr");
    return h;
}


/******************************************************************************
 ** FUNCTION:       bp_ivk_uaHashRemove
 ** 
 ** DESCRIPTION:    This is the function to remove an entry
 **               from the hash table.
 **
 **               Note:
 **               -----
 **               This function should not be invoked if memory does not 
 **               have to be freed at time of invocation. 
 **               bp_ivk_uaHashRelease( ) should be called instead.
 **
 ******************************************************************************/
Sdf_ty_retVal 
bp_ivk_uaHashRemove(Sdf_st_hash *pHash, 
  Sdf_ty_pvoid pKey, 
  void** pObject) {

    LogINGwTrace(false, 0, "IN bp_ivk_uaHashRemove");
  
    if(0 != pObject )  {
      *pObject = 0;
    }

   Sdf_st_hashElement *pTempElement = Sdf_co_null;
   Sdf_ty_u32bit hashKey=0;
   Sdf_ty_u32bit bucket=0;
   Sdf_st_hashElement **ppIterator = Sdf_co_null;
   Sdf_st_error dError;

   /* Compute hash for the element */
   hashKey = pHash->fpHashFunc(pKey);

   /* Locate the bucket */
   bucket = hashKey % pHash->numberOfBuckets;

   /* Go through chain */
   ppIterator = &(pHash->ppHashChains[bucket]);

   while(*ppIterator != Sdf_co_null)
   {
      if(pHash->fpCompareFunc((*ppIterator)->pKey, pKey) == 0)
         break;
      ppIterator = &((*ppIterator)->pNext);
   }
   if(*ppIterator == Sdf_co_null)
   {
      logger.logINGwMsg(false, VERBOSE_FLAG,0, "Out bp_ivk_uaHashRemove failed");
      return Sdf_co_fail;
   }

   /*    check if this hash entry is in use.
      If so just set the remove flag and return */

   //HSS_LOCKREF((*ppIterator)->dRefCount);

   HSS_DECREF((*ppIterator)->dRefCount);
   if (HSS_CHECKREF((*ppIterator)->dRefCount))
   {
      pTempElement = *ppIterator;
        if(0 != pObject) {
          *pObject = (INGwSpStackTimerContext*) pTempElement->pElement;
        }
      *ppIterator = (*ppIterator)->pNext;
      if (pHash->fpElementFreeFunc != Sdf_co_null)
         pHash->fpElementFreeFunc(pTempElement->pElement);
      if (pHash->fpKeyFreeFunc != Sdf_co_null)
         pHash->fpKeyFreeFunc(pTempElement->pKey);

      //HSS_UNLOCKREF(pTempElement->dRefCount);
      //HSS_DELETEREF(pTempElement->dRefCount);

      sdf_memfree(0,(Sdf_ty_pvoid *)&pTempElement,&dError);
      pHash->numberOfElements--;
   }
   else
   {
      //ERROR - object nor deleted
      LogINGwError(false, 0, "bp_ivk_uaHashRemove, object not removed");
      LogINGwTrace(false, 0, "OUT bp_ivk_uaHashRemove");
      return Sdf_co_fail;
   }

    LogINGwTrace(false, 0, "OUT bp_ivk_uaHashRemove");
    return Sdf_co_success;
}

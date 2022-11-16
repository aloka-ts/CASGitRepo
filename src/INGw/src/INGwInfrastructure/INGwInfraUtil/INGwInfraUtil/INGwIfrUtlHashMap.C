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
//     File:     INGwIfrUtlConfigurable.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraUtil");

#include <errno.h>
#include <sstream>

#include "INGwInfraUtil/INGwIfrUtlHashMap.h"

using namespace std;


const int HM_HIGH_WATER_MARK__DEF = 0;
const int HM_HIGH_WATER_MARK__MAX = 100000;
const int HM_HIGH_WATER_MARK__MIN = 0;
const int HM_LOW_WATER_MARK__DEF  = 0;
const int HM_LOW_WATER_MARK__MAX  = 100000;
const int HM_LOW_WATER_MARK__MIN  = 0;
const int HM_DEFAULT_BUCKET_COUNT = 997;


unsigned long elf_hash_func(const char* apcId)
{
    unsigned long h = 0, g=0;
    char* name = NULL;
    if (apcId == NULL) return 0;
   
    name = (char *)apcId;
    while ( *name )
    {
        h = ( h << 4 ) + *name++;
        if ( (g = h & 0xF0000000) )
            h ^= g >> 24;
        h &= ~g;
    }
    return h;
}

template <class TKey, class TValue>
TValue 
BpHashMap<TKey, TValue>::getValue(const TKey& arKey) {
    TValue t = NULL;
    typename BpBucket<TKey, TValue>::iterator it =  mBucket.find(arKey);
      if(mBucket.end() != it) {
        t = it->second;
    }
    return t;
}

template <class TKey, class TValue>
void 
BpHashMap<TKey, TValue>::addPair(const TKey& arKey, TValue aValue) {
    mBucket.insert(BpBucket<TKey, TValue>::value_type(arKey, aValue));
}

// EOF BpHashMap.C

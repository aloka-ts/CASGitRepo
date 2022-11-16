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
#ifndef _INGW_INFRA_UTIL_HASH_MAP_H_
#define _INGW_INFRA_UTIL_HASH_MAP_H_

#include <libelf.h>
#include <pthread.h>

#include <map>
#include <utility>
#include <memory>
#include <string>

#include "INGwInfraUtil/INGwIfrUtlBucket.h"

unsigned long elf_hash_func(const char* );

template <class TKey, class TValue>
class BpHashMap {

      INGwIfrUtlBucket<TKey, TValue> mBucket;

  public :

    TValue getValue(const TKey& arKey);
    void addPair(const TKey& arKey, TValue aValue);
};

class HString_Key {

    const char* mpcId;
    unsigned long mulId;

  public :

    HString_Key() : mpcId(NULL), mulId(0) { }
    // Anurag : porting
#ifdef linux
    HString_Key(const char* apcId) : mpcId(apcId), 
                         mulId(elf_hash((const char *)apcId)) { }
#else
    HString_Key(const char* apcId) : mpcId(apcId), mulId(elf_hash(apcId)) { }
#endif
    HString_Key(const char* apcId, unsigned long aulId) : mpcId(apcId), mulId(aulId) { }

    bool operator== (const HString_Key& aKey) const {
      bool retValue = false;
      if(mulId == aKey.mulId) {
        if(0 == strcmp(mpcId, aKey.mpcId)) {
          retValue = true;
        }
      }
      return retValue;
    }

    bool operator< (const HString_Key& aKey) const {
      return mulId < aKey.mulId;
    }

};


template <class T>
class Equal_to_HString_Key {

    HString_Key  key;

  public :

    Equal_to_HString_Key(const char* apcCallId) {
      key.mpcCallId = apcCallId;
      key.mulHashId = elf_hash(apcCallId);
    }

    // Anurag : porting
    //bool operator() (const const HString_Key& aKey) const {
    bool operator() (const HString_Key& aKey) const {
      return key == aKey;
    }

    bool operator() (const std::pair<const HString_Key, T>& aKeyValuePair) const {
      return key == aKeyValuePair.first;
    }
};

#endif 

// EOF BpHashMap.h

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

#include "INGwInfraUtil/INGwIfrUtlBucket.h"

using namespace std;

template<class TKey, class TValue>
void 
INGwIfrUtlBucket<TKey, TValue>::printAll(ostream& os) {
	// Anurag : porting
    // typename BpMapBucket<TKey, TValue>::iterator it = begin();
    typename INGwIfrUtlBucket<TKey, TValue>::iterator it = begin();
    for( ;it != end(); it++) {
        os << it->second;
	}
}

template<class Cont, class PtrMemFun>
void 
apply(Cont& c, PtrMemFun f, const char* apcData) {
     typename Cont::iterator it = c.begin();
     for( ;it != c.end(); it++) {
          ((it->second)->*f)(apcData);
     }
}

template<class Cont, class PtrMemFun>
void 
apply(Cont& c, PtrMemFun f) {
     typename Cont::iterator it = c.begin();
     for( ;it != c.end(); it++) {
          ((it->second)->*f)();
     }
}

template<class Cont>
void 
print(Cont& c, ostream& os) {
     typename Cont::iterator it = c.begin();
     for( ;it != c.end(); it++) {
          os << it->second;
     }
}

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
//     File:     INGwSpHash.h
//
//     Desc:
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************


#ifndef INGW_SP_HASH_H_
#define INGW_SP_HASH_H_

#include <INGwSipProvider/INGwSpSipIncludes.h>
#include <INGwSipProvider/INGwSpStackTimer.h>

//This is a temp fix for using the stack code

typedef Sdf_ty_uaHashFunc Sdf_ty_hashFunc;
typedef Sdf_ty_uaHashKeyCompareFunc Sdf_ty_hashKeyCompareFunc;
typedef Sdf_ty_uaHashElementFreeFunc Sdf_ty_hashElementFreeFunc;
typedef Sdf_ty_uaHashKeyFreeFunc Sdf_ty_hashKeyFreeFunc;

extern "C" {

Sdf_ty_retVal 
bp_ivk_uaHashInit(Sdf_st_hash *pHash,
  Sdf_ty_hashFunc fpHashFunc,
  Sdf_ty_hashKeyCompareFunc fpCompareFunc,
  Sdf_ty_hashElementFreeFunc fpElemFreeFunc,
  Sdf_ty_hashKeyFreeFunc fpKeyFreeFunc,
  Sdf_ty_u32bit numBuckets, Sdf_ty_u32bit maxElements,
  Sdf_st_error *pErr);

Sdf_ty_retVal 
bp_ivk_uaHashAdd(Sdf_st_hash *pHash, 
  Sdf_ty_pvoid pElement, 
  Sdf_ty_pvoid pKey);

Sdf_ty_pvoid 
bp_ivk_uaHashFetch(Sdf_st_hash *pHash, 
  Sdf_ty_pvoid pKey);

Sdf_ty_retVal 
bp_ivk_uaHashRemove(Sdf_st_hash *pHash, 
  Sdf_ty_pvoid pKey, 
  void** pObject);

Sdf_ty_u32bit 
bp_ivk_uaElfHash(Sdf_ty_pvoid pName);

//Sdf_ty_retVal 
//bp_ivk_uaHashRemove(Sdf_st_hash *pHash,
//  Sdf_ty_pvoid pKey, 
//  BpStackTimerContext** pObject);

Sdf_ty_u32bit 
bp_ivk_uaElfHashTmr(Sdf_ty_pvoid pName);

}

#endif

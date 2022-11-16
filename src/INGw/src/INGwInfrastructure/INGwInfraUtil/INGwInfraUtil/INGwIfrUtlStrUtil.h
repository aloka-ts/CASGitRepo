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
//     File:     INGwIfrUtlStrUtil.h
//
//     Desc:      Utility used to ping the machine availability.
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#ifndef __BP_STR_UTIL_H__
#define __BP_STR_UTIL_H__

namespace SAS_INGW
{
   char * ccmStrCpy(char *dest, const char *source);
   char * ccmStrnCpy(char *dest, const char *source, int len);
   char * ccmStrnStr(const char *source, int sourceLen, 
                     const char *field, int fieldLen);
   char * ccmStrnCaseStr(const char *source, int sourceLen, 
                         const char *field, int fieldLen);
   char * ccmTrim(char *);
};

#endif 

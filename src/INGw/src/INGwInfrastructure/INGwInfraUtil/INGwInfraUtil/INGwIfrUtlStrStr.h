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
//     File:     INGwIfrUtlStrStr.h
//
//     Desc:      Utility used to ping the machine availability.
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#ifndef __BP_STR_STR_H__
#define __BP_STR_STR_H__

#include <pthread.h>

class INGwIfrUtlStrStr
{
   private:

      char *_pattern;
      int _startPos;
      int _patternLen;
      bool _caseSensitive;
      pthread_mutex_t _mutex;

   public:

      INGwIfrUtlStrStr(const char *pattern, bool caseSensitive);
      ~INGwIfrUtlStrStr();

      const char * findPatternIn(const char *msg, int len);

   private:

      const char * _caseSensitivePatternCheck(const char *msg, int len);
      const char * _caseInSensitivePatternCheck(const char *msg, int len);
      void _setStartPos(int len);
};

#endif

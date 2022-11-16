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
//     File:     INGwIfrUtlStrStr.C
//
//     Desc:      Utility used to ping the machine availability.
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#include <Util/Logger.h>
LOG("INGwInfraUtil");

#include <INGwInfraUtil/INGwIfrUtlStrStr.h>
#include <string.h>
#include <limits.h>
#include <ctype.h>

INGwIfrUtlStrStr::INGwIfrUtlStrStr(const char *pattern, bool caseSensitive)
{
   _caseSensitive = caseSensitive;
   _patternLen = strlen(pattern);

   _pattern = new char[_patternLen + 1];
   strcpy(_pattern, pattern);

   _startPos = INT_MAX;

   if(caseSensitive == false)
   {
      for(int idx = 0; idx < _patternLen; idx++)
      {
         _pattern[idx] = toupper(_pattern[idx]);
      }
   }

   pthread_mutex_init(&_mutex, NULL);
}

INGwIfrUtlStrStr::~INGwIfrUtlStrStr()
{
   pthread_mutex_destroy(&_mutex);
}

const char * INGwIfrUtlStrStr::findPatternIn(const char *msg, int len)
{
   if(_caseSensitive)
   {
      return _caseSensitivePatternCheck(msg, len);
   }

   return _caseInSensitivePatternCheck(msg, len);
}

const char * INGwIfrUtlStrStr::_caseSensitivePatternCheck(const char *msg, int len)
{
   if(len < _patternLen)
   {
      return NULL;
   }

   int startPos = _startPos;

   if(len > startPos)
   {
      const char *currpos = msg + startPos;
      const char *endpos = msg + (len - _patternLen);

      while(currpos <= endpos)
      {
         if(*currpos == *_pattern)
         {
            const char *source = currpos;
            const char *inpattern = _pattern;

            source++;
            inpattern++;

            while(*source == *inpattern)
            {
               source++;
               inpattern++;
            }

            if(*inpattern == '\0')
            {
               return currpos;
            }
         }

         currpos++;
      }
   }

   const char *res = strstr(msg, _pattern);

   if(res == NULL)
   {
      return NULL;
   }

   _setStartPos(res - msg);

   return res;
}

const char * INGwIfrUtlStrStr::_caseInSensitivePatternCheck(const char *msg, int len)
{
   if(len < _patternLen)
   {
      return NULL;
   }

   int startPos = _startPos;

   const char *newEndPos = NULL;

   if(len > startPos)
   {
      const char *currpos = msg + startPos;
      const char *endpos = msg + (len - _patternLen);
      newEndPos = currpos;

      while(currpos <= endpos)
      {
         if(toupper(*currpos) == *_pattern)
         {
            const char *source = currpos;
            const char *inpattern = _pattern;

            source++;
            inpattern++;

            while(toupper(*source) == *inpattern)
            {
               source++;
               inpattern++;
            }

            if(*inpattern == '\0')
            {
               return currpos;
            }
         }

         currpos++;
      }
   }

   {
      const char *currpos = msg;
      const char *endpos = msg + (len - _patternLen);

      if(newEndPos && (newEndPos < endpos))
      {
         endpos = newEndPos;
      }

      while(currpos <= endpos)
      {
         if(toupper(*currpos) == *_pattern)
         {
            const char *source = currpos;
            const char *inpattern = _pattern;

            source++;
            inpattern++;

            while(toupper(*source) == *inpattern)
            {
               source++;
               inpattern++;
            }

            if(*inpattern == '\0')
            {
               _setStartPos(currpos - msg);
               return currpos;
            }
         }

         currpos++;
      }
   }

   return NULL;
}

void INGwIfrUtlStrStr::_setStartPos(int len)
{
   pthread_mutex_lock(&_mutex);
   _startPos = len;
   pthread_mutex_unlock(&_mutex);

   logger.logMsg(TRACE_FLAG, 0, "Pattern [%s] check position moved to [%d]",
                 _pattern, len);

   return;
}

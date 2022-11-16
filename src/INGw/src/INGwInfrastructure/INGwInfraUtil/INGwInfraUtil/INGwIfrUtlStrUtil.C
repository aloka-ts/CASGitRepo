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
//     File:     INGwIfrUtlStrUtil.C
//
//     Desc:      Utility used to ping the machine availability.
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#include <ctype.h>
#include <stdio.h>

namespace SAS_INGW
{

char * ccmStrCpy(char *dest, const char *source)
{
   while(*source)
   {
      *dest++ = *source++;
   }

   *dest = '\0';
   return dest;
}

char * ccmStrnCpy(char *dest, const char *source, int len)
{
   while((len != 0) && *source)
   {
      *dest++ = *source++;
      len--;
   }

   *dest = '\0';
   return dest;
}

char * ccmStrnStr(const char *source, int sourceLen,
                  const char *field, int fieldLen)
{
   const char *end = source + (sourceLen - fieldLen);

   while(source <= end)
   {
      if(*source != *field)
      {
         source++;
         continue;
      }

      const char *lSource = source;
      const char *lField = field;

      int len = 0;

      while(*lSource == *lField && len < fieldLen)
      {
         lSource++;
         lField++;
         len++;
      }

      if(len == fieldLen)
      {
         return (char *)source;
      }

      source++;
   }

   return NULL;
}

char * ccmStrnCaseStr(const char *source, int sourceLen, 
                      const char *field, int fieldLen)
{
   int upField = toupper(*field);

   const char *end = source + (sourceLen - fieldLen);

   while(source <= end)
   {
      if(toupper(*source) != upField)
      {
         source++;
         continue;
      }

      const char *lSource = source;
      const char *lField = field;

      int len = 0;

      while((toupper(*lSource) == toupper(*lField)) && (len < fieldLen))
      {
         lSource++;
         lField++;
         len++;
      }

      if(len == fieldLen)
      {
         return (char *)source;
      }

      source++;
   }

   return NULL;
}

char * ccmTrim(char *inData)
{
   while(*inData == ' ') inData++;

   char *ret = inData;
   char *end = ret;

   while(*inData != '\0')
   {
      if(*inData != ' ')
      {
         end = inData;
      }

      inData++;
   }

   if(*end != '\0')
   {
      end++;
   }

   *end = '\0';
   return ret;
}

};


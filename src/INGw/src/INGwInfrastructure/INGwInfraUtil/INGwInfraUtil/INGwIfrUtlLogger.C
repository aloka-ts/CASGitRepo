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
//     File:     INGwIfrUtlLogger.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
#include <stdlib.h>

using namespace std;

INGwIfrUtlLogger::INGwIfrUtlLogger(const char *comp, const char *filename, LogMgr *logMgr) :
Logger(comp, filename, logMgr)
{
}

void INGwIfrUtlLogger::logINGwMsg(bool forceFlag, LogLevel level, int lineno, int errNo, 
                        const char *formatStr, ...)
{
   va_list varg;
   va_start(varg, formatStr);


   if(level == ALARM)
   {
      logAlarmArg(lineno, errNo, 0, formatStr, varg);
   }
   else
   {
      _logMsg(level, lineno, errNo, formatStr, varg, forceFlag);
   }

   va_end(varg);
}

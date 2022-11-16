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
//     File:     INGwIfrUtlLogger.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#ifndef __BP_LOGGER_H__
#define __BP_LOGGER_H__

#include <Util/Logger.h>

class INGwIfrUtlLogger : public Logger
{
   public:

      INGwIfrUtlLogger(const char *, const char *, LogMgr * = NULL);

      void logINGwMsg(bool, LogLevel, int, int, const char *, ...);
};

#define LogINGwTrace(flag, errCode, errMsg) \
        logger.logINGwMsg(flag, Logger::TRACE, __LINE__, errCode, errMsg);
#define LogINGwVerbose(flag, errCode, errMsg) \
        logger.logINGwMsg(flag, Logger::VERBOSE, __LINE__, errCode, errMsg);
#define LogINGwWarning(flag, errCode, errMsg) \
        logger.logINGwMsg(flag, Logger::WARNING, __LINE__, errCode, errMsg);
#define LogINGwError(flag, errCode, errMsg) \
        logger.logINGwMsg(flag, Logger::ERROR, __LINE__, errCode, errMsg);

#define BPLOG(x) static INGwIfrUtlLogger logger((x), __FILE__)
#endif

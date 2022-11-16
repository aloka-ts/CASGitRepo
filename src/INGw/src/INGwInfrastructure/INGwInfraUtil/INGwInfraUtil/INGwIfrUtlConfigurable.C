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
//     Author     			Date     			Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraUtil");


#include "INGwInfraUtil/INGwIfrUtlConfigurable.h"

using namespace std;

const char* INGwIfrUtlConfigurable::mpConfigOpTypeDesc[] = {
     "CONFIG_OP_TYPE_ADD",
     "CONFIG_OP_TYPE_REMOVE",
     "CONFIG_OP_TYPE_REPLACE",
     "\0" 
};

const char* 
INGwIfrUtlConfigurable::getString(ConfigOpType aeType)
{
     const char* retValue = NULL;
     if((CONFIG_OP_TYPE_ADD <= aeType) && (CONFIG_OP_TYPE_REPLACE>= aeType)) {
         retValue = mpConfigOpTypeDesc[aeType];
     }
     else {
         retValue = "INVALID_VALUE";
     }
     return retValue;
}

INGwIfrUtlConfigurable::INGwIfrUtlConfigurable(void) { }
INGwIfrUtlConfigurable::~INGwIfrUtlConfigurable() { }

int INGwIfrUtlConfigurable::configure(const char* apcOID, const char* apcValue, ConfigOpType aeOpType) { return 0; }
int INGwIfrUtlConfigurable::oidChanged(const char* apcOID, const char* apcState, ConfigOpType aeOpType, long alSubsystemId) { return 0; }
void INGwIfrUtlConfigurable::dumpConfig(const char* apcFileName) { }

// EOF INGwIfrUtlConfigurable.C

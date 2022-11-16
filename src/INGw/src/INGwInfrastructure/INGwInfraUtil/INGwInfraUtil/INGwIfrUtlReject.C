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
//     File:     INGwIfrUtlReject.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraUtil");

#include "INGwInfraUtil/INGwIfrUtlReject.h"

using namespace std;

INGwIfrUtlReject::INGwIfrUtlReject(const char* apcInfo) : m_info(apcInfo) { }

INGwIfrUtlReject::~INGwIfrUtlReject() { }


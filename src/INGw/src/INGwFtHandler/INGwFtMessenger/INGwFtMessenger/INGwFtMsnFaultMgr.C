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
//     File:     INGwFtMsnFaultMgr.h
//
//     Desc:
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     pankaj Bathwal  26/11/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtMessenger");

#include <INGwFtMessenger/INGwFtMsnFaultMgr.h>
#include <INGwInfraManager/INGwIfrMgrFtIface.h>

INGwFtMsnFaultMgr::INGwFtMsnFaultMgr(INGwIfrMgrFtIface *aftIface)
{
   _ftIface = aftIface;
   INGwFtTkInterface::instance()->registerFaultCallback(this);
}

INGwFtMsnFaultMgr::~INGwFtMsnFaultMgr()
{
   INGwFtTkInterface::instance()->deregisterFaultCallback();
}

void INGwFtMsnFaultMgr::bogusFault(unsigned int subsysID)
{
   logger.logMsg(ALWAYS_FLAG, 0, "Received Bogus Fault notification from "
                                 "INGwTk for [%d]", subsysID);
   _ftIface->bogusFault(subsysID);
}

void INGwFtMsnFaultMgr::doAction(unsigned int subsysID, 
                                unsigned int actionID)
{
   logger.logMsg(ALWAYS_FLAG, 0, "Action [%d] request from [%d] received.",
                 actionID, subsysID);
   _ftIface->doAction(subsysID, actionID);
}

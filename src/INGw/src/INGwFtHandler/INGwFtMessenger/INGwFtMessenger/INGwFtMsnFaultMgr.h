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

#ifndef INGW_FT_MSN_FAULT_MGR_H_
#define INGW_FT_MSN_FAULT_MGR_H_

#include <INGwFtTalk/INGwFtTkInterface.h>

class INGwIfrMgrFtIface;

class INGwFtMsnFaultMgr : public INGwFtTkFaultHandlerInf
{
   private:

      INGwIfrMgrFtIface *_ftIface;

   public:

      INGwFtMsnFaultMgr(INGwIfrMgrFtIface *aftIface);
      ~INGwFtMsnFaultMgr();

      virtual void bogusFault(unsigned int subsysID);
      virtual void doAction(unsigned int subsysID, unsigned int actionID);
};

#endif

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
//     File:     INGwSpStackConfigMgr.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************
#ifndef INGW_SP_STACK_CONFIG_MGR_H_
#define INGW_SP_STACK_CONFIG_MGR_H_

#include <INGwSipProvider/INGwSpSipListenerThread.h>

class INGwSpStackConfigMgr
{
	public :
		static INGwSpStackConfigMgr*
		getInstance();

		int 
		initializeSipStack();

		int 
		startListenerThread(int p_Port, char* p_Host, void** p_TSDArray);

		void 
		sendToNetwork(void* p_Buffer, int p_Buflen, char* p_Addr, int p_Port);

		SipHdrTypeList& getStackDecodeHeaderTypes();

  private :

		static INGwSpStackConfigMgr* m_selfPtr;
		SipHdrTypeList m_HdrsTobeDecodedList;

		INGwSpStackConfigMgr();
		INGwSpStackConfigMgr(const INGwSpStackConfigMgr& p_StackConfigMgr);
		INGwSpStackConfigMgr& operator=(const INGwSpStackConfigMgr& p_StackConfigMgr);

    INGwSpSipListenerThread m_SipListenerThread;
    int m_SendSock;

		void
		bindFloatingIP(void** p_TSDArray);

		Sdf_ty_retVal 
		readUserProfile(const char*       p_Filename,
										Sdf_st_initData** p_AppProfile,
										Sdf_st_error*     p_Err);

    void
		initDecodedHeaderList();
};

#endif //INGW_SP_STACK_CONFIG_MGR_H_

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
//     File:     INGwLdDstMgr.h
//
//     Desc:     This is singleton class which shall be responsible for 
//						   providing interface with load distributor. 
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#ifndef _INGW_LD_DST_MGR_H_
#define _INGW_LD_DST_MGR_H_

#include <string>
#include <INGwLoadDistributor/INGwLdDstMap.h>

using namespace std;

class INGwLdDstMgr
{
	public:

		INGwLdDstMgr();

		~INGwLdDstMgr();

		int
		initialize();

		void
		setOpcSsnRegisteredWithStack (U32 p_selfPointCode, U8 p_selfSsn);

		void
		unsetOpcSsnRegisteredWithStack (U32 p_selfPointCode, U8 p_selfSsn);

		int
		getNumOfSasAppReg(U32 p_selfPointCode, U8 p_selfSsn);

		INGwLdPcSsnList
		getOpcSsnList();

		bool 
		isOpcSsnRegisteredWithStack (U32 p_selfPointCode, U8 p_selfSsn);

		int
		registerSASApp(U32 &p_selfPointCode, U8 &p_selfSsn, string &p_sasCallId);

		int
		deRegisterSASApp(U32 &p_selfPointCode, U8 &p_selfSsn, string &p_sasCallId);

		int
		removeSasApp(U32 &p_selfPointCode, U8 &p_selfSsn, string &p_sasCallId);

		string
		getDestSasInfo(U32 &p_pc, U8 &p_ssn);

		vector<string>
		getAllRegSasApp(U32 &pc, U8 &p_ssn);

		bool
		isSasIpRegistered(U32 &p_pc, U8 &p_ssn, const string &p_sasIp);

		bool
		serialize(char*&  p_loadDistBuf, int &p_loadDistBufLen);

		bool
		deserialize(char* p_loadDistBuf, int p_loadDistBufLen);

		string
		debugLoadDistInfo();

	private:
		
		int
		extractOpcSsn(string &p_oidVal);

		int
		extractSsn(string &p_oidVal);

		int
		validateOpcSsn(); 

		INGwLdDstMap			*m_dstInfoMap;
		INGwLdPcSsnList  	m_opcSsnConfigList;

};
#endif


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
//     File:     INGwIwfBaseInterface.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#ifndef _INGW_LD_DST_MAP_H_
#define _INGW_LD_DST_MAP_H_

#include <INGwLoadDistributor/INGwLdDstBucket.h>

#include <vector>

using namespace std;

class INGwLdDstMap
{
	public:

		INGwLdDstMap();
		~INGwLdDstMap();

		int
		initialize(INGwLdPcSsnList &p_opcSsnList, string &p_patternType);

		std::string
		debugLog();

		int
		addDstInfo (U32 &p_pc, U8 &p_ssn, string &p_sasCallId);

		int
		delDstInfo (U32 &p_pc, U8 &p_ssn, string &p_sasCallId);

		int 
		removeSasInfo(U32 &p_pc, U8 &p_ssn, string &p_sasCallId);

		int
		markDstUnavailable(U32 &p_pc, U8 &p_ssn);

		int
		markDstAvailable(U32 &p_pc, U8 &p_ssn);

		bool
		isSsnAvailable(U32 &p_pc, U8 &p_ssn);

		string
		getDestSasInfo(U32 &p_pc, U8 &p_ssn);

		vector<string>
		getAllRegSasApp(U32 &p_pc, U8 &p_ssn);

		int
		getNumOfSasAppReg(U32 &p_pc, U8 &p_ssn);

		bool
		isSasIpRegistered(U32 &p_pc, U8 &p_ssn, const string &p_sasIp);

	private:
		
		INGwLdDstBucket	 *m_mapBucket;
		vector<U8>     	  m_regSsn;
};

#endif

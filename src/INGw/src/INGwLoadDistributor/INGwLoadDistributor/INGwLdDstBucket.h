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
//     File:     INGwLdDstBucket.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#ifndef _INGW_LD_DST_BUCKET_H_
#define _INGW_LD_DST_BUCKET_H_

#include <pthread.h>

#include <INGwInfraUtil/INGwIfrUtlGlbInclude.h>

#include <INGwLoadDistributor/INGwLdDstPattern.h>

#include <vector>
#include <map>

using namespace std;

class INGwLdDstInfo
{
	public:

		INGwLdDstInfo();

		~INGwLdDstInfo();

		void
		addOpcSsnInfo(U32 p_opc, U8 p_ssn, U8* p_pcDetail, 
									string &p_patternType);

		void
		getOpcSsn( U32 &p_pc, U8 &p_ssn);

		bool
		belongTo(string &m_sasCallId);

		void
		markSsnAvailable();

		void
		markSsnUnavailable();

		bool
		isSsnAvailable();

		int
		addSasId(string &p_sasCallId);

		int
		delSasId(string &p_sasCallId);

		string
		getDestSasInfo();

		vector<string>
 		getAllRegSasApp();

		bool 
		operator==(U32 &p_pc);

		int
		getNumOfSasAppReg();

		string 
		debugInfo();

		bool
		isSasIpRegistered(const string &p_sasIp);

	private:

		int								     m_numOfRegSrv;
		bool									 m_markAvailability;

		pthread_rwlock_t			 m_rwLock;

		INGwLdPcSsn 					 m_opcSsnInfo;
		INGwLdDstPattern			 m_pattern;
		vector<string> 			   m_sasCallId;
};

class INGwLdDstBucket
{
	public:

		INGwLdDstBucket();
		~INGwLdDstBucket();

		vector<INGwLdDstInfo*> m_dstInfo;
};

#endif

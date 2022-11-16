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
//     File:     INGwLdDstPattern.h
//
//     Desc: This file provides different pattern based on which load     
//					 distributing will be decided among different SAS application
//					 registering with INGw.
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#ifndef _INGW_LD_DST_PATTERN_H_
#define _INGW_LD_DST_PATTERN_H_

#include <INGwInfraUtil/INGwIfrUtlGlbInclude.h>
#include <string>

using namespace std;

class INGwLdDstPattern
{
	public:
		
		enum e_PatternType {
			LD_UNKNOWN =0,
			ROUND_ROBIN,
			PERCENTAGE_BASED
		};
		
		INGwLdDstPattern();
		~INGwLdDstPattern();

		void
		initialize(string &p_patternType);

		void
		incrAppCount();

		void
		decrAppCount();

		int
		getNextSasIndx();

	private:

		e_PatternType 	m_myType;

		// For Round-Robin
		int							m_numOfApp;
		int							m_curCntr;
};

#endif

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
//     File:     INGwLdDstPattern.C
//
//     Desc: This file provides different pattern based on which load     
//					 distributing will be decided among different SAS application
//					 registering with INGw.
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************

#include <INGwLoadDistributor/INGwLdDstPattern.h>

INGwLdDstPattern::INGwLdDstPattern():m_myType(LD_UNKNOWN),
																	  m_numOfApp(0), m_curCntr(0)
{
}

INGwLdDstPattern::~INGwLdDstPattern()
{
}

void
INGwLdDstPattern::initialize(string &p_patternType)
{
	if ("ROUND_ROBIN" == p_patternType) {
		m_myType = ROUND_ROBIN;
	}
	else if ("PERCENTAGE_BASED" == p_patternType) {
		m_myType = PERCENTAGE_BASED;
	}
	else {
		m_myType = ROUND_ROBIN;
	}
}

void
INGwLdDstPattern::incrAppCount()
{
	m_numOfApp++;
}

void
INGwLdDstPattern::decrAppCount()
{
	m_numOfApp--;
}

int
INGwLdDstPattern::getNextSasIndx()
{
	int retIndx =0;

	if (ROUND_ROBIN == m_myType) {
		retIndx = m_curCntr%m_numOfApp;
		m_curCntr++;
	}

	return retIndx;
}

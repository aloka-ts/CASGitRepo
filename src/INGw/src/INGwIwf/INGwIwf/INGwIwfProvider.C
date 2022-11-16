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
//     File:     INGwIwfProvider.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwIwf");

#include <INGwIwf/INGwIwfProvider.h>

#include <INGwTcapProvider/INGwTcapProvider.h>
#include <INGwSipProvider/INGwSpSipProvider.h>

INGwIwfProvider* INGwIwfProvider::m_selfPtr = NULL;

/**
*	This method instantiate IwfProvider.
*/
INGwIwfProvider&
INGwIwfProvider::getInstance()
{
	LogINGwTrace(false, 0,"IN INGwIwfProvider::getInstance()");
             
  if (NULL == m_selfPtr) {
    INGwIwfProvider::m_selfPtr = new INGwIwfProvider();
	}
             
	LogINGwTrace(false, 0,"OUT INGwIwfProvider::getInstance()");
  return *(INGwIwfProvider::m_selfPtr);
}

/**
* Constructor
*/
INGwIwfProvider::INGwIwfProvider():INGwIwfBaseProvider(PROVIDER_TYPE_IWF)
{
	LogINGwTrace(false, 0,"IN INGwIwfProvider::constructor()");

	m_iwfIface = new INGwIwfIface();	// Iwf Iface

	LogINGwTrace(false, 0,"OUT INGwIwfProvider::constructor()");
}

/**
* Destructor
*/
INGwIwfProvider::~INGwIwfProvider()
{
	LogINGwTrace(false, 0,"IN INGwIwfProvider::destructor()");

	if ( NULL != m_iwfIface) {
		delete m_iwfIface;
	}

	LogINGwTrace(false, 0,"OUT INGwIwfProvider::destructor()");
}

/**
* Description : This method is called whenever there is change in state of 
*							  INGw component. It could be Loaded, Running or Stpoped.
*
* @param <p_state> - Enum specifying LOADED, RUNNING or STOPPED.
*
* @return <int> - G_SUCCESS or G_FAILURE
*
*/
int 
INGwIwfProvider::changeState(INGwIwfBaseProvider::ProviderStateType p_state)
{
	LogINGwTrace(false, 0,"IN INGwIwfProvider::changeState()");

	int retVal = G_SUCCESS;
	string lStateName;


	if (INGwIwfBaseProvider::PROVIDER_STATE_LOADED == p_state) {
		lStateName = "LOADED";
	}
	else if (INGwIwfBaseProvider::PROVIDER_STATE_RUNNING == p_state) {
		lStateName = "RUNNING";
	}
	else if (INGwIwfBaseProvider::PROVIDER_STATE_STOPPED == p_state) {
		lStateName = "STOPPED";
	}

	logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
	"[changeState] State changed to [%s]", lStateName.c_str());

	LogINGwTrace(false, 0,"OUT INGwIwfProvider::changeState()");
	return retVal;
}

/**
* Description : 
*
* @param <> -
*
* @return <> -
*
*/
int 
INGwIwfProvider::startUp()
{
	LogINGwTrace(false, 0,"IN INGwIwfProvider::startUp()");

	int retVal = G_SUCCESS;

	INGwIwfBaseIface *lIface;

	// Tcap Interface
	lIface = INGwTcapProvider::getInstance().getInterface();
	m_iwfIface->setTcapIface(lIface);

	// SIP Interface
	lIface = INGwSpSipProvider::getInstance().getInterface();
	m_iwfIface->setSipIface(lIface);

	LogINGwTrace(false, 0,"OUT INGwIwfProvider::startUp()");
	return retVal;
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
void 
INGwIwfProvider::getStatistics(std::ostrstream &output, int tabCount)
{
	LogINGwTrace(false, 0,"IN INGwIwfProvider::getStatistics()");
	LogINGwTrace(false, 0,"OUT INGwIwfProvider::getStatistics()");
}


////////////////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2001, Bay Packets Inc.
// All rights reserved.
//
// Filename   : BpCall.h
// Description: This file contains the declaration of the BpGwCall.  This class
//              provides the gateway functionality to the connections, to
//              propagate data across connections.  Apart from the SLEE
//              operations, this class is the only means of transporting
//              information between connections.
//              This class extends the BpCall, which is more concerned about
//              the call's interface with the rest of the CCM.
//
// NAME           DATE           REASON
// ----------------------------------------------------------------------------
//       31 Jul 2002    Initial Creation
//
////////////////////////////////////////////////////////////////////////////////

#ifndef BP_AIN_SILMSGHDLR_H
#define BP_AIN_SILMSGHDLR_H

//#include "ccm/BpWorkerClbkIntf.h"
#include "INGwInfraManager/INGwIfrMgrWorkerClbkIntf.h"
//#include "ccm/BpWorkUnit.h"
#include "INGwInfraManager/INGwIfrMgrWorkUnit.h"
// #include "INGwProvider.h"
#include "INGwIwf/INGwIwfBaseProvider.h"
#include "INGwSilMsg.h"

#include <string>

// Mriganka - Trillium stack integration
class INGwSilMsgHandler : public INGwIfrMgrWorkerClbkIntf
{
  public:

	// Enumerator to define the status of stack events
	// received. This is required since multiple operation requests
	// could come in a single TCAP packet and these are delivered to the
	// application in different Calls.

	typedef enum {
		BP_AIN_PENDING,
		BP_AIN_PROCESS
	} tAinEventProcessingStatus;

   INGwSilMsgHandler(INGwIwfBaseProvider    &arProvider);
   // INGwCall(INGwIwfBaseProvider    &arProvider,
//              const std::string  &arconCallId,
//              bool           abGenCallId = true);

   virtual ~INGwSilMsgHandler();

   void init();
   
   /////////////////////////// SERVICE SUPPORT FUNCTIONS //////////////////////


	// Cblk method to process the work unit
   virtual int handleWorkerClbk(INGwIfrMgrWorkUnit* apWork);
	
	static INGwSilMsgHandler*
	getInstance();
	
   int
   handleAinSilMsg(INGwSilMsg &arINGwSilMsg);
   

protected:
   
private:

   /**
    ** Helper functions at the Dialoge and Componenet Level
    **/
   
   // ..
	
	INGwIwfBaseProvider&      mrProvider;
	static INGwSilMsgHandler *mpSelf;
	
}; // end of class INGwCall

#endif

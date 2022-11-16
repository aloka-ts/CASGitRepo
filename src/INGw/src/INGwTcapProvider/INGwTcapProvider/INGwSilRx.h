////////////////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2001, Bay Packets Inc.
// All rights reserved.
//
// Filename   : INGwSilRx.h
// Description: This file contains the declaration of the INGwSilRx.  
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
///////////////////////////////////////////////////////////////////////////////

#ifndef BP_AIN_SIL_RX_H
#define BP_AIN_SIL_RX_H


#ifdef SIMULATOR
#include "ainSim/BpWorkerClbkIntf.h"
#else
//#include "ccm/BpWorkerClbkIntf.h"
#include "INGwInfraManager/INGwIfrMgrWorkerClbkIntf.h"
#endif

//#include "INGwProvider/INGwSil.h"
//#include "INGwProvider/INGwSilMsg.h"
#include "INGwTcapProvider/INGwSil.h"
//#include "INGwTcapProvider/INGwSilMsg.h"
#include "INGwTcapProvider/INGwTcapWorkUnitMsg.h"

// Declaration for the iuActvInit and iuActvTsk functions
// for Using this functions in Registering functions of
// INGwProvider

#ifndef __CCPU_CPLUSPLUS
extern "C" {
#endif
   
S16
tuActvInit(Ent ent, Inst inst, Region region, Reason   reason);

S16
tuActvTsk(Pst *pst, Buffer *mBuf);
   
#ifndef __CCPU_CPLUSPLUS
}
#endif

class INGwSilRx;

class INGwSilRx {
public:

   INGwSilRx();
   ~INGwSilRx();
   
   static INGwSilRx& instance();
   int init(INGwIfrMgrWorkerClbkIntf *apINGwIfrMgrWorkerClbkIntf);
   
   // Management Functions
   int start();
   int stop();
   
   // TAPA activity task cblk for the trillium stack to deliver INAP messages
   S16
   tuActvTsk(Pst *pst, Buffer *mBuf);

   // TAPA init task cblk for the trillium stack to deliver INAP messages
   S16
   tuActvInit(Ent      ent,                 /* entity */
	      Inst     inst,                /* instance */
	      Region   region,              /* region */
	      Reason   reason);             /* reason */

   int
   pushMsg(INGwTcapWorkUnitMsg *apQMsg);
   
private:
   static INGwSilRx *mpSelf;

   // Reference to Call back handler
   INGwIfrMgrWorkerClbkIntf *mpINGwIfrMgrWorkerClbkIntf;
   

};
#endif // BP_AIN_SIL_RX_H

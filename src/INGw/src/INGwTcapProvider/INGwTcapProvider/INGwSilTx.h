///////////////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2001, Bay Packets Inc.
// All rights reserved.
//
// Filename   : INGwSilTx.h
// Description: This file contains the declaration of the INGwSilRx.  
//
// NAME           DATE           REASON
// ----------------------------------------------------------------------------
//       31 Jul 2002    Initial Creation
//
///////////////////////////////////////////////////////////////////////////////

#ifndef BP_AIN_SIL_TX_H
#define BP_AIN_SIL_TX_H

#define SUNOS
#define UNIX

#ifndef __CCPU_CPLUSPLUS
extern "C" {
#endif
  #include "envopt.h"        /* environment options */
  #include "envdep.h"        /* environment dependent */
  #include "envind.h"        /* environment independent */
  
  #include "gen.h"           /* general layer */
  #include "ssi.h"           /* system services */
  //#include "lst.h"           /* Inap layer manager interface */
  #include "stu.h"           /* TCAP upper interface */
  
  /* header/extern include files (.x) */
  
  #include "gen.x"           /* general layer */
  #include "ssi.x"           /* system services */
  #include "cm_ss7.x"
  #include "cm_lib.x"        /* common library functions */
  //#include "lst.x"           /* Inap layer manager interface */
  #include "stu.x"           /* TCAP upper interface */
#ifndef __CCPU_CPLUSPLUS
}
#endif

#define TU_SEL_LC       0        /* selector 0 (loosely-coupled) */
#define TU_SEL_TC       1        /* selector 1 (tightly-coupled) */



#include "INGwCommonTypes/INCCommons.h"
#include "INGwTcapProvider/INGwTcapWorkUnitMsg.h"



// Forward declaration for Instance

class INGwSilTx;

class INGwSilTx {
public:
   INGwSilTx();
   ~INGwSilTx();

   static INGwSilTx& instance();
   
   int init();

   void releaseWorkUnit(INGwTcapWorkUnitMsg *apMsg);   
   int sendTcapReq(INGwTcapWorkUnitMsg *apMsg);
   string getSelfSgRole();
#ifdef PK_UNPK_DEBUG_FLAG
   void printMBufStore(int nmbDlgsPerFile = 0);
#endif

   stringBuff getUAbortUInfo() {
     return uInfo;
   }

   stringBuff getRCParamBuf() {
     return mRCParamBuf;
   }
private:
   
   static INGwSilTx *mpSelf;
   
   U8 mSccpSeqCtrl;
   U8 mSccpRetOpt;
   int mInvokeTimer;
   stringBuff   uInfo;
   stringBuff   mRCParamBuf;
};

#endif // BP_AIN_SIL_TX_H

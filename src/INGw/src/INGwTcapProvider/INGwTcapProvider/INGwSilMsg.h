///////////////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2001, Bay Packets Inc.
// All rights reserved.
//
// Filename   : INGwSilMsg.h
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

#ifndef BP_AIN_SIL_MSG_H
#define BP_AIN_SIL_MSG_H


#include "INGwTcapProvider/INGwSil.h"
#include <sys/time.h>

/**
 ** Message Queue element between INGwSil and INGwProvider
 **/

typedef struct tINGwSilMsg
{
   U8           evtType;              /* Primitive type */
   U8           msgType;               /* Tcap message type BEGIN/CONTINUE/END. Applicable only if entType is DAT_IND*/
   SuId         suId;                 /* Sap Id */
   StDlgId      suDlgId;              /* Service User dialogue Id */
   StDlgId      spDlgId;              /* Service Provider dialogue Id */
   SpAddr      *dstAddr;
   SpAddr      *srcAddr;
   Bool         endFlag;              /* used in DatReq primitive */
   Dpc          opc;
   StQosSet    *qosSet;
   // Time at which an ss7 event is  received from the network
   hrtime_t     evInTime;
   Buffer       *cpBuf;
   Bool         compsPres;     /* applicable only if entType is DAT_IND */
   StDataParam   dataParam;
   StOctet        *pAbtCause; /* applicable only if entType is DAT_IND and message type is abort */
   union
   {
      StDlgEv      dlgEv;
      StComps      compEv;

      struct
      {
         StInvokeId    invId;         /* Invoke Id */
         RCause        cause;         /* Return Cause, set by SCCP */
         Buffer       *cpBuf;         /* Parameter buffer */
      } notEv;                        /* Notice Indication */
      Status          status;
      cmSS7SteMgmt ss7SteMgmt;
   } s;
} INGwSilMsg;

#endif //BP_AIN_SIL_MSG_H


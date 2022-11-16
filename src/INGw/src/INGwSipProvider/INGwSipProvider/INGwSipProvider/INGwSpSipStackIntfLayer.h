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
//     File:     INGwSpSipStackIntfLayer.h
//
//     Desc: This file contains the declaration of the INGwSpSipStackIntfLayer
//           class.  The methods declared here provide interface between
//           the HSS sip stack and the rest of the SIP provider.
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef INGW_SP_BP_SIP_STACK_INTF_LAYER_H_
#define INGW_SP_BP_SIP_STACK_INTF_LAYER_H_

#include <INGwSipProvider/INGwSpSipIncludes.h>
#include <INGwSipProvider/INGwSpSipContext.h>
#include <INGwInfraManager/INGwIfrMgrWorkerClbkIntf.h>

extern Sdf_ty_s32bit glbMinSE;
extern Sdf_ty_bool   glbProfileSupportedTimer;

struct RawSipData{
  char* mBuf;
  char* IpAddr;
  int   mBufLen;
  int   mPort;
  INGwSpSipConnection *mSipConn;
  char  mTransptype;
};

// Stack related callbacks
extern "C"
{
  Sdf_ty_s8bit* callidGeneratorFunction(Sdf_st_appData *pAppData);

  Sdf_ty_retVal responseCallback
  (Sdf_st_callObject       **ppCallObj,
   Sdf_st_eventContext      *pEventContext,
   Sdf_st_error             *pErr,
   bool                      aIsReinvite,
   Sdf_st_overlapTransInfo  *pOverlapTransInfo = NULL);

  Sdf_ty_retVal ackCallback
  (Sdf_st_callObject    **ppCallObj,
  Sdf_st_eventContext    *pEventContext,
  Sdf_st_error           *pErr,
  bool                    aIsReinvite);

/*

  Sdf_ty_retVal sdf_cbk_uaNewCallReceived
  (Sdf_st_callObject    **ppCallObj,
   Sdf_st_eventContext   *pEventContext,
   Sdf_st_error          *pErr);

  Sdf_ty_retVal sdf_cbk_uaInProgress
  (Sdf_st_callObject    **ppCallObj,
   Sdf_st_overlapTransInfo *pOverlapTransInfo,
   Sdf_st_eventContext   *pEventContext,
   Sdf_st_error          *pErr);

  Sdf_ty_retVal sdf_cbk_uaCallAcknowledged
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr);

  Sdf_ty_retVal sdf_cbk_uaCallFailed
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr);

  Sdf_ty_retVal sdf_cbk_uaCallAccepted
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr);

  Sdf_ty_retVal sdf_cbk_uaFailedCallAcknowledged
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr);

  Sdf_ty_retVal sdf_cbk_uaCancelReceived
  (Sdf_st_callObject    **ppCallObj,
   Sdf_st_eventContext   *pEventContext,
   Sdf_st_error          *pErr);

  Sdf_ty_retVal sdf_cbk_uaCancelCompleted
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr);

  Sdf_ty_retVal sdf_cbk_uaCallTerminateRequest
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr);

  Sdf_ty_retVal sdf_cbk_uaCallTerminated
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr);

  Sdf_ty_retVal sdf_cbk_uaCallRedirected
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr);

  Sdf_ty_retVal sdf_cbk_uaTransactionCompleted
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr);

  Sdf_ty_retVal sdf_cbk_uaTransactionReceived
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_overlapTransInfo *pOverlapTransInfo,
    Sdf_st_error *pErr);
  Sdf_ty_retVal sdf_cbk_uaReInviteReceived
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr);

  Sdf_ty_retVal sdf_cbk_uaReInviteAcknowledged
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr);

  Sdf_ty_retVal sdf_cbk_uaReInviteFailed
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr);

  Sdf_ty_retVal sdf_cbk_uaReInviteAccepted
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr);

  Sdf_ty_authResult sdf_cbk_uaAuthenticateUser
    (Sdf_st_callObject **ppCallObj,
    SipAddrSpec *pAddrSpec,
    Sdf_st_uasAuthInfo *pUasAuthInfo,
    Sdf_ty_pvoid *ppContext,
    Sdf_st_error *pErr);

  Sdf_ty_authResult sdf_cbk_uaChallengeUser
    (Sdf_st_callObject **ppCallObj,
    SipAddrSpec *pAddrSpec,
    Sdf_st_authenticationParams *pAuthParams,
    Sdf_ty_pvoid *ppContext,
    Sdf_st_error *pErr);

  Sdf_ty_retVal sdf_cbk_uaUnknownMethodReceived
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_overlapTransInfo *pOverlapTransInfo,
    Sdf_st_error *pErr);

  Sdf_ty_retVal sdf_cbk_uaRPRReceived
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_overlapTransInfo *pOverlapTransInfo,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr);

  Sdf_ty_retVal sdf_cbk_uaOverlapTransactionReceived
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_overlapTransInfo *pOverlapTransInfo,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr);

  Sdf_ty_retVal sdf_cbk_uaOverlapTransactionCompleted
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_overlapTransInfo *pOverlapTransInfo,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr);

  Sdf_ty_retVal sdf_cbk_uaFreeApplicationData
    (Sdf_ty_pvoid *ppData);

  Sdf_ty_retVal sdf_cbk_uaCloneAppData
    (Sdf_st_appData *pDestInfo,
    Sdf_st_appData *pSrcInfo,
    Sdf_st_error *pErr);

  Sdf_ty_retVal sdf_cbk_uaFreeEventContextData
    (Sdf_ty_pvoid *ppData);

  Sdf_ty_retVal sdf_cbk_uaStartTimer
    (Sdf_ty_u32bit duration,
    Sdf_ty_s8bit restart,
    timeoutfunctype timeoutfunc,
    Sdf_st_error *pErr);

  Sdf_ty_retVal sdf_cbk_uaStopTimer
    (Sdf_ty_pvoid pTimerHandle,
    Sdf_ty_pvoid *info,
    Sdf_st_error *pErr);

  void sdf_cbk_uaFreeTimerHandle
    (Sdf_ty_pvoid pTimerHandle);

  Sdf_ty_retVal sdf_cbk_uaUnexpectedRequestReceived
    (Sdf_st_callObject  **ppCallObj,
     Sdf_st_eventContext *pEventContext,
     Sdf_st_overlapTransInfo *pOverlapTransInfo,
     Sdf_st_error *pErr);
*/

  SipBool sip_sendToNetwork
    (SIP_S8bit *buffer,
    SIP_U32bit buflen,
    SipTranspAddr *transpaddr,
    SIP_S8bit transptype,
    SipError *err);

  SIP_S8bit sip_willParseSdpLine
    (SIP_S8bit *in,
    SIP_S8bit **out);

  SipBool sip_acceptIncorrectSdpLine
    (SIP_S8bit *in);

  SIP_S8bit sip_willParseHeader
    (en_HeaderType type,
    SipUnknownHeader *hdr);

  SipBool sip_acceptIncorrectHeader
    (en_HeaderType type,
    SipUnknownHeader *hdr);

  void sip_indicateTimeOut
    (SipEventContext *context);

  SipBool sip_decryptBuffer
    (SipMessage *s,
    SIP_S8bit *encinbuffer,
    SIP_U32bit clen,
    SIP_S8bit **encoutbuffer,
    SIP_U32bit *outlen);

/*

  Sdf_ty_retVal sdf_cbk_uaInitRemoteRetransTables
    (Sdf_st_error *pErr);

  Sdf_ty_retVal sdf_cbk_uaFreeRemoteRetransTables
    (Sdf_st_error *pErr);

  Sdf_ty_retVal sdf_fn_uaGetHostIp
    (Sdf_ty_s8bit* pHost,
    Sdf_ty_s8bit** ppDest,
    Sdf_st_error *pError);

  Sdf_ty_retVal sdf_cbk_uaGetRemoteRetransHashTable
    (Sdf_st_hash **ppRemoteRetransHash,
    Sdf_st_eventContext *pContext,
    Sdf_st_error *pErr);
*/

  SipBool fast_startTimer
    (SIP_U32bit duration,
    SIP_S8bit restart,
    sip_timeoutFuncPtr timeoutfunc,
    SIP_Pvoid buffer,
    SipTimerKey *key,
    SipError *err);

  SipBool fast_stopTimer
    (SipTimerKey *inkey,
    SipTimerKey **outkey,
    SIP_Pvoid *buffer,
    SipError *err);
/*

  Sdf_ty_retVal sdf_cbk_uaGetIpFromTelUrl
   (TelUrl *pTelUrl,
    Sdf_st_transportInfo *pDestInfo,
    Sdf_st_error *pErr);

  Sdf_ty_retVal sdf_cbk_uaTransactionCancel
   (Sdf_st_callObject **ppCallObj,
   Sdf_st_eventContext *pEventContext,
   Sdf_st_overlapTransInfo *pOverlapTransInfo,
   Sdf_st_error *pErr);

  Sdf_ty_retVal sdf_cbk_uaHandleSTTimeout
    (Sdf_st_callObject *pCallObj,
    Sdf_ty_handleEvent *pRefresh,
    Sdf_st_error  *pErr);
*/
} // end of extern "c"

class INGwSpSipStackIntfLayer;
class INGwIfrMgrWorkUnit;

class INGwSpSipStackIntfLayer
      : public INGwIfrMgrWorkerClbkIntf
{
  public:
    int recvSipMsg(char *aSipMsg, unsigned long aMaxLength, 
                   void *aContext);
    static INGwSpSipStackIntfLayer& instance();
    void initialize(void);
    char* getBuffer();
    void reuseBuffer(char* apBuffer);

    Sdf_st_callObject* getNewCallLeg(int aTID);
    void reuseCallLeg(Sdf_st_callObject* apCallLeg);
    int handleWorkerClbk(INGwIfrMgrWorkUnit* apWork);

    static int addHBConnection(INGwSpSipConnection* aSipConnection);
		int initInboundMsgConn(INGwSpSipConnection* aSipConnection);


    void INGwSpSipStackIntfLayer::handleConfigUpdate(INGwSipConfigContext* aContext);
   
    //this member is used to manage first retrans delay of SIP messages
    static int miRetransTime;

    //this will hold retransmission counts based on threadId
    static unsigned long long* mplSipRetransCount;
    long mllowThreadId;
    long mlHiThreadId;
    int initRetransCntArray();

  private:
    INGwSpSipStackIntfLayer();

    int sendSipMsgToNetwork(INGwIfrMgrWorkUnit* apWork);
    static INGwSpSipStackIntfLayer *mpSelf;
};

#endif //INGW_SP_BP_SIP_STACK_INTF_LAYER_H_

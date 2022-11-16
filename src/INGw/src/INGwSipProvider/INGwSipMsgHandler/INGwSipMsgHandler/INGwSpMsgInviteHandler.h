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
//     File:     INGwSpMsgInviteHandler.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef INGW_SP_MSG_INVITE_HANDLER_H_
#define INGW_SP_MSG_INVITE_HANDLER_H_

#include <INGwSipMsgHandler/INGwSpMsgBaseHandler.h>
#include <INGwSipMsgHandler/INGwSpMsgInviteStateContext.h>
#include <INGwSipMsgHandler/INGwSpMsgSipSpecificAttr.h>

#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpSipStackIntfLayer.h>
#include <INGwSipProvider/INGwSpSipContext.h>
#include <INGwSipProvider/INGwSpSipHeaderPolicy.h>
#include <INGwSipProvider/INGwSpAddress.h>

#include <string>
#include <vector>


using namespace RSI_NSP_SIP;

class INGwSpMsgInviteHandler : public INGwSpMsgBaseHandler 
{
   private:

      INGwSpMsgInviteStateContext     mInviteStateContext;
      Sdf_st_overlapTransInfo* m_overlapTxInfo;

   private:

      SipUrl *m_oInvReqLineUrl;
      TelUrl *m_oFromTel;
      TelUrl *m_oToTel;
      TelUrl *m_oReqTel;

      INGwSpMsgSipSpecificAttr *sipAttr;

			// This member keeps track of the Offer Answer negotiation 
			//	in Invite Transaction. It is used for UAC as well as 
			//	UAS transactions.
			OfferAnswerState_t	mOfferAnswerState;

   public:

      friend class INGwSpSipConnection;

   public:

      INGwSpMsgInviteHandler();
      ~INGwSpMsgInviteHandler();

      const INGwSpMsgInviteHandler & operator = (const INGwSpMsgInviteHandler &);

      void setSipSpecificAttr(INGwSpMsgSipSpecificAttr *p_SipAttr);

      void reset(void);
      std::string toLog()const;

   public:

      // Callbacks from stack.

      // FROM Base class Interface

         virtual Sdf_ty_retVal stackCallbackRequest(
                    INGwSpSipConnection     *aSipConnection   ,
                    INGwSipMethodType        aMethodType      ,
                    Sdf_st_callObject      **ppCallObj        ,
                    Sdf_st_eventContext     *pEventContext    ,
                    Sdf_st_error            *pErr             ,
                    Sdf_st_overlapTransInfo *pOverlapTransInfo = NULL);

         virtual Sdf_ty_retVal stackCallbackResponse
                  (INGwSpSipConnection     *aSipConnection   ,
                   INGwSipMethodType        aMethodType      ,
                   int                      aRespCode        ,
                   Sdf_st_callObject      **ppCallObj        ,
                   Sdf_st_eventContext     *pEventContext    ,
                   Sdf_st_error            *pErr             ,
                   Sdf_st_overlapTransInfo *pOverlapTransInfo = NULL);

         virtual Sdf_ty_retVal stackCallbackAck
                  (INGwSpSipConnection     *aSipConnection   ,
                   INGwSipMethodType        aMethodType      ,
                   Sdf_st_callObject      **ppCallObj        ,
                   Sdf_st_eventContext     *pEventContext    ,
                   Sdf_st_error            *pErr             );
 
      
      
      // End Base class Interface
      


      void indicateTimeout(INGwSpSipConnection *aSipConnection, 
                           INGwSipMethodType aMethodType, 
                           INGwSipTimerType::TimerType aType, 
                           unsigned int aTimerid);

      
      void _parseFromHdr(INGwSpSipConnection *conn, SipHeader *hdr, 
                         INGwSpAddress &addr);
      void _parseToHdr(INGwSpSipConnection *conn, SipHeader *hdr, INGwSpAddress &addr);
      void _parseReqURI(INGwSpSipConnection *conn, SipAddrSpec *addrSpec, 
                        INGwSpAddress &addr);
      void _parseContact(SipMessage *msg, INGwSpSipConnection *conn);

   public:

      //Functions GW call invokes for request passthru.

      // FROM Base class Interface

      int mSendRequest( INGwSpSipConnection* aSipConnection,
                        INGwSipMethodType     aMethodType,
                        INGwSpData*        aGwData );

      int mSendResponse( INGwSpSipConnection         *aSipConnection   ,
                         INGwSipMethodType             aMethodType      ,
                         INGwSpData                *aGwData          ,
                         int                      aCode);

      int mSendAck ( INGwSpSipConnection         *aSipConnection   ,
                     INGwSipMethodType             aMethodType      ,
                     INGwSpData                *aGwData          );

      // FROM Base class Interface END

      int mSendRPR(INGwSpSipConnection *aSipConnection, INGwSipMethodType aMethodType, 
                   INGwSpData *aGwData, int aCode);
                  
   public:

      //Functions GW call invokes for SLEE operation handling.

      short connect(INGwSpSipConnection *aSipConnection, int aNoAnsTimer);

      short redirect(INGwSpSipConnection* aSipConnection, const char* aTargetAddr);

      short continueProcessing(INGwSpSipConnection *aSipConnection);

   public:

      bool searchContact(INGwSpSipConnection *aSipConnection);

      bool searchOutboundGw(INGwSpSipConnection *aSipConnection);

      bool isIncoming();

      bool getRedirectionInfo(INGwSpSipConnection* aSipConnection, 
                              INGwSipEPInfo& aSourceInfo, INGwSipMethodType aHandlerType);

      inline Sdf_st_overlapTransInfo* getLatestOverLapTxInfo() 
      {
         return m_overlapTxInfo;
      }

      inline void setLatestOverLapTxInfo(Sdf_st_overlapTransInfo* tx) 
      {
         m_overlapTxInfo = tx;
      }

      bool terminateTransaction(INGwSpSipConnection *aSipConnection, int aErrCode, 
                                TerminateType aTermType);

      int getLatestRespCode();


      bool _processIncomingViaHeader(INGwSpSipConnection *conn, 
                                     Sdf_st_callObject* hssCallObj);
      bool _processIncomingFromHeader();
      bool _processIncomingToHeader();
      bool _processIncomingReqURI();
      bool _processIncomingContactHeader();

   private:

      void generateEvent(INGwSpSipConnection *aSipConnection);
      void releaseTempRedirCallObj(INGwSpSipConnection* aSipConnection,
                                   Sdf_st_callObject** ppCallObj);

      Sdf_ty_retVal sendAckToPeer(INGwSpSipConnection *, 
                                  Sdf_st_callObject *aCallObj);

      void handleTimeout(INGwSpSipConnection* aSipConnection,
                         INGwSipTimerType::TimerType aType);
      void processPrivacy(INGwSpSipConnection* aSipConnection,
                          Sdf_st_callObject* aCallObj);

      void changeRequestLine(Sdf_st_callObject* aCallObj, const char* aUser, 
                             const char* aHost, const int aPort);

   private:

      Sdf_ty_retVal stackCallbackResponse_Redir(INGwSpSipConnection *aSipConnection,
                                                INGwSipMethodType aMethodType, 
                                                int aRespCode, 
                                                Sdf_st_callObject **ppCallObj, 
                                           Sdf_st_eventContext *pEventContext, 
                                                Sdf_st_error *pErr,
                             Sdf_st_overlapTransInfo *pOverlapTransInfo = NULL);

      Sdf_ty_retVal processUnProcessedHeaders(Sdf_st_callObject* hssCallObj, 
                                              INGwSpSipConnection* bpConn, 
                                              Sdf_st_error* pErr, SIP_U32bit & causeCode);

      short sendInvite(INGwSpSipConnection *aSipConnection);


      void _setDispName(const INGwSpSipHeaderTreatment &treatment,
                        const INGwSpSipHeaderDefault &defVal, 
                        INGwSpSipConnection *peerConn, INGwSpSipConnection *conn, 
                        RSI_NSP_SIP::HeaderName hdr, SipHeader *header);

      bool _isTelParam(const char *name, int nameLen);
      SipAddrSpec * _makeAddrSpec(const INGwSpSipAddressTreatment &treatment, 
                                  const INGwSpSipAddressDefault &defVal, 
                                  INGwSpSipConnection *peerConn, 
                                  INGwSpSipConnection *conn, 
                                  RSI_NSP_SIP::HeaderName hdr);

      void _setHeaderParam(const INGwSpSipHeaderTreatment &treatment,
                           const INGwSpSipHeaderDefault &defVal, 
                           INGwSpSipConnection *peerConn, INGwSpSipConnection *conn, 
                           RSI_NSP_SIP::HeaderName hdr, SipHeader *header);

      char * _replaceParam(char *start, char *end, const char *name, 
                           int nameLen, const char *value, int valLen);

      SipHeader * _makeToHeader(INGwSpSipConnection *peerConn, 
                                INGwSpSipConnection *conn);

      SipHeader * _makeFromHeader(INGwSpSipConnection *peerConn, 
                                  INGwSpSipConnection *conn);

      SipHeader * _makeContactHeader(INGwSpSipConnection *peerConn, 
                                     INGwSpSipConnection *conn);

      SipAddrSpec * _makeReqURI(INGwSpSipConnection *peerConn, 
                                INGwSpSipConnection *conn);


};
#endif //INGW_SP_MSG_INVITE_HANDLER_H_

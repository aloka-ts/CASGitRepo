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
//     File:     INGwSpMsgNotifyHandler.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef INGW_SP_MSG_NOTIFY_HANDLER_H_
#define INGW_SP_MSG_NOTIFY_HANDLER_H_

#include <INGwSipMsgHandler/INGwSpMsgBaseHandler.h>

class INGwSpSipConnection;
class INGwSpData;

#include <INGwSipProvider/INGwSpSipCommon.h>

#include <INGwSipProvider/INGwSpSipIncludes.h>
#include <INGwSipProvider/INGwSpStackTimer.h>

#include <string>

class INGwSpMsgNotifyHandler : public INGwSpMsgBaseHandler
{
  public:
    INGwSpMsgNotifyHandler();
    ~INGwSpMsgNotifyHandler();

    void reset();

    Sdf_ty_retVal stackCallbackRequest(
                    INGwSpSipConnection         *aSipConnection   ,
                    INGwSipMethodType             aMethodType      ,
                    Sdf_st_callObject      **ppCallObj        ,
                    Sdf_st_eventContext     *pEventContext    ,
                    Sdf_st_error            *pErr             ,
                    Sdf_st_overlapTransInfo *pOverlapTransInfo = NULL);

    Sdf_ty_retVal stackCallbackResponse
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   int                      aRespCode        ,
                   Sdf_st_callObject      **ppCallObj        ,
                   Sdf_st_eventContext     *pEventContext    ,
                   Sdf_st_error            *pErr             ,
                   Sdf_st_overlapTransInfo *pOverlapTransInfo = NULL);

    Sdf_ty_retVal stackCallbackAck
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   Sdf_st_callObject      **ppCallObj        ,
                   Sdf_st_eventContext     *pEventContext    ,
                   Sdf_st_error            *pErr             );

    void          indicateTimeout
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   INGwSipTimerType::TimerType aType           ,
                   unsigned int              aTimerid        );

    int           mSendRequest
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   INGwSpData                *aGwData          );

    int           mSendResponse
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   INGwSpData                *aGwData          ,
                   int                      aCode);

    int           mSendAck
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   INGwSpData                *aGwData          );

    void setLastOperation(LastOperation aLastop);

    std::string toLog() const;
    int mSeqNum;
  private:
    int getDialogueId(unsigned char* pMsgBody);
    LastOperation mLastop;
    unsigned int  muiIvrTimeout;
		std::string m_strHost;
		int m_Port;

}; // end of INGwSpMsgNotifyHandler

#endif //INGW_SP_MSG_NOTIFY_HANDLER_H_

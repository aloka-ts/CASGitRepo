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
//     File:     INGwSpMsgByeHandler.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************


#ifndef INGW_SP_MSG_BYE_HANDLER_H_
#define INGW_SP_MSG_BYE_HANDLER_H_

#include <INGwSipProvider/INGwSpData.h>

#include <INGwSipProvider/INGwSpSipIncludes.h>
#include <INGwSipProvider/INGwSpSipContext.h>
#include <INGwSipMsgHandler/INGwSpMsgByeStateContext.h>

#include <string>
#include <INGwSipMsgHandler/INGwSpMsgBaseHandler.h>

#include <INGwSipProvider/INGwSpSipCommon.h>

class INGwSpMsgByeHandler : public INGwSpMsgBaseHandler
{
  public:
    INGwSpMsgByeHandler();
    ~INGwSpMsgByeHandler();
    void reset(void);

    short disconnect(INGwSpSipConnection *aSipConnection, int aErrCode);

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

   void           indicateTimeout
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

    void          setFailedByePending
                  (bool                     aPending         );

    short         disconnectWithAlso
                  (INGwSpSipConnection    *aSipConnection   ,
                   const char         *aAccountCode     );

    void          receiveRedirectionInfo
                  (INGwSpSipConnection*         aSipConnection   ,
                   int                      aContactCount    ,
                   INGwSipEPInfo*                aContactList     );

     std::string toLog()const;

  private:
    INGwSpMsgByeStateContext mByeStateContext;
    Sdf_st_callObject *mTempCallObj;

   public:
      friend class INGwSpSipConnection;
}; // end of 

#endif //INGW_SP_MSG_BYE_HANDLER_H_

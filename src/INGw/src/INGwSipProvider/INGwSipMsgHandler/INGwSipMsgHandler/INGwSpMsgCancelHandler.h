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
//     File:     INGwSpMsgCancelHandler.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef INGW_SP_MSG_CANCEL_HANDLER_H_
#define INGW_SP_MSG_CANCEL_HANDLER_H_

#include <INGwSipMsgHandler/INGwSpMsgBaseHandler.h>


#include <string>
#include <INGwSipProvider/INGwSpSipCommon.h>

#include <INGwSipProvider/INGwSpSipIncludes.h>
#include <INGwSipProvider/INGwSpStackTimer.h>

class INGwSpSipConnection;
class INGwSpData;

class INGwSpMsgCancelHandler : public INGwSpMsgBaseHandler
{
  public:
    INGwSpMsgCancelHandler();
    ~INGwSpMsgCancelHandler();

    Sdf_ty_retVal stackCallbackRequest(
                    INGwSpSipConnection     *aSipConnection   ,
                    INGwSipMethodType        aMethodType      ,
                    Sdf_st_callObject      **ppCallObj        ,
                    Sdf_st_eventContext     *pEventContext    ,
                    Sdf_st_error            *pErr             ,
                    Sdf_st_overlapTransInfo *pOverlapTransInfo = NULL);

    Sdf_ty_retVal stackCallbackResponse
                  (INGwSpSipConnection     *aSipConnection   ,
                   INGwSipMethodType        aMethodType      ,
                   int                      aRespCode        ,
                   Sdf_st_callObject      **ppCallObj        ,
                   Sdf_st_eventContext     *pEventContext    ,
                   Sdf_st_error            *pErr             ,
                   Sdf_st_overlapTransInfo *pOverlapTransInfo = NULL);

    Sdf_ty_retVal stackCallbackAck
                  (INGwSpSipConnection     *aSipConnection   ,
                   INGwSipMethodType        aMethodType      ,
                   Sdf_st_callObject      **ppCallObj        ,
                   Sdf_st_eventContext     *pEventContext    ,
                   Sdf_st_error            *pErr             );

   void           indicateTimeout
                  (INGwSpSipConnection          *aSipConnection    ,
                   INGwSipMethodType             aMethodType       ,
                   INGwSipTimerType::TimerType   aType             ,
                   unsigned int                  aTimerid          );

    int           mSendRequest
                  (INGwSpSipConnection       *aSipConnection   ,
                   INGwSipMethodType          aMethodType      ,
                   INGwSpData                *aGwData          );

    int           mSendResponse
                  (INGwSpSipConnection       *aSipConnection   ,
                   INGwSipMethodType          aMethodType      ,
                   INGwSpData                *aGwData          ,
                   int                        aCode);

    int           mSendAck
                  (INGwSpSipConnection       *aSipConnection   ,
                   INGwSipMethodType          aMethodType      ,
                   INGwSpData                *aGwData          );
 
    int           sendCancel(INGwSpSipConnection      *aSipConnection   ,
                             INGwSipMethodType         aMethodType      ,
                             bool                      aIsByePending    );

    bool          isTransactionComplete();
    void          setByePending(bool aPending);
    bool          stopTransComplTimer();

    std::string toLog()const;

  private:
    bool isCancelStarted;
    bool isByePending;
    unsigned int muTransComplTimerid;
}; // end of INGwSpMsgCancelHandler

#endif //INGW_SP_MSG_CANCEL_HANDLER_H_

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
//     File:     INGwSpMsgBaseHandler.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef INGW_SP_MSG_BASE_HANDLER_H_
#define INGW_SP_MSG_BASE_HANDLER_H_

class INGwSpSipConnection;
class INGwSpData;

#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpSipIncludes.h>

typedef int TerminateType;
extern const int TERMINATE_BYE; 
extern const int TERMINATE_CANCEL; 

class INGwSpMsgBaseHandler
{
  public:
    virtual void reset(void);
    virtual ~INGwSpMsgBaseHandler() = 0;

    virtual Sdf_ty_retVal stackCallbackRequest(
                    INGwSpSipConnection         *aSipConnection   ,
                    INGwSipMethodType             aMethodType      ,
                    Sdf_st_callObject      **ppCallObj        ,
                    Sdf_st_eventContext     *pEventContext    ,
                    Sdf_st_error            *pErr             ,
                    Sdf_st_overlapTransInfo *pOverlapTransInfo = NULL) = 0;

    virtual Sdf_ty_retVal stackCallbackResponse
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   int                      aRespCode        ,
                   Sdf_st_callObject      **ppCallObj        ,
                   Sdf_st_eventContext     *pEventContext    ,
                   Sdf_st_error            *pErr             ,
                   Sdf_st_overlapTransInfo *pOverlapTransInfo = NULL) = 0;

    virtual Sdf_ty_retVal stackCallbackAck
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   Sdf_st_callObject      **ppCallObj        ,
                   Sdf_st_eventContext     *pEventContext    ,
                   Sdf_st_error            *pErr             )        = 0;

    virtual int           
    mSendRequest(
      INGwSpSipConnection* aSipConnection,
      INGwSipMethodType     aMethodType,
      INGwSpData*        aGwData ) = 0;

    virtual int           mSendResponse
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   INGwSpData                *aGwData          ,
                   int                      aCode            ) = 0;

    virtual int           mSendAck
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   INGwSpData                *aGwData          )        = 0;

    virtual bool          terminateTransaction
                  (INGwSpSipConnection *aSipConnection,
                   int              aErrcode      ,
                   TerminateType    aTermType     );

    virtual bool          terminateServerTransaction
                  (INGwSpSipConnection *aSipConnection, INGwSipMethodType aMethodType,
                   Sdf_st_overlapTransInfo *pOverlapTransInfo = 0);

    virtual void          receiveRedirectionInfo
                  (INGwSpSipConnection *aSipConnection,
                   int              aContactCount,
                   INGwSipEPInfo*        aContactList);

}; // end of INGwSpMsgBaseHandler

#endif  

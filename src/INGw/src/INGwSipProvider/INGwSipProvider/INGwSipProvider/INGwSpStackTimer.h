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
//     File:     INGwSpStackTimer.h
//
//     Desc:
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef INGW_SP_STACK_TIMER_H_
#define INGW_SP_STACK_TIMER_H_

#include <string>

#include <INGwSipProvider/INGwSpSipIncludes.h>
#include <INGwSipProvider/INGwSpSipContext.h>

class INGwSpStackTimer;

#define BP_SIP_NO_OF_HASH_BUCKETS_STACKTIMERMAP  39999
#define BP_SIP_MAX_HSS_TIMEROBJECTS              80000

// This structure contains the sip timer key and buffer, so that when stop timer
// is called, the buffer and the timer key can be returned back to stack for
// destruction.
class INGwSpStackTimerContext : public INGwSipTimerType
{
   public:
     void dummy(void){}
   private:

      friend class INGwSpStackTimer;

      INGwSpStackTimerContext(SipTimerKey *aKey, void *aBuf, unsigned int aTimerId,
                          sip_timeoutFuncPtr aTimeoutFunc) :
         mpKey(aKey),
         mBuf(aBuf),
         mTimerId(aTimerId),
         mTimeoutFunc(aTimeoutFunc)
      {
         mType = STACK_TIMER;
      }

      ~INGwSpStackTimerContext()
      {
      }

      SipTimerKey       *mpKey       ;
      void              *mBuf        ;
      unsigned int       mTimerId    ;
      sip_timeoutFuncPtr mTimeoutFunc;
}; 

class INGwSpSessionTimerContext;

class INGwSpSessionTmrStackHandle {

public:
  unsigned int mTimerId;
  void*        pContextInfo;
  INGwSpSessionTimerContext* pSessionTmrCtx;
  unsigned int mCheckSum;

//BPInd16293
  INGwSpSessionTmrStackHandle() {
    mTimerId = 0;
    pContextInfo = Sdf_co_null;
    pSessionTmrCtx = 0;
    mCheckSum = 0;
  }

};

class INGwSpSessionTimerContext : public INGwSipTimerType {

  public:
    INGwSpSessionTimerContext() {
      mType = SESSION_TIMER;
    }

    ~INGwSpSessionTimerContext() {
    }

    void dummy(void){}

    Sdf_ty_timerType mTimertype;
    Sdf_ty_TimertimeOutFunc mpTimeoutFunc;
    INGwSpSessionTmrStackHandle mStackTmrHandle;

}; // end of INGwSpSessionTimerContext

typedef Sdf_st_hash StackTimerMap;

class INGwSpStackTimer
{
  public:
    INGwSpStackTimer();
    ~INGwSpStackTimer();

    bool startTransComplTimer
      (unsigned int &arTimerid  ,
       int           aDuration  ,
       const std::string       &arCallid   ,
       INGwSipMethodType  aMethodType);
    bool stopTransComplTimer
      (unsigned int &aTimerid);

    bool startInitRespTimer
      (unsigned int &arTimerid  ,
       int           aDuration  ,
       const std::string       &arCallid   ,
       INGwSipMethodType  aMethodType);
    bool stopInitRespTimer
      (unsigned int &aTimerid);

    bool startNoAnswerTimer
      (unsigned int &arTimerid  ,
       int           aDuration  ,
       const std::string       &arCallid   ,
       INGwSipMethodType  aMethodType,
       short         aConnId    );
    bool stopNoAnswerTimer
      (unsigned int &aTimerid);

    bool startSipIvrTimer
      (unsigned int &arTimerid  ,
       int           aDuration  ,
       const std::string       &arCallid   );
    bool stopSipIvrTimer
      (unsigned int &aTimerid);

    bool indicateTimeout
      (INGwSpStackTimerContext *aMsg);

    bool startTimer
      (SipTimerKey*        aKey        ,
       void*               aBuffer     ,
       int                 aDuration   ,
       sip_timeoutFuncPtr  aTimeoutFunc);
    bool stopTimer
      (SipTimerKey*   aInKey ,
       SipTimerKey**  aOutKey,
       void**         aOutBuf);

    bool startRemoteRetransPurgeTimer
      (unsigned int aDuration);

    bool startSessionTimer
      (unsigned int     aDuration,
       Sdf_ty_timerType aTimertype,
       Sdf_ty_pvoid     aContextInfo,
       Sdf_ty_pvoid*    ppTimerHandle,
       Sdf_ty_TimertimeOutFunc aTimeoutFunc);
    bool stopSessionTimer
      (Sdf_ty_pvoid  pTimerHandle,
       Sdf_ty_pvoid* ppContextInfo);
    int getCount();

  private:
    bool startAppTimer
      (unsigned int &arTimerid  ,
       int           aDuration  ,
       const std::string       &arCallid   ,
       INGwSipMethodType  aMethodType,
       INGwSipTimerType::TimerType aType,
       short         aConnId = -1);

    bool stopAppTimer
      (unsigned int &aTimerid);

    bool 
    put(INGwSpStackTimerContext* aContext);

    bool
    remove(SipTimerKey* aKey,
      INGwSpStackTimerContext** pObject);

    StackTimerMap* mTimerMap;
};

#endif

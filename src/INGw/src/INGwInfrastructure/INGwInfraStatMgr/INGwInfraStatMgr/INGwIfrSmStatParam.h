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
//     File:     INGwIfrMgrAgentClbkImpl.C
//
//     Desc:    An object of this class represents one statistics parameter.
//              This class provides methods to update and retrieve statistics
//              parameter values, and to configure the parameters.  These
//              objects are guarded for all value accesses.
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev arya    07/12/07     Initial Creation
//********************************************************************
#ifndef INGW_IFR_STAT_PARAM_H_
#define INGW_IFR_STAT_PARAM_H_

#include <INGwInfraStatMgr/INGwIfrSmCommon.h>
#include <INGwInfraStatMgr/INGwIfrSmStatThreshold.h>
#include <pthread.h>

// An object of this class represents one statistics parameter.  This
// class provides methods to update and retrieve statistics parameter
// values, and to configure the parameters.  These objects are
// guarded for all value accesses.
class INGwIfrSmStatParam
{
  public:
    // Initializes the parameter object.
    INGwIfrSmStatParam(int aIndex = 0);
    ~INGwIfrSmStatParam();

    // These methods flag this parameter as either an EMS parameter or
    // not.  When the EMS starts interacting with the statistics
    // manager, it will only poll those parameters that are meant for
    // EMS.  There may be additional parameters meant for only telnet
    // interface in which the EMS will not be interested.
    bool isEmsParam();
    void setEmsParam(bool aVal);

    // These methods flag a parameter as deferred processing or not.
    // The deferred processing thread will only check parameters which
    // are marked for such processing.
    // The threshold checking in the worker thread (instantaneous) is
    // done only for those parameters which are not marked for deferred
    // processing.
    bool isDeferredProcessingParam();
    void setDeferredProcessingParam(bool aVal);

    // These methods flag a parameter as empty or not.  Although the
    // list of parameters in the statistics manager is fully filled
    // with statistics parameters, not all those parameters are
    // actually used.
    bool empty();
    void setEmpty(bool aVal);

    void setAvg(bool aAvg);
    void setMinMax(bool aMinMax);
    void setSnapShot(bool aSnapShot);

    bool getAvg();
    bool getMinMax();
    bool getSnapShot();

    bool getResetOnRead();
    void setResetOnRead(bool aVal);

    /****************************************************************/
    // Methods for updating and retrieving the statistics value.

    // The methods for setting values affect both the EMS readable
    // value and the internal value of the parameter.
    ThresholdIndication setValue (int aVal);
    ThresholdIndication increment(int& aCurValue, int aVal);
    ThresholdIndication decrement(int& aCurValue, int aVal);

    // This method retrieves the EMS readable value (all of the actual,
    // minimum and maximum values).  These values are exclusively
    // used outside of statistics manager, probably for some form of
    // display, by EMS and telnet interface.
    void getValue(int& aCurValue,
                  int& aAvgValue,
                  int& aMaxValue,
                  int& aMinValue);

    // This method gets the internal value of this parameter, and is
    // exclusively used for internal calculations.  Threshold objects
    // may read this value to keep a history, time difference, or
    // both.
    // The first output parameter will be the current value for
    // cumulative parameters, and will be the average (since last get)
    // for averaged values.
    void getInternalValue(int&    aValue     ,
                          int&    aMaxValue  ,
                          int&    aMinValue  ,
                          bool&   aIsSnapShot);

    /****************************************************************/

    // Methods to guard the parameters
    void lock();
    void unlock();

    // This method checks the current internal value of the parameter
    // against all the thresholds and causes any actions specified if
    // any threshold excess occurs.  This method can be called by the
    // deferred thread, or by the INGwIfrSmStatParam itself, if the parameter is
    // marked for instantaneous processing.
    ThresholdIndication checkThreshold();

    // This method adds a threshold to the parameter.  It returns a false
    // if the addition failed (because the maximum number of thresholds
    // were crossed)
    bool addThreshold(INGwIfrSmStatThreshold* aThreshold);

    void replace(INGwIfrSmStatParam& aParam);
    std::string getOid();
    void setOid(std::string aOid);
    void dump(std::string& aOutput);

  private:
    pthread_mutex_t mLock;
    bool mbEmpty;

    // The list of threshold objects.
    INGwIfrSmStatThreshold** mThresholdList;
    int miThresholdCount;

    INGwIfrSmStatValue mInternalValue;
    INGwIfrSmStatValue mEmsValue;

    int miIndex;
    std::string mOid;

    bool mbSnapShot;
    bool mbMinMax;
    bool mbAvg;
    bool mbEmsParam;
    bool mbDeferred;
    bool mbResetOnRead;

    // The EMS may reset values from time to time for its own use.
    // This reset method will only reset EMS readable value, and not
    // the internal value.
    void resetValue(INGwIfrSmStatValue& aValue);

    friend class INGwIfrSmStatMgr;
    void setIndex(int aIndex);
}; // End of INGwIfrSmStatParam class

#endif

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
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev arya    07/12/07     Initial Creation
//********************************************************************
#ifndef INGW_IFR_STAT_THRESHOLD_H_
#define INGW_IFR_STAT_THRESHOLD_H_

#include <string>

#include <INGwInfraStatMgr/INGwIfrSmCommon.h>
#include <INGwInfraStatMgr/INGwIfrSmStatValue.h>

class INGwIfrSmStatThreshold
{
  public:
    INGwIfrSmStatThreshold(ThresholdType    aType,
                  int              aLowThreshold,
                  int              aHighThreshold,
                  std::string           aAssociatedOid,
                  ThresholdAction  aThresholdAction,
                  int              aAlarmId,
                  std::string           aAlarmMsg,
                  int              aiTimeDiffDuration);

    // This method checks whether the threshold has been crossed for
    // the given value, and if so, takes the specified action.  If the
    // threshold WAS crossed, it indicates it through the return value.
    ThresholdIndication checkThreshold(INGwIfrSmStatValue& aValue, bool aIsSnapShot);

    void dump(std::string& aOutput);

  private:
    // The type of threshold
    ThresholdType meThresholdType;

    // High-low watermark values
    int miHighThreshold;
    int miLowThreshold;

    // The index of the associated parameter, in case of a percentage
    // based threshold
    int miAssociatedId;
    std::string mAssociatedOid;

    // The action to take if threshold is exceeded, whether to log or
    // raise an alarm.
    ThresholdAction meThresholdAction;

    // Alarm related information
    int miAlarmId;
    std::string mstrAlarmMessage;

    // Duration in case of time difference type of threshold
    int miTimeDifference;

    // List of values of this parameter and the associated parameter
    // in case of time difference and percentage thresholds.  These
    // lists are used as circular lists, and when a new value is
    // retrieved, the value at the current index is replaced with the
    // new value and the index incremented.
    int nValues;
    INGwIfrSmStatValue* selfValue;
    INGwIfrSmStatValue* associatedValue;
    int currentValueIndex;

    // Flag to indicate that the high threshold was already crossed.
    // When this happens for the first time, the flag will be set and the
    // action taken.  Next time, if the flag is already set, the action
    // will not be taken.  The flag will be reset when the value goes
    // below the low threshold.  This whole state maintenance is done
    // only when a valid low threshold is specified.  Otherwise, action
    // will be taken everytime the high threshold gets crossed.
    bool mbHighThresholdCrossed;
}; // End of INGwIfrSmStatThreshold class

#endif

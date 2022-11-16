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
#include <INGwInfraUtil/INGwIfrUtlLogger.h>

#include <INGwInfraStatMgr/INGwIfrSmCommon.h>
BPLOG("StatMgr");

#include <INGwInfraManager/INGwIfrMgrAlarmMgr.h>
#include <INGwInfraStatMgr/INGwIfrSmStatMgr.h>
#include <INGwInfraStatMgr/INGwIfrSmStatThreshold.h>

using namespace std;

INGwIfrSmStatThreshold::INGwIfrSmStatThreshold
  (ThresholdType    aType,
   int              aLowThreshold,
   int              aHighThreshold,
   string           aAssociatedOid,
   ThresholdAction  aThresholdAction,
   int              aAlarmId,
   string           aAlarmMsg,
   int              aiTimeDiffDuration)
{
  mbHighThresholdCrossed = false;
  nValues                = -1;
  currentValueIndex      = -1;
  selfValue              = NULL;
  associatedValue        = NULL;

  meThresholdType   = aType;
  miHighThreshold   = aHighThreshold;
  miLowThreshold    = aLowThreshold;
  mAssociatedOid    = aAssociatedOid;
  miAssociatedId    = -1;
  meThresholdAction = aThresholdAction,
  miAlarmId         = aAlarmId;
  mstrAlarmMessage  = aAlarmMsg;
  miTimeDifference  = aiTimeDiffDuration;

  logger.logINGwMsg(false, VERBOSE_FLAG, 0, "INGwIfrSmStatThreshold: mAssociatedOid <%s>", mAssociatedOid.c_str());

  // If the threshold type is timediff or percent_timediff, calculate the
  // number of values we need to keep for the time difference calculation.
  if((meThresholdType == Thresh_TimeDiff) ||
     (meThresholdType == Thresh_TimeDiff_Percent))
  {
    nValues = aiTimeDiffDuration/INGwIfrSmStatMgr::instance().getDeferredDuration();
    if(nValues <= 0)
      nValues = 1;
    logger.logINGwMsg(false, VERBOSE_FLAG, 0, "INGwIfrSmStatThreshold: number of values: <%d>", nValues);

    currentValueIndex = 0;
    selfValue       = new INGwIfrSmStatValue[nValues];
    associatedValue = new INGwIfrSmStatValue[nValues];
  }
} // end of constructor

ThresholdIndication INGwIfrSmStatThreshold::checkThreshold
  (INGwIfrSmStatValue& aValue, bool aIsSnapShot)
{
  int valueForComparison = -1;
  ThresholdIndication retval = NoThresholdExceeded;

  // Get the final value to be checked for threshold crossing, and then
  // only check for the excess.

  // The simplest case is for the absolute.  If the value is a snapshot,
  // then compare with the maximum.  Else, compare with the current value.
  if(meThresholdType == Thresh_Absolute)
  {
    if(aIsSnapShot)
      valueForComparison = aValue.maxValue;
    else
      valueForComparison = aValue.currValue;
  }
  else if(meThresholdType == Thresh_TimeDiff)
  {
    // Copy the value into the list of values.
    selfValue[currentValueIndex].currValue = aValue.currValue;
    selfValue[currentValueIndex].minValue  = aValue.minValue;
    selfValue[currentValueIndex].maxValue  = aValue.maxValue;

    // Calculate the total time difference between this value and the
    // oldest value
    int previousValueIndex = currentValueIndex - 1;
    if(previousValueIndex < 0)
      previousValueIndex = nValues -1;

    valueForComparison = selfValue[currentValueIndex].currValue -
                         selfValue[previousValueIndex].currValue;

    // Increment the index of current self value, and rollover (the selfvalue
    // array is actually a circular list) if more than the number of
    // values.
    ++currentValueIndex;
    if(currentValueIndex>= nValues)
      currentValueIndex %= nValues;
  }
  else if((meThresholdType == Thresh_Percent) ||
          (meThresholdType == Thresh_TimeDiff_Percent))
  {
    // Get the internal value of the associated parameter, and calculate the
    // percentage.
    int  assocValue      = -1;
    int  assocMinValue   = -1;
    int  assocMaxValue   = -1;
    bool isAssocSnapShot = false;

    if(miAssociatedId < 0)
      miAssociatedId = INGwIfrSmStatMgr::instance().getParamIndex(mAssociatedOid, false);

    if(miAssociatedId < 0)
    {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "checkThreshold: Error getting the index for parameter oid <%s>", mAssociatedOid.c_str());
      return retval;
    }

    INGwIfrSmStatMgr::instance().getInternalValue(miAssociatedId,
                                         assocValue,
                                         assocMaxValue,
                                         assocMinValue,
                                         isAssocSnapShot);

    if(meThresholdType == Thresh_Percent)
    {
      // Calculate the percentage
      if(assocValue)
        valueForComparison = aValue.currValue * 100 / assocValue;
      else
        return NoThresholdExceeded;
    }
    else
    {
      // Copy the values into the list of values.
      selfValue[currentValueIndex].currValue = aValue.currValue;
      associatedValue[currentValueIndex].currValue = assocValue;

      // Calculate the total time difference between this value and the
      // oldest value
      int previousValueIndex = currentValueIndex - 1;
      if(previousValueIndex < 0)
        previousValueIndex = nValues -1;

      int selfDifference  = selfValue[currentValueIndex].currValue -
                            selfValue[previousValueIndex].currValue;
      int assocDifference = associatedValue[currentValueIndex].currValue -
                            associatedValue[previousValueIndex].currValue;

      if(assocDifference)
        valueForComparison = selfDifference * 100 / assocDifference;
      else
        return NoThresholdExceeded;

      // Increment the index of current self value, and rollover (the value
      // arrays are actually circular lists) if more than the number of
      // values.
      ++currentValueIndex;
      if(currentValueIndex>= nValues)
        currentValueIndex %= nValues;
    }
  } // end of else

  if(valueForComparison >= 0)
  {
    if(valueForComparison > miHighThreshold)
    {
      retval = UpperThresholdExceeded;
  
      bool takeAction = false;
  
      if(miLowThreshold >= 0)
      {
        if(!mbHighThresholdCrossed)
        {
          mbHighThresholdCrossed = true;
          takeAction = true;
        }
      }
      else
        takeAction = true;
  
      if(takeAction)
      {
        if(meThresholdAction == ThreshAct_Log)
        {
          logger.logINGwMsg(false, ERROR_FLAG, 0, "checkThreshold: Exceeded higher threshold: <%s>", mstrAlarmMessage.c_str());
        }
        else if(meThresholdAction == ThreshAct_Alarm)
        {
          INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::CONFIGURATION,
                                                __FILE__, __LINE__, miAlarmId, 
                                                "StatMgr", 0, 
                                                mstrAlarmMessage.c_str());

        }
      }
    } // end of if
    else if((miLowThreshold >= 0) && (valueForComparison < miLowThreshold))
    {
      // Reset the threshold crossed flag
      mbHighThresholdCrossed = false;
  
      retval = LowerThresholdExceeded;
    }
  } // end of if

  return retval;
} // end of checkThreshold method

void INGwIfrSmStatThreshold::dump(string& aOutput)
{
  char tempStr[128];

  sprintf(tempStr, "meThresholdType          : %d\n        ", meThresholdType);
  aOutput += tempStr;

  sprintf(tempStr, "miHighThreshold          : %d\n        ", miHighThreshold);
  aOutput += tempStr;

  sprintf(tempStr, "miLowThreshold           : %d\n        ", miLowThreshold);
  aOutput += tempStr;

  sprintf(tempStr, "miAssociatedId           : %d\n        ", miAssociatedId);
  aOutput += tempStr;

  sprintf(tempStr, "mAssociatedOid           : %s\n        ", mAssociatedOid.c_str());
  aOutput += tempStr;

  sprintf(tempStr, "meThresholdAction        : %d\n        ", meThresholdAction);
  aOutput += tempStr;

  sprintf(tempStr, "miAlarmId                : %d\n        ", miAlarmId);
  aOutput += tempStr;

  sprintf(tempStr, "mstrAlarmMessage         : %s\n        ", mstrAlarmMessage.c_str());
  aOutput += tempStr;

  sprintf(tempStr, "miTimeDifference         : %d\n        ", miTimeDifference);
  aOutput += tempStr;

  sprintf(tempStr, "mbHighThresholdCrossed   : %d\n        ", mbHighThresholdCrossed);
  aOutput += tempStr;

  sprintf(tempStr, "nValues                  : %d\n", nValues);
  aOutput += tempStr;

  for(int i = 0; i < nValues; ++i)
  {
    sprintf(tempStr, "        Value %d\n", i);
    aOutput += tempStr;
    selfValue[i].dump(aOutput);
    aOutput += "\n";
  }

  if(meThresholdType == Thresh_TimeDiff_Percent)
  {
    aOutput += "\n        Associated Values\n";
    for(int i = 0; i < nValues; ++i)
    {
      sprintf(tempStr, "        Value %d\n", i);
      aOutput += tempStr;
      selfValue[i].dump(aOutput);
      aOutput += "\n";
    }
  }
} // end of dump

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

#include <INGwInfraStatMgr/INGwIfrSmStatParam.h>

using namespace std;

// Initializes the parameter object.
INGwIfrSmStatParam::INGwIfrSmStatParam(int aIndex)
{
   pthread_mutex_init(&mLock, NULL);
  miIndex = aIndex;
  miThresholdCount = 0;
  mThresholdList = 0;
  mbSnapShot = false;
  mbMinMax = true;
  mbAvg = false;
  mbEmsParam = true;
  mbDeferred = true;
  mbResetOnRead = false;
  mbEmpty = true;

  // By default, the values are not snapshot
  setSnapShot(false);

  // By default, no min/max required
  setMinMax(true);

  // By default, no average required
  setAvg(false);
  
  // By default, not an EMS parameter
  setEmsParam(true);

  // By default, for deferred processing
  setDeferredProcessingParam(true);

  // Empty on creation
  setEmpty(true);

  // By default, do not reset on read
  //setResetOnRead(false);
} // end of constructor

INGwIfrSmStatParam::~INGwIfrSmStatParam()
{
   pthread_mutex_destroy(&mLock);
}

bool INGwIfrSmStatParam::isEmsParam()
{
  return mbEmsParam;
}

void INGwIfrSmStatParam::setEmsParam(bool aVal)
{
  mbEmsParam = aVal;
}

bool INGwIfrSmStatParam::isDeferredProcessingParam()
{
  return mbDeferred;
}

void INGwIfrSmStatParam::setDeferredProcessingParam(bool aVal)
{
  mbDeferred = aVal;
}

bool INGwIfrSmStatParam::empty()
{
  return mbEmpty;
}

void INGwIfrSmStatParam::setEmpty(bool aVal)
{
  mbEmpty = aVal;
}

ThresholdIndication INGwIfrSmStatParam::setValue(int aVal)
{
  mEmsValue.setValue(aVal);
  mInternalValue.setValue(aVal);

  if(isDeferredProcessingParam())
    return NoThresholdExceeded;
  else
    return checkThreshold();
}

ThresholdIndication INGwIfrSmStatParam::increment(int& aCurValue, int aVal)
{
  int dummy = 0;
  mEmsValue.increment(aCurValue, aVal);
  mInternalValue.increment(dummy, aVal);

  if(isDeferredProcessingParam())
    return NoThresholdExceeded;
  else
    return checkThreshold();
}

ThresholdIndication INGwIfrSmStatParam::decrement(int& aCurValue, int aVal)
{
  int dummy = 0;
  mEmsValue.decrement(aCurValue, aVal);
  mInternalValue.decrement(dummy, aVal);

  if(isDeferredProcessingParam())
    return NoThresholdExceeded;
  else
    return checkThreshold();
}

void INGwIfrSmStatParam::getValue
  (int& aCurValue,
   int& aAvgValue,
   int& aMaxValue,
   int& aMinValue)
{
  aCurValue = mEmsValue.currValue;
  if(mEmsValue.numberOfReads)
    aAvgValue = mEmsValue.totalValue/mEmsValue.numberOfReads;
  else
    aAvgValue = 0;
  aMinValue = mEmsValue.minValue;
  aMaxValue = mEmsValue.maxValue;

  if(getResetOnRead())
    resetValue(mEmsValue);
}

void INGwIfrSmStatParam::getInternalValue(int&    aValue     ,
                                 int&    aMaxValue  ,
                                 int&    aMinValue  ,
                                 bool&   aIsSnapShot)
{
  if(getAvg())
    if(mInternalValue.numberOfReads)
      aValue = mInternalValue.totalValue/mInternalValue.numberOfReads;
    else
      aValue = 0;
  else
    aValue = mInternalValue.currValue;

  if(getMinMax())
  {
    aMaxValue = mInternalValue.maxValue;
    aMinValue = mInternalValue.minValue;
  }
  aIsSnapShot = getSnapShot();

  // If the internal value is not cumulative, reset it.
  if(aIsSnapShot)
    resetValue(mInternalValue);
}

void INGwIfrSmStatParam::resetValue(INGwIfrSmStatValue& arValue)
{
  if(getAvg())
  {
    arValue.numberOfReads = 0;
    arValue.totalValue = 0;
  }

  if(getSnapShot())
  {
    arValue.numberOfReads = 1;
    arValue.totalValue = arValue.currValue;
  }
  else
    arValue.currValue = 0;

  if(getMinMax())
  {
    arValue.minValue = arValue.currValue;
    arValue.maxValue = arValue.currValue;
  }
}

void INGwIfrSmStatParam::lock()
{
  pthread_mutex_lock(&mLock);
}

void INGwIfrSmStatParam::unlock()
{
  pthread_mutex_unlock(&mLock);
}

ThresholdIndication INGwIfrSmStatParam::checkThreshold()
{
  ThresholdIndication retval = NoThresholdExceeded;

  // Check on all the thresholds.  Even if one of them returns a threshold
  // excess, return an excess.
  for(int i = 0; i < miThresholdCount; ++i)
  {
    ThresholdIndication newIndication =
      mThresholdList[i]->checkThreshold(mInternalValue, mbSnapShot);
    if(retval == NoThresholdExceeded)
      retval = newIndication;
  }

  // If the internal value is not cumulative, reset it.
  if(getSnapShot())
    resetValue(mInternalValue);

  return retval;
}

bool INGwIfrSmStatParam::addThreshold(INGwIfrSmStatThreshold* aThreshold)
{
  // Create a new array of thresholds, move all thresholds from old array to
  // new array, and delete the old array.
  ++miThresholdCount;
  INGwIfrSmStatThreshold** newArray = new INGwIfrSmStatThreshold*[miThresholdCount];
  for(int i = 0; i < miThresholdCount - 1; i++)
    newArray[i] = mThresholdList[i];
  newArray[miThresholdCount - 1] = aThreshold;

  delete[] mThresholdList;
  mThresholdList = newArray;

  return true;
} // end of addThreshold method

void INGwIfrSmStatParam::replace(INGwIfrSmStatParam& aParam)
{
  mbEmpty = false;

  if(mThresholdList)
  {
    for(int i = 0; i < miThresholdCount; ++i)
      delete mThresholdList[i];
    delete[] mThresholdList;
  }

  mThresholdList   = aParam.mThresholdList;
  miThresholdCount = aParam.miThresholdCount;
  aParam.mThresholdList = NULL;

  mbSnapShot    = aParam.mbSnapShot;
  mbMinMax      = aParam.mbMinMax;
  mbAvg         = aParam.mbAvg;
  mbEmsParam    = aParam.mbEmsParam;
  mbDeferred    = aParam.mbDeferred;
  mbResetOnRead = aParam.mbResetOnRead;
  mOid          = aParam.mOid;

  setAvg(mbAvg);
}

void INGwIfrSmStatParam::dump(string& aOutput)
{
  char tempStr[128];

  aOutput += getOid();
  aOutput += "\n===================================\n";
  aOutput += "    mbEmpty          : ";
  sprintf(tempStr, "%d\n    ", mbEmpty);
  aOutput += tempStr;


  sprintf(tempStr, "mbSnapShot       : %d\n    ", mbSnapShot);
  aOutput += tempStr;
  sprintf(tempStr, "mbMinMax         : %d\n    ", mbMinMax);
  aOutput += tempStr;
  sprintf(tempStr, "mbAvg            : %d\n    ", mbAvg);
  aOutput += tempStr;
  sprintf(tempStr, "mbEmsParam       : %d\n    ", mbEmsParam);
  aOutput += tempStr;
  sprintf(tempStr, "mbDeferred       : %d\n    ", mbDeferred);
  aOutput += tempStr;
  sprintf(tempStr, "mbResetOnRead    : %d\n    ", mbResetOnRead);
  aOutput += tempStr;

  sprintf(tempStr, "Internal Value\n");
  aOutput += tempStr;
  mInternalValue.dump(aOutput);

  sprintf(tempStr, "    Ems Value\n");
  aOutput += tempStr;
  mEmsValue.dump(aOutput);

  sprintf(tempStr, "\n    miThresholdCount: %d\n    ", miThresholdCount);
  aOutput += tempStr;
  sprintf(tempStr, "\n    --------------------\n");
  aOutput += tempStr;
  for(int i = 0; i < miThresholdCount; ++i)
  {
    sprintf(tempStr, "\n    Threshold %d\n        ", i);
    aOutput += tempStr;
    mThresholdList[i]->dump(aOutput);
  }

  aOutput += "\n";
}

void INGwIfrSmStatParam::setIndex(int aIndex)
{
  miIndex = aIndex;
}

void INGwIfrSmStatParam::setAvg(bool aAvg)
{
  mbAvg = aAvg;
  mInternalValue.setAggrType(mbSnapShot, mbAvg, mbMinMax);
  mEmsValue.setAggrType(mbSnapShot, mbAvg, mbMinMax);
}

void INGwIfrSmStatParam::setMinMax(bool aMinMax)
{
  mbMinMax = aMinMax;
  mInternalValue.setAggrType(mbSnapShot, mbAvg, mbMinMax);
  mEmsValue.setAggrType(mbSnapShot, mbAvg, mbMinMax);
}

void INGwIfrSmStatParam::setSnapShot(bool aSnapShot)
{
  mbSnapShot = aSnapShot;
  mInternalValue.setAggrType(mbSnapShot, mbAvg, mbMinMax);
  mEmsValue.setAggrType(mbSnapShot, mbAvg, mbMinMax);
}

string INGwIfrSmStatParam::getOid()
{
  return mOid;
}

void INGwIfrSmStatParam::setOid(string aOid)
{
  mOid = aOid;
}

bool INGwIfrSmStatParam::getResetOnRead()
{
  return mbResetOnRead;
}

void INGwIfrSmStatParam::setResetOnRead(bool aVal)
{
  mbResetOnRead = aVal;
  logger.logINGwMsg(false, VERBOSE_FLAG, 0, "setResetOnRead: Setting <%d> for oid <%s>", aVal, mOid.c_str());
}

bool INGwIfrSmStatParam::getAvg()
{
  return mbAvg;
}

bool INGwIfrSmStatParam::getMinMax()
{
  return mbMinMax;
}

bool INGwIfrSmStatParam::getSnapShot()
{
  return mbSnapShot;
}

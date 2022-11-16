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
#include <INGwInfraStatMgr/INGwIfrSmStatValue.h>

using namespace std;

INGwIfrSmStatValue::INGwIfrSmStatValue()
{
  minValue      = -1;
  maxValue      = -1;
  currValue     = 0;

  numberOfReads = 0;
  totalValue    = 0;

  mbSnapShot    = false;;
  mbAvg         = false;
  mbMinMax      = false;
}

void INGwIfrSmStatValue::dump(string& aOutput)
{
  char tempStr[128];

  sprintf(tempStr, "            CurrentValue      : %4d\n            ", currValue);
  aOutput += tempStr;
  sprintf(tempStr, "MinimumValue      : %4d\n            ", minValue);
  aOutput += tempStr;
  sprintf(tempStr, "MaximumValue      : %4d\n            ", maxValue);
  aOutput += tempStr;
  sprintf(tempStr, "NumberOfReads     : %4d\n            ", numberOfReads);
  aOutput += tempStr;
  sprintf(tempStr, "TotalValue        : %4d\n            \n", totalValue);
  aOutput += tempStr;
}

void INGwIfrSmStatValue::setAggrType(bool aSnapShot, bool aAvg, bool aMinMax)
{
  mbSnapShot = aSnapShot;
  mbAvg      = aAvg;
  mbMinMax   = aMinMax;
}

void INGwIfrSmStatValue::increment(int& aCurValue, int aVal)
{
  currValue += aVal;

  if(mbAvg)
  {
    ++numberOfReads;
    totalValue += aVal;
  }

  if((mbMinMax) && (currValue > maxValue))
    maxValue = currValue;

  aCurValue = currValue;
}

void INGwIfrSmStatValue::decrement(int& aCurValue, int aVal)
{
  currValue -= aVal;

  if(mbAvg)
  {
    ++numberOfReads;
    totalValue -= aVal;
  }

  if((mbMinMax) && (currValue < minValue))
    minValue = currValue;

  aCurValue = currValue;
}

void INGwIfrSmStatValue::setValue(int aVal)
{
  currValue = aVal;

  if(mbAvg)
  {
    ++numberOfReads;
    totalValue += aVal;
  }

  if(mbMinMax)
  {
    if(currValue > maxValue)
      maxValue = currValue;
    if((currValue < minValue) || (minValue < 0))
      minValue = currValue;
  }
}

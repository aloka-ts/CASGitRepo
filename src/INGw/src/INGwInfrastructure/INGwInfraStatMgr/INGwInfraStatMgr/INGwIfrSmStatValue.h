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
#ifndef INGW_IFR_STAT_VALUE_H_
#define INGW_IFR_STAT_VALUE_H_

#include <string>

#include <INGwInfraStatMgr/INGwIfrSmCommon.h>

// This class maintains a value of a particular statistics parameter.
// This class provides methods to retrieve the values in std::string forms,
// and in integer forms.
class INGwIfrSmStatValue
{
  public:
    INGwIfrSmStatValue();

    void setAggrType(bool aSnapShot, bool aAvg, bool aMinMax);

    // Methods to get and set the value
    void increment(int& aCurValue, int aVal);
    void decrement(int& aCurValue, int aVal);
    void setValue(int aVal);

    void INGwIfrSmStatValue::dump(std::string& aOutput);

  public:
    int minValue;
    int maxValue;
    int currValue;

    // In case of average type of aggregation, the average is
    // calculated not on every set, but when a get is done.  Till then,
    // the sum of all the set values is simply stored and so is the
    // number of times the set operation was done.  When it is time to
    // calculate the average, the totalValue is divided by the number
    // of times the value was set.
    int numberOfReads;
    int totalValue;

    bool mbSnapShot;
    bool mbAvg;
    bool mbMinMax;
}; // End of INGwIfrSmStatValue

#endif

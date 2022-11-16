#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraMsrMgr");
/************************************************************************
     Name:     Measurement Value - implementation

     Type:     C implementation file

     Desc:     This file provides implementation for Measurement Value Node

     File:     MsrValue.C

     Sid:      MsrValue.C 0  -  11/14/03

     Prg:      gs

************************************************************************/

#include <INGwInfraMsrMgr/MsrIncludes.h>
#include <INGwInfraMsrMgr/MsrValue.h>

//C'tor
MsrValue::MsrValue ():
mulValue (0),
miStatus (0)
{
}

//D'tor
MsrValue::~MsrValue()
{
  mulValue = miStatus = 0;
}

int 
MsrValue::setStatus (int aiStatus)
{
  miStatus = aiStatus;

  return MSR_SUCCESS;
}

unsigned long 
MsrValue::getValue ()
{
  return mulValue;
}

int 
MsrValue::getStatus ()
{
  return miStatus;
}




MsrInstantValue::MsrInstantValue ():
mulMaxValue (0),
mulMinValue (0)
{
}

MsrInstantValue::~MsrInstantValue ()
{
  mulMaxValue = mulMinValue = 0;
}

int
MsrInstantValue::increment (unsigned long aiFactor)
{
  mulValue += aiFactor;

  if (mulMaxValue < mulValue)
    mulMaxValue = mulValue;

  return MSR_SUCCESS;
}

int
MsrInstantValue::decrement (unsigned long aiFactor)
{
  if (aiFactor > mulValue)
  {
    logger.logMsg (TRACE_FLAG, 0,
      "MsrInstantValue::decrement: Value is less than the factor");
    return MSR_FAIL;
  }

  mulValue -= aiFactor;

  if (mulMinValue > mulValue &&
      mulMinValue != 0)
    mulMinValue = mulValue;

  return MSR_SUCCESS;
}

int
MsrInstantValue::setValue (unsigned long aulValue)
{
  logger.logMsg (ERROR_FLAG, 0,
    "MsrInstantValue::setValue: Operation not allowed");

  return MSR_FAIL;
}

unsigned long 
MsrInstantValue::getMaxValue()
{
  return mulMaxValue;
}

unsigned long 
MsrInstantValue::getMinValue()
{
  return mulMinValue;
}

int
MsrInstantValue::duplicate (MsrInstantValue *(&apValue))
{
  if (!apValue)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "MsrInstantValue::duplicate: Destination is NULL");
    return MSR_FAIL;
  }

  apValue->mulValue = this->mulValue;
  apValue->miStatus = this->miStatus;

  if (this->mulMaxValue > apValue->mulMaxValue)
    apValue->mulMaxValue = this->mulMaxValue;

  if (apValue->mulMinValue > this->mulMinValue)
    apValue->mulMinValue = this->mulMinValue;

  return MSR_SUCCESS;
}

int
MsrInstantValue::reset ()
{
  mulValue = 0;
  miStatus = 0;
  mulMaxValue = 0;
  mulMinValue = 0;

  return MSR_SUCCESS;
}

MsrAccValue::MsrAccValue ()
{
  mulNumOfInvokes = 0;
}

MsrAccValue::~MsrAccValue ()
{
  mulNumOfInvokes = 0;
}

unsigned long 
MsrAccValue::getNumInvokes ()
{
  return mulNumOfInvokes;
}

int
MsrAccValue::increment (unsigned long aiFactor)
{
  mulValue += aiFactor;
  ++mulNumOfInvokes;

  return MSR_SUCCESS;
}

int
MsrAccValue::setValue (unsigned long aulValue)
{
  mulValue = aulValue;
  ++ mulNumOfInvokes;

  return MSR_SUCCESS;
}

int
MsrAccValue::decrement (unsigned long aiFactor)
{
  logger.logMsg (ERROR_FLAG, 0,
    "MsrAccValue::decrement: Operation not allowed");

  return MSR_FAIL;
}

int
MsrAccValue::setAverage ()
{
  if (mulNumOfInvokes <= 0)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "MsrAccValue::setAverage : Num of Invocations is <= 0");
    return MSR_FAIL;
  }

  mulValue = mulValue / mulNumOfInvokes;
  mulNumOfInvokes = 1;

  return MSR_SUCCESS;
}


int
MsrAccValue::duplicate (MsrAccValue *(&apValue))
{
  if (!apValue)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "MsrAccValue::duplicate: Destination is NULL");
    return MSR_FAIL;
  }

  apValue->mulValue += this->mulValue;
  apValue->miStatus = this->miStatus;
  apValue->mulNumOfInvokes = this->mulNumOfInvokes;

  return MSR_SUCCESS;
}

int
MsrAccValue::reset ()
{
  mulValue = 0;
  miStatus = 0;
  mulNumOfInvokes = 0;
  
  return MSR_SUCCESS;
}


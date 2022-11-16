/************************************************************************
     Name:     Measurement Value - includes

     Type:     C include file

     Desc:     This file provides access to Measurement Value Node

     File:     MsrValue.h

     Sid:      MsrValue.h 0  -  11/14/03

     Prg:      gs

************************************************************************/

#ifndef _MSR_VALUE_H_
#define _MSR_VALUE_H_

class MsrValue
{
  public :
    //C'tor
    MsrValue ();

    //D'tor
    virtual ~MsrValue();

    virtual int increment (unsigned long aiFactor = 1) = 0;

    int setStatus (int aiStatus);

    unsigned long getValue ();

    virtual int decrement (unsigned long aiFactor = 1) = 0;

    virtual int setValue (unsigned long aulValue) = 0;

    int getStatus ();

    unsigned long getMaxValue() { return 0;}

    unsigned long getMinValue() { return 0;}

    unsigned long getNumInvokes () { return 0;}

  protected:
    unsigned long mulValue;
    int           miStatus;
};

//instantaneous values can only be incremented or decremented
//and can never be set directly
class MsrInstantValue : public MsrValue
{
  public:
    MsrInstantValue ();

    ~MsrInstantValue ();

    unsigned long getMaxValue();

    unsigned long getMinValue();

    int setValue (unsigned long aulValue);

    int increment (unsigned long aiFactor = 1);

    int decrement (unsigned long aiFactor = 1);

    int duplicate (MsrInstantValue *(&apValue));

    int reset ();

  protected:
    unsigned long mulMaxValue;
    unsigned long mulMinValue;
};

//Accumulated values can be set or incremented only.
class MsrAccValue : public MsrValue
{
  public:
    MsrAccValue ();

    ~MsrAccValue ();

    unsigned long getNumInvokes ();

    int increment (unsigned long aiFactor = 1);

    int setValue (unsigned long aulValue);

    int decrement (unsigned long aiFactor = 1);

    int duplicate (MsrAccValue *(&apValue));

    int setAverage ();

    int reset ();
  private:
    unsigned long mulNumOfInvokes;
};

#endif /* _MSR_VALUE_H_ */

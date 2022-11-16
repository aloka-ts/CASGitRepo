/************************************************************************
     Name:     Measurement WorkUnit - includes
 
     Type:     C include file
 
     Desc:     This header file is used for accessing the WorkUnit
               for the MeasurementCounters and MeasurementSets

     File:     MsrWU.h

     Sid:      MsrWU.h 0  -  06/23/03 

     Prg:      gs

************************************************************************/

#ifndef __MEASUREMENT_WU_H_
#define __MEASUREMENT_WU_H_

#include <string>

class MsrWU
{
  public :            /* public data */

    //Workunit type enum
    enum WorkUnitType 
    { 
      MeasurementSetWU, 
      MeasurementCounterWU
    };

    //type of workunit can be - MeasurementSetWU or 
    //MeasurementCounterWU
    WorkUnitType miWUType;

    //identifier of Set or Counter
    std::string mstrID;

    //timer interval for this Workunit
    int miInterval;

    //number of timeouts needed for processing
    int miTimeoutsReqd;

    //number of timeouts passed
    int miTimeouts;

    //timer resolution
    int miTimerRes;

  private :           /* private data */

  protected :         /* protected data */

  private :           /* private operations */

  protected :         /* protected operations */

  public :            /* public operations */

    //default C'tor
    MsrWU () 
    { 
      miInterval = miTimeoutsReqd = miTimeouts = 0; 
      miTimerRes = 0;
    }

    //default D'tor
    ~MsrWU () {}

};

#endif /* __MEASUREMENT_WU_H_ */

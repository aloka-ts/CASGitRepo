/************************************************************************
     Name:     Measurement Manager - includes
 
     Type:     C include file
 
     Desc:     This is the header file needed to access the APIs for
               Measurement Manager.

     File:     MsrMgr.h

     Sid:      MsrMgr.h 0  -  06/23/03 

     Prg:      gs

************************************************************************/

#ifndef ___MSRMGR_H_
#define ___MSRMGR_H_

#include <iostream>
#include <string>
#include <vector>
#include <map>


typedef std::map<std::string,int> EmsOidValMap;

//this class provides the APIs into the Measurement Manager
//this is the only entry point into the Measurement Manager for the
//external components.

class MsrHashMap;
class MsrWorkerThread;

class MsrMgr
{
  private:               /* private data */

  protected:             /* protected data */

    //a pointer to self
    static MsrMgr *mpSelf;

    //level for the Measurement Manager
    int miLevel;
    int miMinScanInterval;
    int miMinAccInterval;

    MsrWorkerThread *mpWorkerThread;

    MsrHashMap      *mpHashMap;

  public:                /* public data */

  private:               /* private operations */

  protected:             /* protected operations */

    //default Constructor
    MsrMgr ();

  public:                /* public operations */

    //default destructor
    virtual ~MsrMgr ();

    //get the instance of MsrMgr
    static MsrMgr* getInstance ();

    //initialize the Measurement Manager via the XML or EMS
    int initialize ();

    //return the level for the Measurement Manager
    int getLevel ();

    void dump ();

    //update the value of the counter specified by the MeasurementID,
    //EntityID, and Name/Index. In case Entity is not defined in the
    //Counter then use "NULL" for the Entity.
    int setValue (std::string astrMId, std::string astrEntity,
                  std::string astrName, unsigned long aulValue);

    //update the value of the counter specified by the hashValue
    int setValue (std::string astrMId, std::string astrEntity,
                  std::string astrName, unsigned long aulHash, 
                  unsigned long aulValue);

    //update the value of the counter specified by the MeasurementID,
    //EntityID, and Name/Index. In case Entity is not defined in the
    //Counter then use "NULL" for the Entity.
    int increment (std::string astrMId, std::string astrEntity,
                  std::string astrName, long aulValue = 1);
    
    //update the value of the counter specified by the hashValue
    int increment (std::string astrMId, std::string astrEntity,
                  std::string astrName, unsigned long aulHash, 
                  long aulValue = 1);

    //update the value of the counter specified by the MeasurementID,
    //EntityID, and Name/Index. In case Entity is not defined in the
    //Counter then use "NULL" for the Entity.
    int decrement (std::string astrMId, std::string astrEntity,
                  std::string astrName, long aulValue = 1);
    
    //update the value of the counter specified by the hashValue
    int decrement (std::string astrMId, std::string astrEntity,
                  std::string astrName, unsigned long aulHash, 
                  long aulValue = 1);



    //get the value for a counter
    int getValue (std::string astrMId, std::string astrEntity,
                  std::string astrName, unsigned long &aulValue);

    int getValue (std::string astrMId, std::string astrEntity,
                  std::string astrName, unsigned long aulHash,
                  unsigned long &aulValue);

    //calculate the hash value from the MeasurementID, EntityID and Name
    int getHash (std::string astrMId, std::string astrEntity,
              std::string astrName, unsigned long &aulHash);

    //EMS related operations
    int configureMsrMgr (std::string astrXml);
    int reconfigureMsrMgr (std::string astrXml);
    int getInstValue (std::vector <std::string> &astrOidList,
                  std::vector <unsigned long> &aulValueList);

    int getInstValue (std::string &astrOid, unsigned long &aulValue);

    void getEmsParams(EmsOidValMap &params);

};


#endif /* __BP_MSRMGR_H_ */

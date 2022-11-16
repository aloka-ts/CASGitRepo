/************************************************************************
     Name:     Measurement Interface - includes

     Type:     C include file

     Desc:     This file provides access to Measurement Interface 

     File:     MsrIntf.h

     Sid:      MsrIntf.h 0  -  11/14/03

     Prg:      gs

************************************************************************/

#ifndef _MSR_INTF_H_
#define _MSR_INTF_H_

//#include <ccm/BpWorkerClbkIntf.h>
#include <INGwInfraMsrMgr/MsrWU.h>
#include <INGwInfraMsrMgr/MsrCoreData.h>
#include <INGwInfraMsrMgr/MsrSet.h>
#include <INGwInfraMsrMgr/MsrUpdateMsg.h>
#include <INGwInfraMsrMgr/MsrPool.h>
#include <map>
#include <deque>

class MsrWorkerThread;

class MsrInterface
{
  public:
    //C'tor
    MsrInterface ();

    //D'tor
    ~MsrInterface ();

    int initialize ();

    int getMeasurementSet (std::string astrSet = "");

    int processUpdateMsg (MsrUpdateMsg *apMsg);

    int processBackupPool (MsrPool<MsrInstantValue> *apPool);

    int processBackupPool (MsrPool<MsrAccValue> *apPool);

    int handleScanInterval (std::string &astrCounter);
    int handleCollectionInterval (std::string &astrSet);

    friend class MsrWorkerThread;

    int getTimerInterval () { return miTimerInterval; }

    void reconfigureTicks ();

    int getGcd (int aiNum1, int aiNum2);

  protected:
    //map of all the measurement sets
    std::map <std::string, MsrSet*> mSetMap;
    pthread_rwlock_t                mSetMapLock;

    //map of all the measurement Counters
    std::map <std::string, MsrCounter*> mCounterMap;

    //GCD of all timers
    int miTimerInterval;

    //list of the ticks and the Counter or Set
    std::vector <MsrWU *> mSetTickList;
    std::vector <MsrWU *> mCounterTickList;

};

#endif  /* _MSR_INTF_H_ */

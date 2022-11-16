/************************************************************************
     Name:     Measurement Set - includes

     Type:     C include file

     Desc:     This file provides access to Measurement Set Node

     File:     MsrSet.h

     Sid:      MsrSet.h 0  -  11/14/03

     Prg:      gs

************************************************************************/

#ifndef _MSR_SET_H_
#define _MSR_SET_H_

#include <vector>
#include <deque>
#include <string>
#include <map>
#include <INGwInfraMsrMgr/MsrCoreData.h>
#include <INGwInfraMsrMgr/MsrValue.h>
#include <INGwInfraMsrMgr/MsrPool.h>

class MsrSet
{
  public:
    //C'tor
    MsrSet (std::string &astrId, std::string &astrVersion, 
            int aiInterval, int aiPriority, bool aiReset = true,
            bool abEnable = true);

    //D'tor
    ~MsrSet ();

    int update (std::string astrVersion, int aiPriority, 
                int aiInterval, int aiEnable);

    int addCounter (MsrCounter *apCtr);
    int clearCounterList (std::vector <std::string> &aCtrList);

    int processBackupPool (MsrPool<MsrInstantValue> *apPool);
    int processBackupPool (MsrPool<MsrAccValue> *apPool);

    int handleCollectionInterval ();
    int handleScanInterval (std::string &astrCounterId);

    std::string &getId ();
    std::string &getVersion ();
    int getInterval ();
    int getPriority ();

  protected:
    int removeCounter (MsrCounter *apCtr);

  protected:
    std::string mstrSet;
    std::string mstrVersion;
    int miCollectionInterval;
    int miExecutionPriority;
    bool mbReset;
    bool mbEnable;
    struct timeval msBeginTime;
    struct timeval msEndTime;

    //counters in this set
    std::map <std::string, MsrCounter*> mCounterList;

    // data structure to update the values for the set
    std::map <int, MsrInstantValue*> mInstantValueMap;
    std::map <int, MsrAccValue*> mAccValueMap;
};

#endif /* _MSR_SET_H_ */

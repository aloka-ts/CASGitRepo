/************************************************************************
     Name:     Measurement Value manager - includes

     Type:     C include file

     Desc:     This file provides access to Value Manager 

     File:     MsrValueMgr.h

     Sid:      MsrValueMgr.h 0  -  11/14/03

     Prg:      gs

************************************************************************/


#ifndef _MSR_VALUE_MGR_H_
#define _MSR_VALUE_MGR_H_

#include <INGwInfraMsrMgr/MsrPool.h>
#include <vector>

const int MAX_NUM_POOLS = 10;

class MsrValueMgr
{
  public:
    //D'tor
    ~MsrValueMgr ();

    static const int INSTANTANEOUS = 1;
    static const int ACCUMULATED = 2;

    static MsrValueMgr *getInstance ();

    //create a new pool
    int createPool (int aiPoolSize, int aiPoolType);

    //backup an existing pool
    int backup (int aiPoolType, MsrPool<MsrInstantValue> *(&arPool));
    int backup (int aiPoolType, MsrPool<MsrAccValue> *(&arPool));

    //creating of a new index
    int createValue (int aiPoolType);

    //setting the value node
    int increment (int aiPoolType, int aiValueIndex, 
                   unsigned long aiFactor);
    int decrement (int aiPoolType, int aiValueIndex, 
                   unsigned long aiFactor);
    int setValue (int aiPoolType, int aiValueIndex, 
                   unsigned long aulValue);
    int setStatus (int aiPoolType, int aiValueIndex, 
                   int aiStatus);

    //retrieving the info from Value node
    int getValue (int aiPoolType, int aiValueIndex, unsigned long &aulValue);
    int getBackupValue (int aiPoolType, int aiValueIndex, unsigned long &aulValue);
    int getMaxValue (int aiPoolType, int aiValueIndex, unsigned long &aulValue);
    int getBackupMaxValue (int aiPoolType, int aiValueIndex, unsigned long &aulValue);
    int getMinValue (int aiPoolType, int aiValueIndex, unsigned long &aulValue);
    int getBackupMinValue (int aiPoolType, int aiValueIndex, unsigned long &aulValue);
    int getNumOfInvokes (int aiPoolType, int aiValueIndex, unsigned long &aulValue);
    int getBackupNumOfInvokes (int aiPoolType, int aiValueIndex, unsigned long &aulValue);
    int getStatus (int aiPoolType, int aiValueIndex, int &aiStatus);
    int getBackupStatus (int aiPoolType, int aiValueIndex, int &aiStatus);

  protected:
    //C'tor
    MsrValueMgr ();

    MsrPool<MsrInstantValue>* mInsPoolList;
    MsrPool<MsrInstantValue>* mInsBackupPool;
    pthread_rwlock_t mInsPoolListLock;

    MsrPool<MsrAccValue>* mAccPoolList;
    MsrPool<MsrAccValue>* mAccBackupPool;
    pthread_rwlock_t mAccPoolListLock;

    static MsrValueMgr *mpSelf;
};

#endif  /* _MSR_VALUE_MGR_H_ */

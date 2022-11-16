/************************************************************************
     Name:     Measurement Hash map - includes

     Type:     C include file

     Desc:     This file provides access to Measurement hashMap Node

     File:     MsrHashMap.h

     Sid:      MsrHashMap.h 0  -  11/14/03

     Prg:      gs

************************************************************************/

#ifndef _MSR_HASH_MAP_H_
#define _MSR_HASH_MAP_H_

#include <INGwInfraMsrMgr/MsrHashNode.h>
#include <map>
#include <string>

static const int MAX_HASH_BUCKET_SIZE = 9973;
static const int MAX_HASH_LOCKS       = 997;


class MsrWorkerThread;

class MsrHashMap
{
  public:
    //C'tor
    MsrHashMap(MsrWorkerThread *apWorker);

    //D'tor
    ~MsrHashMap();

    //create a param
    MsrHashNode * createParam (int aiCounterType,
                               std::string &astrCounter, 
                               std::string &astrEntity,
                               std::string &astrParam);

    int addCounterType (std::string &astrCounter, int aiType);

    int getCounterType (std::string &astrCounter);

    //return the hashId for a set of values
    unsigned long getHash (std::string &astrCounter, 
                           std::string &astrEntity,
                           std::string &astrParam);

    //overloaded increment, decrement, setvalue, getvalue, 
    //setstatus, getstatus
    int increment (std::string &astrCounter, std::string &astrEntity,
                   std::string &astrParam, int aiFactor = 1);
    int increment (unsigned long aulHashKey, std::string &astrCounter, 
            std::string &astrEntity, std::string &astrParam, int aiFactor = 1);

    int decrement (std::string &astrCounter, std::string &astrEntity,
                   std::string &astrParam, int aiFactor = 1);
    int decrement (unsigned long aulHashKey, std::string &astrCounter, 
           std::string &astrEntity, std::string &astrParam, int aiFactor = 1);

    int setValue (std::string &astrCounter, std::string &astrEntity,
                   std::string &astrParam, unsigned long aulValue);
    int setValue (unsigned long aulHashKey, std::string &astrCounter, 
         std::string &astrEntity, std::string &astrParam, unsigned long aulValue);

    int setStatus (std::string &astrCounter, std::string &astrEntity,
                   std::string &astrParam, int aiStatus);
    int setStatus (unsigned long aulHashKey, std::string &astrCounter, 
                  std::string &astrEntity, std::string &astrParam, int aiStatus);

    unsigned long getValue (std::string &astrCounter, std::string &astrEntity,
                   std::string &astrParam);
    unsigned long getValue (unsigned long aulHashKey, std::string &astrCounter, 
                  std::string &astrEntity, std::string &astrParam);

    int getStatus (std::string &astrCounter, std::string &astrEntity,
                   std::string &astrParam);
    int getStatus (unsigned long aulHashKey, std::string &astrCounter, 
                   std::string &astrEntity, std::string &astrParam );

    void dump ();

  protected:
    struct _tHashBucket {
      MsrHashNode           *mpHead;
      int                   miSize;
    };

    struct _tHashBucket mHashMap [MAX_HASH_BUCKET_SIZE];
    pthread_rwlock_t    mBucketLock [MAX_HASH_LOCKS];
    
    MsrWorkerThread *       mpWorkerThread;

    std::map <std::string, int> mCounterTypeMap;
    pthread_mutex_t             mCounterTypeMapLock;
};

#endif  /* _MSR_HASH_MAP_H_ */

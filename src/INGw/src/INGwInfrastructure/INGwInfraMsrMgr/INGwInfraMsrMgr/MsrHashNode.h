/************************************************************************
     Name:     Measurement hashNode - includes

     Type:     C include file

     Desc:     This file provides access to Measurement Hash Node

     File:     MsrHashNode.h

     Sid:      MsrHashNode.h 0  -  11/14/03

     Prg:      gs

************************************************************************/


#ifndef _MSR_HASH_NODE_H_
#define _MSR_HASH_NODE_H_

#include <INGwInfraMsrMgr/MsrValueMgr.h>
#include <string>

class MsrHashMap;

class MsrHashNode
{
  public:
    //C'tor
    MsrHashNode (int miCounterType,
                 std::string &astrCounter,
                 std::string &astrEntity,
                 std::string &astrParam,
                 unsigned long aulHashId);

    //D'tor
    ~MsrHashNode ();

    int getIndex ();

    int getDetails (std::string &astrCounter,
                    std::string &astrEntity,
                    std::string &astrParam);

    //update the param
    int setValue (unsigned long aulValue);
    int increment (int aiFactor);
    int decrement (int aiFactor);
    int setStatus (int aiStatus);

    //get the param info
    unsigned long getValue ();
    int getStatus ();

  protected:
    unsigned long mulHashId;
    std::string mstrCounter;
    std::string mstrEntity;
    std::string mstrParam;

    //accumulated or instantaneous
    int miCounterType;

    //pool index in which the value is stored
    int miPoolListIndex;
    int miPoolValueIndex;

    MsrHashNode *mpNext;

    MsrValueMgr *mpValueMgr;

    friend class MsrHashMap;
};

#endif /* _MSR_HASH_NODE_H_ */

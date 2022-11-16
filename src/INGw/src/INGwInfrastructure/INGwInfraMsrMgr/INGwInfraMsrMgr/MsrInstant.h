/************************************************************************
     Name:     Measurement Instant - includes

     Type:     C include file

     Desc:     This file provides access to EMS instant values

     File:     MsrInstant.h

     Sid:      MsrInstant.h 0  -  11/14/03

     Prg:      gs

************************************************************************/

#ifndef _MSR_INSTANT_H_
#define _MSR_INSTANT_H_

#include <INGwInfraMsrMgr/MsrValueMgr.h>
#include <INGwInfraMsrMgr/MsrPool.h>
#include <INGwInfraMsrMgr/MsrValue.h>
#include <INGwInfraMsrMgr/MsrMgr.h>

class MsrUpdateMsg;

class MsrInstant
{
  public:
    static MsrInstant *getInstance ();

    ~MsrInstant ();

    int createValue (MsrUpdateMsg *lpUnit);
    int handleUpdate (MsrUpdateMsg *lpUnit);
    int removeValue (MsrUpdateMsg *lpUnit);
    int setIndex (std::string &astrOid, int aiIndex);

    int updateValues (MsrPool<MsrInstantValue> *apPool);
    int updateValues (MsrPool<MsrAccValue> *apPool);

    int getValue (std::vector <std::string> &astrOidList,
                  std::vector <unsigned long> &aulValueList);

    int getValue (std::string &astrOid, unsigned long &aulValue);

    void getEmsParams(EmsOidValMap &params);

  protected:

    MsrInstant ();

    static MsrInstant *mpSelf;

    class ValueNode
    {
      public:
        unsigned long value;
        std::string   oid;
        std::string   id;
        int           index;
        int           type;
        int           enable;
    };

    std::map <std::string, ValueNode *> mValueMap;

    pthread_mutex_t  mMapLock;
};

#endif /* _MSR_INSTANT_H_ */

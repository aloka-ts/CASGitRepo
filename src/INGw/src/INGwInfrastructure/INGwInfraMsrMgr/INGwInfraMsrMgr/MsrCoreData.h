/************************************************************************
     Name:     Measurement Core Data - includes

     Type:     C include file

     Desc:     This file provides access to Measurement Code Data

     File:     MsrCoreData.h

     Sid:      MsrCoreData.h 0  -  11/14/03

     Prg:      gs

************************************************************************/

#ifndef _MSR_CORE_DATA_H_
#define _MSR_CORE_DATA_H_

#include <string>
#include <vector>

class MsrSet;

class MsrParam
{
  public:
    //C'tor
    MsrParam(int aiPoolIndex, std::string &astrParam);

    //D'tor
    ~MsrParam ();

    std::string &getId ();

    friend class MsrEntity;
    friend class MsrCounter;
    friend class MsrSet;

  protected:
    int miPoolIndex;
    std::string mstrParam;
};

class MsrEntity
{
  public:
    //C'tor
    MsrEntity(std::string &astrEntity);

    //D'tor
    ~MsrEntity();

    int addParam (std::string &astrParam, int aiPoolIndex);

    std::string &getId ();

    friend class MsrCounter;
    friend class MsrSet;

  protected:
    std::vector <MsrParam*> mParamList;
    std::string mstrEntity;
};

class MsrCounter
{
  public:
    //C'tor
    MsrCounter (std::string &astrCounter, std::string &astrMode,
                        std::string &astrOid, int aiCounterType,
                        int aiInterval, int aiPriority, bool abEnable);

    //D'tor
    ~MsrCounter ();

    int addEntity (std::string &astrEntity);

    int addParam (std::string &astrEntity,
                  std::string &astrParam, 
                  int aiPoolIndex);    

    int update (int aiPriority, int aiInterval, int abEnable);

    friend class MsrSet;

    int handleScanInterval ();

    std::string &getId ();

  protected:
    std::vector <MsrEntity *> mEntityList;
    std::string mstrCounter;
    std::string mstrMode;
    std::string mstrOid;
    int miCounterType;
    int miScanInterval;
    int miExecutionPriority;
    bool mbEnable;

    int miNumOfParams;
};

#endif /* _MSR_CORE_DATA_H_ */

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraMsrMgr");
/************************************************************************
     Name:     Measurement Core Data - implementation

     Type:     C implementation file

     Desc:     This file provides access to Measurement Code Data

     File:     MsrCoreData.C

     Sid:      MsrCoreData.C 0  -  11/14/03

     Prg:      gs

************************************************************************/

#include <INGwInfraMsrMgr/MsrIncludes.h>
#include <INGwInfraMsrMgr/MsrCoreData.h>
#include <INGwInfraMsrMgr/MsrInstant.h>
#include <string>

using namespace std;

//C'tor
MsrParam::MsrParam(int aiPoolIndex, std::string &astrParam):
miPoolIndex (aiPoolIndex),
mstrParam (astrParam)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrParam::MsrParam <%d, %s>", 
    aiPoolIndex, astrParam.c_str());

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrParam::MsrParam");
}

//D'tor
MsrParam::~MsrParam ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrParam::~MsrParam");

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrParam::~MsrParam");
}

std::string &
MsrParam::getId ()
{
  return mstrParam;
}

//C'tor
MsrEntity::MsrEntity(std::string &astrEntity):
mstrEntity (astrEntity)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrEntity::MsrEntity <%s>",
    astrEntity.c_str());

  mParamList.clear();

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrEntity::MsrEntity");
}

//D'tor
MsrEntity::~MsrEntity()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrEntity::~MsrEntity");

  std::vector <MsrParam*>::iterator iter;

  for (iter = mParamList.begin(); iter != mParamList.end(); iter++)
  {
    delete *(iter);
  }

  mParamList.clear();

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrEntity::~MsrEntity");
}

int 
MsrEntity::addParam (std::string &astrParam, int aiPoolIndex)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrEntity::addParam <%s, %d> to Entity <%s>",
    astrParam.c_str(), aiPoolIndex, mstrEntity.c_str());

  std::vector <MsrParam*>::iterator iter;

  for (iter = mParamList.begin(); iter != mParamList.end(); iter++)
  {
    MsrParam *lpParam = *(iter);
    if (lpParam && lpParam->getId () == astrParam)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "MsrEntity::addParam: Param<%s> already exists in Entity<%s>",
        astrParam.c_str(), mstrEntity.c_str());
      return MSR_FAIL;
    }
  }

  MsrParam *lpParam = new MsrParam (aiPoolIndex, astrParam);

  if (lpParam)
    mParamList.push_back (lpParam);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrEntity::addParam");

  return MSR_SUCCESS;
}

std::string &
MsrEntity::getId ()
{
  return mstrEntity;
}

//C'tor
MsrCounter::MsrCounter (std::string &astrCounter, string &astrMode,
                        string &astrOid, int aiCounterType,
                        int aiInterval, int aiPriority, bool abEnable):
mstrCounter (astrCounter),
mstrMode (astrMode),
mstrOid (astrOid),
miCounterType (aiCounterType),
miScanInterval (aiInterval),
miExecutionPriority (aiPriority),
mbEnable (abEnable),
miNumOfParams (0)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrCounter::MsrCounter <%s, %s, %s, %d, %d, %d, %d>",
    astrCounter.c_str(), astrMode.c_str(), astrOid.c_str(),
    aiCounterType, aiInterval, aiPriority, abEnable);

  mEntityList.clear();

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrCounter::MsrCounter");
}

//D'tor
MsrCounter::~MsrCounter ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrCounter::~MsrCounter");

  std::vector <MsrEntity *>::iterator iter;

  for (iter = mEntityList.begin(); iter != mEntityList.end(); iter++)
  {
    delete *(iter);
  }
  mEntityList.clear();

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrCounter::~MsrCounter");
}

int 
MsrCounter::addEntity (std::string &astrEntity)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrCounter::addEntity <%s> to Counter<%s>",
    astrEntity.c_str(), mstrCounter.c_str());

  std::vector <MsrEntity *>::iterator iter;

  for (iter = mEntityList.begin(); iter != mEntityList.end(); iter++)
  {
    MsrEntity *lpEnt = *(iter);
    if (lpEnt && lpEnt->getId () == astrEntity)
    {
      logger.logMsg (ERROR_FLAG, 0, 
        "MsrCounter::addEntity: Entity <%s> already exists in Counter<%s>",
        astrEntity.c_str(), mstrCounter.c_str());
      return MSR_FAIL;
    }
  }

  MsrEntity *lpEnt = new MsrEntity (astrEntity);

  if (lpEnt)
    mEntityList.push_back (lpEnt);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrCounter::addEntity");

  return MSR_SUCCESS;
}

int 
MsrCounter::addParam (std::string &astrEntity,
                      std::string &astrParam, 
                      int aiPoolIndex)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrCounter::addParam <%s, %d> to Entity <%s> of Counter<%s>",
    astrParam.c_str(), aiPoolIndex, astrEntity.c_str(), mstrCounter.c_str());

  std::vector <MsrEntity *>::iterator iter;

  if (!mstrOid.empty())
    MsrInstant::getInstance()->setIndex (mstrOid, aiPoolIndex);

  for (iter = mEntityList.begin(); iter != mEntityList.end(); iter++)
  {
    MsrEntity *lpEnt = *(iter);
    if (lpEnt && lpEnt->getId () == astrEntity)
    {
      logger.logMsg (TRACE_FLAG, 0,
        "Leaving MsrCounter::addParam");
      if (lpEnt->addParam (astrParam, aiPoolIndex) == MSR_SUCCESS)
        miNumOfParams++;
      else
        return MSR_FAIL;

      return MSR_SUCCESS;
    }
  }

  return MSR_FAIL;
}

std::string  &
MsrCounter::getId ()
{
  return mstrCounter;
}

int 
MsrCounter::handleScanInterval ()
{
  return MSR_FAIL;
}

int 
MsrCounter::update (int aiPriority, int aiInterval, int aiEnable)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrCounter::update <%d, %d, %d>",
    aiPriority, aiInterval, aiEnable);

  if (aiPriority > 0)
    miExecutionPriority = aiPriority;

  if (aiInterval > 0)
    miScanInterval = aiInterval;

  if (aiEnable > 0)
    mbEnable = aiEnable;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrCounter::update");
  return MSR_SUCCESS;
}

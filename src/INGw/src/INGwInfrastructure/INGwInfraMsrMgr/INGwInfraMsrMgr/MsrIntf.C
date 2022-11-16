#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraMsrMgr");
/************************************************************************
     Name:     Measurement Interface - implementation

     Type:     C implementation file

     Desc:     This file provides access to Measurement Interface 

     File:     MsrIntf.C

     Sid:      MsrIntf.C 0  -  11/14/03

     Prg:      gs

************************************************************************/

#include <INGwInfraMsrMgr/MsrIncludes.h>
#include <INGwInfraMsrMgr/MsrIntf.h>
#include <INGwInfraMsrMgr/MsrInstant.h>

using namespace std;

//C'tor
MsrInterface::MsrInterface ():
miTimerInterval (0)
{
  pthread_rwlock_init (&mSetMapLock, 0);

  mSetMap.clear();
  mCounterMap.clear();
  mSetTickList.clear();
  mCounterTickList.clear();

  MsrInstant::getInstance();
}

//D'tor
MsrInterface::~MsrInterface ()
{
  std::map <std::string, MsrSet*>::iterator setIter;
  std::map <std::string, MsrCounter*>::iterator counterIter;
  std::vector <MsrWU *>::iterator listIter;

  pthread_rwlock_wrlock (&mSetMapLock);

  for (setIter = mSetMap.begin(); setIter != mSetMap.end(); setIter++)
  {
    MsrSet *lpSet = setIter->second;
    delete lpSet;
  }
  mSetMap.clear();

  for (counterIter = mCounterMap.begin(); counterIter != mCounterMap.end(); 
       counterIter++)
  {
    MsrCounter *lpCounter = counterIter->second;
    delete lpCounter;
  }
  mCounterMap.clear();

  for (listIter = mSetTickList.begin(); listIter != mSetTickList.end(); 
       listIter++)
  {
    MsrWU *lpUnit = *(listIter);
    delete lpUnit;
  }
  mSetTickList.clear();

  for (listIter = mCounterTickList.begin(); listIter != mCounterTickList.end(); 
       listIter++)
  {
    MsrWU *lpUnit = *(listIter);
    delete lpUnit;
  }
  mCounterTickList.clear();

  miTimerInterval = 0;


  pthread_rwlock_unlock (&mSetMapLock);

  delete MsrInstant::getInstance();

  pthread_rwlock_destroy (&mSetMapLock);
}

int 
MsrInterface::initialize ()
{
  return MSR_FAIL;
}

int
MsrInterface::getMeasurementSet (std::string astrSet)
{
  // might not be used anymore since we'll be sending the set to
  // the agent for backing up or sending to EMS.
  return MSR_FAIL;
}

int 
MsrInterface::processUpdateMsg (MsrUpdateMsg *apMsg)
{
  if (!apMsg)
    return MSR_FAIL;

  switch (apMsg->meMsgType)
  {
    case MsrUpdateMsg::addSet:
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "Adding a new Measurement Set <%s, %s, %d, %d, %d>",
        apMsg->upd.set.mstrId.c_str(), apMsg->upd.set.mstrVersion.c_str(),
        apMsg->upd.set.miExecutionPriority,
        apMsg->upd.set.miTimerInterval, apMsg->upd.set.miMaxSetsInQueue);

      MsrSet *lpSet = new MsrSet (apMsg->upd.set.mstrId,
                                  apMsg->upd.set.mstrVersion,
                                  apMsg->upd.set.miTimerInterval,
                                  apMsg->upd.set.miExecutionPriority,
                                  apMsg->upd.set.mbReset,
                                  apMsg->upd.set.miEnable);

      if (!lpSet)
        return MSR_FAIL;

      //the key for the msr set will be actually the id+version
      string lstrKey = apMsg->upd.set.mstrId; 

      pthread_rwlock_wrlock (&mSetMapLock);

      std::map <std::string, MsrSet*>::iterator setIter;
      setIter = mSetMap.find (lstrKey);

      if (setIter != mSetMap.end())
      {
        pthread_rwlock_unlock (&mSetMapLock);
        delete lpSet;
        MsrUpdateMsg lMsg;
        lMsg.meMsgType = MsrUpdateMsg::reconfigSet;
        lMsg.upd.recfg.set.mstrId = apMsg->upd.set.mstrId;
        lMsg.upd.recfg.set.mstrVersion = apMsg->upd.set.mstrVersion;
        lMsg.upd.recfg.set.miExecutionPriority = apMsg->upd.set.miExecutionPriority;
        lMsg.upd.recfg.set.miTimerInterval = apMsg->upd.set.miTimerInterval;
        lMsg.upd.recfg.set.miMaxSetsInQueue = apMsg->upd.set.miMaxSetsInQueue;
        lMsg.upd.recfg.set.mCounterList = apMsg->upd.set.mCounterList;
        lMsg.upd.recfg.set.miEnable = apMsg->upd.set.miEnable;
        lMsg.upd.recfg.set.mbReset = apMsg->upd.set.mbReset;
        lMsg.upd.recfg.set.mstrEntityType = apMsg->upd.set.mstrEntityType;
        return processUpdateMsg (&lMsg);
      }
      mSetMap [lstrKey] = lpSet;

      //create a MsrWU for the Set
      MsrWU *lpUnit = new MsrWU;
      lpUnit->miWUType = MsrWU::MeasurementSetWU;
      lpUnit->mstrID = lstrKey;
      lpUnit->miInterval = apMsg->upd.set.miTimerInterval;

      if (miTimerInterval <= 0)
      {
        miTimerInterval = lpUnit->miInterval;
      }

      mSetTickList.push_back (lpUnit);

      reconfigureTicks ();

      if (apMsg->upd.set.mCounterList.size () != 0)
      {
        std::vector <std::string>::iterator listIter;
        std::map <std::string, MsrCounter*>::iterator mapIter;
        for (listIter = apMsg->upd.set.mCounterList.begin();
             listIter != apMsg->upd.set.mCounterList.end();
             listIter++)
        {
          string lstrCounter = *(listIter);
          mapIter = mCounterMap.find (lstrCounter);
          if (mapIter != mCounterMap.end())
          {
            MsrCounter *lpCounter = mapIter->second;
            if (lpCounter)
            {
              if (lpSet->addCounter (lpCounter) != MSR_SUCCESS)
                logger.logMsg (ERROR_FLAG, 0,
                  "Unable to add Counter <%s> to Set <%s>",
                  lstrCounter.c_str(), apMsg->upd.set.mstrId.c_str());
            }
          }
          else
          {
            logger.logMsg (ERROR_FLAG, 0, 
              "Unable to locate Counter for CounterId<%s> to add in Set<%s>",
              lstrCounter.c_str(), apMsg->upd.set.mstrId.c_str());
          }
        }
      }

      pthread_rwlock_unlock (&mSetMapLock);
    }
    break;

    case MsrUpdateMsg::addCounter:
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "Adding a new Measurement Counter <%s, %d, %d>",
        apMsg->upd.counter.mstrId.c_str(), 
        apMsg->upd.counter.miExecutionPriority,
        apMsg->upd.counter.miTimerInterval);

      MsrCounter *lpCounter = new MsrCounter (apMsg->upd.counter.mstrId,
                                     apMsg->upd.counter.mstrMode,
                                     apMsg->upd.counter.mstrOid,
                                     apMsg->upd.counter.miCounterType,
                                     apMsg->upd.counter.miTimerInterval,
                                     apMsg->upd.counter.miExecutionPriority,
                                     apMsg->upd.counter.miEnable);

      if (!lpCounter)
        return MSR_FAIL;

      pthread_rwlock_wrlock (&mSetMapLock);

      std::map <std::string, MsrCounter*>::iterator iter;
      iter = mCounterMap.find (apMsg->upd.counter.mstrId);
      if (iter == mCounterMap.end())
      {
        mCounterMap [apMsg->upd.counter.mstrId] = lpCounter;

        logger.logMsg (VERBOSE_FLAG, 0,
          "Adding a new counter <%s> in CounterMap",
          apMsg->upd.counter.mstrId.c_str());

        if (apMsg->upd.counter.miTimerInterval)
        {
          //create a MsrWU for the Set
          MsrWU *lpUnit = new MsrWU;
          lpUnit->miWUType = MsrWU::MeasurementCounterWU;
          lpUnit->mstrID = apMsg->upd.counter.mstrId;
          lpUnit->miInterval = apMsg->upd.counter.miTimerInterval;

          if (miTimerInterval <= 0 && lpUnit->miInterval >= 0)
          {
            miTimerInterval = lpUnit->miInterval;
          }
  
          mCounterTickList.push_back (lpUnit);

          reconfigureTicks();
        }
      }

      pthread_rwlock_unlock (&mSetMapLock);

      //if this is instantaneous counter for EMS
      if (!apMsg->upd.counter.mstrOid.empty())
        MsrInstant::getInstance()->createValue (apMsg);
    }
    break;

    case MsrUpdateMsg::addEntity:
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "Adding a new Entity <%s, %s>",
        apMsg->upd.ent.mstrId.c_str(),
        apMsg->upd.ent.mstrCounter.c_str());

      pthread_rwlock_wrlock (&mSetMapLock);

      std::map <std::string, MsrCounter*>::iterator iter;
      iter = mCounterMap.find (apMsg->upd.ent.mstrCounter);
      if (iter != mCounterMap.end())
      {
        MsrCounter *lpCounter = iter->second;
        if (lpCounter)
        {
          if (lpCounter->addEntity (apMsg->upd.ent.mstrId) != MSR_SUCCESS)
            logger.logMsg (ERROR_FLAG, 0,
              "Unable to add Entity <%s> to Counter <%s>",
              apMsg->upd.ent.mstrId.c_str(), 
              apMsg->upd.ent.mstrCounter.c_str());
        }
      }
      else
        logger.logMsg (ERROR_FLAG, 0,
          "Unable to add Entity <%s> to Counter <%s>. Counter non-existant",
          apMsg->upd.ent.mstrId.c_str(), apMsg->upd.ent.mstrCounter.c_str());

      pthread_rwlock_unlock (&mSetMapLock);
    }
    break;

    case MsrUpdateMsg::addParam:
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "Adding a new Param <%s, %s, %s>",
        apMsg->upd.param.mstrId.c_str(),
        apMsg->upd.param.mstrEntity.c_str(),
        apMsg->upd.param.mstrCounter.c_str());
    
      pthread_rwlock_wrlock (&mSetMapLock);

      std::map <std::string, MsrCounter*>::iterator iter;
      iter = mCounterMap.find (apMsg->upd.param.mstrCounter);
      if (iter != mCounterMap.end())
      {
        MsrCounter *lpCounter = iter->second;
        if (lpCounter)
        { 
          if (lpCounter->addParam (apMsg->upd.param.mstrEntity,
              apMsg->upd.param.mstrId, 
              apMsg->upd.param.miPoolIndex) != MSR_SUCCESS)
            logger.logMsg (ERROR_FLAG, 0,
              "Unable to add Param <%s> to Entity <%s> of Counter <%s>",
              apMsg->upd.param.mstrId.c_str(), 
              apMsg->upd.param.mstrEntity.c_str(),
              apMsg->upd.param.mstrCounter.c_str());
        }
      }

      pthread_rwlock_unlock (&mSetMapLock);
    }
    break;

    case MsrUpdateMsg::deleteSet:
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "delete Set request received for Set <%s, %s>",
        apMsg->upd.set.mstrId.c_str(), apMsg->upd.set.mstrVersion.c_str());

      //the key for the msr set will be actually the id+version
      string lstrKey = apMsg->upd.set.mstrId;

      pthread_rwlock_wrlock (&mSetMapLock);

      std::map <std::string, MsrSet*>::iterator iter;
      iter = mSetMap.find (lstrKey);

      if (iter != mSetMap.end())
      {
        MsrSet *lpSet = iter->second;
        if (lpSet->getVersion() == apMsg->upd.set.mstrVersion)
        {
          delete lpSet;

          //create a MsrWU for the Set
          std::vector <MsrWU *>::iterator workIter;
          for (workIter = mSetTickList.begin ();
               workIter != mSetTickList.end ();
               workIter++)
          {
            MsrWU *lpUnit = *(workIter);
            if (lpUnit)
            { 
              if (lpUnit->mstrID == lstrKey)
              {
                delete lpUnit;
                mSetTickList.erase (workIter++);
                break;
              }
            }
          }
        }

        mSetMap.erase (iter++);

        reconfigureTicks ();
      }

      pthread_rwlock_unlock (&mSetMapLock);
    }
    break;

    case MsrUpdateMsg::reconfigSet:
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "Reconfig request received for Set <%s, %s, %d, %d, %d>",
        apMsg->upd.recfg.set.mstrId.c_str(),
        apMsg->upd.recfg.set.mstrVersion.c_str(),
        apMsg->upd.recfg.set.miExecutionPriority,
        apMsg->upd.recfg.set.miTimerInterval,
        apMsg->upd.recfg.set.miEnable);

      //the key for the msr set will be actually the id+version
      string lstrKey = apMsg->upd.recfg.set.mstrId;

      pthread_rwlock_wrlock (&mSetMapLock);

      std::map <std::string, MsrSet*>::iterator iter;
      iter = mSetMap.find (lstrKey);

      if (iter != mSetMap.end())
      {
        MsrSet *lpSet = iter->second;
        if (lpSet)
        {
          lpSet->update (apMsg->upd.recfg.set.mstrVersion,
                         apMsg->upd.recfg.set.miExecutionPriority,
                         apMsg->upd.recfg.set.miTimerInterval,
                         apMsg->upd.recfg.set.miEnable);

          std::vector <std::string>::iterator listIter;

          if (!apMsg->upd.recfg.set.mCounterList.empty())
          {
            lpSet->clearCounterList (apMsg->upd.recfg.set.mCounterList);
            if (!apMsg->upd.recfg.set.mCounterList.empty())
            {
  
              for (listIter = apMsg->upd.recfg.set.mCounterList.begin();
                   listIter != apMsg->upd.recfg.set.mCounterList.end();
                   listIter++)
              {
                string lstrCounter = *(listIter);
                std::map <std::string, MsrCounter*>::iterator ctrIter;
                ctrIter = mCounterMap.find (lstrCounter);
                if (ctrIter != mCounterMap.end())
                {
                  MsrCounter *lpCtr = ctrIter->second;
                  if (lpCtr)
                  {
                      lpSet->addCounter (lpCtr);
                  }
                }
              }
            }
          }

          //update a MsrWU for the Set
          if (apMsg->upd.recfg.set.miTimerInterval > 0)
          {
            std::vector <MsrWU *>::iterator workIter;
            for (workIter = mSetTickList.begin ();
                 workIter != mSetTickList.end ();
                 workIter++)
            {
              MsrWU *lpUnit = *(workIter);
              if (lpUnit)
              {
                if (lpUnit->mstrID == lstrKey)
                {
                  lpUnit->miInterval = apMsg->upd.recfg.set.miTimerInterval;
                  break;
                }
              }
            }
            reconfigureTicks ();
          }
        }
      }
      else
        logger.logMsg (ERROR_FLAG, 0,
          "Reconfiguration failed for Set <%s, %s, %d, %d>",
          apMsg->upd.recfg.set.mstrId.c_str(),
          apMsg->upd.recfg.set.mstrVersion.c_str(),
          apMsg->upd.recfg.set.miExecutionPriority,
          apMsg->upd.recfg.set.miTimerInterval);


      pthread_rwlock_unlock (&mSetMapLock);
    }
    break;

    case MsrUpdateMsg::reconfigCounter:
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "Reconfig request received for Counter <%s, %d, %d, %d>",
        apMsg->upd.recfg.ctr.mstrId.c_str(),
        apMsg->upd.recfg.ctr.miExecutionPriority,
        apMsg->upd.recfg.ctr.miTimerInterval,
        apMsg->upd.recfg.ctr.miEnable);

      pthread_rwlock_wrlock (&mSetMapLock);

      std::map <std::string, MsrCounter*>::iterator ctrIter;
      ctrIter = mCounterMap.find (apMsg->upd.recfg.ctr.mstrId);
      if (ctrIter != mCounterMap.end())
      {
        MsrCounter *lpCtr = ctrIter->second;
        if (lpCtr)
        {
          lpCtr->update (apMsg->upd.recfg.ctr.miExecutionPriority,
                         apMsg->upd.recfg.ctr.miTimerInterval,
                         apMsg->upd.recfg.ctr.miEnable);

          //update a MsrWU for the Set
          if (apMsg->upd.recfg.ctr.miTimerInterval > 0)
          {
            std::vector <MsrWU *>::iterator workIter;
            for (workIter = mCounterTickList.begin ();
                 workIter != mCounterTickList.end ();
                 workIter++)
            {
              MsrWU *lpUnit = *(workIter);
              if (lpUnit)
              {
                if (lpUnit->mstrID == apMsg->upd.recfg.ctr.mstrId)
                {
                  lpUnit->miInterval = apMsg->upd.recfg.ctr.miTimerInterval;
                  break;
                }
              }
            }
            reconfigureTicks ();
          }

        }
      }

      pthread_rwlock_unlock (&mSetMapLock);

      MsrInstant::getInstance()->handleUpdate (apMsg);
    }
    break;

    default:
      logger.logMsg (ERROR_FLAG, 0,
        "Unknown update msg received <%d>",
        (apMsg->meMsgType));
      return MSR_FAIL;
      break;
  }

  return MSR_SUCCESS;
}

int 
MsrInterface::processBackupPool (MsrPool<MsrInstantValue> *apPool)
{
  //logger.logMsg (TRACE_FLAG, 0,
  //  "Entering MsrInterface::processBackupPool");

  if (!apPool) 
  {
    logger.logMsg (ERROR_FLAG, 0,
      "MsrInterface::processBackupPool Pool passed is NULL");
    return MSR_FAIL;
  }

  std::map <std::string, MsrSet*>::iterator iter;

  pthread_rwlock_wrlock (&mSetMapLock);

  for (iter = mSetMap.begin(); iter != mSetMap.end(); iter++)
  {
    MsrSet *lpSet = iter->second;
    if (lpSet)
    {
      if (lpSet->processBackupPool (apPool) != MSR_SUCCESS)
        logger.logMsg (ERROR_FLAG, 0,
          "Unable to update set <%s> during backup pool",
          lpSet->getId ().c_str());
    }
  }

  pthread_rwlock_unlock (&mSetMapLock);

  MsrInstant::getInstance()->updateValues (apPool);

  //logger.logMsg (TRACE_FLAG, 0,
  //  "Leaving MsrInterface::processBackupPool");
  return MSR_SUCCESS;
}

int 
MsrInterface::processBackupPool (MsrPool<MsrAccValue> *apPool)
{
  //logger.logMsg (TRACE_FLAG, 0,
  //  "Entering MsrInterface::processBackupPool");

  if (!apPool) 
  {
    logger.logMsg (ERROR_FLAG, 0,
      "MsrInterface::processBackupPool Pool passed is NULL");
    return MSR_FAIL;
  }

  std::map <std::string, MsrSet*>::iterator iter;

  pthread_rwlock_wrlock (&mSetMapLock);

  for (iter = mSetMap.begin(); iter != mSetMap.end(); iter++)
  {   
    MsrSet *lpSet = iter->second;
    if (lpSet)
    {  
      if (lpSet->processBackupPool (apPool) != MSR_SUCCESS)
        logger.logMsg (ERROR_FLAG, 0,
          "Unable to update set <%s> during backup pool",
          lpSet->getId ().c_str());
    }
  }   

  pthread_rwlock_unlock (&mSetMapLock);
  
  MsrInstant::getInstance()->updateValues (apPool);

  //logger.logMsg (TRACE_FLAG, 0,
  //  "Leaving MsrInterface::processBackupPool");

  return MSR_SUCCESS;
}

int
MsrInterface::handleScanInterval (string &astrCounterId)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrInterface::handleScanInterval <%s>",
    astrCounterId.c_str());

  std::map <std::string, MsrSet*>::iterator iter;

  pthread_rwlock_wrlock (&mSetMapLock);

  for (iter = mSetMap.begin(); iter != mSetMap.end(); iter++)
  {
    MsrSet *lpSet = iter->second;

    if (lpSet)
      lpSet->handleScanInterval (astrCounterId);
  }

  pthread_rwlock_unlock (&mSetMapLock);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrInterface::handleScanInterval");

  return MSR_SUCCESS;
}

int
MsrInterface::handleCollectionInterval (string &astrSetId)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrInterface::handleCollectionInterval <%s>",
    astrSetId.c_str());

  std::map <std::string, MsrSet*>::iterator iter;

  pthread_rwlock_wrlock (&mSetMapLock);

  iter = mSetMap.find (astrSetId);

  if (iter != mSetMap.end())
  {
    MsrSet *lpSet = iter->second;

    if (lpSet)
      lpSet->handleCollectionInterval ();
  }

  pthread_rwlock_unlock (&mSetMapLock);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrInterface::handleCollectionInterval");

  return MSR_SUCCESS;
}

int
MsrInterface::getGcd (register int aiNum1, register int aiNum2)
{
  if (aiNum1 == 0 || aiNum2 == 0)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "MsrInterface::getGcd : Number cannot be zero");
    return 1;
  }

  register int liLess = (aiNum1 < aiNum2) ?  aiNum1 : aiNum2;

  while((aiNum1 % liLess) || (aiNum2 % liLess))
  {
    liLess--;
  }

  logger.logMsg (VERBOSE_FLAG, 0,
    "GCD of <%d, %d> is <%d>", aiNum1, aiNum2, liLess);

  return liLess;
}

void
MsrInterface::reconfigureTicks ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrInterface::reconfigureTicks");

  MsrWU *lpUnit;

  std::vector <MsrWU *>::iterator iter;

  int liInterval = -1;

  for (iter = mSetTickList.begin(); iter != mSetTickList.end(); iter++)
  {
    lpUnit = *(iter);
    if (lpUnit)
    {
      if (liInterval == -1)
        liInterval = lpUnit->miInterval;
      else
        liInterval = getGcd (liInterval, lpUnit->miInterval);
    }
  }

  for (iter = mCounterTickList.begin(); iter != mCounterTickList.end(); iter++)
  {
    lpUnit = *(iter);
    if (lpUnit)
    {
      if (liInterval == -1)
        liInterval = lpUnit->miInterval;
      else
        liInterval = getGcd (liInterval, lpUnit->miInterval);
    }
  }

  miTimerInterval = liInterval;

  logger.logMsg (VERBOSE_FLAG, 0,
    "Timer Resolution after GCD calculation <%d>", miTimerInterval);

  for (iter = mSetTickList.begin(); iter != mSetTickList.end(); iter++)
  {
    lpUnit = *(iter);
    if (lpUnit)
    {
      lpUnit->miTimerRes = miTimerInterval;
      lpUnit->miTimeoutsReqd = lpUnit->miInterval / lpUnit->miTimerRes;

      logger.logMsg (VERBOSE_FLAG, 0,
        "Set <%s>, Timers <%d, %d>, Ticks <%d, %d>",
        lpUnit->mstrID.c_str(), lpUnit->miInterval, lpUnit->miTimerRes,
        lpUnit->miTimeoutsReqd, lpUnit->miTimeouts);
    }
  }

  for (iter = mCounterTickList.begin(); iter != mCounterTickList.end(); iter++)
  {
    lpUnit = *(iter);
    if (lpUnit)
    {
      lpUnit->miTimerRes = miTimerInterval;
      lpUnit->miTimeoutsReqd = lpUnit->miInterval / lpUnit->miTimerRes;

      logger.logMsg (VERBOSE_FLAG, 0,
        "Counter <%s>, Timers <%d, %d>, Ticks <%d, %d>",
        lpUnit->mstrID.c_str(), lpUnit->miInterval, lpUnit->miTimerRes,
        lpUnit->miTimeoutsReqd, lpUnit->miTimeouts);
    } 
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrInterface::reconfigureTicks");
}

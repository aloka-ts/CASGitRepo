#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraMsrMgr"); 
/************************************************************************
     Name:     Measurement Set - implementation

     Type:     C implementation file

     Desc:     This file provides access to Measurement Set Node

     File:     MsrSet.C

     Sid:      MsrSet.C 0  -  11/14/03

     Prg:      gs

************************************************************************/

#include <INGwInfraMsrMgr/MsrIncludes.h>
#include <INGwInfraMsrMgr/MsrSet.h>
#include <INGwInfraMsrMgr/MsrValueMgr.h>
#include <INGwInfraManager/INGwIfrMgrManager.h>
#include <set>

#ifdef _AGENT_IMPL_
#include <EmsIdl/RSIEmsTypes_c.hh>
#include <Agent/BayAgentImpl.h>
#endif

#ifdef _BP_CCM_
#include <ccm/BpCCM.h>
#endif

using namespace std;

//C'tor
MsrSet::MsrSet (std::string &astrId, std::string &astrVersion, 
                int aiInterval, int aiPriority, bool aiReset, bool abEnable):
mstrSet (astrId),
mstrVersion (astrVersion),
miCollectionInterval (aiInterval),
miExecutionPriority (aiPriority),
mbReset (aiReset),
mbEnable (abEnable)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrSet::MsrSet");

  mCounterList.clear ();
  mInstantValueMap.clear();
  mAccValueMap.clear ();

  gettimeofday (&msEndTime, 0);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrSet::MsrSet");
}

//D'tor
MsrSet::~MsrSet ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrSet::~MsrSet");

  mCounterList.clear ();
  std::map <int, MsrInstantValue*>::iterator insIter;
  std::map <int, MsrAccValue*>::iterator accIter;

  for (insIter = mInstantValueMap.begin();
       insIter != mInstantValueMap.end ();
       insIter++)
  {
    delete insIter->second;
  }
  mInstantValueMap.clear();

  for (accIter = mAccValueMap.begin();
       accIter != mAccValueMap.end ();
       accIter++)
  {
    delete accIter->second;
  }
  mAccValueMap.clear();

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrSet::~MsrSet");
}

int 
MsrSet::update (std::string astrVersion, int aiPriority, 
                int aiInterval, int aiEnable)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrSet::update");

  if (!astrVersion.empty())
    mstrVersion = astrVersion;

  if (aiPriority > 0)
    miExecutionPriority = aiPriority;

  if (aiInterval > 0)
    miCollectionInterval = aiInterval;

  mbEnable = (bool) aiEnable;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrSet::update");

  return MSR_SUCCESS;
}

int 
MsrSet::addCounter (MsrCounter *apCtr)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrSet::addCounter");

  if (!apCtr)
    return MSR_FAIL;

  std::map <string, MsrCounter*>::iterator iter;

  iter = mCounterList.find (apCtr->getId());

  if (iter == mCounterList.end())
  {
    mCounterList [apCtr->getId()] = apCtr;
    logger.logMsg (TRACE_FLAG, 0,
      "Leaving MsrSet::addCounter");
    return MSR_SUCCESS;
  }

  logger.logMsg (WARNING_FLAG, 0,
    "MsrSet::addCounter : Counter <%s> Already exists in set<%s>",
    apCtr->getId().c_str(), mstrSet.c_str());

  return MSR_FAIL;
}

int 
MsrSet::removeCounter (MsrCounter *apCtr)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrSet::removeCounter");

  if (!apCtr)
    return MSR_FAIL;

  std::map <int, MsrAccValue*>::iterator accvalueIter;
  std::map <int, MsrInstantValue*>::iterator insvalueIter;
  std::vector <MsrEntity *>::iterator entIter;
  std::vector <MsrParam*>::iterator parmIter;
  MsrEntity *lpEnt;

  for (entIter = apCtr->mEntityList.begin();
       entIter != apCtr->mEntityList.end();
       entIter++)
  {
    lpEnt = *(entIter);
    if (lpEnt)
    {
      for (parmIter = lpEnt->mParamList.begin();
           parmIter != lpEnt->mParamList.end();
           parmIter++)
      {
        MsrParam *lpParm = *(parmIter);
        if (lpParm)
        {
          if (apCtr->miCounterType == MsrValueMgr::ACCUMULATED)
          {
            accvalueIter = mAccValueMap.find (lpParm->miPoolIndex);
            if (accvalueIter == mAccValueMap.end())
            {
              logger.logMsg (WARNING_FLAG, 0,
                "The index <%d> couldn't be found in AccValueMap of Set<%s>",
                lpParm->miPoolIndex, mstrSet.c_str());
            }
            else
            {
              delete accvalueIter->second;
              mAccValueMap.erase (accvalueIter++);
            }
          }
          else if (apCtr->miCounterType == MsrValueMgr::INSTANTANEOUS)
          {
            insvalueIter = mInstantValueMap.find (lpParm->miPoolIndex);
            if (insvalueIter == mInstantValueMap.end())
            {
              logger.logMsg (WARNING_FLAG, 0,
                "The index <%d> couldn't be found in InstantValueMap of Set<%s>",
                lpParm->miPoolIndex, mstrSet.c_str());
            }
            else
            {
              delete insvalueIter->second;
              mInstantValueMap.erase (insvalueIter++);
            }
          }
        }
      }
    }
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrSet::removeCounter");
  return MSR_SUCCESS;
}

int
MsrSet::clearCounterList (vector <string> &aCtrList)
{
  //logger.logMsg (TRACE_FLAG, 0,
  //  "Entering MsrSet::clearCounterList");

  if (aCtrList.empty())
    return MSR_FAIL;

  std::map <string, MsrCounter*>::iterator iter;
  std::vector <std::string>::iterator strIter;
  MsrCounter *lpCounter;
    
  for (iter = mCounterList.begin();
       iter != mCounterList.end();)
  {
    lpCounter = iter->second;
    if (!lpCounter)
      continue;

    strIter = std::find (aCtrList.begin(), 
                         aCtrList.end(), lpCounter->mstrCounter);

    if (strIter == aCtrList.end())
    {
      logger.logMsg (WARNING_FLAG, 0,
        "MsrSet::clearCounterList : Counter <%s> being removed from Set<%s>",
        lpCounter->mstrCounter.c_str(), mstrSet.c_str());

      removeCounter (lpCounter);
      mCounterList.erase (iter++);

    }
    else
      iter++;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrSet::clearCounterList");

  return MSR_SUCCESS;
}

int 
MsrSet::processBackupPool (MsrPool<MsrInstantValue> *apPool)
{
  //logger.logMsg (TRACE_FLAG, 0,
  //  "Entering MsrSet::processBackupPool");

  if (!apPool || !mbEnable)
    return MSR_FAIL;
    
  std::map <string, MsrCounter*>::iterator iter;
  std::map <int, MsrInstantValue*>::iterator valueIter;
  std::vector <MsrEntity *>::iterator entIter;
  std::vector <MsrParam*>::iterator parmIter;
  
  for (iter = mCounterList.begin();
       iter != mCounterList.end();
       iter++) 
  {    
    MsrCounter *lpCtr = iter->second;
    
    if (lpCtr && lpCtr->miCounterType == MsrValueMgr::INSTANTANEOUS)
    {
      for (entIter = lpCtr->mEntityList.begin();
           entIter != lpCtr->mEntityList.end();
           entIter++)
      {
        MsrEntity *lpEnt = *(entIter);
        if (lpEnt)
        {
          for (parmIter = lpEnt->mParamList.begin();
               parmIter != lpEnt->mParamList.end();
               parmIter++)
          {
            MsrParam *lpParm = *(parmIter);
            if (lpParm)
            {
              valueIter = mInstantValueMap.find (lpParm->miPoolIndex);
              MsrInstantValue *lpValue = 0;

              if (valueIter == mInstantValueMap.end())
              {
                lpValue = new MsrInstantValue;
                mInstantValueMap [lpParm->miPoolIndex] = lpValue;
              }
              else
              {
                lpValue = valueIter->second;
              }

              if (lpValue)
              {
                if (apPool->duplicateValue (lpParm->miPoolIndex, lpValue) !=
                    MSR_SUCCESS)
                {
                  logger.logMsg (ERROR_FLAG, 0,
                    "Unable to duplicate value for <%d>",
                    lpParm->miPoolIndex);
                }
              }
            }
          }
        }
      }
    }
  }

  //logger.logMsg (TRACE_FLAG, 0,
  //  "Leaving MsrSet::processBackupPool");

  return MSR_SUCCESS;
}

int 
MsrSet::processBackupPool (MsrPool<MsrAccValue> *apPool)
{
  //logger.logMsg (TRACE_FLAG, 0,
  //  "Entering MsrSet::processBackupPool");

  if (!apPool || !mbEnable)
    return MSR_FAIL;
    
  std::map <string, MsrCounter*>::iterator iter;
  std::map <int, MsrAccValue*>::iterator valueIter;
  std::vector <MsrEntity *>::iterator entIter;
  std::vector <MsrParam*>::iterator parmIter;
  
  for (iter = mCounterList.begin();
       iter != mCounterList.end();
       iter++) 
  {    
    MsrCounter *lpCtr = iter->second;
    
    if (lpCtr && lpCtr->miCounterType == MsrValueMgr::ACCUMULATED)
    {
      for (entIter = lpCtr->mEntityList.begin();
           entIter != lpCtr->mEntityList.end();
           entIter++)
      {
        MsrEntity *lpEnt = *(entIter);
        if (lpEnt)
        {
          for (parmIter = lpEnt->mParamList.begin();
               parmIter != lpEnt->mParamList.end();
               parmIter++)
          {
            MsrParam *lpParm = *(parmIter);
            if (lpParm)
            {
              valueIter = mAccValueMap.find (lpParm->miPoolIndex);
              MsrAccValue *lpValue = 0;

              if (valueIter == mAccValueMap.end())
              {
                lpValue = new MsrAccValue;
                mAccValueMap [lpParm->miPoolIndex] = lpValue;
              }
              else
              {
                lpValue = valueIter->second;
              }

              if (lpValue)
              {
                if (apPool->duplicateValue (lpParm->miPoolIndex, lpValue) !=
                    MSR_SUCCESS)
                {
                  logger.logMsg (ERROR_FLAG, 0,
                    "Unable to duplicate value for <%d>",
                    lpParm->miPoolIndex);
                }
              }
            }
          }
        }
      }
    }
  }

  //logger.logMsg (TRACE_FLAG, 0,
  //  "Leaving MsrSet::processBackupPool");
  return MSR_SUCCESS;
}

int
MsrSet::handleCollectionInterval ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrSet::handleCollectionInterval Set<%s, %s>",
    mstrSet.c_str(), mstrVersion.c_str());

  if (!mbEnable)
    return MSR_FAIL;

  msBeginTime = msEndTime;
  gettimeofday (&msEndTime, 0);

  map<string, MsrCounter*>::iterator iter;
  std::map <int, MsrAccValue*>::iterator accValueIter;
  std::map <int, MsrInstantValue*>::iterator insValueIter;
  std::vector <MsrEntity *>::iterator entIter;
  std::vector <MsrParam*>::iterator parmIter;
  char ch[200];

#ifdef _AGENT_IMPL_
  RSIEmsTypes::MeasurementSetList* lpCorbaSet = 
                             new RSIEmsTypes::MeasurementSetList;
  RSIEmsTypes::MeasurementSetList_var lvarSet(lpCorbaSet);

  lvarSet->numberOfMeasurementSetsPending = 0;
  lvarSet->listOfMeasurementSet.length (1);

  lvarSet->listOfMeasurementSet[0].measurementSetID = mstrSet.c_str();
  lvarSet->listOfMeasurementSet[0].version = mstrVersion.c_str();
  lvarSet->listOfMeasurementSet[0].heldDataFlags = RSIEmsTypes::COMPLETED_NO;
  lvarSet->listOfMeasurementSet[0].measurementBeginTime.tv_sec = 
                                                  msBeginTime.tv_sec;
  lvarSet->listOfMeasurementSet[0].measurementBeginTime.tv_usec = 
                                                  msBeginTime.tv_usec;
  lvarSet->listOfMeasurementSet[0].measurementEndTime.tv_sec = 
                                                  msEndTime.tv_sec;
  lvarSet->listOfMeasurementSet[0].measurementEndTime.tv_usec = 
                                                  msEndTime.tv_usec;

  //printf("measurementSetID[%s] version[%d] sec[%d] msec[%d] endSec[%d] endMsec[%d]\n",
  //mstrSet.c_str(), mstrVersion.c_str(), msBeginTime.tv_sec, msBeginTime.tv_usec,
  //msEndTime.tv_sec, msEndTime.tv_usec);
  //fflush(stdout);

#endif

  multimap<string, MsrCounter*> lsEntityCounterMap;
  multimap<string, MsrCounter*>::iterator entCtrIter;
  set<string> lsEntitySet;

  for (iter = mCounterList.begin();
       iter != mCounterList.end();
       iter++)
  {
    MsrCounter *lpCtr = iter->second;

    if (lpCtr && lpCtr->miNumOfParams > 0)
    {
      for (entIter = lpCtr->mEntityList.begin();
           entIter != lpCtr->mEntityList.end();
           entIter++)
      {
        MsrEntity *lpEnt = *(entIter);
        if (lpEnt)
        {
          lsEntitySet.insert (lpEnt->mstrEntity);
          lsEntityCounterMap.insert (multimap<string, MsrCounter*>::value_type(lpEnt->mstrEntity, lpCtr));;
        }
      }
    }
  }

#ifdef _AGENT_IMPL_
  lvarSet->listOfMeasurementSet[0].listOfMeasurementSetPerEntity.length (
      lsEntitySet.size());

    //lsEntityCounterMap.size());
    //printf("TOTAL LENGTH ....[%d] EntitySet[%d]\n", lsEntityCounterMap.size(),
	//lsEntitySet.size());
#endif

  set<string>::iterator setIter;
  int liEntCount = 0;

  for (setIter = lsEntitySet.begin();
       setIter != lsEntitySet.end();
       setIter++, liEntCount++)
  {

#ifdef _AGENT_IMPL_
    lvarSet->listOfMeasurementSet[0].listOfMeasurementSetPerEntity[liEntCount].
           entityID = setIter->c_str();
    lvarSet->listOfMeasurementSet[0].listOfMeasurementSetPerEntity[liEntCount].
           listOfMeasurementCounters.length (lsEntityCounterMap.count(*setIter));

	//printf("EntityId[%s] \n", setIter->c_str());
	//fflush(stdout);
#endif

    pair<multimap<string, MsrCounter*>::iterator, 
         multimap<string, MsrCounter*>::iterator> pair1;

    pair1 = lsEntityCounterMap.equal_range(*(setIter));
    int liCtrCount = 0;

    for (entCtrIter = pair1.first; entCtrIter != pair1.second; 
         entCtrIter++, liCtrCount++)
    {
      MsrCounter *lpCtr = entCtrIter->second;
  
      if (lpCtr && lpCtr->miNumOfParams > 0)
      {
  
#ifdef _AGENT_IMPL_
        lvarSet->listOfMeasurementSet[0].
          listOfMeasurementSetPerEntity[liEntCount].
          listOfMeasurementCounters[liCtrCount].counterType = RSIEmsTypes::NAMED;

        lvarSet->listOfMeasurementSet[0].
          listOfMeasurementSetPerEntity[liEntCount].
          listOfMeasurementCounters[liCtrCount].measurementID = 
                  lpCtr->mstrCounter.c_str();

#endif

        for (entIter = lpCtr->mEntityList.begin();
             entIter != lpCtr->mEntityList.end();
             entIter++)
        {
          MsrEntity *lpEnt = *(entIter);
          if (lpEnt && lpEnt->mstrEntity == *setIter)
          {
#ifdef _AGENT_IMPL_
            lvarSet->listOfMeasurementSet[0].
              listOfMeasurementSetPerEntity[liEntCount].
              listOfMeasurementCounters[liCtrCount].
              listOfCounterValues.length (lpEnt->mParamList.size());
#endif
            int liParamCount = 0;
            for (parmIter = lpEnt->mParamList.begin();
                 parmIter != lpEnt->mParamList.end();
                 parmIter++, liParamCount++)
            {
              MsrParam *lpParm = *(parmIter);
              if (lpParm)
              {

                unsigned long lulValue = 0;
                int liFlag = 0;
                if (lpCtr->miCounterType == MsrValueMgr::ACCUMULATED)
                {
                  accValueIter = mAccValueMap.find (lpParm->miPoolIndex);
                  if (accValueIter != mAccValueMap.end())
                  {
                    MsrAccValue *lpValue = accValueIter->second;
                    if (lpValue)
                    {
                      lulValue = lpValue->getValue();
                      liFlag = lpValue->getStatus ();
                      if (mbReset)
                        lpValue->reset();

						//printf("Param Name[%s] Type[%d] Value[%d] \n", lpParm->mstrParam.c_str(), lpCtr->miCounterType, lulValue); fflush(stdout);
                    }
                  }
                }
                else if (lpCtr->miCounterType == MsrValueMgr::INSTANTANEOUS)
                {
                  insValueIter = mInstantValueMap.find (lpParm->miPoolIndex);
                  if (insValueIter != mInstantValueMap.end())
                  {
                    MsrInstantValue *lpValue = insValueIter->second;
                    if (lpValue)
                    {
                      lulValue = lpValue->getValue();
                      liFlag = lpValue->getStatus ();
                      if (mbReset)
                        lpValue->reset();
						//printf("INS PARAM Name [%s] Type[%d] Value[%d] \n", lpParm->mstrParam.c_str(),
						//lpCtr->miCounterType, lulValue);
						//fflush(stdout);
                    }
                  }
                }
#ifdef _AGENT_IMPL_
                lvarSet->listOfMeasurementSet[0].heldDataFlags = 
                                             RSIEmsTypes::COMPLETED_YES;

				//printf("FINAL VALUES setPerEntiryCnt[%d] \n",
				//		liEntCount);

				//fflush(stdout);

                lvarSet->listOfMeasurementSet[0].
                  listOfMeasurementSetPerEntity[liEntCount].
                  listOfMeasurementCounters[liCtrCount].
                  listOfCounterValues[liParamCount].ValueID = 
                             lpParm->mstrParam.c_str();
                lvarSet->listOfMeasurementSet[0].
                  listOfMeasurementSetPerEntity[liEntCount].
                  listOfMeasurementCounters[liCtrCount].
                  listOfCounterValues[liParamCount].heldValue.value = 
                             lulValue;
                lvarSet->listOfMeasurementSet[0].
                  listOfMeasurementSetPerEntity[liEntCount].
                  listOfMeasurementCounters[liCtrCount].
                  listOfCounterValues[liParamCount].heldDataFlags =
                             liFlag;
#endif
              }
            }
            break; //get out of the innermost loop
          }
        }
      }
    }
  }

  logger.logMsg (VERBOSE_FLAG, 0,
    "Reporting <%d> Measurement Set to BpCCM",
    lvarSet->listOfMeasurementSet.length());
  
  INGwIfrMgrManager::getInstance().reportMeasurementSet ((void*)lpCorbaSet);
  
  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrSet::handleCollectionInterval");

  return MSR_SUCCESS;
}

int
MsrSet::handleScanInterval (string &astrCounterId)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering handleScanInterval");

  if (!mbEnable)
    return MSR_FAIL;

  map<string, MsrCounter*>::iterator iter;
  std::map <int, MsrAccValue*>::iterator accvalueIter;
  std::map <int, MsrInstantValue*>::iterator insvalueIter;
  std::vector <MsrEntity *>::iterator entIter;
  std::vector <MsrParam*>::iterator parmIter;
  MsrAccValue *lpValue = 0;

  iter = mCounterList.find (astrCounterId);

  if (iter != mCounterList.end())
  {
    MsrCounter *lpCtr = iter->second;
    if (lpCtr && lpCtr->miScanInterval != 0)
    {
      for (entIter = lpCtr->mEntityList.begin();
           entIter != lpCtr->mEntityList.end();
           entIter++)
      {
        MsrEntity *lpEnt = *(entIter);
        if (lpEnt)
        {
          for (parmIter = lpEnt->mParamList.begin();
               parmIter != lpEnt->mParamList.end();
               parmIter++)
          {
            MsrParam *lpParm = *(parmIter);
            if (lpParm)
            {
              if (lpCtr->miCounterType == MsrValueMgr::ACCUMULATED)
              {
                accvalueIter = mAccValueMap.find (lpParm->miPoolIndex);

                if (accvalueIter != mAccValueMap.end())
                {
                  lpValue = accvalueIter->second;
                  if (lpValue)
                    lpValue->setAverage();
                }

              }
            }
          }
        }
      }
    }
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving handleScanInterval");

  return MSR_SUCCESS;
}

std::string &
MsrSet::getId ()
{
  return mstrSet;
}

std::string &
MsrSet::getVersion ()
{
  return mstrVersion;
}

int
MsrSet::getInterval ()
{
  return miCollectionInterval;
}

int
MsrSet::getPriority ()
{
  return miExecutionPriority;
}


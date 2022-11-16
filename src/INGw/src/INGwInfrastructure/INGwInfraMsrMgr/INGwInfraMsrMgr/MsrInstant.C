#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraMsrMgr");

/************************************************************************
     Name:     Measurement Instant - implementation

     Type:     C implementation file

     Desc:     This file provides access to EMS instant values

     File:     MsrInstant.C

     Sid:      MsrInstant.C 0  -  11/14/03

     Prg:      gs

************************************************************************/

#include <INGwInfraMsrMgr/MsrIncludes.h>
#include <INGwInfraMsrMgr/MsrInstant.h>
#include <INGwInfraMsrMgr/MsrUpdateMsg.h>

using namespace std;

MsrInstant *MsrInstant::mpSelf = 0;

MsrInstant*
MsrInstant::getInstance ()
{
  if (!mpSelf)
    mpSelf = new MsrInstant;

  return mpSelf;
}

MsrInstant::MsrInstant ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrInstant::MsrInstant");

  pthread_mutex_init (&mMapLock, 0);

  mValueMap.clear();

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrInstant::MsrInstant");
}

MsrInstant::~MsrInstant ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrInstant::~MsrInstant");

  std::map <std::string, ValueNode *>::iterator iter;

  pthread_mutex_lock (&mMapLock);

  for (iter = mValueMap.begin(); iter != mValueMap.end(); iter++)
  {
    ValueNode *lpNode = iter->second;

    delete lpNode;
  }

  mValueMap.clear();

  pthread_mutex_unlock (&mMapLock);

  pthread_mutex_destroy (&mMapLock);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrInstant::~MsrInstant");
}

int
MsrInstant::createValue (MsrUpdateMsg *lpUnit)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrInstant::createValue");

  if (!lpUnit || lpUnit->upd.counter.mstrOid.empty())
    return MSR_FAIL;

  std::map <std::string, ValueNode *>::iterator iter;

  pthread_mutex_lock (&mMapLock);

  iter = mValueMap.find (lpUnit->upd.counter.mstrOid);

  if (iter != mValueMap.end())
  {
    pthread_mutex_unlock (&mMapLock);
    return MSR_FAIL;
  }

  ValueNode *lpNode = new ValueNode;

  lpNode->id = lpUnit->upd.counter.mstrId;
  lpNode->oid = lpUnit->upd.counter.mstrOid;
  lpNode->type = lpUnit->upd.counter.miCounterType;
  lpNode->value = 0;
  lpNode->index = -1;
  mValueMap [lpUnit->upd.counter.mstrOid] = lpNode;
  pthread_mutex_unlock (&mMapLock);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrInstant::createValue");

  return MSR_SUCCESS;
}

int
MsrInstant::removeValue (MsrUpdateMsg *lpUnit)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrInstant::removeValue");

  if (!lpUnit || lpUnit->upd.counter.mstrOid.empty())
    return MSR_FAIL;

  std::map <std::string, ValueNode *>::iterator iter;

  pthread_mutex_lock (&mMapLock);

  iter = mValueMap.find (lpUnit->upd.counter.mstrOid);

  if (iter != mValueMap.end())
  {
    ValueNode *lpNode = iter->second;

    delete lpNode;
    mValueMap.erase (iter++);
    pthread_mutex_unlock (&mMapLock);
    return MSR_SUCCESS;
  }

  pthread_mutex_unlock (&mMapLock);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrInstant::removeValue");
  return MSR_FAIL;
}

int
MsrInstant::handleUpdate (MsrUpdateMsg *lpUnit)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrInstant::handleUpdate");
  
  if (!lpUnit || lpUnit->upd.recfg.ctr.mstrId.empty() || 
      lpUnit->upd.recfg.ctr.miEnable <= 0)
    return MSR_FAIL;

  std::map <std::string, ValueNode *>::iterator iter;
  ValueNode *lpNode;
  
  pthread_mutex_lock (&mMapLock);

  for (iter = mValueMap.begin();
       iter != mValueMap.end() ; iter++)
  { 
    lpNode = iter->second;
    if (lpNode  && lpNode->id == lpUnit->upd.recfg.ctr.mstrId)
    {
      lpNode->enable = lpUnit->upd.recfg.ctr.miEnable;
      pthread_mutex_unlock (&mMapLock);
      return MSR_SUCCESS;
    }
  }
  
  pthread_mutex_unlock (&mMapLock);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrInstant::handleUpdate");
  
  return MSR_SUCCESS;
} 


int
MsrInstant::setIndex (string &astrOid, int aiIndex)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrInstant::setIndex <%s, %d>", 
    astrOid.c_str(), aiIndex);

  if (aiIndex < 0 || astrOid.empty())
    return MSR_FAIL;

  std::map <std::string, ValueNode *>::iterator iter;

  pthread_mutex_lock (&mMapLock);

  iter = mValueMap.find (astrOid);

  if (iter != mValueMap.end())
  {
    ValueNode *lpNode = iter->second;

    if (lpNode)
    {
      if (lpNode->index != -1)
        logger.logMsg (ERROR_FLAG, 0,
          "setIndex : index being set again <%d, %d>",
          lpNode->index, aiIndex);
      lpNode->index = aiIndex;
    }
    pthread_mutex_unlock (&mMapLock);

    logger.logMsg (TRACE_FLAG, 0,
      "Leaving MsrInstant::setIndex");
    return MSR_SUCCESS;
  }

  pthread_mutex_unlock (&mMapLock);

  return MSR_FAIL;
}

int 
MsrInstant::updateValues (MsrPool<MsrInstantValue> *apPool)
{
  //logger.logMsg (TRACE_FLAG, 0,
  //  "Entering MsrInstant::updateValues");

  if (!apPool)
    return MSR_FAIL;

  std::map <std::string, ValueNode *>::iterator iter;
  unsigned long lulValue = 0;

  pthread_mutex_lock (&mMapLock);

  for (iter = mValueMap.begin(); iter != mValueMap.end(); iter++)
  {
    ValueNode *lpNode = iter->second;

    if (lpNode && lpNode->type == MsrValueMgr::INSTANTANEOUS)
    {
      if (lpNode->index != -1 &&
          apPool->getValue (lpNode->index, lulValue) == MSR_SUCCESS)
        lpNode->value = lulValue;
      else
      {
        //logger.logMsg (WARNING_FLAG, 0,
        //  "Unable to update instant value for <%s, %d>",
        //  lpNode->oid.c_str(), lpNode->index);
      }
    }
  }

  pthread_mutex_unlock (&mMapLock);

  //logger.logMsg (TRACE_FLAG, 0,
  //  "Leaving MsrInstant::updateValues");
  return MSR_SUCCESS;
}

int 
MsrInstant::updateValues (MsrPool<MsrAccValue> *apPool)
{
  //logger.logMsg (TRACE_FLAG, 0,
  //  "Entering MsrInstant::updateValues");

  if (!apPool)
    return MSR_FAIL;

  std::map <std::string, ValueNode *>::iterator iter;
  unsigned long lulValue = 0;

  pthread_mutex_lock (&mMapLock);

  for (iter = mValueMap.begin(); iter != mValueMap.end(); iter++)
  {
    ValueNode *lpNode = iter->second;

    if (lpNode && lpNode->type == MsrValueMgr::ACCUMULATED)
    {
      if (lpNode->index != -1 &&
          apPool->getValue (lpNode->index, lulValue) == MSR_SUCCESS)
        lpNode->value += lulValue;
      else
      {  
        logger.logMsg (WARNING_FLAG, 0,
          "Unable to update instant value for <%s, %d>",
          lpNode->oid.c_str(), lpNode->index);
      }
    }
  }

  pthread_mutex_unlock (&mMapLock);

  //logger.logMsg (TRACE_FLAG, 0,
  //  "Leaving MsrInstant::updateValues");
  return MSR_SUCCESS;
}

int 
MsrInstant::getValue (std::vector <std::string> &astrOidList,
                      std::vector <unsigned long> &aulValueList)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrInstant::getValue");

  std::vector <std::string>::iterator listIter;
  std::map <std::string, ValueNode *>::iterator mapIter;
  ValueNode *lpNode;
  unsigned long lulValue;

  pthread_mutex_lock (&mMapLock);

  if (astrOidList.empty())
  {
    for (mapIter = mValueMap.begin();
         mapIter != mValueMap.end() ; mapIter++)
    {
      lpNode = mapIter->second;
      if (lpNode && lpNode->enable)
      {
        astrOidList.push_back (lpNode->oid);
        lulValue = 0;
        if (lpNode->index >= 0 &&
            MsrValueMgr::getInstance()->getValue (lpNode->type,
                     lpNode->index, lulValue) == MSR_SUCCESS)
        {
          if (lpNode->type == MsrValueMgr::ACCUMULATED)
            lulValue += lpNode->value;
          aulValueList.push_back (lulValue);
        }
        else
          aulValueList.push_back (lpNode->value);
      }
    }

  }
  else
  {
    for (listIter = astrOidList.begin();
         listIter != astrOidList.end();
         listIter++)
    {

      mapIter = mValueMap.find (*listIter);

      if (mapIter != mValueMap.end())
      {
        lpNode = mapIter->second;

        if (lpNode)
        {
          lulValue = 0;
          if (lpNode->index >= 0 &&
              MsrValueMgr::getInstance()->getValue (lpNode->type, 
                       lpNode->index, lulValue) == MSR_SUCCESS)
          {
            if (lpNode->type == MsrValueMgr::ACCUMULATED)
              lulValue += lpNode->value;
            aulValueList.push_back (lulValue);
          }
          else
            aulValueList.push_back (lpNode->value);
        }
      }
      else
      {
        pthread_mutex_unlock (&mMapLock);
        aulValueList.clear();
        return MSR_FAIL;
      }
    }
  }

  pthread_mutex_unlock (&mMapLock);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrInstant::getValue");
  return MSR_SUCCESS;
}

int 
MsrInstant::getValue (std::string &astrOid, unsigned long &aulValue)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrInstant::getValue");

  std::map <std::string, ValueNode *>::iterator mapIter;
  ValueNode *lpNode;
  unsigned long lulValue;

  pthread_mutex_lock (&mMapLock);

  mapIter = mValueMap.find (astrOid);

  if (mapIter != mValueMap.end())
  {
    lpNode = mapIter->second;

    if (lpNode)
    {
      lulValue = 0;
      if (lpNode->index >= 0 &&
          MsrValueMgr::getInstance()->getValue (lpNode->type,
                     lpNode->index, lulValue) == MSR_SUCCESS)
      {
        if (lpNode->type == MsrValueMgr::ACCUMULATED)
          lulValue += lpNode->value;
        aulValue = lulValue;
      }
      else
        aulValue = lpNode->value;
    }
  }
  else
  {
    pthread_mutex_unlock (&mMapLock);
    return MSR_FAIL;
  }

  pthread_mutex_unlock (&mMapLock);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrInstant::getValue");

  return MSR_SUCCESS;
}

void
MsrInstant::getEmsParams(EmsOidValMap &params)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrInstant::getEmsParams");


  std::map <std::string, ValueNode *>::iterator iter;
  unsigned long lulValue = 0;

  pthread_mutex_lock (&mMapLock);

  for (iter = mValueMap.begin(); iter != mValueMap.end(); iter++)
  {
    ValueNode *lpNode = iter->second;
    if (lpNode)
    {
      lulValue = 0;
      if (lpNode->index >= 0 &&
          MsrValueMgr::getInstance()->getValue (lpNode->type,
                     lpNode->index, lulValue) == MSR_SUCCESS)
      {
        if (lpNode->type == MsrValueMgr::ACCUMULATED)
          lulValue += lpNode->value;

        //BPInd12591 : Set the latest value not the last collected from Value Mgr
        //if (lpNode->type == MsrValueMgr::INSTANTANEOUS)
          //lulValue = lpNode->value;
      }
      else
        lulValue = lpNode->value;

      params[lpNode->oid]= lulValue;
    }
 
  }

  pthread_mutex_unlock (&mMapLock);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrInstant::getEmsParams");
  return;

}



//#include <CCMUtil/BpLogger.h>
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwStackManager");

/************************************************************************
   Name:     INAP Stack Manager Statistics Map - impl
 
   Type:     C impl file
 
   Desc:     Implementation required for INGwSmStsMap class

   File:     INGwSmStsMap.C

   Sid:      INGwSmStsMap.C 0  -  03/27/03 

   Prg:      gs,bd

************************************************************************/


#include "INGwStackManager/INGwSmStsMap.h"

using namespace std;



/******************************************************************************
*
*     Fun:   INGwSmStsMap()
*
*     Desc:  Default Contructor
*
*     Notes: None
*
*     File:  INGwSmStsMap.C
*
*******************************************************************************/
INGwSmStsMap::INGwSmStsMap()
{

}


/******************************************************************************
*
*     Fun:   ~INGwSmStsMap()
*
*     Desc:  Default Destructor
*
*     Notes: None
*
*     File:  INGwSmStsMap.C
*
*******************************************************************************/
INGwSmStsMap::~INGwSmStsMap()
{

}


/******************************************************************************
*
*     Fun:   initialize()
*
*     Desc:  initialize the table
*
*     Notes: None
*
*     File:  INGwSmStsMap.C
*
*******************************************************************************/
int
INGwSmStsMap::initialize ()
{
  //cleanup the cache
  miCachedPosition = -1;

  //clean up the map
  meOidMap.clear();

  return BP_AIN_SM_OK;
}


/******************************************************************************
*
*     Fun:   insert()
*
*     Desc:  insert entry into map
*
*     Notes: None
*
*     File:  INGwSmStsMap.C
*
*******************************************************************************/
int
INGwSmStsMap::insert (long alKey, INGwSmStsOid *apValue)
{
  INGwSmOidMap::iterator leMapIter;

  leMapIter = meOidMap.find (alKey);

  if (leMapIter == meOidMap.end())
  {
    INGwSmOidVector *lpOidVector = new INGwSmOidVector;

    lpOidVector->push_back (apValue);

    meOidMap [alKey] = lpOidVector;
  }
  else
  {
    INGwSmOidVector *lpOidVector = 
                        leMapIter->second;

    INGwSmOidVector::iterator leOidIter;
    INGwSmStsOid *lpOid;

    for (leOidIter = lpOidVector->begin();
         leOidIter != lpOidVector->end();
         leOidIter++)
    {
      lpOid = *(leOidIter);
      if (lpOid == apValue)
      {
        return BP_AIN_SM_FAIL;
      }
    }

    lpOidVector->push_back (apValue);
  }

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   getList()
*
*     Desc:  this returns a vector of OIDs for the Key
*
*     Notes: None
*
*     File:  INGwSmStsMap.C
*
*******************************************************************************/
INGwSmStsOidList*
INGwSmStsMap::getList (long alKey, int &aiRetVal)
{
  INGwSmOidMap::iterator leMapIter;

  leMapIter = meOidMap.find (alKey);

  if (leMapIter == meOidMap.end())
  {
    aiRetVal = BP_AIN_SM_FAIL;
  }
  else
    aiRetVal = BP_AIN_SM_OK;

  return (leMapIter->second);
}

/******************************************************************************
*
*     Fun:   get()
*
*     Desc:  get an entry from the Map - resets the cached vector
*
*     Notes: None
*
*     File:  INGwSmStsMap.C
*
*******************************************************************************/
int
INGwSmStsMap::get (long alKey, int aiPosition, INGwSmStsOid *(&apRetVal))
{
  INGwSmOidMap::iterator leMapIter;

  leMapIter = meOidMap.find (alKey);

  if (leMapIter == meOidMap.end())
    return BP_AIN_SM_FAIL;

  meCachedVector = (leMapIter->second); 

  miCachedPosition = 0;

  INGwSmOidVector::iterator leOidIter;

  for (leOidIter = meCachedVector->begin();
       leOidIter != meCachedVector->end();
       leOidIter++)
  {
    if (miCachedPosition == aiPosition)
    {
      apRetVal = *(leOidIter);
      return BP_AIN_SM_OK;
    }
    miCachedPosition++;
  }

  miCachedPosition = 0;
  return BP_AIN_SM_FAIL;
}


/******************************************************************************
*
*     Fun:   getNext()
*
*     Desc:  get next entry from the cached -list
*            NOTE : should not be used in multi-threaded SM
*
*     Notes: None
*
*     File:  INGwSmStsMap.C
*
*******************************************************************************/
int
INGwSmStsMap::getNext (INGwSmStsOid *(&apRetVal))
{
  if (miCachedPosition == -1)
    return BP_AIN_SM_FAIL;

  INGwSmOidVector::iterator leOidIter;
 
  miCachedPosition++;

  int liCount = 0;
  for (leOidIter = meCachedVector->begin();
       leOidIter != meCachedVector->end();
       leOidIter++)
  {
    if (miCachedPosition == liCount)
    {
      apRetVal = *(leOidIter);
      return BP_AIN_SM_OK;
    }
    liCount++;
  }

  miCachedPosition = 0;
  return BP_AIN_SM_FAIL;
}


/******************************************************************************
*
*     Fun:   getByValue()
*
*     Desc:  get by OID Value
*
*     Notes: None
*
*     File:  INGwSmStsMap.C
*
*******************************************************************************/
int
INGwSmStsMap::getByValue (long alKey, string &apOidValue, 
                           INGwSmStsOid *(&apRetVal))
{
  INGwSmOidMap::iterator leMapIter;
  
  leMapIter = meOidMap.find (alKey);

  if (leMapIter == meOidMap.end())
    return BP_AIN_SM_FAIL;
  
  INGwSmOidVector *leVector
                    = (leMapIter->second);
  
  INGwSmOidVector::iterator leOidIter;
  
  for (leOidIter = leVector->begin();
       leOidIter != leVector->end();
       leOidIter++) 
  {    
    apRetVal = *(leOidIter);
    if (apOidValue == apRetVal->oidString)
    {
      return BP_AIN_SM_OK;
    } 
  }

  apRetVal = 0;
  return BP_AIN_SM_FAIL;
}


/******************************************************************************
*
*     Fun:   remove()
*
*     Desc:  remove entry from Map
*
*     Notes: None
*
*     File:  INGwSmStsMap.C
*
*******************************************************************************/
int
INGwSmStsMap::remove (long alKey, INGwSmStsOid *(&apValue))
{
  INGwSmOidMap::iterator leMapIter;
  
  leMapIter = meOidMap.find (alKey);
  
  if (leMapIter == meOidMap.end())
    return BP_AIN_SM_FAIL;

  INGwSmOidVector *leVector
                      = (leMapIter->second);

  INGwSmOidVector::iterator leOidIter;
  INGwSmStsOid *lpOid;

  for (leOidIter = leVector->begin();
       leOidIter != leVector->end();
       leOidIter++)
  {
    lpOid = *(leOidIter);
    if (lpOid == apValue)
    {
      leVector->erase (leOidIter);
      if (leVector->empty())
      {
        delete leVector;
        meOidMap.erase (leMapIter);
      }
      return BP_AIN_SM_OK;
    }
  }

  return BP_AIN_SM_FAIL;
}


/******************************************************************************
*
*     Fun:   removeByValue()
*
*     Desc:  remove OID from Map by OID value
*
*     Notes: None
*
*     File:  INGwSmStsMap.C
*
*******************************************************************************/
int
INGwSmStsMap::removeByValue (long alKey, string &apOidValue, INGwSmStsOid *(&apRetVal))
{
  INGwSmOidMap::iterator leMapIter;
 
  leMapIter = meOidMap.find (alKey);
 
  if (leMapIter == meOidMap.end())
    return BP_AIN_SM_FAIL;

  INGwSmOidVector *leVector
                      = (leMapIter->second);

  INGwSmOidVector::iterator leOidIter;
  INGwSmStsOid *lpOid;

  for (leOidIter = leVector->begin();
       leOidIter != leVector->end();
       leOidIter++)
  {
    lpOid = *(leOidIter);
    if (apOidValue == lpOid->oidString)
    {
      leVector->erase (leOidIter);
      if (leVector->empty())
      {
        delete leVector;
        meOidMap.erase (leMapIter);
      }
      return BP_AIN_SM_OK;
    }
  }

  return BP_AIN_SM_FAIL;
}

/******************************************************************************
*
*     Fun:   getStsHashKey()
*
*     Desc:  generate the hash key for the map
*
*     Notes: None
*
*     File:  INGwSmStsMap.C
*
*******************************************************************************/
long
INGwSmStsMap::getStsHashKey (int aiLayer, int aiOper, int aiLevel)
{
  //check the size of long so that the hash will be correct
  int liSizeLong = sizeof (long);

  long liHashKey = 0;

  if (liSizeLong >= 4)
  {
    liHashKey = (aiLayer << 24) + (aiOper << 8) + aiLevel;
  }
  else
  { 
    logger.logMsg (ERROR_FLAG, 0, 
      "The Size of Key for long < 4 bytes");
    liHashKey = -1;
  }
  
  return liHashKey;
}


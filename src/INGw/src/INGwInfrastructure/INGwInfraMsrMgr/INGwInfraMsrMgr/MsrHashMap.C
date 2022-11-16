#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraMsrMgr");
/************************************************************************
     Name:     Measurement Hash map - implementation

     Type:     C implementation file

     Desc:     This file provides access to Measurement hashMap Node

     File:     MsrHashMap.C

     Sid:      MsrHashMap.C 0  -  11/14/03

     Prg:      gs

************************************************************************/

#include <INGwInfraMsrMgr/MsrIncludes.h>
#include <INGwInfraMsrMgr/MsrHashMap.h>
#include <libelf.h>
#include <INGwInfraMsrMgr/MsrUpdateMsg.h>
#include <INGwInfraMsrMgr/MsrWorkerThread.h>

using namespace std;

//C'tor
MsrHashMap::MsrHashMap(MsrWorkerThread *apWorker):
mpWorkerThread (apWorker)
{
  for (int liCount = 0;
       liCount < MAX_HASH_BUCKET_SIZE; liCount++)
  {
    mHashMap[liCount].miSize = 0;
    mHashMap[liCount].mpHead = 0;
  }

  for (int liCount = 0;
       liCount < MAX_HASH_LOCKS; liCount++)
  {
    pthread_rwlock_init (&(mBucketLock [liCount]), 0);
  }

  pthread_mutex_init (&mCounterTypeMapLock, 0);
}

//D'tor
MsrHashMap::~MsrHashMap()
{
  for (int liCount = 0;
       liCount < MAX_HASH_BUCKET_SIZE; liCount++)
  {
    pthread_rwlock_wrlock (&(mBucketLock [liCount % MAX_HASH_LOCKS]));
    MsrHashNode *lpNode = mHashMap[liCount].mpHead;
    MsrHashNode *lpTmpNode = 0;
    while (lpNode)
    {
      lpTmpNode = lpNode->mpNext;
      delete lpNode;
      lpNode = lpTmpNode;
    }
    mHashMap[liCount].miSize = 0;
    mHashMap[liCount].mpHead = 0;
    pthread_rwlock_unlock (&(mBucketLock [liCount % MAX_HASH_LOCKS]));
  }

  for (int liCount = 0;
       liCount < MAX_HASH_LOCKS; liCount++)
  {
    pthread_rwlock_destroy (&(mBucketLock [liCount]));
  }


  pthread_mutex_lock (&mCounterTypeMapLock);
  mCounterTypeMap.clear();
  pthread_mutex_unlock (&mCounterTypeMapLock);
  pthread_mutex_destroy (&mCounterTypeMapLock);
}

void
MsrHashMap::dump()
{
  cout << "HASH TABLE DISTRIBUTION BEGINS" << endl;
  unsigned long lulTotal = 0;
  int liSize = 0;

  int arr [MAX_HASH_BUCKET_SIZE];

  memset (&arr[0], 0, sizeof (int) * MAX_HASH_BUCKET_SIZE);

  for (int liCount = 0;
       liCount < MAX_HASH_BUCKET_SIZE; liCount++)
  {
    pthread_rwlock_rdlock (&(mBucketLock [liCount % MAX_HASH_LOCKS]));
    liSize = mHashMap[liCount].miSize;
    pthread_rwlock_unlock (&(mBucketLock [liCount % MAX_HASH_LOCKS]));

    cout << liSize << " ";
    lulTotal += liSize;

    arr [liSize]++;
  }
  cout << endl;
  for (int liCount = 0; liCount < MAX_HASH_BUCKET_SIZE; liCount++)
  {
    if (arr [liCount] != 0)
      cout << "Buckets of Size " << liCount << " = " << arr [liCount] << endl;
  }
  cout << endl << "Total Params = " << lulTotal << endl;
  cout << "HASH TABLE DISTRIBUTION ENDS" << endl;
}

//create a param
MsrHashNode *
MsrHashMap::createParam (int aiCounterType, std::string &astrCounter, 
                               std::string &astrEntity,
                               std::string &astrParam)
{
  unsigned long lulHash = getHash (astrCounter, astrEntity, astrParam);
  unsigned int liBucket = lulHash % MAX_HASH_BUCKET_SIZE;

  pthread_rwlock_wrlock (&(mBucketLock [liBucket % MAX_HASH_LOCKS]));

  MsrHashNode *lpNode = new MsrHashNode (aiCounterType, astrCounter,
                         astrEntity, astrParam, lulHash);

  if (lpNode)
  {
    int liIndex = lpNode->getIndex();

    if (liIndex == -1)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "Unable to create a ValueNode for Counter <%s, %s, %s>",
        astrCounter.c_str(), astrEntity.c_str(), astrParam.c_str());
      delete lpNode;
      pthread_rwlock_unlock (&(mBucketLock [liBucket % MAX_HASH_LOCKS]));
      return 0;
    }

    //post an update msg
    MsrUpdateMsg *lpMsg1 = new MsrUpdateMsg;
    lpMsg1->meMsgType = MsrUpdateMsg::addEntity;
    lpMsg1->upd.ent.mstrCounter = astrCounter;
    lpMsg1->upd.ent.mstrId = astrEntity;

    MsrUpdateMsg *lpMsg2 = new MsrUpdateMsg;
    lpMsg2->meMsgType = MsrUpdateMsg::addParam;
    lpMsg2->upd.param.mstrCounter = astrCounter;
    lpMsg2->upd.param.mstrEntity = astrEntity;
    lpMsg2->upd.param.mstrId = astrParam;
    lpMsg2->upd.param.miPoolIndex = liIndex;

    mpWorkerThread->postUpdateMsg (lpMsg1);
    mpWorkerThread->postUpdateMsg (lpMsg2);

    mHashMap [liBucket].miSize ++;

    //insert at the beginning of the list
    if (mHashMap [liBucket].mpHead)
    {
		// BPInd10676 --- Problem in link list creation for same hash code eles
      lpNode->mpNext = mHashMap [liBucket].mpHead;//->mpNext;
      mHashMap [liBucket].mpHead = lpNode;
    }
    else
    {
      mHashMap [liBucket].mpHead = lpNode;
      mHashMap [liBucket].mpHead->mpNext = 0;
    }

  }

  pthread_rwlock_unlock (&(mBucketLock [liBucket % MAX_HASH_LOCKS]));

  return lpNode;
}

int 
MsrHashMap::addCounterType (string &astrCounter, int aiCounterType)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrHashMap::addCounterType <%s, %d>",
    astrCounter.c_str(), aiCounterType);

  pthread_mutex_lock (&mCounterTypeMapLock);

  mCounterTypeMap [astrCounter] = aiCounterType;

  pthread_mutex_unlock (&mCounterTypeMapLock);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrHashMap::addCounterType");

  return MSR_FAIL;
}

int 
MsrHashMap::getCounterType (string &astrCounter)
{ 
  map <string, int>::iterator iter;
  int liType = -1;

  pthread_mutex_lock (&mCounterTypeMapLock);
  
  iter = mCounterTypeMap.find (astrCounter);

  if (iter != mCounterTypeMap.end())
  {
    liType = iter->second;
  }

  pthread_mutex_unlock (&mCounterTypeMapLock);
  
  return liType;
} 


//return the hashId for a set of values
unsigned long 
MsrHashMap::getHash (std::string &astrCounter, 
                           std::string &astrEntity,
                           std::string &astrParam)
{
  string lstr = astrCounter + astrEntity + astrParam;

  return elf_hash (lstr.c_str());
}

//overloaded increment, decrement, setvalue, getvalue, 
//setstatus, getstatus
int 
MsrHashMap::increment (std::string &astrCounter, std::string &astrEntity,
                   std::string &astrParam, int aiFactor)
{
  unsigned long lulHash = getHash (astrCounter, astrEntity, astrParam);

  return increment (lulHash, astrCounter, astrEntity, astrParam, aiFactor);
}

int 
MsrHashMap::increment (unsigned long aulHashKey, std::string &astrCounter, 
                       std::string &astrEntity, std::string &astrParam, 
                       int aiFactor)
{
  unsigned int liBucket = aulHashKey % MAX_HASH_BUCKET_SIZE;
  MsrHashNode *lpNode;
  int liRetVal = MSR_FAIL;
  bool lbFound = false;


  pthread_rwlock_rdlock (&(mBucketLock [liBucket % MAX_HASH_LOCKS]));

  lpNode = mHashMap [liBucket].mpHead;

  while (lpNode)
  {
    if (lpNode->mulHashId == aulHashKey &&
        lpNode->mstrCounter == astrCounter &&
        lpNode->mstrEntity == astrEntity &&
        lpNode->mstrParam == astrParam)
    {
      liRetVal = lpNode->increment (aiFactor);
      lbFound = true;
      break;
    }

    lpNode = lpNode->mpNext;
  }

  pthread_rwlock_unlock (&(mBucketLock [liBucket % MAX_HASH_LOCKS]));

  if (lbFound == false) //neet to create a new entity or param
  {
    int liType = getCounterType (astrCounter);
    if (liType == -1)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "Counter <%s> does not exist.", astrCounter.c_str());

      return MSR_FAIL;
    }

    if ((lpNode = createParam (liType, astrCounter, 
                              astrEntity, astrParam)) != 0)
    {
      pthread_rwlock_rdlock (&(mBucketLock [liBucket % MAX_HASH_LOCKS]));

      liRetVal = lpNode->increment (aiFactor);

      pthread_rwlock_unlock (&(mBucketLock [liBucket % MAX_HASH_LOCKS]));
    }
  }

  return liRetVal;
}

int 
MsrHashMap::decrement (std::string &astrCounter, std::string &astrEntity,
                   std::string &astrParam, int aiFactor)
{
  //if(0 == astrCounter.compare("Active Call")) {
  //  logger.logINGwMsg(false, TRACE_FLAG,0,"remft decrement Active Call");
  //}
  unsigned long lulHash = getHash (astrCounter, astrEntity, astrParam);

  return decrement (lulHash, astrCounter, astrEntity, astrParam, aiFactor);
}
    
int 
MsrHashMap::decrement (unsigned long aulHashKey, std::string &astrCounter, 
                       std::string &astrEntity, std::string &astrParam,
                       int aiFactor)
{
  //if(0 == astrCounter.compare("Active Call")) {
  //  logger.logINGwMsg(false, TRACE_FLAG,0,"remft decrement Active Call");
  //}
  unsigned int liBucket = aulHashKey % MAX_HASH_BUCKET_SIZE;
  MsrHashNode *lpNode;
  int liRetVal = MSR_FAIL;
  bool lbFound = false;


  pthread_rwlock_rdlock (&(mBucketLock [liBucket % MAX_HASH_LOCKS]));

  lpNode = mHashMap [liBucket].mpHead;

  while (lpNode)
  { 
    if (lpNode->mulHashId == aulHashKey &&
        lpNode->mstrCounter == astrCounter &&
        lpNode->mstrEntity == astrEntity &&
        lpNode->mstrParam == astrParam)
    { 
      liRetVal = lpNode->decrement (aiFactor);
      lbFound = true;
      break;
    } 

    lpNode = lpNode->mpNext;
  } 

  pthread_rwlock_unlock (&(mBucketLock [liBucket % MAX_HASH_LOCKS]));

  if (lbFound == false) //neet to create a new entity or param
  { 
    int liType = getCounterType (astrCounter);
    if (liType == -1)
    { 
      logger.logMsg (ERROR_FLAG, 0,
        "Counter <%s> does not exist.", astrCounter.c_str());
        
      return MSR_FAIL; 
    }       
        
    if ((lpNode = createParam (liType, astrCounter,
                              astrEntity, astrParam)) != 0)
    {   
      pthread_rwlock_rdlock (&(mBucketLock [liBucket % MAX_HASH_LOCKS]));
    
      liRetVal = lpNode->decrement (aiFactor);
    
      pthread_rwlock_unlock (&(mBucketLock [liBucket % MAX_HASH_LOCKS]));
    } 
  }     


  return liRetVal;
}

int 
MsrHashMap::setValue (std::string &astrCounter, std::string &astrEntity,
                   std::string &astrParam, unsigned long aulValue)
{
      logger.logMsg (VERBOSE_FLAG, 0,
        "setValue():counter<%s> value[%d]",astrCounter.c_str(),aulValue);

  unsigned long lulHash = getHash (astrCounter, astrEntity, astrParam);

  return setValue (lulHash, astrCounter, astrEntity, astrParam, aulValue);
}

int 
MsrHashMap::setValue (unsigned long aulHashKey, std::string &astrCounter, 
                      std::string &astrEntity, std::string &astrParam, 
                      unsigned long aulValue)
{
  unsigned int liBucket = aulHashKey % MAX_HASH_BUCKET_SIZE;
  MsrHashNode *lpNode;
  int liRetVal = MSR_FAIL;
  bool lbFound = false;
  

  pthread_rwlock_rdlock (&(mBucketLock [liBucket % MAX_HASH_LOCKS]));


  lpNode = mHashMap [liBucket].mpHead;

  while (lpNode)
  { 
#if 0  
      logger.logMsg (ERROR_FLAG, 0,
        "MsrHashMap::setValue lpNode counter ==	<%s> hash id == [%d] entity = [%s] param = [%s]	",lpNode->mstrCounter.c_str(),lpNode->mulHashId,lpNode->mstrEntity.c_str(),lpNode->mstrParam.c_str());
      logger.logMsg (ERROR_FLAG, 0,
        "MsrHashMap::setValue params counter ==	<%s> hash id == [%d] entity ==	[%s] param = [%s]",astrCounter.c_str(),aulHashKey,astrEntity.c_str(),astrParam.c_str());
#endif		
  
    if (lpNode->mulHashId == aulHashKey &&
        lpNode->mstrCounter == astrCounter &&
        lpNode->mstrEntity == astrEntity &&
        lpNode->mstrParam == astrParam)
    { 
      //logger.logMsg (ERROR_FLAG, 0,
      //  "MsrHashMap::setValue Found counter == <%s> value == [%d]",astrCounter.c_str(),aulValue);
	
      liRetVal = lpNode->setValue (aulValue);
      lbFound = true;
      break;
    } 

    lpNode = lpNode->mpNext;
  } 
  
  //logger.logMsg (ERROR_FLAG, 0,
  //      "MsrHashMap::setValue before releiving lock counter == <%s> value == [%d]",astrCounter.c_str(),aulValue);

  pthread_rwlock_unlock (&(mBucketLock [liBucket % MAX_HASH_LOCKS]));

  if (lbFound == false) //neet to create a new entity or param
  { 
    logger.logMsg (ERROR_FLAG, 0,
        "MsrHashMap::setValue Creating new entity/apram counter == <%s> value == [%d]",astrCounter.c_str(),aulValue);

    int liType = getCounterType (astrCounter);
    if (liType == -1)
    { 
      logger.logMsg (ERROR_FLAG, 0,
        "Counter <%s> does not exist.", astrCounter.c_str());
        
      return MSR_FAIL; 
    }       
    
	//logger.logMsg (ERROR_FLAG, 0,
    //    "MsrHashMap::setValue Before Creating pram counter == <%s> value == [%d]",astrCounter.c_str(),aulValue);
        
    if ((lpNode = createParam (liType, astrCounter,
                              astrEntity, astrParam)) != 0)
    {   
      pthread_rwlock_rdlock (&(mBucketLock [liBucket % MAX_HASH_LOCKS]));
    
      liRetVal = lpNode->setValue (aulValue);
    
      pthread_rwlock_unlock (&(mBucketLock [liBucket % MAX_HASH_LOCKS]));
    } 
  }     

  logger.logMsg (VERBOSE_FLAG, 0,
        "setValue():counter<%s> value[%d]",astrCounter.c_str(),aulValue);

  return liRetVal;
}

int 
MsrHashMap::setStatus (std::string &astrCounter, std::string &astrEntity,
                   std::string &astrParam, int aiStatus)
{
  unsigned long lulHash = getHash (astrCounter, astrEntity, astrParam);

  return setStatus (lulHash, astrCounter, astrEntity, astrParam, aiStatus);
}

int 
MsrHashMap::setStatus (unsigned long aulHashKey, std::string &astrCounter, 
                       std::string &astrEntity, std::string &astrParam, 
                       int aiStatus)
{
  unsigned int liBucket = aulHashKey % MAX_HASH_BUCKET_SIZE;
  MsrHashNode *lpNode;
  int liRetVal = MSR_FAIL;
  bool lbFound = false;


  pthread_rwlock_rdlock (&(mBucketLock [liBucket % MAX_HASH_LOCKS]));

  lpNode = mHashMap [liBucket].mpHead;

  while (lpNode)
  { 
    if (lpNode->mulHashId == aulHashKey &&
        lpNode->mstrCounter == astrCounter &&
        lpNode->mstrEntity == astrEntity &&
        lpNode->mstrParam == astrParam)
    { 
      liRetVal = lpNode->setStatus (aiStatus);
      lbFound = true;
      break;
    } 

    lpNode = lpNode->mpNext;
  } 

  pthread_rwlock_unlock (&(mBucketLock [liBucket % MAX_HASH_LOCKS]));
  
  if (lbFound == false) //neet to create a new entity or param
  { 
    int liType = getCounterType (astrCounter);
    if (liType == -1)
    { 
      logger.logMsg (ERROR_FLAG, 0,
        "Counter <%s> does not exist.", astrCounter.c_str());
        
      return MSR_FAIL; 
    }       
        
    if ((lpNode = createParam (liType, astrCounter,
                              astrEntity, astrParam)) != 0)
    {   
      pthread_rwlock_rdlock (&(mBucketLock [liBucket % MAX_HASH_LOCKS]));
    
      liRetVal = lpNode->setStatus (aiStatus);
    
      pthread_rwlock_unlock (&(mBucketLock [liBucket % MAX_HASH_LOCKS]));
    } 
  }     

  return liRetVal;
}

unsigned long 
MsrHashMap::getValue (std::string &astrCounter, std::string &astrEntity,
                   std::string &astrParam)
{
  unsigned long lulHash = getHash (astrCounter, astrEntity, astrParam);

  return getValue (lulHash, astrCounter, astrEntity, astrParam);
}

unsigned long 
MsrHashMap::getValue (unsigned long aulHashKey, std::string &astrCounter, 
                      std::string &astrEntity, std::string &astrParam)
{
  unsigned int liBucket = aulHashKey % MAX_HASH_BUCKET_SIZE;
  MsrHashNode *lpNode;
  unsigned long lulRetVal = 0;
  bool lbFound = false;

  pthread_rwlock_rdlock (&(mBucketLock [liBucket % MAX_HASH_LOCKS]));

  lpNode = mHashMap [liBucket].mpHead;

  while (lpNode)
  { 
    if (lpNode->mulHashId == aulHashKey &&
        lpNode->mstrCounter == astrCounter &&
        lpNode->mstrEntity == astrEntity &&
        lpNode->mstrParam == astrParam)
    { 
      lulRetVal = lpNode->getValue ();
      lbFound = true;
      break;
    } 

    lpNode = lpNode->mpNext;
  } 

  pthread_rwlock_unlock (&(mBucketLock [liBucket % MAX_HASH_LOCKS]));

  return lulRetVal;
}

int 
MsrHashMap::getStatus (std::string &astrCounter, std::string &astrEntity,
                   std::string &astrParam)
{
  unsigned long lulHash = getHash (astrCounter, astrEntity, astrParam);

  return getStatus (lulHash, astrCounter, astrEntity, astrParam);
}

int 
MsrHashMap::getStatus (unsigned long aulHashKey, std::string &astrCounter, 
                       std::string &astrEntity, std::string &astrParam)
{
  unsigned int liBucket = aulHashKey % MAX_HASH_BUCKET_SIZE;
  MsrHashNode *lpNode;
  int liStatus = 0;
  bool lbFound = false;

  pthread_rwlock_rdlock (&(mBucketLock [liBucket % MAX_HASH_LOCKS]));

  lpNode = mHashMap [liBucket].mpHead;

  while (lpNode)
  { 
    if (lpNode->mulHashId == aulHashKey &&
        lpNode->mstrCounter == astrCounter &&
        lpNode->mstrEntity == astrEntity &&
        lpNode->mstrParam == astrParam)
    { 
      liStatus = lpNode->getStatus ();
      lbFound = true;
      break;
    } 

    lpNode = lpNode->mpNext;
  } 

  pthread_rwlock_unlock (&(mBucketLock [liBucket % MAX_HASH_LOCKS]));

  return liStatus;
}


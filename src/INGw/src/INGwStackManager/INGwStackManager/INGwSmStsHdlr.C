//#include <CCMUtil/BpLogger.h>
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSmStsHdlr");

/************************************************************************
   Name:     INAP Stack Manager Statistics Handler - Impl
 
   Type:     C impl file
 
   Desc:     Implementation for Statistics handler

   File:     INGwSmStsHdlr.h

   Sid:      INGwSmStsHdlr.h 0  -  03/27/03 

   Prg:      gs

************************************************************************/

#include "INGwStackManager/INGwSmStsHdlr.h"
#include "INGwStackManager/INGwSmBlkConfig.h"
#include "INGwStackManager/INGwSmStsMap.h"
#include "INGwStackManager/INGwSmReqHdlr.h"
#include "INGwStackManager/INGwSmRepository.h"
#include "INGwStackManager/INGwSmRequestTable.h"
#include <INGwInfraMsrMgr/MsrMgr.h>

#if (defined (_BP_STAT_MGR_) && !defined (STUBBED))
#include "INGwInfraStatMgr/INGwIfrSmStatMgr.h"
#endif

#if (defined (_BP_AIN_PROVIDER_) && !defined (STUBBED))
#include "INGwTcapProvider/INGwTcapProvider.h"

#else
class INGwTcapProvider
{
};
#endif



using namespace std;
#ifdef __cplusplus
#ifndef __CCPU_CPLUSPLUS
extern "C" {
#endif
#endif

  extern short iNGwSmActvStsTmr ();

#ifdef __cplusplus
#ifndef __CCPU_CPLUSPLUS
}
#endif
#endif

/******************************************************************************
*
*     Fun:   INGwSmStsHdlr()
*
*     Desc:  Default Contructor
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
INGwSmStsHdlr::INGwSmStsHdlr(INGwSmDistributor* arDist, int aiLayer):
    INGwSmReqHdlr (*arDist, aiLayer),
    mpDist (arDist)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::INGwSmStsHdlr");

  mpStsMap = new INGwSmStsMap;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::INGwSmStsHdlr");
}


/******************************************************************************
*
*     Fun:   ~INGwSmStsHdlr()
*
*     Desc:  Default Destructor1
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
INGwSmStsHdlr::~INGwSmStsHdlr()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::~INGwSmStsHdlr");


  if (mpStsMap)
    delete mpStsMap;
  mpStsMap = 0;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::~INGwSmStsHdlr");
}

/******************************************************************************
*
*     Fun:   stop()
*
*     Desc:  stop the statistics collection
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int
INGwSmStsHdlr::stop()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::stop");

  //enable the statistics collection
  mbIsRunning = false;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::stop");

  return BP_AIN_SM_OK;
}


/******************************************************************************
*
*     Fun:   initialize()
*
*     Desc:  initialize routine to be invoked once only
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int 
INGwSmStsHdlr::initialize(int aiTransId)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::initialize transId <%d>",aiTransId);


  /*
   * get the pointer of statistics manager in CCM
   * which is a singleton
   */

#if (defined (_BP_STAT_MGR_) && !defined (STUBBED))
  INGwIfrSmStatMgr &lrStatMgr = INGwIfrSmStatMgr::instance();
#endif

  miTransId = aiTransId;

  /* 
   * Initialize the statistics Map
   */

  mpStsMap->initialize();

  INGwSmStsOidList leOidList;


  //extract the OIDs from the DOM Tree in the repository
  mpDist->getSmRepository()->getStatisticsList (leOidList);

  INGwSmStsOid *lpStsOid;

  //get the current level for the statistics
  int liLevel = mpDist->getSmRepository()->getStsLevel ();

  /*
   * After getting the level and oid list from Repository
   * we need to set the operation mask (bitmap). only the oids
   * which are having the levels matching with the existing level
   * will be used to mask the operation bit map.
   */
  mlBitMap = 0;
  INGwSmStsOidList::iterator iter;
  for (iter = leOidList.begin(); iter != leOidList.end(); iter++)
  {
    lpStsOid = *(iter);

    //set the operation mask
    if (lpStsOid->level <= liLevel &&
        lpStsOid->layer == BP_AIN_SM_AIN_LAYER)
    {
      if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_GENSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_IE_GEN;
      }
      else
      {
        logger.logMsg (TRACE_FLAG, 0,
          "<%s>: OID is wrongly configured. Ignoring it.", 
          lpStsOid->oidString);
        continue;
      }
    }
    else if (lpStsOid->level <= liLevel &&
        lpStsOid->layer == BP_AIN_SM_TCA_LAYER)
    {
      if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_GENSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_ST_GEN;
      }
      else
      {
        logger.logMsg (TRACE_FLAG, 0,
          "<%s>: OID is wrongly configured. Ignoring it.", 
          lpStsOid->oidString);
        continue;
      }
    }
    else if (lpStsOid->level <= liLevel &&
        lpStsOid->layer == BP_AIN_SM_SCC_LAYER)
    {
      if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_GENSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_SP_GEN;
      }
      else if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_USPSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_SP_USAP;
      }
      else if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_LSPSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_SP_LSAP;
      }
      else if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_RTESTS)
      {
        mlBitMap = mlBitMap | STS_MASK_SP_RTE;
      }
//
    else if (lpStsOid->level <= liLevel &&
        lpStsOid->layer == BP_AIN_SM_LDF_MTP3_LAYER)
    {
      if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_GENSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_DN_GEN;
      }
      else
      {
        logger.logMsg (TRACE_FLAG, 0,
          "<%s>: OID is wrongly configured. Ignoring it.", 
          lpStsOid->oidString);
        continue;
      }
    }

    else if (lpStsOid->level <= liLevel &&
        lpStsOid->layer == BP_AIN_SM_LDF_M3UA_LAYER)
    {
      if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_GENSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_DV_GEN;
      }
      else
      {
        logger.logMsg (TRACE_FLAG, 0,
          "<%s>: OID is wrongly configured. Ignoring it.", 
          lpStsOid->oidString);
        continue;
      }
    }

    else if (lpStsOid->level <= liLevel &&
        lpStsOid->layer == BP_AIN_SM_MR_LAYER)
    {
      if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_GENSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_MR_GEN;
      }
      else
      {
        logger.logMsg (TRACE_FLAG, 0,
          "<%s>: OID is wrongly configured. Ignoring it.", 
          lpStsOid->oidString);
        continue;
      }
    }
//
      else
      {
        logger.logMsg (TRACE_FLAG, 0,
          "<%s>: OID is wrongly configured. Ignoring it.", 
          lpStsOid->oidString);
        continue;
      }
    }
    else if (lpStsOid->level <= liLevel &&
        lpStsOid->layer == BP_AIN_SM_M3U_LAYER)
    {

      if(TRANSPORT_TYPE_MTP == 
           mpDist->getSmRepository()->getTransportType()) {
        continue;
      }

      if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_GENSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_IT_GEN;
      }
      else if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_USPSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_IT_USAP;
      }
      else if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_LSPSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_IT_LSAP;
      }
      else if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_PSPSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_IT_PSPST;
      }
      else
      {
        logger.logMsg (TRACE_FLAG, 0,
          "<%s>: OID is wrongly configured. Ignoring it.", 
          lpStsOid->oidString);
        continue;
      }
    }
    else if (lpStsOid->level <= liLevel &&
        lpStsOid->layer == BP_AIN_SM_MTP3_LAYER)
    {

      if(TRANSPORT_TYPE_SIGTRAN == 
           mpDist->getSmRepository()->getTransportType()) {
        continue;
      }

      if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_LNKSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_SN_LINK;
      }
      else if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_RTESTS)
      {
        mlBitMap = mlBitMap | STS_MASK_SN_RTE;
      }
      else if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_LSESTS)
      {
        mlBitMap = mlBitMap | STS_MASK_SN_LNKSET;
      }
      else if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_GENSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_SN_SP;
      }
      else
      {
        logger.logMsg (TRACE_FLAG, 0,
          "<%s>: OID is wrongly configured. Ignoring it.", 
          lpStsOid->oidString);
        continue;
      }
    }
    else if (lpStsOid->level <= liLevel &&
        lpStsOid->layer == BP_AIN_SM_SCT_LAYER)
    {
      if(TRANSPORT_TYPE_SIGTRAN != 
           mpDist->getSmRepository()->getTransportType()) {
        continue;
      }

      if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_GENSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_SB_GEN;
      }
      else if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_USPSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_SB_USAP;
      }
      else if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_LSPSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_SB_LSAP;
      }
      else
      {
        logger.logMsg (TRACE_FLAG, 0,
          "<%s>: OID is wrongly configured. Ignoring it.", 
          lpStsOid->oidString);
        continue;
      }
    }
    else if (lpStsOid->level <= liLevel &&
        lpStsOid->layer == BP_AIN_SM_TUC_LAYER)
    {

      if(TRANSPORT_TYPE_SIGTRAN != 
           mpDist->getSmRepository()->getTransportType()) {
        continue;
      }

      if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_GENSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_HI_GEN;
      }
      else if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_USPSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_HI_USAP;
      }
      else
      {
        logger.logMsg (TRACE_FLAG, 0,
          "<%s>: OID is wrongly configured. Ignoring it.", 
          lpStsOid->oidString);
        continue;
      }
    }

    long liHashKey = mpStsMap->getStsHashKey (lpStsOid->layer, lpStsOid->operation,
                                   lpStsOid->level);

    if (liHashKey == -1)
      continue;

    /*
     * Register the OID with the CCM Statistics Manager
     */
#if (defined (_BP_STAT_MGR_) && !defined (STUBBED))

    /*
     * Need to create a new stat Param and register with Stat Mgr
     */
    INGwIfrSmStatParam *lpStatParam = new INGwIfrSmStatParam;

    //set the oid string for the param
    lpStatParam->setOid (lpStsOid->oidString);

    //enable the Statistics as EMS Param with value to be reset on read
    //we will be using cumulative statistics since StatMgr will 
    //keep the total of the statistics which Stack Mgr will be only
    //sending the statistics since the last retrieved ones.
    lpStatParam->setEmsParam (true);
    lpStatParam->setSnapShot (false);
    lpStatParam->setResetOnRead (false);

    //store the index returned for this parameter since it will be used
    //to set the value later on. An index of -1 will indicate an error
    //during the registration
    lpStsOid->index = lrStatMgr.addStatParam (lpStatParam);

    if (lpStsOid->index == -1)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "Unable to register OID <%s> with the Stat Mgr",
        lpStsOid->oidString);
    }
#else
    lpStsOid->index = -1;
#endif

    //initialize the statistics Value
    lpStsOid->stsValue = 0;
    lpStsOid->prevValue = 0;

    //logger.logMsg (VERBOSE_FLAG, 0,
    //  "%s: Inserting into statistics Map",
    //  lpStsOid->oidString);

    //insert into map with hashkey
    mpStsMap->insert (liHashKey, lpStsOid);

    //insert into map with level as key
    mpStsMap->insert (lpStsOid->level, lpStsOid);

  }

  logger.logMsg (ERROR_FLAG, 0,
    "The Statistics Operation Bit Mask = <%X>", mlBitMap);

  mlBitMapCopy = mlBitMap;

  //enable the statistics collection here
	// after initializing bit mask else 
	// false messages will get posted.
  mbIsRunning = true;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::initialize");
  return BP_AIN_SM_OK;
}

/******************************************************************************
*     
*     Fun:   setOperationMask()
*     
*     Desc:  set the operation Bit Mask
*     
*     Notes: None
*     
*     File:  INGwSmStsHdlr.C
*     
*******************************************************************************/
int   
INGwSmStsHdlr::setOperationMask (int aiLevel)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::setOperationMask");
                                   
  //here the hash key will be same as the level
  int liHashKey = aiLevel;

  int liRetVal;
  INGwSmStsOidList* lpOidList = mpStsMap->getList (liHashKey, liRetVal);

  if (liRetVal != BP_AIN_SM_OK || lpOidList->empty())
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to retrieve statistics list from Map<%d>", liHashKey);
    return BP_AIN_SM_FAIL;
  }

  INGwSmStsOidList::iterator leOidIter;

  //reset the bit map
  mlBitMap = 0;

  for (leOidIter = lpOidList->begin();
       leOidIter != lpOidList->end();
       leOidIter++)
  {
    INGwSmStsOid *lpStsOid = *(leOidIter);

    //set the operation mask
    if (lpStsOid->layer == BP_AIN_SM_AIN_LAYER)
    {
      if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_GENSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_IE_GEN;
      }
      else
      {
        continue;
      }
    }
    else if (lpStsOid->layer == BP_AIN_SM_TCA_LAYER)
    {
      if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_GENSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_ST_GEN;
      }
      else
      {
        continue;
      }
    }
    else if (lpStsOid->layer == BP_AIN_SM_SCC_LAYER)
    {
      if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_GENSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_SP_GEN;
      }
      else if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_USPSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_SP_USAP;
      }
      else if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_LSPSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_SP_LSAP;
      }
      else if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_RTESTS)
      {
        mlBitMap = mlBitMap | STS_MASK_SP_RTE;
      }
      else
      {
        continue;
      }
    }
    else if (lpStsOid->layer == BP_AIN_SM_M3U_LAYER)
    {
      if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_GENSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_IT_GEN;
      }
      else if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_USPSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_IT_USAP;
      }
      else if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_LSPSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_IT_LSAP;
      }
      else if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_PSPSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_IT_PSPST;
      }
      else
      {
        continue;
      }
    }
    else if (lpStsOid->layer == BP_AIN_SM_MTP3_LAYER)
    {
      if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_LNKSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_SN_LINK;
      }
      else if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_RTESTS)
      {
        mlBitMap = mlBitMap | STS_MASK_SN_RTE;
      }
      else if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_LSESTS)
      {
        mlBitMap = mlBitMap | STS_MASK_SN_LNKSET;
      }
      else if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_SGPSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_SN_SP;
      }
      else
      {
        logger.logMsg (TRACE_FLAG, 0,
          "<%s>: OID is wrongly configured. Ignoring it.", 
          lpStsOid->oidString);
        continue;
      }
    }
    else if (lpStsOid->layer == BP_AIN_SM_SCT_LAYER)
    {
      if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_GENSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_SB_GEN;
      }
      else if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_USPSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_SB_USAP;
      }
      else if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_LSPSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_SB_LSAP;
      }
      else
      {
        continue;
      }
    }
    else if (lpStsOid->layer == BP_AIN_SM_TUC_LAYER)
    {
      if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_GENSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_HI_GEN;
      }
      else if (lpStsOid->operation == BP_AIN_SM_SUBTYPE_USPSTS)
      {
        mlBitMap = mlBitMap | STS_MASK_HI_USAP;
      }
      else
      {
        continue;
      }
    }
  }


  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::setOperationMask");
  return BP_AIN_SM_OK;
}   

/******************************************************************************
*
*     Fun:   INGwSmStsHdlr::updateStsMgr()
*
*     Desc:  update the CCM Statistics Manager for the OID
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int
INGwSmStsHdlr::updateStsMgr (INGwSmStsOid *apOid)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::updateStsMgr");

  /*
   * update only if the new value is different from the prev value
   */
  if (apOid == 0)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "OID Passed to be updated in STS Manager is NULL");
    return BP_AIN_SM_FAIL;
  }

  if (apOid->prevValue == apOid->stsValue)
  {
    logger.logMsg (WARNING_FLAG, 0,
      "The Value of OID <%s> has not changed from <%ld>",
      apOid->oidString, apOid->stsValue);

    return BP_AIN_SM_FAIL;
  }

  logger.logMsg (VERBOSE_FLAG, 0,
    "Updating the STAT Mgr <%s - %d> : %ld", 
    apOid->oidString, apOid->index, apOid->stsValue);

#if (defined (_BP_STAT_MGR_) && !defined (STUBBED))

  INGwIfrSmStatMgr &lrStatMgr = INGwIfrSmStatMgr::instance();
  int liMgrValue = 0;

  if (apOid->index == -1)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "The index is -1 for OID <%s>", apOid->oidString);

    return BP_AIN_SM_FAIL;
  }

  ThresholdIndication leRetVal = 
          lrStatMgr.increment (apOid->index, liMgrValue, apOid->stsValue);

  //check if any error occurred
  if (leRetVal == NoThresholdExceeded)
  {
    logger.logMsg (VERBOSE_FLAG, 0,
      "OID <%s> has been successfully updated in the Stat Mgr",
      apOid->oidString);
  }
  else if (leRetVal == UpperThresholdExceeded)
  {
    logger.logMsg (WARNING_FLAG, 0,
      "OID <%s> has exceeded the Upper Threshold in the Stat Mgr",
      apOid->oidString);
  }
  else if (leRetVal == LowerThresholdExceeded)
  {
    logger.logMsg (WARNING_FLAG, 0,
      "OID <%s> has exceeded the Lower Threshold in the Stat Mgr",
      apOid->oidString);
  }

#endif

  apOid->prevValue = apOid->stsValue;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::updateStsMgr");
  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   start()
*
*     Desc:  start the statistics configuration
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int 
INGwSmStsHdlr::start ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::start");

  if (mbIsRunning == false)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "The statistics collection is STOPPED.");
    return BP_AIN_SM_FAIL;
  }


  int liRetValue = BP_AIN_SM_OK;

  int transportType = mpDist->getSmRepository()->getTransportType();
  INGwSmConfigQMsg  *lqueueMsg = NULL;
	Ss7SigtranSubsReq *req       = NULL;
  vector<int> lprocIdList;


  lprocIdList.push_back(INGwSmBlkConfig::getInstance().m_selfProcId);
  /*
  * While sending the request to the stack, the operation can fail
  * and hence we need to move to the next state and ignore any failure
  */

  do
  {
    logger.logMsg (TRACE_FLAG, 0,
           "INGwSmStsHdlr::start(),Inside do-while loop <%x>",mlBitMapCopy);

    if ((mlBitMapCopy & STS_MASK_ST_ALL) > 0)
    {
      req = new Ss7SigtranSubsReq;
      Stat layerStat;
      layerStat.layer = BP_AIN_SM_TCA_LAYER;

		  memcpy(&(req->u.stat), &layerStat, sizeof(Stat));
		  req->cmd_type  = GET_STATS;
      lqueueMsg      =  new INGwSmConfigQMsg;
      lqueueMsg->req = req;
      lqueueMsg->src = BP_AIN_SM_SRC_CCM;
      lqueueMsg->procIdList = lprocIdList;
      lqueueMsg->from       = 0;
      mpDist->mpSmWrapper->postMsg(lqueueMsg,true);
    }
    else if ((mlBitMapCopy & STS_MASK_SP_ALL) > 0)
    {
      req = new Ss7SigtranSubsReq;
      Stat layerStat;
      layerStat.layer = BP_AIN_SM_SCC_LAYER;

		  memcpy(&(req->u.stat), &layerStat, sizeof(Stat));

		  req->cmd_type  = GET_STATS;
      lqueueMsg      =  new INGwSmConfigQMsg;
      lqueueMsg->req = req;
      lqueueMsg->src = BP_AIN_SM_SRC_CCM;
      lqueueMsg->procIdList = lprocIdList;
      lqueueMsg->from       = 0;
      mpDist->mpSmWrapper->postMsg(lqueueMsg,true);
    }
    else if ((mlBitMapCopy & STS_MASK_SN_ALL) > 0)
    {
      if((TRANSPORT_TYPE_MTP == transportType) || 
         (TRANSPORT_TYPE_BOTH == transportType)) {

        req = new Ss7SigtranSubsReq;
        Stat layerStat;
        layerStat.layer = BP_AIN_SM_MTP3_LAYER;

		    memcpy(&(req->u.stat), &layerStat, sizeof(Stat));

		    req->cmd_type  = GET_STATS;
        lqueueMsg      =  new INGwSmConfigQMsg;
        lqueueMsg->req = req;
        lqueueMsg->src = BP_AIN_SM_SRC_CCM;
        lqueueMsg->procIdList = lprocIdList;
        lqueueMsg->from       = 0;
        mpDist->mpSmWrapper->postMsg(lqueueMsg,true);
      }
    }
    else if ((mlBitMapCopy & STS_MASK_IT_ALL) > 0)
    {
      if((TRANSPORT_TYPE_SIGTRAN == transportType) || 
          (TRANSPORT_TYPE_BOTH == transportType)) {

        req = new Ss7SigtranSubsReq;
        Stat layerStat;
        layerStat.layer = BP_AIN_SM_M3U_LAYER;

		    memcpy(&(req->u.stat), &layerStat, sizeof(Stat));

		    req->cmd_type  = GET_STATS;
        lqueueMsg      =  new INGwSmConfigQMsg;
        lqueueMsg->req = req;
        lqueueMsg->src = BP_AIN_SM_SRC_CCM;
        lqueueMsg->procIdList = lprocIdList;
        lqueueMsg->from       = 0;
        mpDist->mpSmWrapper->postMsg(lqueueMsg,true);
      }
    }
    else if ((mlBitMapCopy & STS_MASK_SB_ALL) > 0)
    {
      if((TRANSPORT_TYPE_SIGTRAN == transportType) || 
         (TRANSPORT_TYPE_BOTH == transportType)) {

          req = new Ss7SigtranSubsReq;
          Stat layerStat;
          layerStat.layer = BP_AIN_SM_SCT_LAYER;

			    memcpy(&(req->u.stat), &layerStat, sizeof(Stat));

			    req->cmd_type  = GET_STATS;
          lqueueMsg      =  new INGwSmConfigQMsg;
          lqueueMsg->req = req;
          lqueueMsg->src = BP_AIN_SM_SRC_CCM;
          lqueueMsg->procIdList = lprocIdList;
          lqueueMsg->from       = 0;
          mpDist->mpSmWrapper->postMsg(lqueueMsg,true);
      }
    }
//[new statistics start 
/*
STS_MASK_DN_ALL
STS_MASK_DV_ALL, 
STS_MASK_MR_ALL, 

STS_MASK_DV_GEN
STS_MASK_DN_GEN
STS_MASK_MR_GEN
*/

//LDF-MTP3
      else if((mlBitMapCopy & STS_MASK_DN_ALL) > 0)
      {
        liRetValue = ldfMtp3GetStatistics (mlBitMapCopy);
      }
  //LDF-M3UA 
      else if((mlBitMapCopy & STS_MASK_DV_ALL) > 0)
      {
        liRetValue = ldfM3uaGetStatistics (mlBitMapCopy);
      }
//MR
      else if((mlBitMapCopy & STS_MASK_MR_ALL) > 0)
      {
        liRetValue = mrGetStatistics (mlBitMapCopy);
      }

//new statistics end] 
      else
      {
        logger.logMsg (VERBOSE_FLAG, 0,
          "Statistics Handler is waiting for timeout to occur");
      }
    } while (liRetValue != BP_AIN_SM_OK);


  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::start");
  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   handleResponse()
*
*     Desc:  invoked by INGwSmRequest to handle the response from the stack
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int 
INGwSmStsHdlr::handleResponse (INGwSmQueueMsg *apQueueMsg)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::handleResponse");


  /*
   * In case of timeout, we need to restart gathering the statistics again
   * hence we need to refresh our copy of operation BitMap
   */

  int liTransId = apQueueMsg->t.stackMsg.stkMsg.miTransId;

  if (apQueueMsg->t.stackMsg.stkMsgTyp == BP_AIN_SM_STKOP_TIMEOUT)
  {
    mlBitMapCopy = mlBitMap;

    logger.logMsg (VERBOSE_FLAG, 0,
      "Statistics Timeout occurred. Operation Mask <%X>", mlBitMap);
  }
  else if (apQueueMsg->t.stackMsg.stkMsgTyp == BP_AIN_SM_STKOP_STSCFM)
  {

    switch (apQueueMsg->t.stackMsg.stkMsg.miLayerId)
    {
#if 0
      case BP_AIN_SM_AIN_LAYER:
      {
        handleIeRsp (apQueueMsg->t.stackMsg.stkMsg.lyr.ie);
        break;
      }
#endif
      case BP_AIN_SM_TCA_LAYER:
      {
        handleStRsp (apQueueMsg->t.stackMsg.stkMsg.lyr.st);
        break;
      }
      case BP_AIN_SM_SCC_LAYER:
      {
        handleSpRsp (apQueueMsg->t.stackMsg.stkMsg.lyr.sp);
        break;
      }
      case BP_AIN_SM_MTP3_LAYER:
      {
        handleSnRsp (apQueueMsg->t.stackMsg.stkMsg.lyr.sn);
        break;
      }
      case BP_AIN_SM_M3U_LAYER:
      {
        handleItRsp (apQueueMsg->t.stackMsg.stkMsg.lyr.it);
        break;
      }
      case BP_AIN_SM_SCT_LAYER:
      {
        handleSbRsp (apQueueMsg->t.stackMsg.stkMsg.lyr.sb);
        break;
      }
      case BP_AIN_SM_TUC_LAYER:
      {
        handleHiRsp (apQueueMsg->t.stackMsg.stkMsg.lyr.hi);
        break;
      }

      case BP_AIN_SM_LDF_M3UA_LAYER:
      {
        handleDvRsp (apQueueMsg->t.stackMsg.stkMsg.lyr.dv);  
        break;
      } 

      case BP_AIN_SM_LDF_MTP3_LAYER:
      {
        handleDnRsp (apQueueMsg->t.stackMsg.stkMsg.lyr.dn);  
        break;
       
      } 

      case BP_AIN_SM_MR_LAYER:
      {
        handleMrRsp (apQueueMsg->t.stackMsg.stkMsg.lyr.mr);  
        break;
      } 

      default:
      {
        logger.logMsg (ERROR_FLAG, 0,
          "UNKNOWN Layer passed in work unit");
        break;
      }
    }

    INGwSmRequestTable *reqTable = mpDist->getRequestTable();
    INGwSmRequest *lpReq =
                    reqTable->getRequest (liTransId);

    reqTable->removeRequest (liTransId, lpReq);

    if (lpReq){
      delete lpReq;
      lpReq = NULL;
    }
    delete apQueueMsg;
  }
  else
  {
    delete apQueueMsg;
    return BP_AIN_SM_FAIL;
  }

  //check the bit map and send the request
  if (start () != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to send the next statistics request");
    return BP_AIN_SM_FAIL;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::handleResponse");
  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   handleIeRsp()
*
*     Desc:  handle INAP Response
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
#if 0
int
INGwSmStsHdlr::handleIeRsp (IeMngmt &aeMgmt)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::handleIeRsp");

  //get the current stats level from the repository
  int liLevel = mpDist->getSmRepository()->getStsLevel();

  //set the layer id
  int liLayer = BP_AIN_SM_AIN_LAYER;

  //find the operation type
  int liOper;

  if (aeMgmt.hdr.elmId.elmnt == STINSAP)
  {
    liOper = BP_AIN_SM_SUBTYPE_GENSTS;
  }

  long liHashKey = mpStsMap->getStsHashKey (liLayer, liOper, liLevel);

  logger.logMsg (VERBOSE_FLAG, 0,
    "The HashKey for STS MAP =<%d, %d, %d, %d>",
    liHashKey, liLayer, liOper, liLevel);

  if (aeMgmt.cfm.status == LCM_PRIM_NOK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to retrieve statistics <%d>", liHashKey);
    return BP_AIN_SM_FAIL;
  }

  int liRetVal = 0;
  INGwSmStsOidList* lpOidList = mpStsMap->getList (liHashKey, liRetVal);

  if (liRetVal != BP_AIN_SM_OK || lpOidList->empty())
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to retrieve statistics list from Map<%d>", liHashKey);
    return BP_AIN_SM_FAIL;
  }

  INGwSmStsOidList::iterator leOidIter;

  for (leOidIter = lpOidList->begin();
       leOidIter != lpOidList->end();
       leOidIter++)
  {   
    INGwSmStsOid *lpOid = *(leOidIter);

    if (lpOid == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "Oid retrieved from list is NULL");
      continue;
    }

    //this bool will be used to check if element has been mapped or not
    bool lbFoundOid = false;

    //check the element in oid and retrieve from the stack structure
    switch (lpOid->element)
    {
      case 28:
      {
        long llCount = 0;
        for (int count = 0; count < LIE_MAX_OPR; count++)
        {
          if (aeMgmt.t.sts.ieSap.oprSts[count].pres == true)
          {
            llCount += aeMgmt.t.sts.ieSap.oprSts[count].invTx;
          }
        }
        lpOid->stsValue = llCount;
        lbFoundOid = true;
        break;
      }
      case 29:
      {
        long llCount = 0;
        for (int count = 0; count < LIE_MAX_OPR; count++)
        {
          if (aeMgmt.t.sts.ieSap.oprSts[count].pres == true)
          {
            llCount += aeMgmt.t.sts.ieSap.oprSts[count].invRx;
          }
        }
        lpOid->stsValue = llCount;
        lbFoundOid = true;
        break;
      }
      case 30:
      {
        long llCount = 0;
        for (int count = 0; count < LIE_MAX_OPR; count++)
        {
          if (aeMgmt.t.sts.ieSap.oprSts[count].pres == true)
          {
            llCount += aeMgmt.t.sts.ieSap.oprSts[count].rrTx;
          }
        }
        lpOid->stsValue = llCount;
        lbFoundOid = true;
        break;
      }
      case 31:
      {
        long llCount = 0;
        for (int count = 0; count < LIE_MAX_OPR; count++)
        {
          if (aeMgmt.t.sts.ieSap.oprSts[count].pres == true)
          {
            llCount += aeMgmt.t.sts.ieSap.oprSts[count].rrRx;
          }
        }
        lpOid->stsValue = llCount;
        lbFoundOid = true;
        break;
      }
      case 32:
      {
        long llCount = 0;
        for (int count = 0; count < LIE_MAX_OPR; count++)
        {
          if (aeMgmt.t.sts.ieSap.oprSts[count].pres == true)
          {
            llCount += aeMgmt.t.sts.ieSap.oprSts[count].reTx;
          }
        }
        lpOid->stsValue = llCount;
        lbFoundOid = true;
        break;
      }
      case 33:
      {
        long llCount = 0;
        for (int count = 0; count < LIE_MAX_OPR; count++)
        {
          if (aeMgmt.t.sts.ieSap.oprSts[count].pres == true)
          {
            llCount += aeMgmt.t.sts.ieSap.oprSts[count].reRx;
          }
        }
        lpOid->stsValue = llCount;
        lbFoundOid = true;
        break;
      }
      case 34:
      {
        long llCount = 0;
        for (int count = 0; count < LIE_MAX_OPR; count++)
        {
          if (aeMgmt.t.sts.ieSap.oprSts[count].pres == true)
          {
            llCount += aeMgmt.t.sts.ieSap.oprSts[count].rejTx;
          }
        }
        lpOid->stsValue = llCount;
        lbFoundOid = true;
        break;
      }
      case 35:
      {
        long llCount = 0;
        for (int count = 0; count < LIE_MAX_OPR; count++)
        {
          if (aeMgmt.t.sts.ieSap.oprSts[count].pres == true)
          {
            llCount += aeMgmt.t.sts.ieSap.oprSts[count].rejRx;
          }
        }
        lpOid->stsValue = llCount;
        lbFoundOid = true;
        break;
      }
      case 78:
      {
        long llCount = 0;
        for (int count = 0; count < LIE_MAX_OPR; count++)
        {
          if (aeMgmt.t.sts.ieSap.oprSts[count].pres == true)
          {
            llCount += aeMgmt.t.sts.ieSap.oprSts[count].cnclTx;
          }
        }
        lpOid->stsValue = llCount;
        lbFoundOid = true;
        break;
      }
      case 79:
      {
        long llCount = 0;
        for (int count = 0; count < LIE_MAX_OPR; count++)
        {
          if (aeMgmt.t.sts.ieSap.oprSts[count].pres == true)
          {
            llCount += aeMgmt.t.sts.ieSap.oprSts[count].cnclRx;
          }
        }
        lpOid->stsValue = llCount;
        lbFoundOid = true;
        break;
      }
      case 80:
      {
        long llCount = 0;
        for (int count = 0; count < LIE_MAX_OPR; count++)
        {
          if (aeMgmt.t.sts.ieSap.oprSts[count].pres == true)
          {
            llCount += aeMgmt.t.sts.ieSap.oprSts[count].cnclFailTx;
          }
        }
        lpOid->stsValue = llCount;
        lbFoundOid = true;
        break;
      }
      case 81:
      {
        long llCount = 0;
        for (int count = 0; count < LIE_MAX_OPR; count++)
        {
          if (aeMgmt.t.sts.ieSap.oprSts[count].pres == true)
          {
            llCount += aeMgmt.t.sts.ieSap.oprSts[count].cnclFailRx;
          }
        }
        lpOid->stsValue = llCount;
        lbFoundOid = true;
        break;
      }
      case 82:
      {
        long llCount = 0;
        for (int count = 0; count < LIE_MAX_OPR; count++)
        {
          if (aeMgmt.t.sts.ieSap.oprSts[count].pres == true)
          {
            llCount += aeMgmt.t.sts.ieSap.oprSts[count].dbErrTx;
          }
        }
        lpOid->stsValue = llCount;
        lbFoundOid = true;
        break;
      }
      case 83:
      {
        long llCount = 0;
        for (int count = 0; count < LIE_MAX_OPR; count++)
        {
          if (aeMgmt.t.sts.ieSap.oprSts[count].pres == true)
          {
            llCount += aeMgmt.t.sts.ieSap.oprSts[count].dbErrRx;
          }
        }
        lpOid->stsValue = llCount;
        lbFoundOid = true;
        break;
      }
      case 84:
      {
        long llCount = 0;
        for (int count = 0; count < LIE_MAX_OPR; count++)
        {
          if (aeMgmt.t.sts.ieSap.oprSts[count].pres == true)
          {
            llCount += aeMgmt.t.sts.ieSap.oprSts[count].etcFailTx;
          }
        }
        lpOid->stsValue = llCount;
        lbFoundOid = true;
        break;
      }
      case 85:
      {
        long llCount = 0;
        for (int count = 0; count < LIE_MAX_OPR; count++)
        {
          if (aeMgmt.t.sts.ieSap.oprSts[count].pres == true)
          {
            llCount += aeMgmt.t.sts.ieSap.oprSts[count].etcFailRx;
          }
        }
        lpOid->stsValue = llCount;
        lbFoundOid = true;
        break;
      }
      case 86:
      {
        long llCount = 0;
        for (int count = 0; count < LIE_MAX_OPR; count++)
        {
          if (aeMgmt.t.sts.ieSap.oprSts[count].pres == true)
          {
            llCount += aeMgmt.t.sts.ieSap.oprSts[count].impCallRspTx;
          }
        }
        lpOid->stsValue = llCount;
        lbFoundOid = true;
        break;
      }
      case 87:
      {
        long llCount = 0;
        for (int count = 0; count < LIE_MAX_OPR; count++)
        {
          if (aeMgmt.t.sts.ieSap.oprSts[count].pres == true)
          {
            llCount += aeMgmt.t.sts.ieSap.oprSts[count].impCallRspRx;
          }
        }
        lpOid->stsValue = llCount;
        lbFoundOid = true;
        break;
      }
      case 88:
      {
        long llCount = 0;
        for (int count = 0; count < LIE_MAX_OPR; count++)
        {
          if (aeMgmt.t.sts.ieSap.oprSts[count].pres == true)
          {
            llCount += aeMgmt.t.sts.ieSap.oprSts[count].infoKeyErrTx;
          }
        }
        lpOid->stsValue = llCount;
        lbFoundOid = true;
        break;
      }
      case 89:
      {
        long llCount = 0;
        for (int count = 0; count < LIE_MAX_OPR; count++)
        {
          if (aeMgmt.t.sts.ieSap.oprSts[count].pres == true)
          {
            llCount += aeMgmt.t.sts.ieSap.oprSts[count].infoKeyErrRx;
          }
        }
        lpOid->stsValue = llCount;
        lbFoundOid = true;
        break;
      }
      case 90:
      {
        long llCount = 0;
        for (int count = 0; count < LIE_MAX_OPR; count++)
        {
          if (aeMgmt.t.sts.ieSap.oprSts[count].pres == true)
          {
            llCount += aeMgmt.t.sts.ieSap.oprSts[count].missCustRecTx;
          }
        }
        lpOid->stsValue = llCount;
        lbFoundOid = true;
        break;
      }
      case 91:
      {
        long llCount = 0;
        for (int count = 0; count < LIE_MAX_OPR; count++)
        {
          if (aeMgmt.t.sts.ieSap.oprSts[count].pres == true)
          {
            llCount += aeMgmt.t.sts.ieSap.oprSts[count].missCustRecRx;
          }
        }
        lpOid->stsValue = llCount;
        lbFoundOid = true;
        break;
      }
      case 92:
      {
        long llCount = 0;
        for (int count = 0; count < LIE_MAX_OPR; count++)
        {
          if (aeMgmt.t.sts.ieSap.oprSts[count].pres == true)
          {
            llCount += aeMgmt.t.sts.ieSap.oprSts[count].missParamTx;
          }
        }
        lpOid->stsValue = llCount;
        lbFoundOid = true;
        break;
      }
      case 93:
      {
        long llCount = 0;
        for (int count = 0; count < LIE_MAX_OPR; count++)
        {
          if (aeMgmt.t.sts.ieSap.oprSts[count].pres == true)
          {
            llCount += aeMgmt.t.sts.ieSap.oprSts[count].missParamRx;
          }
        }
        lpOid->stsValue = llCount;
        lbFoundOid = true;
        break;
      }
      case 94:
      {
        long llCount = 0;
        for (int count = 0; count < LIE_MAX_OPR; count++)
        {
          if (aeMgmt.t.sts.ieSap.oprSts[count].pres == true)
          {
            llCount += aeMgmt.t.sts.ieSap.oprSts[count].paramOutRangeTx;
          }
        }
        lpOid->stsValue = llCount;
        lbFoundOid = true;
        break;
      }
      case 95:
      {
        long llCount = 0;
        for (int count = 0; count < LIE_MAX_OPR; count++)
        {
          if (aeMgmt.t.sts.ieSap.oprSts[count].pres == true)
          {
            llCount += aeMgmt.t.sts.ieSap.oprSts[count].paramOutRangeRx;
          }
        }
        lpOid->stsValue = llCount;
        lbFoundOid = true;
        break;
      }
      default:
      {
    		lbFoundOid = false;
        logger.logMsg (ERROR_FLAG, 0, "Unknown Element type <%d> read", 
				lpOid->element);
        break;
      }
    }

    //update the Statistics Manager for the OID value
    if (lbFoundOid == true)
      updateStsMgr (lpOid);
    else
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "OID <%s> has not been mapped", lpOid->oidString);
    }
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::handleIeRsp");
  return BP_AIN_SM_OK;
}
#endif

/******************************************************************************
*
*     Fun:   handleStRsp()
*
*     Desc:  handle TCAP Response
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int
INGwSmStsHdlr::handleStRsp (StMngmt &aeMgmt)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::handleStRsp");


  //get the current stats level from the repository
  int liLevel = mpDist->getSmRepository()->getStsLevel();

  //set the layer id
  int liLayer = BP_AIN_SM_TCA_LAYER;

  //find the operation type
  int liOper;

  if (aeMgmt.hdr.elmId.elmnt == STTCUSAP)
  {
    liOper = BP_AIN_SM_SUBTYPE_GENSTS;
  }

  long liHashKey = mpStsMap->getStsHashKey (liLayer, liOper, liLevel);

  if (aeMgmt.cfm.status == LCM_PRIM_NOK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to retrieve statistics <%d>", liHashKey);
    return BP_AIN_SM_FAIL;
  }

  int liRetVal = 0;
  INGwSmStsOidList *lpOidList = mpStsMap->getList (liHashKey, liRetVal);

  if (liRetVal != BP_AIN_SM_OK || lpOidList->empty())
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to retrieve statistics list from Map<%d>", liHashKey);
    return BP_AIN_SM_FAIL;
  }

  INGwSmStsOidList::iterator leOidIter;

  for (leOidIter = lpOidList->begin();
       leOidIter != lpOidList->end();
       leOidIter++)
  {   
    INGwSmStsOid *lpOid = *(leOidIter);

    if (lpOid == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "Oid retrieved from list is NULL");
      continue;
    }

    //this bool will be used to check if element has been mapped or not
    bool lbFoundOid = false;

    //check the element in oid and retrieve from the stack structure
    switch (lpOid->element)
    {
      case 1:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.msgTx;
 				MsrMgr::getInstance()->setValue("Total Message Tx", "INGw", 
								 "Total Message Tx", aeMgmt.t.sts.sapSts.msgTx);
        logger.logMsg (TRACE_FLAG, 0, "MSR COUNTERS: Total Message Tx:<%d>", aeMgmt.t.sts.sapSts.msgTx);
        lbFoundOid = true;
        break;
      }
      case 2:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.uniTx;
 				MsrMgr::getInstance()->setValue("Total Unidirectional Tx", "INGw", 
												 "Total Unidirectional Tx", aeMgmt.t.sts.sapSts.uniTx);
        logger.logMsg (TRACE_FLAG, 0, "MSR COUNTERS: Total Unidirectional Tx:<%d>", aeMgmt.t.sts.sapSts.uniTx);
        lbFoundOid = true;
        break;
      }
      case 3:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.abtTx;
 				MsrMgr::getInstance()->setValue("Total Abort Tx", "INGw", 
												 "Total Abort Tx", aeMgmt.t.sts.sapSts.abtTx);
        logger.logMsg (TRACE_FLAG, 0, "MSR COUNTERS: Total Abort Tx:<%d>", aeMgmt.t.sts.sapSts.abtTx);
        lbFoundOid = true;
        break;
      }
      case 7:
      {
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
        lpOid->stsValue = aeMgmt.t.sts.sapSts.qwpTx;
 				MsrMgr::getInstance()->setValue("Total QWP Tx", "INGw", 
								 "Total QWP Tx", aeMgmt.t.sts.sapSts.qwpTx);
        lbFoundOid = true;
#endif
        break;
      }
      case 8:
      {
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
        lpOid->stsValue = aeMgmt.t.sts.sapSts.qnpTx;
 				MsrMgr::getInstance()->setValue("Total QWoP Tx", "INGw", 
												 "Total QWoP Tx", aeMgmt.t.sts.sapSts.qnpTx);
        lbFoundOid = true;
#endif
        break;
      }
      case 9:
      {
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
        lpOid->stsValue = aeMgmt.t.sts.sapSts.cwpTx;
 				MsrMgr::getInstance()->setValue("Total CWP Tx", "INGw", 
								"Total CWP Tx",  aeMgmt.t.sts.sapSts.cwpTx);
        lbFoundOid = true;
#endif
        break;
      }
      case 10:
      {
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
        lpOid->stsValue = aeMgmt.t.sts.sapSts.cnpTx;
 				MsrMgr::getInstance()->setValue("Total CWoP Tx", "INGw", 
												 "Total CWoP Tx", aeMgmt.t.sts.sapSts.cnpTx);
        lbFoundOid = true;
#endif
        break;
      }
      case 11:
      {
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
        lpOid->stsValue = aeMgmt.t.sts.sapSts.rspTx;
 				MsrMgr::getInstance()->setValue("Total Response Tx", "INGw", 
								 "Total Response Tx", aeMgmt.t.sts.sapSts.rspTx);
        lbFoundOid = true;
#endif
        break;
      }
      case 12:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.msgRx;
 				MsrMgr::getInstance()->setValue("Total Message Rx", "INGw", 
								 "Total Message Rx", aeMgmt.t.sts.sapSts.msgRx);
        lbFoundOid = true;
        break;
      }
      case 13:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.uniRx;
 				MsrMgr::getInstance()->setValue("Total Unidirectional Rx", "INGw", 
								 "Total Unidirectional Rx", aeMgmt.t.sts.sapSts.uniRx);
        logger.logMsg (TRACE_FLAG, 0, "MSR COUNTERS: Total Unidirectional Rx:<%d>", aeMgmt.t.sts.sapSts.uniRx);
        lbFoundOid = true;
        break;
      }
      case 14:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.abtRx;
 				MsrMgr::getInstance()->setValue("Total Abort Rx", "INGw", 
								 "Total Abort Rx", aeMgmt.t.sts.sapSts.abtRx);
        logger.logMsg (TRACE_FLAG, 0, "MSR COUNTERS: Total Abort Rx:<%d>", aeMgmt.t.sts.sapSts.abtRx);
        lbFoundOid = true;
        break;
      }
      case 18:
      {
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
        lpOid->stsValue = aeMgmt.t.sts.sapSts.qwpRx;
 				MsrMgr::getInstance()->setValue("Total QWP Rx", "INGw", 
								 "Total QWP Rx", aeMgmt.t.sts.sapSts.qwpRx);
        lbFoundOid = true;
#endif
        break;
      }
      case 19:
      {
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
        lpOid->stsValue = aeMgmt.t.sts.sapSts.qnpRx;
 				MsrMgr::getInstance()->setValue("Total QWoP Rx", "INGw", 
								 "Total QWoP Rx", aeMgmt.t.sts.sapSts.qnpRx);
        lbFoundOid = true;
#endif
        break;
      }
      case 20:
      {
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
        lpOid->stsValue = aeMgmt.t.sts.sapSts.cwpRx;
 				MsrMgr::getInstance()->setValue("Total CWP Rx", "INGw", 
								 "Total CWP Rx", aeMgmt.t.sts.sapSts.cwpRx);
        lbFoundOid = true;
#endif
        break;
      }
      case 21:
      {
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
        lpOid->stsValue = aeMgmt.t.sts.sapSts.cnpRx;
 				MsrMgr::getInstance()->setValue("Total CWoP Rx", "INGw", 
								 "Total CWoP Rx", aeMgmt.t.sts.sapSts.cnpRx);
        lbFoundOid = true;
#endif
        break;
      }
      case 22:
      {
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
        lpOid->stsValue = aeMgmt.t.sts.sapSts.rspRx;
 				MsrMgr::getInstance()->setValue("Total Response Rx", "INGw", 
								 "Total Response Rx", aeMgmt.t.sts.sapSts.rspRx);
        lbFoundOid = true;
#endif
        break;
      }
      case 23:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.bgnTx;
 				MsrMgr::getInstance()->setValue("Total Begin Tx", "INGw", 
								 "Total Begin Tx", aeMgmt.t.sts.sapSts.bgnTx);
        lbFoundOid = true;
        break;
      }
      case 24:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.cntTx;
 				MsrMgr::getInstance()->setValue("Total Continue Tx", "INGw", 
								 "Total Continue Tx", aeMgmt.t.sts.sapSts.cntTx);
        logger.logMsg (TRACE_FLAG, 0, "MSR COUNTERS: Total Continue Tx:<%d>", aeMgmt.t.sts.sapSts.cntTx);
        lbFoundOid = true;
        break;
      }
      case 25:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.endTx;
 				MsrMgr::getInstance()->setValue("Total End Tx", "INGw", 
								 "Total End Tx", aeMgmt.t.sts.sapSts.endTx);
        logger.logMsg (TRACE_FLAG, 0, "MSR COUNTERS: Total End Tx:<%d>", aeMgmt.t.sts.sapSts.endTx);
        lbFoundOid = true;
        break;
      }
      case 26:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.bgnRx;
 				MsrMgr::getInstance()->setValue("Total Begin Rx", "INGw", 
								 "Total Begin Rx", aeMgmt.t.sts.sapSts.bgnRx);
        logger.logMsg (TRACE_FLAG, 0, "MSR COUNTERS: Total Begin Rx:<%d>", aeMgmt.t.sts.sapSts.bgnRx);
        lbFoundOid = true;
        break;
      }
      case 27:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.cntRx;
 				MsrMgr::getInstance()->setValue("Total Continue Rx", "INGw", 
								 "Total Continue Rx", aeMgmt.t.sts.sapSts.cntRx);
        logger.logMsg (TRACE_FLAG, 0, "MSR COUNTERS: Total Continue Rx:<%d>", aeMgmt.t.sts.sapSts.cntRx);
        lbFoundOid = true;
        break;
      }
      case 28:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.endRx;
 				MsrMgr::getInstance()->setValue("Total End Rx", "INGw", 
								 "Total End Rx", aeMgmt.t.sts.sapSts.endRx);
        lbFoundOid = true;
        break;
      }
      case 29:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.cmpTx;
 				MsrMgr::getInstance()->setValue("Total Component Tx", "INGw", 
								 "Total Component Tx", aeMgmt.t.sts.sapSts.cmpTx);
        logger.logMsg (TRACE_FLAG, 0, "MSR COUNTERS: Total Component Tx:<%d>", aeMgmt.t.sts.sapSts.cmpTx);
        lbFoundOid = true;
        break;
      }
      case 30:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.cmpRx;
 				MsrMgr::getInstance()->setValue("Total Component Rx", "INGw", 
								 "Total Component Rx", aeMgmt.t.sts.sapSts.cmpRx);
        logger.logMsg (TRACE_FLAG, 0, "MSR COUNTERS: Total Component Rx:<%d>", aeMgmt.t.sts.sapSts.cmpRx);
        lbFoundOid = true;
        break;
      }
      case 31:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.invTx;
        lbFoundOid = true;
        break;
      }
      case 32:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.invRx;
        lbFoundOid = true;
        break;
      }
      case 33:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.resTx;
 				MsrMgr::getInstance()->setValue("Total ReturnResult Tx", "INGw", 
								 "Total ReturnResult Tx", aeMgmt.t.sts.sapSts.resTx);
        lbFoundOid = true;
        break;
      }
      case 34:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.resRx;
 				MsrMgr::getInstance()->setValue("Total ReturnResult Rx", "INGw", 
												 "Total ReturnResult Rx", aeMgmt.t.sts.sapSts.resRx);
        lbFoundOid = true;
        break;
      }
      case 35:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.errTx;
 				MsrMgr::getInstance()->setValue("Total ReturnError Tx", "INGw", 
								 "Total ReturnError Tx", aeMgmt.t.sts.sapSts.errTx);
        lbFoundOid = true;
        break;
      }
      case 36:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.errRx;
 				MsrMgr::getInstance()->setValue("Total ReturnError Rx", "INGw", 
								 "Total ReturnError Rx", aeMgmt.t.sts.sapSts.errRx);
        lbFoundOid = true;
        break;
      }
      case 37:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.rejTx;
 				MsrMgr::getInstance()->setValue("Total Reject Tx", "INGw", 
								 "Total Reject Tx", aeMgmt.t.sts.sapSts.rejTx);
        lbFoundOid = true;
        break;
      }
      case 38:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.rejRx;
 				MsrMgr::getInstance()->setValue("Total Reject Rx", "INGw", 
								 "Total Reject Rx", aeMgmt.t.sts.sapSts.rejRx);
        lbFoundOid = true;
        break;
      }
      case 39:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.actTrns;
 				MsrMgr::getInstance()->setValue("Total Active Transaction", "INGw", 
								 "Total Active Transaction", aeMgmt.t.sts.sapSts.actTrns);
        logger.logMsg (TRACE_FLAG, 0, "MSR COUNTERS: Total Active Transaction:<%d>", aeMgmt.t.sts.sapSts.actTrns);
        lbFoundOid = true;
        break;
      }
      case 40:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.actInv;
        lbFoundOid = true;
        break;
      }
      case 41:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.trnsId;
        lbFoundOid = true;
        break;
      }
      case 42:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.drop;
        lbFoundOid = true;
        break;
      }
      case 43:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.urMsgRx.cnt;
        lbFoundOid = true;
        break;
      }
      case 44:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.inTrnRx.cnt;
        lbFoundOid = true;
        break;
      }
      case 45:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.bdTrnRx.cnt;
        lbFoundOid = true;
        break;
      }
      case 46:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.urTidRx.cnt;
        lbFoundOid = true;
        break;
      }
      case 47:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.rsrcLRx.cnt;
        lbFoundOid = true;
        break;
      }
      case 48:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.urCmpRx.cnt;
        lbFoundOid = true;
        break;
      }
      case 49:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.inCmpRx.cnt;
        lbFoundOid = true;
        break;
      }
      case 50:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.bdCmpRx.cnt;
        lbFoundOid = true;
        break;
      }
      case 51:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.urLidRx.cnt;
        lbFoundOid = true;
        break;
      }
      case 52:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.urIidRRRx.cnt;
        lbFoundOid = true;
        break;
      }
      case 53:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.uxResRx.cnt;
        lbFoundOid = true;
        break;
      }
      case 54:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.urIidRERx.cnt;
        lbFoundOid = true;
        break;
      }
      case 55:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.uxErrRx.cnt;
        lbFoundOid = true;
        break;
      }
      case 56:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.urMsgTx.cnt;
        lbFoundOid = true;
        break;
      }
      case 57:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.inTrnTx.cnt;
        lbFoundOid = true;
        break;
      }
      case 58:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.bdTrnTx.cnt;
        lbFoundOid = true;
        break;
      }
      case 59:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.urTidTx.cnt;
        lbFoundOid = true;
        break;
      }
      case 60:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.rsrcLRx.cnt;
        lbFoundOid = true;
        break;
      }
      case 61:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.urCmpTx.cnt;
        lbFoundOid = true;
        break;
      }
      case 62:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.inCmpTx.cnt;
        lbFoundOid = true;
        break;
      }
      case 63:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.bdCmpTx.cnt;
        lbFoundOid = true;
        break;
      }
      case 64:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.urLidTx.cnt;
        lbFoundOid = true;
        break;
      }
      case 65:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.urIidRRTx.cnt;
        lbFoundOid = true;
        break;
      }
      case 66:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.uxResTx.cnt;
        lbFoundOid = true;
        break;
      }
      case 67:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.urIidRETx.cnt;
        lbFoundOid = true;
        break;
      }
      case 68:
      {
        lpOid->stsValue = aeMgmt.t.sts.sapSts.uxErrTx.cnt;
        lbFoundOid = true;
        break;
      }

      default:
      {
    		lbFoundOid = false;
        logger.logMsg (ERROR_FLAG, 0, "Unknown Element type <%d> read",
        lpOid->element);
        break;
      }
    }

    //update the Statistics Manager for the OID value
    if (lbFoundOid == true)
      updateStsMgr (lpOid);
    else
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "OID <%s> has not been mapped", lpOid->oidString);
    }
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::handleStRsp");
  return BP_AIN_SM_OK;
}

//handle SCCP Response
/******************************************************************************
*
*     Fun:   handleSpRsp()
*
*     Desc:  handle SCCP Response
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int
INGwSmStsHdlr::handleSpRsp (SpMngmt &aeMgmt)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::handleSpRsp");


  //get the current stats level from the repository
  int liLevel = mpDist->getSmRepository()->getStsLevel();

  //set the layer id
  int liLayer = BP_AIN_SM_SCC_LAYER;

  //find the operation type
  int liOper;

  if (aeMgmt.hdr.elmId.elmnt == STGEN)
  {
    liOper = BP_AIN_SM_SUBTYPE_GENSTS;
  }
  else if (aeMgmt.hdr.elmId.elmnt == STTSAP)
  {
    liOper = BP_AIN_SM_SUBTYPE_USPSTS;
  }
  else if (aeMgmt.hdr.elmId.elmnt == STNSAP)
  {
    liOper = BP_AIN_SM_SUBTYPE_LSPSTS;
  }
  else if (aeMgmt.hdr.elmId.elmnt == STROUT)
  {
    liOper = BP_AIN_SM_SUBTYPE_RTESTS;
  }

  long liHashKey = mpStsMap->getStsHashKey (liLayer, liOper, liLevel);

  if (aeMgmt.cfm.status == LCM_PRIM_NOK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to retrieve statistics <%d>", liHashKey);
    return BP_AIN_SM_FAIL;
  }

  int liRetVal = 0;
  INGwSmStsOidList *lpOidList = mpStsMap->getList (liHashKey, liRetVal);

  if (liRetVal != BP_AIN_SM_OK || lpOidList->empty())
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to retrieve statistics list from Map<%d>", liHashKey);
    return BP_AIN_SM_FAIL;
  }

  INGwSmStsOidList::iterator leOidIter;

  //this bool will be used to check if element has been mapped or not
  bool lbFoundOid = false;

  for (leOidIter = lpOidList->begin();
       leOidIter != lpOidList->end();
       leOidIter++)
  {   
  	lbFoundOid = false;
    INGwSmStsOid *lpOid = *(leOidIter);

    if (lpOid == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "Oid retrieved from list is NULL");
      continue;
    }

    //check the element in oid and retrieve from the stack structure
    if (liOper == BP_AIN_SM_SUBTYPE_GENSTS)
    {
      switch (lpOid->element)
      {
        case 1:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.rfNTASN;
          lbFoundOid = true;
          break;
        }
        case 2:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.rfNTSA;
          lbFoundOid = true;
          break;
        }
        case 3:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.rfNetFail;
          lbFoundOid = true;
          break;
        }
        case 4:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.rfNetCong;
          lbFoundOid = true;
          break;
        }
        case 5:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.rfSsnFail;
          lbFoundOid = true;
          break;
        }
        case 6:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.rfSsnCong;
          lbFoundOid = true;
          break;
        }
        case 7:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.rfUnequip;
          lbFoundOid = true;
          break;
        }
        case 8:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.rfHopViolate;
          lbFoundOid = true;
          break;
        }
        case 9:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.synError;
          lbFoundOid = true;
          break;
        }
        case 10:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.rfUnknown;
          lbFoundOid = true;
          break;
        }
        case 11:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.ssCongRx;
          lbFoundOid = true;
          break;
        }
        case 12:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.ssProhRx;
          lbFoundOid = true;
          break;
        }
        case 13:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.uDataTx;
          lbFoundOid = true;
          break;
        }
        case 14:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.uDataSrvTx;
          lbFoundOid = true;
          break;
        }
        case 15:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.uDataRx;
          lbFoundOid = true;
          break;
        }
        case 16:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.uDataSrvRx;
          lbFoundOid = true;
          break;
        }
        case 17:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.xuDataTx;
          lbFoundOid = true;
          break;
        }
        case 18:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.xuDataSrvTx;
          lbFoundOid = true;
          break;
        }
        case 19:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.xuDataRx;
          lbFoundOid = true;
          break;
        }
        case 20:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.xuDataSrvRx;
          lbFoundOid = true;
          break;
        }
        case 21:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.luDataTx;
          lbFoundOid = true;
          break;
        }
        case 22:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.luDataSrvTx;
          lbFoundOid = true;
          break;
        }
        case 23:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.luDataRx;
          lbFoundOid = true;
          break;
        }
        case 24:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.luDataSrvRx;
          lbFoundOid = true;
          break;
        }
        case 25:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.segErr;
          lbFoundOid = true;
          break;
        }
        case 26:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.segErrFail;
          lbFoundOid = true;
          break;
        }
        case 27:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.reassemErr;
          lbFoundOid = true;
          break;
        }
        case 28:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.reassemErrTimExp;
          lbFoundOid = true;
          break;
        }
        case 29:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.spGlbSts.reassemErrNoSpc;
          lbFoundOid = true;
          break;
        }
        default:
        {
  				lbFoundOid = false;
          logger.logMsg (ERROR_FLAG, 0, "Unknown Element type <%d> read",
           lpOid->element);
          break;
        }
      }
    }

    //update the Statistics Manager for the OID value
    if (lbFoundOid == true)
      updateStsMgr (lpOid);
    else
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "OID <%s> has not been mapped", lpOid->oidString);
    }
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::handleSpRsp");
  return BP_AIN_SM_OK;
}

//handle M3UA Response
/******************************************************************************
*
*     Fun:   handleItRsp()
*
*     Desc:  handle M3UA Response
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int
INGwSmStsHdlr::handleItRsp (ItMgmt &aeMgmt)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::handleItRsp");


  //get the current stats level from the repository
  int liLevel = mpDist->getSmRepository()->getStsLevel();

  //set the layer id
  int liLayer = BP_AIN_SM_M3U_LAYER;

  //find the operation type
  int liOper;

  if (aeMgmt.hdr.elmId.elmnt == STITGEN)
  {
    liOper = BP_AIN_SM_SUBTYPE_GENSTS;
  }
  else if (aeMgmt.hdr.elmId.elmnt == STITNSAP)
  {
    liOper = BP_AIN_SM_SUBTYPE_USPSTS;
  }
  else if (aeMgmt.hdr.elmId.elmnt == STITSCTSAP)
  {
    liOper = BP_AIN_SM_SUBTYPE_LSPSTS;
  }
  else if (aeMgmt.hdr.elmId.elmnt == STITPSP)
  {
    liOper = BP_AIN_SM_SUBTYPE_PSPSTS;
  }

  long liHashKey = mpStsMap->getStsHashKey (liLayer, liOper, liLevel);

  if (aeMgmt.cfm.status == LCM_PRIM_NOK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to retrieve statistics <%d>", liHashKey);
    return BP_AIN_SM_FAIL;
  }

  int liRetVal = 0;
  INGwSmStsOidList *lpOidList = mpStsMap->getList (liHashKey, liRetVal);

  if (liRetVal != BP_AIN_SM_OK || lpOidList->empty())
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to retrieve statistics list from Map<%d>", liHashKey);
    return BP_AIN_SM_FAIL;
  }

  INGwSmStsOidList::iterator leOidIter;

  //this bool will be used to check if element has been mapped or not
  bool lbFoundOid = false;

  for (leOidIter = lpOidList->begin();
       leOidIter != lpOidList->end();
       leOidIter++)
  {   
		lbFoundOid = false;
    INGwSmStsOid *lpOid = *(leOidIter);

    if (lpOid == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "Oid retrieved from list is NULL");
      continue;
    }

    //check the element in oid and retrieve from the stack structure
    if (liOper == BP_AIN_SM_SUBTYPE_GENSTS)
    {
      switch (lpOid->element)
      {
        case 1:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txMtp3Sts.data;
          lbFoundOid = true;
          break;
        }
        case 2:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txMtp3Sts.pause;
          lbFoundOid = true;
          break;
        }
        case 3:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txMtp3Sts.resume;
          lbFoundOid = true;
          break;
        }
        case 4:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txMtp3Sts.cong;
          lbFoundOid = true;
          break;
        }
        case 5:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txMtp3Sts.drst;
          lbFoundOid = true;
          break;
        }
        case 6:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txMtp3Sts.rstBeg;
          lbFoundOid = true;
          break;
        }
        case 7:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txMtp3Sts.rstEnd;
          lbFoundOid = true;
          break;
        }
        case 8:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txMtp3Sts.upu;
          lbFoundOid = true;
          break;
        }
        case 9:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxMtp3Sts.data;
          lbFoundOid = true;
          break;
        }
        case 10:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxMtp3Sts.pause;
          lbFoundOid = true;
          break;
        }
        case 11:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxMtp3Sts.resume;
          lbFoundOid = true;
          break;
        }
        case 12:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxMtp3Sts.cong;
          lbFoundOid = true;
          break;
        }
        case 13:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxMtp3Sts.drst;
          lbFoundOid = true;
          break;
        }
        case 14:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxMtp3Sts.rstBeg;
          lbFoundOid = true;
          break;
        }
        case 15:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxMtp3Sts.rstEnd;
          lbFoundOid = true;
          break;
        }
        case 16:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxMtp3Sts.upu;
          lbFoundOid = true;
          break;
        }
        case 17:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txM3uaSts.data;
          lbFoundOid = true;
          break;
        }
        case 18:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txM3uaSts.duna;
          lbFoundOid = true;
          break;
        }
        case 19:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txM3uaSts.dava;
          lbFoundOid = true;
          break;
        }
        case 20:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txM3uaSts.daud;
          lbFoundOid = true;
          break;
        }
        case 21:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txM3uaSts.scon;
          lbFoundOid = true;
          break;
        }
        case 22:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txM3uaSts.dupu;
          lbFoundOid = true;
          break;
        }
        case 23:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txM3uaSts.drst;
          lbFoundOid = true;
          break;
        }
        case 24:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txM3uaSts.regReq;
          lbFoundOid = true;
          break;
        }
        case 25:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txM3uaSts.deRegReq;
          lbFoundOid = true;
          break;
        }
        case 26:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txM3uaSts.regRsp;
          lbFoundOid = true;
          break;
        }
        case 27:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txM3uaSts.deRegRsp;
          lbFoundOid = true;
          break;
        }
        case 28:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txM3uaSts.aspUp;
          lbFoundOid = true;
          break;
        }
        case 29:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txM3uaSts.aspUpAck;
          lbFoundOid = true;
          break;
        }
        case 30:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txM3uaSts.aspDn;
          lbFoundOid = true;
          break;
        }
        case 31:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txM3uaSts.aspDnAck;
          lbFoundOid = true;
          break;
        }
        case 32:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txM3uaSts.aspAc;
          lbFoundOid = true;
          break;
        }
        case 33:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txM3uaSts.aspAcAck;
          lbFoundOid = true;
          break;
        }
        case 34:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txM3uaSts.aspIa;
          lbFoundOid = true;
          break;
        }
        case 35:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txM3uaSts.aspIaAck;
          lbFoundOid = true;
          break;
        }
        case 36:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txM3uaSts.hBeat;
          lbFoundOid = true;
          break;
        }
        case 37:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txM3uaSts.hBeatAck;
          lbFoundOid = true;
          break;
        }
        case 38:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txM3uaSts.err;
          lbFoundOid = true;
          break;
        }
        case 39:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.txM3uaSts.notify;
          lbFoundOid = true;
          break;
        }
        case 40:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxM3uaSts.data;
          lbFoundOid = true;
          break;
        }
        case 41:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxM3uaSts.duna;
          lbFoundOid = true;
          break;
        }
        case 42:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxM3uaSts.dava;
          lbFoundOid = true;
          break;
        }
        case 43:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxM3uaSts.daud;
          lbFoundOid = true;
          break;
        }
        case 44:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxM3uaSts.scon;
          lbFoundOid = true;
          break;
        }
        case 45:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxM3uaSts.dupu;
          lbFoundOid = true;
          break;
        }
        case 46:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxM3uaSts.drst;
          lbFoundOid = true;
          break;
        }
        case 47:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxM3uaSts.regReq;
          lbFoundOid = true;
          break;
        }
        case 48:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxM3uaSts.deRegReq;
          lbFoundOid = true;
          break;
        }
        case 49:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxM3uaSts.regRsp;
          lbFoundOid = true;
          break;
        }
        case 50:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxM3uaSts.deRegRsp;
          lbFoundOid = true;
          break;
        }
        case 51:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxM3uaSts.aspUp;
          lbFoundOid = true;
          break;
        }
        case 52:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxM3uaSts.aspUpAck;
          lbFoundOid = true;
          break;
        }
        case 53:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxM3uaSts.aspDn;
          lbFoundOid = true;
          break;
        }
        case 54:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxM3uaSts.aspDnAck;
          lbFoundOid = true;
          break;
        }
        case 55:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxM3uaSts.aspAc;
          lbFoundOid = true;
          break;
        }
        case 56:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxM3uaSts.aspAcAck;
          lbFoundOid = true;
          break;
        }
        case 57:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxM3uaSts.aspIa;
          lbFoundOid = true;
          break;
        }
        case 58:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxM3uaSts.aspIaAck;
          lbFoundOid = true;
          break;
        }
        case 59:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxM3uaSts.hBeat;
          lbFoundOid = true;
          break;
        }
        case 60:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxM3uaSts.hBeatAck;
          lbFoundOid = true;
          break;
        }
        case 61:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxM3uaSts.err;
          lbFoundOid = true;
          break;
        }
        case 62:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.rxM3uaSts.notify;
          lbFoundOid = true;
          break;
        }
        case 63:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.upDataErrSts.dropNoRoute;
          lbFoundOid = true;
          break;
        }
        case 64:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.upDataErrSts.dropPcUnavail;
          lbFoundOid = true;
          break;
        }
        case 65:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.upDataErrSts.dropPcCong;
          lbFoundOid = true;
          break;
        }
        case 66:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.upDataErrSts.dropNoPspAvail;
          lbFoundOid = true;
          break;
        }
        case 67:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.upDataErrSts.dropNoNSapAvail;
          lbFoundOid = true;
          break;
        }
        case 68:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.upDataErrSts.dropLoadShFail;
          lbFoundOid = true;
          break;
        }
        case 69:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.upDataErrSts.dropMmhFail;
          lbFoundOid = true;
          break;
        }
        case 70:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.upDataErrSts.dataQCong;
          lbFoundOid = true;
          break;
        }
        case 71:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.upDataErrSts.dataQAsPend;
          lbFoundOid = true;
          break;
        }
        case 72:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.downDataErrSts.dropNoRoute;
          lbFoundOid = true;
          break;
        }
        case 73:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.downDataErrSts.dropPcUnavail;
          lbFoundOid = true;
          break;
        }
        case 74:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.downDataErrSts.dropPcCong;
          lbFoundOid = true;
          break;
        }
        case 75:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.downDataErrSts.dropNoPspAvail;
          lbFoundOid = true;
          break;
        }
        case 76:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.downDataErrSts.dropNoNSapAvail;
          lbFoundOid = true;
          break;
        }
        case 77:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.downDataErrSts.dropLoadShFail;
          lbFoundOid = true;
          break;
        }
        case 78:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.downDataErrSts.dropMmhFail;
          lbFoundOid = true;
          break;
        }
        case 79:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.downDataErrSts.dataQCong;
          lbFoundOid = true;
          break;
        }
        case 80:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.downDataErrSts.dataQAsPend;
          lbFoundOid = true;
          break;
        }
        default:
        {
          lbFoundOid = false;
          logger.logMsg (ERROR_FLAG, 0, "Unknown Element type <%d> read", 
           lpOid->element);
          break;
        }
      }
    }

    //update the Statistics Manager for the OID value
    if (lbFoundOid == true)
      updateStsMgr (lpOid);
    else
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "OID <%s> has not been mapped", lpOid->oidString);
    }
  }


  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::handleItRsp");
  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   handleSbRsp()
*
*     Desc:  handle SCTP Response
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int
INGwSmStsHdlr::handleSbRsp (SbMgmt &aeMgmt)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::handleSbRsp");


  //get the current stats level from the repository
  int liLevel = mpDist->getSmRepository()->getStsLevel();

  //set the layer id
  int liLayer = BP_AIN_SM_SCT_LAYER;

  //find the operation type
  int liOper;

  if (aeMgmt.hdr.elmId.elmnt == STSBGEN)
  {
    liOper = BP_AIN_SM_SUBTYPE_GENSTS;
  }
  else if (aeMgmt.hdr.elmId.elmnt == STSBSCTSAP)
  {
    liOper = BP_AIN_SM_SUBTYPE_USPSTS;
  }
  else if (aeMgmt.hdr.elmId.elmnt == STSBTSAP)
  {
    liOper = BP_AIN_SM_SUBTYPE_LSPSTS;
  }

  long liHashKey = mpStsMap->getStsHashKey (liLayer, liOper, liLevel);

  if (aeMgmt.cfm.status == LCM_PRIM_NOK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to retrieve statistics <%d>", liHashKey);
    return BP_AIN_SM_FAIL;
  }

  int liRetVal = 0;
  INGwSmStsOidList *lpOidList = mpStsMap->getList (liHashKey, liRetVal);

  if (liRetVal != BP_AIN_SM_OK || lpOidList->empty())
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to retrieve statistics list from Map<%d>", liHashKey);
    return BP_AIN_SM_FAIL;
  }

  INGwSmStsOidList::iterator leOidIter;

  //this bool will be used to check if element has been mapped or not
  bool lbFoundOid = false;

  for (leOidIter = lpOidList->begin();
       leOidIter != lpOidList->end();
       leOidIter++)
  {   
  	lbFoundOid = false;
    INGwSmStsOid *lpOid = *(leOidIter);

    if (lpOid == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "Oid retrieved from list is NULL");
      continue;
    }

    //check the element in oid and retrieve from the stack structure
    if (liOper == BP_AIN_SM_SUBTYPE_GENSTS)
    {
      switch (lpOid->element)
      {
        case 1:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbChunkSts.noInitTx;
          lbFoundOid = true;
          break;
        }
        case 2:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbChunkSts.noInitReTx;
          lbFoundOid = true;
          break;
        }
        case 3:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbChunkSts.noInitRx;
          lbFoundOid = true;
          break;
        }
        case 4:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbChunkSts.noIAckTx;
          lbFoundOid = true;
          break;
        }
        case 5:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbChunkSts.noIAckRx;
          lbFoundOid = true;
          break;
        }
        case 6:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbChunkSts.noShDwnTx;
          lbFoundOid = true;
          break;
        }
        case 7:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbChunkSts.noShDwnReTx;
          lbFoundOid = true;
          break;
        }
        case 8:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbChunkSts.noShDwnRx;
          lbFoundOid = true;
          break;
        }
        case 9:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbChunkSts.noShDwnAckTx;
          lbFoundOid = true;
          break;
        }
        case 10:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbChunkSts.noShDwnAckReTx;
          lbFoundOid = true;
          break;
        }
        case 11:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbChunkSts.noShDwnAckRx;
          lbFoundOid = true;
          break;
        }
        case 12:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbChunkSts.noCookieTx;
          lbFoundOid = true;
          break;
        }
        case 13:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbChunkSts.noCookieReTx;
          lbFoundOid = true;
          break;
        }
        case 14:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbChunkSts.noCookieRx;
          lbFoundOid = true;
          break;
        }
        case 15:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbChunkSts.noCkAckTx;
          lbFoundOid = true;
          break;
        }
        case 16:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbChunkSts.noCkAckRx;
          lbFoundOid = true;
          break;
        }
        case 17:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbChunkSts.noDataTx;
          lbFoundOid = true;
          break;
        }
        case 18:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbChunkSts.noDataReTx;
          lbFoundOid = true;
          break;
        }
        case 19:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbChunkSts.noDataRx;
          lbFoundOid = true;
          break;
        }
        case 20:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbChunkSts.noDAckTx;
          lbFoundOid = true;
          break;
        }
        case 21:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbChunkSts.noDAckRx;
          lbFoundOid = true;
          break;
        }
        case 22:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbChunkSts.noShDwnCmpltTx;
          lbFoundOid = true;
          break;
        }
        case 23:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbChunkSts.noShDwnCmpltRx;
          lbFoundOid = true;
          break;
        }
        case 24:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbByteSts.bytesTx;
          lbFoundOid = true;
          break;
        }
        case 25:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbByteSts.bytesRx;
          lbFoundOid = true;
          break;
        }
        case 26:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbDnsSts.noQueryTx;
          lbFoundOid = true;
          break;
        }
        case 27:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbDnsSts.noQueryReTx;
          lbFoundOid = true;
          break;
        }
        case 28:
        {
          lpOid->stsValue = aeMgmt.t.sts.u.genSts.sbDnsSts.noQueryRspRx;
          lbFoundOid = true;
          break;
        }
        default:
        {
  				lbFoundOid = false;
          logger.logMsg (ERROR_FLAG, 0, "Unknown Element type <%d> read",
           lpOid->element);
          break;
        }
      }
    }

    //update the Statistics Manager for the OID value
    if (lbFoundOid == true)
      updateStsMgr (lpOid);
    else
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "OID <%s> has not been mapped", lpOid->oidString);
    }
  }


  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::handleSbRsp");
  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   handleHiRsp()
*
*     Desc:  handle TUCL Response
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int
INGwSmStsHdlr::handleHiRsp (HiMngmt &aeMgmt)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::handleHiRsp");


  //get the current stats level from the repository
  int liLevel = mpDist->getSmRepository()->getStsLevel();

  //set the layer id
  int liLayer = BP_AIN_SM_TUC_LAYER;

  //find the operation type
  int liOper;

  if (aeMgmt.hdr.elmId.elmnt == STGEN)
  {
    liOper = BP_AIN_SM_SUBTYPE_GENSTS;
  }
  else if (aeMgmt.hdr.elmId.elmnt == STTSAP)
  {
    liOper = BP_AIN_SM_SUBTYPE_USPSTS;
  }

  long liHashKey = mpStsMap->getStsHashKey (liLayer, liOper, liLevel);

  if (aeMgmt.cfm.status == LCM_PRIM_NOK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to retrieve statistics <%d>", liHashKey);
    return BP_AIN_SM_FAIL;
  }

  int liRetVal = 0;
  INGwSmStsOidList *lpOidList = mpStsMap->getList (liHashKey, liRetVal);

  if (liRetVal != BP_AIN_SM_OK || lpOidList->empty())
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to retrieve statistics list from Map<%d>", liHashKey);
    return BP_AIN_SM_FAIL;
  }

  INGwSmStsOidList::iterator leOidIter;

  for (leOidIter = lpOidList->begin();
       leOidIter != lpOidList->end();
       leOidIter++)
  {   
    INGwSmStsOid *lpOid = *(leOidIter);

    if (lpOid == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "Oid retrieved from list is NULL");
      continue;
    }

    //this bool will be used to check if element has been mapped or not
    bool lbFoundOid = false;


    //check the element in oid and retrieve from the stack structure
    if (liOper == BP_AIN_SM_SUBTYPE_GENSTS)
    {
      switch (lpOid->element)
      {
        default:
        {
          logger.logMsg (ERROR_FLAG, 0, "Unknown Element type <%d> read",
           lpOid->element);
          break;
        }
      }
    }

    //update the Statistics Manager for the OID value
    if (lbFoundOid == true)
      updateStsMgr (lpOid);
    else
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "OID <%s> has not been mapped", lpOid->oidString);
    }
  }


  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::handleHiRsp");
  return BP_AIN_SM_OK;
}


/******************************************************************************
*
*     Fun:   sendRequest()
*
*     Desc:  send the message to the Stack - NOT USED
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int 
INGwSmStsHdlr::sendRequest(INGwSmQueueMsg *apMsg,INGwSmRequestContext *apContext)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Entering INGwSmStsHdlr::sendRequest", miTransId);

  switch (miSubOp)
  {
    case BP_AIN_SM_SUBTYPE_STS:
    {
      int liRetVal = createStatsReq (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d>: Failed to create the statistics request",
          miTransId);
					return liRetVal; // rajeev
      }

    }
    break;
  } 

   logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Leaving INGwSmStsHdlr::sendRequest", miTransId);
  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   createStatsReq()
*
*     Desc:  Create statistics request for each layer 
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int
INGwSmStsHdlr::createStatsReq (StackReqResp *stackReq)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmStsHdlr:: createStatsReq for layer <%d> <%x>", 
    miTransId, miLayer,mlBitMapCopy);

  int aiBitMap = mlBitMapCopy;

	if(aiBitMap == 0) // stray call
		return BP_AIN_SM_FAIL;

  switch (miLayer)
  {
    case BP_AIN_SM_TCA_LAYER:
    {

      if ((aiBitMap & STS_MASK_ST_GEN) == STS_MASK_ST_GEN)
      {
        mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_ST_GEN);

        logger.logMsg (VERBOSE_FLAG, 0,
          "Getting TCAP General Statistics");
      }
      else
      {
        logger.logMsg (ERROR_FLAG, 0,
          "Uknown Statistics Operation passed");
        return BP_AIN_SM_FAIL;
      }

      StMngmt *lpSts = new StMngmt;
      cmMemset((U8 *)lpSts, 0, sizeof(StMngmt));
      StMngmt &leSts = *lpSts;

      leSts.hdr.response.selector = BP_AIN_SM_COUPLING;
      leSts.hdr.response.prior = BP_AIN_SM_PRIOR;
      leSts.hdr.response.route = BP_AIN_SM_ROUTE;
      leSts.hdr.response.mem.region  = BP_AIN_SM_REGION;
      leSts.hdr.response.mem.pool = BP_AIN_SM_POOL;

      leSts.hdr.msgType = TSTS;
      leSts.hdr.transId = miTransId;
      /* set configuration parameters */
      leSts.hdr.elmId.elmnt = STTCUSAP; 
      /* Get TcapUsapId from BlkConfig */
      LocalSsnSeq* localSsnList = INGwSmBlkConfig::getInstance().getLocalSsnList();
      LocalSsnSeq::iterator it;
	    for(it=localSsnList->begin(); it != localSsnList->end(); ++it) 
	    {
        logger.logMsg (TRACE_FLAG, 0,"tcapUsapId for statistics <%d>",(*it).tcapUsapId);
        leSts.hdr.elmId.elmntInst1 = (*it).tcapUsapId;
      }


      //initialize the post structure
      Pst *lmPst = mpDist->getSmRepository()->getPst (BP_AIN_SM_TCA_LAYER);
      lmPst->event = EVTLSTSTSREQ;
      lmPst->srcEnt = ENTSM;
      lmPst->dstEnt = ENTST;
      lmPst->dstProcId = SFndProcId();
      lmPst->srcProcId = SFndProcId();

      smMiLstStsReq (lmPst, BP_AIN_SM_STS_ACTION, &leSts);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      //mrDist.updateRspStruct(miTransId,stackReq);
      
      delete lpSts;

    }
    break;
    
    case BP_AIN_SM_SCC_LAYER:
    {
      SpMngmt *lpSts = new SpMngmt;
      cmMemset((U8 *)lpSts, 0, sizeof(SpMngmt));
      SpMngmt &leSts = *lpSts;

      if ((aiBitMap & STS_MASK_SP_GEN) == STS_MASK_SP_GEN)
      {
        mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_SP_GEN);

        /* set configuration parameters */
        leSts.hdr.elmId.elmnt = STGEN;

        logger.logMsg (VERBOSE_FLAG, 0,
          "Getting SCCP General Statistics");
      }
      else if ((aiBitMap & STS_MASK_SP_USAP) == STS_MASK_SP_USAP)
      {
        mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_SP_USAP);

        /* set configuration parameters */
        leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;
        leSts.hdr.elmId.elmnt = STTSAP;

        logger.logMsg (VERBOSE_FLAG, 0,
          "Getting SCCP Upper SAP Statistics");
      }
      else if ((aiBitMap & STS_MASK_SP_LSAP) == STS_MASK_SP_LSAP)
      {
        mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_SP_LSAP);
    
        /* set configuration parameters */
        leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;
        leSts.hdr.elmId.elmnt = STNSAP;
    
        logger.logMsg (VERBOSE_FLAG, 0,
          "Getting SCCP Lower SAP Statistics");
      }
      else if ((aiBitMap & STS_MASK_SP_RTE) == STS_MASK_SP_RTE)
      {
        mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_SP_RTE);
    
        /* set configuration parameters */
        leSts.hdr.elmId.elmnt = STROUT;
    
        /*
        * NOTE : Route Statistics are not supported in phase 1 since
        *        I don't know how the statistics for each route will 
        *        be displayed at EMS.
        */
        //leSts.t.sts.spRteSts.nwId = mpDist->getSmRepository()->getStsNwId();
        //leSts.t.sts.spRteSts.pc = mpDist->getSmRepository()->getStsPc();
    
        logger.logMsg (VERBOSE_FLAG, 0,
          "Getting SCCP Route Statistics");
      }
      else
      {
        logger.logMsg (ERROR_FLAG, 0,
          "Uknown Statistics Operation passed");
    
        delete lpSts;
        return BP_AIN_SM_FAIL;
      }
    
 

      leSts.hdr.response.selector = BP_AIN_SM_COUPLING;
      leSts.hdr.response.prior = BP_AIN_SM_PRIOR;
      leSts.hdr.response.route = BP_AIN_SM_ROUTE;
      leSts.hdr.response.mem.region  = BP_AIN_SM_REGION;
      leSts.hdr.response.mem.pool = BP_AIN_SM_POOL;

      leSts.hdr.msgType = TSTS;
      leSts.hdr.transId = miTransId;

      //initialize the post structure
      Pst *lmPst = mpDist->getSmRepository()->getPst (BP_AIN_SM_SCC_LAYER);
      lmPst->event = EVTLSPSTSREQ;
      lmPst->dstProcId = SFndProcId();
      lmPst->srcProcId = SFndProcId();

      smMiLspStsReq (lmPst, BP_AIN_SM_STS_ACTION, &leSts);

      
      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      //mrDist.updateRspStruct(miTransId,stackReq);
     

      delete lpSts;

    }
    break;

    case BP_AIN_SM_MTP3_LAYER:
    {
      SnMngmt *lpSts = new SnMngmt;
      cmMemset((U8 *)lpSts, 0, sizeof(SnMngmt));
      SnMngmt &leSts = *lpSts;
      
      if ((aiBitMap & STS_MASK_SN_SP) == STS_MASK_SN_SP)
      {
        mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_SN_SP);

        /* set configuration parameters */
        leSts.hdr.elmId.elmnt = STGEN;

        logger.logMsg (TRACE_FLAG, 0,
                 "Getting MTP3 Signalling Point Statistics");
      }
      else if ((aiBitMap & STS_MASK_SN_LINK) == STS_MASK_SN_LINK)
      {
        mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_SN_LINK);

        /* set configuration parameters */
        leSts.hdr.elmId.elmnt = STDLSAP;
      	/* Get MTP2SapId from BlkConfig */
      	LnkSeq* linkList = INGwSmBlkConfig::getInstance().getLinkList();
      	LnkSeq::iterator it;
      	for(it=linkList->begin(); it != linkList->end(); ++it) 
      	{
          logger.logMsg (TRACE_FLAG, 0,"Mtp2UsapId for statistics <%d>",(*it).mtp2UsapId);
          leSts.hdr.elmId.elmntInst1 = (*it).mtp2UsapId;

	  			leSts.hdr.response.selector = BP_AIN_SM_COUPLING;
      		leSts.hdr.response.prior = BP_AIN_SM_PRIOR;
      		leSts.hdr.response.route = BP_AIN_SM_ROUTE;
      		leSts.hdr.response.mem.region  = BP_AIN_SM_REGION;
      		leSts.hdr.response.mem.pool = BP_AIN_SM_POOL;

      		leSts.hdr.msgType = TSTS;
      		leSts.hdr.transId = miTransId;

      		//initialize the post structure
      		Pst *lmPst = mpDist->getSmRepository()->getPst (BP_AIN_SM_MTP3_LAYER);
      		lmPst->event = EVTLSNSTSREQ;
      		lmPst->dstProcId = SFndProcId();
      		lmPst->srcProcId = SFndProcId();

      		smMiLsnStsReq (lmPst, BP_AIN_SM_STS_ACTION, &leSts);

      		stackReq->resp.procId = stackReq->procId;
      		stackReq->txnType = NORMAL_TXN;
      		stackReq->txnStatus = INPROGRESS;
      	}
      	delete lpSts;
    
				return BP_AIN_SM_OK;
      }
      else if ((aiBitMap & STS_MASK_SN_RTE) == STS_MASK_SN_RTE)
      {
        vector<int> m_selfPc;
        int selfPc = 0;

        mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_SN_RTE);
    
        /* set configuration parameters */
        leSts.hdr.elmId.elmnt = STROUT;
      	/* Get Self PC from BlkConfig, so that stsReq canbe blocked for self route */
        AddNetworkSeq* nwList = INGwSmBlkConfig::getInstance().getNetworkList();
      	AddNetworkSeq::iterator itN;
      	for(itN = nwList->begin(); itN != nwList->end(); ++itN) 
      	{
          for(int i=0 ; i<(*itN).nmbSpcs ; i++){
            m_selfPc.push_back((*itN).selfPc[i]);  
          }
        } 

      	/* Get DPC and user part switch from BlkConfig */
      	RouteSeq* rteList = INGwSmBlkConfig::getInstance().getRouteList();
      	RouteSeq::iterator it;
      	for(it = rteList->begin(); it != rteList->end(); ++it) 
      	{
          logger.logMsg (TRACE_FLAG, 0,"DPC <%d> and upSwtch <%d> for MTP3 route statistics ",(*it).dpc,(*it).upSwtch);
    
          vector<int>::iterator iter; 
          for (iter=m_selfPc.begin(); iter < m_selfPc.end(); iter++){
            if((*it).dpc == *iter)
            {
              selfPc = 1;
              break;
            }
          }
          if((selfPc != 1) && ((*it).swtchType != 255)){ //SwtchType check is for not sending stats req for M3UA PC

       	    leSts.t.sts.s.snRout.dpc = (*it).dpc;

            leSts.t.sts.s.snRout.upSwtch = LSN_SW_NTT;

					  leSts.hdr.response.selector = BP_AIN_SM_COUPLING;
            leSts.hdr.response.prior = BP_AIN_SM_PRIOR;
            leSts.hdr.response.route = BP_AIN_SM_ROUTE;
            leSts.hdr.response.mem.region  = BP_AIN_SM_REGION;
            leSts.hdr.response.mem.pool = BP_AIN_SM_POOL;

            leSts.hdr.msgType = TSTS;
            leSts.hdr.transId = miTransId;

            //initialize the post structure
            Pst *lmPst = mpDist->getSmRepository()->getPst (BP_AIN_SM_MTP3_LAYER);
            lmPst->event = EVTLSNSTSREQ;
            lmPst->dstProcId = SFndProcId();
            lmPst->srcProcId = SFndProcId();

            smMiLsnStsReq (lmPst, BP_AIN_SM_STS_ACTION, &leSts);

            stackReq->resp.procId = stackReq->procId;
            stackReq->txnType = NORMAL_TXN;
            stackReq->txnStatus = INPROGRESS;

            //mrDist.updateRspStruct(miTransId,stackReq);
          }
          selfPc = 0;


      	}
        delete lpSts;
				return BP_AIN_SM_OK;
      }
      else if ((aiBitMap & STS_MASK_SN_LNKSET) == STS_MASK_SN_LNKSET)
      {
        mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_SN_LNKSET);
    
        /* set configuration parameters */
        leSts.hdr.elmId.elmnt = STLNKSET;
        leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;
        leSts.hdr.elmId.elmntInst2 = 1;

        logger.logMsg (TRACE_FLAG, 0,
                   "Getting MTP3 LinkSet Statistics ");
      }
      else
      {
        logger.logMsg (ERROR_FLAG, 0,
          "Uknown Statistics Operation passed");
        delete lpSts;
        return BP_AIN_SM_FAIL;
      }
    
      leSts.hdr.response.selector = BP_AIN_SM_COUPLING;
      leSts.hdr.response.prior = BP_AIN_SM_PRIOR;
      leSts.hdr.response.route = BP_AIN_SM_ROUTE;
      leSts.hdr.response.mem.region  = BP_AIN_SM_REGION;
      leSts.hdr.response.mem.pool = BP_AIN_SM_POOL;

      leSts.hdr.msgType = TSTS;
      leSts.hdr.transId = miTransId;

      //initialize the post structure
      Pst *lmPst = mpDist->getSmRepository()->getPst (BP_AIN_SM_MTP3_LAYER);
      lmPst->event = EVTLSNSTSREQ;
      lmPst->dstProcId = SFndProcId();
      lmPst->srcProcId = SFndProcId();

      smMiLsnStsReq (lmPst, BP_AIN_SM_STS_ACTION, &leSts);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      //mrDist.updateRspStruct(miTransId,stackReq);
      

      delete lpSts;

    }
    break;

    case BP_AIN_SM_M3U_LAYER:
    {
      ItMgmt *lpSts = new ItMgmt;
      cmMemset((U8 *)lpSts, 0, sizeof(ItMgmt));
      ItMgmt &leSts = *lpSts;

      if ((aiBitMap & STS_MASK_IT_GEN) == STS_MASK_IT_GEN)
      {
        mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_IT_GEN);

        /* set configuration parameters */
        leSts.hdr.elmId.elmnt = STITGEN;
        //leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;
    
        logger.logMsg (VERBOSE_FLAG, 0,
          "Getting M3UA General Statistics");
      }
      else if ((aiBitMap & STS_MASK_IT_USAP) == STS_MASK_IT_USAP)
      {
        mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_IT_USAP);
    
        /* set configuration parameters */
        leSts.hdr.elmId.elmnt = STITNSAP;
        leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;
    
        leSts.t.sts.u.sntSts.spId = mpDist->getSmRepository()->getItSpId();

        logger.logMsg (VERBOSE_FLAG, 0,
          "Getting M3UA Upper SAP Statistics");
      }
      else if ((aiBitMap & STS_MASK_IT_LSAP) == STS_MASK_IT_LSAP)
      {
        mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_IT_LSAP);
    
        /* set configuration parameters */
        leSts.hdr.elmId.elmnt = STITSCTSAP;
        leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;
    
        leSts.t.sts.u.sctSts.suId = mpDist->getSmRepository()->getItSuId();
    
        logger.logMsg (VERBOSE_FLAG, 0,
          "Getting M3UA Lower SAP Statistics");
      }
      else if ((aiBitMap & STS_MASK_IT_PSPST) == STS_MASK_IT_PSPST)
      {
        mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_IT_PSPST);

        /* set configuration parameters */
        leSts.hdr.elmId.elmnt = STITPSP;
        leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;
    
        /*
        * NOTE : There can be a list of PSPId and hence there is no way to
        * display them in the EMS currently. This will be supported in 
        * phase 2.
        */
        //leSts.t.sts.u.pspSts.pspId = mpDist->getSmRepository()->getStsPspId();

        logger.logMsg (VERBOSE_FLAG, 0,
          "Getting M3UA PSP Statistics");
      }
      else
      {
        logger.logMsg (ERROR_FLAG, 0,
          "Uknown Statistics Operation passed");
        delete lpSts;
        return BP_AIN_SM_FAIL;
      }

      leSts.hdr.response.selector = BP_AIN_SM_COUPLING;
      leSts.hdr.response.prior = BP_AIN_SM_PRIOR;
      leSts.hdr.response.route = BP_AIN_SM_ROUTE;
      leSts.hdr.response.mem.region  = BP_AIN_SM_REGION;
      leSts.hdr.response.mem.pool = BP_AIN_SM_POOL;

      leSts.hdr.msgType = TSTS;
      leSts.hdr.transId = miTransId;

      //initialize the post structure
      Pst *lmPst = mpDist->getSmRepository()->getPst (BP_AIN_SM_M3U_LAYER);
      lmPst->event = EVTLITSTSREQ;
      lmPst->dstProcId = SFndProcId();
      lmPst->srcProcId = SFndProcId();

      smMiLitStsReq (lmPst, BP_AIN_SM_STS_ACTION, &leSts);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      //mrDist.updateRspStruct(miTransId,stackReq);
      

      delete lpSts;

    }
    break;

    case BP_AIN_SM_SCT_LAYER:
    {
      SbMgmt *lpSts = new SbMgmt;
      cmMemset((U8 *)lpSts, 0, sizeof(SbMgmt));
      SbMgmt &leSts = *lpSts;
    
      if ((aiBitMap & STS_MASK_SB_GEN) == STS_MASK_SB_GEN)
      {
        mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_SB_GEN);
    
        /* set configuration parameters */
        leSts.hdr.elmId.elmnt = STSBGEN;
        //leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

        logger.logMsg (VERBOSE_FLAG, 0,
          "Getting SCTP General Statistics");
      }
      else if ((aiBitMap & STS_MASK_SB_USAP) == STS_MASK_SB_USAP)
      {
        mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_SB_USAP);
    
        /* set configuration parameters */
        leSts.hdr.elmId.elmnt = STSBSCTSAP;
        leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;
    
        leSts.t.sts.sapId = 0;
    
        logger.logMsg (VERBOSE_FLAG, 0,
          "Getting SCTP Upper SAP Statistics");
      }
      else if ((aiBitMap & STS_MASK_SB_LSAP) == STS_MASK_SB_LSAP)
      {
        mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_SB_LSAP);
    
        /* set configuration parameters */
        leSts.hdr.elmId.elmnt = STSBTSAP;
        leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

        leSts.t.sts.sapId = 0;

        logger.logMsg (VERBOSE_FLAG, 0,
          "Getting SCTP Lower SAP Statistics");
      }
      else
      {
        logger.logMsg (ERROR_FLAG, 0,
          "Uknown Statistics Operation passed");
        delete lpSts;
        return BP_AIN_SM_FAIL;
      }


      leSts.hdr.response.selector = BP_AIN_SM_COUPLING;
      leSts.hdr.response.prior = BP_AIN_SM_PRIOR;
      leSts.hdr.response.route = BP_AIN_SM_ROUTE;
      leSts.hdr.response.mem.region  = BP_AIN_SM_REGION;
      leSts.hdr.response.mem.pool = BP_AIN_SM_POOL;

      leSts.hdr.msgType = TSTS;
      leSts.hdr.transId = miTransId;

      //initialize the post structure
      Pst *lmPst = mpDist->getSmRepository()->getPst (BP_AIN_SM_SCT_LAYER);
      lmPst->event = LSB_EVTSTSREQ;
      lmPst->dstProcId = SFndProcId();
      lmPst->srcProcId = SFndProcId();

      smMiLsbStsReq (lmPst, BP_AIN_SM_STS_ACTION, &leSts);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      //mrDist.updateRspStruct(miTransId,stackReq);
      

      delete lpSts;

    }
    break;

    default:
    {
      logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> :Invalid Layer passed <%d>", miTransId, miLayer);
      return BP_AIN_SM_FAIL;
    }
  }
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmStsHdlr::createStatsReq", miTransId);

  return BP_AIN_SM_OK;

}





int 
INGwSmStsHdlr::setStatisticsMask(long tMask)
{
  logger.logMsg (ALWAYS_FLAG, 0,
    "Entering INGwSmStsHdlr::setStatisticsMask, to set mask <%lu>",tMask);

  //mlBitMapCopy = tMask;
  mlBitMap = tMask;

  logger.logMsg (ALWAYS_FLAG, 0,
    "Leaving INGwSmStsHdlr::setStatisticsMask");
  return BP_AIN_SM_OK;
}

int 
INGwSmStsHdlr::getStatisticsMask(std::ostrstream &statsLayer)
{
  logger.logMsg (ALWAYS_FLAG, 0,
    "Entering INGwSmStsHdlr::getStatisticsMask ");

  int mask = mlBitMap;
  if((mask & STS_MASK_ST_ALL) > 0)
  {
    statsLayer << "TCAP ";
  }
  if((mask & STS_MASK_SP_ALL) > 0)
  {
    statsLayer << "|SCCP ";
  }
  if((mask & STS_MASK_SN_ALL) > 0)
  {
    statsLayer << "|MTP3 ";
  }
  if((mask & STS_MASK_IT_ALL) > 0)
  {
    statsLayer << "|M3UA ";
  }
  if((mask & STS_MASK_SB_ALL) > 0)
  {
    statsLayer << "|SCTP ";
  }
  
  statsLayer << "\n";
  logger.logMsg (ALWAYS_FLAG, 0,
    "Leaving INGwSmStsHdlr::getStatisticsMask");
  return BP_AIN_SM_OK;
}



/* 
 * INAP Layer Operations
 */

/******************************************************************************
*
*     Fun:   inapGetStatistics()
*
*     Desc:  Get INAP Layer Statistics
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
#if 0
int
INGwSmStsHdlr::inapGetStatistics (int aiBitMap)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::inapGetStatistics");


  if ((aiBitMap & STS_MASK_IE_GEN) == STS_MASK_IE_GEN)
  {
    mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_IE_GEN);

    logger.logMsg (VERBOSE_FLAG, 0,
      "Getting INAP General Statistics");
  }
  else
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Uknown Statistics Operation passed");
    return BP_AIN_SM_FAIL;
  }

  IeMngmt *lpSts = new IeMngmt;
  cmMemset((U8 *)lpSts, 0, sizeof(IeMngmt));
  IeMngmt &leSts = *lpSts;
 
  leSts.hdr.response.selector = BP_AIN_SM_COUPLING;
  leSts.hdr.response.prior = BP_AIN_SM_PRIOR;
  leSts.hdr.response.route = BP_AIN_SM_ROUTE;
  leSts.hdr.response.mem.region  = BP_AIN_SM_REGION;
  leSts.hdr.response.mem.pool = BP_AIN_SM_POOL;

  leSts.hdr.msgType = TSTS;
  leSts.hdr.transId = miTransId;
  /* set configuration parameters */
  leSts.hdr.elmId.elmnt = STINSAP;
  leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

  //initialize the post structure
  Pst *lmPst = mpDist->getSmRepository()->getPst (BP_AIN_SM_AIN_LAYER);
  lmPst->event = LIE_EVTSTSREQ;

  smMiLieStsReq (lmPst, BP_AIN_SM_STS_ACTION, &leSts);

  delete lpSts;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::inapGetStatistics");
  return BP_AIN_SM_OK;
}
#endif


/* 
 * TCAP Layer Operations
 */

/******************************************************************************
*
*     Fun:   tcapGetStatistics()
*
*     Desc:  Get TCAP Layer Statistics
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int 
INGwSmStsHdlr::tcapGetStatistics (int aiBitMap)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::tcapGetStatistics");


  if ((aiBitMap & STS_MASK_ST_GEN) == STS_MASK_ST_GEN)
  {
    mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_ST_GEN);

    logger.logMsg (VERBOSE_FLAG, 0,
      "Getting TCAP General Statistics");
  }
  else
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Uknown Statistics Operation passed");
    return BP_AIN_SM_FAIL;
  }

  StMngmt *lpSts = new StMngmt;
  cmMemset((U8 *)lpSts, 0, sizeof(StMngmt));
  StMngmt &leSts = *lpSts;

  leSts.hdr.response.selector = BP_AIN_SM_COUPLING;
  leSts.hdr.response.prior = BP_AIN_SM_PRIOR;
  leSts.hdr.response.route = BP_AIN_SM_ROUTE;
  leSts.hdr.response.mem.region  = BP_AIN_SM_REGION;
  leSts.hdr.response.mem.pool = BP_AIN_SM_POOL;

  leSts.hdr.msgType = TSTS;
  leSts.hdr.transId = miTransId;
  /* set configuration parameters */
  leSts.hdr.elmId.elmnt = STTCUSAP;
  leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

  //initialize the post structure
  Pst *lmPst = mpDist->getSmRepository()->getPst (BP_AIN_SM_TCA_LAYER);
  lmPst->event = EVTLSTSTSREQ;
  lmPst->srcEnt = ENTSM;
  lmPst->dstEnt = ENTST;

  smMiLstStsReq (lmPst, BP_AIN_SM_STS_ACTION, &leSts);

  delete lpSts;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::tcapGetStatistics");
  return BP_AIN_SM_OK;
}

//Yogesh1
/******************************************************************************
*
*     Fun:   ldfM3uaGetStatistics
*
*     Desc:  Get LDF-M3Ua Layer Statistics
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int 
INGwSmStsHdlr::ldfM3uaGetStatistics(int aiBitMap)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::ldfM3uaGetStatistics");


  if ((aiBitMap & STS_MASK_DV_GEN) == STS_MASK_DV_GEN)
  {
    mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_DV_GEN);

    logger.logMsg (VERBOSE_FLAG, 0,
      "Getting LDF-M3UA General Statistics");
  }
  else
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Uknown Statistics Operation passed");
    return BP_AIN_SM_FAIL;
  }

  LdvMngmt *lpSts = new LdvMngmt;
  cmMemset((U8 *)lpSts, 0, sizeof(LdvMngmt));
  LdvMngmt &leSts = *lpSts;

  leSts.hdr.response.selector = BP_AIN_SM_COUPLING;
  leSts.hdr.response.prior = BP_AIN_SM_PRIOR;
  leSts.hdr.response.route = BP_AIN_SM_ROUTE;
  leSts.hdr.response.mem.region  = BP_AIN_SM_REGION;
  leSts.hdr.response.mem.pool = BP_AIN_SM_POOL;

  leSts.hdr.msgType = TSTS;
  leSts.hdr.transId = miTransId;
  /* set configuration parameters */
  leSts.hdr.elmId.elmnt = STDVGEN;
  leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

  //initialize the post structure
  Pst *lmPst = mpDist->getSmRepository()->getPst (BP_AIN_SM_LDF_M3UA_LAYER);
  lmPst->event = EVTLDVSTSREQ;

  smMiLdvStsReq (lmPst, BP_AIN_SM_STS_ACTION, &leSts);

  delete lpSts;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::ldfM3uaGetStatistics");
  return BP_AIN_SM_OK;
}

//Yogesh2
/******************************************************************************
*
*     Fun:   ldfMtp3GetStatistics()
*
*     Desc:  Get LDF-MTP3 Layer Statistics
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int 
INGwSmStsHdlr::ldfMtp3GetStatistics (int aiBitMap)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::ldfMtp3GetStatistics");


  if ((aiBitMap & STS_MASK_DN_GEN) == STS_MASK_DN_GEN)
  {
    mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_DN_GEN);

    logger.logMsg (VERBOSE_FLAG, 0,
      "Getting Message Router General Statistics");
  }
  else
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Uknown Statistics Operation passed");
    return BP_AIN_SM_FAIL;
  }

  LdnMngmt *lpSts = new LdnMngmt;
  cmMemset((U8 *)lpSts, 0, sizeof(LdnMngmt));
  LdnMngmt &leSts = *lpSts;

  leSts.hdr.response.selector = BP_AIN_SM_COUPLING;
  leSts.hdr.response.prior = BP_AIN_SM_PRIOR;
  leSts.hdr.response.route = BP_AIN_SM_ROUTE;
  leSts.hdr.response.mem.region  = BP_AIN_SM_REGION;
  leSts.hdr.response.mem.pool = BP_AIN_SM_POOL;

  leSts.hdr.msgType = TSTS;
  leSts.hdr.transId = miTransId;
  /* set configuration parameters */
  leSts.hdr.elmId.elmnt = STDNGEN;
  leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

  //initialize the post structure
  Pst *lmPst = mpDist->getSmRepository()->getPst (BP_AIN_SM_LDF_M3UA_LAYER);
  lmPst->event = EVTLDNSTSREQ;

  smMiLdnStsReq (lmPst, BP_AIN_SM_STS_ACTION, &leSts);

  delete lpSts;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::ldfMtp3GetStatistics");
  return BP_AIN_SM_OK;
}

//Yogesh3
/******************************************************************************
*
*     Fun:   mrGetStatistics()
*
*     Desc:  Get MR Layer Statistics
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int 
INGwSmStsHdlr::mrGetStatistics (int aiBitMap)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::mrGetStatistics");


  if ((aiBitMap & STS_MASK_MR_GEN) == STS_MASK_MR_GEN)
  {
    mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_MR_GEN);

    logger.logMsg (VERBOSE_FLAG, 0,
      "Getting Message Router General Statistics");
  }
  else
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Uknown Statistics Operation passed");
    return BP_AIN_SM_FAIL;
  }

  MrMngmt *lpSts = new MrMngmt;
  cmMemset((U8 *)lpSts, 0, sizeof(MrMngmt));
  MrMngmt &leSts = *lpSts;

  leSts.hdr.response.selector = BP_AIN_SM_COUPLING;
  leSts.hdr.response.prior = BP_AIN_SM_PRIOR;
  leSts.hdr.response.route = BP_AIN_SM_ROUTE;
  leSts.hdr.response.mem.region  = BP_AIN_SM_REGION;
  leSts.hdr.response.mem.pool = BP_AIN_SM_POOL;

  leSts.hdr.msgType = TSTS;
  leSts.hdr.transId = miTransId;
  /* set configuration parameters */
  leSts.hdr.elmId.elmnt = STMRRSET;
  leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

  //initialize the post structure
  Pst *lmPst = mpDist->getSmRepository()->getPst (BP_AIN_SM_MR_LAYER);
  lmPst->event = EVTLMRSTSREQ;

  smMiLmrStsReq (lmPst, BP_AIN_SM_STS_ACTION, &leSts);

  delete lpSts;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::mrGetStatistics");
  return BP_AIN_SM_OK;
}
//
/* 
 * SCCP Layer Operations
 */


/******************************************************************************
*
*     Fun:   sccpGetStatistics()
*
*     Desc:  Get SCCP Layer Statistics
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int 
INGwSmStsHdlr::sccpGetStatistics (int aiBitMap)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::sccpGetStatistics");


  SpMngmt *lpSts = new SpMngmt;
  cmMemset((U8 *)lpSts, 0, sizeof(SpMngmt));
  SpMngmt &leSts = *lpSts;

  if ((aiBitMap & STS_MASK_SP_GEN) == STS_MASK_SP_GEN)
  {
    mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_SP_GEN);

    /* set configuration parameters */
    leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;
    leSts.hdr.elmId.elmnt = STGEN;

    logger.logMsg (VERBOSE_FLAG, 0,
      "Getting SCCP General Statistics");
  }
  else if ((aiBitMap & STS_MASK_SP_USAP) == STS_MASK_SP_USAP)
  {
    mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_SP_USAP);

    /* set configuration parameters */
    leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;
    leSts.hdr.elmId.elmnt = STTSAP;

    logger.logMsg (VERBOSE_FLAG, 0,
      "Getting SCCP Upper SAP Statistics");
  }
  else if ((aiBitMap & STS_MASK_SP_LSAP) == STS_MASK_SP_LSAP)
  {
    mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_SP_LSAP);

    /* set configuration parameters */
    leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;
    leSts.hdr.elmId.elmnt = STNSAP;

    logger.logMsg (VERBOSE_FLAG, 0,
      "Getting SCCP Lower SAP Statistics");
  }
  else if ((aiBitMap & STS_MASK_SP_RTE) == STS_MASK_SP_RTE)
  {
    mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_SP_RTE);

    /* set configuration parameters */
    leSts.hdr.elmId.elmnt = STROUT;

    /*
     * NOTE : Route Statistics are not supported in phase 1 since
     *        I don't know how the statistics for each route will 
     *        be displayed at EMS.
     */
    //leSts.t.sts.spRteSts.nwId = mpDist->getSmRepository()->getStsNwId();
    //leSts.t.sts.spRteSts.pc = mpDist->getSmRepository()->getStsPc();

    logger.logMsg (VERBOSE_FLAG, 0,
      "Getting SCCP Route Statistics");
  }
  else
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Uknown Statistics Operation passed");

    delete lpSts;
    return BP_AIN_SM_FAIL;
  }


  leSts.hdr.response.selector = BP_AIN_SM_COUPLING;
  leSts.hdr.response.prior = BP_AIN_SM_PRIOR;
  leSts.hdr.response.route = BP_AIN_SM_ROUTE;
  leSts.hdr.response.mem.region  = BP_AIN_SM_REGION;
  leSts.hdr.response.mem.pool = BP_AIN_SM_POOL;

  leSts.hdr.msgType = TSTS;
  leSts.hdr.transId = miTransId;

  //initialize the post structure
  Pst *lmPst = mpDist->getSmRepository()->getPst (BP_AIN_SM_SCC_LAYER);
  lmPst->event = EVTLSPSTSREQ;

  smMiLspStsReq (lmPst, BP_AIN_SM_STS_ACTION, &leSts);


  delete lpSts;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::sccpGetStatistics");
  return BP_AIN_SM_OK;
}


/* 
 * M3UA Layer Operations
 */

// Get M3UA Layer Statistics
/******************************************************************************
*
*     Fun:   m3uaGetStatistics()
*
*     Desc:  Get M3UA Layer Statistics
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int 
INGwSmStsHdlr::m3uaGetStatistics (int aiBitMap)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::m3uaGetStatistics");


  ItMgmt *lpSts = new ItMgmt;
  cmMemset((U8 *)lpSts, 0, sizeof(ItMgmt));
  ItMgmt &leSts = *lpSts;

  if ((aiBitMap & STS_MASK_IT_GEN) == STS_MASK_IT_GEN)
  {
    mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_IT_GEN);

    /* set configuration parameters */
    leSts.hdr.elmId.elmnt = STITGEN;
    leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

    logger.logMsg (VERBOSE_FLAG, 0,
      "Getting M3UA General Statistics");
  }
  else if ((aiBitMap & STS_MASK_IT_USAP) == STS_MASK_IT_USAP)
  {
    mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_IT_USAP);

    /* set configuration parameters */
    leSts.hdr.elmId.elmnt = STITNSAP;
    leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

    leSts.t.sts.u.sntSts.spId = mpDist->getSmRepository()->getItSpId();

    logger.logMsg (VERBOSE_FLAG, 0,
      "Getting M3UA Upper SAP Statistics");
  }
  else if ((aiBitMap & STS_MASK_IT_LSAP) == STS_MASK_IT_LSAP)
  {
    mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_IT_LSAP);

    /* set configuration parameters */
    leSts.hdr.elmId.elmnt = STITSCTSAP;
    leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

    leSts.t.sts.u.sctSts.suId = mpDist->getSmRepository()->getItSuId();

    logger.logMsg (VERBOSE_FLAG, 0,
      "Getting M3UA Lower SAP Statistics");
  }
  else if ((aiBitMap & STS_MASK_IT_PSPST) == STS_MASK_IT_PSPST)
  {
    mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_IT_PSPST);

    /* set configuration parameters */
    leSts.hdr.elmId.elmnt = STITPSP;
    leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

    /*
     * NOTE : There can be a list of PSPId and hence there is no way to
     * display them in the EMS currently. This will be supported in 
     * phase 2.
     */
    //leSts.t.sts.u.pspSts.pspId = mpDist->getSmRepository()->getStsPspId();

    logger.logMsg (VERBOSE_FLAG, 0,
      "Getting M3UA PSP Statistics");
  }
  else
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Uknown Statistics Operation passed");
    delete lpSts;
    return BP_AIN_SM_FAIL;
  }

  leSts.hdr.response.selector = BP_AIN_SM_COUPLING;
  leSts.hdr.response.prior = BP_AIN_SM_PRIOR;
  leSts.hdr.response.route = BP_AIN_SM_ROUTE;
  leSts.hdr.response.mem.region  = BP_AIN_SM_REGION;
  leSts.hdr.response.mem.pool = BP_AIN_SM_POOL;

  leSts.hdr.msgType = TSTS;
  leSts.hdr.transId = miTransId;

  //initialize the post structure
  Pst *lmPst = mpDist->getSmRepository()->getPst (BP_AIN_SM_M3U_LAYER);
  lmPst->event = EVTLITSTSREQ;

  smMiLitStsReq (lmPst, BP_AIN_SM_STS_ACTION, &leSts);

  delete lpSts;
  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::m3uaGetStatistics");
  return BP_AIN_SM_OK;
}


/* 
 * SCTP Layer Operations
 */

/******************************************************************************
*
*     Fun:   sctpGetStatistics()
*
*     Desc:  Get SCTP Layer Statistics
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int 
INGwSmStsHdlr::sctpGetStatistics (int aiBitMap)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::sctpGetStatistics");


  SbMgmt *lpSts = new SbMgmt;
  cmMemset((U8 *)lpSts, 0, sizeof(SbMgmt));
  SbMgmt &leSts = *lpSts;

  if ((aiBitMap & STS_MASK_SB_GEN) == STS_MASK_SB_GEN)
  {
    mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_SB_GEN);

    /* set configuration parameters */
    leSts.hdr.elmId.elmnt = STSBGEN;
    leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

    logger.logMsg (VERBOSE_FLAG, 0,
      "Getting SCTP General Statistics");
  }
  else if ((aiBitMap & STS_MASK_SB_USAP) == STS_MASK_SB_USAP)
  {
    mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_SB_USAP);

    /* set configuration parameters */
    leSts.hdr.elmId.elmnt = STSBSCTSAP;
    leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

    leSts.t.sts.sapId = 0;

    logger.logMsg (VERBOSE_FLAG, 0,
      "Getting SCTP Upper SAP Statistics");
  }
  else if ((aiBitMap & STS_MASK_SB_LSAP) == STS_MASK_SB_LSAP)
  {
    mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_SB_LSAP);

    /* set configuration parameters */
    leSts.hdr.elmId.elmnt = STSBTSAP;
    leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

    leSts.t.sts.sapId = 0;

    logger.logMsg (VERBOSE_FLAG, 0,
      "Getting SCTP Lower SAP Statistics");
  }
  else
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Uknown Statistics Operation passed");
    delete lpSts;
    return BP_AIN_SM_FAIL;
  }

  leSts.hdr.response.selector = BP_AIN_SM_COUPLING;
  leSts.hdr.response.prior = BP_AIN_SM_PRIOR;
  leSts.hdr.response.route = BP_AIN_SM_ROUTE;
  leSts.hdr.response.mem.region  = BP_AIN_SM_REGION;
  leSts.hdr.response.mem.pool = BP_AIN_SM_POOL;

  leSts.hdr.msgType = TSTS;
  leSts.hdr.transId = miTransId;

  //initialize the post structure
  Pst *lmPst = mpDist->getSmRepository()->getPst (BP_AIN_SM_SCT_LAYER);
  lmPst->event = LSB_EVTSTSREQ;

  smMiLsbStsReq (lmPst, BP_AIN_SM_STS_ACTION, &leSts);

  delete lpSts;
  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::sctpGetStatistics");
  return BP_AIN_SM_OK;
}


/* 
 * TUCL Layer Operations
 */

/******************************************************************************
*
*     Fun:   tuclGetStatistics()
*
*     Desc:  Get TUCL Layer Statistics
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int 
INGwSmStsHdlr::tuclGetStatistics (int aiBitMap)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::tuclGetStatistics");


  HiMngmt *lpSts = new HiMngmt;
  cmMemset((U8 *)lpSts, 0, sizeof(HiMngmt));
  HiMngmt &leSts = *lpSts;

  if ((aiBitMap & STS_MASK_HI_GEN) == STS_MASK_HI_GEN)
  {
    mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_HI_GEN);

    /* set configuration parameters */
    leSts.hdr.elmId.elmnt = STGEN;
    leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

    logger.logMsg (VERBOSE_FLAG, 0,
      "Getting TUCL General Statistics");
  }
  else if ((aiBitMap & STS_MASK_HI_USAP) == STS_MASK_HI_USAP)
  {
    mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_HI_USAP);

    /* set configuration parameters */
    leSts.hdr.elmId.elmnt = STTSAP;
    leSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

    leSts.t.sts.s.sapSts.sapId = 0;

    logger.logMsg (VERBOSE_FLAG, 0,
      "Getting TUCL Upper SAP Statistics");
  }
  else
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Uknown Statistics Operation passed");
    delete lpSts;
    return BP_AIN_SM_FAIL;
  }

  leSts.hdr.response.selector = BP_AIN_SM_COUPLING;
  leSts.hdr.response.prior = BP_AIN_SM_PRIOR;
  leSts.hdr.response.route = BP_AIN_SM_ROUTE;
  leSts.hdr.response.mem.region  = BP_AIN_SM_REGION;
  leSts.hdr.response.mem.pool = BP_AIN_SM_POOL;

  leSts.hdr.msgType = TSTS;
  leSts.hdr.transId = miTransId;

  //initialize the post structure
  Pst *lmPst = mpDist->getSmRepository()->getPst (BP_AIN_SM_TUC_LAYER);
  lmPst->event = EVTLHISTSREQ;

  smMiLhiStsReq (lmPst, BP_AIN_SM_STS_ACTION, &leSts);

  delete lpSts;
  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::tuclGetStatistics");
  return BP_AIN_SM_OK;
}


/******************************************************************************
*
*     Fun:   smMiLieStsReq()
*
*     Desc:  send the statistics request to the INAP
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
#if 0
int
INGwSmStsHdlr::smMiLieStsReq (Pst *pst, Action action, IeMngmt *sts)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::smMiLieStsReq");

  //Just invoke it for loosely coupled for now
  cmPkLieStsReq (pst, action, sts);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::smMiLieStsReq");

  return BP_AIN_SM_OK;
}
#endif

/******************************************************************************
*
*     Fun:   smMiLsnStsReq()
*
*     Desc:  send the statistics request to the MTP3
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int
INGwSmStsHdlr::smMiLsnStsReq (Pst *pst, Action action, SnMngmt *sts)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::smMiLsnStsReq");

  cmPkLsnStsReq (pst, action, sts);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::smMiLsnStsReq");

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   smMiLstStsReq()
*
*     Desc:  send the statistics request to the TCAP
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int
INGwSmStsHdlr::smMiLstStsReq (Pst *pst, Action action, StMngmt *sts)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::smMiLstStsReq");

  //Just invoke it for loosely coupled for now
  cmPkLstStsReq (pst, action, sts);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::smMiLstStsReq");

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   smMiLspStsReq()
*
*     Desc:  send the statistics request to the SCCP
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int
INGwSmStsHdlr::smMiLspStsReq (Pst *pst, Action action, SpMngmt *sts)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::smMiLspStsReq");

  //Just invoke it for loosely coupled for now
  cmPkLspStsReq (pst, action, sts);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::smMiLspStsReq");

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   smMiLitStsReq()
*
*     Desc:  send the statistics request to the M3UA
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int
INGwSmStsHdlr::smMiLitStsReq (Pst *pst, Action action, ItMgmt *sts)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::smMiLitStsReq");

  //Just invoke it for loosely coupled for now
  cmPkLitStsReq (pst, action, sts);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::smMiLitStsReq");

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   smMiLsbStsReq()
*
*     Desc:  send the statistics request to the SCTP
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int
INGwSmStsHdlr::smMiLsbStsReq (Pst *pst, Action action, SbMgmt *sts)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::smMiLsbStsReq");

  //Just invoke it for loosely coupled for now
  cmPkLsbStsReq (pst, action, sts);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::smMiLsbStsReq");

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   smMiLhiStsReq()
*
*     Desc:  send the statistics request to the TUCL
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int
INGwSmStsHdlr::smMiLhiStsReq (Pst *pst, Action action, HiMngmt *sts)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::smMiLhiStsReq");
  
  //Just invoke it for loosely coupled for now
  cmPkLhiStsReq (pst, action, sts);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::smMiLhiStsReq");
  
  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   smMiLdvStsReq()
*
*     Desc:  send the statistics request to the LDF-M3UA
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int
INGwSmStsHdlr::smMiLdvStsReq (Pst *pst, Action action, LdvMngmt *sts)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::smMiLdvStsReq");

  //Just invoke it for loosely coupled for now
  cmPkLdvStsReq (pst, action, sts);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::smMiLdvStsReq");

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   smMiLdnStsReq()
*
*     Desc:  send the statistics request to the LDF-MTP3 
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int
INGwSmStsHdlr::smMiLdnStsReq (Pst *pst, Action action, LdnMngmt *sts)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::smMiLdnStsReq");

  //Just invoke it for loosely coupled for now
  cmPkLdnStsReq (pst, action, sts);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::smMiLdnStsReq");

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   smMiLmrStsReq()
*
*     Desc:  send the statistics request to the Message Router 
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int
INGwSmStsHdlr::smMiLmrStsReq (Pst *pst, Action action, MrMngmt *sts)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::smMiLmrStsReq");

  //Just invoke it for loosely coupled for now
  cmPkMiLmrStsReq (pst, action, sts);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::smMiLmrStsReq");

  return BP_AIN_SM_OK;
}


/******************************************************************************
*
*     Fun:   mtp3GetStatistics()
*
*     Desc:  Get MTP3 layer statistics
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int
INGwSmStsHdlr::mtp3GetStatistics (int aiBitMap) {

  logger.logMsg (TRACE_FLAG, 0,
    "entering INGwSmStsHdlr::mtp3GetStatistics ");

  SnMngmt* snSts = new SnMngmt;
  cmMemset((U8 *)snSts, 0, sizeof(SnMngmt));
  SnMngmt& l_snSts = *snSts;

  if ((aiBitMap & STS_MASK_SN_SP) == STS_MASK_SN_SP)
  {
    mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_SN_SP);

    /* set configuration parameters */
    l_snSts.hdr.elmId.elmnt = STGEN;

    //l_snSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

    logger.logMsg (TRACE_FLAG, 0,
                 "Getting MTP3 Signalling Point Statistics");
  }
  else if ((aiBitMap & STS_MASK_SN_LINK) == STS_MASK_SN_LINK)
  {
    mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_SN_LINK);

    /* set configuration parameters */
    l_snSts.hdr.elmId.elmnt = STDLSAP;
    l_snSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

    logger.logMsg (TRACE_FLAG, 0,
                   "Getting MTP3 Link Statistics");
  }
  else if ((aiBitMap & STS_MASK_SN_RTE) == STS_MASK_SN_RTE)
  {
    mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_IT_LSAP);

    /* set configuration parameters */
    l_snSts.hdr.elmId.elmnt = STROUT;

    logger.logMsg (TRACE_FLAG, 0,
                   "Getting MTP3 Route Statistics");
  }
  else if ((aiBitMap & STS_MASK_SN_LNKSET) == STS_MASK_SN_LNKSET)
  {
    mlBitMapCopy = (mlBitMapCopy ^ STS_MASK_SN_LNKSET);

    /* set configuration parameters */
    l_snSts.hdr.elmId.elmnt = STLNKSET;
    l_snSts.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;
    l_snSts.hdr.elmId.elmntInst2 = 1;

    logger.logMsg (TRACE_FLAG, 0,
                   "Getting MTP3 LinkSet Statistics ");
  }
  else
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Uknown Statistics Operation passed");
    delete snSts;
    return BP_AIN_SM_FAIL;
  }

  l_snSts.hdr.response.selector = BP_AIN_SM_COUPLING;
  l_snSts.hdr.response.prior = BP_AIN_SM_PRIOR;
  l_snSts.hdr.response.route = BP_AIN_SM_ROUTE;
  l_snSts.hdr.response.mem.region  = BP_AIN_SM_REGION;
  l_snSts.hdr.response.mem.pool = BP_AIN_SM_POOL;

  l_snSts.hdr.msgType = TSTS;
  l_snSts.hdr.transId = miTransId;

  //initialize the post structure
  Pst *lmPst = mpDist->getSmRepository()->getPst (BP_AIN_SM_MTP3_LAYER);
  lmPst->event = EVTLSNSTSREQ;

  smMiLsnStsReq (lmPst, BP_AIN_SM_STS_ACTION, &l_snSts);

  delete snSts;
  logger.logMsg (TRACE_FLAG, 0,
    "leaving INGwSmStsHdlr::mtp3GetStatistics");

  return BP_AIN_SM_OK;
}


/******************************************************************************
*
*     Fun:   handleSnRsp()
*
*     Desc:  handle MTP3 response
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int 
INGwSmStsHdlr::handleSnRsp (SnMngmt &aeMgmt) {


  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::handleSnRsp");

  
  //get the current stats level from the repository
  int liLevel = mpDist->getSmRepository()->getStsLevel();

  //set the layer id
  int liLayer = BP_AIN_SM_MTP3_LAYER;

  //find the operation type
  int liOper;

  if (aeMgmt.hdr.elmId.elmnt == STGEN)
  {
    liOper = BP_AIN_SM_SUBTYPE_GENSTS;
  }
  else if (aeMgmt.hdr.elmId.elmnt == STDLSAP)
  {
    liOper = BP_AIN_SM_SUBTYPE_LNKSTS;
  }
  else if (aeMgmt.hdr.elmId.elmnt == STROUT)
  {
    liOper = BP_AIN_SM_SUBTYPE_RTESTS;
  }
  else if (aeMgmt.hdr.elmId.elmnt == STLNKSET)
  {
    liOper = BP_AIN_SM_SUBTYPE_LSESTS;
  }

  long liHashKey = mpStsMap->getStsHashKey (liLayer, liOper, liLevel);

  if (aeMgmt.cfm.status == LCM_PRIM_NOK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to retrieve statistics <%d>", liHashKey);
    return BP_AIN_SM_FAIL;
  }

  int liRetVal = 0;
  INGwSmStsOidList *lpOidList = mpStsMap->getList (liHashKey, liRetVal);

  if (liRetVal != BP_AIN_SM_OK || lpOidList->empty())
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to retrieve statistics list from Map<%d>", liHashKey);
    return BP_AIN_SM_FAIL;
  }

  INGwSmStsOidList::iterator leOidIter;

  //this bool will be used to check if element has been mapped or not
  bool lbFoundOid = false;

  for (leOidIter = lpOidList->begin();
       leOidIter != lpOidList->end();
       leOidIter++)
  {
  	lbFoundOid = false;
    INGwSmStsOid *lpOid = *(leOidIter);

    if (lpOid == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "Oid retrieved from list is NULL");
      continue;
    }

    //check the element in oid and retrieve from the stack structure
    if (liOper == BP_AIN_SM_SUBTYPE_GENSTS)
    {
      switch (lpOid->element)
      {
        case 1:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snGlb.usrUnavailRx;
          lbFoundOid = true;
          break;
        }
        case 2:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snGlb.usrUnavailTx;
          lbFoundOid = true;
          break;
        }
        case 3:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snGlb.traTx;
          lbFoundOid = true;
          break;
        }
        case 4:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snGlb.traRx;
          lbFoundOid = true;
          break;
        }
        case 5:
        {
#if (SS7_ANS92 || SS7_ANS88 || SS7_ANS96 || defined(TDS_ROLL_UPGRADE_SUPPORT))
          lpOid->stsValue = aeMgmt.t.sts.s.snGlb.trwTx;
          lbFoundOid = true;
#endif
          break;
        }
        case 6:
        {
#if (SS7_ANS92 || SS7_ANS88 || SS7_ANS96 || defined(TDS_ROLL_UPGRADE_SUPPORT))
          lpOid->stsValue = aeMgmt.t.sts.s.snGlb.trwRx;
          lbFoundOid = true;
#endif
          break;
        }
        case 7:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snGlb.msuDropRteErr;
          lbFoundOid = true;
          break;
        }
        default:
        {
  				lbFoundOid = false;
          logger.logMsg (ERROR_FLAG, 0, "Unknown Element type <%d> read", 
           lpOid->element);
          break;
        }
      }
    }

    else if (liOper == BP_AIN_SM_SUBTYPE_LNKSTS)
    {
      switch (lpOid->element)
      {
        case 10:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snDLSAP.changeOverTx;
          lbFoundOid = true;
          break;
        }
        case 11:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snDLSAP.changeOverRx;
          lbFoundOid = true;
          break;
        }
        case 12:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snDLSAP.changeBackTx;
          lbFoundOid = true;
          break;
        }
        case 13:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snDLSAP.changeBackRx;
          lbFoundOid = true;
          break;
        }
        case 14:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snDLSAP.emChangeOverTx;
          lbFoundOid = true;
          break;
        }
        case 15:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snDLSAP.emChangeOverRx;
          lbFoundOid = true;
          break;
        }
        case 16:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snDLSAP.lnkInhTx;
          lbFoundOid = true;
          break;
        }
        case 17:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snDLSAP.lnkInhRx;
          lbFoundOid = true;
          break;
        }
        case 18:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snDLSAP.lnkUninhTx;
          lbFoundOid = true;
          break;
        }
        case 19:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snDLSAP.lnkUninhRx;
          lbFoundOid = true;
          break;
        }
        case 20:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snDLSAP.txDrop;
          lbFoundOid = true;
          break;
        }
        case 21:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snDLSAP.txCongDrop;
          lbFoundOid = true;
          break;
        }
        case 22:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snDLSAP.msuTx;
          lbFoundOid = true;
          break;
        }
        case 23:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snDLSAP.msuRx;
          lbFoundOid = true;
          break;
        }
        case 24:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snDLSAP.cong1;
          lbFoundOid = true;
          break;
        }
        case 25:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snDLSAP.cong2;
          lbFoundOid = true;
          break;
        }
        case 26:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snDLSAP.cong3;
          lbFoundOid = true;
          break;
        }
        case 27:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snDLSAP.durLnkUnav;
          lbFoundOid = true;
          break;
        }
        case 28:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snDLSAP.durLnkCong;
          lbFoundOid = true;
          break;
        }
        case 29:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snDLSAP.lnkErrPduRx;
          lbFoundOid = true;
          break;
        }
        default:
        {
  				lbFoundOid = false;
          logger.logMsg (ERROR_FLAG, 0, "Unknown Element type <%d> read", 
         lpOid->element);
          break;
        }
      }
    }
    else if (liOper == BP_AIN_SM_SUBTYPE_RTESTS)
    {
      switch (lpOid->element)
      {
        case 30:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snRout.dpc;
          lbFoundOid = true;
          break;
        }
        case 31:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snRout.upSwtch;
          lbFoundOid = true;
          break;
        }
        case 32:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snRout.txProhibTx;
          lbFoundOid = true;
          break;
        }
        case 33:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snRout.txProhibRx;
          lbFoundOid = true;
          break;
        }
        case 34:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snRout.txRestrictTx;
          lbFoundOid = true;
          break;
        }
        case 35:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snRout.txRestrictRx;
          lbFoundOid = true;
          break;
        }
        case 36:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snRout.txAllowTx;
          lbFoundOid = true;
          break;
        }
        case 37:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snRout.txAllowRx;
          lbFoundOid = true;
          break;
        }
        case 38:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snRout.rteUnavCnt;
          lbFoundOid = true;
          break;
        }
        case 39:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snRout.durRteUnav;
          lbFoundOid = true;
          break;
        }
#if (SS7_TTC || SS7_NTT)
        case 40:
        {
          lpOid->stsValue = aeMgmt.t.sts.s.snRout.usnRx;
          lbFoundOid = true;
          break;
        }
#endif
        default:
        {
  				lbFoundOid = false;
          logger.logMsg (ERROR_FLAG, 0, "Unknown Element type <%d> read", 
            lpOid->element);
          break;
        }
      }
    }
  

    //update the Statistics Manager for the OID value
    if (lbFoundOid == true) {
      updateStsMgr (lpOid);
    }
    else {
      logger.logMsg (TRACE_FLAG, 0,
        "OID <%s> has not been mapped", lpOid->oidString);
    }

  }//for loop

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::handleSnRsp");
  return BP_AIN_SM_OK;
}


/******************************************************************************
*
*     Fun:   mtp2GetStatistics()
*
*     Desc:  Get MTP2 layer statistics
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int 
INGwSmStsHdlr::mtp2GetStatistics (int aiBitMap) {

  return BP_AIN_SM_OK;
}



/******************************************************************************
*
*     Fun:   handleSdRsp()
*
*     Desc:  handle MTP2 layer response
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int 
INGwSmStsHdlr::handleSdRsp (SdMngmt &aeMgmt) {

  return BP_AIN_SM_OK;
}

//start
/******************************************************************************
*
*     Fun:   handleMrRsp()
*
*     Desc:  handle Message Router Statistics Response
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int
INGwSmStsHdlr::handleMrRsp (MrMngmt &aeMgmt)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::handleMrRsp");


  //get the current stats level from the repository
  int liLevel = mpDist->getSmRepository()->getStsLevel();

  //set the layer id
  int liLayer = BP_AIN_SM_MR_LAYER;

  //find the operation type
  int liOper;

  if (aeMgmt.hdr.elmId.elmnt == STMRRSET)
  {
    liOper = BP_AIN_SM_SUBTYPE_GENSTS;
  }

  long liHashKey = mpStsMap->getStsHashKey (liLayer, liOper, liLevel);

  if (aeMgmt.cfm.status == LCM_PRIM_NOK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to retrieve statistics <%d>", liHashKey);
    return BP_AIN_SM_FAIL;
  }

  int liRetVal = 0;
  INGwSmStsOidList *lpOidList = mpStsMap->getList (liHashKey, liRetVal);

  if (liRetVal != BP_AIN_SM_OK || lpOidList->empty())
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to retrieve statistics list from Map<%d>", liHashKey);
    return BP_AIN_SM_FAIL;
  }

  INGwSmStsOidList::iterator leOidIter;

  for (leOidIter = lpOidList->begin();
       leOidIter != lpOidList->end();
       leOidIter++)
  {   
    INGwSmStsOid *lpOid = *(leOidIter);

    if (lpOid == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "Oid retrieved from list is NULL");
      continue;
    }

    //this bool will be used to check if element has been mapped or not
    bool lbFoundOid = false;

    //check the element in oid and retrieve from the stack structure
    switch (lpOid->element)
    {
     case 1:
        ;
      break;
     default:
      ;
     
    } 

    //update the Statistics Manager for the OID value
    if (lbFoundOid == true)
      updateStsMgr (lpOid);
    else
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "OID <%s> has not been mapped", lpOid->oidString);
    }
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::handleMrRsp");
  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   handleDnRsp()
*
*     Desc:  handle LDF MTP3 Response
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int
INGwSmStsHdlr::handleDnRsp (LdnMngmt &aeMgmt)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::handleDnRsp");


  //get the current stats level from the repository
  int liLevel = mpDist->getSmRepository()->getStsLevel();

  //set the layer id
  int liLayer = BP_AIN_SM_LDF_MTP3_LAYER;

  //find the operation type
  int liOper;

  if (aeMgmt.hdr.elmId.elmnt == STDNGEN)
  {
    liOper = BP_AIN_SM_SUBTYPE_GENSTS;
  }

  long liHashKey = mpStsMap->getStsHashKey (liLayer, liOper, liLevel);

  if (aeMgmt.cfm.status == LCM_PRIM_NOK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to retrieve statistics <%d>", liHashKey);
    return BP_AIN_SM_FAIL;
  }

  int liRetVal = 0;
  INGwSmStsOidList *lpOidList = mpStsMap->getList (liHashKey, liRetVal);

  if (liRetVal != BP_AIN_SM_OK || lpOidList->empty())
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to retrieve statistics list from Map<%d>", liHashKey);
    return BP_AIN_SM_FAIL;
  }

  INGwSmStsOidList::iterator leOidIter;

  for (leOidIter = lpOidList->begin();
       leOidIter != lpOidList->end();
       leOidIter++)
  {   
    INGwSmStsOid *lpOid = *(leOidIter);

    if (lpOid == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "Oid retrieved from list is NULL");
      continue;
    }

    //this bool will be used to check if element has been mapped or not
    bool lbFoundOid = false;

    //check the element in oid and retrieve from the stack structure
    switch (lpOid->element)
    {
      case 1:
      {
        ;
        break;
      }
            default:
      {
        logger.logMsg (ERROR_FLAG, 0, "Unknown Element type <%d> read",
            lpOid->element);
        break;
      }
    }

    //update the Statistics Manager for the OID value
    if (lbFoundOid == true)
      updateStsMgr (lpOid);
    else
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "OID <%s> has not been mapped", lpOid->oidString);
    }
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::handleDnRsp");
  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   handleDvRsp()
*
*     Desc:  handle LDF M3UA Response
*
*     Notes: None
*
*     File:  INGwSmStsHdlr.C
*
*******************************************************************************/
int
INGwSmStsHdlr::handleDvRsp (LdvMngmt &aeMgmt)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmStsHdlr::handleDvRsp");


  //get the current stats level from the repository
  int liLevel = mpDist->getSmRepository()->getStsLevel();

  //set the layer id
  int liLayer = BP_AIN_SM_LDF_M3UA_LAYER;

  //find the operation type
  int liOper;
  if (aeMgmt.hdr.elmId.elmnt == STDVGEN)
  {
    liOper = BP_AIN_SM_SUBTYPE_GENSTS;
  }

  long liHashKey = mpStsMap->getStsHashKey (liLayer, liOper, liLevel);

  if (aeMgmt.cfm.status == LCM_PRIM_NOK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to retrieve statistics <%d>", liHashKey);
    return BP_AIN_SM_FAIL;
  }

  int liRetVal = 0;
  INGwSmStsOidList *lpOidList = mpStsMap->getList (liHashKey, liRetVal);

  if (liRetVal != BP_AIN_SM_OK || lpOidList->empty())
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to retrieve statistics list from Map<%d>", liHashKey);
    return BP_AIN_SM_FAIL;
  }

  INGwSmStsOidList::iterator leOidIter;

  for (leOidIter = lpOidList->begin();
       leOidIter != lpOidList->end();
       leOidIter++)
  {   
    INGwSmStsOid *lpOid = *(leOidIter);

    if (lpOid == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "Oid retrieved from list is NULL");
      continue;
    }

    //this bool will be used to check if element has been mapped or not
    bool lbFoundOid = false;

    //check the element in oid and retrieve from the stack structure
    switch (lpOid->element)
    {
      case 1:
        ;
      break;
      
      default:
       ;
    } 

    //update the Statistics Manager for the OID value
    if (lbFoundOid == true)
      updateStsMgr (lpOid);
    else
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "OID <%s> has not been mapped", lpOid->oidString);
    }
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmStsHdlr::handleDvRsp");
  return BP_AIN_SM_OK;
}
//end

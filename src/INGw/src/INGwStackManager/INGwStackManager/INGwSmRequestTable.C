//#include <CCMUtil/BpLogger.h>
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwStackManager");

/************************************************************************
   Name:     INAP Stack Manager RequestTable Messages - Impl
 
   Type:     C impl file
 
   Desc:     Implementation of INGwSmRequestTable class

   File:     INGwSmRequestTable.C

   Sid:      INGwSmRequestTable.C 0  -  03/27/03 

   Prg:      gs,bd

************************************************************************/

#include "INGwStackManager/INGwSmRequestTable.h"
#include "INGwStackManager/INGwSmRequest.h"

using namespace std;



/******************************************************************************
*
*     Fun:   INGwSmRequestTable()
*
*     Desc:  Default Contructor
*
*     Notes: None
*
*     File:  INGwSmRequestTable.C
*
*******************************************************************************/
INGwSmRequestTable::INGwSmRequestTable()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmRequestTable::INGwSmRequestTable");

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmRequestTable::INGwSmRequestTable");
}


/******************************************************************************
*
*     Fun:   ~INGwSmRequestTable()
*
*     Desc:  Default Destructor
*
*     Notes: None
*
*     File:  INGwSmRequestTable.C
*
*******************************************************************************/
INGwSmRequestTable::~INGwSmRequestTable()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmRequestTable::~INGwSmRequestTable");

  INGwSmRequestMap::iterator iter;

  //delete all the requests in the Request Table
  for (iter = meReqTable.begin(); iter != meReqTable.end(); iter++)
  {
    delete iter->second;
  }

  meReqTable.clear();

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmRequestTable::~INGwSmRequestTable");
}


/******************************************************************************
*
*     Fun:   initialize()
*
*     Desc:  clear the request table
*
*     Notes: None
*
*     File:  INGwSmRequestTable.C
*
*******************************************************************************/
int
INGwSmRequestTable::initialize ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmRequestTable::initialize");

  meReqTable.clear();

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmRequestTable::initialize");
  return BP_AIN_SM_OK;
}


/******************************************************************************
*
*     Fun:   getRequest()
*
*     Desc:  get the request from the map based on the transaction id
*
*     Notes: None
*
*     File:  INGwSmRequestTable.C
*
*******************************************************************************/
INGwSmRequest* 
INGwSmRequestTable::getRequest(int aiTransId)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmRequestTable::getRequest, txId:<%d>", aiTransId);

  INGwSmRequestMap::iterator iter;

  iter = meReqTable.find (aiTransId);

  if (iter != meReqTable.end())
  {
    logger.logMsg (TRACE_FLAG, 0,
      "Leaving INGwSmRequestTable::getRequest - Request found.");
    return iter->second;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmRequestTable::getRequest - Request not found.");
  return 0;
}


/******************************************************************************
*
*     Fun:   addRequest()
*
*     Desc:  add the request in the map keyed on the transaction id
*
*     Notes: None
*
*     File:  INGwSmRequestTable.C
*
*******************************************************************************/
int
INGwSmRequestTable::addRequest(int aiTransId, INGwSmRequest *apSmRequest)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmRequestTable::addRequest for transId <%d>",aiTransId);

  INGwSmRequest * lSmRequest = getRequest (aiTransId);

  if (!apSmRequest || lSmRequest != 0) {
    logger.logMsg (ERROR_FLAG, 0,
      "Leaving addRequest(): Failed to add Request transId <%d>%s%s",aiTransId, (!apSmRequest)?" apSmRequest is NULL":"", (!lSmRequest)?" lSmRequest is NULL":"");
    return BP_AIN_SM_FAIL;
  }

  meReqTable [aiTransId] = apSmRequest;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmRequestTable::addRequest for transId <%d>",aiTransId);
  return BP_AIN_SM_OK;
}


/******************************************************************************
*
*     Fun:   removeRequest()
*
*     Desc:  remove a SmRequest. apSmRequest would contain the removed
*            request on return.
*
*     Notes: None
*
*     File:  INGwSmRequestTable.C
*
*******************************************************************************/
int
INGwSmRequestTable::removeRequest(int aiTransId, INGwSmRequest *(&apSmRequest))
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmRequestTable::removeRequest for transId <%d>",aiTransId);

  INGwSmRequestMap::iterator iter;

  iter = meReqTable.find (aiTransId);

  if (iter != meReqTable.end())
  {
    apSmRequest = iter->second;
    meReqTable.erase (iter); // not sure why we were incrmenting iter why erasing
    logger.logMsg (TRACE_FLAG, 0,
      "Leaving INGwSmRequestTable::removeRequest for transId <%d>",aiTransId);
    return BP_AIN_SM_OK;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmRequestTable::removeRequest for transId <%d>",aiTransId);
  return BP_AIN_SM_FAIL;
}

/******************************************************************************
*
*     Fun:   dump ()
*     
*     Desc:  dump the request table on the logs
*            
*     Notes: None
*     
*     File:  INGwSmRequestTable.C
*     
*******************************************************************************/
int
INGwSmRequestTable::dump ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmRequestTable::dump");

  logger.logMsg (TRACE_FLAG, 0,
    "Request Table Map is currently servicing <%d> requests",
    meReqTable.size() );

#ifdef _BP_AIN_SM_DMP_
  INGwSmRequestMap::iterator iter;
  
  for (iter = meReqTable.begin(); iter != meReqTable.end(); iter++)
  {
    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> : found in the Request Table Map",
      iter->first);

    INGwSmRequest *lpReq = iter->second;

    lpReq->dump ();
  } 
#endif /* _BP_AIN_SM_DMP_ */
  
  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmRequestTable::dump");
  return BP_AIN_SM_OK;
}


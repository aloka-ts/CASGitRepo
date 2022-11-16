/************************************************************************

     Name:     INAP Stack Manager Request Handler - Impl
 
     Type:     Implementation file
 
     Desc:     Request Handler Implementation

     File:     INGwSmReqHdlr.C

     Sid:      INGwSmReqHdlr.C 0  -  03/27/03 

     Prg:      gs

************************************************************************/

#include "INGwSmReqHdlr.h"

using namespace std;



/******************************************************************************
*
*     Fun:   INGwSmReqHdlr()
*
*     Desc:  Default Contructor
*
*     Notes: None
*
*     File:  INGwSmReqHdlr.C
*
*******************************************************************************/
INGwSmReqHdlr::INGwSmReqHdlr(INGwSmDistributor &aeDist, int aiLayer) :
miLayer (aiLayer),
mrDist (aeDist)
{
  meReqContext.pspId = 0;
  //zv = NULL;
}

/******************************************************************************
*
*     Fun:   ~INGwSmReqHdlr()
*
*     Desc:  Default Destructor
*
*     Notes: None
*
*     File:  INGwSmReqHdlr.C
*
*******************************************************************************/
INGwSmReqHdlr::~INGwSmReqHdlr() {} 

/******************************************************************************
*
*     Fun:   sendRequest()
*
*     Desc:  send request to the stack
*
*     Notes: None
*
*     File:  INGwSmReqHdlr.C
*
*******************************************************************************/
int 
INGwSmReqHdlr::sendRequest(INGwSmQueueMsg *apMsg,INGwSmRequestContext *apContext)
{
  return BP_AIN_SM_OK;
} 

/******************************************************************************
*
*     Fun:   handleResponse ()
*
*     Desc:  handle response from  the stack
*
*     Notes: None
*
*     File:  INGwSmReqHdlr.C
*
*******************************************************************************/
int
INGwSmReqHdlr::handleResponse (INGwSmQueueMsg *apMsg)
{
  return BP_AIN_SM_OK;
}

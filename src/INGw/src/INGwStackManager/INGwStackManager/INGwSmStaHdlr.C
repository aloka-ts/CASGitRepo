//#include <CCMUtil/BpLogger.h>
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwStackManager");

/************************************************************************
   Name:     INAP Stack Manager Status Handler - impl
 
   Type:     C imlp file
 
   Desc:     Implemantation of Status Handler

   File:     INGwSmStaHdlr.C

   Sid:      INGwSmStaHdlr.C 0  -  04/28/03 

   Prg:      gs

************************************************************************/

#include "INGwStackManager/INGwSmStaHdlr.h"
#include "INGwStackManager/INGwSmRepository.h"

using namespace std;

extern void fillHdr(Header *hdr,U32 miTransId,U8 entId, U8 instId,U8 msgType,S16 elmntId,S16 elInst1Id);

/******************************************************************************
*
*     Fun:   INGwSmStaHdlr()
*
*     Desc:  default Contructor
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
INGwSmStaHdlr::INGwSmStaHdlr(INGwSmDistributor& arDist, int aiLayer,
               int aiOper, int aiSubOp, int aiTransId):
INGwSmReqHdlr (arDist, aiLayer),
miOper (aiOper),
miSubOp (aiSubOp),
miIndex (-1)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Entering INGwSmStaHdlr::INGwSmStaHdlr", aiTransId);

  miTransId = aiTransId;
  mpRep = arDist.getSmRepository ();

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Leaving INGwSmStaHdlr::INGwSmStaHdlr", miTransId);
}


/******************************************************************************
*
*     Fun:   ~INGwSmStaHdlr()
*
*     Desc:  default Destructor
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
INGwSmStaHdlr::~INGwSmStaHdlr()
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Entering INGwSmStaHdlr::~INGwSmStaHdlr", miTransId);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Leaving INGwSmStaHdlr::~INGwSmStaHdlr", miTransId);
}


/******************************************************************************
*
*     Fun:   sendRequest()
*
*     Desc:  sends the request to the Stack
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
int
INGwSmStaHdlr::sendRequest(INGwSmQueueMsg *apMsg,INGwSmRequestContext *apContext)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Entering INGwSmStaHdlr::sendRequest", miTransId);

  //check the layer and then invoke the corres. operation
  switch (miSubOp)
  {
    case BP_AIN_SM_SUBTYPE_SYSID:
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "TID <%d>: System Id Status request for Layer <%d>",
        miTransId, miLayer);

      if (getSystemId (miLayer) != BP_AIN_SM_OK)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d>: Request for system Id failed for layer <%d>",
          miTransId, miLayer);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_SAPSTA:
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "TID <%d>: SAP Status request for Layer <%d>",
        miTransId, miLayer);
      break;
    }
    case BP_AIN_SM_SUBTYPE_RTESTA:
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "TID <%d>: Route Status request for Layer <%d>",
        miTransId, miLayer);
      break;
    }
    case BP_AIN_SM_SUBTYPE_LYRSTA:
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "TID <%d>: Layer Status request for Layer <%d>",
        miTransId, miLayer);
      break;
    }
    case BP_AIN_SM_SUBTYPE_ATTSTA:
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "TID <%d>: Address Translation Table Status request for Layer <%d>",
        miTransId, miLayer);
      break;
    }
    case BP_AIN_SM_SUBTYPE_PSSTA:
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "TID <%d>: PS Status request for Layer <%d>",
        miTransId, miLayer);
      break;
    }
    case BP_AIN_SM_SUBTYPE_PSPSTA:
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "TID <%d>: PSP Status request for Layer <%d>",
        miTransId, miLayer);

      int liPspId = mpRep->getPspId (apContext, miIndex);

      /*
       * PSP Id = 0 means no more PSPs remaining
       * PSP Id = -1 means error occurred
       */
      if (liPspId == -1)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d>: No PSP Id Configured", miTransId);
        return BP_AIN_SM_FAIL;
      }
      else if (liPspId == 0)
      {
        logger.logMsg (VERBOSE_FLAG, 0,
          "TID <%d>: PSP list is over. return INDEX_OVER", miTransId);
        return BP_AIN_SM_INDEX_OVER;
      }

      /*
       * Check the PSP State and send request only if in
       * proper state
       */

      int liRetVal = m3uaGetRemoteSignalProcessStatus (liPspId);

      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d>: Unable to get PSP State", miTransId);
        return BP_AIN_SM_FAIL;
      }


      miIndex++;

      break;
    }
    case BP_AIN_SM_SUBTYPE_DRKSTA:
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "TID <%d>: DRK Status request for Layer <%d>",
        miTransId, miLayer);
      break;
    }
    case BP_AIN_SM_SUBTYPE_ASSSTA:
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "TID <%d>: Association Status request for Layer <%d>",
        miTransId, miLayer);
      break;
    }
    case BP_AIN_SM_SUBTYPE_DSTSTA:
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "TID <%d>: Destination Transport Status request for Layer <%d>",
        miTransId, miLayer);
      break;
    }
    case BP_AIN_SM_SUBTYPE_STA_LINK:
    {
      if(miLayer == BP_AIN_SM_MTP3_LAYER)
      {
        logger.logMsg (VERBOSE_FLAG, 0,
        "TID <%d>: LINK Status request for Layer BP_AIN_SM_MTP3_LAYER",
        miTransId);

        if (mtp3LnkStatus (&(apMsg->t.stackData)) != BP_AIN_SM_OK)
        {
          logger.logMsg (ERROR_FLAG, 0,
          "TID <%d>: Request for Link Status failed for layer BP_AIN_SM_MTP3_LAYER",
          miTransId);
          return BP_AIN_SM_FAIL;
        }
      }
      else
      {
        logger.logMsg (VERBOSE_FLAG, 0,
        "TID <%d>: LINK Status request for Layer BP_AIN_SM_MTP2_LAYER",
        miTransId);
  
        if (mtp2LnkStatus (&(apMsg->t.stackData)) != BP_AIN_SM_OK)
        {
          logger.logMsg (ERROR_FLAG, 0,
          "TID <%d>: Request for Link Status failed for layer BP_AIN_SM_MTP2_LAYER",
          miTransId);
          return BP_AIN_SM_FAIL;
        }
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_STA_LINKSET:
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "TID <%d>: LINK SET Status request for Layer BP_AIN_SM_MTP3_LAYER",
        miTransId);

      //sleep(40);
      if (mtp3LinkSetStatus (&(apMsg->t.stackData)) != BP_AIN_SM_OK)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d>: Request for Link Set Status failed for layer BP_AIN_SM_MTP3_LAYER",
          miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_STA_ROUTE:
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "TID <%d>: Route (DPC) Status request for Layer BP_AIN_SM_SCC_LAYER",
        miTransId);

      if (sccpRouteStatus (&(apMsg->t.stackData)) != BP_AIN_SM_OK)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d>: Request for Route (DPC) Status failed for layer BP_AIN_SM_SCC_LAYER",
          miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_STA_PS:
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "TID <%d>: PS Status request for Layer BP_AIN_SM_M3U_LAYER",
        miTransId);

      if (m3uaPsStatus (&(apMsg->t.stackData)) != BP_AIN_SM_OK)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d>: Request for PS Status failed for layer BP_AIN_SM_M3U_LAYER",
          miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_STA_PSP:
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "TID <%d>: PSP Status request for Layer BP_AIN_SM_M3U_LAYER",
        miTransId);

      if (m3uaPspStatus (&(apMsg->t.stackData)) != BP_AIN_SM_OK)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d>: Request for PSP Status failed for layer BP_AIN_SM_M3U_LAYER",
          miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_STA_NODE:
    {
      logger.logMsg (VERBOSE_FLAG, 0,
        "TID <%d>: NODE Status request for Layer ",
        miTransId);

      if (getNodeStatus (&(apMsg->t.stackData)) != BP_AIN_SM_OK)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d>: Request for Node Status failed for layer ",
          miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    } 



    default :
    {
      logger.logMsg (ERROR_FLAG, 0,
        "TID <%d>: Unkown Control Message invoked <%d>",
        miTransId, miSubOp);
      return BP_AIN_SM_FAIL;
    }
  }

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Leaving INGwSmStaHdlr::sendRequest", miTransId);

  return BP_AIN_SM_OK;
}

        
/******************************************************************************
*
*     Fun:   handleResponse()
*     
*     Desc:  handle the response from the stack
*     
*     Notes: None
*   
*     File:  INGwSmStaHdlr.C
*     
*******************************************************************************/
int     
INGwSmStaHdlr::handleResponse (INGwSmQueueMsg *apMsg)
{   
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Entering INGwSmStaHdlr::handleResponse", miTransId);


  //check the layer and then invoke the corres. operation
  switch (miSubOp)
  {
    case BP_AIN_SM_SUBTYPE_SYSID:
    {
      logger.logMsg (TRACE_FLAG, 0,
        "TID <%d>: System Id Status response for Layer <%d>",
        miTransId, miLayer);


      INGwSmStackMgmtInfo *lpResp = &(apMsg->t.stackMsg.stkMsg);

      switch (lpResp->miLayerId)
      {
#if 0
        case BP_AIN_SM_AIN_LAYER:
        {
          SystemId *lpSys = &(lpResp->lyr.ie.t.ssta.s.sysId);

          char *ptNmb;
          if (lpSys->ptNmb == 0)
            ptNmb = (char*) "NULL";
          else
            ptNmb = lpSys->ptNmb;

          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d>: Layer Status <%d> : [%d, %d, %d, %d, %s]",
            miTransId, lpResp->miLayerId, lpSys->mVer, lpSys->mRev, lpSys->bVer,
            lpSys->bRev, ptNmb);

          fprintf (stdout, "\nLayer Status <%d> : [%d, %d, %d, %d, %s]\n",
            lpResp->miLayerId, lpSys->mVer, lpSys->mRev, lpSys->bVer,
            lpSys->bRev, ptNmb);

          //release the memory for the part number
          if (lpSys->ptNmb)
            delete [] lpSys->ptNmb;

          break;
        }
#endif
        case BP_AIN_SM_TCA_LAYER:
        {
          SystemId *lpSys = &(lpResp->lyr.st.t.ssta.s.sysId);

          char *ptNmb;
          if (lpSys->ptNmb == 0)
            ptNmb = (char*) "NULL";
          else
            ptNmb = lpSys->ptNmb;

          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d>: Layer Status <%d> : [%d, %d, %d, %d, %s]",
            miTransId, lpResp->miLayerId, lpSys->mVer, lpSys->mRev, lpSys->bVer,
            lpSys->bRev, ptNmb);

          fprintf (stdout, "\nLayer Status <%d> : [%d, %d, %d, %d, %s]\n",
            lpResp->miLayerId, lpSys->mVer, lpSys->mRev, lpSys->bVer,
            lpSys->bRev, ptNmb);

          //release the memory for the part number
          if (lpSys->ptNmb)
            delete [] lpSys->ptNmb;

          break;
        }
        case BP_AIN_SM_SCC_LAYER:
        {
          SystemId *lpSys = &(lpResp->lyr.sp.t.ssta.s.sysId);

          char *ptNmb;
          if (lpSys->ptNmb == 0)
            ptNmb = (char*) "NULL";
          else
            ptNmb = lpSys->ptNmb;

          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d>: Layer Status <%d> : [%d, %d, %d, %d, %s]",
            miTransId, lpResp->miLayerId, lpSys->mVer, lpSys->mRev, lpSys->bVer,
            lpSys->bRev, ptNmb);

          fprintf (stdout, "\nLayer Status <%d> : [%d, %d, %d, %d, %s]\n",
            lpResp->miLayerId, lpSys->mVer, lpSys->mRev, lpSys->bVer,
            lpSys->bRev, ptNmb);

          //release the memory for the part number
          if (lpSys->ptNmb)
            delete [] lpSys->ptNmb;

          break;
        }
        case BP_AIN_SM_M3U_LAYER:
        {
          SystemId *lpSys = &(lpResp->lyr.it.t.ssta.s.sysId);

          char *ptNmb;
          if (lpSys->ptNmb == 0)
            ptNmb = (char*) "NULL";
          else
            ptNmb = lpSys->ptNmb;

          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d>: Layer Status <%d> : [%d, %d, %d, %d, %s]",
            miTransId, lpResp->miLayerId, lpSys->mVer, lpSys->mRev, lpSys->bVer,
            lpSys->bRev, ptNmb);

          fprintf (stdout, "\nLayer Status <%d> : [%d, %d, %d, %d, %s]\n",
            lpResp->miLayerId, lpSys->mVer, lpSys->mRev, lpSys->bVer,
            lpSys->bRev, ptNmb);

          //release the memory for the part number
          if (lpSys->ptNmb)
            delete [] lpSys->ptNmb;

          break;
        }
        case BP_AIN_SM_SCT_LAYER:
        {
          SystemId *lpSys = &(lpResp->lyr.sb.t.ssta.s.sysId);

          char *ptNmb;
          if (lpSys->ptNmb == 0)
            ptNmb = (char*) "NULL";
          else
            ptNmb = lpSys->ptNmb;

          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d>: Layer Status <%d> : [%d, %d, %d, %d, %s]",
            miTransId, lpResp->miLayerId, lpSys->mVer, lpSys->mRev, lpSys->bVer,
            lpSys->bRev, ptNmb);

          fprintf (stdout, "\nLayer Status <%d> : [%d, %d, %d, %d, %s]\n",
            lpResp->miLayerId, lpSys->mVer, lpSys->mRev, lpSys->bVer,
            lpSys->bRev, ptNmb);

          //release the memory for the part number
          if (lpSys->ptNmb)
            delete [] lpSys->ptNmb;

          break;
        }
        case BP_AIN_SM_TUC_LAYER:
        {
          SystemId *lpSys = &(lpResp->lyr.hi.t.ssta.s.sysId);

          char *ptNmb;
          if (lpSys->ptNmb == 0)
            ptNmb = (char*) "NULL";
          else
            ptNmb = lpSys->ptNmb;

          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d>: Layer Status <%d> : [%d, %d, %d, %d, %s]",
            miTransId, lpResp->miLayerId, lpSys->mVer, lpSys->mRev, lpSys->bVer,
            lpSys->bRev, ptNmb);

          fprintf (stdout, "\nLayer Status <%d> : [%d, %d, %d, %d, %s]\n",
            lpResp->miLayerId, lpSys->mVer, lpSys->mRev, lpSys->bVer,
            lpSys->bRev, ptNmb);

          //release the memory for the part number
          if (lpSys->ptNmb)
            delete [] lpSys->ptNmb;

          break;
        }
        case BP_AIN_SM_MTP3_LAYER:
        {
          SystemId *lpSys = &(lpResp->lyr.sn.t.ssta.s.sysId);

          char *ptNmb;
          if (lpSys->ptNmb == 0)
            ptNmb = (char*) "NULL";
          else
            ptNmb = lpSys->ptNmb;

          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d>: Layer Status <%d> : [%d, %d, %d, %d, %s]",
            miTransId, lpResp->miLayerId, lpSys->mVer, lpSys->mRev, lpSys->bVer,
            lpSys->bRev, ptNmb);

          fprintf (stdout, "\nLayer Status <%d> : [%d, %d, %d, %d, %s]\n",
            lpResp->miLayerId, lpSys->mVer, lpSys->mRev, lpSys->bVer,
            lpSys->bRev, ptNmb);

          //release the memory for the part number
          if (lpSys->ptNmb)
            delete [] lpSys->ptNmb;

          break;
        }
        case BP_AIN_SM_MTP2_LAYER:
        {
          SystemId *lpSys = &(lpResp->lyr.sd.t.ssta.s.sysId);

          char *ptNmb;
          if (lpSys->ptNmb == 0)
            ptNmb = (char*) "NULL";
          else
            ptNmb = lpSys->ptNmb;

          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d>: Layer Status <%d> : [%d, %d, %d, %d, %s]",
            miTransId, lpResp->miLayerId, lpSys->mVer, lpSys->mRev, lpSys->bVer,
            lpSys->bRev, ptNmb);

          fprintf (stdout, "\nLayer Status <%d> : [%d, %d, %d, %d, %s]\n",
            lpResp->miLayerId, lpSys->mVer, lpSys->mRev, lpSys->bVer,
            lpSys->bRev, ptNmb);

          //release the memory for the part number
          if (lpSys->ptNmb)
            delete [] lpSys->ptNmb;

          break;
        }


        default:
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d>: Response for System id received for unknown Layer Status <%d>",
            miTransId, lpResp->miLayerId);
          return BP_AIN_SM_FAIL;
          break;
        }
      }
        
      break;
    }
    case BP_AIN_SM_SUBTYPE_SAPSTA:
    {
      logger.logMsg (TRACE_FLAG, 0,
        "TID <%d>: SAP Status response for Layer <%d>",
        miTransId, miLayer);
      break;
    }
    case BP_AIN_SM_SUBTYPE_RTESTA:
    {
      logger.logMsg (TRACE_FLAG, 0,
        "TID <%d>: Route Status response for Layer <%d>",
        miTransId, miLayer);
      break;
    }
    case BP_AIN_SM_SUBTYPE_LYRSTA:
    {
      logger.logMsg (TRACE_FLAG, 0,
        "TID <%d>: Layer Status response for Layer <%d>",
        miTransId, miLayer);
      break;
    }
    case BP_AIN_SM_SUBTYPE_ATTSTA:
    {
      logger.logMsg (TRACE_FLAG, 0,
        "TID <%d>: Address Translation Table Status response for Layer <%d>",
        miTransId, miLayer);
      break;
    }
    case BP_AIN_SM_SUBTYPE_PSSTA:
    {
      logger.logMsg (TRACE_FLAG, 0,
        "TID <%d>: PS Status response for Layer <%d>",
        miTransId, miLayer);
      break;
    }
    case BP_AIN_SM_SUBTYPE_PSPSTA:
    {
      logger.logMsg (TRACE_FLAG, 0,
        "TID <%d>: PSP Status response for Layer <%d>",
        miTransId, miLayer);

      if (apMsg->t.stackMsg.stkMsg.miLayerId != miLayer)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d>: <%d> Layer response received instead of <%d> Layer",
          miTransId, apMsg->t.stackMsg.stkMsg.miLayerId, miLayer);

        return BP_AIN_SM_FAIL;
      }

      ItPspSta *lpSta = &(apMsg->t.stackMsg.stkMsg.lyr.it.t.ssta.s.pspSta);

      //dump the status received

#ifdef _BP_AIN_SM_DMP_

      logger.logMsg (TRACE_FLAG, 0,
        "TID <%d>: PSP Status received for state <%d>, inhibited <%d>",
        miTransId, lpSta->assocSta[0].aspSt, lpSta->assocSta[0].inhibited);

      for (int count = 0; count < lpSta->assocSta[0].nmbAct; ++count)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d>: PSP Status received for Active PS[%d] = <%d>",
          miTransId, count, lpSta->assocSta[0].actPs[count]);
      }

      for (int count = 0; count < lpSta->assocSta[0].nmbRegPs; ++count)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d>: PSP Status received for Registered PS[%d] = <%d>",
          miTransId, count, lpSta->assocSta[0].regPs[count]);
      }

      logger.logMsg (TRACE_FLAG, 0,
        "TID <%d>: PSP Status received for Association Id <%d>, Association State <%d>",
        miTransId, lpSta->assocSta[0].spAssocId, lpSta->assocSta[0].hlSt);

#endif /* _BP_AIN_SM_DMP_ */

      //this is the PSP Status so just update the current PSP Status
      //with the stack status in order to sync them.

      int liPspState;

      if (lpSta->assocSta[0].aspSt == LIT_ASP_DOWN)
      {
        if (lpSta->assocSta[0].hlSt != LIT_ASSOC_DOWN)
          liPspState = BP_AIN_SM_PSP_ST_ESTASS;
        else
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d>: PSP <%d> : has gone down ", miTransId, lpSta->pspId);

          liPspState = BP_AIN_SM_PSP_ST_DOWN;
        }
      }

      else if (lpSta->assocSta[0].aspSt == LIT_ASP_INACTIVE)
      {
        if (lpSta->assocSta[0].hlSt != LIT_ASSOC_DOWN)
          liPspState = BP_AIN_SM_PSP_ST_ASPUP;
        else 
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d>: PSP <%d> : has gone down ", miTransId, lpSta->pspId);

          liPspState = BP_AIN_SM_PSP_ST_DOWN;
        }

      }

      else if (lpSta->assocSta[0].aspSt == LIT_ASP_ACTIVE)
      {
        if (lpSta->assocSta[0].hlSt != LIT_ASSOC_DOWN)
          liPspState = BP_AIN_SM_PSP_ST_ASPAC;
        else 
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d>: PSP <%d> : has gone down ", miTransId, lpSta->pspId);

          liPspState = BP_AIN_SM_PSP_ST_DOWN;
        }

      }

      else
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d>: Uknown ASP State <%d> for PSP <%d>",
          miTransId, lpSta->assocSta[0].aspSt, lpSta->pspId);

        return BP_AIN_SM_FAIL;
      }


      mpRep->setPspState (lpSta->pspId, liPspState);

      break;
    }
    case BP_AIN_SM_SUBTYPE_DRKSTA:
    {
      logger.logMsg (TRACE_FLAG, 0,
        "TID <%d>: DRK Status response for Layer <%d>",
        miTransId, miLayer);
      break;
    }
    case BP_AIN_SM_SUBTYPE_ASSSTA:
    {
      logger.logMsg (TRACE_FLAG, 0,
        "TID <%d>: Association Status response for Layer <%d>",
        miTransId, miLayer);
      break;
    }
    case BP_AIN_SM_SUBTYPE_DSTSTA:
    {
      logger.logMsg (TRACE_FLAG, 0,
        "TID <%d>: Destination Transport Status response for Layer <%d>",
        miTransId, miLayer);
      break;
    }
    default :
    {
      logger.logMsg (ERROR_FLAG, 0,
        "TID <%d>: Unknown Control Response received <%d>",
        miTransId, miSubOp);
      return BP_AIN_SM_FAIL;
    }
  }

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Leaving INGwSmStaHdlr::handleResponse", miTransId);

  return BP_AIN_SM_OK;
}

/*
 * Available for All Layers
 */

/******************************************************************************
*
*     Fun:   getSystemId()
*
*     Desc:  get the system id for the layer
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
int
INGwSmStaHdlr::getSystemId (int aiLayerId)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Entering INGwSmStaHdlr::getSystemId <%d>", miTransId, aiLayerId);

  switch (aiLayerId)
  {
#if 0
    case BP_AIN_SM_AIN_LAYER:
    {
      IeMngmt &sta = l.ie;
      (Void) cmMemset((U8 *)&sta, 0, sizeof(IeMngmt));

      sta.hdr.response.selector = BP_AIN_SM_COUPLING;
      sta.hdr.response.prior = BP_AIN_SM_PRIOR;
      sta.hdr.response.route = BP_AIN_SM_ROUTE;
      sta.hdr.response.mem.region  = BP_AIN_SM_REGION;
      sta.hdr.response.mem.pool = BP_AIN_SM_POOL;
      sta.hdr.transId = miTransId;

      sta.hdr.msgType = TSSTA;
      /* set configuration parameters */


      sta.hdr.entId.ent = ENTIE;
      sta.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      sta.hdr.elmId.elmnt = STSID;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_AIN_LAYER);
      lmPst->event = LIE_EVTSTAREQ;

      smMiLieStaReq(lmPst, &sta);

      break;
    }
#endif
    case BP_AIN_SM_TCA_LAYER:
    {
      StMngmt &sta = l.st;
      (Void) cmMemset((U8 *)&sta, 0, sizeof(StMngmt));

      sta.hdr.response.selector = BP_AIN_SM_COUPLING;
      sta.hdr.response.prior = BP_AIN_SM_PRIOR;
      sta.hdr.response.route = BP_AIN_SM_ROUTE;
      sta.hdr.response.mem.region  = BP_AIN_SM_REGION;
      sta.hdr.response.mem.pool = BP_AIN_SM_POOL;
      sta.hdr.transId = miTransId;

      sta.hdr.msgType = TSSTA;
      /* set configuration parameters */


      sta.hdr.entId.ent = ENTST;
      sta.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      sta.hdr.elmId.elmnt = STSID;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_TCA_LAYER);
      lmPst->event = EVTLSTSTAREQ;

      smMiLstStaReq(lmPst, &sta);

      break;
    }
    case BP_AIN_SM_SCC_LAYER:
    {
      SpMngmt &sta = l.sp;
      (Void) cmMemset((U8 *)&sta, 0, sizeof(SpMngmt));
      
      sta.hdr.response.selector = BP_AIN_SM_COUPLING;
      sta.hdr.response.prior = BP_AIN_SM_PRIOR;
      sta.hdr.response.route = BP_AIN_SM_ROUTE;
      sta.hdr.response.mem.region  = BP_AIN_SM_REGION;
      sta.hdr.response.mem.pool = BP_AIN_SM_POOL;
      sta.hdr.transId = miTransId;

      sta.hdr.msgType = TSSTA;
      /* set configuration parameters */


      sta.hdr.entId.ent = ENTSP;
      sta.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      sta.hdr.elmId.elmnt = STSID;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCC_LAYER);
      lmPst->event = EVTLSPSTAREQ;

      smMiLspStaReq(lmPst, &sta);

      break;
    }
    case BP_AIN_SM_M3U_LAYER:
    {
      ItMgmt &sta = l.it;
      (Void) cmMemset((U8 *)&sta, 0, sizeof(ItMgmt));
      
      sta.hdr.response.selector = BP_AIN_SM_COUPLING;
      sta.hdr.response.prior = BP_AIN_SM_PRIOR;
      sta.hdr.response.route = BP_AIN_SM_ROUTE;
      sta.hdr.response.mem.region  = BP_AIN_SM_REGION;
      sta.hdr.response.mem.pool = BP_AIN_SM_POOL;
      sta.hdr.transId = miTransId;

      sta.hdr.msgType = TSSTA;
      /* set configuration parameters */


      sta.hdr.entId.ent = ENTIT;
      sta.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      sta.hdr.elmId.elmnt = STITSID;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
      lmPst->event = EVTLITSTAREQ;

      smMiLitStaReq(lmPst, &sta);

      break;
    }
    case BP_AIN_SM_SCT_LAYER:
    {
      SbMgmt &sta = l.sb;
      (Void) cmMemset((U8 *)&sta, 0, sizeof(SbMgmt));
      
      sta.hdr.response.selector = BP_AIN_SM_COUPLING;
      sta.hdr.response.prior = BP_AIN_SM_PRIOR;
      sta.hdr.response.route = BP_AIN_SM_ROUTE;
      sta.hdr.response.mem.region  = BP_AIN_SM_REGION;
      sta.hdr.response.mem.pool = BP_AIN_SM_POOL;
      sta.hdr.transId = miTransId;

      sta.hdr.msgType = TSSTA;
      /* set configuration parameters */


      sta.hdr.entId.ent = ENTSB;
      sta.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      sta.hdr.elmId.elmnt = STSBSID;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCT_LAYER);
      lmPst->event = LSB_EVTSTAREQ;

      smMiLsbStaReq(lmPst, &sta);

      break;
    }
    case BP_AIN_SM_TUC_LAYER:
    {
      HiMngmt &sta = l.hi;
      (Void) cmMemset((U8 *)&sta, 0, sizeof(HiMngmt));

      sta.hdr.response.selector = BP_AIN_SM_COUPLING;
      sta.hdr.response.prior = BP_AIN_SM_PRIOR;
      sta.hdr.response.route = BP_AIN_SM_ROUTE;
      sta.hdr.response.mem.region  = BP_AIN_SM_REGION;
      sta.hdr.response.mem.pool = BP_AIN_SM_POOL;
      sta.hdr.transId = miTransId;

      sta.hdr.msgType = TSSTA;
      /* set configuration parameters */


      sta.hdr.entId.ent = ENTHI;
      sta.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      sta.hdr.elmId.elmnt = STSID;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_TUC_LAYER);
      lmPst->event = EVTLHISTAREQ;

      smMiLhiStaReq(lmPst, &sta);

      break;
    }
    default:
    {
      logger.logMsg (TRACE_FLAG, 0,
        "TID <%d>: Invalid Layer passed <%d>", miLayer);
      return BP_AIN_SM_FAIL;
    }
  }

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Leaving INGwSmStaHdlr::getSystemId", miTransId);
  return BP_AIN_SM_OK;
}


/******************************************************************************
*
*     Fun:   getSapStatus()
*
*     Desc:  get the SAP Status for a layer and sap
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
int
INGwSmStaHdlr::getSapStatus (int aiSapType , int aiLayerId)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Entering INGwSmStaHdlr::getSapStatus", miTransId);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Leaving INGwSmStaHdlr::getSapStatus", miTransId);

  return BP_AIN_SM_FAIL;
}


/*
 * SCCP Layer Status
 */

/******************************************************************************
*
*     Fun:   sccpGetRouteStatus()
*
*     Desc:  Get the Route Status for a particular route
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
int
INGwSmStaHdlr::sccpGetRouteStatus (int aiNetworkSapId, int aiPointCode)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Entering INGwSmStaHdlr::sccpGetRouteStatus", miTransId);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Leaving INGwSmStaHdlr::sccpGetRouteStatus", miTransId);

  return BP_AIN_SM_FAIL;
}


/*
 * M3UA Layer Status
 */

/******************************************************************************
*
*     Fun:   m3uaGetStatus()
*
*     Desc:  Get the status of the M3UA layer
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
int
INGwSmStaHdlr::m3uaGetStatus ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Entering INGwSmStaHdlr::m3uaGetStatus", miTransId);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Leaving INGwSmStaHdlr::m3uaGetStatus", miTransId);

  return BP_AIN_SM_FAIL;
}


/******************************************************************************
*
*     Fun:   m3uaGetAddressTranslationTableStatus()
*
*     Desc:  Get the Address Translation Table Status
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
int
INGwSmStaHdlr::m3uaGetAddressTranslationTableStatus ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Entering INGwSmStaHdlr::m3uaGetAddressTranslationTableStatus", miTransId);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Leaving INGwSmStaHdlr::m3uaGetAddressTranslationTableStatus", miTransId);

  return BP_AIN_SM_FAIL;
}


/******************************************************************************
*
*     Fun:   m3uaGetPeerServerStatus()
*
*     Desc:  Get the Peer Server Status
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
int
INGwSmStaHdlr::m3uaGetPeerServerStatus (int aiPsId)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Entering INGwSmStaHdlr::m3uaGetPeerServerStatus", miTransId);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Leaving INGwSmStaHdlr::m3uaGetPeerServerStatus", miTransId);

  return BP_AIN_SM_FAIL;
}


/******************************************************************************
*
*     Fun:   m3uaGetRemoteSignalProcessStatus()
*
*     Desc:  Get the Remote Signaling Process Status
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
int
INGwSmStaHdlr::m3uaGetRemoteSignalProcessStatus (int aiPspId)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Entering INGwSmStaHdlr::m3uaGetRemoteSignalProcessStatus PSP <%d>", aiPspId);

  if (miLayer != BP_AIN_SM_M3U_LAYER)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "TID <%d>: This operation is only permitted for M3UA Layer", miTransId);
    return BP_AIN_SM_FAIL;
  }
 
  ItMgmt &sta = l.it;
  cmMemset((U8 *)&sta, 0, sizeof(ItMgmt));

  sta.hdr.response.selector = BP_AIN_SM_COUPLING;
  sta.hdr.response.prior = BP_AIN_SM_PRIOR;
  sta.hdr.response.route = BP_AIN_SM_ROUTE;
  sta.hdr.response.mem.region  = BP_AIN_SM_REGION;
  sta.hdr.response.mem.pool = BP_AIN_SM_POOL;

  sta.hdr.msgType = TSSTA;
  sta.hdr.transId = miTransId;
  /* set configuration parameters */
  sta.hdr.elmId.elmnt = STITPSP;
  sta.hdr.entId.ent = ENTIT;
  sta.hdr.entId.inst = BP_AIN_SM_SRC_INST;
  sta.t.ssta.s.pspSta.pspId = aiPspId;

  Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
  lmPst->event = EVTLITSTAREQ;

  smMiLitStaReq(lmPst, &sta);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Leaving INGwSmStaHdlr::m3uaGetRemoteSignalProcessStatus", miTransId);

  return BP_AIN_SM_OK;
}


/******************************************************************************
*
*     Fun:   m3uaGetDRKMStatus ()
*
*     Desc:  Get the status of DRKM
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
int
INGwSmStaHdlr::m3uaGetDRKMStatus ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Entering INGwSmStaHdlr::m3uaGetDRKMStatus", miTransId);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Leaving INGwSmStaHdlr::m3uaGetDRKMStatus", miTransId);

  return BP_AIN_SM_FAIL;
}


/******************************************************************************
*
*     Fun:   m3uaGetDRKMReqStatus ()
*
*     Desc:  Get the status of DRKM Request sent to server PSP
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
int
INGwSmStaHdlr::m3uaGetDRKMReqStatus (int aiPspId)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Entering INGwSmStaHdlr::m3uaGetDRKMReqStatus", miTransId);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Leaving INGwSmStaHdlr::m3uaGetDRKMReqStatus", miTransId);

  return BP_AIN_SM_FAIL;
}


/*
 * SCTP Layer Status
 */

/******************************************************************************
*
*     Fun:   sctpGetStatus()
*
*     Desc:  Get the SCTP Layer Status
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
int
INGwSmStaHdlr::sctpGetStatus ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Entering INGwSmStaHdlr::sctpGetStatus", miTransId);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Leaving INGwSmStaHdlr::sctpGetStatus", miTransId);

  return BP_AIN_SM_FAIL;
}


/******************************************************************************
*
*     Fun:   sctpGetAssociationStatus()
*
*     Desc:  Get the Association Status
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
int
INGwSmStaHdlr::sctpGetAssociationStatus (int aiAssociationId)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Entering INGwSmStaHdlr::sctpGetAssociationStatus", miTransId);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Leaving INGwSmStaHdlr::sctpGetAssociationStatus", miTransId);

  return BP_AIN_SM_FAIL;
}


/******************************************************************************
*
*     Fun:   sctpGetDestTransportStatus()
*
*     Desc:  Get the Destination Transport Address Status
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
int
INGwSmStaHdlr::sctpGetDestTransportStatus (INGwSmAddress *apTransportAddress)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Entering INGwSmStaHdlr::sctpGetDestTransportStatus", miTransId);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Leaving INGwSmStaHdlr::sctpGetDestTransportStatus", miTransId);

  return BP_AIN_SM_FAIL;
}

/******************************************************************************
*
*     Fun:   smMiLieStaReq()
*
*     Desc:  send the Status request for INAP Layer
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
#if 0
int
INGwSmStaHdlr::smMiLieStaReq (Pst *pst, IeMngmt *sta)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Entering INGwSmStaHdlr::smMiLieStaReq", miTransId);

  //only Loosely coupled is supported now
  cmPkLieStaReq (pst, sta);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Leaving INGwSmStaHdlr::smMiLieStaReq", miTransId);

  return BP_AIN_SM_OK;
}
#endif

/******************************************************************************
*
*     Fun:   smMiLstStaReq()
*
*     Desc:  send the Status request for TCAP Layer
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
int
INGwSmStaHdlr::smMiLstStaReq (Pst *pst, StMngmt *sta)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Entering INGwSmStaHdlr::smMiLstStaReq", miTransId);

  //only Loosely coupled is supported now
  cmPkLstStaReq (pst, sta);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Leaving INGwSmStaHdlr::smMiLstStaReq", miTransId);

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   smMiLspStaReq()
*
*     Desc:  send the Status request for M3UA Layer
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
int
INGwSmStaHdlr::smMiLspStaReq (Pst *pst, SpMngmt *sta)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Entering INGwSmStaHdlr::smMiLspStaReq", miTransId);

  //only Loosely coupled is supported now
  cmPkLspStaReq (pst, sta);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Leaving INGwSmStaHdlr::smMiLspStaReq", miTransId);

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   smMiLitStaReq()
*
*     Desc:  send the Status request for M3UA Layer
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
int
INGwSmStaHdlr::smMiLitStaReq (Pst *pst, ItMgmt *sta)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Entering INGwSmStaHdlr::smMiLitStaReq", miTransId);

  //only Loosely coupled is supported now
  cmPkLitStaReq (pst, sta);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Leaving INGwSmStaHdlr::smMiLitStaReq", miTransId);

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   smMiLsbStaReq()
*
*     Desc:  send the Status request for SCTP Layer
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
int
INGwSmStaHdlr::smMiLsbStaReq (Pst *pst, SbMgmt *sta)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Entering INGwSmStaHdlr::smMiLsbStaReq", miTransId);

  //only Loosely coupled is supported now
  cmPkLsbStaReq (pst, sta);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Leaving INGwSmStaHdlr::smMiLsbStaReq", miTransId);

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   smMiLhiStaReq()
*
*     Desc:  send the Status request for TUCL Layer
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
int
INGwSmStaHdlr::smMiLhiStaReq (Pst *pst, HiMngmt *sta)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Entering INGwSmStaHdlr::smMiLhiStaReq", miTransId);

  //only Loosely coupled is supported now
  cmPkLhiStaReq (pst, sta);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Leaving INGwSmStaHdlr::smMiLhiStaReq", miTransId);

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   smMiLsnStaReq()
*
*     Desc:  send the Status request for MTP3 Layer
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
int
INGwSmStaHdlr::smMiLsnStaReq (Pst *pst, SnMngmt *cntrl) {

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   smMiLsdStaReq()
*
*     Desc:  send the Status request for MTP2 Layer
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
int
INGwSmStaHdlr::smMiLsdStaReq (Pst *pst, SdMngmt *cntrl) {

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   getNodeStatus()
*
*     Desc:  Status Node request for SG LAyer
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
int INGwSmStaHdlr::getNodeStatus(StackReqResp *stackReq)
{
 logger.logMsg (TRACE_FLAG, 0,"Entering getNodeStatus");
 SgMngmt &cntrl = l.sg;
 cmMemset((U8 *)&cntrl, 0, sizeof(SgMngmt));

    fillHdr(&(l.sg.hdr), miTransId, ENTSG, 0, TSSTA, STSGENT, 0);

 cntrl.apiType = LSG_HI_API;
 cntrl.t.hi.ssta.entId = stackReq->req.u.nodeStatus.entId;
 cntrl.t.hi.ssta.instId = stackReq->req.u.nodeStatus.instId;
 cntrl.t.hi.ssta.procId = stackReq->req.u.nodeStatus.procId;

 Pst *lmPst = mpRep->getPst (BP_AIN_SM_SG_LAYER);
 lmPst->event = EVTLSGSTAREQ;
  lmPst->dstProcId = stackReq->procId;
   lmPst->srcProcId = SFndProcId();

 logger.logMsg (TRACE_FLAG, 0,"getNodeStatus ENT <%d>, InSt <%d>, ProcId <%d>",cntrl.t.hi.ssta.entId,cntrl.t.hi.ssta.instId,cntrl.t.hi.ssta.procId);

 SmMiLsgStaReq(lmPst, &cntrl);

  /* update the response structure */
   stackReq->resp.procId = stackReq->procId;
   stackReq->txnType = NORMAL_TXN;
   stackReq->txnStatus = INPROGRESS;

   mrDist.updateRspStruct(miTransId,stackReq);


 logger.logMsg (TRACE_FLAG, 0,"Leaving  getNodeStatus");

   return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   sccpRouteStatus()
*
*     Desc:  send the Status request for Route to SCCP LAyer
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
/* Function to get Route Status */
int INGwSmStaHdlr::sccpRouteStatus(StackReqResp *stackReq)
{
 logger.logMsg (TRACE_FLAG, 0,"Entering sccpRouteStatus");
 SpMngmt &cntrl = l.sp;
 cmMemset((U8 *)&cntrl, 0, sizeof(SpMngmt));

 fillHdr(&(l.sp.hdr), miTransId, ENTSP, 0, TSSTA, STROUT, 0);

 cntrl.t.ssta.s.spRteSta.pcSta.pc = stackReq->req.u.dpcStatus.dpc;
 cntrl.t.ssta.s.spRteSta.pcSta.nwId = stackReq->req.u.dpcStatus.nwkId;

 Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCC_LAYER);
 lmPst->event = EVTLSPSTAREQ;
 lmPst->srcProcId = SFndProcId();
 lmPst->dstProcId = stackReq->procId;

 SmMiLspStaReq(lmPst, &cntrl);
 /* update the response structure */

 stackReq->resp.procId = stackReq->procId;
 stackReq->txnType = NORMAL_TXN;
 stackReq->txnStatus = INPROGRESS;

 mrDist.updateRspStruct(miTransId,stackReq);

 logger.logMsg (TRACE_FLAG, 0,"Leaving  sccpRouteStatus");
 return BP_AIN_SM_OK;

}

/******************************************************************************
*
*     Fun:   mtp2LnkStatus()
*
*     Desc:  send the Status request for Link to MTP2 LAyer
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
/* Function to get MTP2 Link status */
int INGwSmStaHdlr::mtp2LnkStatus(StackReqResp *stackReq)
{
 logger.logMsg (TRACE_FLAG, 0,"Entering mtp2LnkStatus for sapId <%d>",stackReq->req.u.lnkstatus.mtp2UsapId);
 SdMngmt &cntrl = l.sd;
 cmMemset((U8 *)&cntrl, 0, sizeof(SdMngmt));

 fillHdr(&(l.sd.hdr), miTransId, ENTSD, 0, TSSTA, STDLSAP, stackReq->req.u.lnkstatus.mtp2UsapId);

 Pst *lmPst = mpRep->getPst (BP_AIN_SM_MTP2_LAYER);
 lmPst->event = EVTLSDSTAREQ;
 lmPst->srcProcId = SFndProcId();
 lmPst->dstProcId = stackReq->procId;

 //l.sd.t.ssta.s.sdDLSAP = stackReq->req.u.lnkstatus.mtp2UsapId;

 SmMiLsdStaReq(lmPst, &cntrl);
 /*here we need to update the response structure */
 stackReq->resp.procId = stackReq->procId;
 stackReq->txnType = NORMAL_TXN;
 stackReq->txnStatus = INPROGRESS;

 mrDist.updateRspStruct(miTransId,stackReq);
 
 logger.logMsg (TRACE_FLAG, 0,"Leaving  mtp2LnkStatus");
   return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   mtp3LnkStatus()
*
*     Desc:  send the Status request for Link to MTP3 LAyer
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
/* Function to get MTP3 Link status */
int INGwSmStaHdlr::mtp3LnkStatus(StackReqResp *stackReq)
{
 logger.logMsg (TRACE_FLAG, 0,"Entering mtp3LnkStatus for sapId<%d>",stackReq->req.u.lnkstatus.mtp3LsapId);
 SnMngmt &cntrl = l.sn;
 cmMemset((U8 *)&cntrl, 0, sizeof(SnMngmt));

    fillHdr(&(l.sn.hdr), miTransId, ENTSN, 0, TSSTA, STDLSAP, stackReq->req.u.lnkstatus.mtp3LsapId);
 //cntrl.hdr.elmId.elmntInst2 = 0xff;

 Pst *lmPst = mpRep->getPst (BP_AIN_SM_MTP3_LAYER);
 lmPst->event = EVTLSNSTAREQ;
 lmPst->srcProcId = SFndProcId();
 lmPst->dstProcId = stackReq->procId;

 //SmMiLsnCntrlReq(lmPst, &cntrl);
 SmMiLsnStaReq(lmPst, &cntrl);
 /* update the response structure */

 stackReq->resp.procId = stackReq->procId;
 stackReq->txnType = NORMAL_TXN;
 stackReq->txnStatus = INPROGRESS;

 mrDist.updateRspStruct(miTransId,stackReq);

 logger.logMsg (TRACE_FLAG, 0,"Leaving  mtp3LnkStatus");
 return BP_AIN_SM_OK;
}

/* Function to get Link status */
/*int INGwSmStaHdlr::cliGetLinkStatus(LinkStatus *lnk)
{
 logger.logMsg (TRACE_FLAG, 0,"Entering cliLinkStatus");
 if(lnk->layer == LINK_MTP3)
 {
  mtp3LnkStatus(lnk);
 }
 else
 {
  mtp2LnkStatus(lnk);
 }
 logger.logMsg (TRACE_FLAG, 0,"Leaving  cliLinkStatus");
   return BP_AIN_SM_OK;
}*/

/******************************************************************************
*
*     Fun:   mtp3LinkSetStatus()
*
*     Desc:  send the Status request for LinkSet to MTP3 LAyer
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
/* Function to get LinkStatus */
int INGwSmStaHdlr::mtp3LinkSetStatus(StackReqResp *stackReq)
{
   logger.logMsg (TRACE_FLAG, 0,"Entering mtp3LinkSetStatus");
   SnMngmt &cntrl = l.sn;
   cmMemset((U8 *)&cntrl, 0, sizeof(SnMngmt));

   fillHdr(&(l.sg.hdr), miTransId, ENTSN, 0, TSSTA, STLNKSET, stackReq->req.u.lnkStatus.lnkSet);
   cntrl.hdr.elmId.elmntInst2 = stackReq->req.u.lnkStatus.cmbLnkSetId;

   Pst *lmPst = mpRep->getPst (BP_AIN_SM_MTP3_LAYER);
   lmPst->event = EVTLSNSTAREQ;
   lmPst->srcProcId = SFndProcId();
   lmPst->dstProcId = stackReq->procId;

    logger.logMsg (TRACE_FLAG, 0,"STALINKSET LnkSetId <%d> CmbLinkSetid <%d>", cntrl.hdr.elmId.elmntInst1,cntrl.hdr.elmId.elmntInst2);
   SmMiLsnStaReq(lmPst, &cntrl);

   /* update the response structure */
   stackReq->resp.procId = stackReq->procId;
   stackReq->txnType = NORMAL_TXN;
   stackReq->txnStatus = INPROGRESS;

   mrDist.updateRspStruct(miTransId,stackReq);

   logger.logMsg (TRACE_FLAG, 0,"Leaving  mtp3LinkSetStatus");
   return BP_AIN_SM_OK;

}

/******************************************************************************
*
*     Fun:   m3uaPsStatus()
*
*     Desc:  send the Status request for PS to M3UA LAyer
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
int INGwSmStaHdlr::m3uaPsStatus(StackReqResp *stackReq)
{
   logger.logMsg (TRACE_FLAG, 0,"Entering m3uaPsStatus");
   ItMgmt &cntrl = l.it;
   cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));

   cntrl.t.ssta.s.psSta.psId = stackReq->req.u.ps.psId;

   fillHdr(&(l.it.hdr), miTransId, ENTIT, 0, TSSTA, STITPS , 0);

   Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
   lmPst->event = EVTLITSTAREQ;
   lmPst->dstProcId = stackReq->procId;
   lmPst->srcProcId = SFndProcId();


   SmMiLitStaReq(lmPst, &cntrl);

    /* update the response structure */

   stackReq->resp.procId = stackReq->procId;
   stackReq->txnType = NORMAL_TXN;
   stackReq->txnStatus = INPROGRESS;

   mrDist.updateRspStruct(miTransId,stackReq);

   logger.logMsg (TRACE_FLAG, 0,"Leaving  m3uaPsStatus");
   return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   m3uaPsptatus()
*
*     Desc:  send the Status request for PSP to M3UA LAyer
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/
int INGwSmStaHdlr::m3uaPspStatus(StackReqResp *stackReq)
{
   logger.logMsg (TRACE_FLAG, 0,"Entering m3uaPspStatus");
   ItMgmt &cntrl = l.it;
   cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));

   cntrl.t.ssta.s.pspSta.pspId = stackReq->req.u.psp.pspId;

   fillHdr(&(l.it.hdr), miTransId, ENTIT, 0, TSSTA, STITPSP , 0);

   Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
   lmPst->event = EVTLITSTAREQ;
   lmPst->dstProcId = stackReq->procId;
   lmPst->srcProcId = SFndProcId();

   SmMiLitStaReq(lmPst, &cntrl);

   /* update the response structure */
   stackReq->resp.procId = stackReq->procId;
   stackReq->txnType = NORMAL_TXN;
   stackReq->txnStatus = INPROGRESS;

   mrDist.updateRspStruct(miTransId,stackReq);

   logger.logMsg (TRACE_FLAG, 0,"Leaving  m3uaPspStatus");
   return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   GetAssocStatus()
*
*     Desc:  send the Status request for Assoc to M3UA LAyer
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/

int INGwSmStaHdlr::cliGetAssocStatus(PspStatus *psp)
{
   logger.logMsg (TRACE_FLAG, 0,"Entering cliGetAssocStatus");
   ItMgmt &cntrl = l.it;
   cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));

   cntrl.t.ssta.s.pspSta.pspId = psp->pspId;

   fillHdr(&(l.it.hdr), miTransId, ENTIT, 0, TSSTA, STITPSP , 0);

   Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
   lmPst->event = EVTLITSTAREQ;

   SmMiLitStaReq(lmPst, &cntrl);

    /* update the response structure */

   logger.logMsg (TRACE_FLAG, 0,"Leaving  cliGetAssocStatus");
   return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   GetLocalSsnStatus()
*
*     Desc:  send the Status request for LocalSsn to SCCP LAyer
*
*     Notes: None
*
*     File:  INGwSmStaHdlr.C
*
*******************************************************************************/

int INGwSmStaHdlr::cliGetLocalSsnStatus(LocalSsnStatus *ssn)
{
    logger.logMsg (TRACE_FLAG, 0,"Entering cliGetLocalSsnStatus");
    SpMngmt &cntrl = l.sp;
    cmMemset((U8 *)&cntrl, 0, sizeof(SpMngmt));

    fillHdr(&(l.sp.hdr), miTransId, ENTSP, 0, TSSTA, STROUT, 0);

    cntrl.t.ssta.s.spRteSta.pcSta.pc = ssn->dpc;
    cntrl.t.ssta.s.spRteSta.pcSta.nwId = ssn->nwkId;

    Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCC_LAYER);
    lmPst->event = EVTLSPSTAREQ;

    SmMiLspStaReq(lmPst, &cntrl);
    /* update the response structure */
    logger.logMsg (TRACE_FLAG, 0,"Leaving  cliGetLocalSsnStatus");
    return BP_AIN_SM_OK;

}

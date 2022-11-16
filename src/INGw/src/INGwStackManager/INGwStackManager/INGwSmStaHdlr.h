/************************************************************************
     Name:     INAP Stack Manager Status Handler - defines
 
     Type:     C include file
 
     Desc:     Defines required to access Status Handler

     File:     INGwSmStaHdlr.h

     Sid:      INGwSmStaHdlr.h 0  -  03/27/03 

     Prg:      gs

************************************************************************/

#ifndef __BP_AINSMSTAHDLR_H__
#define __BP_AINSMSTAHDLR_H__

//include the various header files
#include "INGwStackManager/INGwSmIncludes.h"
#include "INGwStackManager/INGwSmReqHdlr.h"
#include "INGwStackManager/INGwSmStkReqRespDat.h"

class INGwSmStaHdlr: public INGwSmReqHdlr
{

  /*
   * Public interface
   */
  public:

  //default constructor
  INGwSmStaHdlr(INGwSmDistributor& arDist, int aiLayer, 
           int aiOper, int aiSubOp, int aiTransId);

  //default destructor
  ~INGwSmStaHdlr();

  //invoked by INGwSmRequest to send the message to the Stack
  int sendRequest(INGwSmQueueMsg *apMsg,INGwSmRequestContext *apContext = 0);

  //handle the response
  int handleResponse (INGwSmQueueMsg *apMsg);

  /*
   * Available for All Layers
   */

  //Get the System Id
  int getSystemId (int aiLayerId = 0);

  //Get the SAP Status
  int getSapStatus (int aiSapType = 0, int aiLayerId = 0);

  /*
   * SCCP Layer Status
   */

  //Get the status of route
  int sccpGetRouteStatus (int aiNetworkSapId, int aiPointCode);

  /*
   * M3UA Layer Status
   */

  //Get the status of M3UA Layer
  int m3uaGetStatus ();

  //Get the Address Translation Table Status
  int m3uaGetAddressTranslationTableStatus ();

  //Get the Peer Server Status
  int m3uaGetPeerServerStatus (int aiPsId);

  //Get the Remote Signaling Process Status
  int m3uaGetRemoteSignalProcessStatus (int aiPspId);

  //Get the status of DRKM
  int m3uaGetDRKMStatus ();

  //Get the status of DRKM Request sent to server PSP
  int m3uaGetDRKMReqStatus (int aiPspId);

  /*
   * SCTP Layer Status
   */

  //Get the SCTP Layer Status
  int sctpGetStatus ();

  //Get the Association Status
  int sctpGetAssociationStatus (int aiAssociationId);

  //Get the Destination Transport Address Status
  int sctpGetDestTransportStatus (INGwSmAddress *apTransportAddress);


   int getNodeStatus(StackReqResp *stackReq);
   int sccpRouteStatus(StackReqResp *stackReq);
   int mtp2LnkStatus(StackReqResp *stackReq);
   int mtp3LnkStatus(StackReqResp *stackReq);
   int cliGetLinkStatus(LinkStatus *lnk);
   int mtp3LinkSetStatus(StackReqResp *stackReq);
   int cliGetAssocStatus(PspStatus *psp);
   int m3uaPspStatus(StackReqResp *stackReq);
   int m3uaPsStatus(StackReqResp *stackReq);
   int cliGetLocalSsnStatus(LocalSsnStatus *psp);


  /*
   * Protected interface
   */
  protected:

  //send the request to the respective layer
  //int smMiLieStaReq (Pst *pst, IeMngmt *cntrl);
 
  //send the request to the respective layer
  int smMiLstStaReq (Pst *pst, StMngmt *cntrl);
  
  //send the request to the respective layer
  int smMiLspStaReq (Pst *pst, SpMngmt *cntrl); 
  
  //send the request to the respective layer
  int smMiLitStaReq (Pst *pst, ItMgmt *cntrl);
  
  //send the request to the respective layer
  int smMiLsbStaReq (Pst *pst, SbMgmt *cntrl);
  
  //send the request to the respective layer
  int smMiLhiStaReq (Pst *pst, HiMngmt *cntrl);

  //send the request to the respective layer
  int smMiLsnStaReq (Pst *pst, SnMngmt *cntrl);

  //send the request to the respective layer
  int smMiLsdStaReq (Pst *pst, SdMngmt *cntrl);

  /*
   * Private interface
   */
  private:

  /*
   * Public Data Members
   */
  public:

  /*
   * Protected Data Members
   */
  protected:

  //pointer to repository
  INGwSmRepository *mpRep;

  //operation type
  int miOper;

  //sub-operation type
  int miSubOp;

  //index used for iterating over lists of PSP etc
  int miIndex;

  /*
   * Private Data Members
   */
  private:

};

#endif /* __BP_AINSMSTAHDLR_H__ */

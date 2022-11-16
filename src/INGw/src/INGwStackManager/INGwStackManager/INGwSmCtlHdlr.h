/************************************************************************
     Name:     INAP Stack Manager Control Handler - defines
 
     Type:     C include file
 
     Desc:     Defines required to access Control Request Handler

     File:     INGwSmCtlHdlr.h

     Sid:      INGwSmCtlHdlr.h 0  -  03/27/03 

     Prg:      gs

************************************************************************/

#ifndef __BP_AINSMCTLHDLR_H__
#define __BP_AINSMCTLHDLR_H__

//include the various header files
#include "INGwStackManager/INGwSmIncludes.h"
#include "INGwStackManager/INGwSmReqHdlr.h"
#include "INGwStackManager/INGwSmStkReqRespDat.h"

class INGwSmCtlHdlr: public INGwSmReqHdlr
{

  /*
   * Public interface
   */
  public:

  //default constructor
  INGwSmCtlHdlr(INGwSmDistributor& arDist, int aiLayer, 
          int aiOper, int aiSubOp, int aiTransId);

  //default destructor
  ~INGwSmCtlHdlr();

  //invoked by INGwSmRequest to send the message to the Stack
  int sendRequest(INGwSmQueueMsg *apMsg, INGwSmRequestContext *apContext = 0);

  /*
   * Available for All Layers
   */

  //Enable Alarms
  int setAlarms (StackReqResp *stackReq); 

  //Enable Trace
  //int setTrace (int aiTraceType, int aiStackLayerId = 0);
  int setTrace (StackReqResp *stackReq);
  int disableTrace (StackReqResp *stackReq);

  //Enable Debug Printing
  /*int setDebugPrint (int aiDebugType, int aiDebugLevel = 0,
		  int aiStackLayerId = 0);*/
  int setDebugPrint (StackReqResp *stackReq);
  int disableDebugPrint (StackReqResp *stackReq);
#ifdef INC_DLG_AUDIT
  int ingwAuditMsg (StackReqResp *stackReq);
#endif
  //shutdown Layer
  int shutdown (int aiStackLayerId = 0);
  int disableLsap (StackReqResp *stackReq);
  int disableUsap (StackReqResp *stackReq);

  //enable SAPs
  int updateSAP (StackReqResp *stackReq, int aiSapOper, int aiSapType = 0);

  /* 
   * TCAP Layer Operations
   */

#ifdef INC_DLG_AUDIT
  //Audit Unused Dialogs
  int tcapAuditDialogs ();

  //Audit Unused Invokes
  int tcapAuditInvokes ();
#endif

  /*
   * SCCP Layer Operations
   */

  //SCCP Congestion Control - ITU 96 only - Not implemented
  int sccpCongestionControl ();

  //Delete a Route
  int sccpDeleteRoute (int aiNetworkSAPId = 0, int aiDpc = 0, int aiSsn = 0);

  //Enable/Disable Auditing of signalling connection status
  int sccpConnStatusAudit (int aiConnStaType = 0);

  //Enable SCCP Error Performance and System Availability Report
  //Not Implemented - for ITU 96 only
  int sccpEnableErrorReport ();

  //Start Guard Timer
  int sccpGuardTimer ();

  //Delete the Address Map
  int sccpDeleteAddressMap (int aiActionType = 0, int aiSwitch = 0, int aiReplaceFlag = 0,
		  int aiConnectionCouplingFlag = 0, void *apGtt = 0, 
		  int aiNumSccpEntities = 0);

  //Delete an association
  int sccpDeleteAssociation (void *apRule = 0, void *apActionArray = 0);

  //Delete a configured Network not associated with a SAP
  int sccpDeleteNetwork (void *apNetworkId = 0);

  /*
   * M3UA Layer operations
   */

  int m3uaOpenEp (StackReqResp *stackReq);

  //establish association with a remote PSP
  int m3uaEstablishAssociation (INGwSmPspId &apPsp);
 
  //terminate association with remote PSP
  int m3uaTerminateAssociation (INGwSmPspId &apPsp);
  
  //send ASPAC to remote PSP
  int m3uaSendAspac (int aiPsId, INGwSmPsId *apPsList = 0, void *apInfoField = 0);
  
  //send ASPUP to remote PSP
  int m3uaSendAspup (int aiPsId, INGwSmPsId *apPsList = 0, void *apInfoField = 0);
  int m3uaSendAspdown (StackReqResp *stackReq);
  
  //send ASPDN to remote PSP
  int m3uaSendAspdn (int aiPsId, INGwSmPsId *apPsList = 0, void *apInfoField = 0);
  
  //send ASPIA to remote PSP
  int m3uaSendAspia (int aiPsId, INGwSmPsId *apPsList = 0, void *apInfoField = 0);
  
  //send SCON to remote PSP
  int m3uaSendScon (int aiPsId = 0, INGwSmPsId *apPsList = 0, void *apInfoField = 0);
  
  //send Management Inhibit of association with remote SP
  int m3uaInhibitAssociation (INGwSmPsId apSp = 0);
  
  //send Management Uninhibit of association with remote SP
  int m3uaUninhibitAssociation (INGwSmPsId apSp = 0);
  
  //Delete Routing Entry
  int m3uaDeleteRoutingEntry (int aiNetworkId = 0, INGwSmPsId aiPsId = 0, 
                              INGwSmPc aiDpc = 0, int aiRoutingEntry = 0);
  
  //Delete Network Entry
  int m3uaDeleteNetworkEntry ();
  
  //Delete PS
  int m3uaDeletePS ();
  
  //Delete remote PSP
  int m3uaDeletePSP ();
  
  //register routing Keys to peer server
  int m3uaRegisterRoutingKeys (int aiPsid = 0, int aiDpc = 0, int aiNumOpcs = 0,
       void *apOpcList = 0, int aiNumSio = 0, void *apSioList = 0, void *apCicRange = 0);
  
  //deregister routing Keys to peer server
  int m3uaDeregisterRoutingKeys (int aiPsid = 0, int aiDpc = 0, int aiNumOpcs = 0,
        void *apOpcList = 0, int aiNumSio = 0, void *apSioList = 0, void *apCicRange = 0);
#if 0
 void fillHdr(Header *hdr,U32 miTransId,U8 entId, U8 instId,U8 msgType,S16 elmntId,S16 elInst1Id);
#endif

  int delGtAddrMap (StackReqResp *stackReq);
  int delGtRule (StackReqResp *stackReq);
  int disableUserPart(StackReqResp *stackReq);
  int enableUserPart(StackReqResp *stackReq);
  int enableMtp2Lnk(StackReqResp *stackReq);
  int enableMtp3Lnk(StackReqResp *stackReq);
  int disableMtp2Link(StackReqResp *stackReq);
  int disableMtp3Link( StackReqResp *stackReq);
  int delMtp2Link(StackReqResp *stackReq);
  int delMtp3Link(StackReqResp *stackReq);
  int delLdfMtp3Link(StackReqResp *stackReq);
  int delSccpRoute(StackReqResp *stackReq);
  int delM3uaRoute(StackReqResp *stackReq);
  int delLinkSet(StackReqResp *stackReq);
  int delMtp3Route(StackReqResp *stackReq);
  int delMtp3UserPart(StackReqResp *stackReq);
  int delLdfMtp3UserPart(StackReqResp *stackReq);
  int delSccpUserPart(StackReqResp *stackReq);
  int delM3uaUserPart(StackReqResp *stackReq);
  int delLdfM3uaUserPart(StackReqResp *stackReq);
  int delM3uaPs(StackReqResp *stackReq);
  int delSccpPs(StackReqResp *stackReq);
  int delM3uaPsp(StackReqResp *stackReq);
  int delSccpNwk(StackReqResp *stackReq);
  int delM3uaNwk(StackReqResp *stackReq);
  int disableTcapLocalSsn(StackReqResp *stackReq);
  int enableTcapLocalSsn(StackReqResp *stackReq);
  int delTcapSsn(StackReqResp *stackReq);
  int delTcapUsap(StackReqResp *stackReq);
  int delSccpSsn(StackReqResp *stackReq);
  int disableM3uaEndPoint(StackReqResp *stackReq);
  int disableSctpEndPoint(StackReqResp *stackReq);
  int enableM3uaEndPoint(StackReqResp *stackReq);
  int enableSctpEndPoint(StackReqResp *stackReq);
  int delTuclEndPoint(StackReqResp *stackReq);
  int delSctpEndPoint(StackReqResp *stackReq);
  int delSctpLsapEndPoint(StackReqResp *stackReq);
  int delM3uaEndPoint(StackReqResp *stackReq);
  int delRemoteSsn(StackReqResp *stackReq);
  int m3uaSendAspInActive(StackReqResp *stackReq);
  int m3uaSendAspActive(StackReqResp *stackReq);
  int m3uaSendAspDown(StackReqResp *stackReq);
  int m3uaSendAspUp(StackReqResp *stackReq);
  int m3uaAssocDown(StackReqResp *stackReq);
  int m3uaAssocUp(StackReqResp *stackReq);
  int m3uaAssocAbort(StackReqResp *stackReq);
  int m3uaSctsapUnbnd(StackReqResp *stackReq);
#ifdef INC_ASP_SNDDAUD
  int m3uaSendDaud(StackReqResp *stackReq);
#endif

  //int sgEnable ();
  int sgEnableNode (StackReqResp *stackReq);
  int sgDisableNode (StackReqResp *stackReq);
  int sgAbortTrans (StackReqResp *stackReq);
  

/*
   * Protected interface
   */
  protected:

  //sending the bind request for INAP User
  int iuLiIetBndReq(Pst *pst, SuId suId, SpId spId);

  //send the request to the respective layer
  //int smMiLieCntrlReq (Pst *pst, IeMngmt *cntrl);

  //send the request to the respective layer
  int smMiLstCntrlReq (Pst *pst, StMngmt *cntrl);

  //send the request to the respective layer
  int smMiLspCntrlReq (Pst *pst, SpMngmt *cntrl);

  //send the request to the respective layer
  int smMiLitCntrlReq (Pst *pst, ItMgmt *cntrl);

  //send the request to the respective layer
  int smMiLsbCntrlReq (Pst *pst, SbMgmt *cntrl);

  //send the request to the respective layer
  int smMiLhiCntrlReq (Pst *pst, HiMngmt *cntrl);

  //send the request to MTP3 layer
  int smMiLsnCntrlReq (Pst *pst, SnMngmt *cntrl);

  //send the request to MTP2 layer
  int smMiLsdCntrlReq (Pst *pst, SdMngmt *cntrl);

  int shutdownTcapLayer(StackReqResp *stackReq);
  int shutdownSccpLayer(StackReqResp *stackReq);
  int shutdownMtp3Layer(StackReqResp *stackReq);
  int shutdownM3uaLayer(StackReqResp *stackReq);
  int shutdownSctpLayer(StackReqResp *stackReq);

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

  //the operation type
  int miOper;

  //the sub operation type
  int miSubOp;

  //pointer to repository
  INGwSmRepository *mpRep;

  INGwSmDistributor *mpDist;

  //index for PSP list etc
  int miIndex;

  /*
   * Private Data Members
   */
  private:

};

#endif /* __BP_AINSMCTLHDLR_H__ */

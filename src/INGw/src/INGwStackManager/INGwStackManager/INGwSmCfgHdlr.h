/************************************************************************
     Name:     INAP Stack Manager Configuration Handler - defines
 
     Type:     C include file
 
     Desc:     Defines required to access Configuration Handler

     File:     INGwSmCfgHdlr.h

     Sid:      INGwSmCfgHdlr.h 0  -  03/27/03 

     Prg:      gs

************************************************************************/

#ifndef __BP_AINSMCFGHDLR_H__
#define __BP_AINSMCFGHDLR_H__

//include the various header files
#include "INGwStackManager/INGwSmIncludes.h"
#include "INGwStackManager/INGwSmReqHdlr.h"
#include "INGwStackManager/INGwSmStkReqRespDat.h"

#ifndef BYTE_WORD_DWORD
#define BYTE_WORD_DWORD
typedef  unsigned char  byte;
typedef  unsigned short word;
typedef  uint32_t  dword;
#endif


/*
 * This class defines the object to be created for configuration handling
 * of different layers as well as the sending protocol to the stack
 */

class INGwSmCfgHdlr : public INGwSmReqHdlr
{

  /*
   * Public interface
   */
  public:

  //default constructor
  INGwSmCfgHdlr(INGwSmDistributor& arDist, int aiLayer, 
        int aiOper, int aiSubOp, int aiTransId);

  //default destructor
  ~INGwSmCfgHdlr();

  //invoked by INGwSmRequest to send the message to the Stack
  int sendRequest(INGwSmQueueMsg *apMsg, INGwSmRequestContext *apContext = 0);

  /*
   * INAP Layer configuration
   */

  //General configuration for INAP Layer
  int inapGenCfg ();

  //Upper SAP Configuration for INAP Layer
  int inapUSapCfg ();

  //Lower SAP Configuration for INAP Layer
  int inapLSapCfg ();

  /*
   * TCAP Layer Configuration
   */

  //General Configuration for TCAP Layer
  int tcapGenCfg ();

  //Upper SAP COnfiguration for TCAP Layer
  int tcapUSapCfg (StackReqResp *stackReq);

  //Lower SAP Configuration for TCAP Layer
  int tcapLSapCfg (StackReqResp *stackReq);

  /*
   * SCCP Layer Configuration
   */

  //General Configuration for SCCP Layer
  int sccpGenCfg ();

  //Upper SAP Configuration for SCCP Layer
  int sccpUSapCfg (StackReqResp *stackReq);

  //Lower SAP Configuration for SCCP Layer
  int sccpLSapCfg (StackReqResp *stackReq);

  //Network Configuration for SCCP Layer
  int sccpNwCfg (StackReqResp *nwk);

  //Route Configuration for SCCP Layer
  int sccpRouteCfg (StackReqResp *stackReq);

  /*
   * M3UA Layer Configuration
   */

  //General COnfiguration for M3UA Layer
  int m3uaGenCfg ();

  //Upper SAP Configuration for M3UA Layer
  int m3uaUSapCfg (StackReqResp *up);

  //Lower SAP Configuration for M3UA Layer
  int m3uaLSapCfg (StackReqResp *ep);

  //Network Configuration for M3UA Layer
  int m3uaNwCfg (StackReqResp *nwk);

  //Routing Entry Configuration for M3UA Layer
  int m3uaRouteEntryCfg (StackReqResp *stackReq);

  //Peer Server Configuration for M3UA Layer
  int m3uaPSCfg (StackReqResp *stackReq);

  //Peer Signaling Process Configuration for M3UA Layer
  int m3uaPSPCfg (StackReqResp *stackReq);

  /*
   * SCTP Layer Configuration
   */

  //General Configuration for SCTP Layer
  int sctpGenCfg ();

  //Upper SAP Configuration for SCTP Layer
  int sctpUSapCfg (StackReqResp *stackReq);

  //Lower SAP Configuration for SCTP Layer
  int sctpLSapCfg (StackReqResp *stackReq);

  /*
   * TUCL Layer Configuration
   */

  //General Configuration for TUCL Layer
  int tuclGenCfg ();

  //Upper SAP Configuration for TUCL Layer
  int tuclUSapCfg (StackReqResp *stackReq);

  /*------------------------------------------
     MTP3 Layer Configuration
   -------------------------------------------*/

  //General COnfiguration for MTP3 Layer
  int mtp3GenCfg ();

  //Upper SAP Configuration for MTP3 Layer
  int mtp3USapCfg (StackReqResp *req);

  //Lower SAP Configuration for MTP3 Layer
  /*int mtp3LSapCfg ();*/
  int mtp3LSapCfg (StackReqResp *req);

  //Network Configuration for MTP3 Layer
  int mtp3LinkSetCfg (StackReqResp *req);

  //Self Rout Entry Configuration for MTP3 Layer
  int mtp3RouteCfgSelf ();
  int mtp3RouteCfg(StackReqResp *stackReq);
  //Peer Rout Entry Configuration for MTP3 Layer
  int mtp3RouteCfgPeer ();

  /*------------------------------------------
     RELAY Layer Configuration
   -------------------------------------------*/

  //General COnfiguration for Relay
  int ryGenCfg ();

  //Listen channel Configuration for relay
  int ryLChanCfg ();

  //server channel Configuration for relay
  int rySChanCfg ();

  //client channel Configuration for relay
  int ryCChanCfg ();
  /*------------------------------------------
     MTP2 Layer Configuration
   -------------------------------------------*/

  //General COnfiguration for MTP3 Layer
  int mtp2GenCfg ();

  //Upper SAP Configuration for MTP3 Layer
  //int mtp2DLSapCfg (StackReqResp *req);
  int mtp2DLSapCfg (StackReqResp *req);

  int cliAddRemoteSsn(AddRemoteSsn *ssn);
  int addGtrule (StackReqResp *stackReq);
  int addGtAddrMap (StackReqResp *stackReq);
  int cliAddLink(AddLink *addlink);
  int cliNodeStatus(NodeStatus *node);
  int mtp2LnkStatus(LinkStatus *lnk);
  int cliRouteStatus(RouteStatus *dpc);
  int mtp3LnkStatus(LinkStatus *lnk);
  int cliLinkStatus(LinkStatus *lnk);
  int cliLinkSetStatus(LinkSetStatus *lnkSet);
  int cliAddLinkSet(AddLinkSet *lnkSet);
  int cliAddRoute(AddRoute *dpc);
  int cliAddUserPart(AddUserPart *up);
  int cliAddPs(AddPs *ps);
  int cliAddPsp(AddPsp *psp);
  int cliAddNetwork( AddNetwork *nwk);
  int cliAddLocalSsn(AddLocalSsn *ssn);
  int cliAddEndPoint(AddEndPoint *ep);
  int mtp3RouteCfg();
  int psfTcapGenCfg();
  int psfSccpGenCfg();
  int ldfMtp3GenCfg();
  int ldfMtp3USapCfg(StackReqResp *req);
  int ldfMtp3LSapCfg(StackReqResp *req);
  int ldfMtp3RsetMapCriticalCfg();
  int ldfMtp3RsetMapNonCriticalCfg(); 
  int ldfMtp3RsetMapDefCfg(); 
  int ldfM3uaGenCfg();
  int ldfM3uaUSapCfg(StackReqResp *stackReq);
  int ldfM3uaNwkCfg();
  int ldfM3uaRsetMapCriticalCfg();
  int ldfM3uaRsetMapNonCriticalCfg(); 
  int ldfM3uaRsetMapDefCfg(); 
  int psfMtp3GenCfg();
  int psfMtp3RsetMapCriticalCfg();
  int psfMtp3RsetMapNonCriticalCfg(); 
  int psfMtp3RsetMapNonCrit(); 
  int psfMtp3RsetMapDefCfg(); 
  int psfM3uaGenCfg();
  int psfM3uaRsetMapCriticalCfg();
  int psfM3uaRsetMapNonCriticalCfg(); 
  int psfM3uaRsetMapDefCfg(); 

  int rollBack(StackReqResp *rb);

  int shGenCfg();
  int mrGenCfg();
  int sgGenCfg();
  int sgEntSgCfg();
  int sgEntTuclCfg();
  int sgEntSctpCfg();
  int sgEntM3uaCfg();
  int sgEntMtp2Cfg();
  int sgEntMtp3Cfg();
  int sgEntSccpCfg();
  int sgEntTcapCfg();
/*
   * Protected interface
   */
  protected:

  //send the configuration request to the respective layer
 /* int smMiLieCfgReq (Pst *apPst, IeMngmt *cfg)*/

  //send the configuration request to the respective layer
  int smMiLstCfgReq (Pst *apPst, StMngmt *cfg);

  //send the configuration request to the respective layer
  int smMiLspCfgReq (Pst *apPst, SpMngmt *cfg);

  //send the configuration request to the respective layer
  int smMiLitCfgReq (Pst *apPst, ItMgmt *cfg);

  //send the configuration request to the respective layer
  int smMiLsbCfgReq (Pst *apPst, SbMgmt *cfg);

  //send the configuration request to the respective layer
  int smMiLhiCfgReq (Pst *apPst, HiMngmt *cfg);

  //send the configuration request to the respective layer
  int smMiLsnCfgReq (Pst *apPst, SnMngmt* cfg);

  //send the configuration request to the respective layer
  int smMiLsdCfgReq (Pst *apPst, SdMngmt *cfg);

  /*
   * Private interface
   */
  private:

  INGwSmQueueMsg *mpQueMsg;
  /*
   * Public Data Members
   */
  public:
  INGwSmDistributor *mpDist;

  /*
   * Protected Data Members
   */
  protected:

  //the operation
  int miOper;

  //the subtype
  int miSubOp;

  //index used for multiple routes, PSPs etc.
  int miIndex;

  //Count used for multiple cmb link sets
  int miCount;

  /*
   * Private Data Members
   */
  private:

};

/* MTP2 configuration defines*/


#endif /* __BP_AINSMCFGHDLR_H__ */

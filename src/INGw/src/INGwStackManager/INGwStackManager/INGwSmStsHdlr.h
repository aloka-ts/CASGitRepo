/************************************************************************
     Name:     INAP Stack Manager Statistics Handler - defines
 
     Type:     C include file
 
     Desc:     Defines required to access Statistics handler

     File:     INGwSmStsHdlr.h

     Sid:      INGwSmStsHdlr.h 0  -  03/27/03 

     Prg:      gs

************************************************************************/

#ifndef __BP_AINSMSTSHDLR_H__
#define __BP_AINSMSTSHDLR_H__

//include the various header files
#include "INGwStackManager/INGwSmIncludes.h"
#include "INGwStackManager/INGwSmReqHdlr.h"

#define BP_AIN_SM_STS_ACTION   ZEROSTS

#define STS_MASK_ALL        0xffffffff
#define STS_MASK_IE_ALL     0x00000001  // INAP
#define STS_MASK_IE_GEN     0x00000001
#define STS_MASK_ST_ALL     0x00000010  // TCAP
#define STS_MASK_ST_GEN     0x00000010
#define STS_MASK_SP_ALL     0x00000f00  // SCCP
#define STS_MASK_SP_GEN     0x00000100
#define STS_MASK_SP_USAP    0x00000200
#define STS_MASK_SP_LSAP    0x00000400
#define STS_MASK_SP_RTE     0x00000800
#define STS_MASK_IT_ALL     0x0000f000  // M3UA
#define STS_MASK_IT_GEN     0x00001000
#define STS_MASK_IT_USAP    0x00002000
#define STS_MASK_IT_LSAP    0x00004000
#define STS_MASK_IT_PSPST   0x00008000
#define STS_MASK_SB_ALL     0x00070000  // SCTP
#define STS_MASK_SB_GEN     0x00010000
#define STS_MASK_SB_USAP    0x00020000
#define STS_MASK_SB_LSAP    0x00040000
#define STS_MASK_HI_ALL     0x00300000  // TUCL
#define STS_MASK_HI_GEN     0x00100000
#define STS_MASK_HI_USAP    0x00200000

#define STS_MASK_SN_ALL     0x0f000000  //MTP3
#define STS_MASK_SN_SP      0x01000000
#define STS_MASK_SN_LINK    0x02000000
#define STS_MASK_SN_RTE     0x04000000
#define STS_MASK_SN_LNKSET  0x08000000

//new statistics
#define STS_MASK_DN_ALL     0x10000001 
#define STS_MASK_DN_GEN     0x10000002

#define STS_MASK_DV_ALL     0x10000010 
#define STS_MASK_DV_GEN     0x10000020

#define STS_MASK_MR_ALL     0x10000100  
#define STS_MASK_MR_GEN     0x10000200
//new statistics

//forward reference
class INGwSmStsMap;

class INGwSmStsHdlr : public INGwSmReqHdlr
{

  /*
   * Public interface
   */
  public:

  //default constructor
  INGwSmStsHdlr(INGwSmDistributor* arDist, int aiLayer);


  //default destructor
  ~INGwSmStsHdlr();

  //initialize routine to be invoked once only
  int initialize(int aiTransId);

  //invoked by INGwSmRequest to handle the response from the stack
  int handleResponse (INGwSmQueueMsg *arQueueMsg);

  //start the processing
  int start ();

  //stop the processing
  int stop ();

  //set the operation bit map
  int setOperationMask (int aiLevel);

  int setStatisticsMask (long mask);
  int getStatisticsMask (std::ostrstream &statsLayer);
  /*
   * Protected interface
   */
  protected:

  //start timer
  int startTimer (int aiDuration);

  //stop timer
  int stopTimer (int aiDuration);


  //send the message to the Stack
  int sendRequest(INGwSmQueueMsg *apMsg,INGwSmRequestContext *apContext = 0);
  int createStatsReq (StackReqResp *stackReq);

  /* 
   * INAP Layer Operations
   */

  //Get INAP Layer Statistics
  int inapGetStatistics (int aiBitMap);

  //int handleIeRsp (IeMngmt &aeMgmt);

  //Get LDF-MTP3 Layer Statistics
  int ldfMtp3GetStatistics(int aiBitMap);

  //Get LDF-M3UA Layer Statistics
  int ldfM3uaGetStatistics(int aiBitMap);

  //Get MR Statistics
  int mrGetStatistics(int aiBitMap);
  /* 
   * TCAP Layer Operations
   */

  //Get TCAP Layer Statistics
  int tcapGetStatistics (int aiBitMap);


  int handleStRsp (StMngmt &aeMgmt);


  /* 
   * SCCP Layer Operations
   */

  //Get SCCP Layer Statistics
  int sccpGetStatistics (int aiBitMap);

  int handleSpRsp (SpMngmt &aeMgmt);

  /* 
   * M3UA Layer Operations
   */

  //Get M3UA Layer Statistics
  int m3uaGetStatistics (int aiBitMap);

  int handleItRsp (ItMgmt &aeMgmt);

  /* 
   * SCTP Layer Operations
   */

  //Get SCTP Layer Statistics
  int sctpGetStatistics (int aiBitMap);

  int handleSbRsp (SbMgmt &aeMgmt);

  /* 
   * TUCL Layer Operations
   */

  //Get TUCL Layer Statistics
  int tuclGetStatistics (int aiBitMap);

  int handleHiRsp (HiMngmt  &aeMgmt);

  int handleDvRsp (LdvMngmt &aeMgmt);

  int handleDnRsp (LdnMngmt &aeMgmt);

  int handleMrRsp (MrMngmt  &aeMgmt);

  /* 
   * MTP3 Layer Operations
   */

  //Get MTP3 Layer Statistics
  int mtp3GetStatistics (int aiBitMap);
  int handleSnRsp (SnMngmt &aeMgmt);

  /* 
   * MTP2 Layer Operations
   */

  //Get MTP2 Layer Statistics
  int mtp2GetStatistics (int aiBitMap);
  int handleSdRsp (SdMngmt &aeMgmt);

  //send the request to the respective layer
//  int smMiLieStsReq (Pst *pst, Action action, IeMngmt *sts);

  //send the request to the respective layer
  int smMiLstStsReq (Pst *pst, Action action, StMngmt *sts);

  //send the request to the respective layer
  int smMiLspStsReq (Pst *pst, Action action, SpMngmt *sts);

  //send the request to the respective layer
  int smMiLitStsReq (Pst *pst, Action action, ItMgmt *sts);

  //send the request to the respective layer
  int smMiLsbStsReq (Pst *pst, Action action, SbMgmt *sts);

  //send the request to the respective layer
  int smMiLhiStsReq (Pst *pst, Action action, HiMngmt *sts);

  //send the request to the respective layer
  int smMiLsnStsReq (Pst *pst, Action action, SnMngmt *sts);

  //send the request to the respective layer
  int smMiLsdStsReq (Pst *pst, Action action, SdMngmt *sts);

  //send the request to the respective layer
  int smMiLdvStsReq (Pst *pst, Action action, LdvMngmt *sts);

  //send the request to the respective layer
  int smMiLdnStsReq (Pst *pst, Action action, LdnMngmt *sts);

  //send the request to the respective layer
  int smMiLmrStsReq (Pst *pst, Action action, MrMngmt *sts);

  //update the CCM Stat Mgr
  int updateStsMgr (INGwSmStsOid *apOid);
  /*
   * Private interface
   */
  private:

  /*
   * Public Data Members
   */



  public:
  int miTransId;
  int miOper;
  int miSubOp;
  int miLayer;
  
  /*
   * Protected Data Members
   */
  protected:

  int miMajorState;
  int miMinorState;


  //statistics Map
  INGwSmStsMap *mpStsMap;

  //timer value
  int miDuration;

  //check for if timer is already running
  bool mbIsTimerRunning;

  //check to start and stop sts handler
  bool mbIsRunning;

  /*
   * Private Data Members
   */
  private:

  INGwSmDistributor* mpDist;

  long mlBitMap;	// sts collection bitmap.
  long mlBitMapCopy;	// sts collection bitmap.

};

#endif /* __BP_AINSMSTSHDLR_H__ */

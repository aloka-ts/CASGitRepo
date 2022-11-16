/************************************************************************
     Name:     Stack Manager Adaptor - defines
 
     Type:     C include file
 
     Desc:     Defines required to receive message from Stack in TAPA

     File:     INGwSmAdaptor.h

     Sid:      INGwSmAdaptor.h 0  -  03/27/03 

     Prg:      gs,bd

************************************************************************/

#ifndef __BP_AINSMADAPTOR_H__
#define __BP_AINSMADAPTOR_H__

//include the various header files
#include "INGwStackManager/INGwSmIncludes.h"
#include "INGwStackManager/INGwSmTrcHdlr.h"
#include "INGwStackManager/INGwSmRepository.h"


/*
 * StackManager state
 */
typedef enum {

  BP_AIN_SM_STARTING_UP   = 0,
  BP_AIN_SM_UP            = 1,
  BP_AIN_SM_SHUTTING_DOWN = 2

} INGwSmAdaptorState;

 
class INGwSmDistributor;
class INGwSmTrcHdlr;

/*
 * This class will be encapsulating the TAPA Task which 
 * will receive the messages from the SSI and post them
 * to the SM Queue as the INGwSmQueueMsg.
 * This class is a SINGLETON.
 */

class INGwSmAdaptor
{
  /*
   * Public interface
   */
  public:

  // d'tor
  ~INGwSmAdaptor();

  // singleton accessor/creator
  static INGwSmAdaptor* getInstance();

  // save the Distributor ptr for later use
  static int initialize(INGwSmDistributor *apDist);
   
  // get distributor reference
  static INGwSmDistributor* getDistributor();

  // get trace handler reference
  static INGwSmTrcHdlr* getTrcHdlr();

  // prepare the config confirm msg
  static INGwSmQueueMsg* iNGwSmMkCfgCfmQueMsg(int aLayerId, void* apMgmt);

  // prepare the control confirm msg
  static INGwSmQueueMsg* iNGwSmMkCtlCfmQueMsg(int aLayerId, void* apMgmt);

  // prepare the status confirm msg
  static INGwSmQueueMsg* iNGwSmMkStaCfmQueMsg(int aLayerId, void* apMgmt);

  // prepare the statistics confirm msg
  static INGwSmQueueMsg* iNGwSmMkStsCfmQueMsg(int aLayerId, void* apMgmt);

  // creates a INGwSmQueueMsg containing a Confirmation indication
  // from the stack.
  static INGwSmQueueMsg* iNGwSmMkCfmQueMsg(int aLayerId, void* apMgmt,
                                             INGwSmStackMsgType aStkMsgTyp);

  // creates a INGwSmQueueMsg containing a Alarm indication and
  // information required for handling that alarm.
  static INGwSmQueueMsg* iNGwSmMkStkAlmQueMsg(int aLayerId, void* apMgmt);

  // send a message to dispatcher queue.
  static int sendMsgToDispatcher(INGwSmQueueMsg *aQMsg, bool aPstFlag);

  /*
   * Protected interface
   */
  protected:

  // c'tor  --- this class is a singleton.
  INGwSmAdaptor();


  /*
   * Private methods
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

  /*
   * Private Data Members
   */
  private:

  //to Distributor ptr to postMsg()
  static INGwSmDistributor *mpDist;

  //trace handler created & contained by this class.
  static INGwSmTrcHdlr     *mpTrcHdlr;

  //pointer to self single object (singleton)
  static INGwSmAdaptor     *mpSelf;

  //shutting down flag
  static INGwSmAdaptorState mState;

};


// the following methods would be called from C libraries
// so for clean linkage, need to avoid mangling.
//
#ifdef __cplusplus
#ifndef __CCPU_CPLUSPLUS
extern "C" {
#endif
#endif 

// note: also since func-ptrs need to be registered as 
// callbacks, we cannot permit them to have the hidden "this"
// ptr, so make them "static".
//

//the Initialization TAPA Task routine
short iNGwSmActvInit (Ent aEnt, Inst aInst, Region aRegion, Reason aReason);

//the activate function for the TAPA Task
//this will receive all the messages for stack manager and then
//distribute them according to the Source Entity
short iNGwSmActvTsk (Pst *aPost, Buffer *apBuf);

//the timeout handler function of the Stack
short iNGwSmActvStsTmr ();


/*
 * handler functions for INAP
 */

//activation function for INAP Layer
static short iNGwSmIeActvTsk (Pst *aPost, Buffer *apBuf);

//Configuration Confirm Handler of INAP Layer
//static short iNGwSmIeCfgCfm (Pst *aPost, IeMngmt *aCfm);

//Control Confirm handler of INAP Layer
//static short iNGwSmIeCtlCfm (Pst *aPost, IeMngmt *aCfm);

//Status Confirm handler of INAP Layer
//static short iNGwSmIeStaCfm (Pst *aPost, IeMngmt *aCfm);

//Statistics Confirm handler of INAP Layer
//static short iNGwSmIeStsCfm (Pst *aPost, IeMngmt *aCfm);

//Trace Indication handler of INAP Layer
//static short iNGwSmIeTrcInd (Pst *aPost, IeMngmt *aCfm);

//Unsolicited Status Indication of INAP Layer
//static short iNGwSmIeStaInd (Pst *aPost, IeMngmt *aCfm);

/*
 * handler function for SH
 */

//activation function for SH   Layer
static short iNGwSmShActvTsk (Pst *aPost, Buffer *apBuf);

//Configuration Confirm Handler of SH   Layer
static short iNGwSmShCfgCfm (Pst *aPost, ShMngmt *aCfm);

//Control Confirm handler of SH   Layer
static short iNGwSmShCtlCfm (Pst *aPost, ShMngmt *aCfm);

//Status Confirm handler of SH   Layer
static short iNGwSmShStaCfm (Pst *aPost, ShMngmt *aCfm);

//Statistics Confirm handler of SH   Layer
static short iNGwSmShStsCfm (Pst *aPost, ShMngmt *aCfm);

//Trace Indication handler of SH   Layer
static short iNGwSmShTrcInd (Pst *aPost, ShMngmt *aCfm);

//Unsolicited Status Indication of SH   Layer
static short iNGwSmShStaInd (Pst *aPost, ShMngmt *aCfm);

/*
 * handler function for SG
 */

//activation function for SG   Layer
static short iNGwSmSgActvTsk (Pst *aPost, Buffer *apBuf);

//Configuration Confirm Handler of SG   Layer
static short iNGwSmSgCfgCfm (Pst *aPost, SgMngmt *aCfm);

//Control Confirm handler of SG   Layer
static short iNGwSmSgCtlCfm (Pst *aPost, SgMngmt *aCfm);

//Status Confirm handler of SG   Layer
static short iNGwSmSgStaCfm (Pst *aPost, SgMngmt *aCfm);

//Statistics Confirm handler of SG   Layer
static short iNGwSmSgStsCfm (Pst *aPost, SgMngmt *aCfm);

//Trace Indication handler of SG   Layer
static short iNGwSmSgTrcInd (Pst *aPost, SgMngmt *aCfm);

//Unsolicited Status Indication of SG  Layer
static short iNGwSmSgStaInd (Pst *aPost, SgMngmt *aCfm);




/*
 * handler function for RY
 */

//activation function for RY   Layer
static short iNGwSmRyActvTsk (Pst *aPost, Buffer *apBuf);

//Configuration Confirm Handler of RY   Layer
static short iNGwSmRyCfgCfm (Pst *aPost, RyMngmt *aCfm);

//Control Confirm handler of RY   Layer
static short iNGwSmRyCtlCfm (Pst *aPost, RyMngmt *aCfm);

//Status Confirm handler of RY   Layer
static short iNGwSmRyStaCfm (Pst *aPost, RyMngmt *aCfm);

//Statistics Confirm handler of RY   Layer
static short iNGwSmRyStsCfm (Pst *aPost, RyMngmt *aCfm);

//Trace Indication handler of RY   Layer
static short iNGwSmRyTrcInd (Pst *aPost, RyMngmt *aCfm);

//Unsolicited Status Indication of RY  Layer
static short iNGwSmRyStaInd (Pst *aPost, RyMngmt *aCfm);



/*
 * handler function for MR
 */

//activation function for MR   Layer
static short iNGwSmMrActvTsk (Pst *aPost, Buffer *apBuf);


//Configuration Confirm Handler of MR   Layer
static short iNGwSmMrCfgCfm (Pst *aPost, MrMngmt *aCfm);

//Control Confirm handler of MR   Layer
static short iNGwSmMrCtlCfm (Pst *aPost, MrMngmt *aCfm);

//Status Confirm handler of MR   Layer
static short iNGwSmMrStaCfm (Pst *aPost, MrMngmt *aCfm);

//Statistics Confirm handler of MR   Layer
static short iNGwSmMrStsCfm (Pst *aPost, MrMngmt *aCfm);

//Trace Indication handler of MR   Layer
static short iNGwSmMrTrcInd (Pst *aPost, MrMngmt *aCfm);

//Unsolicited Status Indication of MR  Layer
static short iNGwSmMrStaInd (Pst *aPost, MrMngmt *aCfm);






/*
 * handler function for TCAP
 */

//activation function for TCAP Layer
static short iNGwSmStActvTsk (Pst *aPost, Buffer *apBuf);

//Configuration Confirm Handler of TCAP Layer
static short iNGwSmStCfgCfm (Pst *aPost, StMngmt *aCfm);

//Control Confirm handler of TCAP Layer
static short iNGwSmStCtlCfm (Pst *aPost, StMngmt *aCfm);

//Status Confirm handler of TCAP Layer
static short iNGwSmStStaCfm (Pst *aPost, StMngmt *aCfm);

//Statistics Confirm handler of TCAP Layer
static short iNGwSmStStsCfm (Pst *aPost, StMngmt *aCfm);

//Trace Indication handler of TCAP Layer
static short iNGwSmStTrcInd (Pst *aPost, StMngmt *aCfm);

//Unsolicited Status Indication of TCAP Layer
static short iNGwSmStStaInd (Pst *aPost, StMngmt *aCfm);

//Control Confirm handler of PSF TCAP Layer
static short iNGwSmZtCtlCfm (Pst *aPost, ZtMngmt *aCfm);

static short iNGwSmZtCfgCfm (Pst *aPost, ZtMngmt *aCfm);

static short iNGwSmZtStaCfm (Pst *aPost, ZtMngmt *aCfm);

static short iNGwSmZtStsCfm (Pst *aPost, ZtMngmt *aCfm);

static short iNGwSmZtStaInd (Pst *aPost, ZtMngmt *aCfm);

static short iNGwSmZtTrcInd (Pst *aPost, ZtMngmt *aCfm);

//Control Confirm handler of PSF SCCP Layer
static short iNGwSmZpCtlCfm (Pst *aPost, ZpMngmt *aCfm);

static short iNGwSmZpStaCfm (Pst *aPost, ZpMngmt *aCfm);

static short iNGwSmZpStsCfm (Pst *aPost, ZpMngmt *aCfm);

static short iNGwSmZpCfgCfm (Pst *aPost, ZpMngmt *aCfm);

static short iNGwSmZpStaInd (Pst *aPost, ZpMngmt *aCfm);

static short iNGwSmZpTrcInd (Pst *aPost, ZpMngmt *aCfm);

//Control Confirm handler of PSF MTP3 Layer
static short iNGwSmZnCtlCfm (Pst *aPost, ZnMngmt *aCfm);

static short iNGwSmZnStaCfm (Pst *aPost, ZnMngmt *aCfm);

static short iNGwSmZnStsCfm (Pst *aPost, ZnMngmt *aCfm);

static short iNGwSmZnCfgCfm (Pst *aPost, ZnMngmt *aCfm);

static short iNGwSmZnStaInd (Pst *aPost, ZnMngmt *aCfm);

static short iNGwSmZnTrcInd (Pst *aPost, ZnMngmt *aCfm);


//Configuration Confirm Handler of PSF-M3UA Layer
static short iNGwSmZvCtlCfm (Pst *aPost, ZvMngmt *aCfm);

static short iNGwSmZvStaCfm (Pst *aPost, ZvMngmt *aCfm);

static short iNGwSmZvStsCfm (Pst *aPost, ZvMngmt *aCfm);

static short iNGwSmZvCfgCfm (Pst *aPost, ZvMngmt *aCfm);

static short iNGwSmZvCntrlCfm (Pst *aPost, ZvMngmt *aCfm);

static short iNGwSmZvStaInd (Pst *aPost, ZvMngmt *aCfm);

static short iNGwSmZvTrcInd (Pst *aPost, ZvMngmt *aCfm);

/*
 * handler function for SCCP
 */

//activation function for SCCP Layer
static short iNGwSmSpActvTsk (Pst *aPost, Buffer *apBuf);

//Configuration Confirm Handler of SCCP Layer
static short iNGwSmSpCfgCfm (Pst *aPost, SpMngmt *aCfm);

//Control Confirm handler of SCCP Layer
static short iNGwSmSpCtlCfm (Pst *aPost, SpMngmt *aCfm);

//Status Confirm handler of SCCP Layer
static short iNGwSmSpStaCfm (Pst *aPost, SpMngmt *aCfm);

//Statistics Confirm handler of SCCP Layer
static short iNGwSmSpStsCfm (Pst *aPost, Action aAct, SpMngmt *aCfm);

//Trace Indication handler of SCCP Layer
static short iNGwSmSpTrcInd (Pst *aPost, SpMngmt *aCfm);

//Unsolicited Status Indication of SCCP Layer
static short iNGwSmSpStaInd (Pst *aPost, SpMngmt *aCfm);


/*
 * handler function for M3UA
 */

//activation function for M3UA
static short iNGwSmItActvTsk (Pst *aPost, Buffer *apBuf);

//Configuration Confirm Handler of M3UA Layer
static short iNGwSmItCfgCfm (Pst *aPost, ItMgmt *aCfm);

//Control Confirm handler of M3UA Layer
static short iNGwSmItCtlCfm (Pst *aPost, ItMgmt *aCfm);

//Status Confirm handler of M3UA Layer
static short iNGwSmItStaCfm (Pst *aPost, ItMgmt *aCfm);

//Statistics Confirm handler of M3UA Layer
static short iNGwSmItStsCfm (Pst *aPost, ItMgmt *aCfm);

//Trace Indication handler of M3UA Layer
static short iNGwSmItTrcInd (Pst *aPost, ItMgmt *aCfm);

//Unsolicited Status Indication of M3UA Layer
static short iNGwSmItStaInd (Pst *aPost, ItMgmt *aCfm);



//activation function for LDF M3UA Layer
static short iNGwSmDvActvTsk (Pst *aPost, Buffer *apBuf);

//Configuration Confirm Handler of LDF M3UA Layer
static short iNGwSmDvCfgCfm (Pst *aPost, LdvMngmt *aCfm);

//Control Confirm Handler of LDF M3UA Layer
static short iNGwSmDvCtlCfm (Pst *aPost, LdvMngmt *aCfm);

static short iNGwSmDvStsCfm (Pst *aPost, LdvMngmt *aCfm);

static short iNGwSmDvStaInd (Pst *aPost, LdvMngmt *aCfm);

static short iNGwSmDvStaCfm (Pst *aPost, LdvMngmt *aCfm);

static short iNGwSmDvTrcInd (Pst *aPost, LdvMngmt *aCfm);


/*
 * handler function for SCTP
 */

//activation function for SCTP
static short iNGwSmSbActvTsk (Pst *aPost, Buffer *apBuf);

//Configuration Confirm Handler of SCTP Layer
static short iNGwSmSbCfgCfm (Pst *aPost, SbMgmt *aCfm);

//Control Confirm handler of SCTP Layer
static short iNGwSmSbCtlCfm (Pst *aPost, SbMgmt *aCfm);

//Status Confirm handler of SCTP Layer
static short iNGwSmSbStaCfm (Pst *aPost, SbMgmt *aCfm);

//Statistics Confirm handler of SCTP Layer
static short iNGwSmSbStsCfm (Pst *aPost, SbMgmt *aCfm);

//Trace Indication handler of SCTP Layer
static short iNGwSmSbTrcInd (Pst *aPost, SbMgmt *aCfm);

//Unsolicited Status Indication of SCTP Layer
static short iNGwSmSbStaInd (Pst *aPost, SbMgmt *aCfm);


/*
 * handler function for TUCL
 */

//activation function for TUCL Layer
static short iNGwSmHiActvTsk (Pst *aPost, Buffer *apBuf);

//Configuration Confirm Handler of TUCL Layer
static short iNGwSmHiCfgCfm (Pst *aPost, HiMngmt *aCfm);

//Control Confirm handler of TUCL Layer
static short iNGwSmHiCtlCfm (Pst *aPost, HiMngmt *aCfm);

//Status Confirm handler of TUCL Layer
static short iNGwSmHiStaCfm (Pst *aPost, HiMngmt *aCfm);

//Statistics Confirm handler of TUCL Layer
static short iNGwSmHiStsCfm (Pst *aPost, HiMngmt *aCfm);

//Trace Indication handler of TUCL Layer
static short iNGwSmHiTrcInd (Pst *aPost, HiMngmt *aTrc, Buffer *apBuf);

//Unsolicited Status Indication of TUCL Layer
static short iNGwSmHiStaInd (Pst *aPost, HiMngmt *aUsta);

/*
 * handler function for LDF MTP3
 */

//activation function for MTP3 Layer
static short iNGwSmDnActvTsk (Pst *aPost, Buffer *apBuf);

//Configuration Confirm Handler of MTP3 Layer
static short iNGwSmDnCfgCfm (Pst *aPost, LdnMngmt *aCfm);

//Control Confirm handler of PSF MTP3 Layer
static short iNGwSmDnCtlCfm (Pst *aPost, LdnMngmt *aCfm);


/*
 * handler function for MTP3
 */

//activation function for MTP3 Layer
static short iNGwSmSnActvTsk (Pst *aPost, Buffer *apBuf);

//Configuration Confirm Handler of MTP3 Layer
static short iNGwSmSnCfgCfm (Pst *aPost, SnMngmt *aCfm);

//Control Confirm handler of MTP3 Layer
static short iNGwSmSnCtlCfm (Pst *aPost, SnMngmt *aCfm);

//Status Confirm handler of MTP3 Layer
static short iNGwSmSnStaCfm (Pst *aPost, SnMngmt *aCfm);

//Statistics Confirm handler of MTP3 Layer
static short iNGwSmSnStsCfm (Pst *aPost, Action action, SnMngmt *aCfm);

//Trace Indication handler of MTP3 Layer
static short iNGwSmSnTrcInd (Pst *aPost, SnMngmt *aTrc);

//Unsolicited Status Indication of MTP3 Layer
static short iNGwSmSnStaInd (Pst *aPost, SnMngmt *aUsta);



/*
 * handler function for MTP2
 */

//activation function for TUCL Layer
static short iNGwSmSdActvTsk (Pst *aPost, Buffer *apBuf);

//Configuration Confirm Handler of TUCL Layer
static short iNGwSmSdCfgCfm (Pst *aPost, SdMngmt *aCfm);

//Control Confirm handler of TUCL Layer
static short iNGwSmSdCtlCfm (Pst *aPost, SdMngmt *aCfm);

//Status Confirm handler of TUCL Layer
static short iNGwSmSdStaCfm (Pst *aPost, SdMngmt *aCfm);

//Statistics Confirm handler of TUCL Layer
static short iNGwSmSdStsCfm (Pst *aPost, Action action, SdMngmt *aCfm);

//Trace Indication handler of TUCL Layer
static short iNGwSmSdTrcInd (Pst *aPost, SdMngmt *aTrc);

//Unsolicited Status Indication of TUCL Layer
static short iNGwSmSdStaInd (Pst *aPost, SdMngmt *aUsta);

//activation function for RY   Layer
static short iNGwSmRyActvTsk (Pst *aPost, Buffer *apBuf);

static short iNGwSmRyCfgCfm (Pst *aPost, RyMngmt *aUsta);

static short iNGwSmRyStsCfm (Pst *aPost, RyMngmt *aUsta);

static short iNGwSmRyCtlCfm (Pst *aPost, RyMngmt *aUsta);

static short iNGwSmRyStaCfm (Pst *aPost, RyMngmt *aUsta);

static short iNGwSmRyStaInd (Pst *aPost, RyMngmt *aUsta);

static short iNGwSmRyTrcInd (Pst *aPost, RyMngmt *aUsta);


#ifdef __cplusplus
#ifndef __CCPU_CPLUSPLUS
}
#endif
#endif 



#endif /* __BP_AINSMADAPTOR_H__ */

/************************************************************************
     Name:     INAP Stack Manager Alarm Handler - defines
 
     Type:     C include file
 
     Desc:     Defines required to access Alarm Handler

     File:     INGwSmAlmHdlr.h

     Sid:      INGwSmAlmHdlr.h 0  -  03/27/03 

     Prg:      bd

************************************************************************/

#ifndef __BP_AINSMALMHDLR_H__
#define __BP_AINSMALMHDLR_H__

//include the various header files
#include "INGwStackManager/INGwSmIncludes.h"
#include "INGwStackManager/INGwSmAlmCodes.h"
#include "INGwStackManager/INGwSmAdaptor.h"


/*
 * This class defines the object to be created for alarm handling
 * of different layers.
 *
 * NOTE: although not implemented as a singleton, there would only
 * be a single instance per-Distributor (created by Distributor)
 * of this class.
 */

class INGwSmAlmHdlr
{

  /*
   * Public interface
   */
  public:

  //default constructor
  INGwSmAlmHdlr(INGwSmDistributor* arDist);

  //default destructor
  ~INGwSmAlmHdlr();

  //initialize the handler
  int initialize();

  //process the alarm 
  int handleAlarm(INGwSmAlarmMsg *apAlmMsg);

#ifdef INC_ASP_SNDDAUD
  int sendDaud(ItPsId psId);
#endif


  /*
   * Protected interface
   */
  protected:

  //int handleIeAlarm(IeMngmt *mgmt);  // handle INAP alarms

  int handleStAlarm(StMngmt *mgmt);  // handle TCAP alarms

  int handleSpAlarm(SpMngmt *mgmt);  // handle SCCP alarms

  int handleItAlarm(ItMgmt  *mgmt);  // handle M3UA alarms

  int handleSbAlarm(SbMgmt  *mgmt);  // handle SCTP alarms

  int handleHiAlarm(HiMngmt *mgmt);  // handle TUCL alarms

  int handleSnAlarm(SnMngmt *mgmt);  // handle MTP3 alarms

  int handleSdAlarm(SdMngmt *mgmt);  // handle MTP2 alarms

  int handleDvAlarm(LdvMngmt *mgmt); // handle LDF-M3UA alarms

  int handleDnAlarm(LdnMngmt *mgmt); // handle LDF-MTP3 alarms

  int handleZtAlarm(ZtMngmt *mgmt);  // handle PSF-TCAP alarms

  int handleZpAlarm(ZpMngmt *mgmt);  // handle PSF-SCCP alarms

  int handleZnAlarm(ZnMngmt *mgmt);  // handle PSF-MTP3 alarms

  int handleZvAlarm(ZvMngmt *mgmt);  // handle PSF-M3UA alarms

  int handleSgAlarm(SgMngmt *mgmt);  // handle SG alarms

  int handleShAlarm(ShMngmt *mgmt);  // handle SH alarms

  int handleMrAlarm(MrMngmt *mgmt);  // handle MR alarms

  int handleRyAlarm(RyMngmt *mgmt);  // handle RY alarms

  int handleAsPendingAlm();

  int handleAspmAckCase(ItMgmt *apAspm);

  int handlePspAlmOp(INGwSmAlmOpType aAlmOp, ItPspId aPspId, ItPsId aPsId, SuId asctSuId);

  int handleCommDownAlm(INGwSmCommDownCause aCause, ItPspId aPspId, SuId asctSuId);

  int sendAspActvCmd(int pspid, int sctsuid);
  int sendInitReq(int pspid, int sctsuid);
  //int sendAspUpCmd(int pspid, int sctsuid);


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

  INGwSmAlarmMsg     *mpLastAlmMsg; // not used currently

  INGwSmDistributor  *mpDist;


  /*
   * Private Data Members
   */
  private:

     bool _exitOnFailureFlag;

   private:

     void _checkFailureExit();


};


/* Macros to convert from Integrated value to layer-specific value */
#define MLayerEventFromInteg(integ)       (integ) & 0x000003ff
#define MLayerCauseFromInteg(integ)       ((integ) & 0x000ffc00) >> 10
#define MLayerIdFromInteg(integ)          ((integ) & 0x07f00000) >> 20
#define MSpareFromInteg(integ)            ((integ) & 0xf8000000) >> 27

/* Macros to convert from layer-specific value to Integrated value */
#define MLayerEventIntoInteg(integ,event)  (integ) |= (event)
#define MLayerCauseIntoInteg(integ,cause)  (integ) |= ((cause) << 10)
#define MLayerIdIntoInteg(integ,layer)     (integ) |= ((layer) << 20)
#define MSpareIntoInteg(integ,spare)       (integ) |= ((spare) << 27)

/* Macro to prepare the integrated value from layer-specific values */
#define MMkIntegVal(event,cause,layer)  \
( (event) | ((cause) << 10) | ((layer) << 20) )

/* Macro to prepare the integrated value from layer-specific values (4 args) */
#define MMkIntegVal4(event,cause,layer,spare)  \
( (event) | ((cause) << 10) | ((layer) << 20) | ((spare) << 27) )

/* Macro to assign the integrated value from layer-specific values */
#define MAssignIntegVal(integ,event,cause,layer,spare)  \
(integ) = (event) | ((cause) << 10) | ((layer) << 20) | ((spare) << 27)

// default value of the spare field in integrated alarm code
#define INTALM_SPARE_DEFLT  0


#endif /* __BP_AINSMALMHDLR_H__ */

/************************************************************************
     Name:     INAP Stack Manager Request Handler - defines
 
     Type:     C include file
 
     Desc:     Defines required to access Request Handler

     File:     INGwSmReqHdlr.h

     Sid:      INGwSmReqHdlr.h 0  -  03/27/03 

     Prg:      gs

************************************************************************/

#ifndef __BP_AINSMREQHDLR_H__
#define __BP_AINSMREQHDLR_H__

//include the various header files
#include "INGwStackManager/INGwSmIncludes.h"

/*
 * This abstract class is used to derive the concrete classes for
 * handling requests of different layers .
 */

class INGwSmReqHdlr
{

  /*
   * Public interface
   */
  public:

  //default constructor
  INGwSmReqHdlr(INGwSmDistributor &aeDist, int aiLayer);

  //default destructor
  virtual ~INGwSmReqHdlr();

  virtual int sendRequest (INGwSmQueueMsg *apMsg = 0,INGwSmRequestContext *apContext = 0);

  INGwSmRequestContext * getRequestContext () { return &meReqContext; }

  virtual int handleResponse (INGwSmQueueMsg *apMsg);

  /*
   * Protected interface
   */
  protected:

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

  INGwSmDistributor &mrDist;

  //layer type
  int miLayer;

  //transaction Id
  int miTransId;

  //Context used for matching alarms
  INGwSmRequestContext meReqContext;

  // As we keep sending request to stack we will increment it
  // and when we receive certain response from stack we shall 
  // decrement it, when the count reaches 0, then we shall scan 
  // the map StackReqRespMap for the responses received from 
  // stack and send appropriate response back to EMS.
  // It is assumed that there shall be only one request active from EMS
//  int ss7SigtranStackRespPending;



  union 
  {
    /* IeMngmt ie;*/
    StMngmt st;
    SpMngmt sp;
    ItMgmt it;
    SbMgmt sb;
    HiMngmt hi;
    SnMngmt sn;
    SdMngmt sd;
    SgMngmt sg;
    ShMngmt sh;
    MrMngmt mr;
    ZpMngmt zp;
    ZtMngmt zt;
    ZvMngmt zv;
    ZnMngmt zn;
    LdnMngmt dn;
    LdvMngmt dv;
    RyMngmt  ry;
  } l;

  /*
   * Private Data Members
   */
  private:

};

#endif /* __BP_AINSMREQHDLR_H__ */

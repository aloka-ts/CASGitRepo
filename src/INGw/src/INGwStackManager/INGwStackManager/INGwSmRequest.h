/************************************************************************
     Name:     INAP Stack Manager Request Object - defines
 
     Type:     C include file
 
     Desc:     Defines required for creating the request objest
               which will be created per request from CCM

     File:     INGwSmRequest.h

     Sid:      INGwSmRequest.h 0  -  03/27/03 

     Prg:      gs,bd

************************************************************************/

#ifndef __BP_AINSMREQUEST_H__
#define __BP_AINSMREQUEST_H__

//include the various header files
#include "INGwStackManager/INGwSmIncludes.h"
#include "INGwStackManager/INGwSmStkReqRespDat.h"

class INGwSmRepository;
class INGwSmReqHdlr;
class INGwSmDistributor;

/*
 * This is the message which will be created per request
 */

class INGwSmRequest
{
  /*
   * Public interface
   */
  public:

  //c'tor
  INGwSmRequest(int aiTransId, INGwSmQueueMsg *apOrigMsg, 
                 INGwSmDistributor* apDistributor);

  //d'tor
  ~INGwSmRequest();

  //start processing scenario.
  int  start(int aiScenario, INGwSmQueueMsg *apQueMsg=0, INGwSmRequestContext *apContext = 0);
  
  //handle stack response to last operation
  int handleResponse(INGwSmQueueMsg *apFromStack);

  //handle alarm occurance if the request is waiting for alarm
  int handleAlarm (INGwSmQueueMsg *apAlmMsg, int aiAlarmId);

  //get the blocking context to update the returncode for CCM
  INGwSmBlockingContext *getBlockingContext(int &aiScenario) ;

  //dump the information about the request
  int dump ();

  int rollBack(StackReqResp *rb);

  int updStkRspMap(INGwSmStackMsg *smStkMsg);
  /* 
   * Protected interface
   */
  protected:

  //decode the state and retrieve layer and operation
  int decodeState (int aiState, int &aiLayer, int &aiOper, int &aiSubOp);

  //create the Request Handler
  int createRequestHdlr (int aiLayer, int aiOper, int aiSubOp);

  //send the request to the stack
  int sendRequest (int aiLastReturnValue = -1);

  /*
   * Private interface
   */
  private:

  /*
   * Public Data Members
   */
  public:
  
  INGwSmQueueMsg *mpQueMsg;
  //the orignal request message from CCM
  INGwSmQueueMsg  *mpOrigReq;
  //transactionId to be created to correlate between request and response
  int miTransactionId;
  /* 
   * Protected Data Members
   */
  protected:

  //define the blocking context for the CCM request
  INGwSmBlockingContext meBlockCtx;


  //scenario for this request
  int miScenario;

  //majorState defines the scenario
  int miMajorState;

  //minorState defines the operation on the stack
  int miMinorState;


  //the ptr to appropriate (Ctrl,Cfg,Sts,Sta) request handler
  INGwSmReqHdlr *mpReqHdlr;
	bool          m_isReqHdlrForStats;

  //repository reference
  INGwSmRepository *mpRepository;

  //state vector
  INGwSmIntVector meStateVector;

  //store the request Context here
  INGwSmRequestContext *mpReqContext;

  //INGwSmQueueMsg *mpQueMsg;
  //is the request waiting for some alarm
  int miIsWaitingForAlarm;

  /* 
   * Private Data Members
   */
  private:

  //the INGwSmDistributor
  INGwSmDistributor *mpDist;

};

#endif /* __BP_AINSMREQUEST_H__ */

//*********************************************************************
//   GENBAND, Inc. Confidential and Proprietary
//
// This work contains valuable confidential and proprietary
// information.
// Disclosure, use or reproduction without the written authorization of
// GENBAND, Inc. is prohibited.  This unpublished work by GENBAND, Inc.
// is protected by the laws of the United States and other countries.
// If publication of the work should occur the following notice shall
// apply:
//
// "Copyright 2007 GENBAND, Inc.  All rights reserved."
//*********************************************************************
//********************************************************************
//
//     File:     INGwSpSipCallController.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef INGW_SP_CALL_CONTROLLER_H_
#define INGW_SP_CALL_CONTROLLER_H_

#include <pthread.h>
#include <map>
#include <string>

#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpSipConnection.h>
#include <INGwSipProvider/INGwSpSipIface.h>
#include <INGwSipProvider/INGwSpSipCallMap.h>

#include <INGwInfraUtil/INGwIfrUtlGlbInclude.h>
#include <INGwInfraManager/INGwIfrMgrWorkerClbkIntf.h>
#include <INGwInfraStatMgr/INGwIfrSmStatMgr.h>

class INGwIfrMgrWorkUnit;

class INGwSpSipCallController : public INGwIfrMgrWorkerClbkIntf
{
  public :

		INGwSpSipCallController(INGwSpSipIface* p_SipIface);
		INGwSpSipCallController(const INGwSpSipCallController& selfObj);
		INGwSpSipCallController & operator = (const INGwSpSipCallController& selfObj);
	
    /************SAS End Point MAP manipulation Iface************/

		//It will make copy of end point info and save in its map
		//Retrun 0 if successful else return -1
		int addEndPoint(const INGwSipEPInfo& p_SipEPInfo);

		//This function will remove the entry from the map based on the input Host.
		//Retrun 0 if successful(entry found and removed) else return -1
		int removeEndPoint(std::string& p_EPHost, std::string& p_CallId);

		//This function will copy the end point info in the in/out parameter
		//Retrun 0 if successful(entry found and copied) else return -1
		int getEndPoint(std::string& p_EPHost, INGwSipEPInfo& p_SipEPInfo);

		//This function will update remote client port of EP info
		//Retrun 0 if successful(entry found and updated) else return -1
		int updateEndPoint(std::string& p_EPHost, unsigned short clientport);

    //This function will get the list of all registered End Point
    //Retrun number of entry in the list
    // List will have have EP object allocated, caller needs to free the memory
    int
    getEndPointList(t_EPInfoList& p_EPInfoList);

		std::string toLogEpMap();

    /************SIP Call MAP manipulation Iface************/

		//Function will take a ref and save in its map
		//Retrun 0 if successful else return -1
		int addCall(std::string& p_CallIdStr, INGwSpSipCall* p_SipCall);

		//Function will remove the entry from the map and will remove a ref
		//Retrun 0 if successful(entry found and removed) else return -1
		int removeCall(std::string& p_CallIdStr);

		//Function will take a raf and return call ptr if found
		//Retrun 0 if successful(entry found and copied) else return -1
		INGwSpSipCall* getCall(std::string& p_CallIdStr);


		/*********   Function called by Sip message handlers ************/
		/*********   to interact with difeerent interface    ************/
		/*********   to complete the flow control            ************/

		//
		//When INVITE is recv
		//
		int processSasServerRegistration(INGwSpSipConnection* p_Connection);

		//
		//When BYE is recv
		//
		int processSasServerDeregistration(INGwSpSipConnection* p_Connection);

		//
		//When INFO is recv
		//
		int processSasAppMgmtRequest(INGwSpSipConnection* p_Connection, 
														 const void* p_Sdp, const int p_SdpLen);

		//
		//When NOTIFY is recv
		//
    int processOutboundMsg(INGwSpSipConnection* p_Connection,
													 const void* p_Sdp, const int p_SdpLen,
                           int p_seqNum);

		//
		//When OPTIONS is recv
		//
    int processHBMsg(INGwSpSipConnection* p_Connection);
										 
    //Functions called by interface to interact with message handler and sent 
		// it to end point.

		//
		//When notify needed to be sent
		//
    int handleInboundMsg(g_TransitObj  &p_transitObj);

		//
		//When Replay notify is needed to be sent
		//
    int handleReplayInboundMsg(g_TransitObj  &p_transitObj);

		//
		//When INFO needed to be sent
		//
		int handleSasAppMgmtResponse(g_TransitObj  &p_transitObj);

		//
		//When INFO request failed 
		//408 will used to indicate timeout
		//
		int handleSasAppMgmtMsgFailure(int p_ResponseCode, std::string& p_CallId);

		//
		//When notify request failed 
		//408 will used to indicate timeout
		//
		int handleInboundMsgFailure(int p_ResponseCode, INGwSpSipConnection* p_Conn);

		//
		//Handle work posted
		//
		int
    handleWorkerClbk(INGwIfrMgrWorkUnit* p_Work);

    // Replicate the call object for registered Endpoint
    void
    replicateEpToPeer();

		int
		cleanSipCall(INGwSipEPInfo& p_SipEPInfo);

#ifdef INGW_LOOPBACK_SAS
    typedef struct _lbDlgInfoStruct {
      int  msgCnt;

      _lbDlgInfoStruct() {
        msgCnt = 0;
      }
    } LbDlgInfoStruct;

    int m_lowDlgId;
    int m_highDlgId;

    LbDlgInfoStruct ** loopbackDlgInfo;

    //LbDlgInfoStruct** getLoopbackDlgInfo();
    void initLoopbackDlgInfo(int p_loDlg, int p_hiDlg);
    void getMsgBuf(char * envVar, g_TransitObj & transitObj);

#endif


  private :
    long mNwAliveTime;
		void getNwAliveTime(long pNwAlivetime = -1);
		sendInfoRequest(g_TransitObj  &p_transitObj);

    int
		sendNotifyRequest(g_TransitObj  &p_transitObj, char* p_CallId);

		int 
		startHBTimer(INGwSipEPInfo& p_EPInfo, int p_TimeoutCount = 0);

		int
		stopHBTimer(INGwSipEPInfo& p_EPInfo);

		int 
		handleHBTimeOut(INGwSipHBTimerContext* p_HBTimerContext);

		int
		handleSasHBFailure(INGwSipEPInfo& p_SipEPInfo);

    // It will send a BYE request, 
		//int
		//cleanSipCall(INGwSipEPInfo& p_SipEPInfo);


    // remove EP from its map and notify IWF for SAS deregistration
		int
		cleanSasEpRegistration(INGwSipEPInfo& p_SipEPInfo);

		int
		handlePeerINGWCallMsg(INGwIfrMgrWorkUnit* apWork);

		SipEPInfoMap m_SipEPInfoMap;
		INGwSpSipCallMap m_SipCallMap;
		INGwSpSipIface* m_SipIface;
	
};

#endif //INGW_SP_CALL_CONTROLLER_H_


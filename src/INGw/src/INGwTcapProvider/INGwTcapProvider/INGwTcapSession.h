#include <INGwCommonTypes/INCCommons.h>
#include <INGwTcapProvider/INGwTcapWorkUnitMsg.h>
#include <map>
#ifndef _INGW_TCAP_SESSION_H_
#define _INGW_TCAP_SESSION_H_
typedef struct t_INGwTcapWorkUnitMsg INGwTcapWorkUnitMsg;
typedef int OutBndSeqNum;
/*
Yogesh:INGwTcapSession is a container class that captures all the relevant data
in a Tcap Session. 

INGwTcapFtHandler::m_tcapSessionMap
            <dialogueId,INgwTcapSession>
                          ^ 
                          |
                          |
                   INgwTcapSession<-------sasIp, SSN,
                      ^       ^           major state
                     /         \          minor state       
                    /           \         suId, spId
                   /             \        sasIp, src/dstAddr,
                  /               \       latestSeqNum.
                 /                 \
           m_InbdlgMsgMap          m_outboundMsgMap 
<inbSeqNum,INGwTcapFtMsg>          <outbSeqNum,INGwTcOutMsgContainer>
                ^                    ^
                |                    |
                |                    |
          INGwTcapFtMsg            INGwTcapWorkUnitMsg
                ^                    ^
                |                    |
                |                    |
       encodedBuf, buflen           decoded structures to be 
       to be sent to Sas            sent to SS7-SIG stack 
*/
enum INGwTcSessionMinorState {
  TC_MINSTATE_INVALID,
  TC_MINSTATE_INBOUND_RX,        
  TC_MINSTATE_INBOUND_RX_DONE,
  TC_MINSTATE_OUTBOUND_TX,
  TC_MINSTATE_OUTBOUND_TX_DONE,

  TC_MINSTATE_FTSYNCH_INBOUND_RX,        
  TC_MINSTATE_FTSYNCH_OUTBOUND_TX
};

enum INGwTcSessionMajorState 
{
  TC_MAJOR_STATE_INVALID,
  TC_CONN_CREATED,
  TC_CONN_CONNECTED,
  TC_CONN_DISCONNECTED,
  TC_CONN_FAILED,
  TC_CONN_SYNCH_IN_PRGRS,  //tuai is missing
  TC_CONN_SYNCH_DONE
};
//adding redundent data to trade-off CPU 
//consumption with memory

enum INGwTcTransactionState
{
  TC_BEGIN,
  TC_CONT,
  TC_U_ABRT,
  TC_P_ABRT,
  TC_UNI,
  TC_END
};

class INGwTcapFtMsg{
  private:

  INGwTcSessionMinorState mMinorState; 
  public:

  U8* m_buf;
  int m_bufLen;
  int m_seqNum;

  INGwTcapFtMsg(U8* p_buf, int p_bufLen, int p_seqNum);

  ~INGwTcapFtMsg();

  bool isValid();
  bool isLastMsg(U8 p_dlgtype = 0);
};

class INGwTcOutMsgContainer{
  private:

  public:

  INGwTcapWorkUnitMsg *mpWorkUnitMsg;
  t_tcapMsgBuffer     *mtcapMsgInfo;

  INGwTcOutMsgContainer() {
    mpWorkUnitMsg = NULL;
    mtcapMsgInfo = NULL;
  }

  INGwTcOutMsgContainer(INGwTcapWorkUnitMsg *apWorkUnitMsg) {
    mpWorkUnitMsg = apWorkUnitMsg;
    mtcapMsgInfo = NULL;
  }

  INGwTcOutMsgContainer(t_tcapMsgBuffer *aptcapMsgInfo) {
    mtcapMsgInfo = aptcapMsgInfo;
    mpWorkUnitMsg = NULL;
  }

  ~INGwTcOutMsgContainer() {

  }

  INGwTcOutMsgContainer(const INGwTcOutMsgContainer &pObj);
};

class INGwTcapFtMsg;

class INGwTcapSession{

  private:

  INGwTcSessionMinorState mMinorState; 
  INGwTcSessionMajorState mMajorState;
  string m_sasIp; 
  INGwFtPktTcapMsg* m_initialInfo;
  U8 mTransState;
  int miBillingNo;
  public:

  U8 m_ssn;

  void 
  setBillingNo(int & piBillingNo);

  int 
  getBillingNo();

  void 
  setInitialInfo(INGwFtPktTcapMsg* pInitialInfo);

  U8
  getTcTransState();   

  void
  setTcTransState(U8 pTransState);

  INGwFtPktTcapMsg*
  getInitialInfo();

  AppInstId m_appInstId;    
  
  string 
  getSasIp();

  void
  setSasIp(string pSasIp);

  void
  setSasIp(char* pIpBuf);

  INGwTcSessionMinorState 
  getMinorState();

  INGwTcSessionMajorState 
  getMajorState();

  void 
  setMinorState(INGwTcSessionMinorState p_minorState);

  void 
  setMajorState(INGwTcSessionMajorState p_majorState);

  int 
  cleanTcapSession(bool p_isReplicated = false);

  //this will store the Tuai in tcap session, and also in  
  int
  storeUserInfoInSession();

  //to store different messages in a dialogue,key here is the sequence Id of 
  //message generated at the time of replication 
  map<int,INGwTcapFtMsg*>             m_InbdlgMsgMap;

  //making this void pointer to contain both byteBuffer and WorkUnit
  map<int, INGwTcOutMsgContainer*>    m_outboundMsgMap;

  INGwTcapSession(AppInstId p_appInstId, U8 p_ssn, INGwTcSessionMinorState p_state);
  
  INGwTcapSession(INGwTcSessionMinorState p_minState,
                  INGwTcSessionMajorState p_majorState);

  INGwTcapSession();

  ~INGwTcapSession();
  
};
#endif //_INGW_TCAP_SESSION_H_

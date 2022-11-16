#include <INGwTcapProvider/INGwTcapSession.h>
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwTcapSession");
#include <INGwTcapProvider/INGwTcapProvider.h>
#define CLEAN_BUF

INGwTcapFtMsg:: INGwTcapFtMsg(U8* p_buf, int p_bufLen, int p_seqNum)
{
    m_buf    = p_buf;
    m_bufLen = p_bufLen;
    m_seqNum = p_seqNum;
}

INGwTcapFtMsg::~INGwTcapFtMsg(void)
{
}

bool
INGwTcapFtMsg::isValid()
{
  if(NULL == m_buf){
    return false;
  }
  
  return true;
}

//end 3, abort 4, notice 0x72, 
bool
INGwTcapFtMsg::isLastMsg(U8 p_dlgtype)
{
  bool retVal = false;
  int lLenOffset = 0;
  if(0x00 != (m_buf[1] & 0x80)){
    lLenOffset = (m_buf[1]) - 128;
  }
  p_dlgtype = m_buf[2 + lLenOffset];

  if(INC_END == p_dlgtype   || INC_U_ABORT == p_dlgtype 
     || INC_P_ABORT == p_dlgtype || INC_NOTICE == p_dlgtype)
  {
    retVal = true;
  }
  return retVal;
}

INGwTcapSession:: INGwTcapSession( AppInstId p_appInstId, 
                         U8 p_ssn, INGwTcSessionMinorState p_state)
{
  mMinorState = p_state;
  memcpy(&m_appInstId,&p_appInstId, sizeof(AppInstId));
  m_ssn = p_ssn;
  m_initialInfo = NULL;
}
  
INGwTcapSession::INGwTcapSession() 
{
  mMinorState = TC_MINSTATE_INVALID;
  mMajorState = TC_MAJOR_STATE_INVALID;
  miBillingNo = 0;
  m_initialInfo = NULL;
}

INGwTcapSession::INGwTcapSession(INGwTcSessionMinorState p_minState,
                                 INGwTcSessionMajorState p_majorState) 
{
  mMinorState = p_minState;
  mMajorState = p_majorState;
  miBillingNo = 0;
  m_initialInfo = NULL;
}

INGwTcapSession:: ~INGwTcapSession(){

}

void INGwTcapSession::setBillingNo(int & piBillingNo)
{
  miBillingNo = piBillingNo;
}

int INGwTcapSession::getBillingNo()
{
  return miBillingNo;
}

int 
INGwTcapSession::cleanTcapSession(bool p_isReplicated) 
{

 if(NULL != m_initialInfo) {
   delete m_initialInfo;
   m_initialInfo = NULL;
 }

 map<int,INGwTcapFtMsg*>::iterator iterIn;
 for(iterIn = m_InbdlgMsgMap.begin(); iterIn != m_InbdlgMsgMap.end(); 
                                                                    iterIn++)
 {
   if(0 != (iterIn->second))
   {
     if(iterIn->second->m_buf)
     delete [] iterIn->second->m_buf;
     iterIn->second->m_buf = 0;
     delete iterIn->second;
     iterIn->second = 0;
   }
 } 
 m_InbdlgMsgMap.clear(); 

 map<int, INGwTcOutMsgContainer*>::iterator iterOut;

 INGwTcOutMsgContainer* lpContainer = NULL;
 
 for(iterOut = m_outboundMsgMap.begin();
       iterOut!= m_outboundMsgMap.end(); iterOut++) {
     lpContainer = iterOut->second;

     if(p_isReplicated) { 
     INGwTcapWorkUnitMsg  * lWorkUnit = NULL;
     lWorkUnit =  lpContainer->mpWorkUnitMsg;

     if(NULL != lWorkUnit)
     {
       INGwTcapProvider::getInstance().getAinSilTxRef().
                                      releaseWorkUnit(lWorkUnit);
       delete lWorkUnit->m_tcapMsg;
       lWorkUnit->m_tcapMsg = 0;
    
       delete lWorkUnit; lWorkUnit = 0;
     }
   } else {
     t_tcapMsgBuffer* ltcapMsgInfo =  lpContainer->mtcapMsgInfo;
     if(NULL != ltcapMsgInfo) {
       if(NULL != ltcapMsgInfo->m_buf) {
         delete [] ltcapMsgInfo->m_buf;
         delete ltcapMsgInfo;
       }
     }
   }
   delete lpContainer;        
 }
 m_outboundMsgMap.clear();
 return G_SUCCESS;
}

void 
INGwTcapSession::setMinorState(INGwTcSessionMinorState p_state)
{
  mMinorState = p_state;
}

void 
INGwTcapSession::setMajorState(INGwTcSessionMajorState p_state)
{
  mMajorState = p_state;
}

INGwTcSessionMinorState 
INGwTcapSession::getMinorState()
{
  return mMinorState;
}

INGwTcSessionMajorState 
INGwTcapSession::getMajorState()
{
  return mMajorState;
}

void
INGwTcapSession::setSasIp(string pSasIp){
 logger.logINGwMsg(false,TRACE_FLAG,0,"In INGwTcapSession::setSasIp <%s>",
                                       pSasIp.c_str());
  m_sasIp = pSasIp;

 logger.logINGwMsg(false,TRACE_FLAG,0,"Out INGwTcapSession::setSasIp");
}

void
INGwTcapSession::setSasIp(char* pIpBuf){
 logger.logINGwMsg(false,TRACE_FLAG,0,"In INGwTcapSession::setSasIp <%s>",
                                      pIpBuf); 
  m_sasIp = pIpBuf;

 logger.logINGwMsg(false,TRACE_FLAG,0,"Out INGwTcapSession::setSasIp");
}

string
INGwTcapSession::getSasIp() {
 logger.logINGwMsg(false,TRACE_FLAG,0,"In INGwTcapSession::getSasIp");
 logger.logINGwMsg(false,TRACE_FLAG,0,"Out INGwTcapSession::getSasIp<%s>",
 m_sasIp.c_str());
 return m_sasIp;
}

void
INGwTcapSession::setInitialInfo(INGwFtPktTcapMsg* pInitialInfo){
 logger.logINGwMsg(false,TRACE_FLAG,0,"In INGwTcapSession::setInitialInfo()");
   m_initialInfo =  pInitialInfo;
 logger.logINGwMsg(false,TRACE_FLAG,0,"Out INGwTcapSession::setInitialInfo()");
}

INGwFtPktTcapMsg*
INGwTcapSession::getInitialInfo(){
  logger.logINGwMsg(false,TRACE_FLAG,0,"In INGwTcapSession::getInitialInfo()");
  if(NULL == m_initialInfo){
    logger.logINGwMsg(false,TRACE_FLAG,0,"InitialInfo : null");
  }
  logger.logINGwMsg(false,TRACE_FLAG,0,"Out INGwTcapSession::getInitialInfo()");
  return m_initialInfo; 
}

void
INGwTcapSession::setTcTransState(U8 pTransState){
  //remft
  logger.logINGwMsg(false,TRACE_FLAG,0,"In setTcTransState <%d>", pTransState);
    mTransState = pTransState;
  logger.logINGwMsg(false,TRACE_FLAG,0,"Out setTcTransState");
}

U8
INGwTcapSession::getTcTransState(){
  logger.logINGwMsg(false,TRACE_FLAG,0,"In getTcTransState"); 
  logger.logINGwMsg(false,TRACE_FLAG,0,"Out getTcTransState <%d>",mTransState);
  return mTransState;  
}

//void
//INGwTcapSession::setObStraySeqNum(int aiSeqNum) {
//
//  logger.logINGwMsg(false,ALWAYS_FLAG,0,"In setObStraySeqNum <%d>", aiSeqNum);
//
//    mvStrayMsg.push_back(aiSeqNum);
//
//  logger.logINGwMsg(false,ALWAYS_FLAG,0,"Out setObStraySeqNum");
//}

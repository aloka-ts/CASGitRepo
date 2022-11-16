#include "TcapMessage.hpp"
#ifndef PLANO_BUILD
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwTcapMessage");


#include "INGwInfraMsrMgr/MsrMgr.h"
#include "INGwTcapProvider/INGwTcapInclude.h"
#include "INGwTcapProvider/INGwTcapUtil.h"
#include "INGwTcapProvider/INGwTcapIncMsgHandler.h"
#include "INGwTcapProvider/INGwTcapProvider.h"
#include "INGwTcapProvider/INGwSilTx.h"
#include "INGwInfraUtil/INGwIfrUtlGlbFunc.h"

#include "INGwFtPacket/INGwFtPktTcapCallSeqAck.h"
#include "INGwTcapProvider/INGwTcapStatParam.h"
#include <INGwInfraStatMgr/INGwIfrSmStatMgr.h>
#include "INGwInfraParamRepository/INGwIfrPrParamRepository.h"
#include "INGwStackManager/INGwSmCommon.h"
#include <assert.h>
#endif

#ifdef PLANO_BUILD
#define VERBOSE_FLAG 0
#define ERROR_FLAG 0


class Logger
{
public:
        static void logINGwMsg(int,int,int,const char *fmt, ...)
        {
        }
} logger;

class _Stub
{
public:
        void increment(std::string, std::string, std::string)
        {
        }
        void decrement(std::string, std::string, std::string)
        {
        }

} Stub; 
 class MsrMgr
{
public:
 static _Stub *getInstance()
 {
 	return &Stub;
 }
};

#endif

#include <pthread.h>

// For debugging - Start [
extern int mActCallsThres;
extern bool actCallIncDiagTriggered;
extern bool g_enableDiagLogs;

extern int g_IncDiag(char * action, char * aFile, int aLine,
                     char * reason, int scenario);
// ] End - For debugging

static int giReplayCallCnt = 1;

static pthread_mutex_t mutex ;

// Yogesh initMutex to create static block
static initMutex initMutexLock(mutex);

INGwTcapMsgLogger* TcapMessage:: mLogger = NULL;
U8 TcapMessage::lcdlgCloseOpt = 1;

U8 TcapMessage::lcAbrtCause   =0x01;

int tcapLoDlgId;
int tcapHiDlgId;
int TcapMessage::mCodecLogLevel = 1;
#define LOCK()                                         \
  struct autoMUT { autoMUT() {                         \
    pthread_mutex_lock(&mutex);                        \
  }                                                    \
    ~autoMUT()                                         \
  {                                                    \
    pthread_mutex_unlock(&mutex);                      \
  } }at;                

inline void TcapMessage::getOctetCount(int pVal, U8 &pOctetLen) {
  pOctetLen= 0x00;
  while(pVal) 
  {
    pVal>>=8;
    pOctetLen++;
  }
}

inline U8* TcapMessage::getBuffer(int pSize){
  //get memory from a mem pool from here
  U8 *lBuf = new (nothrow)U8[pSize];
  mBufSize = pSize;
  return lBuf ;
}

void TcapMessage::encodeContentLength(U8* pBuf, int &pBufLen, int pIndex, int pContentLen) {
  if(pContentLen > 127)
  {             
    U8 lOctetLen = 0;           
    getOctetCount(pContentLen, lOctetLen); 

    if(pBufLen+lOctetLen > mBufSize)
    { 
      doReAllocation(&pBuf,pBufLen);      
    }                            

    int ltemp = (pBufLen - pIndex - 1);     
    pBuf[pIndex] = (U8)lOctetLen| 0x80;  

    //shifting the bytes after index by lOctetLen 
    for(int i=0;i<ltemp;i++){                        
      pBuf[pBufLen + lOctetLen - 1 - i] = pBuf[pBufLen-i-1];
    }                                                 

    for(int i=0;i<lOctetLen;i++){                     
      pBuf[pIndex+lOctetLen-i] = pContentLen & 0xff;         
      pContentLen = pContentLen >>8;                                 
    }     
     
    if(pBufLen == pIndex){                
      pBufLen++;                           
    }                                            

    pBufLen+=lOctetLen;                                
  }                                                 
  else
  {                                             
    if(pBufLen+1 > mBufSize){                              
      doReAllocation(&pBuf,pBufLen);      
    }                                                 
    pBuf[pIndex] = pContentLen;                          
  }                                     

  if(pBufLen == pIndex){                
    pBufLen++;                           
  }                                            
}

/*
  Will add the byte containing pByte at the end of the pBuffer.
  pLen is the length of the buffer hitherto, always points to the next available byte
*/
void TcapMessage::addByte(U8* pBuffer, int &pLen, U8 pByte)  {

  if(pLen > mBufSize) {        
    doReAllocation(&pBuffer, pLen);  
  }                         

  pBuffer[pLen++] =  pByte;
}



inline void TcapMessage::addInteger(U8* pBuf, int &pLen, int pVal) {

  if( (pLen + SIZE_OF_INT) > mBufSize) {
    doReAllocation(&pBuf,pLen);
  }

  //Yogesh:not portable 
  memcpy(pBuf + pLen, &pVal, SIZE_OF_INT);
  pLen += SIZE_OF_INT;
}


inline void TcapMessage::addShort(U8* pBuffer, short pVal, int &pLen) {
  if((pLen+2) > mBufSize){
    doReAllocation(&pBuffer,pLen);
  }
  //Yogesh: not portable
  memcpy(&pBuffer, &pVal, SIZE_OF_SHORT);
  pLen += SIZE_OF_SHORT;
}


//improve the logic if required memory > ALLOCATION_CHUNK
inline void TcapMessage::doReAllocation(U8** pBuf, int &pLen, int reallocSize) {
  U8* lTempBuf = *pBuf;
  mReallocCount++;
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"[doReAllocation] Rellocating Buffer"
    " realloc count <%d>", mReallocCount);
  mReallocCount++;
  *pBuf = new (nothrow)U8[mBufSize + ALLOCATION_CHUNK];
  if(NULL == *pBuf) {
    logger.logINGwMsg(false,ERROR_FLAG,0,
      "[doReAllocation] Cannot allocate buffer of size [%d] bytes",
       (mBufSize+ALLOCATION_CHUNK));
    exit(1);
  }
  //Use a lib function here
  memmove((void*)(*pBuf),(void*)lTempBuf, pLen);
  mBufSize += ALLOCATION_CHUNK;
  delete[] lTempBuf;
  lTempBuf = NULL;
}




// inserts length and copies pContentLen number of bytes from array pContentBuf into pBuf 
void TcapMessage::encodeBuffer(U8* pBuf, int pContentLen,int &pBufLen,U8* pContentBuf) {
  
  if((pBufLen + pContentLen + 2) > mBufSize){
    doReAllocation(&pBuf,pBufLen);
  }  
  encodeContentLength(pBuf,pBufLen,pBufLen,pContentLen);

  memcpy(pBuf + pBufLen, pContentBuf, pContentLen);
  pBufLen += pContentLen;
}

//description encodes SccpAddr address along with Tag length 
int TcapMessage::encodeSccpAddr(U8* pBuf,int &pBufLen, SccpAddr* pAddr, U8 pOrigin) {
  logger.logINGwMsg(false,TRACE_FLAG,0,
    "In  encodeSccpAddr");
  addByte(pBuf,pBufLen, pOrigin);
  int lAddrLenIndex =pBufLen++;

  addByte(pBuf,pBufLen,ROUTING_INDICATOR);
  U8 lRtgInd = pAddr->rtgInd;

  if(lRtgInd == INC_RTE_SSN){
    addByte(pBuf,pBufLen,JAIN_ROUTING_SUBSYSTEM);
  }
  else if(lRtgInd == INC_RTE_GT){
    //addByte(pBuf,pBufLen,JAIN_ROUTING_GLOBALTITLE);
    //Even if the routing is GT based we will be encoding
    //PC SSN based routing as SAS cannot process it 
    addByte(pBuf,pBufLen,JAIN_ROUTING_SUBSYSTEM);
  }
  else{
  logger.logINGwMsg(false,ERROR_FLAG,0,
    "+VER+ In encodeSccpAddr Routing Indicator "
    "Invalid <%d>", pBufLen);
    return G_FAILURE; 
  }

  addByte(pBuf,pBufLen,PROTOCOL_VARIANT);

  if( pAddr->sw == SW_JAPAN || pAddr->sw == SW_ITU
    ||pAddr->sw == SW_ANSI  || pAddr->sw == SW_CHINA){
     addByte(pBuf,pBufLen,(pAddr->sw));
  }
  else{
  logger.logINGwMsg(false,ERROR_FLAG,0,
    "+VER+ In encodeSccpAddr SERIOUS ERROR: Invalid Protocol Sw <%d>", pBufLen);
    return G_FAILURE; 
  }

  // We are never encoding GT structure [SBTM]
  //Yogesh : In WIN we are encoding GT since we are having requirements to send
  //a QWP from SAS->INC->STP

  if( (lRtgInd == INC_RTE_GT)/* && 0*/) {
  logger.logINGwMsg(false,TRACE_FLAG,0,
    "+VER+ In encodeSccpAddr going to encode GTI");

  addByte(pBuf,pBufLen,GT);
  U8 lGTFrmt = pAddr->gt.format;
  addByte(pBuf,pBufLen,lGTFrmt);

  switch(lGTFrmt){
    case  GTFRMT_1: 
    {
      addByte(pBuf,pBufLen,NAT_ADDR);
      addByte(pBuf,pBufLen,pAddr->gt.gt.f1.natAddr);

      addByte(pBuf,pBufLen,ODD_EVE_INDICATOR);
      addByte(pBuf,pBufLen,pAddr->gt.gt.f1.oddEven);
    }
    break;

    case GTFRMT_2:
    {
      addByte(pBuf,pBufLen,TRANSLATION_TYPE);
      addByte(pBuf,pBufLen,pAddr->gt.gt.f2.tType);
    }
    break;

    case GTFRMT_3:
    {
      addByte(pBuf,pBufLen,TRANSLATION_TYPE);
      addByte(pBuf,pBufLen,pAddr->gt.gt.f3.tType);

      addByte(pBuf,pBufLen,ENC_SCHEME);
      addByte(pBuf,pBufLen,pAddr->gt.gt.f3.encSch);

      addByte(pBuf,pBufLen,NUM_PLAN);
      addByte(pBuf,pBufLen,pAddr->gt.gt.f3.numPlan);
    }
    break;

    case GTFRMT_4:
    {
      addByte(pBuf,pBufLen,TRANSLATION_TYPE);
      addByte(pBuf,pBufLen,pAddr->gt.gt.f3.tType);

      addByte(pBuf,pBufLen,ENC_SCHEME);
      addByte(pBuf,pBufLen,pAddr->gt.gt.f3.encSch);

      addByte(pBuf,pBufLen,NUM_PLAN);
      addByte(pBuf,pBufLen,pAddr->gt.gt.f3.numPlan);

      addByte(pBuf,pBufLen,NAT_ADDR);
      addByte(pBuf,pBufLen,pAddr->gt.gt.f4.natAddr);
    }
    break;

    case GTFRMT_5:
    {
          addByte(pBuf,pBufLen,IPADDR);
          addInteger(pBuf, pBufLen, pAddr->gt.gt.f5.ipAddr);
    }
    break;

    default:
    {
      logger.logINGwMsg(false,ERROR_FLAG,0,
      "+VER+ Invalid GT format -- <%d>",lGTFrmt);
      return G_FAILURE;
    }
    
  }
     U8 lCntr=0;
     U8 lLen = pAddr->gt.addr.length;
     addByte(pBuf,pBufLen,SHRT_ADDR);
     addByte(pBuf,pBufLen,lLen);
     lCntr = 0;
     while(lCntr != lLen) {
     addByte(pBuf,pBufLen,pAddr->gt.addr.strg[lCntr++]);
     }
  }
  
  //Adding point code
  addByte(pBuf,pBufLen,PC);
  addInteger(pBuf, pBufLen, pAddr->pc);

  //Adding SSN
  addByte(pBuf,pBufLen,SSN);
  addByte(pBuf,pBufLen,pAddr->ssn);

  int lAddrLen = pBufLen-lAddrLenIndex- 1;
  encodeContentLength(pBuf, pBufLen, lAddrLenIndex, lAddrLen);
  logger.logINGwMsg(false,TRACE_FLAG,0,
    " Out encodeSccpAddr");
  return G_SUCCESS;
}

inline int TcapMessage::encodeParamBuffer(U8* pBuf, int &pLen, PtrStr pParam, U8 pParamFlg) {
  int retVal = G_SUCCESS; 
  if((pParam.len >0) && (NULL !=pParam.string)){
    addByte(pBuf,pLen,PARAM_IDENTIFIER);

    switch(pParamFlg){

      case INC_SEQUENCE:
      addByte(pBuf,pLen,JAIN_PARAM_TYPE_SEQUENCE);
      break;

      case INC_NO_SET_SEQ:
      addByte(pBuf,pLen,JAIN_PARAM_TYPE_SINGLE);
      break;

      case INC_SET:
      addByte(pBuf,pLen,JAIN_PARAM_TYPE_SET);
      break;
      
      default:
      logger.logINGwMsg(false,ERROR_FLAG,0,
        "+VER+ invalid parameter flag **QUITTING ENCODE**");
      return 0; 

    }

    addByte(pBuf,pLen,PARAM);
    encodeBuffer(pBuf,pParam.len,pLen,pParam.string);
  }
  return retVal;
}


                                         
inline int TcapMessage::encodeInvokeId(U8* pBuf, int &pLen, TcapComp pComp) {
  int retVal = G_SUCCESS;
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)    
  if(pComp.invIdAnsi.pres){
    addByte(pBuf, pLen, INVOKE_ID);
    addByte(pBuf, pLen, pComp.invIdAnsi.octet);
  }
#else  
  if(pComp.invIdItu.pres){
    addByte(pBuf, pLen, INVOKE_ID);
    addByte(pBuf, pLen, pComp.invIdItu.octet);
  }
#endif
  else{
    logger.logINGwMsg(false,ERROR_FLAG,0,"+VER+ InvokeId not present "
      "**QUITTING ENCODE**");
    retVal = G_FAILURE;
  }
  return retVal;
}

inline int TcapMessage::encodeLinkedId(U8* pBuf, int pLen, TcapComp pComp) {
  int retVal = G_SUCCESS;
  if(pComp.linkedId.pres){
    addByte(pBuf, pLen, LINKED_ID);
    addByte(pBuf, pLen, pComp.linkedId.octet);
  }
  else if(pComp.corrId.pres){
    addByte(pBuf, pLen, LINKED_ID);
    addByte(pBuf, pLen, pComp.corrId.octet);
  }
  return retVal;
}


//decodeLength will decode the length of the content from index pIndex 
//and increment the index by the number of octets in which length was encoded 
inline void TcapMessage::decodeLength(U8* pBuf, int &pIndex, int &pDecodeLen) {
  if((pBuf[pIndex] & 0x80) == 0x00){
    //short form of length
    pDecodeLen = pBuf[pIndex];
    pIndex++;
  }
  else{
    //long form of length
    int lCounter = pIndex;
    int lNumOfOctets =  pBuf[pIndex];
    pDecodeLen = 0;
    while(lNumOfOctets -128-1){
      pDecodeLen = (pDecodeLen | pBuf[++lCounter]);
      pDecodeLen <<=8;
      lNumOfOctets--;
    }

    pDecodeLen = pDecodeLen | pBuf[++lCounter];
    //move ahead by length of length
    pIndex = lCounter+1;
  }
}


inline int 
TcapMessage::decodeValue(U8* pBuf, int pIndex, int pLen) {
  int pVal = 0;
  char * lIntPtr = (char*)&pVal;
  memcpy(lIntPtr + SIZE_OF_INT -pLen, pBuf +pIndex,pLen);
  return pVal;
}                                                                      


int TcapMessage::decodeSccpAddr(U8* pBuf, int &pIndex, int &pAddrLen, SccpAddr* pAddr) {
  int retVal = G_SUCCESS;
  pIndex++; 
  pAddr->pres = true;
  decodeLength(pBuf,pIndex,pAddrLen); 
  logger.logINGwMsg(false, TRACE_FLAG,0,"[decodeSccpAddr] Address length <%d>",pAddrLen);
  int nextTagIndex = pIndex + pAddrLen;

  while( pIndex < nextTagIndex) 
  {
    if(pBuf[pIndex] == ROUTING_INDICATOR) {
      pIndex++; 

      pAddr->rtgInd = decodeValue(pBuf, pIndex, ROUTING_INDICATOR_LEN);
      if(pAddr->rtgInd == JAIN_ROUTING_SUBSYSTEM) {
        pAddr->rtgInd = INC_RTE_SSN;
      }
      else if(pAddr->rtgInd == JAIN_ROUTING_GLOBALTITLE) {
        pAddr->rtgInd = INC_RTE_GT;
      }
      else {
        logger.logINGwMsg(false,ERROR_FLAG,0,"+VER+ Unknown Routing indicator value <%d> "
          "**QUITTING DECODE**",pAddr->rtgInd);
        return G_FAILURE;      
      }

      pIndex += ROUTING_INDICATOR_LEN;
    }
    else if(pBuf[pIndex] == PROTOCOL_VARIANT) {
      pIndex++;
      pAddr->sw = decodeValue(pBuf,pIndex,PROTOCOL_VARIANT_LEN);

      if(pAddr->sw == SW_ITU){
      pAddr->sw = SW_ITU;
      logger.logINGwMsg(false,TRACE_FLAG,0,"[decodeSccpAddr] SW_ITU as protocol switch");
      }
      else if(pAddr->sw == SW_ANSI){
      pAddr->sw = SW_ANSI;
      logger.logINGwMsg(false,TRACE_FLAG,0,"[decodeSccpAddr] SW_ANSI as protocol switch");
      }
      else if(pAddr->sw == SW_CHINA ){
      logger.logINGwMsg(false,TRACE_FLAG,0,"[decodeSccpAddr] SW_CHINA as protocol switch");
      pAddr->sw = SW_CHINA;
      }
      else if(pAddr->sw == SW_JAPAN){
      logger.logINGwMsg(false,TRACE_FLAG,0,"[decodeSccpAddr] SW_JAPAN as protocol switch");
      pAddr->sw = SW_JAPAN;
      }
      else{ 
        logger.logINGwMsg(false,ERROR_FLAG,0,"+VER+ [decodeSccpAddr] Unknown protocol switch!! value = [%d] "
          "**QUITTING DECODE**",pAddr->sw);
        return G_FAILURE;      
      }
      pIndex+= PROTOCOL_VARIANT_LEN;
    }
    else if(pBuf[pIndex] == PC) {
      pIndex++;
      pAddr->pc = decodeValue(pBuf,pIndex,PC_LEN);
      pAddr->pcInd = true;
      logger.logINGwMsg(false,TRACE_FLAG,0,"[decodeSccpAddr] PC [%d]",pAddr->pc);
      pIndex += PC_LEN;
    }
    else if(pBuf[pIndex] == SSN) {
      pIndex++;
      pAddr->ssn = decodeValue(pBuf,pIndex,SSN_LEN);
      pAddr->ssnInd = true;
      logger.logINGwMsg(false,TRACE_FLAG,0,"[decodeSccpAddr] SSN [0x%02X]",pAddr->ssn);
      pIndex +=  SSN_LEN;
    }
    else if(pBuf[pIndex] == GT){
    /*
      logger.logINGwMsg(false,ERROR_FLAG,0,"+VER+[decodeSccpAddr] Unexpected GT tag "
        "**QUITTING DECODE**");
      return G_FAILURE;      
    */
      pIndex++;

      pAddr->gt.format = pBuf[pIndex];
      pIndex++;

      logger.logINGwMsg(false,VERBOSE_FLAG,0,
             "+VER+[decodeSccpAddr] GT tag received <%d>", pAddr->gt.format);
      
      switch(pAddr->gt.format)
      {
        case JAIN_GTINDICATOR_0000:
        {
          logger.logINGwMsg(false,ERROR_FLAG,0,"+VER+[decodeSccpAddr] "
                 "Unexpected GT Format **QUITTING DECODE** index <%d> val <%d>",
                  pIndex, pAddr->gt.format);

          return G_FAILURE;
        }
        break;
        
        case JAIN_GTINDICATOR_0001:
        {
          if(NAT_ADDR == pBuf[pIndex])
          {
            pAddr->gt.gt.f1.natAddr = pBuf[++pIndex]; pIndex++;
          }
          else{
          }
  
          if(ODD_EVE_INDICATOR == pBuf[pIndex])
          {
            pAddr->gt.gt.f1.oddEven = pBuf[++pIndex]; pIndex++;
          }
          else{
          }
        }
        break;

        case JAIN_GTINDICATOR_0010:
        {
          if(TRANSLATION_TYPE == pBuf[pIndex])
          {
            pAddr->gt.gt.f2.tType = pBuf[++pIndex]; pIndex++;
          }
          else {
          }
        }
        break;

        case JAIN_GTINDICATOR_0011:
        {
          if(TRANSLATION_TYPE == pBuf[pIndex])
          {
            pAddr->gt.gt.f2.tType = pBuf[++pIndex]; pIndex++;
          }
          else{
            logger.logINGwMsg(false,ERROR_FLAG,0,"+VER+[decodeSccpAddr] "
                   "decodeSccpAddr **QUITTING DECODE** index <%d> val <%d>",
                    pIndex, pBuf[pIndex]);

            return G_FAILURE;
          }
          
          if(ENC_SCHEME == pBuf[pIndex])
          {
            pAddr->gt.gt.f3.encSch = pBuf[++pIndex]; pIndex++;
          }
          else
          {
            logger.logINGwMsg(false,ERROR_FLAG,0,"+VER+[decodeSccpAddr] "
                   "decodeSccpAddr **QUITTING DECODE** index <%d> val <%d>",
                    pIndex, pBuf[pIndex]);

            return G_FAILURE;
          }
          
          if(NUM_PLAN == pBuf[pIndex])
          {
            pAddr->gt.gt.f3.numPlan = pBuf[++pIndex]; pIndex++;
          }
          else{
            logger.logINGwMsg(false,ERROR_FLAG,0,"+VER+[decodeSccpAddr] "
                   "decodeSccpAddr **QUITTING DECODE** index <%d> val <%d>",
                    pIndex, pBuf[pIndex]);

            return G_FAILURE;
          }
        }
        break;

        case JAIN_GTINDICATOR_0100:
        {
          if(TRANSLATION_TYPE == pBuf[pIndex])
          {
            pAddr->gt.gt.f2.tType = pBuf[++pIndex]; pIndex++;
          }
          else{
            logger.logINGwMsg(false,ERROR_FLAG,0,"+VER+[decodeSccpAddr] "
                   "decodeSccpAddr **QUITTING DECODE** index <%d> val <%d>",
                    pIndex, pBuf[pIndex]);

            return G_FAILURE;
          }

          if(ENC_SCHEME == pBuf[pIndex])
          {
            pAddr->gt.gt.f3.encSch = pBuf[++pIndex]; pIndex++;
          }
          else
          {
            logger.logINGwMsg(false,ERROR_FLAG,0,"+VER+[decodeSccpAddr] "
                   "decodeSccpAddr **QUITTING DECODE** index <%d> val <%d>",
                    pIndex, pBuf[pIndex]);

            return G_FAILURE;
          }
          
          if(NUM_PLAN == pBuf[pIndex])
          {
            pAddr->gt.gt.f3.numPlan = pBuf[++pIndex]; pIndex++;
          }
          else{
            logger.logINGwMsg(false,ERROR_FLAG,0,"+VER+[decodeSccpAddr] "
                   "decodeSccpAddr **QUITTING DECODE** index <%d> val <%d>",
                    pIndex, pBuf[pIndex]);

            return G_FAILURE;
          }

          if(NAT_ADDR == pBuf[pIndex])
          {
            pAddr->gt.gt.f1.natAddr = pBuf[++pIndex]; pIndex++;
          }
          else{
            logger.logINGwMsg(false,ERROR_FLAG,0,"+VER+[decodeSccpAddr] "
                   "decodeSccpAddr **QUITTING DECODE** index <%d> val <%d>",
                    pIndex, pBuf[pIndex]);

            return G_FAILURE;
          }

        }
        break;

        default:

        logger.logINGwMsg(false,ERROR_FLAG,0,"+VER+[decodeSccpAddr] "
               "decodeSccpAddr **QUITTING DECODE** index <%d> val <%d>",
                pIndex, pBuf[pIndex]);

        return G_FAILURE;
      }
    }
    else if(pBuf[pIndex] == SHRT_ADDR)
    {
      pIndex++;
      pAddr->gt.addr.length = pBuf[pIndex++];
      memcpy(pAddr->gt.addr.strg, pBuf + pIndex, pAddr->gt.addr.length);
      pIndex += pAddr->gt.addr.length;
    }
  }
  return retVal;
} 


TcapMessage::TcapMessage(bool _Itu)
{
      memset(&dlgIdR, 0, sizeof(dlgIdR));
      msg_type = 0;
      msgTypeR = 0;
      compPresR = false;
      pAbtCauseR.pres = false;
      memset(&dlgR,0,sizeof(TcapDlg));
      rCauseR.pres = false;
      memset(&qosR,0,sizeof(TcapQosSet));    
      uAbrtInfoR.len = 0;
      comp = NULL;
      memset(&appid,0,sizeof(AppInstId));
      Itu = _Itu;
 		  dialogueClosed = false;
      ipaddr = "";
      mBufSize = 0;
      mReallocCount = 0;
      dlgClndByINGw = false;
      InitLogger();
}

TcapMessage::TcapMessage(const string & _ipaddr, bool _Itu)
{
      this->ipaddr = _ipaddr;
      memset(&dlgIdR, 0, sizeof(dlgIdR));
      this->Itu = _Itu;
      msg_type = 0;
      msgTypeR = 0;
      compPresR = false;
      pAbtCauseR.pres = false;
      memset(&dlgR,0,sizeof(TcapDlg));
      rCauseR.pres = false;
      memset(&qosR,0,sizeof(TcapQosSet));      
      uAbrtInfoR.len = 0;
      comp = NULL;
      memset(&appid,0,sizeof(AppInstId));
 		  dialogueClosed = false;
      mBufSize = 0;
      mReallocCount = 0;
      dlgClndByINGw = false;
      InitLogger();
}

TcapMessage::~TcapMessage()
{
}

int TcapMessage::getDialogue()
{
      return dlgIdR;
}

void TcapMessage::setDialogue(U32 did)
{
 dlgIdR = did;
}

int
TcapMessage:: dumpSpAddr(SccpAddr addr, string pDir, char*pBuf, int pBufLen) {
  logger.logINGwMsg(false, TRACE_FLAG, 0,"In dumpSpAddr");
  //if(mCodecLogLevel  < 2 || true) {//remft
  //  logger.logINGwMsg(false, TRACE_FLAG, 0,
  //                    "Out dumpSpAddr <%d>",mCodecLogLevel);
  //  return 0;
  //}

	if(!g_isTraceEnabled())
		return 0;

  char buffer[4096];
  int  bufLen = 0;
  bzero(buffer, 4096);
  bufLen += sprintf(buffer +bufLen,"%s %s %s","\n--",pDir.c_str(),"--\n");
  
  bufLen += sprintf(buffer + bufLen,"\npres: %s", addr.pres == true?"True":"False");
#ifdef CMSS7_SPHDROPT
  bufLen += sprintf(buffer + bufLen,"\nspHdrOpt: %02X",addr.spHdrOpt);
#endif /*CMSS7_SPHDROPT*/
  bufLen += sprintf(buffer + bufLen,"\nProtocol Switch:%02x",addr.sw);
  bufLen += sprintf(buffer + bufLen,"\nssfPres: %s",addr.ssfPres == true?"True":"False");

  if(addr.ssfPres)
  bufLen += sprintf(buffer + bufLen,"\nssf: %02X",addr.ssf);

  bufLen += sprintf(buffer + bufLen,"\nniInd: %s",addr.niInd == true?"True":"False");
  bufLen += sprintf(buffer + bufLen,"\nRouting Indicator: %s",addr.rtgInd == 0x00?"RTE_GT":"RTE_SSN");
  bufLen += sprintf(buffer + bufLen,"\nPC Indicator: %s",addr.pcInd == true?"True":"False");
  bufLen += sprintf(buffer + bufLen,"\nSSN Indicator: %s",addr.ssnInd == true?"True":"False");
  if(addr.ssnInd)
  bufLen += sprintf(buffer + bufLen,"\nSSN: %d",addr.ssn);

  if(addr.pcInd)
  bufLen += sprintf(buffer + bufLen,"\nPC: %d",addr.pc);

#ifdef PRINT_ADDRINFO    
  if(addr.status){
    bufLen += sprintf(buffer + bufLen,"\ncareerIdenCode: %02X %02X",
                      addr.addrInfo.careerIdenCode[0],
                      addr.addrInfo.careerIdenCode[1]);
    bufLen += sprintf(buffer + bufLen,"\naddrInfoType: %02X : ",
                      addr.addrInfo.addrInfoType);
 
    //if(SP_SERV_INFO == addr.addrInfo.addrInfoType)
    if(0xe8 == addr.addrInfo.addrInfoType){
      bufLen += sprintf(buffer + bufLen,"SP_SERV_INFO\n");
      bufLen += sprintf(buffer + bufLen,"\t oddEven:%d\n",
                        addr.addrInfo.u.ServiceInfo.oddEven);

      bufLen += sprintf(buffer + bufLen,"\tserviceIdenCode: %02X\n",
                        addr.addrInfo.u.ServiceInfo.serviceIdenCode);

      bufLen += sprintf(buffer + bufLen,"\tserviceSpecInfo %02X %02X %02X\n",
                        addr.addrInfo.u.ServiceInfo.serviceSpecInfo[0],
                        addr.addrInfo.u.ServiceInfo.serviceSpecInfo[1],
                        addr.addrInfo.u.ServiceInfo.serviceSpecInfo[2]);
      
    // else if(SP_INTRANW_INFO== addr.addrInfo.addrInfoType)
    } else if(0xe9 == addr.addrInfo.addrInfoType){
      bufLen += sprintf(buffer + bufLen,"SP_INTRANW_INFO %02X-%02X\n",
                        (addr.addrInfo.u.NetworkInfo.intraNwInfo)>>8,
                        ((addr.addrInfo.u.NetworkInfo.intraNwInfo) & 0x00FF));

      bufLen += sprintf(buffer + bufLen,"\t\n");
    } else{
      bufLen += sprintf(buffer + bufLen,"Unknown\n");
    }   
  }else{
      bufLen += sprintf(buffer + bufLen,"\nStatus : NOT SET\n");
  }
#endif
   
  if(addr.rtgInd == 0x00){
    switch(addr.gt.format){
      case GTFRMT_0:
         bufLen +=sprintf(buffer + bufLen,"\nGT Format: GTFRMT_0\n");
         break; 
      case GTFRMT_1:
         bufLen +=sprintf(buffer + bufLen,"\nGT Format: GTFRMT_1\n");
         bufLen += sprintf(buffer + bufLen,"\nOdd/Even Indicator %d",addr.gt.gt.f1.oddEven);
         bufLen += sprintf(buffer + bufLen,"\nNature of Address: %d",addr.gt.gt.f1.natAddr);
         break;
      case GTFRMT_2:
         bufLen +=sprintf(buffer + bufLen,"\nGT Format : GTFRMT_2\n");
         bufLen +=sprintf(buffer + bufLen,"\nTranslation type: %d",addr.gt.gt.f2.tType);
         break;
      case GTFRMT_3:
         bufLen +=sprintf(buffer + bufLen,"\nGT Format : GTFRMT_3\n");
         bufLen +=sprintf(buffer + bufLen,"\nTranslation type: %d",addr.gt.gt.f3.tType);
         bufLen +=sprintf(buffer + bufLen,"\nNumbering Plan: %d",addr.gt.gt.f3.numPlan);
         bufLen +=sprintf(buffer + bufLen,"\nEncoding Scheme :%d",addr.gt.gt.f3.encSch);
         break;
      case GTFRMT_4:
         bufLen +=sprintf(buffer + bufLen,"\nGT Format : GTFRMT_4\n");
         bufLen +=sprintf(buffer + bufLen,"\nTranslation type: %d",addr.gt.gt.f4.tType);
         bufLen +=sprintf(buffer + bufLen,"\nNumbering Plan: %d",addr.gt.gt.f4.numPlan);
         bufLen +=sprintf(buffer + bufLen,"\nEncoding Scheme %d",addr.gt.gt.f4.encSch);
         bufLen +=sprintf(buffer + bufLen,"\nNature of Address %d",addr.gt.gt.f4.natAddr);
         break;
      case GTFRMT_5:
         bufLen +=sprintf(buffer + bufLen,"\nGT Format : GTFRMT_5\n");
         bufLen +=sprintf(buffer + bufLen,"Ip Addr: %d",addr.gt.gt.f5.ipAddr);
         break;
      default:
         bufLen +=sprintf(buffer + bufLen,"\nGT Format : UNKNOWN %d\n",addr.gt.format);
   
    }
    if(0 != addr.gt.addr.length) {
      bufLen +=sprintf(buffer + bufLen,"\n----G T - D I G I T S----\n");
      for(int i=0;i<addr.gt.addr.length;i++) {
        bufLen +=sprintf(buffer + bufLen,"%02X ",addr.gt.addr.strg[i]);
      }
      bufLen +=sprintf(buffer + bufLen,"\n");
    } else{
      bufLen +=sprintf(buffer + bufLen,"\n----G T - D I G I T S----\n"
                                       "    ----   N U L L   ---- \n");
    }   
  }
  
  //if(0 < mLogger->getLoggingLevel()){
    if(pBuf)
    sprintf(pBuf + pBufLen,"%s",buffer);
  //} 
  logger.logINGwMsg(false, TRACE_FLAG, 0,"Out dumpSpAddr \n%s ",buffer);
  return bufLen;
}

int TcapMessage::addReceivedContent(TcapMessage *msg)
{
  static int lcount = 0;
  logger.logINGwMsg(false, VERBOSE_FLAG, 0,
                             "In addReceivedContent %d ",++lcount);
  if (msg->msg_type == EVTSTUCMPIND)
  {
    if(INC_REJECT == msg->comp->compType) {
#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)
      msg->dlgR.dlgType = STU_CNV_PRM;
#else
      msg->dlgR.dlgType = INC_CONTINUE;
#endif

      //not setting spDlgId TcapMessage as if suDlgId != spDlgId we need suDlgId as param
      //and when suDlgId == spDlgId we need not care which one to pass

      msg->setDialogue(msg->dlgR.sudlgId);
      logger.logINGwMsg(false, VERBOSE_FLAG, 0,"Received Reject <%d>",
      msg->getDialogue());
    }

    logger.logINGwMsg(false, VERBOSE_FLAG, 0,
                 "[addReceivedContent]Component Indication");
    this->mCompVector.push_back(msg->comp);
   
    if (msg->comp->lastComp == INC_TRUE)
    {
      logger.logINGwMsg(false,TRACE_FLAG,0,"+rem+ lastComp flag is true");
    	if (dialogueClosed == true)
    	{ 
	  		getUserAddress(ipaddr);

        pthread_mutex_lock(&mutex);

      	TcapUserAddressInformation & tuai = 
	  		TcapMessage::getUserAddressBy(msg->dlgIdR,msg->appid);

        logger.logINGwMsg(false,TRACE_FLAG,0,"+rem+ dialogueClosed is true");
	  		if (tuai.isValid())
      	{
          logger.logINGwMsg(false,TRACE_FLAG,0,"+rem+ Calling removeDialogue tuai is valid");
	  			TcapMessage::removeDialogue(tuai.userDlgId, tuai.did, 
	  									msg->dstaddrR, msg->appid, tuai.ipaddr, false, false);
	  		}

        pthread_mutex_unlock(&mutex);

	  		getUserAddress(ipaddr);
    	}
      else{
        logger.logINGwMsg(false,TRACE_FLAG,0,"+rem+ dialogueClosed is false");
      }
	    logger.logINGwMsg(false, VERBOSE_FLAG, 0,
	            "Last Component - return MESSAGE_COMPLETE");
      return MESSAGE_COMPLETE;
	  }
	  else
	  {
	    logger.logINGwMsg(false, VERBOSE_FLAG, 0,
	             "NOT Last Component - return INC_ROK");
	  }

    return INC_ROK;
  }
  if (msg->msg_type == EVTSTUDATIND || msg->msg_type == EVTSTUUDATIND)
  {
	  logger.logINGwMsg(false, VERBOSE_FLAG, 0,
                      "[addReceivedContent]Dialogue indication msgType<%d>",msg->msgTypeR);
    dialogueClosed = false;
    dlgIdR = msg->dlgR.spdlgId;
    switch(msg->msgTypeR)
    {
      case INC_UNI:
      {

      }
      break;

      case INC_BEGIN:
      {
                   
	      logger.logINGwMsg(false, VERBOSE_FLAG, 0,
          "[addReceivedContent] Rx INC_BEGIN");

      }
      break;

      case INC_CONTINUE:
      {
      }
      break;

      case INC_END:
      {
        dialogueClosed = true;

      }
      break;

      case INC_U_ABORT:
      {
        dialogueClosed = true;
				msg->compPresR = INC_FALSE;
      }
      break;

      case INC_P_ABORT:
      {
			  logger.logINGwMsg(false, VERBOSE_FLAG, 0,
			 		"[addReceivedEvent] PABORT Rxed");
        dialogueClosed = true;
			 	msg->compPresR = INC_FALSE;
      }
            break;

      case INC_NOTICE:
      {
        dialogueClosed = true;
      }
      break;

      default:
             
      logger.logINGwMsg(false, VERBOSE_FLAG, 0,
        "Received Unknown DLG with dlgId = %d\n",msg->dlgIdR);
    }//end of switch 


    if (msg->compPresR == INC_FALSE)
		{
    	if (dialogueClosed == true)
    	{
				getUserAddress(ipaddr);

        pthread_mutex_lock(&mutex);

      	TcapUserAddressInformation & tuai = 
				TcapMessage::getUserAddressBy(msg->dlgIdR, msg->appid);

        logger.logINGwMsg(false, VERBOSE_FLAG, 0,
                         "+rem+ dialogueClosed true, no comp present");
				if (tuai.isValid())
     	 	{
          logger.logINGwMsg(false, VERBOSE_FLAG, 0,
            "+rem+ dialogueClosed true, no comp present, tuai is valid");

					TcapMessage::removeDialogue(tuai.userDlgId, tuai.did, 
											 msg->dstaddrR, msg->appid, tuai.ipaddr,false,false);
				}

        pthread_mutex_unlock(&mutex);

    	}
        
      return MESSAGE_COMPLETE;
      logger.logINGwMsg(false, VERBOSE_FLAG, 0,
             "NO Component - return MESSAGE_COMPLETE");
		}
		else
		{
	    logger.logINGwMsg(false, VERBOSE_FLAG, 0,
	             "Component present - return INC_ROK");
		}
    return INC_ROK;
  }

	logger.logINGwMsg(false, VERBOSE_FLAG, 0,
	  "[addReceivedContent-Inbound] Invalid msg type [%d]",  msg->msg_type);
  return -1;
}

bool TcapMessage::isAnsi()
{
 		return !Itu;
}

//gOutBoundMsgCntr is being used for ft testing 
static int gOutBoundMsgCntr = 0;
extern int g_maxNmbOutDlg;

int TcapMessage::processReceivedContent(U8 * p_msgBody, unsigned int p_bodyLen, int p_seqNum)
{ 
  logger.logINGwMsg(false, TRACE_FLAG, 0,
                    "In TcapMessage::processReceivedContent seqNum<%d>",
                     p_seqNum);
  
  if(!(p_msgBody && p_bodyLen)){
    logger.logINGwMsg(false, ERROR_FLAG, 0,
    "[processReceivedContent] Not Processing p_msgBody %u p_bodyLen %u",
    p_msgBody, p_bodyLen);
    return G_SUCCESS;
  }
  
  U8  lcJainDlgType = 0;
  int liDlgId =0;
  
  extractDlgInfo(p_msgBody, liDlgId, lcJainDlgType);
  
  if((liDlgId < tcapLoDlgId) || (liDlgId >= (tcapHiDlgId + g_maxNmbOutDlg) )) 
  {
    logger.logINGwMsg(false,ERROR_FLAG,0,
                      "dialogueId <%d> out of range lo<%d> Hi<%d> SAS-Hi <%d>",
                       liDlgId, tcapLoDlgId, tcapHiDlgId, g_maxNmbOutDlg);
    return G_FAILURE;
  }

  if((lcJainDlgType != JAIN_UNIDIRECTIONAL)
      && isRetransmission(liDlgId, p_seqNum)) {
    logger.logINGwMsg(false,ERROR_FLAG,0,
                            "retrans found <%d> <499>",liDlgId);
    return 499;
  }

#ifdef MSG_FT_TEST
  if(INGwTcapProvider::getInstance().getFpMask() == 3){
    logger.logINGwMsg(false,ALWAYS_FLAG,0,
    "receive NOTIFY msg - replicate and forced to fail");
    sleep(1);
    exit(1);
  }

  if(INGwTcapProvider::getInstance().getFpMask() == 6){
      gOutBoundMsgCntr++;
      if(2 == gOutBoundMsgCntr){
      logger.logINGwMsg(false,ALWAYS_FLAG,0,
      "receive second NOTIFY  msg - replicate and fail");
      exit(1);
    }
  }
#endif /*MSG_FT_TEST*/

  if(mLogger->getLoggingLevel()) {
    char lBuf[1024];
    short lBufLen = 0;
      
    lBufLen = sprintf(lBuf + lBufLen,
      "\n--Msg From SAS--\n ");

    //getDlgType(p_msgBody,lBuf,lBufLen,liDlgId);
 
	  for (int i=0; i  < p_bodyLen; i++) {
      if(0 == i%16){
        lBufLen += sprintf(lBuf + lBufLen,"%s","\n\t");
      }
     lBufLen += sprintf(lBuf + lBufLen, "%02X ", p_msgBody[i]);
	  }
    mLogger->dumpCodecMsg(lBuf,DEC);
  } 

  bool retVal = G_SUCCESS;
  INGwTcapWorkUnitMsg lworkUnit;
  memset(&lworkUnit,0,sizeof(INGwTcapWorkUnitMsg)); 
   
  retVal = decode(p_msgBody, p_bodyLen, this->dlgR, this->mCompVector);

  if(retVal)
  {
    logger.logINGwMsg(false, VERBOSE_FLAG, 0,
                      "[processReceivedContent-Outbound] decode Success");

    bool lIsValid = false;
    int dlgId = getDialogueFromRequest(this->dlgR, ipaddr,lworkUnit,lIsValid);

    //enabling UNI message replication

    if(JAIN_BEGIN != lcJainDlgType) {
      INGwTcapFtHandler::getInstance().replicateTcapCallDataToPeer
                       (MSG_TCAP_OUTBOUND, p_msgBody, p_bodyLen, p_seqNum, liDlgId);
    }
    else
    {
      //Yogesh: create a global array of type g_TransitObj of size no. of worker threads
      g_TransitObj	l_transitObj;

      l_transitObj.m_buf        = p_msgBody;
      l_transitObj.m_bufLen     = p_bodyLen;
      l_transitObj.m_stackDlgId = 0; //not assigned till yet
      l_transitObj.m_userDlgId  = liDlgId;
      l_transitObj.m_ssn        = dlgR.srcAddr->ssn; 
      l_transitObj.m_suId       = appid.suId;
      l_transitObj.m_spId       = appid.spId;
      l_transitObj.m_seqNum     = p_seqNum;
      l_transitObj.m_sasIp      = ipaddr;

      INGwTcapFtHandler::getInstance().sendCreateTcapSessionMsg(l_transitObj, this);
    }

    if((!lIsValid)) 
    {
      if(JAIN_BEGIN != lcJainDlgType)
      {
        logger.logINGwMsg(false,ERROR_FLAG,0,
        "processReceivedContent OutBoundMsg dlgId<%d> dlgtype <%d> not "
        "found in Map" , liDlgId,lcJainDlgType);

        if(!(liDlgId< tcapLoDlgId) || (liDlgId>= tcapHiDlgId)) 
        {
          INGwTcapFtHandler::getInstance().sendTerminateTcapSession(liDlgId); 
        }
        //sendTerminateTcapSession message to peer
      }
      else
      {
        logger.logINGwMsg(false, VERBOSE_FLAG, 0,"Tc-QWP Rxd from SAS");
      }

      return G_FAILURE;
    }
              
    logger.logINGwMsg(false, VERBOSE_FLAG, 0,
						"[processReceivedContent-Outbound] request ipaddr[%s]" 
						" srcaddr-pc[%d] ssn[%d] dstaddr-pc[%d] ssn[%d] dlgId[%d]",
						(true == ipaddr.empty())?"NULL":ipaddr.c_str(), dlgR.srcAddr->pc,
						dlgR.srcAddr->ssn, dlgR.dstAddr->pc, dlgR.dstAddr->ssn, dlgId);

     //create the workUnit
    if(this->dlgR.dlgType == INC_UNI)
    {
      lworkUnit.eventType = EVTSTUUDATREQ;
    }
    else 
    {
      lworkUnit.eventType = EVTSTUDATREQ;
    }

    lworkUnit.m_dlgId = this->dlgR.sudlgId;
    lworkUnit.m_tcapMsg = this;

    //if compPresR field is set Tx components.
    lworkUnit.compPres = this->compPresR;
    
    //workUnit created now set TcapMessage fields.
    this->msg_type = this->dlgR.dlgType;//lworkUnit.eventType;
     
    logger.logINGwMsg(false, VERBOSE_FLAG, 0, "before sendTcapReq");

    
    // Check the return value for error conditions.
    INGwSilTx::instance().sendTcapReq(&lworkUnit); 

#ifdef MSG_FT_TEST
    if(INGwTcapProvider::getInstance().getFpMask() == 4){
      logger.logINGwMsg(false,ALWAYS_FLAG,0,
      "receive NOTIFY msg - send to network fail before cleaning CONT 0.5 sec");
      sleep(1);
      exit(1);
    }
    if(INGwTcapProvider::getInstance().getFpMask() == 7){
      gOutBoundMsgCntr++;
      if(2 == gOutBoundMsgCntr){
        logger.logINGwMsg(false,ALWAYS_FLAG,0,
        "receive second NOTIFY  msg - send to network "
        "and fail before cleaning");
        exit(1);
      }
    }

    if((this->dlgR.dlgType == INC_END || 
        this->dlgR.dlgType == INC_U_ABORT ) &&  
      (INGwTcapProvider::getInstance().getFpMask() == 10)){
     logger.logINGwMsg(false,ALWAYS_FLAG,0,
     "receive NULL END - send to nw and fail before cleaning");
     exit(1);
    }
#endif /*MSG_FT_TEST*/

    //terminate the session at S-INC 
    //separate packet to reduce no of bytes exchanged
    if(this->dlgR.dlgType == INC_END || this->dlgR.dlgType == INC_U_ABORT ||
       this->dlgR.dlgType == STU_RESPONSE ||
       this->dlgR.dlgType == STU_ANSI_ABORT ||
       this->dlgR.dlgType == STU_ANSI_PABORT) 
    {
       INGwTcapFtHandler::getInstance().sendTerminateTcapSession(liDlgId);
    }
    else {
      INGwTcapFtHandler::getInstance().sendTcapSessionUpdateInfo(
                                       INGW_CLEAN_OUTBOUND_MESSAGE,
                                       lworkUnit.m_dlgId, p_seqNum);
    }

    logger.logINGwMsg(false, VERBOSE_FLAG, 0,
                           "after sendTcapReq");
#ifdef MSG_FT_TEST
    if(INGwTcapProvider::getInstance().getFpMask() == 5){
      logger.logINGwMsg(false,ALWAYS_FLAG,0,
      "receive NOTIFY msg - send to network and fail after cleaning CONT");
      exit(1);
    }
    if(INGwTcapProvider::getInstance().getFpMask() == 8){
      gOutBoundMsgCntr++;
      if(2 == gOutBoundMsgCntr) {
        logger.logINGwMsg(false,ALWAYS_FLAG,0,
        "receive second NOTIFY  msg - send to network "
        "and fail after cleaning");
        exit(1);
      }
    }
#endif /*MSG_FT_TEST*/
  } 
  else{
    logger.logINGwMsg(false,ERROR_FLAG,0,"+VER+ Error in decoding buffer for "
      "outbound message");

    if((dlgR.sudlgId != dlgR.spdlgId) || 
       !((liDlgId< tcapLoDlgId) || (liDlgId>= tcapHiDlgId))) {
      INGwTcapFtHandler::getInstance().sendTerminateTcapSession(liDlgId); 

      TcapUserAddressInformation &tuai = 
                          TcapMessage::getUserAddressBy(liDlgId, ipaddr);

      closeDialogue(0x01, liDlgId, tuai.suId, tuai.spId, false, true, false);
    }
    retVal = G_FAILURE; 
  }

  logger.logINGwMsg(false, VERBOSE_FLAG, 0,
                    "Out TcapMessage::processReceivedContent");
  return retVal; 
}

int TcapMessage::getDialogueFromRequest(TcapDlg& dre, 
                                        const string & _ipaddr,
                                        INGwTcapWorkUnitMsg& p_workUnit,
                                        bool &p_isValid)
{
  logger.logINGwMsg(false, TRACE_FLAG, 0,"In getDialogueFromRequest");
  int userDialogueId = -1;
	bool  useDest = false;
        userDialogueId = dre.sudlgId;
  int msgType = dre.dlgType;
  int stackDialogue = -1;

  switch (msgType)
  {
    case STU_BEGIN:
    case STU_QRY_PRM:
    case STU_QRY_NO_PRM:
    {
      logger.logINGwMsg(false, VERBOSE_FLAG, 0,
        "[getDialogueFromRequest]Yogesh msgType is Begin userDialogueId[%d]",userDialogueId);
#if 0
      if(1) {
        dre.srcAddr->rtgInd          = INC_RTE_GT;
        dre.srcAddr->pcInd           = false;
        dre.srcAddr->ssnInd          = true;
        dre.srcAddr->gt.format       = 2; 
        dre.srcAddr->gt.gt.f2.tType  = 0xE9; 
        dre.srcAddr->gt.addr.length  = 2;
        dre.srcAddr->gt.addr.strg[0] = 0x21;
        dre.srcAddr->gt.addr.strg[1] = 0x43;
      }

      if(1) {
        //INCTBD hardcoding
        dre.dstAddr->rtgInd          = INC_RTE_GT;
        dre.dstAddr->pcInd           = false;
        dre.dstAddr->ssnInd          = true;
        dre.dstAddr->gt.format       = 2; 
        dre.dstAddr->gt.gt.f2.tType  = 0xE9; //Ttype
        dre.dstAddr->gt.addr.length  = 2;
        dre.dstAddr->gt.addr.strg[0] = 0x21;
        dre.dstAddr->gt.addr.strg[1] = 0x43;
      }
#endif

      //spDlgId would be zero (irrelevant here)
      //getBillingId
      //create ansiDlgEv
      //

#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96) 
      NonASNBillingId   lBillingId;
      lBillingId.setBillingNo(dre.miBillingNo);

      StAnsiDlgEv  lansiDlgEv;
      memset(&lansiDlgEv, 0, sizeof(StAnsiDlgEv));
#else
      //get the decoded one from SAS 
      INcStr lObjAcn;
#endif
      INGwTcapProvider::getInstance().getSsnInfo
                   (dre.srcAddr->ssn, appid.suId, appid.spId); 

      logger.logINGwMsg(false, VERBOSE_FLAG, 0,
                        "%d dre.srcAddr->ssn, suId <%d>, spId <%d>",
                        dre.srcAddr->ssn,
                        appid.suId, 
                        appid.spId);

      TcapUserAddressInformation tuai(dre.spdlgId, 
                                      dre.sudlgId, 
                                      *(dre.srcAddr), 
                                      *(dre.dstAddr), 
                                      _ipaddr,
                                      #if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96) 
                                      lansiDlgEv,
                                      lBillingId,
                                      #else
                                      lObjAcn, 
                                      #endif
                                      appid.suId,
                                      appid.spId,
                                      INC_BEGIN); //to be modified later

      storeDialogue(dre.sudlgId, dre.spdlgId, appid, ipaddr, tuai);
    }
    break;
    default:
    {
        //make sp dialogue Id
      dre.srcAddr = new SccpAddr;
      memset(dre.srcAddr,0,sizeof(SccpAddr));
      dre.dstAddr = new SccpAddr;        
      memset(dre.dstAddr,0,sizeof(SccpAddr));
      //LOCK();
      pthread_mutex_lock(&mutex);
      TcapUserAddressInformation &tuai = TcapMessage::getUserAddressBy(userDialogueId, _ipaddr);

      if (tuai.isValid())
      {
        tuai.mDlgType = dre.dlgType;

        AppInstId l_appId;

        l_appId.suId = tuai.suId;
        l_appId.spId = tuai.spId;

        //yogesh
        //optimize here, get the info of suid spid from SIP provider
        TcapUserAddressInformation & tuai2 = 
    			TcapMessage::getUserAddressBy(tuai.did, l_appId);
        tuai2.mDlgType = dre.dlgType;

        logger.logINGwMsg(false,TRACE_FLAG,0,"getDialogueFromRequest TrType <%d>",tuai.mDlgType);
        p_isValid = true;
        logger.logINGwMsg(false, VERBOSE_FLAG, 0, "tuai is valid");
        if(!(INGwIfrPrParamRepository::getInstance().getPeerStatus())
           && false == tuai.isDuplicated){
          tuai.isDuplicated = true;

          INGwTcapFtHandler::getInstance().
              tcapUserInformationForReplication(
                                                tuai.did,
                                                tuai.userDlgId, 
    					                                  tuai.srcaddr, 
                                                tuai.destaddr, 
                                                tuai.ipaddr, 
                                                l_appId, 
                                      #if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)
                                                tuai.ansiDlgEv,
                                                tuai.mBillingId.getBillingNo()
                                      #else
                                                tuai.objAcn
                                      #endif
                                               );
        }

        p_workUnit.m_suId = l_appId.suId = tuai.suId;
        p_workUnit.m_spId = l_appId.spId = tuai.spId;

        logger.logINGwMsg(false,TRACE_FLAG,0,"+rem+ getDialogueFromRequest suId[%d] spId[%d]",
        p_workUnit.m_suId, p_workUnit.m_spId);


		  	//[Telesys] - if stack dialogue id is same as user dialogue id
		  	// then it means message is received from network else 
		  	// generated by SAS app. reversing the order incase of stack
		  	// originated dialogue.
        //[CCPU] stack already swaps the GT structure 

        stackDialogue = tuai.did;
        //both same in case dialogue initiated from network
		  	if (tuai.did == tuai.userDlgId) 
		  	{
          dre.spdlgId = dre.sudlgId;  
          logger.logINGwMsg(false, TRACE_FLAG, 0,
                            "Message is from Network");

          logger.logINGwMsg(false, TRACE_FLAG, 0,
                 "tuai src pc[%d]ssn[%d] dst pc[%d]ssn[%d]",
                 tuai.srcaddr.pc,tuai.srcaddr.ssn,tuai.destaddr.pc,
                 tuai.destaddr.ssn);

		  		memcpy(dlgR.srcAddr, &tuai.srcaddr, sizeof(SccpAddr));
		  		memcpy(dlgR.dstAddr, &tuai.destaddr , sizeof(SccpAddr));
#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96) 
          if((tuai.ansiDlgEv.pres.pres) && (tuai.ansiDlgEv.pres.val))
          {
            dre.pres = true;
          }
          memcpy(&dlgR.ansiDlgEv, &tuai.ansiDlgEv, sizeof(stAnsiDlgEv));
#else
          if(0 != tuai.objAcn.len){
            memcpy(dre.objAcn.string, tuai.objAcn.string, tuai.objAcn.len);
            dre.objAcn.len = tuai.objAcn.len;
            dre.pres = true;
          }
          else{
            dre.pres = false;
          }
#endif
          
          //commenting it to handle case of pcInd true
          //dlgR.srcAddr->pc = 0;
          //dlgR.dstAddr->pc = 0;

          dumpSpAddr((*dlgR.srcAddr),"O U T G O I N G - S O U R C E");
          dumpSpAddr((*dlgR.dstAddr),"O U T G O I N G - D E S T I N A T I O N");
		  		useDest = true;
		    }
		    else 
		  	{
          logger.logINGwMsg(false, TRACE_FLAG, 0,
                            "Message is not from Network");
          memcpy(dlgR.srcAddr, &tuai.srcaddr, sizeof(SccpAddr));
          memcpy(dlgR.dstAddr, &tuai.destaddr, sizeof(SccpAddr));
		    }

        if(dre.dlgType == INC_END || dre.dlgType == INC_U_ABORT 
           || dre.dlgType == INC_UNI) {
           //Yogesh:Uncomment above condition for the cleanup of Uni dialogues
          logger.logINGwMsg(false,TRACE_FLAG,0,"+rem+ processing <%d> "
          "ip %s suid %d,spid %d, userDlgId %d did %d",dre.dlgType,
          tuai.ipaddr.c_str(), l_appId.suId, l_appId.spId, tuai.userDlgId,
          tuai.did);

          //not putting a check on UNI :unnecessary
          //do we need to lock replay giReplayCallCnt 
          bool lrepFlg = gGetReplayFlg();//+rem+
          if(giReplayCallCnt) {
            giReplayCallCnt = gUpdateReplayCallCnt(tuai.userDlgId);
          }

          logger.logINGwMsg(false,TRACE_FLAG,0,
            "+rem+ getDialogueFromRequest Replay Flg <%s> <%d>",
            (lrepFlg == true)?"TRUE":"FALSE", tuai.userDlgId);
 
	        TcapMessage::removeDialogue(dlgR.sudlgId, dlgR.sudlgId, 
	         *(dlgR.srcAddr), l_appId, ipaddr, false, false);
        }
      }
      else {
        logger.logINGwMsg(false,ERROR_FLAG,0, 
               "getDialogueFromRequest():userDialogueId<%d> NOT FOUND",
               userDialogueId);
        p_isValid =false;
        delete dre.srcAddr; dre.srcAddr = NULL;
        delete dre.dstAddr; dre.dstAddr = NULL;
      }

      pthread_mutex_unlock(&mutex);
    }
  }

  logger.logINGwMsg(false, TRACE_FLAG, 0,"Out getDialogueFromRequest");
  return  stackDialogue;
}

void TcapMessage::storeUserInformationFT(
                        int stackDialogue, 
                        int userDialogue, 
                        SccpAddr &srcaddr, 
                        SccpAddr &destaddr, 
                        const string &ipaddr, 
                        AppInstId  &p_appId, 
                        #if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96) 
                        StAnsiDlgEv p_ansiDlgEv,
                        int piBillingNo 
                        #else
                        INcStr &p_objAcn
                        #endif
                        )
{
  logger.logINGwMsg(false,TRACE_FLAG,0,"In storeUserInformationFT");


  logger.logINGwMsg(false, VERBOSE_FLAG, 0,
	"+rem+[TcapUserInformationForSerialization] deserialized Serialize stkDlg[%d] "
	"userDlg[%d] ipadd[%s] destaddr[pc-ssn: %d-%d] srcaddr[pc-ssn: %d-%d]"
   " suid: %d spId %d objAcn Len %d",
	stackDialogue, userDialogue, ipaddr.c_str(),destaddr.pc, destaddr.ssn, srcaddr.pc,
	srcaddr.ssn, p_appId.suId, p_appId.spId,  
  #if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96) 
  (INC_ENC_TYP_OID == p_ansiDlgEv.acnType.val?p_ansiDlgEv.acn.oidAcn.len<<1:4)
  #else
  p_objAcn.len
  #endif
  );

  NonASNBillingId lBillingId;
  lBillingId.setBillingNo(piBillingNo);

  TcapMessage::dumpSpAddr(srcaddr,"+rem+ F T - S O U R C E");
  TcapMessage::dumpSpAddr(destaddr,"+rem+ F T - D E S T");

     //yogesh modify this
      TcapUserAddressInformation tuai(stackDialogue, 
                                      userDialogue, 
                                      srcaddr, destaddr, 
                                      ipaddr,
                                     //To handle ANSI dialogue portion

                                      #if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96) 
                                      p_ansiDlgEv,
                                      lBillingId,
                                      #else
                                      p_objAcn,
                                      #endif

                                      p_appId.suId,
                                      p_appId.spId,INC_BEGIN);

      logger.logINGwMsg(false,TRACE_FLAG,0,"+rem+ storeUserInformationFT address"
                                           " of tuai %x", (&tuai));

      storeDialogue(userDialogue, stackDialogue, p_appId, ipaddr, tuai, true);

  logger.logINGwMsg(false,TRACE_FLAG,0,"Out storeUserInformationFT");
}



int TcapMessage::createUserDialogue(int userDlgId, SccpAddr &srcaddr, SccpAddr &destaddr, const string & ipaddr)
{
    // INCTBD
    // Needs to be changed when we need to handle SAS initiated calls.
    // Basically UserDialogueId management needs to be looked into
    // for this scenario
    #if 0
      U32 did;
      void * userId = getUserId(srcaddr);
      AppInstId appid;
      getAppId(srcaddr, appid);

 		int retVal = M7H_ROK;
      if (M7H_ROK != (retVal = TuGetLocalDlgId(userId, &did, appid))) {
 			logger.logINGwMsg(false, ERROR_FLAG, 0,
 			"[createUserDialogue] TugetLocalDlgId returned error [%d] for sasIp[%s]",
 			retVal, ipaddr.c_str());
 			return -1;
 		}
      U32 did = 101010;   
      TcapUserAddressInformation tuai((U32)did, userDlgId, srcaddr, destaddr, ipaddr);
      storeDialogue(userDlgId, did, userId, appid, ipaddr, tuai);
      return did;
   #endif
   return 10;
}

void TcapMessage::assignUserToDialogue(const string & _ipaddr)
{
  logger.logINGwMsg(false,TRACE_FLAG,0,"In assignUserToDialogue"); 


#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)  
  try { 
     getNonAsnWinParamNew<NonASNBillingId>(mCompVector[0]->param, mBillingId);
  } catch(int e) {
    //abort the call
 	  logger.logINGwMsg(false, ERROR_FLAG, 0,"Exception getNonAsnWinParamNew<%d>",
                      e);
  }


 if(g_isTraceEnabled())
 { 
   mBillingId.logBillingId(getDialogue());
 }
#endif

  if(g_isTraceEnabled()) 
  {
    dumpSpAddr(*(dlgR.srcAddr),"after +rem+ I N C O M I N G - S O U R C E");
    dumpSpAddr(*(dlgR.dstAddr),"after +rem+ I N C O M I N G - D E S T I N A T I O N");
  }

  logger.logINGwMsg(false, VERBOSE_FLAG,0,"+rem+[assignUserToDialogue] Storing suId<%d> spId<%d>",
                    m_suId,m_spId);
  if((STU_BEGIN == this->dlgR.dlgType) || (STU_QRY_NO_PRM == this->dlgR.dlgType) ||
           (STU_QRY_PRM == this->dlgR.dlgType))
  {
    TcapUserAddressInformation tuai(getDialogue(), getDialogue(), *(dlgR.srcAddr),
                                    *(dlgR.dstAddr),
                                    _ipaddr, 
#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)  
                                    this->dlgR.ansiDlgEv,
                                    mBillingId, 
#else
                                    dlgR.objAcn, 
#endif
                                    m_suId, m_spId,
                                    this->dlgR.dlgType);

    appid.suId =  m_suId;
    appid.spId =  m_spId;

    TcapMessage::storeDialogue(getDialogue(), getDialogue(), appid, _ipaddr, tuai);
 	logger.logINGwMsg(false, VERBOSE_FLAG, 0,
         "[assignUserToDialogue]tuai storing getDialogue[%d] src pc[%d] ssn[%d]"
         "dst pc[%d] ssn [%d]",getDialogue(),tuai.srcaddr.pc,
         tuai.srcaddr.ssn, tuai.destaddr.pc, tuai.destaddr.ssn);

  }
  else{
    logger.logINGwMsg(false,TRACE_FLAG,0,
                      "Not creating Session for STU_UNI<%d>",getDialogue()); 
  }

  logger.logINGwMsg(false,TRACE_FLAG,0,"Out assignUserToDialogue"); 
}

static std::map<string, std::map<int, TcapUserAddressInformation> > userDialogues;

static std::map<int, std::map< int, TcapUserAddressInformation > > stackDialogues;

static INGwIfrUtlBitArray *gsDialogueStateMap;
static INGwIfrUtlBitArray *gsDialogueStateMapClone = NULL;

INGwIfrUtlBitArray* 
TcapMessage::cloneDialogueStateMap(){
  LOCK();
  if(NULL != gsDialogueStateMapClone) 
  {
    gsDialogueStateMapClone->updateClone(*gsDialogueStateMap);
  }
  else{
    INGwIfrUtlBitArray *gsDialogueStateMapClone = 
                                              new INGwIfrUtlBitArray(*gsDialogueStateMap);
  }

  return gsDialogueStateMapClone;
}

void 
TcapMessage::initDialogueStateHashMap(int pOffset,int pRange) {
  logger.logINGwMsg(false,ALWAYS_FLAG,0, "In initDialogueStateHashMap"
                   "lowDlgId <%d> Range <%d>",pOffset, pRange);

  try 
  {
    gsDialogueStateMap = new  INGwIfrUtlBitArray(pOffset,pRange + g_maxNmbOutDlg);
  }
  catch(bad_alloc& ba ){
    logger.logINGwMsg(false,ERROR_FLAG,0,"HEAP MEM EXHAUSTED Exception:<%s>",
                      ba.what());
    exit(1);
  }

  logger.logINGwMsg(false,ALWAYS_FLAG,0, "Out initDialogueStateHashMap");
}

void TcapMessage::storeDialogue(int userDlgId,
                                int stackDialogue,
                                AppInstId & appid,
                                const string & ipaddr,
                                TcapUserAddressInformation &tuai,
 																bool forStandby)
{
  logger.logINGwMsg(false,TRACE_FLAG,0,"In storeDialogue +rem+");
  LOCK();
  
  if(!(INGwIfrPrParamRepository::getInstance().getPeerStatus())) {
    tuai.isDuplicated = true;
  }  
  
  if(userDlgId == stackDialogue) 
  {
    gsDialogueStateMap->setBitState(stackDialogue);
  }
  else{
    gsDialogueStateMap->setBitState(userDlgId);
    //WIN FT changes
  }

  if (false == forStandby){
  	MsrMgr::getInstance()->increment("Active Call", "INGw", "Active Call");

    // For debugging - Start [
    if (g_enableDiagLogs) {
      unsigned long  actCalls = 0;
	    MsrMgr::getInstance()->getValue("Active Call", "INGw", 
		  			                          "Active Call", actCalls);
      if (((actCalls > mActCallsThres) && (!actCallIncDiagTriggered))) {
        actCallIncDiagTriggered = true;
        char logInfo[256];
        sprintf(logInfo,
                "ACTIVE CALL DEBUG START ThrdId<%d> actCalls<%d> mActCallsThres<%d>",
                pthread_self(), actCalls, mActCallsThres);
        g_IncDiag((char *)"-s", (char *)__FILE__, __LINE__, logInfo, 1);
      }

      if ((actCalls < mActCallsThres) && (actCallIncDiagTriggered)) {
        char logInfo[256];
        sprintf(logInfo,
                "ACTIVE CALL DEBUG STOP ThrdId<%d> actCalls<%d> mActCallsThres<%d>",
                pthread_self(), actCalls, mActCallsThres);
        g_IncDiag((char *)"-k", (char *)__FILE__, __LINE__, logInfo, 1);
        actCallIncDiagTriggered = false;
      }
    }
    // ] End - For debugging 
  }

  logger.logINGwMsg(false,TRACE_FLAG,0,
                    "storeDialogue suId %d dlgid %d Ip%s",
                     appid.suId, stackDialogue,ipaddr.c_str()); 

  
  (userDialogues[ipaddr])[userDlgId] = tuai;

  if(userDlgId != stackDialogue)
  {
    (stackDialogues[appid.suId])[userDlgId] = tuai;
  }
  else
  {
    (stackDialogues[appid.suId])[stackDialogue] = tuai;
  }

  logger.logINGwMsg(false,TRACE_FLAG,0,"Out storeDialogue sizeof "
                    "userDialogues %d stackDialogues %d ",
                    (userDialogues[ipaddr]).size(),
                    (stackDialogues[appid.suId]).size());
}

void TcapMessage::removeDialogue(int userDlgId, int stackDialogue, 
                                 SccpAddr &userAddr, AppInstId & appid,
                                 const string & ipaddr, bool forStandby,
                                 bool getLock)
{
  logger.logINGwMsg(false, TRACE_FLAG, 0,
                    "In removeDialogue  uDlgId<%d> stkDlgId<%d> getLock<%d>,"
                    "ipaddr<%s>appid.suId<%d>",
                     userDlgId, stackDialogue, getLock,
                     ipaddr.c_str(),appid.suId);
 
  if(getLock) {
    pthread_mutex_lock(&mutex); 
  }

  if (false == forStandby) {
  	MsrMgr::getInstance()->decrement("Active Call", "INGw", "Active Call");
  }

  gsDialogueStateMap->resetBitState(stackDialogue);

  (userDialogues[ipaddr]).erase(userDlgId);
  (stackDialogues[appid.suId]).erase(stackDialogue);
  
  if (getLock) {
     pthread_mutex_unlock(&mutex); 
  }

  logger.logINGwMsg(false, TRACE_FLAG, 0, "Out removeDialogue");
}

void TcapMessage::cleanup(const string &p_sasIpAddr, AppInstId &p_appId,
                          bool forStandBy)
{
 logger.logINGwMsg(false, ERROR_FLAG, 0, "IN CLEANUP for SasIp[%s] suId[%d] spId[%d]",
        p_sasIpAddr.c_str(), p_appId.suId, p_appId.spId);

 if(forStandBy ==false /*&& 
    INGwIfrPrParamRepository::getInstance().getPeerStatus()*/)
 {
   INGwTcapFtHandler::getInstance().sendSasHBFailureMsg(p_sasIpAddr,p_appId);
 } 


 LOCK();
 int usrDlgCount = (userDialogues[p_sasIpAddr]).size();
 (userDialogues[p_sasIpAddr]).clear();
 userDialogues.erase(p_sasIpAddr);

 std::map<int, TcapUserAddressInformation> & tuaiMap = 
 				(stackDialogues[p_appId.suId]);

 vector<int> stackDlgIdList;
 std::map<int, TcapUserAddressInformation>::iterator iter = tuaiMap.begin();

 for(iter; iter != tuaiMap.end(); iter++) {
   logger.logINGwMsg(false,TRACE_FLAG,0,"[cleanup] +rem+ sasip %s dialogueId %d",((*iter).second).ipaddr.c_str(),
   (*iter).first);  
 	 if (p_sasIpAddr == ((*iter).second).ipaddr)
   { 
     logger.logINGwMsg(false,TRACE_FLAG,0,"[cleanup] sasip %s dialogueId %d",((*iter).second).ipaddr.c_str(),
     (*iter).first);  
 	 	 stackDlgIdList.push_back((*iter).first);
 	 }
 }

 logger.logINGwMsg(false,TRACE_FLAG,0,"[cleanup] stackDlgIdList <%d>",
                   stackDlgIdList.size());

 //TcapMessage **lCallMap = INGwTcapIncMsgHandler::getInstance().getCallMap();
 map <int, INGwTcapSession*> *lTcapSessionMap;
 map <int, INGwTcapSession*>::iterator lSessionIter;

 if(true == forStandBy) {
   lTcapSessionMap = INGwTcapFtHandler::getInstance().getTcapSessionMap();
 }

 for( int i=0; i < stackDlgIdList.size(); i++) {
   logger.logINGwMsg(false,TRACE_FLAG,0,"[cleanup] dlgId %d",
   stackDlgIdList[i]);
   if(true == forStandBy) {
      lSessionIter = lTcapSessionMap->find(stackDlgIdList[i]);
      if(lSessionIter != lTcapSessionMap->end()) {
        if(lSessionIter->second) {
          lSessionIter->second->cleanTcapSession(false);
          delete lSessionIter->second;
          lSessionIter->second = NULL;
          lTcapSessionMap->erase(stackDlgIdList[i]);
        }
      }

      gUpdateReplayCallCnt(stackDlgIdList[i]);
      continue;
   }
  
   logger.logINGwMsg(false,TRACE_FLAG,0, "[cleanup]sending abort dlgId %d",
          stackDlgIdList[i]);
   closeDialogue(0x01, stackDlgIdList[i],p_appId.suId, p_appId.spId, false, false, true);

 		MsrMgr::getInstance()->decrement("Active Call", "INGw", "Active Call");
 }

 logger.logINGwMsg(false, ERROR_FLAG, 0, "OUT cleanup Called for SasIp[%s]",
        p_sasIpAddr.c_str());
}

static TcapUserAddressInformation *invalid = new TcapUserAddressInformation();

TcapUserAddressInformation & TcapMessage::getUserAddressBy(int userDialogue,
                                                       const string & ipaddr)
{

      printCallMaps(false);

      std::map<int, TcapUserAddressInformation> & tuaiMap = userDialogues[ipaddr];
      std::map<int, TcapUserAddressInformation>::iterator iter = tuaiMap.find(userDialogue);
      if (iter == tuaiMap.end()) {
        logger.logINGwMsg(false,TRACE_FLAG,0,
          "+rem+getUserAddressBy dialogue Id %d ip <%s> Not found In map",
                                userDialogue, ipaddr.c_str());
            return *invalid;
      }
      else{
        logger.logINGwMsg(false,TRACE_FLAG,0,
          "+rem+getUserAddressBy found the dialogue %d",userDialogue);
      }
      return (*iter).second;
}

TcapUserAddressInformation & TcapMessage::getUserAddressBy(int stackDialogue,
                                                           AppInstId & appid)
{
  logger.logINGwMsg(false, TRACE_FLAG, 0,
                   "In TcapMessage::getUserAddressBy");
  std::map<int, TcapUserAddressInformation> & tuaiMap = 
                                                  (stackDialogues[appid.suId]);
  std::map<int, TcapUserAddressInformation>::iterator iter = 
                                                  tuaiMap.find(stackDialogue);

  if (iter == tuaiMap.end()){
     logger.logINGwMsg(false, TRACE_FLAG, 0,
                       "Out TcapMessage::getUserAddressBy"
                       " Tuai Info NOT present %d",stackDialogue);
        return *invalid;
  }

  logger.logINGwMsg(false, TRACE_FLAG, 0,
                   "Out TcapMessage::getUserAddressBy");
  return (*iter).second;
}

bool TcapUserAddressInformation::isValid() const
{
      return this != invalid;
}

bool TcapMessage::getUserAddress(string &returnAddress)
{
 logger.logINGwMsg(false, TRACE_FLAG, 0,
                   "In TcapMessage::getUserAddress");
      if (ipaddr.empty())
      {
           LOCK();
           
           appid.suId = m_suId;
           appid.spId = m_spId;
   
           logger.logINGwMsg(false,TRACE_FLAG,0,"getUserAddress suId %d spId %d",
             appid.suId, appid.spId);

           int liUserDlgId = getDialogue();

           TcapUserAddressInformation & tuai = getUserAddressBy(liUserDlgId, appid);
           if (tuai.isValid())
           {
              ipaddr = returnAddress = tuai.ipaddr;
              if(tuai.did != tuai.userDlgId)
              {
                //the dialogue has been initiated by sas and we need to update spdlgId
                tuai.did = dlgR.spdlgId;
                logger.logINGwMsg(false, VERBOSE_FLAG, 0, "TcapMessage updated "
                                  "spdlgId in tuai as <%d>", tuai.did);
              }
              int libillingNo = tuai.mBillingId.getBillingNo();
              mBillingId.setBillingNo(libillingNo);
              //check if TUAI is replicated or not
                 
              if(!(INGwIfrPrParamRepository::getInstance().getPeerStatus())
                 && false == tuai.isDuplicated)
              {
                 tuai.isDuplicated = true;
                 tuai.mDlgType = this->dlgR.dlgType;
                    
                   logger.logINGwMsg(false,VERBOSE_FLAG,0,
                                     "getUserAddress TrState<%d>",
                                     tuai.mDlgType);

                 AppInstId appid;
                 appid.suId = tuai.suId;
                 appid.spId = tuai.spId;

                 INGwTcapFtHandler::getInstance().
                 tcapUserInformationForReplication(
                                      tuai.did, 
                                      tuai.userDlgId, 
          			 		                  tuai.srcaddr,
                                      tuai.destaddr, 
                                      tuai.ipaddr, appid,
                                      #if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)
                                      tuai.ansiDlgEv,
                                      tuai.mBillingId.getBillingNo()
                                      #else
                                      tuai.objAcn
                                      #endif
                                      );
              }
              return true;
           }
           else{
             logger.logINGwMsg(false,TRACE_FLAG,0,"getUserAddress returning "
               "invalid for dlgid %d ",getDialogue()); 
           }
      }
      else
      {
           logger.logINGwMsg(false,TRACE_FLAG,0,"+rem+getUserAddress got IP %s",
             ipaddr.c_str());
   
           returnAddress = ipaddr;
           return true;
      }
 logger.logINGwMsg(false,TRACE_FLAG, 0,
                   "Out TcapMessage::getUserAddress");
      return false;
}

U8* TcapMessage::createConfiguration(/*const*/ std::list<SccpAddr> &addrlist,
                                                unsigned int* p_bufLen)
{
  char lBuf [1024];
  int lBufLen = 0;
  logger.logINGwMsg(false, ALWAYS_FLAG, 0,"In createConfiguration");
  std::list<SccpAddr>::const_iterator iter = addrlist.begin();

  U8* buffer  = getBuffer(256); 
  //track current length this will be pointing to next available byte
  int length=0;
  //to store index where length is to be inserted     
  int indexTotalLen=0;
  int temp_len = 0;
  addByte(buffer,length,INC_CONFIG_ACK);
  //leaving index to add total length
  indexTotalLen = length++;
  addByte(buffer, length,INC_CONFIG);
  SccpAddr addr;
  for (;iter != addrlist.end(); iter++)
  {
    memcpy(&addr,&(*iter),sizeof(SccpAddr));
    encodeSccpAddr(buffer,length,&addr,ORIG_SUA);           
    dumpSpAddr(addr,"Creating Configuration Buffer"); 
         
  }
  encodeContentLength(buffer,length,indexTotalLen,length -2);
  *p_bufLen = length;
  
  g_printHexBuff(buffer, length, Logger::TRACE);
  if(mLogger->getLoggingLevel()) {
    lBufLen += sprintf(lBuf + lBufLen,
      "\n Configuration buffer: \n");

    for(int ii=0;ii<length;ii++){
      if(0 == ii%16){
        lBufLen += sprintf(lBuf + lBufLen,"%s","\n\t");
      }
      lBufLen += sprintf(lBuf + lBufLen,
         " %02X", buffer[ii]);
    }
    lBufLen += sprintf(lBuf + lBufLen,"%s","\n");
    mLogger->dumpCodecMsg(lBuf,ENC);
  }
  logger.logINGwMsg(false, ALWAYS_FLAG, 0,"%s Out createConfiguration",lBuf);
  return buffer;
}

U8* TcapMessage::createNpcstateInd(SccpAddr &p_ownaddr, SccpAddr &p_affectedDpc,
                                            U8 p_sps, unsigned int* p_bufLen)
{
 logger.logINGwMsg(false, ALWAYS_FLAG, 0,"in createNpcstateInd");
  int realloc_count = 0;
  U8* buffer  = getBuffer(256);
  //track current length
  int length=0;
  //store index where length is to be inserted
  int index=0; 
  //tag for sccp management messages
  addByte(buffer,length,INC_SCCP_MGMT_MSG);
  index = length++;
  addByte(buffer,length,INC_NPCSTATE_IND);
  
  addByte(buffer,length,INC_SPS);
  if(p_sps == SS_UOS)
  {
  addByte(buffer,length,SAS_UOS);
  }
  else if(p_sps == SS_UIS)
  {
  addByte(buffer,length,SAS_UIS);
  }
  else
  {
  logger.logINGwMsg(false, ERROR_FLAG, 0,"Unknown SPS");
  }
  addByte(buffer,length,PROTOCOL_VARIANT);
  addByte(buffer,length,SW_JAPAN);
  
  addByte(buffer,length,INC_AFFECTED_DPC);
  addInteger(buffer,length,p_affectedDpc.pc);
            
  addByte(buffer,length,INC_OWN_PC);
  addInteger(buffer,length,p_ownaddr.pc);
  encodeContentLength(buffer,length,index,length-index-1);
  
  *p_bufLen = length;
  logger.logINGwMsg(false, ALWAYS_FLAG, 0,"Out createNpcstateInd");
  return buffer;  
}

U8* TcapMessage::createNstateInd(SccpAddr &p_ownaddr, SccpAddr &p_affectedAddress,
                                          U8 us, unsigned int* p_bufLen)
{
  logger.logINGwMsg(false, ALWAYS_FLAG, 0,"In createNstateInd");
  U8* buffer  = getBuffer(256);
   
  int length=0;
  int index=0;
  int temp_len = 0;
  addByte(buffer,length,INC_SCCP_MGMT_MSG);
  index=length++;
  addByte(buffer,length,INC_NSTATE_IND);
  
  addByte(buffer,length,INC_USER_STATUS);
  if(us == 0x04) {
    addByte(buffer,length,0x01);
  }
  else{
    addByte(buffer,length,0x00);
  }
  
  if(G_SUCCESS != encodeSccpAddr(buffer,length,(&p_ownaddr),INC_AFFECTED_USER)){
    logger.logINGwMsg(false,ERROR_FLAG,0,"[createNstateInd]Quitting encode, failure in encodeSccpAddr");
    return 0;
  }
  
  *p_bufLen = length;
  temp_len =length-index-1;
  encodeContentLength(buffer,length,index,temp_len);
  if(0 < mLogger->getLoggingLevel()) {
    char lBuf[512]; 
    int lBufLen = 0; 

    lBufLen += sprintf(lBuf + lBufLen,"%s",
    "\n----------SSN STATE INDICATION----------");

    lBufLen += sprintf(lBuf + lBufLen,
      "\nBuffer Length = %d \n",length);
    for(int i=0;i<length; i++) {
      if(0 == i%16){
        lBufLen += sprintf(lBuf + lBufLen,"%s","\n\t");
      }
      lBufLen += sprintf(lBuf + lBufLen,"%02X ",buffer[i]);
    }
    lBufLen += sprintf(lBuf + lBufLen,"%s",
    "\n----------SSN STATE INDICATION----------");
    mLogger->dumpCodecMsg(lBuf,ENC); 
  }
  logger.logINGwMsg(false, ALWAYS_FLAG, 0,"Out createNstateInd");
  return buffer;   
}

void TcapMessage::providerAbort(int userDialogue, U8 pa)
{

  U8* buffer  = getBuffer(128);

  int length=0;
  int indexTotalLen=0;
  int lengthOfContent = 0;
  addByte(buffer,length,DLGTYPE);
  indexTotalLen = length++;
  addByte(buffer,length,JAIN_PROVIDER_ABORT);
  addByte(buffer,length,DLGID);
  addInteger(buffer, length, userDialogue);
  
  addByte(buffer,length,ABORT_CAUSE);
  switch(pa)
  {
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)   
    case STU_ANSI_ABORT_UP:    /* unrecognized package type */
           addByte(buffer,length,JAIN_ABORT_UNREC_MSG);
    break;

    case STU_ANSI_ABORT_IN:    /* incorrect transaction portion */
           addByte(buffer,length,JAIN_ABORT_INC_TRANS);
    break;

    case STU_ANSI_ABORT_BD:    /* badly structured transaction portion */
           addByte(buffer,length,JAIN_ABORT_BADLY_STRUCTURED_DLG_PORTION);
    break;

    case STU_ANSI_ABORT_UT:    /* unrecognized transaction ID */
           addByte(buffer,length,JAIN_ABORT_UNREC_TRS);
    break;

    case STU_ANSI_ABORT_PR:    /* Permission to release problem */
           addByte(buffer,length,JAIN_ABORT_PERMISSION_TO_RELEASE_PROBLEM);
    break;

    case STU_ANSI_ABORT_RN:    /* resource not available */
           addByte(buffer,length,JAIN_ABORT_RESOURCE);
    break;


    case ST_ANS_PABT_UR_DPID:  /* Unrecognized dialogue portion id */
           addByte(buffer,length,JAIN_ABORT_UNRECOG_DLG_PORTION_ID);
    break;

    case ST_ANS_PABT_BD_DLGP:  /* Badly structured Dialogue portion */
           addByte(buffer,length,JAIN_ABORT_BADLY_STRUCTURED_DLG_PORTION);
    break;

    case ST_ANS_PABT_MS_DLGP:  /* Missing dialogue portion */
           addByte(buffer,length,JAIN_ABORT_MISSING_DIALOGUE_PORTION);
    break;

    case ST_ANS_PABT_IC_DLGP:  /* Inconsistent dialogue portion */
           addByte(buffer,length,JAIN_ABORT_INCONSISTENT_DIALOGUE_PORTION);
    break;

#else

    case INC_ABORT_UNREC_MSG:
      addByte(buffer,length,JAIN_ABORT_UNREC_MSG);
    break;
    
    case INC_ABORT_UNREC_TRS:
      addByte(buffer,length,JAIN_ABORT_UNREC_TRS);
    break;
    
    case INC_ABORT_BAD_FRMT:
      addByte(buffer,length,JAIN_ABORT_BAD_FRMT);
    break;
    
    case INC_ABORT_INC_TRANS:
      addByte(buffer,length,JAIN_ABORT_INC_TRANS);
    break;
    
    case INC_ABORT_RESOURCE:
      addByte(buffer,length,JAIN_ABORT_RESOURCE);
    break;
    
    case INC_ABORT_ABNML_DLG:
      addByte(buffer,length,JAIN_ABORT_ABNML_DLG);
    break;
    
    case INC_ABORT_NO_CMN_DLG:
      addByte(buffer,length,JAIN_ABORT_ABNML_DLG);// confirm
    break;
    
    case INC_ABORT_UNEXP_MSG:
      addByte(buffer,length,JAIN_ABORT_ABNML_DLG);//confirm 
    break;
    
    case INC_ABORT_MISC_ERR:
      addByte(buffer,length,JAIN_ABORT_ABNML_DLG);//confirm
    break;

#endif      
    default:
      logger.logINGwMsg(false,ERROR_FLAG,0,"No Cause Matched for PAbort");
      addByte(buffer,length,JAIN_ABORT_INC_TRANS);
  }//ending switch cause for ITU
  encodeContentLength(buffer, length, indexTotalLen, length-indexTotalLen-1);
  
}

void TcapMessage::closeDialogue(U8 abortReason, int stackDialogue, 
                            S16 p_suId, S16 p_spId,
                            bool isFirstMsg, bool getLock, bool isHbFailClnup, 
                            bool auditFlg)
{
 	logger.logINGwMsg(false, ERROR_FLAG, 0,
    "In closeDialogue() stkDlg %d suId %d "
    "spId %d isFirstMsg %d getLock %d auditFlg<%d>",
    stackDialogue, p_suId, p_spId, isFirstMsg, getLock, auditFlg);

  int lCurVal = 0;

  AppInstId appId;
  appId.suId = p_suId;
  appId.spId = p_spId;
  abortReason = lcAbrtCause;
  //1 all abort
  //2 abort and end based on transaction state

  int lbAbrtOrEnd = 2;

  U8 lcRc = 0x01;
  U8 lcAc = 0x01;

  if (getLock) {
    pthread_mutex_lock(&mutex);
  }

    TcapUserAddressInformation & tuai = 
    			TcapMessage::getUserAddressBy(stackDialogue, appId);

  if ((false == auditFlg) && (false == tuai.isValid()) && (!isFirstMsg)) 
  {
  	 logger.logINGwMsg(false, ERROR_FLAG, 0,"Out closeDialogue invalid tuai");

    if (getLock) {
       pthread_mutex_unlock(&mutex); 
    }
  	 return;
  }
  
  bool lLock = (isHbFailClnup)?false:(!getLock);

  if(false == auditFlg) {
    TcapMessage::removeDialogue(tuai.userDlgId, tuai.did, dstaddrR,
                                appId, tuai.ipaddr, false, lLock);
  }

  if((false == auditFlg) && (!((INC_BEGIN== tuai.mDlgType) || (INC_CONTINUE == tuai.mDlgType)))){
  	 logger.logINGwMsg(false, ERROR_FLAG, 0,"Out closeDialogue TC-UNI.");

    if (getLock) {
       pthread_mutex_unlock(&mutex); 
    }
    return;
  }


  INGwTcapWorkUnitMsg lworkUnit;
  memset(&lworkUnit,0,sizeof(INGwTcapWorkUnitMsg));
  TcapMessage *ltcMsg = new TcapMessage;
  lworkUnit.m_tcapMsg = ltcMsg;
   
  ltcMsg->dlgClndByINGw = true;

  // create an Abort message
  ltcMsg->dlgR.dstAddr = new SccpAddr;
  ltcMsg->dlgR.srcAddr = new SccpAddr; 


  ltcMsg->dlgR.pres = true;

#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
  U8 lcDlgType = STU_ANSI_UABORT;
#else
  U8 lcDlgType = INC_U_ABORT;
#endif;

  TcapComp *lpTcapCmp;

  
  logger.logINGwMsg(false,VERBOSE_FLAG,0,"closeDialogue Tr State: <%d> "
                    "close Type <%d> abortCause <%d>", tuai.mDlgType, 
                    lcdlgCloseOpt, lcAbrtCause);

  if((true == auditFlg) && (INC_BEGIN == tuai.mDlgType)){
    ltcMsg->dlgR.resultPres = true;
    //as per discussion with Rajeev we will set reason as ST_ETS_DLG_REFUSED
    //even if it is not complying with NTT standards.
    //spectra wont be able to decode the same. 

    ltcMsg->dlgR.reason = abortReason;
    //ltcMsg->dlgR.reason = ST_ETS_DLG_REFUSED;
    //ltcMsg->dlgR.reason = 2;
  }
  else if(INC_CONTINUE == tuai.mDlgType) 
  {
    if(1 == lcdlgCloseOpt) 
    {
      lworkUnit.compPres  = false; 
      ltcMsg->dlgR.resultPres = false;
      stringBuff uInfo = INGwSilTx::instance().getUAbortUInfo();
      ltcMsg->dlgR.uInfo.len = uInfo.len;
      ltcMsg->dlgR.uInfo.string = new U8[uInfo.len];
      memcpy(ltcMsg->dlgR.uInfo.string, uInfo.string, uInfo.len);
      ltcMsg->dlgR.uInfo.string[uInfo.string, uInfo.len - 1] = abortReason;
    }
    else 
    {
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
      lcDlgType = STU_RESPONSE;
#else
      lcDlgType = INC_END;
#endif
      //create invoke component and set RC value

      U8  *lpcParamBuf; 
      U8 lcParamLen = 0;
      stringBuff lRCParamBuf = INGwSilTx::instance().getRCParamBuf();

      if((NULL == lRCParamBuf.string) && lRCParamBuf.len) 
      {
        U8 ParamBuf[4] = {0x04, 0x02, 0x83, 0xA9};
        lpcParamBuf = new U8[4];
        lcParamLen = 4;
        memcpy(lpcParamBuf, ParamBuf, 4);
      }
      else 
      {
        lpcParamBuf = new U8[lRCParamBuf.len];
        lcParamLen = lRCParamBuf.len;
        memcpy(lpcParamBuf, lRCParamBuf.string,lRCParamBuf.len); 
      }

      createCmpEvent(&lpTcapCmp,
                     INC_INVOKE,     //acCompType,      
                     0x7F,           //acInvId,      
                     INC_LOCAL,      //acOpType,        
                     0x16,           //acOpCode,         
                     1,              //acOpCodeLen,       
                     lcParamLen,     //acParamLen,        
                     lpcParamBuf,    //acParamString,
                     INC_OPRCLASS1); //acOpClass)    


     ltcMsg->compPresR  = true;
     lworkUnit.compPres = true;
     ltcMsg->mCompVector.push_back(lpTcapCmp);

    }
  }

  lworkUnit.m_suId = p_suId;
  lworkUnit.m_spId = p_spId;

  lworkUnit.eventType = EVTSTUDATREQ;
  lworkUnit.m_dlgId   = stackDialogue; 
  ltcMsg->dlgR.sudlgId  = stackDialogue;
  ltcMsg->dlgR.spdlgId  = stackDialogue;
  
  if(false == auditFlg) 
  {
    memcpy(ltcMsg->dlgR.dstAddr, &tuai.destaddr , sizeof(SccpAddr));
    memcpy(ltcMsg->dlgR.srcAddr, &tuai.srcaddr, sizeof(SccpAddr));
  

    if(0 != tuai.objAcn.len) {
      memcpy(ltcMsg->dlgR.objAcn.string, tuai.objAcn.string, tuai.objAcn.len);
      ltcMsg->dlgR.objAcn.len = tuai.objAcn.len;
    }
  }
  else 
  {
    memset(ltcMsg->dlgR.dstAddr, 0, sizeof(SccpAddr));
    memset(ltcMsg->dlgR.srcAddr, 0, sizeof(SccpAddr));
  }

  if (getLock) {
    pthread_mutex_unlock(&mutex); 
  }

  ltcMsg->dlgR.dlgType  = lcDlgType;
  ltcMsg->dlgR.endFlag  = false;//basic end
  ltcMsg->dlgR.uInfo.len = 0; 
  //ltcMsg->dlgR.uInfo.string
  ltcMsg->appid = appId;

  if(INC_END == lcDlgType)
  {
    INGwIfrSmStatMgr::instance().increment(
                 INGwTcapStatParam::INGW_TRIGR_END_INDEX, lCurVal, 1); 
  }
  else if(INC_U_ABORT == lcDlgType)
  {
    INGwIfrSmStatMgr::instance().increment(
                 INGwTcapStatParam::INGW_TRIGR_UABRT_INDEX, lCurVal, 1); 
  }
  else
  {
      logger.logINGwMsg(false,ERROR_FLAG,0,"closeDialogue dlgType unknown "
                        " <%d> ",stackDialogue);
  }

  int retVal = INGwSilTx::instance().sendTcapReq(&lworkUnit); 

  if (INC_ROK == retVal) {
  	MsrMgr::getInstance()->decrement("Active Call", "INGw", "Active Call");
  }
  else {
  	logger.logINGwMsg(false, ERROR_FLAG, 0,
  	"[closeDialogue] Err in sending <%d> for stackDlgId[%d]",lcDlgType,
     stackDialogue);
  }

  delete ltcMsg;
  ltcMsg = 0;

 	logger.logINGwMsg(false, TRACE_FLAG, 0,"Out closeDialogue");
}

void TcapMessage::Initialize()
{
}

void TcapMessage::Terminate()
{
}


U8* 
TcapMessage::encode(TcapDlg currentDlg, 
                                   vector<TcapComp *>* compVector,
                                   unsigned int * p_bufLen)
{
  logger.logINGwMsg(false, TRACE_FLAG,0, "In  TcapMessage::encode");
  int length=0;        
  //To store index where length will be added
  int index = 0; 

  U8* buffer = getBuffer(512);
  if(buffer == 0){
    logger.logINGwMsg(false, ERROR_FLAG, 0,"+VER+ "
                      "Out of Memory***QUITTING ENCODE***");
    return NULL; 
  }                    
  else{
    logger.logINGwMsg(false, TRACE_FLAG, 0,
                      "Allocated [%d] Bytes Successfully",mBufSize);
  }
  memset(buffer, 0, mBufSize);

  //calulate dlg or component length
  int ldlgCompLen =  0;       

  //adding tag for dialogue type
  addByte(buffer,length,DLGTYPE);
  //at length index we will store length of dialogue so incrementing length
  index = length;
  length+=1;//lengthIndex

  logger.logINGwMsg(false, VERBOSE_FLAG, 0,
    "[encode] DlgType= %d",currentDlg.dlgType);
  switch(currentDlg.dlgType)
  {
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
    case STU_QRY_PRM:
    case STU_QRY_NO_PRM:
#else
    case INC_BEGIN:
#endif

    {
      logger.logINGwMsg(false, VERBOSE_FLAG,0,"[encode]encoding Begin ");
      addByte(buffer,length,JAIN_BEGIN);

      addByte(buffer,length,DLGID);
      addInteger(buffer,length,currentDlg.spdlgId);
      logger.logINGwMsg(false, VERBOSE_FLAG,0,"Added DlgID <%d> ,Index <%d>",
        currentDlg.spdlgId, length);

      dumpSpAddr (*(currentDlg.dstAddr),"[encode] encoding destination Address");
      U32 tmpVal = currentDlg.dstAddr->pc;
      currentDlg.dstAddr->pc = currentDlg.dpc;
      encodeSccpAddr(buffer,length,currentDlg.dstAddr,DEST_SUA); 
      currentDlg.dstAddr->pc = tmpVal;
       
      dumpSpAddr (*(currentDlg.srcAddr),"[encode] Encoding Source Address");

      tmpVal = currentDlg.srcAddr->pc;
      currentDlg.srcAddr->pc = currentDlg.opc; 
      encodeSccpAddr(buffer,length,currentDlg.srcAddr,ORIG_SUA);
      currentDlg.srcAddr->pc =  tmpVal;

      if(
        #if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
         currentDlg.ansiDlgEv.pres.pres
        #else
         0 != currentDlg.objAcn.len 
        #endif
      
        )

      {


#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)     
        if(!encodeAnsiDlgEv(currentDlg.ansiDlgEv, buffer, length))
        {
          logger.logINGwMsg(false,ERROR_FLAG,0,"Error encoding ANSI dlgPrtn");
          return NULL;
        }
#else/*ITU*/

        logger.logINGwMsg(false, VERBOSE_FLAG,0,"<encode>encode ACN ACN Type: %s",
        (currentDlg.acnType == INC_ENC_TYP_OID)?"INC_ENC_TYP_OID":
        (currentDlg.acnType == INC_ENC_TYP_INT)?"INC_ENC_TYP_INT":"UNKNOWN");

        addByte(buffer,length,APP_CONTEXT_NAME_TYPE);

        switch(currentDlg.acnType)
        {
          case INC_ENC_TYP_INT:

            logger.logINGwMsg(false,ERROR_FLAG,0,"int ACN Not supported in ITU");
            return NULL;

          break;
          case INC_ENC_TYP_OID:
          {
            addByte(buffer, length, JAIN_ACN_TYPE_OID);
            
            addByte(buffer,length,APP_CONTEXT_NAME);
            
            encodeBuffer(buffer, currentDlg.objAcn.len, length,
                         currentDlg.objAcn.string); 
          }
          break;

          default:
          logger.logINGwMsg(false, ERROR_FLAG, 0,"Unknown value of AcnType <%d>"
          " *********QUITTING ENCODE*********",currentDlg.acnType);
          return NULL; 
        }  
#endif


      }
      else{
        logger.logINGwMsg(false,ERROR_FLAG,0,"<encode>encode ACN");
      }

      addByte(buffer,length,QOS);
#ifndef _QOS                          
      addByte(buffer,length,INC_ZERO);
#else                        
      addByte(buffer,length,currentDlg.qosSet.msgPrior);
      addByte(buffer,length,currentDlg.qosSet.retOpt);
      addByte(buffer,length,currentDlg.qosSet.seqCtl);

      #ifdef SS7_REUSE_SLS
        addByte(buffer,length,currentDlg.qosSet.lnkSel >> 8 & 0xff);
        addByte(buffer,length,currentDlg.qosSet.lnkSel & 0xff);
      #endif
#endif
 
      //adding new tags for Query with/without permission  
      // 0x01 : with permission
      // 0x02 : without permission
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
      if(STU_QRY_PRM    == currentDlg.dlgType) 
      {
        addByte(buffer,length,TAG_PERMISSION);
        addByte(buffer,length,0x01);
      }
      else if(STU_QRY_NO_PRM == currentDlg.dlgType) 
      {
        addByte(buffer,length,TAG_PERMISSION);
        addByte(buffer,length,0x00);
      }
#endif
    }
    break;
                      
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
    case STU_CNV_PRM:
    case STU_CNV_NO_PRM:
#else
    case INC_CONTINUE:
#endif
    { 
      addByte(buffer,length,JAIN_CONTINUE);
      addByte(buffer,length,DLGID);
      addInteger(buffer, length,currentDlg.spdlgId);
      
      //setting tag for QOS
      addByte(buffer,length,QOS);

#ifndef _QOS                          
      addByte(buffer,length,INC_ZERO);
#else                        
      addByte(buffer,length,currentDlg.qosSet.msgPrior);
      addByte(buffer,length,currentDlg.qosSet.retOpt);
      addByte(buffer,length,currentDlg.qosSet.seqCtl);

      #ifdef SS7_REUSE_SLS
        addByte(buffer,length,currentDlg.qosSet.lnkSel >> 8 & 0xff);
        addByte(buffer,length,currentDlg.qosSet.lnkSel & 0xff);
      #endif

#endif
                          
      //adding new tags for conversation with/without permission  
      // 0x01 : with permission
      // 0x02 : without permission
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
      if(STU_CNV_PRM == currentDlg.dlgType) 
      {
        addByte(buffer,length,TAG_PERMISSION);
        addByte(buffer,length,0x01);
      }
      else if(STU_CNV_NO_PRM == currentDlg.dlgType) 
      {
        addByte(buffer,length,TAG_PERMISSION);
        addByte(buffer,length,0x00);
      }
#endif
    } 

    break;

#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
    case STU_RESPONSE:
#else
    case INC_END :
#endif
    { 
      addByte(buffer,length,JAIN_END);
      addByte(buffer,length,DLGID);
      addInteger(buffer, length,currentDlg.spdlgId);//confirm on this
      
      addByte(buffer,length,QOS);//setting tag for QOS

#ifndef _QOS                          
      addByte(buffer,length,INC_ZERO);
#else                        
      addByte(buffer,length,currentDlg.qosSet.msgPrior);
      addByte(buffer,length,currentDlg.qosSet.retOpt);
      addByte(buffer,length,currentDlg.qosSet.seqCtl);

      #ifdef SS7_REUSE_SLS
      addByte(buffer,length,currentDlg.qosSet.lnkSel >> 8 & 0xff);
      addByte(buffer,length,currentDlg.qosSet.lnkSel & 0xff);
      #endif

#endif
    }
    break;
  
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)  
    case STU_ANSI_UABORT:            
#else 
    case INC_U_ABORT :            
#endif
    { 
      addByte(buffer,length,JAIN_USER_ABORT);
      addByte(buffer,length,DLGID);
      addInteger(buffer, length,currentDlg.spdlgId);
      if(currentDlg.uInfo.len>0){
        addByte(buffer,length,ABORT_INFO);
        encodeBuffer(buffer,currentDlg.uInfo.len,length,currentDlg.uInfo.string);
      }

      if(currentDlg.reason >0){
        if(currentDlg.reason == ST_DLG_RSD_NOACN){
          addByte(buffer,length,ABORT_CAUSE);
          addByte(buffer,length,JAIN_ABRT_RSN_ACN_NOT_SUP);     
        }
       
        else if(currentDlg.reason == ST_DLG_RSD_NCDLG   || 
                currentDlg.reason == ST_ETS_DLG_REFUSED ||
                currentDlg.reason == ST_DLG_RSD_NORSN){
          addByte(buffer,length,ABORT_CAUSE);
          addByte(buffer,length,JAIN_ABRT_RSN_USR_SPECIFIC);    
        }
        
        else{
          logger.logINGwMsg(false, VERBOSE_FLAG, 0,
                 "Invalid UABORT reason");
        } 
      }
    }  
    break;

#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)  
    case STU_ANSI_PABORT:
#else                        
    case INC_P_ABORT:
#endif
    {                 
      addByte(buffer,length,JAIN_PROVIDER_ABORT);
      addByte(buffer,length,DLGID);
      addInteger(buffer, length,currentDlg.spdlgId);
      addByte(buffer,length,QOS);
#ifndef _QOS                          
      addByte(buffer,length,INC_ZERO);
      #else                        
      addByte(buffer,length,currentDlg.qosSet.msgPrior);
      addByte(buffer,length,currentDlg.qosSet.retOpt);
      addByte(buffer,length,currentDlg.qosSet.seqCtl);

      #ifdef SS7_REUSE_SLS
        addByte(buffer,length,currentDlg.qosSet.lnkSel >> 8 & 0xff);
        addByte(buffer,length,currentDlg.qosSet.lnkSel & 0xff);
      #endif
#endif                                           

      addByte(buffer,length,ABORT_CAUSE);

        switch(currentDlg.cause)
        {
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)   
          case STU_ANSI_ABORT_UP:    /* unrecognized package type */
            addByte(buffer,length,JAIN_ABORT_UNREC_MSG);
          break;

          case STU_ANSI_ABORT_IN:    /* incorrect transaction portion */
            addByte(buffer,length,JAIN_ABORT_INC_TRANS);
          break;

          case STU_ANSI_ABORT_BD:    /* badly structured transaction portion */
            addByte(buffer,length,JAIN_ABORT_BAD_FRMT);
          break;

          case STU_ANSI_ABORT_UT:    /* unrecognized transaction ID */
            addByte(buffer,length,JAIN_ABORT_UNREC_TRS);
          break;

          case STU_ANSI_ABORT_PR:    /* Permission to release problem */
            addByte(buffer,length,JAIN_ABORT_PERMISSION_TO_RELEASE_PROBLEM);
          break;

          case STU_ANSI_ABORT_RN:    /* resource not available */
            addByte(buffer,length,JAIN_ABORT_RESOURCE);
          break;

          case ST_ANS_PABT_UR_DPID:  /* Unrecognized dialogue portion id */
            addByte(buffer,length,JAIN_ABORT_UNRECOG_DLG_PORTION_ID);
          break;

          case ST_ANS_PABT_BD_DLGP:  /* Badly structured Dialogue portion */
            addByte(buffer,length,JAIN_ABORT_BADLY_STRUCTURED_DLG_PORTION);
          break;

          case ST_ANS_PABT_MS_DLGP:  /* Missing dialogue portion */
            addByte(buffer,length,JAIN_ABORT_MISSING_DIALOGUE_PORTION);
          break;

          case ST_ANS_PABT_IC_DLGP:  /* Inconsistent dialogue portion */
            addByte(buffer,length,JAIN_ABORT_INCONSISTENT_DIALOGUE_PORTION);
          break;
#else
          case INC_ABORT_UNREC_MSG:
            addByte(buffer,length,JAIN_ABORT_UNREC_MSG);
          break;
          
          case INC_ABORT_UNREC_TRS:
            addByte(buffer,length,JAIN_ABORT_UNREC_TRS);
          break;
          
          case INC_ABORT_BAD_FRMT:
            addByte(buffer,length,JAIN_ABORT_BAD_FRMT);
          break;
          
          case INC_ABORT_INC_TRANS:
            addByte(buffer,length,JAIN_ABORT_INC_TRANS);
          break;
          
          case INC_ABORT_RESOURCE:
            addByte(buffer,length,JAIN_ABORT_RESOURCE);
          break;
          
          case INC_ABORT_ABNML_DLG:
            addByte(buffer,length,JAIN_ABORT_ABNML_DLG);
          break;
          
          case INC_ABORT_NO_CMN_DLG:
            addByte(buffer,length,JAIN_ABORT_ABNML_DLG);
          break;
          
          case INC_ABORT_UNEXP_MSG:
            addByte(buffer,length,JAIN_ABORT_ABNML_DLG);
          break;
          
          case INC_ABORT_MISC_ERR:
            addByte(buffer,length,JAIN_ABORT_ABNML_DLG);
#endif
          default:
          logger.logINGwMsg(false,ERROR_FLAG,0," unrecognized pAbrt cause<%d>",
                            currentDlg.cause);
          addByte(buffer,length,JAIN_ABORT_UNREC_MSG);
        }

    }                 
    break;
                                              
    case INC_NOTICE:
    {
      addByte(buffer,length,JAIN_NOTICE);
      addByte(buffer,length,DLGID);
      addInteger(buffer, length,currentDlg.spdlgId);
      encodeSccpAddr(buffer,length,currentDlg.dstAddr,DEST_SUA);
      encodeSccpAddr(buffer,length,currentDlg.srcAddr,ORIG_SUA);
      addByte(buffer,length,REPORT_CAUSE);
      addByte(buffer,length,currentDlg.cause);
    }
    break;
 
    case INC_UNI:
    {
      logger.logINGwMsg(false, ERROR_FLAG,0, "IN INC_UNI CASE");
      addByte(buffer,length,JAIN_UNIDIRECTIONAL);
      addByte(buffer,length,DLGID);
      addInteger(buffer, length,currentDlg.spdlgId);
      
      currentDlg.dstAddr->pc = currentDlg.dpc;
      encodeSccpAddr(buffer,length,currentDlg.dstAddr,DEST_SUA); 

      currentDlg.srcAddr->pc = currentDlg.opc;  
      encodeSccpAddr(buffer,length,currentDlg.srcAddr,ORIG_SUA);
                          
      addByte(buffer,length,QOS);
#ifndef _QOS                          
      addByte(buffer,length,INC_ZERO);
#else                        
      addByte(buffer,length,currentDlg.qosSet.msgPrior);
      addByte(buffer,length,currentDlg.qosSet.retOpt);
      addByte(buffer,length,currentDlg.qosSet.seqCtl);
      #ifdef SS7_REUSE_SLS
      addByte(buffer,length,currentDlg.qosSet.lnkSel >> 8 & 0xff);
      addByte(buffer,length,currentDlg.qosSet.lnkSel & 0xff);
      #endif
#endif
    }                                                                
    break;
    
    default:
      logger.logINGwMsg(false, ERROR_FLAG, 0,"+VER+[encode]Invalid Dialogue Type "
        "**QUITTING ENCODE**");
    return NULL;
  }

  logger.logINGwMsg(false, VERBOSE_FLAG, 0,
    "Dialogue length  <%d> ",length-index-1);
  encodeContentLength(buffer,length,index,length-index-1);
  
  //starting handling of components 

  TcapComp lComp; 
    
  int lCompCount=0;
  if(compVector){
    lCompCount = (int)compVector->size();
  }
  else{
    lCompCount = 0;
  }
  for(int j=0;j<lCompCount;j++) {
    lComp = *(compVector->at(j));
    ldlgCompLen = length;
    switch(lComp.compType){
     
      case INC_INVOKE :   
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
      case STU_INVOKE_L:    
      case STU_INVOKE_NL:    
#endif
      {                                     
        addByte(buffer,length,COMPONENT_TYPE);
        index = length++; 
        addByte(buffer,length,JAIN_COMP_INVOKE);

#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
        if(lComp.status == INC_INVOKE_L){
          addByte(buffer,length,INC_LAST); 
          addByte(buffer,length,INC_TRUE);
        }

        if(lComp.status == INC_INVOKE_NL){
          addByte(buffer,length,INC_LAST); 
          addByte(buffer,length,INC_FALSE);
        }
#endif
                                                 
        addByte(buffer,length,OPERATION_TYPE);
        switch (lComp.opTag)
        {
          case  INC_NONE:
                addByte(buffer,length,JAIN_OPERATIONTYPE_LOCAL);
          break;

          case  INC_LOCAL: 
                addByte(buffer,length,JAIN_OPERATIONTYPE_LOCAL);
                       
          break;

          case  INC_GLOBAL:
                addByte(buffer,length,JAIN_OPERATIONTYPE_GLOBAL);
          break;

          case  INC_NATIONAL:
                addByte(buffer,length,JAIN_OPERATIONTYPE_GLOBAL);
          break;

          case  INC_PRIVATE: 
                addByte(buffer,length,JAIN_OPERATIONTYPE_LOCAL);
          break;
         
          default:
           logger.logINGwMsg(false,ERROR_FLAG,0,
             "+VER+ Unknown operation tag found **QUITTING ENCODE** <%d>",lComp.opTag);
           return NULL;
        }//end switch operation type tag

        //adding operation code
        addByte(buffer,length,OPERATION_CODE);
        encodeBuffer(buffer,lComp.opCode.len,length,lComp.opCode.string);

        int retVal = encodeParamBuffer(buffer,length,lComp.param,lComp.paramTag);
        if(G_FAILURE == retVal) {
          logger.logINGwMsg(false,ERROR_FLAG,0,
            "+VER+ **QUITTING ENCODE** Error in encoding param buffer");
          return NULL;
        }
        if(G_FAILURE == encodeInvokeId(buffer, length,lComp)) {
          logger.logINGwMsg(false,ERROR_FLAG,0,
            "+VER+ **QUITTING ENCODE** error in encoding Invoke Id");
          return NULL;
        }
        encodeLinkedId(buffer, length,lComp);                                                                        
        addByte(buffer,length,CLASS_TYPE);
        //overlapping with jain constants
        addByte(buffer,length,lComp.opClass);
        //end case INC_INVOKE
      }
      break;

      case INC_RET_RES_NL :
      case INC_RET_RES_L  :
      {
        addByte(buffer,length,COMPONENT_TYPE);
        index = length++;
        addByte(buffer,length,JAIN_COMP_RESULT); 
        addByte(buffer,length,INC_LAST); 
        if(lComp.compType == INC_RET_RES_NL){
          addByte(buffer,length,INC_FALSE);
        }
        else{
          addByte(buffer,length,INC_TRUE);
        }

        if(INC_NONE != lComp.opTag) 
        addByte(buffer,length,OPERATION_TYPE);

        switch (lComp.opTag) {
          
          case INC_NONE :
          {
            logger.logINGwMsg(false,VERBOSE_FLAG,0,
              "No operation tag in INC_RET_RES");
          }
          break;

          case INC_LOCAL : 
          {
            addByte(buffer,length,JAIN_OPERATIONTYPE_LOCAL);
          }
          break;

          case INC_GLOBAL :
          {
            addByte(buffer,length,JAIN_OPERATIONTYPE_GLOBAL);
          }
          break;

          case INC_NATIONAL :
          {
            addByte(buffer,length,JAIN_OPERATIONTYPE_GLOBAL);
          }
          break;

          case INC_PRIVATE : 
          {
            addByte(buffer,length,JAIN_OPERATIONTYPE_LOCAL);
          }
          break;
          default:
          logger.logINGwMsg(false,ERROR_FLAG,0,"no matching op type");
          return NULL;
        }//end switch operation type tag
        if(INC_NONE != lComp.opTag){ 
          addByte(buffer,length,OPERATION_CODE);
          encodeBuffer(buffer, lComp.opCode.len, length,lComp.opCode.string);
        }

        encodeParamBuffer(buffer, length,lComp.param, lComp.paramTag);
        encodeInvokeId(buffer, length,lComp);
        //end case INC_RET_RES_NL, INC_RET_RES_L
      }
      break;

      case INC_RET_ERR :
      {
        addByte(buffer,length,COMPONENT_TYPE);
        index = length++;
        addByte(buffer,length,JAIN_COMP_ERROR);
 
        addByte(buffer,length,ERROR_TYPE);
        switch (lComp.errTag){
          
          case INC_NONE :
          {
            logger.logINGwMsg(false,ERROR_FLAG,0,
            "No error tag in INC_RET_ERR");
          }
          break;

          case INC_LOCAL : 
          {
            addByte(buffer,length,JAIN_ERROR_LOCAL); 
          }
          break;

          case INC_GLOBAL :
          {
            addByte(buffer,length,JAIN_ERROR_GLOBAL);
          }
          break;

          case INC_NATIONAL :
          {
            addByte(buffer,length,JAIN_ERROR_GLOBAL);
          }
          break;

          case INC_PRIVATE : 
          {
            addByte(buffer,length,JAIN_ERROR_LOCAL);
          }
          break;
        }//end switch error type tag

        addByte(buffer,length,ERROR_CODE);
        encodeBuffer(buffer,lComp.errCode.len,length,lComp.errCode.string);
        encodeInvokeId(buffer, length,lComp);
        encodeParamBuffer(buffer,length,lComp.param, lComp.paramTag);
      
      }//end case for return error
      break;

      case INC_REJECT:
      {
        if(lComp.status == INC_COMP_CANCEL)
        {
          //case of local cancel
          addByte(buffer,length,COMPONENT_TYPE);
          index = length++;
          addByte(buffer,length,JAIN_COMP_LOCAL_CANCEL);
          encodeInvokeId(buffer, length,lComp);
          break;
        }

        addByte(buffer,length,COMPONENT_TYPE);
        index = length++;
        addByte(buffer,length,JAIN_COMP_REJECT);
                      
        switch (lComp.probTag)
        {
#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)          
          case STU_ANSI_PRB_NU   :     /* 0x00 not used */
          {
            logger.logINGwMsg(false,ERROR_FLAG,0,"problem Tag \"not used\"");

            addByte(buffer,length,PROBLEM_TYPE);
            addByte(buffer,length,JAIN_PROBLEM_TYPE_GENERAL);

            addByte(buffer,length,PROBLEM_CODE);
            addByte(buffer,length,JAIN_PRBLM_CODE_UNRECOGNISED_COMP);
          }
          break;

          case STU_ANSI_PRB_GEN  :     /* 0x01 general */
          {

            addByte(buffer,length,PROBLEM_TYPE);
            addByte(buffer,length,JAIN_PROBLEM_TYPE_GENERAL);
            addByte(buffer,length,PROBLEM_CODE);

            switch(lComp.probCode.string[0])
            {
              case STU_ANSI_PRB_UR_CMP:    /* unrecognized component */
                addByte(buffer,length,JAIN_PRBLM_CODE_UNRECOGNISED_COMP);
              break;

              case STU_ANSI_PRB_IN_CMP:    /* incorrect component portion */
                addByte(buffer,length,JAIN_PRBLM_CODE_MISTYPED_COM);
              break;

              case STU_ANSI_PRB_BD_CMP:    /* badly structured comp portion */
                addByte(buffer,length,JAIN_PRBLM_CODE_BADLY_STRUCTURED_COMP);
              break;

              case STU_ANSI_PRB_IN_ENC:    /* incorrect component encoding */
                addByte(buffer,length,JAIN_PRBLM_CODE_INCORRECT_COMP_CODING);
              break;
               
              default:
                logger.logINGwMsg(false,ERROR_FLAG,0,"Unexpected probCode<%d>",
                                  lComp.probCode.string[0]);

                addByte(buffer,length,JAIN_PRBLM_CODE_UNRECOGNISED_COMP);
            }
          }
          break;

          case STU_ANSI_PRB_INV  :     /* 0x02 invoke */
          {
            addByte(buffer,length,PROBLEM_TYPE);
            addByte(buffer,length,JAIN_PROBLEM_TYPE_GENERAL);
            addByte(buffer,length,PROBLEM_CODE);

            switch(lComp.probCode.string[0])
            {
              case STU_ANSI_PRB_DUP_ID: /* duplicate invoke ID */
                addByte(buffer,length,JAIN_PRBLM_CODE_DUP_INVOKE_ID);
              break;
               
              case STU_ANSI_PRB_UR_OP : /* unrecognized operation code */
                addByte(buffer,length,JAIN_PRBLM_CODE_UNREC_OPR);
              break;
               
              case STU_ANSI_PRB_IN_PRM: /* incorrect parameter  */
                addByte(buffer,length,JAIN_PRBLM_CODE_MISTYPED_PARAM);
              break;
               
              case STU_ANSI_PRB_IUR_ID: /* unrecognized correlation ID */
                addByte(buffer,length,JAIN_PRBLM_CODE_UNREC_LINKED_ID);
              break;
 
              default:
                logger.logINGwMsg(false,ERROR_FLAG,0,"Unexpected probCode<%d>",
                                  lComp.probCode.string[0]);

                addByte(buffer,length,JAIN_PRBLM_CODE_DUP_INVOKE_ID);
            }
          }
          break;

          case STU_ANSI_PRB_RR   :          /* 0x03 return result */
            switch(lComp.probCode.string[0])
            {
              case STU_ANSI_PRB_RUR_ID:       /* unrecognized correlation ID */
                addByte(buffer,length,JAIN_PRBLM_CODE_DUP_INVOKE_ID);
              break;
 
              case STU_ANSI_PRB_UX_RES:       /* unexpected return result */
                addByte(buffer,length,JAIN_PRBLM_CODE_RETURN_RESULT_UNX);
              break;
 
              case STU_ANSI_PRB_INV_IN_PRM:   /* incorrect parameter  */
                addByte(buffer,length,JAIN_PRBLM_CODE_MISTYPED_PARAM);
              break;
 
              default:
                logger.logINGwMsg(false,ERROR_FLAG,0,"Unexpected probCode<%d>",
                                  lComp.probCode.string[0]);

                addByte(buffer,length,JAIN_PRBLM_CODE_DUP_INVOKE_ID);
            }
          break;

          case STU_ANSI_PRB_RE   :     /* 0x04 return error */
          {
            switch(lComp.probCode.string[0])
            {
              case STU_ANSI_PRB_EUR_ID: /* unrecognized correlation ID */
                //cannot find corresponding JAIN error code
                addByte(buffer,length,JAIN_PRBLM_CODE_RETURN_ERROR_UNX);
              break;
 
              case STU_ANSI_PRB_UX_RER: /* unexpected return error */
                //
                addByte(buffer,length,JAIN_PRBLM_CODE_RETURN_ERROR_UNX);
              break;
 
              case STU_ANSI_PRB_UR_ERR: /* unrecognized error */
                addByte(buffer,length,JAIN_PRBLM_UNRECOGNIZED_ERROR);
              break;
 
              case STU_ANSI_PRB_UX_ERR: /* unexpected error */
                addByte(buffer,length,JAIN_PRBLM_UNEXPECTED_ERROR);
              break;
 
              case STU_ANSI_PRB_EN_PRM: /* incorrect parameter */
                addByte(buffer,length,JAIN_PRBLM_MISTYPED_PARAM);
              break;
            
              default:
                logger.logINGwMsg(false,ERROR_FLAG,0,"Unexpected probCode<%d>",
                                  lComp.probCode.string[0]);

                addByte(buffer,length,JAIN_PRBLM_UNRECOGNIZED_ERROR);

            }
          } 
          break;

          case STU_ANSI_PRB_TRANS:     /* 0x05 transaction portion */
          {
            switch(lComp.probCode.string[0])
            {
              case STU_ANSI_PRB_UR_PKG:/* unrecognized package type */
                addByte(buffer,length,JAIN_PRBLM_CODE_UNREC_PACKAGE_TYPE);
              break;
            
              case STU_ANSI_PRB_IN_TRN:/* incorrect transaction portion */
                addByte(buffer,length,JAIN_PRBLM_CODE_INCORRECT_DLG);
              break;
            
              case STU_ANSI_PRB_BD_TRN:/* badly structured transaction portion*/
                addByte(buffer,length,JAIN_PRBLM_CODE_BADLY_STRUCTURED_DLG);
              break;
            
              case STU_ANSI_PRB_UR_TRN:/* unrecognized transaction ID */
                addByte(buffer,length,JAIN_PRBLM_CODE_UNASSIGNED_RESP_ID);
              break;
            
              case STU_ANSI_PRB_PR_TRN:/* permission to release */
                addByte(buffer,length,JAIN_PRBLM_CODE_PERMISSION_TO_REL);
              break;
            
              case STU_ANSI_PRB_RU_TRN:/* resource unavailable */
                addByte(buffer,length,JAIN_PRBLM_CODE_RES_UNAVAIL);
              break;
            
              default: 
                logger.logINGwMsg(false,ERROR_FLAG,0,"Unexpected probCode<%d>",
                                  lComp.probCode.string[0]);

                addByte(buffer,length,JAIN_PRBLM_CODE_UNREC_PACKAGE_TYPE);
            }
          }
          break;

          case STU_ANSI_PRB_RSRVD:     /* 0xFF all families - reserved */ 
            logger.logINGwMsg(false,ALWAYS_FLAG,0,"Problem Tag "
                                                "\"all families - reserved\"");
          break;
          
#else //ITU
          case INC_PROB_GENERAL :
          {
            addByte(buffer,length,PROBLEM_TYPE);
            addByte(buffer,length,JAIN_PROBLEM_TYPE_GENERAL);
            switch (lComp.probCode.string[0])
            {
             /*General Problems */
              case INC_UNREC_COMP:
              {
                addByte(buffer,length,PROBLEM_CODE);
                addByte(buffer,length,JAIN_PRBLM_CODE_UNRECOGNISED_COMP);
              }
              break;

              case INC_MISTYPED_COMP :
              {
                addByte(buffer,length,PROBLEM_CODE);
                addByte(buffer,length,JAIN_PRBLM_CODE_MISTYPED_COM);
              }
              break;

              case INC_BAD_STRUC_COMP :
              {
                addByte(buffer,length,PROBLEM_CODE);
                addByte(buffer,length,JAIN_PRBLM_CODE_BADLY_STRUCTURED_COMP);
              }
              break;
              default:
                logger.logINGwMsg(false,ERROR_FLAG,0,
                  "+VER+ Invalid Problem code Problem Type General <%d>"
                  " **QUITTING ENCODE**", lComp.probCode.string[0]);
                return NULL;
            }//end of switch problem code
          }// case INC_PROB_GENERAL end
          break;

          case INC_PROB_INVOKE :
          {
            addByte(buffer,length,PROBLEM_TYPE);
            addByte(buffer,length,JAIN_PROBLEM_TYPE_INVOKE);
                    
            switch (lComp.probCode.string[0])
            {
              /* Invoke Problem */
              case INC_DUP_INVOKE :
              {
                addByte(buffer,length,PROBLEM_CODE);
                addByte(buffer,length,JAIN_PRBLM_CODE_DUP_INVOKE_ID);
              }
              break;

              case INC_UNREC_OPR :
              {
                addByte(buffer,length,PROBLEM_CODE);
                addByte(buffer,length,JAIN_PRBLM_CODE_UNREC_OPR);
              }
              break;

              case INC_MISTYPED_PARAM :
              {
                addByte(buffer,length,PROBLEM_CODE);
                addByte(buffer,length,JAIN_PRBLM_CODE_MISTYPED_PARAM);
              }
              break;

              case INC_RESOURCE_LIMIT :
              {
                addByte(buffer,length,PROBLEM_CODE);
                addByte(buffer,length,JAIN_PRBLM_CODE_RESOURCE_LIMIT);
              }
              break;

              case INC_INIT_RELEASE :
              {
                addByte(buffer,length,PROBLEM_CODE);
                addByte(buffer,length,JAIN_PRBLM_CODE_INIT_RELEASE);
              }
              break;

              case INC_UNREC_LINKED_ID :
              {
                addByte(buffer,length,PROBLEM_CODE);
                addByte(buffer,length,JAIN_PRBLM_CODE_UNREC_LINKED_ID);
              }
              break;

              case INC_LINKED_RESP_UNX :
              {
                addByte(buffer,length,PROBLEM_CODE);
                addByte(buffer,length,JAIN_PRBLM_CODE_LINKED_RESP_UNX);
              }
              break;

              case INC_UNX_LINKED_OP :
              {
                addByte(buffer,length,PROBLEM_CODE);
                addByte(buffer,length,JAIN_PRBLM_CODE_UNX_LINKED_OP);
              }
              break;
              default:
                logger.logINGwMsg(false,ERROR_FLAG,0,
                  "+VER+ Invalid Prblm Code,PrblmType Invoke <%d>"
                  "**QUITTING ENCODE**", lComp.probCode.string[0]);
                return NULL;
            }
          }//end of case INC_PROB_INVOKE
          break;

          case INC_PROB_RET_RES :
          {
            addByte(buffer,length,PROBLEM_TYPE);
            addByte(buffer,length,JAIN_PROBLEM_TYPE_RETURN_RESULT);
            switch (lComp.probCode.string[0])
            {
              /*Return Result Problem */
              case INC_RR_UNREC_INVKID :
              {
                addByte(buffer,length,PROBLEM_CODE);
                addByte(buffer,length,JAIN_PRBLM_CODE_UNREC_INVOKE_ID);
              }
              break;

              case INC_UNX_RETRSLT :
              {
                addByte(buffer,length,PROBLEM_CODE);
                addByte(buffer,length,JAIN_PRBLM_CODE_RETURN_RESULT_UNX);
              }
              break;

              case INC_RR_MISTYPED_PAR :
              {
                addByte(buffer,length,PROBLEM_CODE);
                addByte(buffer,length,JAIN_PRBLM_CODE_MISTYPED_PARAM);
              }
              break;
              default:
                logger.logINGwMsg(false,ERROR_FLAG,0,
                  "+VER+ Invalid Problem code Problem Type Return Result <%d>"
                  "**QUITTING ENCODE**",
                  lComp.probCode.string[0]);
                return NULL;
            }
          }//end of  case INC_PROB_RET_RES
          break;

          case INC_PROB_RET_ERR :
          {
            addByte(buffer,length,PROBLEM_TYPE);
            addByte(buffer,length,JAIN_PROBLEM_TYPE_RETURN_ERROR);
                    
            switch (lComp.probCode.string[0])
            {
              /*Return Error Problem */
              case INC_RE_UNREC_INVKID:
              {
                addByte(buffer,length,PROBLEM_CODE);
                addByte(buffer,length,JAIN_PRBLM_UNRECOGNIZED_ERROR);
              }
              break;

              case INC_RE_UNX_RETERR ://done
              {
                addByte(buffer,length,PROBLEM_CODE);
                addByte(buffer,length,JAIN_PRBLM_CODE_RETURN_ERROR_UNX); 
              }
              break;

              case INC_UNREC_ERROR ://done
              {
                addByte(buffer,length,PROBLEM_CODE);
                addByte(buffer,length,JAIN_PRBLM_UNRECOGNIZED_ERROR);       
              }
              break;

              case INC_UNX_ERR :
              {
                addByte(buffer,length,PROBLEM_CODE);
                addByte(buffer,length,JAIN_PRBLM_UNEXPECTED_ERROR);
              }
              break;

              case INC_RE_MISTYPED_PAR                       :
              {
                addByte(buffer,length,PROBLEM_CODE);
                addByte(buffer,length,JAIN_PRBLM_MISTYPED_PARAM);
              }
              break;
              default:
                logger.logINGwMsg(false,ERROR_FLAG,0,
                  "+VER+ Invalid Problem code Problem Type Return Error <%d>"
                  "**QUITTING ENCODE**",
                  lComp.probCode.string[0]);
                return NULL;
            }
          }//end of INC_PROB_RET_ERR
          break;
#endif /*SS7_ANS88 ||SS7_ANS92 || SS7_ANS96*/
          
          default:
          logger.logINGwMsg(false,VERBOSE_FLAG,0,
            "+VER+ default handling <%d>", lComp.probTag);
          {
            addByte(buffer,length,PROBLEM_TYPE);
            addByte(buffer,length,JAIN_PROBLEM_TYPE_RETURN_ERROR);
            addByte(buffer,length,PROBLEM_CODE);
            addByte(buffer,length,JAIN_PRBLM_UNRECOGNIZED_ERROR);       
            
          }
        }/*end of switch probTag*/

        encodeInvokeId(buffer, length,lComp);

        if(lComp.status == INC_COMP_REJ_USR){
          addByte(buffer,length,INC_REJECT_SOURCE);
          addByte(buffer,length,JAIN_REJECT_TYPE_USER);                                 
        }
        else if(lComp.status == INC_COMP_REJ_LOCAL){
          addByte(buffer,length,INC_REJECT_SOURCE);
          addByte(buffer,length,JAIN_REJECT_TYPE_LOCAL);
        }
        else if(lComp.status == INC_COMP_REJ_REMOTE){
          addByte(buffer,length,INC_REJECT_SOURCE);
          addByte(buffer,length,JAIN_REJECT_TYPE_REMOTE);
        }
        else {
          logger.logINGwMsg(false,ERROR_FLAG,0,"Reject type <%02x>",lComp.status);

          addByte(buffer,length,INC_REJECT_SOURCE);
          addByte(buffer,length,JAIN_REJECT_TYPE_LOCAL);
        }
      }  
      break;
      case INC_UCANCEL :
      {
        logger.logINGwMsg(false,ERROR_FLAG,0,
          "+VER+ User cancelled some event");
        assert(0);
      }
      
      default:
     
      logger.logINGwMsg(false,ERROR_FLAG,0,"Unknown Component Type<%d>",
                        lComp.compType);

      return NULL;  
    }//end switch lComp.compType
          
      encodeContentLength(buffer,length,index,length-index-1);
  }//end for(compVector)

  *p_bufLen = length;                           

  logger.logINGwMsg(false, TRACE_FLAG,0, "Out TcapMessage::encode");
  return  buffer;
}


bool TcapMessage::decodeNStateReqEvent(U8* p_recBuf,int &p_bufLen,
                                       SccpAddr &p_affectedUser,U8 &p_us)
{

  logger.logINGwMsg(false,VERBOSE_FLAG,0,"In decodeNStateReqEvent");
  char lBuf[1024];
  int lBufLen = 0;         
  if(!(p_recBuf && p_bufLen)){
    if(true || 0 < mLogger->getLoggingLevel()) {
      lBufLen += sprintf(lBuf + lBufLen,"\n---Cannot Decode State Req Event---\n");
      mLogger->dumpCodecMsg(lBuf,DEC); 
    } 
    logger.logINGwMsg(false,ERROR_FLAG,0,"+VER Cannot decode buffer:<%u> length:<%d> ",
                      p_recBuf, p_bufLen);
    logger.logINGwMsg(false,ERROR_FLAG,0,"Out decodeNStateReqEvent");
    return false;
  }
 
  if(true || 0 < mLogger->getLoggingLevel()) {
    lBufLen += sprintf(lBuf + lBufLen,"\n---State Req Event Buffer---\n");
  } 
  int currIndex = 0;
  if(p_recBuf[currIndex] != INC_SCCP_MGMT_MSG) {
    logger.logINGwMsg(false,ERROR_FLAG,0,
      "+VER+ sccp-state-req message is not found");
    if(true || 0 < mLogger->getLoggingLevel()) {
      lBufLen += sprintf(lBuf + lBufLen,"\n---[INC_SCCP_MGMT_MSG] NOT FOUND---\n");
      mLogger->dumpCodecMsg(lBuf,DEC); 
    } 
    logger.logINGwMsg(false,VERBOSE_FLAG,0,"Out decodeNStateReqEvent");
    return false;
  }
  int msglen = 0;
  currIndex++;
  decodeLength(p_recBuf,currIndex,msglen);
  U8 msgType = p_recBuf[currIndex];
  if (msgType != INC_NSTATE_REQ){
    logger.logINGwMsg(false,ERROR_FLAG,0,
      "Unknown Request recieved[%d]", msgType);
    if(true || 0 < mLogger->getLoggingLevel()) {
      lBufLen += sprintf(lBuf + lBufLen,"\n---[INC_NSTATE_REQ] NOT FOUND---\n");
      mLogger->dumpCodecMsg(lBuf,DEC); 
    } 

    logger.logINGwMsg(false,VERBOSE_FLAG,0,"Out decodeNStateReqEvent");
    return false; 
  }
  logger.logINGwMsg(false,VERBOSE_FLAG,0,"decodeNStateReqEvent currIndex[%d] msglen[%d]",currIndex,msglen);
  
  int endIndex = currIndex + msglen;
  currIndex++; 
  while(currIndex < endIndex){

    logger.logINGwMsg(false,VERBOSE_FLAG,0,"decodeNStateReqEvent Tag [%02X]",
                      p_recBuf[currIndex]);
    switch(p_recBuf[currIndex])
    {
      case INC_USER_STATUS:
      {
        currIndex++;
        p_us = decodeValue(p_recBuf, currIndex, INC_USER_STATUS_LEN);
        if(p_us == SAS_UOS) {
          p_us = SS_UOS;
          if(0 < mLogger->getLoggingLevel()) {
            lBufLen += sprintf(lBuf + lBufLen,"\nUser Status UOS\n");
          } 

        }
        else if (p_us == SAS_UIS) {
          p_us = SS_UIS;
          
          if(0 < mLogger->getLoggingLevel()) {
            lBufLen += sprintf(lBuf + lBufLen,"\nUser Status UIS\n");
          } 
        }
        else{
          if(0 < mLogger->getLoggingLevel()) {
            lBufLen += sprintf(lBuf + lBufLen,"\nUser Status Invalid\n");
            mLogger->dumpCodecMsg(lBuf,DEC); 
          } 
          logger.logINGwMsg(false,ERROR_FLAG,0,"sccp-state-req user status not valid");
          logger.logINGwMsg(false,VERBOSE_FLAG,0,"Out decodeNStateReqEvent");
          return false;
        }
       
        logger.logINGwMsg(false,VERBOSE_FLAG,0,"decodeNStateReqEvent INC_USER_STATUS"
                      " decoded [0x%02X]",p_us);
 
        currIndex += INC_USER_STATUS_LEN;
      }
      break;
      
      case INC_AFFECTED_USER:
      {
        int addrlen; 
        if (G_FAILURE == decodeSccpAddr(p_recBuf, currIndex,
                                 addrlen, (&p_affectedUser))) 
        { 
          logger.logINGwMsg(false,ERROR_FLAG,0,"+VER+ Error in decodeSccpAddr **QUITTING DECODE**"); 
          lBufLen += sprintf(lBuf + lBufLen,"\nCannot Decode SccpAddr\n");
          mLogger->dumpCodecMsg(lBuf,DEC); 
          return false;
        }

        if(mLogger->getLoggingLevel() >= 2) {
          lBufLen += dumpSpAddr(p_affectedUser,"SccpAddr decoded from State Req Event",lBuf, lBufLen); 
        } 
      }
      break;

      default:
      if(0 < mLogger->getLoggingLevel()) {
        lBufLen += sprintf(lBuf + lBufLen,"\nUnknown Tag\n");
        mLogger->dumpCodecMsg(lBuf,DEC); 
      } 
      logger.logINGwMsg(false,ERROR_FLAG,0,
                       "Unknown tag recieved while decoding sccpNstateReq");
      logger.logINGwMsg(false,VERBOSE_FLAG,0,"Out decodeNstateReqEvent");
      return false;
    }//end of switch
   } //end of while

  if(0 < mLogger->getLoggingLevel()) {
    mLogger->dumpCodecMsg(lBuf,DEC); 
  } 

  logger.logINGwMsg(false,VERBOSE_FLAG,0,"Out decodeNstateReqEvent");
  return true;
}

bool TcapMessage::decodeTcapCmp(U8* p_cmpBuf,
                                unsigned int &p_NextTagIndex,
                                TcapComp* p_compTx)
{
  logger.logINGwMsg(false,VERBOSE_FLAG,0,"In decodeTcapCmp");
  char lBuf[1024];
  int lBufLen = 0;
  int lLogLevel = mLogger->getLoggingLevel();
  if(0 < lLogLevel){
    lBufLen += sprintf(lBuf + lBufLen,"%s",
    "\n*decTcCmp*\n");
  }
  int lcurrIndex = 1;
  int lcmpLen =0;                                  
  decodeLength(p_cmpBuf, lcurrIndex, lcmpLen);
  p_compTx->compType = decodeValue(p_cmpBuf,lcurrIndex,COMPONENT_TYPE_LEN);

  bool isInvoke = false;
  bool isRetRes = false;

  if(p_compTx->compType == JAIN_COMP_INVOKE)
  {
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
    isInvoke = true;
#else
    p_compTx->compType = INC_INVOKE;
    if(0 < lLogLevel){
      lBufLen += sprintf(lBuf + lBufLen,"%s",
        "\nCompType:Inv\n"); 
    }
#endif


    
  }

  else if(p_compTx->compType == JAIN_COMP_RESULT)
  {
    //check for L,NL in INC_LAST tag and update
    isRetRes = true;

  }

  else if(p_compTx->compType == JAIN_COMP_REJECT){
    p_compTx->compType = INC_REJECT;
    if(0 < lLogLevel){
      lBufLen += sprintf(lBuf + lBufLen,"%s",
        "\nCompType:Rej\n"); 
    }
  }

  else if(p_compTx->compType == JAIN_COMP_ERROR){
    p_compTx->compType = INC_RET_ERR; 
    if(0 < lLogLevel){
      lBufLen += sprintf(lBuf + lBufLen,"%s",
        "\nCompType:RetErr\n"); 
    }
  }

  else if(p_compTx->compType == JAIN_COMP_USER_CANCEL){
    p_compTx->compType = INC_REJECT;
    if(0 < lLogLevel){
      lBufLen += sprintf(lBuf + lBufLen,"%s",
        "\nCompType:UserCancel-Reject\n"); 
      }
    p_compTx->cancelFlg = true; 
  }

  else if(p_compTx->compType == JAIN_COMP_TIMER_RESET){
    p_compTx->compType = INC_TMR_RESET; 
    if(0 < lLogLevel){
      lBufLen += sprintf(lBuf + lBufLen,"%s",
        "\nnCompType:TmrReset\n");
    } 
  }
  else if(p_compTx->compType == JAIN_COMP_LOCAL_CANCEL){
    p_compTx->compType = INC_REJECT;
    if(0 < lLogLevel){
      lBufLen += sprintf(lBuf + lBufLen,"%s",
        "\nCompType:LocalCancel-Reject\n"); 
    }
    logger.logINGwMsg(false,ERROR_FLAG,0,
                     "[decodeTcapCmp]JAIN_COMP_LOCAL_CANCEL Received, not possible");
  }

  else
  {
    
    if(0 < lLogLevel){
      lBufLen += sprintf(lBuf + lBufLen,"%s",
        "\nCompType:Unkn\n");
      mLogger->dumpCodecMsg(lBuf,DEC); 
    } 
    logger.logINGwMsg(false,ERROR_FLAG,0,
      "[decodeTcapCmp] %s Unknown CompType <%d>",lBuf ,p_compTx->compType);
    return false;
  }

  lcurrIndex += COMPONENT_TYPE_LEN;         
  p_NextTagIndex = lcurrIndex + lcmpLen-1;
  while(lcurrIndex < p_NextTagIndex)
  {
    
    switch(p_cmpBuf[lcurrIndex])
    {
      case INC_LAST:
      {
        lcurrIndex++;

        if(isRetRes)
        {
          p_compTx->compType = ((0x01 == p_cmpBuf[lcurrIndex])?STU_RET_RES_L:
                                                              STU_RET_RES_NL);

          if(0 < lLogLevel)
          {
            
            lBufLen += sprintf(lBuf + lBufLen,"\nCompType:%s\n",
                     (p_compTx->compType == STU_RET_RES_L?"RetResL":"RetResNL")
                     ); 
          }
        }

#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
        else if(isInvoke)
        {
          p_compTx->compType = ((0x01 == p_cmpBuf[lcurrIndex])?STU_INVOKE_L:
                                                              STU_INVOKE_NL);
          if(0 < lLogLevel)
          {
            
            lBufLen += sprintf(lBuf + lBufLen,"\nCompType:%s\n",
                     (p_compTx->compType == STU_INVOKE_L?"InvokeL":"InvokeNL")
                     ); 
          }
        }
#endif

        if(0 < lLogLevel){
          lBufLen += sprintf(lBuf + lBufLen,"%s",
            "\nLastComp\n"); 
        }
        logger.logINGwMsg(false,VERBOSE_FLAG,0,
                                        "INC_LAST [%02x]",p_cmpBuf[lcurrIndex]);
        lcurrIndex++;                             
      }    
      break;

      case OPERATION_TYPE:
      {
        lcurrIndex++;
        //no need to map
        p_compTx->opTag = decodeValue(p_cmpBuf,lcurrIndex, OPERATION_TYPE_LEN);                             
        lcurrIndex += OPERATION_TYPE_LEN;
        if(0 < lLogLevel){
          lBufLen += sprintf(lBuf + lBufLen, "\nOpType:%02X\n",p_compTx->opTag);
        } 
      }    
      
      break;

      case OPERATION_CODE:
      {
        lcurrIndex++;
        int lopCodeLen = 0;
        decodeLength(p_cmpBuf,lcurrIndex,lopCodeLen);
        p_compTx->opCode.len = lopCodeLen;
        memcpy((void*)(p_compTx->opCode.string),(void*)(p_cmpBuf+lcurrIndex),lopCodeLen);
        lcurrIndex += lopCodeLen;
        if(0 < lLogLevel){
          lBufLen += sprintf(lBuf + lBufLen,
            "\nOpCode %02X",p_compTx->opCode.string[0]);
        }
      }                 
      break;

      case PARAM_IDENTIFIER:
      {
        lcurrIndex++;
        p_compTx->paramTag = decodeValue(p_cmpBuf,lcurrIndex,PARAM_IDENTIFIER_LEN);
        if(p_compTx->paramTag == JAIN_PARAM_TYPE_SINGLE){
          p_compTx->paramTag =  INC_NO_SET_SEQ;
        }
        else if(p_compTx->paramTag == JAIN_PARAM_TYPE_SEQUENCE){
          p_compTx->paramTag = INC_SEQUENCE;
        }
        else if(p_compTx->paramTag == JAIN_PARAM_TYPE_SET){
          p_compTx->paramTag = INC_SET;
        }
        else{
          logger.logINGwMsg(false,ERROR_FLAG,0,"%s[decodeTcapCmp] "
            "+VER+**QUITTING DECODE**",lBuf);

          if(TCAP_LOGGING_L2 < lLogLevel) {
            mLogger->dumpCodecMsg(lBuf,DEC); 
          } 

          return false;
        }
        lcurrIndex += PARAM_IDENTIFIER_LEN;
        if(0 < lLogLevel){
          lBufLen += sprintf(lBuf + lBufLen,
            "\nParamId:%02X",p_compTx->paramTag);
        }
      }
      break;

      case PARAM:
      {
        lcurrIndex++;
        int lparamLen = 0;
        decodeLength(p_cmpBuf,lcurrIndex,lparamLen);//l_counter pointing to value now
        p_compTx->param.len = lparamLen;
        p_compTx->param.string = new U8[lparamLen]; //delete in destructor
        memcpy((void*)(p_compTx->param.string),(void*)(p_cmpBuf+lcurrIndex),lparamLen);
        lcurrIndex += lparamLen;       
        if(1 < lLogLevel){
          lBufLen += sprintf(lBuf + lBufLen,
                                          "\nParamLen:%d",p_compTx->param.len);

          for(int i=0;i<p_compTx->param.len;i++) {
            if(0 == i%16){
              lBufLen += sprintf(lBuf + lBufLen,"%s","\n\t");
            }
            lBufLen += sprintf(lBuf + lBufLen," %02X",
              p_compTx->param.string[i]);          
          }                                 
        }
      }
      break;

      case CLASS_TYPE:
      {
        lcurrIndex++;
        //no need to map
        p_compTx->opClass = decodeValue(p_cmpBuf,lcurrIndex,CLASS_TYPE_LEN);
        lcurrIndex += CLASS_TYPE_LEN;
       
        if(1 < lLogLevel){
          lBufLen += sprintf(lBuf + lBufLen,
              "\nClassType:%02X",p_compTx->opClass);
        }
      }
      break;                                    

      case INVOKE_ID:
      {
        lcurrIndex++;
        U8 lOctet = 0xFF;
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)  
        p_compTx->invIdAnsi.pres = INC_TRUE;                
        p_compTx->invIdAnsi.octet = decodeValue(p_cmpBuf,lcurrIndex,INVOKE_ID_LEN);
        lOctet = p_compTx->invIdAnsi.octet;
#else
        p_compTx->invIdItu.pres = INC_TRUE;                
        p_compTx->invIdItu.octet = decodeValue(p_cmpBuf,lcurrIndex,INVOKE_ID_LEN);
        lOctet = p_compTx->invIdItu.octet;
#endif
        lcurrIndex += INVOKE_ID_LEN;     
        if(1 < lLogLevel){
          lBufLen += sprintf(lBuf + lBufLen,
            "\nInvkId:%02X",lOctet);
        }
      }
      break;

      case LINKED_ID:
      {
        lcurrIndex++;
        p_compTx->linkedId.pres = INC_TRUE;
        p_compTx->linkedId.octet = decodeValue(p_cmpBuf,lcurrIndex,INVOKE_ID_LEN);
        lcurrIndex += LINKED_ID_LEN; 
        if(1 < lLogLevel){
          lBufLen += sprintf(lBuf + lBufLen,
            "\nlnkId:%02X",p_compTx->linkedId.octet);
        }
      }
       break;

      case PROBLEM_TYPE:
      {
        lcurrIndex++;
        p_compTx->probTag = decodeValue(p_cmpBuf,lcurrIndex,PROBLEM_TYPE_LEN);
  
#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)
        if(p_compTx->probTag == JAIN_PROBLEM_TYPE_GENERAL){
          p_compTx->probTag = STU_ANSI_PRB_GEN;
        }
        else if(p_compTx->probTag == JAIN_PROBLEM_TYPE_INVOKE){
          p_compTx->probTag = STU_ANSI_PRB_INV;
        }
        else if(p_compTx->probTag == JAIN_PROBLEM_TYPE_RETURN_RESULT){
          p_compTx->probTag = STU_ANSI_PRB_RR;
        }
        else if(p_compTx->probTag == INC_PROB_RET_ERR){
          p_compTx->probTag = STU_ANSI_PRB_RE;
        }
        else if(p_compTx->probTag == JAIN_PROBLEM_TYPE_TRANSACTION){
          p_compTx->probTag =  STU_ANSI_PRB_TRANS;
        }
        else{
          logger.logINGwMsg(false,ERROR_FLAG,0,"Unexpected Problem Type <%02x>",
                            p_compTx->probTag);
        }
#else /*handle ITU specific codes*/
        if(p_compTx->probTag == JAIN_PROBLEM_TYPE_GENERAL){
          p_compTx->probTag = INC_PROB_GENERAL;
        }
        else if(p_compTx->probTag == JAIN_PROBLEM_TYPE_INVOKE){
          p_compTx->probTag = INC_PROB_INVOKE;
        }
        else if(p_compTx->probTag == JAIN_PROBLEM_TYPE_RETURN_RESULT){
          p_compTx->probTag = INC_PROB_RET_RES;
        }
        else if(p_compTx->probTag == INC_PROB_RET_ERR){
          p_compTx->probTag = INC_PROB_RET_ERR;
        }
        else if(p_compTx->probTag == JAIN_PROBLEM_TYPE_TRANSACTION){
          p_compTx->probTag =  INC_PROB_NONE;
          logger.logINGwMsg(false,VERBOSE_FLAG,0,
          "[decodeTcapCmp]Cannot map problem tag JAIN_PROBLEM_TYPE_TRANSACTION"
          " to CCPU specified problem types setting INC_PROB_NONE");
        }
        else{
          logger.logINGwMsg(false,ERROR_FLAG,0,"Unexpected Problem Type <%02x>",
                            p_compTx->probTag);
        }
#endif
        lcurrIndex += PROBLEM_TYPE_LEN;
        if(1 < lLogLevel){
          lBufLen += sprintf(lBuf + lBufLen,
            "\nPrblmType:%02X",p_compTx->probTag);
        }
      }
      break;

      case PROBLEM_CODE:
      {
        lcurrIndex++;
        p_compTx->probCode.string[0] = decodeValue(p_cmpBuf,lcurrIndex,PROBLEM_CODE_LEN);
        if(1 < lLogLevel){
          lBufLen += sprintf(lBuf + lBufLen,
                            "ErrCode:%02x", p_compTx->probCode.string[0]);      
        }             

				p_compTx->probCode.len = PROBLEM_CODE_LEN;
        switch(p_compTx->probCode.string[0])
        {

#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
          case JAIN_PRBLM_CODE_UNRECOGNISED_COMP:
            p_compTx->probCode.string[0] = STU_ANSI_PRB_UR_CMP;
          break;

          case JAIN_PRBLM_CODE_MISTYPED_COM:
            p_compTx->probCode.string[0] = STU_ANSI_PRB_IN_CMP;
          break;

          case JAIN_PRBLM_CODE_BADLY_STRUCTURED_COMP:
            p_compTx->probCode.string[0] = STU_ANSI_PRB_BD_CMP;
          break;

          case JAIN_PRBLM_CODE_INCORRECT_COMP_CODING:
            p_compTx->probCode.string[0] = STU_ANSI_PRB_IN_ENC;
          break;

          case JAIN_PRBLM_CODE_DUP_INVOKE_ID:
            p_compTx->probCode.string[0] = STU_ANSI_PRB_DUP_ID;
          break;

          case JAIN_PRBLM_CODE_UNREC_OPR:
            p_compTx->probCode.string[0] = STU_ANSI_PRB_UR_OP;
          break;

          case JAIN_PRBLM_CODE_MISTYPED_PARAM:
          {
            if(INC_PROB_INVOKE == p_compTx->probTag)
            {
              p_compTx->probCode.string[0] = STU_ANSI_PRB_IN_PRM;
            }
            else if(INC_PROB_RET_RES == p_compTx->probTag)
            {
              p_compTx->probCode.string[0] = STU_ANSI_PRB_INV_IN_PRM;
            }
            else if(INC_PROB_RET_ERR == p_compTx->probTag)
            {
              p_compTx->probCode.string[0] = STU_ANSI_PRB_EN_PRM;
            }
            else {
              logger.logINGwMsg(false,ERROR_FLAG,0,
              "ProbTag %02x - probcode %02x MM ",
              p_compTx->probTag, p_compTx->probCode.string[0]);

              p_compTx->probCode.string[0] = STU_ANSI_PRB_IN_PRM;
            }
          }
          break;

          case JAIN_PRBLM_CODE_RESOURCE_LIMIT:
            p_compTx->probCode.string[0] = STU_ANSI_PRB_RU_TRN;
          break;

          case JAIN_PRBLM_CODE_INIT_RELEASE:
            p_compTx->probCode.string[0] = STU_ANSI_PRB_PR_TRN;
          break;

          case JAIN_PRBLM_CODE_UNREC_LINKED_ID:
          {
            if(INC_PROB_INVOKE == p_compTx->probTag)
            {
              p_compTx->probCode.string[0] = STU_ANSI_PRB_IUR_ID;
            }
            else if(INC_PROB_RET_RES == p_compTx->probTag)
            {
              p_compTx->probCode.string[0] = STU_ANSI_PRB_IUR_ID;
            }
            if(INC_PROB_RET_ERR == p_compTx->probTag)
            {
              p_compTx->probCode.string[0] = STU_ANSI_PRB_IUR_ID;
            }
            else
            {
              logger.logINGwMsg(false,ERROR_FLAG,0,
              "ProbTag %02x - probcode %02x MM ",
              p_compTx->probTag, p_compTx->probCode.string[0]);

              p_compTx->probCode.string[0] = STU_ANSI_PRB_IUR_ID;
            }
          }
          break;

          case JAIN_PRBLM_CODE_LINKED_RESP_UNX:
          {
            //cannot be mapped to ANSI error codes
            if(INC_PROB_GENERAL == p_compTx->probTag)
            {
              p_compTx->probCode.string[0] = STU_ANSI_PRB_DUP_ID;
            }
            else if(INC_PROB_INVOKE == p_compTx->probTag)
            {
              p_compTx->probCode.string[0] = STU_ANSI_PRB_IUR_ID;
            }
            else if(INC_PROB_RET_RES == p_compTx->probTag)
            {
              p_compTx->probCode.string[0] = STU_ANSI_PRB_RUR_ID;
            }
            if(INC_PROB_RET_ERR == p_compTx->probTag)
            {
              p_compTx->probCode.string[0] = STU_ANSI_PRB_EUR_ID;
            }
            else
            {
              logger.logINGwMsg(false,ERROR_FLAG,0,
              "ProbTag %02x - probcode %02x MM ",
              p_compTx->probTag, p_compTx->probCode.string[0]);

              p_compTx->probCode.string[0] = STU_ANSI_PRB_IUR_ID;
            }
          }
          break;

          case JAIN_PRBLM_CODE_UNX_LINKED_OP:
            p_compTx->probCode.string[0] = STU_ANSI_PRB_IUR_ID;
          break;

          /* Return result problem */
          case JAIN_PRBLM_CODE_UNREC_INVOKE_ID:
            p_compTx->probCode.string[0] = STU_ANSI_PRB_RUR_ID;
          break;

          case JAIN_PRBLM_CODE_RETURN_RESULT_UNX:
            p_compTx->probCode.string[0] = STU_ANSI_PRB_UX_RES;
          break;

          /* Return Error problems */
          case JAIN_PRBLM_CODE_RETURN_ERROR_UNX:
            p_compTx->probCode.string[0] = STU_ANSI_PRB_UX_RER;
          break;

          case JAIN_PRBLM_UNRECOGNIZED_ERROR:
            p_compTx->probCode.string[0] = STU_ANSI_PRB_UR_ERR;
          break;

          case JAIN_PRBLM_UNEXPECTED_ERROR:
            p_compTx->probCode.string[0] = STU_ANSI_PRB_UX_ERR;
          break;

          case JAIN_PRBLM_CODE_UNREC_PACKAGE_TYPE:
            p_compTx->probCode.string[0] = STU_ANSI_PRB_UR_PKG;
          break;

          case JAIN_PRBLM_CODE_INCORRECT_DLG:		
            p_compTx->probCode.string[0] = STU_ANSI_PRB_IN_TRN;
          break;

          case JAIN_PRBLM_CODE_BADLY_STRUCTURED_DLG:
            p_compTx->probCode.string[0] = STU_ANSI_PRB_BD_TRN;
          break;

          case JAIN_PRBLM_CODE_UNASSIGNED_RESP_ID:
            p_compTx->probCode.string[0] = STU_ANSI_PRB_UR_TRN;
          break;

          case JAIN_PRBLM_CODE_PERMISSION_TO_REL:
            p_compTx->probCode.string[0] = STU_ANSI_PRB_PR_TRN;
          break;

          case JAIN_PRBLM_CODE_RES_UNAVAIL:
            p_compTx->probCode.string[0] = STU_ANSI_PRB_RU_TRN;
          break;

#else  /*ITU*/

          case JAIN_PRBLM_CODE_UNRECOGNISED_COMP:
            p_compTx->probCode.string[0] = INC_UNREC_COMP;
          break;

          case JAIN_PRBLM_CODE_MISTYPED_COM:
            p_compTx->probCode.string[0] = INC_MISTYPED_COMP;
          break;

          case JAIN_PRBLM_CODE_BADLY_STRUCTURED_COMP:
            p_compTx->probCode.string[0] = INC_BAD_STRUC_COMP;
          break;
#ifdef ST_TPLUS_REQ
          case JAIN_PRBLM_CODE_INCORRECT_COMP_CODING:
            p_compTx->probCode.string[0] = INC_EXCEED_COMP_LEN;
          break;
#endif

          case JAIN_PRBLM_CODE_DUP_INVOKE_ID:
            p_compTx->probCode.string[0] = INC_DUP_INVOKE;
          break;

          case JAIN_PRBLM_CODE_UNREC_OPR:
            p_compTx->probCode.string[0] = INC_UNREC_OPR;
          break;

          case JAIN_PRBLM_CODE_MISTYPED_PARAM:
            p_compTx->probCode.string[0] = INC_MISTYPED_PARAM;
          break;

          case JAIN_PRBLM_CODE_RESOURCE_LIMIT:
            p_compTx->probCode.string[0] = INC_RESOURCE_LIMIT;
          break;

          case JAIN_PRBLM_CODE_INIT_RELEASE:
            p_compTx->probCode.string[0] = INC_INIT_RELEASE;
          break;

          case JAIN_PRBLM_CODE_UNREC_LINKED_ID:
            p_compTx->probCode.string[0] = INC_UNREC_LINKED_ID;
          break;

          case JAIN_PRBLM_CODE_LINKED_RESP_UNX:
            p_compTx->probCode.string[0] = INC_LINKED_RESP_UNX;
          break;

          case JAIN_PRBLM_CODE_UNX_LINKED_OP:
            p_compTx->probCode.string[0] = INC_UNX_LINKED_OP;
          break;

          /* Return result problem */
          case JAIN_PRBLM_CODE_UNREC_INVOKE_ID:
            p_compTx->probCode.string[0] = INC_RR_UNREC_INVKID;
          break;

          case JAIN_PRBLM_CODE_RETURN_RESULT_UNX:
            p_compTx->probCode.string[0] = INC_UNX_RETRSLT;
          break;

          /* Return Error problems */
          case JAIN_PRBLM_CODE_RETURN_ERROR_UNX:
            p_compTx->probCode.string[0] = INC_RE_UNX_RETERR;
          break;

          case JAIN_PRBLM_UNRECOGNIZED_ERROR:
            p_compTx->probCode.string[0] = INC_UNREC_ERROR;
          break;

          case JAIN_PRBLM_UNEXPECTED_ERROR:
            p_compTx->probCode.string[0] = INC_UNX_ERR;
          break;
#endif
          default:
          
          logger.logINGwMsg(false,ERROR_FLAG,0,"%s[decodeTcapCmp] "
            "+VER+**QUITTING DECODE** index<%d>",lBuf,lcurrIndex);

          if(TCAP_LOGGING_L2 < lLogLevel) {
            mLogger->dumpCodecMsg(lBuf,DEC); 
          } 


          return false;
        }
          lcurrIndex += PROBLEM_CODE_LEN; 
          if(1 < lLogLevel){
            lBufLen += sprintf(lBuf + lBufLen,
              "\nPrblmType:%02X",p_compTx->probCode.string[0]);
          }
      }
      break;

      case INC_REJECT_SOURCE: 
      {
      lcurrIndex++;
      p_compTx->status = decodeValue(p_cmpBuf,lcurrIndex,INC_REJECT_SOURCE_LEN);
        switch(p_compTx->status)
        {
        case JAIN_REJECT_TYPE_USER:
        p_compTx->status = INC_COMP_REJ_USR;
        break;

        case JAIN_REJECT_TYPE_LOCAL:
        p_compTx->status = INC_COMP_REJ_LOCAL;
        break;

        case JAIN_REJECT_TYPE_REMOTE:
        p_compTx->status = JAIN_REJECT_TYPE_REMOTE;
        break;
        }
        lcurrIndex += INC_REJECT_SOURCE_LEN;
        if(1 < lLogLevel) {
          lBufLen += sprintf(lBuf + lBufLen,
                                        "\nRejectSource:%02X",p_compTx->status);
        }
      }
      break;

      case ERROR_TYPE:
      {
        lcurrIndex++;
        p_compTx->errTag = decodeValue(p_cmpBuf,lcurrIndex,ERROR_TYPE_LEN);
        switch(p_compTx->errTag)
        {
          case JAIN_ERROR_LOCAL:
          p_compTx->errTag = INC_LOCAL;
          break;
          case JAIN_ERROR_GLOBAL:
          p_compTx->errTag = INC_GLOBAL;                      
          break;
          default:
          p_compTx->errTag = INC_NONE;
        }
        lcurrIndex += ERROR_TYPE_LEN;
        if(1 < lLogLevel){
          lBufLen += sprintf(lBuf + lBufLen, "\nErrType:%02X",p_compTx->errTag);
        }
      }
      break;

      case ERROR_CODE:
      { //p_compTx->errCode
        lcurrIndex++;
        int lerrCodeLen; 
        decodeLength(p_cmpBuf,lcurrIndex,lerrCodeLen);
        p_compTx->errCode.len = lerrCodeLen;
        memcpy((void*)(p_compTx->errCode.string),(void*)(p_cmpBuf+lcurrIndex),lerrCodeLen);
        lcurrIndex += lerrCodeLen; 
        if(1 < lLogLevel){
          lBufLen += sprintf(lBuf + lBufLen,
          "\nErrCode:%02X",p_compTx->errCode.string[0]);
        }
      }
      break;

       default:

        if(1 < lLogLevel){
          lBufLen += sprintf(lBuf + lBufLen,
                  "\nUnknown tag [%02X] indx<%d>", p_cmpBuf[lcurrIndex],
                  lcurrIndex);

          mLogger->dumpCodecMsg(lBuf,DEC); 
        }
        logger.logINGwMsg(false,ERROR_FLAG,0,"%s[decodeTcapCmp] "
            "+VER+**QUITTING DECODE**",lBuf);
       return false;
    }//end of switch
  }  
  if(1 < lLogLevel){
      mLogger->dumpCodecMsg(lBuf,DEC); 
  }
  logger.logINGwMsg(false,VERBOSE_FLAG,0,"Out decodeTcapCmp ");
  return true;
}

bool TcapMessage::decodeTcapDlg(U8* p_dlgBuf, unsigned int &p_nextIndex, TcapDlg & p_dlg)
{
  bool retVal = true;
  logger.logINGwMsg(false,TRACE_FLAG,0,"In decodeTcapDlg");
  char lBuf[1024];
  int lBufLen = 0;
  int lLogLevel = mLogger->getLoggingLevel();
  
  if(TCAP_LOGGING_L2 < lLogLevel) {
    lBufLen += sprintf(lBuf + lBufLen,
      "\n*decTcDlg*\n"); 
  } 

  int lcurrIndex = 1;
  int ldlgLen;     
  p_dlg.pres = true;                             
  decodeLength(p_dlgBuf, lcurrIndex, ldlgLen);       
  p_dlg.dlgType = p_dlgBuf[lcurrIndex];
  if(TCAP_LOGGING_L2 < lLogLevel) {
    lBufLen += sprintf(lBuf + lBufLen,
      "\ndlglen:%d\ndlgType:%02X",ldlgLen, p_dlg.dlgType);  
  } 

  lcurrIndex += DLGTYPE_LEN; 
  if(p_dlg.dlgType == JAIN_BEGIN)
  {
    p_dlg.dlgType = INC_BEGIN;
    if(TCAP_LOGGING_L2 < lLogLevel) {
      lBufLen += sprintf(lBuf + lBufLen,
        ":Begin");
    }  
  }

  else if(p_dlg.dlgType == JAIN_CONTINUE)
  {
    p_dlg.dlgType = INC_CONTINUE; 
    if(TCAP_LOGGING_L2 < lLogLevel) {
      lBufLen += sprintf(lBuf + lBufLen,
        ":Cont");
    } 
  }
                 
  else if(p_dlg.dlgType == JAIN_END)
  {
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
    p_dlg.dlgType = STU_RESPONSE;
#else
    p_dlg.dlgType = INC_END;
#endif
    p_dlg.endFlag = false;
    if(TCAP_LOGGING_L2 < lLogLevel) {
      lBufLen += sprintf(lBuf + lBufLen,
        ":End");
    } 
  }

  else if(p_dlg.dlgType == JAIN_PREARR_END){
    p_dlg.dlgType = INC_END;
    if(TCAP_LOGGING_L2 < lLogLevel) {
      lBufLen += sprintf(lBuf + lBufLen,
        ":PreArrEnd"); 
    }
    p_dlg.endFlag = true;
  }

  else if(p_dlg.dlgType == JAIN_USER_ABORT){

#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
    p_dlg.dlgType = STU_ANSI_UABORT;
#else
    p_dlg.dlgType = INC_U_ABORT;
#endif

    if(TCAP_LOGGING_L2 < lLogLevel) {
      lBufLen += sprintf(lBuf + lBufLen,
        ":Uabrt");
    }
  }

  else if(p_dlg.dlgType == JAIN_UNIDIRECTIONAL){
    p_dlg.dlgType = INC_UNI;
    if(TCAP_LOGGING_L2 < lLogLevel) {
      lBufLen += sprintf(lBuf + lBufLen,
        ":Uni");
    } 
  }

  else if(p_dlg.dlgType == JAIN_PROVIDER_ABORT){
    //should never hit this 
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
    p_dlg.dlgType = STU_ANSI_PABORT;
#else
    p_dlg.dlgType = INC_P_ABORT;
#endif
    if(TCAP_LOGGING_L2 < lLogLevel) {
      lBufLen += sprintf(lBuf + lBufLen,
        ":PAbrt");
    } 
    logger.logINGwMsg(false,VERBOSE_FLAG,0,
    "Received JAIN_PROVIDER_ABORT");
  }

  else{
    if(TCAP_LOGGING_L2 < lLogLevel) {
      lBufLen += sprintf(lBuf + lBufLen,
        ":Unkn");
      mLogger->dumpCodecMsg(lBuf,DEC); 
    } 

    logger.logINGwMsg(false,ERROR_FLAG,0,
      "Unknown dialogue req value <%d>",p_dlg.dlgType);
    
    return false; 
  }

  p_nextIndex = ldlgLen + lcurrIndex-1;
  while(lcurrIndex < p_nextIndex)
  {
    switch(p_dlgBuf[lcurrIndex])
    {
      case DLGID: 
      {
        lcurrIndex++;
        p_dlg.sudlgId = decodeValue(p_dlgBuf,lcurrIndex,DLGID_LEN);
        lcurrIndex += DLGID_LEN; //pointing to tag nxt to DLGID       
        if(TCAP_LOGGING_L2 < lLogLevel) {
          lBufLen += sprintf(lBuf + lBufLen,
            "\nDlgId:%d",(p_dlg).sudlgId);
        }
      }
      break;   

#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
      case TAG_PERMISSION:
      {
        lcurrIndex++;
        U8 lcPemission = decodeValue(p_dlgBuf, lcurrIndex, TAG_PERMISSION_LEN);
        lcurrIndex += TAG_PERMISSION_LEN;

        switch(p_dlg.dlgType)
        {
          case INC_BEGIN:
            p_dlg.dlgType = (0x01 == lcPemission? STU_QRY_PRM:STU_QRY_NO_PRM);
          break;
 
          case INC_CONTINUE:
            p_dlg.dlgType = (0x01 == lcPemission? STU_CNV_PRM:STU_CNV_NO_PRM);
          break;
 

          default:
          logger.logINGwMsg(false,ERROR_FLAG,0,"decodeTcapDlg Err, dlgtype "
                            "not set before decoding transaction permission");
 
        }              
        if(TCAP_LOGGING_L2 < lLogLevel) {
          lBufLen += sprintf(lBuf + lBufLen,
            "\nPermission: %02x",lcPemission);
        }
      }
      break;
#endif

      case ORIG_SUA:
      {
        int lsrcAddrLen = 0;
        (p_dlg).srcAddr = new SccpAddr;
        memset(p_dlg.srcAddr,0,sizeof(SccpAddr));
        if(G_FAILURE == decodeSccpAddr(p_dlgBuf,lcurrIndex, 
                                      lsrcAddrLen, (p_dlg).srcAddr))
        { 
          logger.logINGwMsg(false,ERROR_FLAG,0,"%s "
            "\n+VER+Error in decodeSccpAddr **QUITTING DECODE**",lBuf); 

          if(TCAP_LOGGING_L2 < lLogLevel) {
            mLogger->dumpCodecMsg(lBuf,DEC); 
          } 

          return false;
        }

        p_dlg.srcAddr->rtgInd          = INC_RTE_GT;
        p_dlg.srcAddr->pcInd           = false;
        p_dlg.srcAddr->ssnInd          = true;
        p_dlg.srcAddr->gt.format       = 2; 
        p_dlg.srcAddr->gt.gt.f2.tType  = 0xE9; 
        p_dlg.srcAddr->gt.addr.length  = 2;
        p_dlg.srcAddr->gt.addr.strg[0] = 0x21;
        p_dlg.srcAddr->gt.addr.strg[1] = 0x43;
      }
      break;

      case DEST_SUA:
      {
        int ldstAddrLen = 0;
        (p_dlg).dstAddr = new SccpAddr;
        memset(p_dlg.dstAddr,0,sizeof(SccpAddr));
        if(G_FAILURE == decodeSccpAddr(p_dlgBuf, lcurrIndex,
                               ldstAddrLen, (p_dlg).dstAddr))
        { 
          logger.logINGwMsg(false,ERROR_FLAG,0,
            "%s+VER+ Error in decodeSccpAddr **QUITTING DECODE**",lBuf); 

          if(TCAP_LOGGING_L2 < lLogLevel) {
            mLogger->dumpCodecMsg(lBuf,DEC); 
          } 
          return false;
        }

        p_dlg.dstAddr->rtgInd          = INC_RTE_GT;
        p_dlg.dstAddr->pcInd           = false;
        p_dlg.dstAddr->ssnInd          = true;
        p_dlg.dstAddr->gt.format       = 2; 
        p_dlg.dstAddr->gt.gt.f2.tType  = 0xE8; 
        p_dlg.dstAddr->gt.addr.length  = 2;
        p_dlg.dstAddr->gt.addr.strg[0] = 0x21;
        p_dlg.dstAddr->gt.addr.strg[1] = 0x43;

      }
      break;
            
      case QOS:
      { 
        //Skipping the QOS
        lcurrIndex += 2;
        memset(&(p_dlg.qosSet), 0, sizeof(TcapQosSet));
      }
      break;

      case REPORT_CAUSE:
      {
        lcurrIndex++;
        (p_dlg).cause = decodeValue(p_dlgBuf,lcurrIndex,REPORT_CAUSE_LEN);
        lcurrIndex += REPORT_CAUSE_LEN;
        if(TCAP_LOGGING_L2 < lLogLevel) {
          lBufLen += sprintf(lBuf + lBufLen,
            "\nReportCause:%02X",(p_dlg).cause);
        }
      }
      break;

      case ABORT_CAUSE:
      {
        lcurrIndex++;   
        p_dlg.resultPres = true;
        (p_dlg).reason = decodeValue(p_dlgBuf,lcurrIndex,ABORT_CAUSE_LEN);
        lcurrIndex += ABORT_CAUSE_LEN;
        if(TCAP_LOGGING_L2 < lLogLevel) {
          lBufLen += sprintf(lBuf + lBufLen,
            "\nAbrtCause:%02X",(p_dlg).reason);
        }
      }
      break;

      case ABORT_INFO: 
      {
        lcurrIndex++;
        int uInfoLen = 0;
        p_dlg.resultPres = false;
        decodeLength(p_dlgBuf,lcurrIndex,uInfoLen);//l_counter pointing to value now
        (p_dlg).uInfo.len = uInfoLen;
        (p_dlg).uInfo.string = new U8[uInfoLen];
        memcpy((void*)p_dlg.uInfo.string,(void*)(p_dlgBuf+lcurrIndex),uInfoLen);
        lcurrIndex += uInfoLen;
        if(TCAP_LOGGING_L2 < lLogLevel) {
          lBufLen += sprintf(lBuf + lBufLen,
            "\nAbrtInfoLen:%d\n",(p_dlg).uInfo.len);
          for(int i = 0; i< (p_dlg).uInfo.len ; i++ ) {
            if(0 == i%16){
              lBufLen += sprintf(lBuf + lBufLen,"%s","\n\t");
            }
            lBufLen += sprintf(lBuf + lBufLen,
              " %02X", (p_dlg).uInfo.string[i]);
          }
        }
      }            
      break;
            
      case APP_CONTEXT_NAME_TYPE: 
      {
        lcurrIndex++;                                                      
        (p_dlg).acnType = decodeValue(p_dlgBuf,lcurrIndex,APP_CONTEXT_NAME_TYPE_LEN);
        lcurrIndex+=APP_CONTEXT_NAME_TYPE_LEN;            
        if(TCAP_LOGGING_L2 < lLogLevel) {
          lBufLen += sprintf(lBuf + lBufLen,
           "\nAcnType:%02X",(p_dlg).acnType);
        }
      }
      break;
            
      case APP_CONTEXT_NAME:
      {
        lcurrIndex++;        
        (p_dlg).pres = INC_TRUE;                             
        //it is assumed that APP_CONTEXT_NAME_TYPE will be encoded before 
        if((p_dlg).acnType == JAIN_ACN_TYPE_OID){
        int lLen = 0;
        decodeLength(p_dlgBuf,lcurrIndex,lLen);
        p_dlg.objAcn.len = lLen;
        memcpy((void*)p_dlg.objAcn.string,(void*)(p_dlgBuf+lcurrIndex),
               (p_dlg).objAcn.len);
        }
        lcurrIndex+=(p_dlg).objAcn.len;     

        if(TCAP_LOGGING_L2 < lLogLevel) {
          for(int i=0;i<(p_dlg).objAcn.len;i++){       
            if(0 == i%16){
              lBufLen += sprintf(lBuf + lBufLen,"%s","\n\t");
            }
            lBufLen += sprintf(lBuf + lBufLen,
              " %02X",p_dlg.objAcn.string[i]);
          }
        }
      }
      break;
            
      case DLG_U_BUF: 
      {
        lcurrIndex++;
        int uInfoLen = 0;
        decodeLength(p_dlgBuf,lcurrIndex,uInfoLen);
        (p_dlg).uInfo.len = uInfoLen;
        (p_dlg).uInfo.string = new U8[uInfoLen];
        memcpy((void*)p_dlg.uInfo.string,
               (void*)(p_dlgBuf+lcurrIndex),uInfoLen);
        lcurrIndex += uInfoLen;

        if(TCAP_LOGGING_L2 < lLogLevel) {
          lBufLen += sprintf(lBuf + lBufLen,
            "DlgUBufLen:<%d>",(p_dlg).uInfo.len);
          for(int i=0; i<(p_dlg).uInfo.len;i++)
          {
            if(0 == i%16){
              lBufLen += sprintf(lBuf + lBufLen,"%s","\n\t");
            }
            lBufLen += sprintf(lBuf + lBufLen,
              " %02X",p_dlg.uInfo.string[i]);
          } 
        } 
      } 
                   
      break;

      case TAG_BILLINGID:
      {
        lcurrIndex++;
        p_dlg.miBillingNo = decodeValue(p_dlgBuf,lcurrIndex,TAG_BILLINGID_LEN);
        lcurrIndex += TAG_BILLINGID_LEN; //pointing to tag nxt to TAG_BILLINGID 
        if(TCAP_LOGGING_L2 < lLogLevel) {
          lBufLen += sprintf(lBuf + lBufLen,
            "\nBillingNo : %d",p_dlg.miBillingNo);
        }
      }
      break;

      default:
        logger.logINGwMsg(false,ERROR_FLAG,0,
              "%s Unknown tag found : [%d]",lBuf,p_dlgBuf[lcurrIndex]);
        retVal = false;
    }//switch 
  }//while 

 logger.logINGwMsg(false,TRACE_FLAG,0,"Out decodeTcapDlg");
 return retVal;
}


bool TcapMessage::decode(U8* recBuf ,unsigned int BufLen, 
            TcapDlg &p_Dlg, vector<TcapComp*>& p_complist)
{      
  logger.logINGwMsg(false,VERBOSE_FLAG,0,"In TcapMessage::decode");
  if(!BufLen)
  {
    logger.logINGwMsg(false,ERROR_FLAG,0,"[decode]ERROR: zero length recieved");
    return false;
  }
  
  if(!recBuf)
  {
    logger.logINGwMsg(false,ERROR_FLAG,0,"[decode]ERROR: NULL buffer recieved");
    return false;
  }
  //currIndex will track length of the buffer parsed
  unsigned int currIndex = 0;
  unsigned int addrlen = 0; 
  // reference for dlgReq  
  TcapDlg dlg;
  bool compTxFlg = false, 
       dlgTxFlg  = false;
  
  TcapComp* compTx = NULL;
  
  while(currIndex < BufLen)
  {
    switch(recBuf[currIndex])//tag
    { 
                                
      case DLGTYPE:
      {    
        logger.logINGwMsg(false,VERBOSE_FLAG,0,"[decode]DLGTYPE decoded");
        unsigned int lNextTagOffset = 0;
        memset(&dlg,0,sizeof(TcapDlg)); 
        dlgTxFlg = true;                          
        if(!decodeTcapDlg((recBuf+currIndex), lNextTagOffset, p_Dlg)){
          logger.logINGwMsg(false,ERROR_FLAG,0,"[decode]Error in decoding of dialogue");
          return false;
        }
        currIndex += lNextTagOffset;
      }//case for DLGTYPE
      break;
             
      case COMPONENT_TYPE:
      {    
        logger.logINGwMsg(false,VERBOSE_FLAG,0,"[decode]COMPONENT_TYPE decoded");
        unsigned int lNextTagOffset = 0;
        compTxFlg = true;
        compTx = new TcapComp;
        memset((void*)compTx,0,sizeof(TcapComp));
        if(decodeTcapCmp((recBuf+currIndex),lNextTagOffset,compTx))
        {
          this->compPresR = true;
          p_complist.push_back(compTx);      
          currIndex += lNextTagOffset;           
        }                 
        else
        {
          logger.logINGwMsg(false,ERROR_FLAG,0,"[decode]Error in decodeTcapCmp");
          delete compTx;
          compTx = NULL;
          for(int i=0;i <p_complist.size();i++){
            delete p_complist[i];
            p_complist[i] = NULL;
          }
          return false;
        }                          
      }
            
      break;

      default:
        logger.logINGwMsg(false,ERROR_FLAG,0,
               "[decode]Unknown tag decoded [0x%02x] at index [%d]",
               recBuf[currIndex], currIndex);
        return false;   
    }//end of switch recBuf[i]
                    
  }//end of outermost while

  logger.logINGwMsg(false,VERBOSE_FLAG,0,
    "[decode]CompList len [%d]"
    " Out Decode ,parsed BufLen [%d]",p_complist.size(),currIndex);
  return true;       
}//decode ends


void TcapMessage::releaseTcapMsg(){
  logger.logINGwMsg(false,VERBOSE_FLAG,0,"In releaseTcapMsg()");
  int size = mCompVector.size();
  for(int i=0;i<size;i++){
    TcapComp* lcomp =  mCompVector[i];
    if(lcomp->param.len && lcomp->param.string){
      delete [] lcomp->param.string;
      lcomp->param.len = 0;
      lcomp->param.string = 0;
      delete lcomp;
      lcomp = 0;
    }
  }
  if(NULL != dlgR.srcAddr){
   delete(dlgR.srcAddr);
   dlgR.srcAddr = 0;
  }
   
  if(NULL != dlgR.dstAddr){
   delete(dlgR.dstAddr);
   dlgR.dstAddr = 0;
  }
  
  if(dlgR.uInfo.len && dlgR.uInfo.string)
  {
    delete [] dlgR.uInfo.string; dlgR.uInfo.string = NULL;
  } 
  
  if(0!= dlgR.dlgPortnUInfo.len && NULL != dlgR.dlgPortnUInfo.string)
  {
    delete [] dlgR.dlgPortnUInfo.string; dlgR.dlgPortnUInfo.string = NULL;
  }

  logger.logINGwMsg(false,VERBOSE_FLAG,0,"Out releaseTcapMsg");
}

int
TcapMessage::InitLogger() {
  int retVal = G_SUCCESS;
  logger.logINGwMsg(false,TRACE_FLAG,0,"In InitLogger");
  mLogger = &(INGwTcapMsgLogger::getInstance()); 
  mCodecLogLevel = mLogger->getLoggingLevel();
  logger.logINGwMsg(false,TRACE_FLAG,0,"Out InitLogger LogLevel<%d>",mCodecLogLevel);
  return retVal;
}

bool 
TcapMessage::isRetransmission(U32 pDlgId, int pSeqNum){

#ifdef INGW_LOOPBACK_SAS
  logger.logINGwMsg(false, TRACE_FLAG,0,"isRetransmission(): Skipping check");
  return false;
#endif

  INGwTcapIncMsgHandler *lpIncMsgHandler = 
         &(INGwTcapIncMsgHandler::getInstance());

  int lLastSeqNum = lpIncMsgHandler->getLastSequenceNumForDlg(pDlgId); 
  if(lLastSeqNum >= pSeqNum){
    //retransmission
    return true;
  } 
  else{
    lpIncMsgHandler->updateLastSeqNumForDlg(pDlgId,pSeqNum);
    return false;
  }
}

void TcapMessage::removeDialogueFt(int userDlgId, int stackDialogue, 
                                   AppInstId & appid, const string & ipaddr, 
                                   bool getLock)
{
  logger.logINGwMsg(false, TRACE_FLAG, 0,
                    "In removeDialogueFt uDlgId <%d> stkDlgId <%d> getLock<%d>",
                     userDlgId, stackDialogue, getLock);
  if(getLock) {
    pthread_mutex_lock(&mutex); 
  }
 
  //INGwTcapIncMsgHandler::getInstance().resetSeqNumForDlg(stackDialogue);
 
  (userDialogues[ipaddr]).erase(userDlgId);
  (stackDialogues[appid.suId]).erase(stackDialogue);
  
  gsDialogueStateMap->resetBitState(stackDialogue);

  //logger.logINGwMsg(false, TRACE_FLAG, 0, "+rem+ sizeof userDialogues <%d> "
  //   "sizeof stackDialogues <%d>",userDialogues[ipaddr].size(), 
  //   (stackDialogues[appid.suId]).size());

  if(getLock) {
    pthread_mutex_unlock(&mutex); 
  }

  logger.logINGwMsg(false, TRACE_FLAG, 0,
                    "Out removeDialogueFt");
}

void 
TcapMessage::clearUserInformationFT(int userDlgId, int stackDialogue, AppInstId & appid,
                            const string & ipaddr, bool getLock)
{
  
  logger.logINGwMsg(false, TRACE_FLAG, 0,"In clearUserInformationFT userDlgId"
                    " <%d> stackDialogue <%d> suId <%d> spId <%d> ipaddr <%s>"
                    " getLock<%d> ", userDlgId, stackDialogue, appid.suId,
                    appid.spId, ipaddr.c_str(), getLock);

  removeDialogueFt(userDlgId, stackDialogue, appid, ipaddr, getLock); 

  logger.logINGwMsg(false, TRACE_FLAG, 0,"Out clearUserInformationFT");
}

void 
TcapMessage::dumpObject(char* str){

 logger.logINGwMsg(false,TRACE_FLAG,0,
 "\n+rem+ __DLG__ "
 "\n<%s>"
 "\nmsgTypeR ------ <%d>"
 "\ndlgType  ------ <%d>"
 "\nEventType ----- <%s>"
 "\nCompVector size <%d>",str,msgTypeR,this->dlgR.dlgType, 
 this->msg_type == EVTSTUCMPIND?"EVTSTUCMPIND":
 this->msg_type == EVTSTUDATIND?"EVTSTUDATIND":"UNKNOWN",
 mCompVector.size()); 
}

U8
TcapMessage::getDlgType(U8* p_msgBody,char* pLogBuf,short &pLogBufLen, 
                        int pDlgId){
  int lLenOffset = 0;
  U8 retVal =  255;
  if(0x00 != (p_msgBody[1] & 0x80)){
    lLenOffset = (p_msgBody[1]) - 128;
  }
  retVal = p_msgBody[lLenOffset+2];
  
  pLogBufLen += sprintf(pLogBuf + pLogBufLen,
                     "\t%d : %s \n",pDlgId,
                     JAIN_CONTINUE       ==  retVal?"TC-CONT":
                     JAIN_END            ==  retVal?"TC-END":
                     JAIN_USER_ABORT     ==  retVal?"TC-UABORT":
                     JAIN_UNIDIRECTIONAL ==  retVal?"TC-UNI":
                     JAIN_BEGIN          ==  retVal?"TC-BEGIN":"UNKNOWN");

  return p_msgBody[lLenOffset+2];
}

void
TcapMessage::extractDlgInfo(U8 *p_msgBody, int &pDlgId, U8 &pDlgType){
  int lLenOffset = 0;
  if(0x00 != (p_msgBody[1] & 0x80)){
    lLenOffset = (p_msgBody[1]) - 128;
  }
  pDlgId = (pDlgId | p_msgBody[4 + lLenOffset]) << 8;
  pDlgId = (pDlgId | p_msgBody[5 + lLenOffset]) << 8;
  pDlgId = (pDlgId | p_msgBody[6 + lLenOffset]) << 8;
  pDlgId =  pDlgId | p_msgBody[7 + lLenOffset];
  pDlgType = p_msgBody[lLenOffset+2];
}

bool
TcapMessage:: getObjAcn(U8* pBuf, 
                        int pBufLen, 
#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96) 
                        StAnsiDlgEv &pAnsiDlgEv,
#else
                        INcStr &pObjAcn,
#endif
                        U8 pTag) 
{
  bool retVal = true;
  logger.logINGwMsg(false,VERBOSE_FLAG,0,"In getTagValue");
  
  int lcurrIndex = 1;
  int ldlgLen;    
  TcapMessage ltcMsg; 
  ltcMsg.decodeLength(pBuf, lcurrIndex, ldlgLen);       
  U8 lDlgType = pBuf[lcurrIndex];
   
  int  decTagCntr = 0;
  lcurrIndex += DLGTYPE_LEN;
  if(DLGTYPE == pTag) {
    lDlgType =(lDlgType == JAIN_BEGIN)? INC_BEGIN:
               (lDlgType == JAIN_CONTINUE)?INC_CONTINUE:
               (lDlgType == JAIN_END)?INC_END:
               (lDlgType == JAIN_PREARR_END)?INC_END:
               (lDlgType == JAIN_USER_ABORT)?INC_U_ABORT:
               (lDlgType == JAIN_UNIDIRECTIONAL)?INC_UNI:
               (lDlgType == JAIN_PROVIDER_ABORT)?INC_P_ABORT:255;
    
    if(lDlgType == 255){
      return false;
    }
    return true;
  }

  bool lbDlgPortnEnd = false;
  int  lidlgPortnFields = 0;
  while(lcurrIndex < pBufLen)
  {
      if(lbDlgPortnEnd || (6 == lidlgPortnFields)) {
        retVal = true;
        break;
      }

      logger.logINGwMsg(false,TRACE_FLAG,0,"In While loop %d",lcurrIndex); //remft
      switch(pBuf[lcurrIndex])
      {
        case COMPONENT_TYPE:
          lbDlgPortnEnd = true;
 
        break;

#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)
        case DLG_PORTN_USR_INFO:
        {
          ++lidlgPortnFields;
          lcurrIndex++;
          int liUBufLen = 0; 
          ltcMsg.decodeLength(pBuf,lcurrIndex, liUBufLen);
          U8 * lpcUbuf = new U8[liUBufLen];
 
          memcpy(lpcUbuf, pBuf + lcurrIndex, liUBufLen);

          pAnsiDlgEv.usrInfo.pres = 0x01;

         Buffer *lpBufferUinfo = NULL;
#ifdef SS_HISTOGRAM_SUPPORT
         if (SGetMsg (BP_AIN_SM_REGION, DFLT_POOL, &lpBufferUinfo,
                                    __FILE__, __LINE__) != ROK)
#else
         if (SGetMsg (BP_AIN_SM_REGION, DFLT_POOL, &lpBufferUinfo) != ROK)
#endif
         {
            logger.logMsg(ERROR_FLAG, 0, "Failed to allocate stk:Buffer Type");
            return -1;
         }

         SAddPstMsgMult((Data *)lpcUbuf, liUBufLen, lpBufferUinfo); 
 
         // when to clear this Buffer ? who will clear this buffer??
         pAnsiDlgEv.usrInfo.val = lpBufferUinfo;

         lcurrIndex += liUBufLen;
        }
        break;
  
        case SEC_TYPE:
        {
          ++lidlgPortnFields;
          lcurrIndex++;
          pAnsiDlgEv.secType.pres = 0x01; 
          pAnsiDlgEv.secType.val
                          = ltcMsg.decodeValue(pBuf ,lcurrIndex, SEC_TYPE_LEN);                             
          lcurrIndex += SEC_TYPE_LEN;
          if(pAnsiDlgEv.secType.val != INC_ENC_TYP_OID || 
             pAnsiDlgEv.secType.val != INC_ENC_TYP_INT)
          {
            return false;
          }
        }
        break;
  
        case SEC_VALUE:
        {
          ++lidlgPortnFields;
          lcurrIndex++;
          switch(pAnsiDlgEv.secType.val)
          {
            case INC_ENC_TYP_INT:
            {
              pAnsiDlgEv.secCntxt.intSec.pres = 0x01;

              memcpy(&pAnsiDlgEv.secCntxt.intSec.val, pBuf + lcurrIndex,
                      SIZE_OF_INT);

              lcurrIndex += SIZE_OF_INT;
            }
            break;

            case INC_ENC_TYP_OID:
            {
              pAnsiDlgEv.secCntxt.oidSec.pres = 0x01;

              int liSecValLen = 0;
              ltcMsg.decodeLength(pBuf,lcurrIndex, liSecValLen);
              
              memcpy(&pAnsiDlgEv.secCntxt.oidSec.val, 
                     pBuf + lcurrIndex,
                     liSecValLen );

              pAnsiDlgEv.secCntxt.oidSec.len =  (liSecValLen>>1);

              lcurrIndex += liSecValLen;
            }
            break;

            default:
            
            logger.logINGwMsg(false,ERROR_FLAG,0,"[getObjAcn] secType Unknown",
                              "bufLen <%d> secType<%02x>",
                              lcurrIndex,pAnsiDlgEv.secType.val);
            return false;
          }
        }
        break;
  
        case DLG_CONF_INFO:
        {
          ++lidlgPortnFields;
          lcurrIndex++;

          pAnsiDlgEv.confInfo.pres = 0x01;
          
          int liConfInfoLen = 0;

          ltcMsg.decodeLength(pBuf,lcurrIndex, liConfInfoLen);

          memcpy(&pAnsiDlgEv.confInfo.val, pBuf + lcurrIndex, liConfInfoLen);
  
          lcurrIndex+= liConfInfoLen;
        }
        break;
#endif
 
        case DLGID: 
        {
          lcurrIndex += (DLGID_LEN+1); //pointing to tag nxt to DLGID       
        }
        break;   

        case ORIG_SUA:
        {
          int lsrcAddrLen = 0;
          lcurrIndex++; 
          ltcMsg.decodeLength(pBuf,lcurrIndex,lsrcAddrLen);
          lcurrIndex += lsrcAddrLen; 
        }
        
        break;

        case DEST_SUA:
        {
          int ldstAddrLen = 0;
          lcurrIndex++; 
          ltcMsg.decodeLength(pBuf,lcurrIndex,ldstAddrLen);
          lcurrIndex += ldstAddrLen; 
        }
        break;
              
        case QOS:
        { 
          lcurrIndex += 2;
        }
        break;

        case REPORT_CAUSE:
        {
          lcurrIndex += (REPORT_CAUSE_LEN+1);
        }
        break;

        case ABORT_CAUSE:
        {
          lcurrIndex += (ABORT_CAUSE_LEN+1);
        }
        break;

        case ABORT_INFO: 
        {
          int uInfoLen = 0;
          ltcMsg.decodeLength(pBuf,lcurrIndex,uInfoLen);
          lcurrIndex += uInfoLen;
        }            
        break;
          
        case TAG_PERMISSION:
        {
          lcurrIndex++;                                                      
          
          lcurrIndex +=  TAG_PERMISSION_LEN;
        }    
        case APP_CONTEXT_NAME_TYPE: 
        {
          ++lidlgPortnFields;
          lcurrIndex++;                                                      

#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)
          pAnsiDlgEv.acnType.pres = 0x01;

          pAnsiDlgEv.acnType.val  = 
                   ltcMsg.decodeValue(pBuf ,lcurrIndex, APP_CONTEXT_NAME_TYPE);                             
#else
/*No such specifier is required in case of ITU*/
#endif            

          lcurrIndex+=APP_CONTEXT_NAME_TYPE_LEN;            
        }
        break;
              
        case APP_CONTEXT_NAME:
        {
          ++lidlgPortnFields;
          lcurrIndex++;        

          int lLen = 0;
#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)
          switch(pAnsiDlgEv.acnType.val)
          {
            case INC_ENC_TYP_INT:
            {
              pAnsiDlgEv.acn.intAcn.pres = 0x01;
              memcpy(&pAnsiDlgEv.acn.intAcn.val, pBuf + lcurrIndex,
                      SIZE_OF_INT);
              lcurrIndex += SIZE_OF_INT;
            }
            break;

            case INC_ENC_TYP_OID:
            {
              pAnsiDlgEv.acn.oidAcn.pres = 0x01;

              int liAcnValLen = 0;
              ltcMsg.decodeLength(pBuf,lcurrIndex, liAcnValLen);
              
              memcpy(&pAnsiDlgEv.acn.oidAcn.val, 
                     pBuf + lcurrIndex,
                     liAcnValLen );

              pAnsiDlgEv.acn.oidAcn.len =  (liAcnValLen>>1);

              lcurrIndex += liAcnValLen;
            }
            break;

            default:
            
            logger.logINGwMsg(false,ERROR_FLAG,0,"[getObjAcn] acnType Unknown",
                              "bufLen <%d> acnType<%02x>",
                              lcurrIndex,pAnsiDlgEv.acnType.val);
            return -1;
          }
#else 
          ltcMsg.decodeLength(pBuf,lcurrIndex,lLen);
          pObjAcn.len = lLen;
          memcpy((void*)pObjAcn.string,(void*)(pBuf+lcurrIndex),
                 pObjAcn.len);

          logger.logINGwMsg(false,TRACE_FLAG,0,
                            "ACN extracted: len<%d>, offset<%d>",
                             pObjAcn.len,lcurrIndex);//remft

          lcurrIndex += lLen;     
#endif
        }
        break;
              
        case DLG_U_BUF: 
        {
          lcurrIndex++;
          int uInfoLen = 0;
          ltcMsg.decodeLength(pBuf,lcurrIndex,uInfoLen);
          lcurrIndex += uInfoLen;
        } 
                     
        break;

        default:
          logger.logINGwMsg(false,ERROR_FLAG,0,
                "Unknown tag found : [%d] Index[%d]",
                 pBuf[lcurrIndex], lcurrIndex);
          return false;
      }//switch 
  }//while
  return true; 
}

void TcapUserAddressInformation::logTuai(char * str){
  char lBuf[128]; 
  int lBufLen = 0; lBuf[0] = '\0';
  
  for(int i=0; i<objAcn.len; i++) {
    lBufLen += sprintf(lBuf + lBufLen, "%02X ", objAcn.string[i]);
  }
  str = ((NULL == str)?"---":str);
  logger.logINGwMsg(false, TRACE_FLAG, 0,
    "Tuai origin <%s>\n"
    "userDlgId   <%d>\n"
    "ipaddr      <%s>\n"
    "suId        <%d>\n"
    "spId        <%d>\n"
    "ACN Len     <%d>\n"
    "ACN         <%s>\n",str,userDlgId, ipaddr.c_str(), suId, spId, objAcn.len,lBuf);

    TcapMessage::dumpSpAddr(srcaddr,"logging Tuai srcaddr");
    TcapMessage::dumpSpAddr(destaddr,"logging Tuai destaddr");

}

//this will never be hit now
int TcapMessage::getUserAddressByDlgId(int pDlgId, 
                                       TcapUserAddressInformation& pTuai) {
LOCK();
  logger.logINGwMsg(false, TRACE_FLAG, 0,
                    "In getUserAddressByDlgId <%d>", pDlgId);

  map<int, std::map<int,TcapUserAddressInformation> >::iterator
                                           sapIterator = stackDialogues.begin();
  int retVal = 0;
  for(; sapIterator != stackDialogues.end(); sapIterator++) 
  {
    map<int,TcapUserAddressInformation>::iterator 
      lDlgIterator = (sapIterator->second).find(pDlgId);

      if(lDlgIterator != (sapIterator->second).end()) 
      {
        if(INC_UNI == lDlgIterator->second.mDlgType) {
          retVal = 1;
        }
        else 
        {
          retVal = 0;
          pTuai = lDlgIterator->second; 
        }
      }
      else
      {
        retVal = 2;
      }
  }

  logger.logINGwMsg(false, TRACE_FLAG, 0,
                    "Out getUserAddressByDlgId <%d>", retVal);
  return retVal;
}

int TcapMessage::getUserAddressByDlgId(vector<int> &pDlgList, 
                           vector<TcapUserAddressInformation> &pTuaiList) 
{
  LOCK();
  logger.logINGwMsg(false, TRACE_FLAG, 0,
                    "In getUserAddressByDlgId");

  map<int, std::map<int,TcapUserAddressInformation> >:: iterator
  sapIterator = stackDialogues.begin();
  int retVal = 0;
  for(; sapIterator != stackDialogues.end(); sapIterator++) 
  {
    map<int,TcapUserAddressInformation>::iterator  lDlgIterator;
    for(int j=0; j<pDlgList.size(); j++)
    {
      lDlgIterator = (sapIterator->second).find(pDlgList[j]);

      if(lDlgIterator != (sapIterator->second).end()) 
      {
          logger.logINGwMsg(false, TRACE_FLAG, 0, "Dialogue <%d> is <%d>",
                            pDlgList[j], (lDlgIterator->second).mDlgType);       
          pTuaiList.push_back(lDlgIterator->second);
      }
      else
      {
          logger.logINGwMsg(false, TRACE_FLAG, 0, "Dialogue <%d> Not Found"
                            " for sapId <%d>", pDlgList[j], sapIterator->first);        
	  //yogesh_invalid 
	  //pTuaiList.push_back(*invalid);
      }
    }
  }

  logger.logINGwMsg(false, TRACE_FLAG, 0,
                    "Out getUserAddressByDlgId <%d>", retVal);
  return retVal;
}

//not doing doReallocation check
void TcapUserAddressInformation::serialize(U8* pBuf, int &pOffset) 
{
  logger.logINGwMsg(false,ALWAYS_FLAG,0,
  "serialize tuai <%X> <%d>", pBuf,  pOffset);

  int value = htons(did);
  memcpy(pBuf + pOffset, (void*)&value, SIZE_OF_INT);
  pOffset += SIZE_OF_INT;

  logger.logINGwMsg(false,ALWAYS_FLAG,0,
  "serialize tuai <%X> <%d>", pBuf,  pOffset);

  value = htons(userDlgId);
  memcpy(pBuf + pOffset, (void*)&value, SIZE_OF_INT);
  pOffset += SIZE_OF_INT;

  logger.logINGwMsg(false,ALWAYS_FLAG,0,
  "serialize tuai <%X> <%d>", pBuf,  pOffset);

  int addrLen = sizeof(SccpAddr);

  memcpy(pBuf + pOffset, (void*)&srcaddr, addrLen);
  pOffset += addrLen;

  logger.logINGwMsg(false,ALWAYS_FLAG,0,
  "serialize tuai <%X> <%d>", pBuf,  pOffset);

  memcpy(pBuf + pOffset, (void*)&destaddr, addrLen);
  pOffset += addrLen;

  short sapId = suId;
  sapId = htons(suId);

  logger.logINGwMsg(false,ALWAYS_FLAG,0,
  "serialize tuai <%X> <%d>", pBuf,  pOffset);

  memcpy(pBuf + pOffset, &sapId, SIZE_OF_SHORT);
  pOffset += SIZE_OF_SHORT;

  logger.logINGwMsg(false,ALWAYS_FLAG,0,
  "serialize tuai <%X> <%d>", pBuf,  pOffset);

  sapId = spId;
  sapId = htons(spId);
  memcpy(pBuf + pOffset, &sapId, SIZE_OF_SHORT);
  pOffset += SIZE_OF_SHORT;

  logger.logINGwMsg(false,ALWAYS_FLAG,0,
  "serialize tuai <%X> <%d>", pBuf,  pOffset);

  value = htons(ipaddr.size());
  memcpy(pBuf + pOffset, (void*)&value, SIZE_OF_INT);
  pOffset += SIZE_OF_INT;

  logger.logINGwMsg(false,ALWAYS_FLAG,0,
  "serialize tuai <%X> <%d>", pBuf,  pOffset);

  memcpy(pBuf + pOffset, (void*)ipaddr.c_str(), ipaddr.size());
  pOffset += ipaddr.size();

  logger.logINGwMsg(false,ALWAYS_FLAG,0,
  "serialize tuai <%X> <%d>", pBuf,  pOffset);

#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)   
 //encode dlg ev and billing id

  memcpy(pBuf + pOffset, (void*)&ansiDlgEv, sizeof(StAnsiDlgEv));
  pOffset += sizeof(StAnsiDlgEv);

  int lBillingId = mBillingId.getBillingNo();
  lBillingId = ntohs(lBillingId);

  memcpy(pBuf + pOffset, (void*)&(lBillingId), SIZE_OF_INT); 
  pOffset += SIZE_OF_INT;

#else

  S16 lAcnLen = htons(objAcn.len);
  memcpy(pBuf + pOffset, (void*)&objAcn.len, SIZE_OF_SHORT);
  pOffset += SIZE_OF_SHORT;
#endif
  logger.logINGwMsg(false,ALWAYS_FLAG,0,
  "serialize tuai <%X> <%d>", pBuf,  pOffset);

  memcpy(pBuf + pOffset, (void*)(objAcn.string), objAcn.len);
  pOffset += objAcn.len;
}

void TcapUserAddressInformation::deSerialize(U8* pBuf, int &pOffset) 
{
  int lLen = pBuf[pOffset++];
  memcpy(&did, pBuf + pOffset, SIZE_OF_INT);
  
  did = ntohs(did);
  pOffset += SIZE_OF_INT;

  memcpy(&userDlgId, pBuf + pOffset, SIZE_OF_INT);

  userDlgId= ntohs(userDlgId);
  pOffset += SIZE_OF_INT;

  memcpy(&srcaddr, pBuf + pOffset, sizeof(SccpAddr));
  pOffset += sizeof(SccpAddr);

  memcpy(&destaddr,pBuf + pOffset, sizeof(SccpAddr));
  pOffset += sizeof(SccpAddr);

  memcpy(&suId, pBuf + pOffset, SIZE_OF_SHORT);
  pOffset += SIZE_OF_SHORT;

  memcpy(&spId, pBuf + pOffset, SIZE_OF_SHORT);
  pOffset += SIZE_OF_SHORT;

  int ipAddSize = 0;
  memcpy(&ipAddSize, pBuf + pOffset, SIZE_OF_INT);
  ipAddSize = ntohs(ipAddSize);
  fflush(stdout);
  pOffset += SIZE_OF_INT;

  char buf[60];
  memset(buf,0,sizeof(buf));
  memcpy(buf, (pBuf + pOffset), ipAddSize);

  int a = ipAddSize;
  ipaddr = buf;
  pOffset+= ipAddSize;

#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)
  //TcapMessage::getObjAcn(pBuf + pOffset, pOffset, ansiDlgEv, APP_CONTEXT_NAME); 
  memcpy((void*)&ansiDlgEv, pBuf + pOffset, sizeof(StAnsiDlgEv));
  pOffset += sizeof(StAnsiDlgEv);

  int liBillingNo;
  memcpy(&liBillingNo, pBuf + pOffset, SIZE_OF_INT);
  liBillingNo = ntohs(liBillingNo);

  mBillingId.setBillingNo(liBillingNo);
  pOffset += SIZE_OF_INT;

#else

  U16 lAcnLen = 0;
  memcpy(&lAcnLen, pBuf + pOffset, SIZE_OF_SHORT);
  objAcn.len = ntohs(lAcnLen);
  pOffset+= SIZE_OF_SHORT;

  fflush(stdout);

  memcpy(objAcn.string, pBuf + pOffset, lAcnLen);
  pOffset+= lAcnLen;

#endif

}

void
TcapMessage::getAllActiveDlgs() {
  logger.logINGwMsg(false,TRACE_FLAG,0,"In getAllActiveDlgs");
  LOCK();
  vector<int> lvDlgList;
  char lBuf[4096];
  int lBufLen = 0;

  if(NULL == gsDialogueStateMap) {
      logger.logINGwMsg(false,ERROR_FLAG,0,"getAllActiveDlgs"
        " gsDialogueStateMap NULL");
      return;
  }

  lvDlgList = gsDialogueStateMap->getAllSetBitIndex();
  int liVectorSize =  lvDlgList.size();

  lBufLen += sprintf(lBuf + lBufLen,"%s","\n--Bit Array Status--\n");
  lBufLen += sprintf(lBuf + lBufLen,"\n--No. of Active Dlgs <%d>--\n",
                     liVectorSize);


  for(int i=0; i< liVectorSize; i++) 
  {
    if(!(i & 3)) {
      lBufLen += sprintf(lBuf + lBufLen,"%s","\n\t");
    }

    lBufLen += sprintf(lBuf + lBufLen, "%d ", lvDlgList[i]);
    if(lBufLen > 3900) {
      mLogger->dumpString(lBuf);
      lBufLen = 0; lBuf[0] = '\0';
    }
  }

  mLogger->dumpString(lBuf);
  logger.logINGwMsg(false,TRACE_FLAG,0,"Out getAllActiveDlgs");
}

void
TcapMessage::auditDlg(vector<int> &avDldIdList) 
{
  int liSize = avDldIdList.size();
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"In auditDlg <%d>",liSize);
  SccpAddr     userAddr;
  AppInstId    appid;
  const string ipaddr;

  vector<TcapUserAddressInformation> lTuaiList;
  getUserAddressByDlgId(avDldIdList, lTuaiList);

  //remove from FT message map
  INGwTcapFtHandler::getInstance().handleAuditCfm(avDldIdList);

  for(int i = 0; i < liSize; i++){
          logger.logINGwMsg(false,ALWAYS_FLAG,0,"auditDlg <%d>", avDldIdList[i]);
  }
  //Yogesh_invalid iterating on lTuaiList vector size only no need to iterate over invalid dialogues
  for(int i = 0; i < lTuaiList.size() ; i++) 
  {
    if(lTuaiList[i].isValid()) 
    {
      logger.logINGwMsg(false,ALWAYS_FLAG,0,
                    "auditDlg tuai ipaddr <%s> suId <%d> spId <%d> dlgid <%d>",
                     lTuaiList[i].ipaddr.c_str(), lTuaiList[i].suId, lTuaiList[i].spId,
                     lTuaiList[i].did);

      appid.suId = lTuaiList[i].suId;
      appid.spId = lTuaiList[i].spId;
      
      TcapMessage::removeDialogue(avDldIdList[i], avDldIdList[i], 
                                userAddr,appid,
                                lTuaiList[i].ipaddr, false, true);  
    } 
    else
    {
      logger.logINGwMsg(false,ALWAYS_FLAG,0,"auditDlg tuai not valid<%d>",
                        avDldIdList[i]);
    
    }
  }

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"Out auditDlg");
}

void
TcapMessage:: createCmpEvent(TcapComp **pCompTx,
                             U8  acCompType, 
                             U8  acInvId, 
                             U8  acOpType, 
                             U8  acOpCode,
                             U8  acOpCodeLen, 
                             U8  acParamLen,
                             U8* acParamString,
                             U8  acOpClass)
{
  logger.logINGwMsg(false,TRACE_FLAG,0,"In createCmpEvent acCompType"
  " acCompType<%d> acInvId<%d> acOpType<%d> acOpCode<%d> acOpCodeLen<%d> "
  " acParamLen<%d> acParamString<%d> acOpClass<%d>",
  acCompType, acInvId, acOpType, acOpCode, acOpCodeLen, acParamLen, acParamString, acOpClass);

  (*pCompTx) = new TcapComp;
  
  memset(*pCompTx,0,sizeof(TcapComp));

  (*pCompTx)->compType = acCompType;

  (*pCompTx)->invIdItu.pres  = INC_TRUE;
  (*pCompTx)->invIdItu.octet = acInvId;

  (*pCompTx)->opTag           = acOpType;

  if(0 != acOpCodeLen)
  {
    (*pCompTx)->opCode.len      = acOpCodeLen;
    (*pCompTx)->opCode.string[0] = acOpCode;
  }

  if(0 != acParamLen)
  {
    (*pCompTx)->paramTag        = INC_SEQUENCE;
    (*pCompTx)->param.len       = acParamLen;
    (*pCompTx)->param.string = acParamString;
  }
 
  (*pCompTx)->opClass = acOpClass;
  logger.logINGwMsg(false,TRACE_FLAG,0,"Out createCmpEvent");
}


void TcapMessage:: printCallMaps(bool pbGetLock)
{

  logger.logINGwMsg(false,TRACE_FLAG,0,"In printCallMaps");
  
  if (pbGetLock)
  {
    pthread_mutex_lock(&mutex);  
  }

  map<string, std::map<int, TcapUserAddressInformation> >:: iterator iter1;
  map<int, TcapUserAddressInformation> :: iterator iter2;

  iter1 = userDialogues.begin();
  
  for(iter1 = userDialogues.begin(); iter1 != userDialogues.end(); ++iter1) 
  {
    logger.logINGwMsg(false, TRACE_FLAG, 0,"--->>>> [SAS IP] [%s]"
     "\nDialogues Cnt<%d>\n", (iter1->first).c_str(), (iter1->second).size());
      for(iter2 = (iter1->second).begin(); iter2 != (iter1->second).end(); ++iter2)
      {
        logger.logINGwMsg(false, TRACE_FLAG, 0,"-->dlgId <%d> "
         "-->msgType <%d>\n",iter2->first, (iter2->second).mDlgType);
      }
   
  }


  map<int, std::map< int, TcapUserAddressInformation> >::iterator iter3;
  iter3 = stackDialogues.begin();

  for(iter3 = stackDialogues.begin(); iter3 != stackDialogues.end(); ++iter3) 
  {
    logger.logINGwMsg(false, TRACE_FLAG, 0,"SAP ID SuId<%d>",iter3->first);
    for(iter2 = (iter3->second).begin(); iter2 != (iter3->second).end(); ++iter2)
    {
      logger.logINGwMsg(false, TRACE_FLAG, 0,"-->dlgId <%d> "
        "-->msgType <%d>\n",iter2->first, (iter2->second).mDlgType);
    }
  }

  if (pbGetLock)
  {
    pthread_mutex_unlock(&mutex);  
  }

  logger.logINGwMsg(false,TRACE_FLAG,0,"Out printCallMaps");

}

#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
bool TcapMessage::encodeAnsiDlgEv(StAnsiDlgEv &pStAnsiDlgEv,
                                  U8* pcBuf, 
                                  int &piLen)
{
  logger.logINGwMsg(false,TRACE_FLAG,0,"In encodeAnsiDlgEv");

  if(pStAnsiDlgEv.pres.pres && pStAnsiDlgEv.pres.val) 
  {
    addByte(pcBuf,piLen,APP_CONTEXT_NAME_TYPE);
    if(pStAnsiDlgEv.acnType.pres)
    {
      switch(pStAnsiDlgEv.acnType.val)
      {
        case INC_ENC_TYP_INT:
        {
          addByte(pcBuf, piLen, JAIN_ACN_TYPE_INT);
        
          addByte(pcBuf,piLen,APP_CONTEXT_NAME);

          //adding size of int as ACN len
          addByte(pcBuf,piLen,SIZE_OF_INT);
          if(pStAnsiDlgEv.acn.intAcn.pres) 
          {
            addInteger(pcBuf, piLen, pStAnsiDlgEv.acn.intAcn.val);
          }
          
          else
          {
             logger.logINGwMsg(false,ERROR_FLAG,0,
                               "error in encoding ACN TYP INT, but not pres");
             return false;
          }
           
        }
        break;

        //YOGESH
        //creating this separate case for oid type ACN in ansi because
        //the encoding of ACN in oid type is done as an array of U16
        case INC_ENC_TYP_OID:
        {
          addByte(pcBuf, piLen, JAIN_ACN_TYPE_OID);
          
          addByte(pcBuf,piLen,APP_CONTEXT_NAME);

          if(pStAnsiDlgEv.acn.oidAcn.pres)
          {
            addByte(pcBuf, piLen, pStAnsiDlgEv.acn.oidAcn.len <<1);
            
            //checking the piLen after doubling the length
            if((piLen + (pStAnsiDlgEv.acn.oidAcn.len<<1)) > mBufSize)
            {
              doReAllocation(&pcBuf,piLen);
            }   

            memcpy(pcBuf + piLen, pStAnsiDlgEv.acn.oidAcn.val, 
                   pStAnsiDlgEv.acn.oidAcn.len);
            
            piLen += (pStAnsiDlgEv.acn.oidAcn.len<<1);
          }
          else
          {
            logger.logINGwMsg(false,ERROR_FLAG,0,"Error MM ACNtype & ACN Rx");
            return false;
          }
        }
        break;
 
        default:
          logger.logINGwMsg(false,ERROR_FLAG,0,"Unknown acnType<%d>",
                            pStAnsiDlgEv.acnType.val);
      }
    }
    else
    {
      logger.logINGwMsg(false,ERROR_FLAG,0,"acnType not pres");
    }
  }
  else
  { 
    logger.logINGwMsg(false,ERROR_FLAG,0,"dialogue portion not pres");
    return false;  
  }
 
  //have copied it at the time of Begin indication as it is indicated in
  //Buffer data type allocated on heap by stack
  if(0 != dlgR.dlgPortnUInfo.len)
  {
    addByte(pcBuf, piLen, DLG_PORTN_USR_INFO);
    encodeBuffer(pcBuf,dlgR.dlgPortnUInfo.len,piLen,dlgR.dlgPortnUInfo.string);
  }


  if(pStAnsiDlgEv.secType.pres) 
  {
    addByte(pcBuf, piLen, SEC_TYPE);
    addByte(pcBuf, piLen, pStAnsiDlgEv.secType.val);
    
    switch(pStAnsiDlgEv.secType.val) 
    {
      case ST_ANS_ENC_TYP_INT:
      {
        addByte(pcBuf, piLen, JAIN_SEC_TYPE_INT);
        addByte(pcBuf, piLen, SEC_VALUE);

        if(pStAnsiDlgEv.secCntxt.intSec.pres) 
        {
          addInteger(pcBuf, piLen, pStAnsiDlgEv.secCntxt.intSec.val);
        }
        else
        {
          logger.logINGwMsg(false,ERROR_FLAG,0,"Out encodeAnsiDlgEv, intacn "
                            " not pres");
          return false;
        } 
      }
      break;

      case ST_ANS_ENC_TYP_OID:
      {
        addByte(pcBuf, piLen, JAIN_SEC_TYPE_OID);
        addByte(pcBuf, piLen, SEC_VALUE);

        if(pStAnsiDlgEv.secCntxt.oidSec.pres)
        {
          addByte(pcBuf, piLen, pStAnsiDlgEv.secCntxt.oidSec.len<<1);
          
          //checking the piLen after doubling the length
          if((piLen + (pStAnsiDlgEv.secCntxt.oidSec.len<<1)) > mBufSize)
          {
            doReAllocation(&pcBuf ,piLen);
          }   

          memcpy(pcBuf + piLen, pStAnsiDlgEv.secCntxt.oidSec.val, 
                 pStAnsiDlgEv.secCntxt.oidSec.len<<1);
          
          piLen += (pStAnsiDlgEv.secCntxt.oidSec.len<<1);
        }
        else
        {
          logger.logINGwMsg(false,ERROR_FLAG,0,"Out encodeAnsiDlgEv "
                            "Error MM secType & SEC Rx");
          return false;
        }
      }
      break;

      default:
        logger.logINGwMsg(false,ALWAYS_FLAG,0,"Out encodeAnsiDlgEv"
                         " Unknown value of sectype <%d>",
                          pStAnsiDlgEv.secType.val);
        return false;
    }
  }

  if(pStAnsiDlgEv.confInfo.pres)
  {
    addByte(pcBuf, piLen, DLG_CONF_INFO);
    addByte(pcBuf, piLen, pStAnsiDlgEv.confInfo.len);

    encodeBuffer(pcBuf, 
                 pStAnsiDlgEv.confInfo.len, 
                 piLen,
                 pStAnsiDlgEv.confInfo.val);
    
  } 

  logger.logINGwMsg(false,TRACE_FLAG,0,"Out encodeAnsiDlgEv");

  return true;
}

void TcapMessage::dumpAnsiDlgEv(StAnsiDlgEv &pStAnsiDlgEv, char *pcBuf)
{
  logger.logINGwMsg(false,TRACE_FLAG,0,"In dumpAnsiDlgEv()");

  char lcBuf[512];
  memset(lcBuf,0,sizeof(lcBuf));
  
  int lcBufLen = 0;

  if((true ==  pStAnsiDlgEv.pres.pres) && (0x01 == pStAnsiDlgEv.pres.val))
  {
    
    if(true == pStAnsiDlgEv.pVer.pres)
    {
      lcBufLen += sprintf(lcBuf + lcBufLen,"\npVer: <%02x>", 
                          pStAnsiDlgEv.pVer.val);
    }

    if(true == pStAnsiDlgEv.acnType.pres)
    {
      switch(pStAnsiDlgEv.acnType.val) 
      {
        case ST_ANS_ENC_TYP_INT:
         lcBufLen += sprintf(lcBuf + lcBufLen,"\nacnType: ST_ANS_ENC_TYP_INT"); 

         if(true == pStAnsiDlgEv.acn.intAcn.pres)
         {
           lcBufLen += sprintf(lcBuf + lcBufLen,
                               "\nacn: [%d]",pStAnsiDlgEv.acn.intAcn.val); 
         } 
        break;

        case ST_ANS_ENC_TYP_OID:
         lcBufLen += sprintf(lcBuf + lcBufLen,"\nacnType: ST_ANS_ENC_TYP_OID"); 

         if(true == pStAnsiDlgEv.acn.oidAcn.pres)
         {
           lcBufLen += sprintf(lcBuf + lcBufLen, "\nacn: "); 
           for(int i = 0; i< pStAnsiDlgEv.acn.oidAcn.len; i++)
           {
             lcBufLen += sprintf(lcBuf + lcBufLen, "%04x", 
                                pStAnsiDlgEv.acn.oidAcn.val[i]);
           }
         }
        break;

        default:
         logger.logINGwMsg(false,ERROR_FLAG,0,"Unknown acnType %d",
                           pStAnsiDlgEv.acnType.val);
        
      }
    }


    if((true == pStAnsiDlgEv.usrInfo.pres) && 
       (NULL != pStAnsiDlgEv.usrInfo.val))
    {
      short int msgLen, len;
      SFndLenMsg (pStAnsiDlgEv.usrInfo.val, &msgLen);

      U8 * lpcBuf = new (nothrow) unsigned char[msgLen];

      SCpyMsgFix (pStAnsiDlgEv.usrInfo.val, 0, msgLen,
                 (Data *)lpcBuf,
                  &len);
      
      lcBufLen += sprintf(lcBuf + lcBufLen, "\nusrInfo : ");
      for(int i = 0 ; i < msgLen; i++) 
      {
        lcBufLen += sprintf(lcBuf + lcBufLen, "%02x ",
                            pStAnsiDlgEv.usrInfo.val[i]);
      } 

      if(NULL != lpcBuf)
      {
        delete  [] lpcBuf; lpcBuf = NULL;
      }
    }
    else
    {
      lcBufLen += sprintf(lcBuf + lcBufLen, "\nusrInfo : not pres"); 
    }
    

    if(pStAnsiDlgEv.secType.pres)
    {
      switch(pStAnsiDlgEv.secType.val)
      {
        case ST_ANS_ENC_TYP_INT:
         lcBufLen += sprintf(lcBuf + lcBufLen,"\nsecType: ST_ANS_ENC_TYP_INT"); 

         if(true == pStAnsiDlgEv.secCntxt.intSec.pres)
         {
           lcBufLen += sprintf(lcBuf + lcBufLen,
                               "\nsec: [%d]",pStAnsiDlgEv.secCntxt.intSec.val); 
         } 
        break;

        case ST_ANS_ENC_TYP_OID:
         lcBufLen += sprintf(lcBuf + lcBufLen,"\nsecType: ST_ANS_ENC_TYP_OID"); 

         if(true == pStAnsiDlgEv.secCntxt.oidSec.pres)
         {
           lcBufLen += sprintf(lcBuf + lcBufLen, "\nsec: "); 
           for(int i = 0; i< pStAnsiDlgEv.secCntxt.oidSec.len; i++)
           {
             lcBufLen += sprintf(lcBuf + lcBufLen, "%04x", 
                                pStAnsiDlgEv.secCntxt.oidSec.val[i]);
           }
         }
        break;

        default:
        logger.logINGwMsg(false,ERROR_FLAG,0,"Unknown secType <%d>",
                          pStAnsiDlgEv.secType.val);
      }
    }
    if (0x01 == pStAnsiDlgEv.confInfo.pres)
    {
      lcBufLen += sprintf(lcBuf + lcBufLen, "\nconfInfo : "); 

      for(int i = 0; i < pStAnsiDlgEv.confInfo.len; i++)
      {
        lcBufLen += sprintf(lcBuf + lcBufLen, "%02", 
                                pStAnsiDlgEv.confInfo.val[i]);
      }
    }

  }
  else 
  {
    lcBufLen += sprintf(lcBuf + lcBufLen, "\nStAnsiDlgEv.pres:False");
  }

  if(NULL != pcBuf)
  {
    memcpy(pcBuf, lcBuf, lcBufLen);
  }

  logger.logINGwMsg(false,TRACE_FLAG,0,"<%s>Out dumpAnsiDlgEv()",lcBufLen);
}
#endif

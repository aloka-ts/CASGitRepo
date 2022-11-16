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
//     File:     INGwTcapMsgLogger.C
//
//     Desc:     <Description of file>
//
//     Author     	Date     		Description
//    ----------------------------------------------------------------
//     Rajeev Arya     11/21/07			Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwTcapProvider");

#include <strings.h>
#include <Util/imOid.h>

#include <INGwTcapProvider/INGwTcapMsgLogger.h>
#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>
#include <INGwInfraStreamManager/INGwIfrSmFileHandler.h>
#include <INGwInfraUtil/INGwIfrUtlGlbInclude.h>

INGwTcapMsgLogger* INGwTcapMsgLogger::mpSelf = 0;

INGwTcapMsgLogger::INGwTcapMsgLogger(string pduFile, int loggingLevel) :
      mpPduStream(0), mCounter(0), mLoggingLevel(loggingLevel)
{
  logger.logINGwMsg(false, TRACE_FLAG, 0, "IN INGwTcapMsgLogger()");

  if (pduFile.empty()) {
    pduFile = "ss7Pdu.log";
	}

  mpPduStream = new BpGenUtil::INGwIfrSmAppStreamer("SS7-Messages", pduFile.c_str());
  pthread_mutex_init(&mLock, 0);

  logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT INGwTcapMsgLogger()");
}

INGwTcapMsgLogger::INGwTcapMsgLogger() :
                    mpPduStream(0), mLoggingLevel(0), mCounter(0)
{
  logger.logINGwMsg(false, TRACE_FLAG, 0, "IN INGwTcapMsgLogger()");

  pthread_mutex_init(&mLock, 0);

  logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT INGwTcapMsgLogger()");
}

INGwTcapMsgLogger::~INGwTcapMsgLogger()
{
  logger.logINGwMsg(false, TRACE_FLAG, 0, "IN ~INGwTcapMsgLogger()");

  pthread_mutex_destroy(&mLock);
  if (0 != mpPduStream)
    delete mpPduStream;

  logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT ~INGwTcapMsgLogger()");
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
INGwTcapMsgLogger&
INGwTcapMsgLogger::getInstance()
{
  logger.logINGwMsg(false, TRACE_FLAG, 0, "IN INGwTcapMsgLogger::instance()");

  if (0 == mpSelf) {
    mpSelf = new INGwTcapMsgLogger();
  }

  logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT INGwTcapMsgLogger::instance()");
  return *mpSelf;

}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
int
INGwTcapMsgLogger::initialize()
{
  logger.logINGwMsg(false, TRACE_FLAG, 0, "IN initialize()");

  int retVal = G_SUCCESS;

  try {
    std::string pduFile;
    INGwIfrPrParamRepository::getInstance().getValue(ingwPDU_LOG_FILE, pduFile);

    std::string pduLogLvl;
    INGwIfrPrParamRepository::getInstance().getValue(ingwPDU_LOG_LEVEL, pduLogLvl);

    if (pduFile.empty())
      pduFile = "inapPdu.log";
    int logLvl = (pduLogLvl.empty() == true) ? 0 :
                   (0 > atoi(pduLogLvl.c_str())) ? 0 : atoi(pduLogLvl.c_str());

    logger.logINGwMsg(false, TRACE_FLAG, 0,
             "Pdu Logging File <%s> Logging Status <%s>.",
             pduFile.c_str(), pduLogLvl.c_str());

    mLoggingLevel = logLvl;

    mpPduStream = new BpGenUtil::INGwIfrSmAppStreamer("INAP-Messages", pduFile.c_str());
    mpPduStream->setLoggable(true);

    std::string pduLogSize;
    INGwIfrPrParamRepository::getInstance().getValue(ingwPDU_LOG_FILE_SIZE,pduLogSize);

    logger.logINGwMsg(false, TRACE_FLAG, 0,
                    "Pdu Logging File Size = <%s>.", pduLogSize.c_str());

    int logSize = (pduLogSize.empty() == true) ? 0 : 
                  (0 > atoi(pduLogSize.c_str())) ? 0 : atoi(pduLogSize.c_str());
    if ((logSize < 1000000) || logSize > 100000000) {
      logSize = 5000000;
    }
    BpGenUtil::INGwIfrSmFileHandler *handler = mpPduStream->getHandler(); 
    handler->setLimit(logSize);
  } catch (...) {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "Exception caught in initialize.");
    retVal = G_FAILURE;
  }

  logger.logINGwMsg(false, ALWAYS_FLAG, 0, "Return Code from initialize <%s>.",
                  (retVal == G_FAILURE) ? "G_FAILURE" : "G_SUCCESS");
  logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT initialize()");
  return retVal;
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
void
INGwTcapMsgLogger::setLoggingLevel(int level)
{
  logger.logINGwMsg(false, TRACE_FLAG, 0, "IN setLoggingLevel()");

  logger.logINGwMsg(false, TRACE_FLAG, 0, 
                  "Setting Pdu Logging Level to <%d>", level);
  pthread_mutex_lock(&mLock);

  mLoggingLevel = level;
  pthread_mutex_unlock(&mLock);

  logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT setLoggingLevel()");
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
int
INGwTcapMsgLogger::dumpMsg(int dlg, int msgType, TcapMessage *msg, 
													 bool rxOrTx)
{
  LogINGwTrace(false, 0,"IN dumpMessage, tcapMsg");

  int retVal = G_SUCCESS;

	if(msg == NULL) 
		return 0;

  pthread_mutex_lock(&mLock);

  if (mLoggingLevel == 0)
  {
    pthread_mutex_unlock(&mLock);
    return 0;
  }

  char buffer[4096];
  int  bufLen = 0;
  bzero(buffer, 4096);

  this->fetchHeaderInfo(buffer, bufLen, rxOrTx);
	if(msgType == EVTSTUDATIND || msgType == EVTSTUDATREQ) 
  	bufLen += sprintf(buffer + bufLen,"\n*DIALOGUE*\n");
	else if(msgType == EVTSTUUDATIND || msgType == EVTSTUUDATREQ)
  	bufLen += sprintf(buffer + bufLen,"\n*UNIDIRECTIONAL DIALOGUE*\n");

  bufLen += sprintf(buffer + bufLen, "dlgId: %d\n", dlg);

	if( msgType == EVTSTUDATIND || msgType == EVTSTUUDATIND ||
		msgType == EVTSTUDATREQ || msgType == EVTSTUUDATREQ) {

		// Check for Source and Destination Address
		dumpAddress(msg->dlgR.srcAddr, msg->dlgR.dstAddr, buffer, bufLen, rxOrTx);

	  string TransType;
		switch(msg->dlgR.dlgType) {
			case INC_UNI:
      	TransType = "TC-UNI"; break;
			case INC_BEGIN:
      	TransType = "TC-BEG"; break;
			case INC_CONTINUE:
      	TransType = "TC-CONT"; break;
			case INC_END:
      {
        if (msg->dlgClndByINGw)
      	  TransType = "TC-End (INGw)";
        else
      	  TransType = "TC-End";
      }
      break;
			case INC_U_ABORT:
      {
        if (msg->dlgClndByINGw)
      	  TransType = "TC-U-Abrt (INGw)";
        else
      	  TransType = "TC-U-Abrt";
      }
      break;

			case INC_P_ABORT:
      	TransType = "TC-P-Abrt"; break;

#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
      case STU_QRY_PRM:
      	TransType = "TC-QWP"; break;

      case STU_QRY_NO_PRM:
      	TransType = "TC-QWOP"; break;

      case STU_CNV_PRM:
      	TransType = "TC-CWP"; break;

      case STU_CNV_NO_PRM:
      	TransType = "TC-CWOP"; break;

      case STU_RESPONSE:
      	TransType = "TC-RESP"; break;

      case STU_ANSI_UABORT:
      	TransType = "TC-Uabrt"; break;

      case STU_ANSI_PABORT:
      	TransType = "TC-P-Abrt"; break;
#endif
			case INC_NOTICE:
      	TransType = "Notice"; break;
			default: 
      	TransType = "Unknown"; break;
		}

    if (msg->dlgR.dlgType == INC_U_ABORT) {
	    bufLen += sprintf(buffer + bufLen, "MsgType: %s\n",
                        TransType.c_str());
      if (msg->dlgR.uInfo.len != 0) {
	      string abortReason;
		    switch(msg->dlgR.uInfo.string[msg->dlgR.uInfo.len - 1]) {
		    	case 0x01:
          	abortReason = "no-reason-given"; break;
		    	case 0x02:
          	abortReason = "application-timer-expired"; break;
		    	case 0x03:
          	abortReason = "protocol-prohibited-signal-received"; break;
		    	case 0x04:
          	abortReason = "abnormal-processing"; break;
		    	case 0x05:
          	abortReason = "congestion"; break;
		    	case 0x06:
          	abortReason = "ac-negotiation-failed"; break;
		    	case 0x07:
          	abortReason = "unrecognized-extension-parameter"; break;
		    	default: 
          	abortReason = "Unknown"; break;
		    }
        
	      bufLen += sprintf(buffer + bufLen, "Abort-Reason: %s\nUserInfo: ",
                          (char *)abortReason.c_str());

        for (int i = 0; i < msg->dlgR.uInfo.len; i++)
	        bufLen += sprintf(buffer + bufLen, "%02x ", msg->dlgR.uInfo.string[i]);
      }
    }
    else {
	    bufLen += sprintf(buffer + bufLen,"MsgType: %s\n",TransType.c_str());
    }
	}
	else if(msgType ==EVTSTUCMPIND) {
		dumpComponent(msg->comp, buffer, bufLen);
	}
	else {
  	bufLen += sprintf(buffer + bufLen, 
  	"Unkn MsgType: %d\n", msg->msg_type);
	}

  bufLen += sprintf(buffer + bufLen, 
  "\n-----------------------------------------------------------------");

  if (mpPduStream->isLoggable())
  {
    string msg(buffer);
    mpPduStream->log("%s\n", msg.c_str());
  }

  pthread_mutex_unlock(&mLock);

  LogINGwTrace(false, 0, "OUT dumpMessage");
  return retVal;
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
int
INGwTcapMsgLogger::dumpComponent(int dlg, int msgType, TcapComp *msg, bool rxOrTx)
{
  LogINGwTrace(false, 0,"IN dumpMessage, tcapMsg");

  int retVal = G_SUCCESS;

	if(msg == NULL) 
		return 0;

  pthread_mutex_lock(&mLock);

  if (mLoggingLevel == 0)
  {
    pthread_mutex_unlock(&mLock);
    return 0;
  }

  char buffer[4096];
  int  bufLen = 0;
  bzero(buffer, 4096);

  this->fetchHeaderInfo(buffer, bufLen, rxOrTx);
  bufLen += sprintf(buffer + bufLen,"\n----- C O M P O N E N T ----- \n");
  bufLen += sprintf(buffer + bufLen, "dlgId: %d\n", dlg);

	if(msgType ==EVTSTUCMPIND) {
		dumpComponent(msg, buffer, bufLen);
	}
	else {
  	bufLen += sprintf(buffer + bufLen, 
  	"Unkn MsgType: %d\n", msgType);
	}

  bufLen += sprintf(buffer + bufLen, 
  "-----------------------------------------------------------------");

  if (mpPduStream->isLoggable())
  {
    string msg(buffer);
    mpPduStream->log("%s\n", msg.c_str());
  }

  pthread_mutex_unlock(&mLock);

  LogINGwTrace(false, 0, "OUT dumpMessage");
  return retVal;
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
void
INGwTcapMsgLogger::fetchHeaderInfo(char *buffer, int &bufLen, bool txMode, bool logCodec) 
{
  LogINGwTrace(false, 0,"IN fetchHeaderInfo");
  //bufLen += sprintf(buffer + bufLen, 
  //"-----------------------------------------------------------------\n");
  if(!logCodec) { 
  bufLen += sprintf(buffer + bufLen, "Message [%d]     : %s ", 
                    ++mCounter, (txMode == RX) ? "RXD" : "TXD");
  }
  else{
  bufLen += sprintf(buffer + bufLen, "Message [%d]     : %s ", 
                    ++mCounter, (txMode == RX) ? "ENC" : "DEC");
  }
  struct timeval currTime;
  struct tm localTime;
  gettimeofday(&currTime, 0);
  localtime_r(&currTime.tv_sec, &localTime);

  bufLen += strftime(buffer + bufLen, 64, "%C", &localTime);
  bufLen += sprintf(buffer + bufLen, " [%03d Msec.]\n", currTime.tv_usec/1000);
  LogINGwTrace(false, 0,"OUT fetchHeaderInfo");
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
void
INGwTcapMsgLogger::dumpAddress(SccpAddr *src, SccpAddr *dst, char *buffer, 
													int &bufLen, int pbRxOrTx)
{
  LogINGwTrace(false, 0, "IN dumpAddress");

	if(src == NULL || dst == NULL)
		return;

	SccpAddr  *tmp = NULL;
  
  char *lpcClngOrCld = "UNKNOWN"; 
	for (int i=0 ; i < 2; ++i ) {
    
   
		if(i==0){
			tmp = src;
      if(RX == pbRxOrTx){
        lpcClngOrCld = "\nCalled";
      }
      else{
        lpcClngOrCld = "\nCalling";
      }
    }
		else {
			tmp = dst;
      if(RX == pbRxOrTx){
        lpcClngOrCld = "\nCalling";
      }
      else{
        lpcClngOrCld = "\nCalled";
      }
    }
		
		bufLen += sprintf(buffer + bufLen, "%s Addr:\n", lpcClngOrCld);
																		
		if(tmp->ssnInd)
			bufLen += sprintf(buffer + bufLen, "\tSSN: %d\n", tmp->ssn);

		if(tmp->pcInd)
			bufLen += sprintf(buffer + bufLen, "\tPC: %d\n", tmp->pc);

#ifdef PRINT_ADDRINFO
		if(tmp->status) {
			bufLen += sprintf(buffer + bufLen, "NTT Addr Rxd\n");
			bufLen += sprintf(buffer + bufLen, "\tCarrierIdenCode: %02X-%02X\n",
						tmp->addrInfo.careerIdenCode[0], tmp->addrInfo.careerIdenCode[1]);

			if(tmp->addrInfo.addrInfoType == 0xe8) {
			bufLen += sprintf(buffer + bufLen, "\tCarrierIdenCode: %02X-%02X\n",
						tmp->addrInfo.careerIdenCode[0], tmp->addrInfo.careerIdenCode[1]);
			}
		}
#endif

		if(tmp->rtgInd == 0x00) // GTT Based
		{
			switch(tmp->gt.format) {
				case 1:
  				bufLen += sprintf(buffer + bufLen, "GT Format: 1\n");
  				bufLen += sprintf(buffer + bufLen, "Nature Of Address: %s\n", 
								(tmp->gt.gt.f1.natAddr == 0x1)?"Subscriber Number":
								(tmp->gt.gt.f1.natAddr == 0x3)?"National Sig Number":
								(tmp->gt.gt.f1.natAddr == 0x4)?"International Number":"Unknown");
					break;
				case 2:
  				bufLen += sprintf(buffer + bufLen, "%s GT Frmt: 2\n",lpcClngOrCld);
  				bufLen += sprintf(buffer + bufLen, "TType: %d\n",
							tmp->gt.gt.f2.tType);
					break;
				case 3:
  				bufLen += sprintf(buffer + bufLen, "Calling GT Formt:3\n");
  				bufLen += sprintf(buffer + bufLen, "Translation Type : %d\n", tmp->gt.gt.f3.tType);
  				bufLen += sprintf(buffer + bufLen, "Numbering Plan   : %d\n", 
								(tmp->gt.gt.f3.numPlan == 0x0)?"Unknown":
								(tmp->gt.gt.f3.numPlan == 0x1)?"ISDN Num Plan":
								(tmp->gt.gt.f3.numPlan == 0x2)?"Telephony Num Plan":
								(tmp->gt.gt.f3.numPlan == 0x3)?"Data Num Plan":
								(tmp->gt.gt.f3.numPlan == 0x4)?"Telex Num Plan":
								(tmp->gt.gt.f3.numPlan == 0x5)?"Maritime Mobile Num Plan":
								(tmp->gt.gt.f3.numPlan == 0x6)?"Land Mobile Num Plan":
								(tmp->gt.gt.f3.numPlan == 0x7)?"ISDN Mobile Num Plan":"Unknown");
  							bufLen += sprintf(buffer + bufLen, "Encoding Scheme  : %d\n", 
								(tmp->gt.gt.f3.encSch == 0)?"Unknown":
								(tmp->gt.gt.f3.encSch == 0x1)?"BCD Odd":
								(tmp->gt.gt.f3.encSch == 0x2)?"BCD Even":"Unknown");
					break;
					case 4:
  					bufLen += sprintf(buffer + bufLen, "%s GT Frmt: 4\n",lpcClngOrCld);
  					bufLen += sprintf(buffer + bufLen, "Translation Type : %d\n", tmp->gt.gt.f4.tType);
  				bufLen += sprintf(buffer + bufLen, "Numbering Plan   : %d\n", 
								(tmp->gt.gt.f4.numPlan == 0x0)?"Unknown":
								(tmp->gt.gt.f4.numPlan == 0x1)?"ISDN Num Plan":
								(tmp->gt.gt.f4.numPlan == 0x2)?"Telephony Num Plan":
								(tmp->gt.gt.f4.numPlan == 0x3)?"Data Num Plan":
								(tmp->gt.gt.f4.numPlan == 0x4)?"Telex Num Plan":
								(tmp->gt.gt.f4.numPlan == 0x5)?"Maritime Mobile Num Plan":
								(tmp->gt.gt.f4.numPlan == 0x6)?"Land Mobile Num Plan":
								(tmp->gt.gt.f4.numPlan == 0x7)?"ISDN Mobile Num Plan":"Unknown");
  				bufLen += sprintf(buffer + bufLen, "Encoding Scheme  : %d\n", 
								(tmp->gt.gt.f4.encSch == 0)?"Unknown":
								(tmp->gt.gt.f4.encSch == 0x1)?"BCD Odd":
								(tmp->gt.gt.f4.encSch == 0x2)?"BCD Even":"Unknown");
  				bufLen += sprintf(buffer + bufLen, "Nature Of Address: %s\n", 
								(tmp->gt.gt.f4.natAddr == 0x1)?"Subscriber Number":
								(tmp->gt.gt.f4.natAddr == 0x3)?"National Sig Number":
								(tmp->gt.gt.f4.natAddr == 0x4)?"International Number":"Unknown");
					break;
				case 5:
  				bufLen += sprintf(buffer + bufLen, "%s GT Frmt: 5\n",lpcClngOrCld);
  				bufLen += sprintf(buffer + bufLen, "IP Address       : %d\n",
												tmp->gt.gt.f5.ipAddr);
					break;
				default:
  				bufLen += sprintf(buffer + bufLen, "%s GT Frmt: Unknown\n",lpcClngOrCld);
					break;
			}
		}
		else if(tmp->rtgInd == 0x01) // SN+PC
		{
  		unsigned char pc[3];
			if(tmp->ssnInd == true) {
  			bufLen += sprintf(buffer + bufLen, "Self SSN : %d\n", tmp->ssn);
			}
			if (tmp->pcInd == true) {
  			bufLen += sprintf(buffer + bufLen, "Self PC  : %d\n", tmp->pc); 
	
 	 		// ITU-T pointcode is of 3bits-8bits-8bits format
 	 		pc[0] = (tmp->pc & 0x00000007);
 	 		pc[1] = ((tmp->pc >> 3) & 0x000000ff);
 	 		pc[2] = ((tmp->pc >> 11) & 0x00000007);
 	 		bufLen += sprintf(buffer + bufLen, "Z-C-M [%d-%d-%d]\n",
 	                   pc[2], pc[1], pc[0]);
				}
		}
	}
  LogINGwTrace(false, 0, "OUT fetchTransInfo");
  return;
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
int
INGwTcapMsgLogger::dumpComponent(TcapComp *comp, char* buffer, int& bufLen)
{
  LogINGwTrace(false, 0, "IN dumpComponent");

  int retVal = G_SUCCESS;
  bufLen += sprintf(buffer + bufLen,"\n----- C O M P O N E N T ----- \n");

  string compType;

  bool l_bOprCodePresent = false;
  bool l_bErrCodePresent = false;
  bool l_bProbCodePresent = false;

  switch(comp->compType)
  {
    case INC_INVOKE:
      compType = "Invoke";
      l_bOprCodePresent = true;
      break;
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)

    case STU_INVOKE_L:
      compType = "Invoke_l";
      l_bOprCodePresent = true;
    break;

    case STU_INVOKE_NL:
      compType = "Invoke_nl";
      l_bOprCodePresent = true;
    break;

#endif
    case INC_RET_RES_L:
      compType = "Ret-Res-L";
      if(comp->opCode.len != 0) {
        l_bOprCodePresent = true;
      }
      break;
    case INC_RET_RES_NL:
      compType = "Ret-Res-NL";
      if(comp->opCode.len != 0) {
        l_bOprCodePresent = true;
      }
      break;
    case INC_RET_ERR:
      compType = "Ret-Err";
			break;
    case INC_REJECT:
      compType = "Cmp-Sub-Layer Reject";
      l_bProbCodePresent = true;
      break;
    case INC_PROB_INVOKE:
      compType = "Problem Invoke";
      l_bProbCodePresent = true;
      break;
    case INC_PROB_RET_ERR:
      compType = "Problem Ret Err";
      l_bProbCodePresent = true;
      break;
    case INC_PROB_RET_RES:
      compType = "Problem Ret Res";
      l_bProbCodePresent = true;
      break;
    default:
      compType = "Unkn Op Type";
      break;
  }

  bufLen += sprintf(buffer + bufLen,   "CompType  : %s\n",
                    compType.c_str());

  if (comp->invIdItu.pres == true) {
  	bufLen += sprintf(buffer + bufLen, "InvokeId  : %d\n",
                    comp->invIdItu.octet);
	}

	if(comp->linkedId.pres == true) {
    bufLen += sprintf(buffer + bufLen, "LinkedId  : %d\n",
                      comp->linkedId.octet);
	}

  string msgType = "";

  if(l_bOprCodePresent) {

    unsigned short oprCode = 0;
    oprCode = comp->opCode.string[0];

    int swOpCode = (int)oprCode;
		switch(swOpCode) {
			case  0: msgType="IDP"; break;
			case  1: msgType="RSN"; break;
      case  2: msgType="RSA"; break;
			case 17: msgType="ETC"; break;
			case 18: msgType="DFC"; break;
			case 20: msgType="CON"; break;
			case 22: msgType="RC"; break;
			case 23: msgType="RRB"; break;
			case 24: msgType="ERB"; break;
			case 25: msgType="RNCE"; break;
			case 26: msgType="ENC"; break;
			case 31: msgType="CONTINUE"; break;
			case 46: msgType="SCI"; break;
			case 55: msgType="AT"; break;
			case 96: msgType="ENTITY RELEASE"; break;
			default:
			bufLen += sprintf(buffer + bufLen, "OpCode    : %d\n",
						swOpCode);
		}

    if (msgType.empty() == false) {
      bufLen += sprintf(buffer + bufLen, "OpCode    : %s\n",
                        msgType.c_str());
    } 
    else {
      bufLen += sprintf(buffer + bufLen, "OpCodeLen : %d\n",
                        comp->opCode.len);
    }
  }
	/*
  else if(l_bErrCodePresent) {
    
    msgType = "";

    unsigned short errCode = 0;
    errCode = comp.errCode.string[0];

    int l_iErrCode = (int)errCode;

    switch(l_iErrCode) {
	  	case 0:
	  		msgType = "Cancelled";
	  		break;
	  	case 1:
	  		msgType = "CancelFailed";
	  		break;
	  	case 3:
	  		msgType = "ETCFailed";
	  		break;
	  	case 4:
	  		msgType = "ImproperCalledResponse";
	  		break;
	  	case 6:
	  		msgType = "MissingCustomerRecord";
	  		break;
	  	case 7:
	  		msgType = "MissingParameter";
	  		break;
	  	case 8:
	  		msgType = "ParameterOutOfRange";
	  		break;
	  	case 10:
	  		msgType = "RequestedInfoError";
	  		break;
	  	case 11:
	  		msgType = "SystemFailure";
	  		break;
	  	case 12:
	  		msgType = "TaskRefused";
	  		break;
	  	case 13:
	  		msgType = "UnavailableResource";
	  		break;
	  	case 14:
	  		msgType = "UnexpectedComponentSequence";
	  		break;
	  	case 15:
	  		msgType = "UnexpectedDataValue";
	  		break;
	  	case 16:
	  		msgType = "UnexpectedParameter";
	  		break;
	  	case 17:
	  		msgType = "UnknownLegID";
	  		break;
	  	case 18:
	  		msgType = "UnknownResource";
	  		break;
	  	case 21:
	  		msgType = "ScfReferral";
	  		break;
	  	case 22:
	  		msgType = "ScfTaskRefused";
	  		break;
	  	case 23:
	  		msgType = "ChainingRefused";
	  		break;
	  }

    if (msgType.empty() == false) {
      bufLen += sprintf(buffer + bufLen, "Error Code         : %s\n",
                        msgType.c_str());
    }
    else {
      bufLen += sprintf(buffer + bufLen, "Error Code Len     : %d\n",
                        comp.errCode.len);
    }
  }
  else if(l_bProbCodePresent) {

    msgType = "";

    unsigned short probCode = 0;
    probCode = comp.probCode.string[0];

    int l_iProbCode = (int)probCode;

    switch(comp.probTag) {

      case M7H_PROB_GENERAL:  // 0x80
      {
	  		msgType = "General ProblemType Tag, ProbCode ";
        switch(l_iProbCode) {

          case M7H_UR_COMP :   //0x00
            msgType += "Unrecognized Component";
          break;

          case M7H_MTYPED_COMP : //  0x01
            msgType += "Mistyped Component";
          break;

          case M7H_BAD_STRUCT_COMP :  // 0x02
            msgType += "Badly Structured Component";
          break;
        }
      }
	  	break;

	  	case M7H_PROB_INVOKE: // 0x81
      {
	  		msgType = "Invoke ProblemType Tag, ProbCode ";

        switch(l_iProbCode) {

          case M7H_DUP_INVOKE_ID :  // 0x00
            msgType += "Duplicate InvokeId";
          break;

          case M7H_UR_OP         :  // 0x01     
            msgType += "unrecognized Operation";
          break;

          case M7H_MTYPED_PARAM  :  // 0x02
            msgType += "Mistyped Parameter";
          break;

          case M7H_RESOURCE_LIMIT:  // 0x03
            msgType += "Resource Limitation";
          break;

          case M7H_INIT_RELEASE  :  // 0x04
            msgType += "Initiating Release";
          break;

          case M7H_UR_LNKED_ID   :  // 0x05
            msgType += "Unrecognized LinkedID";
          break;

          case M7H_LNKED_RSP_UNX :  // 0x06
            msgType += "Linked Response unexpected";
          break;

          case M7H_UNX_LNKED_OP  :  // 0x07
            msgType += "Unexpected Linked Operation";
          break;

        }
      }
	  	break;

	  	case M7H_PROB_RET_RES: // 0x82
      {
	  		msgType = "Return Result ProbTypeTag, ProbCode ";

        switch(l_iProbCode) {

          case M7H_UR_INVKID: // 0x000
            msgType += "Unrecognized InvokeId";
          break;
          case M7H_UNX_RR: // 0x01
            msgType += "Return Result unexpected";
          break;
          case M7H_MTYPED_PARAM: // 0x02
            msgType += "Mistyped Parameter";
          break;
        }
      }
	  	break;

	  	case M7H_PROB_RET_ERR: // 0x83
      {
	  		msgType = "Return Error ProbtypeTag, ProbCode ";

        switch(l_iProbCode) {

          case M7H_RE_UR_INVKID: // 0x000
            msgType += "Unrecognized InvokeId";
          break;
          case M7H_RE_UNX_RE: // 0x01
            msgType += "Return Error unexpected";
          break;
          case M7H_UR_ERROR: // 0x02
            msgType += "Unrecognized Error";
          break;
          case M7H_UNX_ERROR: // 0x03
            msgType += "Unexpected Error";
          break;
          case M7H_RE_MTYPED_PAR: // 0x04
            msgType += "Mistyped Parameter";
          break;
        }
      }
  	break;
    }
    
    if (msgType.empty() == false) {
      bufLen += sprintf(buffer + bufLen, "Problem Code       : %s\n",
                        msgType.c_str());
    }
    else {
     bufLen += sprintf(buffer + bufLen, "Problem Code Len   : %d\n",
                        comp.probCode.len);
    }
  }
	*/

  bufLen += sprintf(buffer + bufLen, "PDU Rxed Len[%d]: \n\t",
                    comp->param.len);
  if (NULL != comp->param.string) {
    for(int i = 0; i < comp->param.len; ++i)
    {
      if (i != 0 && (i%16 == 0))
      bufLen += sprintf(buffer + bufLen, "\n\t");
      bufLen += sprintf(buffer + bufLen, "%02x ", comp->param.string[i]);
      //printf("%02x ", comp->param.string[i]);
    }
  }
  else {
    logger.logINGwMsg(false, VERBOSE_FLAG, 0, 
           "Comp Param buffer is null. Length<%d>", comp->param.len);
  }
  bufLen += sprintf(buffer + bufLen, "\n");
  LogINGwTrace(false, 0, "OUT dumpComponent");
  return retVal;
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
int 
INGwTcapMsgLogger::dumpMsg(char * str, int msgType, bool rxOrTx)
{
  LogINGwTrace(false, 0, "IN dumpMsg");

  int retVal = G_SUCCESS;

  pthread_mutex_lock(&mLock);

  char buffer[4096];
  int  bufLen =0;
  bzero(buffer, 4096);

  if(msgType == 100) // 100 for Dumping Configuration Messages
  {
    bufLen += sprintf(buffer + bufLen,
			"\n----- STACK CONFIGURATION DUMP -----\n");
    mpPduStream->log("%s\n", buffer);
    mpPduStream->log("%s\n", str);

    pthread_mutex_unlock(&mLock);
    LogINGwTrace(false, 0, "OUT dumpMsg");
    return 1;
  }

  if ( ! mpPduStream->isLoggable()) {
    pthread_mutex_unlock(&mLock);
    LogINGwTrace(false, 0, "OUT dumpMsg");
    return 1;
  }

  this->fetchHeaderInfo(buffer, bufLen, rxOrTx);

  if (msgType == CM_BND_NOK || msgType == CM_BND_OK )
  {
    bufLen += sprintf(buffer + bufLen,
			"\n----- B I N D  C O N F I R M A T I O N  ----- \n");
  }
	else if(msgType == EVTSSN_STE_IND || msgType == EVTPC_STE_IND )
	{
    bufLen += sprintf(buffer + bufLen,
                      "\n----- PC/SSN S T A T U S  I N D I C A T I O N ----- \n");
  }
	else if(msgType == EVTSSN_STE_CFM || msgType == EVTSSN_STA_CFM) {
    bufLen += sprintf(buffer + bufLen,
							"\n----- S T A T U S  C O N F I R M A T I O N  ----- \n");
	}
	else if(msgType == EVTSSN_CORD_IND ) {
    bufLen += sprintf(buffer + bufLen,
			"\n----- SUBSYSTEM COORDINATION STATE CHANGE INDICATION ----- \n");
	}
	else if(msgType == EVTSSN_CORD_CFM ) {
    bufLen += sprintf(buffer + bufLen,
			"\n----- SUBSYSTEM COORDINATION STATE CHANGE CONFIRMATION ----- \n");
	}
	else if(msgType == EVTSTUGTTIND) {
    	bufLen += sprintf(buffer + bufLen,
			"\n----- GTT INDICATION Received----- \n");
	}
	else if(msgType == -1) {
    	bufLen += sprintf(buffer + bufLen,
			"\n----- Msg Rcvd from SAS ----- \n");
  }

  bufLen += sprintf(buffer + bufLen, "%s\n", str);
  bufLen += sprintf(buffer + bufLen, 
    "-----------------------------------------------------------------");

  if (mpPduStream->isLoggable())
  {
    mpPduStream->log("%s\n", buffer);
  }

  pthread_mutex_unlock(&mLock);

  LogINGwTrace(false, 0, "OUT dumpMsg");
  return retVal;
}

int 
INGwTcapMsgLogger::dumpCodecMsg(char * str, bool encOrdec)
{  
  LogINGwTrace(false, 0, "IN dumpCodecMsg");

  int retVal = G_SUCCESS;

  pthread_mutex_lock(&mLock);

  char buffer[4096];
  int  bufLen =0;
  bzero(buffer, sizeof(buffer));
  this->fetchHeaderInfo(buffer, bufLen, encOrdec, true);

  bufLen += sprintf(buffer + bufLen,"%s",str);
  bufLen += sprintf(buffer + bufLen, 
    "\n-----------------------------------------------------------------");

  if (mpPduStream->isLoggable())
  {
    mpPduStream->log("%s\n", buffer);
  }

  pthread_mutex_unlock(&mLock);

  LogINGwTrace(false, 0, "OUT dumpCodecMsg");
  return retVal;
}

int 
INGwTcapMsgLogger::getLoggingLevel() {
  return mLoggingLevel; 
}

int 
INGwTcapMsgLogger::dumpString(char* str)
{  
  LogINGwTrace(false, 0, "IN dumpString");

  int retVal = G_SUCCESS;

  pthread_mutex_lock(&mLock);

  if (mpPduStream->isLoggable())
  {
    mpPduStream->log("%s\n", str);
  }

  pthread_mutex_unlock(&mLock);

  LogINGwTrace(false, 0, "OUT dumpString");
  return retVal;
}


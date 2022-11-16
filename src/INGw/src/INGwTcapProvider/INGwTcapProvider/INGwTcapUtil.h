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
//     File:     INGwTcapUtil.C
//
//     Desc:     <Description of file>
//
//     Author       Date        Description
//    ----------------------------------------------------------------
//     Rajeev Arya     11/21/07     Initial Creation
//********************************************************************
#ifndef _INGW_TCAP_UTIL_H_
#define _INGW_TCAP_UTIL_H_

#include <string>
#include <strings.h>
#include <Util/LogMgr.h>
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
#include <INGwTcapProvider/INGwTcapInclude.h>

#ifndef __TCAPMESSAGE
#include <INGwTcapMessage/TcapMessage.hpp>
#endif /*__TCAPMESSAGE*/
#include "stu.h"
using namespace std;

const char*
g_tcapPrintMsgType(int p_msgType)
{
	string msgName;

	switch (p_msgType) {

		case EVTSTUDATIND: {
			msgName = "EVTSTUDATIND";
			break;
		}

		case EVTSTUUDATIND: {
			msgName = "EVTSTUUDATIND";
			break;
		}

		case EVTSTUCMPIND: {
			msgName = "EVTSTUCMPIND";
			break;
		}

		case EVTSSN_CORD_CFM: {
			msgName = "EVTSSN_CORD_CFM";
			break;
		}

		case EVTSSN_CORD_IND: {
			msgName = "EVTSSN_CORD_IND";
			break;
		}

		case EVTSTUSTEIND: {
			msgName = "EVTSTUSTEIND";
			break;
		}

		case EVTSTUBNDCFM: {
			msgName = "EVTSTUBNDCFM";
			break;
		}

		default: {
			msgName = "UNKNOWN";
			break;
		}
	} // End of switch

	return (msgName.c_str());
}

U8
g_tcapGetProtocolVar(string &p_pcolVar)
{
	U8 retVal = 0;
  
	if (p_pcolVar  == "ITU88") {
					retVal = SW_ITU;
	}
	else if (p_pcolVar == "ITU92") {
					retVal = SW_ITU;
	}
	else if (p_pcolVar == "ANSI88") {
					retVal = SW_ANSI;
	}
	else if (p_pcolVar == "ANSI92") {
					retVal = SW_ANSI;
	}
	else if (p_pcolVar == "ANSI96") {
					retVal = SW_ANSI;
	}
	else if (p_pcolVar == "AT&T") {
					retVal = SW_ITU;
	}
	else if (p_pcolVar == "JAPAN_TTC") {
					retVal = SW_JAPAN;
  }
	else if (p_pcolVar == "JAPAN_NTT") {
					retVal = SW_JAPAN;
  }
	else if (p_pcolVar == "CHINA") {
					retVal = SW_CHINA;
  }
	else {
					retVal = SW_ITU;
	}

	return retVal;
}

U8
g_sccpGetProtocolVar(string &p_pcolVar)
{
	U8 retVal = 0;

	if (p_pcolVar  == "ITU") {
		retVal = SW_ITU;
	}
	else if (p_pcolVar == "ANSI") {
		retVal = SW_ANSI;
	}
	else if (p_pcolVar == "AT&T") {
		retVal = SW_ITU;
	}
	else if (p_pcolVar == "JAPAN") {
		retVal = SW_JAPAN;
	}

	return retVal;
}

//string 
//g_tcapDebugUsrInfo(M7S_UsrCfg &p_usr)
//{
//	char lBuf[2000];
//	int  lBufLen =0;
//
//	lBufLen += sprintf(lBuf + lBufLen, "M7S_UsrCfg:\n SelfIP-Port [%s][%d]\n",
//	p_usr.cmCtxCfg.selfCmAddr.ip, p_usr.cmCtxCfg.selfCmAddr.port);
//
//	lBufLen += sprintf(lBuf + lBufLen, "numDstCm: [%d]\n", p_usr.cmCtxCfg.numDstCm);
//
//	for (int i=0; i < p_usr.cmCtxCfg.numDstCm; ++i) {
//		lBufLen += sprintf(lBuf + lBufLen, "STACK IP-Port: [%s][%d]\n",
//		p_usr.cmCtxCfg.dstCmAddr[i].ip, p_usr.cmCtxCfg.dstCmAddr[i].port);
//	}
//
//	lBufLen += sprintf(lBuf + lBufLen, "numAppInst: [%d]\n", p_usr.numAppInst);
//
//  for (int i=0; i < p_usr.numAppInst; ++i) {
//
//    lBufLen += sprintf(lBuf + lBufLen, "\nApplication ID:[%d] InstanceId:[%d] \n",
//              p_usr.appInstCfg[i].appId, p_usr.appInstCfg[i].instId);
//
//    lBufLen += sprintf(lBuf + lBufLen, "Mode   : [%d]\n", p_usr.appInstCfg[i].mode);
//    lBufLen += sprintf(lBuf + lBufLen, "SSN    : [%d]\n", p_usr.appInstCfg[i].ssn);
//    lBufLen += sprintf(lBuf + lBufLen, "OPC    : [%d]\n", p_usr.appInstCfg[i].opc);
//    lBufLen += sprintf(lBuf + lBufLen, "numInst: [%d]\n", p_usr.appInstCfg[i].numInst);
//    lBufLen += sprintf(lBuf + lBufLen, "appName: [%s]\n", p_usr.appInstCfg[i].appName);
//    lBufLen += sprintf(lBuf + lBufLen, "nwApp  : [%d]\n", p_usr.appInstCfg[i].nwApp);
//    lBufLen += sprintf(lBuf + lBufLen, "TCAP Protocol Variant: [%d]\n",
//    p_usr.appInstCfg[i].pcolVar);
//    lBufLen += sprintf(lBuf + lBufLen, "SCCP Protocol Variant: [%d]\n",
//    p_usr.appInstCfg[i].spVar);
//    lBufLen += sprintf(lBuf + lBufLen, "appDlgCnt : [%d]\n", p_usr.appInstCfg[i].appDlgCnt);
//    lBufLen += sprintf(lBuf + lBufLen, "stkDlgCnt : [%d]\n",
//    p_usr.appInstCfg[i].stkDlgCnt);
//
//  } 
//
//	return (lBuf);
//}
char* 
g_tcapPrintProblemCode(unsigned char & id,unsigned char probFlg)
{

char* retVal;
/*General Problems*/
  switch(probFlg)
  {
    case STU_PROB_NONE:
      retVal = (char*)"STU_PROB_NONE";
    break;

    case STU_PROB_GENERAL:
    {  
      switch(id)
      {
        case STU_UNREC_COMP:
          retVal = (char*)"STU_UNREC_COMP";
        break;

        case STU_MISTYPED_COMP: 
          retVal = (char*)"STU_MISTYPED_COMP";
        break;

        case STU_BAD_STRUC_COMP:
          retVal = (char*)"STU_BAD_STRUC_COMP"; 
        break;
#ifdef ST_TPLUS_REQ
        case STU_EXCEED_COMP_LEN:
          retVal = (char*)"STU_EXCEED_COMP_LEN"; 
        break;
#endif        
        default:
        retVal = (char *)"UNKNOWN";  
      }
    }
    break;
    /*Invoke problems*/ 
    case STU_PROB_INVOKE:
    {
      switch(id)
      {
        case STU_DUP_INVOKE:
          retVal = (char*)"STU_DUP_INVOKE";
        break;
 
        case STU_UNREC_OPR:
          retVal = (char*)"STU_UNREC_OPR"; 
        break;

        case STU_MISTYPED_PARAM:
          retVal = (char*)"STU_MISTYPED_PARAM";  
        break;

        case STU_RESOURCE_LIMIT:
          retVal = (char*)"STU_RESOURCE_LIMIT";  
        break;

        case STU_INIT_RELEASE:
          retVal = (char*)"STU_INIT_RELEASE"; 
        break;

        case STU_UNREC_LINKED_ID:
          retVal = (char*)"STU_UNREC_LINKED_ID"; 
        break;

        case STU_LINKED_RESP_UNX:
          retVal = (char*)"STU_LINKED_RESP_UNX"; 
        break;

        case STU_UNX_LINKED_OP:
          retVal = (char*)"STU_UNX_LINKED_OP"; 
        break;
        
        default:
        retVal = (char *)"UNKNOWN";  

      }
    }
    break;
    
    case STU_PROB_RET_RES:
    {
      switch(id)
      {
        case STU_RR_UNREC_INVKID:
          retVal = (char*)"STU_RR_UNREC_INVKID"; 
        break;  

        case STU_UNX_RETRSLT:
        retVal = (char*)"STU_UNX_RETRSLT"; 
        break;  

        case STU_RR_MISTYPED_PAR:
        retVal = (char*)"STU_RR_MISTYPED_PAR"; 
        break;
        
        default:
        retVal = (char *)"UNKNOWN";   
      }
    }     
    break;
    
    case STU_PROB_RET_ERR:
    {
      switch(id)
      {
        case STU_RE_UNREC_INVKID:
          retVal = (char*)"STU_RE_UNREC_INVKID";
        break; 

        case STU_RE_UNX_RETERR:
          retVal = (char*)"STU_RE_UNX_RETERR"; 
        break; 

        case STU_UNREC_ERROR:
          retVal = (char*)"STU_UNREC_ERROR"; 
        break; 

        case STU_UNX_ERR:
          retVal = (char*)"STU_UNX_ERR";  
        break; 

        case STU_RE_MISTYPED_PAR:
          retVal = (char*)"STU_RE_MISTYPED_PAR"; 
        break;

        case STU_RSRC_UNAVAIL:
          retVal = (char*)"STU_RSRC_UNAVAIL"; 
        break;

        case STU_ENC_FAILURE:
          retVal = (char*)"STU_ENC_FAILURE"; 
        break;

        case STU_UNREC_OPCLASS:
          retVal = (char*)"STU_UNREC_OPCLASS"; 
        break;
        
        default:
        retVal = (char *)"UNKNOWN"; 
      }
    }
    break;

    default:
    retVal = (char *)"UNKNOWN";
  }
  return retVal;
}

char* 
g_tcapPrintPAbortCause(unsigned char & pAbrtCause){

  char* retVal;
  switch(pAbrtCause)
  {
    case STU_ABORT_UNREC_MSG:
      retVal = (char*)"STU_ABORT_UNREC_MSG";
    break;

    case STU_ABORT_UNREC_TRS:
      retVal = (char*)"STU_ABORT_UNREC_TRS"; 
    break;

    case STU_ABORT_BAD_FRMT:
      retVal = (char*)"STU_ABORT_BAD_FRMT"; 
    break;

    case STU_ABORT_INC_TRANS:
      retVal = (char*)"STU_ABORT_INC_TRANS"; 
    break;

    case STU_ABORT_RESOURCE:
      retVal = (char*)"STU_ABORT_RESOURCE"; 
    break;

    case STU_ABORT_ABNML_DLG:
      retVal = (char*)"STU_ABORT_ABNML_DLG"; 
    break;

    case STU_ABORT_NO_CMN_DLG:
      retVal = (char*)"STU_ABORT_NO_CMN_DLG"; 
    break;

    case STU_ABORT_UNEXP_MSG:
      retVal = (char*)"STU_ABORT_UNEXP_MSG";  
    break;

    case STU_ABORT_MISC_ERR:
      retVal = (char*)"STU_ABORT_MISC_ERR"; 
    break;

    default:
      retVal = (char *)"UNKNOWN";
  }
  return retVal;
} 

/*
char* 
g_tcapPrintError(int &id)
{
    char *retVal;

    switch(id)
    {
        case 0x00:
            retVal = (char*)"M7H_NOERROR";
            break;
        case 0x01:
            retVal = (char*)"M7H_MIN_TPORT_ERR";
        case 0x02:
            retVal = (char*)"M7H_UNIX_ERROR";
            break;
        case 0x03:
            retVal = (char*)"M7H_COMM_CHNL_ERR";
            break;
        case 0x04:
            retVal = (char*)"M7H_COMM_CHNL_DOES_NOT_EXIST";
            break;
        case 0x05:
            retVal = (char*)"M7H_COMM_CHNL_FULL";
            break;
        case 0x06:
            retVal = (char*)"M7H_COMM_CHNL_ALREADY_EXISTS";
            break;
        case 0x07:
            retVal = (char*)"M7H_COMM_CHNL_EMPTY";
            break;
        case 0x08:
            retVal = (char*)"M7H_BAD_PARAM";
            break;
        case 0x09:
            retVal = (char*)"M7H_GT_NOT_EXIST";
            break;
        case 0x0a:
            retVal = (char*)"M7H_SOCK_UNIX_ERR";
            break;
        case 0x0b:
            retVal = (char*)"M7H_CONN_RESET";
            break;
        case 0x0c:
            retVal = (char*)"M7H_NOTCONN";
            break;
        case 0x0d:
            retVal = (char*)"M7H_TIMEDOUT";
            break;
        case 0x0e:
            retVal = (char*)"M7H_SOCK_INTR";
            break;
        case 0x0f:
            retVal = (char*)"M7H_TPORT_AGAIN";
            break;
        case 0x10:
            retVal = (char*)"M7H_INVALID_PRIMITIVE_TYPE";
            break;
        case 0x11:
            retVal = (char*)"M7H_INVALID_DLG_TYPE";
            break;
        case 0x12:
            retVal = (char*)"M7H_INVALID_COMP_TYPE";
            break;
        case 0x13:
            retVal = (char*)"M7H_INVALID_SAP_ID";
            break;
        case 0x14:
            retVal = (char*)"M7H_INVALID_DLG_ID";
            break;
        case 0x15:
            retVal = (char*)"M7H_INVALID_EVENT_TYPE";
            break;
        case 0x16:
            retVal = (char*)"M7H_INVALID_PROTOCOL_TYPE";
            break;
        case 0x17:
            retVal = (char*)"M7H_NOT_INITIALISED";
            break;
        case 0x18:
            retVal = (char*)"M7H_NOT_REGISTERED";
            break;
        case 0x19:
            retVal = (char*)"M7H_BAD_PARAMETER";
            break;
        case 0x1a:
            retVal = (char*)"M7H_ALREADY_REGISTERED";
            break;
        case 0x1b:
            retVal = (char*)"M7H_MEM_ALLOC_ERR";
            break;
        case 0x1c:
            retVal = (char*)"M7H_ENV_NOTDEF";
            break;
        case 0x1d:
            retVal = (char*)"M7H_GIPC_INIT_FAILED";
            break;
        case 0x1e:
            retVal = (char*)"M7H_GIPC_CONN_FAILED";
            break;
        case 0x1f:
            retVal = (char*)"M7H_GIPC_SEND_FAILED";
            break;
        case 0x20:
            retVal = (char*)"M7H_GIPC_RECV_FAILED";
            break;
        case 0x21:
            retVal = (char*)"M7H_IPC_SELECT_FAILED";
            break;
        case 0x22:
            retVal = (char*)"M7H_TCINST_CONN_INPROG";
            break;
        case 0x23:
            retVal = (char*)"M7H_NO_FREE_DLG";
            break;
        case 0x24:
            retVal = (char*)"M7H_USR_PENDING";
            break;
       case 0x25:
            retVal = (char*)"M7H_INST_ALREADY_INIT";
            break;
        case 0x26:
            retVal = (char*)"M7H_INST_NOT_INIT";
            break;
        case 0x27:
            retVal = (char*)"M7H_CHNLTYP_NOT_SUPP";
            break;
        case 0x28:
            retVal = (char*)"M7H_NO_DATA";
            break;
        case 0x29:
            retVal = (char*)"M7H_TCAP_NT_RDY";
            break;
        case 0x2a:
            retVal = (char*)"M7H_TCAP_VALIDATION_FLD";
            break;
        case 0x2b:
            retVal = (char*)"M7H_INTRNL_ERR";
            break;
        case 0x2c:
            retVal = (char*)"M7H_LARGE_NOOFDLG";
            break;
        case 0x2d:
            retVal = (char*)"M7H_INCONSISTENT_DLGID_RNG";
           break;
        case 0x2e:
            retVal = (char*)"M7H_INCONSISTENT_NUMDLG";
            break;
        case 0x2f:
            retVal = (char*)"M7H_INCONSISTENT_NUMINST";
            break;
        case 0x30:
            retVal = (char*)"M7H_INCONSISTENT_PV";
            break;
        case 0x31:
            retVal = (char*)"M7H_INCONSISTENT_SSN";
            break;
        case 0x32:
            retVal = (char*)"M7H_INCONSISTENT_INST_DLGID_RNG";
            break;
        case 0x33:
            retVal = (char*)"M7H_NUMINST_LMT";
            break;
        case 0x34:
            retVal = (char*)"M7H_DLG_NOT_BUSY";
            break;
        case 0x35:
            retVal = (char*)"M7H_ERR_INVLD_LEN";
            break;
        case 0x36:
            retVal = (char*)"M7H_ERR_INVLD_APPID";
            break;
        case 0x37:
            retVal = (char*)"M7H_ERR_INVLD_INSTID";
            break;
        case 0x38:
            retVal = (char*)"M7H_ERR_INVLD_STA";
            break;
        case 0x39:
            retVal = (char*)"M7H_ERR_INVLD_NO_OF_DID";
            break;
        case 0x3a:
            retVal = (char*)"M7H_NO_INST";
            break;
        case 0x3b:
            retVal = (char*)"M7H_LARGE_DATASZ";
            break;
        case 0x3c:
            retVal = (char*)"M7H_OUT_OF_RNG_DID";
            break;
        case 0x3d:
            retVal = (char*)"M7H_DLG_NOT_FREE";
            break;
        case 0x3e:
            retVal = (char*)"M7H_SND_NOT_ALWD";
            break;
        case 0x40:
            retVal = (char*)"M7H_CM_FAIL";
            break;
        default:
            retVal = (char*)"UNKNOWN";
            break;
    }  
    return retVal;
}
*/      

int
g_tcapEncodeOpcSsnList(list<SccpAddr> &p_addr, unsigned char *& p_buf, int &p_len, bool p_protoType = true)
{
  LogINGwTrace(false, 0, "IN INGwTcapUtil::encodeOpcSsnList()");
  int retVal = G_SUCCESS;
//INCTBD
  //ostrstream lOss;
   printf("\nIn g_tcapEncodeOpcSsnList");
  //remove
   std::list<SccpAddr>::const_iterator iter = p_addr.begin();
   printf("\nbefore passing to TcapMessage");
     fflush(stdout);  
   for (;iter != p_addr.end(); iter++)
   {
     const SccpAddr addr = (*iter);
     printf("\nbefore passing to TcapMessage:-\nPC [Ox%08X], SSN [0x%02X]",addr.pc,addr.ssn);
     printf("\npcInd [Ox%02X],ssnInd[0x%02X]",addr.pcInd,addr.ssnInd);
     printf("\nRouting indicator [0x%02x]",addr.rtgInd);
     printf("\nProtocol sw [0x%02X]",addr.sw);
     fflush(stdout);  
   }
  int lbufLen = 0;
  TcapMessage lTcapMessage;
  p_buf = lTcapMessage.createConfiguration(p_addr, (unsigned int*)&lbufLen);
  

  //tcapconfig->marshal(lOss);
  p_len = lbufLen;
  /*
  p_len = lOss.pcount();
  p_buf = new char[lOss.pcount() + 1];
  p_buf[lOss.pcount()] = '\0';
  strncpy(p_buf, lOss.str(), lOss.pcount());
  delete [] lOss.str();
  */
  LogINGwTrace(false, 0,"OUT INGwTcapUtil::encodeOpcSsnList()");
  return retVal;
}

int
g_tcapEncodeMgmtMsg(SccpAddr &p_addr, PcSsnStatusData &p_status, 
										unsigned char *& p_buf, int &p_len, int p_flag)
{
  LogINGwTrace(false, 0, "IN INGwTcapUtil::g_tcapEncodeMgmtMsg()");
  int retVal = G_SUCCESS;

	unsigned char* msg = NULL;
	//std::ostrstream     lOss;
  SccpAddr            lDpc;

  bcopy(&p_addr, &lDpc, sizeof(SccpAddr));
  //memcopy(&lDpc, &p_addr, sizeof(SccpAddr));
  lDpc.pc = p_status.dpc;
  TcapMessage lTcapMessage;

  if (TCAP_SSN == p_flag) {
    lDpc.pc  = p_status.dpc;
    lDpc.ssn = p_status.ssn;

    if( SS_UIS == p_status.ustat|| SS_UOS == p_status.ustat )
    msg = lTcapMessage.createNstateInd(p_addr, lDpc, p_status.ustat,(unsigned int*)&p_len);
    else{
      logger.logINGwMsg(false, ERROR_FLAG,0,"SERIOUS ERROR CAN NOT SEND"
        " SSN State Indication ,Unknown Status Value <%d>",p_status.ustat);
      return G_FAILURE;
    }
  }
  else if (TCAP_PC == p_flag) {
    lDpc.ssnInd = INC_FALSE;
    msg = lTcapMessage.createNpcstateInd(p_addr, lDpc,
                            (p_status.sps == SP_ACC)?
                             SAS_SP_ACC:
                            (p_status.sps == SP_INACC)?
                             SAS_SP_INACC:
                            (p_status.sps == SP_CONG)?
                             SAS_SP_CONG: //INCTBD
                             SAS_SP_CONG_ABATEMENT,(unsigned int*)&p_len);//???
  }

  if (NULL == msg) {
    logger.logINGwMsg(false, ERROR_FLAG, 0,
    "[sendSccpMgmtInfo] Error in getting createNpcstateInd or createNstateInd");
    LogINGwTrace(false, 0, "OUT INGwTcapUtil::g_tcapEncodeMgmtMsg()");
    return retVal;
  }

  //msg->marshal(lOss);
  p_buf = msg; 
	
  LogINGwTrace(false, 0,"OUT INGwTcapUtil::g_tcapEncodeMgmtMsg()");
  return retVal;
}

void g_printHexBuff(unsigned char * p_buff, int p_len,
                  Logger::LogLevel p_logLevel, char * p_callId = NULL) 
{
  LogINGwTrace(false, 0,"IN INGwTcapUtil::g_printHexBuff()");
  if ((NULL == p_buff) || (0 == p_len)) {
    logger.logINGwMsg(false, p_logLevel, __LINE__, 0, 
           "CallId[%s] NULL/Empty Buffer passed Length[%d] bufffer[%s]",
           (p_callId)?p_callId:"NOT-PASSED", p_len, (p_buff)?"NOT-NULL":"NULL");
    LogINGwTrace(false, 0,"OUT INGwTcapUtil::g_printHexBuff()");
    return;
  }

  LogMgr::LogParam logParam;
  LogMgr::instance().getLogParam(logParam);
  
  if((Logger::ALWAYS == p_logLevel) || (logParam.logLevel == Logger::TRACE)) {
  
    char lPrintBuff[4096];
    memset(&(lPrintBuff), 0, sizeof(lPrintBuff));
    int lOffset = 0;
  
    for (int i = 0; i < p_len; i++) {
      lOffset += sprintf((lPrintBuff + lOffset), "0x%02X ", 
                         *(p_buff + i));
    }
  
    logger.logINGwMsg(false, p_logLevel, __LINE__, 0, 
                    "CallId[%s] Len:[%d] Buffer: [%s]", 
                    (p_callId)?p_callId:"NOT-PASSED", p_len, lPrintBuff);
  }
  LogINGwTrace(false, 0,"OUT INGwTcapUtil::g_printHexBuff()");
}
#endif


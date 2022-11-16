//******************************************************************************
//
//     File:     INCTags.h
//
//     Desc:     This file specifies tags - values that will be embedded in buffer
//				 that will be sent in SIP message body.	
//*******************************************************************************

#ifndef __CCPU_CPLUSPLUS
extern "C" {
#endif
  #include "envopt.h"
#ifndef __CCPU_CPLUSPLUS
}
#endif

// START- TAGS
#ifndef _INC_TAGS_
#define _INC_TAGS_

#define DLGTYPE   			      0x01 //1        
#define DLGID   			  	    0x02 //2
#define PROTOCOL_VARIANT   	  0x03 //3 
#define ORIG_SUA              0x04 //4
#define DEST_SUA              0x05 //5
#define ROUTING_INDICATOR    	0x06 //6
#define SSN                   0x07 //7
#define PC                    0x08 //8
#define INC_AFFECTED_DPC      0x09 //9
#define INC_OWN_PC            0x0A //10      
#define COMP_PRESENT   		    0x0B //11
#define GT                    0x0C //12
#define QOS   				        0x0E //14      
#define REPORT_CAUSE   		    0x0F //15       
#define ABORT_CAUSE			      0x10 //16       
#define ABORT_INFO            0x11 //17       
#define APP_CONTEXT_NAME		  0x12 //18
#define APP_CONTEXT_NAME_TYPE 0x13 //19
#define SEC_TYPE              0x14 //20
#define SEC_VALUE             0x15 //21
#define DLG_U_BUF             0x16 //22
#define DLG_CONF_INFO         0x17 //23       
#define TAG_PERMISSION        0x18 //24       
#define TAG_BILLINGID         0x19 //25       
      
/*Tags for Components*/
#define INC_REJECT_SOURCE     0x1A
#define INC_LAST              0x1B //27 To support ANSI LAST/NOT LAST
#define INC_NOT_LAST          0x1C //28 Unused Tag 
#define COMPONENT_TYPE   		  0x1D //29
#define OPERATION_TYPE   		  0x1E //30
#define OPERATION_CODE   		  0x1F //31
#define PARAM_IDENTIFIER   	  0x20 //32
#define PARAM   				      0x21 //33
#define CLASS_TYPE   			    0x22 //34
#define INVOKE_ID   			    0x23 //35
#define LINKED_ID   			    0x24 //36
#define INC_COMP_IND			    0x25 //37 
/*For Reject-Indication*/

#define PROBLEM_TYPE          0x26 //38 
#define PROBLEM_CODE          0x27 //39 
#define REJECT_TYPE           0x28 //40 
/*For Error-Indication*/
#define ERROR_TYPE            0x29 //41
#define ERROR_CODE            0x2A //42

/*GT related - never used*/
#define NAT_ADDR              0x2B //43
#define ODD_EVE_INDICATOR     0x2C //44
#define TRANSLATION_TYPE      0x2D //45
#define ENC_SCHEME            0x2E //46
#define NUM_PLAN              0x2F //47
#define GT_FORMAT             0x30 //48
#define IPADDR                0x31 //49  
#define SHRT_ADDR             0x32 //50
#define INC_USER_STATUS       0x33 //51
#define INC_AFFECTED_USER     ORIG_SUA
#define INC_OWN_ADDRESS       DEST_SUA

#define INC_SPS               INC_USER_STATUS
#define INC_LOCAL_USR_ADDR    ORIG_SUA
#define INC_SCCP_MGMT_MSG     0x34     //52
#define INC_CONFIG_ACK        0x35     //53
#define INC_AFFECTED_SSN      0x36     //54
#define INC_OWN_SSN           0x37     //55

#define DLG_PORTN_USR_INFO    0x38


// END - TAGS

/*values for INC_SCCP_STE_MSG*/
#define INC_NSTATE_IND     11
#define INC_NSTATE_REQ     11
#define INC_NPCSTATE_IND   12
#define INC_NPCSTATE_REQ   12

//General defs 
#define INC_ZERO           0x00
#define INC_TRUE	         0x01
#define INC_FALSE          INC_ZERO
#define MESSAGE_COMPLETE   INC_TRUE
#define INC_ROK            INC_ZERO   /* API invocation successful */

/*ITU dialogue Types*/
#define INC_BEGIN          STU_BEGIN      /* begin message */
#define INC_CONTINUE       STU_CONTINUE   /* continue message */
#define INC_END            STU_END        /* end message */
#define INC_U_ABORT        STU_U_ABORT    /* CCITT user abort message */
#define INC_ABORT          STU_U_ABORT 
#define INC_UNI            STU_UNI        /* unidirectional */
#define INC_NOTICE         0x72 /* Notice indication*/
/*#define INC_TC_BASIC_END   6  */  
/*#define INC_TC_PREARR_END  7  */
#define INC_P_ABORT       STU_P_ABORT 

#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
#define STU_QRY_PRM      6   /* query with permission message */
#define STU_QRY_NO_PRM   7   /* query without permission message */
#define STU_RESPONSE     8   /* response message */
#define STU_CNV_PRM      9   /* conversation with permission message */
#define STU_CNV_NO_PRM  10   /* conversation without permission message */
#define STU_ANSI_UABORT 11   /* TCAP ANSI User abort message */
#define STU_ANSI_ABORT  STU_ANSI_UABORT
#define STU_ANSI_PABORT 12   /* TCAP ANSI Protocol abort message */
#endif /* SS7_ANSXX */

/*values for INC_SCCP_STE_MSG*/
#define INC_NSTATE_IND     11
#define INC_NSTATE_REQ     11
#define INC_NPCSTATE_IND   12
#define INC_NPCSTATE_REQ   12
#define INC_CONFIG         13  

/*values of event type for different SCCP management events*/
#define EVTPC_STE_IND         0x01
#define EVTSSN_STE_REQ        0x02
#define EVTSSN_STE_IND        0x03
#define EVTSSN_CORD_REQ       0x04
#define EVTSSN_CORD_IND       0x05
#define EVTSSN_CORD_RSP       0x06
#define EVTSSN_CORD_CFM       0x07
#ifdef STU2
#define EVTSSN_STA_REQ        0x08
#define EVTSSN_STE_CFM        0x09
#define EVTSSN_STA_CFM        0x0A
#endif/* STU2 */
/*SubSystem Multiplicity Indicator*/
#define SMI_UNK      0x00
#define SMI_SOL      0x01
#define SMI_DUP      0x02
#define UOR_DENIED   0x10  /* ANSI 92/96 and BELL05 only */

/*aSsn   affected subsystem number */
#define SS_UNKNOWN      0x00  /* subsystem - Unknown */
#define SS_SCCPMNGT     0x01  /* subsystem - SCCP Management */
#define SS_ISUP         0x03  /* subsystem - ISDN User Part */
#define SS_OMAP         0x04  /* subsystem - OMAP */
#define SS_MAP          0x05  /* subsystem - Mobile Application Part */
#define SS_HLR          0x06  /* subsystem - Home Location Register */
#define SS_VLR          0x07  /* subsystem - Visiting Location Register */
#define SS_MSC          0x08  /* subsystem - Mobile Switching Center */
#define SS_EIR          0x09  /* subsystem - Equipment Ident Register */
#define SS_AC           0x0a  /* subsystem - Authentication Center */ 
#define SS_RANAP        0x8e  /* subsystem - Radio Access NetworkApplication Part */
#define SS_ISDNSUPP     0x0b  /* subsystem - ISDN supplementary services */
#define SS_BISDNE2EAPP  0x0d  /* subsystem - B-ISDN edge-to-edge  applications */
#define SS_TCTR         0x0e  /* subsystem - TC Test Responder */
#define SS_BSC_BSSAP    0x1a  /* subsystem - BSC (BSSAP-LE) */
#define SS_MSC_BSSAP    0x1b  /* subsystem - MSC (BSSAP-LE) */
#define SS_SMLC         0x1c  /* subsystem - SMLC (BSSAP-LE) */
#define SS_BSSOM        0x1d  /* subsystem - BSS O&M (A-interface) */
#define SS_BSSAP        0x1e  /* subsystem - BSSAP (A-interface) */
#define SS_GMLC         0x91  /* subsystem - GMLC(MAP) */
#define SS_CAP          0x92  /* subsystem - CAP */
#define SS_GSMSCF       0x93  /* subsystem - gsmSCF(MAP) */
#define SS_SIWF         0x94  /* subsystem - SIWF(MAP) */
#define SS_SGSN         0x95  /* subsystem - SGSN(MAP) */
#define SS_GGSN         0x96  /* subsystem - GGSN(MAP) */

/*sps 
The signalling point status is used to indicate the state of the point code.*/
#define SP_ACC      0x00   /* Signalling Point Accessible */
#define SP_INACC    0x01   /* Signalling Point Inaccessible */
#define SP_CONG     0x02   /* Signalling Point Congested */
/*sccpState
The SCCP status is used to indicate the status of the remote SCCP*/
#define RMT_SCCP_AVAIL    0x00     /* remote SCCP available */
#define RMT_SCCP_UNAVAIL  0x01     /* remote SCCP unavailable,
                                      reason unknown */
#define RMT_SCCP_UNEQP    0x02     /* remote SCCP unequipped */
#define RMT_SCCP_INACC    0x03     /* remote SCCP inaccessible */
#define RMT_SCCP_CONG     0x04     /* remote SCCP congested */

#define SPT_STATUS_PC         0x1     /* pointcode status */
#define SPT_STATUS_SS         0x2     /* subsystem status */
/*Ustat*/
#define SP_ACC      0x00   /* Signalling Point Accessible */
#define SP_INACC    0x01   /* Signalling Point Inaccessible */
#define SP_CONG     0x02   /* Signalling Point Congested */
#define SS_UOS      0x03   /* Subsystem user out of service */
#define SS_UIS      0x04   /* Subsystem user in service */

//#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
	#define INC_QRY_PRM      6   /* query with permission message */
	#define INC_QRY_NO_PRM   7   /* query without permission message */
	#define INC_RESPONSE     8   /* response message */
	#define INC_CNV_PRM      9   /* conversation with permission message */
	#define INC_CNV_NO_PRM  10   /* conversation without permission message */
	#define INC_ANSI_UABORT 11   /* TCAP ANSI User abort message */
	#define INC_ANSI_ABORT  INC_ANSI_UABORT
//#endif /* SS7_ANSXX */


/* Foe INC_COMP_TYPE TCAP component types */
#define INC_UNKNOWN     0   /* unknown component*/
#define INC_INVOKE      1   /* invoke */
#define INC_RET_RES_L   2   /* return result last */
#define INC_RET_ERR     3   /* return error */
#define INC_REJECT      4   /* reject */
#define INC_RET_RES_NL  5   /* return result not last */
#define INC_TMR_RESET   8   /* ITU-96 Reset invoke timer */

#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
#define INC_INVOKE_L    6   /* invoke last */
#define INC_INVOKE_NL   7   /* invoke not last */
#endif /* SS7_ANSXX */


/* TCAP component indications : is used mainly to distinguish different reject sources*/
#define  INC_COMP_NONE          STU_COMP_NONE      /* 0 unknown indication */
#define  INC_COMP_INVOKE        STU_COMP_INVOKE    /* 1 TC Invoke */
#define  INC_COMP_RRL           STU_COMP_RRL       /* 2 TC Result last */
#define  INC_COMP_RRNL          STU_COMP_RRNL      /* 3 TC Result not last */
#define  INC_COMP_ERROR         STU_COMP_ERROR     /* 4 TC error */
#define  INC_COMP_CANCEL        STU_COMP_CANCEL    /* 5 TC cancel */
#define  INC_COMP_REJ_USR       STU_COMP_REJ_USR   /* 6 TC user reject component */
#define  INC_COMP_REJ_LOCAL     STU_COMP_REJ_LOCAL /* 7 local reject component */
#define  INC_COMP_REJ_REMOTE    STU_COMP_REJ_REMOTE/* 8 remote reject component */
#define  INC_COMP_REJ_QLOCAL    STU_COMP_REJ_QLOCAL/* 9 local reject component - reject queued in TCAP */

/* For INC_OP_CLASS TCAP Operation Class */
#define INC_OPRCLASS1  STU_OPRCLASS1         /*1 Operation Class */
#define INC_OPRCLASS2  STU_OPRCLASS2         /*2 Operation Class */
#define INC_OPRCLASS3  STU_OPRCLASS3         /*3 Operation Class */
#define INC_OPRCLASS4  STU_OPRCLASS4         /*4 Operation Class */


/* error/operation code flags */
#define  INC_NONE     STU_NONE         /* 0 no operation code flag */
#define  INC_LOCAL    STU_LOCAL        /* 1 local error/operation code flag desired */
#define  INC_GLOBAL   STU_GLOBAL       /* 2 global error/operation code flag desired */
#define  INC_NATIONAL STU_NATIONAL     /* 3 National TCAP */
#define  INC_PRIVATE  STU_PRIVATE      /* 4 Private TCAP */

/*error code*/
#define INC_RE_UNREC_INVKID   STU_RE_UNREC_INVKID  /* 0 return error unerecognized invoke id */
#define INC_RE_UNX_RETERR     STU_RE_UNX_RETERR    /* 1 Unexpected return error */
#define INC_UNREC_ERROR       STU_UNREC_ERROR      /* 2 Unrecgnized error */
#define INC_UNX_ERR           STU_UNX_ERR          /* 3 Unexpected error */
#define INC_RE_MISTYPED_PAR   STU_RE_MISTYPED_PAR  /* 4 Mistyped parameter */ 

/* TC Problem Codes INC_PROB_CODE*/

/* problem code flags INC_PROB_CODE_FLGS*/

#define INC_PROB_NONE     STU_PROB_NONE    /* 0x00 no problem code flag */
#define INC_PROB_GENERAL  STU_PROB_GENERAL /* 0x80 general problem code flag */
#define INC_PROB_INVOKE   STU_PROB_INVOKE  /* 0x81 invoke problem code flag */
#define INC_PROB_RET_RES  STU_PROB_RET_RES /* 0x82 return result problem code flag */
#define INC_PROB_RET_ERR  STU_PROB_RET_ERR /* 0x83 return error problem code flag */

/* General Problems  */
#define INC_UNREC_COMP      STU_UNREC_COMP       /* 0 Unrecognized component */
#define INC_MISTYPED_COMP   STU_MISTYPED_COMP    /* 1 Mistyped parameter */
#define INC_BAD_STRUC_COMP  STU_BAD_STRUC_COMP   /* 2 Badly structured component */

#ifdef ST_TPLUS_REQ
#define STU_EXCEED_COMP_LEN  3  /* Component Length Exceeded */
#define INC_EXCEED_COMP_LEN STU_EXCEED_COMP_LEN  /* 3 Component Length Exceeded */
#endif

/* Invoke problems */
#define INC_DUP_INVOKE        0 /*  Duplicate Invoke Id */
#define INC_UNREC_OPR         1 /*  Unrecognized invoke Id */
#define INC_MISTYPED_PARAM    2 /*  Mistyped parameter */
#define INC_RESOURCE_LIMIT    3 /*  Resource limitation */
#define INC_INIT_RELEASE      4 /*  Initiating Release */
#define INC_UNREC_LINKED_ID   5 /*  Unrecognized linked Id */
#define INC_LINKED_RESP_UNX   6 /*  Linked response unexpected */
#define INC_UNX_LINKED_OP     7 /*  Unexpected linked operation */

/* Return result problem */
#define INC_RR_UNREC_INVKID 0   /* Unrecognized invoke Id */
#define INC_UNX_RETRSLT     1   /* Return result unexpected */
#define INC_RR_MISTYPED_PAR 2   /* Return result mistyped parameter */

/* Return Error problems */
#define INC_RE_UNREC_INVKID 0   /* return error unerecognized invoke id */
#define INC_RE_UNX_RETERR   1   /* Unexpected return error */
#define INC_UNREC_ERROR     2   /* Unrecgnized error */
#define INC_UNX_ERR         3   /* Unexpected error */
#define INC_RE_MISTYPED_PAR 4   /* Mistyped parameter */ 

/* The following reject problem codes are defined for the local Reject
   component which is generated to take care of the local protocol errors */
#define INC_RSRC_UNAVAIL   0x10 /* General - Resource not available */
#define INC_ENC_FAILURE    0x11 /* General - Component encoding failure */
#define INC_UNREC_OPCLASS  0x12 /* Invoke  - Invalid Operation class */



/* Provider-Abort Causes INC_P_ABORT_CAUSE*/
#define INC_ABORT_UNREC_MSG 0x00
#define INC_ABORT_UNREC_TRS 0x01
#define INC_ABORT_BAD_FRMT  0x02
#define INC_ABORT_INC_TRANS 0x03
#define INC_ABORT_RESOURCE  0x04
#define INC_ABORT_ABNML_DLG  0x05   /* trillium proprietary value */
#define INC_ABORT_NO_CMN_DLG 0x06   /* trillium proprietary value */
/* The following abort causes are defined for the local P-Abort message
   which is generated to take care of the local protocol errors */
#define INC_ABORT_UNEXP_MSG  0x10   /* Unexpected message type */
#define INC_ABORT_MISC_ERR   0x11   /* Misc. Errors */

/* ANSI P-Abort Causes */
#define INC_ANSI_ABORT_UP    0x01  /* unrecognized package type */
#define INC_ANSI_ABORT_IN    0x02  /* incorrect transaction portion */
#define INC_ANSI_ABORT_BD    0x03  /* badly structured transaction portion */
#define INC_ANSI_ABORT_UT    0x04  /* unrecognized transaction ID */
#define INC_ANSI_ABORT_PR    0x05  /* Permission to release problem */
#define INC_ANSI_ABORT_RN    0x06  /* resource not available */

#define INC_ANS_PABT_UR_DPID  0x07  /* Unrecognized dialogue portion id */
#define INC_ANS_PABT_BD_DLGP  0x08  /* Badly structured Dialogue portion */
#define INC_ANS_PABT_MS_DLGP  0x09  /* Missing dialogue portion */
#define INC_ANS_PABT_IC_DLGP  0x0A  /* Inconsistent dialogue portion */

/* ANSI PROBLEM CODE TYPES */
#define INC_ANSI_PRB_NU     0x00   /* not used */
#define INC_ANSI_PRB_GEN    0x01   /* general */
#define INC_ANSI_PRB_INV    0x02   /* invoke */
#define INC_ANSI_PRB_RR     0x03   /* return result */
#define INC_ANSI_PRB_RE     0x04   /* return error */
#define INC_ANSI_PRB_TRANS  0x05   /* transaction portion */
#define INC_ANSI_PRB_RSRVD  0xFF   /* all families - reserved */
/* ANSI PROBLEM CODES */
#define INC_ANSI_PRB_UR_CMP 0x01   /* general - unrecognized component */
#define INC_ANSI_PRB_IN_CMP 0x02   /* general - incorrect component portion */
#define INC_ANSI_PRB_BD_CMP 0x03   /* general - badly structured component portion */
#define INC_ANSI_PRB_IN_ENC 0x04   /* general - incorrect component encoding */

#define INC_ANSI_PRB_DUP_ID 0x01   /* invoke - duplicate invoke ID */
#define INC_ANSI_PRB_UR_OP  0x02   /* invoke - unrecognized operation code */
#define INC_ANSI_PRB_IN_PRM 0x03   /* invoke - incorrect parameter  */
#define INC_ANSI_PRB_IUR_ID 0x04   /* invoke - unrecognized correlation ID */

#define INC_ANSI_PRB_RUR_ID 0x01   /* return result - unrecognized correlation ID */
#define INC_ANSI_PRB_UX_RES 0x02   /* return result - unexpected return result */
#define INC_ANSI_PRB_INV_IN_PRM 0x03 /* return result - incorrect parameter  */

#define INC_ANSI_PRB_EUR_ID 0x01   /* return error - unrecognized correlation ID */
#define INC_ANSI_PRB_UX_RER 0x02   /* return error - unexpected return error */
#define INC_ANSI_PRB_UR_ERR 0x03   /* return error - unrecognized error */
#define INC_ANSI_PRB_UX_ERR 0x04   /* return error - unexpected error */
#define INC_ANSI_PRB_EN_PRM 0x05   /* return error - incorrect parameter */

#define INC_ANSI_PRB_UR_PKG 0x01   /* transaction portion - unrecognized package type */
#define INC_ANSI_PRB_IN_TRN 0x02   /* transaction portion - incorrect transaction portion */
#define INC_ANSI_PRB_BD_TRN 0x03   /* transaction portion - badly structured transaction portion */
#define INC_ANSI_PRB_UR_TRN 0x04   /* transaction portion - unrecognized transaction ID */
#define INC_ANSI_PRB_PR_TRN 0x05   /* transaction portion - permission to release */
#define INC_ANSI_PRB_RU_TRN 0x06   /* transaction portion - resource unavailable */

#define INC_BELL_INV_MM_PARM 0x05   /* Missing Mandatory parameter */
#define INC_BELL_RES_MM_PARM 0x04   /* Missing Mandatory parameter */
#define INC_BELL_ERR_MM_PARM 0x06   /* Missing Mandatory parameter */
/*End ANSI P-Abort Causes */

#define GTFRMT_0      0  /* Global title formt 0 */
#define GTFRMT_1      1  /* Global title formt 1 */
#define GTFRMT_2      2  /* Global title formt 2 */
#define GTFRMT_3      3  /* Global title formt 3 */
#define GTFRMT_4      4  /* Global title formt 4 */
#define GTFRMT_5      5  /* Global title formt 5 */ 

/*Numbering plan */
#define NP_UNKN      0x00   /* Unknown */
#define NP_ISDN      0x01   /* ISDN Telephony numbering plan */
#define NP_TEL       0x02   /* Telephony numbering plan - Not valid in ITU 96, ANSI 96 and BELL05 */           
#define NP_GENERIC   0x02   /* Generic numbering plan - only in ITU 96 */
#define NP_DATA      0x03      /* Data numbering plan */
#define NP_TELEX     0x04      /* Telex numbering plan */
#define NP_MARMOB    0x05      /* Maritime Mobile numbering plan */
#define NP_LANMOB    0x06      /* Land Mobile numbering plan */
#define NP_ISDNMOB   0x07      /* ISDN Mobile numbering plan */
#define NP_NWSPEC    0x0e      /* Private NW or network specific numbering plan - valid in ITU 96, ANSI96 and BELL05 */
/*encoding scheme*/
#define ES_UNKN    0x00 /* Unknown */
#define ES_BCDODD  0x01 /* BCD Odd */
#define ES_BCDEVEN 0x02 /* BCD Even */
#define ES_NWSPEC  0x03 
  /* national specific ES - allowed only 
    in ITU 96 */
    
/*nature of address*/
#define NA_SUBNUM         0x1   /* subscriber number */
#define NA_NATSIGNUM      0x3   /* national significant number */
#define NA_INTNUM         0x4   /* international number */

/*if the nifSap FALSE then the Routing Indicator must be populated with the following SUA specific values*/
#define RTE_SUA_GT       0x01    //route on global title
#define RTE_SUA_SSN_PC   0x02    //route on SSN+PC 
#define RTE_SUA_HNAME    0x03    //route on host name
#define RTE_SUA_SSN_IP   0x04    //route on SSN+IP address 

/* routing indicators for sccp */

#define INC_RTE_GT      0	/* route based on global title */
#define INC_RTE_SSN     1   /* route based on ssn */     

/* CCITT - 92, dialogue portion types */
#define INC_DLGP_NONE   0    /* unknown dialog portion type */
#define INC_DLGP_UNI    1    /* unidirectional dialog portion type */
#define INC_DLGP_REQ    2    /* request dialog portion type */
#define INC_DLGP_RSP    3    /* response dialog portion type */
#define INC_DLGP_ABT    4    /* abort dialog portion type */
//## component id
/* parameter flags INC_PARAM_ID*/
#define INC_NO_SET_SEQ  0   /* no set or sequence flag */
#define INC_SEQUENCE    1   /* sequence flag desired */
#define INC_SET      	2   /* set flag desired */   

//added for further consideration
/* Abort source for dialogue abort */
#define STU_DLG_USR_ABRT   0x00     /* Service user */
#define STU_DLG_PRV_ABRT   0x01     /* Service provider */

/* result source tags for dialog portion */
#define ST_DLG_SU_TAG      0xA1     /* dialog service user tag */
#define ST_DLG_SP_TAG      0xA2     /* dialog service provider tag */

/* dialog portion result codes */
#define ST_DLG_ACCEPTED    0x00     /* dialog has been accepted */
#define ST_DLG_REJ_PERM    0x01     /* dialog was rejected permanently */

#define ST_DLG_RSD_NULL    0x00     /* result source diagnostic: null */
#define ST_DLG_RSD_NORSN   0x01     /* result source diagnostic: no reason */
#define ST_DLG_RSD_NOACN   0x02     /* result source diagnostic: user: no acn*/
#define ST_DLG_RSD_NCDLG   0x02     /* result source diagnostic: prov: 
                                       no common dialog portion */
#define ST_DLG_REFUSED     0x03     /* Dialogue refused */

//#if (SS7_ETSI)
#define ST_ETS_DLG_REFUSED 0x03     /* Dialogue refused */

//#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
/* Ansi-96 Dialogue portion defines */

/* Ansi dialogue portion Application context/security context encoding types */
#define INC_ANS_ENC_TYP_INT  1
#define INC_ANS_ENC_TYP_OID  2
#define INC_ENC_TYP_OID  INC_ANS_ENC_TYP_OID

//#endif /* SS7_ANSXX */
//#endif

#define SW_ITU    1  /* switch - itu */
#define SW_ANSI   2  /* switch - ansi */
#define SW_CHINA  6  /* switch - china*/
#define SW_JAPAN  7  /* switch - japan*/

#define INC_UCANCEL  255

/*NOTICE INDICATION report cause*/
#define  RTC_NTBADADDR   0x00 /* No translation, address of such nature */
#define  RTC_NTSPECADDR  0x01 /* No translation, specific address */
#define  RTC_SSCONG      0x02 /* subsystem congestion */
#define  RTC_SSFAIL      0x03 /* subsystem failure */
#define  RTC_UNEQUIP     0x04 /* Unequiped User */
#define  RTC_NETFAIL     0x05 /* Network Failure */
#define  RTC_NETCONG     0x06 /* Network Congestion */
#define  RTC_UNQUAL      0x07 /* Unqualified */
#define  RTC_HOPVIOLATE  0x08 /* Hop counter violation (ANS92) */
#define  RTC_ERRMSGTPRT  0x08 /* Error in message transport ITU-T(CCITT92) */
#define  RTC_ERRLCLPROC  0x09 /* Error in local processing ITU-T(CCITT92) */
#define  RTC_NOREASSEMB  0x0a /* Dest. can not perform reassembly ITU-T(CCITT92) */
#define  RTC_SCCPFAIL    0x0b /* SCCP failure ITU-T(CCITT92) */

#endif




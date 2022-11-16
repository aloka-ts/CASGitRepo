package com.genband.tcap.parser;

public interface TagsConstant {

	//For dialogue (1-20)
	int DLGTYPE = 1 ;	
	int DLGID = 2 ;
	int PROTOCOL_VARIANT = 3 ;
	int ORIG_SUA = 4;
	int DEST_SUA = 5;
	int ROUTING_INDICATOR =  6;
	int SSN = 7 ;
	int PC = 8 ;
	int AFFECTED_DPC = 9 ;
	int OWN_PC = 10 ;
	int COMP_PRESENT = 11 ;
	int GT = 12 ;
	int QOS = 14 ;
	int REPORT_CAUSE = 15 ;
	int ABORT_CAUSE = 16 ;                        
	int ABORT_INFO = 17 ;                          
	int APP_CONTEXT_NAME= 18 ;                               
	int APP_CONTEXT_NAME_TYPE = 19 ;                 
	int SEC_TYPE = 20 ;       
	int SEC_VALUE = 21 ;                               
	int DLG_U_BUF = 22 ;                                
	int DLG_CONF_INFO = 23 ;     
	//int PREARRANGED = 24 ;
    int TC_CORR_ID =25;
	
	//For Component
	int INC_REJECT_SOURCE = 26 ;
	int INC_LAST = 27 ;		 //this is to support ansi return result last,invole last
	int INC_NOT_LAST = 28 ;		//this is to support ansi
	int COMPONENT_TYPE = 29 ;
	int OPERATION_TYPE = 30 ;
	int OPERATION_CODE = 31 ;
	int PARAM_IDENTIFIER = 32 ;
	int PARAM = 33 ;
	int CLASS_TYPE = 34 ;
	int INVOKE_ID = 35 ;
	int LINKED_ID = 36 ;
	int INC_COMP_IND  = 37 ;
	//For Reject-Indication
	int PROBLEM_TYPE = 38 ;
	int PROBLEM_CODE = 39 ;
	int REJECT_TYPE = 40 ;
	//For Error-Indication
	int ERROR_TYPE = 41 ;
	int ERROR_CODE = 42 ;
	
	int NAT_ADDR = 43 ;
	int ODD_EVE_INDICATOR = 44 ;
	int TRANSLATION_TYPE = 45 ;
	int ENC_SCHEME = 46 ;
	int NUM_PLAN = 47 ;
	int GT_FORMAT = 48 ;
	int IPADDR = 49 ; 
	int SHRT_ADDR = 50;
	int USER_STATUS = 51;
	
	
	//For SCCP Mgamt Msgs
	int INC_SCCP_MGMT_MSG = 52;
	
	
	//LENGTH
	int DLGTYPE_LEN = 1 ;	
	int DLGID_LEN = 4 ;
	int PROTOCOL_VARIANT_LEN = 1 ;
	int ORIG_SUA_LEN = -1;	//variable length
	int DEST_SUA_LEN = -1;
	int ROUTING_INDICATOR_LEN =  1;
	int SSN_LEN = 1 ;
	int PC_LEN = 4 ;
	int AFFECTED_DPC_LEN = 4 ;
	int OWN_PC_LEN = 4 ;
	//int COMP_PRESENT_LEN = -1 ;
	int GT_LEN = 1 ;
	int QOS_LEN = 1 ;
	int REPORT_CAUSE_LEN =  1;
	int ABORT_CAUSE_LEN = 1 ;                         
	int ABORT_INFO_LEN = -1 ;                      
	int APP_CONTEXT_NAME_LEN = -1 ;                             
	int APP_CONTEXT_NAME_TYPE_LEN = 1 ;                  
	int SEC_TYPE_LEN = 1 ;         
	//int SEC_VALUE_LEN = -1 ;                      
	int DLG_U_BUF_LEN = -1 ;                                
	//int DLG_CONF_INFO_LEN = -1 ; 
	
	int TC_CORR_ID_LEN=4;

	
	//For Component
	int INC_REJECT_SOURCE_LEN = 1 ;
	int INC_LAST_LEN = 1 ;		 
	int INC_NOT_LAST_LEN = 1 ;		
	int COMPONENT_TYPE_LEN = 1 ;
	int OPERATION_TYPE_LEN = 1 ;
	int OPERATION_CODE_LEN = -1 ;
	int PARAM_IDENTIFIER_LEN = 1 ;
	int PARAM_LEN = -1 ;
	int CLASS_TYPE_LEN = 1 ;
	int INVOKE_ID_LEN = 1 ;
	int LINKED_ID_LEN = 1 ;
	//int INC_COMP_IND_LEN  = -1 ;
	//For Reject-Indication
	int PROBLEM_TYPE_LEN = 1 ;
	int PROBLEM_CODE_LEN = 1 ;
	int REJECT_TYPE_LEN = 1 ;
	//For Error-Indication
	int ERROR_TYPE_LEN = 1 ;
	int ERROR_CODE_LEN = -1 ;
	
	int NAT_ADDR_LEN = 1 ;
	int ODD_EVE_INDICATOR_LEN = 1 ;
	int TRANSLATION_TYPE_LEN = 1 ;
	int ENC_SCHEME_LEN = 1 ;
	int NUM_PLAN_LEN = 1 ;
	int GT_FORMAT_LEN = 1 ;
	int IPADDR_LEN = 4 ; 
	int SHRT_ADDR_LEN = -1 ;
	int USER_STATUS_LEN = 1;
	
	int SSN_STATE_IND_MSG = 11;
	int PC_STATE_IND_MSG = 12;
	int CONF_MSG = 13;
	int MTP3_DPC=60;
	int MPT3_OPC=62;
	int PTY_NO_PC=65;
	
	int RETURN_OPTION=61;
	int SEQUENCE_CONTROL = 63;
	int MESSAGE_PRIORITY = 64;
	int PROTOCOL_VERSION = 66;
	//others 
	int PROTOCOL_VARIANT_ITU = 1;		//ITU protocol variant 3-8-3
	int PROTOCOL_VARIANT_ANSI = 2;		//ANSI protocol variant 8-8-8
	int PROTOCOL_VARIANT_CHINA = 6;		//Chinese protocol variant 3-8-3
	int PROTOCOL_VARIANT_JAPAN = 7;		//Japanese protocol variant 7-4-5
	
	//ANSI
	int ALLOWED_PERMISSION = 24;//0x18
	int ALLOWED_PERMISSION_LEN = 1;
	int MRS_RELAY = 57;
	int MRS_RELAY_LENGTH=1;
	
}

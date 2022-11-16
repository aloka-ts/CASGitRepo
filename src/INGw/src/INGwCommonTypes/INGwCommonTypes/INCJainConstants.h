//********************************************************************************
//********************************************************************************
//
//     File:     INCJainConstants.h
//		
//     Desc:     defining values as per JainTcap Constants
//				 that will be sent/received in SIP message body.	
//     
//     Author: Yogesh Tripathi  
//*********************************************************************************
//*********************************************************************************

#ifndef _JAIN_CONSTANTS_
#define _JAIN_CONSTANTS_
#define JAIN_BEGIN				  11
#define JAIN_CONTINUE			  12
#define JAIN_END				    13
#define JAIN_NOTICE				  14
#define JAIN_PROVIDER_ABORT	15
#define JAIN_UNIDIRECTIONAL	16
#define JAIN_USER_ABORT			17
#define JAIN_PREARR_END     18

#define JAIN_COMP_ERROR			    1
#define JAIN_COMP_INVOKE		    2
#define JAIN_COMP_LOCAL_CANCEL	3
#define JAIN_COMP_REJECT		    5
#define JAIN_COMP_RESULT		    7
#define JAIN_COMP_TIMER_RESET	  9
#define JAIN_COMP_USER_CANCEL	  10

/* For INC_OP_CLASS TCAP Operation Class */
#define JAIN_OPRCLASS1    1       /* Operation Class */
#define JAIN_OPRCLASS2    2       /* Operation Class */
#define JAIN_OPRCLASS3    3       /* Operation Class */
#define JAIN_OPRCLASS4    4       /* Operation Class */

/*Global Title GT*/
#define JAIN_ANSI_SPC_FORMAT			1/*SPC Format Constant*/
#define JAIN_ITU_SPC_FORMAT				2/*SPC Format Constant*/
#define JAIN_NOT_SET							/*Default Constant for the initializaltion of all integer values.*/
/*gt format*/
#define JAIN_GTINDICATOR_0000			0
#define JAIN_GTINDICATOR_0001			1
#define JAIN_GTINDICATOR_0010			2
#define JAIN_GTINDICATOR_0011			3
#define JAIN_GTINDICATOR_0100			4

/*routing indicator*/
#define JAIN_ROUTING_SUBSYSTEM			1
#define JAIN_ROUTING_GLOBALTITLE		2
/*Numbering Plan*/
#define JAIN_NP_UNKNOWN					0  
#define JAIN_NP_ISDN_TEL				1
#define JAIN_NP_GENERIC					2
#define JAIN_NP_DATA					3
#define JAIN_NP_TELEX					4
#define JAIN_NP_MARITIME_MOBILE			5
#define JAIN_NP_LAND_MOBILE				  6
/*The Numbering Plans with integer value of 8 to 13 are spare*/
#define JAIN_NP_NETWORK				14

/*Nature of Address Indicator*/
#define JAIN_NA_UNKNOWN					  0
#define JAIN_NA_SUBSCRIBER				1
#define JAIN_NA_RESERVED				  2
#define JAIN_NA_NATIONAL_SIGNIFICANT	3
/*Integer values from 5 to 127 are spare, were the eighth binary bit is used as an even/odd indicator*/
#define JAIN_NA_INTERNATIONAL			4
/*encoding scheme*/
#define JAIN_ES_UNKNOWN					  0
#define JAIN_ES_ODD						    1
#define JAIN_ES_EVEN					    2
//Indicates National Specific encoding scheme for ITU, 
//This encoding scheme is spare for ANSI, and integer values of 4 to 13 are spare for both variants.
#define JAIN_ES_NATIONAL_SPECIFIC		3

/*network indicator*/

#define JAIN_NI_INTERNATIONAL_00		0
#define JAIN_NI_INTERNATIONAL_01		1
#define JAIN_NI_NATIONAL_02				  2
#define JAIN_NI_NATIONAL_03				  3



/* error/operation code flags */

#define JAIN_OPERATIONTYPE_LOCAL	1 /*Indicates a Local(ITU) or Private(ANSI) operation type*/
#define JAIN_OPERATIONTYPE_GLOBAL	2 /*Indicates a Global(ITU) or National(ANSI) operation type*/

/*error type*/
#define JAIN_ERROR_GLOBAL	1
#define JAIN_ERROR_LOCAL	2

/* problem code flags INC_PROB_CODE_FLGS*/
#define JAIN_PROBLEM_TYPE_GENERAL		1
#define JAIN_PROBLEM_TYPE_INVOKE		2
#define JAIN_PROBLEM_TYPE_RETURN_RESULT	3
#define JAIN_PROBLEM_TYPE_RETURN_ERROR	4
#define JAIN_PROBLEM_TYPE_TRANSACTION	5
//stProbCode
/* TC Problem Codes INC_PROB_CODE*/

/* General Problems  */
#define JAIN_PRBLM_CODE_UNRECOGNISED_COMP		  1
#define JAIN_PRBLM_CODE_MISTYPED_COM			    2
#define JAIN_PRBLM_CODE_BADLY_STRUCTURED_COMP	3
#define JAIN_PRBLM_CODE_INCORRECT_COMP_CODING	4 /*Can only be used by an ANSI 1996 implementation of the JAIN TCAP API*/

/* Invoke problems */
#define JAIN_PRBLM_CODE_DUP_INVOKE_ID		  5
#define JAIN_PRBLM_CODE_UNREC_OPR			    6
#define JAIN_PRBLM_CODE_MISTYPED_PARAM	 	7
#define JAIN_PRBLM_CODE_RESOURCE_LIMIT		8
#define JAIN_PRBLM_CODE_INIT_RELEASE		  9
#define JAIN_PRBLM_CODE_UNREC_LINKED_ID		10
#define JAIN_PRBLM_CODE_LINKED_RESP_UNX		11
#define JAIN_PRBLM_CODE_UNX_LINKED_OP		  12

/* Return result problem */
#define JAIN_PRBLM_CODE_UNREC_INVOKE_ID		13
#define JAIN_PRBLM_CODE_RETURN_RESULT_UNX	14
#define JAIN_PRBLM_CODE_MISTYPED_PARAM		 7

/* Return Error problems */
#define JAIN_PRBLM_CODE_RETURN_ERROR_UNX	15
#define JAIN_PRBLM_UNRECOGNIZED_ERROR		  16
#define JAIN_PRBLM_UNEXPECTED_ERROR			  17 
#define JAIN_PRBLM_MISTYPED_PARAM			    7


/* The following reject problem codes are defined for the local Reject
   component which is generated to take care of the local protocol errors */
#define INC_RSRC_UNAVAIL   0x10 /* General - Resource not available */
#define INC_ENC_FAILURE    0x11 /* General - Component encoding failure */
#define INC_UNREC_OPCLASS  0x12 /* Invoke  - Invalid Operation class */



//PROBLEM_TYPE_TRANSACTION (ansi only, will not be used currently)

#define JAIN_PRBLM_CODE_UNREC_PACKAGE_TYPE  	18
#define JAIN_PRBLM_CODE_INCORRECT_DLG			    19
#define JAIN_PRBLM_CODE_BADLY_STRUCTURED_DLG	20	
#define JAIN_PRBLM_CODE_UNASSIGNED_RESP_ID		21	
#define JAIN_PRBLM_CODE_PERMISSION_TO_REL		  22
#define JAIN_PRBLM_CODE_RES_UNAVAIL				    23


/* Provider-Abort Causes INC_P_ABORT_CAUSE*/
#define JAIN_ABORT_UNREC_MSG 0
#define JAIN_ABORT_UNREC_TRS 1
#define JAIN_ABORT_BAD_FRMT  2
#define JAIN_ABORT_INC_TRANS 3
#define JAIN_ABORT_RESOURCE  4
#define JAIN_ABORT_ABNML_DLG 5   
#define JAIN_ABORT_UNRECOG_DLG_PORTION_ID 			  6	  //ansi
#define JAIN_ABORT_BADLY_STRUCTURED_DLG_PORTION 	7	  //ansi	
#define JAIN_ABORT_MISSING_DIALOGUE_PORTION 		  8	  //ansi
#define JAIN_ABORT_INCONSISTENT_DIALOGUE_PORTION 	9	  //ansi
#define JAIN_ABORT_PERMISSION_TO_RELEASE_PROBLEM	10	//ansi


/* CCITT - 92, dialogue portion types */
#define INC_DLGP_NONE   0    /* unknown dialog portion type */
#define INC_DLGP_UNI    1    /* unidirectional dialog portion type */
#define INC_DLGP_REQ    2    /* request dialog portion type */
#define INC_DLGP_RSP    3    /* response dialog portion type */
#define INC_DLGP_ABT    4    /* abort dialog portion type */

/* parameter flags INC_PARAM_ID*/
#define JAIN_PARAM_TYPE_SINGLE		1
#define JAIN_PARAM_TYPE_SEQUENCE	2
#define JAIN_PARAM_TYPE_SET			  3
/*
ABORT_REASON_ACN_NOT_SUPPORTED
ABORT_REASON_USER_SPECIFIC 
*/
/*return reject source*/
#define JAIN_REJECT_TYPE_LOCAL       1
#define JAIN_REJECT_TYPE_REMOTE      2
#define JAIN_REJECT_TYPE_USER        3

#define JAIN_ANSI_SPC_FMT            1       
#define JAIN_ITU_SPC_FMT             2       

#define JAIN_ACN_TYPE_INT            1
#define JAIN_ACN_TYPE_OID            2

#define SAS_UIS                1
#define SAS_UOS                0

#define SAS_SP_ACC             1
#define SAS_SP_INACC           0
#define SAS_SP_CONG            2
//INCTBD
#define SAS_SP_CONG_ABATEMENT  3

#define JAIN_ABRT_RSN_ACN_NOT_SUP  1
#define JAIN_ABRT_RSN_USR_SPECIFIC 2

#define JAIN_SEC_TYPE_INT           1
#define JAIN_SEC_TYPE_OID           2
#endif /*_JAIN_CONSTANTS_*/


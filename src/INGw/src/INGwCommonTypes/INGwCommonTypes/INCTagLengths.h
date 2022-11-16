      
/*
  Name: INCTagLengths.h
  Copyright: 
  Author: 
  Date: 08/08/11 17:18
  Description: this file is containing the length (in bytes)of the values of tags that INCtags.h is containing
*/
#ifndef _TAG_LEN_
#define _TAG_LEN_       
      
      
#define DLGTYPE_LEN   			    0x01
#define DLGID_LEN   			  	  0x04
#define PROTOCOL_VARIANT_LEN   	0x01		
#define ROUTING_INDICATOR_LEN  	0x01
#define SSN_LEN   			        0x01
#define PC_LEN   			          0x04
#define COMP_PRESENT_LEN   		  0x01
#ifndef      _QOS
      #define QOS_LEN           0x01
#else
      //define the value of QOS that is required over here 3 or 5
      #define QOS_LEN           0x05
#endif
#define REPORT_CAUSE_LEN   		  0x01
#define ABORT_CAUSE_LEN			    0x01
#define APP_CONTEXT_NAME_TYPE_LEN 0x01
#define SEC_TYPE_LEN              0x01
#define TAG_PERMISSION_LEN        0x01
#define TAG_BILLINGID_LEN         0x04
      
      //For Component
#define INC_REJECT_SOURCE_LEN     0x01
#define INC_LAST_LEN              0x01 //this is to support ansi return result last,invole last
#define INC_NOT_LAST_LEN          0x01 //this is to support ansi
#define COMPONENT_TYPE_LEN        0x01
#define OPERATION_TYPE_LEN    	  0x01
#define OPERATION_CODE_LEN    	  0x01
#define PARAM_IDENTIFIER_LEN    	0x01
#define CLASS_TYPE_LEN    		    0x01
#define INVOKE_ID_LEN    			    0x01
#define LINKED_ID_LEN    			    0x01
      //For Reject-Indication

#define PROBLEM_TYPE_LEN          0x01
#define PROBLEM_CODE_LEN          0x01
#define REJECT_TYPE_LEN           0x01
      //For Error-Indication
#define ERROR_TYPE_LEN            0x01

	  //GT related
#define NAT_ADDR_LEN            0x01
#define ODD_EVE_INDICATOR_LEN   0x01
#define TRANSLATION_TYPE_LEN    0x01
#define ENC_SCHEME_LEN          0x01
#define NUM_PLAN_LEN            0x01
#define GT_FORMAT_LEN           0x01
#define IPADDR_LEN              0x04
#define INC_USER_STATUS_LEN     0x01

#endif /*_TAG_LEN_*/

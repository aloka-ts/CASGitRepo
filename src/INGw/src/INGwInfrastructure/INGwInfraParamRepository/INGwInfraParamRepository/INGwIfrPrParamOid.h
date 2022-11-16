//     pankaj Bathwal  26/11/07     Initial Creation
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
//     File:    INGwIfrPrParamOid.h
//
//     Desc:
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     pankaj Bathwal  26/11/07     Initial Creation
//********************************************************************

#ifndef INGW_IFR_PR_PARAM_OID_H_
#define INGW_IFR_PR_PARAM_OID_H_

#ifndef INGW_BASE
#define INGW_BASE                            "32"
#endif

#ifndef ingwCOMMON_BASE
#define ingwCOMMON_BASE                        "32.1"
#endif

#ifndef ingwIS_PRIMARY
#define ingwIS_PRIMARY                         	"32.1.1"
#endif
#define ingwIS_PRIMARY_DEF                      "1"

#ifndef ingwPEER_INGW_ID
#define ingwPEER_INGW_ID                       	"32.1.2"
#endif
#define ingwPEER_INGW_ID_DEF                    "6969"

#ifndef ingwFLOATING_IP_ADDR
#define ingwFLOATING_IP_ADDR                   	"32.1.3"
#endif
#define ingwFLOATING_IP_ADDR_DEF                "192.168.1.225"

#ifndef ingwAGENT_PORT
#define ingwAGENT_PORT                         	"32.1.4"
#endif
#define ingwAGENT_PORT_DEF                         	"32.1.4"

#ifndef ingwHIGH_WATER_MARK
#define ingwHIGH_WATER_MARK                    	"32.1.5"
#endif
#define ingwHIGH_WATER_MARK_DEF                    	"32.1.5"

#ifndef ingwLOW_WATER_MARK
#define ingwLOW_WATER_MARK                     	"32.1.6"
#endif
#define ingwLOW_WATER_MARK_DEF                     	"32.1.6"

#ifndef ingwMAX_NORM_CPU_UTIL
#define ingwMAX_NORM_CPU_UTIL                  	"32.1.7"
#endif
#define ingwMAX_NORM_CPU_UTIL_DEF                  	"32.1.7"

#ifndef ingwCPU_UTIL_DELTA
#define ingwCPU_UTIL_DELTA                     	"32.1.8"
#endif
#define ingwCPU_UTIL_DELTA_DEF                     	"32.1.8"

#ifndef ingwLOG_DIR
#define ingwLOG_DIR                            	"32.1.9"
#endif
#define ingwLOG_DIR_DEF                            	"32.1.9"

#ifndef ingwGRAD_FOR_HIGH_CPU_UTIL
#define ingwGRAD_FOR_HIGH_CPU_UTIL             "32.1.10"
#endif
#define ingwGRAD_FOR_HIGH_CPU_UTIL_DEF              "32.1.10"

#ifndef ingwRESOURCE_USG_MONITORING_DUR
#define ingwRESOURCE_USG_MONITORING_DUR        "32.1.11"
#endif
#define ingwRESOURCE_USG_MONITORING_DUR_DEF          "32.1.11"

#ifndef ingwTCAP_PROVIDER_BASE
#define ingwTCAP_PROVIDER_BASE                 "32.2"
#endif
#define ingwTCAP_PROVIDER_BASE_DEF                 "32.2"

#ifndef ingwTCAP_WORKER_THREAD_COUNT
#define ingwTCAP_WORKER_THREAD_COUNT           	"32.2.1"
#endif
#define ingwTCAP_WORKER_THREAD_COUNT_DEF           	"32.2.1"

#ifndef ingwPDU_LOG_FILE
#define ingwPDU_LOG_FILE                       	"32.2.2"
#endif
#define ingwPDU_LOG_FILE_DEF                       	"32.2.2"

#ifndef ingwPDU_LOG_FILE_SIZE
#define ingwPDU_LOG_FILE_SIZE                  	"32.2.3"
#endif
#define ingwPDU_LOG_FILE_SIZE_DEF                  	"32.2.3"

#ifndef ingwPDU_LOG_LEVEL
#define ingwPDU_LOG_LEVEL                      	"32.2.4"
#endif
#define ingwPDU_LOG_LEVEL_DEF                      	"32.2.4"

#ifndef ingwSS7_APP_INFO
#define ingwSS7_APP_INFO                       	"32.2.5"
#endif
#define ingwSS7_APP_INFO_DEF                       	"32.2.5"

#ifndef ingwSELF_TCAP_IP
#define ingwSELF_TCAP_IP                       	"32.2.6"
#endif
#define ingwSELF_TCAP_IP_DEF                       	"32.2.6"

#ifndef ingwSELF_TCAP_PORT
#define ingwSELF_TCAP_PORT                     	"32.2.7"
#endif
#define ingwSELF_TCAP_PORT_DEF                     	"32.2.7"

#ifndef ingwSTACK_IP_PORT_LIST
#define ingwSTACK_IP_PORT_LIST                 	"32.2.8"
#endif
#define ingwSTACK_IP_PORT_LIST_DEF                 	"32.2.8"

#ifndef ingwNUM_REGISTRATION_RETRY
#define ingwNUM_REGISTRATION_RETRY             	"32.2.9"
#endif
#define ingwNUM_REGISTRATION_RETRY_DEF             	"32.2.9"

#ifndef ingwREGISTRATION_RETRY_TIMER
#define ingwREGISTRATION_RETRY_TIMER           	"32.2.10"
#endif
#define ingwREGISTRATION_RETRY_TIMER_DEF           	"32.2.10"

#ifndef ingwSIP_PROVIDER_BASE
#define ingwSIP_PROVIDER_BASE                  "32.3"
#endif
#define ingwSIP_PROVIDER_BASE_DEF                  "32.3"

#ifndef ingwSIP_WORKER_THREAD_COUNT
#define ingwSIP_WORKER_THREAD_COUNT            	"32.3.1"
#endif
#define ingwSIP_WORKER_THREAD_COUNT_DEF            	"32.3.1"

#ifndef ingwSIP_STACK_DEBUG_LEVEL
#define ingwSIP_STACK_DEBUG_LEVEL              	"32.3.2"
#endif
#define ingwSIP_STACK_DEBUG_LEVEL_DEF              	"32.3.2"

#ifndef ingwSIP_STACK_LISTENER_PORT
#define ingwSIP_STACK_LISTENER_PORT            	"32.3.3"
#endif
#define ingwSIP_STACK_LISTENER_PORT_DEF            	"32.3.3"

#ifndef ingwSIP_HEART_BEAT_TIMEOUT_MSEC
#define ingwSIP_HEART_BEAT_TIMEOUT_MSEC        	"32.3.4"
#endif
#define ingwSIP_HEART_BEAT_TIMEOUT_MSEC_DEF        	"32.3.4"

#ifndef ingwSIP_HEART_BEAT_TIMEOUT_MAX_COUNT
#define ingwSIP_HEART_BEAT_TIMEOUT_MAX_COUNT   	"32.3.5"
#endif
#define ingwSIP_HEART_BEAT_TIMEOUT_MAX_COUNT_DEF   	"32.3.5"

#ifndef ingwSIP_FROMINFO_USERNAME
#define ingwSIP_FROMINFO_USERNAME              	"32.3.6"
#endif
#define ingwSIP_FROMINFO_USERNAME_DEF              	"32.3.6"

#ifndef ingwSIP_CONTACTINFO_USERNAME
#define ingwSIP_CONTACTINFO_USERNAME           	"32.3.7"
#endif
#define ingwSIP_CONTACTINFO_USERNAME_DEF           	"32.3.7"

#ifndef ingwSIP_STACK_USER_PROFILE
#define ingwSIP_STACK_USER_PROFILE             	"32.3.8"
#endif
#define ingwSIP_STACK_USER_PROFILE_DEF             	"32.3.8"

#ifndef ingwSIP_HDR_COPY
#define ingwSIP_HDR_COPY                       	"32.3.9"
#endif
#define ingwSIP_HDR_COPY_DEF                       	"32.3.9"

#ifndef ingwSIP_ParserConfiguration
#define ingwSIP_ParserConfiguration            	"32.3.10"
#endif
#define ingwSIP_ParserConfiguration_DEF            	"32.3.10"

#ifndef ingwSIP_ProcessIncomingVia
#define ingwSIP_ProcessIncomingVia             		"32.3.10.1"
#endif
#define ingwSIP_ProcessIncomingVia_DEF             		"32.3.10.1"

#ifndef ingwLOAD_OID_BASE
#define ingwLOAD_OID_BASE                      "32.4"
#endif
#define ingwLOAD_OID_BASE_DEF                      "32.4"

#ifndef ingwLOAD_DIST_PATTERN
#define ingwLOAD_DIST_PATTERN                  	"32.4.1"
#endif
#define ingwLOAD_DIST_PATTERN_DEF                  	"32.4.1"

#ifndef ingwPERF_OID_BASE
#define ingwPERF_OID_BASE                      "32.5"
#endif

#ifndef INGW_TOTAL_ACTIVE_CALL
#define INGW_TOTAL_ACTIVE_CALL                 	"32.5.1"
#endif

#ifndef INGW_ACTIVE_CALL_PER_SSN
#define INGW_ACTIVE_CALL_PER_SSN               	"32.5.2"
#endif

#ifndef INGW_TOTAL_FAILED_CALL
#define INGW_TOTAL_FAILED_CALL                 	"32.5.3"
#endif

#ifndef ingwMES_OID_BASE
#define ingwMES_OID_BASE                       "32.6"
#endif

#ifndef INGW_INBOUND_DLG
#define INGW_INBOUND_DLG                       	"32.6.1"
#endif

#ifndef INGW_INBOUND_CMP
#define INGW_INBOUND_CMP                       	"32.6.2"
#endif

#ifndef INGW_INBOUND_NOT
#define INGW_INBOUND_NOT                       	"32.6.3"
#endif

#ifndef INGW_INBOUND_ABRT
#define INGW_INBOUND_ABRT                      	"32.6.4"
#endif

#ifndef INGW_INBOUND_UNI
#define INGW_INBOUND_UNI                       	"32.6.5"
#endif

#ifndef INGW_OUTBOUND_DLG
#define INGW_OUTBOUND_DLG                      	"32.6.6"
#endif

#ifndef INGW_OUTBOUND_CMP
#define INGW_OUTBOUND_CMP                      	"32.6.7"
#endif

#ifndef INGW_OUTBOUND_NOT
#define INGW_OUTBOUND_NOT                      	"32.6.8"
#endif

#ifndef INGW_OUTBOUND_ABRT
#define INGW_OUTBOUND_ABRT                     	"32.6.9"
#endif

#ifndef INGW_OUTBOUND_UNI
#define INGW_OUTBOUND_UNI                      	"32.6.10"
#endif

#ifndef INGW_INBOUND_BGN
#define INGW_INBOUND_BGN                      	"32.6.11"
#endif

#ifndef INGW_OUTBOUND_BGN
#define INGW_OUTBOUND_BGN                      	"32.6.12"
#endif

#ifndef INGW_INBOUND_CNT
#define INGW_INBOUND_CNT                      	"32.6.13"
#endif

#ifndef INGW_OUTBOUND_CNT
#define INGW_OUTBOUND_CNT                      	"32.6.14"
#endif

#ifndef INGW_INBOUND_END
#define INGW_INBOUND_END                      	"32.6.15"
#endif

#ifndef INGW_OUTBOUND_END
#define INGW_OUTBOUND_END                      	"32.6.16"
#endif

#ifndef INGW_INBOUND_PABRT
#define INGW_INBOUND_PABRT                      "32.6.17"
#endif

#ifndef INGW_TRIGR_UABRT 
#define INGW_TRIGR_UABRT                        "32.6.18"
#endif

#ifndef INGW_TRIGR_END 
#define INGW_TRIGR_END                          "32.6.19"
#endif

#endif //INGW_IFR_PR_PARAM_OID_H_

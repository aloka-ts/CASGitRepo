/************************************************************************
     Name:     INAP Stack Manager Alarm Code - defines
 
     Type:     C include file
 
     Desc:     Alarm codes needed for stack

     File:     INGwSmAlmCodes.h

     Sid:      INGwSmAlmCodes.h 0  -  03/27/03 

     Prg:      bd

************************************************************************/

#ifndef __BP_AINSMALMCODES_H__
#define __BP_AINSMALMCODES_H__

//include the various header files
#include "Util/imAlarmCodes.h"
#include "INGwSmIncludes.h"


// Alarm codes --- begin

//#define BP_AIN_SM_ALM_SW_FAIL            623   
//#define BP_AIN_SM_ALM_UNEXP_ASPDN_ACK_RX 627
//#define BP_AIN_SM_ALM_LINK_UP            LINK_UP
#define BP_AIN_SM_ALM_LINK_DOWN          1427 
#define BP_AIN_SM_ALM_LINK_UP            1428 
#define BP_AIN_SM_ALM_DPC_PAUSE          1429 
#define BP_AIN_SM_ALM_DPC_RESUME         1430
#define BP_AIN_SM_ALM_CHANNEL_DOWN       1431
#define BP_AIN_SM_ALM_CHANNEL_UP         1432

#define INC_SM_ALM_ASSOC_EST_FAILED      1435                  
#define INC_SM_ALM_ASSOC_EST_OK          1436                         
#define INC_SM_ALM_ASP_DOWN              1437                         
#define INC_SM_ALM_ASP_UP                1438                         
#define INC_SM_ALM_ASP_INACTIVE          1439                           
#define INC_SM_ALM_ASP_ACTIVE            1440
#define INC_SM_ALM_ASSOC_INHIBIT         1441      
#define INC_SM_ALM_SCT_COMM_DOWN         1442           
#define INC_SM_ALM_SCTP_COMM_OK          1443 
#define INC_SM_ALM_AS_DOWN               1444           
#define INC_SM_ALM_AS_UP                 1445             
#define INC_SM_ALM_AS_INACTIVE           1446               
#define INC_SM_ALM_AS_ACTIVE             1447
#define INC_SM_ALM_AS_PENDING            1448            
#define INC_SM_ALM_ASPAC_FAIL            1449            
#define INC_SM_ALM_ASPIA_FAIL            1450             
#define INC_SM_ALM_STATUS_IND            1452           
#define INC_SM_ALM_STK_CONFIG_INPGRS     1454
#define INC_SM_ALM_STK_CONFIG_COMP       1455
// Alarm codes --- end


// Alarm elements (used for gapping) --- begin

#define BP_AIN_SM_ALMENT_CTRLREQ           "CtrlReq"
#define BP_AIN_SM_ALMENT_INAP_DIALOGS      "InapDlgs"
#define BP_AIN_SM_ALMENT_INAP_INVOKES      "InapInvks"
#define BP_AIN_SM_ALMENT_MEM               "Mem"
#define BP_AIN_SM_ALMENT_STACK_INIT        "StackInit"
#define BP_AIN_SM_ALMENT_SW                "Software"
#define BP_AIN_SM_ALMENT_TCAP_DLGS         "TcapDlgs"
#define BP_AIN_SM_ALMENT_TCAP_INVS         "TcapInvs"
#define BP_AIN_SM_ALMENT_TUCL_CONG_START   "TuclCongStart"
#define BP_AIN_SM_ALMENT_TUCL_CONG_STOP    "TuclCongStop"
#define BP_AIN_SM_ALMENT_TUCL_CONNS        "TuclConns"

// Alarm elements (used for gapping) --- end

#endif /* __BP_AINSMALMCODES_H__ */


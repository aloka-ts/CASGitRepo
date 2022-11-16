///////////////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2001, Bay Packets Inc.
// All rights reserved.
//
// Filename   : INGwSil.h
// Description: This file contains the declaration of the INGwSil
// This file is provides a common include for all the Stack related interfaces
//
// NAME           DATE           REASON
// ----------------------------------------------------------------------------
//       31 Jul 2002    Initial Creation
//
///////////////////////////////////////////////////////////////////////////////

#ifndef BP_AIN_SIL_H
#define BP_AIN_SIL_H


/* header include files (.h) */
#ifndef __CCPU_CPLUSPLUS
extern "C" {
#endif
   
#include "envopt.h"        /* environment options */
#include "envdep.h"        /* environment dependent */
#include "envind.h"        /* environment independent */

#include "gen.h"           /* general layer */
#include "ssi.h"           /* system services */
#include "lst.h"           /* Inap layer manager interface */
#include "stu.h"           /* TCAP upper interface */

/* header/extern include files (.x) */

#include "gen.x"           /* general layer */
#include "ssi.x"           /* system services */
#include "cm_ss7.x"
#include "cm_lib.x"        /* common library functions */
#include "lst.x"           /* Inap layer manager interface */
#include "stu.x"           /* TCAP upper interface */



/* Acceptance Test Error Log */



#define EIU100             100
#define EIU101             101

#define TU_PERIOD          10     /* timer resolution */

/* local defines */

#define TU_SEL_LC       0        /* selector 0 (loosely-coupled) */
#define TU_SEL_TC       1        /* selector 1 (tightly-coupled) */
 
#define TU_ACC_INIT_WAIT  100    /* Wait time to initialize the test cases */

/* Sap Ids */
#define TU_ACC_SAP_0    0
#define TU_ACC_SAP_1    1
#define TU_ACC_SAP_2    2
#define TU_ACC_SAP_3    3
#define TU_ACC_SAP_4    4
#define TU_ACC_SAP_5    5
#define TU_ACC_SAP_6    6
#define TU_ACC_SAP_7    7

/* Resources configured in the INAP Layer */
#define TU_ACC_MAX_SAPS  8       /* Maximum number of Saps */
#define TU_ACC_MAX_DLGS 100       /* Maximum number of dialogues */

#define TU_ACC_SAP_DLGS 100       /* Maximum number of dialogues on a Sap */
#define TU_ACC_SAP_COMP  100       /* Maximum number of operations on a Sap */

/* Maximum limit on the number of dialogues and invokes */
#define TU_ACC_MAX_SEQ_DLGS   100  /* Maximum number of sequential dialogues */
#define TU_ACC_MAX_SEQ_COMPS   100  /* Maximum number of sequential operations */

/* Test Queue size, primitives received form the INAP are stored in this queue */
#define TU_ACC_MSG_QSIZE 200     /* Queue Size */

#define TU_ACC_MAX_TESTS 200     /* Maximum number of test cases */

/* Maximum number of tests for bad parameters */
#define TU_ACC_MAX_BADPARAM_TSTS  7

/* Timer values */
#define TU_ACC_REJTMR        5   /* Reject Timer */

/* Timer values - for integrated testing */
#define TU_ACC_ST_T1            10        /* Invoke Timer */
#define TU_ACC_ST_T2             0        /* Reject Timer */

/* dialogue Ids */
#define TU_ACC_START_DLGID   1     /* start of the dialogue Ids */
#define TU_ACC_DLGID_RANGE   100   /* start of the dialogue Ids */

/* Event types */
#define TU_ACC_EVT_BND_CFM  1   /* Open Confirm from INAP Layer */
#define TU_ACC_EVT_DAT_IND   2   /* Abort Indication from INAP Layer */
#define TU_ACC_EVT_UDAT_IND   3   /* Notice Indication from INAP Layer */
#define TU_ACC_EVT_CMP_CFM   4   /* State Confirm from INAP Layer */
#define TU_ACC_EVT_CMP_IND  5   /* Operation Indication from INAP Layer */
#define TU_ACC_EVT_NOT_IND  6   /* Operation Confirm from INAP Layer */
#define TU_ACC_EVT_STE_IND  7   /* Operation Confirm from INAP Layer */
#define TU_ACC_EVT_STE_CFM  8   /* Operation Confirm from INAP Layer */

#define TU_ACC_EVT_BND_REQ  21   /* Open Request to INAP Layer */
#define TU_ACC_EVT_UBND_REQ  22   /* Open Response to INAP Layer */
#define TU_ACC_EVT_DAT_REQ 23   /* Delimiter Request to INAP Layer */
#define TU_ACC_EVT_UDAT_REQ 24   /* Close Request to INAP Layer */
#define TU_ACC_EVT_CMP_REQ   25   /* Abort Request to INAP Layer */
#define TU_ACC_EVT_STA_REQ   26   /* Operation Request to INAP Layer */
#define TU_ACC_EVT_STE_REQ   26   /* Operation Request to INAP Layer */
#define TU_ACC_EVT_STE_RSP   26   /* Operation Request to INAP Layer */

#define TUACC_TRANSID1       1     /* Transaction Id */
#define TUACC_TRANSID2       2     /* Transaction Id */
#define TUACC_TRANSID3       3     /* Transaction Id */
#define TUACC_TRANSID4       4     /* Transaction Id */
#define TUACC_TRANSID5       5     /* Transaction Id */

// Dummy Operation code for Call Info To Resource
#define 	TUT_AIN_CALL_INFO_TO_RSRC  20001

#define cmCpy(s, d, c)  cmMemcpy((U8 *)d, (CONSTANT U8 *)s, c)
#define cmZero(s, c)    cmMemset((U8 *)s, 0, c)

#ifndef __CCPU_CPLUSPLUS
}
#endif

#endif



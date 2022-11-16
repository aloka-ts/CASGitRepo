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
//     File:     INGwIfrMgrWorkUnit.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal   27/11/07     Initial Creation
//********************************************************************
#ifndef _INGW_IFR_MGR_WORK_UNIT_H_
#define _INGW_IFR_MGR_WORK_UNIT_H_

#include <errno.h>
#include <libelf.h>

#ifndef USE_STD_ELF_HASH
#include "INGwInfraUtil/INGwIfrUtlHashMap.h"
#endif

class INGwIfrMgrWorkerClbkIntf;

class INGwIfrMgrWorkUnit
{
    public:

        enum INGwIfrMgrWorkUnitType {
            SIP_CALL_MSG,
            INGW_CALL_MSG,
            CCM_CALL_MSG,
            MGCP_CALL_MSG,
            SLEE_CALL_MSG,
            PEER_INGW_CALL_MSG,
            PEER_CCM_BAGT_MSG,
            TIMER_MSG,
            UPDATE_TSD_MSG,
            PERFORM_CALL_RECOVERY,
            SLEE_DOWN_MSG,
            SLEE_UP_MSG,
            SEC_CCM_DOWN_MSG,
            SEC_CCM_UP_MSG,
            SEC_CCM_TAKEOVER_MSG,
            CALL_CLEANUP_MSG,
            CALL_FORCE_CLEANUP_MSG,
            CALL_SET_SLEE_MSG,
            DIAL_OUT_REQUEST,
            HB_CALL_MSG,
            SIPPROVIDER_SENDINFO,
            CONFIG_UPDATE,
            INIT_MSG,
            INIT_DELAYED_CCM_REPLICATION_MSG,
            START_REPLICATE_MSG,
            DUMP_CALL_MSG,
            COMPONENT_NOTIFICATION,
            OTHER_MSG,
            TIMER_SELF_MSG,
            WORKER_SELF_MSG,
            PENDING_SLEERESPONSE_INFO,
            ASC_CALL_EVENT,
            ASC_CALL_PROC,
						SIP_SEND_INFO_REQ,
						SIP_SEND_NOTIFY_REQ,
						TEMP_SEND_NOTIFY,
						START_ROLE_TIMER,
						START_ROLE_RES_TIMER,
						PEER_MSG_FT_ROLE_PKT,
						LOAD_DIST_MSG,
						MSG_TCAP_MSG_INFO,
            SIP_CLEAN_CALL,
            MSG_NEW_TCAP_SESSION,
            MSG_TCAP_CLEAN_MSG_DATA,
            MSG_MODIFY_TCAP_SESSION,
            MSG_TERMINATE_TCAP_SESSION,
            SAS_HB_FAILURE,
            MSG_PEER_FAILURE,
            MSG_PEER_UP,
            SEND_NOTIFY_TO_NW,
            CLEAR_TCAP_SESSION_MAP,
            DUMP_TCAP_SESSION_MAP,
            MSG_FT_SYNCHUP,
            INIT_RETRANS_ARRAY 
        };

        INGwIfrMgrWorkUnit(void) { mpcCallId = NULL; mpMsg = NULL; }

        virtual 
        ~INGwIfrMgrWorkUnit() { if(NULL != mpcCallId) delete [] mpcCallId; }

        void 
        getHash(void) { 
#ifdef USE_STD_ELF_HASH
          mlHashId = elf_hash(mpcCallId); 
#else
          mlHashId = elf_hash_func(mpcCallId);
#endif
        }

        void
        setHash(unsigned long hash) {
          mlHashId = hash;
        }

        void 
        setWorkerThreadIdx(int aiThreadId) {
          miThreadIdx = aiThreadId;
        }

        int 
        getWorkerThreadIdx() {
          return miThreadIdx;
        }

        INGwIfrMgrWorkUnitType    meWorkType;
        unsigned long     mlHashId;
        char*             mpcCallId;
        void*             mpMsg;
        int               miThreadIdx;

        union {
             unsigned int      muiTimerId;
             unsigned long     mulMsgSize;
        };

        void*             mpContextData;
        INGwIfrMgrWorkerClbkIntf* mpWorkerClbk;

    protected:

        // NONE

    private:

        /** Assignment operator (Not implemented)
         */
        INGwIfrMgrWorkUnit&
        operator= (const INGwIfrMgrWorkUnit& arSelf);

        /** Copy constructor (Not implemented)
         */
        INGwIfrMgrWorkUnit(const INGwIfrMgrWorkUnit& arSelf);

};

#endif 

// EOF INGwIfrMgrWorkUnit.h

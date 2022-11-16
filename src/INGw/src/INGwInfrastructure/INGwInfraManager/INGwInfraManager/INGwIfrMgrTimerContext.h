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
//     File:     INGwIfrMgrTimerContext.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal   27/11/07     Initial Creation
//********************************************************************


#ifndef _INGW_IFR_MGR_TIMER_CONTEXT_H_
#define _INGW_IFR_MGR_TIMER_CONTEXT_H_

#include <string>

class INGwIfrMgrWorkerClbkIntf;

class INGwIfrMgrTimerContext
{
    public:

        enum INGwIfrMgrTimerType {
            SIP_NO_ANSWER_TIMER_MSG,
            SIP_STACK_TIMER_MSG,
            SIP_SESSION_TIMER_MSG,
            SIP_MERGE_TIMER_MSG,
            SIP_TBCT_TIMER_MSG,
            SIP_IVR_RESP_TIMER_MSG,
            MGCP_NO_ANSWER_TIMER_MSG,
            MGCP_STACK_TIMER_MSG,
            MGCP_RQNT_NTFY_TIMER_MSG,
            OTHER_MSG
        };

        INGwIfrMgrTimerContext(void) { msConnId = 0; meTimerType = OTHER_MSG; }

        virtual 
        ~INGwIfrMgrTimerContext() { }

        std::string        mCallId;
        short              msConnId;
        INGwIfrMgrTimerType        meTimerType;
        unsigned int       muiTimerId;

    protected:

        // NONE

    private:

        /** Assignment operator (Not implemented)
         */
        INGwIfrMgrTimerContext&
        operator= (const INGwIfrMgrTimerContext& arSelf);

        /** Copy constructor (Not implemented)
         */
        INGwIfrMgrTimerContext(const INGwIfrMgrTimerContext& arSelf);

};

#endif 

// EOF INGwIfrMgrTimerContext.h

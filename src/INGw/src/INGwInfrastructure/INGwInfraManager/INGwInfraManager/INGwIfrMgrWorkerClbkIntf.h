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
//     File:     INGwIfrMgrWorkerClbkIntf.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal   27/11/07     Initial Creation
//********************************************************************


#ifndef _INGW_IFR_MGR_WORKER_CLB_INTF_H_
#define _INGW_IFR_MGR_WORKER_CLB_INTF_H_
class INGwIfrMgrWorkUnit;

class INGwIfrMgrWorkerClbkIntf
{
    public:

        virtual int handleWorkerClbk(INGwIfrMgrWorkUnit* apWork) = 0;
        virtual ~INGwIfrMgrWorkerClbkIntf() { }

    protected:

        INGwIfrMgrWorkerClbkIntf() { }
        virtual void initObject(bool consFlag = false)
        {
           return;
        }

    private:

        /** Assignment operator (Not implemented)
         */
        INGwIfrMgrWorkerClbkIntf&
        operator= (const INGwIfrMgrWorkerClbkIntf& arSelf);

        /** Copy constructor (Not implemented)
         */
        INGwIfrMgrWorkerClbkIntf(const INGwIfrMgrWorkerClbkIntf& arSelf);

};

#endif 

// EOF INGwIfrMgrWorkerClbkIntf.h

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
//     File:     INGwIfrMgrFtIface.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal   27/11/07     Initial Creation
//********************************************************************

#ifndef _INGW_IFR_MGR_FT_IFACE_H_
#define _INGW_IFR_MGR_FT_IFACE_H_

class INGwFtPktMsg;

class INGwIfrMgrFtIface
{
    public :

				virtual void recvMsgFromPeerINGw(char *msgBuffer, int len,
																															int version ) = 0;

        virtual short handleINGwFailure(int p_PeerINGwId) = 0;

        virtual int getMsgDebugLevel() = 0;

        virtual void bogusFault(unsigned int p_SubsysID) = 0;

        virtual void doAction(unsigned int p_SubsysID,
                              unsigned int p_ActionID) = 0;

        virtual void ingwConnected(int p_aIngwId) = 0;
};

#endif // _INGW_IFR_MGR_FT_IFACE_H_

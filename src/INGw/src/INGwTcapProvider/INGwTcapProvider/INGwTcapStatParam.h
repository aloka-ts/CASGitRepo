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
//     File:     INGwTcapStatParam.h
//
//     Desc:     <Description of file>
//
//     Author       Date        Description
//    ----------------------------------------------------------------
//     Rajeev Arya     11/21/07     Initial Creation
//********************************************************************
#ifndef INGW_TCAP_STAT_PARAM_H_
#define INGW_TCAP_STAT_PARAM_H_

class INGwTcapStatParam
{
	public:

		static int INGW_INBOUND_DLG_INDX;
		static int INGW_INBOUND_CMP_INDX;
		static int INGW_INBOUND_NOT_INDX;
		static int INGW_INBOUND_ABRT_INDX;
		static int INGW_INBOUND_UNI_INDX;
		static int INGW_OUTBOUND_DLG_INDX;
		static int INGW_OUTBOUND_CMP_INDX;
		static int INGW_OUTBOUND_NOT_INDX;
		static int INGW_OUTBOUND_ABRT_INDX;
		static int INGW_OUTBOUND_UNI_INDX;
		static int INGW_INBOUND_BGN_INDEX;
		static int INGW_OUTBOUND_BGN_INDEX;
		static int INGW_INBOUND_CNT_INDEX;
		static int INGW_OUTBOUND_CNT_INDX;
		static int INGW_INBOUND_END_INDEX;
		static int INGW_OUTBOUND_END_INDEX;
		static int INGW_INBOUND_PABRT_INDEX;
    static int INGW_TRIGR_UABRT_INDEX;
    static int INGW_TRIGR_END_INDEX;
};
#endif

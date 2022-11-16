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
//     File:     INGwIwfBaseInterface.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#ifndef _INGW_IWF_BASE_IFACE_H_
#define _INGW_IWF_BASE_IFACE_H_


class INGwIwfBaseIface
{
	public:

		enum IfaceType {
			IWF_UNKNOWN =0,
			IWF,
			SIP,
			TCAP
		};
		
		INGwIwfBaseIface(IfaceType p_ifaceType):m_ifaceType(p_ifaceType)
		{ }

		virtual ~INGwIwfBaseIface() { }

		inline int
		getInterfaceType() {
			return m_ifaceType;
		}

	protected:

		IfaceType 	m_ifaceType;

	private:

		INGwIwfBaseIface(const INGwIwfBaseIface& p_conSelf);
		INGwIwfBaseIface& operator=(const INGwIwfBaseIface& p_conSelf);
		
};

#endif

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
//     File:     INGwIfrUtlConfigurable.h
//
//     Desc:     <Description of file>
//
//     Author     			Date     			Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#ifndef INCLUDE_INGwIfrUtlConfigurable
#define INCLUDE_INGwIfrUtlConfigurable

#include <strings.h>
#include <string>

class INGwIfrUtlConfigurable
{
    public:

        typedef enum {
            CONFIG_OP_TYPE_ADD,
            CONFIG_OP_TYPE_REMOVE,
            CONFIG_OP_TYPE_REPLACE
        } ConfigOpType;
        static const char* getString(ConfigOpType aeOpType);

        INGwIfrUtlConfigurable(void);
        virtual ~INGwIfrUtlConfigurable();

        virtual int configure(const char* apcOID, const char* apcValue, ConfigOpType aeOpType);
        virtual int oidChanged(const char* apcOID, const char* apcValue, ConfigOpType aeOpType, long alSubsystemId);
        virtual void dumpConfig(const char* apcFileName);

    protected:

        static const char* mpConfigOpTypeDesc[];

    private:

        /** Assignment operator (Not implemented)
         */
        INGwIfrUtlConfigurable&
        operator= (const INGwIfrUtlConfigurable& arSelf);

        /** Copy constructor (Not implemented)
         */
        INGwIfrUtlConfigurable(const INGwIfrUtlConfigurable& arSelf);
};

#endif 

// EOF INGwIfrUtlConfigurable.h

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
//     File:     INGwIfrMgrAgentClbkImpl.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev arya    07/12/07     Initial Creation
//********************************************************************
#ifndef INGW_IFR_SM__XML_PARSE_H
#define INGW_IFR_SM__XML_PARSE_H

#include <xercesc/dom/DOMErrorHandler.hpp>
#include <xercesc/util/XMLString.hpp>
#include <iostream.h>

XERCES_CPP_NAMESPACE_USE

// ---------------------------------------------------------------------------
//  Simple error handler deriviative to install on parser
// ---------------------------------------------------------------------------
class BpXmlErrorHandler : public DOMErrorHandler
{
public:
    // -----------------------------------------------------------------------
    //  Constructors and Destructor
    // -----------------------------------------------------------------------
    BpXmlErrorHandler();
    ~BpXmlErrorHandler();

    // -----------------------------------------------------------------------
    //  Getter methods
    // -----------------------------------------------------------------------
    bool getSawErrors() const;

    // -----------------------------------------------------------------------
    //  Implementation of the DOM ErrorHandler interface
    // -----------------------------------------------------------------------
    bool handleError(const DOMError& domError);
    void resetErrors();


private :
    // -----------------------------------------------------------------------
    //  Unimplemented constructors and operators
    // -----------------------------------------------------------------------
    BpXmlErrorHandler(const BpXmlErrorHandler&);
    void operator=(const BpXmlErrorHandler&);


    // -----------------------------------------------------------------------
    //  Private data members

    //
    //  fSawErrors
    //      This is set if we get any errors, and is queryable via a getter
    //      method. Its used by the main code to suppress output if there are
    //      errors.
    // -----------------------------------------------------------------------
    bool    fSawErrors;
};

inline bool BpXmlErrorHandler::getSawErrors() const
{
    return fSawErrors;
}

#endif

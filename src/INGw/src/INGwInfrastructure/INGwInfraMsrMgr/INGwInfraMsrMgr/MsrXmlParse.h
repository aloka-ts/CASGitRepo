#ifndef BP_XML_PARSE_H
#define BP_XML_PARSE_H

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

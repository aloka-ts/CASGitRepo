#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraMsrMgr");

#include <iostream.h>
#include <INGwInfraMsrMgr/MsrXmlParse.h>

#include <xercesc/util/PlatformUtils.hpp>
#include <xercesc/parsers/AbstractDOMParser.hpp>
#include <xercesc/dom/DOMImplementation.hpp>
#include <xercesc/dom/DOMImplementationLS.hpp>
#include <xercesc/dom/DOMImplementationRegistry.hpp>
#include <xercesc/dom/DOMBuilder.hpp>
#include <xercesc/dom/DOMException.hpp>
#include <xercesc/dom/DOMDocument.hpp>
#include <xercesc/dom/DOMNodeList.hpp>
#include <xercesc/dom/DOMError.hpp>
#include <xercesc/dom/DOMElement.hpp>
#include <xercesc/dom/DOMLocator.hpp>
#include <xercesc/dom/DOMNamedNodeMap.hpp>
#include <xercesc/dom/DOMAttr.hpp>
#include <xercesc/dom/DOMText.hpp>
#include <string.h>
#include <stdlib.h>
#include <fstream.h>

using namespace std;

BpXmlErrorHandler::BpXmlErrorHandler() :

    fSawErrors(false)
{
}

BpXmlErrorHandler::~BpXmlErrorHandler()
{
}

// ---------------------------------------------------------------------------
//  BpXmlErrorHandler interface
// ---------------------------------------------------------------------------
bool BpXmlErrorHandler::handleError(const DOMError& domError)
{
    fSawErrors = true;
    if (domError.getSeverity() == DOMError::DOM_SEVERITY_WARNING)
        cerr << "\nWarning at file ";
    else if (domError.getSeverity() == DOMError::DOM_SEVERITY_ERROR)
        cerr << "\nError at file ";
    else
        cerr << "\nFatal Error at file ";

    cerr << XMLString::transcode(domError.getLocation()->getURI())
         << ", line " << domError.getLocation()->getLineNumber()
         << ", char " << domError.getLocation()->getColumnNumber()
         << "\n  Message: " << XMLString::transcode(domError.getMessage()) << endl;

    return true;
}

void BpXmlErrorHandler::resetErrors()
{
    fSawErrors = false;
}

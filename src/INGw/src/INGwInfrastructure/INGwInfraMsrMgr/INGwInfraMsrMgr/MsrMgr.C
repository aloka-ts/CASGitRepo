#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraMsrMgr");
/************************************************************************
     Name:     Measurement Manager - implementation
 
     Type:     C implementation file
 
     Desc:     This file is needed to access the APIs for
               Measurement Manager.

     File:     MsrMgr.C

     Sid:      MsrMgr.C 0  -  06/23/03 

     Prg:      gs

************************************************************************/

#include <INGwInfraMsrMgr/MsrIncludes.h>
#include <INGwInfraMsrMgr/MsrMgr.h>
#include <INGwInfraMsrMgr/MsrWorkerThread.h>
#include <INGwInfraMsrMgr/MsrHashMap.h>
#include <INGwInfraMsrMgr/MsrUpdateMsg.h>
#include <INGwInfraMsrMgr/MsrValueMgr.h>
#include <INGwInfraMsrMgr/MsrInstant.h>
#include <sys/time.h>
#include <strings.h>
#include <string.h>

#include <xercesc/dom/DOMErrorHandler.hpp>
#include <xercesc/util/XMLString.hpp>
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
#include <xercesc/dom/DOMLocator.hpp>
#include <xercesc/dom/DOMAttr.hpp>
#include <xercesc/dom/DOMNamedNodeMap.hpp>
#include <xercesc/dom/DOMElement.hpp>
#include <xercesc/dom/DOMNodeList.hpp>

#include <INGwInfraMsrMgr/MsrXmlParse.h>
#include <fstream>

XERCES_CPP_NAMESPACE_USE

#define MSR_MAX_TAGLEN           200
#define MSR_DEFAULT_MIN_SCAN_INTERVAL     5
#define MSR_DEFAULT_MIN_ACC_INTERVAL      5

MsrMgr *MsrMgr::mpSelf = 0;


//get the attribute value for a DOM Node 
int getAttrVal (DOMNode *apNode, char *apTag, char *apVal);
    
using namespace std;


//default Constructor
MsrMgr::MsrMgr ():
miLevel (0),
miMinScanInterval (MSR_DEFAULT_MIN_SCAN_INTERVAL),
miMinAccInterval (MSR_DEFAULT_MIN_ACC_INTERVAL)
{
}


//default destructor
MsrMgr::~MsrMgr ()
{
  mpSelf = 0;
  delete mpWorkerThread;
  mpWorkerThread = 0;
  delete mpHashMap;
  mpHashMap = 0;
}

//get the instance of MsrMgr
MsrMgr* 
MsrMgr::getInstance ()
{
  if (!mpSelf)
    mpSelf = new MsrMgr;

  return mpSelf;
}

//initialize the Measurement Manager via the XML or EMS
int 
MsrMgr::initialize ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrMgr::initialize");

  MsrValueMgr::getInstance();

  mpWorkerThread = new MsrWorkerThread;
  mpWorkerThread->start();
  mpHashMap = new MsrHashMap(mpWorkerThread);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrMgr::initialize");

  return MSR_SUCCESS;
}

void
MsrMgr::dump ()
{
  mpHashMap->dump ();
}

//return the level for the Measurement Manager
int 
MsrMgr::getLevel ()
{
  return MSR_SUCCESS;
}

//update the value of the counter specified by the MeasurementID,
//EntityID, and Name/Index. In case Entity is not defined in the
//Counter then use "NULL" for the Entity.
int 
MsrMgr::setValue (std::string astrMId, std::string astrEntity,
                  std::string astrName, unsigned long aulValue)
{
  return mpHashMap->setValue (astrMId, astrEntity, astrName, aulValue);
}

//update the value of the counter specified by the hashValue
int 
MsrMgr::setValue (std::string astrMId, std::string astrEntity,
                  std::string astrName, unsigned long aulHash, 
                  unsigned long aulValue)
{
  return mpHashMap->setValue (aulHash, astrMId, astrEntity, astrName, aulValue);
}

//update the value of the counter specified by the MeasurementID,
//EntityID, and Name/Index. In case Entity is not defined in the
//Counter then use "NULL" for the Entity.
int 
MsrMgr::increment (std::string astrMId, std::string astrEntity,
                  std::string astrName, long aulValue)
{
  //if(0 == astrMId.compare("Active Call")) {
  //  logger.logINGwMsg(false, TRACE_FLAG,0,"remft increment Active Call");
  //}
  return mpHashMap->increment (astrMId, astrEntity, astrName, aulValue);
}
    
//update the value of the counter specified by the hashValue
int 
MsrMgr::increment (std::string astrMId, std::string astrEntity,
                  std::string astrName, unsigned long aulHash, 
                  long aulValue)
{
  return mpHashMap->increment (aulHash, astrMId, astrEntity, astrName, aulValue);
}

//update the value of the counter specified by the MeasurementID,
//EntityID, and Name/Index. In case Entity is not defined in the
//Counter then use "NULL" for the Entity.
int 
MsrMgr::decrement (std::string astrMId, std::string astrEntity,
                  std::string astrName, long aulValue)
{
 
  //if(0 == astrMId.compare("Active Call")) {
  //  logger.logINGwMsg(false, TRACE_FLAG,0,"remft decrement Active Call");
  //}
  return mpHashMap->decrement (astrMId, astrEntity, astrName, aulValue);
}
    
//update the value of the counter specified by the hashValue
int 
MsrMgr::decrement (std::string astrMId, std::string astrEntity,
                  std::string astrName, unsigned long aulHash, 
                  long aulValue)
{
  //if(0 == astrMId.compare("Active Call")) {
  //  logger.logINGwMsg(false, TRACE_FLAG,0,"remft decrement Active Call");
  //}
  return mpHashMap->decrement (aulHash, astrMId, astrEntity, astrName, aulValue);
}

//get the value for a counter
int 
MsrMgr::getValue (std::string astrMId, std::string astrEntity,
                  std::string astrName, unsigned long &aulValue)
{
  aulValue = mpHashMap->getValue (astrMId, astrEntity, astrName);

  return MSR_SUCCESS;
}

int 
MsrMgr::getValue (std::string astrMId, std::string astrEntity,
                  std::string astrName, unsigned long aulHash,
                  unsigned long &aulValue)
{
  aulValue = mpHashMap->getValue (aulHash, astrMId, astrEntity, astrName);

  return MSR_SUCCESS;
}

//parse the XML file
int 
MsrMgr::configureMsrMgr (std::string astrXml)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrMgr::configureMsrMgr <%s>", astrXml.c_str());

#ifndef _MSR_STUB_
  char lpcXmlFileName [100];
  struct timeval lVal;
  gettimeofday (&lVal, 0);
  snprintf (lpcXmlFileName, 100, "/tmp/confMsrMgr%ld%ld.xml",
    lVal.tv_sec, lVal.tv_usec);
  string lstrXmlFile (lpcXmlFileName);
  ofstream oFile (lstrXmlFile.c_str());

  if (oFile.fail ())
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to open <%s>", lstrXmlFile.c_str());
    return -1;
  }

  oFile << astrXml;

  oFile.close();
#else
  string lstrXmlFile = astrXml;
#endif

  AbstractDOMParser::ValSchemes valScheme = AbstractDOMParser::Val_Auto;
  bool doNamespaces   = false;
  bool doSchema     = false;
  bool schemaFullChecking = false;
  bool recognizeNEL = false;
  char       localeStr[64];

  memset(localeStr, 0, sizeof(localeStr));

  DOMBuilder *parser = 0;
  DOMDocument *doc = 0;
  DOMImplementation *impl = 0;
  bool lbAnyParsingErrors = false;

  // Initialize the XML4C system
  try
  {
    if (strlen(localeStr))
    {
      XMLPlatformUtils::Initialize(localeStr);
    }
    else
    {
      XMLPlatformUtils::Initialize();
    }

    if (recognizeNEL)
    {
      XMLPlatformUtils::recognizeNEL(recognizeNEL);
    }
  }
  catch (const XMLException& toCatch)
  {
    char *lpErrMsg = XMLString::transcode (toCatch.getMessage());

    logger.logINGwMsg (false, ERROR_FLAG, 0,
      "Error during initialization! : %s", lpErrMsg);

    XMLString::release (&lpErrMsg);

    // BPInd12747 : CCM will dump so don't proceed further
    return -1;
  }

  catch (...)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unexpected unkown exception during initialization!: %s", "No error msg available");
    lbAnyParsingErrors = true;

    // BPInd12747 : CCM will dump so don't proceed further
    return -1;
  }

  // Instantiate the DOM parser.
  static const XMLCh gLS[] = { chLatin_L, chLatin_S, chNull };
  impl = DOMImplementationRegistry::getDOMImplementation(gLS);
  parser = ((DOMImplementationLS*)impl)->createDOMBuilder(
                         DOMImplementationLS::MODE_SYNCHRONOUS, 0);

  parser->setFeature(XMLUni::fgDOMNamespaces, doNamespaces);
  parser->setFeature(XMLUni::fgXercesSchema, doSchema);
  parser->setFeature(XMLUni::fgXercesSchemaFullChecking, schemaFullChecking);

  if (valScheme == AbstractDOMParser::Val_Auto)
  {
    parser->setFeature(XMLUni::fgDOMValidateIfSchema, true);
  }
  else if (valScheme == AbstractDOMParser::Val_Never)
  {
    parser->setFeature(XMLUni::fgDOMValidation, false);
  }
  else if (valScheme == AbstractDOMParser::Val_Always)
  {
    parser->setFeature(XMLUni::fgDOMValidation, true);
  }

  // enable datatype normalization - default is off
  parser->setFeature(XMLUni::fgDOMDatatypeNormalization, true);

  // And create our error handler and install it
  BpXmlErrorHandler errorHandler;
  parser->setErrorHandler(&errorHandler);
  //
  //  Get the starting time and kick off the parse of the indicated
  //  file. Catch any exceptions that might propogate out of it.
  //
  //reset error count first 
  errorHandler.resetErrors();

  try
  {
    // reset document pool
    parser->resetDocumentPool();
  
    doc = parser->parseURI(lstrXmlFile.c_str());
  }
                         
  catch (const XMLException& toCatch)
  {
    char *lpErrMsg = XMLString::transcode (toCatch.getMessage());
    logger.logMsg (ERROR_FLAG, 0,
      "Error during parsing: <%s>, Exception message is: <%s>",
      lstrXmlFile.c_str(), lpErrMsg);
    XMLString::release (&lpErrMsg);
    lbAnyParsingErrors = true;

    // BPInd12747 : CCM will dump so don't proceed further
    return -1;
  }
  catch (const DOMException& toCatch)
  {
    const unsigned int maxChars = 2047;
    XMLCh errText[maxChars + 1];
  
    logger.logMsg (ERROR_FLAG, 0,
      "Error during parsing: <%s>, DOMException code is: <%d>",
      lstrXmlFile.c_str(), toCatch.code);

    if (DOMImplementation::loadDOMExceptionMsg(toCatch.code, errText, maxChars))
    {
      char *lpErrMsg = XMLString::transcode (errText);
      logger.logMsg (ERROR_FLAG, 0,
         "Message is: %s", lpErrMsg);
      XMLString::release (&lpErrMsg);
    }

    lbAnyParsingErrors = true;

    // BPInd12747 : CCM will dump so don't proceed further
    return -1;
  }
  catch (...)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unexpected exception during parsing: %s", lstrXmlFile.c_str());
    lbAnyParsingErrors = true;

    // BPInd12747 : CCM will dump so don't proceed further
    return -1;
  }

  //
  //  Extract the DOM tree
  //
  if (errorHandler.getSawErrors())
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Errors occurred, no output available\n");
    lbAnyParsingErrors = true;
  }

  // remove the tmp filename
  remove(lstrXmlFile.c_str());

  //get the root node
  DOMNode *lpNode = (DOMNode*) doc->getDocumentElement();

  char *lpcValue = new char [MSR_MAX_TAGLEN];

  //now the root node is used to get the MeasurementSets
  if (lpNode)
  {
    if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
    {
      DOMElement *lpElement = (DOMElement *) lpNode;

      XMLCh *lpTag = XMLString::transcode ("MeasurementMgr");
      DOMNodeList *lpNodeList = lpElement->getElementsByTagName (lpTag);
      XMLString::release (&lpTag);

      if (lpNodeList->getLength())
      {
        //this is the actual root node of the Measurement Mgr
        DOMNode *lpRootNode = lpNodeList->item(0);

        if (getAttrVal (lpRootNode, (char*) "minScanIntervalInSec", 
                lpcValue) == MSR_SUCCESS)
        {
          miMinScanInterval = atoi (lpcValue);
        }
  
        if (getAttrVal (lpRootNode, (char*) "minAccumulationIntervalInSec", 
                lpcValue) == MSR_SUCCESS)
        {
          //temporary replacement due to EMS bug
          //miMinAccInterval = atoi (lpcValue);
          miMinAccInterval = 5;
        }

        lpNode = lpRootNode->getFirstChild ();

        while (lpNode) 
        {
      
          if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
          {
            char *nodeName = XMLString::transcode (lpNode->getNodeName());
    
            //if the node is for the Counter
            if (strcmp (nodeName, "MeasurementCounter") == 0)
            {
              char lpcId[MSR_MAX_TAGLEN], lpcType [MSR_MAX_TAGLEN],
                   lpcMode [MSR_MAX_TAGLEN], lpcOid[MSR_MAX_TAGLEN], 
                   lpcInterval [MSR_MAX_TAGLEN], lpcEnable[MSR_MAX_TAGLEN];

							bzero(lpcId, MSR_MAX_TAGLEN);
							bzero(lpcType, MSR_MAX_TAGLEN);
							bzero(lpcMode, MSR_MAX_TAGLEN);
							bzero(lpcOid, MSR_MAX_TAGLEN);
							bzero(lpcInterval, MSR_MAX_TAGLEN);
							bzero(lpcEnable, MSR_MAX_TAGLEN);

              if (getAttrVal (lpNode, (char*) "id", &lpcId[0]) == MSR_SUCCESS &&
                  getAttrVal (lpNode, (char*) "type", &lpcType[0]) == MSR_SUCCESS &&
                  getAttrVal (lpNode, (char*) "mode", &lpcMode[0]) == MSR_SUCCESS &&
                  getAttrVal (lpNode, (char*) "enable", &lpcEnable[0]) 
                    == MSR_SUCCESS)
              {
                MsrUpdateMsg *lpMsg = new MsrUpdateMsg;

                lpMsg->meMsgType = MsrUpdateMsg::addCounter;
                lpMsg->upd.counter.mstrId = lpcId;
                lpMsg->upd.counter.miExecutionPriority = 0;
                lpMsg->upd.counter.miTimerInterval = 0;

                if (strncasecmp (lpcType, "incOnly", 5) == 0)
                {
                  lpMsg->upd.counter.miCounterType = MsrValueMgr::ACCUMULATED;
                }
                else if (strncasecmp (lpcType, "incDec", 5) == 0)
                {
                  lpMsg->upd.counter.miCounterType = MsrValueMgr::INSTANTANEOUS;
                }

                if (strncasecmp (lpcEnable, "false", 5) == 0)
                  lpMsg->upd.counter.miEnable = false;
                else
                  lpMsg->upd.counter.miEnable = true;

                lpMsg->upd.counter.mstrMode = lpcMode;
                if (strncasecmp (lpcMode, "event", 5) == 0)
                {
                  lpMsg->upd.counter.miTimerInterval = 0;
                  lpMsg->upd.counter.mstrOid.clear();
                }
                else if (strncasecmp (lpcMode, "usage", 5) == 0)
                {
                  if (getAttrVal (lpNode, (char*) "scanIntervalInSec", 
                        &lpcInterval[0])  != MSR_SUCCESS)
                  {
                    logger.logMsg (ERROR_FLAG, 0,
                      "ScanInterval must be provided for usage counters<%s>",
                      lpMsg->upd.counter.mstrId.c_str());
                    lpMsg->upd.counter.miTimerInterval = 
                             miMinScanInterval;
                  }
                  else
                    lpMsg->upd.counter.miTimerInterval = atoi (lpcInterval);

                  if (lpMsg->upd.counter.miTimerInterval < miMinScanInterval)
                    lpMsg->upd.counter.miTimerInterval = miMinScanInterval;

                  lpMsg->upd.counter.mstrOid.clear();
                }
                else if (strncasecmp (lpcMode, "instantaneous", 13) == 0)
                {
                  lpMsg->upd.counter.miTimerInterval = 0;
                  lpMsg->upd.counter.mstrOid.clear();
                  if (getAttrVal (lpNode, (char*) "oid", 
                              &lpcOid[0]) == MSR_SUCCESS)
                  {
                    if (strncasecmp (lpcOid, "NULL", 4) != 0)
                      lpMsg->upd.counter.mstrOid = lpcOid;
                  }
                }

                if ((lpMsg->upd.counter.mstrMode == "usage" ||
                     lpMsg->upd.counter.mstrMode == "event") &&
                    lpMsg->upd.counter.miCounterType ==
                        MsrValueMgr::INSTANTANEOUS)
                  delete lpMsg;
                else
                {
                  mpWorkerThread->postUpdateMsg (lpMsg);
                  mpHashMap->addCounterType (lpMsg->upd.counter.mstrId,
                                            lpMsg->upd.counter.miCounterType);
                }
              }
            }
    
            XMLString::release (&nodeName);
          }
          lpNode = lpNode->getNextSibling ();
        }

        lpNode = lpRootNode->getFirstChild ();

        while (lpNode)
        {

          if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
          {
            char *nodeName = XMLString::transcode (lpNode->getNodeName());

            //if the node is for the Counter
            if (strcmp (nodeName, "MeasurementSet") == 0)
            {
              char lpcId[MSR_MAX_TAGLEN],
                   lpcVersion [MSR_MAX_TAGLEN], lpcEntityType[MSR_MAX_TAGLEN],
                   lpcPriority [MSR_MAX_TAGLEN], lpcInterval [MSR_MAX_TAGLEN],
                   lpcEnable [MSR_MAX_TAGLEN];

							bzero(lpcId, MSR_MAX_TAGLEN);
							bzero(lpcInterval, MSR_MAX_TAGLEN);
							bzero(lpcEnable, MSR_MAX_TAGLEN);
							bzero(lpcVersion, MSR_MAX_TAGLEN);
							bzero(lpcEntityType, MSR_MAX_TAGLEN);
							bzero(lpcPriority, MSR_MAX_TAGLEN);

              if (getAttrVal (lpNode, (char*) "id", &lpcId[0]) == MSR_SUCCESS &&
                  getAttrVal (lpNode, (char*) "version", &lpcVersion[0]) == MSR_SUCCESS &&
                  getAttrVal (lpNode, (char*) "entityType", &lpcEntityType[0]) == MSR_SUCCESS &&
                  getAttrVal (lpNode, (char*) "priority", &lpcPriority[0]) == MSR_SUCCESS &&
                  getAttrVal (lpNode, (char*) "enable", &lpcEnable[0]) == MSR_SUCCESS &&
                  getAttrVal (lpNode, (char*) "accumulationIntervalInSec", &lpcInterval[0])
                    == MSR_SUCCESS)
              {
                MsrUpdateMsg *lpMsg = new MsrUpdateMsg;

                lpMsg->meMsgType = MsrUpdateMsg::addSet;
                lpMsg->upd.set.mstrVersion = lpcVersion;
                lpMsg->upd.set.mstrId = lpcId;
                lpMsg->upd.set.miExecutionPriority = atoi(lpcPriority);
                lpMsg->upd.set.mstrEntityType = lpcEntityType;
                lpMsg->upd.set.miMaxSetsInQueue = 10000;

                lpMsg->upd.set.miTimerInterval = atoi(lpcInterval);
                if (lpMsg->upd.set.miTimerInterval < miMinAccInterval)
                  lpMsg->upd.set.miTimerInterval = miMinAccInterval;

                if (strncasecmp (lpcEnable, "false", 5) == 0)
                  lpMsg->upd.set.miEnable = false;
                else
                  lpMsg->upd.set.miEnable = true;


                if (getAttrVal (lpNode, (char*) "resetFlag", &lpcValue[0])
                    == MSR_SUCCESS)
                {
                  if (strncasecmp (lpcValue, "false", 5) == 0)
                  lpMsg->upd.set.mbReset = false;
                else
                  lpMsg->upd.set.mbReset = true;
                }
                else
                  lpMsg->upd.set.mbReset = true;

                lpTag = XMLString::transcode ("MeasurementCounter");
                lpNodeList = 
                           ((DOMElement*) lpNode)->getElementsByTagName (lpTag);
                XMLString::release (&lpTag);

                for (int liCount = 0; liCount < lpNodeList->getLength(); 
                     liCount++)
                {
                  DOMNode *lpChild = lpNodeList->item (liCount);
                  if (lpChild &&
                      lpChild->getNodeType() == DOMNode::ELEMENT_NODE)
                  {
                    char *childName = XMLString::transcode 
                                             (lpChild->getNodeName());
                    if (strncasecmp (childName, (char*) "MeasurementCounter", 18) == 0)
                    {
                      if (getAttrVal (lpChild, (char*) "id", lpcValue) == MSR_SUCCESS)
                        lpMsg->upd.set.mCounterList.push_back (lpcValue);
                    }
                    XMLString::release (&childName);
                  }
                }

                mpWorkerThread->postUpdateMsg (lpMsg);
              }
            }

            XMLString::release (&nodeName);
          }
          lpNode = lpNode->getNextSibling ();
        }

      }
    }
  }

  delete [] lpcValue;

  //    
  //  Delete the parser itself.  Must be done prior to calling Terminate, below.
  //
  if (parser)
    parser->release();
  parser = 0;

  // And call the termination method
  XMLPlatformUtils::Terminate();

  //Measurement Counters for INGw starts

  setValue("Active Call" , "INGw", "Active Call", 0); 

  setValue("Total Message Rx" , "INGw", "Total Message Rx", 0); 
  setValue("Total Message Tx" , "INGw", "Total Message Tx", 0); 

  setValue("Total Active Transaction" , "INGw", "Total Active Transaction", 0); 

  setValue("Total Begin Rx" , "INGw", "Total Begin Rx", 0); 
  setValue("Total Begin Tx" , "INGw", "Total Begin Tx", 0); 

  setValue("Total Continue Rx" , "INGw", "Total Continue Rx", 0); 
  setValue("Total Continue Tx" , "INGw", "Total Continue Tx", 0); 

  setValue("Total End Rx" , "INGw", "Total End Rx", 0); 
  setValue("Total End Tx" , "INGw", "Total End Tx", 0); 

  setValue("Total QWP Rx" , "INGw", "Total QWP Rx", 0); 
  setValue("Total QWP Tx" , "INGw", "Total QWP Tx", 0); 

  setValue("Total QWoP Rx" , "INGw", "Total QWoP Rx", 0); 
  setValue("Total QWoP Tx" , "INGw", "Total QWoP Tx", 0); 

  setValue("Total CWP Rx" , "INGw", "Total CWP Rx", 0); 
  setValue("Total CWP Tx" , "INGw", "Total CWP Tx", 0); 

  setValue("Total CWoP Rx" , "INGw", "Total CWoP Rx", 0); 
  setValue("Total CWoP Tx" , "INGw", "Total CWoP Tx", 0); 

  setValue("Total Response Rx" , "INGw", "Total Response Rx", 0); 
  setValue("Total Response Tx" , "INGw", "Total Response Tx", 0); 

  setValue("Total Unidirectional Rx" , "INGw", "Total Unidirectional Rx", 0); 
  setValue("Total Unidirectional Tx" , "INGw", "Total Unidirectional Tx", 0); 

  setValue("Total Abort Rx" , "INGw", "Total Abort Rx", 0); 
  setValue("Total Abort Tx" , "INGw", "Total Abort Tx", 0); 

  setValue("Total Component Rx" , "INGw", "Total Component Rx", 0); 
  setValue("Total Component Tx" , "INGw", "Total Component Tx", 0); 

  setValue("Total ReturnResult Rx" , "INGw", "Total ReturnResult Rx", 0); 
  setValue("Total ReturnResult Tx" , "INGw", "Total ReturnResult Tx", 0); 

  setValue("Total Reject Rx" , "INGw", "Total Reject Rx", 0); 
  setValue("Total Reject Tx" , "INGw", "Total Reject Tx", 0); 

  setValue("Total ReturnError Rx" , "INGw", "Total ReturnError Rx", 0); 
  setValue("Total ReturnError Tx" , "INGw", "Total ReturnError Tx", 0); 

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrMgr::configureMsrMgr");

  return 0;
}

int
MsrMgr::reconfigureMsrMgr (std::string astrXml)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrMgr::reconfigureMsrMgr <%s> ",
    astrXml.c_str());

#ifndef _MSR_STUB_
  char lpcXmlFileName [100];
  struct timeval lVal;
  gettimeofday (&lVal, 0);
  snprintf (lpcXmlFileName, 100, "/tmp/reconfMsrMgr%ld%ld.xml",
    lVal.tv_sec, lVal.tv_usec);
  string lstrXmlFile (lpcXmlFileName);

  ofstream oFile (lstrXmlFile.c_str());

  if (oFile.fail ())
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to open <%s>", lstrXmlFile.c_str());
    return -1;
  }

  oFile << astrXml;

  oFile.close();
#else
  string lstrXmlFile = astrXml;
#endif

  AbstractDOMParser::ValSchemes valScheme = AbstractDOMParser::Val_Auto;
  bool doNamespaces   = false;
  bool doSchema     = false;
  bool schemaFullChecking = false;
  bool recognizeNEL = false;
  char       localeStr[64];

  memset(localeStr, 0, sizeof(localeStr));

  DOMBuilder *parser = 0;
  DOMDocument *doc = 0;
  DOMImplementation *impl = 0;
  bool lbAnyParsingErrors = false;

  // Initialize the XML4C system
  try
  {
    if (strlen(localeStr))
    {
      XMLPlatformUtils::Initialize(localeStr);
    }
    else
    {
      XMLPlatformUtils::Initialize();
    }

    if (recognizeNEL)
    {
      XMLPlatformUtils::recognizeNEL(recognizeNEL);
    }
  }
  catch (const XMLException& toCatch)
  {
    char *lpErrMsg = XMLString::transcode (toCatch.getMessage());

    logger.logINGwMsg (false, ERROR_FLAG, 0,
      "Error during initialization! : %s", lpErrMsg);

    XMLString::release (&lpErrMsg);

    // BPInd12747 : CCM will dump so don't proceed further
    return -1;
  }
  catch (...)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unexpected unkown exception during initialization!: %s", "No error msg available");
    lbAnyParsingErrors = true;

    // BPInd12747 : CCM will dump so don't proceed further
    return -1;
  }

  // Instantiate the DOM parser.
  static const XMLCh gLS[] = { chLatin_L, chLatin_S, chNull };
  impl = DOMImplementationRegistry::getDOMImplementation(gLS);
  parser = ((DOMImplementationLS*)impl)->createDOMBuilder(
                         DOMImplementationLS::MODE_SYNCHRONOUS, 0);

  parser->setFeature(XMLUni::fgDOMNamespaces, doNamespaces);
  parser->setFeature(XMLUni::fgXercesSchema, doSchema);
  parser->setFeature(XMLUni::fgXercesSchemaFullChecking, schemaFullChecking);

  if (valScheme == AbstractDOMParser::Val_Auto)
  {
    parser->setFeature(XMLUni::fgDOMValidateIfSchema, true);
  }
  else if (valScheme == AbstractDOMParser::Val_Never)
  {
    parser->setFeature(XMLUni::fgDOMValidation, false);
  }
  else if (valScheme == AbstractDOMParser::Val_Always)
  {
    parser->setFeature(XMLUni::fgDOMValidation, true);
  }

  // enable datatype normalization - default is off
  parser->setFeature(XMLUni::fgDOMDatatypeNormalization, true);

  // And create our error handler and install it
  BpXmlErrorHandler errorHandler;
  parser->setErrorHandler(&errorHandler);
  //
  //  Get the starting time and kick off the parse of the indicated
  //  file. Catch any exceptions that might propogate out of it.
  //
  //reset error count first 
  errorHandler.resetErrors();

  try
  {
    // reset document pool
    parser->resetDocumentPool();
  
    doc = parser->parseURI(lstrXmlFile.c_str());
  }
                         
  catch (const XMLException& toCatch)
  {
    char *lpErrMsg = XMLString::transcode (toCatch.getMessage());
    logger.logMsg (ERROR_FLAG, 0,
      "Error during parsing: <%s>, Exception message is: <%s>",
      lstrXmlFile.c_str(), lpErrMsg);
    XMLString::release (&lpErrMsg);
    lbAnyParsingErrors = true;

    // BPInd12747 : CCM will dump so don't proceed further
    return -1;
  }
  catch (const DOMException& toCatch)
  {
    const unsigned int maxChars = 2047;
    XMLCh errText[maxChars + 1];
  
    logger.logMsg (ERROR_FLAG, 0,
      "Error during parsing: <%s>, DOMException code is: <%d>",
      lstrXmlFile.c_str(), toCatch.code);

    if (DOMImplementation::loadDOMExceptionMsg(toCatch.code, errText, maxChars))
    {
      char *lpErrMsg = XMLString::transcode (errText);
      logger.logMsg (ERROR_FLAG, 0,
         "Message is: %s", lpErrMsg);
      XMLString::release (&lpErrMsg);
    }

    lbAnyParsingErrors = true;

    // BPInd12747 : CCM will dump so don't proceed further
    return -1;

  }
  catch (...)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unexpected exception during parsing: %s", lstrXmlFile.c_str());
    lbAnyParsingErrors = true;

    // BPInd12747 : CCM will dump so don't proceed further
    return -1;
  }

  //
  //  Extract the DOM tree
  //
  if (errorHandler.getSawErrors())
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Errors occurred, no output available\n");
    lbAnyParsingErrors = true;
  }
  // remove the tmp filename
  remove(lstrXmlFile.c_str());

  //get the root node
  DOMNode *lpNode = (DOMNode*) doc->getDocumentElement();

  char *lpcValue = new char [MSR_MAX_TAGLEN];

  //now the root node is used to get the MeasurementSets
  if (lpNode)
  {
    if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
    {
      DOMElement *lpElement = (DOMElement *) lpNode;

      XMLCh *lpTag = XMLString::transcode ("MeasurementMgr");
      DOMNodeList *lpNodeList = lpElement->getElementsByTagName (lpTag);
      XMLString::release (&lpTag);

      if (lpNodeList->getLength())
      {
        //this is the actual root node of the Measurement Mgr
        DOMNode *lpRootNode = lpNodeList->item(0);
        lpNode = lpRootNode->getFirstChild ();

        while (lpNode)
        {

          if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
          {
            char *nodeName = XMLString::transcode (lpNode->getNodeName());

            //if the node is for the Set
            if (strcmp (nodeName, "MeasurementSet") == 0)
            {
              if (getAttrVal (lpNode, (char*) "operation", lpcValue) == MSR_SUCCESS)
              {
                MsrUpdateMsg *lpMsg = new MsrUpdateMsg;
                char lpcId[MSR_MAX_TAGLEN],
                   lpcVersion [MSR_MAX_TAGLEN], lpcEntityType[MSR_MAX_TAGLEN],
                   lpcPriority [MSR_MAX_TAGLEN], lpcInterval [MSR_MAX_TAGLEN],
                   lpcEnable [MSR_MAX_TAGLEN];

							bzero(lpcId, MSR_MAX_TAGLEN);
							bzero(lpcInterval, MSR_MAX_TAGLEN);
							bzero(lpcEnable, MSR_MAX_TAGLEN);
							bzero(lpcVersion, MSR_MAX_TAGLEN);
							bzero(lpcEntityType, MSR_MAX_TAGLEN);
							bzero(lpcPriority, MSR_MAX_TAGLEN);

                bool lbToSend = false;

                if (strncasecmp (lpcValue, (char*) "add", 3) == 0)
                {
                  lpMsg->meMsgType = MsrUpdateMsg::addSet;
                  if (getAttrVal (lpNode, (char*) "id", &lpcId[0]) == MSR_SUCCESS &&
                    getAttrVal (lpNode, (char*) "version", &lpcVersion[0]) == MSR_SUCCESS &&        
                    getAttrVal (lpNode, (char*) "entityType", &lpcEntityType[0]) == MSR_SUCCESS &&
                    getAttrVal (lpNode, (char*) "priority", &lpcPriority[0]) == MSR_SUCCESS &&
                    getAttrVal (lpNode, (char*) "enable", &lpcEnable[0]) == MSR_SUCCESS &&
                    getAttrVal (lpNode, (char*) "accumulationIntervalInSec", &lpcInterval[0])
                              == MSR_SUCCESS)
                  {
                    lbToSend = true;
                    lpMsg->upd.set.miExecutionPriority = atoi(lpcPriority);

                    lpMsg->upd.set.miTimerInterval = atoi(lpcInterval);
                    if (lpMsg->upd.set.miTimerInterval < miMinAccInterval) 
                      lpMsg->upd.set.miTimerInterval = miMinAccInterval;

                    lpMsg->upd.set.mstrEntityType = lpcEntityType;

                    if (strncasecmp (lpcEnable, "false", 5) == 0)
                      lpMsg->upd.set.miEnable = false;
                    else
                      lpMsg->upd.set.miEnable = true;

                    lpMsg->upd.set.mstrVersion = lpcVersion;
                    lpMsg->upd.set.mstrId = lpcId;
                    if (getAttrVal (lpNode, (char*) "resetFlag", &lpcValue[0])
                        == MSR_SUCCESS)
                    {
                      if (strncasecmp (lpcValue, "false", 5) == 0)
                        lpMsg->upd.set.mbReset = false;
                      else
                        lpMsg->upd.set.mbReset = true;
                    }
                    else
                      lpMsg->upd.set.mbReset = true;

                  }
                }
                else if (strncasecmp (lpcValue, "delete", 6) == 0)
                {
                  lpMsg->meMsgType = MsrUpdateMsg::deleteSet;
                  if (getAttrVal (lpNode, (char*) "id", &lpcId[0]) == MSR_SUCCESS &&
                    getAttrVal (lpNode, (char*) "version", &lpcVersion[0]) == MSR_SUCCESS)
                  {
                    lbToSend = true;
                    lpMsg->upd.set.mstrVersion = lpcVersion;
                    lpMsg->upd.set.mstrId = lpcId;
                  }
                }
                else if (strncasecmp (lpcValue, "update", 6) == 0)
                {
                  lpMsg->meMsgType = MsrUpdateMsg::reconfigSet;
                  if (getAttrVal (lpNode, (char*) "id", &lpcId[0]) == MSR_SUCCESS)
                  {           
                    lpMsg->upd.recfg.set.mstrId = lpcId;
                    lpMsg->upd.recfg.set.miExecutionPriority = -1;
                    lpMsg->upd.recfg.set.miTimerInterval = -1;
                    lpMsg->upd.recfg.set.miEnable = -1;
                    lpMsg->upd.recfg.set.mstrVersion.clear();

                    if (getAttrVal (lpNode, (char*) "priority", 
                          &lpcPriority[0]) == MSR_SUCCESS)
                    {
                      lpMsg->upd.recfg.set.miExecutionPriority = 
                                       atoi(lpcPriority);
                      lbToSend = true;
                    }

                    if (getAttrVal (lpNode, (char*) 
                        "accumulationIntervalInSec", &lpcInterval[0])
                              == MSR_SUCCESS)
                    {
                      lpMsg->upd.recfg.set.miTimerInterval = atoi(lpcInterval);
                      if (lpMsg->upd.recfg.set.miTimerInterval < 
                                                     miMinAccInterval)
                        lpMsg->upd.recfg.set.miTimerInterval = miMinAccInterval;
                      lbToSend = true;
                    }

                    if (getAttrVal (lpNode, (char*) "enable", 
                           &lpcEnable[0]) == MSR_SUCCESS)
                    {
                      if (strncasecmp (lpcEnable, "false", 5) == 0)
                        lpMsg->upd.recfg.set.miEnable = false;
                      else
                        lpMsg->upd.recfg.set.miEnable = true;
                      lbToSend = true;
                    }

                    if (getAttrVal (lpNode, (char*) "version", 
                                        &lpcVersion[0]) == MSR_SUCCESS)
                    {
                      lpMsg->upd.recfg.set.mstrVersion = lpcVersion;
                      lbToSend = true;
                    }
                  }
                }
                

                DOMNode *lpChild = lpNode->getFirstChild ();
                while (lpChild)
                {
                  if (lpChild->getNodeType() == DOMNode::ELEMENT_NODE)
                  {
                    char *childName = XMLString::transcode 
                                             (lpChild->getNodeName());
                    if (strncasecmp (childName, "MeasurementCounter", 18) == 0)
                    {
                      if (getAttrVal (lpChild, (char*) "id", lpcValue) == MSR_SUCCESS)
                      {
                        if (lpMsg->meMsgType == MsrUpdateMsg::reconfigSet)
                          lpMsg->upd.recfg.set.mCounterList.push_back (lpcValue);
                        else if (lpMsg->meMsgType == MsrUpdateMsg::addSet)
                          lpMsg->upd.set.mCounterList.push_back (lpcValue);
                      }
                    }
                    XMLString::release (&childName);
                  }
                  lpChild = lpChild->getNextSibling ();
                }

                if (lbToSend)
                  mpWorkerThread->postUpdateMsg (lpMsg);
                else
                  delete lpMsg;
              }
            }
            else if (strcmp (nodeName, "MeasurementCounter") == 0)
            {
              char lpcId[MSR_MAX_TAGLEN], lpcInterval [MSR_MAX_TAGLEN],
                   lpcEnable [MSR_MAX_TAGLEN];
              if (getAttrVal (lpNode, (char*) "id", &lpcId[0]) == MSR_SUCCESS)
              {
                MsrUpdateMsg *lpMsg = new MsrUpdateMsg;
                bool lbToSend = false;

                lpMsg->meMsgType = MsrUpdateMsg::reconfigCounter;
                lpMsg->upd.recfg.ctr.miTimerInterval = -1;
                lpMsg->upd.recfg.ctr.miEnable = -1;

                if (getAttrVal (lpNode, (char*) "scanIntervalInSec", 
                        &lpcInterval[0]) == MSR_SUCCESS)
                {
                  lpMsg->upd.recfg.ctr.miTimerInterval = atoi (lpcInterval);

                  if (lpMsg->upd.recfg.ctr.miTimerInterval >
                          miMinScanInterval)
                    lbToSend = true;
                }

                if (getAttrVal (lpNode, (char*) "enable",
                        &lpcEnable[0]) == MSR_SUCCESS)
                {
                  if (strncasecmp (lpcEnable, "false", 5) == 0)
                    lpMsg->upd.recfg.ctr.miEnable = false;
                  else
                    lpMsg->upd.recfg.ctr.miEnable = true;

                  lbToSend = true;
                }

                if (lbToSend)
                  mpWorkerThread->postUpdateMsg (lpMsg);
                else
                  delete lpMsg;
              }
            }

            XMLString::release (&nodeName);
          }
          lpNode = lpNode->getNextSibling ();
        }

      }
    }
  }

  delete [] lpcValue;

  //    
  //  Delete the parser itself.  Must be done prior to calling Terminate, below.
  //
  if (parser)
    parser->release();
  parser = 0;

  // And call the termination method
  XMLPlatformUtils::Terminate();

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrMgr::reconfigureMsrMgr");
  return 0;
}

/******************************************************************************
*
*     Fun:   getAttrVal()
*
*     Desc:  This function returns value of an attribute
*
*     Notes: None
*
*     File:  MsrMgr.C
*
*******************************************************************************/
int
getAttrVal (DOMNode *apElement, char *apTag, char *apVal)
{
  if (apElement && apTag)
  {
    DOMElement *apNode = (DOMElement *) apElement;
    char *attrVal;
    XMLCh *lpTag = (XMLString::transcode(apTag));

    attrVal = XMLString::transcode (apNode->getAttribute
                      (lpTag));

    if (!attrVal)
    {
      XMLString::release (&lpTag);
      XMLString::release (&attrVal);
      return MSR_FAIL;
    }

    strcpy (apVal, attrVal);

    XMLString::release (&lpTag);
    XMLString::release (&attrVal);

    if (apVal)
    {
      logger.logINGwMsg (false, VERBOSE_FLAG, 0,
        "TAG = <%s> , VALUE <%s>", apTag, apVal);
    }
    else
    {
      logger.logINGwMsg (false, ERROR_FLAG, 0,
        "TAG = <%s> has a NULL value", apTag);
      return MSR_FAIL;
    }

    return MSR_SUCCESS;
  }

  return MSR_FAIL;
}

int 
MsrMgr::getInstValue (std::vector <std::string> &astrOidList,
                  std::vector <unsigned long> &aulValueList)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrMgr::getInstValue");

  int liRetVal =
       MsrInstant::getInstance ()->getValue (astrOidList, aulValueList);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrMgr::getInstValue");
  return liRetVal;
}

int 
MsrMgr::getInstValue (std::string &astrOid, unsigned long &aulValue)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrMgr::getInstValue");

  int liRetVal =
       MsrInstant::getInstance ()->getValue (astrOid, aulValue);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrMgr::getInstValue");
  return liRetVal;
}


void MsrMgr::getEmsParams(EmsOidValMap &params)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrMgr::getEmsParams");

  MsrInstant::getInstance ()->getEmsParams(params);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrMgr::getEmsParams");

  return;
}


#ifdef _MSR_STUB_

#include "Util/LogMgr.h"
#include <stdlib.h>
#include <pthread.h>

extern "C"
void*
operateOnCounters (void*)
{
  MsrMgr *lpMgr = MsrMgr::getInstance();

  unsigned long ran;
  char counter[100], entity[100], param[100];

  while (1)
  {
    ran = random () % 3;
    if (ran == 1)
    {
      lpMgr->increment ("testCounter1", "entity1", "param1");
      continue;
    }
    sprintf (counter, "testCounter%ld", ran);
    ran = random () % 2;
    sprintf (entity, "entity%ld", ran);
    ran = random () % 2;
    sprintf (param, "param%ld", ran);
    lpMgr->increment (counter, entity, param);

    sleep (1);
  }

  return (void*) NULL;
}

int main(int argc, char **argv)
{
  AlarmReporterFunction alarmFunc=0;
  int i;
  for (i=1; i<argc;i++)
  {
    if (strcmp(argv[i], "-d") == 0)
      break;
  }

  char *p_lcfg;
  if (argc > i+1)
    p_lcfg = argv[i+1];
  if (p_lcfg == 0) p_lcfg = (char*)"./sm.lcfg";

  LogMgr::instance().init(p_lcfg, alarmFunc);

  MsrMgr *lpMgr = MsrMgr::getInstance();
  lpMgr->initialize ();

  lpMgr->configureMsrMgr ("MsrMgr.xml");

  string counter, entity, param;
  pthread_t tid;
  pthread_create (&tid, 0, operateOnCounters, 0);
  pthread_create (&tid, 0, operateOnCounters, 0);
  pthread_create (&tid, 0, operateOnCounters, 0);
  pthread_create (&tid, 0, operateOnCounters, 0);

  while (1)
  {
    cout << "Enter : "; cout.flush();
    cin >> counter ; 

    if (counter == "dump")
      lpMgr->dump ();
    else if (counter == "reconfig")
    {
      cin >> entity;
      lpMgr->reconfigureMsrMgr (entity);
    }
    else if (counter == "get")
    {
      cin >> entity;
      unsigned long value = 0;
      if (lpMgr->getInstValue (entity, value) == MSR_SUCCESS)
        cout << entity << " : " << value << endl;
      else
        cout << entity << " : NOT FOUND " << endl;
    }
    else
    {
      cin >> entity ; cin >> param;
      lpMgr->increment (counter, entity, param);
    }

  }

  return 0;
}

#endif

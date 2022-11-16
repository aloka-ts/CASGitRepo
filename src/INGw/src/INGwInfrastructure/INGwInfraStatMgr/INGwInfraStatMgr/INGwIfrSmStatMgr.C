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
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>

#include <INGwInfraStatMgr/INGwIfrSmCommon.h>
BPLOG("StatMgr");

#define STATMGR_MODULE

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
#include <INGwInfraStatMgr/INGwIfrSmXmlParse.h>
#include <string.h>
#include <stdlib.h>
#include <fstream.h>
#include <strstream>

#include <INGwInfraStatMgr/INGwIfrSmStatMgr.h>

using namespace std;

class StatString_var
{
   private:

      char *_data;

   public:

      StatString_var()
      {
         _data = NULL;
      }

      StatString_var(char *data)
      {
         _data = data;
      }

      ~StatString_var()
      {
         if(_data)
         {
            delete []_data;
         }

         _data = NULL;
      }

      void operator = (char *data)
      {
         if(_data)
         {
            delete []_data;
         }

         _data = data;
      }

      const char *in()
      {
         return _data;
      }
};

int miMaxNumberOfThresholds = 10;

// Reads one parameter from xml file
bool loadStatParamFromXml(INGwIfrSmXmlParse& aParser,
                          INGwIfrSmStatParam*& aOutParam,
                          DOMNode* aParamNode);

// Reads one threshold from xml file
bool loadThresholdFromXml(INGwIfrSmXmlParse& aParser,
                          INGwIfrSmStatThreshold*& aNewThreshold,
                          DOMNode* aThreshNode);

//
// Parser object holds an instance of the parser.  At present this is
// completely guarded, and will do only one parsing at a time.
//
class INGwIfrSmXmlParse
{
  public:
    INGwIfrSmXmlParse();
    ~INGwIfrSmXmlParse();

    bool init(string aFileName, DOMNode*& aNode);
    bool release();

  private:
    void lock();
    void unlock();

    void reset();

    pthread_mutex_t mLock;

    DOMBuilder* parser;
    ifstream fin;
    bool miParsed;
}; // end of INGwIfrSmXmlParse


INGwIfrSmStatMgr* INGwIfrSmStatMgr::mpSelf = NULL;

INGwIfrSmStatMgr& INGwIfrSmStatMgr::instance()
{
  if(!mpSelf)
    mpSelf = new INGwIfrSmStatMgr();

  return *mpSelf;
}

INGwIfrSmStatMgr::INGwIfrSmStatMgr():miMaxNumberOfStatParams(0),
															       miDeferredThreadDuration(0)
{
  string paramvalstr = "";
  INGwIfrPrParamRepository::getInstance().getValue("MAX_STATPARAM_COUNT", paramvalstr);

	if (true != paramvalstr.empty()) {
  	miMaxNumberOfStatParams = atoi(paramvalstr.c_str());
	}

	if(miMaxNumberOfStatParams < 400)
    	miMaxNumberOfStatParams = 400;

  logger.logINGwMsg(false, ALWAYS_FLAG, 0, "INGwIfrSmStatMgr: miMaxNumberOfStatParams is <%d>", miMaxNumberOfStatParams);

  paramvalstr = "";
  INGwIfrPrParamRepository::getInstance().getValue("STAT_DEFERREDTHREAD_DURATION", paramvalstr);

	if (true == paramvalstr.empty()) {
		miDeferredThreadDuration = atoi(paramvalstr.c_str());
	}

  if(miDeferredThreadDuration <= 0)
    miDeferredThreadDuration = 30000;

  logger.logINGwMsg(false, ALWAYS_FLAG, 0, "INGwIfrSmStatMgr: miDeferredThreadDuration is <%d>", miDeferredThreadDuration);

  pthread_rwlock_init(&mParamIndexMapLock, NULL);

  mXmlParser = new INGwIfrSmXmlParse;

  // Create the list of parameter objects
  mStatParamList = new INGwIfrSmStatParam[miMaxNumberOfStatParams];

  for(int i = 0; i < miMaxNumberOfStatParams; ++i)
    mStatParamList[i].setIndex(i);
}

int INGwIfrSmStatMgr::getDeferredDuration()
{
  return miDeferredThreadDuration;
}

bool INGwIfrSmStatMgr::initialize(string aConfigFileName)
{
  // Try to load the statistics configuration
  bool retval = false;
  retval = loadStatistics(aConfigFileName);

  // Initialize the deferred thread
  if(retval)
  {
    retval = mrDeferredThread.initialize(miDeferredThreadDuration);
    mrDeferredThread.start();
  }

  return retval;
}

bool INGwIfrSmStatMgr::loadStatistics(string aConfigFileName)
{
  DOMNode* rootNode = NULL;
  bool retval = false;

  // Get the file parsed.
  retval = mXmlParser->init(aConfigFileName, rootNode);

  if(!retval || !rootNode)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "initialize: Error parsing the statistics configuration file.");
    mXmlParser->release();
    retval = false;
    return retval;
  }
  else
  {
    if(rootNode->getNodeType() != DOMNode::ELEMENT_NODE)
    {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "initialize: Error parsing the statistics configuration file: Root node is not an element");
      mXmlParser->release();
      retval = false;
      return retval;
    }

    DOMNode* child = NULL;
    int index;

    for(index = 0,                         child = rootNode->getFirstChild();
        index < miMaxNumberOfStatParams,   child != NULL;
        ++index,                           child = child->getNextSibling()) 
    {
      if(child->getNodeType() != DOMNode::ELEMENT_NODE)
        continue;

      // Load as many statistics parameters as available
      INGwIfrSmStatParam* newParam = NULL;
      retval = loadStatParamFromXml(*mXmlParser, newParam, child);

      if(!retval || !newParam)
      {
        logger.logINGwMsg(false, ERROR_FLAG, 0, "loadStatistics: Error loading statistics configuration");
        mXmlParser->release();
        retval = false;
        return retval;
      }
      else
      {
        // Replace the stat param at current index with the new one.
        addStatParam(newParam);
      }
    } // end of for
  }

  mXmlParser->release();

  return retval;
} // end of loadStatistics method

int INGwIfrSmStatMgr::addStatParam(INGwIfrSmStatParam* aParam)
{
  int retval = -1;

  // If the oid is already present in the map, replace that parameter.
  pthread_rwlock_rdlock(&mParamIndexMapLock);
  ParamIndexMap::iterator iter = mParamIndexMap.find(aParam->getOid());
  if(iter != mParamIndexMap.end())
  {
    retval = (*iter).second;
    pthread_rwlock_unlock(&mParamIndexMapLock);

    mStatParamList[retval].lock();
    mStatParamList[retval].replace(*aParam);
    mStatParamList[retval].unlock();
    
    delete aParam;
    return retval;
  }
  pthread_rwlock_unlock(&mParamIndexMapLock);

  // So the oid is already not present in the map, we need to add it.
  // Traverse the list of parameters and update with the first empty parameter.
  for(int i = 0; i <  miMaxNumberOfStatParams; ++i)
  {
    mStatParamList[i].lock();

    if(mStatParamList[i].empty())
    {
      mStatParamList[i].replace(*aParam);
      retval = i;

      // Add the index of the parameter into the parameter oid map
      pthread_rwlock_wrlock(&mParamIndexMapLock);
      mParamIndexMap[mStatParamList[i].getOid()] = i;
      pthread_rwlock_unlock(&mParamIndexMapLock);

      mStatParamList[i].unlock();
      break;
    }

    mStatParamList[i].unlock();
  } // end of for

  delete aParam;

  return retval;
} // end of addStatParam method

INGwIfrSmStatParam* INGwIfrSmStatMgr::loadStatParamFromXmlTemplate(string aXmlTemplateFileName)
{
  INGwIfrSmStatParam* retval = NULL;
  bool status = false;
  DOMNode* rootNode = NULL;

  // Get the file parsed
  status = mXmlParser->init(aXmlTemplateFileName, rootNode);

  if(!status || !rootNode)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "loadStatParamFromXmlTemplate: Error parsing the statistics configuration template xml file");
    mXmlParser->release();
    return NULL;
  }

  if(rootNode->getNodeType() != DOMNode::ELEMENT_NODE)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "loadStatParamFromXmlTemplate: Error parsing the statistics configuration template xml file: Root node is not an element");
    mXmlParser->release();
    return NULL;
  }

  // Load the statistics parameter
  status = loadStatParamFromXml(*mXmlParser, retval, rootNode);
  if(!status || !retval)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "loadStatParamFromXmlTemplate: Error parsing the statistics configuration template xml file: Could not create the parameter");
    mXmlParser->release();
    return NULL;
  }

  mXmlParser->release();
  return retval;
} // end of loadStatParamFromXmlTemplate method

void INGwIfrSmStatMgr::loadNewParam(INGwIfrSmStatParam* aNewParam, int aIndex)
{
  // Update the current parameter at the specified index with the new param
  mStatParamList[aIndex].lock();
  mStatParamList[aIndex].replace(*aNewParam);
  logger.logINGwMsg(false, VERBOSE_FLAG, 0, "Successfully loaded statistics parameter with oid <%s>", aNewParam->getOid().c_str());
  mStatParamList[aIndex].unlock();
} // end of loadNewParam method

#define STATPARAM_ATTRCOUNT 7

bool loadStatParamFromXml
  (INGwIfrSmXmlParse& mXmlParser, INGwIfrSmStatParam*& aOutParam, DOMNode* aParamNode)
{
  // Get the name of the element.  If it is not statparam, return error.
  StatString_var myvar = XMLString::transcode(aParamNode->getNodeName());
  const char* name = myvar.in();
  if(stricmp(name, "statparam"))
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "loadStatParamFromXml: Expected <statparam>, but found <%s>", name);
    return false;
  }

  // Get the list of attributes
  // The attributes are the following, and MUST be in the same order.
  // OID                       : <The oid of the parameter>
  // isDeferred                : <true/false>
  // isEmsParam                : <true/false>
  // resetOnRead               : <true/false>
  // isSnapShotOrCumulative    : <true/false>
  // isMinMaxReqd              : <true/false>
  // isAvgReqd                 : <true/false>
  DOMNamedNodeMap *pList = aParamNode->getAttributes();
  int count = pList->getLength();
  if(count != STATPARAM_ATTRCOUNT)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "loadStatParamFromXml: Expected  <%d> attributes for StatParam, but found <%d>", STATPARAM_ATTRCOUNT, count);
    // return false;
  }

  INGwIfrSmStatParam* newParam = new INGwIfrSmStatParam(0);
// int dum = 0;

  for(int i = STATPARAM_ATTRCOUNT - 1; i >= 0; --i)
  {
    DOMAttr *pAttr = (DOMAttr*)pList->item(i);
    const char* attrName = NULL;
    const char* attrVal  = NULL;

    StatString_var mynamevar = XMLString::transcode(pAttr->getName());
    StatString_var myvalvar = XMLString::transcode(pAttr->getValue());
    attrName = mynamevar.in();
    attrVal  = myvalvar.in();

    if(!attrName || !attrVal)
    {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "loadStatParamFromXml: Error getting parameter attributes");
      return false;
    }
    else
      logger.logINGwMsg(false, VERBOSE_FLAG, 0, "loadStatParamFromXml: Found <%s:%s>", attrName, attrVal);

//////////////////////////////////// MACROS ////////////////////////////////////
#define CHECKBOOL(x, y) \
    if(stricmp(x, "true") && stricmp(x, "false")) \
    { \
      logger.logINGwMsg(false, ERROR_FLAG, 0, "loadStatParamFromXml: Expected <true/false>, but found <%s>", x); \
      return false; \
      delete newParam; \
    } \
    else \
    { \
      if(!stricmp(x, "true")) \
        y = true; \
      else \
        y = false; \
    }
////////////////////////////////// END MACROS //////////////////////////////////

    bool boolValue = false;

    if(!stricmp("OID", attrName))
    {
      logger.logINGwMsg(false, VERBOSE_FLAG, 0, "loadStatParamFromXml: Found <%s>:<%s>", attrName, attrVal);
      newParam->setOid(string(attrVal));
    }
    else if(!stricmp("isDeferred", attrName))
    {
      CHECKBOOL(attrVal, boolValue)
      logger.logINGwMsg(false, VERBOSE_FLAG, 0, "loadStatParamFromXml: Found <%s>:<%s>", attrName, attrVal);
      newParam->setDeferredProcessingParam(boolValue);
    }
    else if(!stricmp("isEmsParam", attrName))
    {
      logger.logINGwMsg(false, VERBOSE_FLAG, 0, "loadStatParamFromXml: Found <%s>:<%s>", attrName, attrVal);
      CHECKBOOL(attrVal, boolValue)
      newParam->setEmsParam(boolValue);
    }
    else if(!stricmp("resetOnRead", attrName))
    {
      logger.logINGwMsg(false, VERBOSE_FLAG, 0, "loadStatParamFromXml: Found <%s>:<%s>", attrName, attrVal);
      CHECKBOOL(attrVal, boolValue)
      newParam->setResetOnRead(boolValue);
      logger.logINGwMsg(false, VERBOSE_FLAG, 0, "loadStatParamFromXml: Got <%d> for resetOnRead", newParam->getResetOnRead());
    }
    else if(!stricmp("isSnapShot", attrName))
    {
      logger.logINGwMsg(false, VERBOSE_FLAG, 0, "loadStatParamFromXml: Found <%s>:<%s>", attrName, attrVal);
      CHECKBOOL(attrVal, boolValue)
      newParam->setSnapShot(boolValue);
    }
    else if(!stricmp("isMinMaxReqd", attrName))
    {
      logger.logINGwMsg(false, VERBOSE_FLAG, 0, "loadStatParamFromXml: Found <%s>:<%s>", attrName, attrVal);
      CHECKBOOL(attrVal, boolValue)
      // newParam->setMinMax(boolValue);
      // Min/Max is set to true for now.
      newParam->setMinMax(true);
    }
    else if(!(/*dum = */stricmp("isAvgReqd", attrName)))
    {
      logger.logINGwMsg(false, VERBOSE_FLAG, 0, "loadStatParamFromXml: Found <%s>:<%s>", attrName, attrVal);
      CHECKBOOL(attrVal, boolValue)
      newParam->setAvg(boolValue);
      logger.logINGwMsg(false, VERBOSE_FLAG, 0, "loadStatParamFromXml: getAvg <%d>", newParam->getAvg());
    }
  } // end of for

#undef CHECKATTRNAME
#undef CHECKBOOL

  // Check for any threshold elements
  DOMNode* childNode = NULL;

  int index;
  for(index = 0,                        childNode = aParamNode->getFirstChild();
      index < miMaxNumberOfThresholds,  childNode != NULL;
      ++index,                          childNode = childNode->getNextSibling())
  {
    if(childNode->getNodeType() != DOMNode::ELEMENT_NODE)
      continue;

    INGwIfrSmStatThreshold* newThreshold = NULL;
    if(!loadThresholdFromXml(mXmlParser, newThreshold, childNode))
    {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "loadStatParamFromXml: Error loading threshold index <%d>", index);
      delete newParam;
      return false;
    }
    else
      newParam->addThreshold(newThreshold);
  } // end of for

  aOutParam = newParam;

  return true;
} // end of loadStatParamFromXml method

#define THRESHOLD_ATTRCOUNT 8

bool loadThresholdFromXml
  (INGwIfrSmXmlParse& mXmlParser, INGwIfrSmStatThreshold*& aNewThreshold, DOMNode* aThreshNode)
{
  // Get the name of the element.  If it is not statthreshold, return error.
  StatString_var nodenamevar = XMLString::transcode(aThreshNode->getNodeName());
  const char* name = nodenamevar.in();
  if(stricmp(name, "statthreshold"))
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "loadThresholdFromXml: Expected <statthreshold, but found <%s>", name);
    return false;
  }

  // Get the list of attributes
  // The attributes are the following, and MUST be in the same order.
  // ThresholdType          : "Absolute/Timediff/Percent/Timediff_Percent"
  // LowThreshold           : <low threshold value>
  // HighThreshold          : <high threshold value>
  // Associated Id          : <id>
  // ThresholdAction        : "Alarm/Log"
  // AlarmId                : <id>
  // AlarmMessage           : <message> 
  // TimeDifference         : <milliseconds>
  DOMNamedNodeMap *pList = aThreshNode->getAttributes();
  int count = pList->getLength();
  if(count != THRESHOLD_ATTRCOUNT)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "loadThresholdFromXml: Expected  <%d> attributes for threshold, but found <%d>", THRESHOLD_ATTRCOUNT);
    return false;
  }

  ThresholdType    lType;
  int              lLowThreshold        = -1;
  int              lHighThreshold       = -1;
  string           lAssociatedOid("");
  ThresholdAction  lAction;
  int              lAlarmId             = -1;
  string           lAlarmMsg("");
  int              lTimeDiffDuration    = -1;

  for(int i = THRESHOLD_ATTRCOUNT - 1; i >= 0; --i)
  {
    DOMAttr *pAttr = (DOMAttr*)pList->item(i);
    const char* attrName = NULL;
    const char* attrVal  = NULL;

    StatString_var namevar = XMLString::transcode(pAttr->getName());
    StatString_var valvar = XMLString::transcode(pAttr->getValue());
    attrName = namevar.in();
    attrVal  = valvar.in();

    if(!attrName || !attrVal)
    {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "loadThresholdFromXml: Error getting threshold attributes");
      return false;
    }
    else
      logger.logINGwMsg(false, VERBOSE_FLAG, 0, "loadThresholdFromXml: Found <%s:%s>", attrName, attrVal);

//////////////////////////////////// MACROS ////////////////////////////////////
#define CHECKATTRNAME(x) \
    if(stricmp(attrName, x)) \
    { \
      logger.logINGwMsg(false, ERROR_FLAG, 0, "loadThresholdFromXml: Expected <%s>, but found <%s>", x, attrName); \
      return false; \
    }
////////////////////////////////// END MACROS //////////////////////////////////

        if(!stricmp(attrName, "ThresholdType"))
        {
          logger.logINGwMsg(false, VERBOSE_FLAG, 0, "loadThresholdFromXml: Found threshold type <%s>", attrVal);
          if(!stricmp(attrVal, "Absolute"))
            lType = Thresh_Absolute;
          else if(!stricmp(attrVal, "Timediff"))
            lType = Thresh_TimeDiff;
          else if(!stricmp(attrVal, "Percent"))
            lType = Thresh_Percent;
          else if(!stricmp(attrVal, "Timediff_Percent"))
            lType = Thresh_TimeDiff_Percent;
          else
          {
            logger.logINGwMsg(false, ERROR_FLAG, 0, "loadThresholdFromXml: Unrecognized threshold type <%s>", attrVal);
            return false;
          }
        }
        else if(!stricmp(attrName, "LowThreshold"))
        {
          logger.logINGwMsg(false, VERBOSE_FLAG, 0, "loadThresholdFromXml: Found low threshold <%s>", attrVal);
          lLowThreshold = atoi(attrVal);
          logger.logINGwMsg(false, VERBOSE_FLAG, 0, "loadThresholdFromXml: Low threshold set to <%d>", lLowThreshold);
        }
        else if(!stricmp(attrName, "HighThreshold"))
        {
          logger.logINGwMsg(false, VERBOSE_FLAG, 0, "loadThresholdFromXml: Found high threshold <%s>", attrVal);
          lHighThreshold = atoi(attrVal);
          logger.logINGwMsg(false, VERBOSE_FLAG, 0, "loadThresholdFromXml: High threshold set to <%d>", lHighThreshold);
        }
        else if(!stricmp(attrName, "AssociatedId"))
        {
          logger.logINGwMsg(false, VERBOSE_FLAG, 0, "loadThresholdFromXml: Found associated id <%s>", attrVal);
          lAssociatedOid = attrVal;
          logger.logINGwMsg(false, VERBOSE_FLAG, 0, "loadThresholdFromXml: Associated id set to <%s>", lAssociatedOid.c_str());
        }
        else if(!stricmp(attrName, "ThresholdAction"))
        {
          logger.logINGwMsg(false, VERBOSE_FLAG, 0, "loadThresholdFromXml: Found threshold action <%s>", attrVal);
          if(!stricmp(attrVal, "ThreshAct_Log"))
            lAction = ThreshAct_Log;
          else if(!stricmp(attrVal, "ThreshAct_Alarm"))
            lAction = ThreshAct_Alarm;
          else
          {
            logger.logINGwMsg(false, ERROR_FLAG, 0, "loadThresholdFromXml: Unrecognized threshold action <%s>", attrVal);
            return false;
          }
        }
        else if(!stricmp(attrName, "AlarmId"))
        {
          logger.logINGwMsg(false, VERBOSE_FLAG, 0, "loadThresholdFromXml: Found alarm id <%s>", attrVal);
          lAlarmId = atoi(attrVal);
          logger.logINGwMsg(false, VERBOSE_FLAG, 0, "loadThresholdFromXml: Alarm id set to <%d>", lAlarmId);
        }
        else if(!stricmp(attrName, "AlarmMessage"))
        {
          logger.logINGwMsg(false, VERBOSE_FLAG, 0, "loadThresholdFromXml: Found alarm message <%s>", attrVal);
          lAlarmMsg = attrVal;
        }
        else if(!stricmp(attrName, "TimeDifference"))
        {
          logger.logINGwMsg(false, VERBOSE_FLAG, 0, "loadThresholdFromXml: Found time difference <%s>", attrVal);
          lTimeDiffDuration = atoi(attrVal);
          logger.logINGwMsg(false, VERBOSE_FLAG, 0, "loadThresholdFromXml: Time difference set to <%d>", lTimeDiffDuration);
        }
  } // end of for

  INGwIfrSmStatThreshold* newThreshold = new INGwIfrSmStatThreshold(lType, lLowThreshold, lHighThreshold, lAssociatedOid, lAction, lAlarmId, lAlarmMsg, lTimeDiffDuration);

  aNewThreshold = newThreshold;

  return true;
} // end of loadThresholdFromXml method

int INGwIfrSmStatMgr::getParamIndex(string aOidString, bool abAddParamIfAbsent)
{
  int retval = -1;

  pthread_rwlock_rdlock(&mParamIndexMapLock);
  ParamIndexMap::iterator iter = mParamIndexMap.find(aOidString);
  if(iter != mParamIndexMap.end())
    retval = (*iter).second;
  pthread_rwlock_unlock(&mParamIndexMapLock);

  if(abAddParamIfAbsent)
  {
    // If the oid did not exist, create a new one.
    if(retval < 0)
    {
      INGwIfrSmStatParam* newparam = new INGwIfrSmStatParam(0);
      newparam->setOid(aOidString);
      retval = addStatParam(newparam);
    }
  }

  return retval;
}

void INGwIfrSmStatMgr::getInternalValue
  (int   aIndex,
   int&  aValue,
   int&  aMaxValue,
   int&  aMinValue,
   bool& aIsSnapShot)
{
  if(aIndex < 0 || aIndex >= miMaxNumberOfStatParams)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "getInternalValue: index <%d> out of range", aIndex);
    return;
  }

  // Lock the required parameter.
  mStatParamList[aIndex].lock();
  mStatParamList[aIndex].getInternalValue
    (aValue, aMaxValue, aMinValue, aIsSnapShot);
  mStatParamList[aIndex].unlock();
}

ThresholdIndication INGwIfrSmStatMgr::setValue(int aIndex, int aValue)
{
  ThresholdIndication retval = NoThresholdExceeded;

  if(aIndex < 0 || aIndex >= miMaxNumberOfStatParams)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "getInternalValue: index <%d> out of range", aIndex);
    return retval;
  }


  mStatParamList[aIndex].lock();
  retval = mStatParamList[aIndex].setValue(aValue);
  mStatParamList[aIndex].unlock();

  return retval;
}

ThresholdIndication INGwIfrSmStatMgr::increment(int aIndex, int& aCurValue, int aValue)
{
  ThresholdIndication retval = NoThresholdExceeded;

  if(aIndex < 0 || aIndex >= miMaxNumberOfStatParams)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "increment: index <%d> out of range", aIndex);
    return retval;
  }

  mStatParamList[aIndex].lock();
  retval = mStatParamList[aIndex].increment(aCurValue, aValue);
  mStatParamList[aIndex].unlock();

  return retval;
}

ThresholdIndication INGwIfrSmStatMgr::decrement(int aIndex, int& aCurValue, int aValue)
{
  ThresholdIndication retval = NoThresholdExceeded;
  if(aIndex < 0 || aIndex >= miMaxNumberOfStatParams)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "decrement: index <%d> out of range", aIndex);
    return retval;
  }

  mStatParamList[aIndex].lock();
  retval = mStatParamList[aIndex].decrement(aCurValue, aValue);
  mStatParamList[aIndex].unlock();

  return retval;
}


char* INGwIfrSmStatMgr::getOidName(const char *oid)
{
  char oName[100];
  memset(oName, 0, sizeof(oName));

  if( strncmp( "8.11.74", oid, strlen("8.11.74")) == 0)
  {
   strncpy(oName, "Tcap: Total Msg Tx",strlen("Tcap: Total Msg Tx"));
  }
  else if( strncmp( "8.11.75", oid, strlen("8.11.75")) == 0)
  {
    strncpy(oName, "Tcap: Total Unidirectional Msg Tx",strlen("Tcap: Total Unidirectional Msg Tx"));
  }
  else if( strncmp( "8.11.76", oid, strlen("8.11.76")) == 0)
  {
    strncpy(oName, "Tcap: Total Abort Msg Tx",strlen("Tcap: Total Abort Msg Tx"));
  }
  else if( strncmp( "8.11.77", oid, strlen("8.11.77")) == 0)
  {
    strncpy(oName, "Tcap: Total Msg Rx",strlen("Tcap: Total Msg Rx"));
  }
  else if( strncmp( "8.11.78", oid, strlen("8.11.78")) == 0)
  {
    strncpy(oName, "Tcap: Total Unidirectional Msg Rx",strlen("Tcap: Total Unidirectional Msg Rx"));
  }
  else if( strncmp( "8.11.79", oid, strlen("8.11.79")) == 0)
  {
    strncpy(oName, "Tcap: Total Abort Msg Rx",strlen("Tcap: Total Abort Msg Rx"));
  }
  else if( strncmp( "8.11.90", oid, strlen("8.11.90")) == 0)
  {
    strncpy(oName, "Tcap: Total TC-Begin Tx",strlen("Tcap: Total TC-Begin Tx"));
  }
  else if( strncmp( "8.11.91", oid, strlen("8.11.91")) == 0)
  {
    strncpy(oName, "Tcap: Total TC-Continue Tx",strlen("Tcap: Total TC-Continue Tx"));
  }
  else if( strncmp( "8.11.92", oid, strlen("8.11.92")) == 0)
  {
    strncpy(oName, "Tcap: Total TC-End Tx",strlen("Tcap: Total TC-End Tx"));
  }
  else if( strncmp( "8.11.93", oid, strlen("8.11.93")) == 0)
  {
    strncpy(oName, "Tcap: Total TC-Begin Rx",strlen("Tcap: Total TC-Begin Rx"));
  }
  else if( strncmp( "8.11.94", oid, strlen("8.11.94")) == 0)
  {
    strncpy(oName, "Tcap: Total TC-Continue Rx",strlen("Tcap: Total TC-Continue Rx"));
  }
  else if( strncmp( "8.11.95", oid, strlen("8.11.95")) == 0)
  {
    strncpy(oName, "Tcap: Total TC-End Rx",strlen("Tcap: Total TC-End Rx"));
  }
  else if( strncmp( "8.11.96", oid, strlen("8.11.96")) == 0)
  {
    strncpy(oName, "Tcap: Total Components Tx",strlen("Tcap: Total Components Tx"));
  }
  else if( strncmp( "8.11.97", oid, strlen("8.11.97")) == 0)
  {
    strncpy(oName, "Tcap: Total Components Rx",strlen("Tcap: Total Components Rx"));
  }
  else if( strncmp( "8.11.98", oid, strlen("8.11.98")) == 0)
  {
    strncpy(oName, "Tcap: Total Invokes Tx",strlen("Tcap: Total Invokes Tx"));
  }
  else if( strncmp( "8.11.99", oid, strlen("8.11.99")) == 0)
  {
    strncpy(oName, "Tcap: Total Invokes Rx",strlen("Tcap: Total Invokes Rx"));
  }
  else if( strncmp( "8.11.100", oid, strlen("8.11.100")) == 0)
  {
    strncpy(oName, "Tcap: Total Return-result comp Tx",strlen("Tcap: Total Return-result comp Tx"));
  }
  else if( strncmp( "8.11.101", oid, strlen("8.11.101")) == 0)
  {
    strncpy(oName, "Tcap: Total Return-result comp Rx",strlen("Tcap: Total Return-result comp Rx"));
  }
  else if( strncmp( "8.11.102", oid, strlen("8.11.102")) == 0)
  {
    strncpy(oName, "Tcap: Total Return-err comp Tx",strlen("Tcap: Total Return-err comp Tx"));
  }
  else if( strncmp( "8.11.103", oid, strlen("8.11.103")) == 0)
  {
    strncpy(oName, "Tcap: Total Return-err comp Rx",strlen("Tcap: Total Return-err comp Rx"));
  }
  else if( strncmp( "8.11.104", oid, strlen("8.11.104")) == 0)
  {
    strncpy(oName, "Tcap: Total Reject comp Tx",strlen("Tcap: Total Reject comp Tx"));
  }
  else if( strncmp( "8.11.105", oid, strlen("8.11.105")) == 0)
  {
    strncpy(oName, "Tcap: Total Reject comp Rx",strlen("Tcap: Total Reject comp Rx"));
  }
  else if( strncmp( "8.11.106", oid, strlen("8.11.106")) == 0)
  {
    strncpy(oName, "Tcap: Total No of Active Trans",strlen("Tcap: Total No of Active Trans"));
  }
  else if( strncmp( "8.11.107", oid, strlen("8.11.107")) == 0)
  {
    strncpy(oName, "Tcap: Total No of Active invokes",strlen("Tcap: Total No of Active invokes"));
  }
  else if( strncmp( "8.11.108", oid, strlen("8.11.108")) == 0)
  {
    strncpy(oName, "Tcap: Total No of used TransId",strlen("Tcap: Total No of used TransId"));
  }
  else if( strncmp( "8.11.109", oid, strlen("8.11.109")) == 0)
  {
    strncpy(oName, "Tcap: Total Rx Msg Dropd",strlen("Tcap: Total Rx Msg Dropd"));
  }
  else if( strncmp( "8.11.110", oid, strlen("8.11.110")) == 0)
  {
    strncpy(oName, "Tcap: Total Unrecognized msg Rx",strlen("Tcap: Total Unrecognized msg Rx"));
  }
  else if( strncmp( "8.11.111", oid, strlen("8.11.111")) == 0)
  {
    strncpy(oName, "Tcap: Total Incorrect trans portion Rx",strlen("Tcap: Total Incorrect trans portion Rx"));
  }
  else if( strncmp( "8.11.112", oid, strlen("8.11.112")) == 0)
  {
    strncpy(oName, "Tcap: Total Bad struct trans portion Rx",strlen("Tcap: Total Bad struct trans portion Rx"));
  }
  else if( strncmp( "8.11.113", oid, strlen("8.11.113")) == 0)
  {
    strncpy(oName, "Tcap: Total Unrecog trans-id Rx",strlen("Tcap: Total Unrecog trans-id Rx"));
  }
  else if( strncmp( "8.11.114", oid, strlen("8.11.114")) == 0)
  {
    strncpy(oName, "Tcap: Total Resource limit Rx",strlen("Tcap: Total Resource limit Rx"));
  }
  else if( strncmp( "8.11.115", oid, strlen("8.11.115")) == 0)
  {
    strncpy(oName, "Tcap: Total Unrecog comp portion Rx",strlen("Tcap: Total Unrecog comp portion Rx"));
  }
  else if( strncmp( "8.11.116", oid, strlen("8.11.116")) == 0)
  {
    strncpy(oName, "Tcap: Total Incorr comp portion Rx",strlen("Tcap: Total Incorr comp portion Rx"));
  }
  else if( strncmp( "8.11.117", oid, strlen("8.11.117")) == 0)
  {
    strncpy(oName, "Tcap: Total Bad struct comp portion Rx",strlen("Tcap: Total Bad struct comp portion Rx"));
  }
  else if( strncmp( "8.11.118", oid, strlen("8.11.118")) == 0)
  {
    strncpy(oName, "Tcap: Total Unrecog linkid Rx",strlen("Tcap: Total Unrecog linkid Rx"));
  }
  else if( strncmp( "8.11.119", oid, strlen("8.11.119")) == 0)
  {
    strncpy(oName, "Tcap: Total Unrecog invkid Rx",strlen("Tcap: Total Unrecog invkid Rx"));
  }
  else if( strncmp( "8.11.120", oid, strlen("8.11.120")) == 0)
  {
    strncpy(oName, "Tcap: Total Unexp ret res Rx",strlen("Tcap: Total Unexp ret res Rx"));
  }
  else if( strncmp( "8.11.121", oid, strlen("8.11.121")) == 0)
  {
    strncpy(oName, "Tcap: Total Unrecog invokeid ret res Rx",strlen("Tcap: Total Unrecog invokeid ret res Rx"));
  }
  else if( strncmp( "8.11.122", oid, strlen("8.11.122")) == 0)
  {
    strncpy(oName, "Tcap: Total Unexp err Rx",strlen("Tcap: Total Unexp err Rx"));
  }
  else if( strncmp( "8.11.123", oid, strlen("8.11.123")) == 0)
  {
    strncpy(oName, "Tcap: Total Unrecognized msg Tx",strlen("Tcap: Total Unrecognized msg Tx"));
  }
  else if( strncmp( "8.11.124", oid, strlen("8.11.124")) == 0)
  {
    strncpy(oName, "Tcap: Total Incorrect trans portion Tx",strlen("Tcap: Total Incorrect trans portion Tx"));
  }
  else if( strncmp( "8.11.125", oid, strlen("8.11.125")) == 0)
  {
    strncpy(oName, "Tcap: Total Bad struct trans portion Tx",strlen("Tcap: Total Bad struct trans portion Tx"));
  }
  else if( strncmp( "8.11.126", oid, strlen("8.11.126")) == 0)
  {
    strncpy(oName, "Tcap: Total Unrecog trans-id Tx",strlen("Tcap: Total Unrecog trans-id Tx"));
  }
  else if( strncmp( "8.11.127", oid, strlen("8.11.127")) == 0)
  {
    strncpy(oName, "Tcap: Total Resource limit Tx",strlen("Tcap: Total Resource limit Tx"));
  }
  else if( strncmp( "8.11.128", oid, strlen("8.11.128")) == 0)
  {
    strncpy(oName, "Tcap: Total Unrecog comp portion Tx",strlen("Tcap: Total Unrecog comp portion Tx"));
  }
  else if( strncmp( "8.11.129", oid, strlen("8.11.129")) == 0)
  {
    strncpy(oName, "Tcap: Total Incorr comp portion Tx",strlen("Tcap: Total Incorr comp portion Tx"));
  }
  else if( strncmp( "8.11.130", oid, strlen("8.11.130")) == 0)
  {
    strncpy(oName, "Tcap: Total Bad struct comp portion Tx",strlen("Tcap: Total Bad struct comp portion Tx"));
  }
  else if( strncmp( "8.11.131", oid, strlen("8.11.131")) == 0)
  {
    strncpy(oName, "Tcap: Total Unrecog linkid Tx",strlen("Tcap: Total Unrecog linkid Tx"));
  }
  else if( strncmp( "8.11.132", oid, strlen("8.11.132")) == 0)
  {
    strncpy(oName, "Tcap: Total Unrecog invkid Tx",strlen("Tcap: Total Unrecog invkid Tx"));
  }
  else if( strncmp( "8.11.133", oid, strlen("8.11.133")) == 0)
  {
    strncpy(oName, "Tcap: Total Unexp ret res Tx",strlen("Tcap: Total Unexp ret res Tx"));
  }
  else if( strncmp( "8.11.134", oid, strlen("8.11.134")) == 0)
  {
    strncpy(oName, "Tcap: Total Unrecog invokeid ret res Tx",strlen("Tcap: Total Unrecog invokeid ret res Tx"));
  }
  else if( strncmp( "8.11.135", oid, strlen("8.11.135")) == 0)
  {
    strncpy(oName, "Tcap: Total Unexp err Tx",strlen("Tcap: Total Unexp err Tx"));
  }//Tcap layer oid ends here
  else if( strncmp( "8.11.140", oid, strlen("8.11.140")) == 0)
  {
    strncpy(oName, "Sccp: Count of No GT for addr of such nat",strlen("Sccp: Count of No GT for addr of such nat"));
  }
  else if( strncmp( "8.11.141", oid, strlen("8.11.141")) == 0)
  {
    strncpy(oName, "Sccp: Count of No GT for this addr",strlen("Sccp: Count of No GT for this addr"));
  }
  else if( strncmp( "8.11.142", oid, strlen("8.11.142")) == 0)
  {
    strncpy(oName, "Sccp: Count of N/W failure",strlen("Sccp: Count of N/W Failure"));
  }
  else if( strncmp( "8.11.143", oid, strlen("8.11.143")) == 0)
  {
    strncpy(oName, "Sccp: Count of N/W congestion",strlen("Sccp: Count of N/W congestion"));
  }
  else if( strncmp( "8.11.144", oid, strlen("8.11.144")) == 0)
  {
    strncpy(oName, "Sccp: Count of SSN failure",strlen("Sccp: Count of SSN failure"));
  }
  else if( strncmp( "8.11.145", oid, strlen("8.11.145")) == 0)
  {
    strncpy(oName, "Sccp: Count of SSN congestion",strlen("Sccp: Count of SSN congestion"));
  }
  else if( strncmp( "8.11.146", oid, strlen("8.11.146")) == 0)
  {
    strncpy(oName, "Sccp: Count of unequippd usr",strlen("Sccp: Count of unequippd usr"));
  }
  else if( strncmp( "8.11.147", oid, strlen("8.11.147")) == 0)
  {
    strncpy(oName, "Sccp: Count of Hop Cnt voilation",strlen("Sccp: Count of Hop Cnt voilation"));
  }
  else if( strncmp( "8.11.148", oid, strlen("8.11.148")) == 0)
  {
    strncpy(oName, "Sccp: Count of Syntax err",strlen("Sccp: Count of Syntax err"));
  }
  else if( strncmp( "8.11.149", oid, strlen("8.11.149")) == 0)
  {
    strncpy(oName, "Sccp: Count of Unknown reason",strlen("Sccp: Count of Unknown reason"));
  }
  else if( strncmp( "8.11.150", oid, strlen("8.11.150")) == 0)
  {
    strncpy(oName, "Sccp: Total SSN cong msg Rx",strlen("Sccp: Total SSN cong msg Rx"));
  }
  else if( strncmp( "8.11.151", oid, strlen("8.11.151")) == 0)
  {
    strncpy(oName, "Sccp: Total SSN prohibit msg Rx",strlen("Sccp: Total SSN prohibit msg Rx"));
  }
  else if( strncmp( "8.11.152", oid, strlen("8.11.152")) == 0)
  {
    strncpy(oName, "Sccp: Total UData Msg Tx",strlen("Sccp: Total UData Msg Tx"));
  }
  else if( strncmp( "8.11.153", oid, strlen("8.11.153")) == 0)
  {
    strncpy(oName, "Sccp: Total UData Srvc Msg Tx",strlen("Sccp: Total UData Srvc Msg Tx"));
  }
  else if( strncmp( "8.11.154", oid, strlen("8.11.154")) == 0)
  {
    strncpy(oName, "Sccp: Total UData Msg Rx",strlen("Sccp: Total UData Msg Rx"));
  }
  else if( strncmp( "8.11.155", oid, strlen("8.11.155")) == 0)
  {
    strncpy(oName, "Sccp: Total UData Srvc Msg Rx",strlen("Sccp: Total UData Srvc Msg Rx"));
  }
  else if( strncmp( "8.11.156", oid, strlen("8.11.156")) == 0)
  {
    strncpy(oName, "Sccp: Total XUData Msg Tx",strlen("Sccp: Total XUData Msg Tx"));
  }
  else if( strncmp( "8.11.157", oid, strlen("8.11.157")) == 0)
  {
    strncpy(oName, "Sccp: Total XUData Srvc Msg Tx",strlen("Sccp: Total XUData Srvc Msg Tx"));
  }
  else if( strncmp( "8.11.158", oid, strlen("8.11.158")) == 0)
  {
    strncpy(oName, "Sccp: Total XUData Msg Rx",strlen("Sccp: Total XUData Msg Rx"));
  }
  else if( strncmp( "8.11.159", oid, strlen("8.11.159")) == 0)
  {
    strncpy(oName, "Sccp: Total XUData Srvc Msg Rx",strlen("Sccp: Total XUData Srvc Msg Rx"));
  }
  else if( strncmp( "8.11.160", oid, strlen("8.11.160")) == 0)
  {
    strncpy(oName, "Sccp: Total LUData Msg Tx",strlen("Sccp: Total LUData Msg Tx"));
  }
  else if( strncmp( "8.11.161", oid, strlen("8.11.161")) == 0)
  {
    strncpy(oName, "Sccp: Total LUData Srvc Msg Tx",strlen("Sccp: Total LUData Srvc Msg Tx"));
  }
  else if( strncmp( "8.11.162", oid, strlen("8.11.162")) == 0)
  {
    strncpy(oName, "Sccp: Total LUData Msg Rx",strlen("Sccp: Total LUData Msg Rx"));
  }
  else if( strncmp( "8.11.163", oid, strlen("8.11.163")) == 0)
  {
    strncpy(oName, "Sccp: Total LUData Srvc Msg Rx",strlen("Sccp: Total LUData Srvc Msg Rx"));
  }
  else if( strncmp( "8.11.164", oid, strlen("8.11.164")) == 0)
  {
    strncpy(oName, "Sccp: Total Segmentation Err Rx",strlen("Sccp: Total Segmentation Err Rx"));
  }
  else if( strncmp( "8.11.165", oid, strlen("8.11.165")) == 0)
  {
    strncpy(oName, "Sccp: Total Segmentation Fail Err Rx",strlen("Sccp: Total Segmentation Fail Err Rx"));
  }
  else if( strncmp( "8.11.166", oid, strlen("8.11.166")) == 0)
  {
    strncpy(oName, "Sccp: Total Reassembly Err Rx",strlen("Sccp: Total Reassembly Err Rx"));
  }
  else if( strncmp( "8.11.167", oid, strlen("8.11.167")) == 0)
  {
    strncpy(oName, "Sccp: Total Reassembly Err-Timer expiry Rx",strlen("Sccp: Total Reassembly Err-Timer expiry Rx"));
  }
  else if( strncmp( "8.11.168", oid, strlen("8.11.168")) == 0)
  {
    strncpy(oName, "Sccp: Total Reassembly Err-No Space Rx",strlen("Sccp: Total Reassembly Err-No Space Rx"));
  }
//Sccp oids end here

  else if( strncmp( "8.11.231", oid, strlen("8.11.231")) == 0)
  {
    strncpy(oName, "M3ua: Total DATA Msg Tx",strlen("M3ua: Total DATA Msg Tx"));
  }
  else if( strncmp( "8.11.232", oid, strlen("8.11.232")) == 0)
  {
    strncpy(oName, "M3ua: Total DUNA Msg Tx",strlen("M3ua: Total DUNA Msg Tx"));
  }
  else if( strncmp( "8.11.233", oid, strlen("8.11.233")) == 0)
  {
    strncpy(oName, "M3ua: Total DAVA Msg Tx",strlen("M3ua: Total DAVA Msg Tx"));
  }
  else if( strncmp( "8.11.234", oid, strlen("8.11.234")) == 0)
  {
    strncpy(oName, "M3ua: Total DAUD Msg Tx",strlen("M3ua: Total DAUD Msg Tx"));
  }
  else if( strncmp( "8.11.235", oid, strlen("8.11.235")) == 0)
  {
    strncpy(oName, "M3ua: Total SCON Msg Tx",strlen("M3ua: Total SCON Msg Tx"));
  }
  else if( strncmp( "8.11.236", oid, strlen("8.11.236")) == 0)
  {
    strncpy(oName, "M3ua: Total DUPU Msg Tx",strlen("M3ua: Total DUPU Msg Tx"));
  }
  else if( strncmp( "8.11.237", oid, strlen("8.11.237")) == 0)
  {
    strncpy(oName, "M3ua: Total DRST Msg Tx",strlen("M3ua: Total DRST Msg Tx"));
  }
  else if( strncmp( "8.11.238", oid, strlen("8.11.238")) == 0)
  {
    strncpy(oName, "M3ua: Total REG-REQ Tx",strlen("M3ua: Total REG-REQ Tx"));
  }
  else if( strncmp( "8.11.239", oid, strlen("8.11.239")) == 0)
  {
    strncpy(oName, "M3ua: Total DEREG-REQ Tx",strlen("M3ua: Total DEREG-REQ Tx"));
  }
  else if( strncmp( "8.11.240", oid, strlen("8.11.240")) == 0)
  {
    strncpy(oName, "M3ua: Total REG-RSP Tx",strlen("M3ua: Total REG-RSP Tx"));
  }
  else if( strncmp( "8.11.241", oid, strlen("8.11.241")) == 0)
  {
    strncpy(oName, "M3ua: Total DEREG-RSP Tx",strlen("M3ua: Total DEREG-RSP Tx"));
  }
  else if( strncmp( "8.11.242", oid, strlen("8.11.242")) == 0)
  {
    strncpy(oName, "M3ua: Total ASP-UP Msg Tx",strlen("M3ua: Total ASP-UP Msg Tx"));
  }
  else if( strncmp( "8.11.243", oid, strlen("8.11.243")) == 0)
  {
    strncpy(oName, "M3ua: Total ASP-UP Ack Msg Tx",strlen("M3ua: Total ASP-UP Ack Msg Tx"));
  }
  else if( strncmp( "8.11.244", oid, strlen("8.11.244")) == 0)
  {
    strncpy(oName, "M3ua: Total ASP-DN Msg Tx",strlen("M3ua: Total ASP-DN Msg Tx"));
  }
  else if( strncmp( "8.11.245", oid, strlen("8.11.245")) == 0)
  {
    strncpy(oName, "M3ua: Total ASP-DN Ack Msg Tx",strlen("M3ua: Total ASP-DN Ack Msg Tx"));
  }
  else if( strncmp( "8.11.246", oid, strlen("8.11.246")) == 0)
  {
    strncpy(oName, "M3ua: Total ASP-ACTV Msg Tx",strlen("M3ua: Total ASP-ACTV Msg Tx"));
  }
  else if( strncmp( "8.11.247", oid, strlen("8.11.247")) == 0)
  {
    strncpy(oName, "M3ua: Total ASP-ACTV Ack Msg Tx",strlen("M3ua: Total ASP-ACTV Ack Msg Tx"));
  }
  else if( strncmp( "8.11.248", oid, strlen("8.11.248")) == 0)
  {
    strncpy(oName, "M3ua: Total ASP-INACTV Msg Tx",strlen("M3ua: Total ASP-INACTV Msg Tx"));
  }
  else if( strncmp( "8.11.249", oid, strlen("8.11.249")) == 0)
  {
    strncpy(oName, "M3ua: Total ASP-INACTV Ack Msg Tx",strlen("M3ua: Total ASP-INACTV Ack Msg Tx"));
  }
  else if( strncmp( "8.11.250", oid, strlen("8.11.250")) == 0)
  {
    strncpy(oName, "M3ua: Total Heartbeat Msg Tx",strlen("M3ua: Total Heartbeat Msg Tx"));
  }
  else if( strncmp( "8.11.251", oid, strlen("8.11.251")) == 0)
  {
    strncpy(oName, "M3ua: Total Heartbeat Ack Msg Tx",strlen("M3ua: Total Heartbeat Ack Msg Tx"));
  }
  else if( strncmp( "8.11.252", oid, strlen("8.11.252")) == 0)
  {
    strncpy(oName, "M3ua: Total Err Msg Tx",strlen("M3ua: Total Err Tx"));
  }
  else if( strncmp( "8.11.253", oid, strlen("8.11.253")) == 0)
  {
    strncpy(oName, "M3ua: Total Notify Msg Tx",strlen("M3ua: Total Notify Msg Tx"));
  }
  else if( strncmp( "8.11.254", oid, strlen("8.11.254")) == 0)
  {
    strncpy(oName, "M3ua: Total DATA Msg Rx",strlen("M3ua: Total DATA Msg Rx"));
  }
  else if( strncmp( "8.11.255", oid, strlen("8.11.255")) == 0)
  {
    strncpy(oName, "M3ua: Total DUNA Msg Rx",strlen("M3ua: Total DUNA Msg Rx"));
  }
  else if( strncmp( "8.11.256", oid, strlen("8.11.256")) == 0)
  {
    strncpy(oName, "M3ua: Total DAVA Msg Rx",strlen("M3ua: Total DAVA Msg Rx"));
  }
  else if( strncmp( "8.11.257", oid, strlen("8.11.257")) == 0)
  {
    strncpy(oName, "M3ua: Total DAUD Msg Rx",strlen("M3ua: Total DAUD Msg Rx"));
  }
  else if( strncmp( "8.11.258", oid, strlen("8.11.258")) == 0)
  {
    strncpy(oName, "M3ua: Total SCON Msg Rx",strlen("M3ua: Total SCON Msg Rx"));
  }
  else if( strncmp( "8.11.259", oid, strlen("8.11.259")) == 0)
  {
    strncpy(oName, "M3ua: Total DUPU Msg Rx",strlen("M3ua: Total DUPU Msg Rx"));
  }
  else if( strncmp( "8.11.260", oid, strlen("8.11.260")) == 0)
  {
    strncpy(oName, "M3ua: Total DRST Msg Rx",strlen("M3ua: Total DRST Msg Rx"));
  }
  else if( strncmp( "8.11.261", oid, strlen("8.11.261")) == 0)
  {
    strncpy(oName, "M3ua: Total REG-REQ Rx",strlen("M3ua: Total REG-REQ Rx"));
  }
  else if( strncmp( "8.11.262", oid, strlen("8.11.262")) == 0)
  {
    strncpy(oName, "M3ua: Total DEREG-REQ Rx",strlen("M3ua: Total DEREG-REQ Rx"));
  }
  else if( strncmp( "8.11.263", oid, strlen("8.11.263")) == 0)
  {
    strncpy(oName, "M3ua: Total REG-RSP Rx",strlen("M3ua: Total REG-RSP Rx"));
  }
  else if( strncmp( "8.11.264", oid, strlen("8.11.264")) == 0)
  {
    strncpy(oName, "M3ua: Total DEREG-RSP Rx",strlen("M3ua: Total DEREG-RSP Rx"));
  }
  else if( strncmp( "8.11.265", oid, strlen("8.11.265")) == 0)
  {
    strncpy(oName, "M3ua: Total ASP-UP Msg Rx",strlen("M3ua: Total ASP-UP Msg Rx"));
  }
  else if( strncmp( "8.11.266", oid, strlen("8.11.266")) == 0)
  {
    strncpy(oName, "M3ua: Total ASP-UP Ack Msg Rx",strlen("M3ua: Total ASP-UP Ack Msg Rx"));
  }
  else if( strncmp( "8.11.267", oid, strlen("8.11.267")) == 0)
  {
    strncpy(oName, "M3ua: Total ASP-DN Msg Rx",strlen("M3ua: Total ASP-DN Msg Rx"));
  }
  else if( strncmp( "8.11.268", oid, strlen("8.11.268")) == 0)
  {
    strncpy(oName, "M3ua: Total ASP-DN Ack Msg Rx",strlen("M3ua: Total ASP-DN Ack Msg Rx"));
  }
  else if( strncmp( "8.11.269", oid, strlen("8.11.269")) == 0)
  {
    strncpy(oName, "M3ua: Total ASP-ACTV Msg Rx",strlen("M3ua: Total ASP-ACTV Msg Rx"));
  }
  else if( strncmp( "8.11.270", oid, strlen("8.11.270")) == 0)
  {
    strncpy(oName, "M3ua: Total ASP-ACTV Ack Msg Rx",strlen("M3ua: Total ASP-ACTV Ack Msg Rx"));
  }
  else if( strncmp( "8.11.271", oid, strlen("8.11.271")) == 0)
  {
    strncpy(oName, "M3ua: Total ASP-INACTV Msg Rx",strlen("M3ua: Total ASP-INACTV Msg Rx"));
  }
  else if( strncmp( "8.11.272", oid, strlen("8.11.272")) == 0)
  {
    strncpy(oName, "M3ua: Total ASP-INACTV Ack Msg Rx",strlen("M3ua: Total ASP-INACTV Ack Msg Rx"));
  }
  else if( strncmp( "8.11.273", oid, strlen("8.11.273")) == 0)
  {
    strncpy(oName, "M3ua: Total Heartbeat Msg Rx",strlen("M3ua: Total Heartbeat Msg Rx"));
  }
  else if( strncmp( "8.11.274", oid, strlen("8.11.274")) == 0)
  {
    strncpy(oName, "M3ua: Total Heartbeat Ack Msg Rx",strlen("M3ua: Total Heartbeat Ack Msg Rx"));
  }
  else if( strncmp( "8.11.275", oid, strlen("8.11.275")) == 0)
  {
    strncpy(oName, "M3ua: Total Err Msg Rx",strlen("M3ua: Total Err Rx"));
  }
  else if( strncmp( "8.11.276", oid, strlen("8.11.276")) == 0)
  {
    strncpy(oName, "M3ua: Total Notify Msg Rx",strlen("M3ua: Total Notify Msg Rx"));
  }
  
  else if( strncmp( "8.11.277", oid, strlen("8.11.277")) == 0)
  {
    strncpy(oName, "M3ua: Total PDUs dropped-No Rte Upwrds",strlen("M3ua: Total PDUs dropped-No Rte Upwrds"));
  }
  else if( strncmp( "8.11.278", oid, strlen("8.11.278")) == 0)
  {
    strncpy(oName, "M3ua: Total PDUs dropped-PC unavail Upwrds",strlen("M3ua: Total PDUs dropped-PC unavail Upwrds"));
  }
  else if( strncmp( "8.11.279", oid, strlen("8.11.279")) == 0)
  {
    strncpy(oName, "M3ua: Total PDUs dropped-PC Cong Upwrds",strlen("M3ua: Total PDUs dropped-PC Cong Upwrds"));
  }
  else if( strncmp( "8.11.280", oid, strlen("8.11.280")) == 0)
  {
    strncpy(oName, "M3ua: Total PDUs dropped-No PSP avail Upwrds",strlen("M3ua: Total PDUs dropped-No PSP avail Upwrds"));
  }
  else if( strncmp( "8.11.281", oid, strlen("8.11.281")) == 0)
  {
    strncpy(oName, "M3ua: Total PDUs dropped-No NSAP avail Upwrds",strlen("M3ua: Total PDUs dropped-No NSAP avail Upwrds"));
  }
  else if( strncmp( "8.11.282", oid, strlen("8.11.282")) == 0)
  {
    strncpy(oName, "M3ua: Total PDUs dropped-Load sharing failed Upwrds",strlen("M3ua: Total PDUs dropped-Load sharinf failed Upwrds"));
  }
  else if( strncmp( "8.11.283", oid, strlen("8.11.283")) == 0)
  {
    strncpy(oName, "M3ua: Total PDUs dropped-Msg handler failed Upwrds",strlen("M3ua: Total PDUs dropped-Msg handler failed Upwrds"));
  }
  else if( strncmp( "8.11.284", oid, strlen("8.11.284")) == 0)
  {
    strncpy(oName, "M3ua: Total PDUs Qd in ASP cong Q Upwrds",strlen("M3ua: Total PDUs Qd in ASP cong Q Upwrds"));
  }
  else if( strncmp( "8.11.285", oid, strlen("8.11.285")) == 0)
  {
    strncpy(oName, "M3ua: Total PDUs Qd in AS pending Q Upwrds",strlen("M3ua: Total PDUs Qd in AS pending Q Upwrds"));
  }
  else if( strncmp( "8.11.286", oid, strlen("8.11.286")) == 0)
  {
    strncpy(oName, "M3ua: Total PDUs dropped-No Rte Dnwrds",strlen("M3ua: Total PDUs dropped-No Rte Dnwrds"));
  }
  else if( strncmp( "8.11.287", oid, strlen("8.11.287")) == 0)
  {
    strncpy(oName, "M3ua: Total PDUs dropped-PC unavail Dnwrds",strlen("M3ua: Total PDUs dropped-PC unavail Dnwrds"));
  }
  else if( strncmp( "8.11.288", oid, strlen("8.11.288")) == 0)
  {
    strncpy(oName, "M3ua: Total PDUs dropped-PC Cong Dnwrds",strlen("M3ua: Total PDUs dropped-PC Cong Dnwrds"));
  }
  else if( strncmp( "8.11.289", oid, strlen("8.11.289")) == 0)
  {
    strncpy(oName, "M3ua: Total PDUs dropped-No PSP avail Dnwrds",strlen("M3ua: Total PDUs dropped-No PSP avail Dnwrds"));
  }
  else if( strncmp( "8.11.290", oid, strlen("8.11.290")) == 0)
  {
    strncpy(oName, "M3ua: Total PDUs dropped-No NSAP avail Dnwrds",strlen("M3ua: Total PDUs dropped-No NSAP avail Dnwrds"));
  }
  else if( strncmp( "8.11.291", oid, strlen("8.11.291")) == 0)
  {
    strncpy(oName, "M3ua: Total PDUs dropped-Load sharing failed Dnwrds",strlen("M3ua: Total PDUs dropped-Load sharinf failed Dnwrds"));
  }
  else if( strncmp( "8.11.292", oid, strlen("8.11.292")) == 0)
  {
    strncpy(oName, "M3ua: Total PDUs dropped-Msg handler failed Dnwrds",strlen("M3ua: Total PDUs dropped-Msg handler failed Dnwrds"));
  }
  else if( strncmp( "8.11.293", oid, strlen("8.11.293")) == 0)
  {
    strncpy(oName, "M3ua: Total PDUs Qd in ASP cong Q Dnwrds",strlen("M3ua: Total PDUs Qd in ASP cong Q Dnwrds"));
  }
  else if( strncmp( "8.11.294", oid, strlen("8.11.294")) == 0)
  {
    strncpy(oName, "M3ua: Total PDUs Qd in AS pending Q Dnwrds",strlen("M3ua: Total PDUs Qd in AS pending Q Dnwrds"));
  } //M3ua oids end here


  else if( strncmp( "8.11.310", oid, strlen("8.11.310")) == 0)
  {
    strncpy(oName, "Sctp: Total Chunks Tx",strlen("Sctp: Total Chunks Tx"));
  }
  else if( strncmp( "8.11.311", oid, strlen("8.11.311")) == 0)
  {
    strncpy(oName, "Sctp: Total Chunks Rx",strlen("Sctp: Total Chunks Rx"));
  }
  else if( strncmp( "8.11.312", oid, strlen("8.11.312")) == 0)
  {
    strncpy(oName, "Sctp: Total Bytes Rx",strlen("Sctp: Total Bytes Rx"));
  }//SCTP ends here

  else if( strncmp( "8.11.330", oid, strlen("8.11.330")) == 0)
  {
    strncpy(oName, "Mtp3: Total UsrPart Unavail Msg Rx",strlen("Mtp3: Total UsrPart Unavail Msg Rx"));
  }
  else if( strncmp( "8.11.331", oid, strlen("8.11.331")) == 0)
  {
    strncpy(oName, "Mtp3: Total UsrPart Unavail Msg Tx",strlen("Mtp3: Total UsrPart Unavail Msg Tx"));
  }
  else if( strncmp( "8.11.332", oid, strlen("8.11.332")) == 0)
  {
    strncpy(oName, "Mtp3: Total Traffic restrt allowd Tx",strlen("Mtp3: Total Traffic restrt allowd Tx"));
  }
  else if( strncmp( "8.11.333", oid, strlen("8.11.333")) == 0)
  {
    strncpy(oName, "Mtp3: Total Traffic restrt allowd Rx",strlen("Mtp3: Total Traffic restrt allowd Rx"));
  }
  else if( strncmp( "8.11.336", oid, strlen("8.11.336")) == 0)
  {
    strncpy(oName, "Mtp3: Total MSU droppd due to Route Err",strlen("Mtp3: Total MSU droppd due to Route Err"));
  }
  else if( strncmp( "8.11.337", oid, strlen("8.11.337")) == 0)
  {
    strncpy(oName, "Mtp3: Lnk-Total Changeover order Tx",strlen("Mtp3: Lnk-Total Changeover order Tx"));
  }
  else if( strncmp( "8.11.338", oid, strlen("8.11.338")) == 0)
  {
    strncpy(oName, "Mtp3: Lnk-Total Changeover order Rx",strlen("Mtp3: Lnk-Total Changeover order Rx"));
  }
  else if( strncmp( "8.11.339", oid, strlen("8.11.339")) == 0)
  {
    strncpy(oName, "Mtp3: Lnk-Total Changeback Tx",strlen("Mtp3: Lnk-Total Changeback Tx"));
  } 
  else if( strncmp( "8.11.340", oid, strlen("8.11.340")) == 0)
  {
    strncpy(oName, "Mtp3: Lnk-Total Changeback Rx",strlen("Mtp3: Lnk-Total Changeback Rx"));
  }
  else if( strncmp( "8.11.341", oid, strlen("8.11.341")) == 0)
  {
    strncpy(oName, "Mtp3: Lnk-Total Emergency Changeover Tx",strlen("Mtp3: Lnk-Total Emergency Changeover Tx"));
  }
  else if( strncmp( "8.11.342", oid, strlen("8.11.342")) == 0)
  {
    strncpy(oName, "Mtp3: Lnk-Total Emergency Changeover Rx",strlen("Mtp3: Lnk-Total Emergency Changeover Rx"));
  }
  else if( strncmp( "8.11.343", oid, strlen("8.11.343")) == 0)
  {
    strncpy(oName, "Mtp3: Lnk-Total Link inhibit Tx",strlen("Mtp3: Lnk-Total Link inhibit Tx"));
  }
  else if( strncmp( "8.11.344", oid, strlen("8.11.344")) == 0)
  {
    strncpy(oName, "Mtp3: Lnk-Total Link inhibit Rx",strlen("Mtp3: Lnk-Total Link inhibit Rx"));
  }
  else if( strncmp( "8.11.345", oid, strlen("8.11.345")) == 0)
  {
    strncpy(oName, "Mtp3: Lnk-Total Link Uninhibit Tx",strlen("Mtp3: Lnk-Total Link Uninhibit Tx"));
  }
  else if( strncmp( "8.11.346", oid, strlen("8.11.346")) == 0)
  {
    strncpy(oName, "Mtp3: Lnk-Total Link Uninhibit Rx",strlen("Mtp3: Lnk-Total Link Uninhibit Rx"));
  }
  else if( strncmp( "8.11.347", oid, strlen("8.11.347")) == 0)
  {
    strncpy(oName, "Mtp3: Lnk-Total Msg dropped due to Err",strlen("Mtp3: Lnk-Total Msg dropped due to Err"));
  }
  else if( strncmp( "8.11.348", oid, strlen("8.11.348")) == 0)
  {
    strncpy(oName, "Mtp3: Lnk-Total MSUs dropped due to cong",strlen("Mtp3: Lnk-Total MSUs dropped due to cong"));
  }
  else if( strncmp( "8.11.349", oid, strlen("8.11.349")) == 0)
  {
    strncpy(oName, "Mtp3: Lnk-Total MSU Tx",strlen("Mtp3: Lnk-Total MSU Tx"));
  }
  else if( strncmp( "8.11.350", oid, strlen("8.11.350")) == 0)
  {
    strncpy(oName, "Mtp3: Lnk-Total MSU Rx",strlen("Mtp3: Lnk-Total MSU Rx"));
  }
  else if( strncmp( "8.11.351", oid, strlen("8.11.351")) == 0)
  {
    strncpy(oName, "Mtp3: Lnk-Cong1",strlen("Mtp3: Lnk-Cong1"));
  }
  else if( strncmp( "8.11.352", oid, strlen("8.11.352")) == 0)
  {
    strncpy(oName, "Mtp3: Lnk-Cong2",strlen("Mtp3: Lnk-Cong2"));
  }
  else if( strncmp( "8.11.353", oid, strlen("8.11.353")) == 0)
  {
    strncpy(oName, "Mtp3: Lnk-Cong3",strlen("Mtp3: Lnk-Cong3"));
  }
  else if( strncmp( "8.11.354", oid, strlen("8.11.354")) == 0)
  {
    strncpy(oName, "Mtp3: Lnk-Time Duration for Link Unavail",strlen("Mtp3: Lnk-Time Duration for Link Unavail"));
  }
  else if( strncmp( "8.11.355", oid, strlen("8.11.355")) == 0)
  {
    strncpy(oName, "Mtp3: Lnk-Time Duration for Link Cong",strlen("Mtp3: Lnk-Time Duration for Link Cong"));
  }
  else if( strncmp( "8.11.356", oid, strlen("8.11.356")) == 0)
  {
    strncpy(oName, "Mtp3: Lnk-Total Invalid PDU Rx",strlen("Mtp3: Lnk-Total Invalid PDU Rx"));
  }

  else if( strncmp( "8.11.361", oid, strlen("8.11.361")) == 0)
  {
    strncpy(oName, "Mtp3: Rte-DPC",strlen("Mtp3: Rte-DPC"));
  }
  else if( strncmp( "8.11.362", oid, strlen("8.11.362")) == 0)
  {
    strncpy(oName, "Mtp3: Rte-User part switch",strlen("Mtp3: Rte-User part switch"));
  }
  else if( strncmp( "8.11.363", oid, strlen("8.11.363")) == 0)
  {
    strncpy(oName, "Mtp3: Rte-Transfer prohibited Tx",strlen("Mtp3: Rte-Transfer prohibited Tx"));
  }
  else if( strncmp( "8.11.364", oid, strlen("8.11.364")) == 0)
  {
    strncpy(oName, "Mtp3: Rte-Transfer prohibited Rx",strlen("Mtp3: Rte-Transfer prohibited Rx"));
  }
  else if( strncmp( "8.11.365", oid, strlen("8.11.365")) == 0)
  {
    strncpy(oName, "Mtp3: Rte-Transfer restrict Tx",strlen("Mtp3: Rte-Transfer restrict Tx"));
  }
  else if( strncmp( "8.11.366", oid, strlen("8.11.366")) == 0)
  {
    strncpy(oName, "Mtp3: Rte-Transfer restrict Rx",strlen("Mtp3: Rte-Transfer restrict Rx"));
  }
  else if( strncmp( "8.11.367", oid, strlen("8.11.367")) == 0)
  {
    strncpy(oName, "Mtp3: Rte-Transfer allowd Tx",strlen("Mtp3: Rte-Transfer allowd Tx"));
  }
  else if( strncmp( "8.11.368", oid, strlen("8.11.368")) == 0)
  {
    strncpy(oName, "Mtp3: Rte-Transfer allowd Rx",strlen("Mtp3: Rte-Transfer allowd Rx"));
  }
  else if( strncmp( "8.11.369", oid, strlen("8.11.369")) == 0)
  {
    strncpy(oName, "Mtp3: Rte-Unavailable",strlen("Mtp3: Rte-Unavailable"));
  }
  else if( strncmp( "8.11.370", oid, strlen("8.11.370")) == 0)
  {
    strncpy(oName, "Mtp3: Rte-Route Unavail duration",strlen("Mtp3: Rte-Route Unavail duration"));
  }
  else if( strncmp( "8.11.371", oid, strlen("8.11.371")) == 0)
  {
    strncpy(oName, "Mtp3: Rte-Total USN Msg Rx",strlen("Mtp3: Rte-Total USN Msg Rx"));
  }//USN->unallocated signaling no
  else if( strncmp( "101.102.1", oid, strlen("101.102.1")) == 0)
  {
    strncpy(oName, "CreateTcapSessionMsg", strlen("CreateTcapSessionMsg"));
  }
  else if( strncmp( "101.102.2", oid, strlen("101.102.2")) == 0)
  {
    strncpy(oName, "TcapCallSeqAckMsg", strlen("TcapCallSeqAckMsg"));
  }
  else if( strncmp( "101.102.3", oid, strlen("101.102.3")) == 0)
  {
    strncpy(oName, "UpdateTcapSessionMsg", strlen("UpdateTcapSessionMsg"));
  }
  else if( strncmp( "101.102.4", oid, strlen("101.102.4")) == 0)
  {
    strncpy(oName, "CloseTcapSessionMsg", strlen("CloseTcapSessionMsg"));
  }
  else if( strncmp( "101.102.6", oid, strlen("101.102.6")) == 0)
  {
    strncpy(oName, "FtDeleteCallMsg", strlen("FtDeleteCallMsg"));
  }
  else if( strncmp( "101.102.7", oid, strlen("101.102.7")) == 0)
  {
    strncpy(oName, "LoadDistMsg", strlen("LoadDistMsg"));
  }
  else if( strncmp( "101.102.8", oid, strlen("101.102.8")) == 0)
  {
    strncpy(oName, "SasHbFailureMsg", strlen("SasHbFailureMsg"));
  }
  else if( strncmp( "101.102.9", oid, strlen("101.102.9")) == 0)
  {
    strncpy(oName, "TcapDlgInfoMsg", strlen("TcapDlgInfoMsg"));
  }
  else if( strncmp( "101.102.10", oid, strlen("101.102.10")) == 0)
  {
    strncpy(oName, "StackConfigMsg", strlen("StackConfigMsg"));
  }
  else if( strncmp( "101.102.11", oid, strlen("101.102.11")) == 0)
  {
    strncpy(oName, "AddLinkMsg", strlen("AddLinkMsg"));
  }
  else if( strncmp( "101.102.12", oid, strlen("101.102.12")) == 0)
  {
    strncpy(oName, "DelLinkMsg", strlen("DelLinkMsg"));
  }
  else if( strncmp( "101.102.13", oid, strlen("101.102.13")) == 0)
  {
    strncpy(oName, "AddLinksetMsg", strlen("AddLinksetMsg"));
  }
  else if( strncmp( "101.102.14", oid, strlen("101.102.14")) == 0)
  {
    strncpy(oName, "DelLinksetMsg", strlen("DelLinksetMsg"));
  }
  else if( strncmp( "101.102.15", oid, strlen("101.102.15")) == 0)
  {
    strncpy(oName, "AddNwMsg", strlen("AddNwMsg"));
  }
  else if( strncmp( "101.102.16", oid, strlen("101.102.16")) == 0)
  {
    strncpy(oName, "DelNwMsg", strlen("DelNwMsg"));
  }
  else if( strncmp( "101.102.17", oid, strlen("101.102.17")) == 0)
  {
    strncpy(oName, "AddRouteMsg", strlen("AddRouteMsg"));
  }
  else if( strncmp( "101.102.18", oid, strlen("101.102.18")) == 0)
  {
    strncpy(oName, "DelRouteMsg", strlen("DelRouteMsg"));
  }
  else if( strncmp( "101.102.19", oid, strlen("101.102.19")) == 0)
  {
    strncpy(oName, "AddSsnMsg", strlen("AddSsnMsg"));
  }
  else if( strncmp( "101.102.20", oid, strlen("101.102.20")) == 0)
  {
    strncpy(oName, "DelSsnMsg", strlen("DelSsnMsg"));
  }
  else if( strncmp( "101.102.21", oid, strlen("101.102.21")) == 0)
  {
    strncpy(oName, "AddUsrPartMsg", strlen("AddUsrPartMsg"));
  }
  else if( strncmp( "101.102.22", oid, strlen("101.102.22")) == 0)
  {
    strncpy(oName, "DelUsrPartMsg", strlen("DelUsrPartMsg"));
  }
  else if( strncmp( "101.102.23", oid, strlen("101.102.23")) == 0)
  {
    strncpy(oName, "AssocDownMsg", strlen("AssocDownMsg"));
  }
  else if( strncmp( "101.102.24", oid, strlen("101.102.24")) == 0)
  {
    strncpy(oName, "AssocUpMsg", strlen("AssocUpMsg"));
  }
  else if( strncmp( "101.102.25", oid, strlen("101.102.25")) == 0)
  {
    strncpy(oName, "AddPsMsg", strlen("AddPsMsg"));
  }
  else if( strncmp( "101.102.26", oid, strlen("101.102.26")) == 0)
  {
    strncpy(oName, "DelPsMsg", strlen("DelPsMsg"));
  }
  else if( strncmp( "101.102.27", oid, strlen("101.102.27")) == 0)
  {
    strncpy(oName, "AddEpMsg", strlen("AddEpMsg"));
  }
  else if( strncmp( "101.102.28", oid, strlen("101.102.28")) == 0)
  {
    strncpy(oName, "DelEpMsg", strlen("DelEpMsg"));
  }
  else if( strncmp( "101.102.29", oid, strlen("101.102.29")) == 0)
  {
    strncpy(oName, "AddPspMsg", strlen("AddPspMsg"));
  }
  else if( strncmp( "101.102.30", oid, strlen("101.102.30")) == 0)
  {
    strncpy(oName, "DelPspMsg", strlen("DelPspMsg"));
  }
  else if( strncmp( "101.102.31", oid, strlen("101.102.31")) == 0)
  {
    strncpy(oName, "AddRuleMsg", strlen("AddRuleMsg"));
  }
  else if( strncmp( "101.102.32", oid, strlen("101.102.32")) == 0)
  {
    strncpy(oName, "DelRuleMsg", strlen("DelRuleMsg"));
  }
  else if( strncmp( "101.102.33", oid, strlen("101.102.33")) == 0)
  {
    strncpy(oName, "AddGtAddrMapMsg", strlen("AddGtAddrMapMsg"));
  }
  else if( strncmp( "101.102.34", oid, strlen("101.102.34")) == 0)
  {
    strncpy(oName, "DelGtAddrMapMsg", strlen("DelGtAddrMapMsg"));
  }
  else if( strncmp( "101.102.35", oid, strlen("101.102.35")) == 0)
  {
    strncpy(oName, "ModLinkMsg", strlen("ModLinkMsg"));
  }
  else if( strncmp( "101.102.36", oid, strlen("101.102.36")) == 0)
  {
    strncpy(oName, "ModLinksetMsg", strlen("ModLinksetMsg"));
  }
  else if( strncmp( "101.102.37", oid, strlen("101.102.37")) == 0)
  {
    strncpy(oName, "ModPsMsg", strlen("ModPsMsg"));
  }
  else if( strncmp( "101.102.38", oid, strlen("101.102.38")) == 0)
  {
    strncpy(oName, "ConfigStatusMsg", strlen("ConfigStatusMsg"));
  }
  else if( strncmp( "101.101.1", oid, strlen("101.101.1")) == 0)
  {
    strncpy(oName, "TotalSipMsgRx", strlen("TotalSipMsgRx"));
  }
  else if( strncmp( "101.101.2", oid, strlen("101.101.2")) == 0)
  {
    strncpy(oName, "TotalSipMsgTx", strlen("TotalSipMsgTx"));
  }
  else if( strncmp( "101.101.3", oid, strlen("101.101.3")) == 0)
  {
    strncpy(oName, "TotalInviteRx", strlen("TotalInviteRx"));
  }
  else if( strncmp( "101.101.4", oid, strlen("101.101.4")) == 0)
  {
    strncpy(oName, "TotalInviteTx", strlen("TotalInviteTx"));
  }
  else if( strncmp( "101.101.5", oid, strlen("101.101.5")) == 0)
  {
    strncpy(oName, "TotalNotifyRx", strlen("TotalNotifyRx"));
  }
  else if( strncmp( "101.101.6", oid, strlen("101.101.6")) == 0)
  {
    strncpy(oName, "TotalNotifyRejected", strlen("TotalNotifyRejected"));
  }
  else if( strncmp( "101.101.7", oid, strlen("101.101.7")) == 0)
  {
    strncpy(oName, "TtoalNotifyTx", strlen("TtoalNotifyTx"));
  }
  else if( strncmp( "101.101.8", oid, strlen("101.101.8")) == 0)
  {
    strncpy(oName, "TotalNotifySentRej", strlen("TotalNotifySentRej"));
  }
  else if( strncmp( "101.101.9", oid, strlen("101.101.9")) == 0)
  {
    strncpy(oName, "TotalInfoRx", strlen("TotalInfoRx"));
  }
  else if( strncmp( "101.101.10", oid, strlen("101.101.10")) == 0)
  {
    strncpy(oName, "TotalInfoRej", strlen("TotalInfoRej"));
  }
  else if( strncmp( "101.101.11", oid, strlen("101.101.11")) == 0)
  {
    strncpy(oName, "TotalInfoTx", strlen("TotalInfoTx"));
  }
  else if( strncmp( "101.101.12", oid, strlen("101.101.12")) == 0)
  {
    strncpy(oName, "TotalInfoSentRej", strlen("TotalInfoSentRej"));
  }
  else if( strncmp( "8.11.80", oid, strlen("8.11.80")) == 0)
  {
    strncpy(oName, "QWPTx", strlen("QWPTx"));
  }
  else if( strncmp( "8.11.81", oid, strlen("8.11.81")) == 0)
  {
    strncpy(oName, "QWoPTx", strlen("QWoPTx"));
  }
  else if( strncmp( "8.11.82", oid, strlen("8.11.82")) == 0)
  {
    strncpy(oName, "CWPTx", strlen("CWPTx"));
  }
  else if( strncmp( "8.11.83", oid, strlen("8.11.83")) == 0)
  {
    strncpy(oName, "CWoPTx", strlen("CWoPTx"));
  }
  else if( strncmp( "8.11.84", oid, strlen("8.11.84")) == 0)
  {
    strncpy(oName, "Rsptx", strlen("Rsptx"));
  }
  else if( strncmp( "8.11.85", oid, strlen("8.11.85")) == 0)
  {
    strncpy(oName, "QWPRx", strlen("QWPRx"));
  }
  else if( strncmp( "8.11.86", oid, strlen("8.11.86")) == 0)
  {
    strncpy(oName, "QWoPRx", strlen("QWoPRx"));
  }
  else if( strncmp( "8.11.87", oid, strlen("8.11.87")) == 0)
  {
    strncpy(oName, "CWPRx", strlen("CWPRx"));
  }
  else if( strncmp( "8.11.88", oid, strlen("8.11.88")) == 0)
  {
    strncpy(oName, "CWoPRx", strlen("CWoPRx"));
  }
  else if( strncmp( "8.11.89", oid, strlen("8.11.89")) == 0)
  {
    strncpy(oName, "RspRx", strlen("RspRx"));
  }
  else if( strncmp( "101.102.5", oid, strlen("101.102.5")) == 0)
  {
    strncpy(oName, "CallBkupMsg", strlen("CallBkupMsg"));
  }
  else
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, 
			"Unknown Oid <%s> received in getOidName()",oid);
    return NULL;
  }
  return oName; 
}


int INGwIfrSmStatMgr::startDeferredProcessing(string& aOutput)
{
  logger.logINGwMsg(false, TRACE_FLAG, 0, "startDeferredProcessing: IN");
  // For all parameters (until an empty one is met), get the ems value
  // and print each of them against the oid of the parameter.
  for(int i = 0; i < miMaxNumberOfStatParams; ++i)
  {
    mStatParamList[i].lock();
    if(mStatParamList[i].empty())
    {
      mStatParamList[i].unlock();
      break;
    } // end of if
    if(!mStatParamList[i].isEmsParam())
    {
      mStatParamList[i].unlock();
      continue;
    }

    int curValue = -1;
    int avgValue = -1;
    int maxValue = -1;
    int minValue = -1;
    char tempStr[128];

    mStatParamList[i].getValue(curValue, avgValue, maxValue, minValue);
    mStatParamList[i].checkThreshold();

    aOutput += mStatParamList[i].getOid();

    sprintf(tempStr, "%d", curValue);
    aOutput += ": CurrentValue <";
    aOutput += tempStr;

    sprintf(tempStr, "%d", avgValue);
    aOutput += ">, AverageValue <";
    aOutput += tempStr;

    sprintf(tempStr, "%d", minValue);
    aOutput += ">, MinimumValue <";
    aOutput += tempStr;

    sprintf(tempStr, "%d", maxValue);
    aOutput += ">, MaximumValue <";
    aOutput += tempStr;
    aOutput += ">\n";

    mStatParamList[i].unlock();
  } // end of for

  //printf("%s", aOutput.c_str());

  logger.logINGwMsg(false, TRACE_FLAG, 0, "startDeferredProcessing: OUT");
  return 0;
} // end of startDeferredProcessing

int INGwIfrSmStatMgr::startDisplayProcessing(string& aOid, string& aOutput)
{
  logger.logINGwMsg(false, TRACE_FLAG, 0, "startDisplayProcessing: IN");

  int i = getParamIndex(aOid, false);
  if(i < 0)
  {
    aOutput += "Could not find parameter with oid <";
    aOutput += aOid;
    aOutput += ">";
    return i;
  }

  mStatParamList[i].lock();
  if(mStatParamList[i].empty())
  {
    mStatParamList[i].unlock();
    return -1;
  }

  int curValue = -1;
  int avgValue = -1;
  int maxValue = -1;
  int minValue = -1;
  char tempStr[128];

  mStatParamList[i].getValue(curValue, avgValue, maxValue, minValue);

  aOutput += mStatParamList[i].getOid();

  sprintf(tempStr, "%d", curValue);
  aOutput += ": CurrentValue <";
  aOutput += tempStr;

  sprintf(tempStr, "%d", avgValue);
  aOutput += ">, AverageValue <";
  aOutput += tempStr;

  sprintf(tempStr, "%d", minValue);
  aOutput += ">, MinimumValue <";
  aOutput += tempStr;

  sprintf(tempStr, "%d", maxValue);
  aOutput += ",> MaximumValue <";
  aOutput += tempStr;
  aOutput += ">\n";

	// Since we are Diplaying just One parameter do the dump() of the Oid also

	mStatParamList[i].dump(aOutput);
	
  mStatParamList[i].unlock();
  logger.logINGwMsg(false, TRACE_FLAG, 0, "startDisplayProcessing: OUT");
  return 0;
}

int INGwIfrSmStatMgr::startStatsDisplayProcessing(string& lfile)
{
  ostrstream aOutput;
  logger.logINGwMsg(false, TRACE_FLAG, 0, "startStatsDisplayProcessing: IN");
  // For all parameters (until an empty one is met), get the ems value
  // and print each of them against the oid of the parameter.
  for(int i = 0; i < miMaxNumberOfStatParams; ++i)
  {
    mStatParamList[i].lock();
    if(mStatParamList[i].empty())
    {
      mStatParamList[i].unlock();
      break;
    } // end of if

    int curValue = -1;
    int avgValue = -1;
    int maxValue = -1;
    int minValue = -1;
    char tempStr[128];
    char *oidName=NULL;

    mStatParamList[i].getValue(curValue, avgValue, maxValue, minValue);

    string oidVal = mStatParamList[i].getOid();
    oidName = getOidName(oidVal.c_str());
    if(oidName != NULL)
    {
      aOutput << oidName;

      sprintf(tempStr, "%d", curValue);
      aOutput << ": <";
      aOutput << tempStr;
      aOutput << ">";
      aOutput << "\n";
    }
    mStatParamList[i].unlock();
  } // end of for

  logger.logINGwMsg(false, TRACE_FLAG, 0, "startStatsDisplayProcessing: OUT ");
    char* data = aOutput.str();
  fstream file(lfile.c_str(), ios::out);
	if(true != file.is_open())
	{
	  logger.logMsg(ERROR_FLAG, 0, "Error opening file ");
	}
	else
	{
	  file << data;
	  file.flush();
		file.close();

	}
	delete [] data;
  return 1;
} 



int INGwIfrSmStatMgr::startDisplayProcessing(string& aOutput)
{
  logger.logINGwMsg(false, TRACE_FLAG, 0, "startDisplayProcessing: IN");
  // For all parameters (until an empty one is met), get the ems value
  // and print each of them against the oid of the parameter.
  for(int i = 0; i < miMaxNumberOfStatParams; ++i)
  {
    mStatParamList[i].lock();
    if(mStatParamList[i].empty())
    {
      mStatParamList[i].unlock();
      break;
    } // end of if

    int curValue = -1;
    int avgValue = -1;
    int maxValue = -1;
    int minValue = -1;
    char tempStr[128];

    mStatParamList[i].getValue(curValue, avgValue, maxValue, minValue);

    aOutput += mStatParamList[i].getOid();

    sprintf(tempStr, "%d", curValue);
    aOutput += ": CurrentValue <";
    aOutput += tempStr;

    sprintf(tempStr, "%d", avgValue);
    aOutput += ">, AverageValue <";
    aOutput += tempStr;

    sprintf(tempStr, "%d", minValue);
    aOutput += ">, MinimumValue <";
    aOutput += tempStr;

    sprintf(tempStr, "%d", maxValue);
    aOutput += ">, MaximumValue <";
    aOutput += tempStr;
    aOutput += ">\n";

    mStatParamList[i].unlock();
  } // end of for

  logger.logINGwMsg(false, TRACE_FLAG, 0, "startDisplayProcessing: OUT <%s>",aOutput.c_str());

  return 0;
} // end of startDeferredProcessing

void INGwIfrSmStatMgr::dump(string& aOutput)
{
  char tempStr[128];

  aOutput += "================================ BEGIN STAT DUMP===============================\n";
  aOutput += "INDEX   OID\n";
  aOutput += "-------------------------------\n";

  pthread_rwlock_rdlock(&mParamIndexMapLock);
  for(ParamIndexMap::iterator iter = mParamIndexMap.begin();
      iter != mParamIndexMap.end(); iter++)
  {
    sprintf(tempStr, "%4d:  ", (*iter).second);
    aOutput += tempStr;
    aOutput += (*iter).first;
    aOutput += "\n";
  }
  pthread_rwlock_unlock(&mParamIndexMapLock);

  aOutput += "\n\n";

  for(int i = 0; i < miMaxNumberOfStatParams; ++i)
  {
    if(mStatParamList[i].empty())
      break;

    sprintf(tempStr, "%4d : ", i);
    aOutput += "Index ";
    aOutput += tempStr;
    mStatParamList[i].lock();
    mStatParamList[i].dump(aOutput);
    mStatParamList[i].unlock();
  }

  aOutput += "================================= END STAT DUMP================================\n";
}

INGwIfrSmXmlParse::INGwIfrSmXmlParse()
{
  pthread_mutex_init(&mLock, NULL);
  reset();
}

INGwIfrSmXmlParse::~INGwIfrSmXmlParse()
{
   pthread_mutex_destroy(&mLock);
}

void INGwIfrSmXmlParse::reset()
{
  parser = NULL;
  miParsed = false;
}

void INGwIfrSmXmlParse::lock()
{
  pthread_mutex_lock(&mLock);
}

void INGwIfrSmXmlParse::unlock()
{
  pthread_mutex_unlock(&mLock);
}

bool INGwIfrSmXmlParse::init(string aFileName, DOMNode*& aNode)
{
  lock();
  reset();

  AbstractDOMParser::ValSchemes valScheme = AbstractDOMParser::Val_Auto;
  bool                       doNamespaces       = false;
  bool                       doSchema           = false;
  bool                       schemaFullChecking = false;
  bool                       doList = false;
  bool                       errorOccurred = false;
  bool                       recognizeNEL = false;
  char                       localeStr[64];
  localeStr[0] = '\0';

  // Initialize the XML4C system
  try
  {
    if(strlen(localeStr))
    {
      XMLPlatformUtils::Initialize(localeStr);
    }
    else
    {
      XMLPlatformUtils::Initialize();
    }

    if(recognizeNEL)
    {
      XMLPlatformUtils::recognizeNEL(recognizeNEL);
    }
  }
  catch(const XMLException& toCatch)
  {
    StatString_var msgvar = XMLString::transcode(toCatch.getMessage());
    logger.logINGwMsg(false, ERROR_FLAG, 0, "Error during initialization: <%s>", 
                    msgvar.in());
    return false;
  }

  DOMDocument* doc = NULL;
  const XMLCh gLS[] = { chLatin_L, chLatin_S, chNull };
  DOMImplementation *impl =DOMImplementationRegistry::getDOMImplementation(gLS);
  parser = ((DOMImplementationLS*)impl)->createDOMBuilder
    (DOMImplementationLS::MODE_SYNCHRONOUS, 0);

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

  // the input is a list file
  fin.open(aFileName.c_str());

  if(fin.fail())
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "init: Could not open file <%s>", aFileName.c_str());
    return false;
  }

  try
  {
    // reset document pool
    parser->resetDocumentPool();

    const unsigned long startMillis = XMLPlatformUtils::getCurrentMillis();
    doc = parser->parseURI(aFileName.c_str());
    const unsigned long endMillis = XMLPlatformUtils::getCurrentMillis();
    unsigned long duration = endMillis - startMillis;
    logger.logINGwMsg(false, ALWAYS_FLAG, 0, "init: duration: <%u>", duration);
    miParsed = true;
  }
  catch(const XMLException& toCatch)
  {
    StatString_var msgvar = XMLString::transcode(toCatch.getMessage());
    logger.logINGwMsg(false, ERROR_FLAG, 0, "Error during parsing: <%s>: <%s>", 
                    aFileName.c_str(), msgvar.in());
    return false;
  }
  catch(const DOMException& toCatch)
  {
    const unsigned int maxChars = 2047;
    XMLCh errText[maxChars + 1];

    logger.logINGwMsg(false, ERROR_FLAG, 0, "init: Error during parsing <%s>", aFileName.c_str());

    if(DOMImplementation::loadDOMExceptionMsg(toCatch.code, errText, maxChars))
    {
       StatString_var errtextvar = XMLString::transcode(errText);
      logger.logINGwMsg(false, ERROR_FLAG, 0, "init: Error message <%s>", 
                      errtextvar.in());
    }

    return false;
  }
  catch(...)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "init: Unexpected exception during parsing <%s>", aFileName.c_str());
    return false;
  }

  if(doc)
  {
    DOMNode* rootNode = (DOMNode*)doc->getDocumentElement();
    aNode = rootNode;
    return true;
  }

  return false;
} // end of init method

bool INGwIfrSmXmlParse::release()
{
  if(miParsed)
  {
    parser->release();
    XMLPlatformUtils::Terminate();
  }

  if(!fin.fail())
    fin.close();

  unlock();

  return true;
}

int INGwIfrSmStatMgr::getStatLevel()
{
  return miStatLevel;
}

void INGwIfrSmStatMgr::setStatLevel(int aLevel)
{
  miStatLevel = aLevel;
}

void INGwIfrSmStatMgr::processTelnetCommand
  (const string& arInputStr, char** apcOutput, int& size)
{
  logger.logINGwMsg(false, TRACE_FLAG, 0, "processTelnetCommand: IN");

  string outstr = "";

  // Break the command into parts
  char* command = new char[arInputStr.size() + 1];
  strcpy(command, arInputStr.c_str());

  char separator[] = " \t\n\r";
  char* brkt;
  char* paramName = NULL;
  char* word = strtok_r(command, separator, &brkt);

  if(!word || strcmp("stat", word))
  {
    outstr = "Unrecognized command <";
    outstr += arInputStr;
    outstr += ">";
  }
  else
  {
    char* argv1 = strtok_r(NULL, separator, &brkt);
    if(!argv1)
    {
      outstr = "Unrecognized command <";
      outstr += arInputStr;
      outstr += ">";
    }
    else
    {
      if(!strcmp(argv1, "dump"))
      {
        dump(outstr);
      }
      else if(!strcmp(argv1, "display"))
      {
        char* argv2 = strtok_r(NULL, separator, &brkt);
        if(!argv2)
        {
          outstr = "Unrecognized command <";
          outstr += arInputStr;
          outstr += ">";
        }
        else if(!strcmp(argv2, "all"))
        {
          startDisplayProcessing(outstr);
        }
        else
        {
          string tmpStr(argv2);
          startDisplayProcessing(tmpStr, outstr);
        }
      }
      else if(!strcmp(argv1, "set-statlevel"))
      {
        char* argv2 = strtok_r(NULL, separator, &brkt);
        if(!argv2)
        {
          outstr = "Unrecognized command <";
          outstr += arInputStr;
          outstr += ">";
        }
        else
        {
          setStatLevel(atoi(argv2));
          char tempstr[128];
          sprintf(tempstr, "New stat level is <%d>", getStatLevel());
          outstr = tempstr;
        }
      }
      else if(!strcmp(argv1, "get-statlevel"))
      {
        int level = getStatLevel();
        char tempstr[32];
        sprintf(tempstr, "%d", level);
        outstr += tempstr;
      }
      else if(!strcmp(argv1, "reload"))
      {
        char* argv2 = strtok_r(NULL, separator, &brkt);
        if(!argv2)
        {
          outstr = "Unrecognized command <";
          outstr += arInputStr;
          outstr += ">";
        }
        else
        {
          bool status = loadStatistics(string(argv2));
          if(status)
            outstr = "Re-loaded statistics";
          else
            outstr = "Error!!: Could not completely reload the statistics";
        }
      }
    }
  }

  outstr += "\n";

  char* output = new char[outstr.size() + 1];
  strcpy(output, outstr.c_str());
  *apcOutput = output;
  size = outstr.size() + 1;

  logger.logINGwMsg(false, TRACE_FLAG, 0, "processTelnetCommand: OUT");
} // end of processTelnetCommand

void INGwIfrSmStatMgr::getValue
  (int aIndex, 
   int& aCurValue,
   int& aAvgValue, 
   int& aMinValue,
   int& aMaxValue)

{
  if(aIndex < 0 || aIndex >= miMaxNumberOfStatParams)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "getValue: index <%d> out of range", aIndex);
    return;
  }

  // Lock the required parameter.
  mStatParamList[aIndex].lock();
  mStatParamList[aIndex].getValue
    (aCurValue, aAvgValue, aMaxValue, aMinValue);
  mStatParamList[aIndex].unlock();
}

void INGwIfrSmStatMgr::getCurValue(int aIndex, int& aValue)
{
  if(aIndex < 0 || aIndex >= miMaxNumberOfStatParams)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "getCurValue: index <%d> out of range", aIndex);
    return;
  }

  int i1, i2, i3;
  // Lock the required parameter.
  mStatParamList[aIndex].lock();
  mStatParamList[aIndex].getValue
    (aValue, i1, i2, i3);
  mStatParamList[aIndex].unlock();
}

void INGwIfrSmStatMgr::getAvgValue(int aIndex, int& aValue)
{
  if(aIndex < 0 || aIndex >= miMaxNumberOfStatParams)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "getAvgValue: index <%d> out of range", aIndex);
    return;
  }

  int i1, i2, i3;
  // Lock the required parameter.
  mStatParamList[aIndex].lock();
  mStatParamList[aIndex].getValue
    (i1, aValue, i2, i3);
  mStatParamList[aIndex].unlock();
}

void INGwIfrSmStatMgr::getMinValue(int aIndex, int& aValue)
{
  if(aIndex < 0 || aIndex >= miMaxNumberOfStatParams)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "getMinValue: index <%d> out of range", aIndex);
    return;
  }

  int i1, i2, i3;
  // Lock the required parameter.
  mStatParamList[aIndex].lock();
  mStatParamList[aIndex].getValue
    (i1, i2, i3, aValue);
  mStatParamList[aIndex].unlock();
}

void INGwIfrSmStatMgr::getMaxValue(int aIndex, int& aValue)
{
  if(aIndex < 0 || aIndex >= miMaxNumberOfStatParams)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "getMaxValue: index <%d> out of range", aIndex);
    return;
  }

  int i1, i2, i3;
  // Lock the required parameter.
  mStatParamList[aIndex].lock();
  mStatParamList[aIndex].getValue
    (i1, i2, aValue, i3);
  mStatParamList[aIndex].unlock();
}

void INGwIfrSmStatMgr::getEmsParams(EmsParamMap &params)
{
   string oid;
   int currVal;
   int otherVal;

   for(int idx = 0; idx <  miMaxNumberOfStatParams; ++idx)
   {
      INGwIfrSmStatParam &currParam = mStatParamList[idx];

      currParam.lock();

      if(currParam.empty())
      {
         currParam.unlock();
         continue;
      }

      if(currParam.isEmsParam())
      {
         oid = currParam.getOid();
         currParam.getValue(currVal, otherVal, otherVal, otherVal);

         params[oid] = currVal;
      }

      currParam.unlock();
   }

   return;
}

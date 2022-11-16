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
//     File:     INGwSpSipHeaderPolicy.C
//
//     Desc:     Header manipulation management
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipProvider");

#include <INGwSipProvider/INGwSpSipHeaderPolicy.h>
#include <sstream>
#include <INGwInfraUtil/INGwIfrUtlAlgorithm.h>

#include <string>
#include <vector>
using namespace std;

namespace RSI_NSP_SIP
{

static const char *__ownerStr[]   = {"SLEE", "CCM", "SLEE_CCM"};
static const char *__displayStr[] = {"ENV", "INCOMING"};
static const char *__protocolStr[] = {"SIP", "TEL", "INCOMING"};
static const char *__userPartStr[] = {"ENV", "INCOMING"};
static const char *__hostPartStr[] = {"GW", "LOOPBACK", "INCOMING", "SELF"};
static const char *__paramStr[] = {"INCOMING", "ENV"};
static const char *__paramSleeStr[] = {"APPEND", "REPLACE", "IGNORE"};

Owner _getHPOwner(const string &val)
{
   if(strcasecmp(val.c_str(), "SLEE") == 0)
   {
      return OWNER_SLEE;
   }
   else if(strcasecmp(val.c_str(), "CCM") == 0)
   {
      return OWNER_CCM;
   }

   logger.logMsg(ERROR_FLAG, 0, "Wrong owner value [%s] defaulted to SLEE",
                 val.c_str());
   return OWNER_SLEE;
}

Owner _getHPContactUserOwner(const string &val)
{
   if(strcasecmp(val.c_str(), "SLEE") == 0)
   {
      return OWNER_SLEE;
   }
   else if(strcasecmp(val.c_str(), "CCM") == 0)
   {
      return OWNER_CCM;
   }
   else if(strcasecmp(val.c_str(), "SLEE_CCM") == 0)
   {
      return OWNER_SLEE_CCM;
   }

   logger.logMsg(ERROR_FLAG, 0, "Wrong owner value [%s] defaulted to SLEE",
                 val.c_str());
   return OWNER_SLEE;
}

DisplayTreatment _getHPDisplay(const string &val)
{
   if(strcasecmp(val.c_str(), "ENV") == 0)
   {
      return DISP_ENVIRONMENT;
   }
   else if(strcasecmp(val.c_str(), "INCOMING") == 0)
   {
      return DISP_INCOMING;
   }

   logger.logMsg(ERROR_FLAG, 0, "Wrong disp value [%s] defaulted to INCOMING",
                 val.c_str());
   return DISP_INCOMING;
}

ProtocolTreatment _getHPProtocol(const string &val)
{
   if(strcasecmp(val.c_str(), "SIP") == 0)
   {
      return PROTO_SIP;
   }
   else if(strcasecmp(val.c_str(), "TEL") == 0)
   {
      return PROTO_TEL;
   }
   else if(strcasecmp(val.c_str(), "INCOMING") == 0)
   {
      return PROTO_INCOMING;
   }

   logger.logMsg(ERROR_FLAG, 0, "Wrong protocol value [%s] defaulted to SIP",
                 val.c_str());
   return PROTO_SIP;
}

UserPartTreatment _getHPUser(const string &val)
{
   if(strcasecmp(val.c_str(), "ENV") == 0)
   {
      return USER_ENVIRONMENT;
   }
   else if(strcasecmp(val.c_str(), "INCOMING") == 0)
   {
      return USER_INCOMING;
   }

   logger.logMsg(ERROR_FLAG, 0, "Wrong userpart value [%s] defaulted to "
                                "INCOMING", val.c_str());
   return USER_INCOMING;
}

HostPartTreatment _getHPHost(const string &val)
{
   if(strcasecmp(val.c_str(), "GW") == 0)
   {
      return HOST_GW;
   }
   else if(strcasecmp(val.c_str(), "LOOPBACK") == 0)
   {
      return HOST_LOOPBACK;
   }
   else if(strcasecmp(val.c_str(), "INCOMING") == 0)
   {
      return HOST_INCOMING;
   }
   else if(strcasecmp(val.c_str(), "SELF") == 0)
   {
      return HOST_SELF;
   }

   logger.logMsg(ERROR_FLAG, 0, "Wrong host value [%s] defaulted to INCOMING",
                 val.c_str());
   return HOST_INCOMING;
}

ParamTreatment _getHPParam(const string &val)
{
   if(strcasecmp(val.c_str(), "ENV") == 0)
   {
      return PARAM_ENVIRONMENT;
   }
   else if(strcasecmp(val.c_str(), "INCOMING") == 0)
   {
      return PARAM_INCOMING;
   }

   logger.logMsg(ERROR_FLAG, 0, "Wrong param value [%s] defaulted to INCOMING",
                 val.c_str());
   return PARAM_INCOMING;
}

ParamSleeInputTreatment _getHPSleeParam(const string &val)
{
   if(strcasecmp(val.c_str(), "APPEND") == 0)
   {
      return PARAM_SLEE_INPUT_APPEND;
   }
   else if(strcasecmp(val.c_str(), "REPLACE") == 0)
   {
      return PARAM_SLEE_INPUT_REPLACE;
   }
   else if(strcasecmp(val.c_str(), "IGNORE") == 0)
   {
      return PARAM_SLEE_INPUT_IGNORE;
   }

   logger.logMsg(ERROR_FLAG, 0, "Wrong SleeParam value [%s] defaulted to "
                                "IGNORE", val.c_str());
   return PARAM_SLEE_INPUT_IGNORE;
}

INGwSpSipAddressDefault::INGwSpSipAddressDefault()
{
   _user = NULL;
   _userLen = 0;
   _param = NULL;
   _paramLen = 0;
}

INGwSpSipAddressDefault::~INGwSpSipAddressDefault()
{
   if(_user) 
   {
      delete []_user;
      _user = NULL;
   }

   _userLen = 0;

   if(_param)
   {
      for(int idx = 0; idx < _paramLen; idx++)
      {
         sip_freeSipParam(_param[idx]);
         _param[idx] = NULL;
      }

      delete []_param;
      _param = NULL;
   }

   _paramLen = 0;
}

const INGwSpSipAddressDefault & INGwSpSipAddressDefault::operator = 
                                             (const INGwSpSipAddressDefault &inData)
{
   setUser(inData._user, inData._userLen);

   if(_param)
   {
      for(int idx = 0; idx < _paramLen; idx++)
      {
         sip_freeSipParam(_param[idx]);
         _param[idx] = NULL;
      }

      delete []_param;
      _param = NULL;
   }

   _paramLen = inData._paramLen;

   if(_paramLen)
   {
      _param = new SipParam*[_paramLen];
      SipError err;

      for(int idx = 0; idx < _paramLen; idx++)
      {
         sip_initSipParam(_param + idx, &err);
         __sip_cloneSipParam(_param[idx], inData._param[idx], &err);
      }
   }
   
   return *this;
}

void INGwSpSipAddressDefault::setUser(const char *user, int userLen)
{
   if(user == NULL)
   {
      user = "";
      userLen = 0;
   }

   if(userLen == -1)
   {
      userLen = strlen(user);
   }

   if(_user)
   {
      delete []_user;
   }

   _user = new char[userLen + 1];
   strncpy(_user, user, userLen);

   _user[userLen] = '\0';
   _userLen = userLen;
}

const char *INGwSpSipAddressDefault::getUser(int &len) const
{
   len = _userLen;
   return _user;
}

void INGwSpSipAddressDefault::setParam(const char *paramStr, int paramLen)
{
   if(_param)
   {
      for(int idx = 0; idx < _paramLen; idx++)
      {
         sip_freeSipParam(_param[idx]);
         _param[idx] = NULL;
      }

      delete []_param;
      _param = NULL;
   }

   _paramLen = 0;

   if(paramLen == 0)
   {
      return;
   }

   typedef vector<string> StrVector;
   typedef StrVector::iterator StrVectorIt;

   StrVector strParams;
   string paramDelimiter(";");

   string inData(paramStr);
   INGwAlgTokenizer(inData, paramDelimiter, back_inserter(strParams));

   _paramLen = strParams.size();

   if(_paramLen == 0)
   {
      return;
   }

   _param = new SipParam*[_paramLen];

   int idx = 0;
   SipError err;

   for(StrVectorIt it = strParams.begin(); it != strParams.end(); it++, idx++)
   {
      sip_initSipParam(_param + idx, &err);

      string &nameValStr = (*it);

      StrVector nameVal;
      string nameValDelimiter("=");
      INGwAlgTokenizer(nameValStr, nameValDelimiter, back_inserter(nameVal));

      if(nameVal.size() != 2)
      {
         logger.logMsg(ERROR_FLAG, 0, 
                       "Configuration fault. [%s] is not valid param", 
                       paramStr);
         logger.logMsg(ALWAYS_FLAG, 0, "Quitting on configuration fault.");
         exit(0);
      }

      SIP_S8bit *dupName = (SIP_S8bit *)fast_memget(0, nameVal[0].size() + 1, 
                                                    &err);
      strcpy(dupName, nameVal[0].c_str());
      sip_setNameInSipParam(_param[idx], dupName, &err);

      SIP_S8bit *dupVal = (SIP_S8bit *)fast_memget(0, nameVal[1].size() + 1, 
                                                   &err);
      strcpy(dupVal, nameVal[1].c_str());
      sip_insertValueAtIndexInSipParam(_param[idx], dupVal, 0, &err);
   }
}

const SipParam * const * INGwSpSipAddressDefault::getParam(int &len) const
{
   len = _paramLen;
   return _param;
}

std::string INGwSpSipAddressDefault::toLog()
{
   std::ostringstream oStr;

   oStr << "Address [" 
        << " User: [" << ((_user == NULL) ? "NULL" : _user) << 
           "] Len[" << _userLen << "]"
        << " ParamCount: [" << _paramLen << "]";

   return oStr.str();
}

INGwSpSipAddressTreatment::INGwSpSipAddressTreatment()
{
   _protocolOwner     = OWNER_CCM;
   _protocolTreatment = PROTO_SIP;

   _userOwner = OWNER_CCM;
   _userTreatment = USER_INCOMING;
   _userRoleChangeChar = '\0';

   _hostOwner = OWNER_CCM;
   _hostTreatment = HOST_SELF;
   _hostRoleChangeChar = '\0';

   _paramOwner = OWNER_CCM;
   _paramTreatment = PARAM_INCOMING;
   _paramSleeInputTreatment = PARAM_SLEE_INPUT_IGNORE;
}

INGwSpSipAddressTreatment::~INGwSpSipAddressTreatment()
{
}

void INGwSpSipAddressTreatment::setProtocolOwner(Owner inOwner)
{
   _protocolOwner = inOwner;
}

Owner INGwSpSipAddressTreatment::getProtocolOwner() const
{
   return _protocolOwner;
}

void INGwSpSipAddressTreatment::setProtocolTreatment(ProtocolTreatment inTreatment)
{
   _protocolTreatment = inTreatment;
}

ProtocolTreatment INGwSpSipAddressTreatment::getProtocolTreatment() const
{
   return _protocolTreatment;
}

void INGwSpSipAddressTreatment:: setUserPartOwner(Owner inOwner)
{
   _userOwner = inOwner;
}

Owner INGwSpSipAddressTreatment::getUserPartOwner() const
{
   return _userOwner;
}

void INGwSpSipAddressTreatment::setUserPartTreatment(UserPartTreatment inTreatment)
{
   _userTreatment = inTreatment;
}

UserPartTreatment INGwSpSipAddressTreatment::getUserPartTreatment() const
{
   return _userTreatment;
}

void INGwSpSipAddressTreatment::setUserPartRoleChangeChar(char inChar)
{
   _userRoleChangeChar = inChar;
}

char INGwSpSipAddressTreatment::getUserPartRoleChangeChar() const
{
   return _userRoleChangeChar;
}

void  INGwSpSipAddressTreatment::setHostPartOwner(Owner inOwner)
{
   _hostOwner = inOwner;
}

Owner INGwSpSipAddressTreatment::getHostPartOwner() const
{
   return _hostOwner;
}

void INGwSpSipAddressTreatment::setHostPartTreatment(HostPartTreatment inTreatment)
{
   _hostTreatment = inTreatment;
}

HostPartTreatment INGwSpSipAddressTreatment::getHostPartTreatment() const
{
   return _hostTreatment;
}

void INGwSpSipAddressTreatment::setHostPartRoleChangeChar(char inChar)
{
   _hostRoleChangeChar = inChar;
}

char INGwSpSipAddressTreatment::getHostPartRoleChangeChar() const
{
   return _hostRoleChangeChar;
}

void INGwSpSipAddressTreatment:: setParamOwner(Owner inOwner)
{
   _paramOwner = inOwner;
}

Owner INGwSpSipAddressTreatment::getParamOwner() const
{
   return _paramOwner;
}

void INGwSpSipAddressTreatment::setParamTreatment(ParamTreatment inTreatment)
{
   _paramTreatment = inTreatment;
}

ParamTreatment INGwSpSipAddressTreatment::getParamTreatment() const
{
   return _paramTreatment;
}

void INGwSpSipAddressTreatment::setParamSleeInputTreatment(
                                            ParamSleeInputTreatment inTreatment)
{
   _paramSleeInputTreatment = inTreatment;
}

ParamSleeInputTreatment INGwSpSipAddressTreatment::getParamSleeInputTreatment() 
                                                                           const
{
   return _paramSleeInputTreatment;
}

std::string INGwSpSipAddressTreatment::toLog()
{
   std::ostringstream oStr;

   oStr << "Address [" 
        << " Protocol [ Owner:" << __ownerStr[_protocolOwner - OWNER_SLEE] 
        << " Treatment:" << __protocolStr[_protocolTreatment - PROTO_SIP] << "]"
        << " User [ Owner:" << __ownerStr[_userOwner - OWNER_SLEE] 
        << " Treatment:" << __userPartStr[_userTreatment - USER_ENVIRONMENT]
        << " RoleChange:" << _userRoleChangeChar << "]"
        << " Host [ Owner:" << __ownerStr[_hostOwner - OWNER_SLEE]
        << " Treatment:" << __hostPartStr[_hostOwner - HOST_GW]
        << " RoleChange:" << _hostRoleChangeChar << "]"
        << " AddrParam [ Owner:" << __ownerStr[_paramOwner - OWNER_SLEE]
        << " Treatment:" << __paramStr[_paramTreatment - PARAM_INCOMING]
        << " SleeInputTreatment:" 
        << __paramSleeStr[_paramSleeInputTreatment - PARAM_SLEE_INPUT_APPEND];

   return oStr.str();
}

INGwSpSipHeaderDefault::INGwSpSipHeaderDefault()
{
   _display = NULL;
   _displayLen = 0;

   _param = NULL;
   _paramLen = 0;
}

INGwSpSipHeaderDefault::~INGwSpSipHeaderDefault()
{
   if(_display)
   {
      delete [] _display;
      _display = NULL;
   }

   _displayLen = 0;

   if(_param)
   {
      for(int idx = 0; idx < _paramLen; idx++)
      {
         sip_freeSipParam(_param[idx]);
         _param[idx] = NULL;
      }

      delete []_param;
      _param = NULL;
   }

   _paramLen = 0;
}

const INGwSpSipHeaderDefault & INGwSpSipHeaderDefault::operator = (
                                               const INGwSpSipHeaderDefault &inData)
{
   setDisplay(inData._display, inData._displayLen);
   _address = inData._address;

   if(_param)
   {
      for(int idx = 0; idx < _paramLen; idx++)
      {
         sip_freeSipParam(_param[idx]);
         _param[idx] = NULL;
      }

      delete []_param;
      _param = NULL;
   }

   _paramLen = inData._paramLen;

   if(_paramLen)
   {
      _param = new SipParam*[_paramLen];
      SipError err;

      for(int idx = 0; idx < _paramLen; idx++)
      {
         sip_initSipParam(_param + idx, &err);
         __sip_cloneSipParam(_param[idx], inData._param[idx], &err);
      }
   }
   
   return *this;
}

void INGwSpSipHeaderDefault::setDisplay(const char *display, int displayLen)
{
   if(display == NULL)
   {
      display = "";
      displayLen = 0;
   }

   if(displayLen == -1)
   {
      displayLen = strlen(display);
   }

   _display = new char [displayLen + 1];
   strncpy(_display, display, displayLen);
   _display[displayLen] = '\0';

   _displayLen = displayLen;
}

const char *INGwSpSipHeaderDefault::getDisplay(int &len) const
{
   len = _displayLen;
   return _display;
}

INGwSpSipAddressDefault & INGwSpSipHeaderDefault::getAddress()
{
   return _address;
}

const INGwSpSipAddressDefault & INGwSpSipHeaderDefault::getAddress() const
{
   return _address;
}

void INGwSpSipHeaderDefault::setParam(const char *paramStr, int len)
{
   if(_param)
   {
      for(int idx = 0; idx < _paramLen; idx++)
      {
         sip_freeSipParam(_param[idx]);
         _param[idx] = NULL;
      }

      delete []_param;
      _param = NULL;
   }

   _paramLen = 0;

   if(len == 0)
   {
      return;
   }

   typedef vector<string> StrVector;
   typedef StrVector::iterator StrVectorIt;

   StrVector strParams;
   string paramDelimiter(";");

   string inData(paramStr);
   INGwAlgTokenizer(inData, paramDelimiter, back_inserter(strParams));

   _paramLen = strParams.size();

   if(_paramLen == 0)
   {
      return;
   }

   _param = new SipParam*[_paramLen];
   SipError err;

   int idx = 0;
   for(StrVectorIt it = strParams.begin(); it != strParams.end(); it++, idx++)
   {
      sip_initSipParam(_param + idx, &err);

      string &nameValStr = (*it);

      StrVector nameVal;
      string nameValDelimiter("=");
      INGwAlgTokenizer(nameValStr, nameValDelimiter, back_inserter(nameVal));

      if(nameVal.size() != 2)
      {
         logger.logMsg(ERROR_FLAG, 0, 
                       "Configuration fault. [%s] is not valid param", 
                       paramStr);
         logger.logMsg(ALWAYS_FLAG, 0, "Quitting on configuration fault.");
         exit(0);
      }

      SIP_S8bit *dupName = (SIP_S8bit *)fast_memget(0, nameVal[0].size() + 1, 
                                                    &err);
      strcpy(dupName, nameVal[0].c_str());
      sip_setNameInSipParam(_param[idx], dupName, &err);

      SIP_S8bit *dupVal = (SIP_S8bit *)fast_memget(0, nameVal[1].size() + 1, 
                                                   &err);
      strcpy(dupVal, nameVal[1].c_str());
      sip_insertValueAtIndexInSipParam(_param[idx], dupVal, 0, &err);
   }
}

const SipParam * const *INGwSpSipHeaderDefault::getParam(int &len) const
{
   len = _paramLen;
   return _param;
}

std::string INGwSpSipHeaderDefault::toLog()
{
   std::ostringstream oStr;

   oStr << "Header [" 
        << " Display: [" << 
           ((_display == NULL) ? "NULL" : _display) << 
           "] Len[" << _displayLen << "]"
        << " " << _address.toLog() 
        << " ParamCount: [" << _paramLen << "]";

   return oStr.str();
}

INGwSpSipHeaderTreatment::INGwSpSipHeaderTreatment()
{
   _displayOwner = OWNER_CCM;
   _displayTreatment = DISP_INCOMING;
   _displayRoleChangeChar = '\0';

   _headerParamOwner = OWNER_CCM;
   _headerParamTreatment = PARAM_INCOMING;
   _headerParamSleeInputTreatment = PARAM_SLEE_INPUT_IGNORE;
}

INGwSpSipHeaderTreatment::~INGwSpSipHeaderTreatment()
{
}

void  INGwSpSipHeaderTreatment::setDisplayOwner(Owner inOwner)
{
   _displayOwner = inOwner;
}

Owner INGwSpSipHeaderTreatment::getDisplayOwner() const
{
   return _displayOwner;
}

void INGwSpSipHeaderTreatment::setDisplayTreatment(DisplayTreatment inTreatment)
{
   _displayTreatment = inTreatment;
}

DisplayTreatment INGwSpSipHeaderTreatment::getDisplayTreatment() const
{
   return _displayTreatment;
}

void INGwSpSipHeaderTreatment::setDisplayRoleChangeChar(char inChar)
{
   _displayRoleChangeChar = inChar;
}

char INGwSpSipHeaderTreatment::getDisplayRoleChangeChar() const
{
   return _displayRoleChangeChar;
}

const INGwSpSipAddressTreatment & INGwSpSipHeaderTreatment::getAddressTreatment() const
{
   return _addressTreatment;
}

INGwSpSipAddressTreatment & INGwSpSipHeaderTreatment::getAddressTreatment()
{
   return _addressTreatment;
}

void  INGwSpSipHeaderTreatment::setHeaderParamOwner(Owner inOwner)
{
   _headerParamOwner = inOwner;
}

Owner INGwSpSipHeaderTreatment::getHeaderParamOwner() const
{
   return _headerParamOwner;
}

void INGwSpSipHeaderTreatment::setHeaderParamTreatment(ParamTreatment inTreatment)
{
   _headerParamTreatment = inTreatment;
}

ParamTreatment INGwSpSipHeaderTreatment::getHeaderParamTreatment() const
{
   return _headerParamTreatment;
}

void INGwSpSipHeaderTreatment::setParamSleeInputTreatment(
                                            ParamSleeInputTreatment inTreatment)
{
   _headerParamSleeInputTreatment = inTreatment;
}

ParamSleeInputTreatment INGwSpSipHeaderTreatment::getParamSleeInputTreatment() const
{
   return _headerParamSleeInputTreatment;
}

std::string INGwSpSipHeaderTreatment::toLog()
{
   std::ostringstream oStr;

   oStr << "Header [" 
        << " Display [ Owner:" << __ownerStr[_displayOwner - OWNER_SLEE] 
        << " Treatment:" << __displayStr[_displayTreatment - DISP_ENVIRONMENT] 
        << " RoleChange:" << _displayRoleChangeChar << "]" 
        << " " << _addressTreatment.toLog()
        << " HdrParam [ Owner:" << __ownerStr[_headerParamOwner - OWNER_SLEE]
        << " Treatment:" << __paramStr[_headerParamTreatment - PARAM_INCOMING]
        << " SleeInputTreatment:" 
        << __paramSleeStr[_headerParamSleeInputTreatment - 
                          PARAM_SLEE_INPUT_APPEND];

   return oStr.str();
}

INGwSpSipHeaderDefaultData::INGwSpSipHeaderDefaultData()
{
}

INGwSpSipHeaderDefaultData::~INGwSpSipHeaderDefaultData()
{
}

const INGwSpSipAddressDefault & INGwSpSipHeaderDefaultData::getReqUriDefault() const
{
   return _reqUriDefault;
}

INGwSpSipAddressDefault & INGwSpSipHeaderDefaultData::getReqUriDefault()
{
   return _reqUriDefault;
}

const INGwSpSipHeaderDefault & INGwSpSipHeaderDefaultData::getToHeaderDefault() const
{
   return _toHeaderDefault;
}

INGwSpSipHeaderDefault & INGwSpSipHeaderDefaultData::getToHeaderDefault()
{
   return _toHeaderDefault;
}

const INGwSpSipHeaderDefault & INGwSpSipHeaderDefaultData::getFromHeaderDefault() const
{
   return _fromHeaderDefault;
}

INGwSpSipHeaderDefault & INGwSpSipHeaderDefaultData::getFromHeaderDefault()
{
   return _fromHeaderDefault;
}

const INGwSpSipHeaderDefault & INGwSpSipHeaderDefaultData::getContactHeaderDefault() 
                                                                           const
{
   return _contactHeaderDefault;
}

INGwSpSipHeaderDefault & INGwSpSipHeaderDefaultData::getContactHeaderDefault()
{
   return _contactHeaderDefault;
}

std::string INGwSpSipHeaderDefaultData::toLog()
{
   std::ostringstream oStr;

   oStr << "ReqURI: [" << _reqUriDefault.toLog() << "]"
        << " FromHDR: [" << _fromHeaderDefault.toLog() << "]"
        << " ToHDR: [" << _toHeaderDefault.toLog() << "]"
        << " ContactHDR:" << _contactHeaderDefault.toLog() << "]";

   return oStr.str();
}

INGwSpSipHeaderPolicy::INGwSpSipHeaderPolicy()
{
}

INGwSpSipHeaderPolicy::~INGwSpSipHeaderPolicy()
{
}

const INGwSpSipAddressTreatment & INGwSpSipHeaderPolicy::getReqUriTreatment() const
{
   return _reqUriTreatment;
}

INGwSpSipAddressTreatment & INGwSpSipHeaderPolicy::getReqUriTreatment()
{
   return _reqUriTreatment;
}

const INGwSpSipHeaderTreatment & INGwSpSipHeaderPolicy::getToHeaderTreatment() const
{
   return _toHeaderTreatment;
}

INGwSpSipHeaderTreatment & INGwSpSipHeaderPolicy::getToHeaderTreatment()
{
   return _toHeaderTreatment;
}

const INGwSpSipHeaderTreatment & INGwSpSipHeaderPolicy::getFromHeaderTreatment() const
{
   return _fromHeaderTreatment;
}

INGwSpSipHeaderTreatment & INGwSpSipHeaderPolicy::getFromHeaderTreatment()
{
   return _fromHeaderTreatment;
}

const INGwSpSipHeaderTreatment & INGwSpSipHeaderPolicy::getContactHeaderTreatment() const
{
   return _contactHeaderTreatment;
}

INGwSpSipHeaderTreatment & INGwSpSipHeaderPolicy::getContactHeaderTreatment()
{
   return _contactHeaderTreatment;
}

std::string INGwSpSipHeaderPolicy::toLog()
{
   std::ostringstream oStr;

   oStr << "HeaderTreatment [" 
        << " ReqUri" << _reqUriTreatment.toLog()
        << " To" << _toHeaderTreatment.toLog()
        << " From" << _fromHeaderTreatment.toLog()
        << " Contact" << _contactHeaderTreatment.toLog();

   return oStr.str();
}

}

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
//     File:     INGwSpSipHeaderPolicy.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef INGW_SP_SIP_HEADER_POLICY_H_
#define INGW_SP_SIP_HEADER_POLICY_H_

#include <string>
#include <INGwSipProvider/INGwSpSipIncludes.h>

namespace RSI_NSP_SIP
{

enum HeaderName
{
   TOHDR = 342,
   FROMHDR,
   CONTACTHDR,
   REQURI  //REQURI is not the header though including for simplicity.
};

enum Owner
{
   OWNER_SLEE = 3456,
   OWNER_CCM,
   OWNER_SLEE_CCM
};

Owner _getHPOwner(const std::string &val);
Owner _getHPContactUserOwner(const std::string &val);

enum DisplayTreatment
{
   DISP_ENVIRONMENT = 3453,
   DISP_INCOMING
};

DisplayTreatment _getHPDisplay(const std::string &val);

enum ProtocolTreatment
{
   PROTO_SIP = 4532,
   PROTO_TEL,
   PROTO_INCOMING
};

ProtocolTreatment _getHPProtocol(const std::string &val);

enum UserPartTreatment
{
   USER_ENVIRONMENT = 349783,
   USER_INCOMING
};

UserPartTreatment _getHPUser(const std::string &val);

enum HostPartTreatment
{
   HOST_GW = 9073,
   HOST_LOOPBACK,
   HOST_INCOMING,
   HOST_SELF
};

HostPartTreatment _getHPHost(const std::string &val);

enum ParamTreatment
{
   PARAM_INCOMING = 3270,
   PARAM_ENVIRONMENT
};

ParamTreatment _getHPParam(const std::string &val);

enum ParamSleeInputTreatment
{
   PARAM_SLEE_INPUT_APPEND = 458929,
   PARAM_SLEE_INPUT_REPLACE,
   PARAM_SLEE_INPUT_IGNORE
};

ParamSleeInputTreatment _getHPSleeParam(const std::string &val);

class INGwSpSipAddressDefault
{
   private:

      char *_user;
      int _userLen;

      SipParam **_param;
      int _paramLen;

   public:

      INGwSpSipAddressDefault();
      ~INGwSpSipAddressDefault();

      const INGwSpSipAddressDefault & operator = (const INGwSpSipAddressDefault &);

      void setUser(const char *user, int userLen = -1);
      const char *getUser(int &len) const;

      void setParam(const char *paramStr, int paramLen);
      const SipParam * const * getParam(int &len) const;

      std::string toLog();
};

class INGwSpSipAddressTreatment
{
   private:

      Owner             _protocolOwner;
      ProtocolTreatment _protocolTreatment;

      Owner             _userOwner;
      UserPartTreatment _userTreatment;
      char              _userRoleChangeChar;

      Owner             _hostOwner;
      HostPartTreatment _hostTreatment;
      char              _hostRoleChangeChar;

      Owner              _paramOwner;
      ParamTreatment     _paramTreatment;
      ParamSleeInputTreatment _paramSleeInputTreatment;

   public:

      INGwSpSipAddressTreatment();
      ~INGwSpSipAddressTreatment();

      void  setProtocolOwner(Owner inOwner);
      Owner getProtocolOwner() const;
      void setProtocolTreatment(ProtocolTreatment inTreatment);
      ProtocolTreatment getProtocolTreatment() const;

      void  setUserPartOwner(Owner inOwner);
      Owner getUserPartOwner() const;
      void setUserPartTreatment(UserPartTreatment inTreatment);
      UserPartTreatment getUserPartTreatment() const;
      void setUserPartRoleChangeChar(char inChar);
      char getUserPartRoleChangeChar() const;

      void  setHostPartOwner(Owner inOwner);
      Owner getHostPartOwner() const;
      void setHostPartTreatment(HostPartTreatment inTreatment);
      HostPartTreatment getHostPartTreatment() const;
      void setHostPartRoleChangeChar(char inChar);
      char getHostPartRoleChangeChar() const;

      void  setParamOwner(Owner inOwner);
      Owner getParamOwner() const;
      void setParamTreatment(ParamTreatment inTreatment);
      ParamTreatment getParamTreatment() const;
      void setParamSleeInputTreatment(ParamSleeInputTreatment inTreatment);
      ParamSleeInputTreatment getParamSleeInputTreatment() const;

      std::string toLog();
};

class INGwSpSipHeaderDefault
{
   private:

      char *_display;
      int _displayLen;

      INGwSpSipAddressDefault _address;

      SipParam **_param;
      int _paramLen;

   public:

      INGwSpSipHeaderDefault();
      ~INGwSpSipHeaderDefault();

      const INGwSpSipHeaderDefault & operator = (const INGwSpSipHeaderDefault &);

      void setDisplay(const char *display, int displayLen = -1);
      const char *getDisplay(int &len) const;

      INGwSpSipAddressDefault & getAddress();
      const INGwSpSipAddressDefault & getAddress() const;

      void setParam(const char *paramStr, int len);
      const SipParam * const * getParam(int &len) const;

      std::string toLog();
};

class INGwSpSipHeaderTreatment
{
   private:

      Owner            _displayOwner;
      DisplayTreatment _displayTreatment;
      char             _displayRoleChangeChar;

      INGwSpSipAddressTreatment _addressTreatment;

      Owner              _headerParamOwner;
      ParamTreatment     _headerParamTreatment;
      ParamSleeInputTreatment _headerParamSleeInputTreatment;

   public:

      INGwSpSipHeaderTreatment();
      ~INGwSpSipHeaderTreatment();

      void  setDisplayOwner(Owner inOwner);
      Owner getDisplayOwner() const;
      void setDisplayTreatment(DisplayTreatment inTreatment);
      DisplayTreatment getDisplayTreatment() const;
      void setDisplayRoleChangeChar(char inChar);
      char getDisplayRoleChangeChar() const;

      const INGwSpSipAddressTreatment & getAddressTreatment() const;
      INGwSpSipAddressTreatment & getAddressTreatment();

      void  setHeaderParamOwner(Owner inOwner);
      Owner getHeaderParamOwner() const;
      void setHeaderParamTreatment(ParamTreatment inTreatment);
      ParamTreatment getHeaderParamTreatment() const;
      void setParamSleeInputTreatment(ParamSleeInputTreatment inTreatment);
      ParamSleeInputTreatment getParamSleeInputTreatment() const;

      std::string toLog();
};

class INGwSpSipHeaderDefaultData
{
   private:

      INGwSpSipAddressDefault _reqUriDefault;
      INGwSpSipHeaderDefault _toHeaderDefault;
      INGwSpSipHeaderDefault _fromHeaderDefault;
      INGwSpSipHeaderDefault _contactHeaderDefault;

   public:

      INGwSpSipHeaderDefaultData();
      ~INGwSpSipHeaderDefaultData();

      const INGwSpSipAddressDefault & getReqUriDefault() const;
      INGwSpSipAddressDefault & getReqUriDefault();

      const INGwSpSipHeaderDefault & getToHeaderDefault() const;
      INGwSpSipHeaderDefault & getToHeaderDefault();

      const INGwSpSipHeaderDefault & getFromHeaderDefault() const;
      INGwSpSipHeaderDefault & getFromHeaderDefault();

      const INGwSpSipHeaderDefault & getContactHeaderDefault() const;
      INGwSpSipHeaderDefault & getContactHeaderDefault();
      
      std::string toLog();
};

class INGwSpSipHeaderPolicy
{
   private:

      INGwSpSipAddressTreatment _reqUriTreatment;
      INGwSpSipHeaderTreatment _toHeaderTreatment;
      INGwSpSipHeaderTreatment _fromHeaderTreatment;
      INGwSpSipHeaderTreatment _contactHeaderTreatment;

   public:

      INGwSpSipHeaderPolicy();
      ~INGwSpSipHeaderPolicy();

      const INGwSpSipAddressTreatment & getReqUriTreatment() const;
      INGwSpSipAddressTreatment & getReqUriTreatment();

      const INGwSpSipHeaderTreatment & getToHeaderTreatment() const;
      INGwSpSipHeaderTreatment & getToHeaderTreatment();

      const INGwSpSipHeaderTreatment & getFromHeaderTreatment() const;
      INGwSpSipHeaderTreatment & getFromHeaderTreatment();

      const INGwSpSipHeaderTreatment & getContactHeaderTreatment() const;
      INGwSpSipHeaderTreatment & getContactHeaderTreatment();

      std::string toLog();
};

};

#endif

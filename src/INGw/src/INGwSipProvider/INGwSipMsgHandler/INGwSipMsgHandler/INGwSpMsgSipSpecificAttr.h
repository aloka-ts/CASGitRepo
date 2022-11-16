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
//     File:     INGwSpMsgSipSpecificAttr.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef INGW_SP_MSG_SIP_SPECIFIC_ATTR_H_
#define INGW_SP_MSG_SIP_SPECIFIC_ATTR_H_

#include <INGwInfraUtil/INGwIfrUtlRefCount.h>
#include <string>

class INGwSpMsgSipBillingInfo
{
   private:

      enum BillingTag
      {
         BCID       = 100,
         FEID,
         RKSGROUP,
         CHARGE,
         CALLING,
         CALLED,
         ROUTING,
         LOCROUTE,
         UNKNOWN
      };

      char *_billingInfoHeader;
      int _billingLen;

      int _serializeLen;
      char *_serializeData;

   private:

      void _cleanup();
      void _addTag(char *indata, BillingTag intag, int inlen, 
                   const char *inval);

   public:

      INGwSpMsgSipBillingInfo();
      ~INGwSpMsgSipBillingInfo();

      const char * getBillingInfoHeader(int &len) const;
      const char *getSerializeData(int &len) const;

      bool parseContent(const char *inInfo, unsigned int len);
      bool deserializeContent(const char *inData, unsigned int inlen);

      std::string toLog() const;

   private:

      INGwSpMsgSipBillingInfo(const INGwSpMsgSipBillingInfo &);
      INGwSpMsgSipBillingInfo& operator = (const INGwSpMsgSipBillingInfo &);
};

class INGwSpMsgSipReasonInfo
{
   private:

      enum ReasonTag
      {
         PROTOCOL   = 500,
         CAUSE,
         REASON_TEXT,
         UNKNOWN
      };

      char *_reasonHeader;
      int _reasonLen;

      int _serializeLen;
      char *_serializeData;

   private:

      void _cleanup();
      void _addTag(char *indata, ReasonTag intag, int inlen, const char *inval);

   public:

      INGwSpMsgSipReasonInfo();
      ~INGwSpMsgSipReasonInfo();

      const char * getReasonHeader(int &len) const;
      const char * getSerializeData(int &len) const;

      bool parseContent(const char *inInfo, unsigned int len);
      bool deserializeContent(const char * inData, unsigned int inlen);

      std::string toLog() const;

   private:

      INGwSpMsgSipReasonInfo(const INGwSpMsgSipReasonInfo &);
      INGwSpMsgSipReasonInfo& operator = (const INGwSpMsgSipReasonInfo &);
};

class INGwSpMsgSipStringInfo
{
   private:

      char *_stringInfo;
      int _stringLen;

   private:

      void _cleanup();

   public:

      INGwSpMsgSipStringInfo();
      ~INGwSpMsgSipStringInfo();

      const char * getStringInfo(int &len) const;
      const char * getSerializeData(int &len) const;

      bool parseContent(const char *inInfo, unsigned int len);
      bool parseContent(const char *inInfo, unsigned int len,
                        const char *prefix, unsigned int prefixLen);
      bool deserializeContent(const char *inData, unsigned int inlen);

      std::string toLog() const;

   private:

      INGwSpMsgSipStringInfo(const INGwSpMsgSipStringInfo &);
      INGwSpMsgSipStringInfo & operator = (const INGwSpMsgSipStringInfo &);
};

class INGwSpMsgSipAddrInfo
{
   public:

      enum AddrTag
      {
         PROTOCOL = 3456,
         UNKNOWN
      };

      enum ProtocolTag
      {
         SIP = 345,
         TEL
      };

   private:

      char *_serializeInfo;
      int _serializeLen;

      ProtocolTag _protocol;

   private:

      void _cleanup();
      void _addTag(char *indata, AddrTag intag, int inlen, const char *inval);

   public:

      INGwSpMsgSipAddrInfo();
      ~INGwSpMsgSipAddrInfo();

      const char *getSerializeData(int &len) const;

      void addParam(const char *name, unsigned int nameLen, 
                    const char *value, unsigned int valueLen);
      void addProtocol(ProtocolTag tag);
      ProtocolTag getProtocol() const;

      bool deserializeContent(const char *inData, unsigned int inlen);

      std::string toLog() const;
};

class INGwSpMsgSipSpecificAttr : public INGwIfrUtlRefCount
{
   private:

      enum AttributeTag
      {
         BILLING_INF0   = 2345,
         REASON_INFO,
         CONTENT_TYPE,
         CONTENT_BODY,
         CONTACT,
         ASSERTED_LEN,
         ASSERTED,
         MEDIA_SERVER_IP,
         ORIG_DATA,  
         DIALED_DATA,
         TARGET_DATA,
         CONTACT_DATA
      };

      INGwSpMsgSipBillingInfo *_billingInfo;
      INGwSpMsgSipReasonInfo *_reasonInfo;
      INGwSpMsgSipStringInfo *_contentType;
      INGwSpMsgSipStringInfo *_contentBody;
      INGwSpMsgSipStringInfo *_contact;
      int _assertedLen;
      INGwSpMsgSipStringInfo **_asserted;
      INGwSpMsgSipStringInfo *_mediaServerIP;
      INGwSpMsgSipAddrInfo *_origData;
      INGwSpMsgSipAddrInfo *_dialedData;
      INGwSpMsgSipAddrInfo *_targetData;
      INGwSpMsgSipAddrInfo *_contactData;

   private:

      void _clearBillingInfo();
      void _clearReasonInfo();
      void _clearContentType();
      void _clearContentBody();
      void _clearContact();
      void _clearAsserted();
      void _clearMediaServerIP();
      void _clearOrigData();
      void _clearDialedData();
      void _clearTargetData();
      void _clearContactData();

      void _addTag(char *indata, AttributeTag intag, int inlen, 
                   const char *inval);

   public:

      INGwSpMsgSipSpecificAttr()
      {
         _billingInfo = NULL;
         _reasonInfo = NULL;
         _contentType = NULL;
         _contentBody = NULL;
         _contact = NULL;
         _assertedLen = 0;
         _asserted = NULL;
         _mediaServerIP = NULL;
         _origData = NULL;
         _dialedData = NULL;
         _targetData = NULL;
         _contactData = NULL;

         mId = "SipAttr";
      }

      ~INGwSpMsgSipSpecificAttr()
      {
         _clearBillingInfo();
         _clearReasonInfo();
         _clearContentType();
         _clearContentBody();
         _clearContact();
         _clearAsserted();
         _clearMediaServerIP();
         _clearOrigData();
         _clearDialedData();
         _clearTargetData();
         _clearContactData();
      }

      bool setBillingInfo(const char *content, unsigned int len);
      const INGwSpMsgSipBillingInfo * getBillingInfo() const;

      bool setReasonInfo(const char *content, unsigned int len);
      const INGwSpMsgSipReasonInfo * getReasonInfo() const;

      bool setContentType(const char *content, unsigned int len);
      const INGwSpMsgSipStringInfo * getContentType() const;

      bool setContentBody(const char *content, unsigned int len);
      const INGwSpMsgSipStringInfo * getContentBody() const;

      bool setContact(const char *content, unsigned int len);
      bool setContact(const char *content, unsigned int len,
                      const char *prefix,  unsigned int prefixLen);
      const INGwSpMsgSipStringInfo * getContact() const;

      bool setAsserted(const char *content, unsigned int len);
      int getAssertedLen() const;
      const INGwSpMsgSipStringInfo * getAsserted(int idx) const;

      bool setMediaServerIP(const char *content, unsigned int len);
      const INGwSpMsgSipStringInfo * getMediaServerIP() const;

      bool setOrigData(INGwSpMsgSipAddrInfo *inData);
      const INGwSpMsgSipAddrInfo * getOrigData() const;

      bool setDialedData(INGwSpMsgSipAddrInfo *inData);
      const INGwSpMsgSipAddrInfo * getDialedData() const;

      bool setTargetData(INGwSpMsgSipAddrInfo *inData);
      const INGwSpMsgSipAddrInfo * getTargetData() const;

      bool setContactData(INGwSpMsgSipAddrInfo *inData);
      const INGwSpMsgSipAddrInfo * getContactData() const;

      int size() const;
      bool serialize(char *dataPtr, int currOffset, int &newOffset, int maxSize,
                     bool forceFullSerialization); 
      bool deserialize(const char *dataPtr, int currOffset, int &newOffset,
                       int maxSize);

      std::string toLog() const;
};

#endif

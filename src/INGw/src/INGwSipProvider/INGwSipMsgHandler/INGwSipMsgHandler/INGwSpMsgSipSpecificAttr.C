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
//     File:     INGwSpMsgSipSpecificAttr.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************
//
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipMsgHandler");

#include <INGwSipMsgHandler/INGwSpMsgSipSpecificAttr.h>
#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <inttypes.h>
#include <sstream>

using namespace std;

void INGwSpMsgSipBillingInfo::_cleanup()
{
   if(_billingInfoHeader)
   {
      delete []_billingInfoHeader; 
      _billingInfoHeader = NULL;
   }

   _billingLen = 0;

   if(_serializeData)
   {
      delete []_serializeData;
      _serializeData = NULL;
   }

   _serializeLen = 0;
}

void INGwSpMsgSipBillingInfo::_addTag(char *indata, BillingTag intag, int inlen, 
                               const char *inval)
{
   int tag = htonl(intag);
   int len = htonl(inlen);
   memcpy(indata, &tag, sizeof(int));
   indata += sizeof(int);
   memcpy(indata, &len, sizeof(int));
   indata += sizeof(int);
   memcpy(indata, inval, inlen);
}

INGwSpMsgSipBillingInfo::INGwSpMsgSipBillingInfo()
{
   _billingInfoHeader = NULL;
   _billingLen = 0;

   _serializeLen = 0;
   _serializeData = NULL;
}

INGwSpMsgSipBillingInfo::~INGwSpMsgSipBillingInfo()
{
   _cleanup();
}

const char * INGwSpMsgSipBillingInfo::getBillingInfoHeader(int &len) const
{
   len = _billingLen;
   return _billingInfoHeader;
}

const char *INGwSpMsgSipBillingInfo::getSerializeData(int &len) const
{
   len = _serializeLen;
   return _serializeData;
}

bool INGwSpMsgSipBillingInfo::parseContent(const char *inInfo, unsigned int len)
{
   _cleanup();

   //Remove blanks.
   const char *start = inInfo;
   while(*start == ' ' && len != 0) 
   {
      start++;
      len--;
   }

   if(len == 0)
   {
      _cleanup();
      return false;
   }

   //Serialize Format.
   //<TAG><VALUE_LEN><VALUE><TAG><VALUE_LEN><VALUE>....
   //For UnknownTAG <UNKNOWN_TAG><VALUE_LEN><VALUE><NAME_LEN><NAME>

   _serializeData = new char[len + 500];

   char *dataPtr = _serializeData;

   const char *end = start;

   while((*end != '/') && (len != 0))
   {
      end++;
      len--;
   }


   if(*end == '/')
   {
      _addTag(dataPtr, BCID, (end - start), start);
      dataPtr += (end - start);
      dataPtr += 8;
   }

   if(len == 0) 
   {
      logger.logMsg(ERROR_FLAG, 0, "Unable to get BCID.");
      _cleanup();
      return false;
   }

   end++;
   len--;
   start = end;

   while((*end != ';') && (len != 0))
   {
      end++;
      len--;
   }

   _addTag(dataPtr, FEID, (end - start), start);
   dataPtr += 8;
   dataPtr += (end - start);

   if(len == 0)
   {
      _serializeLen = (dataPtr - _serializeData);
      return true;
   }

   while(true)
   {
      end++;
      len--;

      const char *token = end;

      while((*end != '=') && (len != 0))
      {
         end++;
         len--;
      }

      if(len == 0)
      {
         break;
      }

      const char *tokenEnd = end;

      end++;
      len--;

      BillingTag currTag = UNKNOWN;
      {
         switch(*token)
         {
            case 'c':
            case 'C':
            {
               if((*(token + 1) == 'h') ||
                  (*(token + 1) == 'H'))
               {
                  if(strncasecmp(token, "charge", 6) == 0)
                  {
                     currTag = CHARGE;
                  }
               }
               else
               {
                  if((*(token + 4) == 'i') ||
                     (*(token + 4) == 'I'))
                  {
                     if(strncasecmp(token, "calling", 7) == 0)
                     {
                        currTag = CALLING;
                     }
                  }
                  else
                  {
                     if(strncasecmp(token, "called", 6) == 0)
                     {
                        currTag = CALLED;
                     }
                  }
               }
            }
            break;

            case 'r':
            case 'R':
            {
               if((*(token + 1) == 'k') ||
                  (*(token + 1) == 'K'))
               {
                  if(strncasecmp(token, "rksgroup", 8) == 0)
                  {
                     currTag = RKSGROUP;
                  }
               }
               else
               {
                  if(strncasecmp(token, "routing", 7) == 0)
                  {
                     currTag = ROUTING;
                  }
               }
            }
            break;

            case 'l':
            case 'L':
            {
               if(strncasecmp(token, "locroute", 8) == 0)
               {
                  currTag = LOCROUTE;
               }
            }
            break;

            default:
            {
            }
         }
      }

      switch(currTag)
      {
         case RKSGROUP:
         case UNKNOWN:
         {
            const char *value = end;

            while((*end != ';') && (len != 0))
            {
               end++;
               len--;
            }

            _addTag(dataPtr, currTag, (end - value), value);
            dataPtr += 8;
            dataPtr += (end - value);

            if(currTag == UNKNOWN)
            {
               int nameLen = (tokenEnd - token);
               nameLen = htonl(nameLen);
               memcpy(dataPtr, &nameLen, sizeof(int));
               dataPtr += sizeof(int);
               memcpy(dataPtr, token, (tokenEnd - token));
               dataPtr += (tokenEnd - token);
            }
         }
         break;

         default:
         {
            while((*end != '\"') && (len != 0))
            {
               end++;
               len--;
            }

            if(len == 0)
            {
               logger.logMsg(ERROR_FLAG, 0, "Parsing error.");
               _cleanup();
               return false;
            }

            end++;
            len--;
            const char *value = end;

            while((*end != '\"') && (len != 0))
            {
               end++;
               len--;
            }

            if(len == 0)
            {
               logger.logMsg(ERROR_FLAG, 0, "Parsing error.");
               _cleanup();
               return false;
            }

            _addTag(dataPtr, currTag, (end - value), value);
            dataPtr += 8;
            dataPtr += (end - value);

            while((*end != ';') && (len != 0))
            {
               end++;
               len--;
            }
         }
      }

      if(len == 0)
      {
         break;
      }
   }

   _serializeLen = (dataPtr - _serializeData);
   return true;
}

bool INGwSpMsgSipBillingInfo::deserializeContent(const char *inData, 
                                          unsigned int inlen)
{
   _cleanup();

   _billingInfoHeader = new char[inlen + 500];
   char *dataPtr = _billingInfoHeader;

   const char *end = inData + inlen;
   const char *start = inData;

   while(start < end)
   {
      int tag; 
      memcpy(&tag, start, sizeof(int));
      start += sizeof(int);

      int length;
      memcpy(&length, start, sizeof(int));
      start += sizeof(int);

      BillingTag currTag = (BillingTag) ntohl(tag);
      length = ntohl(length);

      if((start + length) > end)
      {
         logger.logMsg(ERROR_FLAG, 0, "Deserialize failed.");
         return false;
      }

      switch(currTag)
      {
         case BCID:
         {
            memcpy(dataPtr, start, length);
            dataPtr += length;
            *dataPtr = '/';
            dataPtr++;
         }
         break;

         case FEID:
         {
            memcpy(dataPtr, start, length);
            dataPtr += length;
         }
         break;

         case RKSGROUP:
         {
            memcpy(dataPtr, ";rksgroup=", 10);
            dataPtr += 10;
            memcpy(dataPtr, start, length);
            dataPtr += length;
         }
         break;

         case CHARGE:
         {
            memcpy(dataPtr, ";charge=\"", 9);
            dataPtr += 9;
            memcpy(dataPtr, start, length);
            dataPtr += length;
            *dataPtr = '\"';
            dataPtr++;
         }
         break;

         case CALLING:
         {
            memcpy(dataPtr, ";calling=\"", 10);
            dataPtr += 10;
            memcpy(dataPtr, start, length);
            dataPtr += length;
            *dataPtr = '\"';
            dataPtr++;
         }
         break;

         case CALLED:
         {
            memcpy(dataPtr, ";called=\"", 9);
            dataPtr += 9;
            memcpy(dataPtr, start, length);
            dataPtr += length;
            *dataPtr = '\"';
            dataPtr++;
         }
         break;

         case ROUTING:
         {
            memcpy(dataPtr, ";routing=\"", 10);
            dataPtr += 10;
            memcpy(dataPtr, start, length);
            dataPtr += length;
            *dataPtr = '\"';
            dataPtr++;
         }
         break;

         case LOCROUTE:
         {
            memcpy(dataPtr, ";locroute=\"", 11);
            dataPtr += 11;
            memcpy(dataPtr, start, length);
            dataPtr += length;
            *dataPtr = '\"';
            dataPtr++;
         }
         break;

         case UNKNOWN:
         {
           const char *name = start + length;

           int nameLen;
           memcpy(&nameLen, name, sizeof(int));
           nameLen = ntohl(nameLen);
           name += sizeof(int);

           *dataPtr = ';';
           dataPtr++;
           memcpy(dataPtr, name, nameLen);
           dataPtr += nameLen;
           *dataPtr = '=';
           dataPtr++;
           memcpy(dataPtr, start, length);
           dataPtr += length;

           start += nameLen;
           start += sizeof(int);
         }
         break;

         default:
         {
            logger.logMsg(ERROR_FLAG, 0, "Undefined tag [%d]", currTag);
            _cleanup();
            return false;
         }
      }

      start += length;
   }

   *dataPtr = '\0';
   _billingLen = dataPtr - _billingInfoHeader;
   return true;
}

std::string INGwSpMsgSipBillingInfo::toLog() const
{
   std::ostringstream oStr;

   oStr << "BillingLen [" << _billingLen << "-" << _serializeLen << "]";

   if(_billingInfoHeader)
   {
      oStr << "Bill [" << string(_billingInfoHeader, _billingLen) << "]";
   }

   if(_serializeData)
   {
      const char *end = _serializeData + _serializeLen;
      const char *start = _serializeData;
   
      while(start < end)
      {
         int tag; 
         memcpy(&tag, start, sizeof(int));
         start += sizeof(int);
   
         int length;
         memcpy(&length, start, sizeof(int));
         start += sizeof(int);
   
         BillingTag currTag = (BillingTag) ntohl(tag);
         length = ntohl(length);
   
         if((start + length) > end)
         {
            logger.logMsg(ERROR_FLAG, 0, "Log failed.");
            return oStr.str();
         }
   
         switch(currTag)
         {
            case BCID:
            {
               oStr << ", BCID:[" << string(start, length) << "]";
            }
            break;
   
            case FEID:
            {
               oStr << ", FEID:[" << string(start, length) << "]";
            }
            break;
   
            case RKSGROUP:
            {
               oStr << ", RKSGROUP:[" << string(start, length) << "]";
            }
            break;
   
            case CHARGE:
            {
               oStr << ", CHARGE:[" << string(start, length) << "]";
            }
            break;
   
            case CALLING:
            {
               oStr << ", CALLING:[" << string(start, length) << "]";
            }
            break;
   
            case CALLED:
            {
               oStr << ", CALLED:[" << string(start, length) << "]";
            }
            break;
   
            case ROUTING:
            {
               oStr << ", ROUTING:[" << string(start, length) << "]";
            }
            break;
   
            case LOCROUTE:
            {
               oStr << ", LOCROUTE:[" << string(start, length) << "]";
            }
            break;
   
            case UNKNOWN:
            {
              const char *name = start + length;
   
              int nameLen;
              memcpy(&nameLen, name, sizeof(int));
              nameLen = ntohl(nameLen);
              name += sizeof(int);
   
              oStr << ", UnknownBill:[" << string(name, nameLen) << "][" 
                   << string(start, length) << "]";
                  
              start += nameLen;
              start += sizeof(int);
            }
            break;
   
            default:
            {
               logger.logMsg(ERROR_FLAG, 0, "Log failed unknown tag [%d]", 
                             currTag);
               return oStr.str();
            }
         }
   
         start += length;
      }
   }

   return oStr.str();
}

void INGwSpMsgSipReasonInfo::_cleanup()
{
   if(_reasonHeader)
   {
      delete []_reasonHeader; 
      _reasonHeader = NULL;
   }

   _reasonLen = 0;

   if(_serializeData)
   {
      delete []_serializeData;
      _serializeData = NULL;
   }

   _serializeLen = 0;
}

void INGwSpMsgSipReasonInfo::_addTag(char *indata, ReasonTag intag, int inlen, 
                              const char *inval)
{
   int tag = htonl(intag);
   int len = htonl(inlen);
   memcpy(indata, &tag, sizeof(int));
   indata += sizeof(int);
   memcpy(indata, &len, sizeof(int));
   indata += sizeof(int);
   memcpy(indata, inval, inlen);
}

INGwSpMsgSipReasonInfo::INGwSpMsgSipReasonInfo()
{
   _reasonHeader = NULL;
   _reasonLen = 0;

   _serializeLen = 0;
   _serializeData = NULL;
}

INGwSpMsgSipReasonInfo::~INGwSpMsgSipReasonInfo()
{
   _cleanup();
}

const char * INGwSpMsgSipReasonInfo::getReasonHeader(int &len) const
{
   len = _reasonLen;
   return _reasonHeader;
}

const char *INGwSpMsgSipReasonInfo::getSerializeData(int &len) const
{
   len = _serializeLen;
   return _serializeData;
}

bool INGwSpMsgSipReasonInfo::parseContent(const char *inInfo, unsigned int len)
{
   _cleanup();

   //Remove blanks.
   const char *start = inInfo;
   while(*start == ' ' && len != 0) 
   {
      start++;
      len--;
   }

   if(len == 0)
   {
      _cleanup();
      return false;
   }

   //Serialize Format.
   //<TAG><VALUE_LEN><VALUE><TAG><VALUE_LEN><VALUE>....
   //For UnknownTAG <UNKNOWN_TAG><VALUE_LEN><VALUE><NAME_LEN><NAME>

   _serializeData = new char[len + 500];

   char *dataPtr = _serializeData;

   const char *end = start;

   while((*end != ';') && (len != 0))
   {
      end++;
      len--;
   }

   _addTag(dataPtr, PROTOCOL, (end - start), start);
   dataPtr += 8;
   dataPtr += (end - start);

   if(len == 0)
   {
      _serializeLen = (dataPtr - _serializeData);
      return true;
   }

   while(true)
   {
      end++;
      len--;

      const char *token = end;

      while((*end != '=') && (len != 0))
      {
         end++;
         len--;
      }

      if(len == 0)
      {
         break;
      }

      const char *tokenEnd = end;

      end++;
      len--;

      ReasonTag currTag = UNKNOWN;
      {
         switch(*token)
         {
            case 'c':
            case 'C':
            {
               if(strncasecmp(token, "cause", 5) == 0)
               {
                  currTag = CAUSE;
               }
            }
            break;

            case 't':
            case 'T':
            {
               if(strncasecmp(token, "text", 4) == 0)
               {
                  currTag = REASON_TEXT;
               }
            }
            break;

            default:
            {
            }
         }
      }

      switch(currTag)
      {
         case CAUSE:
         case UNKNOWN:
         {
            const char *value = end;

            while((*end != ';') && (len != 0))
            {
               end++;
               len--;
            }

            _addTag(dataPtr, currTag, (end - value), value);
            dataPtr += 8;
            dataPtr += (end - value);

            if(currTag == UNKNOWN)
            {
               int nameLen = (tokenEnd - token);
               nameLen = htonl(nameLen);
               memcpy(dataPtr, &nameLen, sizeof(int));
               dataPtr += sizeof(int);
               memcpy(dataPtr, token, (tokenEnd - token));
               dataPtr += (tokenEnd - token);
            }
         }
         break;

         default:
         {
            while((*end != '\"') && (len != 0))
            {
               end++;
               len--;
            }

            if(len == 0)
            {
               logger.logMsg(ERROR_FLAG, 0, "Parsing error.");
               _cleanup();
               return false;
            }

            end++;
            len--;
            const char *value = end;

            while((*end != '\"') && (len != 0))
            {
               end++;
               len--;
            }

            if(len == 0)
            {
               logger.logMsg(ERROR_FLAG, 0, "Parsing error.");
               _cleanup();
               return false;
            }

            _addTag(dataPtr, currTag, (end - value), value);
            dataPtr += 8;
            dataPtr += (end - value);

            while((*end != ';') && (len != 0))
            {
               end++;
               len--;
            }
         }
      }

      if(len == 0)
      {
         break;
      }
   }

   _serializeLen = (dataPtr - _serializeData);
   return true;
}

bool INGwSpMsgSipReasonInfo::deserializeContent(const char *inData, 
                                          unsigned int inlen)
{
   _cleanup();

   _reasonHeader = new char[inlen + 500];
   char *dataPtr = _reasonHeader;

   const char *end = inData + inlen;
   const char *start = inData;

   while(start < end)
   {
      int tag; 
      memcpy(&tag, start, sizeof(int));
      start += sizeof(int);

      int length;
      memcpy(&length, start, sizeof(int));
      start += sizeof(int);

      ReasonTag currTag = (ReasonTag) ntohl(tag);
      length = ntohl(length);

      if((start + length) > end)
      {
         logger.logMsg(ERROR_FLAG, 0, "Deserialize failed.");
         return false;
      }

      switch(currTag)
      {
         case PROTOCOL:
         {
            memcpy(dataPtr, start, length);
            dataPtr += length;
         }
         break;

         case CAUSE:
         {
            memcpy(dataPtr, ";cause=", 7);
            dataPtr += 7;
            memcpy(dataPtr, start, length);
            dataPtr += length;
         }
         break;

         case REASON_TEXT:
         {
            memcpy(dataPtr, ";text=\"", 7);
            dataPtr += 7;
            memcpy(dataPtr, start, length);
            dataPtr += length;
            *dataPtr = '\"';
            dataPtr++;
         }
         break;

         case UNKNOWN:
         {
           const char *name = start + length;

           int nameLen = 0;
           memcpy(&nameLen, name, sizeof(int));
           nameLen = ntohl(nameLen);
           name += sizeof(int);

           *dataPtr = ';';
           dataPtr++;
           memcpy(dataPtr, name, nameLen);
           dataPtr += nameLen;
           *dataPtr = '=';
           dataPtr++;
           memcpy(dataPtr, start, length);
           dataPtr += length;

           start += nameLen;
           start += sizeof(int);
         }
         break;

         default:
         {
            logger.logMsg(ERROR_FLAG, 0, "Undefined tag [%d]", currTag);
            _cleanup();
            return false;
         }
      }

      start += length;
   }

   *dataPtr = '\0';
   _reasonLen = dataPtr - _reasonHeader;
   return true;
}

std::string INGwSpMsgSipReasonInfo::toLog() const
{
   std::ostringstream oStr;

   oStr << "ReasonLen [" << _reasonLen << "-" << _serializeLen << "]";

   if(_reasonHeader)
   {
      oStr << "Reason [" << string(_reasonHeader, _reasonLen) << "]";
   }

   if(_serializeData)
   {
      const char *end = _serializeData + _serializeLen;
      const char *start = _serializeData;
   
      while(start < end)
      {
         int tag; 
         memcpy(&tag, start, sizeof(int));
         start += sizeof(int);
   
         int length;
         memcpy(&length, start, sizeof(int));
         start += sizeof(int);
   
         ReasonTag currTag = (ReasonTag) ntohl(tag);
         length = ntohl(length);
   
         if((start + length) > end)
         {
            logger.logMsg(ERROR_FLAG, 0, "Log failed.");
            return oStr.str();
         }
   
         switch(currTag)
         {
            case PROTOCOL:
            {
               oStr << ", PROTOCOL:[" << string(start, length) << "]";
            }
            break;
   
            case CAUSE:
            {
               oStr << ", CAUSE:[" << string(start, length) << "]";
            }
            break;
   
            case REASON_TEXT:
            {
               oStr << ", TEXT:[" << string(start, length) << "]";
            }
            break;
   
            case UNKNOWN:
            {
              const char *name = start + length;
   
              int nameLen;
              memcpy(&nameLen, name, sizeof(int));
              nameLen = ntohl(nameLen);
              name += sizeof(int);
   
              oStr << ", UnknownReason:[" << string(name, nameLen) << "][" 
                   << string(start, length) << "]";
                  
              start += nameLen;
              start += sizeof(int);
            }
            break;
   
            default:
            {
               logger.logMsg(ERROR_FLAG, 0, "Log failed unknown tag [%d]", 
                             currTag);
               return oStr.str();
            }
         }
   
         start += length;
      }
   }

   return oStr.str();
}

INGwSpMsgSipStringInfo::INGwSpMsgSipStringInfo()
{
   _stringInfo = NULL;
   _stringLen = 0;
}

INGwSpMsgSipStringInfo::~INGwSpMsgSipStringInfo()
{
   _cleanup();
}

void INGwSpMsgSipStringInfo::_cleanup()
{
   if(_stringInfo)
   {
      delete []_stringInfo;
      _stringInfo = NULL;
   }

   _stringLen = 0;
}

const char * INGwSpMsgSipStringInfo::getStringInfo(int &len) const
{
   len = _stringLen;
   return _stringInfo;
}

const char * INGwSpMsgSipStringInfo::getSerializeData(int &len) const
{
   return getStringInfo(len);
}

bool INGwSpMsgSipStringInfo::parseContent(const char *inInfo, unsigned int len)
{
   _cleanup();

   _stringInfo = new char[len + 1];
   memcpy(_stringInfo, inInfo, len);
   _stringInfo[len] = '\0';
   _stringLen = len;

   return true;
}

bool INGwSpMsgSipStringInfo::parseContent(const char *inInfo, unsigned int len,
                                   const char *prefix, unsigned int prefixLen)
{
   _cleanup();

   int totalLen = len + prefixLen;
   _stringInfo = new char[totalLen + 1];
   memcpy(_stringInfo, prefix, prefixLen);
   memcpy(_stringInfo + prefixLen, inInfo, len);
   _stringInfo[totalLen] = '\0';
   _stringLen = totalLen;

   return true;
}

bool INGwSpMsgSipStringInfo::deserializeContent(const char *info, unsigned int len)
{
   return parseContent(info, len);
}

std::string INGwSpMsgSipStringInfo::toLog() const
{
   std::ostringstream oStr;

   oStr << "StringLen [" << _stringLen << "]";

   if(_stringInfo)
   {
      oStr << " Info [" << string(_stringInfo, _stringLen) << "]";
   }

   return oStr.str();
}

INGwSpMsgSipAddrInfo::INGwSpMsgSipAddrInfo()
{
   _serializeInfo = NULL;
   _serializeLen = 0;
   _protocol = SIP;
}

INGwSpMsgSipAddrInfo::~INGwSpMsgSipAddrInfo()
{
   _cleanup();
}

void INGwSpMsgSipAddrInfo::_cleanup()
{
   if(_serializeInfo)
   {
      delete []_serializeInfo;
      _serializeInfo = NULL;
   }

   _serializeLen = 0;
   _protocol = SIP;
}

void INGwSpMsgSipAddrInfo::_addTag(char *indata, AddrTag intag, int inlen, 
                            const char *inval)
{
   int tag = htonl(intag);
   int len = htonl(inlen);
   memcpy(indata, &tag, sizeof(int));
   indata += sizeof(int);
   memcpy(indata, &len, sizeof(int));
   indata += sizeof(int);
   memcpy(indata, inval, inlen);
}

const char * INGwSpMsgSipAddrInfo::getSerializeData(int &len) const
{
   len = _serializeLen;
   return _serializeInfo;
}

void INGwSpMsgSipAddrInfo::addParam(const char *name, unsigned int nameLen, 
                             const char *value, unsigned int valueLen)
{
   if(_serializeInfo == NULL)
   {
      _serializeInfo = new char[500];
   }

   _addTag(_serializeInfo + _serializeLen, UNKNOWN, valueLen, value);
   _serializeLen += 8;
   _serializeLen += valueLen;

   int locLen = htonl(nameLen);
   memcpy(_serializeInfo + _serializeLen, &locLen, sizeof(int));
   _serializeLen += sizeof(int);
   memcpy(_serializeInfo + _serializeLen, name, nameLen);
   _serializeLen += nameLen;
}

void INGwSpMsgSipAddrInfo::addProtocol(ProtocolTag tag)
{
   if(_serializeInfo == NULL)
   {
      _serializeInfo = new char[500];
   }

   switch(tag)
   {
      case SIP:
      {
         _addTag(_serializeInfo + _serializeLen, PROTOCOL, 3, "sip");
         _serializeLen += 8;
         _serializeLen += 3;
      }
      break;

      case TEL:
      {
         _addTag(_serializeInfo + _serializeLen, PROTOCOL, 3, "tel");
         _serializeLen += 8;
         _serializeLen += 3;
      }
      break;
   }

   _protocol = tag;
}

INGwSpMsgSipAddrInfo::ProtocolTag INGwSpMsgSipAddrInfo::getProtocol() const
{
   return _protocol;
}

bool INGwSpMsgSipAddrInfo::deserializeContent(const char *inData, unsigned int inlen)
{
   _cleanup();

   _serializeInfo = new char[inlen];
   memcpy(_serializeInfo, inData, inlen);
   _serializeLen = inlen;

   const char *end = inData + inlen;
   const char *start = inData;

   while(start < end)
   {
      int tag; 
      memcpy(&tag, start, sizeof(int));
      start += sizeof(int);

      int length;
      memcpy(&length, start, sizeof(int));
      start += sizeof(int);

      AddrTag currTag = (AddrTag) ntohl(tag);
      length = ntohl(length);

      if((start + length) > end)
      {
         logger.logMsg(ERROR_FLAG, 0, "Deserialize failed.");
         return false;
      }

      switch(currTag)
      {
         case PROTOCOL:
         {
            if(strncasecmp("sip", start, 3) == 0)
            {
               _protocol = SIP;
            }
            else if(strncasecmp("tel", start, 3) == 0)
            {
               _protocol = TEL;
            }
         }
         break;

         case UNKNOWN:
         {
           const char *name = start + length;

           int nameLen = 0;
           memcpy(&nameLen, name, sizeof(int));
           nameLen = ntohl(nameLen);
           name += sizeof(int);

           start += nameLen;
           start += sizeof(int);
         }
         break;

         default:
         {
            logger.logMsg(ERROR_FLAG, 0, "Undefined tag [%d]", currTag);
            _cleanup();
            return false;
         }
      }

      start += length;
   }

   return true;
}

std::string INGwSpMsgSipAddrInfo::toLog() const
{
   std::ostringstream oStr;

   oStr << "AddrLen [" << _serializeLen << "]";

   if(_serializeInfo)
   {
      const char *end = _serializeInfo + _serializeLen;
      const char *start = _serializeInfo;
   
      while(start < end)
      {
         int tag; 
         memcpy(&tag, start, sizeof(int));
         start += sizeof(int);
   
         int length;
         memcpy(&length, start, sizeof(int));
         start += sizeof(int);
   
         AddrTag currTag = (AddrTag) ntohl(tag);
         length = ntohl(length);
   
         if((start + length) > end)
         {
            logger.logMsg(ERROR_FLAG, 0, "Log failed.");
            return oStr.str();
         }
   
         switch(currTag)
         {
            case PROTOCOL:
            {
               oStr << ", PROTOCOL:[" << string(start, length) << "]";
            }
            break;
   
            case UNKNOWN:
            {
              const char *name = start + length;
   
              int nameLen;
              memcpy(&nameLen, name, sizeof(int));
              nameLen = ntohl(nameLen);
              name += sizeof(int);
   
              oStr << ", UnknownInfo:[" << string(name, nameLen) << "][" 
                   << string(start, length) << "]";
                  
              start += nameLen;
              start += sizeof(int);
            }
            break;
   
            default:
            {
               logger.logMsg(ERROR_FLAG, 0, "Log failed unknown tag [%d]", 
                             currTag);
               return oStr.str();
            }
         }
   
         start += length;
      }
   }

   return oStr.str();
}

void INGwSpMsgSipSpecificAttr::_clearBillingInfo()
{
   if(_billingInfo)
   {
      delete _billingInfo;
      _billingInfo = NULL;
   }
}

void INGwSpMsgSipSpecificAttr::_clearReasonInfo()
{
   if(_reasonInfo)
   {
      delete _reasonInfo;
      _reasonInfo = NULL;
   }
}

void INGwSpMsgSipSpecificAttr::_clearContentType()
{
   if(_contentType)
   {
      delete _contentType;
      _contentType = NULL;
   }
}

void INGwSpMsgSipSpecificAttr::_clearContentBody()
{
   if(_contentBody)
   {
      delete _contentBody;
      _contentBody = NULL;
   }
}

void INGwSpMsgSipSpecificAttr::_clearContact()
{
   if(_contact)
   {
      delete _contact;
      _contact = NULL;
   }
}

void INGwSpMsgSipSpecificAttr::_clearAsserted()
{
   if(_asserted)
   {
      for(int idx = 0; idx < _assertedLen; idx++)
      {
         delete _asserted[idx];
      }

      delete []_asserted;
      _asserted = NULL;
   }

   _assertedLen = 0;
}

void INGwSpMsgSipSpecificAttr::_clearMediaServerIP()
{
   if(_mediaServerIP)
   {
      delete _mediaServerIP;
      _mediaServerIP = NULL;
   }
}

void INGwSpMsgSipSpecificAttr::_clearOrigData()
{
   if(_origData)
   {
      delete _origData;
      _origData = NULL;
   }
}

void INGwSpMsgSipSpecificAttr::_clearDialedData()
{
   if(_dialedData)
   {
      delete _dialedData;
      _dialedData = NULL;
   }
}

void INGwSpMsgSipSpecificAttr::_clearTargetData()
{
   if(_targetData)
   {
      delete _targetData;
      _targetData = NULL;
   }
}

void INGwSpMsgSipSpecificAttr::_clearContactData()
{
   if(_contactData)
   {
      delete _contactData;
      _contactData = NULL;
   }
}

bool INGwSpMsgSipSpecificAttr::setBillingInfo(const char *content, unsigned int len)
{
   _clearBillingInfo();

   _billingInfo = new INGwSpMsgSipBillingInfo();
   if(_billingInfo->parseContent(content, len))
   {
      return true;
   }

   _clearBillingInfo();
   return false;
}

const INGwSpMsgSipBillingInfo * INGwSpMsgSipSpecificAttr::getBillingInfo() const
{
   return _billingInfo;
}

bool INGwSpMsgSipSpecificAttr::setReasonInfo(const char *content, unsigned int len)
{
   _clearReasonInfo();

   _reasonInfo = new INGwSpMsgSipReasonInfo();
   if(_reasonInfo->parseContent(content, len))
   {
      return true;
   }

   _clearReasonInfo();
   return false;
}

const INGwSpMsgSipReasonInfo * INGwSpMsgSipSpecificAttr::getReasonInfo() const
{
   return _reasonInfo;
}

bool INGwSpMsgSipSpecificAttr::setContentType(const char *content, unsigned int len)
{
   _clearContentType();

   _contentType = new INGwSpMsgSipStringInfo();

   if(_contentType->parseContent(content, len))
   {
      return true;
   }

   _clearContentType();
   return false;
}

const INGwSpMsgSipStringInfo * INGwSpMsgSipSpecificAttr::getContentType() const
{
   return _contentType;
}

bool INGwSpMsgSipSpecificAttr::setContentBody(const char *content, unsigned int len)
{
   _clearContentBody();

   _contentBody = new INGwSpMsgSipStringInfo();

   if(_contentBody->parseContent(content, len))
   {
      return true;
   }

   _clearContentBody();
   return false;
}

const INGwSpMsgSipStringInfo * INGwSpMsgSipSpecificAttr::getContentBody() const
{
   return _contentBody;
}

bool INGwSpMsgSipSpecificAttr::setContact(const char *content, unsigned int len)
{
   _clearContact();

   _contact = new INGwSpMsgSipStringInfo();

   if(_contact->parseContent(content, len))
   {
      return true;
   }

   _clearContact();
   return false;
}

bool INGwSpMsgSipSpecificAttr::setContact(const char *content, unsigned int len,
                                   const char *prefix, unsigned int prefixLen)
{
   _clearContact();

   _contact = new INGwSpMsgSipStringInfo();

   if(_contact->parseContent(content, len, prefix, prefixLen))
   {
      return true;
   }

   _clearContact();
   return false;
}

const INGwSpMsgSipStringInfo * INGwSpMsgSipSpecificAttr::getContact() const
{
   return _contact;
}

bool INGwSpMsgSipSpecificAttr::setAsserted(const char *content, unsigned int len)
{
   if(_asserted == NULL)
   {
      _asserted = new INGwSpMsgSipStringInfo*[10];
   }

   _asserted[_assertedLen] = new INGwSpMsgSipStringInfo();

   if(_asserted[_assertedLen]->parseContent(content, len))
   {
      _assertedLen++;
      return true;
   }

   delete _asserted[_assertedLen];
   return false;
}

int INGwSpMsgSipSpecificAttr::getAssertedLen() const
{
   return _assertedLen;
}

const INGwSpMsgSipStringInfo * INGwSpMsgSipSpecificAttr::getAsserted(int idx) const
{
   return _asserted[idx];
}

bool INGwSpMsgSipSpecificAttr::setMediaServerIP(const char *content, unsigned int len)
{
   _clearMediaServerIP();

   _mediaServerIP = new INGwSpMsgSipStringInfo();

   if(_mediaServerIP->parseContent(content, len))
   {
      return true;
   }

   _clearMediaServerIP();
   return false;
}

const INGwSpMsgSipStringInfo * INGwSpMsgSipSpecificAttr::getMediaServerIP() const
{
   return _mediaServerIP;
}

bool INGwSpMsgSipSpecificAttr::setOrigData(INGwSpMsgSipAddrInfo *inData)
{
   _clearOrigData();
   _origData = inData;
   return true;
}

const INGwSpMsgSipAddrInfo * INGwSpMsgSipSpecificAttr::getOrigData() const
{
   return _origData;
}

bool INGwSpMsgSipSpecificAttr::setDialedData(INGwSpMsgSipAddrInfo *inData)
{
   _clearDialedData();
   _dialedData = inData;
   return true;
}

const INGwSpMsgSipAddrInfo * INGwSpMsgSipSpecificAttr::getDialedData() const
{
   return _dialedData;
}

bool INGwSpMsgSipSpecificAttr::setTargetData(INGwSpMsgSipAddrInfo *inData)
{
   _clearTargetData();
   _targetData = inData;
   return true;
}

const INGwSpMsgSipAddrInfo * INGwSpMsgSipSpecificAttr::getTargetData() const
{
   return _targetData;
}


bool INGwSpMsgSipSpecificAttr::setContactData(INGwSpMsgSipAddrInfo *inData)
{
   _clearContactData();
   _contactData = inData;
   return true;
}

const INGwSpMsgSipAddrInfo * INGwSpMsgSipSpecificAttr::getContactData() const
{
   return _contactData;
}

int INGwSpMsgSipSpecificAttr::size() const
{
   int retLen = 4;
   int len = 0;

   if(_billingInfo)
   {
      _billingInfo->getSerializeData(len);
      retLen += len;
      retLen += 8;
   }

   if(_reasonInfo)
   {
      _reasonInfo->getSerializeData(len);
      retLen += len;
      retLen += 8;
   }

   if(_contentType)
   {
      _contentType->getSerializeData(len);
      retLen += len;
      retLen += 8;
   }

   if(_contentBody)
   {
      _contentBody->getSerializeData(len);
      retLen += len;
      retLen += 8;
   }

   if(_contact)
   {
      _contact->getSerializeData(len);
      retLen += len;
      retLen += 8;
   }

   if(_asserted)
   {
      retLen += 12; //AssertedLen;

      for(int idx = 0; idx < _assertedLen; idx++)
      {
         _asserted[idx]->getSerializeData(len);
         retLen += len;
         retLen += 8;
      }
   }

   if(_mediaServerIP)
   {
      _mediaServerIP->getSerializeData(len);
      retLen += len;
      retLen += 8;
   }

   if(_origData)
   {
      _origData->getSerializeData(len);
      retLen += len;
      retLen += 8;
   }

   if(_dialedData)
   {
      _dialedData->getSerializeData(len);
      retLen += len;
      retLen += 8;
   }

   if(_targetData)
   {
      _targetData->getSerializeData(len);
      retLen += len;
      retLen += 8;
   }

   if(_contactData)
   {
      _contactData->getSerializeData(len);
      retLen += len;
      retLen += 8;
   }

   return retLen;
}

void INGwSpMsgSipSpecificAttr::_addTag(char *indata, AttributeTag intag, int inlen, 
                                const char *inval)
{
   int tag = htonl(intag);
   int len = htonl(inlen);
   memcpy(indata, &tag, sizeof(int));
   indata += sizeof(int);
   memcpy(indata, &len, sizeof(int));
   indata += sizeof(int);
   memcpy(indata, inval, inlen);
}

bool INGwSpMsgSipSpecificAttr::serialize(char *dataPtr, int currOffset, 
                                  int &newOffset, int, 
                                  bool)
{
   // <TOTAL_LEN><TAG><LENGTH><VALUE><TAG><LENGTH><VALUE>...

   bool retVal = true;

   dataPtr += currOffset;
   char *startPtr = dataPtr;
   newOffset = currOffset;

   int serLen = htonl(size());
   memcpy(dataPtr, &serLen, sizeof(int));
   dataPtr += sizeof(int);

   int len = 0;
   const char *data = NULL;

   if(_billingInfo)
   {
      data =_billingInfo->getSerializeData(len);
      _addTag(dataPtr, BILLING_INF0, len, data);
      dataPtr += 8;
      dataPtr += len;
   }

   if(_reasonInfo)
   {
      data = _reasonInfo->getSerializeData(len);
      _addTag(dataPtr, REASON_INFO, len, data);
      dataPtr += 8;
      dataPtr += len;
   }

   if(_contentType)
   {
      data = _contentType->getSerializeData(len);
      _addTag(dataPtr, CONTENT_TYPE, len, data);
      dataPtr += 8;
      dataPtr += len;
   }

   if(_contentBody)
   {
      data = _contentBody->getSerializeData(len);
      _addTag(dataPtr, CONTENT_BODY, len, data);
      dataPtr += 8;
      dataPtr += len;
   }

   if(_contact)
   {
      data = _contact->getSerializeData(len);
      _addTag(dataPtr, CONTACT, len, data);
      dataPtr += 8;
      dataPtr += len;
   }

   if(_asserted)
   {
      int locLen = htonl(_assertedLen);
      _addTag(dataPtr, ASSERTED_LEN, sizeof(int), (const char *)&locLen);
      dataPtr += 12;

      for(int idx = 0; idx < _assertedLen; idx++)
      {
         data = _asserted[idx]->getSerializeData(len);
         _addTag(dataPtr, ASSERTED, len, data);
         dataPtr += 8;
         dataPtr += len;
      }
   }

   if(_mediaServerIP)
   {
      data = _mediaServerIP->getSerializeData(len);
      _addTag(dataPtr, MEDIA_SERVER_IP, len, data);
      dataPtr += 8;
      dataPtr += len;
   }

   if(_origData)
   {
      data = _origData->getSerializeData(len);
      _addTag(dataPtr, ORIG_DATA, len, data);
      dataPtr += 8;
      dataPtr += len;
   }

   if(_dialedData)
   {
      data = _dialedData->getSerializeData(len);
      _addTag(dataPtr, DIALED_DATA, len, data);
      dataPtr += 8;
      dataPtr += len;
   }

   if(_targetData)
   {
      data = _targetData->getSerializeData(len);
      _addTag(dataPtr, TARGET_DATA, len, data);
      dataPtr += 8;
      dataPtr += len;
   }

   if(_contactData)
   {
      data = _contactData->getSerializeData(len);
      _addTag(dataPtr, CONTACT_DATA, len, data);
      dataPtr += 8;
      dataPtr += len;
   }

   newOffset += (dataPtr - startPtr);

   return true;
}

bool INGwSpMsgSipSpecificAttr::deserialize(const char *dataPtr, int currOffset, 
                                    int &newOffset, int)
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

   dataPtr += currOffset;
   newOffset = currOffset;

   int totalLength;
   memcpy(&totalLength, dataPtr, sizeof(int));
   totalLength = ntohl(totalLength);
   dataPtr += sizeof(int);

   totalLength -= sizeof(int);

   newOffset += (totalLength + sizeof(int));

   int consumedLen = 0;
   const char *end = dataPtr + totalLength;

   while(consumedLen < totalLength)
   {
      int tag; 
      memcpy(&tag, dataPtr, sizeof(int));
      dataPtr += sizeof(int);

      int length;
      memcpy(&length, dataPtr, sizeof(int));
      dataPtr += sizeof(int);
      length = ntohl(length);

      AttributeTag currTag = (AttributeTag) ntohl(tag);
      int currLen = length;


      if((dataPtr + length) > end)
      {
         logger.logMsg(ERROR_FLAG, 0, "Deserialize failed.");
         return false;
      }

      switch(currTag)
      {
         case BILLING_INF0:
         {
            _clearBillingInfo();

            _billingInfo = new INGwSpMsgSipBillingInfo();
            if(!_billingInfo->deserializeContent(dataPtr, currLen))
            {
               logger.logMsg(ERROR_FLAG, 0, "Billing info deserialize failed.");
               return false;
            }
         }
         break;

         case REASON_INFO:
         {
            _clearReasonInfo();
            _reasonInfo = new INGwSpMsgSipReasonInfo();
            if(!_reasonInfo->deserializeContent(dataPtr, currLen))
            {
               logger.logMsg(ERROR_FLAG, 0, "Reason info deserialize failed.");
               return false;
            }
         }
         break;

         case CONTENT_TYPE:
         {
            _clearContentType();
            _contentType = new INGwSpMsgSipStringInfo();
            if(!_contentType->deserializeContent(dataPtr, currLen))
            {
               logger.logMsg(ERROR_FLAG, 0, "ContentType deserialize failed.");
               return false;
            }
         }
         break;

         case CONTENT_BODY:
         {
            _clearContentBody();
            _contentBody = new INGwSpMsgSipStringInfo();
            if(!_contentBody->deserializeContent(dataPtr, currLen))
            {
               logger.logMsg(ERROR_FLAG, 0, "ContentBody deserialize failed.");
               return false;
            }
         }
         break;

         case CONTACT:
         {
            _clearContact();
            _contact = new INGwSpMsgSipStringInfo();
            if(!_contact->deserializeContent(dataPtr, currLen))
            {
               logger.logMsg(ERROR_FLAG, 0, "Contact deserialize failed.");
               return false;
            }
         }
         break;

         case ASSERTED_LEN:
         {
            _asserted = new INGwSpMsgSipStringInfo*[10];
         }
         break;

         case ASSERTED:
         {
            _asserted[_assertedLen] = new INGwSpMsgSipStringInfo();
            if(!_asserted[_assertedLen]->deserializeContent(dataPtr, currLen))
            {
               logger.logMsg(ERROR_FLAG, 0, "Asserted deserialize failed.");
               delete _asserted[_assertedLen];
               return false;
            }

            _assertedLen++;
         }
         break;

         case MEDIA_SERVER_IP:
         {
            _clearMediaServerIP();
            _mediaServerIP = new INGwSpMsgSipStringInfo();
            if(!_mediaServerIP->deserializeContent(dataPtr, currLen))
            {
               logger.logMsg(ERROR_FLAG, 0, "MediaSvrIP deserialize failed.");
               return false;
            }
         }
         break;

         case ORIG_DATA:
         {
            _clearOrigData();
            _origData = new INGwSpMsgSipAddrInfo();
            if(!_origData->deserializeContent(dataPtr, currLen))
            {
               logger.logMsg(ERROR_FLAG, 0, "OrigData deserialize failed.");
               return false;
            }
         }
         break;

         case DIALED_DATA:
         {
            _clearDialedData();
            _dialedData = new INGwSpMsgSipAddrInfo();
            if(!_dialedData->deserializeContent(dataPtr, currLen))
            {
               logger.logMsg(ERROR_FLAG, 0, "DialedData deserialize failed.");
               return false;
            }
         }
         break;

         case TARGET_DATA:
         {
            _clearTargetData();
            _targetData = new INGwSpMsgSipAddrInfo();
            if(!_targetData->deserializeContent(dataPtr, currLen))
            {
               logger.logMsg(ERROR_FLAG, 0, "TargetData deserialize failed.");
               return false;
            }
         }
         break;

         case CONTACT_DATA:
         {
            _clearContactData();
            _contactData = new INGwSpMsgSipAddrInfo();
            if(!_contactData->deserializeContent(dataPtr, currLen))
            {
               logger.logMsg(ERROR_FLAG, 0, "ContactData deserialize failed.");
               return false;
            }
         }
         break;

         default:
         {
            logger.logMsg(ERROR_FLAG, 0, "Unknown tag. [%d]", currTag);
            return false;
         }
      }

      dataPtr += currLen;
      consumedLen += (currLen + 8);
   }

   return true;
}

std::string INGwSpMsgSipSpecificAttr::toLog() const
{
   std::ostringstream oStr;

   oStr << "SipAttrLen: [" << size() << "]";

   if(_billingInfo)
   {
      oStr << " Billing" << _billingInfo->toLog();
   }

   if(_reasonInfo)
   {
      oStr << " Reason" << _reasonInfo->toLog();
   }

   if(_contentType)
   {
      oStr << " ContentType" << _contentType->toLog();
   }

   if(_contentBody)
   {
      int len = 0;
      _contentBody->getSerializeData(len);
      oStr << " ContentBodyLen [" << len << "]";
   }

   if(_contact)
   {
      oStr << " Contact" << _contact->toLog();
   }

   if(_asserted)
   {
      oStr << " NumAsserted [" << _assertedLen << "]";

      for(int idx = 0; idx < _assertedLen; idx++)
      {
         oStr << " Idx [" << idx << "]";

         oStr << " Asserted" << _asserted[idx]->toLog();
      }
   }

   if(_mediaServerIP)
   {
      oStr << " MediaIP" << _mediaServerIP->toLog();
   }

   if(_origData)
   {
      oStr << " OrigData" << _origData->toLog();
   }

   if(_dialedData)
   {
      oStr << " DialedData" << _dialedData->toLog();
   }

   if(_targetData)
   {
      oStr << " TargetData" << _targetData->toLog();
   }

   if(_contactData)
   {
      oStr << " ContactData" << _contactData->toLog();
   }

   oStr << "]";
   return oStr.str();
}

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
//     File:     INGwSpMsgParseInvite.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   08/12/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipMsgHandler");

#include <INGwSipMsgHandler/INGwSpMsgInviteHandler.h>

#include <INGwSipProvider/INGwSpSipConnection.h>
#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpSipContext.h>
#include <INGwSipProvider/INGwSpSipProviderConfig.h>
#include <INGwSipProvider/INGwSpSipHeaderPolicy.h>
#include <INGwSipProvider/INGwSpSipUtil.h>
#include <INGwSipMsgHandler/INGwSpMsgSipSpecificAttr.h>

#include <strstream>
#include <fstream>

extern const char *DEFAULT_ADDRESS;

void INGwSpMsgInviteHandler::_parseFromHdr(INGwSpSipConnection *conn, SipHeader *hdr,
                                       INGwSpAddress &addr)
{
   SipError err;
   SipAddrSpec *addrSpec = NULL;

   SipFromHeader *fromHdr = (SipFromHeader *)(hdr ->pHeader);

   if(fromHdr->pDispName != NULL)
   {
      addr.setDisplayName(fromHdr->pDispName);
   }

   if(sip_getAddrSpecFromFromHdr(hdr, &addrSpec, &err) == SipFail)
   {
      logger.logINGwMsg(conn->mLogFlag, ERROR_FLAG, 0, 
                      "Error getting addrspec from FromHDR");
      addr.setAddress(DEFAULT_ADDRESS);
      return;
   }

	 SipAddrSpec *lAddrSpec = NULL;
	 sip_initSipAddrSpec(&lAddrSpec, SipAddrSipUri, &err);
	 sip_cloneSipAddrSpec(lAddrSpec, addrSpec, &err);

   // Set the from addr specs to be used for setting request uri
   conn->setFromAddSpec(lAddrSpec);

	 sip_freeSipAddrSpec(addrSpec);


   RSI_NSP_SIP::ProtocolTreatment inProtocol;
   TelUrl *telUrl = NULL;
   bool globalNum = false;
   SipUrl *sipUrl = NULL;

   //Now check the Type.

   if((addrSpec->dType == SipAddrSipUri) ||
      (addrSpec->dType == SipAddrSipSUri))
   {
      inProtocol = RSI_NSP_SIP::PROTO_SIP;

      if(sip_getUrlFromAddrSpec(addrSpec, &sipUrl, &err) == SipFail)
      {
         logger.logINGwMsg(conn->mLogFlag, ERROR_FLAG, 0, 
                         "Error getting sipURL from FromHDR");
         addr.setAddress(DEFAULT_ADDRESS);
         return;
      }

      sip_freeSipUrl(sipUrl);
   }
   else
   {
      inProtocol = RSI_NSP_SIP::PROTO_TEL;

      if(sip_getTelUrlFromAddrSpec(addrSpec, &telUrl, &err) == SipFail)
      {
         logger.logINGwMsg(conn->mLogFlag, ERROR_FLAG, 0, 
                         "Error getting telURL from FromHDR");
         addr.setAddress(DEFAULT_ADDRESS);
         return;
      }

      if((telUrl->pGlobal != NULL) && (telUrl->pGlobal->pBaseNo != NULL))
      {
         globalNum = true;
      }
      else if((telUrl->pLocal != NULL) && 
              (telUrl->pLocal->pLocalPhoneDigit != NULL))
      {
         globalNum = false;
      }
      else
      {
         logger.logINGwMsg(conn->mLogFlag, ERROR_FLAG, 0, 
                         "Not a Global/Local Tel num.");
         sip_freeTelUrl(telUrl);
         addr.setAddress(DEFAULT_ADDRESS);
         return;
      }

      m_oFromTel = telUrl; 
   }

   const char *userpart = NULL;
   SipList *params = NULL;
   SipList *areaSpecifier = NULL;

   INGwSpMsgSipAddrInfo *outData = NULL;

   if(INGwSpSipProviderConfig::isSendFromParams())
   {
      outData = new INGwSpMsgSipAddrInfo;
   }

   switch(inProtocol)
   {
      case RSI_NSP_SIP::PROTO_SIP:
      {
         userpart = (const char *)sipUrl->pUser;
         params = &(sipUrl->slParam);

         if(INGwSpSipProviderConfig::isSendFromParams())
         {
            outData->addProtocol(INGwSpMsgSipAddrInfo::SIP);
         }
      }
      break;

      case RSI_NSP_SIP::PROTO_TEL:
      {
         if(INGwSpSipProviderConfig::isSendFromParams())
         {
            outData->addProtocol(INGwSpMsgSipAddrInfo::TEL);
         }

         if(globalNum)
         {
            userpart = (const char *)telUrl->pGlobal->pBaseNo;
            params = &(telUrl->pGlobal->slParams);
            areaSpecifier = &(telUrl->pGlobal->slAreaSpecifier);

            if(INGwSpSipProviderConfig::isSendFromParams())
            {
               TelGlobalNum *glbNum = telUrl->pGlobal;

               if(glbNum->pIsdnSubAddr)
               {
                  outData->addParam("isub", 4, glbNum->pIsdnSubAddr,
                                    strlen(glbNum->pIsdnSubAddr));
               }

               if(glbNum->pPostDial)
               {
                  outData->addParam("postd", 5, glbNum->pPostDial,
                                    strlen(glbNum->pPostDial));
               }
            }
         }
         else
         {
            userpart = (const char *)telUrl->pLocal->pLocalPhoneDigit;
            params = &(telUrl->pLocal->slParams);
            areaSpecifier = &(telUrl->pLocal->slAreaSpecifier);

            if(INGwSpSipProviderConfig::isSendFromParams())
            {
               TelLocalNum *locNum = telUrl->pLocal;

               if(locNum->pIsdnSubAddr)
               {
                  outData->addParam("isub", 4, locNum->pIsdnSubAddr,
                                    strlen(locNum->pIsdnSubAddr));
               }

               if(locNum->pPostDial)
               {
                  outData->addParam("postd", 5, locNum->pPostDial,
                                    strlen(locNum->pPostDial));
               }
            }
         }
      }
      break;
   }

   if(areaSpecifier != NULL)
   {
      if(INGwSpSipProviderConfig::isSendFromParams())
      {
         for(SipListElement *curr = areaSpecifier->head; curr != SIP_NULL; 
             curr=curr->next)
         {
            const char *val = (const char *)curr->pData;

            outData->addParam("phone-context", 13, val,
                              strlen(val));
         }
      }
      else if(INGwSpSipProviderConfig::isSendPhoneContext())
      {
         logger.logINGwMsg(conn->mLogFlag, TRACE_FLAG, 0, 
                         "No of area specifiers [%d]", areaSpecifier->size);

         for(SipListElement *curr = areaSpecifier->head; curr != SIP_NULL; 
             curr=curr->next)
         {
            if(outData == NULL)
            {
               outData = new INGwSpMsgSipAddrInfo;
            }

            const char *val = (const char *)curr->pData;

            outData->addParam("phone-context", 13, val,
                              strlen(val));
         }
      }
   }

   if(userpart == NULL || *userpart == '\0')
   {
      userpart = DEFAULT_ADDRESS;
   }

   const char *bgId = "";

   bool isCPC = false;

   //conn->setChargeNumber(UNKNOWN_BGT);

   for(SipListElement *curr = params->head; curr != SIP_NULL; curr=curr->next)
   {
      SipParam *currParam = (SipParam *)(curr->pData);
      const char *name = currParam->pName;
      const char *val = "";

      if(currParam->slValue.head)
      {
         val = (const char *)currParam->slValue.head->pData;
      }

      if(INGwSpSipProviderConfig::isSendFromParams())
      {
         int nameLen = strlen(name);

         if(currParam->slValue.head == NULL)
         {
            outData->addParam(name, nameLen, NULL, 0);
         }
         else
         {
            for(SipListElement *iter = currParam->slValue.head; 
                iter != SIP_NULL; iter=iter->next)
            {
               const char *val = (const char *)iter->pData;

               outData->addParam(name, nameLen, val, strlen(val));
            }
         }
      }

      switch(*name)
      {
         case 'b':
         case 'B':
         {
            if(strcasecmp(BP_BGT_TAG, name) == 0)
            {
               if(strcasecmp(BP_BGT_PRIVATE_VAL, val) == 0)
               {
                  //conn->setChargeNumber(PRIVATE_BGT);
               }
               else if(strcasecmp(BP_BGT_PUBLIC_VAL, val) == 0)
               {
                  //conn->setChargeNumber(PUBLIC_BGT);
               }
            }
            else if((INGwSpSipProviderConfig::m_bgidProcessingFlag) &&
                    (strcasecmp(BP_BGID_TAG, name) == 0))
            {
               bgId = val;
               addr.setBGId(bgId);
            }
         }
         break;

         case 'c':
         case 'C':
         {
            if(strcasecmp(BP_CPC_TAG, name) == 0)
            {
               if(strcasecmp(OLI_CPC_PAYPHONE_TAG, val) == 0)
               {
                  //conn->setOLI (OLI_CPC_PAYPHONE);
               }
               else
               { 
                  //conn->setOLI (OLI_CPC_OTHER); 
               }

               isCPC = true;
            }
            else if(strcasecmp(BP_CIC_TAG, name) == 0)
            {
               //conn->setCIC(val);
            }
         }
         break;

         case 'i':
         case 'I':
         {
            if(!isCPC && (strcasecmp(BP_OLI_TAG_1, name) == 0))
            {
               //conn->setOLI(atoi(val));
            }
         }
         break;

         case 'n':
         case 'N':
         {
            if(strcasecmp(BP_NPDI_TAG, name) == 0)
            {
               //conn->setLnpQueryInd(true);
            }
         }
         break;

         case 'o':
         case 'O':
         {
            if(strcasecmp(BP_OLI_TAG, name) == 0)
            {
               if(!INGwSpSipProviderConfig::isSendFromParams() &&
                  INGwSpSipProviderConfig::isSendOLI())
               {
                  if(outData == NULL)
                  {
                     outData = new INGwSpMsgSipAddrInfo;
                  }

                  outData->addParam(BP_OLI_TAG, 3, val, strlen(val));
               }

               if(!isCPC)
               {
                  //conn->setOLI(atoi(val));
               }
            }
         }
         break;

         case 'p':
         case 'P':
         {
            if(strcasecmp("phone-context", name) == 0)
            {
               if(!INGwSpSipProviderConfig::isSendFromParams() &&
                  INGwSpSipProviderConfig::isSendPhoneContext())
               {
                  if(outData == NULL)
                  {
                     outData = new INGwSpMsgSipAddrInfo;
                  }

                  outData->addParam("phone-context", 13, val, strlen(val));
               }
            }
         }
         break;

         case 'r':
         case 'R':
         {
            if(strcasecmp(BP_LRN_TAG, name) == 0)
            {
               //conn->setLRN(val);
            }
         }
         break;
      }
   }

   if((bgId != NULL) && (INGwSpSipProviderConfig::m_bgidAppendingFlag))
   {
      char finalAddr[SIZE_OF_ADDR];

      if(globalNum)
      {
         snprintf(finalAddr, SIZE_OF_ADDR, "+%s_%s", bgId, userpart);
      }
      else
      {
         snprintf(finalAddr, SIZE_OF_ADDR, "%s_%s", bgId, userpart);
      }

      addr.setAddress(finalAddr);
   }
   else
   {
      if(globalNum)
      {
         char finalAddr[SIZE_OF_ADDR];
         snprintf(finalAddr, SIZE_OF_ADDR, "+%s", userpart);
         addr.setAddress(finalAddr);
      }
      else
      {
         addr.setAddress(userpart);
      }
   }

   if(outData != NULL)
   {
      INGwSpMsgSipSpecificAttr *connSipAttr = conn->getSipSpecificAttr(); 
      connSipAttr->setOrigData(outData); 
   }

   return;
}

void INGwSpMsgInviteHandler::_parseToHdr(INGwSpSipConnection *conn, SipHeader *hdr,
                                     INGwSpAddress &addr)
{
   SipError err;
   SipAddrSpec *addrSpec = NULL;

   SipToHeader *toHdr = (SipToHeader *)(hdr ->pHeader);

   if(toHdr->pDispName != NULL)
   {
      addr.setDisplayName(toHdr->pDispName);
   }

   if(sip_getAddrSpecFromToHdr(hdr, &addrSpec, &err) == SipFail)
   {
      logger.logINGwMsg(conn->mLogFlag, ERROR_FLAG, 0, 
                      "Error getting addrspec from ToHDR");
      addr.setAddress(DEFAULT_ADDRESS);
      return;
   }

   sip_freeSipAddrSpec(addrSpec);

   RSI_NSP_SIP::ProtocolTreatment inProtocol;
   TelUrl *telUrl = NULL;
   bool globalNum = false;
   SipUrl *sipUrl = NULL;

   //Now check the Type.

   if((addrSpec->dType == SipAddrSipUri) ||
      (addrSpec->dType == SipAddrSipSUri))
   {
      inProtocol = RSI_NSP_SIP::PROTO_SIP;

      if(sip_getUrlFromAddrSpec(addrSpec, &sipUrl, &err) == SipFail)
      {
         logger.logINGwMsg(conn->mLogFlag, ERROR_FLAG, 0, 
                         "Error getting sipURL from ToHDR");
         addr.setAddress(DEFAULT_ADDRESS);
         return;
      }

      sip_freeSipUrl(sipUrl);
   }
   else
   {
      inProtocol = RSI_NSP_SIP::PROTO_TEL;

      if(sip_getTelUrlFromAddrSpec(addrSpec, &telUrl, &err) == SipFail)
      {
         logger.logINGwMsg(conn->mLogFlag, ERROR_FLAG, 0, 
                         "Error getting telURL from FromHDR");
         addr.setAddress(DEFAULT_ADDRESS);
         return;
      }

      if((telUrl->pGlobal != NULL) && (telUrl->pGlobal->pBaseNo != NULL))
      {
         globalNum = true;
      }
      else if((telUrl->pLocal != NULL) && 
              (telUrl->pLocal->pLocalPhoneDigit != NULL))
      {
         globalNum = false;
      }
      else
      {
         logger.logINGwMsg(conn->mLogFlag, ERROR_FLAG, 0, 
                         "Not a Global/Local Tel num.");
         sip_freeTelUrl(telUrl);
         addr.setAddress(DEFAULT_ADDRESS);
         return;
      }

      m_oToTel = telUrl; 
   }

   const char *userpart = NULL;
   SipList *params = NULL;
   SipList *areaSpecifier = NULL;

   INGwSpMsgSipAddrInfo *outData = NULL;

   if(INGwSpSipProviderConfig::isSendToParams())
   {
      outData = new INGwSpMsgSipAddrInfo;
   }

   switch(inProtocol)
   {
      case RSI_NSP_SIP::PROTO_SIP:
      {
         userpart = (const char *)sipUrl->pUser;
         params = &(sipUrl->slParam);

         if(INGwSpSipProviderConfig::isSendToParams())
         {
            outData->addProtocol(INGwSpMsgSipAddrInfo::SIP);
         }
      }
      break;

      case RSI_NSP_SIP::PROTO_TEL:
      {
         if(INGwSpSipProviderConfig::isSendToParams())
         {
            outData->addProtocol(INGwSpMsgSipAddrInfo::TEL);
         }

         if(globalNum)
         {
            userpart = (const char *)telUrl->pGlobal->pBaseNo;
            params = &(telUrl->pGlobal->slParams);
            areaSpecifier = &(telUrl->pGlobal->slAreaSpecifier);

            if(INGwSpSipProviderConfig::isSendToParams())
            {
               TelGlobalNum *glbNum = telUrl->pGlobal;

               if(glbNum->pIsdnSubAddr)
               {
                  outData->addParam("isub", 4, glbNum->pIsdnSubAddr,
                                    strlen(glbNum->pIsdnSubAddr));
               }

               if(glbNum->pPostDial)
               {
                  outData->addParam("postd", 5, glbNum->pPostDial,
                                    strlen(glbNum->pPostDial));
               }
            }
         }
         else
         {
            userpart = (const char *)telUrl->pLocal->pLocalPhoneDigit;
            params = &(telUrl->pLocal->slParams);
            areaSpecifier = &(telUrl->pLocal->slAreaSpecifier);

            if(INGwSpSipProviderConfig::isSendToParams())
            {
               TelLocalNum *locNum = telUrl->pLocal;

               if(locNum->pIsdnSubAddr)
               {
                  outData->addParam("isub", 4, locNum->pIsdnSubAddr,
                                    strlen(locNum->pIsdnSubAddr));
               }

               if(locNum->pPostDial)
               {
                  outData->addParam("postd", 5, locNum->pPostDial,
                                    strlen(locNum->pPostDial));
               }
            }
         }
      }
      break;
   }

   if(areaSpecifier != NULL)
   {
      if(INGwSpSipProviderConfig::isSendToParams())
      {
         for(SipListElement *curr = areaSpecifier->head; curr != SIP_NULL; 
             curr=curr->next)
         {
            const char *val = (const char *)curr->pData;

            outData->addParam("phone-context", 13, val,
                              strlen(val));
         }
      }
      else if(INGwSpSipProviderConfig::isSendPhoneContext())
      {
         for(SipListElement *curr = areaSpecifier->head; curr != SIP_NULL; 
             curr=curr->next)
         {
            if(outData == NULL)
            {
               outData = new INGwSpMsgSipAddrInfo;
            }

            const char *val = (const char *)curr->pData;

            outData->addParam("phone-context", 13, val,
                              strlen(val));
         }
      }
   }

   if(userpart == NULL || *userpart == '\0')
   {
      userpart = DEFAULT_ADDRESS;
   }

   const char *bgId = "";

   for(SipListElement *curr = params->head; curr != SIP_NULL; curr=curr->next)
   {
      SipParam *currParam = (SipParam *)(curr->pData);
      const char *name = currParam->pName;
      const char *val = "";

      if(currParam->slValue.head)
      {
         val = (const char *)currParam->slValue.head->pData;
      }

      if(INGwSpSipProviderConfig::isSendToParams())
      {
         int nameLen = strlen(name);

         if(currParam->slValue.head == NULL)
         {
            outData->addParam(name, nameLen, NULL, 0);
         }
         else
         {
            for(SipListElement *iter = currParam->slValue.head; 
                iter != SIP_NULL; iter=iter->next)
            {
               const char *val = (const char *)iter->pData;

               outData->addParam(name, nameLen, val, strlen(val));
            }
         }
      }

      switch(*name)
      {
         case 'b':
         case 'B':
         {
            if((INGwSpSipProviderConfig::m_bgidProcessingFlag) &&
               (strcasecmp(BP_BGID_TAG, name) == 0))
            {
               bgId = val;
               addr.setBGId(bgId);
            }
         }
         break;

         case 'o':
         case 'O':
         {
            if((!INGwSpSipProviderConfig::isSendToParams()) &&
               INGwSpSipProviderConfig::isSendOLI() && 
               (strcasecmp(BP_OLI_TAG, name) == 0))
            {
               if(outData == NULL)
               {
                  outData = new INGwSpMsgSipAddrInfo;
               }

               outData->addParam(BP_OLI_TAG, 3, val, strlen(val));
            }
         }
         break;

         case 'p':
         case 'P':
         {
            if((!INGwSpSipProviderConfig::isSendToParams()) &&
               INGwSpSipProviderConfig::isSendPhoneContext() &&
               (strcasecmp("phone-context", name) == 0))
            {
               if(outData == NULL)
               {
                  outData = new INGwSpMsgSipAddrInfo;
               }

               outData->addParam("phone-context", 13, val, strlen(val));
            }
         }
         break;
      }
   }

   if((bgId != NULL) && (INGwSpSipProviderConfig::m_bgidAppendingFlag))
   {
      char finalAddr[SIZE_OF_ADDR];

      if(globalNum)
      {
         snprintf(finalAddr, SIZE_OF_ADDR, "+%s_%s", bgId, userpart);
      }
      else
      {
         snprintf(finalAddr, SIZE_OF_ADDR, "%s_%s", bgId, userpart);
      }

      addr.setAddress(finalAddr);
   }
   else
   {
      if(globalNum)
      {
         char finalAddr[SIZE_OF_ADDR];
         snprintf(finalAddr, SIZE_OF_ADDR, "+%s", userpart);
         addr.setAddress(finalAddr);
      }
      else
      {
         addr.setAddress(userpart);
      }
   }

   if(outData != NULL)
   {
      INGwSpMsgSipSpecificAttr *connSipAttr = conn->getSipSpecificAttr(); 
      connSipAttr->setDialedData(outData); 
   }

   return;
}

void INGwSpMsgInviteHandler::_parseReqURI(INGwSpSipConnection *conn, 
                                      SipAddrSpec *addrSpec, INGwSpAddress &addr)
{
   SipError err;

   RSI_NSP_SIP::ProtocolTreatment inProtocol;
   TelUrl *telUrl = NULL;
   bool globalNum = false;
   SipUrl *sipUrl = NULL;

   //Now check the Type.

   if((addrSpec->dType == SipAddrSipUri) ||
      (addrSpec->dType == SipAddrSipSUri))
   {
      inProtocol = RSI_NSP_SIP::PROTO_SIP;

      if(sip_getUrlFromAddrSpec(addrSpec, &sipUrl, &err) == SipFail)
      {
         logger.logINGwMsg(conn->mLogFlag, ERROR_FLAG, 0, 
                         "Error getting sipURL from ToHDR");
         addr.setAddress(DEFAULT_ADDRESS);
         return;
      }

      sip_initSipUrl(&m_oInvReqLineUrl, &err);
      sip_cloneSipUrl(m_oInvReqLineUrl, sipUrl, &err);

      sip_freeSipUrl(sipUrl);
   }
   else
   {
      inProtocol = RSI_NSP_SIP::PROTO_TEL;

      if(sip_getTelUrlFromAddrSpec(addrSpec, &telUrl, &err) == SipFail)
      {
         logger.logINGwMsg(conn->mLogFlag, ERROR_FLAG, 0, 
                         "Error getting telURL from ReqURI");
         addr.setAddress(DEFAULT_ADDRESS);
         return;
      }

      if((telUrl->pGlobal != NULL) && (telUrl->pGlobal->pBaseNo != NULL))
      {
         globalNum = true;
      }
      else if((telUrl->pLocal != NULL) && 
              (telUrl->pLocal->pLocalPhoneDigit != NULL))
      {
         globalNum = false;
      }
      else
      {
         logger.logINGwMsg(conn->mLogFlag, ERROR_FLAG, 0, 
                         "Not a Global/Local Tel num.");
         sip_freeTelUrl(telUrl);
         addr.setAddress(DEFAULT_ADDRESS);
         return;
      }

      m_oReqTel = telUrl; 
   }

   const char *userpart = NULL;
   SipList *params = NULL;
   SipList *areaSpecifier = NULL;

   INGwSpMsgSipAddrInfo *outData = NULL;

   if(INGwSpSipProviderConfig::isSendReqURIParams())
   {
      outData = new INGwSpMsgSipAddrInfo;
   }

   switch(inProtocol)
   {
      case RSI_NSP_SIP::PROTO_SIP:
      {
         userpart = (const char *)sipUrl->pUser;
         params = &(sipUrl->slParam);

         if(INGwSpSipProviderConfig::isSendReqURIParams())
         {
            outData->addProtocol(INGwSpMsgSipAddrInfo::SIP);
         }
      }
      break;

      case RSI_NSP_SIP::PROTO_TEL:
      {
         if(INGwSpSipProviderConfig::isSendReqURIParams())
         {
            outData->addProtocol(INGwSpMsgSipAddrInfo::TEL);
         }

         if(globalNum)
         {
            userpart = (const char *)telUrl->pGlobal->pBaseNo;
            params = &(telUrl->pGlobal->slParams);
            areaSpecifier = &(telUrl->pGlobal->slAreaSpecifier);

            if(INGwSpSipProviderConfig::isSendReqURIParams())
            {
               TelGlobalNum *glbNum = telUrl->pGlobal;

               if(glbNum->pIsdnSubAddr)
               {
                  outData->addParam("isub", 4, glbNum->pIsdnSubAddr,
                                    strlen(glbNum->pIsdnSubAddr));
               }

               if(glbNum->pPostDial)
               {
                  outData->addParam("postd", 5, glbNum->pPostDial,
                                    strlen(glbNum->pPostDial));
               }
            }
         }
         else
         {
            userpart = (const char *)telUrl->pLocal->pLocalPhoneDigit;
            params = &(telUrl->pLocal->slParams);
            areaSpecifier = &(telUrl->pLocal->slAreaSpecifier);

            if(INGwSpSipProviderConfig::isSendReqURIParams())
            {
               TelLocalNum *locNum = telUrl->pLocal;

               if(locNum->pIsdnSubAddr)
               {
                  outData->addParam("isub", 4, locNum->pIsdnSubAddr,
                                    strlen(locNum->pIsdnSubAddr));
               }

               if(locNum->pPostDial)
               {
                  outData->addParam("postd", 5, locNum->pPostDial,
                                    strlen(locNum->pPostDial));
               }
            }
         }
      }
      break;
   }

   if(areaSpecifier != NULL)
   {
      if(INGwSpSipProviderConfig::isSendReqURIParams())
      {
         for(SipListElement *curr = areaSpecifier->head; curr != SIP_NULL; 
             curr=curr->next)
         {
            const char *val = (const char *)curr->pData;

            outData->addParam("phone-context", 13, val,
                              strlen(val));
         }
      }
      else if(INGwSpSipProviderConfig::isSendPhoneContext())
      {
         for(SipListElement *curr = areaSpecifier->head; curr != SIP_NULL; 
             curr=curr->next)
         {
            if(outData == NULL)
            {
               outData = new INGwSpMsgSipAddrInfo;
            }

            const char *val = (const char *)curr->pData;

            outData->addParam("phone-context", 13, val,
                              strlen(val));
         }
      }
   }

   if(userpart == NULL || *userpart == '\0')
   {
      userpart = DEFAULT_ADDRESS;
   }

   const char *bgId = "";

   bool bgtFlag = false;

   for(SipListElement *curr = params->head; curr != SIP_NULL; curr=curr->next)
   {
      SipParam *currParam = (SipParam *)(curr->pData);
      const char *name = currParam->pName;
      const char *val = "";

      if(currParam->slValue.head)
      {
         val = (const char *)currParam->slValue.head->pData;
      }

      if(INGwSpSipProviderConfig::isSendReqURIParams())
      {
         int nameLen = strlen(name);

         if(currParam->slValue.head == NULL)
         {
            outData->addParam(name, nameLen, NULL, 0);
         }
         else
         {
            for(SipListElement *iter = currParam->slValue.head; 
                iter != SIP_NULL; iter=iter->next)
            {
               const char *val = (const char *)iter->pData;

               outData->addParam(name, nameLen, val, strlen(val));
            }
         }
      }

      switch(*name)
      {
         case 'b':
         case 'B':
         {
            if(strcasecmp(BP_BGT_TAG, name) == 0)
            {
               bgtFlag = true;

               if(strcasecmp(BP_BGT_PRIVATE_VAL, val) == 0)
               {
                  //conn->appendChargeNumber(PRIVATE_BGT);
               }
               else if(strcasecmp(BP_BGT_PUBLIC_VAL, val) == 0)
               {
                  //conn->appendChargeNumber(PUBLIC_BGT);
               }
               else
               {
                  //conn->appendChargeNumber(UNKNOWN_BGT);
               }
            }
            else if((INGwSpSipProviderConfig::m_bgidProcessingFlag) &&
                    (strcasecmp(BP_BGID_TAG, name) == 0))
            {
               bgId = val;
               addr.setBGId(bgId);
            }
         }
         break;

         case 'o':
         case 'O':
         {
            if((!INGwSpSipProviderConfig::isSendReqURIParams()) &&
               INGwSpSipProviderConfig::isSendOLI() && 
               (strcasecmp(BP_OLI_TAG, name) == 0))
            {
               if(outData == NULL)
               {
                  outData = new INGwSpMsgSipAddrInfo;
               }

               outData->addParam(BP_OLI_TAG, 3, val, strlen(val));
            }
         }
         break;

         case 'p':
         case 'P':
         {
            if((!INGwSpSipProviderConfig::isSendReqURIParams()) &&
               INGwSpSipProviderConfig::isSendPhoneContext() &&
               (strcasecmp("phone-context", name) == 0))
            {
               if(outData == NULL)
               {
                  outData = new INGwSpMsgSipAddrInfo;
               }

               outData->addParam("phone-context", 13, val, strlen(val));
            }
         }
         break;
      }
   }

   if(!bgtFlag)
   {
      //conn->appendChargeNumber(UNKNOWN_BGT);
   }

   if((bgId != NULL) && (INGwSpSipProviderConfig::m_bgidAppendingFlag))
   {
      char finalAddr[SIZE_OF_ADDR];

      if(globalNum)
      {
         snprintf(finalAddr, SIZE_OF_ADDR, "+%s_%s", bgId, userpart);
      }
      else
      {
         snprintf(finalAddr, SIZE_OF_ADDR, "%s_%s", bgId, userpart);
      }

      addr.setAddress(finalAddr);
   }
   else
   {
      if(globalNum)
      {
         char finalAddr[SIZE_OF_ADDR];
         snprintf(finalAddr, SIZE_OF_ADDR, "+%s", userpart);
         addr.setAddress(finalAddr);
      }
      else
      {
         addr.setAddress(userpart);
      }
   }

   if(outData != NULL)
   {
      INGwSpMsgSipSpecificAttr *connSipAttr = conn->getSipSpecificAttr(); 
      connSipAttr->setTargetData(outData); 
   }

   return;
}

void INGwSpMsgInviteHandler::_parseContact(SipMessage *msg, INGwSpSipConnection *conn)
{
   SipError err;

//   if(!INGwSpSipProviderConfig::isSendContact() &&
//      !INGwSpSipProviderConfig::isSendContactAccess() &&
//      !INGwSpSipProviderConfig::isSendContactParams())
//   {
//      return;
//   }

   TelUrl *telUrl = NULL;
   SipContactHeader *currContact = NULL;

   SipList &contacts = msg->pGeneralHdr->slContactHdr;

   for(SipListElement *curr = contacts.head; curr != SIP_NULL;
       curr=curr->next)
   {
      currContact = (SipContactHeader *)(curr->pData);
            
      if(currContact == NULL)
      {
         continue;
      }
   }

   if(currContact == NULL)
   {
      return;
   }

   SipAddrSpec *addrSpec = currContact->pAddrSpec;

   if(addrSpec == NULL)
   {
      return;
   }

   SipList *params = NULL;
   SipList *areaSpecifier = NULL;
   INGwSpMsgSipAddrInfo *outData = NULL;

/*

	 //PANKAJ TO remove
	 SipList *hdrParam = &currContact->slContactParam;

	 std::ostrstream output;

   for(SipListElement *curr = hdrParam->head; curr != SIP_NULL; curr=curr->next)
   {
      SipParam *currParam = (SipParam *)(curr->pData);
      const char *name = currParam->pName;
      const char *val = "";
			output << endl << "Contact Hdr Param : ";

			output << " Name : " << name;

      if(currParam->slValue.head)
      {
         val = (const char *)currParam->slValue.head->pData;
      }

      {
         int nameLen = strlen(name);

         if(currParam->slValue.head == NULL)
         {
			       output << " Val  : " << "NOT PRESENT ";
         }
         else
         {
            for(SipListElement *iter = currParam->slValue.head; 
                iter != SIP_NULL; iter=iter->next)
            {
               const char *val = (const char *)iter->pData;
			         output << " Val  : " << val;
            }
         }
      }

   }

   logger.logINGwMsg(conn->mLogFlag, TRACE_FLAG, 0, 
                         "CONTACT HDR PARAM [%s]", output.str());
*/
   switch(addrSpec->dType)
   {
      case SipAddrSipUri:
      case SipAddrSipSUri:
      {
         const char *contact = (const char *)addrSpec->u.pSipUrl->pUser;

         if(contact == NULL)
         {
            return;
         }

         if(INGwSpSipProviderConfig::isSendContact())
         {
            INGwSpMsgSipSpecificAttr *connSipAttr = conn->getSipSpecificAttr();
            connSipAttr->setContact(contact, strlen(contact));
         }

         params = &(addrSpec->u.pSipUrl->slParam);
				 conn->setContactHdr(contact, strlen(contact));
				 //PANKAJ
				 INGwSpSipUtil::setContactAddSpec(currContact, conn);
      }
      break;

      case SipAddrReqUri:
      {
         if(sip_getTelUrlFromAddrSpec(addrSpec, &telUrl, &err) == SipFail)
         {
            return;
         }

         const char *contact = NULL;

         bool prefixFlag = false;
         TelGlobalNum *glbNum = telUrl->pGlobal;

         if(glbNum == NULL)
         {
            TelLocalNum *locNum = telUrl->pLocal;
            params = &(telUrl->pLocal->slParams);
            areaSpecifier = &(telUrl->pLocal->slAreaSpecifier);

            if(locNum == NULL)
            {
               sip_freeTelUrl(telUrl);
               return;
            }

            if(INGwSpSipProviderConfig::isSendContactParams())
            {
               TelLocalNum *locNum = telUrl->pLocal;

               if(locNum->pIsdnSubAddr)
               {
                  if(outData == NULL)
                  {
                     outData = new INGwSpMsgSipAddrInfo;
                  }

                  outData->addParam("isub", 4, locNum->pIsdnSubAddr,
                                    strlen(locNum->pIsdnSubAddr));
               }

               if(locNum->pPostDial)
               {
                  if(outData == NULL)
                  {
                     outData = new INGwSpMsgSipAddrInfo;
                  }

                  outData->addParam("postd", 5, locNum->pPostDial,
                                    strlen(locNum->pPostDial));
               }
            }

            contact = locNum->pLocalPhoneDigit;
         }
         else
         {
            params = &(telUrl->pGlobal->slParams);
            areaSpecifier = &(telUrl->pGlobal->slAreaSpecifier);

            contact = glbNum->pBaseNo;
            prefixFlag = true;

            if(contact == NULL)
            {
               sip_freeTelUrl(telUrl);
               return;
            }

            if(INGwSpSipProviderConfig::isSendContactParams())
            {
               if(glbNum->pIsdnSubAddr)
               {
                  if(outData == NULL)
                  {
                     outData = new INGwSpMsgSipAddrInfo;
                  }

                  outData->addParam("isub", 4, glbNum->pIsdnSubAddr,
                                    strlen(glbNum->pIsdnSubAddr));
               }

               if(glbNum->pPostDial)
               {
                  if(outData == NULL)
                  {
                     outData = new INGwSpMsgSipAddrInfo;
                  }

                  outData->addParam("postd", 5, glbNum->pPostDial,
                                    strlen(glbNum->pPostDial));
               }
            }
         }

         if(INGwSpSipProviderConfig::isSendContact())
         {
            INGwSpMsgSipSpecificAttr *connSipAttr = conn->getSipSpecificAttr();

            if(prefixFlag)
            {
               connSipAttr->setContact(contact, strlen(contact), "+", 1);
            }
            else
            {
               connSipAttr->setContact(contact, strlen(contact));
            }
         }
         conn->setContactHdr(contact, strlen(contact));
				 //PANKAJ
				 INGwSpSipUtil::setContactAddSpec(currContact, conn);
         break;
      }
   }

   // CHECK if contact is need in sip attr

   if(!INGwSpSipProviderConfig::isSendContact() &&
      !INGwSpSipProviderConfig::isSendContactAccess() &&
      !INGwSpSipProviderConfig::isSendContactParams())
   {
      return;
   }

   if(areaSpecifier != NULL)
   {
      if(INGwSpSipProviderConfig::isSendContactParams())
      {
         for(SipListElement *curr = areaSpecifier->head; curr != SIP_NULL; 
             curr=curr->next)
         {
            if(outData == NULL)
            {
               outData = new INGwSpMsgSipAddrInfo;
            }

            const char *val = (const char *)curr->pData;

            outData->addParam("phone-context", 13, val,
                              strlen(val));
         }
      }
      else if(INGwSpSipProviderConfig::isSendPhoneContext())
      {
         for(SipListElement *curr = areaSpecifier->head; curr != SIP_NULL; 
             curr=curr->next)
         {
            if(outData == NULL)
            {
               outData = new INGwSpMsgSipAddrInfo;
            }

            const char *val = (const char *)curr->pData;

            outData->addParam("phone-context", 13, val,
                              strlen(val));
         }
      }
   }

   for(SipListElement *curr = params->head; curr != SIP_NULL; curr=curr->next)
   {
      SipParam *currParam = (SipParam *)(curr->pData);
      const char *name = currParam->pName;
      const char *val = "";

      if(currParam->slValue.head)
      {
         val = (const char *)currParam->slValue.head->pData;
      }

      if(INGwSpSipProviderConfig::isSendContactParams())
      {
         int nameLen = strlen(name);

         if(currParam->slValue.head == NULL)
         {
            if(outData == NULL)
            {
               outData = new INGwSpMsgSipAddrInfo;
            }

            outData->addParam(name, nameLen, NULL, 0);
         }
         else
         {
            for(SipListElement *iter = currParam->slValue.head; 
                iter != SIP_NULL; iter=iter->next)
            {
               const char *val = (const char *)iter->pData;

               if(outData == NULL)
               {
                  outData = new INGwSpMsgSipAddrInfo;
               }

               outData->addParam(name, nameLen, val, strlen(val));
            }
         }
      }

      switch(*name)
      {
         case 'a':
         case 'A':
         {
            if(strcasecmp("access", name) == 0)
            {
               if(!INGwSpSipProviderConfig::isSendContactParams() &&
                  INGwSpSipProviderConfig::isSendContactAccess())
               {
                  if(outData == NULL)
                  {
                     outData = new INGwSpMsgSipAddrInfo;
                  }

                  outData->addParam(name, 6, val, strlen(val));
               }
            }
         }
         break;

         case 'o':
         case 'O':
         {
            if((!INGwSpSipProviderConfig::isSendContactParams()) &&
               INGwSpSipProviderConfig::isSendOLI() && 
               (strcasecmp(BP_OLI_TAG, name) == 0))
            {
               if(outData == NULL)
               {
                  outData = new INGwSpMsgSipAddrInfo;
               }

               outData->addParam(BP_OLI_TAG, 3, val, strlen(val));
            }
         }
         break;

         case 'p':
         case 'P':
         {
            if((!INGwSpSipProviderConfig::isSendContactParams()) &&
               INGwSpSipProviderConfig::isSendPhoneContext() &&
               (strcasecmp("phone-context", name) == 0))
            {
               if(outData == NULL)
               {
                  outData = new INGwSpMsgSipAddrInfo;
               }

               outData->addParam("phone-context", 13, val, strlen(val));
            }
         }
         break;
      }
   }

   if(telUrl)
   {
      sip_freeTelUrl(telUrl);
   }

   if(outData)
   {
      INGwSpMsgSipSpecificAttr *connSipAttr = conn->getSipSpecificAttr(); 
      connSipAttr->setContactData(outData); 
   }

   return;
}

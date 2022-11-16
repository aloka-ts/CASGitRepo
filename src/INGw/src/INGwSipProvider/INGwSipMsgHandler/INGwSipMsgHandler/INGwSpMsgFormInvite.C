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
#include <INGwSipMsgHandler/INGwSpMsgSipSpecificAttr.h>

#include <INGwSipProvider/INGwSpSipConnection.h>
#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpSipContext.h>
#include <INGwSipProvider/INGwSpSipProviderConfig.h>
#include <INGwSipProvider/INGwSpSipHeaderPolicy.h>

#include <INGwSipProvider/INGwSpSipUtil.h>
#include <INGwSipProvider/INGwSpSipProvider.h>
#include <INGwInfraUtil/INGwIfrUtlStrUtil.h>


#define MAX_TEL_URI_SIZE 500

void INGwSpMsgInviteHandler::_setDispName(const INGwSpSipHeaderTreatment &treatment,
                                      const INGwSpSipHeaderDefault &defVal,
                                      INGwSpSipConnection *peerConn,
                                      INGwSpSipConnection *conn,
                                      RSI_NSP_SIP::HeaderName hdr,
                                      SipHeader *header)
{
   SipError err;

   bool ownerCCM = false;
   const char *dispName = "";
   int len = -1;

   switch(treatment.getDisplayOwner())
   {
      case RSI_NSP_SIP::OWNER_SLEE:
      {
         switch(hdr)
         {
            case RSI_NSP_SIP::TOHDR:
            case RSI_NSP_SIP::CONTACTHDR:
            {
               INGwSpAddress &dialedAddr = 
                                 conn->getAddress(INGwSpSipConnection::DIALED_ADDRESS);
               dispName = dialedAddr.getDisplayName();
            }
            break;

            case RSI_NSP_SIP::FROMHDR:
            {
               INGwSpAddress &origAddr = 
                            conn->getAddress(INGwSpSipConnection::ORIGINATING_ADDRESS);
               dispName = origAddr.getDisplayName();
            }
            break;

            default:
            {
               logger.logMsg(ERROR_FLAG, 0, "Unknown header [%d] for display",
                             hdr);
               return;
            }
         }

         char roleChangeChar = treatment.getDisplayRoleChangeChar();

         if(*dispName == roleChangeChar)
         {
            if(*dispName == '\0')
            {
               ownerCCM = true;
            }
            else if(*(dispName + 1) == '\0')
            {
               ownerCCM = true;
            }
         }
      }
      break;

      case RSI_NSP_SIP::OWNER_CCM:
      {
         ownerCCM = true;
      }
      break;

      default:
      {
         logger.logMsg(ERROR_FLAG, 0, "Unknown display owner [%d]", 
                       treatment.getDisplayOwner());
         return;
      }
   }

   if(ownerCCM)
   {
      switch(treatment.getDisplayTreatment())
      {
         case RSI_NSP_SIP::DISP_ENVIRONMENT:
         {
            dispName = defVal.getDisplay(len);

            if(len == 0)
            {
               dispName = "";
            }
         }
         break;

         case RSI_NSP_SIP::DISP_INCOMING:
         {
            switch(hdr)
            {
               case RSI_NSP_SIP::TOHDR:
               case RSI_NSP_SIP::CONTACTHDR:
               {
                  INGwSpAddress &dialedAddr = 
                             peerConn->getAddress(INGwSpSipConnection::DIALED_ADDRESS);
                  dispName = dialedAddr.getDisplayName();
               }
               break;

               case RSI_NSP_SIP::FROMHDR:
               {
                  INGwSpAddress &origAddr = 
                        peerConn->getAddress(INGwSpSipConnection::ORIGINATING_ADDRESS);
                  dispName = origAddr.getDisplayName();
               }
               break;

               default:
               {
                  logger.logMsg(ERROR_FLAG, 0, "Unknown header [%d] for "
                                               "display", hdr);
                  return;
               }
            }
         }
         break;

         default:
         {
            logger.logMsg(ERROR_FLAG, 0, "Unknown CCM display treatment [%d]",
                          treatment.getDisplayTreatment());
            return;
         }
      }
   }

   if(*dispName == '\0')
   {
      return;
   }

   if(len == -1)
   {
      len = strlen(dispName);
   }

   SIP_S8bit *lname = (char*) fast_memget (0, len + 1, &err);
   strncpy(lname, dispName, len);
   lname[len] = '\0';

   switch(hdr)
   {
      case RSI_NSP_SIP::TOHDR:
      {
         sip_setDispNameInToHdr(header, lname, &err);
      }
      break;

      case RSI_NSP_SIP::FROMHDR:
      {
         sip_setDispNameInFromHdr(header, lname, &err);
      }
      break;

      case RSI_NSP_SIP::CONTACTHDR:
      {
         sip_setDispNameInContactHdr(header, lname, &err);
      }
      break;

      default:
      {
         logger.logMsg(ERROR_FLAG, 0, "Unknown header [%d] for display", hdr);
         return;
      }
   }

   return;
}

bool INGwSpMsgInviteHandler::_isTelParam(const char *name, int nameLen)
{
   if(name == NULL)
   {
      return false;
   }

   switch(*name)
   {
      case 'i':
      case 'I':
      {
         if((nameLen == 4) && (strncasecmp(name, "isub", nameLen) == 0))
         {
            return true;
         }
      }
      break;

      case 'p':
      case 'P':
      {
         if((nameLen == 5) && (strncasecmp(name, "postd", nameLen) == 0))
         {
            return true;
         }
         else if((nameLen == 13) && 
                 (strncasecmp(name, "phone-context", nameLen) == 0))
         {
            return true;
         }
      }
      break;

      case 't':
      case 'T':
      {
         if((nameLen == 3) && (strcasecmp(name, "tsp") == 0))
         {
            return true;
         }
      }
      break;
   }

   return false;
}

SipAddrSpec * INGwSpMsgInviteHandler::_makeAddrSpec(
                                       const INGwSpSipAddressTreatment &treatment,
                                       const INGwSpSipAddressDefault &defVal,
                                       INGwSpSipConnection *peerConn,
                                       INGwSpSipConnection *conn,
                                       RSI_NSP_SIP::HeaderName hdr)
{
   SipError err;

   bool ownerCCM = false;
   bool userPhoneFlag = false;

   RSI_NSP_SIP::ProtocolTreatment outProtocol;
   RSI_NSP_SIP::ProtocolTreatment inProtocol;

   //Lets have inUrl in place.
	 //PANKAJ - to do
   //INGwSpMsgInviteHandler *peerHandler = static_cast<INGwSpMsgInviteHandler *>(
   //                         peerConn->getSipHandler(INGW_SIP_METHOD_TYPE_INVITE));

   INGwSpMsgInviteHandler *peerHandler = NULL;

   SipUrl *peerSipUrl = NULL;
   TelUrl *peerTelUrl = NULL;
   bool peerGlobalNum = false; 

   Sdf_st_callObject *peerObject = peerConn->getHssCallObject();

   switch(hdr)
   {
      case RSI_NSP_SIP::TOHDR:
      {
         if(peerHandler->m_oToTel)
         {
            peerTelUrl = peerHandler->m_oToTel;
            break;
         }

         peerSipUrl = INGwSpSipUtil::getSipUrlFromHdr(peerObject->pCommonInfo->pTo);

         if(peerSipUrl == NULL)
         {
            // Both Sip & Tel url are empty. It might be the case after CCMFT.
            // Lets try to get the Tel Url out from To hdr.

            SipAddrSpec *addrSpec = NULL;
            sip_getAddrSpecFromToHdr(peerObject->pCommonInfo->pTo, &addrSpec, 
                                     &err);
            if(addrSpec)
            {
               sip_freeSipAddrSpec(addrSpec);

               sip_getTelUrlFromAddrSpec(addrSpec, &peerTelUrl, &err);

               if(peerTelUrl)
               {
                  peerHandler->m_oToTel = peerTelUrl;
               }
            }
         }
         else
         {
            sip_freeSipUrl(peerSipUrl);
         }
      }
      break;

      case RSI_NSP_SIP::FROMHDR:
      {
         if(peerHandler->m_oFromTel)
         {
            peerTelUrl = peerHandler->m_oFromTel;
            break;
         }

         peerSipUrl = 
                    INGwSpSipUtil::getSipUrlFromHdr(peerObject->pCommonInfo->pFrom);

         if(peerSipUrl == NULL)
         {
            // Both Sip & Tel url are empty. It might be the case after CCMFT.
            // Lets try to get the Tel Url out from To hdr.

            SipAddrSpec *addrSpec = NULL;
            sip_getAddrSpecFromFromHdr(peerObject->pCommonInfo->pFrom, 
                                       &addrSpec, &err);
            if(addrSpec)
            {
               sip_freeSipAddrSpec(addrSpec);

               sip_getTelUrlFromAddrSpec(addrSpec, &peerTelUrl, &err);

               if(peerTelUrl)
               {
                  peerHandler->m_oToTel = peerTelUrl;
               }
            }
         }
         else
         {
            sip_freeSipUrl(peerSipUrl);
         }
      }
      break;

      case RSI_NSP_SIP::CONTACTHDR:
      {
         //User Peer ToURL.
         if(peerHandler->m_oToTel)
         {
            peerTelUrl = peerHandler->m_oToTel;
            break;
         }

         peerSipUrl = INGwSpSipUtil::getSipUrlFromHdr(peerObject->pCommonInfo->pTo);

         if(peerSipUrl == NULL)
         {
            // Both Sip & Tel url are empty. It might be the case after CCMFT.
            // Lets try to get the Tel Url out from To hdr.

            SipAddrSpec *addrSpec = NULL;
            sip_getAddrSpecFromToHdr(peerObject->pCommonInfo->pTo, &addrSpec, 
                                     &err);
            if(addrSpec)
            {
               sip_freeSipAddrSpec(addrSpec);

               sip_getTelUrlFromAddrSpec(addrSpec, &peerTelUrl, &err);

               if(peerTelUrl)
               {
                  peerHandler->m_oToTel = peerTelUrl;
               }
            }
         }
         else
         {
            sip_freeSipUrl(peerSipUrl);
         }
      }
      break;

      case RSI_NSP_SIP::REQURI:
      {
         if(peerHandler->m_oReqTel)
         {
            peerTelUrl = peerHandler->m_oReqTel;
            break;
         }

         peerSipUrl = peerHandler->m_oInvReqLineUrl;

         //ReqLine we cant get from the current. Which will be different from 
         //what we got from the initial invite. Rem whenever we sent the 
         //request out reqURI changes.

         break;
      }
      break;

      default:
      {
         logger.logMsg(ERROR_FLAG, 0, "Unhandled hdr [%d] for processing.",
                       hdr);
         logger.logMsg(ALWAYS_FLAG, 0, "Quitting deliberatly.");
         exit(0);
      }
   }

   if(peerSipUrl != NULL)
   {
      inProtocol = RSI_NSP_SIP::PROTO_SIP;
   }
   else if(peerTelUrl != NULL)
   {
      inProtocol = RSI_NSP_SIP::PROTO_TEL;

      if((peerTelUrl->pGlobal != NULL) && 
         (peerTelUrl->pGlobal->pBaseNo != NULL))
      {
         peerGlobalNum = true;
      }
      else if((peerTelUrl->pLocal != NULL) &&
              (peerTelUrl->pLocal->pLocalPhoneDigit != NULL))
      {
         peerGlobalNum = false;
      }
      else
      {
         logger.logMsg(ERROR_FLAG, 0, "Unable to determine globalNum staus");
         return NULL;
      }
   }
   else
   {
      if(hdr == RSI_NSP_SIP::REQURI)
      {
         inProtocol = RSI_NSP_SIP::PROTO_SIP;

         //Since we dont have the details of the incoming inv ReqURI, we are 
         //assuming the following so that CCMFT can proceed. 

         logger.logMsg(WARNING_FLAG, 0, "Dont have the details of the "
                                        "incoming inv ReqURI");
      }
      else
      {
         logger.logMsg(ERROR_FLAG, 0, "Unable to get the incoming protocol.");
         return NULL;
      }
   }

   switch(treatment.getProtocolOwner())
   {
      case RSI_NSP_SIP::OWNER_SLEE:
      {
         switch(hdr)
         {
            case RSI_NSP_SIP::TOHDR:
            {
               const INGwSpMsgSipAddrInfo *dialInfo = NULL;

               if(sipAttr && (dialInfo = sipAttr->getDialedData()))
               {
                  if(dialInfo->getProtocol() == INGwSpMsgSipAddrInfo::TEL)
                  {
                     outProtocol = RSI_NSP_SIP::PROTO_TEL;
                  }
                  else
                  {
                     outProtocol = RSI_NSP_SIP::PROTO_SIP;
                  }
               }
               else
               {
                  ownerCCM = true;
               }
            }
            break;

            case RSI_NSP_SIP::CONTACTHDR:
            {
               const INGwSpMsgSipAddrInfo *contactInfo = NULL;

               if(sipAttr && (contactInfo = sipAttr->getContactData()))
               {
                  if(contactInfo->getProtocol() == INGwSpMsgSipAddrInfo::TEL)
                  {
                     outProtocol = RSI_NSP_SIP::PROTO_TEL;
                  }
                  else
                  {
                     outProtocol = RSI_NSP_SIP::PROTO_SIP;
                  }
               }
               else
               {
                  ownerCCM = true;
               }
            }
            break;

            case RSI_NSP_SIP::FROMHDR:
            {
               const INGwSpMsgSipAddrInfo *fromInfo = NULL;

               if(sipAttr && (fromInfo = sipAttr->getOrigData()))
               {
                  if(fromInfo->getProtocol() == INGwSpMsgSipAddrInfo::TEL)
                  {
                     outProtocol = RSI_NSP_SIP::PROTO_TEL;
                  }
                  else
                  {
                     outProtocol = RSI_NSP_SIP::PROTO_SIP;
                  }
               }
               else
               {
                  ownerCCM = true;
               }
            }
            break;

            case RSI_NSP_SIP::REQURI:
            {
               const INGwSpMsgSipAddrInfo *reqInfo = NULL;

               if(sipAttr && (reqInfo = sipAttr->getTargetData()))
               {
                  if(reqInfo->getProtocol() == INGwSpMsgSipAddrInfo::TEL)
                  {
                     outProtocol = RSI_NSP_SIP::PROTO_TEL;
                  }
                  else
                  {
                     outProtocol = RSI_NSP_SIP::PROTO_SIP;
                  }
               }
               else
               {
                  ownerCCM = true;
               }
            }
            break;

            default:
            {
               logger.logMsg(ERROR_FLAG, 0, "Unknown header [%d] for AddrSpec",
                             hdr);
               return NULL;
            }
         }

         //No Role change for protocol.
      }
      break;

      case RSI_NSP_SIP::OWNER_CCM:
      {
         ownerCCM = true;
      }
      break;

      default:
      {
         logger.logMsg(ERROR_FLAG, 0, "Unknown protocol owner [%d]", 
                       treatment.getProtocolOwner());
         return NULL;
      }
   }

   if(ownerCCM)
   {
      switch(treatment.getProtocolTreatment())
      {
         case RSI_NSP_SIP::PROTO_SIP:
         {
            outProtocol = RSI_NSP_SIP::PROTO_SIP;
         }
         break;

         case RSI_NSP_SIP::PROTO_TEL:
         {
            outProtocol = RSI_NSP_SIP::PROTO_TEL;
         }
         break;

         case RSI_NSP_SIP::PROTO_INCOMING:
         {
            outProtocol = inProtocol;
         }
         break;

         default:
         {
            logger.logMsg(ERROR_FLAG, 0, "Unknown CCM display treatment [%d]",
                          treatment.getProtocolTreatment());
            return NULL;
         }
      }
   }

   SipUrl *ourSipUrl = NULL;
   char *ourTelUrl = NULL;
   SipAddrSpec *ret = NULL;

   switch(outProtocol)
   {
      case RSI_NSP_SIP::PROTO_SIP:
      {
         sip_initSipAddrSpec(&ret, SipAddrSipUri, &err);
         sip_initSipUrl(&ret->u.pSipUrl, &err);

         ourSipUrl = ret->u.pSipUrl;
      }
      break;

      case RSI_NSP_SIP::PROTO_TEL:
      {
         sip_initSipAddrSpec(&ret, SipAddrReqUri, &err);
         ret->u.pUri = (SIP_S8bit *) fast_memget(0, MAX_TEL_URI_SIZE, &err);

         ourTelUrl = (char *) ret->u.pUri;

         ourTelUrl = SAS_INGW::ccmStrCpy(ourTelUrl, "tel:");
      }
      break;

      default:
      {
         logger.logMsg(ERROR_FLAG, 0, "Unknown outProtocol [%d]", outProtocol);
         return NULL;
      }
   }

   //ProcessUser.

   ownerCCM = false;

   switch(treatment.getUserPartOwner())
   {
      case RSI_NSP_SIP::OWNER_SLEE_CCM:
      {
         const char *userPart = "";

         switch(hdr)
         {
            case RSI_NSP_SIP::CONTACTHDR:
            {
               const INGwSpMsgSipStringInfo *sleeData = NULL;

               if(sipAttr && (sleeData = sipAttr->getContact()))
               {
                  int len = 0;
                  userPart = sleeData->getStringInfo(len);

                  if(userPart == NULL || len <= 0)
                  {
                     ownerCCM = true;
                  }
               }
               else
               {
                  ownerCCM = true;
               }
            }
            break;

            case RSI_NSP_SIP::TOHDR:
            {
               INGwSpAddress &dialedAddr = 
                                 conn->getAddress(INGwSpSipConnection::DIALED_ADDRESS);
               userPart = dialedAddr.getAddress();
            }
            break;

            case RSI_NSP_SIP::FROMHDR:
            {
               INGwSpAddress &origAddr = 
                            conn->getAddress(INGwSpSipConnection::ORIGINATING_ADDRESS);
               userPart = origAddr.getAddress();
            }
            break;

            case RSI_NSP_SIP::REQURI:
            {
               INGwSpAddress &targetAddr = 
                                 conn->getAddress(INGwSpSipConnection::TARGET_ADDRESS);
               userPart = targetAddr.getAddress();
            }
            break;
         }

         if(ownerCCM)
         {
            break;
         }

         if(userPart == NULL) userPart = "";

         if((*userPart == '\0') || 
            (*userPart == treatment.getUserPartRoleChangeChar() &&
             *(userPart + 1) == '\0'))
         {
            ownerCCM = true;
            break;
         }

         switch(outProtocol)
         {
            case RSI_NSP_SIP::PROTO_SIP:
            {
               ourSipUrl->pUser = (SIP_S8bit *) fast_memget(0, MAX_TEL_URI_SIZE,
                                                            &err);
               strcpy(ourSipUrl->pUser, userPart);
            }
            break;

            case RSI_NSP_SIP::PROTO_TEL:
            {
               ourTelUrl = SAS_INGW::ccmStrCpy(ourTelUrl, userPart);
            }
         }
      }
      break;

      case RSI_NSP_SIP::OWNER_SLEE:
      {
         const char *userPart = "";

         switch(hdr)
         {
            case RSI_NSP_SIP::CONTACTHDR:
            {
               const INGwSpMsgSipStringInfo *sleeData = NULL;

               if(sipAttr && (sleeData = sipAttr->getContact()))
               {
                  int len = 0;
                  userPart = sleeData->getStringInfo(len);
               }
               
               if(userPart == NULL || *userPart == '\0')
               {
                  INGwSpAddress &dialedAddr = 
                                 conn->getAddress(INGwSpSipConnection::DIALED_ADDRESS);
                  userPart = dialedAddr.getAddress();
               }
            }
            break;

            case RSI_NSP_SIP::TOHDR:
            {
               INGwSpAddress &dialedAddr = 
                                 conn->getAddress(INGwSpSipConnection::DIALED_ADDRESS);
               userPart = dialedAddr.getAddress();
            }
            break;

            case RSI_NSP_SIP::FROMHDR:
            {
               INGwSpAddress &origAddr = 
                            conn->getAddress(INGwSpSipConnection::ORIGINATING_ADDRESS);
               userPart = origAddr.getAddress();
            }
            break;

            case RSI_NSP_SIP::REQURI:
            {
               INGwSpAddress &targetAddr = 
                                 conn->getAddress(INGwSpSipConnection::TARGET_ADDRESS);
               userPart = targetAddr.getAddress();
            }
            break;
         }

         if(userPart == NULL) userPart = "";

         if((*userPart == '\0') || 
            (*userPart == treatment.getUserPartRoleChangeChar() &&
             *(userPart + 1) == '\0'))
         {
            ownerCCM = true;
            break;
         }

         switch(outProtocol)
         {
            case RSI_NSP_SIP::PROTO_SIP:
            {
               ourSipUrl->pUser = (SIP_S8bit *) fast_memget(0, MAX_TEL_URI_SIZE,
                                                            &err);
               strcpy(ourSipUrl->pUser, userPart);
            }
            break;
            case RSI_NSP_SIP::PROTO_TEL:
            {
               ourTelUrl = SAS_INGW::ccmStrCpy(ourTelUrl, userPart);
            }
         }
      }
      break;

      case RSI_NSP_SIP::OWNER_CCM:
      {
         ownerCCM = true;
      }
      break;

      default:
      {
         logger.logMsg(ERROR_FLAG, 0, "Unknown display owner [%d]", 
                       treatment.getUserPartOwner());
         sip_freeSipAddrSpec(ret);
         return NULL;
      }
   }

   if(ownerCCM)
   {
      switch(treatment.getUserPartTreatment())
      {
         case RSI_NSP_SIP::USER_ENVIRONMENT:
         {
            int len = 0;
            const char *userPart = defVal.getUser(len);

            if(userPart == NULL)
            {
               userPart = "";
               len = 0;
            }

            switch(outProtocol)
            {
               case RSI_NSP_SIP::PROTO_SIP:
               {
                  ourSipUrl->pUser = 
                           (SIP_S8bit *) fast_memget(0, MAX_TEL_URI_SIZE, &err);
                  strncpy(ourSipUrl->pUser, userPart, len);
                  ourSipUrl->pUser[len] = '\0';
               }
               break;
               case RSI_NSP_SIP::PROTO_TEL:
               {
                  ourTelUrl = SAS_INGW::ccmStrnCpy(ourTelUrl, userPart, len);
               }
            }
         }
         break;

         case RSI_NSP_SIP::USER_INCOMING:
         {
            switch(inProtocol)
            {
               case RSI_NSP_SIP::PROTO_SIP:
               {
                  const char *userPart = "";

                  if(peerSipUrl)
                  {
                     userPart = peerSipUrl->pUser;
                  }
                  else
                  {
                     //Peer URL can be empty only in case of ReqURI
                     INGwSpAddress &targetAddr = 
                                 conn->getAddress(INGwSpSipConnection::TARGET_ADDRESS);
                     userPart = targetAddr.getAddress();
                  }

                  if(userPart == NULL)
                  {
                     userPart = "";
                  }

                  switch(outProtocol)
                  {
                     case RSI_NSP_SIP::PROTO_SIP:
                     {
                        if ( userPart != NULL && *userPart != '\0')
                        {
                            ourSipUrl->pUser = (SIP_S8bit *) 
                                             fast_memget(0, MAX_TEL_URI_SIZE, &err);
                            strcpy(ourSipUrl->pUser, userPart);
                        }
                     }
                     break;

                     case RSI_NSP_SIP::PROTO_TEL:
                     {
                        ourTelUrl = SAS_INGW::ccmStrCpy(ourTelUrl, userPart);
                     }
                     break;
                  }
               }
               break;

               case RSI_NSP_SIP::PROTO_TEL:
               {
                  // Incoming is Tel. no need to check for peer uri validity 
                  // even in case of reqURI on CCMFT. As only TEL protocol will 
                  // be set only on valid tel uri presence.

                  switch(outProtocol)
                  {
                     case RSI_NSP_SIP::PROTO_SIP:
                     {
                        //User part should be teluserpart + allparams.
                        ourSipUrl->pUser = 
                           (SIP_S8bit *) fast_memget(0, MAX_TEL_URI_SIZE, &err);

                        char *tmpData = ourSipUrl->pUser;

                        SipList *params = NULL;
                        SipList *areaSpecifier = NULL;

                        if(peerGlobalNum)
                        {
                           *tmpData = '+';
                           tmpData++;
                           *tmpData = '\0';

                           TelGlobalNum *glbNum = peerTelUrl->pGlobal;

                           params = &(glbNum->slParams);
                           areaSpecifier = &(glbNum->slAreaSpecifier);

                           tmpData = SAS_INGW::ccmStrCpy(tmpData, 
                                                            glbNum->pBaseNo);
                           if(glbNum->pIsdnSubAddr)
                           {
                              tmpData = SAS_INGW::ccmStrCpy(tmpData, 
                                                               ";isub=");
                              tmpData = SAS_INGW::ccmStrCpy(tmpData, 
                                                          glbNum->pIsdnSubAddr);
                           }

                           if(glbNum->pPostDial)
                           {
                              tmpData = SAS_INGW::ccmStrCpy(tmpData, 
                                                               ";postd=");
                              tmpData = SAS_INGW::ccmStrCpy(tmpData, 
                                                             glbNum->pPostDial);
                           }
                        }
                        else
                        {
                           TelLocalNum *locNum = peerTelUrl->pLocal;

                           params = &(locNum->slParams);
                           areaSpecifier = &(locNum->slAreaSpecifier);

                           tmpData = SAS_INGW::ccmStrCpy(tmpData, 
                                                      locNum->pLocalPhoneDigit);
                           if(locNum->pIsdnSubAddr)
                           {
                              tmpData = SAS_INGW::ccmStrCpy(tmpData, 
                                                               ";isub=");
                              tmpData = SAS_INGW::ccmStrCpy(tmpData, 
                                                          locNum->pIsdnSubAddr);
                           }

                           if(locNum->pPostDial)
                           {
                              tmpData = SAS_INGW::ccmStrCpy(tmpData, 
                                                               ";postd=");
                              tmpData = SAS_INGW::ccmStrCpy(tmpData, 
                                                             locNum->pPostDial);
                           }
                        }

                        for(SipListElement *curr = areaSpecifier->head; 
                            curr != SIP_NULL; curr=curr->next)
                        {
                           tmpData = SAS_INGW::ccmStrCpy(tmpData,
                                                            ";phone-context=");
                           tmpData = SAS_INGW::ccmStrCpy(tmpData,
                                                     (const char *)curr->pData);
                        }

                        for(SipListElement *curr = params->head; 
                            curr != SIP_NULL; curr=curr->next)
                        {
                           SipParam *currParam = (SipParam *)(curr->pData);

                           for(SipListElement *iter = currParam->slValue.head; 
                               iter != SIP_NULL; iter=iter->next)
                           {
                              *tmpData = ';'; tmpData++; *tmpData = '\0';
                              tmpData = SAS_INGW::ccmStrCpy(tmpData,
                                                              currParam->pName);
                              *tmpData = '='; tmpData++; *tmpData = '\0';
                              tmpData = SAS_INGW::ccmStrCpy(tmpData,
                                                     (const char *)iter->pData);
                           }
                        }

                        //We have to add User=Phone.

                        if(!userPhoneFlag)
                        {
                           INGwSpSipProvider &prov = INGwSpSipProvider::getInstance();
                           INGwSpThreadSpecificSipData &tsd = 
                                                prov.getThreadSpecificSipData();

                           sip_insertUrlParamAtIndexInUrl(ourSipUrl, 
                                                          tsd.userPhoneParam, 0,
                                                          &err);
                           userPhoneFlag = true;
                        }
                     }
                     break;

                     case RSI_NSP_SIP::PROTO_TEL:
                     {
                        if(peerGlobalNum)
                        {
                           *ourTelUrl = '+';
                           ourTelUrl++;
                           *ourTelUrl = '\0';

                           TelGlobalNum *glbNum = peerTelUrl->pGlobal;

                           ourTelUrl = SAS_INGW::ccmStrCpy(ourTelUrl, 
                                                              glbNum->pBaseNo);
                        }
                        else
                        {
                           TelLocalNum *locNum = peerTelUrl->pLocal;
                           ourTelUrl = SAS_INGW::ccmStrCpy(ourTelUrl, 
                                                      locNum->pLocalPhoneDigit);
                        }
                     }
                     break;
                  }
               }
               break;
            }
         }
         break;

         default:
         {
            logger.logMsg(ERROR_FLAG, 0, "Unknown CCM userPart treatment [%d]",
                          treatment.getUserPartTreatment());
            sip_freeSipAddrSpec(ret);
            return NULL;
         }
      }
   }

   //Process host, port.
   ownerCCM = false;

   switch(treatment.getHostPartOwner())
   {
      case RSI_NSP_SIP::OWNER_SLEE:
      {
         //Currently we are not getting host/port information slee.
         //So let the ccm handle by itself.
         ownerCCM = true;
      }
      break;

      case RSI_NSP_SIP::OWNER_CCM:
      {
         ownerCCM=true;
      }
      break;

      default:
      {
         logger.logMsg(ERROR_FLAG, 0, "Unknown Hostpart owner [%d]",
                       treatment.getHostPartOwner());
         sip_freeSipAddrSpec(ret);
         return NULL;
      }
   }

   if(ownerCCM)
   {
      const char *ip = "";
      int port = 5060;

      switch(treatment.getHostPartTreatment())
      {
         case RSI_NSP_SIP::HOST_GW:
         {
            //This is only appropriate for the ReqURI. For rest we will use 
            //defaults.
            switch(hdr)
            {
               case RSI_NSP_SIP::TOHDR:
               {
                  ip = conn->getGwIPAddress();
                  port = conn->getGwPort();
                  logger.logMsg(TRACE_FLAG, 0, "RSI_NSP_SIP::TOHDR Ip <%s> Port <%d>", ip, port);
               }
               break;

               case RSI_NSP_SIP::FROMHDR:
               {
                  ip = INGwSpSipProviderConfig::getSelfIp();
                  port = INGwSpSipProviderConfig::getSelfPort();
               }
               break;

               case RSI_NSP_SIP::CONTACTHDR:
               {
                  ip = INGwSpSipProviderConfig::getSelfIp();
                  port = INGwSpSipProviderConfig::getSelfPort();
               }
               break;

               case RSI_NSP_SIP::REQURI:
               {
									//PANKAJ - to do
                  //_getOutboundGw(port, (char **)&ip,
                  //                        mInviteStateContext.mOutboundGwIndex);
               }
               break;
            }
         }
         break;

         case RSI_NSP_SIP::HOST_LOOPBACK:
         {
            ip = peerConn->getGwIPAddress();
            port = peerConn->getGwPort();
      
            logger.logMsg(TRACE_FLAG, 0, "Port <%d> in LOOPBACK Mode", port);
         }
         break;

         case RSI_NSP_SIP::HOST_INCOMING:
         {
            switch(inProtocol)
            {
               case RSI_NSP_SIP::PROTO_SIP:
               {
                  if(peerSipUrl)
                  {
                     ip = peerSipUrl->pHost;

                     if(peerSipUrl->dPort)
                     {
                        port = *(peerSipUrl->dPort);
                     }
                     break;
                  }
                  //Peer uri is empty so let assign default by proceeding 
                  //further. note no break after case.
               }

               case RSI_NSP_SIP::PROTO_TEL:
               {
                  //Incoming is Tel that wont have hostpart. So based on hdr
                  //We will provide defaults.

                  switch(hdr)
                  {
                     case RSI_NSP_SIP::TOHDR:
                     {
                        ip = conn->getGwIPAddress();
                        port = conn->getGwPort();
                     }
                     break;

                     case RSI_NSP_SIP::FROMHDR:
                     {
                        ip = INGwSpSipProviderConfig::getSelfIp();
                        port = INGwSpSipProviderConfig::getSelfPort();
                     }
                     break;

                     case RSI_NSP_SIP::CONTACTHDR:
                     {
                        ip = INGwSpSipProviderConfig::getSelfIp();
                        port = INGwSpSipProviderConfig::getSelfPort();
                     }
                     break;

                     case RSI_NSP_SIP::REQURI:
                     {
                        logger.logMsg(ERROR_FLAG, 0, 
                                      "ReqURI is set as incoming. want to "
                                      "receive the outgoing message :-(");
									      //PANKAJ - to do
                        // _getOutboundGw(port, (char **)&ip, 
                        //                   mInviteStateContext.mOutboundGwIndex);
                     }
                     break;
                  }
               }
               break;
            }
         }
         break;

         case RSI_NSP_SIP::HOST_SELF:
         {
            ip = INGwSpSipProviderConfig::getSelfIp();
            port = INGwSpSipProviderConfig::getSelfPort();
         }
         break;

         default:
         {
            logger.logMsg(ERROR_FLAG, 0, "Unknown HostPart treatment [%d]",
                          treatment.getHostPartTreatment());
            sip_freeSipAddrSpec(ret);
            return NULL;
         }
      }

      if(ip == NULL)
      {
         ip = "";
      }

      switch(outProtocol)
      {
         case RSI_NSP_SIP::PROTO_SIP:
         {
            int hostLen = strlen(ip);
            ourSipUrl->pHost = (SIP_S8bit *) fast_memget(0, hostLen + 1, &err);
            strcpy(ourSipUrl->pHost, ip);

            ourSipUrl->dPort = (SIP_U16bit *)fast_memget(0, sizeof(SIP_U16bit), 
                                                         &err);
            *(ourSipUrl->dPort) = port;
         }
         break;

         case RSI_NSP_SIP::PROTO_TEL:
         {
            //Nothing to do.
         }
         break;
      }

      logger.logMsg(TRACE_FLAG, 0, "Ip <%s> Port <%d>", ip, port);

      if(hdr == RSI_NSP_SIP::REQURI)
      {
         conn->setGwIPAddress(ip);
         conn->setGwPort(port);
      }
   }

   //Param
   ownerCCM = false;
   char *userEnd = NULL;

   if(ourSipUrl)
   {
      userEnd = ourSipUrl->pUser;

      if(userEnd)
         userEnd += strlen(userEnd);
   }

   switch(treatment.getParamOwner())
   {
      case RSI_NSP_SIP::OWNER_SLEE:
      {
         const char *paramData = NULL;
         int paramLen = 0;

         switch(hdr)
         {
            case RSI_NSP_SIP::TOHDR:
            {
               const INGwSpMsgSipAddrInfo *sleeData = NULL;

               if(sipAttr && (sleeData = sipAttr->getDialedData()))
               {
                  paramData = sleeData->getSerializeData(paramLen);
               }
            }
            break;

            case RSI_NSP_SIP::CONTACTHDR:
            {
               const INGwSpMsgSipAddrInfo *sleeData = NULL;

               if(sipAttr && (sleeData = sipAttr->getContactData()))
               {
                  paramData = sleeData->getSerializeData(paramLen);
               }
            }
            break;

            case RSI_NSP_SIP::FROMHDR:
            {
               const INGwSpMsgSipAddrInfo *sleeData = NULL;

               if(sipAttr && (sleeData = sipAttr->getOrigData()))
               {
                  paramData = sleeData->getSerializeData(paramLen);
               }
            }
            break;

            case RSI_NSP_SIP::REQURI:
            {
               const INGwSpMsgSipAddrInfo *sleeData = NULL;

               if(sipAttr && (sleeData = sipAttr->getTargetData()))
               {
                  paramData = sleeData->getSerializeData(paramLen);
               }
            }
            break;
         }

         if(paramData)
         {
            int baseParamCount = 0;
            if(outProtocol == RSI_NSP_SIP::PROTO_SIP)
            {
               baseParamCount = ourSipUrl->slParam.size;
            }

            const char *end = paramData + paramLen;
            const char *start = paramData;

            while(start < end)
            {
               int tag;
               memcpy(&tag, start, sizeof(int));
               start += sizeof(int);

               int length;
               memcpy(&length, start, sizeof(int));
               start += sizeof(int);

               INGwSpMsgSipAddrInfo::AddrTag currTag = 
                                            (INGwSpMsgSipAddrInfo::AddrTag) ntohl(tag);
               length = ntohl(length);

               switch(currTag)
               {
                  case INGwSpMsgSipAddrInfo::UNKNOWN:
                  {
                     const char *name = start + length;

                     int nameLen;
                     memcpy(&nameLen, name, sizeof(int));
                     nameLen = ntohl(nameLen);
                     name += sizeof(int);

                     switch(outProtocol)
                     {
                        case RSI_NSP_SIP::PROTO_SIP:
                        {
                           if(!_isTelParam(name, nameLen))
                           {
                              //CreateParam and Add.
                              SipParam *tempParam;

                              sip_initSipParam(&tempParam, &err);

                              tempParam->pName = 
                                 (SIP_S8bit *)fast_memget(0, nameLen + 1, &err);
                              strncpy(tempParam->pName, name, nameLen);
                              tempParam->pName[nameLen] = '\0';

                              SIP_S8bit *value = 
                                  (SIP_S8bit *)fast_memget(0, length + 1, &err);
                              strncpy(value, start, length);
                              value[length] = '\0';

                              sip_listInsertAt(&(tempParam->slValue), 0, value, 
                                               &err);

                              sip_insertUrlParamAtIndexInUrl(ourSipUrl, 
                                                             tempParam,
                                                             baseParamCount, 
                                                             &err);
                              baseParamCount++;

                              sip_freeSipParam(tempParam);
                           }
                           else
                           {
                              *userEnd = ';'; userEnd++; *userEnd = '\0';
                              userEnd = SAS_INGW::ccmStrnCpy(userEnd, name, 
                                                                nameLen);
                              *userEnd = '='; userEnd++; *userEnd = '\0';
                              userEnd = SAS_INGW::ccmStrnCpy(userEnd, start, 
                                                                length);
                              if(!userPhoneFlag)
                              {
                                 INGwSpSipProvider &prov = 
                                                      INGwSpSipProvider::getInstance();
                                 INGwSpThreadSpecificSipData &tsd = 
                                                prov.getThreadSpecificSipData();

                                 sip_insertUrlParamAtIndexInUrl(ourSipUrl, 
                                                             tsd.userPhoneParam,
                                                                baseParamCount,
                                                                &err);
                                 baseParamCount++;
                                 userPhoneFlag = true;
                              }
                           }
                        }
                        break;

                        case RSI_NSP_SIP::PROTO_TEL:
                        {
                           *ourTelUrl = ';'; ourTelUrl++; *ourTelUrl = '\0';
                           ourTelUrl = SAS_INGW::ccmStrnCpy(ourTelUrl, name, 
                                                               nameLen);
                           *ourTelUrl = '='; ourTelUrl++; *ourTelUrl = '\0';
                           ourTelUrl = SAS_INGW::ccmStrnCpy(ourTelUrl,
                                                               start, length);
                        }
                        break;
                     }

                     start += nameLen;
                     start += sizeof(int);
                  }
               }

               start += length;
            }
         }
      }
      break;

      case RSI_NSP_SIP::OWNER_CCM:
      {
         ownerCCM = true;
      }
      break;

      default:
      {
         logger.logMsg(ERROR_FLAG, 0, "Unknown param owner [%d]",
                       treatment.getParamOwner());
         sip_freeSipAddrSpec(ret);
         return NULL;
      }
   }

   if(ownerCCM)
   {
      char *telParamStart = NULL;

      telParamStart = ourTelUrl;

      switch(treatment.getParamTreatment())
      {
         case RSI_NSP_SIP::PARAM_INCOMING:
         {
            if(peerSipUrl == NULL && peerTelUrl == NULL)
            {
               break;
            }

            switch(inProtocol)
            {
               case RSI_NSP_SIP::PROTO_SIP:
               {
                  switch(outProtocol)
                  {
                     case RSI_NSP_SIP::PROTO_SIP:
                     {
                        int baseParamCount = ourSipUrl->slParam.size;

                        for(SipListElement *curr = peerSipUrl->slParam.head; 
                            curr != SIP_NULL; curr=curr->next)
                        {
                           SipParam *currParam = (SipParam *)(curr->pData);

                           sip_insertUrlParamAtIndexInUrl(ourSipUrl, currParam,
                                                          baseParamCount, 
                                                          &err);
                           baseParamCount++;
                        }
                     }
                     break;

                     case RSI_NSP_SIP::PROTO_TEL:
                     {
                        //SIP URI Params we cant copy into TEL URI params.
                        //Just ignore them.
                     }
                     break;
                  }
               }
               break;

               case RSI_NSP_SIP::PROTO_TEL:
               {
                  switch(outProtocol)
                  {
                     case RSI_NSP_SIP::PROTO_SIP:
                     {
                        //Nothing to do. All the Tel params have to be added
                        //in the user part. That based on the user part policy 
                        //will be taken care.
                     }
                     break;

                     case RSI_NSP_SIP::PROTO_TEL:
                     {
                        //Get all from tel.
                        SipList *params = NULL;
                        SipList *areaSpecifier = NULL;

                        if(peerGlobalNum)
                        {
                           TelGlobalNum *glbNum = peerTelUrl->pGlobal;

                           params = &(glbNum->slParams);
                           areaSpecifier = &(glbNum->slAreaSpecifier);

                           if(glbNum->pIsdnSubAddr)
                           {
                              ourTelUrl = SAS_INGW::ccmStrCpy(ourTelUrl, 
                                                               ";isub=");
                              ourTelUrl = SAS_INGW::ccmStrCpy(ourTelUrl, 
                                                          glbNum->pIsdnSubAddr);
                           }

                           if(glbNum->pPostDial)
                           {
                              ourTelUrl = SAS_INGW::ccmStrCpy(ourTelUrl, 
                                                               ";postd=");
                              ourTelUrl = SAS_INGW::ccmStrCpy(ourTelUrl, 
                                                             glbNum->pPostDial);
                           }
                        }
                        else
                        {
                           TelLocalNum *locNum = peerTelUrl->pLocal;

                           params = &(locNum->slParams);
                           areaSpecifier = &(locNum->slAreaSpecifier);

                           if(locNum->pIsdnSubAddr)
                           {
                              ourTelUrl = SAS_INGW::ccmStrCpy(ourTelUrl, 
                                                               ";isub=");
                              ourTelUrl = SAS_INGW::ccmStrCpy(ourTelUrl, 
                                                          locNum->pIsdnSubAddr);
                           }

                           if(locNum->pPostDial)
                           {
                              ourTelUrl = SAS_INGW::ccmStrCpy(ourTelUrl, 
                                                               ";postd=");
                              ourTelUrl = SAS_INGW::ccmStrCpy(ourTelUrl, 
                                                             locNum->pPostDial);
                           }
                        }

                        for(SipListElement *curr = areaSpecifier->head; 
                            curr != SIP_NULL; curr=curr->next)
                        {
                           ourTelUrl = SAS_INGW::ccmStrCpy(ourTelUrl,
                                                            ";phone-context=");
                           ourTelUrl = SAS_INGW::ccmStrCpy(ourTelUrl,
                                                     (const char *)curr->pData);
                        }

                        for(SipListElement *curr = params->head; 
                            curr != SIP_NULL; curr=curr->next)
                        {
                           SipParam *currParam = (SipParam *)(curr->pData);

                           for(SipListElement *iter = currParam->slValue.head; 
                               iter != SIP_NULL; iter=iter->next)
                           {
                              *ourTelUrl = ';'; ourTelUrl++; *ourTelUrl = '\0';
                              ourTelUrl = SAS_INGW::ccmStrCpy(ourTelUrl,
                                                              currParam->pName);
                              *ourTelUrl = '='; ourTelUrl++; *ourTelUrl = '\0';
                              ourTelUrl = SAS_INGW::ccmStrCpy(ourTelUrl,
                                                     (const char *)iter->pData);
                           }
                        }
                     }
                     break;
                  }
               }
            }
         }
         break;

         case RSI_NSP_SIP::PARAM_ENVIRONMENT:
         {
            const SipParam * const * params = NULL;
            int paramCount = 0;

            params = defVal.getParam(paramCount);

            int baseParamCount = 0;
            if(outProtocol == RSI_NSP_SIP::PROTO_SIP)
            {
               baseParamCount = ourSipUrl->slParam.size;
            }

            for(int idx = 0; idx < paramCount; idx++)
            {
               SipParam *currParam = (SipParam *)params[idx];

               switch(outProtocol)
               {
                  case RSI_NSP_SIP::PROTO_SIP:
                  {
                     if(!_isTelParam(currParam->pName, 
                                     strlen(currParam->pName)))
                     {
                        sip_insertUrlParamAtIndexInUrl(ourSipUrl, currParam,
                                                       baseParamCount + idx, 
                                                       &err);
                     }
                     else
                     {
                        for(SipListElement *iter = currParam->slValue.head; 
                            iter != SIP_NULL; iter=iter->next)
                        {
                           *userEnd = ';'; userEnd++; *userEnd = '\0';
                           userEnd = SAS_INGW::ccmStrCpy(userEnd, 
                                                            currParam->pName);
                           *userEnd = '='; userEnd++; *userEnd = '\0';
                           userEnd = SAS_INGW::ccmStrCpy(userEnd, 
                                                     (const char *)iter->pData);
                                                            
                           if(!userPhoneFlag)
                           {
                              INGwSpSipProvider &prov = 
                                                   INGwSpSipProvider::getInstance();
                              INGwSpThreadSpecificSipData &tsd = 
                                                prov.getThreadSpecificSipData();

                              sip_insertUrlParamAtIndexInUrl(ourSipUrl, 
                                                          tsd.userPhoneParam,
                                                           baseParamCount + idx,
                                                             &err);
                              userPhoneFlag = true;
                           }
                           else
                           {
                              baseParamCount--;
                           }
                        }
                     }
                  }
                  break;

                  case RSI_NSP_SIP::PROTO_TEL:
                  {
                     for(SipListElement *iter = currParam->slValue.head; 
                         iter != SIP_NULL; iter=iter->next)
                     {
                        *ourTelUrl = ';'; ourTelUrl++; *ourTelUrl = '\0';
                        ourTelUrl = SAS_INGW::ccmStrCpy(ourTelUrl,
                                                           currParam->pName);
                        *ourTelUrl = '='; ourTelUrl++; *ourTelUrl = '\0';
                        ourTelUrl = SAS_INGW::ccmStrCpy(ourTelUrl,
                                                     (const char *)iter->pData);
                     }
                  }
                  break;
               }
            }
         }
         break;

         default:
         {
            logger.logMsg(ERROR_FLAG, 0, "Unknown param treatment [%d]",
                          treatment.getParamOwner());
            sip_freeSipAddrSpec(ret);
            return NULL;
         }
      }

      switch(treatment.getParamSleeInputTreatment())
      {
         case RSI_NSP_SIP::PARAM_SLEE_INPUT_APPEND:
         {
            const char *paramData = NULL;
            int paramLen = 0;

            switch(hdr)
            {
               case RSI_NSP_SIP::TOHDR:
               {
                  const INGwSpMsgSipAddrInfo *sleeData = NULL;

                  if(sipAttr && (sleeData = sipAttr->getDialedData()))
                  {
                     paramData = sleeData->getSerializeData(paramLen);
                  }
               }
               break;

               case RSI_NSP_SIP::CONTACTHDR:
               {
                  const INGwSpMsgSipAddrInfo *sleeData = NULL;

                  if(sipAttr && (sleeData = sipAttr->getContactData()))
                  {
                     paramData = sleeData->getSerializeData(paramLen);
                  }
               }
               break;

               case RSI_NSP_SIP::FROMHDR:
               {
                  const INGwSpMsgSipAddrInfo *sleeData = NULL;

                  if(sipAttr && (sleeData = sipAttr->getOrigData()))
                  {
                     paramData = sleeData->getSerializeData(paramLen);
                  }
               }
               break;

               case RSI_NSP_SIP::REQURI:
               {
                  const INGwSpMsgSipAddrInfo *sleeData = NULL;
   
                  if(sipAttr && (sleeData = sipAttr->getTargetData()))
                  {
                     paramData = sleeData->getSerializeData(paramLen);
                  }
               }
               break;
            }

            if(paramData)
            {
               int baseParamCount = 0;
               if(outProtocol == RSI_NSP_SIP::PROTO_SIP)
               {
                  baseParamCount = ourSipUrl->slParam.size;
               }

               const char *end = paramData + paramLen;
               const char *start = paramData;

               while(start < end)
               {
                  int tag;
                  memcpy(&tag, start, sizeof(int));
                  start += sizeof(int);
   
                  int length;
                  memcpy(&length, start, sizeof(int));
                  start += sizeof(int);
   
                  INGwSpMsgSipAddrInfo::AddrTag currTag = 
                                            (INGwSpMsgSipAddrInfo::AddrTag) ntohl(tag);
                  length = ntohl(length);
   
                  switch(currTag)
                  {
                     case INGwSpMsgSipAddrInfo::UNKNOWN:
                     {
                        const char *name = start + length;
   
                        int nameLen;
                        memcpy(&nameLen, name, sizeof(int));
                        nameLen = ntohl(nameLen);
                        name += sizeof(int);

                        switch(outProtocol)
                        {
                           case RSI_NSP_SIP::PROTO_SIP:
                           {
                              if(!_isTelParam(name, nameLen))
                              {
                                 //CreateParam and Add.
                                 SipParam *tempParam;

                                 sip_initSipParam(&tempParam, &err);

                                 tempParam->pName = 
                                 (SIP_S8bit *)fast_memget(0, nameLen + 1, &err);
                                 strncpy(tempParam->pName, name, nameLen);
                                 tempParam->pName[nameLen] = '\0';

                                 SIP_S8bit *value = 
                                  (SIP_S8bit *)fast_memget(0, length + 1, &err);
                                 strncpy(value, start, length);
                                 value[length] = '\0';

                                 sip_listInsertAt(&(tempParam->slValue), 0, 
                                                  value, &err);

                                 sip_insertUrlParamAtIndexInUrl(ourSipUrl, 
                                                                tempParam,
                                                                baseParamCount, 
                                                                &err);
                                 baseParamCount++;

                                 sip_freeSipParam(tempParam);
                              }
                              else
                              {
                                 *userEnd = ';'; userEnd++; *userEnd = '\0';
                                 userEnd = SAS_INGW::ccmStrnCpy(userEnd, 
                                                                   name, 
                                                                   nameLen);
                                 *userEnd = '='; userEnd++; *userEnd = '\0';
                                 userEnd = SAS_INGW::ccmStrnCpy(userEnd, 
                                                                   start, 
                                                                   length);
                                 if(!userPhoneFlag)
                                 {
                                    INGwSpSipProvider &prov = 
                                                      INGwSpSipProvider::getInstance();
                                    INGwSpThreadSpecificSipData &tsd = 
                                                prov.getThreadSpecificSipData();

                                    sip_insertUrlParamAtIndexInUrl(ourSipUrl, 
                                                             tsd.userPhoneParam,
                                                                 baseParamCount,
                                                                   &err);
                                    baseParamCount++;
                                    userPhoneFlag = true;
                                 }
                              }
                           }
                           break;

                           case RSI_NSP_SIP::PROTO_TEL:
                           {
                              *ourTelUrl = ';'; ourTelUrl++; *ourTelUrl = '\0';
                              ourTelUrl = SAS_INGW::ccmStrnCpy(ourTelUrl, 
                                                                  name, 
                                                                  nameLen);
                              *ourTelUrl = '='; ourTelUrl++; *ourTelUrl = '\0';
                              ourTelUrl = SAS_INGW::ccmStrnCpy(ourTelUrl,
                                                                 start, length);
                           }
                           break;
                        }

                        start += nameLen;
                        start += sizeof(int);
                     }
                  }

                  start += length;
               }
            }
         }
         break;

         case RSI_NSP_SIP::PARAM_SLEE_INPUT_REPLACE:
         {
            const char *paramData = NULL;
            int paramLen = 0;

            switch(hdr)
            {
               case RSI_NSP_SIP::TOHDR:
               {
                  const INGwSpMsgSipAddrInfo *sleeData = NULL;

                  if(sipAttr && (sleeData = sipAttr->getDialedData()))
                  {
                     paramData = sleeData->getSerializeData(paramLen);
                  }
               }
               break;

               case RSI_NSP_SIP::CONTACTHDR:
               {
                  const INGwSpMsgSipAddrInfo *sleeData = NULL;

                  if(sipAttr && (sleeData = sipAttr->getContactData()))
                  {
                     paramData = sleeData->getSerializeData(paramLen);
                  }
               }
               break;

               case RSI_NSP_SIP::FROMHDR:
               {
                  const INGwSpMsgSipAddrInfo *sleeData = NULL;

                  if(sipAttr && (sleeData = sipAttr->getOrigData()))
                  {
                     paramData = sleeData->getSerializeData(paramLen);
                  }
               }
               break;

               case RSI_NSP_SIP::REQURI:
               {
                  const INGwSpMsgSipAddrInfo *sleeData = NULL;
   
                  if(sipAttr && (sleeData = sipAttr->getTargetData()))
                  {
                     paramData = sleeData->getSerializeData(paramLen);
                  }
               }
               break;
            }

            if(paramData)
            {
               int baseParamCount = 0;
               if(outProtocol == RSI_NSP_SIP::PROTO_SIP)
               {
                  baseParamCount = ourSipUrl->slParam.size;
               }

               const char *end = paramData + paramLen;
               const char *start = paramData;

               while(start < end)
               {
                  int tag;
                  memcpy(&tag, start, sizeof(int));
                  start += sizeof(int);
   
                  int length;
                  memcpy(&length, start, sizeof(int));
                  start += sizeof(int);
   
                  INGwSpMsgSipAddrInfo::AddrTag currTag = 
                                            (INGwSpMsgSipAddrInfo::AddrTag) ntohl(tag);
                  length = ntohl(length);
   
                  switch(currTag)
                  {
                     case INGwSpMsgSipAddrInfo::UNKNOWN:
                     {
                        const char *name = start + length;
   
                        int nameLen;
                        memcpy(&nameLen, name, sizeof(int));
                        nameLen = ntohl(nameLen);
                        name += sizeof(int);

                        switch(outProtocol)
                        {
                           case RSI_NSP_SIP::PROTO_SIP:
                           {
                              if(!_isTelParam(name, nameLen))
                              {
                                 //CreateParam and Add.
                                 SipParam *tempParam;

                                 sip_initSipParam(&tempParam, &err);

                                 tempParam->pName = 
                                 (SIP_S8bit *)fast_memget(0, nameLen + 1, &err);
                                 strncpy(tempParam->pName, name, nameLen);
                                 tempParam->pName[nameLen] = '\0';

                                 SIP_S8bit *value = 
                                  (SIP_S8bit *)fast_memget(0, length + 1, &err);
                                 strncpy(value, start, length);
                                 value[length] = '\0';

                                 sip_listInsertAt(&(tempParam->slValue), 0, 
                                                  value, &err);

                                 bool replaced = false;
                                 int idx = 0;
                                 for(SipListElement *it = 
                                                        ourSipUrl->slParam.head;
                                     it != SIP_NULL; it=it->next, idx++)
                                 {
                                    SipParam *myP = (SipParam *)(it->pData);
   
                                    if(strcasecmp(myP->pName, tempParam->pName)
                                       == 0)
                                    {
                                       sip_setUrlParamAtIndexInUrl(ourSipUrl, 
                                                                   tempParam, 
                                                                   idx, 
                                                                   &err);
                                       replaced = true;
                                    }
                                 }

                                 if(!replaced)
                                 {
                                    sip_insertUrlParamAtIndexInUrl(ourSipUrl, 
                                                                   tempParam,
                                                                 baseParamCount,
                                                                   &err);
                                    baseParamCount++;
                                 }

                                 sip_freeSipParam(tempParam);
                              }
                              else
                              {
                                 userEnd = _replaceParam(ourSipUrl->pUser,
                                                         userEnd, name, nameLen,
                                                         start, length);
                              }
                           }
                           break;

                           case RSI_NSP_SIP::PROTO_TEL:
                           {
                              ourTelUrl = _replaceParam(telParamStart, 
                                                        ourTelUrl, name, 
                                                        nameLen, start, length);
                           }
                           break;
                        }

                        start += nameLen;
                        start += sizeof(int);
                     }
                  }

                  start += length;
               }
            }
         }
         break;

         case RSI_NSP_SIP::PARAM_SLEE_INPUT_IGNORE:
         {
         }
         break;

         default:
         {
            logger.logMsg(ERROR_FLAG, 0, "Unknown slee param treatment [%d]",
                          treatment.getParamSleeInputTreatment());
            sip_freeSipAddrSpec(ret);
            return NULL;
         }
      }
   }

   return ret;
}

char * INGwSpMsgInviteHandler::_replaceParam(char *start, char *end, 
                                         const char *name, int nameLen, 
                                         const char *value, int valLen)
{
   bool replacedFlag = false;
   char *ourTelUrl = end;

   for(;((end = SAS_INGW::ccmStrnCaseStr(start, (ourTelUrl - start), name, 
                                            nameLen)) != NULL); 
       start = end)
   {
      end += nameLen;

      while(*end == ' ') end++;

      if(*end != '=')
      {
         continue;
      }

      end++;
      while(*end == ' ') end++;

      char *valStart = end;

      while(*end != ';' && *end != '\0') end++;

      char *valEnd = end;

      memmove(valStart + valLen, valEnd, (ourTelUrl - valEnd));
      memcpy(valStart, value, valLen);

      ourTelUrl += (valLen - (ourTelUrl - valEnd));

      end = valStart + valLen;
      replacedFlag = true;
   }

   if(!replacedFlag)
   {
      *ourTelUrl = ';'; ourTelUrl++; *ourTelUrl = '\0';
      ourTelUrl = SAS_INGW::ccmStrnCpy(ourTelUrl, name, nameLen);
      *ourTelUrl = '='; ourTelUrl++; *ourTelUrl = '\0';
      ourTelUrl = SAS_INGW::ccmStrnCpy(ourTelUrl, value, valLen);
   }

   return ourTelUrl;
}

void INGwSpMsgInviteHandler::_setHeaderParam(const INGwSpSipHeaderTreatment &treatment,
                                         const INGwSpSipHeaderDefault &defVal,
                                         INGwSpSipConnection *peerConn,
                                         INGwSpSipConnection *conn,
                                         RSI_NSP_SIP::HeaderName hdr,
                                         SipHeader *header)
{
   SipError err;
   SipList *inParams = NULL;
   SipList *outParams = NULL;

   switch(hdr)
   {
      case RSI_NSP_SIP::TOHDR:
      {
         outParams = &((SipToHeader *)(header->pHeader))->slParam;

         Sdf_st_callObject *peerObj = peerConn->getHssCallObject();

         if(peerObj)
         {
            inParams = 
                &((SipToHeader *)(peerObj->pCommonInfo->pTo->pHeader))->slParam;
         }
      }
      break;

      case RSI_NSP_SIP::FROMHDR:
      {
         outParams = &((SipFromHeader *)(header->pHeader))->slParam;

         Sdf_st_callObject *peerObj = peerConn->getHssCallObject();

         if(peerObj)
         {
            inParams = 
            &((SipFromHeader *)(peerObj->pCommonInfo->pFrom->pHeader))->slParam;
         }
      }
      break;

      default:
      {
         logger.logMsg(ERROR_FLAG, 0, "Unexpected hdr [%d] received.", hdr);
         return;
      }
   }

   bool ownerCCM = false;

   switch(treatment.getHeaderParamOwner())
   {
      case RSI_NSP_SIP::OWNER_SLEE:
      {
         //Currently we are not receiving any params from SLEE.
         ownerCCM = true;
      }
      break;

      case RSI_NSP_SIP::OWNER_CCM:
      {
         ownerCCM = true;
      }
      break;

      default:
      {
         logger.logMsg(ERROR_FLAG, 0, "Unknown header param owner [%d]",
                       treatment.getHeaderParamOwner());
         return;
      }
   }

   if(ownerCCM)
   {
      switch(treatment.getHeaderParamTreatment())
      {
         case RSI_NSP_SIP::PARAM_INCOMING:
         {
            if(inParams == NULL)
            {
               return;
            }

            int count = 0;

            for(SipListElement *curr = inParams->head; curr != SIP_NULL; 
                curr=curr->next, count++)
            {
               SipParam *currParam = (SipParam *)(curr->pData);

               HSS_LOCKEDINCREF(currParam->dRefCount);
               sip_listInsertAt(outParams, count, currParam, &err);
            }
         }
         break;

         case RSI_NSP_SIP::PARAM_ENVIRONMENT:
         {
            int paramCount = 0;
            const SipParam * const * params = defVal.getParam(paramCount);

            for(int idx = 0; idx < paramCount; idx++)
            {
               SipParam *currParam = (SipParam *)params[idx];

               HSS_LOCKEDINCREF(currParam->dRefCount);
               sip_listInsertAt(outParams, idx, currParam, &err);
            }
         }
         break;
      }

      switch(treatment.getParamSleeInputTreatment())
      {
         case RSI_NSP_SIP::PARAM_SLEE_INPUT_APPEND:
         case RSI_NSP_SIP::PARAM_SLEE_INPUT_REPLACE:
         case RSI_NSP_SIP::PARAM_SLEE_INPUT_IGNORE:
         {
            //Since we are not getting anything from Slee just ignoring.
         }
      }
   }

   return;
}

SipHeader * INGwSpMsgInviteHandler::_makeToHeader(INGwSpSipConnection *peerConn, 
                                              INGwSpSipConnection *conn)
{
   const INGwSpSipHeaderPolicy &policy = INGwSpSipProviderConfig::getSipHeaderPolicy();
   const INGwSpSipHeaderTreatment &treatment = policy.getToHeaderTreatment();

   INGwSpThreadSpecificSipData &tsd = 
                           INGwSpSipProvider::getInstance().getThreadSpecificSipData();
   const INGwSpSipHeaderDefault &defVal = tsd.getHdrDefault().getToHeaderDefault();

   SipError err;
   SipHeader *toHdr = NULL;

   sip_initSipHeader(&toHdr, SipHdrTypeTo, &err);

   _setDispName(treatment, defVal, peerConn, conn, RSI_NSP_SIP::TOHDR, toHdr);

   SipAddrSpec *addrSpec = _makeAddrSpec(treatment.getAddressTreatment(),
                                         defVal.getAddress(),
                                         peerConn,
                                         conn,
                                         RSI_NSP_SIP::TOHDR);
   sip_setAddrSpecInToHdr(toHdr, addrSpec, &err);
   logger.logINGwMsg(false, TRACE_FLAG, 0, "sip_setAddrSpecInToHdr Ip <%s> Port <%d>",
               ((SipToHeader *)(toHdr->pHeader))->pAddrSpec->u.pSipUrl->pHost, 
               *((SipToHeader *)(toHdr->pHeader))->pAddrSpec->u.pSipUrl->dPort);
   sip_freeSipAddrSpec(addrSpec);

   _setHeaderParam(treatment, defVal, peerConn, conn, RSI_NSP_SIP::TOHDR, 
                   toHdr);

   return toHdr;
}

SipHeader * INGwSpMsgInviteHandler::_makeFromHeader(INGwSpSipConnection *peerConn,
                                                INGwSpSipConnection *conn)
{
   const INGwSpSipHeaderPolicy &policy = INGwSpSipProviderConfig::getSipHeaderPolicy();
   const INGwSpSipHeaderTreatment &treatment = policy.getFromHeaderTreatment();

   INGwSpThreadSpecificSipData &tsd = 
                           INGwSpSipProvider::getInstance().getThreadSpecificSipData();
   const INGwSpSipHeaderDefault &defVal = 
                                     tsd.getHdrDefault().getFromHeaderDefault();

   SipError err;
   SipHeader *fromHdr = NULL;

   sip_initSipHeader(&fromHdr, SipHdrTypeFrom, &err);

   _setDispName(treatment, defVal, peerConn, conn, RSI_NSP_SIP::FROMHDR, 
                fromHdr);

   SipAddrSpec *addrSpec = _makeAddrSpec(treatment.getAddressTreatment(),
                                         defVal.getAddress(),
                                         peerConn,
                                         conn,
                                         RSI_NSP_SIP::FROMHDR);
   sip_setAddrSpecInFromHdr(fromHdr, addrSpec, &err);
   sip_freeSipAddrSpec(addrSpec);

   _setHeaderParam(treatment, defVal, peerConn, conn, RSI_NSP_SIP::FROMHDR, 
                   fromHdr);
   
   return fromHdr;
}

SipHeader * INGwSpMsgInviteHandler::_makeContactHeader(INGwSpSipConnection *peerConn,
                                                   INGwSpSipConnection *conn)
{
   const INGwSpSipHeaderPolicy &policy = INGwSpSipProviderConfig::getSipHeaderPolicy();
   const INGwSpSipHeaderTreatment &treatment = policy.getContactHeaderTreatment();

   INGwSpThreadSpecificSipData &tsd = 
                           INGwSpSipProvider::getInstance().getThreadSpecificSipData();
   const INGwSpSipHeaderDefault &defVal = 
                                  tsd.getHdrDefault().getContactHeaderDefault();

   SipError err;
   SipHeader *contactHdr = NULL;

   sip_initSipHeader(&contactHdr, SipHdrTypeContactNormal, &err);

   _setDispName(treatment, defVal, peerConn, conn, RSI_NSP_SIP::CONTACTHDR, 
                contactHdr);

   SipAddrSpec *addrSpec = _makeAddrSpec(treatment.getAddressTreatment(),
                                         defVal.getAddress(),
                                         peerConn,
                                         conn,
                                         RSI_NSP_SIP::CONTACTHDR);

   sip_setAddrSpecInContactHdr(contactHdr, addrSpec, &err);
   sip_freeSipAddrSpec(addrSpec);

   return contactHdr;
}

SipAddrSpec * INGwSpMsgInviteHandler::_makeReqURI(INGwSpSipConnection *peerConn, 
                                              INGwSpSipConnection *conn)
{
   const INGwSpSipHeaderPolicy &policy = INGwSpSipProviderConfig::getSipHeaderPolicy();
   const INGwSpSipAddressTreatment &treatment = policy.getReqUriTreatment();

   INGwSpThreadSpecificSipData &tsd = 
                           INGwSpSipProvider::getInstance().getThreadSpecificSipData();
   const INGwSpSipAddressDefault &defVal = tsd.getHdrDefault().getReqUriDefault();

   SipError err;

   SipAddrSpec *addrSpec = _makeAddrSpec(treatment, defVal, peerConn, conn,
                                         RSI_NSP_SIP::REQURI);

   return addrSpec;
}

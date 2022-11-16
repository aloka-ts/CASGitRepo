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
//     File:     INGwSpSipUtil.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipProvider");

#include <INGwSipProvider/INGwSpSipUtil.h>
#include <INGwSipProvider/INGwSpSipProviderConfig.h>
#include <INGwSipProvider/INGwSpSipProvider.h>
#include <INGwSipProvider/INGwSpSipContext.h>
#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpData.h>
#include <INGwSipProvider/INGwSpAddress.h>
#include <INGwSipProvider/INGwSpStackConfigMgr.h>

#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>
#include <Util/imErrorCodes.h>
#include <INGwInfraUtil/INGwIfrUtlStrStr.h>
#include <INGwInfraUtil/INGwIfrUtlStrUtil.h>

#include <INGwInfraUtil/INGwIfrUtlAlgorithm.h>
#include <INGwSipMsgHandler/INGwSpMsgSipSpecificAttr.h>


#include <set>
#include <string>
#include <vector>
#include <algorithm>
#include <iterator>

using namespace std;

const char uri_att_fix[] = "uri_att_fix";

extern const char *DEFAULT_ADDRESS;

std::string INGwSpSipUtil::ourEndPoint;
INGwIfrUtlStrStr INGwSpSipUtil::_sdpCStrStr("c=", true);
INGwIfrUtlStrStr INGwSpSipUtil::_sdpAppSDPStrStr("application/sdp", false);
INGwIfrUtlStrStr INGwSpSipUtil::_isupAppISUPStrStr("application/isup", false); //BPUsa07888
INGwIfrUtlStrStr INGwSpSipUtil::_gtdAppGTDStrStr("application/gtd", false); //BPInd17865
int              INGwSpSipUtil::miSipHdrOpt=SIP_OPT_SHORTFORM;

#define SIP_PRIVACY_HDR_NAME           "Privacy"
#define SIP_PAI_HDR_NAME               "P-Asserted-Identity"
#define SIP_PPI_HDR_NAME               "P-Preferred-Identity"

#define SIP_PRIVACY_NONE_TAG           "none"
#define SIP_PRIVACY_USER_TAG           "user"
#define SIP_PRIVACY_HDR_TAG            "header"
#define SIP_PRIVACY_ID_TAG             "id"

enum SipPrivacyParams {
  sipParamDName     = 1, 
  sipParamUser      = 2, 
  sipParamBGT       = 4, 
  sipParamBGID      = 8, 
  sipParamCPC       = 16, 
  sipParamOLI       = 32,
  sipParamISUPOLI   = 64, 
  sipParamLRN       = 128, 
  sipParamCIC       = 256, 
  sipParamNPDI      = 512
};


using namespace std;

extern "C"
{
  void wrapper_sip_freeSipMsgBody(void* aMsgBody)
  {
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "wrapper_sip_freeSipMsgBody: BEFORE FREE <%u>", ((SipMsgBody*)aMsgBody)->dRefCount.ref);
    sip_freeSipMsgBody((SipMsgBody*)aMsgBody);
  }

  void
  wrapper_sip_freeSipHdr(void* sipHdr) {
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "wrapper_sip_freeSipHdr");
    sip_freeSipHeader((SipHeader*)sipHdr);
  }
}

////////////////////////////////////////////////////////////////////////////////
// Method: createIpDataFromSipMessage
// Description: This method creates INGwSpData from a transaction.  Depending on
//              the need, it can copy either the message body, the message
//              itself, or both.
//              With the message body, the body type and length are also
//              extracted from the transaction and stored in the sip data.
// IN  - aTransaction: The transaction containing the message body and the
//                     SIP message.
// IN  - aType       : The SIP Message type.  This is needed to clone the SIP
//                     message, as the stack uses different structures for
//                     request messages and response messages.
// OUT - aSipData    : The output sip data
// IN  - aAttrib     : This indicates whether the message body, the SIP message,
//                     or both need to be placed in the SipData.
////////////////////////////////////////////////////////////////////////////////
Sdf_ty_retVal INGwSpSipUtil::createIpDataFromSipMessage
(SipMessage         *aSipMsg,
 Sdf_ty_slist       *aMsgBodyList,
 en_SipMessageType   aType,
 INGwSpData            *aSipData,
 Sdf_st_error       *aErr,
 int                 aAttrib,
 INGwSpSipConnection	*aSipConnection)
{
	logger.logMsg(TRACE_FLAG, 0, "IN createIpDataFromSipMessage. Attrib[%d]", 
			aAttrib);

	SipError    siperror;
	SipMessage *clonedSipMsg = NULL;

	// If we need to copy the SIP message.  This may be required if some headers
	// need to be replicated on the peer side.
	if(aAttrib & ATTRIB_SIPMESG)
	{
		sip_initSipMessage (&clonedSipMsg, aType  , &siperror);
		sip_cloneSipMessage(clonedSipMsg , aSipMsg, &siperror);
		aSipData->setSipMessage(clonedSipMsg);
	}

	// Check if the message body is NOT needed.
	if( !(aAttrib & ATTRIB_MSGBODY) )
	{
		LogINGwTrace(false, 0, "OUT createIpDataFromSipMessage");
		return Sdf_co_success;
	} // end of if(attrib...

	// If the SDP parsing is on, then we need to retrieve the SDP from the
	// sip message and compose a raw SDP body from it.  Otherwise, we just need
	// to retrieve the first message body in the message and set it in the
	// sip data.
	Sdf_ty_retVal sdpPresent = sdf_ivk_uaIsSdpBodyPresent(aSipMsg, aErr);
	if(sdpPresent == Sdf_co_success)
	{
		logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
				"INGwSpSipUtil::createIpDataFromSipMessage: SDP PRESENT");
		SipMsgBody     *sipmsgbody = NULL;
		Sdf_st_msgBody *sdfmsgbody = NULL;

		Sdf_ty_retVal sdpRetrieved = sdf_listGetAt(aMsgBodyList, 0,
				(void **)&sdfmsgbody, aErr);
		sipmsgbody = sdfmsgbody->pMsgBody;
		if(sdpRetrieved == Sdf_co_success)
		{
			SipError sipErr;
			char *sdpbuf    = new char[MAX_SDP_LEN];
			// This thing is required bcoz the sdpbuf pointer is changed (!!!) by
			// the form sdp function.
			SIP_S8bit *endbuf = &(sdpbuf[MAX_SDP_LEN - 1]);
			char *tmpsdpbuf = sdpbuf;
			sdpbuf[0] = 0;
			SipBool rawSdpRetrieved = sip_formSdpBody(endbuf,
					sipmsgbody->u.pSdpMessage,
					&sdpbuf, &sipErr);
			sdpbuf = tmpsdpbuf;

			if(rawSdpRetrieved)
			{
				if(INGwSpSipProviderConfig::isSDPFixEnabled() )
				{
					char* fixedSDP = NULL;
					unsigned int fixedSDPLength = 0;

					if( ! (fixSDPBody(sdpbuf, strlen(sdpbuf), &fixedSDP, &fixedSDPLength) ) )
					{
						// ERR
						logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "createIpDataFromSipMessage: Unable to fix the SDP: [%s]:[%d]", 
							sdpbuf, strlen(sdpbuf) );
					} else
					{
						aSipData->setBody((const char *)fixedSDP, fixedSDPLength);
						aSipData->setBodyLength(fixedSDPLength);
						aSipData->setBodyType("application/sdp");
						delete[] fixedSDP;
					}
				}else
				{
					int sdpLength = strlen(sdpbuf);
					aSipData->setBody((const char *)sdpbuf, sdpLength);
					aSipData->setBodyLength(sdpLength);
					aSipData->setBodyType("application/sdp");
				}

			}
			else {
				logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "INGwSpSipUtil::createIpDataFromSipMessage: Unable to set SDP coz rawSdpRetrieved is false");
			}
			delete [] sdpbuf;
		} // end of if(sdpretrieved...
	} // end of if(isSdpParsed...
	else
	{
		// SDP is not present or the SDP parsing is disabled.  Extract the
		// message body as unknown message.
		logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
				"createIpDataFromSipMessage: SDP from UNKNOWN MESSAGE");

		// Extract the body type from the content-type header.
		if(!aSipMsg->pGeneralHdr->pContentTypeHdr)
		{
			logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE,  
					"INGwSpSipUtil::createIpDataFromSipMessage: content-type header absent in sip message.");
			LogINGwTrace(false, 0, "OUT createIpDataFromSipMessage");
			return Sdf_co_success;
		}

		// Content Type Header is present..
		char *mediatype = NULL;
		SipHeader hdr;
		hdr.dType = SipHdrTypeContentType;
		hdr.pHeader = aSipMsg->pGeneralHdr->pContentTypeHdr;
		SipBool bodyTypeRetrieved = sip_getMediaTypeFromContentTypeHdr
			(&hdr, &mediatype, &siperror);

		if( (!mediatype) || (Sdf_co_success != bodyTypeRetrieved) )
		{
			logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, " createIpDataFromSipMessage: Error getting the media type from content header.");
			LogINGwTrace(false, 0, "OUT createIpDataFromSipMessage");
			return Sdf_co_fail;
		}

		logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, " INGwSpSipUtil::createIpDataFromSipMessage: Found media type <%s>", mediatype);

		bool bParseMultipartContent = false;
		// Check if SIPT Parsing is Enabled
		if(INGwSpSipProviderConfig::isSIPTParsingEnabled() )
		{
			// If yes then Check if the mediaType is "multipart/mixed". 
			std::string strCT(mediatype);
			int match = strCT.find(strMULTIPART_MIXED);
			if( (match >= 0 ) && (aSipConnection != NULL) ) 
			{
				// If yes then..set the flag bParseMultipartContent
				bParseMultipartContent = true;
			}
		}

		// Now we are looking for content-type header parameters
		SIP_U32bit paramCount = 0;
		SipBool paramCountRetrieved = sip_getParamCountFromContentTypeHdr(
				&hdr, &paramCount, &siperror);
		if (paramCountRetrieved != SipSuccess) {
			logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
					"Error getting the param count from content type header");
			LogINGwTrace(false, 0, "OUT createIpDataFromSipMessage");
			return Sdf_co_fail;
		}

		logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "Found [%d] params for content type [%s]", paramCount, mediatype);

		// There must be at least the "boundary" param present if the content type is "mutipart"
		if( bParseMultipartContent && (paramCount == 0) )
		{
			logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
					"\"boundary\" param not found in content type header");
			LogINGwTrace(false, 0, "OUT createIpDataFromSipMessage");
			return Sdf_co_fail;
		}

		// "boundary" param value
		std::string multipartBoundary;

		char *mediaTypeValue = mediatype;

		char mediatypeWithAttr[1024];
		strcpy(mediatypeWithAttr, mediatype);

		if(0 < paramCount) {

			SipBool paramRetrieved      = SipSuccess;
			SipBool paramNameRetrieved  = SipSuccess;
			SipBool paramValueRetrieved = SipSuccess;
			SipParam* pParam = NULL;
			SIP_S8bit* pName = NULL;
			SIP_S8bit* pVal  = NULL;

			for(SIP_U32bit index = 0; index < paramCount; index++) {
				paramRetrieved = sip_getParamAtIndexFromContentTypeHdr(
						&hdr, &pParam, index, &siperror);
				if (paramRetrieved != SipSuccess) {
					logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "Error getting the param at index [%d] from content type header", index);
					LogINGwTrace(false, 0, "OUT createIpDataFromSipMessage");
					return Sdf_co_fail;
				}

				paramNameRetrieved = sip_getNameFromSipParam(pParam, &pName, &siperror);
				if (paramNameRetrieved != SipSuccess) {
					logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "Error getting the param name at index [%d] from content type header", index);
					sip_freeSipParam(pParam);
					LogINGwTrace(false, 0, "OUT createIpDataFromSipMessage");
					return Sdf_co_fail;
				}

				paramValueRetrieved = sip_getValueAtIndexFromSipParam(pParam, &pVal, 0, &siperror);
				if (paramValueRetrieved != SipSuccess) {
					logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "Error getting the param value at index [%d] from content type header", index);
					sip_freeSipParam(pParam);
					LogINGwTrace(false, 0, "OUT createIpDataFromSipMessage");
					return Sdf_co_fail;
				}

				// Check if this is the "boundary" param
				if (bParseMultipartContent)
				{
					//	if paramNameRetrieved was "boundary" then store the paramValueRetrieved in multipartBoundary
					if(0 == strcmp(pName, strBOUNDARY) )
						multipartBoundary = pVal;
				}

				logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "Parameter at index [%d] is [%s=%s]", index, pName, pVal);
				strcat(mediatypeWithAttr, ";");
				strcat(mediatypeWithAttr, pName);
				strcat(mediatypeWithAttr, "=");
				strcat(mediatypeWithAttr, pVal);

				sip_freeSipParam(pParam);
			}// for

			// check if we could receive any "boundary" param
			if( bParseMultipartContent)
			{
				if (0 == multipartBoundary.size() ) 
				{
					logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
							"\"boundary\" param invalid in content type header");
					LogINGwTrace(false, 0, "OUT createIpDataFromSipMessage");
					return Sdf_co_fail;
				} else
				{
					logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
							"\"boundary\" param is: %s size:%d", multipartBoundary.c_str(), multipartBoundary.size());
				}
			}

			mediaTypeValue = mediatypeWithAttr;
		}// (paramCount > 0)

		// Extract the body:
		char         *msgBodyBuf       = NULL;
		unsigned int  msgBodyLen       = 0;
		Sdf_ty_retVal msgBodyRetrieved = sdf_ivk_uaGetUnknownBodyFromSipMessage
			(aSipMsg, 0, &msgBodyBuf, &msgBodyLen, aErr);
		if(msgBodyRetrieved != Sdf_co_success)
		{
			logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, "Unable to retrieve message body");
			LogINGwTrace(false, 0, "OUT createIpDataFromSipMessage");
			return Sdf_co_fail;
		}

		logger.logINGwMsg(false, TRACE_FLAG, 0, "Unknown Body [%s][%d] ", msgBodyBuf, msgBodyLen);

		if( ! bParseMultipartContent )
		{
			// Copy the Media Type
			logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "Setting the body type as [%s]", mediaTypeValue);
			aSipData->setBodyType(mediaTypeValue);

			if(INGwSpSipProviderConfig::isSDPFixEnabled() )
			{
				char* fixedSDP = NULL;
				unsigned int fixedSDPLength = 0;

				if( ! (fixSDPBody(msgBodyBuf, msgBodyLen, &fixedSDP, &fixedSDPLength) ) )
				{
					// ERR
					logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "createIpDataFromSipMessage: Unable to fix the SDP: [%s]:[%d]", 
						msgBodyBuf, msgBodyLen );
				} else
				{
					logger.logINGwMsg(false, TRACE_FLAG, 0, "Setting the SDP[%s][%d] from unknownHDR", 
							fixedSDP, fixedSDPLength);
					aSipData->setBody((const char *)fixedSDP, fixedSDPLength);
					aSipData->setBodyLength(fixedSDPLength);
					delete[] fixedSDP;
				}
			}else
			{
				// Copy the SDP Body now
				logger.logINGwMsg(false, TRACE_FLAG, 0, "Setting the SDP[%s][%d] from unknownHDR", 
						msgBodyBuf, msgBodyLen);
				aSipData->setBody((const char *)msgBodyBuf, msgBodyLen);
				aSipData->setBodyLength(msgBodyLen);
			}

			LogINGwTrace(false, 0, "OUT createIpDataFromSipMessage");
			return Sdf_co_success;
		}

		// We have to parse the unknown body for SDP and ISUP bodies
		LogINGwTrace(false, 0, "Multipart content");
		std::string strFullBody(msgBodyBuf);

		// search the location for "application/sdp" in the body 
		// Now search the location of "--<boundary>" after above location
		//	The middle part is SDP
		int locAppSDP = 0;
        //As per RFC2045 mime types are case insensitive.
        const char * resSDP = NULL;
       	if((resSDP = _sdpAppSDPStrStr.findPatternIn(strFullBody.c_str(), strFullBody.size())) != NULL)
		{
			string strSdpPartBody(resSDP);
			// Need to ignore the "application/SDP" string and any '\r' or '\n' following it.
			locAppSDP += strlen( strAPPLICATION_SDP);

		 	locAppSDP = strSdpPartBody.find("\r\n\r\n",locAppSDP)+4;

			int locNextBoundary = strSdpPartBody.find(multipartBoundary, locAppSDP );
			if( locNextBoundary < 0)
			{
				logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, "Decode failure in message body");
				LogINGwTrace(false, 0, "OUT createIpDataFromSipMessage");
				return Sdf_co_fail;
			}

            if((locNextBoundary-strSdpPartBody.rfind("\r\n\r\n",locNextBoundary))==6)
			    locNextBoundary -= 4; // Adjust for crlf && "--" preceding the actual boundary as per rfc2046
            else
				locNextBoundary -= 2;// Adjust only for preceding "--", though wrong as per RFC but doing here to work 
                                     //Novolink gateways which are sending only one crlf delimiting boundary and sdp.

			logger.logINGwMsg(false, TRACE_FLAG, 0, "SDP start [ %d ], end [ %d ]", locAppSDP, locNextBoundary);

			std::string strSDP = strSdpPartBody.substr(locAppSDP, (locNextBoundary - locAppSDP) );

			if(INGwSpSipProviderConfig::isSDPFixEnabled() )
			{
				char* fixedSDP = NULL;
				unsigned int fixedSDPLength = 0;

				if( ! (fixSDPBody(strSDP.c_str(), strSDP.size(), &fixedSDP, &fixedSDPLength) ) )
				{
					// ERR
					logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "createIpDataFromSipMessage: Unable to fix the SDP: [%s]:[%d]", 
						strSDP.c_str(),  strSDP.size() );
				} else
				{
					logger.logINGwMsg(false, TRACE_FLAG, 0, "Setting the SDP[%s][%d] from unknownHDR", 
							fixedSDP, fixedSDPLength);
					aSipData->setBody((const char *)fixedSDP, fixedSDPLength);
					aSipData->setBodyLength(fixedSDPLength);
					delete[] fixedSDP;
				}
			}else
			{
				logger.logINGwMsg(false, TRACE_FLAG, 0, "Setting the SDP[%s][%d] from unknownHDR", 
						strSDP.c_str(), strSDP.size());
				aSipData->setBody( strSDP.c_str(), strSDP.size() );
				aSipData->setBodyLength( strSDP.size() );
			}

			// Copy the Media Type
			logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "Setting the body type as [%s]", 
					strAPPLICATION_SDP );
			aSipData->setBodyType( strAPPLICATION_SDP );
		}
		else
		{
			LogINGwTrace(false, 0, "No SDP body found.");
		}

        
	//BPUsa07888,BPInd17865: we are looking only into the INVITE messages fro ISUP and GTD bodies
		if((aType==SipMessageRequest)&&(INGW_SIP_METHOD_TYPE_INVITE==getMethodFromSipMessage(aSipMsg)))
		{
			const char * res=NULL;
			// Now search for "application/isup" in the body from the begin
            //Here I am assuming that we are only receiving IAM messages in the INVITE
        	if((res = _isupAppISUPStrStr.findPatternIn(msgBodyBuf, msgBodyLen)) != NULL)
			{
				// Need to ignore the "application/ISUP" string  and any '\r' or '\n' following it
                int start_loc=res-msgBodyBuf+1;
                const char *isup_part=NULL;
                isup_part=strstr(res,"\r\n\r\n")+4;
                int tmp_buff_len=msgBodyLen-(isup_part-msgBodyBuf);
                char *ech=(char *)memchr(isup_part,'-',tmp_buff_len);
                while(ech!=NULL)
                {
					if(0==memcmp("\r\n--",ech-2,4))
						break;
                    tmp_buff_len=msgBodyLen-(ech-msgBodyBuf);
                    if(tmp_buff_len<0)
                        tmp_buff_len=0;
					ech=(char *)memchr(ech+1,'-',tmp_buff_len);
				}
                int isup_part_len=0;
				if(ech!=NULL)
					isup_part_len=ech-isup_part;
				else{
					logger.logINGwMsg(false, TRACE_FLAG, 0, "Message corrupted No Isup boundary found");
					logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, "Decode failure in message body");
					LogINGwTrace(false, 0, "OUT createIpDataFromSipMessage");
					return Sdf_co_fail;
				}
                   
                
				char *isup_buff=new char[isup_part_len];
				memcpy(isup_buff,isup_part,isup_part_len);
				//	The middle part is ISUP/SIPT body.
				//As per RFC3204 ISUP Payload is binary and ansi version.
				logger.logINGwMsg(false, TRACE_FLAG, 0, "ISUP start [ %d ], end [ %d ]", isup_part-msgBodyBuf+1, ech-msgBodyBuf-1);

				/*Bytes to skip to reach beginning of optional attributes
 				Message Type(1)+Nature of Connection Indicator(1)+FCI(2)+CPC(1)+Transmission 
				Medium Requirement(1) +pointer to Called Party Number(1)
				*/
				int start_byte=7;
			 	int offset=(int)isup_buff[7];
				if(offset==0)
				{
					LogINGwTrace(false, 0, "IAM Message Optional Param Part is absent");
				}
				else
				{
					start_byte+=offset;
					while(start_byte<isup_part_len) //two CRLF before the next boundary location are also skipped
					{
						//extract param tag
                        char param_tag[1];
                        memcpy(param_tag,isup_buff+start_byte,1);
						int ret2=memcmp(param_tag,"\x00",1);
						if(ret2==0){
							LogINGwTrace(false, 0, "OLI not Found");
							break;
						}
			     		int ret1=memcmp(param_tag,"\xea",1);
                        start_byte+=1;
                      //extract param length
                        int param_len=(int)isup_buff[start_byte];
                        start_byte+=1;
                        if(ret1==0){
				          LogINGwTrace(false, 0, "OLI Tag Found");
                   //extract param value
                          char *oli_buff=new char[param_len];
                          memcpy(oli_buff,isup_buff+start_byte,param_len);
                          int oliValue=(int)oli_buff[0];
                          if( oliValue > -1)
                          {
					          logger.logINGwMsg(false, TRACE_FLAG, 0, "OLI found in ISUP body: %d", oliValue);
                              //aSipConnection->setOLI(oliValue);
                          }
                          delete oli_buff;
                          break;
                        }
                        start_byte+=param_len;
					}
	
				}
                delete isup_buff;
            }
			else
			{
				LogINGwTrace(false, 0, "No ISUP body found.");
			}
			//BPInd17865: Extraction of OLI from GTD body 
			// Now search for "application/gtd" in the body from the begin
			int locAppGTD=0;
        	if((res = _gtdAppGTDStrStr.findPatternIn(strFullBody.c_str(), strFullBody.size())) != NULL)
			{
				// Need to ignore the "application/GTD" string and any '\r' or '\n' following it
				string strGtdPartBody(res);
				locAppGTD += strlen(strAPPLICATION_GTD);
		 		locAppGTD = strGtdPartBody.find("\r\n\r\n",locAppGTD)+4;
	
				// Now search the location of "--<boundary>" after above location
				int locNextBoundary = strGtdPartBody.find(multipartBoundary, locAppGTD );
				if( locNextBoundary < 0)
				{
					logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, "Decode failure in message body");
					LogINGwTrace(false, 0, "OUT createIpDataFromSipMessage");
					return Sdf_co_fail;
				}
	
				locNextBoundary -= 2; // Adjust for "--" preceding the actual boundary
	
				logger.logINGwMsg(false, TRACE_FLAG, 0, "GTD start [ %d ], end [ %d ]", locAppGTD, locNextBoundary);
				std::string strGTD = strGtdPartBody.substr( locAppGTD, (locNextBoundary - locAppGTD) );
				logger.logINGwMsg(false, TRACE_FLAG, 0, "GTD Body: [%s]", strGTD.c_str() );
	
				//	Search for " OLI " in that (for OLI indicator)
				int locOLItag = strGTD.find("OLI", 0 );
	
				logger.logINGwMsg(false, TRACE_FLAG, 0, "OLI start [ %d ], GTD end [ %d ]", locOLItag, locNextBoundary);
	
				if( (locOLItag == -1) || (locOLItag >= locNextBoundary) )
				{
					LogINGwTrace(false, 0, "No OLI info found in GTD body.");
					LogINGwTrace(false, 0, "OUT createIpDataFromSipMessage");
					return Sdf_co_success;
				}
	
				//	If found then we extract the value of OLI between the comma and CRLF flag. 
	
            	int locOLIval=strGTD.find(',',locOLItag) + 1; 
	
            	string OLIval_str=strGTD.substr(locOLIval,(strGTD.find("\r\n",locOLIval)-locOLIval));

				long oliValue = 0;
				oliValue=strtol(OLIval_str.c_str(),NULL,10);
				
				if( oliValue > -1)
				{
					logger.logINGwMsg(false, TRACE_FLAG, 0, "OLI found in GTD body: %d", oliValue);
					//aSipConnection->setOLI(oliValue);
				}
			}
			else
			{
				LogINGwTrace(false, 0, "No GTD body found.");
			}
		}
	} // end of else
	
	LogINGwTrace(false, 0, "OUT createIpDataFromSipMessage");
	return Sdf_co_success;
} // End of createIpDataFromSipMessage

////////////////////////////////////////////////////////////////////////////////
// Method: copyMsgBodyIntoSipMessage
// Description: This method sets a message body in a sip message as unknown
//              body.
// OUT - aSipMessage: Sip Message whose body is set.
// IN  - aMsgBody   : the raw message body to set.
// IN  - aMsgBodyLen: the length of the message body.
////////////////////////////////////////////////////////////////////////////////
Sdf_ty_retVal INGwSpSipUtil::copyMsgBodyIntoSipMessage(SipMessage *aSipMessage,
                                                   const char *aMsgBody,
                                                   int         aMsgBodyLen)
{
  LogINGwTrace(false, 0, "IN copyMsgBodyIntoSipMessage");

  logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, 
    "BDYLEN:<%d>", aMsgBodyLen);

  Sdf_ty_retVal status;
  SipError      siperror;
  Sdf_st_error  sdferror;

  void* tmpMsgBuf = fast_memget(0, aMsgBodyLen, &siperror);
  memcpy(tmpMsgBuf, aMsgBody, aMsgBodyLen);

  // Create a sip message body and set the unknown message body in it.
  SipMsgBody *sipMsgBody = NULL;
  status = sdf_ivk_uaCreateMessageBodyFromBuffer
             (&sipMsgBody,
             (char*)"doesnot/matter",
             (char *)tmpMsgBuf,
             aMsgBodyLen,
             Sdf_co_false,
             &sdferror);

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                    "XXXX: ref count after creating msgbody  is <%u>", sipMsgBody->dRefCount.ref);


  // Create an initialize a list and append the message body to the list.
  // We do not need to delete the list, since it is not a pointer.  We do not
  // need to delete the msg body in the list also, since this is going to be
  // copied into the sip message, and will be deleted when the sip message gets
  // deleted.
  Sdf_ty_slist msgBodyList;
  status = sdf_listInit  (&msgBodyList, Sdf_co_null, Sdf_co_false, &sdferror);
  status = sdf_listAppend(&msgBodyList, (void *)sipMsgBody, &sdferror);

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "XXXX: ref count after setting msgbody  into list is <%u>", sipMsgBody->dRefCount.ref);

  // Set the message body list in the sip message.
  status = sdf_ivk_uaSetMsgBodyListInSipMessage
             (&msgBodyList, aSipMessage, &sdferror);

  status = sdf_listDeleteAll(&msgBodyList, &sdferror);
  if(status != Sdf_co_success)
  {
    logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, "copyMsgBodyIntoSipMessage: Error deleting all elements from temporary msgbodylist");
    checkError(status, sdferror);
  }

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "XXXX: ref count after setting msgbody into sipmsg is <%u>", sipMsgBody->dRefCount.ref);

  // We do not need the msgbody anymore directly
  HSS_LOCKEDDECREF(sipMsgBody->dRefCount);

  LogINGwTrace(false, 0, "OUT copyMsgBodyIntoSipMessage");
  return Sdf_co_success;
} // End of copyMsgBodyIntoSipMessage

////////////////////////////////////////////////////////////////////////////////
// Method: getMethodFromSipMessage
// Description: This method gets the method type from a sip message.
////////////////////////////////////////////////////////////////////////////////
INGwSipMethodType INGwSpSipUtil::getMethodFromSipMessage(SipMessage *aSipMessage)
{
  Sdf_ty_retVal status;
  INGwSipMethodType     rettype = INGW_SIP_METHOD_TYPE_UNKNOWN;
  char            *pMethod = NULL;
  Sdf_st_error     sdferror;
  status = sdf_ivk_uaGetMethodFromSipMessage(aSipMessage, &pMethod, &sdferror);

  if(pMethod != NULL)
  {
         if(!strcmp(pMethod, "INVITE"  )) return INGW_SIP_METHOD_TYPE_INVITE   ;
    else if(!strcmp(pMethod, "ACK"     )) return INGW_SIP_METHOD_TYPE_ACK      ;
    else if(!strcmp(pMethod, "CANCEL"  )) return INGW_SIP_METHOD_TYPE_CANCEL   ;
    else if(!strcmp(pMethod, "BYE"     )) return INGW_SIP_METHOD_TYPE_BYE      ;
    else if(!strcmp(pMethod, "OPTIONS" )) return INGW_SIP_METHOD_TYPE_OPTIONS  ;
    else if(!strcmp(pMethod, "NOTIFY"  )) return INGW_SIP_METHOD_TYPE_NOTIFY   ;
    else if(!strcmp(pMethod, "REFER"   )) return INGW_SIP_METHOD_TYPE_REFER    ;
    else if(!strcmp(pMethod, "INFO"    )) return INGW_SIP_METHOD_TYPE_INFO     ;
    else if(!strcmp(pMethod, "PRACK"   )) return INGW_SIP_METHOD_TYPE_PRACK    ;
    else if(!strcmp(pMethod, "REGISTER"   )) return INGW_SIP_METHOD_TYPE_REGISTER    ;
    else if(!strcmp(pMethod, "UPDATE"   )) return INGW_SIP_METHOD_TYPE_UPDATE    ;
  }

  return rettype;
} // End of getMethodFromSipMessage

void INGwSpSipUtil::checkError(Sdf_ty_retVal aRetval, Sdf_st_error &error)
{
  if(aRetval == Sdf_co_fail)
    logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, "INGwSpSipUtil::checkError: Stack error: <%5d> <%s>\n", error.errCode, (char *)(error.ErrMsg));
}

////////////////////////////////////////////////////////////////////////////////
//
//
// Method: getHostFromHdr
// Description: This method retrieves the "host" part from a From/To header.
//
// The out pointer has to be used/copied before the header is freeded up.
// This function should be viewed just as a clubbing of stack API's to get the
// host pointer from Url
//
////////////////////////////////////////////////////////////////////////////////
void
INGwSpSipUtil::getHostFromHdr(SipHeader *aHdr, 
  char **aOutHost, int* aOutPort) {

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "IN getHostFromHdr");

  SipAddrSpec *addrspec = 0;
  SipUrl      *psipurl  = 0;
  SipBool      sipstatus= SipFail;
  SipError     siperr;

  switch(aHdr->dType) {

    case SipHdrTypeFrom:
      sipstatus = sip_getAddrSpecFromFromHdr(aHdr, &addrspec, &siperr);
      break;
    case SipHdrTypeTo:
      sipstatus = sip_getAddrSpecFromToHdr(aHdr, &addrspec, &siperr);
      break;
    default:
      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
        "getUserFromHdr Unrecognized headertype <%d> passed\n", aHdr->dType);
      break;
  }

  if(SipFail == sipstatus) {
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT getHostFromHdr");
    return;
  }

  sipstatus = sip_getUrlFromAddrSpec(addrspec, &psipurl, &siperr);

  if(SipFail == sipstatus) {
    sip_freeSipAddrSpec(addrspec);
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                           "sip_getUrlFromAddrSpec FAILED ...");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT getHostFromHdr");
    return;
  }

  sipstatus = sip_getHostFromUrl(psipurl, aOutHost, &siperr);
  if(SipFail == sipstatus) {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
                           "sip_getHostFromUrl FAILED ...");
  }

  if(aOutPort)
  {
    unsigned short tmpPort = 0;
    sip_getPortFromUrl(psipurl, &tmpPort, &siperr);

    if(tmpPort == 0)
    {
       tmpPort = 5060;
    }

    *aOutPort = (int)tmpPort;
  }

  // The out pointer will be valid after Url Free (below) if it is 
  // used/copied before the header is freed.

  sip_freeSipUrl(psipurl);
  sip_freeSipAddrSpec(addrspec);

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT getHostFromHdr");
  return;
}

//User has to release the url reference after use.
SipUrl * INGwSpSipUtil::getSipUrlFromHdr(SipHeader *hdr)
{
   SipAddrSpec *addrspec = NULL;
   SipUrl *ret  = NULL;
   SipError err;

   switch(hdr->dType)
   {
      case SipHdrTypeFrom:
      {
         if(sip_getAddrSpecFromFromHdr(hdr, &addrspec, &err) == SipFail)
         {
            logger.logMsg(ERROR_FLAG, 0, "Error getting addrSpec from FromHDR");
            return NULL;
         }
      }
      break;

      case SipHdrTypeTo:
      {
         if(sip_getAddrSpecFromToHdr(hdr, &addrspec, &err) == SipFail)
         {
            logger.logMsg(ERROR_FLAG, 0, "Error getting addrSpec from ToHDR");
            return NULL;
         }
      }
      break;

			case SipHdrTypeRecordRoute:
			{
         if(sip_getAddrSpecFromRecordRouteHdr(hdr, &addrspec, &err) == SipFail)
         {
            logger.logMsg(ERROR_FLAG, 0, "Error getting addrSpec from Record-Route HDR");
            return NULL;
         }
      }
      break;

      default:
      {
         logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                         "Unrecognized headertype <%d> passed", hdr->dType);
         return NULL;
      }
      break;
   }

   if(sip_getUrlFromAddrSpec(addrspec, &ret, &err) == SipSuccess)
   {
      sip_freeSipAddrSpec(addrspec);
      return ret;
   }

   sip_freeSipAddrSpec(addrspec);
   return NULL;
}

////////////////////////////////////////////////////////////////////////////////
// Method: getUserFromHdr
// Description: This method retrieves the "user" part from a From/To header.
////////////////////////////////////////////////////////////////////////////////
char* INGwSpSipUtil::getUserFromHdr(SipHeader *aHdr, char *aOutUser, int aMaxLen)
{
  SipAddrSpec *addrspec = NULL;
  SipUrl      *psipurl  = NULL;
  char        *puser    = NULL;
  SipBool      sipstatus= SipFail;
  SipError     siperr;

  aOutUser[0]          = 0;

  switch(aHdr->dType)
  {
    case SipHdrTypeFrom:
      sipstatus = sip_getAddrSpecFromFromHdr(aHdr, &addrspec, &siperr);
      break;
    case SipHdrTypeTo:
      sipstatus = sip_getAddrSpecFromToHdr(aHdr, &addrspec, &siperr);
      break;
    default:
      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, " INGwSpSipUtil::getUserFromHdr Unrecognized headertype <%d> passed\n", aHdr->dType);
      break;
  }

  if(addrspec && (sipstatus == SipSuccess))
  {
    sipstatus = sip_getUrlFromAddrSpec(addrspec, &psipurl, &siperr);
    if(sipstatus == SipSuccess)
    {
      sipstatus = sip_getUserFromUrl(psipurl, &puser, &siperr);
      if(sipstatus != SipSuccess)
        puser = NULL;
      sip_freeSipUrl(psipurl);
    }
    sip_freeSipAddrSpec(addrspec);
  }

  if(puser)
  {
    if(strlen(puser) > aMaxLen - 1)
      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "INGwSpSipUtil::getUserFromHdr: user <%s> is too big to put in the supplied buffer", puser);
    else
      strcpy(aOutUser, puser);
  }

  return aOutUser;
}

////////////////////////////////////////////////////////////////////////////////
// Method: getDNameFromHdr
// Description: This method retrieves the "user" part from a From/To header.
////////////////////////////////////////////////////////////////////////////////
char* INGwSpSipUtil::getDNameFromHdr(SipHeader *aHdr, char *aOutUser, int aMaxLen)
{
  char        *pName    = NULL;
  SipBool      sipstatus= SipFail;
  SipError     siperr;
  
  aOutUser[0]          = 0;
  
  switch(aHdr->dType)
  {
    case SipHdrTypeFrom:
      sipstatus = sip_getDispNameFromFromHdr(aHdr, &pName, &siperr);
      break;
    case SipHdrTypeTo:
      sipstatus = sip_getDispNameFromToHdr(aHdr, &pName, &siperr);
      break;
    default:
      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, " INGwSpSipUtil::getDNameFromHdr Unrecognized headertype <%d> passed\n", aHdr->dType);
      return NULL;
      break;
  }

  if(pName && (sipstatus == SipSuccess))
  {
    if(strlen(pName) > aMaxLen - 1)
    {
      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "INGwSpSipUtil::getDNameFromHdr: Display Name <%s> is too big to put in the supplied buffer", pName);
      strncpy(aOutUser, pName, aMaxLen -1);
    }
    else
      strcpy(aOutUser, pName);
  }

  logger.logINGwMsg (false, VERBOSE_FLAG, 0,
    "Display Name extracted from the SipHeader is <%s>", 
    aOutUser);

  return aOutUser;
}


////////////////////////////////////////////////////////////////////////////////
// Method: setContentTypeInMessage
// Description: This method sets content type in a sip message.
////////////////////////////////////////////////////////////////////////////////
Sdf_ty_retVal INGwSpSipUtil::setContentTypeInMessage(SipMessage *aSipMessage,
                                                 const char *aType,
                                                 int        contentLength,
                                                 int contentTypeLength)
{
  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "IN setContentTypeInMessage");

  logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, "ContentType:<%s>, ContentLength:<%d>", 
    aType, contentLength);

  if(!aSipMessage->pGeneralHdr->pContentTypeHdr) {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                  "Content-type header is NULL in the SIP message.\n");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT setContentTypeInMessage");
    return Sdf_co_fail;
  }

  SipError siperror;

  SipHeader hdr;
  hdr.dType   = SipHdrTypeContentType;
  hdr.pHeader = aSipMessage->pGeneralHdr->pContentTypeHdr;

  if(contentTypeLength == 0)
  {
     contentTypeLength = strlen(aType);
  }

  char *tmptype = (SIP_S8bit *)fast_memget(0, contentTypeLength + 1, &siperror);
  strcpy(tmptype, aType);

  bool sipstatus = sip_setMediaTypeInContentTypeHdr(&hdr, tmptype, &siperror);
  if(sipstatus != SipSuccess) {
    delete[] tmptype;
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                  "sip_setMediaTypeInContentTypeHdr Failed");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT setContentTypeInMessage");
    return Sdf_co_fail;
  }

  SipHeader* pHeader = Sdf_co_null;
  SipHeader  dHeader;
  Sdf_st_error  l_err;

  if(sip_getHeader(aSipMessage,SipHdrTypeContentLength,&dHeader,
    &siperror)==SipFail) {

    if( siperror == E_NO_EXIST) {

      if (sip_initSipHeader(&pHeader, SipHdrTypeContentLength, 
       &siperror) == SipFail) {

        logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                  "sip_initSipHeader Failed");
        logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT setContentTypeInMessage");
        return Sdf_co_fail;
      }
      if (sip_setLengthInContentLengthHdr(pHeader, contentLength, 
       &siperror) == SipFail) {
        sip_freeSipHeader(pHeader);
        sdf_memfree(0, (Sdf_ty_pvoid *)&pHeader, &l_err);
        
        logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
                  "sip_setLengthInContentLengthHdr Failed");
        logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT setContentTypeInMessage");
        return Sdf_co_fail;
      }
      if (sip_setHeader(aSipMessage, pHeader, 
       &siperror) == SipFail) {
        sip_freeSipHeader(pHeader);
        sdf_memfree(0, (Sdf_ty_pvoid *)&pHeader, &l_err);

        logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
                  "sip_setHeader Failed");
        logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT setContentTypeInMessage");
        return Sdf_co_fail;
      }
      /* Freeing the local reference */	
      sip_freeSipHeader(pHeader);
      sdf_memfree(0, (Sdf_ty_pvoid *)&pHeader, &l_err);
    }
    else {
      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
                  "sip_getHeader Fail reason not E_NO_EXIST");
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT setContentTypeInMessage");
      return Sdf_co_fail;
    }
  }
  else {
    sip_setLengthInContentLengthHdr(&dHeader, contentLength, 
     &siperror);
    /* Free local reference */
    sip_freeSipHeader(&dHeader);
  }

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT setContentTypeInMessage");
  return Sdf_co_success;

} // end of setContentTypeInMessage

Sdf_ty_retVal INGwSpSipUtil::sendCallToPeer(Sdf_st_callObject *aCallObj   ,
                                        SipMessage        *aSipMsg    ,
                                        INGwSipMethodType       aMethodType,
                                        Sdf_st_error      *aErr       ,
                                        INGwSpSipConnection *apConn)
{
  SipOptions    options;
  Sdf_ty_u8bit  dTranspType = 0;

 //options.dOption is being changed from 0 to SIP_OPT_CLEN
 // to include Content-Length header in all the messages going out 
 //as per RFC3261

  options.dOption = miSipHdrOpt;
  
  if(INGwSpSipProviderConfig::isTransportUdp() )
  {
    dTranspType |= SIP_UDP;
  }
  else
  {
    dTranspType |= SIP_TCP;
    dTranspType |= SIP_NORETRANS;

		// Another place where destination could be set
/*
    if(apConn != NULL)
		{
      string l_strHost = string(aSipConnection->getGwIPAddress());
	    int l_port = aSipConnection->getActiveEPPort();
      Sdf_st_overlapTransInfo* lOverlapTransInfo = getLastOverlapTransInfo(aCallObj);
      if(sdf_ivk_uaSetDestTransportInTransaction(aCallObj, 
																								 lOverlapTransInfo, 
																								 (char *)l_strHost.c_str(),
                                                 l_port, 
																								 (char *)"TCP", 
																								 &aErr) != Sdf_co_success)
      {
        logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
													"Error in setting Destination details ");
				INGwSpSipUtil::checkError(status, sdferror);
			}
		}
*/
  }

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "IN sendCallToPeer()");

  bool lPrintTrDet = false;
  //print transport details
  if(lPrintTrDet == true)
  {
    Sdf_st_destDetails * lDestDetails = NULL;
    Sdf_st_overlapTransInfo* lOverlapTransInfo = getLastOverlapTransInfo(aCallObj);
    Sdf_st_error lError_;
    if(sdf_ivk_uaGetDestinationDetails(aCallObj, lOverlapTransInfo, 
                                       aSipMsg,&lDestDetails, &lError_) != Sdf_co_fail)
    {
      Sdf_st_transportInfo * lDstStruct = lDestDetails->u.pDestInfo;
      if(lDstStruct != NULL)
      {
        logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
													"Destination details "
													"IP <%s> "
													"PORT <%d> "
													"SCHEME <%s> ",
													(char*)lDstStruct->pIp,
													(int)lDstStruct->dPort,
													(char*) lDstStruct->pScheme);
      }
      else
      {
        logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "Sdf_st_transportInfo is NULl");
      }
    }
		else
		{
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "Failed to get destination info");
		}
  }
  // Add a timer context in the send call function, so that if this request
  // response times out, we will get a time out callback.
  INGwSipTimerContext *timercontext = new INGwSipTimerContext;
  strcpy(timercontext->mCallId, aCallObj->pCommonInfo->pCallid);
  timercontext->mMethodType       = aMethodType;
  timercontext->mType             = INGwSipTimerContext::STACK_TIMER;
  Sdf_st_eventContext *pEvCont    = NULL;
  sdf_ivk_uaInitEventContext(&pEvCont, aErr);

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "sendCallToPeer 1");

  // Set the timer context in the app data.
  Sdf_st_appData *pAppSpecificData = Sdf_co_null;
  sdf_ivk_uaInitAppData(&pAppSpecificData, aErr);
  pAppSpecificData->pData = (void *)timercontext;

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "sendCallToPeer 2");

  // Set the app data in the event context.
  pEvCont->pData                  = (void *)pAppSpecificData;

#ifdef EXTRA_LOGS
  logger.logINGwMsg(false,VERBOSE_FLAG,0,"+rem+ before sdf_ivk_uaSendCallToPeer +conn+ <%x>",
                    INGwSpSipProvider::getInstance().getThreadSpecificSipData().conn);
#endif

  INGwSpSipProvider::getInstance().getThreadSpecificSipData().conn = apConn;
  Sdf_ty_retVal status = sdf_ivk_uaSendCallToPeer
    (aCallObj, aSipMsg, options, dTranspType, pEvCont, aErr);

#ifdef EXTRA_LOGS
  logger.logINGwMsg(false,VERBOSE_FLAG,0,"+rem+ after sdf_ivk_uaSendCallToPeer +conn+ <%x>",
                    INGwSpSipProvider::getInstance().getThreadSpecificSipData().conn);
#endif

  //do this after
#ifndef QUEUE_CHANGES
  INGwSpSipProvider::getInstance().getThreadSpecificSipData().conn = NULL;
#else
  return status;
#endif

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "sendCallToPeer 3 : [%d] : [%d]", status, (*aErr));

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT sendCallToPeer()");

  // sdf_ivk_uaFreeEventContext(pEvCont);
  return status;
} // end of sendCallToPeer

/////////////////////////////////////////////
//
// copyFromHdrUser
//
/////////////////////////////////////////////
Sdf_ty_retVal
INGwSpSipUtil::copyFromHdrUser(Sdf_st_callObject* fromCallObj,
  Sdf_st_callObject* toCallObj) {
  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "IN copyFromHdrUser");

  SipBool       sipstatus = SipFail;
  Sdf_ty_retVal l_retVal = Sdf_co_fail;

  if(0 == fromCallObj || 0 == toCallObj) {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
         "Hss Call Object passed is null ...");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT copyFromHdrUser");
    return l_retVal;
  }

  SipError     l_sipError;

  SipAddrSpec* l_addrSpecIn = 0;

  SipHeader*   l_fmHdrOg = toCallObj->pCommonInfo->pFrom;
  SipHeader*   l_fmHdrIn = fromCallObj->pCommonInfo->pFrom;

  //copy the displayname also
  SIP_S8bit *l_DispName = 0;

  sipstatus = sip_getDispNameFromFromHdr (l_fmHdrIn, &l_DispName, &l_sipError);
  if(SipSuccess != sipstatus) {
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE,
      "No Display Name in From Header");
    l_DispName = 0;
  }
  else
    logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE,
      "Display Name in Incoming From Header <%s>", l_DispName);


  sipstatus = sip_getAddrSpecFromFromHdr(l_fmHdrIn, &l_addrSpecIn, &l_sipError);
  if(SipSuccess != sipstatus) {
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT copyFromHdrUser");
    return l_retVal;
  }

  SipUrl* l_sipUrlIn = 0;
  sipstatus = sip_getUrlFromAddrSpec(l_addrSpecIn, &l_sipUrlIn, &l_sipError);
  if(SipSuccess != sipstatus) {
    sip_freeSipAddrSpec(l_addrSpecIn);
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT copyFromHdrUser");
    return l_retVal;
  }

  SIP_S8bit* l_userIn = 0;
  sipstatus = sip_getUserFromUrl(l_sipUrlIn, &l_userIn, &l_sipError); 
  if(SipSuccess != sipstatus) {
    l_userIn = 0;
    logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, 
      "sip_getUserFromUrl failed, generated INVITE will not have a user in the from url");
  }

  if(0 != l_userIn) { 
    logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, 
                "user retrieved:<%s>", l_userIn);
  }
  else {
    logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE,
         "user retrieved is null from url of from hdr");
  }

  SipAddrSpec* l_addrSpecOg = 0;
  sipstatus = sip_getAddrSpecFromFromHdr(l_fmHdrOg, &l_addrSpecOg, &l_sipError);
  if(SipSuccess != sipstatus) {
    sip_freeSipUrl(l_sipUrlIn);
    sip_freeSipAddrSpec(l_addrSpecIn);
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT copyFromHdrUser");
    return l_retVal;
  }

  SipUrl* l_sipUrlOg = 0;
  sipstatus = sip_getUrlFromAddrSpec(l_addrSpecOg, &l_sipUrlOg, &l_sipError);
  if(SipSuccess != sipstatus) {
    sip_freeSipUrl(l_sipUrlIn);
    sip_freeSipAddrSpec(l_addrSpecIn);
    sip_freeSipAddrSpec(l_addrSpecOg);
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT copyFromHdrUser");
    return l_retVal;
  }

  SIP_S8bit* l_userOg = 0;
  SIP_S8bit *l_outName = 0;
  if(0 != l_userIn) {
    l_userOg = (SIP_S8bit*) 
                   fast_memget(0, strlen(l_userIn)+1, &l_sipError);
    strcpy(l_userOg, l_userIn);
    //set the display name here
    if (!l_DispName)
      l_DispName = l_userIn;

    if (l_DispName)
    {
      l_outName = (SIP_S8bit*)
                  fast_memget(0, strlen(l_DispName)+1, &l_sipError);

      strcpy (l_outName, l_DispName);

      logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE,
        "Display Name in Outgoing From Header <%s, %s>", l_outName, l_DispName);
      sipstatus = sip_setDispNameInFromHdr (l_fmHdrOg, l_outName, &l_sipError);
      if (sipstatus != SipSuccess)
      {
        fast_memfree (0, l_outName, &l_sipError);
        logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
          "Unable to set Display Name in From Header");
        l_outName = 0;
      }
    }

  }
  else if(0 == INGwSpSipProviderConfig::getDummyUserForFromHdr()) {
    // Mriganka - If Dummy user is configured to NULL(i.e not configured)
    // then don't set the User part to SPACE in outgoing From Header.
    // l_userOg = (SIP_S8bit*)
    //             fast_memget(0, 1+1, &l_sipError);
    // strcpy(l_userOg, " ");
  }
  else {
    char* l_dummyUser = INGwSpSipProviderConfig::getDummyUserForFromHdr();
    l_userOg = (SIP_S8bit*)
                  fast_memget(0, strlen(l_dummyUser)+1, &l_sipError);
    strcpy(l_userOg, l_dummyUser);
  }

  sipstatus = sip_setUserInUrl(l_sipUrlOg, l_userOg, &l_sipError);
  if(SipSuccess != sipstatus) {
    fast_memfree (0, l_outName, &l_sipError);
    fast_memfree(0, l_userOg, &l_sipError);
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                    "Could not set user in outgoing URL");
  }

  sip_freeSipUrl(l_sipUrlIn);
  sip_freeSipAddrSpec(l_addrSpecIn);
  sip_freeSipUrl(l_sipUrlOg);
  sip_freeSipAddrSpec(l_addrSpecOg);
 
  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT copyFromHdrUser");
  return Sdf_co_success;
}

/////////////////////////////////////////////
//
// copyFromHdr
//
/////////////////////////////////////////////
Sdf_ty_retVal
INGwSpSipUtil::copyFromHdr(Sdf_st_callObject* fromCallObj,
  Sdf_st_callObject* toCallObj,
  bool copyUrlParams,
  bool copyHdrParams,
  void* paramList)
{
  LogINGwTrace(false, 0, "IN copyFromHdr");

  Sdf_ty_retVal l_retVal  = Sdf_co_fail;
  SipBool       sipstatus = SipFail;

  if(0 == fromCallObj || 0 == toCallObj)
  {
    LogINGwTrace(false, 0, "OUT copyFromHdr");
    return l_retVal; 
  }

  SipError     l_sipError;
  SipAddrSpec* l_addrSpecIn = 0;

  SipHeader*   l_fmHdrOg = toCallObj->pCommonInfo->pFrom;
  SipHeader*   l_fmHdrIn = fromCallObj->pCommonInfo->pFrom;

  //copy the displayname also
  SIP_S8bit *l_DispName = 0;

  sipstatus = sip_getDispNameFromFromHdr (l_fmHdrIn, &l_DispName, &l_sipError);
  if(SipSuccess != sipstatus) {
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE,
      "No Display Name in From Header");
    l_DispName = 0;
  }


  sipstatus = sip_getAddrSpecFromFromHdr(l_fmHdrIn, &l_addrSpecIn, &l_sipError);
  if((0 == l_addrSpecIn) || (SipSuccess != sipstatus))
  {
    LogINGwTrace(false, 0, "OUT copyFromHdr");
    return l_retVal;
  }

  sipstatus = sip_setAddrSpecInFromHdr(l_fmHdrOg, l_addrSpecIn, &l_sipError);
  if(SipSuccess == sipstatus)
  {
    sip_freeSipAddrSpec(l_addrSpecIn);
  }

  if (!l_DispName)
  {

    SipUrl* l_sipUrlIn = 0;
    sipstatus = sip_getUrlFromAddrSpec(l_addrSpecIn, &l_sipUrlIn, &l_sipError);
    if(SipSuccess != sipstatus) {
      sip_freeSipAddrSpec(l_addrSpecIn);
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT copyFromHdrUser");
      return l_retVal;
    }

    SIP_S8bit* l_userIn = 0;
    sipstatus = sip_getUserFromUrl(l_sipUrlIn, &l_userIn, &l_sipError);
    if(SipSuccess != sipstatus) {
      l_userIn = 0;
      logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE,
        "sip_getUserFromUrl failed, generated INVITE will not have a user in the from url");
    }

    l_DispName = l_userIn;
    sip_freeSipUrl(l_sipUrlIn);
  }



  //set the display name here 
  if (l_DispName)
  {
    SIP_S8bit *l_outName = (SIP_S8bit*)
                fast_memget(0, strlen(l_DispName)+1, &l_sipError);
  
    strcpy (l_outName, l_DispName);
  
    logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE,
      "Display Name in Outgoing From Header <%s>", l_outName);
    sipstatus = sip_setDispNameInFromHdr (l_fmHdrOg, l_outName, &l_sipError);
    if (sipstatus != SipSuccess)
    {
      fast_memfree (0, l_outName, &l_sipError);
      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
        "Unable to set Display Name in From Header");
    }
  } 
      
  LogINGwTrace(false, 0, "OUT copyFromHdr");

  if(SipSuccess == sipstatus)
    return Sdf_co_success;
  else
    return Sdf_co_fail;
} // end of copyFromHdr

Sdf_ty_retVal INGwSpSipUtil::copyToHdr
  (Sdf_st_callObject* fromCallObj, Sdf_st_callObject* toCallObj)
{
  LogINGwTrace(false, 0, "IN copyToHdr");

  Sdf_ty_retVal l_retVal  = Sdf_co_fail;
  SipBool       sipstatus = SipFail;

  if(0 == fromCallObj || 0 == toCallObj)
  {
    LogINGwTrace(false, 0, "OUT copyToHdr");
    return l_retVal;
  }

  SipError     l_sipError;
  SipAddrSpec* l_addrSpecIn = 0;

  SipHeader*   l_fmHdrOg = toCallObj->pCommonInfo->pTo;
  SipHeader*   l_fmHdrIn = fromCallObj->pCommonInfo->pTo;

  sipstatus = sip_getAddrSpecFromFromHdr(l_fmHdrIn, &l_addrSpecIn, &l_sipError);
  if((0 == l_addrSpecIn) || (SipSuccess != sipstatus))
  {
    LogINGwTrace(false, 0, "OUT copyToHdr");
    return l_retVal;
  }

  sipstatus = sip_setAddrSpecInFromHdr(l_fmHdrOg, l_addrSpecIn, &l_sipError);
  if(SipSuccess == sipstatus)
  {
    sip_freeSipAddrSpec(l_addrSpecIn);
  }
  
  LogINGwTrace(false, 0, "OUT copyToHdr");

  if(SipSuccess == sipstatus)
    return Sdf_co_success;
  else
    return Sdf_co_fail;
} // end of copyToHdr

Sdf_ty_retVal 
INGwSpSipUtil::replaceFromHdrUser (Sdf_st_callObject* apCallObj,
                               INGwSpAddress &arOrigAddr)
{
  logger.logINGwMsg (false, TRACE_FLAG, 0,
    "Entering INGwSpSipUtil::replaceFromHdrUser");

  if(0 == apCallObj)
  {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
         "Hss Call Object passed is null ...");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT replaceFromHdrUser");
    return Sdf_co_fail;
  }

  SipError     l_sipError;
  SipBool       sipstatus = SipFail;

  SipHeader*   l_fmHdrOg = apCallObj->pCommonInfo->pFrom;

  SipAddrSpec* l_addrSpecOg = 0;
  sipstatus = sip_getAddrSpecFromFromHdr(l_fmHdrOg, &l_addrSpecOg, &l_sipError);
  if(SipSuccess != sipstatus) {
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT replaceFromHdrUser");
    return Sdf_co_fail;
  }

  SipUrl* l_sipUrlOg = 0;
  sipstatus = sip_getUrlFromAddrSpec(l_addrSpecOg, &l_sipUrlOg, &l_sipError);
  if(SipSuccess != sipstatus) {
    sip_freeSipAddrSpec(l_addrSpecOg);
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT replaceFromHdrUser");
    return Sdf_co_fail;
  }

  SIP_S8bit* l_userOg = 0;
  const char *lpcUser = arOrigAddr.getAddress ();
  if(0 != lpcUser && strlen(lpcUser) > 0) {

    l_userOg = (SIP_S8bit*)
                   fast_memget(0, strlen(lpcUser)+1, &l_sipError);
    strcpy(l_userOg, lpcUser);

    sipstatus = sip_setUserInUrl(l_sipUrlOg, l_userOg, &l_sipError);
    if(SipSuccess != sipstatus) {
      fast_memfree(0, l_userOg, &l_sipError);
      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
                      "Could not set user in outgoing URL");
    }

  }
  else
  {
    logger.logINGwMsg (false, ERROR_FLAG, 0,
      "The originating address passed is NULL");
  }

  sip_freeSipUrl(l_sipUrlOg);
  sip_freeSipAddrSpec(l_addrSpecOg);

  logger.logINGwMsg (false, TRACE_FLAG, 0,
    "Leaving INGwSpSipUtil::replaceFromHdrUser");

  return Sdf_co_success;
}


short INGwSpSipUtil::releaseConnection(INGwSpSipConnection *aSipConnection)
{
  LogINGwTrace(false, 0, "IN releaseConnection");
  short nConnections = 0;

  // Get the connection context from HSS call object.
  Sdf_st_callObject *hssCallObj = aSipConnection->getHssCallObject();


  // Remove the SIP call table's reference of the HSS Call leg.
  Sdf_st_callObject *mapObject = NULL;

  INGwSpSipProvider::getInstance().getThreadSpecificSipData().getCallTable().
    remove(aSipConnection->getLocalCallId().c_str(), &mapObject);


	if(mapObject) {
	  logger.logINGwMsg(aSipConnection->mLogFlag, TRACE_FLAG, 0, 
                    "INGwSpSipUtil::releaseConnection: HSS Call Object Ref-Count [ %d ].", 
                    (mapObject->dRefCount).ref);

    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                      "INGwSpSipUtil::releaseConnection: Removed call-leg <%s> from callleg-map", 
                      aSipConnection->getLocalCallId().c_str());
    sdf_ivk_uaFreeCallObject(&mapObject);
  }


  // Free the HSS call object's reference from the connection.
  if(hssCallObj)
  {
    aSipConnection->setHssCallObject(NULL);

    INGwConnectionContext *bpconncontext =
      (INGwConnectionContext *)(hssCallObj->pAppData->pData);
    if(bpconncontext)
    {
      hssCallObj->pAppData->pData = NULL;
      delete bpconncontext;
      aSipConnection->releaseRef();
    }

	  logger.logINGwMsg(aSipConnection->mLogFlag, TRACE_FLAG, 0, 
                    "INGwSpSipUtil::releaseConnection: HSS Call Object Ref-Count [ %d ].", 
                    (hssCallObj->dRefCount).ref);

    // Remove the connection's reference from the HSS call object.
    sdf_ivk_uaFreeCallObject(&hssCallObj);
  }

  LogINGwTrace(false, 0, "OUT releaseConnection");
  return nConnections;
} // end of releaseConnection method

char* INGwSpSipUtil::muteBody(const char* aType, const char *aBody, int aBodyLength)
{
    if(0 == strcasecmp(aType, "application/sdp")) {
        return INGwSpSipUtil::muteSdp(aBody, aBodyLength);
    }
    else if (aType == strstr(aType, "multipart/mixed")) {
        return INGwSpSipUtil::muteMultipart(aBody, aBodyLength);
    }
    else {
    }
    return NULL;
}

char* INGwSpSipUtil::muteMultipart(const char* aSdp, int sdpLength)
{
   static int muteLen = strlen("c=IN IP4 0.0.0.0");

   char *newsdp = NULL;
   const char *sdp = _sdpAppSDPStrStr.findPatternIn(aSdp, sdpLength);
   if(NULL == sdp) {
       logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "Unable to find application/sdp");
       return newsdp;
   }

   const char *conninfo = _sdpCStrStr.findPatternIn(sdp, sdpLength - (sdp - aSdp));
   if(conninfo)
   {
      newsdp = new char[1024];
      const char *lineend = strstr(conninfo, "\r");

			if( ! lineend )
			{
				lineend = strstr(conninfo, "\n");
			}

      int frontLen = (conninfo - aSdp);
      strncpy(newsdp, aSdp, frontLen);

      strcpy(newsdp + frontLen, "c=IN IP4 0.0.0.0");
      strcpy(newsdp + frontLen + muteLen, lineend);
   }
   else {
       logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "Unable to find c=");
   }

   return newsdp;
}

char* INGwSpSipUtil::muteSdp(const char *aSdp, int sdpLen)
{
   static int muteLen = strlen("c=IN IP4 0.0.0.0");

   char *newsdp = NULL;
   const char *conninfo = _sdpCStrStr.findPatternIn(aSdp, sdpLen);

   if(conninfo)
   {
      newsdp = new char[1024];
      const char *lineend = strstr(conninfo, "\r");

			if( ! lineend )
			{
				lineend = strstr(conninfo, "\n");
			}

      int frontLen = (conninfo - aSdp);
      strncpy(newsdp, aSdp, frontLen);

      strcpy(newsdp + frontLen, "c=IN IP4 0.0.0.0");
      strcpy(newsdp + frontLen + muteLen, lineend);
   }

   return newsdp;
}

SipHeader* INGwSpSipUtil::makeFromHeader(const char* aUsername,
                                     const char* aAddr    ,
                                     int         aPort    )
{
  LogINGwTrace(false, 0, "IN makeFromHeader");
  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "INGwSpSipUtil::makeFromHeader: username <%s>, addr <%s>, port <%d>", aUsername, aAddr, aPort);

  SipError siperror;
  // char *username = new char[strlen(aUsername) + 1];
  // char *address  = new char[strlen(aAddr)     + 1];
  char *username = (char *)fast_memget(0, strlen(aUsername) + 1, &siperror);
  char *address  = (char *)fast_memget(0, strlen(aAddr) + 1, &siperror);
  strcpy(username, aUsername);
  strcpy(address , aAddr    );
  SipBool status = SipFail;

  // Create a uri and set the user, host and port number.
  SipUrl *pUrl = NULL;
  status = sip_initSipUrl  (&pUrl,                    &siperror);
  status = sip_setUserInUrl( pUrl, username,          &siperror);
  status = sip_setHostInUrl( pUrl, (char *)address,   &siperror);
  status = sip_setPortInUrl( pUrl, (SIP_U16bit)aPort, &siperror);

  // Create an address spec.
  SipAddrSpec *addrSpec = NULL;
  status = sip_initSipAddrSpec(&addrSpec, SipAddrSipUri, &siperror);
  status = sip_setUrlInAddrSpec(addrSpec, pUrl         , &siperror);

  // Create a from header.  Place the address spec in the header and we are
  // done.
  SipHeader* retval = NULL;
  sip_initSipHeader(&retval, SipHdrTypeFrom, &siperror);
  status = sip_setAddrSpecInFromHdr(retval, addrSpec, &siperror);

  LogINGwTrace(false, 0, "OUT makeFromHeader");
  return retval;
} // end of makeFromHeader

SipHeader* INGwSpSipUtil::makeContactHeader(const char* aUsername,
                                        const char* aAddr    ,
                                        int         aPort    )
{
  LogINGwTrace(false, 0, "IN makeContactHeader");
  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "INGwSpSipUtil::makeContactHeader: username <%s>, addr <%s>, port <%d>", aUsername, aAddr, aPort);
  SipError siperror;
  // char *username = new char[strlen(aUsername) + 1];
  // char *address  = new char[strlen(aAddr)     + 1];
  char *username = (char *)fast_memget(0, strlen(aUsername) + 1, &siperror);
  char *address  = (char *)fast_memget(0, strlen(aAddr) + 1, &siperror);

  strcpy(username, aUsername);
  strcpy(address , aAddr    );
  SipBool status = SipFail;

  // Create a uri and set the user, host and port number.
  SipUrl *pUrl = NULL;
  status = sip_initSipUrl  (&pUrl,                    &siperror);
  status = sip_setUserInUrl( pUrl, username,          &siperror);
  status = sip_setHostInUrl( pUrl, (char *)address,   &siperror);
  status = sip_setPortInUrl( pUrl, (SIP_U16bit)aPort, &siperror);

  // Create an address spec.
  SipAddrSpec *addrSpec = NULL;
  status = sip_initSipAddrSpec(&addrSpec, SipAddrSipUri, &siperror);
  status = sip_setUrlInAddrSpec(addrSpec, pUrl         , &siperror);

  // Create a contact header.  Place the address spec in the header and we are
  // done.
  SipHeader* retval = NULL;
  sip_initSipHeader(&retval, SipHdrTypeContactNormal, &siperror);
  status = sip_setAddrSpecInContactHdr(retval, addrSpec, &siperror);

  LogINGwTrace(false, 0, "OUT makeContactHeader");
  return retval;
} // end of makeContactHeader

////////////////////////////////////////////////////////////////////////////////
// Function: copyUrlParams
//
// Right now the struct (SipUrl) ref count is increased when the
// caller of this function does a getUrl on a AddressSpec.
// There may be need to make a clone if param changes
// in one leg are not be automatically reflected in the
// other leg.
//
// Freeing (reducing ref count) of the SipUrl struct is the
// headache of a caller of this function. 
// If u are a masochist it could be fun.
//
////////////////////////////////////////////////////////////////////////////////
Sdf_ty_retVal
INGwSpSipUtil::copyUrlParams(SipUrl* fromUrl,
   SipUrl* toUrl,
   Sdf_st_error* pError) {

#ifndef FUNC_ARG_NULL_CHECK_OFF 
  if(0 == fromUrl || 0 == toUrl) {
    return Sdf_co_fail;
  }
#endif

  Sdf_ty_u32bit iterator = 0;
  Sdf_ty_u32bit urlParamCount = 0;

  if(sip_getUrlParamCountFromUrl(fromUrl, &urlParamCount,
          (SipError *)&(pError->errCode)) == SipFail) {

    pError->errCode=Sdf_en_headerManipulationError;
    return Sdf_co_fail;
  }

  if(0 >= urlParamCount) {
    pError->errCode=Sdf_en_headerManipulationError;
    return Sdf_co_fail;
  }

  for(iterator = 0; iterator < urlParamCount; iterator++)
  {
    SipParam *pParam = 0;
    if(sip_getUrlParamAtIndexFromUrl(fromUrl, &pParam, iterator,
          (SipError *)&(pError->errCode)) != SipFail)
    {
      // Copy the Param in toUrl ...
      if(sip_insertUrlParamAtIndexInUrl(toUrl, pParam, iterator,
          (SipError *)&(pError->errCode)) == SipFail)
      {
        sip_freeSipParam(pParam);
#ifdef PARAM_COPY_BEST_EFFORT_ON 
        //log that a setting of param failed. 
#else
        //log that a setting of param failed. 
        return Sdf_co_fail;
#endif
      }
      else
      {
        sip_freeSipParam(pParam);
      }
    }
    else
    {
#ifdef PARAM_COPY_BEST_EFFORT_ON
      //log that a extraction failed ...
#else
      //log that a extraction failed ...
      return Sdf_co_fail;
#endif
    }
  }

  return Sdf_co_success;
}

////////////////////////////////////////////////////////////////////////////////
// Function:
//
//
//
////////////////////////////////////////////////////////////////////////////////
Sdf_ty_retVal
INGwSpSipUtil::copyHeaderParams(SipHeader* origHeader,
   SipHeader* targetHeader,
   Sdf_st_error* pError) {

  logger.logINGwMsg (false, TRACE_FLAG, 0,
    "Entering INGwSpSipUtil::copyHeaderParams");

  if(0 == origHeader || 0 == targetHeader) {
    pError->errCode=Sdf_en_headerManipulationError;
    logger.logINGwMsg (false, TRACE_FLAG, 0,
      "Leaving INGwSpSipUtil::copyHeaderParams");
    return Sdf_co_fail;
  }

  Sdf_ty_u32bit iterator = 0;
  Sdf_ty_u32bit hdrParamCount = 0;
  Sdf_ty_retVal l_retVal = Sdf_co_success;

  switch(origHeader->dType) {
    case SipHdrTypeFrom: {
        //check if destination header is from header.
        if(targetHeader->dType != SipHdrTypeFrom) {
          l_retVal = Sdf_co_fail;
          break;
        }
        
        //destination header is from header. 
        if(sip_getExtensionParamCountFromFromHdr(origHeader,
             &hdrParamCount, (SipError *)&(pError->errCode)) == SipFail) {
          pError->errCode=Sdf_en_headerManipulationError;
          logger.logINGwMsg (false, TRACE_FLAG, 0,
            "Leaving INGwSpSipUtil::copyHeaderParams");
          return Sdf_co_fail;
        }
        if(0 >= hdrParamCount) {
          pError->errCode=Sdf_en_headerManipulationError;
          logger.logINGwMsg (false, TRACE_FLAG, 0,
            "Leaving INGwSpSipUtil::copyHeaderParams");
          return Sdf_co_fail;
        }

        //copy in target...
        for(iterator = 0; iterator < hdrParamCount; iterator++) {
          SipParam *pParam = 0;
          if (sip_getExtensionParamAtIndexFromFromHdr(origHeader,
               &pParam, iterator, (SipError *)&(pError->errCode)) != SipFail) {

            //copy the Param in toUrl ...
            if(sip_insertExtensionParamAtIndexInFromHdr(targetHeader, 
              pParam, iterator, (SipError *)&(pError->errCode)) == SipFail) {
              sip_freeSipParam(pParam);
#ifdef PARAM_COPY_BEST_EFFORT_ON 
              //log that a setting of param failed. 
#else
              //log that a setting of param failed. 
              logger.logINGwMsg (false, TRACE_FLAG, 0,
                "Leaving INGwSpSipUtil::copyHeaderParams");
              return Sdf_co_fail;
#endif
            }
            else
            {
              sip_freeSipParam(pParam);
            }
          }
          else {
#ifdef PARAM_COPY_BEST_EFFORT_ON
            //log that a extraction failed ...
#else
            //log that a extraction failed ...
            logger.logINGwMsg (false, TRACE_FLAG, 0,
              "Leaving INGwSpSipUtil::copyHeaderParams");
            return Sdf_co_fail;
#endif
          }
        } //for loop
      }
      break;

    case SipHdrTypeTo: {
        //check if destination header is from header.
        if(targetHeader->dType != SipHdrTypeTo) {
          l_retVal = Sdf_co_fail;
          break;
        }
        
        //destination header is from header. 
        if(sip_getExtensionParamCountFromToHdr(origHeader,
             &hdrParamCount, (SipError *)&(pError->errCode)) == SipFail) {
          pError->errCode=Sdf_en_headerManipulationError;
          logger.logINGwMsg (false, TRACE_FLAG, 0,
            "Leaving INGwSpSipUtil::copyHeaderParams");
          return Sdf_co_fail;
        }
        if(0 >= hdrParamCount) {
          pError->errCode=Sdf_en_headerManipulationError;
          logger.logINGwMsg (false, TRACE_FLAG, 0,
            "Leaving INGwSpSipUtil::copyHeaderParams");
          return Sdf_co_fail;
        }

        //copy in target...
        for(iterator = 0; iterator < hdrParamCount; iterator++) {
          SipParam *pParam = 0;
          if (sip_getExtensionParamAtIndexFromToHdr(origHeader,
               &pParam, iterator, (SipError *)&(pError->errCode)) != SipFail) {

            //copy the Param in toUrl ...
            if(sip_insertExtensionParamAtIndexInToHdr(targetHeader, 
              pParam, iterator, (SipError *)&(pError->errCode)) == SipFail) {
              sip_freeSipParam(pParam);
#ifdef PARAM_COPY_BEST_EFFORT_ON 
              //log that a setting of param failed. 
#else
              //log that a setting of param failed. 
              logger.logINGwMsg (false, TRACE_FLAG, 0,
                "Leaving INGwSpSipUtil::copyHeaderParams");
              return Sdf_co_fail;
#endif
            }
            else
            {
              sip_freeSipParam(pParam);
            }
          }
          else {
#ifdef PARAM_COPY_BEST_EFFORT_ON
            //log that a extraction failed ...
#else
            //log that a extraction failed ...
            logger.logINGwMsg (false, TRACE_FLAG, 0,
              "Leaving INGwSpSipUtil::copyHeaderParams");
            return Sdf_co_fail;
#endif
          }
        } //for loop
      }
      break;

    case SipHdrTypeContactNormal: {
         


      }
      break;

    default:
      break;
  };

  logger.logINGwMsg (false, TRACE_FLAG, 0,
    "Leaving INGwSpSipUtil::copyHeaderParams");

  return Sdf_co_success;
}


////////////////////////////////////////////////////////////////////////////////
//
// getReqUriParams
//
////////////////////////////////////////////////////////////////////////////////
char*
INGwSpSipUtil::getReqUriParams(SipUrl* reqUrl,
  INGwSpSipConnection* bpConn,
  Sdf_st_error* pError) {

  if(0 == reqUrl) {
    return 0;
  }

  bool l_bgtPresent = false;
  Sdf_ty_u32bit iterator = 0;
  Sdf_ty_u32bit urlParamCount = 0;
  
  if(sip_getUrlParamCountFromUrl(reqUrl, &urlParamCount,
          (SipError *)&(pError->errCode)) == SipFail) {

    pError->errCode=Sdf_en_headerManipulationError;
    return 0;
  }

  if(0 >= urlParamCount) {
    pError->errCode=Sdf_en_headerManipulationError;
    return 0;
  }

  SIP_S8bit* l_retVal = 0;

  for(iterator = 0; iterator < urlParamCount; iterator++) {
    SipParam*  pParam = 0;
    SIP_S8bit* pName  = 0;
    SIP_S8bit* pVal   = 0;

    if(sip_getUrlParamAtIndexFromUrl(reqUrl, &pParam, iterator,
          (SipError *)&(pError->errCode)) != SipFail) {

       if(sip_getNameFromSipParam(pParam, &pName,
            (SipError *)&(pError->errCode)) != SipFail) {

         if(0 == Sdf_mc_strcmp(BP_BGT_TAG, pName)) {
           l_bgtPresent = true;
           if(sip_getValueAtIndexFromSipParam(pParam, &pVal, 0, 
             (SipError *)&(pError->errCode))) {
             if(0 == Sdf_mc_strcmp(BP_BGT_PRIVATE_VAL, pVal)) {
               //bpConn->appendChargeNumber(PRIVATE_BGT);
             }
             else if(0 == Sdf_mc_strcmp(BP_BGT_PUBLIC_VAL, pVal)) {
               //bpConn->appendChargeNumber(PUBLIC_BGT);
             }
             else {
               //bpConn->appendChargeNumber(UNKNOWN_BGT);
             }
           }
         }
         else if((INGwSpSipProviderConfig::m_bgidProcessingFlag) && 
                 (0 == Sdf_mc_strcmp(BP_BGID_TAG, pName)) ) {
           sip_getValueAtIndexFromSipParam(pParam, &l_retVal, 0,
               (SipError *)&(pError->errCode));
         }
       }
      
       //Param object is not deleted as header as a 
       //reference to it. The char* returned is thus valid.
       //A local copy of char* returned is in made the 
       //InviteHandler  
       sip_freeSipParam(pParam);
    }
    else {
    }
  }//for loop

  //If BGT tag does not come assume it as a 
  //Unknown and append the charge number
  if(!l_bgtPresent) {
    //bpConn->appendChargeNumber(UNKNOWN_BGT);
  }

  return l_retVal;
}


///////////////////////////////////////////////
//
// processDiversionHdr
//
///////////////////////////////////////////////
Sdf_ty_retVal
INGwSpSipUtil::processDiversionHdr(SIP_S8bit* hdr, 
  INGwSpSipConnection* apConn, SIP_U32bit & causeCode) 
{ 
	// Not supporting Diversion header
  return Sdf_co_fail;
}


///////////////////////////////////////////////
//
// getDiversionReason
//
///////////////////////////////////////////////
SIP_U32bit
INGwSpSipUtil::getDiversionReason(SIP_S8bit* apHdr)
{
  LogINGwTrace(false, 0, "IN getDiversionReason");
  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                  "INGwSpSipUtil::getDiversionReason: hdr <%s>", apHdr);

  string l_value(apHdr);
  string key("reason");
	int causeCode =0;

  int l_pos = l_value.find(key);
  if (-1 != l_pos) 
	{
      string l_reason = l_value.substr(l_pos + key.length() + 1, l_value.length()); 
	
 			// check for user-busy or no-answer      
			if(-1 != l_reason.find("no-answer"))
				causeCode = 130;
			else if(-1 != l_reason.find("user-busy"))
				causeCode = 129;
  }
  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                  "INGwSpSipUtil::getDiversionReason: CauseCode returned<%d>", causeCode);
  return causeCode;
}

///////////////////////////////////////////////
//
// processPrivacyHeader
//
///////////////////////////////////////////////
Sdf_ty_retVal
INGwSpSipUtil::processPrivacyHeader(SIP_S8bit*       apHdr,
                               INGwSpSipConnection* apConn) 
{
  LogINGwTrace(false, 0, "IN processPrivacyHeader");
  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "INGwSpSipUtil::processPrivacyHeader: hdr <%s>", apHdr);

  int liHdrLen = strlen(apHdr);
  char *lpcValue = new char [ liHdrLen + 1];
  strncpy (lpcValue, apHdr, liHdrLen);
  lpcValue [liHdrLen] = '\0';

  int liPrivacy = 0;

  char *tok, *ch;

  tok = strtok_r (lpcValue, " ;\0\n", &ch);

  while (tok)
  {
    if (strncasecmp (tok, "header", 6) == 0)
    {
      liPrivacy |= INGwSpSipProviderConfig::SIP_PRIVACY_HEADER;
    }
    else if (strncasecmp (tok, "session", 6) == 0)
    {
      liPrivacy |= INGwSpSipProviderConfig::SIP_PRIVACY_SESSION;
    }
    else if (strncasecmp (tok, "user", 6) == 0)
    {
      liPrivacy |= INGwSpSipProviderConfig::SIP_PRIVACY_USER;
    }
    else if (strncasecmp (tok, "none", 6) == 0)
    {
      liPrivacy |= INGwSpSipProviderConfig::SIP_PRIVACY_NONE;
    }
    else if (strncasecmp (tok, "id", 6) == 0)
    {
      liPrivacy |= INGwSpSipProviderConfig::SIP_PRIVACY_ID;
    }
    else if (strncasecmp (tok, "critical", 6) == 0)
    {
      liPrivacy |= INGwSpSipProviderConfig::SIP_PRIVACY_CRITICAL;
    }
    else 
    {
      logger.logINGwMsg (false, ERROR_FLAG, 0,
        "Unknown token <%s> received in Privacy Header<%s>",
        tok, apHdr);
      delete [] lpcValue;
      return Sdf_co_fail;
    }

    tok = strtok_r (0, " ;\0\n", &ch);
  }

  logger.logINGwMsg (false, VERBOSE_FLAG, 0,
    "The Privacy mask in the received invite is <%X>",
    liPrivacy);

  //apConn->setPrivacyMask (liPrivacy);

  delete [] lpcValue;

  LogINGwTrace(false, 0, "OUT processPrivacyHeader");
  return Sdf_co_success;
}

///////////////////////////////////////////////
//
// processPAIHeader
//
///////////////////////////////////////////////
Sdf_ty_retVal
INGwSpSipUtil::processPAIHeader (const char*       apHdr,
                             INGwSpSipConnection* apConn, 
                             int              &aiParamMask)
{
  LogINGwTrace(false, 0, "IN processPAIHeader");
  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "INGwSpSipUtil::processPAIHeader: hdr <%s>", apHdr);

  const char * telPtr = NULL;
  const char * sipPtr = NULL;

  if((telPtr = strstr(apHdr, "tel:")) != NULL) {
    strcpy(apConn->paiTelUri, apHdr);
  }
  else if((sipPtr = strstr(apHdr, "sip:")) != NULL) {
    strcpy(apConn->paiSipUri, apHdr);
  }
  else {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                    "INGwSpSipUtil::processPAIHeader: Neither tel nor sip uri present in hdr <%s>", 
                    apHdr);
  }

  string lstrValue (apHdr);
  string lstrDisplayName;
  string lstrUser;

  INGwSpAddress &origAddrStr =
             apConn->getAddress(INGwSpSipConnection::ORIGINATING_ADDRESS);

  //extract the display name first
  
  //check if display name is present or not
  int pos1 = lstrValue.find ('<');
  if (pos1 > 0)
  {
    if (aiParamMask & sipParamDName != sipParamDName)
    {
      lstrDisplayName = lstrValue.substr (0, pos1);
      int beginPos1 = lstrDisplayName.find ('"');
      if (beginPos1 > -1)
      {
        int beginPos2 = lstrDisplayName.rfind ('"');
        if (beginPos2 > beginPos1)
        {
          lstrDisplayName = lstrDisplayName.substr (beginPos1+1, beginPos2 - beginPos1 - 1);
        }
        else
        {
          logger.logINGwMsg (false, ERROR_FLAG, 0,
            "ERROR Parsing PAI Header <%s>", apHdr);
          return Sdf_co_fail;
        }
      }

    }

    int pos2 = lstrValue.rfind ('>');
    if (pos2 >  pos1)
      lstrUser = lstrValue.substr (pos1+1, pos2 - pos1 - 1);
    else
    {
      logger.logINGwMsg (false, ERROR_FLAG, 0,
        "ERROR Parsing PAI Header <%s>", apHdr);
      return Sdf_co_fail;
    }
  }
  else
    lstrUser = lstrValue;

  //Now extract the user name
  bool isTel = false;
  if (strncasecmp (lstrUser.c_str(), "tel:", 4) == 0)
  {
    isTel = true;
  }

  int colonPos = lstrUser.find (':');
  if (colonPos < 0)
  {
    logger.logINGwMsg (false, ERROR_FLAG, 0,
      "ERROR Parsing PAI Header <%s>", apHdr);
    return Sdf_co_fail;
  }
 
  int paramPos = lstrUser.find (';');
  string lstrParam;

  if (paramPos > -1)
  {
    lstrParam = lstrUser.substr (paramPos + 1, lstrUser.length() - paramPos);
    lstrUser = lstrUser.substr (colonPos + 1, paramPos - colonPos - 1);

    //BPInd16640 :This fix was made to handle parameter in sip uri
    int atPos = lstrUser.find ('@');
    // We need to find only user part
    if(atPos > -1)
    {
      lstrUser = lstrUser.substr (0, atPos);
    }
  }
  else
  {
    int atPos = lstrUser.find ('@');
    if(atPos > -1)
    {
      lstrUser = lstrUser.substr (colonPos + 1, atPos - colonPos - 1);
    }
  }

  // 4.8.0->4.9.0 Migration
  //CGPN Is set from the TEL-PAI Header only
  // if (isTel)  // CGPN is set from TEL aswell as SIP URIs.  // Mriganka
  {
    aiParamMask |= sipParamUser;
    origAddrStr.setAddress (lstrUser.c_str());

    //set the display name in INGwSpAddress
    origAddrStr.setDisplayName (lstrDisplayName.c_str());
    aiParamMask |= sipParamDName;
  }

  //now extract the parameters like cpc, bgid etc
  if (!lstrParam.empty())
  {
    int prevPos = 0;
    int semiPos = lstrParam.find (';');
    string lstrName, lstrNameValue;
    do 
    {
      int equalPos = lstrParam.find ('=', prevPos);

      lstrName = lstrParam.substr (prevPos, equalPos - prevPos);
      if (semiPos > 0) 
        lstrNameValue = lstrParam.substr(equalPos + 1, semiPos - equalPos -1);
      else
        lstrNameValue = lstrParam.substr(equalPos + 1, lstrParam.length() - equalPos);

      logger.logINGwMsg (false, VERBOSE_FLAG, 0,
        "processPAIHeader : param <%s> value <%s>",
        lstrName.c_str(), lstrNameValue.c_str());

      if ((aiParamMask & sipParamBGT) != sipParamBGT && 
           strcasecmp (lstrName.c_str(), BP_BGT_TAG) == 0)
      {
        aiParamMask |= sipParamBGT ;
        if(lstrNameValue == BP_BGT_PRIVATE_VAL) {
          //apConn->setChargeNumber(PRIVATE_BGT);
        }
        else if(lstrNameValue == BP_BGT_PUBLIC_VAL) {
          //apConn->setChargeNumber(PUBLIC_BGT);
        }
        else {
          //apConn->setChargeNumber(UNKNOWN_BGT);
        }

      }
      else if (INGwSpSipProviderConfig::m_bgidProcessingFlag &&
               (aiParamMask & sipParamBGID) != sipParamBGID && 
               strcasecmp (lstrName.c_str(), BP_BGID_TAG) == 0)
      {
        aiParamMask |= sipParamBGID ;
        origAddrStr.setBGId(lstrNameValue.c_str());
   
        if(INGwSpSipProviderConfig::m_bgidAppendingFlag && isTel)
        {
          char l_bgidAppendedAddress[SIZE_OF_ADDR];
          snprintf(l_bgidAppendedAddress, SIZE_OF_ADDR, "%s_%s", 
            lstrNameValue.c_str(), lstrUser.c_str());
          origAddrStr.setAddress(l_bgidAppendedAddress);
        }
      }
      else if ((aiParamMask & sipParamCPC) != sipParamCPC && 
                strcasecmp (lstrName.c_str(), BP_CPC_TAG) == 0)
      {
        aiParamMask |= sipParamCPC ;
        if (strcasecmp (lstrNameValue.c_str(), OLI_CPC_PAYPHONE_TAG) == 0)
        {
          //apConn->setOLI (OLI_CPC_PAYPHONE);
          logger.logINGwMsg (false, VERBOSE_FLAG, 0,
            "cpc=payphone set in the incoming PAI header");
        }
        else
        {
          //apConn->setOLI (OLI_CPC_OTHER);
          logger.logINGwMsg (false, VERBOSE_FLAG, 0,
            "cpc=other set in the incoming PAI header");
        }
      }
      //OLI is set only if CPC is not present
      else if ((aiParamMask & sipParamCPC) != sipParamCPC &&
               (aiParamMask & sipParamOLI) != sipParamOLI &&
               strcasecmp (lstrName.c_str(), BP_OLI_TAG) == 0)
      {
        aiParamMask |= sipParamOLI ;
        //apConn->setOLI (atoi (lstrNameValue.c_str()));
      }
      else if ((aiParamMask & sipParamCPC) != sipParamCPC && 
               (aiParamMask & sipParamISUPOLI) != sipParamISUPOLI && 
               strcasecmp (lstrName.c_str(), BP_OLI_TAG_1) == 0)
      {
        aiParamMask |= sipParamISUPOLI ;
        //apConn->setOLI (atoi (lstrNameValue.c_str()));
      }
      else if ((aiParamMask & sipParamLRN) != sipParamLRN && 
               strcasecmp (lstrName.c_str(), BP_LRN_TAG) == 0)
      {
        aiParamMask |= sipParamLRN ;
        //apConn->setLRN (lstrNameValue.c_str());
      }
      else if ((aiParamMask & sipParamCIC) != sipParamCIC && 
               strcasecmp (lstrName.c_str(), BP_CIC_TAG) == 0)
      {
        aiParamMask |= sipParamCIC ;
        //apConn->setCIC (lstrNameValue.c_str());
      }
      else if ((aiParamMask & sipParamNPDI) != sipParamNPDI && 
               strcasecmp (lstrName.c_str(), BP_NPDI_TAG) == 0)
      {
        aiParamMask |= sipParamNPDI ;
        //apConn->setLnpQueryInd(true);
      }


      prevPos = semiPos + 1;
      if (prevPos > 0) 
        semiPos = lstrParam.find (';', prevPos);
    } while (prevPos > 0);
  }

  LogINGwTrace(false, 0, "OUT processPAIHeader");
  return Sdf_co_success;
}

///////////////////////////////////////////////
//
// processPPIHeader 
// 
///////////////////////////////////////////////
Sdf_ty_retVal
INGwSpSipUtil::processPPIHeader(SIP_S8bit*       lpHdr,
                               INGwSpSipConnection* lpConn)
{
  LogINGwTrace(false, 0, "IN processPPIHeader");
  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "INGwSpSipUtil::processPPIHeader: hdr <%s>", lpHdr);

  string l_value(lpHdr);

  LogINGwTrace(false, 0, "OUT processPPIHeader");
  return Sdf_co_fail;
}

// This method extracts the user, host and port fields from the given contact
// header, and fills in the given epinfo structure.  If the extraction fails,
// it returns an error.
bool INGwSpSipUtil::getContactInfo(SipHeader *aHdr, INGwSipEPInfo *aInfo)
{
  LogINGwTrace(false, 0, "IN getContactInfo");
  SipAddrSpec *addrspec = NULL;
  SipUrl      *psipurl  = NULL;
  char        *puser    = NULL;
  char        *phost    = NULL;
  SipBool      sipstatus= SipFail;
  unsigned short port = 0;
  SipError     siperr;

  if(aHdr->dType != SipHdrTypeContactNormal)
  {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, " INGwSpSipUtil::getContactInfo: The given header is not a contact header.  Header type is %d", aHdr->dType);
    LogINGwTrace(false, 0, "OUT getContactInfo");
    return false;
  }

  sipstatus = sip_getAddrSpecFromContactHdr(aHdr, &addrspec, &siperr);

  if(addrspec && (SipSuccess == sipstatus))
  {
    sipstatus = sip_getUrlFromAddrSpec(addrspec, &psipurl, &siperr);
    if(psipurl && (SipSuccess == sipstatus))
    {
      sip_getUserFromUrl(psipurl, &puser, &siperr);
      strncpy(aInfo->mEpNumber, puser, MAX_EP_NUMBER_LENGTH);
      aInfo->mEpNumber[MAX_EP_NUMBER_LENGTH] = 0;

      sip_getHostFromUrl(psipurl, &phost, &siperr);
      strncpy(aInfo->mEpHost, phost, MAX_EP_HOST_LENGTH);
      aInfo->mEpHost[MAX_EP_HOST_LENGTH] = 0;

      sip_getPortFromUrl(psipurl, &port , &siperr);
      aInfo->port = port;

      sip_freeSipUrl(psipurl);
    }

    sip_freeSipAddrSpec(addrspec);

    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "INGwSpSipUtil::getContactInfo: user <%s>, host <%s>, port <%d>", aInfo->mEpNumber, aInfo->mEpHost, aInfo->port);

    LogINGwTrace(false, 0, "OUT getContactInfo");
    return true;
  }

  LogINGwTrace(false, 0, "OUT getContactInfo");
  return false;
} // end of getContactInfo

///////////////////////////////////////////////
//
// getNumber
//
///////////////////////////////////////////////
string
INGwSpSipUtil::getNumber(string url) {
  string l_num = "X_NO";

    long lPos = url.find("sip:");
    long rPos = -1;

    if(-1 != lPos) {
      rPos = url.find("@");

      if(-1 == rPos || (4 >= (rPos - lPos))) {
        //l_num = "X_NO";
      }
      else {
        l_num = url.substr(lPos+4, rPos-lPos-4);
      }

    } //not sip: url scheme ...
    else {
      lPos = url.find("tel:");
      if(-1 != lPos) {

        rPos = url.find(";");
        if(-1 == rPos) {
          rPos = url.find(">");
          if(-1 == rPos) {
            rPos = url.size();
          }
        }

        if(4 >= (rPos - lPos)) {
          //l_num = "X_NO";
        }
        else {
          l_num = url.substr(lPos+4, rPos-lPos-4);
        }
      }
      else { // not sip: OR tel: scheme
        //l_num = "X_NO";
      }
    } //checked for alternate url schemes ...

  return l_num;
}

int
INGwSpSipUtil::getToHdrTag(SipHeader* toHdr, char** toTag) {

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "IN getToHdrTag");

  if(0 == toHdr) {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "Header null ...");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT getToHdrTag");
    return 0;
  }
  if(toHdr->dType != SipHdrTypeTo) {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "Header Type NOT - TO");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT getToHdrTag");
    return 0;
  }

  SipError l_error;

  if(sip_getTagAtIndexFromToHdr(toHdr, 
    toTag, 0, &l_error) == SipFail) {

    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
              "sip_getTagAtIndexFromToHdr Failed ..... ");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT getToHdrTag");
    return -1;
  }

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT getToHdrTag");
  return 0;
}

int
INGwSpSipUtil::getFromHdrTag(SipHeader* fromHdr, char** fromTag) {

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "IN getFromHdrTag");

  if(0 == fromHdr) {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "Header null ...");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT getFromHdrTag");
    return 0;
  }
  if(fromHdr->dType != SipHdrTypeFrom) {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "Header Type NOT - FROM");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT getFromHdrTag");
    return 0;
  }

  SipError l_error;

  if(sip_getTagAtIndexFromFromHdr(fromHdr, 
    fromTag, 0, &l_error) == SipFail) {

    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                  "sip_getTagAtIndexFromFromHdr Failed .....");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT getFromHdrTag");
    return -1;
  }

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT getFromHdrTag");
  return 0;
}

////////////////////////////////////////////////////////////
//
// createAndAddHeader
// Desc: from supplied buffer
//
////////////////////////////////////////////////////////////
int
INGwSpSipUtil::createAndAddHeader(Sdf_st_callObject* hssCallObj,
  Sdf_ty_transactionType txType,
  en_HeaderType hdrType,
  Sdf_ty_s8bit* pHdrStr,
  Sdf_st_error* pErr) {

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "IN createAndAddHeader");

  SipHeader*         l_pHdr     = Sdf_co_null;
  Sdf_st_headerList* l_pHdrList = Sdf_co_null; 

  //l_pHdr = (SipHeader*) sdf_memget(0,sizeof(SipHeader),Sdf_co_null);

  if(sip_initSipHeader(&l_pHdr, SipHdrTypeAny,
    (SipError*)&(pErr->errCode)) == SipFail) {

    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
         "sip_initSipHeader Failed, errCode:<%d>", pErr->errCode);
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddUnknownHeader");
    return -1;
  }

  if(sip_parseSingleHeader(pHdrStr, hdrType, 
      l_pHdr,(SipError*)&(pErr->errCode)) == SipFail) {

    sip_freeSipHeader(l_pHdr);
    //sdf_free the header itself

    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
           "sip_parseSingleHeader Failed, errCode:<%d>", pErr->errCode);
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddHeader");
    return -1;
  }

  //init header List
  if(sdf_ivk_uaInitHeaderList(&l_pHdrList, 
    hdrType, Sdf_co_null, pErr) == Sdf_co_fail) {

    sip_freeSipHeader(l_pHdr);
    //sdf_free the header itself

    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                             "sdf_ivk_uaInitHeaderList Failed ...");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddHeader");
    return -1;
  }

  //add header to header list
  if(sdf_listAppend(&(l_pHdrList->slHeaders), 
     (Sdf_ty_pvoid)l_pHdr, pErr) == Sdf_co_fail) {

    sip_freeSipHeader(l_pHdr);
    //sdf_free the header itself
    sdf_ivk_uaFreeHeaderList(l_pHdrList);

    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
           "sdf_listAppend Failed to add header to header list ...");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddHeader");
    return -1;
  }

  //Add header list to processed header list
  if(Sdf_en_uacTransaction == txType) { 
    if(sdf_listAppend(
      &hssCallObj->pUacTransaction->slProcessedHeaders,
      (Sdf_ty_pvoid)l_pHdrList, pErr) == Sdf_co_fail) {

      sip_freeSipHeader(l_pHdr);
      //sdf_free the header itself
      sdf_ivk_uaFreeHeaderList(l_pHdrList);

      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                      "sdf_listAppend Failed for pUacTransaction...");
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddHeader");
      return -1;
    }
  }
  else if(Sdf_en_uasTransaction == txType) {
    if(sdf_listAppend(
      &hssCallObj->pUasTransaction->slProcessedHeaders,
      (Sdf_ty_pvoid)l_pHdrList, pErr) == Sdf_co_fail) {

      sip_freeSipHeader(l_pHdr);
      //sdf_free the header itself
      sdf_ivk_uaFreeHeaderList(l_pHdrList);

      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                      "sdf_listAppend Failed for pUasTransaction...");
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddHeader");
      return -1;
    }
  }
  else
  {
    // This is with the overlap transaction.
    // Get the overlap transaction and then append as before.
    Sdf_st_overlapTransInfo *pOverlapTransInfo =
       INGwSpSipUtil::getLastOverlapTransInfo(hssCallObj);
    if(!pOverlapTransInfo)
    {
      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "createAndAddHeader: Error getting overlap transinfo from callobject");
      sip_freeSipHeader(l_pHdr);
      //sdf_free the header itself
      sdf_ivk_uaFreeHeaderList(l_pHdrList);

      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddHeader");
      return -1;
    }

    if(sdf_listAppend(
      &(pOverlapTransInfo->slProcessedHeaders),
      (Sdf_ty_pvoid)l_pHdrList, pErr) == Sdf_co_fail) {

      sip_freeSipHeader(l_pHdr);
      //sdf_free the header itself
      sdf_ivk_uaFreeHeaderList(l_pHdrList);

      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
                      "sdf_listAppend Failed for pOverlapTransaction...");
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddHeader");
      return -1;
    }
  }

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddHeader"); 
  return 0; 
}


////////////////////////////////////////////////////////////
//
// createAndAddHeader
// Desc: from supplied header
//
////////////////////////////////////////////////////////////
int
INGwSpSipUtil::createAndAddHeader(Sdf_st_callObject* hssCallObj,
  Sdf_ty_transactionType txType,
  en_HeaderType hdrType,
  SipHeader* sipHeader,
  Sdf_st_error* pErr) {

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "IN createAndAddHeader");

  if(SipHdrTypeReferTo != hdrType) {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                      "Header type is not SipHdrTypeReferTo ... ");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddHeader");
    return -1;
  }

  if(SipHdrTypeFrom != sipHeader->dType) {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                      "source Header type is not SipHdrTypeFrom ... ");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddHeader");
    return -1;
  }

  SipHeader*         l_pHdr     = Sdf_co_null;
  Sdf_st_headerList* l_pHdrList = Sdf_co_null; 


  SipAddrSpec* l_addrSpecIn = 0;
  SipBool      sipstatus= SipFail; 
  SipError     l_sipErr;
  sipstatus = sip_getAddrSpecFromFromHdr(
                  sipHeader, &l_addrSpecIn, &l_sipErr);
  if((0 == l_addrSpecIn) || (SipSuccess != sipstatus)) {

    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                      "sip_getAddrSpecFromFromHdr Failed.....");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddHeader");
    return -1; 
  }

  if(sip_initSipHeader(&l_pHdr, SipHdrTypeAny,
    (SipError*)&(pErr->errCode)) == SipFail) {

    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
         "sip_initSipHeader Failed, errCode:<%d>", pErr->errCode);
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddUnknownHeader");
    return -1;
  }

  if(sip_parseSingleHeader((char*)"Refer-To: 1", hdrType, 
      l_pHdr,(SipError*)&(pErr->errCode)) == SipFail) {

    sip_freeSipHeader(l_pHdr);
    //sdf_free the header itself
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                      "sip_parseSingleHeader Failed");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddHeader");
    return -1;
  }

  sipstatus = sip_setAddrSpecInReferToHdr(
                  l_pHdr, l_addrSpecIn, &l_sipErr);

  sip_freeSipAddrSpec(l_addrSpecIn);

  if(SipFail == sipstatus) {
    sip_freeSipHeader(l_pHdr);
    //sdf_free the header itself
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                      "sip_setAddrSpecInReferToHdr Failed .... ");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddHeader");
    return -1;
  }

  //init header List
  if(sdf_ivk_uaInitHeaderList(&l_pHdrList, 
    hdrType, Sdf_co_null, pErr) == Sdf_co_fail) {

    sip_freeSipHeader(l_pHdr);
    //sdf_free the header itself

    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                      "sdf_ivk_uaInitHeaderList Failed ....");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddHeader");
    return -1;
  }

  //add header to header list
  if(sdf_listAppend(&(l_pHdrList->slHeaders), 
     (Sdf_ty_pvoid)l_pHdr, pErr) == Sdf_co_fail) {

    sip_freeSipHeader(l_pHdr);
    //sdf_free the header itself
    sdf_ivk_uaFreeHeaderList(l_pHdrList);

    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                      "sdf_listAppend Failed ...");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddHeader");

    return -1;
  }

  //Add header list to processed header list
  if(Sdf_en_uacTransaction == txType) { 
    if(sdf_listAppend(
      &hssCallObj->pUacTransaction->slProcessedHeaders,
      (Sdf_ty_pvoid)l_pHdrList, pErr) == Sdf_co_fail) {

      sip_freeSipHeader(l_pHdr);
      //sdf_free the header itself
      sdf_ivk_uaFreeHeaderList(l_pHdrList);

      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                      "sdf_listAppend Failed for pUacTransaction ...");
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddHeader");

      return -1;
    }
  }
  else if(Sdf_en_uasTransaction == txType) {
    if(sdf_listAppend(
      &hssCallObj->pUasTransaction->slProcessedHeaders,
      (Sdf_ty_pvoid)l_pHdrList, pErr) == Sdf_co_fail) {

      sip_freeSipHeader(l_pHdr);
      //sdf_free the header itself
      sdf_ivk_uaFreeHeaderList(l_pHdrList);

      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                      "sdf_listAppend Failed for pUasTransaction ...");
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddHeader");

      return -1;
    }
  }
  else {
    // This is with the overlap transaction.
    // Get the overlap transaction and then append as before.
    Sdf_st_overlapTransInfo *pOverlapTransInfo =
       INGwSpSipUtil::getLastOverlapTransInfo(hssCallObj);
    if(!pOverlapTransInfo) {
      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
        "createAndAddUnknownHeader: Error getting overlap transinfo from callobject");
      sip_freeSipHeader(l_pHdr);
      //sdf_free the header itself
      sdf_ivk_uaFreeHeaderList(l_pHdrList);
      //fast_memfree(0, l_nameDup, (SipError*)&(pErr->errCode));
      //fast_memfree(0, l_bodyDup, (SipError*)&(pErr->errCode));
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddHeader");
      return -1;
    }

    if(sdf_listAppend(
    &(pOverlapTransInfo->slProcessedHeaders),
    (Sdf_ty_pvoid)l_pHdrList, pErr) == Sdf_co_fail) {

      sip_freeSipHeader(l_pHdr);
      //sdf_free the header itself
      sdf_ivk_uaFreeHeaderList(l_pHdrList);

      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
                      "sdf_listAppend Failed for pOverlapTransaction...");
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddUnknownHeader");
      return -1;
    }
  } 
 
  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddHeader");
  return 0; 
}

////////////////////////////////////////////////////////////
//
// createAndAddUnknownHeader
// Desc: 
//
////////////////////////////////////////////////////////////
int
INGwSpSipUtil::createAndAddUnknownHeader(Sdf_st_callObject* hssCallObject,
  Sdf_ty_transactionType txType,
  Sdf_ty_s8bit* pHdrStr,
  Sdf_ty_s8bit* pHdrName,
  Sdf_st_error* pErr) {

    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "IN createAndAddUnknownHeader");

    //create known header.................................
    SipHeader*         l_pHdr     = Sdf_co_null;
    Sdf_st_headerList* l_pHdrList = Sdf_co_null; 

    if(sip_initSipHeader(&l_pHdr, SipHdrTypeUnknown, 
      (SipError*)&(pErr->errCode)) == SipFail) {

      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
           "sip_initSipHeader Failed, errCode:<%d>", pErr->errCode);
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddUnknownHeader");
      return -1;
    }
     
    //set Name ................................................
    char* l_nameDup = (char*) fast_memget(0,
                   strlen(pHdrName)+1, (SipError*)&(pErr->errCode));
    strcpy(l_nameDup, pHdrName);

    if(sip_setNameInUnknownHdr(l_pHdr, l_nameDup, 
      (SipError*)&(pErr->errCode)) == SipFail) {

      sip_freeSipHeader(l_pHdr);
      //sdf_free the header itself
      //fast_memfree(0, l_nameDup, (SipError*)&(pErr->errCode));
      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
           "sip_setNameInUnknownHdr Failed, errCode:<%d>", pErr->errCode);
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddUnknownHeader");
      return -1;
    }

    //set Body ................................................
    char* l_bodyDup = (char*) fast_memget(0,
                   strlen(pHdrStr)+1, (SipError*)&(pErr->errCode));
    strcpy(l_bodyDup, pHdrStr);
    if(sip_setBodyInUnknownHdr(l_pHdr, l_bodyDup, 
      (SipError*)&(pErr->errCode)) == SipFail) {

      sip_freeSipHeader(l_pHdr);
      //sdf_free the header itself
      //fast_memfree(0, l_nameDup, (SipError*)&(pErr->errCode));
      //fast_memfree(0, l_bodyDup, (SipError*)&(pErr->errCode));

      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
           "sip_setBodyInUnknownHdr Failed, errCode:<%d>", pErr->errCode);
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddUnknownHeader");
      return -1;
    }

    //init header List..........................................
    if(sdf_ivk_uaInitHeaderList(&l_pHdrList,
      SipHdrTypeUnknown, Sdf_co_null, pErr) == Sdf_co_fail) {

      sip_freeSipHeader(l_pHdr);
      //sdf_free the header itself
      //fast_memfree(0, l_nameDup, (SipError*)&(pErr->errCode));
      //fast_memfree(0, l_bodyDup, (SipError*)&(pErr->errCode));

      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                             "sdf_ivk_uaInitHeaderList Failed ...");
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddUnknownHeader");
      return -1;
    }

    //add header to header list..................................
    if(sdf_listAppend(&(l_pHdrList->slHeaders), 
       (Sdf_ty_pvoid)l_pHdr, pErr) == Sdf_co_fail) {

      sip_freeSipHeader(l_pHdr);
      //sdf_free the header itself
      sdf_ivk_uaFreeHeaderList(l_pHdrList);
      //fast_memfree(0, l_nameDup, (SipError*)&(pErr->errCode));
      //fast_memfree(0, l_bodyDup, (SipError*)&(pErr->errCode));

      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
           "sdf_listAppend Failed to add header to header list ...");
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddHeader");
      return -1;
    }

    //Add header list to processed header list
    if(Sdf_en_uacTransaction == txType) { 
      if(sdf_listAppend(
        &hssCallObject->pUacTransaction->slProcessedHeaders,
        (Sdf_ty_pvoid)l_pHdrList, pErr) == Sdf_co_fail) {

        sip_freeSipHeader(l_pHdr);
        //sdf_free the header itself
        sdf_ivk_uaFreeHeaderList(l_pHdrList);
        //fast_memfree(0, l_nameDup, (SipError*)&(pErr->errCode));
        //fast_memfree(0, l_bodyDup, (SipError*)&(pErr->errCode));

        logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                      "sdf_listAppend Failed for pUacTransaction...");
        logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddHeader");
        return -1;
      }
    }
    else if(Sdf_en_uasTransaction == txType) {
      if(sdf_listAppend(
        &hssCallObject->pUasTransaction->slProcessedHeaders,
        (Sdf_ty_pvoid)l_pHdrList, pErr) == Sdf_co_fail) {

        sip_freeSipHeader(l_pHdr);
        //sdf_free the header itself
        sdf_ivk_uaFreeHeaderList(l_pHdrList);
        //fast_memfree(0, l_nameDup, (SipError*)&(pErr->errCode));
        //fast_memfree(0, l_bodyDup, (SipError*)&(pErr->errCode));

        logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                      "sdf_listAppend Failed for pUasTransaction...");
        logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddHeader");
        return -1;
      }
    }
    else
    {
      // This is with the overlap transaction.
      // Get the overlap transaction and then append as before.
      Sdf_st_overlapTransInfo *pOverlapTransInfo =
         INGwSpSipUtil::getLastOverlapTransInfo(hssCallObject);
      if(!pOverlapTransInfo)
      {
        logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "createAndAddUnknownHeader: Error getting overlap transinfo from callobject");
        sip_freeSipHeader(l_pHdr);
        //sdf_free the header itself
        sdf_ivk_uaFreeHeaderList(l_pHdrList);
        //fast_memfree(0, l_nameDup, (SipError*)&(pErr->errCode));
        //fast_memfree(0, l_bodyDup, (SipError*)&(pErr->errCode));
        logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddHeader");
        return -1;
      }

      if(sdf_listAppend(
      &(pOverlapTransInfo->slProcessedHeaders),
      (Sdf_ty_pvoid)l_pHdrList, pErr) == Sdf_co_fail) {

        sip_freeSipHeader(l_pHdr);
        //sdf_free the header itself
        sdf_ivk_uaFreeHeaderList(l_pHdrList);

        logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
                        "sdf_listAppend Failed for pOverlapTransaction...");
        logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddUnknownHeader");
        return -1;
      }
    }

    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddUnknownHeader");
    return 0;
  }

///////////////////////////////////////////////////////////
//
// jnk code - use for C&P in future.
//
///////////////////////////////////////////////////////////

#if 0

//- Not to be deleted
  //Initialise SipList.....................................
  SipList l_hdrList;
  if(sip_listInit(&l_hdrList, wrapper_sip_freeSipHdr,
    (SipError*)&(pErr->errCode)) == SipFail) {

    //free l_sipHdr
    sip_freeSipHeader(l_pHdr);
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
           "sip_listInit Failed, errCode:<%d>", pErr->errCode);
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddUnknownHeader");
    return -1;
  }

  //Put known header in SipList............................

  //Initialise header with Unknown Type
  

  //parse into unknown header .............................
  if(sip_formUnknownHeader(&l_hdrList, name,
    (SipError*)&(pErr->errCode)) == SipFail) {

    //free l_sipHdr
    sip_freeSipHeader(l_pHdr);

    //free l_hdrList
    sip_listDeleteAll(&l_hdrList, (SipError*)&(pErr->errCode));

    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
           "sip_parseUnknownHeader Failed, errCode:<%d>", pErr->errCode);
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT createAndAddUnknownHeader");
    return -1;
  }
  

#endif

////////////////////////////////////////////////////////////
//
// getINGwSipConnFromHssCall
//
////////////////////////////////////////////////////////////

INGwSpSipConnection*
INGwSpSipUtil::getINGwSipConnFromHssCall(
  Sdf_st_callObject* hssCallObj) {

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "IN getINGwSipConnFromHssCall");

  if(0 == hssCallObj) {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "hssCallObj passed is Null");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT getINGwSipConnFromHssCall");
    return 0;
  }

  Sdf_st_appData* l_pAppSpecificData = Sdf_co_null;
  INGwSpSipConnection* l_bpSipConnection = 0;
  Sdf_st_error pErr;

  if(sdf_ivk_uaGetAppDataFromCallObject(hssCallObj,
    &l_pAppSpecificData, &pErr) != Sdf_co_success) {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                    "Error getting app data fm call obj");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT getINGwSipConnFromHssCall");
    return 0;
  }

  INGwConnectionContext* l_connectionContext =
                      (INGwConnectionContext*)(l_pAppSpecificData->pData);

  if(0 == l_connectionContext) {
    sdf_ivk_uaFreeAppData(l_pAppSpecificData);
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
               "INGwConnectionContext from App Data is Null ...");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT getINGwSipConnFromHssCall");
    return 0;
  }

  sdf_ivk_uaFreeAppData(l_pAppSpecificData);
  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT getINGwSipConnFromHssCall");
  return l_connectionContext->mSipConnection;
}


////////////////////////////////////////////////////////////
//
// isSessionRefreshReinvite
//
////////////////////////////////////////////////////////////
bool
INGwSpSipUtil::isSessionRefreshReinvite(INGwSpData* newSipData,
  INGwSpData* baseSipData,
  Sdf_st_callObject* pCallObj) {

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "IN isSessionRefreshReinvite");

  bool l_retVal = false;

  switch(INGwSpSipProviderConfig::m_checkSessionRefreshReinvite) {

    case INGwSpSipProviderConfig::CHECK_SE_HDR : {
        logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                            "INGwSpSipProviderConfig::CHECK_SE_HDR");

        l_retVal = 
            INGwSpSipUtil::isSessionRefreshReinviteSE(pCallObj);

        if(0 == newSipData  || 0 == newSipData->getBody() || 
           0 == baseSipData || 0 == baseSipData->getBody()) {
          logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                  "New OR Base SDP is null, returning false");
          l_retVal = false;
        }
      }
      break; 

    case INGwSpSipProviderConfig::CHECK_SDP : {
        logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                            "INGwSpSipProviderConfig::CHECK_SDP");

        l_retVal = 
            INGwSpSipUtil::isSessionRefreshReinviteSDP(newSipData, 
                                                   baseSipData);
      }
      break;

    case INGwSpSipProviderConfig::CHECK_SDP_SE_HDR : {
        logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                            "INGwSpSipProviderConfig::CHECK_SDP_SE_HDR");

        if(INGwSpSipUtil::isSessionRefreshReinviteSE(pCallObj)) {
          l_retVal = isSessionRefreshReinviteSDP(newSipData, 
                                                   baseSipData);
        }
      }
      break;

    default:
      break;
  };

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT isSessionRefreshReinvite");
  return l_retVal;
}


////////////////////////////////////////////////////////////
//
// isSessionRefreshReinviteSE
//
////////////////////////////////////////////////////////////
bool
INGwSpSipUtil::isSessionRefreshReinviteSE(Sdf_st_callObject* pCallObj) {
  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "IN isSessionRefreshReinviteSE");

  if(0 == 
    pCallObj->pUasTransaction->pSipMsg->pGeneralHdr->pSessionExpiresHdr) {
  
     logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "returning false");
     logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT isSessionRefreshReinviteSE");
     return false;
  }
 
  //SipError l_sipErr;
  //SipHeader dHeader;

  //if(sip_getHeader(pCallObj->pUasTransaction->pSipMsg,
  //  SipHdrTypeSessionExpires, &dHeader, &l_sipErr)==SipFail) {

  //  if(l_sipErr == E_NO_EXIST) {
  //    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "E_NO_EXIST");
  //  }

  //  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "returning false");
  //  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT isSessionRefreshReinviteSE");
  //  return false;
  //}

   
  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "returning true");
  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT isSessionRefreshReinviteSE");
  return true;
}


////////////////////////////////////////////////////////////
//
// isSessionRefreshReinviteSDP
//
////////////////////////////////////////////////////////////
bool
INGwSpSipUtil::isSessionRefreshReinviteSDP(INGwSpData* newSipData, 
  INGwSpData* baseSipData) {

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "IN isSessionRefreshReinviteSDP");

  if(0 == newSipData || 0 == newSipData->getBody()) {

    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "newSipData is NULL ...");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT isSessionRefreshReinviteSDP");
    return false;
  }

  if(0 == baseSipData || 0 == baseSipData->getBody()) {

    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "baseSipData is NULL ...");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT isSessionRefreshReinviteSDP");
    return false;
  }

  string l_newSdp((char*) newSipData->getBody());
  string l_baseSdp((char*) baseSipData->getBody());

  //logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, 
  //  "NewSdp:<%s> BaseSdp:<%s>", l_newSdp.c_str(), l_baseSdp.c_str());

  int l_newO  = l_newSdp.find("o=");
  int l_baseO = l_baseSdp.find("o=");

  if(-1 == l_newO || -1 == l_baseO) {

    logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE,
                          "-1 == l_newO || -1 == l_baseO");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT isSessionRefreshReinviteSDP");
    return false;
  }

  int l_newS  = l_newSdp.find("s=");
  int l_baseS = l_baseSdp.find("s=");

  if(-1 == l_newS || -1 == l_baseS) {

    logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE,
                        "-1 == l_newS || -1 == l_baseS");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT isSessionRefreshReinviteSDP");
    return false;
  }

  string l_newSdpOStr  = l_newSdp.substr(l_newO, l_newS - l_newO);
  string l_baseSdpOStr = l_baseSdp.substr(l_baseO, l_baseS - l_baseO);

  logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, 
   "NewOStr:<%s> BaseOStr:<%s>", l_newSdpOStr.c_str(), l_baseSdpOStr.c_str());

  if(l_newSdpOStr == l_baseSdpOStr) {

    logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE,
            "Strings Match, returning true ...");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT isSessionRefreshReinviteSDP");
    return true;
  }
  else {

    logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE,
            "Strings Do Not Match, returning false ...");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT isSessionRefreshReinviteSDP");
    return false;
  }

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT isSessionRefreshReinviteSDP");
  return true;
}

Sdf_st_overlapTransInfo* INGwSpSipUtil::getLastOverlapTransInfo
  (Sdf_st_callObject* aCallObj)
{
  MARK_BLOCK("INGwSpSipUtil::getLastOverlapTransInfo", TRACE_FLAG);
  Sdf_st_overlapTransInfo* retval = Sdf_co_null;
  Sdf_st_error sdferror;

  unsigned int size = 0;
  Sdf_ty_retVal status =
    sdf_listSizeOf(&(aCallObj->slOverlapTransInfo), &size, &sdferror);

  if((status != Sdf_co_success) || (!size))
  {
    logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, "INGwSpSipUtil::getLastOverlapTransInfo: Error fetching size of overlaptransinfo list");
    if(status != Sdf_co_success)
      checkError(status, sdferror);
  }
  else
  {
    status = sdf_listGetAt(&(aCallObj->slOverlapTransInfo),
                           size - 1, (void **)(&retval),
                           &sdferror);
 
    if(status != Sdf_co_success)
    {
      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "INGwSpSipUtil::getLastOverlapTransInfo: Error retrieving overlapTransinfo from the list");
      checkError(status, sdferror);
    }
    else
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "INGwSpSipUtil::getLastOverlapTransInfo: Successfully retrieved last overlaptransinfo from the list");
  }

  return retval;
} // end of getLastOverlapTransInfo

////////////////////////////////////////////////////////////
//
// isSupported
// Desc: 
//
////////////////////////////////////////////////////////////
bool 
INGwSpSipUtil::isSupported(
  const char* option,
  SipMessage* sipMsg) {

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "IN isSupported");
  bool l_retVal = false;

  if(0 == option || 0 == sipMsg) {

    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT isSupported");
    return l_retVal;
  }

  SipError  l_sipErr;
  SIP_U32bit l_count = 0;

  SipBool l_status = sip_getHeaderCount(sipMsg, SipHdrTypeSupported,
                                      &l_count, &l_sipErr);

  if(!l_status || 0 == l_count) {
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "cnt:<%d>", l_count);
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT isSupported");
    return l_retVal;
  }
  else {
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "cnt:<%d>", l_count);
  }

  //get supported header from sipMsg
  for(SIP_U32bit l_index = 0; l_index < l_count; ++l_index) {

    SipHeader l_hdr;
    l_status = sip_getHeaderAtIndex(sipMsg, SipHdrTypeSupported,
                                &l_hdr, l_index, &l_sipErr);
    if(l_status) {
      char* l_option = 0;
      l_status = 
        sip_getOptionFromSupportedHdr(&l_hdr, &l_option, &l_sipErr); 

      if(l_status) {
        if(0 == strcmp(l_option, option)) {
          logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, 
                 "option:<%s> found, returning true", option);
          l_retVal = true;
          //free local refrence. 
          sip_freeSipHeader(&l_hdr);
          break;
        }
      }
      else {
        //free local refrence. 
        sip_freeSipHeader(&l_hdr);
        //logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE,"option:<%s>", l_option);
      }
    }
    else {
      //free local refrence. 
      sip_freeSipHeader(&l_hdr);
    }
  }//for

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT isSupported");
  return l_retVal;
}

////////////////////////////////////////////////////////////
//
// modifySupportedHdrs
// Desc: 
//
////////////////////////////////////////////////////////////
bool
INGwSpSipUtil::addSupportedHdr(
  SIP_S8bit* hdrString,
  Sdf_st_callObject* hssCallObj) {

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "IN addSupportedHdr");

  Sdf_st_error l_err;

  //Supported: 100rel
  //SIP_S8bit* l_hdrStr = (SIP_S8bit*)
  //   sdf_memget(0, strlen("Supported: 100rel")+1, &l_err);
  //strcpy(l_hdrStr, "Supported: 100rel");

  INGwSpSipUtil::createAndAddHeader(hssCallObj,
    Sdf_en_uacTransaction, SipHdrTypeSupported,
    hdrString, &l_err);

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT addSupportedHdr");
  return true;
}

void INGwSpSipUtil::processRemotePartyID(INGwSpSipConnection *conn, INGwSpAddress &addr,
                                     SipHeader hdrWrap)
{
   SipDcsRemotePartyIdHeader *hdr = 
                                   (SipDcsRemotePartyIdHeader *)hdrWrap.pHeader;
   
   const char *origAddr = DEFAULT_ADDRESS;
   const char *bgid = NULL;

   if(hdr->pAddrSpec == NULL)
   {
      logger.logMsg(ERROR_FLAG, 0, "No AddrSpec in RemotePartyID");
      addr.setAddress(origAddr);
      conn->setGwIPAddress("");
      conn->setGwPort(5060);
      return;
   }

   SipUrl *fromUrl = NULL;

   SipError error;

   if(sip_getUrlFromAddrSpec(hdr->pAddrSpec, &fromUrl, &error) == SipFail)
   {
      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                      "Error getting sip URI from Remote-party-id.");
      addr.setAddress(origAddr);
      conn->setGwIPAddress("");
      conn->setGwPort(5060);
      return;
   }

   sip_freeSipUrl(fromUrl); //Addr spec is already holding the ref.

   if(fromUrl->pHost)
   {
      conn->setGwIPAddress(fromUrl->pHost);
   }
   else
   {
      conn->setGwIPAddress("");
   }

   if(fromUrl->dPort)
   {
      conn->setGwPort(*(fromUrl->dPort));
   }
   else
   {
      conn->setGwPort(5060);
   }

   if(fromUrl->pUser)
   {
      origAddr = fromUrl->pUser;
   }

   SipList &paramList = fromUrl->slParam;

   bool l_bgtPresent = false;
   bool isCPC = false;
   bool isIsupOli = false;
	 
   for(SipListElement *curr = paramList.head; curr != SIP_NULL; 
       curr=curr->next)
   {
      SipParam *currParam = (SipParam *)(curr->pData);

      const char *paramName = currParam->pName;
      const char *value = NULL;

      if(currParam->slValue.size > 0)
      {
         value = (const char *)currParam->slValue.head->pData;
      }

      if(Sdf_mc_strcmp(BP_BGT_TAG, paramName) == 0) 
      {
         l_bgtPresent = true;

         if(value)
         {
            if(Sdf_mc_strcmp(BP_BGT_PRIVATE_VAL, value) == 0) 
            {
               //conn->setChargeNumber(PRIVATE_BGT);
            }
            else if(Sdf_mc_strcmp(BP_BGT_PUBLIC_VAL, value) == 0) 
            {
               //conn->setChargeNumber(PUBLIC_BGT);
            }
            else 
            {
               //conn->setChargeNumber(UNKNOWN_BGT);
            }
         }
      }
      else if(INGwSpSipProviderConfig::m_bgidProcessingFlag && 
              (Sdf_mc_strcmp(BP_BGID_TAG, paramName) == 0)) 
      {
         if(value)
         {
            addr.setBGId(value);
            bgid = value;
         }
      }
//      else if(Sdf_mc_strcmp(BP_OLI_TAG, paramName) == 0) 
//      {
//         if(value)
//         {
//            conn->setOLI(atoi(value));
//         }
//      }
      else if(0 == Sdf_mc_strcmp(BP_CPC_TAG, paramName)) 
      {
        if(value)
        {
          if (strcasecmp (value, OLI_CPC_PAYPHONE_TAG) == 0)
          {
            //conn->setOLI (OLI_CPC_PAYPHONE);
						logger.logINGwMsg (false, TRACE_FLAG, 0, "setting OLI %d from %s", OLI_CPC_PAYPHONE, BP_CPC_TAG);
          }
          else
					{
						//conn->setOLI (OLI_CPC_OTHER);
						logger.logINGwMsg (false, TRACE_FLAG, 0, "setting OLI %d from %s", OLI_CPC_OTHER, BP_CPC_TAG);
					}
					isCPC = true;
				}
      }

      else if(!isCPC && !isIsupOli && 0 == Sdf_mc_strcmp(BP_OLI_TAG, paramName)) {
        if(value)
        {
          //conn->setOLI(atoi(value));
					logger.logINGwMsg (false, TRACE_FLAG, 0, "setting OLI %d from %s", atoi(value), BP_OLI_TAG);
        }
      }
      else if(!isCPC && 0 == Sdf_mc_strcmp(BP_OLI_TAG_1, paramName)) {
        if(value) {
          //conn->setOLI(atoi(value));
					logger.logINGwMsg (false, TRACE_FLAG, 0, "setting OLI %d from %s", atoi(value), BP_OLI_TAG_1);
					isIsupOli = true;
        }
			}
			else if(Sdf_mc_strcmp(BP_LRN_TAG, paramName) == 0) 
			{
         if(value)
         {
            //conn->setLRN(value);
         }
      }
      else if(Sdf_mc_strcmp(BP_CIC_TAG, paramName) == 0) 
      {
         if(value)
         {
            //conn->setCIC(value);
         }
      }
      else if(Sdf_mc_strcmp(BP_NPDI_TAG, paramName) == 0) 
      {
         //conn->setLnpQueryInd(true);
      }
   }

   if(!l_bgtPresent)
   {
      //conn->setChargeNumber(UNKNOWN_BGT);
   }

   if(bgid && INGwSpSipProviderConfig::m_bgidAppendingFlag)
   {
      char l_bgidAppendedAddress[SIZE_OF_ADDR + 1];
      snprintf(l_bgidAppendedAddress, SIZE_OF_ADDR, "%s_%s", bgid, origAddr);
      addr.setAddress(l_bgidAppendedAddress);
   }
   else
   {
      addr.setAddress(origAddr);
   }

	 // BpUsa07879 : [
	 // Check header params also for "np=<NUM>|<STR>"

   bool isNP = false;
	 
	 // Although it should not occur but in case we get multiple params
	 //		with OLI value the ascending order of precedence for params 
	 //		with OLI value is:
	 //
	 //		"oli=<NUM>", "isup-oli=<NUM>", "cpc=<STR>", "np=<NUM>|<STR>"
	 //

  Sdf_ty_u32bit iterator = 0;
	 SIP_U32bit hdrParamCount = 0;

	 if(sip_dcs_getParamCountFromDcsRemotePartyIdHdr(&hdrWrap,
				 &hdrParamCount, &error) == SipFail) {
		 logger.logINGwMsg (false, TRACE_FLAG, 0, "Leaving processRemotePartyID()");
		 return ;
	 }

	 if(0 >= hdrParamCount) {
		 logger.logINGwMsg (false, TRACE_FLAG, 0, "Leaving processRemotePartyID()");
		 return ;
	 }

	 // search for BP_NP_TAG ("np=")..
	 for(iterator = 0; iterator < hdrParamCount; iterator++) {
		 SipParam *currParam = 0;
		 if (sip_dcs_getParamAtIndexFromDcsRemotePartyIdHdr(&hdrWrap,
					 &currParam, iterator, &error) != SipFail) {

			 const char *paramName = currParam->pName;
			 const char *value = NULL;

			 if(currParam->slValue.size > 0)
			 {
				 value = (const char *)currParam->slValue.head->pData;
			 }

			 if( 0 == Sdf_mc_strcmp(BP_NP_TAG, paramName) ) {
				 if(value) {
					 // check if it is a number or string
					 int iVal = atoi(value);
					 if( iVal > 0) 
					 {
						 //conn->setOLI(iVal);
						 logger.logINGwMsg (false, TRACE_FLAG, 0, "setting OLI %d from %s", iVal, BP_NP_TAG);
					 }
					 else // It is a string
					 {
						 if (strcasecmp (value, OLI_CPC_PAYPHONE_TAG) == 0)
						 {
							 //conn->setOLI (OLI_CPC_PAYPHONE);
							 logger.logINGwMsg (false, TRACE_FLAG, 0, "setting OLI %d from %s", OLI_CPC_PAYPHONE, BP_NP_TAG);
						 }
						 else
						 {
							 //conn->setOLI (OLI_CPC_OTHER);
							 logger.logINGwMsg (false, TRACE_FLAG, 0, "setting OLI %d from %s", OLI_CPC_OTHER, BP_NP_TAG);
						 }
					 }

					 isNP = true;
				 }
			 }

		 }
		 else {
			 //log that a extraction failed ...
			 logger.logINGwMsg (false, TRACE_FLAG, 0, "Leaving processRemotePartyID");
			 return ;
		 }

		 sip_freeSipParam(currParam);
	 } //for loop

	 // : ]

   return;
}

void INGwSpSipUtil::initialize()
{
   // Create a new contact header from the given user, the ccm Fip and port
   string contacthost;
   string contactport;
   INGwIfrPrParamRepository::getInstance().getValue(ingwFLOATING_IP_ADDR,contacthost);
   INGwIfrPrParamRepository::getInstance().getValue(ingwSIP_STACK_LISTENER_PORT,
                                             contactport);

   ourEndPoint = contacthost;
   ourEndPoint += ":";
   ourEndPoint += contactport;

   char*   lpcHdrForm =  getenv("SIP_HDR_OPT");
    
   if(NULL != lpcHdrForm) {
     miSipHdrOpt = atoi(lpcHdrForm);

     if(1 == miSipHdrOpt){
       miSipHdrOpt = SIP_OPT_CLEN;
     }
     else if(2 == miSipHdrOpt){
       miSipHdrOpt = SIP_OPT_SHORTFORM;
     }
     else if(3 == miSipHdrOpt){
       miSipHdrOpt = (SIP_OPT_CLEN|SIP_OPT_SHORTFORM);
     }
     else{
       miSipHdrOpt = SIP_OPT_CLEN;
     }

     logger.logINGwMsg(false,ALWAYS_FLAG,0,"SIP_HDR_OPT <%d>",
                       miSipHdrOpt);
   }
   else{
     miSipHdrOpt = SIP_OPT_CLEN;
     logger.logINGwMsg(false,ALWAYS_FLAG,0,"SIP_HDR_OPT NULL <%d>",
                       miSipHdrOpt);
   }
   logger.logMsg(ALWAYS_FLAG, 0, "INGwSpSipUtil initialized. EndPoint [%s]", 
                 ourEndPoint.c_str());
}

bool INGwSpSipUtil::setContactUser(SipMessage* aSipMsg, const char *aUser)
{
  if(aUser == NULL)
  {
     aUser = "";
  }

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "IN setContactUser");
  logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, "setContactUser: specified user <%s>", aUser);

  // Remove all the existing contacts from the sip message
  SipError siperror;
  sip_deleteAllHeaderType(aSipMsg, SipHdrTypeContactNormal,   &siperror);
  sip_deleteAllHeaderType(aSipMsg, SipHdrTypeContactAny,      &siperror);
  sip_deleteAllHeaderType(aSipMsg, SipHdrTypeContactWildCard, &siperror);

  char* finalContact = new char[1025];
  sprintf(finalContact, "Contact: sip:%s@%s", aUser, ourEndPoint.c_str());

  // Insert the new contact into the message
  if(sip_insertHeaderFromStringAtIndex
     (aSipMsg, SipHdrTypeContactNormal, finalContact, 0, &siperror)
     != SipSuccess)
  {
    logger.logINGwMsg(false, WARNING_FLAG, 0, "setContactUser: Error inserting contact header into sip message");
    delete[] finalContact;
    return false;
  }
  else
    logger.logINGwMsg(false, VERBOSE_FLAG, 0, "setContactUser: Successfully inserted the contact <%s> into the sip message", finalContact);

  delete[] finalContact;

  return true;
} // end of setContactUser method

en_HeaderType forbiddenHeaderList[] =
{
  SipHdrTypeCallId,
  SipHdrTypeCseq,
  SipHdrTypeFrom,
  SipHdrTypeRecordRoute,
  SipHdrTypeTo,
  SipHdrTypeVia,
  SipHdrTypeContentLength,
  SipHdrTypeContentType,
  SipHdrTypeMaxforwards,
  SipHdrTypeRoute,
  SipHdrTypeRequire,
  SipHdrTypeRAck,
  SipHdrTypeRSeq,
  SipHdrTypeReferTo,
  SipHdrTypeAlso,
  SipHdrTypeReferredBy,
  SipHdrTypeReplaces,
  SipHdrTypeMinSE,
  SipHdrTypeSessionExpires,
};

set<en_HeaderType> forbiddenHeaderSet
  (forbiddenHeaderList, forbiddenHeaderList + sizeof(forbiddenHeaderList));
set<en_HeaderType> knownHeaderCopyList;
set<string>        unknownHeaderCopyList;

typedef vector<string> StrVector;
typedef StrVector::iterator StrVectorIt;
typedef StrVector::const_iterator StrVectorCIt;

void INGwSpSipUtil::buildHeaderCopyProfile(const char* apListOfHeaders)
{
   logger.logINGwMsg(false, TRACE_FLAG, 0, "buildHeaderCopyProfile: IN");

   if(!apListOfHeaders)
   {
      logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
                      "buildHeaderCopyProfile: list of headers is empty");
      logger.logINGwMsg(false, TRACE_FLAG, 0, "buildHeaderCopyProfile: OUT");
      return;
   }

   string hdrListStr = apListOfHeaders;
   StrVector hdrVec;
   string hdrDelimiter = ",";

   INGwAlgTokenizer(hdrListStr, hdrDelimiter, back_inserter(hdrVec));

   for(StrVectorCIt veciter = hdrVec.begin(); veciter != hdrVec.end(); 
       veciter++)
   {
      const string &currHdr = *veciter;

      if(currHdr.empty())
      {
         continue;
      }

      string hdrcol = currHdr + ":";
      en_HeaderType hdrType = SipHdrTypeUnknown;
      SipError siperror;
      sip_getTypeFromName((char*)hdrcol.c_str(), &hdrType, &siperror);

      // This header may be known, but unless its parsing is enabled, the
      // header will be stored in unknown header list.  In this case, treat
      // it as unknown header.
      if(SipHdrTypeUnknown != hdrType)
      {
				 SipHdrTypeList& lHdrsTobeDecodedList =
							 INGwSpStackConfigMgr::getInstance()->getStackDecodeHeaderTypes();

         if(lHdrsTobeDecodedList.enable[hdrType] != SipSuccess)
         {
            hdrType = SipHdrTypeUnknown;
         }
      }

      if(SipHdrTypeUnknown == hdrType)
      {
        // Store in the unknown vector list.  First convert the entire header
        // into small case, so that there will not be any case-sensitive
        // comparison problems.
        unknownHeaderCopyList.insert(currHdr);
        logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
                        "buildHeaderCopyProfile: Unknown header [%s]", 
                        currHdr.c_str());
      }
      else
      {
         // Check if the header exists in the forbidden header list.  If so,
         // we can ignore this header type for copying.  Otherwise, insert
         // header type into copyable header list.
         if(knownHeaderCopyList.find(hdrType) == knownHeaderCopyList.end())
         {
            knownHeaderCopyList.insert(hdrType);
            logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
                            "buildHeaderCopyProfile: Known header [%s]", 
                            currHdr.c_str());
         }
      }
   } 

   logger.logINGwMsg(false, TRACE_FLAG, 0, "buildHeaderCopyProfile: OUT");
} 

void INGwSpSipUtil::copyHeaders(SipMessage *aSrc, Sdf_st_callObject *hssCallObject,
                            Sdf_ty_transactionType txType)
{
   logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwSpSipUtil::copyHeaders: IN");

   SipMessage *peerSipMsg = aSrc;
   SipMessage *ourSipMsg = hssCallObject->pUacTransaction->pSipMsg;
   SipError err;

   // First iterate through the list of unprocessed headers.
   SipList &locUnknownHdrList = aSrc->pGeneralHdr->slUnknownHdr;

   for(SipListElement *curr = locUnknownHdrList.head; curr != SIP_NULL; 
       curr = curr->next)
   {
      SipUnknownHeader *currHeader = (SipUnknownHeader *)(curr->pData);

      string hdrName = currHeader->pName;

      if(unknownHeaderCopyList.find(hdrName) != unknownHeaderCopyList.end())
      {
         SipHeader hdr;
         hdr.dType = SipHdrTypeUnknown;
         hdr.pHeader = curr->pData;

         if(sip_insertHeaderAtIndex(ourSipMsg, &hdr, 0, &err) == SipFail)
         {
            logger.logINGwMsg(false, ERROR_FLAG, 0, 
                            "copyHeaders: Sip header [%s] insert failed",
                            hdrName.c_str());
         }
      }
   }

   // Now iterate through the list of processed headers.
   // The list of processed headers is not exactly what it seems.  All headers
   // known to the stack are not processed.  Only the headers for which
   // processing is enabled are processed, and only those are to be taken care
   // of here.  Right now most processed headers are forbidden for copying,
   // so the following switch has only 2 cases, one of each type (one header,
   // and the other header list).  In future if some more headers are made
   // processable (list of processable headers can be found in INGwSpStackConfigMgr
   // as m_HdrsTobeDecodedList), they will have to be added as new cases.  The
   // code after switch-case block does not have to be changed.

   for(set<en_HeaderType>::iterator iter = knownHeaderCopyList.begin();
      iter != knownHeaderCopyList.end(); iter++)
   {
      SipHeader hdr;
      hdr.pHeader = NULL;

      en_HeaderType currType = *iter;

      switch(currType)
      {
         case SipHdrTypeCallInfo:
         {
            SipList &list = peerSipMsg->pGeneralHdr->slCallInfoHdr;

            for(SipListElement *curr = list.head; curr != SIP_NULL; 
                curr=curr->next)
            {
               hdr.dType = SipHdrTypeCallInfo;
               hdr.pHeader = curr->pData;

               if(sip_insertHeaderAtIndex(ourSipMsg, &hdr, 0, &err) == SipFail)
               {
                  logger.logINGwMsg(false, ERROR_FLAG, 0, 
                                  "copyHeaders [%d] insert failed", currType);
               }
            } 

            hdr.pHeader = NULL;
         }
         break;

         case SipHdrTypeOrganization:
         {
            hdr.dType = SipHdrTypeOrganization;
            hdr.pHeader = (void *)peerSipMsg->pGeneralHdr->pOrganizationHdr;
         }
         break;

         case SipHdrTypeUserAgent:
         {
            hdr.dType = SipHdrTypeUserAgent;
            hdr.pHeader = (void *)peerSipMsg->pGeneralHdr->pUserAgentHdr;
         }
         break;

         case SipHdrTypeSubject:
         {
            if(peerSipMsg->dType == SipMessageRequest)
            {
               hdr.dType = SipHdrTypeSubject;
               hdr.pHeader = 
                       (void *)peerSipMsg->u.pRequest->pRequestHdr->pSubjectHdr;
            }
         }
         break;

         case SipHdrTypeInReplyTo:
         {
            if(peerSipMsg->dType == SipMessageRequest)
            {
               SipList &list = 
                            peerSipMsg->u.pRequest->pRequestHdr->slInReplyToHdr;

               for(SipListElement *curr = list.head; curr != SIP_NULL; 
                   curr=curr->next)
               {
                  hdr.dType = SipHdrTypeInReplyTo;
                  hdr.pHeader = curr->pData;

                  if(sip_insertHeaderAtIndex(ourSipMsg, &hdr, 0, &err) == 
                     SipFail)
                  {
                     logger.logINGwMsg(false, ERROR_FLAG, 0, 
                                     "copyHeaders [%d] insert failed", 
                                     currType);
                  }
               } 
            }

            hdr.pHeader = NULL;
         }
         break;

         case SipHdrTypeMinSE:
         {
            hdr.dType = SipHdrTypeMinSE;
            hdr.pHeader = (void *)peerSipMsg->pGeneralHdr->pMinSEHdr;
         }
         break;

         case SipHdrTypeReason:
         {
            SipList &list = peerSipMsg->pGeneralHdr->slReasonHdr;

            for(SipListElement *curr = list.head; curr != SIP_NULL;
                curr=curr->next)
            {
               hdr.dType = SipHdrTypeReason;
               hdr.pHeader = curr->pData;

               if(sip_insertHeaderAtIndex(ourSipMsg, &hdr, 0, &err) == SipFail)
               {
                  logger.logINGwMsg(false, ERROR_FLAG, 0, 
                                  "copyHeaders [%d] insert failed", currType);
               }
            }

            hdr.pHeader = NULL;
         }
         break;

         case SipHdrTypeReplyTo:
         {
            hdr.dType = SipHdrTypeReplyTo;
            hdr.pHeader = (void *)peerSipMsg->pGeneralHdr->pReplyToHdr;
         }
         break;
      } 
    
      if(hdr.pHeader)  
      { 
         if(sip_insertHeaderAtIndex(ourSipMsg, &hdr, 0, &err) == SipFail)
         { 
            logger.logINGwMsg(false, ERROR_FLAG, 0, 
                            "copyHeaders [%d] insert failed", currType);
         } 
      } 
   }

   logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwSpSipUtil::copyHeaders: OUT");
} 

void INGwSpSipUtil::_addPrivacyHeader(int gwToProcessMask, SipMessage *ourSipMsg)
{
   char value[100];
   char *currPtr = value;

   *currPtr = '\0';

   bool addcolon = false;

   if(gwToProcessMask & INGwSpSipProviderConfig::SIP_PRIVACY_NONE)
   {
      if(addcolon)
      {
         *currPtr = ';'; currPtr++; *currPtr = '\0';
      }

      currPtr = SAS_INGW::ccmStrCpy(currPtr, "none");

      addcolon = true;
   }

   if(gwToProcessMask & INGwSpSipProviderConfig::SIP_PRIVACY_SESSION)
   {
      if(addcolon)
      {
         *currPtr = ';'; currPtr++; *currPtr = '\0';
      }

      currPtr = SAS_INGW::ccmStrCpy(currPtr, "session");

      addcolon = true;
   }

   if(gwToProcessMask & INGwSpSipProviderConfig::SIP_PRIVACY_HEADER)
   {
      if(addcolon)
      {
         *currPtr = ';'; currPtr++; *currPtr = '\0';
      }

      currPtr = SAS_INGW::ccmStrCpy(currPtr, "header");

      addcolon = true;
   }

   if(gwToProcessMask & INGwSpSipProviderConfig::SIP_PRIVACY_USER)
   {
      if(addcolon)
      {
         *currPtr = ';'; currPtr++; *currPtr = '\0';
      }

      currPtr = SAS_INGW::ccmStrCpy(currPtr, "user");

      addcolon = true;
   }

   if(gwToProcessMask & INGwSpSipProviderConfig::SIP_PRIVACY_ID)
   {
      if(addcolon)
      {
         *currPtr = ';'; currPtr++; *currPtr = '\0';
      }

      currPtr = SAS_INGW::ccmStrCpy(currPtr, "id");

      addcolon = true;
   }

   if(gwToProcessMask & INGwSpSipProviderConfig::SIP_PRIVACY_CRITICAL)
   {
      if(addcolon)
      {
         *currPtr = ';'; currPtr++; *currPtr = '\0';
      }

      currPtr = SAS_INGW::ccmStrCpy(currPtr, "critical");

      addcolon = true;
   }

   if(!addcolon)
   {
      return;
   }

   SipUnknownHeader *hdr = NULL;
   hdr = makeUnknownHeader("Privacy", 7, value, (currPtr - value));

   SipHeader header;
   header.dType = SipHdrTypeUnknown;
   header.pHeader = hdr;

   SipError err;
   sip_insertHeaderAtIndex(ourSipMsg, &header, 0, &err);
   sip_freeSipUnknownHeader(hdr);

   return;
}

void INGwSpSipUtil::_removePrivacyHeader(SipMessage *ourSipMsg)
{
   logger.logINGwMsg (false, TRACE_FLAG, 0, "IN _removePrivacyHeader");

   SipError siperror;
   int index = 0;
   SipHeader lHdr;

   while(sip_getHeaderAtIndex(ourSipMsg, SipHdrTypeUnknown, &lHdr, 
         index, &siperror) != SipFail)
   {
      SipUnknownHeader *currHeader = (SipUnknownHeader *)(lHdr.pHeader);
 
      if(strcasecmp("Privacy", currHeader->pName) == 0)
      {
         sip_deleteHeaderAtIndex(ourSipMsg, SipHdrTypeUnknown, index, 
                                 &siperror);

         sip_freeSipUnknownHeader(currHeader);
         continue;
      }

      index++;
      sip_freeSipUnknownHeader(currHeader);
   }

   logger.logINGwMsg (false, TRACE_FLAG, 0, "OUT _removePrivacyHeader");
   return;
}

void INGwSpSipUtil::_anonymizeHeaders(SipMessage *ourSipMsg)
{
   SipFromHeader *fromHdr = ourSipMsg->pGeneralHdr->pFromHeader;

   FREE(fromHdr->pDispName);
   fromHdr->pDispName = STRDUPACCESSOR("\"Anonymous\"");

   if(fromHdr->pAddrSpec->dType == SipAddrSipUri || 
      fromHdr->pAddrSpec->dType == SipAddrSipSUri)
   {
      SipUrl *url = fromHdr->pAddrSpec->u.pSipUrl;

      FREE(url->pUser);
      url->pUser = STRDUPACCESSOR("Restricted");
   }
   else
   {
      //Uri is not sip, so can be tel or some other uri. 
      //For tel we cant put "restricted" because it violates the uri protocol.
   }

   SipList &contacts = ourSipMsg->pGeneralHdr->slContactHdr;

   for(SipListElement *curr = contacts.head; curr != SIP_NULL; 
       curr = curr->next)
   {
      SipContactHeader *currHeader = (SipContactHeader *)(curr->pData);

      FREE(currHeader->pDispName);
      currHeader->pDispName = STRDUPACCESSOR("\"Anonymous\"");

      if(currHeader->pAddrSpec->dType == SipAddrSipUri || 
         currHeader->pAddrSpec->dType == SipAddrSipSUri)
      {
         SipUrl *url = currHeader->pAddrSpec->u.pSipUrl;
   
         FREE(url->pUser);
         url->pUser = STRDUPACCESSOR("Restricted");
      }
   }

   SipList &unknownList = ourSipMsg->pGeneralHdr->slUnknownHdr;

   for(SipListElement *curr = unknownList.head; curr != SIP_NULL; 
       curr = curr->next)
   {
      SipUnknownHeader *currHeader = (SipUnknownHeader *)(curr->pData);

      if(0 == strcasecmp("P-Asserted-Identity", currHeader->pName))
      {
         char *start = (char *)currHeader->pBody;
         char *end = strstr(start, "sip:");

         if(end == NULL || start == end)
         {
            continue;
         }

         while(end != start && *end != '<')
         {
            end--;
         }

         if(*end != '<')
         {
            continue;
         }

         SipError err;
         char *newOut = (char*) fast_memget(0, 200, &err);
         char *currPtr = SAS_INGW::ccmStrCpy(newOut, 
                                        " \"Anonymous\" ");
         currPtr = SAS_INGW::ccmStrCpy(currPtr, end);

         FREE(currHeader->pBody);
         currHeader->pBody = newOut;
      }
   }
}

void INGwSpSipUtil::_addAssertedHeader(SipMessage *ourSipMsg, INGwSpSipConnection *apPeerConn)
{
  logger.logINGwMsg (false, TRACE_FLAG, 0, "IN _addAssertedHeader");

  if((apPeerConn->paiSipUri[0] != '\0') || (apPeerConn->paiTelUri[0] != '\0')) {

    logger.logINGwMsg (false, TRACE_FLAG, 0, "PAI Header received in Incoming Invite.");

    SipUnknownHeader *hdr = NULL;
    SipError err;
    int pos = 0;

    if(apPeerConn->paiSipUri[0] != '\0') {
      pos = 0;
      while(*(apPeerConn->paiSipUri + pos) == ' ') pos++;

      hdr = makeUnknownHeader("P-Asserted-Identity", 19, 
                              (apPeerConn->paiSipUri + pos), strlen(apPeerConn->paiSipUri + pos));
      
      SipHeader header;
      header.dType = SipHdrTypeUnknown;
      header.pHeader = hdr;
      
      sip_insertHeaderAtIndex(ourSipMsg, &header, 0, &err);
      sip_freeSipUnknownHeader(hdr);
    }

    if(apPeerConn->paiTelUri[0] != '\0') {

      pos = 0;
      while(*(apPeerConn->paiTelUri + pos) == ' ') pos++;

      hdr = makeUnknownHeader("P-Asserted-Identity", 19, 
                              (apPeerConn->paiTelUri + pos), strlen(apPeerConn->paiTelUri + pos));
      
      SipHeader header;
      header.dType = SipHdrTypeUnknown;
      header.pHeader = hdr;
      
      sip_insertHeaderAtIndex(ourSipMsg, &header, 0, &err);
      sip_freeSipUnknownHeader(hdr);
    }
  }
  else {

    logger.logINGwMsg (false, TRACE_FLAG, 0, "No PAI Header in Incoming Invite.");

    char *hdrStr = NULL;
    SipError err;
    
    sip_getHeaderAsStringAtIndex(ourSipMsg, SipHdrTypeFrom, &hdrStr, 0, &err);
    
    if(hdrStr == NULL)
    {
       logger.logMsg(ERROR_FLAG, 0, "Error getting the From Hdr.");
       logger.logINGwMsg (false, TRACE_FLAG, 0, "OUT _addAssertedHeader");
       return;
    }
    
    // Mriganka - 4.8.0->4.9.0 Merger - Start
    // BPInd08848
    // This is requirement from SBB, to remove unnecessary tag
    // from Asserted Header.
    // Check if it all contains tag, if not then normal processing.
    char *containsTag = strstr(hdrStr, ";tag");
    bool tagFlag = false;
    
    if(containsTag != NULL)
    {
        *containsTag = '\0';
        tagFlag = true;
    }
    // Mriganka - 4.8.0->4.9.0 Merger - End
    
    char *msgStart = NULL;
    if(hdrStr[1] == ':')
    {
       //From hdr in short form.
       msgStart = hdrStr + 3;
    }
    else
    {
       msgStart = hdrStr + 6;
    }
    
    {
       SipUnknownHeader *hdr = NULL;
       
       // Mriganka - 4.8.0->4.9.0 Merger - Start
       int msgLen = strlen (msgStart);
    
       // BPInd08848
       //last character of body is ^M\n so reduce length by 2
       if(tagFlag == false)
               	msgLen -= 2;
    
       hdr = makeUnknownHeader("P-Asserted-Identity", 19, 
                               msgStart, msgLen);
       // Mriganka - 4.8.0->4.9.0 Merger - End
    
       SipHeader header;
       header.dType = SipHdrTypeUnknown;
       header.pHeader = hdr;
    
       sip_insertHeaderAtIndex(ourSipMsg, &header, 0, &err);
       sip_freeSipUnknownHeader(hdr);
    }
    
    char *sipStart = NULL;
    
    if((sipStart = strstr(msgStart, "sip:")) != NULL)
    {
       SipUnknownHeader *hdr;
       sip_initSipUnknownHeader(&hdr, &err);
    
       hdr->pName = (SIP_S8bit *) fast_memget(0, 20, &err);
       memcpy(hdr->pName, "P-Asserted-Identity", 19);
       hdr->pName[19] = '\0';
    
       hdr->pBody = (SIP_S8bit *) fast_memget(0, 200, &err);
       char *currPtr = SAS_INGW::ccmStrnCpy(hdr->pBody, msgStart, 
                                               (sipStart - msgStart));
    
       currPtr = SAS_INGW::ccmStrCpy(currPtr, "tel");
    
       char *atStart = NULL;
       if((atStart = strstr(sipStart, "@")) != NULL)
       {
          currPtr = SAS_INGW::ccmStrnCpy(currPtr, sipStart + 3, 
                                            (atStart - sipStart - 3));
          if(*(sipStart + 4) != '+')
          {
             currPtr = SAS_INGW::ccmStrCpy(currPtr, 
                                         INGwSpSipProviderConfig::getAreaAppender());
          }
    
          if(*(sipStart - 1) == '<')
          {
             *currPtr = '>'; currPtr++; *currPtr = '\0';
          }
    
          SipHeader header;
          header.dType = SipHdrTypeUnknown;
          header.pHeader = hdr;
    
          sip_insertHeaderAtIndex(ourSipMsg, &header, 0, &err);
       }
    
       sip_freeSipUnknownHeader(hdr);
    }
    
    FREE(hdrStr);
  }
  logger.logINGwMsg (false, TRACE_FLAG, 0, "OUT _addAssertedHeader");
}

void INGwSpSipUtil::_removeAssertedHeader(SipMessage *ourSipMsg)
{
   logger.logINGwMsg (false, TRACE_FLAG, 0, "IN _removeAssertedHeader");

   SipError siperror;
   int index = 0;
   SipHeader lHdr;

   while(sip_getHeaderAtIndex(ourSipMsg, SipHdrTypeUnknown, &lHdr, 
         index, &siperror) != SipFail)
   {
      SipUnknownHeader *currHeader = (SipUnknownHeader *)(lHdr.pHeader);
 
      if(strcasecmp("P-Asserted-Identity", currHeader->pName) == 0)
      {
         sip_deleteHeaderAtIndex(ourSipMsg, SipHdrTypeUnknown, index, 
                                 &siperror);

         sip_freeSipUnknownHeader(currHeader);
         continue;
      }

      index++;
      sip_freeSipUnknownHeader(currHeader);
   }

   logger.logINGwMsg (false, TRACE_FLAG, 0, "OUT _removeAssertedHeader");
   return;
}

void INGwSpSipUtil::_addInformationalHeaders(SipMessage *ourSipMsg, 
                                         SipMessage *peerSipMsg)
{
   logger.logINGwMsg(false, TRACE_FLAG, 0, "IN addInformationalHeaders");

   SipError siperror;

   // First iterate through the list of unprocessed headers.
   SipList &locUnknownHdrList = peerSipMsg->pGeneralHdr->slUnknownHdr;

   std::set<int> &mProcessedInformationalHeader = 
                        INGwSpSipProviderConfig::getProcessedInformationalHeaders();
   std::set<std::string> &mUnprocessedInformationalHeaders = 
                      INGwSpSipProviderConfig::getUnprocessedInformationalHeaders();

   for(SipListElement *curr = locUnknownHdrList.head; curr != SIP_NULL;
       curr = curr->next)
   {
      SipUnknownHeader *currHeader = (SipUnknownHeader *)(curr->pData);
      string hdrName = currHeader->pName;

      if(mUnprocessedInformationalHeaders.find(hdrName) !=
                                        mUnprocessedInformationalHeaders.end())
      {
         // The header needs to be copied.
         SipHeader hdr;
         hdr.dType = SipHdrTypeUnknown;
         hdr.pHeader = curr->pData;
         if(sip_insertHeaderAtIndex(ourSipMsg, &hdr, 0, (SipError *) &siperror)
            == SipFail)
         {
           logger.logINGwMsg(false, ERROR_FLAG, 0,
             "INGwSpSipProviderConfig::addInformationalHeaders: "
             "Sip header insert failed");
         }
      }
   } 

   // Now iterate through the list of processed headers.
   // The list of processed headers is not exactly what it seems.  All headers
   // known to the stack are not processed.  Only the headers for which
   // processing is enabled are processed, and only those are to be taken care
   // of here.  Right now most processed headers are forbidden for copying,
   // so the following switch has only 2 cases, one of each type (one header,
   // and the other header list).  In future if some more headers are made
   // processable (list of processable headers can be found in INGwSpStackConfigMgr
   // as m_HdrsTobeDecodedList), they will have to be added as new cases.  The
   // code after switch-case block does not have to be changed.

   for(set<int>::iterator iter = mProcessedInformationalHeader.begin();
       iter != mProcessedInformationalHeader.end(); iter++)
   {
      // Get the header structure/list into a temporary variable.
      SipHeader hdr;
      hdr.pHeader = NULL;

      switch(*iter)
      {
         case SipHdrTypeCallInfo:
         {
            SipList &list = peerSipMsg->pGeneralHdr->slCallInfoHdr;

            for(SipListElement *curr = list.head; curr != SIP_NULL; 
                curr=curr->next)
            {
               hdr.dType = SipHdrTypeCallInfo;
               hdr.pHeader = curr->pData;

               if(sip_insertHeaderAtIndex(ourSipMsg, &hdr, 0, 
                                          (SipError*) &siperror)== SipFail)
               {
                  logger.logINGwMsg(false, ERROR_FLAG, 0, 
                                  "_addInformationalHeaders:Sipheader "
                                  "insert failed");
               }
            } 

            hdr.pHeader = NULL;
         }
         break;

         case SipHdrTypeOrganization:
         {
            hdr.dType = SipHdrTypeOrganization;
            hdr.pHeader = (void *)peerSipMsg->pGeneralHdr->pOrganizationHdr;
         }
         break;

         case SipHdrTypeUserAgent:
         {
            hdr.dType = SipHdrTypeUserAgent;
            hdr.pHeader = (void *)peerSipMsg->pGeneralHdr->pUserAgentHdr;
         }
         break;

         case SipHdrTypeSubject:
         {
            if(peerSipMsg->dType == SipMessageRequest)
            {
               hdr.dType = SipHdrTypeSubject;
               hdr.pHeader = 
                       (void *)peerSipMsg->u.pRequest->pRequestHdr->pSubjectHdr;
            }
         }
         break;

         case SipHdrTypeInReplyTo:
         {
            if(peerSipMsg->dType == SipMessageRequest)
            {
               SipList &list = 
                            peerSipMsg->u.pRequest->pRequestHdr->slInReplyToHdr;

               for(SipListElement *curr = list.head; curr != SIP_NULL; 
                   curr=curr->next)
               {
                  hdr.dType = SipHdrTypeInReplyTo;
                  hdr.pHeader = curr->pData;

                  if(sip_insertHeaderAtIndex(ourSipMsg, &hdr, 0, 
                                             (SipError*) &siperror)== SipFail)
                  {
                     logger.logINGwMsg(false, ERROR_FLAG, 0, 
                                     "_addInformationalHeaders:Sipheader "
                                     "insert failed");
                  }
               } 
            }

            hdr.pHeader = NULL;
         }
         break;

         case SipHdrTypeReplyTo:
         {
            hdr.dType = SipHdrTypeReplyTo;
            hdr.pHeader = (void *)peerSipMsg->pGeneralHdr->pReplyToHdr;
         }
         break;
      } 
    
      if(hdr.pHeader)  
      { 
         if(sip_insertHeaderAtIndex(ourSipMsg, &hdr, 0,(SipError*)&siperror) == 
            SipFail)
         { 
            logger.logINGwMsg(false, ERROR_FLAG, 0, 
                            "_addInformationalHeaders:Sipheader insert failed");
         } 
      } 
   } 

   logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT _addInformationalHeaders");
} 

void INGwSpSipUtil::_removeInformationalHeaders(SipMessage *ourSipMsg)
{
   logger.logINGwMsg (false, TRACE_FLAG, 0, "IN _removeInformationalHeaders");

   SipError lerror;

   std::set<int> &mProcessedInformationalHeader = 
                       INGwSpSipProviderConfig::getProcessedInformationalHeaders ();
   std::set<std::string> &mUnprocessedInformationalHeaders = 
                      INGwSpSipProviderConfig::getUnprocessedInformationalHeaders();

   for(set<int>::iterator iter = mProcessedInformationalHeader.begin();
       iter != mProcessedInformationalHeader.end(); iter++)
   {
      sip_deleteAllHeaderType(ourSipMsg, (en_HeaderType)*(iter), &lerror);
   }

   SipError siperror;
   int index = 0;
   SipHeader lHdr;

   while(sip_getHeaderAtIndex(ourSipMsg, SipHdrTypeUnknown, &lHdr, 
         index, &siperror) != SipFail)
   {
      SipUnknownHeader *currHeader = (SipUnknownHeader *)(lHdr.pHeader);

      if(mUnprocessedInformationalHeaders.find(currHeader->pName) != 
         mUnprocessedInformationalHeaders.end())
      {
         sip_deleteHeaderAtIndex(ourSipMsg, SipHdrTypeUnknown, index, 
                                 &siperror);

         sip_freeSipUnknownHeader(currHeader);
         continue;
      }

      index++;
      sip_freeSipUnknownHeader(currHeader);
   }

   logger.logINGwMsg (false, TRACE_FLAG, 0, "OUT _removeInformationalHeaders");
   return;
}

Sdf_ty_retVal INGwSpSipUtil::applyPrivacy(Sdf_st_callObject *aCallObj,
                                      SipMessage *ourSipMsg, 
                                      INGwSpSipConnection *apConn, 
                                      INGwSpSipConnection *apPeerConn)
{
   logger.logINGwMsg (false, TRACE_FLAG, 0, "IN applyPrivacy");
   int requestedMask = INGwSpSipProviderConfig::SIP_PRIVACY_ABSENT;

   requestedMask = INGwSpSipProviderConfig::getDefaultPrivacy();

   if((requestedMask & INGwSpSipProviderConfig::SIP_PRIVACY_NONE) || 
      (requestedMask & INGwSpSipProviderConfig::SIP_PRIVACY_ID))
   {
      //Add Asserted Header.
      _removeAssertedHeader(ourSipMsg);
      _addAssertedHeader(ourSipMsg, apPeerConn);
   }

   //For rest of the priv_value (session, critical, user) we dont have to add 
   //any headers.

   //Following will be the proxy behaviour. Based on the next hop capability 
   //lets consume/apply the handling for the priv_value.

   int gwCapabilityMask = INGwSpSipProviderConfig::getPrivacyMask();

   int processMask = requestedMask & (~gwCapabilityMask);
   int gwToProcessMask = requestedMask & gwCapabilityMask;

   _removePrivacyHeader(ourSipMsg);
   _addPrivacyHeader(gwToProcessMask, ourSipMsg);

   if(processMask == INGwSpSipProviderConfig::SIP_PRIVACY_ABSENT || 
      processMask == INGwSpSipProviderConfig::SIP_PRIVACY_NONE)
   {
   }
   else
   {
      //Remove information hdr.
      _removeInformationalHeaders(ourSipMsg);
   }

   if(processMask & INGwSpSipProviderConfig::SIP_PRIVACY_USER)
   {
      _anonymizeHeaders(ourSipMsg);
   }

   if(processMask & INGwSpSipProviderConfig::SIP_PRIVACY_ID)
   {
      //Remove PAI.
      _removeAssertedHeader(ourSipMsg);
   }

   if(processMask & INGwSpSipProviderConfig::SIP_PRIVACY_HEADER)
   {
      //No need to do any. B2B itself provides header privacy.
   }

   if(processMask & INGwSpSipProviderConfig::SIP_PRIVACY_NONE)
   {
      //We are supposed to add PAI, which is added in the front.
      //No spl processing here.
   }

   if(processMask & INGwSpSipProviderConfig::SIP_PRIVACY_SESSION)
   {
      logger.logMsg(ERROR_FLAG, 0, "Configuration problem in privacy setting."
                                   "Privacy Session should be handled by gw.");
      logger.logINGwMsg (false, ERROR_FLAG, 0, "OUT applyPrivacy");
      return Sdf_co_fail;
   }

   if(processMask & INGwSpSipProviderConfig::SIP_PRIVACY_CRITICAL)
   {
      logger.logMsg(ERROR_FLAG, 0, "Configuration problem in privacy setting."
                                   "Privacy Critical should be handled by gw.");
      logger.logINGwMsg (false, ERROR_FLAG, 0, "OUT applyPrivacy");
      return Sdf_co_fail;
   }

   logger.logINGwMsg (false, TRACE_FLAG, 0, "OUT applyPrivacy");
   return Sdf_co_success;
}

bool INGwSpSipUtil::setBodyFromAttr(SipMessage *msg, INGwSpSipConnection *conn)
{
	 // WILL not set BodyFromAttr
   return false;
}

SipUnknownHeader * INGwSpSipUtil::makeUnknownHeader(const char *name, int nameLen, 
                                                const char *val, int valLen)
{
   SipUnknownHeader *ret;

   SipError err;
   sip_initSipUnknownHeader(&ret, &err);

   ret->pName = (SIP_S8bit *) fast_memget(0, nameLen + 1, &err);
   ret->pBody = (SIP_S8bit *) fast_memget(0, valLen + 1, &err);

   memcpy(ret->pName, name, nameLen);
   memcpy(ret->pBody, val, valLen);

   ret->pName[nameLen] = '\0';
   ret->pBody[valLen] = '\0';

   return ret;
}

bool INGwSpSipUtil::setBillingInfoFromAttr(SipMessage *msg, INGwSpSipConnection *conn)
{
	 // WILL not set billing info
   return true;
}

bool INGwSpSipUtil::setReasonInfoFromAttr(SipMessage *msg, INGwSpSipConnection *conn)
{
	 // WILL not set Reason info
   return true;
}

bool INGwSpSipUtil::setAssertedFromAttr(SipMessage *msg, INGwSpSipConnection *conn)
{
	 // WILL not set Asserted info
   return true;
}

void INGwSpSipUtil::addHeadersFromAttr(SipMessage *msg, INGwSpSipConnection *conn)
{
   logger.logINGwMsg (false, TRACE_FLAG, 0, "IN addHeadersFromAttr");
   setBillingInfoFromAttr(msg, conn);
   setReasonInfoFromAttr(msg, conn);
   setAssertedFromAttr(msg, conn);

   logger.logINGwMsg (false, TRACE_FLAG, 0, "OUT addHeadersFromAttr");

   return;
}

bool INGwSpSipUtil::setContactInAttr(SipMessage *msg, INGwSpSipConnection *conn)
{

   SipList &contacts = msg->pGeneralHdr->slContactHdr;

   for(SipListElement *curr = contacts.head; curr != SIP_NULL;
       curr=curr->next)
   {
      SipContactHeader *currContact = (SipContactHeader *)(curr->pData);
            
      if(currContact == NULL)
      {
         continue;
      }

      SipAddrSpec *addrSpec = currContact->pAddrSpec;

      if(addrSpec == NULL)
      {
         continue;
      }

      switch(addrSpec->dType)
      {
         case SipAddrSipUri:
         case SipAddrSipSUri:
         {
            const char *contact = (const char *)addrSpec->u.pSipUrl->pUser;

            if(contact == NULL)
            {
               continue;
            }

            INGwSpMsgSipSpecificAttr *sipAttr = conn->getSipSpecificAttr();
            sipAttr->setContact(contact, strlen(contact));
            return true;
         }
         break;

         case SipAddrReqUri:
         {
            //Can be Tel.
            SipError err;
            TelUrl *telUrl;

            if(sip_getTelUrlFromAddrSpec(addrSpec, &telUrl, &err) == SipFail)
            {
               continue;
            }

            const char *contact = NULL;

            bool prefixFlag = false;
            TelGlobalNum *globalNum = telUrl->pGlobal;
            if(globalNum == NULL)
            {
               TelLocalNum *locNum = telUrl->pLocal;

               if(locNum == NULL)
               {
                  sip_freeTelUrl(telUrl);
                  continue;
               }

               contact = locNum->pLocalPhoneDigit;
            }
            else
            {
               contact = globalNum->pBaseNo;
               prefixFlag = true;
            }

            if(contact == NULL)
            {
               sip_freeTelUrl(telUrl);
               continue;
            }

            INGwSpMsgSipSpecificAttr *sipAttr = conn->getSipSpecificAttr();

            if(prefixFlag)
            {
               sipAttr->setContact(contact, strlen(contact), "+", 1);
            }
            else
            {
               sipAttr->setContact(contact, strlen(contact));
            }

            sip_freeTelUrl(telUrl);
            return true;
         }
         break;
      }
   }

   return false;
}

char*	INGwSpSipUtil::getRouteHeaderFromRR( Sdf_st_callObject *hssCallObj)
{

	logger.logINGwMsg(false, TRACE_FLAG, 0, 
			"getRouteHeaderFromRR() IN");

	char *dst = NULL;

	//Sdf_ty_slist RRListStruct ;
	Sdf_st_headerList RRListStruct ;
	SipError sipError;
	Sdf_st_error error;
	Sdf_ty_retVal retVal = Sdf_co_success;
	SIP_S8bit *pRRHeader = Sdf_co_null;

	if (SipFail == sip_getHeaderAsStringAtIndex (
				hssCallObj->pUasTransaction->pSipMsg, SipHdrTypeRecordRoute,  
				&pRRHeader, (SIP_U32bit)0, &sipError) ){

		logger.logINGwMsg(false, ERROR_FLAG, 0, "Record-Route Header not found.");

		return NULL;
	}

		logger.logINGwMsg(false, TRACE_FLAG, 0, "Record-Route list Header found.");

	if(Sdf_co_null == pRRHeader){

		logger.logINGwMsg(false, ERROR_FLAG, 0, "Record-Route Haeder String [ NULL ]" );

		return NULL;
	}

	logger.logINGwMsg(false, TRACE_FLAG, 0, "Record-Route string [ %s ]", 
			(char *) (pRRHeader) );

	// set the same value to connection
	dst = new char[MAX_HEADER_LEN];
	logger.logINGwMsg(false, TRACE_FLAG, 0, "Record-Route Header found -1 ");
	bzero( dst, MAX_HEADER_LEN);

	logger.logINGwMsg(false, TRACE_FLAG, 0, "Record-Route Header found -2 ");

	sprintf(dst, "Route%s", (char *)(pRRHeader + strlen("Record-Route") ) );
	
	logger.logINGwMsg(false, TRACE_FLAG, 0, "Route string [ %s ]", 
			(char *) (dst) );

	FREE(pRRHeader);

	return dst;

}

//	BPUsa07893 : [
//
//	This method is supposed to scan a Sip message char by char trying to 
//	find out if some unwanted char (out of those spcified in RFC 3261) is
//	present in either the Request/response line or any of the header lines of
//	the SIP message. We DO NOT scan the message bodies in this method.
//
//	This method returns 1 if the message is fine or 0 if some 
//	unspecified char was found and the message could be corrupted.
//
int INGwSpSipUtil::screenSipMessageForCorruption(const char* buffer, unsigned int length)
{
	int ret = 1;

	LogINGwTrace(false, 0, "IN screenSipMessageForCorruption()");

	if( (buffer == NULL) || (length == 0) )
	{

		logger.logMsg(ERROR_FLAG, 0, "Null Sip message.");
		LogINGwTrace(false, 0, "Out screenSipMessageForCorruption()");
		return 0;
	}

	for(int i=0; i< length; i++)
	{
		if(buffer[i]==13 && buffer[i+1]==10) //Allow \r only if it is followed by \n
		{
			if(length - i > 3)
			{
				// Check if we reached the end of headers list
				if(buffer[i+2]==13 && buffer[i+3]==10)
				{
					// We'r done with this message
					break;
				}
			}
			i++;
			continue;
		}
		if(buffer[i]==9 || buffer[i]==10) //Allow Tab and New Line Character
			continue;
		if(buffer[i]<32 || buffer[i]>126)
		{
			// We've some alien char here...
			logger.logMsg(ERROR_FLAG, 0, "Invalid char in Sip message: %x at index %d", buffer[i], i );
			ret = 0; 
			break;
		}
	}// for
	LogINGwTrace(false, 0, "Out screenSipMessageForCorruption()");
	return ret;
}

// : ]

// BPUsa07898 : [
int INGwSpSipUtil::fixSDPBody(const char* buffer, unsigned int length, char** pNewBuffer, unsigned int* pNewLength)
{
	LogINGwTrace(false, 0, "IN fixSDPBody()");

	if( (!length) || (buffer == NULL) )
	{
		// ERR
		logger.logMsg(ERROR_FLAG, 0, "Invalid SDP buffer.");
		LogINGwTrace(false, 0, "Out fixSDPBody()");
		return 0;
	}

	// Allocate new memory
	char* newBuffer = new char[MAX_SDP_LEN];
	if( !newBuffer)
	{
		// ERR
		logger.logMsg(ERROR_FLAG, 0, "Memory allocation failure.");
		LogINGwTrace(false, 0, "Out fixSDPBody()");
		return 0;
	}

	unsigned int newLength = 0;

	// Copy all the chars except '\n'
	// check if '\n' was preceeded by a '\r'
	//	if NOT then put a '\r' before it.

	unsigned int count = 0;

	while( count < length)
	{
		if( buffer[count] == '\n')
		{
			if (buffer[count-1] != '\r' )
			{
				newBuffer[newLength] = '\r';
				newLength++;
			}
		}

		newBuffer[newLength] = buffer[count];
		newLength++; count++;
	}

	// Check if the new buffer is NULL terminated..
	if( newBuffer[newLength-1] != '\0')
	{
		newBuffer[newLength] = '\0'; newLength++;
	}

	(*pNewBuffer) = newBuffer;
	(*pNewLength) = newLength;

	LogINGwTrace(false, 0, "Out fixSDPBody()");
	return 1;
}

// : ]
bool 
INGwSpSipUtil::setContactAddSpec(SipContactHeader* p_ContactHeader,
																	INGwSpSipConnection* p_Conn)
{
	SipAddrSpec* lContactAddSpec = NULL;
	SipError lErr;
  SipHeader header;
	header.dType = SipHdrTypeContactNormal;
	header.pHeader = (SIP_Pvoid)p_ContactHeader;

	SipBool ret = sip_getAddrSpecFromContactHdr(&header, 
																							&lContactAddSpec, &lErr);
	if(ret == SipFail)
	{
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
										 "sip_getAddrSpecFromContactHdr failed error code <%d>",
										 int(lErr));
	}
	else
	{
		SipAddrSpec *lAddrSpec = NULL;
		sip_initSipAddrSpec(&lAddrSpec, SipAddrSipUri, &lErr);
		sip_cloneSipAddrSpec(lAddrSpec, lContactAddSpec, &lErr);

		p_Conn->setContactAddSpec(lAddrSpec);

    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
										 "sip_getAddrSpecFromContactHdr success type  = %d", 
										 (int)lContactAddSpec->dType);

    SipList *params = NULL;
		params = &(lContactAddSpec->u.pSipUrl->slParam);
		for(SipListElement *curr = params->head; curr != SIP_NULL; curr=curr->next)
		{
			SipParam *currParam = (SipParam *)(curr->pData);
			const char *name = currParam->pName;
			const char *val = "";
			if(currParam->slValue.head)
			{
				val = (const char *)currParam->slValue.head->pData;
			}
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
										    "sip_getAddrSpecFromContactHdr  Param <%s> Value <%s> ", 
												name, val);
		}
    sip_freeSipAddrSpec(lContactAddSpec);
	}
	return true;
}

bool 
INGwSpSipUtil::updateSipAddSpec(INGwSpSipConnection* aSipConnection)
{
	LogINGwTrace(false, 0, "IN updateSipAddSpec()");

  // Extract the various addresses from the Call object and store them in the
  // connection.

  INGwSpAddress &origAddrStr = 
             aSipConnection->getAddress(INGwSpSipConnection::ORIGINATING_ADDRESS);

  INGwSpAddress &termAddrStr = 
             aSipConnection->getAddress(INGwSpSipConnection::TARGET_ADDRESS);

  INGwSpAddress &dialAddrStr = 
             aSipConnection->getAddress(INGwSpSipConnection::DIALED_ADDRESS);

  Sdf_st_callObject* lCallObj = aSipConnection->getHssCallObject();

  if(lCallObj == NULL)
  {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
										 "ERROR. NO Stack Call object in connection retrun false");
    return false;
  }

  INGwSpMsgInviteHandler* lInvHandler = 
      (INGwSpMsgInviteHandler*) aSipConnection->getSipHandler(INGW_SIP_METHOD_TYPE_INVITE);

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
										 "Parsing From header");
  lInvHandler->_parseFromHdr(aSipConnection, lCallObj->pCommonInfo->pFrom,
                             origAddrStr);

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
										 "Parsing To header");
  lInvHandler->_parseToHdr(aSipConnection, lCallObj->pCommonInfo->pTo, dialAddrStr);

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
										 "Parsing Conatct header");
  lInvHandler->_parseContact(lCallObj->pUasTransaction->pSipMsg, aSipConnection);
 
  // update the address spec
  SipAddrSpec* lContactAddSpec = aSipConnection->getContactAddSpec();
	SipAddrSpec* lFromAddSpec = aSipConnection->getFromAddSpec();
  
  //Update User, host and port in addr specs
  SipUrl* psipurl  = 0;
  SipError siperror;

  if(lFromAddSpec != NULL)
  {
    if((sip_getUrlFromAddrSpec(lFromAddSpec, &psipurl, &siperror) != SipSuccess) ||
                                                                                (!psipurl))
    {
      logger.logINGwMsg(aSipConnection->mLogFlag, ERROR_FLAG, 0,
                      "Count not get UrlFromAddrSpec so will use existing user and host");
    }
    else
    {
      if(psipurl->pHost != NULL && psipurl->dPort != NULL)
      {
        logger.logINGwMsg(false, TRACE_FLAG, 0,
                          "Before replace Host [%s] Port [%d]",
                          psipurl->pHost, *(psipurl->dPort));
      }
      char* newHost = (char *)fast_memget(0, strlen(aSipConnection->getGwIPAddress()) + 1, &siperror);

      strcpy(newHost, aSipConnection->getGwIPAddress());
      sip_setHostInUrl(psipurl, newHost, &siperror);
      sip_setPortInUrl(psipurl, aSipConnection->getGwPort(), &siperror);

      logger.logINGwMsg(false, TRACE_FLAG, 0,
                      "After replace Host [%s] Port [%d]",
                      psipurl->pHost, *(psipurl->dPort));
      sip_freeSipUrl(psipurl);
    }
  }


  SipUrl* psipurlContact  = 0;
  if(lContactAddSpec != NULL)
  {
    if((sip_getUrlFromAddrSpec(lContactAddSpec, &psipurlContact, &siperror) != SipSuccess) ||
                                                                                (!psipurlContact))
    {
      logger.logINGwMsg(aSipConnection->mLogFlag, ERROR_FLAG, 0,
                      "Count not get UrlFromAddrSpec so will use existing user and host");
    }
    else
    {
      if(psipurlContact->pHost != NULL && psipurlContact->dPort != NULL)
      {
        logger.logINGwMsg(false, TRACE_FLAG, 0,
                          "Before replace Host [%s] Port [%d]",
                          psipurlContact->pHost, *(psipurlContact->dPort));
    }
      std::string lUser = "ingw";
      //char* newUser = (char *)fast_memget(0, lUser.length()+ 1, &siperror);
      char* newHost = (char *)fast_memget(0, strlen(aSipConnection->getGwIPAddress()) + 1, &siperror);

      //strcpy(newUser, lUser.c_str());
      strcpy(newHost, aSipConnection->getGwIPAddress());
      //sip_setUserInUrl(psipurlContact, newUser, &siperror);
      sip_setHostInUrl(psipurlContact, newHost, &siperror);
      sip_setPortInUrl(psipurlContact, aSipConnection->getGwPort(), &siperror);

      logger.logINGwMsg(false, TRACE_FLAG, 0,
                      "After replace Host [%s] Port [%d]",
                      psipurlContact->pHost, *(psipurlContact->dPort));
      sip_freeSipUrl(psipurlContact);
    }
  }

	LogINGwTrace(false, 0, "OUT updateSipAddSpec()");
  return true;
}

bool 
INGwSpSipUtil::processIncomingViaHeader(INGwSpSipConnection *conn,
                                        Sdf_st_callObject *hssObj)
{
	 LogINGwTrace(false, 0, "IN processIncomingViaHeader()");
   if(!INGwSpSipProviderConfig::isProcessIncomingVia())
   {
	    LogINGwTrace(false, 0, "OUT processIncomingViaHeader()");
      return false;
   }

   SipList &viaHdrs = hssObj->pUasTransaction->pSipMsg->pGeneralHdr->slViaHdr;

   if(viaHdrs.size == 0)
   {
      logger.logINGwMsg(conn->mLogFlag, ERROR_FLAG, 0, "No via headers");
	    LogINGwTrace(false, 0, "OUT processIncomingViaHeader()");
      return false;
   }

   SipViaHeader *firstVia;
  
   switch(INGwIfrPrParamRepository::getInstance().getOperationMode())
   {
      case NPlusZero:
      case NPlusOne:
      {
         //We have to get the GW from second via.
         if(viaHdrs.size < 2)
         {
            logger.logINGwMsg(conn->mLogFlag, TRACE_FLAG, 0, "No GW Via headers");
            return false;
         }

         firstVia = (SipViaHeader *) viaHdrs.head->next->pData;
      }
      break;
      default:
      {
         firstVia = (SipViaHeader *) viaHdrs.head->pData;
      }
   }

   if(firstVia == NULL)
   {
      logger.logINGwMsg(conn->mLogFlag, ERROR_FLAG, 0, "NULL via in list");
	    LogINGwTrace(false, 0, "OUT processIncomingViaHeader()");
      return false;
   }

   char *peerInfo = firstVia->pSentBy;

   if(peerInfo == NULL)
   {
      logger.logINGwMsg(conn->mLogFlag, ERROR_FLAG, 0, "No sentby in Via");
	    LogINGwTrace(false, 0, "OUT processIncomingViaHeader()");
      return false;
   }

   //PeerInfo syntax. host[:port]
   //Lets check whether port is there or not.

   while(*peerInfo == ' ') peerInfo++;

   char *colonStart = strchr(peerInfo, ':');

   if(colonStart)
   {
      char *portStart = colonStart + 1;
      while(*portStart == ' ') portStart++;
      conn->setGwPort(atoi(portStart));

      char *peerEnd = colonStart - 1;
      while(*peerEnd == ' ' && peerEnd > peerInfo) peerEnd--;
      conn->setGwIPAddress(peerInfo, peerEnd - peerInfo + 1);
   }
   else
   {
      char *peerEnd = peerInfo;

      while(*peerEnd != ' ' && *peerEnd != '\0') peerEnd++;
      conn->setGwIPAddress(peerInfo, peerEnd - peerInfo);
      conn->setGwPort(5060);
   }

	 LogINGwTrace(false, 0, "OUT processIncomingViaHeader()");
   return true;
}



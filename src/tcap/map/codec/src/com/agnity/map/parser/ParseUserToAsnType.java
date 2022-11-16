package com.agnity.map.parser;

import java.math.BigInteger;

import org.apache.log4j.Logger;
import org.bn.types.BitString;

import com.agnity.map.asngenerated.AdditionalRequestedCAMEL_SubscriptionInfo;
import com.agnity.map.asngenerated.AddressString;
import com.agnity.map.asngenerated.AlertingPattern;
import com.agnity.map.asngenerated.AnyTimeSubscriptionInterrogationArg;
import com.agnity.map.asngenerated.BasicServiceCode;
import com.agnity.map.asngenerated.BearerServiceCode;
import com.agnity.map.asngenerated.EMLPP_Priority;
import com.agnity.map.asngenerated.Ext_BasicServiceCode;
import com.agnity.map.asngenerated.Ext_BearerServiceCode;
import com.agnity.map.asngenerated.Ext_TeleserviceCode;
import com.agnity.map.asngenerated.ForwardingReason;
import com.agnity.map.asngenerated.ForwardingReason.EnumType;
import com.agnity.map.asngenerated.IMSI;
import com.agnity.map.asngenerated.ISDN_AddressString;
import com.agnity.map.asngenerated.IST_SupportIndicator;
import com.agnity.map.asngenerated.InterrogationType;
import com.agnity.map.asngenerated.NumberOfForwarding;
import com.agnity.map.asngenerated.OR_Phase;
import com.agnity.map.asngenerated.RequestedCAMEL_SubscriptionInfo;
import com.agnity.map.asngenerated.RequestedSubscriptionInfo;
import com.agnity.map.asngenerated.SS_Code;
import com.agnity.map.asngenerated.SS_ForBS_Code;
import com.agnity.map.asngenerated.SendRoutingInfoArg;
import com.agnity.map.asngenerated.SubscriberIdentity;
import com.agnity.map.asngenerated.SupportedCCBS_Phase;
import com.agnity.map.asngenerated.SuppressMTSS;
import com.agnity.map.asngenerated.TBCD_STRING;
import com.agnity.map.asngenerated.TeleserviceCode;
import com.agnity.map.datatypes.AlertingPatternDataType;
import com.agnity.map.datatypes.AnyTimeSubscriptionInterrogationArgMap;
import com.agnity.map.datatypes.ExtBasicServiceCodeMap;
import com.agnity.map.datatypes.ExtBearerServiceCodeMap;
import com.agnity.map.datatypes.ExtTeleserviceCodeMap;
import com.agnity.map.datatypes.ISDNAddressStringMap;
import com.agnity.map.datatypes.RequestedSubscriptionInfoMap;
import com.agnity.map.datatypes.SendRoutingInfoArgMap;
import com.agnity.map.datatypes.SsCodeMap;
import com.agnity.map.datatypes.SubscriberIdentityMap;
import com.agnity.map.datatypes.SuppressMtssMap;
import com.agnity.map.enumdata.AddlRequestedCAMELSubscriptionInfoMapEnum;
import com.agnity.map.enumdata.CamelCapabilityHandlingMapEnum;
import com.agnity.map.enumdata.ForwardingReasonMapEnum;
import com.agnity.map.enumdata.InterrogationTypeEnumMap;
import com.agnity.map.enumdata.IstSupportIndicatorMapEnum;
import com.agnity.map.enumdata.RequestedCAMELSubscriptionInfoMapEnum;
import com.agnity.map.enumdata.SuppressMtssMapEnum;
import com.agnity.map.exceptions.InvalidInputException;
import org.apache.log4j.Logger;
import org.bn.types.BitString;

public class ParseUserToAsnType {
	
	private static Logger logger = Logger.getLogger(ParseUserToAsnType.class);
	
	public static SendRoutingInfoArg encodeUserToSriArg(SendRoutingInfoArgMap request) 
	throws InvalidInputException {
		if(request == null){
			logger.error("object to encode is null");
			throw new InvalidInputException("Object to encode is null");
		}
		
		SendRoutingInfoArg sriArgAsn = new SendRoutingInfoArg();
		
		// Get the mandatory types
		ISDNAddressStringMap msisdnUser = request.getMsisdn();
		InterrogationTypeEnumMap interrogationTypeUser = request.getInterrogationType();
		ISDNAddressStringMap gmscOrGsmScfAddrUser = request.getGmscOrGsmScfAddress();
		
		ISDN_AddressString msisdnAsn = new ISDN_AddressString();
		msisdnAsn.setValue(new AddressString(msisdnUser.encode()));
		sriArgAsn.setMsisdn(msisdnAsn);
		
		if(request.getInterrogationType() == InterrogationTypeEnumMap.BASIC_CALL){
			InterrogationType type = new InterrogationType();
			type.setValue(InterrogationType.EnumType.basicCall);
			sriArgAsn.setInterrogationType(type);
		}
		else if(request.getInterrogationType() == InterrogationTypeEnumMap.FORWARDING){
			InterrogationType type = new InterrogationType();
			type.setValue(InterrogationType.EnumType.forwarding);
			sriArgAsn.setInterrogationType(type);
		}
		
		
		ISDN_AddressString gmscOrGsmScfAddrAsn = new ISDN_AddressString();
		gmscOrGsmScfAddrAsn.setValue(new AddressString(gmscOrGsmScfAddrUser.encode()));
		sriArgAsn.setGmsc_OrGsmSCF_Address(gmscOrGsmScfAddrAsn);
		
		//Fetch optional types
	
		sriArgAsn.setNumberOfForwarding(new NumberOfForwarding(request.getNumberOfForwarding()));
		sriArgAsn.setOr_Capability(new OR_Phase(request.getOrCapability()));
		
		if(request.getForwardingReason() == ForwardingReasonMapEnum.BUSY){
			ForwardingReason reason = new ForwardingReason();
			reason.setValue(ForwardingReason.EnumType.busy);
			sriArgAsn.setForwardingReason(reason);			
		}
		else if(request.getForwardingReason() == ForwardingReasonMapEnum.NO_REPLY){
			ForwardingReason reason = new ForwardingReason();
			reason.setValue(ForwardingReason.EnumType.noReply);
			sriArgAsn.setForwardingReason(reason);			
		}
		else if(request.getForwardingReason() == ForwardingReasonMapEnum.NOT_REACHABLE){
			ForwardingReason reason = new ForwardingReason();
			reason.setValue(ForwardingReason.EnumType.notReachable);
			sriArgAsn.setForwardingReason(reason);			
		}
		
		int selectionCount = 0; 
		if(request.getBasicServiceGroup().getExtBearerService() != null){
			Ext_BasicServiceCode extBsCodeAsn = new Ext_BasicServiceCode();
			extBsCodeAsn.selectExt_BearerService(new Ext_BearerServiceCode(
					ExtBearerServiceCodeMap.encode(request.getBasicServiceGroup().getExtBearerService())));
			selectionCount += 1;
			sriArgAsn.setBasicServiceGroup(extBsCodeAsn);
		}
		
		if(request.getBasicServiceGroup().getExtTeleservice() != null){
			Ext_BasicServiceCode extBsCodeAsn = new Ext_BasicServiceCode();
			extBsCodeAsn.selectExt_Teleservice(new Ext_TeleserviceCode(
					ExtTeleserviceCodeMap.encode(request.getBasicServiceGroup().getExtTeleservice())));
			selectionCount += 1;
			sriArgAsn.setBasicServiceGroup(extBsCodeAsn);
		}
		
		if(selectionCount > 1) {
			logger.error("BasicServiceGroup can't have both bearer & tele services selected");
			throw new InvalidInputException("BasicServiceGroup can't have both bearer & tele services selected");
		}
		
		selectionCount = 0;
		if(request.getBasicServiceGroup2().getExtBearerService() != null){
			Ext_BasicServiceCode extBsCodeAsn = new Ext_BasicServiceCode();
			extBsCodeAsn.selectExt_BearerService(new Ext_BearerServiceCode(
					ExtBearerServiceCodeMap.encode(request.getBasicServiceGroup2().getExtBearerService())));
			selectionCount += 1;
			sriArgAsn.setBasicServiceGroup2(extBsCodeAsn);
		}
		
		if(request.getBasicServiceGroup2().getExtTeleservice() != null){
			Ext_BasicServiceCode extBsCodeAsn = new Ext_BasicServiceCode();
			extBsCodeAsn.selectExt_Teleservice(new Ext_TeleserviceCode(
					ExtTeleserviceCodeMap.encode(request.getBasicServiceGroup2().getExtTeleservice())));
			selectionCount += 1;
			sriArgAsn.setBasicServiceGroup2(extBsCodeAsn);

		}
		
		if(selectionCount > 1) {
			logger.error("BasicServiceGroup can't have both bearer & tele services selected");
			throw new InvalidInputException("BasicServiceGroup can't have both bearer & tele services selected");
		}
		
		
		sriArgAsn.setAlertingPattern(new AlertingPattern(AlertingPatternDataType.
				encodeAlertingPttrn(request.getAlertingPattern().getTypeOfPatrnEnum(), 
						request.getAlertingPattern().getTypeOfAlertCatgEnum(), 
						request.getAlertingPattern().getTypeOfAlertLevelEnum())));
		
		
		sriArgAsn.setCallPriority(new EMLPP_Priority(request.getCallingPriority()));
		sriArgAsn.setSupportedCCBS_Phase(new SupportedCCBS_Phase(request.getSupportedCCBSPhase()));
		sriArgAsn.setSuppressMTSS(new SuppressMTSS(new BitString(SuppressMtssMap.encode(request.getSuppressMTSS()))));
		
		if(request.getIstSupportIndicator() == IstSupportIndicatorMapEnum.BASIC_IST_SUPPORTED){
			IST_SupportIndicator istIndAsn = new IST_SupportIndicator();
			istIndAsn.setValue(IST_SupportIndicator.EnumType.basicISTSupported);
			sriArgAsn.setIstSupportIndicator(istIndAsn);
		}
		else if(request.getIstSupportIndicator() == IstSupportIndicatorMapEnum.IST_COMMAND_SUPPORTED){
			IST_SupportIndicator istIndAsn = new IST_SupportIndicator();
			istIndAsn.setValue(IST_SupportIndicator.EnumType.istCommandSupported);
			sriArgAsn.setIstSupportIndicator(istIndAsn);
		}
		
		return sriArgAsn;
	} 
	
	public static AnyTimeSubscriptionInterrogationArg encodeUserToAtsiArg(
			AnyTimeSubscriptionInterrogationArgMap request)
	throws InvalidInputException {
		if(request==null){
			logger.error("Object to encode is null");
			throw new InvalidInputException("Object to encode is null");
		}
		
		SubscriberIdentityMap subiduser = request.getSubscriberIdentity();
		RequestedSubscriptionInfoMap reqinfouser = request.getRequestedSubsInfo();
		ISDNAddressStringMap gsmscfaddruser = request.getGsmScfAddress();
		
		if (subiduser == null) {
			logger.error("Mandatory Parameter Subscriber Id Missing");
			throw new InvalidInputException("Mandatory Parameter Subscriber Id Missing");
		}
		
		if (reqinfouser == null) {
			logger.error("Mandatory Parameter Requested Subscription Info Missing");
			throw new InvalidInputException("Mandatory Subscription Info Missing");
		}
		
		if (gsmscfaddruser == null) {
			logger.error("Mandatory Parameter GsmScfAddress Missing");
			throw new InvalidInputException("Mandatory Parameter GsmScfAddress Missing");
		}
		
		// Create  Subscriber Id for ASN
		SubscriberIdentity subIdAsn = new SubscriberIdentity();
		IMSI imsiAsn = new IMSI();
		if (subiduser.getImsi() != null) {
			imsiAsn.setValue(new TBCD_STRING(subiduser.getImsi().encode()));
			subIdAsn.selectImsi(imsiAsn);	
		} 
		else if (subiduser.getMsisdn() != null){
			ISDN_AddressString msisdnAsn = new ISDN_AddressString();
			msisdnAsn.setValue(new AddressString(subiduser.getMsisdn().encode()));
			subIdAsn.selectMsisdn(msisdnAsn);
		}
		else {
			logger.error("Subscriber Identity Not Provided, user must select either MSISDN or IMSI");
			throw new InvalidInputException("Subscriber Identity Not Provided, user must select either MSISDN or IMSI");
		}
			
		// Create  GSM SCF Addr for ASN
		ISDN_AddressString gsmScfAddrAsn = new ISDN_AddressString();
		gsmScfAddrAsn.setValue(new AddressString(gsmscfaddruser.encode()));
		
		// Create  Req.Subs Info for ASN
		RequestedSubscriptionInfo reqInfoAsn =  new RequestedSubscriptionInfo();
		if (reqinfouser.getAddlReqCAMELSubsInfo() != null ) {
			AdditionalRequestedCAMEL_SubscriptionInfo csiasn = 
					new AdditionalRequestedCAMEL_SubscriptionInfo();
			
			AddlRequestedCAMELSubscriptionInfoMapEnum csiuser = reqinfouser.getAddlReqCAMELSubsInfo();
			
			if (csiuser == AddlRequestedCAMELSubscriptionInfoMapEnum.D_IM_CSI){
				csiasn.setValue( AdditionalRequestedCAMEL_SubscriptionInfo.EnumType.d_IM_CSI );
			}
			else if (csiuser == AddlRequestedCAMELSubscriptionInfoMapEnum.MG_CSI){
				csiasn.setValue( AdditionalRequestedCAMEL_SubscriptionInfo.EnumType.mg_csi);
			}
			else if (csiuser == AddlRequestedCAMELSubscriptionInfoMapEnum.MT_SMS_CSI){
				csiasn.setValue( AdditionalRequestedCAMEL_SubscriptionInfo.EnumType.mt_sms_CSI);
			}
			else if (csiuser == AddlRequestedCAMELSubscriptionInfoMapEnum.O_IM_CSI){
				csiasn.setValue( AdditionalRequestedCAMEL_SubscriptionInfo.EnumType.o_IM_CSI);
			}
			else if (csiuser == AddlRequestedCAMELSubscriptionInfoMapEnum.VT_IM_CSI){
				csiasn.setValue( AdditionalRequestedCAMEL_SubscriptionInfo.EnumType.vt_IM_CSI);
			}
			
			reqInfoAsn.setAdditionalRequestedCAMEL_SubscriptionInfo(csiasn);
		}
		
		if (reqinfouser.getReqCAMELSubsInfo() != null) {

			RequestedCAMEL_SubscriptionInfo csiasn = new RequestedCAMEL_SubscriptionInfo();
			
			RequestedCAMELSubscriptionInfoMapEnum csiuser =  reqinfouser.getReqCAMELSubsInfo();
			if(csiuser == RequestedCAMELSubscriptionInfoMapEnum.D_CSI) {
				csiasn.setValue(RequestedCAMEL_SubscriptionInfo.EnumType.d_csi);	
			} 
			else if (csiuser == RequestedCAMELSubscriptionInfoMapEnum.GPRS_CSI) {
				csiasn.setValue(RequestedCAMEL_SubscriptionInfo.EnumType.gprs_CSI);
			}
			else if (csiuser == RequestedCAMELSubscriptionInfoMapEnum.M_CSI) {
				csiasn.setValue(RequestedCAMEL_SubscriptionInfo.EnumType.m_CSI);
			}
			else if (csiuser == RequestedCAMELSubscriptionInfoMapEnum.MO_SMS_CSI) {
				csiasn.setValue(RequestedCAMEL_SubscriptionInfo.EnumType.mo_sms_CSI);
			}
			else if (csiuser == RequestedCAMELSubscriptionInfoMapEnum.O_CSI) {
				csiasn.setValue(RequestedCAMEL_SubscriptionInfo.EnumType.o_CSI);
			}
			else if (csiuser == RequestedCAMELSubscriptionInfoMapEnum.SS_CSI) {
				csiasn.setValue(RequestedCAMEL_SubscriptionInfo.EnumType.ss_CSI);
			}
			else if (csiuser == RequestedCAMELSubscriptionInfoMapEnum.T_CSI) {
				csiasn.setValue(RequestedCAMEL_SubscriptionInfo.EnumType.t_CSI);
			}
			else if (csiuser == RequestedCAMELSubscriptionInfoMapEnum.VT_CSI) {
				csiasn.setValue(RequestedCAMEL_SubscriptionInfo.EnumType.vt_CSI);
			}
			
			reqInfoAsn.setRequestedCAMEL_SubscriptionInfo(csiasn);
		}
		
		if (reqinfouser.getSsforBSCode() != null) {
			SS_ForBS_Code ssobj = new SS_ForBS_Code();
			// set the mandatory parameter for SsForBSCode
			ssobj.setSs_Code(new SS_Code(SsCodeMap.encode(reqinfouser.getSsforBSCode().getSscode())));
			
			if (reqinfouser.getSsforBSCode().getBasicServiceCode() != null) {
				BasicServiceCode bscode = new BasicServiceCode();
				if(reqinfouser.getSsforBSCode().getBasicServiceCode().getBearerServiceCode() != null) {
					BearerServiceCode bearer = new BearerServiceCode();
					// TODO: Provide Encode method for Enum
					byte[] data = new byte[]{(byte) (0xFF&reqinfouser.getSsforBSCode().
							getBasicServiceCode().getBearerServiceCode().getCode())};
					bearer.setValue(data);
					bscode.selectBearerService(bearer);
				}
				else if (reqinfouser.getSsforBSCode().getBasicServiceCode().getTeleServiceCode() != null) {
					System.out.println("found tele service code");
					TeleserviceCode tele = new TeleserviceCode();
					// TODO: Provide Encode method for Enum
					tele.setValue(new byte[]{(byte) (0xFF&reqinfouser.getSsforBSCode().
							getBasicServiceCode().getTeleServiceCode().getCode())});
					
					bscode.selectTeleservice(tele);
				} else {
					logger.error("Error No Value selected for BasicService");
					throw new InvalidInputException("Error No Value selected for BasicService");
				}
				ssobj.setBasicService(bscode);
			}
			reqInfoAsn.setRequestedSS_Info(ssobj);
		}
		
		
		AnyTimeSubscriptionInterrogationArg atsiasn = new AnyTimeSubscriptionInterrogationArg();
		
		atsiasn.setRequestedSubscriptionInfo(reqInfoAsn);
		atsiasn.setSubscriberIdentity(subIdAsn);
		atsiasn.setGsmSCF_Address(gsmScfAddrAsn);
		
		return atsiasn;
	}
}

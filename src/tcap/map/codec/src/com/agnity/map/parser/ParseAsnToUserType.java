package com.agnity.map.parser;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.agnity.map.asngenerated.AdditionalRequestedCAMEL_SubscriptionInfo;
import com.agnity.map.asngenerated.AddressString;
import com.agnity.map.asngenerated.AllowedServices;
import com.agnity.map.asngenerated.AnyTimeInterrogationRes;
import com.agnity.map.asngenerated.AnyTimeModificationRes;
import com.agnity.map.asngenerated.AnyTimeSubscriptionInterrogationArg;
import com.agnity.map.asngenerated.AnyTimeSubscriptionInterrogationRes;
import com.agnity.map.asngenerated.BasicServiceCode;
import com.agnity.map.asngenerated.BearerServiceCode;
import com.agnity.map.asngenerated.CAMEL_SubscriptionInfo;
import com.agnity.map.asngenerated.CallBarringData;
import com.agnity.map.asngenerated.CallForwardingData;
import com.agnity.map.asngenerated.CauseValue;
import com.agnity.map.asngenerated.CellGlobalIdOrServiceAreaIdOrLAI;
import com.agnity.map.asngenerated.DP_AnalysedInfoCriteriaList;
import com.agnity.map.asngenerated.DP_AnalysedInfoCriterium;
import com.agnity.map.asngenerated.D_CSI;
import com.agnity.map.asngenerated.DefaultCallHandling;
import com.agnity.map.asngenerated.DefaultGPRS_Handling;
import com.agnity.map.asngenerated.DestinationNumberCriteria;
import com.agnity.map.asngenerated.Ext_BasicServiceCode;
import com.agnity.map.asngenerated.Ext_CallBarFeatureList;
import com.agnity.map.asngenerated.Ext_CallBarringFeature;
import com.agnity.map.asngenerated.Ext_CallBarringInfoFor_CSE;
import com.agnity.map.asngenerated.Ext_ForwFeature;
import com.agnity.map.asngenerated.Ext_ForwFeatureList;
import com.agnity.map.asngenerated.Ext_ForwardingInfoFor_CSE;
import com.agnity.map.asngenerated.Ext_SS_InfoFor_CSE;
import com.agnity.map.asngenerated.ForwardingData;
import com.agnity.map.asngenerated.GPRS_CSI;
import com.agnity.map.asngenerated.GPRS_CamelTDPData;
import com.agnity.map.asngenerated.GPRS_TriggerDetectionPoint;
import com.agnity.map.asngenerated.GSN_Address;
import com.agnity.map.asngenerated.IMSI;
import com.agnity.map.asngenerated.IMS_VoiceOverPS_SessionsInd;
import com.agnity.map.asngenerated.ISDN_AddressString;
import com.agnity.map.asngenerated.ISDN_SubaddressString;
import com.agnity.map.asngenerated.LocationInformation;
import com.agnity.map.asngenerated.LocationInformationEPS;
import com.agnity.map.asngenerated.LocationInformationGPRS;
import com.agnity.map.asngenerated.MG_CSI;
import com.agnity.map.asngenerated.MM_Code;
import com.agnity.map.asngenerated.MNPInfoRes;
import com.agnity.map.asngenerated.MSISDN_BS;
import com.agnity.map.asngenerated.M_CSI;
import com.agnity.map.asngenerated.NotReachableReason;
import com.agnity.map.asngenerated.NumberPortabilityStatus;
import com.agnity.map.asngenerated.RequestedCAMEL_SubscriptionInfo;
import com.agnity.map.asngenerated.RequestedSubscriptionInfo;
import com.agnity.map.asngenerated.SS_ForBS_Code;
import com.agnity.map.asngenerated.SendRoutingInfoRes.SendRoutingInfoResSequenceType;
import com.agnity.map.asngenerated.SubscriberIdentity;
import com.agnity.map.asngenerated.TBCD_STRING;
import com.agnity.map.asngenerated.NotReachableReason.EnumType;
import com.agnity.map.asngenerated.NoteSubscriberDataModifiedArg;
import com.agnity.map.asngenerated.ODB_Data;
import com.agnity.map.asngenerated.ODB_Info;
import com.agnity.map.asngenerated.O_BcsmCamelTDPData;
import com.agnity.map.asngenerated.O_BcsmCamelTDPDataList;
import com.agnity.map.asngenerated.O_BcsmCamelTDP_Criteria;
import com.agnity.map.asngenerated.O_BcsmTriggerDetectionPoint;
import com.agnity.map.asngenerated.O_CSI;
import com.agnity.map.asngenerated.PDP_ContextInfo;
import com.agnity.map.asngenerated.PDP_ContextInfoList;
import com.agnity.map.asngenerated.PS_SubscriberState;
import com.agnity.map.asngenerated.RoutingInfo;
import com.agnity.map.asngenerated.SMS_CAMEL_TDP_Data;
import com.agnity.map.asngenerated.SMS_CAMEL_TDP_DataList;
import com.agnity.map.asngenerated.SMS_CSI;
import com.agnity.map.asngenerated.SS_CSI;
import com.agnity.map.asngenerated.SS_CamelData;
import com.agnity.map.asngenerated.SS_Code;
import com.agnity.map.asngenerated.SS_List;
import com.agnity.map.asngenerated.SendRoutingInfoRes;
import com.agnity.map.asngenerated.SpecificCSI_Withdraw;
import com.agnity.map.asngenerated.SubscriberInfo;
import com.agnity.map.asngenerated.SubscriberState;
import com.agnity.map.asngenerated.T_BCSM_CAMEL_TDP_Criteria;
import com.agnity.map.asngenerated.T_BcsmCamelTDPData;
import com.agnity.map.asngenerated.T_BcsmCamelTDPDataList;
import com.agnity.map.asngenerated.T_CSI;
import com.agnity.map.asngenerated.T_CauseValueCriteria;
import com.agnity.map.asngenerated.TeleserviceCode;
import com.agnity.map.asngenerated.UnavailabilityCause;
import com.agnity.map.asngenerated.Used_RAT_Type;
import com.agnity.map.asngenerated.UserCSGInformation;
import com.agnity.map.asngenerated.SSInvocationNotificationArg;
import com.agnity.map.asngenerated.SS_EventSpecification;
import com.agnity.map.asngenerated.CCBS_RequestState;
import com.agnity.map.asngenerated.SS_EventSpecification;
import com.agnity.map.datatypes.AddressStringMap;
import com.agnity.map.datatypes.SSEventSpecificationMap;
import com.agnity.map.enumdata.CCBSRequestStateEnum;
import com.agnity.map.enumdata.SupplementaryServicesMapEnum;

import com.agnity.map.datatypes.SSInvocationNotificationArgMap;
import com.agnity.map.datatypes.AgeOfLocationInformationMap;
import com.agnity.map.datatypes.AllowedServicesMap;
import com.agnity.map.datatypes.AnyTimeInterrogationResMap;
import com.agnity.map.datatypes.AnyTimeModificationResMap;
import com.agnity.map.datatypes.AnyTimeSubscriptionInterrogationArgMap;
import com.agnity.map.datatypes.AnyTimeSubscriptionInterrogationResMap;
import com.agnity.map.datatypes.BasicServiceCodeMap;
import com.agnity.map.datatypes.CallBarringDataMap;
import com.agnity.map.datatypes.CallForwardingDataMap;
import com.agnity.map.datatypes.CamelSubscriptionInfoMap;
import com.agnity.map.datatypes.Cause;
import com.agnity.map.datatypes.CellGidOrSaiOrLaiMap;
import com.agnity.map.datatypes.CellGlobalIdOrServiceAreaIdFixedLengthMap;
import com.agnity.map.datatypes.DCsiMap;
import com.agnity.map.datatypes.DPAnalyzedInfoCriteriumMap;
import com.agnity.map.datatypes.DestinationNumberCriteriaMap;
import com.agnity.map.datatypes.EUtranCellGlobalIdentityMap;
import com.agnity.map.datatypes.ExtBasicServiceCodeMap;
import com.agnity.map.datatypes.ExtBearerServiceCodeMap;
import com.agnity.map.datatypes.ExtCallBarringFeatureMap;
import com.agnity.map.datatypes.ExtCallBarringInfoForCSEMap;
import com.agnity.map.datatypes.ExtForwFeatureMap;
import com.agnity.map.datatypes.ExtForwOptionsMap;
import com.agnity.map.datatypes.ExtForwardingInfoForCSEMap;
import com.agnity.map.datatypes.ExtSsInfoForCseMap;
import com.agnity.map.datatypes.ExtSsStatusMap;
import com.agnity.map.datatypes.ExtTeleserviceCodeMap;
import com.agnity.map.datatypes.ForwOptionsMap;
import com.agnity.map.datatypes.ForwardingDataMap;
import com.agnity.map.datatypes.FtnAddressStringMap;
import com.agnity.map.datatypes.GeodeticInformationMap;
import com.agnity.map.datatypes.GeographicalInformationMap;
import com.agnity.map.datatypes.GprsCamelTdpDataMap;
import com.agnity.map.datatypes.GprsCsiMap;
import com.agnity.map.datatypes.ISDNAddressStringMap;
import com.agnity.map.datatypes.ImeiMap;
import com.agnity.map.datatypes.ImsiDataType;
import com.agnity.map.datatypes.LAIFixedLenDataType;
import com.agnity.map.datatypes.LSAIdentityMap;
import com.agnity.map.datatypes.LocationInformationEpsMap;
import com.agnity.map.datatypes.LocationInformationGprsMap;
import com.agnity.map.datatypes.LocationInformationMap;
import com.agnity.map.datatypes.LocationNumberMap;
import com.agnity.map.datatypes.MCsiMap;
import com.agnity.map.datatypes.MgCsiMap;
import com.agnity.map.datatypes.MmCodeMap;
import com.agnity.map.datatypes.MnpInfoResMap;
import com.agnity.map.datatypes.MsClassMark2Map;
import com.agnity.map.datatypes.MsIsdnBsMap;
import com.agnity.map.datatypes.NoteSubscriberDataModifiedArgMap;
import com.agnity.map.datatypes.OBcsmCamelTDPCriteriaMap;
import com.agnity.map.datatypes.OBcsmCamelTDPDataMap;
import com.agnity.map.datatypes.OCsiMap;
import com.agnity.map.datatypes.ODBDataMap;
import com.agnity.map.datatypes.ODBGeneralDataMap;
import com.agnity.map.datatypes.ODBHplmnDataMap;
import com.agnity.map.datatypes.ODBInfoMap;
import com.agnity.map.datatypes.OfferedCamel4CsiMap;
import com.agnity.map.datatypes.PDPContextInfoMap;
import com.agnity.map.datatypes.RequestedNodesMap;
import com.agnity.map.datatypes.RequestedSubscriptionInfoMap;
import com.agnity.map.datatypes.RouteingAreaIdentityMap;
import com.agnity.map.datatypes.RoutingInfoMap;
import com.agnity.map.datatypes.SendRoutingInfoResMap;
import com.agnity.map.datatypes.ServiceKeyMap;
import com.agnity.map.datatypes.SmsCamelTdpDataMap;
import com.agnity.map.datatypes.SmsCsiMap;
import com.agnity.map.datatypes.SpecificCSIWithdrawMap;
import com.agnity.map.datatypes.SsCamelDataMap;
import com.agnity.map.datatypes.SsCodeMap;
import com.agnity.map.datatypes.SsCsiMap;
import com.agnity.map.datatypes.SsForBSCodeMap;
import com.agnity.map.datatypes.SubscriberIdentityMap;
import com.agnity.map.datatypes.SubscriberInfoMap;
import com.agnity.map.datatypes.SubscriberStateMap;
import com.agnity.map.datatypes.SupportedCamelPhasesMap;
import com.agnity.map.datatypes.TBcsmCamelTDPCriteriaMap;
import com.agnity.map.datatypes.TBcsmCamelTDPDataMap;
import com.agnity.map.datatypes.TCsiMap;
import com.agnity.map.datatypes.TimeMap;
import com.agnity.map.datatypes.TrackingAreaIdentityMap;
import com.agnity.map.datatypes.UserCsgInformationMap;
import com.agnity.map.enumdata.AccessModeMapEnum;
import com.agnity.map.enumdata.BearerServiceCodeMapEnum;
import com.agnity.map.enumdata.CallTypeCriteriaMapEnum;
import com.agnity.map.enumdata.CamelCapabilityHandlingMapEnum;
import com.agnity.map.enumdata.CauseValEnum;
import com.agnity.map.enumdata.CmiMapEnum;
import com.agnity.map.enumdata.DefaultCallHandlingMapEnum;
import com.agnity.map.enumdata.DefaultSmsHandlingMapEnum;
import com.agnity.map.enumdata.GprsTriggerDetectionPointMapEnum;
import com.agnity.map.enumdata.ImsVoiceOverPSSessionsIndMapEnum;
import com.agnity.map.enumdata.NotReachableReasonMapEnum;
import com.agnity.map.enumdata.NumberPlanMapEnum;
import com.agnity.map.enumdata.NumberPortabilityStatusMapEnum;
import com.agnity.map.enumdata.OBcsmTriggerDetectionPointMapEnum;
import com.agnity.map.enumdata.SmsTriggerDetectionPointMapEnum;
import com.agnity.map.enumdata.TBcsmTriggerDetectionPointMapEnum;
import com.agnity.map.enumdata.TeleServiceCodeMapEnum;
import com.agnity.map.enumdata.UnavailabilityCauseMapEnum;
import com.agnity.map.enumdata.UsedRATTypeMapEnum;
import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.operations.MapOpCodes;
import com.agnity.map.operations.MapOperationsCoding;
import com.agnity.map.enumdata.DefaultGPRSHandlingMapEnum;
import com.agnity.map.util.MapFunctions;
import com.agnity.map.util.NonAsnArg;
import com.genband.tcap.parser.Util;

import com.agnity.map.datatypes.PDPContextInfoListMap;
import com.agnity.map.datatypes.PsSubscriberStateMap;

public class ParseAsnToUserType {
	
	private static Logger logger = Logger.getLogger(ParseAsnToUserType.class);
	
	public static AnyTimeInterrogationResMap decodeAsnToAtiRes(AnyTimeInterrogationRes atiRes)
	throws InvalidInputException{

		SubscriberInfo subInfoAsn = atiRes.getSubscriberInfo();
		SubscriberInfoMap ssuser = decodeAsnToSubsInfo(subInfoAsn);
		
		// subscriber info in the result
		AnyTimeInterrogationResMap result = new AnyTimeInterrogationResMap(ssuser);
		
		return result;
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
		if (reqinfouser.getSsforBSCode() != null ) {
			AdditionalRequestedCAMEL_SubscriptionInfo addlInfo = 
					new AdditionalRequestedCAMEL_SubscriptionInfo();
			addlInfo.setIntegerForm(reqinfouser.getAddlReqCAMELSubsInfo().getCode());
			reqInfoAsn.setAdditionalRequestedCAMEL_SubscriptionInfo(addlInfo);
		}
		
		if (reqinfouser.getReqCAMELSubsInfo() != null) {
			RequestedCAMEL_SubscriptionInfo cinfo = new RequestedCAMEL_SubscriptionInfo();
			cinfo.setIntegerForm(reqinfouser.getReqCAMELSubsInfo().getCode());
			reqInfoAsn.setRequestedCAMEL_SubscriptionInfo(cinfo);
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
					bearer.setValue(new byte[]{(byte) (0xFF&reqinfouser.getSsforBSCode().
							getBasicServiceCode().getBearerServiceCode().getCode())});
					bscode.selectBearerService(bearer);
				}
				else if (reqinfouser.getSsforBSCode().getBasicServiceCode().getTeleServiceCode() != null) {
					TeleserviceCode tele = new TeleserviceCode();
					// TODO: Provide Encode method for Enum
					tele.setValue(new byte[]{(byte) (0xFF&reqinfouser.getSsforBSCode().
							getBasicServiceCode().getTeleServiceCode().getCode())});
					
					bscode.selectTeleservice(tele);
				} else {
					logger.error("Error No Value selected for BasicService");
					throw new InvalidInputException("Error No Value selected for BasicService");
				}
			}
		}
		
		
		AnyTimeSubscriptionInterrogationArg atsiasn = new AnyTimeSubscriptionInterrogationArg();
		
		atsiasn.setRequestedSubscriptionInfo(reqInfoAsn);
		atsiasn.setSubscriberIdentity(subIdAsn);
		atsiasn.setGsmSCF_Address(gsmScfAddrAsn);

		return atsiasn;
	}
	
	public static AnyTimeSubscriptionInterrogationResMap decodeAsnToAtsiRes(
			AnyTimeSubscriptionInterrogationRes atsiresasn) throws InvalidInputException {

		AnyTimeSubscriptionInterrogationResMap response = new AnyTimeSubscriptionInterrogationResMap();
		
		if(atsiresasn.isCallBarringDataPresent()) {
			CallBarringData asnobj = atsiresasn.getCallBarringData();
			CallBarringDataMap cb = decodeAsnToCallBarringData(asnobj);
			response.setCallBarringData(cb);
		}
		
		if(atsiresasn.isCallForwardingDataPresent()) { /////////////////////////
			
			CallForwardingData cfdasn = atsiresasn.getCallForwardingData();
			Ext_ForwFeatureList flist = cfdasn.getForwardingFeatureList();
			Iterator<Ext_ForwFeature> asniter = flist.getValue().iterator();
			
			ArrayList<ExtForwFeatureMap> arlist = new ArrayList<ExtForwFeatureMap>();
			
			while(asniter.hasNext()){
				Ext_ForwFeature objfrom = asniter.next();
				ExtForwFeatureMap objto = new ExtForwFeatureMap(
						ExtSsStatusMap.decode(objfrom.getSs_Status().getValue()));

				
				if(objfrom.isForwardedToNumberPresent()) {
					objto.setForwardedToNumber(new ISDNAddressStringMap(
							objfrom.getForwardedToNumber().getValue().getValue()));
				}
				
				if(objfrom.isForwardedToSubaddressPresent()){
					objto.setForwardedToSubaddress(new ISDNAddressStringMap(
							objfrom.getForwardedToSubaddress().getValue()
							));
				}
				
				if(objfrom.isForwardingOptionsPresent()){
					objto.setForwardingOptions(new ExtForwOptionsMap(
							objfrom.getForwardingOptions().getValue()));
				}
				
				if(objfrom.isNoReplyConditionTimePresent()){
					objto.setNoReplyConditionTime(objfrom.getNoReplyConditionTime().getValue());
				}
				
				if(objfrom.isLongForwardedToNumberPresent()){
					objto.setLongForwardedToNumber(new FtnAddressStringMap(
							objfrom.getLongForwardedToNumber().getValue().getValue()));
				}
				arlist.add(objto);
			}
			
			CallForwardingDataMap cfdmap = new CallForwardingDataMap(arlist);
			
			response.setCallForwardingData(cfdmap);
		}

		if(atsiresasn.isOdb_InfoPresent()){
			ODB_Info odbasn = atsiresasn.getOdb_Info();
			ODBInfoMap odbuser = decodeAsnToOdbInfo(odbasn);
			response.setodbInfo(odbuser);
		}
		
		if(atsiresasn.isCamel_SubscriptionInfoPresent()){
			CAMEL_SubscriptionInfo csiasn = atsiresasn.getCamel_SubscriptionInfo();
			CamelSubscriptionInfoMap csiobj = decodeAsnToCamelSubsInfo(csiasn);
			response.setCamelSubscriptionInfo(csiobj);
		}
		
		if(atsiresasn.isSupportedVLR_CAMEL_PhasesPresent()){
			response.setSupportedVlrCamelPhases(
					SupportedCamelPhasesMap.decode(
					atsiresasn.getSupportedVLR_CAMEL_Phases().getValue().getValue()));
		}
		
		if(atsiresasn.isSupportedSGSN_CAMEL_PhasesPresent()){
			response.setSupportedSgsnCamelPhases(
					SupportedCamelPhasesMap.decode(
							atsiresasn.getSupportedSGSN_CAMEL_Phases().getValue().getValue()));
		}
		
		if(atsiresasn.isOfferedCamel4CSIsInSGSNPresent()){
			response.setOfferedCamel4CsisInSgsn(OfferedCamel4CsiMap.decode(
					atsiresasn.getOfferedCamel4CSIsInSGSN().getValue().getValue()));
		}
		
		if(atsiresasn.isOfferedCamel4CSIsInVLRPresent()){
			response.setOfferedCamel4CsisInVLR(OfferedCamel4CsiMap.decode(
					atsiresasn.getOfferedCamel4CSIsInVLR().getValue().getValue()));
		}
		
		if(atsiresasn.isMsisdn_BS_ListPresent()){
			System.out.println("BS List is present");
			Iterator<MSISDN_BS> iter = atsiresasn.getMsisdn_BS_List().getValue().iterator();
			ArrayList<MsIsdnBsMap> list = new ArrayList<MsIsdnBsMap>();
			while(iter.hasNext()){
				
				MSISDN_BS fromobj = iter.next();
				ISDNAddressStringMap msisdnuser =new ISDNAddressStringMap(fromobj.getMsisdn().getValue().getValue());
		
				MsIsdnBsMap toobj = new MsIsdnBsMap(msisdnuser);
				System.out.println("got msisdn user");
				
				if(fromobj.isBasicServiceListPresent()){
					System.out.println("HAS bs list");
					ArrayList<ExtBasicServiceCodeMap> bslist = new ArrayList<ExtBasicServiceCodeMap>();
					Iterator<Ext_BasicServiceCode> bsiter = fromobj.getBasicServiceList().getValue().iterator();
					while(bsiter.hasNext()){
						System.out.println("iterating bs code");
						Ext_BasicServiceCode el = bsiter.next();
						ExtBasicServiceCodeMap eluser = decodeAsnToExtBasicServiceCodeMap(el);
						bslist.add(eluser);
					}
					toobj.setBasicServiceList(bslist);
				}
				list.add(toobj);
			}
			response.setMsisdnBsList(list);
		}
		
		return response;
	}


	/**
	 * @param binaryres
	 * @return
	 * @throws InvalidInputException
	 */
	public static SendRoutingInfoResMap decodeAsnToSriRes(SendRoutingInfoRes binaryres) 
			throws InvalidInputException{
 		System.out.println("decodeAsnToSriRes Entr");

 		if(logger.isDebugEnabled()) {
 			logger.debug("decodeAsnToSriRes Entr");
 		}
 		
 		SendRoutingInfoResSequenceType asnres = binaryres.getValue();
 		
		SendRoutingInfoResMap response = new SendRoutingInfoResMap();
		
		if( asnres.isNumberPortabilityStatusPresent()){
			if(asnres.getNumberPortabilityStatus().getValue() == NumberPortabilityStatus.EnumType.foreignNumberPortedIn){
			response.setNumberPortabilityStatus(NumberPortabilityStatusMapEnum.FOREIGN_NUMBER_PORTED_IN);
			}
			if(asnres.getNumberPortabilityStatus().getValue() == NumberPortabilityStatus.EnumType.foreignNumberPortedToForeignNetwork){
			response.setNumberPortabilityStatus(NumberPortabilityStatusMapEnum.FOREIGN_NUMBER_PORTED_TO_FOREIGN_NETWORK);
			}
			if(asnres.getNumberPortabilityStatus().getValue() == NumberPortabilityStatus.EnumType.notKnownToBePorted){
			response.setNumberPortabilityStatus(NumberPortabilityStatusMapEnum.NOT_KNOWN_TO_BE_PORTED);
			}
			if(asnres.getNumberPortabilityStatus().getValue() == NumberPortabilityStatus.EnumType.ownNumberNotPortedOut){
			response.setNumberPortabilityStatus(NumberPortabilityStatusMapEnum.OWN_NUMBER_NOT_PORTED_OUT);
			}
			if(asnres.getNumberPortabilityStatus().getValue() == NumberPortabilityStatus.EnumType.ownNumberPortedOut){
			response.setNumberPortabilityStatus(NumberPortabilityStatusMapEnum.OWN_NUMBERPORTED_OUT);
			}


		}
		
		if(asnres.isImsiPresent()) {
			response.setImsi(ImsiDataType.decodeImsi(
					asnres.getImsi().getValue().getValue()));
		}
		
		if(asnres.isSubscriberInfoPresent()){
			response.setSubscriberInfo(decodeAsnToSubsInfo(asnres.getSubscriberInfo()));
		}
		
		if(asnres.isBasicServicePresent()){
			response.setBasicService(decodeAsnToExtBasicServiceCodeMap(asnres.getBasicService()));
		}
		
		if(asnres.isBasicService2Present()){
			response.setBasicService2(decodeAsnToExtBasicServiceCodeMap(asnres.getBasicService2()));
		}
		
		if(asnres.isSs_ListPresent()){
			SS_List sslist = asnres.getSs_List();
			Iterator<SS_Code> itr = sslist.getValue().iterator();
			ArrayList<SsCodeMap> ssmaplist = new ArrayList<SsCodeMap>();
			
			while(itr.hasNext()){
				ssmaplist.add(SsCodeMap.decode(itr.next().getValue()));
			}
			
			response.setSsList(ssmaplist);
		}
		
		if(asnres.isVmsc_AddressPresent()){
			response.setVmscAddress(
					new ISDNAddressStringMap(asnres.getVmsc_Address().getValue().getValue()));
		}
		
		if(asnres.isMsisdnPresent()){
			response.setMsisdn(new ISDNAddressStringMap(asnres.getMsisdn().getValue().getValue()));
		}
		
		if(asnres.isNumberPortabilityStatusPresent()){
			response.setNumberPortabilityStatus(decodeAsnToNpStatus(asnres.getNumberPortabilityStatus()));
		}
		
		if(asnres.isSupportedCamelPhasesInVMSCPresent()){
			response.setSupportedCamelPhasesMap(
					SupportedCamelPhasesMap.decode(
							asnres.getSupportedCamelPhasesInVMSC().getValue().getValue()));
		}

		if(asnres.isOfferedCamel4CSIsInVMSCPresent()){
			response.setOfferedCamel4CSIsInVMSC(OfferedCamel4CsiMap.decode(
					asnres.getOfferedCamel4CSIsInVMSC().getValue().getValue()));
		}
		
		if(asnres.isRoutingInfo2Present()){
			System.out.println("Routing info is present ====");
			RoutingInfo asnri = asnres.getRoutingInfo2();
			RoutingInfoMap useri = null; 
			if(asnri.isRoamingNumberSelected()) {
				ISDNAddressStringMap roamingNumber = new ISDNAddressStringMap(
						asnri.getRoamingNumber().getValue().getValue());
				useri = new RoutingInfoMap(roamingNumber);
			}
			else if(asnri.isForwardingDataSelected()){
				ForwardingDataMap userdata = new ForwardingDataMap();
				ForwardingData asnfwd = asnri.getForwardingData();
				if(asnfwd.isForwardedToNumberPresent()){
					userdata.setForwardedToNumber(
							new ISDNAddressStringMap(
									asnfwd.getForwardedToNumber().getValue().getValue()));
				}
				if(asnfwd.isForwardedToSubaddressPresent()){
					userdata.setForwardedToSubaddress(
							new ISDNAddressStringMap(
									asnfwd.getForwardedToSubaddress().getValue()));
				}
				if(asnfwd.isForwardingOptionsPresent()){
					ForwOptionsMap fwdoptions = new ForwOptionsMap();
					fwdoptions.decode(asnfwd.getForwardingOptions().getValue());
				}
				if(asnfwd.isLongForwardedToNumberPresent()){
					userdata.setLongForwardedToNumber(
							new FtnAddressStringMap(
									asnfwd.getLongForwardedToNumber().getValue().getValue()));
				}
				
				useri = new RoutingInfoMap(userdata);
			}
			
			response.setRoutingInfo2(useri);
		}
		
		if(asnres.isSs_List2Present()){
			SS_List sslist = asnres.getSs_List2();
			Iterator<SS_Code> itr = sslist.getValue().iterator();
			ArrayList<SsCodeMap> ssmaplist = new ArrayList<SsCodeMap>();
			
			while(itr.hasNext()){
				ssmaplist.add(SsCodeMap.decode(itr.next().getValue()));
			}
			
			response.setSsList2(ssmaplist);
		}

		if(asnres.isUnavailabilityCausePresent()){
			UnavailabilityCause.EnumType asnenum  = asnres.getUnavailabilityCause().getValue();
			if(asnenum == UnavailabilityCause.EnumType.absentSubscriber){
				response.setUnavailabilityCause(UnavailabilityCauseMapEnum.ABSENTSUBSCRIBER);
			}
			else if(asnenum == UnavailabilityCause.EnumType.bearerServiceNotProvisioned){
				response.setUnavailabilityCause(UnavailabilityCauseMapEnum.BEARERSERVICENOTPROVISIONED);						
			}
			else if(asnenum == UnavailabilityCause.EnumType.busySubscriber){
				response.setUnavailabilityCause(UnavailabilityCauseMapEnum.BUSYSUBSCRIBER);						
			}
			else if(asnenum == UnavailabilityCause.EnumType.callBarred){
				response.setUnavailabilityCause(UnavailabilityCauseMapEnum.CALLBARRED);						
			}
			else if(asnenum == UnavailabilityCause.EnumType.cug_Reject){
				response.setUnavailabilityCause(UnavailabilityCauseMapEnum.CUG_REJECT);						
			}
			else if(asnenum == UnavailabilityCause.EnumType.teleserviceNotProvisioned){
				response.setUnavailabilityCause(UnavailabilityCauseMapEnum.TELESERVICENOTPROVISIONED);						
			}
		}
		
		return response;
	}

	/**
	 * @param locInfoAsn
	 * @return
	 * @throws InvalidInputException
	 */
	public static LocationInformationMap decodeAsnToLocInfo(LocationInformation locInfoAsn) 
	throws InvalidInputException {
		LocationInformationMap locInfoUser = new LocationInformationMap();
		if(locInfoAsn == null) {
			logger.error("LocationInformation Object is null");
			throw new InvalidInputException("LocationInformation object is null");
		}
		
		if (locInfoAsn.isAgeOfLocationInformationPresent()) {
			locInfoUser.setAgeOfLocation(new AgeOfLocationInformationMap(
					locInfoAsn.getAgeOfLocationInformation().getValue()));
		}

		if (locInfoAsn.isGeographicalInformationPresent()) {
			locInfoUser
					.setGeographicalInfo(GeographicalInformationMap
							.decode(locInfoAsn.getGeographicalInformation()
									.getValue()));
		}

		if (locInfoAsn.isVlr_numberPresent()) {
			locInfoUser.setVlrNum(new ISDNAddressStringMap(locInfoAsn
					.getVlr_number().getValue().getValue()));
		}

		if (locInfoAsn.isLocationNumberPresent()) {
			locInfoUser.setLocNum(LocationNumberMap.decode(locInfoAsn
					.getLocationNumber().getValue()));
		}

		if (locInfoAsn.isCellGlobalIdOrServiceAreaIdOrLAIPresent()) {
			// if CGID/SAI present
			if (locInfoAsn.getCellGlobalIdOrServiceAreaIdOrLAI()
					.isCellGlobalIdOrServiceAreaIdFixedLengthSelected()) {

				byte[] data = locInfoAsn.getCellGlobalIdOrServiceAreaIdOrLAI()
						.getCellGlobalIdOrServiceAreaIdFixedLength().getValue();

				CellGlobalIdOrServiceAreaIdFixedLengthMap cgidSaiObj = CellGlobalIdOrServiceAreaIdFixedLengthMap
						.decode(data);
				locInfoUser.setCgidOrSaiOrLai(new CellGidOrSaiOrLaiMap(
						cgidSaiObj));
			} else if (locInfoAsn.getCellGlobalIdOrServiceAreaIdOrLAI()
					.isLaiFixedLengthSelected()) {
				byte[] data = locInfoAsn.getCellGlobalIdOrServiceAreaIdOrLAI()
						.getLaiFixedLength().getValue();
				LAIFixedLenDataType lai = LAIFixedLenDataType.decodeLAI(data);
				locInfoUser.setCgidOrSaiOrLai(new CellGidOrSaiOrLaiMap(lai));
			}
		}

		if (locInfoAsn.isSelectedLSA_IdPresent()) {
			locInfoUser.setSelectedLsaId(LSAIdentityMap.decode(locInfoAsn
					.getSelectedLSA_Id().getValue()));
		}

		if (locInfoAsn.isMsc_NumberPresent()) {
			locInfoUser.setMscNumber(new ISDNAddressStringMap(locInfoAsn
					.getMsc_Number().getValue().getValue()));
		}

		if (locInfoAsn.isGeodeticInformationPresent()) {
			locInfoUser.setGeodeticInfo(GeodeticInformationMap
					.decode(locInfoAsn.getGeodeticInformation().getValue()));
		}

		if (locInfoAsn.isLocationInformationEPSPresent()) {
			LocationInformationEpsMap epsUser = new LocationInformationEpsMap();
			LocationInformationEPS epsAsn = locInfoAsn
					.getLocationInformationEPS();
			if (epsAsn.isAgeOfLocationInformationPresent()) {
				epsUser.setAgeOfLocation(new AgeOfLocationInformationMap(epsAsn
						.getAgeOfLocationInformation().getValue()));
			}

			if (epsAsn.isE_utranCellGlobalIdentityPresent()) {
				epsUser.setEUtranCgid(EUtranCellGlobalIdentityMap.decode(epsAsn
						.getE_utranCellGlobalIdentity()));
			}

			if (epsAsn.isGeodeticInformationPresent()) {
				epsUser.setGeodeticInformation(GeodeticInformationMap
						.decode(epsAsn.getGeodeticInformation().getValue()));
			}

			if (epsAsn.isGeographicalInformationPresent()) {
				epsUser.setGeographicalInformation(GeographicalInformationMap
						.decode(epsAsn.getGeographicalInformation().getValue()));
			}

			if (epsAsn.isTrackingAreaIdentityPresent()) {
				epsUser.setTrackingAreaIdentity(TrackingAreaIdentityMap
						.decode(epsAsn.getTrackingAreaIdentity()));
			}

			locInfoUser.setLocInfoEps(epsUser);
		}

		if (locInfoAsn.isUserCSGInformationPresent()) {
			UserCSGInformation ucsgasn = locInfoAsn.getUserCSGInformation();
			UserCsgInformationMap usg = new UserCsgInformationMap(ucsgasn.getCsg_Id().getValue().getValue());
			if (ucsgasn.isAccessModePresent()) {
				int mode = ucsgasn.getAccessMode()[0];
				usg.setAccessMode(AccessModeMapEnum.getValue(mode));
			}

			if (ucsgasn.isCmiPresent()) {
				int cmi = ucsgasn.getCmi()[0];
				usg.setCmi(CmiMapEnum.getValue(cmi));
			}
			locInfoUser.setUserCsgInfo(usg);
		}

		return locInfoUser;
	}

	/**
	 * @param ssasn
	 * @return
	 * @throws InvalidInputException
	 */
	public static SubscriberStateMap decodeAsnToSubState(SubscriberState ssasn)
	throws InvalidInputException{
		SubscriberStateMap ssobj = new SubscriberStateMap();

		if(ssasn.isNetDetNotReachableSelected()) {
			System.out.println("isNetDetReachable is selected");
			System.out.println("asn getting vlaue = "+ssasn.getNetDetNotReachable().getValue());
			//
			// Hack-alert:
			// Due to bug ACM-XXX in binary notes, i'm not using getIntegerForm()
			//
			EnumType reason = ssasn.getNetDetNotReachable().getValue();
			if(reason == NotReachableReason.EnumType.imsiDetached){
				ssobj.setNetDetNotReachableReason(NotReachableReasonMapEnum.IMSI_DETACHED);
			} 
			else if(reason == NotReachableReason.EnumType.msPurged){
				ssobj.setNetDetNotReachableReason(NotReachableReasonMapEnum.MS_PERGED);
			}
			else if(reason == NotReachableReason.EnumType.notRegistered){
				ssobj.setNetDetNotReachableReason(NotReachableReasonMapEnum.NOT_REGISTERED);
			}
			else if(reason == NotReachableReason.EnumType.restrictedArea){
				ssobj.setNetDetNotReachableReason(NotReachableReasonMapEnum.RESTRICTED_AREA);
			}
		}
			
		ssobj.setAssumedIdle(ssasn.isAssumedIdleSelected());
		ssobj.setCamelBusy(ssasn.isCamelBusySelected());
		ssobj.setNotProvidedFromVLR(ssasn.isNotProvidedFromVLRSelected());
		
		return ssobj;
	}


	/**
	 * @param psSubscriberStateAsn
	 * @return
	 * @throws InvalidInputException
	 */
	public static PsSubscriberStateMap decodeAsnToPsSubscriberState(PS_SubscriberState psSubscriberStateAsn) 
			throws InvalidInputException {
		PsSubscriberStateMap psSubsState = new PsSubscriberStateMap();

		if(psSubscriberStateAsn.isNotProvidedFromSGSNorMMESelected()) {
			psSubsState.setNotProvidedFromSGSNorMME(true);
		}
		else {
			psSubsState.setNotProvidedFromSGSNorMME(false);
		}
		
		if(psSubscriberStateAsn.isPs_DetachedSelected()) {
			psSubsState.setPsDetached(true);
		} 
		else {
			psSubsState.setPsDetached(false);
		}
		
		if(psSubscriberStateAsn.isPs_AttachedNotReachableForPagingSelected()) {
			psSubsState.setPsAttachedNotReachableForPaging(true);
		} 
		else {
			psSubsState.setPsAttachedNotReachableForPaging(false);
		}

		if(psSubscriberStateAsn.isPs_AttachedReachableForPagingSelected()) {
			psSubsState.setPsAttachedReachableForPaging(true);
		} 
		else {
			psSubsState.setPsAttachedReachableForPaging(false);
		}

		if(psSubscriberStateAsn.isPs_PDP_ActiveNotReachableForPagingSelected()){ /////////////////////////

			PDP_ContextInfoList pdpContextInfoListAsn = psSubscriberStateAsn.getPs_PDP_ActiveNotReachableForPaging();
			Iterator<PDP_ContextInfo> asniter = pdpContextInfoListAsn.getValue().iterator();

			PDPContextInfoListMap pdpContextInfoListMap = new PDPContextInfoListMap();
			pdpContextInfoListMap.initValue();
			java.util.Collection<PDPContextInfoMap> pdpContextInfoMapList = pdpContextInfoListMap.getValue();

			while(asniter.hasNext()){
				PDP_ContextInfo pdpContextInfoAsn = asniter.next();
				pdpContextInfoMapList.add(PDPContextInfoMap.decode(pdpContextInfoAsn));

			}
		}

		if(psSubscriberStateAsn.isNetDetNotReachableSelected()){
			EnumType reason = psSubscriberStateAsn.getNetDetNotReachable().getValue();
			if(reason == NotReachableReason.EnumType.imsiDetached){
				psSubsState.setNetDetNotReachable(NotReachableReasonMapEnum.IMSI_DETACHED);
			} 
			else if(reason == NotReachableReason.EnumType.msPurged){
				psSubsState.setNetDetNotReachable(NotReachableReasonMapEnum.MS_PERGED);
			}
			else if(reason == NotReachableReason.EnumType.notRegistered){
				psSubsState.setNetDetNotReachable(NotReachableReasonMapEnum.NOT_REGISTERED);
			}
			else if(reason == NotReachableReason.EnumType.restrictedArea){
				psSubsState.setNetDetNotReachable(NotReachableReasonMapEnum.RESTRICTED_AREA);
			}
		}

		if(psSubscriberStateAsn.isPs_PDP_ActiveNotReachableForPagingSelected()){
			PDP_ContextInfoList pdpContextInfoListAsn =  psSubscriberStateAsn.getPs_PDP_ActiveNotReachableForPaging();
			java.util.Collection<PDP_ContextInfo> pdpContextInfoAsn = pdpContextInfoListAsn.getValue();
		}

		return psSubsState;
	}

	/**
	 * @param locasn
	 * @return
	 * @throws InvalidInputException
	 */
	public static LocationInformationGprsMap decodeAsnToLocInfoGprs(LocationInformationGPRS locasn) 
			throws InvalidInputException {
		LocationInformationGprsMap locgprs = new LocationInformationGprsMap();

		if(locasn.isAgeOfLocationInformationPresent()) {
			locgprs.setAgeOfLocation(new AgeOfLocationInformationMap(
					locasn.getAgeOfLocationInformation().getValue()));
		}
		
		if(locasn.isCellGlobalIdOrServiceAreaIdOrLAIPresent()) {
			CellGlobalIdOrServiceAreaIdOrLAI asn = locasn.getCellGlobalIdOrServiceAreaIdOrLAI();
			if(asn.isCellGlobalIdOrServiceAreaIdFixedLengthSelected()) {
				byte[] data = asn.getCellGlobalIdOrServiceAreaIdFixedLength()
						.getValue();

				CellGlobalIdOrServiceAreaIdFixedLengthMap cgidSaiObj = 
						CellGlobalIdOrServiceAreaIdFixedLengthMap.decode(data);
				
				locgprs.setCgidOrSaiOrLai(new CellGidOrSaiOrLaiMap(cgidSaiObj));
			}
			else if(asn.isLaiFixedLengthSelected()) {
				byte[] data =  asn.getLaiFixedLength().getValue();
				LAIFixedLenDataType lai = LAIFixedLenDataType.decodeLAI(data);
				locgprs.setCgidOrSaiOrLai(new CellGidOrSaiOrLaiMap(lai));
			}
		}
		
		if(locasn.isGeodeticInformationPresent()) {
			locgprs.setGeodeticInformation(
				GeodeticInformationMap.decode(locasn.getGeodeticInformation().getValue()));
		}
		
		if(locasn.isGeographicalInformationPresent()) {
			locgprs.setGeoInformation(
				GeographicalInformationMap.decode(locasn.getGeographicalInformation().getValue()));
		}
		
		if(locasn.isRouteingAreaIdentityPresent()) {
			// TODO: need to put RA identity when support in codec is 
			// available
		}
		
		if(locasn.isSelectedLSAIdentityPresent()){
			locgprs.setSelectedLSAIdentity(LSAIdentityMap
					.decode(locasn.getSelectedLSAIdentity().getValue()));
		}
		
		if(locasn.isSgsn_NumberPresent()){
			locgprs.setSgsnNumber(new ISDNAddressStringMap(
					locasn.getSgsn_Number().getValue().getValue()));
		}
		
		if(locasn.isUserCSGInformationPresent()) {
			UserCSGInformation ucsgasn = locasn.getUserCSGInformation();
			
			UserCsgInformationMap usg = new UserCsgInformationMap(ucsgasn.getCsg_Id().getValue().getValue());
			
			if(ucsgasn.isAccessModePresent()){
				int mode = ucsgasn.getAccessMode()[0];
				usg.setAccessMode( AccessModeMapEnum.getValue(mode) );
			}
			
			if(ucsgasn.isCmiPresent()) {
				int cmi = ucsgasn.getCmi()[0];
				usg.setCmi(CmiMapEnum.getValue(cmi));
			}
			locgprs.setUserCSGInformation(usg);
		}
		
		return locgprs;
	}

	/**
	 * @param asn
	 * @return
	 * @throws InvalidInputException
	 */
	public static LocationInformationEpsMap decodeAsnToLocInfoEps(LocationInformationEPS asn) 
	throws InvalidInputException {
		LocationInformationEpsMap user = new LocationInformationEpsMap();
		if(asn.isE_utranCellGlobalIdentityPresent()){
			user.setEUtranCgid(EUtranCellGlobalIdentityMap
					.decode(asn.getE_utranCellGlobalIdentity()));
		}
		
		if(asn.isTrackingAreaIdentityPresent()){
			user.setTrackingAreaIdentity(TrackingAreaIdentityMap
					.decode(asn.getTrackingAreaIdentity()));
		}
		
		if(asn.isGeographicalInformationPresent()){
			user.setGeographicalInformation(GeographicalInformationMap
					.decode(asn.getGeographicalInformation().getValue()));
		}
		
		if(asn.isGeodeticInformationPresent()){
			user.setGeodeticInformation(GeodeticInformationMap.
					decode(asn.getGeodeticInformation().getValue()));
		}
		return user;
	}


	/**
	 * @param subInfoAsn
	 * @return
	 * @throws InvalidInputException
	 */
	public static SubscriberInfoMap decodeAsnToSubsInfo(SubscriberInfo subInfoAsn)
	throws InvalidInputException{
		SubscriberInfoMap ssuser = new SubscriberInfoMap();
		
		// Location Information 
		if(subInfoAsn.isLocationInformationPresent()){
			LocationInformation locInfoAsn = subInfoAsn.getLocationInformation();
			ssuser.setLocationInfo(decodeAsnToLocInfo(locInfoAsn));
		}
		
		// Decode Subscriber state
		
		if(subInfoAsn.isSubscriberStatePresent()) {
			SubscriberState ssasn = subInfoAsn.getSubscriberState();
			ssuser.setSubscriberState(decodeAsnToSubState(ssasn));
		}
				
		// Decode LocationInformationGPRS
		if(subInfoAsn.isLocationInformationGPRSPresent()) {
			LocationInformationGPRS locasn = subInfoAsn.getLocationInformationGPRS();
			ssuser.setLocationInformationGPRS(decodeAsnToLocInfoGprs(locasn));
		}
		
		// decode PS-SubscriberState
		if(subInfoAsn.isPs_SubscriberStatePresent()){
			// TODO: need to included when available in codec
			PS_SubscriberState psSubsriberStateasn = subInfoAsn.getPs_SubscriberState();
			ssuser.setPsSubscriberState(decodeAsnToPsSubscriberState(psSubsriberStateasn));
		}
		
		// decode Imei
		if(subInfoAsn.isImeiPresent()) {
			ssuser.setImei(ImeiMap.decode(subInfoAsn.getImei().getValue().getValue()));
		}
		
		// decode MS Class Mark2
		if(subInfoAsn.isMs_Classmark2Present()) {
			ssuser.setMsClassmark2(MsClassMark2Map
					.decode(subInfoAsn.getMs_Classmark2().getValue()));
		}
		
		// decode MNPInfoRes
		if(subInfoAsn.isMnpInfoResPresent()){
			MnpInfoResMap mnpinfo = new MnpInfoResMap();
			MNPInfoRes asn = subInfoAsn.getMnpInfoRes();
			if(asn.isRouteingNumberPresent()) {
				mnpinfo.setRouteingNumber(NonAsnArg.tbcdStringDecoder(
						asn.getRouteingNumber().getValue().getValue(), 0));
			}
			
			if(asn.isImsiPresent()){
				mnpinfo.setImsi(ImsiDataType.decodeImsi(asn.getImsi().getValue().getValue()));
			}
			
			if(asn.isMsisdnPresent()){
				mnpinfo.setMsisdn(new ISDNAddressStringMap(asn.getMsisdn().getValue().getValue()));
			}
			
			if(asn.isNumberPortabilityStatusPresent()){
				
				// BUG: BN doesn't set return anything from getIntegerForm() for an enum
				if(asn.getNumberPortabilityStatus().getValue() == 
						NumberPortabilityStatus.EnumType.foreignNumberPortedIn){
					mnpinfo.setNpStatus(NumberPortabilityStatusMapEnum.FOREIGN_NUMBER_PORTED_IN);
								
				}
				else if (asn.getNumberPortabilityStatus().getValue() ==
						NumberPortabilityStatus.EnumType.foreignNumberPortedToForeignNetwork){
					mnpinfo.setNpStatus(NumberPortabilityStatusMapEnum.FOREIGN_NUMBER_PORTED_TO_FOREIGN_NETWORK);
				}
				else if (asn.getNumberPortabilityStatus().getValue() ==
						NumberPortabilityStatus.EnumType.notKnownToBePorted){
					mnpinfo.setNpStatus(NumberPortabilityStatusMapEnum.NOT_KNOWN_TO_BE_PORTED);
				}
				else if (asn.getNumberPortabilityStatus().getValue() ==
						NumberPortabilityStatus.EnumType.ownNumberNotPortedOut){
					mnpinfo.setNpStatus(NumberPortabilityStatusMapEnum.OWN_NUMBER_NOT_PORTED_OUT);
				}
				else if (asn.getNumberPortabilityStatus().getValue() ==
						NumberPortabilityStatus.EnumType.ownNumberPortedOut){
					mnpinfo.setNpStatus(NumberPortabilityStatusMapEnum.OWN_NUMBERPORTED_OUT);
				}
			}
			
			ssuser.setMnpInfo(mnpinfo);
		}
		
		// decode IMS-VoiceOverPS-SessionsInd
		// BUG: BN doesn't set return anything from getIntegerForm() 
		if(subInfoAsn.isImsVoiceOverPS_SessionsIndicationPresent()){
			if(subInfoAsn.getImsVoiceOverPS_SessionsIndication().getValue() == 
					IMS_VoiceOverPS_SessionsInd.EnumType.imsVoiceOverPS_SessionsNotSupported){
				ssuser.setImsVoPsSessionIndication(
						ImsVoiceOverPSSessionsIndMapEnum.IMS_VOICE_OVER_PS_SESSIONS_NOT_SUPPORTED);	
			}
			else
			if(subInfoAsn.getImsVoiceOverPS_SessionsIndication().getValue() == 
				IMS_VoiceOverPS_SessionsInd.EnumType.imsVoiceOverPS_SessionsSupported){
				ssuser.setImsVoPsSessionIndication(
					ImsVoiceOverPSSessionsIndMapEnum.IMS_VOICE_OVER_PS_SESSIONS_SUPPORTED);	
			}
		}
		
		if(subInfoAsn.isLastUE_ActivityTimePresent()){
			ssuser.setLastUeActivityTime(new TimeMap(Integer.parseInt(MapFunctions.decodeNumber(
					subInfoAsn.getLastUE_ActivityTime().getValue(), 0))));
		}
		
		// decode last RAT Type
		// Bug ACM-2184: getIntegerForm() returns null
		if(subInfoAsn.isLastRAT_TypePresent()) {
			if (subInfoAsn.getLastRAT_Type().getValue() ==
					Used_RAT_Type.EnumType.e_utran){
				ssuser.setLastRatType(UsedRATTypeMapEnum.E_UTRAN);
			}
			else
			if (subInfoAsn.getLastRAT_Type().getValue() ==
					Used_RAT_Type.EnumType.gan){
				ssuser.setLastRatType(UsedRATTypeMapEnum.GAN);
			}
			else
			if (subInfoAsn.getLastRAT_Type().getValue() ==
					Used_RAT_Type.EnumType.geran){
				ssuser.setLastRatType(UsedRATTypeMapEnum.GERAN);
			}
			else
			if (subInfoAsn.getLastRAT_Type().getValue() ==
					Used_RAT_Type.EnumType.i_hspa_evolution){
				ssuser.setLastRatType(UsedRATTypeMapEnum.I_HSPA_EVOLUTION);
			}
			else
			if (subInfoAsn.getLastRAT_Type().getValue() ==
					Used_RAT_Type.EnumType.utran){
				ssuser.setLastRatType(UsedRATTypeMapEnum.UTRAN);
			}
			
						
		}
		
		// decode Geographical EPS Subscriber data
		
		if(subInfoAsn.isLocationInformationEPSPresent()) {
			LocationInformationEPS asn = subInfoAsn.getLocationInformationEPS();
			LocationInformationEpsMap user = new LocationInformationEpsMap();
			if(asn.isE_utranCellGlobalIdentityPresent()){
				user.setEUtranCgid(EUtranCellGlobalIdentityMap
						.decode(asn.getE_utranCellGlobalIdentity()));
			}
			
			if(asn.isTrackingAreaIdentityPresent()){
				user.setTrackingAreaIdentity(TrackingAreaIdentityMap
						.decode(asn.getTrackingAreaIdentity()));
			}
			
			if(asn.isGeographicalInformationPresent()){
				user.setGeographicalInformation(GeographicalInformationMap
						.decode(asn.getGeographicalInformation().getValue()));
			}
			
			if(asn.isGeodeticInformationPresent()){
				user.setGeodeticInformation(GeodeticInformationMap.
						decode(asn.getGeodeticInformation().getValue()));
			}
			ssuser.setLocationInfoEps(user);
		}
		
		return ssuser;
	}

	/**
	 * @param ocsiasn
	 * @return
	 * @throws InvalidInputException
	 */
	public static OCsiMap decodeAsnToOCsiMap(O_CSI ocsiasn) 
			throws InvalidInputException {
		
	

		// Mandatory attribute
		O_BcsmCamelTDPDataList listasn = ocsiasn.getO_BcsmCamelTDPDataList();
		Iterator<O_BcsmCamelTDPData> iter = listasn.getValue().iterator();
		ArrayList<OBcsmCamelTDPDataMap> dlist = new ArrayList<OBcsmCamelTDPDataMap>();
		while(iter.hasNext()){
			O_BcsmCamelTDPData fromobj = iter.next();
			OBcsmCamelTDPDataMap toobj = new OBcsmCamelTDPDataMap();
			
			if(fromobj.getO_BcsmTriggerDetectionPoint()!=null){
				toobj.setoBcsmTdp(
						OBcsmTriggerDetectionPointMapEnum.getValue(
								fromobj.getO_BcsmTriggerDetectionPoint().getIntegerForm().intValue()));
			}
			
			if(fromobj.getServiceKey()!= null){
				toobj.setServiceKey(new ServiceKeyMap(
						fromobj.getServiceKey().getValue()));
			}
			
			if(fromobj.getGsmSCF_Address()!=null){
				toobj.setGsmScfAddress(new ISDNAddressStringMap(
						fromobj.getGsmSCF_Address().getValue().getValue()));;
			}
			
			if(fromobj.getDefaultCallHandling() != null){
				toobj.setDefaultCallHandling(DefaultCallHandlingMapEnum.getValue(
						fromobj.getDefaultCallHandling().getIntegerForm().intValue()));
			}
			
			dlist.add(toobj);
		}
		
		OCsiMap ocsiuser = new OCsiMap(dlist);

		if(ocsiasn.isCamelCapabilityHandlingPresent()){
			ocsiuser.setCamelCapabilityHandling(
					CamelCapabilityHandlingMapEnum.getValue(
							ocsiasn.getCamelCapabilityHandling().getValue().intValue()));
		}
		
		return ocsiuser;
	}

	/**
	 * @param dcsiasn
	 * @return
	 * @throws InvalidInputException
	 */
	public static DCsiMap decodeAsnToDCsiMap(D_CSI dcsiasn) throws InvalidInputException {
		DCsiMap dcsiobj = new DCsiMap();
		
		if(dcsiasn.isDp_AnalysedInfoCriteriaListPresent()){
			DP_AnalysedInfoCriteriaList listasn = dcsiasn.getDp_AnalysedInfoCriteriaList();
			Iterator<DP_AnalysedInfoCriterium> iter = listasn.getValue().iterator();
			ArrayList<DPAnalyzedInfoCriteriumMap> listuser = new ArrayList<DPAnalyzedInfoCriteriumMap>();
			
			while(iter.hasNext()){
				DP_AnalysedInfoCriterium elasn = iter.next();
				ISDNAddressStringMap dialledNumber = new ISDNAddressStringMap(elasn.getDialledNumber().getValue().getValue());
				ServiceKeyMap serviceKey = new ServiceKeyMap(elasn.getServiceKey().getValue());
				System.out.println("service key = "+serviceKey);
				ISDNAddressStringMap gsmScfAddress = new ISDNAddressStringMap(elasn.getGsmSCF_Address().getValue().getValue());
				DefaultCallHandlingMapEnum defaultCallHandling = DefaultCallHandlingMapEnum.CONTINUE_CALL;
				if( elasn.getDefaultCallHandling().getValue() == DefaultCallHandling.EnumType.continueCall){
					defaultCallHandling = DefaultCallHandlingMapEnum.CONTINUE_CALL;
				}
				else if(elasn.getDefaultCallHandling().getValue() == DefaultCallHandling.EnumType.releaseCall){
					defaultCallHandling = DefaultCallHandlingMapEnum.RELEASE_CALL;
				}
				DPAnalyzedInfoCriteriumMap eluser = new 
						DPAnalyzedInfoCriteriumMap(dialledNumber, serviceKey, gsmScfAddress, defaultCallHandling);
				
				listuser.add(eluser);
			}
			System.out.println("DIMCSI setting the value ");
			dcsiobj.setDpAnalysedInfoCriteriaList(listuser);
		}
		
		if(dcsiasn.isCamelCapabilityHandlingPresent()){
			dcsiobj.setCamelCapabilityHandling(CamelCapabilityHandlingMapEnum.getValue(
					dcsiasn.getCamelCapabilityHandling().getValue().intValue()));
		}
		
		return dcsiobj;
	}


	/**
	 * @param tcsiasn
	 * @return
	 * @throws InvalidInputException
	 */
	public static TCsiMap decodeAsnToTCsiMap(T_CSI tcsiasn) throws InvalidInputException {

		// Mandatory Attribute
		T_BcsmCamelTDPDataList tdplistasn = tcsiasn.getT_BcsmCamelTDPDataList();
		Iterator<T_BcsmCamelTDPData> asniter = tdplistasn.getValue().iterator();
		ArrayList<TBcsmCamelTDPDataMap> usertdplist = new ArrayList<TBcsmCamelTDPDataMap>();
		
		while(asniter.hasNext()){
			T_BcsmCamelTDPData objfrom = asniter.next();
			TBcsmCamelTDPDataMap objto = new TBcsmCamelTDPDataMap();
			if(objfrom.getT_BcsmTriggerDetectionPoint() != null ){
				objto.settBcsmTdp(
					TBcsmTriggerDetectionPointMapEnum.getValue(
							objfrom.getT_BcsmTriggerDetectionPoint().getIntegerForm().intValue()));
			}
			
			if(objfrom.getServiceKey() != null){
				objto.setServiceKey(new ServiceKeyMap(objfrom.getServiceKey().getValue()));
			}
			
			if(objfrom.getGsmSCF_Address() != null){
				objto.setGsmScfAddress(new ISDNAddressStringMap(
						objfrom.getGsmSCF_Address().getValue().getValue()));
			}
			
			if(objfrom.getDefaultCallHandling() != null){
				objto.setDefaultCallHandling(
						DefaultCallHandlingMapEnum.getValue(
								objfrom.getDefaultCallHandling().getIntegerForm().intValue()));
			}
		
			usertdplist.add(objto);
		}
		
		TCsiMap tcsiobj = new TCsiMap(usertdplist);
		
		if(tcsiasn.isCamelCapabilityHandlingPresent()){
			tcsiobj.setCamelCapabilityHandling(CamelCapabilityHandlingMapEnum.getValue(
					tcsiasn.getCamelCapabilityHandling().getValue().intValue()));
		}
		
		return tcsiobj;
	}

	/**
	 * @param listasn
	 * @return
	 * @throws InvalidInputException
	 */
	public static Collection<TBcsmCamelTDPCriteriaMap> decodeAsnToTBcsmCamelTdpCriteriaList(
			Collection<T_BCSM_CAMEL_TDP_Criteria> listasn) throws InvalidInputException {
		
		ArrayList<TBcsmCamelTDPCriteriaMap> userlist = new ArrayList<TBcsmCamelTDPCriteriaMap>();
		
		Iterator<T_BCSM_CAMEL_TDP_Criteria> iter = listasn.iterator();
		
		while(iter.hasNext()){
			T_BCSM_CAMEL_TDP_Criteria fromobj = iter.next();
			TBcsmCamelTDPCriteriaMap toobj = new TBcsmCamelTDPCriteriaMap();
			
			if(fromobj.getT_BCSM_TriggerDetectionPoint() != null){
				toobj.settBcsmTriggerDetectionPoint(
						TBcsmTriggerDetectionPointMapEnum.getValue(
						fromobj.getT_BCSM_TriggerDetectionPoint().getIntegerForm().intValue()));
			}
			
			if(fromobj.getT_CauseValueCriteria() != null) {
				T_CauseValueCriteria tsn = fromobj.getT_CauseValueCriteria();
				Iterator<CauseValue> iterasn = tsn.getValue().iterator();
				ArrayList<Cause> clist = new ArrayList<Cause>();
				while(iterasn.hasNext()){
					CauseValue cv = iterasn.next();
					Cause cause = new Cause();
					cause.setCauseValEnum(
							CauseValEnum.fromInt(Integer.valueOf(MapFunctions.decodeNumber(cv.getValue(), 0))));

					clist.add(cause);
				}
				toobj.settCauseValueCriteria(clist);
			}
			
			userlist.add(toobj);
		}
		return userlist;
	}

	/**
	 * @param tdplistasn
	 * @return
	 * @throws InvalidInputException
	 */
	public static Collection<OBcsmCamelTDPCriteriaMap> decodeAsnToOBcsmCamelTdpCriteriaList(
			Collection<O_BcsmCamelTDP_Criteria> tdplistasn) throws InvalidInputException {
		Iterator<O_BcsmCamelTDP_Criteria> asniter = tdplistasn.iterator();
		ArrayList<OBcsmCamelTDPCriteriaMap> usertdplist = new ArrayList<OBcsmCamelTDPCriteriaMap>();
		while(asniter.hasNext()){
			O_BcsmCamelTDP_Criteria objfrom = asniter.next();
			OBcsmCamelTDPCriteriaMap objto = new OBcsmCamelTDPCriteriaMap();
			if(objfrom.isDestinationNumberCriteriaPresent()){
				DestinationNumberCriteriaMap dcmap = new DestinationNumberCriteriaMap();
				DestinationNumberCriteria dcasn = objfrom.getDestinationNumberCriteria();
				
				if(dcasn.isDestinationNumberListPresent()){
					ArrayList<ISDNAddressStringMap> dnlist = new ArrayList<ISDNAddressStringMap>();
					Collection<ISDN_AddressString> asnlist = dcasn.getDestinationNumberList().getValue();
					Iterator<ISDN_AddressString> iter = asnlist.iterator();
					while(iter.hasNext()){
						dnlist.add(new ISDNAddressStringMap(iter.next().getValue().getValue()));
					}
					dcmap.setDestinationNumberList(dnlist);
				}
				objto.setDestinationNumberCriteria(dcmap);
			}
			
			if(objfrom.isCallTypeCriteriaPresent()){
				objto.setCallTypeCriteria(CallTypeCriteriaMapEnum.getValue(
						objfrom.getCallTypeCriteria().getIntegerForm().intValue()));
			}
			
			if(objfrom.isO_CauseValueCriteriaPresent()){
				Iterator<CauseValue> iter = objfrom.getO_CauseValueCriteria().getValue().iterator();
				ArrayList<Cause> clist = new ArrayList<Cause>();
				while(iter.hasNext()){
					CauseValue cvasn = iter.next();
					clist.add(Cause.decodeCauseVal(cvasn.getValue()));
				}
				objto.setoCauseValueCriteria(clist);
			}
			
			if(objfrom.getO_BcsmTriggerDetectionPoint() != null){
				O_BcsmTriggerDetectionPoint odpasn = objfrom.getO_BcsmTriggerDetectionPoint();
				objto.setoBcsmTriggerDetectionPoint(
						OBcsmTriggerDetectionPointMapEnum.getValue(
								odpasn.getIntegerForm().intValue()));
						
			}
		
			usertdplist.add(objto);
		}
	
		return usertdplist;
	}

	/**
	 * @param smscsiasn
	 * @return
	 * @throws InvalidInputException
	 */
	public static SmsCsiMap decodeAsnToSmsCsiMap(SMS_CSI smscsiasn) throws InvalidInputException{
		SmsCsiMap smscsiuser = new SmsCsiMap();
		
		if(smscsiasn.isSms_CAMEL_TDP_DataListPresent()){
			
			SMS_CAMEL_TDP_DataList listasn = smscsiasn.getSms_CAMEL_TDP_DataList();
			Iterator<SMS_CAMEL_TDP_Data> asniter = listasn.getValue().iterator();
			ArrayList<SmsCamelTdpDataMap> userlist = new ArrayList<SmsCamelTdpDataMap>();
			
			while(asniter.hasNext()){
				SMS_CAMEL_TDP_Data objfrom = asniter.next();
				SmsCamelTdpDataMap objto = new SmsCamelTdpDataMap();
				if(objfrom.getSms_TriggerDetectionPoint() != null ){
					objto.setSmsTriggerDetectionPoint(
						SmsTriggerDetectionPointMapEnum.getValue(
								objfrom.getSms_TriggerDetectionPoint().getIntegerForm().intValue()));
				}
				
				if(objfrom.getServiceKey() != null){
					objto.setServiceKey(new ServiceKeyMap(objfrom.getServiceKey().getValue()));
				}
				
				if(objfrom.getGsmSCF_Address() != null){
					objto.setGsmScfAddr(new ISDNAddressStringMap(
							objfrom.getGsmSCF_Address().getValue().getValue()));
				}
				
				if(objfrom.getDefaultSMS_Handling() != null){
					objto.setDefaultSMSHandling(
							DefaultSmsHandlingMapEnum.getValue(
									objfrom.getDefaultSMS_Handling().getIntegerForm().intValue()));
				}
			
				userlist.add(objto);
			}
		
			smscsiuser.setSmsCamelTdpDataList(userlist);
		}
		
		if(smscsiasn.isCamelCapabilityHandlingPresent()){
			smscsiuser.setCamelCapabilityHandling(CamelCapabilityHandlingMapEnum.getValue(
					smscsiasn.getCamelCapabilityHandling().getValue().intValue()));
		}
	
		return smscsiuser;
	}


	/**
	 * @param ssinAsn
	 * @return
	 * @throws InvalidInputException
	 */
	public static SSInvocationNotificationArgMap decodeAsnToSSINArg(SSInvocationNotificationArg ssinAsn) 
			throws InvalidInputException {

			ImsiDataType imsi = ImsiDataType.decodeImsi(ssinAsn.getImsi().getValue().getValue());
			ISDNAddressStringMap msisdn = new ISDNAddressStringMap(ssinAsn.getMsisdn().getValue().getValue());
			SS_Code ssCode = ssinAsn.getSs_Event();
			SsCodeMap ssCodeMap = new SsCodeMap();
			SupplementaryServicesMapEnum var = null;

			int ssCodeVal = (int)(ssCode.getValue()[0] & 0xFF);
			var = SupplementaryServicesMapEnum.getValue(ssCodeVal);

			if (null == var)
			{
				logger.error("Mandatory Parameter SS-Code missing: "+ ssCodeVal);
				throw new InvalidInputException("Mandatory Parameter SS-Code missing");
			}
			else
			{
				ssCodeMap.setSsCode(var);
			}
				
			SS_EventSpecification ssEventMap = null;
			Collection<AddressString> addrEvt = null;
			SSEventSpecificationMap addrMap = null;
			Collection<AddressStringMap> addrEvtMap = null;

			if (ssinAsn.isSs_EventSpecificationPresent()) {
				ssEventMap = ssinAsn.getSs_EventSpecification();
				addrEvt = ssEventMap.getValue();
				addrMap = new SSEventSpecificationMap();
				addrEvtMap = addrMap.getSsEventSpecificationList();

				Iterator<AddressString> iter = addrEvt.iterator();
				while(iter.hasNext())
				{
					AddressStringMap addrMap1 = new AddressStringMap(iter.next().getValue());
					addrEvtMap.add(addrMap1);
				}
			}

			ISDNAddressStringMap bSubsNum = null;
			if (ssinAsn.isB_subscriberNumberPresent()) {
				 bSubsNum = new ISDNAddressStringMap(ssinAsn.getB_subscriberNumber().getValue().getValue());
			}

			CCBS_RequestState ccbsReqState = null;
			CCBSRequestStateEnum ccbsRequestStateEnum = null;

			if (ssinAsn.isCcbs_RequestStatePresent()) {
				ccbsReqState = ssinAsn.getCcbs_RequestState();

				if (ccbsReqState.getValue() == CCBS_RequestState.EnumType.request)
				{
					ccbsRequestStateEnum = CCBSRequestStateEnum.REQUEST;
				}
				else  if (ccbsReqState.getValue() == CCBS_RequestState.EnumType.recall)
				{
					ccbsRequestStateEnum = CCBSRequestStateEnum.RECALL;
				}
				else if (ccbsReqState.getValue() == CCBS_RequestState.EnumType.active)
				{
					ccbsRequestStateEnum = CCBSRequestStateEnum.ACTIVE;
				}
				else if (ccbsReqState.getValue() == CCBS_RequestState.EnumType.completed)
				{
					ccbsRequestStateEnum = CCBSRequestStateEnum.COMPLETED;
				}
				else if (ccbsReqState.getValue() == CCBS_RequestState.EnumType.suspended)
				{
					ccbsRequestStateEnum = CCBSRequestStateEnum.SUSPENDED;
				}
				else if (ccbsReqState.getValue() == CCBS_RequestState.EnumType.frozen)
				{
					ccbsRequestStateEnum = CCBSRequestStateEnum.FROZEN;
				}
				else if (ccbsReqState.getValue() == CCBS_RequestState.EnumType.deleted)
				{
					ccbsRequestStateEnum = CCBSRequestStateEnum.DELETED;
				}
			}

			SSInvocationNotificationArgMap response = new SSInvocationNotificationArgMap(
					imsi, msisdn, ssCodeMap,addrMap, bSubsNum, ccbsRequestStateEnum);
			return response;
		}

	/**
	 * @param mcsiasn
	 * @return
	 * @throws InvalidInputException
	 */
	public static MCsiMap decodeAsntoMCsiMap(M_CSI mcsiasn) throws InvalidInputException {
		ArrayList<MmCodeMap> mobtriggers = new ArrayList<MmCodeMap>();
		
		if(mcsiasn.getMobilityTriggers() != null){
			Iterator<MM_Code> iter = mcsiasn.getMobilityTriggers().getValue().iterator();
			
			while(iter.hasNext()){
				MM_Code fromobj = iter.next();
				MmCodeMap toobj = MmCodeMap.decode(fromobj.getValue());
				mobtriggers.add(toobj);
			}
		}
		
		ServiceKeyMap sk = null; 
		if(mcsiasn.getServiceKey() != null){
			sk = new ServiceKeyMap(mcsiasn.getServiceKey().getValue().intValue());
		}
		
		ISDNAddressStringMap gsmscfaddr = null;
		if(mcsiasn.getGsmSCF_Address() != null){
			gsmscfaddr = new ISDNAddressStringMap(mcsiasn.getGsmSCF_Address().getValue().getValue());
		}
		
		MCsiMap mcsiuser = new MCsiMap(mobtriggers, sk, gsmscfaddr);

		
		return mcsiuser;
	}

	/**
	 * @param csiasn
	 * @return
	 * @throws InvalidInputException
	 */
	public static CamelSubscriptionInfoMap decodeAsnToCamelSubsInfo(CAMEL_SubscriptionInfo csiasn)
			throws InvalidInputException{
		CamelSubscriptionInfoMap csiobj = new CamelSubscriptionInfoMap();

		if(csiasn.isO_BcsmCamelTDP_CriteriaListPresent()) {///////////////
			Collection<O_BcsmCamelTDP_Criteria> tdplistasn = csiasn.getO_BcsmCamelTDP_CriteriaList().getValue();
			Collection<OBcsmCamelTDPCriteriaMap> usertdplist = decodeAsnToOBcsmCamelTdpCriteriaList(tdplistasn);
			csiobj.setoBcsmCamelTdpCriteriaList(usertdplist);
		}

		if(csiasn.isD_IM_CSIPresent()) {
			System.out.println("dimcs is set");
			D_CSI dimcsiasn = csiasn.getD_IM_CSI();
			DCsiMap dimcsiuser = decodeAsnToDCsiMap(dimcsiasn);
			csiobj.setdImCsi(dimcsiuser);
		}


		if(csiasn.isO_CSIPresent()){
			O_CSI ocsiasn = csiasn.getO_CSI();
			OCsiMap ocsiuser = decodeAsnToOCsiMap(ocsiasn);
			csiobj.setOcsi(ocsiuser);

		}

		if(csiasn.isD_CSIPresent()){
			D_CSI dcsiasn = csiasn.getD_CSI();
			DCsiMap dcsiuser = decodeAsnToDCsiMap(dcsiasn);
			csiobj.setDcsi(dcsiuser);
		}

		if(csiasn.isT_CSIPresent()){
			T_CSI tcsiasn = csiasn.getT_CSI();
			TCsiMap tcsiuser = decodeAsnToTCsiMap(tcsiasn);
			csiobj.setTcsi(tcsiuser);
		}

		if(csiasn.isT_BCSM_CAMEL_TDP_CriteriaListPresent()){
			Collection<T_BCSM_CAMEL_TDP_Criteria>  listasn = csiasn.getT_BCSM_CAMEL_TDP_CriteriaList().getValue();

			Collection<TBcsmCamelTDPCriteriaMap>  listuser= decodeAsnToTBcsmCamelTdpCriteriaList(listasn);
			csiobj.settBcsmCamelTdpCriteriaList(listuser);
		}

		if(csiasn.isVt_CSIPresent()){
			T_CSI vtcsiasn = csiasn.getVt_CSI();
			TCsiMap vtcsiuser = decodeAsnToTCsiMap(vtcsiasn);
			csiobj.setVtcsi(vtcsiuser);
		}

		if(csiasn.isVt_BCSM_CAMEL_TDP_CriteriaListPresent()){
			Collection<T_BCSM_CAMEL_TDP_Criteria> listasn = csiasn.getVt_BCSM_CAMEL_TDP_CriteriaList().getValue();
			Collection<TBcsmCamelTDPCriteriaMap>  listuser= decodeAsnToTBcsmCamelTdpCriteriaList(listasn);
			csiobj.setVtBcsmCamelTdpCriteriaList(listuser);
		}

		if(csiasn.isVt_IM_BCSM_CAMEL_TDP_CriteriaListPresent()){
			Collection<T_BCSM_CAMEL_TDP_Criteria> listasn = csiasn.getVt_IM_BCSM_CAMEL_TDP_CriteriaList().getValue();
			Collection<TBcsmCamelTDPCriteriaMap>  listuser= decodeAsnToTBcsmCamelTdpCriteriaList(listasn);
			csiobj.setVtImBcsmCamelTdpCriteriaList(listuser);
		}

		if(csiasn.isO_IM_BcsmCamelTDP_CriteriaListPresent()){
			Collection<O_BcsmCamelTDP_Criteria> tdplistasn = csiasn.getO_IM_BcsmCamelTDP_CriteriaList().getValue();
			Collection<OBcsmCamelTDPCriteriaMap> usertdplist = decodeAsnToOBcsmCamelTdpCriteriaList(tdplistasn);
			csiobj.setoBcsmCamelTdpCriteriaList(usertdplist);
		}

		if(csiasn.isD_CSIPresent()){
			D_CSI dcsiasn = csiasn.getD_CSI();
			DCsiMap dcsiuser = decodeAsnToDCsiMap(dcsiasn);
			csiobj.setDcsi(dcsiuser);
		}

		if(csiasn.isVt_CSIPresent()){
			T_CSI tcsiasn = csiasn.getVt_CSI();
			TCsiMap tcsiuser = decodeAsnToTCsiMap(tcsiasn);
			csiobj.setVtcsi(tcsiuser);
		}

		if(csiasn.isO_IM_CSIPresent()){
			O_CSI ocsiasn = csiasn.getO_IM_CSI();
			OCsiMap ocsiuser = decodeAsnToOCsiMap(ocsiasn);
			csiobj.setoImCsi(ocsiuser);
		}

		if(csiasn.isMo_sms_CSIPresent()){
			SMS_CSI smscsiasn = csiasn.getMo_sms_CSI();
			SmsCsiMap smscsiuser = decodeAsnToSmsCsiMap(smscsiasn);
			csiobj.setMoSmscsi(smscsiuser);
		}

		if(csiasn.isMt_sms_CSIPresent()){
			SMS_CSI smscsiasn = csiasn.getMt_sms_CSI();
			SmsCsiMap smscsiuser = decodeAsnToSmsCsiMap(smscsiasn);
			csiobj.setMtSmsCSI(smscsiuser);
		}

		if(csiasn.isSpecificCSIDeletedListPresent()){
			SpecificCSI_Withdraw swasn = csiasn.getSpecificCSIDeletedList();
			csiobj.setSpecificCSIDeletedList(SpecificCSIWithdrawMap.decode(
					swasn.getValue().getValue()));
		}

		if(csiasn.isSs_CSIPresent()){
			SS_CSI sscsiasn = csiasn.getSs_CSI();

				ArrayList<SsCodeMap> eventList = new ArrayList<SsCodeMap>();

				SS_CamelData sscdata =sscsiasn.getSs_CamelData();
				Iterator<SS_Code> iter = sscdata.getSs_EventList().getValue().iterator();
				
				while(iter.hasNext()){

					SS_Code fromobj = iter.next();
					SsCodeMap toobj = SsCodeMap.decode(fromobj.getValue());
					eventList.add(toobj);
				}
				
				ISDNAddressStringMap gsmScfAddr = new ISDNAddressStringMap(
						sscsiasn.getSs_CamelData().getGsmSCF_Address().getValue().getValue());

				SsCamelDataMap sscduser = new SsCamelDataMap(eventList, gsmScfAddr);
				
				SsCsiMap sscsiuser = new SsCsiMap(sscduser);

				csiobj.setSscsi(sscsiuser);
			}
			
			if(csiasn.isGprs_CSIPresent()){
				GPRS_CSI gprscsiasn = csiasn.getGprs_CSI();
				GprsCsiMap gprscsiuser = decodeAsntoGprsCsiMap(gprscsiasn);
				csiobj.setGprscsi(gprscsiuser);
			}
			
			
			if(csiasn.isM_CSIPresent()){
				M_CSI mcsiasn = csiasn.getM_CSI();
				MCsiMap mcsiuser = decodeAsntoMCsiMap(mcsiasn);
				csiobj.setMcsi(mcsiuser);
			}
			
			if(csiasn.isMg_csiPresent()){
				MG_CSI mgasn = csiasn.getMg_csi();
				M_CSI masn = new M_CSI();
				masn.setMobilityTriggers(mgasn.getMobilityTriggers());
				masn.setGsmSCF_Address(mgasn.getGsmSCF_Address());
				masn.setServiceKey(mgasn.getServiceKey());
				MCsiMap mcsiuser = decodeAsntoMCsiMap(masn);
				
				MgCsiMap mguser = new MgCsiMap(mcsiuser.getMobilityTriggers(), mcsiuser.getServiceKey(), mcsiuser.getGsmScfAddress());
				csiobj.setMgcsi(mguser);
			}
			
			return csiobj;
	}

	/**
	 * @param atmres
	 * @return
	 * @throws InvalidInputException
	 */
	public static AnyTimeModificationResMap decodeAsnToAtmRes(AnyTimeModificationRes atmres)
	throws InvalidInputException{
		AnyTimeModificationResMap response = new AnyTimeModificationResMap();
		
		if(atmres.isCamel_SubscriptionInfoPresent()){
			CAMEL_SubscriptionInfo csiasn = atmres.getCamel_SubscriptionInfo();
			CamelSubscriptionInfoMap csiobj = decodeAsnToCamelSubsInfo(csiasn);
			response.setCamelSubscriptionInfo(csiobj);
		}
		
		if(atmres.isOdb_InfoPresent()){
			ODB_Info odbasn = atmres.getOdb_Info();
			ODBInfoMap odbuser = decodeAsnToOdbInfo(odbasn);
			response.setOdbInfo(odbuser);
		}
		
		if(atmres.isSs_InfoFor_CSEPresent()){
			Ext_SS_InfoFor_CSE asnobj = atmres.getSs_InfoFor_CSE();
			
			ExtSsInfoForCseMap userobj = null;
			
			// Choice option 
			if(asnobj.isCallBarringInfoFor_CSESelected()){
				Ext_CallBarringInfoFor_CSE extcbasn = asnobj.getCallBarringInfoFor_CSE();
				SsCodeMap sscode = SsCodeMap.decode(extcbasn.getSs_Code().getValue());
				Collection<ExtCallBarringFeatureMap> cblist = decodeAsnToExtCallBarringFeature(extcbasn);
				ExtCallBarringInfoForCSEMap cbinfo = new ExtCallBarringInfoForCSEMap(sscode, cblist);
				
				if(extcbasn.isPasswordPresent()){
					cbinfo.setPassword(extcbasn.getPassword().getValue());
				}
				
				if(extcbasn.isWrongPasswordAttemptsCounterPresent()){
					cbinfo.setWrongPasswordAttemptsCounter(extcbasn.getWrongPasswordAttemptsCounter().getValue());
				}
				userobj = new ExtSsInfoForCseMap(cbinfo);
			}
			else 
			if(asnobj.isForwardingInfoFor_CSESelected()){
				Ext_ForwardingInfoFor_CSE extcfasn = asnobj.getForwardingInfoFor_CSE();
				SsCodeMap sscode = SsCodeMap.decode(extcfasn.getSs_Code().getValue());
				Iterator<Ext_ForwFeature> iter = extcfasn.getForwardingFeatureList().getValue().iterator();
				ArrayList<ExtForwFeatureMap> infolist  = new ArrayList<ExtForwFeatureMap>();
				
				while(iter.hasNext()){
					Ext_ForwFeature elasn = iter.next();
					ExtSsStatusMap status = ExtSsStatusMap.decode(elasn.getSs_Status().getValue());
					ExtForwFeatureMap eluser = new ExtForwFeatureMap(status);
					
					if(elasn.isBasicServicePresent()){
						eluser.setBasicService(decodeAsnToExtBasicServiceCodeMap(elasn.getBasicService()));
					}
					if(elasn.isForwardedToNumberPresent()){
						eluser.setForwardedToNumber(new ISDNAddressStringMap(elasn.getForwardedToNumber().getValue().getValue()));
					}
					if(elasn.isForwardedToSubaddressPresent()){
						eluser.setForwardedToNumber(new ISDNAddressStringMap(elasn.getForwardedToSubaddress().getValue()));
					}
					if(elasn.isForwardingOptionsPresent()){
						eluser.setForwardingOptions(new ExtForwOptionsMap(elasn.getForwardingOptions().getValue()));
					}
					if(elasn.isLongForwardedToNumberPresent()){
						eluser.setLongForwardedToNumber(new FtnAddressStringMap(elasn.getLongForwardedToNumber().getValue().getValue()));
					}
					if(elasn.isNoReplyConditionTimePresent()){
						eluser.setNoReplyConditionTime(elasn.getNoReplyConditionTime().getValue());
					}
					infolist.add(eluser);
				}
				
				userobj = new ExtSsInfoForCseMap(new ExtForwardingInfoForCSEMap(sscode, infolist));
			}
			
			response.setSsInfoForCse(userobj);
		}

		return response;
	}
	
	public static ODBInfoMap decodeAsnToOdbInfo(ODB_Info odbasn) throws InvalidInputException {
		
		ODB_Data odbdatasn = odbasn.getOdb_Data();

		ODBDataMap odbuser = new ODBDataMap(ODBGeneralDataMap.decode(
				odbdatasn.getOdb_GeneralData().getValue().getValue()));
		if(odbdatasn.isOdb_HPLMN_DataPresent()){
			odbuser.setOdbHplmnData(ODBHplmnDataMap.decode(
					odbdatasn.getOdb_HPLMN_Data().getValue().getValue()));
		}

		ODBInfoMap odbinfo = new ODBInfoMap();
		odbinfo.setOdbData(odbuser);
		return odbinfo;
	}

	/**
	 * @param asnobj
	 * @return
	 * @throws InvalidInputException
	 */
	public static Collection<ExtCallBarringFeatureMap> decodeAsnToExtCallBarringFeature(Ext_CallBarringInfoFor_CSE asnobj)
	throws InvalidInputException {
		
		Iterator<Ext_CallBarringFeature> iter = asnobj.getCallBarringFeatureList().getValue().iterator();   
		ArrayList<ExtCallBarringFeatureMap> extcblist = new ArrayList<ExtCallBarringFeatureMap>();

		while(iter.hasNext()){
			Ext_CallBarringFeature fromobj = iter.next();
			ExtCallBarringFeatureMap userobj = new ExtCallBarringFeatureMap(
					ExtSsStatusMap.decode(fromobj.getSs_Status().getValue()));
			
			// get optional parameter
			if(fromobj.isBasicServicePresent()){
				Ext_BasicServiceCode ebasicasn = fromobj.getBasicService();
				ExtBasicServiceCodeMap ebasicuser = decodeAsnToExtBasicServiceCodeMap(ebasicasn);
				userobj.setBasicService(ebasicuser);
			}
			
			extcblist.add(userobj);
		}
		
		return extcblist;
	}

	/**
	 * @param asnobj
	 * @return
	 * @throws InvalidInputException
	 */
	public static CallBarringDataMap decodeAsnToCallBarringData(CallBarringData asnobj)
	throws InvalidInputException {
		Ext_CallBarFeatureList extcblistasn = asnobj.getCallBarringFeatureList();
		Iterator<Ext_CallBarringFeature> asniter = extcblistasn.getValue().iterator();
		
		ArrayList<ExtCallBarringFeatureMap> extcblist = new ArrayList<ExtCallBarringFeatureMap>();

		while(asniter.hasNext()){
			Ext_CallBarringFeature fromobj = asniter.next();
			ExtCallBarringFeatureMap userobj = new ExtCallBarringFeatureMap(
					ExtSsStatusMap.decode(fromobj.getSs_Status().getValue()));
			extcblist.add(userobj);
		}
		
		CallBarringDataMap userobj = new CallBarringDataMap(extcblist);
		if(asnobj.isPasswordPresent()){
			userobj.setPassword(asnobj.getPassword().getValue());
		}
		
		if(asnobj.isWrongPasswordAttemptsCounterPresent()){
			userobj.setWrongPasswordAttemptsCounter(asnobj.getWrongPasswordAttemptsCounter().getValue());
		}
		
		return userobj;
	}

	/**
	 * @param nsdmasn
	 * @return
	 * @throws InvalidInputException
	 */
	public static NoteSubscriberDataModifiedArgMap decodeAsnToNsdmArg(NoteSubscriberDataModifiedArg nsdmasn) 
	throws InvalidInputException{
		NoteSubscriberDataModifiedArgMap response = null;
		
		// mandatory param from nsdm arg
		ImsiDataType imsi = ImsiDataType.decodeImsi(nsdmasn.getImsi().getValue().getValue());
		ISDNAddressStringMap msisdn = new ISDNAddressStringMap(
				nsdmasn.getMsisdn().getValue().getValue());
		

		// optional params
		if(nsdmasn.isCallBarringInfoFor_CSEPresent()) {
			Ext_CallBarringInfoFor_CSE ecbasn = nsdmasn.getCallBarringInfoFor_CSE();
			SsCodeMap sscode = SsCodeMap.decode(ecbasn.getSs_Code().getValue());
			
			Iterator<Ext_CallBarringFeature> iter = ecbasn.getCallBarringFeatureList().getValue().iterator(); 
			ArrayList<ExtCallBarringFeatureMap> cblist = new ArrayList<ExtCallBarringFeatureMap>();
			
			while(iter.hasNext()) {
				Ext_CallBarringFeature easn = iter.next();
				
				// obtain the mandatory ss-status attribute
				ExtSsStatusMap ssStatus = ExtSsStatusMap.decode(easn.getSs_Status().getValue());
				
				// Create the user object with mandatory object
				ExtCallBarringFeatureMap euser = new ExtCallBarringFeatureMap(ssStatus);
				
				// Check if optional attribute is present
				if(easn.isBasicServicePresent()) {
					Ext_BasicServiceCode ebasicasn = easn.getBasicService();
					ExtBasicServiceCodeMap ebasicuser = decodeAsnToExtBasicServiceCodeMap(ebasicasn);
					euser.setBasicService(ebasicuser);
				}
				cblist.add(euser);
			}
			// Create the object with mandatory params
			ExtCallBarringInfoForCSEMap ecbuser = new ExtCallBarringInfoForCSEMap(sscode, cblist);
			
			response.setCallBarringInfoForCSE(ecbuser);
		}
		
		if(nsdmasn.isForwardingInfoFor_CSEPresent()){
			Ext_ForwardingInfoFor_CSE efiasn = nsdmasn.getForwardingInfoFor_CSE();
			
			// mandatory param sscode
			SsCodeMap sscode = SsCodeMap.decode(efiasn.getSs_Code().getValue());
			Iterator<Ext_ForwFeature> iter = efiasn.getForwardingFeatureList().getValue().iterator();
			
			// mandatory param Collection<ExtForwFeatureMap>
			ArrayList<ExtForwFeatureMap> efflist = new ArrayList<ExtForwFeatureMap>();

			while(iter.hasNext()){
				Ext_ForwFeature effasn = iter.next();
				ExtSsStatusMap ssStatus = ExtSsStatusMap.decode(effasn.getSs_Status().getValue());
				// create object with mandatory param
				ExtForwFeatureMap effuser = new ExtForwFeatureMap(ssStatus);
				
				// check for all optional params
				if(effasn.isBasicServicePresent()){
					Ext_BasicServiceCode ebasicasn = effasn.getBasicService();
					effuser.setBasicService(decodeAsnToExtBasicServiceCodeMap(ebasicasn));
				}
				
				if(effasn.isForwardedToNumberPresent()){
					effuser.setForwardedToNumber(new ISDNAddressStringMap(
							effasn.getForwardedToNumber().getValue().getValue()));
				}
				
				if(effasn.isForwardedToSubaddressPresent()){
					effuser.setForwardedToSubaddress(new ISDNAddressStringMap(
							effasn.getForwardedToSubaddress().getValue()));
				}
				
				if(effasn.isForwardingOptionsPresent()){
					effuser.setForwardingOptions(new ExtForwOptionsMap(
							effasn.getForwardingOptions().getValue()));
				}
				
				if(effasn.isNoReplyConditionTimePresent()){
					effuser.setNoReplyConditionTime(effasn.getNoReplyConditionTime().getValue());
				}
				
				if(effasn.isLongForwardedToNumberPresent()){
					effuser.setLongForwardedToNumber(new FtnAddressStringMap(
							effasn.getLongForwardedToNumber().getValue().getValue()));
				}
				
				efflist.add(effuser);
			}
			
			ExtForwardingInfoForCSEMap efiuser = new ExtForwardingInfoForCSEMap(sscode, efflist);
			
			response.setForwardingInfoForCSE(efiuser);
		}
		
		if(nsdmasn.isOdb_InfoPresent()){
			response.setOdbInfo(decodeAsnToOdbInfo(nsdmasn.getOdb_Info()));
		}
		
		if(nsdmasn.isCamel_SubscriptionInfoPresent()) {
			response.setCsi(decodeAsnToCamelSubsInfo(nsdmasn.getCamel_SubscriptionInfo()));
		}
		
		if(nsdmasn.isUe_reachablePresent()){
			RequestedNodesMap node = RequestedNodesMap.decode(
					nsdmasn.getUe_reachable().getValue().getValue());
			response.setUeReachable(node);
		}
		
		return response; 
	}

	/**
	 * @param ebasicasn
	 * @return
	 * @throws InvalidInputException
	 */
	public static ExtBasicServiceCodeMap decodeAsnToExtBasicServiceCodeMap(Ext_BasicServiceCode ebasicasn)
	throws InvalidInputException
	{
		ExtBasicServiceCodeMap ebasicuser = null;
		if(ebasicasn.isExt_BearerServiceSelected()) {
			ExtBearerServiceCodeMap bearer = 
					ExtBearerServiceCodeMap.decode(
							ebasicasn.getExt_BearerService().getValue());
			
			ebasicuser = new ExtBasicServiceCodeMap(bearer);
		}
		else if(ebasicasn.isExt_TeleserviceSelected()){
			ExtTeleserviceCodeMap tele = 
					ExtTeleserviceCodeMap.decode(
							ebasicasn.getExt_Teleservice().getValue());
			
			ebasicuser = new ExtBasicServiceCodeMap(tele);
		}
		
		return ebasicuser;
	}

	
	public static GprsCsiMap decodeAsntoGprsCsiMap(GPRS_CSI gprscsi) throws InvalidInputException {
		GprsCsiMap userobj = new GprsCsiMap();
		if(gprscsi.isCamelCapabilityHandlingPresent()){
			userobj.setCamelCapabilityHandling(
					CamelCapabilityHandlingMapEnum.getValue(
							gprscsi.getCamelCapabilityHandling().getValue().intValue()));
		}
		
		if(gprscsi.isGprs_CamelTDPDataListPresent()){
			Iterator<GPRS_CamelTDPData> iter = gprscsi.getGprs_CamelTDPDataList().getValue().iterator();
			ArrayList<GprsCamelTdpDataMap> tdplist = new ArrayList<GprsCamelTdpDataMap>();
			
			while(iter.hasNext()) {
				GPRS_CamelTDPData elasn = iter.next();
				DefaultGPRSHandlingMapEnum callHandling = null;
				
				if(elasn.getDefaultSessionHandling().getValue() == 
						DefaultGPRS_Handling.EnumType.continueTransaction) {
					callHandling = DefaultGPRSHandlingMapEnum.CONTINUE_TRANSACTION;
				}
				else if (elasn.getDefaultSessionHandling().getValue() == 
						DefaultGPRS_Handling.EnumType.releaseTransaction) {
					callHandling  = DefaultGPRSHandlingMapEnum.RELEASE_TRANSACTION;
				}
				
				
				ServiceKeyMap skey =  new ServiceKeyMap(elasn.getServiceKey().getValue());
				
				GprsTriggerDetectionPointMapEnum triggerdp = null;
				
				if (elasn.getGprs_TriggerDetectionPoint().getValue() == 
						GPRS_TriggerDetectionPoint.EnumType.attach) {
					triggerdp = GprsTriggerDetectionPointMapEnum.ATTACH;
				} 
				else
				if (elasn.getGprs_TriggerDetectionPoint().getValue() == 
						GPRS_TriggerDetectionPoint.EnumType.attachChangeOfPosition) {
					triggerdp = GprsTriggerDetectionPointMapEnum.ATTACH_CHANCE_OF_POSITION;
				}
				else
				if (elasn.getGprs_TriggerDetectionPoint().getValue() == 
					GPRS_TriggerDetectionPoint.EnumType.pdp_ContextChangeOfPosition) {
					triggerdp = GprsTriggerDetectionPointMapEnum.PDP_CONTEXT_CHANGE_OF_POSITION;
				}
				else
				if (elasn.getGprs_TriggerDetectionPoint().getValue() == 
					GPRS_TriggerDetectionPoint.EnumType.pdp_ContextEstablishment) {
					triggerdp = GprsTriggerDetectionPointMapEnum.PDP_CONTEXT_ESTABLISHED;
				}
				else
				if (elasn.getGprs_TriggerDetectionPoint().getValue() == 
					GPRS_TriggerDetectionPoint.EnumType.pdp_ContextEstablishmentAcknowledgement) {
					triggerdp = GprsTriggerDetectionPointMapEnum.PDP_CONTEXT_ESTTABLISHMENT_ACK;
				}
				
				ISDNAddressStringMap gsmScfAddr = new ISDNAddressStringMap(elasn.getGsmSCF_Address().getValue().getValue());
				
				GprsCamelTdpDataMap eluser = new GprsCamelTdpDataMap(triggerdp, skey, gsmScfAddr, callHandling);

				tdplist.add(eluser);
			}
			userobj.setGprsCamelTDPDataList(tdplist);
		}
		
		
		return userobj;
	}
	
	public static NumberPortabilityStatusMapEnum decodeAsnToNpStatus(NumberPortabilityStatus asn){
		NumberPortabilityStatusMapEnum npstatus = null;
		// BUG: BN doesn't set return anything from getIntegerForm() for an enum
		if(asn.getValue() == 
				NumberPortabilityStatus.EnumType.foreignNumberPortedIn){
			npstatus = NumberPortabilityStatusMapEnum.FOREIGN_NUMBER_PORTED_IN;
						
		}
		else if (asn.getValue() ==
				NumberPortabilityStatus.EnumType.foreignNumberPortedToForeignNetwork){
			npstatus = NumberPortabilityStatusMapEnum.FOREIGN_NUMBER_PORTED_TO_FOREIGN_NETWORK;
		}
		else if (asn.getValue() ==
				NumberPortabilityStatus.EnumType.notKnownToBePorted){
			npstatus = NumberPortabilityStatusMapEnum.NOT_KNOWN_TO_BE_PORTED;
		}
		else if (asn.getValue() ==
				NumberPortabilityStatus.EnumType.ownNumberNotPortedOut){
			npstatus = NumberPortabilityStatusMapEnum.OWN_NUMBER_NOT_PORTED_OUT;
		}
		else if (asn.getValue() ==
				NumberPortabilityStatus.EnumType.ownNumberPortedOut){
			npstatus = NumberPortabilityStatusMapEnum.OWN_NUMBERPORTED_OUT;
		}
		
		return npstatus;
	}
}

package com.genband.isup.messagetypes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.genband.isup.datatypes.AccessTransport;
import com.genband.isup.datatypes.AdditionalPartyCat;
import com.genband.isup.datatypes.AdditionalPartyCatPair;
import com.genband.isup.datatypes.CalledPartyNum;
import com.genband.isup.datatypes.CallingPartyNum;
import com.genband.isup.datatypes.ContractorNumber;
import com.genband.isup.datatypes.ChargeNumber;
import com.genband.isup.datatypes.DPCInfo;
import com.genband.isup.datatypes.FwCallIndicators;
import com.genband.isup.datatypes.GenericDigits;
import com.genband.isup.datatypes.GenericNumber;
import com.genband.isup.datatypes.OriginalCalledNumber;
import com.genband.isup.datatypes.RedirectingNumber;
import com.genband.isup.datatypes.RedirectionInformation;
import com.genband.isup.datatypes.JurisdictionInfo;
import com.genband.isup.datatypes.NatOfConnIndicators;
import com.genband.isup.datatypes.ScfId;
import com.genband.isup.datatypes.ServiceActivation;
import com.genband.isup.datatypes.TtcCalledINNumber;
import com.genband.isup.datatypes.TtcCarrierInfoTrfr;
import com.genband.isup.datatypes.TtcChargeAreaInfo;
import com.genband.isup.datatypes.UserServiceInfo;
import com.genband.isup.enumdata.CalgPartyCatgEnum;
import com.genband.isup.enumdata.TransmissionMedReqEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.operations.ISUPConstants;
import com.genband.isup.util.NonAsnArg;
import com.genband.isup.util.Util;

/**
 * This class contains parameters for IAM message
 * 
 * @author vgoel
 * 
 */
public class IAMMessage {
	byte[] messageType;

	byte[] natureOfConnIndicators;

	byte[] forwardCallIndicators;

	byte[] callingPartyCategory;

	byte[] tmr;

	byte[] calledPartyNumber;

	byte[] callingPartyNumber;

	byte[] correlationId;

	byte[] scfId;

	byte[] calledINNumber;

	byte[] accessTransport;

	byte[] additionalPartyCategory;

	byte[] carrierInformationTransfer;

	byte[] dpcInfo;

	byte[] genericNumber;

	byte[] chargeAreaInformation;

	byte[] serviceActivation;

	byte[] contractorNumber;

	byte[] redirectingNumber;

	byte[] originalCalledNumber;

	byte[] redirectionInfo;

	byte[] userServiceInfo; // ANSI Parameter

	byte[] jurisdictionInfo; // ANSI Parameter
	
	byte[] chargeNumber;     // ANSI Parameter

	int protocol = 0;          //0 - ISUP_ITUT, 1 - ISUP_ANSI


	public void setUserServiceInfo(byte[] userServiceInfo) {
		this.userServiceInfo = userServiceInfo;
	}
	/**
	 * Map containing unknown optional fields code as keys and field data as
	 * value.
	 */
	Map<Integer, byte[]> otherOptParams = null;

	public Map<Integer, byte[]> getOtherOptParams() {
		return otherOptParams;
	}

	public void setOtherOptParams(Map<Integer, byte[]> otherOptParams) {
		this.otherOptParams = otherOptParams;
	}

	public String getMessageType() {
		return String.valueOf(messageType[0]); // message type will be of one
		// byte only
	}

	public NatOfConnIndicators getNatureOfConnIndicators()
			throws InvalidInputException {
		return NatOfConnIndicators.decodeConnIndicators(natureOfConnIndicators);
	}

	public FwCallIndicators getForwardCallIndicators()
			throws InvalidInputException {
		return FwCallIndicators.decodeFwCallInd(forwardCallIndicators);
	}

	public CalgPartyCatgEnum getCallingPartyCategory()
			throws InvalidInputException {
		return NonAsnArg.decodeCalgPartyCatg(callingPartyCategory);
	}

	public TransmissionMedReqEnum getTmr() throws InvalidInputException {
		return NonAsnArg.decodeTmr(tmr);
	}

	public CalledPartyNum getCalledPartyNumber() throws InvalidInputException {
		return CalledPartyNum.decodeCaldParty(calledPartyNumber);
	}

	public CallingPartyNum getCallingPartyNumber() throws InvalidInputException {
		return CallingPartyNum.decodeCalgParty(callingPartyNumber);
	}

	public GenericDigits getCorrelationId() throws InvalidInputException {
		return GenericDigits.decodeGenericDigits(correlationId);
	}

	public ScfId getScfId() throws InvalidInputException {
		return ScfId.decodeScfId(scfId);
	}

	public TtcCalledINNumber getCalledINNumber() throws InvalidInputException {
		return TtcCalledINNumber.decodeTtcCalledINNum(calledINNumber);
	}

	public AccessTransport getAccessTransport() throws InvalidInputException {
		return AccessTransport.decodeAccessTransport(accessTransport);
	}

	public LinkedList<AdditionalPartyCatPair> getAdditionalPartyCat()
			throws InvalidInputException {
		return AdditionalPartyCat
				.decodeAdditionalPartyCategory(additionalPartyCategory);
	}

	public TtcCarrierInfoTrfr getCarrierInfoTransfer()
			throws InvalidInputException {
		return TtcCarrierInfoTrfr.decodeTtcCarrierInfoTrfr(carrierInformationTransfer);
	}

	public DPCInfo getDPCInfo() throws InvalidInputException {
		return DPCInfo.decodeDPCInfo(dpcInfo);
	}

	public GenericNumber getGenericNumber() 
			throws InvalidInputException {
		return GenericNumber.decodeGenericNum(genericNumber);
	}

	public TtcChargeAreaInfo getChargeAreaInformation() 
			throws InvalidInputException {
		return TtcChargeAreaInfo.decodeTtcChargeAreaInfo(chargeAreaInformation);
	}

	public ServiceActivation getServiceActivation() 
			throws InvalidInputException {
		return ServiceActivation.decodeServiceActivation(serviceActivation);
	}

	public ContractorNumber getContractorNumber() 
			throws InvalidInputException {
		return ContractorNumber.decodeContractorNumber(contractorNumber);
	}

	public RedirectingNumber getRedirectingNumber() 
			throws InvalidInputException {
		return RedirectingNumber.decodeRedirectingNumber(redirectingNumber);
	}

	public OriginalCalledNumber getOriginalCalledNumber() 
			throws InvalidInputException {
		return OriginalCalledNumber.decodeOriginalCalledNumber(originalCalledNumber);
	}

	public RedirectionInformation getRedirectionInfo() 
			throws InvalidInputException {
		return RedirectionInformation.decodeRedirectionInformation(redirectionInfo);
	}

	public UserServiceInfo getUserServiceInfo()
			throws InvalidInputException {
		return UserServiceInfo.decodeUserServiceInfo(userServiceInfo);
	}

	public JurisdictionInfo getJurisdictionInfo()
			throws InvalidInputException {
		return JurisdictionInfo.decodeJurisdictionInfo(jurisdictionInfo);
	}
	
	public ChargeNumber getChargeNumber() 
			throws InvalidInputException {
		return ChargeNumber.decodeChargeNumber(chargeNumber);
	}

	public void setAccessTransport(byte[] accessTransport) {
		this.accessTransport = accessTransport;
	}

	public void setCalledINNumber(byte[] calledINNumber) {
		this.calledINNumber = calledINNumber;
	}

	public void setScfId(byte[] scfId) {
		this.scfId = scfId;
	}

	public void setCorrelationId(byte[] correlationId) {
		this.correlationId = correlationId;
	}

	public void setCallingPartyNumber(byte[] callingPartyNumber) {
		this.callingPartyNumber = callingPartyNumber;
	}

	public void setMessageType(byte[] messageType) {
		this.messageType = messageType;
	}

	public void setNatureOfConnIndicators(byte[] natureOfConnIndicators) {
		this.natureOfConnIndicators = natureOfConnIndicators;
	}

	public void setForwardCallIndicators(byte[] forwardCallIndicators) {
		this.forwardCallIndicators = forwardCallIndicators;
	}

	public void setCallingPartyCategory(byte[] callingPartyCategory) {
		this.callingPartyCategory = callingPartyCategory;
	}

	public void setTmr(byte[] tmr) {
		this.tmr = tmr;
	}

	public void setCalledPartyNumber(byte[] calledPartyNumber) {
		this.calledPartyNumber = calledPartyNumber;
	}

	public void setAdditionalPartyCat(byte[] additionalPartyCat) {
		this.additionalPartyCategory = additionalPartyCat;
	}

	public void setCarrierInfoTransfer(byte[] carrierInfoTransfer) {
		this.carrierInformationTransfer = carrierInfoTransfer;
	}

	public void setDPCInfo(byte[] dpcInfo) {
		this.dpcInfo = dpcInfo;
	}

	public void setGenericNumber(byte[] genericNumber) {
		this.genericNumber = genericNumber; 
	}

	public void setChargeAreaInformation (byte[] chargeAreaInfo) {
		this.chargeAreaInformation = chargeAreaInfo;
	}

	public void setServiceActivation (byte[] serviceActivation) {
		this.serviceActivation = serviceActivation;
	}

	public void setContractorNumber(byte[] contractorNum) {
		this.contractorNumber = contractorNum;
	}

	public void setRedirectingNumber(byte[] redirectingNum) {
		this.redirectingNumber = redirectingNum;
	}

	public void setOriginalCalledNumber(byte[] origCalledNum) {
		this.originalCalledNumber = origCalledNum;
	}

	public void setRedirectionInfo(byte[] redirectionInfo) {
		this.redirectionInfo = redirectionInfo;
	}
	
	public void setChargeNumber(byte[] chargeNumber) {
		this.chargeNumber = chargeNumber;
	}
	
	// getters which return byte[]
	public byte[] getMessageTypeBytes() {
		return messageType; // message type will be of one byte only
	}

	public byte[] getNatureOfConnIndicatorsBytes() {
		return natureOfConnIndicators;
	}

	public byte[] getForwardCallIndicatorsBytes() {
		return forwardCallIndicators;
	}

	public byte[] getCallingPartyCategoryBytes() {
		return callingPartyCategory;
	}

	public byte[] getTmrBytes() {
		return tmr;
	}

	public byte[] getCalledPartyNumberBytes() {
		return calledPartyNumber;
	}

	public byte[] getCallingPartyNumberBytes() {
		return callingPartyNumber;
	}

	public byte[] getCorrelationIdBytes() {
		return correlationId;
	}

	public byte[] getScfIdBytes() {
		return scfId;
	}

	public byte[] getCalledINNumberBytes() {
		return calledINNumber;
	}

	public byte[] getAccessTransportBytes() {
		return accessTransport;
	}

	public byte[] getAdditionalPartyCategoryBytes() {
		return additionalPartyCategory;
	}

	public byte[] getCarrierInfoTransferBytes() {
		return carrierInformationTransfer;
	}

	public byte[] getDpcInfoBytes() {
		return dpcInfo;
	}

	public byte[] getGenericNumberBytes() {
		return genericNumber;
	}

	public byte[] getChargeAreaInformationBytes() {
		return chargeAreaInformation;
	}

	public byte[] getServiceActivationBytes() {
		return serviceActivation;
	}

	public byte[] getContractorNumberBytes() {
		return contractorNumber;
	}

	public byte[] getRedirectingNumberBytes() {
		return redirectingNumber;
	}

	public byte[] getOriginalCalledNumberBytes() {
		return originalCalledNumber;
	}

	public byte[] getRedirectionInfoBytes() {
		return redirectionInfo;
	}

	public byte[] getUserServiceInfoBytes() {
		return userServiceInfo;
	}

	public void setUserServiceInfoByte(byte[] userServiceInfo) {
		this.userServiceInfo = userServiceInfo;
	}

	public int getProtocol() {
		return protocol;
	}

	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}

	public byte[] getJurisdictionInfoByte() {
		return jurisdictionInfo;
	}

	public void setJurisdictionInfoByte(byte[] jurisdictionInfo) {
		this.jurisdictionInfo = jurisdictionInfo;
	}

	public byte[] getChargeNumberBytes() {
		return chargeNumber;
	}


	public String toString() {
		String obj = "messageType:" + Util.formatBytes(messageType)
				+ ", Protocol:" + protocol + ", natureOfConnIndicators:"
				+ Util.formatBytes(natureOfConnIndicators)
				+ ", forwardCallIndicators:"
				+ Util.formatBytes(forwardCallIndicators)
				+ ", callingPartyCategory:"
				+ Util.formatBytes(callingPartyCategory) + ", tmr:"
				+ Util.formatBytes(tmr) + ", calledPartyNumber:"
				+ Util.formatBytes(calledPartyNumber) + ", callingPartyNumber:"
				+ Util.formatBytes(callingPartyNumber) + ", correlationId:"
				+ Util.formatBytes(correlationId) + ", scfId:"
				+ Util.formatBytes(scfId) + ", calledINNumber:"
				+ Util.formatBytes(calledINNumber) + ", accessTransport:"
				+ Util.formatBytes(accessTransport) + ", dpcInfo:"
				+ Util.formatBytes(dpcInfo) + ", additionalPartyCategory:"
				+ Util.formatBytes(additionalPartyCategory) + ", carrierInformationTransfer:"
				+ Util.formatBytes(carrierInformationTransfer) + ", genericNumber:"
				+ Util.formatBytes(genericNumber) + ", chargeAreaInfo:"
				+ Util.formatBytes(chargeAreaInformation) + ", userServiceInfo:"
				+ Util.formatBytes(userServiceInfo) + ", JurisdictionInfo: "  
				+ Util.formatBytes(jurisdictionInfo)+ ", ChargeNumber: "  
				+ Util.formatBytes(chargeNumber) + ", contractorNumber:"
				+ Util.formatBytes(contractorNumber) + ", redirectingNumber:"
				+ Util.formatBytes(redirectingNumber) + ", originalCalledNum:"
				+ Util.formatBytes(originalCalledNumber) + ", redirectionInfo:"
				+ Util.formatBytes(redirectionInfo) + ", otherOptParams:"
				+ otherOptParams;

		return obj;
	}

	public void setParams(Map<Integer, byte[]> optMap) {
		Iterator<Map.Entry<Integer, byte[]>> it = optMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, byte[]> param = it.next();
			if (param.getKey() == ISUPConstants.CODE_CALLING_PARTY_NUM) {
				this.setCallingPartyNumber(param.getValue());
			} else if (param.getKey() == ISUPConstants.CODE_CORRELATION_ID) {
				this.setCorrelationId(param.getValue());
			} else if (param.getKey() == ISUPConstants.CODE_SCF_ID) {
				this.setScfId(param.getValue());
			} else if (param.getKey() == ISUPConstants.CODE_CALLED_IN_NUMBER) {
				this.setCalledINNumber(param.getValue());
			} else if (param.getKey() == ISUPConstants.CODE_ACCESS_TRANSPORT) {
				this.setAccessTransport(param.getValue());
			} else if (param.getKey() == ISUPConstants.CODE_ADDITIONAL_PARTY_CAT) {
				this.setAdditionalPartyCat(param.getValue());
			} else if (param.getKey() == ISUPConstants.CODE_CARRIER_INFO_TRFR) {
				this.setCarrierInfoTransfer(param.getValue());
			} else if (param.getKey() == ISUPConstants.CODE_DPC_INFO  // DPC and Charge Number share same code however DPC will be in ITUT
					&& protocol == ISUPConstants.ISUP_ITUT) {         // and Charge number in ANSI
				this.setDPCInfo(param.getValue());
			} else if (param.getKey() == ISUPConstants.CODE_CHARGE_NUM  
					&& protocol == ISUPConstants.ISUP_ANSI) {
				this.setChargeNumber(param.getValue());
			} else if (param.getKey() == ISUPConstants.CODE_GENERIC_NUMBER) {
				this.setGenericNumber(param.getValue());
			} else if (param.getKey() == ISUPConstants.CODE_CHARGE_AREA_INFO) {
				this.setChargeAreaInformation(param.getValue());
			}else if (param.getKey() == ISUPConstants.CODE_SERVICE_ACTIVATION) {
				this.setServiceActivation(param.getValue());
			}else if (param.getKey() == ISUPConstants.CODE_CONTRACTOR_NUMBER ) {
				this.setContractorNumber(param.getValue());
			}else if (param.getKey() == ISUPConstants.CODE_REDIRECTING_NUMBER ) {
				this.setRedirectingNumber(param.getValue());
			}else if (param.getKey() == ISUPConstants.CODE_ORIGINAL_CALLED_NUMBER ) {
				this.setOriginalCalledNumber(param.getValue());
			}else if (param.getKey() == ISUPConstants.CODE_REDIRECTION_INFO ) {
				this.setRedirectionInfo(param.getValue());
			}else if (param.getKey() == ISUPConstants.CODE_JURISDICTION_INFO) {
				this.setJurisdictionInfoByte(param.getValue());
			}else if (param.getKey() == ISUPConstants.CODE_USER_SERVICE_INFO ) {
				this.setUserServiceInfo(param.getValue());
			}
			// else if
			// for unknown opt params
			else {
				if (otherOptParams == null)
					otherOptParams = new HashMap<Integer, byte[]>();
					otherOptParams.put(param.getKey(), param.getValue());
			}
		}
		setOtherOptParams(otherOptParams);
	}
}

package com.genband.isup.messagetypes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.genband.isup.datatypes.BwCallIndicators;
import com.genband.isup.datatypes.Cause;
import com.genband.isup.datatypes.Jti;
import com.genband.isup.datatypes.OptBwCallIndicators;
import com.genband.isup.datatypes.TtcCarrierInfoTrfr;
import com.genband.isup.datatypes.TtcChargeAreaInfo;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.operations.ISUPConstants;
import com.genband.isup.util.Util;

/**
 * This class contains parameters for IAM message 
 * @author vgoel
 *
 */
public class  ACMMessage {

	byte[] messageType;
	
	byte[] backwardCallIndicators;
	
	byte[] causeIndicators;
	
	byte[] optBwCallIndicators;
	
	byte[] carrierInfoTrfr;

	byte[] chargeAreaInfo;
	
	byte[] jti;
	
	int protocol;

	/**
	 * Map containing unknown optional fields code as keys and field data as value.
	 */
	Map<Integer, byte[]> otherOptParams = null; 
		
	
	public Map<Integer, byte[]> getOtherOptParams() {
		return otherOptParams;
	}

	public void setOtherOptParams(Map<Integer, byte[]> otherOptParams) {
		this.otherOptParams = otherOptParams;
	}

	public String getMessageType() {
		return String.valueOf(messageType[0]);		//message type will be of one byte only
	}
	
	public BwCallIndicators getBackwardCallIndicators() throws InvalidInputException {
		return BwCallIndicators.decodeBwCallInd(backwardCallIndicators);
	}

	public Cause getCauseIndicators() throws InvalidInputException {
		return Cause.decodeCauseVal(causeIndicators);
	}
	
	public OptBwCallIndicators getOptBwCallIndicators() throws InvalidInputException {
		return OptBwCallIndicators.decodeOptBwCallInd(optBwCallIndicators);
	}
	
	public TtcCarrierInfoTrfr getCarrierInfoTrfr() throws InvalidInputException {
		return TtcCarrierInfoTrfr.decodeTtcCarrierInfoTrfr(carrierInfoTrfr);
	}
	
	public TtcChargeAreaInfo getChargeAreaInfo() throws InvalidInputException {
		return TtcChargeAreaInfo.decodeTtcChargeAreaInfo(chargeAreaInfo);
	}
	
	public Jti getJti() throws InvalidInputException {
		return Jti.decodeJti(jti);
	}
	
	public void setCarrierInfoTrfr(byte[] carrierInfoTrfr) {
		this.carrierInfoTrfr = carrierInfoTrfr;
	}

	public void setBackwardCallIndicators(byte[] backwardCallIndicators) {
		this.backwardCallIndicators = backwardCallIndicators;
	}


	public void setCauseIndicators(byte[] causeIndicators) {
		this.causeIndicators = causeIndicators;
	}


	public void setMessageType(byte[] messageType) {
		this.messageType = messageType;
	}
	
	public void setOptBwCallIndicators(byte[] optBwCallIndicators) {
		this.optBwCallIndicators = optBwCallIndicators;
	}
	
	public void setChargeAreaInfo(byte[] chargeAreaInfo) {
		this.chargeAreaInfo = chargeAreaInfo;
	}
	
	public void setJti(byte[] jti) {
		this.jti = jti;
	}
	
	//getters which return byte[]
	public byte[] getMessageTypeBytes() {
		return messageType;		//message type will be of one byte only
	}
	
	public byte[] getBackwardCallIndicatorsBytes() {
		return backwardCallIndicators;
	}

	public byte[] getCauseIndicatorsBytes() {
		return causeIndicators;
	}
	
	public byte[] getOptBwCallIndicatorsBytes() {
		return optBwCallIndicators;
	}
	
	public byte[] getCarrierInfoTrfrBytes() {
		return carrierInfoTrfr;
	}
		
	public byte[] getChargeAreaInfoBytes() {
		return chargeAreaInfo;
	}
	
	public byte[] getJtiBytes() {
		return jti;
	}
	
	public int getProtocol() {
		return protocol;
	}

	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}
	
	public String toString()
	{		
		String obj = "messageType:" + Util.formatBytes(messageType) + ", backwardCallIndicators:"+ Util.formatBytes(backwardCallIndicators) + ", causeIndicators:"
		 + Util.formatBytes(causeIndicators) + ", optBwCallIndicators:" + Util.formatBytes(optBwCallIndicators) 
		 + "Jti:" + Util.formatBytes(jti) + ", Protocol: " + protocol +", otherOptParams:" + otherOptParams;
		
		return obj ;
	}
	
	public void setParams(Map<Integer, byte[]> optMap)
	{
		Iterator<Map.Entry<Integer,byte[]>> it = optMap.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<Integer, byte[]> param = it.next();
			if(param.getKey() == ISUPConstants.CODE_CAUSE_IND) {
				this.setCauseIndicators(param.getValue());
			}
			else if(param.getKey() == ISUPConstants.CODE_OPT_BW_CALL_IND) {
				this.setOptBwCallIndicators(param.getValue());
			}
			else if(param.getKey() == ISUPConstants.CODE_CHARGE_AREA_INFO) {
				this.setChargeAreaInfo(param.getValue());
			}
			else if(param.getKey() == ISUPConstants.CODE_CARRIER_INFO_TRFR) {
				this.setCarrierInfoTrfr(param.getValue());
			}
			else if(param.getKey() == ISUPConstants.CODE_JTI) {
				this.setJti(param.getValue());
			}
			//else if
			//for unknown opt params
			else {
				if(otherOptParams == null)
					otherOptParams = new HashMap<Integer, byte[]>();
				otherOptParams.put(param.getKey(), param.getValue());
			}
		}
		setOtherOptParams(otherOptParams);
	}
	
}

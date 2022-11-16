package com.genband.isup.messagetypes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.genband.isup.datatypes.BwCallIndicators;
import com.genband.isup.datatypes.Cause;
import com.genband.isup.datatypes.EventInfo;
import com.genband.isup.datatypes.Jti;
import com.genband.isup.datatypes.TtcCarrierInfoTrfr;
import com.genband.isup.datatypes.TtcChargeAreaInfo;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.operations.ISUPConstants;
import com.genband.isup.util.Util;

/**
 * This class contains parameters for CPG message 
 * @author vgoel
 *
 */
public class CPGMessage {

	byte[] messageType;
	
	byte[] eventInfo;
	
	byte[] bwCallIndicators;
	
	byte[] carrierInformationTransfer;
	
	byte[] chargeAreaInformation;
	
	byte[] cause;
	
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
	
	public EventInfo getEventInfo() throws InvalidInputException {
		return EventInfo.decodeEventInfo(eventInfo);
	}
	
	public BwCallIndicators getBwCallIndicators() throws InvalidInputException {
		return BwCallIndicators.decodeBwCallInd(bwCallIndicators);
	}
	
	public TtcCarrierInfoTrfr getCarrierInfoTransfer() throws InvalidInputException {
		return TtcCarrierInfoTrfr.decodeTtcCarrierInfoTrfr(carrierInformationTransfer);
	}
	
	public TtcChargeAreaInfo getChargeAreaInfo() throws InvalidInputException {
		return TtcChargeAreaInfo.decodeTtcChargeAreaInfo(chargeAreaInformation);
	}

	public Cause getCauseIndicator() throws InvalidInputException {
		return Cause.decodeCauseVal(cause);
	}
	
	public void setMessageType(byte[] messageType) {
		this.messageType = messageType;
	}

	public void setEvenntInfo(byte[] eventInfo) {
		this.eventInfo = eventInfo;
	}

	public void setBwCallIndicators(byte[] bwCallIndicators) {
		this.bwCallIndicators = bwCallIndicators;
	}
	
	public byte[] getBwCallIndicatorsBytes() {
		return this.bwCallIndicators;
	}

	public void setCarrierInfoTransfer(byte[] carrierInfoTransfer) {
		this.carrierInformationTransfer = carrierInfoTransfer;
	}
	
	public void setChargeAreaInfo(byte[] chargeAreaInfo) {
		this.chargeAreaInformation = chargeAreaInfo;
	}
	
	public void setCauseIndicator(byte[] cause) {
		this.cause = cause;
	}
	
	//getters which return byte[]
	public byte[] getMessageTypeBytes() {
		return messageType;		//message type will be of one byte only
	}
	
	public byte[] getEventInfoBytes() {
		return eventInfo;
	}

	public byte[] getCarrierInfoTransferBytes() {
		return carrierInformationTransfer;
	}
	
	public byte[] getChargeAreaInfoBytes() {
		return chargeAreaInformation;
	}
	
	public byte[] getCauseIndicatorBytes() {
		return cause;
	}
	
	public Jti getJti() throws InvalidInputException {
		return Jti.decodeJti(jti);
	}
	
	public void setJti(byte[] jti) {
		this.jti = jti;
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
		String obj = "messageType:" + Util.formatBytes(messageType) + 
		", eventInfo:"+ Util.formatBytes(eventInfo)
		+ ", bwCallIndicators:" + Util.formatBytes(bwCallIndicators) + 
		", CarrierInfoTransfer: "+Util.formatBytes(carrierInformationTransfer)+
		", chargeAreaInfo:"+ Util.formatBytes(chargeAreaInformation)+
		", CauseIndicator:"+ Util.formatBytes(cause) + ", Jti:" + ", Protocol: " + protocol + Util.formatBytes(jti) + ", otherOptParams:" + otherOptParams;
		
		return obj ;
	}
	
	public void setParams(Map<Integer, byte[]> optMap)
	{
		Iterator<Map.Entry<Integer,byte[]>> it = optMap.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<Integer, byte[]> param = it.next();
			if(param.getKey() == ISUPConstants.CODE_BW_CALL_IND) {
				this.setBwCallIndicators(param.getValue());
			} else if(param.getKey() == ISUPConstants.CODE_CARRIER_INFO_TRFR) {
				this.setCarrierInfoTransfer(param.getValue());
			} else if(param.getKey() == ISUPConstants.CODE_CHARGE_AREA_INFO) {
				this.setChargeAreaInfo(param.getValue());
			} else if(param.getKey() == ISUPConstants.CODE_CAUSE_IND) {
				this.setCauseIndicator(param.getValue());
			} else if(param.getKey() == ISUPConstants.CODE_JTI) {
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

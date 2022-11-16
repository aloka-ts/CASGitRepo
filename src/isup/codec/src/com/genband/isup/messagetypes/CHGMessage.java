package com.genband.isup.messagetypes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.genband.isup.datatypes.ChargingInfo;
import com.genband.isup.datatypes.ChargingInfoCategory;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

public class CHGMessage {

	byte[] messageType;
	
	byte[] chargingInfoCategory;
	
	byte[] chargingInfo;
	
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
		return String.valueOf(messageType[0]&0xFF);		//message type will be of one byte only
	}

	public ChargingInfoCategory getChargingInfoCategory() throws InvalidInputException {
		return ChargingInfoCategory.decodeChargingInfoCat(chargingInfoCategory);
	}

	public ChargingInfo getChargingInfo() throws InvalidInputException {
		return ChargingInfo.decodeChargingInfo(chargingInfo, getChargingInfoCategory().getChargingInfoCatEnum());
	}
	
	public void setMessageType(byte[] messageType) {
		this.messageType = messageType;
	}

	public void setChargingInfoCategory(byte[] chargingInfoCategory) {
		this.chargingInfoCategory = chargingInfoCategory;
	}

	public void setChargingInfo(byte[] chargingInfo) {
		this.chargingInfo = chargingInfo;
	}
	
	//getters which return byte[]
	public byte[] getMessageTypeBytes() {
		return messageType;		//message type will be of one byte only
	}
	
	public byte[] getChargingInfoCategoryBytes() {
		return chargingInfoCategory;
	}

	public byte[] getChargingInfoBytes() {
		return chargingInfo;
	}
	
	public int getProtocol() {
		return protocol;
	}

	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}

	public String toString()
	{		
		String obj = "messageType:" + Util.formatBytes(messageType) + ", chargingInfoCategory:"+ Util.formatBytes(chargingInfoCategory)
		+ ", chargingInfo:" + Util.formatBytes(chargingInfo) + ", Protocol:"+ protocol + ", otherOptParams:" + otherOptParams;
		
		return obj ;
	}
	
	public void setParams(Map<Integer, byte[]> optMap)
	{
		Iterator<Map.Entry<Integer,byte[]>> it = optMap.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<Integer, byte[]> param = it.next();
			//if(param.getKey() == ISUPConstants.CODE_BW_CALL_IND) {
				//this.setBackwardCallIndicators(param.getValue());
			//}
			//else if
			//for unknown opt params
			//else {
				if(otherOptParams == null)
					otherOptParams = new HashMap<Integer, byte[]>();
				otherOptParams.put(param.getKey(), param.getValue());
			//}
		}
		setOtherOptParams(otherOptParams);
	}
	
}

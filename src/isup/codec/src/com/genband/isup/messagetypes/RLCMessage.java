package com.genband.isup.messagetypes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.genband.isup.util.Util;

/**
 * This class contains parameters for RLC message 
 * @author vgoel
 *
 */
public class RLCMessage {

	byte[] messageType;
	
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
	
	public void setMessageType(byte[] messageType) {
		this.messageType = messageType;
	}
	
	
	//getters which return byte[]
	public byte[] getMessageTypeBytes() {
		return messageType;		//message type will be of one byte only
	}
	
	public int getProtocol() {
		return protocol;
	}

	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}

	public String toString()
	{		
		String obj = "messageType:" + Util.formatBytes(messageType) + ", Protocol: " + protocol + ", otherOptParams:" + otherOptParams;
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

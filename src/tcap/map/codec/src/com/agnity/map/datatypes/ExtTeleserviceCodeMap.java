package com.agnity.map.datatypes;

import org.apache.log4j.Logger;

import com.agnity.map.enumdata.TeleServiceCodeMapEnum;
import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.util.Util;

public class ExtTeleserviceCodeMap {
	private TeleServiceCodeMapEnum teleserviceCode;
	private static Logger logger = Logger.getLogger(ExtTeleserviceCodeMap.class);
	
	/**
	 * Set the teleservice code 
	 * @param code
	 */
	public void setTeleserviceCode(TeleServiceCodeMapEnum code){
		this.teleserviceCode = code;
	}
	
	/**
	 * Get Teleservice code
	 * @param data
	 * @return
	 * @throws InvalidInputException
	 */
	public TeleServiceCodeMapEnum getTeleserviceCode() {
		return this.teleserviceCode;
	}
	
	public static ExtTeleserviceCodeMap decode(byte[] data)
	throws InvalidInputException {
		
		ExtTeleserviceCodeMap theobj = new ExtTeleserviceCodeMap();
		if(data == null) {
			logger.error("binary data to decode is null");
			throw new InvalidInputException("ExtTeleserviceCodeMap:decode data to decode is null");
		}
		
		if(data.length < 1 || data.length > 5) {
			logger.error("binary data length is invalid");
			throw new InvalidInputException("ExtTeleserviceCodeMap:decode invalid data length ["+data.length+"]");
		}
		
		int decimalVal = data[0]&0xFF;
		
		TeleServiceCodeMapEnum teleservice = TeleServiceCodeMapEnum.getValue(decimalVal);
		theobj.setTeleserviceCode(teleservice);
		
		return theobj;
	}
			
	public static byte[] encode (ExtTeleserviceCodeMap encobj) throws InvalidInputException {
		if(encobj == null){
			logger.error("Object to encode is null");
			throw new InvalidInputException("ExtTeleserviceCodeMap:encode object to encode is null");
		}
		
		TeleServiceCodeMapEnum code = encobj.getTeleserviceCode(); 
		
		if(code == null){
			logger.error("Object's bearerservicecode is set to null");
			throw new InvalidInputException("ExtTeleserviceCodeMap:encode object has no bearer service code set");
		}
		
		int decimalVal = code.getCode();
		byte b0 = (byte)decimalVal;
		byte[] encdata = new byte[]{b0};
		
		if(logger.isDebugEnabled()){
			logger.debug("Encoded bytes are "+Util.formatBytes(encdata));
		}
		
		return encdata; 
	}
	
	public String toString() {
		String state = "bearerServiceCode [ "+this.teleserviceCode+"]";
		return state;
	}		
	
			

}

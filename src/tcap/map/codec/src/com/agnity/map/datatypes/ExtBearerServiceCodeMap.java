package com.agnity.map.datatypes;

import java.math.BigInteger;

import org.apache.log4j.Logger;

import com.agnity.map.enumdata.BearerServiceCodeMapEnum;
import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.util.Util;

public class ExtBearerServiceCodeMap {

	private static Logger logger = Logger.getLogger(ExtBearerServiceCodeMap.class);
	private BearerServiceCodeMapEnum bearerServiceCode;
	
	/**
	 * Set the Bearer service code
	 * @param code
	 */
	
	public void setBearerServicecode(BearerServiceCodeMapEnum code) {
		this.bearerServiceCode = code;
		
	}
	
	/**
	 * get Bearer service code
	 * @return
	 */
	
	public BearerServiceCodeMapEnum getBearerServiceCode(){
		return this.bearerServiceCode;
	}
	
	public static ExtBearerServiceCodeMap decode(byte[] data) 
			throws InvalidInputException{
		
		ExtBearerServiceCodeMap theobj = new ExtBearerServiceCodeMap();
		
		if(data == null) {
			logger.error("binary data to decode is null");
			throw new InvalidInputException("ExtBearerServiceCodeMap:decode data to decode is null");
		}
		
		if(data.length < 1 || data.length > 5) {
			logger.error("binary data length is invalid");
			throw new InvalidInputException("ExtBearerServiceCodeMap:decode invalid data length ["+data.length+"]");
		}
		
		int decimalVal = data[0]&0xFF;
		
		BearerServiceCodeMapEnum bearservice = BearerServiceCodeMapEnum.getValue(decimalVal);
		theobj.setBearerServicecode(bearservice);
		
		return theobj;
	}
	
	public static byte[] encode(ExtBearerServiceCodeMap encobj) throws InvalidInputException{
		
		if(encobj == null){
			logger.error("Object to encode is null");
			throw new InvalidInputException("ExtBearerServiceCodeMap:encode object to encode is null");
		}
		
		BearerServiceCodeMapEnum code = encobj.getBearerServiceCode();
		
		if(code == null){
			logger.error("Object's bearerservicecode is set to null");
			throw new InvalidInputException("ExtBearerServiceCodeMap:encode object has no bearer service code set");
		}
		
		int decimalVal = code.getCode();
		byte b0 = (byte)decimalVal;
		byte[] encdata = new byte[]{b0};
		
		if(logger.isDebugEnabled()){
			logger.debug("Encode bytes are "+Util.formatBytes(encdata));
		}
		
		return encdata; 
	}
	
	public String toString() {
		String state = "bearerServiceCode [ "+this.bearerServiceCode+"]";
		return state;
	}
}

/****
 * Copyright (c) 2013 Agnity, Inc. All rights reserved.
 * 
 * This is proprietary source code of Agnity, Inc.
 * 
 * Agnity, Inc. retains all intellectual property rights associated with this
 * source code. Use is subject to license terms.
 * 
 * This source code contains trade secrets owned by Agnity, Inc. Confidentiality
 * of this computer program must be maintained at all times, unless explicitly
 * authorized by Agnity, Inc.
 ****/
package com.agnity.map.util;

import java.math.BigInteger;

import org.apache.log4j.Logger;

import com.agnity.map.exceptions.InvalidInputException;

public class MapFunctions {
	
	private static Logger logger = Logger.getLogger(MapFunctions.class);
			
	public static String decodeNumber(byte[] bytes,int index){
		StringBuilder number = new StringBuilder();
		for(int i=index;i<bytes.length;i++){
		 	number.append(Integer.toHexString((bytes[i]&0x0f)));
		 	number.append(Integer.toHexString((bytes[i]>>4)&0x0f));
		}
		return number.toString();
	}
	
	public static String encodenNumber(String num){
		if(logger.isDebugEnabled()){
			logger.debug("encodenNumber number to encode : "+num);
		}
		if(num.length()%2 == 1) {
			num += "f"; // filler for odd length numbers
		}
		StringBuilder number = new StringBuilder();
		for(int i=0;i<num.length();i+=2){
		 	number.append(num.charAt(i+1));
		 	number.append(num.charAt(i));
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("Encoded number is : "+number);
		}
		return number.toString();
	}
	
    
    /**
     * encode address signal into byte byte array
     * @param addressSignal 
     * @return byte array
     * @throws InvalidInputException
     */
    public static String encodeAddress(String address) {
    	if(logger.isDebugEnabled()){
    		logger.debug("encode address digits = "+address);
    	}
        if(address.length()%2!=0){
        	address = address+"0";
        }
        
        StringBuilder number = new StringBuilder();
        for(int i=0;i<address.length();i+=2){
        	number.append(address.charAt(i+1));
            number.append(address.charAt(i));
        }
        return number.toString();
    }

    
    
    
    
	
	/**
	 * hexString convert into byte array
	 * @param String s
	 */
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len/2];
	    for (int i = 0; i < len; i += 2) {
	        data[i/2]=(byte)((Character.digit(s.charAt(i), 16) << 4)
	                               + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	
	public static String byteArrayToDegOfLatHigh(byte[] data) throws InvalidInputException{
		if(data == null ){
			logger.error("can't convert null to degrees of latitude High");
			throw new InvalidInputException("can't convert null to Degrees Of Latitude High");
		}
		if (data.length != 1){
			logger.error("Invalid data length ["+data.length+"], expected is 1 bytes");
			throw new InvalidInputException("Invalid data length, expected is 1 bytes");
		}
		System.out.println("degree of lat bytes to decode : " + Util.formatBytes(data));
		String degreesOfLatitude = null;
        if (Integer.valueOf(data[0]&0xff) > 0){
        	degreesOfLatitude = String.valueOf(Integer.valueOf(data[0]&0x7f));
        }
        
        if(logger.isDebugEnabled()) {
        	logger.debug("Degrees of latitude = "+degreesOfLatitude);
        }
        return degreesOfLatitude;
	}
	
	public static String byteArrayToDegOfLatLow(byte[] data) throws InvalidInputException{
		if(data == null ){
			logger.error("can't convert null to degrees of latitude Low");
			throw new InvalidInputException("can't convert null to Degrees Of Latitude Low");
		}
		if (data.length != 2){
			logger.error("Invalid data length ["+data.length+"], expected is 2 bytes");
			throw new InvalidInputException("Invalid data length, expected is 2 bytes");
		}
		System.out.println("degree of lat bytes to decode : " + Util.formatBytes(data));
		String degreesOfLatitude = String.valueOf((new BigInteger(data)).intValue());

		
/*        if (Integer.valueOf(data[0]&0xff) > 0) {
        		degreesOfLatitude = String.valueOf(Integer.valueOf(data[0]&0xff));
        }
        if(Integer.valueOf(data[1]&0xff)>0){
        	if(degreesOfLatitude!=null)
        		degreesOfLatitude += String.valueOf(Integer.valueOf(data[1]&0xff));
        	else
        		degreesOfLatitude = String.valueOf(Integer.valueOf(data[1]&0xff));
        		
        }*/
        
        if(logger.isDebugEnabled()) {
        	logger.debug("Degrees of latitude = "+degreesOfLatitude);
        }
        return degreesOfLatitude;
	}
	
	
	public static String byteArrayToDegOfLongitude(byte[] data) throws InvalidInputException{
		if(data == null ){
			logger.error("can't convert null to degrees of Longitude");
			throw new InvalidInputException("can't convert null to Degrees Of Longitude");
		}
		if (data.length != 3){
			logger.error("Invalid data length ["+data.length+"], expected is 3 bytes");
			throw new InvalidInputException("Invalid data length, expected is 3 bytes");
		}
		
		System.out.println("decode deg of longitude bytes to decode : " + Util.formatBytes(data));
		/*
		String degreesOfLatitude = null;
        if (Integer.valueOf(data[0]&0xff) > 0){
        	degreesOfLatitude = String.valueOf(Integer.valueOf(data[0]&0xff));
        }
        if (Integer.valueOf(data[1]&0xff) > 0) {
        	if(degreesOfLatitude!=null)
        		degreesOfLatitude += String.valueOf(Integer.valueOf(data[1]&0xff));
        	else 
        		degreesOfLatitude = String.valueOf(Integer.valueOf(data[1]&0xff));
        }
        if(Integer.valueOf(data[2]&0xff)>0){
        	if(degreesOfLatitude!=null)
        		degreesOfLatitude += String.valueOf(Integer.valueOf(data[2]&0xff));
        	else
        		degreesOfLatitude = String.valueOf(Integer.valueOf(data[2]&0xff));
        }
        */
		String degreesOfLatitude = (new BigInteger(data)).toString();
        if(logger.isDebugEnabled()) {
        	logger.debug("Degrees of latitude = "+degreesOfLatitude);
        }
        System.out.println("Degrees of latitude = "+ degreesOfLatitude);
        return degreesOfLatitude;
	}
	
	
	public static String byteArrayToMcc(byte[] bytes) throws InvalidInputException {
		
		if(bytes == null ){
			logger.error("can't convert null to degrees of Longitude");
			throw new InvalidInputException("can't convert null to Degrees Of Longitude");
		}
		if (bytes.length != 2){
			logger.error("Invalid data length ["+bytes.length+"], expected is 3 bytes");
			throw new InvalidInputException("Invalid data length, expected is 3 bytes");
		}
		
		String mcc = String.valueOf(Integer.valueOf((bytes[0]&0x0f)))+String.valueOf(Integer.valueOf(((bytes[0]>>4)&0x0f)))
	             +String.valueOf(Integer.valueOf((bytes[1]&0x0f)));
		return mcc;
	}
	

	
}

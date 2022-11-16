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
package com.agnity.cap.v3.datatypes;

import org.apache.log4j.Logger;

import com.agnity.cap.v3.exceptions.InvalidInputException;

/**
 * ref: Q.763 Closed user group interlock code 
 * @author rnarayan
 */
public class CUG_InterlockCapV3 {

	/*
	 * Network Identity 
	 * Each digit is coded in the binary coded 
	 * decimal representation from 0 to 9
	 */
	private int[] niDigits;
	
	/*
	 * A code allocated to a closed user group 
	 * administered by a particular ISDN or data network
	 */
	private byte[] binaryCode;
	
	private static Logger logger = Logger.getLogger(CUG_InterlockCapV3.class);
	
	/**
	 * 
	 * @return  Network Identity Digits
	 */
	public int[] getNiDigits() {
		return niDigits;
	}
	
	/**
	 * 
	 * @param nIDigits
	 */
	public void setNiDigits(int[] niDigits) {
		this.niDigits = niDigits;
	}
	
	/**
	 * 
	 * @return byte array of binary code
	 */
	public byte[] getBinaryCode() {
		return binaryCode;
	}
	
	/**
	 * 
	 * @param binaryCode
	 */
	public void setBinaryCode(byte[] binaryCode) {
		this.binaryCode = binaryCode;
	}
	
	/**
	 * decode CUG_Interlock 
	 * @param bytes of CUG_Interlock
	 * @return non asn object of CUG_InterlockCapV3
	 */
	public static CUG_InterlockCapV3 decode(byte[] bytes) throws InvalidInputException{
		if(logger.isDebugEnabled()){
			logger.debug("CUG_Interlock byte length ::"+bytes.length);
		}
		CUG_InterlockCapV3 ci= new CUG_InterlockCapV3();
		ci.niDigits = new int[4];
		ci.niDigits[0] = (bytes[0]>>4)&0x0f;
		ci.niDigits[1] = bytes[0] & 0x0f ;
		ci.niDigits[2] = (bytes[1]>>4)&0x0f;
		ci.niDigits[3] = bytes[1] & 0x0f ;
		
		int bcLength = bytes.length-2;
		ci.binaryCode = new byte[bcLength];
		for(int i=0;i<bcLength;i++){
			ci.binaryCode[i] = bytes[i+2];
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("CUG_Interlock successfully decoded.");
		}
		return ci;
	}
	
	/**
	 * encode CUG_Interlock 
	 * @param CUG_InterlockCapV3 object
	 * @return CUG_Interlock byte array
	 */
	public static byte[] encode(CUG_InterlockCapV3 ci) throws InvalidInputException{
		byte[] bytes = null;
		
		int[] niDigits = ci.niDigits;
		byte[] binaryCode = ci.binaryCode;
		
		int bytesLen = 2+binaryCode.length;
		bytes = new byte[bytesLen];
		
		bytes[0] = (byte) (((niDigits[0]<<4)+niDigits[1])& 0xff); 
		bytes[1] = (byte) (((niDigits[2]<<4)+niDigits[3])& 0xff); 
		
		for(int i=0;i<binaryCode.length;i++){
			bytes[i+2] = binaryCode[i];
		}
		if(logger.isDebugEnabled()){
			logger.debug("CUG_Interlock successfully encoded, bytes length:"+bytes.length);
		}
		return bytes;
	}
	
	//test
	/*public static void main(String[] args) {
		byte[] bytes = CapV2Functions
		.hexStringToByteArray("2365760956874534");
		
		byte[] exBytes = encode(decode(bytes));
		
		System.out.println(Arrays.equals(bytes, exBytes));
	}
	*/
	
}

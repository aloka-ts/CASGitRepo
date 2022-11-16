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

package com.agnity.map.datatypes;

import java.util.Arrays;

import org.apache.log4j.Logger;

import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.util.Util;

/**
 * 
 * @author sanjay
 *  trackingAreaIdentity [1] OCTET STRING (SIZE(5))
 *  Octets are coded as described in ETSI TS 124 301 V12.6.0
 *  
 *  Octet    MSB                                            LSB
 *          ________________________________________________
 *         | 8  |  7  |  6 |  5  |  4  |  3  |  2  |  1     |
 *         |____|_____|____|_____|_____|_____|_____|________|
 *         |  MCC Digit 2        |     MCC Digit 1          |
 *   e     |_____________________|__________________________|
 *         |  MNC Digit 3        |     MCC Digit 3          |
 *   e+1   |_____________________|__________________________| 
 *         |  MNC Digit 2        |     MNC Digit 1          |
 *   e+2   |_____________________|__________________________|
 *   e+3   |           Tracking Area Code (TAC)             |
 * to e+4  |________________________________________________|
 * 
 *
 */

public class TrackingAreaIdentityMap {
	private String mobCountryCode;
	private String mobNetworkCode;
	private byte[] tac;   // Tracking Area Code
	private static Logger logger = Logger.getLogger(TrackingAreaIdentityMap.class);
	
	/** 
	 * @return Mobile Country Code 
	 */
	public String getMobCountryCode() {
		return mobCountryCode;
	}
	
	/**
	 * set Mobile Country Code
	 * @param mobCountryCode
	 */
	public void setMobCountryCode(String mobCountryCode) {
		this.mobCountryCode = mobCountryCode;
	}
	
	/**
	 * 
	 * @return Mobile Network Code
	 */
	public String getMobNetworkCode() {
		return mobNetworkCode;
	}
	
	/**
	 * set Mobile Network Code
	 * @param mobNetworkCode
	 */
	public void setMobNetworkCode(String mobNetworkCode) {
		this.mobNetworkCode = mobNetworkCode;
	}
	
	
	/**
	 * 
	 * @return Tracking Area Code
	 */
	public byte[] getTac() {
		return this.tac;
	}
	
	/**
	 * set Tracking Area code
	 * @param Tac
	 */
	public void setTac(byte[] tac) {
		this.tac = tac;
	}
	
	/**
	 * decode TrackingAreaIdentityMap bytes array in non-asn object of
	 *  TrackingAreaIdentityMap
	 * @param bytes
	 * @return TrackingAreaIdentityMap object
	 */
	public static TrackingAreaIdentityMap decode(byte[] bytes) throws InvalidInputException{
		
		if(logger.isDebugEnabled()){
			logger.debug("In decode, TrackingAreaIdentityMap bytes array length::"+bytes.length);
		}
		
		if( bytes.length != 5 ){
			logger.error("Invalid data length ["+bytes.length+"], expceted 5 bytes");
			throw new InvalidInputException("Invalid data length ["+bytes.length+"], expceted 5 bytes");
		}
		
		TrackingAreaIdentityMap taiObj = new TrackingAreaIdentityMap();
		
		String mcc = String.valueOf(Integer.valueOf((bytes[0]&0x0f)))+String.valueOf(Integer.valueOf(((bytes[0]>>4)&0x0f)))
	             +String.valueOf(Integer.valueOf((bytes[1]&0x0f)));
		
		taiObj.mobCountryCode = mcc;
		
		String mnc= null;
		if(((bytes[1]>>4)&0x0f)!= 0x0f){ // if not 1111, we have mnc digit 3
			mnc =String.valueOf(Integer.valueOf((bytes[2]&0x0f)))+String.valueOf(Integer.valueOf((bytes[2]>>4)&0x0f))
			    +String.valueOf(Integer.valueOf((bytes[1]>>4)&0x0f));	
		}else{
			mnc= String.valueOf(Integer.valueOf((bytes[2]&0x0f)))+String.valueOf(Integer.valueOf((bytes[2]>>4)&0x0f));	
		}
		
		taiObj.mobNetworkCode = mnc;
		
		taiObj.tac = new byte[]{bytes[3], bytes[4]};
		
		if(logger.isDebugEnabled()){
			logger.debug("TrackingAreaIdentityMap non asn succesfully decoded.");
		}
		return taiObj;
	}	
	


	/**
	 * encode TrackingAreaIdentityMap non asn into byte array of 
	 * TrackingAreaIdentityMap
	 * @param cellId object
	 * @return byte array of 
	 * EUtranCellGlobalIdentityMap
	 */
	public static byte[] encode(TrackingAreaIdentityMap tai) throws InvalidInputException{
		byte[] bytes= null;
		char[] mcc = tai.mobCountryCode.toCharArray();
		System.out.println("size of mcc array = "+mcc.length);
		if(mcc.length != 3) {
			logger.error("Invalid MCC length");
			throw new InvalidInputException("Invalid MCC length ["+mcc.length+"], expected length is 3");
		}
		int digit1 = Integer.parseInt(Character.toString(mcc[0]));
		int digit2 = Integer.parseInt(Character.toString(mcc[1]));
		int digit3 = Integer.parseInt(Character.toString(mcc[2]));
		
		char[] mnc = tai.mobNetworkCode.toCharArray();
		
		if(mnc.length < 2) {
			logger.error("Invalid MNC length "+mnc.length);
			throw new InvalidInputException("Invalid MNC length ["+mnc.length+"], expected length 2 or 3");
		}
		int digit4 = 0;
		int digit5 = 0;
		int digit6 = 0;
		
		
		if(mnc.length==2){
			//filler 1111 
			digit4 = 15;
			digit5 = Integer.parseInt(Character.toString(mnc[0]));
			digit6 = Integer.parseInt(Character.toString(mnc[1]));
		}else{
			digit4 = Integer.parseInt(Character.toString(mnc[2]));
			digit5 = Integer.parseInt(Character.toString(mnc[0]));
			digit6 = Integer.parseInt(Character.toString(mnc[1]));
		}
		
		byte b0 = (byte)(((digit2<<4)+digit1)&0xff);
		byte b1 = (byte)(((digit4<<4)+digit3)&0xff);
		byte b2 = (byte)(((digit6<<4)+digit5)&0xff);
	   
		byte[] tacId = tai.tac;

		bytes = new byte[]{b0, b1, b2, tacId[0], tacId[1]};
		
		if(logger.isDebugEnabled()){
			logger.debug("TrackingAreaIdentityMap non asn succesfully encoded. byte array length:"+bytes.length);
		}
		return bytes;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TrackingAreaIdentityMap [mobCountryCode=" + mobCountryCode
				+ ", mobNetworkCode=" + mobNetworkCode + ", tac="
				+ Arrays.toString(tac) + "]";
	}
}

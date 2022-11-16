/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/

package com.agnity.map.datatypes;

import org.apache.log4j.Logger;

import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.util.Util;

public class RouteingAreaIdentityMap {
	private int mobCountryCode;
	private int mobNetworkCode;
	private byte[] lac;   // Location Area Code
	private byte[] rac;   // Routing Area Code
	private static Logger logger = Logger.getLogger(TrackingAreaIdentityMap.class);
	
	/** 
	 * @return Mobile Country Code 
	 */
	public int getMobCountryCode() {
		return mobCountryCode;
	}
	
	/**
	 * set Mobile Country Code
	 * @param mobCountryCode
	 */
	public void setMobCountryCode(int mobCountryCode) {
		this.mobCountryCode = mobCountryCode;
	}
	
	/**
	 * 
	 * @return Mobile Network Code
	 */
	public int getMobNetworkCode() {
		return mobNetworkCode;
	}
	
	/**
	 * set Mobile Network Code
	 * @param mobNetworkCode
	 */
	public void setMobNetworkCode(int mobNetworkCode) {
		this.mobNetworkCode = mobNetworkCode;
	}
	
	
	/**
	 * 
	 * @return Location Area Code
	 */
	public byte[] getLac() {
		return this.lac;
	}
	
	/**
	 * set Location Area code
	 * @param lac
	 */
	public void setLac(byte[] lac) {
		this.lac = lac;
	}
	
	/**
	 * 
	 * @return Routing Area Code
	 */
	public byte[] getRac() {
		return this.rac;
	}
	
	/**
	 * set Routing Area code
	 * @param lac
	 */
	public void seRac(byte[] rac) {
		this.rac = rac;
	}
	
	
	/**
	 * decode TrackingAreaIdentityMap bytes array in non-asn object of
	 *  TrackingAreaIdentityMap
	 * @param bytes
	 * @return TrackingAreaIdentityMap object
	 */
	public static RouteingAreaIdentityMap decode(byte[] bytes) throws InvalidInputException{
		
		if(logger.isDebugEnabled()){
			logger.debug("In decode, TrackingAreaIdentityMap bytes array length::"+bytes.length);
		}
		
		RouteingAreaIdentityMap raiObj = new RouteingAreaIdentityMap();
		String mcc = Util.decodeMcc(new byte[]{bytes[0], bytes[1]});
		raiObj.mobCountryCode = Integer.parseInt(mcc);
		
		String mnc= Util.decodeMnc(new byte[]{bytes[1], bytes[2]});
		
		raiObj.mobNetworkCode = Integer.parseInt(mnc);
		
		raiObj.lac = new byte[]{bytes[3], bytes[4]};
		raiObj.rac = new byte[]{bytes[5]};
		
		if(logger.isDebugEnabled()){
			logger.debug("RouteingAreaIdentityMap non asn succesfully decoded.");
		}
		return raiObj;
	}	
	
	/**
	 * encode RouteingAreaIdentityMap non asn into byte array of 
	 * RouteingAreaIdentityMap
	 * @param cellId object
	 * @return byte array of 
	 * RouteingAreaIdentityMap
	 */
	public static byte[] encode(RouteingAreaIdentityMap rai) throws InvalidInputException{
		byte[] bytes= null;
		char[] mcc = String.valueOf(rai.mobCountryCode).toCharArray();
		int digit1 = Integer.parseInt(Character.toString(mcc[0]));
		int digit2 = Integer.parseInt(Character.toString(mcc[1]));
		int digit3 = Integer.parseInt(Character.toString(mcc[2]));
		
		char[] mnc = String.valueOf(rai.mobNetworkCode).toCharArray();
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
	   
		byte[] lacArray = rai.lac;
		byte[] racArray = rai.rac;

		bytes = new byte[]{b0, b1, b2, lacArray[0], lacArray[1], racArray[0]};
		
		if(logger.isDebugEnabled()){
			logger.debug("RouteingAreaIdentityMap non asn succesfully encoded. byte array length:"+bytes.length);
		}
		return bytes;
	}


}

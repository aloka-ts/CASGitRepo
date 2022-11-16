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
import com.agnity.map.util.MapFunctions;
import com.agnity.map.util.Util;

/**
 * @ref: ETSI TS 100 974 V7.15.0 (2004-03) 
 * @author sanjay
 * @note: CellGlobalIdOrServiceAreaIdFixedLength and CellIdFixedLength are same 
 * Refers to Cell Global Identification defined in TS GSM 03.03. 
   The internal structure is defined as follows: 
	octet 1 bits 4321 Mobile Country Code 1st digit 
	        bits 8765 Mobile Country Code 2nd digit 
	octet 2 bits 4321 Mobile Country Code 3rd digit 
	        bits 8765 Mobile Network Code 3rd digit 
	                  or filler (1111) for 2 digit MNCs 
	octet 3 bits 4321 Mobile Network Code 1st digit 
	        bits 8765 Mobile Network Code 2nd digit 
	octets 4 and 5 Location Area Code according to TS GSM 04.08 
	octets 6 and 7 Cell Identity (CI) according to TS GSM 04.08 
 */
public class CellGlobalIdOrServiceAreaIdFixedLengthMap{

	private String mobCountryCode;
	private String mobNetworkCode;
	private String locAreaCode;
	private String cellIdentity;
	private String byteValue;
	
	public String getByteValue() {
		return byteValue;
	}

	private static Logger logger = Logger.getLogger(CellGlobalIdOrServiceAreaIdFixedLengthMap.class);
	
	
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
	 * @return Location Area Code
	 */
	public String getLocAreaCode() {
		return locAreaCode;
	}
	
	/**
	 * set Location Area Code
	 * @param locAreaCode
	 */
	public void setLocAreaCode(String locAreaCode) {
		this.locAreaCode = locAreaCode;
	}
	
	
	
	/**
	 * 
 	 * @return Cell Identity value
	 */
	public String getCellIdentity() {
		return cellIdentity;
	}
	
	/**
	 * set Cell Identity value
	 * @param cellIdentity
	 */
	public void setCellIdentity(String cellIdentity) {
		this.cellIdentity = cellIdentity;
	}
	
	/**
	 * decode CellGlobalIdOrServiceAreaIdFixedLength bytes array in non-asn object of
	 *  CellGlobalIdOrServiceAreaIdFixedLengthMap
	 * @param bytes
	 * @return CellGlobalIdOrServiceAreaIdFixedLengthMap object
	 */
	public static CellGlobalIdOrServiceAreaIdFixedLengthMap decode(byte[] bytes) throws InvalidInputException{
		if(logger.isDebugEnabled()){
			logger.debug("In decode, CellGlobalIdOrServiceAreaIdFixedLength bytes array length::"+bytes.length);
		}
		System.out.println("data to decod = "+Util.formatBytes(bytes));
		
		CellGlobalIdOrServiceAreaIdFixedLengthMap ci = new CellGlobalIdOrServiceAreaIdFixedLengthMap();
		ci.byteValue=Util.formatBytes(bytes);
		System.out.println("first digit 1 = "+(bytes[0]&0x0f));
		System.out.println("first digit 2 = "+((bytes[0]>>4)&0x0f));
		System.out.println("first digit 3 = "+(bytes[1]&0x0f));
		
		StringBuilder sb = new StringBuilder();
		sb.append(bytes[0]&0x0f).append((bytes[0]>>4)&0x0f).append(bytes[1]&0x0f);

		String mcc = sb.toString();
		System.out.println("mcc = "+ mcc);
		ci.mobCountryCode = mcc;
		System.out.println("after parse = "+ci.mobCountryCode);

				
		
		String mnc= null;
		if(((bytes[1]>>4)&0x0f)!= 0x0f){
			StringBuilder mncsb = new StringBuilder();
			mncsb.append((bytes[2]&0x0f)).append((bytes[2]>>4)&0x0f).append((bytes[1]>>4)&0x0f);

			mnc = mncsb.toString();
		}else{
			StringBuilder mncsb = new StringBuilder();
			mncsb.append((bytes[2]&0x0f)).append((bytes[2]>>4)&0x0f);
			mnc= mncsb.toString();	
		}
		ci.mobNetworkCode = mnc;
		
		StringBuilder lacsb = new StringBuilder();
		lacsb.append(Integer.toHexString((bytes[3]&0xff)));
		lacsb.append(Integer.toHexString((bytes[4]&0xff)));
		ci.locAreaCode = String.valueOf(Long.parseLong(lacsb.toString(), 16));
		
		StringBuilder cidsb = new StringBuilder();
		cidsb.append(Integer.toHexString((bytes[5]&0xff)));
		cidsb.append(Integer.toHexString((bytes[6]&0xff)));
		ci.cellIdentity = String.valueOf(Long.parseLong(cidsb.toString(), 16));
		
		if(logger.isDebugEnabled()){
			logger.debug("CellGlobalIdOrServiceAreaIdFixedLength non asb succesfully decoded.");
		}
		return ci;
	}
	
	/**
	 * encode CellGlobalIdOrServiceAreaIdFixedLengthMap non asn into byte array of 
	 * CellGlobalIdOrServiceAreaIdFixedLength
	 * @param ci
	 * @return byte array of 
	 * CellGlobalIdOrServiceAreaIdFixedLength
	 */
	public static byte[] encode(CellGlobalIdOrServiceAreaIdFixedLengthMap ci) throws InvalidInputException{
		byte[] bytes= null;
		char[] mcc = ci.mobCountryCode.toCharArray();
		int digit1 = Integer.parseInt(Character.toString(mcc[0]));
		int digit2 = Integer.parseInt(Character.toString(mcc[1]));
		int digit3 = Integer.parseInt(Character.toString(mcc[2]));
		
		char[] mnc = ci.mobNetworkCode.toCharArray();
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
	   
		byte[] laCode = MapFunctions.hexStringToByteArray(MapFunctions.encodeAddress(ci.locAreaCode));
		byte[] ciValue = MapFunctions.hexStringToByteArray(MapFunctions.encodeAddress(ci.cellIdentity));
		bytes = new byte[]{b0,b1,b2,laCode[0],laCode[1],ciValue[0],ciValue[1]};
		
		if(logger.isDebugEnabled()){
			logger.debug("CellGlobalIdOrServiceAreaIdFixedLength non asn succesfully encoded. byte array length:"+bytes.length);
		}
		return bytes;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CellGlobalIdOrServiceAreaIdFixedLengthMap [mobCountryCode="
				+ mobCountryCode + ", mobNetworkCode=" + mobNetworkCode
				+ ", locAreaCode=" + locAreaCode
				+ ", cellIdentity=" + cellIdentity + "]";
	}
}

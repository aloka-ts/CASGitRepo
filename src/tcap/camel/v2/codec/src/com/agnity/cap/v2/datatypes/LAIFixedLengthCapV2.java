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
package com.agnity.cap.v2.datatypes;


import org.apache.log4j.Logger;

import com.agnity.cap.v2.exceptions.InvalidInputException;

/**
 * @ref: ETSI TS 100 974 V7.15.0 (2004-03) 
 * @author rnarayan
 -- Refers to Location Area Identification defined in TS GSM 03.03. 
 -- The internal structure is defined as follows: 
 -- octet 1 bits 4321 Mobile Country Code 1st digit 
 --         bits 8765 Mobile Country Code 2nd digit 
 -- octet 2 bits 4321 Mobile Country Code 3rd digit 
 --         bits 8765 Mobile Network Code 3rd digit 
 --                   or filler (1111) for 2 digit MNCs 
 -- octet 3 bits 4321 Mobile Network Code 1st digit 
 --         bits 8765 Mobile Network Code 2nd digit 
 -- octets 4 and 5 Location Area Code according to TS GSM 04.08 
 *
 */
public class LAIFixedLengthCapV2 {

	private int mobCountryCode;
	private int mobNetworkCode;
	private byte[] locAreaCode;
	private static Logger logger = Logger.getLogger(LAIFixedLengthCapV2.class);
	
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
	public byte[] getLocAreaCode() {
		return locAreaCode;
	}
	
	/**
	 * set Location Area Code
	 * @param locAreaCode
	 */
	public void setLocAreaCode(byte[] locAreaCode) {
		this.locAreaCode = locAreaCode;
	}
	
	
	/**
	 * decode LAIFixedLength bytes array in non-asn object of LAIFixedLengthCapV2
	 * @param bytes
	 * @return LAIFixedLengthCapV2 object
	 * @throws InvalidInputException
	 */
	public static LAIFixedLengthCapV2 decode(byte[] bytes) throws InvalidInputException{
		
		if(logger.isDebugEnabled()){
			logger.debug("In decode, LAIFixedLength bytes array length::"+bytes.length);
		}
		
		LAIFixedLengthCapV2 lai = new LAIFixedLengthCapV2();
		String mcc = Integer.toHexString((bytes[0]&0x0f))+Integer.toHexString((bytes[0]>>4)&0x0f)
		             +Integer.toHexString((bytes[1]&0x0f));
		lai.mobCountryCode = Integer.parseInt(mcc);
		
		String mnc= null;
		if(((bytes[1]>>4)&0x0f)!= 0x0f){
			mnc =Integer.toHexString((bytes[2]&0x0f))+Integer.toHexString((bytes[2]>>4)&0x0f)
			    +Integer.toHexString((bytes[1]>>4)&0x0f);	
		}else{
			mnc= Integer.toHexString((bytes[2]&0x0f))+Integer.toHexString((bytes[2]>>4)&0x0f);	
		}
		lai.mobNetworkCode = Integer.parseInt(mnc);
		lai.locAreaCode = new byte[]{bytes[3],bytes[4]};
		
		if(logger.isDebugEnabled()){
			logger.debug("LAIFixedLength non asb succesfully decoded.");
		}
		return lai;
		
	}
	
	/**
	 * encode LAIFixedLengthCapV2 non asn into byte array of LAIFixedLength
	 * @param lai
	 * @return byte array of LAIFixedLength
	 * @throws InvalidInputException
	 */
	public static byte[] encode(LAIFixedLengthCapV2 lai) throws InvalidInputException{
		byte[] bytes= null;
		char[] mcc = String.valueOf(lai.mobCountryCode).toCharArray();
		int digit1 = Integer.parseInt(Character.toString(mcc[0]));
		int digit2 = Integer.parseInt(Character.toString(mcc[1]));
		int digit3 = Integer.parseInt(Character.toString(mcc[2]));
		
		char[] mnc = String.valueOf(lai.mobNetworkCode).toCharArray();
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
	   
		byte[] laCode = lai.locAreaCode;
		
		bytes = new byte[]{b0,b1,b2,laCode[0],laCode[1]};
		
		if(logger.isDebugEnabled()){
			logger.debug("LAIFixedLength non asb succesfully encoded. byte array length:"+bytes.length);
		}
		return bytes;
	}
	
	//test
	/*public static void main(String[] args) throws InvalidInputException {
		byte[] bytes = CapV2Functions.hexStringToByteArray("3467218945");
		byte[] exByte = encode(decode(bytes));
		System.out.println(Arrays.equals(bytes, exByte));
	}*/
	
}

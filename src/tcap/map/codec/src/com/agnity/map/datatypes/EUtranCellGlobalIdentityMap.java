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

import java.math.BigInteger;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.util.MapFunctions;
import com.agnity.map.util.Util;

/**
 * 
 * @author sanjay
 * 
 * e-utranCellGlobalIdentity [0] OCTET STRING (SIZE(7)) OPTIONAL
 * -- Octets are coded as described in 3GPP TS 29.118.

 * 
 * The coding of the E-UTRAN Cell Global Identity value is according
 * to ECGI field information element as specified in subclause 8.21.5
 * of 3GPP TS 29.274 [17A]
 * 
 * Octet    MSB                                            LSB
 *          ________________________________________________
 *         | 8  |  7  |  6 |  5  |  4  |  3  |  2  |  1     |
 *         |____|_____|____|_____|_____|_____|_____|________|
 *         |  MCC Digit 2        |     MCC Digit 1          |
 *   e     |_____________________|__________________________|
 *         |  MNC Digit 3        |     MCC Digit 3          |
 *   e+1   |_____________________|__________________________|  // MNC digit 3 if exists, else 1111
 *         |  MNC Digit 2        |     MNC Digit 1          |
 *   e+2   |_____________________|__________________________|
 *         |  Spare              |     ECI                  | ----->> Found ECI swapped with Spare in Spectra codec.
 *   e+3   |_____________________|__________________________|
 *   e+4   |        ECI (U-TRAN Cell Identifier)            |
 * to e+6  |________________________________________________|
 *
 *  The E-UTRAN Cell Identifier (ECI) consists of 28 bits. 
 *  The ECI field shall start with Bit 4 of octet e+3, which is the
 *  most significant bit. Bit 1 of Octet e+6 is the least significant bit.
 */

public class EUtranCellGlobalIdentityMap {
	
	private String mobCountryCode;
	private String mobNetworkCode;
	private byte[] eCellGid;   // E-Utran Cell Global Id
	private static Logger logger = Logger.getLogger(EUtranCellGlobalIdentityMap.class);
	

	
	/**
	 * @return the mobCountryCode
	 */
	public String getMobCountryCode() {
		return mobCountryCode;
	}

	/**
	 * @return the mobNetworkCode
	 */
	public String getMobNetworkCode() {
		return mobNetworkCode;
	}



	/**
	 * @return the eCellGid
	 */
	public byte[] geteCellGid() {
		return eCellGid;
	}

	/**
	 * @param mobCountryCode the mobCountryCode to set
	 */
	public void setMobCountryCode(String mobCountryCode) {
		this.mobCountryCode = mobCountryCode;
	}

	/**
	 * @param mobNetworkCode the mobNetworkCode to set
	 */
	public void setMobNetworkCode(String mobNetworkCode) {
		this.mobNetworkCode = mobNetworkCode;
	}



	/**
	 * @param eCellGid the eCellGid to set
	 */
	public void seteCellGid(byte[] eCellGid) {
		this.eCellGid = eCellGid;
	}

	/**
	 * decode EUtranCellGlobalIdentityMap bytes array in non-asn object of
	 *  EUtranCellGlobalIdentityMap
	 * @param bytes
	 * @return EUtranCellGlobalIdentityMap object
	 */
	public static EUtranCellGlobalIdentityMap decode(byte[] bytes) throws InvalidInputException{
		if(logger.isDebugEnabled()){
			logger.debug("In decode, EUtranCellGlobalIdentityMap bytes array length::"+bytes.length);
		}
		System.out.println("EUtranCellGlobalIdentityMap::decode "+Util.formatBytes(bytes));
		
		EUtranCellGlobalIdentityMap cellId = new EUtranCellGlobalIdentityMap();
		//String mcc = Integer.toHexString((bytes[0]&0x0f))+Integer.toHexString((bytes[0]>>4)&0x0f)
		//             +Integer.toHexString((bytes[1]&0x0f));
		String mcc = String.valueOf(Integer.valueOf((bytes[0]&0x0f)))+String.valueOf(Integer.valueOf(((bytes[0]>>4)&0x0f)))
	             +String.valueOf(Integer.valueOf((bytes[1]&0x0f)));
		
		cellId.mobCountryCode = mcc;
		
		String mnc= null;
		if(((bytes[1]>>4)&0x0f)!= 0x0f){ // if not 1111, we have mnc digit 3
			mnc =String.valueOf(Integer.valueOf((bytes[2]&0x0f)))+String.valueOf(Integer.valueOf((bytes[2]>>4)&0x0f))
			    +String.valueOf(Integer.valueOf((bytes[1]>>4)&0x0f));	
		}else{
			mnc= String.valueOf(Integer.valueOf((bytes[2]&0x0f)))+String.valueOf(Integer.valueOf((bytes[2]>>4)&0x0f));	
		}
		cellId.mobNetworkCode = mnc;
		cellId.eCellGid = new byte[]{(byte)(bytes[3]&0x0f), bytes[4], bytes[5], bytes[6]};
		
		if(logger.isDebugEnabled()){
			logger.debug("EUtranCellGlobalIdentityMap non asn succesfully decoded.");
		}
		return cellId;
	}
	

	/**
	 * encode EUtranCellGlobalIdentityMap non asn into byte array of 
	 * EUtranCellGlobalIdentityMap
	 * @param cellId object
	 * @return byte array of 
	 * EUtranCellGlobalIdentityMap
	 */
	public static byte[] encode(EUtranCellGlobalIdentityMap cellId) throws InvalidInputException{
		byte[] bytes= null;
		char[] mcc = String.valueOf(cellId.mobCountryCode).toCharArray();
		int digit1 = Integer.parseInt(Character.toString(mcc[0]));
		int digit2 = Integer.parseInt(Character.toString(mcc[1]));
		int digit3 = Integer.parseInt(Character.toString(mcc[2]));
		
		char[] mnc = String.valueOf(cellId.mobNetworkCode).toCharArray();
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
	   
		byte[] eci = cellId.eCellGid;

		bytes = new byte[]{b0, b1, b2, (byte)(eci[3]&0x0f), eci[4], eci[5], eci[6]};
		
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
		return "EUtranCellGlobalIdentityMap [mobCountryCode=" + mobCountryCode
				+ ", mobNetworkCode=" + mobNetworkCode 
				+ ", eCellGid=" + Arrays.toString(eCellGid) + "]";
	}

}

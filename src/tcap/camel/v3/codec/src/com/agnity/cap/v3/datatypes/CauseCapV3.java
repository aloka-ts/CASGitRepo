/****
 * Copyright (coffee) 2013 Agnity, Inc. All rights reserved.
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


import com.agnity.cap.v3.datatypes.enumType.CauseLocationCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.CodingStandardCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.ExtentionIndicatorCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.CauseIndicatorCap2Enum;
import com.agnity.cap.v3.exceptions.InvalidInputException;


/**
 * ref: Q763/Q850
 */
public class CauseCapV3 {

	private CauseLocationCapV3Enum causeLocation;
	private CodingStandardCapV3Enum codingStandard;
	private ExtentionIndicatorCapV3Enum extIndicator;
	private ExtentionIndicatorCapV3Enum extIndicator2;
	private CauseIndicatorCap2Enum causeIndicator;
	
	

	public ExtentionIndicatorCapV3Enum getExtIndicator2() {
		return extIndicator2;
	}

	public void setExtIndicator2(ExtentionIndicatorCapV3Enum extIndicator2) {
		this.extIndicator2 = extIndicator2;
	}
	public CauseIndicatorCap2Enum getCauseIndicator() {
		return causeIndicator;
	}

	public void setCauseIndicator(CauseIndicatorCap2Enum causeIndicator) {
		this.causeIndicator = causeIndicator;
	}

	public CauseLocationCapV3Enum getCauseLocation() {
		return causeLocation;
	}

	public void setCauseLocation(CauseLocationCapV3Enum causeLocation) {
		this.causeLocation = causeLocation;
	}

	public CodingStandardCapV3Enum getCodingStandard() {
		return codingStandard;
	}

	public void setCoadingStandard(CodingStandardCapV3Enum codingStandard) {
		this.codingStandard = codingStandard;
	}

	public ExtentionIndicatorCapV3Enum getExtIndicator() {
		return extIndicator;
	}

	public void setExtIndicator(ExtentionIndicatorCapV3Enum extIndicator) {
		this.extIndicator = extIndicator;
	}
	

	public static CauseCapV3 decode(byte[] bytes) throws InvalidInputException {
		CauseCapV3 rc = new CauseCapV3();
		rc.extIndicator = ExtentionIndicatorCapV3Enum.getValue((byte) (bytes[0]>>7)&0x01);
		rc.codingStandard = CodingStandardCapV3Enum.getValue((byte) (bytes[0]>>5)&0x03);
		rc.causeLocation = CauseLocationCapV3Enum.fromInt((byte) (bytes[0])&0x0f);		
		rc.causeIndicator = CauseIndicatorCap2Enum.getValue((byte) (bytes[1]>>4)&0x07);
		rc.extIndicator2 = ExtentionIndicatorCapV3Enum.getValue((byte) (bytes[1]>>7)&0x01);		
		return rc;
	}
		
	public static byte[] encode(CauseCapV3 rc) throws InvalidInputException{
		
		int extIndicator = rc.extIndicator.getCode();
		int coadingstandard = rc.codingStandard.getCode();
		int causelocation = rc.causeLocation.getCode();
		int causeIndicator = rc.causeIndicator.getCode();
		int extIndicator2 =rc.extIndicator.getCode();
		
		byte b0 = (byte)(((extIndicator<<7)+(coadingstandard<<5)) |causelocation);
		byte b1 = (byte)((extIndicator2<<7) + (causeIndicator<<4));	
						
	    byte[] bytes = new byte[]{(byte)b0,(byte)b1};
	  
		return bytes;
	}
	
	@Override
	public String toString() {
		String bl = "\n";
		return "causelocation="+this.causeLocation+bl+
		"coadingstandard="+this.codingStandard+bl+"extIndicator="+this.extIndicator+bl
		+"extIndicator2+"+this.extIndicator2+bl+"causeIndicator="+this.causeIndicator;
	}
	
	/******
	 ***
	 **
	For Unit Testing
	 *****
	 ****
	 ***
	 */
	
 	public static void main(String[] args) throws InvalidInputException{
 		CauseCapV3 decode= new CauseCapV3();
 		CauseCapV3 decodeAG= new CauseCapV3();
		
		
		byte[] dpByte1 = new byte[]{(byte)0x80,(byte)0x90};

		decode=CauseCapV3.decode(dpByte1);
		
		System.out.println("Extension Indicator:"+decode.extIndicator);
		System.out.println("CoadingStandardCapV2Enum :" +decode.codingStandard);
		System.out.println("CauseLocationCapV2Enum :" +decode.causeLocation);
		System.out.println("CauseIndicatorCap2Enum :" +decode.causeIndicator);
		System.out.println("Extension Indicator:"+decode.extIndicator2);
	
				
		byte[] dpByte3 =CauseCapV3.encode(decode);
						
		decodeAG=CauseCapV3.decode(dpByte3);	
		
		System.out.println("Extension Indicator:"+decodeAG.extIndicator);
		System.out.println("CoadingStandardCapV2Enum :" +decodeAG.codingStandard);
		System.out.println("CauseLocationCapV2Enum :" +decodeAG.causeLocation);
		System.out.println("CauseIndicatorCap2Enum :" +decodeAG.causeIndicator);
		System.out.println("Extension Indicator:"+decodeAG.extIndicator2);
				
   	}


	
}

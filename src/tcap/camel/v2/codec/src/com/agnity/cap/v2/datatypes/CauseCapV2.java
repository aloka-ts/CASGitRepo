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
package com.agnity.cap.v2.datatypes;


import com.agnity.cap.v2.datatypes.enumType.CauseLocationCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.CodingStandardCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.ExtentionIndicatorCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.CauseIndicatorCap2Enum;
import com.agnity.cap.v2.exceptions.InvalidInputException;


/**
 * ref: Q763/Q850
 */
public class CauseCapV2 {

	private CauseLocationCapV2Enum causeLocation;
	private CodingStandardCapV2Enum codingStandard;
	private ExtentionIndicatorCapV2Enum extIndicator;
	private ExtentionIndicatorCapV2Enum extIndicator2;
	private CauseIndicatorCap2Enum causeIndicator;
	
	

	public ExtentionIndicatorCapV2Enum getExtIndicator2() {
		return extIndicator2;
	}

	public void setExtIndicator2(ExtentionIndicatorCapV2Enum extIndicator2) {
		this.extIndicator2 = extIndicator2;
	}
	public CauseIndicatorCap2Enum getCauseIndicator() {
		return causeIndicator;
	}

	public void setCauseIndicator(CauseIndicatorCap2Enum causeIndicator) {
		this.causeIndicator = causeIndicator;
	}

	public CauseLocationCapV2Enum getCauseLocation() {
		return causeLocation;
	}

	public void setCauseLocation(CauseLocationCapV2Enum causeLocation) {
		this.causeLocation = causeLocation;
	}

	public CodingStandardCapV2Enum getCodingStandard() {
		return codingStandard;
	}

	public void setCoadingStandard(CodingStandardCapV2Enum codingStandard) {
		this.codingStandard = codingStandard;
	}

	public ExtentionIndicatorCapV2Enum getExtIndicator() {
		return extIndicator;
	}

	public void setExtIndicator(ExtentionIndicatorCapV2Enum extIndicator) {
		this.extIndicator = extIndicator;
	}
	

	public static CauseCapV2 decode(byte[] bytes) throws InvalidInputException {
		CauseCapV2 rc = new CauseCapV2();
		rc.extIndicator = ExtentionIndicatorCapV2Enum.getValue((byte) (bytes[0]>>7)&0x01);
		rc.codingStandard = CodingStandardCapV2Enum.getValue((byte) (bytes[0]>>5)&0x03);
		rc.causeLocation = CauseLocationCapV2Enum.fromInt((byte) (bytes[0])&0x0f);		
		rc.causeIndicator = CauseIndicatorCap2Enum.getValue((byte) (bytes[1]>>4)&0x07);
		rc.extIndicator2 = ExtentionIndicatorCapV2Enum.getValue((byte) (bytes[1]>>7)&0x01);		
		return rc;
	}
		
	public static byte[] encode(CauseCapV2 rc) throws InvalidInputException{
		
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
 		CauseCapV2 decode= new CauseCapV2();
 		CauseCapV2 decodeAG= new CauseCapV2();
		
		
		byte[] dpByte1 = new byte[]{(byte)0x80,(byte)0x90};

		decode=CauseCapV2.decode(dpByte1);
		
		System.out.println("Extension Indicator:"+decode.extIndicator);
		System.out.println("CoadingStandardCapV2Enum :" +decode.codingStandard);
		System.out.println("CauseLocationCapV2Enum :" +decode.causeLocation);
		System.out.println("CauseIndicatorCap2Enum :" +decode.causeIndicator);
		System.out.println("Extension Indicator:"+decode.extIndicator2);
	
				
		byte[] dpByte3 =CauseCapV2.encode(decode);
						
		decodeAG=CauseCapV2.decode(dpByte3);	
		
		System.out.println("Extension Indicator:"+decodeAG.extIndicator);
		System.out.println("CoadingStandardCapV2Enum :" +decodeAG.codingStandard);
		System.out.println("CauseLocationCapV2Enum :" +decodeAG.causeLocation);
		System.out.println("CauseIndicatorCap2Enum :" +decodeAG.causeIndicator);
		System.out.println("Extension Indicator:"+decodeAG.extIndicator2);
				
   	}


	
}

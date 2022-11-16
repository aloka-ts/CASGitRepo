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

import java.util.Arrays;

import org.apache.log4j.Logger;

import com.agnity.cap.v2.datatypes.enumType.CallingPartyCategoryCapV2Enum;
import com.agnity.cap.v2.exceptions.InvalidInputException;
import com.agnity.cap.v2.util.CapFunctions;
/**
 * ref - T-REC-Q.763-3.11
 * @author rnarayan
 * CallingPartyCategory non-asn decoading & encoading
 */
public class CallingPartyCategoryCapV2 {
 	
	private CallingPartyCategoryCapV2Enum callingPartyCategory;
	private static Logger logger = Logger.getLogger(CallingPartyCategoryCapV2.class);
	
	/**
	 * 
	 * @return CallingPartyCategoryCapV2Enum
	 */
	public CallingPartyCategoryCapV2Enum getCallingPartyCategory() {
		return callingPartyCategory;
	}
	
	/**
	 * 
	 * @param callingPartyCategory
	 */
	public void setCallingPartyCategory(CallingPartyCategoryCapV2Enum callingPartyCategory) {
		this.callingPartyCategory = callingPartyCategory;
	}
	
	/**
	 * 
	 * @param byte array of CallingPartyCategory
	 * @return CallingPartyCategoryCapV2 object of non-asn paraneters
	 */
	public static CallingPartyCategoryCapV2 decode(byte[] bytes)  throws InvalidInputException {
		CallingPartyCategoryCapV2 cpc = null;
			cpc = new CallingPartyCategoryCapV2();
			cpc.callingPartyCategory = CallingPartyCategoryCapV2Enum.getValue(bytes[0]);
			
			if(logger.isDebugEnabled()){
				logger.debug("CallingPartyCategory non asn decoded successfully");
			}
		
		return cpc;
	}
	
	/**
	 * 
	 * @param CallingPartyCategoryCapV2 object with non-asn parameters
	 * @return encoded byte array for CallingPartyCategory
	 */
	public static byte[] encode(CallingPartyCategoryCapV2 cpc)  throws InvalidInputException{
		byte[] bytes= null;
			int callngPartCat = cpc.callingPartyCategory.getCode(); 
			   byte b0 = (byte)(callngPartCat);
			   bytes = new byte[]{b0};
			   //CapV2Functions.hexStringToByteArray("0"+Integer.toHexString(callingPartCategory.getCode()));	
		       if(logger.isDebugEnabled()){
		    	   logger.debug("CallingPartyCategory non-asn encoded successfully.");
		       }
		   return bytes;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "CallingPartyCategory="+this.callingPartyCategory;
	}
	
	/*public static void main(String[] args) {
		CallingPartyCategoryCapV2 dt = new CallingPartyCategoryCapV2();
		dt.setCallingPartyCategory(CallingPartyCategoryCapV2Enum.ORDINARY_CALLING_SUBSCRIBER);
		
		byte[] bytes = encode(dt);
		byte[] expectedBytes = new byte[]{0x0a};
		System.out.println(Arrays.equals(bytes,expectedBytes));
		
		CallingPartyCategoryCapV2 d2 = CallingPartyCategoryCapV2.decode(bytes);
		System.out.println(d2.getCallingPartyCategory());
	    int b = 0010;
	    int c=  0001;
	    int d = ((b<<4)+c);
	    System.out.println(d);
	    System.out.println(d2.toString());
	}*/
}

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
package com.agnity.cap.v3.util;

public class CapFunctions {
  
	public static String decodeNumber(byte[] bytes,int index){
		StringBuilder number = new StringBuilder();
		for(int i=index;i<bytes.length;i++){
		 	number.append(Integer.toHexString((bytes[i]&0x0f)));
		 	number.append(Integer.toHexString((bytes[i]>>4)&0x0f));
		}
		return number.toString();
	}
	
	public static String encodenNumber(String num){
		StringBuilder number = new StringBuilder();
		for(int i=0;i<num.length();i+=2){
		 	number.append(num.charAt(i+1));
		 	number.append(num.charAt(i));
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
}

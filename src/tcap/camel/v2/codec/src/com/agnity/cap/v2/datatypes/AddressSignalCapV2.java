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

import com.agnity.cap.v2.exceptions.InvalidInputException;
import com.agnity.cap.v2.util.CapFunctions;

/**
 * ref:Q763
 * @author rnarayan
 */
public class AddressSignalCapV2 {
	
	/**
	 * encode address signal into byte byte array
	 * @param addressSignal 
	 * @return byte array
	 * @throws InvalidInputException
	 */
	public static byte[] encode(String addressSignal) throws InvalidInputException{
		if(addressSignal.length()%2!=0){
			addressSignal = addressSignal+"0";
		}
		StringBuilder number = new StringBuilder();
		for(int i=0;i<addressSignal.length();i+=2){
		 	number.append(addressSignal.charAt(i+1));
		 	number.append(addressSignal.charAt(i));
		}
		return CapFunctions.hexStringToByteArray(number.toString());
	}
	
	/**
	 * decode address signal in to string
	 * @param bytes
	 * @param index (from which index address signal starts)
	 * @return string 
	 * @throws InvalidInputException
	 */
	public static String decode(byte[] bytes,int index)  throws InvalidInputException{
		StringBuilder number = new StringBuilder();
		int length = bytes.length;
		for(int i=index;i<length;i++){
		 	number.append(Integer.toHexString((bytes[i]&0x0f)));
		 	//condition for remove filler element, in case of odd number of address signal
		 	//it have 0 in last as filler element 
		 	if((((bytes[i]>>4)&0x0F)==0) && (i==(length-1))){
			      break;	
			    }
		 	number.append(Integer.toHexString((bytes[i]>>4)&0x0f));
		}
		return number.toString();
	}
}

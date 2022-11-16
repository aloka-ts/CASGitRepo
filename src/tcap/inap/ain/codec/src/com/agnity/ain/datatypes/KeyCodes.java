/*******************************************************************************
 * Copyright (c) 2020 Agnity, Inc. All rights reserved.
 *
 * This is proprietary source code of Agnity, Inc.
 *
 * Agnity, Inc. retains all intellectual property rights associated
 * with this source code. Use is subject to license terms.
 *
 * This source code contains trade secrets owned by Agnity, Inc.
 * Confidentiality of this computer program must be maintained at
 * all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
/**
 * 
 */
package com.agnity.ain.datatypes;
import com.agnity.ain.util.Util;

import org.apache.log4j.Logger;
public class KeyCodes extends AddressSignal{

	//Instance of logger
	private static Logger logger = Logger.getLogger(KeyCodes.class);	

	// refer SR3511 table 6-3 for detail mapping 
	private static final char hexcodes[] = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', '*', '#', 'f' };


	/**
	 * Method used to encode keycodes. Format
	 *   Number of digits 
	 *   BCD format of digits
	 * @param keyCode
	 * @return
	 */
	public static byte[] encodeKeyCodes(String keyCode){

		if(logger.isDebugEnabled()){
			logger.debug("Enter encodeKeyCodes: " + keyCode);
		}
		
		//check string length for odd and even and append 0 in case of odd
		if(!(keyCode.length() %2==0)){
			keyCode=keyCode+0;
		}
		
		int actualLen = keyCode.length();
		int len = keyCode.length();
		int size = ( len + 1) / 2;

		byte[] out = new byte[size+1];
		
		// First octet contains number of DTMF digits 
		out[0] = (byte) actualLen;

		for (int i = 0, j = 1; i < len; i += 2, j++){
			byte b1 = getByteForDtmfChar(keyCode.charAt(i));
			byte b2 = 0;
			
			if ((i + 1) < len) {
				b2 = getByteForDtmfChar(keyCode.charAt(i+1)); 
			}

			out[j] = (byte) ((b2 << 4) | b1);
		}

		return out;
	}


	/**
	 * Method decodes Key Code and return DTMF digits. 
	 * @param keyCodeBuf
	 * @return
	 */
	public static String decodekeyCodes(byte[] keyCodeBuf){

		int len = keyCodeBuf.length ;
		int actualLen = keyCodeBuf[0];
		
		char []output = null;
		
		if(actualLen%2 == 0){
			output = new char[actualLen];
		}else{
			output = new char[actualLen+1];
		}
		
		int top = 0;
		for (int i = 1; i < len; i++) 
		{	
			output[top++] = hexcodes[keyCodeBuf[i] & 0xf];
			output[top++] = hexcodes[(keyCodeBuf[i] >> 4) & 0xf];
		}

		String tmpStr = new String(output);

		if(!(actualLen%2 == 0)){
			tmpStr = tmpStr.substring(0, tmpStr.length()-1) ;
		}

		return tmpStr;

	}

	/**
	 * Encodes digit to byte
	 * @param in
	 * @return
	 */
	private static byte getByteForDtmfChar(char in){
		byte retVal=0x00;

		if(in == '#'){
			retVal= 0x0e;
		}else if(in == '*'){
			retVal=0x0d;
		}else{
			retVal = (byte) (in - '0');
		}
		return retVal;
	}
}

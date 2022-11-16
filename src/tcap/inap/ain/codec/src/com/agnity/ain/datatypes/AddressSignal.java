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
package com.agnity.ain.datatypes;

import org.apache.log4j.Logger;

import com.agnity.ain.exceptions.InvalidInputException;
import com.agnity.ain.util.Util;

/**
 * Used for encoding and decoding of address signal octets 
 * used in AinDigits, Dn , BillingInd etc.
 * @author nishantsharma
 *
 */
public class AddressSignal {
	protected String addrSignal ;
	private static final char hexcodes[] = { '0', '1', '2', '3', '4', '5', '6',
		'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', '#' ,'*'};
	//Instance of logger
	private static Logger logger = Logger.getLogger(AddressSignal.class);	 
	/**
	 * This function will encode address signal.
	 * @param addrSignal
	 * @return encoded data byte[]
	 * @throws InvalidInputException 
	 */
	public static byte[] encodeAdrsSignal(String addrSignal) throws InvalidInputException{

		if(logger.isDebugEnabled())		{
			logger.debug("Enter: encodeAdrsSignal:Input--> addrSignal:" + addrSignal);		}
		if(addrSignal == null || addrSignal.equals(" "))		{
			logger.error("encodeAdrsSignal: InvalidInputException(AddressSignal is null or blank)");
			throw new InvalidInputException("AddressSignal is null or blank");
		}
		int len = addrSignal.length();
		int size = ( len + 1) / 2;
		byte[] out = new byte[size];
		for (int i = 0, j = 0; i < len; i += 2, j++) 		{
			byte b1 = (byte) (addrSignal.charAt(i) - '0');
			byte b2 = 0;
			if ((i + 1) < len) 			{
				b2 = (byte) (addrSignal.charAt(i + 1) - '0');
			}
			out[j] = (byte) ((b2 << 4) | b1);
		}
		if(logger.isDebugEnabled())		{
			logger.debug("Exit: encodeAdrsSignal:Output<-- byte[]:" + Util.formatBytes(out));		}
		return out;
	}
	/**
	 * This function will decode the address signal.
	 * @param data
	 * @param offset
	 * @param parity
	 * @return decoded data String
	 * @throws InvalidInputException 
	 */
	public static String decodeAdrsSignal(byte[] data , int offset, int parity) throws InvalidInputException	{
		if(logger.isDebugEnabled())		{
			logger.debug("Enter: decodeAdrsSignal:Input--> data:" + Util.formatBytes(data)+ " ,offset:"+ offset + " ,parity"+ parity);		}
		if(data == null)		{
			logger.error("decodeAdrsSignal: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		int len = data.length ;
		char output[] = new char[2 * (len - offset)];
		int top = 0;
		for (int i = offset; i < len; i++) 		{
			output[top++] = hexcodes[data[i] & 0xf];
			output[top++] = hexcodes[(data[i] >> 4) & 0xf];
		}
		String tmpStr = new String(output);
		tmpStr = tmpStr.substring(0, tmpStr.length()- parity) ;
		if(logger.isDebugEnabled())		{
			logger.debug("Exit: decodeAdrsSignal:Output<-- adrssignal:" + tmpStr);		}
		return tmpStr;
	}
	public void setAddrSignal(String addrSignal) 	{
		this.addrSignal = addrSignal;
	}
	public String getAddrSignal() 	{
		return addrSignal;
	}
}

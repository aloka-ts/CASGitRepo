package com.genband.inap.datatypes;

import org.apache.log4j.Logger;

import com.genband.inap.exceptions.InvalidInputException;
import com.genband.inap.util.Util;

/**
 * Used for encoding and decoding of address signal octets 
 * used in CalledPartyNum, CallingPartyNum etc.
 * @author vgoel
 *
 */
public class AddressSignal {

	String addrSignal ;
	
	private static final char hexcodes[] = { '0', '1', '2', '3', '4', '5', '6',
		'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	
	//Instance of logger
	private static Logger logger = Logger.getLogger(AddressSignal.class);	 
	 
	/**
	 * This function will encode address signal.
	 * @param addrSignal
	 * @return encoded data byte[]
	 * @throws InvalidInputException 
	 */
	public static byte[] encodeAdrsSignal(String addrSignal) throws InvalidInputException{
		if (logger.isInfoEnabled()) {
			logger.info("encodeAdrsSignal:Enter");
		}
		if(logger.isDebugEnabled())
			logger.debug("encodeAdrsSignal:Input--> addrSignal:" + addrSignal);
		if(addrSignal == null || addrSignal.equals(" ")){
			logger.error("encodeAdrsSignal: InvalidInputException(AddressSignal is null or blank)");
			throw new InvalidInputException("AddressSignal is null or blank");
		}
		int len = addrSignal.length();
		int size = ( len + 1) / 2;
		byte[] out = new byte[size];

		for (int i = 0, j = 0; i < len; i += 2, j++) {
			byte b1 = (byte) (addrSignal.charAt(i) - '0');
			byte b2 = 0;
			if ((i + 1) < len) {
				b2 = (byte) (addrSignal.charAt(i + 1) - '0');
			}

			out[j] = (byte) ((b2 << 4) | b1);
		}
		if(logger.isDebugEnabled())
			logger.debug("encodeAdrsSignal:Output<-- byte[]:" + Util.formatBytes(out));
		if (logger.isInfoEnabled()) {
			logger.info("encodeAdrsSignal:Exit");
		}
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
	public static String decodeAdrsSignal(byte[] data , int offset, int parity) throws InvalidInputException{
		if (logger.isInfoEnabled()) {
			logger.info("decodeAdrsSignal:Enter");
		}
		if(logger.isDebugEnabled())
			logger.debug("decodeAdrsSignal:Input--> data:" + Util.formatBytes(data)+ " ,offset:"+ offset + " ,parity"+ parity);
		if(data == null){
			logger.error("decodeAdrsSignal: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		int len = data.length ;
		char output[] = new char[2 * (len - offset)];
		int top = 0;

		for (int i = offset; i < len; i++) {
			output[top++] = hexcodes[data[i] & 0xf];
			output[top++] = hexcodes[(data[i] >> 4) & 0xf];
		}
		String tmpStr = new String(output);
		tmpStr = tmpStr.substring(0, tmpStr.length()- parity) ;
		
		if(logger.isDebugEnabled())
			logger.debug("decodeAdrsSignal:Output<-- adrssignal:" + tmpStr);
		if (logger.isInfoEnabled()) {
			logger.info("decodeAdrsSignal:Exit");
		}
		return tmpStr;
	}
	
	public void setAddrSignal(String addrSignal) {
		this.addrSignal = addrSignal;
	}

	public String getAddrSignal() {
		return addrSignal;
	}
}

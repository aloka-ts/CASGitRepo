package com.agnity.inapitutcs2.datatypes;

import org.apache.log4j.Logger;

import com.agnity.inapitutcs2.exceptions.InvalidInputException;
import com.agnity.inapitutcs2.util.Util;

/**
 * Used for encoding and decoding of address signal octets 
 * used in CalledPartyNum, CallingPartyNum etc.
 * @author Mriganka
 *
 */
public class AddressSignal {

	String addrSignal ;
	
	private static final char hexcodes[] = { '0', '1', '2', '3', '4', '5', '6',
		'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', '#' ,'*' };
	
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
			byte b1;
			//35 denotes decimal value of '#'  
			if(addrSignal.charAt(i)==35){
				b1=(byte) 0x0c; // # - 0x0c
			}else if(addrSignal.charAt(i) ==42){
				b1=(byte) 0x0b; // * = 0x0b
			}
			//66 and 98 denotes decimal value of 'B' and 'b' respectively  
			else if (addrSignal.charAt(i)== 66|| addrSignal.charAt(i)== 98) {
				b1=(byte) 0x0b;
			}
			//67 and 99 denotes decimal value of 'C' and 'c' respectively
			else if (addrSignal.charAt(i)== 67|| addrSignal.charAt(i)== 99) {
				b1=(byte) 0x0c;
			} else {
				b1 = (byte) (addrSignal.charAt(i) - '0');
			}
			
			byte b2 = 0;

			if ((i + 1) < len) {

				if(addrSignal.charAt(i+1)==35){
					b2=(byte) 0x0c; // # - 0x0c
				}else if(addrSignal.charAt(i+1) ==42){
					b2=(byte) 0x0b; // * = 0x0b
				}else if((addrSignal.charAt(i+1)== 67|| addrSignal.charAt(i+1)== 99)){			
					b2 = (byte) 0x0c;
				}else if (addrSignal.charAt(i+1)== 66|| addrSignal.charAt(i+1)== 98) {
                                	b2=(byte) 0x0b;
                        	}else{
					b2 = (byte) (addrSignal.charAt(i + 1) - '0');
				}
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
			logger.debug("rajeev decodeAdrsSignal:Input--> data:" + Util.formatBytes(data)+ " ,offset:"+ offset + " ,parity"+ parity);
		if(data == null){
			logger.error("decodeAdrsSignal: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		int len = data.length ;
		char output[] = new char[2 * (len - offset)];
		int top = 0;

		for (int i = offset; i < len; i++) {
			if (((data[i] & 0x0f) == 0x0e) || ((data[i] & 0x0f) == 0x0c)) {
				output[top++] = hexcodes[16]; // 16 - #
				logger.debug("idecode: #");
			}
			else if (((data[i] & 0x0f) == 0x0d) || ((data[i] & 0x0f) == 0x0b)) {
				output[top++] = hexcodes[17]; // 17 - *
				logger.debug("idecode: *");
			}
			else {
				output[top++] = hexcodes[data[i] & 0xf];
			}
			
			if ((((data[i] >> 4) & 0x0f) == 0x0e) || (((data[i] >> 4) & 0x0f) == 0x0c)) {
				output[top++] = hexcodes[16];
				logger.debug("second idecode: #");
			}
			else if ((((data[i] >> 4) & 0x0f) == 0x0d)|| (((data[i] >> 4) & 0x0f) == 0x0b)) {
				output[top++] = hexcodes[17];
				logger.debug("second digit idecode: *");
			}
			else {
				output[top++] = hexcodes[(data[i] >> 4) & 0xf];
			}

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

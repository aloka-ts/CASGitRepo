package com.agnity.map.util;

import org.apache.log4j.Logger;

import com.agnity.map.exceptions.InvalidInputException;


/**
 * This class have some utility function for conversion.
 * @author Rajeev Arya
 *
 */
public class Util {

	
	//Instance of logger
	private static Logger logger = Logger.getLogger(Util.class);	
	
	/**
	 * This function will print byte in binary form.
	 * @param n
	 * @return String
	 */
	public static String conversiontoBinary(byte n){
		StringBuilder sb = new StringBuilder("00000000"); 

		for (int bit = 0; bit < 8; bit++) { 
			if (((n >> bit) & 1) > 0) { 
				sb.setCharAt(7 - bit, '1'); 
			} 
		} 
		return sb.toString(); 
	}

	public static String toString(int dialgID){

		return "DialogueID: "+ dialgID ;
	}

	public static final char hexcodes[] = { '0', '1', '2', '3', '4', '5', '6',
		'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public  static String formatBytes(byte data[]) {
		char output[] = new char[5 * (data.length)];
		int top = 0;

		for (int i = 0; i < data.length; i++) {
			output[top++] = '0';
			output[top++] = 'x' ;
			output[top++] = hexcodes[(data[i] >> 4) & 0xf];
			output[top++] = hexcodes[data[i] & 0xf];
			output[top++] = ' ';
		}

		return (new String(output).trim());
	}
	
	public static byte[] asciToHex(String asciiVal) throws InvalidInputException{
		logger.debug("Input:"+ asciiVal);
		if(asciiVal == null || asciiVal.equals(" ")){
			throw new InvalidInputException("AddressSignal is null or blank");
		}
		asciiVal = asciiVal.toLowerCase();
		int len = asciiVal.length();
		int size = ( len + 1) / 2;
		byte[] out = new byte[size];

		for (int i = 0, j = 0; i < len; i += 2, j++) {
			byte b1 = (byte) (asciiVal.charAt(i) - '0');
			if(b1 > 9 || b1 < 0){
				b1 = (byte) (asciiVal.charAt(i) - 87);
			}
			byte b2 = 0;
			if ((i + 1) < len) {
				b2 = (byte) (asciiVal.charAt(i + 1) - '0');
				if(b2 > 9 || b2 < 0){
					b2 = (byte) (asciiVal.charAt(i) - 87);
				}
			}
			if(len >= 2)
				out[j] = (byte) ((b1 << 4) | b2);
			else
				out[j] = (byte)b1 ;
			logger.debug("b1: "+ b1 + " b2: " + b2 + " i: " + i +" j:" +j +" out[j]: "+ out[j]);
		}
		return out;
	}
	
	/**
	 * Converts byte array to Mobile Country Code. 
	 * The method expects the following format for the 
	 * first 2 bytes of input data
	 * 
	 *      ________________________________________________
	 *     |  MCC Digit 2        |     MCC Digit 1          |  
     *     |_____________________|__________________________|  octet-1
     *     |MNC Digit 3 (ignored)|     MCC Digit 3          |
     *     |_____________________|__________________________|  octet-2
     *     

	 * 
	 * @param data
	 * @return Mobile Country Code String 
	 */
	
	public static String decodeMcc(byte[] data) {
		String mcc = Integer.toHexString((data[0]&0x0f))+Integer.toHexString((data[0]>>4)&0x0f)
	             +Integer.toHexString((data[1]&0x0f));
		return mcc;
	}
	
	/**
	 * Converts an input Mobile Country Code integer value to byte array
	 * @param Mobile Country Code int
	 * @return byte array of MCC
	 */
	/*
	public static byte[] encodeMcc(int mcc) {
		byte[] bytes= null;
		char[] mccCharArray = String.valueOf(mcc).toCharArray();
		int digit1 = Integer.parseInt(Character.toString(mccCharArray[0]));
		int digit2 = Integer.parseInt(Character.toString(mccCharArray[1]));
		int digit3 = Integer.parseInt(Character.toString(mccCharArray[2]));	

		byte b0 = (byte)(((digit2<<4)+digit1)&0xff);
		byte b1 = (byte)(digit3&0x0f);
		
		return new byte[]{b0, b1};

	}
	*/
	
	/**
	 * Converts byte array to Mobile Network Code. 
	 * The method expects the following format for the
	 * first 2 bytes of the input data
	 *      ________________________________________________
	 *     |  MNC Digit 3        |     MCC Digit 3 (ignored)|  
     *     |_____________________|__________________________|  octet-1
     *     |  MNC Digit 2        |     MNC Digit 1          |
     *     |_____________________|__________________________|  octet-2
     * 
	 * @param data
	 * @return Mobile Network Code String
	 */
	public static String decodeMnc(byte[] bytes) {
		String mnc= null;
		if(((bytes[0]>>4)&0x0f)!= 0x0f){ // if not 1111, we use mnc digit 3
			mnc =Integer.toHexString((bytes[1]&0x0f))+Integer.toHexString((bytes[1]>>4)&0x0f)
			    +Integer.toHexString((bytes[0]>>4)&0x0f);	
		}else{
			mnc= Integer.toHexString((bytes[1]&0x0f))+Integer.toHexString((bytes[1]>>4)&0x0f);	
		}
		
		return mnc;
	}
	
}

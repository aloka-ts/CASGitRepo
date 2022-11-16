package com.agnity.sas.apps.util;

import org.apache.log4j.Logger;

/**
* This class have some utility function for conversion.
* @author saneja
*
*/
public class Util {

	
	//Instance of logger
	private static Logger logger = Logger.getLogger(Util.class);	
	
	private static int correlationIdStart=10000000;
	
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
		System.out.println("format bytes:"+ data.length);
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
	
	public static byte[] asciToHex(String asciiVal) throws Exception{
		logger.debug("Input:"+ asciiVal);
		if(asciiVal == null || asciiVal.equals(" ")){
			throw new Exception("AddressSignal is null or blank");
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

	public static int getNextCorrId() {
		correlationIdStart++;
		if(correlationIdStart>19999999)
			correlationIdStart=10000001;
		return correlationIdStart;
	}
	
	
}

package com.agnity.camelv2.util;

import org.apache.log4j.Logger;

import com.agnity.camelv2.enumdata.CalgPartyCatgEnum;
import com.agnity.camelv2.exceptions.InvalidInputException;

public class NonAsnArg {
	
	
	private static Logger logger = Logger.getLogger(NonAsnArg.class);	 
	/**
	 * This function will encode calling Party Category.
	 * @param calgPartyCatgEnum
	 * @return byte[]
	 */
	public static byte[] encodeCalgPartyCatg(CalgPartyCatgEnum calgPartyCatgEnum){
		
		logger.info("encodeCalgPartyCatg:Enter");
		byte[] myParams = new byte[1];
		int calgPartyCatg = calgPartyCatgEnum.getCode();
		myParams[0] = (byte)((calgPartyCatg | 0x0));
		logger.debug("encodeCalgPartyCatg:Output<--" + "\n");
		logger.debug("byte[0]-" + Util.conversiontoBinary(myParams[0]));
		logger.info("encodeCalgPartyCatg:Exit");
		return myParams ;
	}
	
	/**
	 * This function will decode the CalgPartyCategory.
	 * @param data
	 * @return CalgPartyCatgEnum
	 * @throws InvalidInputException 
	 */
	public static CalgPartyCatgEnum decodeCalgPartyCatg(byte[] data) throws InvalidInputException{
		
		logger.info("decodeCalgPartyCatg:Enter");
		logger.debug("decodeCalgPartyCatg: Input--> data:" + data);
		if(data == null){
			logger.error("decodeCalgPartyCatg: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		int calgParty = data[0]| 0x0 ;
		CalgPartyCatgEnum calgCatgEnum = CalgPartyCatgEnum.fromInt(calgParty);
		logger.debug("decodeCalgPartyCatg:Output<--");
		logger.debug(calgCatgEnum);
		logger.info("decodeCalgPartyCatg:Exit");
		return calgCatgEnum ;
	}
	
	
	public static byte[] tbcdStringEncoder(String digits){
		
		logger.info("tbcdStringEncoder:Enter");
		int len = digits.length() ;
		int size = (len + 1) / 2;
		byte[] out = new byte[size];

		for (int i = 0, j = 0; i < len ; i += 2, j++) {
			byte b1;
			byte b2 = 0xf;
			if(digits.charAt(i)== '*'){
				b1 = 0xa;
			}else if(digits.charAt(i) == '#'){
				b1 = 0xb;
			}else if(digits.charAt(i) == 'a'){
				b1 = 0xc ;
			}else if(digits.charAt(i) == 'b'){
				b1 = 0xd;
			}else if(digits.charAt(i) == 'c'){
				b1 = 0xe ;
			}else {
				b1 = (byte) (digits.charAt(i) - '0');
			}			
			
			if ((i + 1) < len) {
				if(digits.charAt(i+1)== '*'){
					b2 = 0xa;
				}else if(digits.charAt(i+1) == '#'){
					b2 = 0xb;
				}else if(digits.charAt(i+1) == 'a'){
					b2 = 0xc ;
				}else if(digits.charAt(i+1) == 'b'){
					b2 = 0xd;
				}else if(digits.charAt(i+1) == 'c'){
					b2 = 0xe ;
				}else {
					b2 = (byte) (digits.charAt(i + 1) - '0');
				}		
			}

			out[j] = (byte) ((b2 << 4) | b1);
		}
		logger.info("tbcdStringEncoder:Exit");
		return out;
	}
	
	public static final char TbcdCodes[] = { '0', '1', '2', '3', '4', '5', '6',
		'7', '8', '9', '*', '#', 'a', 'b', 'c', ' ' };
	
	public static String TbcdStringDecoder(byte data[], int offset) {
		logger.info("TbcdStringDecoder:Enter");
		int len = data.length ;
		char output[] = new char[2 * ( len - offset) ];
		int top = 0;

		for (int i = offset; i < len; i++) {
			output[top++] = TbcdCodes[data[i] & 0xf];
			output[top++] = TbcdCodes[(data[i] >> 4) & 0xf];
		}
		String tmpStr = new String(output);
		logger.info("TbcdStringDecoder:Exit");
		return (tmpStr.substring(0, tmpStr.length()).trim());
		

	}
	
	/*
	 * Encoding for the non-decimal characters: 1011 (*), 1100 (#).
	 */
	public static final char CollectedDigitsCodes[] = { '0', '1', '2', '3', '4', '5', '6',
		'7', '8', '9', 'a', '*', '#'};
	
	/**
	 * This message will encode the endOfReplyDigit, cancelDigit, and startDigit of PCarg 
	 * The endOfReplyDigit, cancelDigit, and startDigit parameters 
	 * have been designated as OCTET STRING, and are to be encoded as BCD, one digit per octet only, 
	 * contained  in the four least significant bits of each OCTET. The following encoding shall
	 * be applied for the non-decimal characters: 1011 (*), 1100 (#).
	 */
	public static byte[] collectedDigitsEncoder(String digits){
		
		logger.info("collectedDigitsEncoder:Enter");
		int size = digits.length();
		byte[] out = new byte[size];

		for (int i = 0; i<size; i++) {
			byte b1 = 0x0;
			if(digits.charAt(i) == '*')
				b1 = 0xb;
			else if(digits.charAt(i) == '#')
				b1 = 0xc;
			else
				b1 = (byte) (digits.charAt(i) - '0');
			
			out[i] = b1;
		}
		
		logger.info("collectedDigitsEncoder:Exit");
		return out;
	}
	
	
	/**
	 * This message will decode the endOfReplyDigit, cancelDigit, and startDigit of PCarg 
	 */
	public static String collectedDigitsDecoder(byte data[]) throws InvalidInputException {
		logger.info("collectedDigitsDecoder:Enter");
		
		if(data == null ){
			logger.error("collectedDigitsDecoder: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		String out = "";
		for(int i=0; i<data.length; i++){
			out += CollectedDigitsCodes[ data[i] & 0xf ];
		}
		
		logger.info("collectedDigitsDecoder:Exit");		
		return out;
	}
	
}

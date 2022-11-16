package com.agnity.win.datatypes;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.MINType;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for MINType .
 * It is a representation of a MS Mobile Identification Number coded in BCD
  It is 10 digits in length
   @author Supriya Jain
 */
public class NonASNMINType {
	/*
	 * MINType H G F E D C B A  octet 
	 *         Digit 2 Digit 1    1 
	 *         Digit 4 Digit 3    2 
	 *         Digit 6 Digit 5    3 
	 *         Digit 8 Digit 7    4 
	 *         Digit 10 Digit 9   5
	 * 
	 * Digit value Digit n, where n={0,1, 2, ?, 9} 
	 * (octets 1-5) Bits D C B A        Value   Meaning 
	 * 					 0 0 0 0          0      Digit = 0 or filler. 0 0 0 1 1 Digit = 1.
	 * 					 0 0 1 0 		  2      Digit = 2. 
	 * 					 0 0 1 1 		  3 	 Digit = 3.
	 *  				 0 1 0 0 		  4 	 Digit = 4. 
	 *  				 0 1 0 1 		  5 	 Digit = 5. 
	 *  				 0 1 1 0          6 	 Digit = 6. 
	 *  				 0 1 1 1 		  7 	 Digit = 7. 
	 *  				 1 0 0 0 		  8      Digit = 8.
	 * 					 1 0 0 1          9      Digit = 9.
	 *  				 X X X X - Other values reserved
	 */

	String bcdCode;
	private static final char digitCodes[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9' };
	// Instance of logger
	private static Logger logger = Logger.getLogger(NonASNMINType.class);

	/**
	 * This function will encode MINType.
	 * 
	 * @param bcdCode
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeMINType(String bcdCode)
			throws InvalidInputException {
		logger.info("encodeMINType");
		if (logger.isDebugEnabled())
			logger.debug("encodeMINType--> bcdCode:" + bcdCode);
		if (bcdCode == null || bcdCode.equals(" ")) {
			logger.error("encodeMINType: InvalidInputException(bcdCode is null or blank)");
			throw new InvalidInputException("bcdCode is null or blank");
		}
		int len = bcdCode.length();
		int size = (len + 1) / 2;
		byte[] out = new byte[size];

		for (int i = 0, j = 0; i < len; i += 2, j++) {
			if(!(Character.isLetter(bcdCode.charAt(i))))
		{
			byte b1 = (byte) (bcdCode.charAt(i) - '0');
			byte b2 = 0;
			if ((i + 1) < len) {
				b2 = (byte) (bcdCode.charAt(i + 1) - '0');
			}

			out[j] = (byte) ((b2 << 4) | b1);
		}
		else
		{
		  logger.error("encodeMINType: InvalidInputException(bcdCode contains alphanumeric characters)");
			throw new InvalidInputException("bcdCode contains alphanumeric characters");
		}
		}
		if (logger.isDebugEnabled())
			logger.debug("encodeMINType:Output<-- byte[]:"
					+ Util.formatBytes(out));
		logger.info("encodeMINType:Exit");
		return out;
	}

	/**
	 * This function will encode NonASN MINType to ASN MINType object
	 * @param nonASNMINType
	 * @return MINType
	 * @throws InvalidInputException
	 */
	public static MINType encodeMINType(NonASNMINType nonASNMINType)
			throws InvalidInputException {
		
		logger.info("Before encodeMINType : nonASN to ASN");
		MINType mINType = new MINType();
		mINType.setValue(encodeMINType(nonASNMINType.getMINType()));
		logger.info("After encodeMINType : nonASN to ASN");
		return mINType;
	}
	
	/**
	 * This function will decode the MINType.
	 * 
	 * @param data
	 * @param offset
	 * @param parity
	 * @return decoded data String
	 * @throws InvalidInputException
	 */
	public static NonASNMINType decodeMINType(byte[] data)
			throws InvalidInputException {
		logger.info("decodeMINType:Enter");
		if (logger.isDebugEnabled())
			logger.debug("decodeMINType:Input--> data:"
					+ Util.formatBytes(data));
		if (data == null|| data.length==0) {
			logger.error("decodeMINType: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		NonASNMINType minType = new NonASNMINType();
		int len = data.length;
		if (len > 5) {
			logger.error("decodeMINType: InvalidInputException(data exceeds size allocated)");
			throw new InvalidInputException("data exceeds size allocated");
		}
		char output[] = new char[2 * (len)];
		int top = 0;

		for (int i = 0; i < len; i++) {
			if (((data[i] & 0xf) > 9) || ((data[i] >> 4 & 0xf) > 9)) {
				logger.error("decodeMINType: InvalidInputException(Reserved values used)");
				throw new InvalidInputException("Reserved values used");
			}
			output[top++] = digitCodes[(data[i] & 0xf)];
			output[top++] = digitCodes[((data[i] >> 4) & 0xf)];
		}
		String tmpStr = new String(output);
		tmpStr = tmpStr.substring(0, tmpStr.length());
		minType.bcdCode = tmpStr;
		if (logger.isDebugEnabled())
			logger.debug("decodeMINType:Output<-- bcdCode:" + tmpStr);
		logger.info("decodeMINType:Exit");
		return minType;
	}

	public void setMINType(String bcdCode) {
		this.bcdCode = bcdCode;
	}

	public String getMINType() {
		return bcdCode;
	}

	public String toString() {

		String obj = "MINType :" + bcdCode;
		return obj;
	}
}

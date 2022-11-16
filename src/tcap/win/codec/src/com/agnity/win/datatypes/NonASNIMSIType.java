package com.agnity.win.datatypes;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.IMSIType;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;


/*
 * This class provides encode and decode methods for IMSIType.
 * ESN parameter is used to identify a specific MS.
  It may be upto 15 digits in length
   @author Supriya Jain
 */

public class NonASNIMSIType {

	/*
	 * IMSIType
	 * 
	 * Note: IMSI may be up to 15 digits in length
	 * 
	 * H G F E D C B A octet Digit 2 Digit 1 1 Digit 4 Digit 3 2 Digit 6 Digit 5
	 * 3 Digit 8 Digit 7 4 Digit 10 Digit 9 5 Digit 12 Digit 11 6 Digit 14 Digit
	 * 13 7 filler Digit 15 8
	 * 
	 * Digit value Digit n, where n={0,1, 2, ?, 9} (octets 1-5) Bits H G F E D C
	 * B A Value Meaning 0 0 0 0 0 Digit = 0 0 0 0 1 1 Digit = 1. 0 0 1 0 2
	 * Digit = 2. 0 0 1 1 3 Digit = 3. 0 1 0 0 4 Digit = 4. 0 1 0 1 5 Digit = 5.
	 * 0 1 1 0 6 Digit = 6. 0 1 1 1 7 Digit = 7. 1 0 0 0 8 Digit = 8. 1 0 0 1 9
	 * Digit = 9. X X X X - Other values reserved 1 1 1 1 15 Filler
	 */

	String code;
	private static final char digitCodes[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'f' };
	// Instance of logger
	private static Logger logger = Logger.getLogger(NonASNIMSIType.class);

	/**
	 * This function will encode IMSIType.
	 * @param code
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeIMSIType(String code)
			throws InvalidInputException {
		if(logger.isInfoEnabled())
		logger.info("encodeIMSIType");
		if (logger.isDebugEnabled())
			logger.debug("encodeIMSIType--> code:" + code);
		if (code == null || code.equals("")) {
			logger.error("encodeIMSIType: InvalidInputException(code is null or blank)");
			throw new InvalidInputException("code is null or blank");
		}
		int len = code.length();
		int size = (len + 1) / 2;
		byte[] out = new byte[size];

		for (int i = 0, j = 0; i < len; i += 2, j++) {
			byte b1 = (byte) (code.charAt(i) - '0');
			byte b2 = 0;
			if ((i + 1) < len) {
				if ((i == len - 2) && (code.charAt(i + 1) == 'f'))
					b2 = 0x0f;
				else
					b2 = (byte) (code.charAt(i + 1) - '0');
			}

			out[j] = (byte) ((b2 << 4) | b1);
		}
		if (logger.isDebugEnabled())
			logger.debug("encodeIMSIType:Output<-- byte[]:"
					+ Util.formatBytes(out));
		if(logger.isInfoEnabled())
		logger.info("encodeIMSIType:Exit");
		return out;
	}

	/**
	 * This function will encode NonASN IMSIType to ASN IMSIType object
	 * @param nonASNIMSIType
	 * @return IMSIType
	 * @throws InvalidInputException
	 */
	public static IMSIType encodeIMSIType(NonASNIMSIType nonASNIMSIType)
			throws InvalidInputException {
		if(logger.isInfoEnabled())
		logger.info("Before encodeIMSIType : nonASN to ASN");
		IMSIType imsiType = new IMSIType();
		imsiType.setValue(encodeIMSIType(nonASNIMSIType.getIMSIType()));
		if(logger.isInfoEnabled())
		logger.info("After encodeIMSIType : nonASN to ASN");
		return imsiType;
	}
	
	/**
	 * This function will decode the IMSIType.
	 * @param data
	 * @return decoded NonASNIMSIType object
	 * @throws InvalidInputException
	 */
	public static NonASNIMSIType decodeIMSIType(byte[] data)
			throws InvalidInputException {
		if(logger.isInfoEnabled())
		logger.info("decodeIMSIType:Enter");
		if (logger.isDebugEnabled())
			logger.debug("decodeIMSIType:Input--> data:"
					+ Util.formatBytes(data));
		if (data == null || data.length == 0) {
			logger.error("decodeIMSIType: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		NonASNIMSIType imsiType = new NonASNIMSIType();
		int len = data.length;
		if (len > 8) {
			logger.error("decodeIMSIType: InvalidInputException(data exceeds size allocated)");
			throw new InvalidInputException("data exceeds size allocated");
		}
		char output[] = new char[2 * (len)];
		int top = 0;

		for (int i = 0; i < len; i++) {

			if ((i == len - 1) && (((data[i] >> 4) & 0xf) == 15)) {
				output[top++] = digitCodes[(data[i] & 0xf)];
				output[top] = digitCodes[10];
				break;
			} else {
				if (((data[i] & 0xf) > 9) || ((data[i] >> 4 & 0xf) > 9)) {
					logger.error("decodeIMSIType: InvalidInputException(Reserved values used)");
					throw new InvalidInputException("Reserved values used");
				}
				output[top++] = digitCodes[(data[i] & 0xf)];
				output[top++] = digitCodes[((data[i] >> 4) & 0xf)];
			}
		}
		String tmpStr = new String(output);
		tmpStr = tmpStr.substring(0, tmpStr.length());
		imsiType.code = tmpStr;
		if (logger.isDebugEnabled())
			logger.debug("decodeIMSIType:Output<-- code:" + tmpStr);
		if(logger.isInfoEnabled())
		logger.info("decodeIMSIType:Exit");
		return imsiType;
	}

	public void setIMSIType(String code) {
		this.code = code;
	}

	public String getIMSIType() {
		return code;
	}

	public String toString() {

		String obj = "IMSIType :" + code;
		return obj;
	}
}

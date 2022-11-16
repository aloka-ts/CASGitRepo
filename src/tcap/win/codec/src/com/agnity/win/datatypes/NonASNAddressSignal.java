package com.agnity.win.datatypes;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/**
 * Used for encoding and decoding of address signal octets used in Dialed Digits
 * 
 * @author vgoel
 * 
 */
public class NonASNAddressSignal implements Serializable{  

	String addrSignal;

	private static final char hexcodes[] = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', '?', 'B', 'C', '*', '#', 'f' };

	private static byte getByteForChar(char in) {
		switch (in) {
		case '0':
			return (byte) 0x00;
		case '1':
			return (byte) 0x01;
		case '2':
			return (byte) 0x02;
		case '3':
			return (byte) 0x03;
		case '4':
			return (byte) 0x04;
		case '5':
			return (byte) 0x05;
		case '6':
			return (byte) 0x06;
		case '7':
			return (byte) 0x07;
		case '8':
			return (byte) 0x08;
		case '9':
			return (byte) 0x09;
		case '?':
			return (byte) 0x0a;
		case 'B':
			return (byte) 0x0b;
		case 'C':
			return (byte) 0x0c;
		case '*':
			return (byte) 0x0d;
		case '#':
			return (byte) 0x0e;
		case 'f':
			return (byte) 0x0f;
		default:
			return (byte) 0x00;
		}
	}

	// Instance of logger
	private static Logger logger = Logger.getLogger(NonASNAddressSignal.class);

	/**
	 * This function will encode address signal.
	 * 
	 * @param addrSignal
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeAdrsSignal(String addrSignal)
			throws InvalidInputException {
		logger.info("encodeAdrsSignal:Enter");
		if (logger.isDebugEnabled())
			logger.debug("encodeAdrsSignal:Input--> addrSignal:" + addrSignal);
		if (addrSignal == null || addrSignal.equals(" ")) {
			logger.error("encodeAdrsSignal: InvalidInputException(AddressSignal is null or blank)");
			throw new InvalidInputException("AddressSignal is null or blank");
		}
		int len = addrSignal.length();
		int size = (len + 1) / 2;
		byte[] out = new byte[size];

		for (int i = 0, j = 0; i < len; i += 2, j++) {
			// byte b1 = (byte) (addrSignal.charAt(i) - '0');
			byte b1 = getByteForChar(addrSignal.charAt(i));
			byte b2 = 0;
			if ((i + 1) < len) {
				// b2 = (byte) (addrSignal.charAt(i + 1) - '0');
				b2 = getByteForChar(addrSignal.charAt(i + 1));
			}

			out[j] = (byte) ((b2 << 4) | b1);
		}
		if (logger.isDebugEnabled())
			logger.debug("encodeAdrsSignal:Output<-- byte[]:"
					+ Util.formatBytes(out));
		logger.info("encodeAdrsSignal:Exit");
		return out;
	}

	/**
	 * This function will decode the address signal.
	 * 
	 * @param data
	 * @param offset
	 * @param parity
	 * @return decoded data String
	 * @throws InvalidInputException
	 */
	public static String decodeAdrsSignal(byte[] data, int offset, int parity)
			throws InvalidInputException {
		logger.info("decodeAdrsSignal:Enter");
		if (logger.isDebugEnabled())
			logger.debug("decodeAdrsSignal:Input--> data:"
					+ Util.formatBytes(data) + " ,offset:" + offset
					+ " ,parity" + parity);
		if (data == null) {
			logger.error("decodeAdrsSignal: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		int len = data.length;
		char output[] = new char[2 * (len - offset)];
		int top = 0;

		for (int i = offset; i < len; i++) {
			output[top++] = hexcodes[data[i] & 0xf];
			output[top++] = hexcodes[(data[i] >> 4) & 0xf];
		}
		String tmpStr = new String(output);
		tmpStr = tmpStr.substring(0, tmpStr.length() - parity);

		if (logger.isDebugEnabled())
			logger.debug("decodeAdrsSignal:Output<-- adrssignal:" + tmpStr);
		logger.info("decodeAdrsSignal:Exit");
		return tmpStr;
	}

	public void setAddrSignal(String addrSignal) {
		this.addrSignal = addrSignal;
	}

	public String getAddrSignal() {
		return addrSignal;
	}
}

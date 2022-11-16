package com.agnity.win.datatypes;
import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.CDMAServiceOption;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;
/*
 * This class provides encode and decode methods for NonASNCDMAServiceOption
 * as per definition given in Circuit Mode Services :N.S0008-0 v 1.0 section 6.5.2.f
  @author Supriya Jain
 */
// Refer to CDMAServiceOptionConstants. java for description of CDMAServiceOption Values
public class NonASNCDMAServiceOption {
	private static Logger logger = Logger.getLogger(NonASNCDMAServiceOption.class);

	
	public int CDMAServiceOptionVal;

	public int getCDMAServiceOption() {
		return CDMAServiceOptionVal;
	}

	public void setCDMAServiceOption(int CDMAServiceOptionVal) {
		this.CDMAServiceOptionVal = CDMAServiceOptionVal;
	}

	/**
	 * This function will encode NonASNCDMAServiceOption as per specification
	 *Circuit Mode Services :N.S0008-0 v 1.0 section 6.5.2.f
	 * @param CDMAServiceOptionVal
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeCDMAServiceOption(int CDMAServiceOptionVal)
			throws InvalidInputException {
		logger.info("encodeCDMAServiceOption");

		byte[] myParams = new byte[2];
		//CDMAServiceOptionVal is 16 bit with octet1 as significant one
		myParams[0] = (byte) (CDMAServiceOptionVal >> 8 & 0x00ff);
		myParams[1] = (byte) (CDMAServiceOptionVal & 0x00ff);
		
		if (logger.isDebugEnabled())
			logger.debug("encodeCDMAServiceOption: Encoded CDMAServiceOption: "
					+ Util.formatBytes(myParams));
		logger.info("encodeCDMAServiceOption");
		return myParams;
	}

	/**
	 * This function will encode NonASN CDMAServiceOption to ASN CDMAServiceOption object
	 * @param nonASNCDMAServiceOption
	 * @return CDMAServiceOption
	 * @throws InvalidInputException
	 */
	public static CDMAServiceOption encodeCDMAServiceOption(NonASNCDMAServiceOption nonASNCDMAServiceOption)
			throws InvalidInputException {
		
		logger.info("Before encodeCDMAServiceOption : nonASN to ASN");
		CDMAServiceOption cdmaServiceOption = new CDMAServiceOption();
		cdmaServiceOption.setValue(encodeCDMAServiceOption(nonASNCDMAServiceOption.getCDMAServiceOption()));
		logger.info("After encodeCDMAServiceOption : nonASN to ASN");
		return cdmaServiceOption;
	}
	
	/**
	 * This function will decode NonASNCDMAServiceOption as per specification
	 * Circuit Mode Services :N.S0008-0 v 1.0 section 6.5.2.f
	 * @param data bytes to be decoded
	 * @return object of NonASNCDMAServiceOption
	 * @throws InvalidInputException
	 */
	public static NonASNCDMAServiceOption decodeCDMAServiceOption (byte[] data)
			throws InvalidInputException {
		if (logger.isDebugEnabled())
			logger.debug("decodeCDMAServiceOption: Input--> data:"
					+ Util.formatBytes(data));

		if (data == null || data.length==0) {
			logger.error("decodeCDMAServiceOption: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		//octet1 and octet2 combined represents CDMAServiceOption Value
		NonASNCDMAServiceOption cdmaSvcOption = new NonASNCDMAServiceOption();
		cdmaSvcOption.CDMAServiceOptionVal = ((data[0] & 0xff) << 8) | (data[1] & 0xff);
		

		if (logger.isDebugEnabled())
			logger.debug("decodeCDMAServiceOption: Output<--" + cdmaSvcOption.toString());
		logger.info("decodeCDMAServiceOption");
		return cdmaSvcOption;
	}

	public String toString() {

		String obj = "CDMAServiceOptionVal :" + CDMAServiceOptionVal;
		return obj;
	}
}

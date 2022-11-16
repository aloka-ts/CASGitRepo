package com.agnity.win.datatypes;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.agnity.win.enumdata.AccessDeniedReasonEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for NonASNAccessDeniedReason
 * as per definition given in TIA-EIA-41-D, section 6.5.2.1.
 * @author Supriya Jain
 */

public class NonASNAccessDeniedReason implements Serializable{
	private static Logger logger = Logger
			.getLogger(NonASNAccessDeniedReason.class);

	/**
	 * @see AccessDeniedReasonEnum    
	 */
	AccessDeniedReasonEnum accessDeniedReason;

	public AccessDeniedReasonEnum getAccessDeniedReasonEnum() {
		return accessDeniedReason;
	}

	public void setAccessDeniedReason(AccessDeniedReasonEnum accessDeniedReason) {
		this.accessDeniedReason = accessDeniedReason;
	}

	/**
	 * This function will encode NonASNAccessDeniedReason as per specification
	 * TIA-EIA-41-D section 6.5.2.1
	 * @param accessDeniedReasonEnum Access Denied Reason value
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeAccessDeniedReason(
			AccessDeniedReasonEnum accessDeniedReasonEnum)
			throws InvalidInputException {
		logger.info("encodeAccessDeniedReason");
		byte[] myParms = new byte[1];

		int AccessDeniedReasonVal;
		if (accessDeniedReasonEnum == null) {
			logger.info("encodeAccessDeniedReason: Invalid Value of Type Of NonASNAccessDeniedReason");
			throw new InvalidInputException("Type Of NonASNAccessDeniedReason is null");
		}
		AccessDeniedReasonVal = accessDeniedReasonEnum.getCode();

		// encoding
		myParms[0] = (byte) (AccessDeniedReasonVal & 0xFF);
		if (logger.isDebugEnabled())
			logger.debug("encodeAccessDeniedReason: Encoded : "
					+ Util.formatBytes(myParms));
		logger.info("encodeAccessDeniedReason");
		return myParms;
	}

	/**
	 * This function will decode NonASNAccessDeniedReason as per specification
	 * TIA-EIA-41-D section 6.5.2.1
	 * @param data  bytes to be decoded
	 * @return object of NonASNAccessDeniedReason
	 * @throws InvalidInputException
	 */
	public static NonASNAccessDeniedReason decodeAccessDeniedReason(byte[] data)
			throws InvalidInputException {

		if (logger.isDebugEnabled())
			logger.debug("decodeAccessDeniedReason: Input--> data:"
					+ Util.formatBytes(data));
		// check to see if there is no byte of data
		if (data == null || data.length == 0) {
			logger.error("decodeAccessDeniedReason: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		NonASNAccessDeniedReason accessDeniedReasonData = new NonASNAccessDeniedReason();
		accessDeniedReasonData.setAccessDeniedReason(AccessDeniedReasonEnum
				.fromInt(data[0] & 0xff));

		if (logger.isDebugEnabled())
			logger.debug("decodeAccessDeniedReason: Output<--"
					+ accessDeniedReasonData.toString());
		logger.info("decodeAccessDeniedReason");
		return accessDeniedReasonData;
	}

	public String toString() {

		String obj = "NonASNAccessDeniedReason  :" + accessDeniedReason;
		return obj;
	}

}

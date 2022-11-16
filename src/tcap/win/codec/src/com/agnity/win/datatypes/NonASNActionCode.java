package com.agnity.win.datatypes;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.ActionCode;
import com.agnity.win.enumdata.ActionCodeEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for NonASNActionCode
 * as per definition given in TIA-EIA-41-D, section 6.5.2.2.
 *  @author Supriya Jain
 */
public class NonASNActionCode {
	private static Logger logger = Logger.getLogger(NonASNActionCode.class);
	/**
	 * @see ActionCodeEnum
	 */
	LinkedList<ActionCodeEnum> actionCode;

	public LinkedList<ActionCodeEnum> getActionCode() {
		return actionCode;
	}

	public void setActionCode(LinkedList<ActionCodeEnum> actionCode) {
		this.actionCode = actionCode;
	}

	/**
	 * This function will encode NonASNActionCode as per specification
	 * TIA-EIA-41-D section 6.5.2.2
	 * 
	 * @param list
	 *            of ActionCodeEnums
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeActionCode(LinkedList<ActionCodeEnum> actionCode)
			throws InvalidInputException {
		logger.info("encodeActionCode");
		if (actionCode == null) {
			logger.error("encodeActionCode: InvalidInputException(actionCode is null)");
			throw new InvalidInputException("actionCode is null");
		}

		int i = 0;
		byte[] myParams = new byte[actionCode.size()];
		for (ActionCodeEnum ac : actionCode) {
			myParams[i++] = (byte) (ac.getCode());
		}
		// every decoded byte represents an action
		if (logger.isDebugEnabled())
			logger.debug("encodeActionCode: Encoded Action Code: "
					+ Util.formatBytes(myParams));
		logger.info("encodeActionCode");
		return myParams;
	}
	
	/**
	 * This function will encode NonASN ActionCode to ASN ActionCode object
	 * @param nonASNActionCode
	 * @return ActionCode
	 * @throws InvalidInputException
	 */
	public static ActionCode encodeActionCode(NonASNActionCode nonASNActionCode)
			throws InvalidInputException {
		
		logger.info("Before encodeActionCode : nonASN to ASN");
		ActionCode actionCode = new ActionCode();
		actionCode.setValue(encodeActionCode(nonASNActionCode.getActionCode()));
		logger.info("After encodeActionCode : nonASN to ASN");
		return actionCode;
	}

	/**
	 * This function will decode NonASNActionCode as per specification
	 * TIA-EIA-41-D section 6.5.2.2
	 * 
	 * @param data
	 *            bytes to be decoded
	 * @return object of NonASNActionCode
	 * @throws InvalidInputException
	 */
	public static NonASNActionCode decodeActionCode(byte[] data)
			throws InvalidInputException {
		if (logger.isDebugEnabled())
			logger.debug("decodeActionCode: Input--> data:"
					+ Util.formatBytes(data));

		if (data == null || data.length == 0) {
			logger.error("decodeActionCode: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		NonASNActionCode actCode = new NonASNActionCode();
		actCode.actionCode = new LinkedList<ActionCodeEnum>();
		// every byte represents an action ,decoding gives list of actions
		for (int i = 0; i < data.length; i++) {
			actCode.actionCode.add(ActionCodeEnum.fromInt(data[i] & 0xFF));
		}

		if (logger.isDebugEnabled())
			logger.debug("decodeActionCode: Output<--" + actCode.toString());
		logger.info("decodeActionCode");
		return actCode;
	}

	public String toString() {

		String obj = "actionCode :" + actionCode;
		return obj;
	}

}

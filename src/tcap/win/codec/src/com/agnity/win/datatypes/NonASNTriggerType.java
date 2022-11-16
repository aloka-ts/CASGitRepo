package com.agnity.win.datatypes;

import org.apache.log4j.Logger;

import com.agnity.win.enumdata.TriggerTypeEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for NonASNTriggerType
 *  as per definition given in TIA/EIA/IS-771, section 6.5.2.dh.
 *   @author Supriya Jain
 */
public class NonASNTriggerType {
	private static Logger logger = Logger.getLogger(NonASNTriggerType.class);

	/**
	 * @see TriggerTypeEnum
	 */
	TriggerTypeEnum triggerTypeEnum;

	public TriggerTypeEnum getTriggerTypeEnum() {
		return triggerTypeEnum;
	}

	public void setTriggerTypeEnum(TriggerTypeEnum triggerTypeEnum)
			throws InvalidInputException {
		this.triggerTypeEnum = triggerTypeEnum;
	}

	/**
	 * This function will encode NonASNTriggerType.
	 * 
	 * @param TriggerTypeEnum
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeTriggerType(TriggerTypeEnum triggerTypeEnum)
			throws InvalidInputException {
		logger.info("encodeTriggerTypeEnum");
		if (triggerTypeEnum == null) {
			logger.error("encodeTriggerType: InvalidInputException(triggerType is null)");
			throw new InvalidInputException("triggerType is null");
		}
		byte[] myParms = new byte[1];

		int TriggerTypeVal;

		TriggerTypeVal = triggerTypeEnum.getCode();
		// encoding starts
		myParms[0] = (byte) (TriggerTypeVal & 0xFF);
		if (logger.isDebugEnabled())
			logger.debug("encodeTriggerTypeEnum: Encoded : "
					+ Util.formatBytes(myParms));
		logger.info("encodeTriggerTypeEnum");
		return myParms;
	}

	/**
	 * This function will decode NonASNTriggerType.
	 * 
	 * @param data
	 * @return object of NonASNTriggerType
	 * @throws InvalidInputException
	 */
	public static NonASNTriggerType decodeTriggerType(byte[] data)
			throws InvalidInputException {
		if (logger.isDebugEnabled())
			logger.debug("decodeTriggerType: Input--> data:"
					+ Util.formatBytes(data));
		if (data == null || data.length==0) {
			logger.error("decodeTriggerType: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		NonASNTriggerType triggerTypeData = new NonASNTriggerType();
		triggerTypeData.setTriggerTypeEnum(TriggerTypeEnum
				.fromInt(data[0] & 0xff));
		if (logger.isDebugEnabled())
			logger.debug("decodeTriggerType: Output<--"
					+ triggerTypeData.toString());
		logger.info("decodeTriggerType");
		return triggerTypeData;
	}

	public String toString() {

		String obj = "TriggerTypeEnum :" + triggerTypeEnum;
		return obj;
	}

}

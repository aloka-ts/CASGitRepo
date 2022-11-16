/****
 * Copyright (c) 2013 Agnity, Inc. All rights reserved.
 * 
 * This is proprietary source code of Agnity, Inc.
 * 
 * Agnity, Inc. retains all intellectual property rights associated with this
 * source code. Use is subject to license terms.
 * 
 * This source code contains trade secrets owned by Agnity, Inc. Confidentiality
 * of this computer program must be maintained at all times, unless explicitly
 * authorized by Agnity, Inc.
 ****/
package com.agnity.cap.v3.datatypes;

import org.apache.log4j.Logger;

import com.agnity.cap.v3.datatypes.enumType.AlertingPatternCapV3Enum;
import com.agnity.cap.v3.exceptions.InvalidInputException;

/**
 *  ref: 3GPP TS 29.002 [13]
 * @author rnarayan
 */
public class AlertingPatternCapV3 {

	private static Logger logger = Logger.getLogger(AlertingPatternCapV3.class);
	private AlertingPatternCapV3Enum alertPattern;
	
	/**
	 * @return AlertingPatternCapV3Enum
	 */
	public AlertingPatternCapV3Enum getAlertingPattern() {
		return alertPattern;
	}
	
	/**
	 * set AlertPattern 
	 * @param alertPattern
	 */
	public void setAlertingPattern(AlertingPatternCapV3Enum alertPattern) {
		this.alertPattern = alertPattern;
	}
	
	/**
	 * decode alerting pattern
	 * @param bytes of alerting pattern
	 * @return AlertingPatternCapV3 object
	 * @throws InvalidInputException
	 */
	public static AlertingPatternCapV3 decode(byte[] bytes)  throws InvalidInputException{
		AlertingPatternCapV3 ap= null;
			ap = new AlertingPatternCapV3();
			ap.alertPattern = AlertingPatternCapV3Enum.getValue((byte)(bytes[0]&0x0f)); 
		return ap;
	}
	
	/**
	 * encode alerting pattern 
	 * @param AlertingPatternCapV3 object
	 * @return byte array of alerting pattern 
	 * @throws InvalidInputException
	 */
	public static byte[] encode(AlertingPatternCapV3 ap)  throws InvalidInputException{
		byte[] bytes= null;
			int alertPattern = ap.alertPattern.getCode();
			byte b1 = (byte)(alertPattern&0x0f);
			bytes = new byte[]{b1};

		return bytes;
	}
}

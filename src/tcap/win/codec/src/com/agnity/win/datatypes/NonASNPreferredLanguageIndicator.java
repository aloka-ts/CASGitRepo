package com.agnity.win.datatypes;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.PreferredLanguageIndicator;
import com.agnity.win.enumdata.PreferredLanguageEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for NonASNPreferredLanguageIndicator
 * as per definition given in TIA-EIA-41-D, section 6.5.2.96.
 *  @author Supriya Jain
 */
public class NonASNPreferredLanguageIndicator {
	private static Logger logger = Logger
			.getLogger(NonASNPreferredLanguageIndicator.class);

	/**
	 * @see PreferredLanguageEnum
	 */
	LinkedList<PreferredLanguageEnum> PreferredLanguage;

	public LinkedList<PreferredLanguageEnum> getPreferredLanguage() {
		return PreferredLanguage;
	}

	public void setPreferredLanguage(
			LinkedList<PreferredLanguageEnum> PreferredLanguage) {
		this.PreferredLanguage = PreferredLanguage;
	}

	/**
	 * This function will encode NonASNPreferredLanguageIndicator as per
	 * specification TIA-EIA-41-D section 6.5.2.96
	 * 
	 * @param list
	 *            of PreferredLanguageEnum
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodePreferredLanguageIndicator(
			LinkedList<PreferredLanguageEnum> PreferredLanguage)
			throws InvalidInputException {
		logger.info("encodePreferredLanguageIndicator");
		if (PreferredLanguage == null) {
			logger.error("encodePreferredLanguageIndicator: InvalidInputException(PreferredLanguage is null)");
			throw new InvalidInputException("PreferredLanguage is null");
		}
		int i = 0;
		byte[] myParams = new byte[PreferredLanguage.size()];
		for (PreferredLanguageEnum pl : PreferredLanguage) {
			myParams[i++] = (byte) (pl.getCode());
		}
		if (logger.isDebugEnabled())
			logger.debug("encodePreferredLanguageIndicator: Encoded : "
					+ Util.formatBytes(myParams));
		logger.info("encodePreferredLanguageIndicator");
		return myParams;
	}

	/**
	 * This function will encode Non ASN PreferredLanguageIndicator to ASN PreferredLanguageIndicator object
	 * @param nonASNPreferredLanguageIndicator
	 * @return PreferredLanguageIndicator
	 * @throws InvalidInputException
	 */
	public static PreferredLanguageIndicator encodePreferredLanguageIndicator(NonASNPreferredLanguageIndicator nonASNPreferredLanguageIndicator)
			throws InvalidInputException {
		
		logger.info("Before encodePreferredLanguageIndicator : nonASN to ASN");
		PreferredLanguageIndicator preferredLanguageIndicator = new PreferredLanguageIndicator();
		preferredLanguageIndicator.setValue(encodePreferredLanguageIndicator(nonASNPreferredLanguageIndicator.getPreferredLanguage()));
		logger.info("After encodePreferredLanguageIndicator : nonASN to ASN");
		return preferredLanguageIndicator;
	}
	
	/**
	 * This function will decode NonASNPreferredLanguageIndicator as per
	 * specification TIA-EIA-41-D section 6.5.2.96
	 * 
	 * @param data
	 *            bytes to be decoded
	 * @return object of NonASNPreferredLanguageIndicator
	 * @throws InvalidInputException
	 */

	public static NonASNPreferredLanguageIndicator decodePreferredLanguageIndicator(
			byte[] data) throws InvalidInputException {
		if (logger.isDebugEnabled())
			logger.debug("decodePreferredLanguageIndicator: Input--> data:"
					+ Util.formatBytes(data));
		if (data == null || data.length == 0) {
			logger.error("decodePreferredLanguageIndicator: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		
		NonASNPreferredLanguageIndicator prefLang = new NonASNPreferredLanguageIndicator();
		prefLang.PreferredLanguage = new LinkedList<PreferredLanguageEnum>();

		// every byte represents an Preferred Language ,decoding gives list of
		// Preferred Languages
		for (int i = 0; i < data.length; i++) {
			prefLang.PreferredLanguage.add(PreferredLanguageEnum
					.fromInt(data[i] & 0xFF));
		}
		if (logger.isDebugEnabled())
			logger.debug("decodePreferredLanguageIndicator: output : "
					+ prefLang.toString());
		logger.info("decodePreferredLanguageIndicator");
		return prefLang;
	}

	public String toString() {

		String obj = "PreferredLanguage :" + PreferredLanguage;
		return obj;
	}

}

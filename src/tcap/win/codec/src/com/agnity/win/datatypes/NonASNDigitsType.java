package com.agnity.win.datatypes;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.DigitsType;
import com.agnity.win.enumdata.EncodingSchemeEnum;
import com.agnity.win.enumdata.NatureOfNumAvailIndEnum;
import com.agnity.win.enumdata.NatureOfNumIndEnum;
import com.agnity.win.enumdata.NatureOfNumPresentationIndEnum;
import com.agnity.win.enumdata.NatureOfNumScreenIndEnum;
import com.agnity.win.enumdata.NumPlanEnum;
import com.agnity.win.enumdata.TypeOfDigitsEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for Digits
 * as per definition given in TIA-EIA-41-D, section 6.5.2.58.
 */

public class NonASNDigitsType extends NonASNAddressSignal implements Serializable{

	private static Logger logger = Logger.getLogger(NonASNDigitsType.class);

	/*
	 * Type Of Digits
	 */
	TypeOfDigitsEnum typeOfDigits;

	/*
	 * Nature of Number - Number Indicator - Presentation Indicator - Screening
	 * Indicator - Availability Indicator
	 */
	NatureOfNumIndEnum natOfNumInd;
	NatureOfNumAvailIndEnum natOfNumAvlInd;
	NatureOfNumPresentationIndEnum natOfNumPresInd;
	NatureOfNumScreenIndEnum natOfNumScrnInd;

	/*
	 * Numbering Plan
	 */
	NumPlanEnum numberingPlan;

	/*
	 * Encoding Scheme
	 */
	EncodingSchemeEnum encoding;

	/**
	 * This function will decode Digits as per specification TIA-EIA-41-D
	 * section 6.5.2.58
	 * 
	 * @param data
	 * @return decoded Digits
	 * @throws InvalidInputException
	 */
	public static NonASNDigitsType decodeDigits(byte[] data)
			throws InvalidInputException {
		if (logger.isDebugEnabled())
			logger.debug("decodeDigits: Input--> data:"
					+ Util.formatBytes(data));

		if (data == null) {
			logger.error("decodeDigits: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}

		NonASNDigitsType digits = new NonASNDigitsType();

		digits.typeOfDigits = TypeOfDigitsEnum.fromInt((int) data[0]);
		digits.natOfNumInd = NatureOfNumIndEnum.fromInt((int) (data[1] & 0x01));
		digits.natOfNumPresInd = NatureOfNumPresentationIndEnum
				.fromInt((int) ((data[1] >> 1) & 0x01));
		digits.natOfNumAvlInd = NatureOfNumAvailIndEnum
				.fromInt((int) ((data[1] >> 2) & 0x01));
		digits.natOfNumScrnInd = NatureOfNumScreenIndEnum
				.fromInt((int) ((data[1] >> 4) & 0x03));

		digits.encoding = EncodingSchemeEnum.fromInt((int) (data[2] & 0x0F));
		digits.numberingPlan = NumPlanEnum
				.fromInt((int) ((data[2] >> 4) & 0x0F));

		// Check if number of digits is not zero
		if (data[3] > 0) {
			int parity = data[3] % 2;
			digits.addrSignal = NonASNAddressSignal.decodeAdrsSignal(data, 4,
					parity);
		}

		if (logger.isDebugEnabled())
			logger.debug("decodeDigits: Output<--" + digits.toString());
		logger.info("decodeDigits");

		return digits;
	}

	/**
	 * This function will encode Digits as per specification TIA-EIA-41-D
	 * section 6.5.2.58
	 * 
	 * @param addrSignal
	 *            - Digits to be encoded
	 * @param tod
	 *            - Type of Digits
	 * @param nonInd
	 *            - Nature of Number - National Indicator
	 * @param nonPresInd
	 *            - Nature of Number - Presentation Indicator
	 * @param nonAvailInd
	 *            - Nature of Number - Available Indicator
	 * @param nonScreenInd
	 *            - Nature of Number - Screening Indicator
	 * @param np
	 *            - Numbering Plan
	 * @param es
	 *            - Encoding scheme
	 * @return byte[] of encoded Digits
	 * @throws InvalidInputException
	 */
	public static byte[] encodeDigits(String addrSignal, TypeOfDigitsEnum tod,
			NatureOfNumIndEnum nonInd, NatureOfNumAvailIndEnum nonAvailInd,
			NatureOfNumPresentationIndEnum nonPresInd,
			NatureOfNumScreenIndEnum nonScreenInd, NumPlanEnum np,
			EncodingSchemeEnum es) throws InvalidInputException {

		logger.info("encodeDigits");

		int encodingSch;
		if (es == null) {
			logger.info("encodeDigits: Using BCD as deafult encoding scheme");
			encodingSch = 1;
		} else {
			encodingSch = es.getCode();
		}

		byte[] bcdDigits = NonASNAddressSignal.encodeAdrsSignal(addrSignal);
		int seqLength = 4 + bcdDigits.length;

		int i = 0;
		byte[] myParams = new byte[seqLength];

		int typeOfDig;
		if (tod == null) {
			logger.info("encodeDigits: Invalid Value of Type Of Digits");
			throw new InvalidInputException("Type Of Digits is null");
		}
		typeOfDig = tod.getCode();

		int natureOfNum = 0;

		if (nonInd == null) {
			logger.info("encodeDigits: Using default value of NatureOfNumber");
			natureOfNum = 0; // National
		} else {
			natureOfNum = (nonInd.getCode() & 0x01);
		}

		if (nonPresInd == null) {
			logger.info("encodeDigits: NatureOfNumber Presentation Ind not present");
			throw new InvalidInputException(
					"Nature of Number - Presentation Indicator is not present");
		} else {
			natureOfNum |= ((nonPresInd.getCode() << 1) & 0x02);
		}

		if (nonAvailInd == null) {
			logger.info("encodeDigits: NatureOfNumber Availability Indicator not present");
			throw new InvalidInputException(
					"Nature of Number - Availability Indicator is not present");
		} else {
			natureOfNum |= ((nonAvailInd.getCode() << 2) & 0x04);
		}

		if (nonScreenInd == null) {
			logger.info("encodeDigits: NatureOfNumber Screening Indicator not present");
			throw new InvalidInputException(
					"Nature of Number - Screening Indicator is not present");
		} else {
			natureOfNum |= ((nonScreenInd.getCode() << 4) & 0x30);
		}

		int numPlan;
		if (np == null) {
			logger.info("encodeDigits: Using telephony Numbering as default value of NumberingPlan");
			numPlan = 2;
		} else {
			numPlan = np.getCode();
		}

		// Start Encoding
		myParams[i++] = (byte) (typeOfDig & 0xFF);
		myParams[i++] = (byte) (natureOfNum & 0xFF);
		myParams[i++] = (byte) (((numPlan << 4) & 0xF0) | (encodingSch & 0x0F));
		myParams[i++] = (byte) (addrSignal.length());

		for (int j = 0; j < bcdDigits.length; j++) {
			myParams[i++] = bcdDigits[j];
		}

		if (logger.isDebugEnabled())
			logger.debug("encodeDigits: Encoded Digits: "
					+ Util.formatBytes(myParams));
		logger.info("encodeDigits:Exit");

		return myParams;
	}
	
	/**
	 * This function will encode Non ASN DigitsType to ASN DigitsType object
	 * @param nonASNDigitsType
	 * @return DigitsType
	 * @throws InvalidInputException
	 */
	public static DigitsType encodeDigits(NonASNDigitsType nonASNDigitsType)
			throws InvalidInputException {
		
		logger.info("Before encodeDigitsType : nonASN to ASN");
		DigitsType digitsType = new DigitsType();
		digitsType.setValue(encodeDigits(nonASNDigitsType.getAddrSignal(),nonASNDigitsType.getTypeOfDigits(),
				nonASNDigitsType.getNatOfNumInd(),nonASNDigitsType.getNatOfNumAvlInd(),nonASNDigitsType.getNatOfNumPresInd(),
				nonASNDigitsType.getNatOfNumScrnInd(),nonASNDigitsType.getNumberingPlan(),nonASNDigitsType.getEncoding()));
		logger.info("After encodeDigitsType : nonASN to ASN");
		return digitsType;
	}

	public TypeOfDigitsEnum getTypeOfDigits() {
		return typeOfDigits;
	}

	public void setTypeOfDigits(TypeOfDigitsEnum typeOfDigits) {
		this.typeOfDigits = typeOfDigits;
	}

	public NumPlanEnum getNumberingPlan() {
		return numberingPlan;
	}

	public void setNumberingPlan(NumPlanEnum numberingPlan) {
		this.numberingPlan = numberingPlan;
	}

	public EncodingSchemeEnum getEncoding() {
		return encoding;
	}

	public void setEncoding(EncodingSchemeEnum encoding) {
		this.encoding = encoding;
	}

	public NatureOfNumIndEnum getNatOfNumInd() {
		return natOfNumInd;
	}

	public void setNatOfNumInd(NatureOfNumIndEnum natOfNumInd) {
		this.natOfNumInd = natOfNumInd;
	}

	public NatureOfNumAvailIndEnum getNatOfNumAvlInd() {
		return natOfNumAvlInd;
	}

	public void setNatOfNumAvlInd(NatureOfNumAvailIndEnum natOfNumAvlInd) {
		this.natOfNumAvlInd = natOfNumAvlInd;
	}

	public NatureOfNumPresentationIndEnum getNatOfNumPresInd() {
		return natOfNumPresInd;
	}

	public void setNatOfNumPresInd(
			NatureOfNumPresentationIndEnum natOfNumPresInd) {
		this.natOfNumPresInd = natOfNumPresInd;
	}

	public NatureOfNumScreenIndEnum getNatOfNumScrnInd() {
		return natOfNumScrnInd;
	}

	public void setNatOfNumScrnInd(NatureOfNumScreenIndEnum natOfNumScrnInd) {
		this.natOfNumScrnInd = natOfNumScrnInd;
	}

	public String toString() {
		String obj = "TypeOfDigits: " + typeOfDigits + " ,NatureOfNumber-Ind: "
				+ natOfNumInd + " -AvailableInd: " + natOfNumAvlInd
				+ " PresentInd: " + natOfNumPresInd + " -ScreenInd: "
				+ natOfNumScrnInd + " ,NumberingPlan: " + numberingPlan
				+ " EncodingScheme:" + encoding + " Digits:" + addrSignal;

		return obj;
	}
}

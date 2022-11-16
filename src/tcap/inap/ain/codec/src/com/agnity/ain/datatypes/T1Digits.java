/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
/**
 * 
 */
package com.agnity.ain.datatypes;

import org.apache.log4j.Logger;

import com.agnity.ain.datatypes.AddressSignal;
import com.agnity.ain.enumdata.CalgNatOfNumEnum;
import com.agnity.ain.enumdata.CalledNatOfNumEnum;
import com.agnity.ain.enumdata.ChargeNumEnum;
import com.agnity.ain.enumdata.TypeOfDigitEnum;
import com.agnity.ain.enumdata.NatureOfNumEnum;
import com.agnity.ain.enumdata.ClgPrsntRestIndEnum;
import com.agnity.ain.enumdata.EncodingSchemeEnum;
import com.agnity.ain.enumdata.NumPlanEnum;
import com.agnity.ain.enumdata.ScreeningIndEnum;
import com.agnity.ain.exceptions.InvalidInputException;
import com.agnity.ain.util.Constant;
import com.agnity.ain.util.Util;

/**
 * @author nishantsharma
 *
 */
public class T1Digits extends AddressSignal {
	private static Logger logger = Logger.getLogger(T1Digits.class);
	private TypeOfDigitEnum typeOfDigit;
	private NatureOfNumEnum noa;
	private NumPlanEnum numPlanEnum;
	private EncodingSchemeEnum encodingSchemeEnum;
	private int numOfDigits;
	private String addrSignal;
	private int presentationRestricted =0;

	public static T1Digits getInstance() {
		return new T1Digits();
	}

	/**
	 * 
	 * This function will encode T1Digits.It encodes the
	 * 
	 * called and calling parameter like charge number ,Calling party ID
	 * 
	 * and Called Party ID etc.
	 * 
	 * @return encoded data of Digits
	 * 
	 * @throws InvalidInputException
	 * 
	 */
	public byte[] encodeDigits() throws InvalidInputException {
		if (logger.isInfoEnabled()) {
			logger.info("encodeDigits:Enter");
		}
		int i = 0;
		int natureOfnum = 0;
		int encodingScheme = 0;
		int numPlan;
		if (this.noa != null) {
			natureOfnum = this.noa.getCode();
		}

		byte[] bcdDigits = AddressSignal.encodeAdrsSignal(this.addrSignal);
		// calculate the length of AINDIgits parameters
		int seqLength = 4 + bcdDigits.length;
		byte[] data = new byte[seqLength];

		// encoding for type of Digit
		if (this.typeOfDigit != null) {
			data[i++] = (byte) this.typeOfDigit.getCode();
		}

		// encoding for Nature of Number
		if (this.noa != null) {
			data[i++] = (byte)(presentationRestricted << 1 |  (natureOfnum & 0x01));
		}

		// encoding for numbering plan and encoding scheme
		if (this.numPlanEnum != null) {
			numPlan = this.numPlanEnum.getCode();
			encodingScheme = this.encodingSchemeEnum.getCode();
		} else {
			numPlan = 0;
			encodingScheme = 0;
		}

		data[i++] = (byte) (0 << 7 | numPlan << 4 | encodingScheme);
		// encoding for Number of digits
		data[i++] = (byte) this.numOfDigits;
		// encoding for digits
		for (int j = 0; j < bcdDigits.length; j++) {
			data[i++] = bcdDigits[j];
		}
		if (logger.isDebugEnabled()) {
			logger.debug("encodeDigits data: " + Util.formatBytes(data));
		}
		if (logger.isInfoEnabled()) {
			logger.info("encodeDigits:Exit");
		}
		return data;
	}

	/**
	 * 
	 * This function will decode AinDigits..
	 * 
	 * @param data
	 * 
	 * @param lata boolean flag for checking called parameter lata.Becuase it takes
	 *             maximum 3 digits
	 * 
	 * @return object of AinDigits DataType
	 * 
	 * @throws InvalidInputException
	 * 
	 */
	public T1Digits decodeDigits(byte[] data, String ainClgCldType) throws InvalidInputException {
		if (logger.isInfoEnabled()) {
			logger.info("decodeDigits:Enter");
		}
		if (data == null) {
			logger.error("InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		if (data.length >= 4) {
			this.typeOfDigit = TypeOfDigitEnum.fromInt(data[0] & 0xFF);
			this.noa = NatureOfNumEnum.fromInt(data[1] & 0xFF);
			int numberingPlan = (data[2] >> 4) & 0xF;
			this.numPlanEnum = NumPlanEnum.fromInt(numberingPlan);
			int encodingScheme = (data[2]) & 0xF;
			this.encodingSchemeEnum = EncodingSchemeEnum.fromInt(encodingScheme);

			int numOfDigits = data[3] & 0xFF;
			this.numOfDigits = numOfDigits;
		}
		String addrSignal = AddressSignal.decodeAdrsSignal(data, 4, 0);
		this.addrSignal = addrSignal.substring(0, numOfDigits);
		return this;
	}

	@Override
	public String toString() {
		return "Digits [typeOfDigit=" + typeOfDigit + ", noa=" + noa + ", numPlanEnum=" + numPlanEnum
				+ ", encodingSchemeEnum=" + encodingSchemeEnum + ", numOfDigits=" + numOfDigits + ", addrSignal="
				+ addrSignal + "]";
	}

	public NumPlanEnum getNumPlanEnum() {
		return numPlanEnum;
	}

	public void setNumPlanEnum(NumPlanEnum numPlanEnum) {
		this.numPlanEnum = numPlanEnum;
	}

	public String getAddrSignal() {
		return addrSignal;
	}

	public void setAddrSignal(String addrSignal) {
		this.addrSignal = addrSignal;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger logger) {
		T1Digits.logger = logger;
	}

	public TypeOfDigitEnum getTypeOfDigit() {
		return typeOfDigit;
	}

	public void setTypeOfDigit(TypeOfDigitEnum typeOfDigit) {
		this.typeOfDigit = typeOfDigit;
	}

	public NatureOfNumEnum getNoa() {
		return noa;
	}

	public void setNoa(NatureOfNumEnum noa) {
		this.noa = noa;
	}

	public EncodingSchemeEnum getEncodingSchemeEnum() {
		return encodingSchemeEnum;
	}

	public void setEncodingSchemeEnum(EncodingSchemeEnum encodingSchemeEnum) {
		this.encodingSchemeEnum = encodingSchemeEnum;
	}

	public int getNumOfDigits() {
		return numOfDigits;
	}

	public void setNumOfDigits(int numOfDigits) {
		this.numOfDigits = numOfDigits;
	}

	public int getPresentationRestricted() {
		return presentationRestricted;
	}

	public void setPresentationRestricted(int presentationRestricted) {
		this.presentationRestricted = presentationRestricted;
	}
}

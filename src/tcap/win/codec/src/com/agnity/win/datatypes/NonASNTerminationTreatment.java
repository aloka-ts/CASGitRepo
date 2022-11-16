package com.agnity.win.datatypes;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.ActionCode;
import com.agnity.win.asngenerated.TerminationTreatment;
import com.agnity.win.enumdata.TerminationTreatmentEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for NonASNTerminationTreatment
 * as per definition given in TIA-EIA-41-D, section 6.5.2.158.
 *  @author Supriya Jain
 */
public class NonASNTerminationTreatment {
	private static Logger logger = Logger.getLogger(NonASNTerminationTreatment.class);
	/**
	 * @see TerminationTreatmentEnum
	 */
	LinkedList<TerminationTreatmentEnum> termTreatment;

	public LinkedList<TerminationTreatmentEnum> getActionCode() {
		return termTreatment;
	}

	public void setActionCode(LinkedList<TerminationTreatmentEnum> termTreatment) {
		this.termTreatment = termTreatment;
	}

	/**
	 * This function will encode NonASNTerminationTreatment as per specification
	 * TIA-EIA-41-D section 6.5.2.158
	 * @param list of TerminationTreatmentEnums
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeTerminationTreatment(LinkedList<TerminationTreatmentEnum> termTreatment)
			throws InvalidInputException {
		logger.info("encodeTerminationTreatment");
		if (termTreatment == null || termTreatment.isEmpty()) {
			logger.error("encodeTerminationTreatment: InvalidInputException(terminationTreatment is not present)");
			throw new InvalidInputException("terminationTreatment is not present");
		}

		int i = 0;
		byte[] myParams = new byte[termTreatment.size()];
		for (TerminationTreatmentEnum tt : termTreatment) {
			myParams[i++] = (byte) (tt.getCode());
		}
		// every decoded byte represents an Termination Treatment
		if (logger.isDebugEnabled())
			logger.debug("encodeTerminationTreatment: Encoded Termination Treatment: "
					+ Util.formatBytes(myParams));
		logger.info("encodeTerminationTreatment");
		return myParams;
	}
	
	/**
	 * This function will encode NonASN TerminationTreatment to ASN TerminationTreatment object
	 * @param NonASNTerminationTreatment
	 * @return TerminationTreatment
	 * @throws InvalidInputException
	 */
	public static TerminationTreatment encodeTerminationTreatment(NonASNTerminationTreatment NonASNTerminationTreatment)
			throws InvalidInputException {
		
		logger.info("Before encodeTerminationTreatment : nonASN to ASN");
		TerminationTreatment termTreatment = new TerminationTreatment();
		termTreatment.setValue(encodeTerminationTreatment(NonASNTerminationTreatment.getActionCode()));
		logger.info("After encodeTerminationTreatment : nonASN to ASN");
		return termTreatment;
	}

	/**
	 * This function will decode NonASNTerminationTreatment as per specification
	 * TIA-EIA-41-D section 6.5.2.158
	 * @param data bytes to be decoded
	 * @return object of NonASNTerminationTreatment
	 * @throws InvalidInputException
	 */
	public static NonASNTerminationTreatment decodeTerminationTreatment(byte[] data)
			throws InvalidInputException {
		if (logger.isDebugEnabled())
			logger.debug("decodeTerminationTreatment: Input--> data:"
					+ Util.formatBytes(data));

		if (data == null || data.length == 0) {
			logger.error("decodeTerminationTreatment: InvalidInputException(Input data (bytes) not present)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		NonASNTerminationTreatment termTreat = new NonASNTerminationTreatment();
		termTreat.termTreatment = new LinkedList<TerminationTreatmentEnum>();
		// every byte represents an TerminationTreatment ,decoding gives list of TerminationTreatments
		for (int i = 0; i < data.length; i++) {
			termTreat.termTreatment.add(TerminationTreatmentEnum.fromInt(data[i] & 0xFF));
		}

		if (logger.isDebugEnabled())
			logger.debug("decodeTerminationTreatment: Output<--" + termTreat.toString());
		logger.info("decodeTerminationTreatment");
		return termTreat;
	}

	public String toString() {

		String obj = "termTreatment :" + termTreatment;
		return obj;
	}

}

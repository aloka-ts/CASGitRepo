package com.agnity.win.datatypes;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.TriggerCapability;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for TriggerCapability
 *  @author Supriya Jain
 */
public class NonASNTriggerCapability {
	private static Logger logger = Logger
			.getLogger(NonASNTriggerCapability.class);

	// Introducing Star/Pound represents 1st bit of 1st octet.
	byte init;
	// K-th digit represents 2nd bit of 1st octet.
	byte kDigit;
	// All-Calls represents 3rd bit of 1st octet.
	byte allCalls;
	// Revertive Call represents 4th bit of 1st octet.
	byte revertiveCall;
	// call types represents 1st bit of 2nd octet.
	byte call_type;
	// unrecognized number represents 2nd bit of 2nd octet.
	byte unrecNo;
	// prior Agreement represents 3rd bit of 2nd octet.
	byte priorAgmt;
	// advanced termination represents 4th bit of 2nd octet.
	byte advTerm;
	// terminating Resource available represents 1st bit of 3rd octet.
	byte termResAvail;
	// T_busy represents 2nd bit of 3rd octet.
	byte tBusy;
	// t_no_answer represents 3rd bit of 3rd octet.
	byte tNoAns;

	/**
	 * This function will encode NonASNTriggerCapability
	 * @param bit values
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeTriggerCapability(byte init, byte kDigit,
			byte allCalls, byte revertiveCall, byte call_type, byte unrecNo,
			byte priorAgmt, byte advTerm, byte termResAvail, byte tBusy,
			byte tNoAns) throws InvalidInputException {
		if(logger.isInfoEnabled())
	      logger.info("encodeTriggerCapability");
		byte[] param = new byte[3];
		
		// octet 1
		param[0] = (byte) ((init & 0x01) | ((kDigit & 0x01) << 1)
				| ((allCalls & 0x01) << 2) | ((revertiveCall & 0x01) << 3));
		// octet 2
		param[1] = (byte) ((call_type & 0x01) | ((unrecNo & 0x01) << 1)
				| ((priorAgmt & 0x01) << 2) | ((advTerm & 0x01) << 3));
		// octet 3
		param[2] = (byte) ((termResAvail & 0x01) | ((tBusy & 0x01) << 1) | ((tNoAns & 0x01) << 2));

		if (logger.isDebugEnabled())
			logger.debug("encodeTriggerCapability: Encoded : "
					+ Util.formatBytes(param));
		if(logger.isInfoEnabled())
		logger.info("encodeTriggerCapability");
		return param;
	}
	
	/**
	 * This function will encode NonASN TriggerCapability to ASN TriggerCapability object
	 * @param nonASNTriggerCapability
	 * @return TriggerCapability
	 * @throws InvalidInputException
	 */
	public static TriggerCapability encodeTriggerCapability(NonASNTriggerCapability nonASNTriggerCapability)
			throws InvalidInputException {
		if(logger.isInfoEnabled())
		logger.info("Before encodeTriggerCapability : nonASN to ASN");
		TriggerCapability TriggerCapability = new TriggerCapability();
		TriggerCapability.setValue(encodeTriggerCapability(nonASNTriggerCapability.getInit_can_be_armed(),
				nonASNTriggerCapability.getKDigit_can_be_armed(),nonASNTriggerCapability.getAll_can_be_armed(),	
				nonASNTriggerCapability.getRvtc_can_be_armed(),nonASNTriggerCapability.getCt_can_be_armed(),
				nonASNTriggerCapability.getUnrec_can_be_armed(),nonASNTriggerCapability.getPa_can_be_armed(),nonASNTriggerCapability.getAt_can_be_armed(),
				nonASNTriggerCapability.getTra_can_be_armed(),nonASNTriggerCapability.getTbusy_can_be_armed(), nonASNTriggerCapability.getTna_can_be_armed()));
		if(logger.isInfoEnabled())
		logger.info("After encodeTriggerCapability : nonASN to ASN");
		return TriggerCapability;
	}
	
	
	
	/**
	 * This function will decode TriggerCapability
	 * @param data
	 * @return object of NonASNTriggerCapabilityDataType
	 * @throws InvalidInputException
	 */
	public static NonASNTriggerCapability decodeTriggerCapability(byte[] data)
			throws InvalidInputException {
		if (logger.isDebugEnabled())
			logger.debug("decodeTriggerCapability: Input--> data:"
					+ Util.formatBytes(data));
		if (data == null || data.length==0) {
			logger.error("decodeTriggerCapability: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		NonASNTriggerCapability trigCap = new NonASNTriggerCapability();
		trigCap.init = (byte) (data[0] & 0x1);
		trigCap.kDigit = (byte) (data[0] >> 1 & 0x1);
		trigCap.allCalls = (byte) (data[0] >> 2 & 0x1);
		trigCap.revertiveCall = (byte) (data[0] >> 3 & 0x1);
		trigCap.call_type = (byte) (data[1] & 0x1);
		trigCap.unrecNo = (byte) (data[1] >> 1 & 0x1);
		trigCap.priorAgmt = (byte) (data[1] >> 2 & 0x1);
		trigCap.advTerm = (byte) (data[1] >> 3 & 0x1);
		trigCap.termResAvail = (byte) (data[2] & 0x1);
		trigCap.tBusy = (byte) (data[2] >> 1 & 0x1);
		trigCap.tNoAns = (byte) (data[2] >> 2 & 0x1);

		if (logger.isDebugEnabled())
			logger.debug("decodeTriggerCapability: Output<--"
					+ trigCap.toString());
		if(logger.isInfoEnabled())
		logger.info("decodeTriggerCapability");
		return trigCap;
	}

	public byte getInit_can_be_armed() {
		return init;
	}

	public void setInit_can_be_armed(byte init) {
		this.init = init;
	}

	public byte getAll_can_be_armed() {
		return allCalls;
	}

	public void setAll_can_be_armed(byte allCalls) {
		this.allCalls = allCalls;
	}

	public byte getKDigit_can_be_armed() {
		return kDigit;
	}

	public void setKDigit_can_be_armed(byte kDigit) {
		this.kDigit = kDigit;
	}

	public byte getRvtc_can_be_armed() {
		return revertiveCall;
	}

	public void setRvtc_can_be_armed(byte revertiveCall) {
		this.revertiveCall = revertiveCall;
	}

	public byte getCt_can_be_armed() {
		return call_type;
	}

	public void setCt_can_be_armed(byte call_type) {
		this.call_type = call_type;
	}

	public byte getUnrec_can_be_armed() {
		return unrecNo;
	}

	public void setUnrec_can_be_armed(byte unrecNo) {
		this.unrecNo = unrecNo;
	}

	public byte getAt_can_be_armed() {
		return advTerm;
	}

	public void setAt_can_be_armed(byte advTerm) {
		this.advTerm = advTerm;
	}

	public byte getTra_can_be_armed() {
		return termResAvail;
	}

	public void setTra_can_be_armed(byte termResAvail) {
		this.termResAvail = termResAvail;
	}

	public byte getTbusy_can_be_armed() {
		return tBusy;
	}

	public void setTbusy_can_be_armed(byte tBusy) {
		this.tBusy = tBusy;
	}

	public byte getTna_can_be_armed() {
		return tNoAns;
	}

	public void setTna_can_be_armed(byte tNoAns) {
		this.tNoAns = tNoAns;
	}

	public byte getPa_can_be_armed() {
		return priorAgmt;
	}

	public void setPa_can_be_armed(byte priorAgmt) {
		this.priorAgmt = priorAgmt;
	}

	public String toString() {
		String obj = "init:" + init + " ,kDigit:" + kDigit + " ,allCalls:"
				+ allCalls + " ,revertiveCall:" + revertiveCall
				+ " ,call_type:" + call_type + " ,unrecNo:" + unrecNo
				+ " ,priorAgmt:" + priorAgmt + " ,advTerm:" + advTerm
				+ " ,termResAvail:" + termResAvail + " ,tBusy:" + tBusy
				+ " ,tNoAns:" + tNoAns;
		return obj;
	}

}

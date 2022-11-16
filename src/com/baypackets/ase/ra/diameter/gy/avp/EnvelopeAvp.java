package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpEnvelope;

public class EnvelopeAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(EnvelopeAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpEnvelope stackObj;

	public EnvelopeAvp(AvpEnvelope stkObj){
		super(stkObj);
		this.stackObj=stkObj;
	}

	/**
	 * This method returns the standard code for this AVP.
	 */
	public int getCode() {
		return stackObj.getCode();
	}

	/**
	 * The standard rule for the M (mandatory) AVP header flag
	 */
	public FlagRuleEnum getMRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getMRule());
	}

	/**
	 * This method returns the name of this AVP.
	 */
	public String getName() {
		return stackObj.getName();
	}

	/**
	 * The standard rule for the P (end-to-end encryption) AVP header flag
	 */
	public FlagRuleEnum getPRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getPRule());
	}

	/**
	 * This method returns the vendor ID associated with this AVP.
	 */
	public long getVendorId() {
		return stackObj.getVendorId();
	}

	/**
	 * The standard rule for the V (vendor) AVP header flag
	 */
	public FlagRuleEnum getVRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getVRule());
	}

	/**
	 *  Adding CCInputOctets AVP of type Unsigned64 to the message.
	 */
	public CCInputOctetsAvp addCCInputOctets(long value, boolean mFlag) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCCInputOctets()");
			}
			return new CCInputOctetsAvp(stackObj.addCCInputOctets(value,mFlag));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCCInputOctets",e);
		}
	}

	/**
	 *  Adding CCOutputOctets AVP of type Unsigned64 to the message.
	 */
	public CCOutputOctetsAvp addCCOutputOctets(long value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCCOutputOctets()");
			}
			return new CCOutputOctetsAvp(stackObj.addCCOutputOctets(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCCOutputOctets",e);
		}
	}

	/**
	 *  Adding CCServiceSpecificUnits AVP of type Unsigned64 to the message.
	 */
	public CCServiceSpecificUnitsAvp addCCServiceSpecificUnits(long value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCCServiceSpecificUnits()");
			}
			return new CCServiceSpecificUnitsAvp(stackObj.addCCServiceSpecificUnits(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCCServiceSpecificUnits",e);
		}
	}

	/**
	 *  Adding CCTotalOctets AVP of type Unsigned64 to the message.
	 */
	public CCTotalOctetsAvp addCCTotalOctets(long value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCCTotalOctets()");
			}
			return new CCTotalOctetsAvp(stackObj.addCCTotalOctets(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCCTotalOctets",e);
		}
	}

	/**
	 *  Adding EnvelopeEndTime AVP of type Time to the message.
	 */
	public EnvelopeEndTimeAvp addEnvelopeEndTime(java.util.Date value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addEnvelopeEndTime()");
			}
			return new EnvelopeEndTimeAvp(stackObj.addEnvelopeEndTime(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addEnvelopeEndTime",e);
		}
	}

	/**
	 *  Adding EnvelopeStartTime AVP of type Time to the message.
	 */
	public EnvelopeStartTimeAvp addEnvelopeStartTime(java.util.Date value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addEnvelopeStartTime()");
			}
			return new EnvelopeStartTimeAvp(stackObj.addEnvelopeStartTime(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addEnvelopeStartTime",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned64 value from CCInputOctets AVPs.
	 */
	public long getCCInputOctets() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCCInputOctets()");
			}
			return stackObj.getCCInputOctets();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCCInputOctets",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned64 value from CCOutputOctets AVPs.
	 */
	public long getCCOutputOctets() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCCOutputOctets()");
			}
			return stackObj.getCCOutputOctets();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCCOutputOctets",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned64 value from CCServiceSpecificUnits AVPs.
	 */
	public long getCCServiceSpecificUnits() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCCServiceSpecificUnits()");
			}
			return stackObj.getCCServiceSpecificUnits();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCCServiceSpecificUnits",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned64 value from CCTotalOctets AVPs.
	 */
	public long getCCTotalOctets() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCCTotalOctets()");
			}
			return stackObj.getCCTotalOctets();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCCTotalOctets",e);
		}
	}

	/**
	 *  Retrieving a single Time value from EnvelopeEndTime AVPs.
	 */
	public java.util.Date getEnvelopeEndTime() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnvelopeEndTime()");
			}
			return stackObj.getEnvelopeEndTime();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnvelopeEndTime",e);
		}
	}

	/**
	 *  Retrieving a single Time value from EnvelopeStartTime AVPs.
	 */
	public java.util.Date getEnvelopeStartTime() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnvelopeStartTime()");
			}
			return stackObj.getEnvelopeStartTime();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnvelopeStartTime",e);
		}
	}


}
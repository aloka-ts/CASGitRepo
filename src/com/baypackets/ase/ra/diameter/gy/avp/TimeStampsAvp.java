package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpTimeStamps;

public class TimeStampsAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(TimeStampsAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpTimeStamps stackObj;

	public TimeStampsAvp(AvpTimeStamps stkObj){
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
	 *  Adding SIPRequestTimestamp AVP of type Time to the message.
	 */
	public SIPRequestTimestampAvp addSIPRequestTimestamp(java.util.Date value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSIPRequestTimestamp()");
			}
			return new SIPRequestTimestampAvp(stackObj.addSIPRequestTimestamp(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addSIPRequestTimestamp",e);
		}
	}

	/**
	 *  Adding SIPRequestTimestampFraction AVP of type Unsigned32 to the message.
	 */
//	public SIPRequestTimestampFractionAvp addSIPRequestTimestampFraction(long value) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addSIPRequestTimestampFraction()");
//			}
//			return new SIPRequestTimestampFractionAvp(stackObj.addSIPRequestTimestampFraction(value));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addSIPRequestTimestampFraction",e);
//		}
//	}

	/**
	 *  Adding SIPResponseTimestamp AVP of type Time to the message.
	 */
	public SIPResponseTimestampAvp addSIPResponseTimestamp(java.util.Date value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSIPResponseTimestamp()");
			}
			return new SIPResponseTimestampAvp(stackObj.addSIPResponseTimestamp(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addSIPResponseTimestamp",e);
		}
	}

	/**
	 *  Adding SIPResponseTimestampFraction AVP of type Unsigned32 to the message.
	 */
//	public SIPResponseTimestampFractionAvp addSIPResponseTimestampFraction(long value) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addSIPResponseTimestampFraction()");
//			}
//			return new SIPResponseTimestampFractionAvp(stackObj.addSIPResponseTimestampFraction(value));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addSIPResponseTimestampFraction",e);
//		}
//	}

	/**
	 *  Retrieving a single Time value from SIPRequestTimestamp AVPs.
	 */
	public java.util.Date getSIPRequestTimestamp() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getSIPRequestTimestamp()");
			}
			return stackObj.getSIPRequestTimestamp();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getSIPRequestTimestamp",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from SIPRequestTimestampFraction AVPs.
	 */
//	public long getSIPRequestTimestampFraction( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getSIPRequestTimestampFraction()");
//			}
//			return stackObj.getSIPRequestTimestampFraction();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getSIPRequestTimestampFraction",e);
//		}
//	}

	/**
	 *  Retrieving a single Time value from SIPResponseTimestamp AVPs.
	 */
	public java.util.Date getSIPResponseTimestamp( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getSIPResponseTimestamp()");
			}
			return stackObj.getSIPResponseTimestamp();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getSIPResponseTimestamp",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from SIPResponseTimestampFraction AVPs.
	 */
//	public long getSIPResponseTimestampFraction( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getSIPResponseTimestampFraction()");
//			}
//			return stackObj.getSIPResponseTimestampFraction();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getSIPResponseTimestampFraction",e);
//		}
//	}


}
package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpSDPTimeStamps;

public class SDPTimeStampsAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(SDPTimeStampsAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpSDPTimeStamps stackObj;

	public SDPTimeStampsAvp(AvpSDPTimeStamps stkObj){
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
	 *  Adding SDPAnswerTimestamp AVP of type Time to the message.
	 */
	public SDPAnswerTimestampAvp addSDPAnswerTimestamp(java.util.Date value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSDPAnswerTimestamp()");
			}
			return new SDPAnswerTimestampAvp(stackObj.addSDPAnswerTimestamp(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addSDPAnswerTimestamp",e);
		}
	}

	/**
	 *  Adding SDPOfferTimestamp AVP of type Time to the message.
	 */
	public SDPOfferTimestampAvp addSDPOfferTimestamp(java.util.Date value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSDPOfferTimestamp()");
			}
			return new SDPOfferTimestampAvp(stackObj.addSDPOfferTimestamp(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addSDPOfferTimestamp",e);
		}
	}

	/**
	 *  Retrieving a single Time value from SDPAnswerTimestamp AVPs.
	 */
	public java.util.Date getSDPAnswerTimestamp( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getSDPAnswerTimestamp()");
			}
			return stackObj.getSDPAnswerTimestamp();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getSDPAnswerTimestamp",e);
		}
	}

	/**
	 *  Retrieving a single Time value from SDPOfferTimestamp AVPs.
	 */
	public java.util.Date getSDPOfferTimestamp( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getSDPOfferTimestamp()");
			}
			return stackObj.getSDPOfferTimestamp();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getSDPOfferTimestamp",e);
		}
	}

}
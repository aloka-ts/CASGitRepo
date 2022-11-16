package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.TimeQuotaTypeEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpTimeQuotaMechanism;

public class TimeQuotaMechanismAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(TimeQuotaMechanismAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpTimeQuotaMechanism stackObj;

	public TimeQuotaMechanismAvp(AvpTimeQuotaMechanism stkObj){
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
	 *  Adding BaseTimeInterval AVP of type Unsigned32 to the message.
	 */
	public BaseTimeIntervalAvp addBaseTimeInterval(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addBaseTimeInterval()");
			}
			return new BaseTimeIntervalAvp(stackObj.addBaseTimeInterval(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addBaseTimeInterval",e);
		}
	}

	/**
	 *  Adding TimeQuotaType AVP of type Enumerated to the message.
	 */
	public TimeQuotaTypeAvp addTimeQuotaType(TimeQuotaTypeEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addTimeQuotaType()");
			}
			return new TimeQuotaTypeAvp(stackObj.addTimeQuotaType(TimeQuotaTypeEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addTimeQuotaType",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from BaseTimeInterval AVPs.
	 */
	public long getBaseTimeInterval( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getBaseTimeInterval()");
			}
			return stackObj.getBaseTimeInterval();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getBaseTimeInterval",e);
		}
	}

	/**
	 *  This method returns the enum value corrosponding to TimeQuotaTypeAvp.
	 */
	public TimeQuotaTypeEnum getEnumTimeQuotaType( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumTimeQuotaType()");
			}
			return TimeQuotaTypeEnum.getContainerObj(stackObj.getEnumTimeQuotaType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumTimeQuotaType",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from TimeQuotaType AVPs.
	 */
	public int getTimeQuotaType( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getTimeQuotaType()");
			}
			return stackObj.getTimeQuotaType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getTimeQuotaType",e);
		}
	}

}
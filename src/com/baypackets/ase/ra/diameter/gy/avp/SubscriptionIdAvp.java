package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.SubscriptionIdTypeEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpSubscriptionId;

public class SubscriptionIdAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(SubscriptionIdAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpSubscriptionId stackObj;

	public SubscriptionIdAvp(AvpSubscriptionId stkObj){
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
	 *  Adding SubscriptionIdData AVP of type UTF8String to the message.
	 */
	public SubscriptionIdDataAvp addSubscriptionIdData(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSubscriptionIdData()");
			}
			return new SubscriptionIdDataAvp(stackObj.addSubscriptionIdData(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addSubscriptionIdData",e);
		}
	}

	//TODO
	/**
	 *  Adding SubscriptionIdType AVP of type Enumerated to the message.
	 */
	public SubscriptionIdTypeAvp addSubscriptionIdType(SubscriptionIdTypeEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSubscriptionIdType()");
			}
			return new SubscriptionIdTypeAvp(stackObj.addSubscriptionIdType(SubscriptionIdTypeEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addSubscriptionIdType",e);
		}
	}

	/**
	 *  This method returns the enum value corrosponding to SubscriptionIdTypeAvp.
	 */
	public SubscriptionIdTypeEnum getEnumSubscriptionIdType( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumSubscriptionIdType()");
			}
			return SubscriptionIdTypeEnum.getContainerObj(stackObj.getEnumSubscriptionIdType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumSubscriptionIdType",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from SubscriptionIdData AVPs.
	 */
	public java.lang.String getSubscriptionIdData( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getSubscriptionIdData()");
			}
			return stackObj.getSubscriptionIdData();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getSubscriptionIdData",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from SubscriptionIdType AVPs.
	 */
	public int getSubscriptionIdType( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getSubscriptionIdType()");
			}
			return stackObj.getSubscriptionIdType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getSubscriptionIdType",e);
		}
	}

}
package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.LocationEstimateTypeEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpLocationType;
import com.traffix.openblox.diameter.gy.generated.enums.EnumLocationEstimateType;

public class LocationTypeAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(LocationTypeAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpLocationType stackObj;

	public LocationTypeAvp(AvpLocationType stkObj){
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
	 *  Adding DeferredLocationEventType AVP of type UTF8String to the message.
	 */
	public DeferredLocationEventTypeAvp addDeferredLocationEventType(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addDeferredLocationEventType()");
			}
			return new DeferredLocationEventTypeAvp(stackObj.addDeferredLocationEventType(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addDeferredLocationEventType",e);
		}
	}

	/**
	 *  Adding LocationEstimateType AVP of type Enumerated to the message.
	 */
	public LocationEstimateTypeAvp addLocationEstimateType(EnumLocationEstimateType value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addLocationEstimateType()");
			}
			return new LocationEstimateTypeAvp(stackObj.addLocationEstimateType(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addLocationEstimateType",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from DeferredLocationEventType AVPs.
	 */
	public java.lang.String getDeferredLocationEventType() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getDeferredLocationEventType()");
			}
			return stackObj.getDeferredLocationEventType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getDeferredLocationEventType",e);
		}
	}

	/**
	 *  Retrieves Enum of type LocationEstimate.
	 */
	public LocationEstimateTypeEnum getEnumLocationEstimateType() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumLocationEstimateType()");
			}
			return LocationEstimateTypeEnum.getContainerObj(stackObj.getEnumLocationEstimateType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumLocationEstimateType",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from LocationEstimateType AVPs.
	 */
	public int getLocationEstimateType() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getLocationEstimateType()");
			}
			return stackObj.getLocationEstimateType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getLocationEstimateType",e);
		}
	}

}
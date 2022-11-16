package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.AoCServiceObligatoryTypeEnum;
import com.baypackets.ase.ra.diameter.gy.enums.AoCServiceTypeEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpAoCService;
import com.traffix.openblox.diameter.gy.generated.enums.EnumAoCServiceObligatoryType;
import com.traffix.openblox.diameter.gy.generated.enums.EnumAoCServiceType;

public class AoCServiceAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(AoCServiceAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpAoCService stackObj;

	public AoCServiceAvp(AvpAoCService stkObj){
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
	 *  Adding AoCServiceObligatoryType AVP of type Enumerated to the message.
	 */
	public AoCServiceObligatoryTypeAvp addAoCServiceObligatoryType(EnumAoCServiceObligatoryType value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAoCServiceObligatoryType()");
			}
			return new AoCServiceObligatoryTypeAvp(stackObj.addAoCServiceObligatoryType(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addAoCServiceObligatoryType",e);
		}
	}

	/**
	 *  Adding AoCServiceType AVP of type Enumerated to the message.
	 */
	public AoCServiceTypeAvp addAoCServiceType(EnumAoCServiceType value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAoCServiceType()");
			}
			return new AoCServiceTypeAvp(stackObj.addAoCServiceType(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addAoCServiceType",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from AoCServiceObligatoryType AVPs.
	 */
	public int getAoCServiceObligatoryType() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAoCServiceObligatoryType()");
			}
			return stackObj.getAoCServiceObligatoryType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getAoCServiceObligatoryType",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from AoCServiceType AVPs.
	 */
	public int getAoCServiceType() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAoCServiceType()");
			}
			return stackObj.getAoCServiceType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getAoCServiceType",e);
		}
	}

	/**
	 *  Gets Enum of AoCServiceObligatoryType.
	 */
	public AoCServiceObligatoryTypeEnum getEnumAoCServiceObligatoryType() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumAoCServiceObligatoryType()");
			}
			return	AoCServiceObligatoryTypeEnum.getContainerObj(stackObj.getEnumAoCServiceObligatoryType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumAoCServiceObligatoryType",e);
		}
	}

	/**
	 *  Gets Enum of AoCServiceType.
	 */
	public AoCServiceTypeEnum getEnumAoCServiceType() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumAoCServiceType()");
			}
			return	AoCServiceTypeEnum.getContainerObj(stackObj.getEnumAoCServiceType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumAoCServiceType",e);
		}
	}

}
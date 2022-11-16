package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.CCUnitTypeEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpGSUPoolReference;
import com.traffix.openblox.diameter.gy.generated.enums.EnumCCUnitType;

public class GSUPoolReferenceAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(GSUPoolReferenceAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpGSUPoolReference stackObj;

	public GSUPoolReferenceAvp(AvpGSUPoolReference stkObj){
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
	 *  Adding CCUnitType AVP of type Enumerated to the message.
	 */
	public CCUnitTypeAvp addCCUnitType(EnumCCUnitType value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCCUnitType()");
			}
			return new CCUnitTypeAvp(stackObj.addCCUnitType(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCCUnitType",e);
		}
	}

	/**
	 *  Adding UnitValue AVP of type Grouped to the message.
	 */
	public UnitValueAvp addGroupedUnitValue() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedUnitValue()");
			}
			return new UnitValueAvp(stackObj.addGroupedUnitValue());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedUnitValue",e);
		}
	}

	/**
	 *  Adding GSUPoolIdentifier AVP of type Unsigned32 to the message.
	 */
	public GSUPoolIdentifierAvp addGSUPoolIdentifier(long value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGSUPoolIdentifier()");
			}
			return new GSUPoolIdentifierAvp(stackObj.addGSUPoolIdentifier(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGSUPoolIdentifier",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from CCUnitType AVPs.
	 */
	public int getCCUnitType() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCCUnitType()");
			}
			return stackObj.getCCUnitType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCCUnitType",e);
		}
	}

	/**
	 *  Retrieves Enum of CCUnitType.
	 */
	public CCUnitTypeEnum getEnumCCUnitType() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumCCUnitType()");
			}
			return CCUnitTypeEnum.getContainerObj(stackObj.getEnumCCUnitType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumCCUnitType",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from UnitValue AVPs.
	 */
	public UnitValueAvp getGroupedUnitValue() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedUnitValue()");
			}
			return new UnitValueAvp(stackObj.getGroupedUnitValue());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedUnitValue",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from GSUPoolIdentifier AVPs.
	 */
	public long getGSUPoolIdentifier() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGSUPoolIdentifier()");
			}
			return stackObj.getGSUPoolIdentifier();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGSUPoolIdentifier",e);
		}
	}


}
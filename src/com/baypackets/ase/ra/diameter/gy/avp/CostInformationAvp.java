package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpCostInformation;

public class CostInformationAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(CostInformationAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpCostInformation stackObj;

	public CostInformationAvp(AvpCostInformation stkObj){
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
	 *  Adding CostUnit AVP of type UTF8String to the message.
	 */
	public CostUnitAvp addCostUnit(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCostUnit()");
			}
			return new CostUnitAvp(stackObj.addCostUnit(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCostUnit",e);
		}
	}

	/**
	 *  Adding CurrencyCode AVP of type Unsigned32 to the message.
	 */
	public CurrencyCodeAvp addCurrencyCode(long value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCurrencyCode()");
			}
			return new CurrencyCodeAvp(stackObj.addCurrencyCode(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCurrencyCode",e);
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
	 *  Retrieving a single UTF8String value from CostUnit AVPs.
	 */
	public java.lang.String getCostUnit() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCostUnit()");
			}
			return stackObj.getCostUnit();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCostUnit",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from CurrencyCode AVPs.
	 */
	public long getCurrencyCode() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCurrencyCode()");
			}
			return stackObj.getCurrencyCode();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCurrencyCode",e);
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


}
package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpUnitCost;

public class UnitCostAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(UnitCostAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpUnitCost stackObj;

	public UnitCostAvp(AvpUnitCost stkObj){
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
	 *  Adding Exponent AVP of type Integer32 to the message.
	 */
	public ExponentAvp addExponent(int value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addExponent()");
			}
			return new ExponentAvp(stackObj.addExponent(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addExponent",e);
		}
	}

	/**
	 *  Adding ValueDigits AVP of type Integer64 to the message.
	 */
	public ValueDigitsAvp addValueDigits(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addValueDigits()");
			}
			return new ValueDigitsAvp(stackObj.addValueDigits(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addValueDigits",e);
		}
	}

	/**
	 *  Retrieving a single Integer32 value from Exponent AVPs.
	 */
	public int getExponent( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getExponent()");
			}
			return stackObj.getExponent();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getExponent",e);
		}
	}

	/**
	 *  Retrieving a single Integer64 value from ValueDigits AVPs.
	 */
	public long getValueDigits( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getValueDigits()");
			}
			return stackObj.getValueDigits();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getValueDigits",e);
		}
	}

}
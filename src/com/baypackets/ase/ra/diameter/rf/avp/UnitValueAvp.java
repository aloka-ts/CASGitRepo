package com.baypackets.ase.ra.diameter.rf.avp;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.rf.RfResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.rf.generated.avp.AvpUnitValue;

public class UnitValueAvp extends AvpDiameterGrouped {

	public static final long vendorId = 0L;

	private AvpUnitValue stackObj;

	public UnitValueAvp(AvpUnitValue stkObj){
		super(stkObj);
		this.stackObj=stkObj;
	}

	public int getCode() {
		return stackObj.getCode();
	}

	public FlagRuleEnum getMRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getMRule());
	}

	public String getName() {
		return stackObj.getName();
	}

	public FlagRuleEnum getPRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getPRule());
	}

	public long getVendorId() {
		return stackObj.getVendorId();
	}

	public FlagRuleEnum getVRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getVRule());
	}
	
	/**
	 * Adding Exponent AVP of type Integer32 to the message.
	 * @param value
	 * @return
	 * @throws RfResourceException 
	 */
	public ExponentAvp addExponent(int value) throws RfResourceException {
		try {
			return new ExponentAvp(stackObj.addExponent(value));
		} catch (ValidationException e) {
			throw new RfResourceException(e);
		}
	}

	/**
	 * Adding ValueDigits AVP of type Integer64 to the message.
	 * @param value
	 * @return
	 * @throws RfResourceException 
	 */
	public ValueDigitsAvp addValueDigits(long value) throws RfResourceException {
		try {
			return new ValueDigitsAvp(stackObj.addValueDigits(value));
		} catch (ValidationException e) {
			throw new RfResourceException(e);
		}
	}

	/**
	 * Retrieving a single Integer32 value from Exponent AVPs.
	 * @return
	 * @throws RfResourceException 
	 */
	public int getExponent() throws RfResourceException {
		try {
			return stackObj.getExponent();
		} catch (ValidationException e) {
			throw new RfResourceException(e);
		}
	}

	/**
	 * Retrieving a single Integer64 value from ValueDigits AVPs.
	 * @return
	 * @throws RfResourceException 
	 */
	public long getValueDigits() throws RfResourceException {
		try {
			return stackObj.getExponent();
		} catch (ValidationException e) {
			throw new RfResourceException(e);
		}
	}
}
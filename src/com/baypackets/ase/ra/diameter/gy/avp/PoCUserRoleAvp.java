package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.PoCUserRoleInfoUnitsEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpPoCUserRole;
import com.traffix.openblox.diameter.gy.generated.enums.EnumPoCUserRoleInfoUnits;

public class PoCUserRoleAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(PoCUserRoleAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpPoCUserRole stackObj;

	public PoCUserRoleAvp(AvpPoCUserRole stkObj){
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
	 *  Adding PoCUserRoleIDs AVP of type UTF8String to the message.
	 */
	public PoCUserRoleIDsAvp addPoCUserRoleIDs(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addPoCUserRoleIDs()");
			}
			return new PoCUserRoleIDsAvp(stackObj.addPoCUserRoleIDs(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addPoCUserRoleIDs",e);
		}
	}

	/**
	 *  Adding PoCUserRoleInfoUnits AVP of type Enumerated to the message.
	 */
	public PoCUserRoleInfoUnitsAvp addPoCUserRoleInfoUnits(PoCUserRoleInfoUnitsEnum value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addPoCUserRoleInfoUnits()");
			}
			return new PoCUserRoleInfoUnitsAvp(stackObj.addPoCUserRoleInfoUnits(PoCUserRoleInfoUnitsEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addPoCUserRoleInfoUnits",e);
		}
	}

	/**
	 *  Retrieves Enum of PoCUserRoleInfoUnits type.
	 */
	public PoCUserRoleInfoUnitsEnum getEnumPoCUserRoleInfoUnits() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumPoCUserRoleInfoUnits()");
			}
			return PoCUserRoleInfoUnitsEnum.getContainerObj(stackObj.getEnumPoCUserRoleInfoUnits());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumPoCUserRoleInfoUnits",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from PoCUserRoleIDs AVPs.
	 */
	public java.lang.String getPoCUserRoleIDs() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getPoCUserRoleIDs()");
			}
			return stackObj.getPoCUserRoleIDs();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getPoCUserRoleIDs",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from PoCUserRoleInfoUnits AVPs.
	 */
	public int getPoCUserRoleInfoUnits() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getPoCUserRoleInfoUnits()");
			}
			return stackObj.getPoCUserRoleInfoUnits();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getPoCUserRoleInfoUnits",e);
		}
	}


}
package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.CSGAccessModeEnum;
import com.baypackets.ase.ra.diameter.gy.enums.CSGMembershipIndicationEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpUserCSGInformation;

public class UserCSGInformationAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(UserCSGInformationAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpUserCSGInformation stackObj;

	public UserCSGInformationAvp(AvpUserCSGInformation stkObj){
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
	 *  Adding CSGAccessMode AVP of type Enumerated to the message.
	 */
	public CSGAccessModeAvp addCSGAccessMode(CSGAccessModeEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCSGAccessMode()");
			}
			return new CSGAccessModeAvp(stackObj.addCSGAccessMode(CSGAccessModeEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCSGAccessMode",e);
		}
	}

	/**
	 *  Adding CSGId AVP of type Unsigned32 to the message.
	 */
	public CSGIdAvp addCSGId(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCSGId()");
			}
			return new CSGIdAvp(stackObj.addCSGId(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCSGId",e);
		}
	}

	/**
	 *  Adding CSGMembershipIndication AVP of type Enumerated to the message.
	 */
	public CSGMembershipIndicationAvp addCSGMembershipIndication(CSGMembershipIndicationEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCSGMembershipIndication()");
			}
			return new CSGMembershipIndicationAvp(stackObj.addCSGMembershipIndication(CSGMembershipIndicationEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCSGMembershipIndication",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from CSGAccessMode AVPs.
	 */
	public int getCSGAccessMode( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCSGAccessMode()");
			}
			return stackObj.getCSGAccessMode();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCSGAccessMode",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from CSGMembershipIndication AVPs.
	 */
	public int getCSGMembershipIndication( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCSGMembershipIndication()");
			}
			return stackObj.getCSGMembershipIndication();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCSGMembershipIndication",e);
		}
	}

	/**
	 *  This method returns the enum value of CSGAccessModeAvp.
	 */
	public CSGAccessModeEnum getEnumCSGAccessMode( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumCSGAccessMode()");
			}
			return CSGAccessModeEnum.getContainerObj(stackObj.getEnumCSGAccessMode());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumCSGAccessMode",e);
		}
	}

	/**
	 *  This method returns the enum value of CSGMembershipIndicationAvp.
	 */
	public CSGMembershipIndicationEnum getEnumCSGMembershipIndication( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumCSGMembershipIndication()");
			}
			return CSGMembershipIndicationEnum.getContainerObj(stackObj.getEnumCSGMembershipIndication());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumCSGMembershipIndication",e);
		}
	}

}
package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.UserEquipmentInfoTypeEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpUserEquipmentInfo;

public class UserEquipmentInfoAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(UserEquipmentInfoAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpUserEquipmentInfo stackObj;

	public UserEquipmentInfoAvp(AvpUserEquipmentInfo stkObj){
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
	 *  Adding UserEquipmentInfoType AVP of type Enumerated to the message.
	 */
	public UserEquipmentInfoTypeAvp addUserEquipmentInfoType(UserEquipmentInfoTypeEnum value, boolean mFlag) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addUserEquipmentInfoType()");
			}
			return new UserEquipmentInfoTypeAvp(stackObj.addUserEquipmentInfoType(UserEquipmentInfoTypeEnum.getStackObj(value),mFlag));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addUserEquipmentInfoType",e);
		}
	}

	/**
	 *  Adding UserEquipmentInfoValue AVP of type OctetString to the message.
	 */
	public UserEquipmentInfoValueAvp addUserEquipmentInfoValue(byte[] value, boolean mFlag) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addUserEquipmentInfoValue()");
			}
			return new UserEquipmentInfoValueAvp(stackObj.addUserEquipmentInfoValue(value,mFlag));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addUserEquipmentInfoValue",e);
		}
	}

	/**
	 *  Adding UserEquipmentInfoValue AVP of type OctetString to the message.
	 */
	public UserEquipmentInfoValueAvp addUserEquipmentInfoValue(java.lang.String value, boolean mFlag) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addUserEquipmentInfoValue()");
			}
			return new UserEquipmentInfoValueAvp(stackObj.addUserEquipmentInfoValue(value, mFlag));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addUserEquipmentInfoValue",e);
		}
	}

	/**
	 *  Thie method returns the enum value corrospinding to UserEquipmentInfoTypeAvp.
	 */
	public UserEquipmentInfoTypeEnum getEnumUserEquipmentInfoType( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumUserEquipmentInfoType()");
			}
			return UserEquipmentInfoTypeEnum.getContainerObj(stackObj.getEnumUserEquipmentInfoType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumUserEquipmentInfoType",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from UserEquipmentInfoValue AVPs.
	 */
	public byte[] getRawUserEquipmentInfoValue( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawUserEquipmentInfoValue()");
			}
			return stackObj.getRawUserEquipmentInfoValue();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawUserEquipmentInfoValue",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from UserEquipmentInfoType AVPs.
	 */
	public int getUserEquipmentInfoType( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getUserEquipmentInfoType()");
			}
			return stackObj.getUserEquipmentInfoType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getUserEquipmentInfoType",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from UserEquipmentInfoValue AVPs.
	 */
	public java.lang.String getUserEquipmentInfoValue( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getUserEquipmentInfoValue()");
			}
			return stackObj.getUserEquipmentInfoValue();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getUserEquipmentInfoValue",e);
		}
	}


}
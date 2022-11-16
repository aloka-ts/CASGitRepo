package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.InterfaceTypeEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpDestinationInterface;
import com.traffix.openblox.diameter.gy.generated.enums.EnumInterfaceType;

public class DestinationInterfaceAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(DestinationInterfaceAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpDestinationInterface stackObj;

	public DestinationInterfaceAvp(AvpDestinationInterface stkObj){
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
	 *  Adding InterfaceId AVP of type UTF8String to the message.
	 */
	public InterfaceIdAvp addInterfaceId(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addInterfaceId()");
			}
			return new InterfaceIdAvp(stackObj.addInterfaceId(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addInterfaceId",e);
		}
	}

	/**
	 *  Adding InterfacePort AVP of type UTF8String to the message.
	 */
	public InterfacePortAvp addInterfacePort(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addInterfacePort()");
			}
			return new InterfacePortAvp(stackObj.addInterfacePort(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addInterfacePort",e);
		}
	}

	/**
	 *  Adding InterfaceText AVP of type UTF8String to the message.
	 */
	public InterfaceTextAvp addInterfaceText(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addInterfaceText()");
			}
			return new InterfaceTextAvp(stackObj.addInterfaceText(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addInterfaceText",e);
		}
	}

	/**
	 *  Adding InterfaceType AVP of type Enumerated to the message.
	 */
	public InterfaceTypeAvp addInterfaceType(EnumInterfaceType value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addInterfaceType()");
			}
			return new InterfaceTypeAvp(stackObj.addInterfaceType(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addInterfaceType",e);
		}
	}

	/**
	 *  Retrieves Enum of type Interface
	 */
	public InterfaceTypeEnum getEnumInterfaceType() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumInterfaceType()");
			}
			return InterfaceTypeEnum.getContainerObj(stackObj.getEnumInterfaceType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumInterfaceType",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from InterfaceId AVPs.
	 */
	public java.lang.String getInterfaceId() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getInterfaceId()");
			}
			return stackObj.getInterfaceId();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getInterfaceId",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from InterfacePort AVPs.
	 */
	public java.lang.String getInterfacePort() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getInterfacePort()");
			}
			return stackObj.getInterfacePort();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getInterfacePort",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from InterfaceText AVPs.
	 */
	public java.lang.String getInterfaceText() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getInterfaceText()");
			}
			return stackObj.getInterfaceText();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getInterfaceText",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from InterfaceType AVPs.
	 */
	public int getInterfaceType() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getInterfaceType()");
			}
			return stackObj.getInterfaceType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getInterfaceType",e);
		}
	}

}
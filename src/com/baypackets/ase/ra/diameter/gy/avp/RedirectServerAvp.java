package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.RedirectAddressTypeEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpRedirectServer;

public class RedirectServerAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(RedirectServerAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpRedirectServer stackObj;

	public RedirectServerAvp(AvpRedirectServer stkObj){
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
	 *  Adding RedirectAddressType AVP of type Enumerated to the message.
	 */
	public RedirectAddressTypeAvp addRedirectAddressType(RedirectAddressTypeEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addRedirectAddressType()");
			}
			return new RedirectAddressTypeAvp(stackObj.addRedirectAddressType(RedirectAddressTypeEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addRedirectAddressType",e);
		}
	}

	/**
	 *  Adding RedirectServerAddress AVP of type UTF8String to the message.
	 */
	public RedirectServerAddressAvp addRedirectServerAddress(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addRedirectServerAddress()");
			}
			return new RedirectServerAddressAvp(stackObj.addRedirectServerAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addRedirectServerAddress",e);
		}
	}

	/**
	 *  This method returns the enum value corrosponding to RedirectAddressTypeAvp.
	 */
	public RedirectAddressTypeEnum getEnumRedirectAddressType( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumRedirectAddressType()");
			}
			return RedirectAddressTypeEnum.getContainerObj(stackObj.getEnumRedirectAddressType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumRedirectAddressType",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from RedirectAddressType AVPs.
	 */
	public int getRedirectAddressType( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRedirectAddressType()");
			}
			return stackObj.getRedirectAddressType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRedirectAddressType",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from RedirectServerAddress AVPs.
	 */
	public java.lang.String getRedirectServerAddress( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRedirectServerAddress()");
			}
			return stackObj.getRedirectServerAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRedirectServerAddress",e);
		}
	}

}
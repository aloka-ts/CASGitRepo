package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpApplicationServerInformation;

public class ApplicationServerInformationAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(ApplicationServerInformationAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpApplicationServerInformation stackObj;

	public ApplicationServerInformationAvp(AvpApplicationServerInformation stkObj){
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
	 *  Adding ApplicationProvidedCalledPartyAddress AVP of type UTF8String to the message.
	 */
	public ApplicationProvidedCalledPartyAddressAvp addApplicationProvidedCalledPartyAddress(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addApplicationProvidedCalledPartyAddress()");
			}
			return new ApplicationProvidedCalledPartyAddressAvp(stackObj.addApplicationProvidedCalledPartyAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addApplicationProvidedCalledPartyAddress",e);
		}
	}

	/**
	 *  Adding ApplicationServer AVP of type UTF8String to the message.
	 */
	public ApplicationServerAvp addApplicationServer(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addApplicationServer()");
			}
			return new ApplicationServerAvp(stackObj.addApplicationServer(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addApplicationServer",e);
		}
	}

	/**
	 *  Retrieving multiple UTF8String values from ApplicationProvidedCalledPartyAddress AVPs.
	 */
	public java.lang.String[] getApplicationProvidedCalledPartyAddresss() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getApplicationProvidedCalledPartyAddresss()");
			}
			return stackObj.getApplicationProvidedCalledPartyAddresss();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getApplicationProvidedCalledPartyAddresss",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from ApplicationServer AVPs.
	 */
	public java.lang.String getApplicationServer() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getApplicationServer()");
			}
			return stackObj.getApplicationServer();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getApplicationServer",e);
		}
	}


}
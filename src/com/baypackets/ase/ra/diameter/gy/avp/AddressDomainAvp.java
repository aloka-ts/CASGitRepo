package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpAddressDomain;

public class AddressDomainAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(AddressDomainAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpAddressDomain stackObj;

	public AddressDomainAvp(AvpAddressDomain stkObj){
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
	 *  Adding 3GPPIMSIMCCMNC AVP of type UTF8String to the message.
	 */
	public IMSIMCCMNC3GPPAvp add3GPPIMSIMCCMNC(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside add3GPPIMSIMCCMNC()");
			}
			return new IMSIMCCMNC3GPPAvp(stackObj.add3GPPIMSIMCCMNC(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in add3GPPIMSIMCCMNC",e);
		}
	}

	/**
	 *  Adding DomainName AVP of type UTF8String to the message.
	 */
	public DomainNameAvp addDomainName(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addDomainName()");
			}
			return new DomainNameAvp(stackObj.addDomainName(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addDomainName",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from 3GPPIMSIMCCMNC AVPs.
	 */
	public java.lang.String get3GPPIMSIMCCMNC() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside get3GPPIMSIMCCMNC()");
			}
			return stackObj.get3GPPIMSIMCCMNC();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in get3GPPIMSIMCCMNC",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from DomainName AVPs.
	 */
	public java.lang.String getDomainName() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getDomainName()");
			}
			return stackObj.getDomainName();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getDomainName",e);
		}
	}


}
package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpLCSRequestorID;

public class LCSRequestorIDAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(LCSRequestorIDAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpLCSRequestorID stackObj;

	public LCSRequestorIDAvp(AvpLCSRequestorID stkObj){
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
	 *  Adding LCSDataCodingScheme AVP of type UTF8String to the message.
	 */
	public LCSDataCodingSchemeAvp addLCSDataCodingScheme(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addLCSDataCodingScheme()");
			}
			return new LCSDataCodingSchemeAvp(stackObj.addLCSDataCodingScheme(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addLCSDataCodingScheme",e);
		}
	}

	/**
	 *  Adding LCSRequestorIDString AVP of type UTF8String to the message.
	 */
	public LCSRequestorIDStringAvp addLCSRequestorIDString(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addLCSRequestorIDString()");
			}
			return new LCSRequestorIDStringAvp(stackObj.addLCSRequestorIDString(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addLCSRequestorIDString",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from LCSDataCodingScheme AVPs.
	 */
	public java.lang.String getLCSDataCodingScheme() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getLCSDataCodingScheme()");
			}
			return stackObj.getLCSDataCodingScheme();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getLCSDataCodingScheme",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from LCSRequestorIDString AVPs.
	 */
	public java.lang.String getLCSRequestorIDString() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getLCSRequestorIDString()");
			}
			return stackObj.getLCSRequestorIDString();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getLCSRequestorIDString",e);
		}
	}


}
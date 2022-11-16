package com.baypackets.ase.ra.diameter.gy.avp;


import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.LCSFormatIndicatorEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpLCSClientName;
import com.traffix.openblox.diameter.gy.generated.enums.EnumLCSFormatIndicator;

public class LCSClientNameAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(LCSClientNameAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpLCSClientName stackObj;

	public LCSClientNameAvp(AvpLCSClientName stkObj){
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
	 *  Adding LCSFormatIndicator AVP of type Enumerated to the message.
	 */
	public LCSFormatIndicatorAvp addLCSFormatIndicator(EnumLCSFormatIndicator value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addLCSFormatIndicator()");
			}
			return new LCSFormatIndicatorAvp(stackObj.addLCSFormatIndicator(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addLCSFormatIndicator",e);
		}
	}

	/**
	 *  Adding LCSNameString AVP of type UTF8String to the message.
	 */
	public LCSNameStringAvp addLCSNameString(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addLCSNameString()");
			}
			return new LCSNameStringAvp(stackObj.addLCSNameString(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addLCSNameString",e);
		}
	}

	/**
	 *  Retrieves Enum of LCSFormatIndicator type.
	 */
	public LCSFormatIndicatorEnum getEnumLCSFormatIndicator() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumLCSFormatIndicator()");
			}
			return LCSFormatIndicatorEnum.getContainerObj(stackObj.getEnumLCSFormatIndicator());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumLCSFormatIndicator",e);
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
	 *  Retrieving a single Enumerated value from LCSFormatIndicator AVPs.
	 */
	public int getLCSFormatIndicator() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getLCSFormatIndicator()");
			}
			return stackObj.getLCSFormatIndicator();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getLCSFormatIndicator",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from LCSNameString AVPs.
	 */
	public java.lang.String getLCSNameString() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getLCSNameString()");
			}
			return stackObj.getLCSNameString();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getLCSNameString",e);
		}
	}


}
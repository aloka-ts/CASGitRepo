package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.LCSClientTypeEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpLCSClientID;
import com.traffix.openblox.diameter.gy.generated.enums.EnumLCSClientType;

public class LCSClientIDAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(LCSClientIDAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpLCSClientID stackObj;

	public LCSClientIDAvp(AvpLCSClientID stkObj){
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
	 *  Adding LCSClientName AVP of type Grouped to the message.
	 */
	public LCSClientNameAvp addGroupedLCSClientName() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedLCSClientName()");
			}
			return new LCSClientNameAvp(stackObj.addGroupedLCSClientName());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedLCSClientName",e);
		}
	}

	/**
	 *  Adding LCSRequestorID AVP of type Grouped to the message.
	 */
	public LCSRequestorIDAvp addGroupedLCSRequestorID() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedLCSRequestorID()");
			}
			return new LCSRequestorIDAvp(stackObj.addGroupedLCSRequestorID());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedLCSRequestorID",e);
		}
	}

	/**
	 *  Adding LCSAPN AVP of type UTF8String to the message.
	 */
	public LCSAPNAvp addLCSAPN(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addLCSAPN()");
			}
			return new LCSAPNAvp(stackObj.addLCSAPN(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addLCSAPN",e);
		}
	}

	/**
	 *  Adding LCSClientDialedByMS AVP of type UTF8String to the message.
	 */
	public LCSClientDialedByMSAvp addLCSClientDialedByMS(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addLCSClientDialedByMS()");
			}
			return new LCSClientDialedByMSAvp(stackObj.addLCSClientDialedByMS(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addLCSClientDialedByMS",e);
		}
	}

	/**
	 *  Adding LCSClientExternalID AVP of type UTF8String to the message.
	 */
	public LCSClientExternalIDAvp addLCSClientExternalID(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addLCSClientExternalID()");
			}
			return new LCSClientExternalIDAvp(stackObj.addLCSClientExternalID(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addLCSClientExternalID",e);
		}
	}

	/**
	 *  Adding LCSClientType AVP of type Enumerated to the message.
	 */
	public LCSClientTypeAvp addLCSClientType(EnumLCSClientType value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addLCSClientType()");
			}
			return new LCSClientTypeAvp(stackObj.addLCSClientType(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addLCSClientType",e);
		}
	}

	/**
	 *  Retrieves Enum of LCSClientType
	 */
	public LCSClientTypeEnum getEnumLCSClientType() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumLCSClientType()");
			}
			return LCSClientTypeEnum.getContainerObj(stackObj.getEnumLCSClientType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumLCSClientType",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from LCSClientName AVPs.
	 */
	public LCSClientNameAvp getGroupedLCSClientName() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedLCSClientName()");
			}
			return new LCSClientNameAvp(stackObj.getGroupedLCSClientName());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedLCSClientName",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from LCSRequestorID AVPs.
	 */
	public LCSRequestorIDAvp getGroupedLCSRequestorID() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedLCSRequestorID()");
			}
			return new LCSRequestorIDAvp(stackObj.getGroupedLCSRequestorID());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedLCSRequestorID",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from LCSAPN AVPs.
	 */
	public java.lang.String getLCSAPN() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getLCSAPN()");
			}
			return stackObj.getLCSAPN();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getLCSAPN",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from LCSClientDialedByMS AVPs.
	 */
	public java.lang.String getLCSClientDialedByMS() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getLCSClientDialedByMS()");
			}
			return stackObj.getLCSClientDialedByMS();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getLCSClientDialedByMS",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from LCSClientExternalID AVPs.
	 */
	public java.lang.String getLCSClientExternalID() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getLCSClientExternalID()");
			}
			return stackObj.getLCSClientExternalID();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getLCSClientExternalID",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from LCSClientType AVPs.
	 */
	public int getLCSClientType() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getLCSClientType()");
			}
			return stackObj.getLCSClientType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getLCSClientType",e);
		}
	}


}
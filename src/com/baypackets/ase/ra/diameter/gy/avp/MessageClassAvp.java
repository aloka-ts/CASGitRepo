package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.ClassIdentifierEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpMessageClass;
import com.traffix.openblox.diameter.gy.generated.enums.EnumClassIdentifier;

public class MessageClassAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(MessageClassAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpMessageClass stackObj;

	public MessageClassAvp(AvpMessageClass stkObj){
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
	 *  Adding ClassIdentifier AVP of type Enumerated to the message.
	 */
	public ClassIdentifierAvp addClassIdentifier(EnumClassIdentifier value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addClassIdentifier()");
			}
			return new ClassIdentifierAvp(stackObj.addClassIdentifier(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addClassIdentifier",e);
		}
	}

	/**
	 *  Adding TokenText AVP of type UTF8String to the message.
	 */
	public TokenTextAvp addTokenText(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addTokenText()");
			}
			return new TokenTextAvp(stackObj.addTokenText(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addTokenText",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from ClassIdentifier AVPs.
	 */
	public int getClassIdentifier() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getClassIdentifier()");
			}
			return stackObj.getClassIdentifier();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getClassIdentifier",e);
		}
	}

	/**
	 *  This method returns the Enum value of class identifier.
	 */
	public ClassIdentifierEnum getEnumClassIdentifier() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumClassIdentifier()");
			}
			return ClassIdentifierEnum.getContainerObj((stackObj.getEnumClassIdentifier()));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumClassIdentifier",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from TokenText AVPs.
	 */
	public java.lang.String getTokenText() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getTokenText()");
			}
			return stackObj.getTokenText();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getTokenText",e);
		}
	}


}

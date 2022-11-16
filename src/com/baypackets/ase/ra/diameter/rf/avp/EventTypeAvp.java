package com.baypackets.ase.ra.diameter.rf.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.rf.RfResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.rf.generated.avp.AvpEventType;

public class EventTypeAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(EventTypeAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpEventType stackObj;

	public EventTypeAvp(AvpEventType stkObj){
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
	*  Adding Event AVP of type UTF8String to the message.
	 * @throws RfResourceException 
	*/
	public EventAvp addEvent(java.lang.String value) throws RfResourceException  {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addEvent()");
			}
			return new EventAvp(stackObj.addEvent(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addEvent ",e);
		}
	}

	/**
	*  Adding Expires AVP of type Unsigned32 to the message.
	 * @throws RfResourceException 
	*/
	public ExpiresAvp addExpires(long value) throws RfResourceException  {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addExpires()");
			}
			return new ExpiresAvp(stackObj.addExpires(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addExpires ",e);
		}
	}

	/**
	*  Adding SIPMethod AVP of type UTF8String to the message.
	 * @throws RfResourceException 
	*/
	public SIPMethodAvp addSIPMethod(java.lang.String value) throws RfResourceException  {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSIPMethod()");
			}
			return new SIPMethodAvp(stackObj.addSIPMethod(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addSIPMethod ",e);
		}
	}

	/**
	*  Retrieving a single UTF8String value from Event AVPs.
	 * @throws RfResourceException 
	*/
	public java.lang.String getEvent() throws RfResourceException  {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEvent()");
			}
			return	stackObj.getEvent();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getEvent ",e);
		}
	}

	/**
	*  Retrieving a single Unsigned32 value from Expires AVPs.
	 * @throws RfResourceException 
	*/
	public long getExpires() throws RfResourceException  {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getExpires()");
			}
			return	stackObj.getExpires();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getExpires ",e);
		}
	}

	/**
	*  Retrieving a single UTF8String value from SIPMethod AVPs.
	 * @throws RfResourceException 
	*/
	public java.lang.String getSIPMethod() throws RfResourceException  {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getSIPMethod()");
			}
			return	stackObj.getSIPMethod();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getSIPMethod ",e);
		}
	}
}
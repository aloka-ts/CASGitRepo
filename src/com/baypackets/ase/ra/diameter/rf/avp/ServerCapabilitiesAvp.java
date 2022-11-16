package com.baypackets.ase.ra.diameter.rf.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterUTF8String;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.rf.RfResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.rf.generated.avp.AvpServerCapabilities;

public class ServerCapabilitiesAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(ServerCapabilitiesAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpServerCapabilities stackObj;

	public ServerCapabilitiesAvp(AvpServerCapabilities stkObj){
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
	*  Adding MandatoryCapability AVP of type Unsigned32 to the message.
	*/
	public MandatoryCapabilityAvp addMandatoryCapability(long value) throws RfResourceException  {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addMandatoryCapability()");
			}
			return new MandatoryCapabilityAvp(stackObj.addMandatoryCapability(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addMandatoryCapability ",e);
		}
	}

	/**
	*  Adding OptionalCapability AVP of type Unsigned32 to the message.
	*/
	public OptionalCapabilityAvp addOptionalCapability(long value) throws RfResourceException  {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addOptionalCapability()");
			}
			return new OptionalCapabilityAvp(stackObj.addOptionalCapability(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addOptionalCapability ",e);
		}
	}

	/**
	*  Adding ServerName AVP of type UTF8String to the message.
	*/
	public ServerNameAvp addServerName(java.lang.String value) throws RfResourceException  {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addServerName()");
			}
			return new ServerNameAvp(stackObj.addServerName(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addServerName ",e);
		}
	}


	/**
	*  Retrieving multiple Unsigned32 values from MandatoryCapability AVPs.
	*/
	public long[] getMandatoryCapabilitys() throws RfResourceException  {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getMandatoryCapabilitys()");
			}
			return	stackObj.getMandatoryCapabilitys();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getMandatoryCapabilitys ",e);
		}
	}

	/**
	*  Retrieving multiple Unsigned32 values from OptionalCapability AVPs.
	*/
	public long[] getOptionalCapabilitys() throws RfResourceException  {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getOptionalCapabilitys()");
			}
			return	stackObj.getOptionalCapabilitys();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getOptionalCapabilitys ",e);
		}
	}

	/**
	*  Retrieving multiple UTF8String values from ServerName AVPs.
	 * @throws RfResourceException 
	*/
	public java.lang.String[] getServerNames() throws RfResourceException  {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getServerNames()");
			}
			return	stackObj.getServerNames();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getServerNames ",e);
		}
	}

}
package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpProxyInfo;

public class ProxyInfoAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(ProxyInfoAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpProxyInfo stackObj;

	public ProxyInfoAvp(AvpProxyInfo stkObj){
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
	 *  Adding ProxyHost AVP of type DiameterIdentity to the message.
	 */
	public ProxyHostAvp addProxyHost(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addProxyHost()");
			}
			return new ProxyHostAvp(stackObj.addProxyHost(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addProxyHost",e);
		}
	}

	/**
	 *  Adding ProxyState AVP of type OctetString to the message.
	 */
	public ProxyStateAvp addProxyState(byte[] value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addProxyState()");
			}
			return new ProxyStateAvp(stackObj.addProxyState(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addProxyState",e);
		}
	}

	/**
	 *  Adding ProxyState AVP of type OctetString to the message.
	 */
	public ProxyStateAvp addProxyState(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addProxyState()");
			}
			return new ProxyStateAvp(stackObj.addProxyState(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addProxyState",e);
		}
	}

	/**
	 *  Retrieving a single DiameterIdentity value from ProxyHost AVPs.
	 */
	public java.lang.String getProxyHost() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getProxyHost()");
			}
			return stackObj.getProxyHost();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getProxyHost",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from ProxyState AVPs.
	 */
	public java.lang.String getProxyState() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getProxyState()");
			}
			return stackObj.getProxyState();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getProxyState",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from ProxyState AVPs.
	 */
	public byte[] getRawProxyState() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawProxyState()");
			}
			return stackObj.getRawProxyState();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawProxyState",e);
		}
	}

}
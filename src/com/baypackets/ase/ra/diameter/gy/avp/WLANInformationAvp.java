package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpWLANInformation;

public class WLANInformationAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(WLANInformationAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpWLANInformation stackObj;

	public WLANInformationAvp(AvpWLANInformation stkObj){
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
	 *  Adding WLANRadioContainer AVP of type Grouped to the message.
	 */
	public WLANRadioContainerAvp addGroupedWLANRadioContainer( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedWLANRadioContainer()");
			}
			return new WLANRadioContainerAvp(stackObj.addGroupedWLANRadioContainer());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedWLANRadioContainer",e);
		}
	}

	/**
	 *  Adding PDGAddress AVP of type Address to the message.
	 */
	public PDGAddressAvp addPDGAddress(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addPDGAddress()");
			}
			return new PDGAddressAvp(stackObj.addPDGAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addPDGAddress",e);
		}
	}

	/**
	 *  Adding PDGAddress AVP of type Address to the message.
	 */
	public PDGAddressAvp addPDGAddress(java.net.InetAddress value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addPDGAddress()");
			}
			return new PDGAddressAvp(stackObj.addPDGAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addPDGAddress",e);
		}
	}

	/**
	 *  Adding PDGChargingId AVP of type Unsigned32 to the message.
	 */
	public PDGChargingIdAvp addPDGChargingId(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addPDGChargingId()");
			}
			return new PDGChargingIdAvp(stackObj.addPDGChargingId(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addPDGChargingId",e);
		}
	}

	/**
	 *  Adding WAGAddress AVP of type Address to the message.
	 */
	public WAGAddressAvp addWAGAddress(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addWAGAddress()");
			}
			return new WAGAddressAvp(stackObj.addWAGAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addWAGAddress",e);
		}
	}

	/**
	 *  Adding WAGAddress AVP of type Address to the message.
	 */
	public WAGAddressAvp addWAGAddress(java.net.InetAddress value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addWAGAddress()");
			}
			return new WAGAddressAvp(stackObj.addWAGAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addWAGAddress",e);
		}
	}

	/**
	 *  Adding WAGPLMNId AVP of type OctetString to the message.
	 */
	public WAGPLMNIdAvp addWAGPLMNId(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addWAGPLMNId()");
			}
			return new WAGPLMNIdAvp(stackObj.addWAGPLMNId(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addWAGPLMNId",e);
		}
	}

	/**
	 *  Adding WAGPLMNId AVP of type OctetString to the message.
	 */
	public WAGPLMNIdAvp addWAGPLMNId(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addWAGPLMNId()");
			}
			return new WAGPLMNIdAvp(stackObj.addWAGPLMNId(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addWAGPLMNId",e);
		}
	}

	/**
	 *  Adding WLANSessionId AVP of type UTF8String to the message.
	 */
	public WLANSessionIdAvp addWLANSessionId(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addWLANSessionId()");
			}
			return new WLANSessionIdAvp(stackObj.addWLANSessionId(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addWLANSessionId",e);
		}
	}

	/**
	 *  Adding WLANUELocalIPAddress AVP of type Address to the message.
	 */
	public WLANUELocalIPAddressAvp addWLANUELocalIPAddress(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addWLANUELocalIPAddress()");
			}
			return new WLANUELocalIPAddressAvp(stackObj.addWLANUELocalIPAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addWLANUELocalIPAddress",e);
		}
	}

	/**
	 *  Adding WLANUELocalIPAddress AVP of type Address to the message.
	 */
	public WLANUELocalIPAddressAvp addWLANUELocalIPAddress(java.net.InetAddress value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addWLANUELocalIPAddress()");
			}
			return new WLANUELocalIPAddressAvp(stackObj.addWLANUELocalIPAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addWLANUELocalIPAddress",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from WLANRadioContainer AVPs.
	 */
	public WLANRadioContainerAvp getGroupedWLANRadioContainer( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedWLANRadioContainer()");
			}
			return new WLANRadioContainerAvp(stackObj.getGroupedWLANRadioContainer());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedWLANRadioContainer",e);
		}
	}

	/**
	 *  Retrieving a single Address value from PDGAddress AVPs.
	 */
	public java.net.InetAddress getPDGAddress( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getPDGAddress()");
			}
			return stackObj.getPDGAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getPDGAddress",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from PDGChargingId AVPs.
	 */
	public long getPDGChargingId( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getPDGChargingId()");
			}
			return stackObj.getPDGChargingId();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getPDGChargingId",e);
		}
	}

	/**
	 *  Retrieving a single Address value from PDGAddress AVPs.
	 */
	public byte[] getRawPDGAddress( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawPDGAddress()");
			}
			return stackObj.getRawPDGAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawPDGAddress",e);
		}
	}

	/**
	 *  Retrieving a single Address value from WAGAddress AVPs.
	 */
	public byte[] getRawWAGAddress( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawWAGAddress()");
			}
			return stackObj.getRawWAGAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawWAGAddress",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from WAGPLMNId AVPs.
	 */
	public byte[] getRawWAGPLMNId( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawWAGPLMNId()");
			}
			return stackObj.getRawWAGPLMNId();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawWAGPLMNId",e);
		}
	}

	/**
	 *  Retrieving a single Address value from WLANUELocalIPAddress AVPs.
	 */
	public byte[] getRawWLANUELocalIPAddress( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawWLANUELocalIPAddress()");
			}
			return stackObj.getRawWLANUELocalIPAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawWLANUELocalIPAddress",e);
		}
	}

	/**
	 *  Retrieving a single Address value from WAGAddress AVPs.
	 */
	public java.net.InetAddress getWAGAddress( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getWAGAddress()");
			}
			return stackObj.getWAGAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getWAGAddress",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from WAGPLMNId AVPs.
	 */
	public java.lang.String getWAGPLMNId( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getWAGPLMNId()");
			}
			return stackObj.getWAGPLMNId();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getWAGPLMNId",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from WLANSessionId AVPs.
	 */
	public java.lang.String getWLANSessionId( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getWLANSessionId()");
			}
			return stackObj.getWLANSessionId();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getWLANSessionId",e);
		}
	}

	/**
	 *  Retrieving a single Address value from WLANUELocalIPAddress AVPs.
	 */
	public java.net.InetAddress getWLANUELocalIPAddress( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getWLANUELocalIPAddress()");
			}
			return stackObj.getWLANUELocalIPAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getWLANUELocalIPAddress",e);
		}
	}

}
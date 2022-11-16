package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpRecipientInfo;
import com.traffix.openblox.diameter.gy.generated.avp.AvpRecipientAddress;
import com.traffix.openblox.diameter.gy.generated.avp.AvpRecipientReceivedAddress;

public class RecipientInfoAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(RecipientInfoAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpRecipientInfo stackObj;

	public RecipientInfoAvp(AvpRecipientInfo stkObj){
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
	 *  Adding DestinationInterface AVP of type Grouped to the message.
	 */
	public DestinationInterfaceAvp addGroupedDestinationInterface( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedDestinationInterface()");
			}
			return new DestinationInterfaceAvp(stackObj.addGroupedDestinationInterface());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedDestinationInterface",e);
		}
	}

	/**
	 *  Adding RecipientAddress AVP of type Grouped to the message.
	 */
	public RecipientAddressAvp addGroupedRecipientAddress( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedRecipientAddress()");
			}
			return new RecipientAddressAvp(stackObj.addGroupedRecipientAddress());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedRecipientAddress",e);
		}
	}

	/**
	 *  Adding RecipientReceivedAddress AVP of type Grouped to the message.
	 */
	public RecipientReceivedAddressAvp addGroupedRecipientReceivedAddress( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedRecipientReceivedAddress()");
			}
			return new RecipientReceivedAddressAvp(stackObj.addGroupedRecipientReceivedAddress());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedRecipientReceivedAddress",e);
		}
	}

	/**
	 *  Adding RecipientSCCPAddress AVP of type Address to the message.
	 */
	public RecipientSCCPAddressAvp addRecipientSCCPAddress(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addRecipientSCCPAddress()");
			}
			return new RecipientSCCPAddressAvp(stackObj.addRecipientSCCPAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addRecipientSCCPAddress",e);
		}
	}

	/**
	 *  Adding RecipientSCCPAddress AVP of type Address to the message.
	 */
	public RecipientSCCPAddressAvp addRecipientSCCPAddress(java.net.InetAddress value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addRecipientSCCPAddress()");
			}
			return new RecipientSCCPAddressAvp(stackObj.addRecipientSCCPAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addRecipientSCCPAddress",e);
		}
	}

	/**
	 *  Adding SMProtocolID AVP of type OctetString to the message.
	 */
	public SMProtocolIDAvp addSMProtocolID(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSMProtocolID()");
			}
			return new SMProtocolIDAvp(stackObj.addSMProtocolID(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addSMProtocolID",e);
		}
	}

	/**
	 *  Adding SMProtocolID AVP of type OctetString to the message.
	 */
	public SMProtocolIDAvp addSMProtocolID(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSMProtocolID()");
			}
			return new SMProtocolIDAvp(stackObj.addSMProtocolID(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addSMProtocolID",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from DestinationInterface AVPs.
	 */
	public DestinationInterfaceAvp getGroupedDestinationInterface( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedDestinationInterface()");
			}
			return new DestinationInterfaceAvp(stackObj.getGroupedDestinationInterface());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedDestinationInterface",e);
		}
	}

	/**
	 *  Retrieving multiple Grouped values from RecipientAddress AVPs.
	 */
	public RecipientAddressAvp[] getGroupedRecipientAddresss( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedRecipientAddresss()");
			}
			AvpRecipientAddress[] stackAv= stackObj.getGroupedRecipientAddresss();
			RecipientAddressAvp[] contAvp= new RecipientAddressAvp[stackAv.length];
			for(int i=0;i<stackAv.length;i++){
				contAvp[i]=new RecipientAddressAvp(stackAv[i]);
			}
			return contAvp;
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedRecipientAddresss",e);
		}
	}

	/**
	 *  Retrieving multiple Grouped values from RecipientReceivedAddress AVPs.
	 */
	public RecipientReceivedAddressAvp[] getGroupedRecipientReceivedAddresss( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedRecipientReceivedAddresss()");
			}
			AvpRecipientReceivedAddress[] stackAv= stackObj.getGroupedRecipientReceivedAddresss();
			RecipientReceivedAddressAvp[] contAvp= new RecipientReceivedAddressAvp[stackAv.length];
			for(int i=0;i<stackAv.length;i++){
				contAvp[i]=new RecipientReceivedAddressAvp(stackAv[i]);
			}
			return contAvp;
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedRecipientReceivedAddresss",e);
		}
	}

	/**
	 *  Retrieving a single Address value from RecipientSCCPAddress AVPs.
	 */
	public byte[] getRawRecipientSCCPAddress( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawRecipientSCCPAddress()");
			}
			return stackObj.getRawRecipientSCCPAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawRecipientSCCPAddress",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from SMProtocolID AVPs.
	 */
	public byte[] getRawSMProtocolID( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawSMProtocolID()");
			}
			return stackObj.getRawSMProtocolID();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawSMProtocolID",e);
		}
	}

	/**
	 *  Retrieving a single Address value from RecipientSCCPAddress AVPs.
	 */
	public java.net.InetAddress getRecipientSCCPAddress( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRecipientSCCPAddress()");
			}
			return stackObj.getRecipientSCCPAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRecipientSCCPAddress",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from SMProtocolID AVPs.
	 */
	public java.lang.String getSMProtocolID( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getSMProtocolID()");
			}
			return stackObj.getSMProtocolID();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getSMProtocolID",e);
		}
	}

}
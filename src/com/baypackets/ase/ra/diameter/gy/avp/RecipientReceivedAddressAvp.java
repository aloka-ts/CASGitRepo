package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.AddressTypeEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpRecipientReceivedAddress;

public class RecipientReceivedAddressAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(RecipientReceivedAddressAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpRecipientReceivedAddress stackObj;

	public RecipientReceivedAddressAvp(AvpRecipientReceivedAddress stkObj){
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
	 *  Adding AddressData AVP of type UTF8String to the message.
	 */
	public AddressDataAvp addAddressData(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAddressData()");
			}
			return new AddressDataAvp(stackObj.addAddressData(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addAddressData",e);
		}
	}

	/**
	 *  Adding AddressType AVP of type Enumerated to the message.
	 */
	public AddressTypeAvp addAddressType(AddressTypeEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAddressType()");
			}
			return new AddressTypeAvp(stackObj.addAddressType(AddressTypeEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addAddressType",e);
		}
	}

	/**
	 *  Adding AddressDomain AVP of type Grouped to the message.
	 */
	public AddressDomainAvp addGroupedAddressDomain( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedAddressDomain()");
			}
			return new AddressDomainAvp(stackObj.addGroupedAddressDomain());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedAddressDomain",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from AddressData AVPs.
	 */
	public java.lang.String getAddressData( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAddressData()");
			}
			return stackObj.getAddressData();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getAddressData",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from AddressType AVPs.
	 */
	public int getAddressType( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAddressType()");
			}
			return stackObj.getAddressType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getAddressType",e);
		}
	}

	/**
	 *  This method retunrs the enum value corrosponding to AddressTypeAvp.
	 */
	public AddressTypeEnum getEnumAddressType( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumAddressType()");
			}
			return AddressTypeEnum.getContainerObj(stackObj.getEnumAddressType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumAddressType",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from AddressDomain AVPs.
	 */
	public AddressDomainAvp getGroupedAddressDomain( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedAddressDomain()");
			}
			return new AddressDomainAvp(stackObj.getGroupedAddressDomain());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedAddressDomain",e);
		}
	}


}
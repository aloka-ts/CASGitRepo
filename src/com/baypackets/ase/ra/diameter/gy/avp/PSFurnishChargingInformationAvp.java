package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.PSAppendFreeFormatDataEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpPSFurnishChargingInformation;

public class PSFurnishChargingInformationAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(PSFurnishChargingInformationAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpPSFurnishChargingInformation stackObj;

	public PSFurnishChargingInformationAvp(AvpPSFurnishChargingInformation stkObj){
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
	 *  Adding 3GPPChargingId AVP of type OctetString to the message.
	 */
	public ChargingId3GPPAvp add3GPPChargingId(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside add3GPPChargingId()");
			}
			return new ChargingId3GPPAvp(stackObj.add3GPPChargingId(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in add3GPPChargingId",e);
		}
	}

	/**
	 *  Adding 3GPPChargingId AVP of type OctetString to the message.
	 */
	public ChargingId3GPPAvp add3GPPChargingId(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside add3GPPChargingId()");
			}
			return new ChargingId3GPPAvp(stackObj.add3GPPChargingId(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in add3GPPChargingId",e);
		}
	}

	/**
	 *  Adding PSAppendFreeFormatData AVP of type Enumerated to the message.
	 */
	public PSAppendFreeFormatDataAvp addPSAppendFreeFormatData(PSAppendFreeFormatDataEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addPSAppendFreeFormatData()");
			}
			return new PSAppendFreeFormatDataAvp(stackObj.addPSAppendFreeFormatData(PSAppendFreeFormatDataEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addPSAppendFreeFormatData",e);
		}
	}

	/**
	 *  Adding PSFreeFormatData AVP of type OctetString to the message.
	 */
	public PSFreeFormatDataAvp addPSFreeFormatData(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addPSFreeFormatData()");
			}
			return new PSFreeFormatDataAvp(stackObj.addPSFreeFormatData(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addPSFreeFormatData",e);
		}
	}

	/**
	 *  Adding PSFreeFormatData AVP of type OctetString to the message.
	 */
	public PSFreeFormatDataAvp addPSFreeFormatData(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addPSFreeFormatData()");
			}
			return new PSFreeFormatDataAvp(stackObj.addPSFreeFormatData(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addPSFreeFormatData",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from 3GPPChargingId AVPs.
	 */
	public java.lang.String get3GPPChargingId( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside get3GPPChargingId()");
			}
			return stackObj.get3GPPChargingId();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in get3GPPChargingId",e);
		}
	}

	/**
	 *  This method returns the enum corresponding to PSAppendFreeFormatDataAvp.
	 */
	public PSAppendFreeFormatDataEnum getEnumPSAppendFreeFormatData( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumPSAppendFreeFormatData()");
			}
			return PSAppendFreeFormatDataEnum.getContainerObj(stackObj.getEnumPSAppendFreeFormatData());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumPSAppendFreeFormatData",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from PSAppendFreeFormatData AVPs.
	 */
	public int getPSAppendFreeFormatData( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getPSAppendFreeFormatData()");
			}
			return stackObj.getPSAppendFreeFormatData();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getPSAppendFreeFormatData",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from PSFreeFormatData AVPs.
	 */
	public java.lang.String getPSFreeFormatData( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getPSFreeFormatData()");
			}
			return stackObj.getPSFreeFormatData();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getPSFreeFormatData",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from 3GPPChargingId AVPs.
	 */
	public byte[] getRaw3GPPChargingId( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRaw3GPPChargingId()");
			}
			return stackObj.getRaw3GPPChargingId();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRaw3GPPChargingId",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from PSFreeFormatData AVPs.
	 */
	public byte[] getRawPSFreeFormatData( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawPSFreeFormatData()");
			}
			return stackObj.getRawPSFreeFormatData();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawPSFreeFormatData",e);
		}
	}

}
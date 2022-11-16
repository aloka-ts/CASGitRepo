package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.MediaInitiatorFlagEnum;
import com.baypackets.ase.ra.diameter.gy.enums.SDPTypeEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpSDPMediaComponent;

public class SDPMediaComponentAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(SDPMediaComponentAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpSDPMediaComponent stackObj;

	public SDPMediaComponentAvp(AvpSDPMediaComponent stkObj){
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
	 *  Adding AccessNetworkChargingIdentifierValue AVP of type OctetString to the message.
	 */
	public AccessNetworkChargingIdentifierValueAvp addAccessNetworkChargingIdentifierValue(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAccessNetworkChargingIdentifierValue()");
			}
			return new AccessNetworkChargingIdentifierValueAvp(stackObj.addAccessNetworkChargingIdentifierValue(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addAccessNetworkChargingIdentifierValue",e);
		}
	}

	/**
	 *  Adding AccessNetworkChargingIdentifierValue AVP of type OctetString to the message.
	 */
	public AccessNetworkChargingIdentifierValueAvp addAccessNetworkChargingIdentifierValue(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAccessNetworkChargingIdentifierValue()");
			}
			return new AccessNetworkChargingIdentifierValueAvp(stackObj.addAccessNetworkChargingIdentifierValue(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addAccessNetworkChargingIdentifierValue",e);
		}
	}

	/**
	 *  Adding AuthorizedQoS AVP of type UTF8String to the message.
	 */
//	public AuthorizedQoSAvp addAuthorizedQoS(java.lang.String value) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addAuthorizedQoS()");
//			}
//			return new AuthorizedQoSAvp(stackObj.addAuthorizedQoS(value));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addAuthorizedQoS",e);
//		}
//	}

	/**
	 *  Adding MediaInitiatorFlag AVP of type Enumerated to the message.
	 */
	public MediaInitiatorFlagAvp addMediaInitiatorFlag(MediaInitiatorFlagEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addMediaInitiatorFlag()");
			}
			return new MediaInitiatorFlagAvp(stackObj.addMediaInitiatorFlag(MediaInitiatorFlagEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addMediaInitiatorFlag",e);
		}
	}

	/**
	 *  Adding MediaInitiatorParty AVP of type UTF8String to the message.
	 */
	public MediaInitiatorPartyAvp addMediaInitiatorParty(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addMediaInitiatorParty()");
			}
			return new MediaInitiatorPartyAvp(stackObj.addMediaInitiatorParty(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addMediaInitiatorParty",e);
		}
	}

	/**
	 *  Adding SDPMediaDescription AVP of type UTF8String to the message.
	 */
	public SDPMediaDescriptionAvp addSDPMediaDescription(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSDPMediaDescription()");
			}
			return new SDPMediaDescriptionAvp(stackObj.addSDPMediaDescription(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addSDPMediaDescription",e);
		}
	}

	/**
	 *  Adding SDPMediaName AVP of type UTF8String to the message.
	 */
	public SDPMediaNameAvp addSDPMediaName(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSDPMediaName()");
			}
			return new SDPMediaNameAvp(stackObj.addSDPMediaName(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addSDPMediaName",e);
		}
	}

	/**
	 *  Adding SDPType AVP of type Enumerated to the message.
	 */
//	public SDPTypeAvp addSDPType(SDPTypeEnum value) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addSDPType()");
//			}
//			return new SDPTypeAvp(stackObj.addSDPType(SDPTypeEnum.getStackObj(value)));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addSDPType",e);
//		}
//	}

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
	 *  Retrieving a single OctetString value from AccessNetworkChargingIdentifierValue AVPs.
	 */
	public java.lang.String getAccessNetworkChargingIdentifierValue( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAccessNetworkChargingIdentifierValue()");
			}
			return stackObj.getAccessNetworkChargingIdentifierValue();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getAccessNetworkChargingIdentifierValue",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from AuthorizedQoS AVPs.
//	 */
//	public java.lang.String getAuthorizedQoS( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getAuthorizedQoS()");
//			}
//			return stackObj.getAuthorizedQoS();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getAuthorizedQoS",e);
//		}
//	}

	/**
	 *  This method returns the enum value corrosponding to MediaInitiatorFlagAvp.
	 */
	public MediaInitiatorFlagEnum getEnumMediaInitiatorFlag( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumMediaInitiatorFlag()");
			}
			return MediaInitiatorFlagEnum.getContainerObj(stackObj.getEnumMediaInitiatorFlag());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumMediaInitiatorFlag",e);
		}
	}

	/**
	 *  This method returns the enum value corrosponding to SDPTypeAvp.
	 */
//	public SDPTypeEnum getEnumSDPType( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getEnumSDPType()");
//			}
//			return SDPTypeEnum.getContainerObj(stackObj.getEnumSDPType());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getEnumSDPType",e);
//		}
//	}

	/**
	 *  Retrieving a single Enumerated value from MediaInitiatorFlag AVPs.
	 */
	public int getMediaInitiatorFlag( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getMediaInitiatorFlag()");
			}
			return stackObj.getMediaInitiatorFlag();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getMediaInitiatorFlag",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from MediaInitiatorParty AVPs.
	 */
	public java.lang.String getMediaInitiatorParty( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getMediaInitiatorParty()");
			}
			return stackObj.getMediaInitiatorParty();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getMediaInitiatorParty",e);
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
	 *  Retrieving a single OctetString value from AccessNetworkChargingIdentifierValue AVPs.
	 */
	public byte[] getRawAccessNetworkChargingIdentifierValue( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawAccessNetworkChargingIdentifierValue()");
			}
			return stackObj.getRawAccessNetworkChargingIdentifierValue();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawAccessNetworkChargingIdentifierValue",e);
		}
	}

	/**
	 *  Retrieving multiple UTF8String values from SDPMediaDescription AVPs.
	 */
	public java.lang.String[] getSDPMediaDescriptions( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getSDPMediaDescriptions()");
			}
			return stackObj.getSDPMediaDescriptions();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getSDPMediaDescriptions",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from SDPMediaName AVPs.
	 */
	public java.lang.String getSDPMediaName( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getSDPMediaName()");
			}
			return stackObj.getSDPMediaName();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getSDPMediaName",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from SDPType AVPs.
	 */
//	public int getSDPType( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getSDPType()");
//			}
//			return stackObj.getSDPType();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getSDPType",e);
//		}
//	}

}
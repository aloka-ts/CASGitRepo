package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.ParticipantActionTypeEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.enums.FlagRule;
import com.traffix.openblox.diameter.gy.generated.avp.AvpSupplementaryService;

public class SupplementaryServiceAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(SupplementaryServiceAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpSupplementaryService stackObj;

	public SupplementaryServiceAvp(AvpSupplementaryService stkObj){
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
	 *  Adding AssociatedPartyAddress AVP of type UTF8String to the message.
	 */
	public AssociatedPartyAddressAvp addAssociatedPartyAddress(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAssociatedPartyAddress()");
			}
			return new AssociatedPartyAddressAvp(stackObj.addAssociatedPartyAddress(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addAssociatedPartyAddress",e);
		}
	}

	/**
	 *  Adding ChangeTime AVP of type Time to the message.
	 */
	public ChangeTimeAvp addChangeTime(java.util.Date value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addChangeTime()");
			}
			return new ChangeTimeAvp(stackObj.addChangeTime(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addChangeTime",e);
		}
	}

	/**
	 *  Adding CUGInformation AVP of type OctetString to the message.
	 */
	public CUGInformationAvp addCUGInformation(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCUGInformation()");
			}
			return new CUGInformationAvp(stackObj.addCUGInformation(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCUGInformation",e);
		}
	}

	/**
	 *  Adding CUGInformation AVP of type OctetString to the message.
	 */
	public CUGInformationAvp addCUGInformation(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCUGInformation()");
			}
			return new CUGInformationAvp(stackObj.addCUGInformation(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCUGInformation",e);
		}
	}

	/**
	 *  Adding NumberOfDiversions AVP of type Unsigned32 to the message.
	 */
	public NumberOfDiversionsAvp addNumberOfDiversions(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addNumberOfDiversions()");
			}
			return new NumberOfDiversionsAvp(stackObj.addNumberOfDiversions(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addNumberOfDiversions",e);
		}
	}

	/**
	 *  Adding NumberOfParticipants AVP of type Unsigned32 to the message.
	 */
	public NumberOfParticipantsAvp addNumberOfParticipants(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addNumberOfParticipants()");
			}
			return new NumberOfParticipantsAvp(stackObj.addNumberOfParticipants(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addNumberOfParticipants",e);
		}
	}

	/**
	 *  Adding ParticipantActionType AVP of type Enumerated to the message.
	 */
	public ParticipantActionTypeAvp addParticipantActionType(ParticipantActionTypeEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addParticipantActionType()");
			}
			return new ParticipantActionTypeAvp(stackObj.addParticipantActionType(ParticipantActionTypeEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addParticipantActionType",e);
		}
	}

	/**
	 *  Adding ServiceId AVP of type UTF8String to the message.
	 */
	public ServiceIdAvp addServiceId(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addServiceId()");
			}
			return new ServiceIdAvp(stackObj.addServiceId(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addServiceId",e);
		}
	}

	/**
	 *  Adding ServiceMode AVP of type Unsigned32 to the message.
	 */
	public ServiceModeAvp addServiceMode(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addServiceMode()");
			}
			return new ServiceModeAvp(stackObj.addServiceMode(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addServiceMode",e);
		}
	}

	/**
	 *  Adding ServiceType AVP of type Unsigned32 to the message.
	 */
	public ServiceTypeAvp addServiceType(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addServiceType()");
			}
			return new ServiceTypeAvp(stackObj.addServiceType(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addServiceType",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from AssociatedPartyAddress AVPs.
	 */
	public java.lang.String getAssociatedPartyAddress( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAssociatedPartyAddress()");
			}
			return stackObj.getAssociatedPartyAddress();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getAssociatedPartyAddress",e);
		}
	}

	/**
	 *  Retrieving a single Time value from ChangeTime AVPs.
	 */
	public java.util.Date getChangeTime( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getChangeTime()");
			}
			return stackObj.getChangeTime();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getChangeTime",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from CUGInformation AVPs.
	 */
	public java.lang.String getCUGInformation( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCUGInformation()");
			}
			return stackObj.getCUGInformation();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCUGInformation",e);
		}
	}

	/**
	 *  This method returns the enum value of ParticipantActionTypeAvp.
	 */
	public ParticipantActionTypeEnum getEnumParticipantActionType( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumParticipantActionType()");
			}
			return ParticipantActionTypeEnum.getContainerObj(stackObj.getEnumParticipantActionType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumParticipantActionType",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from NumberOfDiversions AVPs.
	 */
	public long getNumberOfDiversions( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getNumberOfDiversions()");
			}
			return stackObj.getNumberOfDiversions();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getNumberOfDiversions",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from NumberOfParticipants AVPs.
	 */
	public long getNumberOfParticipants( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getNumberOfParticipants()");
			}
			return stackObj.getNumberOfParticipants();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getNumberOfParticipants",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from ParticipantActionType AVPs.
	 */
	public int getParticipantActionType( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getParticipantActionType()");
			}
			return stackObj.getParticipantActionType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getParticipantActionType",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from CUGInformation AVPs.
	 */
	public byte[] getRawCUGInformation( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawCUGInformation()");
			}
			return stackObj.getRawCUGInformation();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawCUGInformation",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from ServiceId AVPs.
	 */
	public java.lang.String getServiceId( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getServiceId()");
			}
			return stackObj.getServiceId();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getServiceId",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from ServiceMode AVPs.
	 */
	public long getServiceMode( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getServiceMode()");
			}
			return stackObj.getServiceMode();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getServiceMode",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from ServiceType AVPs.
	 */
	public long getServiceType( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getServiceType()");
			}
			return stackObj.getServiceType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getServiceType",e);
		}
	}

}
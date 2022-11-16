package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.ParticipantAccessPriorityEnum;
import com.baypackets.ase.ra.diameter.gy.enums.UserParticipatingTypeEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpParticipantGroup;
import com.traffix.openblox.diameter.gy.generated.avp.AvpServiceParameterInfo;

public class ServiceParameterInfoAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(ServiceParameterInfoAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpServiceParameterInfo stackObj;

	public ServiceParameterInfoAvp(AvpServiceParameterInfo stkObj){
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
	 *  Adding CalledPartyAddress AVP of type UTF8String to the message.
	 */
	public  ServiceParameterTypeAvp addServiceParameterType(long value, boolean mFlag) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCalledPartyAddress()");
			}
			return new ServiceParameterTypeAvp(stackObj.addServiceParameterType(value,mFlag));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCalledPartyAddress",e);
		}
	}
	
	public  ServiceParameterValueAvp addServiceParameterValue(String value, boolean mFlag) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCalledPartyAddress()");
			}
			return new ServiceParameterValueAvp(stackObj.addServiceParameterValue(value,mFlag));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCalledPartyAddress",e);
		}
	}
	
	public  ServiceParameterValueAvp addServiceParameterValue(byte[] value, boolean mFlag) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCalledPartyAddress()");
			}
			return new ServiceParameterValueAvp(stackObj.addServiceParameterValue(value,mFlag));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCalledPartyAddress",e);
		}
	}
	
	public byte[] getRawServiceParameterValue() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCalledPartyAddress()");
			}
			return stackObj.getRawServiceParameterValue();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCalledPartyAddress",e);
		}
	}
	
	public long getServiceParameterType() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCalledPartyAddress()");
			}
			return stackObj.getServiceParameterType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCalledPartyAddress",e);
		}
	}
	
	public String getServiceParameterValue() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCalledPartyAddress()");
			}
			return stackObj.getServiceParameterValue();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCalledPartyAddress",e);
		}
	}
	
	
//	/**
//	 *  Adding ParticipantAccessPriority AVP of type Enumerated to the message.
//	 */
//	public ParticipantAccessPriorityAvp addParticipantAccessPriority(ParticipantAccessPriorityEnum value) throws GyResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addParticipantAccessPriority()");
//			}
//			return new ParticipantAccessPriorityAvp(stackObj.addParticipantAccessPriority(ParticipantAccessPriorityEnum.getStackObj(value)));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addParticipantAccessPriority",e);
//		}
//	}
//
//	/**
//	 *  Adding UserParticipatingType AVP of type Enumerated to the message.
//	 */
//	public UserParticipatingTypeAvp addUserParticipatingType(UserParticipatingTypeEnum value) throws GyResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addUserParticipatingType()");
//			}
//			return new UserParticipatingTypeAvp(stackObj.addUserParticipatingType(UserParticipatingTypeEnum.getStackObj(value)));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addUserParticipatingType",e);
//		}
//	}
//
//	/**
//	 *  Retrieving a single UTF8String value from CalledPartyAddress AVPs.
//	 */
//	public java.lang.String getCalledPartyAddress() throws GyResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getCalledPartyAddress()");
//			}
//			return stackObj.getCalledPartyAddress();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getCalledPartyAddress",e);
//		}
//	}
//
//	/**
//	 *  Retrieves Enum of ParticipantAccessPriority type.
//	 */
//	public ParticipantAccessPriorityEnum getEnumParticipantAccessPriority() throws GyResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getEnumParticipantAccessPriority()");
//			}
//			return ParticipantAccessPriorityEnum.getContainerObj(stackObj.getEnumParticipantAccessPriority());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getEnumParticipantAccessPriority",e);
//		}
//	}
//
//	/**
//	 *  Retrieves Enum of UserParticipatingType.
//	 */
//	public UserParticipatingTypeEnum getEnumUserParticipatingType() throws GyResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getEnumUserParticipatingType()");
//			}
//			return UserParticipatingTypeEnum.getContainerObj(stackObj.getEnumUserParticipatingType());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getEnumUserParticipatingType",e);
//		}
//	}
//
//	/**
//	 *  Retrieving a single Enumerated value from ParticipantAccessPriority AVPs.
//	 */
//	public int getParticipantAccessPriority() throws GyResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getParticipantAccessPriority()");
//			}
//			return stackObj.getParticipantAccessPriority();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getParticipantAccessPriority",e);
//		}
//	}
//
//	/**
//	 *  Retrieving a single Enumerated value from UserParticipatingType AVPs.
//	 */
//	public int getUserParticipatingType() throws GyResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getUserParticipatingType()");
//			}
//			return stackObj.getUserParticipatingType();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getUserParticipatingType",e);
//		}
//	}


}

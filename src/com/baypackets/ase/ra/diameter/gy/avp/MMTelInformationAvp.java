package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.SubscriberRoleEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpMMTelInformation;
import com.traffix.openblox.diameter.gy.generated.avp.AvpSupplementaryService;
import com.traffix.openblox.diameter.gy.generated.enums.EnumSubscriberRole;

public class MMTelInformationAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(MMTelInformationAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpMMTelInformation stackObj;

	public MMTelInformationAvp(AvpMMTelInformation stkObj){
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
	 *  Adding SupplementaryService AVP of type Grouped to the message.
	 */
	public SupplementaryServiceAvp addGroupedSupplementaryService() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedSupplementaryService()");
			}
			return new SupplementaryServiceAvp(stackObj.addGroupedSupplementaryService());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedSupplementaryService",e);
		}
	}

	/**
	 *  Adding SubscriberRole AVP of type Enumerated to the message.
	 */
//	public SubscriberRoleAvp addSubscriberRole(EnumSubscriberRole value) throws GyResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addSubscriberRole()");
//			}
//			return new SubscriberRoleAvp(stackObj.addSubscriberRole(value));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addSubscriberRole",e);
//		}
//	}

	/**
	 *  Retrieves Enum of SubscriberRole type.
	 */
//	public SubscriberRoleEnum getEnumSubscriberRole() throws GyResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getEnumSubscriberRole()");
//			}
//			return SubscriberRoleEnum.getContainerObj(stackObj.getEnumSubscriberRole());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getEnumSubscriberRole",e);
//		}
//	}

	/**
	 *  Retrieving multiple Grouped values from SupplementaryService AVPs.
	 */
	public SupplementaryServiceAvp[] getGroupedSupplementaryServices() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedSupplementaryServices()");
			}
			AvpSupplementaryService[] stackAv= stackObj.getGroupedSupplementaryServices();
			SupplementaryServiceAvp[] contAvp= new SupplementaryServiceAvp[stackAv.length];
			for(int i=0;i<stackAv.length;i++){
				contAvp[i]=new SupplementaryServiceAvp(stackAv[i]);
			}
			return contAvp;
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedSupplementaryServices",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from SubscriberRole AVPs.
	 */
//	public int getSubscriberRole() throws GyResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getSubscriberRole()");
//			}
//			return stackObj.getSubscriberRole();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getSubscriberRole",e);
//		}
//	}


}
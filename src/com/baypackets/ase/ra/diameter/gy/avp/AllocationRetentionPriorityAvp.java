package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.PreemptionCapabilityEnum;
import com.baypackets.ase.ra.diameter.gy.enums.PreemptionVulnerabilityEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpAllocationRetentionPriority;
import com.traffix.openblox.diameter.gy.generated.enums.EnumPreemptionCapability;
import com.traffix.openblox.diameter.gy.generated.enums.EnumPreemptionVulnerability;

public class AllocationRetentionPriorityAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(AllocationRetentionPriorityAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpAllocationRetentionPriority stackObj;

	public AllocationRetentionPriorityAvp(AvpAllocationRetentionPriority stkObj){
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
	 *  Adding PreemptionCapability AVP of type Enumerated to the message.
	 */
	public PreemptionCapabilityAvp addPreemptionCapability(EnumPreemptionCapability value, boolean mFlag) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addPreemptionCapability()");
			}
			return new PreemptionCapabilityAvp(stackObj.addPreemptionCapability(value,mFlag));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addPreemptionCapability",e);
		}
	}

	/**
	 *  Adding PreemptionVulnerability AVP of type Enumerated to the message.
	 */
	public PreemptionVulnerabilityAvp addPreemptionVulnerability(EnumPreemptionVulnerability value, boolean mFlag) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addPreemptionVulnerability()");
			}
			return new PreemptionVulnerabilityAvp(stackObj.addPreemptionVulnerability(value,mFlag));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addPreemptionVulnerability",e);
		}
	}

	/**
	 *  Adding PriorityLevel AVP of type Unsigned32 to the message.
	 */
	public PriorityLevelAvp addPriorityLevel(long value, boolean mFlag) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addPriorityLevel()");
			}
			return new PriorityLevelAvp(stackObj.addPriorityLevel(value,mFlag));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addPriorityLevel",e);
		}
	}

	/**
	 *  Gets Enum of PreemptionCapability
	 */
	public PreemptionCapabilityEnum getEnumPreemptionCapability() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumPreemptionCapability()");
			}
			return	PreemptionCapabilityEnum.getContainerObj(stackObj.getEnumPreemptionCapability());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumPreemptionCapability",e);
		}
	}

	/**
	 *  Gets Enum of PreemptionVulnerability
	 */
	public PreemptionVulnerabilityEnum getEnumPreemptionVulnerability() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumPreemPtionVulnerability()");
			}
			return	PreemptionVulnerabilityEnum.getContainerObj(stackObj.getEnumPreemptionVulnerability());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumPreemPtionVulnerability",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from PreemptionCapability AVPs.
	 */
	public int getPreemptionCapability() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getPreemptionCapability()");
			}
			return stackObj.getPreemptionCapability();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getPreemptionCapability",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from PreemptionVulnerability AVPs.
	 */
	public int getPreemptionVulnerability() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getPreemptionVulnerability()");
			}
			return stackObj.getPreemptionVulnerability();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getPreemptionVulnerability",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from PriorityLevel AVPs.
	 */
	public long getPriorityLevel() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getPriorityLevel()");
			}
			return stackObj.getPriorityLevel();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getPriorityLevel",e);
		}
	}


}
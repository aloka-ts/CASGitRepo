package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpAoCInformation;

public class AoCInformationAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(AoCInformationAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpAoCInformation stackObj;

	public AoCInformationAvp(AvpAoCInformation stkObj){
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
	 *  Adding AoCCostInformation AVP of type Grouped to the message.
	 */
	public AoCCostInformationAvp addGroupedAoCCostInformation() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedAoCCostInformation()");
			}
			return new AoCCostInformationAvp(stackObj.addGroupedAoCCostInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedAoCCostInformation",e);
		}
	}

	/**
	 *  Adding AoCSubscriptionInformation AVP of type Grouped to the message.
	 */
	public AoCSubscriptionInformationAvp addGroupedAoCSubscriptionInformation() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedAoCSubscriptionInformation()");
			}
			return new AoCSubscriptionInformationAvp(stackObj.addGroupedAoCSubscriptionInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedAoCSubscriptionInformation",e);
		}
	}

	/**
	 *  Adding TariffInformation AVP of type Grouped to the message.
	 */
	public TariffInformationAvp addGroupedTariffInformation() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedTariffInformation()");
			}
			return new TariffInformationAvp(stackObj.addGroupedTariffInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedTariffInformation",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from AoCCostInformation AVPs.
	 */
	public AoCCostInformationAvp getGroupedAoCCostInformation() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedAoCCostInformation()");
			}
			return new AoCCostInformationAvp(stackObj.getGroupedAoCCostInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedAoCCostInformation",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from AoCSubscriptionInformation AVPs.
	 */
	public AoCSubscriptionInformationAvp getGroupedAoCSubscriptionInformation() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedAoCSubscriptionInformation()");
			}
			return new AoCSubscriptionInformationAvp(stackObj.getGroupedAoCSubscriptionInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedAoCSubscriptionInformation",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from TariffInformation AVPs.
	 */
	public TariffInformationAvp getGroupedTariffInformation() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedTariffInformation()");
			}
			return new TariffInformationAvp(stackObj.getGroupedTariffInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedTariffInformation",e);
		}
	}


}
package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpAoCCostInformation;
import com.traffix.openblox.diameter.gy.generated.avp.AvpIncrementalCost;

public class AoCCostInformationAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(AoCCostInformationAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpAoCCostInformation stackObj;

	public AoCCostInformationAvp(AvpAoCCostInformation stkObj){
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
	 *  Adding CurrencyCode AVP of type Unsigned32 to the message.
	 */
	public CurrencyCodeAvp addCurrencyCode(long value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCurrencyCode()");
			}
			return new CurrencyCodeAvp(stackObj.addCurrencyCode(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCurrencyCode",e);
		}
	}

	/**
	 *  Adding AccumulatedCost AVP of type Grouped to the message.
	 */
	public AccumulatedCostAvp addGroupedAccumulatedCost() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedAccumulatedCost()");
			}
			return new AccumulatedCostAvp(stackObj.addGroupedAccumulatedCost());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedAccumulatedCost",e);
		}
	}

	/**
	 *  Adding IncrementalCost AVP of type Grouped to the message.
	 */
	public IncrementalCostAvp addGroupedIncrementalCost() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedIncrementalCost()");
			}
			return new IncrementalCostAvp(stackObj.addGroupedIncrementalCost());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedIncrementalCost",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from CurrencyCode AVPs.
	 */
	public long getCurrencyCode() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCurrencyCode()");
			}
			return stackObj.getCurrencyCode();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCurrencyCode",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from AccumulatedCost AVPs.
	 */
	public AccumulatedCostAvp getGroupedAccumulatedCost() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedAccumulatedCost()");
			}
			return new AccumulatedCostAvp(stackObj.getGroupedAccumulatedCost());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedAccumulatedCost",e);
		}
	}

	/**
	 *  Retrieving multiple Grouped values from IncrementalCost AVPs.
	 */
	public IncrementalCostAvp[] getGroupedIncrementalCosts() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedIncrementalCosts()");
			}
			AvpIncrementalCost[] stackAv= stackObj.getGroupedIncrementalCosts();
			IncrementalCostAvp[] contAvp= new IncrementalCostAvp[stackAv.length];
			for(int i=0;i<stackAv.length;i++){
				contAvp[i]=new IncrementalCostAvp(stackAv[i]);
			}
			return contAvp;
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedIncrementalCosts",e);
		}
	}
}
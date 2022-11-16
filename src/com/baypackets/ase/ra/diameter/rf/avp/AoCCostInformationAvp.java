package com.baypackets.ase.ra.diameter.rf.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.rf.RfResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.rf.generated.avp.AvpAoCCostInformation;
import com.traffix.openblox.diameter.rf.generated.avp.AvpIncrementalCost;

public class AoCCostInformationAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(AoCCostInformationAvp.class.getName());

	public static final String name = "ErrorReportingHost";
	public static final int code = 294;
	public static final long vendorId = 0L;

	private AvpAoCCostInformation stackObj;

	public AoCCostInformationAvp(AvpAoCCostInformation stkObj){
		super(stkObj);
		this.stackObj=stkObj;
	}

	public int getCode() {
		return stackObj.getCode();
	}

	public FlagRuleEnum getMRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getMRule());
	}

	public String getName() {
		return stackObj.getName();
	}

	public FlagRuleEnum getPRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getPRule());
	}

	public long getVendorId() {
		return stackObj.getVendorId();
	}

	public FlagRuleEnum getVRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getVRule());
	}


	/**
	 * Adding CurrencyCode AVP of type Unsigned32 to the message.
	 * @param value
	 * @return
	 * @throws RfResourceException 
	 */
	public CurrencyCodeAvp addCurrencyCode(long value) throws RfResourceException {
		try {
			return new CurrencyCodeAvp(stackObj.addCurrencyCode(value));
		} catch (ValidationException e) {
			throw new RfResourceException(e);
		}
	}

	/**
	 * Adding AccumulatedCost AVP of type Grouped to the message.
	 * @return
	 * @throws RfResourceException 
	 */
	public AccumulatedCostAvp addGroupedAccumulatedCost() throws RfResourceException {
		try {
			return new AccumulatedCostAvp(stackObj.addGroupedAccumulatedCost());
		} catch (ValidationException e) {
			throw new RfResourceException(e);
		}
	}

	/**
	 * Adding IncrementalCost AVP of type Grouped to the message.
	 * @return
	 * @throws RfResourceException 
	 */
	public IncrementalCostAvp addGroupedIncrementalCost() throws RfResourceException {
		try {
			return new IncrementalCostAvp(stackObj.addGroupedIncrementalCost());
		} catch (ValidationException e) {
			throw new RfResourceException(e);
		}
	}

	/**
	 * Retrieving a single Unsigned32 value from CurrencyCode AVPs.
	 * @return
	 * @throws RfResourceException 
	 */
	public long getCurrencyCode() throws RfResourceException {
		try {
			return stackObj.getCurrencyCode();
		} catch (ValidationException e) {
			throw new RfResourceException(e);
		}
	}

	/**
	 * Retrieving a single Grouped value from AccumulatedCost AVPs.
	 * @return
	 * @throws RfResourceException 
	 */
	public AccumulatedCostAvp getGroupedAccumulatedCost() throws RfResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedAccumulatedCost()");
			}
			return	new AccumulatedCostAvp(stackObj.getGroupedAccumulatedCost());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getGroupedAccumulatedCost ",e);
		}
	}

	/**
	 * Retrieving multiple Grouped values from IncrementalCost AVPs.
	 * @return
	 * @throws RfResourceException 
	 */
	public IncrementalCostAvp[] getGroupedIncrementalCosts() throws RfResourceException {
		try{
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
			throw new RfResourceException("Exception in getGroupedIncrementalCosts ",e);
		}
	}
}
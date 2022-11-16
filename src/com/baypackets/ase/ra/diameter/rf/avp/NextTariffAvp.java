package com.baypackets.ase.ra.diameter.rf.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.rf.RfResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.rf.generated.avp.AvpNextTariff;
import com.traffix.openblox.diameter.rf.generated.avp.AvpRateElement;

public class NextTariffAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(NextTariffAvp.class.getName());
	
	public static final String name = "ErrorReportingHost";
	public static final int code = 294;
	public static final long vendorId = 0L;

	private AvpNextTariff stackObj;

	public NextTariffAvp(AvpNextTariff stkObj){
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
	 * Adding RateElement AVP of type Grouped to the message.
	 * @return
	 * @throws RfResourceException 
	 */
	public RateElementAvp addGroupedRateElement() throws RfResourceException {
		try {
			return new RateElementAvp(stackObj.addGroupedRateElement());
		} catch (ValidationException e) {
			throw new RfResourceException(e);
		}
	}

	/**
	 * Adding ScaleFactor AVP of type Grouped to the message.
	 * @return
	 * @throws RfResourceException 
	 */
	public ScaleFactorAvp addGroupedScaleFactor() throws RfResourceException {
		try {
			return new ScaleFactorAvp(stackObj.addGroupedScaleFactor());
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
	 * Retrieving multiple Grouped values from RateElement AVPs.
	 * @return
	 * @throws RfResourceException 
	 */
	public RateElementAvp[] getGroupedRateElements() throws RfResourceException {
		try{
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedRateElements()");
			}
			AvpRateElement[] stackAv= stackObj.getGroupedRateElements();
			RateElementAvp[] contAvp= new RateElementAvp[stackAv.length];
			for(int i=0;i<stackAv.length;i++){
				contAvp[i]=new RateElementAvp(stackAv[i]);
			}
			return contAvp;
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getGroupedRateElements ",e);
		}
	}

	/**
	 * Retrieving a single Grouped value from ScaleFactor AVPs.
	 * @return
	 * @throws RfResourceException 
	 */
	public ScaleFactorAvp getGroupedScaleFactor() throws RfResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedScaleFactor()");
			}
			return	new ScaleFactorAvp(stackObj.getGroupedScaleFactor());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getGroupedScaleFactor ",e);
		}
	}

}
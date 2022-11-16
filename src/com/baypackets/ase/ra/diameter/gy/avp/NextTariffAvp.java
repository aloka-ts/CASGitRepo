package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpNextTariff;
import com.traffix.openblox.diameter.gy.generated.avp.AvpRateElement;

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
	 * @throws GyResourceException 
	 */
	public CurrencyCodeAvp addCurrencyCode(long value) throws GyResourceException {
		try {
			return new CurrencyCodeAvp(stackObj.addCurrencyCode(value));
		} catch (ValidationException e) {
			throw new GyResourceException(e);
		}
	}

	/**
	 * Adding RateElement AVP of type Grouped to the message.
	 * @return
	 * @throws GyResourceException 
	 */
	public RateElementAvp addGroupedRateElement() throws GyResourceException {
		try {
			return new RateElementAvp(stackObj.addGroupedRateElement());
		} catch (ValidationException e) {
			throw new GyResourceException(e);
		}
	}

	/**
	 * Adding ScaleFactor AVP of type Grouped to the message.
	 * @return
	 * @throws GyResourceException 
	 */
	public ScaleFactorAvp addGroupedScaleFactor() throws GyResourceException {
		try {
			return new ScaleFactorAvp(stackObj.addGroupedScaleFactor());
		} catch (ValidationException e) {
			throw new GyResourceException(e);
		}
	}

	/**
	 * Retrieving a single Unsigned32 value from CurrencyCode AVPs.
	 * @return
	 * @throws GyResourceException 
	 */
	public long getCurrencyCode() throws GyResourceException {
		try {
			return stackObj.getCurrencyCode();
		} catch (ValidationException e) {
			throw new GyResourceException(e);
		}
	}

	/**
	 * Retrieving multiple Grouped values from RateElement AVPs.
	 * @return
	 * @throws GyResourceException 
	 */
	public RateElementAvp[] getGroupedRateElements() throws GyResourceException {
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
			throw new GyResourceException("Exception in getGroupedRateElements ",e);
		}
	}

	/**
	 * Retrieving a single Grouped value from ScaleFactor AVPs.
	 * @return
	 * @throws GyResourceException 
	 */
	public ScaleFactorAvp getGroupedScaleFactor() throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedScaleFactor()");
			}
			return	new ScaleFactorAvp(stackObj.getGroupedScaleFactor());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedScaleFactor ",e);
		}
	}

}
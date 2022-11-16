package com.baypackets.ase.ra.diameter.rf.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterIdentity;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.rf.RfResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.rf.generated.avp.AvpTariffInformation;

public class TariffInformationAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(TariffInformationAvp.class.getName());
	
	public static final String name = "ErrorReportingHost";
	public static final int code = 294;
	public static final long vendorId = 0L;

	private AvpTariffInformation stackObj;

	public TariffInformationAvp(AvpTariffInformation stkObj){
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
	 * Adding CurrentTariff AVP of type Grouped to the message.
	 * @return
	 * @throws RfResourceException 
	 */
	public CurrentTariffAvp addGroupedCurrentTariff() throws RfResourceException {
		try {
			return new CurrentTariffAvp(stackObj.addGroupedCurrentTariff());
		} catch (ValidationException e) {
			throw new RfResourceException(e);
		}
	}

	/**
	 * Adding NextTariff AVP of type Grouped to the message.
	 * @return
	 * @throws RfResourceException 
	 */
	public NextTariffAvp addGroupedNextTariff() throws RfResourceException {
		try {
			return new NextTariffAvp(stackObj.addGroupedNextTariff());
		} catch (ValidationException e) {
			throw new RfResourceException(e);
		}
	}

	/**
	 * Adding TariffTimeChange AVP of type Time to the message.
	 * @param value
	 * @return
	 * @throws RfResourceException 
	 */
	public TariffTimeChangeAvp addTariffTimeChange(java.util.Date value) throws RfResourceException {
		try {
			return new TariffTimeChangeAvp(stackObj.addTariffTimeChange(value));
		} catch (ValidationException e) {
			throw new RfResourceException(e);
		}
	}

	/**
	 * Retrieving a single Grouped value from CurrentTariff AVPs.
	 * @return
	 * @throws RfResourceException 
	 */
	public CurrentTariffAvp getGroupedCurrentTariff() throws RfResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedCurrentTariff()");
			}
			return	new CurrentTariffAvp(stackObj.getGroupedCurrentTariff());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getGroupedCurrentTariff ",e);
		}
	}

	/**
	 * Retrieving a single Grouped value from NextTariff AVPs.
	 * @return
	 * @throws RfResourceException 
	 */
	public NextTariffAvp getGroupedNextTariff() throws RfResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedNextTariff()");
			}
			return	new NextTariffAvp(stackObj.getGroupedNextTariff());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getGroupedNextTariff ",e);
		}
	}

	/**
	 * Retrieving a single Time value from TariffTimeChange AVPs.
	 * @return
	 * @throws RfResourceException 
	 */
	public java.util.Date getTariffTimeChange() throws RfResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getTariffTimeChange()");
			}
			return	stackObj.getTariffTimeChange();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getTariffTimeChange ",e);
		}
	}

}
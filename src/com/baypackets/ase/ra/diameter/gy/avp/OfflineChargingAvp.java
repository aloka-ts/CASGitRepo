package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.EnvelopeReportingEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpOfflineCharging;
import com.traffix.openblox.diameter.gy.generated.avp.AvpMultipleServicesCreditControl;

public class OfflineChargingAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(OfflineChargingAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpOfflineCharging stackObj;

	public OfflineChargingAvp(AvpOfflineCharging stkObj){
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
	 *  Adding EnvelopeReporting AVP of type Enumerated to the message.
	 */
	public EnvelopeReportingAvp addEnvelopeReporting(EnvelopeReportingEnum value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addEnvelopeReporting()");
			}
			return new EnvelopeReportingAvp(stackObj.addEnvelopeReporting(EnvelopeReportingEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addEnvelopeReporting",e);
		}
	}

	/**
	 *  Adding MultipleServicesCreditControl AVP of type Grouped to the message.
	 */
	public MultipleServicesCreditControlAvp addGroupedMultipleServicesCreditControl() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedMultipleServicesCreditControl()");
			}
			return new MultipleServicesCreditControlAvp(stackObj.addGroupedMultipleServicesCreditControl());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedMultipleServicesCreditControl",e);
		}
	}

	/**
	 *  Adding TimeQuotaMechanism AVP of type Grouped to the message.
	 */
	public TimeQuotaMechanismAvp addGroupedTimeQuotaMechanism() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedTimeQuotaMechanism()");
			}
			return new TimeQuotaMechanismAvp(stackObj.addGroupedTimeQuotaMechanism());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedTimeQuotaMechanism",e);
		}
	}

	/**
	 *  Adding QuotaConsumptionTime AVP of type Unsigned32 to the message.
	 */
	public QuotaConsumptionTimeAvp addQuotaConsumptionTime(long value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addQuotaConsumptionTime()");
			}
			return new QuotaConsumptionTimeAvp(stackObj.addQuotaConsumptionTime(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addQuotaConsumptionTime",e);
		}
	}

	/**
	 *  Retrieves Enum of EnvelopeReporting type.
	 */
	public EnvelopeReportingEnum getEnumEnvelopeReporting() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumEnvelopeReporting()");
			}
			return EnvelopeReportingEnum.getContainerObj(stackObj.getEnumEnvelopeReporting());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumEnvelopeReporting",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from EnvelopeReporting AVPs.
	 */
	public int getEnvelopeReporting() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnvelopeReporting()");
			}
			return stackObj.getEnvelopeReporting();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnvelopeReporting",e);
		}
	}

	/**
	 *  Retrieving multiple Grouped values from MultipleServicesCreditControl AVPs.
	 */
	public MultipleServicesCreditControlAvp[] getGroupedMultipleServicesCreditControls() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedMultipleServicesCreditControls()");
			}
			AvpMultipleServicesCreditControl[] stackAv= stackObj.getGroupedMultipleServicesCreditControls();
			MultipleServicesCreditControlAvp[] contAvp= new MultipleServicesCreditControlAvp[stackAv.length];
			for(int i=0;i<stackAv.length;i++){
				contAvp[i]=new MultipleServicesCreditControlAvp(stackAv[i]);
			}
			return contAvp;
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedMultipleServicesCreditControls",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from TimeQuotaMechanism AVPs.
	 */
	public TimeQuotaMechanismAvp getGroupedTimeQuotaMechanism() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedTimeQuotaMechanism()");
			}
			return new TimeQuotaMechanismAvp(stackObj.getGroupedTimeQuotaMechanism());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedTimeQuotaMechanism",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from QuotaConsumptionTime AVPs.
	 */
	public long getQuotaConsumptionTime() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getQuotaConsumptionTime()");
			}
			return stackObj.getQuotaConsumptionTime();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getQuotaConsumptionTime",e);
		}
	}

}
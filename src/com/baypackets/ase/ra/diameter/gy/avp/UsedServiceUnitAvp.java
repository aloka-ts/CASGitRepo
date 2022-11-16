package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.ReportingReasonEnum;
import com.baypackets.ase.ra.diameter.gy.enums.TariffChangeUsageEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpUsedServiceUnit;

public class UsedServiceUnitAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(UsedServiceUnitAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpUsedServiceUnit stackObj;

	public UsedServiceUnitAvp(AvpUsedServiceUnit stkObj){
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
	 *  Adding CCInputOctets AVP of type Unsigned64 to the message.
	 */
	public CCInputOctetsAvp addCCInputOctets(long value, boolean mFlag) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCCInputOctets()");
			}
			return new CCInputOctetsAvp(stackObj.addCCInputOctets(value,mFlag));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCCInputOctets",e);
		}
	}

	/**
	 *  Adding CCOutputOctets AVP of type Unsigned64 to the message.
	 */
	public CCOutputOctetsAvp addCCOutputOctets(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCCOutputOctets()");
			}
			return new CCOutputOctetsAvp(stackObj.addCCOutputOctets(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCCOutputOctets",e);
		}
	}

	/**
	 *  Adding CCServiceSpecificUnits AVP of type Unsigned64 to the message.
	 */
	public CCServiceSpecificUnitsAvp addCCServiceSpecificUnits(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCCServiceSpecificUnits()");
			}
			return new CCServiceSpecificUnitsAvp(stackObj.addCCServiceSpecificUnits(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCCServiceSpecificUnits",e);
		}
	}

	/**
	 *  Adding CCTime AVP of type Unsigned32 to the message.
	 */
	public CCTimeAvp addCCTime(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCCTime()");
			}
			return new CCTimeAvp(stackObj.addCCTime(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCCTime",e);
		}
	}

	/**
	 *  Adding CCTotalOctets AVP of type Unsigned64 to the message.
	 */
	public CCTotalOctetsAvp addCCTotalOctets(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCCTotalOctets()");
			}
			return new CCTotalOctetsAvp(stackObj.addCCTotalOctets(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCCTotalOctets",e);
		}
	}

	/**
	 *  Adding EventChargingTimeStamp AVP of type Time to the message.
	 */
	public EventChargingTimeStampAvp addEventChargingTimeStamp(java.util.Date value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addEventChargingTimeStamp()");
			}
			return new EventChargingTimeStampAvp(stackObj.addEventChargingTimeStamp(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addEventChargingTimeStamp",e);
		}
	}

	/**
	 *  Adding CCMoney AVP of type Grouped to the message.
	 */
//	public CCMoneyAvp addGroupedCCMoney(boolean mFlag) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addGroupedCCMoney()");
//			}
//			return new CCMoneyAvp(stackObj.addGroupedCCMoney(mFlag));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addGroupedCCMoney",e);
//		}
//	}

	/**
	 *  Adding ReportingReason AVP of type Enumerated to the message.
	 */
	public ReportingReasonAvp addReportingReason(ReportingReasonEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addReportingReason()");
			}
			return new ReportingReasonAvp(stackObj.addReportingReason(ReportingReasonEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addReportingReason",e);
		}
	}

	/**
	 *  Adding TariffChangeUsage AVP of type Enumerated to the message.
	 */
	public TariffChangeUsageAvp addTariffChangeUsage(TariffChangeUsageEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addTariffChangeUsage()");
			}
			return new TariffChangeUsageAvp(stackObj.addTariffChangeUsage(TariffChangeUsageEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addTariffChangeUsage",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned64 value from CCInputOctets AVPs.
	 */
	public long getCCInputOctets( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCCInputOctets()");
			}
			return stackObj.getCCInputOctets();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCCInputOctets",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned64 value from CCOutputOctets AVPs.
	 */
	public long getCCOutputOctets( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCCOutputOctets()");
			}
			return stackObj.getCCOutputOctets();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCCOutputOctets",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned64 value from CCServiceSpecificUnits AVPs.
	 */
	public long getCCServiceSpecificUnits( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCCServiceSpecificUnits()");
			}
			return stackObj.getCCServiceSpecificUnits();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCCServiceSpecificUnits",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from CCTime AVPs.
	 */
	public long getCCTime( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCCTime()");
			}
			return stackObj.getCCTime();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCCTime",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned64 value from CCTotalOctets AVPs.
	 */
	public long getCCTotalOctets( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCCTotalOctets()");
			}
			return stackObj.getCCTotalOctets();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCCTotalOctets",e);
		}
	}

	/**
	 *  This method returns the enum value corrosponding to ReportingReasonAvp.
	 */
	public ReportingReasonEnum getEnumReportingReason( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumReportingReason()");
			}
			return ReportingReasonEnum.getContainerObj(stackObj.getEnumReportingReason());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumReportingReason",e);
		}
	}

	/**
	 *  This method returns the enum value corrosponding to TariffChangeUsageAvp.
	 */
	public TariffChangeUsageEnum getEnumTariffChangeUsage( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumTariffChangeUsage()");
			}
			return TariffChangeUsageEnum.getContainerObj(stackObj.getEnumTariffChangeUsage());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumTariffChangeUsage",e);
		}
	}

	/**
	 *  Retrieving multiple Time values from EventChargingTimeStamp AVPs.
	 */
	public java.util.Date[] getEventChargingTimeStamps( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEventChargingTimeStamps()");
			}
			return stackObj.getEventChargingTimeStamps();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEventChargingTimeStamps",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from CCMoney AVPs.
	 */
//	public CCMoneyAvp getGroupedCCMoney( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getGroupedCCMoney()");
//			}
//			return new CCMoneyAvp(stackObj.getGroupedCCMoney());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getGroupedCCMoney",e);
//		}
//	}

	/**
	 *  Retrieving a single Enumerated value from ReportingReason AVPs.
	 */
	public int getReportingReason( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getReportingReason()");
			}
			return stackObj.getReportingReason();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getReportingReason",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from TariffChangeUsage AVPs.
	 */
	public int getTariffChangeUsage( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getTariffChangeUsage()");
			}
			return stackObj.getTariffChangeUsage();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getTariffChangeUsage",e);
		}
	}

}
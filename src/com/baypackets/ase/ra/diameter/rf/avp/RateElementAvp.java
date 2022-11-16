package com.baypackets.ase.ra.diameter.rf.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.rf.RfResourceException;
import com.baypackets.ase.ra.diameter.rf.enums.CCUnitTypeEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.rf.generated.avp.AvpRateElement;
import com.traffix.openblox.diameter.rf.generated.enums.EnumCCUnitType;

public class RateElementAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(RateElementAvp.class.getName());
	public static final String name = "ErrorReportingHost";
	public static final int code = 294;
	public static final long vendorId = 0L;

	private AvpRateElement stackObj;

	public RateElementAvp(AvpRateElement stkObj){
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
	 * Adding CCUnitType AVP of type Enumerated to the message.
	 * @param value
	 * @return
	 */
	public CCUnitTypeAvp addCCUnitType(CCUnitTypeEnum value) throws RfResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCCUnitType()");
			}
			EnumCCUnitType stackEnum = CCUnitTypeEnum.getStackObj(value);
			return new CCUnitTypeAvp(stackObj.addCCUnitType(stackEnum));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addCCUnitType ",e);
		}
	}
	/**
	 * Adding UnitCost AVP of type Grouped to the message.
	 * @return
	 */
	public UnitCostAvp addGroupedUnitCost() throws RfResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedUnitCost()");
			}
			return new UnitCostAvp(stackObj.addGroupedUnitCost());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addGroupedUnitCost ",e);
		}
	}

	/**
	 * Adding UnitValue AVP of type Grouped to the message.
	 * @return
	 */
	public UnitValueAvp addGroupedUnitValue() throws RfResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedUnitValue()");
			}
			return new UnitValueAvp(stackObj.addGroupedUnitValue());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addGroupedUnitValue ",e);
		}
	}

	/**
	 * Adding UnitQuotaThreshold AVP of type Unsigned32 to the message.
	 * @param value
	 * @return
	 */
	public UnitQuotaThresholdAvp addUnitQuotaThreshold(long value) throws RfResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addUnitQuotaThreshold()");
			}
			return new UnitQuotaThresholdAvp(stackObj.addUnitQuotaThreshold(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addUnitQuotaThreshold ",e);
		}
	}

	/**
	 * Retrieving a single Enumerated value from CCUnitType AVPs.
	 * @return
	 */
	public int getCCUnitType() throws RfResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCCUnitType()");
			}
			return stackObj.getCCUnitType();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getCCUnitType",e);
		}
	}

	public CCUnitTypeEnum getEnumCCUnitType() throws RfResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumCCUnitType()");
			}
			return CCUnitTypeEnum.getContainerObj(stackObj.getEnumCCUnitType());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getEnumCCUnitType",e);
		}
	}

	/**
	 * Retrieving a single Grouped value from UnitCost AVPs.
	 * @return
	 */
	public UnitCostAvp getGroupedUnitCost() throws RfResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedUnitCost()");
			}
			return new UnitCostAvp(stackObj.getGroupedUnitCost());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getGroupedUnitCost",e);
		}
	}

	/**
	 * Retrieving a single Grouped value from UnitValue AVPs.
	 * @return
	 */
	public UnitValueAvp getGroupedUnitValue() throws RfResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedUnitValue()");
			}
			return new UnitValueAvp(stackObj.getGroupedUnitValue());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getGroupedUnitValue",e);
		}
	}

	/**
	 * Retrieving a single Unsigned32 value from UnitQuotaThreshold AVPs.
	 * @return
	 * @throws RfResourceException 
	 */
	public long getUnitQuotaThreshold() throws RfResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getUnitQuotaThreshold()");
			}
			return stackObj.getUnitQuotaThreshold();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getUnitQuotaThreshold",e);
		}
	}


}
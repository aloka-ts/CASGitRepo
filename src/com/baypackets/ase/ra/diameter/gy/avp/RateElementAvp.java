package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.CCUnitTypeEnum;
import com.baypackets.ase.ra.diameter.gy.enums.ReasonCodeEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpRateElement;

public class RateElementAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(RateElementAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpRateElement stackObj;

	public RateElementAvp(AvpRateElement stkObj){
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
	 *  Adding CCUnitType AVP of type Enumerated to the message.
	 */
	public CCUnitTypeAvp addCCUnitType(CCUnitTypeEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addCCUnitType()");
			}
			return new CCUnitTypeAvp(stackObj.addCCUnitType(CCUnitTypeEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addCCUnitType",e);
		}
	}

	/**
	 *  Adding UnitCost AVP of type Grouped to the message.
	 */
	public UnitCostAvp addGroupedUnitCost( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedUnitCost()");
			}
			return new UnitCostAvp(stackObj.addGroupedUnitCost());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedUnitCost",e);
		}
	}

	/**
	 *  Adding UnitValue AVP of type Grouped to the message.
	 */
	public UnitValueAvp addGroupedUnitValue( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedUnitValue()");
			}
			return new UnitValueAvp(stackObj.addGroupedUnitValue());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedUnitValue",e);
		}
	}

	/**
	 *  Adding ReasonCode AVP of type Enumerated to the message.
	 */
	public ReasonCodeAvp addReasonCode(ReasonCodeEnum value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addReasonCode()");
			}
			return new ReasonCodeAvp(stackObj.addReasonCode(ReasonCodeEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addReasonCode",e);
		}
	}

	/**
	 *  Adding UnitQuotaThreshold AVP of type Unsigned32 to the message.
	 */
	public UnitQuotaThresholdAvp addUnitQuotaThreshold(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addUnitQuotaThreshold()");
			}
			return new UnitQuotaThresholdAvp(stackObj.addUnitQuotaThreshold(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addUnitQuotaThreshold",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from CCUnitType AVPs.
	 */
	public int getCCUnitType( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getCCUnitType()");
			}
			return stackObj.getCCUnitType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getCCUnitType",e);
		}
	}

	/**
	 *  This method returns the enum value corrosponding to CCUnitTypeAvp.
	 */
	public CCUnitTypeEnum getEnumCCUnitType( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumCCUnitType()");
			}
			return CCUnitTypeEnum.getContainerObj(stackObj.getEnumCCUnitType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumCCUnitType",e);
		}
	}

	/**
	 *  This method returns the enum value corrosponding to ReasonCodeAvp.
	 */
	public ReasonCodeEnum getEnumReasonCode( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumReasonCode()");
			}
			return ReasonCodeEnum.getContainerObj(stackObj.getEnumReasonCode());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumReasonCode",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from UnitCost AVPs.
	 */
	public UnitCostAvp getGroupedUnitCost( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedUnitCost()");
			}
			return new UnitCostAvp(stackObj.getGroupedUnitCost());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedUnitCost",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from UnitValue AVPs.
	 */
	public UnitValueAvp getGroupedUnitValue( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedUnitValue()");
			}
			return new UnitValueAvp(stackObj.getGroupedUnitValue());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedUnitValue",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from ReasonCode AVPs.
	 */
	public int getReasonCode( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getReasonCode()");
			}
			return stackObj.getReasonCode();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getReasonCode",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from UnitQuotaThreshold AVPs.
	 */
	public long getUnitQuotaThreshold( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getUnitQuotaThreshold()");
			}
			return stackObj.getUnitQuotaThreshold();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getUnitQuotaThreshold",e);
		}
	}

}
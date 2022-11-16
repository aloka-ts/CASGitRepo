package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpGrantedServiceUnit;

public class GrantedServiceUnitAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(GrantedServiceUnitAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpGrantedServiceUnit stackObj;

	public GrantedServiceUnitAvp(AvpGrantedServiceUnit stkObj){
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
	 *  Adding CCMoney AVP of type Grouped to the message.
	 */
	public CCMoneyAvp addGroupedCCMoney(boolean mFlag) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedCCMoney()");
			}
			return new CCMoneyAvp(stackObj.addGroupedCCMoney(mFlag));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedCCMoney",e);
		}
	}

	/**
	 *  Adding TariffTimeChange AVP of type Time to the message.
	 */
	public TariffTimeChangeAvp addTariffTimeChange(java.util.Date value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addTariffTimeChange()");
			}
			return new TariffTimeChangeAvp(stackObj.addTariffTimeChange(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addTariffTimeChange",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned64 value from CCInputOctets AVPs.
	 */
	public long getCCInputOctets() throws GyResourceException { 

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
	public long getCCOutputOctets() throws GyResourceException { 

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
	public long getCCServiceSpecificUnits() throws GyResourceException { 

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
	public long getCCTime() throws GyResourceException { 

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
	public long getCCTotalOctets() throws GyResourceException { 

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
	 *  Retrieving a single Grouped value from CCMoney AVPs.
	 */
	public CCMoneyAvp getGroupedCCMoney() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedCCMoney()");
			}
			return new CCMoneyAvp(stackObj.getGroupedCCMoney());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedCCMoney",e);
		}
	}

	/**
	 *  Retrieving a single Time value from TariffTimeChange AVPs.
	 */
	public java.util.Date getTariffTimeChange() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getTariffTimeChange()");
			}
			return stackObj.getTariffTimeChange();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getTariffTimeChange",e);
		}
	}


}
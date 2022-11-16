package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.AoCFormatEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpAoCSubscriptionInformation;
import com.traffix.openblox.diameter.gy.generated.avp.AvpAoCService;
import com.traffix.openblox.diameter.gy.generated.enums.EnumAoCFormat;

public class AoCSubscriptionInformationAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(AoCSubscriptionInformationAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpAoCSubscriptionInformation stackObj;

	public AoCSubscriptionInformationAvp(AvpAoCSubscriptionInformation stkObj){
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
	 *  Adding AoCFormat AVP of type Enumerated to the message.
	 */
	public AoCFormatAvp addAoCFormat(EnumAoCFormat value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAoCFormat()");
			}
			return new AoCFormatAvp(stackObj.addAoCFormat(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addAoCFormat",e);
		}
	}

	/**
	 *  Adding AoCService AVP of type Grouped to the message.
	 */
	public AoCServiceAvp addGroupedAoCService() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedAoCService()");
			}
			return new AoCServiceAvp(stackObj.addGroupedAoCService());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedAoCService",e);
		}
	}

	/**
	 *  Adding PreferredAoCCurrency AVP of type Unsigned32 to the message.
	 */
	public PreferredAoCCurrencyAvp addPreferredAoCCurrency(long value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addPreferredAoCCurrency()");
			}
			return new PreferredAoCCurrencyAvp(stackObj.addPreferredAoCCurrency(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addPreferredAoCCurrency",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from AoCFormat AVPs.
	 */
	public int getAoCFormat() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAoCFormat()");
			}
			return stackObj.getAoCFormat();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getAoCFormat",e);
		}
	}

	/**
	 *  Gets Enum of AoCFormat type.
	 */
	public AoCFormatEnum getEnumAoCFormat() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumAoCFormat()");
			}
			return AoCFormatEnum.getContainerObj(stackObj.getEnumAoCFormat());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumAoCFormat",e);
		}
	}

	/**
	 *  Retrieving multiple Grouped values from AoCService AVPs
	 */
	public AoCServiceAvp[] getGroupedAoCServices() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedAoCServices()");
			}
			AvpAoCService[] stackAv= stackObj.getGroupedAoCServices();
			AoCServiceAvp[] contAvp= new AoCServiceAvp[stackAv.length];
			for(int i=0;i<stackAv.length;i++){
				contAvp[i]=new AoCServiceAvp(stackAv[i]);
			}
			return contAvp;
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedAoCServices",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from PreferredAoCCurrency AVPs.
	 */
	public long getPreferredAoCCurrency() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getPreferredAoCCurrency()");
			}
			return stackObj.getPreferredAoCCurrency();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getPreferredAoCCurrency",e);
		}
	}

}
package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpWLANRadioContainer;

public class WLANRadioContainerAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(WLANRadioContainerAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpWLANRadioContainer stackObj;

	public WLANRadioContainerAvp(AvpWLANRadioContainer stkObj){
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
	 *  Adding LocationType AVP of type Grouped to the message.
	 */
	public LocationTypeAvp addGroupedLocationType( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedLocationType()");
			}
			return new LocationTypeAvp(stackObj.addGroupedLocationType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedLocationType",e);
		}
	}

	/**
	 *  Adding LocationInformation AVP of type UTF8String to the message.
	 */
	public LocationInformationAvp addLocationInformation(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addLocationInformation()");
			}
			return new LocationInformationAvp(stackObj.addLocationInformation(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addLocationInformation",e);
		}
	}

	/**
	 *  Adding OperatorName AVP of type OctetString to the message.
	 */
	public OperatorNameAvp addOperatorName(byte[] value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addOperatorName()");
			}
			return new OperatorNameAvp(stackObj.addOperatorName(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addOperatorName",e);
		}
	}

	/**
	 *  Adding OperatorName AVP of type OctetString to the message.
	 */
	public OperatorNameAvp addOperatorName(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addOperatorName()");
			}
			return new OperatorNameAvp(stackObj.addOperatorName(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addOperatorName",e);
		}
	}

	/**
	 *  Adding WLANTechnology AVP of type Unsigned32 to the message.
	 */
	public WLANTechnologyAvp addWLANTechnology(long value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addWLANTechnology()");
			}
			return new WLANTechnologyAvp(stackObj.addWLANTechnology(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addWLANTechnology",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from LocationType AVPs.
	 */
	public LocationTypeAvp getGroupedLocationType( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedLocationType()");
			}
			return new LocationTypeAvp(stackObj.getGroupedLocationType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedLocationType",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from LocationInformation AVPs.
	 */
	public java.lang.String getLocationInformation( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getLocationInformation()");
			}
			return stackObj.getLocationInformation();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getLocationInformation",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from OperatorName AVPs.
	 */
	public java.lang.String getOperatorName( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getOperatorName()");
			}
			return stackObj.getOperatorName();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getOperatorName",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from OperatorName AVPs.
	 */
	public byte[] getRawOperatorName( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawOperatorName()");
			}
			return stackObj.getRawOperatorName();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawOperatorName",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from WLANTechnology AVPs.
	 */
	public long getWLANTechnology( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getWLANTechnology()");
			}
			return stackObj.getWLANTechnology();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getWLANTechnology",e);
		}
	}

}
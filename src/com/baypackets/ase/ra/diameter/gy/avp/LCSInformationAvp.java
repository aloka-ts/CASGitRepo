package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpLCSInformation;

public class LCSInformationAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(LCSInformationAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpLCSInformation stackObj;

	public LCSInformationAvp(AvpLCSInformation stkObj){
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
	 *  Adding 3GPPIMSI AVP of type UTF8String to the message.
	 */
//	public IMSI3GPPAvp add3GPPIMSI(java.lang.String value) throws GyResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside add3GPPIMSI()");
//			}
//			return new IMSI3GPPAvp(stackObj.add3GPPIMSI(value));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in add3GPPIMSI",e);
//		}
//	}

	/**
	 *  Adding LCSClientID AVP of type Grouped to the message.
	 */
	public LCSClientIDAvp addGroupedLCSClientID() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedLCSClientID()");
			}
			return new LCSClientIDAvp(stackObj.addGroupedLCSClientID());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedLCSClientID",e);
		}
	}

	/**
	 *  Adding LocationType AVP of type Grouped to the message.
	 */
	public LocationTypeAvp addGroupedLocationType() throws GyResourceException { 

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
	 *  Adding LocationEstimate AVP of type UTF8String to the message.
	 */
	public LocationEstimateAvp addLocationEstimate(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addLocationEstimate()");
			}
			return new LocationEstimateAvp(stackObj.addLocationEstimate(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addLocationEstimate",e);
		}
	}

	/**
	 *  Adding MSISDN AVP of type OctetString to the message.
	 */
	public MSISDNAvp addMSISDN(byte[] value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addMSISDN()");
			}
			return new MSISDNAvp(stackObj.addMSISDN(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addMSISDN",e);
		}
	}

	/**
	 *  Adding MSISDN AVP of type OctetString to the message.
	 */
	public MSISDNAvp addMSISDN(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addMSISDN()");
			}
			return new MSISDNAvp(stackObj.addMSISDN(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addMSISDN",e);
		}
	}

	/**
	 *  Adding PositioningData AVP of type UTF8String to the message.
	 */
	public PositioningDataAvp addPositioningData(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addPositioningData()");
			}
			return new PositioningDataAvp(stackObj.addPositioningData(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addPositioningData",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from 3GPPIMSI AVPs.
	 */
//	public java.lang.String get3GPPIMSI() throws GyResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside get3GPPIMSI()");
//			}
//			return stackObj.get3GPPIMSI();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in get3GPPIMSI",e);
//		}
//	}

	/**
	 *  Retrieving a single Grouped value from LCSClientID AVPs.
	 */
	public LCSClientIDAvp getGroupedLCSClientID() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedLCSClientID()");
			}
			return new LCSClientIDAvp(stackObj.getGroupedLCSClientID());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedLCSClientID",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from LocationType AVPs.
	 */
	public LocationTypeAvp getGroupedLocationType() throws GyResourceException { 

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
	 *  Retrieving a single UTF8String value from LocationEstimate AVPs.
	 */
	public java.lang.String getLocationEstimate() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getLocationEstimate()");
			}
			return stackObj.getLocationEstimate();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getLocationEstimate",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from MSISDN AVPs.
	 */
	public java.lang.String getMSISDN() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getMSISDN()");
			}
			return stackObj.getMSISDN();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getMSISDN",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from PositioningData AVPs.
	 */
	public java.lang.String getPositioningData() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getPositioningData()");
			}
			return stackObj.getPositioningData();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getPositioningData",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from MSISDN AVPs.
	 */
	public byte[] getRawMSISDN() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawMSISDN()");
			}
			return stackObj.getRawMSISDN();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawMSISDN",e);
		}
	}

}
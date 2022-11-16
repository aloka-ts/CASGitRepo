package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpTerminalInformation;

public class TerminalInformationAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(TerminalInformationAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpTerminalInformation stackObj;

	public TerminalInformationAvp(AvpTerminalInformation stkObj){
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
	 *  Adding 3GPP2MEID AVP of type OctetString to the message.
	 */
//	public MEID3GPP2Avp add3GPP2MEID(byte[] value) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside add3GPP2MEID()");
//			}
//			return new MEID3GPP2Avp(stackObj.add3GPP2MEID(value));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in add3GPP2MEID",e);
//		}
//	}

	/**
	 *  Adding 3GPP2MEID AVP of type OctetString to the message.
	 */
//	public MEID3GPP2Avp add3GPP2MEID(java.lang.String value) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside add3GPP2MEID()");
//			}
//			return new MEID3GPP2Avp(stackObj.add3GPP2MEID(value));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in add3GPP2MEID",e);
//		}
//	}

	/**
	 *  Adding IMEI AVP of type UTF8String to the message.
	 */
//	public IMEIAvp addIMEI(java.lang.String value) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addIMEI()");
//			}
//			return new IMEIAvp(stackObj.addIMEI(value));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addIMEI",e);
//		}
//	}

	/**
	 *  Adding SoftwareVersion AVP of type UTF8String to the message.
	 */
//	public SoftwareVersionAvp addSoftwareVersion(java.lang.String value) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addSoftwareVersion()");
//			}
//			return new SoftwareVersionAvp(stackObj.addSoftwareVersion(value));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addSoftwareVersion",e);
//		}
//	}

	/**
	 *  Retrieving a single OctetString value from 3GPP2MEID AVPs.
	 */
//	public java.lang.String get3GPP2MEID( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside get3GPP2MEID()");
//			}
//			return stackObj.get3GPP2MEID();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in get3GPP2MEID",e);
//		}
//	}

	/**
	 *  Retrieving a single UTF8String value from IMEI AVPs.
	 */
//	public java.lang.String getIMEI( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getIMEI()");
//			}
//			return stackObj.getIMEI();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getIMEI",e);
//		}
//	}

	/**
	 *  Retrieving a single OctetString value from 3GPP2MEID AVPs.
	 */
//	public byte[] getRaw3GPP2MEID( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getRaw3GPP2MEID()");
//			}
//			return stackObj.getRaw3GPP2MEID();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getRaw3GPP2MEID",e);
//		}
//	}

	/**
	 *  Retrieving a single UTF8String value from SoftwareVersion AVPs.
	 */
//	public java.lang.String getSoftwareVersion( ) throws GyResourceException {
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getSoftwareVersion()");
//			}
//			return stackObj.getSoftwareVersion();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getSoftwareVersion",e);
//		}
//	}

}
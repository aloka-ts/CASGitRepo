package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.CNIPMulticastDistributionEnum;
import com.baypackets.ase.ra.diameter.gy.enums.FileRepairSupportedEnum;
import com.baypackets.ase.ra.diameter.gy.enums.MBMS2G3GIndicatorEnum;
import com.baypackets.ase.ra.diameter.gy.enums.MBMSServiceTypeEnum;
import com.baypackets.ase.ra.diameter.gy.enums.MBMSUserServiceTypeEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpMBMSInformation;
import com.traffix.openblox.diameter.gy.generated.enums.EnumCNIPMulticastDistribution;
import com.traffix.openblox.diameter.gy.generated.enums.EnumFileRepairSupported;
import com.traffix.openblox.diameter.gy.generated.enums.EnumMBMS2G3GIndicator;
import com.traffix.openblox.diameter.gy.generated.enums.EnumMBMSServiceType;
import com.traffix.openblox.diameter.gy.generated.enums.EnumMBMSUserServiceType;

public class MBMSInformationAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(MBMSInformationAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpMBMSInformation stackObj;

	public MBMSInformationAvp(AvpMBMSInformation stkObj){
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
	 *  Adding CNIPMulticastDistribution AVP of type Enumerated to the message.
	 */
//	public CNIPMulticastDistributionAvp addCNIPMulticastDistribution(EnumCNIPMulticastDistribution value) throws GyResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addCNIPMulticastDistribution()");
//			}
//			return new CNIPMulticastDistributionAvp(stackObj.addCNIPMulticastDistribution(value));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addCNIPMulticastDistribution",e);
//		}
//	}

	/**
	 *  Adding FileRepairSupported AVP of type Enumerated to the message.
	 */
	public FileRepairSupportedAvp addFileRepairSupported(EnumFileRepairSupported value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addFileRepairSupported()");
			}
			return new FileRepairSupportedAvp(stackObj.addFileRepairSupported(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addFileRepairSupported",e);
		}
	}

	/**
	 *  Adding MBMS2G3GIndicator AVP of type Enumerated to the message.
	 */
	public MBMS2G3GIndicatorAvp addMBMS2G3GIndicator(EnumMBMS2G3GIndicator value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addMBMS2G3GIndicator()");
			}
			return new MBMS2G3GIndicatorAvp(stackObj.addMBMS2G3GIndicator(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addMBMS2G3GIndicator",e);
		}
	}

	/**
	 *  Adding MBMSServiceArea AVP of type OctetString to the message.
	 */
	public MBMSServiceAreaAvp addMBMSServiceArea(byte[] value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addMBMSServiceArea()");
			}
			return new MBMSServiceAreaAvp(stackObj.addMBMSServiceArea(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addMBMSServiceArea",e);
		}
	}

	/**
	 *  Adding MBMSServiceArea AVP of type OctetString to the message.
	 */
	public MBMSServiceAreaAvp addMBMSServiceArea(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addMBMSServiceArea()");
			}
			return new MBMSServiceAreaAvp(stackObj.addMBMSServiceArea(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addMBMSServiceArea",e);
		}
	}

	/**
	 *  Adding MBMSServiceType AVP of type Enumerated to the message.
	 */
	public MBMSServiceTypeAvp addMBMSServiceType(EnumMBMSServiceType value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addMBMSServiceType()");
			}
			return new MBMSServiceTypeAvp(stackObj.addMBMSServiceType(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addMBMSServiceType",e);
		}
	}

	/**
	 *  Adding MBMSSessionIdentity AVP of type OctetString to the message.
	 */
	public MBMSSessionIdentityAvp addMBMSSessionIdentity(byte[] value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addMBMSSessionIdentity()");
			}
			return new MBMSSessionIdentityAvp(stackObj.addMBMSSessionIdentity(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addMBMSSessionIdentity",e);
		}
	}

	/**
	 *  Adding MBMSSessionIdentity AVP of type OctetString to the message.
	 */
	public MBMSSessionIdentityAvp addMBMSSessionIdentity(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addMBMSSessionIdentity()");
			}
			return new MBMSSessionIdentityAvp(stackObj.addMBMSSessionIdentity(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addMBMSSessionIdentity",e);
		}
	}

	/**
	 *  Adding MBMSUserServiceType AVP of type Enumerated to the message.
	 */
	public MBMSUserServiceTypeAvp addMBMSUserServiceType(EnumMBMSUserServiceType value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addMBMSUserServiceType()");
			}
			return new MBMSUserServiceTypeAvp(stackObj.addMBMSUserServiceType(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addMBMSUserServiceType",e);
		}
	}

	/**
	 *  Adding RAI AVP of type UTF8String to the message.
	 */
	public RAIAvp addRAI(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addRAI()");
			}
			return new RAIAvp(stackObj.addRAI(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addRAI",e);
		}
	}

	/**
	 *  Adding RequiredMBMSBearerCapabilities AVP of type UTF8String to the message.
	 */
	public RequiredMBMSBearerCapabilitiesAvp addRequiredMBMSBearerCapabilities(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addRequiredMBMSBearerCapabilities()");
			}
			return new RequiredMBMSBearerCapabilitiesAvp(stackObj.addRequiredMBMSBearerCapabilities(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addRequiredMBMSBearerCapabilities",e);
		}
	}

	/**
	 *  Adding TMGI AVP of type OctetString to the message.
	 */
	public TMGIAvp addTMGI(byte[] value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addTMGI()");
			}
			return new TMGIAvp(stackObj.addTMGI(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addTMGI",e);
		}
	}

	/**
	 *  Adding TMGI AVP of type OctetString to the message.
	 */
	public TMGIAvp addTMGI(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addTMGI()");
			}
			return new TMGIAvp(stackObj.addTMGI(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addTMGI",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from CNIPMulticastDistribution AVPs.
	 */
//	public int getCNIPMulticastDistribution() throws GyResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getCNIPMulticastDistribution()");
//			}
//			return stackObj.getCNIPMulticastDistribution();
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getCNIPMulticastDistribution",e);
//		}
//	}

	/**
	 *  Retrieves Enum of type CNIPMulticastDistribution.
	 */
//	public CNIPMulticastDistributionEnum getEnumCNIPMulticastDistribution() throws GyResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getEnumCNIPMulticastDistribution()");
//			}
//			return CNIPMulticastDistributionEnum.getContainerObj(stackObj.getEnumCNIPMulticastDistribution());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getEnumCNIPMulticastDistribution",e);
//		}
//	}

	/**
	 *  Retrieves Enum of type FileRepairSupported.
	 */
	public FileRepairSupportedEnum getEnumFileRepairSupported() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumFileRepairSupported()");
			}
			return FileRepairSupportedEnum.getContainerObj(stackObj.getEnumFileRepairSupported());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumFileRepairSupported",e);
		}
	}

	/**
	 *  Retrieves Enum of type MBMS2G3GIndicator.
	 */
	public MBMS2G3GIndicatorEnum getEnumMBMS2G3GIndicator() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumMBMS2G3GIndicator()");
			}
			return MBMS2G3GIndicatorEnum.getContainerObj(stackObj.getEnumMBMS2G3GIndicator());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumMBMS2G3GIndicator",e);
		}
	}

	/**
	 *  Retrieves Enum of type MBMSServiceType.
	 */
	public MBMSServiceTypeEnum getEnumMBMSServiceType() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumMBMSServiceType()");
			}
			return MBMSServiceTypeEnum.getContainerObj(stackObj.getEnumMBMSServiceType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumMBMSServiceType",e);
		}
	}

	/**
	 *  Retrieves Enum of type MBMSUserServiceType.
	 */
	public MBMSUserServiceTypeEnum getEnumMBMSUserServiceType() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumMBMSUserServiceType()");
			}
			return MBMSUserServiceTypeEnum.getContainerObj(stackObj.getEnumMBMSUserServiceType());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumMBMSUserServiceType",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from FileRepairSupported AVPs.
	 */
	public int getFileRepairSupported() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getFileRepairSupported()");
			}
			return stackObj.getFileRepairSupported();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getFileRepairSupported",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from MBMS2G3GIndicator AVPs.
	 */
	public int getMBMS2G3GIndicator() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getMBMS2G3GIndicator()");
			}
			return stackObj.getMBMS2G3GIndicator();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getMBMS2G3GIndicator",e);
		}
	}

	/**
	 *  Retrieving multiple OctetString values from MBMSServiceArea AVPs.
	 */
	public java.lang.String[] getMBMSServiceAreas() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getMBMSServiceAreas()");
			}
			return stackObj.getMBMSServiceAreas();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getMBMSServiceAreas",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from MBMSServiceType AVPs.
	 */
	public int getMBMSServiceType() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getMBMSServiceType()");
			}
			return stackObj.getMBMSServiceType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getMBMSServiceType",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from MBMSSessionIdentity AVPs.
	 */
	public java.lang.String getMBMSSessionIdentity() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getMBMSSessionIdentity()");
			}
			return stackObj.getMBMSSessionIdentity();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getMBMSSessionIdentity",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from MBMSUserServiceType AVPs.
	 */
	public int getMBMSUserServiceType() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getMBMSUserServiceType()");
			}
			return stackObj.getMBMSUserServiceType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getMBMSUserServiceType",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from RAI AVPs.
	 */
	public java.lang.String getRAI() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRAI()");
			}
			return stackObj.getRAI();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRAI",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from MBMSSessionIdentity AVPs.
	 */
	public byte[] getRawMBMSSessionIdentity() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawMBMSSessionIdentity()");
			}
			return stackObj.getRawMBMSSessionIdentity();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawMBMSSessionIdentity",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from TMGI AVPs.
	 */
	public byte[] getRawTMGI() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawTMGI()");
			}
			return stackObj.getRawTMGI();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawTMGI",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from RequiredMBMSBearerCapabilities AVPs.
	 */
	public java.lang.String getRequiredMBMSBearerCapabilities() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRequiredMBMSBearerCapabilities()");
			}
			return stackObj.getRequiredMBMSBearerCapabilities();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRequiredMBMSBearerCapabilities",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from TMGI AVPs.
	 */
	public java.lang.String getTMGI() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getTMGI()");
			}
			return stackObj.getTMGI();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getTMGI",e);
		}
	}


}
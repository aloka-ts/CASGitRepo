package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.enums.EnumOmna;
import com.traffix.openblox.diameter.gy.generated.avp.AvpMMContentType;
import com.traffix.openblox.diameter.gy.generated.avp.AvpAdditionalContentInformation;

public class MMContentTypeAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(MMContentTypeAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpMMContentType stackObj;

	public MMContentTypeAvp(AvpMMContentType stkObj){
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
	 *  Adding AdditionalTypeInformation AVP of type UTF8String to the message.
	 */
	public AdditionalTypeInformationAvp addAdditionalTypeInformation(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAdditionalTypeInformation()");
			}
			return new AdditionalTypeInformationAvp(stackObj.addAdditionalTypeInformation(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addAdditionalTypeInformation",e);
		}
	}

	/**
	 *  Adding ContentSize AVP of type Unsigned32 to the message.
	 */
	public ContentSizeAvp addContentSize(long value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addContentSize()");
			}
			return new ContentSizeAvp(stackObj.addContentSize(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addContentSize",e);
		}
	}

	/**
	 *  Adding AdditionalContentInformation AVP of type Grouped to the message.
	 */
	public AdditionalContentInformationAvp addGroupedAdditionalContentInformation() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedAdditionalContentInformation()");
			}
			return new AdditionalContentInformationAvp(stackObj.addGroupedAdditionalContentInformation());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedAdditionalContentInformation",e);
		}
	}

	/**
	 *  Adding TypeNumber AVP of type Enumerated to the message.
	 */
	public TypeNumberAvp addTypeNumber(EnumOmna value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addTypeNumber()");
			}
			return new TypeNumberAvp(stackObj.addTypeNumber(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addTypeNumber",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from AdditionalTypeInformation AVPs.
	 */
	public java.lang.String getAdditionalTypeInformation() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAdditionalTypeInformation()");
			}
			return stackObj.getAdditionalTypeInformation();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getAdditionalTypeInformation",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from ContentSize AVPs.
	 */
	public long getContentSize() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getContentSize()");
			}
			return stackObj.getContentSize();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getContentSize",e);
		}
	}

	// TODO
//	/**
//	 *  This method returns the Enum value of TYpeNumber.
//	 */
//	public OmnaEnum getEnumTypeNumber() throws GyResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getEnumTypeNumber()");
//			}
//			return new OmnaEnum(stackObj.getEnumTypeNumber());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getEnumTypeNumber",e);
//		}
//	}


	/**
	 *  Retrieving multiple Grouped values from AdditionalContentInformation AVPs.
	 */
	public AdditionalContentInformationAvp[] getGroupedAdditionalContentInformations() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedAdditionalContentInformations()");
			}
			AvpAdditionalContentInformation[] stackAv= stackObj.getGroupedAdditionalContentInformations();
			AdditionalContentInformationAvp[] contAvp= new AdditionalContentInformationAvp[stackAv.length];
			for(int i=0;i<stackAv.length;i++){
				contAvp[i]=new AdditionalContentInformationAvp(stackAv[i]);
			}
			return contAvp;
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedAdditionalContentInformations",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from TypeNumber AVPs.
	 */
	public int getTypeNumber() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getTypeNumber()");
			}
			return stackObj.getTypeNumber();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getTypeNumber",e);
		}
	}


}

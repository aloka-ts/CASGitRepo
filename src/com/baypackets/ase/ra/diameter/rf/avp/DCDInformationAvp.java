package com.baypackets.ase.ra.diameter.rf.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterTime;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.rf.RfResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.rf.generated.avp.AvpDCDInformation;

public class DCDInformationAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(DCDInformationAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpDCDInformation stackObj;

	public DCDInformationAvp(AvpDCDInformation stkObj){
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
	 * Adding ContentID AVP of type UTF8String to the message.
	 * @param value
	 * @return
	 * @throws RfResourceException 
	 */
	public ContentIDAvp addContentID(java.lang.String value) throws RfResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addContentID()");
			}
			return new ContentIDAvp(stackObj.addContentID(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addContentID ",e);
		}
	}

	/**
	 * Adding ContentProviderID AVP of type UTF8String to the message.
	 * @param value
	 * @return
	 * @throws RfResourceException 
	 */
	public ContentProviderIDAvp addContentProviderID(java.lang.String value) throws RfResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addContentProviderID()");
			}
			return new ContentProviderIDAvp(stackObj.addContentProviderID(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addContentProviderID ",e);
		}
	}

	/**
	 * Retrieving a single UTF8String value from ContentID AVPs.
	 * @return
	 * @throws RfResourceException 
	 */
	public String getContentID() throws RfResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getContentID()");
			}
			return stackObj.getContentID();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getContentID",e);
		}
	}

	/**
	 * Retrieving a single UTF8String value from ContentProviderID AVPs.
	 * @return
	 * @throws RfResourceException 
	 */
	public String getContentProviderID() throws RfResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getContentProviderID()");
			}
			return stackObj.getContentProviderID();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getContentProviderID",e);
		}
	}

}
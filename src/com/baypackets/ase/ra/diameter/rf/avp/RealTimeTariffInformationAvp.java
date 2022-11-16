package com.baypackets.ase.ra.diameter.rf.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.rf.RfResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.rf.generated.avp.AvpRealTimeTariffInformation;

public class RealTimeTariffInformationAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(RealTimeTariffInformationAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpRealTimeTariffInformation stackObj;

	public RealTimeTariffInformationAvp(AvpRealTimeTariffInformation stkObj){
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
	 *  Adding TariffInformation AVP of type Grouped to the message.
	 */
	public TariffInformationAvp addGroupedTariffInformation() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedTariffInformation()");
			}
			return new TariffInformationAvp(stackObj.addGroupedTariffInformation());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addGroupedTariffInformation",e);
		}
	}

	/**
	 *  Adding TariffXML AVP of type UTF8String to the message.
	 */
	public TariffXMLAvp addTariffXML(java.lang.String value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addTariffXML()");
			}
			return new TariffXMLAvp(stackObj.addTariffXML(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addTariffXML",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from TariffInformation AVPs.
	 */
	public TariffInformationAvp getGroupedTariffInformation() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedTariffInformation()");
			}
			return new TariffInformationAvp(stackObj.getGroupedTariffInformation());
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getGroupedTariffInformation",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from TariffXML AVPs.
	 */
	public java.lang.String getTariffXML() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getTariffXML()");
			}
			return stackObj.getTariffXML();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getTariffXML",e);
		}
	}


}
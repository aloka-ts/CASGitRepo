package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpEarlyMediaDescription;
import com.traffix.openblox.diameter.gy.generated.avp.AvpSDPMediaComponent;

public class EarlyMediaDescriptionAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(EarlyMediaDescriptionAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpEarlyMediaDescription stackObj;

	public EarlyMediaDescriptionAvp(AvpEarlyMediaDescription stkObj){
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
	 *  Adding SDPMediaComponent AVP of type Grouped to the message.
	 */
	public SDPMediaComponentAvp addGroupedSDPMediaComponent() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedSDPMediaComponent()");
			}
			return new SDPMediaComponentAvp(stackObj.addGroupedSDPMediaComponent());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedSDPMediaComponent",e);
		}
	}

	/**
	 *  Adding SDPTimeStamps AVP of type Grouped to the message.
	 */
	public SDPTimeStampsAvp addGroupedSDPTimeStamps() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedSDPTimeStamps()");
			}
			return new SDPTimeStampsAvp(stackObj.addGroupedSDPTimeStamps());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedSDPTimeStamps",e);
		}
	}

	/**
	 *  Adding SDPSessionDescription AVP of type UTF8String to the message.
	 */
	public SDPSessionDescriptionAvp addSDPSessionDescription(java.lang.String value) throws GyResourceException { 
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addSDPSessionDescription()");
			}
			return new SDPSessionDescriptionAvp(stackObj.addSDPSessionDescription(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addSDPSessionDescription",e);
		}
	}

	/**
	 *  Retrieving multiple Grouped values from SDPMediaComponent AVPs.
	 */
	public SDPMediaComponentAvp[] getGroupedSDPMediaComponents() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedSDPMediaComponents()");
			}
			AvpSDPMediaComponent[] stackAv= stackObj.getGroupedSDPMediaComponents();
			SDPMediaComponentAvp[] contAvp= new SDPMediaComponentAvp[stackAv.length];
			for(int i=0;i<stackAv.length;i++){
				contAvp[i]=new SDPMediaComponentAvp(stackAv[i]);
			}
			return contAvp;
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedSDPMediaComponents",e);
		}
	}

	/**
	 *  Retrieving a single Grouped value from SDPTimeStamps AVPs.
	 */
	public SDPTimeStampsAvp getGroupedSDPTimeStamps() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedSDPTimeStamps()");
			}
			return new SDPTimeStampsAvp(stackObj.getGroupedSDPTimeStamps());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedSDPTimeStamps",e);
		}
	}

	/**
	 *  Retrieving multiple UTF8String values from SDPSessionDescription AVPs.
	 */
	public java.lang.String[] getSDPSessionDescriptions() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getSDPSessionDescriptions()");
			}
			return stackObj.getSDPSessionDescriptions();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getSDPSessionDescriptions",e);
		}
	}
}
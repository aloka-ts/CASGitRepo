package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpAFCorrelationInformation;
import com.traffix.openblox.diameter.gy.generated.avp.AvpFlows;

public class AFCorrelationInformationAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(AFCorrelationInformationAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpAFCorrelationInformation stackObj;

	public AFCorrelationInformationAvp(AvpAFCorrelationInformation stkObj){
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
	 *  Adding AFChargingIdentifier AVP of type OctetString to the message.
	 */
	public AFChargingIdentifierAvp addAFChargingIdentifier(byte[] value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAFChargingIdentifier()");
			}
			return new AFChargingIdentifierAvp(stackObj.addAFChargingIdentifier(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addAFChargingIdentifier",e);
		}
	}

	/**
	 *  Adding AFChargingIdentifier AVP of type OctetString to the message.
	 */
	public AFChargingIdentifierAvp addAFChargingIdentifier(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addAFChargingIdentifier()");
			}
			return new AFChargingIdentifierAvp(stackObj.addAFChargingIdentifier(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addAFChargingIdentifier",e);
		}
	}

	/**
	 *  Adding Flows AVP of type Grouped to the message.
	 */
	public FlowsAvp addGroupedFlows() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedFlows()");
			}
			return new FlowsAvp(stackObj.addGroupedFlows());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedFlows",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from AFChargingIdentifier AVPs.
	 */
	public java.lang.String getAFChargingIdentifier() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getAFChargingIdentifier()");
			}
			return stackObj.getAFChargingIdentifier();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getAFChargingIdentifier",e);
		}
	}

	/**
	 *  Retrieving multiple Grouped values from Flows AVPs.
	 */
	public FlowsAvp[] getGroupedFlowss() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedFlowss()");
			}
			AvpFlows[] stackAv = stackObj.getGroupedFlowss();
			FlowsAvp[] contAvp= new FlowsAvp[stackAv.length];
			for(int i=0;i<stackAv.length;i++){
				contAvp[i]=new FlowsAvp(stackAv[i]);
			}
			return contAvp;
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedFlowss",e);
		}
	}

	/**
	 *  Retrieving a single OctetString value from AFChargingIdentifier AVPs.
	 */
	public byte[] getRawAFChargingIdentifier() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getRawAFChargingIdentifier()");
			}
			return stackObj.getRawAFChargingIdentifier();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getRawAFChargingIdentifier",e);
		}
	}
}
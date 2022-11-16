package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpTrunkGroupID;

public class TrunkGroupIDAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(TrunkGroupIDAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpTrunkGroupID stackObj;

	public TrunkGroupIDAvp(AvpTrunkGroupID stkObj){
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
	 *  Adding IncomingTrunkGroupID AVP of type UTF8String to the message.
	 */
	public IncomingTrunkGroupIDAvp addIncomingTrunkGroupID(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addIncomingTrunkGroupID()");
			}
			return new IncomingTrunkGroupIDAvp(stackObj.addIncomingTrunkGroupID(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addIncomingTrunkGroupID",e);
		}
	}

	/**
	 *  Adding OutgoingTrunkGroupID AVP of type UTF8String to the message.
	 */
	public OutgoingTrunkGroupIDAvp addOutgoingTrunkGroupID(java.lang.String value) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addOutgoingTrunkGroupID()");
			}
			return new OutgoingTrunkGroupIDAvp(stackObj.addOutgoingTrunkGroupID(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addOutgoingTrunkGroupID",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from IncomingTrunkGroupID AVPs.
	 */
	public java.lang.String getIncomingTrunkGroupID( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getIncomingTrunkGroupID()");
			}
			return stackObj.getIncomingTrunkGroupID();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getIncomingTrunkGroupID",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from OutgoingTrunkGroupID AVPs.
	 */
	public java.lang.String getOutgoingTrunkGroupID( ) throws GyResourceException {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getOutgoingTrunkGroupID()");
			}
			return stackObj.getOutgoingTrunkGroupID();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getOutgoingTrunkGroupID",e);
		}
	}


}
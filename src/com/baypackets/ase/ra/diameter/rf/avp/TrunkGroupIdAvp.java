package com.baypackets.ase.ra.diameter.rf.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.rf.RfResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.rf.generated.avp.AvpTrunkGroupId;

public class TrunkGroupIdAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(TrunkGroupIdAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpTrunkGroupId stackObj;

	public TrunkGroupIdAvp(AvpTrunkGroupId stkObj){
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
	 *  Adding IncomingTrunkGroupId AVP of type UTF8String to the message.
	 */
	public IncomingTrunkGroupIdAvp addIncomingTrunkGroupId(java.lang.String value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addIncomingTrunkGroupId()");
			}
			return new IncomingTrunkGroupIdAvp(stackObj.addIncomingTrunkGroupId(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addIncomingTrunkGroupId",e);
		}
	}

	/**
	 *  Adding OutgoingTrunkGroupId AVP of type UTF8String to the message.
	 */
	public OutgoingTrunkGroupIdAvp addOutgoingTrunkGroupId(java.lang.String value) throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addOutgoingTrunkGroupId()");
			}
			return new OutgoingTrunkGroupIdAvp(stackObj.addOutgoingTrunkGroupId(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addOutgoingTrunkGroupId",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from IncomingTrunkGroupId AVPs.
	 */
	public java.lang.String getIncomingTrunkGroupId() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getIncomingTrunkGroupId()");
			}return stackObj.getIncomingTrunkGroupId();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getIncomingTrunkGroupId",e);
		}
	}

	/**
	 *  Retrieving a single UTF8String value from OutgoingTrunkGroupId AVPs.
	 */
	public java.lang.String getOutgoingTrunkGroupId() throws RfResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getOutgoingTrunkGroupId()");
			}return stackObj.getOutgoingTrunkGroupId();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getOutgoingTrunkGroupId",e);
		}
	}



}
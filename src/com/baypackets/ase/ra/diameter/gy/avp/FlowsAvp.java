package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.FinalUnitActionEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpFlows;
import com.traffix.openblox.diameter.gy.generated.enums.EnumFinalUnitAction;

public class FlowsAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(FlowsAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpFlows stackObj;

	public FlowsAvp(AvpFlows stkObj){
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
	 *  Adding FinalUnitAction AVP of type Enumerated to the message.
	 */
	public FinalUnitActionAvp addFinalUnitAction(EnumFinalUnitAction value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addFinalUnitAction()");
			}
			return new FinalUnitActionAvp(stackObj.addFinalUnitAction(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addFinalUnitAction",e);
		}
	}

	/**
	 *  Adding FlowNumber AVP of type Unsigned32 to the message.
	 */
	public FlowNumberAvp addFlowNumber(long value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addFlowNumber()");
			}
			return new FlowNumberAvp(stackObj.addFlowNumber(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addFlowNumber",e);
		}
	}

	/**
	 *  Adding MediaComponentNumber AVP of type Unsigned32 to the message.
	 */
	public MediaComponentNumberAvp addMediaComponentNumber(long value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addMediaComponentNumber()");
			}
			return new MediaComponentNumberAvp(stackObj.addMediaComponentNumber(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addMediaComponentNumber",e);
		}
	}

	/**
	 *  Retrieves Enum of FinalUnitAction type.
	 */
	public FinalUnitActionEnum getEnumFinalUnitAction() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumFinalUnitAction()");
			}
			return FinalUnitActionEnum.getContainerObj(stackObj.getEnumFinalUnitAction());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumFinalUnitAction",e);
		}
	}

	/**
	 *  Retrieving a single Enumerated value from FinalUnitAction AVPs.
	 */
	public int getFinalUnitAction() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getFinalUnitAction()");
			}
			return stackObj.getFinalUnitAction();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getFinalUnitAction",e);
		}
	}

	/**
	 *  Retrieving multiple Unsigned32 values from FlowNumber AVPs.
	 */
	public long[] getFlowNumbers() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getFlowNumbers()");
			}
			return stackObj.getFlowNumbers();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getFlowNumbers",e);
		}
	}

	/**
	 *  Retrieving a single Unsigned32 value from MediaComponentNumber AVPs.
	 */
	public long getMediaComponentNumber() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getMediaComponentNumber()");
			}
			return stackObj.getMediaComponentNumber();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getMediaComponentNumber",e);
		}
	}

}
package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.common.utils.IPFilter;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.FinalUnitActionEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpFinalUnitIndication;
import com.traffix.openblox.diameter.gy.generated.enums.EnumFinalUnitAction;

public class FinalUnitIndicationAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(FinalUnitIndicationAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpFinalUnitIndication stackObj;

	public FinalUnitIndicationAvp(AvpFinalUnitIndication stkObj){
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
	 *  Adding FilterId AVP of type UTF8String to the message.
	 */
	public FilterIdAvp addFilterId(java.lang.String value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addFilterId()");
			}
			return new FilterIdAvp(stackObj.addFilterId(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addFilterId",e);
		}
	}

	/**
	 *  Adding FinalUnitAction AVP of type Enumerated to the message.
	 */
	public FinalUnitActionAvp addFinalUnitAction(FinalUnitActionEnum value) throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addFinalUnitAction()");
			}
			return new FinalUnitActionAvp(stackObj.addFinalUnitAction(FinalUnitActionEnum.getStackObj(value)));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addFinalUnitAction",e);
		}
	}

	/**
	 *  Adding RedirectServer AVP of type Grouped to the message.
	 */
	public RedirectServerAvp addGroupedRedirectServer() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addGroupedRedirectServer()");
			}
			return new RedirectServerAvp(stackObj.addGroupedRedirectServer());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addGroupedRedirectServer",e);
		}
	}

	/**
	 *  Adding RestrictionFilterRule AVP of type IPFilterRule to the message.
	 */
	// TODO
//	public RestrictionFilterRuleAvp addRestrictionFilterRule(IPFilter value) throws GyResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside addRestrictionFilterRule()");
//			}
//			return new RestrictionFilterRuleAvp(stackObj.addRestrictionFilterRule(value));
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in addRestrictionFilterRule",e);
//		}
//	}

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
	 *  Retrieving multiple UTF8String values from FilterId AVPs.
	 */
	public java.lang.String[] getFilterIds() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getFilterIds()");
			}
			return stackObj.getFilterIds();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getFilterIds",e);
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
	 *  Retrieving a single Grouped value from RedirectServer AVPs.
	 */
	public RedirectServerAvp getGroupedRedirectServer() throws GyResourceException { 

		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getGroupedRedirectServer()");
			}
			return new RedirectServerAvp(stackObj.getGroupedRedirectServer());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getGroupedRedirectServer",e);
		}
	}

	/**
	 *  Retrieving multiple IPFilterRule values from RestrictionFilterRule AVPs.
	 */
	//TODO
//	public IPFilter[] getRestrictionFilterRules() throws GyResourceException { 
//
//		try {
//			if(logger.isDebugEnabled()){
//				logger.debug("Inside getRestrictionFilterRules()");
//			}
//			return new RedirectServerAvp(stackObj.getRestrictionFilterRules());
//		} catch (ValidationException e) {
//			throw new GyResourceException("Exception in getRestrictionFilterRules",e);
//		}
//	}


}
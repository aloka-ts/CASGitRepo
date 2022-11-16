package com.baypackets.ase.ra.diameter.rf.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.rf.RfResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.rf.generated.avp.AvpInterOperatorIdentifier;

public class InterOperatorIdentifierAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(InterOperatorIdentifierAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpInterOperatorIdentifier stackObj;

	public InterOperatorIdentifierAvp(AvpInterOperatorIdentifier stkObj){
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
	*  Adding OriginatingIOI AVP of type UTF8String to the message.
	 * @throws RfResourceException 
	*/
	public OriginatingIOIAvp addOriginatingIOI(java.lang.String value) throws RfResourceException  {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addOriginatingIOI()");
			}
			return new OriginatingIOIAvp(stackObj.addOriginatingIOI(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addOriginatingIOI ",e);
		}
	}

	/**
	*  Adding TerminatingIOI AVP of type UTF8String to the message.
	 * @throws RfResourceException 
	*/
	public TerminatingIOIAvp addTerminatingIOI(java.lang.String value) throws RfResourceException  {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addTerminatingIOI()");
			}
			return new TerminatingIOIAvp(stackObj.addTerminatingIOI(value));
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in addTerminatingIOI ",e);
		}
	}

	/**
	*  Retrieving a single UTF8String value from OriginatingIOI AVPs.
	 * @throws RfResourceException 
	*/
	public java.lang.String getOriginatingIOI() throws RfResourceException  {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getOriginatingIOI()");
			}
			return	stackObj.getOriginatingIOI();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getOriginatingIOI ",e);
		}
	}

	/**
	*  Retrieving a single UTF8String value from TerminatingIOI AVPs.
	 * @throws RfResourceException 
	*/
	public java.lang.String getTerminatingIOI() throws RfResourceException  {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getTerminatingIOI()");
			}
			return	stackObj.getTerminatingIOI();
		} catch (ValidationException e) {
			throw new RfResourceException("Exception in getTerminatingIOI ",e);
		}
	}


}
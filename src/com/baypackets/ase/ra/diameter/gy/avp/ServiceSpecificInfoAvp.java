package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpServiceSpecificInfo;

public class ServiceSpecificInfoAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(ServiceSpecificInfoAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpServiceSpecificInfo stackObj;

	public ServiceSpecificInfoAvp(AvpServiceSpecificInfo stkObj){
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
*  Adding ServiceSpecificData AVP of type UTF8String to the message.
*/
public ServiceSpecificDataAvp addServiceSpecificData(java.lang.String value) throws GyResourceException {
try {
if(logger.isDebugEnabled()){
logger.debug("Inside addServiceSpecificData()");
}
return new ServiceSpecificDataAvp(stackObj.addServiceSpecificData(value));
} catch (ValidationException e) {
throw new GyResourceException("Exception in addServiceSpecificData",e);
}
}

/**
*  Adding ServiceSpecificType AVP of type Unsigned32 to the message.
*/
public ServiceSpecificTypeAvp addServiceSpecificType(long value) throws GyResourceException {
try {
if(logger.isDebugEnabled()){
logger.debug("Inside addServiceSpecificType()");
}
return new ServiceSpecificTypeAvp(stackObj.addServiceSpecificType(value));
} catch (ValidationException e) {
throw new GyResourceException("Exception in addServiceSpecificType",e);
}
}

/**
*  Retrieving a single UTF8String value from ServiceSpecificData AVPs.
*/
public java.lang.String getServiceSpecificData( ) throws GyResourceException {
try {
if(logger.isDebugEnabled()){
logger.debug("Inside getServiceSpecificData()");
}
return stackObj.getServiceSpecificData();
} catch (ValidationException e) {
throw new GyResourceException("Exception in getServiceSpecificData",e);
}
}

/**
*  Retrieving a single Unsigned32 value from ServiceSpecificType AVPs.
*/
public long getServiceSpecificType( ) throws GyResourceException {
try {
if(logger.isDebugEnabled()){
logger.debug("Inside getServiceSpecificType()");
}
return stackObj.getServiceSpecificType();
} catch (ValidationException e) {
throw new GyResourceException("Exception in getServiceSpecificType",e);
}
}

}
package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterEnumerated;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.traffix.openblox.diameter.gy.generated.avp.AvpCSGAccessMode;

public class CSGAccessModeAvp extends AvpDiameterEnumerated {

	private static Logger logger = Logger.getLogger(CSGAccessModeAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpCSGAccessMode stackObj;

	public CSGAccessModeAvp(AvpCSGAccessMode stkObj){
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
	 *   Range check of the assigned integer32 value
	 */
	public boolean isValid(){
		return stackObj.isValid();
	}

	/**
	 *    Get name of enumerated value
	 */
	public String getEnumName(){
		return stackObj.getEnumName();
	}

	/**
	 * Returns enumeration value of a given enumeration name
	 */
	public int getValue(String s) throws IllegalArgumentException {
		return stackObj.getValue(s);
	}
}
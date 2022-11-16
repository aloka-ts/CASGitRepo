package com.baypackets.ase.ra.diameter.rf.avp;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterEnumerated;
import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterUnsigned32;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.traffix.openblox.diameter.rf.generated.avp.AvpCCUnitType;

public class CCUnitTypeAvp extends AvpDiameterEnumerated {

	public static final long vendorId = 0L;

	private AvpCCUnitType stackObj;

	public CCUnitTypeAvp(AvpCCUnitType stkObj){
		super(stkObj);
		this.stackObj=stkObj;
	}

	public int getCode() {
		return stackObj.getCode();
	}

	public FlagRuleEnum getMRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getMRule());
	}

	public String getName() {
		return stackObj.getName();
	}

	public FlagRuleEnum getPRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getPRule());
	}

	public long getVendorId() {
		return stackObj.getVendorId();
	}

	public FlagRuleEnum getVRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getVRule());
	}
	
	/**
	 * Get name of enumerated value
	 */
	 public String getEnumName() {
		 return stackObj.getEnumName();
	 }

	public int getValue(String enumName) {
		return stackObj.getValue(enumName);
	}
     
}
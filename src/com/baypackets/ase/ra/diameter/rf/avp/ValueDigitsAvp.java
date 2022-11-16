package com.baypackets.ase.ra.diameter.rf.avp;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterInteger64;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.traffix.openblox.diameter.rf.generated.avp.AvpValueDigits;

public class ValueDigitsAvp extends AvpDiameterInteger64 {

	public static final long vendorId = 0L;

	private AvpValueDigits stackObj;

	public ValueDigitsAvp(AvpValueDigits stkObj){
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
}
package com.baypackets.ase.ra.diameter.rf.avp;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterUnsigned32;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.traffix.openblox.diameter.rf.generated.avp.AvpAccountingApplicationId;


public class AccountingApplicationIdAvp extends AvpDiameterUnsigned32 {

	private AvpAccountingApplicationId stackObj;

	public AccountingApplicationIdAvp(AvpAccountingApplicationId stkObj){
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
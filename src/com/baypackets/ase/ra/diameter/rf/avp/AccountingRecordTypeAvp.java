package com.baypackets.ase.ra.diameter.rf.avp;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterEnumerated;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.traffix.openblox.diameter.rf.generated.avp.AvpAccountingRecordType;

public class AccountingRecordTypeAvp extends AvpDiameterEnumerated {

	public static final String name = "AccountingRecordType";
	public static final int code = 480;
	public static final long vendorId = 0L;

	private AvpAccountingRecordType stackObj;

	public AccountingRecordTypeAvp(AvpAccountingRecordType stkObj){
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


	public boolean isValid(){
		return stackObj.isValid();
	}

	public String getEnumName(){
		return stackObj.getEnumName();
	}

	public int getValue(String s) throws IllegalArgumentException {
		return stackObj.getValue(s);
	}

}
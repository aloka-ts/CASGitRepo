package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterIdentity;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.traffix.openblox.diameter.gy.generated.avp.AvpErrorReportingHost;

public class ErrorReportingHostAvp extends AvpDiameterIdentity {

	private static Logger logger = Logger.getLogger(ErrorReportingHostAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpErrorReportingHost stackObj;

	public ErrorReportingHostAvp(AvpErrorReportingHost stkObj){
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
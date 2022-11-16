package com.baypackets.ase.ra.diameter.rf.stackif;

import com.baypackets.ase.ra.diameter.common.exception.ValidationRecord;

public class ValidationRecordImpl implements ValidationRecord {
	
	com.traffix.openblox.core.exceptions.ValidationRecord stackObj;
	
	public ValidationRecordImpl(com.traffix.openblox.core.exceptions.ValidationRecord stkObj){
		this.stackObj=stkObj;
	}

//	@Override
//	public BaseAvp getFailedAvp() {
//		return null;
//	}

	@Override
	public int getFailedCode() {
		return stackObj.getFailedCode();
	}

}

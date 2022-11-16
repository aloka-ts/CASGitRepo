package com.agnity.simulator.callflowadaptor.element.type.tcapsubtype;

import jain.protocol.ss7.tcap.TcapConstants;

import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.utils.Constants;

public class UAbortNode extends TcapNode {
	
	

	public UAbortNode(){
		super(Constants.U_ABORT, null);
		setDialogType(TcapConstants.PRIMITIVE_USER_ABORT);
	}
	
	
	
}

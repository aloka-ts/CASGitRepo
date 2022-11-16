package com.agnity.simulator.callflowadaptor.element.type.tcapsubtype;

import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.utils.Constants;
import com.genband.inap.operations.InapOpCodes;

public class DfcNode extends TcapNode {

	public DfcNode(){
		super(Constants.DFC, InapOpCodes.DFC);
	}
	
	
	
}

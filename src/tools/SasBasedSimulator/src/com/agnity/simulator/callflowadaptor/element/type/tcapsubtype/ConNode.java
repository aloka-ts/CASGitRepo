package com.agnity.simulator.callflowadaptor.element.type.tcapsubtype;

import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.utils.Constants;
import com.genband.inap.operations.InapOpCodes;

public class ConNode extends TcapNode {

	public ConNode(){
		super(Constants.CON, InapOpCodes.CONNECT);
	}
	
	
	
}

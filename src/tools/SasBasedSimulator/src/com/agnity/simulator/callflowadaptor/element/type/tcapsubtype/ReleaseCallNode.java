package com.agnity.simulator.callflowadaptor.element.type.tcapsubtype;

import jain.protocol.ss7.tcap.TcapConstants;

import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.utils.Constants;
import com.genband.inap.operations.InapOpCodes;

public class ReleaseCallNode extends TcapNode {
	
	public ReleaseCallNode(){
		super(Constants.RELEASE_CALL, InapOpCodes.RELEASE_CALL);
		setDialogType(TcapConstants.PRIMITIVE_END);
		
	}

	
	
	
	
}

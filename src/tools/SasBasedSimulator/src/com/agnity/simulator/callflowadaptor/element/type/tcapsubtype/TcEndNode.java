package com.agnity.simulator.callflowadaptor.element.type.tcapsubtype;

import jain.protocol.ss7.tcap.TcapConstants;

import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.utils.Constants;

public class TcEndNode extends TcapNode {
	
	

	public TcEndNode(){
		super(Constants.TC_END, null);
		setDialogType(TcapConstants.PRIMITIVE_END);
	}
	
	
	
}

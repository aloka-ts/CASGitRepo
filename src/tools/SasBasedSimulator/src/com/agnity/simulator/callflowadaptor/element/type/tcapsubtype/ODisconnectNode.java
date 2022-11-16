package com.agnity.simulator.callflowadaptor.element.type.tcapsubtype;

import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.utils.Constants;
import com.agnity.win.operations.WinOpCodes;

public class ODisconnectNode extends TcapNode {

	public ODisconnectNode(){
		super(Constants.ODISCONNECT,WinOpCodes.O_DISC);
	}


}

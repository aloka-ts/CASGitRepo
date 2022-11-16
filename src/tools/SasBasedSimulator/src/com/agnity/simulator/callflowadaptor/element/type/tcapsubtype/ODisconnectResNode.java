package com.agnity.simulator.callflowadaptor.element.type.tcapsubtype;

import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.utils.Constants;
import com.agnity.win.operations.WinOpCodes;

public class ODisconnectResNode extends TcapNode {

	public ODisconnectResNode(){
		super(Constants.ODISCONNECTRES,WinOpCodes.O_DISC);
	}


}

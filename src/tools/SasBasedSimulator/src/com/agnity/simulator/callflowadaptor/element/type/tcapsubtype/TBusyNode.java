package com.agnity.simulator.callflowadaptor.element.type.tcapsubtype;

import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.utils.Constants;
import com.agnity.win.operations.WinOpCodes;

public class TBusyNode extends TcapNode {

	public TBusyNode(){
		super(Constants.TBUSY,WinOpCodes.T_BUSY);
	}


}

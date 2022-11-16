package com.agnity.simulator.callflowadaptor.element.type.tcapsubtype;

import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.utils.Constants;
import com.agnity.win.operations.WinOpCodes;

public class CallControlDirNode extends TcapNode {

	public CallControlDirNode(){
		super(Constants.CALLCONTROLDIRREQ,WinOpCodes.CALL_CNTRL_DIR);
	}


}

package com.agnity.simulator.callflowadaptor.element.type.tcapsubtype;

import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.utils.Constants;
import com.agnity.win.operations.WinOpCodes;

public class CallControlDirRespNode extends TcapNode {

	public CallControlDirRespNode(){
		super(Constants.CALLCONTROLDIRRES,WinOpCodes.CALL_CNTRL_DIR);
	}


}

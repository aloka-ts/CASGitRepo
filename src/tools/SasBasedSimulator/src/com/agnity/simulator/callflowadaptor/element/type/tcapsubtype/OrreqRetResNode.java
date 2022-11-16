package com.agnity.simulator.callflowadaptor.element.type.tcapsubtype;

import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.utils.Constants;
import com.genband.inap.operations.InapOpCodes;
import com.agnity.win.operations.WinOpCodes;

public class OrreqRetResNode extends TcapNode {

	public OrreqRetResNode(){
		super(Constants.ORIG_REQ_RET_RESULT,WinOpCodes.OR);
	}


}

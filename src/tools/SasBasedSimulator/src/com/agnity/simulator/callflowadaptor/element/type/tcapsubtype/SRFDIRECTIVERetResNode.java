package com.agnity.simulator.callflowadaptor.element.type.tcapsubtype;

import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.utils.Constants;
import com.agnity.win.operations.WinOpCodes;

public class SRFDIRECTIVERetResNode extends TcapNode {

	public SRFDIRECTIVERetResNode(){
		super(Constants.SRFDIRECTIVE_RET_RES,WinOpCodes.SRF_DIR);
	}


}

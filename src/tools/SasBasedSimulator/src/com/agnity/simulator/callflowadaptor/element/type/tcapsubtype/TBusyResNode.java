package com.agnity.simulator.callflowadaptor.element.type.tcapsubtype;

import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.utils.Constants;
import com.agnity.win.operations.WinOpCodes;

public class TBusyResNode extends TcapNode {

	public TBusyResNode(){
		super(Constants.TBUSYRES,WinOpCodes.T_BUSY);
	}


}

package com.agnity.simulator.callflowadaptor.element.type.tcapsubtype;

import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.utils.Constants;
import com.agnity.win.operations.WinOpCodes;

public class OCalledPartyBusyResNode extends TcapNode {

	public OCalledPartyBusyResNode(){
		super(Constants.OCALLEDPARTYBUSYRES,WinOpCodes.O_CLD_PTY_BUSY);
	}


}

package com.agnity.simulator.callflowadaptor.element.type.tcapsubtype;

import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.utils.Constants;
import com.agnity.win.operations.WinOpCodes;

public class OCalledPartyBusyNode extends TcapNode {

	public OCalledPartyBusyNode(){
		super(Constants.OCALLEDPARTYBUSY,WinOpCodes.O_CLD_PTY_BUSY);
	}


}

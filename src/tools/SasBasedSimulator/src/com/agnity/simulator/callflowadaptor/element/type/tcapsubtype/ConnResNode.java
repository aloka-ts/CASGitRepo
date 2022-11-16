package com.agnity.simulator.callflowadaptor.element.type.tcapsubtype;

import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.utils.Constants;
import com.agnity.win.operations.WinOpCodes;

public class ConnResNode extends TcapNode {

	public ConnResNode(){
		super(Constants.CONNRES,WinOpCodes.CR);
	}


}

package com.agnity.simulator.callflowadaptor.element.type.tcapsubtype;

import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.utils.Constants;
import com.agnity.win.operations.WinOpCodes;

public class OAnswerNode extends TcapNode {

	public OAnswerNode(){
		super(Constants.OANSWER,WinOpCodes.O_ANS);
	}


}

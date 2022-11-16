package com.agnity.simulator.callflowadaptor.element.type.tcapsubtype;

import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.utils.Constants;
import com.agnity.win.operations.WinOpCodes;

public class TNoAnswerResNode extends TcapNode {

	public TNoAnswerResNode(){
		super(Constants.TNOANSWERRES,WinOpCodes.T_NOANS);
	}


}

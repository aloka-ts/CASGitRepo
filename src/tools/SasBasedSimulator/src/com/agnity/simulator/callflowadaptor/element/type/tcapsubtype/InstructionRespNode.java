package com.agnity.simulator.callflowadaptor.element.type.tcapsubtype;

import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.utils.Constants;
import com.agnity.win.operations.WinOpCodes;

public class InstructionRespNode extends TcapNode {

	public InstructionRespNode(){
		super(Constants.INSTRUCTIONRES,WinOpCodes.IR);
	}


}

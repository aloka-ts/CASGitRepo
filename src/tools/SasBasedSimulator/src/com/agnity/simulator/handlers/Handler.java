package com.agnity.simulator.handlers;

import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;

public interface Handler {
	
	public void performAction(Node node, SimCallProcessingBuffer simCpb);
	
	public void recieveMessage(Node node, SimCallProcessingBuffer simCpb, Object message);

}

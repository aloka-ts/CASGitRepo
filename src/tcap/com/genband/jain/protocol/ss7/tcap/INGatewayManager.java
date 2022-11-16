package com.genband.jain.protocol.ss7.tcap;

import java.util.Iterator;

public interface INGatewayManager {
	
	public Iterator<INGateway> getAllINGateways();
	
	public INGateway getINGateway(String id);
	
	public void inGatewayUp(String id);
	
	public void inGatewayDown(String id);
}

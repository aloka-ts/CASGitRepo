package com.baypackets.ase.spi.container;

import java.io.IOException;

public interface SasMessageContext {

	public String getId();
	
	public String getObjectName();
	
	public String getVersion();
	
	public String getProtocol();
	
	public SasMessageCallback getMessageCallback();
	
	public void sendMessage(SasMessage message) throws IOException;
}

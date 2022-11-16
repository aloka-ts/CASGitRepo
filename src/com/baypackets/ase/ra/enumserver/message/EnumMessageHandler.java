package com.baypackets.ase.ra.enumserver.message;

import com.baypackets.ase.resource.MessageHandler;

public interface EnumMessageHandler extends MessageHandler
{

	public void receiveEnumMessage(EnumMessage m);

	public void receiveEnumMessage(EnumRequest request);
	
}

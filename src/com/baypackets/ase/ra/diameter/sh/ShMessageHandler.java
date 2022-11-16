package com.baypackets.ase.ra.diameter.sh;

import com.baypackets.ase.resource.MessageHandler;
import com.baypackets.ase.resource.ResourceException;

public interface ShMessageHandler extends MessageHandler
{

	public void doUDR(ShRequest shrequest)throws ResourceException;
	
	public void doPUR(ShRequest shrequest)throws ResourceException;
	
	public void doSNR(ShRequest shrequest)throws ResourceException;

}

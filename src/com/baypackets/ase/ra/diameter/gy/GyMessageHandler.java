package com.baypackets.ase.ra.diameter.gy;

import com.baypackets.ase.resource.MessageHandler;
import com.baypackets.ase.resource.ResourceException;

public interface GyMessageHandler extends MessageHandler
{

	public void handleEventCCRRequest(CreditControlRequest request)throws ResourceException;
	
	public void handleInitialCCRRequest(CreditControlRequest request)throws ResourceException;
	
	public void handleInterimCCRRequest(CreditControlRequest request)throws ResourceException;

	public void handleTerminationCCRRequest(CreditControlRequest request)throws ResourceException;
	
}
package com.baypackets.ase.ra.diameter.rf;

import com.baypackets.ase.resource.MessageHandler;
import com.baypackets.ase.resource.ResourceException;

public interface RfMessageHandler extends MessageHandler
{

	public void handleEventRecordRequest(RfAccountingRequest request)throws ResourceException;
	
	public void handleStartRecordRequest(RfAccountingRequest request)throws ResourceException;
	
	public void handleInterimRecordRequest(RfAccountingRequest request)throws ResourceException;

	public void handleStopRecordRequest(RfAccountingRequest request)throws ResourceException;
	
}

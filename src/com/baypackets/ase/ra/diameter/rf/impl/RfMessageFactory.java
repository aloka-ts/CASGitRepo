package com.baypackets.ase.ra.diameter.rf.impl;

import com.baypackets.ase.ra.diameter.rf.RfAccountingRequest;
import com.baypackets.ase.ra.diameter.rf.RfResourceException;
import com.baypackets.ase.ra.diameter.rf.enums.AccountingRecordTypeEnum;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.MessageFactory;

public interface RfMessageFactory extends MessageFactory {

	public SasMessage createResponse(SasMessage request) throws RfResourceException;

	/**
	 * Creating ProfileUpdateRequest message.
	 * @param destinationRealm
	 * @param destinationHost
	 * @return
	 */
	public RfAccountingRequest createAccountingRequest(SasProtocolSession session, 
			int type)
	throws ResourceException;
}

package com.baypackets.ase.ra.diameter.gy.impl;

import com.baypackets.ase.ra.diameter.gy.CreditControlRequest;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.AccountingRecordTypeEnum;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.MessageFactory;

public interface GyMessageFactory extends MessageFactory {

	public SasMessage createResponse(SasMessage request) throws GyResourceException;

	/**
	 * Creating ProfileUpdateRequest message.
	 * @param destinationRealm
	 * @param destinationHost
	 * @return
	 */
	public CreditControlRequest createCreditControlRequest(SasProtocolSession session, 
			int type)
	throws ResourceException;
}


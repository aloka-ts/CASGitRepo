package com.baypackets.ase.ra.diameter.ro.impl;

import com.baypackets.ase.ra.diameter.ro.CreditControlRequest;
import com.baypackets.ase.ra.diameter.ro.RoResourceException;
import com.baypackets.ase.ra.diameter.ro.enums.AccountingRecordTypeEnum;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.MessageFactory;

import fr.marben.diameter._3gpp.ro.DiameterRoMessageFactory;

public interface RoMessageFactory extends MessageFactory {

	public SasMessage createResponse(SasMessage request) throws RoResourceException;

	/**
	 * Creating ProfileUpdateRequest message.
	 * @param destinationRealm
	 * @param destinationHost
	 * @return
	 */
	public CreditControlRequest createCreditControlRequest(SasProtocolSession session, 
			int type)
	throws ResourceException;
	
	public DiameterRoMessageFactory getDiameterRoMessageFactory();
}

package com.baypackets.ase.ra.diameter.sh.impl;

import com.baypackets.ase.ra.diameter.sh.ShProfileUpdateRequest;
import com.baypackets.ase.ra.diameter.sh.ShResourceException;
import com.baypackets.ase.ra.diameter.sh.ShSubscribeNotificationRequest;
import com.baypackets.ase.ra.diameter.sh.ShUserDataRequest;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.MessageFactory;

import fr.marben.diameter._3gpp.sh.DiameterShMessageFactory;

public interface ShMessageFactory extends MessageFactory {

	public SasMessage createResponse(SasMessage request) throws ShResourceException;

	/**
	 * Creating ProfileUpdateRequest message.
	 *
	 * @return
	 */
	ShProfileUpdateRequest createProfileUpdateRequest(SasProtocolSession session) throws ResourceException;

	/**
	 * Creating SubscribeNotificationsRequest message.
	 *
	 * @return
	 */
	ShSubscribeNotificationRequest createSubscribeNotificationsRequest(SasProtocolSession session) throws ResourceException;

	/**
	 * Creating UserDataRequest message.
	 *
	 * @return
	 */
	ShUserDataRequest createUserDataRequest(SasProtocolSession session,String realm,String msisdn) throws ResourceException;
	
	public SasMessage createRequest(SasProtocolSession session, int type,String remoteRealm,String msisdn) throws ResourceException;

	public DiameterShMessageFactory getDiameterShMessageFactory();
}
	//public  void setDiameterShMsgFactory(DiameterShMessageFactory shMsgFactory);

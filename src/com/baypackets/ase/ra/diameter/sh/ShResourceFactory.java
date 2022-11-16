package com.baypackets.ase.ra.diameter.sh;

import javax.servlet.sip.SipApplicationSession;

import com.baypackets.ase.resource.DefaultResourceFactory;
import com.baypackets.ase.resource.ResourceException;

public interface ShResourceFactory extends DefaultResourceFactory {

	ShProfileUpdateRequest createProfileUpdateRequest(SipApplicationSession appSession) throws ResourceException;

	ShSubscribeNotificationRequest createSubscribeNotificationsRequest(SipApplicationSession appSession) throws ResourceException;

	ShUserDataRequest createUserDataRequest(SipApplicationSession appSession,String realm,String msisdn) throws ResourceException;

}

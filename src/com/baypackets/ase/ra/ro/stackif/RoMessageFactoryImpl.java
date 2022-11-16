package com.baypackets.ase.ra.ro.stackif;

import org.apache.log4j.Logger;

import com.condor.apncommon.DiameterBaseInfo;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.resource.MessageFactory;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.ra.ro.*;
import com.baypackets.ase.ra.ro.impl.RoSession;

public class RoMessageFactoryImpl implements MessageFactory, Constants {
	private static Logger logger = Logger.getLogger(RoMessageFactoryImpl.class);
	private ResourceContext context;

	public void init(ResourceContext context) throws ResourceException {
		this.context = context;
	}

	public SasMessage createRequest(SasProtocolSession session, int type)
		throws ResourceException {

		return new CreditControlRequestImpl((RoSession)session, type);
	}

	public SasMessage createResponse(SasMessage request, int type) throws ResourceException {
		return null;
	}

	public static RoResponse createResponse(	DiameterBaseInfo stackResp,
												RoSession session,
												RoRequest req)
		throws ResourceException {
		return new CreditControlAnswerImpl(stackResp, session, (CreditControlRequestImpl)req);
	}

	@Override
	public SasMessage createRequest(SasProtocolSession session, int type,
			String remoteRealm) throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SasMessage createRequest(SasProtocolSession session, int type,
			String remoteRealm, String msisdn) throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}
}

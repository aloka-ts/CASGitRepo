package com.baypackets.ase.sbbdeployment;

import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;
import com.baypackets.ase.container.exceptions.AseInvocationFailedException;
import com.baypackets.ase.deployer.AbstractDeployableObject;
import com.baypackets.ase.spi.container.SasMessage;


public class SbbContext extends AbstractDeployableObject {
	private static final long serialVersionUID = -3146342646478497223L;
	@Override
	public SipApplicationSession createApplicationSession(
			String protocol, String sessionId) {
		// TODO Auto-generated method stub [NOT REQUIRED FOR SBB]
		return null;
	}

	@Override
	public void processMessage(SasMessage message)
			throws AseInvocationFailedException, ServletException {
		// TODO Auto-generated method stub [NOT REQUIRED FOR SBB]
		
	}	
}

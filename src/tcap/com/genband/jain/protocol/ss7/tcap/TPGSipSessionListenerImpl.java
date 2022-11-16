package com.genband.jain.protocol.ss7.tcap;

import javax.servlet.sip.SipSessionEvent;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipSessionActivationListener;

public class TPGSipSessionListenerImpl implements SipSessionActivationListener
{
	public void sessionDidActivate(SipSessionEvent se)
	{
		SipSession ss = se.getSession();
		TcapProviderGateway tpg = (TcapProviderGateway)ss.getAttribute(TcapProviderGateway.class.getName());
		if (tpg != null)
		{
			SipApplicationSession sas = ss.getApplicationSession();
			TcapProviderGateway.addInstance(tpg, sas, ss);
		}
	}

	public void sessionWillPassivate(SipSessionEvent se)
	{
	}
}


package com.baypackets.ase.sipconnector;

import java.util.TimerTask;

import org.apache.log4j.Logger;

public class AseProxyTimer extends TimerTask{
	private static transient Logger logger = Logger.getLogger(AseProxyTimer.class);
	private AseProxyBranch proxyBranch;

	public AseProxyTimer(AseProxyBranch p_proxyBranch)
	{
		this.proxyBranch = p_proxyBranch;
	}

	public void run()
	{
		if (logger.isDebugEnabled())logger.debug("Timer timed out for branch: "+ proxyBranch.toString());
		try {
			proxyBranch.onTimeout();
		} catch (Exception e) {
			logger.error("Problem in timeout task", e);
		}
	}
}

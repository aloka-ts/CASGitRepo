package com.baypackets.ase.ra.smpp.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.baypackets.ase.ra.smpp.stackif.Constants;
import com.baypackets.ase.ra.smpp.stackif.SmscSession;

public class SMScPollingConnection implements Runnable {

	private static Logger logger = Logger.getLogger(SMScPollingConnection.class);
	private SmscSession smscSession;

	private static final int waitTime = 60000;
	
	public SmscSession getSmscSession() {
		return smscSession;
	}

	public void setSmscSession(SmscSession smscSession) {
		this.smscSession = smscSession;
	}

	@Override
	public void run() {
		try {
			logger.debug("Starting thread for  SMScPollingConnection for remaining smcsSession:- " + smscSession);

			int count = 0;
			while (true) {
				smscSession.makeSmscConnection();
				
				logger.debug("Currently trying to connect with :- " +smscSession.toString() +" for the "+ count + " th time");
				if (smscSession.isBound()
						&& StringUtils.equalsIgnoreCase(smscSession.getCurrentStatus(), Constants.STATUS_ACTIVE)) {
					logger.debug("Session is now active again :- " + smscSession);
					SmppConfMgr.currentSmcsDownList.remove(smscSession);
					break;
				}
				count++;
				Thread.sleep(waitTime);
			}

			
		} catch (Exception e) {
			logger.error("SMScPollingConnection error occured :- " + e);
		}

	}

}

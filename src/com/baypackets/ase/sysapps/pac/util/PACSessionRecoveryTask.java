package com.baypackets.ase.sysapps.pac.util;

import java.util.TimerTask;
import org.apache.log4j.Logger;

import com.baypackets.ase.sysapps.pac.adaptors.SIPPACAdaptor;
import com.baypackets.ase.sysapps.pac.dao.rdbms.PACDAOImpl;

/**
 * 
 * This class is used to recover session after FT. This will create session for users whose sessions were not recovered 
 * by sessionDidActivate callback by PAC Application. 
 *   
 */
public class PACSessionRecoveryTask  extends TimerTask{
	private static Logger logger = Logger.getLogger(PACSessionRecoveryTask.class);
	

	
	/**
	 * Constructor for PACSessionRecovery Task.
	 * @param channelDO
	 */
	public PACSessionRecoveryTask() {
		
	}
	
	@Override
	public void run() {
		logger.error("Inside run() method ....");
		PACDAOImpl dao=new PACDAOImpl();
		try{
			dao.checkAndRecoverSessions(SIPPACAdaptor.SIP_CHANNEL);
		}catch (Exception e) {
			logger.error("Exception in checkAndRecoverSessions() ", e);
		}
		logger.error("Exitting run() method ....");
	}
	
	
}

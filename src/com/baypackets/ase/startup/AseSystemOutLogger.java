/*******************************************************************************
 *   Copyright (c) 2020 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
package com.baypackets.ase.startup;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

public class AseSystemOutLogger extends Thread {
	
	private static Logger logger = Logger.getLogger(AseSystemOutLogger.class);
	
	boolean m_sr_stopped=false;
	
	long timestampHeartbeatInterval=2000;
	

	public AseSystemOutLogger(String _threadName) {
		super(_threadName);
		
		ConfigRepository repository = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);

		String heartbeatInt = repository.getValue(Constants.CAS_SYSOUT_LOGGER_INTERVAL);
		if (heartbeatInt != null && !heartbeatInt.isEmpty()) {
			timestampHeartbeatInterval = Long.parseLong(heartbeatInt);
		}
	}

	public void run() {
		System.out.println("AseSystemOutLogger is started. with logger interval is "+timestampHeartbeatInterval);

		try {
			while (!this.m_sr_stopped) {
				try {
					// Set thread state to idle before blocking on readLine
					//this.setThreadState(MonitoredThreadState.Idle);	
					
					System.out.println("AseSystemOutLogger-->Current Time is: "+ getCurrentLocalDateTimeStamp());
					
					Thread.currentThread().sleep(timestampHeartbeatInterval);
				}catch (Exception e) {
					logger.error("" , e);
					// continue;
				}
			 // while
		  }
		}finally {
			// Unregister thread with thread monitor
		}
		System.out.println("AseSystemOutLogger shuting down...");
	}
	
	public String getCurrentLocalDateTimeStamp() {
	    return LocalDateTime.now()
	       .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
	}


}

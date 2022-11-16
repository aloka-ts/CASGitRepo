/**
 * @(#)file      AseThreadMonitor.java
 * @(#)author    Neeraj Jain, BayPackets Inc.
 * @(#)version   0.1
 * @(#)date		 May 18, 2005
 *
 * Copyright 2000 BayPackets, Inc. All rights reserved.
 * This software is the proprietary information of BayPacketss, Inc.
 * Use is subject to license terms.
 */
package com.baypackets.ase.util;

import org.apache.log4j.Logger;

import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;

public class AseThreadMonitor extends ThreadMonitor implements MComponent
{
	public AseThreadMonitor(ConfigRepository configRepos) {
		if(logger.isDebugEnabled())
		logger.debug("Creating ASE Thread Monitor");
		configRepository = configRepos ;
		this.state = MComponentState.STOPPED;
	}

	public void changeState(MComponentState componentState)
		throws UnableToChangeStateException {
		if(logger.isDebugEnabled())
		logger.debug("changeState(MComponentState): enter");

		switch(componentState.getValue()) {
			case MComponentState.LOADED:
				if(state == MComponentState.STOPPED) { 
					if(logger.isDebugEnabled())
					logger.debug("Component State: STOPPED ==> LOADED");

					try {
						// Get configured value for thread timeout time
						String timeout =
							configRepository.getValue(Constants.PROP_MT_MONITOR_THREAD_TIMEOUT);
						if(timeout != null) {
							threadTimeoutTime = Integer.parseInt(timeout);
						}

						if(logger.isDebugEnabled()) {
							logger.debug("Thread timeout time is : " + threadTimeoutTime);
						}

						// Get configured value for system-restart on thread timeout flag
						String isSysResReqStr =
						configRepository.getValue(Constants.PROP_MT_MONITOR_RESTART_ON_EXPIRY);

						boolean isSysResReq = false;

						if(isSysResReqStr != null
						&& Integer.parseInt(isSysResReqStr) == 1) {
							isSysResReq = true;
							if(logger.isDebugEnabled())
							logger.debug("System restart on thread blocking is enabled");
						}

						initialize(BaseContext.getTraceService() ,isSysResReq );
						state = MComponentState.LOADED;
					} catch(Exception e) {
						logger.error("Exception in Loading Thread Monitor", e);
						throw new UnableToChangeStateException("Cannot change Thread Monitor state");
					}
				} else {
					logger.error("Illegal State Received");
					throw new UnableToChangeStateException("Illegal State Received");
				}
				break;

			case MComponentState.RUNNING:
				if(state == MComponentState.LOADED) {
					if(logger.isDebugEnabled())
					logger.debug("Component State: LOADED ==> RUNNING");

					start();

					state = MComponentState.RUNNING;
				} else {
					logger.error("Illegal State Received");
					throw new UnableToChangeStateException("Illegal State Received");
				}
				break;
 
				//To handle the Soft Stop state
			case MComponentState.SOFT_STOP:
				if (state == MComponentState.RUNNING) {
					if (logger.isDebugEnabled()){
						logger.debug("Component State: RUNNING ==> SOFT STOP");
					}
				state = MComponentState.SOFT_STOP;
				if (logger.isDebugEnabled())
					logger.debug("Component State changed to SOFT STOP . Do Nothing");
				}else {
				logger.error("Illegal State Received");
				throw new UnableToChangeStateException("Illegal State Received");
				}
				break;
				
			case MComponentState.STOPPED:
				if(state == MComponentState.RUNNING) {
					if(logger.isDebugEnabled())
					logger.debug("Component State: RUNNING ==> STOPPED");

					stop();

					state = MComponentState.STOPPED;
				} else if (state == MComponentState.SOFT_STOP){
			    	if(logger.isDebugEnabled()){
			    		logger.debug("Component State: SOFT_STOP ==> STOPPED");
			    	}
			    this.stop();
			    
				state = MComponentState.STOPPED;
				}else {
					logger.error("Illegal State Received");
					throw new UnableToChangeStateException("Illegal State Received");
				}
				break;

			default:
				logger.error("Illegal State Received");
				throw new UnableToChangeStateException("Illegal State Received");
		}// switch
		if(logger.isDebugEnabled())
		logger.debug("changeState(MComponentState): exit");
	}

	public void updateConfiguration(Pair[] configData, OperationType optype)
		throws UnableToUpdateConfigException {
		if(logger.isDebugEnabled())	
		logger.debug("updateConfiguration(Pair[], OperationType): called");
	}

	public static int getThreadTimeoutTime() {
		if(logger.isDebugEnabled()) {
			logger.debug("Returning thread timeout time = " + threadTimeoutTime);
		}

		return threadTimeoutTime;
	}

	private ConfigRepository configRepository = null;
	private int state = -1;
	private static int threadTimeoutTime = 100;
	private static Logger logger = Logger.getLogger(AseThreadMonitor.class.getName());
}

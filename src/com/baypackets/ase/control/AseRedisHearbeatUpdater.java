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
package com.baypackets.ase.control;

import org.apache.log4j.Logger;

import com.agnity.redis.client.RedisWrapper;
import com.agnity.redis.exeption.RedisLettuceCommandTimeoutException;
import com.agnity.redis.exeption.RedisLettuceConnectionException;
import com.baypackets.ase.common.RedisAlarmHandler;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.AseAlarmService;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.ThreadAlreadyRegisteredException;
import com.baypackets.bayprocessor.slee.common.ThreadNotRegisteredException;
import com.baypackets.bayprocessor.slee.internalservices.TraceService;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThread;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThreadState;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadOwner;

public class AseRedisHearbeatUpdater extends MonitoredThread {
	
	private static Logger logger = Logger.getLogger(AseRedisHearbeatUpdater.class);
	
	private RedisWrapper redisWrapper = null;
	
	private ThreadOwner threadOwner=null;
	
	boolean m_sr_stopped=false;
	String selfId=null;
	
	long timestampHeartbeatInterval=1000;
	
	private ThreadMonitor threadMonitor = (ThreadMonitor) Registry
			.lookup(Constants.NAME_THREAD_MONITOR);

	private AseAlarmService alarmService;

	public AseRedisHearbeatUpdater(String _threadName, long _timeOutTime,
			TraceService _traceService) {
		super(_threadName, _timeOutTime, _traceService);
		
		this.redisWrapper = (RedisWrapper) Registry.lookup(Constants.REDIS_WRAPPER);
		
		this.alarmService = (AseAlarmService)Registry.lookup(Constants.NAME_ALARM_SERVICE);
		
		ControlManager controlMgr = (ControlManager) Registry.lookup(Constants.NAME_CONTROL_MGR);
		
		selfId=controlMgr.getSelfInfo().getId();

		ConfigRepository repository = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);

		String heartbeatInt = repository.getValue(Constants.CAS_TIMESTAMP_HEARBEAT_INTERVAL);
		if (heartbeatInt != null && !heartbeatInt.isEmpty()) {
			timestampHeartbeatInterval = Long.parseLong(heartbeatInt);
		}
	}

	@Override
	public ThreadOwner getThreadOwner() {
		// TODO Auto-generated method stub
		return threadOwner;
	}
	
	public void setThreadOwner(ThreadOwner owner){
		threadOwner=owner;
	}
	
	public void start() {
		super.start();
	}
	
	public void stopIt(){
		super.shutdown();
		m_sr_stopped=true;
	}

	public void run() {
		if (logger.isInfoEnabled())
			logger.info("AseRedisHearbeatUpdater is started. with hearbeat interval as "+timestampHeartbeatInterval);

		// Register thread with thread monitor
		try {
			// Set thread state to idle before registering
			this.setThreadState(MonitoredThreadState.Idle);

			threadMonitor.registerThread(this);
		} catch (ThreadAlreadyRegisteredException exp) {
			logger.error(
					"This thread is already registered with Thread Monitor",
					exp);
		}
		try {
			while (!this.m_sr_stopped) {
				try {
					// Set thread state to idle before blocking on readLine
					this.setThreadState(MonitoredThreadState.Idle);	
					
					try {
						redisWrapper.getHashOperations().addInHashes(ClusterManager.CAS_INSTANCE_HEARTBEAT, selfId,""+System.currentTimeMillis());
						
						RedisAlarmHandler.redisIsAccessible(selfId);
						
					} catch(RedisLettuceConnectionException e){
						logger.error("exception while writing headrtbeat in redis " +e );
						
						RedisAlarmHandler.redisNotAccessible(selfId);
					}catch(RedisLettuceCommandTimeoutException e){
						logger.error("exception while writing headrtbeat in redis " +e );
						RedisAlarmHandler.redisNotAccessible(selfId);
					}
					
					Thread.currentThread().sleep(timestampHeartbeatInterval);
				}catch (Exception e) {
					logger.error("" , e);
					// continue;
				}
			 // while
		  }
		}finally {
			// Unregister thread with thread monitor
			try {
				threadMonitor.unregisterThread(this);
			} catch (ThreadNotRegisteredException exp) {
				logger.error(
						"This thread is not registered with Thread Monitor",
						exp);
			}
		}
		if (logger.isInfoEnabled())
			logger.info("AseRedisHearbeatUpdater shuting down...");
	}


}

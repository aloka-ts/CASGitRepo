package com.baypackets.ase.common;

import org.apache.log4j.Logger;

//import com.baypackets.ase.control.ClusterManager;
import com.baypackets.ase.util.AseAlarmService;
import com.baypackets.ase.util.Constants;
//import com.baypackets.ase.control.ClusterManager;
//import com.baypackets.ase.startup.AseMain;
import com.baypackets.bayprocessor.slee.common.BaseContext;

public class RedisAlarmHandler {

	private static final Logger logger = Logger
			.getLogger(RedisAlarmHandler.class);

	private static RedisAlarmHandler alarmHandler = new RedisAlarmHandler();

	static boolean alarmRaised = false;

	private static AseAlarmService alarmService;

	private RedisAlarmHandler() {
		this.alarmService = (AseAlarmService) Registry
				.lookup(Constants.NAME_ALARM_SERVICE);
	}

	public static RedisAlarmHandler getInstance() {

		return alarmHandler;
	}

	public static void redisNotAccessible(String selfId) {

		try {
			String shutdowncas = BaseContext.getConfigRepository().getValue(
					Constants.PROP_REDIS_SHUTDOWN_CAS);
			if ("1".equals(shutdowncas)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Shutdown cas is enabled so shutdown cas "
							+ selfId);
				}
				if (logger.isDebugEnabled()) {
					logger
						.debug("initiateShutdown on redis connect failure");
				}
//				ClusterManager clusterMgr = (ClusterManager) Registry.lookup(Constants.NAME_CLUSTER_MGR);
//				try{
//					clusterMgr.shutdown();
//				}catch(Exception e){
//					logger.error("Error Initiating shutdown", e);
//				}finally{
//					logger.error("Calling System.exit");
//					System.exit(1);
//				}//@end try catch finally.			
				}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (!alarmRaised) {
			if (logger.isDebugEnabled()) {
				logger.debug("Raise redisNotAccessible.. for " + selfId);
			}
			alarmService.raiseAlarm(Constants.ALARM_REDIS_NOT_REACHABLE,
					"Redis is currently not accessible from " + selfId);

			alarmRaised = true;
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Alarm " + Constants.ALARM_REDIS_NOT_REACHABLE
						+ " is already raised ");
			}
		}
	}

	public static void redisIsAccessible(String selfId) {

//		if (logger.isDebugEnabled()) {
//			logger.debug("Raise redisIsAccessible..  "
//					+ selfId);
//		}
		if (alarmRaised) {	
			if (logger.isDebugEnabled()) {
			logger.debug("Raise redisIsAccessible..  "
					+ selfId);
		}
			alarmService.raiseAlarm(Constants.ALARM_REDIS_IS_REACHABLE,
					"Redis is accessible now from " + selfId);
			alarmRaised = false;

		} else {
//			if (logger.isDebugEnabled()) {
//				logger.debug("No pending Alarm  "
//						+ Constants.ALARM_REDIS_NOT_REACHABLE +" No need to raise "+Constants.ALARM_REDIS_IS_REACHABLE);
//			}
		}
	}

}

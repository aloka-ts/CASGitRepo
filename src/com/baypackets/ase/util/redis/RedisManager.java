package com.baypackets.ase.util.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseBackgroundProcessor;
import com.baypackets.ase.common.BackgroundProcessListener;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseEngine;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.threadpool.Queue;
import com.baypackets.ase.util.threadpool.ThreadPool;
import com.baypackets.ase.util.threadpool.WorkHandler;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThread;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadOwner;
import com.dynamicsoft.DsLibs.DsUtil.DsWorkQueue;
/**
 * This class is currently used for replicating data in redis its not used for other redis operations
 * as other operations are one time operations
 * @author reeta
 *
 */
public class RedisManager implements WorkHandler, ThreadOwner,BackgroundProcessListener, MComponent{

	private static Logger _logger = Logger.getLogger(RedisManager.class);
	private ThreadPool threadPool = null;
	private static int poolSize = 10;
	
	String replEnabled="";

	/**
	 * Method from MComponent Interface.
	 */
	public void changeState(MComponentState state)
			throws UnableToChangeStateException {
		try {
			if (_logger.isInfoEnabled()) {
				_logger.info("Entering changeState(): state = "
						+ state.getValue());
			}
			if (state.getValue() == MComponentState.LOADED) {
				this.initialize();
			} else if (state.getValue() == MComponentState.RUNNING) {
				this.start();
			} else if (state.getValue() == MComponentState.STOPPED) {
				this.stop();
			}
		} catch (Exception exp) {
			_logger.error(exp.getMessage(), exp);
		}
	}

	public void updateConfiguration(Pair[] configData, OperationType opType)
			throws UnableToUpdateConfigException {
		// No op
	}

	private void initialize() {
		if (_logger.isInfoEnabled()) {
			_logger.info(" Creating RedisManager ThreadPool");
		}
		try {
			
			replEnabled=BaseContext.getConfigRepository().getValue(Constants.PROP_REPLICATION_ENABLED);
			
			if("0".equals(replEnabled)){
				
				if (_logger.isInfoEnabled()) {
					_logger.info("returning as replication is disabled");
				}
				return;
			}
			ConfigRepository configRepository = (ConfigRepository) Registry
					.lookup(Constants.NAME_CONFIG_REPOSITORY);
			String tmpQSize = (String) configRepository
					.getValue(Constants.REDIS_QUEUE_SIZE);
			String poolSizeStr = (String) configRepository
					.getValue(Constants.REDIS_POOL_SIZE);
			
			if(poolSizeStr!=null && !poolSizeStr.isEmpty()){
				poolSize=Integer.parseInt(poolSizeStr);
			}
			
			if (_logger.isInfoEnabled()) {
				_logger.info("Initializing threadpool with size "+poolSize);
			}
			this.threadPool = new ThreadPool(poolSize, false, "RedisManager",
					this, this, 100);
			ThreadMonitor tm = (ThreadMonitor) Registry
					.lookup(Constants.NAME_THREAD_MONITOR);
			this.threadPool.setThreadMonitor(tm);
			if (tmpQSize != null&& !tmpQSize.isEmpty() ) {
				this.threadPool.setMaxQueueSize(Integer.parseInt(tmpQSize));
			}
			this.threadPool.setPolicy(Queue.POLICY_DROP_LAST);
			
			registerForBackgroundProcess();

			if (_logger.isInfoEnabled()) {
				_logger.info("Leaving");
			}
			// this.threadPool.start();
		} catch (Exception e) {
			_logger.error(e.getMessage(), e);
		}
	}
	
	private void registerForBackgroundProcess()  {
		ConfigRepository config = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		
		long dumpPeriod=60;
		try {
			String str = config.getValue(Constants.REDIS_QUEUE_DUMP_INTERVAL);
			if (str != null)  {
				dumpPeriod = (long)Long.parseLong(str);
			} 
		} catch (Exception e) {
			_logger.error("exception "+e);
		}

		try  {
			AseBackgroundProcessor processor = (AseBackgroundProcessor) Registry.lookup(Constants.BKG_PROCESSOR);
			processor.registerBackgroundListener(this, dumpPeriod);
		} catch (Exception e)  {
			_logger.error(e.getMessage(), e);
		}
	}

	private void start() {
		if (_logger.isDebugEnabled()) {
			_logger.debug("start() ");
		}
		if("0".equals(replEnabled)){
			
			if (_logger.isInfoEnabled()) {
				_logger.info("returning as replication is disabled");
			}
			return;
		}
		if (_logger.isDebugEnabled()) {
			_logger.debug("starting threadpool ");
		}
		this.threadPool.start();
	}

	private void stop() {
		if (_logger.isDebugEnabled()) {
			_logger.debug("stop()");
		}
		if("0".equals(replEnabled)){
			
			if (_logger.isInfoEnabled()) {
				_logger.info("returning as replication is disabled");
			}
			return;
		}
		this.threadPool.shutdown();
	}
	
	/**
	 * Enque task in redis
	 * @param task
	 * @return
	 */
	public void enqueueTask(RedisTask task){
		this.threadPool.submit(task.getIndex(), task);
	}
	
	/**
	 * Callback as thread owner.
	 */
	public int threadExpired(MonitoredThread thread) {
		if(_logger.isInfoEnabled()) {
			_logger.info(thread.getName() + " expired");
		}

		// Calling ThreadPool's method as logic for min percentage of common
		// thread required, lies there
		return threadPool.threadExpired(thread);
	}

	@Override
	public void execute(Object task) {
		// TODO Auto-generated method stub
		
		if(task instanceof RedisTask){
			
			RedisTask redTask=(RedisTask)task;
			redTask.execute();
			
		}
		
	}
	
	/**
	 * This method is used to print redis queue
	 * @param currentTime
	 */
	public void process(long currentTime) {

		ArrayList redisQ = threadPool.getQueueList();

		StringBuffer buff = new StringBuffer();

		// Adding stack Queue
		buff.append("REDIS_QUEUE:");

		for (int i = 0; i < redisQ.size(); i++) {
			Queue queue = (Queue) redisQ.get(i);
			if (queue != null) {
				buff.append(queue.size());
				buff.append(AseStrings.COMMA);
			} else {
				_logger.error("QUEUE is null");
			}
		}

		String val = buff.substring(0, buff.lastIndexOf(AseStrings.COMMA));
		_logger.error(val);
	}

}

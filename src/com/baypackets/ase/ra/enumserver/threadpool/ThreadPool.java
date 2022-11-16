
package com.baypackets.ase.ra.enumserver.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * 
 *
 */
public class ThreadPool implements ThreadPoolManager{

	private static final Logger logger = Logger.getLogger(ThreadPool.class);

	/**
	 *  ExecutorService refernce
	 *  will refr to Executor service implementation(cached thread pool)
	 *  over which decorator is written
	 */
	private ThreadPoolExecutor es;

	private int corePoolSize;
	private int maxPoolSize;
	private long timeout;
	private BlockingQueue<Runnable> queue;

	/**
	 * creates cached pool implementation
	 */
	public ThreadPool(){
		corePoolSize=0;
		maxPoolSize=2147483647;
		timeout=60L;
		queue=new SynchronousQueue<Runnable>();
	}

	/**
	 * creates
	 * @param poolSize size of core pool
	 */
	public ThreadPool(int poolSize){
		corePoolSize=poolSize;
		maxPoolSize=poolSize;
		timeout=60L;
		queue=new LinkedBlockingQueue<Runnable>();
	}



	/**
	 * Methods creates cache thread pool for thread manager
	 * (non-Javadoc)
	 * @see com.baypackets.ase.ra.http.workmanager.ThreadPoolManager#startPool()
	 */
	@Override
	public void startPool() {
		if(es == null || es.isShutdown() || es.isTerminated()){
			es = new ThreadPoolExecutor(corePoolSize, maxPoolSize, timeout, TimeUnit.SECONDS, queue);
			es.allowCoreThreadTimeOut(true);
		}
		else{
			logger.error("Pool already running");
		}
		if(logger.isInfoEnabled())
			logger.info("thread pool created");
	}

	/**
	 * calls shutdown method on thread pool 
	 * (non-Javadoc)
	 * @see com.baypackets.ase.ra.http.workmanager.ThreadPoolManager#stopPool()
	 */
	@Override
	public void stopPool() {
		if(logger.isInfoEnabled())
			logger.info("shutting down thread pool");
		es.shutdownNow();
		if(logger.isInfoEnabled())
			logger.info("shut down complete");
	}

	/**
	 * excutes task in executorservice
	 * (non-Javadoc)
	 * @see com.baypackets.ase.ra.http.workmanager.TaskManager#executeTask(java.lang.Runnable)
	 */
	@Override
	public void executeTask(Runnable paramRunnable) {
		if(logger.isInfoEnabled())
			logger.info("Execute task in  thread pool");
		es.execute(paramRunnable);


	}

	/**
	 * submits task in executorservice
	 * (non-Javadoc)
	 * @see com.baypackets.ase.ra.http.workmanager.TaskManager#submitTask(java.lang.Runnable)
	 */
	@Override
	public Future<?> submitTask(Runnable paramRunnable) {
		if(logger.isInfoEnabled())
			logger.info("Submit task in  thread pool");
		return es.submit(paramRunnable);
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.ra.http.workmanager.ThreadPoolManager#setPoolSize(int)
	 */
	@Override
	public void incrementPoolSize(int incrementBy) {
		int size=es.getCorePoolSize()+incrementBy;
		es.setCorePoolSize(size);
		es.setMaximumPoolSize(es.getCorePoolSize());
	}

	/**
	 * decrease pool size by specified value
	 * if net size is less than 0 set pool size to 0
	 *  (non-Javadoc)
	 * @see com.baypackets.ase.ra.http.workmanager.ThreadPoolManager#decrementPoolSize(int)
	 */
	@Override
	public void decrementPoolSize(int decrementBy) {
		int size=es.getCorePoolSize()+decrementBy;
		if(size>=0){
			es.setCorePoolSize(size);
			es.setMaximumPoolSize(es.getCorePoolSize());
		}else{
			es.setCorePoolSize(0);
			es.setMaximumPoolSize(es.getCorePoolSize());
		}

	}


}

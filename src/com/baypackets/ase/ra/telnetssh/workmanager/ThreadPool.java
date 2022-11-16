/**
 * Decorator over executor of cached thread pool 
 * with limited functionality
 */
package com.baypackets.ase.ra.telnetssh.workmanager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.baypackets.ase.util.AseStrings;

import java.util.concurrent.ThreadFactory;

/**
 * @author saneja
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
	private String poolPrefix;
	private BlockingQueue<Runnable> queue;

	/**
	 * creates cached pool implementation
	 * @param poolPrefix Prefix string for thread name.
	 */
	public ThreadPool(String poolPrefix){
		if(poolPrefix==null || poolPrefix.trim().isEmpty()){
			throw new IllegalArgumentException("Pool PreFix can not be NULL or blank..");
		}
		this.poolPrefix=poolPrefix;
		corePoolSize=0;
		maxPoolSize=2147483647;
		timeout=60L;
		queue=new SynchronousQueue<Runnable>();
	}

	 
	/**
	 * This creates a thread pool of given size.
	 * @param poolPrefix Prefix string for thread name.
	 * @param poolSize core size of pool
	 */
	public ThreadPool(String poolPrefix,int poolSize){
		if(poolPrefix==null || poolPrefix.trim().isEmpty()){
			throw new IllegalArgumentException("Pool PreFix can not be null or blank..");
		}
		this.poolPrefix=poolPrefix;
		corePoolSize=poolSize;
		maxPoolSize=poolSize;
		timeout=60L;
		queue=new LinkedBlockingQueue<Runnable>();
	}



	/**
	 * Methods creates cache thread pool for thread manager
	 * (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.workmanager.ThreadPoolManager#startPool()
	 */
	@Override
	public void startPool() {
		if(es == null || es.isShutdown() || es.isTerminated()){
			CustomThreadFactory customThreadFactory=new CustomThreadFactory(poolPrefix);
			es = new ThreadPoolExecutor(corePoolSize, maxPoolSize, timeout, TimeUnit.SECONDS, queue,customThreadFactory);
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
	 * @see com.baypackets.ase.ra.telnetssh.workmanager.ThreadPoolManager#stopPool()
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
	 * @see com.baypackets.ase.ra.telnetssh.workmanager.TaskManager#executeTask(java.lang.Runnable)
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
	 * @see com.baypackets.ase.ra.telnetssh.workmanager.TaskManager#submitTask(java.lang.Runnable)
	 */
	@Override
	public Future<?> submitTask(Runnable paramRunnable) {
		if(logger.isInfoEnabled())
			logger.info("Submit task in  thread pool:"+poolPrefix+":"+queue.size());
		return es.submit(paramRunnable);
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.workmanager.ThreadPoolManager#setPoolSize(int)
	 */
	@Override
	public void incrementPoolSize(int incrementBy) {
		int size=es.getCorePoolSize()+incrementBy;
		es.setCorePoolSize(size);
		es.setMaximumPoolSize(es.getCorePoolSize());
		logger.error("New core size of "+poolPrefix+" pool :"+es.getCorePoolSize()+"New max size of "+poolPrefix+" pool :"+es.getMaximumPoolSize());
		//logger.error("New max size of "+poolPrefix+" pool :"+es.getMaximumPoolSize());
	}

	/**
	 * decrease pool size by specified value
	 * if net size is less than 0 set pool size to 0
	 *  (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.workmanager.ThreadPoolManager#decrementPoolSize(int)
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
	
	/**
	 * This class is an implementation of ThreadFactory. This will be used by {@link ThreadPool} class for
	 * creating thread with specified prefix.
	 * @author abaxi
	 *
	 */
	private class CustomThreadFactory implements ThreadFactory{

		
		private String threadNamePrefix;
		private AtomicInteger threadCounter=new AtomicInteger(0);
		
		/**
		 * This is default constructor for {@link CustomThreadFactory} class
		 * @param threadNamePrefix : to be used for thread name.
		 */
		public CustomThreadFactory(String threadNamePrefix){
			this.threadNamePrefix=threadNamePrefix;
		}
		
		@Override
		public Thread newThread(Runnable runnable) {
			Thread t=new Thread(runnable, threadNamePrefix+AseStrings.MINUS+threadCounter.getAndIncrement());
			if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}
		
	}

}

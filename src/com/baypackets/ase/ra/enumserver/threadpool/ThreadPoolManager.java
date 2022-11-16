/**
 * describes lifecycle methods of thread manager
 * 
 */

package com.baypackets.ase.ra.enumserver.threadpool;

import java.util.concurrent.Future;

/**
 * The Interface ThreadManagerInterface.
 */
public interface ThreadPoolManager {
	
	public void executeTask(Runnable paramRunnable);
	
	public Future<?> submitTask(Runnable paramRunnable);
	
	/**
	 * Starts the thread pool.
	 */
	public void startPool();
	
	/**
	 * Stops shutdowns the thread pool.
	 */
	public void stopPool();
	
	/**
	 * increase pool size.
	 */
	public void incrementPoolSize(int incrementBy);
	
	/**
	 * Decrease pool size.
	 */
	public void decrementPoolSize(int decrementBy);
	
}

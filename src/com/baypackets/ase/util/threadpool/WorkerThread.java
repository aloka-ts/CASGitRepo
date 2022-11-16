/*
 * Created on Aug 6, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.baypackets.ase.util.threadpool;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import com.baypackets.ase.spi.util.Work;
import com.baypackets.ase.spi.util.WorkListener;
import com.baypackets.ase.util.AseThreadMonitor;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ThreadAlreadyRegisteredException;
import com.baypackets.bayprocessor.slee.common.ThreadNotRegisteredException;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThread;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThreadState;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadOwner;

/**
 * @author Ravi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WorkerThread extends MonitoredThread implements PooledThread{

	private static final Logger logger = Logger.getLogger(WorkerThread.class);
		
	private static final int BULK_COUNT = 20;
	
	private static ThreadMonitor threadMonitor = null;
	
	private int queueNumber = 0;
	
	private Queue queue = null;
	private ThreadOwner threadOwner = null;
	private WorkHandler handler = null;
	private int fetchCount = 1;
	private boolean stopped = false;
	
	private Work currentWork = null;
	
	public WorkerThread(){
		this("worker");
	}
	
	public WorkerThread(String name){
		super(name, AseThreadMonitor.getThreadTimeoutTime(), BaseContext.getTraceService());
	}
	
	public void run() {
		// Register thread with thread monitor
		try {
			// Set thread state to idle before registering
			this.setThreadState(MonitoredThreadState.Idle);
			this.threadMonitor.registerThread(this);
		} catch(ThreadAlreadyRegisteredException exp) {
			logger.error("This thread is already registered with Thread Monitor", exp);
		} catch(Throwable e) {
			logger.error(e.getMessage(),e);
		}

		try {
			//Object[] objects = new Object[this.fetchCount <= 0 ? 1 : this.fetchCount];
			Object object = null;
			while(!this.stopped) {
				try {
					//Out of wait, so dequeue from the top of the queue.
					/*int count = this.queue.dequeue(objects);		
				
					// Update time in thread monitor
					this.updateTimeStamp();

					// Set thread state to running before blocking on dequeue
					this.setThreadState(MonitoredThreadState.Running);
				
					//Send the object for executing, if it is not null.
					for(int i=0; i<count;i++) {
						if(objects[i] == null)
							continue;

						// Update time in thread monitor
						this.updateTimeStamp();
						
						if(objects[i] instanceof Work){
							this.executeWork((Work)objects[i]);
						}else{
							this.execute(objects[i]);
						}
						//objects[i] = null;
					}*/
					
					object = this.queue.dequeue();

					if (object == null) {
						continue;
					}
					
					// Update time in thread monitor
					this.updateTimeStamp();

					// Set thread state to running before blocking on dequeue
					this.setThreadState(MonitoredThreadState.Running);
					
					if(object instanceof Work) {
						this.executeWork((Work)object);
					} else {
						this.execute(object);
					}
					object = null;
				} catch(Throwable t) {
					//Catch the throwable to handle some runtime error kills the thread.
					logger.error(t.getMessage(),t);
				}
				
				// Set thread state to idle before blocking on dequeue
				this.setThreadState(MonitoredThreadState.Idle);
			}
		} catch(Throwable th) {
			logger.error("Caught at Thread Level", th);
		} finally {
			// Unregister thread with thread monitor
			try {
				this.threadMonitor.unregisterThread(this);
			} catch(ThreadNotRegisteredException exp) {
				logger.error("This thread is not registered with Thread Monitor", exp);
			}
		}
	}
	
	public ThreadOwner getThreadOwner() {
		return threadOwner;
	}

	public void setThreadOwner(ThreadOwner owner) {
		threadOwner = owner;
	}
	
	public void shutdown(){
		this.stopped = true;

		if (this.getThreadState() == MonitoredThreadState.Idle) {
			if (logger.isDebugEnabled()) {
				logger.debug("Thread [" + this.getName() +
										"] is being stoppped.");
			}

			this.interrupt();
		}

		this.setThreadState(MonitoredThreadState.Stopped);
	}
	
	public Queue getQueue() {
		return queue;
	}

	public void setQueue(Queue queue) {
		this.queue = queue;
	}
	
	public void setHandler(WorkHandler handler){
		this.handler = handler;
	}
	
	public WorkHandler getHandler(){
		return this.handler;
	}
	public void setThreadMonitor(ThreadMonitor tm) {
		threadMonitor = tm;
	}
	
	public void execute(Object obj){
		if(logger.isDebugEnabled()){
			logger.debug("Going to execute Object :" + obj);
		}
		if (this.handler == null){
			if(logger.isEnabledFor(Level.ERROR)){
				logger.error("There is no work handler associated with this thread.");
			}
			return;
		}
	
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("Processing Message :"+obj);
		}
		
		//Execute the work.
		handler.execute(obj);
	}
	
	public void executeWork(Work work){
		if(logger.isDebugEnabled()){
			logger.debug("Going to execute WORK :" + work);
		}
		
		if(this.currentWork != null){
			logger.error("This thread is handling a WORK already.");
		}
		
		long timeout = this.getTimeoutTime();
		WorkListener listener = work.getWorkListener();
		if(work.getTimeout() > timeout){
			this.setTimeoutTime(work.getTimeout());
		}
		
		try{
			work.execute();
			if(listener != null){
				listener.workCompleted(work);
			}
		}catch(Throwable t){
			logger.error(t.getMessage(),t);
			if(listener != null){
				listener.workFailed(work, t);
			}
		}finally{
			this.setTimeoutTime(timeout);
			if(logger.isDebugEnabled()){
				logger.debug("Setting currentWork == null ");
			}
			this.currentWork = null;
		}
	}

	public int getFetchCount() {
		return fetchCount;
	}

	public void setFetchCount(int i) {
		fetchCount = i;
	}
	
	public boolean expired(){
		
		boolean expired = true;
		if(currentWork == null)	{
			if(logger.isDebugEnabled()){
                                logger.debug("currentWork == null ");
                        }

			return expired;
		}
		
		if(currentWork.getTimeout() == 0){
			this.updateTimeStamp();
			expired = false;
		}else{
			if(logger.isDebugEnabled()){
                        	logger.debug("currentWork.getTimeout() != 0 ");
                	}
			WorkListener listener = this.currentWork.getWorkListener();
			if(listener != null){
				listener.workTimedout(this.currentWork);
			}
		}
		return expired;
	}
	
	public int getQueueNumber() {
		return queueNumber;
	}

	public void setQueueNumber(int queueNumber) {
		this.queueNumber = queueNumber;
	}
}

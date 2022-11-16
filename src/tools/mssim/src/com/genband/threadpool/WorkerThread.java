/*
 * Created on Aug 6, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.genband.threadpool;

import org.apache.log4j.Logger;

/**
 * @author Ravi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WorkerThread extends Thread implements PooledThread{

	private static final Logger logger = Logger.getLogger(WorkerThread.class);
		
	public static final int BULK_COUNT = 20;
	
	private Queue queue = null;
	private WorkHandler handler = null;
	private int fetchCount = 1;
	private boolean stopped = false;
	
	private Work currentWork = null;
	
	public WorkerThread(){
		this("worker");
	}
	
	public WorkerThread(String name){
		super(name);
	}
	
	public void run() {
		Object[] objects = new Object[this.fetchCount <= 0 ? 1 : this.fetchCount];
		
		while(!this.stopped) {
			try{
				//Out of wait, so dequeue from the top of the queue.
				int count = this.queue.dequeue(objects);		
				
				//Send the object for executing, if it is not null.
				for(int i=0; i<count;i++){
					if(objects[i] == null)
						continue;

					if(objects[i] instanceof Work){
						this.executeWork((Work)objects[i]);
					}else{
						this.execute(objects[i]);
					}
					objects[i] = null;
				}
			}catch(Throwable t){
				//Catch the throwable to handle some runtime error kills the thread.
				logger.error("Caught at thread level", t);
			}
		}
	}
	
	public void shutdown(){
		this.stopped = true;
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
	
	public void execute(Object obj){
		if (this.handler == null){
			logger.error("There is no work handler associated with this thread.");
			return;
		}
	
		if(logger.isInfoEnabled()){
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
		
		try{
			work.execute();
		}catch(Throwable t){
			logger.error("executeWork failed", t);
		}finally{
			this.currentWork = null;
		}
	}

	public int getFetchCount() {
		return fetchCount;
	}

	public void setFetchCount(int i) {
		fetchCount = i;
	}
}

/*
 * Created on Aug 6, 2004
 *
 * To change the template for this generated file go to

 */
package com.genband.threadpool;

import java.util.ArrayList;
import java.util.Random;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author Ravi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ThreadPool implements WorkManager{
	
	private static Logger logger = Logger.getLogger(ThreadPool.class);
	private static Random randomGenerator = new Random();
	
	private static final int NEW = 1;
	private static final int STARTED = 2;
	private static final int STOPPED = 3;
	private static final String DEFAULT_THREAD_NAME = "Worker Thread";
	
	private ArrayList workers = new ArrayList();
	private ArrayList queues = new ArrayList();
	private Queue queue = new Queue();
	private WorkHandler handler = null;
	
	private String threadName = DEFAULT_THREAD_NAME;
	private int numOfActiveThreads = 0;
	private int numOfThreads = 0;
	
	private boolean singleQueue = false;
	private int state = NEW;
	private int fetchCount = WorkerThread.BULK_COUNT;
	
	public ThreadPool(int poolSize, boolean singleQueue,
						WorkHandler handler) 
						throws ThreadPoolException{
		this(poolSize, singleQueue, null, handler);
	}
	
	public ThreadPool(int poolSize, boolean singleQueue, 
						String threadName,
						WorkHandler handler)
						throws ThreadPoolException{
		try{
			//Assign the class memebers
			this.singleQueue = singleQueue;
			this.threadName = threadName;
			this.handler = handler;
			this.numOfThreads = poolSize;
			
			logger.info("Creating the queue(s)...");

			//Create the queue if it is single queue.
			if(singleQueue){
				this.queue = new Queue();
			}
			
			this.fetchCount = WorkerThread.BULK_COUNT;
			
			//Now create instances of worker threads.	
			if(logger.isEnabledFor(Level.INFO)){
				logger.info("Creating the worker(s)...");
			}
			
			for(int i=0; handler != null && i<poolSize;i++){
				this.addThread(handler);	
			}
			
		}catch(Exception e){
			throw new ThreadPoolException(e);
		}
	}
	
	public ThreadPool(boolean singleQueue) throws ThreadPoolException{
		this(0,singleQueue,null);
	}

	public Queue getQueue() {
		return this.queue;
	}

	public synchronized int addThread(PooledThread thread) throws ThreadPoolException{
		
		int index = -1;
		
		if(thread == null)
			return index;
		
		if(! (thread instanceof Thread)){
			throw new ThreadPoolException("Thread object should extend java.lang.Thread");
		}
		
		if(((Thread)thread).isAlive()){
			throw new IllegalThreadStateException("Thread is already running");
		}
		
		//Get the index to add this thread.
		index = this.workers.size();
		
		//Get the queue to wait on.
		Queue tQueue = this.singleQueue ? this.queue : new Queue();
		if(!singleQueue){
			this.queues.add(index, tQueue);	
		}
		
		//Add this thread to the workers list.
		this.workers.add(index, thread);

		numOfActiveThreads++;
		// PRASHNAT numOfThreads++;
		
		//Associate the queue with this thread
		thread.setQueue(tQueue);
		
		//Start the thread if the POOL is already started
		if(this.state == STARTED){
			((Thread)thread).start();
		}
		
		//Log a message indicating this thread is added
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("Thread :" + ((Thread)thread).getName() + "added at index :" + index);
		}
		
		//Return the index of the queue.
		return index;
	}
	
	public synchronized int addThread(WorkHandler handler) throws ThreadPoolException{
		String tName = this.threadName + " [" + this.workers.size() +"]";
		WorkerThread worker = new WorkerThread(tName);
		worker.setDaemon(true);
		worker.setHandler(handler);
		worker.setFetchCount(this.fetchCount);

		return this.addThread(worker);
	}
	
	public int submit(int index, Object obj){
		
		if(this.state == STOPPED){
			throw new IllegalStateException("Pool already stopped.");
		}
		
		//In case of the active threads count == 0, Do the OP synchronously.
		if(this.numOfActiveThreads  == 0 ){
			if(logger.isDebugEnabled()){
				logger.debug("Going to invoke operation synchronously.");
			}
			if(this.handler != null){
				this.handler.execute(obj);
			}else{
				logger.error("No handler available to execute this operation");
			}
			return index;
		}
		
		Queue tQueue = null;
		if(this.singleQueue){
			tQueue = this.queue;
		}else{
			int size = this.queues.size();
			 if(index < 0 || index >= size){
				index = Math.abs(index % size);
			}
			tQueue = (Queue) this.queues.get(index);
		}
		
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("Submitting Work to Queue at index :" + index);
		}

		tQueue.enqueue(obj);
		
		return index;	
	}
	
	public int submit(Object obj) throws IllegalStateException{
		int index = randomGenerator.nextInt();
		return submit(index, obj);
	}
	
	public synchronized void start(){
		if(this.state != NEW){
			throw new IllegalStateException("Thread Pool already started.");
		}
		
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("Starting the thread pool...");
		}
		
		this.state = STARTED;
		
		for(int i=0; i<this.workers.size();i++){
			Thread thread = (Thread) workers.get(i);
			thread.start();
		}
		
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("Thread pool started...");
		}
	}
	
	public void shutdown(){
		//Simply return if it is already stopped.
		if(this.state == STOPPED)
			return;

		if(logger.isEnabledFor(Level.INFO)){
			logger.info("Stopping the thread pool...");
		}
		
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("Shutting down the thread pool");
		}
		//Set the stopped flag to true.
		this.state = STOPPED;
		
		//Shutdown the threads.
		for(int i=0; i<workers.size();i++){
			PooledThread worker = (PooledThread) this.workers.get(i);
			worker.shutdown();
		}
		
		//Stop the single queue.
		if(this.queue != null){
			this.queue.stop();
		}
		
		//Stop all the queues.
		for(int i=0; i<this.queues.size();i++){
			Queue tQueue = (Queue)this.queues.get(i);
			tQueue.stop();
		}

		if(logger.isEnabledFor(Level.INFO)){
			logger.info("Thread pool stopped...");
		}
	}
	
	public void doWork(Work work) {
		this.submit(work);
	}


	public int getNumberOfThreads() {
		return this.numOfThreads;
	}
}

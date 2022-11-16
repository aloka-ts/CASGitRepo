/*
 * Created on Aug 6, 2004
 *
 * To change the template for this generated file go to

 */
package com.baypackets.ase.util.threadpool;

import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;
import java.util.Collection;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseMessage;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.spi.util.Work;
import com.baypackets.ase.spi.util.WorkManager;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThread;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadOwner;

/**
 * @author Ravi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ThreadPool implements ThreadOwner, WorkManager{
	
	private static Logger logger = Logger.getLogger(ThreadPool.class);
	private static Random randomGenerator = new Random();
	
	private static final int NEW = 1;
	private static final int STARTED = 2;
	private static final int STOPPED = 3;
	private static final String DEFAULT_THREAD_NAME = "Worker Thread";
	private static ConfigRepository configRep = BaseContext.getConfigRepository();
	private static String inapTraffic = configRep.getValue(Constants.INAP_TRAFFIC);
	private static String prepaidRoutingStr = configRep.getValue(Constants.PREPAID_TRAFFIC_DISTRIBUTION);

	private static String inapWorkerRatio = configRep.getValue(Constants.INAP_WORKER_THREADS);
	
	private static String prepaidWorkerRatio = configRep.getValue(Constants.PREPAID_WORKER_THREADS);
	
	private static boolean prepaidRouting = false;
	
	private ArrayList workers = new ArrayList();
	private ArrayList queues = new ArrayList();
	private Queue queue = new Queue();
	private WorkHandler handler = null;
	private ThreadMonitor threadMonitor = null;
	
	
	private ThreadOwner owner = null;
	private String threadName = DEFAULT_THREAD_NAME;
	private int percentageMinActiveThreadsRequired = 50; // %
	private int numOfActiveThreads = 0;
	private int numOfThreads = 0;
	private int policy = Queue.POLICY_UNBOUNDED;
	private int maxQueueSize = Integer.MAX_VALUE;
	
	private boolean singleQueue = false;
	private int state = NEW;
	private int fetchCount = Constants.DEFAULT_QUEUE_FETCH_COUNT;
	
	private ConfigRepository m_configRepository	= (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
	String threadExpiryStackDumpFlag = (String)m_configRepository.getValue(Constants.THREAD_EXPIRY_STACKDUMP_FLAG);
	
	public ThreadPool(int poolSize, boolean singleQueue,
						WorkHandler handler) 
						throws ThreadPoolException{
		this(poolSize, singleQueue,DEFAULT_THREAD_NAME, handler, null, 50);
	}
	
	public ThreadPool(int poolSize, boolean singleQueue, 
						String threadName,
						WorkHandler handler,
						ThreadOwner owner,
						int _percentageMinActiveThreadsRequired)
						throws ThreadPoolException{
		try{
			//Assign the class memebers
			this.singleQueue = singleQueue;
			this.threadName = threadName;
			this.owner = owner;
			this.handler = handler;
			this.percentageMinActiveThreadsRequired = _percentageMinActiveThreadsRequired;
			// PRASHANT     this.numOfActiveThreads = poolSize;
			this.numOfThreads = poolSize;
			
			if (logger.isInfoEnabled()){
				logger.info("Creating the queue(s)...");
			}

			//Create the queue if it is single queue.
			if(singleQueue){
				this.queue = new Queue();
			}
			
			//Get the fetch count from the config repository.
			ConfigRepository configRep = BaseContext.getConfigRepository();
			String strFetchCount = configRep.getValue(Constants.PROP_MT_QUEUE_BATCH_SIZE);
			try{
				strFetchCount = (strFetchCount == null) ? AseStrings.BLANK_STRING : strFetchCount.trim();
				this.fetchCount = Integer.parseInt(strFetchCount);
			}catch(NumberFormatException nfe){}
			if(logger.isEnabledFor(Level.INFO)){
				logger.info("Bulk fetch count from the queue :" + this.fetchCount);
			}
			
			//Now create instances of worker threads.	
			if(logger.isEnabledFor(Level.INFO)){
				logger.info("Creating the worker(s)...");
			}
			
			for(int i=0; i<poolSize;i++){
				this.addThread(handler);	
			}
			prepaidRoutingStr = prepaidRoutingStr.toLowerCase();
			if(prepaidRoutingStr.equals(AseStrings.TRUE_SMALL)){
				if(logger.isEnabledFor(Level.INFO))
					logger.info("Prepaid Distribution is On");
				prepaidRouting = true;
			}

		}catch(Exception e){
			new ThreadPoolException(e.getMessage());
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
		String tName = this.threadName + AseStrings.SQUARE_BRACKET_OPEN + this.workers.size() +AseStrings.SQUARE_BRACKET_CLOSE;
		WorkerThread worker = new WorkerThread();
		worker.setName(tName);
		worker.setDaemon(true);
		worker.setHandler(handler);
		worker.setThreadOwner(this.owner == null ? this : this.owner);
		worker.setFetchCount(this.fetchCount);
		
		worker.setQueueNumber(this.workers.size());
		if(logger.isDebugEnabled()){
			logger.debug("Setting queue number:" + worker.getQueueNumber());
		}
		return this.addThread(worker);
	}
	
	public synchronized void addThread(int index, WorkerThread thread) {
		String tName = this.threadName + " [" + index +"]";
		WorkerThread worker = new WorkerThread();
		worker.setName(tName);
		worker.setDaemon(true);
		worker.setHandler(thread.getHandler());
		worker.setThreadOwner(this.owner == null ? this : this.owner);
		worker.setFetchCount(this.fetchCount);
		
		worker.setQueueNumber(index);
		if(logger.isDebugEnabled()){
			logger.debug("Setting queue number:" + worker.getQueueNumber());
		}
		
		//Get the queue to wait on.
		/*Queue tQueue = this.singleQueue ? this.queue : new Queue();
		if(!singleQueue){
			this.queues.add(index, tQueue);	
		}*/
		
		//Add this thread to the workers list.
		this.workers.add(index, worker);

		//Associate the queue with this thread
		((PooledThread)worker).setQueue((Queue)this.queues.get(index));
		
		//Start the thread if the POOL is already started
		if(this.state == STARTED){
			((Thread)worker).start();
		}
		
		//Log a message indicating this thread is added
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("Thread :" + ((Thread)worker).getName() + "added at index :" + index);
		}
	}

	public void setFetchCount(int i) {
		this.fetchCount = i;
		
		for(Iterator it = this.workers.iterator(); it.hasNext(); ) {
			WorkerThread worker = (WorkerThread)it.next();
			worker.setFetchCount(i);
		}
	}
		
	
	/**
	* This overloaded method takes a boolean argument to  indicate
	* Priority of a SIP message
	*/	
	public int submit(int index, Object obj, boolean priorityMsg){
		
		int actualIndex = index;

		if(this.state == STOPPED){
			throw new IllegalStateException("Pool already stopped.");
		}
		//boolean priorityMsg = ((AseMessage)obj).isPriorityMessage();
		if(logger.isDebugEnabled()){
			logger.debug("Value of priorityMsg:  " +priorityMsg);
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
		
		if(inapTraffic.toLowerCase().equals(AseStrings.TRUE_SMALL)){
			tQueue = this.getQueue(actualIndex,true,obj);
			/*if(this.singleQueue){
				tQueue = this.queue;
				int size = this.workers.size();
				 if(index < 0 || index >= size){
					actualIndex = Math.abs(index % size);
				}
			}else{
				int size = this.queues.size();
				 if(index < 0 || index >= size){
					actualIndex = Math.abs(index % size);
				}
				tQueue = (Queue) this.queues.get(actualIndex);
			}*/
		}else{
			tQueue = this.getQueue(actualIndex,false,obj);
			/*if(this.singleQueue){
				tQueue = this.queue;
				int size = this.workers.size();
				 if(index < 0 || index >= size){
					actualIndex = Math.abs(index % size);
				}
			}else{
				int size = this.queues.size();
				 if(index < 0 || index >= size){
					actualIndex = Math.abs(index % size);
				}
				tQueue = (Queue) this.queues.get(actualIndex);
			}*/
		}
		int queueIndex = tQueue.getIndex();
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("Submitting Work to Queue at index :" + queueIndex);
		}
		((AseMessage) obj).setWorkQueue(queueIndex);
		/*if (((AseMessage) obj).getMessageType() == AseMessage.MESSAGE){
			if(AseSipServletMessage.class.isInstance(((AseMessage) obj).getMessage().getClass())){
				((AseSipServletMessage)((AseMessage) obj).getMessage()).setWorkQueue(queueIndex);
				if(logger.isEnabledFor(Level.INFO)){
					logger.info("Sip Servlet Message thus setting work queue index:" + queueIndex);
				}
			}
		}*/
		if(priorityMsg)	{
			Thread t = (Thread)this.workers.get(actualIndex);
			if(t.getPriority() != Thread.MAX_PRIORITY)	
				t.setPriority(Thread.MAX_PRIORITY);
			tQueue.enqueue(obj,true);
		}else	{
			tQueue.enqueue(obj,false);
		}
		
		return queueIndex;	
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
			}else if ( obj instanceof Work ) {
					( (Work)obj).execute();
			} else {
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
	
	private Queue getQueue(int index, boolean inapTraffic,Object obj){
		//boolean aseMsgInstance = AseMessage.class.isInstance(obj);
//		boolean sipMsgInstance = false;
//		if (aseMsgInstance){
//			sipMsgInstance = AseSipServletMessage.class.isInstance(((AseMessage)obj).getMessage());
//		}
		//if (!aseMsgInstance || !inapTraffic || (aseMsgInstance && !((AseMessage)obj).isInitial())){
		//if (!aseMsgInstance || !inapTraffic){
		if(!threadName.equals("Worker") || (!inapTraffic && !prepaidRouting)){
			if(logger.isEnabledFor(Level.INFO)){
				logger.info("Thread Name " + threadName);
				logger.info("Queing Conditions: inapTraffic" + ThreadPool.inapTraffic);
			}
			int actualIndex = index;
			Queue tQueue = null;
			if(this.singleQueue){
				tQueue = this.queue;
				int size = this.workers.size();
				 if(index < 0 || index >= size){
					actualIndex = Math.abs(index % size);
				}
			}else{
				int size = this.queues.size();
				 if(index < 0 || index >= size){
					actualIndex = Math.abs(index % size);
				}
				tQueue = (Queue) this.queues.get(actualIndex);
			}
			tQueue.setIndex(actualIndex);
			return tQueue;
		}
		
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("Thread Name " + threadName);
			logger.info("INAP Queing Conditions: inapTraffic" + ThreadPool.inapTraffic);
			logger.info("Prepaid Traffic Distribution" + ThreadPool.prepaidRoutingStr);
		}
		AseMessage message = (AseMessage) obj;	
		//String dlgId = ((AseSipServletMessage) message.getMessage()).getHeader("Dialogue-Id");
		
		int actualIndex = index;
		Queue tQueue = null;
		if (inapTraffic && prepaidRouting){
			if(this.singleQueue){
				tQueue = this.queue;
				int size = this.workers.size();
				actualIndex = this.getActualIndex(index,message.isInitial(), message.isInapMessage(),message.isPrepaidMessage(),size);
			}else{
				int size = this.queues.size();
				actualIndex = this.getActualIndex(index,message.isInitial(),message.isInapMessage(),message.isPrepaidMessage(),size);
				tQueue = (Queue) this.queues.get(actualIndex);
			}			
		}else if (inapTraffic){
			if(this.singleQueue){
				tQueue = this.queue;
				int size = this.workers.size();
				actualIndex = this.getActualIndex(index,message.isInitial(),message.isInapMessage(),size);
			}else{
				int size = this.queues.size();
				actualIndex = this.getActualIndex(index,message.isInitial(),message.isInapMessage(),size);
				tQueue = (Queue) this.queues.get(actualIndex);
			}
		}else if (prepaidRouting){
			if(this.singleQueue){
				tQueue = this.queue;
				int size = this.workers.size();
				actualIndex = this.getActualIndexForPrepaid(index, message.isInitial(), message.isPrepaidMessage(),size);
			}else{
				int size = this.queues.size();
				actualIndex = this.getActualIndexForPrepaid(index, message.isInitial(), message.isPrepaidMessage(),size);
				tQueue = (Queue) this.queues.get(actualIndex);
			}
		}
		tQueue.setIndex(actualIndex);
		return tQueue;
	}
	
	public int getActualIndex(int index, boolean isInitial, boolean isInap, int size){
		int actualIndex = index;
		int inapWorkers = 0;
		if(index < 0 || index >= size){
			if(logger.isEnabledFor(Level.INFO)){
				logger.info("getActualIndex: index < 0 || index >= size" + index);
			}
			//if (isInitial && isInap) {
			if (isInap) {
				inapWorkers = (Integer.parseInt(inapWorkerRatio) * size)/100;
				actualIndex = Math.abs(index % inapWorkers);
				if(logger.isEnabledFor(Level.INFO)){
					logger.info("getActualIndex: INAP Call " + "INDEX " + index + " Actual Index " + actualIndex);
				}
			//}else if (isInitial){
			}else {
				inapWorkers = (Integer.parseInt(inapWorkerRatio) * size)/100;
				actualIndex = Math.abs(index % (size - inapWorkers));
				actualIndex = actualIndex + inapWorkers;
				/*actualIndex = Math.abs(index % (size));
				if (actualIndex < inapWorkers)
					actualIndex = actualIndex + inapWorkers;*/
				if(logger.isEnabledFor(Level.INFO)){
					logger.info("getActualIndex: SIP Call " + "INDEX " + index + " Actual Index " + actualIndex);
				}
			}
		}else if (index < size && index > 0){
			if(logger.isEnabledFor(Level.INFO)){
				logger.info("getActualIndex: index < size && index > 0" + index);
			}
			if (isInitial){	
				// if (isInitial && isInap) {
				if (isInap) {
					inapWorkers = (Integer.parseInt(inapWorkerRatio) * size) / 100;
					if (index > (inapWorkers - 1)) {
						actualIndex = Math.abs(index % inapWorkers);
					}
					if (logger.isEnabledFor(Level.INFO)) {
						logger.info("getActualIndex: INAP Call " + "INDEX "
								+ index + " Actual Index " + actualIndex);
					}
					// }else if (isInitial){
				} else {
					inapWorkers = (Integer.parseInt(inapWorkerRatio) * size) / 100;
					actualIndex = Math.abs(index % (size - (inapWorkers)));
					actualIndex = actualIndex + inapWorkers;

					/*
					 * if (index < inapWorkers){ actualIndex = index +
					 * inapWorkers; }
					 */
					if (logger.isEnabledFor(Level.INFO)) {
						logger.info("getActualIndex: SIP Call " + "INDEX "
								+ index + " Actual Index " + actualIndex);
					}
				}
			}
		}else if (index == 0){
			if(logger.isEnabledFor(Level.INFO)){
				logger.info("getActualIndex:  index == 0" + index);
			}
			//if (isInitial && !isInap) {
			if (isInitial){	
				if (!isInap) {
					inapWorkers = (Integer.parseInt(inapWorkerRatio) * size) / 100;
					actualIndex = inapWorkers;
					if (logger.isEnabledFor(Level.INFO)) {
						logger.info("getActualIndex: SIP Call " + "INDEX "
								+ index + " Actual Index " + actualIndex);
					}
				}
			}
		}
		return actualIndex;
	}
	public int getActualIndex(int index,boolean isInitial,boolean isInap,boolean isPrepaid, int size){
		int actualIndex = index;
		int inapWorkers = 0;
		int prepaidWorkers = 0;
		if(index < 0 || index >= size){
			if(logger.isEnabledFor(Level.INFO)){
				logger.info("getActualIndex (INAP-Prepaid): index < 0 || index >= size" + index);
			}
			if (isInap) {
				inapWorkers = (Integer.parseInt(inapWorkerRatio) * size)/100;
				actualIndex = Math.abs(index % inapWorkers);
				if(logger.isEnabledFor(Level.INFO)){
					logger.info("getActualIndex (INAP-Prepaid): INAP Call " + "INDEX " + index + " Actual Index " + actualIndex);
				}
			}else if(isPrepaid){
				inapWorkers = (Integer.parseInt(inapWorkerRatio) * size)/100;
				prepaidWorkers = (Integer.parseInt(prepaidWorkerRatio) * size)/100;
				actualIndex = Math.abs(index % prepaidWorkers);
				//if (actualIndex < inapWorkers)
				actualIndex = actualIndex + inapWorkers;
				if(logger.isEnabledFor(Level.INFO)){
					logger.info("getActualIndex (INAP-Prepaid): Prepaid Call " + "INDEX " + index + " Actual Index " + actualIndex);
				}
			}else{
				inapWorkers = (Integer.parseInt(inapWorkerRatio) * size)/100;
				prepaidWorkers = (Integer.parseInt(prepaidWorkerRatio) * size)/100;
				//actualIndex = Math.abs(index % size);
				/*if (actualIndex < inapWorkers){
					actualIndex = actualIndex + (inapWorkers + prepaidWorkers);
					if (actualIndex >= size){
						actualIndex = size - 1;
						if(logger.isEnabledFor(Level.INFO)){
							logger.info("Index goes out of bound thus hard coding to the last thread");
						}
					}
						
				}else if (actualIndex < (prepaidWorkers + inapWorkers)){
					actualIndex = actualIndex + prepaidWorkers;	
					if (actualIndex >= size){
						actualIndex = size - 1;
						if(logger.isEnabledFor(Level.INFO)){
							logger.info("Index goes out of bound thus hard coding to the last thread");
						}
					}
				}*/
				actualIndex = Math.abs(index % (size - (inapWorkers + prepaidWorkers)));
				actualIndex = actualIndex + (inapWorkers + prepaidWorkers);
				if(logger.isEnabledFor(Level.INFO)){
					logger.info("getActualIndex (INAP-Prepaid): SIP Call " + "INDEX " + index + " Actual Index " + actualIndex);
				}
			}
		}else if (index < size && index > 0){
			if(logger.isEnabledFor(Level.INFO)){
				logger.info("getActualIndex: index < size && index > 0" + index);
			}
			if (isInitial){
				if (isInap) {
					inapWorkers = (Integer.parseInt(inapWorkerRatio) * size) / 100;
					if (index > (inapWorkers - 1)) {
						actualIndex = Math.abs(index % inapWorkers);
					}
					if (logger.isEnabledFor(Level.INFO)) {
						logger.info("getActualIndex (INAP-Prepaid): INAP Call "
								+ "INDEX " + index + " Actual Index "
								+ actualIndex);
					}
				} else if (isPrepaid) {
					inapWorkers = (Integer.parseInt(inapWorkerRatio) * size) / 100;
					prepaidWorkers = (Integer.parseInt(prepaidWorkerRatio) * size) / 100;
					/*
					 * actualIndex = Math.abs(index % (inapWorkers +
					 * prepaidWorkers)); if (actualIndex < inapWorkers)
					 * actualIndex = actualIndex + inapWorkers;
					 */
					actualIndex = Math.abs(index % prepaidWorkers);
					actualIndex = actualIndex + inapWorkers;
					// This has been changed to avoid if checks and to
					// distribute
					// calls evenly among threads. For instance, if we have
					// total
					// 24 threads and 10 is for inap and rest for prepaid then
					// 10-20
					// will be used more in place of 20-24
					/*
					 * if(index < inapWorkers){ actualIndex = actualIndex +
					 * inapWorkers; }else if (index >= (inapWorkers +
					 * prepaidWorkers)){ actualIndex = Math.abs(index %
					 * (inapWorkers + prepaidWorkers)); if (actualIndex <
					 * inapWorkers) actualIndex = actualIndex + inapWorkers; }
					 */
					if (logger.isEnabledFor(Level.INFO)) {
						logger.info("getActualIndex (INAP-Prepaid): Prepaid Call "
								+ "INDEX "
								+ index
								+ " Actual Index "
								+ actualIndex);
					}
				} else {
					inapWorkers = (Integer.parseInt(inapWorkerRatio) * size) / 100;
					prepaidWorkers = (Integer.parseInt(prepaidWorkerRatio) * size) / 100;
					actualIndex = Math.abs(index
							% (size - (inapWorkers + prepaidWorkers)));
					actualIndex = actualIndex + (inapWorkers + prepaidWorkers);
					/*
					 * if (index < inapWorkers){ actualIndex = index +
					 * (inapWorkers + prepaidWorkers); //This will happen when
					 * INAP is given 20% and Prepaid is given 70% for instance
					 * if (actualIndex >= size){ actualIndex = size - 1;
					 * if(logger.isEnabledFor(Level.INFO)){ logger.info(
					 * "Index goes out of bound thus hard coding to the last thread"
					 * ); } } }else if(index < (prepaidWorkers + inapWorkers)){
					 * actualIndex = index + prepaidWorkers; if (actualIndex >=
					 * size){ actualIndex = size - 1;
					 * if(logger.isEnabledFor(Level.INFO)){ logger.info(
					 * "Index goes out of bound thus hard coding to the last thread"
					 * ); } } }
					 */
					if (logger.isEnabledFor(Level.INFO)) {
						logger.info("getActualIndex (INAP-Prepaid): SIP Call "
								+ "INDEX " + index + " Actual Index "
								+ actualIndex);
					}
				}
			}
		}else if (index == 0){
			if(logger.isEnabledFor(Level.INFO)){
				logger.info("getActualIndex:  index == 0" + index);
			}
			//if (isInitial && !isInap) {
			if (isInitial){
				if (isPrepaid) {
					inapWorkers = (Integer.parseInt(inapWorkerRatio) * size) / 100;
					actualIndex = inapWorkers;
				} else if (!isInap && !isPrepaid) {
					inapWorkers = (Integer.parseInt(inapWorkerRatio) * size) / 100;
					prepaidWorkers = (Integer.parseInt(prepaidWorkerRatio) * size) / 100;
					actualIndex = inapWorkers + prepaidWorkers;
					if (logger.isEnabledFor(Level.INFO)) {
						logger.info("getActualIndex (INAP-Prepaid): SIP Call "
								+ "INDEX " + index + " Actual Index "
								+ actualIndex);
					}
				}
			}
		}
		return actualIndex;
	}
	public int getActualIndexForPrepaid(int index,boolean isInitial,boolean isPrepaid, int size){
		int actualIndex = index;
		int prepaidWorkers = 0;
		if(index < 0 || index >= size){
			if(logger.isEnabledFor(Level.INFO)){
				logger.info("getActualIndexForPrepaid: index < 0 || index >= size" + index);
			}
			if (isPrepaid) {
				prepaidWorkers = (Integer.parseInt(prepaidWorkerRatio) * size)/100;
				actualIndex = Math.abs(index % prepaidWorkers);
				if(logger.isEnabledFor(Level.INFO)){
					logger.info("getActualIndexForPrepaid: Prepaid Call " + "INDEX " + index + " Actual Index " + actualIndex);
				}
			}else {
				prepaidWorkers = (Integer.parseInt(prepaidWorkerRatio) * size)/100;
				actualIndex = Math.abs(index % (size - prepaidWorkers));
				actualIndex = actualIndex + prepaidWorkers;
				/*actualIndex = Math.abs(index % (size));
				if (actualIndex < prepaidWorkers)
					actualIndex = actualIndex + prepaidWorkers;*/
				if(logger.isEnabledFor(Level.INFO)){
					logger.info("getActualIndexForPrepaid: SIP Call " + "INDEX " + index + " Actual Index " + actualIndex);
				}
			}
		}else if (index < size && index > 0){
			if(logger.isEnabledFor(Level.INFO)){
				logger.info("getActualIndexForPrepaid: index < size && index > 0" + index);
			}
			if (isInitial){
				if (isPrepaid) {
					prepaidWorkers = (Integer.parseInt(prepaidWorkerRatio) * size) / 100;
					if (index > (prepaidWorkers - 1)) {
						actualIndex = Math.abs(index % prepaidWorkers);
					}
					if (logger.isEnabledFor(Level.INFO)) {
						logger.info("getActualIndexForPrepaid: Prepaid Call "
								+ "INDEX " + index + " Actual Index "
								+ actualIndex);
					}
				} else {
					prepaidWorkers = (Integer.parseInt(prepaidWorkerRatio) * size) / 100;
					actualIndex = Math.abs(index % (size - (prepaidWorkers)));
					actualIndex = actualIndex + prepaidWorkers;
					/*
					 * if (index < prepaidWorkers){ actualIndex = index +
					 * prepaidWorkers; }
					 */
					if (logger.isEnabledFor(Level.INFO)) {
						logger.info("getActualIndexForPrepaid: SIP Call "
								+ "INDEX " + index + " Actual Index "
								+ actualIndex);
					}
				}
			}
		}else if (index == 0){
			if(logger.isEnabledFor(Level.INFO)){
				logger.info("getActualIndexForPrepaid:  index == 0" + index);
			}
			if (isInitial){
				if (!isPrepaid) {
					prepaidWorkers = (Integer.parseInt(prepaidWorkerRatio) * size) / 100;
					actualIndex = prepaidWorkers;
					if (logger.isEnabledFor(Level.INFO)) {
						logger.info("getActualIndexForPrepaid: SIP Call "
								+ "INDEX " + index + " Actual Index "
								+ actualIndex);
					}
				}
			}
		}
		return actualIndex;
	}
	public int submit(Object obj) throws IllegalStateException{
		int index = randomGenerator.nextInt();
		return submit(index, obj);
	}
	
	public int submit(Object obj,boolean priority) throws IllegalStateException{
		int index = randomGenerator.nextInt();
		return submit(index, obj,priority);
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
			Object obj = workers.get(i);
			((WorkerThread)obj).setThreadMonitor(this.threadMonitor);
			((Thread)obj).start();
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
		
		//Stop the single queue.
		if(this.queue != null){
			this.queue.stop();
		}
		
		//Stop all the queues.
		for(int i=0; i<this.queues.size();i++){
			Queue tQueue = (Queue)this.queues.get(i);
			tQueue.stop();
		}

		//Shutdown the threads.
		for(int i=0; i<workers.size();i++){
			PooledThread worker = (PooledThread) this.workers.get(i);
			worker.shutdown();
		}
		
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("Thread pool stopped...");
		}
	}

	public ArrayList getQueueList()	{
		return this.queues;
	}

	public boolean isEmpty() {
		if(this.singleQueue) {
			return this.queue.isEmpty();
		} else {
			for(Object q : this.queues) {
				if(!((Queue)q).isEmpty()) {
					return false;
				}
			}

			return true;
		}
	}

	public int threadExpired(MonitoredThread thread) {
		
		if(thread instanceof WorkerThread){
			if(logger.isDebugEnabled()){
				logger.debug("Going to check whether the thread :" + thread.getName() + " expired or not");
			}
			boolean expired = ((WorkerThread)thread).expired();
			if(!expired){
				if(logger.isDebugEnabled()){
					logger.debug("Thread " + thread.getName() + " is not expired");
				}
				return CONTINUE;
			}
		}
		int queueNumber = ((WorkerThread)thread).getQueueNumber();
		
		this.addThread(queueNumber, (WorkerThread)thread);
		
		if(logger.isDebugEnabled()){
			logger.debug("Worker thread's queue number " + queueNumber);
		}

		logger.error("Thread " + thread.getName() + " has expired");

		//log expired thread stack::
		StackTraceElement[] arrSte=thread.getStackTrace();
		for(StackTraceElement ste:arrSte){
			logger.error(ste);
		}
		///
		if(threadExpiryStackDumpFlag.equalsIgnoreCase(AseStrings.TRUE_SMALL)){
			// Dump stack
			AseUtils.dumpStack();
		}

		boolean isSystemRestartRequired = false;

		numOfActiveThreads--;

		int minNumberOfActiveThreadsReq = numOfThreads*percentageMinActiveThreadsRequired/100;

		if(numOfActiveThreads <= minNumberOfActiveThreadsReq) {
			logger.error("Number of active threads reach below the minimum.");
			isSystemRestartRequired = true;
		}

		try {
			//Shutdown the Thread
			thread.shutdown();
		} catch(Exception exp) {
			logger.error("threadExpired()", exp);
		}

		if(isSystemRestartRequired == true )
			return SYSTEM_RESTART;
		else
			return CONTINUE;
	}

	public void doWork(Work work) {
		this.submit(work);
	}


	public int getNumberOfThreads() {
		return this.numOfThreads;
	}

	public void setThreadMonitor(ThreadMonitor tm) {
		this.threadMonitor = tm;
	}

	public void setMaxQueueSize(int size) {
		this.maxQueueSize = size;
		if(this.policy == Queue.POLICY_UNBOUNDED) {
			return;
		}
		if(singleQueue) {
			this.queue.setMaxQueueSize(size);
		} else {
			for(int i=0 ; i< this.queues.size() ; i++) {
				((Queue)this.queues.get(i)).setMaxQueueSize(size);
			}
		}
	}

	public void setPolicy(int policy) {
		this.policy = policy;
		if(singleQueue) {
			this.queue.setPolicy(policy);
			if(policy != Queue.POLICY_UNBOUNDED) {
				this.queue.setMaxQueueSize(this.maxQueueSize);
			}
		} else {
			for(int i=0 ; i< this.queues.size() ; i++) {
				Queue tQueue = (Queue)this.queues.get(i);
				tQueue.setPolicy(policy);
				if(policy != Queue.POLICY_UNBOUNDED) {
					tQueue.setMaxQueueSize(this.maxQueueSize);
				}
			}
		}
	}
	

}

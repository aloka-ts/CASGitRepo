package com.baypackets.ase.ra.http.qm;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

import com.baypackets.ase.ra.http.message.HttpRequest;
import com.baypackets.ase.resource.ResourceException;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.http.workmanager.ThreadPool;


public class HttpQueueManagerImpl implements HttpQueueManager {

	private static HttpQueueManagerImpl queueManager=null;
	private HttpQueue httpQueue;
 	private BlockingQueue<HttpRequest> queue;
 	private Logger logger = Logger.getLogger(HttpQueueManagerImpl.class);
 	private HttpRequest request;
 	private DequeTask dequeTask=null;
 	private ThreadPool threadPool = null;

    public HttpQueueManagerImpl(){
              httpQueue = new HttpQueue();
              this.queue = httpQueue.getHttpQ();
	}
	@Override
	public void destroy() {
		//stop dequeue threadPool
		threadPool.stopPool();
		//clear http queue
		queue.clear();
		
	}

	@Override
	public boolean enQueueRequest(HttpRequest request) throws ResourceException {

		if (request != null) {

			boolean success = queue.add(request);
			//
			// synchronized (queue) {
			// queue.notify();
			// }

			if (logger.isDebugEnabled())
				logger.debug("httpRequest added in queue. " + success + queue);

			return true;
		} else {

			if (logger.isDebugEnabled())
				logger.debug("httpRequest is null");
			return false;
		}
	}

	@Override
	public void load() {
		
		if(logger.isDebugEnabled())
			logger.debug("load() and start threadPool and DequeTask. For queue "+queue);
		
		dequeTask = new DequeTask(queue);
		//dequeue threadpool initialize
		threadPool = new ThreadPool();
		threadPool.startPool();
		threadPool.executeTask(dequeTask);
	}

	@Override
	public boolean notifyDequeueTask(HttpRequest request) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pollRequest(HttpRequest request) {
		// TODO Auto-generated method stub
		return false;
	}

	/** dequeue request from queue **/
	@Override
	public HttpRequest removeRequest() {
		request = queue.remove();
		return request;
	}

	public static HttpQueueManagerImpl getInstance() {
		if(queueManager==null){
			queueManager = new HttpQueueManagerImpl();
		}
		return queueManager;
	}
	
	public Queue<HttpRequest> getQueue(){
		return queue;
	}

}

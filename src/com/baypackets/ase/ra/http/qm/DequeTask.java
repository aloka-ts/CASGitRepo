package com.baypackets.ase.ra.http.qm;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

import com.baypackets.ase.ra.http.message.HttpRequest;
import com.baypackets.ase.ra.http.qm.HttpQueueManagerImpl;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.http.web.WebManager;
//import com.baypackets.ase.ra.http.workmanager.ThreadPool;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public class DequeTask implements Runnable{

	//private HttpQueueManagerImpl queueManager = HttpQueueManagerImpl.getInstance();
	private Logger logger = Logger.getLogger(DequeTask.class);
//	private int check = 0;
	private BlockingQueue<HttpRequest> queue;
	private WebManager webManager = WebManager.getInstance();
	//private ThreadPool threadPool = null;

	
	
	public DequeTask(BlockingQueue<HttpRequest> queue){
		this.queue=queue;
		//threadPool = new ThreadPool();
		//threadPool.startPool();
		//get request handler thread pool
		//threadPool = webManager.getThreadPool();
	}
	
	@Override
	public void run() {
		
		if (logger.isDebugEnabled()) {
			logger.debug(" run().. ");
		}
		try {
			dequeRequest();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
  
	private void dequeRequest() throws InterruptedException {
		
		if (logger.isDebugEnabled()) {
			logger.debug("dequeRequest() " + this.queue);
		}
		while (true) {
//			synchronized (this.queue) {
//				if (this.queue.isEmpty()) {
//					try {
//						if (logger.isDebugEnabled()) {
//							logger.debug("dequeRequest() queue is empty wait");
//						}
//						this.queue.wait();
//					} catch (InterruptedException e1) {
//						if (logger.isDebugEnabled()) {
//							logger.debug("Exception in deque thread:"
//									+ e1.getMessage());
//						}
//					}
//				}
//			}
//			if (logger.isDebugEnabled()) {
//				logger.debug("Found Request in queue.");
//			}

			HttpRequest request = (HttpRequest) this.queue.take();
			
			if (logger.isDebugEnabled()) {
				logger.debug("HttpRequest found.");
			}
			webManager.handleRequest(request);

		}
	}

}
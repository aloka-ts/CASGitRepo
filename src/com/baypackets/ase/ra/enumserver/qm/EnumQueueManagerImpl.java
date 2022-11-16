package com.baypackets.ase.ra.enumserver.qm;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.enumserver.message.EnumMessage;
import com.baypackets.ase.ra.enumserver.message.EnumRequest;
import com.baypackets.ase.ra.enumserver.threadpool.ThreadPool;
import com.baypackets.ase.resource.ResourceException;

public class EnumQueueManagerImpl implements EnumQueueManager {

	private static EnumQueueManagerImpl queueManager = null;
	private EnumQueue enumQueue;
	private BlockingQueue<EnumMessage> queue;
	private Logger logger = Logger.getLogger(EnumQueueManagerImpl.class);
	private EnumMessage request;
	private EnumDequeTask dequeTask = null;
	private ThreadPool threadPool = null;

	public EnumQueueManagerImpl() {
		enumQueue = new EnumQueue();
		this.queue = enumQueue.getEnumQ();
	}

	@Override
	public void destroy() {
		// stop dequeue threadPool
		threadPool.stopPool();
		// clear queue
		queue.clear();

	}

	@Override
	public boolean enQueueMessage(EnumMessage message) throws ResourceException {

		if (message != null) {

			boolean success = queue.add(message);

			if (logger.isDebugEnabled())
				logger.debug("EnumMessage added in queue. " + success + queue);

			return true;
		} else {

			if (logger.isDebugEnabled())
				logger.debug("EnumMessage is null");
			return false;
		}
	}

	@Override
	public void load() {

		if (logger.isDebugEnabled())
			logger.debug("load() and start threadPool and DequeTask. For queue "
					+ queue);

		dequeTask = new EnumDequeTask(queue);
		// dequeue threadpool initialize
		threadPool = new ThreadPool();
		threadPool.startPool();
		threadPool.executeTask(dequeTask);
	}

	@Override
	public boolean notifyDequeueTask(EnumRequest request) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pollRequest(EnumRequest request) {
		// TODO Auto-generated method stub
		return false;
	}

	/** dequeue request from queue **/
	@Override
	public EnumMessage removeMessage() {
		request = queue.remove();
		return request;
	}

	public static EnumQueueManagerImpl getInstance() {
		if (queueManager == null) {
			queueManager = new EnumQueueManagerImpl();
		}
		return queueManager;
	}

	public Queue<EnumMessage> getQueue() {
		return queue;
	}

}

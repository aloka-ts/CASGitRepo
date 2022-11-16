package com.baypackets.ase.ra.enumserver.qm;

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.enumserver.EnumResourceAdaptorImpl;
import com.baypackets.ase.ra.enumserver.message.EnumMessage;
import com.baypackets.ase.ra.enumserver.message.EnumRequest;
import com.baypackets.ase.ra.enumserver.message.EnumResponse;
import com.baypackets.ase.ra.enumserver.receiver.EnumReceiver;
import com.baypackets.ase.resource.ResourceException;

public class EnumDequeTask implements Runnable {

	private Logger logger = Logger.getLogger(EnumDequeTask.class);

	private BlockingQueue<EnumMessage> queue;

	public EnumDequeTask(BlockingQueue<EnumMessage> queue2) {
		this.queue = queue2;
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

			EnumMessage message = (EnumMessage) this.queue.take();

			if (logger.isDebugEnabled()) {
				logger.debug("EnumMessage found.");
			}

			if (message instanceof EnumRequest) {

				if (logger.isDebugEnabled()) {
					logger.debug("EnumMessage is EnumRequest.");
				}
				try {
					EnumResourceAdaptorImpl.getInstance().deliverRequest(
							(EnumRequest) message);
				} catch (ResourceException e) {
					e.printStackTrace();
				}
			} else if (message instanceof EnumResponse) {

				if (logger.isDebugEnabled()) {
					logger.debug("EnumMessage is EnumResponse.");
				}
				EnumReceiver.getInstance().processResponse(
						(EnumResponse) message);
			}

		}
	}

}
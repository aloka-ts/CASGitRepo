package com.agnity.simulator.handlers.impl;

import org.apache.log4j.Logger;

import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.type.TimerNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;

public class TimerHandler extends AbstractHandler{

	private static Logger logger = Logger.getLogger(TimerHandler.class);
	private static Handler handler;

	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (TimerHandler.class) {
				if(handler ==null){
					handler = new TimerHandler();
				}
			}
		}
		return handler;
	}

	private TimerHandler(){

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside TimerHandler processNode()");

		if(!(node.getType().equals(Constants.TIMER_NODE))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}	
		TimerNode tNode = (TimerNode) node;
		int timeoutinSecs=tNode.getTimeout();
		
		synchronized (simCpb) {
			try {
				simCpb.wait(timeoutinSecs*1000);
			} catch (InterruptedException e) {
				if(logger.isDebugEnabled())
					logger.debug("Sleep failed",e);
			}
		}
		
		if(logger.isInfoEnabled())
			logger.info("Leaving TimerHandler processNode() with true status");
		return true;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {
				
		if(logger.isInfoEnabled())
			logger.info("Leaving TimerHandler processRecievedMessage() with false status  as receive mode is not supported");
		//will always work in send mode
		return false;
	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {
		//can't be true as will always work in send mode
		if(logger.isInfoEnabled())
			logger.info("Leaving TimerHandler validateMessage() with false status  as receive mode is not supported");
		return false;
	}



}

package com.agnity.simulator.tasks;

import java.util.Timer;

import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.NodeManager;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.type.StartNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.handlers.factory.HandlerFactory;
import com.agnity.simulator.logger.SuiteLogger;
import com.agnity.simulator.statistics.Counters;
import com.agnity.simulator.utils.Constants;

public class InitiateCallTask implements Runnable {

	private static Logger logger = Logger.getLogger(InitiateCallTask.class);

	private Timer timer;

	public InitiateCallTask(Timer timer) {
		this.timer = timer;
	}

	@Override
	public void run() {
		if(logger.isDebugEnabled())
			logger.debug("initiateCall Triggered");
		initiateCall();
		if(logger.isDebugEnabled())
			logger.debug("initiateCall() complete()");
	}


	private void initiateCall() {
		InapIsupSimServlet instance =InapIsupSimServlet.getInstance();
		NodeManager nodeManager =instance.getNodeManager();
		Node initailNode=nodeManager.getNextNode(null);
		if(initailNode == null  || !(initailNode instanceof StartNode)){
			logger.error("start node not present...invalid call flow xml.");
			return;
		}
		Node secondNode = nodeManager.getNextNode(initailNode.getNodeId());
		String action = secondNode.getAction();
		
		StringBuilder logString = new StringBuilder();
		logString.append("\r\n\r\n");
		logString.append("####################################################################################");
		logString.append("\r\n");
		logString.append("##############################NEW FLOW: ");
		logString.append(instance.getCurrentFileName());
		logString.append("#################################");
		logString.append("\r\n");
		logString.append("####################################################################################");
		
		SuiteLogger.getInstance().log(logString.toString());
		
		if(action.toLowerCase().equals(Constants.RECIEVE_ACTION.toLowerCase())){
			if(logger.isDebugEnabled())
				logger.debug("initiateCall() ->start in recieve mode.. exit");
			SuiteLogger.getInstance().log("WAIT for incoming MSG->FileName::["+instance.getCurrentFileName()+"] FLOW in recieve mode");
			timer.cancel();
			return;
		}	
		SuiteLogger.getInstance().log("FLOW STARTED->FileName::["+instance.getCurrentFileName()+"]");
		Counters.getInstance().incrementTotalCalls();
		if(logger.isDebugEnabled())
			logger.debug("initiateCall() initialnode recieved is::"+initailNode.getType());
		Handler handler = HandlerFactory.getHandler(initailNode);
		if(handler== null){
			logger.error("ERROR:::Handler NOT FOUND for node type::["+initailNode.getType()+"]  and id::["+initailNode.getNodeId()+"]");
			Counters.getInstance().incrementUnHandledNode();
			Counters.getInstance().incrementFailedCalls();
			SuiteLogger.getInstance().log("FAILED-->FileName::["+instance.getCurrentFileName()+"] Node id::["+initailNode.getNodeId()+
				"] Handler not defined in initite CALL");
			if(instance.isTestSuite()){
				if(logger.isDebugEnabled())
					logger.debug("InapIsupSimServlet initiateCall()-->Test suite; attempt next flow");
				instance.initializeAndStartFlow();
			}
			return;
		}

		if(logger.isDebugEnabled())
			logger.debug("initiateCall() handler recieved is::"+handler);
		handler.performAction(initailNode, new SimCallProcessingBuffer());

	}

}

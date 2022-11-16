package com.agnity.simulator.handlers;

import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.handlers.factory.HandlerFactory;
import com.agnity.simulator.logger.SuiteLogger;
import com.agnity.simulator.statistics.Counters;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;

public abstract class AbstractHandler implements Handler{

	private static Logger logger = Logger.getLogger(AbstractHandler.class);
	protected static final String RECEIVED_RESPONSE = "receivedRes".toLowerCase();
	private Counters counters;

	public AbstractHandler() {
		counters = Counters.getInstance();
	}

	@Override
	public void performAction(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isDebugEnabled())
			logger.debug("AbstractHandler performAction");
		if(simCpb == null || node == null){
			logger.error("Error  node or CPB is null");
			return;
		}
		InapIsupSimServlet instance = InapIsupSimServlet.getInstance();
		//setting last update timestamp for DEFAULT_TCAP_SESSION_TIMEOUT cases
		simCpb.setLastInvokeTime(System.currentTimeMillis());
		//processing the node
		boolean status= false;
		try{
			status = processNode(node, simCpb);
		}catch(Throwable th){
			status =false;
			logger.error("AbstractHandler performAction-->Unhandled exception in process node for NodeId::["+node.getNodeId()+
					"] Handler::["+this+"]",th);
			
		}
		//saving state after processing so that any intermediate message is not handled by next node
		simCpb.setCurrNode(node);
		simCpb.setExpectedNode(null);


		if(!status){
			if(logger.isDebugEnabled())
				logger.debug("AbstractHandler performAction-->processing failed Terminate");
			counters.incrementFailedCalls();
			Helper.cleanUpResources(simCpb, true);
			SuiteLogger.getInstance().log("FAILED-->FileName::["+instance.getCurrentFileName()+"] Node id::["+node.getNodeId()+
			"] process Node Fail to send message");
			if(instance.isTestSuite()){
				if(logger.isDebugEnabled())
					logger.debug("AbstractHandler performAction()-->Test suite; attempt next flow");
				instance.initializeAndStartFlow();
			}
			return;

		}
		//checking clean up is done then terminate happens with timer node if clenup has happened
		if(simCpb.isCleaned()){
			if(logger.isDebugEnabled())
				logger.debug("AbstractHandler performAction-->call cleaned.Terminating");
			counters.incrementFailedCalls();
			SuiteLogger.getInstance().log("FAILED-->FileName::["+instance.getCurrentFileName()+"] Node id::["+node.getNodeId()+
			"] Call already cleaned");
			if(instance.isTestSuite()){
				if(logger.isDebugEnabled())
					logger.debug("AbstractHandler performAction()-->Test suite; attempt next flow");
				instance.initializeAndStartFlow();
			}
			return;
		}
		Node nextNode= instance.getNodeManager().getNextNode(node);

		if(nextNode==null){
			if(logger.isDebugEnabled())
				logger.debug("next node not found...return and clean");
			counters.incrementScuccesCalls();
			simCpb.setCallSuccess(true);
			Helper.cleanUpResources(simCpb, true);
			SuiteLogger.getInstance().log("SUCCESS-->FileName::["+instance.getCurrentFileName()+"]");
			if(instance.isTestSuite()){
				if(logger.isDebugEnabled())
					logger.debug("AbstractHandler performAction()-->Test suite; attempt next flow");
				instance.initializeAndStartFlow();
			}
			return;
		}			
		String action =nextNode.getAction();
		//check action and do decison
		if(action.toLowerCase().equals(Constants.RECIEVE_ACTION.toLowerCase())){
			simCpb.setExpectedNode(nextNode);
			if(logger.isDebugEnabled())
				logger.debug("next node is reciuve wait for message");

		}else if(action.toLowerCase().equals(Constants.SEND_ACTION.toLowerCase())){
			Handler handler = HandlerFactory.getHandler(nextNode);
			if(handler== null){
				logger.error("ERROR:::Handler NOT FOUND for node type::["+nextNode.getType()+"]  and id::["+nextNode.getNodeId()+"]");
				counters.incrementUnHandledNode();
				counters.incrementFailedCalls();
				SuiteLogger.getInstance().log("FAILED-->FileName::["+instance.getCurrentFileName()+"] Node id::["+nextNode.getNodeId()+
				"] Node with undefined handler in performAction");
				if(instance.isTestSuite()){
					if(logger.isDebugEnabled())
						logger.debug("AbstractHandler performAction()-->Test suite; attempt next flow");
					instance.initializeAndStartFlow();
				}
				return;
			}
			handler.performAction(nextNode, simCpb);
		}else if(action.toLowerCase().equals(Constants.NO_ACTION.toLowerCase())){
			Handler handler = HandlerFactory.getHandler(nextNode);
			if(handler== null){
				logger.error("ERROR:::Handler NOT FOUND for node type::["+nextNode.getType()+"]  and id::["+nextNode.getNodeId()+"]");
				counters.incrementUnHandledNode();
				counters.incrementFailedCalls();
				SuiteLogger.getInstance().log("FAILED-->FileName::["+instance.getCurrentFileName()+"] Node id::["+nextNode.getNodeId()+
				"] Node with undefined handler in performAction");
				if(instance.isTestSuite()){
					if(logger.isDebugEnabled())
						logger.debug("AbstractHandler performAction()-->Test suite; attempt next flow");
					instance.initializeAndStartFlow();
				}
				return;
			}
			handler.performAction(nextNode, simCpb);
		}

	}

	@Override
	public void recieveMessage(Node node, SimCallProcessingBuffer simCpb,
			Object message) {
		if(logger.isDebugEnabled())
			logger.debug("AbstractHandler recieveMessage");
		if(simCpb == null || message == null || node ==null){
			logger.error("Error  node, message or CPB is null");
			return;
		}
		//setting last update timestamp for DEFAULT_TCAP_SESSION_TIMEOUT cases
		simCpb.setLastInvokeTime(System.currentTimeMillis());
		boolean status = false;
		boolean isValid = false;
		boolean isInitial = false;
		InapIsupSimServlet instance = InapIsupSimServlet.getInstance();
		//check if its inital handle
//		if(node.getAction().toLowerCase().equals(Constants.NO_ACTION.toLowerCase())){
		if(node.isInitial()){
			if(logger.isDebugEnabled())
				logger.debug("Initial node is recived wait");
			try{
				status = processNode(node, simCpb);
			}catch(Throwable th){
				status =false;
				logger.error("AbstractHandler recieveMessage-->Unhandled exception in process node for NodeId::["+node.getNodeId()+
						"] Handler::["+this+"]",th);
			}
			isInitial=true;
		}else{
			//Not an initial node
			if(logger.isDebugEnabled())
				logger.debug("Intermediate recieve");
			//if valid message
			try{
				isValid = validateMessage(node,message,simCpb);
			}catch(Throwable th){
				isValid =false;
				logger.error("AbstractHandler recieveMessage-->Unhandled exception in validateMessage for NodeId::["+node.getNodeId()+
						"] Handler::["+this+"]",th);
			}
			
			if(isValid){
				//processing
				if(logger.isDebugEnabled())
					logger.debug("Validation on reciecve successfull");
				SuiteLogger.getInstance().log("VALID MESSAGE-->FileName::["+instance.getCurrentFileName()+"] Node id::["+node.getNodeId()+
				"] Processing recieved messgae");
				try{
					status = processRecievedMessage(node, simCpb, message);
				}catch(Throwable th){
					status =false;
					logger.error("AbstractHandler recieveMessage-->Unhandled exception in validateMessage for NodeId::["+node.getNodeId()+
							"] Handler::["+this+"]",th);
				}
			}else{
				if(logger.isDebugEnabled())
					logger.debug("Validation on reciecve failed");
				counters.incrementUnExpectedMessages();
				SuiteLogger.getInstance().log("UNEXPECTED MESSAGE-->FileName::["+instance.getCurrentFileName()+"] Node id::["+node.getNodeId()+
				"] Return without processing");
				return;
			}
		}//@END processing of node

		//saving state after processing so that any intermediate message is not handled by next node
		simCpb.setCurrNode(node);
		simCpb.setExpectedNode(null);

		//check if processing failed
		if(!status){
			if(logger.isDebugEnabled())
				logger.debug("AbstractHandler recieveMessage-->processing failed Terminate");
			counters.incrementFailedCalls();
			Helper.cleanUpResources(simCpb, true);
			SuiteLogger.getInstance().log("FAILED-->FileName::["+instance.getCurrentFileName()+"] Node id::["+node.getNodeId()+
			"] process of recieved messgae failed");
			if(instance.isTestSuite()){
				if(logger.isDebugEnabled())
					logger.debug("AbstractHandler recieveMessage()-->Test suite; attempt next flow");
				instance.initializeAndStartFlow();
			}
			return;

		}			
		//checking clean up is done then terminate
		if(simCpb.isCleaned()){
			if(logger.isDebugEnabled())
				logger.debug("AbstractHandler recieveMessage-->call cleaned.Terminating");
			counters.incrementFailedCalls();
			SuiteLogger.getInstance().log("FAILED-->FileName::["+instance.getCurrentFileName()+"] Node id::["+node.getNodeId()+
			"] call already cleaned");
			if(instance.isTestSuite()){
				if(logger.isDebugEnabled())
					logger.debug("AbstractHandler recieveMessage()-->Test suite; attempt next flow");
				instance.initializeAndStartFlow();
			}
			return;
		}

		//Processing for next node
		Handler handler = null;
		Node nextNode= instance.getNodeManager().getNextNode(node);
		if(nextNode==null){
			if(logger.isDebugEnabled())
				logger.debug("next node not found...return and clean");
			counters.incrementScuccesCalls();
			simCpb.setCallSuccess(true);
			Helper.cleanUpResources(simCpb, true);
			SuiteLogger.getInstance().log("SUCCESS-->FileName::["+instance.getCurrentFileName()+"]");
			if(instance.isTestSuite()){
				if(logger.isDebugEnabled())
					logger.debug("AbstractHandler performAction()-->Test suite; attempt next flow");
				instance.initializeAndStartFlow();
			}
			return;
		}
		String action =nextNode.getAction();
		//check action and do decison
		
		if(isInitial){
			if(action.toLowerCase().equals(Constants.RECIEVE_ACTION.toLowerCase())){
				SuiteLogger.getInstance().log("FLOW STARTED->FileName::["+instance.getCurrentFileName()+"]");
				counters.incrementTotalCalls();
				simCpb.setExpectedNode(nextNode);
				if(logger.isDebugEnabled())
					logger.debug("Initialization on receive...move to next node processing in recieve mode");

				handler = HandlerFactory.getHandler(nextNode);
				if(handler== null){
					logger.error("ERROR:::Handler NOT FOUND for node type::["+nextNode.getType()+"]  and id::["+nextNode.getNodeId()+"]");
					counters.incrementUnHandledNode();
					counters.incrementFailedCalls();
					SuiteLogger.getInstance().log("FAILED-->FileName::["+instance.getCurrentFileName()+"]   Node id::["+nextNode.getNodeId()+
					"] Handler not found in recievMessage");
					if(instance.isTestSuite()){
						if(logger.isDebugEnabled())
							logger.debug("AbstractHandler recieveMessage()-->Test suite; attempt next flow");
						instance.initializeAndStartFlow();
					}
					return;
				}
				handler.recieveMessage(nextNode, simCpb, message);
			}else{
				if(logger.isDebugEnabled())
					logger.debug("First Node not in recieve mode after initial node");
				counters.incrementUnExpectedMessages();
				SuiteLogger.getInstance().log("UNEXPECTED MESSAGE-->FileName::["+instance.getCurrentFileName()+"] Node id::["+nextNode.getNodeId()+
				"] Return without processing");
				return;
			}
			
		}else if(action.toLowerCase().equals(Constants.RECIEVE_ACTION.toLowerCase())){
			simCpb.setExpectedNode(nextNode);
			if(logger.isDebugEnabled())
				logger.debug("next node is reciuve wait for message");

		}else if(action.toLowerCase().equals(Constants.SEND_ACTION.toLowerCase())){
			if(logger.isDebugEnabled())
				logger.debug("next node isSend..acting on same");

			handler = HandlerFactory.getHandler(nextNode);
			if(handler== null){
				logger.error("ERROR:::Handler NOT FOUND for node type::["+nextNode.getType()+"]  and id::["+nextNode.getNodeId()+"]");
				counters.incrementUnHandledNode();
				counters.incrementFailedCalls();
				SuiteLogger.getInstance().log("FAILED-->FileName::["+instance.getCurrentFileName()+"]  Node id::["+nextNode.getNodeId()+
				"] Handler not found in recievMessage");
				if(instance.isTestSuite()){
					if(logger.isDebugEnabled())
						logger.debug("AbstractHandler recieveMessage()-->Test suite; attempt next flow");
					instance.initializeAndStartFlow();
				}

				return;
			}
			handler.performAction(nextNode, simCpb);
		}else if(action.toLowerCase().equals(Constants.NO_ACTION.toLowerCase())){
			if(logger.isDebugEnabled())
				logger.debug("next node is Special node with no send recive actions.acting on same");

			handler = HandlerFactory.getHandler(nextNode);
			if(handler== null){
				logger.error("ERROR:::Handler NOT FOUND for node type::["+nextNode.getType()+"]  and id::["+nextNode.getNodeId()+"]");
				counters.incrementUnHandledNode();
				counters.incrementFailedCalls();
				SuiteLogger.getInstance().log("FAILED-->FileName::["+instance.getCurrentFileName()+"]  Node id::["+nextNode.getNodeId()+
				"] Handler not found in recievMessage");
				if(instance.isTestSuite()){
					if(logger.isDebugEnabled())
						logger.debug("AbstractHandler recieveMessage()-->Test suite; attempt next flow");
					instance.initializeAndStartFlow();
				}
				return;
			}
			handler.performAction(nextNode, simCpb);
		}//@END:ACtion base descions in IF statement

	}

	/**
	 * 
	 * @param node
	 * @param simCpb
	 * @return true in case of successful processing else false
	 */
	protected abstract boolean processNode(Node node, SimCallProcessingBuffer simCpb);

	/**
	 * 
	 * @param node
	 * @param simCpb
	 * @param message
	 * @return true in case of successful processing else false
	 */
	protected abstract boolean processRecievedMessage(Node node, SimCallProcessingBuffer simCpb,
			Object message);

	/**
	 * 
	 * @param node
	 * @param message
	 * @param simCpb
	 * @return true if validation succeeds else false
	 */
	protected abstract boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb);
}

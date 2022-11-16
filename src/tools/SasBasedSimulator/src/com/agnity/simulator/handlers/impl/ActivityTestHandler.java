package com.agnity.simulator.handlers.impl;

import java.io.IOException;

import jain.ParameterNotSetException;
import jain.protocol.ss7.tcap.DialogueReqEvent;
import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.ResultReqEvent;

import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.SimulatorConfig;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;

public class ActivityTestHandler extends AbstractHandler{

	private static Logger logger = Logger.getLogger(ActivityTestHandler.class);
	private static Handler handler;

	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (ActivityTestHandler.class) {
				if(handler ==null){
					handler = new ActivityTestHandler();
				}
			}
		}
		return handler;
	}

	private ActivityTestHandler(){

	}

	@Override
	public void performAction(Node node, SimCallProcessingBuffer simCpb) {
		
	}
	@Override
	public void recieveMessage(Node node, SimCallProcessingBuffer simCpb,
			Object message) {
		this.processRecievedMessage(node, simCpb, message);
	}
	
	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
				return true;

	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {
		InvokeIndEvent activityTest = (InvokeIndEvent) message;
		SimulatorConfig config = InapIsupSimServlet.getInstance().getConfigData();
		String sendActivityTestResp = config.getActivityTestResponse();
		if (sendActivityTestResp.toLowerCase().equals("true")){
			ResultReqEvent activityTestResp = new ResultReqEvent(InapIsupSimServlet.getInstance());
			try{
				if (activityTest.isInvokeIdPresent())
					activityTestResp.setInvokeId(activityTest.getInvokeId());
				if (activityTest.isDialogueIdPresent())
					activityTestResp.setDialogueId(activityTest.getDialogueId());
				activityTestResp.setLastResultEvent(true);
				activityTestResp.setOperation(activityTest.getOperation());
				Helper.sendComponent(activityTestResp, simCpb);
				DialogueReqEvent reqDialogEvent = Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(),Constants.DIALOG_CONTINUE, simCpb);
				Helper.sendDialogue(reqDialogEvent, simCpb);
			}catch (ParameterNotSetException e) {
				if(logger.isDebugEnabled())
					logger.debug("Parameter not set exception while sending Activity test response",e);
				return false;
			}catch (IOException e) {
				if(logger.isDebugEnabled())
					logger.debug("IO Exception while sending Activity test response",e);
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {
		
		return true;
	}
}

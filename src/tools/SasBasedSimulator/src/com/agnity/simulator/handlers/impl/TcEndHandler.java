package com.agnity.simulator.handlers.impl;

import jain.MandatoryParameterNotSetException;
import jain.protocol.ss7.tcap.DialogueIndEvent;
import jain.protocol.ss7.tcap.DialogueReqEvent;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;

public class TcEndHandler extends AbstractHandler{

	private static Logger logger = Logger.getLogger(TcEndHandler.class);
	private static Handler handler;

	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (TcEndHandler.class) {
				if(handler ==null){
					handler = new TcEndHandler();
				}
			}
		}
		return handler;
	}

	private TcEndHandler(){

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside TcEndHandler processNode()");

		if(!(node.getType().equals(Constants.TC_END))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}			

		DialogueReqEvent dialogEvent=Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(),Constants.DIALOG_END,simCpb);
		try {
			if(logger.isDebugEnabled())
				logger.debug("TcEndHandler processNode()-->sending created dialog ["+dialogEvent+"]");
			Helper.sendDialogue(dialogEvent, simCpb);
		} catch (MandatoryParameterNotSetException e) {
			logger.error("Mandatory param excpetion sending Dialog ::"+Constants.DIALOG_END,e);
			return false;
		} catch (IOException e) {
			logger.error("IOException excpetion sending Dialog ::"+Constants.DIALOG_END,e);
			return false;
		}

		if(logger.isInfoEnabled())
			logger.info("Leaving TcEndHandler processNode() with true");
		return true;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for TcEndHandler");

		List<Node> subElements =node.getSubElements();
		if(subElements.size() == 0){
			if(logger.isDebugEnabled())
				logger.debug("TcEndHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}

		if(logger.isDebugEnabled())
			logger.debug("TcEndHandler processRecievedMessage()->subelemnts present but " +
			"handling is not defined so returning from handler");
		return true;



	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {

		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for TcEndHandler");

		if(!(message instanceof DialogueIndEvent)){
			if(logger.isDebugEnabled())
				logger.debug("Not an DialogueIndEvent message");
			return false;
		}

		if(!( node.getType().equals(Constants.TC_END) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a TcEnd Node");
			return false;
		}

		if(!(node.getAction().toLowerCase().equals(Constants.RECIEVE_ACTION.toLowerCase()))){
			if(logger.isDebugEnabled())
				logger.debug("Not a Recieve Action Node");
			return false;
		}
		TcapNode tcapNode = (TcapNode) node;

		int dialogType = simCpb.getLastDialoguePrimitive();
		boolean isValid=false;
		if(dialogType==tcapNode.getDialogType()){
			isValid=true;
		}
		if(logger.isDebugEnabled())
			logger.debug("TcEndHandler validateMessage() isValid::["+isValid+"]  Expected DialogType::["+ tcapNode.getDialogType()+ 
					"] Actual DialogType::["+dialogType+"]");
		return isValid;
	}



}

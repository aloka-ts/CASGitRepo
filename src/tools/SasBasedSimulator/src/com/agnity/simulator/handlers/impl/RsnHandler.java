package com.agnity.simulator.handlers.impl;

import jain.MandatoryParameterNotSetException;
import jain.ParameterNotSetException;
import jain.protocol.ss7.SccpUserAddress;
import jain.protocol.ss7.tcap.DialogueReqEvent;
import jain.protocol.ss7.tcap.TcapConstants;
import jain.protocol.ss7.tcap.component.InvokeReqEvent;
import jain.protocol.ss7.tcap.ComponentReqEvent;
import jain.protocol.ss7.tcap.component.Parameters;
import jain.protocol.ss7.tcap.dialogue.UnidirectionalReqEvent;
import jain.protocol.ss7.SignalingPointCode;
import jain.protocol.ss7.tcap.component.Operation;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import javax.servlet.sip.SipURI;

import org.apache.log4j.Logger;


import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Helper;
import com.agnity.simulator.utils.Constants;
import com.genband.inap.asngenerated.RestartNotificationArg;
import com.genband.inap.asngenerated.RestartedNodeID;
import com.genband.inap.datatypes.ScfId;
import com.genband.inap.enumdata.GTIndicatorEnum;
import com.genband.inap.enumdata.RoutingIndicatorEnum;
import com.genband.inap.enumdata.SPCIndicatorEnum;
import com.genband.inap.enumdata.SSNIndicatorEnum;
import com.genband.inap.operations.InapOpCodes;
import com.genband.inap.operations.InapOperationsCoding;


public class RsnHandler extends AbstractHandler{

	private static Logger logger = Logger.getLogger(RsnHandler.class);
	private static Handler handler;
	//private static RsnHandler rsnHandler;

	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (RsnHandler.class) {
				if(handler ==null){
					handler = new RsnHandler();
				}
			}
		}
		return handler;
	}

	public RsnHandler(){

	}
	
	public void callProcessNode()
	{
		processNode(null,null);
	}
		
	@Override
	public void recieveMessage(Node node, SimCallProcessingBuffer simCpb, Object message)
	{
		processRecievedMessage(node,simCpb,message);
	}
	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		
		
		LinkedList<byte[]> outList = new LinkedList<byte[]>();
		try {
			RestartedNodeID rsnNodeId = new RestartedNodeID();

			SccpUserAddress orignatingAddr = InapIsupSimServlet.getInstance()
					.getLocalAddrs().get(0);
			
			byte[] ac = ScfId.encodeScfId(SPCIndicatorEnum.SPC_PRESENT,
					SSNIndicatorEnum.SSN_PRESENT,

					GTIndicatorEnum.NO_GT, RoutingIndicatorEnum.ROUTING_PC_SSN,

					orignatingAddr.getSubSystemAddress()
							.getSignalingPointCode().getZone(),

					orignatingAddr.getSubSystemAddress()
							.getSignalingPointCode().getCluster(),

					orignatingAddr.getSubSystemAddress()
							.getSignalingPointCode().getMember(),

					orignatingAddr.getSubSystemAddress().getSubSystemNumber());

			rsnNodeId.setValue(ac);

			RestartNotificationArg rsnArg = new RestartNotificationArg();

			Collection<RestartedNodeID> rsnNodeIdList = new LinkedList<RestartedNodeID>();

			rsnNodeIdList.add(rsnNodeId);

			rsnArg.setRestartedNodeIDs(rsnNodeIdList);

			LinkedList<Object> opObjects = new LinkedList<Object>();

			LinkedList<String> opCodes = new LinkedList<String>();

			opObjects.add(rsnArg);

			opCodes.add(InapOpCodes.RESTART_NOTIFICATION);

			outList = InapOperationsCoding.encodeOperations(opObjects, opCodes);
		} catch (Exception e) {
			e.printStackTrace();
		}

		byte[] rsnByte = outList.get(0);

		byte[] RsnOpCode = { 0x01 };
		
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL,RsnOpCode);
		InvokeReqEvent ire = new InvokeReqEvent(InapIsupSimServlet.getInstance(),Constants.dialogueIdRSN,requestOp);

		ire.setInvokeId(Constants.invokeIdRSN);
		ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SINGLE,
				rsnByte));
		// ire.setClassType(); havn't set classtype here??
		try {
		ComponentReqEvent cre = (ComponentReqEvent)ire;
		if (logger.isDebugEnabled())
			logger
			.debug("RsnHandler processNode()-->reqEvent created, sending component["
					+ ire + "]>>>"+cre.getDialogueId());
		// sending component
		try {
			Helper.sendComponent(ire, null);
		} catch (ParameterNotSetException e) {
			logger.error("ParameterNotSetException sending idp component", e);
			return false;
		}
		if (logger.isDebugEnabled())
			logger.debug("RsnHandler processNode()-->component send");

		if (logger.isDebugEnabled())
			logger.debug("RsnHandler processNode()--> creating dialog");

		// sending dialogue

		DialogueReqEvent dialogEvent = Helper.createDialogReqEvent(
				InapIsupSimServlet.getInstance(), "unidirectional", null);
		
			if (logger.isDebugEnabled())
				logger.debug("RsnHandler processNode()-->sending created dialog ["+ dialogEvent + "]");
			Helper.sendDialogue(dialogEvent, null);
		} catch (MandatoryParameterNotSetException e) {
			logger.error("Mandatory param excpetion sending Dialog on Rsn::" ,e);
			return false;
		} catch (IOException e) {
			logger.error("IOException excpetion sending Dialog on Rsn::" , e);
			return false;
		}

		return true;

	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {
		return true;
	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {
		
		return true;
	}
	
	
	}

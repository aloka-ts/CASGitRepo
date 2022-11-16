package com.agnity.simulator.handlers.impl;

import jain.MandatoryParameterNotSetException;
import jain.ParameterNotSetException;
import jain.protocol.ss7.tcap.DialogueIndEvent;
import jain.protocol.ss7.tcap.dialogue.DialogueConstants;
import jain.protocol.ss7.tcap.dialogue.UserAbortIndEvent;
import jain.protocol.ss7.tcap.dialogue.UserAbortReqEvent;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.child.FieldElem;
import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.genband.inap.util.Util;

public class UAbortHandler extends AbstractHandler{

	private static Logger logger = Logger.getLogger(UAbortHandler.class);
	private static Handler handler;
	
	private byte[] uAbortInfo=null;

	private static final String U_ABORT_REASON = "reason".toLowerCase();
	private static final String U_ABORT_INFO = "abortinformation".toLowerCase();
	
	private enum AbortInfoEnum{

		NO_REASON_GIVEN(1),APPLICATION_TIMER_EXPIRED(2),PROTOCOL_PROHIBITED_SIGNAL_RECIEVED(3),ABNORMAL_PROCESSING(4),CONGESTION(5),
		AC_NEGOTIATION_FAILED(6),UNRECOGNIZED_EXTENSION_PARAMETER(7);
		
		private AbortInfoEnum(int i){
			this.code=i;
		}
		private int code;

		public int getCode() {
			return code;
		}
		
		public static AbortInfoEnum fromInt(int num) {
			AbortInfoEnum abortInfo=NO_REASON_GIVEN;
			switch (num) {
				case 1: { 
					abortInfo= NO_REASON_GIVEN; 
					break;
				}
				case 2: { 
					abortInfo= APPLICATION_TIMER_EXPIRED;
					break;
				}
				case 3: { 
					abortInfo= PROTOCOL_PROHIBITED_SIGNAL_RECIEVED;
					break;
				}
				case 4: { 
					abortInfo= ABNORMAL_PROCESSING;
					break;
				}
				case 5: { 
					abortInfo= CONGESTION;
					break;
				}
				case 6: { 
					abortInfo= AC_NEGOTIATION_FAILED;
					break;
				}
				case 7: { 
					abortInfo= UNRECOGNIZED_EXTENSION_PARAMETER;
					break;
				}
			}//@End Switch
			return abortInfo;
		}
	}
	
	//reason values
	private static final String U_ABORT_REASON_ACN_NOT_SUPPPORTED = "ABORT_REASON_ACN_NOT_SUPPORTED";
	private static final String U_ABORT_REASON_USER_SPECIFIC = "ABORT_REASON_USER_SPECIFIC";
	
	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (UAbortHandler.class) {
				if(handler ==null){
					handler = new UAbortHandler();
				}
			}
		}
		return handler;
	}

	private UAbortHandler(){
		String fixedInfo= InapIsupSimServlet.getInstance().getConfigData().getUabortInfoFixedPart();
		if(fixedInfo !=null){
			byte[] tmpFixedInfo=Helper.hexStringToByteArray(fixedInfo);
			uAbortInfo =new byte[(tmpFixedInfo.length+1)];
			for(int i =0; i<tmpFixedInfo.length;i++)
				uAbortInfo[i]=tmpFixedInfo[i];
		}
		

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside UAbortHandler processNode()");

		if(!(node.getType().equals(Constants.U_ABORT))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}			
			
		List<Node> subElements =node.getSubElements();
		Iterator<Node> subElemIterator = subElements.iterator();
		
		
		UserAbortReqEvent dialogEvent=(UserAbortReqEvent) Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(),Constants.DIALOG_U_ABORT,simCpb);
		
		//aded for bug 10342
		setUAbortFields(simCpb, subElemIterator, dialogEvent);
		
		
		
		
		try {
			if(logger.isDebugEnabled())
				logger.debug("RrbeHandler processNode()-->sending created dialog ["+dialogEvent+"]");
			Helper.sendDialogue(dialogEvent, simCpb);
		} catch (MandatoryParameterNotSetException e) {
			logger.error("Mandatory param excpetion sending Dialog ::"+Constants.DIALOG_U_ABORT,e);
			return false;
		} catch (IOException e) {
			logger.error("IOException excpetion sending Dialog ::"+Constants.DIALOG_U_ABORT,e);
			return false;
		}

		if(logger.isInfoEnabled())
			logger.info("Leaving UAbortHandler processNode() with true");
		return true;
	}

	
	//set fileds of u abort if present added for bug 10342
	private void setUAbortFields(SimCallProcessingBuffer simCpb, Iterator<Node> fieldElemIterator,
			UserAbortReqEvent dialogEvent) {

		Node subElem =null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();


		//adding variables to CPB
		while(fieldElemIterator.hasNext()){
			subElem = fieldElemIterator.next();

			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem =(FieldElem) subElem;
				String fieldName = fieldElem.getFieldType();
				if(fieldName.equals(U_ABORT_REASON)){

					String reason = fieldElem.getValue(varMap);
					if(reason.equals(U_ABORT_REASON_ACN_NOT_SUPPPORTED)){
						if(logger.isDebugEnabled())
							logger.debug("set uabort reason as ACN not supported");
						dialogEvent.setAbortReason(DialogueConstants.ABORT_REASON_ACN_NOT_SUPPORTED);
					}else if(reason.equals(U_ABORT_REASON_USER_SPECIFIC)){
						if(logger.isDebugEnabled())
							logger.debug("set uabort reason as USER specific");
						dialogEvent.setAbortReason(DialogueConstants.ABORT_REASON_USER_SPECIFIC);
					}else{
						if(logger.isDebugEnabled())
							logger.debug("Unknonwn u abort reason::"+reason);
					}
				}else if(fieldName.equals(U_ABORT_INFO)){
					
					String info = fieldElem.getValue(varMap);
					int code = AbortInfoEnum.valueOf(info).getCode();
					int pos=uAbortInfo.length-1;
					uAbortInfo[pos]=(byte) code;
					dialogEvent.setUserAbortInformation(uAbortInfo);
				}else{
					if(logger.isDebugEnabled())
						logger.debug("UAbortHandler Ignore invalid field name::"+fieldName);
				}//@End:complete field type checks
			}//complete if subelem is field
		}//complete while
		
	}
	
		
	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for UAbortHandler");

		List<Node> subElements =node.getSubElements();
		
		UserAbortIndEvent dlgIndEvent = (UserAbortIndEvent)message;
		
		//priniting user information
		try{
		if(dlgIndEvent.isAbortReasonPresent() && logger.isDebugEnabled())
			logger.debug("Uabort dialog reason is:::["+dlgIndEvent.getAbortReason()+"]");
		
		if(dlgIndEvent.isUserAbortInformationPresent() && logger.isDebugEnabled()){
			byte[] abortInfo=dlgIndEvent.getUserAbortInformation();
			logger.debug("Uabort dialog INfo byte array  is:::["+Util.formatBytes(abortInfo)+"]");
			
			int pos = abortInfo.length-1;
			int info=abortInfo[pos];
			logger.debug("Uabort dialog INfo is:::["+AbortInfoEnum.fromInt(info)+"]");
			
		}
		}catch(ParameterNotSetException e){
			logger.error("Got parameter not set exception even after is Present check",e);
		}
		
		if(subElements.size() == 0){
			if(logger.isDebugEnabled())
				logger.debug("UAbortHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}

		if(logger.isDebugEnabled())
			logger.debug("UAbortHandler processRecievedMessage()->subelemnts present but " +
			"handling is not defined so returning from handler");
		return true;



	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {

		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for UAbortHandler");

		if(!(message instanceof DialogueIndEvent)){
			if(logger.isDebugEnabled())
				logger.debug("Not and DialogueIndEvent message");
			return false;
		}

		if(!( node.getType().equals(Constants.U_ABORT) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a UAbort Node");
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
			if(logger.isDebugEnabled())
				logger.debug("UAbortHandler validateMessage() isValid::["+isValid+"]  Expected DialogType::["+ tcapNode.getDialogType()+ 
						"] Actual DialogType::["+dialogType+"]");
		} 
		return isValid;
	}



}

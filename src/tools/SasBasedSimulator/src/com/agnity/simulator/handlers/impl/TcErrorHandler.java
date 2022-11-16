package com.agnity.simulator.handlers.impl;

import jain.MandatoryParameterNotSetException;
import jain.ParameterNotSetException;
import jain.protocol.ss7.tcap.DialogueReqEvent;
import jain.protocol.ss7.tcap.component.ComponentConstants;
import jain.protocol.ss7.tcap.component.ErrorIndEvent;
import jain.protocol.ss7.tcap.component.ErrorReqEvent;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.child.FieldElem;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.TcErrorNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.genband.inap.util.Util;

public class TcErrorHandler extends AbstractHandler{

	private static Logger logger = Logger.getLogger(TcErrorHandler.class);
	private static Handler handler;

	private static final String ERROR_TYPE = "errorType".toLowerCase();
	private static final String ERROR_CODE = "errorCode".toLowerCase();

	private enum ErrorCodeEnum{

		ETCFAILED(3),MISSINGCUSTOMERRECORD(6),MISSINGPARAMETER(7),PARAMETEROUTOFRANGE(8),SYSTEMFAILURE(11),TASKREFUSED(12),UNEXPECTEDCOMPONENTSEQUENCE(14),
		UNEXPECTEDDATAVALUE(15),UNEXPECTEDPARAMETER(16),UNKNOWNLEGID(17),ITCFAILED(-128);

		private ErrorCodeEnum(int i){
			this.code=i;
		}
		private int code;

		public int getCode() {
			return code;
		}

		public static ErrorCodeEnum fromInt(int num) {
			ErrorCodeEnum errorCode=null;
			switch (num) {
			case 3: { 
				errorCode= ETCFAILED; 
				break;
			}
			case 6: { 
				errorCode= MISSINGCUSTOMERRECORD;
				break;
			}
			case 7: { 
				errorCode= MISSINGPARAMETER;
				break;
			}
			case 8: { 
				errorCode= PARAMETEROUTOFRANGE;
				break;
			}
			case 11: { 
				errorCode= SYSTEMFAILURE;
				break;
			}
			case 12: { 
				errorCode= TASKREFUSED;
				break;
			}
			case 14: { 
				errorCode= UNEXPECTEDCOMPONENTSEQUENCE;
				break;
			}
			case 15: { 
				errorCode= UNEXPECTEDDATAVALUE;
				break;
			}
			case 16: { 
				errorCode= UNEXPECTEDPARAMETER;
				break;
			}
			case 17: { 
				errorCode= UNKNOWNLEGID;
				break;
			}
			case -128: { 
				errorCode= ITCFAILED;
				break;
			}
			}//@End Switch
			return errorCode;
		}
	}

	//error Type
	private static final String ERROR_LOCAL = "ERROR_LOCAL";
	private static final String ERROR_GLOBAL = "ERROR_GLOBAL";


	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (TcErrorHandler.class) {
				if(handler ==null){
					handler = new TcErrorHandler();
				}
			}
		}
		return handler;
	}

	private TcErrorHandler(){

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside TcErrorHandler processNode()");

		if(!(node.getType().equals(Constants.TC_ERROR))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}
		TcErrorNode errorNode = (TcErrorNode) node;

		List<Node> subElements =node.getSubElements();
		Iterator<Node> subElemIterator = subElements.iterator();
		ErrorReqEvent erroReq =null;

		if(subElemIterator.hasNext()){
			//creating components and setting fields
			erroReq=createErrorReqEvent(InapIsupSimServlet.getInstance(),simCpb,subElemIterator); 

		}

		if(erroReq==null){
			logger.error("Recieved erroReq as null");
			return false;
		}

		if(logger.isDebugEnabled())
			logger.debug("ErroHandlerr processNode()-->reqEvent created, sending component["+erroReq+"]");
		//sending componentr
		try {
			Helper.sendComponent(erroReq, simCpb);
		} catch (ParameterNotSetException e1) {
			logger.error(Util.toString(simCpb.getDialogId())+ " param excpetion sending TcError component",e1);
			return false;
		}

		if(errorNode.isLastMessage()){
			if(logger.isDebugEnabled())
				logger.debug("ErrorHandler processNode()-->last message sending dialog also creating dialog::["+errorNode.getDialogAs()+"]");
			DialogueReqEvent dialogEvent=Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(),errorNode.getDialogAs(),simCpb);
			try {
				if(logger.isDebugEnabled())
					logger.debug("ErrorHandler processNode()-->sending created dialog ["+dialogEvent+"]");
				Helper.sendDialogue(dialogEvent, simCpb);
			} catch (MandatoryParameterNotSetException e) {
				logger.error("Mandatory param excpetion sending Dialog on Error::"+errorNode.getDialogAs(),e);
				return false;
			} catch (IOException e) {
				logger.error("IOException excpetion sending Dialog on Error::"+errorNode.getDialogAs(),e);
				return false;
			}
		}

		if(logger.isInfoEnabled())
			logger.info("Leaving TcErrorHandler processNode() with true");
		return true;
	}

	private ErrorReqEvent createErrorReqEvent(Object source,SimCallProcessingBuffer simCpb, Iterator<Node> fieldElemIterator) {
		if(logger.isInfoEnabled())
			logger.info(Util.toString(simCpb.getDialogId())+ " creating Error component");


		ErrorReqEvent errorReqEvent=new ErrorReqEvent(source);
		errorReqEvent.setInvokeId(simCpb.incrementAndGetInvokeId());
		errorReqEvent.setDialogueId(simCpb.getDialogId());

		Node subElem =null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();


		//adding variables to CPB
		while(fieldElemIterator.hasNext()){
			subElem = fieldElemIterator.next();

			if(subElem.getType().equals(Constants.FIELD)){
				fieldElem =(FieldElem) subElem;
				String fieldName = fieldElem.getFieldType();
				if(fieldName.equals(ERROR_TYPE)){

					String errorType = fieldElem.getValue(varMap);
					if(errorType.equals(ERROR_GLOBAL)){
						if(logger.isDebugEnabled())
							logger.debug("set error type as global");
						errorReqEvent.setErrorType(ComponentConstants.ERROR_GLOBAL);
					}else if(errorType.equals(ERROR_LOCAL)){
						if(logger.isDebugEnabled())
							logger.debug("set error type as local");
						errorReqEvent.setErrorType(ComponentConstants.ERROR_LOCAL);
					}else{
						if(logger.isDebugEnabled())
							logger.debug("unknown error type::"+errorType);
					}
				}else if(fieldName.equals(ERROR_CODE)){
					if(logger.isDebugEnabled())
						logger.debug("setting error code");

					String errorcode = fieldElem.getValue(varMap);
					if(logger.isDebugEnabled())
						logger.debug("setting error code::"+errorcode);
					byte[] errorByte=null;
					if(errorcode.matches("[0-9A-Fa-f]*")){
						if(logger.isDebugEnabled())
							logger.debug("setting from hex string:"+errorcode);
						errorByte=Helper.hexStringToByteArray(errorcode);
					}else{
						if(logger.isDebugEnabled())
							logger.debug("setting from predefined value:"+errorcode);
						int code = ErrorCodeEnum.valueOf(errorcode).getCode();
						errorByte=new byte[]{(byte)code};
					}
					
					if(logger.isInfoEnabled())
						logger.info(Util.toString(simCpb.getDialogId())+ " ErrorreqEvent created with byte::"+Util.formatBytes(errorByte));

					errorReqEvent.setErrorCode(errorByte);
				}else{
					if(logger.isDebugEnabled())
						logger.debug("ErrorHandler Ignore invalid field name::"+fieldName);
				}//@End:complete field type checks
			}//complete if subelem is field
		}//complete while


		return errorReqEvent;
	}


	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for TcErrorHandler");

		List<Node> subElements =node.getSubElements();
		if(subElements.size() == 0){
			if(logger.isDebugEnabled())
				logger.debug("TcErrorHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}

		ErrorIndEvent receivedError = (ErrorIndEvent)message;

		try {
			if(logger.isDebugEnabled())
				logger.debug(Util.toString(receivedError.getDialogueId()) + "::: TcErrorHandler: Error Type :" + receivedError.getErrorType());
			byte[] error = receivedError.getErrorCode();
			if(logger.isDebugEnabled())
				logger.debug(Util.toString(receivedError.getDialogueId()) + "::: TcErrorHandler: Error :" + Util.formatBytes(error));
			if(logger.isDebugEnabled() && error.length==1)
				logger.debug(Util.toString(receivedError.getDialogueId()) + "::: TcErrorHandler: Error predefined:" + ErrorCodeEnum.fromInt(error[0]));
		} catch (MandatoryParameterNotSetException e) {
			logger.error("TcErrorHandler processRecievedMessage()->MandatoryParameterNotSetException",e);
			return false;
		}

		if(logger.isDebugEnabled())
			logger.debug("TcErrorHandler processRecievedMessage()->subelemnts present but " +
			"handling is not defined so returning from handler");
		return true;

	}


	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {

		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for TcErrorHandler");

		if(!(message instanceof ErrorIndEvent)){
			if(logger.isDebugEnabled())
				logger.debug("Not an ErrorIndEvent message");
			return false;
		}

		if(!( node.getType().equals(Constants.TC_ERROR) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a TcError  Node");
			return false;
		}

		if(!(node.getAction().toLowerCase().equals(Constants.RECIEVE_ACTION.toLowerCase()))){
			if(logger.isDebugEnabled())
				logger.debug("TcErrorHandler-->Not a Recieve Action Node");
			return false;
		}
		TcErrorNode tcapNode = (TcErrorNode) node;

		int dialogType = simCpb.getLastDialoguePrimitive();
		int dialogId = simCpb.getDialogId();

		ErrorIndEvent receivedError = (ErrorIndEvent)message;
		int primitiveType= receivedError.getPrimitiveType();
		int expectedPrimitive= tcapNode.getPrimitiveType();
		boolean isValid= false;
		if( (expectedPrimitive == primitiveType)   &&  (dialogType== tcapNode.getDialogType())  ) {
			isValid= true;
		}	
		if(logger.isDebugEnabled())
			logger.debug("TcErrorHandler validateMessage() "+Util.toString(dialogId)+"  isValid::["+isValid+
					"]  Expected primitiveType::["+expectedPrimitive+"] Actual primitiveType::["+primitiveType+
					"] Expected DialogType::["+ tcapNode.getDialogType()+"] Actual DialogType::["+dialogType+"]");

		return isValid;
	}



}

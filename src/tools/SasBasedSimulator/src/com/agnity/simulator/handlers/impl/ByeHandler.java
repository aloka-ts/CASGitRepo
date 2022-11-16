package com.agnity.simulator.handlers.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;

import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.child.FieldElem;
import com.agnity.simulator.callflowadaptor.element.child.SetElem;
import com.agnity.simulator.callflowadaptor.element.child.SubFieldElem;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.genband.isup.datatypes.Cause;
import com.genband.isup.enumdata.CauseValEnum;
import com.genband.isup.enumdata.CodingStndEnum;
import com.genband.isup.enumdata.LocationEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.messagetypes.RELMessage;
import com.genband.isup.operations.ISUPConstants;
import com.genband.isup.operations.ISUPOperationsCoding;
import com.genband.isup.util.Util;

public class ByeHandler extends AbstractHandler{

	private static final String REL_FIELD_CAUSE = "Cause".toLowerCase();
	
	private static final String REL_LOCATION_ENUM = "LocationEnum".toLowerCase();
	private static final String REL_CODING_STND_ENUM = "CodingStndEnum".toLowerCase();
	private static final String REL_CAUSE_VALUE_ENUM = "CauseValEnum".toLowerCase();
	
	private static final String REL_SET_CAUSE_BYTE = "Cause".toLowerCase();

	private static Logger logger = Logger.getLogger(ByeHandler.class);
	private static Handler handler;

	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (ByeHandler.class) {
				if(handler ==null){
					handler = new ByeHandler();
				}
			}
		}
		return handler;
	}

	private ByeHandler(){

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside ByeHandler processNode()");

		if(!(node.getType().equals(Constants.BYE))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}			
		SipServletRequest byeRequest = null;
		
		int leg = node.getSipLeg();
		SipServletMessage message = null;
		if(leg == 1)
			message = simCpb.getLastSipMessageLeg1();
		else if(leg == 2)
			message = simCpb.getLastSipMessageLeg2();
		else
			message = simCpb.getLastSipMessage();

		if(message != null) {
			byeRequest = message.getSession().createRequest("BYE");
			
			
			if(leg == 1)
				simCpb.setLastSipMessageLeg1(byeRequest);
			else if(leg == 2)
				simCpb.setLastSipMessageLeg2(byeRequest);
			else
				simCpb.setLastSipMessage(byeRequest);
		}else{
			logger.error("Last sip message can not be null for BYE");
			return false;
		}

		// Adding REL message to BYE request
		Node subElem =null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		RELMessage relMessage= null;

		List<Node> subElements =node.getSubElements();
		Iterator<Node> subElemIterator = subElements.iterator();
		//reading sub elements
		while(subElemIterator.hasNext()){
			subElem = subElemIterator.next();

			if(subElem.getType().equals(Constants.BODY)){
				FieldElem fieldElem = null;
				Node bodySubElem = null;
				relMessage = new RELMessage();
				// Message type will be Constant for REL
				relMessage.setMessageType(new byte[]{0x0c});

				Iterator<Node> bodySubElemIterator = subElem.getSubElements().iterator();

				while(bodySubElemIterator.hasNext()){
					bodySubElem = bodySubElemIterator.next();
					if(bodySubElem.getType().equals(Constants.FIELD)){
						fieldElem  = (FieldElem) bodySubElem; 
						String fieldName = fieldElem.getFieldType();

						if(fieldName.equals(REL_FIELD_CAUSE)){
							byte[] cause = null;
							Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();

							String locationEnum = subFieldElems.get(REL_LOCATION_ENUM).getValue(varMap);
							String codingStdEnum = subFieldElems.get(REL_CODING_STND_ENUM).getValue(varMap);
							String causeValueEnum = subFieldElems.get(REL_CAUSE_VALUE_ENUM).getValue(varMap);

							try {
								cause = Cause.encodeCauseVal(LocationEnum.valueOf(locationEnum), 
										CodingStndEnum.valueOf(codingStdEnum), 
										CauseValEnum.valueOf(causeValueEnum));

							} catch (InvalidInputException e) {
								logger.error("InvalidInputException encoding Cause in REL/BYE request",e);
								return false;
							} 
							relMessage.setCause(cause);
						}
					}
				}

				if(relMessage != null){
					LinkedList<byte[]> encode = null;
					LinkedList<Object> objLL = new LinkedList<Object>();
					LinkedList<String> opCode = new LinkedList<String>();

					objLL.add(relMessage);
					opCode.add(ISUPConstants.OP_CODE_REL);
					try {
						encode = ISUPOperationsCoding.encodeOperations(objLL, opCode);
						byte[] rel =  encode.get(0);
						Multipart mp = new MimeMultipart();	
						//adding REL to multi-part
						Helper.formMultiPartMessage(mp, rel, Constants.ISUP_CONTENT_TYPE);
						byeRequest.setContent(mp,mp.getContentType());

					} catch (MessagingException e) {
						logger.error("MessagingException creating and setting multipat message",e);
						return false;
					}catch (UnsupportedEncodingException e) {
						logger.error("UnsupportedEncodingException setting content",e);
						return false;
					}catch (Exception e) {
						logger.error("Exception encoding REL message",e);
						return false;
					}
				}
				
			} // BODY loop ends
		} // While loop ends

		try {
			byeRequest.getSession().setInvalidateWhenReady(false);
			byeRequest.send();
		} catch (IOException e) {
			logger.error("IOException sending BYE request",e);
			return false;
		}

		if(logger.isInfoEnabled())
			logger.info("Leaving ByeHandler processNode() with status true");
		return true;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {
		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for ByeHandler");

		if(!(node.getType().equals(Constants.BYE))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}	
		SipServletMessage byeReq = (SipServletMessage) message;
		
		int leg = node.getSipLeg();
		if(leg == 1)
			simCpb.setLastSipMessageLeg1(byeReq);
		else if(leg == 2)
			simCpb.setLastSipMessageLeg2(byeReq);
		else
			simCpb.setLastSipMessage(byeReq);
		
		List<Node> subElements =node.getSubElements();
		if(subElements.size() == 0){
			if(logger.isDebugEnabled())
				logger.debug("ByeHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true; 
		}
		
		try {			
			String contentType = byeReq.getContentType();
			//check if multipart content present
			byte[] isupContent= null;
			if(contentType!=null && contentType.startsWith(Constants.MULTIPART_CONTENT_TYPE)){
				if(logger.isDebugEnabled())
					logger.debug("ByeHandler processRecievedMessage()->Multipart content present");
				Multipart mp = (Multipart) byeReq.getContent();
				int bpCount =mp.getCount();
				if(logger.isDebugEnabled())
					logger.debug("ByeHandler processRecievedMessage()->Checking for ISUP content type["+Constants.ISUP_CONTENT_TYPE+"]");
				for(int i =0; i<bpCount;i++){
					BodyPart bp = mp.getBodyPart(i);
					if(logger.isDebugEnabled())
						logger.debug("ByeHandler processRecievedMessage()->Matching body part content type["+bp.getContentType()+"]");
					if(bp.getContentType().equals(Constants.ISUP_CONTENT_TYPE)){
						if(logger.isDebugEnabled())
							logger.debug("ByeHandler processRecievedMessage()->ISUP content found");
						ByteArrayInputStream bis=(ByteArrayInputStream) bp.getContent();	
						int bytes=bis.available();
						isupContent=new byte[bytes];
						bis.read(isupContent,0,bytes);
						break;
					}
				}//end for loop
			}else{
				logger.error("ByeHandler processRecievedMessage()->Multipart content type not present so return with false");
				return false; 
			}
			
			if(isupContent == null){
				logger.error("ByeHandler processRecievedMessage()->ISUP content not found so return with false");
				return false; 
			}
			if(logger.isDebugEnabled())
				logger.debug("ByeHandler processRecievedMessage()->Recived REL bytes::["+Util.formatBytes(isupContent)+"]");
			
			//parsing REL
			LinkedList<byte[]> byteList=new LinkedList<byte[]>();
			byteList.add(isupContent);
			LinkedList<String> opCodeList=new LinkedList<String>();
			opCodeList.add(ISUPConstants.OP_CODE_REL);

			List<Object> list= ISUPOperationsCoding.decodeOperations(byteList,opCodeList);
			RELMessage relMessage = (RELMessage) list.get(0);
			if(logger.isDebugEnabled())
				logger.debug("ByeHandler processRecievedMessage()->REL message parsed:: "+relMessage);
			
			//store rel fields
			storeFields(subElements,simCpb,relMessage);
			

		} catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException getting content",e);
			return false;
		} catch (IOException e) {
			logger.error("IOException getting content",e);
			return false;
		} catch (MessagingException e) {
			logger.error("MessagingException getting bodypart",e);
			return false;
		}
		
		

		if(logger.isDebugEnabled())
			logger.debug("leaving processRecievedMessage() for ByeHandler as true");
		return true;
	}

	private void storeFields(List<Node> subElements, SimCallProcessingBuffer simCpb, RELMessage relMessage) {
		Node subElem =null;
		SetElem setElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();
		Variable var = null;
		Iterator<Node> subElemIterator = subElements.iterator();

		while (subElemIterator.hasNext()) {
			subElem = subElemIterator.next();
			//only set subelem needs to be hanled..
			if(subElem.getType().equals(Constants.SET)){
				setElem =(SetElem) subElem;

				String varName = setElem.getVarName();
				var =varMap.get(varName);
				if(var == null){
					var = new Variable();
					var.setVarName(varName);
				}

				String varField = setElem.getVarField();
				String varVal = null;
				if(varField.equals(REL_SET_CAUSE_BYTE)){
					byte[] cause =relMessage.getCauseBytes();
					if(cause !=null){
							varVal = Helper.byteArrayToHexString(cause);
						}
				} 
				if(logger.isDebugEnabled())
					logger.debug("ByeHandler storeFields::variable::"+varName+" value::"+varVal);
				//finally storing variable
				var.setVarValue(varVal);
				simCpb.addVariable(var);

			}//end if check for set elem
		}//end while loop on subelem

	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {

		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for ByeHandler");

		if(!(message instanceof SipServletRequest)){
			if(logger.isDebugEnabled())
				logger.debug("Not a Sip request message");
			return false;
		}

		SipServletRequest sipRequest= (SipServletRequest) message;
		if(!( node.getType().equals(Constants.BYE) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a BYE Node");
			return false;
		}
		if(!(sipRequest.getMethod().equals("BYE")) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a BYE method");
			return false;
		}
		
		if(!(node.getAction().toLowerCase().equals(Constants.RECIEVE_ACTION.toLowerCase()))){
			if(logger.isDebugEnabled())
				logger.debug("Not a Recieve Action Node");
			return false;
		}
		
		//changes for b2b mode,this check is to avoid handling of an unexpected leg message
		if(InapIsupSimServlet.getInstance().isB2bMode){
			int leg = node.getSipLeg();
			String callId="";
			if(leg==1)
				callId = simCpb.getLastSipMessageLeg1().getCallId();
			else if(leg==2)
				callId = simCpb.getLastSipMessageLeg2().getCallId();
			if(!(callId.equalsIgnoreCase(((SipServletMessage) message).getCallId())))
			{
				logger.debug("Leaving validateMessage() for ByeHandler with status false as wrong leg mesaage handled");
				return false;
			}
		}
		
		if(logger.isDebugEnabled())
			logger.debug("Leaving validateMessage() for ByeHandler with status "+true);
		return true;

	}



}

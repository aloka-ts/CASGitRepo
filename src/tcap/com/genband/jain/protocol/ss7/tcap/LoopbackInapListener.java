package com.genband.jain.protocol.ss7.tcap;

import jain.InvalidAddressException;
import jain.MandatoryParameterNotSetException;
import jain.ParameterNotSetException;
import jain.protocol.ss7.SccpUserAddress;
import jain.protocol.ss7.UserAddressEmptyException;
import jain.protocol.ss7.UserAddressLimitException;
import jain.protocol.ss7.sccp.StateIndEvent;
import jain.protocol.ss7.tcap.ComponentIndEvent;
import jain.protocol.ss7.tcap.DialogueIndEvent;
import jain.protocol.ss7.tcap.TcapConstants;
import jain.protocol.ss7.tcap.TcapErrorEvent;
import jain.protocol.ss7.tcap.TcapUserAddress;
import jain.protocol.ss7.tcap.TimeOutEvent;
import jain.protocol.ss7.tcap.component.InvokeIndEvent;

import org.apache.log4j.Logger;

import com.genband.tcap.provider.TcapListener;
import com.genband.tcap.provider.TcapProvider;
import com.genband.tcap.provider.TcapSession;

public class LoopbackInapListener implements TcapListener {

	private static Logger		logger				= Logger.getLogger(LoopbackInapListener.class);

	private static Object		src					= "source".intern();
	private static final int	INVOKE_CLASS_TYPE	= 1;

	
	TcapProvider	tcapProvider;

	public LoopbackInapListener(TcapProvider tcapProvider) {
		this.tcapProvider = tcapProvider;

	}

	@Override
	public void processDialogueIndEvent(DialogueIndEvent dialogueIndEvent) {
		//loopbak behavior handled through this method;
//		if (logger.isDebugEnabled()) {
//			logger.debug("Inside processDialogueIndEvent-proceed loopbak behavior ardoded flow");
//		}
//		int id = dialogueIndEvent.getDialogueId();
//		
//		TcapSession ts = tcapProvider.getTcapSession(id);
//		String loopBackScenario = (String) ts.getAttribute("loopback");
//
//		
//		if (loopBackScenario != null && loopBackScenario.toUpperCase().equals("IDP-ENC")){
//			SipServletRequest ssr = factory.createRequest(factory.createApplicationSession(), "NOTIFY", source(), destination());
//			ssr.setRequestURI(destination());
//
//			if(ssr != null) {
//				ssr.addHeader("Event", "tcap-event");
//				ssr.addHeader("Subscription-State", "active");
//
//				ssr.addHeader("Dialogue-id", req.getHeader("Dialogue-id"));
//
//				
//
//				if(operationCode.equalsIgnoreCase("0x00")) {
//					if(logger.isDebugEnabled()){
//						logger.debug("doNotify: Load Testing-Executing hardcoded flow: Received IDP, sending CONN");
//					}
//					byte[] baos = {(byte)0x01, (byte)0x06, (byte)0x0C, (byte)0x02, (byte)(id >>> 24), (byte)(id >>> 16), (byte)(id >>> 8), (byte)id, 
//							(byte)0x1D, (byte)0x1E, (byte)0x02, (byte)0x23, (byte)0x00, (byte)0x22, (byte)0x01, (byte)0x1E, 
//							(byte)0x01, (byte)0x1F, (byte)0x01, (byte)0x2E, (byte)0x20, (byte)0x02, (byte)0x21, (byte)0x10, 
//							(byte)0x30, (byte)0x0E, (byte)0x80, (byte)0x07, (byte)0xAF, (byte)0x05, (byte)0xA0, (byte)0x03, 
//							(byte)0x80, (byte)0x01, (byte)0x01, (byte)0xA1, (byte)0x03, (byte)0x80, (byte)0x01, (byte)0x01,
//							(byte)0x1D, (byte)0x2B, (byte)0x02, (byte)0x23, (byte)0x01, (byte)0x22, (byte)0x01, (byte)0x1E, 
//							(byte)0x01, (byte)0x1F, (byte)0x01, (byte)0x19, (byte)0x20, (byte)0x02, (byte)0x21, (byte)0x1D,
//							(byte)0x30, (byte)0x1B, (byte)0x30, (byte)0x19, (byte)0x80, (byte)0x0F, (byte)0xAF, (byte)0x0D, 
//							(byte)0xA0, (byte)0x0B, (byte)0xA0, (byte)0x09, (byte)0x0A, (byte)0x01, (byte)0x00, (byte)0x0A,
//							(byte)0x01, (byte)0x02, (byte)0x0A, (byte)0x01, (byte)0x03, (byte)0x81, (byte)0x01, (byte)0x01,
//							(byte)0xA2, (byte)0x03, (byte)0x80, (byte)0x01, (byte)0x02, (byte)0x1D, (byte)0x51, (byte)0x02,
//							(byte)0x23, (byte)0x02, (byte)0x22, (byte)0x01, (byte)0x1E, (byte)0x01, (byte)0x1F, (byte)0x01, 
//							(byte)0x17, (byte)0x20, (byte)0x02, (byte)0x21, (byte)0x43, (byte)0x30, (byte)0x41, (byte)0xA0,
//							(byte)0x3F, (byte)0x30, (byte)0x06, (byte)0x80, (byte)0x01, (byte)0x07, (byte)0x81, (byte)0x01, 
//							(byte)0x01, (byte)0x30, (byte)0x06, (byte)0x80, (byte)0x01, (byte)0x05, (byte)0x81, (byte)0x01, 
//							(byte)0x00, (byte)0x30, (byte)0x0B, (byte)0x80, (byte)0x01, (byte)0x06, (byte)0x81, (byte)0x01, 
//							(byte)0x00, (byte)0xBE, (byte)0x03, (byte)0x81, (byte)0x01, (byte)0x0A, (byte)0x30, (byte)0x0B,
//							(byte)0x80, (byte)0x01, (byte)0x09, (byte)0x81, (byte)0x01, (byte)0x01, (byte)0xA2, (byte)0x03, 
//							(byte)0x80, (byte)0x01, (byte)0x01, (byte)0x30, (byte)0x0B, (byte)0x80, (byte)0x01, (byte)0x09,
//							(byte)0x81, (byte)0x01, (byte)0x01, (byte)0xA2, (byte)0x03, (byte)0x80, (byte)0x01, (byte)0x02, 
//							(byte)0x30, (byte)0x06, (byte)0x80, (byte)0x01, (byte)0x0A, (byte)0x81, (byte)0x01, (byte)0x01, 
//							(byte)0x1D, (byte)0x3D, (byte)0x02, (byte)0x23, (byte)0x03, (byte)0x22, (byte)0x01, (byte)0x1E, 
//							(byte)0x01, (byte)0x1F, (byte)0x01, (byte)0x14, (byte)0x20, (byte)0x02, (byte)0x21, (byte)0x2F, 
//							(byte)0x30, (byte)0x2D, (byte)0xA0, (byte)0x09, (byte)0x04, (byte)0x07, (byte)0x02, (byte)0x10, 
//							(byte)0x04, (byte)0x10, (byte)0x00, (byte)0x01, (byte)0x50, (byte)0xAA, (byte)0x18, (byte)0x30,
//							(byte)0x16, (byte)0x02, (byte)0x01, (byte)0xFF, (byte)0x0A, (byte)0x01, (byte)0x00, (byte)0xA1, 
//							(byte)0x0E, (byte)0x30, (byte)0x0C, (byte)0xA1, (byte)0x0A, (byte)0x80, (byte)0x08, (byte)0x02, 
//							(byte)0xFA, (byte)0x05, (byte)0xFE, (byte)0x03, (byte)0x00, (byte)0x02, (byte)0x31, (byte)0xAF, 
//							(byte)0x06, (byte)0x87, (byte)0x01, (byte)0x00, (byte)0x8B, (byte)0x01, (byte)0x00, (byte)13, (byte)10};
//
//					this.addSeqDlgParameter(ssr,1,id);
//					ssr.addHeader("TC-Seq", "1");	
//					ssr.setContent(baos, "application/tcap");
//					ssr.send();
//				} else if(operationCode.equalsIgnoreCase("0x18")) {
//					if(logger.isDebugEnabled()){
//						logger.debug("doNotify: Load Testing-Executing hardcoded flow: Received ERB, sending TC-END");
//					}
//					//dialog type dialog id tag dialog id
//					byte[] baos = {(byte)0x01, (byte)0x06 , (byte)0x0D, (byte)0x02, (byte)(id >>> 24), (byte)(id >>> 16), (byte)(id >>> 8), (byte)id, (byte)13, (byte)10};
//					
//					this.addSeqDlgParameter(ssr,2,id);
//					ssr.addHeader("TC-Seq", "2");	
//					ssr.setContent(baos, "application/tcap");
//					ssr.send();
//					req.getApplicationSession().setAttribute("CLOSED","true");
//				}			
//			}
//			SipServletResponse resp = req.createResponse(200, "OK");
//			if(logger.isDebugEnabled()){
//				logger.debug("Sending response:\n" + resp);
//			}
//			resp.send();
//		}else if (loopBackScenario != null && loopBackScenario.toUpperCase().equals("REL")){
//			SipServletRequest ssr = factory.createRequest(factory.createApplicationSession(), "NOTIFY", source(), destination());
//			ssr.setRequestURI(destination());
//
//			if(ssr != null) {
//				ssr.addHeader("Event", "tcap-event");
//				ssr.addHeader("Subscription-State", "active");
//
//				ssr.addHeader("Dialogue-id", req.getHeader("Dialogue-id"));
//
//				int id = diEvent.getDialogueId();
//
//				if(operationCode.equalsIgnoreCase("0x00")) {
//					if(logger.isDebugEnabled()){
//						logger.debug("doNotify: Load Testing-Executing hardcoded flow: Received IDP, sending REL");
//					}
//					byte[] baos = {(byte)0x01, (byte)0x06, (byte)0x0D, (byte)0x02, (byte)(id >>> 24), (byte)(id >>> 16), 
//							(byte)(id >>> 8), (byte)id, (byte)0x1D, (byte)0x12, (byte)0x02, (byte)0x23, (byte)0x00, 
//							(byte)0x22, (byte)0x01, (byte)0x1E, (byte)0x01, (byte)0x1F, (byte)0x01, (byte)0x16, 
//							(byte)0x20, (byte)0x02, (byte)0x21, (byte)0x04, (byte)0x04, (byte)0x02, (byte)0x83, 
//							(byte)0xA9, (byte)13, (byte)10};
//					this.addSeqDlgParameter(ssr,1,id);
//					ssr.addHeader("TC-Seq", "1");	
//					ssr.setContent(baos, "application/tcap");
//					ssr.send();
//					req.getApplicationSession().setAttribute("CLOSED","true");
//				}
//			}
//			SipServletResponse resp = req.createResponse(200, "OK");
//			if(logger.isDebugEnabled()){
//				logger.debug("Sending response:\n" + resp);
//			}
//			resp.send();
//		}
	
		
		
	
	}

	@Override
	public void processComponentIndEvent(ComponentIndEvent componentIndEvent) {
		
		int primitive = componentIndEvent.getPrimitiveType();
		int dialogueId = -1;
		try {
			dialogueId = componentIndEvent.getDialogueId();
		} catch (MandatoryParameterNotSetException e) {
			if (logger.isDebugEnabled()) {
				logger
					.debug("Got MandatoryParameterNotSetException in processComponentIndEvent", e);
			}
		} catch (ParameterNotSetException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Got ParameterNotSetException in processComponentIndEvent", e);
			}
		}

		if (primitive == TcapConstants.PRIMITIVE_INVOKE) {
			InvokeIndEvent invokeIndEvent = (InvokeIndEvent) componentIndEvent;
			byte[] operCode;
			try {
				operCode = invokeIndEvent.getOperation().getOperationCode();
				byte operCodeByte = operCode[0];
				logger.warn(dialogueId + "::Default Listener invoked for component type::"
								+ primitive + " and messageType:" + operCodeByte);
			} catch (MandatoryParameterNotSetException e) {
				if (logger.isDebugEnabled()) {
					logger.debug("Got ParameterNotSetException in processComponentIndEvent", e);
				}
				logger.warn(dialogueId + "::Default Listener invoked for component type::"
								+ primitive);
			}

		} else {
			logger.warn(dialogueId + "::Default Listener invoked for component type::" + primitive);
		}

		return;

	}

	@Override
	public void processStateIndEvent(StateIndEvent event) {
		return;

	}

	@Override
	public void processTcapError(TcapErrorEvent error) {
		return;

	}

	@Override
	public void addUserAddress(SccpUserAddress userAddress) throws UserAddressLimitException {
		return;

	}

	@Override
	public void removeUserAddress(SccpUserAddress userAddress) throws InvalidAddressException {
		return;

	}

	@SuppressWarnings("deprecation")
	@Override
	public void addUserAddress(TcapUserAddress userAddress) {
		return;

	}

	@SuppressWarnings("deprecation")
	@Override
	public void removeUserAddress(TcapUserAddress userAddress) {
		return;

	}

	@Override
	public SccpUserAddress[] getUserAddressList() throws UserAddressEmptyException {
		return null;
	}

	@Override
	public SccpUserAddress[] processRSNUniDirIndEvent(TcapSession tcapSession,
					DialogueIndEvent event) {
		return null;
	}

	@Override
	public void processTimeOutEvent(TimeOutEvent timeOutEvent) {
		return;

	}

	@Override
	public String getInviteSessionId() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getVersion() {
		return null;
	}

	@Override
	public void processTcapSessionActivationEvent(TcapSession tcapSession) {
		return;
		
	}

	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

}

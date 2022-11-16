package com.baypackets.ase.sbb.b2b;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.sip.Address;
import javax.servlet.sip.Rel100Exception;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.EarlyMediaCallback;
import com.baypackets.ase.sbb.MsSessionController;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.util.Constants;
import com.baypackets.ase.sbb.util.SBBResponseUtil;
import com.baypackets.ase.util.AseStrings;

/**
 * This file is a modified version of EarlyMediaConnectHandler that sends 200 Ok of PRACK request before sending
 * ACK to B party for 200 Ok of INVITE. This handler will send a additional 183 response with CPG content if set by service. 
 * @author Amit Baxi
 *
 */
public class VXMLEarlyMediaConnectHandler extends EarlyMediaConnectHandler implements EarlyMediaCallback {

	private static final long serialVersionUID = 407142438024334290L;
	private static final Logger logger = Logger.getLogger(VXMLEarlyMediaConnectHandler.class);
	
	/**
	 * Public Default Constructor used for Externalizing this Object
	 */
	public VXMLEarlyMediaConnectHandler() {
		super();
	}
	
	public VXMLEarlyMediaConnectHandler(SipServletRequest incomingReq, Address addressB) {
		super(incomingReq, addressB);
	}
	
	public VXMLEarlyMediaConnectHandler(SipServletRequest request, Address partyB, Address from) {
		super(request, partyB, from);
	}
	
	
	
	public void handleRequest(SipServletRequest request) {
		SBB sbb = (SBB) this.getOperationContext();
		
		if(earlyMedia && request.getMethod().equals(AseStrings.PRACK)){

			if(request.getSession().getAttribute(Constants.CPG_PLAY_SEND)!=null){
				if(logger.isDebugEnabled()){
					logger.debug("Prack received for 183 progress before sending info ::cpg");
				}
				try{
					SipServletResponse resp = request.createResponse(200);
					sendResponse(resp, false);
					if(logger.isDebugEnabled()){
						logger.debug("cpg::200Ok sends for PRACK");
					}
				}catch(Rel100Exception e){
					logger.error(e.getMessage(),e);
				}
				catch(IOException e){
					logger.error(e.getMessage(),e);
				}
				return;
			}
			try{
				SipSession partyB = this.getSBB().getB();
				
				//Send a 200 OK upstream.
				if(logger.isDebugEnabled()){
					logger.debug("Sending a 200 response for PRACK");
				}
				
				SipServletResponse respOut = request.createResponse(200);
				this.sendResponse(respOut, false);
				
				// Check for extra 183 with CPG needs to sent or not
				byte[] extraCPG = (byte[])sbb.getAttribute(MsSessionController.EXTRA_183_CPG);
				if(extraCPG!=null){
					this.generateExtraCPGProvisionalResponse(this.requestIn,extraCPG);
				}
				//if initial invite doesn't contain sdp
				//ADDED is ack pending to isolate form update needed and without sdp occuring together.
				
				if (this.isInviteWithoutSDP()
						&& response2xx.getAttribute(Constants.IS_ACK_PENDING) != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("sdp null in invite so send ack after prack");
					}
					SipServletRequest ackToB = this.response2xx.createAck();
					ContentType ct = getSDP(request);
					if (ct != null) {

						ackToB.setContent(ct.getContent(), ct.getContentType());
					}
					if (sbb.getClass().getName()
							.equals(
									"com.baypackets.ase.sbb.mediaserver.GroupedMsSessionControllerImpl")) {
						if (logger.isInfoEnabled()) {
							logger.info("The SBB is GroupedMsSessionController so Disabling the OBGW Proxy for this request");
						}
						ackToB.setAttribute("DISABLE_OUTBOUND_PROXY", "");
					}
					response2xx.removeAttribute(Constants.IS_ACK_PENDING);
					this.sendRequest(ackToB);
				}
				
				this.generateEarlyMediaEvent(request,null);
				
			}catch(Rel100Exception e){
				logger.error(e.getMessage(), e);
				this.failOperation(SBBEvent.REASON_CODE_ERROR, e);
			}catch(IOException e){
				logger.error(e.getMessage(), e);
				this.failOperation(SBBEvent.REASON_CODE_ERROR, e);
			}
		
		}else{
			if(logger.isDebugEnabled()){
				logger.debug("Received request. Going to call super.handleRequest()");
			}
			super.handleRequest(request);
		}
	}

	/**
	 * 
	 * This method will send a additional 183 responce with CPG content after PRACK - 200 OK 
	 * in VXML call flow as per SBTM requirement
	 * @param requestIn
	 * @param cpgContent
	 */
	private void generateExtraCPGProvisionalResponse(SipServletRequest requestIn,byte[] cpgContent) {
		
		SipServletResponse extra183withCPG = requestIn.createResponse(183);
		Multipart mp = new MimeMultipart();
		
		try{
			SBBResponseUtil.formMultiPartMessage(mp, cpgContent, Constants.ISUP_CONTENT_TYPE,null);
			extra183withCPG.setContent(mp, mp.getContentType());
		}catch(MessagingException me){
			logger.error(me.getMessage(),me);
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
}

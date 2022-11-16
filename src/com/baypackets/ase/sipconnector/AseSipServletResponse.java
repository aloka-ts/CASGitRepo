/*
 * AseSipServletResponse.java
 *
 * @author Vishal Sharma
 */

package com.baypackets.ase.sipconnector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.sip.Proxy;
import javax.servlet.sip.ProxyBranch;
import javax.servlet.sip.Rel100Exception;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
import org.apache.log4j.Logger;

import com.baypackets.ase.container.AseApplicationSession;
import com.baypackets.ase.container.AseBaseRequest;
import com.baypackets.ase.container.AseBaseResponse;
import com.baypackets.ase.container.AseEngine;
import com.baypackets.ase.container.AseProtocolSession;
import com.baypackets.ase.container.AseThreadData;
import com.baypackets.ase.latency.AseLatencyData;
import com.baypackets.ase.latency.AseLatencyData.ComponentTimes;
import com.baypackets.ase.ocm.TimeMeasurement;
import com.baypackets.ase.replication.ReplicatedMessageHolder;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.util.AseUtils;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipClientTransaction;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipServerTransaction;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipAuthenticateHeaderBase;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipContentTypeHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderInterface;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderList;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipMessage;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRequest;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRequireHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipResponse;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipFrameStream;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserException;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserListenerException;
import com.dynamicsoft.DsLibs.DsUtil.DsSSLBindingInfo;

/**
 * This class represents SIP responses.
 */

@DefaultSerializer(ExternalizableSerializer.class)
public class AseSipServletResponse extends AseSipServletMessage
implements SipServletResponse, AseBaseResponse, TimeMeasurement, Cloneable
{
	private static final String ATTR_RESPONSE_TLS_CERTIFICATE = "javax.servlet.response.X509Certificate";
	
	private static final long serialVersionUID = -384885847114974854L;
	/**
	 * Adds the response to the message map maintained by the AseApplication Session
	 */
	public void addToApplicationSession (){
			((AseApplicationSession)this.getApplicationSession()).addSipServletMessage(this.assignMessageId(),this);
	}
	/**
	 * Sets the content of the message as specified.
	 *
	 * @param content message content as Object instance.
	 * @param contentType type of message content.
	 *
	 * @throws UnsupportedEncodingException if the specified encoding is not
	 *	supported.
	 * @throws IllegalArgumentException if the specified MIME type cannot be
	 *	serialized.
	 * @throws IllegalStateException if the message state prevents setting of
	 *	content.
	 */
	public void setContent (Object content, String contentType) throws UnsupportedEncodingException, 
	IllegalArgumentException, IllegalStateException {
		if (("MESSAGE".equals(this.getMethod())) 
				&& (this.getStatus() < 300) && (this.getStatus() > 199)) {
			throw new IllegalStateException("A 2xx response to a MESSAGE request MUST NOT contain a body.");
		}
		super.setContent(content, contentType);
	}

	public AseBaseRequest getBaseRequest()
	{
		if (m_l.isDebugEnabled()) m_l.debug("getBaseRequest() called.");

		return m_request;
	}

	/**
	 * Returns the request associated with the response. For responses received 
	 * for proxied requests, this method returns the corresponding request 
	 * object that was sent downstream.
	 *
	 * @return the request associated with the response.
	 */
	public SipServletRequest getRequest()
	{
		if (m_l.isDebugEnabled()) m_l.debug("getRequest() called.");

		return m_request;
	}

	/**
	 * Returns the request associated with the response. For responses received
	 * for proxied requests, this method returns the corresponding request
	 * object that was sent downstream.
	 *
	 * @return the request associated with the response.
	 */
	public boolean canCreateMultipleDialogs()
	{
		if (m_l.isDebugEnabled()) m_l.debug("canCreateMultipleDialogs() called.");

		// Even failure responses for INVITE will create new dialog
		// (AseSipSession infact, not dialog)
		//bug# BPInd09232
		if(DsSipConstants.INVITE == this.getDsResponse().getMethodID() ) {
			if(100 == this.getStatus()) {
				return false;
			} else if( (1 == m_response.getResponseClass())
					&& (!this.getDialogId().hasToTag()) ) {
				return false;
			}
			return true;
		} else if(2 == m_response.getResponseClass()) {
			// We can create multiple SIP session for REFER and SUBSCRIBE 2xx
			// responses
			if( (m_response.getMethodID() == DsSipConstants.REFER) ||
					(m_response.getMethodID() == DsSipConstants.SUBSCRIBE) ) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Determine if a dialog can be created by this response
	 */
	public boolean canCreateDialog() {
		if (m_l.isDebugEnabled()) m_l.debug("canCreateDialog() called.");

		// For INVITE this is a 1XX with TO tag
		if (DsSipConstants.INVITE == getDsResponse().getMethodID()) {
			if (1 == getDsResponse().getResponseClass() &&
					100 != getDsResponse().getStatusCode()) {
				if (m_l.isDebugEnabled()) m_l.debug("1XX INVITE response canCreateDialog returns TRUE");
				return true;
			}
		}

		// For SUBSCRIBE, REFER, INVITE this is a 2XX response
		if (2 == getDsResponse().getResponseClass() &&
				(DsSipConstants.INVITE == getDsResponse().getMethodID() ||
						DsSipConstants.SUBSCRIBE == getDsResponse().getMethodID() ||
						DsSipConstants.REFER == getDsResponse().getMethodID())) {
		if (m_l.isDebugEnabled())	m_l.debug("2XX INVITE/SUBSCRIBE/REFER response. " +
			"canCreateDialog returns TRUE");
			return true;
		}

		if (m_l.isDebugEnabled()) m_l.debug("canCreateDialog returns FALSE");
		return false;
	}

	/**
	 * Determine if a dialog can be terminted by this response
	 */
	public boolean canTerminateDialog() {
		if (m_l.isDebugEnabled()) m_l.debug("canTerminateDialog() called.");

		if (408 == getDsResponse().getStatusCode() ||
				481 == getDsResponse().getStatusCode()) {
		if (m_l.isDebugEnabled())	m_l.debug("481/408 response. canTerminateDialog returns TRUE");
			return true;
		}

		if (m_l.isDebugEnabled()) m_l.debug("Non 481/408 response. canTerminateDialog returns FALSE");
		return false;
	}


	/**
	 * Returns the status code of the response.
	 *
	 * @return the status code of the response.
	 */
	public int getStatus()
	{
		//m_l.debug("getStatus() called.");

		return m_response.getStatusCode();
	}

	/**
	 * Sets the status code of the response object.
	 *
	 * @param statusCode status code.
	 */
	public void setStatus (int statusCode)
	{
		if (m_l.isDebugEnabled()) m_l.debug("setStatus (int) called.");

		// NOTE: This method should throw an exception (IllegalStateException?) 
		// in case status code for a received response is sought to be changed.
		// DsSipResponse.setStatusCode(statusCode) would be invoked.
		// right now would just returning ..

		if (!isMutable()) 
		{
			m_l.warn("Message is immutable.");

			return;
		}

		m_response.setStatusCode(statusCode);
	}

	/**
	 * Sets the status code and reason phrase of the response object.
	 *
	 * @param statusCode status code.
	 * @param reasonPhrase reason phrase.
	 */
	public void setStatus (int statusCode, String reasonPhrase)
	{
	if (m_l.isDebugEnabled())	m_l.debug("setStatus (int, String) called.");

		// NOTE: This method should throw an exception (IllegalStateException?) 
		// if invoked for a received response.
		// DsSipResponse.setStatusCode(statusCode) would be invoked, followed 
		// by DsSipResponse.setReasonPhrase().
		// right now just returning ..

		if (!isMutable()) 
		{
			m_l.warn("Message is immutable.");

			return;
		}

		m_response.setStatusCode(statusCode);
		//bug# BPInd09232
		if(null != reasonPhrase){
			m_response.setReasonPhrase(new DsByteString(reasonPhrase));    
		}else{
			m_l.error("setStatus():reasonPhrase is null");
		}

	}

	/**
	 * Returns the reason phrase for the response.
	 *
	 * @return the reason phrase for the response.
	 */
	public String getReasonPhrase()
	{
		if (m_l.isDebugEnabled()) m_l.debug("getReasonPhrase() called.");

		// DsSipResponse.getReasonPhrase() would be used.

		// do we need to return a copy of the String?
		String reason = m_response.getReasonPhrase().toString();
		return reason;
	}

	/**
	 * Returns the ServletOutputStream; <code>null</code> in case of SIP 
	 * resposnes.
	 *
	 * @return the ServletOutputStream; <code>null</code> in case of SIP 
	 *	responses.
	 */
	public ServletOutputStream getOutputStream()
	{
		if (m_l.isDebugEnabled()) m_l.debug("getOutputStream() called.");

		// Always returns NULL.
		return null;
	}

	/**
	 * Returns PrintWriter; <code>null</code> in case of SIP responses.
	 *
	 * @return PrintWriter; <code>null</code> in case of SIP responses.
	 */
	public PrintWriter getWriter()
	{
		if (m_l.isDebugEnabled()) m_l.debug("getWriter() called.");

		// Always returns NULL.
		return null;
	}

	/**
	 * Returns the proxy object associated.
	 *
	 * @return the proxy object associated with it.
	 */
	public Proxy getProxy()
	{
		if (m_l.isDebugEnabled()) m_l.debug("getProxy() called.");

		return m_proxy;
	}

	/**
	 * Requests that the response be sent reliably using the 100rel extension 
	 * defined in RFC 3262.
	 *
	 * @throws Rel100Exception if the response is not a provisional response
	 *	other than 100, or 100rel extension is not supported by UAC or ASE.
	 */
	public void sendReliably()
	throws Rel100Exception
	{
		if(m_l.isDebugEnabled()) {
			m_l.debug("sendReliably() called.");
		}

		AseLatencyData.ThreadLocalLatencyContainer.handleBeginOut(ComponentTimes.RETURN, this);

		boolean changeLock = AseThreadData.setIcLock(m_sipSession);

		try {
			// Rel100Exception is thrown in case 
			// -	the response is not a provisional response other than 100
			// -	UAC did not indicate support for 100rel extension
			// -	ASE does not support 100rel extension (javax.servlet.sip.100rel 
			//		should exist in the ServletContext with a value of 
			//		Boolean.TRUE.)

			// java.lang.IllegalStateException should be thrown in case the response
			//  is not valid in the current state of underlying SIP transaction or 
			//	the response was received from downstream or it has already been 
			//	sent.
			// java.io.IOException is thrown in case transport error is encountered.
			// TBD.
			// Throw  Rel100Exception.

			// If this response was already sent, throw exception.
			// Throwing Rel100Exception as IllegalStateException is not specified in
			// API Java doc
			if (!isMutable()) {
				m_l.error("Message is immutable.");
				throw new Rel100Exception(0);
			}

			// If it is an incoming response, throw exception.
			if(m_source != AseSipConstants.SRC_SERVLET) {
				m_l.error("Incoming response, cannot be sent by servlet");
				throw new Rel100Exception(0);
			}

			// Make sure this is a non-100 1XX response
			if((1 != getDsResponse().getResponseClass()) ||
					(100 == getDsResponse().getStatusCode())) {
				m_l.error("Only non-100 provisonal responses may be sent reliably.");
				throw new Rel100Exception(Rel100Exception.NOT_1XX);
			}

			// Make sure that the original request was an INVITE
			DsSipRequest dsRequest = ((AseSipServletRequest)getRequest()).getDsRequest();
			if(DsSipConstants.INVITE != dsRequest.getMethodID()) {
				m_l.error("Can send relibale response only for INVITE requests");
				throw new Rel100Exception(Rel100Exception.NOT_INVITE);
			}

			// Make sure the original INVITE either Required or Supported
			// the 100rel extension
			AseSipServletRequest sipReq = (AseSipServletRequest)getRequest();
			if(AseSipServletRequest.REL_NOT_SUPPORTED == sipReq.getRelStatus()) {
				m_l.error("Orginal INVITE request does not support 100rel");
				throw new Rel100Exception(Rel100Exception.NOT_SUPPORTED);
			}

			//Throw IllegalStateException if the associated Session Object is NULL. 
			super.checkSessionState();

			// Add a Require Header with value 100rel
			DsSipRequireHeader reqHeader = new DsSipRequireHeader(new DsByteString("100rel"));
			getDsResponse().addHeader((DsSipHeaderInterface)reqHeader);

			// Set the m_isReliable flag in this response
			m_isReliable = true;

			//
			// Diagnostic Logging
			//
			AseSipDiagnosticsLogger diag = AseSipDiagnosticsLogger.getInstance();
			if (diag.isAppMsgLoggingEnabled()) {
				if (diag.dumpResponse(m_response.getMethodID(), this.getStatus())) {
					diag.log("RES2SAS:" + this.getMethod() + "/" + this.getStatus() + ":" + this.getCallId() + ":" + m_sipSession.getId());
				}
			}

			// Send the response to the session for handling
			try {
				m_sipSession.sendResponse(this);
				setImmutable();
			} catch(AseSipSessionException exp) {
				//Don't log any error message here
				throw new Rel100Exception(0);
			}catch (Exception e) {
				m_l.error("Exception from Sip Session", e);
				throw new Rel100Exception(0);
			}
		} finally {
			AseThreadData.resetIcLock(m_sipSession, changeLock);
		}

		AseLatencyData.ThreadLocalLatencyContainer.handleEndOut(ComponentTimes.RETURN, this);

		if(m_l.isDebugEnabled()) {
			m_l.debug("Leaving sendReliably()");
		}
	}

	/**
	 * Sends out the response. This would be used by UASs for sending 
	 * provisional and final responses, and by proxies for generating 
	 * provisional responses..
	 *
	 * @throws IOException if an IO error occurs.
	 * @throws IllegalStateException if the current state of the underlying 
	 *	SIP transaction does not permit sending of this response.
	 */
	public void send()
	throws IOException, IllegalStateException
	{
		if(m_l.isDebugEnabled()) {
			m_l.debug("send():enter");
		}

		AseLatencyData.ThreadLocalLatencyContainer.handleBeginOut(ComponentTimes.RETURN, this);

		boolean changeLock = AseThreadData.setIcLock(m_sipSession);

		try {
			// What if a proxy invokes this method for a final response?? 
			// IllegalStateException ??
			// Transport errors would result in java.io.IOException being thrown.
			// Sends the SipServletMessage.
			// java.io.IOException is returned if a transport error occurs. 
			// java.lang.IllegalStateException is returned if the message can not 
			// be legally sent in the current state of the underlying SIP 
			// transaction.
			// It marks/updates the message as pending for transmission till it is
			// actually sent on the network or chained it to another session so as
			// to handle the race conditions and validations e.g. servlet invoking
			// send() multiple times. (isMutable would be set to FALSE to restrict 
			// modifications to the sent message - this would be used in 
			// conjunction with isProxy flag of the SipSession to determine 
			// whether any changes can be made to the message at all after send() 
			// is invoked). SipSession.handleResponse() or 
			// SipSession.handleResponse() would be invoked depending upon whether 
			// the message-in-question is a request or a response. In SipSession, 
			// all validations pertaining to dialog state and SIP session state 
			// are performed. SIP session state-validations check whether this 
			// message can be send in the current state of SIP session. This is 
			// detailed in another section. isCommitted flag is also updated (this 
			// is done is SipSession).

			// If this response was already sent, throw exception.
			if(!isMutable()) {
				m_l.error("Message is immutable.");
				throw new IllegalStateException("Response already sent.");
			}

			// If it is an incoming response, throw exception.
			if(m_source != AseSipConstants.SRC_SERVLET) {
				m_l.error("Incoming response, cannot be sent by servlet");
				throw new IllegalStateException("Incoming response, cannot be sent by servlet.");
			}

			// If the original request requires 100rel and then reject this
			// response if this is a non-100 1xx response
			if(1 == getDsResponse().getResponseClass() &&
					100 != getDsResponse().getStatusCode()) {
				if(AseSipServletRequest.REL_REQUIRED == m_request.getRelStatus()) {
					m_l.error("Original request requires 100rel");
					throw new IllegalStateException("Original request requires 100rel");
				}
			}

			//Throw IllegalStateException if the associated Session Object is NULL. 
			super.checkSessionState();

			// Set request as responded
			// For an initial request, any response sent will set this flag.
			// For a subsequent request, only non-100 response will set this flag.
			if(m_request.isInitial() || (100 != getDsResponse().getStatusCode()) ) {
				if (m_l.isDebugEnabled()) m_l.debug("Setting responded flag in request");
				((AseSipServletRequest)m_request).setResponded();
			}

			if(m_sipSession.getRole() == AseSipSession.ROLE_PROXY) {
				// check if we need to clone the session, if not a 100 response
				// to initial request
				if( (100 != getDsResponse().getStatusCode())
						&& m_request.isInitial() ) {
					// clone this session and set association with response
					AseConnectorSipFactory factory =
						((AseConnectorSipFactory) m_connector.getFactory());
					m_sipSession = factory.createSession(m_sipSession);
				}

				// Send all 1xx responses directly.
				// For all other responses, send to Proxy first and depending on
				// return value, send it to session.
				if(1 != getDsResponse().getResponseClass()) {
					// Set session role to UAS
					m_sipSession.setRole(AseSipSession.ROLE_UAS);

					if(m_l.isDebugEnabled()) {
						m_l.debug("Sending final reponse to Proxy");
					}
				}
			}

			boolean pMsg = false;
			if(AseUtils.getCallPrioritySupport() == 1)      {
				AseApplicationSession appSession = (AseApplicationSession)getApplicationSession();
				DsSipMessage dsSipMessage = getDsMessage();
				pMsg = AseNsepMessageHandler.getMessagePriority(this);
				if(pMsg)        {
					if(m_l.isDebugEnabled())
						m_l.debug("Resource-Priority header present in outgoing Request"
								+" treat it as priority message");
					//set message priority to true
					dsSipMessage.setMessagePriority(true);
					appSession.setPriorityStatus(true);

				}else   {
					if(m_l.isDebugEnabled())
						m_l.debug("No Resource-Priority header in outgoing Request"
								+" treat it as normal message");
					//set message priority to false
					dsSipMessage.setMessagePriority(false);
					appSession.setPriorityStatus(false);


				}
			}

			//
			// Diagnostic Logging
			//
			AseSipDiagnosticsLogger diag = AseSipDiagnosticsLogger.getInstance();
			if (diag.isAppMsgLoggingEnabled()) {
				if (diag.dumpResponse(m_response.getMethodID(), this.getStatus())) {
					diag.log("RES2SAS:" + this.getMethod() + "/" + this.getStatus() + ":" + this.getCallId() + ":" + m_sipSession.getId());
				}
			}


			try {
				m_sipSession.sendResponse(this);
				setImmutable();
			} catch(AseSipSessionException exp) {
				//Don't log any error message here
				try{
					throw new Rel100Exception(0);
				}catch(Rel100Exception ex) {
					m_l.error("Exception  Rel100 from Sip Session" ) ;
				}
			}catch (Exception e) {
				m_l.error("Exception from Sip Session", e);
				throw new IllegalStateException(e.toString());
			}
		} finally {
			AseThreadData.resetIcLock(m_sipSession, changeLock);
		}

		AseLatencyData.ThreadLocalLatencyContainer.handleEndOut(ComponentTimes.RETURN, this);

		if(m_l.isDebugEnabled()) { 
			m_l.debug("send():exit");
		}
	}

	/**
	 * Returns an ACK request corresponding to this response. This is done only 
	 * for 2xx responses for the purpose of application-controlled sending of 
	 * ACK; for non-2xx responses, ACKs are automatically sent by ASE.
	 *
	 * @return ACK corresponding to this response.
	 */
	public SipServletRequest createAck()
	{
		if (m_l.isDebugEnabled()) m_l.debug("createAck() called.");

		// java.lang.IllegalStateException would be thrown if the state does 
		// not allow ACK to be sent, viz.
		// -	the original request was not an initial INVITE
		// -	the response is provisional
		// -	ACK has already been generated (for non-2xx: by ASE, for 2xx: 
		//		by application).
		// Constructor DsSipAckMessage(DsSipResponse, NULL, NULL) would be used.

		// Can Create ACK's for all INVITE transactions
		//bug# BPInd09232
		if(DsSipConstants.INVITE != m_request.getDsRequest().getMethodID() )
		{
			m_l.error("ACK can be generated for an initial INVITE txn only.");

			throw new IllegalStateException 
			("Not corresponding to a valid SIP request transaction.");
		}
		else if (1 == m_response.getResponseClass()) 
		{
			m_l.error("ACK can not be generated for provisional responses.");

			throw new IllegalStateException 
			("No ACK for provisional responses.");
		}
		// 		else if (2 != m_response.getResponseClass()) 
		// 		{
		// 		    m_l.error("ACK would be generated by ASE.");

		// 			throw new IllegalStateException 
		// 				("ACK for non-2xx cases is generated by ASE.");
		// 		}
		else if (m_ackAlreadyGenerated) 
		{
			m_l.error("ACK already generated.");

			throw new IllegalStateException ("ACK already generated.");
		}

		//Throw IllegalStateException if the associated Session Object is NULL. 
		super.checkSessionState();

		AseSipServletRequest ack = 
			( (AseConnectorSipFactory) m_connector.getFactory()).createAck( 
					this);



		if(m_response.getResponseClass() > 2) {
			ack.setNon2XXAck();

			//BpInd 18330
			if (m_l.isDebugEnabled()) m_l.debug("Setting the special headers if any");
			if(getRequest().getHeader("Accept-Contact")!=null)
			{
				ack.addHeader("Accept-Contact",getRequest().getHeader("Accept-Contact"));
			}

			if(getRequest().getHeader("Reject-Contact")!=null)
			{
				ack.addHeader("Reject-Contact",getRequest().getHeader("Reject-Contact"));
			}
			if(getRequest().getHeader("Request-Disposition")!=null)
			{
				ack.addHeader("Request-Disposition",getRequest().getHeader("Request-Disposition"));
			}


		}

		// should we mark the ACK generated here or should it be marked
		// only when ACk is actually sent? In that case, the variable would
		// be m_ackAlreadySent. Right now, using m_ackAlreadyGenerated only ..
		m_ackAlreadyGenerated = true;

		return ack;
	}

	// -- implementation for interface ServletResponse methods

	public void flushBuffer()
	{
		//Implementation may do nothing according to the JSR 289.
	}

	public int getBufferSize()
	{
		return 0;  // recommended as per JSR 289.
	}

	public Locale getLocale() 
	{
		if (m_l.isDebugEnabled()) m_l.debug("getLocale() called.");
		Locale loc = null;
		String lang = super.getCharacterEncoding();
		if(null != lang){
			loc = new Locale(lang);
		}

		return loc;

	}

	public void reset()
	{
		//Implementation may do nothing according to the JSR 289.
	}

	public void resetBuffer()
	{
		// done .. anything else, sir!?
	}

	public void setBufferSize(int size)
	{
		//Implementation may do nothing according to the JSR 289.
	}

	public void setLocale(Locale loc)
	{
		if (m_l.isDebugEnabled()) m_l.debug("setLocale(Locale) called.");
		try{
			super.setCharacterEncoding(loc.getLanguage());
		}catch(UnsupportedEncodingException e){
			m_l.error("setLocale(Locale) Language not supported",e);
		}
	}

	// -- interface SipServletMessage: overidden method --

	/**
	 * Returns the SipSession to which the message belongs. The session is
	 * created if it did not exist already.
	 *
	 * @return the SIP session to which message belongs.
	 */
	public SipSession getSession()
	{
		if (m_l.isDebugEnabled()) m_l.debug("getSession() called.");

		// sipSession would be returned.

		if (null == m_sipSession)
		{
			m_l.error("Response object without an associated session");
		}

		return m_sipSession;
	}

	// -- interface AseBaseResponse methods --

	// -- local methods --

	int getResponseClass() {
		return m_response.getResponseClass();
	}

	public AseSipServletResponse() {
		if(m_l.isDebugEnabled() ) 
			m_l.debug("Inside deafult constructor");
	}


	/**
	 * Constructor corresponding to 
	 * SipServletRequest.createResponse(int statusCode).
	 *
	 * @param request SIP request.
	 * @param statusCode status code.
	 */
	AseSipServletResponse (	AseSipServletRequest	request, 
			int						statusCode)
			{
		if (m_l.isDebugEnabled()) m_l.debug("AseSipServletResponse(AseSipServletRequest, int) called.");

		m_source = AseSipConstants.SRC_SERVLET;
		m_request = request;
		m_clientTxn = request.getClientTxn();
		m_serverTxn = request.getServerTxn();
		m_pClientTxn = request.getPseudoClientTxn();
		m_pServerTxn = request.getPseudoServerTxn();
		m_sipSession = request.getAseSipSession();
		m_ackAlreadyGenerated = false;

		m_connector = request.getSipConnector();

		// Bug Id # BPUsa07614
		boolean copyRRHeader = false;
		if( (request.getDsRequest().getToTag() == null) && 
				(statusCode/100 == 1) && (statusCode%100 != 0) ) {
			copyRRHeader = true;
		}
		m_response = new DsSipResponse
		(statusCode, request.getDsRequest(), null, null, true, copyRRHeader);
		m_message = m_response;

		m_proxy = request.m_proxy;
			}

	/**
	 * Constructor corresponsing to 
	 * SipServletRequest.createResponse(int statusCode, String reasonPhrase).
	 *
	 * @param request SIP request.
	 * @param statusCode status code.
	 * @param reasonPhrase reason phrase.
	 */
	AseSipServletResponse (	AseSipServletRequest	request, 
			int						statusCode, 
			String					reasonPhrase)
			{
		if (m_l.isDebugEnabled()) m_l.debug("AseSipServletResponse(AseSipServletRequest, int, String) called.");

		m_source = AseSipConstants.SRC_SERVLET;
		m_request = request;
		m_clientTxn = request.getClientTxn(); // would be null
		m_serverTxn = request.getServerTxn();
		m_pClientTxn = request.getPseudoClientTxn();
		m_pServerTxn = request.getPseudoServerTxn();
		m_sipSession = request.getAseSipSession();
		m_ackAlreadyGenerated = false;

		m_connector = request.getSipConnector();

		// Bug Id # BPUsa07614
		boolean copyRRHeader = false;
		if( (request.getDsRequest().getToTag() == null) && 
				(statusCode/100 == 1) && (statusCode%100 != 0) ) {
			copyRRHeader = true;
		}
		m_response = new DsSipResponse
		(statusCode, request.getDsRequest(), null, null, true, copyRRHeader);

		//bug# BPInd09232
		if(null != reasonPhrase){
			m_response.setReasonPhrase(new DsByteString(reasonPhrase));                                    
		}else{
			m_l.error("AseSipServletResponse():reasonPhrase is null");
		}

		m_message = m_response;
		m_proxy = request.m_proxy;
			}

	/**
	 * Constructor to be invoked for responses received over the wire.
	 *
	 * @param connector SIP connector.
	 * @param response DS SIP response.
	 * @param txn DS SIP client transaction.
	 */
	AseSipServletResponse (	AseSipConnector				connector,
			DsSipResponse				response, 
			DsSipClientTransaction		txn)
			{
	if (m_l.isDebugEnabled())	m_l.debug("AseSipServletResponse(AseSipConnector, DsSipResponse, DsSipClientTransaction) called.");

		m_source = AseSipConstants.SRC_NETWORK;
		m_request = ((AseSipTransaction)txn).getAseSipRequest();
		m_clientTxn = txn;
		m_serverTxn = null;
		m_ackAlreadyGenerated = false;

		m_connector = connector;

		m_response = response;
		m_message = m_response;
		try {
			DsSipContentTypeHeader contentType = response.getContentTypeHeaderValidate();
			if (contentType != null) {
				DsByteString encoding = contentType.getParameter("charset");
				if (encoding != null) {
					m_enc = encoding.toString();
				}
			}
		}
		catch(DsSipParserException exp) {
			if (m_l.isInfoEnabled()) m_l.info("Exception caught " + exp);
		}
		catch(DsSipParserListenerException exp) {
			if (m_l.isInfoEnabled()) m_l.info("Exception caught " + exp);
		}


		// Check if this is a relibale response
		checkIfReliable();
		if(m_request != null) {
			m_proxy = m_request.m_proxy;
		}

		if(m_message.getBindingInfo() instanceof DsSSLBindingInfo) {
			m_isSecure = true;

			DsSSLBindingInfo sslBInfo = (DsSSLBindingInfo)m_message.getBindingInfo();
			if(sslBInfo != null) {
				try {
					this.setAttribute(ATTR_RESPONSE_TLS_CERTIFICATE, sslBInfo.getPeerCertificateChain());
				} catch(javax.net.ssl.SSLPeerUnverifiedException exp) {
					m_l.error("AseSipServletResponse(): getting TLS certificate", exp);
				}
			}
		}
			}

	/**
	 * Constructor to be invoked for responses received over the wire on the 
	 * stray interface.
	 *
	 * @param connector SIP connector.
	 * @param response DS SIP response.
	 */
	AseSipServletResponse (	AseSipConnector			connector, 
			DsSipResponse			response)
			{
	if (m_l.isDebugEnabled())	m_l.debug("AseSipServletResponse (AseSipConnector, DsSipResponse) called.");

		m_source = AseSipConstants.SRC_NETWORK;
		m_request = null;
		m_clientTxn = null;
		m_serverTxn = null;
		m_ackAlreadyGenerated = false;

		m_connector = connector;

		m_response = response;
		m_message = m_response;
		try {
			DsSipContentTypeHeader contentType = response.getContentTypeHeaderValidate();
			if (contentType != null) {
				DsByteString encoding = contentType.getParameter("charset");
				if (encoding != null) {
					m_enc = encoding.toString();
				}
			}

		}
		catch(DsSipParserException exp) {
			if (m_l.isInfoEnabled()) m_l.info("Exception caught " + exp);
		}
		catch(DsSipParserListenerException exp) {
			if (m_l.isInfoEnabled()) m_l.info("Exception caught " + exp);
		}

		// Check if this is a relibale response
		checkIfReliable();

		if(m_message.getBindingInfo() instanceof DsSSLBindingInfo) {
			m_isSecure = true;

			DsSSLBindingInfo sslBInfo = (DsSSLBindingInfo)m_message.getBindingInfo();
			if(sslBInfo != null) {
				try {
					this.setAttribute(ATTR_RESPONSE_TLS_CERTIFICATE, sslBInfo.getPeerCertificateChain());
				} catch(javax.net.ssl.SSLPeerUnverifiedException exp) {
					m_l.error("AseSipServletResponse(): getting TLS certificate", exp);
				}
			}
		}
			}

	/**
	 * Constructor to be invoked for handling client transaction timeouts. 
	 * In this constructor a DsSipResponse object with the specified 
	 * statusCode (408) would be created from the relevant request obtained 
	 * from the specified client transaction.
	 *
	 * @param connector SIP connector.
	 * @param statusCode status code.
	 * @param txn DS SIP client transaction.
	 */
	AseSipServletResponse (	AseSipConnector				connector,
			int							statusCode, 
			DsSipClientTransaction		txn)
			{
		if (m_l.isDebugEnabled()) m_l.debug("AseSipServletResponse (AseSipConnector, int, DsSipClientTransaction) called.");

		m_source = AseSipConstants.SRC_ASE;
		m_request = ((AseSipTransaction)txn).getAseSipRequest();
		m_clientTxn = txn;
		m_serverTxn = null;
		m_ackAlreadyGenerated = false;

		m_connector = connector;

		// generate a DsSipResponse object with given status code
		m_response = new DsSipResponse
		(statusCode, m_request.getDsRequest(), null, null, true);
		m_message = m_response;
		m_proxy = m_request.m_proxy;
			}

	/**
	 * Sets a reference to the original ASE SIP request.
	 * 
	 * @param request SIP request.
	 */
	void setRequest (AseSipServletRequest request)
	{
		if (m_l.isDebugEnabled()) m_l.debug("setRequest (AseSipServletRequest) called.");

		m_request = request;
	}

	/**
	 * Returns the DsSipResponse associated with the ASE SIP response.
	 *
	 * @return the associated DS SIP response.  
	 */
	DsSipResponse getDsResponse()
	{
	if (m_l.isDebugEnabled())	m_l.debug("getDsResponse() called.");

		return m_response;
	}

	/**
	 * Returns the client transaction associated with the ASE SIP response.
	 *
	 * @return the associated client transaction. 
	 */
	DsSipClientTransaction getClientTxn()
	{
	if (m_l.isDebugEnabled())	m_l.debug("getClientTxn() called.");

		return m_clientTxn;
	}

	/**
	 * Returns the server transaction associated with the ASE SIP response.
	 *
	 * @return the associated server transaction.
	 */
	DsSipServerTransaction getServerTxn()
	{
		if (m_l.isDebugEnabled()) m_l.debug("getServerTxn() called.");

		return m_serverTxn;
	}

	void clearStackTxn()
	{
		if (m_l.isDebugEnabled()) m_l.debug("clearStackTxn() called:");

		m_request = null;

		m_serverTxn = null;
		m_clientTxn = null;
		m_pClientTxn = null;
		m_pServerTxn = null;

		m_ackAlreadyGenerated = false;
		timestamp = -1;
	}

	void setPseudoClientTxn(AsePseudoSipClientTxn txn)
	{
		if (m_l.isDebugEnabled()) m_l.debug("setPseudoClientTxn(AsePseudoSipClientTxn) called.");
		m_pClientTxn = txn;
	}

	AsePseudoSipClientTxn getPseudoClientTxn()
	{
	if (m_l.isDebugEnabled())	m_l.debug("getPseudoClientTxn() called.");
		return m_pClientTxn;
	}

	void setPseudoServerTxn(AsePseudoSipServerTxn txn)
	{
		if (m_l.isDebugEnabled()) m_l.debug("setPseudoServerTxn(AsePseudoSipServerTxn) called.");
		m_pServerTxn = txn;
	}

	AsePseudoSipServerTxn getPseudoServerTxn()
	{
		if (m_l.isDebugEnabled()) m_l.debug("getPseudoServerTxn() called.");
		return m_pServerTxn;
	}

	/**
	 * Returns whether the CONTACT header is mutable; implementation of abstract
	 * method of base class.
	 *
	 * @return <code>true</code> if the CONTACT header is mutable;
	 *	<code>false</code> otherwise.
	 */
	boolean canMutateContactHeader()
	{
		if (m_l.isDebugEnabled()) m_l.debug("canMutateContactHeader() called.");

		// can mutate Contact header only in 3xx and 485 mutable responses and OPTIONS/200 responses

		if (isMutable()) 
		{
			if ((3 == m_response.getResponseClass()) || 
					(485 == m_response.getStatusCode()) || 
					(m_response.getMethodID() == DsSipConstants.REGISTER) ||
					((m_response.getMethodID() == DsSipConstants.OPTIONS)&& (m_response.getStatusCode()== 200)))
			{
			if (m_l.isInfoEnabled()) 	m_l.info("Can mutate CONTACT header.");

				return true;
			}

		}

		if (m_l.isInfoEnabled()) m_l.info("Can not mutate CONTACT header.");

		return false;
	}

	/**
	 * Checks if this response is a relibale response
	 * Response has to be a non-100 1XX response
	 * It has to have a RSEQ header
	 */

	void checkIfReliable() {
		if (m_l.isDebugEnabled()) m_l.debug( "Entering checkIfReliable");

		// Check for presence of RSeq header and REQUIRE header
		// only when it is a non-100
		// provisional response.

		DsSipResponse response = m_response;
		if ((1 == response.getResponseClass()) &&
				(100 != response.getStatusCode())) {

			// first check presence of REQUIRE header
			DsSipHeaderInterface reqHdr =
				response.getHeader(DsSipConstants.REQUIRE);

			if (null == reqHdr) {
				m_l.debug("REQUIRE header not present.");
				m_l.debug("Leaving checkIfReliable");
				return;
			}

			// Check for RSEQ header
			DsSipHeaderInterface rseqHdr =
				response.getHeader(DsSipConstants.RSEQ);

			if (null == rseqHdr) {
				m_l.debug("RSEQ header not present.");
				m_l.debug("Leaving checkIfReliable");
				return;
			}

			// Since both headers are present
			m_isReliable = true;
		}
		if (m_l.isDebugEnabled()) m_l.debug("Leaving checkIfReliable");
	}

	public void setCharacterEncoding(String enc)
	{
		try {
			super.setCharacterEncoding(enc);
		} catch (UnsupportedEncodingException e) {
			m_l.error(e.getMessage(),e);
		}
	}

	/**
	 * Return the value of the m_isReliable flag
	 *
	 * @return indicates if this is a reliable provisional response
	 */
	boolean isReliable() {
		return m_isReliable;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public boolean hasTimestamp() {
		return this.timestamp == -1? false : true;
	}

	boolean isFinalResponse() {
		if (1 == getDsResponse().getResponseClass())
			return false;
		return true;
	}

	boolean isInviteResponse() {
		if (DsSipConstants.INVITE == m_response.getMethodID())
			return true;
		return false;
	}

	public AseProtocolSession getPrevSession() {
		return m_prevSession;
	}

	public void setPrevSession(AseProtocolSession session) {
		m_prevSession = (AseSipSession)session;
	}

	public java.lang.Object clone()
	throws CloneNotSupportedException {
		if (m_l.isDebugEnabled()) m_l.debug("clone(): enter");

		AseSipServletResponse copy = (AseSipServletResponse)super.clone();

		// Response is already cloned as DsSipMessage in super class
		copy.m_response		= (DsSipResponse)copy.m_message;
		copy.m_isReliable	= this.m_isReliable;
		// since it is used for app composition only
		copy.m_source		= AseSipConstants.SRC_ASE;
		copy.m_ackAlreadyGenerated = false;

		if (m_l.isDebugEnabled()) m_l.debug("clone(): exit");
		return copy;
	}

	// -- data members --

	/**
	 * Reference to the original ASE SIP request.
	 */
	private AseSipServletRequest m_request = null;

	private ReplicatedMessageHolder m_ReqHolder;

	/**
	 * The associated DS stack response object.
	 */
	private DsSipResponse m_response = null;

	/**
	 * Reference to the corresponding client transaction for responses from 
	 * the network.
	 */
	private transient DsSipClientTransaction m_clientTxn = null;

	/**
	 * Reference to the relevant server transaction in case the response is 
	 * originated from servlets or ASE (auto-generated 100 Trying).
	 */
	private transient DsSipServerTransaction m_serverTxn = null;

	private transient AsePseudoSipClientTxn m_pClientTxn = null;

	private transient AsePseudoSipServerTxn m_pServerTxn = null;

	/**
	 * Flag to indicate whether an ACK has been generated for the response.
	 * Would be applicable only for 2xx responses.
	 */
	private boolean m_ackAlreadyGenerated = false;

	private boolean m_prackAlreadyGenerated = false;

	/**
	 * DsByteString token for testing presence of 100rel
	 */
	private final static DsByteString DS_100REL = new DsByteString("100rel");


	/**
	 * Whether this response is reliable
	 */
	private boolean m_isReliable = false;

	/**
	 * In-coming or out-going timestamp
	 */
	private long timestamp = -1;


	// logger instance for the class
	private static Logger m_l =
		Logger.getLogger(AseSipServletResponse.class.getName());

	boolean isResponseProcessed = false;
	boolean branchResponseFlagInternal = false;
	boolean isBestResponse = false;
	
	
	//
	// UT code
	//

	public static void main(String[] args) 
	{
		if (m_l.isInfoEnabled()) m_l.info("Creating container");
		AseEngine container = new AseEngine();

		if (m_l.isInfoEnabled()) m_l.info("Creating sipconn");
		AseSipConnector sipconn = new AseSipConnector(container);

	if (m_l.isInfoEnabled()) 	m_l.info("Creating sipsession");
		AseSipSession sipsess = new AseSipSession(sipconn);

		if (m_l.isInfoEnabled()) m_l.info("Creating request");
		AseSipServletRequest request = new AseSipServletRequest(sipsess, 
				sipconn, "INVITE", "one@sender.com:5060", 
				"someone@receiver.com:5060", "dummyCall");


		if (m_l.isInfoEnabled()) m_l.info("Creating response[200]");
		AseSipServletResponse response200 = 
			new AseSipServletResponse(request, 200);

		if (m_l.isInfoEnabled()) m_l.info("Creating response[485]");
		AseSipServletResponse response485 = 
			new AseSipServletResponse(request, 485, "Some reason");

		if (m_l.isInfoEnabled()) m_l.info("Creating response[777]");
		AseSipServletResponse response777 = 
			new AseSipServletResponse(request, 777);
		if (m_l.isInfoEnabled()) m_l.info("This type of response creation is blocked at Request.");

		if (m_l.isInfoEnabled()) m_l.info("testing getRequest() ..");
		SipServletRequest req = response200.getRequest();
		String method = req.getMethod();
		if (m_l.isInfoEnabled()) m_l.info("Method for request is: " + method);

		int status200 = response200.getStatus();
		String reason200 = response200.getReasonPhrase();
		if (m_l.isInfoEnabled()) m_l.info("Status, reason for 200 is: " + status200 + ", " + reason200);

		int status485 = response485.getStatus();
		String reason485 = response485.getReasonPhrase();
		if (m_l.isInfoEnabled()) m_l.info("Status, reason for 485 is: " + status485 + ", " + reason485);

	if (m_l.isInfoEnabled()) 	m_l.info("This would be stopped at Request.");
		int status777 = response777.getStatus();
		String reason777 = response777.getReasonPhrase();
		if (m_l.isInfoEnabled()) m_l.info("Status, reason for 777 is: " + status777 + ", " + reason777);

		if (m_l.isInfoEnabled()) m_l.info("testing setStatus(int) ..");
		response777.setStatus(100);
		status777 = response777.getStatus();
		reason777 = response777.getReasonPhrase();
		if (m_l.isInfoEnabled()) m_l.info("Status, reason for 777 is: " + status777 + ", " + reason777);

		if (m_l.isInfoEnabled()) m_l.info("testing setStatus(int, String) ..");
		response777.setStatus(300, "Moved");
		status777 = response777.getStatus();
		reason777 = response777.getReasonPhrase();
		if (m_l.isInfoEnabled()) m_l.info("Status, reason for 777 is: " + status777 + ", " + reason777);

		SasProtocolSession protoSession = response200.getProtocolSession();
		if (m_l.isInfoEnabled()) m_l.info("Protocol session is: " + protoSession);

		DsSipResponse dsResponse = response200.getDsResponse();
		if (m_l.isInfoEnabled()) m_l.info("DS response is: " + dsResponse);

		DsSipClientTransaction clientTxn = response200.getClientTxn();
		if (m_l.isInfoEnabled()) m_l.info("DS client transaction is: " + clientTxn);

		DsSipServerTransaction serverTxn = response200.getServerTxn();
		if (m_l.isInfoEnabled()) m_l.info("DS server transaction is: " + serverTxn);

		if (m_l.isInfoEnabled()) m_l.info("testing canMutateContactHeader() ..");
		boolean mut3xx = response777.canMutateContactHeader();
		boolean mut485 = response485.canMutateContactHeader();
		boolean mut200 = response200.canMutateContactHeader();
		if (m_l.isInfoEnabled())
 {      m_l.info("Can mutate 3xx: " + mut3xx);
		m_l.info("Can mutate 485: " + mut485);
		m_l.info("Can mutate 200: " + mut200);

		m_l.info("setting 485 immutable ..");
		}
		response485.setImmutable();
		mut485 = response485.canMutateContactHeader();
		if (m_l.isInfoEnabled()) 
		{ m_l.info("Can mutate 485: " + mut485);

		m_l.info("exiting from main()");
	} 
	}// main(): UT code
	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {

		if(m_l.isDebugEnabled())
			m_l.debug("Entering readExternal ");
		super.readExternal(in);

		m_ackAlreadyGenerated = in.readBoolean();
		m_isReliable = in.readBoolean();
		timestamp = in.readLong();

		m_ReqHolder = (ReplicatedMessageHolder)in.readObject();
		//m_response = (DsSipResponse)in.readObject();
		//m_message = m_response;

		/////////////////////// stack obj de-serialization ////////////////////
		int msgSize = in.readInt();	
		byte[] msgBytes = new byte[msgSize];
		in.readFully(msgBytes);
		try {
			m_response = (DsSipResponse) DsSipMessage.createMessage(msgBytes);
			m_message = m_response;
		} catch(DsSipParserListenerException exp) {
			m_l.error("Exception in De-serializing stack response", exp);
		} catch(DsSipParserException exp) {
			m_l.error("Exception in De-serializing stack response", exp);
		}
		m_message = m_response;
		//Setting the reference as null for easy GC
		msgBytes = null;
		/////////////////////// stack obj de-serialization ////////////////////
		
		//FT Handling Strategy: Making Request serialisable in order to send
		// handle the transient calls (sending ACK from standby node) after failover
		//TODO: This needs to be checked at FT as this will impact the point
		//where we need to send ACK from standby and this will happen when FT
		//happens after sending 200 OK to party-a and ACK needs to be send to
		//party-b after receiving ACK from party-a
		//m_request = (AseSipServletRequest) in.readObject();
		
		if(m_l.isDebugEnabled())
			m_l.debug("Exiting readExternal : Message = "+this.getMethod());
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {

		if(m_l.isDebugEnabled())
			m_l.debug("Entering writeExternal : Message = "+this.getMethod());
		super.writeExternal(out);

		out.writeBoolean(m_ackAlreadyGenerated);
		out.writeBoolean(m_isReliable);
		out.writeLong(timestamp);

		out.writeObject(m_ReqHolder);
		//out.writeObject(m_response);

		//////////////////////// stack obj serialization ////////////////////
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		m_response.write(baos);
		byte[] msgBytes = baos.toByteArray();
		out.writeInt(msgBytes.length);
		out.write(msgBytes);
		//Setting the reference as null for easy GC
		msgBytes = null;
		//////////////////////// stack obj serialization ////////////////////

		//FT Handling Strategy: Making Request serializable in order to handle 
		//the transient calls (sending ACK from standby node) after failover
		//TODO: This needs to be checked at FT as this will impact the point
		//where we need to send ACK from standby and this will happen when FT
		//happens after sending 200 OK to party-a and ACK needs to be send to
		//party-b after receiving ACK from party-a
		//out.writeObject(m_request);
		if(m_l.isDebugEnabled())
			m_l.debug("Exiting writeExternal");
	}

	public void storeMessageAttr() {
		super.storeMessageAttr();
		if(m_request !=null) {
			if(m_request.m_attrStored == true)
				return;
			//m_request.m_attrStored = true;
			m_ReqHolder = new ReplicatedMessageHolder(m_request);
			m_request.storeMessageAttr();
			//((AseApplicationSession)m_request.getApplicationSession()).
			//		addSipServletMessage(m_request.assignMessageId(), m_request);
		}
	}



	/**
	 * Called on standby while activating.
	 */
	public void activate() {
		super.activate();
		if(this.m_ReqHolder != null) {
			this.m_request = (AseSipServletRequest)this.m_ReqHolder.resolve();
		}
	}

	/**
	 * Called on active when this message is set as attribute.
	 */
	public int assignMessageId() {
		if(this.m_request != null) {
			this.m_request.assignMessageId();
		}

		return super.assignMessageId();
	}

	// Bug ID : 5325 JSR 289.8 PRACK Support

	/**
	 * Returns a PRACK request corresponding to this response. This is done only 
	 * for 1xx responses (Except 100 Trying) for the purpose of acknowledging 
	 * the reliable provisional responses
	 *
	 * @return PRACK corresponding to this response.
	 */
	public SipServletRequest createPrack() throws Rel100Exception {

		if (m_l.isDebugEnabled()) m_l.debug("createPrack() method called:");
		this.checkIfReliable();
		if(DsSipConstants.INVITE != m_request.getDsRequest().getMethodID() ){
			m_l.error("PRACK can be generated for an initial INVITE txn only");
			throw new Rel100Exception(Rel100Exception.NOT_INVITE);	
		}

		else if(m_isReliable == false){
			m_l.error("PRACK can not be generated for non reliable provisional responses");
			throw new Rel100Exception(Rel100Exception.NOT_100rel);
		}

		else if (m_prackAlreadyGenerated) {
			m_l.error("PRACK already generated.");
			throw new IllegalStateException ("PRACK already generated.");
		}
		super.checkSessionState();
		AseSipServletRequest prack = 
			( (AseConnectorSipFactory) m_connector.getFactory()).createPrack( 
					this);
		m_prackAlreadyGenerated = true;
		return prack;
	}

	// Bug ID : 5638 :Method is used to get AuthHeaders.
	public DsSipHeaderList getAuthHeaders(DsSipResponse dsResponse,String header ){
		DsSipHeaderList headerListProxyAuthenticate = null;
		try{
			// This is require because of the behaviour of DS stack on order to auto convert DsSipHeaderList into DsSipAuthenticateHeaderBase
			DsSipAuthenticateHeaderBase authHeader = (DsSipAuthenticateHeaderBase)dsResponse.getAuthenticationHeader();
			if(header.equalsIgnoreCase(("PROXY_AUTHENTICATE"))){
				headerListProxyAuthenticate = (DsSipHeaderList)dsResponse.getHeaders(DsSipResponse.PROXY_AUTHENTICATE);	
			}else if(header.equalsIgnoreCase(("WWW_AUTHENTICATE"))){
				headerListProxyAuthenticate = (DsSipHeaderList)dsResponse.getHeaders(DsSipResponse.WWW_AUTHENTICATE);
			}
		}catch (Exception e) {
			m_l.error(e.getMessage());
		}
		return headerListProxyAuthenticate;

	}

	// Bug Id : 5638 Method is used to get challenge realms.
	public Iterator<String> getChallengeRealms() {
		// TODO Auto-generated method stub
		if (m_l.isDebugEnabled()) m_l.debug(" Entering in getChallengeRealms()...");
		List<String> realms = new ArrayList<String>();
		try{
			DsSipResponse dsResponse = ((AseSipServletResponse)this).getDsResponse();

			DsSipHeaderList headerListProxyAuthenticate  = getAuthHeaders(dsResponse,"PROXY_AUTHENTICATE");
			DsSipAuthenticateHeaderBase authHeader =null;
			if(headerListProxyAuthenticate != null ){
				for(int i =0 ; i<headerListProxyAuthenticate.size();i++){
					authHeader = (DsSipAuthenticateHeaderBase)headerListProxyAuthenticate.get(i);
					realms.add(DsByteString.toString(authHeader.getChallengeInfo().getRealm()));
				}
			}else{
				DsSipHeaderList headerListWWWAuthenticate  = getAuthHeaders(dsResponse,"WWW_AUTHENTICATE");
				if(headerListWWWAuthenticate != null ){
					for(int i =0 ; i<headerListWWWAuthenticate.size();i++){
						authHeader = (DsSipAuthenticateHeaderBase)headerListWWWAuthenticate.get(i);
						realms.add(DsByteString.toString(authHeader.getChallengeInfo().getRealm()));
					}
				}
			}
		}catch (Exception e) {
			m_l.error(e.getMessage());
		}
		return realms.iterator();
	}

	public ProxyBranch getProxyBranch() {
		// TODO Auto-generated method stub
		if (m_l.isDebugEnabled()) m_l.debug(" Entering in getProxyBranch...");
		AseProxyImpl aseProxyImpl = (AseProxyImpl) this.getProxy();
		if(aseProxyImpl == null){
			return null;
		}

		return aseProxyImpl.getBranches(this.getRequest().getRequestURI());
	}

	public boolean isBranchResponse() {
	
		if(this.isBranchResponseInternal() && !this.isResponseProcessed() ){
			return true;
		}
		if(this.isBranchResponseInternal() && this.isResponseProcessed() ){
			return false;
		}
		return false;
	}
	
	public boolean isResponseProcessed() {
		return isResponseProcessed;
	}

	public void setResponseProcessed(boolean isResponseProcessed) {
		this.isResponseProcessed = isResponseProcessed;
	}


	public boolean isBranchResponseInternal() {
		
		return branchResponseFlagInternal;
		
	}
	
	public void setBranchResponseInternal(boolean branchResponseFlagInternal) {
		this.branchResponseFlagInternal = branchResponseFlagInternal;
	}
	
	
	
	public void removeAttribute(String arg0) {
		// TODO Auto-generated method stub

	}


	protected boolean isIncoming() {
		return m_source != AseSipConstants.SRC_SERVLET;
		//return (m_serverTxn != null || m_pServerTxn != null);
	}
	/**
	 * Returns <code>true</code> is the response is committed. A response would 
	 * be committed in the following conditions:
	 * 
	 * �This message is an incoming non-reliable provisional response received by a servlet acting as a UAC 
	 * �This message is an incoming reliable provisional response for which PRACK has already been generated. (Note that this scenario applies to containers that support the 100rel extension.) 
	 * �This message is an incoming final response received by a servlet acting as a UAC for a Non INVITE transaction 
	 * �This message is a response which has been forwarded upstream 
	 * �This message is an incoming final response to an INVITE transaction and an ACK has been generated 
	 */
	public boolean isCommitted() {
		boolean incoming = this.isIncoming();
		boolean provisional = getDsResponse().getResponseClass() == 1;  
		boolean isFinalResp = this.isFinalResponse();
		boolean isInviteResp = this.isInviteResponse();

		if(m_l.isDebugEnabled()){
			m_l.debug("SipServletResponse:: isCommitted():: incoming==" + incoming + 
					":: reliable==" + m_isReliable + 
					":: final=="+ isFinalResp + 
					":: inviteResp ==" + isInviteResp + 
					":: ackGenerated ==" + m_ackAlreadyGenerated );
		}

		//a response which has been forwarded upstream
		if(!incoming)
			return true;

		//an incoming non-reliable provisional response received by a servlet acting as a UAC
		if(incoming && !this.m_isReliable && provisional)
			return true;

		//an incoming reliable provisional response for which PRACK has already been generated.
		if(incoming && this.m_isReliable && provisional && this.m_prackAlreadyGenerated)
			return true;

		//an incoming final response received by a servlet acting as a UAC for a Non INVITE transaction
		if(incoming && isFinalResp && !isInviteResp)
			return true;

		//an incoming final response to an INVITE transaction and an ACK has been generated
		if(incoming && isFinalResp && isInviteResp && this.m_ackAlreadyGenerated)
			return true;

		return false;
	}
}

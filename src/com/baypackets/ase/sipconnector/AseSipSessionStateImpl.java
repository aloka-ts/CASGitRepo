/**
 * AseSipSessionStateImpl.java
 */

package com.baypackets.ase.sipconnector;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.sip.Address;
import javax.servlet.sip.SipURI;

import com.baypackets.ase.serializer.kryo.KryoIncrementalStreamProcessor;
import com.baypackets.ase.util.AseObjectInputStream;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.spi.replication.Replicable;
import com.baypackets.ase.spi.replication.ReplicationSet;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipFromHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderInterface;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderList;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipMessage;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRouteHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipTag;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipToHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsURI;

/**
 * This class implements the SIP session state machine
 */

@DefaultSerializer(ExternalizableSerializer.class)
class AseSipSessionStateImpl implements AseSipSessionState, Cloneable, Replicable 
{

	private static Logger m_logger = Logger.getLogger(AseSipSessionStateImpl.class);
	private static final long serialVersionUID = -384885848884389094L;

	private static boolean isKryoSerializer = BaseContext.getConfigRepository().getValue(Constants.IS_KRYO_SERIALIZER_ACTIVATED).equals("1");

	private AseSipSessionStateImpl.SessionStateAttributes sessionStateAttributes;

	/**
	 * The constructor
	 */
	public AseSipSessionStateImpl() {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("AseSipSessionStateImpl constructor");
		}
		sessionStateAttributes = new SessionStateAttributes();
	}

	public void setSessionState(int newState)
	{
		sessionStateAttributes.m_sipState = newState;
		if(this.m_sipSession != null)
		{
			m_sipSession.setAttribute(Constants.ATTRIBUTE_SESSION_STATE,new Integer(sessionStateAttributes.m_sipState));  //NJADAUN
			m_sipSession.setModified(true);
		}
		this._modified = true;
	}
	public int getSessionState()
	{
		return sessionStateAttributes.m_sipState;
	}

	public String getCallId() 
	{
		return sessionStateAttributes.m_callId;
	}

	public void setCallId(String callId) {
	
		sessionStateAttributes.m_callId = callId;
	}
	 
	public DsByteString getLocalTag() 
	{
		return sessionStateAttributes.m_localTag;
	}
	 
	public DsByteString getRemoteTag() 
	{
		return sessionStateAttributes.m_remoteTag;
	}
	 
	 public void setLocalTag(DsByteString tag) {
		 sessionStateAttributes.m_localTag = tag;
		 this._modified = true;
	 }
	 
	 public void setRemoteTag(DsByteString tag) {
		 sessionStateAttributes.m_remoteTag = tag;
		 this._modified = true;
	 }
	 
	 public DsSipFromHeader getFromHeader() {
		  return sessionStateAttributes.m_fromHeader;
	 }
	 
	 public DsSipToHeader getToHeader() {
		  return sessionStateAttributes.m_toHeader;
	 }
	 
	 public void setFromHeader(DsSipFromHeader header) {
		 sessionStateAttributes.m_fromHeader = header;
		 this._modified = true;
	 }
	 
	 public void setToHeader(DsSipToHeader header) {
		 sessionStateAttributes.m_toHeader = header;
		 this._modified = true;
	 }
	 
	 public DsSipHeaderInterface getLocalTarget() {
		  return sessionStateAttributes.m_localTarget;
	 }
	 
	 public DsURI getRemoteTarget() {
		  return sessionStateAttributes.m_remoteTarget;
	 }
	 
	 public long getLocalCSeqNumber() {
			this._modified = true;
		  return ++sessionStateAttributes.m_localCSeq;
	 }
	 
	 public long getLocalCSeqNumber(long incr) {
			this._modified = true;
			this.sessionStateAttributes.m_localCSeq = this.sessionStateAttributes.m_localCSeq + incr;
			return this.sessionStateAttributes.m_localCSeq;
	 }
	 
	 public long getRemoteCSeqNumber() {
		  return sessionStateAttributes.m_remoteCSeq;
	 }
	 
	 public DsSipHeaderList getRouteSet() {
		  return sessionStateAttributes.m_routeSet;
	 }
	 
	 public boolean isSecure() {
		  return sessionStateAttributes.m_isSecure;
	 }
	 
	 public AseSipDialogId getUpstreamDialogId() {
		  return sessionStateAttributes.m_upstreamDialogId;
	 }
	 
	 public AseSipDialogId getDownstreamDialogId() {
		  return sessionStateAttributes.m_downstreamDialogId;
	 }

	 public Address getLocalParty() {
		  return sessionStateAttributes.m_localParty;
	 }

	 public void setLocalParty( Address lParty )
	 {
		 sessionStateAttributes.m_localParty = lParty;
		 this._modified = true;
	 }

	 public void setRemoteParty( Address rParty )
	 {
		sessionStateAttributes.m_remoteParty = rParty;
		this._modified = true;
	 }
	 
	 public Address getRemoteParty() {
		  return sessionStateAttributes.m_remoteParty;
	 }

	 public boolean isSupervised() {
		  return sessionStateAttributes.m_supervised;
	 }
	 
	 public boolean isRecordRouted() {
		  return sessionStateAttributes.m_recordRoute;
	 }
	 
	 public SipURI getRecordRouteURI() {
		  return sessionStateAttributes.m_recordRouteURI;
	 }
	 
	 /**
	  * Method handleInitialRequest
	  * Invoked when a new initial request is received by ASE. This is invoked
	  * by the corresponding method within the AseSipSession
	  */
	 void handleInitialRequest(AseSipServletRequest request)
		  throws AseSipSessionStateException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering AseSipSessionStateImpl " +
									"handleInitialRequest" + m_sipSession.getLogId());
		  
		  // Retrieve the remote target from the CONTACT header
		  // If not found then retrieve it from the FROM header
		  // If still not found throw an exception
		  sessionStateAttributes.m_remoteTarget = AseSipContactHeaderHandler.getURI(request);
		  if (null == sessionStateAttributes.m_remoteTarget)
			  sessionStateAttributes.m_remoteTarget = AseSipFromHeaderHandler.getURI(request);

		  if (null == sessionStateAttributes.m_remoteTarget) {
				m_logger.error("Failed to retrieve remote target from the " +
									"incoming request." + m_sipSession.getLogId());
				throw new
					 AseSipSessionStateException("Failed to retrieve " +
														  "remote target from request");
		  }

		 sessionStateAttributes.m_callId = request.getCallId();
		 sessionStateAttributes.m_localParty = request.getTo();
		 sessionStateAttributes.m_remoteParty = request.getFrom();
		 sessionStateAttributes.m_remoteCSeq = request.getDsRequest().getCSeqNumber();
		  
		 if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving AseSipSessionStateImpl " +
									"handleInitialRequest" + m_sipSession.getLogId());
	 }

	 /**
	  * Method handleSubsequentRequest
	  * Invoked when a subsequent request is received by ASE. This is invoked
	  * by the corresponding method within the AseSipSession
	  */
	 void handleSubsequentRequest(AseSipServletRequest request)
		  throws AseSipSessionStateException {
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("AseSipSessionStateImpl " +
									"handleSubsequentRequest" +
									m_sipSession.getLogId());
	 }
	 
	 /**
	  * Method sendInitialRequest
	  * Invoked when a new initial message is been sent by ASE. This is invoked
	  * by the corresponding method within the AseSipSession
	  */
	 void sendInitialRequest(AseSipServletRequest request)
		  throws AseSipSessionStateException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering AseSipSessionStateImpl " +
									"sendInitialRequest" + m_sipSession.getLogId());
		  
		  // Copy the CONTACT header from the request and store it as the local
		  // target
		  sessionStateAttributes.m_localTarget = request.getDsRequest().getContactHeader();
		  sessionStateAttributes.m_callId = request.getCallId();
		  sessionStateAttributes.m_localParty = request.getFrom();
		  sessionStateAttributes.m_remoteParty = request.getTo();
		  sessionStateAttributes.m_remoteTarget = request.getDsRequest().getURI();
		  sessionStateAttributes.m_localTag = request.getDsRequest().getFromTag();

		  // Retrieve the FROM and the TO headers
		  try {
			  sessionStateAttributes.m_fromHeader = request.getDsRequest().getFromHeaderValidate();
			  sessionStateAttributes.m_toHeader = request.getDsRequest().getToHeaderValidate();
		  }
		  catch (Exception e) {
				// Should not happen
				m_logger.error("Error retrieving FROM/TO headers", e);
		  }
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving AseSipSessionStateImpl " +
									"sendInitialRequest" + m_sipSession.getLogId());
	 }

	 /**
	  * Invoked when a response is received from the stack. This is invoked by
	  * the corresponding method in the AseSipSession
	  * This method is capabale of affecting dialog state for PROXY and UAC
	  */
	 void recvResponse(AseSipServletResponse response)
		  throws AseSipSessionStateException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering AseSipSessionStateImpl " +
									"recvResponse" + m_sipSession.getLogId());

		  // Retrieve the response class and the status code
		  int responseClass = response.getDsResponse().getResponseClass();
		  int statusCode = response.getDsResponse().getStatusCode();
		  
		  // If state is STATE_INITIAL or STATE_EARLY and we get a 3XX-6XX 
		  // response then reset the dialog parameters
		  if (sessionStateAttributes.m_sipState == STATE_INITIAL || sessionStateAttributes.m_sipState == STATE_EARLY) {
				if (3 <= responseClass && responseClass <= 6) {
					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Resetting state. " +
											  "New State: STATE_INITIAL" +
											  m_sipSession.getLogId());

					 // Clear the remote TAG and the remote CSEQ
					 if( (sessionStateAttributes.m_toHeader != null) && (sessionStateAttributes.m_toHeader.isTagPresent()) ) {
						 sessionStateAttributes.m_toHeader.removeTag();
					 }
					 sessionStateAttributes.m_remoteTag = null;
					sessionStateAttributes.m_remoteCSeq = -1;

					 // Clear the route set
					 sessionStateAttributes.m_routeSet.clear();
					 
					 // Reset the remote target
					 if(sessionStateAttributes.m_toHeader != null) {
						 sessionStateAttributes.m_remoteTarget = sessionStateAttributes.m_toHeader.getURI();
					 }

					 // Invoke the dialogTerminated callback on the session
					 // if dialog was created in the first place
					 if (STATE_EARLY == sessionStateAttributes.m_sipState) {
						 this.setSessionState(STATE_INITIAL);
						 m_invalidateWhenReady = true;
						  m_sipSession.dialogTerminated();
					 }
					 
					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Leaving AseSipSessionStateImpl " +
											  "recvResponse" + m_sipSession.getLogId());
					 return;
				}
		  }

		  // If original state is STATE_INITIAL
		  if (sessionStateAttributes.m_sipState == STATE_INITIAL) {
				if (true == response.canCreateDialog()) {
					 // If 1XX response new state is STATE_EARLY
					 if (1 == response.getDsResponse().getResponseClass()) {
						  if (null == response.getDsResponse().getToTag()) {
								if (m_logger.isDebugEnabled())
									 m_logger.debug("1XX without TO tag. " +
														 "NOOP" + m_sipSession.getLogId());
								return;
						  }
								
						  if (m_logger.isDebugEnabled())
								m_logger.debug("New State: STATE_EARLY");
						  this.setSessionState(STATE_EARLY);
					 }
					 // If 2XX response new state is STATE_CONFIRMED
					 else if (2 == response.getDsResponse().getResponseClass()) {
						  if (m_logger.isDebugEnabled())
								m_logger.debug("New State: STATE_CONFIRMED");
						  this.setSessionState(STATE_CONFIRMED);
					 }

					 // Over write remoteParty Address object to update tag
					sessionStateAttributes.m_remoteParty = response.getTo();
						  
					 // Get the remote tag and the TO header
					sessionStateAttributes.m_remoteTag = response.getDsResponse().getToTag();
					 try {
						 sessionStateAttributes.m_toHeader = response.getDsResponse().
								getToHeaderValidate();
					 }
					 catch (Exception e) {
						  // Should not happen
						  m_logger.error("Error retrieving TO header", e);
					 }
					 
					 // Get the contact header
					 DsURI rTgt = AseSipContactHeaderHandler.getURI(response);
					 if (null != rTgt)
						 sessionStateAttributes.m_remoteTarget = rTgt;

					 // Compute the route set
					 setRouteSet(response.getDsResponse(), -1);
					 
					 // Create the upstream and downstream dialog IDS
					 // Upstream Dialog Id
					 if (AseSipSession.ROLE_PROXY == m_sipSession.getRole()) {
						  DsByteString fromTag = response.getDsResponse().getFromTag();
						  DsByteString toTag = response.getDsResponse().getToTag();

						  // Upstream Dialog Id
						 sessionStateAttributes.m_upstreamDialogId =
								new AseSipDialogId(sessionStateAttributes.m_callId, fromTag, toTag);
						  
						  // Downstream Dialog Id
						 sessionStateAttributes.m_downstreamDialogId =
								new AseSipDialogId(sessionStateAttributes.m_callId, toTag, fromTag);
						  
						  // Copy the PROXY parameters
						 sessionStateAttributes.m_recordRoute = m_sipSession.getProxy().getRecordRoute();
						 sessionStateAttributes.m_supervised = m_sipSession.getProxy().getSupervised();
						  
						  if (sessionStateAttributes.m_recordRoute)
							  sessionStateAttributes.m_recordRouteURI = m_sipSession.getProxy().
									 getRecordRouteUri(response);
					 }
					 else {
						 sessionStateAttributes.m_upstreamDialogId =
								new AseSipDialogId(sessionStateAttributes.m_callId,
										sessionStateAttributes.m_localTag,
										sessionStateAttributes.m_remoteTag);
						  // Downstream Dialog Id
						 sessionStateAttributes.m_downstreamDialogId =
								new AseSipDialogId(sessionStateAttributes.m_callId,
										sessionStateAttributes.m_remoteTag,
										sessionStateAttributes.m_localTag);
					 }
					 
					 // Invoke a callback on the parent session to indicate that
					 // a dialog is created
					 m_sipSession.dialogCreated(response);

					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Leaving AseSipSessionStateImpl " +
											  "recvResponse" + m_sipSession.getLogId());
					 return;
				}
		  }

		  // If original state is STATE_EARLY
		  if (sessionStateAttributes.m_sipState == STATE_EARLY) {
				if (true == response.canCreateDialog()) {
					 // If 1XX response then no change
					 if (1 == response.getDsResponse().getResponseClass()) {
						  if (m_logger.isDebugEnabled())
								m_logger.debug("No state change. State: STATE_EARLY");
					 }
					 // If 2XX response new state is STATE_CONFIRMED
					 else if (2 == response.getDsResponse().getResponseClass()) {
						  if (m_logger.isDebugEnabled())
								m_logger.debug("New State: STATE_CONFIRMED");
						  this.setSessionState(STATE_CONFIRMED);

						  // Get the contact header
						  DsURI rTgt = AseSipContactHeaderHandler.getURI(response);
						  if (null != rTgt)
							  sessionStateAttributes.m_remoteTarget = rTgt;

						  // Recompute the route set
						  setRouteSet(response.getDsResponse(), -1);
					 }

					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Leaving AseSipSessionStateImpl " +
											  "recvResponse" + m_sipSession.getLogId());
					 return;
				}
		  }
		  
		  // If original state is STATE_CONFIRMED
		  // canTerminateDialog returns TRUE if response is a 481 or a 408
		  // These responses always terminate a dialog
		  if (sessionStateAttributes.m_sipState == STATE_CONFIRMED) {
				if (true == response.canTerminateDialog()) {
					 if (m_logger.isDebugEnabled())
						  m_logger.debug("New State: STATE_TERMINATED");
					 
					 this.setSessionState(STATE_TERMINATED);
					 m_sipSession.dialogTerminated();

					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Leaving AseSipSessionStateImpl " +
											  "recvResponse" + m_sipSession.getLogId());
					 return;
				}
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("NOOP. Leaving AseSipSessionStateImpl " +
									"recvResponse" + m_sipSession.getLogId());		  
	 }

	 /**
	  * Invoked when a response is sent by ASE. This is invoked by
	  * the corresponding method in the AseSipSession
	  */
	 void handleResponse(AseSipServletResponse response)
		  throws AseSipSessionStateException {
		  if (m_logger.isDebugEnabled())
				m_logger.debug("AseSipSessionStateImpl " +
									"handleResponse" + m_sipSession.getLogId());
	 }
	 
	 /**
	  * Invoked when a response is sent by ASE. This is invoked by
	  * the corresponding method in the AseSipSession
	  */
	 void sendResponse(AseSipServletResponse response)
		  throws AseSipSessionStateException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering AseSipSessionStateImpl " +
									"sendResponse" + m_sipSession.getLogId());
	 
		  // Get the response class and the status code
		  int responseClass = response.getDsResponse().getResponseClass();
		  int statusCode = response.getDsResponse().getStatusCode();
		  
		  // If 100 response nothing to do
		  if (100 == statusCode) {
				if (m_logger.isDebugEnabled()) {
					 m_logger.debug("100 Response. NOOP. " +
										 m_sipSession.getLogId());
					 m_logger.debug("Leaving AseSipSessionStateImpl " +
										 "sendResponse" + m_sipSession.getLogId());
				}
				return;
		  }
				
		  // If state is STATE_INITIAL or STATE_EARLY and we get a 3XX-6XX 
		  // response then terminate the dialog
		  if (sessionStateAttributes.m_sipState == STATE_INITIAL || sessionStateAttributes.m_sipState == STATE_EARLY) {
				if (3 <= responseClass && responseClass <= 6) {
					if(!((response.getRequest()).isInitial()))
						return ;
					
					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Terminating the dialog. " +
											  "New State: STATE_TERMINATED" +
											  m_sipSession.getLogId());

					 ConfigRepository config = (ConfigRepository) Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
					 String includeToTag_Str = config.getValue(Constants.PROP_INCLUDE_TO_TAG_IN_SIP_3XX_6XX_RESPONSE);
					 String propogateFailureResponseAck = config.getValue(Constants.PROP_PROPOGATE_FAILURE_RESPONSE_ACK_TO_APPLICATION);
					 
					 boolean includeToTag=true;
					 
					 if (includeToTag_Str == null|| includeToTag_Str.equals("")) {
						 includeToTag = true;
						} 
					 else{
						 includeToTag_Str=includeToTag_Str.trim();
							if (includeToTag_Str.equalsIgnoreCase("false"))
								includeToTag = false;
					 }
					 if(includeToTag){
						 if (m_logger.isDebugEnabled()) {
							 m_logger.debug("Including to tag in 3xx-6xx response....");
						 }
						 DsSipToHeader tHdr = null;
						 try {
							 tHdr = response.getDsResponse().getToHeaderValidate();
						 }
						 catch (Exception e) {
							 // Should not happen
							 m_logger.error("Exception in retrieving TO headers", e);
						 }

						 // Local tag from TO header else generate
						 // If generated add it to the TO header
						 if (true == tHdr.isTagPresent())
							 sessionStateAttributes.m_localTag = tHdr.getTag();
						 else {
						if (sessionStateAttributes.m_localTag == null) {
							sessionStateAttributes.m_localTag = DsSipTag
									.generateTag();
						}
							 tHdr.setTag(sessionStateAttributes.m_localTag);
						 }
					 }else{
						 m_logger.debug("Not including to tag in response as "+Constants.PROP_INCLUDE_TO_TAG_IN_SIP_3XX_6XX_RESPONSE+" is:"+includeToTag);
					 }
					 
				if (Boolean.parseBoolean(propogateFailureResponseAck)) { // Allow 3xx-6xx to service

					DsSipFromHeader fHdr = null;
					try {
						fHdr = response.getDsResponse().getFromHeaderValidate();
					} catch (Exception e) {
						// Should not happen
						m_logger.error("Exception retrieving FROM/TO headers", e);
					}

					sessionStateAttributes.m_remoteTag = fHdr.getTag();

					if (m_logger.isDebugEnabled()) {
						m_logger.debug("3xxCall Id = " + sessionStateAttributes.m_callId);
						m_logger.debug("3xxRemote Tag = " + sessionStateAttributes.m_remoteTag.toString());
						m_logger.debug("3xxLocal Tag = " + sessionStateAttributes.m_localTag.toString());
					}
					
					// Upstream Dialog Id
					sessionStateAttributes.m_upstreamDialogId = new AseSipDialogId(
							sessionStateAttributes.m_callId,
							sessionStateAttributes.m_remoteTag,
							sessionStateAttributes.m_localTag);

					// Downstream Dialog Id
					sessionStateAttributes.m_downstreamDialogId = new AseSipDialogId(
							sessionStateAttributes.m_callId,
							sessionStateAttributes.m_localTag,
							sessionStateAttributes.m_remoteTag);

					m_sipSession.dialogCreated(response);
					m_terminateOnACK = true;
				} else {
					// Invoke the dialogTerminated callback on the session
					// if dialog was created in the first place
					if (STATE_EARLY == sessionStateAttributes.m_sipState) {
						m_sipSession.dialogTerminated();
					}
				}
				
					 this.setSessionState(STATE_TERMINATED);
					 
					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Leaving AseSipSessionStateImpl " +
											  "sendResponse" + m_sipSession.getLogId());
					
					 return;
				}
		  }

		  // If original state is STATE_INITIAL
		  if (sessionStateAttributes.m_sipState == STATE_INITIAL || (sessionStateAttributes.m_sipState == STATE_EARLY) ) {
				if (true == response.canCreateDialog()) {
					 // If 1XX response new state is STATE_EARLY
					 if (1 == responseClass && sessionStateAttributes.m_sipState == STATE_INITIAL) {
						  if (m_logger.isDebugEnabled())
								m_logger.debug("New State: STATE_EARLY");
					 	  this.setSessionState(STATE_EARLY);
					 }
					 // If 2XX response new state is STATE_CONFIRMED
					 else if (2 == responseClass) {
						  if (m_logger.isDebugEnabled())
								m_logger.debug("New State: STATE_CONFIRMED");
					 	  this.setSessionState(STATE_CONFIRMED);
					 }

					 // Over write localParty Address object to update tag
					sessionStateAttributes.m_localParty = response.getTo();

					 // Retrieve the FROM and TO headers from the response
					 DsSipFromHeader fHdr = null;
					 DsSipToHeader tHdr = null;
					 try {
						  fHdr = response.getDsResponse().getFromHeaderValidate();
						  tHdr = response.getDsResponse().getToHeaderValidate();
					 }
					 catch (Exception e) {
						  // Should not happen
						  m_logger.error("Exception retrieving FROM/TO headers", e);
					 }
					 
					 // Remote Tag from FROM header
					sessionStateAttributes.m_remoteTag = fHdr.getTag();

					 // Local tag from TO header else generate
					 // If generated add it to the TO header
					 if (true == tHdr.isTagPresent())
						 sessionStateAttributes.m_localTag = tHdr.getTag();
					 else {
						 if(sessionStateAttributes.m_localTag==null){
						    sessionStateAttributes.m_localTag = DsSipTag.generateTag();
						 }
						  tHdr.setTag(sessionStateAttributes.m_localTag);
					 }
					 
					 // Now construct our FROM and TO headers
					 try {
						 sessionStateAttributes.m_toHeader = new DsSipToHeader(fHdr.getNameAddress(),
																	fHdr.getParameters());
						 sessionStateAttributes.m_toHeader.setTag(sessionStateAttributes.m_remoteTag);

						 sessionStateAttributes.m_fromHeader = new DsSipFromHeader(tHdr.getNameAddress(),
																		 tHdr.getParameters());
						 sessionStateAttributes.m_fromHeader.setTag(sessionStateAttributes.m_localTag);
					 }
					 catch (Exception e) {
						  // Should not happen
						  m_logger.error("Error construction FROM/TO headers", e);
					 }
					 
					 // Compute the route set
					 setRouteSet(response.getDsResponse(), 0);

					//Get the Contact Header from the response and set in the Session State.
					//So that it can be used in the subsequent requests.
					DsSipHeaderInterface ch = response.getDsResponse().getContactHeader();
					if(ch != null) {
						sessionStateAttributes.m_localTarget = ch;
					}

					 // Create the upstream and downstream dialog IDS
					 // Upstream Dialog Id

					if (m_logger.isDebugEnabled()) {
						 m_logger.debug("Call Id = " + sessionStateAttributes.m_callId);
						 m_logger.debug("Remote Tag = " + sessionStateAttributes.m_remoteTag.toString());
						 m_logger.debug("Local Tag = " + sessionStateAttributes.m_localTag.toString());
					}

					sessionStateAttributes.m_upstreamDialogId =
						  new AseSipDialogId(sessionStateAttributes.m_callId,
								  sessionStateAttributes.m_remoteTag,
								  sessionStateAttributes.m_localTag);

					 // Downstream Dialog Id
					sessionStateAttributes.m_downstreamDialogId =
						  new AseSipDialogId(sessionStateAttributes.m_callId,
								  sessionStateAttributes.m_localTag,
								  sessionStateAttributes.m_remoteTag);
					 
					 // Invoke a callback on the parent session to indicate that
					 // a dialog is created
					 if (null != m_sipSession)
						 if (m_logger.isDebugEnabled()) { 
							 m_logger.debug("NON NULL SIP SESSION");
						 }
					 
					 m_sipSession.dialogCreated(response);

					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Leaving AseSipSessionStateImpl " +
											  "sendResponse" + m_sipSession.getLogId());
					 return;
				}
		  }

		  // If original state is STATE_EARLY
		  if (sessionStateAttributes.m_sipState == STATE_EARLY) {
				if (true == response.canCreateDialog()) {
					 // If 1XX response then no change
					 if (1 == responseClass) {
						  if (m_logger.isDebugEnabled())
								m_logger.debug("No state change. State: STATE_EARLY " + response.getDsResponse());
						  
						  // Over write localParty Address object to update tag
							sessionStateAttributes.m_localParty = response.getTo();

							 // Retrieve the FROM and TO headers from the response
							 DsSipFromHeader fHdr = null;
							 DsSipToHeader tHdr = null;
							 try {
								  fHdr = response.getDsResponse().getFromHeaderValidate();
								  tHdr = response.getDsResponse().getToHeaderValidate();
							 }
							 catch (Exception e) {
								  // Should not happen
								  m_logger.error("Exception retrieving FROM/TO headers", e);
							 }
							 
							 // Remote Tag from FROM header
							sessionStateAttributes.m_remoteTag = fHdr.getTag();

							 // Local tag from TO header else generate
							 // If generated add it to the TO header
							 if (true == tHdr.isTagPresent())
								 sessionStateAttributes.m_localTag = tHdr.getTag();
							 else {
								 sessionStateAttributes.m_localTag = DsSipTag.generateTag();
								  tHdr.setTag(sessionStateAttributes.m_localTag);
							 }
							 
							 // Now construct our FROM and TO headers
							 try {
								 sessionStateAttributes.m_toHeader = new DsSipToHeader(fHdr.getNameAddress(),
																			fHdr.getParameters());
								 sessionStateAttributes.m_toHeader.setTag(sessionStateAttributes.m_remoteTag);

								 sessionStateAttributes.m_fromHeader = new DsSipFromHeader(tHdr.getNameAddress(),
																				 tHdr.getParameters());
								 sessionStateAttributes.m_fromHeader.setTag(sessionStateAttributes.m_localTag);
							 }
							 catch (Exception e) {
								  // Should not happen
								  m_logger.error("Error construction FROM/TO headers", e);
							 }
							 
							 
					 }
					 // If 2XX response new state is STATE_CONFIRMED
					 else if (2 == responseClass) {
						  if (m_logger.isDebugEnabled())
								m_logger.debug("New State: STATE_CONFIRMED");
					 	  this.setSessionState(STATE_CONFIRMED);
						  // Recompute the route set
						  setRouteSet(response.getDsResponse(), 0);
					 }

					//Get the Contact Header from the response and set in the Session State.
					//So that it can be used in the subsequent requests.
					DsSipHeaderInterface ch = response.getDsResponse().getContactHeader();
					if(ch != null) {
						sessionStateAttributes.m_localTarget = ch;
					}

					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Leaving AseSipSessionStateImpl " +
											  "sendResponse" + m_sipSession.getLogId());
					 return;
				}
		  }
		  
		  // If original state is STATE_CONFIRMED
		  if (sessionStateAttributes.m_sipState == STATE_CONFIRMED) {
				if (true == response.canTerminateDialog()) {
					 if (m_logger.isDebugEnabled())
						  m_logger.debug("New State: STATE_TERMINATED");
					 
					 this.setSessionState(STATE_TERMINATED);
					 m_sipSession.dialogTerminated();

					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Leaving AseSipSessionStateImpl " +
											  "sendResponse" + m_sipSession.getLogId());
					 return;
				}
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("NOOP. Leaving AseSipSessionStateImpl " +
									"sendResponse" + m_sipSession.getLogId());		  
	 }

	 /**
	  * Invoked when a subsequent request is received from the network.
	  * This is invoked by the corresponding method in the AseSipSession
	  * A dialog is created or terminated by NOTIFY requests
	  * A dialog is terminated by BYE requests
	  * Once a dialog is created certain requests can modify the remote target
	  * Once a dialog is created certain requests can modify the remote CSEQ
	  * TBD
	  */
	 void recvRequest(AseSipServletRequest request)
		  throws AseSipSessionStateException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering AseSipSessionStateImpl " +
									"recvRequest" + m_sipSession.getLogId());
		  
		  if(request.getMethod().equalsIgnoreCase("ACK") && m_terminateOnACK == true) {
			  m_sipSession.dialogTerminated();
			  return;
		  }

		  // Check for remote target updation
		  if (sessionStateAttributes.m_sipState == STATE_EARLY || sessionStateAttributes.m_sipState == STATE_CONFIRMED) {
				if (true == request.canResetRemoteTarget()) {
					 DsURI rTgt = AseSipContactHeaderHandler.getURI(request);
					 if (null != rTgt)
						 sessionStateAttributes.m_remoteTarget = rTgt;
				}
		  }
		  
		  // Check for CSEQ updation
		  if (true == request.canModifySequenceNumber()) {
			  sessionStateAttributes.m_remoteCSeq = request.getDsRequest().getCSeqNumber();
		  }
		  
		  if (sessionStateAttributes.m_sipState == STATE_INITIAL) {
				if (true == request.canCreateDialog() &&
					 true == isDialogReferenced()) {
					 if (m_logger.isDebugEnabled())
						  m_logger.debug("New State: STATE_CONFIRMED");

					 this.setSessionState(STATE_CONFIRMED);
					 
					 // Get the remote tag and construct the TO header
					sessionStateAttributes.m_remoteTag = request.getDsRequest().getFromTag();
					 try {
						  DsSipFromHeader fHdr = request.getDsRequest().
								getFromHeaderValidate();
						 sessionStateAttributes.m_toHeader = new DsSipToHeader(fHdr.getNameAddress(),
																	fHdr.getParameters());
						 sessionStateAttributes.m_toHeader.setTag(sessionStateAttributes.m_remoteTag);
					 }
					 catch (Exception e) {
						  // Should not happen
						  m_logger.error("Error retrieving FROM header", e);
					 }
					 
					 // Get the contact header
					 DsURI rTgt = AseSipContactHeaderHandler.getURI(request);
					 if (null != rTgt)
						 sessionStateAttributes.m_remoteTarget = rTgt;

					 // Compute the route set
					 setRouteSet(request.getDsRequest(), 0);
					 
					 // Create the upstream and downstream dialog IDS
					 // Upstream Dialog Id
					sessionStateAttributes.m_upstreamDialogId =
						  new AseSipDialogId(sessionStateAttributes.m_callId,
								  sessionStateAttributes.m_localTag,
								  sessionStateAttributes.m_remoteTag);
					 // Downstream Dialog Id
					sessionStateAttributes.m_downstreamDialogId =
						  new AseSipDialogId(sessionStateAttributes.m_callId,
								  sessionStateAttributes.m_remoteTag,
								  sessionStateAttributes.m_localTag);
					 
					 // Invoke a callback on the parent session to indicate that
					 // a dialog is created
					 m_sipSession.dialogCreated(request);

					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Leaving AseSipSessionStateImpl " +
											  "recvRequest" + m_sipSession.getLogId());
					 return;
				}

				if (true == request.canTerminateDialog() &&
					 false == isDialogReferenced()) {
					 if (m_logger.isDebugEnabled())
						  m_logger.debug("New State: STATE_TERMINATED");

					 if (sessionStateAttributes.m_sipState == STATE_EARLY)
						  m_sipSession.dialogTerminated();
					 
					 this.setSessionState(STATE_TERMINATED);

					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Leaving AseSipSessionStateImpl " +
											  "recvRequest" + m_sipSession.getLogId());
					 return;
				}
		  }

		  if (sessionStateAttributes.m_sipState == STATE_EARLY) {
				if (true == request.canCreateDialog() &&
					 true == isDialogReferenced()) {

					 if (m_logger.isDebugEnabled())
						  m_logger.debug("New State: STATE_CONFIRMED");
					 
					 // Get the contact header
					 DsURI rTgt = AseSipContactHeaderHandler.getURI(request);
					 if (null != rTgt)
						 sessionStateAttributes.m_remoteTarget = rTgt;

					 // Compute the route set
					 setRouteSet(request.getDsRequest(), 0);

					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Leaving AseSipSessionStateImpl " +
											  "recvRequest" + m_sipSession.getLogId());
					 return;
				}
				
				if (true == request.canTerminateDialog() &&
					 false == isDialogReferenced()) {
					 
					 if (m_logger.isDebugEnabled())
						  m_logger.debug("New State: STATE_TERMINATED");

					 m_sipSession.dialogTerminated();
					 this.setSessionState(STATE_TERMINATED);

					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Leaving AseSipSessionStateImpl " +
											  "recvRequest" + m_sipSession.getLogId());
					 return;
				}
		  }
				
		  if (sessionStateAttributes.m_sipState == STATE_CONFIRMED) {
				if (true == request.canTerminateDialog() &&
					 false == isDialogReferenced()) {

					 if (m_logger.isDebugEnabled())
						  m_logger.debug("New State: STATE_TERMINATED");

					 m_sipSession.dialogTerminated();
					 this.setSessionState(STATE_TERMINATED);

					 
					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Leaving AseSipSessionStateImpl " +
											  "recvRequest" + m_sipSession.getLogId());
					 return;
				}
		  }
		  
	 }
	 
	 /**
	  * Invoked when a subsequent request is sent by ASE.
	  * This is invoked by the corresponding method in the AseSipSession
	  * Dialog is terminated by BYE and NOTIFY requests
	  * TBD
	  */
	 void sendSubsequentRequest(AseSipServletRequest request)
		  throws AseSipSessionStateException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering AseSipSessionStateImpl " +
									"sendSubsequentRequest" + m_sipSession.getLogId());

		  if (sessionStateAttributes.m_sipState == STATE_CONFIRMED) {
				if (true == request.canTerminateDialog() &&
					 false == isDialogReferenced()) {
					 if (m_logger.isDebugEnabled())
						  m_logger.debug("New State: STATE_TERMINATED");

					 this.setSessionState(STATE_TERMINATED);
                	 m_sipSession.dialogTerminated(); 

					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Leaving AseSipSessionStateImpl " +
											  "sendSubsequentRequest" +
											  m_sipSession.getLogId());
					 return;
				}
		  }
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("NOOP. Leaving AseSipSessionStateImpl " +
									"sendSubsequentRequest" + m_sipSession.getLogId());
	 }

	 /**
	  * Compute the routSet from the received message
     * Retrieve the record-route headers from the message and compute
     * the route set depending on our role
     * The direction parameter can be either 0 or -1.
     * 0 signifies route and record-route headers have the same order
     * -1 signifies route headers have opposite order of record-record headers
     */
	 private void setRouteSet(DsSipMessage message, int direction) {
        if (m_logger.isDebugEnabled()) {
            m_logger.debug("Entering AseSipSessionStateImpl.setRouteSet" +
                         m_sipSession.getLogId() +" in direction " +direction);
        }
		this._modified = true;

        // First retrieve the record-route headers from the message
        // We use the DSByteString constant BS_RECORD_ROUTE for this
        DsSipHeaderList rrHeaders =
            message.getHeaders(DsSipConstants.BS_RECORD_ROUTE);
		  
		// Clear route-set first
		sessionStateAttributes.m_routeSet.clear();

        // Now we need to convert the record-route headers into route headers
        // Retrieve each header from the list as a byte string and create
        // a new headerlist of route headers
		  
        // Check if the returned list is null
        if (null == rrHeaders) {
            if (m_logger.isDebugEnabled()) {
                m_logger.debug("Message does not contain record-route " +
										 "headers. Route set will be NULL" +
										 m_sipSession.getLogId());
            }

            if (m_logger.isDebugEnabled()) {
                  m_logger.debug("Leaving AseSipSessionStateImpl.setRouteSet" +
											m_sipSession.getLogId());
            }
            return;
        }
		  
		// Get an iterator from the DsSipHeaderList
        java.util.ListIterator iter = rrHeaders.listIterator();
        while (iter.hasNext()) {
            DsByteString header =
                ((DsSipHeaderInterface)(iter.next())).getValue();
            // Create a new DsSipRouteHeader
            try {
                DsSipRouteHeader rHeader = new DsSipRouteHeader(header);
                if (0 == direction)
					sessionStateAttributes.m_routeSet.addLast(rHeader);
                else
					sessionStateAttributes.m_routeSet.addFirst(rHeader);
            }
            catch (Exception e) {
                m_logger.error("Exception creating DsSipRouteHeader " + e);
            }
        }
        if (m_logger.isDebugEnabled()) {
            m_logger.debug("Leaving AseSipSessionStateImpl.setRouteSet" +
									m_sipSession.getLogId());
        }
    }

	 /**
	  * Set the parent session
	  */
	 void setParentSession(AseSipSession parentSession) {
		  m_sipSession = parentSession;
		  if (m_sipSession.getAttribute(Constants.ATTRIBUTE_SESSION_STATE) == null) {
		 		 m_sipSession.setAttribute(Constants.ATTRIBUTE_SESSION_STATE,new Integer(sessionStateAttributes.m_sipState));
		  } //NJADAUN 
		  if (m_logger.isDebugEnabled())
				m_logger.debug("AseSipSessionStateImpl setParentSession" +
									m_sipSession.getLogId());
	 }
	 
	 /**
	  * Registers a AseSipDialogReferenceManager
	  */
	 void registerDialogReferenceManager(AseSipDialogReferenceManager mgr) {
		 m_dialogReferenceManagers.add(mgr);

		  if (m_logger.isDebugEnabled())
				m_logger.debug("AseSipSessionStateImpl " +
									"registerDialogReferenceManager" +
									m_sipSession.getLogId());
	 }

	 /**
	  * Check if a dialog is still referenced
	  */
	 private boolean isDialogReferenced() {
		  Iterator iter = m_dialogReferenceManagers.iterator();
		  while (iter.hasNext()) {
				AseSipDialogReferenceManager mgr =
					 (AseSipDialogReferenceManager)(iter.next());
				if (true == mgr.isDialogReferenced()) {
					 if (m_logger.isDebugEnabled())
						  m_logger.debug("isDialogReferenced returning TRUE");
					 return true;
				}
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("isDialogReferenced returning FALSE");
		  return false;
	 }

	 /**
	  * Implementation of the cloenable interface
	  * This gets invoked when the session is cloned for handling
	  * MFR's and dialog establishing NOTIFY requests
	  */
	 public Object clone() throws CloneNotSupportedException {
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering clone");
		  
		  AseSipSessionStateImpl clonedState =
				(AseSipSessionStateImpl)(super.clone());
		  
		  // Set the dialog state to STATE_INITIAL
		  clonedState.sessionStateAttributes.m_sipState = AseSipSessionState.STATE_INITIAL;
			clonedState.m_sipSession.setAttribute(
				Constants.ATTRIBUTE_SESSION_STATE,new Integer(sessionStateAttributes.m_sipState));

		  // Initialize CSeq no. in clone session state
	 	  clonedState.sessionStateAttributes.m_remoteCSeq = -1;

		  // Set the dialog ids to null
		  clonedState.sessionStateAttributes.m_upstreamDialogId = null;
		  clonedState.sessionStateAttributes.m_downstreamDialogId = null;

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving clone");
		  
		  return clonedState;
	 }

	////////////////////Replicable Interface implementation starts //////////////
	
	public void partialActivate(ReplicationSet parent) {
		//NOOP
	}

	public void activate(ReplicationSet parent) {
		//NOOP
	}

	public String getReplicableId() {
		return this.replicableId;
	}

	public void setReplicableId(String replicableId) {
		this.replicableId = replicableId;
		this._modified = true;
	}

	public boolean isModified() {
		return this._modified;
	}

	public boolean isNew() {
		return this._new;
	}

	public boolean isReadyForReplication() {
		//int relStatus = this.m_sipSession.getOrigRequest().getRelStatus();
		//Now replication can be done at initial state as well and in case of Party-B
		//original request not necessarily have 100 rel always there by allowing replication
		//at any point now
		return true;
	}

	public void readIncremental(ObjectInput in) throws IOException, ClassNotFoundException {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("readIncremental() called for: " + this.getClass());
		}

		if (isKryoSerializer) {
			ClassLoader currentClassLoader = ((AseObjectInputStream) in).getClassLoader();
			this.sessionStateAttributes = (SessionStateAttributes) KryoIncrementalStreamProcessor.readObjectFromClassAwareStream(in, currentClassLoader);
		} else {
			this.sessionStateAttributes = (SessionStateAttributes) in.readObject();
		}

		if (m_logger.isDebugEnabled()) {
			m_logger.debug("readIncremental() leaving for: " + this.getClass());
		}
	}

	public void writeIncremental(ObjectOutput out, int replicationType) throws IOException {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("writeIncremental () called for: " + this.getClass());
		}
		//FT Handling strategy Update: Replication will be done for the provisional
		//responses as well, so need to replicate the upstream and downstream dialog
		//in order to handle PRACK request after sending the 183 message
		//TO tag is replicated to handle the ACK for
		//Initial INVITE request. local TO tag is not getting replicated at the time of
		//provisional response replication, which is the reason why while sending 200 OK
		//for INVITE TO tag from sip session(SessionStateImpl object) is not getting set
		//and correspondingly not getting retrieved in ACK and hence ACK is getting strayed.

		if (isKryoSerializer) {
			KryoIncrementalStreamProcessor.writeObjectToStream(sessionStateAttributes, out);
		} else {
			out.writeObject(sessionStateAttributes);
		}

		if (m_logger.isDebugEnabled()) {
			m_logger.debug("writeIncremental() leaving for: " + this.getClass());
		}
	}

	public void replicationCompleted() {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug(m_sipSession.getId()+"::AseSipSessionStateImpl: replicationCompleted(): Setting _new = false");
		}
		replicationCompleted(false);

	}
	
	public void replicationCompleted(boolean noReplication) {
		if(m_logger.isDebugEnabled())
			m_logger.debug(m_sipSession.getId()+"::AseSipSessionStateImpl: replicationCompleted()"+noReplication);
		
		if(!noReplication){
			this._new = false;
			this._modified = false;
		}
		
		if(m_logger.isDebugEnabled()) {
			m_logger.debug(m_sipSession.getId()+"::Leaving replicationCompleted():"+noReplication);
		}
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("readExternal() called");
		}

		this.replicableId = (String)in.readObject();
		this.sessionStateAttributes = (SessionStateAttributes) in.readObject();

		if (m_logger.isDebugEnabled()) {
			m_logger.debug("readExternal() completed");
		}
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("writeExternal called");
		}

		if (m_logger.isDebugEnabled()){
			m_logger.debug("setFirstReplicationCompleted(true); ");
		}
		this.setFirstReplicationCompleted(true);
	
		out.writeObject(this.replicableId);
		out.writeObject(this.sessionStateAttributes);

		if (m_logger.isDebugEnabled()) {
			m_logger.debug("writeExternal completed");
		}
	}
	
	@Override
	public boolean isFirstReplicationCompleted() {
		return mFirstReplicationCompleted;
	}

	@Override
	public void setFirstReplicationCompleted(boolean isFirstReplicationCompleted) {
		mFirstReplicationCompleted=isFirstReplicationCompleted;
	}

	private String replicableId;
	private boolean _new = true;
	private boolean _modified = true;
	private boolean mFirstReplicationCompleted=false;

	// The AseSipSession whose state is been maintained
	transient private AseSipSession m_sipSession;

	public boolean m_invalidateWhenReady = false;
	 
	private boolean m_terminateOnACK = false;

	//Transient Attributes
	transient private List m_dialogReferenceManagers = new ArrayList(); // List of Dialog reference managers


	private class SessionStateAttributes implements Serializable {

		private static final long serialVersionUID = -384885848884389094L;

		private String m_callId;
		private DsSipFromHeader m_fromHeader = null;
		private DsSipToHeader m_toHeader = null;
		private DsSipHeaderInterface m_localTarget = null; // Local target (our contact header)
		private DsURI m_remoteTarget = null; // Remote target
		private long m_localCSeq = 0; //Local CSeq number. Initially set to 0.
		private long m_remoteCSeq = -1; // Remote CSeq number.
		private boolean m_supervised = false; // If ROLE_PROXY then is te supervised flag set
		private boolean m_recordRoute = false; // If ROLE_PROXY then if record routed
		private AseSipDialogId m_upstreamDialogId = null;
		private AseSipDialogId m_downstreamDialogId = null;
		private DsByteString m_localTag = null;
		private DsByteString m_remoteTag = null;
		private Address m_localParty = null;
		private Address m_remoteParty = null;
		private DsSipHeaderList m_routeSet = new DsSipHeaderList(DsSipConstants.ROUTE); // Route set.
		private boolean m_isSecure = false; // Secure flag. If this dialog is on a secure channel. Always false
		private SipURI m_recordRouteURI = null; // The record roue URI
		private int m_sipState = STATE_INITIAL; // Current state of the SIP dialog. At construction time sipState is always INITIAL
	}
}

/**
 * AseSipReplicationHandler.java
 */

package com.baypackets.ase.sipconnector;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.sip.SipSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.spi.replication.ReplicationEvent;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;

/**
 * This class impements the replication handler
 */

class AseSipReplicationHandler implements AseSipReplicationHandlerInterface,
														Cloneable {

	 private AseSipSession m_sipSession = null;

	 public AseSipReplicationHandler(AseSipSession session) {
	 	this.m_sipSession = session;
	}

	 public void setSipSession(AseSipSession session) {
	 	this.m_sipSession = session;
	 }

	 public Object clone() throws CloneNotSupportedException {
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering clone.");

		  AseSipReplicationHandler clonedHdlr =
				(AseSipReplicationHandler)(super.clone());
		  clonedHdlr.m_prCount = this.m_prCount;

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving clone.");
		  
		  return clonedHdlr;
	 }
	 
	public boolean isReadyForReplication() {
		if (m_logger.isDebugEnabled())
			m_logger.debug("Inside isReadyForReplication");

		// FT handling strategy update: Replication will also be done
		// after receiving or sending the INVITE Reliable Provisional Response
		// Because of this reason replication would be done even when there
		// is pending request and state is initial.
		//CONFIRMED state added in the check because of the fact that for Party-A
		//from tag and to tag is added while sending 200 OK and at that time
		//prCount is greater than zero. prCount becomes zero on receiving ACK and at that
		//time sipSessionState Impl is not selected for replication and hence replication of sip
		//session with from and to tag will never happen
		if (this.m_sipSession.getLinkedSessionId() != null){
			// B2BUA Calls
			AseSipSession linkedSipSession = (AseSipSession) m_sipSession
					.getApplicationSession().getSipSession(m_sipSession.getLinkedSessionId());
			if ((0 == m_prCount && this.m_sipSession.getSessionState() != AseSipSessionState.STATE_INITIAL)
					|| (m_prCount > 0 && this.m_sipSession.getSessionState() == AseSipSessionState.STATE_INITIAL)
					|| this.m_sipSession.getSessionState() == AseSipSessionState.STATE_EARLY
					|| this.m_sipSession.getSessionState() == AseSipSessionState.STATE_CONFIRMED
					|| this.m_sipSession.getSessionState() == AseSipSessionState.STATE_TERMINATED
					|| linkedSipSession.getSessState() == AseSipSessionState.STATE_EARLY) {
				if (m_logger.isDebugEnabled())
					m_logger.debug("isReadyForReplication return TRUE");
				return true;
			}
		}else{
			if ((0 == m_prCount && this.m_sipSession.getSessionState() != AseSipSessionState.STATE_INITIAL)
					|| (m_prCount > 0 && this.m_sipSession.getSessionState() == AseSipSessionState.STATE_INITIAL)
					|| this.m_sipSession.getSessionState() == AseSipSessionState.STATE_CONFIRMED
					|| this.m_sipSession.getSessionState() == AseSipSessionState.STATE_TERMINATED
					|| this.m_sipSession.getSessionState() == AseSipSessionState.STATE_EARLY) {
				if (m_logger.isDebugEnabled())
					m_logger.debug("isReadyForReplication return TRUE");
				return true;
			}
		}
		
		if (m_logger.isDebugEnabled())
			m_logger.debug("isReadyForReplication return FALSE");

		return false;
	}
	 
	 public void incrementPrCount() {
		  m_prCount ++;

		  if (m_logger.isDebugEnabled())
				m_logger.debug("New Pending Request Count = " + m_prCount);
	 }

	 public void decrementPrCount() {
		  m_prCount --;

		  if (m_logger.isDebugEnabled())
				m_logger.debug("New Pending Request Count = " + m_prCount);
	 }

	 public void resetPrCount() {
		  m_prCount = 0;

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Pending Request Count reset to 0");
	 }

	 public void handleInitialRequest(AseSipServletRequest request,
												 AseSipSession session) {
	 }
	 
	 public void handleSubsequentRequest(AseSipServletRequest request,
													 AseSipSession session) {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering handleSubsequentRequest" +
									session.getLogId());

		  // If ACK request then the INVITE transaction is complete
		  // so we check if replication is required
		  if(session.getSessionState() != AseSipSessionState.STATE_INITIAL
		  && session.getRole() != AseSipSession.ROLE_PROXY
		  && DsSipConstants.ACK == request.getDsRequest().getMethodID()) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("ACK request and replication required. " +
										 "Generating replication event" +
										 session.getLogId());
				
				if(!session.isValid()){
					if (m_logger.isDebugEnabled())
						m_logger.debug("handleSubsequentRequest() Session invalidated returning without replication.");
					
					return;
				}
				
				ReplicationEvent event =
					 new ReplicationEvent(session, "INVITE");
				
				//Bug 13141
				if(session.getAttribute("FIRST_ACK_RECEIVED") != null) {
				  if (m_logger.isDebugEnabled())
						m_logger.debug("ACK for RE-INVITE transaction");
					event.setReinviteTranComplete(true);
				} else {
					session.setAttribute("FIRST_ACK_RECEIVED", "TRUE");
				}
				
				session.sendReplicationEvent(event);
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving handleSubsequentRequest" +
									session.getLogId());
	 }

	 public void recvRequest(AseSipServletRequest request,
									 AseSipSession session) {
	 }

	 public void sendInitialRequest(AseSipServletRequest request,
											  AseSipSession session) {
	 }
	 
	 public void sendSubsequentRequest(AseSipServletRequest request,
												  AseSipSession session) {
	 }
	 
	 public void requestPreSend(AseSipServletRequest request,
										 AseSipSession session) {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering requestPreSend" + session.getLogId());

		  // If ACK request then the INVITE transaction is complete
		  // so we check if replication is required
		  if(session.getSessionState() != AseSipSessionState.STATE_INITIAL
		  && DsSipConstants.ACK == request.getDsRequest().getMethodID()) {
		 		if (request.isNon2XXAck()) {
					if (m_logger.isDebugEnabled()) {
						 m_logger.debug("ACK request for Non 2XX Response. "+
					 				"Replication not required "+session.getLogId());
					}
				} else {
					if (m_logger.isDebugEnabled())
						 m_logger.debug("ACK request and replication required. " +
										 "Generating replication event" +
										 session.getLogId());
					ReplicationEvent event =
						 new ReplicationEvent(session, "INVITE");
					
					//Bug 13141
					if(session.getAttribute("FIRST_ACK_RECEIVED") != null) {
					  if (m_logger.isDebugEnabled())
							m_logger.debug("ACK for RE-INVITE transaction");
						event.setReinviteTranComplete(true);
					} else {
						session.setAttribute("FIRST_ACK_RECEIVED", "TRUE");
					}
					
					session.sendReplicationEvent(event);
				}
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving requestPreSend" + session.getLogId());
	 }
	 
	 public void requestPostSend(AseSipServletRequest request,
										  AseSipSession session) {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering requestPostSend" + session.getLogId());

		  /*
		  // If ACK request then the INVITE transaction is complete
		  // so we check if replication is required
		  if (DsSipConstants.ACK == request.getDsRequest().getMethodID()) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("ACK request and replication required. " +
										 "Generating replication event" +
										 session.getLogId());
				
				ReplicationEvent event =
					 new ReplicationEvent(session, "INVITE");
				session.sendReplicationEvent(event);
		  }
		  */

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving requestPostSend" + session.getLogId());
	 }
	 
	 public void handleResponse(AseSipServletResponse response,
										 AseSipSession session) {
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering handleResponse" + session.getLogId());
		  
		  if((session.getSessionState() != AseSipSessionState.STATE_INITIAL)
		  && (session.getRole() != AseSipSession.ROLE_PROXY)
		  && (response.getDsResponse().getStatusCode() > 199)
		  && (false == response.isInviteResponse())) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Non-INVITE final response received. " +
										 "Generate replication event" +
										 session.getLogId());
				ReplicationEvent event =
					 new ReplicationEvent(session, response.getMethod());
				session.sendReplicationEvent(event);
		  }
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving handleResponse" + session.getLogId());
	 }

	 /**
	  * This method is called from AseSipSession class in order to 
	  * do the replication in following cases:
	  * When 200 OK final response for INVITE request has been received
	  * When 180 or 183 provisional response for INVITE request has been 
	  * received
	  * @param response AseSipServletResponse
	  * @param session AseSipSession
	  */
	 public void recvResponse(AseSipServletResponse response,
									  AseSipSession session) {
		// FT handling strategy update: Replication will be done before sending
		// and after receiving 200 OK final response for INVITE rather than on 
		// receiving ACK

		if ((session.getSessionState() != AseSipSessionState.STATE_INITIAL)
				&& (session.getRole() != AseSipSession.ROLE_PROXY)
				&& (response.getDsResponse().getStatusCode() == 200)
				&& (true == response.isInviteResponse())) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("INVITE 200 final response received. "
						+ "Generate replication event" + session.getLogId());
			ReplicationEvent event = new ReplicationEvent(session, response
					.getMethod());
			
			if(!response.getRequest().isInitial()) {
				event.setReinviteTranComplete(true);
			}
			
			session.sendReplicationEvent(event);
			
		}
		// FT handling strategy update: Replication will be done after receiving 
		// 180 or 183 provisional response only in case of 100rel
		// coming in either REQUIRE header or SUPPOPRTED header of the INVITE
		// request
		AseSipServletRequest request = (AseSipServletRequest) response.getRequest();
		int relStatus = request.getRelStatus();
		if ((session.getSessionState() != AseSipSessionState.STATE_INITIAL)
				&& (session.getRole() != AseSipSession.ROLE_PROXY)
				&& ((response.getDsResponse().getStatusCode() == 180) || (response
						.getDsResponse().getStatusCode() == 183))
				&& (true == response.isInviteResponse())
				&& (relStatus == AseSipServletRequest.REL_REQUIRED || relStatus == AseSipServletRequest.REL_SUPPORTED)) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("INVITE Reliable Provisional response received. "
						+ "Generate replication event" + session.getLogId());
			//m_sipSession.getApplicationSession().setAttribute("ProvRelMsg", true);
			ReplicationEvent event = new ReplicationEvent(session, String.valueOf(response
					.getDsResponse().getStatusCode()));
			session.sendReplicationEvent(event);
		}
	 }
	 
	 public void sendResponse(AseSipServletResponse response,
									  AseSipSession session) {
	 }

	 public void responsePreSend(AseSipServletResponse response,
										  AseSipSession session) {
	 }
	 
	 public void responsePostSend(AseSipServletResponse response,
											AseSipSession session) {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering responsePostSend" + session.getLogId());

		  if((session.getSessionState() != AseSipSessionState.STATE_INITIAL)
		  && (response.getDsResponse().getStatusCode() > 199) &&
				(false == response.isInviteResponse())) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Non-INVITE final response sent. " +
										 "Generate replication event" +
										 session.getLogId());
				
				ReplicationEvent event =
					 new ReplicationEvent(session, response.getMethod());
				session.sendReplicationEvent(event);
				
		  }
		  
		// FT handling update: Replication will now happen after sending 
		// final response of INVITE rather than on sending ACK
		if ((session.getSessionState() != AseSipSessionState.STATE_INITIAL)
				&& (response.getDsResponse().getStatusCode() > 199)
				&& (true == response.isInviteResponse())) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("INVITE final response sent. "
						+ "Generate replication event" + session.getLogId());

			ReplicationEvent event =null;
			
			int respCode=response.getDsResponse().getStatusCode();
			
			if (respCode > 300 && respCode < 399) {

				event = new ReplicationEvent(session, String.valueOf(respCode));
			} else {
				event = new ReplicationEvent(session, response.getMethod());
			}
			
			if(!response.getRequest().isInitial()) {
				event.setReinviteTranComplete(true);
			}
			
			session.sendReplicationEvent(event);
		}
		
		// FT handling support added for in progress calls only in case of 100rel
		// coming in either REQUIRE header or SUPPOPRTED header of the INVITE
		// request
		AseSipServletRequest request = (AseSipServletRequest) response
				.getRequest();
		int relStatus = request.getRelStatus();
		if ((session.getSessionState() != AseSipSessionState.STATE_INITIAL)
				&& (response.getDsResponse().getStatusCode() == 183 || response
						.getDsResponse().getStatusCode() == 180)
				&& (relStatus == AseSipServletRequest.REL_REQUIRED || relStatus == AseSipServletRequest.REL_SUPPORTED)) {
			if (m_logger.isDebugEnabled())
				m_logger.debug(response.getMethod()
								+ " Reliable Provisional Response sent. Generate replication event"
								+ session.getLogId());
			ReplicationEvent event = new ReplicationEvent(session, String.valueOf(response.getDsResponse().getStatusCode()));
			session.sendReplicationEvent(event);
		}		  
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving responsePostSend" + session.getLogId());
	 }

	 private int m_prCount = 0;
	 
	 transient private static Logger m_logger =
		  Logger.getLogger(AseSipReplicationHandler.class);
}



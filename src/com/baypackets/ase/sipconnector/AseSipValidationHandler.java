/**
 * AseSipValidationHandler.java
 */

package com.baypackets.ase.sipconnector;

import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;

import com.baypackets.ase.container.AseProtocolSession;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * This class provides validation for incoming/outgoing messages against
 * SIP dialog state and SIP session state for UA role
 */

class AseSipValidationHandler {
	 public int handleInitialRequest(AseSipServletRequest request,
												AseSipSession session)
		  throws AseSipValidationException {
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside handleInitialRequest" +
									"Return CONTINUE." +
									session.getLogId());

		  return CONTINUE;
	 }
	 
	 public int handleSubsequentRequest(AseSipServletRequest request,
													AseSipSession session)
		  throws AseSipValidationException {
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering handleSubsequentRequest" +
									session.getLogId());
		  
				
// 		  if (AseProtocolSession.VALID != session.getState()) {
// 				// If this request is an ACK request eat it up
// 				if (DsSipConstants.ACK == request.getDsRequest().getMethodID) {
// 				m_logger.error("Session invalidated. " +
// 									"Create and send 481 response." +
// 									session.getLogId());
// 				// Increment pending request count for this session
// 				session.incrementPrCount();
				
// 				try {
// 					 AseSipServletResponse resp =
// 						  (AseSipServletResponse)request.createResponse(481);
// 					 session.sendResponse(resp);
// 				}
// 				catch (Exception e) {
// 					 m_logger.error("Exception creating and sending 481 response" +
// 										 session.getLogId());
// 				}

// 				throw new AseSipValidationException("Session invalidated. " +
// 																"Sending 481 response");
// 		  }
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Return CONTINUE. Leaving handleInitialRequest" +
									session.getLogId());

		  return CONTINUE;
	 }
	 
	 public int recvRequest(AseSipServletRequest request,
									AseSipSession session)
		  throws AseSipValidationException {
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering recvRequest" +
									session.getLogId());

		  // Always allow ACK requests
		  if (DsSipConstants.ACK == request.getDsRequest().getMethodID()) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("ACK Request. Return CONTINUE. " +
										 "Leaving recvRequest" +
										 session.getLogId());
				return CONTINUE;
		  }

		  // For all requests except ACK and CANCEL validate the received
		  // CSeq number
		  try {
				validateCSeq(request, session);
		  }
		  catch (Exception e) {
				m_logger.error("Exception in cseq validation", e);
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Create and send a 400 response" +
										 session.getLogId());
				
				// Increment the Pending Request count for this session
				session.incrementPrCount();
				
				// If INVITE request add to outstanding request list
				if (DsSipConstants.INVITE == request.getDsRequest().getMethodID())
					 session.addOutstandingRequest(request);
				
				AseSipServletResponse resp =
					 (AseSipServletResponse)request.
					 createResponse(400, "Invalid CSeq");
				try {
					 session.sendResponse(resp);
				}
				catch (AseSipSessionException exp) {
					//Don't log any error message here
				}
				catch (Exception se) {
					 m_logger.error("Exception in sendResponse", se);
				}
				
				throw new AseSipValidationException(e.toString());
		  }

		  // If Dialog state is terminated or the session is invalidated
		  // do not allow anything to come in. Send 481 response
		  boolean send481 = false;
		  AseSipValidationException sExp = null;
		  
		  if (AseProtocolSession.VALID != session.getProtocolSessionState()) {
				m_logger.error("Session invalidated. " +
									"Create and send 481 response. Return NOOP");
				send481 = true;
				sExp = new AseSipValidationException("Session invalidated. " +
																 "Sending 481 response");
		  }
		  else if (AseSipSessionState.STATE_TERMINATED ==
				session.getSessionState()) {
				m_logger.error("Dialog terminated. " +
									"Create and send 481 response. Return NOOP");
				send481 = true;
				sExp = new AseSipValidationException("Dialog termintaed. " +
																 "Sending 481 response");
		  }
		  
		  if (true == send481) {
				// Increment the Pending Request count for this session
				session.incrementPrCount();
				
				// If INVITE request add it to the outstanding request list
				if (DsSipConstants.INVITE == request.getDsRequest().getMethodID())
					 session.addOutstandingRequest(request);
				
				try {
					 AseSipServletResponse resp =
						  (AseSipServletResponse)request.createResponse(481);
					 session.sendResponse(resp);
				}
				catch (AseSipSessionException exp) {
					//Don't log any error message here
				}
				catch (Exception e) {
					 m_logger.error("Exception creating and sending 481 response" +
										 session.getLogId());
				}
				throw sExp;
		  }
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Return CONTINUE. Leaving recvRequest" +
									session.getLogId());
		  
		  return CONTINUE;
	 }
	 
	 public int sendInitialRequest(AseSipServletRequest request,
											 AseSipSession session)
		  throws AseSipValidationException {
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering sendInitialRequest" +
									session.getLogId());
		  
		  if (AseProtocolSession.VALID != session.getProtocolSessionState()) {
				m_logger.error("Session invalidated. " + session.getLogId());
				throw new AseSipValidationException("Session invalidated");
		  }
		  
		  if (AseSipSessionState.STATE_TERMINATED ==
				session.getSessionState()) {
				m_logger.error("Dialog state terminated. " + session.getLogId());
				throw new AseSipValidationException("Dialog terminated");
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Return CONTINUE. Leaving sendInitialRequest" +
									session.getLogId());
		  return CONTINUE;
	 }
	 
	 public int sendSubsequentRequest(AseSipServletRequest request,
												 AseSipSession session)
		  throws AseSipValidationException {
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering sendSubsequentRequest" +
									session.getLogId());
		  
		  if (AseProtocolSession.VALID != session.getProtocolSessionState()) {
				m_logger.error("Session invalidated. " + session.getLogId());
				throw new AseSipValidationException("Session invalidated");
		  }
		  
		  if (AseSipSessionState.STATE_TERMINATED ==
				session.getSessionState()) {
				m_logger.error("Dialog state terminated. " + session.getLogId());
				throw new AseSipValidationException("Dialog terminated");
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Return CONTINUE. Leaving sendSubsequentRequest" +
									session.getLogId());
		  return CONTINUE;
	 }
	 
	 public int requestPreSend(AseSipServletRequest request,
										AseSipSession session)
		  throws AseSipValidationException {
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside requestPreSend. Return CONTINUE." +
									session.getLogId());
		  return CONTINUE;
	 }
	 
	 public int requestPostSend(AseSipServletRequest request,
										 AseSipSession session)
		  throws AseSipValidationException {
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside requestPostSend. Return CONTINUE." +
									session.getLogId());
		  return CONTINUE;
	 }
	 
	 public int handleResponse(AseSipServletResponse response,
										AseSipSession session)
		  throws AseSipValidationException {
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering handleResponse" + session.getLogId());
		  
		  if (AseProtocolSession.VALID != session.getProtocolSessionState()) {
				m_logger.error("Session invalidated. " + session.getLogId());
				throw new AseSipValidationException("Session invalidated");
		  }
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Return CONTINUE. Leaving handleResponse" +
									session.getLogId());

		  return CONTINUE;
	 }
	 
	 public int recvResponse(AseSipServletResponse response,
									 AseSipSession session)
		  throws AseSipValidationException {
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside recvResponse. Return CONTINUE." +
									session.getLogId());
		  return CONTINUE;
	 }
	 
	 public int sendResponse(AseSipServletResponse response,
									 AseSipSession session)
		  throws AseSipValidationException {
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside sendResponse. Return CONTINUE." +
									session.getLogId());
		  return CONTINUE;
	 }
	 
	 public int responsePreSend(AseSipServletResponse response,
										 AseSipSession session)
		  throws AseSipValidationException {
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside responsePreSend. Return CONTINUE." +
									session.getLogId());
		  return CONTINUE;
	 }
	 
	 public int responsePostSend(AseSipServletResponse response,
										  AseSipSession session)
		  throws AseSipValidationException {
		  
		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside responsePostSend. Return CONTINUE." +
									session.getLogId());
		  return CONTINUE;
	 }

	 /**
	  * Validate the cseq number of the incoming request
	  * CSEQ number has to be greater than what we have stored as the 
	  * remote cseq number
	  */
	 void validateCSeq(AseSipServletRequest request, AseSipSession session)
		  throws AseSipValidationException {

		  // Validate CSEQ for all except ACK and CANCEL
		  if (DsSipConstants.ACK == request.getDsRequest().getMethodID() ||
				DsSipConstants.CANCEL == request.getDsRequest().getMethodID())
				return;
		  
		  long cseq = request.getDsRequest().getCSeqNumber();
		  if (cseq <= session.getRemoteCSeqNumber())
				throw new AseSipValidationException("Invalid CSeq Number");
	 }

	 /**
	  * Return values
	  */
	 static int NOOP = 1;
	 static int CONTINUE = 2;
	 static int PROXY = 3;
	 
	 /**
	  * The logger reference
	  */
	 transient private static Logger m_logger =
		  Logger.getLogger(AseSipValidationHandler.class);
}


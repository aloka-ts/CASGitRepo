/**
 * AseSipAckMessageHandler.java
 */

package com.baypackets.ase.sipconnector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;
/**
 * This class provides a UA implememtation for handling ACK messages
 */

class AseSipAckMessageHandler extends AseSipDefaultMessageHandler {

	 public int handleInitialRequest(AseSipServletRequest request,
												AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering handleInitialRequest" +
									session.getLogId());

		  m_logger.error("ACK is not an initial Request");
		  throw new AseSipMessageHandlerException("ACK not an initial request");
	 }
	 
	 public int handleSubsequentRequest(AseSipServletRequest request,
													AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering handleSubsequentRequest " +
									session.getLogId());

		  // If dialog is terminated or session is invalidated
		  boolean eatUpAck = false;
		  
		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated. Eating up the ACK" +
									session.getLogId());
				eatUpAck = true;
		  }

        // BPUsa07445 
        // Even if dialog is terminated we let the ACK through
		  if (true == isDialogTerminated(session)) {
				if (m_logger.isDebugEnabled())
				   m_logger.debug("Dialog terminated. Let the ACK through" +
									   session.getLogId());
				// eatUpAck = true;
		  }

		  if (true == eatUpAck) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Leaving handleSubsequentRequest. " +
										 "Return NOOP " +
										 session.getLogId());
				return genRetNoop();
		  }

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Leaving handleSubsequentRequest. " +
									"Return CONTINUE " +
									session.getLogId());

		  return genRetContinue();
	 }
	 
	 public int recvRequest(AseSipServletRequest request,
									AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering recvRequest " +
									session.getLogId());

		  // If ACK for success response then send it up
		  // If ACK for failure response, eat it up
		  // If stray ACK log and eat it up
		  
		  if(!isAckAllowedChecked){
			isAckAllowedChecked= true;
			ConfigRepository config = (ConfigRepository) Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
			String propogateFailureResponseAck = config.getValue(Constants.PROP_PROPOGATE_FAILURE_RESPONSE_ACK_TO_APPLICATION);
			isAckAllowed = Boolean.parseBoolean(propogateFailureResponseAck);	
		  }
		  
		  
		  AseSipServletRequest req = null;
		  long cseq = request.getDsRequest().getCSeqNumber();
		  
		  if (null == (req = session.removeSuccessRequest(cseq))) {
				if (null == (req = session.removeFailureRequest(cseq))) {
					 m_logger.error("Stray ACK received. Eat it up " +
								 session.getLogId());

					 if (m_logger.isDebugEnabled())
						  m_logger.debug("Return NOOP. " +
											  "Leaving recvRequest" +
											  session.getLogId());

					 return genRetNoop();
				}else if(isAckAllowed){
					AsePseudoSipServerTxn psTxn = req.getPseudoServerTxn();
					if(psTxn != null) {
						m_logger.debug("Passing Non2xx-ACK to pseudo server transaction");
						try {
							psTxn.recvRequest(request);
						} catch(AsePseudoTxnException exp) {
							m_logger.error("Passing NON 2xx ACK to transaction", exp);
						}
					}
					session.decrementPrCount();
					session.setAttribute("ACK_FOR_RESPONSE", "ERROR");
					return genRetContinue();
				}			
				
				if (m_logger.isDebugEnabled()) {
					 m_logger.debug("ACK for non-2xx final response. Eat it up " +
										 "Return NOOP" + session.getLogId());
					 m_logger.debug("Leaving recvRequest " +
										 session.getLogId());
				}
				session.decrementPrCount();
				return genRetNoop();
		  }

		  // ACK for 2XX final response.
		  if (m_logger.isDebugEnabled())
				m_logger.debug("ACK for 2xx final response." + session.getLogId());
		  
		  session.decrementPrCount();
		  session.setAttribute("ACK_FOR_RESPONSE", "SUCCESS");

		  // Notify ACK reception to associated transaction, so that 2xx retransmission
		  // timer is stopped
		  AsePseudoSipServerTxn psTxn = req.getPseudoServerTxn();
		  if(psTxn != null) {
		  		m_logger.debug("Passing 2xx-ACK to pseudo server transaction");
		  		try {
		  			psTxn.recvRequest(request);
		  		} catch(AsePseudoTxnException exp) {
		  			m_logger.error("Passing ACK to transaction", exp);
				}
		  }

		  // If dialog is terminated or session is invalidated
		  boolean eatUpAck = false;
		  
		  if (false == isSessionValid(session)) {
				m_logger.error("Session invalidated. Eating up the ACK" +
									session.getLogId());
				eatUpAck = true;
		  }

        // BPUsa07445 
        // Even if dialog is terminated we let the ACK through
		  if (true == isDialogTerminated(session)) {
				if (m_logger.isDebugEnabled())
				   m_logger.debug("Dialog terminated. Let the ACK through" +
									   session.getLogId());
				// eatUpAck = true;
		  }

		  if (true == eatUpAck) {
				if (m_logger.isDebugEnabled())
					 m_logger.debug("Leaving recvRequest. Return NOOP " +
										 session.getLogId());
				return genRetNoop();
		  }
		  
		  if (m_logger.isDebugEnabled()) {
				m_logger.debug("Leaving recvRequest. Return CONTINUE " +
									session.getLogId());
		  }
		  return genRetContinue();
	 }

	 public int sendInitialRequest(AseSipServletRequest request,
											 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering sendInitialRequest " +
									session.getLogId());

		  m_logger.error("ACK is not an initial request");
		  throw new AseSipMessageHandlerException("ACK not an initial request");
	 }
	 
	 public int sendSubsequentRequest(AseSipServletRequest request,
												 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Entering sendSubsequentRequest " +
									"sendSubsequentRequest" + session.getLogId());

		  // Check if the original INVITE request is in the success list
		  // If not then this is a stary ACK, so generate an exception
		  long cseq = request.getDsRequest().getCSeqNumber();
		  AseSipServletRequest req = null;
		  
		  if (null == (req = session.removeSuccessRequest(cseq))) {
				if (null == (req = session.removeFailureRequest(cseq))) {
					 m_logger.error("Ack does not match any INVITE response");
					 throw new AseSipMessageHandlerException("Stray ACK");
				}
				
				if (m_logger.isDebugEnabled()) {
					 m_logger.debug("ACK for non-2xx final response. " +
										 "Return CONTINUE" + session.getLogId());
					 m_logger.debug("Leaving sendSubsequentRequest " +
										 session.getLogId());
				}
				session.decrementPrCount();
				return genRetContinue();
		  }

		  // ACK for 2xx response
		  session.decrementPrCount();
		  
		  if (m_logger.isDebugEnabled()) {
				m_logger.debug("ACK for 2xx final response. Send it on its way " +
									"Return CONTINUE" + session.getLogId());
				m_logger.debug("Leaving sendSubsequentRequest " +
									session.getLogId());
		  }
		  return genRetContinue();
	 }
	 
	 public int requestPreSend(AseSipServletRequest request,
										AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside requestPreSend. Return CONTINUE " +
									session.getLogId());

		  return genRetContinue();
	 }
	 
	 public int requestPostSend(AseSipServletRequest request,
										 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside requestPostSend. Return CONTINUE " +
									session.getLogId());

		  return genRetContinue();
	 }

	 public int handleResponse(AseSipServletResponse response,
										AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside handleResponse. " +
									session.getLogId());

		  m_logger.error("ACK request does not have a response");
		  throw new
				AseSipMessageHandlerException("ACK does not have a response");
	 }
	 
	 public int recvResponse(AseSipServletResponse response,
									 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside recvResponse. " +
									session.getLogId());

		  m_logger.error("ACK request does not have a response");
		  throw new
				AseSipMessageHandlerException("ACK does not have a response");
	 }
	 
	 public int sendResponse(AseSipServletResponse response,
									 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside sendResponse. " +
									session.getLogId());

		  m_logger.error("ACK request does not have a response");
		  throw new
				AseSipMessageHandlerException("ACK does not have a response");
	 }
	 
	 public int responsePreSend(AseSipServletResponse response,
										 AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside responsePreSend." +
									session.getLogId());

		  m_logger.error("ACK request does not have a response");
		  throw new
				AseSipMessageHandlerException("ACK does not have a response");
	 }
	 
	 public int responsePostSend(AseSipServletResponse response,
										  AseSipSession session)
		  throws AseSipMessageHandlerException {

		  if (m_logger.isDebugEnabled())
				m_logger.debug("Inside responsePostSend. " +
									session.getLogId());

		  m_logger.error("ACK request does not have a response");
		  throw new
				AseSipMessageHandlerException("ACK does not have a response");
	 }
	 
	 private boolean isAckAllowed = false;
	 private boolean isAckAllowedChecked = false;
	 
	 /**
	  * The logger reference
	  */
	 transient private static Logger m_logger =
		  Logger.getLogger(AseSipAckMessageHandler.class);
}


/*
 * AsePseudoStackInterfaceLayer.java
 *
 * Created on Mar 9, 2005
 */
package com.baypackets.ase.sipconnector;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.sip.Rel100Exception;
import javax.servlet.sip.SipErrorEvent;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseContainer;
import com.baypackets.ase.common.AseEvent;
import com.baypackets.ase.common.AseMessage;
import com.baypackets.ase.common.exceptions.AseLockException;
import com.baypackets.ase.common.logging.LoggingCriteria;
import com.baypackets.ase.container.AseApplicationSession;
import com.baypackets.ase.container.AseIc;
import com.baypackets.ase.container.AseThreadData;
import com.baypackets.ase.container.sip.AseSipAppCompositionHandler;
import com.baypackets.ase.measurement.AseMeasurementUtil;
import com.baypackets.ase.util.Constants;

import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipMaxForwardsHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRequest;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipResponse;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipTransportType;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipViaHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipTimestampHeader;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserException;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserListenerException;

/**
 * The <code>AsePseudoStackInterfaceLayer</code> class provides SIP stack
 * behavior for SIP messages exchanges between SAS applications in application
 * path. It resides within <code>AseSipConnector</code>.
 *
 * @author Neeraj Jain
 */

public class AsePseudoStackInterfaceLayer
	implements AseStackInterface {

	/////////////////////////////// Attributes ////////////////////////////////

	private AseSipConnector				m_connector	= null;

	private AseSipSubscriptionManager	m_subscriptionMgr	= null;

	private AseConnectorSipFactory		m_factory	= null;

	private AseSipAppCompositionHandler	m_sach	= null;

	private ConcurrentHashMap						m_serverTxnMap = null;

	private ConcurrentHashMap						m_clientTxnMap = null;

	private static AsePsilMessageLoggingInterface	m_msgLogger = null;

	private static Logger m_l = Logger.getLogger(
								AsePseudoStackInterfaceLayer.class.getName());

	///////////////////////////////// Methods /////////////////////////////////

	/**
	 * Constructor. Creates basic stack objects and sets private attributes.
	 *
	 * @param connector SIP connector reference
	 */
	AsePseudoStackInterfaceLayer(	AseSipConnector				connector,
									AseSipSubscriptionManager	subMgr,
									AseConnectorSipFactory		factory) {
		if(m_l.isDebugEnabled()) m_l.debug("AsePseudoStackInterfaceLayer(AseSipConnector, AseSipSubscriptionManager, AseConnectorSipFactory): enter");

		m_connector			= connector;
		m_subscriptionMgr	= subMgr;
		m_factory			= factory;
		m_serverTxnMap		= new ConcurrentHashMap(1000,0.6f);
		m_clientTxnMap		= new ConcurrentHashMap(1000,0.6f);
		m_sach				= AseSipAppCompositionHandler.getInstance();

		if(m_l.isDebugEnabled()) m_l.debug("AsePseudoStackInterfaceLayer(AseSipConnector, AseSipSubscriptionManager, AseConnectorSipFactory): exit");
	}

	public void initialize(AseContainer container) {
		m_l.debug("initialize(AseContainer): called");

		// Set PSIL
		AsePseudoSipClientTxn.setPSIL(this);
		AsePseudoSipServerTxn.setPSIL(this);

		// Set SIP factory
		AsePseudoSipClientTxn.setFactory(m_factory);
		AsePseudoSipServerTxn.setFactory(m_factory);
	}

	public void start() {
		if(m_l.isDebugEnabled()) m_l.debug("start(): called");
	}

	public void shutdown() {
		if(m_l.isDebugEnabled()) m_l.debug("shutdown(): called");
	}

	public void processMessages() {
		if(m_l.isDebugEnabled()) m_l.debug("processMessages(): enter");

		Object obj = null;

		// Retrieve data from thread specific list one by one and process it
		// till the list is empty.
		while((obj = AseThreadData.get()) != null) {
			if(obj instanceof AseSipServletRequest) {
				// Request object
			if(m_l.isDebugEnabled())	m_l.debug("Retrieved a request from thread specific list.");

				AseSipServletRequest req = (AseSipServletRequest)obj;

				if(m_msgLogger != null) {
					m_msgLogger.logRequest(req.toString());
				}

				try {
					((AseSipSession)req.getPrevSession()).acquire();
				} catch(AseLockException exp) {
					m_l.fatal("SESSION LOCK ACQUIRE FAILURE", exp);

					try {
						((AseSipSession)req.getPrevSession()).release();
					} catch(AseLockException exp1) {
						m_l.fatal("SESSION LOCK RELEASE FAILURE", exp1);
					}

					if(m_l.isDebugEnabled()) m_l.debug("processMessages():exit");
					return;
				}

				try {
					_handleRequest(req);
				} catch(Throwable t) {
					m_l.error("In processMessages()", t);
				}

				try {
					((AseSipSession)req.getPrevSession()).release();
				} catch(AseLockException exp1) {
					m_l.fatal("SESSION LOCK RELEASE FAILURE", exp1);
				}
			} else if(obj instanceof AseSipServletResponse) {
				// Response object
				if(m_l.isDebugEnabled()) m_l.debug("Retrieved a response from thread specific list.");

				AseSipServletResponse resp = (AseSipServletResponse)obj;

				if(m_msgLogger != null) {
					m_msgLogger.logResponse(resp.toString());
				}

				try {
					((AseSipSession)resp.getPrevSession()).acquire();
				} catch(AseLockException exp) {
					m_l.fatal("SESSION LOCK ACQUIRE FAILURE", exp);

					try {
						((AseSipSession)resp.getPrevSession()).release();
					} catch(AseLockException exp1) {
						m_l.fatal("SESSION LOCK RELEASE FAILURE", exp1);
					}

				if(m_l.isDebugEnabled())	m_l.debug("processMessages():exit");
					return;
				}

				try {
					//Increment the counter for this incoming request
					AseMeasurementUtil.incrementLbResponse(resp.getDsResponse().getStatusCode());

					_handleResponse(resp);
				} catch(Throwable t) {
					m_l.error("In processMessages()", t);
				}

				try {
					((AseSipSession)resp.getPrevSession()).release();
				} catch(AseLockException exp1) {
					m_l.fatal("SESSION LOCK RELEASE FAILURE", exp1);
				}
			}
		}// while

		if(m_l.isDebugEnabled()) m_l.debug("processMessages(): exit");
	}

	public void handleRequest(AseSipServletRequest request) {
		_handleRequest(request);
	}

	public void handleResponse(AseSipServletResponse response) {
		_handleResponse(response);
	}

	public void sendRequest(AseSipServletRequest request) {
		_sendRequest(request);
	}

	public void sendResponse(AseSipServletResponse response) {
		_sendResponse(response);
	}

	public void handleTimeout(AseSipTransaction	transaction) {
		if(m_l.isDebugEnabled()) m_l.debug("handleTimeout(AseSipTransaction):enter");

		AseSipServletRequest request = transaction.getAseSipRequest();
		AseSipServletResponse response = transaction.getAseSipResponse();

		AseSipSession session = request.getAseSipSession();

		AseEvent aseEvt = null;

		// Check if it is an ACK timeout and server transaction timeout
		if( (response != null) && (response.getStatus() >= 200) ) {
			// increment the ACT timedout counter
			AseMeasurementUtil.counterLbAckTimedout.increment();

			// ACK timeout. Create AseEvent object with new SipErrorEvent.
			if(m_l.isInfoEnabled())
				m_l.info("ACK timed out for call id = " + request.getCallId() +
								", request method = " + request.getMethod() +
								", session id = " + session.getId());

			if(m_l.isDebugEnabled()) m_l.debug("Creating AseEvent with SipErrorEvent");
			aseEvt = new AseEvent(	session,
									Constants.EVENT_SIP_ACK_ERROR,
									new SipErrorEvent(request, response));
		} else {
			// Server transaction timeout. Create AseEvent object with request.
			if(m_l.isInfoEnabled())
				m_l.info("Server transaction timed out for call id = " +
								request.getCallId() +
								", request method = " + request.getMethod() +
								", session id = " + session.getId());

			if(m_l.isDebugEnabled()) m_l.debug("Creating AseEvent with request");
			aseEvt = new AseEvent(	session, 
									Constants.EVENT_SERVER_TXN_TIMEOUT,
									request);
		}

		try {
			session.acquire();
		} catch(AseLockException exp) {
			m_l.fatal("SESSION LOCK ACQUIRE FAILURE", exp);

			try {
				session.release();
			} catch(AseLockException exp1) {
				m_l.fatal("SESSION LOCK RELEASE FAILURE", exp1);
			}

			if(m_l.isDebugEnabled()) m_l.debug("handleTimeout(AseSipTransaction):exit");
			return;
		}

		// Sending AseEvent to session
		if(m_l.isDebugEnabled()) m_l.debug("Sending AseEvent to session");
		try {
			int i = session.recvEvent(aseEvt);
			switch(i) {
				case AseSipSession.NOOP:
					if(m_l.isDebugEnabled()) m_l.debug("SIP session return code: NOOP");

					// Do not send this event to container. No more
					// processing required, cleanup resources associated
					// what cleanup to do ??
				break;

				case AseSipSession.CONTINUE:
				if(m_l.isDebugEnabled())	m_l.debug("SIP session return code: CONTINUE");

					// Pass this event to container
					 if(m_l.isInfoEnabled()) m_l.info("Send timeout event to container");
					m_sach.handleEvent(aseEvt, (AseSipSession)aseEvt.getSource());
				break;
			}
		} catch(AseSessionInvalidException exp) {
			m_l.error("Invalid session for call id = " + request.getCallId() +
								", request method = " + request.getMethod() +
								", session id = " + session.getId());
		} catch (AseDialogInvalidException exp) {
			m_l.error("Invalid session for call id = " + request.getCallId() +
							 ", request method = " + request.getMethod() +
							 ", session id = " + session.getId());
		} finally {
			try {
				session.release();
			} catch(AseLockException exp) {
				m_l.fatal("SESSION LOCK RELEASE FAILURE", exp);
			}
		}

		if(m_l.isDebugEnabled()) m_l.debug("handleTimeout(AseSipTransaction):exit");
	}

	public void removeClientTxn(String key) {
		if(m_clientTxnMap.remove(key) == null) {
			m_l.error("removeClientTxn(String): No transaction found for : " + key);
		}
	}

	public void removeServerTxn(String key) {
		if(m_serverTxnMap.remove(key) == null) {
			m_l.error("removeServerTxn(String): No transaction found for : " + key);
		}
	}

	private void _handleRequest(AseSipServletRequest request) {
		if(m_l.isDebugEnabled()) m_l.debug("_handleRequest(AseSipServletRequest):enter");

		AseSipDialogId dialogId = request.getDialogId();
		DsSipRequest dsReq = request.getDsRequest();
		AsePseudoSipServerTxn txn = null;
		AseSipServletResponse resp = null;

		if(m_l.isDebugEnabled()) {
			m_l.debug("PSIL in request:");
			m_l.debug(dsReq);
		}

		//Increment the counter for this incoming request
		AseMeasurementUtil.incrementLbRequest(dsReq.getMethodID());

		// Enable or disable logging for the current thread based on
		// any criteria specified for SIP messages.
		LoggingCriteria.getInstance().check(request);

		// Get Via branch id
		String branch = AseSipViaHeaderHandler.getTopViaBranch(request).toString();

		if((dsReq.getMethodID() == DsSipConstants.ACK)
		|| (dsReq.getMethodID() == DsSipConstants.CANCEL)) {
			// Find the server transaction in map
			txn = (AsePseudoSipServerTxn)m_serverTxnMap.get(branch);

			if(dsReq.getMethodID() == DsSipConstants.CANCEL) {
				if(txn == null) {
					// Transaction not found

					// Create 481 and add into thread list
					resp = m_factory.createResponse(request, 481, null);

					resp.clearStackTxn();
					if(m_l.isDebugEnabled()) m_l.debug("Adding 481 to CANCEL in thread list");
					AseThreadData.add(resp);

				if(m_l.isDebugEnabled())	m_l.debug("_handleRequest(AseSipServletRequest):exit");
					return;
				} else {
					// Transaction found

					// Send CANCEL to transaction
					try {
						txn.recvRequest(request);
					} catch(AsePseudoTxnException e) {
						m_l.error("receiving CANCEL", e);

						if(m_l.isDebugEnabled()) m_l.debug("_handleRequest(AseSipServletRequest):exit");
						return;
					}

					// Create 200 and add into thread list
					resp = m_factory.createResponse(request, 200, null);
					try {
						txn.sendResponse(resp);
					} catch(AsePseudoTxnException e) {
						m_l.error("sending response", e);

					if(m_l.isDebugEnabled())	m_l.debug("_handleRequest(AseSipServletRequest):exit");
						return;
					}

					//bug# BPInd18392
					// Set prev session after taking it from txn
					resp.setPrevSession(txn.getSipSession());

					resp.clearStackTxn();
					if(m_l.isDebugEnabled()) m_l.debug("Adding 200 to CANCEL in thread list");
					AseThreadData.add(resp);
				}
			} else if(dsReq.getMethodID() == DsSipConstants.ACK) {
				if(txn != null) {
				if(m_l.isDebugEnabled())	m_l.debug("Sending ACK to transaction");

					try {
						txn.recvRequest(request);
					} catch(AsePseudoTxnException e) {
						m_l.error("receiving ACK", e);

					if(m_l.isDebugEnabled())	m_l.debug("_handleRequest(AseSipServletRequest):exit");
						return;
					}
				} else {
				if(m_l.isDebugEnabled())	m_l.debug("Transaction for ACK not found");
				}
			}
		} else {
			// Create server transaction object
			if(dsReq.getMethodID() == DsSipConstants.INVITE) {
				txn = new AsePseudoSipServerInviteTxn(request, branch);
			} else {
				txn = new AsePseudoSipServerTxn(request, branch);
			}

			// Add to server transaction map
			Object replaced = m_serverTxnMap.put(branch, txn);

			if(m_l.isDebugEnabled())
				m_l.debug("Created server transaction for branch=" + branch +
													" and added into map");
			if(replaced != null) {
				m_l.error("A server transaction with branch=" + branch +
											" got replaced in PSIL map!!!" );
			}
		}

		if(dsReq.getMethodID() != DsSipConstants.CANCEL
		&& dsReq.getMethodID() != DsSipConstants.ACK) {
			// Check if dialog id has To tag
			if(!dialogId.hasToTag()) {
				// Initial request
				if(m_l.isDebugEnabled()) m_l.debug("handleRequest(): dialogId does not have 'to' tag");

				// Dialog can be initiated, pass request to container
				if(m_l.isDebugEnabled())
					m_l.debug("Passing initial request [call id = " +
										request.getCallId() + "] to container");
				request.extractParamsFromRequestURI();
				request.setInitial();
				request.setInProgress();
			
				m_sach.handleRequest(request);

				txn.setSipSession(request.getAseSipSession());

				if(m_l.isDebugEnabled()) m_l.debug("_handleRequest(AseSipServletRequest):exit");
				return;
			}
		}

		AseSipSession session = null;

		if(dsReq.getMethodID() == DsSipConstants.CANCEL
		|| dsReq.getMethodID() == DsSipConstants.ACK) {
			session = request.getAseSipSession();
		}

		if(session == null) {
			// Get IC from the request
			AseIc ic = ((AseApplicationSession)request.getPrevSession().getApplicationSession()).getIc();
			// Get session from IC
			session = (AseSipSession)ic.getSession(request);
		}

		// If this is a NOTIFY, we may find the session in the subscription
		// manager.
 		if(session == null && dsReq.getMethodID() == DsSipConstants.NOTIFY) {
			session = m_subscriptionMgr.getSession(request);
		} // NOTIFY

		if(session == null) {
			// Session not found

			// Create 481 and add into thread list
			resp = m_factory.createResponse(request, 481, null);

			if(m_l.isDebugEnabled()) m_l.debug("Send 481 to server transaction");
			try {
				txn.sendResponse(resp);
			} catch(AsePseudoTxnException e) {
				m_l.error("sending response", e);

			if(m_l.isDebugEnabled())	m_l.debug("_handleRequest(AseSipServletRequest):exit");
				return;
			}
			resp.setPrevSession(request.getPrevSession());//abaxi
			resp.clearStackTxn();
			if(m_l.isDebugEnabled()) m_l.debug("Adding 481 response in thread list");
			AseThreadData.add(resp);

			if(m_l.isDebugEnabled()) m_l.debug("_handleRequest(AseSipServletRequest):exit");
			return;
		}

		// Associated session with transaction and request
		if(m_l.isDebugEnabled())
			m_l.debug("Session [id = " + session.getId()
				+ "] found, associating it with transaction & request");
		if(txn != null) {
			txn.setSipSession(session);
		}
		request.setAseSipSession(session);

		// Handing over request to AseSipSession and processing it further
		// depending on the return code.

		/***
		AseSipSession lockedSession = session;

		try {
			lockedSession.acquire();
		} catch(AseLockException exp) {
			m_l.fatal("SESSION LOCK ACQUIRE FAILURE", exp);

			m_l.debug("handleRequest(AseSipServletRequest):exit");
			return;
		}
		***/

		try {
			// If NOTIFY
			if(dsReq.getMethodID() == DsSipConstants.NOTIFY) {
				// If proxy, compare session
				if(session.getRole() == AseSipSession.ROLE_PROXY) {
					if(!session.isMatchingSession(request)) {
						// This is NOT a matching session
						// Derive from this session to create multiple dialog
						if(m_l.isDebugEnabled())
		 					m_l.debug("Create multiple dialog for NOTIFY, call id: "
		 											+ request.getCallId());

						// Use original session for derivation (just like that)
						session = m_factory.createSession(session);

						// Set session into request & txn
						txn.setSipSession(session);
						request.setAseSipSession(session);
					}
				} // if proxy
			} // if NOTIFY

			try {
				int i = session.recvRequest(request);
				switch(i) {
					case AseSipSession.NOOP:
						m_l.debug("SIP session return code: NOOP");

						// Do not send this request to container. No more
						// processing required, cleanup resources associated
						// what cleanup to do ??
					break;

					case AseSipSession.CONTINUE:
						if(m_l.isDebugEnabled()) m_l.debug("SIP session return code: CONTINUE");

						// Pass this request to container
						if(m_l.isDebugEnabled()) m_l.debug("Send subsequent request to container");
						m_sach.handleRequest(request);
					break;

					case AseSipSession.CANCEL_REQUEST:
						if(m_l.isDebugEnabled()) m_l.debug("SIP session return code: CANCEL_REQUEST");

						// Cancel original INVITE, create 487 and add into
						// thread list
						resp = m_factory.createResponse(
										txn.getAseSipRequest(), 487, null);

						if(m_l.isDebugEnabled()) m_l.debug("Send 487 to server transaction");
						try {
							txn.sendResponse(resp);
						} catch(AsePseudoTxnException e) {
							m_l.error("sending response", e);

							if(m_l.isDebugEnabled()) m_l.debug("_handleRequest(AseSipServletRequest):exit");
							return;
						}

						// Set current session as previous session of response
						resp.setPrevSession(session);
						// Set response session to null
						resp.setAseSipSession(null);

						resp.clearStackTxn();
						if(m_l.isDebugEnabled()) m_l.debug("Adding 487 response in thread list");
						AseThreadData.add(resp);

						// Pass this request to container
						if(m_l.isDebugEnabled()) m_l.debug("Send subsequent request to container");
						m_sach.handleRequest(request);
					break;

					case AseSipSession.OPTIONS_RESPONSE:
						if(m_l.isDebugEnabled()) m_l.debug("SIP session return code: OPTIONS_RESPONSE");

						// Send 200 for OPTIONS
						resp = m_factory.createResponse(request, 200, null);

						if(m_l.isDebugEnabled()) m_l.debug("Send 200 to server transaction");
						try {
							txn.sendResponse(resp);
						} catch(AsePseudoTxnException e) {
							m_l.error("sending response", e);

							if(m_l.isDebugEnabled()) m_l.debug("_handleRequest(AseSipServletRequest):exit");
							return;
						}

						// Set current session as previous session of response
						resp.setPrevSession(session);
						// Set response session to null
						resp.setAseSipSession(null);

						resp.clearStackTxn();
						m_l.debug("Adding 200 response in thread list");
						AseThreadData.add(resp);
					break;

					case AseSipSession.TOO_MANY_HOPS:
						if(m_l.isDebugEnabled()) m_l.debug("SIP session return code: TOO_MANY_HOPS");

						// Too many hops, send 483
						resp = m_factory.createResponse(request, 483, null);

						if(m_l.isDebugEnabled()) m_l.debug("Send 483 to server transaction");
						try {
							txn.sendResponse(resp);
						} catch(AsePseudoTxnException e) {
							m_l.error("sending response", e);

							if(m_l.isDebugEnabled()) m_l.debug("_handleRequest(AseSipServletRequest):exit");
							return;
						}

						// Set current session as previous session of response
						resp.setPrevSession(session);
						// Set response session to null
						resp.setAseSipSession(null);

						resp.clearStackTxn();
						m_l.debug("Adding 483 response in thread list");
						AseThreadData.add(resp);
					break;

					default:
						if(m_l.isEnabledFor(Level.ERROR))
							m_l.error("session.recvRequest() return code [" + i +
									" for call id [" + request.getCallId() +
														"] is not handled");
				}
			} catch(AseStrayMessageException exp) {
				m_l.error("call id = " + request.getCallId(), exp);
			} catch(AseCannotCancelException exp) {
				m_l.error("call id = " + request.getCallId(), exp);
			} catch(AseOutOfSequenceException exp) {
				m_l.error("call id = " + request.getCallId(), exp);

				if(dsReq.getMethodID() != DsSipConstants.ACK) {
					m_l.debug("Sending 400 for request received out of sequence");

					resp = m_factory.createResponse(request, 400, null);

					if(m_l.isDebugEnabled()) m_l.debug("Send 400 to server transaction");
					try {
						txn.sendResponse(resp);
					} catch(AsePseudoTxnException e) {
						m_l.error("sending response", e);

						if(m_l.isDebugEnabled()) m_l.debug("_handleRequest(AseSipServletRequest):exit");
						return;
					}

					// Set current session as previous session of response
					resp.setPrevSession(session);
					// Set response session to null
					resp.setAseSipSession(null);

					resp.clearStackTxn();
					if(m_l.isDebugEnabled()) m_l.debug("Adding 400 response in thread list");
					AseThreadData.add(resp);
				}
			} catch(IllegalStateException exp) {
				m_l.error("call id = " + request.getCallId(), exp);

				if(dsReq.getMethodID() != DsSipConstants.ACK) {
					if(m_l.isDebugEnabled()) m_l.debug("Sending 481 for request received in illegal session state");

					resp = m_factory.createResponse(request, 481, null);

					if(m_l.isDebugEnabled()) m_l.debug("Send 481 to server transaction");
					try {
						txn.sendResponse(resp);
					} catch(AsePseudoTxnException e) {
						m_l.error("sending response", e);

						if(m_l.isDebugEnabled()) m_l.debug("_handleRequest(AseSipServletRequest):exit");
						return;
					}

					// Set current session as previous session of response
					resp.setPrevSession(session);
					// Set response session to null
					resp.setAseSipSession(null);

					resp.clearStackTxn();
					if(m_l.isDebugEnabled()) m_l.debug("Adding 481 response in thread list");
					AseThreadData.add(resp);
				}
			} catch(AseSessionInvalidException exp) {
				m_l.error("call id = " + request.getCallId(), exp);

				if(dsReq.getMethodID() != DsSipConstants.ACK) {
					if(m_l.isDebugEnabled()) m_l.debug("Sending 481 for request received for an invalid session");

					resp = m_factory.createResponse(request, 481, null);

					if(m_l.isDebugEnabled()) m_l.debug("Send 481 to server transaction");
					try {
						txn.sendResponse(resp);
					} catch(AsePseudoTxnException e) {
						m_l.error("sending response", e);

						if(m_l.isDebugEnabled()) m_l.debug("_handleRequest(AseSipServletRequest):exit");
						return;
					}

					// Set current session as previous session of response
					resp.setPrevSession(session);
					// Set response session to null
					resp.setAseSipSession(null);

					resp.clearStackTxn();
					if(m_l.isDebugEnabled()) m_l.debug("Adding 481 response in thread list");
					AseThreadData.add(resp);
				}
			} catch(AseDialogInvalidException exp) {
				m_l.error("call id = " + request.getCallId(), exp);

				if(dsReq.getMethodID() != DsSipConstants.ACK) {
					if(m_l.isDebugEnabled()) m_l.debug("Sending 481 for request received in invalid dialog state");

					resp = m_factory.createResponse(request, 481, null);

					if(m_l.isDebugEnabled()) m_l.debug("Send 481 to server transaction");
					try {
						txn.sendResponse(resp);
					} catch(AsePseudoTxnException e) {
						m_l.error("sending response", e);

						if(m_l.isDebugEnabled()) m_l.debug("_handleRequest(AseSipServletRequest):exit");
						return;
					}

					// Set current session as previous session of response
					resp.setPrevSession(session);
					// Set response session to null
					resp.setAseSipSession(null);

					resp.clearStackTxn();
					if(m_l.isDebugEnabled()) m_l.debug("Adding 481 response in thread list");
					AseThreadData.add(resp);
				}
			} catch(AseSubscriptionInvalidException exp) {
				m_l.error("call id = " + request.getCallId(), exp);

				if(dsReq.getMethodID() != DsSipConstants.ACK) {
					if(m_l.isDebugEnabled()) m_l.debug("Sending 481 as subscription does not exist");

					resp = m_factory.createResponse(request, 481, null);

					if(m_l.isDebugEnabled()) m_l.debug("Send 481 to server transaction");
					try {
						txn.sendResponse(resp);
					} catch(AsePseudoTxnException e) {
						m_l.error("sending response", e);

						if(m_l.isDebugEnabled()) m_l.debug("_handleRequest(AseSipServletRequest):exit");
						return;
					}

					// Set current session as previous session of response
					resp.setPrevSession(session);
					// Set response session to null
					resp.setAseSipSession(null);

					resp.clearStackTxn();
					 if(m_l.isDebugEnabled())m_l.debug("Adding 481 response in thread list");
					AseThreadData.add(resp);
				}
			}
		} finally {
			/***
			try {
				lockedSession.release();
			} catch(AseLockException exp) {
				m_l.fatal("SESSION LOCK RELEASE FAILURE", exp);
			}
			***/
		}

		if(m_l.isDebugEnabled()) m_l.debug("_handleRequest(AseSipServletRequest):exit");
		return;
	}

	private void _handleResponse(AseSipServletResponse response) {
		if(m_l.isDebugEnabled()) m_l.debug("_handleResponse(AseSipServletResponse):enter");

		AsePseudoSipClientTxn txn = null;
		AseSipDialogId dialogId = response.getDialogId();
		DsSipResponse dsRes = response.getDsResponse();

		if(m_l.isDebugEnabled()) {
			m_l.debug("PSIL in response:");
			m_l.debug(dsRes);
		}
		
		// Enable or disable logging for the current thread based on any
		// criteria currently set on SIP messages.
		LoggingCriteria.getInstance().check(response);

		// Get top Via branch id
		String branch = AseSipViaHeaderHandler.getTopViaBranch(response).toString();

		if(m_l.isDebugEnabled())
			m_l.debug(response.getMethod() + " response with dialogId = " +
				dialogId.toString() + ", call id = " + response.getCallId());

		// Find the client transaction in map
		txn = (AsePseudoSipClientTxn)m_clientTxnMap.get(branch);

		if(txn == null) {
			// No transaction found, discard this response
			m_l.error("No client txn found [branch=" + branch +
				"]. Discarding response with dialogId = " +
				dialogId.toString() + ", call id = " + response.getCallId());

			if(m_l.isDebugEnabled()) m_l.debug("_handleResponse(AseSipServletResponse):exit");
			return;
		}

		// Pass response to txn
		try {
			txn.recvResponse(response);
		} catch(AsePseudoTxnException e) {
			m_l.error("receiving response", e);

			if(m_l.isDebugEnabled()) m_l.debug("_handleResponse(AseSipServletResponse):exit");
			return;
		}

		// Process any pending CANCEL with client transaction
		if(txn != null && dsRes.getMethodID() == DsSipConstants.INVITE) {
			if(m_l.isDebugEnabled()) m_l.debug("Going to process pending CANCEL, if any");
			AseSipServletRequest cancel = ((AsePseudoSipClientInviteTxn)txn).
															getCancelPending();
			if(cancel != null) {
				// Pass CANCEL to transaction for processing
				try {
					((AsePseudoSipClientInviteTxn)txn).cancel(cancel);
				} catch(AsePseudoTxnException e) {
					m_l.error("sending CANCEL", e);

					throw new IllegalStateException(e.toString());
				}

 				// Add pending CANCEL into thread list
				if(dsRes.getResponseClass() == 1) {
					AseSipServletRequest cc = m_factory.createRequest(cancel);
					cc.clearStackTxn();
					if(m_l.isDebugEnabled()) m_l.debug("Adding pending CANCEL in thread list");
					AseThreadData.add(cc);
				}

				// Unset pending CANCEL
				// Even if it was actually not sent because the response was final
				txn.setCancelPending(null);
			}
		}

		// Assuming that transaction would have associated the session
		// with response
		AseSipSession session = response.getAseSipSession();

		if(session == null) {
			m_l.error("Unexpectedly no associated session with response... discarding it.");

			if(m_l.isDebugEnabled()) m_l.debug("_handleResponse(AseSipServletResponse):exit");
			return;
		}

		// Handing over response to AseSipSession and processing it further
		// depending on the return code.
		boolean ackResponse		= false;
		boolean sendToContainer	= false;

		/***
		AseSipSession lockedSession = session;

		try {
			lockedSession.acquire();
		} catch(AseLockException exp) {
			m_l.fatal("SESSION LOCK ACQUIRE FAILURE", exp);

			m_l.debug("handleResponse(AseSipServletResponse):exit");
			return;
		}
		***/

		try {
			// Check for MFR
			if(!session.isMatchingSession(response)) {
				// This is not a matching session
				if(response.canCreateMultipleDialogs()){
					// Derive from this session to create multiple dialog
					if(m_l.isDebugEnabled())
					 	m_l.debug("Create multiple dialog. [call id = " +
					 		response.getCallId() + "]");

					// Use original session for derivation
					session = m_factory.createSession(session);

					// Set session into txn
					txn.setSipSession(session);

					// Set session into response
					response.setAseSipSession(session);
					session.resetDialogParameters(response);
				} else {
					// Response can't create multiple dialog, return here.
					if(m_l.isDebugEnabled())
				 		m_l.debug("Cannot create multiple dialog. [call id = " +
					 		response.getCallId() + "]");

					if(m_l.isDebugEnabled()) m_l.debug("_handleResponse(AseSipServletResponse):exit");
					return;
				}
			}

			try {
				int i = session.recvResponse(response);
				switch(i) {
					case AseSipSession.NOOP:
						if(m_l.isDebugEnabled())
							m_l.debug("Doing nothing for response, call id = " +
													response.getCallId());
					break;

					case AseSipSession.CONTINUE:
						// Pass this response to container
						sendToContainer = true;
					break;

					case AseSipSession.ACK_RESPONSE:
						// Send ACK for the response
						ackResponse = true;
						// Send the response to container
						sendToContainer = true;
					break;

					default:
						m_l.error("session.recvResponse() return code [" + i +
									"], call id [" + response.getCallId() +
									"], session id [" + session.getId() +
									"] is not handled");
				}
			} catch(AseStrayMessageException exp) {
				m_l.error("passing response [method = " + response.getMethod()
									+ ", call id = " + response.getCallId() +
									"] to default handler", exp);
				// Do nothing
			} catch(AseSessionInvalidException exp) {
				m_l.error("response [method = " + response.getMethod()
									+ ", call id = " + response.getCallId() +
									"] received for invalid session", exp);

				// Acknowledge if this is final response to INVITE
            	if(DsSipConstants.INVITE == response.getDsResponse().getMethodID()
            		&& (response.getStatus() >= 200)) {
                	ackResponse = true;
            	}
			} catch(AseDialogInvalidException exp) {
				m_l.error("response [method = " + response.getMethod()
								+ ", call id = " + response.getCallId() +
								"] received in invalid dialog state", exp);

				// Acknowledge if this is final response to INVITE
            	if(DsSipConstants.INVITE == response.getDsResponse().getMethodID()
            		&& (response.getStatus() >= 200)) {
                	ackResponse = true;
            	}
			} catch(IllegalStateException exp) {
				m_l.error("response [method = " + response.getMethod()
								+ ", call id = " + response.getCallId() +
								"] received in illegal state", exp);

				// Acknowledge if this is final response to INVITE
            	if(DsSipConstants.INVITE == response.getDsResponse().getMethodID()
            		&& (response.getStatus() >= 200)) {
                	ackResponse = true;
            	}
			} catch (Rel100Exception exp) {
				m_l.error("response [method = " + response.getMethod()
							+ ", call id = " + response.getCallId() +
							 "] is an unexpected response", exp);
				// Do nothing
			} catch (AseSubscriptionInvalidException e) {
				m_l.error("response [method = " + response.getMethod()
							+ ", call id = " + response.getCallId() +
							 "] with illegal subscription information", e);
				// Do nothing
			}

			// Acknowledge response, if required
			if(ackResponse) {
				if(m_l.isDebugEnabled())
					m_l.debug("Going to send ACK for response, call id = " +
														response.getCallId());

				// TBDNeeraj - Route set in this case has to be taken from orig INVITE
				AseSipServletRequest ack = m_factory.createAck(response);

				// Set current session as previous session of ACK
				ack.setPrevSession(session);
				// Set ack session to null
				ack.setAseSipSession(null);

				if(m_l.isDebugEnabled()) m_l.debug("Pass ACK to client transaction");
				boolean sendAck = true;
				if(ack.isNon2XXAck()) {
					// Only non-2xx response's ACKs should be sent to transactions
					try {
						txn.ack(ack);
					} catch(AsePseudoTxnException e) {
						m_l.error("Sending ACK", e);

						sendAck = false;
					}
				}

				if(sendAck) {
					ack.clearStackTxn();
					m_l.debug("Adding ACK in thread list");
					AseThreadData.add(ack);
				}
			}

			// Send to container, if required
			if(sendToContainer) {
				if(m_l.isDebugEnabled())
					m_l.debug("Sending response [call id = " +
							response.getCallId() + "] to container");
				m_sach.handleResponse(response);
			}
		} finally {
			/***
			try {
				lockedSession.release();
			} catch(AseLockException exp) {
				m_l.fatal("SESSION LOCK RELEASE FAILURE", exp);
			}
			***/
		}

		if(m_l.isDebugEnabled()) m_l.debug("_handleResponse(AseSipServletResponse):exit");
		return;
	}

	private void _sendRequest(AseSipServletRequest request) {
		if(m_l.isDebugEnabled()) m_l.debug("_sendRequest(AseSipServletRequest):enter");

		AsePseudoSipClientTxn txn = request.getPseudoClientTxn();
		DsSipViaHeader via = null;
		String branch = null;
		boolean addVia = true;
		boolean isProxy = false;

		DsSipRequest dsReq = request.getDsRequest();
		AseSipSession session = request.getAseSipSession();

		if(m_l.isDebugEnabled()) {
			m_l.debug("PSIL out request:");
			m_l.debug(dsReq);
		}

		if(session.getRole() == AseSipSession.ROLE_PROXY) {
			// PROXY only
			if(m_l.isDebugEnabled())
				m_l.debug("Proxy session id = " + session.getId()
						+ ", call id = " + request.getCallId());

			isProxy = true;

			if(dsReq.getMethodID() != DsSipConstants.CANCEL) {
				// Decrement/add Max-Forwards header
				DsSipMaxForwardsHeader maxForwardsHdr = null;
				try {
					maxForwardsHdr = (DsSipMaxForwardsHeader)
						dsReq.getHeaderValidate(DsSipConstants.MAX_FORWARDS);
				} catch(Exception exp) {
					m_l.error("Getting Max-forwards", exp);
				}

				if(maxForwardsHdr != null) {
					int maxFwdVal = maxForwardsHdr.getMaxForwards();
					maxForwardsHdr.setMaxForwards(--maxFwdVal);
					m_l.debug("Max-Forwards value decremented");
				} else {
					maxForwardsHdr = new DsSipMaxForwardsHeader(70);
					dsReq.addHeader(maxForwardsHdr, true, false);
					m_l.debug("Max-Forwards added with value = 70");
				}
			}

			addVia = false;
		} else if(dsReq.getMethodID() == DsSipConstants.CANCEL
			|| request.isNon2XXAck()) {
			// UAC only
			// Add Via on all requests other than CANCEL and non-2xx ACKs
			addVia = false;
		}

		if(addVia) {
			// Create new VIA header
			AseSipViaHeaderHandler.addViaHeader(request,
						new DsByteString(m_connector.getIPAddress()),
						m_connector.getPort(),
						DsSipTransportType.UDP,
						true);
		}// if addVia

		// Notify session for state updation
		session.requestPreSend(request);

		if(dsReq.getMethodID() == DsSipConstants.CANCEL) {
			// CANCEL handling
			if(!txn.isCancellable()) {
			 if(m_l.isInfoEnabled())	m_l.info("Setting pending CANCEL");
				txn.setCancelPending(request);

				// Notify session for state updation
				session.requestPostSend(request);

				if(m_l.isDebugEnabled()) m_l.debug("_sendRequest(AseSipServletRequest):exit");
				return;
			} else {
				// Send CANCEL to transaction
				try {
					txn.cancel(request);
				} catch(AsePseudoTxnException e) {
					m_l.error("sending CANCEL", e);

					throw new IllegalStateException(e.toString());
				}
			}
		} else if(dsReq.getMethodID() == DsSipConstants.ACK) {
			if(request.isNon2XXAck()) {
				// Send non-2xx ACK to transaction
				try {
					txn.ack(request);
				} catch(AsePseudoTxnException e) {
					m_l.error("sending ACK", e);

					throw new IllegalStateException(e.toString());
				}
			}
		} else {
			// Get branch id of top Via header
			branch = AseSipViaHeaderHandler.getTopViaBranch(request).toString();

			// Create client transaction object
			if(dsReq.getMethodID() == DsSipConstants.INVITE) {
				if(m_l.isDebugEnabled()) m_l.debug("Creating AsePseudoSipClientInviteTxn");
				txn = new AsePseudoSipClientInviteTxn(request, branch);
			} else {
				if(m_l.isDebugEnabled()) m_l.debug("Creating AsePseudoSipClientTxn");
				txn = new AsePseudoSipClientTxn(request, branch);
			}

			// Add new transaction into map
			Object replaced = m_clientTxnMap.put(branch, txn);

			if(m_l.isDebugEnabled())
				m_l.debug("Added client txn into map with branch : " + branch);

			if(replaced != null) {
				m_l.error("A client transaction with branch = " + branch +
										" got replaced in PSIL map!!!" );
			}

			// Set SIP session into transaction
			txn.setSipSession(session);

			// Set request into transaction
			txn.setAseSipRequest(request);

			// Start transaction
			try {
				txn.start();
			} catch(AsePseudoTxnException e) {
				m_l.error("sending request", e);

				throw new IllegalStateException(e.toString());
			}
		}

		// Clone the request and add new request into thread list
		AseSipServletRequest cr = m_factory.createRequest(request);

		// Set current session as previous session of request
		cr.setPrevSession(session);
		// Set request session to null
		cr.setAseSipSession(null);
		
		//Set the loopback original request
		cr.setLoopbackSourceMessage(request);
		cr.setLoopback(true);

		cr.clearStackTxn();

		if(((AseApplicationSession)session.getApplicationSession()).getIc()
			== AseThreadData.getCurrentIc()) {
			// Synchronous loopback message
			if(m_l.isDebugEnabled()) {
				m_l.debug("Adding " + dsReq.getMethodID() +
						" to thread list with call-id : " + request.getCallId());
			}

			AseThreadData.add(cr);
		} else {
			// Asynchronous loopback message
			if(m_l.isDebugEnabled()) {
				m_l.debug("Sending " + dsReq.getMethodID() +
						" to container with call-id : " + request.getCallId());
			}

			AseMessage msg = new AseMessage(cr);
			msg.setStatus(AseMessage.LOOPBACK_ASYNC);
			m_connector.sendToContainer(msg);
		}

		// Notify session for state updation
		session.requestPostSend(request);

		if(m_l.isDebugEnabled()) m_l.debug("_sendRequest(AseSipServletRequest):exit");
	}

	private void _sendResponse(AseSipServletResponse response) {
		if(m_l.isDebugEnabled()) m_l.debug("_sendResponse(AseSipServletResponse):enter");

		boolean isProxy = false;
		AseSipSession session = response.getAseSipSession();
		DsSipResponse dsResp = response.getDsResponse();
		AsePseudoSipServerTxn txn = ((AseSipServletRequest)response.getRequest()).getPseudoServerTxn();

		if(m_l.isDebugEnabled()) {
			m_l.debug("PSIL out response:");
			m_l.debug(dsResp);
		}

		// Following is done to add any delay in sending 100 response (ISC Reqmnt)
		if(dsResp.getStatusCode() == 100) {
			// Now set delay in response's Timestamp header
			DsSipTimestampHeader tsh = null;
			try {
				tsh = (DsSipTimestampHeader)dsResp.getHeaderValidate(DsSipResponse.TIMESTAMP);
			} catch(DsSipParserException exp) {
				m_l.error("Parsing Timestamp header", exp);
			} catch(DsSipParserListenerException exp) {
				m_l.error("Parsing Timestamp header", exp);
			}

			if(tsh != null) {
				long reqTime = ((AseSipServletRequest)response.getBaseRequest()).getTimestamp();
				float delay;
				if(reqTime > 0) {
					delay = System.currentTimeMillis() - reqTime;
					delay /= 1000.0; // convert delay to seconds
					if(delay > 0.0) {
						tsh.setDelay(delay);
					}
				}
			}
		}

		if(session.getRole() == AseSipSession.ROLE_PROXY) {
			if(m_l.isDebugEnabled()) m_l.debug("Proxy session");

			isProxy = true;

			// For responses to subsequent requests, remove top Via
			if(!response.getRequest().isInitial()) {
				// Remove top Via header, if it matches to SAS
				AseSipViaHeaderHandler.removeTopViaHeader(response);

				// If no more Via, drop it and return
				if(null == dsResp.getViaHeaders()) {
					if(m_l.isInfoEnabled()) m_l.info("No more Via header for proxy response");

					if(m_l.isDebugEnabled()) m_l.debug("_sendResponse(AseSipServletResponse):exit");
					return;
				}
			} // if(!response.getRequest().isInitial())
		} // if proxy

		// Notify session for state updation
		session.responsePreSend(response);

		try {
			txn.sendResponse(response);
		} catch(AsePseudoTxnException e) {
			m_l.error("sending response", e);
		
			throw new IllegalStateException(e.toString());
		}

		// Clone the response and add new response into thread list
		AseSipServletResponse cr = m_factory.createResponse(response);

		// Set current session as previous session of response
		cr.setPrevSession(session);
		// Set response session to null
		cr.setAseSipSession(null);

		cr.clearStackTxn();

		if(((AseApplicationSession)session.getApplicationSession()).getIc()
			== AseThreadData.getCurrentIc()) {
			// Synchronous loopback message
			if(m_l.isDebugEnabled()) {
				m_l.debug("Adding response for " + dsResp.getMethodID() +
						" to thread list with call-id : " + response.getCallId());
			}

			AseThreadData.add(cr);
		} else {
			// Asynchronous loopback message
			if(m_l.isDebugEnabled()) {
				m_l.debug("Adding response for " + dsResp.getMethodID() +
						" to container with call-id : " + response.getCallId());
			}

			AseMessage msg = new AseMessage(cr);
			msg.setStatus(AseMessage.LOOPBACK_ASYNC);
			m_connector.sendToContainer(msg);
		}

		// Notify session for state updation
		session.responsePostSend(response);

		m_l.debug("_sendResponse(AseSipServletResponse):exit");
	}

	public static void setMessageLoggingInterface(AsePsilMessageLoggingInterface msgLogger) {
		m_msgLogger = msgLogger;
	}
}

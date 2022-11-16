/**
 * Filename: AseSipAppCompositionHandler.java
 *
 * Created on Mar 7, 2005
 */
package com.baypackets.ase.container.sip;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseBaseConnector;
import com.baypackets.ase.common.AseEvent;
import com.baypackets.ase.common.AseEventListener;
import com.baypackets.ase.common.AseMessage;
import com.baypackets.ase.container.AseApplicationSession;
import com.baypackets.ase.container.AseBaseContainer;
import com.baypackets.ase.container.AseBaseRequest;
import com.baypackets.ase.container.AseBaseResponse;
import com.baypackets.ase.container.AseChainInfo;
import com.baypackets.ase.container.AseIc;
import com.baypackets.ase.container.AseProtocolSession;
import com.baypackets.ase.sipconnector.AseSipServletRequest;
import com.baypackets.ase.sipconnector.AseSipServletResponse;
import com.baypackets.ase.sipconnector.AseSipSession;
import com.baypackets.ase.sipconnector.AseNsepMessageHandler;
import com.baypackets.ase.sipconnector.AseSipSessionState;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.AseUtils;


import javax.servlet.sip.SipServletRequest;

/**
 * The <code>AseSipAppCompositionHandler</code> class is a singleton class
 * which performs all the application composition related processing. This
 * includes determining next destination for an outgoing request.
 *
 * @author Neeraj Jain
 */
public class AseSipAppCompositionHandler {

	///////////////////////////////// Attributes //////////////////////////////

	private static final String NO_APPLICATION_COMPOSITION =
													"NoApplicationComposition";
	private AseBaseConnector m_sipConnector = null;

	private AseBaseContainer m_container = null;

	private static boolean m_noApplicationComposition = false;

	private static AseSipAppCompositionHandler m_self = null;

	private static Logger m_l = Logger.getLogger(
								AseSipAppCompositionHandler.class.getName());

	////////////////////////////////// Methods ////////////////////////////////

	/**
	 * Private constructor to make this class singleton.
	 */
	private AseSipAppCompositionHandler() {
		m_l.debug("AseSipAppCompositionHandler() called");
	}

	/**
	 * This method returns instance of this class, if already instantiated.
	 * If no object exists, then it creates one and returns it.
	 *
	 * @return <code>AseSipAppCompositionHandler</code> instance
	 */
	public static AseSipAppCompositionHandler getInstance() {
		if(m_self == null) {
			m_self = new AseSipAppCompositionHandler();
			ConfigRepository rep = BaseContext.getConfigRepository();
			String nac = rep.getValue(NO_APPLICATION_COMPOSITION);
			if(nac != null && nac.equals(AseStrings.ONE)) {
				// Application composition is disabled
				m_noApplicationComposition = true;
				m_l.info("Application composition is disabled.");
			} else {
				m_l.info("Application composition is enabled.");
			}
		}

		return m_self;
	}

	/**
	 * This method associates an <code>AseBaseConnector</code> object with
	 * this SIP application composition handler.
	 *
	 * @param connector SIP connector reference
	 */
	public void setSipConnector(AseBaseConnector connector) {
		m_l.debug("setConnector(AseBaseConnector) called");
		m_sipConnector = connector;
	}

	public void setContainer(AseBaseContainer container) {
		m_l.debug("setConnector(AseBaseContainer) called");
		m_container = container;
	}

	/**
	 * This method is called by SIP session to send a request out from the
	 * application.
	 *
	 * @param req request object to be sent out.
	 */
	public void sendRequest(AseBaseRequest req) throws IOException {
		m_l.debug("sendRequest(AseBaseRequest): enter");

		boolean loopback = false;

		if(m_noApplicationComposition == false) {
			AseChainInfo chainInfo = ((AseProtocolSession)req.getProtocolSession()).getChainInfo();

			if (m_l.isDebugEnabled()) {
				m_l.debug("Chain info is " + chainInfo);
			}
			
			if(req.isInitial()) {
				// Initial request. Check with IC, if loopback is required?
				AseIc ic = ((AseApplicationSession)req.getProtocolSession().getApplicationSession()).getIc();
				loopback = ic.checkLoopback(req);

				// Set downstream chain flag
				chainInfo.setChainedDownstream(loopback);
				if(loopback) {
					m_l.debug("Setting request is chained downstream");
					req.setChainedDownstream();
				}
			} else {
				// Subsequent request.
				// Check direction
				
				int dir = ((AseSipSession)req.getProtocolSession()).
									checkDirection((AseSipServletRequest)req);

				// Check if loopback is required from chain info
				loopback = (dir == AseSipSession.DIR_UPSTREAM) ?
					chainInfo.isChainedDownstream():chainInfo.isChainedUpstream();
					
				if(!loopback && req.chainedDownstream()) {
					// This might be true in case of CANCEL
					m_l.debug("Request is loopback because of chained downstream");
					loopback = true;
				}
				
				/**
				 * change done for ACK loopback issue trnsaction is not found after looping back
				 */

//				if(req.getMethod().equals("ACK")||req.getMethod().equals("BYE")){
//					
//					if (m_l.isDebugEnabled()) {
//						m_l.debug("Not looping back ACK or BYE request ");
//					}
//					
//					loopback=false;
//				}

			}
		}
	
		AseMessage msg = null;
        boolean pMsg = false;
		if(AseUtils.getCallPrioritySupport() == 1)      {
			pMsg = AseNsepMessageHandler.getMessagePriority((AseSipServletRequest)req);
		}
		msg =  new AseMessage(req, pMsg);
		msg.setStatus(loopback ? AseMessage.LOOPBACK_SYNC : AseMessage.PROCESSED);
		m_sipConnector.handleMessage(msg);

		m_l.debug("sendRequest(AseBaseRequest): exit");
	}

	/**
	 * This method is called by SIP session to send a response out from the
	 * application.
	 *
	 * @param resp response object to be sent out.
	 */
	public void sendResponse(AseBaseResponse resp) throws IOException {
		m_l.debug("sendResponse(AseBaseResponse): enter");

		boolean loopback = false;

		if(m_noApplicationComposition == false) {
			AseChainInfo chainInfo = ((AseProtocolSession)resp.getProtocolSession()).getChainInfo();

			if (m_l.isDebugEnabled()) {
				m_l.debug("Chain info is " + chainInfo);
			}
			// Check direction
			int dir = ((AseSipSession)resp.getProtocolSession()).
								checkDirection((AseSipServletResponse)resp);

			// Check if loopback is required from chain info
			if(!resp.getBaseRequest().isInitial()) {
				loopback = (dir == AseSipSession.DIR_UPSTREAM) ?
					chainInfo.isChainedDownstream():chainInfo.isChainedUpstream();
			} else {
				loopback = (dir == AseSipSession.DIR_UPSTREAM) ?
					chainInfo.wasChainedDownstream():chainInfo.wasChainedUpstream();
			}
		}
		AseMessage msg = null;
		boolean pMsg = false;
		if(AseUtils.getCallPrioritySupport() == 1)      {
			pMsg = AseNsepMessageHandler.getMessagePriority((AseSipServletResponse)resp);
		}
		msg =  new AseMessage(resp, pMsg);
		msg.setStatus(loopback ? AseMessage.LOOPBACK_SYNC : AseMessage.PROCESSED);
		m_sipConnector.handleMessage(msg);

		m_l.debug("sendResponse(AseBaseResponse): exit");
	}

    public void handleRequest(AseBaseRequest request) {
		m_l.debug("handleRequest(AseBaseRequest): enter");

		// If initial request, update chain info
		if(request.isInitial()) {
			((AseProtocolSession)request.getProtocolSession(true)).getChainInfo().setChainedUpstream(true);
		}

		// Set loopback flag in request
		request.setLoopback(true);

		// Send request to container
		AseMessage msg = new AseMessage(request);
		msg.setStatus(AseMessage.LOOPBACK_SYNC);
		m_container.handleMessage(msg);

		m_l.debug("handleRequest(AseBaseRequest): exit");
	}

    public void handleResponse(AseBaseResponse response) {
		m_l.debug("handleResponse(AseBaseResponse): enter");

		// Send response to container
		AseMessage msg = new AseMessage(response);
		msg.setStatus(AseMessage.LOOPBACK_SYNC);
		m_container.handleMessage(msg);

		m_l.debug("handleResponse(AseBaseResponse): exit");
	}

	public void handleEvent(AseEvent event, AseEventListener listener) {
		m_l.debug("handleEvent(AseEvent): enter");

		// Send event to container
		AseMessage msg = new AseMessage(event, listener);
		msg.setStatus(AseMessage.LOOPBACK_SYNC);
		m_container.handleMessage(msg);

		m_l.debug("handleEvent(AseEvent): exit");
	}

	/**
	 * This method is called by <code>AseSipSession</code> when SIP dialog is
	 * established. It is given to <code>AseSipConnector</code> for addition
	 * into <code>AseDialogManager</code> if the SIP session is at edge of SIP
	 * application path and to <code>AseIc</code> if SIP session is in middle
	 * of SIP application path.
	 *
	 * @param session session object to be added
	 */
	public void addSession(AseProtocolSession session) {
		m_l.debug("SACH:addSession(AseProtocolSession): enter");

		if(m_noApplicationComposition == false) {
			AseChainInfo chinfo = session.getChainInfo();
			AseIc ic = ((AseApplicationSession)session.getApplicationSession()).getIc();

			if(!chinfo.isChainingReqd()) {
				// Chaining not required for this session
				if(chinfo.isChainedDownstream()) {
					// There should exist an app-chain beforehand
					AseProtocolSession currUSEdge = ic.getUpstreamEdge(session);

					if(!chinfo.isChainedUpstream()) {
						if(currUSEdge != null) {
							// This session is at edge of app-chain, add previous
							// session (current upstream edge) into dialog manager.
							m_sipConnector.addSession(currUSEdge);	

							// The previous session is no more chained at upstream
							currUSEdge.getChainInfo().setChainedUpstream(false);
						}
					}
				}
			} else if(chinfo.isChainableBothSides()) {
				// Chainable at both sides
				if(chinfo.isChainedDownstream() || chinfo.isChainedUpstream()) {
					// At least one side is chained
					ic.addSession(session);	
				}

				if(!chinfo.isChainedDownstream() || !chinfo.isChainedUpstream()) {
					// At least one side is not chained
					m_sipConnector.addSession(session);	
				}
			} else {
				// Chainable at one side
				if(chinfo.isChainedDownstream() || chinfo.isChainedUpstream()) {
					// Chained
					ic.addSession(session);	
				}

				if(!(chinfo.isChainedDownstream() || chinfo.isChainedUpstream())) {
					// Not chained
					m_sipConnector.addSession(session);	
				}
			}
		} else {
			// Application chaining is disabled
			m_sipConnector.addSession(session);	
		}

		m_l.debug("SACH:addSession(AseProtocolSession): exit");
	}

	/**
	 * This method is called by <code>AseSipSession.activate()</code> method.
	 */
	public void activateSession(AseProtocolSession session) {
		m_l.debug("SACH:activateSession(AseProtocolSession): enter");
		AseSipSession sipSession = null;
		if(m_noApplicationComposition == false) {
			AseChainInfo chinfo = session.getChainInfo();
			AseIc ic = ((AseApplicationSession)session.getApplicationSession()).getIc();

			if(!chinfo.isChainingReqd()) {
				m_l.debug("Chaining not required");
			} else if(chinfo.isChainableBothSides()) {
				// Chainable at both sides
				if(chinfo.getChainId() >= 0) {
					// At least one side is chained
					ic.activateSession(session);	
				}

				if(!chinfo.isChainedDownstream() || !chinfo.isChainedUpstream()) {
					//FT Handling Strategy Update: SAS failover can happen at the 
					//time when Sip Session State is INITIAL, and at that time
					//downstream and upstream dialog id is null. There by provided
					//the check to handle that scenario
					// At least one side is not chained
					if (session instanceof AseSipSession){
						sipSession = (AseSipSession) session; 
						if ((sipSession.getSessionState() == AseSipSessionState.STATE_EARLY)
								|| (sipSession.getSessionState() == AseSipSessionState.STATE_CONFIRMED))
							m_sipConnector.addSession(session);
					}
				}
			} else {
				// Chainable at one side
				if(chinfo.getChainId() >= 0) {
					// Chained
					ic.activateSession(session);	
				}

				if(!(chinfo.isChainedDownstream() || chinfo.isChainedUpstream())) {
					// Not chained
					if (session instanceof AseSipSession){
						sipSession = (AseSipSession) session; 
						if ((sipSession.getSessionState() == AseSipSessionState.STATE_EARLY)
								|| (sipSession.getSessionState() == AseSipSessionState.STATE_CONFIRMED))
							m_sipConnector.addSession(session);
					}
				}
			}
		} else {
			// Application chaining is disabled
			if (session instanceof AseSipSession){
				sipSession = (AseSipSession) session; 
				if ((sipSession.getSessionState() == AseSipSessionState.STATE_EARLY)
						|| (sipSession.getSessionState() == AseSipSessionState.STATE_CONFIRMED))
					m_sipConnector.addSession(session);
			}
		}

		m_l.debug("SACH:activateSession(AseProtocolSession): exit");
	}
}

/**
 * Filename: AseSipApplicationChain.java
 *
 * Created on Mar 7, 2005
 */
package com.baypackets.ase.container.sip;

import java.util.ArrayList;

import javax.servlet.sip.SipSession;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import com.baypackets.ase.container.AseChainInfo;
import com.baypackets.ase.container.AseProtocolSession;
import com.baypackets.ase.container.AseBaseRequest;

import com.baypackets.ase.spi.container.SasProtocolSession;

import com.baypackets.ase.sipconnector.AseSipSession;
import com.baypackets.ase.sipconnector.AseSipServletRequest;
import com.baypackets.ase.util.AseStrings;

/**
 * The <code>AseSipApplicationChain</code> class represents an application
 * chain. It contains all <code>AseSipSession</code> objects in chain in same
 * sequence.
 *
 * @author Neeraj Jain
 */
public class AseSipApplicationChain {

	///////////////////////////////// Attributes //////////////////////////////
	
	/**
	 * Base Q value for a chain.
	 */
	private static final int BASE_QVALUE = 2000;

	/**
	 * Delta Q value for a chain. Difference in Q values of successive
	 * applications in chain.
	 */
	private static int DELTA_QVALUE = 10;

	/**
	 * ID for this application chain
	 */
	private int id;

	/**
	 * Hash code of upstream SIP dialog id.
	 */
	private int m_upstreamHashCode = 0;

	/**
	 * Hash code of downstream SIP dialog id.
	 */
	private int m_downstreamHashCode = 0;

	/**
	 * Sorted list of protocol session in application chain in order of
	 * their Q value.
	 */
	private ArrayList m_chain = new ArrayList();

	private static Logger m_l = Logger.getLogger(
								AseSipApplicationChain.class.getName());

	////////////////////////////////// Methods ////////////////////////////////

	/**
	 * Constructors.
	 */
	public AseSipApplicationChain() {
		m_l.debug("AseSipApplicationChain(): called");
	}

	public AseSipApplicationChain(int id) {
		m_l.debug("AseSipApplicationChain(id): enter");
		this.id = id;
		m_l.debug("AseSipApplicationChain(id): exit");
	}

	public AseSipApplicationChain(AseProtocolSession session) {
		m_l.debug("AseSipApplicationChain(session): enter");

		// Add session
		addSession(session);

		m_l.debug("AseSipApplicationChain(session): exit");
	}

	public int getId(){
		return this.id;
	}

	public void setId(int id){
		this.id = id;
	}

	public void addSession(AseProtocolSession session) {
		m_l.debug("addSession(AseProtocolSession): enter");

		if(m_upstreamHashCode == 0) {
			m_upstreamHashCode =
							((AseSipSession)session).getUpstreamDialogId().hashCode();
			m_downstreamHashCode =
							((AseSipSession)session).getDownstreamDialogId().hashCode();
		}

		// Check if given session's dialog id matches?
		if(!isMatching(session)) {
			if(m_l.isDebugEnabled()){
				m_l.debug("session not match case");
				m_l.debug("addSession(AseProtocolSession): exit");
			}
			return;
		}

		AseChainInfo chainInfo = session.getChainInfo();

		// Set the Q value, if not already set
		int qValue = BASE_QVALUE;
		if(chainInfo.getQValue() == 0) {
			if(m_chain.size() != 0) {
				AseProtocolSession baseSession = (AseProtocolSession)m_chain.get(0);
				qValue = baseSession.getChainInfo().getQValue() + DELTA_QVALUE;
			}

			chainInfo.setQValue(qValue);
		}

		// Add it to the starting of the list
		m_chain.add(0, session);
		session.getChainInfo().setChainId(id);
		if(m_l.isDebugEnabled()) {
			m_l.debug("Added at start of chain : " + session +
										", Q value : " + qValue);
			m_l.debug(this.toString()); // print this chain
		}

		m_l.debug("addSession(AseProtocolSession): exit");
	}

	/**
	 * Uses only previous session stored in request to find next session in
	 * chain. This session is never null for requests arriving here.
	 */
	public AseProtocolSession getSession(AseBaseRequest request) {
		m_l.debug("getSession(AseBaseRequest): enter");

		AseProtocolSession retSession = null;

		if(m_l.isDebugEnabled()) {
			m_l.debug("Request's prev session : " + request.getPrevSession().getId() +
						"; dialog-id hash code : " +
						((AseSipServletRequest)request).getDialogId().hashCode());
			m_l.debug(this.toString()); // print this chain
		}

		if(m_chain.size() == 1) {
			// Only one session in chain, return it.
			retSession = (AseProtocolSession)m_chain.get(0);
			if(!retSession.getChainInfo().isChainingReqd()) {
				// Chaining not required for this session, return null
				retSession = null;
			} else {
				m_l.debug("Only one session in chain, returning same.");
			}
		} else {
			// Get the index of previous session
			int prevSessionIdx = m_chain.indexOf(request.getPrevSession());
			if(prevSessionIdx == -1) {
				m_l.error("Previous session for request not found in chain");
				m_l.debug("getSession(AseBaseRequest): exit");
				return null;
			}

			// Determine direction of request to get index of next session
			int index = -1;
			boolean isDownstream =
				((AseSipServletRequest)request).getDialogId().hashCode()
														== m_downstreamHashCode;
			index = isDownstream ? prevSessionIdx - 1 : prevSessionIdx + 1;

			// Get the next session in chain
			while(index >= 0 && index < m_chain.size()) {
				retSession = (AseProtocolSession)m_chain.get(index);
				if(retSession.getChainInfo().isChainingReqd()) {
					// Return the session indicated by index
					if(m_l.isDebugEnabled()) {
						m_l.debug("Found next session : " + retSession);
					}

					m_l.debug("getSession(AseBaseRequest): exit");
					return retSession;
				} else {
					// Chaining not required for this session, return next
					index = isDownstream ? index - 1 : index + 1;
				}
			}

			retSession = null;
			if(m_l.isDebugEnabled())
				m_l.debug("This is end of app chain; index = " + index);
		}

		m_l.debug("getSession(AseBaseRequest): exit");
		return retSession;
	}

	public void addSessionByQValue(AseProtocolSession session) {
		m_l.debug("addSessionByQValue(AseProtocolSession): enter");

		// This method will be only called on standby side while activating.
		// i.e., the chain will be formed using the qValue only on standby side.
		// This check is just to make sure, if the session was added to the
		// chain and then removed from the chain (due to the reason it is not
		// participating in chaining) on the active side. Since it was added
		// to the chain, it will have a qValue.
		// But it is replicated to the standby, So we shouldn't add this just
		// because of the qValue.
		AseChainInfo sessionChainInfo = session.getChainInfo(); 
		if(!sessionChainInfo.isChainingReqd()){
			if(m_l.isDebugEnabled()){
				m_l.debug("Not adding session : " +session);
			}

			m_l.debug("addSessionByQValue(AseProtocolSession): exit");
			return;
		}

		int index = m_chain.size();
		if(0 != m_chain.size()) {
			AseProtocolSession first = (AseProtocolSession)m_chain.get(0);
			AseProtocolSession last = (AseProtocolSession)m_chain.get(m_chain.size()-1);
			if(sessionChainInfo.getQValue()
										> first.getChainInfo().getQValue()) {
				index = 0;
			}
			else if(sessionChainInfo.getQValue()
										< last.getChainInfo().getQValue()) {
				// use default index    
			} else {
				// search for right position
				for(int i = m_chain.size(); i > 0; i--) {
					AseProtocolSession baseSession = (AseProtocolSession)m_chain.get(i-1);
					if(baseSession.getChainInfo().getQValue()
											> sessionChainInfo.getQValue()) {
						index = i;
						break ;
					}
				}// for
			}
		}

		m_chain.add(index, session);
		if (m_l.isDebugEnabled()) {
			m_l.debug(session.getId() + " with q = " +
			sessionChainInfo.getQValue() + " added in chain at index " + index);
		}

		m_l.debug("addSessionByQValue(AseProtocolSession): exit");
	}

	public void removeSession(AseProtocolSession session) {
		m_l.debug("removeSession(AseProtocolSession): enter");

		m_l.debug("Removing " + this.toString()); // print this chain

		// Remove the session from chain
		m_chain.remove(session);

		m_l.debug("removeSession(AseProtocolSession): exit");
	}

	public AseProtocolSession getUpstreamEdge() {
		return (AseProtocolSession)m_chain.get(0);
	}

	public int size() {
		m_l.debug("size(): called");
		return m_chain.size();
	}

	public boolean isMatching(AseProtocolSession session) {
		m_l.debug("isMatching(AseProtocolSession): enter");

		if(m_upstreamHashCode == 0) {
			m_l.debug("New chain");
			m_l.debug("isMatching(AseProtocolSession): exit");
			return true;
		}

		if(m_upstreamHashCode != ((AseSipSession)session).
								getUpstreamDialogId().hashCode()) {
			m_l.debug("Upstream dialog id do not match");

			m_l.debug("isMatching(AseProtocolSession): exit");
			return false;
		}

		if(m_downstreamHashCode != ((AseSipSession)session).
								getDownstreamDialogId().hashCode()) {
			m_l.debug("Downstream dialog id do not match");

			m_l.debug("isMatching(AseProtocolSession): exit");
			return false;
		}

		m_l.debug("isMatching(AseProtocolSession): exit");
		return true;
	}

	/**
	 * This method is used in FT take over
	 */
	public void activateSession(AseProtocolSession session) {
		m_l.debug("activateSession(AseProtocolSession): enter");

		if(m_upstreamHashCode == 0) {
			m_upstreamHashCode =
							((AseSipSession)session).getUpstreamDialogId().hashCode();
			m_downstreamHashCode =
							((AseSipSession)session).getDownstreamDialogId().hashCode();
		}

		// Check if given session's dialog id matches?
		if(!isMatching(session)) {
			m_l.debug("activateSession(AseProtocolSession): exit");
			return;
		}

		int qValue = session.getChainInfo().getQValue();
		int i = 0;

		// Get Q value and insert into chain in acsending order of it
		for(; i < m_chain.size(); ++i) {
			AseProtocolSession idxSession = (AseProtocolSession)m_chain.get(i);
			if(qValue > idxSession.getChainInfo().getQValue()) {
				break;
			}
		}

		m_chain.add(i, session);
		if(m_l.isDebugEnabled()) {
			m_l.debug("Added at " + i + " into chain : " + session +
										", Q value : " + qValue);
			m_l.debug(this.toString()); // print this chain
		}

		m_l.debug("activateSession(AseProtocolSession): exit");
	}

	/**
	* This method will be called from the IC everytime a protocol session is invalidated.
	* The Application Chain will check whether all the protocol sessions are INVALID.
        * When the last protocol session is invalidated, it will cleanup all the protocol sessions.
	**/
	public boolean cleanup(){
		for(int i=0; i<m_chain.size();i++){
			AseProtocolSession session = (AseProtocolSession)m_chain.get(i);
			if(session != null && session.getProtocolSessionState()==  SasProtocolSession.VALID)
				return false;
		}	
		for(int i=0; i<m_chain.size();i++){
			AseProtocolSession session = (AseProtocolSession)m_chain.get(i);
			if(session != null)
				session.cleanup();
		}
		m_chain.clear();
		return true;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("AppChain ");

		sb.append("[id:" +id+ " US:" + m_upstreamHashCode + ", DS:" + m_downstreamHashCode + "] ==> ");

		for(int i = 0; i < m_chain.size(); ++i) {
			AseProtocolSession session = (AseProtocolSession)m_chain.get(i);
			sb.append(i + AseStrings.COLON + session.getId() + AseStrings.SEMI_COLON);
		}

		return sb.toString();
	}
}

/*
 * Created on Feb 14, 2005
 * 
 */
package com.baypackets.ase.sipconnector;

import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Iterator;

import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserException;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserListenerException;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;

/**
 * @author BayPackets
 *
 */
public class AsePseudoSipClientTxn implements AseSipTransaction {

    /**
     * Creates a new Pseudo Client Txn Object
     */
    public AsePseudoSipClientTxn(AseSipServletRequest req, String key) {
        m_sipServletRequest = req;
		m_key = new String(key);
        m_refStateTable = new AsePseudoSipClientTxnStateTable(this);
    }
    
	public static void setPSIL(AsePseudoStackInterfaceLayer psil) {
		m_psil = psil;
	}

	public static void setFactory(AseConnectorSipFactory factory) {
		m_factory = factory;
	}

	// Removes this transaction from map
	public void remove() {
		if (m_logger.isDebugEnabled()) m_logger.debug("Removing client transaction [" + m_key + "] from map");
		m_psil.removeClientTxn(m_key);
	}

    public void sendRequest(AseSipServletRequest req) 
                throws AsePseudoTxnException{
    }

    public void ack(AseSipServletRequest req) 
                throws AsePseudoTxnException{
	 	 	req.setPseudoClientTxn(this);
           throw new AsePseudoTxnException(m_fstrAckExp);             
     }

    public void cancel(AseSipServletRequest req) 
                throws AsePseudoTxnException{
	 	 	req.setPseudoClientTxn(this);
             throw new  AsePseudoTxnException(m_fstrCancelExp);                            
     }
     
     public void start() throws AsePseudoTxnException{
        if (m_logger.isDebugEnabled()) m_logger.debug("start():enter");

	 	 m_sipServletRequest.setPseudoClientTxn(this);
         m_refStateTable.
             setSmType(AsePseudoSipClientTxnStateTable.NON_INVITE_TXN);
		 m_refSipSessionList.add(m_sipServletRequest.getAseSipSession());
         m_refStateTable.start();     

        if (m_logger.isDebugEnabled()) m_logger.debug("start():exit");
     }
     
     public void recvResponse(AseSipServletResponse resp) 
                throws  AsePseudoTxnException {
        int respClass = resp.getDsResponse().getResponseClass();
        if(AsePseudoSipClientTxnStateTable.ERROR ==
			m_refStateTable.getNextState(respClass)) {
            throw new AsePseudoTxnException(m_fstrSMExp);
        }
		m_sipServletResponse = resp;
		m_refStateTable.setCancellable();
		resp.setPseudoClientTxn(this);

		resp.setAseSipSession(this.getSipSession(resp));
		resp.setRequest(m_sipServletRequest);
     }
     
	 // Create a 408 response and send to PSIL
	 public void timeout() {
	 	if (m_logger.isDebugEnabled()) m_logger.debug("timeout(): enter");

	 	// Create 408 response to original request
		AseSipServletResponse tmoutResp = m_factory.createResponse(
													m_sipServletRequest,
													408,
													"Transaction Timeout");
		tmoutResp.setSource(AseSipConstants.SRC_ASE);

		// If To-tag was previously generated, add it on 408 response
		if(tmoutResp.getDsResponse().getToTag() == null) {
			DsByteString _toTag = m_sipServletResponse.getDsResponse().getToTag();
			if(_toTag != null) {
				try {
					tmoutResp.getDsResponse().getToHeaderValidate().setTag(_toTag);
				} catch(DsSipParserException exp) {
					m_logger.error("Getting To-header of timeout response", exp);
				} catch(DsSipParserListenerException exp) {
					m_logger.error("Getting To-header of timeout response", exp);
				}
			}
		}

		m_sipServletResponse = tmoutResp;

		// Send it to PSIL
		m_psil.handleResponse(tmoutResp);

		// Process any messages in thread list too
		m_psil.processMessages();

	 	if (m_logger.isDebugEnabled()) m_logger.debug("timeout(): exit");
	 }
     
    /* 
     * @see com.baypackets.ase.sipconnector.AseSipTransaction#getAseSipRequest()
     */
    public AseSipServletRequest getAseSipRequest() {
        return m_sipServletRequest;
    }

    /* 
     * @see com.baypackets.ase.sipconnector.AseSipTransaction#getAseSipResponse()
     */
    public AseSipServletResponse getAseSipResponse() {
        return m_sipServletResponse;
    }

    /* 
     * @see com.baypackets.ase.sipconnector.AseSipTransaction#getSipSession()
     */
    public AseSipSession getSipSession() {
		// Return first session in list
        Iterator iter = m_refSipSessionList.iterator();
		return (AseSipSession)iter.next();
    }

    /* 
     * @see com.baypackets.ase.sipconnector.AseSipTransaction#isCancellable()
     */
    public boolean isCancellable() {
        // TODO Auto-generated method stub
        // if cancel object is not null then true....
        return false;
    }

    /* 
     * @see com.baypackets.ase.sipconnector.AseSipTransaction#isDefaultProxy()
     */
    public boolean isDefaultProxy() {
        // TODO Auto-generated method stub
        return false;
    }

    /* 
     * @see com.baypackets.ase.sipconnector.AseSipTransaction#processPendingCancel(int)
     */
    public void processPendingCancel(int responseCode) {
        // TODO Auto-generated method stub
        
    }

    /* 
     * @see com.baypackets.ase.sipconnector.AseSipTransaction#setAseSipRequest(com.baypackets.ase.sipconnector.AseSipServletRequest)
     */
    public void setAseSipRequest(AseSipServletRequest request) {
        m_sipServletRequest = request;
    }

    /* 
     * @see com.baypackets.ase.sipconnector.AseSipTransaction#setAseSipResponse(com.baypackets.ase.sipconnector.AseSipServletResponse)
     */
    public void setAseSipResponse(AseSipServletResponse response) {
        m_sipServletResponse = response;
    }

    /* 
     * @see com.baypackets.ase.sipconnector.AseSipTransaction#setCancellable()
     */
    public void setCancellable() {
        // TODO Auto-generated method stub

    }

    /* 
     * @see com.baypackets.ase.sipconnector.AseSipTransaction#setCancelPending(com.baypackets.ase.sipconnector.AseSipServletRequest)
     */
    public void setCancelPending(AseSipServletRequest cancel) {
        // TODO Auto-generated method stub

    }

    /* 
     * @see com.baypackets.ase.sipconnector.AseSipTransaction#setDefaultProxy()
     */
    public void setDefaultProxy() {
        // TODO Auto-generated method stub

    }

    /* 
     * @see com.baypackets.ase.sipconnector.AseSipTransaction#setSipSession(com.baypackets.ase.sipconnector.AseSipSession)
     */
    public void setSipSession(AseSipSession session) {

		m_refSipSessionList.add(session);
    }
    
	public void setProxyServerMode(boolean proxy) {
		m_refStateTable.setProxyServerMode(proxy);	
	}

	public boolean getProxyServerMode() {
		return m_refStateTable.getProxyServerMode();
	}

    protected AseSipSession getSipSession(AseSipServletMessage message) {
		Iterator iter = m_refSipSessionList.iterator();
		while(iter.hasNext()) {
			AseSipSession session = (AseSipSession)iter.next();
			if(session.isMatchingSession(message)) {
				return session;
			}
		}

		// No matching session found, return first session in list
        return (AseSipSession)m_refSipSessionList.iterator().next();
    }

	public void setRequestFlag() {
        m_reqFlag = true;
    }

    public boolean getRequestFlag() {
        return m_reqFlag;
    }


	private boolean                 m_reqFlag       = false;
	protected String m_key = null;
    protected AseSipServletRequest m_sipServletRequest = null;
    protected AseSipServletResponse m_sipServletResponse = null;
    protected HashSet m_refSipSessionList = new HashSet();
    protected AsePseudoSipClientTxnStateTable m_refStateTable = null;

	private static AsePseudoStackInterfaceLayer m_psil = null;
	private static AseConnectorSipFactory m_factory = null;
    protected static final String m_fstrSMExp = "Invalid State";
    private static final String m_fstrAckExp = "Cannot call ACK";
    private static final String m_fstrCancelExp = "Should not call Cancel";
    private static Logger m_logger = Logger.getLogger(AsePseudoSipClientTxn.class);
}

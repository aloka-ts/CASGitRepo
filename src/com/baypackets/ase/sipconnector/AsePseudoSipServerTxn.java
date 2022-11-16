/*
 * Created on Feb 15, 2005
 * 
 */
package com.baypackets.ase.sipconnector;

import org.apache.log4j.Logger;

import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;

/**
 * @author BayPackets
 *
 */
public class AsePseudoSipServerTxn implements AseSipTransaction {

	public AsePseudoSipServerTxn() {
	}

    /**
     * @param req
     */
    public AsePseudoSipServerTxn(AseSipServletRequest req, String key) {
        m_refOrigRequest = req;
		m_key = new String(key);
        m_refStateTable = new AsePseudoSipServerTxnStateTable(this);
        m_refStateTable.
            setSmType(AsePseudoSipServerTxnStateTable.NON_INVITE_TXN);
        m_iState = m_refStateTable.getState();             
		req.setPseudoServerTxn(this);
    }
    
	public static void setPSIL(AsePseudoStackInterfaceLayer psil) {
		m_psil = psil;
	}

	public static void setFactory(AseConnectorSipFactory factory) {
		m_factory = factory;
	}

	// Removes this transaction from map
	public void remove() {
		if(m_logger.isDebugEnabled())
			m_logger.debug("Removing server transaction [" + m_key + "] from map");
		m_psil.removeServerTxn(m_key);
	}

    /**
     * @param resp
     * @throws AsePseudoTxnException
     * This method checks on the SM and throws and exception on State
     * Error...
     */
    public void sendResponse(AseSipServletResponse resp) 
                throws AsePseudoTxnException {
        //  can be used to send 1xx or final responses...
		//if(resp.getRequest().isInitial()) {
        	m_respClass = resp.getDsResponse().getResponseClass();
        	m_iState = m_refStateTable.getNextState(m_respClass);
        	m_refStateTable.setResponse(resp);
        	if(m_refStateTable.ERROR == m_iState) {
            	throw new AsePseudoTxnException(m_fstrInvalidState);
       	}
		//}
    }
    
    /**
     * @param req
     * @throws AsePseudoTxnException
     */
    public void recvRequest(AseSipServletRequest req) 
                throws AsePseudoTxnException {
    }
    
	/**
	 * Retransmit 2xx response to INVITE
	 */
	public void retransmit2xx() {
		m_logger.error("2xx retransmission in non-INVITE transaction!!!");
	}

	/**
 	 * ACK has timed out
 	 */
	public void ackTimeout() {
		m_logger.error("ACK timeout in non-INVITE transaction!!!");
	}

	/**
	 * Timeout as no response was sent.
	 */
	public void txnTimeout() {
		m_psil.handleTimeout(this);

		// Process any messages in thread list too
		m_psil.processMessages();
	}

    /* 
     * @see com.baypackets.ase.sipconnector.AseSipTransaction#getAseSipRequest()
     */
    public AseSipServletRequest getAseSipRequest() {
        // Not required for Server Txn
        return m_refOrigRequest;
    }

    /* 
     * @see com.baypackets.ase.sipconnector.AseSipTransaction#getAseSipResponse()
     */
    public AseSipServletResponse getAseSipResponse() {
        // TODO Auto-generated method stub
        return m_refStateTable.getResponse();
    }

    /* 
     * @see com.baypackets.ase.sipconnector.AseSipTransaction#getSipSession()
     */
    public AseSipSession getSipSession() {
        // TODO Auto-generated method stub
        return m_refSipSession;
    }

    /* 
     * @see com.baypackets.ase.sipconnector.AseSipTransaction#isCancellable()
     */
    public boolean isCancellable() {
        // Not to be used in Server Txn
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
        // Not to be used in Server Txn

    }

    /* 
     * @see com.baypackets.ase.sipconnector.AseSipTransaction#setAseSipResponse(com.baypackets.ase.sipconnector.AseSipServletResponse)
     */
    public void setAseSipResponse(AseSipServletResponse response) {
        // TODO Auto-generated method stub
        m_refStateTable.setResponse(response);
    }

    /* 
     * @see com.baypackets.ase.sipconnector.AseSipTransaction#setCancellable()
     */
    public void setCancellable() {
        // not to be used in ServerTxn

    }

    /* 
     * @see com.baypackets.ase.sipconnector.AseSipTransaction#setCancelPending(com.baypackets.ase.sipconnector.AseSipServletRequest)
     */
    public void setCancelPending(AseSipServletRequest cancel) {
        // Not to used in Server Txn

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
        // TODO Auto-generated method stub
        m_refSipSession = session;
    }

	public void setProxyServerMode(boolean proxy) {
		m_refStateTable.setProxyServerMode(proxy);
	}

	public boolean getProxyServerMode() {
		return m_refStateTable.getProxyServerMode();
	}
	public void setRequestFlag() {
        m_reqFlag = true;
    }

    public boolean getRequestFlag() {
        return m_reqFlag;
    }

	private boolean                 m_reqFlag       = false;
	protected static AsePseudoStackInterfaceLayer m_psil = null;
	protected static AseConnectorSipFactory m_factory = null;
    protected AsePseudoSipServerTxnStateTable   m_refStateTable = null;
    protected AseSipSession m_refSipSession;
	protected String m_key = null;
    protected int m_iState;
	protected int m_respClass;
    final static String m_fstrInvalidState = "Invalid State";    
    protected AseSipServletRequest m_refOrigRequest;
    private static Logger m_logger =
         Logger.getLogger(AsePseudoSipServerTxn.class.getName());
}

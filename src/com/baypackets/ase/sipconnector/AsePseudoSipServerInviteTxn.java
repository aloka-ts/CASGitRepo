/*
 * Created on Feb 16, 2005
 * 
 */
package com.baypackets.ase.sipconnector;

import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import org.apache.log4j.Logger;


/**
 * @author BayPackets
 *
 */
public class AsePseudoSipServerInviteTxn extends AsePseudoSipServerTxn {

    /**
     * @param req
     */
    public AsePseudoSipServerInviteTxn(AseSipServletRequest req, String key) {
        m_refOrigRequest = req;
		m_key = new String(key);
        m_refStateTable = new AsePseudoSipServerTxnStateTable(this);
        m_refStateTable.
            setSmType(AsePseudoSipServerTxnStateTable.INVITE_TXN);
		req.setPseudoServerTxn(this);
    }
    
    /* 
     * @see com.baypackets.ase.sipconnector.AsePseudoSipServerTxn#recvRequest(com.baypackets.ase.sipconnector.AseSipServletRequest)
     * For an INVITE request check for ACK message for State Transition
     * check for Cancel to cancel this Txn
     */
    public void recvRequest(AseSipServletRequest req) 
                throws AsePseudoTxnException {
        int reqId = req.getDsRequest().getMethodID();
        
        if(DsSipConstants.ACK == reqId) {
            m_refStateTable.setAckRecvd(true);
			if(m_respClass > 2) req.setNon2XXAck();
        }

		// Set transaction in this request
		req.setPseudoServerTxn(this);

		// Set session in this request from original INVITE
		req.setAseSipSession(m_refOrigRequest.getAseSipSession());
    }
    
	public void retransmit2xx() {
		if (m_logger.isDebugEnabled()) m_logger.debug("retransmit2xx(): enter");

		AseSipServletResponse origResp = m_refStateTable.getResponse();

		// Clone the response and add new response into thread list
		AseSipServletResponse cr = m_factory.createResponse(origResp);

		// Set current session as previous session of response
		cr.setPrevSession(origResp.getAseSipSession());
		// Set response session to null
		cr.setAseSipSession(null);

		cr.clearStackTxn();

		// Send 2xx retransmission to PSIL
		m_psil.handleResponse(cr);

		// Process any messages in thread list too
		m_psil.processMessages();

		if (m_logger.isDebugEnabled()) m_logger.debug("retransmit2xx(): exit");
	}

	public void ackTimeout() {
		m_psil.handleTimeout(this);

		// Process any messages in thread list too
		m_psil.processMessages();
	}

    private static Logger m_logger =
         Logger.getLogger(AsePseudoSipServerInviteTxn.class.getName());
}

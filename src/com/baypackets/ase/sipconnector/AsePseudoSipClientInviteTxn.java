/*
 * Created on Feb 14, 2005
 * 
 */
package com.baypackets.ase.sipconnector;

import org.apache.log4j.Logger;

import com.dynamicsoft.DsLibs.DsSipObject.DsSipViaHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserException;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserListenerException;

/**
 * @author BayPackets
 *
 */
public class AsePseudoSipClientInviteTxn 
    extends AsePseudoSipClientTxn {

    /**
     * Creates a new Pseudo Client Invite Txn Object
     */
    public AsePseudoSipClientInviteTxn(AseSipServletRequest req, String key) {
        super(req, key);

		// Store top Via header from request
		try {
			DsSipViaHeader vh = (DsSipViaHeader)req.getDsRequest().
							getHeaderValidate(DsSipConstants.VIA);
			if(vh != null) {
				m_viaHdr = (DsSipViaHeader)vh.clone();
				if(m_logger.isDebugEnabled()) {
					m_logger.debug("Stored via header : " + m_viaHdr.toString());
				}
			}
		} catch(DsSipParserException exp) {
			m_logger.error("Retrieving VIA", exp);
		} catch(DsSipParserListenerException exp) {
			m_logger.error("Retrieving VIA", exp);
		}
    }

    public void sendRequest(AseSipServletRequest req) 
                throws AsePseudoTxnException{
//    Not required....                         
     }

    public void ack(AseSipServletRequest req)
                throws AsePseudoTxnException {
		 req.setAseSipSession(this.getSipSession(req));

		 // Put Via from INVITE on to non-2xx ACK
		 if(req.isNon2XXAck()) {
		 	if(m_viaHdr != null) {
		 		req.getDsRequest().addHeader(m_viaHdr, true, true);
				if(m_logger.isDebugEnabled()) {
					m_logger.debug("Added via header : " + m_viaHdr.toString());
				}
		 	}
		 }

         m_refStateTable.setAckReq(req);
		 
		 // If response was due to transaction timeout, prevent sending ACK
		 if(isRespTimeout) {
		 	throw new AsePseudoTxnException("Transaction Timedout");
		 }
     }
     
//   TBD use PseudoSil to send request
    public void cancel(AseSipServletRequest req) 
                throws AsePseudoTxnException{
		 // Put Via from INVITE on to CANCEL
		 if(m_viaHdr != null) {
		 	req.getDsRequest().addHeader(m_viaHdr, true, true);
		 }
         m_refStateTable.setCancelReq(req);                                         
		 req.setAseSipSession(this.getSipSession(req));
     }
     
     public void start() throws AsePseudoTxnException{
	 	 m_sipServletRequest.setPseudoClientTxn(this);
         m_refStateTable.
             setSmType(AsePseudoSipClientTxnStateTable.INVITE_TXN);
		 m_refSipSessionList.add(m_sipServletRequest.getAseSipSession());
         m_refStateTable.start();
     }
     
	 public void timeout() {
		isRespTimeout = true;
		// Dereference pending CANCEL, if any
        m_refStateTable.setCancelReq(null);
	 	super.timeout();
	}

    /**
     * All Cancel operations to be implemented by the Invite Txn...
     */

    /* 
     * @see com.baypackets.ase.sipconnector.AseSipTransaction#isCancellable()
     */
    public boolean isCancellable() {
        return m_refStateTable.isCancellable();
    }

    /* 
     * @see com.baypackets.ase.sipconnector.AseSipTransaction#processPendingCancel(int)
     */
    public void processPendingCancel(int responseCode) {
        // TODO Auto-generated method stub
//      should we call actual cancel processing code ... Only for Invite
        
    }

    /* 
     * @see com.baypackets.ase.sipconnector.AseSipTransaction#setCancellable()
     */
    public void setCancellable() {
        // Nothing to do setCancelPending should be used by client code.
        m_refStateTable.setCancellable();
    }

    /* 
     * @see com.baypackets.ase.sipconnector.AseSipTransaction#setCancelPending(com.baypackets.ase.sipconnector.AseSipServletRequest)
     */
    public void setCancelPending(AseSipServletRequest cancel) {
        // Cancel operation is set in the SM
        m_refStateTable.setCancelReq(cancel);
    }
    
    public AseSipServletRequest getCancelPending() {
        return m_refStateTable.getCancelRequest();
    }
    
	private DsSipViaHeader m_viaHdr = null;
	private boolean isRespTimeout = false;
	private static Logger m_logger = Logger.getLogger(AsePseudoSipClientInviteTxn.class);
}

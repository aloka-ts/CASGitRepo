/**
 * AseSipInvitationHandler.java
 */

package com.baypackets.ase.sipconnector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
import java.io.Externalizable;

/**
 * This keeps track of potential INVITE based dialogs
 */

class AseSipInvitationHandler implements AseSipDialogReferenceManager,
													  AseSipInvitationHandlerInterface,
													  Serializable {
	private static final long serialVersionUID = -384885831890984370L;
	/**
	  * Check if the dialog is still referenced.
	  * It is referenced if m_invitationCount is non-zero
	  */
	 public boolean isDialogReferenced() {
		  if (0 == m_invitationCount)
				return false;
		  return true;
	 }
	 
	 /**
	  * Increment the m_invitationCount counter
	  */
	 public void setInvitation() {
		  m_invitationCount ++;

		  if (m_logger.isDebugEnabled())
				m_logger.debug("setInvitation. New m_invitationCount = " +
									m_invitationCount);
	 }
	 
	 /**
	  * Decrement the m_invitationCount counter
	  */
	 public void unsetInvitation() {
		  m_invitationCount --;

		  if (m_logger.isDebugEnabled())
				m_logger.debug("unsetInvitation. New m_invitationCount = " +
									m_invitationCount);
	 }
	 
	 /**
	  * Reset the m_invitationCount counter to zero. Typically called for BYE
	  */
	 public void resetInvitation() {
		  m_invitationCount = 0;

		  if (m_logger.isDebugEnabled())
				m_logger.debug("resetInvitation. New m_invitationCount = " +
									m_invitationCount);
	 }

	 /**
	  * This method returns the current invitation count.
	  * This method will be called by the SIP Session while Serializing.
	  * @return The current invitation count.
	  */
	 int getInvitationCount() {
		return m_invitationCount;
	 }

	 /**
	  * This method sets the current invitation count.
	  * This method will be called by the SIP Session while De-Serializing.
	  */
	 void setInvitationCount(int count) {
		m_invitationCount = count;
	 }

	/**
	  * Add an outstanding request. It will reain here till a final response
	  * is seen
	  */
	 public void addOutstandingRequest(AseSipServletRequest request) {
		  m_outstandingRequests.add(request);
	 }

	 /**
	  * Remove a outstanding request based on the cseq number.
	  * If found, remove it and return it
	  * Else return NULL
	  8 Called when a final response is seen for the request
	  */
	 public AseSipServletRequest removeOutstandingRequest(long cseq) {
		  return removeRequest(m_outstandingRequests, cseq);
	 }

	 /**
	  * Check if there is an outstanding request with the specified cseq
	  */
	 public boolean isRequestOutstanding(long cseq) {
		  
		  Iterator iter = m_outstandingRequests.iterator();
		  AseSipServletRequest req = null;
		  
		  while (iter.hasNext()) {
				req = (AseSipServletRequest)(iter.next());
            if (req.getDsRequest().getCSeqNumber() == cseq)
                return true;
		  }
		  
		  return false;
	 }

    /**
     * Check if there is an outstanding ACK for an INVITE-2XX xaction
     * Essentially check if there is a request in the success list
     */
    public boolean isAckOutstanding() {
        if (0 == m_successRequests.size())
            return false;
        return true;
    }   
	 
	 /**
	  * Add a success request
	  * Request for which a 2XX response is seen
	  */ 
	 public void addSuccessRequest(AseSipServletRequest request) {
		  m_successRequests.add(request);
	 }
	 
	 /**
	  * Remove a success request using the cseq number
	  * if found, remove it and return it
	  * Else return NULL
	  * Typically called when an ACK is seen for the success response
	  */
	 public AseSipServletRequest removeSuccessRequest(long cseq) {
		  return removeRequest(m_successRequests, cseq);
	 }

	 /**
	  * Add a failure request
	  * Request for which a 3XX-6XX response is seen
	  */		  
	 public void addFailureRequest(AseSipServletRequest request) {
		  m_failureRequests.add(request);
	 }
	 
	 /**
	  * Remove a failure request using the cseq number
	  * if found, remove it and return it
	  * Else return NULL
	  * Typically called when an ACK is seen for the failure response
	  */
	 public AseSipServletRequest removeFailureRequest(long cseq) {
		  return removeRequest(m_failureRequests, cseq);
	 }
		  
	 /**
	  * Remove a request from the list using the cseq number
	  * if found, remove it and return it
	  * Else return NULL
	  */
	 private AseSipServletRequest removeRequest(List requestsList, long cseq) {
		  Iterator iter = requestsList.iterator();
		  AseSipServletRequest req = null;
		  
		  while (iter.hasNext()) {
				req = (AseSipServletRequest)(iter.next());
            if (req.getDsRequest().getCSeqNumber() == cseq)
                break;

				req = null;
		  }
		  
		  // If request is found remove it
		  if (null != req)
				iter.remove();
		  
		  return req;
	 }

    /**
     * Initialize method. Called after the AseSipSession has been replicated
    void initialize() {
        if (null == m_outstandingRequests)
            m_outstandingRequests = new ArrayList();  
        if (null == m_successRequests)
            m_successRequests = new ArrayList();  
        if (null == m_failureRequests)
            m_failureRequests = new ArrayList();  
    }
     */
		  
	 private static Logger m_logger =
		  Logger.getLogger(AseSipInvitationHandler.class);

	 public List m_outstandingRequests = Collections.synchronizedList(new ArrayList());
	 //TODO: Made these two transient, successrequests are needed to send ACK.
	 //this will impact the point where we need to send ACK from standby and this will 
	 //happen when FT happens after sending 200 OK to party-a and ACK needs to be send to
	 //party-b after receiving ACK from party-a
	 private transient List m_successRequests = Collections.synchronizedList(new ArrayList());
	 private transient List m_failureRequests = Collections.synchronizedList(new ArrayList());
	 
	 private int m_invitationCount = 0;
	 
}


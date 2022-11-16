package com.genband.tcap.provider;

import jain.protocol.ss7.IdNotAvailableException;
import jain.protocol.ss7.tcap.ComponentIndEvent;
import jain.protocol.ss7.tcap.ComponentReqEvent;
import jain.protocol.ss7.tcap.DialogueIndEvent;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;

import java.util.Iterator;
import java.util.List;

/**
 * The Tcap Session Interface.
 * 
 * Allows the application to store the state information.
 */
public interface TcapSession {

	/**
	 * Returns the Dialogue ID associated with this TcapSession.
	 * @return Dialogue ID.
	 */
	public int getDialogueId();
	
	/**
	 * Return the Dialogue ID used in outgoing dialogue. 
	 * @return Dialogue ID
	 */
	public int getOutgoingDialogueId();
	
	
	/**
	 * Set outgoing dialogue
	 * @param outgoingDialogueId
	 */
	void setOutgoingDialogueId(int outgoingDialogueId);
	
	/**
	 * @reeta Added for Ansi Tcap messages correlation 
	 * @return
	 */
	public int getTcCorrelationId();
	
	public void setTcCorrelationId(int tcCorrelationId);
	
	/**
	 * Returns the iterator of the attribute names.
	 * @return Iterator of the attribute names.
	 */
	public Iterator<String> getAttributeNames();
	
	/**
	 * Returns the attribute with the specified name.
	 * @param name - Name of the attribute.
	 * @return attribute value object.
	 */
	public Object getAttribute(String name);
	
	/**
	 * Allows the application to store attributes as name-value pairs.
	 * @param name - Name of the attribute.
	 * @param value - Value of the attribute.
	 */
	public void setAttribute(String name, Object value);
	
	/**
	 * Removes the attribute specified by the name.
	 * @param name - Name of the attribute.
	 * @return The value object associated with this name.
	 */
	public Object removeAttribute(String name);
	
	/**
	 * Invalidates this TcapSession object.
	 * @throws IdNotAvailableException - If the Dialogue ID associated with the TcapSession 
	 * could not be released.
	 */
	public void invalidate() throws IdNotAvailableException;
	
	/**
	 * Replicates this TcapSession object to the remote standby server based on the current
	 * policies configured in the container.
	 * 
	 * i.e., If the container runtime configuration and policies allow the object to be replicated,
	 * calling this method will replicate this object to the container's standby instance.
	 */
	public void replicate();
	
	public int incrementCounter();
	
	/**
	 * 	This method is exposed to the service for getting the Application Session from
	 *  JainTcapProvider's Servlet Context.
	 *  This is introduced to get the Application Session in case of FT. As storing App Session
	 *  in the attribute resulting in the multiple references of same app session and on FT
	 *  these turns out to be different objects all together.
	 */
	public SipApplicationSession getAppSession(String id);
	
	public void removeAttributes();
	
	//This API is needed to clean the Correlation Map in case of FT. This is introduced as 
	//in sessionDidActivate service doesn't get the Servlet Context as it was getting replicated
	//as part of App Session attribute
	public void cleanCorrMap(Integer correlationId);
	
	/**
	 * This method is added to check state of tcap session.
	 * Method should retuyrn true if tcapsession is closed.
	 * Introduced to handle scenarios in recieved Tcap method where
	 * recieved dialg has multiple components 
	 * and call has to be cleaned after first component.
	 * Eg:: ENC+ERB_OANS and parsing fails for ENC. in this cse service cleans call on ENC.
	 */
	public boolean isClosed();

	/**
	 * release lock on ts
	 */
	public void release();

	/**
	 * take lock on ts
	 */
	public void acquire();
	
	/**
	 * 
	 * @return wheteher session is activated on FT or not
	 */
	public boolean isActive();
	
	/**
	 * used to activate sessions on use
	 */
	public void activate();
	
	/**
	 * used to cleanup TS if not removed before AS cleanup
	 */
	public void cleanup();
	
	/**
	 * Updates the tcap-print-info for incoming IndEvent
	 */
	public void updateIndEventPrintInfo(SipServletRequest req, List<ComponentIndEvent> list, int primitiveType);
	/**
	 * Updates the tcap-print-info for outgoing ReqEvent
	 */
	public void updateReqEventPrintInfo(SipServletRequest req, List<ComponentReqEvent> list, int primitiveType);
}

package com.baypackets.ase.sbb.impl;

import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.Rel100Exception;
import java.io.IOException;
	

import com.baypackets.ase.sbb.ProcessMessageException;
/**
 * This interface defines the methods for implementing 
 * the SBB specific functionalities. 
 */
public interface SBBOperation {

	public static int ACTION_CONTINUE = 1;
	public static int ACTION_NOOP =	2;
	public static int ACTION_FAIL = 3;
	public static int ACTION_UNKNOWN = 4;
	
	/**
	 * This method will be invoked when the SBB Servlet receives 
	 * a request from the network.
	 * 
	 * <p>
	 * The Servlet invokes this method only if the isMatching() on this object returns TRUE. 
	 * 
	 * @param request The incoming request to be handled.
	 */
	public void handleRequest(SipServletRequest request);
	
	/**
	 * This method will be invoked when the SBB Servlet receives 
	 * a response from the network.
	 * 
	 * <p>
	 * The Servlet invokes this method only if the isMatching() on this object returns TRUE. 
	 * 
	 * @param response The incoming response to be handled.
	 */
	public void handleResponse(SipServletResponse response);
	
	/**
	 * This method would be called from this operation handler itself to do some
	 * basic operation like invoking the method on the MessageModifier interface
	 * before sending this request OUT.
	 * 
	 * <P>
	 * Also this method sends out this request.
	 * 
	 * @param request Request Object to be send OUT.
	 */
	public void sendRequest(SipServletRequest request) throws IOException ;
	
	/**
	 * This method would be called from this operation handler itself to do some
	 * basic operation like invoking the method on the MessageModifier interface
	 * before sending this response OUT.
	 * 
	 * <P>
	 * Also this method sends out this response.
	 * 
	 * @param response Response to be send out
	 * @param reliable Flag indicating whether to send this response reliably or not.
	 */
	public void sendResponse(SipServletResponse response, boolean reliable)
					throws Rel100Exception,IOException ;
	
	/**
	 * This method will be invoked when the SBB Servlet receives 
	 * a timeout notification for the ACK messages.
	 * 
	 * <p>
	 * The Servlet invokes this method only if the isMatching() on this object returns TRUE. 
	 * @param session
	 */
	public void ackTimedout(SipSession session);
	
	/**
	 * This method will be invoked when the SBB Servlet receives 
	 * a timeout notification for the PRACK messages.
	 * 
	 * <p>
	 * The Servlet invokes this method only if the isMatching() on this object returns TRUE. 
	 * @param session
	 */
	public void prackTimedout(SipSession session);
	
	/**
	 * Specifies whether or not the specific operation is Completed.
	 * In case of the operation is COMPLETED, isMatching() will not be called on this object.
	 * @return whether the Operation is COMPLETED or NOT.
	 */
	public boolean isCompleted();
	
	/**
	 * The Servlet invokes this method to check whether this is the right handler 
	 * object to handle the incoming SIP messages.
	 * 
	 * @param message Incoming SIP message
	 * @return true if this handler can handle this message, false otherwise.
	 */
	public boolean isMatching(SipServletMessage message);
	
	/**
	 * Returns the Context Object associated with this SBB Operation.
	 * @return the SBBOperationContext object.
	 */
	public SBBOperationContext getOperationContext();
	
	/**
	 * Sets the SBB OperationContext object.
	 * @param ctx  The SBB OperationContext for this Object.
	 */
	public void setOperationContext(SBBOperationContext ctx);
	
	/**
	 * This method will be invoked to start this operation.
	 * This would be done, when the application invokes an operation on the SBB.
	 * (OR) when the SBB Servlet receives an message from network,
	 * but there are no handlers available to handle it.
	 *
	 */
	public void start() throws ProcessMessageException;


	/**
     * This method will be invoked to start this operation.
     * This would be done, when the application invokes an operation on the SBB.
     * (OR) when the SBB Servlet receives an message from network,
     * but there are no handlers available to handle it.
     *
     */
	public void timerExpired(ServletTimer timer) ;
	
	/**
	 * This method will be called to cancel a specific operation
	 * that was started by the application.
	 */
	public void cancel();
}

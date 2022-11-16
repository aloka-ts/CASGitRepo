/*
 * CallTraceService.java
 *
 * Created on October 5, 2004, 2:57 PM
 */
package com.baypackets.ase.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletMessage;

import com.baypackets.utils.calltracing.CallConstraint;


/**
 * This interface defines an object that is used to selectively log
 * SIP messages.
 */
public interface CallTraceService {

    // Call states
    public static final int CALL_IN_PROGRESS = 0;
    public static final int CALL_TERMINATED = 1;
    public static final int CALL_TRACING_NOT_ENABLE = -1;
    public static final int MAX_TRACED_CALL_COUNT_REACHED = -2;


    /**
     * Compares the attributes of the given SipServletMessage with this 
     * object's constraints on SIP messages and returns a value of "true" if
     * it is a match or returns "false" otherwise.
     */
    public boolean matchesCriteria(SipServletMessage message);

    /**
     * Compares the provided parameters with all the CallTracing 
     * constraints and returns the list of all the matched constraints
     * return  null otherwise.
     */
    public List<Integer> matchesCriteria(String origAddr,String termAddr,String DialedAddr);
    
    /**
     * Compares the provided parameters with all the CallTracing 
     * constraints and returns the list of all the matched constraints
     * return  null otherwise.
     */
    public List<Integer> matchesCriteria(String origAddr,String termAddr,String dialedAddr,String ipAddressPort,String opc,String serviceKey);

    /**
     * Logs the given SIP message.
     *
     * @param message - The SIP message to be logged.
     * @param logMessage - Additional info to be logged with the message.
     */
    public void trace(SipServletMessage message, String logMessage);

    /**
     * Logs the given SIP message.for supplied constraintId.
     *
     * @param message - The SIP message to be logged.
     * @param traceKey - unique key to identify one call.
     * @param constraintId - constraint for which message is to be trace to 
     *  EMS console.
     *  @param callState - state of call. CALL_IN_PROGRESS or CALL_TERMINATED
     *  @return int - '0' incase call was traced successfully
     *  			- CALL_TRACING_NOT_ENABLE if call tracing is not enabled.
     *  			- MAX_TRACED_CALL_COUNT_REACHED if max call tracing count is reached.
     */
    public int trace (int constraintId, String traceKey , String logMessage, int callState);
    
    /**
     * Returns the set of all SIP message constraints currently registered
     * with this object.
     *
     * @return  A Collection of CallConstraint objects.
     * @see com.baypackets.utils.calltracing.CallConstraint
     */
    public Collection getCallConstraints();


    /**
     * Registers a new SIP message constraint with this object.
     */
    public void addCallConstraint(CallConstraint constraint);


    /**
     * Removes the specified SIP message constraint.
     */
    public void removeCallConstraint(String constraintID);

	/**
	 * Indicates whether SIP message logging is currently enabled for container.
	 * If this is 'false' then container will not trace any SIP message.
	 */
	public boolean isContainerTracingEnabled();
	
    /**
     * Indicates whether SIP message logging is currently enabled.
     */
    public boolean isEnabled();


    /**
     * Enables SIP message logging.
     */
    public void enableCallTracing();


    /**
     * Disables SIP message logging.
     */
    public void disableCallTracing();
    
    /**
     * Decrements the active call count for any constraint ID.
     */
    public void decrementActiveCallCount(int constraintId);

	int trace(int constraintId, String traceKey, String logMessage,
			int callState, String caller, String called);

}

/*
 * EMSCallTraceAppender.java
 *
 * Created on October 5, 2004, 2:57 PM
 */
package com.baypackets.ase.util;

import com.baypackets.ase.common.AgentDelegate;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;
import org.apache.log4j.Logger;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.MDC;
import org.apache.log4j.spi.LoggingEvent;


/**
 * Custom log4j Appender used to send call trace messages to the EMS console.
 */
public class EMSCallTraceAppender extends AppenderSkeleton {

    private static Logger _logger = Logger.getLogger(EMSCallTraceAppender.class);
    
    private static String CALL_ID = "CALL_ID";
    private static String MESSAGE = "MESSAGE";
    private static String TRACE_ID = "TRACE_ID";
    private static String TEST_INDICATOR = "TEST_INDICATOR";


    /**
     * Sends the data encapsulated by the given LoggingEvent object to EMS.
     */
    protected void append(LoggingEvent loggingEvent) {
        if (_logger.isInfoEnabled()) {
            _logger.info("invoking EmsAgent.reportCallHistoryInfo()");
        }
        
        // Obtain the agent used to communicate with EMS
        AgentDelegate agent = (AgentDelegate)Registry.lookup(Constants.NAME_AGENT_DELEGATE);

        // Extract the parameters to log from the MDC Map
        String callID = (String)MDC.get(CALL_ID);
        String traceID = (String)MDC.get(TRACE_ID);
        String message = (String)MDC.get(MESSAGE);
        Boolean testIndicator = (Boolean)MDC.get(TEST_INDICATOR);

        if (traceID == null || testIndicator == null) {
            return;
        }

        int testIndicatorValue = testIndicator.booleanValue() ? 1 : 0;

        // Invoke agent to log the call trace
        agent.reportCallHistoryInfo(callID, message, Integer.parseInt(traceID), testIndicatorValue);
    }


    /**
     * Implemented from the AppenderSkeleton super class.
     */
    public boolean requiresLayout() {
        return getLayout() == null;
    }


    /**
     *
     */
    public void close() {
        // No op
    }

}

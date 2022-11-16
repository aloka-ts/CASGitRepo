/*
 * CallStatePersister.java
 *
 * Created on September 16, 2004, 2:10 PM
 */
package com.baypackets.clicktodial.util;


import java.util.Date;

import javax.servlet.sip.SipApplicationSessionListener;
import javax.servlet.sip.SipApplicationSessionEvent;
import javax.servlet.sip.SipApplicationSession;

import org.apache.log4j.Logger;


/**
 * This listener is used to persist any relevant call state information stored 
 * in the application session to the backing store just before the session is 
 * invalidated.
 */
public class CallStatePersister implements SipApplicationSessionListener, Constants {    

    private static Logger _logger = Logger.getLogger(CallStatePersister.class);
        
    /**
     * Called by the container just before an application session is destroyed.
     * Any call state information stored in that session will be persisted 
     * to the backing store in this method.
     *
     * @param event contains the SipApplicationSession that is about to be
     * invalidated.
     */
    public void sessionExpired(SipApplicationSessionEvent event) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("sessionDestroyed() called");
        }
        
        try {
            SipApplicationSession session = event.getApplicationSession();
                        
            // Get a reference to the data access object from the session.
            CallDAO dao = (CallDAO)session.getAttribute(CALL_DAO);
        
            if (dao == null) {
                return;
            }

            if (_logger.isDebugEnabled()) {
                _logger.debug("Persisting call state info to the backing store...");
            }
            
            // Create a Call object to encapsulate the call state info.
            Call call = new Call();
            call.setCallID(session.getId());
            call.setCallStartTime((Date)session.getAttribute(CALL_START_TIME));
            call.setCallEndTime((Date)session.getAttribute(CALL_END_TIME));
        
            // Persist the Call object to the backing store
            dao.persist(call);                         
        } catch (Exception e) {
            String msg = "Error occurred while persisting call state info to the backing store: " + e.toString();
            _logger.error(msg, e);
            throw new RuntimeException(msg);
        }
        
        if (_logger.isDebugEnabled()) {
            _logger.debug("Successfully persisted call state to the backing store.");
        }
    }
    
        
    /**
     * Does nothing.
     */
    public void sessionDestroyed(SipApplicationSessionEvent event) {
        // No op
    }        
    
    
    /**
     * Does nothing
     */
    public void sessionCreated(javax.servlet.sip.SipApplicationSessionEvent sipApplicationSessionEvent) {
        // No op
    }    
        
}

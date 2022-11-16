/*
 * LoggingCriteria.java
 *
 * Created on September 22, 2004, 7:01 PM
 */
package com.baypackets.ase.common.logging;

import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;

import javax.servlet.sip.SipServletMessage;


/**
 * An instance of this class encapsulates constraints that determine when
 * logging should be performed.
 */
public class LoggingCriteria implements Serializable {
    
	private static final long serialVersionUID = -346683627894780L;

	private static LoggingCriteria _instance = new LoggingCriteria();
            
    private MessageConstraints _constraints = new MessageConstraints();
    private Set _threadNames = new HashSet();
    private boolean _enabled;
        
    private LoggingCriteria() {        
    }
    
    
    /**
     * Returns the singleton instance.
     */
    public static LoggingCriteria getInstance() {
        return _instance;
    }
    
    
    /**
     * Removes all logging constraints held by this object.
     */
    public void clear() {
        _constraints.removeAll();
        _threadNames.clear();
    }
    
    
    /**
     * Returns a flag indicating whether logging criteria checking 
     * should be performed.
     */
    public boolean isEnabled() {
        return _enabled;
    }
    
    
    /**
     * Enables or disables the logging criteria.
     */
    public void setEnabled(boolean enabled) {
        _enabled = enabled;
    }        
    
        
    /**
     * Returns the constraints that are used to match against a given
     * SipServletMessage object.
     *
     * @see javax.servlet.sip.SipServletMessage
     */
    public MessageConstraints getMessageConstraints() {
        return _constraints;        
    }          
    
    
    /**
     * Indicates whether the specified thread has permission to perform 
     * logging.
     */
    public boolean hasLoggingPermission(String threadName) {
        return _threadNames.contains(threadName);
    }
    
    
    /**
     * Grants the specified thread the permission to perform logging.
     */
    public synchronized void grantLoggingPermission(String threadName) {
        _threadNames.add(threadName);
    }
    
    
    /**
     * Denies the specified thread the permission to perform logging.
     */
    public synchronized void denyLoggingPermission(String threadName) {
        _threadNames.remove(threadName);
    }
    
    
    /**
     * Convenience method used to enable or disable logging for the calling
     * thread based on whether the given SipServletMessage matches the 
     * MessageConstraints encapsulated by this object.
     */
    public void check(SipServletMessage message) {        
        if (!this.isEnabled()) {
            return;
        }
        
        if (_constraints.matches(message)) {
            this.grantLoggingPermission(Thread.currentThread().getName());
        } else {
            this.denyLoggingPermission(Thread.currentThread().getName());
        }
    }
    
}

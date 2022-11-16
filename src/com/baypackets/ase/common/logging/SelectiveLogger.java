/*
 * SelectiveLogger.java
 *
 * Created on September 22, 2004, 7:01 PM
 */
package com.baypackets.ase.common.logging;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;


/**
 * This extension of Apache's Logger class checks that the calling thread has 
 * been granted permission to log before logging a given message. 
 */
public class SelectiveLogger extends Logger {

    private static LoggingCriteria _criteria = LoggingCriteria.getInstance();
        
    /**
     * Calls the super class constructor.
     */
    public SelectiveLogger(String name) {
        super(name);
    }
    
    /**
     * Overriden from the Logger class.
     */
    public boolean isEnabledFor(Priority priority) {        
        if (_criteria.isEnabled() && _criteria.hasLoggingPermission(Thread.currentThread().getName())) {
            return true;
        }
        return super.isEnabledFor(priority);
    }
    
    /**
     * Overriden from the Logger class.
     */
    public boolean isDebugEnabled() {     
        if (_criteria.isEnabled() && _criteria.hasLoggingPermission(Thread.currentThread().getName())) {
            return true;
        }
        return super.isDebugEnabled();
    }    
    
    /**
     * Overriden from the Logger class.
     */
    public boolean isInfoEnabled() {
        if (_criteria.isEnabled() && _criteria.hasLoggingPermission(Thread.currentThread().getName())) {
            return true;
        }
        return super.isInfoEnabled();
    }    
        
    /**
     * Overriden from the Logger class.
     */
    public void log(Priority priority, Object message) {
        if (_criteria.isEnabled() && _criteria.hasLoggingPermission(Thread.currentThread().getName())) {
            super.forcedLog(null, priority, message, null);
        }
        super.log(priority, message);
    }    
    
    /**
     * Overriden from the Logger class.
     */
    public void log(Priority priority, Object message, Throwable throwable) {
        if (_criteria.isEnabled() && _criteria.hasLoggingPermission(Thread.currentThread().getName())) {
            super.forcedLog(null, priority, message, throwable);
        }
        super.log(priority, message, throwable);
    }    
        
    /**
     * Overriden from the Logger class.
     */
    public void log(String FQCN, Priority priority, Object message, Throwable throwable) {
        if (_criteria.isEnabled() && _criteria.hasLoggingPermission(Thread.currentThread().getName())) {
            super.forcedLog(null, priority, message, throwable);
        }
        super.log(FQCN, priority, message, throwable);        
    }      
    
    /**
     * Overriden from the Logger class.
     */
    public void debug(Object msg) {
        if (_criteria.isEnabled() && _criteria.hasLoggingPermission(Thread.currentThread().getName())) {
            super.forcedLog(null, Level.DEBUG, msg, null);
        }        
        super.debug(msg);        
    }
        
    /**
     * Overriden from the Logger class.
     */
    public void debug(Object msg, Throwable th) {
        if (_criteria.isEnabled() && _criteria.hasLoggingPermission(Thread.currentThread().getName())) {
            super.forcedLog(null, Level.DEBUG, msg, th);
        }        
        super.debug(msg, th);        
    }
        
    /**
     * Overriden from the Logger class.
     */
    public void error(Object msg) {
        if (_criteria.isEnabled() && _criteria.hasLoggingPermission(Thread.currentThread().getName())) {
            super.forcedLog(null, Level.ERROR, msg, null);
        }        
        super.error(msg);        
    }
       
    /**
     * Overriden from the Logger class.
     */
    public void error(Object msg, Throwable th) {
        if (_criteria.isEnabled() && _criteria.hasLoggingPermission(Thread.currentThread().getName())) {
            super.forcedLog(null, Level.ERROR, msg, th);
        }        
        super.error(msg, th);        
    }
        
    /**
     * Overriden from the Logger class.
     */
    public void fatal(Object msg) {
        if (_criteria.isEnabled() && _criteria.hasLoggingPermission(Thread.currentThread().getName())) {
            super.forcedLog(null, Level.FATAL, msg, null);
        }        
        super.fatal(msg);        
    }
        
    /**
     * Overriden from the Logger class.
     */
    public void fatal(Object msg, Throwable th) {
        if (_criteria.isEnabled() && _criteria.hasLoggingPermission(Thread.currentThread().getName())) {
            super.forcedLog(null, Level.FATAL, msg, th);
        }        
        super.fatal(msg, th);        
    }
        
    /**
     * Overriden from the Logger class.
     */
    public void info(Object msg) {
        if (_criteria.isEnabled() && _criteria.hasLoggingPermission(Thread.currentThread().getName())) {
            super.forcedLog(null, Level.INFO, msg, null);
        }        
        super.info(msg);        
    }
        
    /**
     * Overriden from the Logger class.
     */
    public void info(Object msg, Throwable th) {
        if (_criteria.isEnabled() && _criteria.hasLoggingPermission(Thread.currentThread().getName())) {
            super.forcedLog(null, Level.INFO, msg, th);
        }        
        super.info(msg, th);        
    }
        
    /**
     * Overriden from the Logger class.
     */
    public void warn(Object msg) {
        if (_criteria.isEnabled() && _criteria.hasLoggingPermission(Thread.currentThread().getName())) {
            super.forcedLog(null, Level.WARN, msg, null);
        }        
        super.warn(msg);        
    }
        
    /**
     * Overriden from the Logger class.
     */
    public void warn(Object msg, Throwable th) {
        if (_criteria.isEnabled() && _criteria.hasLoggingPermission(Thread.currentThread().getName())) {
            super.forcedLog(null, Level.WARN, msg, th);
        }        
        super.warn(msg, th);        
    }
    
}

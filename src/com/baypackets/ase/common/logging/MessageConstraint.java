/*
 * MessageConstraint.java
 *
 * Created on September 22, 2004, 7:01 PM
 */
package com.baypackets.ase.common.logging;

import java.io.Serializable;
import java.util.regex.Pattern;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.Address;


/**
 * This class encapsulates criteria used to match against a given 
 * SipServletMessage.
 *
 * @see java.servlet.sip.SipServletMessage
 * @see com.baypackets.ase.common.logging.MessageConstraints
 */
public class MessageConstraint implements Serializable {
        
	private static final long serialVersionUID = 24880642438970839L;
    private Pattern callID;
    private Pattern toURI;
    private Pattern fromURI;
           
    
    /**
     * Returns "true" if the specified SipServletMessage matches the criteria
     * encapsulated by this object or returns "false" otherwise.
     */
    public boolean matches(SipServletMessage message) {
        if (!match(message.getCallId(), callID)) {
            return false;
        }
        if (!match(message.getTo().getURI().toString(), toURI)) {
            return false;
        }
        if (!match(message.getFrom().getURI().toString(), fromURI)) {
            return false;
        }
        return true;
    }
    
    
    /**
     *
     */
    private boolean match(Object value, Pattern pattern) {        
        if (pattern == null) {
            return true;
        }        
        if (value == null) {
            return false;
        }                                   
        return value.toString().matches(pattern.pattern());
    }        
    

    /**
     * 
     */
    public Pattern getCallID() {
        return callID;
    }
    
    /**
     *
     */
    public void setCallID(Pattern callID) {
        this.callID = callID;
    }
    
    /**
     *
     */
    public Pattern getToURI() {
        return toURI;
    }
    
    /**
     *
     */
    public void setToURI(Pattern toURI) {
        this.toURI = toURI;
    }
    
    /**
     *
     */
    public Pattern getFromURI() {
        return fromURI;
    }
    
    /**
     *
     */
    public void setFromURI(Pattern fromURI) {
        this.fromURI = fromURI;
    }
    
}

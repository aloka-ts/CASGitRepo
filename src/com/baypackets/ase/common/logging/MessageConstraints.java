/*
 * MessageConstraints.java
 *
 * Created on September 22, 2004, 7:01 PM
 */
package com.baypackets.ase.common.logging;

import java.io.Serializable;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Iterator;
import javax.servlet.sip.SipServletMessage;


/**
 * This class encapsulates a set of MessageConstraint objects that are used to
 * match against a given SipServletMessage.
 *
 * @see java.servlet.sip.SipServletMessage
 */
public class MessageConstraints implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -6330985977759389036L;
	private Set _constraints = new LinkedHashSet();
    
    
    /**
     * Returns "true" if the given SipServletMessage matches at least one of 
     * the MessageConstraint objects encapsulated by this instance or returns
     * "false" otherwise.
     */
    public boolean matches(SipServletMessage message) {
        Iterator constraints = _constraints.iterator();
        
        if (!constraints.hasNext()) {
            return true;
        }
        
        while (constraints.hasNext()) {
            MessageConstraint constraint = (MessageConstraint)constraints.next();
            
            if (constraint.matches(message)) {
                return true;
            }
        }        
        
        return false;        
    }
    
    
    /**
     * 
     */
    public void addConstraint(MessageConstraint constraint) {
        _constraints.add(constraint);
    }
    
    /**
     * 
     */
    public void removeConstraint(MessageConstraint constraint) {
        _constraints.remove(constraint);
    }
    
    /**
     * 
     */
    public MessageConstraint[] toArray() {
        return (MessageConstraint[])_constraints.toArray(new MessageConstraint[_constraints.size()]);
    }
    
    /**
     * 
     */
    public boolean isEmpty() {
        return _constraints.isEmpty();
    }
    
    /**
     * 
     */
    public void removeAll() {
        _constraints.clear();
    }
    
}

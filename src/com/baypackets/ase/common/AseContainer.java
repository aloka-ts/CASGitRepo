/*
 * AseContainer.java
 *
 * Created on August 6, 2004, 3:18 PM
 */
package com.baypackets.ase.common;

import java.util.EventObject;
import java.util.Iterator;

import com.baypackets.ase.spi.container.SasMessageProcessor;


/**
 * This is the base interface implemented by all container classes
 * in the Agility Servlet Engine.
 *
 * @author Zoltan Medveczky
 */
public interface AseContainer extends SasMessageProcessor{
    
    /**
     * Returns the unique identifier for this AseContainer object.
     *
     */
    public String getName();
    
    
    
    /**
     * Returns this container's parent or null if it has none.
     *
     */
    public AseContainer getParent();
    
    /**
     * Sets this container's parent.  This method may throw an 
     * IllegalArgumentException if the specified parent is not 
     * compatible with this AseContainer implementation (ex. specifying 
     * an AseHost object as the parent of an AseEngine will throw 
     * an IllegalArgumentException).
     *
     */
    public void setParent(AseContainer parent);
    
    /**
     * Adds the specified object as a child to this container.  This method 
     * may throw an IllegalArgumentException if the added child is
     * not compatible with this AseContainer implementation (ex. adding an 
     * AseHost object as a child of AseContext will throw an 
     * IllegalArgumentException).
     * 
     * @param child  The child container to add to this object
     */
    public void addChild(AseContainer child);
    
    /**
     * Returns this object's child container by name or returns null if no 
     * such child exists.
     *
     * @param name   The name of the child container to find
     */
    public AseContainer findChild(String name);
    
    /**
     * Returns all children of this container or null if it has no children.
     *
     * @return  An array containing all children of this container.
     */
    public AseContainer[] findChildren();
    
    /**
     * Removes the specified child from this container.
     *
     * @return flag indicating whether the specified child was in 
     * fact removed from this container.
     */
    public boolean removeChild(AseContainer child);
    
    /**
     * Removes the specified child from this container.
     *
     * @param name  The name of the child to remove.
     * @return flag indicating whether the specified child was in 
     * fact removed from this container.
     */
    public boolean removeChild(String name);    
    
    /**
     * Processes the given servlet request and response objects.
     *
     * @param request  An object encapsulating a servlet request.
     * @param response  An object encapsulating a servlet response.
     */
    //public void invoke(AseBaseRequest request, AseBaseResponse response) 
	//						throws AseInvocationFailedException, ServletException; 
    
    /**
     * Processes the given event.
     *
     */
    public void handleEvent(EventObject event, AseEventListener listener);
    
    /**
     * Processes the given message.
     *
     */
    public void handleMessage(AseMessage message);  
    
    /**
     * When a new protocol connector is deployed into the ASE, the connector
     * will register itself with the container hierarchy by calling this
     * method on the root AseContainer implementation.
     *
     * @param connector  An implementation of a protocol connector.
     */
    public void registerConnector(AseBaseConnector connector);
    
    /**
     * This method will return all connectors that are currently registered 
     * with the underlying AseContainer implementation.  If no connectors are 
     * directly registered with that AseContainer, it will invoke this method 
     * on it's parent.  A value of null will be returned if no connectors are 
     * registered anywhere in the container hierarchy.
     *
     * @return an iteration over all AseBaseConnector objects registered with
     * this AseContainer
     * @see com.baypackets.ase.common.AseBaseConnector
     */
    public Iterator getConnectors();
    
}

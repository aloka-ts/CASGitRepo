/*
 * AseBaseContainer.java
 *
 * Created on August 6, 2004, 3:47 PM
 */
package com.baypackets.ase.container;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.EventObject;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseBaseConnector;
import com.baypackets.ase.common.AseContainer;
import com.baypackets.ase.common.AseEventListener;
import com.baypackets.ase.common.AseMessage;
import com.baypackets.ase.common.Lifecycle;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.common.exceptions.ShutdownFailedException;
import com.baypackets.ase.common.exceptions.StartupFailedException;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.StringManager;


/**
 * This class provides a partial implementation of the AseContainer interface.
 * The different containers in the Servlet engine will extend this base class 
 * to provide a complete implementation.
 *
 * @author  Zoltan Medveczky
 */

@DefaultSerializer(ExternalizableSerializer.class)
public abstract class AseBaseContainer implements AseContainer, Lifecycle,Externalizable {
    
    private static Logger _logger = Logger.getLogger(AseBaseContainer.class);
    private static StringManager _strings = StringManager.getInstance(AseBaseContainer.class.getPackage());
    private static final long serialVersionUID = -3814634264647849721L;
    private String _name;
    private AseContainer _parent;
    private ArrayList _connectors = new ArrayList();    
    private Map _children = new ConcurrentHashMap();
    private boolean _running = false;
    
    
    /**
     * Default Constructor.
     *
     */
    public AseBaseContainer() {  
        super();
    }
    
    /**
     * Assigns the specified name to this AseContainer instance.
     *
     * @param name  The unique name to associate with this container
     * @throws IllegalArgumentException if the given name is null
     */
    public AseBaseContainer(String name) {  
        _name = name;
    }
    
    /**
     * Returns the name that uniquely identifies this AseContainer object.
     *
     * @return  This AseContainer's unique name.
     */
    public String getName() {
        return _name;
    }
    
    /**
     * Returns this AseContainer's parent or null if it has none.
     *
     * @return  This AseContainer's parent.
     */
    public AseContainer getParent() {
        return _parent;
    }
    
    /**
     * Sets this AseContainer's parent.  This method may throw an 
     * IllegalArgumentException if the specified parent is not 
     * compatible with this AseContainer implementation (ex. specifying 
     * an AseHost object as the parent of an AseEngine will throw 
     * an IllegalArgumentException).
     *
     */
    public void setParent(AseContainer parent) {        
        _parent = parent;
    }
    
    /**
     * Adds the specified object as a child to this AseContainer.  This method
     * may throw an IllegalArgumentException if the added child is
     * not compatible with this AseContainer implementation (ex. adding an 
     * AseHost object as a child of AseContext will throw an 
     * IllegalArgumentException).
     * 
     * @param child  The child container to add to this object
     */
    public void addChild(AseContainer child) {
        if (child != null) {
		if (_logger.isDebugEnabled()) {

			_logger.debug(" childname="+child.getName() );
		}
            _children.put(child.getName(), child);
            child.setParent(this);
        }
	else { 
		if (_logger.isDebugEnabled()) {

		_logger.debug(" child is NULL "); }
		}
    }
    
    /**
     * Returns the specified child container by name or returns null if no 
     * such child exists.
     *
     * @param name  The key used to look up the specified child container
     */
    public AseContainer findChild(String name) {
        return (AseContainer)_children.get(name);
    }
    
    /**
     * Returns all children of this container or null if it has no children.
     *
     * @return  An array containing all children of this container.
     */
    public AseContainer[] findChildren() {
        if (_children.isEmpty()) {
            return new AseContainer[0];
        }
        return (AseContainer[])_children.values().toArray(new AseContainer[_children.size()]);
    }
    
    /**
     * Removes the specified child from this container.
     *
     * @return flag indicating whether the specified child was in fact removed
     * from this container.
     */
    public boolean removeChild(AseContainer child) {
        if (child != null && this.equals(child.getParent())) {
            child.setParent(null);
            return _children.remove(child.getName()) != null;
        }
        return false;
    }
    
    /**
     * Removes the specified child from this container.
     *
     * @param name  The name of the child to remove.
     * @return flag indicating whether the specified child was in fact removed
     * from this container.
     */
    public boolean removeChild(String name) {
        return removeChild(findChild(name)); 
    } 
    
    
	public void initialize() throws Exception {
	}

	/**
     * This method is implemented from the Lifecylce interface.  It performs
     * actions that are common to most AseContainer implementations when they
     * are started up.
     *
     */
    public void start() throws StartupFailedException {        
        try {
            // start this container's child Lifecycle objects
            Iterator iter = _children.values().iterator();
            while (iter.hasNext()) {
                AseContainer child = (AseContainer)iter.next();
                if (child instanceof Lifecycle) {
                    ((Lifecycle)child).start();
                }
            }            
            _running = true;  // set state to "running"
        } catch (Exception e) {
            _logger.error(e.toString(), e);
            
            if (e instanceof StartupFailedException) {
                throw (StartupFailedException)e;
            }
            throw new StartupFailedException(e.toString());
        }        
    }
    
    /**
     * This method is implemented from the Lifecylce interface.  It performs
     * actions that are common to most AseContainer implementations when they
     * are shutdown.
     *
     */
    public void stop() throws ShutdownFailedException {        
        try {
            // stop this container's child Lifecycle objects
            Iterator iter = _children.values().iterator();
            while (iter.hasNext()) {
                AseContainer child = (AseContainer)iter.next();
                if (child instanceof Lifecycle) {
                    ((Lifecycle)child).stop();
                }
            }
            _running = false;  // set state to "stopped"
        } catch (Exception e) {
            _logger.error(e.toString(), e);
            
            if (e instanceof ShutdownFailedException) {
                throw (ShutdownFailedException)e;
            }
            throw new ShutdownFailedException(e.toString());
        }  
    }
    
    /**
     * This method is implemented from the Lifecycle interface.  It 
     * indicates the running state of this container.
     *
     */
    public boolean isRunning() {
        return _running;
    }
    
    /**
     * Invoked by subclasses to set this container's running state.
     *
     */
    protected void setRunning(boolean running) {
        _running = running;
    }
    
    /**
     * Processes the given event.
     *
     */
    public void handleEvent(EventObject event, AseEventListener listener) {
        // WIP
    }
    
    /**
     * Processes the given message.
     *
     */
    public void handleMessage(AseMessage message) {
        // WIP       
    }
    
    /**
     * When a new protocol connector is deployed into the ASE, the connector
     * will register itself with the container hierarchy by calling this
     * method on the root AseContainer implementation.
     *
     * @param connector  An implementation of a protocol connector.
     */
    public void registerConnector(AseBaseConnector connector) {
      if (_logger.isInfoEnabled()) {
         _logger.info("registerConnector");
         _logger.info("Connector Protocol is " + connector.getProtocol());
      }

    	if(_connectors.indexOf(connector) == -1) {
            _connectors.add(connector); 
        }
    }
    
    /**
     * This method will return all connectors that are currently registered 
     * with the underlying AseContainer implementation.  If no connectors are 
     * directly registered with that AseContainer, it will invoke this method 
     * on it's parent.  A value of null will be returned if no connectors are 
     * registered anywhere in the AseContainer hierarchy.
     *
     * @return an iteration over all AseBaseConnector objects registered with
     * this AseContainer or one of it's ancestors
     * @see com.baypackets.ase.common.AseBaseConnector
     */
    public Iterator getConnectors() {
        if (_connectors == null || _connectors.isEmpty()) {
            AseContainer parent = getParent();
            if (parent != null) {
                Iterator connectors = parent.getConnectors();
                if (connectors != null) {
                    return connectors;
                }
            }
            return null;
        }        

        if (_logger.isInfoEnabled()) {
           _logger.info("Returning a iterator over the _connectors");
        }
        return _connectors.iterator();
    }
	

    /**
     *
     *
     */
    public AseProtocolAdapter getProtocolAdapter(String protocol){
        AseProtocolAdapter adapter = null;
	adapter = (AseProtocolAdapter) Registry.lookup(protocol + Constants.NAME_ADAPTER_SUFFIX);
        return adapter;
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
    	//TODO: Added for the probable future use
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
    	//TODO: Added for the probable future use
    }

    
}

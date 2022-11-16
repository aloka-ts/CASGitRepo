/**
 * ExternalDevice.java
 */
package com.baypackets.ase.sbb;

import java.net.InetAddress;
import java.util.Iterator;

/**
 * The ExternalDevice interface provides the details of an external device including its ID, Name, State, 
 * Host, Port
 */
public interface ExternalDevice {
	
    /**
     * Device's active state
     */
    public static final int STATE_ACTIVE = 2;

    /**
     * Device's suspect state
     */
    public static final int STATE_SUSPECT = 1;

    /**
     * Device's admin state
     */
    public static final int STATE_ADMIN = 3;
    
    
    /**
     * Device's down state
     */
    public static final int STATE_DOWN = 0;
    

    /**
     * Returns the host of the Device.
     * @return The host of the Device.
     */
    public InetAddress getHost();

    /**
     * Returns the ID of the Device.
     * This value is a unique identifier for each device provisioned in the system.
     * @return The ID of the Device.
     */
    public String getId();

    /**
     * Returns the name of the Device.
     * More than one device could have the same name. 
     * If the selectByName() method is called on the <code>ExternalDeviceSelector</code>
     * and if there are more than one ACTIVE device with the specified name,
     * then the selector applies a selection logic on them and returns one.
     * @return The name of the Device.
     */
    public String getName();
	
    /**
     * Returns the port of the Device.
     * @return The port where the Device is listening.
     */
    public int getPort();
	
    /**
     * Returns the state of the device, ACTIVE, DOWN, SUSPECT
     * @return state of the device.
     */
    public int getState();
	
    /**
     * Returns the device priority
     * @return numeric value of the priority
     */
     public int getPriority();

     /**
     * Returns the names of those attributes whose values are set.
     * @return  The list of set attributes or an empty Iterator if
     * none are currently set.
     */
    public Iterator getAttributeNames();
	
    /**
     * Returns the value of the specified attribute or NULL if the
     * attribute is not currently set.
     * @param name - The name of the attribute to return.
     */
    public Object getAttribute(String name);
	
    /**
     * Sets the specified attribute.
     * @param name - The name of the attribute to set.
     * @param value - The value to set on the attribute.
     * @throws IllegalArgumentException if no such attribute is 
     * supported by the media server or if the specified value
     * is invalid.
     */
    public void setAttribute(String name, Object value);

    /**
     * Enable heart beats on the device
     */
    public void enableHeartbeat();

    /**
     * disable heart beats on the device
     */
    public void disableHeartbeat();

    /**
     * Return whether heartbeats are enabled on the device
     * @return the heartbeat status for the device
     */
    public boolean isHeartbeatEnabled();

    /**
     * Get the heartbeat URI setting for the device
     * @return the String URI for heartbeats toward the device
     */
    public String getHeartbeatUri();

    /**
     * Set the heartbeat URI for the device
     * @param uri String uri (sip:x@y)
     */
    public void setHeartbeatUri(String uri);
}

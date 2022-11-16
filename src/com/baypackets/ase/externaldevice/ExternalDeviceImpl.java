/*
 * ExternalDeviceImpl.java
 *
 */
package com.baypackets.ase.externaldevice;

import com.baypackets.ase.util.StringManager;
import java.net.InetAddress;
import java.net.URI;
import java.util.Map;
import java.util.Hashtable;
import java.util.Iterator;
import com.baypackets.ase.sbb.ExternalDevice;

/**
 * The implementation of the ExternalDevice interface.
 */
public class ExternalDeviceImpl implements ExternalDevice, java.io.Serializable {

	private static final long serialVersionUID = 439473013418248251L;
    /**
     * Human consumable identifier for the device
     */
    String id;

    /**
     * Human consumable vendor name for the device
     */
    private String name;

    /*
     * Connectivity state for the device.
     * @see com.baypackets.ase.sbb.ExternalDevice
     */
    private int state = STATE_DOWN;

    /*
     * IP address for accessing the device (SIP)
     */
    private InetAddress host = null;

    /*
     * TCP/UDP port for accessing the device
     */
    private int port;

    /*
     * Indicator as to whether heartbeats are enabled towards the device
     */
    private boolean heartbeatState = true;

    /*
     * Request URI to be used in heartbeats, if blank then computed one will be used.
     */
    private String heartbeatUri;

    /*
     * Priority for using the device over other similar devices, lower values > 0 indicate
     * higher priority, 0 indicate no priority set.
     */
    private int priority = java.lang.Integer.MAX_VALUE;

    /*
     * Map containing non-specific attributes that can be applied to the device
     */
    public Map attributes;

    /**
     * Returns the IP address of the device
     * @return the host of the device.
     */
    public InetAddress getHost() {
    return host;
    }

    /**
     * Returns the ID of the device
     * @return the ID of the device
     */
    public String getId() {
    return id;
    }

    /**
     * Returns the name of the vendor name
     * @return Name of the vendor name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the Port of the TCP/UDP port
     * @return Port on which the device is listening.
     */
    public int getPort() {
    return port;
    }

    /**
     * Returns the connectivity state of the device
     * @return state of connectivity to the device
     */
    public int getState() {
    return state;
    }

    /**
     * Sets the IP address of the device
     * @param host - IP address.
     */
    public void setHost(InetAddress addr) {
    host = addr;
    state = STATE_ACTIVE;
    }

    /**
     * Sets the ID for the device.
     * The ID is the Unique identifier for device.
     * @param id of the device
     */
    public void setId(String id) {
    this.id = id;
    }

    /**
     * Sets the vendor name of the device
     * @param name - Vendor Name of device
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the port of the device.
     * @param port - Port of the device
     */
    public void setPort(int port) {
        if ((port < 1023) || (port > 65535)) {
            throw new IllegalArgumentException("Port out of range (1024-65535): " + port);
        }
        this.port = port;
    }

    /**
     * Sets the connectivity state of the device
     * @param connectivity state towards the device
     */
    public void setState(int state) {
    this.state = state;
    }

    /**
     * Return the supported attributes
     * @return support attributes iterator
     */
    public Iterator getSupportedAttributes() {
        return this.attributes != null ? this.attributes.keySet().iterator() : null;
    }

    /**
     * Return stored attribute names
     * @return attribute names iterator
     */
    public Iterator getAttributeNames() {
        return this.attributes != null ? this.attributes.keySet().iterator() : null;
    }

    /**
     * Return stored attribute value for given name
     * @param name String name of the attribute
     * @return value of the attribubte, null if not one stored
     */
    public Object getAttribute(String name) {
        return this.attributes != null ? attributes.get(name) : null;
    }

    /**
     * Set the value of an attribute
     * @param name String name of the attribute
     * @param object Object to store
     */
    public void setAttribute(String name, Object object) {
        if (name == null || object == null) {
            throw new IllegalArgumentException("setAttribute() called on device with NULL parameters.");
        }
        if (this.attributes == null) {
            this.attributes = new Hashtable();
        }
        this.attributes.put(name, object);
    }

    /**
     * Get the priority setting for the device
     * @return the integer priority for the device
     */
    public int getPriority() {
    return priority;
    }

    /**
     * Set the priority setting for the device
     * @param the integer priority for the device
     */
    public void setPriority(int pri) throws IllegalArgumentException {
        if ((pri < 0) || (pri > 20)) {
            throw new IllegalArgumentException("Priority out of range (0-20) " + pri);
        }
        priority = pri;
    }

    /**
     * Enable heart beats on the device
     */
    public void enableHeartbeat() {
        this.heartbeatState = true;
    }

    /**
     * disable heart beats on the device
     */
    public void disableHeartbeat() {
        this.heartbeatState = false;
    }

    /**
     * Return whether heartbeats are enabled on the device
     * @return the heartbeat status for the device
     */
    public boolean isHeartbeatEnabled() {
        return this.heartbeatState;
    }

    /**
     * Get the heartbeat URI setting for the device
     * @return the String URI for heartbeats toward the device
     */
    public String getHeartbeatUri() {
    return heartbeatUri;
    }

    /**
     * Set the heartbeat URI for the device
     * @param uri String uri (sip:x@y)
     */
    public void setHeartbeatUri(String uri) {
    heartbeatUri = uri;
    }

    /**
     * Comparison for equals is of IDs
     *
     * @param dev - device to compare against
     *
     * @return - true if object match (ids are equal)
     */
    public boolean equals(ExternalDevice dev) {
    return id.equals(dev.getId());
    }
}

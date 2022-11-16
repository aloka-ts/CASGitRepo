/*
 * Policy.java
 *
 * Created on November 5, 2004, 10:54 AM
 */
package com.baypackets.ase.replication;

import java.io.Serializable;


/**
 * This class represents a replication policy.
 *
 * @see com.baypackets.ase.replication.PolicyManager
 */
public class Policy implements Serializable {

	private static final long serialVersionUID = 533442241824L;
    // Replication action types
    public static final short NO_REPLICATION = 0;    
    public static final short REPLICATE = 1;
    public static final short FULL_REPLICATION = 2;
    
    // Policy types
    public static final short APPLICATION_POLICY = 3;
    public static final short CONTAINER_POLICY = 4;
    
    private String id;
    private short type;
    private short replicationType;
    private String creatorId;
    private String eventId;
    
    
    /**
     * Returns this policy's unique id.
     */
    public String getId() {
        return id;
    }
    
    
    /**
     *
     */
    public void setId(String id) {
        this.id = id;
    }
    
    
    /**
     * Returns a value indicating the type of entity that created this
     * policy (i.e. Application or Container).  The range of possible return
     * values from this method are enumerated by this class's public static 
     * short constants.
     */
    public short getType() {
        return type;
    }
    
    
    /**
     *
     */
    public void setType(short type) {
        this.type = type;
    }
    
    
    /**
     * Returns the ID of the entity that created this policy.
     * (ex. the name of an application)
     */
    public String getCreatorId() {
        return creatorId;
    }
    
    
    /**
     *
     */
    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }
    
    
    /**
     * Returns the ID of an event that should be responded to by either a full
     * or partial replication or no replication at all.  The course of action
     * to take is determined by the return value of the "getReplicationType()"
     * method.
     */
    public String getReplicationEventId() {
        return eventId;
    }
    
    
    /**
     *
     */
    public void setReplicationEventId(String eventId) {
        this.eventId = eventId;
    }
    
    
    /**
     * Returns the type of replication that should occur in response to the 
     * event specified by the return value of the "getReplicationEventId()"
     * method.  The possible return values for this method are enumerated by
     * this class's public static short constants. 
     */
    public short getReplicationType() {
        return replicationType;
    }
    
    
    /**
     *
     */
    public void setReplicationType(short replicationType) {
        this.replicationType = replicationType;
    }
    
    
    /**
     *
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("id: ");
        buffer.append(id);
        buffer.append(", type: ");
        buffer.append(String.valueOf(type));
        buffer.append(", replicationType: ");
        buffer.append(String.valueOf(replicationType));
        buffer.append(", creatorId: ");
        buffer.append(creatorId);
        buffer.append(", eventId: ");
        buffer.append(eventId);
        return buffer.toString();
    }
    
}

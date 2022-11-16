/*
 * PolicyManager.java
 *
 * Created on November 5, 2004, 10:54 AM
 */
package com.baypackets.ase.replication;

import java.util.Collection;

import org.w3c.dom.Element;

import com.baypackets.ase.spi.replication.ReplicationEvent;


/**
 * This interface defines an object that manages a repository of replication
 * policies.
 *
 * @see com.baypackets.ase.replication.Policy
 */
public interface PolicyManager {
        
    // Policy lookup modes
    public static final short NORMAL = 0;
    public static final short CONTAINER_ONLY = 1;
    public static final short ALWAYS_REPLICATE = 2;
    public static final short NEVER_REPLICATE = 3;        
    
    
    /**
     * Adds a new replication policy to the repository.
     */
    public void addPolicy(Policy policy);
        
    /**
     * Removes the specified policy from the repository.
     *
     * @param policyID  The primary key of the Policy to remove.
     * @return the Policy object that was removed or null if none exists
     * for the given ID.
     */
    public Policy removePolicy(String policyID);
            
    /**
     * Removes all replication policies for the specified application.
     */
    public boolean removePoliciesForApp(String appName);
            
    /**
     * Returns a value indicating the type of replication to be 
     * performed (if any) for the given event.  The possible return values 
     * are enumerated as public static constants of the Policy class.
     */
    public short query(ReplicationEvent event); 
    
    /**
     * Sets the policy lookup mode.  This has an effect on what the return 
     * value of the "query()" method will be.  The range of possible values
     * for the given "mode" parameter are enumerated by this interface's public
     * static constants.
     *
     * <p>
     * NORMAL - Do a lookup of both the container and application policies when
     * the "query" method is invoked.
     * CONTAINER_ONLY - Do a lookup of only the container policies and whatever
     * policies that belong to the applications specified by the "setAppNames"
     * method.  
     * ALWAYS_REPLICATE - Always indicate that a full replication should be 
     * performed when queried regardless of both container and application 
     * policies.
     * NEVER_REPLICATE - Always indicate no replication when queried regardless
     * of policies.
     * </p>
     */
    public void setLookupMode(short mode);
    
    /**
     * Returns the replication policy lookup mode.
     */
    public short getLookupMode();
        
    /**
     * Specifies the set of applications whose replication policies will be 
     * considered when the "query" method is invoked and the policy lookup
     * mode is CONTAINER_ONLY.
     */
    public void setAppNames(Collection appNames);
    
    /**
     * Returns the names of the applications whose replication policies will
     * be considered when the policy lookup mode is CONTAINER_ONLY.
     */
    public Collection getAppNames();
            
    /**
     * Returns the rate at which an active peer's objects should be replicated
     * to a peer that has just come up.
     */
    public int getReplicationPerSec();
        
    /**
     * Parses the given DOM object for replication policies.
     *
     * @param appName  The name of the application for which the parsed
     * policies belong.  If this is null, the policies are considered to
     * be container policies.
     * @return  A Collection of Policy objects.
     * @see com.baypackets.ase.replication.Policy
     */
    public Collection parseForPolicies(Element elem, String appName);
    
    /**
     * Returns the policy type for the specified String value.
     * @param replicationType
     * @return
     */
    public short getReplicationType(String replicationType);
            
}

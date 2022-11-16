/*
 * Created on Oct 22, 2004
 *
 */
package com.baypackets.ase.spi.replication;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 */
public interface Replicable extends Externalizable {
	
	public String getReplicableId();
	
	public void setReplicableId(String replicableId);
	
	/**
    * This method is used by the replicable to notify the replication manager whether
    * the replicable is in a stable state to be replicated or not.
    * 
    * <p>
    * Ex. the replicable is in the midst of a transaction, and it would make sense to
    * replicate it only when the transaction is completed.
    * The replication manager will skip replication of this object if this method returns false.
    */
	public boolean isReadyForReplication();
	
	public void readIncremental(ObjectInput in) throws IOException, ClassNotFoundException;
	
	public void writeIncremental(ObjectOutput out, int replicationType)throws IOException;

	public void activate(ReplicationSet parent);

    /** 
     * This method is used, if the standby machine whom an AseIc is replicated to,
     *  wants to do some processing with this AseIc object prior to the activation 
     * of it.
     */

	public void partialActivate(ReplicationSet parent);
	
	/**
    * This method will be used by the Replication Manager
    * to check whether anything has changed from the previous replication or not.
    * 
    * <p>
    * In case of replication mode = INCREMENTAL and isReplicationReqd() returns false,
    * The replication manager will not consider this replicable for replication.
    * In case of replication mode = FULL, the replication manager calls this replicable
    * for serialization irrespective of the replicationRequired flag.
    * Usually the replicables set this flag when there is any state change and resets this
    * flag when it gets the replicationCompleted callback.
    */
   public boolean isModified();

   public boolean isNew();
   
   /**
    * This is the callback method that would be called when the
    * the replication packet is created.
    * The replicable object will make use of this callback to
    * reset replicationRequired flag.
    */
   public void replicationCompleted();
   
   public void replicationCompleted(boolean noReplication);
   
   public boolean isFirstReplicationCompleted();
   
   public void setFirstReplicationCompleted(boolean isFirstReplicationComplete);
   
  
}

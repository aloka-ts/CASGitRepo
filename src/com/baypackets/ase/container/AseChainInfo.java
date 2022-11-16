/*
 * Created on Jan 13, 2005
 *
 */
package com.baypackets.ase.container;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
import org.apache.log4j.Logger;

import com.baypackets.ase.spi.replication.Replicable;
import com.baypackets.ase.spi.replication.ReplicationSet;
import com.baypackets.ase.util.AseStrings;

/**
 * @author Ravi
 */

@DefaultSerializer(ExternalizableSerializer.class)
public class AseChainInfo implements Replicable, Cloneable {

	private static final long serialVersionUID=-384270838354L;
	/**
	 * This is index into list of application chains in associated IC. This,
	 * in combination with <code>qValue</code>, determines position of
	 * associated protocol session object in list of application chains.
	 * Negative value indicates an invalid/uninitialized chain-id.
	 */
	private int chainId = -1;

	/**
	 * This field contains flag to indicate if this session is chained at downstream
	 */
 	private boolean chainedDownstream = false;
 	private boolean prevChainedDownstream = false;
	private static final Logger logger = Logger.getLogger(AseChainInfo.class);
	
	/**
	 * This field contains flag to indicate if this session is chained at upstream
	 */
 	private boolean chainedUpstream = false;
 	private boolean prevChainedUpstream = false;
	
	/**
	 * A value indicating the relative order in the chain
	 */
  	private int qValue = 0;
	
	/**
	 * A flag indicating whether this session should participate in chaining
	 * By default the value is set to true, so that all sessions participate in chaining
	 * But applications like non Record Routing proxy set this flag FALSE, 
	 * so that this session will not be in the Application Chain Path for
	 * Non INITIAL requests (except ACK for INVITE). 
	 * ACKs are processed using the transaction rather than the Application Chains.   
	 */
	private boolean chainingReqd = true;

	private boolean chainableBothSides = false;
	
	private boolean _new = true;
	private boolean _modified = true;
	private String replicableId;
	private boolean mFirstReplicationCompleted = false;

	public AseChainInfo(){}
	
	public AseChainInfo(AseChainInfo from) {
		this.chainId			= from.chainId;
		this.chainedDownstream	= from.chainedDownstream;
		this.chainedUpstream	= from.chainedUpstream;
		this.chainingReqd		= from.chainingReqd;
		this.qValue				= from.qValue;
		this.chainableBothSides	= from.chainableBothSides;
	}


	public boolean isChainedDownstream() {
		return chainedDownstream;
	}
	
	public boolean isChainedUpstream() {
		return chainedUpstream;
	}

	public boolean wasChainedDownstream() {
		return prevChainedDownstream;
	}
	
	public boolean wasChainedUpstream() {
		return prevChainedUpstream;
	}

	public void setChainedDownstream(boolean chained) {
		synchronized (this) {
			chainedDownstream = chained;
			if(chained) {
				prevChainedDownstream = true;
			}
			this._modified = true;
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("setChainedDownstream "+chainedDownstream);
		}
	}

	public void setChainedUpstream(boolean chained) {
		synchronized (this) {
			chainedUpstream = chained;
			if(chained) {
				prevChainedUpstream = true;
			}
			this._modified = true;
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("setChainedUpstream "+chained);
		}
	}

	public int getQValue() {
		return qValue;
	}

	public boolean isChainingReqd() {
		return chainingReqd;
	}

	public void setQValue(int i) {
		synchronized (this) {
			qValue = i;
			this._modified = true;
		}
	}

	public void setChainingReqd(boolean b) {
		synchronized (this) {
			chainingReqd = b;
			this._modified = true;
		}
	}

	public int getChainId() {
		return chainId;
	}

	public void setChainId(int cid) {
		synchronized (this) {
			chainId = cid;
			this._modified = true;
		}
	}

	public boolean isChainableBothSides() {
		return chainableBothSides;
	}

	public void setChainableBothSides() {
		synchronized (this) {
			chainableBothSides = true;
			this._modified = true;
		}
	}

	public Object clone()
		throws CloneNotSupportedException {
		AseChainInfo copy = (AseChainInfo)super.clone();

		// Reset chainId, qValue and downstream info
		copy.chainId = -1;
		copy.qValue = 0;
		copy.chainedDownstream = false;
		copy._new = true;
		copy._modified = true;
		copy.mFirstReplicationCompleted = false;

		if(logger.isDebugEnabled()){
			logger.debug("clone() ");
		}
		return copy;
	}

	///// Replicable Interface methods starts 

	public void partialActivate(ReplicationSet parent) {
		// NOOP
	}
	public void activate(ReplicationSet parent) {
		// NOOP
	}

	public String getReplicableId() {
		return this.replicableId;
	}

	public boolean isModified() {
		return this._modified;
	}

	public boolean isNew() {
		return this._new;
	}

	public boolean isReadyForReplication() {
		return true;
	}

	public void readIncremental(ObjectInput in) throws IOException, ClassNotFoundException {
		synchronized (this) {
			this.chainId 		= in.readInt();
			this.qValue			= in.readInt();
			this.chainedDownstream	= in.readBoolean();
			this.prevChainedDownstream = in.readBoolean();
			this.chainedUpstream	= in.readBoolean();
			this.prevChainedUpstream = in.readBoolean();
			this.chainingReqd		= in.readBoolean();
			this.chainableBothSides	= in.readBoolean();
			
		}
	}

	public void replicationCompleted() {
		if (logger.isDebugEnabled()) {
			logger.debug("::AseChainInfo: replicationCompleted(): Setting _new = false");
		}
		replicationCompleted(false);
		
	}
	
	public void replicationCompleted(boolean noReplication) {
		if(logger.isDebugEnabled())
			logger.debug("::AseChainInfo: replicationCompleted()"+noReplication);
		
		if(!noReplication){
//			this._new = false;
//			this._modified = false;
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("::Leaving AseChainInfo replicationCompleted():"+noReplication);
		}
	}
	

	public void setReplicableId(String replicableId) {
		this.replicableId = replicableId;
	}

	public void writeIncremental(ObjectOutput out, int  replicationType) throws IOException {
		synchronized (this) {
			out.writeInt(this.chainId);
			out.writeInt(this.qValue);
			out.writeBoolean(this.chainedDownstream);
			out.writeBoolean(this.prevChainedDownstream);
			out.writeBoolean(this.chainedUpstream);
			out.writeBoolean(this.prevChainedUpstream);
			out.writeBoolean(this.chainingReqd);
			out.writeBoolean(this.chainableBothSides);
			this._new = false;
			this._modified = false;
		}
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		synchronized (this) {
			this.replicableId = (String)in.readObject();
			this.chainId	=	in.readInt();
			this.qValue	=	in.readInt();
			this.chainedDownstream = in.readBoolean();
			this.prevChainedDownstream = in.readBoolean();
			this.chainedUpstream	=	in.readBoolean();
			this.prevChainedUpstream	=	in.readBoolean();
			this.chainingReqd	=	in.readBoolean();
			this.chainableBothSides	=	in.readBoolean();
		}
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		synchronized (this) {
			if (logger.isDebugEnabled()){
				logger.debug("setFirstReplicationCompleted(true); ");
			}
			this.setFirstReplicationCompleted(true);
			out.writeObject(this.replicableId);
			out.writeInt(this.chainId);
			out.writeInt(this.qValue);
			out.writeBoolean(this.chainedDownstream);
			out.writeBoolean(this.prevChainedDownstream);
			out.writeBoolean(this.chainedUpstream);
			out.writeBoolean(this.prevChainedUpstream);
			out.writeBoolean(this.chainingReqd);
			out.writeBoolean(this.chainableBothSides);
			this._new = false;
			this._modified = false;
		}
		
	}
	
	@Override
	public boolean isFirstReplicationCompleted() {
		return mFirstReplicationCompleted;
	}

	@Override
	public void setFirstReplicationCompleted(boolean isFirstReplicationCompleted) {
		mFirstReplicationCompleted=isFirstReplicationCompleted;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("ChainInfo(");
		buffer.append("replicableId=");
		buffer.append(this.replicableId);
		buffer.append("chainId=");
		buffer.append(this.chainId);
		buffer.append(",chainedUpstream=");
		buffer.append(this.chainedUpstream);
		buffer.append(",chainedDownstream=");
		buffer.append(this.chainedDownstream);
		buffer.append(",chainingReqd=");
		buffer.append(this.chainingReqd);
		buffer.append(",chainableBothSides=");
		buffer.append(this.chainableBothSides);
		buffer.append(",QValue=");
		buffer.append(this.qValue);
		buffer.append(AseStrings.PARENTHESES_CLOSE);
		return buffer.toString();
	}
}

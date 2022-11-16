/*
 * Created on Oct 26, 2004
 *
 */
package com.baypackets.ase.replication;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Serializable;

import org.apache.log4j.Logger;

import com.baypackets.ase.channel.ChannelException;
import com.baypackets.ase.channel.PeerMessage;
import com.baypackets.ase.control.MessageTypes;
import com.baypackets.ase.util.AseStrings;


/**
 * @author Ravi
 */
public class ReplicationMessage extends PeerMessage implements Serializable{
	
	private transient static Logger logger = Logger.getLogger(ReplicationMessage.class);

	public static final short CLEANUP = 0;
	public static final short CREATE = 1;
	public static final short REPLICATE = 2;
	public static final short REINIT = 3;
	
	
	
	private String contextId;
	
	/**
	 * Added to enable hashing on repId if present
	 */
	private String repId;
	
	private short sequenceNo;
	private short action;
	private short mode;
	private long replicationTime;
	private String partitionId;
	private String[] destinations = null;
	
	private boolean activate=false;
	
	
	
	
	public boolean isActivate() {
		return activate;
	}

	public void setActivate(boolean activate) {
		this.activate = activate;
	}
	
	public ReplicationMessage(){
		super();
	}

	public ReplicationMessage(short mode) throws ChannelException{
		super(MessageTypes.REPLICATION_MESSAGE, mode);
	}

	public int getIndex(){
		if(repId == null || repId.trim().equals("")){
			if(logger.isDebugEnabled()){
				logger.debug("use contextID for replication hashing");
			}
			return this.contextId.hashCode();
		}else{
			if(logger.isDebugEnabled()){
				logger.debug("use repId for replication hashing");
			}
			return this.repId.hashCode();
		}
	}

	public void readHeader(DataInput dataIn) throws ChannelException{
		try{
			this.contextId = dataIn.readUTF();
			this.sequenceNo = dataIn.readShort();
			this.repId = dataIn.readUTF();
			this.action = dataIn.readShort();
			this.mode = dataIn.readShort();
			this.replicationTime = dataIn.readLong();
			this.partitionId = dataIn.readUTF();
			this.destinations = new String[dataIn.readInt()];
			for(int i=0; i<destinations.length;i++){
				this.destinations[i] = dataIn.readUTF();
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new ChannelException(e.getMessage());
		}
	}

	public void writeHeader(DataOutput dataOut) throws ChannelException {
		try{
			dataOut.writeUTF(this.contextId != null ? this.contextId : "");
			dataOut.writeShort(this.sequenceNo);
			dataOut.writeUTF(this.repId != null ? this.repId : "");
			dataOut.writeShort(this.action);
			dataOut.writeShort(this.mode);
			dataOut.writeLong(this.replicationTime);
			dataOut.writeUTF(this.partitionId != null ? this.partitionId : "");
			int size = this.destinations != null ? this.destinations.length : 0;
			dataOut.writeInt(size);
			for(int i=0; i<size;i++){
				dataOut.writeUTF(this.destinations[i] == null ? "" : this.destinations[i]);
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new ChannelException(e.getMessage());
		}
	}

	public short getAction() {
		return action;
	}

	public String getPartitionId() {
		return partitionId;
	}

	public String getContextId() {
		return contextId;
	}

	public short getSequenceNo() {
		return sequenceNo;
	}

	public void setAction(short s) {
		action = s;
	}

	public void setPartitionId(String string) {
		partitionId = string;
	}

	public void setContextId(String i) {
		contextId = i;
	}

	public void setSequenceNo(short s) {
		sequenceNo = s;
	}
	
	public short getMode() {
		return mode;
	}

	public void setMode(short s) {
		mode = s;
	}

	public static String getActionString(short action){
		String str;
		switch(action){
			case CLEANUP:
				str = "Cleanup";
				break;
			case CREATE:
				str = "Create";
				break;
			case REPLICATE:
				str="Replicate";
				break;
			case REINIT:
				str="Re-init";
				break;
			default:
				str="Unknown";
		}
		return str;
	}

	public String[] getDestinations() {
		return destinations;
	}

	public long getReplicationTime() {
		return replicationTime;
	}

	public void setDestinations(String[] strings) {
		destinations = strings;
	}

	public void setReplicationTime(long l) {
		replicationTime = l;
	}
	
	/**
	 * @param repId the repId to set
	 */
	public void setRepId(String repId) {
		this.repId = repId;
	}

	/**
	 * @return the repId
	 */
	public String getRepId() {
		return repId;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("ReplicationMessage[");
		buffer.append("id=");
		buffer.append(this.contextId);
		buffer.append(",Repid=");
		buffer.append(((this.repId != null) ? repId : ""));
		buffer.append(",seq=");
		buffer.append(this.sequenceNo);
		buffer.append(",partition=");
		buffer.append(this.partitionId);
		buffer.append(",action=");
		buffer.append(this.action);
		buffer.append(",mode=");
		buffer.append(this.mode);
		buffer.append(",destinations=");
		for(int i=0;this.destinations != null && i<this.destinations.length;i++){
			buffer.append(i==0 ? AseStrings.BLANK_STRING : AseStrings.COLON);
			buffer.append(destinations[i]);
		}
		buffer.append(AseStrings.SQUARE_BRACKET_CLOSE);
		return buffer.toString();
	}

}

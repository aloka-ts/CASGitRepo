package com.baypackets.ase.router.acm;

import java.io.DataInput;
import java.io.DataOutput;

import org.apache.log4j.Logger;

import com.baypackets.ase.channel.ChannelException;
import com.baypackets.ase.channel.PeerMessage;
import com.baypackets.ase.control.MessageTypes;

public class AppDataMessage extends PeerMessage{

	private static Logger logger = Logger.getLogger(AppDataMessage.class);

    // Message types
    public static final short APP_CHAIN_MAP_UPDATED = 0;
    public static final short ACK = 1;
    
    private String callId;
    
    private String currentChainedServices=null;
    private String allTriggredServices=null;
    
    public String getAllTriggredServices() {
		return allTriggredServices;
	}

	public void setAllTriggredServices(String allTriggredServices) {
		this.allTriggredServices = allTriggredServices;
	}

	public String getCurrentChainedServices() {
		return currentChainedServices;
	}

	public void setCurrentChainedServices(String currentChainedServices) {
		this.currentChainedServices = currentChainedServices;
	}

	public String getCallId() {
		return callId;
	}

	public void setCallId(String callId) {
		this.callId = callId;
	}

	private short msgType;
    private String senderId;
        
    public AppDataMessage(short mode) throws ChannelException{
        super(MessageTypes.REPLICATION_MESSAGE, mode);
    }

	public int getIndex(){
		return 1;
	}

    public void readHeader(DataInput dataIn) throws ChannelException {
        try {
            this.callId = dataIn.readUTF(); 
            this.currentChainedServices = dataIn.readUTF();
            this.allTriggredServices = dataIn.readUTF();
            this.senderId = dataIn.readUTF();
            this.msgType = dataIn.readShort();
        } catch(Exception e) {
	    logger.error(e.getMessage(), e);
            throw new ChannelException(e.getMessage());
        }
    }

    public void writeHeader(DataOutput dataOut) throws ChannelException {
        try {
            dataOut.writeUTF(this.callId == null ? "" : this.callId);
            dataOut.writeUTF(this.currentChainedServices == null ? "" : this.currentChainedServices);
            dataOut.writeUTF(this.allTriggredServices == null ? "" : this.allTriggredServices);
            dataOut.writeUTF(this.senderId == null ? "" : this.senderId);   
            dataOut.writeShort(this.msgType);
        } catch(Exception e) {
	    logger.error(e.getMessage(), e);
            throw new ChannelException(e.getMessage());
	}
    }
    
    @Override
    public String toString(){
    	
    	StringBuffer buffer = new StringBuffer();
		buffer.append("CallId =");
		buffer.append(this.callId);
		buffer.append(" currentChainedServices =");
		buffer.append(this.currentChainedServices);
		buffer.append(" allTriggredServices =");
		buffer.append(this.allTriggredServices);
		buffer.append(", senderId =");
		buffer.append(this.senderId);
		buffer.append(", msgType =");
		buffer.append(this.msgType);
		return buffer.toString();
    	
    }

    public short getMsgType() {
        return msgType;
    }
    
    public void setMsgType(short type) {
        this.msgType = type;
    }
    
    public String getSenderId() {
        return senderId;
    }
    
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

}

package com.baypackets.ase.sysapps.cim.util;

import java.io.Serializable;

public class MessageData implements Serializable {

	private static final long serialVersionUID = 166767667L;
	private String sender;
	private boolean isSenderAconyx;
	private String senderAOR;
	private String receiver;
	private boolean isReceiverAconyx;
	private String receiverAOR;
	
	public MessageData(String sender, String receiver) {		
		this.sender = sender;
		this.receiver = receiver;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public boolean isSenderAconyx() {
		return isSenderAconyx;
	}

	public void setSenderAconyx(boolean isSenderAconyx) {
		this.isSenderAconyx = isSenderAconyx;
	}

	public String getSenderAOR() {
		return senderAOR;
	}

	public void setSenderAOR(String senderAOR) {
		this.senderAOR = senderAOR;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public boolean isReceiverAconyx() {
		return isReceiverAconyx;
	}

	public void setReceiverAconyx(boolean isReceiverAconyx) {
		this.isReceiverAconyx = isReceiverAconyx;
	}

	public String getReceiverAOR() {
		return receiverAOR;
	}

	public void setReceiverAOR(String receiverAOR) {
		this.receiverAOR = receiverAOR;
	}
}

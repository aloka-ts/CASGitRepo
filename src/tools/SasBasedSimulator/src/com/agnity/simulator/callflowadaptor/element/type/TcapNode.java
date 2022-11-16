package com.agnity.simulator.callflowadaptor.element.type;

import jain.protocol.ss7.tcap.TcapConstants;

import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.utils.Constants;


public abstract class TcapNode extends Node {
	//for INAP messages
	private boolean isLastMessage;
	private String dialogAs;
	private String  opCodeString;
	private int dialogType;
	
	public TcapNode(String type, String opCodeString){
		super(type);
		this.setLastMessage(false);
		this.setOpCodeString(opCodeString);
	}


	/**
	 * @param isLastMessage the isLastMessage to set
	 */
	public void setLastMessage(boolean isLastMessage) {
		this.isLastMessage = isLastMessage;
	}


	/**
	 * @return the isLastMessage
	 */
	public boolean isLastMessage() {
		return isLastMessage;
	}


	/**
	 * @param dialogAs the dialogAs to set
	 */
	public void setDialogAs(String dialogAs) {
		this.dialogAs = dialogAs.toLowerCase();
		if(this.dialogAs.equals(Constants.DIALOG_BEGIN)){
			dialogType = TcapConstants.PRIMITIVE_BEGIN;
		}else if(this.dialogAs.equals(Constants.DIALOG_END)){
			dialogType = TcapConstants.PRIMITIVE_END;
		}else if(this.dialogAs.equals(Constants.DIALOG_CONTINUE)){
			dialogType = TcapConstants.PRIMITIVE_CONTINUE;
		}else if(this.dialogAs.equals(Constants.DIALOG_UNIDIR)){
			dialogType = TcapConstants.PRIMITIVE_UNIDIRECTIONAL;
		}
		
	}


	/**
	 * @return the dialogAs
	 */
	public String getDialogAs() {
		return dialogAs;
	}


	/**
	 * @param opCodeString the opCodeString to set
	 */
	public void setOpCodeString(String opCodeString) {
		this.opCodeString = opCodeString;
	}


	/**
	 * @return the opCodeString
	 */
	public String getOpCodeString() {
		return opCodeString;
	}


	/**
	 * @param dialogType the dialogType to set
	 */
	protected void setDialogType(int dialogType) {
		this.dialogType = dialogType;
	}


	/**
	 * @return the dialogType
	 */
	public int getDialogType() {
		return dialogType;
	}

}

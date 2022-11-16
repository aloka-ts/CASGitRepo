package com.camel.CAPMsg;

import java.util.LinkedList;
import java.util.List;

import jain.protocol.ss7.tcap.ComponentReqEvent;
import jain.protocol.ss7.tcap.DialogueReqEvent;

/**
 * This class will contain all the components and dialogue
 * which are to be sent to network
 * @author nkumar
 *
 */
public class SasCapMsgsToSend {

	/**
	 * A ComponentReqEvent is the superclass of all 
	 * Component request primitives like ErrorReqEvent,InvokeReqEvent, 
	 * RejectReqEvent, ResultReqEvent.
	 */
	private List<ComponentReqEvent> compReqEvents = new LinkedList<ComponentReqEvent>();
	/**
	 * A DialogueReqEvent is the superclass of all 
	 * Dialogue Request primitives like BeginReqEvent, 
	 * ContinueReqEvent, EndReqEvent
	 */
	private DialogueReqEvent dlgReqEvent ;
	
	public List<ComponentReqEvent> getCompReqEvents() {
		return compReqEvents;
	}
	public void setCompReqEvents(List<ComponentReqEvent> compReqEvents) {
		this.compReqEvents = compReqEvents;
	}
	public DialogueReqEvent getDlgReqEvent() {
		return dlgReqEvent;
	}
	public void setDlgReqEvent(DialogueReqEvent dlgReqEvent) {
		this.dlgReqEvent = dlgReqEvent;
	}
}

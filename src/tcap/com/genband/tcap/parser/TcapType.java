package com.genband.tcap.parser;
import jain.protocol.ss7.sccp.StateIndEvent;
import jain.protocol.ss7.sccp.StateReqEvent;
import jain.protocol.ss7.tcap.ComponentIndEvent;
import jain.protocol.ss7.tcap.DialogueIndEvent;
import jain.protocol.ss7.tcap.DialogueReqEvent;
import jain.protocol.ss7.tcap.component.LocalCancelIndEvent;
import jain.protocol.ss7.tcap.component.RejectIndEvent;

import java.util.ArrayList;
import java.util.List;


public class TcapType {

	
    protected DialogueReqEvent dialogueReqEvent;
   
    protected DialogueIndEvent dialogueIndEvent;
   
    protected StateReqEvent stateReqEvent;
   
    protected StateIndEvent stateIndEvent;
   
    protected LocalCancelIndEvent localCancelIndEvent;
    
    protected RejectIndEvent rejectIndEvent;   

	protected List<ComponentIndEvent> componentIndEvent ;
    
    protected ConfigurationMsgDataType configMsg ;

	public ConfigurationMsgDataType getConfigMsg() {
		return configMsg;
	}

	public void setConfigMsg(ConfigurationMsgDataType configMsg) {
		this.configMsg = configMsg;
	}

	public DialogueReqEvent getDialogueReqEvent() {
		return dialogueReqEvent;
	}

	public void setDialogueReqEvent(DialogueReqEvent dialogueReqEvent) {
		this.dialogueReqEvent = dialogueReqEvent;
	}

	public DialogueIndEvent getDialogueIndEvent() {
		return dialogueIndEvent;
	}

	public void setDialogueIndEvent(DialogueIndEvent dialogueIndEvent) {
		this.dialogueIndEvent = dialogueIndEvent;
	}

	public StateReqEvent getStateReqEvent() {
		return stateReqEvent;
	}

	public void setStateReqEvent(StateReqEvent stateReqEvent) {
		this.stateReqEvent = stateReqEvent;
	}

	public StateIndEvent getStateIndEvent() {
		return stateIndEvent;
	}

	public void setStateIndEvent(StateIndEvent stateIndEvent) {
		this.stateIndEvent = stateIndEvent;
	}

	public LocalCancelIndEvent getLocalCancelIndEvent() {
		return localCancelIndEvent;
	}

	public void setLocalCancelIndEvent(LocalCancelIndEvent localCancelIndEvent) {
		this.localCancelIndEvent = localCancelIndEvent;
	}

	public RejectIndEvent getRejectIndEvent() {
		return rejectIndEvent;
	}

	public void setRejectIndEvent(RejectIndEvent rejectIndEvent) {
		this.rejectIndEvent = rejectIndEvent;
	}
	
	 public List<ComponentIndEvent> getComponentIndEvent() {
	        if (componentIndEvent == null) {
	            componentIndEvent = new ArrayList<ComponentIndEvent>();
	        }
	        return this.componentIndEvent;
	 }

	public void setComponentIndEvent(List<ComponentIndEvent> componentIndEvent) {
		this.componentIndEvent = componentIndEvent;
	}
	 
	@Override
	public String toString() {
		return "TcapType [componentIndEvent=" + componentIndEvent
				+ ", configMsg=" + configMsg + ", dialogueIndEvent="
				+ dialogueIndEvent + ", dialogueReqEvent=" + dialogueReqEvent
				+ ", localCancelIndEvent=" + localCancelIndEvent
				+ ", rejectIndEvent=" + rejectIndEvent + ", stateIndEvent="
				+ stateIndEvent + ", stateReqEvent=" + stateReqEvent + "]";
	}
    
   // protected TcapConfigType configuration;
    
    
}

package com.sas.cap;

import jain.protocol.ss7.tcap.DialogueIndEvent;

import java.util.HashMap;

public class EventData {

    public DialogueIndEvent dlgIndEvent;
    HashMap<String, byte[]> opCodeParamsMap = new HashMap<String, byte[]>();

    /**
     * @param dlgIndEvent
     */
    public EventData(DialogueIndEvent dlgIndEvent) {
	this.dlgIndEvent = dlgIndEvent;
    }

    public void setOpcodeParams(String opCode, byte[] params) {
	opCodeParamsMap.put(opCode, params);
    }

    public DialogueIndEvent getDlgIndEvent() {
	return dlgIndEvent;
    }

    public HashMap<String, byte[]> getOpCodeParamsMap() {
	return opCodeParamsMap;
    }

}

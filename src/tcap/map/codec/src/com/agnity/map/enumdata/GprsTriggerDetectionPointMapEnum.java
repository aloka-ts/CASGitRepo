package com.agnity.map.enumdata;

public enum GprsTriggerDetectionPointMapEnum {
	ATTACH(1),
	ATTACH_CHANCE_OF_POSITION(2),
	PDP_CONTEXT_ESTABLISHED(11),
	PDP_CONTEXT_ESTTABLISHMENT_ACK(12),
	PDP_CONTEXT_CHANGE_OF_POSITION(14);
	
	
	private int code;
	private GprsTriggerDetectionPointMapEnum(int code){
		this.code = code;
	}
	
	public int getCode(){
		return this.code;
	}
	
	public static GprsTriggerDetectionPointMapEnum getValue(int tag) {
		switch(tag) {
		case 1: return ATTACH;
		case 2: return ATTACH_CHANCE_OF_POSITION;
		case 11: return PDP_CONTEXT_ESTABLISHED;
		case 12: return PDP_CONTEXT_ESTTABLISHMENT_ACK;
		case 14: return PDP_CONTEXT_CHANGE_OF_POSITION;
		default: return null;
		}
	}
}

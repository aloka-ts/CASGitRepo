package com.agnity.map.enumdata;

public enum OBcsmTriggerDetectionPointMapEnum {
	COLLECTED_INFO(2),
	ROUTE_SELECTED_FAILURE(4);
	
	private int code;
	private OBcsmTriggerDetectionPointMapEnum(int code){
		this.code = code;
	}
	
	public int getCode(){
		return this.code;
	}
	
	public static OBcsmTriggerDetectionPointMapEnum getValue(int tag) {
		switch(tag) {
		case 2: return COLLECTED_INFO;
		case 4: return ROUTE_SELECTED_FAILURE;
		default: return null;
		}
	}
}

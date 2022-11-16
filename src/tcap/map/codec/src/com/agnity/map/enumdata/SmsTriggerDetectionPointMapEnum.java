package com.agnity.map.enumdata;

public enum SmsTriggerDetectionPointMapEnum {
	SMS_COLLECTED_INFO(1),
	SMS_DELIVERY_REPORT(2);
	
	private int code;
	private SmsTriggerDetectionPointMapEnum(int code){
		this.code = code;
	}
	
	public int getCode(){
		return this.code;
	}
	
	public static SmsTriggerDetectionPointMapEnum getValue(int tag) {
		switch(tag) {
		case 1: return SMS_COLLECTED_INFO;
		case 2: return SMS_DELIVERY_REPORT;
		default: return null;
		}
	}
}

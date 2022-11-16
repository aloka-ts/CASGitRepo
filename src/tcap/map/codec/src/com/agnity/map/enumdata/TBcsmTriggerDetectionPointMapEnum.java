package com.agnity.map.enumdata;

public enum TBcsmTriggerDetectionPointMapEnum {
	TERM_ATTEMPT_AUTHORIZED(12),
	TBUSY(13),
	TNOANSWER(14);
	
	private int code;
	private TBcsmTriggerDetectionPointMapEnum(int code){
		this.code = code;
	}
	
	public int getCode(){
		return this.code;
	}
	
	public static TBcsmTriggerDetectionPointMapEnum getValue(int tag) {
		switch(tag) {
		case 12: return TERM_ATTEMPT_AUTHORIZED;
		case 13: return TBUSY;
		case 14: return TNOANSWER;
		default: return null;
		}
	}
}

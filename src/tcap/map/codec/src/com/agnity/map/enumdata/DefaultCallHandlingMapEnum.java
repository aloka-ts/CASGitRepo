package com.agnity.map.enumdata;

public enum DefaultCallHandlingMapEnum {

	CONTINUE_CALL(0),
	RELEASE_CALL(1);
	
	private int code;
	private DefaultCallHandlingMapEnum(int code) {
		this.code = code;
	}
	
	public int getCode(){
		return code;
	}
	
	public static DefaultCallHandlingMapEnum getValue(int tag) {
		switch(tag) {
		case 0: return CONTINUE_CALL;
		case 1: return RELEASE_CALL;
		default: return null;
		}
	}
}

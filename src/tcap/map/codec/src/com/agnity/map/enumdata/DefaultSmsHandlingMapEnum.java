package com.agnity.map.enumdata;

public enum DefaultSmsHandlingMapEnum {

	CONTINUE_TRANSACTION(0),
	RELEASE_TRANSACTION(1);
	
	private int code;
	private DefaultSmsHandlingMapEnum(int code) {
		this.code = code;
	}
	
	public int getCode(){
		return code;
	}
	
	public static DefaultSmsHandlingMapEnum getValue(int tag) {
		switch(tag) {
		case 0: return CONTINUE_TRANSACTION;
		case 1: return RELEASE_TRANSACTION;
		default: return null;
		}
	}
}

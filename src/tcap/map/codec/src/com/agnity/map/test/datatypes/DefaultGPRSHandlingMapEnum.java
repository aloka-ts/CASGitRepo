package com.agnity.map.test.datatypes;

public enum DefaultGPRSHandlingMapEnum {
	CONTINUE_TRANSACTION(0),
	RELEASE_TRANSACTION(1);
	
	int code;
	private DefaultGPRSHandlingMapEnum(int code){
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public static DefaultGPRSHandlingMapEnum getValue(int tag){
		switch(tag){
			case 0: return CONTINUE_TRANSACTION;
			case 1: return RELEASE_TRANSACTION;
			default: return null;
		}
	}
}

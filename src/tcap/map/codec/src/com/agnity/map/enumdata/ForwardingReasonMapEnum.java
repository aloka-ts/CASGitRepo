package com.agnity.map.enumdata;

public enum ForwardingReasonMapEnum {
	NOT_REACHABLE(0),
	BUSY(1),
	NO_REPLY(2);
	
	private int code;
	private ForwardingReasonMapEnum(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public static ForwardingReasonMapEnum getValue(int tag) {
		switch(tag) {
		case 0: return NOT_REACHABLE;
		case 1: return BUSY;
		case 2: return NO_REPLY;
		default: return null;
		}
	}
}

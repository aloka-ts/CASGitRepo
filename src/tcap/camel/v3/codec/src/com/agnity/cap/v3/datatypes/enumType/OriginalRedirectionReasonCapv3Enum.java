package com.agnity.cap.v3.datatypes.enumType;


public enum OriginalRedirectionReasonCapv3Enum {

	unknown(0),user_busy(1),
	no_reply(2),
	unconditional(3);
	
	private int code;
	private OriginalRedirectionReasonCapv3Enum(int code){
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static OriginalRedirectionReasonCapv3Enum getValue(int tag) {
	     switch (tag) {
		case 0: return unknown;
		case 1: return user_busy;
		case 2: return no_reply;
		case 3: return unconditional;
		default: return unknown;
		}

	}
	

}


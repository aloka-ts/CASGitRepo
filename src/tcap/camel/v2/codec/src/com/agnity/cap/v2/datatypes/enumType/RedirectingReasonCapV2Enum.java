package com.agnity.cap.v2.datatypes.enumType;

public enum RedirectingReasonCapV2Enum {

	unknown(0),user_busy(1),
	no_reply(2),
	unconditional(3),
	deflection_during_alerting(4),
	deflection_immediate_response(5),
	mobile_subscribernotreachable(6);
	
	private int code;
	private RedirectingReasonCapV2Enum(int code){
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static RedirectingReasonCapV2Enum getValue(int tag) {
	     switch (tag) {
		case 0: return unknown;
		case 1: return user_busy;
		case 2: return no_reply;
		case 3: return unconditional;
		case 4: return deflection_during_alerting;
		case 5: return deflection_immediate_response;
		case 6: return mobile_subscribernotreachable;
		default: return unknown;
		}

	}
	

}

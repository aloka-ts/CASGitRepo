package com.agnity.win.enumdata;

public enum ActionCodeEnum {

	/*
	 * NonASNActionCode (octet 1) Bits HG FE DC BA Value Meaning 00 00 00 00 0
	 * Not used. 00 00 00 01 1 Continue processing. 00 00 00 10 2 Disconnect
	 * call. 00 00 00 11 3 Disconnect call leg. 00 00 01 00 4 Conference Calling
	 * Drop Last Party. 00 00 01 01 5 Bridge call leg(s) to conference call. 00
	 * 00 01 10 6 Drop call leg on busy or routing failure. 00 00 01 11 7
	 * Disconnect all call legs. 00 00 10 00 20 Release leg and redirect
	 * subscriber 00 00 10 00 24 Present display text to calling MS
	 */

	NOT_USED(0), CONTINUE_PROCESSING(1), DISCONNECT_CALL(2), DISCONNECT_CALL_LEG(
			3), CONF_CALLING_DROP_LAST_PARTY(4), BRIDGE_CALL_LEG_TO_CONF_CALL(5), DROP_CALL_LEG_BUSY_ROUTINGFAILURE(
			6), DISCONNECT_ALL_CALL_LEG(7), RELEASE_LEG_REDIRECT_SUBSCRIBER(20), PRESENT_DISPLAY_TEXT_TO_CALLING_MS(
			24);

	private int code;

	private ActionCodeEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static ActionCodeEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return NOT_USED;
		}
		case 1: {
			return CONTINUE_PROCESSING;
		}
		case 2: {
			return DISCONNECT_CALL;
		}
		case 3: {
			return DISCONNECT_CALL_LEG;
		}
		case 4: {
			return CONF_CALLING_DROP_LAST_PARTY;
		}
		case 5: {
			return BRIDGE_CALL_LEG_TO_CONF_CALL;
		}
		case 6: {
			return DROP_CALL_LEG_BUSY_ROUTINGFAILURE;
		}
		case 7: {
			return DISCONNECT_ALL_CALL_LEG;
		}
		case 20: {
			return RELEASE_LEG_REDIRECT_SUBSCRIBER;
		}
		case 24: {
			return PRESENT_DISPLAY_TEXT_TO_CALLING_MS;
		}
		default: {
			return null;
		}
		}
	}

}

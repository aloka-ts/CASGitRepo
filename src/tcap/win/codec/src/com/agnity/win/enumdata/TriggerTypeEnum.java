package com.agnity.win.enumdata;

public enum TriggerTypeEnum {

	/*
	 * NonASNTriggerType (octet 1) Bits HG FE DC BA Value Meaning 00 00 00 00 0
	 * Not used. 00 00 00 01 1 all-Calls 00 00 00 10 2 double-Introducing-Star
	 * 00 00 00 11 3 single-Introducing-Star 00 00 01 00 4
	 * reserved-for-Home-System-Feature-Code. 00 00 01 01 5
	 * double-Introducing-Pound. 00 00 01 10 6 single-Introducing-Pound. 00 00
	 * 01 11 7 revertive-Call. 00 00 10 00 8 a0-Digit 00 00 10 00 9 a1-Digit 00
	 * 00 10 00 10 a2-Digit 00 00 10 00 11 a3-Digit 00 00 10 00 12 a4-Digit 00
	 * 00 10 00 13 a5-Digit 00 00 10 00 14 a6-Digit 00 00 10 00 15 a7-Digit 00
	 * 00 10 00 16 a8-Digit 00 00 10 00 17 a9-Digit 00 00 10 00 18 a10-Digit 00
	 * 00 10 00 19 a11-Digit 00 00 10 00 20 a12-Digit 00 00 10 00 21 a13-Digit
	 * 00 00 10 00 22 a14-Digit 00 00 10 00 23 a15-Digit 00 00 10 00 24
	 * local-Call 00 00 10 00 25 intra-LATA-Toll-Call 00 00 10 00 26
	 * inter-LATA-Toll-Call 00 00 10 00 27 world-Zone-Call 00 00 10 00 28
	 * international Call 00 00 10 00 29 unrecognized number 00 00 10 00 30
	 * prior-Agreement 00 00 10 00 31 specific-Called-Party-Digit-String 00 00
	 * 10 00 32 mobile-Termination 00 00 10 00 33 advanced-Termination 00 00 10
	 * 00 34 location 00 00 10 00 35 location-Termination 00 00 10 00 36
	 * origination-Attempt-Authorized 00 00 10 00 37
	 * calling-Routing-Address-Available 00 00 10 00 38 initial-Termination 00
	 * 00 10 00 37 called-Routing-Address-Available 00 00 10 00 40 o-Answer 00
	 * 00 10 00 41 o-Disconnect 00 00 10 00 42 o-Called-Party-Busy 00 00 10 00
	 * 43 o-No-Answer 00 00 10 00 64 terminating-Resource-Available 00 00 10 00
	 * 65 t-Busy 00 00 10 00 66 t-No-Answer 00 00 10 00 67 t-No-Page-Response 00
	 * 00 10 00 68 t-unRoutable 00 00 10 00 69 t-Answer 00 00 10 00 70
	 * t-Disconnect
	 */

	UNSPECIFIED(0), ALL_CALLS(1), DOUBLE_INTRODUCING_STARS(2), SINGLE_INTRODUCING_STARS(
			3), DOUBLE_INTRODUCING_POUND(5), SINGLE_INTRODUCING_POUND(6), REVERTIVE_CALL(
			7), DIGIT_0(8), DIGIT_1(9), DIGIT_2(10), DIGIT_3(11), DIGIT_4(12), DIGIT_5(
			13), DIGIT_6(14), DIGIT_7(15), DIGIT_8(16), DIGIT_9(17), DIGIT_10(
			18), DIGIT_11(19), DIGIT_12(20), DIGIT_13(21), DIGIT_14(22), DIGIT_15(
			23), INTRA_LATA_TOLL_CALL(25), INTER_LATA_TOLL_CALL(26), LOCAL_CALL(
			24), MOBILE_TERMINATION(32), SPECIFIC_CALLED_PARTY_DIGIT_STRING(31), PRIOR_AGREEMENT(
			30), UNRECOGNIZED_NO(29), INTERNATIONAL_CALL(28), WORLD_ZONE_CALL(
			27), ADVANCED_TERMINATION(33), LOCATION(34), LOCALLY_ALLOWED_SPECIFIC_DIGIT_STRING(
			35), ORIGINATION_ATTEMPT_AUTHORIZED(36), CALLING_ROUTING_ADDRESS_AVAILABLE(
			37), INITIAL_TERMINATION(38), CALLED_ROUTING_ADDRESS_AVAILABLE(39), O_ANSWER(
			40), O_DISCONNECT(41), O_CALLED_PARTY_BUSY(42), O_NO_ANSWER(43), TERMINATING_RESOURCE_AVAILABLE(
			64), T_BUSY(65), T_NOANSWER(66), T_NO_PAGE_RESPONSE(67), T_UNROUTABLE(
			68), T_ANSWER(69), T_DISCONNECT(70), ;

	int code;

	private TriggerTypeEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static TriggerTypeEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return UNSPECIFIED;
		}
		case 1: {
			return ALL_CALLS;
		}
		case 2: {
			return DOUBLE_INTRODUCING_STARS;
		}
		case 3: {
			return SINGLE_INTRODUCING_STARS;
		}
		case 5: {
			return DOUBLE_INTRODUCING_POUND;
		}
		case 6: {
			return SINGLE_INTRODUCING_POUND;
		}
		case 7: {
			return REVERTIVE_CALL;
		}
		case 8: {
			return DIGIT_0;
		}
		case 9: {
			return DIGIT_1;
		}
		case 10: {
			return DIGIT_2;
		}
		case 11: {
			return DIGIT_3;
		}
		case 12: {
			return DIGIT_4;
		}
		case 13: {
			return DIGIT_5;
		}
		case 14: {
			return DIGIT_6;
		}
		case 15: {
			return DIGIT_7;
		}
		case 16: {
			return DIGIT_8;
		}
		case 17: {
			return DIGIT_9;
		}
		case 18: {
			return DIGIT_10;
		}
		case 19: {
			return DIGIT_11;
		}
		case 20: {
			return DIGIT_12;
		}
		case 21: {
			return DIGIT_13;
		}
		case 22: {
			return DIGIT_14;
		}
		case 23: {
			return DIGIT_15;
		}
		case 24: {
			return LOCAL_CALL;
		}
		case 25: {
			return INTRA_LATA_TOLL_CALL;
		}
		case 26: {
			return INTER_LATA_TOLL_CALL;
		}
		case 27: {
			return WORLD_ZONE_CALL;
		}
		case 28: {
			return INTERNATIONAL_CALL;
		}
		case 29: {
			return UNRECOGNIZED_NO;
		}
		case 30: {
			return PRIOR_AGREEMENT;
		}
		case 31: {
			return SPECIFIC_CALLED_PARTY_DIGIT_STRING;
		}
		case 32: {
			return MOBILE_TERMINATION;
		}
		case 33: {
			return ADVANCED_TERMINATION;
		}
		case 34: {
			return LOCATION;
		}
		case 35: {
			return LOCALLY_ALLOWED_SPECIFIC_DIGIT_STRING;
		}
		case 36: {
			return ORIGINATION_ATTEMPT_AUTHORIZED;
		}
		case 37: {
			return CALLING_ROUTING_ADDRESS_AVAILABLE;
		}
		case 38: {
			return INITIAL_TERMINATION;
		}
		case 39: {
			return CALLED_ROUTING_ADDRESS_AVAILABLE;
		}
		case 40: {
			return O_ANSWER;
		}
		case 41: {
			return O_DISCONNECT;
		}
		case 42: {
			return O_CALLED_PARTY_BUSY;
		}
		case 43: {
			return O_NO_ANSWER;
		}
		case 64: {
			return TERMINATING_RESOURCE_AVAILABLE;
		}
		case 65: {
			return T_BUSY;
		}
		case 66: {
			return T_NOANSWER;
		}
		case 67: {
			return T_NO_PAGE_RESPONSE;
		}
		case 68: {
			return T_UNROUTABLE;
		}
		case 69: {
			return T_ANSWER;
		}
		case 70: {
			return T_DISCONNECT;
		}
		default: {
			return null;
		}
		}
	}

}

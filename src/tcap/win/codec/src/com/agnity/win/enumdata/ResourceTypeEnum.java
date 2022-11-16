package com.agnity.win.enumdata;

public enum ResourceTypeEnum {

	/*
	 * ResourceType (octet 1) Bits HG FE DC BA Value Meaning 00 00 00 00 0 Not
	 * used. 00 00 00 01 1 DTMF_TONE_DETECTOR 00 00 00 10 2 AUTOMATIC SPEECH
	 * RECOGNITION - SPEAKER INDEPENDENT DIGITS 00 00 00 11 3 AUTOMATIC SPEECH
	 * RECOGNITION - SPEAKER INDEPENDENT SPEECH USER INTERFACE V1
	 */

	NOT_USED(0), DTMF_TONE_DETECTOR(1), AUTO_SPEECH_RECOG_INDEPENDENT_DIGITS(2), AUTO_SPEECH_RECOG_INDEPENDENT_SPEAKER_UI_V1(
			3);

	private int code;

	private ResourceTypeEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static ResourceTypeEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return NOT_USED;
		}
		case 1: {
			return DTMF_TONE_DETECTOR;
		}
		case 2: {
			return AUTO_SPEECH_RECOG_INDEPENDENT_DIGITS;
		}
		case 3: {
			return AUTO_SPEECH_RECOG_INDEPENDENT_SPEAKER_UI_V1;
		}
		default: {
			return null;
		}
		}
	}

}

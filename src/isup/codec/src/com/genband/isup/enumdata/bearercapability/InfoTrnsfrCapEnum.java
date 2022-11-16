package com.genband.isup.enumdata.bearercapability;
/**
 * This enum represents Information Transfer Capability.
 * @author vgoel
 */
public enum InfoTrnsfrCapEnum {
	
	/**
	 * 0-Speech
	 * 8-Unrestricted digital information
	 * 9-Restricted digital information
	 * 16-3.1 kHz audio
	 * 17-Unrestricted digital information with tones/announcements
	 * 24-Video
	 */
	SPEECH(0), UNRESTRICTED_DIGITAL_INFO(8), RESTRICTED_DIGITAL_INFO(9), AUDIO_3_1(16), UNRESTRICTED_DIGITAL_INFO_TONE(17), VIDEO(24);
	
	private int code;

	private InfoTrnsfrCapEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static InfoTrnsfrCapEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	SPEECH	; }
		case 8: { return 	UNRESTRICTED_DIGITAL_INFO	; }
		case 9: { return 	RESTRICTED_DIGITAL_INFO	; }
		case 16: { return 	AUDIO_3_1	; }
		case 17: { return 	UNRESTRICTED_DIGITAL_INFO_TONE	; }
		case 24: { return 	VIDEO	; }
		default: { return null; }
		}
	}
}

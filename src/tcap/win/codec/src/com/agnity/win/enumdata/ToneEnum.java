package com.agnity.win.enumdata;

public enum ToneEnum {

	/*
* Tone(octet 1)Bits HG FE DC BA   Value    Meaning 
	 * 				00 00 00 00 	0 		DialTone. A continuous 350 Hz tone added to a 440 Hz tone. 
	 * 				00 00 00 01 	1 		RingBack or AudibleAlerting. A 440 Hz tone added to a 480 Hz tone repeated in a 2s on 4s off pattern. 
	 * 				00 00 00 10 	2 		InterceptTone or MobileReorder. Alternating 440 Hz and 620 Hz tones, each on for 250 ms. 
	 * 				00 00 00 11 	3 		CongestionTone or ReorderTone. A 480 Hz tone added to a 620 Hz tone repeated in a 250 ms on, 250 ms off cycle. 
	 * 				00 00 01 00 	4 		BusyTone. A 480 Hz tone added to a 620 Hz tone repeated in a 500 ms on, 500 ms off cycle. 
	 * 				00 00 01 01 	5       ConfirmationTone. A 350 Hz tone added to a 440 Hz tone repeated 3 times in a 100 ms on, 100 ms off cycle. 
	 * 				00 00 01 10		6 		AnswerTone. Answer tone is not presently used in North American networks.
	 *  			00 00 01 11 	7		CallWaitingTone. A single 300 ms burst of 440 Hz tone. 
	 *  			00 00 10 00 	8		OffHookTone. Off-hook warning tone on. 
	 *  			00 01 00 01 	17 		RecallDialTone.Three bursts (0.1 s on, 0.1s off) then steady on of dial tone. [N-ISDN]
	 * 				00 01 00 10 	18 		BargeInTone. No information available.[N-ISDN] 
	 * 				00 11 11 11		63 		TonesOff. All tones off. 
	 * 				11 00 00 00 	192 	PipTone. Four bursts of (0.1s on, 0.1 s off) of 480 Hz tone, then off. [TIA/EIA-664] 
	 * 				11 00 00 01 	193		AbbreviatedIntercept. 4 seconds of Intercept- Tone. [CDMA] 
	 * 				11 00 00 10		194 	AbbreviatedCongestion. 4 seconds of CongestionTone. [CDMA] 
	 * 				11 00 00 11 	195 	WarningTone. A single 0.1 s burst of 480 Hz tone. [TIA/EIA-664] 
	 * 				11 00 01 00 	196 	DenialToneBurst. A single 2.0 s burst of 480 Hz tone added to a 620 Hz tone. [TIA/EIA-664] 
	 * 				11 00 01 01 	197 	DialToneBurst. A single 2.0 s burst of DialTone. [TIA/EIA-664] 
	 * 				11 11 10 10		250		IncomingAdditionalCallTone. No information available. [N-ISDN] 
	 * 				11 11 10 11 	251 	PriorityAdditionalCallTone. No information available. [N-ISDN] XX
	 * 				XX XX XX 				Other values are reserved.
	 */

	DIALTONE(0), RINGBACK_AUDALERT(1), INTERCEPTTONE_MOBILEREORDER(2), CONGTONE_REORDERTONE(
			3), BUSY_TONE(4), CONFIRM_TONE(5), ANSWER_TONE(6), CALLWAITING_TONE(
			7), OFFHOOK_TONE(8), RECALL_DIALTONE(17), BARGEINTONE(18), TONESOFF(
			63), PIPTONE(192), ABBINTERCEPT(193), ABBCONGESTION(194), WARNINGTONE(
			195), DENIALTONE_BURST(196), DIALTONE_BURST(197), INCOMINGADDCALLTONE(
			250), PRIORITYADDCALLTONE(251);

	private int code;

	private ToneEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static ToneEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return DIALTONE;
		}
		case 1: {
			return RINGBACK_AUDALERT;
		}
		case 2: {
			return INTERCEPTTONE_MOBILEREORDER;
		}
		case 3: {
			return CONGTONE_REORDERTONE;
		}
		case 4: {
			return BUSY_TONE;
		}
		case 5: {
			return CONFIRM_TONE;
		}
		case 6: {
			return ANSWER_TONE;
		}
		case 7: {
			return CALLWAITING_TONE;
		}
		case 8: {
			return OFFHOOK_TONE;
		}
		case 17: {
			return RECALL_DIALTONE;
		}
		case 18: {
			return BARGEINTONE;
		}
		case 63: {
			return TONESOFF;
		}
		case 192: {
			return PIPTONE;
		}
		case 193: {
			return ABBINTERCEPT;
		}
		case 194: {
			return ABBCONGESTION;
		}
		case 195: {
			return WARNINGTONE;
		}
		case 196: {
			return DENIALTONE_BURST;
		}
		case 197: {
			return DIALTONE_BURST;
		}
		case 250: {
			return INCOMINGADDCALLTONE;
		}
		case 251: {
			return PRIORITYADDCALLTONE;
		}

		default: {
			return null;
		}
		}
	}

}
